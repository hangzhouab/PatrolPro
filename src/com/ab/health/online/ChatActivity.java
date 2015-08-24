package com.ab.health.online;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ab.health.R;
import com.ab.health.utility.AppSetting;
import com.ab.health.online.BaiduPush;
import com.google.gson.Gson;
import com.ab.health.online.L;
import com.ab.health.online.RecentItem;
import com.ab.health.online.MessageAdapter;
import com.ab.health.online.User;
import com.ab.health.online.MessageItem;
import com.baidu.android.pushservice.PushManager;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.SwipeRefreshLayout;

public class ChatActivity extends Activity implements MyPushMessageReceiver.EventHandler,SwipeRefreshLayout.OnRefreshListener {

	private com.ab.health.online.Message mMsg;
	private BaiduPush baiduPush;
	private Gson gson;
	private EditText inputET;
	private MessageDB messageDB;
	private MessageAdapter adapter;
	private User mFromUser; 
	private ListView chatLV;
	private InputMethodManager imm;
	private RecentDB mRecentDB;
	private static int MsgPagerNum;
	public static final int NEW_MESSAGE = 0x001;// 收到消息
	public static final int REFRASH = 0x002;// 下拉刷新
	private SwipeRefreshLayout mSwipeLayout;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == NEW_MESSAGE) {
				// String message = (String) msg.obj;
				com.ab.health.online.Message msgItem = (com.ab.health.online.Message) msg.obj;
				String userId = msgItem.getUser_id();
				if (!userId.equals(mFromUser.getUserId()))// 如果不是当前正在聊天对象的消息，不处理
					return;

				int headId = msgItem.getHead_id();
				
				MessageItem item = new MessageItem(
						MessageItem.MESSAGE_TYPE_TEXT, msgItem.getNick(),
						System.currentTimeMillis(), msgItem.getMessage(),
						headId, true, 0);
				adapter.upDateMsg(item);
				
				messageDB.saveMsg(msgItem.getUser_id(), item);
				RecentItem recentItem = new RecentItem(userId, headId,
						msgItem.getNick(), msgItem.getMessage(), 0,
						System.currentTimeMillis());
				mRecentDB.saveRecent(recentItem);
				chatLV.setSelection(adapter.getCount() - 1);
			}
			if(msg.what == REFRASH ){
				MsgPagerNum++;
				List<MessageItem> msgList = initMsgData();
				int position = adapter.getCount();
				adapter.setMessageList(msgList);				
				chatLV.setSelection(adapter.getCount() - position - 1);
				
				mSwipeLayout.setRefreshing(false);
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		messageDB = new MessageDB(getApplicationContext());
		mRecentDB = new RecentDB(getApplicationContext());
		Click click = new Click();
		setContentView(R.layout.online_chat);
		chatLV = (ListView) findViewById(R.id.online_list);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_ly);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,  
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
		Button back = (Button) findViewById(R.id.tool_standardweight_back_btn);
		back.setOnClickListener(click);
		inputET = (EditText) findViewById(R.id.msg_et);
		Button send = (Button) findViewById(R.id.send_btn);
		send.setOnClickListener(click);
		MsgPagerNum = 0;
		mFromUser = (User) getIntent().getSerializableExtra("user");
		if (mFromUser == null) {// 如果为空，直接关闭
			finish();
		}		
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		TextView title = (TextView) findViewById(R.id.chat_titile);
		
		title.setText(mFromUser.getNick());
		
		adapter = new MessageAdapter(this, initMsgData());
		chatLV.setAdapter(adapter);
		chatLV.setSelection(adapter.getCount() - 1);
		baiduPush = new BaiduPush(BaiduPush.HTTP_METHOD_POST, AppSetting.PUSH_SECRIT_KEY, AppSetting.PUSH_API_KEY);
	}

	
	class Click implements View.OnClickListener{

		@Override  
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tool_standardweight_back_btn:
				finish();
				break;

			case R.id.send_btn:
				String inputMessage = inputET.getText().toString();
				SharedPreferences sp = getSharedPreferences(AppSetting.getSettingFile(), Context.MODE_PRIVATE);
	            String userid = sp.getString("PushUserId", "");
				String channelid = sp.getString("PushChannelId", "");
				String nickname = sp.getString("nicename", "");
				
				MessageItem item = new MessageItem(MessageItem.MESSAGE_TYPE_TEXT,
						userid, System.currentTimeMillis(), inputMessage, 0, false, 0);
				adapter.upDateMsg(item);
				
				chatLV.setSelection(adapter.getCount() - 1);
				messageDB.saveMsg(mFromUser.getUserId(), item);
				inputET.setText("");
				imm.hideSoftInputFromWindow(inputET.getWindowToken(), 0);
				mMsg = new com.ab.health.online.Message(
						userid, channelid, nickname, System.currentTimeMillis(), inputMessage, "");
				gson = new Gson();
				SendAsyncTask sendTask =  new SendAsyncTask();
				sendTask.execute();
				break;
			default:
				break;
			}
			
		}    	
    }
	
	class SendAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... message) {
			String result = gson.toJson(mMsg);
			Log.e("result", result);
			baiduPush.PushMessage(result, mFromUser.getUserId());	
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			   
			super.onPostExecute(result);
				
		}  
	}
	
	
	private List<MessageItem> initMsgData() {
		List<MessageItem> list = messageDB.getMsg(mFromUser.getUserId(),MsgPagerNum);
		
		List<MessageItem> msgList = new ArrayList<MessageItem>();// 消息对象数组
		if (list.size() > 0) {
			for (MessageItem entity : list) {				
				if (entity.getName().equals("")) {
					entity.setName(mFromUser.getNick());
				}
				
				msgList.add(entity);
			}
		}
		return msgList;

	}


	@Override
	protected void onPause() {
		super.onPause();
		MyPushMessageReceiver.ehList.remove(this);
		Log.i("pasue", "pause");
	}

	

	@Override
	protected void onResume() {		
		super.onResume();
		if(!PushManager.isPushEnabled(this)){
			PushManager.resumeWork(this);
		}
		
		MyPushMessageReceiver.ehList.add(this);
	}

	@Override
	public void onMessage(com.ab.health.online.Message message) {
		Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
		handlerMsg.obj = message;
		
		handler.sendMessage(handlerMsg);
	}


	@Override
	public void onRefresh() {		
		
		handler.sendEmptyMessageDelayed(REFRASH, 1000);
	}


	@Override
	public void onBind() {
		// TODO 自动生成的方法存根
		
	}


	@Override
	protected void onDestroy() {
		// TODO 自动生成的方法存根
		super.onDestroy();
		Log.i("destroidy", "pause");
	}


	
	
	
	
}
