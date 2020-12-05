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

		File directory = new File("");//�趨Ϊ��ǰ�ļ���
		try{
//		    logger.debug(directory.getCanonicalPath());//��ȡ��׼��·��
//		    logger.debug(directory.getAbsolutePath());//��ȡ����·��
//		    this.systeminstalledpath = toUNIXpath(directory.getCanonicalPath()+ "\\");
		    Properties properties = System.getProperties();
		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //�û����г���ĵ�ǰĿ¼
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
