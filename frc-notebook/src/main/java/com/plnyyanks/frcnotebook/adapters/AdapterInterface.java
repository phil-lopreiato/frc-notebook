package com.plnyyanks.frcnotebook.adapters;

import com.plnyyanks.frcnotebook.datatypes.Note;

/**
 * File created by phil on 3/16/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public interface AdapterInterface {

    public void addNote(Note n);
    public void updateNote(Note n);
    public void updateListData();
}
