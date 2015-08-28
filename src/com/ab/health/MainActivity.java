package com.ab.health;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;

import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.AbsListView.OnScrollListener;
import android.widget.RelativeLayout.LayoutParams;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.ab.health.R;

import com.ab.health.GongGaoActivity.ItemClick;

import com.ab.health.model.User;

import com.ab.health.utility.DensityUtil;
import com.ab.health.utility.HealthUtility;
import com.ab.health.utility.HttpGetData;
import com.ab.health.utility.AppSetting;
import com.ab.health.utility.NetworkConnect;
import com.ab.health.utility.UpdateManager;
import com.ab.nfc.NFCReader;


public class MainActivity extends Activity {

	private ViewFlipper contentViewPager = null;
	private LayoutInflater flater = null;
	private View viewGongGao, activityServer, activityTool,
			activityPhysiology;

	private Button btn_about, btn_bbs, btn_tool, btn_record, btn_topbar_home;
			
	private Button btn_patrolQuery;
	
	private Button gonggao;
	
	
	private OnClickListener onClickListener;
	private String url, param, ret, inputWeight = "";
	private int recordCalorie = 0, sex;
	private String username = "nlk", nicename = "nlk", height = "175",
			weight = "75", age = "30", target = "70", days = "20";

	private TextView recordCal, titleBar, recordSports, courseTarget,
			sportsTarget, changePlain, yourBMI, yourWeight, targetWeight;
	private ArrayList<View> weightRecordList = new ArrayList<View>();
	private UpdateManager update;
	private HttpGetData commitDate;
	private LinearLayout bottom_record, bottom_tool, bottom_weight,
			bottom_server;
	

	
	// GongGao viewFliper
	private List<HashMap<String, String>> gonggaoData;
	private SimpleAdapter gongGaoAdapter;
	private ListView gonggaoLV;
	private boolean isComplelet = false;
	private int catId;
	private HttpGetData httpData;
	private String subTitle;
	private GongGaoItemClick itemclick;
	
	// PatrolRecord viewFliper
	private List<HashMap<String, String>> patrolRecordData;
	private SimpleAdapter patrolRecordAdapter;
	private ListView patrolRecordLV;	
	
	private String TAGaddress;
	
	private  CalendarView calendar;
	
