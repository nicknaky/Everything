package com.mushroomrobot.finwiz.reports;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.data.EverythingContract.Category;
import com.mushroomrobot.finwiz.data.EverythingContract.Transactions;
import com.mushroomrobot.finwiz.utils.MPCustomNumberFormatter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Nick.
 */
public class ReportsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int LOADER_PIE = 0;
    private final int LOADER_BAR = 1;
    private final int LOADER_EXPENSIVE = 2;

    ArrayList<Entry> entries;
    ArrayList<String> labels;

    TextView monthTextView;

    PieChart pieChart;
    HorizontalBarChart barChart;

    LinearLayout expensiveLinearLayout;
    CardView barChartCardView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_reports, container, false);

        monthTextView = (TextView) rootView.findViewById(R.id.reports_month_value);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        String monthYear = sdf.format(calendar.getTime());
        monthTextView.setText(monthYear);
        monthTextView.setOnClickListener(mClickListener);

        pieChart = (PieChart) rootView.findViewById(R.id.reports_pichart);
        pieChart.setDescription("");

        barChartCardView = (CardView) rootView.findViewById(R.id.bar_graph_cardview);
        barChart = (HorizontalBarChart) rootView.findViewById(R.id.reports_barchart);
        barChart.setDrawValueAboveBar(true);
        barChart.setDescription("");

        expensiveLinearLayout = (LinearLayout) rootView.findViewById(R.id.expensive_linearlayout);

        getLoaderManager().initLoader(LOADER_PIE, null, this);
        getLoaderManager().initLoader(LOADER_BAR, null, this);
        getLoaderManager().initLoader(LOADER_EXPENSIVE, null, this);

        return rootView;
    }

    View.OnClickListener mClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.reports_month_value:
                    //initiate date picker dialog with just month and year, hide day
                    FragmentManager fm = getFragmentManager();
                    MonthYearDialog monthYearDialog = new MonthYearDialog();
                    monthYearDialog.show(fm,"");

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == LOADER_PIE) {
            CursorLoader cursorLoader = new CursorLoader(getActivity(), Category.CONTENT_URI, null, null, null, "spent desc");
            return cursorLoader;
        } else if (id == LOADER_BAR) {
            CursorLoader cursorLoader = new CursorLoader(getActivity(), Category.CONTENT_URI_FREQUENCY, null, null, null, null);
            return cursorLoader;
        } else if (id == LOADER_EXPENSIVE) {
            CursorLoader cursorLoader = new CursorLoader(getActivity(), Transactions.CONTENT_URI_TOP_THREE, null, null, null, null);
            return cursorLoader;
        } else return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst()) {

            data.moveToPrevious();

            if (loader.getId() == LOADER_PIE) {

                entries = new ArrayList<>();
                labels = new ArrayList<>();
                int i = 0;
                String name = "";

                while (data.moveToNext()) {

                    if (i < 5) {
                        name = data.getString(data.getColumnIndex(Category.COLUMN_NAME));
                        labels.add(name);
                    } else {
                        name = "";
                        labels.add(name);
                    }

                    double value = data.getDouble(data.getColumnIndex(Category.COLUMN_SPENT)) / 100;
                    entries.add(new Entry((float) value, i));
                    i++;

                    String logIt = name + ": " + String.valueOf(value);
                    Log.v("Log", logIt);
                }

                String description = "";
                PieDataSet dataSet = new PieDataSet(entries, description);
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);

                ArrayList<Integer> colors = new ArrayList<>();
                for (int c : ColorTemplate.VORDIPLOM_COLORS) {
                    colors.add(c);
                }
                dataSet.setColors(colors);

                PieData pieData = new PieData(labels, dataSet);
                pieData.setValueFormatter(new PercentFormatter());
                pieData.setValueTextSize(12f);
                pieData.setValueTextColor(getActivity().getResources().getColor(R.color.textview));
                pieData.setValueTypeface(Typeface.SANS_SERIF);

                pieChart.setUsePercentValues(true);
                pieChart.setDrawSliceText(true);
                pieChart.setData(pieData);
                pieChart.animateX(1800);

                pieChart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

            } else if (loader.getId() == LOADER_BAR) {

                ArrayList<String> labels = new ArrayList<>();
                ArrayList<BarEntry> entries = new ArrayList<>();

                String name = "";
                int value = 0;
                int i = 0;
                data.moveToLast();
                data.moveToNext();
                //We need to go backwards due to library's Entry xIndex
                while (data.moveToPrevious()) {

                    name = data.getString(data.getColumnIndex("category"));
                    labels.add(name);

                    value = data.getInt(data.getColumnIndex("number_transactions"));
                    entries.add(new BarEntry(value, i));
                    i++;
                }
                BarDataSet dataSet = new BarDataSet(entries, "Data Set");
                dataSet.setBarSpacePercent(25f);


                BarData barData = new BarData(labels, dataSet);
                barData.setValueTextColor(getActivity().getResources().getColor(R.color.textview));
                barData.setValueTextSize(10f);
                barData.setValueTypeface(Typeface.SANS_SERIF);
                barData.setValueFormatter(new MPCustomNumberFormatter());

                switch (labels.size()) {

                    case 1:
                        barChartCardView.getLayoutParams().height = (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 110, getActivity().getResources().getDisplayMetrics());
                        barChartCardView.requestLayout();
                        break;
                    case 2:
                        barChartCardView.getLayoutParams().height = (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 140, getActivity().getResources().getDisplayMetrics());
                        barChartCardView.requestLayout();
                        break;
                    default:
                        break;
                }

                barChart.setDrawBarShadow(false);
                barChart.setTouchEnabled(false);

                barChart.getLegend().setEnabled(false);
                barChart.setDrawGridBackground(false);

                YAxis leftAxis = barChart.getAxisLeft();
                leftAxis.setEnabled(false);
                leftAxis.setDrawAxisLine(false);

                YAxis rightAxis = barChart.getAxisRight();
                rightAxis.setTextColor(getActivity().getResources().getColor(R.color.textview));
                rightAxis.setDrawGridLines(false);
                rightAxis.setEnabled(false);
                rightAxis.setDrawAxisLine(false);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setDrawGridLines(false);
                xAxis.setTextColor(getActivity().getResources().getColor(R.color.textview));
                xAxis.setDrawAxisLine(false);


                barChart.setDrawValueAboveBar(true);
                barChart.setData(barData);
                barChart.animateY(3000);

            } else if (loader.getId() == LOADER_EXPENSIVE) {

                data.moveToLast();
                data.moveToNext();
                while (data.moveToPrevious()) {


                    int i = 0;

                    View expensiveView = getActivity().getLayoutInflater().inflate(R.layout.list_item_expensive, expensiveLinearLayout, false);
                    expensiveLinearLayout.addView(expensiveView, i);

                    View view = expensiveLinearLayout.getChildAt(i);
                    i++;

                    TextView dateTextView = (TextView) view.findViewById(R.id.expensive_date_textview);
                    TextView amountTextView = (TextView) view.findViewById(R.id.expensive_amount_textview);
                    TextView categoryTextView = (TextView) view.findViewById(R.id.expensive_category_textview);
                    TextView descTextView = (TextView) view.findViewById(R.id.expensive_desc_textview);

                    long dateInMilis = data.getLong(data.getColumnIndex(Transactions.COLUMN_DATE));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(dateInMilis);
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE\ndd", Locale.US);
                    String date = sdf.format(calendar.getTime());

                    double amount = data.getDouble(data.getColumnIndex(Transactions.COLUMN_AMOUNT)) / 100;
                    String category = data.getString(data.getColumnIndex(Transactions.COLUMN_CATEGORY));
                    String description = data.getString(data.getColumnIndex(Transactions.COLUMN_DESCRIPTION));

                    dateTextView.setText(date);
                    amountTextView.setText(NumberFormat.getCurrencyInstance().format(amount));
                    categoryTextView.setText(category);
                    descTextView.setText(description);
                }

            }
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }
}
