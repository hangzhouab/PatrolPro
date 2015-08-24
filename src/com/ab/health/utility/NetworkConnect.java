package com.ab.health.utility;

import com.ab.health.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;

public class NetworkConnect {
	
	public static void AlertNotCon(final Context context){		
		AlertDialog.Builder bu = new Builder(context);
		LayoutInflater flater = ((Activity)context).getLayoutInflater();
		/*View titleView = flater.inflate(R.layout.alert_title_view, null);			
		bu.setCustomTitle(titleView);*/
		bu.setTitle("没有网络连接，请连接后再试！");
		bu.setPositiveButton("退出", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				((Activity)context).finish();
			}
		});
		bu.show();		
	}
	
	public static boolean isNetworkConnected(Context context) { 
		
		if (context != null) { 
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
			if (mNetworkInfo != null) { 
				return mNetworkInfo.isAvailable();
			}
		} 
		return false;
	}

}
