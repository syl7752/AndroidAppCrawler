package com.cocomonkey.crawler;

import com.cocomonkey.type.Node;

/**
 * 
 * @author shiyl
 * @date 2015年11月25日
 */
public class View {
	private final String id;
	
	private final Node node;
	
	public View(String id,Node node)
	{
		this.id=id;
		this.node=node;
	}
	public String getId()
	{
		return this.id;
	}
	public Node getNode()
	{
		return this.node;
	}
	

}
