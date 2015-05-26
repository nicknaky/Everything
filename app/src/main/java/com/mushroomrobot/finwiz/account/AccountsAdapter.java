package com.mushroomrobot.finwiz.account;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

/**
 * Created by Nick.
 */
public class AccountsAdapter extends SimpleCursorAdapter {

    private int[] colors = {Color.parseColor("#f0f0f0"), Color.parseColor("#FAFAFA")};

    public AccountsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = super.getView(position, convertView, parent);
        int colorPos = position % colors.length;
        view.setBackgroundColor(colors[colorPos]);

        return view;
    }


}
