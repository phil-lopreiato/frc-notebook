import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.TestCase;

/**
 * File created by phil on 4/10/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
 */
public class BasicTest extends TestCase {

    @SmallTest
    public void testBasics() {
        assertEquals("Test true == true",true,true);
        assertEquals("10 x 5 must be 50", 50, 10*5);
    }
}
