package com.cocomonkey.type;

import com.google.common.base.Objects;

/**
 * 
 * @author shiyl
 * @date 2015年11月30日
 */
public class UIElement {
	
	private String text;
	
	private String resourceId;
	
	private String compareId;
	
	private String bounds;
	
	private boolean isScrollable;
	
	private boolean isClickable;
	
	private Type type;
	
	private static boolean isTraced=false;
	
	public enum Type { BUTTON, TEXT_INPUT_FIELD, IMAGE_VIEW,EDIT_TEXT,RADIO_BUTTON,UNKNOWN }
	
	public UIElement(String resourceId,String bounds,Type type,boolean isScrollable,boolean isClickable,String text)
	{
	     this.resourceId=resourceId;
	     this.bounds=bounds;
	     this.type=type;
	     this.isScrollable=isScrollable;
	     this.isClickable=isClickable;
	     this.text=text;
	     this.compareId=resourceId+","+bounds+","+type+","+isScrollable+","+isClickable;
	}
	
	public String getId()
	{
		return this.resourceId;
	}
	
	public String getBounds()
	{
		return this.bounds;
	}
	
	public boolean isClickable()
	{
		return this.isClickable;
	}
	
	public boolean isScrollable()
	{
		return this.isScrollable;
	}
	
	public Type getType()
	{
		return this.type;
	}
	
	public String getText()
	{
		return this.text;
	}
	
	/**
	 * 
	 * @author shiyl
	 * @date 2015年12月4日
	 */
	public String getCompareId()
	{
		return this.compareId;
	}
	
    @Override
    public int hashCode() {
    	// TODO Auto-generated method stub
    	return Objects.hashCode(this.compareId);
    }
    
    @Override
    public boolean equals(Object obj) {
    	// TODO Auto-generated method stub
    	UIElement element=(UIElement) obj;
    	if(this.compareId.equals(element.getCompareId()))
    	{
    		return true;
    	}
    	return false;
    }
    
    public void setTraced(boolean isTraced)
    {
    	this.isTraced=isTraced;
    }
    
    public boolean isTraced()
    {
    	return this.isTraced;
    }

}
