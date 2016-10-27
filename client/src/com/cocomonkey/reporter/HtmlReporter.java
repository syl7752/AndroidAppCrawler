package com.cocomonkey.reporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cocomonkey.trace.ElementFileter;
import com.cocomonkey.trace.FootPrint;
import com.cocomonkey.trace.Recorder;
import com.cocomonkey.type.Node;
import com.cocomonkey.type.UIElement;
import com.cocomonkey.util.Utils;
/**
 * 
 * @author shiyl
 * @date 2015年12月16日
 */
public class HtmlReporter {
	private Recorder recorder;
	private String pkgName;
	private StringBuilder sb=new StringBuilder();
	private ElementFileter filter;
	public static final String NOT_TRACED="Not_Traced";
	
	public HtmlReporter(String pkgName,Recorder recorder,ElementFileter fiter)
	{
		this.pkgName=pkgName;
		this.recorder=recorder;
		this.filter=fiter;
		Utils.createFolder(Utils.HTML_PATH);
	}
	
	public void generate()
	{
		generateHeader();
		generateContent();
		outPutToFile(sb,Utils.HTML_PATH+File.separator+"index.html");
	}
	
	/**
	 * 
	 * @author shiyl
	 * @date 2015年12月16日
	 */
	private void generateHeader()
	{
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"zh\">"+"\r\n");
		sb.append("<head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\"/>"+"\r\n");
		sb.append("<link rel=\"stylesheet\" href=\"../.resources/report.css\" type=\"text/css\"/>"
				+ "<script type=\"text/javascript\" src=\"../.resources/sort.js\"></script></head>"+"\r\n");
		sb.append("<body onload=\"initialSort(['breadcrumb', 'coveragetable'])\">"+"\r\n");
		sb.append("<h1>"+pkgName+"</h1>"+"\r\n");
		sb.append("<table class=\"coverage\" cellspacing=\"0\" id=\"tracetable\"><thead><tr>"+"\r\n");
		sb.append("<td class=\"sortable\">Activity</td>"+"\r\n");
		sb.append("<td class=\"sortable\">Cov.</td>"+"\r\n");
		sb.append("<td class=\"sortable\">Missed</td>"+"\r\n");
		sb.append("<td class=\"sortable\">Total</td>"+"\r\n");
		sb.append("</tr></thead><tbody>\r\n");
	}

	
	/**
	 * 
	 * @author shiyl
	 * @date 2015年12月16日
	 */
	private void generateContent()
	{
		Map<String, Node> nodes=recorder.getSteps();
		Set keys=nodes.keySet();
		Iterator<String> itor=keys.iterator();
		while(itor.hasNext())
		{
			String key=itor.next();
			Node node=nodes.get(key);
			if(!node.getId().contains(pkgName))
				continue;
			
			List<FootPrint> validFootPrintList=fileterFootPrint(node.getFootPrint());
			
			List<UIElement> elements=filter.filterClickElement(node, new ArrayList<UIElement>());
			   
			double totalNodeCount=elements.size();
			double validNodeCount=validFootPrintList.size();
			double missedNodeCount=totalNodeCount-validNodeCount;
			String covPer="0%";
			if(validNodeCount!=0&&totalNodeCount!=0)
			{
				double per=validNodeCount/totalNodeCount;
				DecimalFormat df1 = new DecimalFormat("0.00%");        
				covPer= df1.format(per);
			}
				
			String activityName=node.getId().split("/.")[1];
		
		    sb.append("<tr><td> <a href=\""+activityName+".html\">"+node.getId()+"</a></td>");
		    sb.append("<td class=\"ctr2\">"+covPer+"</td>");
		    sb.append("<td class=\"ctr2\">"+(int)missedNodeCount+"</td>");
			sb.append("<td class=\"ctr1\">"+(int)totalNodeCount+"</td></tr>");
			
			generateDetailReport(activityName,validFootPrintList, elements);
			
		}
		sb.append("</tbody></table></body></html>");
	}
	
	/**
	 * 
	 * @author shiyl
	 * @date 2015年12月16日
	 */
	private List<FootPrint> fileterFootPrint(Set<FootPrint> footprintSet)
	{
		List<FootPrint> validElements=new ArrayList<>();
		for (FootPrint fp:footprintSet) {
			if(fp.getUIElement().isPresent())
			{
				validElements.add(fp);
			}
		}
		return validElements;
	}
	
	/**
	 * 
	 * @author shiyl
	 * @date 2015年12月16日
	 */
	private void outPutToFile(StringBuilder sb,String fileName)
	{
		File file=new File(fileName);
	
		PrintWriter pw=null;
		try {
			if(!file.exists())
				file.createNewFile();
			pw=new PrintWriter(file);
			pw.write(sb.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(pw!=null)
		      pw.close();
		}
		
	}
	
	/**
	 * 
	 * @author shiyl
	 * @date 2015年12月21日
	 */
	private void generateDetailReport(String name,List<FootPrint> validElements,List<UIElement> elements)
	{
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n");
		stringBuilder.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"zh\">"+"\r\n");
		stringBuilder.append("<head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\"/>"+"\r\n");
		stringBuilder.append("<link rel=\"stylesheet\" href=\"../.resources/report.css\" type=\"text/css\"/>"
				+ "<script type=\"text/javascript\" src=\"../.resources/sort.js\"></script></head>"+"\r\n");
		stringBuilder.append("<body onload=\"initialSort(['breadcrumb', 'coveragetable'])\">"+"\r\n");
		stringBuilder.append("<h1>"+name+"</h1>"+"\r\n");
		stringBuilder.append("<table class=\"coverage\" cellspacing=\"0\" id=\"tracetable\"><thead><tr>"+"\r\n");
		stringBuilder.append("<td class=\"sortable\">Id</td>"+"\r\n");	
		stringBuilder.append("<td class=\"sortable\">Type</td>"+"\r\n");
		stringBuilder.append("<td class=\"sortable\">Text</td>"+"\r\n");
		stringBuilder.append("<td class=\"sortable\">Bounds</td>"+"\r\n");
		stringBuilder.append("<td class=\"sortable\">Target</td>"+"\r\n");
		stringBuilder.append("<td class=\"sortable\">IsTraced</td>"+"\r\n");
		
		stringBuilder.append("</tr></thead><tbody>\r\n");
		
		for (UIElement element:elements) {
			stringBuilder.append("<tr><td>"+element.getId()+"</td>");
			stringBuilder.append("<td>"+element.getType().toString()+"</td>");
			stringBuilder.append("<td>"+element.getText()+"</td>");
			stringBuilder.append("<td>"+element.getBounds()+"</td>");
			String target=isContains(validElements, element);
			if(!target.equals(NOT_TRACED))
			{
				stringBuilder.append("<td>"+target+"</td>");
			    stringBuilder.append("<td><font color=\"#00CD00\">"+true+"</font></td></tr>");
			}
			else
			{
				stringBuilder.append("<td></td>");
				stringBuilder.append("<td><font color=\"#EE0000\">"+false+"</font></td></tr>");
			}
		}
		stringBuilder.append("</tbody></table></body></html>");
		
		outPutToFile(stringBuilder,Utils.HTML_PATH+File.separator+name+".html");
	}
	
	/**
	 * 
	 * @author shiyl
	 * @date 2015年12月21日
	 */
	private String isContains(List<FootPrint> validElements,UIElement element)
	{
		String result=NOT_TRACED;
		for (FootPrint fp:validElements) {
			if(fp.getUIElement().get().equals(element))
			{
				String sourceId=fp.getSource().getId();
				String targetId=fp.getTarget().getId();
				if(sourceId.equals(targetId))
				 result="";
				else
				 result=fp.getTarget().getId(); 
			}
		}
		return result;
	}

}
