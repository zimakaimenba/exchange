package com.exchangeinfomanager.database;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.TDXNodesXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItemForJFC;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;
import com.google.common.primitives.UnsignedBytes;
import com.mysql.jdbc.MysqlDataTruncation;
import com.sun.rowset.CachedRowSetImpl;

public class BanKuaiDZHDbOperation 
{
	private ConnectDZHDataBase dzhconnectdb;
	private ConnectDataBase tdxconnectdb;
	private SetupSystemConfiguration sysconfig;

	public BanKuaiDZHDbOperation() 
	{
		tdxconnectdb = ConnectDataBase.getInstance();
//		dzhconnectdb = ConnectDZHDataBase.getInstance();
		sysconfig = new SetupSystemConfiguration();
	}
	/*
	 * 
	 */
	public void refreshDZHGaiNianBanKuaiGeGu ()
	{
		List<BanKuai> dzhbklist = this.getDZHBanKuaiList ();
			for(BanKuai dzhbk : dzhbklist ) {
				if(!dzhbk.getBanKuaiOperationSetting().isImportBKGeGu() )
					continue;
				
				String dzhbkcode = dzhbk.getMyOwnCode();
				Set<String> tmpstockcodesetold = getDZHGaiNianBanKuaiGeGuSetInDb (dzhbkcode);
				Set<String> tmpstockcodesetnew  = getDZHGaiNianBanKuaiGeGuSetFromDZHFiles (dzhbkcode);
				
				//把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要从数据库中删除
				SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
	   	        for (String stockcode : differencebankuaiold) {
	   	        	String sqlupdatestat = 
	   	        	" UPDATE 大智慧股票概念板块对应表  \r\n" + 
	   	        	" SET 移除时间 =  " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'\r\n" +  
	   	        	" WHERE 板块代码 = '" + dzhbkcode + "' \r\n" + 
	   	        	" AND 股票代码 = '" + stockcode + "' \r\n" + 
	   	        	" AND isnull(移除时间)"
	   	        	;
			   		try { int autoIncKeyFromApi = tdxconnectdb.sqlUpdateStatExecute(sqlupdatestat);
					} catch (SQLException e) { e.printStackTrace();}
	   	        }
	   	        differencebankuaiold = null;
	   	        
	   	        //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
	   		    for (String stockcode : differencebankuainew) {

	   		    	String sqlinsertstat = "INSERT INTO 大智慧股票概念板块对应表(股票代码,板块代码,股票权重)\r\n" + 
	   		    						   "VALUES ( " 
	   		    						   	+ "'" + stockcode + "',"
	   		    						   	+ "'" + dzhbk +  "',"
	   		    						   	+ "10"
	   		    						   	+ ")"   
	   		    							;
	   				
	   				try {	int autoIncKeyFromApi = tdxconnectdb.sqlInsertStatExecute(sqlinsertstat);
					} catch (SQLException e) { e.printStackTrace();}
	   			}
	   		    differencebankuainew = null;
			}
		 
		
		return;
	}
	/*
	 * 
	 */
	public Set<String> getDZHGaiNianBanKuaiGeGuSetFromDZHFiles(String dzhbk) 
	{
		Set<String> dzhbkstock = new HashSet<> ();
		
		String dzhfilepath = sysconfig.getDaZhiHuiBanKuaiFilePath ();
		String bkfilename = dzhbk + ".BLK";
		File dzhbkfile = new File(dzhfilepath + "/" + bkfilename);
		if (!dzhbkfile.exists() || dzhbkfile.isDirectory() || !dzhbkfile.canRead())   
		    return dzhbkstock;
		
		BufferedInputStream dis = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(dzhbkfile);
			dis = new BufferedInputStream(in);
			
			 int fileheadbytenumber = 4;
	         byte[] itemBuf = new byte[fileheadbytenumber];
	         dis.read(itemBuf, 0, fileheadbytenumber);
			 String fileHead = new String(itemBuf,0,fileheadbytenumber);
				
			 int sigbk = 12;
	         byte[] itemBuf2 = new byte[sigbk];
	         while (dis.read(itemBuf2, 0, sigbk) != -1) {
	         	   //板块名称
	         	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
	         	   String stockcode = gupiaoheader.substring(2);
	         	  dzhbkstock.add(stockcode);
	         	   
	         }
		} catch (FileNotFoundException e) {	e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();}  
		
