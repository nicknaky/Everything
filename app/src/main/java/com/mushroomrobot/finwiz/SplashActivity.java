package com.mushroomrobot.finwiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.mushroomrobot.finwiz.budget.DisplayBudgetActivity;
import com.mushroomrobot.finwiz.common.BaseActivity;

/**
 * Created by NLam.
 */
public class SplashActivity extends BaseActivity {

    EditText pinEntry;
    TextView pinMessage;
    SharedPreferences sharedPref;
    int pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        pin = sharedPref.getInt(getResources().getString(R.string.pref_pin_key), 0);

        if (pin==0) {

            Intent intent = new Intent(SplashActivity.this, DisplayBudgetActivity.class);
            startActivity(intent);
            finish();
        }
        else if (pin!=0) {

            setContentView(R.layout.activity_splash);

            pinEntry = (EditText) findViewById(R.id.pin_edit_splash);
            pinMessage = (TextView) findViewById(R.id.pinSplashMessage);
            pinEntry.addTextChangedListener(new pinSplashWatcher());

        }


    }
    private class pinSplashWatcher implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (s.length() == 1){
                pinMessage.setVisibility(TextView.INVISIBLE);
            }

            if (s.length() == 4){
                int enteredPin = Integer.valueOf(s.toString());

                if (enteredPin == pin){
                    Intent intent = new Intent(SplashActivity.this, DisplayBudgetActivity.class);
                    startActivity(intent);
                    finish();
                } else if (enteredPin != pin){
                    pinEntry.setText("");
                    pinMessage.setVisibility(TextView.VISIBLE);
                }
            }

        }
    }
}
