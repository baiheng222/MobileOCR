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
    public static final String KEY_RESULT_FILE_IsDelete = DBHelper.KEY_RESULT_FILE_ISDELETE;

    public static final String KEY_ORDER_NUMBER = DBHelper.KEY_QUERY_ORDER_NUMBER;
    public static final String KEY_ORDER_PAY_MODE = DBHelper.KEY_QUERY_ORDER_PAYMODE;

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



    public void updateResultPathByFid(String Path,String fid)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_RESULT_FILE_PATH,Path);
        db.update(DBHelper.DATABASE_TABLE, values, KEY_RESULT_FILE_ID + "= ?", new String[]{fid});
    }

    public void updateResultIsDeleteByFid(String fid)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_RESULT_FILE_IsDelete,1);
        db.update(DBHelper.DATABASE_TABLE, values, KEY_RESULT_FILE_ID + "= ?", new String[]{fid});
    }

    public void deleteRecordByFid(String fid){
        db.delete(DBHelper.DATABASE_TABLE, KEY_RESULT_FILE_ID + "= ?", new String[]{fid});
    }

    public FileInfo queryRecordByFid(String fid){
        Cursor cursor = db.query(DBHelper.DATABASE_TABLE, null, KEY_RESULT_FILE_ID + " = ?", new String[]{fid}, null, null, null);
        FileInfo fileInfo = null;
        if(cursor.moveToFirst()){
            fileInfo = new FileInfo();
            do{
                fileInfo.setResultPath(cursor.getString(cursor.getColumnIndex(KEY_RESULT_FILE_PATH)));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return fileInfo;
    }

    public String GetDeleteFids(){
        String Fids = "";
        int num = 0;
        Cursor cursor = db.rawQuery("SELECT " + KEY_RESULT_FILE_ID + " FROM " + DBHelper.DATABASE_TABLE + " WHERE " +
                KEY_RESULT_FILE_IsDelete + " = 1" + " ORDER BY " + KEY_RESULT_FILE_ID + " DESC", null);
        FileInfo fileInfo = null;
        if(cursor.moveToFirst()){
            do{
                num++;
                if(num == 1){
                    Fids = "{" + cursor.getString(cursor.getColumnIndex(KEY_RESULT_FILE_ID));
                }else{
                    Fids += "," + cursor.getString(cursor.getColumnIndex(KEY_RESULT_FILE_ID));
                }
            }while(cursor.moveToNext());
        }
        cursor.close();
        if(num != 0){
            Fids += "}";
        }
        return Fids;
    }

    public List<OrderInfo> QueryAllOrder(){
        Cursor c = db.rawQuery("SELECT * FROM " + DBHelper.DATABASE_ORDER_TABLE + " ORDER BY " +
                DBHelper.KEY_QUERY_ORDER_PAYMODE + " DESC", null);
        List<OrderInfo> orderlist = new ArrayList<OrderInfo>();
        while (c.moveToNext())
        {
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setOrderNumber(c.getString(c.getColumnIndex(KEY_ORDER_NUMBER)));
            orderInfo.setPayMode(c.getString(c.getColumnIndex(KEY_ORDER_PAY_MODE)));

            orderlist.add(orderInfo);
        }
        c.close();
        return orderlist;
    }

    public void insertOrder(OrderInfo info)
    {
        ContentValues value = new ContentValues();
        value.put(KEY_ORDER_NUMBER, info.getOrderNumber());
        value.put(KEY_ORDER_PAY_MODE, info.getPayMode());
        db.insert(DBHelper.DATABASE_ORDER_TABLE, null, value);
    }

    public void deleteOrderFromId(String ordernumber){
        db.delete(DBHelper.DATABASE_CREATE_ORDER_TABLE, KEY_ORDER_NUMBER + "= ?", new String[]{ordernumber});
    }

    public boolean getOrderIsInDatabase(OrderInfo orderInfo){
        Cursor c = db.query(DBHelper.DATABASE_ORDER_TABLE, null, DBHelper.KEY_QUERY_ORDER_NUMBER + " = ?", new String[]{orderInfo.getOrderNumber()}, null, null, null);
        List<OrderInfo> orderlist = new ArrayList<OrderInfo>();
        while (c.moveToNext())
        {
            OrderInfo orderInfo1 = new OrderInfo();
            orderInfo1.setOrderNumber(c.getString(c.getColumnIndex(KEY_ORDER_NUMBER)));
            orderInfo1.setPayMode(c.getString(c.getColumnIndex(KEY_ORDER_PAY_MODE)));

            orderlist.add(orderInfo1);
        }
        c.close();

        if(orderlist.size() > 0){
            return true;
        }
        return false;
    }
}
