package com.mushroomrobot.finwiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.mushroomrobot.finwiz.budget.DisplayBudgetActivity;
import com.mushroomrobot.finwiz.common.BaseActivity;

/**
 * Created by NLam.
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean pinMode = sharedPref.getBoolean(getResources().getString(R.string.pref_pinMode_key), false);

        if (!pinMode) {

            Intent intent = new Intent(SplashActivity.this, DisplayBudgetActivity.class);
            startActivity(intent);
            finish();
        }
        else if (pinMode) {
            finish();
        }


    }
}
