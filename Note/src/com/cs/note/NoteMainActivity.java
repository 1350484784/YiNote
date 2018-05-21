package com.cs.note;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import com.cs.domain.Note;
import com.cs.fragment.AllNotesFragment;
import com.cs.fragment.LeftFrag;
import com.cs.fragment.LeftFrag.MyListener;
import com.cs.fragment.RightFrag;
import com.cs.fragment.SearchNoteFragment;
import com.cs.util.ImageUtil;

/**
 * 记事本界面
 * @author Carey
 *
 */
public class NoteMainActivity extends Activity implements MyListener,ImageUtil.CropHandler{

	private long curTimeMills;
	Fragment mFragment;
	String userName;
	ImageView imageView;
	Button cloudDataButton;
	
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESIZE_REQUEST_CODE = 2;
	private static final String IMAGE_FILE_NAME = "ic_header_login.png";

	
	// 是否启动Service执行计划任务
    public static boolean IS_SYNC = false;
	
	@Override
	public void settings(String index) {
		if(index.equals("allNotes")){
			mFragment = new AllNotesFragment();
			showFragment(mFragment);
		}
		if(index.equals("searchNote")){
			mFragment = new SearchNoteFragment();
			showFragment(mFragment);
		}
		
		if(index.equals("logout")){
			logout();
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_main);
		
		// 获取配置信息
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        IS_SYNC = pref.getBoolean("auto_sync", false);
        userName = getIntent().getStringExtra("send_user_name");
		
		FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        LeftFrag leftFrag = new LeftFrag();
        RightFrag rightFrag = new RightFrag();
        
        transaction.add(R.id.leftlayout, leftFrag, "leftlayout");
        transaction.add(R.id.rightlayout, rightFrag, "rightlayout");
        
        transaction.commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		ImageUtil.getCropHelperInstance().sethandleResultListerner(NoteMainActivity.this, requestCode, resultCode,  
                data);  
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		imageView = (ImageView) findViewById(R.id.id_left_userimg);
		
		imageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent galleryIntent = ImageUtil  
                        .getCropHelperInstance()  
                        .buildGalleryIntent();
                startActivityForResult(galleryIntent,  
                        ImageUtil.REQUEST_GALLERY); 
			}
		});
		
		
		cloudDataButton = (Button) findViewById(R.id.cloudData);
		cloudDataButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BmobQuery<Note> query = new BmobQuery<Note>();
				query.addWhereEqualTo("userName", userName);
				query.findObjects(NoteMainActivity.this, new FindListener<Note>() {

					@Override
					public void onError(int i, String s) {
						// TODO Auto-generated method stub
						Toast.makeText(NoteMainActivity.this, s, Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(List<Note> list) {
						// TODO Auto-generated method stub
						
						ArrayList<String> array = new ArrayList<String>();
						ArrayList<String> objectId = new ArrayList<String>();
						for (Note note : list) {
							array.add(note.getTitle());
						}
						for(int i=0;i<list.size();i++){
							objectId.add(list.get(i).getObjectId());
						}
						Intent intent = new Intent(NoteMainActivity.this,MulSelectActivity.class);
						intent.putExtra("username", userName);
						intent.putStringArrayListExtra("notes", array);
						intent.putStringArrayListExtra("objectId", objectId);
						startActivity(intent);
					}
				});
				
			}
		});
		
	}
	

	/**
	 * 显示指定的Fragment
	 * @param fragment
	 */
    private void showFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.id_fl_main_content, fragment).commit();
    }
	
	/**
	 * 添加菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.addnote, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.id_menu_add_note:
			Intent intent = new Intent(NoteMainActivity.this, NoteWriteActivity.class);
			intent.putExtra("username", userName);
            startActivity(intent);
			break;
		default:
			break;
		}
		return false;
	}
	
	
	@Override
	public void onPhotoCropped(Bitmap photo, int requestCode) {
		switch (requestCode){  
        case ImageUtil.RE_GALLERY:  
            imageView.setImageBitmap(photo);  
            break;  
        default :  
            break;  
		}  
	}

	@Override
	public void onCropCancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCropFailed(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Activity getContext() {
		// TODO Auto-generated method stub
		return NoteMainActivity.this;
	}
	
	/**
     * 单击后退键时,提示用户是否退出
     */
    @Override
	public void onBackPressed() {
    	exitAPP();
	}

    /**
     * 两秒内单击两下即可关闭APP
     */
    private void exitAPP() {
        if (System.currentTimeMillis() - curTimeMills > 2000) {
            Toast.makeText(NoteMainActivity.this, "再单击一下即可退出", Toast.LENGTH_SHORT).show();
            curTimeMills = System.currentTimeMillis();
        } else {
            finish();
        }
    }
    
    /**
     * 退出当前用户
     */
    private void logout() {
        BmobUser.logOut(this);
        Intent intent = new Intent(this, LoginMainActivity.class);
        startActivity(intent);
        finish();
    }
}    
