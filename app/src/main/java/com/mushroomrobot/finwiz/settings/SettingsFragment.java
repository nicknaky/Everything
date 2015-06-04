package com.mushroomrobot.finwiz.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.mushroomrobot.finwiz.R;

/**
 * Created by NLam.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.shared_pref);
        }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        }
}
