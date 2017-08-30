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
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;
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
	
	private String systeminstalledpath;
	private String tdxinstalledpath;
	private String curdatabasetype;
	private CurDataBase curdbs; //�������ݿ�������Ϣ
	private CurDataBase rmtcurdb; //Զ�����ݿ�������Ϣ
	private int tryingcount = 0; //ϵͳ�������������3�Σ�3�β���ֱ���˳�
	private String bkparsestoredpath;
	private int setSoftWareMode; //�趨ϵͳģʽ����2�֣��������ݺ�ͨ����ͬ�����ݡ�
	private String datatablesfromserver;
	public static int MODELSERVER=0, MODELCLIENT=1, MODELSERVERCLIENT=2;
	
	private void getSystemInfoFromXML() 
	{
		File directory = new File("");//�趨Ϊ��ǰ�ļ���
		try{
//		    System.out.println(directory.getCanonicalPath());//��ȡ��׼��·��
//		    System.out.println(directory.getAbsolutePath());//��ȡ����·��
//		    this.systeminstalledpath = toUNIXpath(directory.getCanonicalPath()+ "\\");
		    Properties properties = System.getProperties();
		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //�û����г���ĵ�ǰĿ¼
		    System.out.println(this.systeminstalledpath );
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
			
//			Element eletdxvol = xmlroot.element("tdxvolpah");
//			this.tdxvolpath = eletdxvol.getText(); 
			
			//�������ݿ���Ϣ
			Element elesorce = xmlroot.element("databasesources");
			Iterator it = elesorce.elementIterator();
			while (it.hasNext()) 
			{
				Element tmpelement = (Element) it.next();
				String cursel = tmpelement.attributeValue("curselecteddbs");
				if(tmpelement.attributeValue("curselecteddbs").equals("yes")) {
					curdbs = new CurDataBase (tmpelement.attributeValue("dbsname") );
//					System.out.println( tmpelement.getText() );
//					System.out.println(tmpelement.attributeValue("user") );
//					System.out.println( tmpelement.attributeValue("password") );
//					System.out.println( tmpelement.attributeValue("databasetype").trim()  ) ;
					
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
					System.out.println( tmpelement.getText() );
					System.out.println(tmpelement.attributeValue("user") );
					System.out.println( tmpelement.attributeValue("password") );
					System.out.println( tmpelement.attributeValue("databasetype").trim()  ) ;
					
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
			
			//��Щ���ݴ�server��ȡ
			Element elermtdatatablefromserver = xmlroot.element("tablesfromserver");
			this.datatablesfromserver = elermtdatatablefromserver.getText();
			
			try {
				xmlfileinput.close();
				tryingcount = 0;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (DocumentException e) {
			System.out.println(this.getSysSettingFile() + "���ڴ������޸�");
			//e.printStackTrace();
			
			tryingcount ++;
			if( tryingcount == 3) {
				JOptionPane.showMessageDialog(null,"�����ļ���������ϵͳ�Զ��˳����������������ã��ټ���", "����",JOptionPane.INFORMATION_MESSAGE);  
				System.exit(0);
			}
			else {
				System.out.println(this.getSysSettingFile());
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
	 /*
	  * ��Щ���ݴ�server���ȡ
	  */
	 public String getTablesDataSelectedFromServer ()
	 {
		 return this.datatablesfromserver;
	 }
	 /*
	  * 
	  */
	 public String getSystemInstalledPath ()
	 {
		 return this.systeminstalledpath;
	 }
	 
	 public String getBanKuaiParsedFileStoredPath ()
	 {
		 return this.bkparsestoredpath;
	 }
		/**
		 * @return the tdxbbfilenamegainiantishi
		 */
		public String getTdxBbFileGaiNianTiShi() {
			return this.getSystemInstalledPath() + "TDX������ʾ.txt";
		}
		/**
		 * @return the tdxbbfilenamefumianxiaoxi
		 */
		public String getTdxBbfileFuMianXiaoXi() {
			return this.getSystemInstalledPath()  + "TDX������Ϣ.txt";
		}
		/**
		 * @return the tdxbbfilenamezfxgkhzd
		 */
		public String getTdxBbFileZzfxgkhzd() {
			return this.getSystemInstalledPath() + "TDX������ؿͻ�����.txt";
		}
		public String getTDXChanYeLianReportFile() 
		{
			return this.getSystemInstalledPath() + "TDX���ɲ�ҵ������.txt";
		}
		
	 public String getSysSettingFile () 
	 {
		 return this.getSystemInstalledPath() + "ϵͳ����.xml";
	 }
		
	 public String getQuanShangJiaoYiSheZhi ()
	 {
		 return this.getSystemInstalledPath() + "ȯ�̽�������.xml";
	 }
	 public List<String> getTDXVOLFilesPath ()
	 {
		 String  settingfilename = this.tdxinstalledpath + "T0002/user.ini";
		 File settingfile = new File(settingfilename);
		 if(!settingfile.exists()) { //������,�˳�
			 System.out.println(settingfilename + "������");
			return null;
		 }		
		
		 List<String>   readLines = null;
		try {
			TDXExportLineProcessor tdxef = new TDXExportLineProcessor ();
			readLines = Files.readLines(settingfile, this.charSet (),tdxef);
			System.out.println(readLines);
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
		 * 
		 */
		public String getGeGuChanYeLianXmlFile() 
		{
			return systeminstalledpath + "���ɲ�ҵ������.xml";
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
		 * ����ͨ�������Դ���
		 */
		public String getZhongDianGuanZhuBanKuaiSheZhiTongDaXinFile() 
		{
			// TODO Auto-generated method stub
			return systeminstalledpath + "TDX�ص��ע�������ͨ���Ŵ���.txt";
		}
		public String getTwelveZhongDianGuanZhuBanKuaiSheZhiXmlFile() 
		{
			return  systeminstalledpath + "�ص��ע�������.xml";
		}
		//�Ӱ��/����ҵ��
		public String getBanKuaiChanYeLianXml() 
		{
			return systeminstalledpath + "����ҵ��.xml";
		}
		public String getBuyingChecklistsXMLFile (String type)
		{
			switch (type) {
				case "caiwu": return  systeminstalledpath + "����checklist����.xml";
				case "zhenche": return  systeminstalledpath + "����checklist����.xml";
				case "ticai": return  systeminstalledpath + "����checklist���.xml";
				case "gudong": return  systeminstalledpath +"����checklist�ɶ�.xml";
				case "jishu": return  systeminstalledpath + "����checklist����.xml";
			}
			return null;
			
		}
		public String formatDate(Date tmpdate)
		{
			try {
				SimpleDateFormat formatterhwy=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return formatterhwy.format(tmpdate);
			} catch (java.lang.NullPointerException e) {
				return null;
			}
			
		}
		public Date formateStringToDate(String tmpdate) 
		{
			DateFormat format = null;
			if(tmpdate.length()>8)
				 format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			else if(tmpdate.length() == 10)
				format = new SimpleDateFormat("yyyy-MM-dd");
			else if(tmpdate.length() == 8)
				format = new SimpleDateFormat("yyyyMMdd");
			
			Date date = null;
			try {
				System.out.println("Data need to be parsed is" + tmpdate);
				date = format.parse(tmpdate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
			return date;
		}
		public void setSoftWareMode(int model) 
		{
			this.setSoftWareMode = model;
		}
		public int getSoftWareMode() 
		{
			return this.setSoftWareMode;
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

