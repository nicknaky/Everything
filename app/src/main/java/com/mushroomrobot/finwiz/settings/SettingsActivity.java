package com.mushroomrobot.finwiz.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.budget.DisplayBudgetActivity;

/**
 * Created by NLam.
 */
public class SettingsActivity extends Activity {

    Switch pinModeSwitch;
    SharedPreferences sharedPreferences;

    int pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_settings);
        pinModeSwitch = (Switch) findViewById(R.id.pin_switch);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        checkPin();

        pinModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){


                    android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getFragmentManager().beginTransaction().replace(R.id.container_settings, new PinFragment(),"PinFrag")
                                    .addToBackStack(null).commit();
                            checkPin();
                        }
                    }, 250);

                }
                if (!isChecked){
                    sharedPreferences.edit().putInt(getResources().getString(R.string.pref_pin_key), 0).commit();
                }

            }
        });

    }
    private void checkPin(){
        pin = sharedPreferences.getInt(getResources().getString(R.string.pref_pin_key), 0);

        if (pin == 0){
            pinModeSwitch.setChecked(false);
        } else if (pin != 0) {
            pinModeSwitch.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, DisplayBudgetActivity.class);
        startActivity(intent);
    }
}
