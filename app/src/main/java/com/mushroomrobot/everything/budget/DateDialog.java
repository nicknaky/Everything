package com.mushroomrobot.everything.budget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
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
public class DateDialog {

    //This will have a date string in the format MM/dd/yy
    static EditText dateText;

    String dateSting;

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy",Locale.US);


    public DateDialog(EditText editText){
        dateText = editText;
        dateSting = dateText.getText().toString();


    }



    public static class DateDialogFrag extends DialogFragment {


        int year, month, day;


        TextView transDateBox;


        final Calendar myCalendar = Calendar.getInstance();


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                dateText.setText(sdf.format(myCalendar.getTime()));
            }
        };

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            year = myCalendar.get(Calendar.YEAR);
            month = myCalendar.get(Calendar.MONTH);
            day = myCalendar.get(Calendar.DAY_OF_MONTH);


            final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date, year, month, day);

            //DatePickerDialog.Builder builder = new DatePickerDialog.Builder(getActivity());

            LayoutInflater inflater2 = getActivity().getLayoutInflater();
            View view = inflater2.inflate(R.layout.datepicker, null);

            transDateBox = (TextView) view.findViewById(R.id.transaction_date_edit);

            final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datepicker);
            ViewGroup childPicker = (ViewGroup) datePicker.findViewById(R.id.datepicker);

            TextView month = (TextView) childPicker.findViewById(Resources.getSystem().getIdentifier("date_picker_month", "id", "android"));// month widget
            TextView day = (TextView) childPicker.findViewById(Resources.getSystem().getIdentifier("date_picker_day", "id", "android"));

            month.setTextColor(getActivity().getResources().getColor(R.color.theme));
            day.setTextColor(getActivity().getResources().getColor(R.color.theme));

            Button save = (Button) view.findViewById(R.id.save_button);
            Button cancel = (Button) view.findViewById(R.id.cancel_button);


            View.OnClickListener mClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.save_button:
                            date.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                            datePickerDialog.dismiss();
                        case R.id.cancel_button:
                            datePickerDialog.dismiss();
                        default:
                            break;
                    }
                }
            };

            save.setOnClickListener(mClickListener);
            cancel.setOnClickListener(mClickListener);

            datePickerDialog.setView(view);

                /*
                DialogInterface.OnClickListener dListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        date.onDateSet(datePicker,datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
                    }
                };
                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Save",dListener);
*/
//        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Save",null);
            //datePickerDialog.show();

            return datePickerDialog;
        }

    }
}