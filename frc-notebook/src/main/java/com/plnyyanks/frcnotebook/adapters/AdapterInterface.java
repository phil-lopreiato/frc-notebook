package com.plnyyanks.frcnotebook.adapters;

import com.plnyyanks.frcnotebook.datatypes.Note;

/**
 * Created by phil on 3/16/14.
 */
public interface AdapterInterface {

    public void addNote(Note n);
    public void updateNote(Note n);
    public void updateListData();
}
