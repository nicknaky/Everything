package com.mushroomrobot.finwiz.budget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.data.EverythingContract;

/**
 * Created by Nick.
 */

public class DeleteDialog extends DialogFragment {

    final private int DELETE_BUDGET = 1;
    final private int DELETE_TRANS = 2;

    int passedDeleteType;

    long passedCategoryId;
    String passedBudgetName;

    long passedTransactionId;
    String passedTransactionDesc;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        passedDeleteType = b.getInt("deleteType");

        final AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.delete_dialog, null);

        TextView titleView = (TextView) rootView.findViewById(R.id.delete_dialog_title);
        TextView descriptionView = (TextView) rootView.findViewById(R.id.delete_dialog_desc);
        Button deleteButton = (Button) rootView.findViewById(R.id.delete_yes_button);
        Button cancelButton = (Button) rootView.findViewById(R.id.delete_no_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        switch (passedDeleteType) {
            case DELETE_BUDGET:
                passedCategoryId = b.getLong("categoryId");
                passedBudgetName = b.getString("budgetName");

                titleView.setText("Delete " + passedBudgetName + " Budget?");
                descriptionView.setText(getActivity().getResources().getString(R.string.delete_budget_dialog_desc));
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), DisplayBudgetActivity.class);
                        intent.putExtra("deleteUri", EverythingContract.Category.CONTENT_URI + "/" + passedCategoryId);
                        intent.putExtra("deleteCategory", "\"" + passedBudgetName + "\"");
                        startActivity(intent);

                        getActivity().finish();
                        deleteDialog.dismiss();
                    }
                });
                break;

            case DELETE_TRANS:
                passedTransactionId = b.getLong("transactionId");
                passedTransactionDesc = b.getString("transactionDesc");

                titleView.setVisibility(View.GONE);
                descriptionView.setText("Delete transaction?");
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri mUri = Uri.parse(EverythingContract.Transactions.CONTENT_URI + "/" + passedTransactionId);
                        getActivity().getContentResolver().delete(mUri,null,null);
                        Log.v("Uri", String.valueOf(mUri));
                        deleteDialog.dismiss();
                    }
                });
                break;
        }

        deleteDialog.setView(rootView);

        return deleteDialog;
    }
}

