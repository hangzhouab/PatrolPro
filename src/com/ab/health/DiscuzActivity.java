package com.ab.health;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.ab.health.online.L;
import com.ab.health.online.MessageItem;
import com.ab.health.online.RecentItem;
import com.ab.health.online.TimeUtil;
import com.ab.health.utility.HttpGetData;
import com.ab.health.utility.AppSetting;
import com.ab.health.utility.NetworkConnect;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DiscuzActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{

	private ListView discuzLV;
	private Button btn_back;
	private OnClickLis onClickLis;
	private List<HashMap<String, String>> discuzData;
	private List<HashMap<String, String>> refrashData;
	private SimpleAdapter discuzAdapter;
	private TextView btn_xie;
	private HttpGetData httpData;
	private String url,param,subTitle;
	private String nickname;
	private int catId,ret=0;
	private ItemClick itemclick;
	public static final int REFRASH = 0x002;// 下拉刷新
	private SwipeRefreshLayout mSwipeLayout;
	private boolean isRefrash =  false, isComplelet = false;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {	
			isRefrash = true;
			if(msg.what == REFRASH ){	
				RefrashDiscuzAysnTask loadCourse = new RefrashDiscuzAysnTask();
				loadCourse.execute(0);
				mSwipeLayout.setRefreshing(false);
			}
		}
		
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if(!NetworkConnect.isNetworkConnected(this)){
			NetworkConnect.AlertNotCon(this);
		}else {
			setContentView(R.layout.activity_discuz);
			onClickLis = new OnClickLis();
			discuzData = new ArrayList<HashMap<String, String>>();
			refrashData = new ArrayList<HashMap<String, String>>();
			SharedPreferences appSetting = getSharedPreferences(AppSetting.getSettingFile(), MODE_PRIVATE);
			nickname = appSetting.getString("nicename", "");
			discuzLV = (ListView) findViewById(R.id.act_courseSearch_data_lv);
			
			btn_back = (Button) findViewById(R.id.tool_standardweight_back_btn);			
			btn_xie = (TextView) findViewById(R.id.discuz_xie);
			btn_xie.setOnClickListener(onClickLis);
			
			btn_back.setOnClickListener(onClickLis);
			
			httpData = new HttpGetData();
			url = AppSetting.getRootURL() +  "discuz.php";
			
			LoadDiscuzAysnTask loadCourse = new LoadDiscuzAysnTask();
			loadCourse.execute(0);
			Log.i("sid", String.valueOf(discuzData.size()));
			Log.i("javaTime", String.valueOf(System.currentTimeMillis()));
			mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_ly);
			mSwipeLayout.setOnRefreshListener(this);
			mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,  
	                android.R.color.holo_orange_light, android.R.color.holo_red_light);
			
			discuzAdapter = new SimpleAdapter(this, discuzData,R.layout.view_discuz_list, 
					new String[] { "nickname", "content", "date" },new int[] { R.id.discuz_name,R.id.discuz_content,R.id.discuz_time });
		
			discuzLV.setAdapter(discuzAdapter);
		
			
			
			
			discuzLV.setOnScrollListener(new AbsListView.OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					if(scrollState ==  OnScrollListener.SCROLL_STATE_IDLE){
						if(discuzLV.getLastVisiblePosition() == (discuzLV.getCount()-1)){
							Toast.makeText(getApplicationContext(), "正在加载...", Toast.LENGTH_SHORT).show();
							LoadDiscuzAysnTask load = new LoadDiscuzAysnTask();
							load.execute(discuzData.size());
						}
					}
					
				}
				
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					
					
				}
			});
			
			
			itemclick = new ItemClick();		 	
			discuzLV.setOnItemClickListener(itemclick);
		}
		
	}
	
	


	class ItemClick implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {			
		/*	HashMap<String, String> course = discuzData.get(arg2);
			String title = course.get("titlelong");
			String date = course.get("date");
			int id = Integer.valueOf( course.get("Id"));
			Intent intent = new Intent(DiscuzActivity.this, GongGaoDetailActivity.class);
			intent.putExtra("title", title);
			intent.putExtra("date", date);
			intent.putExtra("count", id);
			startActivity(intent);*/
		} 		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		RefrashDiscuzAysnTask loadCourse = new RefrashDiscuzAysnTask();
		loadCourse.execute(0);
		
	}
	
	

	private class LoadDiscuzAysnTask extends AsyncTask<Object, Integer, Integer>{
		
		

		@Override
		protected void onPreExecute() {		
	//		
			if( isComplelet ){ 
				Toast.makeText(getApplicationContext(), "全部信息已加载", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(getApplicationContext(), "正在加载...", Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		protected Integer doInBackground(Object... params) {			
			int sId = (Integer) params[0];
			param = "?startid="+sId+"&endid=10";		
			ret = discuzJsonHandle(httpData.HttpGets(url,param));
			return ret;
		}

		@Override
		protected void onPostExecute(Integer result) {			
			discuzAdapter.notifyDataSetChanged();
		
		}

	}
	
	
	private class RefrashDiscuzAysnTask extends AsyncTask<Object, Integer, Integer>{
		int ret;
		

		@Override
		protected void onPreExecute() {		
	//			
			Toast.makeText(getApplicationContext(), "正在加载...", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		protected Integer doInBackground(Object... params) {			
			int sId = (Integer) params[0];			
			param = "?startid="+sId+"&endid=10&catid=" + catId;		
			ret = refrashJsonHandle(httpData.HttpGets(url,param));		
			return ret;
		}

		@Override
		protected void onPostExecute(Integer result) {	
			
			HashMap<String, String> tempRefrash;
			HashMap<String, String> oldRefrash;
			int position = 10;
			for (int i = 0; i < 10; i++) {
				tempRefrash = refrashData.get(i);
				Log.i("refrash", tempRefrash.get("content"));
				for (int j = 0; j < 10; j++) {
					oldRefrash = discuzData.get(j);
					Log.i("old", oldRefrash.get("content"));
					if(tempRefrash.get("content").equals(oldRefrash.get("content"))){
						position = i;
						break;
					}
				}
				if(position != 10){
					break;
				}
			}
			Log.i("postigon", String.valueOf(position));
			
			for (int i = 9; i >= position; i--) {
				refrashData.remove(i);
			}
			
			discuzData.addAll(0, refrashData);
			discuzAdapter.notifyDataSetChanged();
		
		}

	}
	
	private Integer refrashJsonHandle(String retResponse) {
		ret =0;
		try { 
			refrashData.clear();
			JSONObject json = new JSONObject(retResponse);
			JSONArray discuzArray = json.getJSONArray("discuz");
			for (int i = 0; i < discuzArray.length(); i++) {
				JSONObject temp = (JSONObject) discuzArray.opt(i);
				HashMap<String, String> discuzItem = new HashMap<String, String>();
				String nickname = temp.getString("nickname");  
				String content = temp.getString("content");
				Long tempTime = 0L;
				tempTime = Long.valueOf(temp.getString("time"));
				String date = String.valueOf( TimeUtil.getChatTime(tempTime));
				String position = temp.getString("id");			
				discuzItem.put("nickname", nickname);			
				discuzItem.put("content", content);
				discuzItem.put("position", position);
				discuzItem.put("date", date);	
				refrashData.add(discuzItem);				
			}
			ret =0 ;
		} catch (JSONException e) {
			e.printStackTrace();
			ret = 1;
		}
		return ret;
	}   

	
	

	private Integer discuzJsonHandle(String retResponse) {
		
		try { 
			JSONObject json = new JSONObject(retResponse);
			JSONArray discuzArray = json.getJSONArray("discuz");
			if(discuzArray.length() == 0){
				isComplelet = true;
			}
			for (int i = 0; i < discuzArray.length(); i++) {
				JSONObject temp = (JSONObject) discuzArray.opt(i);
				HashMap<String, String> discuzItem = new HashMap<String, String>();
				String nickname = temp.getString("nickname");  
				String content = temp.getString("content");
				Long tempTime = 0L;
				tempTime = Long.valueOf(temp.getString("time"));
				String date = String.valueOf( TimeUtil.getChatTime(tempTime));
				String position = temp.getString("id");			
				discuzItem.put("nickname", nickname);			
				discuzItem.put("content", content);
				discuzItem.put("position", position);
				discuzItem.put("date", date);	
				discuzData.add(discuzItem);		
				
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			ret = 1;
		}
		return ret;
	}   

	class OnClickLis implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tool_standardweight_back_btn:
				finish();
				break;
			case R.id.discuz_xie:
				Intent intent = new Intent(getApplicationContext(), DiscuzNew.class);
				startActivity(intent);
				break;
			
			default:
				break;
			}
		}
	}

	@Override
	public void onRefresh() {
		handler.sendEmptyMessageDelayed(REFRASH, 1000);
		
	}
	
	
	
	
	
	
	
}


