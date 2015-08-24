package com.ab.health;

import com.ab.health.model.User;
import com.ab.health.utility.AppSetting;
import com.ab.health.utility.HealthUtility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.provider.SyncStateContract.Helpers;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HomeActivity extends Activity {
	private String username,nicename,height,weight,age,target,days;
	//private String bmi,bmr,standardweight,healthweight;
	private TextView bmiTV, bmrTV,standardweightTV,healthweightTV,weightTV;
	private boolean sex;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_body_info);
		bmiTV = (TextView) findViewById(R.id.person_bmi);
		weightTV = (TextView) findViewById(R.id.person_weight);
		healthweightTV = (TextView) findViewById(R.id.person_health);
		standardweightTV = (TextView) findViewById(R.id.person_standard);
		bmrTV = (TextView) findViewById(R.id.person_bmr);
		
		SharedPreferences appSetting = getSharedPreferences(AppSetting.getSettingFile(), MODE_PRIVATE);
				
		username = appSetting.getString("username", "");
		nicename = appSetting.getString("nicename", "");
		height = appSetting.getString("height", "");
		weight = appSetting.getString("weight", "");
		target = appSetting.getString("target", "");
		
		days = appSetting.getString("days", "");
		age = appSetting.getString("age", "");
		int sexTemp = appSetting.getInt("sex", 0);
		
		
		
		
		
		if(sexTemp == 1){
			sex = true;
		}else {
			sex = false;
		}
		
		int ageTemp  = Integer.valueOf(age);
		int heightTemp = Integer.valueOf(height);
		float weightTemp =  Float.valueOf(weight);
		
		User user = new User(sex, ageTemp, weightTemp, heightTemp);
		String _bmi = String.valueOf((int)HealthUtility.calBMI(user));
		String _bmr = String.valueOf(HealthUtility.calBMR(user));
		String _tixing = String.valueOf(HealthUtility.boolHealth(user));
		String _standard = String.valueOf(HealthUtility.calStanderWeight(user));
		String _health_low = String.valueOf((int)HealthUtility.standardHealthWeight(user, true));
		String _health_high = String.valueOf((int)HealthUtility.standardHealthWeight(user, false));
		
		bmiTV.setText(_bmi);
		bmrTV.setText(_bmr);
		standardweightTV.setText(_standard);
		healthweightTV.setText(_health_low + "~" + _health_high);
		weightTV.setText(_tixing);
		
		Button back = (Button) findViewById(R.id.act_recordCal_back_btn);
		back.setOnClickListener(new View.OnClickListener() {
			 
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
				
			}
		});
		
		TextView change = (TextView) findViewById(R.id.changeInfo);
		change.setOnClickListener(new View.OnClickListener() {
			 
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, ChangeInfoActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);
				
			}
		});
		
	}
	
	

}
