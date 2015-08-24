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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ZhiShiActivity extends Activity {

	private ListView courseMenu;
	private Button btn_back;
	private OnClickLis onClickLis;
	private List<HashMap<String, String>> courseData,courseCatarg;
	private SimpleAdapter sa;
	private LinearLayout loadingLineLayout,menulinelayout;
	private TextView loadingText;
	private HttpGetData httpData;
	private String url,param,subTitle;

	private int catId;
	private ItemClick itemclick;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if(!NetworkConnect.isNetworkConnected(this)){
			NetworkConnect.AlertNotCon(this);
		}else {
			setContentView(R.layout.activity_zhishi);
			onClickLis = new OnClickLis();
			
			courseMenu = (ListView) findViewById(R.id.act_courseSearch_data_lv);
			
			btn_back = (Button) findViewById(R.id.act_courseSearch_back_btn);
			loadingLineLayout = (LinearLayout) findViewById(R.id.act_couseSearch_loading);
			menulinelayout = (LinearLayout) findViewById(R.id.act_courseSearch_data_ly);
	//		loadingText = (TextView) findViewById(R.id.act_courseSearch_loading);
			courseMenu.setOnScrollListener(new AbsListView.OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					if(scrollState ==  OnScrollListener.SCROLL_STATE_IDLE){
						if(courseMenu.getLastVisiblePosition() == (courseMenu.getCount()-1)){
							Toast.makeText(getApplicationContext(), "正在加载", Toast.LENGTH_SHORT).show();
							LoadCourseAysnTask load = new LoadCourseAysnTask();
							load.execute(courseData.size());	
						}
					}
					
				}
				
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					
					
				}
			});
			
			btn_back.setOnClickListener(onClickLis);
			
			httpData = new HttpGetData();
			url = AppSetting.getRootURL() +  "zhishi.php";
			
			LoadCourseAysnTask loadCourse = new LoadCourseAysnTask();
			loadCourse.execute(0);
			 
			
			
			
			sa = new SimpleAdapter(this, courseData,R.layout.view_zhishi_list, 
					new String[] { "title", "date" },new int[] { R.id.view_meal_list_name_tv,R.id.view_meal_list_cal_tv });
		
			courseMenu.setAdapter(sa);
		
			itemclick = new ItemClick();		 	
			courseMenu.setOnItemClickListener(itemclick);
		}
		
	}
	
	
	class ItemClick implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {			
			HashMap<String, String> course = courseData.get(arg2);
			String title = course.get("titlelong");
			String date = course.get("date");
			int id = Integer.valueOf( course.get("newId"));
			Intent intent = new Intent(ZhiShiActivity.this, ZhiShiDetailActivity.class);
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
	
	

	private class LoadCourseAysnTask extends AsyncTask<Object, Integer, Integer>{
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
			ret = JsonHandle(httpData.HttpGets(url,param));
			return ret;
		}

		@Override
		protected void onPostExecute(Integer result) {
			
			if(ret ==0){			
					sa.notifyDataSetChanged();		
			}else {
				loadingText.setText("加载失败");
			}			
		}

	}
	
	
	
	
	public ZhiShiActivity() {
		courseData = new ArrayList<HashMap<String, String>>();
		courseCatarg = new ArrayList<HashMap<String, String>>();
	}
	
	

	private Integer JsonHandle(String retResponse) {
		int ret =0;
		try { 
			JSONObject json = new JSONObject(retResponse);
			JSONArray foodsArray = json.getJSONArray("foods");
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
				courseData.add(courseItem);				
			}
			ret =0 ;
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
			case R.id.act_courseSearch_back_btn:
				finish();
				break;
			
			default:
				break;
			}
		}
	}
	
	
	
	
}


