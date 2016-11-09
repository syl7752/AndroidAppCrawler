package com.cocomonkey.handler;

import com.android.uiautomator.core.UiDevice;

public class ExitCommand extends CommandHandler{

	protected boolean handle(String cmdContent) {
		// TODO Auto-generated method stub
		return exitApp();
	}
    
	private boolean exitApp()
	{
		UiDevice device=UiDevice.getInstance();
		return device.pressHome();
	}
}
