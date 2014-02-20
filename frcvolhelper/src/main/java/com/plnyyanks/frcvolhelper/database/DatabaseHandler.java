package com.plnyyanks.frcvolhelper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.plnyyanks.frcvolhelper.Constants;
import com.plnyyanks.frcvolhelper.datatypes.Event;
import com.plnyyanks.frcvolhelper.datatypes.Match;
import com.plnyyanks.frcvolhelper.datatypes.Note;
import com.plnyyanks.frcvolhelper.datatypes.Team;
import com.plnyyanks.frcvolhelper.json.JSONManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 2/19/14.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION       = 7;
    private static final String DATABASE_NAME       = "VOL_NOTES",

                                TABLE_EVENTS        = "events",
                                    KEY_EVENTKEY    = "eventKey",
                                    KEY_EVENTNAME   = "eventName",
                                    KEY_EVENTSHORT  = "eventShortName",
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
                                    KEY_TEAMNAME    = "teamName",
                                    KEY_TEAMSITE    = "teamWebsite",
                                    KEY_TEAMEVENTS  = "events",

                                TABLE_NOTES         = "notes",
                                    KEY_NOTEID      = "id",
                                    //KEY_EVENTKEY  = "eventKey",
                                    //KEY_MATCHKEY  = "matchKey",
                                    //KEY_TEAMKEY   = "teamKey",
                                    KEY_NOTE        = "note",
                                    KEY_NOTETIME    = "timestamp";

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
                + KEY_EVENTSHORT+ " TEXT,"
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
                + KEY_TEAMKEY    + " TEXT PRIMARY KEY,"
                + KEY_TEAMNUMBER + " INTEGER,"
                + KEY_TEAMNAME   + " TEXT,"
                + KEY_TEAMSITE   + " TEXT,"
                + KEY_TEAMEVENTS + " TEXT"
                + ")";
        db.execSQL(CREATE_TEAMS_TABLE);

        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + KEY_NOTEID    + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_EVENTKEY  + " TEXT,"
                + KEY_MATCHKEY  + " TEXT,"
                + KEY_TEAMKEY   + " TEXT,"
                + KEY_NOTE      + " TEXT,"
                + KEY_NOTETIME  + " TEXT"
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
    public long addEvent(Event in){
        //first, check if that event exists already and only insert if it doesn't
        if(!eventExists(in.getEventKey())){

            ContentValues values = new ContentValues();
            values.put(KEY_EVENTKEY,    in.getEventKey());
            values.put(KEY_EVENTNAME,   in.getEventName());
            values.put(KEY_EVENTSHORT,  in.getShortName());
            values.put(KEY_EVENTYEAR,   in.getEventYear());
            values.put(KEY_EVENTLOC,    in.getEventLocation());
            values.put(KEY_EVENTSTART,  in.getEventStart());
            values.put(KEY_EVENTEND,    in.getEventEnd());

            //insert the row
            return db.insert(TABLE_EVENTS,null,values);
        }else{
            return updateEvent(in);
        }
    }
    public Event getEvent(String key){

        Cursor cursor = db.query(TABLE_EVENTS, new String[] {KEY_EVENTKEY,KEY_EVENTNAME,KEY_EVENTSHORT,KEY_EVENTYEAR,KEY_EVENTLOC,KEY_EVENTSTART,KEY_EVENTEND},
                                 KEY_EVENTKEY + "=?",new String[] {key},null,null,null,null);
        if(cursor!= null && cursor.moveToFirst()){
            Event event = new Event(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(4),cursor.getString(5),cursor.getString(6),Integer.parseInt(cursor.getString(3)));
            cursor.close();
            return event;
        }else{
            return null;
        }

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
                event.setShortName(cursor.getString(2));
                event.setEventYear(Integer.parseInt(cursor.getString(3)));
                event.setEventLocation(cursor.getString(4));
                event.setEventStart(cursor.getString(5));
                event.setEventEnd(cursor.getString(6));

                eventList.add(event);
            }while(cursor.moveToNext());
        }

        cursor.close();

        return eventList;
    }
    public boolean eventExists(String key){
        Cursor cursor = db.query(TABLE_EVENTS,new String[]{KEY_EVENTKEY},KEY_EVENTKEY + "=?",new String[]{key},null,null,null,null);
        if(cursor.moveToFirst())
            return true;
        else
            return false;
    }
    public int updateEvent(Event in){

        ContentValues values = new ContentValues();
        values.put(KEY_EVENTKEY,    in.getEventKey());
        values.put(KEY_EVENTNAME,   in.getEventName());
        values.put(KEY_EVENTSHORT,  in.getShortName());
        values.put(KEY_EVENTYEAR,   in.getEventYear());
        values.put(KEY_EVENTLOC,    in.getEventLocation());
        values.put(KEY_EVENTSTART,  in.getEventStart());
        values.put(KEY_EVENTEND,    in.getEventEnd());

        return db.update(TABLE_EVENTS,values, KEY_EVENTKEY + " =?", new String[]{in.getEventKey()});
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
    public long addTeam(Team in){
        //first, check if that event exists already and only insert if it doesn't
        if(!teamExists(in.getTeamKey())){

            ContentValues values = new ContentValues();
            values.put(KEY_TEAMKEY,     in.getTeamKey());
            values.put(KEY_TEAMNUMBER,  in.getTeamNumber());
            values.put(KEY_TEAMNAME,    in.getTeamName());
            values.put(KEY_TEAMSITE,    in.getTeamWebsite());
            values.put(KEY_TEAMEVENTS,  JSONManager.flattenToJsonArray(in.getTeamEvents()));

            //insert the row
            return db.insert(TABLE_TEAMS,null,values);
        }else{
            return updateTeam(in);
        }
    }
    public Team getTeam(String key){
        Cursor cursor = db.query(TABLE_TEAMS, new String[] {KEY_TEAMKEY,KEY_TEAMNUMBER,KEY_TEAMNAME,KEY_TEAMSITE,KEY_TEAMEVENTS},
                KEY_TEAMKEY + "=?",new String[] {key},null,null,null,null);
        if(cursor!= null && cursor.moveToFirst()){
            Team team = new Team(cursor.getString(0),Integer.parseInt(cursor.getString(1)), cursor.getString(2), cursor.getString(3), JSONManager.getAsArrayList(cursor.getString(4)));
            cursor.close();
            return team;
        }else{
            return null;
        }

    }
    public List<Team> getAllTeams(){
        List<Team> teamList = new ArrayList<Team>();

        String selectQuery = "SELECT * FROM "+TABLE_TEAMS;

        Cursor cursor = db.rawQuery(selectQuery,null);

        //loop through rows
        if(cursor.moveToFirst()){
            do{
                Team team = new Team();
                team.setTeamKey(cursor.getString(0));
                team.setTeamNumber(Integer.parseInt(cursor.getString(1)));
                team.setTeamName(cursor.getString(2));
                team.setTeamName(cursor.getString(3));
                team.setTeamEvents(JSONManager.getAsArrayList(cursor.getString(4)));

                teamList.add(team);
            }while(cursor.moveToNext());
        }

        cursor.close();

        return teamList;
    }
    public ArrayList<Team> getAllTeamAtEvent(String eventKey){
        ArrayList<Team> teamList = new ArrayList<Team>();

        String selectQuery = "SELECT * FROM "+TABLE_TEAMS+" WHERE "+KEY_TEAMEVENTS + " LIKE '%"+eventKey+"%'";

        Cursor cursor = db.rawQuery(selectQuery,null);

        //loop through rows
        if(cursor.moveToFirst()){
            do{
                Log.d(Constants.LOG_TAG,"Team Events: "+cursor.getString(2));
                Team team = new Team();
                team.setTeamKey(cursor.getString(0));
                team.setTeamNumber(Integer.parseInt(cursor.getString(1)));
                team.setTeamName(cursor.getString(2));
                team.setTeamName(cursor.getString(3));
                team.setTeamEvents(JSONManager.getAsArrayList(cursor.getString(4)));

                teamList.add(team);
            }while(cursor.moveToNext());
        }

        cursor.close();

        return teamList;
    }
    public boolean teamExists(String key){
        Cursor cursor = db.query(TABLE_TEAMS,new String[]{KEY_TEAMKEY},KEY_TEAMKEY + "=?",new String[]{key},null,null,null,null);
        if(cursor.moveToFirst())
            return true;
        else
            return false;
    }
    public int updateTeam(Team in){

        Team currentVals = getTeam(in.getTeamKey());
        in.mergeEvents(currentVals.getTeamEvents());

        ContentValues values = new ContentValues();
        values.put(KEY_TEAMKEY,     in.getTeamKey());
        values.put(KEY_TEAMNUMBER,  in.getTeamNumber());
        values.put(KEY_TEAMNAME,    in.getTeamName());
        values.put(KEY_TEAMSITE,    in.getTeamWebsite());
        values.put(KEY_TEAMEVENTS,  in.getTeamEvents().toString());

        return db.update(TABLE_TEAMS,values, KEY_TEAMKEY + " =?", new String[]{in.getTeamKey()});
    }
    public void deleteTeam(Team in){
        db.delete(TABLE_TEAMS,KEY_TEAMKEY + "=?", new String[]{in.getTeamKey()});
    }

    //managing notes in SQL
    public long addNote(Note in){
        //first, check if that event exists already and only insert if it doesn't
        if(!noteExists(in.getId())){

            ContentValues values = new ContentValues();
            values.put(KEY_EVENTKEY,    in.getEventKey());
            values.put(KEY_MATCHKEY,    in.getMatchKey());
            values.put(KEY_TEAMKEY,     in.getTeamKey());
            values.put(KEY_NOTE, in.getNote());
            values.put(KEY_NOTETIME,    in.getTimestamp());

            //insert the row
            return db.insert(TABLE_NOTES,null,values);
        }else{
            return updateNote(in);
        }
    }
    public Note getNote(String eventKey, String matchKey, String teamKey){
        return null;
    }
    public ArrayList<Note> getAllNotes(){
        ArrayList<Note> noteList = new ArrayList<Note>();

        String selectQuery = "SELECT * FROM "+TABLE_NOTES;

        Cursor cursor = db.rawQuery(selectQuery,null);

        //loop through rows
        if(cursor.moveToFirst()){
            do{
                Note note = new Note();
                note.setId(Short.parseShort(cursor.getString(0)));
                note.setEventKey(cursor.getString(1));
                note.setMatchKey(cursor.getString(2));
                note.setTeamKey(cursor.getString(3));
                note.setNote(cursor.getString(4));
                note.setTimestamp(Long.parseLong(cursor.getString(5)));

                noteList.add(note);
            }while(cursor.moveToNext());
        }

        cursor.close();

        return noteList;
    }
    public ArrayList<Note> getAllNotes(String teamKey){
        ArrayList<Note> noteList = new ArrayList<Note>();

        Cursor cursor = db.query(TABLE_NOTES, new String[] {KEY_NOTEID,KEY_EVENTKEY,KEY_MATCHKEY,KEY_TEAMKEY,KEY_NOTE,KEY_NOTETIME},
                KEY_TEAMKEY + "=?",new String[] {teamKey},null,null,null,null);

        //loop through rows
        if(cursor.moveToFirst()){
            do{
                Note note = new Note();
                note.setId(Short.parseShort(cursor.getString(0)));
                note.setEventKey(cursor.getString(1));
                note.setMatchKey(cursor.getString(2));
                note.setTeamKey(cursor.getString(3));
                note.setNote(cursor.getString(4));
                note.setTimestamp(Long.parseLong(cursor.getString(5)));

                noteList.add(note);
            }while(cursor.moveToNext());
        }

        cursor.close();

        return noteList;
    }
    public ArrayList<Note> getAllNotes(String teamKey, String matchKey){
        ArrayList<Note> noteList = new ArrayList<Note>();

        Cursor cursor = db.query(TABLE_NOTES, new String[] {KEY_NOTEID,KEY_EVENTKEY,KEY_MATCHKEY,KEY_TEAMKEY,KEY_NOTE,KEY_NOTETIME},
                KEY_TEAMKEY + "=? AND "+KEY_MATCHKEY+"=?",new String[] {teamKey,matchKey},null,null,null,null);

        //loop through rows
        if(cursor.moveToFirst()){
            do{
                Note note = new Note();
                Log.d(Constants.LOG_TAG,"NOTE ID "+cursor.getString(0));
                note.setId(Short.parseShort(cursor.getString(0)));
                note.setEventKey(cursor.getString(1));
                note.setMatchKey(cursor.getString(2));
                note.setTeamKey(cursor.getString(3));
                note.setNote(cursor.getString(4));
                note.setTimestamp(Long.parseLong(cursor.getString(5)));

                noteList.add(note);
            }while(cursor.moveToNext());
        }

        cursor.close();

        return noteList;
    }
    public boolean noteExists(short id){
        Cursor cursor = db.query(TABLE_NOTES,new String[]{KEY_NOTEID},KEY_NOTEID + "=?",new String[]{Short.toString(id)},null,null,null,null);
        if(cursor.moveToFirst())
            return true;
        else
            return false;
    }
    public int updateNote(Note in){
        ContentValues values = new ContentValues();
        values.put(KEY_NOTEID,      in.getId());
        values.put(KEY_EVENTKEY,    in.getEventKey());
        values.put(KEY_MATCHKEY,    in.getMatchKey());
        values.put(KEY_TEAMKEY,     in.getTeamKey());
        values.put(KEY_NOTE,        in.getNote());
        values.put(KEY_NOTETIME,    in.getTimestamp());

        return db.update(TABLE_NOTES,values, KEY_NOTEID + " =?", new String[]{Short.toString(in.getId())});
    }
    public void deleteNote(Note in){
        db.delete(TABLE_NOTES,KEY_NOTEID + "=?", new String[]{Short.toString(in.getId())});
    }

}
