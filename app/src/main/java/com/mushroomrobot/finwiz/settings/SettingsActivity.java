package com.mushroomrobot.finwiz.settings;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.mushroomrobot.finwiz.R;

/**
 * Created by NLam.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        ActivityManager.TaskDescription td = new ActivityManager.TaskDescription(null, bm, getResources().getColor(R.color.white));
        setTaskDescription(td);
        bm.recycle();

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment(), "Settings").commit();
    }

    @Override
    public boolean onNavigateUp () {
        onBackPressed();
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
