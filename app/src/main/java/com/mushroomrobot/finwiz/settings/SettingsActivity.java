package com.mushroomrobot.finwiz.settings;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.budget.DisplayBudgetActivity;
import com.mushroomrobot.finwiz.data.DemoDialog;
import com.mushroomrobot.finwiz.data.ExportCsv;

/**
 * Created by NLam.
 */
public class SettingsActivity extends Activity {

    SharedPreferences sharedPreferences;

    Switch pinModeSwitch;
    int pin;

    RelativeLayout dataView, demoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        pinModeSwitch = (Switch) findViewById(R.id.pin_switch);
        checkPin();

        demoView = (RelativeLayout) findViewById(R.id.demo_view);
        demoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DialogFragment dialog = new DemoDialog();
                dialog.show(fm, "Demo");
            }
        });

        dataView = (RelativeLayout) findViewById(R.id.data_view);
        dataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Thread(new ExportCsv(SettingsActivity.this))).start();


            }
        });


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
        //super.onBackPressed();

        PinFragment pinFragment = (PinFragment)getFragmentManager().findFragmentByTag("PinFrag");
        if (pinFragment !=null && pinFragment.isVisible()){
            Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
            finish();
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, DisplayBudgetActivity.class);
            finish();
            startActivity(intent);
        }

    }
}
