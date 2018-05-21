package com.cs.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	public final static String DB_NAME = "mynote";
    public final static int DB_VERSON = 1;
    
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSON);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table "+DB_NAME+
				"(_id integer primary key autoincrement," +
				"title text, " +
				"content text, " +
				"create_time text, " +
				"userName text," +
				"isSync text default 'false' not null)"
				);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists" + DB_NAME);
        onCreate(db);
	}

}
