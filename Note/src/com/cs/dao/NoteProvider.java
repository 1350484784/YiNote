package com.cs.dao;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * 数据提供者,给不同应用提供内容访问
 * @author Carey
 *
 */
public class NoteProvider extends ContentProvider{

	private NoteDao myNoteDao;
	
	@Override
	public boolean onCreate() {
		myNoteDao = new NoteDao(getContext());
		return false;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		Cursor c = myNoteDao.queryNote(selection, selectionArgs);
		return c;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		myNoteDao.insertNote(values);
		return null;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return myNoteDao.deleteNote(selection, selectionArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return myNoteDao.updateNote(values, selection, selectionArgs);
	}
	
	@Override
	public String getType(Uri uri) {
		return null;
	}
}
