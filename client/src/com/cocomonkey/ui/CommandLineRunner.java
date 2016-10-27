package com.cocomonkey.ui;

import java.io.IOException;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.TimeoutException;
import com.cocomonkey.command.Command;
import com.cocomonkey.command.CommandHelper;
import com.cocomonkey.crawler.Crawler;
import com.cocomonkey.device.AndroidDevice;
import com.cocomonkey.device.DeviceManager;
import com.cocomonkey.mode.Mode;
import com.cocomonkey.mode.TraceMode;
import com.cocomonkey.util.Utils;

/**
 * 
 * @author shiyl
 * @date 2015年11月23日
 */
public class CommandLineRunner {

	private String pkgName;

	private String activityName;

	private String mArgs[];

	private int nextArgIndex = 0;

	private String serial;

	private CommandHelper helper;

	private Mode mode;

	private Crawler crawler;

	private IDevice device;

	public static void main(String[] args) {
		new CommandLineRunner().run(args);
	}

	public void run(String[] args) {
		mArgs = args;
		if (!parseArgs())
			return;

		if (!initDevice())
			return;

		initCrawler();
		while (true) {
			if (crawler.crawl().equals("complete")) {
				break;
			}
		}
		destroy();
	}

	public boolean initDevice() {
		DeviceManager.init();

		device = DeviceManager.getDevice(serial);
		if (device == null) {
			Utils.log("can't find device with serial " + serial);
			return false;
		}
		return true;
	}

	public void initCrawler() {

		AndroidDevice deviceInfo = new AndroidDevice(device);
		deviceInfo.setTargetActivity(activityName);
		deviceInfo.setTargetActivity(activityName);

		helper = new CommandHelper(device, pkgName + "/." + activityName);
		helper.open();

		crawler = new Crawler(device, new TraceMode(helper, deviceInfo), helper);

		Utils.clearLog();
		Utils.createLogFolder();
	}

	public void destroy() {
		mode.generateReport();
		helper.close();
		System.exit(0);
	}

	/**
	 * 
	 * @author shiyl
	 * @date 2015年12月7日
	 */
	public boolean parseArgs() {
		if (mArgs.length < 1) {
			return false;
		}
		String opt = null;
		try {
			while ((opt = nextOption()) != null) {
				if (opt.equals("-p")) {
					pkgName = nextOptionData();
				} else if (opt.equals("-a")) {
					activityName = nextOptionData();
				} else if (opt.equals("-s")) {
					serial = nextOptionData();
				} else if (opt.equals("-h")) {
					showHelp();
					return false;
				} else {
					showHelp();
					return false;
				}
			}
		} catch (RuntimeException e) {
			System.err.println(e.toString());
			showHelp();
			return false;
		}
		return true;

	}

	/**
	 * 获得命令行参数
	 * 
	 * @author shiyl
	 * @date 2015年12月7日
	 */
	public String nextOption() {
		if (nextArgIndex >= mArgs.length) {
			return null;
		}

		String nextArg = mArgs[nextArgIndex];

		nextArgIndex++;

		if (!nextArg.startsWith("-")) {
			return null;
		}

		if (nextArg.equals("--")) {
			return null;
		}
		return nextArg;
	}

	/**
	 * 获得参数具体数值
	 * 
	 * @author shiyl
	 * @date 2015年12月7日
	 */
	public String nextOptionData() {
		if (nextArgIndex >= mArgs.length) {
			return null;
		}

		String nextArgData = mArgs[nextArgIndex];

		nextArgIndex++;

		return nextArgData;
	}

	/**
	 * 帮助信息
	 * 
	 * @author shiyl
	 * @date 2015年12月7日
	 */
	public void showHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("Usage: java -jar monkeytest.jar [-options] [args]\r\n");
		sb.append("Options: \r\n");
		sb.append("        -p   target package name \r\n");
		sb.append("        -a   target activity name \r\n");
		sb.append("        -s   device serial number \r\n");
		Utils.log(sb.toString());
	}

}
