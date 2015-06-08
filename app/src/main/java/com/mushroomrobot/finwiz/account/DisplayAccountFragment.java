package com.mushroomrobot.finwiz.account;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.data.EverythingContract.Accounts;

import java.text.NumberFormat;



public class DisplayAccountFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {


    private double sum_assets,sum_debts,networth;

    private final int LOADER_OVERVIEW = 0;
    private final int LOADER_ACCOUNTS = 1;

    private final int UPDATE_ID = 1;
    private final int DELETE_ID = 2;

    TextView totalAssetsTextView, totalDebtsTextView, networthTextView, noAccountsTextView;
    ListView listView;
    Button addAccount;

    private AccountsAdapter accountsListAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.accounts_list,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.action_addaccount:
                startActivity(new Intent(getActivity(), AddAccountActivity.class));
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        totalAssetsTextView = (TextView) rootView.findViewById(R.id.total_assets_textview);
        totalDebtsTextView = (TextView) rootView.findViewById(R.id.total_debts_textview);
        networthTextView = (TextView) rootView.findViewById(R.id.networth_textview);

        noAccountsTextView = (TextView) rootView.findViewById(R.id.no_accounts_textview);
        noAccountsTextView.setVisibility(TextView.VISIBLE);
        addAccount = (Button) rootView.findViewById(R.id.add_account_button);
        addAccount.setVisibility(Button.VISIBLE);

        listView = (ListView) rootView.findViewById(R.id.list);

        fillData();

        registerForContextMenu(listView);

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 2, R.string.menu_delete_acc);
        menu.add(0, UPDATE_ID,1, R.string.menu_update_acc);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();
                Uri uri = Uri.parse(Accounts.CONTENT_URI + "/" + info.id);
                getActivity().getContentResolver().delete(uri, null, null);
                fillData();
                return true;
            case UPDATE_ID:
                AdapterView.AdapterContextMenuInfo info2 = (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();


                Uri uri2 = Uri.parse(Accounts.CONTENT_URI + "/" + info2.id);
                long accountId = info2.id;

                Intent intent = new Intent(getActivity(),AddAccountActivity.class);
                intent.putExtra("uri",uri2.toString());
                intent.putExtra("accountId",accountId);
                startActivity(intent);

                return true;
        }
        return super.onContextItemSelected(item);
    }


    private void fillData(){
        String[] list_from = {Accounts.COLUMN_NAME,Accounts.COLUMN_BALANCE, Accounts.COLUMN_LAST_UPDATE};
        int[] list_to = {R.id.list_item_account_name_textview,R.id.list_item_account_balance_textview,R.id.list_item_account_date};


        getLoaderManager().initLoader(LOADER_OVERVIEW,null,this);
        getLoaderManager().initLoader(LOADER_ACCOUNTS,null,this);


        accountsListAdapter = new AccountsAdapter(getActivity(),R.layout.list_item_account,null,list_from, list_to,0);

        //Use setViewBinder to allow additional implementation of data beyond the scope of the simpleCursorAdapter.
        accountsListAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                final int typeIndex = cursor.getColumnIndex(Accounts.COLUMN_TYPE);
                final String type = cursor.getString(typeIndex);

                final int balanceIndex = cursor.getColumnIndex(Accounts.COLUMN_BALANCE);
                final double balance = cursor.getDouble(balanceIndex) / 100;
                final String formattedBalance = NumberFormat.getCurrencyInstance().format(balance);

                if (view.getId() == R.id.list_item_account_balance_textview) {
                    ((TextView) view).setText(String.valueOf(formattedBalance));

                    switch (type) {
                        case "Asset":
                            ((TextView) view).setTextColor(getResources().getColor(R.color.green_money));
                            break;
                        case "Debt":
                            ((TextView) view).setTextColor(getResources().getColor(R.color.red_money));
                        default:
                            break;
                    }
                    return true;
                }
                return false;
            }
        });
        listView.setAdapter(accountsListAdapter);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startActivity(new Intent(getActivity(), AddAccountActivity.class));

            FragmentTransaction mFragMan = getFragmentManager().beginTransaction();
            mFragMan.remove(new DisplayAccountFragment());

        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id==LOADER_ACCOUNTS){
            String[] projection = { Accounts._ID, Accounts.COLUMN_NAME, Accounts.COLUMN_BALANCE, Accounts.COLUMN_TYPE, Accounts.COLUMN_LAST_UPDATE};
            String sortOrder = "name collate nocase asc";
            CursorLoader cursorLoader = new CursorLoader(getActivity(), Accounts.CONTENT_URI,projection,null,null,sortOrder);
            return cursorLoader;
        }
        else if (id==LOADER_OVERVIEW){
            String sum_assets = "(Select sum(balance) from accounts where type like 'asset') sum_assets";
            String sum_debts = "(Select sum(balance) from accounts where type like 'debt') sum_debts";
            String[] projection = {Accounts._ID,sum_assets,sum_debts};
            CursorLoader cursorLoader = new CursorLoader(getActivity(), Accounts.CONTENT_URI,projection,null,null,null);
            return cursorLoader;
        }
        else return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (loader.getId()==LOADER_ACCOUNTS) {
            if (data.moveToFirst()){
                noAccountsTextView.setVisibility(TextView.INVISIBLE);
                addAccount.setVisibility(Button.INVISIBLE);
                accountsListAdapter.swapCursor(data);
            }
            else {
                noAccountsTextView.setVisibility(TextView.VISIBLE);
                addAccount.setVisibility(Button.VISIBLE);
                addAccount.setOnClickListener(mClickListener);
            }

        }
        else if (loader.getId()==LOADER_OVERVIEW){

            if (data.moveToFirst()){
                sum_assets = data.getDouble(1)/100;
                sum_debts = data.getDouble(2)/100;
                networth = sum_assets - sum_debts;
                if (networth < 0){
                    networthTextView.setTextColor(getActivity().getResources().getColor(R.color.red_money));
                } else if (networth > 0) {
                    networthTextView.setTextColor(getActivity().getResources().getColor(R.color.green_money));
                } else if (networth == 0) {
                    networthTextView.setTextColor(getActivity().getResources().getColor(R.color.textview));
                }
                String formattedSumAssets = NumberFormat.getCurrencyInstance().format(sum_assets);
                String formattedSumDebts = NumberFormat.getCurrencyInstance().format(sum_debts);
                String formattedNetworth = NumberFormat.getCurrencyInstance().format(networth);

                totalAssetsTextView.setText("+" + formattedSumAssets);
                totalDebtsTextView.setText("-" + formattedSumDebts);
                networthTextView.setText(formattedNetworth);
            } else {
                totalAssetsTextView.setText("+$0.00");
                totalDebtsTextView.setText("-$0.00");
                networthTextView.setText("$0.00");
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        accountsListAdapter.swapCursor(null);
    }
}
