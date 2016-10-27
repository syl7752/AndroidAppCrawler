package com.cocomonkey.crawler;

import com.android.ddmlib.IDevice;
import com.cocomonkey.command.CommandHelper;
import com.cocomonkey.factory.DumpFactory;
import com.cocomonkey.mode.Mode;
import com.cocomonkey.type.Node;
import com.cocomonkey.util.Utils;

/**
 * 
 * @author shiyl
 * @date 2015年11月24日
 */
public class Crawler {
	
	private final IDevice device;
	
	private final DumpFactory factory;
	
	private Mode runMode;

	public Crawler(IDevice device,Mode mode,CommandHelper cmdHelper)
	{
		this.device=device;
		this.runMode=mode;
		factory=new DumpFactory(device,cmdHelper);
	}
	
	/**
	 * 
	 * @author shiyl
	 * @date 2015年11月24日
	 */
	public String crawl()
	{
		Utils.log("start crawl");
		String id=factory.getCurViewId();
		Node node=factory.dumpHierarchy();
		node.setViewId(id);
		View view=new View(id, node);
		return runMode.run(view);
	}
	

}
