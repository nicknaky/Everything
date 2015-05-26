package com.mushroomrobot.everything.budget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.mushroomrobot.everything.R;
import com.mushroomrobot.everything.data.EverythingContract;
import com.mushroomrobot.everything.data.EverythingContract.Transactions;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Nick.
 */
public class BudgetHistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String budgetName;
    private static ArrayList categoryList;

    XYPlot historyPlot;
    TextView historySpend, numTrans;
    ListView historyList;

    Button addTransaction;

    SimpleCursorAdapter historyAdapter;

    private static final int EDIT_ID = 0;
    private static final int DELETE_ID = 1;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 1, R.string.menu_delete_trans);
        menu.add(0, EDIT_ID, 0, R.string.menu_edit_trans);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Bundle b = new Bundle();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        b.putLong("transId",info.id);

        FragmentManager fm = getFragmentManager();

        switch (item.getItemId()){
            case (DELETE_ID):
                DeleteTransDialog dialog = new DeleteTransDialog();
                dialog.setArguments(b);
                dialog.show(fm,null);
                break;
            case (EDIT_ID):
                b.putStringArrayList("categoryList", categoryList);
                b.putString("budgetName", budgetName);
                AddTransactionDialog transdialog = new AddTransactionDialog();
                transdialog.setArguments(b);
                transdialog.show(fm,null);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    public static class DeleteTransDialog extends DialogFragment {

        long transactionId;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);

            transactionId = getArguments().getLong("transId");
            Log.v("transId",String.valueOf(transactionId));

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(R.string.delete_trans_msg);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Uri mUri = Uri.parse(Transactions.CONTENT_URI + "/" + transactionId);
                    getActivity().getContentResolver().delete(mUri,null,null);
                    Log.v("Uri",String.valueOf(mUri));
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_budgethistory, container, false);

        Bundle bundle = getArguments();
        budgetName = bundle.getString("budgetName");

        categoryList = new ArrayList<String>();
        categoryList = bundle.getStringArrayList("categoryList");

        historyPlot = (XYPlot) rootView.findViewById(R.id.xy_history_plot);
        historyPlot.setVisibility(View.INVISIBLE);
        //This will query Transactions table for monthly Transaction amounts to populate chart
        getLoaderManager().initLoader(1, null, this);




        getActivity().getActionBar().setTitle(budgetName + " History");

        historySpend = (TextView) rootView.findViewById(R.id.history_spend_value);
        numTrans = (TextView) rootView.findViewById(R.id.history_num_trans);
        historyList = (ListView) rootView.findViewById(R.id.history_listview);

        addTransaction = (Button) rootView.findViewById(R.id.history_trans_button);
        addTransaction.setOnClickListener(mClickListener);

        TextView dateHeader = (TextView) rootView.findViewById(R.id.history_dateheader);
        TextView descHeader = (TextView) rootView.findViewById(R.id.history_descheader);
        TextView amountHeader = (TextView) rootView.findViewById(R.id.history_amountheader);

        dateHeader.setPaintFlags(dateHeader.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        descHeader.setPaintFlags(descHeader.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        amountHeader.setPaintFlags(amountHeader.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        fillData();

        registerForContextMenu(historyList);

        return rootView;
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

    private void fillData() {

        String[] tranFrom = {EverythingContract.Transactions.COLUMN_DATE, EverythingContract.Transactions.COLUMN_DESCRIPTION, EverythingContract.Transactions.COLUMN_AMOUNT};
        int[] tranTo = {R.id.bd_list_date, R.id.bd_list_desc, R.id.bd_list_amount};

        historyAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_budgetdetail, null, tranFrom, tranTo, 0);

        //This will query Transactions table and populate the listView
        getLoaderManager().initLoader(0, null, this);


        historyList.setAdapter(historyAdapter);
        historyAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                double amount = cursor.getDouble(cursor.getColumnIndex(EverythingContract.Transactions.COLUMN_AMOUNT)) / 100;
                String formattedAmount = NumberFormat.getCurrencyInstance().format(amount);

                long dateInMilli = cursor.getLong(cursor.getColumnIndex(EverythingContract.Transactions.COLUMN_DATE));
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

    private void plotChart(final ArrayList<String> labelsList, ArrayList<Double> monthsList) {

        historyPlot.setVisibility(View.VISIBLE);

        historyPlot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
        historyPlot.setPlotMargins(0, 0, 0, 0);
        historyPlot.setPlotPadding(0, 0, 0, 0);
        historyPlot.setGridPadding(0, 0, 0, 0);

        int colorText = getActivity().getResources().getColor(R.color.textview);
        historyPlot.getGraphWidget().getDomainLabelPaint().setColor(colorText);
        historyPlot.getGraphWidget().getRangeLabelPaint().setColor(colorText);

        historyPlot.getGraphWidget().getDomainOriginLabelPaint().setColor(colorText);
        historyPlot.getGraphWidget().getDomainOriginLinePaint().setColor(colorText);
        historyPlot.getGraphWidget().getRangeOriginLinePaint().setColor(colorText);

        // Domain
        Log.v("monthsList Size", String.valueOf(monthsList.size()));
        //historyPlot.setDomainBoundaries(0, monthsList.size(), BoundaryMode.FIXED);
        historyPlot.setDomainStep(XYStepMode.SUBDIVIDE, monthsList.size());
        historyPlot.getGraphWidget().setDomainValueFormat(new Format() {
            @Override
            public StringBuffer format(Object arg0, StringBuffer arg1, FieldPosition arg2) {
                int parsedInt = Math.round(Float.parseFloat(arg0.toString()));
                Log.d("test", parsedInt + " " + arg1 + " " + arg2);
                String labelString = labelsList.get(parsedInt);
                arg1.append(labelString);
                return arg1;
            }

            @Override
            public Object parseObject(String arg0, ParsePosition arg1) {
                return labelsList.indexOf(arg0);
            }
        });

        if (monthsList.size()<=6){
            historyPlot.setTicksPerDomainLabel(1);
        }
        else if (monthsList.size()>6 && monthsList.size() <=12){
            historyPlot.setTicksPerDomainLabel(2);
        }
        else if (monthsList.size()>12 && monthsList.size() <=24){
            historyPlot.setTicksPerDomainLabel(4);
        }
        else if (monthsList.size()>24 && monthsList.size() <=36){
            historyPlot.setTicksPerDomainLabel(6);
        }
        else if (monthsList.size() >36){
            historyPlot.setTicksPerDomainLabel(12);
        }

        double maxValue = Collections.max(monthsList);
        int listSize = monthsList.size();
        Log.v("passing plotCharT()","ok");
        Log.v("monthsList Size", String.valueOf(listSize));
        Log.v("max history value:", String.valueOf(maxValue));
        Log.v("monthsList Size", String.valueOf(monthsList.size()));
        double maxY = maxValue * 1.05;
        if (maxY == 0) {
            maxY = 100;
        }
        // Range
        historyPlot.setRangeBoundaries(0, maxY, BoundaryMode.FIXED);
        historyPlot.setRangeStepValue(5);
        historyPlot.setRangeValueFormat(new DecimalFormat("$#,###"));

        if (maxValue < 10){
            historyPlot.setRangeValueFormat(new DecimalFormat("$#,###.##"));
        }

        XYSeries series1 = new SimpleXYSeries(monthsList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");

        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                getResources().getColor(R.color.theme),
                Color.TRANSPARENT,
                Color.TRANSPARENT, null);

        historyPlot.addSeries(series1, series1Format);

        historyPlot.getLayoutManager().remove(historyPlot.getLegendWidget());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //This is to query the transactions listview
        if (id == 0) {
            //Remember, in SQLite string values need to be surrounded by quotes (single), otherwise it'll be read as column names
            //This probably explains the whole reason of having selectionArgs in examples even when there's only one WHERE value criteria
            String selection = "category = '" + budgetName + "'";
            CursorLoader cursorLoader = new CursorLoader(getActivity(), Transactions.CONTENT_URI_HISTORY, null, selection, null, null);
            return cursorLoader;
        }

        //This is to query the sum transactions amount each month to populate the plot
        if (id == 1) {
            //Remember, in SQLite string values need to be surrounded by quotes (single), otherwise it'll be read as column names
            //This probably explains the whole reason of having selectionArgs in examples even when there's only one WHERE value criteria
            String selection = "category = '" + budgetName + "'";
            CursorLoader cursorLoader = new CursorLoader(getActivity(), Transactions.CONTENT_URI_AMOUNT_BY_MONTH, null, selection, null, null);
            return cursorLoader;
        }
        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (loader.getId() == 0) {
            historyAdapter.swapCursor(data);

            numTrans.setText(String.valueOf(data.getCount()));

        }
        if (loader.getId() == 1) {

            int diffMonth = 0;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.US);
            SimpleDateFormat sdf2 = new SimpleDateFormat("MMM' '''yy", Locale.US);
            if (data.moveToFirst()) {
                try {

                    Calendar startCalendar = Calendar.getInstance();
                    String startDate = data.getString(data.getColumnIndex("year_month"));
                    Log.v("startDate", startDate);
                    Date start = sdf.parse(startDate);
                    startCalendar.setTime(start);

                    data.moveToLast();
                    Calendar endCalendar = Calendar.getInstance();
                    String endDate = data.getString(data.getColumnIndex("year_month"));
                    Log.v("endDate", endDate);
                    Date end = sdf.parse(endDate);
                    endCalendar.setTime(end);

                    int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                    Log.v("diffYear", String.valueOf(diffYear));
                    diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
                    Log.v("diffMonth", String.valueOf(diffMonth));
                    ArrayList<Double> monthsList = new ArrayList<>();

                    ArrayList<String> labelsList = new ArrayList<>();

                    String labelDate;
                    Calendar labelCalendar = Calendar.getInstance();
                    labelCalendar.setTime(start);

                    for (int i = 0; i < diffMonth + 1; i++) {
                        monthsList.add(i, 0.0);

                        labelDate = sdf2.format(labelCalendar.getTime());
                        labelsList.add(i, labelDate);
                        labelCalendar.add(Calendar.MONTH, 1);

                    }

                    Log.v("monthlist size", String.valueOf(monthsList.size()));
                    data.moveToFirst();
                    monthsList.set(0, data.getDouble(data.getColumnIndex("monthly_total")) / 100);

                    while (data.moveToNext()) {
                        Calendar nextCalendar = Calendar.getInstance();
                        String nextDate = data.getString(data.getColumnIndex("year_month"));
                        Date next = sdf.parse(nextDate);
                        nextCalendar.setTime(next);

                        Log.v("next date", sdf.format(next));

                        diffYear = nextCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                        Log.v("diffYear index", String.valueOf(diffYear));
                        diffMonth = diffYear * 12 + nextCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
                        Log.v("diffMonth index", String.valueOf(diffMonth));
                        monthsList.set(diffMonth, data.getDouble(data.getColumnIndex("monthly_total")) / 100);
                    }
                    double sum = 0;
                    for (double d : monthsList) {
                        sum += d;
                    }

                    //If we only have one month of data, then we need to append a previous month as a dummy holder to plot the chart
                    if (monthsList.size() == 1) {
                        monthsList.add(0, 0.0);
                        //We subtract two months here, one to backtrack the add() in the for loop, and another to get the previous month
                        labelCalendar.add(Calendar.MONTH, -2);
                        labelDate = sdf2.format(labelCalendar.getTime());
                        labelsList.add(0, labelDate);
                    }

                    String formattedSum = NumberFormat.getCurrencyInstance().format(sum);
                    historySpend.setText(formattedSum);
                    Log.v("monthlist max", String.valueOf(Collections.max(monthsList)));
                    historyPlot.clear();
                    plotChart(labelsList, monthsList);
                    historyPlot.redraw();

                } catch (ParseException e) {

                    Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_SHORT);
                    System.err.print(e);
                }
            }else {
                historySpend.setText("$0.00");
                historyPlot.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        historyAdapter.swapCursor(null);
    }
}
