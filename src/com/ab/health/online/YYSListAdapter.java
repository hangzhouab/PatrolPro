package com.ab.health.online;

import java.util.HashMap;
import java.util.List;

import com.ab.health.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

public class YYSListAdapter extends BaseAdapter {

	
	private Context context;
	private List<HashMap<String, Object>> yysData;
	private LayoutInflater mInflater;
	private User user;
	HashMap<String, Object> item;
	int positions;
	
	public YYSListAdapter( Context context, List<HashMap<String, Object>> yysData){		
		this.yysData = yysData;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {	
		return yysData.size();
	}

	@Override
	public Object getItem(int position) {
		
		return yysData.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		item = yysData.get(position);
		
		Log.i("position", String.valueOf(position));
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.view_online_list, null);
		}
		
		ImageView headImage = (ImageView) convertView.findViewById(R.id.yys_head);
		TextView nameTV = (TextView) convertView.findViewById(R.id.yys_name);
		TextView jieshaoTV = (TextView) convertView.findViewById(R.id.yys_jieshao);		
		headImage.setTag(position);
		
		String nickname = (String)item.get("nickname");
		String jieshao = (String)item.get("jieshao");
		nameTV.setText(nickname);
		jieshaoTV.setText( jieshao );
		
		if( nickname.equals("罗佳")){
			headImage.setImageDrawable(context.getResources().getDrawable(R.drawable.yys_luojia_head));				
		}else if (nickname.equals("王樱蓓")) {
			headImage.setImageDrawable(context.getResources().getDrawable(R.drawable.yys_wangyingbei_head));			
		}else if (nickname.equals("丁洁")) {
			headImage.setImageDrawable(context.getResources().getDrawable(R.drawable.yys_dingjie_head));			
		}else if (nickname.equals("郑小燕")) {
			headImage.setImageDrawable(context.getResources().getDrawable(R.drawable.yys_zhengxiaoyan_head));			
		}else if (nickname.equals("赵小娟")) {
			headImage.setImageDrawable(context.getResources().getDrawable(R.drawable.yys_zhaoxiaojuan_head));	
		}else if (nickname.equals("管红林")) {
			headImage.setImageDrawable(context.getResources().getDrawable(R.drawable.yys_guanhonglin_head));			
		}else if (nickname.equals("鲍彦")) {
			headImage.setImageDrawable(context.getResources().getDrawable(R.drawable.yys_baoyan_head));			
		}else if (nickname.equals("李冬梅")) {
			headImage.setImageDrawable(context.getResources().getDrawable(R.drawable.yys_lidongmei_head));
		}else if (nickname.equals("邓娜")) {
			headImage.setImageDrawable(context.getResources().getDrawable(R.drawable.yys_dengna_head));	
		}else if (nickname.equals("徐佳佳")) {
			headImage.setImageDrawable(context.getResources().getDrawable(R.drawable.yys_xujiajia_head));			
		}else if (nickname.equals("投诉处")) { 
			headImage.setImageDrawable(context.getResources().getDrawable(R.drawable.yys_server_head));
		}else{
			headImage.setImageDrawable(context.getResources().getDrawable(R.drawable.yys_other_head));
		}
		
		return convertView;
		
	}

}
