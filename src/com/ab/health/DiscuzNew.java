package com.ab.health;

import com.ab.health.utility.AppSetting;
import com.ab.health.utility.HttpGetData;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DiscuzNew extends Activity {
	EditText contentET;
	float result;
	TextView calResult;
	private String nickname,content,username;
	private HttpGetData httpData;
	private String url,param;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discuz_new);
		
		SharedPreferences appSetting = getSharedPreferences(AppSetting.getSettingFile(), MODE_PRIVATE);
		nickname = appSetting.getString("nicename", "");
		username = appSetting.getString("username", "");
		contentET = (EditText) findViewById(R.id.msg_et);
		
		TextView cal = (TextView) findViewById(R.id.add_userinfo_next_text);		
		cal.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				content =  contentET.getText().toString();
				SubmitAysnTask submit = new SubmitAysnTask();
				submit.execute(0);
				finish();
			}
		});
		
		Button back = (Button) findViewById(R.id.tool_standardweight_back_btn);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
	}
	
	
	private class SubmitAysnTask extends AsyncTask<Object, Integer, Integer>{
		int ret;
		@Override
		protected Integer doInBackground(Object... params) {	
			url = AppSetting.getRootURL() + "discuz_submit.php";			
			param = "?nickname=" + nickname + "&content=" + content + "&username=" + username;
			httpData = new HttpGetData();
			httpData.HttpGets(url, param);
			return ret;
		}

		@Override
		protected void onPostExecute(Integer result) {			
			
		}

	}

}
