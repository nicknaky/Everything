package com.mushroomrobot.finwiz.data;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.mushroomrobot.finwiz.R;

/**
 * Created by NLam.
 */
public class ClearDataDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Delete Database?");
        builder.setMessage("Doing this will permanently delete all existing data. Please use the " +
                "Export feature to backup data before proceeding.");
        builder.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EverythingDbHelper dbHelper = new EverythingDbHelper(getActivity());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                db.delete(EverythingContract.Category.TABLE_NAME, null, null);
                db.delete(EverythingContract.Transactions.TABLE_NAME, null, null);
                db.delete(EverythingContract.Accounts.TABLE_NAME, null, null);

                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
