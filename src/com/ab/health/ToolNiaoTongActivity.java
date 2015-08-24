package com.ab.health;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ab.health.utility.DensityUtil;
import com.ab.health.utility.HttpGetData;
import com.ab.health.utility.AppSetting;
import com.ab.health.utility.NetworkConnect;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ToolNiaoTongActivity extends Activity implements OnCheckedChangeListener {


	private TextView btn_back;
	private OnClickLis onClickLis;
	private int selectColor = 0;
	private HttpGetData httpData;
	private String url,param,ret;
	private CheckBox color1,color2,color3,color4;
	private LayoutInflater flater;
	private Button saveBtn;
	private boolean isSubmit = false;
	private LinearLayout niaotongRecordly;
	private ArrayList<View> niaotongRecordList = new ArrayList<View>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if(!NetworkConnect.isNetworkConnected(this)){
			NetworkConnect.AlertNotCon(this);
		}else {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_niao);
			onClickLis = new OnClickLis();
			saveBtn = (Button) findViewById(R.id.act_physiology_record_weight);
			btn_back = (TextView) findViewById(R.id.targetWeightback);	
			btn_back.setOnClickListener(onClickLis);
			saveBtn.setOnClickListener(onClickLis);
			color1 = (CheckBox) findViewById(R.id.color1);
			color2 = (CheckBox) findViewById(R.id.color2);
			color3 = (CheckBox) findViewById(R.id.color3);
			color4 = (CheckBox) findViewById(R.id.color4);
			color1.setOnCheckedChangeListener(this);
			color2.setOnCheckedChangeListener(this);
			color3.setOnCheckedChangeListener(this);
			color4.setOnCheckedChangeListener(this);
			niaotongRecordly = (LinearLayout) findViewById(R.id.act_physiology_canvas_image_layout);
			httpData = new HttpGetData();
			
			NiaoTongAysnTask getHistory =  new NiaoTongAysnTask();
			getHistory.execute();
			
	
		}
		
	}
	

	private class NiaoTongAysnTask extends AsyncTask<Object, Integer, Integer>{
		String username;
		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
			
	    	
		}
		
		@Override
		protected Integer doInBackground(Object... params) {
			SharedPreferences userSP = getSharedPreferences(AppSetting.getSettingFile(), MODE_PRIVATE);
	    	username = userSP.getString("username", null);	
	    	if(isSubmit){
				url = AppSetting.getRootURL() + "niaotong_commit.php";    
				param = "?username=" + username + "&color=" + selectColor;   
				httpData.HttpGets(url, param);
	    	}
			url = AppSetting.getRootURL() + "user_niaotong.php";    
			param = "?username=" + username;   
			ret = httpData.HttpGets(url, param);	
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			flater = getLayoutInflater();	
			niaotongRecordList.clear();
			niaotongRecordly.removeAllViews();
			
			try {
				JSONObject json = new JSONObject(ret);
				JSONArray weightArray = json.getJSONArray("weights");
				for (int i = 0; i < weightArray.length(); i++) {
					View tempView = flater.inflate(R.layout.view_niaotong_image_item, null);
					TextView tempDate = (TextView) tempView.findViewById(R.id.act_physiology_physiology_date);
					TextView tempHeight = (TextView) tempView.findViewById(R.id.act_physiology_canvas_weight);
					ImageView tempColor = (ImageView) tempView.findViewById(R.id.act_physiology_physiology_image);
					
					JSONObject temp = (JSONObject) weightArray.opt(i);
					int color = Integer.valueOf(temp.getString("color"));
					String date = temp.getString("date");		
					float weighttemp = 70;
					int dpHeighttemp = DensityUtil.px2dip(getApplicationContext(), 135 - weighttemp );
					int dpHeight = DensityUtil.dip2px(getApplicationContext(), dpHeighttemp);
					RelativeLayout.LayoutParams paramheight = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					paramheight.topMargin = dpHeight;
					tempHeight.setLayoutParams(paramheight);
					tempDate.setText(date);
					switch (color) {
					case 1:
						tempColor.setImageDrawable(getResources().getDrawable(R.drawable.niaotong_1));
						break;
					case 2:
						tempColor.setImageDrawable(getResources().getDrawable(R.drawable.niaotong_2));
						break;
					case 3:
						tempColor.setImageDrawable(getResources().getDrawable(R.drawable.niaotong_3));
						break;
					case 4:
						tempColor.setImageDrawable(getResources().getDrawable(R.drawable.niaotong_4));
						break;

					default:
						break;
					}
					
					niaotongRecordList.add(tempView);
				}
				
			} catch (JSONException e) {
				
				e.printStackTrace();
			}		
			
			
			for (int i = 0; i < niaotongRecordList.size(); i++) {			
				View temp2 = niaotongRecordList.get(i);
				niaotongRecordly.addView(temp2);
			}	
		}

		

	}

	
	

	

	class OnClickLis implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.targetWeightback:
				finish();
				break;
			case R.id.act_physiology_record_weight:
				if(selectColor == 0){
					Toast.makeText(getApplicationContext(), "请先选择颜色", Toast.LENGTH_SHORT).show();
					return ;
				}
				isSubmit = true;
				NiaoTongAysnTask submit = new NiaoTongAysnTask();
				submit.execute();
				break;
			
			default:
				break;
			}
		}
	}






	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.color1:
			if(isChecked){
				color2.setChecked(false);
				color3.setChecked(false);
				color4.setChecked(false);
				selectColor = 1;
			}
			break;
		case R.id.color2:
			if(isChecked){
				color4.setChecked(false);
				color3.setChecked(false);
				color1.setChecked(false);
				selectColor = 2;
			}
			break;
		case R.id.color3:
			if(isChecked){
				color2.setChecked(false);
				color1.setChecked(false);
				color4.setChecked(false);
				selectColor = 3;
			}
			break;
		case R.id.color4:
			if(isChecked){
				color2.setChecked(false);
				color3.setChecked(false);
				color1.setChecked(false);
				selectColor = 4;
			}
			break;

		default:
			break;
		}
		
	}
	
	
	
	
}


