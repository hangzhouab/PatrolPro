package com.ab.health;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.ab.health.utility.AppSetting;
import com.ab.health.utility.HttpGetData;
import com.ab.nfc.NFCAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class PatrolQueryActivity extends Activity {

	private List<HashMap<String, String>> patrolRecordData;
	private NFCAdapter patrolRecordAdapter;
	private ListView patrolRecordLV;	
	private ProgressBar patrolwaiter;
	private boolean isComplelet = false;
	private String ramUsername,ramOrgniztion;
	private Button back;
	private TextView queryResult;
	private boolean patroltouch=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patrol_query);
		queryResult = (TextView) findViewById(R.id.query_result);
		back = (Button) findViewById(R.id.tool_standardweight_back_btn);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		
		InitPatrolRecordViewFliper();
	}
	
	
	private class LoadPatrolRecordAysnTask extends AsyncTask<Object, Integer, Integer>{
		String loadParam,url;
		int year,month,day;
		@Override
		protected void onPreExecute() {		
			if( isComplelet ){ 
				Toast.makeText(getApplicationContext(), "全部信息已加载完成", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(getApplicationContext(), "正在加载，请稍后...", Toast.LENGTH_SHORT).show();
			}
			if(!patroltouch){
				patrolRecordData.clear();
			}
			Intent intent = getIntent();
			year = intent.getIntExtra("year", 0);
			month = intent.getIntExtra("month", 0);
			month ++;
			day = intent.getIntExtra("day", 0);
			
			url = AppSetting.getRootURL() + "querypatrol.php";
			GetShared();
		} 
		
		@Override
		protected Integer doInBackground(Object... params) {	
			int sId = (Integer) params[0];
			loadParam = "?username="+ramUsername + "&startid="+sId+"&endid=10" +"&year="+year+"&month="+month+"&day="+day + "&unit=" + ramOrgniztion;				
			HttpGetData httpData = new HttpGetData();
			patrolRecordJsonHandle(httpData.HttpGets(url,loadParam));
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {	
			
			patrolwaiter.setVisibility(View.GONE);			
			patrolRecordLV.setVisibility(View.VISIBLE);
			patrolRecordAdapter.notifyDataSetChanged();					
			if(patrolRecordData.size() <= 0){
				queryResult.setVisibility(View.VISIBLE);
			}
		}

	}
	
	private void GetShared() {
		SharedPreferences appSetting = getSharedPreferences(AppSetting.getSettingFile(), MODE_PRIVATE);
		ramUsername = appSetting.getString(AppSetting.username, "");
		ramOrgniztion = appSetting.getString(AppSetting.orgnization, "");	
	}
	
	private void InitPatrolRecordViewFliper() {
		
		patrolRecordData = new ArrayList<HashMap<String, String>>();		
		patrolRecordLV = (ListView) findViewById(R.id.act_patrol_data_lv);	
		patrolwaiter = (ProgressBar) findViewById(R.id.patrol_waiter);
		
		patrolRecordLV.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState ==  OnScrollListener.SCROLL_STATE_IDLE){
					if(patrolRecordLV.getLastVisiblePosition() == (patrolRecordLV.getCount()-1)){
						patroltouch = true;
						
						LoadPatrolRecordAysnTask load = new LoadPatrolRecordAysnTask();
						load.execute(patrolRecordData.size());	
					}
				}				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});				
		
		
		LoadPatrolRecordAysnTask loadCourse = new LoadPatrolRecordAysnTask();
		loadCourse.execute(0);
		
		
		patrolRecordAdapter = new NFCAdapter(this, patrolRecordData,R.layout.view_patrol_record_list, 
				new String[] { "address", "time" },new int[] { R.id.view_meal_list_name_tv,R.id.view_meal_list_cal_tv });
	
		patrolRecordLV.setAdapter(patrolRecordAdapter);
		
	
	}

	
	private Integer patrolRecordJsonHandle(String retResponse) {
		int ret =0;
		if(retResponse == null ){
			Log.i("ret", "获取巡更失败");			
			return 1;
		}
		Log.i("ret", retResponse);
		
		
		try { 					
			JSONObject json = new JSONObject(retResponse);
			JSONArray foodsArray = json.getJSONArray("foods");				
			if(foodsArray.length() == 0 ){
				isComplelet = true;
			}
			for (int i = 0; i < foodsArray.length(); i++) {
				JSONObject temp = (JSONObject) foodsArray.opt(i);
				HashMap<String, String> courseItem = new HashMap<String, String>();				
				String time = temp.getString("time");		
				String address = temp.getString("address");					
				courseItem.put("time", time);	
				courseItem.put("address", address);					
				patrolRecordData.add(courseItem);					  
			}			
			
		} catch (JSONException e) {
			e.printStackTrace();			
			ret = 1;
		}
		return ret;
	}
}
