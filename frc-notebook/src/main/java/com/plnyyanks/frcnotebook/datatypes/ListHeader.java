package com.plnyyanks.frcnotebook.datatypes;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.adapters.ListViewArrayAdapter;

/**
 * Created by phil on 3/1/14.
 */
public class ListHeader implements ListItem{
    private final String name;

    public ListHeader(String name) {
        this.name = name;
    }

    @Override
    public int getViewType() {
        return ListViewArrayAdapter.ItemType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        //if (convertView == null) {
            view = (View) inflater.inflate(R.layout.list_header, null);
            // Do some initialization
        /*} else {
            view = convertView;
        }*/

        TextView text = (TextView) view.findViewById(R.id.separator);
        text.setText(name);

        return view;
    }

    @Override
    public void setSelected(boolean selected) {

    }
}
