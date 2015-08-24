package com.ab.health;




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

public class ToolSanWei extends Activity {
	private User user;
	private EditText  inputHeight;
	private TextView xiongweiTV,yaoweiTV,tunweiTV,calButton;
	private Button btnWowmen,btnMen;
	private Boolean sex = false;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tool_sanwei);
		Click click = new Click();
		user = new User();
		inputHeight = (EditText) findViewById(R.id.add_userinfo_height_editText);		
		
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
				
				if(tempHeight.length() <=0 )
				{
					Toast.makeText(getApplicationContext(), "输入不能为空", Toast.LENGTH_SHORT).show();
					break;
				}
				user.setHeight(Float.valueOf(tempHeight));				
				user.setSex(sex);
				double tunwei = HealthUtility.tunwei(user);
				double xiongwei = HealthUtility.xiongwei(user);
				double yaowei = HealthUtility.yaowei(user);
				xiongweiTV = (TextView) findViewById(R.id.tool_standard_xiongwei);
				xiongweiTV.setText(String.valueOf((int)xiongwei));
				yaoweiTV = (TextView) findViewById(R.id.tool_standard_yaowei);
				yaoweiTV.setText(String.valueOf((int)yaowei));
				tunweiTV = (TextView) findViewById(R.id.tool_standard_tunwei);
				tunweiTV.setText(String.valueOf((int)tunwei));
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
