package com.mushroomrobot.everything.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Created by Nick.
 */
public class CurrencyFormatter implements TextWatcher{

    public EditText editText;

    public CurrencyFormatter(EditText newEditText) {
        editText = newEditText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {

        if (!s.toString().equals(s)) {
            editText.removeTextChangedListener(this);

            String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
            String cleanString = s.toString().replaceAll(replaceable, "");

            BigDecimal parsed;
            try {
                parsed = new BigDecimal(cleanString).divide(new BigDecimal(100));
            } catch (NumberFormatException e) {
                parsed = new BigDecimal(0.00);
            }
            String formatted = NumberFormat.getCurrencyInstance().format(parsed);
            editText.setText(formatted);
            editText.setSelection(formatted.length());
            editText.addTextChangedListener(this);
        }
    }


}
