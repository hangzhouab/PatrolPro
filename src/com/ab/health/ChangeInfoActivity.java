package com.ab.health;




import com.ab.health.model.User;
import com.ab.health.utility.AppSetting;
import com.ab.health.utility.HealthUtility;
import com.ab.health.utility.HttpGetData;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeInfoActivity extends Activity {
	private User user;
	private EditText inputAge, inputHeight,inputNicename;
	private TextView standardWeight,calButton;
	private Button btnWowmen,btnMen;
	private Boolean sex = false;
	private int sexTemp;
	private String username,url,param,password,xuetang,xueya_shu,xueya_shou;
	private String height,weight,target,age,days,nicename;

	private EditText xuetangET, xueya_shuET, xueya_shouET;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changeinfo);		

		SharedPreferences appSetting = getSharedPreferences(AppSetting.getSettingFile(), MODE_PRIVATE);
		username = appSetting.getString("username", "");
		nicename = appSetting.getString("nicename", "");
		height = appSetting.getString("height", "");
		weight = appSetting.getString("weight", "");
		target = appSetting.getString("target", "");
		xuetang = appSetting.getString("xuetang", "");
		xueya_shou = appSetting.getString("xueyashou", "");
		xueya_shu = appSetting.getString("xueyashu", "");
		days = appSetting.getString("days", "");
		password = appSetting.getString("password", "");
		age = appSetting.getString("age", "");
		sexTemp = appSetting.getInt("sex", 0);
		if(sexTemp == 1){
			sex = true;
		}else {
			sex = false; 
		}
		Click click = new Click();
		user = new User();
		
		xuetangET =  (EditText) findViewById(R.id.xuetang);
		xuetangET.setHint(xuetang);
		xueya_shuET = (EditText) findViewById(R.id.xueya_shuzhangya);
		xueya_shuET.setHint(xueya_shu);
		xueya_shouET = (EditText) findViewById(R.id.xueya_shousuoya);
		xueya_shouET.setHint(xueya_shou);
		inputHeight = (EditText) findViewById(R.id.add_userinfo_height_editText);
		inputHeight.setHint(height);
		inputAge = (EditText) findViewById(R.id.tool_userinfo_age_editText);
		inputAge.setHint(age);		
		calButton = (TextView) findViewById(R.id.add_userinfo_next_text);
		btnWowmen = (Button) findViewById(R.id.add_userinfo_sex_woman);
		btnMen = (Button) findViewById(R.id.add_userinfo_sex_man);
		btnWowmen.setOnClickListener(click);
		btnMen.setOnClickListener(click);
		calButton.setOnClickListener(click);	
		inputNicename = (EditText) findViewById(R.id.add_userinfo_nicename_editText);		
		inputNicename.setHint(nicename);
		
		
		
		Button back = (Button) findViewById(R.id.tool_standardweight_back_btn);
		back.setOnClickListener(click);
	}
	
	private class SubmitAysnTask extends AsyncTask<Object, Integer, Integer>{
		
		String pass;
		HttpGetData http = new HttpGetData();
		@Override
		protected void onPreExecute() {		
			url= AppSetting.getRootURL() + "changeinfo.php";
			//?name=13884372321&pd=zhaige&weight=70&target=69&sex=1&height=172&age=30
			param = "?name=" + username + "&pd=" + password +"&weight=" +
					weight + "&target=" + target + "&sex=" + sex + "&nicename=" + nicename +
					"&age=" + age + "&height=" + height + "&days=" + days + "&xuetang=" + xuetang +
					"&xueyashou=" + xueya_shou + "&xueyashu=" + xueya_shu;
			Toast.makeText(getApplicationContext(), "正在提交更新，请稍后", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		protected Integer doInBackground(Object... params) {					
			pass = http.HttpGets(url, param);
			Log.i("param", param); 
			Log.i("reg", pass); 
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer result) {		
			if(pass.equals("0")){
				writeAppConfig();
				finish();
				return ;
			}else{
				Toast.makeText(getApplicationContext(), "提交更新失败", Toast.LENGTH_SHORT).show();
			}			
		}

	}
	
	
	void writeAppConfig(){
		SharedPreferences appSetting = getSharedPreferences(AppSetting.getSettingFile(), MODE_PRIVATE);
		Editor editor = appSetting.edit();
		editor.putString("nicename", nicename);
		editor.putString("height", height);
		editor.putString("weight", weight);
		editor.putString("target", target);
		editor.putString("days", days);
		editor.putString("age", age);
		editor.putInt("sex", sexTemp);
		editor.putString("xuetang", xuetang);
		editor.putString("xueyashu", xueya_shu);
		editor.putString("xueyashou", xueya_shou);
		editor.commit();
	}
	
	class Click implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tool_standardweight_back_btn:
				finish();
				overridePendingTransition(R.anim.slide_in_from_top, R.anim.slide_out_to_bottom);
				break;
			case R.id.add_userinfo_next_text:
				
				
				if(!inputHeight.getText().toString().equals("")){
					height = inputHeight.getText().toString();		
				}
				if(!inputAge.getText().toString().equals("")){
					age = inputAge.getText().toString();	
				}
				if(!inputNicename.getText().toString().equals("")){
					nicename = inputNicename.getText().toString();
				}		
				
				if(!xuetangET.getText().toString().equals("")){
					xuetang = xuetangET.getText().toString();
				}	
				if(!xueya_shuET.getText().toString().equals("")){
					xueya_shu = xueya_shuET.getText().toString();
				}	
				if(!xueya_shouET.getText().toString().equals("")){
					xueya_shou = xueya_shouET.getText().toString();
				}	
				SubmitAysnTask change = new SubmitAysnTask();
				change.execute(0);
				break;
			case R.id.add_userinfo_sex_woman:
				btnWowmen.setBackgroundResource(R.drawable.pk_group_left_press);
				btnMen.setBackgroundResource(R.drawable.button_right_normal);
				btnWowmen.setTextColor(Color.WHITE);
				btnMen.setTextColor(Color.BLACK);
				sex=false;
				break;
			case R.id.add_userinfo_sex_man:
				btnWowmen.setBackgroundResource(R.drawable.button_left_normal);
				btnWowmen.setTextColor(Color.BLACK);
				btnMen.setBackgroundResource(R.drawable.pk_group_right_press);
				btnMen.setTextColor(Color.WHITE);	
				sex=true;
				break;
			
			default:
				break;
			}
			
		}
		
	}
	
	

}
