package com.cocomonkey.mode;

import com.cocomonkey.crawler.View;

/**
 * 
 * @author shiyl
 * @date 2015年11月24日
 */
public interface Mode {
	
	public String run(View view);
	
	public void generateReport();

}
