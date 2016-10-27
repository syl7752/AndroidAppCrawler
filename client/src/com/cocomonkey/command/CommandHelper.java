package com.cocomonkey.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.TimeoutException;
import com.cocomonkey.util.Utils;

/**
 * 
 * @author shiyl
 * @date 2015年11月25日
 */
public class CommandHelper {

	private Socket socket;

	public static final int UIAUTOMATOR_PORT = 4824;

	private BufferedReader br;

	private PrintWriter pw;

	private IDevice device;
	
	private String activity;

	public CommandHelper(IDevice device,String activity) {
		this.device = device;
		this.activity=activity;
	}
	
	public void open()
	{
		openServer();
		openConnect();
		this.sendConmand(new Command.HomeEvent());
	}

	/**
	 * 开启uiautomator server端
	 * 
	 * @author shiyl
	 * @date 2015年12月3日
	 */
	public void openServer() {
		try {
			device.pushFile("uiautomatortest.jar",
					"/sdcard/uiautomatortest.jar");
			Utils.log(System.getProperty("user.dir").toString());
			Runtime.getRuntime()
					.exec(Utils.ADB_PATH
							+ " shell uiautomator runtest /sdcard/uiautomatortest.jar"); //启动uiautomator测试服务端
			Thread.sleep(2000);

		} catch (TimeoutException | AdbCommandRejectedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SyncException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 建立socket连接
	 * 
	 * @author shiyl
	 * @date 2015年11月25日
	 */
	public void openConnect() {

		Utils.log("start connect socket");
		try {
			device.createForward(4824, 4824);  //adb forward 将4824端口映射到本地
			if (socket == null)
				socket = new Socket(InetAddress.getByName("127.0.0.1"),
						UIAUTOMATOR_PORT);
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			pw = new PrintWriter(socket.getOutputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AdbCommandRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 发送操作指令
	 * @author shiyl
	 * @date 2015年11月25日
	 */
	public String sendConmand(Command command) {
		String response = "";
		try {
			if (command instanceof Command.HomeEvent) {
				Utils.log("send home event");
				Utils.log(Utils.ADB_PATH+" shell am start -n "+activity);
				Runtime.getRuntime().exec(Utils.ADB_PATH+" shell am start -a android.intent.action.MAIN -n "+activity
						+" --activity-clear-top");
				Thread.sleep(5000);
			}else
			{
				Utils.log("send cmd " + command.getCommandContent());
				sendStr(command.getCommandContent());
				Thread.sleep(1000);
				response = br.readLine();
				if(response==null)
					response="fail";
				else if(response.equals("fc")||response.equals("anr"))
				{
					Utils.log("take screenshot");
					Utils.takeSnapShot(device,response);
					Utils.saveEventLog(response);
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Utils.log("get cmd response " + response);
		return response;
	}

	public void sendStr(String str) {
		pw.println(str);
		pw.flush();
	}
	
	public void close()
	{
		if(socket!=null)
		{
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
