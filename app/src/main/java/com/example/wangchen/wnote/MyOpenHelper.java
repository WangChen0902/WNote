package com.example.wangchen.wnote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * 重写了SQLiteOpenHelper类，用来建立数据库和表
 */
public class MyOpenHelper extends SQLiteOpenHelper {

    public MyOpenHelper(Context context) {
        super(context, "WNote.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table myNote(" +
                "ids integer PRIMARY KEY autoincrement," +
                "title text," +
                "content text," +
                "times text," +
                "picture text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}