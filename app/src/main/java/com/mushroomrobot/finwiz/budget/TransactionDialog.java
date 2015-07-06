package com.mushroomrobot.finwiz.budget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mushroomrobot.finwiz.R;
import com.mushroomrobot.finwiz.data.EverythingContract.Transactions;
import com.mushroomrobot.finwiz.utils.CurrencyFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;




/**
 * Created by Nick.
 */

public class TransactionDialog extends DialogFragment {

    ArrayList categoryList;
    String budgetName;

    static EditText transDateBox;

    int checkedItem;
    long transactionId;

    int editMode = 0;

    @Override
    public void onStart() {
        super.onStart();

        float d = getActivity().getResources().getDisplayMetrics().density;
        int width = 330;
        int height = 365;
        int dialogWidth = (int) (d * width + 0.5f);
        int dialogHeight = (int) (d * height + 0.5f);
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog transDialog = new AlertDialog.Builder(getActivity()).create();
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_transaction, null);

        TextView titleView = (TextView) dialogView.findViewById(R.id.trans_dialog_title);
        final EditText transCatBox = (EditText) dialogView.findViewById(R.id.transaction_category_edit);
        final EditText transAmountBox = (EditText) dialogView.findViewById(R.id.transaction_amount_edit);
        transDateBox = (EditText) dialogView.findViewById(R.id.transaction_date_edit);
        final EditText transDescBox = (EditText) dialogView.findViewById(R.id.transaction_desc_edit);
        final Button transSaveButton = (Button) dialogView.findViewById(R.id.trans_save_button);
        final Button transCancelButton = (Button) dialogView.findViewById(R.id.trans_cancel_button);

        transCatBox.setGravity(Gravity.RIGHT);

        categoryList = new ArrayList<String>();
        categoryList = getArguments().getStringArrayList("categoryList");

        budgetName = getArguments().getString("budgetName");

        checkedItem = categoryList.indexOf(budgetName);

        if (checkedItem != -1) {
            transCatBox.setText(budgetName);
        }

        transCatBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final String[] categoryArray = new String[categoryList.size()];
                categoryList.toArray(categoryArray);
                String editCategory = transCatBox.getText().toString();
                if (editCategory != ""){
                    checkedItem = categoryList.indexOf(editCategory);
                }
                builder.setTitle("Choose Budget").setSingleChoiceItems(categoryArray, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        transCatBox.setText(categoryArray[which]);
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        transAmountBox.addTextChangedListener(new CurrencyFormatter(transAmountBox));

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);

        String currentDate = simpleDateFormat.format(new Date());
        transDateBox.setText(currentDate);

        //Determine if transaction is in edit mode.
        transactionId = getArguments().getLong("transactionId", -99);
        if (transactionId!=-99){
            editMode = 1;
            titleView.setText(getActivity().getResources().getString(R.string.edit_transaction_title));
            Uri mUri = Uri.parse(Transactions.CONTENT_URI + "/" + transactionId);
            Cursor cursor = getActivity().getContentResolver().query(mUri,null, Transactions._ID + " = " + transactionId,null,null);
            cursor.moveToFirst();
            transAmountBox.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(Transactions.COLUMN_AMOUNT))));

            long dateInMillis = cursor.getLong(cursor.getColumnIndex(Transactions.COLUMN_DATE));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateInMillis);

            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            transDateBox.setText(sdf.format(calendar.getTime()));

            String editDesc = cursor.getString(cursor.getColumnIndex(Transactions.COLUMN_DESCRIPTION));
            transDescBox.setText(editDesc);

            cursor.close();
        }

        currentDate = transDateBox.getText().toString();


        final Calendar myCalendar = Calendar.getInstance();

        try {
            myCalendar.setTime(simpleDateFormat.parse(currentDate));
        }catch (ParseException e){
            Toast.makeText(getActivity(),"Error parsing date.",Toast.LENGTH_SHORT).show();
        }

        transDateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.support.v4.app.FragmentManager fm = getFragmentManager();
                DialogFragment newFragment = new DateFragment();
                newFragment.show(fm, "date");

            }
        });

        transSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transCatBox.getText().toString().length() > 0) {
                    try {
                        String enterCategory = transCatBox.getText().toString();
                        String cleanedAmount = transAmountBox.getText().toString().replaceAll("[^0-9]", "");
                        int enterAmount = Integer.valueOf(cleanedAmount);

                        Date cleanedDate = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
                        try {
                            cleanedDate = new SimpleDateFormat("MM/dd/yy", Locale.US).parse(transDateBox.getText().toString());
                        } catch (ParseException e) {
                            Toast.makeText(getActivity(), "Error reading date", Toast.LENGTH_SHORT);
                            System.err.println("Error parsing date: " + e);
                        }
                        long enterDate = cleanedDate.getTime();
                        String enterDesc = transDescBox.getText().toString();
                        if (enterDesc.length() < 1) {
                            enterDesc = "No Description";
                        }

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("category", enterCategory);
                        contentValues.put("amount", enterAmount);
                        contentValues.put("date", enterDate);
                        contentValues.put("description", enterDesc);

                        if (editMode==0) {
                            getActivity().getContentResolver().insert(Transactions.CONTENT_URI, contentValues);
                        }
                        else if (editMode==1){
                            getActivity().getContentResolver().update(Uri.parse(Transactions.CONTENT_URI + "/" + transactionId),contentValues,null,null);
                        }

                        transDialog.dismiss();
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_amount_dialog), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.error_category_dialog), Toast.LENGTH_SHORT).show();
                }

            }
        });

        transCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transDialog.dismiss();
            }
        });

        transDialog.setView(dialogView);
        return transDialog;
    }

    public static class DateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        Calendar c;
        SimpleDateFormat sdf;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            c = Calendar.getInstance();
            String editDate = transDateBox.getText().toString();
            if (editDate != "") {

                Date date;
                try {
                    date = sdf.parse(editDate);
                    c.setTime(date);
                } catch (ParseException e) {
                    Toast.makeText(getActivity(), "Error parsing date", Toast.LENGTH_SHORT);
                }
            }
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            c.set(year, monthOfYear, dayOfMonth);
            transDateBox.setText(sdf.format(c.getTime()));
        }
    }
}

