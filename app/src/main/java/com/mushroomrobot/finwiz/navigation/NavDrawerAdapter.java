package com.mushroomrobot.finwiz.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
    private int selectedPosition;

    private String[] navOptions = new String[]{"Budgets", "Reports", "Accounts", "Settings"};
    private int[] navIcons = new int[]{
            R.drawable.ic_wallet_grey,
            R.drawable.ic_w_graph_grey,
            R.drawable.ic_bank_grey600_48dp,
            R.drawable.ic_settings_grey600_48dp
    };

    public NavDrawerAdapter(Context context, ArrayList<Item> items, int selectedPosition) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        this.selectedPosition = selectedPosition;
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
                ImageView image = (ImageView)v.findViewById(R.id.drawer_li_icons);
                if (image != null) image.setBackground(context.getDrawable(ei.icon));
                if (text != null) text.setText(ei.text);
                if (position == selectedPosition){
                    text.setTextColor(context.getResources().getColor(R.color.theme));
                } else text.setTextColor(context.getResources().getColor(R.color.textview));
            }
        }
        return v;
    }




}

