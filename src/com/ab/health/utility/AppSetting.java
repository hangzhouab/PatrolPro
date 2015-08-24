package com.ab.health.utility;

public class AppSetting {
	private  final static String url="http://www.lsanbang.com/fitness/";
	private  final static String settingfile ="settingbh";
	public final static String PUSH_API_KEY = "Cu8XF05UqqjGAbZzC33SHe25";
	public final static String PUSH_SECRIT_KEY = "ef0l0D02H8GBEqlkvHC1xfKeGArSaCRA";
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
	
}
