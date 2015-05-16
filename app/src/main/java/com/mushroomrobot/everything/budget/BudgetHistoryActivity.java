package com.mushroomrobot.everything.budget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mushroomrobot.everything.R;

/**
 * Created by Nick.
 */
public class BudgetHistoryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_budgethistory);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        BudgetHistoryFragment historyFragment = new BudgetHistoryFragment();
        historyFragment.setArguments(bundle);

        getFragmentManager().beginTransaction().add(R.id.container_budgethistory, historyFragment, "history").commit();
    }

    @Override
    public boolean onNavigateUp () {
        onBackPressed();
        return true;
    }
}
