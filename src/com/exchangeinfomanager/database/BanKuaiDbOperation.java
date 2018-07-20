package com.exchangeinfomanager.database;

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
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.bankuaifengxi.ai.ZhongDianGuanZhu;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.sun.rowset.CachedRowSetImpl;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;


public class BanKuaiDbOperation 
{
	public BanKuaiDbOperation() 
	{
		initializeDb ();
		initialzieSysconf ();
	}
	
	private  ConnectDataBase connectdb;
	private  SystemConfigration sysconfig;
	private static Logger logger = Logger.getLogger(BanKuaiDbOperation.class);

	private void initializeDb() 
	{
		connectdb = ConnectDataBase.getInstance();
	}
	private void initialzieSysconf ()
	{
		sysconfig = SystemConfigration.getInstance();
	}
	/*
	 * 
	 */
	public Stock getStockBasicInfo(Stock stockbasicinfo) 
	{
		String stockcode = stockbasicinfo.getMyOwnCode();
		CachedRowSetImpl rsagu = null;
		try {
			 String sqlquerystat= "SELECT * FROM A股   WHERE 股票代码 =" +"'" + stockcode +"'" ;	
			rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);
			while(rsagu.next())
				setSingleNodeInfo (stockbasicinfo,rsagu);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rsagu.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rsagu = null;
		}
		
