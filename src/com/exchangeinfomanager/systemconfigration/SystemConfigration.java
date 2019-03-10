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
	private CurDataBase curdbs; //本地数据库连接信息
	private CurDataBase rmtcurdb; //远程数据库连接信息
	private int tryingcount = 0; //系统如果出错，会重试3次，3次不成直接退出
	private String bkparsestoredpath;
	private int setSoftWareMode; //设定系统模式，有2种，基本数据和通达信同步数据。
	private String datatablesfromserver;
	private String gephifileexportparth;
	public static int MODELSERVER=0, MODELCLIENT=1, MODELSERVERCLIENT=2;
	private int givenperiodofmonth;
	private int zhanbifengxizhouqi; //成交量占比分析周期
	private double bkdpzhanbimarker;
	private double ggdpzhanbimarker;
	private Boolean priavtemode;
	private String pythoninterpreter;
	
	private void getSystemInfoFromXML() 
	{
		File directory = new File("");//设定为当前文件夹
		try{
//		    logger.debug(directory.getCanonicalPath());//获取标准的路径
//		    logger.debug(directory.getAbsolutePath());//获取绝对路径
//		    this.systeminstalledpath = toUNIXpath(directory.getCanonicalPath()+ "\\");
		    Properties properties = System.getProperties();
		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //用户运行程序的当前目录
		    logger.debug(this.systeminstalledpath );
		}catch(Exception e)
		{
			System.exit(0);
		}
		
		File sysconfigfile = new File(this.getSysSettingFile() );
		if(!sysconfigfile.exists()) { //不存在，创建该文件，并显示设置窗口让客户设置相关信息
			
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
			
			 
			
			//本地数据库信息
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
			//远程数据库信息
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
			
//			//哪些数据从server读取
//			Element elermtdatatablefromserver = xmlroot.element("tablesfromserver");
//			this.datatablesfromserver = elermtdatatablefromserver.getText();
			
			try {
				xmlfileinput.close();
				tryingcount = 0;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (DocumentException e) {
			logger.debug(this.getSysSettingFile() + "存在错误，请修改");
			//e.printStackTrace();
			
			tryingcount ++;
			if( tryingcount == 3) {
				JOptionPane.showMessageDialog(null,"设置文件不完整，系统自动退出，重启后重新配置，再见！", "警告",JOptionPane.INFORMATION_MESSAGE);  
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

	// 单例实现  
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
	 public CurDataBase getCurrentDatabaseSource ()
	 {
		 return curdbs;
	 }
	 public  CurDataBase getRemoteCurrentDatabaseSource ()
	 {
		 return rmtcurdb;
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
		 String path = this.getSystemInstalledPath() + "/reports/TDX个股分析报告.txt";
		 
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
		 String path = this.getSystemInstalledPath() + "/reports/TDX重点关注历史报告.txt";
		 
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
			 String path = this.getSystemInstalledPath() + "/reports/TDX概念提示报告.txt";
			 
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
			String path =  this.getSystemInstalledPath()  + "/reports/TDX负面消息报告.txt";
			
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
			String path =  this.getSystemInstalledPath() + "/reports/TDX正负相关客户竞对报告.txt";
			
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
			String path =   this.getSystemInstalledPath() + "/reports/TDX个股产业链报告报告.txt";
			
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
			String path =    this.getSystemInstalledPath() + "/weeklyreports/占比模型输出/";
			
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
			 String path =  this.getSystemInstalledPath() + "/weeklyreports/市值分析输出/";
			 
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
			 String path =  this.getSystemInstalledPath() + "/weeklyreports/Gephi分析输出/";
			 
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
			return this.getTDXInstalledLocation() + "T0002/blocknew/blocknew.cfg";
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

