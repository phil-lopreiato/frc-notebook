package com.plnyyanks.frcnotebook.datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * File created by phil on 2/23/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
 */
public class ListGroup {

    public String string;
    public final List<String> children = new ArrayList<String>();
    public final List<String> children_keys = new ArrayList<String>();

    public ListGroup(String string) {
        this.string = string;
    }

    public void updateTitle(String string){
        this.string = string;
    }

    public String getTitle(){
        return string;
    }

}
