package com.mushroomrobot.finwiz.utils;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by Nick.
 */
public class MPCustomNumberFormatter implements ValueFormatter {

    private DecimalFormat numberFormat;

    public MPCustomNumberFormatter(){
        numberFormat = new DecimalFormat("#,###");
    }

    @Override
    public String getFormattedValue(float value) {
        return numberFormat.format(value);
    }
}
