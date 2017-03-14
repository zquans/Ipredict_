package com.woyuce.activity.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LeBang on 2017/2/15
 */
public class DbUtil {

    public static DbHelper getHelper(Context context, String path) {
        return new DbHelper(context, path);
    }

    //API方法,返回新增成功的 row ID,若发生错误返回-1
    public static long insert(SQLiteDatabase database, String table, ContentValues values) {
        return database.insert(table, null, values);
    }

    public static int queryToInt(SQLiteDatabase database, String table, String column, String condition, String condition_value) {
        int target = 0;
        Cursor cursor = database.query(table, new String[]{column}, condition, new String[]{condition_value}, null, null, null);
        //开启事务批量操作
        if (cursor != null) {
            while (cursor.moveToNext()) {
                target = Integer.parseInt(cursor.getString(0));
            }
        }
        return target;
    }

    public static String queryToString(SQLiteDatabase database, String table, String column, String condition, String condition_value) {
        String target = "none";
        Cursor cursor = database.query(table, new String[]{column}, null, null, null, null, null);
        //开启事务批量操作
        if (cursor != null) {
            while (cursor.moveToNext()) {
                return cursor.getString(0);
            }
        }
        return target;
    }

    public static String queryToExist(SQLiteDatabase database, String table, String column, String condition, String condition_value) {
        String target = "none";
        Cursor cursor = database.query(table, new String[]{column}, condition + " =? ", new String[]{condition_value}, null, null, null);
        //开启事务批量操作
        if (cursor != null) {
            while (cursor.moveToNext()) {
                return cursor.getString(0);
            }
        }
        return target;
    }
}