package com.ab.health.ble;

import java.util.Calendar;

import com.ab.health.R;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BluetoothResult extends Activity {
	private String TAG ="BluetoothResult"; 
	private int stepsSum=0;
	private float distanceSum=0,calorieSum=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_result);
		
		db();
		
		Button back = (Button) findViewById(R.id.act_recordCal_back_btn);
		back.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
		TextView setpsTV = (TextView) findViewById(R.id.add_userinfo_height_unit);
		setpsTV.setText(String.valueOf(stepsSum));
		TextView distanceTV = (TextView) findViewById(R.id.add_userinfo_height_unit2);
		distanceTV.setText(String.valueOf((int)distanceSum));
		TextView calorieTV = (TextView) findViewById(R.id.add_userinfo_height_unit3);
		calorieTV.setText(String.valueOf((int)calorieSum));
	}

   
	
	 private void db(){
			SQLiteDatabase db = openOrCreateDatabase("health", Context.MODE_PRIVATE, null);		
			
			Calendar now = Calendar.getInstance();
	    	int yearNow = now.get(Calendar.YEAR) - 2000;
	    	int monthNow = now.get(Calendar.MONTH) + 1;
	    	int dayNow = now.get(Calendar.DAY_OF_MONTH) ;
	    	//year INTEGER, month INTEGER,day INTEGER
			String sql = "SELECT * FROM record_sports where year=" + yearNow + " and month=" + monthNow +" and day=" + dayNow + " and category=1";
			Cursor c = db.rawQuery(sql, null);
			while( c.moveToNext()){
				stepsSum += c.getInt(c.getColumnIndex("setps"));
				distanceSum += c.getFloat(c.getColumnIndex("distance"));
				calorieSum += c.getFloat(c.getColumnIndex("calorie"));
			}
			
			db.close();
		}
}


