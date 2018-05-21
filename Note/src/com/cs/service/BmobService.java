package com.cs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import com.cs.domain.Note;
import com.cs.util.Constants;

public class BmobService extends Service{

	private List<BmobObject> myNotes = new ArrayList<>();
	private Timer mTimer = new Timer();
	private Uri uri = Uri.parse("content://com.cs.NoteBook");
	private ContentResolver myResolver;
	public static final String SEND_SYNC_STATE = "STATE";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 执行定时任务
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		myResolver = getContentResolver();
		TimerTask task = new SyncTask();
        mTimer.schedule(task, 5000, 5 * 60 * 1000); // 五分钟更新一次
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mTimer.cancel();
	}
	
	/**
	 * 定时任务
	 * @author Carey
	 *
	 */
	class SyncTask extends TimerTask{

		@Override
		public void run() {
			myNotes.clear();
			Cursor cursor = myResolver.query(uri, null, "userName=? and isSync=?", new String[]{BmobUser.getCurrentUser(BmobService.this).getUsername(),"false"}, null);
			if(cursor != null){
				while(cursor.moveToNext()){
					Note note = new Note();
					int noteID = cursor.getInt(0);
					note.setContent(cursor.getString(cursor.getColumnIndex("title")));
					note.setContent(cursor.getString(cursor.getColumnIndex("content")));
					note.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
					note.setUserName(BmobUser.getCurrentUser(BmobService.this).getUsername());
					myNotes.add(note);
					// 标记为已同步
                    ContentValues values = new ContentValues();
                    values.put("isSync", "true");
                    myResolver.update(uri, values, "_id=?", new String[]{noteID + ""});
				}
				cursor.close();
				// 向服务器发送数据
				new BmobObject().insertBatch(BmobService.this, myNotes, new SaveListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
                        intent.setAction(Constants.SYNC_BROADCAST_ACTION);
                        intent.putExtra(SEND_SYNC_STATE, "自动同步完成");
                        sendBroadcast(intent);
					}
					
					@Override
					public void onFailure(int i, String s) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
                        intent.setAction(Constants.SYNC_BROADCAST_ACTION);
                        intent.putExtra(SEND_SYNC_STATE, s);
                        sendBroadcast(intent);
					}
				});
			}
		}
	}
}
