package com.ab.health;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
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
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;

import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.AbsListView.OnScrollListener;
import android.widget.RelativeLayout.LayoutParams;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.ab.health.R;

import com.ab.health.GongGaoActivity.ItemClick;

import com.ab.health.contactstview.ActivityTxl;
import com.ab.health.contactstview.CharacterParser;
import com.ab.health.contactstview.ClearEditText;
import com.ab.health.contactstview.PinyinComparator;
import com.ab.health.contactstview.SideBar;
import com.ab.health.contactstview.SortAdapter;
import com.ab.health.contactstview.SortModel;
import com.ab.health.model.User;

import com.ab.health.utility.DensityUtil;
import com.ab.health.utility.HealthUtility;
import com.ab.health.utility.HttpGetData;
import com.ab.health.utility.AppSetting;
import com.ab.health.utility.NetworkConnect;
import com.ab.health.utility.UpdateManager;
import com.ab.nfc.NFCAdapter;
import com.ab.nfc.NFCReader;


public class MainActivity extends Activity {

	private ViewFlipper contentViewPager = null;
	private LayoutInflater flater = null;
	private View viewGongGao, activityServer, activityTool,
			activityPhysiology;

	private Button btn_about, btn_bbs, btn_tool, btn_record,btn_addresslist;
			
	private OnClickListener onClickListener;
	private String url;
	private int recordCalorie = 0;
	private TextView titleBar, query,btn_loginout;
	private ArrayList<View> weightRecordList = new ArrayList<View>();
	private UpdateManager update;
	private LinearLayout bottom_record, bottom_tool, bottom_weight,
			bottom_server;
	

	
	// 公告 viewFliper
	private List<HashMap<String, String>> gonggaoData;
	private SimpleAdapter gongGaoAdapter;
	private ListView gonggaoLV;
	private boolean isComplelet = false;
	
	private HttpGetData httpData;
	private String subTitle;
	private GongGaoItemClick itemclick;
	
	// PatrolRecord viewFliper
	private List<HashMap<String, String>> patrolRecordData;
	private NFCAdapter patrolRecordAdapter;
	private ListView patrolRecordLV;	
	private ProgressBar patrolwaiter;
	private SoundPool soundPool;
	private String TAGaddress,tagid;
	private int year,month,day=0;
	private  CalendarView calendar;
	private Button refresh,noTagid;
	private boolean patroltouch=false;
	private String ramUsername,ramOrgniztion;
	
	
	// 通信录
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;	
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	private PinyinComparator pinyinComparator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(firstUse())
			return;
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
	
	
	