		return stockbasicinfo;
	}
	/*
	 * 
	 */
	public BanKuai getBanKuaiBasicInfo(BanKuai bkbasicinfo)
	{
		String bkcode = bkbasicinfo.getMyOwnCode();
		CachedRowSetImpl rsagu = null;
		try {
			 String sqlquerystat= "select 板块名称, 概念时间,概念板块提醒,负面消息时间,负面消息,券商评级时间,券商评级提醒,正相关及客户,负相关及竞争对手,客户,竞争对手 from 通达信板块列表  where 板块ID = '" + bkcode +"' \r\n"  
//			 		"union\r\n" + 
//			 		"select 板块名称,概念时间,概念板块提醒,负面消息时间,负面消息,券商评级时间,券商评级提醒,正相关及客户,负相关及竞争对手,客户,竞争对手 from 通达信交易所指数列表  where 板块ID = '" + bkcode + "' " 
					 			;
			 logger.debug(sqlquerystat);
			rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);
			while(rsagu.next())
				setSingleNodeInfo (bkbasicinfo,rsagu);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rsagu.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rsagu = null;
		}
		
		return bkbasicinfo;

	}
	/*
	 * 获取用户输入的node的基本信息，node可能是板块也可能是个股
	 */
	public ArrayList<BkChanYeLianTreeNode> getNodesBasicInfo(String nodecode) 
	{
		ArrayList<BkChanYeLianTreeNode> nodenamelist = new ArrayList<BkChanYeLianTreeNode> ();
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
		        	String stockname = rs.getString("股票名称");
		        	BkChanYeLianTreeNode tmpnode = null;
		        	
		        	if(rs.getString("type").equals("gegu")) {
		        		tmpnode = new Stock(nodecode,stockname);
		        		
		        		sqlquerystat= "SELECT * FROM A股   WHERE 股票代码 =" +"'" + nodecode +"'" ;
		        		CachedRowSetImpl rsagu = null;
		    			rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);
		    			
		    			while(rsagu.next())
		    				setSingleNodeInfo(tmpnode,rsagu);
		    			
		    			rsagu.close();
		    			rsagu = null;
		        	}
		        	else {
		        		tmpnode = new BanKuai(nodecode,stockname);
		        		String searchtable = rs.getString("tablename");
		        		
		        		sqlquerystat= "SELECT * FROM " +  searchtable  + "  WHERE 板块ID =" +"'" + nodecode +"'" ;
		        		CachedRowSetImpl rsagu = null;
		    			rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);
		    			
		    			while(rsagu.next())
		    				setSingleNodeInfo(tmpnode,rsagu);
		    			
		    			rsagu.close();
		    			rsagu = null;
		        	}
		        	
		        	nodenamelist.add(tmpnode);
		        }
		       
		    }catch(java.lang.NullPointerException e){ 
		    	e.printStackTrace();
		    } catch (SQLException e) {
		    	e.printStackTrace();
		    }catch(Exception e){
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
		return nodenamelist;
	}


	
	private void setSingleNodeInfo(BkChanYeLianTreeNode tmpnode, CachedRowSetImpl rs) 
	{
		if(tmpnode.getType() == 6) {
			try {
				String stockname = rs.getString("股票名称").trim();
				tmpnode.setMyOwnName(stockname);
			} catch(java.lang.NullPointerException ex1) {
				tmpnode.setMyOwnName( " ");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				Boolean stockname = rs.getBoolean("已退市");
				//这里应该设置退市状态，目前没那么重要，所以暂时不开发
			} catch(java.lang.NullPointerException ex1) {
				Boolean stockname = false;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				String bankuainame = rs.getString("板块名称").trim();
				tmpnode.setMyOwnName(bankuainame);
			} catch(java.lang.NullPointerException ex1) {
				tmpnode.setMyOwnName( " ");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		BkChanYeLianTreeNode.NodeJiBenMian nodejbm = tmpnode.getNodeJiBenMian();
		
		try {
			java.util.Date gainiantishidate = rs.getDate("概念时间");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(gainiantishidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			nodejbm.setGainiantishidate(ldgainiantishidate); 
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setGainiantishidate(null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		try {
			String gainiantishi = rs.getString("概念板块提醒");
			nodejbm.setGainiantishi(gainiantishi);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setGainiantishi(" ");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try 
		{
			Date quanshangpingjidate = rs.getDate("券商评级时间");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(quanshangpingjidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			nodejbm.setQuanshangpingjidate(ldgainiantishidate);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setQuanshangpingjidate(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try	{
			String quanshangpingji = rs.getString("券商评级提醒").trim();
			nodejbm.setQuanshangpingji(quanshangpingji);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setQuanshangpingji("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try	{
			Date fumianxiaoxidate = rs.getDate("负面消息时间");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(fumianxiaoxidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			nodejbm.setFumianxiaoxidate(ldgainiantishidate);
		} catch(java.lang.NullPointerException ex1) { 
			nodejbm.setFumianxiaoxidate(null);
		} catch(Exception ex2) {
			ex2.printStackTrace();				
		}

		try	{
			String fumianxiaoxi = rs.getString("负面消息").trim();
			nodejbm.setFumianxiaoxi(fumianxiaoxi);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setFumianxiaoxi("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			String zhengxiangguan = rs.getString("正相关及客户");
			nodejbm.setZhengxiangguan(zhengxiangguan);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setZhengxiangguan("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try {
			String fuxiangguan = rs.getString("负相关及竞争对手");
			nodejbm.setFuxiangguan(fuxiangguan);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setFuxiangguan("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try {
			String keHuCustom = rs.getString("客户");
			nodejbm.setKeHuCustom(keHuCustom);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setKeHuCustom("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try {
			String jingZhengDuiShou = rs.getString("竞争对手");
			nodejbm.setJingZhengDuiShou(jingZhengDuiShou);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setJingZhengDuiShou("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
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
	 * 
	 */
	public File refreshDZHStockGuQuan() 
	{
//		File file = new File(sysconfig.getDZHStockGuQuanFile() );
		File file = new File("E:/stock/dzh365/Download/PWR/full_sh.PWR");
		 if(!file.exists() ) {
			 return null;
		 }
		 
		 BufferedInputStream dis = null;
		 FileInputStream in = null;
		 try {
			    in = new FileInputStream(file);  
			    dis = new BufferedInputStream(in);
			    
			    
			    int fileheadbytenumber = 12;
	            byte[] itemBuf = new byte[fileheadbytenumber];
	            dis.read(itemBuf, 0, fileheadbytenumber); 
	            String fileHead =new String(itemBuf,0,fileheadbytenumber);  
	            logger.debug(fileHead);
	            
	            while(true) {
	            	int sigbk = 16;
		            byte[] itemBuf2 = new byte[sigbk];
//		            int i=0;
		            dis.read(itemBuf2, 0, sigbk) ;
		            String stockcode = new String(itemBuf2, 0, sigbk );
		            logger.debug(stockcode);
		            
		            int sigbk2 = 20;
		            byte[] itemBuf3 = new byte[sigbk2];
//		            int i=0;
		            dis.read(itemBuf3, 0, sigbk2) ;
		            String stockdate = new String(itemBuf3, 0, sigbk2 );
		            logger.debug(stockdate);
		            
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

	            }
			    
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
		 
		 
		 
//		return null;
	}
	/*
	 * 通达信的概念板块与个股对应
	 */
	private int refreshTDXGaiNianBanKuaiToGeGu (File tmprecordfile)
	{
		File file = new File(sysconfig.getTDXGaiNianBanKuaiToGeGuFile() );
		 if(!file.exists() ) {
			 try {
				Files.append( "没有发现" +  sysconfig.getTDXGaiNianBanKuaiToGeGuFile() +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			} catch (IOException e) {
				e.printStackTrace();
			}
			 return -1;
		 }
		 
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
               int fileheadbytenumber = 384;
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
            	   logger.debug(gupiaoheader);
            	   
            	   //板块个股
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                  
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   logger.debug(tmpstockcodestr);
//                   System.out.print(tmpstockcodestr.size());
                   Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                   logger.debug(tmpstockcodesetnew);
                   
                   //从数据库中读出当前该板块的个股
                   Set<String> tmpstockcodesetold = new HashSet();
                   CachedRowSetImpl rs = null;
	   			    try {
	   			    	String sqlquerystat = "SELECT gnbk.股票代码  FROM 股票通达信概念板块对应表 gnbk, 通达信板块列表 bklb"
		    						+ " WHERE  gnbk.板块代码 = bklb.`板块ID`"
		    						+ " AND bklb.`板块名称` = '"  + gupiaoheader + "'"
		    						+ " AND ISNULL(gnbk.移除时间)"
		    						;
	   			    	
//	   			    	String sqlquerystat = "SELECT *  FROM 股票通达信概念板块对应表  WHERE  概念板块 = " 
//	   			    							+ "'"  + gupiaoheader + "'"
//	   			    							+ "AND ISNULL(移除时间)"
//	   			    							;
	   			    	logger.debug(sqlquerystat);
	   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			        rs.last();  
	   			        int rows = rs.getRow();  
	   			        rs.first();  
	   			        for(int j=0;j<rows;j++) {  
	   			        	String stockcode = rs.getString("股票代码");
	   			        	tmpstockcodesetold.add(stockcode);
	   			        	
	   			            rs.next();
	   			        }
	   			        
	   			    }catch(java.lang.NullPointerException e){ 
	   			    	e.printStackTrace();
	   			    } catch (SQLException e) {
	   			    	e.printStackTrace();
	   			    }catch(Exception e){
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
	   			    
	   			 //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要从数据库中删除
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
//		   	        	String sqldeletetstat = "DELETE  FROM 股票通达信概念板块对应表 "
//		   	        							+ " WHERE 股票代码=" + "'" + str + "'" 
//		   	        							+ " AND 概念板块=" + "'" + gupiaoheader + "'" 
//		   	        							;
//		   	        	logger.debug(sqldeletetstat);
//		   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//		                Files.append("删除：" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//		   	        	String sqlupdatestat = "UPDATE 股票通达信概念板块对应表   SET"
//		   	        			+ " 移除时间 =" + "'" +  sysconfig.formatDate(new Date() ) + "'" 
//	   			 				+ " WHERE 股票代码 = " + "'" + str.trim() + "'" 
//	   			 				+ " AND 概念板块=" + "'" + gupiaoheader + "'" 
//	   			 				+ " AND isnull(移除时间)"
//	   			 				;
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader bufr = new BufferedReader (tongdaxinfile);
		
		HashMap<String,List<String>> tmpsysbkmap = new HashMap<String, List<String>> ();
		String line = null;  
        try {
			while((line = bufr.readLine()) != null) {
				
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //内蒙板块|880232|3|1|0|32
				logger.debug(tmpbkinfo);
//				if(tmpbkinfo.get(1).toString().trim().equals("880601"))
//					logger.debug("880601 arrived");
//				if( !Pattern.matches("\\d{4}00",tmpbkinfo.get(5) ) ) { //如果是申万板块，不导入
//					tmpsysbkmap.put(tmpbkinfo.get(1), tmpbkinfo );
					
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
	        	   logger.debug(sqlinsertstat);
	   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				tongdaxinfile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        
        try {
			Files.append("开始导入通达信板块信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		} catch (IOException e3) {
			e3.printStackTrace();
		}
        
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
				logger.debug(line);
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //T01|能源  T010101|煤炭开采
				logger.debug(tmpbkinfo);
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
   			      
   			    }catch(java.lang.NullPointerException e){ 
   			    	e.printStackTrace();
   			    } catch (SQLException e) {
   			    	e.printStackTrace();
   			    }catch(Exception e){
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
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				fr.close();
				fr = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				bufr.close();
				bufr = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
            	   logger.debug(gupiaoheader);
            	   
            	   //板块个股
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                  
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   logger.debug(tmpstockcodestr);
//                   System.out.print(tmpstockcodestr.size());
                   Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                   logger.debug(tmpstockcodesetnew);
                   
                   //从数据库中读出当前该板块的个股
                   Set<String> tmpstockcodesetold = new HashSet();
                   CachedRowSetImpl rs = null;
	   			    try {
	   			    	String sqlquerystat = "SELECT gnbk.股票代码  FROM 股票通达信概念板块对应表 gnbk, 通达信板块列表 bklb"
		    						+ " WHERE  gnbk.板块代码 = bklb.`板块ID`"
		    						+ " AND bklb.`板块名称` = '"  + gupiaoheader + "'"
		    						+ " AND ISNULL(gnbk.移除时间)"
		    						;
	   			    	
//	   			    	String sqlquerystat = "SELECT *  FROM 股票通达信概念板块对应表  WHERE  概念板块 = " 
//	   			    							+ "'"  + gupiaoheader + "'"
//	   			    							+ "AND ISNULL(移除时间)"
//	   			    							;
	   			    	logger.debug(sqlquerystat);
	   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			        rs.last();  
	   			        int rows = rs.getRow();  
	   			        rs.first();  
	   			        for(int j=0;j<rows;j++) {  
	   			        	String stockcode = rs.getString("股票代码");
	   			        	tmpstockcodesetold.add(stockcode);
	   			        	
	   			            rs.next();
	   			        }
	   			        
	   			    }catch(java.lang.NullPointerException e){ 
	   			    	e.printStackTrace();
	   			    } catch (SQLException e) {
	   			    	e.printStackTrace();
	   			    }catch(Exception e){
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
	   			    
	   			 //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要从数据库中删除
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
//		   	        	String sqldeletetstat = "DELETE  FROM 股票通达信概念板块对应表 "
//		   	        							+ " WHERE 股票代码=" + "'" + str + "'" 
//		   	        							+ " AND 概念板块=" + "'" + gupiaoheader + "'" 
//		   	        							;
//		   	        	logger.debug(sqldeletetstat);
//		   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//		                Files.append("删除：" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//		   	        	String sqlupdatestat = "UPDATE 股票通达信概念板块对应表   SET"
//		   	        			+ " 移除时间 =" + "'" +  sysconfig.formatDate(new Date() ) + "'" 
//	   			 				+ " WHERE 股票代码 = " + "'" + str.trim() + "'" 
//	   			 				+ " AND 概念板块=" + "'" + gupiaoheader + "'" 
//	   			 				+ " AND isnull(移除时间)"
//	   			 				;
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
	 		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
	 		
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
			autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		
	}
	/*
	 *早期代码有错误，导致板块和个股的对应关系中可能出现重复， 
	 */
	public File checkImportTDXDataSync()
	{
		String checkfilename = sysconfig.getSystemInstalledPath ()+  "通达信数据一致性检查结果.txt";
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
									Files.append(updateresult,filefmxx, charset);
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
								Files.append(stock,filefmxx, charset);
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
		
		if(countaddline >0)
			return filefmxx;
		else
			return null;
	}
	
	/*
	 * 通达信的风格与个股代码对应文件
	 */
	private void refreshTDXFengGeBanKuaiToGeGu (File tmprecordfile)
	{
		File file = new File(sysconfig.getTDXFengGeBanKuaiToGeGuFile() );
		 
		 if(!file.exists() ) {
			 logger.debug("File not exist");
			 return;
		 }
		 
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
               Files.append("开始导入通达信股票风格对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   //板块名称
            	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
            	   logger.debug(gupiaoheader);
            	   
            	   //板块个股
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   logger.debug(tmpstockcodestr);
                   logger.debug(tmpstockcodestr.size());
                   Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                   logger.debug(tmpstockcodesetnew);
                   
                   //比如 融资融券， 是个板块，但没有ID，在通达信板块列表里面没有，要先判断这种情形，以便用不同的SQL
                   boolean hasbkcode = true;
                   CachedRowSetImpl rspd = null; 
	   			   try {
	   			    	String sqlquerystat = "SELECT count(*) as result FROM 通达信板块列表 WHERE 板块名称='"  + gupiaoheader + "'";
	   			    	logger.debug(sqlquerystat);
	   			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			    	int result = 0;
	   			        while(rspd.next())  {
	   			        	result = rspd.getInt("result");
	   			        }
	   			   		if(result == 0)
	   			   			hasbkcode = false;
	   			       
	   			    }catch(java.lang.NullPointerException e){ 
	   			    	e.printStackTrace();
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

                   
                   //从数据库中读出当前该板块的个股
                   Set<String> tmpstockcodesetold = new HashSet();
                   CachedRowSetImpl rs = null; 
	   			   try {
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

	   			    	logger.debug(sqlquerystat);
	   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			        rs.last();  
	   			        int rows = rs.getRow();  
	   			        rs.first();  
	   			        for(int j=0;j<rows;j++) {  //把数据库中现有的个股代码保存
	   			        	String stockcode = rs.getString("股票代码");
	   			        	tmpstockcodesetold.add(stockcode);
	   			        	
	   			            rs.next();
	   			        }
	   			       
	   			    }catch(java.lang.NullPointerException e){ 
	   			    	e.printStackTrace();
	   			    } catch (SQLException e) {
	   			    	e.printStackTrace();
	   			    }catch(Exception e){
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
		 } catch (Exception e) {
			 e.printStackTrace();
		 } finally {
             try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             try {
				dis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		if(!file.exists() ) {
			 logger.debug("File not exist");
			 return -1;
		 }
		 
		 int addednumber =0;
		 FileInputStream in = null;  
		 BufferedInputStream dis = null;
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
              logger.debug(fileHead);
              
              //板块个数：2字节
              dis.read(itemBuf, 0, 2); 
              String bknumber =new String(itemBuf,0,2);  
              logger.debug(bknumber);
			 
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
           	   logger.debug(gupiaoheader);
           	   
           	   //板块个股
                  String gupiaolist =new String(itemBuf2,12,sigbk-12);
                 
                  List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                  logger.debug(tmpstockcodestr);
//                  System.out.print(tmpstockcodestr.size());
                  Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                  logger.debug(tmpstockcodesetnew);
                  
                  //从数据库中读出当前该板块的个股
                  Set<String> tmpstockcodesetold = new HashSet();
                  CachedRowSetImpl rs = null;
	   			    try {
//	   			    	String sqlquerystat = "SELECT *  FROM 股票通达信交易所指数对应表   WHERE  指数板块 = " 
//	   			    							+ "'"  + gupiaoheader + "'"
//	   			    							+ " AND ISNULL(移除时间)"
//	   			    							;
	   			    	String sqlquerystat = "SELECT zsbk.股票代码  FROM 股票通达信交易所指数对应表  zsbk, 通达信板块列表 zslb"
	    						+ " WHERE  zsbk.板块代码 = zslb.`板块ID`"
	    						+ " AND zslb.`板块名称` = '"  + gupiaoheader + "'"
	    						+ " AND ISNULL(zsbk.移除时间)"
	    						;
	   			    	logger.debug(sqlquerystat);
	   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			        rs.last();  
	   			        int rows = rs.getRow();  
	   			        rs.first();  
	   			        for(int j=0;j<rows;j++) {  
	   			        	String stockcode = rs.getString("股票代码");
	   			        	tmpstockcodesetold.add(stockcode);
	   			        	
	   			            rs.next();
	   			        }
	   			        
	   			    }catch(java.lang.NullPointerException e){ 
	   			    	e.printStackTrace();
	   			    } catch (SQLException e) {
	   			    	e.printStackTrace();
	   			    }catch(Exception e){
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
				       	logger.debug(sqlupdatestat);
				   		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   	        }
		   	     differencebankuaiold = null;
		   	     
	   		        //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   				
//		   				String sqlinsertstat = "INSERT INTO  股票通达信交易所指数对应表(股票代码,指数板块,股票权重) values ("
//		   						+ "'" + str.trim() + "'" + ","
//		   						+ "'" + gupiaoheader + "'" + ","
//		   						+ 10
//		   						+ ")"
//		   						;
		   		    	String sqlinsertstat = "insert into 股票通达信交易所指数对应表(股票代码,板块代码,股票权重)"
								+ "	  SELECT '" + str.trim() + "', 通达信板块列表.`板块ID`, 10 "
								+ " FROM 通达信板块列表  where 通达信板块列表.`板块名称` = '" + gupiaoheader + "'"   
								;
		   				logger.debug(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				addednumber ++;
		                Files.append("加入：" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   		 differencebankuainew = null;
              }
		 } catch (Exception e) {
			 e.printStackTrace();
			 return -1;
		 } finally {
			 try {
				in.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
             try {
				dis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		
		 return addednumber;
	}
	/*
	 * 从通达信更新所有的交易所指数，上海part
	 */
	private int refreshTDXZhiShuShangHaiLists(File tmprecordfile)
	{
		//先从数据库中读取现有所有的上海指数
		  HashSet<String> curallshbk = this.getTDXBanKuaiSet("sh");
		
		//从文件中读取最新的指数
		HashSet allinsertbk = new HashSet (); //所有最新的指数，用来和curallshbk，以判断是否有旧的指数需要被删除
		
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
//               logger.debug(fileHead);
               
               int sigbk = 18*16+12+14;
               byte[] itemBuf2 = new byte[sigbk];
               int i=0;
               Files.append("开始导入通达信股票指数板块对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   String zhishuline =new String(itemBuf2,0,sigbk);
            	   logger.debug(zhishuline);
            	   String zhishucode = zhishuline.trim().substring(0, 6);
//            	   logger.debug(zhishucode);
            	   
//            	   if(  zhishucode.startsWith("600") || zhishucode.startsWith("601") || zhishucode.startsWith("602") || zhishucode.startsWith("603") 
//            			   || zhishucode.startsWith("10") || zhishucode.startsWith("11") || zhishucode.startsWith("01") || zhishucode.startsWith("02") || zhishucode.startsWith("09") 
//            			   || zhishucode.startsWith("12") || zhishucode.startsWith("13") || zhishucode.startsWith("14") ||  zhishucode.startsWith("18") || zhishucode.startsWith("19") || zhishucode.startsWith("20") 
//            			   || zhishucode.startsWith("50") || zhishucode.startsWith("51") || zhishucode.startsWith("52") 
//            			   || zhishucode.startsWith("75") || zhishucode.startsWith("73") || zhishucode.startsWith("78") || zhishucode.startsWith("79")
//            			   || zhishucode.startsWith("90"))
            	   if( !(zhishucode.startsWith("000") || zhishucode.startsWith("880") || zhishucode.startsWith("999") ))
            		   continue;
            	   
//            	   if(zhishucode.equals("880890"))
//            		   logger.debug("880890 arrived");
            	   
            	   List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(zhishuline.substring(6, zhishuline.length()));
            	   String zhishuname = null;
            	   try {
	            	    zhishuname = tmplinelist.get(0).trim().substring(0, 6).trim();
            	   } catch (java.lang.StringIndexOutOfBoundsException ex) {
            		   List<String> tmplinepartnamelist = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(1).trim());
            		   zhishuname = tmplinelist.get(0).trim() + tmplinepartnamelist.get(0).trim();
            	   }
//            	   logger.debug(zhishuname);
            	   
//            	   String sqlinsertstat = "INSERT INTO  通达信交易所指数列表(板块名称,板块ID,指数所属交易所) values ("
//	   						+ " '" + zhishuname.trim() + "'" + ","
//	   						+ " '" + zhishucode.trim() + "'" + ","
//	   						+ " '" + "sh" + "'"
//	   						+ ")"
//	   						+ " ON DUPLICATE KEY UPDATE "
//	   						+ " 板块名称=" + " '" + zhishuname.trim() + "'" + ","
//	   						+ " 指数所属交易所=" + " '" + "sh" + "'" 
//	   						;
            	   String sqlinsertstat = "INSERT INTO  通达信板块列表(板块名称,板块ID,指数所属交易所) values ("
	   						+ " '" + zhishuname.trim() + "'" + ","
	   						+ " '" + zhishucode.trim() + "'" + ","
	   						+ " '" + "sh" + "'"
	   						+ ")"
	   						+ " ON DUPLICATE KEY UPDATE "
	   						+ "板块名称 =" + " '" + zhishuname.trim() + "'" + ","
	   						+ "指数所属交易所= " + " '" + "sh" + "'"
	   						;
            	   logger.debug(sqlinsertstat);
	   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//	                Files.append("加入：" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	   				allinsertbk.add(zhishucode);
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
		 
		 SetView<String> differencebankuaiold = Sets.difference( curallshbk, allinsertbk );
		 if(differencebankuaiold.size() >0 ) { //说明有某些板块需要删除
			 for(String oldbkcode : differencebankuaiold) {
				 String sqldeletetstat = "DELETE  FROM  通达信板块列表 WHERE "
						 				+ " 板块ID =" + " '" + oldbkcode.trim() + "'" 
						 				;
         	   logger.debug(sqldeletetstat);
	   			int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
	   			
//	   			//还要删除该板块在 股票概念对应表/股票行业对应表/股票风格对应表/产业链子版块列表  中的股票和板块的对应信息
//	    		sqldeletetstat = "DELETE  FROM  股票通达信概念板块对应表"
//						+ " WHERE 板块代码=" + "'"  + oldbkcode.trim() + "'"
//						;
//				logger.debug(sqldeletetstat);
//				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				
//				sqldeletetstat = "DELETE  FROM  股票通达信行业板块对应表"
//						+ " WHERE 板块代码=" + "'"  + oldbkcode.trim() + "'"
//						;
//				logger.debug(sqldeletetstat);
//				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				
//				sqldeletetstat = "DELETE  FROM 股票通达信风格板块对应表"
//						+ " WHERE 板块代码=" + "'"  + oldbkcode.trim() + "'"
//						;
//				logger.debug(sqldeletetstat);
//				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				
//				sqldeletetstat = "DELETE  FROM 产业链子板块列表"
//						+ " WHERE 所属通达信板块=" + "'"  + oldbkcode.trim() + "'"
//						;
//				logger.debug(sqldeletetstat);
//				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
			 }
		 }
		 differencebankuaiold = null;
		
		return 1;
	}
	/*
	 * 从通达信更新所有的交易所指数，深圳part
	 */
	private int refreshTDXZhiShuShenZhenLists(File tmprecordfile)
	{
		//先从数据库中读取现有所有的上海指数
		HashSet<String> curallszbk = this.getTDXBanKuaiSet("sz");
				
		//从文件中读取最新的指数
		HashSet allinsertbk = new HashSet (); //所有最新的指数，用来和curallshbk，以判断是否有旧的指数需要被删除
				
				
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
               Files.append("开始导入通达信股票指数板块对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   String zhishuline =new String(itemBuf2,0,sigbk);
            	   logger.debug(zhishuline);
            	   String zhishucode = zhishuline.trim().substring(0, 6);
//            	   logger.debug(zhishucode);
            	   
//            	   if(  zhishucode.startsWith("000") || zhishucode.startsWith("001") || zhishucode.startsWith("002") || zhishucode.startsWith("300")  
//            			   ||zhishucode.startsWith("100") ||zhishucode.startsWith("101") ||zhishucode.startsWith("106") ||zhishucode.startsWith("107") ||zhishucode.startsWith("108") ||zhishucode.startsWith("109")
//            			   ||zhishucode.startsWith("111") ||zhishucode.startsWith("112") ||zhishucode.startsWith("114") ||zhishucode.startsWith("115") ||zhishucode.startsWith("116") ||zhishucode.startsWith("117") ||zhishucode.startsWith("118") || zhishucode.startsWith("119")
//            			   ||zhishucode.startsWith("120") ||zhishucode.startsWith("121") ||zhishucode.startsWith("123") ||zhishucode.startsWith("127") ||zhishucode.startsWith("128")
//            			   ||zhishucode.startsWith("131") ||zhishucode.startsWith("140") 
//            			   ||zhishucode.startsWith("150") ||zhishucode.startsWith("160") ||zhishucode.startsWith("161") ||zhishucode.startsWith("162") ||zhishucode.startsWith("163")
//            			   ||zhishucode.startsWith("164") ||zhishucode.startsWith("165") ||zhishucode.startsWith("166") ||zhishucode.startsWith("167") ||zhishucode.startsWith("168") ||zhishucode.startsWith("169")
//            			   ||zhishucode.startsWith("184")
//            			   ||zhishucode.startsWith("200")
//            			   ||zhishucode.startsWith("395")
//            			)
            	   if(! (zhishucode.startsWith("399") || zhishucode.startsWith("159") ) )
            		   continue;
            	   
            	   List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(zhishuline.substring(6, zhishuline.length()));
            	   String zhishuname = null;
            	   try {
	            	    zhishuname = tmplinelist.get(0).trim().substring(0, 6).trim();
            	   } catch (java.lang.StringIndexOutOfBoundsException ex) {
            		   List<String> tmplinepartnamelist = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(1).trim());
            		   zhishuname = tmplinelist.get(0).trim() + tmplinepartnamelist.get(0).trim();
            	   }
//            	   logger.debug(zhishuname);
            	   
//            	   String sqlinsertstat = "INSERT INTO  通达信交易所指数列表(板块名称,板块ID,指数所属交易所) values ("
//	   						+ " '" + zhishuname.trim() + "'" + ","
//	   						+ " '" + zhishucode.trim() + "'" + ","
//	   						+ " '" + "sz" + "'"
//	   						+ ")"
//	   						+ " ON DUPLICATE KEY UPDATE "
//	   						+ " 板块名称=" + " '" + zhishuname.trim() + "'" + ","
//	   						+ " 指数所属交易所=" + " '" + "sz" + "'" 
//	   						;
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
//	                Files.append("加入：" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	   				allinsertbk.add(zhishucode);
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
		 
		 SetView<String> differencebankuaiold = Sets.difference(curallszbk, allinsertbk );
		 if(differencebankuaiold.size() >0 ) { //说明有某些板块需要删除
			 for(String oldbkcode : differencebankuaiold) {
				 String sqldeletetstat = "DELETE  FROM  通达信板块列表 WHERE "
						 				+ " 板块ID =" + " '" + oldbkcode.trim() + "'" 
						 				;
         	   logger.debug(sqldeletetstat);
	   			int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
	   			
	   			//还要删除该板块在 股票概念对应表/股票行业对应表/股票风格对应表/产业链子版块列表  中的股票和板块的对应信息
	    		sqldeletetstat = "DELETE  FROM  股票通达信概念板块对应表"
						+ " WHERE 板块代码=" + "'"  + oldbkcode.trim() + "'"
						;
				logger.debug(sqldeletetstat);
				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
				
				sqldeletetstat = "DELETE  FROM  股票通达信行业板块对应表"
						+ " WHERE 板块代码=" + "'"  + oldbkcode.trim() + "'"
						;
				logger.debug(sqldeletetstat);
				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
				
				sqldeletetstat = "DELETE  FROM 股票通达信风格板块对应表"
						+ " WHERE 板块代码=" + "'"  + oldbkcode.trim() + "'"
						;
				logger.debug(sqldeletetstat);
				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
				
				sqldeletetstat = "DELETE  FROM 产业链子板块列表"
						+ " WHERE 所属通达信板块=" + "'"  + oldbkcode.trim() + "'"
						;
				logger.debug(sqldeletetstat);
				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
			 }
		 }
		 differencebankuaiold = null;
		return -1;
	}

	

	/*
	 * 找出数据库中通达信自定义板块
	 */
	private HashMap<String, BanKuai> initializeSystemAllZdyBanKuaiList()  
	{
		//HashMap<String,BanKuai> sysbankuailiebiaoinfo;
//		private  HashMap<String,BanKuai> zdybankuailiebiaoinfo;
		HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();

		String sqlquerystat = "SELECT 板块ID,板块名称,创建时间  FROM 通达信自定义板块列表 	ORDER BY 板块ID,板块名称 ,创建时间 "
							   ;   
		logger.debug(sqlquerystat);
		CachedRowSetImpl  rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	        rs.last();  
	        int rows = rs.getRow();  
//	        data = new Object[rows][];    
//	        int columnCount = 3;//列数  
	        rs.first();  
	        //int k = 0;  
	        //while(rs.next())
	        for(int j=0;j<rows;j++) {  
	            //Object[] row = new Object[columnCount]; 
	        	BanKuai tmpbk = new BanKuai (rs.getString("板块ID"),rs.getString("板块名称") );
	        	tmpsysbankuailiebiaoinfo.put(rs.getString("板块ID"), tmpbk);
	            rs.next();
	        }
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
	    	e.printStackTrace();
	    } finally {
	    	try {
				rs.close ();
				rs = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
	    
	    return  tmpsysbankuailiebiaoinfo;
   
	}
	/*
	 * 所有股票
	 */
	public ArrayList<Stock> getAllStocks() 
	{
		ArrayList<Stock> tmpsysbankuailiebiaoinfo = new ArrayList<Stock> ();

		String sqlquerystat = "SELECT 股票代码,股票名称,已退市,所属交易所 FROM A股"									
							   ;   
		logger.debug(sqlquerystat);
		CachedRowSetImpl rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);

	        while(rs.next()) {
	        	Stock tmpbk = null;
	        	if(!rs.getBoolean("已退市")) {
	        		tmpbk = new Stock (rs.getString("股票代码"),rs.getString("股票名称"));
	        		tmpbk.setSuoShuJiaoYiSuo(rs.getString("所属交易所"));
		        	tmpsysbankuailiebiaoinfo.add(tmpbk);
	        	} else {
	        		logger.debug(rs.getString("股票代码") + "已经退市");
	        	}
	        	
	        }
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
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
	    
	    return tmpsysbankuailiebiaoinfo;
	}
	/*
	 * 找出通达信定义的所有板块.不包括交易所的指数板块
	 */
	private HashSet<String> getTDXBanKuaiSet(String jys)
	{
		HashSet<String> tmpsysbankuailiebiaoinfo = new HashSet<String>();
		
		String sqlquerystat;
		if(!jys.toLowerCase().equals("all"))
			 sqlquerystat = "SELECT 板块ID,板块名称,指数所属交易所,板块类型描述  FROM 通达信板块列表 	WHERE 指数所属交易所 = '" + jys +"' "  ;
		else
			 sqlquerystat = "SELECT 板块ID,板块名称,指数所属交易所,板块类型描述 FROM 通达信板块列表 	"  ;
		logger.debug(sqlquerystat);
		CachedRowSetImpl rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	        rs.last();  
	        int rows = rs.getRow();  
//	        data = new Object[rows][];    
//	        int columnCount = 3;//列数  
	        rs.first();  
	        //int k = 0;  
	        //while(rs.next())
	        for(int j=0;j<rows;j++) {  
	        	String bkcode = rs.getString("板块ID");
	        	tmpsysbankuailiebiaoinfo.add(bkcode);
	        	
	            rs.next();
	        }
	        
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
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
	    
	    return tmpsysbankuailiebiaoinfo;
	}
	public ArrayList<BanKuai> getTDXBanKuaiList(String jys)   
	{
		ArrayList<BanKuai> tmpsysbankuailiebiaoinfo = new ArrayList<BanKuai> ();
		
		String sqlquerystat;
		if(!jys.toLowerCase().equals("all"))
			 sqlquerystat = "SELECT 板块ID,板块名称,指数所属交易所,板块类型描述,导出Gephi,导入交易数据,板块分析   FROM 通达信板块列表 	WHERE 指数所属交易所 = '" + jys +"' "  ;
		else
			 sqlquerystat = "SELECT 板块ID,板块名称,指数所属交易所,板块类型描述,导出Gephi,导入交易数据,板块分析   FROM 通达信板块列表 	"  ;
		logger.debug(sqlquerystat);
		CachedRowSetImpl rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	        rs.last();  
	        int rows = rs.getRow();  
//	        data = new Object[rows][];    
//	        int columnCount = 3;//列数  
	        rs.first();  
	        //int k = 0;  
	        //while(rs.next())
	        for(int j=0;j<rows;j++) {  
	        	BanKuai tmpbk = new BanKuai (rs.getString("板块ID"),rs.getString("板块名称") );
	        	tmpbk.setSuoShuJiaoYiSuo(rs.getString("指数所属交易所"));
	        	tmpbk.setBanKuaiLeiXing( rs.getString("板块类型描述") );
	        	
	        	tmpbk.setExporttogehpi(rs.getBoolean("导出Gephi"));
	        	tmpbk.setImportdailytradingdata(rs.getBoolean("导入交易数据"));
	        	tmpbk.setShowinbkfxgui(rs.getBoolean("板块分析"));
	        	
	        	tmpsysbankuailiebiaoinfo.add(tmpbk);
	        	
	            rs.next();
	        }
	        
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
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
	    
	    return tmpsysbankuailiebiaoinfo;
	}
	/*
	 * 找出某通达信板块的所有子板块，为板块产业链
	 */
	public HashMap<String,String> getSubBanKuai(String tdxbk) 
	{
		String sqlquerystat = "SELECT 产业链子板块列表.`子板块代码`, 产业链子板块列表.`子板块名称`" + 
				" FROM 产业链子板块列表" +
				" INNER JOIN 产业链板块子板块对应表" +
				" ON 产业链子板块列表.`子板块代码` = 产业链板块子板块对应表.`子板块代码`" +
				" WHERE 产业链板块子板块对应表.`通达信板块代码`  = '" + tdxbk + "' " +
				" ORDER BY 产业链子板块列表.`子板块代码`" 
				; 
		logger.debug(sqlquerystat);
		HashMap<String,String> tmpsubbklist = new HashMap<String,String> ();
		CachedRowSetImpl rs = null;
		try {  
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			rs.last();  
			int rows = rs.getRow();  
			rs.first();  
			for(int j=0;j<rows;j++) {  
				String tmpbkcode = rs.getString("子板块代码");
				String tmpbkname = rs.getString("子板块名称");
				tmpsubbklist.put(tmpbkcode,tmpbkname);
				rs.next();
			}
		
		}catch(java.lang.NullPointerException e){ 
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
	    	if(rs != null)
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }  
		return tmpsubbklist;
	}
	
	/*
	 * 当前只能加不能删除
	 */
	public String addNewSubBanKuai(String tdxbk,String newsubbk) 
	{
		HashMap<String, String> cursubbks = this.getSubBanKuai(tdxbk);
		int cursize = cursubbks.size();
		if(cursubbks.containsValue(newsubbk)) 
			return null;
		
		String subcylcode;
		if(cursize+1 >= 10)
			subcylcode = tdxbk + String.valueOf(cursize+1);
		else
			subcylcode = tdxbk + "0" + String.valueOf(cursize+1);
		
		
		String sqlinsertstat = "INSERT INTO  产业链子板块列表(子板块名称,子板块代码) values ("
								+ "'" + newsubbk.trim() + "'" + ","
								+ "'" + subcylcode.trim() + "'"
								+ ")"
								;
		logger.debug(sqlinsertstat);
		int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		
		sqlinsertstat = "INSERT INTO  产业链板块子板块对应表(通达信板块代码,子板块代码) values ("
				+ "'" + tdxbk.trim() + "'" + ","
				+ "'" + subcylcode.trim() + "'"
				+ ")"
				;
		logger.debug(sqlinsertstat);
		autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);


		cursubbks = null;
		return subcylcode;
		
	}
	/*
	 * 
	 */
	public void setNewZdyBanKuai(String bkname, String bkcode) 
	{
		String dbbkname =  "zdy"+bkname.trim();
		String sqlinsertstat = "INSERT INTO  自定义板块列表(板块名称,创建时间,板块ID) values ("
				+ "'" + dbbkname + "'" + ","
				+  "#" + CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now()) + "#" + ","
				+ "'" + bkcode.trim() + "'" 
				+ ")"
				;
		logger.debug(sqlinsertstat);
		int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		
//		BanKuai tmpbkai = new BanKuai (dbbkname);
//		tmpbkai.setBankuaiCode(bkcode);
//		//this.zdybankuailiebiaoinfo.put(dbbkname, tmpbkai);
	}
	
	public HashMap<String, BanKuai> getZdyBanKuaiList ()
	{
				//HashSet<String> tmpbankuailiebiaoset = new HashSet<String> ();
				HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();
				//Multimap<String,String> tmpsysbkmap =   ArrayListMultimap.create();
				
				String sqlquerystat = "SELECT 板块ID,板块名称,创建时间  FROM 通达信自定义板块列表 	ORDER BY 板块名称, 板块ID, 创建时间";   
				logger.debug(sqlquerystat);
				CachedRowSetImpl  rs = null;  
			    try {  
			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			        rs.last();  
			        int rows = rs.getRow();  
//			        data = new Object[rows][];    
//			        int columnCount = 3;//列数  
			        rs.first();  
			        //int k = 0;  
			        //while(rs.next())
			        for(int j=0;j<rows;j++) {  
			            //Object[] row = new Object[columnCount]; 
			        	BanKuai tmpbk = new BanKuai (rs.getString("板块ID"),rs.getString("板块名称") );
			        	//tmpbk.setBankuaicreatedtime(rs.getDate("创建时间"));
		          	  
			        	//tmpbankuailiebiaoset.add(rs.getString("板块名称"));
			        	tmpsysbankuailiebiaoinfo.put(rs.getString("板块名称"),tmpbk); 
			           
			            rs.next();
			        }
			        
			    }catch(java.lang.NullPointerException e){ 
			    	e.printStackTrace();
			    } catch (SQLException e) {
			    	e.printStackTrace();
			    }catch(Exception e){
			    	e.printStackTrace();
			    }finally {
			    	if(rs != null)
						try {
							rs.close();
							rs = null;
						} catch (SQLException e) {
							e.printStackTrace();
						}
			    }  

			    return tmpsysbankuailiebiaoinfo;
	}
	
	/*
	 * 检查数据库中的通达信板块哪些有记录文件，哪些没有。 
	 */
	public File preCheckTDXBanKuaiVolAmoToDb (String cys)
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
		
		HashSet<String> allsysbk = this.getTDXBanKuaiSet (cys.toLowerCase());
		ArrayList<String> allbkcode = new ArrayList<String>(allsysbk );
		int found =0 ; int notfound = 0;
		try {
			Files.append("系统共有"+ allbkcode.size() + "个板块！"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		
			for(String tmpbkcode:allbkcode) {
//				String bkcys = allsysbk.get(tmpbkcode).getSuoShuJiaoYiSuo().trim().toUpperCase();
				String bkfilename = (filenamerule.replaceAll("YY",cys.toUpperCase())).replaceAll("XXXXXX", tmpbkcode);
				
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
		
		return tmprecordfile;
	}
	/*
	 * 在用户导入成交量后，要更新板块的类型，// 通达信里面定义的板块有几种：1.有个股自身有成交量数据 2. 有个股自身无成交量数据 3.无个股自身有成交量数据
	 */
	public void refreshTDXSystemBanKuaiLeiXing () 
	{
		 ArrayList<BanKuai> curallbk = this.getTDXBanKuaiList("all");

		for ( BanKuai bankuai : curallbk) {

		    String bkcode = bankuai.getMyOwnCode();
		    String bkcys = bankuai.getSuoShuJiaoYiSuo();
		    
		    String cjltablename;
			if(bkcys.toLowerCase().equals("sh"))
				cjltablename = "通达信板块每日交易信息";
			else
				cjltablename = "通达信交易所指数每日交易信息";
			
		    String sqlquerystat = "SELECT COUNT(*) AS RESULT FROM " + cjltablename + " WHERE  代码 = " 
								+ "'"  + bkcode + "'" 
								;
		    logger.debug(sqlquerystat);
		    CachedRowSetImpl  rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		    int dy = 0;
		    try {
				while(rs.next()) {
					 dy = rs.getInt("RESULT");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		    
		    Boolean bkhacjl = false;
		    if(dy>0)
		    	bkhacjl = true;
		    
		    HashMap<String, String> actiontables = this.getActionRelatedTables(bankuai,null);
		    String bktypetable = actiontables.get("股票板块对应表");
		    Boolean bkhasgegu = false;
			if(bktypetable != null)
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
			
			if(bktype != null) {
				String sqlupdatequery = "UPDATE 通达信板块列表 SET 板块类型描述 = " + "'" + bktype +"'" + " WHERE 板块ID = '" + bkcode + "'";
				logger.debug(sqlupdatequery);
			    connectdb.sqlUpdateStatExecute(sqlupdatequery);
			}
			if(bktype.equals(BanKuai.NOGGWITHSELFCJL)  ||  bktype.equals(BanKuai.NOGGNOSELFCJL)) { //没有个股的板块肯定不导出到gephi
				String sqlupdatequery = "UPDATE 通达信板块列表 SET 导出Gephi = false WHERE 板块ID = '" + bkcode + "'";
				logger.debug(sqlupdatequery);
			    connectdb.sqlUpdateStatExecute(sqlupdatequery);
			}
			if(  bktype.equals(BanKuai.NOGGNOSELFCJL)) { //没有个股没有成交量的板块肯定不做板块分析
				String sqlupdatequery = "UPDATE 通达信板块列表 SET 板块分析 = false WHERE 板块ID = '" + bkcode + "'";
				logger.debug(sqlupdatequery);
			    connectdb.sqlUpdateStatExecute(sqlupdatequery);
			}
				

		}
		
		curallbk = null;
	}
	/*
	 * 同步通达信上证板块的每日成交量信息，信息存在"通达信板块每日交易信息"，
	 */
	public File refreshTDXBanKuaiVolAmoToDb (String cys)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步通达信上海板块成交量报告.tmp");
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		String cjltablename;
		if(cys.toLowerCase().equals("sh"))
			cjltablename = "通达信板块每日交易信息";
		else
			cjltablename = "通达信交易所指数每日交易信息";
		
//		 HashSet<String> allbkcode = this.getTDXBanKuaiSet (cys.toLowerCase());
		 ArrayList<BanKuai> allbklist = this.getTDXBanKuaiList(cys.toLowerCase()); //这样可以判断哪些板块无需导入每日数据
//		for(String tmpbkcode:allbkcode) {
		for(BanKuai bk :  allbklist ) {
//			String bkcys = allsysbk.get(tmpbkcode).getSuoShuJiaoYiSuo().trim().toUpperCase();
			if(!bk.isImportdailytradingdata())
				continue;
			
			String tmpbkcode = bk.getMyOwnCode();
			String bkfilename = (filenamerule.replaceAll("YY",cys.toUpperCase())).replaceAll("XXXXXX", tmpbkcode);
			File tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {  
			    continue; 
			} 

				CachedRowSetImpl  rs = null;
				LocalDate ldlastestdbrecordsdate = null;
				try { 				
					String sqlquerystat = "SELECT  MAX(交易日期) 	MOST_RECENT_TIME"
							+ " FROM " + cjltablename + "  WHERE  代码 = " 
   							+ "'"  + tmpbkcode + "'" 
   							;
					logger.debug(sqlquerystat);
   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   			    	while(rs.next()) {
//   			    		logger.debug(rs.getMetaData().getColumnCount());
   			    		java.sql.Date lastestdbrecordsdate = rs.getDate("MOST_RECENT_TIME"); //mOST_RECENT_TIME
   			    		try {
   			    			ldlastestdbrecordsdate = lastestdbrecordsdate.toLocalDate();
   			    		} catch (java.lang.NullPointerException e) {
   			    			logger.debug(tmpbkcode + "没有记录");
   			    		}
//	   			    	Instant instant = Instant.ofEpochMilli(lastestdbrecordsdate.getTime());
//	   			    	ldlastestdbrecordsdate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()) .toLocalDate();
//   			    		ldlastestdbrecordsdate = 	(lastestdbrecordsdate.toInstant()).atZone(ZoneId.systemDefault()).toLocalDate();
   			    	}
   			    } catch(java.lang.NullPointerException e) { 
   			    	e.printStackTrace();
   			    } catch (SQLException e) {
   			    	e.printStackTrace();
   			    }catch(Exception e){
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
				
				setVolAmoRecordsFromFileToDatabase(tmpbkcode,tmpbkfile,ldlastestdbrecordsdate,cjltablename,dateRule,tmprecordfile);
		}
		
		allbklist = null;
//		allbkcode = null;
		
		return tmprecordfile;
	}
	/*
	 * 
	 */
	private List<String> getTDXVolFilesRule ()
	{
		List<String> volamooutput = sysconfig.getTDXVOLFilesPath();
		String exportath = sysconfig.toUNIXpath(Splitter.on('=').trimResults().omitEmptyStrings().splitToList(volamooutput.get(0) ).get(1) );
		String filenamerule = Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(1)).get(1);
		String dateRule = getTDXDateExportDateRule(Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(2)).get(1));
		
		List<String> tdxVolFileRule = new ArrayList<String> ();
		tdxVolFileRule.add(exportath);
		tdxVolFileRule.add(filenamerule);
		tdxVolFileRule.add(dateRule);
		
		return tdxVolFileRule;
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
	private void setVolAmoRecordsFromFileToDatabase (String tmpbkcode, File tmpbkfile, LocalDate lastestdbrecordsdate, String inserttablename, String datarule,File tmprecordfile)
	{
		 
		Charset charset = sysconfig.charSet();
		if(lastestdbrecordsdate == null) //null说明数据库里面还没有相关的数据，把时间设置为1900年把文件中所有数据都导入
			try {
		        lastestdbrecordsdate = LocalDate.now().minus(100,ChronoUnit.YEARS);
				Files.append(tmpbkcode + "板块成交量记录将导入所有记录！"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
		
		RandomAccessFile rf = null;
        try {  
            rf = new RandomAccessFile(tmpbkfile, "r");  
            long len = rf.length();  
            long start = rf.getFilePointer();  
            long nextend = start + len - 1;  
            String line;  
            rf.seek(nextend);  
            int c = -1;  
            boolean finalneededsavelinefound = false;
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
                    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datarule);
                    			curlinedate =  LocalDate.parse(beforparsedate,formatter) ;
                    			
                    			LocalTime tdytime = LocalTime.now(); //如果是在交易时间导入数据的话，当天的数据还没有，所以要判断一下
            				    LocalDate tdydate = LocalDate.now(); 
            					if( ( tdytime.compareTo(LocalTime.of(9, 0, 0)) >0 && tdytime.compareTo(LocalTime.of(18, 0, 0)) <0) ) {
            						 if(curlinedate.equals(tdydate)) {
            							 curlinedate = null;
            							 continue;
            						 }
            					}
            					
                    			if( curlinedate.isAfter(lastestdbrecordsdate)) {
                        			String sqlinsertstat = "INSERT INTO " + inserttablename +"(代码,交易日期,开盘价,最高价,最低价,收盘价,成交量,成交额) values ("
                    						+ "'" + tmpbkcode + "'" + ","
                    						+ "'" +  curlinedate + "'" + ","
                    						+ "'" +  tmplinelist.get(1) + "'" + "," 
                    						+ "'" +  tmplinelist.get(2) + "'" + "," 
                    						+ "'" +  tmplinelist.get(3) + "'" + "," 
                    						+ "'" +  tmplinelist.get(4) + "'" + "," 
                    						+ "'" +  tmplinelist.get(5) + "'" + "," 
                    						+ "'" +  tmplinelist.get(6) + "'"  
                    						+ ")"
                    						;
                        			logger.debug(sqlinsertstat);
                    				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
                    				lineimportcount ++;
                        		} else if(curlinedate.compareTo(lastestdbrecordsdate) == 0) {
                        			finalneededsavelinefound = true;
                        			Files.append(tmpbkcode + "指数成交量记录导入起始时间为:" + line + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        			Files.append(tmpbkcode + "共导入:" + lineimportcount + "个记录" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        		}
                    		} catch (Exception e) {
//                    			e.printStackTrace();
                    			logger.debug("不是有日期的数据行");
                    		}
                        }
                    } //else {  
//                        logger.debug(line);  
//                  }  
                    nextend--;  
                }  
                nextend--;  
                rf.seek(nextend);  
                if (nextend == 0) {// 当文件指针退至文件开始处，输出第一行   文件第一行是文件抬头，不需要处理
                    //logger.debug(new String(rf.readLine().getBytes("ISO-8859-1"), charset ));  
                }  
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                if (rf != null)  
                    rf.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
	}
/*
 * 
 */
	public Date getTDXRelatedTableLastModfiedTime() 
	{
		Date lastesttdxtablesmdftime = null;
		
		HashMap<String,String> sqlstatmap = new HashMap<String,String> (); 
		String sqlquerystat = " SELECT MAX(创建时间) FROM 通达信板块列表" 
						   ;
		sqlstatmap.put("mysql", sqlquerystat);
			
			CachedRowSetImpl rs = null; 
		    try {  
		    	 rs = connectdb.sqlQueryStatExecute(sqlstatmap);
		    	 
		    	 java.sql.Timestamp tmplastmdftime = null;
		    	 while(rs.next())
		    		  tmplastmdftime = rs.getTimestamp("MAX(创建时间)");
		    	 logger.debug(tmplastmdftime);
		    	 lastesttdxtablesmdftime = tmplastmdftime;

		    }catch(java.lang.NullPointerException e){
		    	lastesttdxtablesmdftime = null;
		    	e.printStackTrace();
		    } catch (SQLException e) {
		    	logger.debug(sqlquerystat);
		    	lastesttdxtablesmdftime = null;
		    	e.printStackTrace();
		    }catch(Exception e){
		    	lastesttdxtablesmdftime = null;
		    	e.printStackTrace();
		    }  finally {
		    	if(rs != null)
					try {
						rs.close();
						rs = null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
		    }
			
		return lastesttdxtablesmdftime;
	}
	
	private boolean getTDXRelatedTablesStatus()
	{
		boolean tableunderupdated = false;
		// show OPEN TABLES where In_use > 0;
		//SHOW OPEN TABLES WHERE `Table` LIKE '%[TABLE_NAME]%' AND `Database` LIKE '[DBNAME]' AND In_use > 0;
		HashMap<String,String> sqlstatmap = new HashMap<String,String> (); 
//		ArrayList<String> rmtservertable = new ArrayList<String>();
//		rmtservertable.add("通达信交易所指数列表");
//		rmtservertable.add("通达信板块列表");
		List<String> rmtservertable = Lists.newArrayList("通达信板块列表");
		
		
			String sqlquerystat =  " show OPEN TABLES where In_use > 0" ;
			//sqlquerystat = "SHOW OPEN TABLES WHERE `Table` LIKE '%[a股]%' AND `Database` LIKE '[stockinfomanagementtest]' AND In_use > 0";		   
			logger.debug(sqlquerystat);
			sqlstatmap.put("mysql", sqlquerystat);
			
			CachedRowSetImpl rs = null; 
		    try {  
		    	 rs = connectdb.sqlQueryStatExecute(sqlstatmap);
		    	 
		    	 while(rs.next()) {
		    		 String tmptablename = rs.getString("Table");
		    		 String tmpinuse = rs.getString("In_use");
		    		 if(rmtservertable.contains(tmptablename) && tmpinuse.equals("1"))
		    			 tableunderupdated = true; 
		    		 
		    	 }
		    		  
		    }catch(java.lang.NullPointerException e){ 
		    	e.printStackTrace();
		    } catch (SQLException e) {
		    	e.printStackTrace();
		    }catch(Exception e){
		    	e.printStackTrace();
		    }  finally {
		    	if(rs != null)
					try {
						rs.close();
						rs = null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
		    }
			
		
		
		return tableunderupdated;
	}
	/*
	 * 判断板块是什么类型的板块，概念/风格/指数/行业/地域
	 */
	private HashMap<String,String> getActionRelatedTables (BanKuai bankuai,String stockcode)
	{
		String bkcode = bankuai.getMyOwnCode();
		HashMap<String,String> actiontables = new HashMap<String,String> ();
		if(!Strings.isNullOrEmpty(bkcode)) {
			String stockvsbktable =null;
				if(stockvsbktable == null) {	
					CachedRowSetImpl rs = null;
					int dy = -1;
					try {  
						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM 股票通达信概念板块对应表  WHERE 板块代码='" + bkcode + "'";
//						logger.debug(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							 dy = rs.getInt("RESULT");
	//						 logger.debug(dy);
						 }
					}catch(java.lang.NullPointerException e){ 
						logger.debug( "数据库连接为NULL!");
					} catch (SQLException e) {
						e.printStackTrace();
					}catch(Exception e){
						e.printStackTrace();
					} finally {
						try {
							if(rs != null)
								rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						rs = null;
						
						if(dy>0)
							stockvsbktable =  "股票通达信概念板块对应表";
					}
				}
				if(stockvsbktable == null) {
					CachedRowSetImpl rs = null;
					int dy = -1;
					try {
						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM 股票通达信风格板块对应表 WHERE 板块代码='" + bkcode + "'";
//						logger.debug(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							  dy = rs.getInt("RESULT");
//							 logger.debug(dy1);
						 }
					}catch(java.lang.NullPointerException e){ 
						logger.debug( "数据库连接为NULL!");
					} catch (SQLException e) {
						e.printStackTrace();
					}catch(Exception e){
						e.printStackTrace();
					} finally {
						try {
							if(rs != null)
								rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						rs = null;
						
						if(dy>0)
							stockvsbktable =  "股票通达信风格板块对应表";
					}
				}
				
				if(stockvsbktable == null) {
					CachedRowSetImpl rs = null;
					int dy = -1;
					try {
						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM 股票通达信行业板块对应表  WHERE 板块代码='" + bkcode + "'";
//						logger.debug(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							 dy = rs.getInt("RESULT");
//							 logger.debug(dy);
						 }
					}catch(java.lang.NullPointerException e){ 
						logger.debug( "数据库连接为NULL!");
					} catch (SQLException e) {
						e.printStackTrace();
					}catch(Exception e){
						e.printStackTrace();
					} finally {
						try {
							if(rs != null)
								rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						rs = null;
						
						if(dy>0)
							stockvsbktable = "股票通达信行业板块对应表";
					}
				}
				
				if(stockvsbktable == null) {
					CachedRowSetImpl rs = null;
					int dy = -1;
					try {
						String sqlquerystat = "SELECT COUNT(*) AS RESULT FROM 股票通达信交易所指数对应表  WHERE 板块代码='" + bkcode + "'";
//						logger.debug(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							 dy = rs.getInt("RESULT");
//							 logger.debug(dy);
						 }
					}catch(java.lang.NullPointerException e) {
						e.printStackTrace();
						logger.debug( "数据库连接为NULL!");
					} catch (SQLException e) {
						e.printStackTrace();
					}catch(Exception e){
						e.printStackTrace();
					} finally {
						try {
							if(rs != null)
								rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						rs = null;
						
						if(dy>0)
							stockvsbktable = "股票通达信交易所指数对应表";
					}
				}
				if(stockvsbktable == null) {
					try {
						CachedRowSetImpl rsdy = null;
						int dy = -1;
						try {
							String diyusqlquerystat = "SELECT 对应TDXSWID  FROM 通达信板块列表 WHERE 板块id = '" + bkcode + "'" ;
//							logger.debug(diyusqlquerystat);
							 rsdy = connectdb.sqlQueryStatExecute(diyusqlquerystat);
							 while(rsdy.next()) {
								 dy = rsdy.getInt("对应TDXSWID");
//								 logger.debug(dy);
							 }
						}catch(java.lang.NullPointerException e){ 
							logger.debug( "数据库连接为NULL!");
						} catch (SQLException e) {
//							e.printStackTrace();
							logger.debug("java.sql.SQLException: 对列 1 中的值 (芯片) 执行 getInt 失败，不是地域板块。");
						}catch(Exception e){
							e.printStackTrace();
						} finally {
							try {
								if(rsdy != null)
									rsdy.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							rsdy = null;
							if(dy>=1 && dy <=32) {
								stockvsbktable =  "股票通达信基本面信息对应表";
								actiontables.put("地域代码", String.valueOf(dy) );
							}
						}
					} catch (java.lang.NullPointerException e1) {
						
					}
				}
				
				if(stockvsbktable != null)
					actiontables.put("股票板块对应表", stockvsbktable);
				
				
				String bkcjltable = null;
				if(bankuai.getSuoShuJiaoYiSuo().toLowerCase().equals("sh"))
					bkcjltable = "通达信板块每日交易信息";
				else 
					bkcjltable = "通达信交易所指数每日交易信息";
				actiontables.put("板块每日交易量表", bkcjltable);
		}
		
		if(!Strings.isNullOrEmpty(stockcode)) {
				String gegucjltable;
				// TODO Auto-generated method stub
				if(stockcode.startsWith("6"))
					gegucjltable = "通达信上交所股票每日交易信息";
				else
					gegucjltable = "通达信深交所股票每日交易信息";
				actiontables.put("股票每日交易量表", gegucjltable);
		}
		
		return actiontables;
	}
	/*
	 * 找出某个时间点 行业/概念/风格/指数 通达信某个板块的所有个股及权重,不找成交额
	 */
	public BanKuai getTDXBanKuaiGeGuOfHyGnFg(BanKuai currentbk,LocalDate selecteddatestart , LocalDate selecteddateend, BkChanYeLianTree treeallstocks)
	{
		String currentbkcode = currentbk.getMyOwnCode();
		
		HashMap<String, String> actiontables = this.getActionRelatedTables(currentbk,null);
//		logger.debug(actiontables);
		String bktypetable = actiontables.get("股票板块对应表");
		String bknametable = actiontables.get("板块指数名称表");
		if(bktypetable == null) {
			return currentbk;
		}
		
		String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart).trim();
		String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend).trim();

		ArrayList<Stock> gegumap = new ArrayList<Stock> ();
		
		CachedRowSetImpl rs1 = null;
		try { 
			String sqlquerystat1 = "";
			if(bktypetable.trim().equals("股票通达信基本面信息对应表") ) { //找地域板块
				String dycode = actiontables.get("地域代码");
				sqlquerystat1 = "SELECT a股.`股票代码` , a股.`股票名称` ,10 as '股票权重' \r\n" + 
				 		"FROM 股票通达信基本面信息对应表, a股\r\n" + 
				 		"WHERE 股票通达信基本面信息对应表.`地域DY` = " + dycode + "\r\n" + 
				 		"AND 股票通达信基本面信息对应表.`股票代码GPDM` = a股.`股票代码`"
				 		;
			 } else { //概念风格行业指数板块
				 //from WQW
				 sqlquerystat1 = "select "+ bktypetable + ".`股票代码` , a股.`股票名称`, "+ bktypetable + ".`板块代码` , "+ bktypetable + ".`股票权重`\r\n" + 
					 		"          from "+ bktypetable + ", a股\r\n" + 
					 		"          where "+ bktypetable + ".`股票代码`  = a股.`股票代码`  AND a股.`已退市` IS NULL \r\n" + 
					 		
					 		"and (  (  Date("+ bktypetable + ".`加入时间`) between '" +  formatedstartdate + "'  and '" +  formatedenddate + "')\r\n" + 
					 		"           		 or( ifnull("+ bktypetable + ".`移除时间`, '2099-12-31') between '" +  formatedstartdate + "'  and '" +  formatedenddate + "')\r\n" + 
					 		"           		 or( Date("+ bktypetable + ".`加入时间`) <= '" +  formatedstartdate + "' and ifnull(" + bktypetable + ".`移除时间`, '2099-12-31') >= '" +  formatedenddate + "' )\r\n" + 
					 		"			  )" +
//					 		"           and '" +  formatedstartdate + "' >= Date("+ bktypetable + ".`加入时间`)\r\n" + 
//					 		"           and '" +  formatedenddate + "' <  ifnull("+ bktypetable + ".`移除时间`, '2099-12-31')\r\n" + 
					 		
					 		
					 		"           and "+ bktypetable + ".`板块代码` =  '" + currentbkcode + "'\r\n" + 
					 		"         \r\n" + 
					 		"         group by "+ bktypetable + ".`股票代码`, "+ bktypetable + ".`板块代码`\r\n" + 
					 		"         order by "+ bktypetable + ".`股票代码`, "+ bktypetable + ".`板块代码` ";
			 }
//			 logger.debug(sqlquerystat1);
			 logger.debug("给板块" + currentbk.getMyOwnCode() + currentbk.getMyOwnName() + "寻找从" + formatedstartdate.toString() + "到" + formatedenddate.toString() + "时间段内的个股！");
			 rs1 = connectdb.sqlQueryStatExecute(sqlquerystat1);
			 
			 while(rs1.next()) {  
				String tmpstockcode = rs1.getString("股票代码");
				
				String tmpstockname;
				try {
					tmpstockname = rs1.getString("股票名称"); //"股票名称"
				} catch (java.lang.NullPointerException e ) {
					tmpstockname = "";
				}
				
				Stock tmpstock = (Stock)treeallstocks.getSpecificNodeByHypyOrCode (tmpstockcode,BanKuaiAndStockBasic.TDXGG);
				if(tmpstock != null) {
					StockOfBanKuai bkofst = new StockOfBanKuai(currentbk,tmpstock);
					Integer weight = rs1.getInt("股票权重");
					bkofst.setStockQuanZhong(weight);
					
					currentbk.addNewBanKuaiGeGu(bkofst);
				}
			}
				
			}catch(java.lang.NullPointerException e){ 
				e.printStackTrace();
//				logger.debug( "数据库连接为NULL!");
			} catch (SQLException e) {
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			} finally {
				try {
					if(rs1 != null)
						rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				rs1 = null;
			}
		
		return currentbk;
	}
	/*
	 * 板块设定时间跨度内和大盘相比的按周期占比。
	 */
	public  BanKuai getBanKuaiZhanBi (BanKuai bankuai,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{//本函数初始是开发为周的占比，所以日/月线的占比掉用其他函数
		if(period.equals(StockGivenPeriodDataItem.DAY)) //调用日线查询函数
			; 
		else if(period.equals(StockGivenPeriodDataItem.MONTH)) //调用月线查询函数
			;
		
		NodeXPeriodDataBasic nodewkperioddata = bankuai.getNodeXPeroidData(period);
		
		String bkcode = bankuai.getMyOwnCode();
		String bkcjltable;
		String bkcys = bankuai.getSuoShuJiaoYiSuo();
		if(bkcys.toLowerCase().equals("sh"))
			bkcjltable = "通达信板块每日交易信息";
		else
			bkcjltable = "通达信交易所指数每日交易信息";
		
		String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart);
		String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend);
		
		//包含成交量和成交额的SQL
		String sqlquerystat = "SELECT YEAR(t.workday) AS CALYEAR, WEEK(t.workday) AS CALWEEK, M.BKCODE AS BKCODE, t.EndOfWeekDate AS EndOfWeekDate," +
				 "M.板块周交易额 as 板块周交易额, SUM(T.AMO) AS 大盘周交易额 ,  M.板块周交易额/SUM(T.AMO) AS 占比, \r\n" +

				"M.板块周交易量 as 板块周交易量, SUM(T.VOL) AS 大盘周交易量 ,  M.板块周交易量/SUM(T.VOL) AS VOL占比 \r\n" + 
				 
				"FROM\r\n" + 
				"(\r\n" + 
				"select  通达信板块每日交易信息.`交易日期` as workday, DATE(通达信板块每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信板块每日交易信息.交易日期)) DAY) as EndOfWeekDate, \r\n" + 
				"		  sum(通达信板块每日交易信息.`成交额`) AS AMO , sum(通达信板块每日交易信息.`成交量`) AS VOL \r\n" + 
				"from 通达信板块每日交易信息\r\n" + 
				"where 代码 = '999999'\r\n" + 
				"group by year(通达信板块每日交易信息.`交易日期`),week(通达信板块每日交易信息.`交易日期`)\r\n" + 
				"\r\n" + 
				"UNION ALL\r\n" + 
				"\r\n" + 
				"select  通达信交易所指数每日交易信息.`交易日期` as workday,   DATE(通达信交易所指数每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信交易所指数每日交易信息.交易日期)) DAY) as EndOfWeekDate, \r\n" + 
				"			sum(通达信交易所指数每日交易信息.`成交额`) AS AMO, sum(通达信交易所指数每日交易信息.`成交量`) AS VOL \r\n" + 
				"from 通达信交易所指数每日交易信息\r\n" + 
				"where 代码 = '399001'\r\n" + 
				"group by year(通达信交易所指数每日交易信息.`交易日期`),week(通达信交易所指数每日交易信息.`交易日期`)\r\n" + 
				") T,\r\n" + 
				"\r\n" + 
				"(select " + bkcjltable + ".`代码` AS BKCODE, " + bkcjltable + ".`交易日期` as workday,  "
						+ "sum( " + bkcjltable + ".`成交额`) AS 板块周交易额 , sum( " + bkcjltable + ".`成交量`) AS 板块周交易量 from " + bkcjltable + "\r\n" +  
				"where 代码 = '" + bkcode + "'\r\n" + 
				"GROUP BY YEAR( " + bkcjltable + ".`交易日期`), WEEK( " + bkcjltable +".`交易日期`)\r\n" + 
				") M\r\n" + 
				"WHERE YEAR(T.WORKDAY) = YEAR(M.WORKDAY) AND  WEEK(T.WORKDAY) = WEEK(M.WORKDAY)\r\n" + 
				"		AND T.WORKDAY BETWEEN'" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" +
				" GROUP BY year(t.workday),week(t.workday)"
				;
							
//		logger.debug(sqlquerystat);
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
			rsfx = connectdb.sqlQueryStatExecute(sqlquerystatfx);
			while(rsfx.next()) {
				java.sql.Date recdate = rsfx.getDate("日期");
				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(recdate);
				Integer reccount = rsfx.getInt("RESULT");
				nodewkperioddata.addFxjgToPeriod(wknum, reccount);
			}
			
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			while(rs.next()) {
				Double bankuaicje = rs.getDouble("板块周交易额");
				Double dapancje = rs.getDouble("大盘周交易额");
				java.sql.Date lastdayofweek = rs.getDate("EndOfWeekDate");
				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(lastdayofweek);
				Double bankuaicjl = rs.getDouble("板块周交易量");
				Double dapancjl = rs.getDouble("大盘周交易量");
				
				StockGivenPeriodDataItem bkperiodrecord = new StockGivenPeriodDataItem( bkcode, StockGivenPeriodDataItem.WEEK,
						wknum, 0.0, 0.0,  0.0,  0.0, bankuaicje, bankuaicjl,null,null,null);
				
				bkperiodrecord.setRecordsDayofEndofWeek(lastdayofweek);
				bkperiodrecord.setUpLevelChengJiaoEr(dapancje);
				bkperiodrecord.setUplevelchengjiaoliang(dapancjl);
				
				nodewkperioddata.addNewXPeriodData(bkperiodrecord);
				
				bkperiodrecord = null;
				bankuaicje = null;
				dapancje = null;
				lastdayofweek = null;
				wknum = null;
				bankuaicjl = null;
				dapancjl = null;


				
			}
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	try {
				rs.close();
				rsfx.close();
				rs = null;
				rsfx = null;
				bkcjltable = null;
//				System.gc();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

		return bankuai;
	}

	/*
	 * 股票对板块的按周占比
	 */
	public StockOfBanKuai getGeGuZhanBiOfBanKuai(BanKuai bkcode, StockOfBanKuai stock,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{//本函数初始是开发为周的占比，所以日/月线的占比掉用其他函数
		if(period.equals(StockGivenPeriodDataItem.DAY)) //调用日线查询函数
			; 
		else if(period.equals(StockGivenPeriodDataItem.MONTH)) //调用月线查询函数
			;
		
		NodeXPeriodDataBasic nodedayperioddata = bkcode.getStockXPeriodDataForABanKuai(stock.getMyOwnCode(),period);
		
		String stockcode = stock.getMyOwnCode();
		
		HashMap<String, String> actiontables = this.getActionRelatedTables(bkcode,stockcode);
		String stockvsbktable = actiontables.get("股票板块对应表");
		String bkcjetable = actiontables.get("板块每日交易量表");
		String gegucjetable = actiontables.get("股票每日交易量表");
		String bknametable = "通达信板块列表";
		
		String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart);
		String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend);
		
		String sqlquerystat = "";
		if(stockvsbktable.trim().equals("股票通达信基本面信息对应表") ) { //找地域板块
			String dycode = actiontables.get("地域代码");
			sqlquerystat = "";
			return stock;
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
				"		and " +  stockvsbktable + ".`板块代码` =  '"  + bkcode +"'\r\n" + 
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
				"		and " +  bkcjetable + ".`代码` =  '"  + bkcode + "'\r\n" + 
				"group by year(" +  bkcjetable + ".`交易日期`), week(" +  bkcjetable + ".`交易日期`)," +  bkcjetable + ".`代码`\r\n" + 
				"order by year(" +  bkcjetable + ".`交易日期`), week(" +  bkcjetable + ".`交易日期`), " +  bkcjetable + ".`代码`) Z \r\n" + 
				"         \r\n" + 
				"where Y.year = Z. year\r\n" + 
				"    	and Y.week = Z.week\r\n" + 
				"order by Y.year, Y.week"
				;
		 }
//		logger.debug(sqlquerystat);
		try{
			logger.debug("为个股" + stock.getMyOwnCode()  + "寻找从" + selecteddatestart.toString() + "到" + selecteddateend.toString() + "在" + bkcode + "的占比数据！");
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
				org.jfree.data.time.Week recordwk = new org.jfree.data.time.Week (lastdayofweek);
				
				StockGivenPeriodDataItem stokofbkrecord = new StockGivenPeriodDataItem( stockcode, StockGivenPeriodDataItem.WEEK, recordwk, 
						  0.0,  0.0,  0.0,  0.0, stcokcje, stcokcjl,null,null,null);
				
				stokofbkrecord.setRecordsDayofEndofWeek(lastdayofweek);
				stokofbkrecord.setUpLevelChengJiaoEr(bkcje);
				stokofbkrecord.setUplevelchengjiaoliang(bkcjl);
			
				nodedayperioddata.addNewXPeriodData(stokofbkrecord);
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
		
		return stock;

	}
	/*
	 * //上面是个股对板块的指定周期(默认是周线)，下面部分是为个股自身周线数据，用来计算个股对大盘的数据
	 */
	public Stock getStockZhanBi(Stock stock,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{	
		if(period.equals(StockGivenPeriodDataItem.DAY)) //调用日线查询函数
			; 
		else if(period.equals(StockGivenPeriodDataItem.MONTH)) //调用月线查询函数
			;
		
		StockNodeXPeriodData nodewkperioddata = (StockNodeXPeriodData)stock.getNodeXPeroidData(period);
	
		String stockcode = stock.getMyOwnCode();
		String bkcjltable;
		if(stockcode.startsWith("6"))
			bkcjltable = "通达信上交所股票每日交易信息";
		else
			bkcjltable = "通达信深交所股票每日交易信息";
		
		String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart);
		String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend);
		
		//包含成交量和成交额的SQL
		String sqlquerystat = "SELECT YEAR(t.workday) AS CALYEAR, WEEK(t.workday) AS CALWEEK, M.BKCODE AS BKCODE, t.EndOfWeekDate AS EndOfWeekDate," +
				 "M.板块周交易额 as 板块周交易额, SUM(T.AMO) AS 大盘周交易额 ,  M.板块周交易额/SUM(T.AMO) AS 占比, \r\n" +
				"M.板块周交易量 as 板块周交易量, SUM(T.VOL) AS 大盘周交易量 ,  M.板块周交易量/SUM(T.VOL) AS VOL占比, \r\n" +
				"M.板块周换手率 as 板块周换手率, M.总市值/M.JILUTIAOSHU as 周平均总市值, M.总流通市值/M.JILUTIAOSHU as 周平均流通市值, M.JILUTIAOSHU       \r\n" +
				 
				"FROM\r\n" + 
				"(\r\n" + 
				"select  通达信板块每日交易信息.`交易日期` as workday, DATE(通达信板块每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信板块每日交易信息.交易日期)) DAY) as EndOfWeekDate, \r\n" + 
				"		  sum(通达信板块每日交易信息.`成交额`) AS AMO , sum(通达信板块每日交易信息.`成交量`) AS VOL \r\n" + 
				"from 通达信板块每日交易信息\r\n" + 
				"where 代码 = '999999'\r\n" + 
				"group by year(通达信板块每日交易信息.`交易日期`),week(通达信板块每日交易信息.`交易日期`)\r\n" + 
				"\r\n" + 
				"UNION ALL\r\n" + 
				"\r\n" + 
				"select  通达信交易所指数每日交易信息.`交易日期` as workday,   DATE(通达信交易所指数每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信交易所指数每日交易信息.交易日期)) DAY) as EndOfWeekDate, \r\n" + 
				"			sum(通达信交易所指数每日交易信息.`成交额`) AS AMO, sum(通达信交易所指数每日交易信息.`成交量`) AS VOL \r\n" + 
				"from 通达信交易所指数每日交易信息\r\n" + 
				"where 代码 = '399001'\r\n" + 
				"group by year(通达信交易所指数每日交易信息.`交易日期`),week(通达信交易所指数每日交易信息.`交易日期`)\r\n" + 
				") T,\r\n" + 
				"\r\n" + 
				"(select " + bkcjltable + ".`代码` AS BKCODE, " + bkcjltable + ".`交易日期` as workday,  "
						+ " sum( " + bkcjltable + ".`成交额`) AS 板块周交易额 , \r\n"
						+ " sum( " + bkcjltable + ".`成交量`) AS 板块周交易量,  \r\n"
						+ " sum( " + bkcjltable + ".`换手率`) AS 板块周换手率 , \r\n"
						+ " sum( " + bkcjltable + ".`总市值`) AS 总市值 , \r\n"
						+ " sum( " + bkcjltable + ".`流通市值`) AS 总流通市值, \r\n"
						+ "  count(1) as JILUTIAOSHU \r\n"
						+ " from " + bkcjltable + "\r\n" +  
				"where 代码 = '" + stockcode + "'\r\n" + 
				"GROUP BY YEAR( " + bkcjltable + ".`交易日期`), WEEK( " + bkcjltable +".`交易日期`)\r\n" + 
				") M\r\n" + 
				"WHERE YEAR(T.WORKDAY) = YEAR(M.WORKDAY) AND  WEEK(T.WORKDAY) = WEEK(M.WORKDAY)\r\n" + 
				"		AND T.WORKDAY BETWEEN'" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" +
				" GROUP BY year(t.workday),week(t.workday)"
				;
							
	//	logger.debug(sqlquerystat); 
		try {
			logger.debug("为板块" + stock.getMyOwnCode() + stock.getMyOwnName() + "寻找从" + selecteddatestart.toString() + "到" + selecteddateend.toString() + "占比数据！");
		} catch (java.lang.NullPointerException e) {
			e.printStackTrace();
		}
		
		String sqlquerystatfx = "SELECT 操作记录重点关注.`日期`, COUNT(*) AS RESULT FROM 操作记录重点关注 \r\n" + 
				" WHERE 股票代码='" + stockcode + "'" + "\r\n" + 
				" AND 操作记录重点关注.`日期` between '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" +
				" GROUP BY YEAR(操作记录重点关注.`日期`), WEEK(操作记录重点关注.`日期`) "
				;
		
		CachedRowSetImpl rs = null;
		CachedRowSetImpl rsfx = null;
		Boolean hasfengxiresult = false;
		try {
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			rsfx = connectdb.sqlQueryStatExecute(sqlquerystatfx);
			while(rsfx.next()) {
				java.sql.Date recdate = rsfx.getDate("日期");
				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(recdate);
				Integer reccount = rsfx.getInt("RESULT");
				nodewkperioddata.addFxjgToPeriod(wknum, reccount);
			}
			 
			while(rs.next()) {
				double bankuaicje = rs.getDouble("板块周交易额");
				double dapancje = rs.getDouble("大盘周交易额");
				java.sql.Date lastdayofweek = rs.getDate("EndOfWeekDate");
				org.jfree.data.time.Week recordwk = new org.jfree.data.time.Week (lastdayofweek);
				double bankuaicjl = rs.getDouble("板块周交易量");
				double dapancjl = rs.getDouble("大盘周交易量");
				double huanshoulv = rs.getDouble("板块周换手率");
				double pingjunzongshizhi = rs.getDouble("周平均总市值");
				double pingjunliutongshizhi = rs.getDouble("周平均流通市值");

				StockGivenPeriodDataItem stokrecord = new StockGivenPeriodDataItem( stockcode, period, recordwk, 
						  0.0,  0.0,  0.0,  0.0, bankuaicje, bankuaicjl,huanshoulv,pingjunzongshizhi,pingjunliutongshizhi);
				
				stokrecord.setRecordsDayofEndofWeek(lastdayofweek);
				stokrecord.setUpLevelChengJiaoEr(dapancje);
				stokrecord.setUplevelchengjiaoliang(dapancjl);
				
				nodewkperioddata.addNewXPeriodData(stokrecord);
				
				stokrecord = null;
			}
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	try {
				rs.close();
				rsfx.close();
				rs = null;
				rsfx = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	
	    	bkcjltable = null;
	    	nodewkperioddata = null; 
	    }

		return stock;
	}
	
	/*
	 * 获取个股或板块某时间段的日线走势
	 */
	public Stock getNodeKXianZouShi(Stock stock, LocalDate nodestartday, LocalDate nodeendday, String period) 
	{//本函数初始是开发为日的K线数据，所以周/月线的占比掉用其他函数
		if(period.equals(StockGivenPeriodDataItem.WEEK)) //调用周线查询函数
			; 
		else if(period.equals(StockGivenPeriodDataItem.MONTH)) //调用月线查询函数
			;
		
		NodeXPeriodDataBasic nodedayperioddata = stock.getNodeXPeroidData(period);
		
		String nodecode = stock.getMyOwnCode();
		String jys = stock.getSuoShuJiaoYiSuo();
		String searchtable;
		if(jys.toLowerCase().equals("sh"))
			searchtable = "通达信上交所股票每日交易信息";
		else
			searchtable = "通达信深交所股票每日交易信息";
		
		String sqlquerystat = "SELECT *  FROM " + searchtable + " \r\n" + 
				"WHERE 代码='" + nodecode + "'" + "\r\n" + 
				"AND 交易日期  between'" + nodestartday + "' AND '" + nodeendday + "'"
				;
		
		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
		try {  
			 rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			 while(rsfx.next()) {
				 double open = rsfx.getDouble("开盘价");
				 double high = rsfx.getDouble("最高价");
				 double low = rsfx.getDouble("最低价");
				 double close = rsfx.getDouble("收盘价");
				 double cje = rsfx.getDouble("成交额");
				 double cjl = rsfx.getDouble("成交量");
				 java.sql.Date actiondate = rsfx.getDate("交易日期");
				 org.jfree.data.time.Day recordwk = new org.jfree.data.time.Day (actiondate);
				 
				 StockGivenPeriodDataItem stokofbkrecord = new StockGivenPeriodDataItem( nodecode, period, recordwk, 
						 open,  high,  low,  close, cje, cjl,null,null,null);
				 
				 stokofbkrecord.setRecordsDayofEndofWeek(actiondate);
				 nodedayperioddata.addNewXPeriodData(stokofbkrecord);
				 
				 stokofbkrecord = null;
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
				if(rsfx != null)
					rsfx.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rsfx = null;
			searchtable = null;
		}

		return stock;
	}
	
	/*
	 * 获取某个节点在指定周的所有关注分析等结果
	 */
//	public ArrayList<JiaRuJiHua> getZdgzFxjgForANodeOfGivenPeriod (String nodecode, LocalDate givenwk, String period)
//	{
//		String sqlquerystat = "SELECT *  FROM 操作记录重点关注 \r\n" + 
//				"WHERE 股票代码='" + nodecode + "'" + "\r\n" + 
//				"AND WEEK(操作记录重点关注.`日期`) = WEEK('" + givenwk + "')" 
//				;
//		logger.debug(sqlquerystat);
//		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
//		
//		ArrayList<JiaRuJiHua> jrjh = new ArrayList<JiaRuJiHua> ();
//		try {
//			while(rsfx.next()) {
//				String acttype = rsfx.getString("加入移出标志");
//				JiaRuJiHua guanzhu = new JiaRuJiHua(nodecode,acttype); 
//				
//				java.sql.Date  lastdayofweek = rsfx.getDate("日期");
//				LocalDate actiondate = lastdayofweek.toLocalDate(); 
//				guanzhu.setJiaRuDate (actiondate );
//				String shuoming = rsfx.getString("原因描述");
//				guanzhu.setiHuaShuoMing(shuoming);
//				jrjh.add(guanzhu);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  finally {
//			try {
//				if(rsfx != null)
//					rsfx.close();
//				rsfx.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			rsfx = null;
//		}
//		
//		if(jrjh.size() == 0)
//			jrjh = null;
//		return jrjh;
//	}
	
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
//		List<String> volamooutput = sysconfig.getTDXVOLFilesPath();
//		String exportath = sysconfig.toUNIXpath(Splitter.on('=').trimResults().omitEmptyStrings().splitToList(volamooutput.get(0) ).get(1) );
//		String filenamerule = Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(1)).get(1);
//		String dateRule = getTDXDateExportDateRule(Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(2)).get(1));
		
		ArrayList<String> allgegucode = new ArrayList<String>( );
		CachedRowSetImpl  rsdm = null;
		try { 	
			String sqlquerystat = "SELECT  股票代码  FROM A股  WHERE      ifnull(已退市,1 )  or 已退市 = false "
						;
			logger.debug(sqlquerystat);
		    	rsdm = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	while(rsdm.next()) {
		    		 String ggcode = rsdm.getString("股票代码"); //mOST_RECENT_TIME
		    		 if( (ggcode.startsWith("00") || ggcode.startsWith("30") ) && jiaoyisuo.toLowerCase().equals("sz")) //只存深市股票
		    			 allgegucode.add(ggcode);
		    		 else if( (ggcode.startsWith("6") ) && jiaoyisuo.toLowerCase().equals("sh") ) //只存沪市股票
		    			 allgegucode.add(ggcode);
		    	}
		    } catch(java.lang.NullPointerException e) { 
		    	e.printStackTrace();
		    } catch (SQLException e) {
		    	e.printStackTrace();
		    }catch(Exception e){
		    	e.printStackTrace();
		    } finally {
		    	if(rsdm != null)
				try {
					rsdm.close();
					rsdm = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    }
		
		String optTable = null;
		if(jiaoyisuo.toLowerCase().equals("sz")  )
			optTable = "通达信深交所股票每日交易信息";
		else if(jiaoyisuo.toLowerCase().equals("sh") )
			optTable = "通达信上交所股票每日交易信息";
		for(String tmpbkcode:allgegucode) {
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

				CachedRowSetImpl  rs = null;
				LocalDate ldlastestdbrecordsdate = null;
				try { 				
					String sqlquerystat = "SELECT  MAX(交易日期) 	MOST_RECENT_TIME"
							+ " FROM "+ optTable +  " WHERE  代码 = " 
   							+ "'"  + tmpbkcode + "'" 
   							;
					logger.debug(sqlquerystat);
   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   			    	while(rs.next()) {
//   			    		logger.debug(rs.getMetaData().getColumnCount());
   			    		 java.sql.Date lastestdbrecordsdate = rs.getDate("MOST_RECENT_TIME"); //mOST_RECENT_TIME 
   			    		 try {
   			    			 ldlastestdbrecordsdate =lastestdbrecordsdate.toLocalDate();
   			    		 } catch (java.lang.NullPointerException e) {
   			    			 logger.info(tmpbkcode + "似乎没有数据，请检查！");
   			    		 }
//   			    		 Instant instant = Instant.ofEpochMilli(lastestdbrecordsdate.getTime());
//   			    		ldlastestdbrecordsdate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
//   			    		ldlastestdbrecordsdate = lastestdbrecordsdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
   			    	}
   			    } catch(java.lang.NullPointerException e) { 
   			    	e.printStackTrace();
   			    } catch (SQLException e) {
   			    	e.printStackTrace();
   			    }catch(Exception e){
   			    	e.printStackTrace();
   			    } finally {
   			    	if(rs != null)
						try {
							rs.close();
							rs = null;
						} catch (SQLException e) {
							e.printStackTrace();
						}
   			    	allgegucode = null;
   			    }
				
				setVolAmoRecordsFromFileToDatabase(tmpbkcode,tmpbkfile,ldlastestdbrecordsdate,optTable,dateRule,tmprecordfile);

		}
		
		return tmprecordfile;
	}
	/*
	 * 修改板块中某个个股的权重
	 */
	public void setStockWeightInBanKuai(BanKuai bankuai, String bkname, String stockcode, int weight) 
	{
		String bkcode = bankuai.getMyOwnCode();
		HashMap<String, String> actiontables = this.getActionRelatedTables(bankuai, stockcode);
		String bktypetable = actiontables.get("股票板块对应表");
		String bkorzsvoltable = actiontables.get("板块每日交易量表");
		String bknametable = actiontables.get("板块指数名称表");
		String sqlupdatestat = "UPDATE " + bktypetable  + " SET 股票权重 = " +  weight  + 
				" WHERE 板块代码 = '" + bkcode + 
				"' AND 股票代码 = '" + stockcode + "'" +
				"  AND  ISNULL(移除时间)"
				;
		logger.debug(sqlupdatestat);
		connectdb.sqlUpdateStatExecute(sqlupdatestat);
		
		actiontables = null;
	}
	
	/*
	 * 
	 */
	public File refreshTDXZDYBanKuai (HashMap<String, String> neededimportedzdybkmap)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步通达信自定义板块报告.txt");
		
		 //准备XML文件
//		File sysconfigfile = new File(bankuaichanyelianxml );
//		if(!sysconfigfile.exists()) { //不存在，创建该文件	
//			try {
//				sysconfigfile.createNewFile();
//				logger.debug(bankuaichanyelianxml + "不存在，已经创建");
//			} catch (IOException e) {
//				logger.debug(bankuaichanyelianxml + "不存在，创建失败！");
//			}
//		}
		
		this.refreshTDXZiDingYiBanKuai (neededimportedzdybkmap,tmprecordfile);
		this.refreshTDXZiDingYiBanKuaiToGeGu(neededimportedzdybkmap, tmprecordfile);
		
		return tmprecordfile;
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
	 * 更新通达信的自定义板块
	 */
	private int refreshTDXZiDingYiBanKuai (HashMap<String, String> neededimportedzdybkmap, File tmprecordfile)
	{
		Set<String> neededimportzdybknames = neededimportedzdybkmap.keySet(); 
		Set<String> curzdybknames = this.initializeSystemAllZdyBanKuaiList().keySet();
		
		 //把新的自定义板块加入数据库
	      SetView<String> differencebankuainew = Sets.difference(neededimportzdybknames, curzdybknames ); 
		    for (String str : differencebankuainew) {
				String sqlinsertstat = "INSERT INTO  通达信自定义板块列表(板块名称,创建时间) values ("
						+ "'" + str.trim() + "'" + ","
						+ "\"" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now())   + "\""  
						+ ")"
						;
				//logger.debug(sqlinsertstat);
				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
				//tfldresult.append("加入：" + str.trim() + " " + gupiaoheader + "\n");
			}
		    differencebankuainew = null;
		    
	        //把旧的自定义板块删除出数据库
	        SetView<String> differencebankuaiold = Sets.difference(curzdybknames,neededimportzdybknames  );
	        for (String str : differencebankuaiold) {
	        	String sqldeletetstat = "DELETE  FROM 通达信自定义板块列表 "
	        							+ " WHERE 板块名称=" + "'" + str.trim() + "'" 
	        							;
	        	//logger.debug(sqldeletetstat);
	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
	    		//tfldresult.append("删除：" + str.trim() + " " + gupiaoheader + "\n");
	    		
	    		sqldeletetstat = "DELETE  FROM 股票通达信自定义板块对应表 "
						+ " WHERE 自定义板块=" + "'" + str.trim() + "'" 
						;
				//logger.debug(sqldeletetstat);
				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
	        }
	        differencebankuaiold = null;
	        
	        neededimportzdybknames = null;
	        curzdybknames = null;
	        
	        
	        return 1;
	}
	/*
	 * 更新自定义板块和个股的对应关系
	 */
	private int refreshTDXZiDingYiBanKuaiToGeGu (HashMap<String, String> neededimportedzdybkmap, File tmprecordfile) 
	{
		Set<String> neededimportzdybknames = neededimportedzdybkmap.keySet(); 
		//Set<String> curzdybknames = this.getZdyBkSet ();
		
		for(String newzdybk:neededimportzdybknames) {
			String filename = neededimportedzdybkmap.get(newzdybk); //str是自定义板块的名称
			File zdybk = new File(filename);
			List<String> readLines = null;
			try {
				readLines = Files.readLines(zdybk,Charsets.UTF_8,new TDXBanKuaiFielProcessor ());
			} catch (IOException e) {
				e.printStackTrace();
			}
			Set<String> stocknamesnew = new HashSet<String>(readLines);
			
			Set<String> tmpstockcodesetold = new HashSet<String>();
			CachedRowSetImpl rs = null;
			try {
			    	String sqlquerystat = "SELECT *  FROM 股票通达信自定义板块对应表  WHERE  自定义板块 = " 
			    							+ "'"  + newzdybk.trim() + "'";
			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			        rs.last();  
			        int rows = rs.getRow();  
			        rs.first();  
			        for(int j=0;j<rows;j++) {  
			        	String stockcode = rs.getString("股票代码");
			        	tmpstockcodesetold.add(stockcode);
			        	
			            rs.next();
			        }
			       
			    }catch(java.lang.NullPointerException e){ 
			    	e.printStackTrace();
			    } catch (SQLException e) {
			    	e.printStackTrace();
			    }catch(Exception e){
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
			
			 //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
		    SetView<String> differencebankuainew = Sets.difference(stocknamesnew, tmpstockcodesetold ); 
   		    for (String newstock : differencebankuainew) {
   				
   				String sqlinsertstat = "INSERT INTO  股票通达信自定义板块对应表(股票代码,自定义板块) values ("
   						+ "'" + newstock.trim() + "'" + ","
   						+ "'" + newzdybk.trim() + "'" 
   						+ ")"
   						;
   				//logger.debug(sqlinsertstat);
   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
   				//tfldresult.append("加入：" + str.trim() + " " + gupiaoheader + "\n");
   			}
   		 differencebankuainew = null;
   		 
   	        //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要从数据库中删除
   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,stocknamesnew  );
   	        for (String oldstock : differencebankuaiold) {
   	        	String sqldeletetstat = "DELETE  FROM 股票通达信自定义板块对应表 "
   	        							+ " WHERE 股票代码=" +  "'" + oldstock.trim() + "'"
   	        							+ " AND 风格板块=" + "'" + newzdybk.trim() + "'"
   	        							;
   	        	//logger.debug(sqldeletetstat);
   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
   	    		//tfldresult.append("加入：" + str.trim() + " " + gupiaoheader + "\n");
   	        }
   	     differencebankuaiold = null;
		}
		
		
		neededimportzdybknames = null;
		return 0;
	}
	
	/*
	 * 自定义板块有些并不需要导入，用户选择导入哪些自定义板块
	 */
	public HashMap<String, String> getTDXZiDingYiBanKuaiList ()
	{
		File file = new File(sysconfig.getTDXSysZDYBanKuaiFile () );
		 
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
		
		 return zdybkmap;
	}
		/*
		 * 获取板块或个股的买入卖出关注送配股等信息
		 */
		public BkChanYeLianTreeNode getZdgzMrmcZdgzYingKuiFromDB (BkChanYeLianTreeNode stockbasicinfo)
		{
			String sqlquerystat;
			if(stockbasicinfo.getType() == BanKuaiAndStockBasic.TDXGG || stockbasicinfo.getType() == BanKuaiAndStockBasic.BKGEGU) 
				sqlquerystat = getZdgzMrmcYingKuiSQLForStock (stockbasicinfo);
			else //板块和个股是不一样的，板块没有买入卖出信息
				sqlquerystat = getZdgzMrmcYingKuiSQLForBanKuai (stockbasicinfo);
			
			 CachedRowSetImpl rs = null;
			 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			 stockbasicinfo.getNodeJiBenMian().setZdgzmrmcykRecords( setZdgzMrmcZdgzYingKuiRecords (rs) );
			 
			 try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
			 } 
			
			 return stockbasicinfo;
		}
		/*
		 * 获取板块的相关信息
		 */
		private String getZdgzMrmcYingKuiSQLForBanKuai(BkChanYeLianTreeNode stockbasicinfo) 
		{
			String stockcode = stockbasicinfo.getMyOwnCode();
			
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
		private String getZdgzMrmcYingKuiSQLForStock(BkChanYeLianTreeNode stockbasicinfo) 
		{
			String stockcode = stockbasicinfo.getMyOwnCode();
			String sqlquerystat1= "SELECT ggyk.日期, "
								+ "IF(ggyk.盈亏金额>0,'盈利','亏损') AS 盈亏情况,"
								+ " ggyk.原因描述,"
								+ " ggyk.ID,"
								+ " ggyk.操作账号,"
								+  "'个股盈亏'" 
								+ " FROM  A股个股盈亏 ggyk "
								+ " WHERE ggyk.股票代码 =" + "'" + stockcode + "'" 
								
								+ " UNION ALL " 
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
								
								+ " UNION ALL " 
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
								
								+ " UNION  ALL "
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
								
								+ " UNION ALL "
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
								+ " ORDER BY 1 DESC,5,3 " //desc
								;
	     //logger.debug(sqlquerystat5);
		
	     String sqlquerystat = sqlquerystat1 + sqlquerystat2 + sqlquerystat3 + sqlquerystat4 + sqlquerystat5;
	     //logger.debug(sqlquerystat);
	     
	     return sqlquerystat;
		}

		/*
		 * 获得和该股票相关的所有信息
		 */
		private Object[][] setZdgzMrmcZdgzYingKuiRecords(CachedRowSetImpl rs) 
		{
			String[][] data = null;  
		    try  {
		    	int k = 0;
		        int columnCount = 6;//列数
		        
		        rs.last();  
		        int rows = rs.getRow();  
		        data = new String[rows][];
		        rs.first();  
		        //while(rs.next())  //{ "日期", "操作", "说明","ID","操作账户","信息表" };
		        for(int j=0;j<rows;j++) {   //{ "日期", "操作", "说明","ID","操作账户","信息表" };
		            String[] row = new String[columnCount];  
		            row[0] = rs.getTimestamp(1).toString();
		            if(row[0].contains("."))
		            	row[0] = row[0].substring(0, row[0].length()-2);
		            row[1] = rs.getString(2);
		            row[2] = rs.getString(3);
		            row[3] = rs.getString(4);
		            row[4] = rs.getString(5);
		            row[5] = rs.getString(6);
	              
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
		

		
		

//		private boolean getANewDtockCodeIndicate(CachedRowSetImpl rs) 
//		{
//			boolean newStockSign = false;
//			try	{   
//				rs.next();
//				if(0 == rs.getRow()) {
//					newStockSign= true;
//				} else {
//					newStockSign= false;
//				}
//				rs.first();
//			} catch(Exception e) {
//				JOptionPane.showMessageDialog(null,"ConnectDatabase -> setANewDtockCodeIndicate 不可预知的错误");
//				logger.debug(""+e);
//			} 		
//			
//			return newStockSign;
//		}

		public Stock getCheckListsXMLInfo(Stock stockbasicinfo)
		{
			String stockcode = stockbasicinfo.getMyOwnCode();
			String sqlquerystat= "SELECT checklistsitems FROM A股   WHERE 股票代码 =" +"'" + stockcode +"'" ;
			//logger.debug(sqlquerystat);
			CachedRowSetImpl rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);

			try {
				rsagu.first();
				while (rsagu.next()) {
					String checklistsitems = rsagu.getString("checklistsitems");
					stockbasicinfo.setChecklistXml(checklistsitems);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				rsagu.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return stockbasicinfo;
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
		public boolean updateStockNewInfoToDb(BkChanYeLianTreeNode nodeshouldbedisplayed) 
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
		    
		    String txtfldinputzhengxiangguan = "'" + nodeshouldbedisplayed.getNodeJiBenMian().getZhengxiangguan().trim() + "'";
		    String txtfldinputfuxiangguan = "'" + nodeshouldbedisplayed.getNodeJiBenMian().getFuxiangguan().trim() + "'";
		    
		    String keHuCustom = nodeshouldbedisplayed.getNodeJiBenMian().getKeHuCustom();
		    String jingZhengDuiShou = nodeshouldbedisplayed.getNodeJiBenMian().getJingZhengDuiShou();
	    
			 if(nodeshouldbedisplayed.getType() == 6) {//是股票{
				 HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
				 String sqlupdatestat= "UPDATE A股  SET "
							+ " 股票名称=" + stockname +","
							+ " 概念时间=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getGainiantishidate()) ) +","
							+ " 概念板块提醒=" + txtareainputgainiants +","
							+ " 券商评级时间=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getQuanshangpingjidate() ) ) +","
							+ " 券商评级提醒=" + txtfldinputquanshangpj +","
							+ " 负面消息时间=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getFumianxiaoxidate()  ) ) +","
							+ " 负面消息=" + txtfldinputfumianxx +","
							+ " 正相关及客户=" + txtfldinputzhengxiangguan +","
							+ " 负相关及竞争对手=" + txtfldinputfuxiangguan +","
							+ " 客户=" + "'" + keHuCustom +"'" + ","
							+ " 竞争对手=" + "'" + jingZhengDuiShou + "'"  
							+ " WHERE 股票代码=" + stockcode
							;
				 sqlstatmap.put("mysql", sqlupdatestat);
				 //logger.debug(sqlupdatestat);
				 
				 if( connectdb.sqlUpdateStatExecute(sqlupdatestat) !=0)
					 return true;
				 else return false;
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
							+ " 负面消息=" + txtfldinputfumianxx +","
							+ " 正相关及客户=" + txtfldinputzhengxiangguan +","
							+ " 负相关及竞争对手=" + txtfldinputfuxiangguan +","
							+ " 客户=" + "'" + keHuCustom +"'" + ","
							+ " 竞争对手=" + "'" + jingZhengDuiShou + "'"  
							+ " WHERE 板块ID=" + stockcode
							;
				 //logger.debug(sqlinsertstat); 
				 sqlstatmap.put("mysql", sqlinsertstat);
				 if( connectdb.sqlUpdateStatExecute(sqlinsertstat) !=0 ) {
					 return true;
				 } else return false;
			 }
		}


		public boolean updateChecklistsitemsToDb (Stock stockbasicinfo)
		{
			String stockcode = stockbasicinfo.getMyOwnCode();
			String checklistsitems = stockbasicinfo.getChecklistXml();
			String sqlupdatestat= "UPDATE A股  SET "
					+ "checklistsitems=" + "'" + checklistsitems + "'"
					+ "WHERE 股票代码=" + stockcode
					
					;
		// logger.debug(sqlupdatestat);
		 connectdb.sqlUpdateStatExecute(sqlupdatestat);
		 return true;
		}

		public Stock getTDXBanKuaiForAStock(Stock stockbasicinfo) 
		{
			String stockcode = stockbasicinfo.getMyOwnCode();
			HashMap<String,String> tmpsysbk = new HashMap<String,String> ();
			
//			SELECT gpgn.概念板块, tdxbk.`板块ID`
//			FROM 股票通达信概念板块对应表 gpgn, 通达信板块列表 tdxbk
//			WHERE 股票代码= '000001' AND gpgn.概念板块 = tdxbk.`板块名称`
			String sqlquerystat =null;
			sqlquerystat=  "SELECT gpgn.板块代码 板块代码, tdxbk.`板块名称` 板块名称 FROM 股票通达信概念板块对应表 gpgn, 通达信板块列表 tdxbk"
					+ "  WHERE 股票代码=" + "'" +  stockcode.trim() + "'" + "AND gpgn.板块代码 = tdxbk.`板块ID` AND ISNULL(移除时间)"
					+ "UNION " 
					+ " SELECT gphy.板块代码 板块代码, tdxbk.`板块名称` 板块名称 FROM 股票通达信行业板块对应表 gphy, 通达信板块列表 tdxbk "
					+ " WHERE 股票代码=" + "'" +  stockcode.trim() + "'" + "AND gphy.`板块代码` = tdxbk.`板块ID` AND ISNULL(移除时间)"
					+ "UNION " 
					+ " SELECT gpfg.`板块代码` 板块代码, tdxbk.`板块名称` 板块名称  FROM 股票通达信风格板块对应表 gpfg, 通达信板块列表 tdxbk"
					+ "  WHERE 股票代码= "+ "'" +  stockcode.trim() + "'" + "AND gpfg.`板块代码` = tdxbk.`板块ID` AND ISNULL(移除时间)"
					;
			
			logger.debug(sqlquerystat);
			CachedRowSetImpl rs_gn = connectdb.sqlQueryStatExecute(sqlquerystat);
			try  {     
		        while(rs_gn.next()) {
		        	String bkcode = rs_gn.getString("板块代码");
		        	String bkname = rs_gn.getString("板块名称");
		        	tmpsysbk.put(bkcode,bkname);
		        } 
		        
		    }catch(java.lang.NullPointerException e){ 
		    	e.printStackTrace();
		    	
		    }catch(Exception e) {
		    	e.printStackTrace();
		    } finally {
		    	if(rs_gn != null) {
		    		try {
						rs_gn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		rs_gn = null;
		    	}
		    }
			
			stockbasicinfo.setGeGuCurSuoShuTDXSysBanKuaiList(tmpsysbk);
			
//			sqlquerystat=  "SELECT 自定义板块 FROM 股票通达信自定义板块对应表  WHERE 股票代码= "
//							+ "'" +  stockcode.trim() + "'"
//							;
//			//logger.debug(sqlquerystat);
//			CachedRowSetImpl rs_zdy = connectdb.sqlQueryStatExecute(sqlquerystat);
//			HashSet<String> tmpzdybk = new HashSet<String> ();
//			try  {     
//				rs_zdy.last();  
//		        int rows = rs_zdy.getRow();  
//		        rs_zdy.first();  
//		        int k = 0;  
//		        //while(rs.next())
//		        for(int j=0;j<rows;j++) { 
//		        	tmpzdybk.add( rs_zdy.getString("自定义板块") );
//		        	rs_zdy.next();
//		        } 
//		        rs_zdy.close();
//		    }catch(java.lang.NullPointerException e){ 
//		    	e.printStackTrace();
//		    }catch(Exception e) {
//		    	e.printStackTrace();
//		    }
//			
//			stockbasicinfo.setSuoShuTDXZdyBanKuai(tmpzdybk);
			
			return stockbasicinfo;
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
	            	   
	            	   if(  !zhishucode.startsWith("000") && !zhishucode.startsWith("001") && !zhishucode.startsWith("002") && !zhishucode.startsWith("300"))
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
	            				   e.printStackTrace();
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
	            	   
	            	   if(  !(zhishucode.startsWith("60") ) )
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
			 connectdb.sqlUpdateStatExecute(sqlupdatestat);
			
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
			
			HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
			sqlstatmap.put("mysql", sqlquerystat);
			
//			sqlquerystat = getZdgzMrmcYingKuiSQLForAccess (stockbasicinfo);
//			sqlstatmap.put("access", sqlquerystat);
	     
			 CachedRowSetImpl rs = null;
			 rs = connectdb.sqlQueryStatExecute(sqlstatmap);
			 
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
	                if( !(stockcode.startsWith("00") || stockcode.startsWith("30") || stockcode.startsWith("60") ) 
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
	                
	                 sqlinsetstat = "INSERT INTO 股票通达信基本面信息对应表 (股票代码GPDM, 总股本ZGB,更新日期GXRQ, GJG,FQRFRG,法人股FRG,B股BG,H股HG,流通A股LTAG,每股收益ZGG,"
	                														+  "ZPG,总资产ZZC,流动资产LDZC,固定资产GDZC,无形资产WXZC,股东人数CQTZ,流动负债LDFZ,少数股权CQFZ,"
	                														+ "公积金ZBGJJ,净资产JZC,主营收益ZYSY,营业成本ZYLY,应收帐款QTLY,营业利润YYLY,投资收益TZSY,"
	                														+ "经营现金流BTSY,总现金流YYWSZ,存货SNSYTZ,利润总额LYZE,税后利润SHLY,净利润JLY,"
	                														+ "未分配利益WFPLY,净资产TZMGJZ,地域DY,行业HY,ZBNB) "
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
			                					+ zbnb
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
	                        					+ " ZBNB=" + zbnb
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
	                	if(stockcode.startsWith("60"))
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
		public void updateBanKuaiExportGephiBkfxOperation(String nodecode, boolean importdailydata, boolean exporttogephi, boolean showinbkfx)
		{
			String sqlupdatestat = "UPDATE 通达信板块列表 SET " +
								" 导入交易数据=" + importdailydata  + "," +
								" 导出Gephi=" + exporttogephi +  ","  +
								" 板块分析=" + showinbkfx +
								" WHERE 板块ID='" + nodecode + "'"
								;
			logger.debug(sqlupdatestat);
	   		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		}
		/*
		 * 导入网易股票行情接口的数据，
		 */
		public void importNetEaseStockData() 
		{
			ArrayList<Stock> allstocks = this.getAllStocks ();
			//下载需要的时间段的数据
			for(Stock stock : allstocks) {
				boolean neteasthasdata = false;
				String stockcode = stock.getMyOwnCode();
				String formatedstockcode; String stockdatatable ;
				if(stock.getSuoShuJiaoYiSuo().trim().toLowerCase().equals("sh")) { 
					formatedstockcode = "0" + stockcode;
					stockdatatable = "通达信上交所股票每日交易信息";
				} else {
					formatedstockcode = "1" + stockcode;
					stockdatatable = "通达信深交所股票每日交易信息";
				}
				
				CachedRowSetImpl  rs = null;
				LocalDate ldlastestdbrecordsdate = null;
				try { 	
					//找出每日交易的起点终点
					String sqlquerystat = "SELECT  MAX(交易日期) 	MOST_RECENT_TIME FROM  " + stockdatatable + 
											" WHERE  代码 = '" + stockcode + "' AND 换手率 IS NOT NULL AND 总市值 IS  NOT NULL AND 流通市值 IS  NOT NULL"
											;
					logger.debug(sqlquerystat);
				    rs = connectdb.sqlQueryStatExecute(sqlquerystat);
				    while(rs.next()) {
//				    		logger.debug(rs.getMetaData().getColumnCount());
				    		 java.sql.Date lastestdbrecordsdate = rs.getDate("MOST_RECENT_TIME"); //mOST_RECENT_TIME 
				    		 try {
				    			 ldlastestdbrecordsdate = lastestdbrecordsdate.toLocalDate();
				    		 } catch (java.lang.NullPointerException e) {
				    			 logger.info(stockcode + "似乎没有数据，请检查！");
				    		 }
				    }
				} catch(java.lang.NullPointerException e) { 
			    	e.printStackTrace();
				} catch (SQLException e) {
			    	e.printStackTrace();
				}catch(Exception e){
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
				
			   if(ldlastestdbrecordsdate == null)
				    	ldlastestdbrecordsdate =  LocalDate.parse("2013-03-04"); //当前数据的起点
			   else
				    	ldlastestdbrecordsdate = ldlastestdbrecordsdate.plusDays(1);
				    
				    //获取网易文件
				    URL URLink; //https://blog.csdn.net/NarutoInspire/article/details/72716724
				    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
				    String startdate = ldlastestdbrecordsdate.format(formatters);
				    
				    LocalTime tdytime = LocalTime.now(); //如果是在交易时间导入数据的话，当天的数据还没有，所以要判断一下
				    LocalDate endldate = LocalDate.now(); 
					if( ( tdytime.compareTo(LocalTime.of(9, 0, 0)) >0 && tdytime.compareTo(LocalTime.of(18, 0, 0)) <0) ) {
						 endldate = endldate.minusDays(1);
					}
				    
					if(ldlastestdbrecordsdate.compareTo(endldate) >0) { //说明只有今天的数据没有导入，而今天还没有数据，所以跳出
						continue;
					}
					
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
					} catch ( IOException e) {
						e.printStackTrace();
					}
					//导入数据到数据库
					if(!savedfile.exists()) {
						System.out.println(stockcode + "似乎未能从网易得到"+ stockcode + "的数据，请立即检查！");
						neteasthasdata = false;
					} else {
						try (
//								java.io.Reader filereader = java.nio.file.Files.newBufferedReader(Paths.get(savedfilename));
//					            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
								
								FileReader filereader = new FileReader(savedfile);
								CSVReader csvReader = new CSVReader(filereader);
					    ){ 
					            // Reading Records One by One in a String array
					            String[] nextRecord; int titlesign =0; 
					            while ((nextRecord = csvReader.readNext()) != null) {
					            	if(0 == titlesign) {
						                String huanshoulv = nextRecord[10];
						                String zongshizhi = nextRecord[13];
						                
						                if(!huanshoulv.trim().equals("换手率") || !zongshizhi.trim().equals("总市值")) {
						                	System.out.println("网易数据文件格式可能有改变，请检查！");
						                	neteasthasdata = false;
						                	break;
						                } else {
						                	titlesign = 1;
						                }
					            	} else { //第二行
					            		String shoupanjia = nextRecord[3];
					            		if(Double.parseDouble(shoupanjia) == 0) //收盘价不可能为0，0说明停牌，在交易数据表中，停牌是没有记录的，跳过 
					            			continue;
					            		
					            		LocalDate lactiondate = null;
					            		String actiondate =  nextRecord[0];
					            		if(Pattern.matches("\\d*/\\d*/\\d*",actiondate) ) {
					            			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
					            			lactiondate = LocalDate.parse(actiondate, formatter);
					            		} else if(Pattern.matches("\\d*-\\d*-\\d*",actiondate) ) {
					            			lactiondate = LocalDate.parse(actiondate);
					            		} else {
					            			System.out.println("网易数据文件日期格式改变！");
					            			neteasthasdata = false;
						                	break;
					            		}
					            		
						                String zhangdiefu = nextRecord[9];
						                String huanshoulv = nextRecord[10];
						                String zongshizhi = nextRecord[13];
						                String liutongshizhi = nextRecord[14];
						                //update 涨跌幅和换手率数据
						                String sqlupdate = "Update " + stockdatatable + " SET " +
						                					" 涨跌幅=" + Double.parseDouble(zhangdiefu) + "," +
						                					" 换手率=" + Double.parseDouble(huanshoulv) + "," +
						                					" 流通市值= " + Double.parseDouble(liutongshizhi) + "," +
						                					" 总市值= " + Double.parseDouble(zongshizhi) +
						                					" WHERE 交易日期 = '" + lactiondate + "'" + 
						                					" AND 代码= '" + stockcode + "'"
						                					;
						                logger.debug(sqlupdate);
									    int result = connectdb.sqlUpdateStatExecute(sqlupdate);		
									    
									    neteasthasdata = true;
					            	}
					            	
					            }
					        } catch (IOException e) {
								e.printStackTrace();
							} 
					}
				 
				
//				if(!neteasthasdata) { //网易有时候会得不到数据，就要用雪球的方法得到数据
//					logger.info(stockcode + "未能从网易获取日期为" + ldlastestdbrecordsdate + "数据，将从其他数据源获取数据！");
//					importXueQiuStockData (stockcode,ldlastestdbrecordsdate);
//				}
			}

			allstocks = null;
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
		 * check股票每日数据是否完整
		 */
		public HashSet<String> checkStockDataIsCompleted() 
		{
			HashSet<String> stockset = new HashSet<String>(); 
			CachedRowSetImpl  rssh = null;
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
						  "and a股.已退市 is null \r\n"
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
			
			
			CachedRowSetImpl  rssz = null;
			try { 	
				//找出每日交易的起点终点
				String sqlquerystat = "SELECT \r\n" +
						  "通达信深交所股票每日交易信息.`代码`\r\n" +
						  "FROM \r\n" +
						  "通达信深交所股票每日交易信息 \r\n" +   
						  "LEFT JOIN a股 \r\n" +
						  "ON 通达信深交所股票每日交易信息.`代码` = a股.股票代码  \r\n" +
						  "WHERE \r\n" +
						  "(通达信深交所股票每日交易信息.涨跌幅 is null or      通达信深交所股票每日交易信息.换手率 is null or      通达信深交所股票每日交易信息.总市值 is null or      通达信深交所股票每日交易信息.流通市值 is null) \r\n" +
						  "and a股.已退市 is null \r\n"
						  ;
				logger.debug(sqlquerystat);
				
			    rssz = connectdb.sqlQueryStatExecute(sqlquerystat);
			    
			    while(rssz.next()) {
			    	String stockcode = rssz.getString("代码");
			    	stockset.add(stockcode);
			    }
			} catch(java.lang.NullPointerException e) { 
		    	e.printStackTrace();
			} catch (SQLException e) {
		    	e.printStackTrace();
			}catch(Exception e){
		    	e.printStackTrace();
			} finally {
		    	if(rssz != null)
				try {
					rssz.close();
					rssz = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		   }
			return stockset;
		}
		/*
		 * 从东方财富下载的数据，数据全，但是只能当天数据，如果当天错过，就无法得到历史数据
		 */
		public boolean importEastMoneyStockData(File eastmoneyfile)
		{
			HashSet<String> missingdatastockset = this.checkStockDataIsCompleted ();
		    FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(eastmoneyfile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
		    POIFSFileSystem fileSystem = null;
			try {
				fileSystem = new POIFSFileSystem(bufferedInputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    HSSFWorkbook workbook = null;
			try {
				workbook = new HSSFWorkbook(fileSystem);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 // for each sheet in the workbook
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {

                String sheetname = workbook.getSheetName(i);
                LocalDate lactiondate = null;
                try {
					if(Pattern.matches("\\d*/\\d*/\\d*",sheetname) ) {
	        			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	        			lactiondate = LocalDate.parse(sheetname, formatter);
	        		} else if (Pattern.matches("\\d*",sheetname) ) {
	        			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	        			lactiondate = LocalDate.parse(sheetname, formatter);
	        		} else if(Pattern.matches("\\d*-\\d*-\\d*",sheetname) ) 
	        			lactiondate = LocalDate.parse(sheetname);
                } catch (Exception ex) {
                	lactiondate = LocalDate.now();
                }
                
                HSSFSheet sheet = workbook.getSheet(workbook.getSheetName(i));

    		    int lastRowIndex = sheet.getLastRowNum();
    		     //先找到数据对应的列的值
    		    int codeindex = -1, huanshouindex = -1,zongshizhiindex = -1,liutongshizhiindex = -1,zhangdiefuindex = -1;
    		    HSSFRow headrow = sheet.getRow(0);
    		    short lastCellNum = headrow.getLastCellNum();
		        for (int j = 0; j < lastCellNum; j++) {
		        	String cellValue = headrow.getCell(j).getStringCellValue();
		        	if(cellValue.equals("换手%"))
		        		huanshouindex = j;
		        	else if(cellValue.equals("总市值"))
		        		zongshizhiindex = j;
		        	else if(cellValue.equals("流通市值"))
		        		liutongshizhiindex = j;
		        	else if(cellValue.equals("代码"))
		        		codeindex = j;
		        	else if(cellValue.equals("涨幅"))
		        		zhangdiefuindex = j;
		        }
		        if(huanshouindex == -1 || zongshizhiindex == -1 || liutongshizhiindex == -1) {
		        	logger.info("东方财富数据格式可能有改变，请检查！");
		        	return false;
		        }
		        //导入数据
		        for (int m = 1; m <= lastRowIndex; m++) {
    		        HSSFRow row = sheet.getRow(m);
    		        if (row == null) { break; }

    		        String stockcode = row.getCell(codeindex).getStringCellValue();
    		        String stockdatatable;
    		        if(missingdatastockset.contains(stockcode)) {
    		        	if(stockcode.startsWith("6")) { 
    						stockdatatable = "通达信上交所股票每日交易信息";
    					} else {
    						stockdatatable = "通达信深交所股票每日交易信息";
    					}
    		        	
    		        	String huanshoulv = row.getCell(huanshouindex).getStringCellValue().trim();
        		        String zongshizhi = row.getCell(zongshizhiindex).getStringCellValue().trim();
        		        String liutongshizhi = row.getCell(liutongshizhiindex).getStringCellValue().trim();
        		        String zhangdiefu = row.getCell(zhangdiefuindex).getStringCellValue().trim();
        		        
		                //update 涨跌幅和换手率数据
		                String sqlupdate = "Update " + stockdatatable + " SET " +
		                					" 涨跌幅=" + Double.parseDouble(zhangdiefu) + "," +
		                					" 换手率=" + Double.parseDouble(huanshoulv) + "," +
		                					" 流通市值= " + Double.parseDouble(liutongshizhi) + "," +
		                					" 总市值= " + Double.parseDouble(zongshizhi) +
		                					" WHERE 交易日期 = '" + lactiondate + "'" + 
		                					" AND 代码= '" + stockcode + "'"
		                					;
		                logger.debug(sqlupdate);
					    int result = connectdb.sqlUpdateStatExecute(sqlupdate);
    		        }
    		    }
            }

		    try {
				bufferedInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		    missingdatastockset = null;
		    return true;
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
		 * 股票池/板块州
		 */
		public HashMap<String, String> getGuPiaoChi() 
		{
			HashMap<String,String> gpcindb = new HashMap<String,String> ();
			boolean hasbkcode = true;
            CachedRowSetImpl rspd = null; 
            
			try {
				   String sqlquerystat = "SELECT * FROM 产业链板块州列表 "
							;
	
			    	logger.debug(sqlquerystat);
			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			    	
			        while(rspd.next())  {
			        	String bkzcode = rspd.getString("板块州代码");
			        	String bkzname = rspd.getString("板块州名称");
			        	gpcindb.put(bkzcode, bkzname);
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
	
			return gpcindb;
		}
		/*
		 * 股票池对应的子股票池/板块国
		 */
		public HashMap<String, String> getSubGuPiaoChi(String gpccode) 
		{
			HashMap<String,String> gpcindb = new HashMap<String,String> ();
			boolean hasbkcode = true;
            CachedRowSetImpl rspd = null; 
            
			try {
				   String sqlquerystat = "SELECT 产业链板块国列表.`板块国代码`,产业链板块国列表.`板块国名称`" + 
										" FROM 产业链板块国列表" +
										" INNER JOIN 产业链板块州板块国对应表" +
										" ON 产业链板块国列表.`板块国代码` = 产业链板块州板块国对应表.`板块国代码`" +
										" WHERE 产业链板块州板块国对应表.`板块州代码`  = '" + gpccode + "' " +
										" ORDER BY 产业链板块国列表.`板块国代码`" 
										;
	
			    	logger.debug(sqlquerystat);
			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			    	
			        while(rspd.next())  {
			        	String bkzcode = rspd.getString("板块国代码");
			        	String bkzname = rspd.getString("板块国名称");
			        	gpcindb.put(bkzcode, bkzname);
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
	
			return gpcindb;
		}
		/*
		 * 
		 */
		public String addNewSubGuoPiaoChi(String nodecode, String subnodename) 
		{
			HashMap<String, String> cursubbks = this.getSubGuPiaoChi(nodecode);
			int cursize = cursubbks.size();
			if(cursubbks.containsValue(subnodename.trim())) 
				return null;
			
			String subgpccode;
			if(cursize+1 >= 10)
				subgpccode = nodecode + String.valueOf(cursize+1);
			else
				subgpccode = nodecode + "0" + String.valueOf(cursize+1);
			
			String sqlinsertstat = "INSERT INTO 产业链板块国列表(板块国代码,板块国名称) VALUES ("
									+ "'" + subgpccode.trim() + "'" + ","
									+ "'" + subnodename.trim() + "'" 
											+ ")"
									;
			logger.debug(sqlinsertstat);
			int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
			
			sqlinsertstat = "INSERT INTO  产业链板块州板块国对应表(板块州代码,板块国代码) values ("
									+ "'" + nodecode.trim() + "'" + ","
									+ "'" + subgpccode.trim() + "'" 
									+ ")"
									;
			logger.debug(sqlinsertstat);
			autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
			
			cursubbks = null;
			return subgpccode;
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
			int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
			return autoIncKeyFromApi;
		}
		
}

/*
 * google guava files 类，可以直接处理读出的line
 */
class TDXBanKuaiFielProcessor implements LineProcessor<List<String>> 
{
   
    private List<String> stocklists = Lists.newArrayList();
   
    @Override
    public boolean processLine(String line) throws IOException {
    	if(line.length() >=7)
    		stocklists.add(line.substring(1));
        return true;
    }
    @Override
    public List<String> getResult() {
        return stocklists;
    }
}

//  SELECT 'a股',UPDATE_TIME FROM information_schema.tables where TABLE_SCHEMA='stockinfomanagementtest' 