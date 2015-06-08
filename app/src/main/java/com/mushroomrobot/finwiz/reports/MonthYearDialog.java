package com.mushroomrobot.finwiz.reports;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.mushroomrobot.finwiz.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by NLam.
 */
public class MonthYearDialog extends DialogFragment{

    TextView monthYearView;
    DatePicker datePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        monthYearView = (TextView) getActivity().findViewById(R.id.reports_month_value);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_month_year, null);
        Button saveButton = (Button) dialogView.findViewById(R.id.month_year_save_button);
        Button cancelButton = (Button) dialogView.findViewById(R.id.month_year_cancel_button);

        final SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.US);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar newCalendar = Calendar.getInstance();
                newCalendar.set(Calendar.MONTH, datePicker.getMonth());
                newCalendar.set(Calendar.YEAR, datePicker.getYear());
                String monthYear = sdf.format(newCalendar.getTime());
                monthYearView.setText(monthYear);
                dialog.dismiss();
            }
        });

        datePicker = (DatePicker) dialogView.findViewById(R.id.month_year_picker);
        datePicker.findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);

        String monthYearText = monthYearView.getText().toString();
        Log.v("monthYearText", monthYearText);

        Date monthYearDate = new Date();

        try {
            monthYearDate = sdf.parse(monthYearText);
        } catch (ParseException e) {
            Toast.makeText(getActivity(), "Failed parsing date text.", Toast.LENGTH_SHORT).show();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(monthYearDate);

        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePicker.updateDate(year, month, 1);

        String logDate = "month: " + String.valueOf(month) + " , year: " + String.valueOf(year);
        Log.v("monthYear", logDate);

        dialog.setView(dialogView);

        return dialog;
    }
/*
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String month = new DateFormatSymbols().getMonths()[monthOfYear];
        String result = month + " " + year;

        monthYearView.setText(result);

    }
    */
}
