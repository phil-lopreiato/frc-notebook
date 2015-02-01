import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.TestCase;

/**
 * File created by phil on 4/10/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class BasicTest extends TestCase {

    @SmallTest
    public void testBoolean() {
        assertEquals("Test true == true",true,true);
        assertEquals("Test false == false",false,false);
    }

    @SmallTest
    public void testMultiplication(){
        assertEquals("10 x 5 must be 50", 50, 10*5);
    }

    @SmallTest
    public void testAddition(){
        assertEquals("2+2=4?",2+2,4);
        assertEquals("2-2=0",2-2,0);
    }
}
