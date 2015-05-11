package com.mushroomrobot.everything.budget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.androidplot.Plot;
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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by Nick.
 */
public class BudgetDetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static long categoryId;
    Uri mUri;
    private static ArrayList categoryList;
    private static String budgetName;
    private static double budgetAmount, budgetSpent, budgetRemaining;

    Button addTransaction;
    ListView listView;
    TextView budgetAmountTextView, budgetSpentTextView, budgetRemainingTextView, numRowsTextView;
    ProgressBar verticalProgress;
    SimpleCursorAdapter transactionsAdapter;

    private static final int EDIT_ID = 0;
    private static final int DELETE_ID = 1;


    int rawBudgetAmount;


    private XYPlot plot;

    int budgetPercentRemaining;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        categoryList = new ArrayList<String>();
        categoryList = getArguments().getStringArrayList("categoryList");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.budgetdetails, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        switch (item.getItemId()) {

            case (R.id.edit_budget):
                Bundle bundle = new Bundle();
                bundle.putString("budgetName", budgetName);
                bundle.putString("budgetAmount", String.valueOf(rawBudgetAmount).replace("[^0-9]",""));
                bundle.putInt("editMode", 1);
                AddBudgetDialog editBDialog = new AddBudgetDialog();
                editBDialog.setArguments(bundle);
                editBDialog.show(fm, null);
                break;
            case (R.id.delete_budget):

                DeleteBudgetDialog deleteBDialog = new DeleteBudgetDialog();
                deleteBDialog.show(fm, null);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 1, R.string.menu_delete_trans);
        menu.add(0, EDIT_ID, 0, R.string.menu_edit_trans);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case (DELETE_ID):
                break;
            case (EDIT_ID):
                break;
            default:
                break;
        }


        return super.onContextItemSelected(item);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_budgetdetails, container, false);

        categoryId = getArguments().getLong("categoryId");

        plot = (XYPlot) rootView.findViewById(R.id.xy_trans_plot);

        drawPlot();
        //Loader to query for category trasnactions amount by day
        //getLoaderManager().initLoader(2,null,this);



        //Retrieves CATEGORY_ID uri
        mUri = Uri.parse(getArguments().getString("uri"));

        verticalProgress = (ProgressBar) rootView.findViewById(R.id.bd_vertical_progressbar);

        numRowsTextView = (TextView) rootView.findViewById(R.id.bd_num_trans);

        budgetAmountTextView = (TextView) rootView.findViewById(R.id.bd_monthly_budget_value);
        budgetSpentTextView = (TextView) rootView.findViewById(R.id.bd_monthly_spend_value);
        budgetRemainingTextView = (TextView) rootView.findViewById(R.id.bd_monthly_remaining_value);


        listView = (ListView) rootView.findViewById(R.id.bd_listview);
        addTransaction = (Button) rootView.findViewById(R.id.bd_trans_button);
        addTransaction.setOnClickListener(mClickListener);

        TextView dateHeader = (TextView) rootView.findViewById(R.id.bd_dateheader);
        TextView descHeader = (TextView) rootView.findViewById(R.id.bd_descheader);
        TextView amountHeader = (TextView) rootView.findViewById(R.id.bd_amountheader);

        dateHeader.setPaintFlags(dateHeader.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        descHeader.setPaintFlags(descHeader.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        amountHeader.setPaintFlags(amountHeader.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        fillData();

        registerForContextMenu(listView);

        return rootView;
    }

    private void plotChart(ArrayList<Integer> daysList, ArrayList<Double> transList){

        //plot.setBackgroundColor(Color.WHITE);
        //plot.getGraphWidget().getBackgroundPaint().setColor(Color.parseColor("#fff3f3f3"));
        //plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.parseColor("#fff3f3f3"));

        plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
        plot.setPlotMargins(0, 0, 0, 0);
        plot.setPlotPadding(0, 0, 0, 0);
        plot.setGridPadding(0, 0, 0, 0);


        plot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);

        plot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
        plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);

        // Domain
        plot.setDomainBoundaries(0,31,BoundaryMode.FIXED);
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 5);
        plot.setDomainValueFormat(new DecimalFormat("0"));
        //plot.setDomainStepValue(5);

        double maxValue = Collections.max(transList);
        Log.v("max value:", String.valueOf(maxValue));

        double maxY = maxValue * 1.05;
        if (maxY==0){
            maxY = budgetAmount;
        }
        // Range
        plot.setRangeBoundaries(0, maxY, BoundaryMode.FIXED);
        plot.setRangeStepValue(5);
        //mySimpleXYPlot.setRangeStep(XYStepMode.SUBDIVIDE, values.length);
        plot.setRangeValueFormat(new DecimalFormat("$#,###"));


        XYSeries series1 = new SimpleXYSeries(daysList, transList, "Series1");

        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                getResources().getColor(R.color.theme),
                Color.TRANSPARENT,
                Color.TRANSPARENT, null);

        plot.addSeries(series1, series1Format);

        plot.getLayoutManager().remove(plot.getLegendWidget());

    }

    private void fillData() {

        String[] tranFrom = {Transactions.COLUMN_DATE, Transactions.COLUMN_DESCRIPTION, Transactions.COLUMN_AMOUNT};
        int[] tranTo = {R.id.bd_list_date, R.id.bd_list_desc, R.id.bd_list_amount};
        transactionsAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_budgetdetail, null, tranFrom, tranTo, 0);

        //Loader to query for category transactions
        getLoaderManager().initLoader(0, null, this);

        //Loader to query for category overview metrics such as budget, spend, remaining
        getLoaderManager().initLoader(1, null, this);



        listView.setAdapter(transactionsAdapter);

        transactionsAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                double amount = cursor.getDouble(cursor.getColumnIndex(Transactions.COLUMN_AMOUNT)) / 100;
                String formattedAmount = NumberFormat.getCurrencyInstance().format(amount);

                long dateInMilli = cursor.getLong(cursor.getColumnIndex(Transactions.COLUMN_DATE));
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dateInMilli);

                String formattedDate = sdf.format(calendar.getTime());

                switch (view.getId()) {
                    case R.id.bd_list_amount:
                        ((TextView) view).setText(formattedAmount);
                        return true;
                    case R.id.bd_list_date:
                        ((TextView) view).setText(formattedDate);
                        return true;
                    default:
                        break;
                }

                return false;
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        if (id == 0) {
            String selection = "transactions.category = ?";

            String[] selectionArgs = {String.valueOf(categoryId)};
            //mUri   Retrieves CATEGORY_ID uri
            CursorLoader cursorLoader = new CursorLoader(getActivity(), Transactions.CONTENT_URI, null, selection, selectionArgs, null);
            return cursorLoader;
        }

        // Querying category overview metrics such as budget, spend, remaining
        if (id == 1) {

            String selection = "category._id = " + String.valueOf(categoryId);
            CursorLoader cursorLoader = new CursorLoader(getActivity(), Uri.parse(Category.CONTENT_URI + "/" + categoryId), null, selection, null, null);
            return cursorLoader;

        }
        // Querying category transactions amount by day
        if (id==2){
            String selection = "transactions.category = ?";

            String[] selectionArgs = {String.valueOf(categoryId)};
            //mUri   Retrieves CATEGORY_ID uri
            CursorLoader cursorLoader = new CursorLoader(getActivity(), Transactions.CONTENT_URI_AMOUNT_BY_DAY, null, selection, selectionArgs, null);
            return cursorLoader;
        }
        else {
            return null;
        }
    }


    private void drawPlot(){

        String selection = "transactions.category = ?";
        String[] selectionArgs = {String.valueOf(categoryId)};
        Cursor cursor = getActivity().getContentResolver().query(Transactions.CONTENT_URI_AMOUNT_BY_DAY,null,selection,selectionArgs,null);

        Calendar c = Calendar.getInstance();
        int daysInCurrentMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.v("Days in current month", String.valueOf(daysInCurrentMonth));

        ArrayList<Integer> daysList = new ArrayList<>();
        for (int i=0; i<daysInCurrentMonth; i++){
            daysList.add(i,i+1);
        }
        Log.v("size of days list", String.valueOf(daysList.size()));
        Log.v("day at index 0", String.valueOf(daysList.get(0)));
        Log.v("day at last index", String.valueOf(daysList.get(daysList.size()-1)));

        // Initialize the transAmount list with size equal to number of days in month
        ArrayList<Double> transList = new ArrayList<>();
        for (int i=0; i<daysList.size(); i++){
            transList.add(i,0.0);
        }
        Log.v("size of trans list", String.valueOf(transList.size()));
        Log.v("first index should be 0", String.valueOf(transList.get(0)));
        Log.v("last index should be 0", String.valueOf(transList.get(transList.size() - 1)));

        while (cursor.moveToNext()){

            //Retrieve date in milliseconds and convert to day of month
            long dateInMilli = cursor.getLong(cursor.getColumnIndex(Transactions.COLUMN_DATE));
            SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.US);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateInMilli);

            //The actual day will serve as the index of the trans list to insert the actual maount
            int day = Integer.valueOf(sdf.format(calendar.getTime()));

            double amount = cursor.getDouble(cursor.getColumnIndex("sum(amount)")) / 100;

            String dayAndAmount = "Day: " + day + ". Total Amount: " + amount;
            Log.v("Day and Amount", dayAndAmount);

            transList.set(day-1,amount);
        }

        int i =1;
        for (double x : transList){
            Log.v("transList contents: ", "Day: " + i + " Amount: " + x);
            i++;
        }


        //Append a "day 0" slot to show a cleaner chart format
        daysList.add(0,0);
        transList.add(0,0.0);

        plotChart(daysList, transList);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (loader.getId() == 0) {
            transactionsAdapter.swapCursor(data);

            if (data.moveToFirst()) {

                String countRows = String.valueOf(data.getCount());
                numRowsTextView.setText(countRows);
            }
        }
        if (loader.getId() == 1) {
            if (data.moveToFirst()) {

                budgetName = data.getString(data.getColumnIndex(Category.COLUMN_NAME));
                getActivity().getActionBar().setTitle(budgetName + " Budget");

                rawBudgetAmount = data.getInt(data.getColumnIndex(Category.COLUMN_BUDGET));
                budgetAmount = data.getDouble(data.getColumnIndex(Category.COLUMN_BUDGET))/ 100;
                String formattedAmount = NumberFormat.getCurrencyInstance().format(budgetAmount);
                budgetAmountTextView.setText(formattedAmount);

                budgetSpent = data.getDouble(data.getColumnIndex(Category.COLUMN_SPENT)) / 100;
                String formattedSpent = NumberFormat.getCurrencyInstance().format(budgetSpent);
                budgetSpentTextView.setText(formattedSpent);

                budgetRemaining = data.getDouble(data.getColumnIndex(Category.COLUMN_REMAINING)) / 100;
                String formattedRemaining = NumberFormat.getCurrencyInstance().format(budgetRemaining);
                budgetRemainingTextView.setText(formattedRemaining);

                if (budgetRemaining < 0){
                    budgetRemainingTextView.setTextColor(getResources().getColor(R.color.red_money));
                }

                //Need to take the inverse of COLUMN_PERCENT since it calculates percent of spend / original budget amount
                budgetPercentRemaining = 100 - (data.getInt(data.getColumnIndex(Category.COLUMN_PERCENT)));

                verticalProgress.setProgress(budgetPercentRemaining);
            }
        }
        plot.clear();
        drawPlot();
        plot.redraw();

        if (loader.getId() == 2){

            Calendar c = Calendar.getInstance();
            int daysInCurrentMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            Log.v("Days in current month", String.valueOf(daysInCurrentMonth));

            ArrayList<Integer> daysList = new ArrayList<>();
            for (int i=0; i<daysInCurrentMonth; i++){
                daysList.add(i,i+1);
            }
            Log.v("size of days list", String.valueOf(daysList.size()));
            Log.v("day at index 0", String.valueOf(daysList.get(0)));
            Log.v("day at last index", String.valueOf(daysList.get(daysList.size()-1)));

            // Initialize the transAmount list with size equal to number of days in month
            ArrayList<Double> transList = new ArrayList<>();
            for (int i=0; i<daysList.size(); i++){
                transList.add(i,0.0);
            }
            Log.v("size of trans list", String.valueOf(transList.size()));
            Log.v("first index should be 0", String.valueOf(transList.get(0)));
            Log.v("last index should be 0", String.valueOf(transList.get(transList.size() - 1)));



            while (data.moveToNext()){

                //Retrieve date in milliseconds and convert to day of month
                long dateInMilli = data.getLong(data.getColumnIndex(Transactions.COLUMN_DATE));
                SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.US);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dateInMilli);

                //The actual day will serve as the index of the trans list to insert the actual maount
                int day = Integer.valueOf(sdf.format(calendar.getTime()));

                double amount = data.getDouble(data.getColumnIndex("sum(amount)")) / 100;

                String dayAndAmount = "Day: " + day + ". Total Amount: " + amount;
                Log.v("Day and Amount", dayAndAmount);

                transList.set(day-1,amount);
            }

            int i =1;
            for (double x : transList){
                Log.v("transList contents: ", "Day: " + i + " Amount: " + x);
                i++;
            }


            //Append a "day 0" slot to show a cleaner chart format
            daysList.add(0,0);
            transList.add(0,0.0);

            plotChart(daysList, transList);


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        transactionsAdapter.swapCursor(null);
    }


    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == addTransaction) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("categoryList", categoryList);
                bundle.putString("budgetName", budgetName);

                FragmentManager fm = getFragmentManager();
                AddTransactionDialog dialog = new AddTransactionDialog();
                dialog.setArguments(bundle);
                dialog.show(fm, null);
            }
        }
    };

    public static class DeleteBudgetDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(R.string.delete_budget_msg);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(getActivity(), DisplayBudgetActivity.class);
                    intent.putExtra("deleteUri", Category.CONTENT_URI + "/" + categoryId);
                    intent.putExtra("deleteCategory", "\"" + budgetName + "\"");
                    startActivity(intent);

                    getActivity().finish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            return builder.create();
        }
    }

    public static class DeleteTransDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(R.string.delete_trans_msg);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            return builder.create();
        }
    }
}
