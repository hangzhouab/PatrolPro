package com.ab.health;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ab.health.utility.AppSetting;
import com.ab.health.utility.HttpGetData;
import com.ab.health.utility.NetworkConnect;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class TongXunLuActivity extends Activity {
	
	private ListView AddressListLV;
	private Button btn_back;
	
	private List<HashMap<String, String>> AddressListData;
	private SimpleAdapter AddressListAdapter;
	
	private HttpGetData httpData;
	private String url,param,subTitle,ramOrgniztion;
	
	private boolean isComplelet = false;
	
	private ItemClick itemclick;
	private ProgressBar patrolwaiter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		if(!NetworkConnect.isNetworkConnected(this)){
			NetworkConnect.AlertNotCon(this);
		}else {
			setContentView(R.layout.activity_addresslist);			
			AddressListData = new ArrayList<HashMap<String, String>>();			
			AddressListLV = (ListView) findViewById(R.id.act_patrol_data_lv);
			patrolwaiter = (ProgressBar) findViewById(R.id.patrol_waiter);
			btn_back = (Button) findViewById(R.id.tool_standardweight_back_btn);
			btn_back.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
					
				}
			});
			
			AddressListLV.setOnScrollListener(new AbsListView.OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					if(scrollState ==  OnScrollListener.SCROLL_STATE_IDLE){
						if(AddressListLV.getLastVisiblePosition() == (AddressListLV.getCount()-1)){
							Toast.makeText(getApplicationContext(), "正在加载", Toast.LENGTH_SHORT).show();
							LoadAddressListAysnTask load = new LoadAddressListAysnTask();
							load.execute(AddressListData.size());	
						}
					}
					
				}
				
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					
					
				}
			});
			
			
			
			httpData = new HttpGetData();				
			LoadAddressListAysnTask loadCourse = new LoadAddressListAysnTask();
			loadCourse.execute(0);
			
			
			AddressListAdapter = new SimpleAdapter(this, AddressListData,R.layout.view_address_list, 
					new String[] { "username", "phone" },new int[] { R.id.view_meal_list_name_tv,R.id.view_meal_list_cal_tv });
		
			AddressListLV.setAdapter(AddressListAdapter);
		
			itemclick = new ItemClick();		 	
			AddressListLV.setOnItemClickListener(itemclick);
		}
		
	}
	
	private void GetShared() {
		SharedPreferences appSetting = getSharedPreferences(AppSetting.getSettingFile(), MODE_PRIVATE);
		
		ramOrgniztion = appSetting.getString(AppSetting.orgnization, "");	
	}
	
	class ItemClick implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {	
			HashMap<String, String> course = AddressListData.get(arg2);
			String phone = course.get("phone");			
			Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + phone));			
			startActivity(intent);
		} 		
	}
	


	private class LoadAddressListAysnTask extends AsyncTask<Object, Integer, Integer>{
		String loadParam,url;
		@Override
		protected void onPreExecute() {		
			if( isComplelet ){ 
				Toast.makeText(getApplicationContext(), "全部信息已加载完成", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(getApplicationContext(), "正在加载，请稍后...", Toast.LENGTH_SHORT).show();
			}
			GetShared();
			url = AppSetting.getRootURL() + "addresslist.php";
			
		} 
		
		@Override
		protected Integer doInBackground(Object... params) {	
			int sId = (Integer) params[0];
			loadParam = "?unit="+ ramOrgniztion + "&startid="+sId+"&endid=10";		
			HttpGetData httpData = new HttpGetData();
			AddressListJsonHandle(httpData.HttpGets(url,loadParam));
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {				
			patrolwaiter.setVisibility(View.GONE);	
			
			AddressListLV.setVisibility(View.VISIBLE);
			AddressListAdapter.notifyDataSetChanged();				
			
		}

	}
	
	

	
	

	private Integer AddressListJsonHandle(String retResponse) {
		int ret =0;
		if(retResponse == null ){
			Log.i("ret", "获取通信录失败");			
			return 1;
		}
		Log.i("ret", retResponse);
		try { 					
			JSONObject json = new JSONObject(retResponse);
			JSONArray foodsArray = json.getJSONArray("foods");	
			Log.i("food", String.valueOf(foodsArray.length()));
			if(foodsArray.length() == 0 ){
				isComplelet = true;
			}
			for (int i = 0; i < foodsArray.length(); i++) {
				JSONObject temp = (JSONObject) foodsArray.opt(i);
				HashMap<String, String> courseItem = new HashMap<String, String>();				
				String username = temp.getString("title");		
				String phone = temp.getString("phone");					
				courseItem.put("username", username);	
				courseItem.put("phone", phone);					
				AddressListData.add(courseItem);	
				  
			}			
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.i("excetion", retResponse);
			ret = 1;
		}
		return ret;
	}
}
