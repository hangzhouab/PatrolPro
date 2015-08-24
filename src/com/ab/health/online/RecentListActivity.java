package com.ab.health.online;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.android.pushservice.PushManager;
import com.google.gson.Gson;
import com.ab.health.online.RecentAdapter;


import com.ab.health.online.MessageItem;
import com.ab.health.online.RecentItem;
import com.ab.health.online.User;

import com.ab.health.online.T;
import com.ab.health.online.MessageDB;
import com.ab.health.online.RecentDB;
import com.ab.health.online.UserDB;

import com.ab.health.R;

import com.ab.health.online.swipelistview.BaseSwipeListViewListener;
import com.ab.health.online.swipelistview.SwipeListView;

public class RecentListActivity extends Activity implements View.OnClickListener, MyPushMessageReceiver.EventHandler{
	public static final int NEW_MESSAGE = 0x000;// 有新消息
	public static final int NEW_FRIEND = 0x001;// 有好友加入

	private SwipeListView mRecentListView;
	private TextView mEmpty;
	private LinkedList<RecentItem> mRecentDatas;
	private RecentAdapter mAdapter;
	
	private UserDB mUserDB;
	private MessageDB mMsgDB;
	private RecentDB mRecentDB;
	
	private Gson mGson;

	
	private TextView mTitleName;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			case NEW_MESSAGE:
				// String message = (String) msg.obj;
				com.ab.health.online.Message msgItem = (com.ab.health.online.Message) msg.obj;
				String userId = msgItem.getUser_id();
				String nick = msgItem.getNick();
				String content = msgItem.getMessage();
				int headId = msgItem.getHead_id();
				
				if (mUserDB.selectInfo(userId) == null) {// 如果不存在此好友，则添加到数据库
					User user = new User(userId, msgItem.getChannel_id(), nick,
							headId, 0);
					mUserDB.addUser(user);
				}
				
				MessageItem item = new MessageItem(
						MessageItem.MESSAGE_TYPE_TEXT, nick,
						System.currentTimeMillis(), content, headId, true, 1);
				mMsgDB.saveMsg(userId, item);
				// 保存到最近会话列表
				RecentItem recentItem = new RecentItem(userId, headId, nick,
						content, 0, System.currentTimeMillis());
				mRecentDB.saveRecent(recentItem);
				mAdapter.addFirst(recentItem);
	//			T.showShort(mApplication, nick + ":" + content);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.online_recentlist);
		Button back = (Button) findViewById(R.id.tool_standardweight_back_btn);
		back.setOnClickListener(this);
		initData();
		initView(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!PushManager.isPushEnabled(this))
			PushManager.resumeWork(this);
	
		MyPushMessageReceiver.ehList.add(this);
		initRecentData();
//		mApplication.getNotificationManager().cancel(MyPushMessageReceiver.NOTIFY_ID);
		MyPushMessageReceiver.mNewNum = 0;
		if (mTitleName != null)
			mTitleName.requestFocusFromTouch();
	}

	@Override
	protected void onPause() {
		
		super.onPause();
		MyPushMessageReceiver.ehList.remove(this);// 暂停就移除监听
	}

	private void initData() {
		
		mGson = new Gson();
		mUserDB = new UserDB(getApplicationContext());
		mMsgDB = new MessageDB(getApplicationContext());
		mRecentDB = new RecentDB(getApplicationContext());
	}

	private void initRecentData() {
		// TODO Auto-generated method stub
		mRecentDatas = mRecentDB.getRecentList();
		mAdapter = new RecentAdapter(this, mRecentDatas, mRecentListView);
		mRecentListView.setAdapter(mAdapter);

	}

	public void upDateList() {
		initRecentData();
	}

	private void initView(Bundle savedInstanceState) {
		

	
		mTitleName = (TextView) findViewById(R.id.ivTitleName);
		

		mRecentListView = (SwipeListView) findViewById(R.id.recent_listview);
		mEmpty = (TextView) findViewById(R.id.empty);
		mRecentListView.setEmptyView(mEmpty);
		mRecentListView
				.setSwipeListViewListener(new BaseSwipeListViewListener() {
					@Override
					public void onOpened(int position, boolean toRight) {
					}

					@Override
					public void onClosed(int position, boolean fromRight) {
					}

					@Override
					public void onListChanged() {
					}

					@Override
					public void onMove(int position, float x) {
					}

					@Override
					public void onStartOpen(int position, int action,
							boolean right) {
						// L.d("swipe", String.format(
						// "onStartOpen %d - action %d", position, action));
					}

					@Override
					public void onStartClose(int position, boolean right) {
						// L.d("swipe",
						// String.format("onStartClose %d", position));
					}

					@Override
					public void onClickFrontView(int position) {
						Log.i("position", String.valueOf(position));
						RecentItem item = (RecentItem) mAdapter.getItem(position);
						User u = new User(item.getUserId(), "", item.getName(),
								item.getHeadImg(), 0);
						mMsgDB.clearNewCount(item.getUserId());
						Intent toChatIntent = new Intent(RecentListActivity.this,
								ChatActivity.class);
						toChatIntent.putExtra("user", u);
						startActivity(toChatIntent);
					}

					@Override
					public void onClickBackView(int position) {
						mRecentListView.closeOpenedItems();// 关闭打开的项
					}

					@Override
					public void onDismiss(int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							mAdapter.remove(position);
						}
						// mAdapter.notifyDataSetChanged();
					}
				});
	}





	@Override
	public void onMessage(com.ab.health.online.Message message) {
		Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
		handlerMsg.obj = message;
		handler.sendMessage(handlerMsg);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tool_standardweight_back_btn:
			finish();
			break;

		default:
			break;
		}
		
	}

	@Override
	public void onBind() {
		// TODO 自动生成的方法存根
		
	}

}
