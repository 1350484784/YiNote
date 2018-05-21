package com.cs.note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.listener.DeleteListener;

import com.cs.domain.Note;

public class MulSelectActivity extends Activity implements OnClickListener {

	private ListView listview;
	private Context context;
	private List<String> array = new ArrayList<String>();
	private List<String> objectId = new ArrayList<String>();
	private List<String> selectid = new ArrayList<String>();
	private boolean isMulChoice = false; // 是否多选
	private Adapter adapter;
	private RelativeLayout layout;
	private Button cancle, delete;
	private TextView txtcount;
	private String userName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.batchprocessing_note);
		
		userName = getIntent().getStringExtra("username");
		array = getIntent().getStringArrayListExtra("notes");
		objectId = getIntent().getStringArrayListExtra("objectId");
		
		// 显示返回按钮
		ActionBar myActionBar = getActionBar();
		myActionBar.setHomeButtonEnabled(true);
		myActionBar.setDisplayHomeAsUpEnabled(true);
		myActionBar.setTitle("云端数据");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		context = this;
		listview = (ListView) findViewById(R.id.list);
		layout = (RelativeLayout) findViewById(R.id.relative);
		txtcount = (TextView) findViewById(R.id.txtcount);
		cancle = (Button) findViewById(R.id.cancle);
		delete = (Button) findViewById(R.id.delete);
		cancle.setOnClickListener(this);
		delete.setOnClickListener(this);
		
		adapter = new Adapter(context, txtcount);
		listview.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancle:
			isMulChoice = false;
			selectid.clear();
			adapter = new Adapter(context, txtcount);
			listview.setAdapter(adapter);
			layout.setVisibility(View.INVISIBLE);
			break;
		case R.id.delete:
			new AlertDialog.Builder(MulSelectActivity.this)   
			.setTitle("是否删除数据" )  
			.setMessage("确定吗？" )  
			.setPositiveButton("是" , new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					isMulChoice = false;
					List<BmobObject> notes = new ArrayList<BmobObject>();
					
					for (int i = 0; i < selectid.size(); i++) {
						for (int j = 0; j < array.size(); j++) {
							if (selectid.get(i).equals(array.get(j))) {
								Note note = new Note();
								note.setObjectId(objectId.get(j));
								notes.add(note);
								array.remove(j);
							}
						}
					}
					new BmobObject().deleteBatch(MulSelectActivity.this, notes, new DeleteListener() {
						
						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							Toast.makeText(MulSelectActivity.this, "删除云端数据成功", Toast.LENGTH_SHORT).show();
						}
						
						@Override
						public void onFailure(int i, String s) {
							// TODO Auto-generated method stub
							Toast.makeText(MulSelectActivity.this, s, Toast.LENGTH_SHORT).show();
						}
					});
					
					selectid.clear();
					adapter = new Adapter(context, txtcount);
					listview.setAdapter(adapter);
					layout.setVisibility(View.INVISIBLE);
					
				}
			})  
			.setNegativeButton("否" , null)  
			.show(); 
			
			break;
		default:
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Operate");
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
	
	class Adapter extends BaseAdapter{
		private Context context;
		private LayoutInflater inflater=null;
		private HashMap<Integer, View> mView ;
		public HashMap<Integer, Integer> visiblecheck ;//用来记录是否显示checkBox
		public HashMap<Integer, Boolean> ischeck;
		private TextView txtcount;
		
		public Adapter(Context context,TextView txtcount){
			this.context = context;
			this.txtcount = txtcount;
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mView = new HashMap<Integer, View>();
			visiblecheck = new HashMap<Integer, Integer>();
			ischeck = new HashMap<Integer, Boolean>();
			if(isMulChoice){
				for(int i=0;i<array.size();i++){
					ischeck.put(i, false);
					visiblecheck.put(i, CheckBox.VISIBLE);
				}
			}else{
				for(int i=0;i<array.size();i++){
					ischeck.put(i, false);
					visiblecheck.put(i, CheckBox.INVISIBLE);
				}
			}
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return array.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return array.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = mView.get(position);
			if(view == null){
				view = inflater.inflate(R.layout.batchprocessing_item, null);
				TextView txt = (TextView)view.findViewById(R.id.txtName);
				final CheckBox ceb = (CheckBox)view.findViewById(R.id.check);
				
				txt.setText(array.get(position));
				ceb.setChecked(ischeck.get(position));
				ceb.setVisibility(visiblecheck.get(position));
				
				view.setOnLongClickListener(new View.OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						isMulChoice = true;
						selectid.clear();
						layout.setVisibility(View.VISIBLE);
						for(int i=0;i<array.size();i++){
							adapter.visiblecheck.put(i, CheckBox.VISIBLE);
						}
						adapter = new Adapter(context,txtcount);
						listview.setAdapter(adapter);
						return true;
					}
				});
				
				view.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(isMulChoice){
							if(ceb.isChecked()){
								ceb.setChecked(false);
								selectid.remove(array.get(position));
							}else{
								ceb.setChecked(true);
								selectid.add(array.get(position));
							}
							txtcount.setText("共选择了"+selectid.size()+"项");
						}else{
							Toast.makeText(context, "点击了"+array.get(position), Toast.LENGTH_LONG).show();
						}
					}
				});
				mView.put(position, view);
			}
			return view;
		}
	}
}
