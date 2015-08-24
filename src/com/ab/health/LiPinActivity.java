package com.ab.health;


import java.net.URL;
import com.ab.health.utility.AppSetting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LiPinActivity extends Activity {

	private LinearLayout loadingLL;
	private ImageView adImage;
	Bitmap pngBM;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lipinzhanshi);
		adImage = (ImageView) findViewById(R.id.start_ad);	
		loadingLL = (LinearLayout) findViewById(R.id.loading);
		Button back = (Button) findViewById(R.id.tool_standardweight_back_btn);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		
		LoadAdImageAysnTask load = new LoadAdImageAysnTask();
		load.execute(1);
	}


	private class LoadAdImageAysnTask extends AsyncTask<Object, Integer, Integer>{
		@Override
		protected void onPreExecute() {	
			
		}  
		
		@Override
		protected Integer doInBackground(Object... params) {
			try {
    			URL picUrl = new URL(AppSetting.getRootURL()+ "lipin.png");
    			pngBM = BitmapFactory.decodeStream(picUrl.openStream());     			
    			
    		}catch (Exception e){ 
    			e.printStackTrace();
    		}    			
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(pngBM == null){
				Toast.makeText(getApplicationContext(), "连接异常，请稍后再连接", Toast.LENGTH_SHORT).show();
			}else{
				loadingLL.setVisibility(View.GONE);
				adImage.setVisibility(View.VISIBLE);
				adImage.setImageBitmap(pngBM);	
			}
		
		}
	
	}
}
	

