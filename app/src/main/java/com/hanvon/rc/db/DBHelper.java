package com.hanvon.rc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hanvon.rc.utils.LogUtil;

/**
 * Created by baiheng222 on 16-4-27.
 */
public class DBHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "rc.db";
    private static final int DATABASE_VERSINO = 1;
    public static final String DATABASE_TABLE = "file_table";

    public static final String KEY_ID = "_id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_ORIGIN_PIC_PATH = "origin_file_path";
    public static final String KEY_RESULT_FILE_PATH = "result_file_path";
    public static final String KEY_RESULT_FILE_TYPE = "result_file_type";
    public static final String KEY_RESULT_FILE_ID = "result_file_id";
    public static final String KEY_RESULT_FILE_ISDELETE = "result_file_isdelete";

    public static final String KEY_RESULT_FILE_SIZE = "result_file_size";

    private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + "(" +
            KEY_ID + " integer primary key autoincrement, " +  KEY_USER_ID + " VARCHAR, " +
            KEY_ORIGIN_PIC_PATH + " VARCHAR, " + KEY_RESULT_FILE_PATH + " VARCHAR, " +
            KEY_RESULT_FILE_TYPE + " VARCHAR, " + KEY_RESULT_FILE_ID + " VARCHAR, " +
            KEY_RESULT_FILE_SIZE + " integer" + KEY_RESULT_FILE_ISDELETE + " integer" + ")";


    //查询订单支付状态
    public static final String KEY_QUERY_ORDER_ID = "_id";
    public static final String KEY_QUERY_ORDER_NUMBER = "ordernumber";
    public static final String KEY_QUERY_ORDER_PAYMODE = "paymode";
    public static final String DATABASE_ORDER_TABLE = "order_table";
    public static final String DATABASE_CREATE_ORDER_TABLE = "create table " + DATABASE_ORDER_TABLE + "(" +
            KEY_QUERY_ORDER_ID + " integer primary key autoincrement, " +  KEY_QUERY_ORDER_NUMBER + " VARCHAR, " +
            KEY_QUERY_ORDER_PAYMODE + " VARCHAR " + ")";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSINO);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        LogUtil.i("create tabel string is : " + DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE_ORDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }
}
