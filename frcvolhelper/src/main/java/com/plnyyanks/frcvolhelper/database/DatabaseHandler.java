package com.plnyyanks.frcvolhelper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.plnyyanks.frcvolhelper.datatypes.Event;
import com.plnyyanks.frcvolhelper.datatypes.Match;
import com.plnyyanks.frcvolhelper.datatypes.Note;
import com.plnyyanks.frcvolhelper.datatypes.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 2/19/14.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION       = 1;
    private static final String DATABASE_NAME       = "VOL_NOTES",

                                TABLE_EVENTS        = "events",
                                    KEY_EVENTKEY    = "eventKey",
                                    KEY_EVENTNAME   = "eventName",
                                    KEY_EVENTYEAR   = "eventYear",
                                    KEY_EVENTLOC    = "eventLocation",
                                    KEY_EVENTSTART  = "startDate",
                                    KEY_EVENTEND    = "endDate",

                                TABLE_MATCHES       = "matches",
                                    KEY_MATCHKEY    = "matchKey",
                                    KEY_PARENTEVENT = "parentEvent",
                                    KEY_MATCHTYPE   = "type",
                                    KEY_MATCHNO     = "matchNumber",
                                    KEY_BLUE1       = "blue1",
                                    KEY_BLUE2       = "blue2",
                                    KEY_BLUE3       = "blue3",
                                    KEY_RED1        = "red1",
                                    KEY_RED2        = "red2",
                                    KEY_RED3        = "red3",

                                TABLE_TEAMS         = "teams",
                                    KEY_TEAMKEY     = "teamKey",
                                    KEY_TEAMNUMBER  = "teamNumber",
                                    KEY_TEAMEVENTS  = "events",

                                TABLE_NOTES         = "notes",
                                    KEY_NOTEID      = "id",
                                    //KEY_EVENTKEY  = "eventKey",
                                    //KEY_MATCHKEY  = "matchKey",
                                    //KEY_TEAMKEY   = "teamKey",
                                    KEY_NOTE        = "note";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + KEY_EVENTKEY  + " TEXT PRIMARY KEY,"
                + KEY_EVENTNAME + " TEXT,"
                + KEY_EVENTYEAR + " INTEGER,"
                + KEY_EVENTLOC  + " TEXT,"
                + KEY_EVENTSTART+ " TEXT,"
                + KEY_EVENTEND  + " TEXT"
                + ")";
        db.execSQL(CREATE_EVENTS_TABLE);

        String CREATE_MATCHES_TABLE = "CREATE TABLE " + TABLE_MATCHES + "("
                + KEY_MATCHKEY  + " TEXT PRIMARY KEY,"
                + KEY_MATCHTYPE + " TEXT,"
                + KEY_MATCHNO   + " INTEGER,"
                + KEY_BLUE1     + " INTEGER,"
                + KEY_BLUE2     + " INTEGER,"
                + KEY_BLUE3     + " INTEGER,"
                + KEY_RED1     + " INTEGER,"
                + KEY_RED2     + " INTEGER,"
                + KEY_RED3     + " INTEGER"
                + ")";
        db.execSQL(CREATE_MATCHES_TABLE);

        String CREATE_TEAMS_TABLE = "CREATE TABLE " + TABLE_TEAMS + "("
                + KEY_TEAMKEY    + "TEXT PRIMARY KEY,"
                + KEY_TEAMNUMBER + "INTEGER,"
                + KEY_TEAMEVENTS + "TEXT"
                + ")";
        db.execSQL(CREATE_TEAMS_TABLE);

        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + KEY_NOTEID    + "INTEGER PRIMARY KEY,"
                + KEY_EVENTKEY  + "TEXT,"
                + KEY_MATCHKEY  + "TEXT,"
                + KEY_TEAMKEY   + "TEXT,"
                + KEY_NOTE      + "TEXT"
                + ")";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        // on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCHES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);

        // create new tables
        onCreate(sqLiteDatabase);
    }

    //managing events in SQL
    public void addEvent(Event in){
        ContentValues values = new ContentValues();
        values.put(KEY_EVENTKEY,    in.getEventKey());
        values.put(KEY_EVENTNAME,   in.getEventName());
        values.put(KEY_EVENTYEAR,   in.getEventYear());
        values.put(KEY_EVENTLOC,    in.getEventLocation());
        values.put(KEY_EVENTSTART,  in.getEventStart());
        values.put(KEY_EVENTEND,    in.getEventEnd());

        //insert the row
        db.insert(TABLE_EVENTS,null,values);

    }
    public Event getEvent(String key){

        Cursor cursor = db.query(TABLE_MATCHES, new String[] {KEY_EVENTKEY,KEY_EVENTNAME,KEY_EVENTYEAR,KEY_EVENTLOC,KEY_EVENTSTART,KEY_EVENTEND},
                                 KEY_MATCHKEY + "?=",new String[] {key},null,null,null,null);

        if(cursor!= null)
            cursor.moveToFirst();

        Event event = new Event(cursor.getString(0),cursor.getString(1),cursor.getString(3),cursor.getString(4),cursor.getString(5),Integer.parseInt(cursor.getString(2)));

        cursor.close();

        return event;
    }
    public List<Event> getAllEvents(){
        List<Event> eventList = new ArrayList<Event>();

        String selectQuery = "SELECT * FROM "+TABLE_EVENTS;

        Cursor cursor = db.rawQuery(selectQuery,null);

        //loop through rows
        if(cursor.moveToFirst()){
            do{
                Event event = new Event();
                event.setEventKey(cursor.getString(0));
                event.setEventName(cursor.getString(1));
                event.setEventYear(Integer.parseInt(cursor.getString(2)));
                event.setEventLocation(cursor.getString(3));
                event.setEventStart(cursor.getString(4));
                event.setEventEnd(cursor.getString(5));

                eventList.add(event);
            }while(cursor.moveToNext());
        }

        cursor.close();

        return eventList;
    }
    public int updateEvent(Event in){

        ContentValues values = new ContentValues();
        values.put(KEY_EVENTKEY,    in.getEventKey());
        values.put(KEY_EVENTNAME,   in.getEventName());
        values.put(KEY_EVENTYEAR,   in.getEventYear());
        values.put(KEY_EVENTLOC,    in.getEventLocation());
        values.put(KEY_EVENTSTART,  in.getEventStart());
        values.put(KEY_EVENTEND,    in.getEventEnd());

        return db.update(TABLE_EVENTS,values, KEY_EVENTKEY + "=?", new String[]{in.getEventKey()});
    }
    public void deleteEvent(Event in){
        db.delete(TABLE_EVENTS,KEY_EVENTKEY + "=?", new String[]{in.getEventKey()});
    }

    //managing Matches in SQL
    public void addMatch(Match in){

    }
    public Match getMatch(String key){
        return null;
    }
    public Match updateMatch(String key){
        return null;
    }

    //managing teams in SQL
    public void addTeam(Team in){

    }
    public Team getTeam(String key){
        return null;
    }
    public Team updateTeam(String key){
        return null;
    }

    //managing notes in SQL
    public void addNote(Note in){

    }
    public Note getNote(String eventKey, String matchKey, String teamKey){
        return null;
    }
    public Note updateNote(String eventKey, String matchKey, String teamKey){
        return null;
    }

}
