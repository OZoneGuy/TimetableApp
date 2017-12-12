package com.example.omar.timeTableV2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DBHandler extends SQLiteOpenHelper{

    private static String[]         daysOfWeak        = {"Sunday", "Monday", "Tuesday", "Wednesday",
                                                         "Thursday", "Friday", "Saturday"};
    private static SimpleDateFormat sessionTimeFormat = new SimpleDateFormat("HH mm", Locale.US);
    private static SimpleDateFormat reminderFormat    = new SimpleDateFormat("HH mm dd MM yyyy",
                                                                             Locale.US);
    private static SimpleDateFormat taskDateFormat    = new SimpleDateFormat("dd MM yyyy",
                                                                             Locale.US);


    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory,
                     int version){

        super(context, name, factory, version);
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
                                  "FOREIGN KEY (_SUBJ_ID) REFERENCES _SUBJECTS(_ID)," +
                                  "_START_TIME TEXT, _END_TIME TEXT," +
                                  "_TIME_UNTIL_NEXT INTEGER, _LOCATION TEXT);";
            sqLiteDatabase.execSQL(sqlStatement);
        }

        //create tasks table
        //stores all tasks
        sqLiteDatabase.execSQL(
                "CREATE TABLE  IF NOT EXISTS _TASKS ( _ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "FOREIGN KEY (_SUBJ_ID) REFERENCES _SUBJECTS(_ID), _TITLE TEXT, _DESCRIPTION TEXT, " +
                "_SET_DATE TEXT, _DUE_DATE TEXT);");

        //create reminders table
        //stores reminders for tasks
        sqLiteDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS _REMINDERS ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "FOREIGN KEY (_TASK_ID) REFERENCES _TASKS (_ID), _TIME TEXT);");

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){

    }


    public void addSubject(String subj){

        //store values to be addedd
        ContentValues values = new ContentValues();
        values.put("_NAME", subj);

        //create dbHandler
        SQLiteDatabase db = getWritableDatabase();
        db.insert("_SUBJECTS", null, values);
        db.close();
    }


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
        dbWrite.insert(day, null, values);
        dbWrite.close();
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
        Cursor c = dbRead.rawQuery("SELECT _ID FROM _SUBJECTS WHERE _ NAME = " + subj, null);

        int id;

        if(c.getCount() > 0){
            c.moveToFirst();
            c.close();
            id = c.getInt(0);
        } else{
            c.close();
            throw new DoesNotExist("Subject does not exist: " + subj);
        }

        return id;
    }


    private class DoesNotExist extends Exception{

        DoesNotExist(String message){

            super(message);
        }
    }

}
