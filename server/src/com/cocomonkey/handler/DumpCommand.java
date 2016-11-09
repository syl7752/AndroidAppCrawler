package com.cocomonkey.handler;

import java.io.File;

import android.os.Environment;

import com.android.uiautomator.core.UiDevice;
import com.cocomonkey.util.Utils;

public class DumpCommand extends CommandHandler{
	final File dumpFolder = new File(Environment.getDataDirectory(), "local/tmp");
	final String dumpFileName = "ui.xml";
	 final File dumpFile = new File(dumpFolder, dumpFileName);
	protected boolean handle(String cmdContent) {
		// TODO Auto-generated method stub
		Utils.log("start dump");
		dumpWindow();
		return true;
	}
	
	private void dumpWindow()
	{
		    dumpFolder.mkdirs();

		    dumpFile.delete();

		    try {
		      // dumpWindowHierarchy often has a NullPointerException
		      UiDevice.getInstance().dumpWindowHierarchy(dumpFileName);
		    } catch (Exception e) {
		      e.printStackTrace();
		      // If there's an error then the dumpfile may exist and be empty.
		      dumpFile.delete();
		    }
	}

}
