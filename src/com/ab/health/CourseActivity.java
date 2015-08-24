package com.ab.health;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.ab.health.utility.HttpGetData;
import com.ab.health.utility.AppSetting;
import com.ab.health.utility.NetworkConnect;

import android.R.integer;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class CourseActivity extends Activity {

	private ListView courseMenu,courseCat,searchResult;
	private Button btn_back;
	private OnClickLis onClickLis;
	private List<HashMap<String, String>> courseData,courseCatarg,searchData;
	private SimpleAdapter sa;
	private LinearLayout loadingLineLayout,menulinelayout;
	private TextView loadingText,loading;
	private HttpGetData httpData;
	private String url,param;

	private BluetoothAdapter mBluetoothAdapter;
	private boolean isComplelet = false;
	private BluetoothDevice mDevice;
	private int catId;
	private boolean isLoadingMore = false;
	private ItemClick itemclick;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if(!NetworkConnect.isNetworkConnected(this)){
			NetworkConnect.AlertNotCon(this);
		}else {
			setContentView(R.layout.activity_course_search);
			onClickLis = new OnClickLis();
			
			
			
			courseCat = (ListView) findViewById(R.id.act_courseSearch_condition_lv);
			courseMenu = (ListView) findViewById(R.id.act_courseSearch_data_lv);
			searchResult = (ListView) findViewById(R.id.act_courseSearch_result);
			btn_back = (Button) findViewById(R.id.act_courseSearch_back_btn);
			loadingLineLayout = (LinearLayout) findViewById(R.id.act_couseSearch_loading);
			menulinelayout = (LinearLayout) findViewById(R.id.act_courseSearch_data_ly);
	//		loadingText = (TextView) findViewById(R.id.act_courseSearch_loading);
			Button search = (Button) findViewById(R.id.act_recordCal_commit_btn);
			search.setOnClickListener(onClickLis);
			
			courseMenu.setOnScrollListener(new AbsListView.OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					if(scrollState ==  OnScrollListener.SCROLL_STATE_IDLE){
						if(courseMenu.getLastVisiblePosition() == (courseMenu.getCount()-1)){
							Toast.makeText(getApplicationContext(), "正在加载...", Toast.LENGTH_SHORT).show();
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
			url = AppSetting.getRootURL() +  "course.php";
			
			LoadCourseAysnTask loadCourse = new LoadCourseAysnTask();
			loadCourse.execute(0);
			
			
			String []catName = new String[]{"主食","蔬菜","水果","肉类","蛋类","水产","奶类","油脂","其它"};
			//		String []catName = new String[]{"主食","蔬菜","水果","肉类","蛋类","水产","奶类","油脂","糕点","糖类","饮料","菌藻","其它"};
					
			for(int i =0; i < catName.length; i++){
				
				HashMap<String, String> courseItem = new HashMap<String, String>();
				courseItem.put("name", catName[i]);			
				courseCatarg.add(courseItem);
			}
			
			SimpleAdapter catarg = new SimpleAdapter(this, courseCatarg, R.layout.view_course_catarg, 
									new String[]{"name"}, new int[]{R.id.course_catarg_name});
			courseCat.setAdapter(catarg);
			CatItemClick catClick = new CatItemClick();
			courseCat.setOnItemClickListener(catClick);
			
			//SimpleAdapter searchAdapter = new SimpleAdapter(this, courseData, R.layout.view_meal_list, from, to)
			
			
			
			sa = new SimpleAdapter(this, courseData,R.layout.view_meal_list, 
					new String[] { "title", "calorie" },new int[] { R.id.view_meal_list_name_tv,R.id.view_meal_list_cal_tv });
		//	courseMenu.addFooterView(loadMore);
			courseMenu.setAdapter(sa);
			/*ImageView foodImage = (ImageView) findViewById(R.id.view_meal_list_icon_iv);
			foodImage.setBackgroundResource(R.drawable.fooditem);*/
			itemclick = new ItemClick();		 	
			courseMenu.setOnItemClickListener(itemclick);
		}
		
	}
	
	class CatItemClick implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {			
			
			catId = arg2;
			courseData.clear();	
			
			LoadCatAysnTask loadCat = new LoadCatAysnTask();
			loadCat.execute(0);
			TextView tv;
			for(int i = 0; i < arg0.getChildCount(); i++ ){
				tv = (TextView) arg0.getChildAt(i);
				tv.setBackgroundColor(0Xffd0d0e0);
				tv.setTextColor(0XFF111111);				
			}
			
			TextView item = (TextView) arg0.getChildAt(arg2);
			
			item.setBackgroundColor(0Xff454589);
			item.setTextColor(0XFFffffff); 
		}		
	}
	
	class ItemClick implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {			
			HashMap<String, String> course = courseData.get(arg2);
			String title = course.get("title");
			String cal = course.get("cal");
			Intent intent = new Intent(CourseActivity.this, RecordCourseActivity.class);
			intent.putExtra("title", title);
			intent.putExtra("cal", cal);
			startActivity(intent);
		} 		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private class LoadCatAysnTask extends AsyncTask<Object, Integer, Integer>{
		int ret;
		

		@Override
		protected void onPreExecute() {	
			menulinelayout.setVisibility(View.GONE);
			loadingLineLayout.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Integer doInBackground(Object... params) {
			param = "?startid=0&endid=10&catid=" + catId;
			ret = JsonHandle(httpData.HttpGets(url,param));
			return ret;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(ret ==0){				
					menulinelayout.setVisibility(View.VISIBLE);
					loadingLineLayout.setVisibility(View.GONE);		
					sa.notifyDataSetChanged();
			}else {
				loadingText.setText("加载失败");
			}			
		}

	}
	

	private class LoadCourseAysnTask extends AsyncTask<Object, Integer, Integer>{
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
	
	private class LoadMoreAysnTask extends AsyncTask<Object, Integer, Integer>{
		int ret;
		

		@Override
		protected void onPreExecute() {		
//			loadingText.setText("正在加载...");
			
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
				
			}else {
				loadingText.setText("加载失败");
			}			
		}

	}
	
	
	
	
	
	public CourseActivity() {
		courseData = new ArrayList<HashMap<String, String>>();
		courseCatarg = new ArrayList<HashMap<String, String>>();
	}
	
	

	private Integer JsonHandle(String retResponse) {
		int ret =0;
		try { 
			JSONObject json = new JSONObject(retResponse);
			JSONArray foodsArray = json.getJSONArray("foods");
			if(foodsArray.length() == 0 ){
				isComplelet = true;
			}
			for (int i = 0; i < foodsArray.length(); i++) {
				JSONObject temp = (JSONObject) foodsArray.opt(i);
				String courseName = temp.getString("name");
				String cal = temp.getString("calorie");
				String calorie = cal + "千卡" + "/" + "100克";
				HashMap<String, String> courseItem = new HashMap<String, String>();
				courseItem.put("title", courseName);
				courseItem.put("calorie", calorie);		
				courseItem.put("cal", cal);		
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
			
			case R.id.act_recordCal_commit_btn:				
				EditText tempInput = (EditText) findViewById(R.id.act_coursekeysearch_key_et);				
				String temInput = tempInput.getText().toString();
				String input = URLEncoder.encode(temInput);			
				url = AppSetting.getRootURL() + "searchCourse.php"; 
				param = "?keyword=" + input; 		
				courseData.clear();
				SearchCourseAysnTask search = new SearchCourseAysnTask();
				search.execute(0);
				searchResult.setAdapter(sa);
				searchResult.setOnItemClickListener(itemclick);
				break;
			default:
				break;
			}
		}
	}
	
	private class SearchCourseAysnTask extends AsyncTask<Object, Integer, Integer>{
		int ret;		

		@Override
		protected void onPreExecute() {				
				menulinelayout.setVisibility(View.GONE);
				courseCat.setVisibility(View.GONE);
				loadingLineLayout.setVisibility(View.VISIBLE);			
		}
		
		@Override
		protected Integer doInBackground(Object... params) {					
			ret = JsonHandle(httpData.HttpGets(url,param));
			return ret;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if(ret ==0){							
					loadingLineLayout.setVisibility(View.GONE);		
					searchResult.setVisibility(View.VISIBLE);
					sa.notifyDataSetChanged();
			}else {
				loadingText.setText("加载失败");
			}			
		}

	}
	
	
}


