package com.plnyyanks.frcnotebook.datatypes;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by phil on 3/1/14.
 */
public interface ListItem {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
    public void setSelected(boolean selected);
}
