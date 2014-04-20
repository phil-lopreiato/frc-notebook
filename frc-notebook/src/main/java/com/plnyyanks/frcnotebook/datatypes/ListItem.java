package com.plnyyanks.frcnotebook.datatypes;

import android.view.LayoutInflater;
import android.view.View;

/**
 * File created by phil on 3/1/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
 */
public interface ListItem {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
    public void setSelected(boolean selected);
}
