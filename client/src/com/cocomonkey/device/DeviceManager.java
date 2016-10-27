package com.cocomonkey.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IDevice.DeviceState;

/**
 * 设备管理类
 * 
 * @author shiyl
 * @date 2015年11月23日
 */
public class DeviceManager {

	public static boolean isAdbInited = false;

	public static AndroidDebugBridge bridge = null;

	public static final String adbLocation = "./adb";

	// private static final HashMap<String,AndroidDevice> deviceMap=new
	// HashMap<String,AndroidDevice>();

	private static final HashMap<IDevice, DeviceState> deviceMap = new HashMap<IDevice, DeviceState>();

	/**
	 * 初始化adb
	 * 
	 * @author shiyl
	 * @date 2015年11月23日
	 */
	public static void init() {
		if (!isAdbInited) {

			try {
				AndroidDebugBridge.init(false);

				isAdbInited = true;
				DeviceChangeListener listener = new DeviceChangeListener();
				AndroidDebugBridge.addDeviceChangeListener(listener); // 添加device状态监听
				bridge = AndroidDebugBridge.createBridge(adbLocation, false); // 建立adb连接
				Thread.sleep(2000);
			} catch (IllegalStateException e) {
				return;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * device状态监听器
	 * 
	 * @author shiyl
	 * @date 2015年11月23日
	 */
	static class DeviceChangeListener implements IDeviceChangeListener {

		public void deviceChanged(IDevice arg0, int arg1) {
			// TODO Auto-generated method stub
			deviceMap.put(arg0, arg0.getState());
		}

		public void deviceConnected(IDevice arg0) {
			// TODO Auto-generated method stub
			deviceMap.put(arg0, arg0.getState());
		}

		public void deviceDisconnected(IDevice arg0) {
			// TODO Auto-generated method stub
			deviceMap.remove(arg0);
		}

	}

	/**
	 * 获取online状态的device列表
	 * 
	 * @author shiyl
	 * @date 2015年11月24日
	 */
	public static List<IDevice> getOnlineDevices() {

		List<IDevice> olDeviceList = new ArrayList<IDevice>();
		for (IDevice device : deviceMap.keySet()) {
			if (deviceMap.get(device).equals(DeviceState.ONLINE))
				olDeviceList.add(device);

		}
		return olDeviceList;

	}

	/**
	 * 根据serial获取device
	 * 
	 * @author shiyl
	 * @date 2015年11月24日
	 */
	public static IDevice getDevice(String serial) {
		IDevice device = null;
		List<IDevice> devices = getOnlineDevices();
		for (int i = 0; i < devices.size(); i++) {
			if (devices.get(i).getSerialNumber().equals(serial)) {
				device = devices.get(i);
			}

		}
		return device;
	}

}
