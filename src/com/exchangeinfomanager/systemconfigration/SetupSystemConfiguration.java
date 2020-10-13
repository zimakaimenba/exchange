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
	
	private int tryingcount = 0; //系统如果出错，会重试3次，3次不成直接退出

	private String tdxzdybkpath;
	private int zhanbifengxizhouqi; //成交量占比分析周期
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
		File directory = new File("");//设定为当前文件夹
		try{
//		    logger.debug(directory.getCanonicalPath());//获取标准的路径
//		    logger.debug(directory.getAbsolutePath());//获取绝对路径
//		    this.systeminstalledpath = toUNIXpath(directory.getCanonicalPath()+ "\\");
		    Properties properties = System.getProperties();
		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //用户运行程序的当前目录
		    logger.debug(this.systeminstalledpath );
		} catch(Exception e) {
			System.exit(0);
		}
		
//		FileInputStream inputStream = null;
		String propxmlFileName = null ;
		try {
//			Properties properties = System.getProperties();
//		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //用户运行程序的当前目录
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
			zhanbifengxizhouqi = Integer.parseInt( propxml.getProperty ("zhanbifengxizhouqi") ); //成交量占比分析周期
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
//					"jdbc:odbc:Driver={Microsoft FoxPro VFP Driver (*.dbf)};" + 		//写法相对固定
//					"UID=;"+
//					"Deleted=Yes;"+
//					"Null=Yes;"+
//					"Collate=Machine;"+
//					"BackgroundFetch=Yes;"+
//					"Exclusive=No;"+
//					"SourceType=DBF;"+     							//此处指定解析文件的后缀
//					"SourceDB=" + this.tdxinstalledpath + "T0002/hq_cache/base.dbf"; 	//此处为dbf文件所在的目录
//		 foxprodbs.setDataBaseConStr (dbconstr);
		 
		 return  this.tdxinstalledpath + "T0002/hq_cache/base.dbf";
	 }
	 
