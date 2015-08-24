package com.ab.health;

import com.ab.health.model.User;
import com.ab.health.utility.HealthUtility;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ToolHealthWeight extends Activity {
	EditText weightET,heightET;
	float result;
	TextView calResult;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tool_health_weight);
		
		heightET = (EditText) findViewById(R.id.add_userinfo_height_editText);
		
		TextView cal = (TextView) findViewById(R.id.add_userinfo_next_text);
		calResult = (TextView) findViewById(R.id.tool_health_weight);
		cal.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				float height =  Float.valueOf( heightET.getText().toString() );
				User user = new User();
				user.setHeight(height);
				int low  = (int)HealthUtility.standardHealthWeight(user, true);
				int hight = (int)HealthUtility.standardHealthWeight(user, false);
				
				calResult.setText(String.valueOf(low) + "~" + String.valueOf(hight) + "公斤");
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

}
