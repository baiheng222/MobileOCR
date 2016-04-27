package com.hanvon.rc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baiheng222 on 16-4-27.
 */
public class DBManager
{
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public static final String KEY_USER_ID = DBHelper.KEY_USER_ID;
    public static final String KEY_ORIGIN_PIC_PATH = DBHelper.KEY_ORIGIN_PIC_PATH;
    public static final String KEY_RESULT_FILE_PATH = DBHelper.KEY_RESULT_FILE_PATH;
    public static final String KEY_RESULT_FILE_TYPE = DBHelper.KEY_RESULT_FILE_TYPE;
    public static final String KEY_RESULT_FILE_ID = DBHelper.KEY_RESULT_FILE_ID;
    public static final String KEY_RESULT_FILE_SIZE = DBHelper.KEY_RESULT_FILE_SIZE;

    public DBManager(Context context)
    {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void closeDB()
    {
        db.close();
    }

    public List<FileInfo>  queryForAll()
    {
        Cursor c = db.rawQuery("SELECT * FROM " + DBHelper.DATABASE_TABLE + " ORDER BY " +
        KEY_RESULT_FILE_ID + " DESC", null);
        List<FileInfo> flist = new ArrayList<FileInfo>();
        while (c.moveToNext())
        {
            FileInfo file = new FileInfo();
            file.setUserID(c.getString(c.getColumnIndex(KEY_USER_ID)));
            file.setOriginPath(c.getString(c.getColumnIndex(KEY_ORIGIN_PIC_PATH)));
            file.setResultPath(c.getString(c.getColumnIndex(KEY_RESULT_FILE_PATH)));
            file.setResultType(c.getString(c.getColumnIndex(KEY_RESULT_FILE_TYPE)));
            file.setResultFUID(c.getString(c.getColumnIndex(KEY_RESULT_FILE_ID)));
            file.setResultSize(c.getInt(c.getColumnIndex(KEY_RESULT_FILE_SIZE)));
            flist.add(file);
        }
        c.close();
        return flist;
    }


    public void insertRecord(FileInfo info)
    {
        ContentValues value = new ContentValues();
        value.put(KEY_USER_ID, info.getUserID());
        value.put(KEY_ORIGIN_PIC_PATH, info.getOriginPath());
        value.put(KEY_RESULT_FILE_PATH, info.getResultPath());
        value.put(KEY_RESULT_FILE_TYPE, info.getResultType());
        value.put(KEY_RESULT_FILE_ID, info.getResultFUID());
        value.put(KEY_RESULT_FILE_SIZE, info.getResultSize());
        db.insert(DBHelper.DATABASE_TABLE, null, value);
    }



    private void updateRecord(FileInfo info)
    {

    }

}