//	 /*
//	  * 哪些数据从server表读取
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
		 String path = this.getSystemInstalledPath() + "/reports/StockMiningToTdxReports/TDX个股分析报告.txt";
		 
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
		 String path = this.getSystemInstalledPath() + "/reports/StockMiningToTdxReports/TDX重点关注历史报告.txt";
		 
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
			 String path = this.getSystemInstalledPath() + "/reports/StockMiningToTdxReports/TDX概念提示报告.txt";
			 
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
			String path =  this.getSystemInstalledPath()  + "/reports/StockMiningToTdxReports/TDX负面消息报告.txt";
			
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
			String path =  this.getSystemInstalledPath() + "/reports/StockMiningToTdxReports/TDX个股关键词报告.txt";
			
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
			String path =   this.getSystemInstalledPath() + "/reports/StockMiningToTdxReports/TDX个股产业链报告.txt";
			
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
			String path =    this.getSystemInstalledPath() + "/reports/占比模型输出/";
			
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
			 String path =  this.getSystemInstalledPath() + "/reports/市值分析输出/";
			 
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
			 String path =  this.getSystemInstalledPath() + "/reports/Gephi分析输出/";
			 
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
			 String path =  this.getSystemInstalledPath() + "/config/个股产业链设置.xml";
			 
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
		 * 生成通达信语言代码
		 */
		public String getZhongDianGuanZhuBanKuaiSheZhiTongDaXinFile() 
		{
			String path =   systeminstalledpath + "TDX重点关注板块设置通达信代码.txt";
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
			String path =  systeminstalledpath + "/config/重点关注板块设置.xml";
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
		//子版块/板块产业链
		public String getBanKuaiChanYeLianXml() 
		{
			String path =  systeminstalledpath + "/config/板块产业链.xml";
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
//			return this.getSystemInstalledPath() + "/config/板块文件分析结果.xml";
//		}
	 public String getSysSettingFile () 
	 {
		 String path =  this.getSystemInstalledPath() + "/config/系统设置.xml";
		 
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
		 String path =  this.getSystemInstalledPath() + "/config/券商交易设置.xml";
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
		 if(!settingfile.exists()) { //不存在,退出
			 logger.debug(settingfilename + "不存在");
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
		 * 通达信系统安装路径
		 * 相关文件：
			incon.dat                                       证监会行业，通达信新行业，申万行业等描述信息
			T0002/hq_cache/block.dat         一般板块
			T0002hq_cacheblock_gn.dat      概念板块
			T0002hq_cacheblock_fg.dat       风格板块
			T0002hq_cacheblock_zs.dat       指数板块:
			T0002hq_cachetdxhy.cfg             每个股票对应通达信行业和申万行业
			T0002hq_cachetdxzs.cfg             板块指数，部分板块的最后一个字段映射到incon.dat的TDXNHY和SWHY
			T0002blocknewblocknew.cfg        自定义板块概要描述文件
		 */
		public String getTDXInstalledLocation ()
		{
			return this.tdxinstalledpath;
		}
		/*
		 * 通达信系统板块列表文件
		 * 相关文件：
			incon.dat                                       证监会行业，通达信新行业，申万行业等描述信息
			T0002/hq_cache/block.dat         一般板块
			T0002hq_cacheblock_gn.dat      概念板块
			T0002hq_cacheblock_fg.dat       风格板块
			T0002hq_cacheblock_zs.dat       指数板块:
			T0002hq_cachetdxhy.cfg             每个股票对应通达信行业和申万行业
			T0002hq_cachetdxzs.cfg             板块指数，部分板块的最后一个字段映射到incon.dat的TDXNHY和SWHY
			T0002blocknewblocknew.cfg        自定义板块概要描述文件
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
		 * 关注的自定义板块的名字
		 */
		public String getNameOfGuanZhuZdyBanKuai ()
		{
			return this.nameofguanzhubankuai;
		}
		public String getTDXSysAllBanKuaiFile() 
		{
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "tdxzs.cfg"; //tdxzs包含所有板块
		}
		/*
		 * 通达信的概念个股对应文件
		 * 相关文件：
			incon.dat                                       证监会行业，通达信新行业，申万行业等描述信息
			T0002/hq_cache/block.dat         一般板块
			T0002/hq_cache/block_gn.dat      概念板块
			T0002/hq_cache/block_fg.dat       风格板块
			T0002/hq_cache/block_zs.dat       指数板块:
			T0002/hq_cache/tdxhy.cfg             每个股票对应通达信行业和申万行业
			T0002/hq_cache/tdxzs.cfg             板块指数，部分板块的最后一个字段映射到incon.dat的TDXNHY和SWHY
			T0002/blocknew/blocknew.cfg        自定义板块概要描述文件
		 */
		public String getTDXGaiNianBanKuaiToGeGuFile() {
			
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "block_gn.dat";
			
		}
		public String getTDXZhiShuBanKuaiToGeGuFile() {
			 
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "block_zs.dat";
			
		}
		/*
		 * 通达信的风格个股对应文件
		 * 相关文件：
			incon.dat                                       证监会行业，通达信新行业，申万行业等描述信息
			T0002/hq_cache/block.dat         一般板块
			T0002/hq_cache/block_gn.dat      概念板块
			T0002/hq_cache/block_fg.dat       风格板块
			T0002/hq_cache/block_zs.dat       指数板块:
			T0002/hq_cache/tdxhy.cfg             每个股票对应通达信行业和申万行业
			T0002/hq_cache/tdxzs.cfg             板块指数，部分板块的最后一个字段映射到incon.dat的TDXNHY和SWHY
			T0002/blocknew/blocknew.cfg        自定义板块概要描述文件
		 */
		public String getTDXFengGeBanKuaiToGeGuFile ()
		{
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "block_fg.dat";
		}
		/*
		 * 通达信的行业个股对应文件
		 * 相关文件：
			incon.dat                        证监会行业，通达信新行业，申万行业等描述信息
			T0002/hq_cache/block.dat         一般板块
			T0002/hq_cache/block_gn.dat      概念板块
			T0002/hq_cache/block_fg.dat       风格板块
			T0002/hq_cache/block_zs.dat       指数板块:
			T0002/hq_cache/tdxhy.cfg             每个股票对应通达信行业和申万行业
			T0002/hq_cache/tdxzs.cfg             板块指数，部分板块的最后一个字段映射到incon.dat的TDXNHY和SWHY
			T0002/blocknew/blocknew.cfg        自定义板块概要描述文件
		 */
		public String getTDXHangYeBanKuaiToGeGuFile ()
		{
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "tdxhy.cfg"; 
			//return "d:/tdxhy.cfg";
		}
		/*
		 * 通达信的行业与行业代码对应文件
		 * 相关文件：
			incon.dat                        证监会行业，通达信新行业，申万行业等描述信息
			T0002/hq_cache/block.dat         一般板块
			T0002/hq_cache/block_gn.dat      概念板块
			T0002/hq_cache/block_fg.dat       风格板块
			T0002/hq_cache/block_zs.dat       指数板块:
			T0002/hq_cache/tdxhy.cfg             每个股票对应通达信行业和申万行业
			T0002/hq_cache/tdxzs.cfg             板块指数，部分板块的最后一个字段映射到incon.dat的TDXNHY和SWHY
			T0002/blocknew/blocknew.cfg        自定义板块概要描述文件
			
			行业包括三个类别：证监会行业；申万行业；通达信新行业
			行业在文件“incon.dat”中定义。文件格式：
			1) 文件包含多个行业分类：
				a) 证监会行业：开头#ZJHHY，结束######
				b) 申万行业：开头#SWHY，结束######
				c) 通达信新行业：开头#TDXNHY，结束######
			2) 每个分类中，每一行包含一个细分行业的代码和名称，以“|”分隔
				a) 证监会行业：一级分类为A~M，二级分类A99，三级分类为A9999
				b) 申万行业：一级分类为990000，二级分类为999900，三级分类为999999
				c) 通达信新行业：一级分类为T99，二级分类为T9999，三级分类为T999999
		 */
		public String getTDXHangYeToDaiMaFile () 
		{
			return this.getTDXInstalledLocation() + "incon.dat"; 
		}

		
		/*
		 * 指数对应关系文件shm.tnf
		 */
		public String getTDXShangHaiZhiShuNameFile ()
		{
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "shm.tnf";
		}
		/*
		 * 指数对应关系文件shm.tnf
		 */
		public String getTDXShenZhenShuNameFile ()
		{
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "szm.tnf";
		}
		/*
		 * 通达信曾用名文件
		 */
		public String getTDXStockEverUsedNameFile ()
		{
			return this.getTDXInstalledLocation() + "T0002/hq_cache/" + "profile.dat";
		}
		/*
		 * 通达信划线信息
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
				case "caiwu": return  this.getTDXInstalledLocation() + "/checklists/买入checklist财务.xml";
				case "zhenche": return  this.getTDXInstalledLocation() + "/checklists/买入checklist政策.xml";
				case "ticai": return  this.getTDXInstalledLocation() + "/checklists/买入checklist题材.xml";
				case "gudong": return  this.getTDXInstalledLocation() +"/checklists/买入checklist股东.xml";
				case "jishu": return  this.getTDXInstalledLocation() + "/checklists/买入checklist技术.xml";
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
			String path = this.systeminstalledpath + "reports/研究报告/";
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
		 * 当前关注个股存放的板块名
		 */
		public String getCurZdyBanKuaiOfGuanZhuGeGu() 
		{
			// TODO Auto-generated method stub
			return "模型验证";
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
		 * 最核心的几个指数
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
    	if(line.equals("[Export]")) //找到E这一行
    		found = true;
    	else if(found == false) //还没找到，继续找，行号增加
    		 rownum++ ;
    	else if(found == true && rowneedtoreadnum !=0) { //找到，开始读，共读rowneedtoreadnum行
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



