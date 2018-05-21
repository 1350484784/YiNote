package com.cs.fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import com.cs.adapter.ShowNoteAdapter;
import com.cs.dao.NoteDao;
import com.cs.domain.Note;
import com.cs.note.NoteMainActivity;
import com.cs.note.NoteWriteActivity;
import com.cs.note.R;
import com.cs.service.BmobService;
import com.cs.util.Constants;

public class AllNotesFragment extends Fragment implements android.widget.AdapterView.OnItemClickListener,LoaderCallbacks<Cursor>,SwipeRefreshLayout.OnRefreshListener{

	private final static int CONTEXT_UPDATE_ORDER = 0;
    private final static int CONTEXT_DELETE_ORDER = 1;
	
    String name;
	NoteDao mNoteDao;
	ListView mNotes;
	SwipeRefreshLayout mSwipeRefreshLayout;
	CursorAdapter mAdapter;
	Cursor mCursor;
	View root;
    List<BmobObject> mSyncNotes = new ArrayList<>();
    Set<Note> mAllNotes = new HashSet<>();
    
    // 自动同步功能需使用
    Timer mTimer;
    SyncStateReceiver mReceiver;
    
    public AllNotesFragment() { }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mNoteDao = new NoteDao(getActivity());
		name = getActivity().getIntent().getStringExtra("send_user_name");
		// 查询本用户的所有行
		mCursor = mNoteDao.queryNote("userName=?",new String[]{name});
		
		// 获取同步信息,启动Service的计划任务
		if(NoteMainActivity.IS_SYNC){
			Intent intent = new Intent(getActivity(),BmobService.class);
			getActivity().startService(intent);
			//注册广播
			mReceiver = new SyncStateReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constants.SYNC_BROADCAST_ACTION);
			getActivity().registerReceiver(mReceiver, filter);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_all_note, container, false);
		mNotes = (ListView) root.findViewById(R.id.id_lv_all_note);
		
		mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.id_srl_refresh);
		mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#3F51B5"), 0, 0, 0);
		// 在下拉刷新时同步数据
		mSwipeRefreshLayout.setOnRefreshListener(this);
		
		//mAdapter = new ShowNoteAdapter(getActivity(), mCursor, name);
		mAdapter = new ShowNoteAdapter(getActivity(), mCursor, "userName=?",new String[]{name});
		mNotes.setAdapter(mAdapter);
		
		getLoaderManager().initLoader(0, null, this);
		mNotes.setOnItemClickListener(this);
		registerForContextMenu(mNotes);
		return root;
	}

	/**
     * 此时重启Loader机制更新数据
     */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mCursor = mNoteDao.queryNote("userName=?",new String[]{name});
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mCursor.close();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mReceiver != null){
			getActivity().unregisterReceiver(mReceiver);
			Intent intent = new Intent(getActivity(), BmobService.class);
			getActivity().stopService(intent);
		}
	}

	/**
     * 上下文菜单的回调函数
     */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		Cursor c = (Cursor) mAdapter.getItem(position);
		final int itemID = c.getInt(c.getColumnIndex("_id"));
		switch (item.getOrder()) {
		case CONTEXT_UPDATE_ORDER:// 更新操作
			Intent intent = new Intent(getActivity(),NoteWriteActivity.class);
			intent.putExtra(NoteWriteActivity.SENDED_NOTE_ID, itemID);
			intent.putExtra("username", name);
			startActivity(intent);
			break;

		case CONTEXT_DELETE_ORDER:
			mNoteDao.deleteNote("_id=?", new String[]{itemID + ""});
			getLoaderManager().restartLoader(0, null, this);
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	/**
     * 创建上下文菜单
     */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, v.getId(), CONTEXT_UPDATE_ORDER, "Update");
		menu.add(0, v.getId(), CONTEXT_DELETE_ORDER, "Delete");
	}

	/**
	 *  跳转到详情页
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Cursor c = (Cursor) mAdapter.getItem(position);
		int itemID = c.getInt(c.getColumnIndex("_id"));
		Intent intent = new Intent(getActivity(),NoteWriteActivity.class);
		intent.putExtra(NoteWriteActivity.SENDED_NOTE_ID, itemID);
		intent.putExtra("username", name);
		startActivity(intent);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = Uri.parse("content://com.cs.NoteBook");
		return new CursorLoader(getActivity(),uri, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
	
	/**
     * 下拉刷新时,向Bmob后台同步数据
     */
	@Override
	public void onRefresh() {
		final Uri uri = Uri.parse("content://com.cs.NoteBook");
		Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
		if(cursor != null){
			while(cursor.moveToNext()){
				Note note = new Note();
				int noteID = cursor.getInt(0);
				
				note.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				note.setContent(cursor.getString(cursor.getColumnIndex("content")));
				note.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
				note.setUserName(name);
				// 将数据库中所有数据添加到集合中
                mAllNotes.add(note);
                
                if(cursor.getString(cursor.getColumnIndex("isSync")).equals("false")){
                	ContentValues values = new ContentValues();
                	values.put("isSync", "true");
                	getActivity().getContentResolver().update(uri, values, "_id=?", new String[]{noteID + ""});
                	mSyncNotes.add(note);
                }
			}
			cursor.close();
			// 批量向服务器上传数据数据
			new BmobObject().insertBatch(getActivity(), mSyncNotes, new SaveListener() {
				
				@Override
				public void onSuccess() {
					mSyncNotes.clear();
					// 向服务器下载本机没有的数据
					BmobQuery<Note> bmobQuery = new BmobQuery<>();
					bmobQuery.addWhereEqualTo("userName", name);
					bmobQuery.setLimit(10); // 返回10条数据
					// 从服务器获取数据
					bmobQuery.findObjects(getActivity(), new FindListener<Note>() {
						@Override
						public void onSuccess(List<Note> list) {
							// 获取所有没有在服务器中的数据
							list.removeAll(mAllNotes);
							ContentResolver resolver = getActivity().getContentResolver();
							// 将此数据写入数据库中
							for (Note note : list) {
								ContentValues values = new ContentValues();
								values.put("title",  note.getTitle());
								values.put("content", note.getContent());
                                values.put("create_time", note.getCreateTime());
                                values.put("userName", name);
                                values.put("isSync", "true");
                                resolver.insert(uri, values);
							}
							mAllNotes.clear();
							mSwipeRefreshLayout.setRefreshing(false);
							// 通知UI更新界面
							getLoaderManager().restartLoader(0, null, AllNotesFragment.this);
							Toast.makeText(getActivity(), "同步完成", Toast.LENGTH_SHORT).show();
						}
						
						@Override
						public void onError(int i, String s) {
							// TODO Auto-generated method stub
							Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
						}
					});
				}
				
				@Override
				public void onFailure(int i, String s) {
					mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "更新失败 " + s, Toast.LENGTH_SHORT).show();
				}
			});
		}
	
	}
	
	/**
     * 接收Service发送的广播,更新UI
     */
    class SyncStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String state = intent.getStringExtra(BmobService.SEND_SYNC_STATE);
            Toast.makeText(context, state, Toast.LENGTH_SHORT).show();
        }
    }
	
}
