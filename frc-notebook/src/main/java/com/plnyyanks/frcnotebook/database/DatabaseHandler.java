package com.plnyyanks.frcnotebook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.datatypes.Note;
import com.plnyyanks.frcnotebook.datatypes.Team;
import com.plnyyanks.frcnotebook.json.JSONManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 16;
    private static final String DATABASE_NAME = "VOL_NOTES",

    TABLE_EVENTS            = "events",
            KEY_EVENTKEY    = "eventKey",
            KEY_EVENTNAME   = "eventName",
            KEY_EVENTSHORT  = "eventShortName",
            KEY_EVENTYEAR   = "eventYear",
            KEY_EVENTLOC    = "eventLocation",
            KEY_EVENTSTART  = "startDate",
            KEY_EVENTEND    = "endDate",

    TABLE_MATCHES           = "matches",
            KEY_MATCHTIME   = "time",
            KEY_MATCHKEY    = "matchKey",
            KEY_MATCHTYPE   = "type",
            KEY_MATCHNO     = "matchNumber",
            KEY_MATCHSET    = "matchSet",
            KEY_REDALLIANCE = "redAlliance",
            KEY_BLUEALLIANCE= "blueAlliance",
            KEY_BLUESCORE   = "blueScore",
            KEY_REDSCORE    = "redScore",

    TABLE_TEAMS             = "teams",
            KEY_TEAMKEY     = "teamKey",
            KEY_TEAMNUMBER  = "teamNumber",
            KEY_TEAMNAME    = "teamName",
            KEY_TEAMSITE    = "teamWebsite",
            KEY_TEAMEVENTS  = "events",

    TABLE_NOTES             = "notes",
            KEY_NOTEID      = "id",
            //KEY_EVENTKEY  = "eventKey",
            //KEY_MATCHKEY  = "matchKey",
            //KEY_TEAMKEY   = "teamKey",
            KEY_NOTE        = "note",
            KEY_NOTETIME    = "timestamp",
            KEY_NOTEPARENT  = "parentId",
            KEY_NOTEPICS    = "pictures",

    TABLE_PREDEF_NOTES      = "prefedined_note",
            KEY_DEF_NOTEID  = "id",
            KEY_DEF_NOTE    = "note";


    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + KEY_EVENTKEY + " TEXT PRIMARY KEY,"
                + KEY_EVENTNAME + " TEXT,"
                + KEY_EVENTSHORT + " TEXT,"
                + KEY_EVENTYEAR + " INTEGER,"
                + KEY_EVENTLOC + " TEXT,"
                + KEY_EVENTSTART + " TEXT,"
                + KEY_EVENTEND + " TEXT"
                + ")";
        db.execSQL(CREATE_EVENTS_TABLE);

        String CREATE_MATCHES_TABLE = "CREATE TABLE " + TABLE_MATCHES + "("
                + KEY_MATCHKEY + " TEXT PRIMARY KEY,"
                + KEY_MATCHTYPE + " TEXT,"
                + KEY_MATCHNO + " INTEGER,"
                + KEY_MATCHSET + " INTEGER,"
                + KEY_BLUEALLIANCE + " TEXT,"
                + KEY_REDALLIANCE + " TEXT,"
                + KEY_BLUESCORE + " INTEGER,"
                + KEY_REDSCORE + " INTEGER,"
                + KEY_MATCHTIME + " TEXT"
                + ")";
        db.execSQL(CREATE_MATCHES_TABLE);

        String CREATE_TEAMS_TABLE = "CREATE TABLE " + TABLE_TEAMS + "("
                + KEY_TEAMKEY + " TEXT PRIMARY KEY,"
                + KEY_TEAMNUMBER + " INTEGER,"
                + KEY_TEAMNAME + " TEXT,"
                + KEY_TEAMSITE + " TEXT,"
                + KEY_TEAMEVENTS + " TEXT"
                + ")";
        db.execSQL(CREATE_TEAMS_TABLE);

        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + KEY_NOTEID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_EVENTKEY + " TEXT NOT NULL,"
                + KEY_MATCHKEY + " TEXT NOT NULL,"
                + KEY_TEAMKEY + " TEXT NOT NULL,"
                + KEY_NOTE + " TEXT NOT NULL,"
                + KEY_NOTETIME + " TEXT,"
                + KEY_NOTEPARENT + " INTEGER,"
                + KEY_NOTEPICS + " TEXT"
                + ")";
        db.execSQL(CREATE_NOTES_TABLE);

        String CREATE_DEF_NOTE_TABLE = "CREATE TABLE "+TABLE_PREDEF_NOTES + "("
                + KEY_DEF_NOTEID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_DEF_NOTE + " TEXT"
                + ")";
        db.execSQL(CREATE_DEF_NOTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        if(oldVersion<12&&newVersion>=12){
            String upgradeQuery1 = "ALTER TABLE "+TABLE_NOTES+" ADD COLUMN "+KEY_NOTEPARENT+" INTEGER";
            String upgradeQuery2 = "ALTER TABLE "+TABLE_NOTES+" ADD COLUMN "+KEY_NOTEPICS+  " TEXT";
            sqLiteDatabase.execSQL(upgradeQuery1);
            sqLiteDatabase.execSQL(upgradeQuery2);

            String CREATE_DEF_NOTE_TABLE = "CREATE TABLE "+TABLE_PREDEF_NOTES + "("
                    + KEY_DEF_NOTEID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + KEY_DEF_NOTE + " TEXT"
                    + ")";
            sqLiteDatabase.execSQL(CREATE_DEF_NOTE_TABLE);

            return;
        }

        if(oldVersion<14 && newVersion>=14){
            String updateQuery = "ALTER TABLE "+TABLE_MATCHES+" ADD COLUMN "+KEY_MATCHTIME+" TEXT";
            sqLiteDatabase.execSQL(updateQuery);

            return;
        }

        if(oldVersion<16 && newVersion>=16 ){
            if(!columnExists(sqLiteDatabase,TABLE_MATCHES,KEY_MATCHTIME)) {
                Log.d(Constants.LOG_TAG,"Adding match time column");
                String updateQuery = "ALTER TABLE " + TABLE_MATCHES + " ADD COLUMN " + KEY_MATCHTIME + " TEXT";
                sqLiteDatabase.execSQL(updateQuery);
            }
            return;
        }

        // on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCHES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PREDEF_NOTES);

        // create new tables
        onCreate(sqLiteDatabase);

    }

    public void clearDatabase() {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREDEF_NOTES);

        // create new tables
        onCreate(db);
    }

    //managing events in SQL
    public long addEvent(Event in) {
        //first, check if that event exists already and only insert if it doesn't
        if (!eventExists(in.getEventKey())) {

            ContentValues values = new ContentValues();
            values.put(KEY_EVENTKEY, in.getEventKey());
            values.put(KEY_EVENTNAME, in.getEventName());
            values.put(KEY_EVENTSHORT, in.getShortName());
            values.put(KEY_EVENTYEAR, in.getEventYear());
            values.put(KEY_EVENTLOC, in.getEventLocation());
            values.put(KEY_EVENTSTART, in.getEventStart());
            values.put(KEY_EVENTEND, in.getEventEnd());

            //insert the row
            return db.insert(TABLE_EVENTS, null, values);
        } else {
            return updateEvent(in);
        }
    }
    public Event getEvent(String key) {

        Cursor cursor = db.query(TABLE_EVENTS, new String[]{KEY_EVENTKEY, KEY_EVENTNAME, KEY_EVENTSHORT, KEY_EVENTYEAR, KEY_EVENTLOC, KEY_EVENTSTART, KEY_EVENTEND},
                KEY_EVENTKEY + "=?", new String[]{key}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Event event = new Event(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(3));
            cursor.close();
            return event;
        } else {
            return null;
        }

    }
    public ArrayList<Event> getAllEvents() {
        ArrayList<Event> eventList = new ArrayList<Event>();

        String selectQuery = "SELECT * FROM " + TABLE_EVENTS;

        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through rows
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setEventKey(cursor.getString(0));
                event.setEventName(cursor.getString(1));
                event.setShortName(cursor.getString(2));
                event.setEventYear(cursor.getInt(3));
                event.setEventLocation(cursor.getString(4));
                event.setEventStart(cursor.getString(5));
                event.setEventEnd(cursor.getString(6));

                eventList.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return eventList;
    }
    public ArrayList<Event> getCurrentEvents(){
        ArrayList<Event> eventList = new ArrayList<Event>();

        String selectQuery = "SELECT * FROM " + TABLE_EVENTS;

        Cursor cursor = db.rawQuery(selectQuery, null);
        Date currentDate = new Date();
        //loop through rows
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setEventKey(cursor.getString(0));
                event.setEventName(cursor.getString(1));
                event.setShortName(cursor.getString(2));
                event.setEventYear(cursor.getInt(3));
                event.setEventLocation(cursor.getString(4));
                event.setEventStart(cursor.getString(5));
                event.setEventEnd(cursor.getString(6));
                if(currentDate.compareTo(event.getStartDate())>=0 && currentDate.compareTo(event.getEndDate())<=0)
                    eventList.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return eventList;
    }
    public ArrayList<String> getAllEventKeys(){
        ArrayList<String> eventList = new ArrayList<String>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through rows
        if (cursor.moveToFirst()) {
            do {
                eventList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return eventList;
    }
    public boolean eventExists(String key) {
        Cursor cursor = db.query(TABLE_EVENTS, new String[]{KEY_EVENTKEY}, KEY_EVENTKEY + "=?", new String[]{key}, null, null, null, null);
        return cursor.moveToFirst();
    }
    public int updateEvent(Event in) {

        ContentValues values = new ContentValues();
        values.put(KEY_EVENTKEY, in.getEventKey());
        values.put(KEY_EVENTNAME, in.getEventName());
        values.put(KEY_EVENTSHORT, in.getShortName());
        values.put(KEY_EVENTYEAR, in.getEventYear());
        values.put(KEY_EVENTLOC, in.getEventLocation());
        values.put(KEY_EVENTSTART, in.getEventStart());
        values.put(KEY_EVENTEND, in.getEventEnd());

        return db.update(TABLE_EVENTS, values, KEY_EVENTKEY + " =?", new String[]{in.getEventKey()});
    }
    public void deleteEvent(String eventKey) {
        db.delete(TABLE_EVENTS, KEY_EVENTKEY + "=?", new String[]{eventKey});
        deleteMatchesAtEvent(eventKey);
        deleteEventFromTeams(eventKey);
        deleteNotesFromEvent(eventKey);
    }
    public JsonArray exportEvents() {
        JsonArray output = new JsonArray();
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        //loop through rows
        if (cursor.moveToFirst()) {
            do {
                JsonObject event = new JsonObject();
                event.addProperty(KEY_EVENTKEY, cursor.getString(0));
                event.addProperty(KEY_EVENTNAME, cursor.getString(1));
                event.addProperty(KEY_EVENTSHORT, cursor.getString(2));
                event.addProperty(KEY_EVENTYEAR, cursor.getInt(3));
                event.addProperty(KEY_EVENTLOC, cursor.getString(4));
                event.addProperty(KEY_EVENTSTART, cursor.getString(5));
                event.addProperty(KEY_EVENTEND, cursor.getString(6));

                output.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return output;
    }
    public void importEvents(JsonArray events) {
        Iterator<JsonElement> iterator = events.iterator();
        JsonObject e;
        Event event;

        while (iterator.hasNext()) {
            e = iterator.next().getAsJsonObject();
            event = new Event();
            event.setEventKey(e.get(KEY_EVENTKEY).getAsString());
            event.setEventName(e.get(KEY_EVENTNAME).getAsString());
            event.setShortName(e.get(KEY_EVENTSHORT).getAsString());
            event.setEventYear(e.get(KEY_EVENTYEAR).getAsInt());
            event.setEventLocation(e.get(KEY_EVENTLOC).getAsString());
            event.setEventStart(e.get(KEY_EVENTSTART).getAsString());
            event.setEventEnd(e.get(KEY_EVENTEND).getAsString());

            addEvent(event);
        }
    }

    //managing Matches in SQL
    public long addMatch(Match in) {
        //first, check if that event exists already and only insert if it doesn't
        if (!matchExists(in.getMatchKey())) {

            ContentValues values = new ContentValues();
            values.put(KEY_MATCHKEY, in.getMatchKey());
            values.put(KEY_MATCHTYPE, Match.SHORT_TYPES.get(in.getMatchType()));
            values.put(KEY_MATCHNO, in.getMatchNumber());
            values.put(KEY_MATCHSET, in.getSetNumber());
            values.put(KEY_BLUEALLIANCE, in.getBlueAlliance());
            values.put(KEY_REDALLIANCE, in.getRedAlliance());
            values.put(KEY_BLUESCORE, in.getBlueScore());
            values.put(KEY_REDSCORE, in.getRedScore());
            values.put(KEY_MATCHTIME,in.getMatchTime());

            //insert the row
            return db.insert(TABLE_MATCHES, null, values);
        } else {
            return updateMatch(in);
        }
    }
    public Match getMatch(String key) {

        Cursor cursor = db.query(TABLE_MATCHES, new String[]{KEY_MATCHKEY, KEY_MATCHTYPE, KEY_MATCHNO, KEY_MATCHSET, KEY_BLUEALLIANCE, KEY_REDALLIANCE, KEY_BLUESCORE, KEY_REDSCORE,KEY_MATCHTIME},
                KEY_MATCHKEY + "=?", new String[]{key}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            //(String matchKey, String matchType, int matchNumber, String blueAlliance, String redAlliance, int blueScore, int redScore)
            Match match = new Match();
            match.setMatchKey(cursor.getString(0));
            match.setMatchType(cursor.getString(1));
            match.setMatchNumber(cursor.getInt(2));
            match.setSetNumber(cursor.getInt(3));
            match.setBlueAlliance(cursor.getString(4));
            match.setRedAlliance(cursor.getString(5));
            match.setBlueScore(cursor.getInt(6));
            match.setRedScore(cursor.getInt(7));
            match.setMatchTime(cursor.getString(8));

            cursor.close();
            return match;
        } else {
            return null;
        }

    }
    public ArrayList<Match> getAllMatchesForTeam(String teamKey){
        ArrayList<Match> matchList = new ArrayList<Match>();
        String selectQuery = "SELECT * FROM " + TABLE_MATCHES + (!teamKey.equals("all")?(" WHERE " + KEY_REDALLIANCE + " LIKE '%" + teamKey + "%' OR "+KEY_BLUEALLIANCE + " LIKE '%" + teamKey + "%'"):"");
        Cursor cursor = db.rawQuery(selectQuery, null);
        //loop through rows
        ////(String matchKey, String matchType, int matchNumber, int[] blueAlliance, int[] redAlliance, int blueScore, int redScore)
        if (cursor.moveToFirst()) {
            do {
                Match match = new Match();
                match.setMatchKey(cursor.getString(0));
                match.setMatchType(cursor.getString(1));
                match.setMatchNumber(cursor.getInt(2));
                match.setSetNumber(cursor.getInt(3));
                match.setBlueAlliance(cursor.getString(4));
                match.setRedAlliance(cursor.getString(5));
                match.setBlueScore(cursor.getInt(6));
                match.setRedScore(cursor.getInt(7));
                match.setMatchTime(cursor.getString(8));
                matchList.add(match);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return matchList;
    }
    public ArrayList<Match> getAllMatches(String eventKey) {
        ArrayList<Match> matchList = new ArrayList<Match>();

        String selectQuery = "SELECT * FROM " + TABLE_MATCHES + (!eventKey.equals("all")?(" WHERE " + KEY_MATCHKEY + " LIKE '%" + eventKey + "%'"):"");

        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through rows
        ////(String matchKey, String matchType, int matchNumber, int[] blueAlliance, int[] redAlliance, int blueScore, int redScore)
        if (cursor.moveToFirst()) {
            do {
                Match match = new Match();
                match.setMatchKey(cursor.getString(0));
                match.setMatchType(cursor.getString(1));
                match.setMatchNumber(cursor.getInt(2));
                match.setSetNumber(cursor.getInt(3));
                match.setBlueAlliance(cursor.getString(4));
                match.setRedAlliance(cursor.getString(5));
                match.setBlueScore(cursor.getInt(6));
                match.setRedScore(cursor.getInt(7));
                match.setMatchTime(cursor.getString(8));
                matchList.add(match);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return matchList;
    }
    public ArrayList<Match> getAllMatches(String eventKey, String teamKey) {
        ArrayList<Match> matchList = new ArrayList<Match>();

        String selectQuery = "SELECT * FROM " + TABLE_MATCHES + " WHERE " + KEY_MATCHKEY + " LIKE '%" + eventKey + "%'"+(!teamKey.equals("all")?(" AND (" + KEY_REDALLIANCE + " LIKE '%" + teamKey + "%' OR "+KEY_BLUEALLIANCE + " LIKE '%" + teamKey + "%')"):"");

        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through rows
        ////(String matchKey, String matchType, int matchNumber, int[] blueAlliance, int[] redAlliance, int blueScore, int redScore)
        if (cursor.moveToFirst()) {
            do {
                Match match = new Match();
                match.setMatchKey(cursor.getString(0));
                match.setMatchType(cursor.getString(1));
                match.setMatchNumber(cursor.getInt(2));
                match.setSetNumber(cursor.getInt(3));
                match.setBlueAlliance(cursor.getString(4));
                match.setRedAlliance(cursor.getString(5));
                match.setBlueScore(cursor.getInt(6));
                match.setRedScore(cursor.getInt(7));
                match.setMatchTime(cursor.getString(8));
                matchList.add(match);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return matchList;
    }
    public boolean matchExists(String key) {
        Log.d(Constants.LOG_TAG,"Testing key: "+key);
        if(key==null) return false;
        Cursor cursor = db.query(TABLE_MATCHES, new String[]{KEY_MATCHKEY}, KEY_MATCHKEY + "=?", new String[]{key}, null, null, null, null);
        return cursor.moveToFirst();

    }
    public int updateMatch(Match in) {

        ContentValues values = new ContentValues();
        values.put(KEY_MATCHKEY, in.getMatchKey());
        values.put(KEY_MATCHTYPE, Match.SHORT_TYPES.get(in.getMatchType()));
        values.put(KEY_MATCHNO, in.getMatchNumber());
        values.put(KEY_MATCHSET, in.getSetNumber());
        values.put(KEY_BLUEALLIANCE, in.getBlueAlliance());
        values.put(KEY_REDALLIANCE, in.getRedAlliance());
        values.put(KEY_BLUESCORE, in.getBlueScore());
        values.put(KEY_REDSCORE, in.getRedScore());
        values.put(KEY_MATCHTIME,in.getMatchTime());

        return db.update(TABLE_MATCHES, values, KEY_MATCHKEY + " =?", new String[]{in.getMatchKey()});
    }
    public void deleteMatchesAtEvent(String eventKey) {
        db.delete(TABLE_MATCHES, KEY_MATCHKEY + " LIKE '%" + eventKey + "%'", null);
    }
    public JsonArray exportMatches() {
        JsonArray output = new JsonArray();
        String selectQuery = "SELECT * FROM " + TABLE_MATCHES;
        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through rows
        ////(String matchKey, String matchType, int matchNumber, int[] blueAlliance, int[] redAlliance, int blueScore, int redScore)
        if (cursor.moveToFirst()) {
            do {
                JsonObject match = new JsonObject();
                match.addProperty(KEY_MATCHKEY, cursor.getString(0));
                match.addProperty(KEY_MATCHTYPE, cursor.getString(1));
                match.addProperty(KEY_MATCHNO, cursor.getInt(2));
                match.addProperty(KEY_MATCHSET, cursor.getInt(3));
                match.add(KEY_REDALLIANCE, JSONManager.getasJsonArray(cursor.getString(5)));
                match.add(KEY_BLUEALLIANCE, JSONManager.getasJsonArray(cursor.getString(4)));
                match.addProperty(KEY_BLUESCORE, cursor.getInt(6));
                match.addProperty(KEY_REDSCORE, cursor.getInt(7));
                match.addProperty(KEY_MATCHTIME,cursor.getString(8));

                output.add(match);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return output;
    }
    public void importMatches(JsonArray matches) {
        Iterator<JsonElement> iterator = matches.iterator();
        JsonObject m;
        Match match;

        while (iterator.hasNext()) {
            m = iterator.next().getAsJsonObject();
            match = new Match();
            match.setMatchKey(m.get(KEY_MATCHKEY).getAsString());
            match.setMatchType(m.get(KEY_MATCHTYPE).getAsString());
            match.setMatchNumber(m.get(KEY_MATCHNO).getAsInt());
            match.setSetNumber(m.get(KEY_MATCHSET).getAsInt());
            match.setRedAlliance(m.get(KEY_REDALLIANCE).toString());
            match.setBlueAlliance(m.get(KEY_BLUEALLIANCE).toString());
            match.setRedScore(m.get(KEY_REDSCORE).getAsInt());
            match.setBlueScore(m.get(KEY_BLUESCORE).getAsInt());
            try {
                match.setMatchTime(m.get(KEY_MATCHTIME).getAsString());
            }catch(Exception e){
                match.setMatchTime("");
            }

            addMatch(match);
        }
    }

    //managing teams in SQL
    public long addTeam(Team in) {
        //first, check if that event exists already and only insert if it doesn't
        if (!teamExists(in.getTeamKey())) {

            ContentValues values = new ContentValues();
            values.put(KEY_TEAMKEY, in.getTeamKey());
            values.put(KEY_TEAMNUMBER, in.getTeamNumber());
            values.put(KEY_TEAMNAME, in.getTeamName());
            values.put(KEY_TEAMSITE, in.getTeamWebsite());
            values.put(KEY_TEAMEVENTS, JSONManager.flattenToJsonArray(in.getTeamEvents()));

            //insert the row
            return db.insert(TABLE_TEAMS, null, values);
        } else {
            return updateTeam(in);
        }
    }
    public Team getTeam(String key) {
        Cursor cursor = db.query(TABLE_TEAMS, new String[]{KEY_TEAMKEY, KEY_TEAMNUMBER, KEY_TEAMNAME, KEY_TEAMSITE, KEY_TEAMEVENTS},
                KEY_TEAMKEY + "=?", new String[]{key}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Team team = new Team(cursor.getString(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), JSONManager.getAsStringArrayList(cursor.getString(4)));
            cursor.close();
            return team;
        } else {
            return null;
        }

    }
    public ArrayList<Team> getAllTeams() {
        ArrayList<Team> teamList = new ArrayList<Team>();

        String selectQuery = "SELECT * FROM " + TABLE_TEAMS;

        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through rows
        if (cursor.moveToFirst()) {
            do {
                Team team = new Team();
                team.setTeamKey(cursor.getString(0));
                team.setTeamNumber(cursor.getInt(1));
                team.setTeamName(cursor.getString(2));
                team.setTeamName(cursor.getString(3));
                team.setTeamEvents(JSONManager.getAsStringArrayList(cursor.getString(4)));

                teamList.add(team);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return teamList;
    }
    public ArrayList<Team> getAllTeamAtEvent(String eventKey) {
        if(eventKey.equals("all")) return getAllTeams();

        ArrayList<Team> teamList = new ArrayList<Team>();

        String selectQuery = "SELECT * FROM " + TABLE_TEAMS + " WHERE " + KEY_TEAMEVENTS + " LIKE '%" + eventKey + "%'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through rows
        if (cursor.moveToFirst()) {
            do {
                Team team = new Team();
                team.setTeamKey(cursor.getString(0));
                team.setTeamNumber(cursor.getInt(1));
                team.setTeamName(cursor.getString(2));
                team.setTeamName(cursor.getString(3));
                team.setTeamEvents(JSONManager.getAsStringArrayList(cursor.getString(4)));

                teamList.add(team);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return teamList;
    }
    public boolean teamExists(String key) {
        Cursor cursor = db.query(TABLE_TEAMS, new String[]{KEY_TEAMKEY}, KEY_TEAMKEY + "=?", new String[]{key}, null, null, null, null);
        return cursor.moveToFirst();
    }
    public int updateTeam(Team in) {
        return updateTeam(in, true);
    }
    public int updateTeam(Team in, boolean mergeEvents) {

        Team currentVals = getTeam(in.getTeamKey());
        if (mergeEvents)
            in.mergeEvents(currentVals.getTeamEvents());

        ContentValues values = new ContentValues();
        values.put(KEY_TEAMKEY, in.getTeamKey());
        values.put(KEY_TEAMNUMBER, in.getTeamNumber());
        values.put(KEY_TEAMNAME, in.getTeamName());
        values.put(KEY_TEAMSITE, in.getTeamWebsite());
        values.put(KEY_TEAMEVENTS, in.getTeamEvents().toString());

        return db.update(TABLE_TEAMS, values, KEY_TEAMKEY + " =?", new String[]{in.getTeamKey()});
    }
    public void deleteTeam(Team in) {
        db.delete(TABLE_TEAMS, KEY_TEAMKEY + "=?", new String[]{in.getTeamKey()});
    }
    public void deleteEventFromTeams(String eventKey) {
        String selectQuery = "SELECT * FROM " + TABLE_TEAMS + " WHERE " + KEY_TEAMEVENTS + " LIKE '%" + eventKey + "%'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through rows
        if (cursor.moveToFirst()) {
            do {
                Team team = new Team();
                team.setTeamKey(cursor.getString(0));
                team.setTeamNumber(cursor.getInt(1));
                team.setTeamName(cursor.getString(2));
                team.setTeamName(cursor.getString(3));
                team.setTeamEvents(JSONManager.getAsStringArrayList(cursor.getString(4)));

                team.removeEvent(eventKey);
                updateTeam(team, false);
            } while (cursor.moveToNext());
        }

        cursor.close();
    }
    public JsonArray exportTeams() {
        JsonArray output = new JsonArray();
        String selectQuery = "SELECT * FROM " + TABLE_TEAMS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through rows
        if (cursor.moveToFirst()) {
            do {
                JsonObject team = new JsonObject();
                team.addProperty(KEY_TEAMKEY, cursor.getString(0));
                team.addProperty(KEY_TEAMNUMBER, cursor.getInt(1));
                team.addProperty(KEY_TEAMNAME, cursor.getString(2));
                team.addProperty(KEY_TEAMNAME, cursor.getString(3));
                team.add(KEY_TEAMEVENTS, JSONManager.getasJsonArray(cursor.getString(4)));

                output.add(team);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return output;
    }
    public void importTeams(JsonArray teams) {
        Iterator<JsonElement> iterator = teams.iterator();
        JsonObject t;
        Team team;

        while (iterator.hasNext()) {
            t = iterator.next().getAsJsonObject();
            team = new Team();

            team.setTeamKey(t.get(KEY_TEAMKEY).getAsString());
            try {
                team.setTeamName(t.get(KEY_TEAMNAME).getAsString());
            } catch (Exception e) {
                team.setTeamName("");
            }
            team.setTeamNumber(t.get(KEY_TEAMNUMBER).getAsInt());
            try {
                team.setTeamWebsite(t.get(KEY_TEAMSITE).getAsString());
            } catch (Exception e) {
                team.setTeamWebsite("");
            }

            team.setTeamEvents(JSONManager.getAsStringArrayList(t.get(KEY_TEAMEVENTS).getAsJsonArray().toString()));

            addTeam(team);
        }
    }

    //managing notes in SQL
    public short addNote(Note in) {
        short existCheck = noteExists(in);
        if (existCheck != -1) {
            Log.d(Constants.LOG_TAG,"Note already exists");
            return existCheck;
        }
        Log.d(Constants.LOG_TAG, "ADDING NOTE FOR: " + in.getTeamKey() + " " + in.getEventKey() + " " + in.getMatchKey());
        ContentValues values = new ContentValues();
        values.put(KEY_EVENTKEY, in.getEventKey());
        values.put(KEY_MATCHKEY, in.getMatchKey());
        values.put(KEY_TEAMKEY, in.getTeamKey());
        values.put(KEY_NOTE, in.getNote());
        values.put(KEY_NOTETIME, in.getTimestamp());
        values.put(KEY_NOTEPARENT, in.getParent());
        values.put(KEY_NOTEPICS, in.getPictures());

        //insert the row
        if (db.insert(TABLE_NOTES, null, values) == -1) {
            //error, return -1
            return -1;
        } else {
            //else, return the note's ID
            Cursor cursor = db.rawQuery("SELECT MAX(" + KEY_NOTEID + ") FROM " + TABLE_NOTES, null);
            if (cursor.moveToFirst()) {
                Log.d(Constants.LOG_TAG, "LARGEST ID FETCHED: " + cursor.getShort(0));
                return cursor.getShort(0);
            } else {
                Log.d(Constants.LOG_TAG, "NO RECORD FOUND");
                return -1;
            }
        }
    }
    public Note getNote(short id) {
        Cursor cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTEID, KEY_EVENTKEY, KEY_MATCHKEY, KEY_TEAMKEY, KEY_NOTE, KEY_NOTETIME,KEY_NOTEPARENT,KEY_NOTEPICS},
                KEY_NOTEID + "=? ", new String[]{Short.toString(id)}, null, null, null, null);

        //loop through rows
        Note note = new Note();
        if (cursor.moveToFirst()) {

            note.setId(cursor.getShort(0));
            note.setEventKey(cursor.getString(1));
            note.setMatchKey(cursor.getString(2));
            note.setTeamKey(cursor.getString(3));
            note.setNote(cursor.getString(4));
            note.setTimestamp(cursor.getLong(5));
            note.setParent(cursor.getShort(6));
            note.setPictures(cursor.getString(7));

        }
        cursor.close();
        return note;
    }
    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> noteList = new ArrayList<Note>();

        String selectQuery = "SELECT * FROM " + TABLE_NOTES;

        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through rows
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getShort(0));
                note.setEventKey(cursor.getString(1));
                note.setMatchKey(cursor.getString(2));
                note.setTeamKey(cursor.getString(3));
                note.setNote(cursor.getString(4));
                note.setTimestamp(cursor.getLong(5));
                note.setParent(cursor.getShort(6));
                note.setPictures(cursor.getString(7));

                noteList.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return noteList;
    }
    public ArrayList<Note> getAllNotes(String teamKey) {
        ArrayList<Note> noteList = new ArrayList<Note>();

        Cursor cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTEID, KEY_EVENTKEY, KEY_MATCHKEY, KEY_TEAMKEY, KEY_NOTE, KEY_NOTETIME,KEY_NOTEPARENT,KEY_NOTEPICS},
                KEY_TEAMKEY + "=?", new String[]{teamKey}, null, null, null, null);

        //loop through rows
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getShort(0));
                note.setEventKey(cursor.getString(1));
                note.setMatchKey(cursor.getString(2));
                note.setTeamKey(cursor.getString(3));
                note.setNote(cursor.getString(4));
                note.setTimestamp(cursor.getLong(5));
                note.setParent(cursor.getShort(6));
                note.setPictures(cursor.getString(7));

                noteList.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return noteList;
    }
    public ArrayList<Note> getAllNotes(String teamKey, String eventKey) {
        ArrayList<Note> noteList = new ArrayList<Note>();

        Cursor cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTEID, KEY_EVENTKEY, KEY_MATCHKEY, KEY_TEAMKEY, KEY_NOTE, KEY_NOTETIME,KEY_NOTEPARENT,KEY_NOTEPICS},
                KEY_TEAMKEY + "=? AND " + KEY_EVENTKEY + "=?", new String[]{teamKey, eventKey}, null, null, null, null);

        //loop through rows
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getShort(0));
                note.setEventKey(cursor.getString(1));
                note.setMatchKey(cursor.getString(2));
                note.setTeamKey(cursor.getString(3));
                note.setNote(cursor.getString(4));
                note.setTimestamp(cursor.getLong(5));
                note.setParent(cursor.getShort(6));
                note.setPictures(cursor.getString(7));

                noteList.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        Log.d(Constants.LOG_TAG, " FOUND " + noteList.size() + " NOTES");
        return noteList;
    }
    public ArrayList<Note> getAllNotes(String teamKey, String eventKey, String matchKey) {
        ArrayList<Note> noteList = new ArrayList<Note>();

        Cursor cursor;
        if (!eventKey.equals("all") && !teamKey.equals("")) {
            if(teamKey.equals("all")){
                //looking for notes for ALL THE TEAMS
                cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTEID, KEY_EVENTKEY, KEY_MATCHKEY, KEY_TEAMKEY, KEY_NOTE, KEY_NOTETIME,KEY_NOTEPARENT,KEY_NOTEPICS},
                        KEY_EVENTKEY + "=? AND " + KEY_MATCHKEY + "=?", new String[]{eventKey, matchKey}, null, null, null, null);
            }else{
                //regular event. Proceed normally
                cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTEID, KEY_EVENTKEY, KEY_MATCHKEY, KEY_TEAMKEY, KEY_NOTE, KEY_NOTETIME,KEY_NOTEPARENT,KEY_NOTEPICS},
                        KEY_TEAMKEY + "=? AND " + KEY_EVENTKEY + "=? AND " + KEY_MATCHKEY + "=?", new String[]{teamKey, eventKey, matchKey}, null, null, null, null);
            }
        } else if (eventKey.equals("all")) {
            //looking for all events worth of notes
            if(teamKey.equals("all")){
                cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTEID, KEY_EVENTKEY, KEY_MATCHKEY, KEY_TEAMKEY, KEY_NOTE, KEY_NOTETIME,KEY_NOTEPARENT,KEY_NOTEPICS},
                        KEY_EVENTKEY+"=? AND "+ KEY_MATCHKEY + "=?", new String[]{eventKey,matchKey}, null, null, null, null);
            }else{
                cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTEID, KEY_EVENTKEY, KEY_MATCHKEY, KEY_TEAMKEY, KEY_NOTE, KEY_NOTETIME,KEY_NOTEPARENT,KEY_NOTEPICS},
                        KEY_TEAMKEY + "=? AND " +KEY_EVENTKEY+"=? AND "+ KEY_MATCHKEY + "=?", new String[]{teamKey,eventKey,matchKey}, null, null, null, null);
            }
        } else if (teamKey.equals("")) {
            //looking for all notes on a particular match
            cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTEID, KEY_EVENTKEY, KEY_MATCHKEY, KEY_TEAMKEY, KEY_NOTE, KEY_NOTETIME,KEY_NOTEPARENT,KEY_NOTEPICS},
                    KEY_EVENTKEY + "=? AND " + KEY_MATCHKEY + "=?", new String[]{eventKey, matchKey}, null, null, null, null);
        } else {
            if(teamKey.equals("all")){
                cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTEID, KEY_EVENTKEY, KEY_MATCHKEY, KEY_TEAMKEY, KEY_NOTE, KEY_NOTETIME,KEY_NOTEPARENT,KEY_NOTEPICS},
                        KEY_EVENTKEY + "=? AND " + KEY_MATCHKEY + "=?", new String[]{eventKey, matchKey}, null, null, null, null);
            }else{
                cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTEID, KEY_EVENTKEY, KEY_MATCHKEY, KEY_TEAMKEY, KEY_NOTE, KEY_NOTETIME,KEY_NOTEPARENT,KEY_NOTEPICS},
                        KEY_TEAMKEY + "=? AND " + KEY_EVENTKEY + "=? AND " + KEY_MATCHKEY + "=?", new String[]{teamKey, eventKey, matchKey}, null, null, null, null);
            }
        }
        //loop through rows
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getShort(0));
                note.setEventKey(cursor.getString(1));
                note.setMatchKey(cursor.getString(2));
                note.setTeamKey(cursor.getString(3));
                note.setNote(cursor.getString(4));
                note.setTimestamp(cursor.getLong(5));
                note.setParent(cursor.getShort(6));
                note.setPictures(cursor.getString(7));

                noteList.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        Log.d(Constants.LOG_TAG, " FOUND " + noteList.size() + " NOTES");
        return noteList;
    }
    public ArrayList<Note> getAllMatchNotes(String teamKey, String eventKey) {
        ArrayList<Note> noteList = new ArrayList<Note>();

        Cursor cursor;
        if (!eventKey.equals("all")) {
            if(teamKey.equals("all")){
                cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTEID, KEY_EVENTKEY, KEY_MATCHKEY, KEY_TEAMKEY, KEY_NOTE, KEY_NOTETIME,KEY_NOTEPARENT,KEY_NOTEPICS},
                       KEY_EVENTKEY + "=? AND " + KEY_MATCHKEY + "!=?", new String[]{eventKey, "all"}, null, null, null, null);
            }else{
                cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTEID, KEY_EVENTKEY, KEY_MATCHKEY, KEY_TEAMKEY, KEY_NOTE, KEY_NOTETIME,KEY_NOTEPARENT,KEY_NOTEPICS},
                        KEY_TEAMKEY + "=? AND " + KEY_EVENTKEY + "=? AND " + KEY_MATCHKEY + "!=?", new String[]{teamKey, eventKey, "all"}, null, null, null, null);
            }
        } else {
            if(teamKey.equals("all")){
                cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTEID, KEY_EVENTKEY, KEY_MATCHKEY, KEY_TEAMKEY, KEY_NOTE, KEY_NOTETIME,KEY_NOTEPARENT,KEY_NOTEPICS},
                        KEY_MATCHKEY + "!=?", new String[]{"all"}, null, null, null, null);
            }else{
                cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTEID, KEY_EVENTKEY, KEY_MATCHKEY, KEY_TEAMKEY, KEY_NOTE, KEY_NOTETIME,KEY_NOTEPARENT,KEY_NOTEPICS},
                        KEY_TEAMKEY + "=? AND " + KEY_MATCHKEY + "!=?", new String[]{teamKey, "all"}, null, null, null, null);
            }
        }
        //loop through rows
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getShort(0));
                note.setEventKey(cursor.getString(1));
                note.setMatchKey(cursor.getString(2));
                note.setTeamKey(cursor.getString(3));
                note.setNote(cursor.getString(4));
                note.setTimestamp(cursor.getLong(5));

                noteList.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        Log.d(Constants.LOG_TAG, " FOUND " + noteList.size() + " NOTES");
        return noteList;
    }
    public short noteExists(Note note) {
        Cursor cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTE}, KEY_MATCHKEY + "=? AND " + KEY_EVENTKEY + "=? AND " + KEY_TEAMKEY + "=? AND " + KEY_NOTE + "=? AND "+KEY_NOTEPARENT+"=?",
                new String[]{note.getMatchKey(), note.getEventKey(), note.getTeamKey(), note.getNote(),Short.toString(note.getParent())}, null, null, null, null);
        if (cursor.moveToFirst())
            return cursor.getShort(0);
        else
            return -1;
    }
    public boolean noteExists(short id) {
        Cursor cursor = db.query(TABLE_NOTES, new String[]{KEY_NOTEID}, KEY_NOTEID + "=?", new String[]{Short.toString(id)}, null, null, null, null);
        return cursor.moveToFirst();
    }
    public int updateNote(Note in) {
        ContentValues values = new ContentValues();
        values.put(KEY_NOTEID, in.getId());
        values.put(KEY_EVENTKEY, in.getEventKey());
        values.put(KEY_MATCHKEY, in.getMatchKey());
        values.put(KEY_TEAMKEY, in.getTeamKey());
        values.put(KEY_NOTE, in.getNote());
        values.put(KEY_NOTETIME, in.getTimestamp());
        values.put(KEY_NOTEPARENT,in.getParent());
        values.put(KEY_NOTEPICS,in.getPictures());

        return db.update(TABLE_NOTES, values, KEY_NOTEID + " =?", new String[]{Short.toString(in.getId())});
    }
    public void deleteNote(Note in) {
        deleteNote(Short.toString(in.getId()));
    }
    public void deleteNote(String id) {
        db.delete(TABLE_NOTES, KEY_NOTEID + "=?", new String[]{id});
    }
    public void deleteNotesFromEvent(String eventKey) {
        db.delete(TABLE_NOTES, KEY_EVENTKEY + "=?", new String[]{eventKey});
    }
    public void deleteNotesFromParent(short parentId){
        db.delete(TABLE_NOTES, KEY_NOTEPARENT + "=?", new String[]{Short.toString(parentId)});
    }
    public JsonArray exportNotes() {
        JsonArray output = new JsonArray();
        String selectQuery = "SELECT * FROM " + TABLE_NOTES;
        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through rows
        if (cursor.moveToFirst()) {
            do {
                JsonObject note = new JsonObject();
                note.addProperty(KEY_NOTEID, cursor.getShort(0));
                note.addProperty(KEY_EVENTKEY, cursor.getString(1));
                note.addProperty(KEY_MATCHKEY, cursor.getString(2));
                note.addProperty(KEY_TEAMKEY, cursor.getString(3));
                note.addProperty(KEY_NOTE, cursor.getString(4));
                note.addProperty(KEY_NOTETIME, cursor.getLong(5));
                try {
                    note.addProperty(KEY_NOTEPARENT, cursor.getShort(6));
                    note.addProperty(KEY_NOTEPICS, cursor.getString(7));
                }catch(Exception e){
                    note.addProperty(KEY_NOTEPARENT,-1);
                    note.addProperty(KEY_NOTEPICS,"");
                }

                output.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return output;
    }
    public void importNotes(JsonArray notes) {
        Iterator<JsonElement> iterator = notes.iterator();
        JsonObject n;
        Note note;

        while (iterator.hasNext()) {
            n = iterator.next().getAsJsonObject();
            note = new Note();

            note.setId(n.get(KEY_NOTEID).getAsShort());
            note.setEventKey(n.get(KEY_EVENTKEY).getAsString());
            note.setMatchKey(n.get(KEY_MATCHKEY).getAsString());
            note.setTeamKey(n.get(KEY_TEAMKEY).getAsString());
            note.setNote(n.get(KEY_NOTE).getAsString());
            note.setTimestamp(n.get(KEY_NOTETIME).getAsLong());
            try {
                note.setParent(n.get(KEY_NOTEPARENT).getAsShort());
                note.setPictures(n.get(KEY_NOTEPICS).getAsString());
            }catch(Exception e){
                note.setParent((short)-1);
                note.setPictures("");
            }

            addNote(note);
        }
    }

    public short addDefNote(String n){
        short existCheck = defNoteExists(n);
        if (existCheck != -1) {
            return existCheck;
        }
        ContentValues values = new ContentValues();
        values.put(KEY_DEF_NOTE, n);

        //insert the row
        if (db.insert(TABLE_PREDEF_NOTES, null, values) == -1) {
            //error, return -1
            return -1;
        } else {
            //else, return the note's ID
            Cursor cursor = db.rawQuery("SELECT MAX(" + KEY_DEF_NOTEID + ") FROM " + TABLE_PREDEF_NOTES, null);
            if (cursor.moveToFirst()) {
                return cursor.getShort(0);
            } else {
                return -1;
            }
        }
    }
    public String getDefNote(short id){
        Cursor cursor = db.query(TABLE_PREDEF_NOTES, new String[]{KEY_DEF_NOTE},
                KEY_DEF_NOTEID + "=? ", new String[]{Short.toString(id)}, null, null, null, null);

        if (cursor.moveToFirst()) {
            String out = cursor.getString(0);
            cursor.close();
            return out;
        }
        cursor.close();
        return "";
    }
    public HashMap<Short,String> getAllDefNotes(){
        HashMap<Short,String> noteList = new HashMap<Short, String>();
        String selectQuery = "SELECT * FROM " + TABLE_PREDEF_NOTES;
        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through rows
        if (cursor.moveToFirst()) {
            do {
               noteList.put(cursor.getShort(0), cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return noteList;
    }
    public short defNoteExists(String n){
        Cursor cursor = db.query(TABLE_PREDEF_NOTES, new String[]{KEY_DEF_NOTEID}, KEY_DEF_NOTE + "=?",
                new String[]{n}, null, null, null, null);
        if (cursor.moveToFirst())
            return cursor.getShort(0);
        else
            return -1;
    }
    public int updateDefNote(short id,String n){
        ContentValues values = new ContentValues();
        values.put(KEY_DEF_NOTE, n);

        return db.update(TABLE_PREDEF_NOTES, values, KEY_DEF_NOTEID+ " =?", new String[]{Short.toString(id)});
    }
    public void deleteDefNote(short id){
        db.delete(TABLE_PREDEF_NOTES, KEY_DEF_NOTEID + "=?", new String[]{Short.toString(id)});
        deleteNotesFromParent(id);
    }
    public JsonArray exportDefNotes(){
        JsonArray output = new JsonArray();
        String selectQuery = "SELECT * FROM " + TABLE_PREDEF_NOTES;
        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through rows
        if (cursor.moveToFirst()) {
            do {
                JsonObject note = new JsonObject();
                note.addProperty(KEY_DEF_NOTEID, cursor.getShort(0));
                note.addProperty(KEY_DEF_NOTE,cursor.getString(1));

                output.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return output;
    }
    public void importDefNotes(JsonArray notes){
        Iterator<JsonElement> iterator = notes.iterator();
        JsonObject n;

        while (iterator.hasNext()) {
            n = iterator.next().getAsJsonObject();

            addDefNote(n.get(KEY_DEF_NOTE).getAsString());
        }
    }

    public JsonObject exportDatabase() {
        return exportDatabase(true, true, true, true);
    }
    public JsonObject exportDatabase(boolean events, boolean matches, boolean teams, boolean notes) {
        JsonObject output = new JsonObject();
        if (events) {
            output.add(TABLE_EVENTS, exportEvents());
        }
        if (matches) {
            output.add(TABLE_MATCHES, exportMatches());
        }
        if (teams) {
            output.add(TABLE_TEAMS, exportTeams());
        }
        if (notes) {
            output.add(TABLE_NOTES, exportNotes());
            if(tableExists(TABLE_PREDEF_NOTES)){
                output.add(TABLE_PREDEF_NOTES,exportDefNotes());
            }
        }
        return output;
    }
    public void importDatabase(JsonObject data) {
        if(data == null || data.equals(new JsonObject())) return;
        JsonElement e;

        e = data.get(TABLE_EVENTS);
        if(e != null)
            importEvents(e.getAsJsonArray());

        e = data.get(TABLE_MATCHES);
        if(e != null)
            importMatches(e.getAsJsonArray());

        e = data.get(TABLE_TEAMS);
        if(e != null)
            importTeams(e.getAsJsonArray());

        e = data.get(TABLE_NOTES);
        if(e != null)
            importNotes(e.getAsJsonArray());

        if(tableExists(TABLE_PREDEF_NOTES)){
            try {
                e = data.get(TABLE_PREDEF_NOTES);
                if(e != null)
                    importDefNotes(e.getAsJsonArray());
            }catch (Exception ex){

            }
        }
    }

    public boolean tableExists(String table){
        Cursor cursor = db.rawQuery("SELECT DISTINCT tbl_name from sqlite_master where tbl_name = '"+table+"'", null);
        if(cursor!=null && cursor.getCount()>0) {
            cursor.close();
            return true;
        }
        return false;
    }
    private boolean columnExists(SQLiteDatabase inDatabase, String inTable, String columnToCheck) {
        try{
            //query 1 row
            Cursor mCursor  = inDatabase.rawQuery( "SELECT * FROM " + inTable + " LIMIT 0", null );

            //getColumnIndex gives us the index (0 to ...) of the column - otherwise we get a -1
            if(mCursor.getColumnIndex(columnToCheck) != -1)
                return true;
            else
                return false;

        }catch (Exception Exp){
            //something went wrong. Missing the database? The table?
            Log.d("... - existsColumnInTable","When checking whether a column exists in the table, an error occurred: " + Exp.getMessage());
            return false;
        }
    }
}
