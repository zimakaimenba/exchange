package com.exchangeinfomanager.systemconfigration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.exchangeinfomanager.bankuaifengxi.ai.JiaRuJiHua;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;


public class SystemConfigration 
{
	private SystemConfigration ()
	{  
		getSystemInfoFromXML ();
	}  
	public Charset charSet ()
	{
		Charset charset = Charset.forName("GBK") ;
		return charset;
	}
	public  String toUNIXpath(String filePath) 
	{
		    return filePath.replace('\\', '/');
	}
	
	private static Logger logger = Logger.getLogger(SystemConfigration.class);
	private String systeminstalledpath;
	private String tdxinstalledpath;
	private String curdatabasetype;
	private CurDataBase curdbs; //�������ݿ�������Ϣ
	private CurDataBase rmtcurdb; //Զ�����ݿ�������Ϣ
	private int tryingcount = 0; //ϵͳ�������������3�Σ�3�β���ֱ���˳�
	private String bkparsestoredpath;
	private int setSoftWareMode; //�趨ϵͳģʽ����2�֣��������ݺ�ͨ����ͬ�����ݡ�
	private String datatablesfromserver;
	private String gephifileexportparth;
	public static int MODELSERVER=0, MODELCLIENT=1, MODELSERVERCLIENT=2;
	private int givenperiodofmonth;
	private int zhanbifengxizhouqi; //�ɽ���ռ�ȷ�������
	private double bkdpzhanbimarker;
	private double ggdpzhanbimarker;
	private Boolean priavtemode;
	private String pythoninterpreter;
	
