package com.cocomonkey.util;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;





import javax.imageio.ImageIO;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;

public class Utils {
	
	private static final boolean isDebug=true;
	
	public static final String PATH=System.getProperty("user.dir");
	
    public static final String ADB_PATH=PATH+File.separator+"adb";
    
    public static final String SCREENSHOT_PATH=PATH+File.separator+"screenshot";
    
    public static final String LOG_PATH=PATH+File.separator+"Log";
    
    public static final String LOG_FILE=LOG_PATH+File.separator+"event.log";
    
    public static final String HTML_PATH=PATH+File.separator+"html";
    
    public static final SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");
 
	/**
	 * 打印log
	 * @author shiyl
	 * @date 2015年11月30日
	 */
	public static void log(String content)
	{
		if(isDebug)
		System.out.println(content);
	}
	
	/**
	 * 
	 * @author shiyl
	 * @date 2015年12月10日
	 */
	public static void writeEventLog(String content)
	{
		File file = new File(LOG_FILE);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, true))); 
			out.write(getTime()+"--------"+content);
			out.close();
		} catch (FileNotFoundException e) {
			try {
				file.createNewFile();
			} catch (Exception exce) {
				exce.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void createFolder(String fileName)
	{
		File file=new File(fileName);
		if(!file.exists())
		{
			file.mkdirs();
		}
	}
	
	public static boolean takeSnapShot(IDevice device,String errorType)
	{
		   RawImage rawImage = null;  
	        try {  
	            rawImage = device.getScreenshot();  
	        } catch (Exception ioe) {  
	            System.out.println("Unable to get frame buffer: " + ioe.getMessage());  
	        }  
	   
	        if (rawImage == null)  
	            return false;  
	   
	        BufferedImage image = new BufferedImage(rawImage.width, rawImage.height,  
	                BufferedImage.TYPE_INT_ARGB);  
	  
	        int index = 0;  
	        int IndexInc = rawImage.bpp >> 3;  
	        for (int y = 0; y < rawImage.height; y++) {  
	            for (int x = 0; x < rawImage.width; x++) {  
	                int value = rawImage.getARGB(index);  
	                index += IndexInc;  
	                image.setRGB(x, y, value);  
	            }  
	        } 
	        String screenshotPath=SCREENSHOT_PATH+File.separator+getTime()+"_"+errorType+".png";
	        boolean result = false;
			try {
				result = ImageIO.write(image, "png", new File(screenshotPath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        if (result) {  
	            System.out.println("file is saved in:" + screenshotPath);  
	        }  
	        return result;
	}
	
	public static String getTime() {
		Date date = new Date();
		String time = format.format(date);
		return time;
	}
	public static void saveEventLog(String logName)
	{
		File srcFile=new File(LOG_FILE);
		File destFile=new File(LOG_PATH+File.separator+getTime()+"_"+logName);
		renameFile(srcFile, destFile);
		log("save event log complete");
	}
	public static void renameFile(File src,File dest)
	{
		if(!src.getName().equals(dest.getName()))
		{
			if(!src.exists())
			{
				log("src file is not exist");
				return;
			}
			if(dest.exists())
                log("dest has exist");
			else
				src.renameTo(dest);
		}
	}
	public static void clearLog()
	{
		deleteFolder(LOG_PATH);
		deleteFolder(SCREENSHOT_PATH);
		deleteFolder(HTML_PATH);
	}
	public static void deleteFolder(String folderPath)
	{
		File file=new File(folderPath);
		if(file.isDirectory())
		{
			for (File childFile:file.listFiles()) {
				deleteFolder(childFile.getAbsolutePath());
			}
		}
		else
		{
			file.delete();
		}
		
	}
	
	public static void createLogFolder()
	{
		createFolder(LOG_PATH);
		createFolder(SCREENSHOT_PATH);
		createFolder(HTML_PATH);
	}
}
