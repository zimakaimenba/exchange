package com.exchangeinfomanager.bankuaifengxi;

import java.awt.FlowLayout;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.JPanel;

public class HighLightSevice  
{
	private BanKuaiGeGuMatchCondition condition;
	private String systeminstalledpath;
	private Properties prop;
	private String properitesfile;

	public HighLightSevice (BanKuaiGeGuMatchCondition bkcondition, String properitesfile1) 
	{
		this.condition = bkcondition;
		this.properitesfile = properitesfile1;
		
		setPropertiesInfo ();
	}
	
	private void setPropertiesInfo ()
	{

		File directory = new File("");//设定为当前文件夹
		try{
//		    logger.debug(directory.getCanonicalPath());//获取标准的路径
//		    logger.debug(directory.getAbsolutePath());//获取绝对路径
//		    this.systeminstalledpath = toUNIXpath(directory.getCanonicalPath()+ "\\");
		    Properties properties = System.getProperties();
		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //用户运行程序的当前目录
		} catch(Exception e) {
			System.exit(0);
		}
		
		String propxmlFileName = null ;
		try {
			prop = new Properties();
			String propFileName = this.systeminstalledpath  + this.properitesfile ; //"/config/bankuaifenxihighlightsetting.properties"
			FileInputStream inputStream = new FileInputStream(propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			} 
			inputStream.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	private void setPropertiesToCondition ()
	{
		String column_width0  = prop.getProperty ("Dayujunxian");
		this.condition.setSettingMaFormula(column_width0);
	}
	
	private  String toUNIXpath(String filePath) 
	{
	    return filePath.replace('\\', '/');
	}
}
