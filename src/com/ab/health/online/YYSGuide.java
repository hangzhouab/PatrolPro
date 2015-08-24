package com.ab.health.online;

import java.io.InputStream;
import java.net.URL;

import com.ab.health.R;
import com.ab.health.R.id;
import com.ab.health.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class YYSGuide extends Activity {

	String userid,channelid,nickname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yys_guide);
		Intent intent = getIntent();
		nickname = intent.getStringExtra("nickname");
		userid = intent.getStringExtra("userid");
		channelid = intent.getStringExtra("channelid");
		
		TextView nameTV = (TextView) findViewById(R.id.yys_nickname);		
		nameTV.setText(nickname);
		Button back = (Button) findViewById(R.id.tool_standardweight_back_btn);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		TextView zixunTV = (TextView) findViewById(R.id.yys_zixun);		
		zixunTV.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				User user = new User(userid, channelid, nickname, 0, 0);
				Intent toChatIntent = new Intent(YYSGuide.this,ChatActivity.class);
				toChatIntent.putExtra("user", user);
				startActivity(toChatIntent);				
			}
		});
		
		
		TextView dengjiTV = (TextView) findViewById(R.id.yys_dengji);
		dengjiTV.setText(intent.getStringExtra("jieshao"));
		TextView jianjieTV = (TextView) findViewById(R.id.yys_jianjie);
		jianjieTV.setText(Html.fromHtml(intent.getStringExtra("jianjie")));
		ImageView imageTV = (ImageView) findViewById(R.id.yys_image);
		
		if( nickname.equals("罗佳")){
			imageTV.setImageDrawable(getResources().getDrawable(R.drawable.yys_luojia_body));				
		}else if (nickname.equals("王樱蓓")) {
			imageTV.setImageDrawable(getResources().getDrawable(R.drawable.yys_wangyingbei_body));			
		}else if (nickname.equals("丁洁")) {
			imageTV.setImageDrawable(getResources().getDrawable(R.drawable.yys_dingjie_body));			
		}else if (nickname.equals("郑小燕")) {
			imageTV.setImageDrawable(getResources().getDrawable(R.drawable.yys_zhengxiaoyan_body));			
		}else if (nickname.equals("赵小娟")) {
			imageTV.setImageDrawable(getResources().getDrawable(R.drawable.yys_zhaoxiaojuan_body));	
		}else if (nickname.equals("管红林")) {
			imageTV.setImageDrawable(getResources().getDrawable(R.drawable.yys_guanhonglin_body));			
		}else if (nickname.equals("鲍彦")) {
			imageTV.setImageDrawable(getResources().getDrawable(R.drawable.yys_baoyan_body));			
		}else if (nickname.equals("李冬梅")) {
			imageTV.setImageDrawable(getResources().getDrawable(R.drawable.yys_lidongmei_body));
		}else if (nickname.equals("邓娜")) {
			imageTV.setImageDrawable(getResources().getDrawable(R.drawable.yys_dengna_body));	
		}else if (nickname.equals("徐佳佳")) {
			imageTV.setImageDrawable(getResources().getDrawable(R.drawable.yys_xujiajia_body));			
		}else if (nickname.equals("投诉处")) { 
			imageTV.setImageDrawable(getResources().getDrawable(R.drawable.yys_server_body));
		}else{
			imageTV.setImageDrawable(getResources().getDrawable(R.drawable.yys_other_body));
		}
		
	}

}
