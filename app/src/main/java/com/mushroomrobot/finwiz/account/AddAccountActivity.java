package com.mushroomrobot.finwiz.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.data.EverythingProvider;

/**
 * Created by Nick.
 */
public class AddAccountActivity extends AppCompatActivity {

    private int                     mFrameLayout = R.id.addaccount_frame;
    private Fragment                mAddAccount;
    private EverythingProvider      mProvider;
    private RelativeLayout          mFrame;
    private Context                 mContext;


    android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addaccount);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        long temp = -1;
        long accountId = intent.getLongExtra("accountId",temp);
        String uri = intent.getStringExtra("uri");

        Bundle b = new Bundle();
        b.putLong("accountId",accountId);
        b.putString("uri",uri);

        mAddAccount = new AddAccountFragment();
        mAddAccount.setArguments(b);

        FragmentTransaction mFragMan = getSupportFragmentManager().beginTransaction();
        mFragMan.add(mFrameLayout, mAddAccount);
        mFragMan.commit();

        mProvider = new EverythingProvider();
        mFrame = (RelativeLayout) findViewById(R.id.addaccount_frame);
        mContext = this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(mContext,DisplayAccountActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext,MainActivity.class));
        finish();

    }*/
}