		return dzhbkstock;
	}
	/*
	 * 
	 */
	public Set<String> getDZHGaiNianBanKuaiGeGuSetFromSQLite(String dzhbk) 
	{
		CachedRowSetImpl rsagu = null;
		try {
			 String sqlquerystat = "SELECT * FROM blocks WHERE  blockid = '" + dzhbk.trim() + "'"
		 			;
			rsagu = dzhconnectdb.dbcon.sqlQueryStatExecute(sqlquerystat);
			while(rsagu.next()) {
//				  Blob input = rsagu.getBlob("constituents");
				Object input = rsagu.getBinaryStream(2);
				      Object input2 = rsagu.getBinaryStream("constituents");
//				 BufferedInputStream dis = new BufferedInputStream(input);
//				 int fileheadbytenumber = 384;
//		         byte[] itemBuf = new byte[fileheadbytenumber];
//		         try {
//					dis.read(itemBuf, 0, fileheadbytenumber);
//					String fileHead = new String(itemBuf,0,fileheadbytenumber);
//					
//					int sigbk = 2813;
//		            byte[] itemBuf2 = new byte[sigbk];
//		            while (dis.read(itemBuf2, 0, sigbk) != -1) {
//		         	   //板块名称
//		         	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
//		            }
//				} catch (IOException e) {e.printStackTrace();} 
				 int i =0;
			
			}
		} catch (Exception e) { e.printStackTrace();
		} finally {
			try {	rsagu.close();	} catch (SQLException e) {	e.printStackTrace();}
			rsagu = null;
		}
		
		
         
        return null; 
         
	}
	/*
	 * 
	 */
	public Set<String> getDZHGaiNianBanKuaiGeGuSetInDb (String dzhbk)
	{
		CachedRowSetImpl rs = null; Set<String> tmpstockcodesetold = new HashSet<> ();
		try {
		    	String sqlquerystat = "SELECT 股票代码  \r\n" + 
		    			"FROM 大智慧股票概念板块对应表 gnb\r\n" + 
		    			"WHERE gnb.`板块代码` = '" + dzhbk + "'  AND ISNULL(gnb.`移除时间`)			"
						;
		    	rs = tdxconnectdb.sqlQueryStatExecute(sqlquerystat);
		    	
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
		
		return tmpstockcodesetold;
		
	}
	/*
	 * 
	 */
	public void refreshDZHShenZhenZhiShuAndGaiNianBanKuaiFromExportFileToDatabase ()
	{
		Set<String> dzhbksetindb = this.getDZHBanKuaiNameSet ("SZ");
		
		Set<String> dzhbksetinfile = new HashSet<> ();
		try {
			File dzhbkfile = new File(sysconfig.getDZHShenZhenZhiShuNameFile ());
			String line; BufferedReader reader;
			reader = new BufferedReader(new FileReader(dzhbkfile.getAbsolutePath() ));
			line = reader.readLine();
			while (line != null) {
				List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);
           		try {
	            			String bkcode = tmplinelist.get(0);
	            			if( ! sysconfig.isShenZhengZhiShu(bkcode))   {
	            				line = reader.readLine();
	            				continue;
	            			}
	            			
	            			String bkname=null ; if(tmplinelist.size() >1 ) bkname = tmplinelist.get(1);
	            			String sqlinsertstat = "INSERT INTO  大智慧板块列表(板块ID,板块名称,指数所属交易所) values ("
	        	   						+ " '" + bkcode.trim() + "'" + ","
	        	   						+ " '" + bkname.trim() + "'" + ","
	        	   						+ " 'SZ'"
	        	   						+ ")"
	        	   						+ " ON DUPLICATE KEY UPDATE "
	        	   						+ "板块名称 =" + " '" + bkname.trim() + "'" 
	        	   						+ ", 指数所属交易所 = 'SZ'"
	        	   						;
	            				int autoIncKeyFromApi = tdxconnectdb.sqlInsertStatExecute(sqlinsertstat);
        	   				
	            				dzhbksetinfile.add(bkcode);
           		} catch(SQLException e) {e.printStackTrace();
           		} catch (Exception e) {	}
				// read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {e.printStackTrace(); }
		
		SetView<String> differencebankuaiold = Sets.difference( dzhbksetindb, dzhbksetinfile );
		if(differencebankuaiold.size() >0 ) { //说明有某些板块需要删除
			 for(String oldbkcode : differencebankuaiold) {
				 String sqldeletetstat = "DELETE  FROM  大智慧板块列表 WHERE "
						 				+ " 板块ID =" + " '" + oldbkcode.trim() + "'" 
						 				;
	   			try {
					int autoIncKeyFromApi = tdxconnectdb.sqlDeleteStatExecute(sqldeletetstat);
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();	}

			 }
		 }
		 differencebankuaiold = null;
		 
		 return;
		
	}
	/*
	 * 板块设定时间跨度内和大盘相比的按周期占比。
	 */
	public  BanKuai getBanKuaiZhanBi (BanKuai bankuai,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{
		if(bankuai == null  )		return null;
		
		selecteddatestart = selecteddatestart.with(DayOfWeek.MONDAY);
		if(!bankuai.isNodeDataAtNotCalWholeWeekMode() )
			selecteddateend = selecteddateend.with(DayOfWeek.FRIDAY);
		
		//本函数初始是开发为周的占比，所以日/月线的占比掉用其他函数
		if(period.equals(NodeGivenPeriodDataItem.DAY)) //调用日线查询函数
			; 
		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //调用月线查询函数
			;
		
		NodeXPeriodData nodewkperioddata = bankuai.getNodeXPeroidData(period);
		
		String bkcode = bankuai.getMyOwnCode();
		String bkcjltable = null;
		String bkcys = bankuai.getNodeJiBenMian().getSuoShuJiaoYiSuo();
		if(bkcys == null)	return bankuai;
		if(bkcys.equalsIgnoreCase("BK"))	bkcjltable = "大智慧板块每日交易信息";
		else return bankuai;

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
		
		String sqlquerystatfx = "SELECT 操作记录重点关注.`日期`, COUNT(*) AS RESULT FROM 操作记录重点关注 \r\n" + 
				" WHERE 股票代码='" + bkcode + "'" + "\r\n" + 
				" AND 操作记录重点关注.`日期` between '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" +
				" GROUP BY YEAR(操作记录重点关注.`日期`), WEEK(操作记录重点关注.`日期`) "
				;
		
		CachedRowSetImpl rs = null;
		CachedRowSetImpl rsfx = null;
		try {
			//分析
			rsfx = tdxconnectdb.sqlQueryStatExecute(sqlquerystatfx);
			while(rsfx.next()) {
				java.sql.Date recdate = rsfx.getDate("日期");
				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(recdate);
				Integer reccount = rsfx.getInt("RESULT");
				nodewkperioddata.addFxjgToPeriod(wknum, reccount);
			}
			
			//交易
			rs = tdxconnectdb.sqlQueryStatExecute(sqlquerystat);
			
			while(rs.next()) {
				java.sql.Date lastdayofweek = rs.getDate("EndOfWeekDate");
				ZonedDateTime zdtime = lastdayofweek.toLocalDate().with(DayOfWeek.FRIDAY).atStartOfDay(ZoneOffset.UTC);
				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(lastdayofweek);
				
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
		}	catch(java.lang.NullPointerException e){e.printStackTrace();
	    } 	catch(Exception e){e.printStackTrace();
	    }  	finally {
	    	try {	rs.close();	rsfx.close();	rs = null;rsfx = null;bkcjltable = null;	} catch (SQLException e) {e.printStackTrace();}
	    }

		return bankuai;
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
		String bkcys = bk.getNodeJiBenMian().getSuoShuJiaoYiSuo();
		if(bkcys == null)
			return bk;
		if(bkcys.toLowerCase().equals("BK"))
			searchtable = "大智慧板块每日交易信息";
		else return bk;
		
		TemporalField fieldCH = WeekFields.of(Locale.CHINA).dayOfWeek();
		try{
			nodestartday = nodestartday.with(fieldCH, 1); //确保K线总是显示完整得一周
		} catch (java.lang.NullPointerException e) {return bk;}
		if(!bk.isNodeDataAtNotCalWholeWeekMode() )
			nodeendday = nodeendday.with(fieldCH, 7);
		
		NodeXPeriodData nodedayperioddata = bk.getNodeXPeroidData(period);

		String nodecode = bk.getMyOwnCode();
		
		String sqlquerystat = "SELECT *  FROM " + searchtable + " \r\n" + 
				"WHERE 代码='" + nodecode + "'" + "\r\n" + 
				"AND 交易日期  between'" + nodestartday + "' AND '" + nodeendday + "'"
				;
		
		CachedRowSetImpl rsfx = tdxconnectdb.sqlQueryStatExecute(sqlquerystat);
		LocalDate lastdaydate = nodestartday.with(fieldCH, 6); //
//		org.ta4j.core.TimeSeries nodenewohlc = new BaseTimeSeries.SeriesBuilder().withName(bk.getMyOwnCode()).build();
		OHLCSeries nodenewohlc = new OHLCSeries (bk.getMyOwnCode());
		try {  
			 rsfx = tdxconnectdb.sqlQueryStatExecute(sqlquerystat);
			
			 while(rsfx.next()) {
				 double open = rsfx.getDouble("开盘价");
				 double high = rsfx.getDouble("最高价");
				 double low = rsfx.getDouble("最低价");
				 double close = rsfx.getDouble("收盘价");
				 double cje = rsfx.getDouble("成交额");
				 double cjl = rsfx.getDouble("成交量");
				 java.sql.Date actiondate = rsfx.getDate("交易日期");
				 ZonedDateTime zdtime = actiondate.toLocalDate().atStartOfDay(ZoneOffset.UTC);
				 org.jfree.data.time.Day recordday = new org.jfree.data.time.Day (actiondate);
				 
				 NodeGivenPeriodDataItem bkperiodrecord = new NodeGivenPeriodDataItemForJFC( bk.getMyOwnCode(), NodeGivenPeriodDataItem.DAY,
						 recordday, open, high,  low,  close, 
							cjl, cje );
				 
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
	/*
	 * 函数需要修改
	 */
	private TDXNodes getTDXNodesWeeklyKXianZouShiForJFC (TDXNodes tdxnode, LocalDate friday, OHLCSeries nodenewohlc)
	{
		int newcount = nodenewohlc.getItemCount();
		if(newcount == 0)
			return tdxnode;
		
		TDXNodesXPeriodDataForJFC nodexdata =  (TDXNodesXPeriodDataForJFC) tdxnode.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		//在前面处理周线成交量等数据的时候，已经存了OHLC的数据，都是0，现在要把这个数据找到，是情况处理
		Integer spcohlcdataindex = nodexdata.getIndexOfSpecificDateOHLCData(friday, 0);
		if(spcohlcdataindex != null) {
			OHLCItem wkohlcdata = nodexdata.getSpecificDateOHLCData(friday,0);
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
	 * 
	 */
	public void refreshDZHGaiNianBanKuaiFromExportFileToDatabase ()
	{
		Set<String> dzhbksetindb = this.getDZHBanKuaiNameSet ("BK");
		
		Set<String> dzhbksetinfile = new HashSet<> ();
		//
		try {
//			System.out.println("大智慧板块文件路径：" + sysconfig.getDZHShangHaiBanKuaiNameFile ());
			File dzhbkfile = new File(sysconfig.getDZHShangHaiBanKuaiNameFile ());
			String line; BufferedReader reader;
			reader = new BufferedReader(new FileReader(dzhbkfile.getAbsolutePath() ));
			line = reader.readLine();
			while (line != null) {
				List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);
           		try {
	            			String bkcode = tmplinelist.get(0).trim();
//	            			System.out.println(bkcode + "   ");
	            			if( ! sysconfig.isShangHaiZhiShu(bkcode))   {
//	            				System.out.println(bkcode + " is not shanghaizshishu .  ");
	            				line = reader.readLine();
	            				continue;
	            			}
	            			
	            			String bkname=null ; if(tmplinelist.size() >1 ) bkname = tmplinelist.get(1);
	            			String sqlinsertstat = "INSERT INTO  大智慧板块列表(板块ID,板块名称,指数所属交易所) values ("
	        	   						+ " '" + bkcode.trim() + "'" + ","
	        	   						+ " '" + bkname.trim() + "'" + ","
	        	   						+ " 'BK'"
	        	   						+ ")"
	        	   						+ " ON DUPLICATE KEY UPDATE "
	        	   						+ "板块名称 =" + " '" + bkname.trim() + "'" 
	        	   						+ ", 指数所属交易所 = 'BK'"
	        	   						;
	            				int autoIncKeyFromApi = tdxconnectdb.sqlInsertStatExecute(sqlinsertstat);
        	   				
	            				dzhbksetinfile.add(bkcode);
           		} catch(SQLException e) {e.printStackTrace();
           		} catch (Exception e) {e.printStackTrace();	}
				// read next line
				line = reader.readLine();
			}
			reader.close(); dzhbkfile= null;
		} catch (IOException e) {e.printStackTrace(); }
		
		SetView<String> differencebankuaiold = com.google.common.collect.Sets.difference( dzhbksetindb, dzhbksetinfile );
//		System.out.println("dzhbksetindb" + dzhbksetindb.toString() + "\r\n");
//		System.out.println("dzhbksetinfile" + dzhbksetinfile.toString() + "\r\n");
//		System.out.println("differencebankuaiold" + differencebankuaiold.toString() + "\r\n");
		for(String oldbkcode : differencebankuaiold) {
				 String sqldeletetstat = "DELETE  FROM  大智慧板块列表 WHERE "
						 				+ " 板块ID =" + " '" + oldbkcode.trim() + "'" 
						 				;
	   			try {
					int autoIncKeyFromApi = tdxconnectdb.sqlDeleteStatExecute(sqldeletetstat);
//					System.out.println("从数据库中删除" + oldbkcode.trim());
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();	}
		}
		differencebankuaiold = null;
		 
		 return;
	}
	public void refreshDZHShangHaiZhiShuAndGaiNianBanKuaiFromExportFileToDatabase ()
	{
		Set<String> dzhbksetindb = this.getDZHBanKuaiNameSet ("SH");
		
		Set<String> dzhbksetinfile = new HashSet<> ();
//		//
//		try {
//			System.out.println("大智慧板块文件路径：" + sysconfig.getDZHShangHaiBanKuaiNameFile ());
//			File dzhbkfile = new File(sysconfig.getDZHShangHaiBanKuaiNameFile ());
//			String line; BufferedReader reader;
//			reader = new BufferedReader(new FileReader(dzhbkfile.getAbsolutePath() ));
//			line = reader.readLine();
//			while (line != null) {
//				List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);
//           		try {
//	            			String bkcode = tmplinelist.get(0).trim();
//	            			if( ! sysconfig.isShangHaiZhiShu(bkcode))   {
//	            				line = reader.readLine();
//	            				continue;
//	            			}
//	            			
//	            			String bkname=null ; if(tmplinelist.size() >1 ) bkname = tmplinelist.get(1);
//	            			String sqlinsertstat = "INSERT INTO  大智慧板块列表(板块ID,板块名称,指数所属交易所) values ("
//	        	   						+ " '" + bkcode.trim() + "'" + ","
//	        	   						+ " '" + bkname.trim() + "'" + ","
//	        	   						+ " 'SH'"
//	        	   						+ ")"
//	        	   						+ " ON DUPLICATE KEY UPDATE "
//	        	   						+ "板块名称 =" + " '" + bkname.trim() + "'" 
//	        	   						+ ", 指数所属交易所 = 'SH'"
//	        	   						;
//	            				int autoIncKeyFromApi = tdxconnectdb.sqlInsertStatExecute(sqlinsertstat);
//        	   				
//	            				dzhbksetinfile.add(bkcode);
//           		} catch(SQLException e) {e.printStackTrace();
//           		} catch (Exception e) {e.printStackTrace();	}
//				// read next line
//				line = reader.readLine();
//			}
//			reader.close(); dzhbkfile= null;
//		} catch (IOException e) {e.printStackTrace(); }
		//
		
		try {
			File dzhshzsfile = new File(sysconfig.getDZHShangHaiZhiShuNameFile ());
			String line; BufferedReader readerdzhshzs;
			readerdzhshzs = new BufferedReader(new FileReader(dzhshzsfile.getAbsolutePath() ));
			line = readerdzhshzs.readLine();
			while (line != null) {
				List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);
           		try {
	            			String bkcode = tmplinelist.get(0).trim();
	            			if( ! sysconfig.isShangHaiZhiShu(bkcode))   {
	            				line = readerdzhshzs.readLine();
	            				continue;
	            			}
	            			
	            			String bkname=null ; if(tmplinelist.size() >1 ) bkname = tmplinelist.get(1);
            				String sqlinsertstat = "INSERT INTO  大智慧板块列表(板块ID,板块名称,指数所属交易所) values ("
	        	   						+ " '" + bkcode.trim() + "'" + ","
	        	   						+ " '" + bkname.trim() + "'" + ","
	        	   						+ " 'SH'" 
	        	   						+ ")"
	        	   						+ " ON DUPLICATE KEY UPDATE "
	        	   						+ "板块名称 =" + " '" + bkname.trim() + "'"
	        	   						+ ", 指数所属交易所 = 'SH'"
	        	   						;
            				int autoIncKeyFromApi = tdxconnectdb.sqlInsertStatExecute(sqlinsertstat);
        	   				
            				dzhbksetinfile.add(bkcode);
           		} catch(SQLException e) {e.printStackTrace();
           		} catch (Exception e) {e.printStackTrace();	}
				
				// read next line
				line = readerdzhshzs.readLine();
			}
			readerdzhshzs.close(); dzhshzsfile = null;
		} catch (IOException e) {e.printStackTrace(); }

		
		SetView<String> differencebankuaiold = com.google.common.collect.Sets.difference( dzhbksetindb, dzhbksetinfile );
//		System.out.println("dzhbksetindb" + dzhbksetindb.toString() + "\r\n");
//		System.out.println("dzhbksetinfile" + dzhbksetindb.toString() + "\r\n");
//		System.out.println("dzhbksetinfile" + differencebankuaiold.toString() + "\r\n");
		for(String oldbkcode : differencebankuaiold) {
				 String sqldeletetstat = "DELETE  FROM  大智慧板块列表 WHERE "
						 				+ " 板块ID =" + " '" + oldbkcode.trim() + "'" 
						 				;
	   			try {
					int autoIncKeyFromApi = tdxconnectdb.sqlDeleteStatExecute(sqldeletetstat);
//					System.out.println("从数据库中删除" + oldbkcode.trim());
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();	}
		}
		differencebankuaiold = null;
		 
		 return;
	}
	/*
	 * 
	 */
	public Set<String> getDZHBanKuaiNameSet (String jys) 
	{
		CachedRowSetImpl rsagu = null;
		Set<String> dzhbkset = new HashSet<> ();
		try {
			 String sqlquerystat = "SELECT  *  FROM 大智慧板块列表   WHERE 指数所属交易所 = '" + jys + "'";
			rsagu = tdxconnectdb.sqlQueryStatExecute(sqlquerystat);
			while(rsagu.next()) {
				String dzhbk = rsagu.getString("板块ID"); 
				dzhbkset.add(dzhbk.trim());
			}
		} catch (Exception e) { e.printStackTrace();
		} finally {
			try {	rsagu.close();} catch (SQLException e) {	e.printStackTrace();}
			rsagu = null;
		}
		
		return dzhbkset;
	}
	/*
	 * 
	 */
	public List<BanKuai> getDZHBanKuaiList () 
	{
		CachedRowSetImpl rs = null;
		List<BanKuai> dzhbkset = new ArrayList<> ();
		try {
			 String sqlquerystat = "select *  FROM 大智慧板块列表   "
		 			;
			rs = tdxconnectdb.sqlQueryStatExecute(sqlquerystat);
			while(rs.next()) {
				String dzhbkcode = rs.getString("板块ID"); 
				String dzhbkname = rs.getString("板块名称");
				
				BanKuai dzhbk = new BanKuai(dzhbkcode,dzhbkname, "DZH");
				dzhbk.getNodeJiBenMian().setSuoShuJiaoYiSuo(rs.getString("指数所属交易所"));
//				dzhbk.setBanKuaiLeiXing( rs.getString("板块类型描述") );
				dzhbk.getBanKuaiOperationSetting().setExporttogehpi(rs.getBoolean("导出Gephi"));
				dzhbk.getBanKuaiOperationSetting().setImportdailytradingdata(rs.getBoolean("导入交易数据"));
				dzhbk.getBanKuaiOperationSetting().setShowinbkfxgui(rs.getBoolean("板块分析"));
				dzhbk.getBanKuaiOperationSetting().setShowincyltree(rs.getBoolean("产业链树"));
				dzhbk.getBanKuaiOperationSetting().setExportTowWlyFile(rs.getBoolean("周分析文件"));
				dzhbk.getBanKuaiOperationSetting().setImportBKGeGu(rs.getBoolean("导入板块个股"));
				dzhbk.getBanKuaiOperationSetting().setBanKuaiLabelColor(rs.getString("DefaultCOLOUR"));

				dzhbkset.add(dzhbk);
			}
		} catch (Exception e) { e.printStackTrace();
		} finally {
			try {	rs.close();	} catch (SQLException e) {	e.printStackTrace();}
			rs = null;
		}
		
		return dzhbkset;
	}
	/*
	 * 
	 */
	public Stock getDZHBanKuaiForAStock(Stock stock) 
	{
		if(stock.getGeGuCurSuoShuDZHSysBanKuaiList() != null && !stock.getGeGuCurSuoShuDZHSysBanKuaiList().isEmpty() )
			return  stock;
		
		BanKuaiAndStockTree treeofstkbk = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks() ;
		String stockcode = stock.getMyOwnCode();
		Set<BkChanYeLianTreeNode> stockbanks = new HashSet<>();

		String sqlquerystat =null;
		sqlquerystat=  "SELECT *  \r\n" + 
						"FROM 大智慧股票概念板块对应表 gnb\r\n" + 
						"WHERE gnb.`股票代码` = '" + stockcode + "'  AND ISNULL(gnb.`移除时间`)			"
						;

		CachedRowSetImpl rs_gn = tdxconnectdb.sqlQueryStatExecute(sqlquerystat);
		try  {     
	        while(rs_gn.next()) {
	        	String bkcode = rs_gn.getString("板块代码");
	        	Integer quanzhong = rs_gn.getInt("股票权重");
	        	BanKuai bk = (BanKuai)treeofstkbk.getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.DZHBK);
	        	if(bk == null) continue;
	        	try {
	        		StockOfBanKuai bkofst = new StockOfBanKuai(bk,stock);
	        		bkofst.setStockQuanZhong (quanzhong);
					bk.addNewBanKuaiGeGu(bkofst);
		        	stockbanks.add(bk);
	        	} catch(java.lang.NullPointerException e) { 		e.printStackTrace();}
	        } 
	    } catch(java.lang.NullPointerException e){	e.printStackTrace();
	    } catch(Exception e) {e.printStackTrace();
	    } finally {
	    	if(rs_gn != null) {	try {rs_gn.close();	} catch (SQLException e) {	e.printStackTrace();}
	    		rs_gn = null;   }	
	    }
//	    Collections.sort (stockbanks);
		stock.setGeGuCurSuoShuDZHSysBanKuaiList(stockbanks);
		
		return stock;
	}
	/*
	 * 
	 */
	public void updateBanKuaiOperationsSettings(BkChanYeLianTreeNode node, boolean importdailydata, boolean exporttogephi, 
			boolean showinbkfx,boolean showincyltree, boolean exporttowkfile, boolean importbkgg, Color bkcolor, boolean corezhishu)
	{
		String colorcode = String.format("#%02x%02x%02x", bkcolor.getRed(), bkcolor.getGreen(), bkcolor.getGreen() );
		
		String sqlupdatestat = "UPDATE 大智慧板块列表  SET " +
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
   		try {	int autoIncKeyFromApi = tdxconnectdb.sqlUpdateStatExecute(sqlupdatestat);
		} catch (MysqlDataTruncation e) {e.printStackTrace();
		} catch (SQLException e) {e.printStackTrace();}
	}
	/*
	 * 
	 */
	public BanKuai getDZHBanKuaiGeGuOfHyGnFg(BanKuai bankuai, LocalDate bkstartday, LocalDate bkendday)
	{
		String bkcode = bankuai.getMyOwnCode();

		String sqlquerystat =null;
		sqlquerystat=  "SELECT *  \r\n" + 
						"FROM 大智慧股票概念板块对应表 gnb\r\n" + 
						"WHERE gnb.`板块代码` = '" + bkcode + "'  AND ISNULL(gnb.`移除时间`)			"
						;

		CachedRowSetImpl rs1 = tdxconnectdb.sqlQueryStatExecute(sqlquerystat);
		try  {     
	        while(rs1.next()) {
	        	String tmpstockcode = rs1.getString("股票代码");
	        	Integer weight = rs1.getInt("股票权重");
				Boolean longtou;
				try{	longtou = rs1.getBoolean("板块龙头");
				} catch (java.sql.SQLException e) {longtou = false;}
				
				LocalDate joindate = null;
				try{	joindate = rs1.getDate("加入时间").toLocalDate();
				} catch (java.sql.SQLException ex1) {continue; }
				LocalDate leftdate;
				try {		leftdate = rs1.getDate("移除时间").toLocalDate().with(DayOfWeek.FRIDAY); //周线都是以周五为计算的，任何个股移出都调整到周五
				} catch (java.lang.NullPointerException e) {leftdate = LocalDate.parse("3000-01-01");}
				
				DateTime joindt= new DateTime(joindate.getYear(), joindate.getMonthValue(), joindate.getDayOfMonth(), 0, 0, 0, 0);
				DateTime leftdt = new DateTime(leftdate.getYear(), leftdate.getMonthValue(), leftdate.getDayOfMonth(), 0, 0, 0, 0);
				Interval joinleftinterval = null ;
				try {		joinleftinterval = new Interval(joindt, leftdt);
				} catch (Exception e) {joinleftinterval = new Interval(leftdt,joindt );}
	        	
	        	if(bankuai.getStockOfBanKuai(tmpstockcode) != null ) {//已经有了
	        		StockOfBanKuai bkofst =  bankuai.getStockOfBanKuai(tmpstockcode);
					bkofst.setStockQuanZhong(weight);
					bkofst.setBkLongTou(longtou);
					bkofst.addInAndOutBanKuaiInterval(joinleftinterval);
	        	} else {
	        		BanKuai bk = (BanKuai)CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.DZHBK);
	        		if(bk == null) continue;
		        	Stock stock = (Stock)CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(tmpstockcode,BkChanYeLianTreeNode.TDXGG);
		        	if(stock == null) continue;
					StockOfBanKuai bkofst = new StockOfBanKuai(bk,stock);
					bkofst.setStockQuanZhong (weight);
					bkofst.addInAndOutBanKuaiInterval(joinleftinterval);
					bk.addNewBanKuaiGeGu(bkofst);
	        	}
	        } 
	        
	    } catch(java.lang.NullPointerException e){	e.printStackTrace();
	    } catch(Exception e) {e.printStackTrace();
	    } finally {
	    	if(rs1 != null) {	try {rs1.close();	} catch (SQLException e) {	e.printStackTrace();}
	    		rs1 = null;   }	
	    }
		
		return bankuai;
	}
	/*
	 * 
	 */
	private void setTDXBanKuaiGeGuDuiYingBiao (String sqlquerystat1)
	{
//		CachedRowSetImpl rsdm = connectdb.sqlQueryStatExecute(sqlquerystat1);
//		try {
//			while(rsdm.next()) {
//	    		 String bkcode = rsdm.getString("板块代码"); //mOST_RECENT_TIME
//	    		 BanKuai bk = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
//	    		 if(bk == null) 
//	    			 continue;
//	    		 try { String dyb = rsdm.getString("DYB");
//	    		 		bk.getShuJuJiLuInfo().setGuPiaoBanKuaiDuiYingBiao(dyb);
//	    		 } catch (java.lang.NullPointerException ex) {}
//	    	}
//		} catch(java.lang.NullPointerException e) {	e.printStackTrace();
//	    } catch (SQLException e) {	e.printStackTrace();
//	    } catch(Exception e){	e.printStackTrace();
//	    } finally {
//	    	if(rsdm != null)
//			try {rsdm.close();rsdm = null;
//			} catch (SQLException e) {	e.printStackTrace();}
//	    }
//		
//		return;
	}
	/*
	 * 
	 */
	public void getDZHBanKuaiGeGuDuiYingBiaoByJiaoYiSuo (String jiaoyisuo)
	{
//		Collection<BkChanYeLianTreeNode> bkjys = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.DZHBK, jiaoyisuo);
//		String bkcodes = "";
//		for(BkChanYeLianTreeNode tmpbk : bkjys)
//			bkcodes = bkcodes + tmpbk.getMyOwnCode() + ",";
//		bkcodes = bkcodes.substring(0, bkcodes.length()-2);
//		
//		String sqlquerystat1 =  
//				" SELECT 板块代码, COUNT(*) AS RESULT , '股票通达信概念板块对应表'  AS DYB FROM 股票通达信概念板块对应表\r\n" + 
//				" WHERE  板块代码 IN (" +  bkcodes + ")\r\n" + 
//				" GROUP BY 板块代码  \r\n"  
////				" + UNION \r\n" 
//				; 
//		String sqlquerystat2 =		" SELECT 板块代码, COUNT(*) AS RESULT ,'股票通达信风格板块对应表' AS DYB FROM 股票通达信风格板块对应表\r\n" + 
//				" WHERE  板块代码 IN (" +  bkcodes + ")\r\n" + 
//				" GROUP BY 板块代码  \r\n"  
////				"+  UNION \r\n" 
//				; 
//				
//		String sqlquerystat3 =			" SELECT 板块代码, COUNT(*) AS RESULT , '股票通达信行业板块对应表'   AS DYB FROM  股票通达信行业板块对应表\r\n" + 
//				" WHERE  板块代码 IN (" +  bkcodes + ")\r\n" + 
//				" GROUP BY 板块代码  \r\n"  
////				" + UNION \r\n" 
//				; 
//		String sqlquerystat4 =			"  SELECT 板块代码, COUNT(*) AS RESULT , '股票通达信交易所指数对应表'  AS DYB FROM 股票通达信交易所指数对应表\r\n" + 
//				" WHERE  板块代码 IN (" +  bkcodes + ") \r\n" +  
//				" GROUP BY 板块代码"
//				;
//		String sqlquerystat = sqlquerystat1 + sqlquerystat2 + sqlquerystat3 + sqlquerystat4;
//		setTDXBanKuaiGeGuDuiYingBiao (sqlquerystat1);
//		setTDXBanKuaiGeGuDuiYingBiao (sqlquerystat2);
//		setTDXBanKuaiGeGuDuiYingBiao (sqlquerystat3);
//		setTDXBanKuaiGeGuDuiYingBiao (sqlquerystat4);
		
	}
	/*
	 * 
	 */
	private void getTDXBanKuaiShuJuJiLuMaxMinDateByJiaoYiSuo (String jiaoyisuo)
	{
		String cjltablename;
		if(jiaoyisuo.equalsIgnoreCase("BK") )	cjltablename = "大智慧板块每日交易信息";
		else 	return;
		
		String sqlquerystat = "SELECT * FROM \r\n" + 
				"(SELECT 板块ID  FROM 大智慧板块列表\r\n" + 
				"WHERE 指数所属交易所 = '" + jiaoyisuo + "' ) agu\r\n" + 
				"\r\n" + 
				"LEFT JOIN \r\n" + 
				"( SELECT 代码, MIN(交易日期)  minjytime , MAX(交易日期)  maxjytime FROM " + cjltablename + "\r\n" + 
				"  GROUP BY 代码) shjyt\r\n" + 
				" ON agu.板块ID =  shjyt.`代码`\r\n" + 
				""
				;
		CachedRowSetImpl rsdm = tdxconnectdb.sqlQueryStatExecute(sqlquerystat);
		try { 	
	    	while(rsdm.next()) {
		    		 String bkcode = rsdm.getString("板块ID"); //mOST_RECENT_TIME
		    		 BanKuai bk = (BanKuai) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.DZHBK);
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
	 *	https://blog.csdn.net/zjbo_123/article/details/6640807 
	 */
	public File refreshDZHBanKuaiVolAmoToDbBulkImport (String jiaoyisuo, String datafilepath, int bulkcount)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步板块成交量报告.tmp");
		
		File datfile = new File (datafilepath);
		if (!datfile.exists() || datfile.isDirectory() || !datfile.canRead())
			return null;
		
		getTDXBanKuaiShuJuJiLuMaxMinDateByJiaoYiSuo (jiaoyisuo);
		
		String cjltablename = null;
		if(jiaoyisuo.equalsIgnoreCase("bk"))	cjltablename = "大智慧板块每日交易信息";
		
		int nodecount = 0; List<String> nodeinsertdata = new ArrayList<>(); 
		 BufferedInputStream dis = null;  FileInputStream in = null;
		 try {
			    in = new FileInputStream(datfile);  
			    dis = new BufferedInputStream(in);
              
              int fileheadbytenumber = 4;
              byte[] itemBuf = new byte[fileheadbytenumber];
              dis.read(itemBuf, 0, fileheadbytenumber); 

              byte[] itemBuf2 = new byte[fileheadbytenumber];
              dis.read(itemBuf2, 0, fileheadbytenumber); 
              
              byte[] itemBuf3 = new byte[fileheadbytenumber];
              dis.read(itemBuf3, 0, fileheadbytenumber); 
              ByteBuffer bb = ByteBuffer.wrap(itemBuf3);
              IntBuffer ib = bb.asIntBuffer();
              int i0 = ib.get(0);
              
              byte[] itemBuf4 = new byte[fileheadbytenumber];
              dis.read(itemBuf4, 0, fileheadbytenumber); 
              
              byte[] itemBufseperator = new byte[4];
              Boolean foundanewstock = false; Integer countseperator = 16; List<Integer> seperatorlist = new ArrayList<> (); 
              ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
              while(dis.read(itemBufseperator, 0, 4) != -1) {
           	   if(itemBufseperator[0] == -1 &&  itemBufseperator[1] == -1 && itemBufseperator[2] == -1 && itemBufseperator[3] == -1 ) {
           		   seperatorlist.add(countseperator);
           		   countseperator = countseperator +  4;
           		   
           		   if(seperatorlist.size() ==1) outputStream.write( itemBufseperator );
           		   else foundanewstock = true;
           	   } else {
           		   countseperator = countseperator +  4;
           		   outputStream.write( itemBufseperator );
           	   }
           	   
           	   if(foundanewstock) {
           		   byte astockrecords[] = outputStream.toByteArray( );
           		   
                      String nodecodebyte =new String(astockrecords,4,8);
                      String nodecode = nodecodebyte.substring(2, nodecodebyte.length());
                      BanKuai bk = (BanKuai) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.DZHBK);
                      if(bk == null || !bk.getBanKuaiOperationSetting().isImportdailytradingdata())	{
                    	  outputStream.reset();
                          outputStream.write( itemBufseperator );
                          foundanewstock = false;
                    	  continue; 
                      } 

					 nodecount ++;
                      
                      LocalDate ldlastestdbrecordsdate = bk.getShuJuJiLuInfo().getJyjlmaxdate();
                      if(ldlastestdbrecordsdate == null)	ldlastestdbrecordsdate = LocalDate.now().minus(100,ChronoUnit.YEARS);
                      
                      int blocksize = 32; int startposition = 32;
                      while(startposition < astockrecords.length ) {
                    	  if( (nodecount == bulkcount  && !nodeinsertdata.isEmpty() ) || nodeinsertdata.size()> bulkcount * 4  ) {
     		   				 setNodeDataToDataBase (cjltablename, nodeinsertdata);
     		   				 nodecount = 0; nodeinsertdata.clear();
                    	  }
                    	  
	                   	   byte astockdailyrecord[] = new byte[32]; 
	                   	   System.arraycopy(astockrecords, startposition, astockdailyrecord, 0, blocksize);
	                   	   
	                   	   String binary=toBin(astockdailyrecord[3])+toBin(astockdailyrecord[2])+toBin(astockdailyrecord[1])+toBin(astockdailyrecord[0]);
	                       long unixSeconds=stringToInt(binary);
	                       Date date = new Date(unixSeconds*1000L); 
//	                       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
//	                       sdf.setTimeZone(TimeZone.getDefault()); 
//	                       String formattedDate = sdf.format(date);
	                       LocalDate recordlocaldate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	                       if(recordlocaldate.isBefore(ldlastestdbrecordsdate) || recordlocaldate.isEqual(ldlastestdbrecordsdate)) {
	                    	   startposition = startposition + 32;
	                    	   continue;
	                       }
	                       
	                       byte[] openbyte = {astockdailyrecord[4],astockdailyrecord[5],astockdailyrecord[6],astockdailyrecord[7] };
	                       float open = ByteBuffer.wrap(openbyte).order(ByteOrder.LITTLE_ENDIAN).getFloat();  
	                       byte[] highbyte = {astockdailyrecord[8],astockdailyrecord[9],astockdailyrecord[10],astockdailyrecord[11] };
	                       float high = ByteBuffer.wrap(highbyte).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	                       byte[] lowbyte = {astockdailyrecord[12],astockdailyrecord[13],astockdailyrecord[14],astockdailyrecord[15] };
	                       float low = ByteBuffer.wrap(lowbyte).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	                       byte[] closebyte = {astockdailyrecord[16],astockdailyrecord[17],astockdailyrecord[18],astockdailyrecord[19] };
	                       float close = ByteBuffer.wrap(closebyte).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	                       byte[] volbyte = {astockdailyrecord[20],astockdailyrecord[21],astockdailyrecord[22],astockdailyrecord[23] };
	                       float vol = ByteBuffer.wrap(volbyte).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	                       byte[] amobyte = {astockdailyrecord[24],astockdailyrecord[25],astockdailyrecord[26],astockdailyrecord[27] };
	                       float amo = ByteBuffer.wrap(amobyte).order(ByteOrder.LITTLE_ENDIAN).getFloat();

	                       String sqlinsertstat = "(" 
           						+ "'" + bk.getMyOwnCode().trim() + "'" + ","
           						+ "'" +  recordlocaldate + "'" + ","
           						+ "'" +  open + "'" + "," 
           						+ "'" +  high + "'" + "," 
           						+ "'" +  low + "'" + "," 
           						+ "'" +  close + "'" + "," 
           						+ "'" +  vol + "'" + "," 
           						+ "'" +  amo + "'"  
           						+ "), \r\n"
           						;
	                       nodeinsertdata.add(sqlinsertstat);

	                       startposition = startposition + 32;
                      }
                      
                      outputStream.reset();
                      outputStream.write( itemBufseperator );
                      foundanewstock = false;
           	   }
              }
              setNodeDataToDataBase (cjltablename, nodeinsertdata);
		 } catch (Exception e) { e.printStackTrace();	 return null;
		 } finally {
			 try { in.close();	} catch (IOException e) {e.printStackTrace();}
            try { dis.close(); } catch (IOException e) {e.printStackTrace();}     
		 }

		 return tmprecordfile;
	}
	
	private List<String> setVolAmoRecordsFromFileToDatabaseBulkImport (BkChanYeLianTreeNode tmpnode, File tmpbkfile, LocalDate lastestdbrecordsdate,  List<String> nodeinsertdata, File tmprecordfile)
	{return null;}
	/*
	 * 
	 */
	private void setNodeDataToDataBase (String optable, List<String> inserteddata) 
	{
		if(inserteddata.isEmpty())
			return;
		
		String insertstr = "";
		for(String nodedata : inserteddata) 
			insertstr = insertstr + nodedata;
		String sqlinsertstat = null;
		sqlinsertstat = "INSERT INTO " + optable +"(代码,交易日期,开盘价,最高价,最低价,收盘价,成交量,成交额)"
									+ " values \r\n" 
									+ insertstr
									;
		
		sqlinsertstat = sqlinsertstat.trim().substring(0, sqlinsertstat.trim().length() - 1);
		try {
			int autoIncKeyFromApi = tdxconnectdb.sqlInsertStatExecute(sqlinsertstat);
		} catch (SQLException e) {e.printStackTrace();}
		
		return;
	}
	/*
	 * 
	 */
	public int refreshDZHBanKuaiDailyData(File tmprecordfile)
	{
		File file = new File("E:/SUPERSTK2.DAD" );
		if(!file.exists() ) return -1;
		
		 int addednumber =0;
		 BufferedInputStream dis = null;  FileInputStream in = null;
		 try {
			    in = new FileInputStream(file);  
			    dis = new BufferedInputStream(in);
               
               int fileheadbytenumber = 4;
               byte[] itemBuf = new byte[fileheadbytenumber];
               dis.read(itemBuf, 0, fileheadbytenumber); 

               byte[] itemBuf2 = new byte[fileheadbytenumber];
               dis.read(itemBuf2, 0, fileheadbytenumber); 
               
               byte[] itemBuf3 = new byte[fileheadbytenumber];
               dis.read(itemBuf3, 0, fileheadbytenumber); 
               ByteBuffer bb = ByteBuffer.wrap(itemBuf3);
               IntBuffer ib = bb.asIntBuffer();
               int i0 = ib.get(0);
               
               byte[] itemBuf4 = new byte[fileheadbytenumber];
               dis.read(itemBuf4, 0, fileheadbytenumber); 
               
               byte[] itemBufseperator = new byte[4];
               Boolean foundanewstock = false; Integer countseperator = 16; List<Integer> seperatorlist = new ArrayList<> (); 
               ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
               while(dis.read(itemBufseperator, 0, 4) != -1) {
            	   if(itemBufseperator[0] == -1 &&  itemBufseperator[1] == -1 && itemBufseperator[2] == -1 && itemBufseperator[3] == -1 ) {
            		   seperatorlist.add(countseperator);
            		   countseperator = countseperator +  4;
            		   
            		   if(seperatorlist.size() ==1) outputStream.write( itemBufseperator );
            		   else foundanewstock = true;
            	   } else {
            		   countseperator = countseperator +  4;
            		   outputStream.write( itemBufseperator );
            	   }
            	   
            	   if(foundanewstock) {
            		   byte astockrecords[] = outputStream.toByteArray( );
            		   
                       String nodecode =new String(astockrecords,4,8);
                       System.out.print(nodecode);
                       int blocksize = 32; int startposition = 32;
                       while(startposition < astockrecords.length ) {
                    	   byte astockdailyrecord[] = new byte[32]; 
                    	   System.arraycopy(astockrecords, startposition, astockdailyrecord, 0, blocksize);
                    	   
                    	   String binary=toBin(astockdailyrecord[3])+toBin(astockdailyrecord[2])+toBin(astockdailyrecord[1])+toBin(astockdailyrecord[0]);
                           long unixSeconds=stringToInt(binary);
                           Date date = new Date(unixSeconds*1000L); 
                           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                           sdf.setTimeZone(TimeZone.getDefault()); 
                           String formattedDate = sdf.format(date);
                           System.out.println(formattedDate); 
                    	   
	                       byte[] openbyte = {astockdailyrecord[4],astockdailyrecord[5],astockdailyrecord[6],astockdailyrecord[7] };
	                       float open = ByteBuffer.wrap(openbyte).order(ByteOrder.LITTLE_ENDIAN).getFloat();  
	                       byte[] highbyte = {astockdailyrecord[8],astockdailyrecord[9],astockdailyrecord[10],astockdailyrecord[11] };
	                       float high = ByteBuffer.wrap(highbyte).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	                       
	                       byte[] lowbyte = {astockdailyrecord[12],astockdailyrecord[13],astockdailyrecord[14],astockdailyrecord[15] };
	                       float low = ByteBuffer.wrap(lowbyte).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	                       
	                       byte[] closebyte = {astockdailyrecord[16],astockdailyrecord[17],astockdailyrecord[18],astockdailyrecord[19] };
	                       float close = ByteBuffer.wrap(closebyte).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	                       
	                       byte[] volbyte = {astockdailyrecord[20],astockdailyrecord[21],astockdailyrecord[22],astockdailyrecord[23] };
	                       float vol = ByteBuffer.wrap(volbyte).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	                       
	                       byte[] amobyte = {astockdailyrecord[24],astockdailyrecord[25],astockdailyrecord[26],astockdailyrecord[27] };
	                       float amo = ByteBuffer.wrap(amobyte).order(ByteOrder.LITTLE_ENDIAN).getFloat();

                    	   //time to store data to database
//	                       String sqlinsertstat = "INSERT INTO  通达信板块列表(板块名称,板块ID,指数所属交易所) values ("
//		   						+ " '" + zhishuname.trim() + "'" + ","
//		   						+ " '" + zhishucode.trim() + "'" + ","
//		   						+ " '" + "sh" + "'"
//		   						+ ")"
//		   						+ " ON DUPLICATE KEY UPDATE "
//		   						+ "板块名称 =" + " '" + zhishuname.trim() + "'" + ","
//		   						+ "指数所属交易所= " + " '" + "sh" + "'"
//		   						;
//			   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//			                Files.append("加入：" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//			   				allinsertbk.add(zhishucode);
		                       
	                       startposition = startposition + 32;
                       }
                       
                       outputStream.reset();
                       outputStream.write( itemBufseperator );
                       foundanewstock = false;
                       
            	   }
               }
               
               try { in.close();	} catch (IOException e) {e.printStackTrace();}
               try { dis.close(); } catch (IOException e) {e.printStackTrace();}
               
                              
//               int sigbk = 18*16+12+14;
//               itemBuf2 = new byte[sigbk];
//               int i=0;
//               while (dis.read(itemBuf2, 0, sigbk) != -1) {
//            	  
//
////            	   String sqlinsertstat = "INSERT INTO  通达信板块列表(板块名称,板块ID,指数所属交易所) values ("
////	   						+ " '" + zhishuname.trim() + "'" + ","
////	   						+ " '" + zhishucode.trim() + "'" + ","
////	   						+ " '" + "sh" + "'"
////	   						+ ")"
////	   						+ " ON DUPLICATE KEY UPDATE "
////	   						+ "板块名称 =" + " '" + zhishuname.trim() + "'" + ","
////	   						+ "指数所属交易所= " + " '" + "sh" + "'"
////	   						;
////	   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//////	                Files.append("加入：" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
////	   				allinsertbk.add(zhishucode);
//               }
               
		 } catch (Exception e) { e.printStackTrace();	 return -1;
		 } finally {
			 try { in.close();	} catch (IOException e) {e.printStackTrace();}
             try { dis.close(); } catch (IOException e) {e.printStackTrace();}     
		 }
		 
		return 1;
	}
	
	public static String toBin(byte numDec){

	    int num= UnsignedBytes.toInt(numDec);
	    if(num==0)
	            return "00000000";

	    String result=Integer.toBinaryString(num);
	    int cont= 8-result.length()%8;
	    if(cont!=8){
	        for(int i=0; i<cont;i++){
	            result="0"+result;
	        }
	    }
	        return result;  }

	public static long stringToInt(String bin){

	      long result=0;
	      bin= bin.trim();
	      for(int i=bin.length()-1;i>=0;i--){
	          String aux= String.valueOf(bin.charAt(i));          
	          result+= Integer.parseInt(aux)* Math.pow(2, (bin.length()-1-i));
	      }
	      return result;
	}
}

