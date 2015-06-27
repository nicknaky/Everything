package com.mushroomrobot.finwiz.reports;

import android.os.Bundle;

import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.navigation.NavDrawerActivity;

/**
 * Created by Nick.
 */
public class ReportsActivity extends NavDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentOption = REPORTS_OPTION;
        setContentView(R.layout.activity_reports);
        getSupportActionBar().setElevation(0);
        getSupportFragmentManager().beginTransaction().add(R.id.container_reports, new ReportsFragment()).commit();

    }
}
