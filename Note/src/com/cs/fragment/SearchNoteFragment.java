package com.cs.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cs.adapter.ShowNoteAdapter;
import com.cs.dao.NoteDao;
import com.cs.note.NoteWriteActivity;
import com.cs.note.R;

/**
 * 根据标题查找所有匹配的note
 * @author Carey
 *
 */
public class SearchNoteFragment extends Fragment{
	EditText editSearch;
	Button butSearchByTitle;
	ListView lvResult;
	NoteDao mNoteDao;
	Cursor mCursor;
	CursorAdapter mAdapter;
	String name;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNoteDao = new NoteDao(getActivity());
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_search,container, false);
		editSearch = (EditText) root.findViewById(R.id.id_et_search_title);
		butSearchByTitle = (Button) root.findViewById(R.id.id_btn_search);
		lvResult = (ListView) root.findViewById(R.id.id_lv_found_note);
		
		name = getActivity().getIntent().getStringExtra("send_user_name");
		
		butSearchByTitle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String title = editSearch.getText().toString();
				if(title.length() > 0 && title !=null ){
					mCursor = mNoteDao.queryNote("title like ? ", new String[]{"%" + title + "%"});
				}
				if(!mCursor.moveToNext()){
					Toast.makeText(getActivity(), "没有这个结果", Toast.LENGTH_SHORT).show();
				}
				String selection = "userName= ? and title like ? ";
				String[] selectionArgs = new String[]{name,"%"+ title +"%"};
				mAdapter = new ShowNoteAdapter(getActivity(), mCursor, selection, selectionArgs);
				lvResult.setAdapter(mAdapter);
			}
		});
		
		lvResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Cursor cursor = (Cursor) mAdapter.getItem(position); //CursorAdapter中getItem()返回特定的cursor对象
				int itemID = cursor.getInt(cursor.getColumnIndex("_id"));
				Intent intent = new Intent(getActivity(),NoteWriteActivity.class);
				intent.putExtra(NoteWriteActivity.SENDED_NOTE_ID, itemID);
				intent.putExtra("username", name);
				startActivity(intent);
			}
		});
		
		return root;
	}


	@Override
	public void onPause() {
		super.onPause();
		if (mCursor != null) {
            mCursor.close();
        }
	}
	
	
}
