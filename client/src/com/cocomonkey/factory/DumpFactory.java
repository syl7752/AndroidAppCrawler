package com.cocomonkey.factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.TimeoutException;
import com.cocomonkey.command.Command;
import com.cocomonkey.command.CommandHelper;
import com.cocomonkey.type.Node;
import com.cocomonkey.type.UIElement;
import com.cocomonkey.util.Utils;
/**
 * 获取dump的view信息以及当前activity类
 * @author shiyl
 * @date 2015年11月25日
 */
public class DumpFactory {
	
	private final IDevice device;
	
	private static final String UI_PATH="./ui.xml";
	
	private final CommandHelper helper;
	
	private boolean isScoll=false;
	
	
	private String excuteResult;
	
	
	
	public DumpFactory(IDevice device,CommandHelper cmdHelper)
	{
		this.device =device;
		this.helper=cmdHelper;
	}
	
	/**
	 * 获取当前的view id
	 * @author shiyl
	 * @date 2015年11月25日
	 */
	public String getCurViewId() 
	{
		String curViewId="";
		String line="";
		int index=0;
		try {
			//使用dumpsys activity获取当前界面的activity name
			Process process=Runtime.getRuntime().exec(Utils.ADB_PATH+" shell dumpsys activity top");
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			while((line=br.readLine())!=null)
			{
				index++;
				if(index==2)
				{
					String[] ids=line.trim().split(" ");
					if(ids.length>1)
					{
						curViewId=ids[1];
						Utils.log("curView = "+curViewId);
						break;
					}
				}
			}
			process.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		return curViewId;
	}
	
	/**
	 * 
	 * @author shiyl
	 * @date 2015年11月25日
	 */
	public Node dumpHierarchy()
	{
		isScoll=false;
		excuteDump();	
		return parseXml();
	}
	
	/**
	 * 执行dump操作，获取当前view的元素信息
	 * @author shiyl
	 * @date 2015年11月25日
	 */
	public void excuteDump()
	{
		try {
			Utils.log("start dump ui.xml");
			if(helper.sendConmand(new Command.DumpWindow()).equals("success"));
			{
				Thread.sleep(500);
			device.pullFile("/data/local/tmp/local/tmp/ui.xml", UI_PATH);
			}
			Thread.sleep(500);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SyncException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AdbCommandRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 解析dump后的xml文件
	 * @author shiyl
	 * @date 2015年11月25日
	 */
	public Node parseXml()
	{
		SAXReader reader = new SAXReader();
		Node targetNode=null;
		try {
			Document doc=reader.read(new FileInputStream(new File(UI_PATH)));
			Element sourceNode = doc.getRootElement();
			targetNode = transform(sourceNode);
			targetNode.setScoll(isScoll);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return targetNode;
	}
	
	/**
	 * 将元素转换为node
	 * @author shiyl
	 * @date 2015年11月25日
	 */
	public Node transform(Element source)
	{
		String resourceId="";
		String index="";
		String text="";
		UIElement uiElement=null;
		
		//如果元素信息里没有class属性，则为无效元素
		if(source.attribute("class")!=null)
		{

		if(source.attribute("resource-id")!=null)
		{
			resourceId=source.attribute("resource-id").getText();
		}
		
		if(source.attribute("bounds")!=null)
		{
			index=source.attribute("bounds").getText();
		}
		
		if(source.attribute("text")!=null)
		{
			text=source.attribute("text").getText();
		}
		UIElement.Type type = toType(source.attribute("class").getText());
		boolean isScrollable="true".equals(source.attribute("scrollable").getText());
		boolean isClickable="true".equals(source.attribute("clickable").getText());
		uiElement=new UIElement(resourceId,index, type, isScrollable, isClickable,text);
		
		if(isScrollable)
		    isScoll=true;
		}
		else
		{
			uiElement=new UIElement("","", UIElement.Type.UNKNOWN, false, false,"");
		}
		
		List<Node> nodeList=new ArrayList<>();
		Iterator itor=source.elementIterator();
		while(itor.hasNext())
		{
			Element element=(Element) itor.next();
			Node node=transform(element);
			nodeList.add(node);
		}
		
		return new Node(uiElement, nodeList);
	}
	
	/**
	 * 将元素的class转换为type
	 * @author shiyl
	 * @date 2015年11月25日
	 */
	public UIElement.Type toType(String clsName)
	{
		if("android.widget.TextView".equals(clsName)) {
			return UIElement.Type.TEXT_INPUT_FIELD;
		}

		if("android.widget.Button".equals(clsName)) {
			return UIElement.Type.BUTTON;
		}
		
		if("android.widget.ImageView".equals(clsName)) {
			return UIElement.Type.IMAGE_VIEW;
		}
		
		if("android.widget.EditText".equals(clsName)){
			return UIElement.Type.EDIT_TEXT;
		}	
		if("android.widget.EditText".equals(clsName)){
			return UIElement.Type.EDIT_TEXT;
		}
//		if("android.widget.RadioButton".equals(clsName)){
//			return UIElement.Type.RADIO_BUTTON;
//		}

		return UIElement.Type.UNKNOWN;
	}
}
