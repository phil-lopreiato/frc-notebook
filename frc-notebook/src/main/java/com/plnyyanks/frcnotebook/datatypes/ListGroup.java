package com.plnyyanks.frcnotebook.datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 2/23/14.
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

}
