package com.ab.health.online;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.google.gson.Gson;

import com.ab.health.R;

import com.ab.health.online.RecentItem;
import com.ab.health.online.Message;
import com.ab.health.online.User;
import com.ab.health.utility.AppSetting;
import com.ab.health.utility.HttpGetData;
import com.ab.health.utility.TelPhone;

/**
 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
 * onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
 * onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调
 * 
 * 返回值中的errorCode，解释如下： 
 *  0 - Success
 *  10001 - Network Problem
 *  30600 - Internal Server Error
 *  30601 - Method Not Allowed 
 *  30602 - Request Params Not Valid
 *  30603 - Authentication Failed 
 *  30604 - Quota Use Up Payment Required 
 *  30605 - Data Required Not Found 
 *  30606 - Request Time Expires Timeout 
 *  30607 - Channel Token Timeout 
 *  30608 - Bind Relation Not Found 
 *  30609 - Bind Number Too Many
 * 
 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
 * 
 */
public class MyPushMessageReceiver extends FrontiaPushMessageReceiver {
    /** TAG to Log */
    public static final String TAG = MyPushMessageReceiver.class
            .getSimpleName();

    public static final int NOTIFY_ID = 0x000;
    public static int mNewNum = 0;// 通知栏新消息条目，我只是用了一个全局变量，
    public static abstract interface EventHandler {
		public abstract void onMessage(Message message);
		public abstract void onBind();
	}    
    public static ArrayList<EventHandler>  ehList = new ArrayList<EventHandler>();
    private MessageDB messageDB;
    private RecentDB recentDB;
    private String userid,channelid,username;
    private Context context;
    /**
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     * 
     * @param context
     *            BroadcastReceiver的执行Context
     * @param errorCode
     *            绑定接口返回值，0 - 成功
     * @param appid
     *            应用id。errorCode非0时为null
     * @param userId
     *            应用user id。errorCode非0时为null
     * @param channelId
     *            应用channel id。errorCode非0时为null
     * @param requestId
     *            向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
    @Override
    public void onBind(Context context, int errorCode, String appid,
    						String userId, String channelId, String requestId) {
    	userid = userId;
    	channelid = channelId;  
    	this.context = context;
    	SendAsyncTask send = new SendAsyncTask();
        send.execute();
    }
    
   
    class SendAsyncTask extends AsyncTask<Void, Void, String> {

    	HttpGetData httpData;
    	@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
		    SharedPreferences sp = context.getSharedPreferences(AppSetting.getSettingFile(), Context.MODE_PRIVATE);
	        Editor editor = sp.edit();	
	        editor.putString("PushUserId", userid);
	        editor.putString("PushChannelId", channelid);
	        username = sp.getString("username", "");
	        editor.commit();       
	        httpData = new HttpGetData();
		}    
    	
		@Override
		protected String doInBackground(Void... message) {
		
			String url = AppSetting.getRootURL() + "yyslist.php";
			String param = "?userid=" + userid + "&channelid=" + channelid + "&username=" + username;		
			httpData.HttpGets(url,param);
			return "";
		}
	}

    /**
     * 接收透传消息的函数。
     * 
     * @param context
     *            上下文
     * @param message
     *            推送的消息
     * @param customContentString
     *            自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String message,
            String customContentString) {
        
       
        Gson gson = new Gson();
        try {
        	Message msg = gson.fromJson(message, Message.class);
        	 if (ehList.size() > 0) {// 有监听的时候，传递下去
     			for (int i = 0; i < ehList.size(); i++)
     				((EventHandler) ehList.get(i)).onMessage(msg);
             }else {
            	 MessageItem messageItem = new MessageItem(MessageItem.MESSAGE_TYPE_TEXT,  msg.getNick(),
         				System.currentTimeMillis(), msg.getMessage(), 0, true, 1);
                 messageDB = new MessageDB(context);
                 messageDB.saveMsg(msg.getUser_id(), messageItem);
                 RecentItem recentItem = new RecentItem(msg.getUser_id(), msg.getHead_id(), msg.getNick(), 
                		 					msg.getMessage(), 0, System.currentTimeMillis());
                 recentDB = new RecentDB(context);
                 recentDB.saveRecent(recentItem);
                 showNotify(msg,context);
			}          
           
		} catch (Exception e) {
		
		}
        
     
    }

    
    
    
    @SuppressWarnings("deprecation")
	private void showNotify(Message msg,Context context) {
			
			mNewNum++;
			// 更新通知栏
		
			NotificationManager notificationManager = (NotificationManager)    
	            context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);   
			CharSequence tickerText = msg.getNick() + ":" + msg.getMessage();
	        // 定义Notification的各种属性   
	        Notification notification =new Notification(R.drawable.chat_message,   
	        		tickerText, System.currentTimeMillis()); 
	        //FLAG_AUTO_CANCEL   该通知能被状态栏的清除按钮给清除掉
	        //FLAG_NO_CLEAR      该通知不能被状态栏的清除按钮给清除掉
	        //FLAG_ONGOING_EVENT 通知放置在正在运行
	        //FLAG_INSISTENT     是否一直进行，比如音乐一直播放，知道用户响应
	//        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中   
	        notification.flags |= Notification.FLAG_AUTO_CANCEL; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用   
	        notification.flags |= Notification.FLAG_SHOW_LIGHTS;   
	        //DEFAULT_ALL     使用所有默认值，比如声音，震动，闪屏等等
	        //DEFAULT_LIGHTS  使用默认闪光提示
	        //DEFAULT_SOUNDS  使用默认提示声音
	        //DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission android:name="android.permission.VIBRATE" />权限
	        notification.defaults = Notification.DEFAULT_LIGHTS; 
	        
	        //叠加效果常量
	        //notification.defaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;
	        notification.ledARGB = Color.BLUE;   
	        notification.ledOnMS =3000; //闪光时间，毫秒
	          
	        // 设置通知的事件消息   
	        CharSequence contentTitle = "收到" + mNewNum + "条新消息"; // 通知栏标题   
	        
	        Intent notificationIntent =new Intent(context, RecentListActivity.class); // 点击该通知后要跳转的Activity
	     //   notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    //    User user = new User(msg.getUser_id(), msg.getChannel_id(), msg.getNick(), 0, 0);
	   //     notificationIntent.putExtra("user", user);
	        
	        PendingIntent contentItent = PendingIntent.getActivity(context, 0, notificationIntent, 0);   
	        notification.setLatestEventInfo(context, contentTitle, tickerText, contentItent);   
	          
	        // 把Notification传递给NotificationManager   
	        notificationManager.notify(0, notification); 
		
	}
    
    /**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     * 
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title,
            String description, String customContentString) {
        String notifyString = "通知点击 title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Log.d(TAG, notifyString);

        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * setTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     * @param successTags
     *            设置成功的tag
     * @param failTags
     *            设置失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onSetTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

    
    }

    /**
     * delTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
     * @param successTags
     *            成功删除的tag
     * @param failTags
     *            删除失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

    }

    /**
     * listTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示列举tag成功；非0表示失败。
     * @param tags
     *            当前应用设置的所有tag。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
            String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        Log.d(TAG, responseString);

     
    }

    /**
     * PushManager.stopWork() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Log.d(TAG, responseString);

        // 解绑定成功，设置未绑定flag，
        if (errorCode == 0) {
            Utils.setBind(context, false);
        }
      
    }

    
}
