package com.ab.health;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.ab.health.utility.HttpGetData;
import com.ab.health.utility.AppSetting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private String url,param,username,password,orgniztion;
	
	
	
	private EditText userName,passWord;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		isFirstUse();
		
		userName = (EditText) findViewById(R.id.login_account_edittext);
		passWord = (EditText) findViewById(R.id.login_password_edittext);
		
		

		TextView reg = (TextView) findViewById(R.id.login_jump_textview);
		reg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
		
		
		Button login = (Button) findViewById(R.id.login_sumbit_button);
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(userName.length()<=0 ||passWord.length()<=0 ){
					Toast.makeText(getApplicationContext(), "用户名、密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}else{									
					LoginAysnTask loginAysn = new LoginAysnTask();
					loginAysn.execute(0);					
				}	
			}
		});
	}
	
	private void isFirstUse() {
		SharedPreferences appSetting = getSharedPreferences( AppSetting.getSettingFile(), MODE_PRIVATE);		
		if (appSetting.getBoolean(AppSetting.isRegister, false)) {			
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}
	
	
	private class LoginAysnTask extends AsyncTask<Object, Integer, Integer>{
		String pass;		
		HttpGetData http = new HttpGetData();
		@Override
		protected void onPreExecute() {		
		}
		
		@Override
		protected Integer doInBackground(Object... params) {	
			username = userName.getText().toString();
			password = passWord.getText().toString();
			url= AppSetting.getRootURL() + "login.php";
			param = "?username=" + username + "&password=" + password ;
			pass = http.HttpGets(url, param);				
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if(pass == null){
				Toast.makeText(getApplicationContext(),"网络异常，请重试",Toast.LENGTH_SHORT).show();
				return;
			}
			if(pass.equals("1")){				
				Toast.makeText(getApplicationContext(),"该账户不存在，请先注册",Toast.LENGTH_SHORT).show();
				return;
			}
			if(pass.equals("2")){				
				Toast.makeText(getApplicationContext(),"密码不正确",Toast.LENGTH_SHORT).show();
				return;
			}
			JsonHandle(pass);	
			writeAppConfig();			
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);			
			finish();
		}
	}
	
	
	private Integer JsonHandle(String retResponse) {
		int ret =0;
		try { 
			JSONObject json = new JSONObject(retResponse);
			JSONArray foodsArray = json.getJSONArray("foods");
			for (int i = 0; i < foodsArray.length(); i++) {
				JSONObject temp = (JSONObject) foodsArray.opt(i);
				username = temp.getString("username");
				orgniztion = temp.getString("orgniztion");				
			}			
		} catch (JSONException e) {
			e.printStackTrace();
			ret = 1;
		}
		return ret;
	}
	
	void writeAppConfig(){		
		SharedPreferences appSetting = getSharedPreferences(AppSetting.getSettingFile(), MODE_PRIVATE);		
		Editor editor = appSetting.edit();
		editor.putString(AppSetting.username, username);
		editor.putString(AppSetting.orgnization, orgniztion);		
		editor.putBoolean(AppSetting.isRegister, true);			
		editor.commit();
		Log.i("username", username);
		Log.i("org", orgniztion);
	}
}
