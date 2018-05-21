package com.cs.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NoteDao {
	private DBHelper mHelper;
	
	public NoteDao(Context context) {
		mHelper =  new DBHelper(context);
	}
	
	/**
	 * 插入，返回id(自增)
	 * @param contentValues
	 * @return
	 */
	public long insertNote(ContentValues contentValues) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long id = db.insert(DBHelper.DB_NAME, null, contentValues);
        db.close();
        return id;
    }
	
	/**
	 * 修改
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int updateNote(ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int count;
        count = db.update(DBHelper.DB_NAME, values, whereClause, whereArgs);
        db.close();
        return count;
    }
	
	/**
	 * 删
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int deleteNote(String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int count = db.delete(DBHelper.DB_NAME, whereClause, whereArgs);
        return count;
    }
	
	/**
	 * 查
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public Cursor queryNote(String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor c = db.query(false, DBHelper.DB_NAME, null, selection, selectionArgs
                ,null, null, null, null);
        return c;
	}
	
}
