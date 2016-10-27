package com.cocomonkey.device;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IDevice.DeviceState;

/**
 * 
 * @author shiyl
 * @date 2015年11月23日
 */
public class AndroidDevice {
	
private final IDevice device;
	
	private DeviceState state;
	
	private String serialNum;
	
	private String pkgName;
	
	private String activityName;
	
	public AndroidDevice(IDevice device)
	{
		this.device=device;
	}
	
	public void setTargetPackage(String pkgName)
	{
		this.pkgName=pkgName;
	}
	
	public void setTargetActivity(String activityName)
	{
		this.activityName=activityName;
	}
	
	public String getPackageName()
	{
		return this.pkgName;
	}
	public String getActivityName()
	{
		return this.activityName;
	}

}
