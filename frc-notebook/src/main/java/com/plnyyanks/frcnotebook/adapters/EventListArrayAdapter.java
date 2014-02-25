package com.plnyyanks.frcnotebook.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by phil on 2/22/14.
 */
public class EventListArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String values[], keys[];

    public EventListArrayAdapter(Context context,String[] values,String[] keys){
        super(context,android.R.layout.simple_list_item_1,values);
        this.context = context;
        this.values = values;
        this.keys = keys;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            /*LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(android.R.layout.simple_list_item_1,parent,false);
            TextView mainLine = (TextView) rowView.findViewById(android.R.layout.simple_list_item_1); */
        return super.getView(position, convertView, parent);
    }

    public String getEventKey(int position){
        return keys[position];
    }

}