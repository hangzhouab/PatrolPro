package com.ab.health;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ab.health.utility.AppSetting;
import com.ab.health.utility.DensityUtil;
import com.ab.health.utility.HttpGetData;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class SportsRecordActivity extends Activity {

	private LinearLayout weightRecordly;
	private ArrayList<View> weightRecordList = new ArrayList<View>();
	private String url,param,ret;
	private HttpGetData commitDate;
	private LayoutInflater flater;
	private int calorieSum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sports_record);
		flater = getLayoutInflater();
		weightRecordly = (LinearLayout) findViewById(R.id.act_physiology_canvas_image_layout);
		db();
		Button back = (Button) findViewById(R.id.act_recordCal_back_btn);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		
	}
	
	
	private void db(){
		SQLiteDatabase db = openOrCreateDatabase("health", Context.MODE_PRIVATE, null);		
		
		Calendar now = Calendar.getInstance();
    	int yearNow = now.get(Calendar.YEAR) - 2000;
    	int monthNow = now.get(Calendar.MONTH) + 1;
    	int dayNow = now.get(Calendar.DAY_OF_MONTH) ;
		boolean result = false;
		float sum = 0;
		int day,dayTemp = dayNow,monthTemp = monthNow,monthPlus=0,month = monthNow,year = yearNow;
		for(int i=0; i < 30; i++){
			day = dayTemp - i;	
			if(day <= 0){
				dayTemp = dayNow + 31;
				day = 31;
				monthPlus = +1;
				monthTemp -= monthPlus ;
				month = monthTemp;
				if(monthTemp <= 0){
					monthPlus = 0;
					monthTemp = 12;
					month = monthTemp;
					year = yearNow - 1;					
				}
			}
			String sql3 = "SELECT * FROM record_sports where year=" + year + " and month=" + month +" and day=" + day;	
			
			String date = String.valueOf(month)+ "." +String.valueOf(day);
			Cursor c2 = db.rawQuery(sql3, null);
			while(  c2.moveToNext()){
				result = true;
				sum += c2.getFloat(c2.getColumnIndex("calorie"));		
				 
			}
			if(result){
				int temp = (int)sum;
				draw(temp,date);
				sum = 0;
				result = false;
			}
			
		}
		db.close();
	}
	
	private void draw(int height,String dateinput){
		View tempView = flater.inflate(R.layout.view_physiology_image_item, null);
		TextView tempDate = (TextView) tempView.findViewById(R.id.act_physiology_physiology_date);
		TextView tempHeight = (TextView) tempView.findViewById(R.id.act_physiology_canvas_weight);
		
		
		String weight = String.valueOf(height);
		String date = dateinput;		
		float weighttemp =Float.valueOf(weight);
		float convertNumber = 0.0258f; //  (129 / 5000) * input
		weighttemp *= convertNumber;
		if(weighttemp > 129)
			weighttemp = 129;				
		int dpHeighttemp = DensityUtil.px2dip(getApplicationContext(), 135 - weighttemp );
		int dpHeight = DensityUtil.dip2px(getApplicationContext(), dpHeighttemp);
		RelativeLayout.LayoutParams paramheight = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		paramheight.topMargin = dpHeight;
	
		tempHeight.setLayoutParams(paramheight);
		tempHeight.setText(weight);
		tempDate.setText(date);
		weightRecordly.addView(tempView);
	}

}
