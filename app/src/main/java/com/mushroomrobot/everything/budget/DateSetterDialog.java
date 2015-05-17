package com.mushroomrobot.everything.budget;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * Created by Nick.
 */
public class DateSetterDialog extends DatePickerDialog {


    DatePickerDialog.OnDateSetListener listener;
    int year, month, day;


    public DateSetterDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }
/*
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), listener, year, month, day);

        LayoutInflater inflater = getContext().getLayoutInflater();

        View view = inflater.inflate(R.layout.datepicker, null);

        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datepicker);
        ViewGroup childpicker = (ViewGroup)datePicker.findViewById(R.id.datepicker);

        TextView month = (TextView)childpicker.findViewById(Resources.getSystem().getIdentifier("date_picker_month","id","android"));// month widget
        TextView day = (TextView) childpicker.findViewById(Resources.getSystem().getIdentifier("date_picker_day","id","android"));

        month.setTextColor(getActivity().getResources().getColor(R.color.theme));
        day.setTextColor(getActivity().getResources().getColor(R.color.theme));

        datePickerDialog.setView(view);

        datePickerDialog.show();
        return datePickerDialog;
    }
*/
}
