package com.mushroomrobot.everything.budget;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mushroomrobot.everything.R;
import com.mushroomrobot.everything.data.EverythingContract.Category;

import java.util.ArrayList;

/**
 * Created by Nick.
 */
public class DisplayBudgetFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static ArrayList<String> categoryList;

    ListView listView;
    Button addBudgetButton, addTransactionsButton;
    TextView noBudgetsTextView;
    ProgressBar budgetProgress;

    private double budget_set, budget_spent, budget_left;

    BudgetsAdapter budgetsListAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.budgets_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addbudget:
                FragmentManager fm = getFragmentManager();
                AddBudgetDialog dialog = new AddBudgetDialog();
                dialog.show(fm, null);
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_budget, container, false);


        listView = (ListView) rootView.findViewById(R.id.budget_listview);

        noBudgetsTextView = (TextView) rootView.findViewById(R.id.no_budgets_textview);
        addBudgetButton = (Button) rootView.findViewById(R.id.add_budget_button);
        addBudgetButton.setOnClickListener(mClickListener);

        noBudgetsTextView.setVisibility(TextView.INVISIBLE);
        addBudgetButton.setVisibility(Button.INVISIBLE);
        categoryList = new ArrayList<>();

        budgetProgress = (ProgressBar) rootView.findViewById(R.id.list_item_budget_progressbar);

        fillData();

        addTransactionsButton = (Button) rootView.findViewById(R.id.add_transactions_button);
        addTransactionsButton.setOnClickListener(mClickListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri uri = Uri.parse(Category.CONTENT_URI + "/" + id);
                long categoryId = id;


                Intent intent = new Intent(getActivity(),BudgetDetailsActivity.class);

                //Remember, while putExtra allows passing in Uri's, we aren't able to retrieve them with getExtra.
                //Thus we'll need to pass in the Uri as a string, and then retrieve and parse it into a Uri later.
                intent.putExtra("uri", uri.toString());
                intent.putExtra("categoryId", categoryId);
                intent.putExtra("categoryList",categoryList);

                startActivity(intent);

            }
        });

        return rootView;
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == addBudgetButton) {
                FragmentManager fm = getFragmentManager();
                AddBudgetDialog dialog = new AddBudgetDialog();
                dialog.show(fm, null);
            } else if (v == addTransactionsButton) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("categoryList",categoryList);

                FragmentManager fm = getFragmentManager();
                AddTransactionDialog dialog = new AddTransactionDialog();
                dialog.setArguments(bundle);
                dialog.show(fm, null);
            }
        }
    };

    private void fillData() {
        String[] list_from = {Category.COLUMN_NAME,
                Category.COLUMN_SPENT,
                Category.COLUMN_BUDGET,
                Category.COLUMN_REMAINING};
        int[] list_to = {R.id.list_item_budget_name_textview,
                R.id.list_item_budget_spent,
                R.id.list_item_budget_orig,
                R.id.list_item_budget_remaining};

        budgetsListAdapter = new BudgetsAdapter(getActivity(), R.layout.list_item_budget, null, list_from, list_to, 0);
        getLoaderManager().initLoader(0, null, this);

        listView.setAdapter(budgetsListAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == 0) {
            CursorLoader cursorLoader = new CursorLoader(getActivity(), Category.CONTENT_URI, null, null, null, null);
            return cursorLoader;
        }
        else return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        noBudgetsTextView.setVisibility(TextView.INVISIBLE);
        addBudgetButton.setVisibility(Button.INVISIBLE);
        addTransactionsButton.setVisibility(TextView.VISIBLE);

        String getDataCursor = data.getColumnName(1);
        Log.v("getDataCursor", getDataCursor);

        if (loader.getId() == 0) {
            budgetsListAdapter.swapCursor(data);

            if (!budgetsListAdapter.getCursor().moveToFirst()) {
                noBudgetsTextView.setVisibility(TextView.VISIBLE);
                addBudgetButton.setVisibility(Button.VISIBLE);
                addTransactionsButton.setVisibility(Button.INVISIBLE);
            } else {
                String vBudget_name = data.getString(data.getColumnIndex(Category.COLUMN_NAME));
                String vBudget_spent = String.valueOf(data.getDouble(data.getColumnIndex(Category.COLUMN_SPENT)));
                String vBudget_orig = String.valueOf(data.getDouble(data.getColumnIndex(Category.COLUMN_BUDGET)));
                String vBudget_remaining = String.valueOf(data.getDouble(data.getColumnIndex(Category.COLUMN_REMAINING)));

                Log.v("budget_name", vBudget_name);
                Log.v("budget_spent", vBudget_spent);
                Log.v("budget_orig", vBudget_orig);
                Log.v("budget_remaining", vBudget_remaining);

                categoryList.clear();
                while (!data.isAfterLast()) {
                    //Log.v("adding category:", getString(data.getColumnIndex(Category.COLUMN_NAME)));
                    categoryList.add(data.getString(data.getColumnIndex(Category.COLUMN_NAME)));

                    data.moveToNext();
                }

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        budgetsListAdapter.swapCursor(null);
    }

}
