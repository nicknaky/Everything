package com.mushroomrobot.finwiz.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mushroomrobot.finwiz.R;

import java.util.ArrayList;

/**
 * Created by Nick.
 */
public class NavDrawerAdapter extends ArrayAdapter<Item> {

    private Context context;
    private ArrayList<Item> items;
    private LayoutInflater vi;

    private String[] navOptions = new String[]{"Budgets", "Reports", "Accounts", "Settings"};

    public NavDrawerAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        final Item i = items.get(position);
        if (i != null) {
            if(i.isDivider()){
                DividerItem di = (DividerItem)i;
                v = vi.inflate(R.layout.drawer_li_divider, null);
                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);
            } else {
                EntryItem ei = (EntryItem)i;
                v = vi.inflate(R.layout.drawer_list_item, null);
                final TextView text =
                        (TextView)v.findViewById(R.id.drawer_li_labels);
                if (text != null) text.setText(ei.text);
            }
        }
        return v;
    }


}

