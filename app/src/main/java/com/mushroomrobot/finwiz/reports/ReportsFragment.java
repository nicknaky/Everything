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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.data.EverythingContract;

import java.util.ArrayList;

/**
 * Created by Nick.
 */
public class ReportsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int LOADER_CATEGORY = 0;

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


        getLoaderManager().initLoader(LOADER_CATEGORY, null, this);

        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == LOADER_CATEGORY) {
            CursorLoader cursorLoader = new CursorLoader(getActivity(), EverythingContract.Category.CONTENT_URI, null, null, null, "spent desc");
            return cursorLoader;
        } else return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_CATEGORY) {

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

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
