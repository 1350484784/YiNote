package com.cs.note;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import com.cs.util.Constants;

/**
 * 登录界面
 * @author Carey
 *
 */
public class LoginMainActivity extends Activity {

	EditText edit_username;
	EditText edit_password;
	Button signin;
	TextView signup;
	View mProgressView;
	View mLoginFormView;
	
	private long curTimeMills;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //初始化Bmob 服务器
        Bmob.initialize(this, Constants.BMOB_API_KEY);
        
        mProgressView = findViewById(R.id.login_pb_loading);
        mLoginFormView = findViewById(R.id.login_linearLayout);
        
        
        //登录
        signin = (Button) findViewById(R.id.loginBut);
        signin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				//判断edit的值 
				edit_username = (EditText) findViewById(R.id.login_username);
				String username = edit_username.getText().toString();
				edit_password = (EditText)findViewById(R.id.login_password);
				String password = edit_password.getText().toString();
				
				if(username != null && !username.equals("")){
					if(password != null && !password.equals("")){
						//从服务器中查找用户信息
						loginInBmob(username, password);
					}else{
						Toast.makeText(LoginMainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
					}
				}else {
					Toast.makeText(LoginMainActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
        
        //注册
        signup = (TextView) findViewById(R.id.signUp);
        signup.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginMainActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
    }

    /**
     * 向Bmob 提交登陆数据
     * @param username
     * @param password
     */
    private void loginInBmob(String username, String password){
    	BmobUser user = new BmobUser();
    	user.setUsername(username);
    	user.setPassword(password);
    	user.login(LoginMainActivity.this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(LoginMainActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(LoginMainActivity.this,SplashActivity.class);
				startActivity(intent);
				finish();
			}
			
			@Override
			public void onFailure(int i, String s) {
				// TODO Auto-generated method stub
				showProgress(false);
				Toast.makeText(LoginMainActivity.this, s, Toast.LENGTH_SHORT).show();
			}
		});
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
            Toast.makeText(LoginMainActivity.this, "再单击一下即可退出", Toast.LENGTH_SHORT).show();
            curTimeMills = System.currentTimeMillis();
        } else {
            finish();
        }
    }
    
	@Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    /**
     * Shows the progress UI and hides the login form.
     */
	private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
}
