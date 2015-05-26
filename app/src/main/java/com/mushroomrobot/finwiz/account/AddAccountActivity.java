package com.mushroomrobot.finwiz.account;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.data.EverythingProvider;

/**
 * Created by Nick.
 */
public class AddAccountActivity extends Activity {

    private int                     mFrameLayout = R.id.addaccount_frame;
//    private ActionBar               mActionBar;
    private Fragment                mAddAccount;
    private EverythingProvider      mProvider;
    private FrameLayout             mFrame;
    private Context                 mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addaccount);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        long temp = -1;
        long accountId = intent.getLongExtra("accountId",temp);
        String uri = intent.getStringExtra("uri");

        Bundle b = new Bundle();
        b.putLong("accountId",accountId);
        b.putString("uri",uri);

        mAddAccount = new AddAccountFragment();
        mAddAccount.setArguments(b);

        FragmentTransaction mFragMan = getFragmentManager().beginTransaction();
        mFragMan.add(mFrameLayout, mAddAccount);
        mFragMan.commit();

        mProvider = new EverythingProvider();
        mFrame = (FrameLayout) findViewById(R.id.addaccount_frame);
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
