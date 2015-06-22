package com.mushroomrobot.finwiz.navigation;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.account.DisplayAccountActivity;
import com.mushroomrobot.finwiz.budget.DisplayBudgetActivity;
import com.mushroomrobot.finwiz.common.BaseActivity;
import com.mushroomrobot.finwiz.reports.ReportsActivity;
import com.mushroomrobot.finwiz.settings.SettingsActivity;

import java.util.ArrayList;

/**
 * Created by NLam.
 */
//http://stackoverflow.com/questions/24524331/android-navigation-drawer-on-multiple-activities
public class NavDrawerActivity extends BaseActivity {

    static DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;
    private String[] navOptions = new String[]{"Budgets", "Reports", "Accounts", "Settings", "Demo"};

    private ArrayList<Item> items = new ArrayList<Item>();


    protected final int BUDGETS_OPTION = 0;
    protected final int REPORTS_OPTION = 1;
    protected final int ACCOUNTS_OPTION = 2;
    //protected final int DIVIDER = 3;
    protected final int SETTINGS_OPTION = 4;
    //protected final int DEMO_OPTION = 5;

    String savedTitle;

    protected static int currentOption = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else if (currentOption == 0) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(NavDrawerActivity.this, DisplayBudgetActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        DrawerLayout fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer, null);

        LinearLayout activityContent = (LinearLayout) fullLayout.findViewById(R.id.container_content);

        mDrawerLayout = (DrawerLayout) fullLayout.findViewById(R.id.drawer_layout);
        final ListView mDrawerList = (ListView) fullLayout.findViewById(R.id.nav_drawer);
        View headerView = (RelativeLayout) getLayoutInflater().inflate(R.layout.drawer_li_header, null);
        mDrawerList.addHeaderView(headerView, null, false);

        items.add(new EntryItem("Budgets", R.drawable.ic_wallet_grey));
        items.add(new EntryItem("Reports", R.drawable.ic_w_graph_grey));
        items.add(new EntryItem("Accounts", R.drawable.ic_bank_grey600_48dp));
        items.add(new DividerItem());
        items.add(new EntryItem("Settings", R.drawable.ic_settings_grey600_48dp));
        //items.add(new EntryItem("Demo"));


        NavDrawerAdapter adapter = new NavDrawerAdapter(NavDrawerActivity.this, items, currentOption);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent;
                int adjustedPosition = position - mDrawerList.getHeaderViewsCount();
                if (!items.get(adjustedPosition).isDivider()) {

                    if (adjustedPosition == currentOption) {
                        mDrawerLayout.closeDrawer(mDrawerList);
                    } else {
                        final Handler handler = new Handler();
                        switch (adjustedPosition) {
                            case BUDGETS_OPTION:
                                intent = new Intent(NavDrawerActivity.this, DisplayBudgetActivity.class);
                                mDrawerList.setItemChecked(adjustedPosition, true);
                                mDrawerLayout.closeDrawer(mDrawerList);

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Do something after 5s = 5000ms
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 250);

                                break;
                            case REPORTS_OPTION:
                                intent = new Intent(NavDrawerActivity.this, ReportsActivity.class);
                                mDrawerList.setItemChecked(adjustedPosition, true);
                                mDrawerLayout.closeDrawer(mDrawerList);

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Do something after 5s = 5000ms
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 250);

                                break;

                            case ACCOUNTS_OPTION:
                                intent = new Intent(NavDrawerActivity.this, DisplayAccountActivity.class);
                                mDrawerList.setItemChecked(adjustedPosition, true);
                                mDrawerLayout.closeDrawer(mDrawerList);

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Do something after 5s = 5000ms
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 250);

                                break;

                            case SETTINGS_OPTION:
                                Log.v("Settings option pressed", "Settings option pressed");

                                intent = new Intent(NavDrawerActivity.this, SettingsActivity.class);
                                mDrawerList.setItemChecked(adjustedPosition, true);
                                mDrawerLayout.closeDrawer(mDrawerList);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Do something after 5s = 5000ms
                                        startActivity(intent);

                                    }
                                }, 250);


                                //getFragmentManager().beginTransaction().replace(android.R.id.content,new SettingsFragment(), "Settings").commit();

                                break;

                            /*case DEMO_OPTION:
                                if (currentOption == 0) {
                                    FragmentManager fm = getFragmentManager();
                                    DialogFragment dialog = new DemoDialog();
                                    dialog.show(fm, "Demo");
                                } */
                            default:
                                break;
                        }
                    }

                }

            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                savedTitle = getActionBar().getTitle().toString();
                getActionBar().setTitle(R.string.app_name);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActionBar().setTitle(savedTitle);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        getLayoutInflater().inflate(layoutResID, activityContent, true);

        super.setContentView(fullLayout);
    }

}
