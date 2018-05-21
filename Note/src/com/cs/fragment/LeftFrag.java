package com.cs.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs.note.R;

public class LeftFrag extends Fragment {
	
	//传递操作
	public interface MyListener{
		public void settings(String index);
	}
	
	ImageView userImg;
	TextView username;
	Button allNotes;
	Button searchNote;
	//Button shareNote;
	Button logout;
	
	private MyListener myListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		myListener = (MyListener) activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.note_main_left, container,false);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		String name = getActivity().getIntent().getStringExtra("send_user_name");
		
		userImg = (ImageView) getActivity().findViewById(R.id.id_left_userimg);
		username = (TextView) getActivity().findViewById(R.id.id_left_username);
        username.setText(name);
		
		allNotes = (Button) getActivity().findViewById(R.id.allNotes);
		searchNote = (Button) getActivity().findViewById(R.id.searchNote);
		//shareNote = (Button) getActivity().findViewById(R.id.shareNote);
		logout = (Button) getActivity().findViewById(R.id.logout);
		
		MyClickListen clickListen = new MyClickListen();
		//userImg.setOnClickListener(clickListen);
		allNotes.setOnClickListener(clickListen);
		searchNote.setOnClickListener(clickListen);
		//shareNote.setOnClickListener(clickListen);
		logout.setOnClickListener(clickListen);
	}
	



	/**
	 * 监听类
	 * @author Carey
	 *
	 */
	class MyClickListen implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				/*case R.id.id_left_userimg:
					myListener.settings("selectImg");
				break;*/
				case R.id.allNotes:
					myListener.settings("allNotes");
					break;
				case R.id.searchNote:
					myListener.settings("searchNote");
					break;
				/*case R.id.shareNote:
					myListener.settings("shareNote");
					break;*/
				case R.id.logout: 
					myListener.settings("logout");
					break;
				default:
					break;
			}
			/*
			Button button = (Button)v;
			if(button == allNotes){
				myListener.settings("allNotes");
			}
			if(button == searchNote){
				myListener.settings("searchNote");
			}
			if(button == shareNote){
				myListener.settings("shareNote");
			}
			if(button == logout){
				myListener.settings("logout");
			}
			*/
		}
	}
}
