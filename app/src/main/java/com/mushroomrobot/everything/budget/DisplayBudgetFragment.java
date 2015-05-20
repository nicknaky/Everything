package com.mushroomrobot.everything.budget;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidplot.Plot;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.mushroomrobot.everything.R;
import com.mushroomrobot.everything.data.EverythingContract.Category;
import com.mushroomrobot.everything.data.EverythingContract.Transactions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Nick.
 */
public class DisplayBudgetFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_CATEGORIES = 1;
    private static final int LOADER_TRENDING_SPEND = 2;

    private static ArrayList<String> categoryList;

    ListView listView;
    Button addBudgetButton, addTransactionsButton;
    TextView noBudgetsTextView;
    ProgressBar budgetProgress;

    XYPlot transTrendingPlot;

    double totalBudgetSet = 0;
    double totalSpent = 0;

    private double budget_set, budget_spent, budget_left;

    BudgetsAdapter budgetsListAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.budgets_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addbudget:
                FragmentManager fm = getFragmentManager();
                AddBudgetDialog dialog = new AddBudgetDialog();
                dialog.show(fm, null);
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_budget_v3draft, container, false);

        Calendar myCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.US);
        String monthYear = sdf.format(myCalendar.getTime());
        getActivity().getActionBar().setTitle("Budgets - " + monthYear);

        transTrendingPlot = (XYPlot) rootView.findViewById(R.id.trans_trending_plot);

        listView = (ListView) rootView.findViewById(R.id.budget_listview);

        noBudgetsTextView = (TextView) rootView.findViewById(R.id.no_budgets_textview);
        addBudgetButton = (Button) rootView.findViewById(R.id.add_budget_button);
        addBudgetButton.setOnClickListener(mClickListener);

        noBudgetsTextView.setVisibility(TextView.INVISIBLE);
        addBudgetButton.setVisibility(Button.INVISIBLE);
        categoryList = new ArrayList<>();

        budgetProgress = (ProgressBar) rootView.findViewById(R.id.list_item_budget_progressbar);

        fillData();

        addTransactionsButton = (Button) rootView.findViewById(R.id.add_transactions_button);
        addTransactionsButton.setOnClickListener(mClickListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri uri = Uri.parse(Category.CONTENT_URI + "/" + id);
                long categoryId = id;

                Cursor cursor = getActivity().getContentResolver().query(Category.CONTENT_URI, new String[]{Category.COLUMN_NAME}, "category._id = " + String.valueOf(categoryId), null, null);
                cursor.moveToFirst();
                String categoryName = cursor.getString(cursor.getColumnIndex(Category.COLUMN_NAME));

                Intent intent = new Intent(getActivity(), BudgetDetailsActivity.class);

                //Remember, while putExtra allows passing in Uri's, we aren't able to retrieve them with getExtra.
                //Thus we'll need to pass in the Uri as a string, and then retrieve and parse it into a Uri later.
                intent.putExtra("uri", uri.toString());
                intent.putExtra("categoryId", categoryId);
                intent.putExtra("categoryName", categoryName);
                intent.putExtra("categoryList", categoryList);

                startActivity(intent);

            }
        });

        return rootView;
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == addBudgetButton) {
                FragmentManager fm = getFragmentManager();
                AddBudgetDialog dialog = new AddBudgetDialog();
                dialog.show(fm, null);
            } else if (v == addTransactionsButton) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("categoryList", categoryList);

                FragmentManager fm = getFragmentManager();
                AddTransactionDialog dialog = new AddTransactionDialog();
                dialog.setArguments(bundle);
                dialog.show(fm, null);
            }
        }
    };

    private void fillData() {
        String[] list_from = {Category.COLUMN_NAME,
                Category.COLUMN_SPENT,
                Category.COLUMN_BUDGET,
                Category.COLUMN_REMAINING};
        int[] list_to = {R.id.list_item_budget_name_textview,
                R.id.list_item_budget_spent,
                R.id.list_item_budget_orig,
                R.id.list_item_budget_remaining};

        budgetsListAdapter = new BudgetsAdapter(getActivity(), R.layout.list_item_budget, null, list_from, list_to, 0);
        getLoaderManager().initLoader(LOADER_CATEGORIES, null, this);

        listView.setAdapter(budgetsListAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == LOADER_CATEGORIES) {
            CursorLoader cursorLoader = new CursorLoader(getActivity(), Category.CONTENT_URI, null, null, null, null);
            return cursorLoader;
        }
        if (id == LOADER_TRENDING_SPEND) {
            CursorLoader cursorLoader = new CursorLoader(getActivity(), Transactions.CONTENT_URI_AMOUNT_BY_DAY, null, null, null, null);
            return cursorLoader;
        } else return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        noBudgetsTextView.setVisibility(TextView.INVISIBLE);
        addBudgetButton.setVisibility(Button.INVISIBLE);
        addTransactionsButton.setVisibility(TextView.VISIBLE);

        String getDataCursor = data.getColumnName(1);
        Log.v("getDataCursor", getDataCursor);

        if (loader.getId() == LOADER_CATEGORIES) {
            budgetsListAdapter.swapCursor(data);

            if (!budgetsListAdapter.getCursor().moveToFirst()) {
                noBudgetsTextView.setVisibility(TextView.VISIBLE);
                addBudgetButton.setVisibility(Button.VISIBLE);
                addTransactionsButton.setVisibility(Button.INVISIBLE);
            } else {

                //Clear old values otherwise they end up being added up with the new batch of values
                categoryList.clear();
                totalBudgetSet = 0;
                totalSpent = 0;
                while (data.moveToNext()) {
                    categoryList.add(data.getString(data.getColumnIndex(Category.COLUMN_NAME)));

                    totalBudgetSet += (data.getDouble(data.getColumnIndex(Category.COLUMN_BUDGET)) / 100);
                    totalSpent += (data.getDouble(data.getColumnIndex(Category.COLUMN_SPENT)) / 100);
                }

                getLoaderManager().initLoader(LOADER_TRENDING_SPEND, null, this);

            }
        }
        if (loader.getId() == LOADER_TRENDING_SPEND) {
            onTrendingSpendLoaded(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        budgetsListAdapter.swapCursor(null);
    }

    private void onTrendingSpendLoaded(Cursor cursor) {

        int daysInMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);

        ArrayList<Integer> numDays = new ArrayList<>();
        for (int i = 0; i < daysInMonth; i++) {
            numDays.add(i, i);
        }
        ArrayList<Double> transList = new ArrayList<>();
        for (int i = 0; i < numDays.size(); i++) {
            transList.add(i, 0.0);
        }
        while (cursor.moveToNext()) {
            long dateInMillis = cursor.getLong(cursor.getColumnIndex(Transactions.COLUMN_DATE));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateInMillis);

            SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.US);
            int day = Integer.valueOf(sdf.format(calendar.getTime()));

            double amount = cursor.getDouble(cursor.getColumnIndex("sum(amount)")) / 100;

            transList.set(day, amount);
        }
        ArrayList<Double> transTrending = new ArrayList<>();

        transTrending.add(0, 0.0);
        for (int i = 1; i < transList.size(); i++) {
            transTrending.add(i, 0.0);
            transTrending.set(i, transList.get(i) + transTrending.get(i - 1));
        }
        transTrendingPlot.clear();
        plotChart(numDays, transTrending);
        transTrendingPlot.redraw();
    }

    private void plotChart(ArrayList<Integer> daysList, ArrayList<Double> trendingList) {

        transTrendingPlot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
        transTrendingPlot.setPlotMargins(0, 0, 0, 0);
        transTrendingPlot.setPlotPadding(0, 0, 0, 0);
        transTrendingPlot.setGridPadding(0, 0, 0, 0);

        transTrendingPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
        transTrendingPlot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);

        transTrendingPlot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
        transTrendingPlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        transTrendingPlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);

        SizeMetrics sm = new SizeMetrics(0, SizeLayoutType.FILL, 0, SizeLayoutType.FILL);
        transTrendingPlot.getGraphWidget().setSize(sm);
        transTrendingPlot.getGraphWidget().position(0, XLayoutStyle.RELATIVE_TO_RIGHT, 0, YLayoutStyle.RELATIVE_TO_BOTTOM);

        // Domain
        transTrendingPlot.setDomainBoundaries(0, daysList.size(), BoundaryMode.FIXED);
        transTrendingPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 5);
        transTrendingPlot.setDomainValueFormat(new DecimalFormat("0"));

        double maxY = totalBudgetSet;
        if (totalSpent > totalBudgetSet) {
            maxY = totalSpent;
        }
        int roundUp = (((int)maxY + 499) / 500) * 500;

        // Range
        transTrendingPlot.setRangeBoundaries(0, roundUp, BoundaryMode.FIXED);
        //transTrendingPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 50.00);
        transTrendingPlot.setRangeStepValue(5);

        transTrendingPlot.setRangeValueFormat(new DecimalFormat("$#,###"));

        XYSeries series1 = new SimpleXYSeries(daysList, trendingList, "Series1");

        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                getResources().getColor(R.color.theme),
                Color.TRANSPARENT,
                Color.TRANSPARENT, null);

        transTrendingPlot.addSeries(series1, series1Format);
        transTrendingPlot.getLayoutManager().remove(transTrendingPlot.getLegendWidget());

    }

}
