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

public class ChangePlainActivity extends Activity {
	private User user;
	private EditText inputDays, inputTarget;	
	private TextView standardWeight,calButton,inputWeight;
	private Button btnWowmen,btnMen;
	private Boolean sex = false;
	private int sexTemp;
	private String username,url,param,password,xuetang,xueya_shu,xueya_shou;
	private String height,weight,target,age,days,nicename;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plainchange);		

		SharedPreferences appSetting = getSharedPreferences(AppSetting.getSettingFile(), MODE_PRIVATE);
		username = appSetting.getString("username", "");
		nicename = appSetting.getString("nicename", "");
		height = appSetting.getString("height", "");
		weight = appSetting.getString("weight", "");
		target = appSetting.getString("target", "");
		days = appSetting.getString("days", "");
		xuetang = appSetting.getString("target", "");
		xueya_shou = appSetting.getString("xueyashou", "");
		xueya_shu = appSetting.getString("xueyashu", "");
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
		inputTarget = (EditText) findViewById(R.id.add_userinfo_weighttarget_editText);
		inputTarget.setHint(target);		
		inputDays = (EditText) findViewById(R.id.add_userinfo_tian_editText);
		inputDays.setHint(days);
		inputWeight = (TextView) findViewById(R.id.add_userinfo_weight_editText);
		inputWeight.setText(weight);
		
		TextView commit = (TextView) findViewById(R.id.add_userinfo_next_text2);
		commit.setOnClickListener(click);
		
		TextView back = (TextView) findViewById(R.id.act_recordCal_back_btn);
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
					"&xueyashou=" + xueya_shou + "&xueyashu=" + xueya_shu;;
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
		editor.commit();
	}
	
	class Click implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.act_recordCal_back_btn:
				finish();
				overridePendingTransition(R.anim.slide_in_from_top, R.anim.slide_out_to_bottom);
				break;
			case R.id.add_userinfo_next_text2:
				if(!inputTarget.getText().toString().equals("")){
					target = inputTarget.getText().toString();		
				}
				if(!inputDays.getText().toString().equals("")){
					days = inputDays.getText().toString();	
				}				
				float targetInput = Float.valueOf(target);
				float weightInput = Float.valueOf(weight);
				/*int targetInput = (int)targetInputT;
				int weightInput = (int)weightInputT;*/
				if(weightInput <= targetInput){
					Toast.makeText(getApplicationContext(), "目标体重不能大于或等于现在体重", Toast.LENGTH_SHORT).show();
					break;
				}
				
				int	day = Integer.valueOf(days);
				if(day <= 0){
					Toast.makeText(getApplicationContext(), "减肥周期天数不能小于或等于0", Toast.LENGTH_SHORT).show();
					break;
				}
				
				SubmitAysnTask change = new SubmitAysnTask();
				change.execute(0);
				break;
			
			default:
				break;
			}
			
		}
		
	}
	
	

}
