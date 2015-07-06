package com.mushroomrobot.finwiz.data;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.mushroomrobot.finwiz.R;

/**
 * Created by Nick.
 */
public class DemoDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Initiate demo?");
        builder.setMessage("WARNING: This will remove all existing data.");
        builder.setPositiveButton(R.string.close_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Demo demo = new Demo();
                demo.demoSetUp(getActivity());
                dialog.dismiss();
                //mDrawerLayout.closeDrawers();
            }
        });
        builder.setNegativeButton(R.string.close_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.show();

    }
}