package com.mushroomrobot.finwiz.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.mushroomrobot.finwiz.R;

/**
 * Created by NLam.
 */
public class SettingsActivityDraft extends Activity {

    Switch pinModeSwitch;
    SharedPreferences sharedPreferences;

    int pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean pinMode = sharedPreferences.getBoolean(getResources().getString(R.string.pref_pinMode_key), false);

        pinModeSwitch = (Switch) findViewById(R.id.pin_switch);
        pinModeSwitch.setChecked(pinMode);

        pinModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(getResources().getString(R.string.pref_pinMode_key),isChecked).apply();

                if (isChecked == true){



                }
            }
        });

    }
}
