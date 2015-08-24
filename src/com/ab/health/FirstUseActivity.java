package com.ab.health;

import java.util.Date;

import com.ab.health.R.color;
import com.ab.health.utility.AppSetting;
import com.ab.health.utility.HttpGetData;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class FirstUseActivity extends Activity {

	private ViewFlipper containView;
	private LayoutInflater inflater;
	private String userName,passWord,confirmPassWord,url,param,dateString;
	private String height,weight,target,age,requestCode,days,nicename,xuetang,xueya_shu,xueya_shou;
	private EditText et2,et3,et,heightET,weightET,targetET,ageET,requestCodeET,daysET,nicenameET;
	private EditText xuetangET, xuezhiET, xueya_shuET, xueya_shouET;
	private int sex=0;
	private Button btnWowmen,btnMen,btnRquestCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_firstuse);
		OnClick click = new OnClick();
		inflater = getLayoutInflater();
		containView = (ViewFlipper) findViewById(R.id.firstuse_fragment);
		
		View page1 = inflater.inflate(R.layout.activity_account_login, containView);
		View page2 = inflater.inflate(R.layout.activity_fill_body_info, containView);
		View pagePlain = inflater.inflate(R.layout.activity_plain, containView);
		
		et = (EditText) page1.findViewById(R.id.regist_account_edittext);
		nicenameET = (EditText) page1.findViewById(R.id.regist_username_edittext);
		et2 = (EditText) page1.findViewById(R.id.regist_password_edittext);
		et3 = (EditText) page1.findViewById(R.id.regist_confirm_password_edittext);
		TextView regNext = (TextView) page1.findViewById(R.id.reg_userinfo_next_text);
		regNext.setOnClickListener(click);
		
		heightET = (EditText) page2.findViewById(R.id.add_userinfo_height_editText);
		weightET = (EditText) pagePlain.findViewById(R.id.add_userinfo_weight_editText);
		
		targetET = (EditText) pagePlain.findViewById(R.id.add_userinfo_weighttarget_editText);
		ageET = (EditText) page2.findViewById(R.id.add_userinfo_age_editText);
		daysET = (EditText) pagePlain.findViewById(R.id.add_userinfo_tian_editText);
		
		xuetangET =  (EditText) page2.findViewById(R.id.xuetang);
		xueya_shuET = (EditText) page2.findViewById(R.id.xueya_shuzhangya);
		xueya_shouET = (EditText) page2.findViewById(R.id.xueya_shousuoya);
		
		Button back = (Button) findViewById(R.id.add_userinfo_back_button);
		back.setOnClickListener(click);
		
		TextView btnPre = (TextView) page2.findViewById(R.id.add_userinfo_back_text);
		btnPre.setOnClickListener(click);
		TextView btnNext = (TextView) page2.findViewById(R.id.add_userinfo_next_text);
		btnNext.setOnClickListener(click);
		
		TextView btnPre2 = (TextView) page2.findViewById(R.id.add_userinfo_back_text2);
		btnPre2.setOnClickListener(click);
		TextView btnNext2 = (TextView) page2.findViewById(R.id.add_userinfo_next_text2);
		btnNext2.setOnClickListener(click);
		
		btnRquestCode = (Button) findViewById(R.id.reg_request_code);
		btnRquestCode.setOnClickListener(click);
		
		requestCodeET = (EditText) findViewById(R.id.regist_requestcode_edittext);
		
		btnWowmen = (Button) findViewById(R.id.add_userinfo_sex_woman);
		btnMen = (Button) findViewById(R.id.add_userinfo_sex_man);
		btnWowmen.setOnClickListener(click);
		btnMen.setOnClickListener(click);
		
	}
	
	private class RequestCodeAysnTask extends AsyncTask<Object, Integer, Integer>{
		String pass;		
		HttpGetData http = new HttpGetData();
		@Override
		protected void onPreExecute() {		
		}
		
		@Override
		protected Integer doInBackground(Object... params) {		
			url= AppSetting.getRootURL() + "rquestcode.php";
			param = "?username=" + userName ;
			pass = http.HttpGets(url, param);	
			Log.i("request", pass);
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if(pass.equals("1")){				
				Toast.makeText(getApplicationContext(),"该手机号码已存在，请使用其它号码",Toast.LENGTH_SHORT).show();
			}
		}

	}
	
	private class CheckCodeAysnTask extends AsyncTask<Object, Integer, Integer>{
		String pass;	
		HttpGetData http = new HttpGetData();
		@Override
		protected void onPreExecute() {		
		}
		
		@Override
		protected Integer doInBackground(Object... params) {		
			url= AppSetting.getRootURL() + "checkcode.php";
			param = "?username=" + userName + "&requestcode=" + requestCode ;
			pass = http.HttpGets(url, param);		
			Log.i("check", pass);
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if(pass.equals("1")){				
				Toast.makeText(getApplicationContext(),"验证码错误，请再次核对",Toast.LENGTH_SHORT).show();
				return ;
			}
			containView.showNext();
		}
	}
	
	
	private class SubmitAysnTask extends AsyncTask<Object, Integer, Integer>{
		
		String pass;
		HttpGetData http = new HttpGetData();
		@Override
		protected void onPreExecute() {		
			url= AppSetting.getRootURL() + "register.php";
			//?name=13884372321&pd=zhaige&weight=70&target=69&sex=1&height=172&age=30
			param = "?name=" + userName + "&pd=" + passWord +"&weight=" +
					weight + "&target=" + target + "&sex=" + sex + "&nicename=" + nicename +
					"&age=" + age + "&height=" + height + "&days=" + days + "&xuetang=" + xuetang +
					"&xueyashou=" + xueya_shou + "&xueyashu=" + xueya_shu;
			Log.i("param", param);
		}
		
		@Override
		protected Integer doInBackground(Object... params) {					
			pass = http.HttpGets(url, param);	
			
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer result) {	
			finish();
			writeAppConfig();
			Intent intentWeight = new Intent(FirstUseActivity.this,PingGuActivity.class);
			startActivity(intentWeight);	
		}

	}
	
	void writeAppConfig(){
		SharedPreferences appSetting = getSharedPreferences(AppSetting.getSettingFile(), MODE_PRIVATE);
		Editor editor = appSetting.edit();
		editor.putBoolean("NoRegister", false);	
		editor.putString("username", userName);
		editor.putString("nicename", nicename);
		editor.putString("height", height);
		editor.putString("weight", weight);
		editor.putString("target", target);
		editor.putString("days", days);
		editor.putString("password", passWord);
		editor.putString("age", age);
		editor.putInt("sex", sex);
		editor.putString("xuetang", xuetang);
		editor.putString("xueyashu", xueya_shu);
		editor.putString("xueyashou", xueya_shou);
		editor.commit();
	}
	
	void getInputInfo(){
		//  1 page
		userName = et.getText().toString();
		passWord = et2.getText().toString();
		confirmPassWord = et3.getText().toString();
		requestCode = requestCodeET.getText().toString();
		nicename = nicenameET.getText().toString();
		// 2 page
		height = heightET.getText().toString();
		age = ageET.getText().toString();
		
	}
	
	class OnClick implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			getInputInfo();			
			switch (v.getId()) {
			case R.id.reg_userinfo_next_text:				
				if(userName.length()<=0 || nicename.length()<=0 ||passWord.length()<=0 || confirmPassWord.length()<=0 || requestCode.length() <= 0 ){
					Toast.makeText(getApplicationContext(), "用户名、密码、验证码不能为空", Toast.LENGTH_SHORT).show();
					break;
				}else{
					if(passWord.equals(confirmPassWord)){
						
					}else {
						Toast.makeText(getApplicationContext(), "两次密码输入不一样", Toast.LENGTH_SHORT).show();
						break;
					}					
				}
				if(requestCode.length() <= 0){
					Toast.makeText(getApplicationContext(), "验证码不能为空", Toast.LENGTH_SHORT).show();	
					break;
				}else {
					CheckCodeAysnTask check = new CheckCodeAysnTask();
					check.execute(0);
				}
				break;
			case R.id.add_userinfo_back_text:
				containView.showPrevious();				
				break;
			case R.id.add_userinfo_back_text2:
				containView.showPrevious();				
				break;
			case R.id.add_userinfo_next_text:
				if(height.length() <= 0 || age.length() <= 0){
					Toast.makeText(getApplicationContext(), "身高、年龄输入不能为空", Toast.LENGTH_SHORT).show();	
				}else {
					containView.showNext();
				}
				break;
			case R.id.add_userinfo_next_text2:				
				weight = weightET.getText().toString();				
				target = targetET.getText().toString();
				days = daysET.getText().toString();
				xuetang = xuetangET.getText().toString();
				xueya_shu = xueya_shuET.getText().toString();
				xueya_shou = xueya_shouET.getText().toString();
				
				if(weight.length() <= 0 || target.length() <= 0 || days.length() <= 0){
					Toast.makeText(getApplicationContext(), "输入不能为空", Toast.LENGTH_SHORT).show();	
				}else {
					if(!targetET.getText().toString().equals("")){
						target = targetET.getText().toString();		
					}
					if(!daysET.getText().toString().equals("")){
						days = daysET.getText().toString();	
					}
					if(!weightET.getText().toString().equals("")){
						weight = weightET.getText().toString();
					}
					float targetInput = Float.valueOf(target);
					float weightInput = Float.valueOf(weight);
					/*int targetInput = (int)targetInputT;
					int weightInput = (int)weightInputT;*/
					if(weightInput <= targetInput){
						Toast.makeText(getApplicationContext(), "目标体重不能大于或等于现在体重", Toast.LENGTH_SHORT).show();
						break;
					}
					int day = Integer.valueOf(days);
					if(day <= 0){
						Toast.makeText(getApplicationContext(), "减肥周期天数不能小于或等于0", Toast.LENGTH_SHORT).show();
						break;
					}
					Toast.makeText(getApplicationContext(), "正在提交注册，请稍后", Toast.LENGTH_SHORT).show();
					SubmitAysnTask submit = new SubmitAysnTask();
					submit.execute(0);
				}		
				break;
			case R.id.add_userinfo_back_button:
				finish();
				break;
			case R.id.reg_request_code:
				if(userName.length() == 0){
					Toast.makeText(getApplicationContext(), "请先输入手机号", Toast.LENGTH_SHORT).show();
					break;
				}
				btnRquestCode.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "验证码已发出，请稍候", Toast.LENGTH_SHORT).show();
				RequestCodeAysnTask requestCode = new RequestCodeAysnTask();
				requestCode.execute(0);
				break;
				
			case R.id.add_userinfo_sex_woman:
				btnWowmen.setBackgroundResource(R.drawable.pk_group_left_press);
				btnMen.setBackgroundResource(R.drawable.button_right_normal);
				btnWowmen.setTextColor(color.white);
				btnMen.setTextColor(color.black);
				sex =0;
				break;
			case R.id.add_userinfo_sex_man:
				btnWowmen.setBackgroundResource(R.drawable.button_left_normal);
				btnWowmen.setTextColor(color.black);
				btnMen.setBackgroundResource(R.drawable.pk_group_right_press);
				btnMen.setTextColor(color.white);		
				sex = 1;
				break;
			default:
				break;
			}
			
		}
		
	}

}
