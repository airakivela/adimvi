package com.application.adimviandroid.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlUtil {
    myDbHelper myhelper;
    public SqlUtil(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    public long insertData(Integer userid, Integer postID, String date)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.USERID, userid);
        contentValues.put(myDbHelper.POSTID, postID);
        contentValues.put(myDbHelper.DATE, date);
        long id = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        return id;
    }

    public boolean getData(Integer userid, Integer postID, String date)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID,myDbHelper.USERID,myDbHelper.POSTID,myDbHelper.DATE};
        String condition = "SELECT * FROM " + myDbHelper.TABLE_NAME + " WHERE " + columns[1] + "=" + userid + " AND " + columns[2] + "=" + postID + " AND " + columns[3] + "='" + date + "'";
        Cursor cursor =db.rawQuery(condition,null);
        boolean exist = cursor.getCount() > 0;
        cursor.close();
        return exist;
    }

    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "adimvi";    // Database Name
        private static final String TABLE_NAME = "postViewed";   // Table Name
        private static final int DATABASE_Version = 1;    // Database Version
        private static final String UID="_id";     // Column I (Primary Key)
        private static final String USERID = "UserID";    //Column II
        private static final String POSTID= "PostID";    // Column III
        private static final String DATE= "DATE";    // Column IIII
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+USERID+" INTEGER ,"+ POSTID+" INTEGER , "+DATE+" VARCHAR(255));";
        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (Exception e) {

            }
        }
    }
}
