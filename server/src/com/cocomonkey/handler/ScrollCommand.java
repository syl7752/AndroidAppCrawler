package com.cocomonkey.handler;

import java.util.Random;

import com.android.uiautomator.core.Configurator;
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.cocomonkey.util.Utils;

import android.util.Log;

public class ScrollCommand extends CommandHandler {

	protected boolean handle(String cmdContent) {
		// TODO Auto-generated method stub
			return scrollVertical();
	}

	private boolean scrollHor() {
		UiDevice device = UiDevice.getInstance();
		int width = device.getDisplayWidth();
		int height = device.getDisplayHeight();
		return device.swipe(width * 4 / 5, height/2, width / 5, height / 2, 30);
	}
	private boolean scrollVertical() {
		UiDevice device = UiDevice.getInstance();
		int width = device.getDisplayWidth();
		int height = device.getDisplayHeight();
		return device.swipe(width / 2, height * 3 / 4, width / 2, height / 4, 30);
	}

}
