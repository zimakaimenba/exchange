package com.exchangeinfomanager.database;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.URL;

import java.nio.charset.Charset;

import java.sql.SQLException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;

import org.apache.http.impl.client.HttpClientBuilder;

import org.apache.log4j.Logger;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.python.icu.util.TimeZone;

import com.exchangeinfomanager.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.ParseBanKuaiWeeklyFielGetStocksProcessor;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.nodejibenmian.NodeJiBenMian;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.TDXNodesXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItemForJFC;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;


import com.mysql.jdbc.MysqlDataTruncation;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.sun.rowset.CachedRowSetImpl;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;



public class BanKuaiDbOperation 
{
	public BanKuaiDbOperation() 
	{
		connectdb = ConnectDataBase.getInstance();
		sysconfig = new SetupSystemConfiguration();
	}
	
	private  ConnectDataBase connectdb;
	private  SetupSystemConfiguration sysconfig;
	private static Logger logger = Logger.getLogger(BanKuaiDbOperation.class);
	
	/*
	 * 
	 */
	public List<BanKuai> getTDXBanKuaiList(String jys)   
	{
		List<BanKuai> tmpsysbankuailiebiaoinfo = new ArrayList<> ();
		
		String sqlquerystat;
		if(!jys.toLowerCase().equals("all"))
			 sqlquerystat = "SELECT * FROM\r\n" + 
			 		"(\r\n" + 
			 		"SELECT *   FROM 通达信板块列表 WHERE 指数所属交易所 = '" + jys + "') AS node\r\n" + 
			 		"LEFT   JOIN \r\n" + 
			 		"(SELECT * FROM 板块股票占比阈值 )\r\n" + 
			 		"AS nodezbextremelevel\r\n" + 
			 		"ON node.板块ID = nodezbextremelevel.代码\r\n" + 
			 		"\r\n" + 
			 		"LEFT   JOIN \r\n" + 
			 		"(SELECT 代码, MIN(交易日期)  SHminjytime , MAX(交易日期)  SHmaxjytime fROM   通达信板块每日交易信息\r\n" + 
			 		"GROUP BY 代码) shjyt \r\n" + 
			 		"\r\n" + 
			 		"ON shjyt.代码 = node.板块ID AND node.指数所属交易所 = 'SH'\r\n" + 
			 		"\r\n" + 
			 		"LEFT   JOIN \r\n" + 
			 		"(SELECT 代码, MIN(交易日期)  SZminjytime , MAX(交易日期)  SZmaxjytime fROM   通达信交易所指数每日交易信息\r\n" + 
			 		"GROUP BY 代码) szjyt \r\n" + 
			 		"\r\n" + 
			 		"ON szjyt.代码 = node.板块ID AND node.指数所属交易所='SZ'\r\n"  
					;
		else
			 sqlquerystat = 
						"SELECT * FROM\r\n" + 
						"(\r\n" + 
						"SELECT *   FROM 通达信板块列表 ) AS node\r\n" + 
						"LEFT   JOIN \r\n" + 
						"(SELECT * FROM 板块股票占比阈值 )\r\n" + 
						"AS nodezbextremelevel\r\n" + 
						"ON node.板块ID = nodezbextremelevel.代码\r\n" + 
						"\r\n" + 
						"LEFT   JOIN \r\n" + 
						"(SELECT 代码, MIN(交易日期)  SHminjytime , MAX(交易日期)  SHmaxjytime fROM   通达信板块每日交易信息\r\n" + 
						"GROUP BY 代码) shjyt \r\n" + 
						"\r\n" + 
						"ON shjyt.代码 = node.板块ID AND node.指数所属交易所 = 'SH'\r\n" + 
						"\r\n" + 
						"LEFT   JOIN \r\n" + 
						"(SELECT 代码, MIN(交易日期)  SZminjytime , MAX(交易日期)  SZmaxjytime fROM   通达信交易所指数每日交易信息\r\n" + 
						"GROUP BY 代码) szjyt \r\n" + 
						"\r\n" + 
						"ON szjyt.代码 = node.板块ID AND node.指数所属交易所='SZ'\r\n"  
						;
		
		CachedRowSetImpl rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	        while(rs.next()) {
	        	BanKuai tmpbk = new BanKuai (rs.getString("板块ID"),rs.getString("板块名称"),"TDX" );
	        	String zhishujys = rs.getString("指数所属交易所");
	        	tmpbk.getNodeJiBenMian().setSuoShuJiaoYiSuo(zhishujys);
	        	tmpbk.setBanKuaiLeiXing( rs.getString("板块类型描述") );
	        	tmpbk.getBanKuaiOperationSetting().setImportBKGeGu(rs.getBoolean("导入板块个股"));
	        	tmpbk.getBanKuaiOperationSetting().setExporttogehpi(rs.getBoolean("导出Gephi"));
	        	tmpbk.getBanKuaiOperationSetting().setImportdailytradingdata(rs.getBoolean("导入交易数据"));
	        	tmpbk.getBanKuaiOperationSetting().setShowinbkfxgui(rs.getBoolean("板块分析"));
	        	tmpbk.getBanKuaiOperationSetting().setShowincyltree(rs.getBoolean("产业链树"));
	        	tmpbk.getBanKuaiOperationSetting().setExportTowWlyFile(rs.getBoolean("周分析文件"));
	        	tmpbk.getBanKuaiOperationSetting().setBanKuaiLabelColor(rs.getString("DefaultCOLOUR"));
	        	tmpbk.getNodeJiBenMian().setNodeCjlZhanbiLevel (rs.getDouble("成交量占比下限"), rs.getDouble("成交量占比上限"));
	        	tmpbk.getNodeJiBenMian().setNodeCjeZhanbiLevel (rs.getDouble("成交额占比下限"), rs.getDouble("成交额占比上限"));
	        	tmpbk.getNodeJiBenMian().setIsCoreZhiShu (rs.getBoolean("核心指数"));
	        	
	        	if(zhishujys.equalsIgnoreCase("SH")) {
	        		try {	LocalDate shmaxdate = rs.getDate("SHmaxjytime").toLocalDate();
	        				tmpbk.getShuJuJiLuInfo().setJyjlmaxdate(shmaxdate);
	        		} catch(java.lang.NullPointerException e) {
	        			tmpbk.getShuJuJiLuInfo().setJyjlmaxdate(null);
	        		}
	        		try {	LocalDate shmindate = rs.getDate("SHminjytime").toLocalDate();
	        				tmpbk.getShuJuJiLuInfo().setJyjlmindate(shmindate);
	        		} catch(java.lang.NullPointerException e) {
	        			tmpbk.getShuJuJiLuInfo().setJyjlmaxdate(null);
	        		}
	        	} else if(zhishujys.equalsIgnoreCase("SZ")) {
	        		try {	LocalDate szmaxdate = rs.getDate("SZmaxjytime").toLocalDate();
	        				tmpbk.getShuJuJiLuInfo().setJyjlmaxdate(szmaxdate);
		    		} catch(java.lang.NullPointerException e) {
		    				tmpbk.getShuJuJiLuInfo().setJyjlmaxdate(null);
		    		}
		    		try {	LocalDate szmindate = rs.getDate("SZminjytime").toLocalDate();
	        				tmpbk.getShuJuJiLuInfo().setJyjlmindate(szmindate);
		    		} catch(java.lang.NullPointerException e) {
		    				tmpbk.getShuJuJiLuInfo().setJyjlmaxdate(null);
		    		}
	        	}
	        	
	        	tmpsysbankuailiebiaoinfo.add(tmpbk);
	        }
	    } catch(java.lang.NullPointerException e){ e.printStackTrace();
	    } catch (SQLException e) {e.printStackTrace();
	    } catch(Exception e){e.printStackTrace();
	    } finally {	if(rs != null)	try {rs.close();rs = null;} catch (SQLException e) {e.printStackTrace();}
	    } 
	    
	    return tmpsysbankuailiebiaoinfo;
	    

	}
	/*
	 * 
	 */
	public BanKuai getBanKuaiBasicInfo(BanKuai bk)
	{
		String bkcode = bk.getMyOwnCode();
		CachedRowSetImpl rsagu = null;
		try {
			 String sqlquerystat = "select 板块名称, 概念时间,概念板块提醒,负面消息时间,负面消息,券商评级时间,券商评级提醒,正相关及客户,负相关及竞争对手,客户,竞争对手 "
			 		+ " FROM 通达信板块列表  B "
//					+ " RIGHT JOIN 通达信板块社交关系表 F ON (F.板块左 = m.板块ID OR F.板块右 = m.板块ID)  "
			 		+ "WHERE B.板块ID = '" + bkcode +"' \r\n"  
		 			;
			logger.debug(sqlquerystat);
			rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);
			while(rsagu.next())
				setSingleNodeInfo (bk,rsagu);
			
			
			sqlquerystat = "SELECT * FROM 通达信板块社交关系表 F"
					+ " WHERE (F.板块左 = '" + bkcode + "' OR F.板块右 = '" + bkcode + "')"
					;
			rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);
			while(rsagu.next()) {
				String friendcodeleft = rsagu.getString("板块左"); 
				String friendcoderight = rsagu.getString("板块右");
				Boolean relationship = rsagu.getBoolean("关系");
				if(relationship == null)					relationship = true;
				if(!friendcodeleft.equals(bkcode) && relationship )					    bk.addSocialFriendsPostive(friendcodeleft);
				if(!friendcoderight.equals(bkcode) && relationship )					bk.addSocialFriendsPostive(friendcoderight);
				if(!friendcodeleft.equals(bkcode) && !relationship )					bk.addSocialFriendsNegtive(friendcodeleft);
				if(!friendcoderight.equals(bkcode) && !relationship )					bk.addSocialFriendsNegtive(friendcoderight);	
			}
		} catch (Exception e) { e.printStackTrace();
		} finally {
			try {	rsagu.close();	} catch (SQLException e) {	e.printStackTrace();}
			rsagu = null;
		}
		
		return bk;
	}
	
	/*
	 * 
	 */
	public BkChanYeLianTreeNode getStockBasicInfo(BkChanYeLianTreeNode stock) 
	{
		String stockcode = stock.getMyOwnCode();
		CachedRowSetImpl rsagu = null;
		try {
			 String sqlquerystat= "SELECT * FROM A股   WHERE 股票代码 =" +"'" + stockcode +"'" ;	
			rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);
			while(rsagu.next())
				setSingleNodeInfo (stock,rsagu);

		} catch (Exception e) {	e.printStackTrace();
		} finally {
			try {rsagu.close();} catch (SQLException e) {e.printStackTrace();}
			rsagu = null;
		}
		
		return stock;
	}
	/*
	 * 获取用户输入的node的基本信息，node可能是板块也可能是个股
	 */
	public List<BkChanYeLianTreeNode> getNodesBasicInfo(String nodecode) 
	{
		List<BkChanYeLianTreeNode> nodenamelist = new ArrayList<BkChanYeLianTreeNode> ();
		CachedRowSetImpl rs = null;
		
		try {
			String sqlquerystat = "select 股票名称, 'a股' as tablename, 'gegu' as type from a股 where  股票代码 = '" + nodecode + "'" + 
					" union \r\n" + 
					" select 板块名称, '通达信板块列表' as tablename , 'bankuai' as type from 通达信板块列表 where 板块ID = '" + nodecode + "'" 
//					+" union\r\n" + 
//					" select 板块名称, '通达信交易所指数列表' as tablename, 'zhishu' as type from 通达信交易所指数列表 where 板块ID = '" + nodecode + "'"
					;
				logger.debug(sqlquerystat);
		    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	
		        while(rs.next()) {  
		        	BkChanYeLianTreeNode tmpnode = null;
		        	if(rs.getString("type").equals("gegu")) {
		        		tmpnode = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXGG);
		        		this.getStockBasicInfo(tmpnode );
		        	} else {
		        		tmpnode = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXBK);
		        		this.getBanKuaiBasicInfo( (BanKuai)tmpnode );
		        	}
		        	nodenamelist.add(tmpnode);
		        }
		       
		    } catch(java.lang.NullPointerException e){e.printStackTrace();
		    } catch (SQLException e) {e.printStackTrace();
		    } catch(Exception e){e.printStackTrace();
		    } finally {
		    	if(rs != null)	try {rs.close();rs = null;} catch (SQLException e) {e.printStackTrace();}
		    }
		
		return nodenamelist;
	}

	private void setSingleNodeInfo(BkChanYeLianTreeNode tmpnode, CachedRowSetImpl rs) 
	{
		if(tmpnode.getType() == 6) {
			try {
				String stockname = rs.getString("股票名称").trim();
				tmpnode.setMyOwnName(stockname);
			} catch(java.lang.NullPointerException ex1) {tmpnode.setMyOwnName( " ");
			} catch (Exception ex) {}
			try {
				Boolean stockname = rs.getBoolean("已退市");
				//这里应该设置退市状态，目前没那么重要，所以暂时不开发
			} catch(java.lang.NullPointerException ex1) {Boolean stockname = false;
			} catch (Exception ex) {}
		} else {
			try {
				String bankuainame = rs.getString("板块名称").trim();
				tmpnode.setMyOwnName(bankuainame);
			} catch(java.lang.NullPointerException ex1) {tmpnode.setMyOwnName(" ");
			} catch (Exception ex) {}
		}
		
		NodeJiBenMian nodejbm = tmpnode.getNodeJiBenMian();
		try {
			java.util.Date gainiantishidate = rs.getDate("概念时间");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(gainiantishidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			nodejbm.setGainiantishidate(ldgainiantishidate); 
		} catch(java.lang.NullPointerException ex1) {nodejbm.setGainiantishidate(null);
		} catch (Exception e) {}
		
		try {
			String gainiantishi = rs.getString("概念板块提醒");
			nodejbm.setGainiantishi(gainiantishi);
		} catch(java.lang.NullPointerException ex1) {nodejbm.setGainiantishi(" ");			
		} catch (Exception e) {e.printStackTrace();}
		
		try 
		{
			Date quanshangpingjidate = rs.getDate("券商评级时间");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(quanshangpingjidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			nodejbm.setQuanshangpingjidate(ldgainiantishidate);
		} catch(java.lang.NullPointerException ex1) {nodejbm.setQuanshangpingjidate(null);
		} catch (Exception e) {e.printStackTrace();}
	
		try	{
			String quanshangpingji = rs.getString("券商评级提醒").trim();
			nodejbm.setQuanshangpingji(quanshangpingji);
		} catch(java.lang.NullPointerException ex1) {nodejbm.setQuanshangpingji("");			
		} catch (Exception e) {e.printStackTrace();}
		
		try	{
			Date fumianxiaoxidate = rs.getDate("负面消息时间");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(fumianxiaoxidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			nodejbm.setFumianxiaoxidate(ldgainiantishidate);
		} catch(java.lang.NullPointerException ex1) {nodejbm.setFumianxiaoxidate(null);
		} catch(Exception ex2) {ex2.printStackTrace();}

		try	{
			String fumianxiaoxi = rs.getString("负面消息").trim();
			nodejbm.setFumianxiaoxi(fumianxiaoxi);
		} catch(java.lang.NullPointerException ex1) {nodejbm.setFumianxiaoxi("");			
		} catch (Exception e) {e.printStackTrace();}

	}
	/*
	 * 控制 同步通达信相关的信息  导入通达信定义的板块信息 ，包括概念，行业，风格，指数 板块
	 */
	public File refreshTDXSystemBanKuai ()
	{
		File tmpreportfolder = Files.createTempDir(); 
		File tmprecordfile = new File(tmpreportfolder + "同步通达信系统板块报告.txt");

		//更新通达信系统所有板块信息，在更新中，把新的存入数据库，
		this.refreshTDXZhiShuShangHaiLists (tmprecordfile); //上海指数
		this.refreshTDXZhiShuShenZhenLists (tmprecordfile); //深圳指数
		this.refreshTDXAllBanKuaiToSystem(tmprecordfile); //对880板块，更新一些信息
		 
		//同步相关板块个股信息
		this.refreshTDXFengGeBanKuaiToGeGu(tmprecordfile); //风格
		this.refreshTDXGaiNianBanKuaiToGeGu(tmprecordfile); //概念
		this.refreshTDXHangYeBanKuaiToGeGu(tmprecordfile); //行业
		this.refreshTDXZhiShuBanKuaiToGeGu(tmprecordfile); //通达信指数和股票对应关系

		return tmprecordfile;
	}
	/*
	 * 大智慧股本文件分析，目前没有用到
	 */
//	public File refreshDZHStockGuQuan() 
//	{
////		File file = new File(sysconfig.getDZHStockGuQuanFile() );
//		File file = new File("E:/stock/dzh365/Download/PWR/full_sh.PWR");
//		 if(!file.exists() ) {
//			 return null;
//		 }
//		 
//		 BufferedInputStream dis = null;
//		 FileInputStream in = null;
//		 try {
//			    in = new FileInputStream(file);  
//			    dis = new BufferedInputStream(in);
//			    
//			    
//			    int fileheadbytenumber = 12;
//	            byte[] itemBuf = new byte[fileheadbytenumber];
//	            dis.read(itemBuf, 0, fileheadbytenumber); 
//	            String fileHead =new String(itemBuf,0,fileheadbytenumber);  
//	            logger.debug(fileHead);
//	            
//	            while(true) {
//	            	int sigbk = 16;
//		            byte[] itemBuf2 = new byte[sigbk];
////		            int i=0;
//		            dis.read(itemBuf2, 0, sigbk) ;
//		            String stockcode = new String(itemBuf2, 0, sigbk );
//		            logger.debug(stockcode);
//		            
//		            int sigbk2 = 20;
//		            byte[] itemBuf3 = new byte[sigbk2];
////		            int i=0;
//		            dis.read(itemBuf3, 0, sigbk2) ;
//		            String stockdate = new String(itemBuf3, 0, sigbk2 );
//		            logger.debug(stockdate);
		            
//		            sigbk2 = 4;
//		            itemBuf3 = new byte[sigbk2];
////		            int i=0;
//		            dis.read(itemBuf3, 0, sigbk2) ;
//		            String stocknext = new String(itemBuf3, 0, sigbk2 );
//		            logger.debug(stocknext);
//		            
//		            sigbk2 = 4;
//		            itemBuf3 = new byte[sigbk2];
////		            int i=0;
//		            dis.read(itemBuf3, 0, sigbk2) ;
//		            stocknext = new String(itemBuf3, 0, sigbk2 );
//		            logger.debug(stocknext);
//		            
//		            sigbk2 = 4;
//		            itemBuf3 = new byte[sigbk2];
////		            int i=0;
//		            dis.read(itemBuf3, 0, sigbk2) ;
//		            stocknext = new String(itemBuf3, 0, sigbk2 );
//		            logger.debug(stocknext);
//		            
//		            sigbk2 = 4;
//		            itemBuf3 = new byte[sigbk2];
////		            int i=0;
//		            dis.read(itemBuf3, 0, sigbk2) ;
//		            stocknext = new String(itemBuf3, 0, sigbk2 );
//		            logger.debug(stocknext);

//	            }
//			    
//		 } catch (Exception e) {
//			 e.printStackTrace();
//			 return null;
//		 } finally {
//			 try {
//				in.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//             try {
//				dis.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}     
//		 }
		 
		 
		 
//		return null;
//	}
	/*https://www.55188.com/thread-9111311-1-1.html
	 * 通达信的概念板块与个股对应
	 */
	private int refreshTDXGaiNianBanKuaiToGeGu (File tmprecordfile)
	{
		File file = new File(sysconfig.getTDXGaiNianBanKuaiToGeGuFile() );
		 if(!file.exists() ) {
			 try {	Files.append( "没有发现" +  sysconfig.getTDXGaiNianBanKuaiToGeGuFile() +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			 } catch (IOException e) {e.printStackTrace();}
			 return -1;
		 }
		 
		 int addednumber =0;BufferedInputStream dis = null;FileInputStream in = null;
		 try {
			    in = new FileInputStream(file);  
			    dis = new BufferedInputStream(in);
               
           // 股票板块信息文件头格式，T0002/hq_cache/block.dat block_fg.dat block_gn.dat block_zs.dat
		   // 文件头：384字节
//               struct TTDXBlockHeader
//               {
//                   char         szVersion[64];        // 0, Registry ver:1.0 (1999-9-28)
//                   int            nIndexOffset;            // 64, 0x00000054
//                   int            nDataOffset;            // 68, 0x00000180
//                   int            nData1;                // 72, 0x00000003
//                   int            nData2;                // 76, 0x00000000
//                   int            nData3;                // 80, 0x00000003
//               };
               int fileheadbytenumber = 384;
               byte[] itemBuf = new byte[fileheadbytenumber];
               dis.read(itemBuf, 0, fileheadbytenumber); 
               String fileHead =new String(itemBuf,0,fileheadbytenumber);  

               //板块个数：2字节
               dis.read(itemBuf, 0, 2); 
               String bknumber =new String(itemBuf,0,2);  
               
			 
               //各板块数据存储结构（紧跟板块数目依次存放）
//               第一个版块的起始位置为0x182h。
//               每个板块占据的存储空间为2813个字节，每个板块最多包括400只股票。(2813 -9 - 2 - 2) / 7 =  400
//               板块名称：9字节
//               该板块包含的个股个数：2字节
//               板块类别：2字节
//               该板块下个股代码列表（连续存放，直到代码为空）
//                   个股代码：7字节
//               struct TTDXBlockRecord
//               {
//                   char         szName[9];
//                   short        nCount;
//                   short        nLevel;
//                   char         szCode[400][7];
//               };
               int sigbk = 2813;
               byte[] itemBuf2 = new byte[sigbk];
               Files.append("开始导入通达信股票概念对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   //板块名称
            	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
            	   CachedRowSetImpl rspd = null; 
	   			   try {
	   			    	String sqlquerystat = "SELECT *  FROM 通达信板块列表 WHERE 板块名称='"  + gupiaoheader + "'";
	   			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			    	String bkcode = null;
	   			        while(rspd.next())  
	   			        	bkcode = rspd.getString("板块ID");
	   			        
	   			        	BkChanYeLianTreeNode bankuai = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
		   			        if( bankuai != null && !((BanKuai)bankuai).getBanKuaiOperationSetting().isImportBKGeGu()   ) 
		            		   continue;
	   			    } catch(java.lang.NullPointerException e){ e.printStackTrace();
	   			    } catch (SQLException e) {e.printStackTrace();
	   			    } catch(Exception e){e.printStackTrace();
	   			    } finally {
	   			    	if(rspd != null) try {rspd.close();rspd = null; } catch (SQLException e) {e.printStackTrace();}
	   			    }
            	   
            	   //板块个股
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   Set<String> tmpstockcodesetnew = new HashSet<>(tmpstockcodestr);

                   //从数据库中读出当前该板块的个股
                   Set<String> tmpstockcodesetold = new HashSet<>();
                   CachedRowSetImpl rs = null;
	   			    try {
	   			    	String sqlquerystat = "SELECT gnbk.股票代码  FROM 股票通达信概念板块对应表 gnbk, 通达信板块列表 bklb"
		    						+ " WHERE  gnbk.板块代码 = bklb.`板块ID`"
		    						+ " AND bklb.`板块名称` = '"  + gupiaoheader + "'"
		    						+ " AND ISNULL(gnbk.移除时间)"
		    						;
	   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	while(rs.next()) {
	   			    		String stockcode = rs.getString("股票代码");
	   			        	tmpstockcodesetold.add(stockcode);
	   			    	}
	   			    } catch(java.lang.NullPointerException e) { e.printStackTrace();
	   			    } catch (SQLException e) { e.printStackTrace();
	   			    } catch(Exception e) { e.printStackTrace();
	   			    } finally {
				    	if(rs != null)
							try { rs.close();rs = null;	} catch (SQLException e) {e.printStackTrace();}
				    }
	   			    
	   			 //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要从数据库中删除
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
		   	        	String sqlupdatestat = "UPDATE 股票通达信概念板块对应表 JOIN 通达信板块列表"
       							+ "  ON  股票通达信概念板块对应表.`股票代码` = " + "'" + str.trim() + "'"
       							+ "  AND isnull(移除时间)"
       							+ "  and  股票通达信概念板块对应表.`板块代码` = 通达信板块列表.`板块ID` "
       							+ "  and 通达信板块列表.`板块名称`=" + "'" + gupiaoheader + "'"
       							+ "  SET 移除时间 = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
       							;
				   		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   	        }
		   	     differencebankuaiold = null;
		   	     
	   		        //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   		    	String sqlinsertstat = "insert into 股票通达信概念板块对应表(股票代码,板块代码,股票权重)"
								+ "	  SELECT '" + str + "', 通达信板块列表.`板块ID`, 10 "
								+ " FROM 通达信板块列表  where 通达信板块列表.`板块名称` = '" + gupiaoheader + "'"   
								;
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				addednumber ++;
		                Files.append("加入：" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   		 differencebankuainew = null;
               }
		 } catch (Exception e) { e.printStackTrace();return -1;
		 } finally {
			 try {in.close();  } catch (IOException e) { e.printStackTrace();}
             try {dis.close(); } catch (IOException e) { e.printStackTrace();}     
		 }
		
		 return addednumber;
	}
	/*
	 * 对余880指数板块，有一些在TDX配置文件中有额外信息，更新一下
	 */
	private boolean refreshTDXAllBanKuaiToSystem (File tmprecordfile) 
	{
		FileReader tongdaxinfile = null;
		try {
			tongdaxinfile = new FileReader(sysconfig.getTDXSysAllBanKuaiFile() );
		} catch (FileNotFoundException e) {e.printStackTrace();}
		BufferedReader bufr = new BufferedReader (tongdaxinfile);
		
//		Map<String,List<String>> tmpsysbkmap = new HashMap<String, List<String>> ();
		String line = null;  
        try {
			while((line = bufr.readLine()) != null) {
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //内蒙板块|880232|3|1|0|32
					String newbkcode = tmpbkinfo.get(1);
					String newbkname = tmpbkinfo.get(0);
		        	String newbktdxswcode = tmpbkinfo.get(5);
		        	String bkcjs = "sh";
					
					String sqlinsertstat = "INSERT INTO  通达信板块列表(板块ID,对应TDXSWID,板块名称,指数所属交易所) values ("
	   						+ " '" + newbkcode.trim() + "'" + ","
	   						+ " '" + newbktdxswcode.trim() + "'" + ","
	   						+ " '" + newbkname.trim() + "'" + ","
	   						+ " '" + bkcjs.trim() + "'"
	   						+ ")"
	   						+ " ON DUPLICATE KEY UPDATE "
	   						+ " 对应TDXSWID =" + " '" + newbktdxswcode.trim() + "'" + "," 
	   						+ " 板块名称=" + " '" + newbkname.trim() + "'" + ","
	   						+ " 指数所属交易所 = " + " '" + bkcjs.trim() + "'" 
	   						;
	   				try {
						int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
					} catch (MysqlDataTruncation e) {e.printStackTrace();
					} catch (SQLException e) {e.printStackTrace();} 
			}
		} catch (IOException e) {e.printStackTrace();
		} finally {
			try {bufr.close();} catch (IOException e) {e.printStackTrace();}
			try {tongdaxinfile.close();} catch (IOException e) {e.printStackTrace();}
		}
        
        try {
			Files.append("开始导入通达信板块信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		} catch (IOException e3) {e3.printStackTrace();}
        
		return true;
	}
	/*
	 * 通达信的行业与个股对应文件
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
	private int refreshTDXHangYeBanKuaiToGeGu (File tmprecordfile)
	{
		File fileHyTDm = new File(sysconfig.getTDXHangYeToDaiMaFile() );  //"incon.dat";
		 if(!fileHyTDm.exists() ) {
			 logger.debug("File not exist");
			 return -1;
		 }
		 File fileHyTGg = new File(sysconfig.getTDXHangYeBanKuaiToGeGuFile() );  //"T0002/hq_cache/" + "tdxhy.cfg";
		 if(!fileHyTGg.exists() ) {
			 logger.debug("File not exist");
			 return -1;
		 }
		 
		 //把板块和对应的板块代码存入map
		 HashMap<String,String> hydmmap = new HashMap<String,String> (); 
		 int allupdatednum = 0;
		 FileReader fr = null;
		 BufferedReader  bufr = null; 
		 try {
			 fr = new FileReader(sysconfig.getTDXHangYeToDaiMaFile());
			 bufr = new BufferedReader (fr);
			
			String line = null;
			int i=0;
			do {
			} while ( !(line = bufr.readLine() ).equals("#TDXNHY") );
			
			while ( !(line = bufr.readLine() ).equals("######") ) {  //行业名称和T开头代码对应关系
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //T01|能源  T010101|煤炭开采
				hydmmap.put(tmpbkinfo.get(0).trim(), tmpbkinfo.get(1).trim());
			}
			
            Files.append("开始导入通达信股票行业对应信息:" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			fr = new FileReader(sysconfig.getTDXHangYeBanKuaiToGeGuFile()); //通达信个股和板块的对应关系
			bufr = new BufferedReader (fr);
			while((line = bufr.readLine()) != null) {
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //0|000001|T1001|440101
				String stockcode = tmpbkinfo.get(1).trim();
				String stockbkcode = tmpbkinfo.get(2).trim(); //T开头的代码,不是880开头的
				String stockbkname = null;
				 
				try {
					stockbkname = hydmmap.get(stockbkcode).trim();
				 }catch(java.lang.NullPointerException e){
					stockbkname = "未知";
				}
				
				 
				//先在数据库中找出该股票，比较行业，如果不一样旧更新，如果相同就不更新
				CachedRowSetImpl rs = null;
				try {
   			    	String sqlquerystat = "SELECT *  FROM 股票通达信行业板块对应表  WHERE  股票代码 = " 
   			    							+ "'"  + stockcode + "'"
//   			    							+ " AND 对应TDXSWID=" +  "'" + stockbkcode + "'"
   			    							+ " AND isnull(移除时间)"
   			    							;
   			    	logger.debug(sqlquerystat);
   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   			    	
   			        rs.last(); 
   			        int rows = rs.getRow();  //个股和行业是一对一的，如果有大于1的行数，说明有问题,可能是过去同步发生的问题，现在要纠正
   			        rs.first();  
   			        if(rows == 0) { //该股票还不存在于数据库中，要添加
//   			        	String sqlinsertstat = "INSERT INTO  股票通达信行业板块对应表(股票代码,行业板块,对应TDXSWID,股票权重) values ("
//		   						+ "'" + stockcode.trim() + "'" + ","
//		   						+ "'" + stockbkname.trim() + "'"  + ","
//		   						+ "'" + stockbkcode.trim() + "'" + ","
//		   						+ 10
//		   						+ ")"
//		   						;
   			        	String sqlinsertstat =  "insert into 股票通达信行业板块对应表(股票代码,板块代码,对应TDXSWID,股票权重)"
												+ " SELECT '" + stockcode.trim() + "', 通达信板块列表.`板块ID`,'" + stockbkcode + "',10 "
												+ " FROM 通达信板块列表  where 通达信板块列表.`对应TDXSWID` = '" + stockbkcode + "'"
												;
		   				logger.debug(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   	            Files.append("加入：" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   				
		   				allupdatednum++;
   			        } else if(rows == 1) { //存在
   			        	String stockbkanameindb = rs.getString("对应TDXSWID");
   			        	if( !stockbkanameindb.equals(stockbkcode) ) { //不一样说明板块有变化，老的板块更新移除时间，同时加入一条新信息，反应新的板块
   			        		checkStockHangYeNewInfo (stockcode,stockbkanameindb,stockbkcode);
   			        		//先把老记录 移除时间更新
//	   			     		String sqlupdatestat = "UPDATE  股票通达信行业板块对应表  SET " 
//	   			 				+ " 移除时间 =" + "'" +  sysconfig.formatDate(new Date() ) + "'" + ","
//	   			 				+ " WHERE 股票代码 = " + "'" + stockcode.trim() + "'" 
//	   			 				+ " AND 对应TDXSWID=" + stockbkanameindb 
//	   			 				+ " AND isnull(移除时间)"
//	   			 				;
   			        		allupdatednum ++;
//   			        		String sqlupdatestat = "UPDATE 股票通达信行业板块对应表 "
//   	       										+ "  SET 移除时间 = " + "'" +  LocalDate.now().toString()  + "'"
//   	       										+ "  WHERE  股票通达信行业板块对应表.`股票代码` = " + "'" + stockcode.trim() + "'"
//   	       										+ "  AND isnull(移除时间)"
//   	       										+ "  AND 股票通达信行业板块对应表.`对应TDXSWID` = " + "'" + stockbkanameindb + "'"
//   	       							;
//		   			 		logger.debug(sqlupdatestat);
//		   			 		@SuppressWarnings("unused")
//		   			 		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   			 		
		   			 		
		   			 		//再增加一条记录
//			   			 	String sqlinsertstat = "INSERT INTO  股票通达信行业板块对应表(股票代码,行业板块,对应TDXSWID,股票权重) values ("
//			   						+ "'" + stockcode.trim() + "'" + ","
//			   						+ "'" + stockbkname.trim() + "'"  + ","
//			   						+ "'" + stockbkcode.trim() + "'" + ","
//			   						+ 10
//			   						+ ")"
//			   						;
//		   			 		String sqlinsertstat =  "insert into 股票通达信行业板块对应表(股票代码,板块代码,对应TDXSWID,股票权重)"
//								+ " SELECT '" + stockcode.trim() + "', 通达信板块列表.`板块ID`,'" + stockbkcode + "',10 "
//								+ " FROM 通达信板块列表  where 通达信板块列表.`对应TDXSWID` = '" + stockbkcode + "'"
//								;
//			   				logger.debug(sqlinsertstat);
//			   				autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//		   	            Files.append("加入：" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//			   	        Files.append("更新：" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
   			        	}
   			        } else if(rows > 1) { //通达信中个股和行业是一对一的，如果有大于1的行数，说明有问题,可能是过去同步发生的问题，现在要纠正
   			        	JOptionPane.showMessageDialog(null,"错误！"+ stockcode + "有"+ rows + "个行业板块！请检查数据一致性！");
//   			        	do {
//   			        		//如果有错误行的同时板块有更新，判断哪个是错误行，哪个是需要更新的板块？怎么搞？ 2017.12.14
//   			        		//最好是出一个对话框，让用户选择，现在出一个通知，后面慢慢想其他办法,最后觉得还是手动在数据库里面改，因为费时间涉及一个机制，可能修改完就不再出现了，投入产出不合算
//   			        		String bkcode = rs.getString("板块代码");
//   			        		String stockbkanameindb = rs.getString("对应TDXSWID");
//   			        		int action = JOptionPane.showConfirmDialog(null, stockcode + "的" + bkcode + "信息是否需要删除？","错误！"+ stockcode + "有"+ rows + "个行业板块！请检查数据一致性！", JOptionPane.YES_NO_OPTION);
//   							if(0 == action) {//用户同意则删除
//   								String sqldeletestat = "DELETE  FROM 股票通达信行业板块对应表"
//   													+ " WHERE 股票通达信行业板块对应表.`股票代码` = " + "'" + stockcode.trim() + "'"
//   													+ " AND 股票通达信行业板块对应表.`对应TDXSWID` = " + "'" + stockbkanameindb + "'" 
//   													+ " AND 股票通达信行业板块对应表.`板块代码` = " + "'" + bkcode + "'"
//   													;
//   								logger.debug(sqldeletestat);
//   			   			 		@SuppressWarnings("unused")
//   			   			 		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletestat);
//   							} else { //不删除的话，就更新和rows =1一样的情况
//   		   			        	if( !stockbkanameindb.equals(stockbkcode) )  //不一样说明板块有变化，老的板块更新移除时间，同时加入一条新信息，反应新的板块
//   		   			        		checkStockHangYeNewInfo (stockcode,stockbkanameindb,stockbkcode);
//   							}
//   			        	} while(rs.next() );
   			        }
   			      
   			    } catch(java.lang.NullPointerException e) { e.printStackTrace();
   			    } catch (SQLException e) {e.printStackTrace();
   			    } catch(Exception e){e.printStackTrace();
   			    } finally {
   			    	if(rs != null)	try {rs.close();rs = null;} catch (SQLException e) {e.printStackTrace();}
   			    }
			}
		} catch (FileNotFoundException e) {e.printStackTrace();return -1;
		} catch (IOException e) {e.printStackTrace();return -1;
		} finally {
			try {fr.close(); fr = null;} catch (IOException e) {e.printStackTrace();}
			try {bufr.close();bufr = null; } catch (IOException e) {e.printStackTrace();}
		}
		 
		 return allupdatednum;
	}
	/*
	 * 
	 */
	public int refreshTDXDrawLineInfo (File tmprecordfile)
	{
		File file = new File(sysconfig.getTDXDrawLineNameFile() );
//		 if(!file.exists() ) {
//			 try {
//				Files.append( "没有发现" +  sysconfig.getTDXGaiNianBanKuaiToGeGuFile() +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			 return -1;
//		 }
		 
		 int addednumber =0;
		 BufferedInputStream dis = null;
		 FileInputStream in = null;
		 try {
			    in = new FileInputStream(file);  
			    dis = new BufferedInputStream(in);
               
           // 股票板块信息文件头格式，T0002/hq_cache/block.dat block_fg.dat block_gn.dat block_zs.dat
		   // 文件头：384字节
//               struct TTDXBlockHeader
//               {
//                   char         szVersion[64];        // 0, Registry ver:1.0 (1999-9-28)
//                   int            nIndexOffset;            // 64, 0x00000054
//                   int            nDataOffset;            // 68, 0x00000180
//                   int            nData1;                // 72, 0x00000003
//                   int            nData2;                // 76, 0x00000000
//                   int            nData3;                // 80, 0x00000003
//               };
               int fileheadbytenumber = 500;
               byte[] itemBuf = new byte[fileheadbytenumber];
               dis.read(itemBuf, 0, fileheadbytenumber); 
               String fileHead =new String(itemBuf,0,fileheadbytenumber);  
               logger.debug(fileHead);
               
               //板块个数：2字节
               dis.read(itemBuf, 0, 2); 
               String bknumber =new String(itemBuf,0,2);  
               logger.debug(bknumber);
			 
               //各板块数据存储结构（紧跟板块数目依次存放）
//               第一个版块的起始位置为0x182h。
//               每个板块占据的存储空间为2813个字节，每个板块最多包括400只股票。(2813 -9 - 2 - 2) / 7 =  400
//               板块名称：9字节
//               该板块包含的个股个数：2字节
//               板块类别：2字节
//               该板块下个股代码列表（连续存放，直到代码为空）
//                   个股代码：7字节
//               struct TTDXBlockRecord
//               {
//                   char         szName[9];
//                   short        nCount;
//                   short        nLevel;
//                   char         szCode[400][7];
//               };
               int sigbk = 2813;
               byte[] itemBuf2 = new byte[sigbk];
               int i=0;
               Files.append("开始导入通达信股票概念对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   //板块名称
            	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
            	   CachedRowSetImpl rspd = null; 
	   			   try {
	   			    	String sqlquerystat = "SELECT *  FROM 通达信板块列表 WHERE 板块名称='"  + gupiaoheader + "'";
	   			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			    	String bkcode = null;
	   			        while(rspd.next())  
	   			        	bkcode = rspd.getString("板块ID");
	   			        
	   			        	BkChanYeLianTreeNode bankuai = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
		   			        if( bankuai != null && !((BanKuai)bankuai).getBanKuaiOperationSetting().isImportBKGeGu()   ) 
		            		   continue;
	   			    } catch(java.lang.NullPointerException e){ e.printStackTrace();
	   			    } catch (SQLException e) {e.printStackTrace();
	   			    } catch(Exception e){e.printStackTrace();
	   			    } finally {
	   			    	if(rspd != null) try {rspd.close();rspd = null; } catch (SQLException e) {e.printStackTrace();}
	   			    }
            	   
            	   //板块个股
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                   
                   //从数据库中读出当前该板块的个股
                   Set<String> tmpstockcodesetold = new HashSet();
                   CachedRowSetImpl rs = null;
	   			    try {
	   			    	String sqlquerystat = "SELECT gnbk.股票代码  FROM 股票通达信概念板块对应表 gnbk, 通达信板块列表 bklb"
		    						+ " WHERE  gnbk.板块代码 = bklb.`板块ID`"
		    						+ " AND bklb.`板块名称` = '"  + gupiaoheader + "'"
		    						+ " AND ISNULL(gnbk.移除时间)"
		    						;
	   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	while(rs.next()) {
	   			    		String stockcode = rs.getString("股票代码");
	   			        	tmpstockcodesetold.add(stockcode);
	   			    	}
	   			    } catch(java.lang.NullPointerException e){e.printStackTrace();
	   			    } catch (SQLException e) {e.printStackTrace();
	   			    } catch(Exception e){e.printStackTrace();
	   			    } finally {
				    	if(rs != null)		try {rs.close();rs = null;} catch (SQLException e) {e.printStackTrace();}
				    }
	   			    
	   			 //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要从数据库中删除
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
		   	        	String sqlupdatestat = "UPDATE 股票通达信概念板块对应表 JOIN 通达信板块列表"
       							+ "  ON  股票通达信概念板块对应表.`股票代码` = " + "'" + str.trim() + "'"
       							+ "  AND isnull(移除时间)"
       							+ "  and  股票通达信概念板块对应表.`板块代码` = 通达信板块列表.`板块ID` "
       							+ "  and 通达信板块列表.`板块名称`=" + "'" + gupiaoheader + "'"
       							+ "  SET 移除时间 = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
       							;
				       	logger.debug(sqlupdatestat);
				   		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   	        }
		   	     differencebankuaiold = null;
	   			   
	   		        //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   				
//		   				String sqlinsertstat = "INSERT INTO  股票通达信概念板块对应表(股票代码,概念板块,股票权重) values ("
//		   						+ "'" + str.trim() + "'" + ","
//		   						+ "'" + gupiaoheader + "'"  + ","
//		   						+ 10 
//		   						+ ")"
//		   						;
		   		    	String sqlinsertstat = "insert into 股票通达信概念板块对应表(股票代码,板块代码,股票权重)"
								+ "	  SELECT '" + str + "', 通达信板块列表.`板块ID`, 10 "
								+ " FROM 通达信板块列表  where 通达信板块列表.`板块名称` = '" + gupiaoheader + "'"   
								;
		   				logger.debug(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				addednumber ++;
		                Files.append("加入：" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   		 differencebankuainew = null;
               }
		 } catch (Exception e) {e.printStackTrace();return -1;
		 } finally {
			 try {in.close();} catch (IOException e) {e.printStackTrace();}
             try {dis.close();} catch (IOException e) {e.printStackTrace();}     
		 }
		
		 return addednumber;
	}
	/*
	 * 
	 */
	private void checkStockHangYeNewInfo(String stockcode, String stockoldTDXSWID, String stocknewTDXSWID)
	{
		//先把老记录 移除时间更新
//    		String sqlupdatestat = "UPDATE  股票通达信行业板块对应表  SET " 
//				+ " 移除时间 =" + "'" +  sysconfig.formatDate(new Date() ) + "'" + ","
//				+ " WHERE 股票代码 = " + "'" + stockcode.trim() + "'" 
//				+ " AND 对应TDXSWID=" + stockbkanameindb 
//				+ " AND isnull(移除时间)"
//				;
			
   			String sqlupdatestat = "UPDATE 股票通达信行业板块对应表 "
									+ "  SET 移除时间 = " + "'" +  LocalDate.now().toString()  + "'"
									+ "  WHERE  股票通达信行业板块对应表.`股票代码` = " + "'" + stockcode.trim() + "'"
									+ "  AND isnull(移除时间)"
									+ "  AND 股票通达信行业板块对应表.`对应TDXSWID` = " + "'" + stockoldTDXSWID + "'"
						;
	 		logger.debug(sqlupdatestat);
	 		@SuppressWarnings("unused")
			int autoIncKeyFromApi;
			try {
				autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
			} catch (MysqlDataTruncation e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 		
	 		//再增加一条记录
//		 	String sqlinsertstat = "INSERT INTO  股票通达信行业板块对应表(股票代码,行业板块,对应TDXSWID,股票权重) values ("
//					+ "'" + stockcode.trim() + "'" + ","
//					+ "'" + stockbkname.trim() + "'"  + ","
//					+ "'" + stockbkcode.trim() + "'" + ","
//					+ 10
//					+ ")"
//					;
	 		String sqlinsertstat =  "insert into 股票通达信行业板块对应表(股票代码,板块代码,对应TDXSWID,股票权重)"
			+ " SELECT '" + stockcode.trim() + "', 通达信板块列表.`板块ID`,'" + stocknewTDXSWID + "',10 "
			+ " FROM 通达信板块列表  where 通达信板块列表.`对应TDXSWID` = '" + stocknewTDXSWID + "'"
			;
			logger.debug(sqlinsertstat);
			try {
				autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
			} catch (MysqlDataTruncation e) {e.printStackTrace();
			} catch (SQLException e) {e.printStackTrace();}
		
	}
	
	/*
	 * 通达信的风格与个股代码对应文件
	 */
	private void refreshTDXFengGeBanKuaiToGeGu (File tmprecordfile)
	{
		BanKuaiAndStockTree treeofbkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		
		File file = new File(sysconfig.getTDXFengGeBanKuaiToGeGuFile() );
		 if(!file.exists() ) {	 logger.debug("File not exist");	return;	 }
		 
		 FileInputStream in = null;  
		 BufferedInputStream dis = null;
		 try {
			    in = new FileInputStream(file);  
			    dis = new BufferedInputStream(in);
               
           // 股票板块信息文件头格式，T0002/hq_cache/block.dat block_fg.dat block_gn.dat block_zs.dat
		   // 文件头：384字节
//               struct TTDXBlockHeader
//               {
//                   char         szVersion[64];        // 0, Registry ver:1.0 (1999-9-28)
//                   int            nIndexOffset;            // 64, 0x00000054
//                   int            nDataOffset;            // 68, 0x00000180
//                   int            nData1;                // 72, 0x00000003
//                   int            nData2;                // 76, 0x00000000
//                   int            nData3;                // 80, 0x00000003
//               };
               int fileheadbytenumber = 384;
               byte[] itemBuf = new byte[fileheadbytenumber];
               dis.read(itemBuf, 0, fileheadbytenumber); 
               String fileHead =new String(itemBuf,0,fileheadbytenumber);  
               
               //板块个数：2字节
               dis.read(itemBuf, 0, 2); 
               String bknumber =new String(itemBuf,0,2);  
			 
               //各板块数据存储结构（紧跟板块数目依次存放）
//               第一个版块的起始位置为0x182h。
//               每个板块占据的存储空间为2813个字节，每个板块最多包括400只股票。(2813 -9 - 2 - 2) / 7 =  400
//               板块名称：9字节
//               该板块包含的个股个数：2字节
//               板块类别：2字节
//               该板块下个股代码列表（连续存放，直到代码为空）
//                   个股代码：7字节
//               struct TTDXBlockRecord
//               {
//                   char         szName[9];
//                   short        nCount;
//                   short        nLevel;
//                   char         szCode[400][7];
//               };
               int sigbk = 2813;
               byte[] itemBuf2 = new byte[sigbk];
               int i=0;
               Files.append("开始导入通达信股票风格对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   //板块名称
            	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
            	   
            	   //比如 融资融券， 是个板块，但没有ID，在通达信板块列表里面没有，要先判断这种情形，以便用不同的SQL
                   boolean hasbkcode = true;
                   CachedRowSetImpl rspd = null; 
	   			   try {
	   			    	String sqlquerystat = "SELECT *  FROM 通达信板块列表 WHERE 板块名称='"  + gupiaoheader + "'";
	   			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			    	String bkcode = null;
	   			        while(rspd.next())  
	   			        	bkcode = rspd.getString("板块ID");

	   			        if(bkcode == null)	hasbkcode = false;
	   			        else {
	   			        	BkChanYeLianTreeNode bankuai = treeofbkstk.getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
		   			        if( bankuai != null && !((BanKuai)bankuai).getBanKuaiOperationSetting().isImportBKGeGu()   ) 
		            		   continue;
	   			        }
	   			    } catch(java.lang.NullPointerException e){ e.printStackTrace();
	   			    } catch (SQLException e) {e.printStackTrace();
	   			    } catch(Exception e){e.printStackTrace();
	   			    } finally {
	   			    	if(rspd != null) try {rspd.close();rspd = null; } catch (SQLException e) {e.printStackTrace();}
	   			    }
	   			   
            	   //板块个股
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   Set<String> tmpstockcodesetnew = new HashSet<>(tmpstockcodestr);
                   
                   //从数据库中读出当前该板块的个股
                   String sqlquerystat;
   				   if(hasbkcode)
   			    	sqlquerystat = "SELECT fgbk.股票代码  FROM 股票通达信风格板块对应表 fgbk, 通达信板块列表 bklb"
   			    						+ " WHERE  fgbk.板块代码 = bklb.`板块ID`"
   			    						+ " AND bklb.`板块名称` = '"  + gupiaoheader + "'"
   			    						+ " AND ISNULL(fgbk.移除时间)"
   			    						;
   				   else 
   					sqlquerystat = "SELECT 股票代码  FROM 股票通达信风格板块对应表 "
   									+ " WHERE 板块代码='" + gupiaoheader + "'"
   									+ " AND ISNULL(移除时间)"
   									;  
                   Set<String> tmpstockcodesetold = new HashSet<>();
   				   CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			   try {
	   			    	while(rs.next() ) {
	   			    		String stockcode = rs.getString("股票代码");
	   			        	tmpstockcodesetold.add(stockcode);
	   			    	}
	   			    } catch(java.lang.NullPointerException e){e.printStackTrace();
	   			    } catch (SQLException e) {e.printStackTrace();
	   			    } catch(Exception e){e.printStackTrace();
	   			    } finally {
	   			    	if(rs != null)
							try {rs.close();rs = null;
							} catch (SQLException e) {e.printStackTrace();}
	   			    }
		   		    
		   	        //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要在数据库中update移除的时间后才能增加新的
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
//		   	        	String sqldeletetstat = "DELETE  FROM 股票通达信风格板块对应表 "
//		   	        							+ " WHERE 股票代码=" +  "'" + str + "'"
//		   	        							+ " AND 风格板块=" + "'" + gupiaoheader + "'"
//		   	        							;
		   	        	String sqlupdatestat ;
		   	        	if(hasbkcode)
		   	        		sqlupdatestat = "UPDATE 股票通达信风格板块对应表 JOIN 通达信板块列表"
		   	        							+ "  ON  股票通达信风格板块对应表.`股票代码` = " + "'" + str.trim() + "'"
		   	        							+ "  AND isnull(移除时间)"
		   	        							+ "  and  股票通达信风格板块对应表.`板块代码` = 通达信板块列表.`板块ID` "
		   	        							+ "  and 通达信板块列表.`板块名称` = " + "'" + gupiaoheader + "'"
		   	        							+ "  SET 移除时间 = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
		   	        							;
		   	        	else
		   	        		sqlupdatestat = "UPDATE 股票通达信风格板块对应表 SET 移除时间 = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
		   	        						+" WHERE 板块代码=" + "'" + gupiaoheader + "'"
		   	        						+" AND 股票代码= " + "'" + str.trim() + "'"
		   	        						+ "  AND isnull(移除时间)"
		   	        						;
		   	        	logger.debug(sqlupdatestat);
		   	    		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
//		   	    		Files.append("删除："  + str.trim() + " " + gupiaoheader + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   	        }
		   	        differencebankuaiold = null;
		   	        
	   		        //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   				
		   		    	String sqlinsertstat ;
		   	        	if(hasbkcode)
			   				 sqlinsertstat = "insert into 股票通达信风格板块对应表(股票代码,板块代码,股票权重)"
			   									+ "	  SELECT '" + str + "', 通达信板块列表.`板块ID`, 10 "
			   									+ " FROM 通达信板块列表  where 通达信板块列表.`板块名称` = '" + gupiaoheader + "'"   
			   									;
		   	        	else
		   	        		sqlinsertstat = "insert into 股票通达信风格板块对应表(股票代码,板块代码,股票权重) VALUES "
		   	        						+ "('" + str + "','" +  gupiaoheader + "',10)"
		   	        						;
		   				logger.debug(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				
//		   				Files.append("加入："  + str.trim() + " " + gupiaoheader + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   		    differencebankuainew = null;
		   	        
//		   	        if(differencebankuainew.size() ==0 && differencebankuaiold.size()>0 ) { // 新的为0，旧的大于零，删除的时候现在实现机制数据库不更新最后更新时间，对更新XML有影响，所以强制更新一条信息
//		   	        	String sqlupdatestat = "UPDATE 通达信板块列表 SET 数据最新更新时间 = "
//		   	        			+  formateDateForDiffDatabase("mysql", sysconfig.formatDate( LocalDateTime.now() ) ) + ","
//		   	        			;
//		   	        	logger.debug(sqlupdatestat);
//		   				connectdb.sqlUpdateStatExecute (sqlupdatestat);
//		   	        }
               }
		 } catch (Exception e) {e.printStackTrace();
		 } finally {
             try { in.close();	} catch (IOException e) {e.printStackTrace();			}
             try {dis.close();} catch (IOException e) {e.printStackTrace();}
		 }
	
	}
	/*
	 * 通达信的行业与个股对应文件
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
	private int refreshTDXZhiShuBanKuaiToGeGu (File tmprecordfile)
	{
		File file = new File(sysconfig.getTDXZhiShuBanKuaiToGeGuFile() );
		if(!file.exists() ) 
			 return -1;
		 
		 int addednumber =0;FileInputStream in = null;BufferedInputStream dis = null;
		 try {
			    in = new FileInputStream(file);  
			    dis = new BufferedInputStream(in);
              
          // 股票板块信息文件头格式，T0002/hq_cache/block.dat block_fg.dat block_gn.dat block_zs.dat
		   // 文件头：384字节
//              struct TTDXBlockHeader
//              {
//                  char         szVersion[64];        // 0, Registry ver:1.0 (1999-9-28)
//                  int            nIndexOffset;            // 64, 0x00000054
//                  int            nDataOffset;            // 68, 0x00000180
//                  int            nData1;                // 72, 0x00000003
//                  int            nData2;                // 76, 0x00000000
//                  int            nData3;                // 80, 0x00000003
//              };
              int fileheadbytenumber = 384;
              byte[] itemBuf = new byte[fileheadbytenumber];
              dis.read(itemBuf, 0, fileheadbytenumber); 
              String fileHead =new String(itemBuf,0,fileheadbytenumber);  
             
              //板块个数：2字节
              dis.read(itemBuf, 0, 2); 
              String bknumber =new String(itemBuf,0,2);  
			 
              //各板块数据存储结构（紧跟板块数目依次存放）
//              第一个版块的起始位置为0x182h。
//              每个板块占据的存储空间为2813个字节，每个板块最多包括400只股票。(2813 -9 - 2 - 2) / 7 =  400
//              板块名称：9字节
//              该板块包含的个股个数：2字节
//              板块类别：2字节
//              该板块下个股代码列表（连续存放，直到代码为空）
//                  个股代码：7字节
//              struct TTDXBlockRecord
//              {
//                  char         szName[9];
//                  short        nCount;
//                  short        nLevel;
//                  char         szCode[400][7];
//              };
              int sigbk = 2813;
              byte[] itemBuf2 = new byte[sigbk];
              int i=0;
              Files.append("开始导入通达信股票指数板块对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
              while (dis.read(itemBuf2, 0, sigbk) != -1) {
	           	   //板块名称
	           	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
	           	   CachedRowSetImpl rspd = null; 
				   try {
				    	String sqlquerystat = "SELECT *  FROM 通达信板块列表 WHERE 板块名称='"  + gupiaoheader + "'";
				    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
				    	
				    	String bkcode = null;
				        while(rspd.next())  
				        	bkcode = rspd.getString("板块ID");
				        
				        BkChanYeLianTreeNode bankuai = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
		   			    if( bankuai != null && !((BanKuai)bankuai).getBanKuaiOperationSetting().isImportBKGeGu()   ) 
		            		   continue;
				    } catch(java.lang.NullPointerException e){ e.printStackTrace();
				    } catch (SQLException e) {e.printStackTrace();
				    } catch(Exception e){e.printStackTrace();
				    } finally {
				    	if(rspd != null) try {rspd.close();rspd = null; } catch (SQLException e) {e.printStackTrace();}
				    }
           	   
           	   //板块个股
                  String gupiaolist =new String(itemBuf2,12,sigbk-12);
                 
                  List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                  Set<String> tmpstockcodesetnew = new HashSet<>(tmpstockcodestr);
                  //从数据库中读出当前该板块的个股
                  Set<String> tmpstockcodesetold = new HashSet<>();
                  String sqlquerystat = "SELECT zsbk.股票代码  FROM 股票通达信交易所指数对应表  zsbk, 通达信板块列表 zslb"
  						+ " WHERE  zsbk.板块代码 = zslb.`板块ID`"
  						+ " AND zslb.`板块名称` = '"  + gupiaoheader + "'"
  						+ " AND ISNULL(zsbk.移除时间)"
  						;
                  CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    try {
	   			    	
	   			    	while(rs.next()) {  
	   			        	String stockcode = rs.getString("股票代码");
	   			        	tmpstockcodesetold.add(stockcode);
	   			        }
	   			    }catch(java.lang.NullPointerException e){e.printStackTrace();
	   			    } catch (SQLException e) {e.printStackTrace();
	   			    }catch(Exception e){e.printStackTrace();
	   			    } finally {
	   			    	if(rs != null)	try {rs.close();rs = null;} catch (SQLException e) {e.printStackTrace();}
	   			    }
	   			    
	   			    //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要从数据库中更新时间
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {

		   	        	String sqlupdatestat = "UPDATE 股票通达信交易所指数对应表  JOIN 通达信板块列表"
       							+ "  ON  股票通达信交易所指数对应表.`股票代码` = " + "'" + str.trim() + "'"
       							+ "  AND isnull(移除时间)"
       							+ "  and  股票通达信交易所指数对应表.`板块代码` = 通达信板块列表.`板块ID` "
       							+ "  and 通达信板块列表.`板块名称` = " + "'" + gupiaoheader + "'"
       							+ "  SET 移除时间 = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
       							;	
				   		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   	        }
		   	     differencebankuaiold = null;
		   	     
	   		        //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   		    	String sqlinsertstat = "insert into 股票通达信交易所指数对应表(股票代码,板块代码,股票权重)"
								+ "	  SELECT '" + str.trim() + "', 通达信板块列表.`板块ID`, 10 "
								+ " FROM 通达信板块列表  where 通达信板块列表.`板块名称` = '" + gupiaoheader + "'"   
								;
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				addednumber ++;
		                Files.append("加入：" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   		 differencebankuainew = null;
              }
		 } catch (Exception e) {e.printStackTrace();return -1;
		 } finally {
			 try {in.close();} catch (IOException e1) {e1.printStackTrace();}
             try {dis.close();} catch (IOException e) {e.printStackTrace();}
		 }
		
		 return addednumber;
	}
	/*
	 * 从通达信更新所有的交易所指数，上海part
	 */
	private int refreshTDXZhiShuShangHaiLists(File tmprecordfile)
	{
		//先从数据库中读取现有所有的上海指数
		Collection<BkChanYeLianTreeNode> requiredbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK,"sh");
		Set<String> curallshbk = new HashSet<> ();
		for(BkChanYeLianTreeNode tmpnode : requiredbk)
			curallshbk.add(tmpnode.getMyOwnCode());

		File file = new File(sysconfig.getTDXShangHaiZhiShuNameFile() );
		if(!file.exists() ) return -1;
		
		 int addednumber =0;
		 Set<String> allinsertbk = new HashSet<> (); //所有最新的指数，用来和curallshbk，以判断是否有旧的指数需要被删除
		 BufferedInputStream dis = null;  FileInputStream in = null;
		 try {
			    in = new FileInputStream(file);  
			    dis = new BufferedInputStream(in);
               
               int fileheadbytenumber = 50;
               byte[] itemBuf = new byte[fileheadbytenumber];
               dis.read(itemBuf, 0, fileheadbytenumber); 
               String fileHead =new String(itemBuf,0,fileheadbytenumber);  
               
               int sigbk = 18*16+12+14;
               byte[] itemBuf2 = new byte[sigbk];
               int i=0;
               Files.append("开始导入通达信股票指数板块对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   String zhishuline =new String(itemBuf2,0,sigbk);
            	   String zhishucode = zhishuline.trim().substring(0, 6);

            	   if( ! sysconfig.isShangHaiZhiShu(zhishucode))   continue;
            	   
            	   List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(zhishuline.substring(6, zhishuline.length()));
            	   String zhishuname = null;
            	   try {	zhishuname = tmplinelist.get(0).trim().substring(0, 6).trim();
            	   } catch (java.lang.StringIndexOutOfBoundsException ex) {
            		   List<String> tmplinepartnamelist = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(1).trim());
            		   zhishuname = tmplinelist.get(0).trim() + tmplinepartnamelist.get(0).trim();
            	   }

            	   String sqlinsertstat = "INSERT INTO  通达信板块列表(板块名称,板块ID,指数所属交易所) values ("
	   						+ " '" + zhishuname.trim() + "'" + ","
	   						+ " '" + zhishucode.trim() + "'" + ","
	   						+ " '" + "sh" + "'"
	   						+ ")"
	   						+ " ON DUPLICATE KEY UPDATE "
	   						+ "板块名称 =" + " '" + zhishuname.trim() + "'" + ","
	   						+ "指数所属交易所= " + " '" + "sh" + "'"
	   						;
	   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//	                Files.append("加入：" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	   				allinsertbk.add(zhishucode);
               }
               
		 } catch (Exception e) { e.printStackTrace();	 return -1;
		 } finally {
			 try { in.close();	} catch (IOException e) {e.printStackTrace();}
             try { dis.close(); } catch (IOException e) {e.printStackTrace();}     
		 }
		 
		 SetView<String> differencebankuainew = Sets.difference(allinsertbk, curallshbk  );//新的板块要加到树上，否则对后面导入板块交易数据有影响
		 for(String newbkcode : differencebankuainew) {
			 BanKuai newbk = new BanKuai(newbkcode,"");
			 newbk.getNodeJiBenMian().setSuoShuJiaoYiSuo("SH");
			 BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot();
			 treeroot.add(newbk);
		 }
		 
//		 SetView<String> differencebankuaiold = Sets.difference( curallshbk, allinsertbk );
//		 if(differencebankuaiold.size() >0 ) { //说明有某些板块需要删除
//			 for(String oldbkcode : differencebankuaiold) {
//				 String sqldeletetstat = "DELETE  FROM  通达信板块列表 WHERE "
//						 				+ " 板块ID =" + " '" + oldbkcode.trim() + "'" 
//						 				;
//	   			try {
//					int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e) {e.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();				}
//	   			
//	   		//还要删除该板块在 股票概念对应表/股票行业对应表/股票风格对应表/产业链子版块列表  中的股票和板块的对应信息
//	    		sqldeletetstat = "DELETE  FROM  股票通达信概念板块对应表"
//						+ " WHERE 板块代码=" + "'"  + oldbkcode.trim() + "'"
//						;
//				try {
//					int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e3) {e3.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();}
//				
//				sqldeletetstat = "DELETE  FROM  股票通达信行业板块对应表"
//						+ " WHERE 板块代码=" + "'"  + oldbkcode.trim() + "'"
//						;
//				try {
//					int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e2) {e2.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();}
//				
//				sqldeletetstat = "DELETE  FROM 股票通达信风格板块对应表"
//						+ " WHERE 板块代码=" + "'"  + oldbkcode.trim() + "'"
//						;
//				try {
//					int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e1) {e1.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();	}
//
//			 }
//		 }
//		 differencebankuaiold = null;
		 curallshbk = null;
		return 1;
	}
	/*
	 * 从通达信更新所有的交易所指数，深圳part
	 */
	private int refreshTDXZhiShuShenZhenLists(File tmprecordfile)
	{
		//先从数据库中读取现有所有的上海指数
		Collection<BkChanYeLianTreeNode> requiredbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK,"sz");
		Set<String> curallszbk = new HashSet<> ();
		for(BkChanYeLianTreeNode tmpnode : requiredbk)
			curallszbk.add(tmpnode.getMyOwnCode());
			
		File file = new File(sysconfig.getTDXShenZhenShuNameFile() );
		if(!file.exists() )  return -1;

		int addednumber =0;
		Set<String> allinsertbk = new HashSet (); //所有最新的指数，用来和curallshbk，以判断是否有旧的指数需要被删除
		 BufferedInputStream dis = null; FileInputStream in = null;
		 try {
			    in = new FileInputStream(file);  
			    dis = new BufferedInputStream(in);
               
               int fileheadbytenumber = 50;
               byte[] itemBuf = new byte[fileheadbytenumber];
               dis.read(itemBuf, 0, fileheadbytenumber); 
               String fileHead =new String(itemBuf,0,fileheadbytenumber);  
               logger.debug(fileHead);
               
               int sigbk = 18*16+12+14;
               byte[] itemBuf2 = new byte[sigbk];
               int i=0;
               Files.append("开始导入通达信股票指数板块对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   String zhishuline =new String(itemBuf2,0,sigbk);
            	   logger.debug(zhishuline);
            	   String zhishucode = zhishuline.trim().substring(0, 6);

            	   if(! sysconfig.isShenZhengZhiShu(zhishucode))		   continue;
            	   
            	   List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(zhishuline.substring(6, zhishuline.length()));
            	   String zhishuname = null;
            	   try {   zhishuname = tmplinelist.get(0).trim().substring(0, 6).trim();
            	   } catch (java.lang.StringIndexOutOfBoundsException ex) {
            		   List<String> tmplinepartnamelist = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(1).trim());
            		   zhishuname = tmplinelist.get(0).trim() + tmplinepartnamelist.get(0).trim();
            	   }

            	   String sqlinsertstat = "INSERT INTO  通达信板块列表(板块名称,板块ID,指数所属交易所) values ("
	   						+ " '" + zhishuname.trim() + "'" + ","
	   						+ " '" + zhishucode.trim() + "'" + ","
	   						+ " '" + "sz" + "'"
	   						+ ")"
	   						+ " ON DUPLICATE KEY UPDATE "
	   						+ "板块名称 =" + " '" + zhishuname.trim() + "'" + ","
	   						+ "指数所属交易所= " + " '" + "sz" + "'" 
	   						;
            	   logger.debug(sqlinsertstat);
	   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
	   				allinsertbk.add(zhishucode);
               }
		 } catch (Exception e) {e.printStackTrace();return -1;
		 } finally {
			 try {in.close();} catch (IOException e) {e.printStackTrace();}
             try {dis.close();} catch (IOException e) {e.printStackTrace();}     
		 }
		 
		 SetView<String> differencebankuainew = Sets.difference(allinsertbk, curallszbk);//新的板块要加到树上，否则对后面导入板块交易数据有影响  //
		 for(String newbkcode : differencebankuainew) {
			 BanKuai newbk = new BanKuai(newbkcode,"");
			 newbk.getNodeJiBenMian().setSuoShuJiaoYiSuo("SZ");
			 BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot();
			 treeroot.add(newbk);
		 }
		 
//		 SetView<String> differencebankuaiold = Sets.difference(curallszbk, allinsertbk );
//		 if(differencebankuaiold.size() >0 ) { //说明有某些板块需要删除
//			 for(String oldbkcode : differencebankuaiold) {
//				 String sqldeletetstat = "DELETE  FROM  通达信板块列表 WHERE "
//						 				+ " 板块ID =" + " '" + oldbkcode.trim() + "'" 
//						 				;
//	   			int autoIncKeyFromApi;
//				try {
//					autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e4) {e4.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();}
//	   			
//	   			//还要删除该板块在 股票概念对应表/股票行业对应表/股票风格对应表/产业链子版块列表  中的股票和板块的对应信息
//	    		sqldeletetstat = "DELETE  FROM  股票通达信概念板块对应表"
//						+ " WHERE 板块代码=" + "'"  + oldbkcode.trim() + "'"
//						;
//				try {
//					autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e3) {e3.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();}
//				
//				sqldeletetstat = "DELETE  FROM  股票通达信行业板块对应表"
//						+ " WHERE 板块代码=" + "'"  + oldbkcode.trim() + "'"
//						;
//				try {
//					autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e2) {e2.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();}
//				
//				sqldeletetstat = "DELETE  FROM 股票通达信风格板块对应表"
//						+ " WHERE 板块代码=" + "'"  + oldbkcode.trim() + "'"
//						;
//				try {
//					autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e1) {e1.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();	}
//			 }
//		 }
//		 differencebankuaiold = null;
		 curallszbk = null;
		 return -1;
	}
	/*
	 * 所有股票
	 */
	public ArrayList<Stock> getAllStocks() 
	{
		ArrayList<Stock> tmpsysbankuailiebiaoinfo = new ArrayList<Stock> ();

		String sqlquerystat = "SELECT * FROM \r\n" + 
				"(\r\n" + 
				"SELECT 股票代码,股票名称,已退市,所属交易所,股票通达信基本面信息对应表.`上市日期SSDATE` \r\n" + 
				"FROM A股 \r\n" + 
				"LEFT JOIN 股票通达信基本面信息对应表 \r\n" + 
				"ON 股票通达信基本面信息对应表.`股票代码GPDM` = a股.`股票代码`\r\n" + 
				") node\r\n" + 
				"LEFT JOIN 板块股票占比阈值\r\n" + 
				"ON node.股票代码 = 板块股票占比阈值.`代码`"									
				;   
		logger.debug(sqlquerystat);
		CachedRowSetImpl rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);

	        while(rs.next()) {
	        	Stock tmpbk = null;
	        	if(!rs.getBoolean("已退市")) {
	        		String nodecode = rs.getString("股票代码");
	        		String nodename = rs.getString("股票名称");
	        		tmpbk = new Stock (nodecode,nodename);
	        		tmpbk.getNodeJiBenMian().setSuoShuJiaoYiSuo(rs.getString("所属交易所"));
	        		try{
	        			LocalDate shangshiriqi = rs.getDate("上市日期SSDATE").toLocalDate();
	        			if(!shangshiriqi.equals(LocalDate.parse("1992-01-01"))) //通达信把所有暂时没有上市交易的股票上市日期都定义为1992-0101
	        				tmpbk.getNodeJiBenMian().setShangShiRiQi( shangshiriqi );
	        		} catch (java.lang.NullPointerException e) {}
	        		tmpbk.getNodeJiBenMian().setNodeCjeZhanbiLevel(rs.getDouble("成交额占比下限"), rs.getDouble("成交额占比上限"));
	        		tmpbk.getNodeJiBenMian().setNodeCjlZhanbiLevel(rs.getDouble("成交量占比下限"), rs.getDouble("成交量占比上限"));
	        		if(nodename != null && nodename.toUpperCase().contains("ST"))
	        			tmpbk.getShuJuJiLuInfo().setHasReviewedToday(true);
		        	tmpsysbankuailiebiaoinfo.add(tmpbk);
	        	} else { 		logger.debug(rs.getString("股票代码") + "已经退市");
	        	}
	        	
	        }
	    }catch(java.lang.NullPointerException e){ e.printStackTrace();
	    } catch (SQLException e) {e.printStackTrace();
	    }catch(Exception e){e.printStackTrace();
	    } finally {
	    	if(rs != null)	try {rs.close();rs = null;} catch (SQLException e) {e.printStackTrace();}
	    } 
	    
	    return tmpsysbankuailiebiaoinfo;
	}
	
	/*
	 * 检查数据库中的通达信板块哪些有记录文件，哪些没有。 
	 */
	public File preCheckTDXBanKuaiVolAmoToDb (String jys)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步通达信板上海块成交量预检查.tmp");
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
//		List<String> volamooutput = sysconfig.getTDXVOLFilesPath();
//		String exportath = sysconfig.toUNIXpath(Splitter.on('=').trimResults().omitEmptyStrings().splitToList(volamooutput.get(0) ).get(1) );
//		String filenamerule = Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(1)).get(1);
//		boolean header;
//		if(volamooutput.get(4).endsWith("1"))
//			header = true;
//		else header = false;
		
		Collection<BkChanYeLianTreeNode> requiredbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK,jys);
		List<String> allbkcode = new ArrayList<String>( );
		for(BkChanYeLianTreeNode tmpnode : requiredbk)
			allbkcode.add(tmpnode.getMyOwnCode());
		
		int found =0 ; int notfound = 0;
		try {
			Files.append("系统共有"+ allbkcode.size() + "个板块！"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		
			for(String tmpbkcode:allbkcode) {
//				String bkcys = allsysbk.get(tmpbkcode).getSuoShuJiaoYiSuo().trim().toUpperCase();
				String bkfilename = (filenamerule.replaceAll("YY",jys.toUpperCase())).replaceAll("XXXXXX", tmpbkcode);
				
				File tmpbkfile = new File(exportath + "/" + bkfilename);
					
					if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {
						Files.append(tmpbkcode + "未找到记录文件！"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
						notfound ++;
					}
					 else {
						 Files.append(tmpbkcode + "有记录文件！"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
						 found ++;
					 }
			}
			
			Files.append(found + "个板块有记录文件！"+ notfound + "个板块没有记录文件！" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		allbkcode = null;
		return tmprecordfile;
	}
	/*
	 * 在用户导入成交量后，要更新板块的类型，// 通达信里面定义的板块有几种：1.有个股自身有成交量数据 2. 有个股自身无成交量数据 3.无个股自身有成交量数据
	 * 每周做一次就可以，否则太耗时间
	 */
	public void refreshTDXSystemBanKuaiLeiXing () 
	{
		Collection<BkChanYeLianTreeNode> curallbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getAllRequiredNodes(BkChanYeLianTreeNode.TDXBK);
		
		this.getTDXBanKuaiShuJuJiLuMaxMinDateByJiaoYiSuo ("SH");
		this.getTDXBanKuaiShuJuJiLuMaxMinDateByJiaoYiSuo ("SZ");
		
		this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SH");
	    this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SZ");
	
		for ( BkChanYeLianTreeNode tmpnode : curallbk) {
			BanKuai bankuai = (BanKuai)tmpnode;
			String leixing = bankuai.getBanKuaiLeiXing();
		    if(leixing != null && leixing.equals(BanKuai.HASGGWITHSELFCJL) )  //最完整的板块类型，不用在更新了
		    	continue;
		    
		    Boolean bkhacjl = false;
		    if(bankuai.getShuJuJiLuInfo().getJyjlmaxdate()  != null)
		    	bkhacjl = true;
		    
			boolean bkhasgegu = false;
			if(bankuai.getShuJuJiLuInfo().getGuPiaoBanKuaiDuiYingBiao() != null)
		    	bkhasgegu  = true;

			String  bktype = null ;
			if(bkhacjl && bkhasgegu )
				bktype = BanKuai.HASGGWITHSELFCJL;
			else if(bkhacjl && !bkhasgegu)
				bktype = BanKuai.NOGGWITHSELFCJL;
			else if(!bkhacjl && bkhasgegu)
				bktype = BanKuai.HASGGNOSELFCJL;
			else
				bktype = BanKuai.NOGGNOSELFCJL;
			
			String bkcode = tmpnode.getMyOwnCode();
			if(bktype != null) {
				String sqlupdatequery = "UPDATE 通达信板块列表 SET 板块类型描述 = " + "'" + bktype +"'" + " WHERE 板块ID = '" + bkcode + "'";
			    try {
					connectdb.sqlUpdateStatExecute(sqlupdatequery);
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();
				}
			}
//			if(bktype.equals(BanKuai.NOGGWITHSELFCJL)  ||  bktype.equals(BanKuai.NOGGNOSELFCJL)) { //没有个股的板块肯定不导出到gephi
//				String sqlupdatequery = "UPDATE 通达信板块列表 SET 导出Gephi = false WHERE 板块ID = '" + bkcode + "'";
//				logger.debug(sqlupdatequery);
//			    try {
//					connectdb.sqlUpdateStatExecute(sqlupdatequery);
//				} catch (MysqlDataTruncation e) {e.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();
//				}
//			}
		}
		
		curallbk = null;
	}
	/*
	 * 
	 */
	public void getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo (String jiaoyisuo)
	{
		Collection<BkChanYeLianTreeNode> bkjys = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK, jiaoyisuo);
		String bkcodes = "";
		for(BkChanYeLianTreeNode tmpbk : bkjys)
			bkcodes = bkcodes + tmpbk.getMyOwnCode() + ",";
		bkcodes = bkcodes.substring(0, bkcodes.length()-2);
		
		String sqlquerystat1 =  
				" SELECT 板块代码, COUNT(*) AS RESULT , '股票通达信概念板块对应表'  AS DYB FROM 股票通达信概念板块对应表\r\n" + 
				" WHERE  板块代码 IN (" +  bkcodes + ")\r\n" + 
				" GROUP BY 板块代码  \r\n"  
//				" + UNION \r\n" 
				; 
		String sqlquerystat2 =		" SELECT 板块代码, COUNT(*) AS RESULT ,'股票通达信风格板块对应表' AS DYB FROM 股票通达信风格板块对应表\r\n" + 
				" WHERE  板块代码 IN (" +  bkcodes + ")\r\n" + 
				" GROUP BY 板块代码  \r\n"  
//				"+  UNION \r\n" 
				; 
				
		String sqlquerystat3 =			" SELECT 板块代码, COUNT(*) AS RESULT , '股票通达信行业板块对应表'   AS DYB FROM  股票通达信行业板块对应表\r\n" + 
				" WHERE  板块代码 IN (" +  bkcodes + ")\r\n" + 
				" GROUP BY 板块代码  \r\n"  
//				" + UNION \r\n" 
				; 
		String sqlquerystat4 =			"  SELECT 板块代码, COUNT(*) AS RESULT , '股票通达信交易所指数对应表'  AS DYB FROM 股票通达信交易所指数对应表\r\n" + 
				" WHERE  板块代码 IN (" +  bkcodes + ") \r\n" +  
				" GROUP BY 板块代码"
				;
		String sqlquerystat = sqlquerystat1 + sqlquerystat2 + sqlquerystat3 + sqlquerystat4;
		setTDXBanKuaiGeGuDuiYingBiao (sqlquerystat1);
		setTDXBanKuaiGeGuDuiYingBiao (sqlquerystat2);
		setTDXBanKuaiGeGuDuiYingBiao (sqlquerystat3);
		setTDXBanKuaiGeGuDuiYingBiao (sqlquerystat4);
		
	}
	/*
	 * 
	 */
	private void setTDXBanKuaiGeGuDuiYingBiao (String sqlquerystat1)
	{
		CachedRowSetImpl rsdm = connectdb.sqlQueryStatExecute(sqlquerystat1);
		try {
			while(rsdm.next()) {
	    		 String bkcode = rsdm.getString("板块代码"); //mOST_RECENT_TIME
	    		 BanKuai bk = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
	    		 if(bk == null) 
	    			 continue;
	    		 try { String dyb = rsdm.getString("DYB");
	    		 		bk.getShuJuJiLuInfo().setGuPiaoBanKuaiDuiYingBiao(dyb);
	    		 } catch (java.lang.NullPointerException ex) {}
	    	}
		} catch(java.lang.NullPointerException e) {	e.printStackTrace();
	    } catch (SQLException e) {	e.printStackTrace();
	    } catch(Exception e){	e.printStackTrace();
	    } finally {
	    	if(rsdm != null)
			try {rsdm.close();rsdm = null;
			} catch (SQLException e) {	e.printStackTrace();}
	    }
		
		return;
	}
	/*
	 * 
	 */
	public void getTDXBanKuaiShuJuJiLuMaxMinDateByJiaoYiSuo (String jiaoyisuo)
	{
		String cjltablename;
		if(jiaoyisuo.toLowerCase().equals("sh"))			cjltablename = "通达信板块每日交易信息";
		else			cjltablename = "通达信交易所指数每日交易信息";
		
		String sqlquerystat = "SELECT * FROM \r\n" + 
				"(SELECT 板块ID  FROM 通达信板块列表\r\n" + 
				"WHERE 指数所属交易所 = '" + jiaoyisuo + "' ) agu\r\n" + 
				"\r\n" + 
				"LEFT JOIN \r\n" + 
				"( SELECT 代码, MIN(交易日期)  minjytime , MAX(交易日期)  maxjytime FROM " + cjltablename + "\r\n" + 
				"  GROUP BY 代码) shjyt\r\n" + 
				" ON agu.板块ID =  shjyt.`代码`\r\n" + 
				""
				;
		CachedRowSetImpl rsdm = connectdb.sqlQueryStatExecute(sqlquerystat);
		try { 	
	    	while(rsdm.next()) {
		    		 String bkcode = rsdm.getString("代码"); //mOST_RECENT_TIME
		    		 BanKuai bk = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
		    		 if(bk == null) 
		    			 continue;
		    		 try { LocalDate mintime = rsdm.getDate("minjytime").toLocalDate();
		    		 		bk.getShuJuJiLuInfo().setJyjlmindate(mintime);
		    		 } catch (java.lang.NullPointerException ex) {}
		    		 try { LocalDate maxtime = rsdm.getDate("maxjytime").toLocalDate();
		    		 		bk.getShuJuJiLuInfo().setJyjlmaxdate(maxtime);
		    		 } catch (java.lang.NullPointerException ex) {}
		    	}
		    } catch(java.lang.NullPointerException e) {	e.printStackTrace();
		    } catch (SQLException e) {	e.printStackTrace();
		    } catch(Exception e){	e.printStackTrace();
		    } finally {
		    	if(rsdm != null)
				try {rsdm.close();rsdm = null;
				} catch (SQLException e) {	e.printStackTrace();}
		    }
		
		return;
	}
	/*
	 * 同步通达信上证板块的每日成交量信息，信息存在"通达信板块每日交易信息"，
	 */
	public File refreshTDXBanKuaiVolAmoToDb (String jiaoyisuo)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步板块成交量报告.tmp");
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		getTDXBanKuaiShuJuJiLuMaxMinDateByJiaoYiSuo (jiaoyisuo);
		
		String cjltablename;
		if(jiaoyisuo.toLowerCase().equals("sh"))			cjltablename = "通达信板块每日交易信息";
		else			cjltablename = "通达信交易所指数每日交易信息";
		
		 Collection<BkChanYeLianTreeNode> requiredbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK,jiaoyisuo);
		 for(BkChanYeLianTreeNode tmpnode :  requiredbk ) {
			BanKuai bk = (BanKuai) tmpnode;
			if(!bk.getBanKuaiOperationSetting().isImportdailytradingdata())				continue;
			
			String tmpbkcode = bk.getMyOwnCode();
			File tmpbkfile = null; String bkfilename = null;
			if(bk.getMyOwnCode().equalsIgnoreCase("000852"))  //通达信不知道为什么，中证1000的每日交易数据输出文件不一样
				 bkfilename = "62000852.TXT";
			else 
				bkfilename = (filenamerule.replaceAll("YY",jiaoyisuo.toUpperCase())).replaceAll("XXXXXX", tmpbkcode);
			tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead())   
			    continue; 

			LocalDate ldlastestdbrecordsdate = bk.getShuJuJiLuInfo().getJyjlmaxdate();
//			CachedRowSetImpl  rs = null;
//				try { 				
//					String sqlquerystat = "SELECT  MAX(交易日期) 	MOST_RECENT_TIME"
//							+ " FROM " + cjltablename + "  WHERE  代码 = " 
//   							+ "'"  + tmpbkcode + "'" 
//   							;
//   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//   			    	while(rs.next()) {
//   			    		java.sql.Date lastestdbrecordsdate = rs.getDate("MOST_RECENT_TIME"); //mOST_RECENT_TIME
//   			    		try {
//   			    			ldlastestdbrecordsdate = lastestdbrecordsdate.toLocalDate();
//   			    		} catch (java.lang.NullPointerException e) {	logger.debug(tmpbkcode + "没有记录");	}
//   			    	}
//   			    } catch(java.lang.NullPointerException e) { e.printStackTrace();
//   			    } catch (SQLException e) {e.printStackTrace();
//   			    } catch(Exception e){e.printStackTrace();
//   			    } finally {
//   			    	if(rs != null)
//						try { rs.close();rs = null;
//						} catch (SQLException e) {e.printStackTrace();}
//   			    }
				
				setVolAmoRecordsFromFileToDatabase(tmpnode,tmpbkfile,ldlastestdbrecordsdate,cjltablename,dateRule,tmprecordfile);
		}
		
		return tmprecordfile;
	}
	/*
	 * 
	 */
	private List<String> getTDXVolFilesRule ()
	{
		List<String> volamooutput = sysconfig.getTDXVOLFilesPath(); 
		try {
			String exportath = sysconfig.toUNIXpath(Splitter.on('=').trimResults().omitEmptyStrings().splitToList(volamooutput.get(0) ).get(1) );
			String filenamerule = Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(1)).get(1);
			String dateRule = getTDXDateExportDateRule(Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(2)).get(1));
			
			List<String> tdxVolFileRule = new ArrayList<String> ();
			tdxVolFileRule.add(exportath);
			tdxVolFileRule.add(filenamerule);
			tdxVolFileRule.add(dateRule);
			
			return tdxVolFileRule;
		} catch (java.lang.IndexOutOfBoundsException e) {
			return null;
		}
	}
	private String getTDXDateExportDateRule(String ruleindex) 
	{
		String value = null;
		switch (ruleindex.trim()) {
		case "0": 	value = "MM/dd/yyyy";
			break;
		case "1": 	value = "yyyy/MM/dd";
			break;
		case "2":	value = "dd/MM/yyyy";
			break;
		case "3":	value = "MM-dd-yyyy";
			break;
		case "4":	value = "yyyy-MM-dd";
			break;
		case "5":	value = "dd-MM-yyyy";
			break;
		case "6":	value = "MMddyyyy";
			break;
		case "7":	value = "yyyyMMdd";
			break;
		case "8":	value = "ddMMyyyy";
			break;
		}
		
		return value;
	}
	/*
	 * 
	 */
	public Boolean setVolAmoRecordsFromTDXExportFileToDatabase (TDXNodes node, LocalDate requiredstartdate, LocalDate requiredenddate, File tmplogfile)
	{
		String inserttablename = null;
		if(node.getType() == BkChanYeLianTreeNode.TDXBK ) 
			inserttablename = this.getBanKuaiJiaoYiLiangBiaoName( (BanKuai)node);
		else if(node.getType() == BkChanYeLianTreeNode.TDXGG ) 
			inserttablename = this.getStockJiaoYiLiangBiaoName(  (Stock)node );
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		File exportfile = this.getTDXNodesTDXSystemExportFile(node);
		
		RandomAccessFile rf = null;
        Boolean newdataimported;
		try {  
            rf = new RandomAccessFile(exportfile, "r");  
            long len = rf.length();  
            long start = rf.getFilePointer();  
            long nextend = start + len - 1;  
            String line;  
            rf.seek(nextend);  
            int c = -1;  
            boolean finalneededsavelinefound = false; //标记需要导入的日期的起始行
            int lineimportcount =0;
            while (nextend > start && finalneededsavelinefound == false) {
                c = rf.read();
            	if (c == '\n' || c == '\r') {  
                    line = rf.readLine();  
                    if (line != null) {  
                        @SuppressWarnings("deprecation")
						List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);

                        if( tmplinelist.size() ==7 && !tmplinelist.get(5).equals("0")) { //有可能是半天数据，有0，不完整，不能录入,对个股来说，半天数据也有，要特别处理
                        	LocalDate curlinedate = null;
                    		try {
                    			String beforparsedate = tmplinelist.get(0);
                    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateRule);
                    			curlinedate =  LocalDate.parse(beforparsedate,formatter) ;
                    			if( curlinedate.isAfter(requiredstartdate) && curlinedate.isBefore(requiredenddate) ) {
                        			String sqlinsertstat = "INSERT INTO " + inserttablename +"(代码,交易日期,开盘价,最高价,最低价,收盘价,成交量,成交额) values ("
                    						+ "'" + node.getMyOwnCode().trim() + "'" + ","
                    						+ "'" +  curlinedate + "'" + ","
                    						+ "'" +  tmplinelist.get(1).trim() + "'" + "," 
                    						+ "'" +  tmplinelist.get(2).trim() + "'" + "," 
                    						+ "'" +  tmplinelist.get(3).trim() + "'" + "," 
                    						+ "'" +  tmplinelist.get(4).trim() + "'" + "," 
                    						+ "'" +  tmplinelist.get(5).trim() + "'" + "," 
                    						+ "'" +  tmplinelist.get(6).trim() + "'"  
                    						+ ")"
                    						;
                        			logger.debug(sqlinsertstat);
                    				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
                    				lineimportcount ++;
                    				newdataimported = true;
                        		} else if(curlinedate.isBefore(requiredstartdate) ) {
                        			finalneededsavelinefound = true;
                        			if(lineimportcount == 0) {
                        				newdataimported = false;
                        				Files.append(node.getMyOwnCode() + "交量记录导入起始时间为:" + line + System.getProperty("line.separator") ,tmplogfile,sysconfig.charSet());
                            			Files.append(node.getMyOwnCode() + "共导入:" + lineimportcount + "个记录" + System.getProperty("line.separator") ,tmplogfile,sysconfig.charSet());
                        			} else {
                        				Files.append(node.getMyOwnCode() + "成交量记录导入起始时间为:" + line + System.getProperty("line.separator") ,tmplogfile,sysconfig.charSet());
                            			Files.append(node.getMyOwnCode() + "共导入:" + lineimportcount + "个记录" + System.getProperty("line.separator") ,tmplogfile,sysconfig.charSet());
                        			}
                        		}
                    		} catch (Exception e) {
                    			logger.debug("不是有日期的数据行");
                    		}
                        }
                    } 
                    nextend--;  
                }  
                nextend--;     rf.seek(nextend);  
            }  
        } catch (FileNotFoundException e) { newdataimported = null; e.printStackTrace();  
        } catch (IOException e)           { newdataimported = null; e.printStackTrace();  
        } finally {  
            try {  
                if (rf != null)  rf.close();  
                rf = null;
            } catch (IOException e) {   e.printStackTrace();  }  
        } 
		
 		return true;
	}
	/**
	 * 
	 * @param tmpbkcode
	 * @param tmpbkfile
	 * @param lastestdbrecordsdate
	 * @param inserttablename
	 * @param datarule
	 * @param tmprecordfile
	 * @return
	 */
	private Boolean setVolAmoRecordsFromFileToDatabase (BkChanYeLianTreeNode tmpnode, File tmpbkfile, LocalDate lastestdbrecordsdate, String inserttablename, String datarule,File tmprecordfile)
	{
		Boolean newdataimported = null;
		String tmpbkcode = tmpnode.getMyOwnCode();
		if(lastestdbrecordsdate == null) //null说明数据库里面还没有相关的数据，把时间设置为1900年把文件中所有数据都导入
			try {
		        lastestdbrecordsdate = LocalDate.now().minus(100,ChronoUnit.YEARS);
				Files.append(tmpbkcode + "成交量记录将导入所有记录！"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			} catch (Exception e1) {e1.printStackTrace();} 
		
		RandomAccessFile rf = null;
        try {  
            rf = new RandomAccessFile(tmpbkfile, "r");  
            long len = rf.length();  
            long start = rf.getFilePointer();  
            long nextend = start + len - 1;  
            String line;  
            rf.seek(nextend);  
            int c = -1;  
            boolean finalneededsavelinefound = false; //标记需要导入的日期的起始行
            int lineimportcount =0;
            while (nextend > start && finalneededsavelinefound == false) {
                c = rf.read();
            	if (c == '\n' || c == '\r') {  
                    line = rf.readLine();  
                    if (line != null) {  
                        @SuppressWarnings("deprecation")
						List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);

                        if( tmplinelist.size() ==7 && !tmplinelist.get(5).equals("0")) { //有可能是半天数据，有0，不完整，不能录入,对个股来说，半天数据也有，要特别处理
                        	String open = tmplinelist.get(1).trim();
                        	String high = tmplinelist.get(2).trim();
                        	String low  = tmplinelist.get(3).trim();
                        	String close= tmplinelist.get(4).trim();
                        	String vol = tmplinelist.get(5).trim();
                        	String amo = tmplinelist.get(6).trim();
                        	
                        	if(tmpbkcode.equalsIgnoreCase("000852") && tmpnode.getType() == BkChanYeLianTreeNode.TDXBK) {//中证1000的文件，vol and amo 数据没有精确到个位
                        		vol = String.valueOf( Double.parseDouble(vol) * 10000 );
                        		amo = String.valueOf( Double.parseDouble(amo) * 1000000 );
                        	}
                        		
                        	LocalDate curlinedate = null;
                    		try {
                    			String beforparsedate = tmplinelist.get(0);
                    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datarule);
                    			curlinedate =  LocalDate.parse(beforparsedate,formatter) ;
                    			if( curlinedate.isAfter(lastestdbrecordsdate)) {
                        			String sqlinsertstat = "INSERT INTO " + inserttablename +"(代码,交易日期,开盘价,最高价,最低价,收盘价,成交量,成交额) values ("
                    						+ "'" + tmpbkcode.trim() + "'" + ","
                    						+ "'" +  curlinedate + "'" + ","
                    						+ "'" +  open + "'" + "," 
                    						+ "'" +  high + "'" + "," 
                    						+ "'" +  low + "'" + "," 
                    						+ "'" +  close + "'" + "," 
                    						+ "'" +  vol + "'" + "," 
                    						+ "'" +  amo + "'"  
                    						+ ")"
                    						;
                    				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
                    				lineimportcount ++;
                    				newdataimported = true;
                        		} else if(curlinedate.compareTo(lastestdbrecordsdate) == 0) {
                        			finalneededsavelinefound = true;
                        			if(lineimportcount == 0) {
                        				newdataimported = false;
                        				Files.append(tmpbkcode + "交量记录导入起始时间为:" + line + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                            			Files.append(tmpbkcode + "共导入:" + lineimportcount + "个记录" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        			} else {
                        				Files.append(tmpbkcode + "成交量记录导入起始时间为:" + line + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                            			Files.append(tmpbkcode + "共导入:" + lineimportcount + "个记录" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        			}
                        		}
                    		} catch (Exception e) { logger.debug("不是有日期的数据行");}
                        }
                    } 
                    nextend--;  
                }  
                nextend--;  
                rf.seek(nextend);  
                if (nextend == 0) {// 当文件指针退至文件开始处，输出第一行   文件第一行是文件抬头，不需要处理
                    //logger.debug(new String(rf.readLine().getBytes("ISO-8859-1"), charset ));  
                }  
            }  
        } catch (FileNotFoundException e) {  newdataimported = null; e.printStackTrace();  
        } catch (IOException e) { newdataimported = null;  e.printStackTrace();  
        } finally {  
            try {  
                if (rf != null)  rf.close();        rf = null;
            } catch (IOException e) { e.printStackTrace(); }  
        }  
        
        return newdataimported;
	}
/*
 * 
 */
//	public Date getTDXRelatedTableLastModfiedTime() 
//	{
//		Date lastesttdxtablesmdftime = null;
//		String sqlquerystat = " SELECT MAX(创建时间) FROM 通达信板块列表" ;
//
//		CachedRowSetImpl rs = null; 
//		try {  
//		    	 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//		    	 
//		    	 java.sql.Timestamp tmplastmdftime = null;
//		    	 while(rs.next())
//		    		  tmplastmdftime = rs.getTimestamp("MAX(创建时间)");
//		    	 logger.debug(tmplastmdftime);
//		    	 lastesttdxtablesmdftime = tmplastmdftime;
//
//		    } catch(java.lang.NullPointerException e){ lastesttdxtablesmdftime = null;e.printStackTrace();
//		    } catch (SQLException e) { lastesttdxtablesmdftime = null; e.printStackTrace();
//		    } catch(Exception e) { lastesttdxtablesmdftime = null; e.printStackTrace();
//		    }  finally {
//		       if(rs != null)
//					try {
//						rs.close(); rs = null;
//					} catch (SQLException e) {e.printStackTrace();}
//		    }
//			
//		return lastesttdxtablesmdftime;
//	}
	/*
	 * 判断板块是什么类型的板块，概念/风格/指数/行业/地域
	 */
//	private HashMap<String,String> getActionRelatedTables1 (BanKuai bankuai,String stockcode)
//	{
//		
////		HashMap<String,String> actiontables = new HashMap<String,String> ();
////		if(bankuai != null) {
////			String bkcode = bankuai.getMyOwnCode();
////			
////			String stockvsbktable =null;
////				if(stockvsbktable == null) {	
////					CachedRowSetImpl rs = null;
////					int dy = -1;
////					try {  
////						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM 股票通达信概念板块对应表  WHERE 板块代码='" + bkcode + "'";
////						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
////						 while(rs.next()) {
////							 dy = rs.getInt("RESULT");
////						 }
////					} catch(java.lang.NullPointerException e) { logger.debug( "数据库连接为NULL!");
////					} catch (SQLException e) { e.printStackTrace();
////					} catch(Exception e) {e.printStackTrace();
////					} finally {
////						try {
////							if(rs != null) rs.close();
////						} catch (SQLException e) { e.printStackTrace();}
////						rs = null;
////						
////						if(dy>0)
////							stockvsbktable =  "股票通达信概念板块对应表";
////					}
////				}
////				if(stockvsbktable == null) {
////					CachedRowSetImpl rs = null;
////					int dy = -1;
////					try {
////						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM 股票通达信风格板块对应表 WHERE 板块代码='" + bkcode + "'";
////						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
////						 while(rs.next()) {
////							  dy = rs.getInt("RESULT");
////						 }
////					} catch(java.lang.NullPointerException e){logger.debug( "数据库连接为NULL!");
////					} catch (SQLException e) {e.printStackTrace();
////					} catch(Exception e){e.printStackTrace();
////					} finally {
////						try {
////							if(rs != null)rs.close();
////						} catch (SQLException e) {e.printStackTrace();}
////						rs = null;
////						
////						if(dy>0)
////							stockvsbktable =  "股票通达信风格板块对应表";
////					}
////				}
////				
////				if(stockvsbktable == null) {
////					CachedRowSetImpl rs = null;
////					int dy = -1;
////					try {
////						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM 股票通达信行业板块对应表  WHERE 板块代码='" + bkcode + "'";
////						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
////						 while(rs.next()) {
////							 dy = rs.getInt("RESULT");
////						 }
////					} catch(java.lang.NullPointerException e){logger.debug( "数据库连接为NULL!");
////					} catch (SQLException e) {e.printStackTrace();
////					} catch(Exception e){e.printStackTrace();
////					} finally {
////						try {
////							if(rs != null)rs.close();
////						} catch (SQLException e) {e.printStackTrace();}
////						rs = null;
////						
////						if(dy>0)
////							stockvsbktable = "股票通达信行业板块对应表";
////					}
////				}
////				
////				if(stockvsbktable == null) {
////					CachedRowSetImpl rs = null;
////					int dy = -1;
////					try {
////						String sqlquerystat = "SELECT COUNT(*) AS RESULT FROM 股票通达信交易所指数对应表  WHERE 板块代码='" + bkcode + "'";
////						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
////						 while(rs.next()) {
////							 dy = rs.getInt("RESULT");
////						 }
////					} catch(java.lang.NullPointerException e) {e.printStackTrace();logger.debug( "数据库连接为NULL!");
////					} catch (SQLException e) {e.printStackTrace();
////					} catch(Exception e){e.printStackTrace();
////					} finally {
////						try {
////							if(rs != null)rs.close();
////						} catch (SQLException e) {e.printStackTrace();
////						}
////						rs = null;
////						
////						if(dy>0)
////							stockvsbktable = "股票通达信交易所指数对应表";
////					}
////				}
////				if(stockvsbktable == null) {
////					try {
////						CachedRowSetImpl rsdy = null;
////						int dy = -1;
////						try {
////							String diyusqlquerystat = "SELECT *  FROM 通达信板块列表 WHERE 板块id = '" + bkcode + "'" ;
////							 rsdy = connectdb.sqlQueryStatExecute(diyusqlquerystat);
////							 while(rsdy.next()) {
////								 dy = rsdy.getInt("对应TDXSWID");
////							 }
////						} catch(java.lang.NullPointerException e){logger.debug( "数据库连接为NULL!");
////						} catch (SQLException e) {logger.debug("java.sql.SQLException: 对列 1 中的值 (芯片) 执行 getInt 失败，不是地域板块。");
////						} catch(Exception e){e.printStackTrace();
////						} finally {
////							try {
////								if(rsdy != null) rsdy.close();
////							} catch (SQLException e) {e.printStackTrace();}
////							rsdy = null;
////							if(dy>=1 && dy <=32) { stockvsbktable =  "股票通达信基本面信息对应表"; actiontables.put("地域代码", String.valueOf(dy) );}
////						}
////					} catch (java.lang.NullPointerException e1) {}
////				}
////				
////				if(stockvsbktable != null)
////					actiontables.put("股票板块对应表", stockvsbktable);
////		}
//		
//	String bkcjltable = null;
//	String jys = bankuai.getSuoShuJiaoYiSuo();
//	if(jys == null)
//		bkcjltable = "";
//	else if(bankuai.getSuoShuJiaoYiSuo().toLowerCase().equals("sh"))
//		bkcjltable = "通达信板块每日交易信息";
//	else 
//		bkcjltable = "通达信交易所指数每日交易信息";
//	actiontables.put("板块每日交易量表", bkcjltable);
//	
//		if(!Strings.isNullOrEmpty(stockcode)) {
//				String gegucjltable;
//				// TODO Auto-generated method stub
////				if(stockcode.startsWith("6"))
//				if(sysconfig.isShangHaiStock(stockcode)) gegucjltable = "通达信上交所股票每日交易信息";
//				else  gegucjltable = "通达信深交所股票每日交易信息";
//				actiontables.put("股票每日交易量表", gegucjltable);
//		}
//		
//		return actiontables;
//	}
	private String getBanKuaiJiaoYiLiangBiaoName (BanKuai bankuai) {
		String bkcjltable = null;
		String jys = bankuai.getNodeJiBenMian().getSuoShuJiaoYiSuo();
		if(jys == null)
			bkcjltable = "";
		else if(bankuai.getNodeJiBenMian().getSuoShuJiaoYiSuo().toLowerCase().equals("sh"))
			bkcjltable = "通达信板块每日交易信息";
		else if(bankuai.getNodeJiBenMian().getSuoShuJiaoYiSuo().toLowerCase().equals("sz"))
			bkcjltable = "通达信交易所指数每日交易信息";
		else if(bankuai.getNodeJiBenMian().getSuoShuJiaoYiSuo().toLowerCase().equals("bk"))
			bkcjltable = "大智慧板块每日交易信息";

		return bkcjltable;
	}
	private String getStockJiaoYiLiangBiaoName (Stock stock) {
		String stockcode = stock.getMyOwnCode();
					String gegucjltable;
					// TODO Auto-generated method stub
//					if(stockcode.startsWith("6"))
					if(sysconfig.isShangHaiStock(stockcode)) gegucjltable = "通达信上交所股票每日交易信息";
					else  gegucjltable = "通达信深交所股票每日交易信息";
		return gegucjltable;
	}
	/*
	 * 找出某个时间点 行业/概念/风格/指数 通达信某个板块的所有个股及权重,不找成交额,存放在系统的两个树的BanKuaiAndStockTree,来反应板块和个股的关系。
	 */
	public BanKuai getTDXBanKuaiGeGuOfHyGnFg(BanKuai currentbk,LocalDate selecteddatestart , LocalDate selecteddateend  )
	{
		BanKuaiAndStockTree treeallstocks = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		String currentbkcode = currentbk.getMyOwnCode();
		
		String bktypetable = currentbk.getShuJuJiLuInfo().getGuPiaoBanKuaiDuiYingBiao();
		if(bktypetable == null) {
			this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SH");
		    this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SZ");
		}
		bktypetable = currentbk.getShuJuJiLuInfo().getGuPiaoBanKuaiDuiYingBiao();
		if(bktypetable == null)
			return currentbk;
			
		String sqlquerystat1 = "";
		if(bktypetable.trim().equals("股票通达信基本面信息对应表") ) { //找地域板块,用不上
//			String dycode = actiontables.get("地域代码");
//			sqlquerystat1 = "SELECT a股.`股票代码` , a股.`股票名称` ,10 as '股票权重' \r\n" + 
//			 		"FROM 股票通达信基本面信息对应表, a股\r\n" + 
//			 		"WHERE 股票通达信基本面信息对应表.`地域DY` = " + dycode + "\r\n" + 
//			 		"AND 股票通达信基本面信息对应表.`股票代码GPDM` = a股.`股票代码`"
//			 		;
		 } else { //概念风格行业指数板块
			 //from WQW
			 sqlquerystat1 = "select "+ bktypetable + ".`股票代码` , a股.`股票名称`, "+ bktypetable + ".`板块代码` , \r\n"
					 			+ bktypetable + ".`股票权重` , "+ bktypetable + ".`板块龙头`, \r\n "
					 			+ bktypetable + ".`加入时间`, " + bktypetable + ".`移除时间`  \r\n"
					 			+ " from "+ bktypetable + ", a股\r\n" + 
				 		"          where "+ bktypetable + ".`股票代码`  = a股.`股票代码`   \r\n" + 
				 		
				 		"and (  (  Date("+ bktypetable + ".`加入时间`) between '" +  selecteddatestart + "'  and '" +  selecteddateend + "')\r\n" + 
				 		"           		 or( ifnull("+ bktypetable + ".`移除时间`, '2099-12-31') between '" +  selecteddatestart + "'  and '" +  selecteddateend + "')\r\n" + 
				 		"           		 or( Date("+ bktypetable + ".`加入时间`) <= '" +  selecteddatestart + "' and ifnull(" + bktypetable + ".`移除时间`, '2099-12-31') >= '" +  selecteddateend + "' )\r\n" + 
				 		"			  )" +
				 		"           and "+ bktypetable + ".`板块代码` =  '" + currentbkcode + "'\r\n" + 
				 		"         \r\n"  
				 		;
		 }
		 CachedRowSetImpl rs1 = connectdb.sqlQueryStatExecute(sqlquerystat1);
		try { 
			 while(rs1.next()) {  
				String tmpstockcode = rs1.getString("股票代码");
				Integer weight = rs1.getInt("股票权重");
				Boolean longtou;
				try{
					longtou = rs1.getBoolean("板块龙头");
				} catch (java.sql.SQLException e) {longtou = false;}
				LocalDate joindate = null;
				try{
					joindate = rs1.getDate("加入时间").toLocalDate();
				} catch (java.sql.SQLException ex1) {continue; }
				LocalDate leftdate;
				try {
					leftdate = rs1.getDate("移除时间").toLocalDate().with(DayOfWeek.FRIDAY); //周线都是以周五为计算的，任何个股移出都调整到周五
				} catch (java.lang.NullPointerException e) {
					leftdate = LocalDate.parse("3000-01-01");
				}
				DateTime joindt= new DateTime(joindate.getYear(), joindate.getMonthValue(), joindate.getDayOfMonth(), 0, 0, 0, 0);
				DateTime leftdt = new DateTime(leftdate.getYear(), leftdate.getMonthValue(), leftdate.getDayOfMonth(), 0, 0, 0, 0);
				Interval joinleftinterval = null ;
				try {
					joinleftinterval = new Interval(joindt, leftdt);
				} catch (Exception e) {joinleftinterval = new Interval(leftdt,joindt );}
				
				if(currentbk.getStockOfBanKuai(tmpstockcode) != null ) {//已经有了
					StockOfBanKuai bkofst =  currentbk.getStockOfBanKuai(tmpstockcode);
					bkofst.setStockQuanZhong(weight);
					bkofst.setBkLongTou(longtou);
					bkofst.addInAndOutBanKuaiInterval(joinleftinterval);
				} else {
					Stock tmpstock = (Stock)treeallstocks.getSpecificNodeByHypyOrCode (tmpstockcode,BkChanYeLianTreeNode.TDXGG);
					if(tmpstock != null) {
						
						StockOfBanKuai bkofst = new StockOfBanKuai(currentbk,tmpstock);
						bkofst.setStockQuanZhong(weight);
						bkofst.setBkLongTou(longtou);
						bkofst.addInAndOutBanKuaiInterval(joinleftinterval);
						
						currentbk.addNewBanKuaiGeGu(bkofst);
					}
				}
			}
				
		} catch(java.lang.NullPointerException e){ e.printStackTrace();
//				logger.debug( "数据库连接为NULL!");
		} catch (SQLException e) {e.printStackTrace();
		} catch(Exception e){e.printStackTrace();
		} finally {
				try {if(rs1 != null)rs1.close();} catch (SQLException e) {e.printStackTrace();}
				rs1 = null;
		}
		
		return currentbk;
	}
	/*
	 * 
	 */
	
	/*
	 * 查找在市值范围内的所有股票代码,日期如果是周六/日按周五的市值
	 */
	public Set<String> getStockOfRangeShiZhi(Double shizhidown, Double shizhiup, LocalDate localDate, int difference, String zhishitype) 
	{
		LocalDate requireend = localDate.with(DayOfWeek.FRIDAY);
		LocalDate requirestart = localDate.with(DayOfWeek.MONDAY);
	
		String sqlquerystat = "SELECT A.STOCKCODE AS STOCKCODE, A.WORKDAY, A.总市值/JILUTIAOSHU AS 个股周平均总市值, A.总流通市值/JILUTIAOSHU AS 个股周平均流通市值\r\n" +
								"FROM (select 通达信深交所股票每日交易信息.`代码` AS STOCKCODE, 通达信深交所股票每日交易信息.`交易日期` as workday,   sum( 通达信深交所股票每日交易信息.`成交额`) AS 板块周交易额 ,\r\n" + 
								"sum( 通达信深交所股票每日交易信息.`成交量`) AS 板块周交易量,  \r\n" +
								"sum( 通达信深交所股票每日交易信息.`换手率`) AS 板块周换手率 , \r\n" +
								"sum( 通达信深交所股票每日交易信息.`总市值`) AS 总市值 , \r\n" +
								"sum( 通达信深交所股票每日交易信息.`流通市值`) AS 总流通市值, \r\n" + 
								"count(1) as JILUTIAOSHU  \r\n" +
								"from 通达信深交所股票每日交易信息 \r\n" +
								"where 交易日期  between ' " + requirestart.toString() + "' and  '" + requireend.toString() + "' \r\n" +
								"GROUP BY YEAR( 通达信深交所股票每日交易信息.`交易日期`), WEEK( 通达信深交所股票每日交易信息.`交易日期`), 代码 \r\n" +
								") A \r\n" +
								"WHERE A.总流通市值/JILUTIAOSHU >= " + shizhidown + " AND A.总流通市值/JILUTIAOSHU <" + shizhiup +" \r\n" + 
								" \r\n"  +
								"UNION \r\n" +
								" \r\n" +
								"SELECT A.STOCKCODE AS STOCKCODE, A.WORKDAY, A.总市值/JILUTIAOSHU AS 个股周平均总市值, A.总流通市值/JILUTIAOSHU AS 个股周平均流通市值\r\n" +
								"FROM (select 通达信上交所股票每日交易信息.`代码` AS STOCKCODE, 通达信上交所股票每日交易信息.`交易日期` as workday,   sum( 通达信上交所股票每日交易信息.`成交额`) AS 板块周交易额 ,\r\n" + 
								" sum( 通达信上交所股票每日交易信息.`成交量`) AS 板块周交易量,  \r\n" +
								" sum( 通达信上交所股票每日交易信息.`换手率`) AS 板块周换手率 , \r\n" +
								" sum( 通达信上交所股票每日交易信息.`总市值`) AS 总市值 , \r\n" +
								" sum( 通达信上交所股票每日交易信息.`流通市值`) AS 总流通市值,\r\n" + 
								"  count(1) as JILUTIAOSHU \r\n" +
								" from 通达信上交所股票每日交易信息 \r\n" +
								"where 交易日期 between ' " + requirestart.toString() + "' and  '" + requireend.toString() + "' \r\n" +
								"GROUP BY YEAR( 通达信上交所股票每日交易信息.`交易日期`), WEEK( 通达信上交所股票每日交易信息.`交易日期`), 代码 \r\n" +
								") A \r\n" +
								" \r\n" +
								"WHERE A.总流通市值/JILUTIAOSHU >= " + shizhidown + " AND A.总流通市值/JILUTIAOSHU <" + shizhiup +" \r\n"   
							  ;
		
		CachedRowSetImpl rsfg = null;
		Set<String> stockcodesetbyshizhirang = new HashSet<String> ();
		try {  
			 rsfg = connectdb.sqlQueryStatExecute(sqlquerystat);
			 while(rsfg.next()) {
				String stockcode  = rsfg.getString("STOCKCODE");
				stockcodesetbyshizhirang.add(stockcode);
			}
		}catch(java.lang.NullPointerException e){ e.printStackTrace();
//			logger.debug( "数据库连接为NULL!");
		} catch (SQLException e) {e.printStackTrace();
		}catch(Exception e){e.printStackTrace();
		} finally {
			try { if(rsfg != null) rsfg.close();} catch (SQLException e) {e.printStackTrace();}
			rsfg = null;
		}
		
		return stockcodesetbyshizhirang;
	}
	/*
	 * 板块设定时间跨度内和大盘相比的按周期占比。按实际时间，周数据的时间吻合正确由调用函数负责
	 */
	public  BanKuai getBanKuaiZhanBi (BanKuai bankuai,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{
		if(bankuai == null  )
			return null;
		
//		selecteddatestart = selecteddatestart.with(DayOfWeek.MONDAY);
//		if(!bankuai.isNodeDataAtNotCalWholeWeekMode() )
//			selecteddateend = selecteddateend.with(DayOfWeek.FRIDAY);
		
		//本函数初始是开发为周的占比，所以日/月线的占比掉用其他函数
		if(period.equals(NodeGivenPeriodDataItem.DAY)) //调用日线查询函数
			; 
		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //调用月线查询函数
			;
		
		NodeXPeriodData nodewkperioddata = bankuai.getNodeXPeroidData(period);
//		if(nodewkperioddata.isInNotCalWholeWeekMode() == null)
//			selecteddateend = selecteddateend.with(DayOfWeek.FRIDAY);
		
		String bkcys = bankuai.getNodeJiBenMian().getSuoShuJiaoYiSuo();
		if(bkcys == null)	return bankuai;
		String bkcjltable = null;
		if(bkcys.equalsIgnoreCase("sh"))
			bkcjltable = "通达信板块每日交易信息";
		else if(bkcys.equalsIgnoreCase("sz"))
			bkcjltable = "通达信交易所指数每日交易信息";
		else if(bkcys.equalsIgnoreCase("BK"))	
			bkcjltable = "大智慧板块每日交易信息";
		
		String bkcode = bankuai.getMyOwnCode();
		String formatedstartdate = selecteddatestart.toString();
		String formatedenddate  = selecteddateend.toString();
		
		//包含成交量和成交额的SQL
		String sqlquerystat = "SELECT YEAR(t.workday) AS CALYEAR, WEEK(t.workday) AS CALWEEK, M.BKCODE AS BKCODE, t.EndOfWeekDate AS EndOfWeekDate," +
				 "M.板块周交易额 as 板块周交易额, SUM(T.AMO) AS 大盘周交易额 ,  M.板块周交易额/SUM(T.AMO) AS CJE占比,\r\n" +

				"M.板块周交易量 as 板块周交易量, SUM(T.VOL) AS 大盘周交易量 ,  M.板块周交易量/SUM(T.VOL) AS VOL占比, \r\n" +
				"  M.JILUTIAOSHU  \r\n"+
				 
				"FROM\r\n" + 
				"(\r\n" + 
				"SELECT  通达信板块每日交易信息.`交易日期` as workday, DATE(通达信板块每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信板块每日交易信息.交易日期)) DAY) as EndOfWeekDate, \r\n" + 
				"		  sum(通达信板块每日交易信息.`成交额`) AS AMO , sum(通达信板块每日交易信息.`成交量`) AS VOL \r\n" + 
				"FROM 通达信板块每日交易信息 \r\n" + 
				"WHERE 代码 = '999999' AND 通达信板块每日交易信息.`交易日期` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" + 
				" GROUP by YEARWEEK(通达信板块每日交易信息.`交易日期`,2)\r\n" + 
				"\r\n" + 
				"UNION ALL\r\n" + 
				"\r\n" + 
				"select  通达信交易所指数每日交易信息.`交易日期` as workday,   DATE(通达信交易所指数每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信交易所指数每日交易信息.交易日期)) DAY) as EndOfWeekDate, \r\n" + 
				"			sum(通达信交易所指数每日交易信息.`成交额`) AS AMO, sum(通达信交易所指数每日交易信息.`成交量`) AS VOL \r\n" + 
				"from 通达信交易所指数每日交易信息\r\n" + 
				"where 代码 = '399001' AND 通达信交易所指数每日交易信息.`交易日期` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" + 
				" group by YEARWEEK(通达信交易所指数每日交易信息.`交易日期`,2)\r\n" + 
				") T,\r\n" + 
				"\r\n" + 
				"(select " + bkcjltable + ".`代码` AS BKCODE, " + bkcjltable + ".`交易日期` as workday,  "
						+ "sum( " + bkcjltable + ".`成交额`) AS 板块周交易额 , \r\n"
						+ "sum( " + bkcjltable + ".`成交量`) AS 板块周交易量, \r\n"
						+ " count(1) as JILUTIAOSHU \r\n"
						+ "from " + bkcjltable + "\r\n" +
						
				"where 代码 = '" + bkcode + "' AND " + bkcjltable + ".`交易日期` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" + 
				"GROUP BY YEARWEEK( " + bkcjltable + ".`交易日期`,2)\r\n" + 
				") M\r\n" + 
				" WHERE YEARWEEK(T.WORKDAY,2) = YEARWEEK(M.WORKDAY,2) \r\n" + 
				"  GROUP BY YEARWEEK(t.workday,2)"
				;
							
		logger.debug("为板块" + bankuai.getMyOwnCode() + bankuai.getMyOwnName() + "寻找从" + selecteddatestart.toString() + "到" + selecteddateend.toString() + "占比数据！");
		
		String sqlquerystatfx = "SELECT 操作记录重点关注.`日期`, COUNT(*) AS RESULT FROM 操作记录重点关注 \r\n" + 
				" WHERE 股票代码='" + bkcode + "'" + "\r\n" + 
				" AND 操作记录重点关注.`日期` between '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" +
				" GROUP BY YEAR(操作记录重点关注.`日期`), WEEK(操作记录重点关注.`日期`) "
				;
		
		CachedRowSetImpl rs = null;
		CachedRowSetImpl rsfx = null;
		Boolean hasfengxiresult = false;
		
		try {
			//分析
			rsfx = connectdb.sqlQueryStatExecute(sqlquerystatfx);
			while(rsfx.next()) {
				java.sql.Date recdate = rsfx.getDate("日期");
				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(recdate);
				Integer reccount = rsfx.getInt("RESULT");
				nodewkperioddata.addFxjgToPeriod(wknum, reccount);
			}
			
			//交易
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			while(rs.next()) {
				java.util.Date lastdayofweek = rs.getDate("EndOfWeekDate");
//				ZonedDateTime zdtime = lastdayofweek.toLocalDate().with(DayOfWeek.FRIDAY).atStartOfDay(ZoneOffset.UTC);
				java.util.TimeZone china =  java.util.TimeZone.getDefault();
//				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(lastdayofweek, china, Locale.CHINA);
				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(lastdayofweek);
				Date startdate = wknum.getStart(); Date endstart = wknum.getEnd();
				
				Double bankuaicje = rs.getDouble("板块周交易额");
				Double dapancje = rs.getDouble("大盘周交易额");
				Double cjezb = rs.getDouble("CJE占比");

				Double bankuaicjl = rs.getDouble("板块周交易量");
				Double dapancjl = rs.getDouble("大盘周交易量");
				Double cjlzb = rs.getDouble("VOL占比");
				
				int exchangedaysnumber = rs.getInt("JILUTIAOSHU");
				
				NodeGivenPeriodDataItem bkperiodrecord = new NodeGivenPeriodDataItemForJFC( bkcode, NodeGivenPeriodDataItem.WEEK,
						wknum, 0.0, 0.0,  0.0,  0.0,bankuaicjl, bankuaicje );
				
//				NodeGivenPeriodDataItem bkperiodrecord = new NodeGivenPeriodDataItemForTA4J( bkcode, NodeGivenPeriodDataItem.WEEK,
//						zdtime, PrecisionNum.valueOf(0.0), PrecisionNum.valueOf(0.0),  PrecisionNum.valueOf(0.0),  PrecisionNum.valueOf(0.0), 
//						PrecisionNum.valueOf(bankuaicjl), PrecisionNum.valueOf(bankuaicje) );
				
				bkperiodrecord.setNodeToDpChenJiaoErZhanbi(cjezb);
				bkperiodrecord.setNodeToDpChenJiaoLiangZhanbi(cjlzb);
				bkperiodrecord.setUplevelChengJiaoEr(dapancje);
				bkperiodrecord.setUplevelChengJiaoLiang(dapancjl);
				bkperiodrecord.setExchangeDaysNumber(exchangedaysnumber);
				
				nodewkperioddata.addNewXPeriodData(bkperiodrecord);
				
				
				wknum = null;
				bkperiodrecord = null;
				bankuaicje = null;
				dapancje = null;
				lastdayofweek = null;
				bankuaicjl = null;
				dapancjl = null;
				
				
			}
		}catch(java.lang.NullPointerException e){e.printStackTrace();
	    } catch(Exception e){e.printStackTrace();
	    }  finally {
	    	try {
				rs.close();	rsfx.close();
				rs = null;rsfx = null;bkcjltable = null;
			} catch (SQLException e) {e.printStackTrace();}
	    }

		return bankuai;
	}
	/*
	 * 股票对板块的按周占比
	 */
	public StockOfBanKuai getGeGuZhanBiOfBanKuai(BanKuai bankuai, StockOfBanKuai stockofbk,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{//本函数初始是开发为周的占比，所以日/月线的占比掉用其他函数
		if(period.equals(NodeGivenPeriodDataItem.DAY)) //调用日线查询函数
			; 
		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //调用月线查询函数
			;
		
//		StockOfBanKuai stockofbk = bankuai.getBanKuaiGeGu(stock.getMyOwnCode());
		NodeXPeriodData nodedayperioddata = stockofbk.getNodeXPeroidData(period);
		
		String stockcode = stockofbk.getMyOwnCode();
		String stockvsbktable = bankuai.getShuJuJiLuInfo().getGuPiaoBanKuaiDuiYingBiao();
		if(stockvsbktable == null) {
			this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SH");
		    this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SZ");
		}
		String bkcjetable = this.getBanKuaiJiaoYiLiangBiaoName(bankuai);
		String gegucjetable = this.getStockJiaoYiLiangBiaoName(stockofbk.getStock() );
		String bknametable = "通达信板块列表";
		
		String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart);
		String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend);
		
		String sqlquerystat = "";
		if(stockvsbktable.trim().equals("股票通达信基本面信息对应表") ) { //找地域板块
//			String dycode = actiontables.get("地域代码");
//			sqlquerystat = "";
//			return stockofbk;
		 } else { //概念风格行业指数板块
			 //包括成交额和 成交量的SQL
		 sqlquerystat = "SELECT Y.year, Y.week,Y.EndOfWeekDate, Y.股票代码, Y.板块代码, Y.stock_amount, Y.stock_vol, Y.`股票权重` ,Y.`股票名称`,\r\n" + 
				"Z.代码 , Z.板块名称,  z.category_amount, Y.stock_amount/ z.category_amount ratio, z.category_vol, Y.stock_vol/z.category_vol volratio \r\n" + 
				"FROM \r\n" + 
				"(select X.year, X.week, X.EndOfWeekDate, X.股票代码, X.板块代码, X.stock_amount,X.stock_vol, X.`股票权重` ,a股.`股票名称`\r\n" + 
				"from (select year(" +  gegucjetable    + ".`交易日期`) as year,week(" +  gegucjetable    + ".`交易日期`) as week, \r\n" + 
				"DATE(" +  gegucjetable    + ".交易日期 + INTERVAL (6 - DAYOFWEEK(" +  gegucjetable    + ".交易日期)) DAY) EndOfWeekDate, \r\n" + 
				"" +  stockvsbktable + ".`股票代码` , " +  stockvsbktable + ".`板块代码` , \r\n" + 
				"sum(" +  gegucjetable    + ".`成交额`) stock_amount, sum(" +  gegucjetable    + ".`成交量`) stock_vol," +  stockvsbktable + ".`股票权重`\r\n" + 
				"from " +  stockvsbktable + ", " +  gegucjetable    + "\r\n" + 
				"where " +  stockvsbktable + ".`股票代码`   = " +  gegucjetable    + ".`代码`\r\n" + 
				"		and Date(" +  gegucjetable    + ".`交易日期`) >= Date(" +  stockvsbktable + ".`加入时间`)\r\n" + 
				"		and " +  gegucjetable    + ".`交易日期` <  ifnull(" +  stockvsbktable + ".`移除时间`, '2099-12-31')\r\n" + 
				"		and " +  gegucjetable    + ".`交易日期` BETWEEN  '" +  formatedstartdate + "' AND '" +  formatedenddate + "'\r\n" + 
				"		and " +  stockvsbktable + ".`板块代码` =  '"  + bankuai.getMyOwnCode() +"'\r\n" + 
				"		and " +  stockvsbktable + ".`股票代码` = '"+ stockcode +"'\r\n" + 
				"         \r\n" + 
				"group by year(" +  gegucjetable    + ".`交易日期`) ,week(" +  gegucjetable    + ".`交易日期`) , \r\n" + 
				"		  " +  stockvsbktable + ".`股票代码`, " +  stockvsbktable + ".`板块代码`\r\n" + 
				"order by year(" +  gegucjetable    + ".`交易日期`) ,week(" +  gegucjetable    + ".`交易日期`) , \r\n" + 
				"			" +  stockvsbktable + ".`股票代码`, " +  stockvsbktable + ".`板块代码`) X,\r\n" + 
				"a股\r\n" + 
				"where   X.`股票代码` = a股.`股票代码` ) Y,\r\n" + 
				"						\r\n" + 
				"(select year(" +  bkcjetable + ".`交易日期`) as year, week(" +  bkcjetable + ".`交易日期`) as week,\r\n" + 
				"DATE(" +  bkcjetable + ".交易日期 + INTERVAL (6 - DAYOFWEEK(" +  bkcjetable + ".交易日期)) DAY) EndOfWeekDate, \r\n" + 
				"" +  bkcjetable + ".`代码` , " + bknametable + ".`板块名称`,   sum(" +  bkcjetable + ".`成交额`) category_amount, sum(" +  bkcjetable + ".`成交量`) category_vol\r\n" + 
				"from " +  bkcjetable + ", " + bknametable + "\r\n" + 
				"where " +  bkcjetable + ".`代码` = " + bknametable  + ".`板块ID`\r\n" + 
				"		and " +  bkcjetable + ".`交易日期` BETWEEN '" +  formatedstartdate + "' AND '" +  formatedenddate + "'\r\n" + 
				"		and " +  bkcjetable + ".`代码` =  '"  + bankuai.getMyOwnCode() + "'\r\n" + 
				"group by year(" +  bkcjetable + ".`交易日期`), week(" +  bkcjetable + ".`交易日期`)," +  bkcjetable + ".`代码`\r\n" + 
				"order by year(" +  bkcjetable + ".`交易日期`), week(" +  bkcjetable + ".`交易日期`), " +  bkcjetable + ".`代码`) Z \r\n" + 
				"         \r\n" + 
				"where Y.year = Z. year\r\n" + 
				"    	and Y.week = Z.week\r\n" + 
				"order by Y.year, Y.week"
				;
		 }
		
		try{
			logger.debug(sqlquerystat);
			logger.debug("为个股" + stockofbk.getMyOwnCode()  + "寻找从" + selecteddatestart.toString() + "到" + selecteddateend.toString() + "在" + bankuai.getMyOwnCode() + "的占比数据！");
		} catch (java.lang.NullPointerException e) {
			
		}
		

		CachedRowSetImpl rsfg = null;

		try {  
			 rsfg = connectdb.sqlQueryStatExecute(sqlquerystat);

			 while(rsfg.next()) {
				double stcokcje = rsfg.getDouble("stock_amount");
				double bkcje = rsfg.getDouble("category_amount");
				double stcokcjl = rsfg.getDouble("stock_vol");
				double bkcjl = rsfg.getDouble("category_vol");
				java.sql.Date  lastdayofweek = rsfg.getDate("EndOfWeekDate");
				ZonedDateTime zdtime = lastdayofweek.toLocalDate().with(DayOfWeek.FRIDAY).atStartOfDay(ZoneOffset.UTC);
				org.jfree.data.time.Week recordwk = new org.jfree.data.time.Week (lastdayofweek);
				
				NodeGivenPeriodDataItem sobperiodrecord = new NodeGivenPeriodDataItemForJFC( stockofbk.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
						recordwk, 0.0, 0.0,  0.0, 0.0, 
						stcokcjl, stcokcje );
//				NodeGivenPeriodDataItem sobperiodrecord = new NodeGivenPeriodDataItemForTA4J( stockofbk.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
//						zdtime, DoubleNum.valueOf(0.0), DoubleNum.valueOf(0.0),  DoubleNum.valueOf(0.0),  DoubleNum.valueOf(0.0), 
//						PrecisionNum.valueOf(stcokcjl), PrecisionNum.valueOf(stcokcje) );
				
				sobperiodrecord.setUplevelChengJiaoEr(bkcje);
				sobperiodrecord.setUplevelChengJiaoLiang(bkcjl);
			
				nodedayperioddata.addNewXPeriodData(sobperiodrecord);
			}
		}catch(java.lang.NullPointerException e){ 
			e.printStackTrace();
//			logger.debug( "数据库连接为NULL!");
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			try {
				if(rsfg != null)
					rsfg.close();
//				rsfx.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rsfg = null;
			nodedayperioddata = null;
//			rsfx = null;
		}
		
		//上面是个股对板块的制定周期(默认是周线)，下面部分是为个股自身周线数据，用来计算个股对大盘的数据
//		StockOfBanKuai stockofbankuai = bkcode.getBanKuaiGeGu(stock.getMyOwnCode());
		
//		this.getStockZhanBi (stock.getStock(), selecteddatestart, selecteddateend,addposition,period);
		
		return stockofbk;
	}
	/*
	 * //上面是个股对板块的指定周期(默认是周线)，下面部分是为个股自身周线数据，用来计算个股对大盘的数据
	 */
	public Stock getStockZhanBi(Stock stock,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{
		if(stock == null)
			return null;
		
		selecteddatestart = selecteddatestart.with(DayOfWeek.MONDAY);
		
		if(period.equals(NodeGivenPeriodDataItem.DAY)) //调用日线查询函数
			; 
		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //调用月线查询函数
			;
		
		NodeXPeriodData nodewkperioddata = stock.getNodeXPeroidData(period);
		if( nodewkperioddata.isInNotCalWholeWeekMode() == null)
			selecteddateend = selecteddateend.with(DayOfWeek.FRIDAY);

		String stockcode = stock.getMyOwnCode();
		String bkcjltable;
		if(sysconfig.isShangHaiStock(stockcode))
			bkcjltable = "通达信上交所股票每日交易信息";
		else
			bkcjltable = "通达信深交所股票每日交易信息";
		
		String formatedstartdate = selecteddatestart.toString();
		String formatedenddate  = selecteddateend.toString();
		
		//包含成交量和成交额的SQL
		String sqlquerystat = "SELECT YEAR(t.workday) AS CALYEAR, WEEK(t.workday,1) AS CALWEEK, M.BKCODE AS BKCODE, \r\n"
				+ "t.StartOfWeekDate AS StartOfWeekDate, t.EndOfWeekDate AS EndOfWeekDate, \r\n" +
				 "M.板块周交易额 as 板块周交易额, SUM(T.AMO) AS 大盘周交易额 ,  M.板块周交易额/SUM(T.AMO) AS CJE占比, \r\n" +
				"M.板块周交易量 as 板块周交易量, SUM(T.VOL) AS 大盘周交易量 ,  M.板块周交易量/SUM(T.VOL) AS VOL占比, \r\n" +
				"M.板块周换手率 as 板块周换手率, M.板块周自由流通换手率 AS 板块周自由流通换手率, \r\n"
				+ "M.总市值/M.SHIZHIJILUTIAOSHU   as 周平均总市值, "
				+ "M.总流通市值/M.SHIZHIJILUTIAOSHU   as 周平均流通市值, "
				+ "M.JILUTIAOSHU , M.SHIZHIJILUTIAOSHU  , "
				+ "M.周最大涨跌幅,M.周最小涨跌幅   ,M.涨停,M.跌停        \r\n" +
				 
				"FROM\r\n" + 
				"(\r\n" + 
				"select 通达信板块每日交易信息.`交易日期` as workday, "
				+ " 	DATE(通达信板块每日交易信息.交易日期 + INTERVAL (2 - DAYOFWEEK(通达信板块每日交易信息.交易日期)) DAY) as StartOfWeekDate, \r\n"
				+ "		DATE(通达信板块每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信板块每日交易信息.交易日期)) DAY) as EndOfWeekDate, \r\n" + 
				"		sum(通达信板块每日交易信息.`成交额`) AS AMO , sum(通达信板块每日交易信息.`成交量`) AS VOL \r\n" + 
				"from 通达信板块每日交易信息\r\n" + 
				"where 代码 = '999999'\r\n" + 
				" AND 通达信板块每日交易信息.`交易日期` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n"+
				" group by YEARWEEK(通达信板块每日交易信息.`交易日期`,2)\r\n" + 
				"\r\n" + 
				"UNION ALL\r\n" + 
				"\r\n" + 
				"select  通达信交易所指数每日交易信息.`交易日期` as workday,   "
				+"		DATE(通达信交易所指数每日交易信息.交易日期 + INTERVAL (2 - DAYOFWEEK(通达信交易所指数每日交易信息.交易日期)) DAY) as StartOfWeekDate, \r\n"
				+" 		DATE(通达信交易所指数每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信交易所指数每日交易信息.交易日期)) DAY) as EndOfWeekDate, \r\n" + 
				"		sum(通达信交易所指数每日交易信息.`成交额`) AS AMO, sum(通达信交易所指数每日交易信息.`成交量`) AS VOL \r\n" + 
				"from 通达信交易所指数每日交易信息\r\n" + 
				"where 代码 = '399001' "
				+" AND 通达信交易所指数每日交易信息.`交易日期` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n"+ 
				"group by YEARWEEK(通达信交易所指数每日交易信息.`交易日期`,2)\r\n" + 
				") T,\r\n" + 
				"\r\n" + 
				"(select " + bkcjltable + ".`代码` AS BKCODE, " + bkcjltable + ".`交易日期` as workday,  "
						+ " sum( " + bkcjltable + ".`成交额`) AS 板块周交易额 , \r\n"
						+ " sum( " + bkcjltable + ".`成交量`) /100 AS 板块周交易量,  \r\n"
						+ " sum( " + bkcjltable + ".`换手率`) AS 板块周换手率 , \r\n"
						+ " sum( " + bkcjltable + ".`自由流通换手率`) AS 板块周自由流通换手率 , \r\n"
						+ " sum( " + bkcjltable + ".`总市值`) AS 总市值 , \r\n"
						+ " sum( " + bkcjltable + ".`流通市值`) AS 总流通市值, \r\n"
						+ "  COUNT(1) as JILUTIAOSHU ,\r\n"
						+ "  COUNT(" + bkcjltable + ".`总市值`) AS SHIZHIJILUTIAOSHU,"
						+ ""
						+ "max(" + bkcjltable + ".`涨跌幅`) as 周最大涨跌幅, \r\n"
						+ "min(" + bkcjltable + ".`涨跌幅`) as 周最小涨跌幅, \r\n"
						+ "COUNT( IF(" + bkcjltable + ".`涨跌幅` >= 9.0,1,NULL) ) AS 涨停, \r\n"
						+ "COUNT( IF(" + bkcjltable + ".`涨跌幅` <= -9.0,1,NULL) ) AS 跌停  \r\n"
						+ " from " + bkcjltable + "\r\n" +  
				" Where 代码 = '" + stockcode + "'\r\n" + 
				" AND " + bkcjltable + ".`交易日期` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" +
				" GROUP BY  YEARWEEK( " + bkcjltable +".`交易日期`,2)\r\n" + 
				") M\r\n" + 
				" WHERE   YEARWEEK(T.WORKDAY,2) = YEARWEEK(M.WORKDAY,2)\r\n" + 
				" GROUP BY YEARWEEK(t.workday,2)"
				;
				
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		Boolean hasfengxiresult = false;
		try {
			while(rs.next()) {
				java.sql.Date startdayofweek = rs.getDate("StartOfWeekDate");
				java.sql.Date lastdayofweek = rs.getDate("EndOfWeekDate");
				
				double stockcje = rs.getDouble("板块周交易额") ;
				double dapancje = rs.getDouble("大盘周交易额") ;
				double cjezb = rs.getDouble("CJE占比") ;
				double stockcjl = rs.getDouble("板块周交易量") ;
				double dapancjl = rs.getDouble("大盘周交易量") ;
				double cjlzb = rs.getDouble("VOL占比") ;
				double huanshoulv = rs.getDouble("板块周换手率") ;
				double huanshoulvfree = rs.getDouble("板块周自由流通换手率") ;
				double pingjunzongshizhi = rs.getDouble("周平均总市值") ;
				double pingjunliutongshizhi = rs.getDouble("周平均流通市值") ;
				double periodhighestzhangdiefu = rs.getDouble("周最大涨跌幅") ;
				double periodlowestzhangdiefu = rs.getDouble("周最小涨跌幅") ;
				int exchengdaysnumber = rs.getInt("JILUTIAOSHU") ;
				int zhangtingnum = rs.getInt("涨停") ;
				int dietingnum = rs.getInt("跌停") ;
				
				org.jfree.data.time.Week recordwk = new org.jfree.data.time.Week (lastdayofweek);

				NodeGivenPeriodDataItem stockperiodrecord = new NodeGivenPeriodDataItemForJFC( stock.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
						recordwk, 0.0, 0.0,  0.0,  0.0, 
						stockcjl, stockcje );
				
//				NodeGivenPeriodDataItem stockperiodrecord = new NodeGivenPeriodDataItemForTA4J( stock.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
//						zdtime, PrecisionNum.valueOf(0.0), PrecisionNum.valueOf(0.0),  PrecisionNum.valueOf(0.0),  PrecisionNum.valueOf(0.0), 
//						PrecisionNum.valueOf(stockcjl), PrecisionNum.valueOf(stockcje) );
				
				stockperiodrecord.setNodeToDpChenJiaoErZhanbi(cjezb);
				stockperiodrecord.setNodeToDpChenJiaoLiangZhanbi(cjlzb);
				stockperiodrecord.setUplevelChengJiaoEr(dapancje);
				stockperiodrecord.setUplevelChengJiaoLiang(dapancjl);
				stockperiodrecord.setPeriodHighestZhangDieFu(periodhighestzhangdiefu);
				stockperiodrecord.setPeriodLowestZhangDieFu(periodlowestzhangdiefu);
				stockperiodrecord.setExchangeDaysNumber(exchengdaysnumber);
				stockperiodrecord.setZhangTingNumber(zhangtingnum);
				stockperiodrecord.setDieTingNumber(dietingnum);
				stockperiodrecord.setHuanShouLv(huanshoulv);
				stockperiodrecord.setHuanShouLvFree(huanshoulvfree);
				stockperiodrecord.setLiuTongShiZhi(pingjunliutongshizhi);
				stockperiodrecord.setZongShiZhi(pingjunzongshizhi);
				
				nodewkperioddata.addNewXPeriodData(stockperiodrecord);
				
				stockperiodrecord = null;
			}
		} catch(java.lang.NullPointerException e){ e.printStackTrace();
	    } catch(Exception e){e.printStackTrace();
	    } finally {
	    	try { rs.close(); rs = null;} catch (SQLException e) {e.printStackTrace();}
	    	bkcjltable = null;
	    	nodewkperioddata = null; 
	    }

		return stock;
	}
	public BanKuai getBanKuaiGeGuZhanBi(BanKuai bk,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{
		if(period.equals(NodeGivenPeriodDataItem.DAY)) //调用日线查询函数
			; 
		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //调用月线查询函数
			;
		
		getBanKuaiGeGuZhanBiByJys( bk, selecteddatestart, selecteddateend, period,"SH");
		getBanKuaiGeGuZhanBiByJys( bk, selecteddatestart, selecteddateend, period,"SZ");
		
		return bk;
	}
	/*
	 * 
	 */
	private BanKuai getBanKuaiGeGuZhanBiByJys(BanKuai bk,LocalDate selecteddatestart,LocalDate selecteddateend,String period,String jys)
	{
		List<BkChanYeLianTreeNode> bkshgegu = bk.getAllGeGuOfBanKuaiInHistoryByJiaoYiSuo(jys);
		if(bkshgegu == null  || bkshgegu.isEmpty())
			return bk;
		
		String bkggstr = "";
		for(BkChanYeLianTreeNode stockofbk : bkshgegu) {
			//系统后台如果自动下载了个股的数据，再在这里做一次，浪费时间，要判断
//			Stock stock = (Stock) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(stockofbk.getMyOwnCode(), BkChanYeLianTreeNode.TDXGG);
			Stock stock = ((StockOfBanKuai)stockofbk).getStock();
			if(stock == null) continue;
			
			NodeXPeriodData nodewkperioddata = stock.getNodeXPeroidData(period);
			if(nodewkperioddata.getAmoRecordsStartDate() != null) //如果有数据，就不做了，至于数据是否完整，后面有个股自己的同步来保证。这样简单点。
				continue;
			
			bkggstr  = bkggstr + "'" + stockofbk.getMyOwnCode() +  "', ";
		}
		if(bkggstr.isBlank() )
			return bk;
		bkggstr = bkggstr.substring(0, bkggstr.length() -2);
		
		String bkcjltable;
		if(jys.equalsIgnoreCase("SH"))
			bkcjltable = "通达信上交所股票每日交易信息";
		else
			bkcjltable = "通达信深交所股票每日交易信息";
		
		String formatedstartdate = selecteddatestart.toString();
		String formatedenddate  = selecteddateend.toString();
		//包含成交量和成交额的SQL
				String sqlquerystat = "SELECT YEAR(t.workday) AS CALYEAR, WEEK(t.workday,1) AS CALWEEK, M.BKCODE AS BKCODE, \r\n"
						+ "t.StartOfWeekDate AS StartOfWeekDate, t.EndOfWeekDate AS EndOfWeekDate, \r\n" +
						 "M.板块周交易额 as 板块周交易额, SUM(T.AMO) AS 大盘周交易额 ,  M.板块周交易额/SUM(T.AMO) AS CJE占比, \r\n" +
						"M.板块周交易量 as 板块周交易量, SUM(T.VOL) AS 大盘周交易量 ,  M.板块周交易量/SUM(T.VOL) AS VOL占比, \r\n" +
						"M.板块周换手率 as 板块周换手率, M.板块周自由流通换手率 AS 板块周自由流通换手率, \r\n"
						+ "M.总市值/M.SHIZHIJILUTIAOSHU   as 周平均总市值, "
						+ "M.总流通市值/M.SHIZHIJILUTIAOSHU   as 周平均流通市值, \r\n"
						+ "M.JILUTIAOSHU , M.SHIZHIJILUTIAOSHU  , \r\n"
						+ "M.周最大涨跌幅,M.周最小涨跌幅   ,M.涨停,M.跌停        \r\n" +
						 
						"FROM\r\n" + 
						"(\r\n" + 
						"select 通达信板块每日交易信息.`交易日期` as workday, "
						+ " 	DATE(通达信板块每日交易信息.交易日期 + INTERVAL (2 - DAYOFWEEK(通达信板块每日交易信息.交易日期)) DAY) as StartOfWeekDate, \r\n"
						+ "		DATE(通达信板块每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信板块每日交易信息.交易日期)) DAY) as EndOfWeekDate, \r\n" + 
						"		sum(通达信板块每日交易信息.`成交额`) AS AMO , sum(通达信板块每日交易信息.`成交量`) AS VOL \r\n" + 
						"from 通达信板块每日交易信息\r\n" + 
						"where 代码 = '999999'\r\n" + 
						" AND 通达信板块每日交易信息.`交易日期` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n"+
						" group by YEARWEEK(通达信板块每日交易信息.`交易日期`,2)\r\n" + 
						"\r\n" + 
						"UNION ALL\r\n" + 
						"\r\n" + 
						"select  通达信交易所指数每日交易信息.`交易日期` as workday,   "
						+"		DATE(通达信交易所指数每日交易信息.交易日期 + INTERVAL (2 - DAYOFWEEK(通达信交易所指数每日交易信息.交易日期)) DAY) as StartOfWeekDate, \r\n"
						+" 		DATE(通达信交易所指数每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信交易所指数每日交易信息.交易日期)) DAY) as EndOfWeekDate, \r\n" + 
						"		sum(通达信交易所指数每日交易信息.`成交额`) AS AMO, sum(通达信交易所指数每日交易信息.`成交量`) AS VOL \r\n" + 
						"from 通达信交易所指数每日交易信息\r\n" + 
						"where 代码 = '399001' "
						+" AND 通达信交易所指数每日交易信息.`交易日期` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n"+ 
						"group by YEARWEEK(通达信交易所指数每日交易信息.`交易日期`,2)\r\n" + 
						") T,\r\n" + 
						"\r\n" + 
						"(select " + bkcjltable + ".`代码` AS BKCODE, " + bkcjltable + ".`交易日期` as workday,  "
								+ " sum( " + bkcjltable + ".`成交额`) AS 板块周交易额 , \r\n"
								+ " sum( " + bkcjltable + ".`成交量`) /100 AS 板块周交易量,  \r\n"
								+ " sum( " + bkcjltable + ".`换手率`) AS 板块周换手率 , \r\n"
								+ " sum( " + bkcjltable + ".`自由流通换手率`) AS 板块周自由流通换手率 , \r\n"
								+ " sum( " + bkcjltable + ".`总市值`) AS 总市值 , \r\n"
								+ " sum( " + bkcjltable + ".`流通市值`) AS 总流通市值, \r\n"
								+ "  COUNT(1) as JILUTIAOSHU ,\r\n"
								+ "  COUNT(" + bkcjltable + ".`总市值`) AS SHIZHIJILUTIAOSHU,"
								+ ""
								+ "max(" + bkcjltable + ".`涨跌幅`) as 周最大涨跌幅, \r\n"
								+ "min(" + bkcjltable + ".`涨跌幅`) as 周最小涨跌幅, \r\n"
								+ "COUNT( IF(" + bkcjltable + ".`涨跌幅` >= 9.0,1,NULL) ) AS 涨停, \r\n"
								+ "COUNT( IF(" + bkcjltable + ".`涨跌幅` <= -9.0,1,NULL) ) AS 跌停  \r\n"
								+ " from " + bkcjltable + "\r\n" +  
						" Where 代码  IN (" + bkggstr + " ) \r\n" + 
						" AND " + bkcjltable + ".`交易日期` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" +
						" GROUP BY 代码,  YEARWEEK( " + bkcjltable +".`交易日期`,2)\r\n" + 
						") M\r\n" + 
						" WHERE   YEARWEEK(T.WORKDAY,2) = YEARWEEK(M.WORKDAY,2)\r\n" + 
						" GROUP BY bkcode,  YEARWEEK(t.workday,2)"
						;
		CachedRowSetImpl rs = null; Stock stock = null; String curstockcode = ""; NodeXPeriodData nodewkperioddata = null;
		try {
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			while(rs.next()) {
				String stockcode = rs.getString("bkcode");
				if(!curstockcode.equalsIgnoreCase(stockcode) )  {
					curstockcode = stockcode;
					stock = (Stock) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(stockcode, BkChanYeLianTreeNode.TDXGG);
					if(stock == null) continue;
					else nodewkperioddata = stock.getNodeXPeroidData(period);
				}
				java.sql.Date startdayofweek = rs.getDate("StartOfWeekDate");
				java.sql.Date lastdayofweek = rs.getDate("EndOfWeekDate");
				double stockcje = rs.getDouble("板块周交易额") ;
				double dapancje = rs.getDouble("大盘周交易额") ;
				double cjezb = rs.getDouble("CJE占比") ;
				double stockcjl = rs.getDouble("板块周交易量") ;
				double dapancjl = rs.getDouble("大盘周交易量") ;
				double cjlzb = rs.getDouble("VOL占比") ;
				double huanshoulv = rs.getDouble("板块周换手率") ;
				double huanshoulvfree = rs.getDouble("板块周自由流通换手率");
				double pingjunzongshizhi = rs.getDouble("周平均总市值") ;
				double pingjunliutongshizhi = rs.getDouble("周平均流通市值") ;
				double periodhighestzhangdiefu = rs.getDouble("周最大涨跌幅") ;
				double periodlowestzhangdiefu = rs.getDouble("周最小涨跌幅") ;
				int exchengdaysnumber = rs.getInt("JILUTIAOSHU") ;
				int zhangtingnum = rs.getInt("涨停") ;
				int dietingnum = rs.getInt("跌停") ;
				
				org.jfree.data.time.Week recordwk = new org.jfree.data.time.Week (lastdayofweek);

				NodeGivenPeriodDataItem stockperiodrecord = new NodeGivenPeriodDataItemForJFC( stock.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
						recordwk, 0.0, 0.0,  0.0,  0.0, 
						stockcjl, stockcje );
				
//				NodeGivenPeriodDataItem stockperiodrecord = new NodeGivenPeriodDataItemForTA4J( stock.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
//						zdtime, PrecisionNum.valueOf(0.0), PrecisionNum.valueOf(0.0),  PrecisionNum.valueOf(0.0),  PrecisionNum.valueOf(0.0), 
//						PrecisionNum.valueOf(stockcjl), PrecisionNum.valueOf(stockcje) );
				
				stockperiodrecord.setNodeToDpChenJiaoErZhanbi(cjezb);
				stockperiodrecord.setNodeToDpChenJiaoLiangZhanbi(cjlzb);
				stockperiodrecord.setUplevelChengJiaoEr(dapancje);
				stockperiodrecord.setUplevelChengJiaoLiang(dapancjl);
				stockperiodrecord.setPeriodHighestZhangDieFu(periodhighestzhangdiefu);
				stockperiodrecord.setPeriodLowestZhangDieFu(periodlowestzhangdiefu);
				stockperiodrecord.setExchangeDaysNumber(exchengdaysnumber);
				stockperiodrecord.setZhangTingNumber(zhangtingnum);
				stockperiodrecord.setDieTingNumber(dietingnum);
				stockperiodrecord.setHuanShouLv(huanshoulv);
				stockperiodrecord.setHuanShouLvFree(huanshoulvfree);
				stockperiodrecord.setLiuTongShiZhi(pingjunliutongshizhi);
				stockperiodrecord.setZongShiZhi(pingjunzongshizhi);
				
				nodewkperioddata.addNewXPeriodData(stockperiodrecord);
				
				stockperiodrecord = null;

			}
		} catch(java.lang.NullPointerException e){ e.printStackTrace();
	    } catch(Exception e){e.printStackTrace();
	    } finally {
	    	try { rs.close(); rs = null;	} catch (SQLException e) {e.printStackTrace();}
	    	bkcjltable = null;
	    }

		return bk;
	}
	/**
	 * 
	 */
//	public Stock getStockGuanZhuJiLu (Stock stock,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
//	{
//		if(stock == null)
//			return null;
//		
//		selecteddatestart = selecteddatestart.with(DayOfWeek.MONDAY);
//		if(!stock.isNodeDataAtNotCalWholeWeekMode())
//			selecteddateend = selecteddateend.with(DayOfWeek.FRIDAY);
//		
//		if(period.equals(NodeGivenPeriodDataItem.DAY)) //调用日线查询函数
//			; 
//		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //调用月线查询函数
//			;
//		
//		NodeXPeriodData nodewkperioddata = stock.getNodeXPeroidData(period);
//
//		String stockcode = stock.getMyOwnCode();
//		String guanzhubk = sysconfig.getCurZdyBanKuaiOfGuanZhuGeGu();
//	    String sqlquerystat6="SELECT zdgz.加入时间   AS 日期,"
//					+ "'加入关注' AS ACTION ,"
//					+ "null, "
//					+ "null,"
//					+ "null,"
//					+ "'操作记录重点关注'"
//					+ " FROM 股票通达信自定义板块对应表  zdgz" 
//					+ " WHERE zdgz.股票代码 ="  + "'" + stockcode + "'"
//					+ " AND 自定义板块="  + "'" + guanzhubk + "'" 
//
//					+ " \n UNION ALL \n"
//					;
//	     String sqlquerystat7="SELECT zdgz.移除时间 AS 日期 ,"
//					+ "'移除关注' AS ACTION,"
//					+ "null, "
//					+ "null,"
//					+ "null,"
//					+ "'操作记录重点关注'"
//					+ " FROM 股票通达信自定义板块对应表  zdgz" 
//					+ " WHERE zdgz.股票代码 ="  + "'" + stockcode + "'"
//					+ " AND 自定义板块="  + "'" + guanzhubk + "'" 
//					+ " AND zdgz.移除时间 IS NOT NULL \n"
//					+ " ORDER BY 1 DESC,5,3 " //desc
//					;
//	     String sqlquerystatgz = sqlquerystat6 + sqlquerystat7;
//		
//		//关注数据
//		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystatgz);
//		try {
//			while(rs.next()) {
//				java.sql.Date recdate = rs.getDate("日期");
//				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(recdate);
//				
//				String action = rs.getString("ACTION").trim();
//				if(action.equals("加入关注"))
//					((StockNodesXPeriodData)nodewkperioddata).addGzjlToPeriod(wknum, 1);
//				else if(action.equals("移除关注"))
//					((StockNodesXPeriodData)nodewkperioddata).addGzjlToPeriod(wknum, 0);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//	    	try {
//				rs.close();
//				rs = null;
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//	    }
//		
//		return stock;
//	}
	/*
	 * 
	 */
	public Stock getStockFengXiJieGuo (Stock stock,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{
		if(stock == null)
			return null;
		
		selecteddatestart = selecteddatestart.with(DayOfWeek.MONDAY);
		
		if(period.equals(NodeGivenPeriodDataItem.DAY)) //调用日线查询函数
			; 
		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //调用月线查询函数
			;
		
		NodeXPeriodData nodewkperioddata = stock.getNodeXPeroidData(period);
		if( nodewkperioddata.isInNotCalWholeWeekMode() == null )
			selecteddateend = selecteddateend.with(DayOfWeek.FRIDAY);

		String stockcode = stock.getMyOwnCode();
		
		String sqlquerystatfx = "SELECT 操作记录重点关注.`日期`,    COUNT(*) AS RESULT FROM 操作记录重点关注 \r\n" + 
				" WHERE 股票代码='" + stockcode + "'" + "\r\n" + 
				" AND 操作记录重点关注.`日期` between '" + selecteddatestart + "' AND '" + selecteddateend + "' \r\n" +
				" GROUP BY YEAR(操作记录重点关注.`日期`), WEEK(操作记录重点关注.`日期`) "
				;
		
//		 String sqlquerystat5="SELECT zdgz.日期   AS 日期,"
//					+ "zdgz.加入移出标志 AS ACTION,"
//					+ "zdgz.原因描述,"
//					+ "zdgz.ID,"
//					+ "null,"
//					+ "'操作记录重点关注'"
//					+ " FROM 操作记录重点关注  zdgz"
//					+ " WHERE zdgz.股票代码 =" + "'" + stockcode + "'"
//					;
		
		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sqlquerystatfx);
		try {
			while(rsfx.next()) {
				java.sql.Date recdate = rsfx.getDate("日期");
				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(recdate);
				Integer reccount = rsfx.getInt("RESULT");
				nodewkperioddata.addFxjgToPeriod(wknum, reccount);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	    	try {
				rsfx.close();
				rsfx = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
		
		return stock;
	}
	/*
	 * 
	 */
//	public Stock getStockMaiRuMaiChuJiLu (Stock stock,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
//	{
//		if(stock == null)
//			return null;
//		
//		selecteddatestart = selecteddatestart.with(DayOfWeek.MONDAY);
//		if(!stock.isNodeDataAtNotCalWholeWeekMode())
//			selecteddateend = selecteddateend.with(DayOfWeek.FRIDAY);
//		
//		if(period.equals(NodeGivenPeriodDataItem.DAY)) //调用日线查询函数
//			; 
//		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //调用月线查询函数
//			;
//		
//		NodeXPeriodData nodewkperioddata = stock.getNodeXPeroidData(period);
//
//		String stockcode = stock.getMyOwnCode();
//
//		String sqlquerystat2="SELECT czjl.日期, " +		""
//				+ "IF(czjl.买入卖出标志,'买入','卖出')  AS 买卖,"
//				+ " czjl.原因描述,"
//				+ " czjl.ID,"
//				+ " czjl.买卖账号,"
//				+ "'操作记录买卖'" 
//				+ " FROM 操作记录买卖 czjl "
//				+ "	WHERE czjl.股票代码 =" + "'" + stockcode + "'"
//				+ " AND czjl.买卖金额 > 0 "
//				+ " AND czjl.挂单 = FALSE"  
//				+ "  AND `日期`  between '" + selecteddatestart + "' AND '" + selecteddateend + "' \r\n" 
//				
//				+ " \n UNION ALL \n" 
//				;
//
//		String sqlquerystat3="SELECT rqczjl.日期,"
//				+ " IF(rqczjl.买入卖出标志,'买入','卖出')  AS 买卖,"
//				+ " rqczjl.原因描述,"
//				+ " rqczjl.ID,"
//				+ " rqczjl.买卖账号,"
//				+ " '操作记录融券买卖'"
//				+ " FROM 操作记录融券买卖 rqczjl"
//				+ "	WHERE rqczjl.股票代码 =" + "'" + stockcode + "'"
//				+ " AND rqczjl.买卖金额 > 0 "
//				+ " AND rqczjl.挂单 = FALSE"
//				+ "  AND `日期`  between '" + selecteddatestart + "' AND '" + selecteddateend + "' \r\n" 
//				
//				+ " \n UNION ALL \n"
//				;
//
//		String sqlquerystat4="SELECT rzczjl.日期,"
//				+ "IF(rzczjl.买入卖出标志,'买入','卖出')  AS 买卖,"
//				+ "rzczjl.原因描述,"
//				+ "rzczjl.ID,"
//				+ "rzczjl.买卖账号,"
//				+ "'操作记录融资买卖'"
//				+ " FROM 操作记录融资买卖 rzczjl"
//				+ " WHERE rzczjl.股票代码 =" + "'" + stockcode + "'"
//				+ " AND rzczjl.买卖金额 > 0 "
//				+ " AND rzczjl.挂单 = FALSE"
//				+ "  AND `日期`  between '" + selecteddatestart + "' AND '" + selecteddateend + "' \r\n" 
//				;
//		String sqlquerystatfx = sqlquerystat2 + sqlquerystat3 + sqlquerystat4;
//		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sqlquerystatfx);
//		try {
//			while(rsfx.next()) {
//				java.sql.Date recdate = rsfx.getDate("日期");
//				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(recdate);
//				String maimai = rsfx.getString("买卖");
//				if(maimai.equals("买入")) {
//					((StockNodesXPeriodData)nodewkperioddata).addMaiRuJiLu(wknum, 1);
//				} else
//				if(maimai.equals("卖出")) {
//					((StockNodesXPeriodData)nodewkperioddata).addMaiChuJiLu(wknum, 0);
//				}
//			}
//		} catch (SQLException e) {e.printStackTrace();
//		} finally {
//	    	try {rsfx.close();rsfx = null;} catch (SQLException e) {e.printStackTrace();}
//	    }
//		
//		return stock;
//	}

	/*
	 * 这个函数计算出某个不固定周期个股和大盘的占比数据
	 */
	public NodeGivenPeriodDataItem getStockNoFixPerioidZhanBiByWeek(Stock stock,LocalDate formatedstartdate,LocalDate formatedenddate,String period)
	{
		if(period.equals(NodeGivenPeriodDataItem.DAY)) //调用日线查询函数
			; 
		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //调用月线查询函数
			;
		
//		StockNodeXPeriodData nodewkperioddata = (StockNodeXPeriodData)stock.getNodeXPeroidData(period);
	
		String stockcode = stock.getMyOwnCode();
		String bkcjltable;
//		if(stockcode.startsWith("6"))
		if( sysconfig.isShangHaiStock(stockcode) )
			bkcjltable = "通达信上交所股票每日交易信息";
		else
			bkcjltable = "通达信深交所股票每日交易信息";
	
		
		String sqlquerystat = "SELECT  M.BKCODE AS BKCODE, '" + formatedstartdate + "' AS STARTDAY, '" + formatedenddate + "' AS ENDDAY , " +
				 "M.板块周交易额 as 板块周交易额, SUM(T.AMO) AS 大盘周交易额 ,  M.板块周交易额/SUM(T.AMO) AS 占比, \r\n" +
				"M.板块周交易量 as 板块周交易量, SUM(T.VOL) AS 大盘周交易量 ,  M.板块周交易量/SUM(T.VOL) AS VOL占比, \r\n" +
				"M.板块周换手率 as 板块周换手率, M.总市值/M.JILUTIAOSHU as 周平均总市值, M.总流通市值/M.JILUTIAOSHU as 周平均流通市值, M.JILUTIAOSHU , M.周最大涨跌幅,M.周最小涨跌幅      \r\n" +
				 
				"FROM\r\n" + 
				"(\r\n" + 
				"select  通达信板块每日交易信息.`交易日期` as workday, DATE(通达信板块每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信板块每日交易信息.交易日期)) DAY) as EndOfWeekDate, \r\n" + 
				"		  sum(通达信板块每日交易信息.`成交额`) AS AMO , sum(通达信板块每日交易信息.`成交量`) AS VOL \r\n" + 
				"from 通达信板块每日交易信息\r\n" + 
				"WHERE 代码 = '999999'\r\n" + 
				"AND  通达信板块每日交易信息.`交易日期` BETWEEN'" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" + 
				"\r\n" + 
				"UNION ALL\r\n" + 
				"\r\n" + 
				"select  通达信交易所指数每日交易信息.`交易日期` as workday,   DATE(通达信交易所指数每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信交易所指数每日交易信息.交易日期)) DAY) as EndOfWeekDate, \r\n" + 
				"			sum(通达信交易所指数每日交易信息.`成交额`) AS AMO, sum(通达信交易所指数每日交易信息.`成交量`) AS VOL \r\n" + 
				"from 通达信交易所指数每日交易信息\r\n" + 
				"where 代码 = '399001'\r\n" + 
				"AND 通达信交易所指数每日交易信息.`交易日期` BETWEEN'" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" + 
				") T,\r\n" + 
				"\r\n" + 
				"(select " + bkcjltable + ".`代码` AS BKCODE, " 
						+ " sum( " + bkcjltable + ".`成交额`) AS 板块周交易额 , \r\n"
						+ " sum( " + bkcjltable + ".`成交量`) AS 板块周交易量,  \r\n"
						+ " sum( " + bkcjltable + ".`换手率`) AS 板块周换手率 , \r\n"
						+ " sum( " + bkcjltable + ".`总市值`) AS 总市值 , \r\n"
						+ " sum( " + bkcjltable + ".`流通市值`) AS 总流通市值, \r\n"
						+ "  count(1) as JILUTIAOSHU ,\r\n"
						+ "max(" + bkcjltable + ".`涨跌幅`) as 周最大涨跌幅, \r\n"
						+ "min(" + bkcjltable + ".`涨跌幅`) as 周最小涨跌幅 \r\n"
						+ " from " + bkcjltable + "\r\n" +  
				"where 代码 = '" + stockcode + "'\r\n" + 
				"AND " + bkcjltable + ".`交易日期` BETWEEN'" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" + 
				") M\r\n"  
				;
		logger.debug(sqlquerystat); 
		//交易数据
		CachedRowSetImpl rs = null;
		NodeGivenPeriodDataItem stockperiodrecord = null;
		try{
				rs = connectdb.sqlQueryStatExecute(sqlquerystat);
				while(rs.next()) {
					double stockcje = rs.getDouble("板块周交易额");
					double dapancje = rs.getDouble("大盘周交易额");
//					java.sql.Date lastdayofweek = rs.getDate("STARTDAY");
					java.sql.Date lastdayofweek =  java.sql.Date.valueOf(formatedstartdate);
					ZonedDateTime zdtime = lastdayofweek.toLocalDate().with(DayOfWeek.FRIDAY).atStartOfDay(ZoneOffset.UTC);
					org.jfree.data.time.Week recordwk = new org.jfree.data.time.Week (lastdayofweek);
					double stockcjl = rs.getDouble("板块周交易量");
					double dapancjl = rs.getDouble("大盘周交易量");
					double huanshoulv = rs.getDouble("板块周换手率");
					double pingjunzongshizhi = rs.getDouble("周平均总市值");
					double pingjunliutongshizhi = rs.getDouble("周平均流通市值");
					double periodhighestzhangdiefu = rs.getDouble("周最大涨跌幅");
					double periodlowestzhangdiefu = rs.getDouble("周最小涨跌幅");
					int exchengdaysnumber = rs.getInt("JILUTIAOSHU");
		
					stockperiodrecord = new NodeGivenPeriodDataItemForJFC( stock.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
							recordwk,0.0, 0.0,  0.0,  0.0, 
							stockcjl, stockcje );
					
//					stockperiodrecord = new NodeGivenPeriodDataItemForTA4J( stock.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
//							zdtime, DoubleNum.valueOf(0.0), DoubleNum.valueOf(0.0),  DoubleNum.valueOf(0.0),  DoubleNum.valueOf(0.0), 
//							PrecisionNum.valueOf(stockcjl), PrecisionNum.valueOf(stockcje) );
					
					stockperiodrecord.setUplevelChengJiaoEr(dapancje);
					stockperiodrecord.setUplevelChengJiaoLiang(dapancjl);
					stockperiodrecord.setPeriodHighestZhangDieFu(periodhighestzhangdiefu);
					stockperiodrecord.setPeriodLowestZhangDieFu(periodlowestzhangdiefu);
					stockperiodrecord.setExchangeDaysNumber(exchengdaysnumber);
					
//					nodewkperioddata.addNewXPeriodData(stockperiodrecord);
					
					
				}
			}catch(java.lang.NullPointerException e){ 
		    	e.printStackTrace();
		    } catch(Exception e){
		    	e.printStackTrace();
		    }  finally {
		    	try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    	
		    	bkcjltable = null;
//		    	nodewkperioddata = null; 
		    }

		return stockperiodrecord;
	}
	/*
	 * 对于个股，存在复权问题，一旦复权，数据库中存放的每日交易数据就不准确。所以每日交易数据还是从TDX导出的TXT里面读取比较准确。
	 */
	public Stock getStockDailyKXianZouShiFromCsv (Stock stock, LocalDate requiredstartday, LocalDate requiredendday, String period)
	{
		//确定需要读取的时间跨度和现有的跨度的差异值，以免重复读取已经有的数据，节约时间
		NodeXPeriodData nodedayperioddata = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
		
		TemporalField fieldCH = WeekFields.of(Locale.CHINA).dayOfWeek();
		requiredstartday = requiredstartday.with(fieldCH, 1); //确保K线总是显示完整得一周
//		LocalDate notcalwholewkmodedate = nodedayperioddata.isInNotCalWholeWeekMode();
//		if(notcalwholewkmodedate == null)	requiredendday = requiredendday.with(fieldCH, 7);
//		else requiredendday = notcalwholewkmodedate;
		
		String csvfilepath = sysconfig.getCsvPathOfExportedTDXVOLFiles();
		String stockcode = stock.getMyOwnCode();
		String jiaoyisuo = stock.getNodeJiBenMian().getSuoShuJiaoYiSuo();
//		String optTable = null;
//		if(jiaoyisuo.toLowerCase().equals("sz")  )
//			optTable = "通达信深交所股票每日交易信息";
//		else if(jiaoyisuo.toLowerCase().equals("sh") )
//			optTable = "通达信上交所股票每日交易信息";
		
		List<String> volamooutput = getTDXVolFilesRule ();
		if(volamooutput == null)
			return stock;
		String filenamerule = volamooutput.get(1);
		String csvfilename = (filenamerule.replaceAll("YY",jiaoyisuo.toUpperCase())).replaceAll("XXXXXX", stockcode).replace("TXT", "CSV") ;
		File csvfile = new File(csvfilepath + "/" + csvfilename);
		if (!csvfile.exists() || csvfile.isDirectory() || !csvfile.canRead()) {  
				logger.info("读取" + csvfilename + "发生错误！没有获得" + stock.getMyOwnName() +  "的K线数据。");
				return stock;
		} 
		
		CSVReader stockcsvreader = null;
		try {
			stockcsvreader = new CSVReaderBuilder(new FileReader(csvfilepath + "/" + csvfilename))
			        .withSkipLines(2)
			        .withCSVParser(new CSVParserBuilder().withSeparator(',').withEscapeChar('\'').build())
			        .build();
		} catch (FileNotFoundException e) {e.printStackTrace();}

		try {
			LocalDate lastdaydate = requiredstartday.with(fieldCH, 6); //
//			org.ta4j.core.TimeSeries nodenewohlc = new BaseTimeSeries.SeriesBuilder().withName(stock.getMyOwnCode()).build();
			OHLCSeries nodenewohlc = new OHLCSeries (stock.getMyOwnCode());
			int linenumber =0;
			
//			String [] linevalue ;
//			while ( (linevalue = stockcsvreader.readNext())!=null ) {
			List<String[]> records = stockcsvreader.readAll();
			for (String[] linevalue : records) {
				linenumber ++;
				
				String recordsdate = linevalue[0];
				LocalDate curlinedate;
				try {
//					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern(volamooutput.get(2).trim() );
					curlinedate = LocalDate.parse(recordsdate, formatter);
					
					if(linenumber == 1) //计算第一行的日期，给周K线的计算使用
						lastdaydate = curlinedate.with(fieldCH, 6); //
				} catch (java.time.format.DateTimeParseException e) {break;}
				
				try {
					if(curlinedate.isBefore(requiredstartday) || curlinedate.isAfter(requiredendday) ) continue;		
				} catch (java.lang.NullPointerException e) { continue;}
				// 为生成ohlc的时间做处理
				java.sql.Date sqldate = null;
				try {
//					DateFormat format = new SimpleDateFormat("ddMMyyyy", Locale.CHINA);
					String formatstr = volamooutput.get(2).trim();
					DateFormat format = new SimpleDateFormat(formatstr, Locale.CHINA);
					 sqldate = new java.sql.Date(format.parse(recordsdate).getTime()  );
				} catch (ParseException e) { e.printStackTrace(); }
				org.jfree.data.time.Day recordday = new org.jfree.data.time.Day (sqldate);
				ZonedDateTime zdtime = sqldate.toLocalDate().atStartOfDay(ZoneOffset.UTC);
					
				String open = linevalue[1];
				String high = linevalue[2];
				String low = linevalue[3];
				String close = linevalue[4];
				String cjl = linevalue[5];
				String cje = linevalue[6];
				
				NodeGivenPeriodDataItem stockperiodrecord = new NodeGivenPeriodDataItemForJFC( stock.getMyOwnCode(), NodeGivenPeriodDataItem.DAY,
						recordday, Double.valueOf( open), Double.valueOf(high), Double.valueOf( low), Double.valueOf( close), 
						Double.valueOf(cjl), Double.valueOf(cje) );
				 
//				NodeGivenPeriodDataItem stockperiodrecord = new NodeGivenPeriodDataItemForTA4J( stock.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
//						zdtime, PrecisionNum.valueOf(open), PrecisionNum.valueOf(high),  PrecisionNum.valueOf(low),  PrecisionNum.valueOf(close), 
//						PrecisionNum.valueOf(cjl), PrecisionNum.valueOf(cje) );
				 
				 nodedayperioddata.addNewXPeriodData(stockperiodrecord);
				 
				 //计算周线OHLC数据
				 WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
				 int lastdayweekNumber = lastdaydate.get(weekFields.weekOfWeekBasedYear());
				 int curweekNumber = curlinedate.get(weekFields.weekOfWeekBasedYear());
				 if(lastdayweekNumber == curweekNumber) {
//					 nodenewohlc.addBar( (NodeGivenPeriodDataItemForJFC)stockperiodrecord); //先把本周的日线数据存储起来，等待后面处理
					 nodenewohlc.add( (NodeGivenPeriodDataItemForJFC)stockperiodrecord); //先把本周的日线数据存储起来，等待后面处理
				 } else {
					 //换周了，计算上一周周线OHLC 数据
					 this.getTDXNodesWeeklyKXianZouShiForJFC(stock, lastdaydate.with(DayOfWeek.FRIDAY), nodenewohlc);
					 
					 nodenewohlc = null;
//					 nodenewohlc = new BaseTimeSeries.SeriesBuilder().withName(stock.getMyOwnCode()).build();
					 nodenewohlc = new OHLCSeries (stock.getMyOwnCode());
					 lastdaydate = curlinedate.with(DayOfWeek.FRIDAY);
					 nodenewohlc.add( (NodeGivenPeriodDataItemForJFC)stockperiodrecord);
					 stockperiodrecord = null;
				 }
					 
			}
			 //目前算法最后一周要跳出循环后才能计算
			this.getTDXNodesWeeklyKXianZouShiForJFC(stock, lastdaydate.with(DayOfWeek.FRIDAY), nodenewohlc);
			
			//Former ZhangFu can be get from Netease, but for the lastest, sometimes Neteast data is still not imported,
			//so must cal the zhangfu by CSV data.
			this.calTDXNodesWeekyHighestAndLowestZhangFuForJFC (stock, lastdaydate.with(DayOfWeek.FRIDAY), nodenewohlc);;
			
			nodenewohlc = null;
			records = null;
		} catch (IOException e) {e.printStackTrace();}

		return stock;
	}
//	public Stock getStockDailyAmoVol (Stock stock, LocalDate requiredstartday, LocalDate requiredendday, String period)
//	{
//		//本函数初始是开发为日周期的K线数据，
//				if(period.equals(NodeGivenPeriodDataItem.WEEK)) //调用周线查询函数
//					; 
//				else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //调用月线查询函数
//					;
//
//				String stockcode = stock.getMyOwnCode();
//				String bkcjltable;
////				if(stockcode.startsWith("6"))
//				if( sysconfig.isShangHaiStock(stockcode) )
//					bkcjltable = "通达信上交所股票每日交易信息";
//				else
//					bkcjltable = "通达信深交所股票每日交易信息"; 
//				
//				TemporalField fieldCH = WeekFields.of(Locale.CHINA).dayOfWeek();
//				try{
//					requiredstartday = requiredstartday.with(fieldCH, 1); //确保K线总是显示完整得一周
//				} catch (java.lang.NullPointerException e) {
//					return stock;
//				}
//				if(!stock.isNodeDataAtNotCalWholeWeekMode() )
//					requiredendday = requiredendday.with(fieldCH, 7);
//				
//				NodeXPeriodData nodedayperioddata = stock.getNodeXPeroidData(period);
//
//				String sqlquerystat = "SELECT *  FROM " + bkcjltable + " \r\n" + 
//						"WHERE 代码='" + stockcode + "'" + "\r\n" + 
//						"AND 交易日期  between'" + requiredstartday + "' AND '" + requiredendday + "'"
//						;
//				
//				CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
//				OHLCSeries nodenewohlc = new OHLCSeries (stock.getMyOwnCode());
//				try {  
//					 rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
//					
//					 while(rsfx.next()) {
//						 double cje = rsfx.getDouble("成交额");
//						 double cjl = rsfx.getDouble("成交量");
//						 java.sql.Date actiondate = rsfx.getDate("交易日期");
//						 ZonedDateTime zdtime = actiondate.toLocalDate().atStartOfDay(ZoneOffset.UTC);
//						 org.jfree.data.time.Day recordday = new org.jfree.data.time.Day (actiondate);
//						 
//						 NodeGivenPeriodDataItem bkperiodrecord = new NodeGivenPeriodDataItemForJFC( bk.getMyOwnCode(), NodeGivenPeriodDataItem.DAY,
//								 recordday, PrecisionNum.valueOf(open), PrecisionNum.valueOf(high),  PrecisionNum.valueOf(low),  PrecisionNum.valueOf(close), 
//									PrecisionNum.valueOf(cjl), PrecisionNum.valueOf(cje) );
//						 
////						 NodeGivenPeriodDataItem bkperiodrecord = new NodeGivenPeriodDataItemForTA4J( bk.getMyOwnCode(), NodeGivenPeriodDataItem.DAY,
////									zdtime, PrecisionNum.valueOf(open), PrecisionNum.valueOf(high),  PrecisionNum.valueOf(low),  PrecisionNum.valueOf(close), 
////									PrecisionNum.valueOf(cjl), PrecisionNum.valueOf(cje) );
//						 
//						 
//						 nodedayperioddata.addNewXPeriodData(bkperiodrecord);
//						 
//						 //计算周线OHLC数据
//						 WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
//						 int lastdayweekNumber = lastdaydate.get(weekFields.weekOfWeekBasedYear());
//						 int curweekNumber = actiondate.toLocalDate().get(weekFields.weekOfWeekBasedYear());
//						 if(lastdayweekNumber == curweekNumber) {
//							 try{
////								 nodenewohlc.addBar( (NodeGivenPeriodDataItemForTA4J)bkperiodrecord);
//								 nodenewohlc.add( (NodeGivenPeriodDataItemForJFC)bkperiodrecord);
//							 } catch (java.lang.IllegalArgumentException e) {
//								 e.printStackTrace();
//							 }
//						 } else {
//							 //换周了，计算上一周周线OHLC 数据
//							 this.getTDXNodesWeeklyKXianZouShiForJFC(bk, lastdaydate.with(DayOfWeek.FRIDAY), nodenewohlc);
//							 
//							 nodenewohlc = null;
//							 nodenewohlc = new OHLCSeries (bk.getMyOwnCode());
//							 lastdaydate = actiondate.toLocalDate().with(DayOfWeek.FRIDAY);
//							 nodenewohlc.add( (NodeGivenPeriodDataItemForJFC)bkperiodrecord);
//						 }
//						 
//						 bkperiodrecord = null;
//					 }
//					 //目前算法最后一周要跳出循环后才能计算
//					 this.getTDXNodesWeeklyKXianZouShiForJFC(bk, lastdaydate.with(DayOfWeek.FRIDAY), nodenewohlc);
//					 
//						
//				}catch(java.lang.NullPointerException e){ 
//					e.printStackTrace();
////					logger.debug( "数据库连接为NULL!");
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}catch(Exception e){
//					e.printStackTrace();
//				} finally {
//					try {
//						if(rsfx != null)
//							rsfx.close();
//					} catch (SQLException e) {
//						e.printStackTrace();
//					}
//					rsfx = null;
//					searchtable = null;
//					nodenewohlc = null;
//				}
//
//				return bk;
//	}
	/*
	 * 
	 */
	private void calTDXNodesWeekyHighestAndLowestZhangFuForJFC(Stock stock, LocalDate friday, OHLCSeries nodenewohlc) 
	{
		//This part is for get stock highest and lowest zhangfu in the week. //Temperailly abandon.
		StockXPeriodDataForJFC nodexdata =  (StockXPeriodDataForJFC) stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);

		Integer spcohlcdataindex = nodexdata.getIndexOfSpecificDateOHLCData(friday);
		OHLCItem weeklyohlcdatalast;
		try {
			weeklyohlcdatalast = (OHLCItem) nodexdata.getOHLCData().getDataItem(spcohlcdataindex-1);
		} catch (java.lang.IndexOutOfBoundsException e) {
//			e.printStackTrace();
			return;
		}
		Double lastwkclose = weeklyohlcdatalast.getCloseValue();
		Double curwkclose = ((OHLCItem) nodenewohlc.getDataItem(0)  ).getCloseValue();
		Double weeklyhighzhangfu = (  curwkclose  - lastwkclose) / lastwkclose;
		Double weeklylowestzhangfu = (  curwkclose - lastwkclose) / lastwkclose;
		for(int i=1;i<nodenewohlc.getItemCount();i++) {
				OHLCItem curohlctmp = (OHLCItem) nodenewohlc.getDataItem(i);
				OHLCItem lastohlctmp = (OHLCItem) nodenewohlc.getDataItem(i-1);
				
				Double lastdayclose = lastohlctmp.getCloseValue();
				Double curdayclose = curohlctmp.getCloseValue();
				Double zhangfu = (curdayclose - lastdayclose ) /  lastdayclose; 
				if( zhangfu > weeklyhighzhangfu)
					weeklyhighzhangfu = zhangfu;
				if( zhangfu < weeklylowestzhangfu)
					weeklylowestzhangfu = zhangfu;
		}
		((StockNodesXPeriodData)nodexdata).addPeriodHighestZhangDieFu (friday,weeklyhighzhangfu);
		((StockNodesXPeriodData)nodexdata).addPeriodLowestZhangDieFu (friday,weeklylowestzhangfu);
	}
	/*
	 *
	 */
//	private TDXNodes getTDXNodesWeeklyKXianZouShiForTA4J (TDXNodes tdxnode, LocalDate friday, OHLCSeries nodenewohlc)
//	{
////		int newcount = nodenewohlc.getBarCount();
//		int newcount = nodenewohlc.getItemCount();
//		if(newcount == 0)
//			return tdxnode;
//		
//		TDXNodesXPeriodDataForTA4J nodexdata =  (TDXNodesXPeriodDataForTA4J) tdxnode.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
//		//在前面处理周线成交量等数据的时候，已经存了OHLC的数据，都是0，现在要把这个数据找到，是情况处理
//		Integer spcohlcdataindex = nodexdata.getIndexOfSpecificDateOHLCData(friday, 0);
//		if(spcohlcdataindex == null)
//			return tdxnode;
//		
//		if(spcohlcdataindex != null) {
//			Bar wkohlcdata = nodexdata.getSpecificDateOHLCData(friday,0);
//			
//			if(! (wkohlcdata.getClosePrice().doubleValue() == 0 &&
//				wkohlcdata.getMaxPrice().doubleValue() == 0 &&
//				wkohlcdata.getMinPrice().doubleValue() == 0 &&
//				wkohlcdata.getOpenPrice().doubleValue() == 0) ) 
//			{
//				return tdxnode; //都不是0，说明正确的数据已经在前面找出来了，直接退出。
//			}
//		} 
//
//		Num weeklyopen = null;
//		try{
//			Bar newohlcdata0 = nodenewohlc.getBar(0);
//			weeklyopen = newohlcdata0.getOpenPrice();
//		} catch (java.lang.IndexOutOfBoundsException e) {
//			e.printStackTrace();
//		}
//		
//		
//		Bar newohlcdatalast = nodenewohlc.getBar(nodenewohlc.getBarCount() - 1);
//		Num weeklyclose = newohlcdatalast.getClosePrice();
//		
//		Num weeklyhigh= DoubleNum.valueOf(0.0); Num weeklylow= DoubleNum.valueOf(10000000.0);
//		for(int i=0;i<nodenewohlc.getBarCount();i++) {
//			Bar newohlctmp = nodenewohlc.getBar(i) ;
//			if( newohlctmp.getMaxPrice().doubleValue() > weeklyhigh.doubleValue())
//				weeklyhigh = newohlctmp.getMaxPrice();
//			
//			if( newohlctmp.getMinPrice().doubleValue() < weeklylow.doubleValue())
//				weeklylow = newohlctmp.getMinPrice();
//		}
//		
//		Bar wkohlcdata = nodexdata.getSpecificDateOHLCData(friday,0);
//		try{
//			wkohlcdata.updateBarOHLC(weeklyopen, weeklyhigh, weeklylow, weeklyclose, null, null);
//		} catch (java.lang.NullPointerException e) {
//			e.printStackTrace();
//		}
//		
// 		return tdxnode;
//	}
	/*
	 * 函数需要修改
	 */
	private TDXNodes getTDXNodesWeeklyKXianZouShiForJFC (TDXNodes tdxnode, LocalDate friday, OHLCSeries nodenewohlc)
	{
		int newcount = nodenewohlc.getItemCount();
		if(newcount == 0)	return tdxnode;
		
		TDXNodesXPeriodDataForJFC nodexdata =  (TDXNodesXPeriodDataForJFC) tdxnode.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		//在前面处理周线成交量等数据的时候，已经存了OHLC的数据，都是0，现在要把这个数据找到，是情况处理
		Integer spcohlcdataindex = nodexdata.getIndexOfSpecificDateOHLCData(friday);
		if(spcohlcdataindex != null) {
			OHLCItem wkohlcdata = nodexdata.getSpecificDateOHLCData(friday);
			double wkclose = wkohlcdata.getCloseValue(); double wkhigh = wkohlcdata.getHighValue();
			double wklow = wkohlcdata.getLowValue(); double wkopen = wkohlcdata.getOpenValue() ;
			if(! (wkclose == 0 && wkhigh == 0 && wklow == 0 && wkopen	== 0) ) 
				return tdxnode; //都不是0，说明正确的数据已经在前面找出来了，直接退出。
		} 
		
		try { //都是0，就把原来的数据移除，然后把新数据放进去
			if(spcohlcdataindex != null) 
				nodexdata.getOHLCData().remove(spcohlcdataindex.intValue());
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {e.printStackTrace();}
		
		Double weeklyopen = null;
		try{
			OHLCItem newohlcdata0 = (OHLCItem) nodenewohlc.getDataItem(0);
			weeklyopen = newohlcdata0.getOpenValue();
		} catch (java.lang.IndexOutOfBoundsException e) {e.printStackTrace();}

		//Close Of this week
		OHLCItem newohlcdatalast = (OHLCItem) nodenewohlc.getDataItem(nodenewohlc.getItemCount()-1); 
		Double weeklyclose = newohlcdatalast.getCloseValue();

		Double weeklyhigh=0.0; Double weeklylow=10000000.0; 
		for(int i=0;i<nodenewohlc.getItemCount();i++) {
			OHLCItem newohlctmp = (OHLCItem) nodenewohlc.getDataItem(i);
			if( newohlctmp.getHighValue() > weeklyhigh)
				weeklyhigh = newohlctmp.getHighValue();
			
			if( newohlctmp.getLowValue() < weeklylow)
				weeklylow = newohlctmp.getLowValue();
		}
		
		java.sql.Date sqldate = null;
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			sqldate = new java.sql.Date(format.parse(friday.toString()).getTime());
		} catch (ParseException e) {e.printStackTrace();}
		try {
			org.jfree.data.time.Week recordwk = new org.jfree.data.time.Week (sqldate);
			nodexdata.getOHLCData().add(new OHLCItem(recordwk,weeklyopen,weeklyhigh,weeklylow,weeklyclose) );
		} catch(Exception e) {e.printStackTrace();}

		return tdxnode;
	}
	/*
	 * 从数据库中获取板块某时间段的日线走势，而个股是从CSV中读取
	 */
	public BanKuai getBanKuaiKXianZouShi(BanKuai bk, LocalDate nodestartday, LocalDate nodeendday, String period) 
	{//本函数初始是开发为日周期的K线数据，
		if(period.equals(NodeGivenPeriodDataItem.WEEK)) //调用周线查询函数
			; 
		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //调用月线查询函数
			;

		String searchtable = null;
		if(bk.getType() == BkChanYeLianTreeNode.TDXBK) 
			searchtable = this.getBanKuaiJiaoYiLiangBiaoName(bk);
		else if(bk.getType() == BkChanYeLianTreeNode.DZHBK)
			searchtable = "大智慧板块每日交易信息";
		
		TemporalField fieldCH = WeekFields.of(Locale.CHINA).dayOfWeek();
		NodeXPeriodData nodedayperioddata = bk.getNodeXPeroidData(period);
//		try{	nodestartday = nodestartday.with(fieldCH, 1); //确保K线总是显示完整得一周
//		} catch (java.lang.NullPointerException e) {return bk;}
//		LocalDate notcalwholewkmodedate = nodedayperioddata.isInNotCalWholeWeekMode();
//		if(notcalwholewkmodedate == null)	nodeendday = nodeendday.with(fieldCH, 7);
//		else nodeendday = notcalwholewkmodedate;

		String nodecode = bk.getMyOwnCode();
		String sqlquerystat = "SELECT *  FROM " + searchtable + " \r\n" + 
				"WHERE 代码='" + nodecode + "'" + "\r\n" + 
				"AND 交易日期  between'" + nodestartday + "' AND '" + nodeendday + "'"
				;
		
		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
		LocalDate lastdaydate = nodestartday.with(fieldCH, 6); //
//		org.ta4j.core.TimeSeries nodenewohlc = new BaseTimeSeries.SeriesBuilder().withName(bk.getMyOwnCode()).build();
		OHLCSeries nodenewohlc = new OHLCSeries (bk.getMyOwnCode());
		try {  
			 rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			 while(rsfx.next()) {
				 double open = rsfx.getDouble("开盘价");
				 double high = rsfx.getDouble("最高价");
				 double low = rsfx.getDouble("最低价");
				 double close = rsfx.getDouble("收盘价");
				 double cje = rsfx.getDouble("成交额");
				 double cjl = rsfx.getDouble("成交量");
				 double cjezbgr = rsfx.getDouble("DPCJEZB增长率");
				 double cjlzbgr = rsfx.getDouble("DPCJLZB增长率");
				 java.sql.Date actiondate = rsfx.getDate("交易日期");
				 ZonedDateTime zdtime = actiondate.toLocalDate().atStartOfDay(ZoneOffset.UTC);
				 org.jfree.data.time.Day recordday = new org.jfree.data.time.Day (actiondate);
				 
				 NodeGivenPeriodDataItem bkperiodrecord = new NodeGivenPeriodDataItemForJFC( bk.getMyOwnCode(), NodeGivenPeriodDataItem.DAY,
						 recordday, open, high,  low,  close, 
							cjl, cje );
				 bkperiodrecord.setDaPanChenJiaoErZhanBiGrowingRate(cjezbgr);
				 bkperiodrecord.setDaPanChenJiaoLiangZhanBiGrowingRate(cjlzbgr);
				 
//				 NodeGivenPeriodDataItem bkperiodrecord = new NodeGivenPeriodDataItemForTA4J( bk.getMyOwnCode(), NodeGivenPeriodDataItem.DAY,
//							zdtime, PrecisionNum.valueOf(open), PrecisionNum.valueOf(high),  PrecisionNum.valueOf(low),  PrecisionNum.valueOf(close), 
//							PrecisionNum.valueOf(cjl), PrecisionNum.valueOf(cje) );
				 
				 
				 nodedayperioddata.addNewXPeriodData(bkperiodrecord);
				 
				 //计算周线OHLC数据
				 WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
				 int lastdayweekNumber = lastdaydate.get(weekFields.weekOfWeekBasedYear());
				 int curweekNumber = actiondate.toLocalDate().get(weekFields.weekOfWeekBasedYear());
				 if(lastdayweekNumber == curweekNumber) {
					 try{
//						 nodenewohlc.addBar( (NodeGivenPeriodDataItemForTA4J)bkperiodrecord);
						 nodenewohlc.add( (NodeGivenPeriodDataItemForJFC)bkperiodrecord);
					 } catch (java.lang.IllegalArgumentException e) {
						 e.printStackTrace();
					 }
				 } else {
					 //换周了，计算上一周周线OHLC 数据
					 this.getTDXNodesWeeklyKXianZouShiForJFC(bk, lastdaydate.with(DayOfWeek.FRIDAY), nodenewohlc);
					 
					 nodenewohlc = null;
					 nodenewohlc = new OHLCSeries (bk.getMyOwnCode());
					 lastdaydate = actiondate.toLocalDate().with(DayOfWeek.FRIDAY);
					 nodenewohlc.add( (NodeGivenPeriodDataItemForJFC)bkperiodrecord);
				 }
				 
				 bkperiodrecord = null;
			 }
			 //目前算法最后一周要跳出循环后才能计算
			 this.getTDXNodesWeeklyKXianZouShiForJFC(bk, lastdaydate.with(DayOfWeek.FRIDAY), nodenewohlc);
			 
		} catch(java.lang.NullPointerException e){e.printStackTrace();
		} catch (SQLException e) {e.printStackTrace();
		} catch(Exception e){e.printStackTrace();
		} finally {
			try {if(rsfx != null) rsfx.close();} catch (SQLException e) {e.printStackTrace();}
			rsfx = null;
			searchtable = null;
			nodenewohlc = null;
		}

		return bk;
	}
	public BanKuai getShangZhengZhangDieTingInfo(BanKuai bk, LocalDate requiredstartday, LocalDate requiredendday, String period) 
	{
		String sql = "SELECT A.`EndOfWeekDate`, A.DIETING, B.ZHANGTING FROM\r\n" + 
				"\r\n" + 
				"( SELECT 通达信上交所股票每日交易信息.`交易日期`, COUNT(*) AS DIETING , DATE(通达信上交所股票每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信上交所股票每日交易信息.交易日期)) DAY) as EndOfWeekDate\r\n" + 
				"FROM 通达信上交所股票每日交易信息\r\n" + 
				"WHERE  涨跌幅 BETWEEN -20.0 AND -9.9\r\n" + 
				"AND 通达信上交所股票每日交易信息.`交易日期` BETWEEN ' " + requiredstartday + "' AND '" + requiredendday + "'\r\n" + 
				"AND  代码 NOT LIKE  '688%'\r\n" + 
				"GROUP BY YEAR (通达信上交所股票每日交易信息.`交易日期`),  WEEK(通达信上交所股票每日交易信息.`交易日期`)\r\n" + 
				") A\r\n" + 
				"\r\n" + 
				"LEFT JOIN \r\n" + 
				"( SELECT 通达信上交所股票每日交易信息.`交易日期`, COUNT(*) AS ZHANGTING, DATE(通达信上交所股票每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信上交所股票每日交易信息.交易日期)) DAY) as EndOfWeekDate\r\n" + 
				"FROM 通达信上交所股票每日交易信息\r\n" + 
				"WHERE  涨跌幅 BETWEEN 9.9 AND 20\r\n" + 
				"AND 通达信上交所股票每日交易信息.`交易日期` BETWEEN ' " + requiredstartday + "' AND '" + requiredendday + "'\r\n" + 
				"AND  代码 NOT LIKE  '688%'\r\n" + 
				"GROUP BY YEAR (通达信上交所股票每日交易信息.`交易日期`),  WEEK(通达信上交所股票每日交易信息.`交易日期`)\r\n" + 
				") B\r\n" + 
				"ON A.`EndOfWeekDate` = B.`EndOfWeekDate`\r\n" + 
				";\r\n" + 
				""
				;
		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sql);
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
			while(rsfx.next()) {
				LocalDate actiondate = rsfx.getDate("EndOfWeekDate").toLocalDate();
				Integer diefu = rsfx.getInt("DIETING");
				Integer zhangfu = rsfx.getInt("ZHANGTING");
				bkwkdate.addZhangDieTingTongJiJieGuo(actiondate.with(DayOfWeek.FRIDAY), zhangfu, diefu, false);
			}
		} catch(java.lang.NullPointerException e){ e.printStackTrace();
		} catch (SQLException e) {e.printStackTrace();
		} catch(Exception e){e.printStackTrace();
		} finally {
			try {if(rsfx != null)rsfx.close();} catch (SQLException e) {e.printStackTrace();}
			rsfx = null;
			sql = null;
		}
		
		return bk;
	}
	public BanKuai getShenZhenZhangDieTingInfo(BanKuai bk, LocalDate requiredstartday, LocalDate requiredendday, String period) 
	{
		String sql = "SELECT A.`EndOfWeekDate`, A.DIETING, B.ZHANGTING FROM\r\n" + 
				"\r\n" + 
				"( SELECT 通达信深交所股票每日交易信息.`交易日期`, COUNT(*) AS DIETING , DATE(通达信深交所股票每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信深交所股票每日交易信息.交易日期)) DAY) as EndOfWeekDate\r\n" + 
				"FROM 通达信深交所股票每日交易信息\r\n" + 
				"WHERE  涨跌幅 BETWEEN -20.0 AND -9.9\r\n" + 
				"AND 通达信深交所股票每日交易信息.`交易日期` BETWEEN ' " + requiredstartday + "' AND '" + requiredendday + "'\r\n" + 
				"GROUP BY YEAR (通达信深交所股票每日交易信息.`交易日期`),  WEEK(通达信深交所股票每日交易信息.`交易日期`)\r\n" + 
				") A\r\n" + 
				"\r\n" + 
				"LEFT JOIN \r\n" + 
				"( SELECT 通达信深交所股票每日交易信息.`交易日期`, COUNT(*) AS ZHANGTING, DATE(通达信深交所股票每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信深交所股票每日交易信息.交易日期)) DAY) as EndOfWeekDate\r\n" + 
				"FROM 通达信深交所股票每日交易信息\r\n" + 
				"WHERE  涨跌幅 BETWEEN 9.9 AND 20\r\n" + 
				"AND 通达信深交所股票每日交易信息.`交易日期` BETWEEN ' " + requiredstartday + "' AND '" + requiredendday + "'\r\n" + 
				"GROUP BY YEAR (通达信深交所股票每日交易信息.`交易日期`),  WEEK(通达信深交所股票每日交易信息.`交易日期`)\r\n" + 
				") B\r\n" + 
				"ON A.`EndOfWeekDate` = B.`EndOfWeekDate`\r\n"  
				;
		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sql);
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
			while(rsfx.next()) {
				LocalDate actiondate = rsfx.getDate("EndOfWeekDate").toLocalDate();
				Integer diefu = rsfx.getInt("DIETING");
				Integer zhangfu = rsfx.getInt("ZHANGTING");
				bkwkdate.addZhangDieTingTongJiJieGuo(actiondate.with(DayOfWeek.FRIDAY), zhangfu, diefu, false);
			}
		} catch(java.lang.NullPointerException e){ e.printStackTrace();
		} catch (SQLException e) {e.printStackTrace();
		}catch(Exception e){e.printStackTrace();
		} finally {
			try {if(rsfx != null)rsfx.close();} catch (SQLException e) {e.printStackTrace();	}
			rsfx = null;
			sql = null;
		}
		
		return bk;
	}
	/*
	 * 创业板
	 */
	public BanKuai getChuangYeBanZhangDieTingInfo(BanKuai bk, LocalDate requiredstartday, LocalDate requiredendday, String period) 
	{
		 String sql = "SELECT A.`EndOfWeekDate`, A.DIETING, B.ZHANGTING FROM\r\n" + 
					"\r\n" + 
					"( SELECT 通达信深交所股票每日交易信息.`交易日期`, COUNT(*) AS DIETING , DATE(通达信深交所股票每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信深交所股票每日交易信息.交易日期)) DAY) as EndOfWeekDate\r\n" + 
					"FROM 通达信深交所股票每日交易信息\r\n" + 
					"WHERE  涨跌幅 BETWEEN -20.0 AND -9.9\r\n" + 
					"AND 通达信深交所股票每日交易信息.`交易日期` BETWEEN ' " + requiredstartday + "' AND '" + requiredendday + "'\r\n" +
					"AND   代码 LIKE  '300%'\r\n" +
					"GROUP BY YEAR (通达信深交所股票每日交易信息.`交易日期`),  WEEK(通达信深交所股票每日交易信息.`交易日期`)\r\n" + 
					") A\r\n" + 
					"\r\n" + 
					"LEFT JOIN \r\n" + 
					"( SELECT 通达信深交所股票每日交易信息.`交易日期`, COUNT(*) AS ZHANGTING, DATE(通达信深交所股票每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信深交所股票每日交易信息.交易日期)) DAY) as EndOfWeekDate\r\n" + 
					"FROM 通达信深交所股票每日交易信息\r\n" + 
					"WHERE  涨跌幅 BETWEEN 9.9 AND 20\r\n" + 
					"AND 通达信深交所股票每日交易信息.`交易日期` BETWEEN ' " + requiredstartday + "' AND '" + requiredendday + "'\r\n" +
					"AND   代码 LIKE  '300%'\r\n" +
					"GROUP BY YEAR (通达信深交所股票每日交易信息.`交易日期`),  WEEK(通达信深交所股票每日交易信息.`交易日期`)\r\n" + 
					") B\r\n" + 
					"ON A.`EndOfWeekDate` = B.`EndOfWeekDate`\r\n"  
					;
		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sql);
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
			while(rsfx.next()) {
				LocalDate actiondate = rsfx.getDate("EndOfWeekDate").toLocalDate();
				Integer diefu = rsfx.getInt("DIETING");
				Integer zhangfu = rsfx.getInt("ZHANGTING");
				bkwkdate.addZhangDieTingTongJiJieGuo(actiondate.with(DayOfWeek.FRIDAY), zhangfu, diefu, false);
			}
		} catch(java.lang.NullPointerException e){ e.printStackTrace();
		} catch (SQLException e) {e.printStackTrace();
		} catch(Exception e){e.printStackTrace();
		} finally {
			try {if(rsfx != null)rsfx.close();} catch (SQLException e) {e.printStackTrace();}
			rsfx = null;
			sql = null;
		}
		
		return bk;
	}
	/*
	 * 
	 */
	public Set<LocalDate> getNodeDataBaseStoredDataTimeSet (TDXNodes node, LocalDate start, LocalDate end)
	{
		String cjltable;
		if(node.getType() == BkChanYeLianTreeNode.TDXBK) 
			cjltable = this.getBanKuaiJiaoYiLiangBiaoName( (BanKuai)node);
		else 
			cjltable = this.getStockJiaoYiLiangBiaoName( (Stock)node);
		
		if(start == null)
			start = LocalDate.parse("1990-01-01");
		if(end == null)
			end = LocalDate.parse("9990-01-01");
		String sqlquerystat = "SELECT  交易日期"
				+ " FROM " + cjltable + "  WHERE  代码 = " 
					+ "'"  + node.getMyOwnCode() + "'" 
					+ " AND  交易日期  BETWEEN '" + start + "'  AND '" + end + "'"
					;
		CachedRowSetImpl	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		Set<LocalDate> nodetimeset = new HashSet();
		try { 				

		    	while(rs.next()) {
		    		java.sql.Date recordsdate = rs.getDate("交易日期"); 
		    		nodetimeset.add(recordsdate.toLocalDate() );
		    	}
		 } catch(java.lang.NullPointerException e) { 	e.printStackTrace();
		 } catch (SQLException e) {e.printStackTrace();
		 } catch(Exception e){	e.printStackTrace();
		 } finally {
		    	if(rs != null)	try {rs.close(); rs = null;} catch (SQLException e) {	e.printStackTrace();	}
		 }
		
		return nodetimeset;
	}
	/*
	 * 
	 */
	public Set<LocalDate> getNodeTuShareExtraDataTimeSet(Stock node,LocalDate start, LocalDate end) 
	{
		String cjltable = this.getStockJiaoYiLiangBiaoName( (Stock)node);
		String sqlquerystat = "SELECT  交易日期"
				+ " FROM " + cjltable + "  WHERE  代码 = " 
					+ "'"  + node.getMyOwnCode() + "'" 
					+ " AND 自由流通换手率 IS not NULL "
					+ " AND 交易日期 BETWEEN '" +  start + "'  AND '" + end + "'" 
					;
		CachedRowSetImpl	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		Set<LocalDate> nodetimeset = new HashSet();
		try { 				

		    	while(rs.next()) {
		    		java.sql.Date recordsdate = rs.getDate("交易日期"); 
		    		nodetimeset.add(recordsdate.toLocalDate() );
		    	}
		 } catch(java.lang.NullPointerException e) { 	e.printStackTrace();
		 } catch (SQLException e) {e.printStackTrace();
		 } catch(Exception e){	e.printStackTrace();
		 } finally {
		    	if(rs != null)	try {rs.close(); rs = null;} catch (SQLException e) {	e.printStackTrace();	}
		 }
		
		return nodetimeset;
	}
	/*
	 * 
	 */
	public Interval getNodeDataBaseStoredDataTimeRange (TDXNodes node)
	{
		String cjltable;
		if(BkChanYeLianTreeNode.isBanKuai(node)) 
			cjltable = this.getBanKuaiJiaoYiLiangBiaoName( (BanKuai)node);
		else 
			cjltable = this.getStockJiaoYiLiangBiaoName( (Stock)node);
		
		CachedRowSetImpl  rs = null;
		LocalDate ldlastestdbrecordsdate = null;
		LocalDate loldestdbrecordsdate = null;
		try { 				
			String sqlquerystat = "SELECT  MAX(交易日期)	MOST_RECENT_TIME , MIN(交易日期)	MOST_OLDEST_TIME"
					+ " FROM " + cjltable + "  WHERE  代码 = " 
						+ "'"  + node.getMyOwnCode() + "'" 
						;
		    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	while(rs.next()) {
		    		java.sql.Date lastestdbrecordsdate = rs.getDate("MOST_RECENT_TIME"); //mOST_RECENT_TIME
		    		java.sql.Date oldestdbrecordsdate =  rs.getDate("MOST_OLDEST_TIME"); //mOST_RECENT_TIME
		    		try {
		    			ldlastestdbrecordsdate = lastestdbrecordsdate.toLocalDate();
		    			loldestdbrecordsdate = oldestdbrecordsdate.toLocalDate();
		    		} catch (java.lang.NullPointerException e) {	}
		    	}
		    } catch(java.lang.NullPointerException e) { 	e.printStackTrace();
		    } catch (SQLException e) {e.printStackTrace();
		    } catch(Exception e){	e.printStackTrace();
		    } finally {
		    	if(rs != null) try {rs.close(); rs = null;} catch (SQLException e) {	e.printStackTrace();	}
		    }
		
		DateTime requiredstartdt= new DateTime(loldestdbrecordsdate.getYear(), loldestdbrecordsdate.getMonthValue(), loldestdbrecordsdate.getDayOfMonth(), 0, 0, 0, 0);
		DateTime requiredenddt= new DateTime(ldlastestdbrecordsdate.getYear(), ldlastestdbrecordsdate.getMonthValue(), ldlastestdbrecordsdate.getDayOfMonth(), 0, 0, 0, 0);
		Interval requiredinterval = new Interval(requiredstartdt,requiredenddt);
		
		return requiredinterval;
	}
	/*
	 * 
	 */
	public Set<LocalDate> getNodeExportFileDataTimeSet (TDXNodes node)
	{
		Set<LocalDate> nodetimeset = new HashSet();
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		File nodeexportfile = this.getTDXNodesTDXSystemExportFile(node);
		BufferedReader reader;
		String line; int linecount = 0; 
		try {
			reader = new BufferedReader(new FileReader(nodeexportfile.getAbsolutePath() ));
			line = reader.readLine();
			linecount ++;
			while (line != null) {
				List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);
               	LocalDate curlinedate = null;
           		try {
	            			String beforparsedate = tmplinelist.get(0);
	            			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateRule);
	            			curlinedate =  LocalDate.parse(beforparsedate,formatter) ;
	            			nodetimeset.add(curlinedate);
           		} catch (Exception e) {	}
				
				// read next line
				line = reader.readLine();
				linecount ++;
			}
			reader.close();
		} catch (IOException e) {e.printStackTrace(); }
		
		return nodetimeset;
		
	}
	/*
	 * 
	 */
	public Interval getNodeExportFileDataTimeRange (TDXNodes node)
	{
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		File nodeexportfile = this.getTDXNodesTDXSystemExportFile(node);
		LocalDate startdate = null; LocalDate enddate = null;
		//get start date
		BufferedReader reader;
		String line; int linecount = 0; Boolean foundalinewithdate = false;
		try {
			reader = new BufferedReader(new FileReader(nodeexportfile.getAbsolutePath() ));
			line = reader.readLine();
			linecount ++;
			while (line != null) {
				List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);
				
				if(!foundalinewithdate) { //计算起始日期，一般在第二行
	                	LocalDate curlinedate = null;
	            		try {
	            			String beforparsedate = tmplinelist.get(0);
	            			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateRule);
	            			curlinedate =  LocalDate.parse(beforparsedate,formatter) ;
	            			startdate = curlinedate;
	            			foundalinewithdate = true;
	    					break;
	            		} catch (Exception e) {	foundalinewithdate = false; }
				}
				// read next line
				line = reader.readLine();
				linecount ++;
			}
			reader.close();
		} catch (IOException e) {e.printStackTrace(); }
		
		//get end date
		foundalinewithdate = false;
		RandomAccessFile rf = null;
        try {  
            rf = new RandomAccessFile(nodeexportfile, "r");  
            long len = rf.length();  
            long start = rf.getFilePointer();  
            long nextend = start + len - 1;  
            rf.seek(nextend);  
            int c = -1;  
            int lineimportcount = 0;
            while (nextend > start ) {
                c = rf.read();
            	if (c == '\n' || c == '\r') {  
                    line = rf.readLine();  
                    if (line != null) {  
                        @SuppressWarnings("deprecation")
						List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);

                        if( !foundalinewithdate) { //有可能是半天数据，有0，不完整，不能录入,对个股来说，半天数据也有，要特别处理
                        	LocalDate curlinedate = null;
                    		try {
                    			String beforparsedate = tmplinelist.get(0);
                    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateRule);
                    			curlinedate =  LocalDate.parse(beforparsedate,formatter) ;
                    			enddate = curlinedate;
                    			foundalinewithdate = true;
                    			break;
                    		} catch (Exception e) {	foundalinewithdate = false;	}
                        }
                    } 
                    nextend--;  
                }  
                nextend--;  
                rf.seek(nextend);  
            }  
        } catch (FileNotFoundException e) {e.printStackTrace();  
        } catch (IOException e) { e.printStackTrace();  
        } finally {  
            try {      if (rf != null)  rf.close();   rf = null;} catch (IOException e) {  e.printStackTrace();  }  
        }
        
        if(startdate != null  && enddate != null) {
        	DateTime requiredstartdt= new DateTime(startdate.getYear(), startdate.getMonthValue(), startdate.getDayOfMonth(), 0, 0, 0, 0);
    		DateTime requiredenddt= new DateTime(enddate.getYear(), enddate.getMonthValue(), enddate.getDayOfMonth(), 0, 0, 0, 0);
    		Interval requiredinterval = new Interval(requiredstartdt,requiredenddt);
    		
    		return requiredinterval;
        } else
        	return null;
	}
	/*
	 * 从通达信导出数据文件的位置
	 */
	public File getTDXNodesTDXSystemExportFile (TDXNodes node ) 
	{
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		String bkfilename;
		if(node.getMyOwnCode().equalsIgnoreCase("000852"))  //通达信不知道为什么，中证1000的每日交易数据输出文件不一样
			 bkfilename = "62000852.TXT";
		else 
			bkfilename = (filenamerule.replaceAll("YY",node.getNodeJiBenMian().getSuoShuJiaoYiSuo().toUpperCase())).replaceAll("XXXXXX", node.getMyOwnCode());

		File tmpbkfile = new File(exportath + "/" + bkfilename);
		
		return tmpbkfile;
	}
	/*
	 * 
	 */
	private void getTDXGeGuShuJuJiLuMaxMinDateByJiaoYiSuo (String jiaoyisuo)
	{
		String optTable = null;
		if(jiaoyisuo.equalsIgnoreCase("SZ")  )
			optTable = "通达信深交所股票每日交易信息";
		else if(jiaoyisuo.equalsIgnoreCase("SH") )
			optTable = "通达信上交所股票每日交易信息";
		CachedRowSetImpl  rsdm = null;
		try { 	
			String sqlquerystat = "SELECT * FROM \r\n" + 
					"(SELECT 股票代码  FROM A股\r\n" + 
					"WHERE 所属交易所 = '" + jiaoyisuo + "' ) agu\r\n" + 
					"\r\n" + 
					"LEFT JOIN \r\n" + 
					"( SELECT 代码, MIN(交易日期) minjytime , MAX(交易日期) maxjytime FROM " + optTable + "\r\n" + 
					"GROUP BY 代码) shjyt\r\n" + 
					"ON agu.股票代码 =  shjyt.`代码`\r\n"
					;
	    	rsdm = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	while(rsdm.next()) {
		    		 String ggcode = rsdm.getString("股票代码"); //mOST_RECENT_TIME
		    		 Stock stock = (Stock) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(ggcode, BkChanYeLianTreeNode.TDXGG);
		    		 if(stock == null) 
		    			 continue;
		    		 try { LocalDate mintime = rsdm.getDate("minjytime").toLocalDate();
		    		 		stock.getShuJuJiLuInfo().setJyjlmindate(mintime);
		    		 } catch (java.lang.NullPointerException ex) {}
		    		 try { LocalDate maxtime = rsdm.getDate("maxjytime").toLocalDate();
		    		 		stock.getShuJuJiLuInfo().setJyjlmaxdate(maxtime);
		    		 } catch (java.lang.NullPointerException ex) {}
		    		 
		    		 
		    	}
		    } catch(java.lang.NullPointerException e) {	e.printStackTrace();
		    } catch (SQLException e) {	e.printStackTrace();
		    } catch(Exception e){	e.printStackTrace();
		    } finally {
		    	if(rsdm != null)  try {rsdm.close();rsdm = null;} catch (SQLException e) {	e.printStackTrace();}
		    }
	}
	/*
	 * 
	 */
	public File refreshTDXBanKuaiVolAmoToDbBulkImport (String jiaoyisuo, int bulkcount)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步板块成交量报告.tmp");
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		getTDXBanKuaiShuJuJiLuMaxMinDateByJiaoYiSuo (jiaoyisuo);
		
		String cjltablename;
		if(jiaoyisuo.toLowerCase().equals("sh"))	cjltablename = "通达信板块每日交易信息";
		else	cjltablename = "通达信交易所指数每日交易信息";
		int nodecount = 0; List<String> nodeinsertdata = new ArrayList<>(); 
		
		 Collection<BkChanYeLianTreeNode> requiredbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK,jiaoyisuo);
		 for(BkChanYeLianTreeNode tmpnode :  requiredbk ) {
			 if( (nodecount == bulkcount  && !nodeinsertdata.isEmpty() ) || nodeinsertdata.size()> bulkcount * 4  ) {
				 setNodeDataToDataBase (cjltablename, nodeinsertdata,BkChanYeLianTreeNode.TDXBK );
				 nodecount = 0; nodeinsertdata.clear();
			 }
			 
			BanKuai bk = (BanKuai) tmpnode;
			if(!bk.getBanKuaiOperationSetting().isImportdailytradingdata())			continue;
			
			String tmpbkcode = bk.getMyOwnCode();
			File tmpbkfile = null; String bkfilename = null;
			if(bk.getMyOwnCode().equalsIgnoreCase("000852"))  //通达信不知道为什么，中证1000的每日交易数据输出文件不一样
				 bkfilename = "62000852.TXT";
			else 
				bkfilename = (filenamerule.replaceAll("YY",jiaoyisuo.toUpperCase())).replaceAll("XXXXXX", tmpbkcode);
			tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead())   
			    continue; 
			
			LocalDate ldlastestdbrecordsdate = bk.getShuJuJiLuInfo().getJyjlmaxdate();
			int datasizebefore = nodeinsertdata.size();
			nodeinsertdata = setVolAmoRecordsFromFileToDatabaseBulkImport(tmpnode,tmpbkfile,ldlastestdbrecordsdate,dateRule,nodeinsertdata,tmprecordfile);
			int datasizeafter = nodeinsertdata.size();
			if(datasizeafter > datasizebefore)
				nodecount ++;
		}
		 
		 setNodeDataToDataBase (cjltablename, nodeinsertdata,BkChanYeLianTreeNode.TDXBK );
		
		return tmprecordfile;
	}
	/*
	 * 
	 */
	private void setNodeDataToDataBase (String optable, List<String> inserteddata, int datatype) 
	{
		if(inserteddata.isEmpty())
			return;
		
		String insertstr = "";
		for(String nodedata : inserteddata) 
			insertstr = insertstr + nodedata;
		String sqlinsertstat = null;
		if(datatype == BkChanYeLianTreeNode.TDXBK)
			sqlinsertstat = "INSERT INTO " + optable +"(代码,交易日期,开盘价,最高价,最低价,收盘价,成交量,成交额)"
									+ " values \r\n" 
									+ insertstr
									;
		else if(datatype == BkChanYeLianTreeNode.TDXGG)
			sqlinsertstat = "INSERT INTO " + optable +"(代码,交易日期,成交量,成交额)"
									+ " values \r\n" 
									+ insertstr
									;
		sqlinsertstat = sqlinsertstat.trim().substring(0, sqlinsertstat.trim().length() - 1);
		try {
			int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		} catch (SQLException e) {e.printStackTrace();}
		
		return;
	}
	/*
	 * 
	 */
	public void refreshExtraStockDataFromTushare (LocalDate start, LocalDate end)
	{
		LocalDate ldlastestdbrecordsdate = null;
		   if(start == null)		   ldlastestdbrecordsdate =  LocalDate.parse("2019-12-29"); //当前数据的起点
		   else			   ldlastestdbrecordsdate = start; //从最新数据的后一天开始导入数据
		   
		   if(end == null)   end = LocalDate.now().plusDays(1);
		   
		while(ldlastestdbrecordsdate.isBefore( end.plusDays(1) )) {
			if( ldlastestdbrecordsdate.getDayOfWeek() == DayOfWeek.SUNDAY || ldlastestdbrecordsdate.getDayOfWeek() == DayOfWeek.SATURDAY  ) { //开始导入数据日期一直到今天是周末，肯定不需要导入
				ldlastestdbrecordsdate = ldlastestdbrecordsdate.plusDays(1);
				continue;
			}
			
			String savedfilename = sysconfig.getTuShareExtraDataDownloadedFilePath () + "/" + ldlastestdbrecordsdate.toString().replaceAll("-", "") + "dailyexchangedata.csv";
			File savedfile = new File (savedfilename);
			if(!savedfile.exists()) {
				ldlastestdbrecordsdate = ldlastestdbrecordsdate.plusDays(1);
				continue;
			}
			
			try (
					FileReader filereader = new FileReader(savedfile);
					CSVReader csvReader = new CSVReader(filereader);
		    ){ 
				int titlesign = -1;//标记CSV文件的第一行 
				Integer turnover_rate_f_index = null; Integer free_share_index = null; Integer stockcode_index = null; Integer pe_index = null;Integer pe_ttm_index = null; Integer pb_index = null;
				Integer turnover_rate_index = null; Integer total_mv_index = null; Integer circ_mv_index = null;
				List<String[]> records = csvReader.readAll();
				for (String[] nextRecord : records) {
		            	if(-1 == titlesign) {
		            		for(int i=0;i<nextRecord.length;i++) {
		            			if(nextRecord[i].equalsIgnoreCase("ts_code")  )
		            				stockcode_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("turnover_rate_f")  )
		            				turnover_rate_f_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("turnover_rate")  )
		            				turnover_rate_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("free_share")  )
		            				free_share_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("pe")  )
		            				pe_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("pe_ttm")  )
		            				pe_ttm_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("pb")  )
		            				pb_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("total_mv")  )
		            				total_mv_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("circ_mv")  )
		            				circ_mv_index = i;
		            		}
		            		
		            		titlesign ++;
		            	} 
		            	else { //第二行
		            		String stockcode = null; 
		            		if(stockcode_index != null) 
		            			stockcode = nextRecord[stockcode_index].trim().substring(0, 6);
		            		BkChanYeLianTreeNode stock = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(stockcode, BkChanYeLianTreeNode.TDXGG );		
		            		if(stock == null)
		            			continue;

		            		String optTable = null;
		            		if(stock.getNodeJiBenMian().getSuoShuJiaoYiSuo().equalsIgnoreCase("SZ")  )
		            			optTable = "通达信深交所股票每日交易信息";
		            		else if(stock.getNodeJiBenMian().getSuoShuJiaoYiSuo().equalsIgnoreCase("SH") )
		            			optTable = "通达信上交所股票每日交易信息";
		            		
		            		Double turnover_rate_f = null; Double turnover_rate = null; Double free_share = null;
		            		Double pe = null;Double pe_ttm = null; Double pb = null; Double total_mv = null; Double circ_mv = null;
		            		if(turnover_rate_f_index != null) {
		            			String turnover_rate_f_str = nextRecord[turnover_rate_f_index];
		            			if(!Strings.isNullOrEmpty(turnover_rate_f_str))
		            				turnover_rate_f = Double.parseDouble(turnover_rate_f_str);
		            		}
		            		if(turnover_rate_index != null) {
		            			String turnover_rate_str = nextRecord[turnover_rate_index];
		            			if(!Strings.isNullOrEmpty(turnover_rate_str))
		            				turnover_rate = Double.parseDouble(turnover_rate_str);
		            		}
		            		if(total_mv_index != null) {
		            			String total_mv_str = nextRecord[total_mv_index];
		            			if(!Strings.isNullOrEmpty(total_mv_str))
		            				total_mv = Double.parseDouble(total_mv_str) * 10000;
		            		}
		            		if(circ_mv_index != null) {
		            			String circ_mv_str = nextRecord[circ_mv_index];
		            			if(!Strings.isNullOrEmpty(circ_mv_str))
		            				circ_mv = Double.parseDouble(circ_mv_str) * 10000;
		            		}
//		            		if(free_share_index != null) {
//		            			String free_share_str = nextRecord[free_share_index];
//		            			if(!Strings.isNullOrEmpty(free_share_str))
//		            				free_share = Double.parseDouble(free_share_str) * 10000;
//		            		}
//		            		if(pe_index != null) {
//		            			String pe_str = nextRecord[pe_index];
//		            			if(!Strings.isNullOrEmpty(pe_str))
//		            				pe = Double.parseDouble(pe_str) ;
//		            		}
//		            		if(pe_ttm_index != null) {
//		            			String pe_ttm_str = nextRecord[pe_ttm_index];
//		            			if(!Strings.isNullOrEmpty(pe_ttm_str))
//		            				pe_ttm = Double.parseDouble(pe_ttm_str);
//		            		}
//		            		if(pb_index != null) {
//		            			String pb_str = nextRecord[pb_index];
//		            			if(!Strings.isNullOrEmpty(pb_str))
//		            				pb = Double.parseDouble(pb_str);
//		            		}
		            		
			                String sqlupdate = "Update " + optTable + " SET " +
			                					"  自由流通换手率=" + turnover_rate_f +
			                					"  , 换手率=" + turnover_rate +
			                					"  , 总市值=" + total_mv +
			                					"  , 流通市值= " + circ_mv +
			                					" WHERE 交易日期 = '" + ldlastestdbrecordsdate + "'" + 
			                					" AND 代码= '" + stockcode + "'"
			                					;
						    try {
								int result = connectdb.sqlUpdateStatExecute(sqlupdate);
							} catch (MysqlDataTruncation e) {e.printStackTrace();
							} catch (SQLException e) {e.printStackTrace();}
		            	}
		        }
			} catch (IOException e) {e.printStackTrace();}
			  
			ldlastestdbrecordsdate = ldlastestdbrecordsdate.plusDays(1); 
			savedfile = null;
		}

		return ;
	}
	/*
	 * 
	 */
	public void refreshExtraStockDataFromTushare ()
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "导入TUSHARE股票额外数据信息.tmp");
		
		LocalDate mintime = null;LocalDate maxtime = null; //use stock 60000 as the benchmark for exta data
		String sqlquerystat = "SELECT MIN(交易日期) minjytime , MAX(交易日期) maxjytime \r\n" + 
				"FROM 通达信上交所股票每日交易信息\r\n" + 
				"WHERE \r\n" + 
				"  自由流通换手率 IS NOT NULL  "
				;
		CachedRowSetImpl rsdm = connectdb.sqlQueryStatExecute(sqlquerystat);
		try { 	
	    	while(rsdm.next()) {
		    		 try {  mintime = rsdm.getDate("minjytime").toLocalDate();
		    		 } catch (java.lang.NullPointerException ex) {}
		    		 try {  maxtime = rsdm.getDate("maxjytime").toLocalDate();
		    		 } catch (java.lang.NullPointerException ex) {}
		    	}
		    } catch(java.lang.NullPointerException e) {	e.printStackTrace();
		    } catch (SQLException e) {	e.printStackTrace();
		    } catch(Exception e){	e.printStackTrace();
		    } finally {
		    	if(rsdm != null)
				try {rsdm.close();rsdm = null;
				} catch (SQLException e) {	e.printStackTrace();}
		    }
		
		LocalDate ldlastestdbrecordsdate = null;
		   if(maxtime == null)
			   ldlastestdbrecordsdate =  LocalDate.parse("2019-12-29"); //当前数据的起点
		   else
			   ldlastestdbrecordsdate = maxtime.plusDays(1); //从最新数据的后一天开始导入数据
		   
		   this.refreshExtraStockDataFromTushare (ldlastestdbrecordsdate, null);
		   return ;
	}
	/*
	 * 
	 */
	public File refreshTDXGeGuVolAmoToDbBulkImport(String jiaoyisuo, int bulkcount)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步个股成交量信息.tmp");
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		Collection<BkChanYeLianTreeNode> allgegucode = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXGG,jiaoyisuo);		

		getTDXGeGuShuJuJiLuMaxMinDateByJiaoYiSuo (jiaoyisuo);
		
		String optTable = null;
		if(jiaoyisuo.equalsIgnoreCase("SZ")  )
			optTable = "通达信深交所股票每日交易信息";
		else if(jiaoyisuo.equalsIgnoreCase("SH") )
			optTable = "通达信上交所股票每日交易信息";
		int nodecount = 0; List<String> nodeinsertdata = new ArrayList<>();
		
		for(BkChanYeLianTreeNode tmpnode :allgegucode) {
			if( (nodecount ==  bulkcount && !nodeinsertdata.isEmpty() ) || nodeinsertdata.size()>  bulkcount*4) {
				setNodeDataToDataBase (optTable, nodeinsertdata, BkChanYeLianTreeNode.TDXGG  );
				nodecount = 0; nodeinsertdata.clear();
			}
			
			String tmpbkcode = tmpnode.getMyOwnCode();
			String bkfilename = null;
			if(jiaoyisuo.toLowerCase().equals("sz") )
				bkfilename = (filenamerule.replaceAll("YY","SZ")).replaceAll("XXXXXX", tmpbkcode);
			else if(jiaoyisuo.toLowerCase().equals("sh") ) 
				bkfilename = (filenamerule.replaceAll("YY","SH")).replaceAll("XXXXXX", tmpbkcode);
			File tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {  
				logger.debug("读取" + bkfilename + "发生错误！");
			    continue; 
			} 
			
			LocalDate ldlastestdbrecordsdate = ((Stock)tmpnode).getShuJuJiLuInfo().getJyjlmaxdate();
			int datasizebefore = nodeinsertdata.size();
			nodeinsertdata = setVolAmoRecordsFromFileToDatabaseBulkImport(tmpnode,tmpbkfile,ldlastestdbrecordsdate,dateRule,nodeinsertdata,tmprecordfile);
			int datasizeafter = nodeinsertdata.size();
			if(datasizeafter > datasizebefore)
				nodecount ++;
				
				//用户同步完个股和板块成交量后，要把个股的TXT转为CSV FILE，这是因为复权问题，因为数据库存放的是当前的成交数据，如果发生复权，前面所有的OHLC都会改变，
				//重新把数据库中的数据改写一遍成本很高，所以个股每天的OHLC数据直接从复权的TXT转化的CSV中读取。这里先把TXT 都转为CSV，存放到指定位置
			if(datasizeafter > datasizebefore) {
				String lastestinsertdata = nodeinsertdata.get(nodeinsertdata.size()-1);
				if(lastestinsertdata.contains(tmpbkcode)) { //说明有数据插入
					String csvfilepath = sysconfig.getCsvPathOfExportedTDXVOLFiles();
					ConvertTxtToCsv cttc = new ConvertTxtToCsv ();
					cttc.convertTxtFileToCsv(exportath + "/" + bkfilename, csvfilepath + bkfilename.replace("TXT", "CSV"));
					cttc = null;
				}
			}
			
		}
		
		setNodeDataToDataBase (optTable, nodeinsertdata , BkChanYeLianTreeNode.TDXGG);
		
		return tmprecordfile;
	}
	/*
	 */
	private List<String> setVolAmoRecordsFromFileToDatabaseBulkImport (BkChanYeLianTreeNode tmpnode, File tmpbkfile, LocalDate lastestdbrecordsdate,  String datarule, List<String> nodeinsertdata, File tmprecordfile)
	{
		String tmpbkcode = tmpnode.getMyOwnCode();
		if(lastestdbrecordsdate == null) //null说明数据库里面还没有相关的数据，把时间设置为1900年把文件中所有数据都导入
			try {
		        lastestdbrecordsdate = LocalDate.now().minus(100,ChronoUnit.YEARS);
				Files.append(tmpbkcode + "成交量记录将导入所有记录！"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			} catch (Exception e1) {e1.printStackTrace();} 
		
		RandomAccessFile rf = null;
        try {  
            rf = new RandomAccessFile(tmpbkfile, "r");  
            long len = rf.length();  
            long start = rf.getFilePointer();  
            long nextend = start + len - 1;  
            String line;  
            rf.seek(nextend);  
            int c = -1;  
            boolean finalneededsavelinefound = false; //标记需要导入的日期的起始行
            int lineimportcount =0;
            while (nextend > start && finalneededsavelinefound == false) {
                c = rf.read();
            	if (c == '\n' || c == '\r') {  
                    line = rf.readLine();  
                    if (line != null) {  
                        @SuppressWarnings("deprecation")
						List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);

                        if( tmplinelist.size() ==7 && !tmplinelist.get(5).equals("0")) { //有可能是半天数据，有0，不完整，不能录入,对个股来说，半天数据也有，要特别处理
                        	String open = tmplinelist.get(1).trim();
                        	String high = tmplinelist.get(2).trim();
                        	String low  = tmplinelist.get(3).trim();
                        	String close= tmplinelist.get(4).trim();
                        	String vol = tmplinelist.get(5).trim();
                        	String amo = tmplinelist.get(6).trim();
                        	
                        	if(tmpbkcode.equalsIgnoreCase("000852") && tmpnode.getType() == BkChanYeLianTreeNode.TDXBK) {//中证1000的文件，vol and amo 数据没有精确到个位
                        		vol = String.valueOf( Double.parseDouble(vol) * 10000 );
                        		amo = String.valueOf( Double.parseDouble(amo) * 1000000 );
                        	}
                        		
                        	LocalDate curlinedate = null;
                    		try {
                    			String beforparsedate = tmplinelist.get(0);
                    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datarule);
                    			curlinedate =  LocalDate.parse(beforparsedate,formatter) ;
                    			if( curlinedate.isAfter(lastestdbrecordsdate)) { // ('John', 123, 'Lloyds Office'),
                    				String sqlinsertstat = null;
                    				if(tmpnode.getType() == BkChanYeLianTreeNode.TDXBK)
	                        			sqlinsertstat = "(" 
	                    						+ "'" + tmpbkcode.trim() + "'" + ","
	                    						+ "'" +  curlinedate + "'" + ","
	                    						+ "'" +  open + "'" + "," 
	                    						+ "'" +  high + "'" + "," 
	                    						+ "'" +  low + "'" + "," 
	                    						+ "'" +  close + "'" + "," 
	                    						+ "'" +  vol + "'" + "," 
	                    						+ "'" +  amo + "'"  
	                    						+ "), \r\n"
	                    						;
                    				else if(tmpnode.getType() == BkChanYeLianTreeNode.TDXGG)
                    					sqlinsertstat = "(" 
	                    						+ "'" + tmpbkcode.trim() + "'" + ","
	                    						+ "'" +  curlinedate + "'" + ","
	                    						+ "'" +  vol + "'" + "," 
	                    						+ "'" +  amo + "'"  
	                    						+ "), \r\n"
	                    						;
                        			nodeinsertdata.add(sqlinsertstat);
                    				lineimportcount ++;
                        		} else if(curlinedate.compareTo(lastestdbrecordsdate) == 0) {
                        			finalneededsavelinefound = true;
                        			if(lineimportcount == 0) {
                        				Files.append(tmpbkcode + "交量记录导入起始时间为:" + line + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                            			Files.append(tmpbkcode + "共导入:" + lineimportcount + "个记录" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        			} else {
                        				Files.append(tmpbkcode + "成交量记录导入起始时间为:" + line + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                            			Files.append(tmpbkcode + "共导入:" + lineimportcount + "个记录" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        			}
                        		}
                    		} catch (Exception e) { logger.debug("不是有日期的数据行");}
                        }
                    } 
                    nextend--;  
                }  
                nextend--;  
                rf.seek(nextend);  
                if (nextend == 0) {// 当文件指针退至文件开始处，输出第一行   文件第一行是文件抬头，不需要处理
                    //logger.debug(new String(rf.readLine().getBytes("ISO-8859-1"), charset ));  
                }  
            }  
        } catch (FileNotFoundException e) {  e.printStackTrace();  
        } catch (IOException e) {  e.printStackTrace();  
        } finally {  
            try {   if (rf != null)  rf.close();        rf = null;     } catch (IOException e) { e.printStackTrace(); }  
        }  
        
        return nodeinsertdata;
	}
	/*
	 * 同步个股成交量信息，参数为交易所 SH/SZ
	 */
	public File refreshTDXGeGuVolAmoToDb(String jiaoyisuo) 
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步个股成交量信息.tmp");
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		Collection<BkChanYeLianTreeNode> allgegucode = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXGG,jiaoyisuo);		

		getTDXGeGuShuJuJiLuMaxMinDateByJiaoYiSuo (jiaoyisuo);
		
		String optTable = null;
		if(jiaoyisuo.equalsIgnoreCase("SZ")  )
			optTable = "通达信深交所股票每日交易信息";
		else if(jiaoyisuo.equalsIgnoreCase("SH") )
			optTable = "通达信上交所股票每日交易信息";
		
		for(BkChanYeLianTreeNode tmpnode :allgegucode) {
			String tmpbkcode = tmpnode.getMyOwnCode();
			String bkfilename = null;
			if(jiaoyisuo.toLowerCase().equals("sz") )
				bkfilename = (filenamerule.replaceAll("YY","SZ")).replaceAll("XXXXXX", tmpbkcode);
			else if(jiaoyisuo.toLowerCase().equals("sh") ) 
				bkfilename = (filenamerule.replaceAll("YY","SH")).replaceAll("XXXXXX", tmpbkcode);
			File tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {  
				logger.debug("读取" + bkfilename + "发生错误！");
			    continue; 
			} 

			LocalDate ldlastestdbrecordsdate = ((Stock)tmpnode).getShuJuJiLuInfo().getJyjlmaxdate();
			Boolean newdataimported = setVolAmoRecordsFromFileToDatabase(tmpnode,tmpbkfile,ldlastestdbrecordsdate,optTable,dateRule,tmprecordfile);
				
				//用户同步完个股和板块成交量后，要把个股的TXT转为CSV FILE，这是因为复权问题，因为数据库存放的是当前的成交数据，如果发生复权，前面所有的OHLC都会改变，
				//重新把数据库中的数据改写一遍成本很高，所以个股每天的OHLC数据直接从复权的TXT转化的CSV中读取。这里先把TXT 都转为CSV，存放到指定位置
				if(newdataimported != null && newdataimported) {
					String csvfilepath = sysconfig.getCsvPathOfExportedTDXVOLFiles();
					ConvertTxtToCsv cttc = new ConvertTxtToCsv ();
					cttc.convertTxtFileToCsv(exportath + "/" + bkfilename, csvfilepath + bkfilename.replace("TXT", "CSV"));
					cttc = null;
				}
		}
		
		return tmprecordfile;
	}
	/*
	 * 
	 */
	public void setBanKuaiLongTou (BanKuai bankuai, String stockcode,Boolean longtouornot)
	{
		String bkcode = bankuai.getMyOwnCode();
		String bktypetable = bankuai.getShuJuJiLuInfo().getGuPiaoBanKuaiDuiYingBiao();
		if(bktypetable == null) {
			this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SH");
		    this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SZ");
//		    this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("BK");
		}
		String sqlupdatestat = "UPDATE " + bktypetable  + " SET 板块龙头 = " +  longtouornot  + 
				" WHERE 板块代码 = '" + bkcode + 
				"' AND 股票代码 = '" + stockcode + "'" +
				"  AND  ISNULL(移除时间)"
				;
		try {
			connectdb.sqlUpdateStatExecute(sqlupdatestat);
		} catch (MysqlDataTruncation e) {e.printStackTrace();
		} catch (SQLException e) {e.printStackTrace();
		}
	}
	/*
	 * 修改板块中某个个股的权重
	 */
	public void setStockWeightInBanKuai(BanKuai bankuai, String stockcode, int weight) 
	{
		String bkcode = bankuai.getMyOwnCode();
		String bktypetable = ((BanKuai)bankuai).getShuJuJiLuInfo().getGuPiaoBanKuaiDuiYingBiao();
		
		String sqlupdatestat = "UPDATE " + bktypetable  + " SET 股票权重 = " +  weight  + 
				" WHERE 板块代码 = '" + bkcode + 
				"' AND 股票代码 = '" + stockcode + "'" +
				"  AND  ISNULL(移除时间)"
				;
		try {	connectdb.sqlUpdateStatExecute(sqlupdatestat);
		} catch (MysqlDataTruncation e) {			e.printStackTrace();
		} catch (SQLException e) {	System.out.println(sqlupdatestat + "\r\n");	e.printStackTrace();	}
	}
	/*
	 * 通达信的行业与个股对应文件
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
			
			基本目录：T0002blocknew
			配置文件：blocknew.cfg 记录自定义的板块名称和文件头
			配置文件存储格式：
			1) 每个板块120字节
			2) 板块名称50字节
			3) 板块文件名头70字节
			// 自定义板块概述文件格式，T0002blocknewblocknew.cfg
			struct TTDXUserBlockRecord
			{
			char szName[50];
			char szAbbr[70]; // 也是文件名称前缀 T0002blocknewxxxx.blk
			};
			
			板块列表文件: *.blk
			1) 每行一条记录：每个记录7个数字：
			a) 市场代码1位：0 – 深市；1 – 沪市
			b) 股票代码6位
			c) 行结束符：rn
			T0002blocknewZXG.blk
			市场 股票代码
			1999999
			0399001
			0399005
			0399006
			1000016
			1000300
			0399330
	 */
	/*
	 * 
	 */
	public File refreshTDXZDYBanKuai (Map<String, String> neededimportedzdybkmap)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步通达信自定义板块报告.txt");
		this.refreshTDXZiDingYiBanKuaiToGeGu(neededimportedzdybkmap, tmprecordfile);
		
		return tmprecordfile;
	}

	/*
	 * 更新自定义板块和个股的对应关系
	 */
	private File refreshTDXZiDingYiBanKuaiToGeGu (Map<String, String> neededimportedzdybkmap, File tmprecordfile) 
	{
		Set<String> neededimportzdybknames = neededimportedzdybkmap.keySet(); 
		//Set<String> curzdybknames = this.getZdyBkSet ();
		
		for(String newzdybk:neededimportzdybknames) {
			String filename = neededimportedzdybkmap.get(newzdybk); //str是自定义板块的名称
			File zdybk = new File(filename);
			List<String> readLines = null;
			try {	readLines = Files.readLines(zdybk,Charsets.UTF_8,new ParseBanKuaiWeeklyFielGetStocksProcessor ());
			} catch (java.io.FileNotFoundException e) {
				logger.info("自定义板块文件没有找到，导入自定义板块失败！");
				return null;
			} catch (IOException e) {	e.printStackTrace();	return null;}
			Set<String> stocknamesnew = new HashSet<String>(readLines);
			
			//找出数据库中现有的该板块个股
			Set<String> tmpstockcodesetold = new HashSet<String>();
			CachedRowSetImpl rs = null;
			try {
			    	String sqlquerystat = "SELECT *  FROM 股票通达信自定义板块对应表  WHERE  自定义板块 = " 
			    							+ "'"  + newzdybk.trim() + "'"
			    							+ "AND 移除时间 IS  NULL"
			    							;
			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	while(rs.next()) {  
			        	String stockcode = rs.getString("股票代码");
			        	tmpstockcodesetold.add(stockcode);
			        }
			       
			    }catch(java.lang.NullPointerException e){    	e.printStackTrace();
			    } catch (SQLException e) { 	e.printStackTrace();
			    }catch(Exception e){  	e.printStackTrace();
			    } finally {
			    	if(rs != null)
						try {	rs.close();	rs = null;
						} catch (SQLException e) {	e.printStackTrace();	}
			    }
			
			 //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
		    SetView<String> differencebankuainew = Sets.difference(stocknamesnew, tmpstockcodesetold ); 
   		    for (String newstock : differencebankuainew) {
   				
   				String sqlinsertstat = "INSERT INTO  股票通达信自定义板块对应表(股票代码,自定义板块) values ("
   						+ "'" + newstock.trim() + "'" + ","
   						+ "'" + newzdybk.trim() + "'" 
   						+ ")"
   						;
   				//logger.debug(sqlinsertstat);
   				try {	int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();	}
   				
   			}
   		    differencebankuainew = null;
   		 
   	        //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要从数据库中添加移除时间
   		    
   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,stocknamesnew  );
   	        for (String oldstock : differencebankuaiold) {
   	        	String sqlupdatestat = "UPDATE 股票通达信自定义板块对应表  "
						+ "  SET 移除时间 = " + "'" +  LocalDate.now().toString()  + "'"
						+ "  WHERE  股票通达信自定义板块对应表 .`股票代码` = " + "'" + oldstock.trim().trim() + "'"
						+ "  AND 自定义板块 = " +  "'" + newzdybk.trim() + "'"
						+ "  AND isnull(移除时间)"
						;
   	        	//logger.debug(sqldeletetstat);
   	    		try {	int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqlupdatestat);
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();	}
   	        }
   	        
   	     differencebankuaiold = null;
   	     stocknamesnew = null;
		}
	
		neededimportzdybknames = null;
		return tmprecordfile;
	}
	
	/*
	 * 获取通达信系统里面的自定义板块列表，并更新数据库自定义板块
	 */
	public HashMap<String, String> getTDXZiDingYiBanKuaiList ()
	{
		File file = new File(sysconfig.getTDXSysZDYBanKuaiFile () );
		logger.debug(sysconfig.getTDXSysZDYBanKuaiFile ());
		 if(!file.exists() ) {
			 logger.debug("Zdy BanKuai File not exist");
			 return null;
		 }
		 
		 int addednumber =0;
		 HashMap<String,String> zdybkmap = Maps.newHashMap();
		 FileInputStream in = null ;  
		 BufferedInputStream dis = null;
		 try {
			   in = new FileInputStream(file);  
			   dis = new BufferedInputStream(in);
              
              int eachbankuaibytenumber = 120;
              byte[] itemBuf2 = new byte[eachbankuaibytenumber];
              int i=0;
              
              while (dis.read(itemBuf2, 0, eachbankuaibytenumber) != -1) {
           	   //板块名称
           	   String zdybankuainame = (new String(itemBuf2,0,48)).trim();
           	   logger.debug(zdybankuainame);
           	   
           	   //板块对应file
               String bkfilename = sysconfig.toUNIXpath(file.getParent()) + "/" + new String(itemBuf2,49,eachbankuaibytenumber-51).trim() +".blk";
               logger.debug(bkfilename);
               
               zdybkmap.put(zdybankuainame, bkfilename );
               
              }
		 } catch (Exception e) { e.printStackTrace();	 return null;
		 } finally {
			 if(in != null)
				try {	in.close();	} catch (IOException e) {	e.printStackTrace();	}
			 if(dis != null)
				try {	dis.close();} catch (IOException e) {e.printStackTrace();		}
		 }
		
		 this.refreshTDXZiDingYiBanKuai (zdybkmap);
		 return zdybkmap;
	}
	/*
	 * 找出数据库中已经保存通达信自定义板块
	 */
	private HashMap<String, BanKuai> initializeSystemAllZdyBanKuaiList()  
	{
		//HashMap<String,BanKuai> sysbankuailiebiaoinfo;
//		private  HashMap<String,BanKuai> zdybankuailiebiaoinfo;
		HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();

		String sqlquerystat = "SELECT *  FROM 通达信自定义板块列表"
							   ;   
		logger.debug(sqlquerystat);
		CachedRowSetImpl  rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	    	
//	        rs.last();  
//	        int rows = rs.getRow();  
//	        data = new Object[rows][];    
//	        int columnCount = 3;//列数  
//	        rs.first();  
	        //int k = 0;  
	        while(rs.next()) {  
	        	BanKuai tmpbk = new BanKuai (rs.getString("ID"),rs.getString("板块名称") );
	        	tmpsysbankuailiebiaoinfo.put(rs.getString("板块名称"), tmpbk);
	        }
	    }catch(java.lang.NullPointerException e){e.printStackTrace();
	    } catch (SQLException e) {   	e.printStackTrace();
	    }catch(Exception e){  	e.printStackTrace();
	    } finally {
	    	try {	rs.close ();	rs = null;	} catch (SQLException e) {	e.printStackTrace();	}
	    }
	    
	    return  tmpsysbankuailiebiaoinfo;
   
	}
	/*
	 * 根据最新信息，同步 更新数据库中的通达信的自定义板块
	 */
	private int refreshTDXZiDingYiBanKuai (HashMap<String, String> neededimportedzdybkmap)
	{
		Set<String> neededimportzdybknames = neededimportedzdybkmap.keySet(); 
		Set<String> curzdybknames = this.initializeSystemAllZdyBanKuaiList().keySet();
		
		 //把新的自定义板块加入数据库
	      SetView<String> differencebankuainew = Sets.difference(neededimportzdybknames, curzdybknames ); 
		    for (String str : differencebankuainew) {
				String sqlinsertstat = "INSERT INTO  通达信自定义板块列表(板块名称) values ("
						+ "'" + str.trim() + "'" 
						  
						+ ")"
						;
				//logger.debug(sqlinsertstat);
				try {	int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();	}

			}
		    differencebankuainew = null;
		    
	        //把旧的自定义板块删除出数据库
	        SetView<String> differencebankuaiold = Sets.difference(curzdybknames,neededimportzdybknames  );
	        for (String str : differencebankuaiold) {
	        	String sqldeletetstat = "DELETE  FROM 通达信自定义板块列表 "
	        							+ " WHERE 板块名称=" + "'" + str.trim() + "'" 
	        							;
	        	//logger.debug(sqldeletetstat);
	    		int autoIncKeyFromApi;
				try {
					autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
				} catch (MysqlDataTruncation e1) {e1.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();}

	    		
	    		sqldeletetstat = "DELETE  FROM 股票通达信自定义板块对应表 "
						+ " WHERE 自定义板块=" + "'" + str.trim() + "'" 
						;
				//logger.debug(sqldeletetstat);
				try {
					autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();}
	        }
	        
	        differencebankuaiold = null;
	        neededimportzdybknames = null;
	        curzdybknames = null;

	        return 1;
	}
		/*
		 * 获取板块或个股的买入卖出关注送配股等信息
		 */
		public BkChanYeLianTreeNode getNodeGzMrMcYkInfo (TDXNodes node,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
		{
			if(node == null)	return null;
			if(node.getNodeJiBenMian().getZdgzmrmcykRecords() != null &&  !node.getNodeJiBenMian().getZdgzmrmcykRecords().isEmpty())
	        	return node;
//			selecteddatestart = selecteddatestart.with(DayOfWeek.MONDAY);
//			selecteddateend = selecteddateend.with(DayOfWeek.FRIDAY);
			
			if(period.equals(NodeGivenPeriodDataItem.DAY)) //调用日线查询函数
				; 
			else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //调用月线查询函数
				;
			
			NodeXPeriodData nodewkperioddata = node.getNodeXPeroidData(period);
			String sqlquerystat;
			if(node.getType() == BkChanYeLianTreeNode.TDXGG || node.getType() == BkChanYeLianTreeNode.BKGEGU) 
				sqlquerystat = getZdgzMrmcYingKuiSQLForStock (node);
			else //板块和个股是不一样的，板块没有买入卖出信息
				sqlquerystat = getZdgzMrmcYingKuiSQLForBanKuai (node);
			
			 CachedRowSetImpl rs = null;
			 try {
				 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
				 List<Object[]> data = new ArrayList<>();
				 int columnCount = 6;//列数
			     while(rs.next()) {   //{ "日期", "操作", "说明","ID","操作账户","信息表" };
			            Object[] row = new Object[columnCount];  
			            row[0] = (java.sql.Date) rs.getDate("日期");
			            row[1] = rs.getString(2);
			            row[2] = rs.getString(3);
			            row[3] = rs.getString(4);
			            row[4] = rs.getString(5);
			            row[5] = rs.getString(6);
			            String action = (String) row[1];
						if(action.equals("买入")) {
							org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
							((StockNodesXPeriodData)nodewkperioddata).addMaiRuJiLu(wknum, 1);
						} else
						if(action.equals("卖出")) {
							org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
							((StockNodesXPeriodData)nodewkperioddata).addMaiChuJiLu(wknum, 0);
						} else
						if(action.equals("加入关注")) {
							org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
							((StockNodesXPeriodData)nodewkperioddata).addGzjlToPeriod(wknum, 1);
						} else 
						if(action.equals("移除关注")) {
							org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
							((StockNodesXPeriodData)nodewkperioddata).addGzjlToPeriod(wknum, 0);
						}
						
			            data.add(row);
			        }   
			        if(!data.isEmpty())  	node.getNodeJiBenMian().setZdgzmrmcykRecords( data );
				} catch (SQLException e) {e.printStackTrace();
				} finally {try {rs.close();} catch (SQLException e) {e.printStackTrace();}
					rs = null;
				}
			 
			 return node;
		}
		/*
		 * 获取板块的相关信息
		 */
		private String getZdgzMrmcYingKuiSQLForBanKuai(BkChanYeLianTreeNode node) 
		{
			String stockcode = node.getMyOwnCode();
			
			String serachnodecode ; //除了以下和大盘紧密相关的指数外，其他板块应该只找和自己相关的记录。
			Set<String> zhishucodes = new HashSet<String> ();
			zhishucodes.add("000000");
			zhishucodes.add("999999");
			zhishucodes.add("399001");
			zhishucodes.add("399006");
			zhishucodes.add("399005");
			zhishucodes.add("000852");
			zhishucodes.add("000300");
			zhishucodes.add("000016");
			zhishucodes.add("399673");
			if(zhishucodes.contains(stockcode) ) 
				serachnodecode = "000000";
			else
				serachnodecode = stockcode;
			
			String sqlquerystat = "SELECT zdgz.日期,"
								+ "zdgz.加入移出标志,"
								+ "zdgz.原因描述,"
								+ "zdgz.ID,"
								+ "null,"
								+ "'操作记录重点关注'"
								+ " FROM 操作记录重点关注  zdgz"
								+ " WHERE (zdgz.股票代码 =" + "'" + stockcode + "'"
								+ " OR zdgz.股票代码 ='" + serachnodecode + "')"
								+ " ORDER BY 1 DESC,5,3 " //desc
								;
	     	     
	     return sqlquerystat;
		}
		/*
		 * 获取个股的相关信息
		 */
		private String getZdgzMrmcYingKuiSQLForStock(BkChanYeLianTreeNode node) 
		{
			String stockcode = node.getMyOwnCode();
			String sqlquerystat1= "SELECT ggyk.日期, "
								+ "IF(ggyk.盈亏金额>0,'盈利','亏损') AS 盈亏情况,"
								+ " ggyk.原因描述,"
								+ " ggyk.ID,"
								+ " ggyk.操作账号,"
								+  "'个股盈亏'" 
								+ " FROM  A股个股盈亏 ggyk "
								+ " WHERE ggyk.股票代码 =" + "'" + stockcode + "'" 
								
								+ " \n UNION ALL \n" 
								;
			//logger.debug(sqlquerystat1);
	       String sqlquerystat2="SELECT czjl.日期, " +		""
								+ "IF( czjl.买卖金额=0,'送转股',IF(czjl.挂单 = true,IF(czjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(czjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
								+ " czjl.原因描述,"
								+ " czjl.ID,"
								+ " czjl.买卖账号,"
								+ "'操作记录买卖'" 
								+ " FROM 操作记录买卖 czjl "
								+ "	WHERE czjl.股票代码 =" + "'" + stockcode + "'"
								
								+ " \n UNION ALL \n" 
								;
	       //logger.debug(sqlquerystat2);
	      String sqlquerystat3="SELECT rqczjl.日期,"
								+ "IF( rqczjl.买卖金额=0,'送转股',IF(rqczjl.挂单 = true,IF(rqczjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(rqczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
								+ "rqczjl.原因描述,"
								+ "rqczjl.ID,"
								+ "rqczjl.买卖账号,"
								+ "'操作记录融券买卖'"
								+ " FROM 操作记录融券买卖 rqczjl"
								+ "	WHERE rqczjl.股票代码 =" + "'" + stockcode + "'"
								
								+ " \n UNION ALL \n"
								;
	      //logger.debug(sqlquerystat3);
		 String sqlquerystat4="SELECT rzczjl.日期,"
								+ "IF( rzczjl.买卖金额=0,'送转股',IF(rzczjl.挂单 = true,IF(rzczjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(rzczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
								+ "rzczjl.原因描述,"
								+ "rzczjl.ID,"
								+ "rzczjl.买卖账号,"
								+ "'操作记录融资买卖'"
								+ " FROM 操作记录融资买卖 rzczjl"
								+ " WHERE rzczjl.股票代码 =" + "'" + stockcode + "'"
								
								+ " \n UNION ALL \n"
								;
		 //logger.debug(sqlquerystat4);
	     String sqlquerystat5="SELECT zdgz.日期,"
								+ "zdgz.加入移出标志,"
								+ "zdgz.原因描述,"
								+ "zdgz.ID,"
								+ "null,"
								+ "'操作记录重点关注'"
								+ " FROM 操作记录重点关注  zdgz"
								+ " WHERE zdgz.股票代码 =" + "'" + stockcode + "'"
								

								+ " \n UNION ALL \n"
								;
	     //logger.debug(sqlquerystat5);
	     String guanzhubk = sysconfig.getCurZdyBanKuaiOfGuanZhuGeGu();
	     String sqlquerystat6="SELECT zdgz.加入时间,"
					+ "'加入关注',"
					+ "null, "
					+ "null,"
					+ "null,"
					+ "'操作记录重点关注'"
					+ " FROM 股票通达信自定义板块对应表  zdgz" 
					+ " WHERE zdgz.股票代码 ="  + "'" + stockcode + "'"
					+ " AND 自定义板块="  + "'" + guanzhubk + "'" 
 					

					+ " \n UNION ALL \n"
					;
	     String sqlquerystat7="SELECT zdgz.移除时间,"
					+ "'移除关注',"
					+ "null, "
					+ "null,"
					+ "null,"
					+ "'操作记录重点关注'"
					+ " FROM 股票通达信自定义板块对应表  zdgz" 
					+ " WHERE zdgz.股票代码 ="  + "'" + stockcode + "'"
					+ " AND 自定义板块="  + "'" + guanzhubk + "'" 
					+ " AND zdgz.移除时间 IS NOT NULL \n"
					+ " ORDER BY 1 DESC,5,3 " //desc
					;
		
	     String sqlquerystat = sqlquerystat1 + sqlquerystat2 + sqlquerystat3 + sqlquerystat4 + sqlquerystat5 + sqlquerystat6 +sqlquerystat7;
	     return sqlquerystat;
		}
		/*
		 * 
		 */
		public String getMingRiJiHua() 
		{
			String sqlquerystat = 	" SELECT * FROM 操作记录重点关注   \r\n" + 
									" WHERE 日期 >= date_sub(curdate(),interval 30 day) \r\n" + 
									"		and 日期 <= curdate()\r\n" + 
									" AND 加入移出标志 = '明日计划' " 
									;
			CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			String pmdresult = "";
			try  {     
		        rs.last();  
		        int rows = rs.getRow();  
		        rs.first();  
		        int k = 0;  
		        //while(rs.next())
		        for(int j=0;j<rows;j++) { 
		        	pmdresult = pmdresult + "--*(" + rs.getDate("日期") + ")(" + rs.getString("股票代码") + ")  " + rs.getString("原因描述") + " ";
		            rs.next();
		        } 
		        
		    }catch(java.lang.NullPointerException e){ 
		    	e.printStackTrace();
		    }catch(Exception e) {
		    	e.printStackTrace();
		    } finally {
		    	if(rs != null)
					try {
						rs.close();
						rs = null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
		    }
			
			return pmdresult;
		}
		public int setMingRiJiHua(BkChanYeLianTreeNode node, String msg)
		{
			String sqlinsertstat = "INSERT INTO  操作记录重点关注(股票代码,日期,加入移出标志,原因描述) values ("
						+ " '" + node.getMyOwnCode() + "'" + ","
						+ " '" + LocalDate.now().toString() + "'" + ","
						+ " '明日计划'" + ","
						+ " '" + msg + "'"
						+ ")"
						;
    	   logger.debug(sqlinsertstat);
    	   int autoIncKeyFromApi = 0;
			try {
				autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
			} catch (MysqlDataTruncation e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
				
			return autoIncKeyFromApi;
		}

		
		private String formateDateForDiffDatabase (String databasetype,String dbdate)
		{
			if(dbdate != null) {
				switch(databasetype) {
				case "access": return "#" + dbdate + "#";
								
				case "mysql":  return "\"" + dbdate + "\"";
								
				}
			} else
				return null;
			return null;
		}
		/*
		 * 
		 */
		public Boolean updateStockNewInfoToDb(BkChanYeLianTreeNode nodeshouldbedisplayed) 
		{
			String dategainiants = null;
			
			String txtareainputgainiants = null;
			String datequanshangpj = null;
			String txtfldinputquanshangpj;
			String datefumianxx = null;
			String txtfldinputfumianxx = null;
			
			String stockname = "'" +  nodeshouldbedisplayed.getMyOwnName().trim() + "'" ;
			String stockcode = "'" +  nodeshouldbedisplayed.getMyOwnCode().trim() + "'" ;
			
			try {
					txtareainputgainiants = "'" + nodeshouldbedisplayed.getNodeJiBenMian().getGainiantishi().trim() + "'";
			} catch (java.lang.NullPointerException  e) {
			}

			txtfldinputquanshangpj = "'" + nodeshouldbedisplayed.getNodeJiBenMian().getQuanshangpingji().trim() + "'";
		    
			txtfldinputfumianxx = "'" + nodeshouldbedisplayed.getNodeJiBenMian().getFumianxiaoxi() + "'";
		    
//		    String txtfldinputzhengxiangguan = "'" + nodeshouldbedisplayed.getNodeJiBenMian().getZhengxiangguan().trim() + "'";
//		    String txtfldinputfuxiangguan = "'" + nodeshouldbedisplayed.getNodeJiBenMian().getFuxiangguan().trim() + "'";
		    
//		    String keHuCustom = nodeshouldbedisplayed.getNodeJiBenMian().getKeHuCustom();
//		    String jingZhengDuiShou = nodeshouldbedisplayed.getNodeJiBenMian().getJingZhengDuiShou();
	    
			 if(nodeshouldbedisplayed.getType() == 6) {//是股票{
				 HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
				 String sqlupdatestat= "UPDATE A股  SET "
							+ " 股票名称=" + stockname +","
							+ " 概念时间=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getGainiantishidate()) ) +","
							+ " 概念板块提醒=" + txtareainputgainiants +","
							+ " 券商评级时间=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getQuanshangpingjidate() ) ) +","
							+ " 券商评级提醒=" + txtfldinputquanshangpj +","
							+ " 负面消息时间=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getFumianxiaoxidate()  ) ) +","
							+ " 负面消息=" + txtfldinputfumianxx
//							+","
//							+ " 正相关及客户=" + txtfldinputzhengxiangguan +","
//							+ " 负相关及竞争对手=" + txtfldinputfuxiangguan +","
//							+ " 客户=" + "'" + keHuCustom +"'" + ","
//							+ " 竞争对手=" + "'" + jingZhengDuiShou + "'"  
							+ " WHERE 股票代码=" + stockcode
							;
				 sqlstatmap.put("mysql", sqlupdatestat);
				 //logger.debug(sqlupdatestat);
				 
				 try {
					if( connectdb.sqlUpdateStatExecute(sqlupdatestat) !=0)
						 return true;
					 else return false;
				} catch (MysqlDataTruncation e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 } else { //是板块
				 HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
				 String actiontable =  "通达信板块列表";
				 
				 String sqlinsertstat = "UPDATE " + actiontable + " SET "
						 	+ " 板块名称=" + stockname +","
							+ " 概念时间=" + formateDateForDiffDatabase("mysql",CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getGainiantishidate()) ) +","
							+ " 概念板块提醒=" + txtareainputgainiants +","
							+ " 券商评级时间=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getQuanshangpingjidate() ) ) +","
							+ " 券商评级提醒=" + txtfldinputquanshangpj +","
							+ " 负面消息时间=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getFumianxiaoxidate()  ) ) +","
							+ " 负面消息=" + txtfldinputfumianxx 
//							+","
//							+ " 正相关及客户=" + txtfldinputzhengxiangguan +","
//							+ " 负相关及竞争对手=" + txtfldinputfuxiangguan +","
//							+ " 客户=" + "'" + keHuCustom +"'" + ","
//							+ " 竞争对手=" + "'" + jingZhengDuiShou + "'"  
							+ " WHERE 板块ID=" + stockcode
							;
				 //logger.debug(sqlinsertstat); 
				 sqlstatmap.put("mysql", sqlinsertstat);
				 try {
					if( connectdb.sqlUpdateStatExecute(sqlinsertstat) !=0 ) {
						 return true;
					 } else return false;
				} catch (MysqlDataTruncation e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 
			 return null;
		}
		/*
		 * 
		 */
		public BanKuai getBanKuaiGeGuGzMrMcYkInfo (BanKuai bk)
		{
			List<BkChanYeLianTreeNode> bkshgegu = bk.getAllGeGuOfBanKuaiInHistory();
			if(bkshgegu== null || bkshgegu.isEmpty())
				return bk;
			
			String bkggstr = ""; //int ggcount = 0;
			for(BkChanYeLianTreeNode stockofbk : bkshgegu) {
				StockOfBanKuai stkbk = (StockOfBanKuai)stockofbk;
				if(stkbk.getStock().getNodeJiBenMian().getZdgzmrmcykRecords()!= null &&  !stkbk.getStock().getNodeJiBenMian().getZdgzmrmcykRecords().isEmpty())
		        	continue;
				else {
					bkggstr  = bkggstr + "'" + stockofbk.getMyOwnCode() +  "', ";
//					ggcount ++;
				}
			}
			bkggstr = bkggstr.substring(0, bkggstr.length() -2);
			
			String sqlquerystat = "\r\n" + 
					"SELECT ggyk.股票代码, ggyk.日期, IF(ggyk.盈亏金额>0,'盈利','亏损') AS 盈亏情况, ggyk.原因描述, ggyk.ID, ggyk.操作账号,'个股盈亏' FROM  A股个股盈亏 ggyk \r\n" + 
					" WHERE ggyk.股票代码 IN ( " + bkggstr + ") \r\n" + 
					" UNION ALL \r\n" + 
					"SELECT czjl.股票代码, czjl.日期, IF( czjl.买卖金额=0,'送转股',IF(czjl.挂单 = true,IF(czjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(czjl.买入卖出标志,'买入','卖出') )  ) AS 买卖, czjl.原因描述, czjl.ID, czjl.买卖账号,'操作记录买卖' FROM 操作记录买卖 czjl \r\n" + 
					"	WHERE czjl.股票代码 IN (" + bkggstr + ") \r\n" + 
					" UNION ALL \r\n" + 
					"SELECT rqczjl.股票代码, rqczjl.日期,IF( rqczjl.买卖金额=0,'送转股',IF(rqczjl.挂单 = true,IF(rqczjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(rqczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,rqczjl.原因描述,rqczjl.ID,rqczjl.买卖账号,'操作记录融券买卖' FROM 操作记录融券买卖 rqczjl	\r\n" + 
					"WHERE rqczjl.股票代码 IN (" + bkggstr + ") \r\n" + 
					" UNION ALL \r\n" + 
					"SELECT rzczjl.股票代码 , rzczjl.日期,IF( rzczjl.买卖金额=0,'送转股',IF(rzczjl.挂单 = true,IF(rzczjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(rzczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,rzczjl.原因描述,rzczjl.ID,rzczjl.买卖账号,'操作记录融资买卖' FROM 操作记录融资买卖 rzczjl\r\n" + 
					" WHERE rzczjl.股票代码 IN (" + bkggstr + ") \r\n" + 
					" UNION ALL \r\n" + 
					"SELECT zdgz.股票代码, zdgz.日期,zdgz.加入移出标志,zdgz.原因描述,zdgz.ID,null,'操作记录重点关注' FROM 操作记录重点关注  zdgz \r\n" + 
					"WHERE zdgz.股票代码 IN (" + bkggstr + ") \r\n" + 
					" UNION ALL \r\n" + 
					"SELECT zdgz.股票代码, zdgz.加入时间,'加入关注',null, null,null,'操作记录重点关注' FROM 股票通达信自定义板块对应表  zdgz \r\n" + 
					"WHERE zdgz.股票代码 IN (" + bkggstr + ") \r\n" + 
					" AND 自定义板块='模型验证' \r\n" + 
					" UNION ALL \r\n" + 
					"SELECT zdgz.股票代码, zdgz.移除时间,'移除关注',null, null,null,'操作记录重点关注' FROM 股票通达信自定义板块对应表  zdgz\r\n" + 
					" WHERE zdgz.股票代码 IN (" + bkggstr + ") \r\n" + 
					"  AND 自定义板块='模型验证' AND zdgz.移除时间 IS NOT NULL \r\n" + 
					" ORDER BY 1 ,2 DESC "
					;
			CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat); 
			String curstockcode = ""; Stock tmpstock = null;
			try  {  
				while(rs.next()) {
					String stockcode = rs.getString("股票代码");
					Stock stock = (Stock) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(stockcode, BkChanYeLianTreeNode.TDXGG);
					NodeXPeriodData nodewkperioddata = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
					Object[] row = new Object[6];  
		            row[0] = (java.sql.Date) rs.getDate("日期");
		            row[1] = rs.getString(3);
		            row[2] = rs.getString(4);
		            row[3] = rs.getString(5);
		            row[4] = rs.getString(6);
		            row[5] = rs.getString(7);
		            String action = (String) row[1];
					if(action.equals("买入")) {
						org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
						((StockNodesXPeriodData)nodewkperioddata).addMaiRuJiLu(wknum, 1);
					} else
					if(action.equals("卖出")) {
						org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
						((StockNodesXPeriodData)nodewkperioddata).addMaiChuJiLu(wknum, 0);
					} else
					if(action.equals("加入关注")) {
						org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
						((StockNodesXPeriodData)nodewkperioddata).addGzjlToPeriod(wknum, 1);
					} else 
					if(action.equals("移除关注")) {
						org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
						((StockNodesXPeriodData)nodewkperioddata).addGzjlToPeriod(wknum, 0);
					}
					
					stock.getNodeJiBenMian().addZdgzmrmcykRecord(row);
				}
				
			 } catch(java.lang.NullPointerException e){	e.printStackTrace();
			 } catch(Exception e) {e.printStackTrace();
			 } finally {
			    	if(rs != null) {	try {rs.close();	} catch (SQLException e) {	e.printStackTrace();}
			    	rs = null;    }
			 }
			
			return bk;
		}
		/*
		 * 
		 */
		public BanKuai getTDXBanKuaiSetForBanKuaiGeGu (BanKuai bk) 
		{
			List<BkChanYeLianTreeNode> bkgg = bk.getAllGeGuOfBanKuaiInHistory();
			if(bkgg == null || bkgg.isEmpty())
				return bk;
			
			List<Stock> bkstock = new ArrayList<> ();
			for(BkChanYeLianTreeNode tmpbkgg : bkgg) 
				bkstock.add( ((StockOfBanKuai)tmpbkgg).getStock()    );
			this.getTDXBanKuaiSetForStocks(bkstock);
			
			return bk;
		}
		/*
		 * 
		 */
		public Stock getTDXBanKuaiSetForStock(Stock stock)
		{
			List<Stock> nodeset = new ArrayList<>();
			nodeset.add((Stock)stock);
			this.getTDXBanKuaiSetForStocks (nodeset ); //通达信板块信息
			
			return stock;
		}
		/*
		 * 
		 */
		public List<Stock> getTDXBanKuaiSetForStocks(List<Stock> stocklist) 
		{
			String bkggstr = "";
			for(Stock stock : stocklist ) {
				if(  stock.getGeGuCurSuoShuTDXSysBanKuaiList() != null && ! stock.getGeGuCurSuoShuTDXSysBanKuaiList().isEmpty() )
					continue;
				else bkggstr  = bkggstr + "'" + stock.getMyOwnCode() +  "', ";
			}
			if(bkggstr.isEmpty())
				return stocklist;
			else
				bkggstr = bkggstr.substring(0, bkggstr.length() -2);
			
			BanKuaiAndStockTree treeofstkbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks() ;
//			Set<BkChanYeLianTreeNode> stockbanksset = new HashSet<>();
			String sqlquerystat = 
						"SELECT gpgn.`股票代码`,    gpgn.板块代码 板块代码, tdxbk.`板块名称` 板块名称 ,  gpgn.`股票权重`\r\n" + 
						"FROM 股票通达信概念板块对应表 gpgn, 通达信板块列表 tdxbk  \r\n" + 
						"WHERE 股票代码 IN (" + bkggstr + " )  AND gpgn.板块代码 = tdxbk.`板块ID` AND ISNULL(移除时间)\r\n" + 
						"GROUP BY 股票代码, 板块代码\r\n" + 
						"UNION  \r\n" + 
						"\r\n" + 
						"SELECT gphy.`股票代码`,  gphy.板块代码 板块代码, tdxbk.`板块名称` 板块名称 , gphy.`股票权重`\r\n" + 
						"FROM 股票通达信行业板块对应表 gphy, 通达信板块列表 tdxbk  \r\n" + 
						"WHERE 股票代码 IN (" + bkggstr + " )  AND gphy.`板块代码` = tdxbk.`板块ID` AND ISNULL(移除时间)\r\n" + 
						"GROUP BY 股票代码, 板块代码\r\n" + 
						"UNION \r\n" + 
						" \r\n" + 
						"SELECT gpfg.`股票代码`, gpfg.`板块代码` 板块代码, tdxbk.`板块名称` 板块名称  ,  gpfg.`股票权重`\r\n" + 
						"FROM 股票通达信风格板块对应表 gpfg, 通达信板块列表 tdxbk \r\n" + 
						"WHERE 股票代码 IN (" + bkggstr + " )  AND gpfg.`板块代码` = tdxbk.`板块ID` AND ISNULL(移除时间)\r\n" + 
						"GROUP BY 股票代码, 板块代码\r\n" + 
						"UNION \r\n" + 
						" \r\n" + 
						"SELECT gpfg.`股票代码`,  gpfg.`板块代码` 板块代码, tdxbk.`板块名称` 板块名称  ,  gpfg.`股票权重`\r\n" + 
						"FROM 股票通达信交易所指数对应表 gpfg, 通达信板块列表 tdxbk \r\n" + 
						"WHERE 股票代码 IN (" + bkggstr + ")  AND gpfg.`板块代码` = tdxbk.`板块ID` AND ISNULL(移除时间) \r\n" + 
						"GROUP BY 股票代码, 板块代码"
						;
			CachedRowSetImpl rs_gn = connectdb.sqlQueryStatExecute(sqlquerystat); 
			String curstockcode = ""; Stock tmpstock = null;
			try  {     
			        while(rs_gn.next()) {
			        	String stockcode = rs_gn.getString("股票代码");
			        	if(!stockcode.equalsIgnoreCase(curstockcode)) {
			        		curstockcode = stockcode;
			        		tmpstock = (Stock)treeofstkbk.getSpecificNodeByHypyOrCode (stockcode,BkChanYeLianTreeNode.TDXGG);
			        	}
			        	
			        	String bkcode = rs_gn.getString("板块代码");
			        	Integer quanzhong = rs_gn.getInt("股票权重");
			        	BanKuai bk = (BanKuai)treeofstkbk.getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
						StockOfBanKuai bkofst = new StockOfBanKuai(bk,tmpstock);
						bkofst.setStockQuanZhong (quanzhong);
						bk.addNewBanKuaiGeGu(bkofst);
//						stockbanksset.add(bk);
						tmpstock.addGeGuTDXSysBanKuai (bk);
			        } 
			    } catch(java.lang.NullPointerException e){	e.printStackTrace();
			    } catch(Exception e) {e.printStackTrace();
			    } finally {
			    	if(rs_gn != null) {	try {rs_gn.close();	} catch (SQLException e) {	e.printStackTrace();}
			    	rs_gn = null;    }
			    }

			return stocklist;
		}

		public String getStockCodeByName(String stockname) 
		{
			String sqlquerystat= "SELECT 股票代码 FROM A股   WHERE 股票名称 =" +"'" + stockname +"'" ;
			//logger.debug(sqlquerystat);
			CachedRowSetImpl rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);

			String stockcode = null;
			try {
				//rsagu.first();
				while (rsagu.next()) {
					stockcode = rsagu.getString("股票代码");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rsagu.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
		
			return stockcode;
		}

		public String getStockCodeByEverUsedName(String stockname) 
		{
			HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
			String sqlquerystat = null;
			sqlquerystat= "SELECT 股票代码 FROM A股   WHERE 曾用名  LIKE" +"'%" + stockname +"%'" ;
			sqlstatmap.put("mysql", sqlquerystat);
//			sqlquerystat= "SELECT 股票代码 FROM A股   WHERE 曾用名  LIKE" +"'*" + stockname +"*%'" ;
//			sqlstatmap.put("access", sqlquerystat);

			CachedRowSetImpl rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);

			String stockcode = "";
			try {
				//rsagu.first();
				while (rsagu.next()) 
					stockcode = rsagu.getString("股票代码");
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rsagu.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
		
			return stockcode;
		}

		/*
		 *同步股票现用名字信息 
		 */
		public Integer refreshCurrentUsedStorkNameOfShenZhen()
		{
			File file = new File(sysconfig.getTDXShenZhenShuNameFile() );
			
			if(!file.exists() ) {
				 logger.debug("File not exist");
				 return -1;
			 }
			
			 int addednumber =0;
			 BufferedInputStream dis = null;
			 FileInputStream in = null;
			 try {
				    in = new FileInputStream(file);  
				    dis = new BufferedInputStream(in);
	               
	               int fileheadbytenumber = 50;
	               byte[] itemBuf = new byte[fileheadbytenumber];
	               dis.read(itemBuf, 0, fileheadbytenumber); 
	               String fileHead =new String(itemBuf,0,fileheadbytenumber);  
	               logger.debug(fileHead);
	               
	               int sigbk = 18*16+12+14;
	               byte[] itemBuf2 = new byte[sigbk];
	               int i=0;
//	               Files.append("开始导入通达信股票指数板块对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	               while (dis.read(itemBuf2, 0, sigbk) != -1) {
	            	   String zhishuline =new String(itemBuf2,0,sigbk);
//	            	   logger.debug(zhishuline);
	            	   String zhishucode = zhishuline.trim().substring(0, 6);
	            	   logger.debug(zhishucode);
	            	   
//	            	   if(  !zhishucode.startsWith("000") && !zhishucode.startsWith("001") && !zhishucode.startsWith("002") && !zhishucode.startsWith("300"))
	            	   if( !sysconfig.isShenZhengStock(zhishucode) )
	            		   continue;
	            	   
	            	   List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(zhishuline.substring(6, zhishuline.length()));
	            	   String zhishuname = null;
	            	   try {
	            		   logger.debug(tmplinelist.get(0));
		            	    zhishuname = tmplinelist.get(0).trim().substring(0, 6).trim();
	            	   } catch (java.lang.StringIndexOutOfBoundsException ex) {
	            		   List<String> tmplinepartnamelist = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(1).trim());
	            		   zhishuname = tmplinelist.get(0).trim() + tmplinepartnamelist.get(0).trim();
	            		   if(zhishuname.trim().length() ==2 ) {
	            			   try{
	            			   List<String> tmplinepartnamelist2 = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(2).trim());
	            			   zhishuname = zhishuname + tmplinepartnamelist2.get(0).trim();
	            			   } catch (java.lang.IndexOutOfBoundsException e) {
//	            				   e.printStackTrace();
	            				   logger.debug(tmplinelist + "出错");
	            				   
	            			   }
	            		   }
	            			   
	            	   }
	            	   if(zhishuname.trim().length() <4)
	            		   logger.debug(zhishuname);
	            	   if(zhishuname.trim().length() ==2 )
	            		   logger.debug(zhishuname);
	            	   
	            	   String sqlinsertstat = "Update a股 set 股票名称 = '" + zhishuname.trim() + "'\r\n" + 
	            			   					", 所属交易所 = 'SZ' "	+ "\r\n" +
	            			   				   " WHERE 股票代码 = '" + zhishucode.trim() + "' "
	   						   ;
	            	   logger.debug(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//		                Files.append("加入：" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	               }
	               
			 } catch (Exception e) {
				 e.printStackTrace();
				 return -1;
			 } finally {
				 try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	             try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}     
			 }
			
			return -1;
		}
		/*
		 * 同步股票现用名字信息
		 */
		public Integer refreshCurrentUsedStorkNameOfShangHai()
		{
			File file = new File(sysconfig.getTDXShangHaiZhiShuNameFile() );
			
			if(!file.exists() ) {
				 logger.debug("File not exist");
				 return -1;
			 }
			
			 int addednumber =0;
			 BufferedInputStream dis = null;
			 FileInputStream in = null;
			 try {
				    in = new FileInputStream(file);  
				    dis = new BufferedInputStream(in);
	               
	               int fileheadbytenumber = 50;
	               byte[] itemBuf = new byte[fileheadbytenumber];
	               dis.read(itemBuf, 0, fileheadbytenumber); 
	               String fileHead =new String(itemBuf,0,fileheadbytenumber);  
//	               logger.debug(fileHead);
	               
	               int sigbk = 18*16+12+14;
	               byte[] itemBuf2 = new byte[sigbk];
	               int i=0;
//	               Files.append("开始导入通达信股票指数板块对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	               while (dis.read(itemBuf2, 0, sigbk) != -1) {
	            	   String zhishuline =new String(itemBuf2,0,sigbk);
//	            	   logger.debug(zhishuline);
	            	   String zhishucode = zhishuline.trim().substring(0, 6);
//	            	   logger.debug(zhishucode);
	            	   
//	            	   if(  !(zhishucode.startsWith("60") ) )
	            	   if( ! sysconfig.isShangHaiStock(zhishucode) )
	            		   continue;
	            	   
	            	   List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(zhishuline.substring(6, zhishuline.length()));
	            	   String zhishuname = null;
	            	   try {
		            	    zhishuname = tmplinelist.get(0).trim().substring(0, 6).trim();
	            	   } catch (java.lang.StringIndexOutOfBoundsException ex) {
	            		   List<String> tmplinepartnamelist = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(1).trim());
	            		   zhishuname = tmplinelist.get(0).trim() + tmplinepartnamelist.get(0).trim();
	            	   }
	            	   if(zhishuname.trim().length() <4)
	            		   logger.debug(zhishuname);
	            	   if(zhishuname.trim().length() == 2)
	            		   logger.debug(zhishuname);
	            	   
	            	   String sqlinsertstat = "Update a股 set 股票名称 = '" + zhishuname.trim() + "'\r\n" + 
	            			   				   ", 所属交易所 = 'SH' "	+ "\r\n" +
	            	   						   " WHERE 股票代码 = '" + zhishucode.trim() + "' "
		   						;
//	            	   logger.debug(sqlinsertstat);
		   			   int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//		               Files.append("加入：" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	               }
	               
			 } catch (Exception e) {
				 e.printStackTrace();
				 return -1;
			 } finally {
				 try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	             try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}     
			 }
			
			return 1;
		}
		/*
		 * 从通达信中导入股票曾用名的信息
		 */
		public File refreshEverUsedStorkName()
		{
			File tmpreportfolder = Files.createTempDir();
			File tmprecordfile = new File(tmpreportfolder + "同步通达信曾用名报告.txt");
		
			File file = new File(sysconfig.getTDXStockEverUsedNameFile() );
			if(!file.exists() ) {
				 logger.debug("File not exist");
				 return null;
			 }
			
			 int addednumber =0;
			 HashMap<String,String> cymmap = Maps.newHashMap();
			 FileInputStream in = null ;  
			 BufferedInputStream dis = null;
			 try {
				 Files.append("开始导入通达信股票曾用名信息:" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
				   in = new FileInputStream(file);  
				   dis = new BufferedInputStream(in);
				   
				   	  int eachcymbytenumber = 64;
		              byte[] itemBuf2 = new byte[eachcymbytenumber];
		              int i=0;
		              
		              while (dis.read(itemBuf2, 0, eachcymbytenumber) != -1) {
		            	String cymline = (new String(itemBuf2,0,63)).trim();
			           	List<String> tmplinelist = Splitter.fixedLength(6).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(cymline);
			           	logger.debug(tmplinelist);
			           	
			           	String stockcode = tmplinelist.get(0).trim();
			           	String stockcym = tmplinelist.get(1).replaceAll("\\s*", "");
			           	
			           	if(!cymmap.keySet().contains(tmplinelist.get(0).trim()) )
			           		cymmap.put(stockcode,stockcym);
			           	else {
			           		String cymstr = cymmap.get(stockcode);
			           		cymstr = cymstr +  "|" + stockcym;  
			           		cymmap.put(stockcode,cymstr);
			           	}
		              }
				   
			 } catch (Exception e) {
				 e.printStackTrace();
				 return null;
			 } finally {
				 if(in != null)
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				 if(dis != null)
					try {
						dis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			 }
			
			 for(String stockcode:cymmap.keySet()) {
				 String cymstr = cymmap.get(stockcode);
				 
				 updateStockEverUsedName (stockcode,cymstr);
				 
//				 String sqlupdatestat= "UPDATE A股  SET "
//							+ " 曾用名=" + "'" + cymstr + "'"
//							+ " WHERE 股票代码=" + stockcode
//							;
//				 logger.debug(sqlupdatestat);
//				 connectdb.sqlUpdateStatExecute(sqlupdatestat);
			 }
			 
			return tmprecordfile;
		}
		
		public void updateStockEverUsedName (String stockcode, String everusedname)
		{
			 String sqlupdatestat= "UPDATE A股  SET "
						+ " 曾用名=" + "'" + everusedname + "'"
						+ " WHERE 股票代码=" + stockcode
						;
			 logger.debug(sqlupdatestat);
			 try {
				connectdb.sqlUpdateStatExecute(sqlupdatestat);
			} catch (MysqlDataTruncation e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		public Object[][] getTodaysOperations() 
		{
			 String searchdate = CommonUtility.formatDateYYYY_MM_DD_HHMMSS( LocalDateTime.now() );
			 searchdate = searchdate.replaceAll("\\d{2}:\\d{2}:\\d{2}", "");
			 String sqlquerystat2=" SELECT czjl.股票代码, " 
						+ " IF( czjl.买卖金额=0.0,'送转股',IF(czjl.挂单 = true,IF(czjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(czjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
						+ " czjl.原因描述,"
						+ " czjl.ID,"
						+ " czjl.买卖账号,"
						+ "'操作记录买卖'" 
						+ " FROM 操作记录买卖 czjl "
						+ "	WHERE czjl.日期>=" + "'" + searchdate + "'"
						
						+ " UNION ALL " 
						;
			logger.debug(sqlquerystat2);
			String sqlquerystat3=" SELECT rqczjl.股票代码, "
								+ "IF( rqczjl.买卖金额=0.0,'送转股',IF(rqczjl.挂单 = true,IF(rqczjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(rqczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
								+ "rqczjl.原因描述,"
								+ "rqczjl.ID,"
								+ "rqczjl.买卖账号,"
								+ "'操作记录融券买卖'"
								+ " FROM 操作记录融券买卖 rqczjl"
								+ "	WHERE rqczjl.日期>=" + "'" + searchdate + "'"
								
								+ " UNION  ALL "
								;
			logger.debug(sqlquerystat3);
			String sqlquerystat4=" SELECT rzczjl.股票代码,"
								+ "IF( rzczjl.买卖金额=0.0,'送转股',IF(rzczjl.挂单 = true,IF(rzczjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(rzczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
								+ "rzczjl.原因描述,"
								+ "rzczjl.ID,"
								+ "rzczjl.买卖账号,"
								+ "'操作记录融资买卖'"
								+ " FROM 操作记录融资买卖 rzczjl"
								+ " WHERE rzczjl.日期>=" + "'" + searchdate + "'"
								
								
								;
			logger.debug(sqlquerystat4);
			String sqlquerystat =  sqlquerystat2 + sqlquerystat3 + sqlquerystat4 ;
	     
			 CachedRowSetImpl rs = null;
			 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			 
			 Object[][] data = null;  
			    try  {     
			        rs.last();  
			        int rows = rs.getRow();  
			        data = new Object[rows][];    
			        int columnCount = 4;//列数  
			        rs.first();  
			        int k = 0;  
			        //while(rs.next())
			        for(int j=0;j<rows;j++) {   //{ "日期", "操作", "说明","ID","操作账户","信息表" };
			            Object[] row = new Object[columnCount];  
			            row[0] = rs.getString(1);
			            row[1] = rs.getString(2);
			            row[2] = rs.getString(3);
			            row[3] = rs.getString(5);

			            data[k] = row;  
			            k++; 
			            rs.next();
			        } 
			       
			    } catch(java.lang.NullPointerException e) { 
			    	e.printStackTrace();
			    }catch(Exception e) {
			    	e.printStackTrace();
			    } finally {
			    	if(rs != null)
						try {
							rs.close();
							rs = null;
						} catch (SQLException e) {
							e.printStackTrace();
						}
			    }
			    
			    return data;
		}
		/*
		 * 
		 */
		public File refreshStockJiBenMianInfoFromTdxFoxProFile ()
		{
			File tmpreportfolder = Files.createTempDir();
			File tmprecordfile = new File(tmpreportfolder + "同步通达信个股基本面报告.tmp");
			
			Charset stringCharset = Charset.forName("Cp866");
			String dbffile = sysconfig.getTdxFoxProFileSource();
//	        InputStream dbf = getClass().getClassLoader().getResourceAsStream(dbffile);
	        InputStream dbf = null;
			try {
				dbf = new FileInputStream(new File(dbffile) );
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

	        DbfRecord rec;
	        String sqlinsetstat = null;
	        try (DbfReader reader = new DbfReader(dbf)) {
	            DbfMetadata meta = reader.getMetadata();

	            logger.debug("Read DBF Metadata: " + meta);
	            int recCounter = 0;
	            while ( (rec = reader.read() ) != null) {
	                rec.setStringCharset(stringCharset);
//	                logger.debug("Record is DELETED: " + rec.isDeleted());
//	                logger.debug(rec.getRecordNumber());
//	                logger.debug(rec.toMap());
//	        		{SC=0, GPDM=000001, GXRQ=20170811, ZGB=1717041.12, GJG=180199.00, FQRFRG=12292000.00, FRG=54769000.00, BG=0.00, HG=0.00, LTAG=1691799.00, ZGG=0.68, 
//	        		ZPG=null, ZZC=3092142080.0, LDZC=0.0, GDZC=7961000.0, WXZC=4636000.0, CQTZ=379179.0, LDFZ=0.0, CQFZ=0.0, ZBGJJ=56465000.0, JZC=211454000.0, 
//	        		ZYSY=54073000.0, ZYLY=0.0, QTLY=0.0, YYLY=40184000.0, TZSY=739000.0, BTSY=-128180000.0, YYWSZ=-107760000.0, SNSYTZ=0.0, LYZE=16432000.0, 
//	        		SHLY=12554000.0, JLY=12554000.0, WFPLY=73110000.0, TZMGJZ=11.150, DY=18, HY=1, ZBNB=6, SSDATE=19910403, MODIDATE=null, GDRS=null}

	                Map<String, Object> jbminfomap = rec.toMap();
	                String stockcode = jbminfomap.get("GPDM").toString();
	                String datavalueindb = jbminfomap.get("GXRQ").toString() ;
	                if( ! (sysconfig.isShangHaiStock(stockcode) || sysconfig.isShenZhengStock(stockcode) )
	                		|| stockcode.equals("000003") 
	                		|| datavalueindb.equals("0") )
	                	continue;

	                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	                LocalDate lastupdatedate = LocalDate.parse(datavalueindb,formatter );
	                double zgb = Double.parseDouble(jbminfomap.get("ZGB").toString());
	                double gjg  = Double.parseDouble(jbminfomap.get("GJG").toString());
	                double fqrfrg  = Double.parseDouble(jbminfomap.get("FQRFRG").toString());
	                double frg  = Double.parseDouble(jbminfomap.get("FRG").toString());
	                double bg  = Double.parseDouble(jbminfomap.get("BG").toString());
	                double hg  = Double.parseDouble(jbminfomap.get("HG").toString());
	                double ltag  = Double.parseDouble(jbminfomap.get("LTAG").toString());
	                double zgg  = Double.parseDouble(jbminfomap.get("ZGG").toString());
	                
	                String zpg = "' '";
	                if(jbminfomap.get("ZPG") != null)
	                	zpg  = jbminfomap.get("ZPG").toString(); 
	                double zzc  = Double.parseDouble(jbminfomap.get("ZZC").toString());
	                double ldzc  = Double.parseDouble(jbminfomap.get("LDZC").toString());
	                double gdzc  = Double.parseDouble(jbminfomap.get("GDZC").toString());
	                double wxzc  = Double.parseDouble(jbminfomap.get("WXZC").toString());
	                float cqtz  = Float.parseFloat(jbminfomap.get("CQTZ").toString());
	                double ldfz  = Double.parseDouble(jbminfomap.get("LDFZ").toString());
	                double cqfz  = Double.parseDouble(jbminfomap.get("CQFZ").toString());
	                double zbgjj  = Double.parseDouble(jbminfomap.get("ZBGJJ").toString());
	                double jzc  = Double.parseDouble(jbminfomap.get("JZC").toString());
	                
	                double zysy  = Double.parseDouble(jbminfomap.get("ZYSY").toString());
	                double zyly  = Double.parseDouble(jbminfomap.get("ZYLY").toString());
	                double qtly  = Double.parseDouble(jbminfomap.get("QTLY").toString());
	                double yyly  = Double.parseDouble(jbminfomap.get("YYLY").toString());
	                double tzsy  = Double.parseDouble(jbminfomap.get("TZSY").toString());
	                double btsy  = Double.parseDouble(jbminfomap.get("BTSY").toString());
	                double yywsz  = Double.parseDouble(jbminfomap.get("YYWSZ").toString());
	                double snsytz  = Double.parseDouble(jbminfomap.get("SNSYTZ").toString()); 
	                double lyze  = Double.parseDouble(jbminfomap.get("LYZE").toString());
	                
	                double shly  = Double.parseDouble(jbminfomap.get("SHLY").toString());
	                double jly  = Double.parseDouble(jbminfomap.get("JLY").toString());
	                double wfply  = Double.parseDouble(jbminfomap.get("WFPLY").toString());
	                double tzmgjz  = Double.parseDouble(jbminfomap.get("TZMGJZ").toString());
	                int dy = Integer.parseInt(jbminfomap.get("DY").toString() );
	                int hy = Integer.parseInt(jbminfomap.get("HY").toString() );
	                int zbnb = Integer.parseInt(jbminfomap.get("ZBNB").toString() );
	                LocalDate shangshidate = null;
	                try {
	                	 shangshidate = LocalDate.parse(jbminfomap.get("SSDATE").toString(),formatter );
	                } catch (java.time.format.DateTimeParseException e) {
	                	shangshidate = LocalDate.parse("1992-01-01");
	                }
	                
	                 sqlinsetstat = "INSERT INTO 股票通达信基本面信息对应表 (股票代码GPDM, 总股本ZGB,更新日期GXRQ, GJG,FQRFRG,法人股FRG,B股BG,H股HG,流通A股LTAG,每股收益ZGG,"
	                														+  "ZPG,总资产ZZC,流动资产LDZC,固定资产GDZC,无形资产WXZC,股东人数CQTZ,流动负债LDFZ,少数股权CQFZ,"
	                														+ "公积金ZBGJJ,净资产JZC,主营收益ZYSY,营业成本ZYLY,应收帐款QTLY,营业利润YYLY,投资收益TZSY,"
	                														+ "经营现金流BTSY,总现金流YYWSZ,存货SNSYTZ,利润总额LYZE,税后利润SHLY,净利润JLY,"
	                														+ "未分配利益WFPLY,净资产TZMGJZ,地域DY,行业HY,ZBNB,上市日期SSDATE) "
	                		              		+ "VALUES("
	                		              		+ "'" + stockcode + "'" +","
	                		              		+ zgb +","
	                		              		+ "'" + lastupdatedate +"',"
	                		              		+ gjg +","
	                		              		+ fqrfrg +","
	                		              		+ frg +","
			                					+ bg +","
			                					+ hg +","
			                					 + ltag +","
			                					 + zgg + ","
			                					+ zpg +","
			                					+ zzc +","
			                					+ ldzc +","
			                					+ gdzc +","
			                					+ wxzc +","
			                					+ cqtz +","
			                					+ ldfz +","
			                					+ cqfz +","
			                					+ zbgjj +","
			                					+ jzc +","
			                					+ zysy +","
			                					+ zyly +","
			                					+ qtly +","
			                					+ yyly +","
			                					+ tzsy +","
			                					+ btsy +","
			                					+ yywsz +","
			                					+ snsytz +","
			                					+ lyze +","
			                					+ shly +","
			                					+ jly +","
			                					+ wfply +","
			                					+ tzmgjz +","
			                					+ dy +","
			                					+ hy +","
			                					+ zbnb +","
			                					+ "'" + shangshidate +"'" 
	                		              		+ ")"
	                		              		+ " ON DUPLICATE KEY UPDATE "
	                        					+ " 总股本ZGB=" + zgb +","
	                        					+ " 更新日期GXRQ= '" + lastupdatedate +"' ,"
	                        					+ " GJG=" + gjg +","
	                        					+ " FQRFRG=" + fqrfrg +","
	                        					+ " 法人股FRG=" + frg +","
	                        					+ " B股BG=" + bg +","
	                        					+ " H股HG=" + hg +","
	                        					+ " 流通A股LTAG=" + ltag +","
	                        					+ " 每股收益ZGG=" + zgg + ","
	                        					+ " ZPG=" + zpg +","
	                        					+ " 总资产ZZC=" + zzc +","
	                        					+ " 流动资产LDZC=" + ldzc +","
	                        					+ " 固定资产GDZC=" + gdzc +","
	                        					+ " 无形资产WXZC=" + wxzc +","
	                        					+ " 股东人数CQTZ=" + cqtz +","
	                        					+ " 流动负债LDFZ=" + ldfz +","
	                        					+ " 少数股权CQFZ=" + cqfz +","
	                        					+ " 公积金ZBGJJ=" + zbgjj +","
	                        					+ " 净资产JZC=" + jzc +","
	                        					+ " 主营收益ZYSY=" + zysy +","
	                        					+ " 营业成本ZYLY=" + zyly +","
	                        					+ " 应收帐款QTLY=" + qtly +","
	                        					+ " 营业利润YYLY=" + yyly +","
	                        					+ " 投资收益TZSY=" + tzsy +","
	                        					+ " 经营现金流BTSY=" + btsy +","
	                        					+ " 总现金流YYWSZ=" + yywsz +","
	                        					+ " 存货SNSYTZ=" + snsytz +","
	                        					+ " 利润总额LYZE=" + lyze +","
	                        					+ " 税后利润SHLY=" + shly +","
	                        					+ " 净利润JLY=" + jly +","
	                        					+ " 未分配利益WFPLY=" + wfply +","
	                        					+ " 净资产TZMGJZ=" + tzmgjz +","
	                        					+ " 地域DY=" + dy +","
	                        					+ " 行业HY=" + hy +","
	                        					+ " ZBNB=" + zbnb +" ,"
	                        					+ " 上市日期SSDATE= '" + shangshidate + "'"
	                        					
	                        					; 
	              logger.debug(sqlinsetstat);
	              connectdb.sqlUpdateStatExecute(sqlinsetstat);
	                recCounter++;

	                //如果在表：A股中也没有这个股票代码，则在A股中也增加该股票
	                sqlinsetstat = "INSERT INTO A股 (股票代码)"
	                		+ "   SELECT * FROM (SELECT '" + stockcode + "') AS tmp"
	                		+ "   WHERE NOT EXISTS ("
	                		+ "   SELECT 股票代码 FROM A股 WHERE 股票代码 = '" + stockcode + "'"
	                		+ ") LIMIT 1"
	                		;
	                int insertresult = connectdb.sqlInsertStatExecute(sqlinsetstat);
	                if(insertresult >0) {
	                	String jys;
//	                	if(stockcode.startsWith("60"))
	                	if( sysconfig.isShangHaiStock(stockcode) )
	                		jys = "SH";
	                	else
	                		jys = "SZ";
	                	String sqlinsertstat = "Update a股 SET 所属交易所 = '" + jys + "'"	+ "\r\n" +
 	   						   " WHERE 股票代码 = '" + stockcode.trim() + "' "
 	   						   ;
	                	int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
	                }
	            }
	        } catch (IOException e1) {
	        	logger.debug("出错SQL是:" + sqlinsetstat);
				e1.printStackTrace();
			} catch (ParseException e2) {
				logger.debug("出错SQL是:" + sqlinsetstat);
				
				e2.printStackTrace();
			} catch (Exception e3) {
				logger.debug("出错SQL是:" + sqlinsetstat);
				e3.printStackTrace();
			}
	        
	        return tmprecordfile;
			
		}
		/*
		 * 设置板块的属性: 是否导入数据，是否出现在板块分析界面中，是否导出到Gephi
		 */
		public void updateBanKuaiOperationsSettings(BkChanYeLianTreeNode node, boolean importdailydata, boolean exporttogephi, 
				boolean showinbkfx,boolean showincyltree, boolean exporttowkfile, boolean importbkgg, Color bkcolor, boolean corezhishu)
		{
			String colorcode = String.format("#%02x%02x%02x", bkcolor.getRed(), bkcolor.getGreen(), bkcolor.getBlue() );
			
			String sqlupdatestat = "UPDATE 通达信板块列表 SET " +
								" 导入交易数据=" + importdailydata  + "," +
								" 导出Gephi=" + exporttogephi +  ","  +
								" 板块分析=" + showinbkfx +  ","  +
								" 产业链树=" + showincyltree + ","  +
								" 周分析文件 = " + exporttowkfile + ","  +
								" DefaultCOLOUR = '" + colorcode + "',"  +
								" 核心指数 = " + corezhishu + ","  +
								" 导入板块个股 = " + importbkgg +
								" WHERE 板块ID='" + node.getMyOwnCode() + "'"
								;
	   		try {	int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
			} catch (MysqlDataTruncation e) {e.printStackTrace();
			} catch (SQLException e) {e.printStackTrace();}
	   		
		}
		/*
		 * 导入网易股票行情接口的数据，
		 */
		public File importNetEaseStockData(String jiaoyisuo) 
		{
			File tmpreportfolder = Files.createTempDir();
			File tmprecordfile = new File(tmpreportfolder + "导入网易股票信息.tmp");
				
			try {
				FileUtils.cleanDirectory(new File(sysconfig.getNetEaseDownloadedFilePath () ) );
			} catch (IOException e1) {e1.printStackTrace();}
			
			Collection<BkChanYeLianTreeNode> allgegucode = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXGG,jiaoyisuo);		

			String optTable = null;
			if(jiaoyisuo.equalsIgnoreCase("SZ")  )
				optTable = "通达信深交所股票每日交易信息";
			else if(jiaoyisuo.equalsIgnoreCase("SH") )
				optTable = "通达信上交所股票每日交易信息";
			
			String sqlquerystat = "SELECT * FROM \r\n" + 
					"(SELECT 股票代码  FROM A股\r\n" + 
					"WHERE 所属交易所 = '" + jiaoyisuo + "' ) agu\r\n" + 
					"\r\n" + 
					"LEFT JOIN \r\n" + 
					"( SELECT 代码, MIN(交易日期) minjytime , MAX(交易日期) maxjytime FROM " + optTable + "\r\n" + 
					" WHERE  涨跌幅 IS NOT NULL " +
					" GROUP BY 代码) shjyt\r\n" + 
					" ON agu.股票代码 =  shjyt.`代码`\r\n"
					;
			CachedRowSetImpl  rsdm = connectdb.sqlQueryStatExecute(sqlquerystat);
			try { 	
		    	while(rsdm.next()) {
			    		 String ggcode = rsdm.getString("股票代码"); //mOST_RECENT_TIME
			    		 Stock stock = (Stock) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(ggcode, BkChanYeLianTreeNode.TDXGG);
			    		 if(stock == null) 
			    			 continue;
			    		 try { LocalDate mintime = rsdm.getDate("minjytime").toLocalDate();
			    		 		stock.getShuJuJiLuInfo().setNeteasejlmindate(mintime);
			    		 } catch (java.lang.NullPointerException ex) {}
			    		 try { LocalDate maxtime = rsdm.getDate("maxjytime").toLocalDate();
			    		 		stock.getShuJuJiLuInfo().setNeteasejlmaxdate(maxtime);
			    		 } catch (java.lang.NullPointerException ex) {}
			    	}
			    } catch(java.lang.NullPointerException e) {	e.printStackTrace();
			    } catch (SQLException e) {	e.printStackTrace();
			    } catch(Exception e){	e.printStackTrace();
			    } finally {
			    	if(rsdm != null)	try {rsdm.close();rsdm = null;	} catch (SQLException e) {	e.printStackTrace();}
			    }

			//下载需要的时间段的数据
			for(BkChanYeLianTreeNode tmpnode : allgegucode) {
				Stock stock = (Stock)tmpnode;
				boolean neteasthasdata = false;
				String stockcode = stock.getMyOwnCode();
				String formatedstockcode; 
				if(stock.getNodeJiBenMian().getSuoShuJiaoYiSuo().equalsIgnoreCase("sh"))  
					formatedstockcode = "0" + stockcode;
				else 
					formatedstockcode = "1" + stockcode;
				
				LocalDate ldlastestdbrecordsdate = stock.getShuJuJiLuInfo().getNeteasejlmaxdate();
			   if(ldlastestdbrecordsdate == null)
				   ldlastestdbrecordsdate =  LocalDate.parse("2013-03-04"); //当前数据的起点
			   else
				   ldlastestdbrecordsdate = ldlastestdbrecordsdate.plusDays(1); //从最新数据的后一天开始导入数据
			   
			   
//				 if( ( ldlastestdbrecordsdate.getDayOfWeek() == DayOfWeek.SUNDAY || ldlastestdbrecordsdate.getDayOfWeek() == DayOfWeek.SATURDAY  )
//						 && ( LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY || LocalDate.now().getDayOfWeek() == DayOfWeek.SATURDAY   )  ) //开始导入数据日期一直到今天是周末，肯定不需要导入
//					 continue;
				    
				    //获取网易文件
				    URL URLink; //https://blog.csdn.net/NarutoInspire/article/details/72716724
				    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
				    String startdate = ldlastestdbrecordsdate.format(formatters);
				    
				    LocalTime tdytime = LocalTime.now(); //如果是在交易时间导入数据的话，当天的数据还没有，所以要判断一下
				    LocalDate endldate = LocalDate.now(); 
					if( ( tdytime.compareTo(LocalTime.of(9, 0, 0)) >0 && tdytime.compareTo(LocalTime.of(18, 0, 0)) <0) ) 
						 endldate = endldate.minusDays(1);
				    
					if(ldlastestdbrecordsdate.compareTo(endldate) >0)  //说明只有今天的数据没有导入，而今天还没有数据，所以跳出
						continue;
					
				    String enddate = endldate.format(formatters);
				    String urlstr = "http://quotes.money.163.com/service/chddata.html?code=" + formatedstockcode + "&start=" + startdate + 
				    				"&end=" + enddate + "&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";
				    String savedfilename = sysconfig.getNetEaseDownloadedFilePath () + stockcode + ".csv";
				    
				    File savedfile = null;
					try {
						URLink = new URL( urlstr); //"http://quotes.money.163.com/service/chddata.html?code=0601857&start=20071105&end=20150618&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP"
						
						savedfile = new File (savedfilename);
						if(savedfile.exists())
							savedfile.delete();
						
						FileUtils.copyURLToFile(URLink, savedfile,10000,10000); //http://commons.apache.org/proper/commons-io/javadocs/api-2.4/org/apache/commons/io/FileUtils.html#copyURLToFile(java.net.URL,%20java.io.File)
					} catch (java.net.SocketTimeoutException e)  {logger.info("获取" + stockcode + "网易数据超时！");
					} catch ( IOException e) { e.printStackTrace();
					} finally {URLink = null;
					}
					//导入数据到数据库
					if(!savedfile.exists()) {
						System.out.println(stockcode + "：未能从网易获得"+ stockcode + "的数据文件，请检查！");
						try {
							Files.append(stockcode + "未能从网易得到"+ stockcode + "的数据文件，请检查！" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
						} catch (IOException e) {e.printStackTrace();
						}
						neteasthasdata = false;
					} else {
						try (
								FileReader filereader = new FileReader(savedfile);
								CSVReader csvReader = new CSVReader(filereader);
					    ){ 
							int titlesign = -1;//标记网易CSV文件的第一行
							Integer hsl_index = null; Integer zsz_index = null; Integer zdf_index = null; Integer ltsz_index = null; Integer spj_index = null;
							List<String[]> records = csvReader.readAll();
							for (String[] nextRecord : records) {
					            	if(-1 == titlesign) {
					            		for(int m=0;m<nextRecord.length;m++) {
					            			if(nextRecord[m].equalsIgnoreCase("换手率")) 
					            				hsl_index = m;
					            			else if(nextRecord[m].equalsIgnoreCase("总市值")) 
					            				zsz_index = m;
					            			else if(nextRecord[m].equalsIgnoreCase("涨跌幅")) 
					            				zdf_index = m;
					            			else if(nextRecord[m].equalsIgnoreCase("流通市值")) 
					            				ltsz_index = m;
					            			else if(nextRecord[m].equalsIgnoreCase("收盘价")) 
					            				spj_index = m;
					            		}
//						                String huanshoulv = nextRecord[10];
//						                String zongshizhi = nextRecord[13];
//						                
//						                if(!huanshoulv.trim().equals("换手率") || !zongshizhi.trim().equals("总市值")) {
//						                	System.out.println("网易数据文件格式可能有改变，请检查！");
//						                	Files.append("网易数据文件格式可能有改变，请检查！" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//						                	neteasthasdata = false;
//						                	break;
//						                } else 
						                	titlesign ++;
					            	} else { //第二行
					            		String shoupanjia = nextRecord[spj_index];
					            		if(Double.parseDouble(shoupanjia) == 0) //收盘价不可能为0，0说明停牌，在交易数据库表中，停牌是没有记录的，跳过 
					            			continue;
					            		
					            		LocalDate lactiondate = null;
					            		String actiondate =  nextRecord[0];
					            		if(Pattern.matches("\\d*/\\d*/\\d*",actiondate) ) {
					            			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
					            			lactiondate = LocalDate.parse(actiondate, formatter);
					            		} else if(Pattern.matches("\\d*-\\d*-\\d*",actiondate) ) {
					            			lactiondate = LocalDate.parse(actiondate);
					            		} else {
					            			System.out.println("严重错误, 网易数据文件日期格式改变！");
					            			neteasthasdata = false;
						                	break;
					            		}
					            		Double zhangdiefu = null;Double huanshoulv = null;Double zongshizhi = null;Double liutongshizhi = null;
					            		if(zdf_index!= null) {
					            			String zhangdiefu_str = nextRecord[zdf_index]; if(!Strings.isNullOrEmpty(zhangdiefu_str)) zhangdiefu = Double.parseDouble(zhangdiefu_str);
					            		}
					            		if(hsl_index!= null) {
					            			String huanshoulv_str = nextRecord[hsl_index]; if(!Strings.isNullOrEmpty(huanshoulv_str)) huanshoulv = Double.parseDouble(huanshoulv_str);
					            		}
					            		if(zsz_index!= null) {
					            			String zongshizhi_str = nextRecord[zsz_index]; if(!Strings.isNullOrEmpty(zongshizhi_str)) zongshizhi = Double.parseDouble(zongshizhi_str);
					            		}
					            		if(ltsz_index!= null) {
					            			String liutongshizhi_str = nextRecord[ltsz_index]; if(!Strings.isNullOrEmpty(liutongshizhi_str)) liutongshizhi = Double.parseDouble(liutongshizhi_str);
					            		}
						                
//						                String sqlupdateorinsert = "INSERT INTO " + optTable + "(涨跌幅,换手率,流通市值,总市值) VALUES("
//						                							+ zhangdiefu + "," 
//						                							+ huanshoulv + ","
//						                							+ liutongshizhi + ","
//						                							+ zongshizhi
//						                							+ ") ON DUPLICATE KEY UPDATE" 
//						                							+ "交易日期 = '" + lactiondate + "'," 
//						                							+ "代码= '" + stockcode + "'"
//						                							;	
						                String sqlupdate = "Update " + optTable + " SET " +
						                					" 涨跌幅=" + zhangdiefu + 
//						                					" ,换手率=" + huanshoulv + 
//						                					" ,流通市值= " + liutongshizhi + 
//						                					" ,总市值= " + zongshizhi +
						                					" WHERE 交易日期 = '" + lactiondate + "'" + 
						                					" AND 代码= '" + stockcode + "'"
						                					;
									    try {
											int result = connectdb.sqlUpdateStatExecute(sqlupdate);
										} catch (MysqlDataTruncation e) {e.printStackTrace();
										} catch (SQLException e) {e.printStackTrace();}
									    
									    Files.append(stockcode + "导入网易数据成功！" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
									    
									    neteasthasdata = true;
					            	}
					            }
					            if(!neteasthasdata) 
					            	Files.append(stockcode + "网易文件没有数据，导入0个记录！" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());

					            records = null;
					        } catch (IOException e) {e.printStackTrace();} 
					}
			}
			return tmprecordfile;
		}
		/*
		 * 从雪球获得数据，目前不完善
		 */
		private void importXueQiuStockData(String stockcode,LocalDate ldlastestdbrecordsdate)
		{
//			Authenticator.setDefault (new Authenticator() {
//			    protected PasswordAuthentication getPasswordAuthentication() {
//			        return new PasswordAuthentication ("justjustjustjust@gmail.com", "sure2001".toCharArray());
//			    }
//			});
			

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet("https://xueqiu.com/stock/forchartk/stocklist.json?symbol=SH600756&period=1day&type=before&begin=1478620800000&end=1510126200000&_=1510126200000");
			httpGet.addHeader(BasicScheme.authenticate(
			 new UsernamePasswordCredentials("justjustjustjust@gmail.com", "sure2001"),
			 "GBK", false));

			HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpEntity responseEntity = httpResponse.getEntity();
			try {
				InputStreamReader isr = new InputStreamReader(responseEntity.getContent() );
				int numCharsRead;
				char[] charArray = new char[1024];
				StringBuffer sb = new StringBuffer();
				while ((numCharsRead = isr.read(charArray)) > 0) {
					sb.append(charArray, 0, numCharsRead);
				}
				String result = sb.toString();
				if(result.contains("error")) { //网易有时候会得不到数据，就要用雪球的方法得到数据
					logger.info(stockcode + "未能从雪球获取日期为" + ldlastestdbrecordsdate + "数据，将从其他数据源获取数据！");
					importSoHuStockData (stockcode,ldlastestdbrecordsdate);
				} else {
					System.out.println(result);
				}
				
			} catch (UnsupportedOperationException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*
		 * 搜狐数据只有涨跌幅和换手率，没有流通市值和总市值数据
		 */
		private void importSoHuStockData(String stockcode, LocalDate ldlastestdbrecordsdate) 
		{
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
		    String startdate = ldlastestdbrecordsdate.format(formatters);
		    String enddate = LocalDate.now().format(formatters);
		    //              "http://q.stock.sohu.com/hisHq?code=cn_000001&start=20170930&end=20181231&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp");
		    String urlstr = "http://q.stock.sohu.com/hisHq?code=cn_" + stockcode + "&start=" + startdate + "&end=" + enddate + "&stat=1&order=D&period=d&callback=historySearchHandler&rt=json";
//		    String urlstr = "http://cq.ssajax.cn/interact/getTradedata.ashx?pic=qlpic_000001_2_6";
			HttpGet httpGet = new HttpGet(urlstr);

			HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			HttpEntity responseEntity = httpResponse.getEntity();
			try {
				InputStreamReader isr = new InputStreamReader(responseEntity.getContent() );
				int numCharsRead;
				char[] charArray = new char[1024];
				StringBuffer sb = new StringBuffer();
				while ((numCharsRead = isr.read(charArray)) > 0) {
					sb.append(charArray, 0, numCharsRead);
				}
				String result = sb.toString();
				if(result.contains("error")) { 
					logger.info(stockcode + "未能从搜狐获取日期为" + ldlastestdbrecordsdate + "数据，将从其他数据源获取数据！");
				} else {
//					Map jsonJavaRootObject = new Gson().fromJson(result, Map.class);
//					Map<String, Object> map = new Gson().fromJson(result, new TypeToken<Map<String, Object>>(){}.getType());
//					map.forEach((x,y)-> System.out.println("key : " + x + " , value : " + y));
				}
				
			} catch (UnsupportedOperationException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		public void setStockAsYiJingTuiShi(String formatStockCode) 
		{
			// TODO Auto-generated method stub
			
		}
		/*
		 *早期代码有错误，导致板块和个股的对应关系中可能出现重复， 
		 */
		private void checkImportTDXDataDuplicateError(File logfile)
		{
			CachedRowSetImpl rspd = null;
			String tablename[] = {"股票通达信风格板块对应表","股票通达信概念板块对应表","股票通达信行业板块对应表","股票通达信交易所指数对应表"};
			Charset charset = Charset.forName("GBK") ;
			int countaddline =0;
			for(int i=0;i<tablename.length;i++) {
				String sqlquerystat = "SELECT 板块代码, 股票代码, 移除时间, COUNT(*) " +
						" FROM  " + tablename[i] + 
						" where isnull(移除时间)" +
						" GROUP BY  板块代码,股票代码,isnull(移除时间)" +
						" HAVING   COUNT(*) > 1"
						;
				rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
				try {
					while(rspd.next())  {
						String bkcode = rspd.getString("板块代码");
						String stockcode = rspd.getString("股票代码");
					     
						 Integer duplinecount = rspd.getInt("COUNT(*)");
						 if(duplinecount == 2) { //只有2行，那就直接修补
							 CachedRowSetImpl rsdup = null;
							 sqlquerystat = "SELECT MAX(加入时间), MIN(id)\n" + 
										"FROM  股票通达信风格板块对应表\n" + 
										"where 板块代码 = '" + bkcode + "' and 股票代码 = '" + stockcode + "' and isnull(移除时间) "
										;
							 rsdup = connectdb.sqlQueryStatExecute(sqlquerystat);
							 while(rsdup.next()) {
								 java.util.Date maxdupdate = rsdup.getDate("MAX(加入时间)");
								 Integer minid = rsdup.getInt("MIN(id)");
								 String sqlupdatequery = "UPDATE 股票通达信风格板块对应表 SET 移除时间 = '" + maxdupdate + "' \n" + 
														  "where id = '" + minid + "'"
															;
								 int result = connectdb.sqlUpdateStatExecute(sqlupdatequery);
								 
								 String updateresult = tablename[i] + "  " + bkcode + "  的个股  "  + stockcode  + "  存在数据重复，已经更新，把日期" + maxdupdate.toString() + "更新到ID=" + minid.toString() + System.getProperty("line.separator");
								 logger.debug(updateresult);
								 try {
										Files.append(updateresult,logfile, charset);
										countaddline ++;
								 } catch (IOException e) {
										e.printStackTrace();
								 }
								 
							 }
							 rsdup.close();
							 rsdup = null;
						 } else {
							 String stock = "重要：" + tablename[i] + "  " + bkcode + "  的个股  "  + stockcode  + "  存在数据重复,未更新，请手动更新！"+ System.getProperty("line.separator");
							 logger.debug(stock);
							 try {
									Files.append(stock,logfile, charset);
									countaddline ++;
							 } catch (IOException e) {
									e.printStackTrace();
							 }
						 }
						 
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}catch(Exception e){
				    	e.printStackTrace();
				} finally {
				    	if(rspd != null)
						try {
							rspd.close();
							rspd = null;
							
						} catch (SQLException e) {
							e.printStackTrace();
						}
				}
			}
			
		}
		
		/*
		 * check每日导入的数据是否完整,结果输出到reports
		 */
		private File checkTDXDataImportIsCompleted ()
		{
			LocalDate today = LocalDate.now();
			TemporalField fieldISO = WeekFields.of(Locale.CHINA).dayOfWeek();
			LocalDate monday = today.with(fieldISO, 2);
			LocalDate friday = today.with(fieldISO, 6);
			
			String checkfilename = sysconfig.getTdxDataImportCheckResult () + friday.toString() +  "通达信数据导入检查结果.txt";
			File filefmxx = new File( checkfilename );
			try {
					if (filefmxx.exists()) {
						filefmxx.delete();
						filefmxx.createNewFile();
					} else
						filefmxx.createNewFile();
			} catch (Exception e) {
					e.printStackTrace();
					return null;
			}
			
			Charset charset = Charset.forName("GBK") ;
			
			//首先检查几个典型指数是否完整，999999/399001
			CachedRowSetImpl  rssh = null;
			Integer dapanjyrsm =0 ;
			try {
				String sqlquerystat = "SELECT  COUNT(1) AS JIAOYIRISHUMU FROM 通达信板块每日交易信息  \r\n" +
						  "	WHERE  代码 = '999999' \r\n" +
						  "	AND 交易日期 BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"  
							;
				logger.debug(sqlquerystat);
				
			    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
			    while(rssh.next()) {
			    	dapanjyrsm = rssh.getInt("JIAOYIRISHUMU");
			    }
			} catch(java.lang.NullPointerException e) {   	e.printStackTrace();
			} catch (SQLException e) { 	e.printStackTrace();
			} catch(Exception e){   	e.printStackTrace();
			} finally {
		    	if(rssh != null)
				try {	rssh.close();	rssh = null;
				} catch (SQLException e) {	e.printStackTrace();}
			}
			try {
				String checkresult = "上证999999本周交易数据有" + dapanjyrsm + "天"; 
				Files.append(checkresult +  System.getProperty("line.separator"),filefmxx, charset);
			 } catch (IOException e) {e.printStackTrace();	 }

			try {
				String sqlquerystat = "SELECT  COUNT(1) AS JIAOYIRISHUMU FROM 通达信交易所指数每日交易信息  \r\n" +
						  "	WHERE  代码 = '399001' \r\n" +
						  "	AND 交易日期 BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"  
							;
				logger.debug(sqlquerystat);
				
			    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
			    while(rssh.next()) {
			    	dapanjyrsm = rssh.getInt("JIAOYIRISHUMU");
			    }
			} catch(java.lang.NullPointerException e) { e.printStackTrace();
			} catch (SQLException e) { e.printStackTrace();
			} catch(Exception e){e.printStackTrace();
			} finally {
		    	if(rssh != null)
				try {rssh.close();rssh = null;
				} catch (SQLException e) {e.printStackTrace();}
			}
			try {
				String checkresult = "深证399001本周交易数据有" + dapanjyrsm + "天"; 
				Files.append(checkresult +  System.getProperty("line.separator"),filefmxx, charset);
			 } catch (IOException e) {e.printStackTrace();}
			//检查个股板块记录条数和大盘记录条数是否一致
//			List<BanKuai> allbklist = this.getTDXBanKuaiList("sh"); 
			Collection<BkChanYeLianTreeNode> requiredbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK,"SH");
			for( BkChanYeLianTreeNode tmpnode: requiredbk ) {
				BanKuai bk = (BanKuai) tmpnode;
				if(!bk.getBanKuaiOperationSetting().isImportdailytradingdata()) //这样可以判断哪些板块无需导入每日数据
					continue;
				
				int bkjiaoyirishumu = 0;
				String tmpbkcode = bk.getMyOwnCode();
				try {
					String sqlquerystat = "SELECT  COUNT(1) AS JIAOYIRISHUMU FROM 通达信板块每日交易信息  \r\n" +
							  "	WHERE  代码 = '" + tmpbkcode + "' \r\n" +
							  "	AND 交易日期 BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"  
								;
					logger.debug(sqlquerystat);
					
				    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
				    while(rssh.next()) {
				    	bkjiaoyirishumu = rssh.getInt("JIAOYIRISHUMU");
				    }
				} catch(java.lang.NullPointerException e) { e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();
				}catch(Exception e){e.printStackTrace();
				} finally {
			    	if(rssh != null)
					try {rssh.close();rssh = null;
					} catch (SQLException e) {e.printStackTrace();}
				}
				if(bkjiaoyirishumu != dapanjyrsm) {
					try {
						String checkresult = "注意：板块" +  tmpbkcode + "交易数据条目有" + bkjiaoyirishumu + "天,与大盘交易数据条目数据不符合！"; 
						Files.append(checkresult +  System.getProperty("line.separator"),filefmxx, charset);
					 } catch (IOException e) {e.printStackTrace();}
				}
				
			}
			Collection<BkChanYeLianTreeNode> requiredszbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK,"SZ"); 
			for(BkChanYeLianTreeNode tmpnode:  requiredszbk ) {
				BanKuai bk = (BanKuai) tmpnode;

				if(!bk.getBanKuaiOperationSetting().isImportdailytradingdata()) //这样可以判断哪些板块无需导入每日数据
					continue;
				
				int bkjiaoyirishumu = 0;
				String tmpbkcode = bk.getMyOwnCode();
				try {
					String sqlquerystat = "SELECT  COUNT(1) AS JIAOYIRISHUMU FROM 通达信交易所指数每日交易信息  \r\n" +
							  "	WHERE  代码 = '" + tmpbkcode + "' \r\n" +
							  "	AND 交易日期 BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"  
								;
					logger.debug(sqlquerystat);
					
				    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
				    Integer stockcode =0 ;
				    while(rssh.next()) {
				    	bkjiaoyirishumu = rssh.getInt("JIAOYIRISHUMU");
				    }
				} catch(java.lang.NullPointerException e) { e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();
				}catch(Exception e){e.printStackTrace();
				} finally {
			    	if(rssh != null)
					try {rssh.close();rssh = null;
					} catch (SQLException e) {e.printStackTrace();}
				}
				if(bkjiaoyirishumu != dapanjyrsm) {
					try {
						String checkresult = "注意：板块" +  tmpbkcode + "交易数据条目有" + bkjiaoyirishumu + "天,与大盘交易数据条目数据不符合！"; 
						Files.append(checkresult + System.getProperty("line.separator"),filefmxx, charset);
					 } catch (IOException e) {e.printStackTrace();}
				}
			}
			//检查型个股数据中从网易导入的数据是否完整   
			HashSet<String> stockset = new HashSet<String>(); 
			try { 	
				//找出每日交易的起点终点
				String sqlquerystat = "SELECT \r\n" +
						  "通达信上交所股票每日交易信息.`代码`\r\n" +
						  "FROM \r\n" +
						  "通达信上交所股票每日交易信息 \r\n" +   
						  "LEFT JOIN a股 \r\n" +
						  "ON 通达信上交所股票每日交易信息.`代码` = a股.股票代码  \r\n" +
						  "WHERE \r\n" +
						  "(通达信上交所股票每日交易信息.涨跌幅 is null or      通达信上交所股票每日交易信息.换手率 is null or      通达信上交所股票每日交易信息.总市值 is null or      通达信上交所股票每日交易信息.流通市值 is null) \r\n" +
						  "and a股.已退市 is null \r\n" + 
						  "	AND 交易日期 BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"
						  ;
				logger.debug(sqlquerystat);
				
			    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
			    
			    while(rssh.next()) {
			    	String stockcode = rssh.getString("代码");
			    	stockset.add(stockcode);
			    }
			} catch(java.lang.NullPointerException e) { 
		    	e.printStackTrace();
			} catch (SQLException e) {
		    	e.printStackTrace();
			}catch(Exception e){
		    	e.printStackTrace();
			} finally {
		    	if(rssh != null)
				try {
					rssh.close();
					rssh = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		   }
			try { 	
				String sqlquerystat = "SELECT \r\n" +
						  "通达信深交所股票每日交易信息.`代码`\r\n" +
						  "FROM \r\n" +
						  "通达信深交所股票每日交易信息 \r\n" +   
						  "LEFT JOIN a股 \r\n" +
						  "ON 通达信深交所股票每日交易信息.`代码` = a股.股票代码  \r\n" +
						  "WHERE \r\n" +
						  "(通达信深交所股票每日交易信息.涨跌幅 is null or      通达信深交所股票每日交易信息.换手率 is null or      通达信深交所股票每日交易信息.总市值 is null or      通达信深交所股票每日交易信息.流通市值 is null) \r\n" +
						  "and a股.已退市 is null \r\n" + 
						  "	AND 交易日期 BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"
						  ;
				logger.debug(sqlquerystat);
				
			    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
			    
			    while(rssh.next()) {
			    	String stockcode = rssh.getString("代码");
			    	stockset.add(stockcode);
			    }
			} catch(java.lang.NullPointerException e) { 
		    	e.printStackTrace();
			} catch (SQLException e) {
		    	e.printStackTrace();
			}catch(Exception e){
		    	e.printStackTrace();
			} finally {
		    	if(rssh != null)
				try {
					rssh.close();
					rssh = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		   }
			for(String stockcode : stockset) {
					try {
						String checkresult = "注意：个股" + stockcode + "交易数据网易数据部分有NULL数据"; 
						Files.append(checkresult + System.getProperty("line.separator"),filefmxx, charset);
					 } catch (IOException e) {
							e.printStackTrace();
					 }
			}
			//检查型个股数据中从TDX导入的数据是否完整   
			stockset.clear(); 
			try { 	
				//找出每日交易的起点终点
				String sqlquerystat = "SELECT \r\n" +
						  "通达信上交所股票每日交易信息.`代码`\r\n" +
						  "FROM \r\n" +
						  "通达信上交所股票每日交易信息 \r\n" +   
						  "LEFT JOIN a股 \r\n" +
						  "ON 通达信上交所股票每日交易信息.`代码` = a股.股票代码  \r\n" +
						  "WHERE \r\n" +
						  "(通达信上交所股票每日交易信息.收盘价 is null or 通达信上交所股票每日交易信息.最低价 is null or 通达信上交所股票每日交易信息.最高价 is null or 通达信上交所股票每日交易信息.开盘价 is null) \r\n" +
						  "and a股.已退市 is null \r\n" + 
						  "	AND 交易日期 BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"
						  ;
				logger.debug(sqlquerystat);
				
			    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
			    
			    while(rssh.next()) {
			    	String stockcode = rssh.getString("代码");
			    	stockset.add(stockcode);
			    }
			} catch(java.lang.NullPointerException e) { 
		    	e.printStackTrace();
			} catch (SQLException e) {
		    	e.printStackTrace();
			}catch(Exception e){
		    	e.printStackTrace();
			} finally {
		    	if(rssh != null)
				try {
					rssh.close();
					rssh = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		   }
			try { 	
				String sqlquerystat = "SELECT \r\n" +
						  "通达信深交所股票每日交易信息.`代码`\r\n" +
						  "FROM \r\n" +
						  "通达信深交所股票每日交易信息 \r\n" +   
						  "LEFT JOIN a股 \r\n" +
						  "ON 通达信深交所股票每日交易信息.`代码` = a股.股票代码  \r\n" +
						  "WHERE \r\n" +
						  "(通达信深交所股票每日交易信息.收盘价 is null or 通达信深交所股票每日交易信息.最低价 is null or 通达信深交所股票每日交易信息.最高价 is null or 通达信深交所股票每日交易信息.开盘价 is null) \r\n" +
						  "and a股.已退市 is null \r\n" + 
						  "	AND 交易日期 BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"
						  ;
				logger.debug(sqlquerystat);
				
			    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
			    
			    while(rssh.next()) {
			    	String stockcode = rssh.getString("代码");
			    	stockset.add(stockcode);
			    }
			} catch(java.lang.NullPointerException e) { 
		    	e.printStackTrace();
			} catch (SQLException e) {
		    	e.printStackTrace();
			}catch(Exception e){
		    	e.printStackTrace();
			} finally {
		    	if(rssh != null)
				try {
					rssh.close();
					rssh = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		   }
			for(String stockcode : stockset) {
				try {
					String checkresult = "注意：个股" + stockcode + "交易数据通达信数据部分有NULL数据"; 
					Files.append(checkresult + System.getProperty("line.separator"),filefmxx, charset);
				 } catch (IOException e) {
						e.printStackTrace();
				 }
		}
		
			
//		早期代码有错误，导致板块和个股的对应关系中可能出现重复， 
		checkImportTDXDataDuplicateError(filefmxx);
		
		return 	filefmxx;
	}

	
		/*
		 * 该周大盘共有几个交易日
		 */
		public int getTradingDaysOfTheWeek(LocalDate curselectdate) 
		{
			LocalDate monday = curselectdate.with(DayOfWeek.MONDAY);
			LocalDate friday = curselectdate.with(DayOfWeek.FRIDAY);
			
			boolean hasbkcode = true;
            CachedRowSetImpl rspd = null; 
            int result = 0;
			try {
				   String sqlquerystat = "select count(*) as result from 通达信板块每日交易信息 " + 
							"where 代码 = '999999' " +
							"and 交易日期 >= '" + monday + "'" + 
							"and 交易日期 <= '" + friday + "'"
							;
	
			    	logger.debug(sqlquerystat);
			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			    	
			        while(rspd.next())  {
			        	result = rspd.getInt("result");
			        }
			       
			} catch(java.lang.NullPointerException e){ 
			    	e.printStackTrace();
			} catch (SQLException e) {
			    	e.printStackTrace();
			} catch(Exception e){
			    	e.printStackTrace();
			} finally {
			    	if(rspd != null)
						try {
							rspd.close();
							rspd = null;
						} catch (SQLException e) {
							e.printStackTrace();
						}
			}
			
			return result;
		}
		
		
		/*
		 * 
		 */
		public String getBanKuaiOrStockZdgzInfo(String nodecode, LocalDate date) 
		{
			LocalDate monday = date.with(DayOfWeek.MONDAY);
			LocalDate saterday = date.with(DayOfWeek.SUNDAY);
			
			CachedRowSetImpl rspd = null; 
            String result = "";
            
			try {
				String sqlquerystat = "SELECT * FROM 操作记录重点关注 " +
						  " WHERE 股票代码= '" + nodecode + "'" + 
						  " AND 日期  BETWEEN '" + monday + "' AND '" + saterday + "'" +
						  " AND (加入移出标志 = '加入关注' OR 加入移出标志 = '移除重点' OR 加入移出标志 = '分析结果' "
						  + " OR 加入移出标志 = '重点关注' OR 加入移出标志 = '热点板块 ' OR  加入移出标志 = '加入重点' OR  加入移出标志 = '移出关注')    "
						  ;
	
			    	logger.debug(sqlquerystat);
			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			    	int surviveid = 0 ; int recordsnum = 0;
			        while(rspd.next())  {
			        	result = result + rspd.getString("原因描述");
			        	surviveid = rspd.getInt("id");
			        	
			        	recordsnum ++;
			        	
			        	if(recordsnum >1) { //过去设计遗留，现在要全部改正，每周只有一个记录
				        	int deletedid = rspd.getInt("id");
				        	result = result + rspd.getDate("日期") + rspd.getString("加入移出标志") + rspd.getString("原因描述");
			        		String sqldelete = "DELETE FROM 操作记录重点关注 WHERE ID = " + deletedid ;
			        		connectdb.sqlDeleteStatExecute(sqldelete);
				        }
			        }
			        
			        if(recordsnum >1) { 
			        	String sqlupdate = "UPDATE  操作记录重点关注 SET 原因描述= '" + result + "' WHERE ID = " + surviveid ;
		        		connectdb.sqlUpdateStatExecute(sqlupdate);
			        }
			} catch(java.lang.NullPointerException e){ 
			    	e.printStackTrace();
			} catch (SQLException e) {
			    	e.printStackTrace();
			} catch(Exception e){
			    	e.printStackTrace();
			} finally {
			    	if(rspd != null)
						try {
							rspd.close();
							rspd = null;
						} catch (SQLException e) {
							e.printStackTrace();
						}
			}
			
			if(Strings.isNullOrEmpty(result))
				return null;
			else
				return result.trim();
		}
		/*
		 * 
		 */
		public void updateBanKuaiOrStockZdgzInfo(String nodecode, LocalDate currentdate, String xmlcontents) 
		{
			LocalDate monday = currentdate.with(DayOfWeek.MONDAY);
			LocalDate saterday = currentdate.with(DayOfWeek.SUNDAY);
			
			CachedRowSetImpl rspd = null; 
            int updateid = -1;
            
			try {
				String sqlquerystat = "SELECT * FROM 操作记录重点关注 " +
						  " WHERE 股票代码= '" + nodecode + "'" + 
						  " AND 日期  BETWEEN '" + monday + "' AND '" + saterday + "'" +
						  " AND (加入移出标志 = '加入关注' OR 加入移出标志 = '移除重点' OR 加入移出标志 = '分析结果' "
						  + " OR 加入移出标志 = '重点关注' OR 加入移出标志 = '热点板块 ' OR  加入移出标志 = '加入重点' OR  加入移出标志 = '移出关注')    " 
						  ;
	
			    	logger.debug(sqlquerystat);
			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			        while(rspd.next())  {
			        	updateid =  rspd.getInt("id");
			        }
			        
			        if(-1 == updateid ) { //说明本来数据库里没有相关的XML记录
			        	String sqlinsert = "INSERT INTO  操作记录重点关注(股票代码,日期,加入移出标志,原因描述) values ("
		   						+ "'" + nodecode.trim() + "'" + ","
		   						+ "'" + currentdate + "'"  + ","
		   						+ "'分析结果'"  + ","
		   						+ "'" + xmlcontents + "'" 
		   						+ ")"
		   						;
			        	connectdb.sqlInsertStatExecute(sqlinsert);
			        } else {
			        	String sqlupdate = "UPDATE  操作记录重点关注 SET 原因描述= '" + xmlcontents + "',"
			        						+ "加入移出标志 = '分析结果' "
			        						+ " WHERE ID = " + updateid ;
		        		connectdb.sqlUpdateStatExecute(sqlupdate);
			        }
			        
			} catch(java.lang.NullPointerException e){ 
			    	e.printStackTrace();
			} catch (SQLException e) {
			    	e.printStackTrace();
			} catch(Exception e){
			    	e.printStackTrace();
			} finally {
			    	if(rspd != null)
						try {
							rspd.close();
							rspd = null;
						} catch (SQLException e) {
							e.printStackTrace();
						}
			}
	
			
		}
		/*
		 * 
		 */
		public int setMingRiJiHua(BkChanYeLianTreeNode nodecode,LocalDate actiondate, String mrjhaction,Double mrjhprice,String shuoming) 
		{
			String stockcode = nodecode.getMyOwnCode();		
			String zdgzsign = "明日计划";
			String mrjhshuoming = mrjhaction + "(价格" + mrjhprice + ")(" +  shuoming + ")";

			String sqlinsertstat = "INSERT INTO 操作记录重点关注(股票代码,日期,加入移出标志,原因描述) values ("
					+ "'" +  stockcode.trim() + "'" + "," 
					+ "'" + actiondate + "'" + ","
					+  "'" + zdgzsign + "'" + ","
					+ "'" + mrjhshuoming + "'"  
					+ ")"
					;
			logger.debug(sqlinsertstat);
			int autoIncKeyFromApi = 0;
			try {
				autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
			} catch (MysqlDataTruncation e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return autoIncKeyFromApi;
		}
		/*
		 * TDX划线文件
		 */
		public File refreshTDXDrawingFile() 
		{
//			File file = new File(sysconfig.getDZHStockGuQuanFile() );
			File file = new File("E:/tdxline.eld");
			 if(!file.exists() ) {
				 return null;
			 }
			 
			 BufferedInputStream dis = null;
			 FileInputStream in = null;
			 try {
				    in = new FileInputStream(file);  
				    dis = new BufferedInputStream(in);
				    
				    
				    int fileheadbytenumber = 1;
		            byte[] jiaoyisuocode = new byte[fileheadbytenumber];
		            dis.read(jiaoyisuocode, 0, fileheadbytenumber); 
		              
		            int nodecodebytenumber = 6;
		            byte[] nodecode = new byte[nodecodebytenumber];
		            dis.read(nodecode, 0, nodecodebytenumber);
		            String nodecodestr =new String(nodecode,0,nodecodebytenumber);
		            
		            int zhongjiantempbytenumber = 17;
		            byte[] zhongjian = new byte[zhongjiantempbytenumber];
		            dis.read(zhongjian, 0, zhongjiantempbytenumber);
		            
		            dis.read(jiaoyisuocode, 0, fileheadbytenumber);
		            
		            int zhibiaonumber = 6;
		            byte[] zhibiaobyte = new byte[zhibiaonumber];
		            dis.read(zhibiaobyte, 0, zhibiaonumber);
		            String zhibiaoname =new String(zhibiaobyte,0,zhibiaonumber);
		            
		            int zhongjian2number = 68;
		            byte[] zhongjian2byte = new byte[zhongjian2number];
		            dis.read(zhongjian2byte, 0, zhongjian2number);
		            
				    
			 } catch (Exception e) {
				 e.printStackTrace();
				 return null;
			 } finally {
				 try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	             try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}     
			 }
			return file;
		}
		/*
		 * 
		 */
		public Set<BkChanYeLianTreeNode> sysnRecentGuanZhuGeGu(String zdybkname, LocalDate searchstartdate,LocalDate searchenddate) 
		{
			BanKuaiAndStockTree treeallstocks = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();

			Set<BkChanYeLianTreeNode> namelist = new HashSet<> ();
			CachedRowSetImpl rspd = null;
			try {
				String sqlquerystat = "SELECT * FROM 股票通达信自定义板块对应表 " +
						  " WHERE 自定义板块= '" + zdybkname + "'" + 
						  " AND 加入时间  BETWEEN '" + searchstartdate + "' AND '" + searchenddate + "'"  
						  ;
	
			    	logger.debug(sqlquerystat);
			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			        while(rspd.next())  {
			        	String stockcode=  rspd.getString("股票代码");
			        	
			        	Stock tmpstock = (Stock)treeallstocks.getSpecificNodeByHypyOrCode (stockcode,BkChanYeLianTreeNode.TDXGG);
			        	
			        	LocalDate joindate = rspd.getDate("加入时间").toLocalDate();
			        	LocalDate removedate;
			        	try { removedate = rspd.getDate("移除时间").toLocalDate();
			        	} catch (java.lang.NullPointerException e) { removedate = LocalDate.parse("3000-01-01"); 	}
			        	tmpstock.addNewDuanQiGuanZhuRange (joindate,removedate);
			        	namelist.add(tmpstock);
			        }
			} catch(java.lang.NullPointerException e){	e.printStackTrace();
			} catch (SQLException e) {	e.printStackTrace();
			} catch(Exception e){	e.printStackTrace();
			} finally {
			    	if(rspd != null)
						try {	rspd.close();		rspd = null;} catch (SQLException e) {	e.printStackTrace();}
			}
			
			return namelist;
		}
		
		private List<QueKou> checkQueKouForAGivenPeriod (TDXNodes childnode,LocalDate startdate, LocalDate enddate, List<QueKou> qklist, String period )
		{
			if(  ((Stock)childnode).isVeryVeryNewXinStock(startdate)  )	return null;
			
			if(qklist == null)		qklist = new ArrayList<QueKou> ();
			
			NodeXPeriodData nodexdata = childnode.getNodeXPeroidData(period);
			OHLCSeries nodeohlc = ((TDXNodesXPeriodDataForJFC)nodexdata).getOHLCData();
			List<QueKou> newquekou = new ArrayList<> ();
			for(int j=1;j < nodeohlc.getItemCount();j++) {
				
				OHLCItem kxiandatacurwk = (OHLCItem) nodeohlc.getDataItem(j);
				RegularTimePeriod curperiod = kxiandatacurwk.getPeriod();
				LocalDate curstart = curperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate curend = curperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//				if(curend.equals(LocalDate.parse("2019-12-27"))) {
//					String tempstr = "";
//				}
				
				Double curhigh = kxiandatacurwk.getHighValue();
				Double curlow = kxiandatacurwk.getLowValue();
				Double curclose = kxiandatacurwk.getCloseValue();
				
				if(curstart.isBefore(startdate) || curstart.isAfter(enddate)) { //对于不在要求检查范围内的日线数据，只做检查是否补当前缺口用
					
					Iterator itr = qklist.iterator(); 
					while (itr.hasNext()) 
					{ 
						QueKou tmpqk = (QueKou)itr.next();
						LocalDate tmpqkdate = tmpqk.getQueKouDate();
						if(tmpqkdate.isAfter(curstart))
							continue;
						
						if (!tmpqk.hasQueKouHuiBu() ) {
							String huibuinfo = tmpqk.checkQueKouHuiBu(curstart,curlow, curhigh);
						}
					}
				} else { //在要求查找范围内的，除了查找是否补缺口，还要看是否产生新缺口

					Iterator itr = qklist.iterator(); 
					while (itr.hasNext()) 
					{ 
						QueKou tmpqk = (QueKou)itr.next();
						LocalDate tmpqkdate = tmpqk.getQueKouDate();
						if(tmpqkdate.isAfter(curstart))
							continue;
						
						if (!tmpqk.hasQueKouHuiBu() ) {
							String huibuinfo = tmpqk.checkQueKouHuiBu(curstart,curlow, curhigh);
						}
					} 
					
					//看看有没有产生新的缺口
					OHLCItem kxiandatalastwk = (OHLCItem) nodeohlc.getDataItem(j-1);
					double lasthigh = kxiandatalastwk.getHighValue();
					double lastlow = kxiandatalastwk.getLowValue();
					
					Boolean tiaokongup = false; Boolean tiaokongdown = false;
					if(curlow > lasthigh) {
						Double newqkup = curlow;
						Double newqkdown = lasthigh;
						QueKou newqk = new QueKou (childnode.getMyOwnCode(),curstart,newqkdown,newqkup,true);
						newqk.setShouPanJia(curclose);
						
						qklist.add(0, newqk);
					}
					else 
					if(curhigh < lastlow) {
						Double newqkup = lastlow;
						Double newqkdown = curhigh;
						QueKou newqk = new QueKou (childnode.getMyOwnCode(),curstart,newqkdown,newqkup,false);
						newqk.setShouPanJia(curclose);
						
						qklist.add(0, newqk);
					}
				}

			}
			
			return qklist;
		}
		/*
		 * 
		 */
		public Boolean updateNodeQueKouInfoToDb (List<QueKou> qklist )
		{
			if(qklist == null)
				return null;
			
			for(QueKou tmpqk : qklist) {
				
        		if(!tmpqk.shouldUpdatedToDb())
        			continue;
        		
					String nodecode = tmpqk.getNodeCode ();
					Double qkup = tmpqk.getQueKouUp();  
		        	Double qkdown =  tmpqk.getQueKouDown();
		        	Boolean type = tmpqk.getQueKouLeiXing();
		        	LocalDate qkdate = tmpqk.getQueKouDate();
		        	LocalDate huidate = tmpqk.getQueKouHuiBuDate();
		        	String sqlhuibupart ;
		        	Double shoupanjia = tmpqk.getShouPanJia();
		        	
		        	try {
		        		String sqlinsertstat;
		        		if(huidate == null) {
		        			 sqlinsertstat = "INSERT INTO 缺口统计表(股票代码,产生日期,缺口上限,缺口下限,缺口类型,回补日期,产生收盘价) VALUES ("
			   						+ "'" + nodecode.trim() + "'" + ","
			        				+ "'" + qkdate + "'," 
			   						+ qkup + ","
			   						+ qkdown + ","
			   						+ type + ","
			   						+ null +  ","
			   						+ shoupanjia + ")"
			   						+ " ON DUPLICATE KEY UPDATE "
			   						+ " 回补日期 =" + null + "," 
			   						+ " 缺口上限 =" + qkup + ","
			   						+ " 缺口下限 =" + qkdown  + ","
			   						+ " 产生收盘价 = " + shoupanjia + ","
			        				+ " 缺口类型 = " + type
			   						;
		        		} else {
		        			 sqlinsertstat = "INSERT INTO 缺口统计表(股票代码,产生日期,缺口上限,缺口下限,缺口类型,回补日期,产生收盘价) VALUES ("
			   						+ "'" + nodecode.trim() + "'" + ","
			        				+ "'" + qkdate + "'," 
			   						+ qkup + ","
			   						+ qkdown + ","
			   						+ type + ","
			   						+ "'" + huidate  + "'," 
			   						+ shoupanjia + ")"
			   						+ " ON DUPLICATE KEY UPDATE "
			   						+ " 回补日期 =" + " '" + huidate + "'," 
			   						+ " 缺口上限 =" + qkup + ","
			   						+ " 缺口下限 =" + qkdown  + ","
			   						+ " 产生收盘价 = " + shoupanjia + ","
			        				+ " 缺口类型 = " + type
			   						;
		        			
		        		}
		        		
								
		   				logger.debug(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   					        		
					} catch(java.lang.NullPointerException e){ 
					    	e.printStackTrace();
					} catch(Exception e){
					    	e.printStackTrace();
					} finally {
					}

				
				
			}
			
			return true;
		}
		public LocalDate getLatestTDXNodeQueKouDate() 
		{
			Set<String> namelist = new HashSet<String> ();
			CachedRowSetImpl rspd = null;
			try {
				String sqlquerystat = "SELECT  GREATEST (a.maxqk, a.maxhb ) AS MAXDATE "
						+ "FROM ( SELECT MAX(产生日期) AS maxqk, MAX(回补日期) AS maxhb	FROM 缺口统计表	"
						+ ") a"
						;
			    	logger.debug(sqlquerystat);
			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			    	java.sql.Date maxdate = null;
			        while(rspd.next())  {
			        	 maxdate =  rspd.getDate("MAXDATE");
			        }
			        
			        return maxdate.toLocalDate();
			        
			} catch(java.lang.NullPointerException e){ e.printStackTrace();
			} catch (SQLException e) {e.printStackTrace();
			} catch(Exception e){e.printStackTrace();
			} finally {
			    	if(rspd != null)
						try {rspd.close();rspd = null;
						} catch (SQLException e) {e.printStackTrace();}
			}
			
			return null;
		}
		/*
		 *从个股日线数据直接计算出缺口信息 
		 */
		public Stock getStockDailyQueKouAnalysisResult (Stock stock,LocalDate requiredstartday,LocalDate requiredendday, String period)
		{
			if( ((Stock)stock).isVeryVeryNewXinStock(requiredstartday) ) //新股的缺口不考虑
				return stock;
			
			NodeXPeriodData stockdailyxdate = stock.getNodeXPeroidData(period);
			List<QueKou> qklist = stockdailyxdate.getPeriodQueKou();
			
			qklist = checkQueKouForAGivenPeriod ( stock, requiredstartday, requiredendday,qklist,NodeGivenPeriodDataItem.DAY );
			try {	Collections.sort(qklist, new NodeLocalDateComparator() );
			} catch ( java.lang.IllegalArgumentException e) {e.printStackTrace();}
			stockdailyxdate.setPeriodQueKou(qklist);
			
			 return stock;
		}
		
		/*
		 * 
		 */
		public Boolean updateNodeSocialFriendShips(BanKuai mainnode, BanKuai friend, Boolean relationship)
		{
			Set<String>	friendsetpostive = mainnode.getSocialFriendsSetPostive();
			Set<String>	friendsetnegtive = mainnode.getSocialFriendsSetNegtive();
			
			String friendcode = friend.getMyOwnCode();
			Boolean alreadyinpostive = false;
			if( friendsetpostive.contains(friendcode ) )
				alreadyinpostive = true;
			Boolean alreadyinnegtive = false;
			if( friendsetnegtive.contains(friendcode ) )
				alreadyinnegtive = true;
				
			if( (alreadyinpostive && relationship) || (alreadyinnegtive && !relationship) ) { //已经是朋友了要取消
				String sqlupdatestat = "DELETE FROM 通达信板块社交关系表  "
						+ " WHERE ( "
						+ "( '板块左' = '" + mainnode.getMyOwnCode() + "'"
						+ " AND '板块右' = '" + friend.getMyOwnCode() + "') " 
						+ " OR "
						+ "( '板块右' = '" + mainnode.getMyOwnCode() + "'"
						+ " AND '板块左' = '" + friend.getMyOwnCode() + "') ) "
						;
				try {
					int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
				} catch(java.lang.NullPointerException e){ e.printStackTrace();
				} catch(Exception e){e.printStackTrace();
				} finally {}
				
				if(alreadyinpostive)	friendsetpostive.remove(friendcode );
				if(alreadyinnegtive)	friendsetnegtive.remove(friendcode );
			} else 
			if( (alreadyinpostive && !relationship) || (alreadyinnegtive && relationship) ) { //已经是朋友,要取反
				String sqlupdatestat = "UPDATE  通达信板块社交关系表  SET 关系= " + relationship
						+ " WHERE ( "
						+ "( '板块左' = '" + mainnode.getMyOwnCode() + "'"
						+ " AND '板块右' = '" + friend.getMyOwnCode() + "') " 
						+ " OR "
						+ "( '板块右' = '" + mainnode.getMyOwnCode() + "'"
						+ " AND '板块左' = '" + friend.getMyOwnCode() + "') ) "
						;
				try {
					int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
				} catch(java.lang.NullPointerException e){e.printStackTrace();
				} catch(Exception e){e.printStackTrace();
				} finally {}
				if(alreadyinpostive) {
					friendsetpostive.remove(friendcode );
					friendsetnegtive.add(friendcode );
				}
				if(alreadyinnegtive){
					friendsetpostive.add(friendcode );
					friendsetnegtive.remove(friendcode );
				}
			}
			else { //不是朋友，要加入
				String sqlinsertstat =  "INSERT INTO  通达信板块社交关系表(板块左,板块右,关系) values ("
   						+ " '" + mainnode.getMyOwnCode() + "'" + ","
   						+ " '" + friend.getMyOwnCode() + "'"  + ","
   						+ relationship
   						+ ")"
   						;
				try {
					int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlinsertstat);

				} catch(java.lang.NullPointerException e){e.printStackTrace();
				} catch(Exception e){e.printStackTrace();
				} finally {}
				
				if(relationship) friendsetpostive.add(friendcode );
				else	friendsetnegtive.add(friendcode );
			}
			
			return null;
		}
		/*
		 * 
		 */
		public BkChanYeLianTreeNode getNodeCjeCjlExtremeZhanbiUpDownLevel(TDXNodes node) 
		{
			String sqlquerystat = "SELECT  * FROM 板块股票占比阈值"
					+ " WHERE 代码 = '" + node.getMyOwnCode() + "'"
					;
			CachedRowSetImpl	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			try {
			        while(rspd.next())  {
			        	Double cjezbmax =  rspd.getDouble("成交额占比上限");
			        	Double cjezbmin =  rspd.getDouble("成交额占比下限");
			        	
			        	Double cjlzbmax =  rspd.getDouble("成交量占比上限");
			        	Double cjlzbmin =  rspd.getDouble("成交量占比下限");
			        	
			        	node.getNodeJiBenMian().setNodeCjlZhanbiLevel(cjlzbmin, cjlzbmax);
			        	node.getNodeJiBenMian().setNodeCjeZhanbiLevel(cjezbmin, cjezbmax);
			        }
			} catch(java.lang.NullPointerException e){ e.printStackTrace();
			} catch (SQLException e) {e.printStackTrace();
			} catch(Exception e){e.printStackTrace();
			} finally {
			    	if(rspd != null)
						try {rspd.close();rspd = null;
						} catch (SQLException e) {e.printStackTrace();}
			}
			
			return node;
		}
		/*
		 * 
		 */
		public BkChanYeLianTreeNode setNodeCjlExtremeZhanbiUpDownLevel(TDXNodes node, Double min, Double max) 
		{
				String sqlinsertstat = "INSERT INTO 板块股票占比阈值(代码,成交量占比下限,成交量占比上限) VALUES ("
						+ "'" + node.getMyOwnCode().trim() + "'" + ","
        				+ min + "," 
   						+ max
   						+ ")"
   						+ " ON DUPLICATE KEY UPDATE "
   						+ " 成交量占比下限 =" + min + "," 
   						+ " 成交量占比上限 =" + max 
   						;
				try {
					int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();} 
			
			return node;
		}
				/*
		 * 
		 */
		public BkChanYeLianTreeNode setNodeCjeExtremeZhanbiUpDownLevel(TDXNodes node, Double min, Double max) 
		{
			int nodetype = node.getType();
			String sqlinsertstat = "INSERT INTO 板块股票占比阈值(代码,node类型,成交额占比下限,成交额占比上限) VALUES ("
					+ "'" + node.getMyOwnCode().trim() + "'" + ","
					+ nodetype + ","
    				+ min + "," 
						+ max
						+ ")"
						+ " ON DUPLICATE KEY UPDATE "
						+ " 成交额占比下限 =" + min + "," 
						+ " 成交额占比上限 =" + max + ","
						+ " node类型 = " + nodetype
						;
			try {
				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
			} catch (MysqlDataTruncation e) {e.printStackTrace();
			} catch (SQLException e) {e.printStackTrace();} 
		
			return node;
		}
		/*
		 * 
		 */
		public Collection<BkChanYeLianTreeNode> searchKeyWordsInStockJiBenMian(String searchstring) 
		{
			if(searchstring.trim().isEmpty())
				return null	;
			
			Collection<BkChanYeLianTreeNode> nodeset = new ArrayList <> ();
			
			String searchstringformated = "'%" + searchstring.trim()  + "%'";
			String searchsqlstat = "SELECT * FROM A股  WHERE 概念板块提醒 LIKE " + searchstringformated + " OR 负面消息 LIKE " + searchstringformated + " " + "ORDER BY 股票代码" ; 
			CachedRowSetImpl  rs = null;
			try {
				rs = connectdb.sqlQueryStatExecute(searchsqlstat);
				while(rs.next() ) {
					String stockcode = rs.getString("股票代码");
					BkChanYeLianTreeNode node = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(stockcode, BkChanYeLianTreeNode.TDXGG);
					if(node != null) nodeset.add(node);
				}
			} catch(java.lang.NullPointerException e) {
		    } catch(Exception e){e.printStackTrace();
		    } finally {
		    	if(rs != null)
				try { rs.close();rs = null;
				} catch (SQLException e) {e.printStackTrace();}
		    }
			return nodeset;
		}
		public Collection<BkChanYeLianTreeNode> searchKeyWordsInBanKuaiJiBenMian(String searchstring) 
		{
			if(searchstring.trim().isEmpty())
				return null	;
			
			Collection<BkChanYeLianTreeNode> nodeset = new ArrayList <> ();
			
			String searchstringformated = "'*" + searchstring  + "*'";
			String searchsqlstat = "SELECT * FROM 通达信板块列表  WHERE 概念板块提醒 LIKE " + searchstringformated + " OR 负面消息 LIKE " + searchstringformated + " " + "ORDER BY 板块ID" ; 
			CachedRowSetImpl  rs = null;
			try {
				rs = connectdb.sqlQueryStatExecute(searchsqlstat);
				while(rs.next() ) {
					String stockcode = rs.getString("股票代码");
					BkChanYeLianTreeNode node = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(stockcode, BkChanYeLianTreeNode.TDXBK);
					if(node != null) nodeset.add(node);
				}
			} catch(java.lang.NullPointerException e) {
		    } catch(Exception e){e.printStackTrace();
		    } finally {
		    	if(rs != null)
				try { rs.close();rs = null;
				} catch (SQLException e) {e.printStackTrace();}
		    }
			return nodeset;
		}
		/*
		 * 
		 */
		public void forcedeleteBanKuaiImportedDailyExchangeData(BanKuai bk) 
		{
			String searchtable = null;
			if(BkChanYeLianTreeNode.isBanKuai(bk)) 
				searchtable = this.getBanKuaiJiaoYiLiangBiaoName(bk);
			try {
				String sqlquerystat = "DELETE FROM " + searchtable + " WHERE 代码 = '" + bk.getMyOwnCode() + "'";
				connectdb.sqlDeleteStatExecute(sqlquerystat);
			} catch(java.lang.NullPointerException e) {
		    } catch(Exception e){e.printStackTrace();
		    } finally {  }
		}
		/*
		 * 
		 */
		public void forcedeleteBanKuaiImportedGeGuData(BanKuai bk) 
		{
			try {
				String searchtable = bk.getShuJuJiLuInfo().getGuPiaoBanKuaiDuiYingBiao();
				if(searchtable == null) return;
				String sqlquerystat = "DELETE FROM " + searchtable + " WHERE 板块代码 = '" + bk.getMyOwnCode() + "'";
				connectdb.sqlQueryStatExecute(sqlquerystat);
			} catch(java.lang.NullPointerException e) {
		    } catch(Exception e){e.printStackTrace();
		    } finally {  }
		}
		/*
		 * 
		 */
		public void saveBanKuaiExtraDataToDatabase(BanKuai bk, LocalDate date, String extradatakeywords[] ) 
		{
			NodeXPeriodData nodexdatawk = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
			NodeXPeriodData nodexdataday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
			
			String setString = "";
			for(String keyword : extradatakeywords ) {
				Object value = nodexdataday.getNodeDataByKeyWord(keyword,date,"");
				if(value == null) break;
				if( (Double)value == 100.0) 
					continue;
				
				switch(keyword) {
				case "CjeZbGrowRate":	setString = setString + " DPCJEZB增长率  = " + ((Double)value).toString() + ",";
				break;
				case "CjlZbGrowRate":	setString = setString + " DPCJLZB增长率  = " + ((Double)value).toString() + ",";
				break;
				}
			}
			try {
				setString = setString.trim().substring(0, setString.length() - 2);
				setString = " SET " + setString ;
			} catch ( java.lang.StringIndexOutOfBoundsException e) {
				e.printStackTrace(); return;
			}
			
			String bkcys = bk.getNodeJiBenMian().getSuoShuJiaoYiSuo();
			if(bkcys == null)	return ;
			
			String bkcjltable = null;
			if(bkcys.equalsIgnoreCase("sh"))
				bkcjltable = "通达信板块每日交易信息";
			else if(bkcys.equalsIgnoreCase("sz"))
				bkcjltable = "通达信交易所指数每日交易信息";
			else if(bkcys.equalsIgnoreCase("BK"))	
				bkcjltable = "大智慧板块每日交易信息";
			
			String sqlupdatestat = "UPDATE " + bkcjltable  +"\r\n"
					+ setString + "\r\n"  
					+ "  WHERE " + bkcjltable + ".`代码` = " + "'" + bk.getMyOwnCode() + "' \r\n"
					+ "  AND " + bkcjltable + ".交易日期 = '" +  date.toString()  + "'"
					;
			try {
				connectdb.sqlUpdateStatExecute(sqlupdatestat);
			} catch(java.lang.NullPointerException e) {
		    } catch(Exception e){e.printStackTrace(); System.out.print(sqlupdatestat + "\r\n");
		    } finally {  }
			return;
		}

}


class NodeLocalDateComparator implements Comparator<QueKou> 
{
    public int compare(QueKou qk1, QueKou qk2) 
    {
    	LocalDate qk1date = qk1.getQueKouDate();
    	LocalDate qk2date = qk2.getQueKouDate();
       
        try{
        	if(qk1date.isBefore(qk2date))	return -1;
        	else return 1;
        } catch (java.lang.NullPointerException e) { return -1;}
    	  catch (java.lang.Exception e) { return -1;}
    }
}