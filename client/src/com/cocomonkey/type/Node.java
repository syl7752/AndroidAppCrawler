package com.cocomonkey.type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cocomonkey.trace.FootPrint;
import com.google.common.base.Objects;

/**
 * 
 * @author shiyl
 * @date 2015年11月24日
 */
public class Node {
	
	//private final HashMap<String,FootPrint> fpMap=new HashMap<>();
	
	private final Set<FootPrint> fpList=new HashSet<>();

	private final UIElement element;
	private final List<Node> childs;
	
	private String id;
	private boolean isScrollable;

	public Node(UIElement element, List<Node> childs) {
		this.element = element;
		this.childs = childs;
	}

	public UIElement getElement() {
		return element;
	}

	public List<Node> getChilds() {
		return childs;
	}
	
	public void addFootPrint(FootPrint fp)
	{
		fpList.add(fp);
	}
	
	public Set<FootPrint> getFootPrint()
	{
		return this.fpList;
	}
	
	public int getFootPrintSize()
	{
		return this.fpList.size();
	}
	
	public void setViewId(String id)
	{
		this.id=id;
	}

	public String getId()
	{
		return this.id;
	}
	
	  @Override
	    public int hashCode() {
	    	// TODO Auto-generated method stub
	    	return Objects.hashCode(this.getElement().getCompareId());
	    }
	    
	    @Override
	    public boolean equals(Object obj) {
	    	// TODO Auto-generated method stub
	    	Node node=(Node) obj;
	    	if(this.getElement().getCompareId().equals(node.getElement().getCompareId()))
	    	{
	    		return true;
	    	}
	    	return false;
	    }
	    
	 public void addNode(Node node)
	 {
		 this.childs.add(node);
	 }
	 
	 public boolean canScroll()
	 {
		 return isScrollable;
	 }
	 public void setScoll(boolean isScrollable)
	 {
		 this.isScrollable=isScrollable;
	 }
}
