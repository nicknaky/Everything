package com.mushroomrobot.everything.budget;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.mushroomrobot.everything.R;
import com.mushroomrobot.everything.data.EverythingContract;
import com.mushroomrobot.everything.utils.CurrencyFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Nick.
 */

public class AddTransactionDialog extends DialogFragment {

    ArrayList categoryList;
    String budgetName;

    int checkedItem;

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
        View dialogView = inflater.inflate(R.layout.transaction_dialog, null);

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

        //Implementing date dialog within a dialog: http://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                transDateBox.setText(sdf.format(myCalendar.getTime()));
            }
        };
        transDateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        String currentDate = new SimpleDateFormat("MM/dd/yy", Locale.US).format(new Date());
        transDateBox.setText(currentDate);

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

                        getActivity().getContentResolver().insert(EverythingContract.Transactions.CONTENT_URI, contentValues);

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

