package com.cocomonkey.handler;

import android.util.Log;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.cocomonkey.util.Utils;

public class ClickCommand extends CommandHandler{

	protected boolean handle(String cmdContent) {
		// TODO Auto-generated method stubounds
		String cmdStr=cmdContent.split(";")[1];
		if(!cmdStr.contains("bounds:"))
		{
		     return click(cmdStr);
		}else
		{
			String bounds=cmdStr.split(":")[1];
			String str=bounds.replace("][", ",");
			String [] mBounds=str.substring(1, str.length()-1).split(",");
			Utils.log("bounds = "+mBounds[0]+" "+mBounds[1]+" "+mBounds[2]+" "+mBounds[3]);
			if(mBounds.length==4){
				int x=(Integer.parseInt(mBounds[0])+Integer.parseInt(mBounds[2]))/2;
				int y=(Integer.parseInt(mBounds[1])+Integer.parseInt(mBounds[3]))/2;
				return click(x,y);
			}
			
		}
		return false;
	}
	
	private boolean click(String resourceId)
	{
		UiObject object=new UiObject(new UiSelector().resourceId(resourceId));
		try {
			return object.click();
		} catch (UiObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean click(int x,int y)
	{
		Utils.log("click "+x+","+y);
		UiDevice device=UiDevice.getInstance();
		return device.click(x, y);

	}
}
