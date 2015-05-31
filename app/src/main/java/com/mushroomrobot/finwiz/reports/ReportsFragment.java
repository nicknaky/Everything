package com.mushroomrobot.finwiz.reports;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.mushroomrobot.finwiz.data.EverythingContract;
import com.mushroomrobot.finwiz.utils.MPCustomNumberFormatter;

import java.util.ArrayList;

/**
 * Created by Nick.
 */
public class ReportsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int LOADER_PIE = 0;
    private final int LOADER_BAR = 1;

    ArrayList<Entry> entries;
    ArrayList<String> labels;

    PieChart pieChart;
    HorizontalBarChart barChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_reports, container, false);

        pieChart = (PieChart) rootView.findViewById(R.id.reports_pichart);
        pieChart.setDescription("");

        barChart = (HorizontalBarChart) rootView.findViewById(R.id.reports_barchart);
        barChart.setDrawValueAboveBar(true);
        barChart.setDescription("");


        getLoaderManager().initLoader(LOADER_PIE, null, this);
        getLoaderManager().initLoader(LOADER_BAR, null, this);

        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == LOADER_PIE) {
            CursorLoader cursorLoader = new CursorLoader(getActivity(), EverythingContract.Category.CONTENT_URI, null, null, null, "spent desc");
            return cursorLoader;
        }
        else if (id == LOADER_BAR) {
            CursorLoader cursorLoader = new CursorLoader(getActivity(), EverythingContract.Category.CONTENT_URI_FREQUENCY, null, null, null, null);
            return cursorLoader;
        }
        else return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_PIE) {

            entries = new ArrayList<>();
            labels = new ArrayList<>();
            int i = 0;
            String name = "";
            while (data.moveToNext()) {

                if (i < 5) {
                    name = data.getString(data.getColumnIndex(EverythingContract.Category.COLUMN_NAME));
                    labels.add(name);
                } else {
                    name = "";
                    labels.add(name);
                }

                double value = data.getDouble(data.getColumnIndex(EverythingContract.Category.COLUMN_SPENT)) / 100;
                entries.add(new Entry((float) value, i));
                i++;

                String logIt = name + ": " + String.valueOf(value);
                Log.v("Log", logIt);
            }

            String description = "";
            PieDataSet dataSet = new PieDataSet(entries, description);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);


            int[] CUSTOM_COLORS = {
                    Color.rgb(178, 235, 242),
                    Color.rgb(128, 222, 234),
                    Color.rgb(77, 208, 225),
                    Color.rgb(38, 198, 218),
                    Color.rgb(0, 188, 212),
                    Color.rgb(0, 188, 212)
            };

        /*              Color.rgb(0, 140, 140),
                        Color.rgb(0, 160, 160),
                        Color.rgb(0, 180, 180),
                        Color.rgb(0, 200, 200),
                        Color.rgb(25, 206, 206),
                        Color.rgb(51, 211, 211),
                        Color.rgb(77, 216, 216),
                        Color.rgb(102,222,222),
                        Color.rgb(128,228,228)
                        */

            int[] THEME_COLORS = {
                    Color.rgb(0, 140, 140),

                    Color.rgb(0, 180, 180),

                    Color.rgb(25, 206, 206),

                    Color.rgb(77, 216, 216),

                    Color.rgb(128, 228, 228)
            };

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

        }
        else if (loader.getId() == LOADER_BAR){

            ArrayList<String> labels = new ArrayList<>();
            ArrayList<BarEntry> entries = new ArrayList<>();

            String name = "";
            int value = 0;
            int i = 0;
            data.moveToLast();
            //We need to go backwards due to library's Entry xIndex
            while (data.moveToPrevious()){

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



        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
