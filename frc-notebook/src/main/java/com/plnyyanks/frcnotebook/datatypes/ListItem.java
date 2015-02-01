package com.plnyyanks.frcnotebook.datatypes;

import android.view.LayoutInflater;
import android.view.View;

/**
 * File created by phil on 3/1/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public interface ListItem {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
    public void setSelected(boolean selected);
}
