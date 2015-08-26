package com.ab.health;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ab.health.utility.HttpGetData;
import com.ab.health.utility.AppSetting;
import com.ab.health.utility.NetworkConnect;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class GongGaoActivity extends Activity {

	private ListView gonggaoLV;
	private Button btn_back;
	
	private List<HashMap<String, String>> gonggaoData;
	private SimpleAdapter gongGaoAdapter;
	
	private HttpGetData httpData;
	private String url,param,subTitle;

	private boolean isComplelet = false;
	private int catId;
	private ItemClick itemclick;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// wgp new 
		super.onCreate(savedInstanceState);
		if(!NetworkConnect.isNetworkConnected(this)){
			NetworkConnect.AlertNotCon(this);
		}else {
			setContentView(R.layout.activity_gonggao2);
			
			gonggaoData = new ArrayList<HashMap<String, String>>();
			
			gonggaoLV = (ListView) findViewById(R.id.act_courseSearch_data_lv);
			
					
			
			gonggaoLV.setOnScrollListener(new AbsListView.OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					if(scrollState ==  OnScrollListener.SCROLL_STATE_IDLE){
						if(gonggaoLV.getLastVisiblePosition() == (gonggaoLV.getCount()-1)){
							Toast.makeText(getApplicationContext(), "正在加载", Toast.LENGTH_SHORT).show();
							LoadGongGaoAysnTask load = new LoadGongGaoAysnTask();
							load.execute(gonggaoData.size());	
						}
					}
					
				}
				
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					
					
				}
			});
			
			
			
			httpData = new HttpGetData();
			url = AppSetting.getRootURL() +  "gonggao.php";
			
			LoadGongGaoAysnTask loadCourse = new LoadGongGaoAysnTask();
			loadCourse.execute(0);
			
			
			gongGaoAdapter = new SimpleAdapter(this, gonggaoData,R.layout.view_gonggao_list, 
					new String[] { "title", "date" },new int[] { R.id.view_meal_list_name_tv,R.id.view_meal_list_cal_tv });
		
			gonggaoLV.setAdapter(gongGaoAdapter);
		
			itemclick = new ItemClick();		 	
			gonggaoLV.setOnItemClickListener(itemclick);
		}
		
	}
	
	
	class ItemClick implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {			
			HashMap<String, String> course = gonggaoData.get(arg2);
			String title = course.get("titlelong");
			String date = course.get("date");
			int id = Integer.valueOf( course.get("newId"));
			Intent intent = new Intent(GongGaoActivity.this, GongGaoDetailActivity.class);
			intent.putExtra("title", title);
			intent.putExtra("date", date);
		
			intent.putExtra("count", id);
			startActivity(intent);
		} 		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	

	private class LoadGongGaoAysnTask extends AsyncTask<Object, Integer, Integer>{
		int ret;
		

		@Override
		protected void onPreExecute() {		
			if( isComplelet ){ 
				Toast.makeText(getApplicationContext(), "全部信息已加载", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(getApplicationContext(), "正在加载...", Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		protected Integer doInBackground(Object... params) {			
			int sId = (Integer) params[0];
			param = "?startid="+sId+"&endid=10&catid=" + catId;		
			ret = gongGaoJsonHandle(httpData.HttpGets(url,param));
			return ret;
		}

		@Override
		protected void onPostExecute(Integer result) {
			
			if(ret ==0){			
					gongGaoAdapter.notifyDataSetChanged();	
					
			}else {

			}			
		}

	}
	
	

	
	

	private Integer gongGaoJsonHandle(String retResponse) {
		int ret =0;
		try { 
			JSONObject json = new JSONObject(retResponse);
			JSONArray foodsArray = json.getJSONArray("foods");
			if(foodsArray.length() == 0 ){
				isComplelet = true;
			}
			for (int i = 0; i < foodsArray.length(); i++) {
				JSONObject temp = (JSONObject) foodsArray.opt(i);
				HashMap<String, String> courseItem = new HashMap<String, String>();
				String titlelong = temp.getString("title");
				String newsid = temp.getString("id");
				String date = temp.getString("date");
				if(titlelong.length() > 12){
					subTitle = titlelong.substring(0, 11) + "...";
					courseItem.put("title", subTitle);
				}else {
					courseItem.put("title", titlelong);
				}
				courseItem.put("newId", newsid);
				courseItem.put("titlelong", titlelong);
				courseItem.put("date", date);	
				gonggaoData.add(courseItem);				
			}
			ret =0 ;
		} catch (JSONException e) {
			e.printStackTrace();
			ret = 1;
		}
		return ret;
	}
	
}


