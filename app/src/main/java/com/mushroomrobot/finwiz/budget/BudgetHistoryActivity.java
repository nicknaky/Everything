package com.mushroomrobot.finwiz.budget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mushroomrobot.finwiz.R;

/**
 * Created by Nick.
 */
public class BudgetHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_budgethistory);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        BudgetHistoryFragment historyFragment = new BudgetHistoryFragment();
        historyFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.container_budgethistory, historyFragment, "history").commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
/*
    @Override
    public boolean onNavigateUp () {
        Intent originIntent = getIntent();
        String budgetName = originIntent.getStringExtra("budgetName");
        ArrayList<String> categoryList = originIntent.getStringArrayListExtra("categoryList");
        long categoryId = originIntent.getLongExtra("categoryId", 0);

        Intent backIntent = new Intent(this, BudgetDetailsActivity.class);
        backIntent.putExtra("budgetName", budgetName);
        backIntent.putExtra("categoryList", categoryList);
        backIntent.putExtra("categoryId", categoryId);
        startActivity(backIntent);
        finish();

        return true;
    }
    */
}
