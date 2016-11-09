package com.cocomonkey.uiautomatortest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.util.Log;

import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import com.cocomonkey.handler.BackCommand;
import com.cocomonkey.handler.ClickCommand;
import com.cocomonkey.handler.CommandHandler;
import com.cocomonkey.handler.DumpCommand;
import com.cocomonkey.handler.ExitCommand;
import com.cocomonkey.handler.InputTextCommand;
import com.cocomonkey.handler.ScrollCommand;

public class UIServerSocket extends UiAutomatorTestCase {
	ServerSocket serverSocket;
	UIMonitor uiMonitor;
	PrintWriter pw;
	private final TheWatchers watchers = TheWatchers.getInstance();
	private final Timer timer = new Timer("WatchTimer");

	CommandHandler dumpCommand;

	public void testDemo() throws IOException {

		uiMonitor = UIMonitor.getInstance(this);
		uiMonitor.startMonite();

		final TimerTask updateWatchers = new TimerTask() {

			public void run() {
				try {
					watchers.check();
				} catch (final Exception e) {
				}
			}
		};
		timer.scheduleAtFixedRate(updateWatchers, 100, 100);
		
		Bundle bundle = getParams();
		String portStr = bundle.getString("port");
		int port = Integer.parseInt(portStr);
		Log.d("uiautomator", "serversocket port = "+portStr);
		serverSocket = new ServerSocket(port);
		Socket socket = serverSocket.accept();
		Log.d("uiautomator", "connect");

		initHandler();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		pw = new PrintWriter(socket.getOutputStream());
		String cmdStr = "";
		while ((cmdStr = br.readLine()) != null) {
			Log.d("uiautomator", cmdStr);
			String result = dumpCommand.handleCommand(cmdStr);
			sendStr(result);
			if(cmdStr.contains("ExitCommand"))
			{
				Log.d("uiautomator", "exit uiautomator");
				break;
			}
		}
		serverSocket.close();
		// Utils.log("complete");

	}

	private void initHandler() {
		dumpCommand = new DumpCommand();
		CommandHandler clickCommand = new ClickCommand();
		CommandHandler inputTextCommand = new InputTextCommand();
		CommandHandler scrollCommand = new ScrollCommand();
		CommandHandler backCommand = new BackCommand();
		CommandHandler exitCommand = new ExitCommand();

		dumpCommand.setNextHandler(clickCommand);
		clickCommand.setNextHandler(inputTextCommand);
		inputTextCommand.setNextHandler(scrollCommand);
		scrollCommand.setNextHandler(backCommand);
		backCommand.setNextHandler(exitCommand);

	}
	
	public void sendStr(String str) {
		if (pw != null) {
			pw.println(str);
			pw.flush();
		}
	}
}
