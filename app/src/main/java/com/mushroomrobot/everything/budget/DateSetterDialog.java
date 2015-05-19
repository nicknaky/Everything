package com.mushroomrobot.everything.budget;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.mushroomrobot.everything.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Nick.
 */
public class DateSetterDialog extends DialogFragment {

    EditText editText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog dateDialog = new AlertDialog.Builder(getActivity()).create();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.datepicker,null);

        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datepicker);

        ViewGroup childPicker = (ViewGroup)datePicker.findViewById(R.id.datepicker);
        TextView month = (TextView)childPicker.findViewById(Resources.getSystem().getIdentifier("date_picker_month","id","android"));// month widget
        TextView day = (TextView) childPicker.findViewById(Resources.getSystem().getIdentifier("date_picker_day","id","android"));
        month.setTextColor(getActivity().getResources().getColor(R.color.theme));
        day.setTextColor(getActivity().getResources().getColor(R.color.theme));

        Button save = (Button) view.findViewById(R.id.save_button);
        Button cancel = (Button) view.findViewById(R.id.cancel_button);

        View.OnClickListener mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.save_button:
                        date.onDateSet(datePicker,datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
                        datePicker.updateDate(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
                        dateDialog.dismiss();
                    case R.id.cancel_button:
                        dateDialog.dismiss();
                    default:
                        break;
                }
            }
        };



        return dateDialog;
    }
    final Calendar myCalendar = Calendar.getInstance();

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            //transDateBox.setText(sdf.format(myCalendar.getTime()));
            Log.v("day of month in dateset", String.valueOf(myCalendar.get(Calendar.DAY_OF_MONTH)));
        }
    };
}