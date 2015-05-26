package com.mushroomrobot.finwiz.budget;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mushroomrobot.finwiz.R;

import java.util.ArrayList;

/**
 * Created by Nick.
 */
public class BudgetDetailsActivity extends Activity {

    private Fragment mBudgetDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgetdetails);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        long temp = -1;
        long categoryId = intent.getLongExtra("categoryId",temp);
        //At this stage we still don't need the actual parsed Uri just yet, since it's going to be passed again to the fragment.
        String uri = intent.getStringExtra("uri");

        ArrayList<String> categoryList = intent.getStringArrayListExtra("categoryList");

        Bundle b = new Bundle();
        b.putLong("categoryId",categoryId);
        b.putString("uri",uri);
        b.putStringArrayList("categoryList",categoryList);

        mBudgetDetails = new BudgetDetailsFragment();
        mBudgetDetails.setArguments(b);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
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
