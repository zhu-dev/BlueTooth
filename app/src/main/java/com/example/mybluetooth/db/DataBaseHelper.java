package com.example.mybluetooth.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DataBaseHelper";

    private Context context;

    private static final String CREATE_COURSE = "CREATE TABLE users ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "time TEXT,"
            + "date TEXT,"
            + "name TEXT,"
            + "sys_pressure INTEGER,"
            + "dia_pressure INTEGER)";

    /**
     * @param context context
     * @param name    数据库名字
     * @param factory 返回自定义cursor ，一般传null
     * @param version 数据库版本号，更新时需要改大
     */
    public DataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COURSE);
        Log.e(TAG, "onCreate:-------ok---- ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists course");
        onCreate(db);
    }
}
