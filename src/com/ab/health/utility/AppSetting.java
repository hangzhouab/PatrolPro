package com.ab.health.utility;

import com.ab.health.model.Guarder;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppSetting {
	private  final static String url="http://www.suoto.cn:8020/patrol/";
	private  final static String settingfile ="setting";
	public final static String PUSH_API_KEY = "Cu8XF05UqqjGAbZzC33SHe25";
	public final static String PUSH_SECRIT_KEY = "ef0l0D02H8GBEqlkvHC1xfKeGArSaCRA";
	
	// write settingFile's key
	public final static String username="username";
	public final static String orgnization="orgnization";
	public final static String phone="phone";
	public final static String isRegister = "isRegister";
	public final static String bumen="bumen";
	
	
	public static String getRootURL()
	{
		return url;
	}
	
	public static String getSettingFile(){
		return settingfile;
	}
	
	
	public static int getAndroidSDKVersion() { 
	   int version = 0; 
	   try { 
	     version = Integer.valueOf(android.os.Build.VERSION.SDK); 
	   } catch (NumberFormatException e) { 
	     e.toString();
	   } 
	   return version; 
	}
	
	public static void  writeAppConfig(Context ctx, Guarder guarder){
		SharedPreferences appSetting = ctx.getSharedPreferences(AppSetting.getSettingFile(), Context.MODE_PRIVATE);
		Editor editor = appSetting.edit();
		editor.putString("username",guarder.getName() );
		editor.putString("orgnization", guarder.getOrgnization());
//		editor.putString("height", height);
//		editor.putString("weight", weight);
//		editor.putString("target", target);
//		editor.putString("days", days);
//		editor.putString("password", password);
//		editor.putString("age", age);
//		editor.putInt("sex", sex);
		editor.putBoolean("NoRegister", false);	
		editor.commit();
	}
	
}
