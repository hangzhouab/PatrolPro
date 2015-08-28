package com.ab.health;


import com.ab.health.utility.AppSetting;
import com.ab.health.utility.HttpGetData;

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
import android.widget.Toast;


public class RegisterActivity extends Activity {

	private EditText userName,passWord,confirmPassWord,unit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		userName = (EditText) findViewById(R.id.login_account_edittext);
		passWord = (EditText) findViewById(R.id.login_password_edittext);
		confirmPassWord = (EditText) findViewById(R.id.login_password_edittext2);
		unit = (EditText) findViewById(R.id.login_unit_edittext);		
		
		Button btn_register = (Button) findViewById(R.id.login_sumbit_button);
		btn_register.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(userName.length()<=0 ||passWord.length()<=0 || confirmPassWord.length()<=0 || unit.length() <= 0){
					Toast.makeText(getApplicationContext(), "用户名、密码、单位不能为空", Toast.LENGTH_SHORT).show();
					return;
				}else{
					if(!passWord.getText().toString().equals(confirmPassWord.getText().toString())){
						Toast.makeText(getApplicationContext(), "两次密码输入不一样", Toast.LENGTH_SHORT).show();	
						return;
					}
					RegisterAysnTask loginAysn = new RegisterAysnTask();
					loginAysn.execute(0);					
				}				
			}			
		});
		
	}
	

	
private class RegisterAysnTask extends AsyncTask<Object, Integer, Integer>{
		
		String res,url,param;
		
		HttpGetData http = new HttpGetData();
		@Override
		protected void onPreExecute() {		
			url= AppSetting.getRootURL() + "register.php";
			//?name=13884372321&pd=zhaige&weight=70&target=69&sex=1&height=172&age=30
			param = "?name=" + userName.getText().toString() + "&pd=" + passWord.getText().toString() +"&unit=" +
					unit.getText().toString();
			Log.i("param", param);
			res = "init";
		}
		
		@Override
		protected Integer doInBackground(Object... params) {					
			res = http.HttpGets(url, param);			
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer result) {	
			if( res == null){
				Log.i("res", "res返回异常");
				return;
			}
			
			
			if(res.equals("noUnit")){
				Toast.makeText(getApplicationContext(), "该单位不存在，请联系系统管理员", Toast.LENGTH_SHORT).show();	
				return;
			}else if (res.equals("ok")) {
				writeAppConfig();				
				Intent intentWeight = new Intent(RegisterActivity.this,MainActivity.class);
				startActivity(intentWeight);	
				finish();
			}else {
				Toast.makeText(getApplicationContext(), "网络连接异常，请重试", Toast.LENGTH_SHORT).show();	
			}
		}
	}
	
	void writeAppConfig(){
		SharedPreferences appSetting = getSharedPreferences(AppSetting.getSettingFile(), MODE_PRIVATE);
		Editor editor = appSetting.edit();
		editor.putBoolean(AppSetting.isRegister, true);	
		editor.putString(AppSetting.username, userName.getText().toString());
		editor.putString(AppSetting.orgnization, unit.getText().toString());		
		editor.commit();
	}
	
}
