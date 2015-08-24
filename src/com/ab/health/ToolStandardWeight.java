package com.ab.health;




import com.ab.health.R.color;
import com.ab.health.model.User;
import com.ab.health.utility.HealthUtility;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ToolStandardWeight extends Activity {
	private User user;
	private EditText inputAge, inputHeight;
	private TextView standardWeight,calButton;
	private Button btnWowmen,btnMen;
	private Boolean sex = false;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tool_standard_weight);
		Click click = new Click();
		user = new User();
		inputHeight = (EditText) findViewById(R.id.add_userinfo_height_editText);
		inputAge = (EditText) findViewById(R.id.tool_userinfo_age_editText);
		calButton = (TextView) findViewById(R.id.add_userinfo_next_text);
		btnWowmen = (Button) findViewById(R.id.add_userinfo_sex_woman);
		btnMen = (Button) findViewById(R.id.add_userinfo_sex_man);
		btnWowmen.setOnClickListener(click);
		btnMen.setOnClickListener(click);
		calButton.setOnClickListener(click);		
		Button back = (Button) findViewById(R.id.tool_standardweight_back_btn);
		back.setOnClickListener(click);
	}
	
	class Click implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tool_standardweight_back_btn:
				finish();
				break;
			case R.id.add_userinfo_next_text:
				String tempHeight = inputHeight.getText().toString();				
				String tempAge = inputAge.getText().toString();				
				if(tempHeight.length() <=0 || tempAge.length() <= 0)
				{
					Toast.makeText(getApplicationContext(), "输入不能为空", Toast.LENGTH_SHORT).show();
					break;
				}
				user.setHeight(Float.valueOf(tempHeight));
				user.setAge(Integer.valueOf(tempAge));
				user.setSex(sex);
				float weight = HealthUtility.calStanderWeight(user);
				standardWeight = (TextView) findViewById(R.id.tool_standard_weight);
				standardWeight.setText(String.valueOf(weight));
				break;
			case R.id.add_userinfo_sex_woman:
				btnWowmen.setBackgroundResource(R.drawable.pk_group_left_press);
				btnMen.setBackgroundResource(R.drawable.pk_group_right_noaml);
				btnWowmen.setTextColor(Color.WHITE);
				btnMen.setTextColor(Color.BLACK);
				sex=false;
				break;
			case R.id.add_userinfo_sex_man:
				btnWowmen.setBackgroundResource(R.drawable.pk_group_left_nomal);
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
