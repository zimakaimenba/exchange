package com.exchangeinfomanager.systemconfigration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;


public class DataBaseConfigration 
{
	private DataBaseConfigration ()
	{  
		this.curdbmap = new HashMap<String,CurDataBase> ();
		getSettingInfoFromXML ();
	}  

	
	private static Logger logger = Logger.getLogger(DataBaseConfigration.class);
	private String systeminstalledpath;

	private String curdatabasetype;
	private HashMap<String, CurDataBase> curdbmap;
	
	private CurDataBase curdbs; //�������ݿ�������Ϣ

	private int tryingcount = 0; //ϵͳ�������������3�Σ�3�β���ֱ���˳�
//	private String bkparsestoredpath;
	private int setSoftWareMode; //�趨ϵͳģʽ����2�֣��������ݺ�ͨ����ͬ�����ݡ�

	
//	private void getDataBaseInfoFromXML() 
//	{
//		
//		File sysconfigfile = new File(this.getDataBaseSettingFile() );
//		if(!sysconfigfile.exists()) { //�����ڣ��������ļ�������ʾ���ô����ÿͻ����������Ϣ
//			
//			try {
//				sysconfigfile.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			SystemSettingDialog systemset = new SystemSettingDialog ( ); 
//			systemset.setModal(true);
//			systemset.setVisible(true);
//			
//			getSettingInfoFromXML ();
//			
//		} else {
//			getSettingInfoFromXML ();
//		}
//	}
	

	private void getSettingInfoFromXML() 
	{
		Document document;
    	Element xmlroot ;
    	SAXReader reader ;
    	
    	FileInputStream xmlfileinput = null;
		try {
			xmlfileinput = new FileInputStream(this.getDataBaseSettingFile() );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		reader = new SAXReader();
	
		try {
			document = reader.read(xmlfileinput);
			xmlroot = document.getRootElement();
			
//			Element eletdx = xmlroot.element("tdxpah");
//			this.tdxinstalledpath = eletdx.getText();
//			
//			Element eletbkparsefile = xmlroot.element("nameofguanzhubankuai");
//			this.nameofguanzhubankuai = eletbkparsefile.getText();
//			
//			Element tdxzdyfilepath = xmlroot.element("tdxzdybkpath");
//			if(tdxzdyfilepath != null)
//				this.tdxzdybkpath = tdxzdyfilepath.getText();
//			else 
//				this.tdxzdybkpath = null;
//			
//			Element zhanbifengxizhouqi = xmlroot.element("zhanbifengxizhouqi");
//			this.zhanbifengxizhouqi = Integer.parseInt(zhanbifengxizhouqi.getText());
//			
//			Element priavtemodesetting = xmlroot.element("privatemode");
//			this.priavtemode = Boolean.parseBoolean(priavtemodesetting.getText());
//			
//			Element elepythoninterpreter = xmlroot.element("pythoninterper");
//			this.pythoninterpreter = elepythoninterpreter.getText();
//			
//			Element eleLicense = xmlroot.element("License");
//			if(eleLicense != null)
//				this.LinceseMac = eleLicense.getText();
//			else
//				this.LinceseMac = "";
			
			//���ݿ���Ϣ
			Element elesorce = xmlroot.element("databasesources");
			Iterator it = elesorce.elementIterator();
			 while (it.hasNext()) 
			 {
				 Element elementdbs = (Element) it.next();

				 logger.debug( elementdbs.attributeValue("dbsname") ) ;
				 CurDataBase tmpdb = new CurDataBase (elementdbs.attributeValue("dbsname"));
				 logger.debug( elementdbs.attributeValue("user") ) ;
				 tmpdb.setUser(elementdbs.attributeValue("user"));
				 logger.debug( elementdbs.attributeValue("password") ) ;
				 tmpdb.setPassWord(elementdbs.attributeValue("password"));
				 logger.debug( elementdbs.getText() ) ;
				 tmpdb.setDataBaseConStr(elementdbs.getText());
				 tmpdb.setCurDatabaseType( elementdbs.attributeValue("databasetype") );
				 logger.debug( elementdbs.attributeValue("curselecteddbs") ) ;
				 if(elementdbs.attributeValue("curselecteddbs").equals("yes")){
					 tmpdb.setCurrentSelectedDbs(true);
					 curdbs = tmpdb;
				 }
				 else
					 tmpdb.setCurrentSelectedDbs(false);
				 
				 curdbmap.put( elementdbs.attributeValue("dbsname"), tmpdb);
			 }
		
			try {
				xmlfileinput.close();
				tryingcount = 0;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (DocumentException e) {
			logger.debug(this.getDataBaseSettingFile() + "���ڴ������޸�");
			//e.printStackTrace();
			
			tryingcount ++;
			if( tryingcount == 3) {
				JOptionPane.showMessageDialog(null,"�����ļ���������ϵͳ�Զ��˳����������������ã��ټ���", "����",JOptionPane.INFORMATION_MESSAGE);  
				System.exit(0);
			}
			else {
				logger.debug(this.getDataBaseSettingFile());
				SystemSettingDialog systemset = new SystemSettingDialog (  );
				systemset.setModal(true);
				systemset.setVisible(true);
				getSettingInfoFromXML ();
			}
		}
	}

	// ����ʵ��  
	 public static DataBaseConfigration getInstance ()
	 {  
	        return Singtonle.instance;  
	 }
	 
	 private static class Singtonle 
	 {  
	        private static DataBaseConfigration instance =  new DataBaseConfigration ();  
	 }
	 
	 public String getSystemDataBaseType ()
	 {
		 return this.curdatabasetype;
	 }
	 
	 public CurDataBase getCurrentDatabaseSource ()
	 {
		 return curdbs;
	 }
	 public HashMap<String, CurDataBase> getgetCurrentAllDatabaseSources ()
	 {
		 return this.curdbmap;
	 }

	 /*
	  * 
	  */
//	 public String getSystemInstalledPath ()
//	 {
//		 return this.systeminstalledpath;
//	 }
	 
	 public String getDataBaseSettingFile () 
	 {
		 try{
//			    logger.debug(directory.getCanonicalPath());//��ȡ��׼��·��
//			    logger.debug(directory.getAbsolutePath());//��ȡ����·��
//			    this.systeminstalledpath = toUNIXpath(directory.getCanonicalPath()+ "\\");
			    Properties properties = System.getProperties();
			    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //�û����г���ĵ�ǰĿ¼
			    logger.debug(this.systeminstalledpath );
			}catch(Exception e)
			{
				System.exit(0);
			}
		 
		 String path =  this.systeminstalledpath + "/config/���ݿ�����.xml";
		 
		 if (java.nio.file.Files.notExists(Paths.get(path))) {
			 try {
				java.nio.file.Files.createDirectories(Paths.get(path).getParent() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 return path; 
	 }
	 
	 private  String toUNIXpath(String filePath) 
		{
			    return filePath.replace('\\', '/');
		 }
	 

}

