import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;

/**
 * File created by phil on 4/10/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class TestStartActivity extends ActivityInstrumentationTestCase2<StartActivity> {

    private Activity activity;
    private ListView eventList;
    private TextView headerView;

    public TestStartActivity(Class<StartActivity> activityClass) {
        super(activityClass);
    }

    public TestStartActivity(){
        super(StartActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        activity = getActivity();
        eventList = (ListView)activity.findViewById(R.id.event_list);
        headerView  = (TextView)activity.findViewById(R.id.select_event_header);
    }

    public void testPreconditions(){
        assertNotNull("Start Activity is null",activity);
        assertNotNull("Local event ListView is null",eventList);
        assertNotNull("Local Event Screen Header is null",headerView);
    }

    public void testHeaderText(){
        final String expectedHeader = activity.getString(R.string.select_event_title);
        final String actualHeader = headerView.getText().toString();
        assertNotNull("Local Event Screen Header is null",actualHeader);
        assertEquals("Local Event Screen Header is incorrect",expectedHeader,actualHeader);
    }
}
