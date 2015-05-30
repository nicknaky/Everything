package com.mushroomrobot.finwiz.budget;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.navigation.NavDrawerActivity;

/**
 * Created by Nick.
 */
public class DisplayBudgetActivity extends NavDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentOption = BUDGETS_OPTION;
        setContentView(R.layout.activity_budget);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        ActivityManager.TaskDescription td = new ActivityManager.TaskDescription(null, bm, getResources().getColor(R.color.white));
        setTaskDescription(td);
        bm.recycle();

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
