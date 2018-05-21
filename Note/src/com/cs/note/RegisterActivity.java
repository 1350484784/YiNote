package com.cs.note;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.SaveListener;

/**
 * 注册界面
 * @author Carey
 *
 */
public class RegisterActivity extends Activity{

	Button confirmbButton;
	Button cancel;
	EditText username;
	EditText password;
	EditText confirmpassword;
	EditText phone;
	EditText email;
	
	boolean userFlag = false;//用户是否已存在
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		
		//注册用户
		confirmbButton = (Button) findViewById(R.id.confirm);
		confirmbButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				username = (EditText) findViewById(R.id.register_username);
				password = (EditText) findViewById(R.id.register_password);
				confirmpassword = (EditText) findViewById(R.id.register_confirmpassword);
				phone = (EditText) findViewById(R.id.register_phone);
				email = (EditText) findViewById(R.id.register_email);
				
				String userName = username.getText().toString();
				String pwd = password.getText().toString();
				String confirm_pwd = confirmpassword.getText().toString();
				String userPhone = phone.getText().toString();
				String userEmail = email.getText().toString();
				
				//判断
				if(userName != null && !userName.equals("")){
					//用户不存在
					if(!findUser(userName)){
						if(pwd != null && !pwd.equals("")){
							if(confirm_pwd.equals(pwd)){
								if(userPhone !=null && !userPhone.equals("")){
									if(userEmail !=null && !userEmail.equals("")){
										//注册
										signupBmob(userName, pwd, userPhone, userEmail);
									}else{
										Toast.makeText(RegisterActivity.this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
									}
								}else{
									Toast.makeText(RegisterActivity.this, "电话不能为空", Toast.LENGTH_SHORT).show();
								}
							}else{
								Toast.makeText(RegisterActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
								password.setText("");
								confirmpassword.setText("");
							}
						}else {
							Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
						}
					}else {
						Toast.makeText(RegisterActivity.this, "用户名已存在！！！", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		//取消注册
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 查找用户
	 * @param name
	 * @return
	 */
	private boolean findUser(String name){
		
		@SuppressWarnings("rawtypes")
		BmobQuery query = new BmobQuery(name);
		query.findObjects(RegisterActivity.this, new FindCallback() {
			
			@Override
			public void onFailure(int i, String s) {
				// TODO Auto-generated method stub
				userFlag = false;
			}
			
			@Override
			public void onSuccess(JSONArray jsonArray) {
				// TODO Auto-generated method stub
				userFlag =  true;
			}
		});
		return userFlag;
	}
	
	/**
	 * 在Bmob 注册数据
	 * @param name
	 * @param pwd
	 * @param phone
	 * @param email
	 */
	private void signupBmob(final String name, String pwd, String phone, String email){
		BmobUser user = new BmobUser();
		user.setUsername(name);
		user.setPassword(pwd);
		user.setMobilePhoneNumber(phone);
		user.setEmail(email);
		user.signUp(RegisterActivity.this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(RegisterActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(RegisterActivity.this,SplashActivity.class);
				intent.putExtra(SplashActivity.SEND_USER_NAME, name);
				startActivity(intent);
				finish();
			}
			
			@Override
			public void onFailure(int i, String s) {
				// TODO Auto-generated method stub
				Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
}
