package com.plnyyanks.frcnotebook.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by phil on 2/22/14.
 */
public class EventListArrayAdapter extends ArrayAdapter<String> {
    private Context context;
    public ArrayList<String> values, keys;

    public EventListArrayAdapter(Context context,String[] values,String[] keys){
        this(context, new ArrayList<String>(Arrays.asList(values)),new ArrayList<String>(Arrays.asList(keys)));
    }

    public EventListArrayAdapter(Context context,ArrayList<String> values,ArrayList<String> keys){
        super(context,android.R.layout.simple_list_item_1,values);
        this.context = context;
        this.values = values;
        this.keys = keys;
    }

    public void removeAt(int index){
        if(index >= 0){
            values.remove(index);
            keys.remove(index);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            /*LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(android.R.layout.simple_list_item_1,parent,false);
            TextView mainLine = (TextView) rowView.findViewById(android.R.layout.simple_list_item_1); */
        View v = super.getView(position, convertView, parent);
        if(v.isSelected()){
            v.setBackgroundResource(android.R.color.holo_blue_light);
        }else{
            v.setBackgroundResource(android.R.color.transparent);
        }
        return v;
    }

    public String getEventKey(int position){
        return keys.get(position);
    }

}