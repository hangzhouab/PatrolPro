package com.ab.health.online;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.health.GongGaoActivity;
import com.ab.health.GongGaoDetailActivity;
import com.ab.health.GuideActivity;
import com.ab.health.MainActivity;
import com.ab.health.R;
import com.ab.health.utility.AppSetting;
import com.ab.health.utility.HttpGetData;
import com.ab.health.utility.NetworkConnect;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

/*
 * 云推送Demo主Activity。
 * 代码中，注释以Push标注开头的，表示接下来的代码块是Push接口调用示例
 */
public class OnlineMainActivity extends Activity {

    private static final String TAG = OnlineMainActivity.class.getSimpleName();   
    TextView gotoRecentTV;

    public static int initialCnt = 0;    
    
    private ListView onlineLV; 
    private String retYYSlist;
    private List<HashMap<String, Object>> yysData;
    private YYSListAdapter yysAdapter;
    HashMap<String, Object> item;
    private LinearLayout loadingLL;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	if(!NetworkConnect.isNetworkConnected(this)){
			NetworkConnect.AlertNotCon(this);
			return;
    	}
    	
    	
        super.onCreate(savedInstanceState);
        Click click = new Click();
        ItemClick itemclick = new ItemClick();
        Resources resource = this.getResources();
        String pkgName = this.getPackageName();
        
        yysData =new ArrayList<HashMap<String, Object>>();
        setContentView(R.layout.online_main);
        loadingLL = (LinearLayout) findViewById(R.id.yyslist_loading);
        Button back = (Button) findViewById(R.id.tool_standardweight_back_btn);  
        back.setOnClickListener(click);
        gotoRecentTV = (TextView) findViewById(R.id.gotorecent);
        gotoRecentTV.setOnClickListener(click);
        onlineLV = (ListView) findViewById(R.id.online_list);
        onlineLV.setOnItemClickListener(itemclick);
   
        
		GetYYSListAsyncTask getYYS = new GetYYSListAsyncTask();
		getYYS.execute();
       
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
                getApplicationContext(), resource.getIdentifier(
                        "notification_custom_builder", "layout", pkgName),
                resource.getIdentifier("notification_icon", "id", pkgName),
                resource.getIdentifier("notification_title", "id", pkgName),
                resource.getIdentifier("notification_text", "id", pkgName));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE);  
   //     cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
       
        cBuilder.setLayoutDrawable(resource.getIdentifier(
                "simple_notification_icon", "drawable", pkgName));
        PushManager.setNotificationBuilder(this, 1, cBuilder);
    }
    
    
    
    class Click implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tool_standardweight_back_btn:
				finish();
				break;
			case R.id.gotorecent:
				Intent intent = new Intent(OnlineMainActivity.this, RecentListActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}  
			
		}
    	
    }
    
    class ItemClick implements AdapterView.OnItemClickListener{
    	String nickname,userid,channelid,jieshao,jianjie;
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			item = yysData.get(arg2);
			nickname = (String)item.get("nickname");
			userid = (String)item.get("userid");
			channelid = (String)item.get("channleid");
			jieshao = (String)item.get("jieshao");
			jianjie = (String)item.get("jianjie");
			
			final ImageView headImage = (ImageView) arg1.findViewById(R.id.yys_head);
			headImage.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					HashMap<String, Object> item2;
					item2 = yysData.get((Integer)v.getTag());
					nickname = (String)item2.get("nickname");
					userid = (String)item2.get("userid");
					channelid = (String)item2.get("channleid");
					jieshao = (String)item2.get("jieshao");
					jianjie = (String)item2.get("jianjie");
					Intent intent = new Intent();
					intent.setClass(OnlineMainActivity.this, YYSGuide.class);
					intent.putExtra("nickname", nickname);
					intent.putExtra("userid", userid);
					intent.putExtra("channelid", channelid);
					intent.putExtra("jianjie", jianjie);
					intent.putExtra("jieshao", jieshao);
					startActivity(intent); 
				}
			});
			
			SharedPreferences appSetting = getSharedPreferences(AppSetting.getSettingFile(), MODE_PRIVATE);
			Editor editor = appSetting.edit();	
			if(appSetting.getBoolean("isFirstViewYYS" + String.valueOf(arg2), true)){
				editor.putBoolean("isFirstViewYYS" + String.valueOf(arg2), false);
				editor.commit();
				Intent intent = new Intent();
				intent.setClass(OnlineMainActivity.this, YYSGuide.class);
				intent.putExtra("nickname", nickname);
				intent.putExtra("userid", userid);
				intent.putExtra("channelid", channelid);
				intent.putExtra("jianjie", jianjie);
				intent.putExtra("jieshao", jieshao);
				startActivity(intent); 
			}else {
				User user = new User(userid, channelid, nickname, 0, 0);
				Intent toChatIntent = new Intent(OnlineMainActivity.this,ChatActivity.class);
				toChatIntent.putExtra("user", user);
				startActivity(toChatIntent);
			}
			
		} 		
	}
    
    class GetYYSListAsyncTask extends AsyncTask<Void, Void, String> {
    	HttpGetData httpData;
    	@Override
		protected void onPreExecute() {			
			super.onPreExecute();
			
			loadingLL.setVisibility(View.VISIBLE);
			httpData = new HttpGetData();
		}  
    	
		@Override
		protected String doInBackground(Void... message) {		
			String url = AppSetting.getRootURL() + "get_yys_list.php";
			String param = "";		
			retYYSlist = httpData.HttpGets(url,param);				
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			loadingLL.setVisibility(View.GONE);
			try {					
				JSONObject json = new JSONObject(retYYSlist);
				JSONArray yysArray = json.getJSONArray("yys");
				for (int i = 0; i < yysArray.length(); i++) {
					JSONObject temp = (JSONObject) yysArray.opt(i);
					String userid = temp.getString("userid");
					String channelid = temp.getString("channelid");
					String username = temp.getString("username");
					String nickname = temp.getString("nickname");
					String jieshao = temp.getString("jieshao");
					String jianjie = temp.getString("jianjie");
					HashMap<String, Object> yysItem = new HashMap<String, Object>();
					yysItem.put("userid", userid);
					yysItem.put("channelid", channelid);		
					yysItem.put("username", username);	
					yysItem.put("nickname", nickname);
					yysItem.put("jieshao", jieshao);
					yysItem.put("jianjie", jianjie);
					yysData.add(yysItem);						
				}	
			yysAdapter = new YYSListAdapter(getApplicationContext(), yysData);
			onlineLV.setAdapter(yysAdapter);
			} catch (JSONException e) {					
				e.printStackTrace();
			}	
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(!PushManager.isPushEnabled(this)){
			PushManager.resumeWork(this);
		}
	}
    
}
