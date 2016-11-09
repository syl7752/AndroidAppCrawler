package com.cocomonkey.handler;

import com.android.uiautomator.core.UiDevice;
import com.cocomonkey.util.Utils;

import android.text.NoCopySpan.Concrete;

public abstract class CommandHandler {

	private CommandHandler nextHandler;

	private String result = "";

	protected abstract boolean handle(String cmdContent);

	public String handleCommand(String cmdStr) {
		String[] cmds = cmdStr.split(";");
		if (cmds.length == 0)
			return "error";
        Utils.log("aaa "+getClass().getSimpleName());
		String cmdType = cmds[0];
		if (cmdType.equals(getClass().getSimpleName())) {
			if (this.handle(cmdStr))
				return "success";
			else
				return "fail";
		} else {
			return this.nextHandler.handleCommand(cmdStr);
		}
	}

	public void setNextHandler(CommandHandler nextHandler) {
		this.nextHandler = nextHandler;
	}

	protected boolean pressBack() {
		UiDevice device = UiDevice.getInstance();
		if (device.pressBack()) {
			return true;
		}
		return false;
	}
}
