package com.mushroomrobot.finwiz.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.budget.DisplayBudgetActivity;
import com.mushroomrobot.finwiz.data.ClearDataDialog;
import com.mushroomrobot.finwiz.data.DemoDialog;
import com.mushroomrobot.finwiz.data.ExportCsv;

/**
 * Created by NLam.
 */
public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    Switch pinModeSwitch;
    int pin;

    TextView exportView, clearView, contactView;

    RelativeLayout demoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        pinModeSwitch = (Switch) findViewById(R.id.pin_switch);
        checkPin();

        demoView = (RelativeLayout) findViewById(R.id.demo_view);
        demoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DialogFragment dialog = new DemoDialog();
                dialog.show(fm, "Demo");
            }
        });

        exportView = (TextView) findViewById(R.id.exportdata);
        exportView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Thread(new ExportCsv(SettingsActivity.this))).start();
            }
        });

        clearView = (TextView) findViewById(R.id.clear_data);
        clearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DialogFragment dialog = new ClearDataDialog();
                dialog.show(fm, "Clear Data");
            }
        });

        pinModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {


                    android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getFragmentManager().beginTransaction().replace(R.id.container_settings, new PinFragment(), "PinFrag")
                                    .addToBackStack(null).commit();
                            checkPin();
                        }
                    }, 250);

                }
                if (!isChecked) {
                    sharedPreferences.edit().putInt(getResources().getString(R.string.pref_pin_key), 0).commit();
                }
            }
        });

        contactView = (TextView) findViewById(R.id.contactus);
        contactView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"nick@mushroomrobot.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FinWiz Support");
                try {
                    startActivity(emailIntent);
                } catch (android.content.ActivityNotFoundException e){
                    Toast.makeText(SettingsActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
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
