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
import android.widget.ImageView;

public class AdStartActivity extends Activity {

	private ImageView adImage;
	Bitmap pngBM;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ad_start);
		adImage = (ImageView) findViewById(R.id.start_ad);	
		  
		LoadAdImageAysnTask load = new LoadAdImageAysnTask();
		load.execute(1);
	}


	private class LoadAdImageAysnTask extends AsyncTask<Object, Integer, Integer>{
		@Override
		protected void onPreExecute() {	
		}  
		
		@Override
		protected Integer doInBackground(Object... params) {
//			try {
//    			URL picUrl = new URL(AppSetting.getRootURL()+ "ad.png");
//    			pngBM = BitmapFactory.decodeStream(picUrl.openStream());     			
//    			
//    		}catch (Exception e){ 
//    			e.printStackTrace();
//    		}    			
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(pngBM == null){
				pngBM = BitmapFactory.decodeResource(getResources(), R.drawable.ad);
				adImage.setImageBitmap(pngBM);	
			}else{
				adImage.setImageBitmap(pngBM);	
			}
			new Handler().postDelayed(new Runnable() {    
		           @Override
					public void run() {  	
		        	   	
						Intent intent = new Intent(AdStartActivity.this, MainActivity.class);						
						startActivity(intent);
						finish();
		           }
			},3500);
		}
	
	}
}
	

