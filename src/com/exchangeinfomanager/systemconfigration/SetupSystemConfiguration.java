package com.exchangeinfomanager.systemconfigration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

public class SetupSystemConfiguration 
{
	private String systeminstalledpath;
	private String tdxinstalledpath;
	
	private int tryingcount = 0; //ϵͳ�������������3�Σ�3�β���ֱ���˳�

	private String tdxzdybkpath;
	private int zhanbifengxizhouqi; //�ɽ���ռ�ȷ�������
	private Boolean priavtemode;
	private String LinceseMac;
	private String pythoninterpreter;
	private String nameofguanzhubankuai;
	private List<String> shanghaistockcodeprefixlist;
	private List<String> shengzhengstockcodeprefixlist;
	private List<String> shanghaizhishucodeprefixlist;
	private List<String> shengzhengzhishucodeprefixlist;

	private static Logger logger = Logger.getLogger(SetupSystemConfiguration.class);

	public SetupSystemConfiguration() 
	{
		File directory = new File("");//�趨Ϊ��ǰ�ļ���
		try{
//		    logger.debug(directory.getCanonicalPath());//��ȡ��׼��·��
//		    logger.debug(directory.getAbsolutePath());//��ȡ����·��
//		    this.systeminstalledpath = toUNIXpath(directory.getCanonicalPath()+ "\\");
		    Properties properties = System.getProperties();
		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //�û����г���ĵ�ǰĿ¼
		    logger.debug(this.systeminstalledpath );
		} catch(Exception e) {
			System.exit(0);
		}
		
//		FileInputStream inputStream = null;
		String propxmlFileName = null ;
		try {
//			Properties properties = System.getProperties();
//		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //�û����г���ĵ�ǰĿ¼
//		    
//			Properties prop = new Properties();
//			String propFileName =  this.systeminstalledpath  + "/config/config.properties";
//			inputStream = new FileInputStream(propFileName);
//			if (inputStream != null) {
//				prop.load(inputStream);
//			} else {
//				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
//			}
			
			Properties propxml = new Properties();
			 propxmlFileName =  this.systeminstalledpath  + "/config/SystemConfigration.xml";
			FileInputStream inputStreamxml = new FileInputStream(propxmlFileName);
			if (inputStreamxml != null) {
				propxml.loadFromXML(inputStreamxml);
			} 
			inputStreamxml.close();
			
			tdxzdybkpath = propxml.getProperty ("tdxzdybkpath");
			zhanbifengxizhouqi = Integer.parseInt( propxml.getProperty ("zhanbifengxizhouqi") ); //�ɽ���ռ�ȷ�������
			priavtemode = Boolean.valueOf(propxml.getProperty ("privatemode")) ;
			LinceseMac = propxml.getProperty ("License");
			pythoninterpreter = propxml.getProperty ("pythoninterper");
			nameofguanzhubankuai = propxml.getProperty ("nameofguanzhubankuai");
			tdxinstalledpath = propxml.getProperty ("tdxpah");
			
			String shanghaistockcodeprefix = propxml.getProperty ("shanghaistockcodeprefix");
			shanghaistockcodeprefixlist = Splitter.on("/").omitEmptyStrings().splitToList(shanghaistockcodeprefix);
			
			String shengzhengstockcodeprefix = propxml.getProperty ("shengzhengstockcodeprefix");
			shengzhengstockcodeprefixlist = Splitter.on("/").omitEmptyStrings().splitToList(shengzhengstockcodeprefix);
			
			String shanghaizhishucodeprefix = propxml.getProperty ("shanghaizhishucodeprefix");
			shanghaizhishucodeprefixlist = Splitter.on("/").omitEmptyStrings().splitToList(shanghaizhishucodeprefix);
			
			String shengzhengzhishucodeprefix = propxml.getProperty ("shengzhengzhishucodeprefix");
			shengzhengzhishucodeprefixlist = Splitter.on("/").omitEmptyStrings().splitToList(shengzhengzhishucodeprefix);
			
		} catch (Exception e) {
			logger.info("property file '" + propxmlFileName + "' not found in the classpath") ;
		} finally {
			
		}
		
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
	public String getLinceseMac ()
	 {
		 return this.LinceseMac;
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
	 public String getSavedImageStoredPath ()
	 {
		 String path = this.getSystemInstalledPath() + "/dailydata/savedimage/";
		 if (java.nio.file.Files.notExists(Paths.get(path))) {
			 try {
				java.nio.file.Files.createDirectories(Paths.get(path));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 return path; 
	 } 
	 
	 /*
	  * 
	  */
	 public String getTdxDataImportCheckResult ()
	 {
		 String path = this.getSystemInstalledPath() + "/dailydata/dataimportcheckresult/";
		 
		 if (java.nio.file.Files.notExists(Paths.get(path))) {
			 try {
				java.nio.file.Files.createDirectories(Paths.get(path));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 return path;
	 }
	 /*
	  * 
	  */
	 public String getTdxFenXiReportFile ()
	 {
		 String path = this.getSystemInstalledPath() + "/reports/StockMiningToTdxReports/TDX���ɷ�������.txt";
		 
		 if (java.nio.file.Files.notExists(Paths.get(path).getParent())) {
			 try {
				java.nio.file.Files.createDirectories(Paths.get(path).getParent() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 return path; 
	 }
	 /*
	  * 
	  */
	 public String getTdxZdgzReportFile ()
	 {
		 String path = this.getSystemInstalledPath() + "/reports/StockMiningToTdxReports/TDX�ص��ע��ʷ����.txt";
		 
		 if (java.nio.file.Files.notExists(Paths.get(path).getParent())) {
			 try {
				java.nio.file.Files.createDirectories(Paths.get(path).getParent() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 return path; 
	 }
		/**
		 * @return the tdxbbfilenamegainiantishi
		 */
		public String getTdxBbFileGaiNianTiShi() 
		{
			 String path = this.getSystemInstalledPath() + "/reports/StockMiningToTdxReports/TDX������ʾ����.txt";
			 
			 if (java.nio.file.Files.notExists(Paths.get(path).getParent())) {
				 try {
					java.nio.file.Files.createDirectories(Paths.get(path).getParent() );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 return path;  
		}
		/**
		 * @return the tdxbbfilenamefumianxiaoxi
		 */
		public String getTdxBbfileFuMianXiaoXi() 
		{
			String path =  this.getSystemInstalledPath()  + "/reports/StockMiningToTdxReports/TDX������Ϣ����.txt";
			
			if (java.nio.file.Files.notExists(Paths.get(path).getParent())) {
				 try {
					java.nio.file.Files.createDirectories(Paths.get(path).getParent() );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 return path; 
		}
		/**
		 * @return the tdxbbfilenamezfxgkhzd
		 */
		public String getTdxBbFileZzfxgkhzd() {
			String path =  this.getSystemInstalledPath() + "/reports/StockMiningToTdxReports/TDX���ɹؼ��ʱ���.txt";
			
			if (java.nio.file.Files.notExists(Paths.get(path).getParent())) {
				 try {
					java.nio.file.Files.createDirectories(Paths.get(path).getParent() );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 return path; 
		}
		public String getTDXChanYeLianReportFile() 
		{
			String path =   this.getSystemInstalledPath() + "/reports/StockMiningToTdxReports/TDX���ɲ�ҵ������.txt";
			
			if (java.nio.file.Files.notExists(Paths.get(path).getParent())) {
				 try {
					java.nio.file.Files.createDirectories(Paths.get(path).getParent() );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 return path; 
		}
		/*
		 * 
		 */
		public String getTDXModelMatchExportFile ()
		{
			String path =    this.getSystemInstalledPath() + "/reports/ռ��ģ�����/";
			
			if (java.nio.file.Files.notExists(Paths.get(path))) {
				 try {
					java.nio.file.Files.createDirectories(Paths.get(path) );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 return path; 
		}
		/*
		 * 
		 */
		 public String getShiZhiFenXiFilesStoredPath ()
		 {
			 String path =  this.getSystemInstalledPath() + "/reports/��ֵ�������/";
			 
			 if (java.nio.file.Files.notExists(Paths.get(path))) {
				 try {
					java.nio.file.Files.createDirectories(Paths.get(path) );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 return path; 
		 }
		 /*
		  * 
		  */
		 public String getGephiFenXiFilesStoredPath ()
		 {
			 String path =  this.getSystemInstalledPath() + "/reports/Gephi�������/";
			 
			 if (java.nio.file.Files.notExists(Paths.get(path))) {
				 try {
					java.nio.file.Files.createDirectories(Paths.get(path) );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 return path; 
		 }
		/*
		 * 
		 */
		public String getGeGuChanYeLianXmlFile() 
		{
			 String path =  this.getSystemInstalledPath() + "/config/���ɲ�ҵ������.xml";
			 
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
		/*
		 * ����ͨ�������Դ���
		 */
		public String getZhongDianGuanZhuBanKuaiSheZhiTongDaXinFile() 
		{
			String path =   systeminstalledpath + "TDX�ص��ע�������ͨ���Ŵ���.txt";
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
		public String getTwelveZhongDianGuanZhuBanKuaiSheZhiXmlFile() 
		{
			String path =  systeminstalledpath + "/config/�ص��ע�������.xml";
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
		//�Ӱ��/����ҵ��
		public String getBanKuaiChanYeLianXml() 
		{
			String path =  systeminstalledpath + "/config/����ҵ��.xml";
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
//		public String getBkfxWeeklyFileResultXml ()
//		{
//			return this.getSystemInstalledPath() + "/config/����ļ��������.xml";
//		}
	 public String getSysSettingFile () 
	 {
		 String path =  this.getSystemInstalledPath() + "/config/ϵͳ����.xml";
		 
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
		
	 public String getQuanShangJiaoYiSheZhi ()
	 {
		 String path =  this.getSystemInstalledPath() + "/config/ȯ�̽�������.xml";
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
//			return 
//			return "\\\\jeffauas\\blocknew\\blocknew.cfg";
			if(this.tdxzdybkpath != null)
				return this.tdxzdybkpath + "blocknew.cfg";
			else
				return this.getTDXInstalledLocation() + "T0002/blocknew/blocknew.cfg"; 
			
//			"\\\\Comp-1\\FileIO\\Stop.txt"
		}
		/*
		 * ��ע���Զ����������
		 */
		public String getNameOfGuanZhuZdyBanKuai ()
		{
			return this.nameofguanzhubankuai;
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
		public String getBanKuaiFengXiChecklistXmlFile ()
		{
			return this.getSystemInstalledPath() + "/checklists/bankuaifengxiremindmatrix.xml";
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
		
		public String getGephiFileExportPath() {
			return this.systeminstalledpath;
		}
		public String getNodeExportCsvFilePath () 
		{
			String path = this.systeminstalledpath + "dailydata/csv/" ;
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
		public boolean getPrivateModeSetting ()
		{
			return this.priavtemode;
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
				return "\"" + this.systeminstalledpath + "thirdparty/python/execscripts/" ;
			} else 
				return this.systeminstalledpath + "thirdparty/python/execscripts/";
		}
		public String getPythonScriptsExecExportsPath ()
		{
			String path = this.systeminstalledpath + "dailydata/python/execexports/";
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
		public String getPowerShellScriptsPath ()
		{
			String path = this.systeminstalledpath + "thirdparty/powershell/execscripts/";
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
		public String getNetEaseDownloadedFilePath() 
		{
			String path = this.systeminstalledpath + "dailydata/netease/downloaded/";
			 if (java.nio.file.Files.notExists(Paths.get(path))) {
				 try {
					java.nio.file.Files.createDirectories(Paths.get(path) );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 return path; 
		}
		public String getYanJiuBaoGaoDownloadedFilePath ()
		{
			String path = this.systeminstalledpath + "reports/�о�����/";
			 if (java.nio.file.Files.notExists(Paths.get(path))) {
				 try {
					java.nio.file.Files.createDirectories(Paths.get(path) );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 return path; 
		}
		public String getXueQiuDownloadedFilePath() 
		{
			String path = this.systeminstalledpath + "dailydata/xueqiu/downloaded/";
			if (java.nio.file.Files.notExists(Paths.get(path))) {
				 try {
					java.nio.file.Files.createDirectories(Paths.get(path) );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 return path; 
		}
		public String getEastMoneyDownloadedFilePath() 
		{
			String path = this.systeminstalledpath + "dailydata/eastmoney/downloaded/";
			if (java.nio.file.Files.notExists(Paths.get(path))) {
				 try {
					java.nio.file.Files.createDirectories(Paths.get(path) );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 return path; 
		}
		public String getDaPanFengXiWeeklyXmlMatrixFile() 
		{
			String path = this.systeminstalledpath + "checklists/dapanfengximatrix.xml";
			if (java.nio.file.Files.notExists(Paths.get(path))) {
				 try {
					java.nio.file.Files.createDirectories(Paths.get(path) );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 return path; 
		}
		/*
		 * 
		 */
		public String getBanKuaiFengXiReminderMartirxFile ()
		{
			String path = this.systeminstalledpath + "checklists/bankuaifengxiremindermatrix.xml";
			if (java.nio.file.Files.notExists(Paths.get(path))) {
				 try {
					java.nio.file.Files.createDirectories(Paths.get(path) );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 return path;
		}
		/*
		 * 
		 */
		public String getGeGuFengXiWeeklyXmlMatrixFile() 
		{
			String path = this.systeminstalledpath + "checklists/gegufengximatrix.xml";
			if (java.nio.file.Files.notExists(Paths.get(path))) {
				 try {
					java.nio.file.Files.createDirectories(Paths.get(path) );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 return path; 
		}
		public String getSystemAudioPlayed ()
		{
			return this.systeminstalledpath + "audio/SystemAlarm1.wav";
		}
		/*
		 * ��ǰ��ע���ɴ�ŵİ����
		 */
		public String getCurZdyBanKuaiOfGuanZhuGeGu() 
		{
			// TODO Auto-generated method stub
			return "ģ����֤";
		}
		/*
		 * 
		 */
		public String getCsvPathOfExportedTDXVOLFiles()
		{
			String path = this.systeminstalledpath + "dailydata/tdxexportedtxtfilestocsv/" ;
			if (java.nio.file.Files.notExists(Paths.get(path))) {
				 try {
					 Path parpath = Paths.get(path) ;
					java.nio.file.Files.createDirectories(Paths.get(path) );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			return path;
			
		}
		/*
		 * 
		 */
		public String getFreeMindInstallationPath ()
		{
			String path = "C:\\Program Files\\FreeMind\\Freemind.bat"; //D:\\tools\\HeidiSQL\\heidisql.exe
			return path;
		}
		/*
		 * private List<String> shanghaistockcodeprefixlist;
	private List<String> shengzhengstockcodeprefixlist;
	private List<String> shanghaizhishucodeprefixlist;
	private List<String> shengzhengzhishucodeprefixlist;
		 */
		public Boolean isShangHaiStock (String nodecode)
		{
			String nodecodeprefix = nodecode.substring(0, 3);
			if( shanghaistockcodeprefixlist.contains(nodecodeprefix) )
				return true;
			else 
				return false;
//			if(nodecode.startsWith("600") || nodecode.startsWith("688") ||  nodecode.startsWith("603") || nodecode.startsWith("601") )
//				return true;
//			else
//				return false;
		}
		public Boolean isShangHaiZhiShu (String nodecode)
		{
			String nodecodeprefix = nodecode.substring(0, 3);
			if( shanghaizhishucodeprefixlist.contains(nodecodeprefix) )
				return true;
			else 
				return false;
			
//			if(nodecode.startsWith("000") || nodecode.startsWith("880") || nodecode.startsWith("999")  )
//				return true;
//			else
//				return false;
		}
		public Boolean isShenZhengStock (String nodecode)
		{
			String nodecodeprefix = nodecode.substring(0, 3);
			if( shengzhengstockcodeprefixlist.contains(nodecodeprefix) )
				return true;
			else 
				return false;
			
//			if(nodecode.startsWith("000") || nodecode.startsWith("300") || nodecode.startsWith("001") || nodecode.startsWith("002") || nodecode.startsWith("003"))
//				return true;
//			else
//				return false;
		}
		public Boolean isShenZhengZhiShu (String nodecode)
		{
			String nodecodeprefix = nodecode.substring(0, 3);
			if( shengzhengzhishucodeprefixlist.contains(nodecodeprefix) )
				return true;
			else 
				return false;
			
//			if(nodecode.startsWith("399") || nodecode.startsWith("159") )
//				return true;
//			else
//				return false;
		}
		/*
		 * ����ĵļ���ָ��
		 */
		public List<String> getCoreZhiShuCodeList ()
		{
			List<String> zhishu = new ArrayList<> ();
			zhishu.add("999999");
			zhishu.add("399001");
			zhishu.add("399006");
			zhishu.add("000016");
			
			return zhishu ;
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



