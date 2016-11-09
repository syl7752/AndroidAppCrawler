package com.cocomonkey.util;

import android.util.Log;

public class Utils {
         
	   public static final String LOG_TAG="uiautomator";
	   
	   public static final boolean DEBUG = true;
	   
	   public static void log(String content)
	   {
		   if(DEBUG)
			   Log.d(LOG_TAG, "uiautomator test - "+content);
	   }
}
