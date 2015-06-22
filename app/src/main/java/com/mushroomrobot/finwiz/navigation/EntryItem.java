package com.mushroomrobot.finwiz.navigation;

/**
 * Created by Nick.
 */
public class EntryItem implements Item{

    public int icon;
    public String text;

    public EntryItem(String text, int icon){
        this.text = text;
        this.icon = icon;
    }

    public String getText(){
        return text;
    }

    @Override
    public boolean isDivider() {
        return false;
    }
}
