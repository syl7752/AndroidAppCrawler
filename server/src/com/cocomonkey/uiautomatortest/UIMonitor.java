package com.cocomonkey.uiautomatortest;

import android.util.Log;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.core.UiWatcher;

public class UIMonitor {

	private static UIMonitor uiMonitor;

	UIServerSocket socket;

	public static UIMonitor getInstance(UIServerSocket socket) {
		if (uiMonitor == null) {
			uiMonitor = new UIMonitor(socket);
		}
		return uiMonitor;
	}

	public UIMonitor(UIServerSocket socket) {
		this.socket = socket;
	}

	public void startMonite() {
		UiDevice.getInstance().registerWatcher("ANR", new UiWatcher() {

			public boolean checkForCondition() {
				// TODO Auto-generated method stub
				UiObject anrDialog = new UiObject(
						new UiSelector().packageName(
								"android").textContains("无响应"));
				if (anrDialog.exists()) {
					Log.d("uiautomator", "anr dialog exist");
					passErrorDialog();
					return true;
				}
				return false;
			}
		});

		UiDevice.getInstance().registerWatcher("ANR2", new UiWatcher() {

			public boolean checkForCondition() {
				// TODO Auto-generated method stub
				UiObject anrDialog = new UiObject(new UiSelector().packageName(
						"android").textContains("isn't responding."));
				if (anrDialog.exists()) {
					Log.d("uiautomator", "anr dialog exist");
					passErrorDialog2();
					return true;
				}
				return false;
			}
		});

		UiDevice.getInstance().registerWatcher("FC", new UiWatcher() {

			public boolean checkForCondition() {
				// TODO Auto-generated method stub
				UiObject fcDialog = new UiObject(new UiSelector().packageName(
						"android").textContains("停止运行"));
				if (fcDialog.exists()) {
					Log.d("uiautomator", "fc dialog exist");
					passErrorDialog();
					return true;
				}
				return false;
			}
		});

		UiDevice.getInstance().registerWatcher("FC2", new UiWatcher() {

			public boolean checkForCondition() {
				// TODO Auto-generated method stub
				UiObject fcDialog = new UiObject(new UiSelector().packageName(
						"android").textContains("has stopped"));
				if (fcDialog.exists()) {
					Log.d("uiautomator", "fc dialog exist");
					passErrorDialog2();
					return true;
				}
				return false;
			}
		});
	}
	
	public void passErrorDialog() {
		UiObject buttonOK = new UiObject(new UiSelector().text("确定").enabled(
				true));
		buttonOK.waitForExists(5000);
		try {
			buttonOK.click();
		} catch (UiObjectNotFoundException e) {
			Log.e("uiautomator", "Exception", e);
		}
	}
	
	public void passErrorDialog2() {
		UiObject buttonOK = new UiObject(new UiSelector().text("OK").enabled(
				true));
		buttonOK.waitForExists(5000);
		try {
			buttonOK.click();
		} catch (UiObjectNotFoundException e) {
			Log.e("uiautomator", "Exception", e);
		}
	}
	
}
