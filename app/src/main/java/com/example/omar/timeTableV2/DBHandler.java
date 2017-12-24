package com.example.omar.timeTableV2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DBHandler extends SQLiteOpenHelper{

    private static int VERSION = 1;

    private static String[] daysOfWeak = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
                                          "Friday", "Saturday"};

    //date formats
    private static SimpleDateFormat sessionTimeFormat = new SimpleDateFormat("HH mm", Locale.US);
    private static SimpleDateFormat reminderFormat    = new SimpleDateFormat("HH mm dd MM yyyy",
                                                                             Locale.US);
    private static SimpleDateFormat taskDateFormat    = new SimpleDateFormat("dd MM yyyy",
                                                                             Locale.US);


    public DBHandler(Context context, SQLiteDatabase.CursorFactory factory){

        super(context, "Timetable.db", factory, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){

        //Create subjects table
        //stores all subject names and gives them IDs
        sqLiteDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS _SUBJECTS ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "_NAME TEXT);");

        //Create days tables
        //stores classes times and locations
        //a table for each day
        for(int i = 0; i < 7; i++){
            String sqlStatement = "CREATE TABLE IF NOT EXISTS " + daysOfWeak[i] +
                                  "( _ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                  "_SUBJ_ID INTEGER," + "_START_TIME TEXT, _END_TIME TEXT," +
                                  "_TIME_UNTIL_NEXT INTEGER, _LOCATION TEXT);";
            sqLiteDatabase.execSQL(sqlStatement);
        }

        //create tasks table
        //stores all tasks
        sqLiteDatabase.execSQL(
                "CREATE TABLE  IF NOT EXISTS _TASKS ( _ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "_SUBJ_ID INTEGER, _TITLE TEXT, _DESCRIPTION TEXT, " +
                "_SET_DATE TEXT, _DUE_DATE TEXT);");

        //create reminders table
        //stores reminders for tasks
        sqLiteDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS _REMINDERS ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "_TASK_ID INTEGER, _TIME TEXT);");

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){

    }


    //adds a new subject
    public void addSubject(String subj){

        //store values to be added
        ContentValues values = new ContentValues();
        values.put("_NAME", subj);

        //create dbHandler
        SQLiteDatabase db = getWritableDatabase();
        db.insert("_SUBJECTS", null, values);
        db.close();
    }


    //gets all subject names
    public List<String> getSubjects(){

        SQLiteDatabase dbRead = getReadableDatabase();
        String         sql    = "SELECT _NAME FROM _SUBJECTS WHERE 1";

        Cursor c = dbRead.rawQuery(sql, null);

        List<String> subjects = new ArrayList<>();

        //moves to first row
        c.moveToFirst();
        //checks if not after last (before last or on last)
        while(!c.isAfterLast()){
            subjects.add(c.getString(0));
            c.moveToNext();
        }

        c.close();
        return subjects;
    }


    public void clearDay(String day){

        SQLiteDatabase dbWrite = getWritableDatabase();
        dbWrite.delete(day, "1", null);
        dbWrite.close();
    }


    public boolean isDayDone(String day){

        boolean isDone;

        SQLiteDatabase dbRead = getReadableDatabase();
        Cursor         c      = dbRead.rawQuery("SELECT * FROM " + day + " WHERE 1", null);
        isDone = c.getCount() > 0;
        c.close();

        return isDone;
    }


    //sessions


    //add session
    public void addSession(String day, String subj, Date startTime, Date endTime){

        ContentValues  values  = new ContentValues();
        SQLiteDatabase dbWrite = getWritableDatabase();

        //convert date to string for storage
        String startTimeString = sessionTimeFormat.format(startTime);
        String endTimeString   = sessionTimeFormat.format(endTime);

        int subjID;
        try {
            subjID = getSubjectID(subj);
        } catch(DoesNotExist doesNotExist) {
            doesNotExist.printStackTrace();
            return;
        }

        //insert values into table
        values.put("_SUBJ_ID", subjID);
        values.put("_START_TIME", startTimeString);
        values.put("_END_TIME", endTimeString);

        List<Date> endTimes = getEndTimes(day);

        ContentValues oldValues = new ContentValues();

        if(endTimes.size() != 0){
            long diff = startTime.getTime() - endTimes.get(endTimes.size() - 1).getTime();
            diff /= 60000;
            oldValues.put("_TIME_UNTIL_NEXT", diff);
            dbWrite.update(day, oldValues, "_END_TIME = ?", new String[]{
                    sessionTimeFormat.format(endTimes.get(endTimes.size() - 1))});
        }

        dbWrite.insert(day, null, values);
        dbWrite.close();
    }

    //get all


    //sessions
    public List<String> getSessionNames(String day){

        List<String>  sessions   = new ArrayList<>();
        List<Integer> sessionIDs = new ArrayList<>();

        SQLiteDatabase dbRead = getReadableDatabase();
        Cursor         c      = dbRead.rawQuery("SELECT _SUBJ_ID FROM " + day + " WHERE 1", null);

        if(c.getCount() > 0){
            c.moveToFirst();
            while(!c.isAfterLast()){
                sessionIDs.add(c.getInt(0));
                c.moveToNext();
            }
        }
        c.close();

        for(int id : sessionIDs){
            try {
                sessions.add(getSubjectName(id));
            } catch(DoesNotExist doesNotExist) {
                doesNotExist.printStackTrace();
            }

        }

        return sessions;
    }


    //start Times
    public List<Date> getStartTimes(String day){

        List<Date> startTimes = new ArrayList<>();

        SQLiteDatabase dbRead = getReadableDatabase();
        Cursor         c      = dbRead
                .rawQuery("SELECT _START_TIME FROM " + day + " WHERE 1", null);

        c.moveToFirst();

        while(!c.isAfterLast()){
            try {
                startTimes.add(sessionTimeFormat.parse(c.getString(0)));
            } catch(ParseException e) {
                e.printStackTrace();
            }
            c.moveToNext();
        }

        c.close();

        return startTimes;
    }


    //end Times
    public List<Date> getEndTimes(String day){

        List<Date> endTimes = new ArrayList<>();

        SQLiteDatabase dbRead = getReadableDatabase();
        Cursor         c      = dbRead.rawQuery("SELECT _END_TIME FROM " + day + " WHERE 1", null);

        c.moveToFirst();

        while(!c.isAfterLast()){
            try {
                endTimes.add(sessionTimeFormat.parse(c.getString(0)));
            } catch(ParseException e) {
                e.printStackTrace();
            }
            c.moveToNext();
        }

        c.close();

        return endTimes;
    }


    //time Until next
    public List<Integer> getMinutesUntilNext(String day){

        List<Integer> timeUntilNext = new ArrayList<>();

        SQLiteDatabase dbRead = getReadableDatabase();
        Cursor c = dbRead.rawQuery("SELECT _TIME_UNTIL_NEXT FROM " + day + " WHERE 1", null);

        c.moveToFirst();

        while(!c.isAfterLast()){
            timeUntilNext.add(c.getInt(0));
            c.moveToNext();
        }

        c.close();
        return timeUntilNext;
    }


    public void addTask(String subj, String title, String desc, Date setDate, Date dueDate){

        //convert date to string for storage
        String setDateString = taskDateFormat.format(setDate);
        String dueDateString = taskDateFormat.format(dueDate);

        ContentValues  values  = new ContentValues();
        SQLiteDatabase dbWrite = getWritableDatabase();

        int subjID;
        try {
            subjID = getSubjectID(subj);
        } catch(DoesNotExist e) {
            e.printStackTrace();
            return;
        }

        values.put("_SUBJ_TASK", subjID);
        values.put("_TITLE", title);
        values.put("_DESCRIPTION", desc);
        values.put("_SET_DATE", setDateString);
        values.put("_DUE_DATE", dueDateString);

        dbWrite.insert("_TASKS", null, values);

    }


    public void addReminder(int taskID, Date setTime){

        ContentValues  values  = new ContentValues();
        SQLiteDatabase dbWrite = getWritableDatabase();

        String setTimeString = reminderFormat.format(setTime);

        values.put("_TASK_ID", taskID);
        values.put("_TIME", setTimeString);
        dbWrite.insert("_REMINDERS", null, values);
    }


    private int getSubjectID(String subj) throws DoesNotExist{

        SQLiteDatabase dbRead = getReadableDatabase();
        Cursor c = dbRead
                .rawQuery("SELECT _ID FROM _SUBJECTS WHERE _NAME = \"" + subj + "\"", null);

        int id;

        if(c.getCount() > 0){
            c.moveToFirst();
            id = c.getInt(0);
            c.close();
        } else{
            c.close();
            throw new DoesNotExist("Subject does not exist: " + subj);
        }

        return id;
    }


    private String getSubjectName(int ID) throws DoesNotExist{

        String name;

        SQLiteDatabase dbRead = getReadableDatabase();
        Cursor         c      = dbRead
                .rawQuery("SELECT _NAME FROM _SUBJECTS WHERE _ID = " + ID, null);

        if(c.getCount() > 0){
            c.moveToFirst();
            name = c.getString(0);
        } else{
            throw new DoesNotExist("Subject ID: " + ID + " Does not exist in Database");
        }

        c.close();

        return name;
    }


    private class DoesNotExist extends Exception{

        DoesNotExist(String message){

            super(message);
        }
    }

}
