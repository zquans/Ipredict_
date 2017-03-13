package com.woyuce.activity.Utils;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LeBang on 2017/2/15
 */
public class DbHelper extends SQLiteOpenHelper {

    private String mDatabaseName;

    public DbHelper(Context context, String name) {
        this(context, name, null, 1);
        mDatabaseName = name;
    }

    public DbHelper(Context context, String name, int version) {
        this(context, name, null, version);
        mDatabaseName = name;
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mDatabaseName = name;
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        mDatabaseName = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        LogUtil.i(mDatabaseName + " |database version " + Constants.DATABASE_VERSION + " onCreate");
        //打开或者创建我的本地数据库DOWNLOAD
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //用于升级数据库时调用，作备份时用
        //调用触发条件是,创建本类对象时传入一个比之前创建传入的version大的数即可。
//        LogUtil.i(mDatabaseName + " |database version " + Constants.DATABASE_VERSION + " onUpgrade");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
//        LogUtil.i(mDatabaseName + " |database version " + Constants.DATABASE_VERSION + " onOpen");
    }
}