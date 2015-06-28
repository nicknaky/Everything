package com.mushroomrobot.finwiz.common;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Nick.
 */

public class UniversalDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    EditText dateEdit;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        String editDate = dateEdit.getText().toString();
        if (editDate != "") {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
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

        String month = new DateFormatSymbols().getMonths()[monthOfYear];
        String result = month + " " + dayOfMonth + ", " + year;
        dateEdit.setText(result);
    }
}