	private boolean firstUse() {
		SharedPreferences appSetting = getSharedPreferences( AppSetting.getSettingFile(), MODE_PRIVATE);		
		if (!appSetting.getBoolean(AppSetting.isRegister, false)) {			
			Intent intent = new Intent();
			intent.setClass(this, AdStartActivity.class);
			startActivity(intent);
			finish();
			return true;
		}		
		return false;
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
		}else{
			
			UploadPatrolRecordAysnTask upload = new UploadPatrolRecordAysnTask();
			upload.execute(0);
		}
	}

	private void InitGongGaoViewFliper() {
		
		gonggaoData = new ArrayList<HashMap<String, String>>();		
		gonggaoLV = (ListView) findViewById(R.id.act_courseSearch_data_lv);	
		
		gonggaoLV.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState ==  OnScrollListener.SCROLL_STATE_IDLE){
					if(gonggaoLV.getLastVisiblePosition() == (gonggaoLV.getCount()-1)){						
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
		activityTool = flater.inflate(R.layout.activity_patrol_record, contentViewPager);
		viewGongGao = flater.inflate(R.layout.activity_gonggao2, contentViewPager);
		
		
	//	activityPhysiology = flater.inflate(R.layout.fragment_patrol_query,	contentViewPager); 
		activityPhysiology = flater.inflate(R.layout.activity_txl,	contentViewPager); 
		activityServer = flater.inflate(R.layout.activity_setting_about,
				contentViewPager);
		onClickListener = new OnClickListener();

		calendar = (CalendarView) findViewById(R.id.patrol_query_calendar);
		calendar.setBackgroundColor(0xffcccccc);
		calendar.setOnDateChangeListener(new OnDateChangeListener() {
			
			@Override
			public void onSelectedDayChange(CalendarView view, int y, int m,
					int d) {				
				year = y;
				month = m;
				day = d;
			}
		});
		Time t = new Time();
		t.setToNow();
		year = t.year;
		month = t.month;
		day = t.monthDay;
		
		refresh = (Button) findViewById(R.id.patrol_refresh);
		refresh.setOnClickListener(onClickListener);
		query = (TextView) findViewById(R.id.topbar_query);
		query.setOnClickListener(onClickListener);
		noTagid = (Button) findViewById(R.id.patrol_noTagid);
		
		btn_record = (Button) findViewById(R.id.bottombar_record);
		btn_record.setOnClickListener(onClickListener);
		btn_bbs = (Button) findViewById(R.id.bottombar_bbs);
		btn_bbs.setOnClickListener(onClickListener);
		btn_about = (Button) findViewById(R.id.bottombar_about);
		btn_about.setOnClickListener(onClickListener);
		btn_tool = (Button) findViewById(R.id.bottombar_tool);
		btn_tool.setOnClickListener(onClickListener);


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
//		gonggao = (Button) findViewById(R.id.bottombar_gonggao);
//		gonggao.setOnClickListener(onClickListener);
//		btn_topbar_home = (Button) findViewById(R.id.bottombar_home);
//		btn_topbar_home.setOnClickListener(onClickListener);
		titleBar = (TextView) findViewById(R.id.titlebar_home_title);
		btn_addresslist = (Button) findViewById(R.id.topbar_address_list);
		btn_addresslist.setOnClickListener(onClickListener);
		
		btn_loginout = (TextView) findViewById(R.id.topbar_loginout);
		btn_loginout.setOnClickListener(onClickListener);
		

	}



	private void db() {
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
			c2.getFloat(c2.getColumnIndex("calorie"));
		}

		db.close();
	}


	private class OnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.bottombar_record:
				contentViewPager.setDisplayedChild(3);
				contentViewPager.showNext();
				titleBar.setText("巡更记录");
				bottom_record.setBackgroundColor(0Xff46cdd8);
				bottom_server.setBackgroundColor(0XFFFFFFFF);
				bottom_tool.setBackgroundColor(0XFFFFFFFF);
				bottom_weight.setBackgroundColor(0XFFFFFFFF);		
				query.setVisibility(View.GONE);
				btn_loginout.setVisibility(View.GONE);
				btn_addresslist.setVisibility(View.VISIBLE);
				break;

			case R.id.bottombar_tool:
				contentViewPager.setDisplayedChild(0);
				contentViewPager.showNext();
				titleBar.setText("单位公告");
				bottom_record.setBackgroundColor(0XFFFFFFFF);
				bottom_server.setBackgroundColor(0XFFFFFFFF);
				bottom_tool.setBackgroundColor(0Xff46cdd8);
				bottom_weight.setBackgroundColor(0XFFFFFFFF);
				query.setVisibility(View.GONE);
				btn_loginout.setVisibility(View.GONE);
				btn_addresslist.setVisibility(View.VISIBLE);
				break;
			case R.id.bottombar_bbs:
				btn_addresslist.setVisibility(View.GONE);
				contentViewPager.setDisplayedChild(1);
				contentViewPager.showNext();
				titleBar.setText("巡更查询");
				bottom_record.setBackgroundColor(0XFFFFFFFF);
				bottom_server.setBackgroundColor(0XFFFFFFFF);
				bottom_tool.setBackgroundColor(0XFFFFFFFF);
				bottom_weight.setBackgroundColor(0Xff46cdd8);
				query.setVisibility(View.VISIBLE);
				btn_loginout.setVisibility(View.GONE);
				break;
			case R.id.bottombar_about:
				contentViewPager.setDisplayedChild(2);
				contentViewPager.showNext();
				titleBar.setText("关于我们");
				bottom_record.setBackgroundColor(0XFFFFFFFF);
				bottom_server.setBackgroundColor(0Xff46cdd8);
				bottom_tool.setBackgroundColor(0XFFFFFFFF);
				bottom_weight.setBackgroundColor(0XFFFFFFFF);
				
				query.setVisibility(View.GONE);
				btn_addresslist.setVisibility(View.GONE);
				btn_loginout.setVisibility(View.VISIBLE);
				
				break;
			case R.id.changefitness:
				Intent intentplain = new Intent(MainActivity.this,
						ChangePlainActivity.class);
				startActivity(intentplain);
				overridePendingTransition(R.anim.slide_in_from_bottom,
						R.anim.slide_out_to_top);
				break;		
			
			case R.id.topbar_query:
				
				Intent intent = new Intent(MainActivity.this,PatrolQueryActivity.class);
				intent.putExtra("year", year);
				intent.putExtra("month", month);
				intent.putExtra("day", day);
				startActivity(intent);	
				break;	
			case R.id.patrol_refresh:	
				patrolwaiter.setVisibility(View.VISIBLE);
				refresh.setVisibility(View.GONE);
				LoadPatrolRecordAysnTask load = new LoadPatrolRecordAysnTask();
				load.execute(0);
				break;	
			case R.id.topbar_address_list:			
				Intent intent4 = new Intent(MainActivity.this,ActivityTxl.class);
			//	Intent intent4 = new Intent(MainActivity.this,TongXunLuActivity.class);
				startActivity(intent4);
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
		String ggURL,gonggaoParam;
		@Override
		protected void onPreExecute() {		
			GetShared();
			ggURL = AppSetting.getRootURL() + "gonggao.php";
			if( isComplelet ){ 
				Toast.makeText(getApplicationContext(), "全部信息已加载", Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		protected Integer doInBackground(Object... params) {			
			int sId = (Integer) params[0];
			gonggaoParam = "?startid="+sId+"&endid=10&unit=" + ramOrgniztion;	
			
			ret = gongGaoJsonHandle(httpData.HttpGets(ggURL,gonggaoParam));
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
		String loadParam,url;
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
			url = AppSetting.getRootURL() + "loadpatrol.php";
			GetShared();
		} 
		
		@Override
		protected Integer doInBackground(Object... params) {	
			int sId = (Integer) params[0];
			loadParam = "?username="+ramUsername + "&startid="+sId+"&endid=10" + "&unit=" + ramOrgniztion;		
			HttpGetData httpData = new HttpGetData();
			patrolRecordJsonHandle(httpData.HttpGets(url,loadParam));
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {				
			patrolwaiter.setVisibility(View.GONE);		
			refresh.setVisibility(View.GONE);
			noTagid.setVisibility(View.GONE);
			patrolRecordLV.setVisibility(View.VISIBLE);
			patrolRecordAdapter.notifyDataSetChanged();				
			if(patrolRecordData.size() <= 0){
				refresh.setVisibility(View.VISIBLE);
			}
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
	
	private static String byte2HexStr(byte[] b) {  
	    String hs = "";  
	    String stmp = "";  
	    for (int n = 0; n < b.length; n++) {  
	        stmp = (Integer.toHexString(b[n] & 0XFF));  
	        if (stmp.length() == 1)  
	            hs = hs + "0" + stmp;  
	        else  
	            hs = hs + stmp;  	        
	    }  
	    return hs.toUpperCase();  
	}  
	
	private class UploadPatrolRecordAysnTask extends AsyncTask<Object, Integer, Integer>{
		
		String uploadparam,url,ret;
		@Override
		protected void onPreExecute() {	
			Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
			if(tag != null){
				byte[] id = tag.getId();
				tagid = byte2HexStr(id);
			}
			soundPool = new SoundPool(10, AudioManager.STREAM_NOTIFICATION, 0);
			soundPool.load(getApplicationContext(), R.raw.notis, 1);
			url = AppSetting.getRootURL() + "patrolrecord.php";
			GetShared();
			uploadparam = "?username="+ramUsername + "&address=" + TAGaddress + "&tagid=" + tagid + "&unit=" + ramOrgniztion ;	
			
		}  
		
		@Override
		protected Integer doInBackground(Object... params) {	
			HttpGetData httpData = new HttpGetData();
			Log.i("upload", uploadparam);
			ret = httpData.HttpGets(url,uploadparam);
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {	
			if(ret.equals("tagid")){
				patrolRecordLV.setVisibility(View.GONE);
				patrolwaiter.setVisibility(View.GONE);	
				noTagid.setVisibility(View.VISIBLE);
				
			}else{
				LoadPatrolRecordAysnTask load = new LoadPatrolRecordAysnTask();
				load.execute(0);
				soundPool.play(1, 1, 1, 1, 0, 1);
			}
		}
	}
	
	private class CreateTableAysnTask extends AsyncTask<Object, Integer, Integer>{
		int ret;
		String url,param;
		@Override
		protected void onPreExecute() {		
			url = AppSetting.getRootURL() + "createtable.php";
			GetShared();			
		}
		
		@Override
		protected Integer doInBackground(Object... params) {
			
			param = "?username="+ramUsername + "&unit=" + ramOrgniztion;		
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
		Log.i("Patrolret", retResponse);
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
				String time = temp.getString("time");		
				String address = temp.getString("address");					
				courseItem.put("time", time);	
				courseItem.put("address", address);					
				patrolRecordData.add(courseItem);	
				  
			}			
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.i("excetion", retResponse);
			ret = 1;
		}
		return ret;
	}

}
