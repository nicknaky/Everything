package com.mushroomrobot.finwiz.budget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.data.EverythingContract;

/**
 * Created by Nick.
 */

public class DeleteBudgetDialog extends DialogFragment {

    long passedCategoryId;
    String passedBudgetName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        passedCategoryId = b.getLong("categoryId");
        passedBudgetName = b.getString("budgetName");

        final AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.delete_dialog, null);

        TextView titleView = (TextView) rootView.findViewById(R.id.delete_dialog_title);
        Button deleteButton = (Button) rootView.findViewById(R.id.delete_yes_button);
        Button cancelButton = (Button) rootView.findViewById(R.id.delete_no_button);

        titleView.setText("Delete " + passedBudgetName + " Budget?");

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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.setView(rootView);

        return deleteDialog;
    }
}

