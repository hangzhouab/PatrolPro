package com.ab.health.online;

import java.util.LinkedList;
import java.util.regex.Matcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.ab.health.online.RecentAdapter;

import com.ab.health.online.MyPushMessageReceiver;
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

public class RecentAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private LinkedList<RecentItem> mData;
	private SwipeListView mListView;
	private MessageDB mMessageDB;
	private RecentDB mRecentDB;
	private Context mContext;

	public RecentAdapter(Context context, LinkedList<RecentItem> data,
			SwipeListView listview) {
		mContext = context;
		this.mInflater = LayoutInflater.from(context);
		mData = data;
		this.mListView = listview;
		mMessageDB = new MessageDB(mContext);
		mRecentDB = new RecentDB(mContext);
	}

	public void remove(int position) {
		if (position < mData.size()) {
			mData.remove(position);
			notifyDataSetChanged();
		}
	}

	public void remove(RecentItem item) {
		if (mData.contains(item)) {
			mData.remove(item);
			notifyDataSetChanged();
		}
	}

	public void addFirst(RecentItem item) {
		if (mData.contains(item)) {
			mData.remove(item);
		}
		mData.addFirst(item);
		L.i("addFirst: " + item);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final RecentItem item = mData.get(position);
		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.recent_listview_item, null);
		}
		TextView nickTV = (TextView) convertView
				.findViewById(R.id.recent_list_item_name);
		TextView msgTV = (TextView) convertView
				.findViewById(R.id.recent_list_item_msg);
		TextView numTV = (TextView) convertView.findViewById(R.id.unreadmsg);
		TextView timeTV = (TextView) convertView
				.findViewById(R.id.recent_list_item_time);
		
		Button deleteBtn = (Button) convertView
				.findViewById(R.id.recent_del_btn);
		nickTV.setText(item.getName());
		msgTV.setText(item.getMessage(),BufferType.SPANNABLE);
		timeTV.setText(TimeUtil.getChatTime(item.getTime()));
		
		int num = mMessageDB.getNewCount(item.getUserId());
		if (num > 0) {
			numTV.setVisibility(View.VISIBLE);
			numTV.setText(num + "");
		} else {
			numTV.setVisibility(View.GONE);
		}
		deleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mData.remove(position);
				mRecentDB.delRecent(item.getUserId());
				notifyDataSetChanged();
				if (mListView != null)
					mListView.closeOpenedItems();
			}
		});
		return convertView;
	}

}
