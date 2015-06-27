package com.mushroomrobot.finwiz.budget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        final EditText transDateBox = (EditText) dialogView.findViewById(R.id.transaction_date_edit);
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

                final AlertDialog dateDialog = new AlertDialog.Builder(getActivity()).create();

                LayoutInflater inflater2 = getActivity().getLayoutInflater();
                View view = inflater2.inflate(R.layout.datepicker, null);
                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datepicker);

                /* Change color of calendarView
                ViewGroup childPicker = (ViewGroup)datePicker.findViewById(R.id.datepicker);
                TextView month = (TextView)childPicker.findViewById(Resources.getSystem().getIdentifier("date_picker_month","id","android"));// month widget
                TextView day = (TextView) childPicker.findViewById(Resources.getSystem().getIdentifier("date_picker_day","id","android"));
                month.setTextColor(getActivity().getResources().getColor(R.color.theme));
                day.setTextColor(getActivity().getResources().getColor(R.color.theme));
                */

                Button save = (Button) view.findViewById(R.id.save_button);
                Button cancel = (Button) view.findViewById(R.id.cancel_button);

                datePicker.updateDate(myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));

                View.OnClickListener mClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()){
                            case R.id.save_button:
                                myCalendar.set(Calendar.YEAR,datePicker.getYear());
                                myCalendar.set(Calendar.MONTH, datePicker.getMonth());
                                myCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());


                                transDateBox.setText(simpleDateFormat.format(myCalendar.getTime()));

                                dateDialog.dismiss();
                            case R.id.cancel_button:
                                dateDialog.dismiss();
                            default:
                                break;
                        }
                    }
                };

                save.setOnClickListener(mClickListener);
                cancel.setOnClickListener(mClickListener);

                dateDialog.setView(view);
                dateDialog.show();

                float d = getActivity().getResources().getDisplayMetrics().density;
                int width = 292;
                int dialogWidth = (int) (d * width + 0.5f);
                dateDialog.getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

               /*
                //http://stackoverflow.com/questions/14439538/how-can-i-change-the-color-of-alertdialog-title-and-the-color-of-the-line-under
                //https://chromium.googlesource.com/android_tools/+/704c8ddee726aabe0e78bb88545972d2cf298190/sdk/platforms/android-16/data/res/layout/alert_dialog.xml
                ViewGroup buttonPanel = (ViewGroup) datePickerDialog.findViewById(Resources.getSystem().getIdentifier("buttonPanel", "id", "android"));
                buttonPanel.setVisibility(ViewGroup.GONE);
                ViewGroup contentPanel = (ViewGroup) datePickerDialog.findViewById(Resources.getSystem().getIdentifier("contentPanel", "id", "android"));
                contentPanel.setVisibility(ViewGroup.GONE);
                ViewGroup topPanel = (ViewGroup) datePickerDialog.findViewById(Resources.getSystem().getIdentifier("topPanel", "id", "android"));
                topPanel.setVisibility(ViewGroup.GONE);
*/

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
}