	private boolean isInit = false;
	private String ramUsername,ramOrgniztion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!NetworkConnect.isNetworkConnected(this)) {
			NetworkConnect.AlertNotCon(this);
		} else {			
			setContentView(R.layout.activity_main);	
			InitButton();
			CreatePatroRecordTabel();
			CheckAppUpdate();					
			InitGongGaoViewFliper();
			InitPatrolRecordViewFliper();			
			UpdatePatrolRecord();
		}
	}
	
	
	private void CreatePatroRecordTabel() {
		CreateTableAysnTask create = new CreateTableAysnTask();
		create.execute(1);
		
	}


	private void CheckAppUpdate(){
		UpdateAppAysnTask updateApp = new UpdateAppAysnTask();
		updateApp.execute(1);
	}

	private void UpdatePatrolRecord() {		
		NFCReader nfcReader = new NFCReader();
		TAGaddress = nfcReader.read(getIntent());
		if(TAGaddress.equals("error")){
			return;
		}
		UploadPatrolRecordAysnTask upload = new UploadPatrolRecordAysnTask();
		upload.execute(1);
	}

	private void InitGongGaoViewFliper() {
		
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
		itemclick = new GongGaoItemClick();		 	
		gonggaoLV.setOnItemClickListener(itemclick);
	}

	private void InitPatrolRecordViewFliper() {
		
		patrolRecordData = new ArrayList<HashMap<String, String>>();		
		patrolRecordLV = (ListView) findViewById(R.id.act_patrol_data_lv);	
		
		patrolRecordLV.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState ==  OnScrollListener.SCROLL_STATE_IDLE){
					if(patrolRecordLV.getLastVisiblePosition() == (patrolRecordLV.getCount()-1)){
						Toast.makeText(getApplicationContext(), "正在加载", Toast.LENGTH_SHORT).show();
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
		
		
		patrolRecordAdapter = new SimpleAdapter(this, patrolRecordData,R.layout.view_patrol_record_list, 
				new String[] { "address", "time" },new int[] { R.id.view_meal_list_name_tv,R.id.view_meal_list_cal_tv });
	
		patrolRecordLV.setAdapter(patrolRecordAdapter);
		 	
	
	}
	
	private void GetShared() {
		SharedPreferences appSetting = getSharedPreferences(AppSetting.getSettingFile(), MODE_PRIVATE);
		ramUsername = appSetting.getString(AppSetting.username, "");
		ramOrgniztion = appSetting.getString(AppSetting.orgnization, "");	
	}

	class GongGaoItemClick implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {			
			HashMap<String, String> course = gonggaoData.get(arg2);
			String title = course.get("titlelong");
			String date = course.get("date");
			int id = Integer.valueOf( course.get("newId"));
			Intent intent = new Intent(MainActivity.this, GongGaoDetailActivity.class);
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

	private void InitButton() {
		flater = getLayoutInflater();
		contentViewPager = (ViewFlipper) findViewById(R.id.content_flipper);

		viewGongGao = flater.inflate(R.layout.activity_gonggao2, contentViewPager);
		
		activityTool = flater.inflate(R.layout.activity_patrol_record, contentViewPager);
		activityPhysiology = flater.inflate(R.layout.fragment_patrol_query,
				contentViewPager); 
		activityServer = flater.inflate(R.layout.activity_setting_about,
				contentViewPager);
		onClickListener = new OnClickListener();

		calendar = (CalendarView) findViewById(R.id.calendarView1);
		calendar.setBackgroundColor(0xffcccccc);
		
		
		btn_record = (Button) findViewById(R.id.bottombar_record);
		btn_record.setOnClickListener(onClickListener);
		btn_bbs = (Button) findViewById(R.id.bottombar_bbs);
		btn_bbs.setOnClickListener(onClickListener);
		btn_about = (Button) findViewById(R.id.bottombar_about);
		btn_about.setOnClickListener(onClickListener);
		btn_tool = (Button) findViewById(R.id.bottombar_tool);
		btn_tool.setOnClickListener(onClickListener);
		TextView goWeb = (TextView) findViewById(R.id.setting_about_weibo_textView);
		goWeb.setOnClickListener(onClickListener);
		TextView coreBreif = (TextView) findViewById(R.id.setting_about_website_textView);
		coreBreif.setOnClickListener(onClickListener);


		// buttom button
		bottom_record = (LinearLayout) findViewById(R.id.bottom_record);
		bottom_record.setOnClickListener(onClickListener);
		bottom_tool = (LinearLayout) findViewById(R.id.bottom_tool);
		bottom_tool.setOnClickListener(onClickListener);
		bottom_server = (LinearLayout) findViewById(R.id.bottom_about);
		bottom_server.setOnClickListener(onClickListener);
		bottom_weight = (LinearLayout) findViewById(R.id.bottom_bbs);
		bottom_weight.setOnClickListener(onClickListener);
//
//		// topbar 
		gonggao = (Button) findViewById(R.id.bottombar_gonggao);
		gonggao.setOnClickListener(onClickListener);
		btn_topbar_home = (Button) findViewById(R.id.bottombar_home);
		btn_topbar_home.setOnClickListener(onClickListener);
		titleBar = (TextView) findViewById(R.id.titlebar_home_title);

		
		// patrol query view fliper		
		btn_patrolQuery = (Button) findViewById(R.id.act_physiology_record_weight);
		btn_patrolQuery.setOnClickListener(onClickListener);

	}



	private void db() {
		int calorieSum = 0;
		recordCalorie = 0;
		SQLiteDatabase db = openOrCreateDatabase("health",
				Context.MODE_PRIVATE, null);

		db.execSQL("CREATE TABLE IF NOT EXISTS record_course (_id INTEGER PRIMARY KEY AUTOINCREMENT,year INTEGER, month INTEGER,day INTEGER,hour INTEGER,minute INTEGER,calorie INTEGER)");
		db.execSQL("CREATE TABLE IF NOT EXISTS record_sports (_id INTEGER PRIMARY KEY AUTOINCREMENT,year INTEGER, month INTEGER,day INTEGER,hour INTEGER,minute INTEGER,setps INTEGER,distance FLOAT,calorie FLOAT,category INTEGER)");

		Calendar now = Calendar.getInstance();
		int yearNow = now.get(Calendar.YEAR) - 2000;
		int monthNow = now.get(Calendar.MONTH) + 1;
		int dayNow = now.get(Calendar.DAY_OF_MONTH);

		String sql = "SELECT * FROM record_course where year=" + yearNow
				+ " and month=" + monthNow + " and day=" + dayNow;
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) {
			recordCalorie += c.getInt(c.getColumnIndex("calorie"));
		}

		String sql2 = "SELECT * FROM record_sports where year=" + yearNow
				+ " and month=" + monthNow + " and day=" + dayNow;
		Cursor c2 = db.rawQuery(sql2, null);
		while (c2.moveToNext()) {
			calorieSum += c2.getFloat(c2.getColumnIndex("calorie"));
		}

		db.close();
	}


	private class OnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.bottombar_record:
//				LoadGongGaoAysnTask loadCourse = new LoadGongGaoAysnTask();
//				loadCourse.execute(0);
				contentViewPager.setDisplayedChild(3);
				contentViewPager.showNext();
				titleBar.setText("单位公告");
				bottom_record.setBackgroundColor(0Xff46cdd8);
				bottom_server.setBackgroundColor(0XFFFFFFFF);
				bottom_tool.setBackgroundColor(0XFFFFFFFF);
				bottom_weight.setBackgroundColor(0XFFFFFFFF);
				db();
				
				break;

			case R.id.bottombar_tool:
				contentViewPager.setDisplayedChild(0);
				contentViewPager.showNext();
				titleBar.setText("巡更记录");
				bottom_record.setBackgroundColor(0XFFFFFFFF);
				bottom_server.setBackgroundColor(0XFFFFFFFF);
				bottom_tool.setBackgroundColor(0Xff46cdd8);
				bottom_weight.setBackgroundColor(0XFFFFFFFF);
				break;
			case R.id.bottombar_bbs:
				contentViewPager.setDisplayedChild(1);
				contentViewPager.showNext();
				titleBar.setText("记录查询");
				bottom_record.setBackgroundColor(0XFFFFFFFF);
				bottom_server.setBackgroundColor(0XFFFFFFFF);
				bottom_tool.setBackgroundColor(0XFFFFFFFF);
				bottom_weight.setBackgroundColor(0Xff46cdd8);
				break;
			case R.id.bottombar_about:
				contentViewPager.setDisplayedChild(2);
				contentViewPager.showNext();
				titleBar.setText("关于我们");
				bottom_record.setBackgroundColor(0XFFFFFFFF);
				bottom_server.setBackgroundColor(0Xff46cdd8);
				bottom_tool.setBackgroundColor(0XFFFFFFFF);
				bottom_weight.setBackgroundColor(0XFFFFFFFF);
				break;
			case R.id.changefitness:
				Intent intentplain = new Intent(MainActivity.this,
						ChangePlainActivity.class);
				startActivity(intentplain);
				overridePendingTransition(R.anim.slide_in_from_bottom,
						R.anim.slide_out_to_top);
				break;
			
			case R.id.bottombar_home:
				Intent intenthome = new Intent(MainActivity.this,
						HomeActivity.class);
				startActivity(intenthome);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_right_out);
				break;
			case R.id.tool_food_btn:
				Intent intent = new Intent(MainActivity.this,
						CourseActivity.class);
				startActivity(intent);
				break;
			
			case R.id.tool_gonggao:
				Intent intentgongg = new Intent(MainActivity.this,
						GongGaoActivity.class);
				startActivity(intentgongg);
				break;
			case R.id.tool_sports_record:
				Intent intentsports_record = new Intent(MainActivity.this,
						SportsRecordActivity.class);
				startActivity(intentsports_record);
				break;			
			case R.id.tool_sports_btn:
				Intent intentSports = new Intent(MainActivity.this,
						SportsActivity.class);
				startActivity(intentSports);
				break;

			
			
			case R.id.tool_lipin:
				Intent intentLipin = new Intent(MainActivity.this,
						LiPinActivity.class);
				startActivity(intentLipin);
				break;

			case R.id.tool_zhishi:
				Intent intentZhishi = new Intent(MainActivity.this,
						ZhiShiActivity.class);
				startActivity(intentZhishi);
				break;
			case R.id.tool_bluetooth:
				int version = AppSetting.getAndroidSDKVersion();
				if (version < 18) {
					Toast.makeText(getApplicationContext(),
							"您系统的版本小于4.3,请先升级再使用", Toast.LENGTH_LONG).show();
					break;
				}
				Toast.makeText(getApplicationContext(), "请稍候，正在打开蓝牙",
						Toast.LENGTH_SHORT).show();				
				break;			
			case R.id.setting_about_website_textView:
				Intent intent2 = new Intent(MainActivity.this,
						CoreBriefActivity.class);
				startActivity(intent2);
				break;			
			case R.id.act_physiology_record_weight:
				Toast.makeText(MainActivity.this, "dd", Toast.LENGTH_SHORT).show();
				break;
			case R.id.setting_about_weibo_textView:
				Uri nuolikangUri = Uri.parse("http://www.nlk759.com");
				Intent intent3 = new Intent(Intent.ACTION_VIEW, nuolikangUri);
				startActivity(intent3);
				break;
			default:
				break;
			}
		}
	}

	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	private class LoadGongGaoAysnTask extends AsyncTask<Object, Integer, Integer>{
		int ret;
		String ggURL;
		@Override
		protected void onPreExecute() {		
			ggURL = AppSetting.getRootURL() + "gonggao.php";
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
			Log.i("url", url);
			ret = gongGaoJsonHandle(httpData.HttpGets(ggURL,param));
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
	
	private class LoadPatrolRecordAysnTask extends AsyncTask<Object, Integer, Integer>{
		
		@Override
		protected void onPreExecute() {		
			url = AppSetting.getRootURL() + "loadpatrol.php";
			GetShared();
		}
		
		@Override
		protected Integer doInBackground(Object... params) {	
			int sId = (Integer) params[0];
			param = "?username="+ramUsername + "&startid="+sId+"&endid=10";		
			HttpGetData httpData = new HttpGetData();
			patrolRecordJsonHandle(httpData.HttpGets(url,param));
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {			
			patrolRecordAdapter.notifyDataSetChanged();					
					
		}

	}
	
	private class UpdateAppAysnTask extends AsyncTask<Object, Integer, Integer>{
		int ret;
		@Override
		protected void onPreExecute() {		
			update = new UpdateManager(MainActivity.this);
		}
		
		@Override
		protected Integer doInBackground(Object... params) {			
			update.checkUpdate();
			return ret;
		}
	}
	
	private class UploadPatrolRecordAysnTask extends AsyncTask<Object, Integer, Integer>{
		String time;
		@Override
		protected void onPreExecute() {		
			url = AppSetting.getRootURL() + "patrolrecord.php";
			GetShared();
			param = "?username="+ramUsername + "&address=" + TAGaddress ;	
			Log.i("upload", param);
		}
		
		@Override
		protected Integer doInBackground(Object... params) {	
			HttpGetData httpData = new HttpGetData();
			time = httpData.HttpGets(url,param);
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {	
//			if(time == null ){
//				Log.i("time", "patrol记录时间返回错误");
//				return;
//			}				
//			HashMap<String, String> recordItem = new HashMap<String, String>();			
//			recordItem.put("address", TAGaddress);
//			recordItem.put("time", time);			
//			patrolRecordData.add(recordItem);
//			patrolRecordAdapter.notifyDataSetChanged();		
			LoadPatrolRecordAysnTask load = new LoadPatrolRecordAysnTask();
			load.execute(0);
//					
		}
	}
	
	private class CreateTableAysnTask extends AsyncTask<Object, Integer, Integer>{
		int ret;
		@Override
		protected void onPreExecute() {		
			url = AppSetting.getRootURL() + "createtable.php";
			GetShared();			
		}
		
		@Override
		protected Integer doInBackground(Object... params) {
			
			param = "?username="+ramUsername;		
			HttpGetData httpData = new HttpGetData();
			httpData.HttpGets(url,param);
			return ret;
		}

		@Override
		protected void onPostExecute(Integer result) {			
			
		}

	}
	
	
	
	private Integer gongGaoJsonHandle(String retResponse) {
		int ret =0;
		if(retResponse == null ){
			Log.i("ret", "获取公告失败");
			return 1;
		}
		try { 
			Log.i("gonggao", retResponse);
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
	
	private Integer patrolRecordJsonHandle(String retResponse) {
		int ret =0;
		if(retResponse == null ){
			Log.i("ret", "获取巡更失败");
			return 1;
		}
		try { 		
			Log.i("patrolRet", retResponse);
			JSONObject json = new JSONObject(retResponse);
			JSONArray foodsArray = json.getJSONArray("foods");			
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
