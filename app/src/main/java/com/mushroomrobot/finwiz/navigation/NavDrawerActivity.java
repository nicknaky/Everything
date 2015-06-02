package com.mushroomrobot.finwiz.navigation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.account.DisplayAccountActivity;
import com.mushroomrobot.finwiz.budget.DisplayBudgetActivity;
import com.mushroomrobot.finwiz.data.Demo;
import com.mushroomrobot.finwiz.reports.ReportsActivity;

import java.util.ArrayList;

/**
 * Created by NLam on 5/28/2015.
 */
public class NavDrawerActivity extends Activity {

    static DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;
    private String[] navOptions = new String[]{"Budgets", "Reports", "Accounts", "Settings", "Demo"};

    private ArrayList<Item> items = new ArrayList<Item>();

    protected final int BUDGETS_OPTION = 0;
    protected final int REPORTS_OPTION = 1;
    protected final int ACCOUNTS_OPTION = 2;
    //protected final int DIVIDER = 3;
    protected final int SETTINGS_OPTION = 4;
    protected final int DEMO_OPTION = 5;

    String savedTitle;

    protected static int currentOption;

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

        items.add(new EntryItem("Budgets"));
        items.add(new EntryItem("Reports"));
        items.add(new EntryItem("Accounts"));
        items.add(new DividerItem());
        items.add(new EntryItem("Settings"));
        items.add(new EntryItem("Demo"));


        NavDrawerAdapter adapter = new NavDrawerAdapter(NavDrawerActivity.this, items);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent;

                if (!items.get(position).isDivider()) {

                    if (position == currentOption) {
                        mDrawerLayout.closeDrawer(mDrawerList);
                    } else {
                        switch (position) {
                            case BUDGETS_OPTION:
                                intent = new Intent(NavDrawerActivity.this, DisplayBudgetActivity.class);
                                mDrawerList.setItemChecked(position, true);
                                mDrawerLayout.closeDrawer(mDrawerList);

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Do something after 5s = 5000ms
                                        startActivity(intent);

                                    }
                                }, 250);
                                finish();
                                break;
                            case REPORTS_OPTION:
                                intent = new Intent(NavDrawerActivity.this, ReportsActivity.class);
                                mDrawerList.setItemChecked(position, true);
                                mDrawerLayout.closeDrawer(mDrawerList);
                                startActivity(intent);
                                finish();
                                break;

                            case ACCOUNTS_OPTION:
                                intent = new Intent(NavDrawerActivity.this, DisplayAccountActivity.class);
                                mDrawerList.setItemChecked(position, true);
                                mDrawerLayout.closeDrawer(mDrawerList);
                                startActivity(intent);
                                finish();
                                break;

                            case DEMO_OPTION:
                                if (currentOption == 0) {
                                    FragmentManager fm = getFragmentManager();
                                    DialogFragment dialog = new DemoDialog();
                                    dialog.show(fm, "Demo");
                                }
                            default:
                                break;
                        }
                    }

                }

            }
        });


/*
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, R.id.drawer_li_labels, navOptions));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;

                if (position == currentOption) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    switch (position) {
                        case 0:
                            intent = new Intent(NavDrawerActivity.this, DisplayBudgetActivity.class);
                            mDrawerList.setItemChecked(position, true);
                            mDrawerLayout.closeDrawer(mDrawerList);

                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(NavDrawerActivity.this, DisplayAccountActivity.class);
                            mDrawerList.setItemChecked(position, true);
                            mDrawerLayout.closeDrawer(mDrawerList);
                            startActivity(intent);
                            break;
                    }
                }
            }
        });
*/

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
    public static class DemoDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Initiate demo?");
            builder.setPositiveButton(R.string.close_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Demo demo = new Demo();
                    demo.demoSetUp(getActivity());
                    dialog.dismiss();
                    mDrawerLayout.closeDrawers();
                }
            });
            builder.setNegativeButton(R.string.close_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            return builder.create();

        }
    }
}
