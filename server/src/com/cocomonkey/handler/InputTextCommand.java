package com.cocomonkey.handler;

import java.util.Random;

import com.android.uiautomator.core.Configurator;
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.cocomonkey.util.Utils;

import android.util.Log;

public class InputTextCommand extends CommandHandler{

	protected boolean handle(String cmdContent) {
		// TODO Auto-generated method stub
		String cmdStr=cmdContent.split(";")[1];
		String text=cmdContent.split(";")[2];
		if(!cmdStr.trim().equals(""))
			return inputText(cmdStr,text);
		else
			return false;
	}
	
	private boolean inputText(String resourceId,String text)
	{
		Utils.log("input text - "+resourceId);
		boolean result=false;
		Configurator config=Configurator.getInstance();
		config.setKeyInjectionDelay(40);
		UiObject object=new UiObject(new UiSelector().resourceId(resourceId));
		try {
			result=object.setText(text);
		} catch (UiObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		config.setKeyInjectionDelay(0);
		//pressBack();    //退出输入法
		return result;
	}
	
}
