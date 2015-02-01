package com.plnyyanks.frcnotebook.datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * File created by phil on 2/23/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
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
