package com.mushroomrobot.finwiz.budget;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.data.EverythingContract.Category;
import com.mushroomrobot.finwiz.utils.CurrencyFormatter;

/**
 * Created by Nick.
 */

public class AddBudgetDialog extends DialogFragment {

    String passedBudgetName;
    String passedBudgetAmount;

    int editMode = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog budgetDialog = new AlertDialog.Builder(getActivity()).create();
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.budget_dialog, null);

        final TextView budgetTitle = (TextView) dialogView.findViewById(R.id.budget_title_dialog);

        final EditText budgetNameBox = (EditText) dialogView.findViewById(R.id.budgetNameBox);
        final EditText budgetAmountBox = (EditText) dialogView.findViewById(R.id.budgetAmountBox);

        final Button saveBudgetButton = (Button) dialogView.findViewById(R.id.budget_save_button);
        final Button cancelBudgetButton = (Button) dialogView.findViewById(R.id.budget_cancel_button);

        budgetAmountBox.addTextChangedListener(new CurrencyFormatter(budgetAmountBox));

        Bundle b = getArguments();

        if (b!=null){
            budgetTitle.setText(getActivity().getResources().getString(R.string.edit_budget));
            editMode = getArguments().getInt("editMode");
            passedBudgetName = getArguments().getString("budgetName");
            budgetNameBox.setText(passedBudgetName);
            budgetNameBox.setSelection(budgetNameBox.getText().length());
            passedBudgetAmount = getArguments().getString("budgetAmount");
            budgetAmountBox.setText(String.valueOf(passedBudgetAmount));

        }

        saveBudgetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (budgetNameBox.getText().toString().length() > 0) {
                    try {
                        String name = budgetNameBox.getText().toString();
                        String cleanedAmount = budgetAmountBox.getText().toString().replaceAll("[^0-9]", "");
                        int amount = Integer.valueOf(cleanedAmount);

                        ContentValues catContentValues = new ContentValues();
                        catContentValues.put(Category.COLUMN_NAME, name);
                        catContentValues.put(Category.COLUMN_BUDGET, amount);

                        if (editMode == 1) {
                            String catSelection = Category.COLUMN_NAME + " = ?";
                            String[] catSelectionArgs = {passedBudgetName};
                            try {
                                getActivity().getContentResolver().update(Category.CONTENT_URI, catContentValues, catSelection, catSelectionArgs);
                                budgetDialog.dismiss();
                            } catch (SQLiteConstraintException e){
                                Toast.makeText(getActivity(), R.string.budget_name_exists, Toast.LENGTH_SHORT).show();
                            }
                        } else if (editMode == 0) {
                            try {
                                getActivity().getContentResolver().insert(Category.CONTENT_URI, catContentValues);
                                budgetDialog.dismiss();
                            }catch (SQLiteConstraintException e ){
                                Toast.makeText(getActivity(), R.string.budget_name_exists, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (NumberFormatException e){
                        Toast.makeText(getActivity(),R.string.error_amount_dialog, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), R.string.error_budget_dialog, Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelBudgetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                budgetDialog.dismiss();
            }
        });
        budgetDialog.setView(dialogView);
        return budgetDialog;
    }
}

/*

builder.setPositiveButton(R.string.save_budget_dialog, new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {

        if (budgetNameBox.getText().toString().length() > 0) {
        String name = budgetNameBox.getText().toString();
        String cleanedAmount = budgetAmountBox.getText().toString().replaceAll("[^0-9]", "");
        int amount = Integer.valueOf(cleanedAmount);

        ContentValues catContentValues = new ContentValues();
        catContentValues.put(Category.COLUMN_NAME, name);
        catContentValues.put(Category.COLUMN_BUDGET, amount);

        if (editMode==1){
        String catSelection = Category.COLUMN_NAME + " = ?";
        String[] catSelectionArgs = {passedBudgetName};

        getActivity().getContentResolver().update(Category.CONTENT_URI, catContentValues, catSelection, catSelectionArgs);

        dialog.dismiss();

        }
        else if(editMode==0) {
        getActivity().getContentResolver().insert(Category.CONTENT_URI, catContentValues);
        dialog.dismiss();
        }

        } else {
        Toast.makeText(getActivity(), R.string.error_budget_dialog, Toast.LENGTH_SHORT).show();
        }
        }
        });
        builder.setNegativeButton(R.string.cancel_budget_dialog, new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {
        dismiss();
        ;
        }
        });

        */