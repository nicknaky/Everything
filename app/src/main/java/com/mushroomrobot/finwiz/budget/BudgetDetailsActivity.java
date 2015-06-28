package com.mushroomrobot.finwiz.budget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mushroomrobot.finwiz.R;

import java.util.ArrayList;

/**
 * Created by Nick.
 */
public class BudgetDetailsActivity extends AppCompatActivity {

    private Fragment mBudgetDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgetdetails);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        long temp = -1;
        long categoryId = intent.getLongExtra("categoryId",temp);

        ArrayList<String> categoryList = intent.getStringArrayListExtra("categoryList");

        Bundle b = new Bundle();
        b.putLong("categoryId",categoryId);
        b.putStringArrayList("categoryList",categoryList);

        mBudgetDetails = new BudgetDetailsFragment();
        mBudgetDetails.setArguments(b);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container_budgetdetails, mBudgetDetails,"Budget Details").commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
