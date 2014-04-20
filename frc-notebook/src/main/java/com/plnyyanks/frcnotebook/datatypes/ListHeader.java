package com.plnyyanks.frcnotebook.datatypes;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.adapters.ListViewArrayAdapter;

/**
 * File created by phil on 3/1/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
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
