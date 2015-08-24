package com.ab.health;

import com.ab.health.model.User;
import com.ab.health.utility.HealthUtility;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ToolConvert extends Activity {
	EditText weightET,heightET;
	float result;
	TextView calResult;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tool_convert);
		
		heightET = (EditText) findViewById(R.id.add_userinfo_height_editText);
		
		TextView cal = (TextView) findViewById(R.id.add_userinfo_next_text);
		calResult = (TextView) findViewById(R.id.tool_standard_weight);
		cal.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				int height =  Integer.valueOf( heightET.getText().toString() );
			
				result = 7700 * height;
				calResult.setText(String.valueOf((int)result));
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
