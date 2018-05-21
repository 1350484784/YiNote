package com.cs.note;

import cn.bmob.v3.BmobUser;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

public class SplashActivity extends Activity{

	public static final String SEND_USER_NAME = "send_user_name";
	private ImageView imageView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        
        imageView = (ImageView) findViewById(R.id.id_splash);
        // 实现渐变效果
        Animation animation = new AlphaAnimation(0.5f, 1f);
        animation.setDuration(2000);
        imageView.startAnimation(animation);
        // 动画结束后启动登陆界面或主界面
        animation.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				// 判断是否有用户登陆
                BmobUser user = BmobUser.getCurrentUser(SplashActivity.this);
                if (user != null) {
                	String userName = user.getUsername();
                	Intent intent = new Intent(SplashActivity.this, NoteMainActivity.class);
                    intent.putExtra(SEND_USER_NAME, userName);
                    startActivity(intent);
                }else {
                	// 启动登陆界面
                    Intent intent = new Intent(SplashActivity.this, LoginMainActivity.class);
                    startActivity(intent);
				}
                finish();
			}
		});
        
	}

}
