package com.cs.note;

import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs.dao.NoteDao;
import com.cs.domain.Note;
import com.cs.util.TextFormat;

public class NoteWriteActivity extends Activity{

	EditText title;
	EditText content;
	Button modify;
	Note mNote;
	NoteDao mNoteDao;
	Cursor mCursor;
	String userName;
	public static final String SENDED_NOTE_ID = "note_id";
	private int mNoteID = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_write);

		userName = getIntent().getStringExtra("username");
		
		// 显示返回按钮
		ActionBar myActionBar = getActionBar();
		myActionBar.setHomeButtonEnabled(true);
		myActionBar.setDisplayHomeAsUpEnabled(true);
		myActionBar.setTitle("Write Note");
		
		Intent intent = getIntent();
		mNote = new Note("", "", TextFormat.formatDate(new Date()));
		mNoteID = intent.getIntExtra(SENDED_NOTE_ID, -1);
		// 如果有ID参数,从数据库中获取信息
        mNoteDao = new NoteDao(this);
        if (mNoteID != -1) {
            // 进行查询必须使用?匹配参数
            mCursor = mNoteDao.queryNote("_id=? and userName=?", new String[]{mNoteID+"", userName});
            if (mCursor.moveToNext()) {
                mNote.setTitle(mCursor.getString(mCursor.getColumnIndex("title")));
                mNote.setContent(mCursor.getString(mCursor.getColumnIndex("content")));
                mNote.setCreateTime(mCursor.getString(mCursor.getColumnIndex("create_time")));
            }
        }
        title = (EditText) findViewById(R.id.id_et_title);
        content = (EditText)findViewById(R.id.id_et_content);
        title.setText(mNote.getTitle());
        content.setText(mNote.getContent());
        
        modify = (Button) findViewById(R.id.id_btn_modify);
        modify.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        String titleString = title.getText().toString();
		        String contenString = content.getText().toString();
				ContentValues values = new ContentValues();
				values.put("title", titleString);
	            values.put("content", contenString);
	            values.put("create_time", mNote.getCreateTime());
	            values.put("userName", userName);
	            int rowID = -1;
	            // 向数据库添加或者更新已有记录
	            if(mNoteID == -1){
	            	rowID = (int) mNoteDao.insertNote(values);
	            }else{
	            	rowID = mNoteDao.updateNote(values, "_id=?", new String[]{mNoteID+""});
	            }
	            
	            if(rowID != -1){
	            	Toast.makeText(NoteWriteActivity.this, "修改或添加成功", Toast.LENGTH_SHORT).show();
	            	getContentResolver().notifyChange(Uri.parse("content://com.cs.NoteBook"), null);
	            	finish();
	            }
			}
		});
	}

	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mCursor != null){
			mCursor.close();
		}
	}

	/**
	 * 返回上个界面
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case android.R.id.home:
        	finish();
            return true;
        default:
        	return super.onOptionsItemSelected(item);
		}
	}

}
