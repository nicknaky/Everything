package com.mushroomrobot.everything.budget;

import android.app.Activity;
import android.os.Bundle;

import com.androidplot.Plot;
import com.androidplot.xy.XYPlot;
import com.mushroomrobot.everything.R;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Nick.
 */
public class DynamicXYPlotActivity extends Activity {

    //redraws a plot whenever an update is received
    private class MyPlotUpdater implements Observer {

        Plot plot;

        public MyPlotUpdater(Plot plot){
            this.plot = plot;
        }

        @Override
        public void update(Observable observable, Object data) {
            plot.redraw();
        }

    }

    private XYPlot dynamicPlot;
    private MyPlotUpdater plotUpdater;
    //SampleDynamicXYDataSource data;
    private Thread myThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

    }
}
