package com.plnyyanks.frcnotebook.datatypes;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.plnyyanks.frcnotebook.adapters.ListViewArrayAdapter;

/**
 * File created by phil on 3/1/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
 */
public class ListElement implements ListItem {
    private final String str1,key;
    private View view;
    private boolean selected=false;

    public ListElement(String text1,String key) {
        this.str1 = text1;
        this.key = key;
    }

    @Override
    public int getViewType() {
        return ListViewArrayAdapter.ItemType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        if(view == null){
            view = (View) inflater.inflate(android.R.layout.simple_list_item_1, null);

            TextView text1 = (TextView) view.findViewById(android.R.id.text1);
            text1.setText(str1);
            text1.setSelected(selected);
            if(text1.isSelected()){
                text1.setBackgroundResource(android.R.color.holo_blue_light);
            }else{
                text1.setBackgroundResource(android.R.color.transparent);
            }
        }
        return view;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public String getKey(){
        return key;
    }
}
