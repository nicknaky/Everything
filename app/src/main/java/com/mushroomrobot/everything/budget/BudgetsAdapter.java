package com.mushroomrobot.everything.budget;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mushroomrobot.everything.R;
import com.mushroomrobot.everything.data.EverythingContract;

import java.text.NumberFormat;

/**
 * Created by Nick.
 */
//Overrides SimpleCursorAdapter to allow for ProgressBar binding
public class BudgetsAdapter extends SimpleCursorAdapter {
    
    public BudgetsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags){
        super(context,layout,c,from,to,flags);
    }


    //In case having problems with landscape view, check out: http://stackoverflow.com/questions/9987630/disappearing-progressbar-in-android-listview
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.list_item_budget_progressbar);
        int percentBudget = cursor.getInt(cursor.getColumnIndex(EverythingContract.Category.COLUMN_PERCENT));
        progressBar.setProgress(percentBudget);

        TextView spentView = (TextView) view.findViewById(R.id.list_item_budget_spent);
        double budgetSpent = cursor.getDouble(cursor.getColumnIndex(EverythingContract.Category.COLUMN_SPENT)) / 100;
        String formatSpent = NumberFormat.getCurrencyInstance().format(budgetSpent);
        spentView.setText(formatSpent);

        TextView originalView = (TextView) view.findViewById(R.id.list_item_budget_orig);
        double budgetOrig = cursor.getDouble(cursor.getColumnIndex(EverythingContract.Category.COLUMN_BUDGET)) / 100;
        String formatOrig = NumberFormat.getCurrencyInstance().format(budgetOrig);
        originalView.setText(formatOrig);

        TextView remainingView = (TextView) view.findViewById(R.id.list_item_budget_remaining);
        double budgetRemaining = cursor.getDouble(cursor.getColumnIndex(EverythingContract.Category.COLUMN_REMAINING)) / 100;
        if (budgetRemaining < 0.00){
            remainingView.setTextColor(context.getResources().getColor(R.color.red_money));
        }
        else if (budgetRemaining > 0){
            remainingView.setTextColor(context.getResources().getColor(R.color.textview));
        }
        String formatRemaining = NumberFormat.getCurrencyInstance().format(budgetRemaining);
        //Optional balance format which removes cents.
        String formatRemainingV2 = formatRemaining.substring(0, formatRemaining.length() - 3);
        remainingView.setText(formatRemainingV2);
        Log.v("budgetRemaining", String.valueOf(budgetRemaining));
    }

    @Override
    public void setViewBinder(ViewBinder viewBinder) {
        super.setViewBinder(viewBinder);
    }
}
