package com.ab.health;

import java.net.URL;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ab.health.utility.AppSetting;
import com.ab.health.utility.HttpGetData;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GongGaoDetailActivity extends Activity {

	
	private TextView contentTV;
	private String contentString,title,date;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gonggaodetail);
		Intent intent = getIntent();
		title = "";
		date = "未知";
		title = intent.getStringExtra("title");
		date = intent.getStringExtra("date");
		
		int count = intent.getIntExtra("count", 1);
		
		/*TextView titleTv = (TextView) findViewById(R.id.act_recordCal_title_tv);
		titleTv.setText(title);
		TextView dateTv = (TextView) findViewById(R.id.act_recordCal_name_tv);
		dateTv.setText(date);*/
		contentTV = (TextView) findViewById(R.id.gonggao_content);
		LoadGongGaoDetailAysnTask load = new LoadGongGaoDetailAysnTask();
		load.execute(count);
		
		
		BtnClick btnClick =new BtnClick();
		Button btnBack = (Button) findViewById(R.id.act_recordCal_back_btn);
		btnBack.setOnClickListener(btnClick);
		
		 
	}
	
	class BtnClick implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.act_recordCal_back_btn:
				finish();
				break;
			default:
				break;
				
				
			}
		}
		
	}
	
	
	
	private class LoadGongGaoDetailAysnTask extends AsyncTask<Object, Integer, Integer>{
		int ret;
		

		@Override
		protected void onPreExecute() {	
		}
		
		@Override
		protected Integer doInBackground(Object... params) {
			
			HttpGetData httpData = new HttpGetData();
			String url = AppSetting.getRootURL() + "gonggaodetail.php";
			int id = (Integer) params[0];
			
			String param = "?id="+id;		
			contentString = "<br><b><font size='1'>" + title + "</font></b><p><font color='gray'>发表时间：&nbsp;&nbsp;" + date + "</font><p>";
			contentString +=	httpData.HttpGets(url,param);
			
			return ret;
		}

		@Override
		protected void onPostExecute(Integer result) {
			
			contentTV.setText(Html.fromHtml(contentString));
			
		}

	}
	
	
}
