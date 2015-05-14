package com.mushroomrobot.everything.budget;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mushroomrobot.everything.R;

/**
 * Created by Nick.
 */
public class DisplayBudgetActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        Intent intent = getIntent();
        //deleteUri is Category.CONTENT_URI/CATEGORY_ID
        String deleteUri = intent.getStringExtra("deleteUri");
        String deleteCategory = intent.getStringExtra("deleteCategory");

        try {
            if (deleteUri != null && deleteCategory != null) {
                Uri uri = Uri.parse(deleteUri);
                getContentResolver().delete(uri, deleteCategory, null);
            }
        }catch (NullPointerException e){

        }
        getFragmentManager().beginTransaction().add(R.id.container_budget,new DisplayBudgetFragment(),"Budget").commit();
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