	private void getSystemInfoFromXML() 
	{
		File directory = new File("");//�趨Ϊ��ǰ�ļ���
		try{
//		    logger.debug(directory.getCanonicalPath());//��ȡ��׼��·��
//		    logger.debug(directory.getAbsolutePath());//��ȡ����·��
//		    this.systeminstalledpath = toUNIXpath(directory.getCanonicalPath()+ "\\");
		    Properties properties = System.getProperties();
		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //�û����г���ĵ�ǰĿ¼
		    logger.debug(this.systeminstalledpath );
		}catch(Exception e)
		{
			System.exit(0);
		}
		
		File sysconfigfile = new File(this.getSysSettingFile() );
		if(!sysconfigfile.exists()) { //�����ڣ��������ļ�������ʾ���ô����ÿͻ����������Ϣ
			
			try {
				sysconfigfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			SystemSetting systemset = new SystemSetting ( this.getSysSettingFile() ); 
			systemset.setModal(true);
			systemset.setVisible(true);
			
			getSettingInfoFromXML ();
			
		} else {
			getSettingInfoFromXML ();
		}
	}
	public boolean reconfigSystemSettings ()
	{
		SystemSetting systemset = new SystemSetting ( this.getSysSettingFile() ); 
		systemset.setModal(true);
		systemset.setVisible(true);
		
		if(systemset.isNewSystemSetting ()) {
			getSettingInfoFromXML ();
			return true;
		}
		else return false;
		
	}

	private void getSettingInfoFromXML() 
	{
		Document document;
    	Element xmlroot ;
    	SAXReader reader ;
    	
    	FileInputStream xmlfileinput = null;
		try {
			xmlfileinput = new FileInputStream(this.getSysSettingFile() );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		reader = new SAXReader();
	
		try {
			document = reader.read(xmlfileinput);
			xmlroot = document.getRootElement();
			
			Element eletdx = xmlroot.element("tdxpah");
			this.tdxinstalledpath = eletdx.getText();
			
			Element eletbkparsefile = xmlroot.element("bankuaiparsefilepah");
			this.bkparsestoredpath = eletbkparsefile.getText();
			
			Element gephifilepath = xmlroot.element("gephifilepath");
			this.gephifileexportparth = gephifilepath.getText();
			
			
			Element zhanbifengxizhouqi = xmlroot.element("zhanbifengxizhouqi");
			this.zhanbifengxizhouqi = Integer.parseInt(zhanbifengxizhouqi.getText());
			
			Element priavtemodesetting = xmlroot.element("privatemode");
			this.priavtemode = Boolean.parseBoolean(priavtemodesetting.getText());
			
			Element elepythoninterpreter = xmlroot.element("pythoninterper");
			this.pythoninterpreter = elepythoninterpreter.getText();
			
			 
			
			//�������ݿ���Ϣ
			Element elesorce = xmlroot.element("databasesources");
			Iterator it = elesorce.elementIterator();
			while (it.hasNext()) 
			{
				Element tmpelement = (Element) it.next();
				String cursel = tmpelement.attributeValue("curselecteddbs");
				if(tmpelement.attributeValue("curselecteddbs").equals("yes")) {
					curdbs = new CurDataBase (tmpelement.attributeValue("dbsname") );
//					logger.debug( tmpelement.getText() );
//					logger.debug(tmpelement.attributeValue("user") );
//					logger.debug( tmpelement.attributeValue("password") );
//					logger.debug( tmpelement.attributeValue("databasetype").trim()  ) ;
					
					curdbs.setDataBaseConStr (tmpelement.getText() );
					curdbs.setUser (tmpelement.attributeValue("user").trim() );
					curdbs.setPassWord (tmpelement.attributeValue("password").trim() );
					curdbs.setCurDatabaseType(tmpelement.attributeValue("databasetype").trim());
				}
			}
			//Զ�����ݿ���Ϣ
			Element elermtsorce = xmlroot.element("serverdatabasesources");
			Iterator itrmt = elermtsorce.elementIterator();
			while (itrmt.hasNext()) 
			{
				Element tmpelement = (Element) itrmt.next();
				String cursel = tmpelement.attributeValue("curselecteddbs");
				if(tmpelement.attributeValue("curselecteddbs").equals("yes")) {
					rmtcurdb = new CurDataBase (tmpelement.attributeValue("dbsname") );
					logger.debug( tmpelement.getText() );
					logger.debug(tmpelement.attributeValue("user") );
					logger.debug( tmpelement.attributeValue("password") );
					logger.debug( tmpelement.attributeValue("databasetype").trim()  ) ;
					
					if(tmpelement.getText().trim().equals(curdbs.getCurDatabaseType() ) )
						rmtcurdb = curdbs;
					else {
						rmtcurdb.setDataBaseConStr (tmpelement.getText() );
						rmtcurdb.setUser (tmpelement.attributeValue("user").trim() );
						rmtcurdb.setPassWord (tmpelement.attributeValue("password").trim() );
						rmtcurdb.setCurDatabaseType(tmpelement.attributeValue("databasetype").trim());
					}
				}
			}
			
//			//��Щ���ݴ�server��ȡ
//			Element elermtdatatablefromserver = xmlroot.element("tablesfromserver");
//			this.datatablesfromserver = elermtdatatablefromserver.getText();
			
			try {
				xmlfileinput.close();
				tryingcount = 0;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (DocumentException e) {
			logger.debug(this.getSysSettingFile() + "���ڴ������޸�");
			//e.printStackTrace();
			
			tryingcount ++;
			if( tryingcount == 3) {
				JOptionPane.showMessageDialog(null,"�����ļ���������ϵͳ�Զ��˳����������������ã��ټ���", "����",JOptionPane.INFORMATION_MESSAGE);  
				System.exit(0);
			}
			else {
				logger.debug(this.getSysSettingFile());
				SystemSetting systemset = new SystemSetting ( this.getSysSettingFile() );
				systemset.setModal(true);
				systemset.setVisible(true);
				getSettingInfoFromXML ();
			}
		}
	}

	// ����ʵ��  
	 public static SystemConfigration getInstance ()
	 {  
	        return Singtonle.instance;  
	 }
	 
	 private static class Singtonle 
	 {  
	        private static SystemConfigration instance =  new SystemConfigration ();  
	 }
//	 public CurDataBase getCurrentDataBase ()
//	 {
//		 return curdbs;
//	 }
	 public String getSystemDataBaseType ()
	 {
		 return this.curdatabasetype;
	 }
	 public String getTdxFoxProFileSource ()
	 {
//		 //"Provider=vfpoledb;Data Source=C:\\path\\to\\Data\\;Collating Sequence=general;";
//		 //urlToDababasecrypt = "jdbc:ucanaccess://" + dbconnectstr + ";jackcessOpener=com.exchangeinfomanager.database.CryptCodecOpener";
//		 CurDataBase foxprodbs = new CurDataBase ("FoxPro");
//		 foxprodbs.setUser("sa");
//		 foxprodbs.setPassWord("");
////		 String dbconstr = "Provider=vfpoledb;Data Source="
////				 			+ this.tdxinstalledpath + "T0002/hq_cache/base.dbf"
////				 			+ ";Collating Sequence=general;"
////				 			;
//		 String dbconstr =
//					"jdbc:odbc:Driver={Microsoft FoxPro VFP Driver (*.dbf)};" + 		//д����Թ̶�
//					"UID=;"+
//					"Deleted=Yes;"+
//					"Null=Yes;"+
//					"Collate=Machine;"+
//					"BackgroundFetch=Yes;"+
//					"Exclusive=No;"+
//					"SourceType=DBF;"+     							//�˴�ָ�������ļ��ĺ�׺
//					"SourceDB=" + this.tdxinstalledpath + "T0002/hq_cache/base.dbf"; 	//�˴�Ϊdbf�ļ����ڵ�Ŀ¼
//		 foxprodbs.setDataBaseConStr (dbconstr);
		 
		 return  this.tdxinstalledpath + "T0002/hq_cache/base.dbf";
	 }
	 public CurDataBase getCurrentDatabaseSource ()
	 {
		 return curdbs;
	 }
	 public  CurDataBase getRemoteCurrentDatabaseSource ()
	 {
		 return rmtcurdb;
	 } 
//	 /*
//	  * ��Щ���ݴ�server���ȡ
//	  */
//	 public String getTablesDataSelectedFromServer ()
//	 {
//		 return this.datatablesfromserver;
//	 }
	 /*
	  * 
	  */
	 public String getSystemInstalledPath ()
	 {
		 return this.systeminstalledpath;
	 }
	 /*
	  * 
	  */
	 public String getTdxZdgzReportFile ()
	 {
		return this.getSystemInstalledPath() + "/reports/TDX�ص��ע��ʷ����.txt";
	 }
		/**
		 * @return the tdxbbfilenamegainiantishi
		 */
		public String getTdxBbFileGaiNianTiShi() {
			return this.getSystemInstalledPath() + "/reports/TDX������ʾ����.txt";
		}
		/**
		 * @return the tdxbbfilenamefumianxiaoxi
		 */
		public String getTdxBbfileFuMianXiaoXi() {
			return this.getSystemInstalledPath()  + "/reports/TDX������Ϣ����.txt";
		}
		/**
		 * @return the tdxbbfilenamezfxgkhzd
		 */
		public String getTdxBbFileZzfxgkhzd() {
			return this.getSystemInstalledPath() + "/reports/TDX������ؿͻ����Ա���.txt";
		}
		public String getTDXChanYeLianReportFile() 
		{
			return this.getSystemInstalledPath() + "/reports/TDX���ɲ�ҵ�����汨��.txt";
		}
		/*
		 * 
		 */
		public String getTDXModelMatchExportFile ()
		{
			return this.getSystemInstalledPath() + "/weeklyreports/ռ��ģ�����/";
		}
		/*
		 * 
		 */
		 public String getShiZhiFenXiFilesStoredPath ()
		 {
//			 return this.bkparsestoredpath;
			 return this.getSystemInstalledPath() + "/weeklyreports/��ֵ�������/";
		 }
		 /*
		  * 
		  */
		 public String getGephiFenXiFilesStoredPath ()
		 {
//			 return this.bkparsestoredpath;
			 return this.getSystemInstalledPath() + "/weeklyreports/Gephi�������/";
		 }
		/*
		 * 
		 */
		public String getGeGuChanYeLianXmlFile() 
		{
			return this.getSystemInstalledPath() + "/config/���ɲ�ҵ������.xml";
		}
		/*
		 * ����ͨ�������Դ���
		 */
		public String getZhongDianGuanZhuBanKuaiSheZhiTongDaXinFile() 
		{
			// TODO Auto-generated method stub
			return systeminstalledpath + "TDX�ص��ע�������ͨ���Ŵ���.txt";
		}
		public String getTwelveZhongDianGuanZhuBanKuaiSheZhiXmlFile() 
		{
			return  systeminstalledpath + "/config/�ص��ע�������.xml";
		}
		//�Ӱ��/����ҵ��
		public String getBanKuaiChanYeLianXml() 
		{
			return systeminstalledpath + "/config/����ҵ��.xml";
		}
//		public String getBkfxWeeklyFileResultXml ()
//		{
//			return this.getSystemInstalledPath() + "/config/����ļ��������.xml";
//		}
	 public String getSysSettingFile () 
	 {
		 return this.getSystemInstalledPath() + "/config/ϵͳ����.xml";
	 }
		
	 public String getQuanShangJiaoYiSheZhi ()
	 {
		 return this.getSystemInstalledPath() + "/config/ȯ�̽�������.xml";
	 }
	 public List<String> getTDXVOLFilesPath ()
	 {
		 String  settingfilename = this.tdxinstalledpath + "T0002/user.ini";
		 File settingfile = new File(settingfilename);
		 if(!settingfile.exists()) { //������,�˳�
			 logger.debug(settingfilename + "������");
			return null;
		 }		
		
		 List<String>   readLines = null;
		try {
			TDXExportLineProcessor tdxef = new TDXExportLineProcessor ();
			readLines = Files.readLines(settingfile, this.charSet (),tdxef);
			logger.debug(readLines);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return readLines;
	 }
	 
	 
		/*
		 * ͨ����ϵͳ��װ·��
		 * ����ļ���
			incon.dat                                       ֤�����ҵ��ͨ��������ҵ��������ҵ��������Ϣ
			T0002/hq_cache/block.dat         һ����
			T0002hq_cacheblock_gn.dat      ������
			T0002hq_cacheblock_fg.dat       �����
			T0002hq_cacheblock_zs.dat       ָ�����:
			T0002hq_cachetdxhy.cfg             ÿ����Ʊ��Ӧͨ������ҵ��������ҵ
			T0002hq_cachetdxzs.cfg             ���ָ�������ְ������һ���ֶ�ӳ�䵽incon.dat��TDXNHY��SWHY
			T0002blocknewblocknew.cfg        �Զ������Ҫ�����ļ�
		 */
		public String getTDXInstalledLocation ()
		{
			return this.tdxinstalledpath;
		}
		/*
		 * ͨ����ϵͳ����б��ļ�
		 * ����ļ���
			incon.dat                                       ֤�����ҵ��ͨ��������ҵ��������ҵ��������Ϣ
			T0002/hq_cache/block.dat         һ����
			T0002hq_cacheblock_gn.dat      ������
			T0002hq_cacheblock_fg.dat       �����
			T0002hq_cacheblock_zs.dat       ָ�����:
			T0002hq_cachetdxhy.cfg             ÿ����Ʊ��Ӧͨ������ҵ��������ҵ
			T0002hq_cachetdxzs.cfg             ���ָ�������ְ������һ���ֶ�ӳ�䵽incon.dat��TDXNHY��SWHY
			T0002blocknewblocknew.cfg        �Զ������Ҫ�����ļ�
		 */
		public String getTDXSysZDYBanKuaiFile ()
		{
			return this.getTDXInstalledLocation() + "T0002/blocknew/blocknew.cfg";
		}
		public String getTDXSysAllBanKuaiFile() 
		{
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "tdxzs.cfg"; //tdxzs�������а��
		}
		/*
		 * ͨ���ŵĸ�����ɶ�Ӧ�ļ�
		 * ����ļ���
			incon.dat                                       ֤�����ҵ��ͨ��������ҵ��������ҵ��������Ϣ
			T0002/hq_cache/block.dat         һ����
			T0002/hq_cache/block_gn.dat      ������
			T0002/hq_cache/block_fg.dat       �����
			T0002/hq_cache/block_zs.dat       ָ�����:
			T0002/hq_cache/tdxhy.cfg             ÿ����Ʊ��Ӧͨ������ҵ��������ҵ
			T0002/hq_cache/tdxzs.cfg             ���ָ�������ְ������һ���ֶ�ӳ�䵽incon.dat��TDXNHY��SWHY
			T0002/blocknew/blocknew.cfg        �Զ������Ҫ�����ļ�
		 */
		public String getTDXGaiNianBanKuaiToGeGuFile() {
			
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "block_gn.dat";
			
		}
		public String getTDXZhiShuBanKuaiToGeGuFile() {
			 
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "block_zs.dat";
			
		}
		/*
		 * ͨ���ŵķ����ɶ�Ӧ�ļ�
		 * ����ļ���
			incon.dat                                       ֤�����ҵ��ͨ��������ҵ��������ҵ��������Ϣ
			T0002/hq_cache/block.dat         һ����
			T0002/hq_cache/block_gn.dat      ������
			T0002/hq_cache/block_fg.dat       �����
			T0002/hq_cache/block_zs.dat       ָ�����:
			T0002/hq_cache/tdxhy.cfg             ÿ����Ʊ��Ӧͨ������ҵ��������ҵ
			T0002/hq_cache/tdxzs.cfg             ���ָ�������ְ������һ���ֶ�ӳ�䵽incon.dat��TDXNHY��SWHY
			T0002/blocknew/blocknew.cfg        �Զ������Ҫ�����ļ�
		 */
		public String getTDXFengGeBanKuaiToGeGuFile ()
		{
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "block_fg.dat";
		}
		/*
		 * ͨ���ŵ���ҵ���ɶ�Ӧ�ļ�
		 * ����ļ���
			incon.dat                        ֤�����ҵ��ͨ��������ҵ��������ҵ��������Ϣ
			T0002/hq_cache/block.dat         һ����
			T0002/hq_cache/block_gn.dat      ������
			T0002/hq_cache/block_fg.dat       �����
			T0002/hq_cache/block_zs.dat       ָ�����:
			T0002/hq_cache/tdxhy.cfg             ÿ����Ʊ��Ӧͨ������ҵ��������ҵ
			T0002/hq_cache/tdxzs.cfg             ���ָ�������ְ������һ���ֶ�ӳ�䵽incon.dat��TDXNHY��SWHY
			T0002/blocknew/blocknew.cfg        �Զ������Ҫ�����ļ�
		 */
		public String getTDXHangYeBanKuaiToGeGuFile ()
		{
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "tdxhy.cfg"; 
			//return "d:/tdxhy.cfg";
		}
		/*
		 * ͨ���ŵ���ҵ����ҵ�����Ӧ�ļ�
		 * ����ļ���
			incon.dat                        ֤�����ҵ��ͨ��������ҵ��������ҵ��������Ϣ
			T0002/hq_cache/block.dat         һ����
			T0002/hq_cache/block_gn.dat      ������
			T0002/hq_cache/block_fg.dat       �����
			T0002/hq_cache/block_zs.dat       ָ�����:
			T0002/hq_cache/tdxhy.cfg             ÿ����Ʊ��Ӧͨ������ҵ��������ҵ
			T0002/hq_cache/tdxzs.cfg             ���ָ�������ְ������һ���ֶ�ӳ�䵽incon.dat��TDXNHY��SWHY
			T0002/blocknew/blocknew.cfg        �Զ������Ҫ�����ļ�
			
			��ҵ�����������֤�����ҵ��������ҵ��ͨ��������ҵ
			��ҵ���ļ���incon.dat���ж��塣�ļ���ʽ��
			1) �ļ����������ҵ���ࣺ
				a) ֤�����ҵ����ͷ#ZJHHY������######
				b) ������ҵ����ͷ#SWHY������######
				c) ͨ��������ҵ����ͷ#TDXNHY������######
			2) ÿ�������У�ÿһ�а���һ��ϸ����ҵ�Ĵ�������ƣ��ԡ�|���ָ�
				a) ֤�����ҵ��һ������ΪA~M����������A99����������ΪA9999
				b) ������ҵ��һ������Ϊ990000����������Ϊ999900����������Ϊ999999
				c) ͨ��������ҵ��һ������ΪT99����������ΪT9999����������ΪT999999
		 */
		public String getTDXHangYeToDaiMaFile () 
		{
			return this.getTDXInstalledLocation() + "incon.dat"; 
		}

		
		/*
		 * ָ����Ӧ��ϵ�ļ�shm.tnf
		 */
		public String getTDXShangHaiZhiShuNameFile ()
		{
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "shm.tnf";
		}
		/*
		 * ָ����Ӧ��ϵ�ļ�shm.tnf
		 */
		public String getTDXShenZhenShuNameFile ()
		{
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "szm.tnf";
		}
		/*
		 * ͨ�����������ļ�
		 */
		public String getTDXStockEverUsedNameFile ()
		{
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "profile.dat";
		}
		/*
		 * ͨ���Ż�����Ϣ
		 * 
		 */
		public String getTDXDrawLineNameFile ()
		{
//			return this.getTDXInstalledLocation() + "T0002/tdxline.dat" ;
			return  "e:/tdxline.dat" ;
		}
		
		public String getBuyingChecklistsXMLFile (String type)
		{
			switch (type) {
				case "caiwu": return  this.getTDXInstalledLocation() + "/checklists/����checklist����.xml";
				case "zhenche": return  this.getTDXInstalledLocation() + "/checklists/����checklist����.xml";
				case "ticai": return  this.getTDXInstalledLocation() + "/checklists/����checklist���.xml";
				case "gudong": return  this.getTDXInstalledLocation() +"/checklists/����checklist�ɶ�.xml";
				case "jishu": return  this.getTDXInstalledLocation() + "/checklists/����checklist����.xml";
			}
			return null;
			
		}
//		public String formatDate(Date tmpdate)
//		{
//			try {
//				SimpleDateFormat formatterhwy=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				return formatterhwy.format(tmpdate);
//			} catch (java.lang.NullPointerException e) {
//				return null;
//			}
//			
//		}
//		public Date formateStringToDate(String tmpdate) 
//		{
//			DateFormat format = null;
//			if(tmpdate.length()>8)
//				 format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			else if(tmpdate.length() == 10)
//				format = new SimpleDateFormat("yyyy-MM-dd");
//			else if(tmpdate.length() == 8)
//				format = new SimpleDateFormat("yyyyMMdd");
//			
//			Date date = null;
//			try {
//				logger.debug("Data need to be parsed is" + tmpdate);
//				date = format.parse(tmpdate);
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				return null;
//			}
//			
//			return date;
//		}
		
		public int banKuaiFengXiMonthRange () 
		{
//			givenperiodofmonth = 8;
			return this.zhanbifengxizhouqi;
		}
		public void setSoftWareMode(int model) 
		{
			this.setSoftWareMode = model;
		}
		public int getSoftWareMode() 
		{
			return this.setSoftWareMode;
		}
		public String getGephiFileExportPath() {
			return this.systeminstalledpath;
		}
		
		public boolean getPrivateModeSetting ()
		{
			return this.priavtemode;
		}
		/**
		 * @return the bkdpzhanbimarker
		 */
		public double getBkdpzhanbimarker() {
			return bkdpzhanbimarker;
		}
		/**
		 * @return the ggdpzhanbimarker
		 */
		public double getGgdpzhanbimarker() {
			return ggdpzhanbimarker;
		}
		/*
		 * 
		 */
		public String getPythonInterpreter ()
		{
			return pythoninterpreter;
		}
		public String getPythonScriptsPath() 
		{
			if(this.systeminstalledpath.contains(" ")) {
				return "\"" + this.systeminstalledpath + "dailydata/python/execscripts/" ;
			} else 
				return this.systeminstalledpath + "dailydata/python/execscripts/";
		}
		public String getPythonScriptsExecExportsPath ()
		{
			return this.systeminstalledpath + "dailydata/python/execexports/";
		}
		public String getNetEaseDownloadedFilePath() 
		{
			return this.systeminstalledpath + "dailydata/netease/downloaded/";
		}
		public String getXueQiuDownloadedFilePath() 
		{
			return this.systeminstalledpath + "dailydata/xueqiu/downloaded/";
		}
		public String getEastMoneyDownloadedFilePath() 
		{
			return this.systeminstalledpath + "dailydata/eastmoney/downloaded/";
		}
		public String getDaPanFengXiWeeklyXmlMatrixFile() 
		{
			return this.systeminstalledpath + "checklists/dapanfengximatrix.xml";
		}
		public String getGeGuFengXiWeeklyXmlMatrixFile() 
		{
			return this.systeminstalledpath + "checklists/gegufengximatrix.xml";
		}
		public String getSystemAudioPlayed ()
		{
			return this.systeminstalledpath + "audio/SystemAlarm1.wav";
		}

}


class TDXExportLineProcessor implements LineProcessor<List<String>> 
{
   private int rownum =1;
   private boolean found = false;
   private int rowneedtoreadnum = 6;
   private List<String> stocklists = Lists.newArrayList();
   
   public boolean processLine(String line) throws IOException {
    	if(line.equals("[Export]")) //�ҵ�E��һ��
    		found = true;
    	else if(found == false) //��û�ҵ��������ң��к�����
    		 rownum++ ;
    	else if(found == true && rowneedtoreadnum !=0) { //�ҵ�����ʼ��������rowneedtoreadnum��
    		stocklists.add(line);
    		rowneedtoreadnum --;
    	} else if(found == true && rowneedtoreadnum ==0) {
    		
    	}
    		
    		
    	return true;
    }
    @Override
    public List<String> getResult() {
        return stocklists;
    }
}

