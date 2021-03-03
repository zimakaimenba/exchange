package com.exchangeinfomanager.database;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.ExternalNewsType.ChangQiGuanZhu;
import com.exchangeinfomanager.News.ExternalNewsType.DuanQiGuanZhu;
import com.exchangeinfomanager.News.ExternalNewsType.ExternalNewsType;
import com.exchangeinfomanager.News.ExternalNewsType.InsertedExternalNews;
import com.exchangeinfomanager.News.ExternalNewsType.QiangShi;
import com.exchangeinfomanager.News.ExternalNewsType.RuoShi;
import com.exchangeinfomanager.News.ExternalNewsType.ZhiShuBoLang;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

import com.google.common.base.Strings;
import com.sun.rowset.CachedRowSetImpl;


import java.awt.*;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Class for accessing database.
 */
@SuppressWarnings("all")
public final class StockCalendarAndNewDbOperation 
{

    private static Logger logger = Logger.getLogger(StockCalendarAndNewDbOperation.class);

    public StockCalendarAndNewDbOperation() {
    	initializeDb ();
//		initialzieSysconf ();
	}
	
	private  ConnectDataBase connectdb;
//	private  SystemConfigration sysconfig;
		
	

	private void initializeDb() 
	{
		connectdb = ConnectDataBase.getInstance();
	}
//	private void initialzieSysconf ()
//	{
//		sysconfig = SystemConfigration.getInstance();
//	}
	/*
	 * 
	 */
	public Collection<News> getZhiShuKeyDates (String bankuaiid,LocalDate startdate, LocalDate enddate)
	{
		Collection<News> meetings = new ArrayList<>();
		
		BanKuaiAndStockTree treeofbkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		 //找出指数关键日期  
		CachedRowSetImpl rspd = null;
		try {
			String sqlquerystat = null;
			if("ALL".equals(bankuaiid.toUpperCase()) ) 
				sqlquerystat = "SELECT * FROM 指数关键日期表 \r\n"
		        			+ " WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'"
		        			+ " OR 截至日期 BETWEEN '" + startdate + "' AND '" + enddate + "'"
		        			+ " ORDER BY  日期 DESC"
		        			;
			else
				sqlquerystat = "SELECT * FROM 指数关键日期表 \r\n"
	        			+ " WHERE  ( 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'"
	        			+ " OR 截至日期 BETWEEN '" + startdate + "' AND '" + enddate + "')"
	        			+ " AND 代码 = '" + bankuaiid + "'"
	        			+ " ORDER BY  日期 DESC"
	        			;
	
			 rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			 while(rspd.next())  {
				 int meetingID = rspd.getInt("id");
				 String nodecode = rspd.getString("代码");
				 Integer nodetype = rspd.getInt("类型");
				 LocalDate start = rspd.getDate("日期").toLocalDate(); 
		         java.sql.Date recordenddate = rspd.getDate("截至日期");
		         LocalDate end ;
		         try{
		        	 end = recordenddate.toLocalDate();
		         } catch (java.lang.NullPointerException ex) {
		        	 end =  null; 
		         }
		         String shuoming = rspd.getString("说明");
		         String detail = rspd.getString("详细说明");
		         String url = rspd.getString("URL");
		         String keywords = rspd.getString("关键词");
		         
		         BkChanYeLianTreeNode node = treeofbkstk.getSpecificNodeByHypyOrCode(nodecode, nodetype);
		         
		         ZhiShuBoLang zs = new ZhiShuBoLang(node, shuoming, start, end, detail, keywords, 
		        		 new HashSet<InsertedNews.Label>(), url, "A" );
		         
		         InsertedExternalNews newmeeting = new InsertedExternalNews(zs, meetingID);
			                
		         meetings.add(newmeeting);
			 }
			 
			//label 	 
	  		 for (News m : meetings) {
	        	int meetingid = ((InsertedExternalNews)m).getID();

	        	String area = "指数关键日期";
	        	
	        	sqlquerystat = "SELECT label.* FROM label INNER "
	        					+" JOIN meetingLabel "
	        					+" ON label.LABEL_ID = meetingLabel.LABEL_ID " 
	        					+" WHERE meetingLabel.NEWS_ID = " + meetingid
	        					+" AND meetingLabel.fromtable = " + "'" + area + "'"
	        					;
	        	
	        	CachedRowSetImpl set = connectdb.sqlQueryStatExecute(sqlquerystat);
	        	
	            while (set.next()) {
	                int labelID = set.getInt("LABEL_ID");
	                String name = set.getString("NAME");
	                Color colour = Color.decode(set.getString("COLOUR"));
	                boolean active = set.getBoolean("ACTIVE");
	                InsertedNews.Label label = new InsertedNews.Label(new News.Label(name, colour), labelID);
	                m.getLabels().add(label);
	            }
	            
	            set.close();
	            set = null;
	        }
			
		 }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	if(rspd != null)
				try {
					rspd.close();
					rspd = null;
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }
	    
		 
		 return meetings;
	}
//	public void refactoryNews ()
//	{
//		String sqlquerystat = "SELECT * FROM 商业新闻  \r\n"
//				+ "  where  关联板块  LIKE '%gzgzgz%' " //长期记录 
//				+ " ORDER BY 录入日期 DESC"
//				;
//		CachedRowSetImpl result = null;
//		try {
//			result = connectdb.sqlQueryStatExecute(sqlquerystat);
//   		 	while(result.next()) {
//	   		 	int meetingID = result.getInt("news_id");
//		        java.sql.Date recorddate = result.getDate("录入日期"); 
//	            LocalDate start = recorddate.toLocalDate();
//	            String title = result.getString("新闻标题");
//	            String description = result.getString("具体描述");
//	            if(Strings.isNullOrEmpty(description))
//	            	description = "描述";
//	            
//	            String sql = "INSERT INTO 关注个股板块表(代码,类型,关注类型,日期,说明,具体描述) VALUES("
//	            		+ "'999999', 4, false, '" + start + "', '" + title + "'," + "'" + description + "'"
//	            		+ ")";
//	            
//	            connectdb.sqlInsertStatExecute(sql);
//	            
//	            sql = "DELETE FROM 商业新闻 WHERE news_id = " + meetingID;
//	            connectdb.sqlDeleteStatExecute(sql);
//	            
//	            sql = "DELETE FROM meetinglabel where news_ID = " + meetingID + " AND fromtable = '商业新闻'";
//	            connectdb.sqlDeleteStatExecute(sql);
//	         }
//		}catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    } catch (SQLException e) {
//	    	e.printStackTrace();
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    }  finally {
//	    	if(result != null)
//				try {
//					result.close();
//					result = null;
//					
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//	    }
//
//	}
	/*
	 * 
	 */
	public Collection<News> getNodeRelatedNews(String bankuaiid,LocalDate startdate, LocalDate enddate) 
	{
		List<News> meetings = new ArrayList<> ();
		
		String timerangesql = null ;
		if(startdate == null && enddate != null) {
			timerangesql = "WHERE 录入日期  < '" + enddate + "' \r\n" ;
		} else if(startdate != null && enddate == null) {
			timerangesql = "WHERE 录入日期 > '" + startdate + "' \r\n" ;
		} else if(startdate != null && enddate != null) {
			timerangesql = " WHERE 录入日期  BETWEEN '" + startdate + "' AND '" + enddate + "' \r\n";
		}
		
		String sqlquerystat = null;
		if("ALL".equals(bankuaiid.toUpperCase()) ) 
			sqlquerystat = "SELECT * FROM 商业新闻  \r\n"
							+ timerangesql + "\r\n" 
							+ "  AND  关联板块 NOT LIKE '%wwwwww%' " //长期记录 
							+ "  AND  关联板块 NOT LIKE '%GZGZGZ%' " //
							+ " ORDER BY 录入日期 DESC"
							;
		else
			sqlquerystat = "SELECT * FROM 商业新闻   "
					+ timerangesql
					+ " AND ( 关联板块 like '%" + bankuaiid.trim() +  "%' ) \r\n"
					+ " ORDER BY  录入日期 DESC"
					;

		CachedRowSetImpl result = null;
		try {
			result = connectdb.sqlQueryStatExecute(sqlquerystat);
   		 	while(result.next()) {
	   		 	int meetingID = result.getInt("news_id");
		        java.sql.Date recorddate = result.getDate("录入日期"); 
	            LocalDate start = recorddate.toLocalDate();
	            String title = result.getString("新闻标题");
	            String description = result.getString("具体描述");
	            if(Strings.isNullOrEmpty(description))
	            	description = "描述";
	            String keywords = result.getString("关键词");
	            String slackurl = result.getString("SLACK链接");
	            String ownercodes = result.getString("关联板块");
	            
	            InsertedNews newmeeting = new InsertedNews(
		                new News(title, start,  description, keywords, new HashSet<InsertedNews.Label>(),slackurl,ownercodes), meetingID);

	            meetings.add(newmeeting);
   		 	}
   		 
   		//label 	 
   		 for (News m : meetings) {
         	int meetingid = ((InsertedNews)m).getID();
         	
         	String area  = "商业新闻";
         	sqlquerystat = "SELECT label.* FROM label INNER "
         					+" JOIN meetingLabel "
         					+" ON label.LABEL_ID = meetingLabel.LABEL_ID " 
         					+" WHERE meetingLabel.NEWS_ID = " + meetingid
         					+" AND meetingLabel.fromtable = " + "'" + area + "'"
         					;
         	
         	CachedRowSetImpl set = connectdb.sqlQueryStatExecute(sqlquerystat);
         	
             while (set.next()) {
                 int labelID = set.getInt("LABEL_ID");
                 String name = set.getString("NAME");
                 Color colour = Color.decode(set.getString("COLOUR"));
                 boolean active = set.getBoolean("ACTIVE");
                 InsertedNews.Label label = new InsertedNews.Label(new News.Label(name, colour), labelID);
                 m.getLabels().add(label);
             }
             
             set.close();
             set = null;
         }

		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	if(result != null)
				try {
					result.close();
					result = null;
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }
		
		logger.debug("Database: query was successful [SELECT * FROM MEETING]");

        return meetings;
	}
	/*
	 * 
	 */
//	private Collection<InsertedNews> getWeeklySummary(LocalDate startdate, LocalDate enddate)
//	{
//		Collection<InsertedNews> meetings = new ArrayList<InsertedNews>();
//		
//		//如果是ALL，还要从操作记录重点关注中读取每周总结报告
//		String 	sqlquerystat = "SELECT * FROM 操作记录重点关注 " +
//			  " WHERE 股票代码= '999999'" + 
//			  " AND  日期  BETWEEN '" + startdate + "' AND '" + enddate + "'" +
//			  " AND (加入移出标志 = '加入关注' OR 加入移出标志 = '移除重点' OR 加入移出标志 = '分析结果' OR 加入移出标志 = '重点关注' )" 
//			  ;
//
//    	logger.debug(sqlquerystat);
//    	
//    	CachedRowSetImpl result = null;
//    	try{
//    		result = connectdb.sqlQueryStatExecute(sqlquerystat);
//    		
//    		while(result.next())  {
//    	        	int meetingID = result.getInt("id");
//    		        java.sql.Date recorddate = result.getDate("日期"); 
//    	            LocalDate start = recorddate.toLocalDate();
//    	            String description = result.getString("原因描述");
//    	            if(Strings.isNullOrEmpty(description))
//    	            	description = "描述";
//
//    	            InsertedNews newmeeting = new InsertedNews(
//    		                new Meeting("一周总结", start,  description, "一周总结", new HashSet<InsertedNews.Label>(),null,"000000",Meeting.WKZONGJIE), meetingID);
//    	            meetings.add(newmeeting);
//    	    }
//    		
//    	}catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    } catch (SQLException e) {
//	    	e.printStackTrace();
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    }  finally {
//	    	if(result != null)
//				try {
//					result.close();
//					result = null;
//					
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//	    }
//		
//		logger.debug("Database: query was successful [SELECT * FROM MEETING]");
//
//        return meetings;
//	}
	/*
	 * 
	 */
	public Collection<News> getQiangShiBanKuaiAndStock(String nodeid, LocalDate startdate, LocalDate enddate)
	{
		BanKuaiAndStockTree treeofbkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		
		Collection<News> meetings = new ArrayList<>();
		
		String sqlquerystat = null;
		if(nodeid.equals("ALL"))
        sqlquerystat = "SELECT 强弱势板块个股表.* , a股.`股票名称` AS 名称\r\n"
        		+ " FROM 强弱势板块个股表 , a股 \r\n"
        		+ " WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " AND  强弱类型 = true " 
        		+ " AND 强弱势板块个股表.`代码` = a股.`股票代码`\r\n"
        		+ ""
        		+ " UNION \r\n"
        		+ " SELECT 强弱势板块个股表.* , 通达信板块列表.`板块名称` AS 名称 \r\n"
        		+ " FROM 强弱势板块个股表 , 通达信板块列表 \r\n"
        		+ " WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " AND  强弱类型 = true " 
        		+ " and 强弱势板块个股表.`代码` = 通达信板块列表.`板块ID` \r\n"
        		;
		else 
			sqlquerystat = "SELECT 强弱势板块个股表.* , a股.`股票名称` AS 名称\r\n"
        		+ " FROM 强弱势板块个股表 , a股 \r\n"
        		+ " WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " AND 强弱势板块个股表.`代码` = a股.`股票代码`\r\n"
        		+ " AND 强弱势板块个股表.`代码` = '" + nodeid + "'"
        		+ " AND  强弱类型 = true " 
        		+ ""
        		+ " UNION \r\n"
        		+ " SELECT 强弱势板块个股表.* , 通达信板块列表.`板块名称` AS 名称 \r\n"
        		+ " FROM 强弱势板块个股表 , 通达信板块列表 \r\n"
        		+ "WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " and 强弱势板块个股表.`代码` = 通达信板块列表.`板块ID` \r\n"
        		+ " AND 强弱势板块个股表.`代码` = '" + nodeid + "'"
        		+ " AND  强弱类型 = true " 
        		;
			;
        logger.debug(sqlquerystat);
        CachedRowSetImpl rspd = null;
        try{
        	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
        	while(rspd.next())  {
   			 int meetingID = rspd.getInt("id");
   			 String nodecode = rspd.getString("代码");
   			 Integer nodetype = rspd.getInt("类型");
   			 LocalDate start  = rspd.getDate("日期").toLocalDate();
   			LocalDate end = null;
   			 try {
   				 end  = rspd.getDate("截至日期").toLocalDate();
   			 } catch (java.lang.NullPointerException e) {
   				 end = LocalDate.parse("3000-01-01");
   			 }
   	         String shuoming = rspd.getString("说明");
   	         String detail = rspd.getString("具体描述");
   	         Boolean leixing = rspd.getBoolean("强弱类型");
   	         String keywords = rspd.getString("关键字");
   	         String newsUrl = rspd.getString("url");
   	        		 
   	         
   	         BkChanYeLianTreeNode node = treeofbkstk.getSpecificNodeByHypyOrCode(nodecode, nodetype);
   	         QiangShi newsnews = new QiangShi(node, shuoming, start, end,   detail, keywords, 
                		new HashSet<InsertedNews.Label>(),newsUrl);
   	         InsertedExternalNews newmeeting = new InsertedExternalNews(newsnews, meetingID);
   	         meetings.add(newmeeting);
   		 }
        	
        	//label 	 
      		 for (News m : meetings) {
            	int meetingid = ((InsertedExternalNews)m).getID();
            	
            	String area = "强弱势板块个股";
            	
            	sqlquerystat = "SELECT label.* FROM label INNER "
            					+" JOIN meetingLabel "
            					+" ON label.LABEL_ID = meetingLabel.LABEL_ID " 
            					+" WHERE meetingLabel.NEWS_ID = " + meetingid
            					+" AND meetingLabel.fromtable = " + "'" + area + "'"
            					;
            	
            	CachedRowSetImpl set = connectdb.sqlQueryStatExecute(sqlquerystat);
            	
                while (set.next()) {
                    int labelID = set.getInt("LABEL_ID");
                    String name = set.getString("NAME");
                    Color colour = Color.decode(set.getString("COLOUR"));
                    boolean active = set.getBoolean("ACTIVE");
                    InsertedNews.Label label = new InsertedNews.Label(new News.Label(name, colour), labelID);
                    m.getLabels().add(label);
                }
                
                set.close();
                set = null;
            }
        	
        }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	if(rspd != null)
				try {
					rspd.close();
					rspd = null;
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }
		
		logger.debug("Database: query was successful [SELECT * FROM MEETING]");

        return meetings;

	}
	public Collection<News> getRuoShiBanKuaiAndStock(String nodeid, LocalDate startdate, LocalDate enddate)
	{
		BanKuaiAndStockTree treeofbkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		
	    //找出强弱板块个股
		Collection<News> meetings = new ArrayList<>();
		
		String sqlquerystat = null;
		if(nodeid.equals("ALL"))
        sqlquerystat = "SELECT 强弱势板块个股表.* , a股.`股票名称` AS 名称\r\n"
        		+ " FROM 强弱势板块个股表 , a股 \r\n"
        		+ " WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " AND 强弱势板块个股表.`代码` = a股.`股票代码`\r\n"
        		+ " AND  强弱类型 = false "
        		+ ""
        		+ " UNION \r\n"
        		+ " SELECT 强弱势板块个股表.* , 通达信板块列表.`板块名称` AS 名称 \r\n"
        		+ " FROM 强弱势板块个股表 , 通达信板块列表 \r\n"
        		+ "WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " and 强弱势板块个股表.`代码` = 通达信板块列表.`板块ID` \r\n"
        		+ " AND  强弱类型 = false "
        		;
		else 
			sqlquerystat = "SELECT 强弱势板块个股表.* , a股.`股票名称` AS 名称\r\n"
        		+ " FROM 强弱势板块个股表 , a股 \r\n"
        		+ " WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " AND 强弱势板块个股表.`代码` = a股.`股票代码`\r\n"
        		+ " AND 强弱势板块个股表.`代码` = '" + nodeid + "'"
        		+ " AND  强弱类型 = false "
        		+ ""
        		+ " UNION \r\n"
        		+ " SELECT 强弱势板块个股表.* , 通达信板块列表.`板块名称` AS 名称 \r\n"
        		+ " FROM 强弱势板块个股表 , 通达信板块列表 \r\n"
        		+ "WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " and 强弱势板块个股表.`代码` = 通达信板块列表.`板块ID` \r\n"
        		+ " AND 强弱势板块个股表.`代码` = '" + nodeid + "'"
        		+ " AND  强弱类型 = false "
        		;
			;
        logger.debug(sqlquerystat);
        CachedRowSetImpl rspd = null;
        try{
        	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
        	while(rspd.next())  {
   			 int meetingID = rspd.getInt("id");
   			 String nodecode = rspd.getString("代码");
   			 Integer nodetype = rspd.getInt("类型");
   			 LocalDate start  = rspd.getDate("日期").toLocalDate();
   			LocalDate end = null;
  			 try {
  				 end  = rspd.getDate("截至日期").toLocalDate();
  			 } catch (java.lang.NullPointerException e) {
  				 end = LocalDate.parse("3000-01-01");
  			 }
   	         String shuoming = rspd.getString("说明");
   	         String detail = rspd.getString("具体描述");
   	         Boolean leixing = rspd.getBoolean("强弱类型");
   	         String keywords = rspd.getString("关键字");
   	         String newsUrl = rspd.getString("url");
   	        		 
   	         
   	         BkChanYeLianTreeNode node = treeofbkstk.getSpecificNodeByHypyOrCode(nodecode, nodetype);
   	         RuoShi newsnews = new RuoShi(node, shuoming, start, end,   detail, keywords, 
                		new HashSet<InsertedNews.Label>(),newsUrl);
   	         InsertedExternalNews newmeeting = new InsertedExternalNews(newsnews, meetingID);
   	         meetings.add(newmeeting);
   		 }
        	
        	//label 	 
      		 for (News m : meetings) {
            	int meetingid = ((InsertedExternalNews)m).getID();
            	
            	
            	String area = "强弱势板块个股";
            	
            	sqlquerystat = "SELECT label.* FROM label INNER "
            					+" JOIN meetingLabel "
            					+" ON label.LABEL_ID = meetingLabel.LABEL_ID " 
            					+" WHERE meetingLabel.NEWS_ID = " + meetingid
            					+" AND meetingLabel.fromtable = " + "'" + area + "'"
            					;
            	
            	CachedRowSetImpl set = connectdb.sqlQueryStatExecute(sqlquerystat);
            	
                while (set.next()) {
                    int labelID = set.getInt("LABEL_ID");
                    String name = set.getString("NAME");
                    Color colour = Color.decode(set.getString("COLOUR"));
                    boolean active = set.getBoolean("ACTIVE");
                    InsertedNews.Label label = new InsertedNews.Label(new News.Label(name, colour), labelID);
                    m.getLabels().add(label);
                }
                
                set.close();
                set = null;
            }
        	
        }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	if(rspd != null)
				try {
					rspd.close();
					rspd = null;
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }
		
//		logger.debug("Database: query was successful [SELECT * FROM MEETING]");

        return meetings;
	}
	/*
	 * 
	 */
	public Collection<News> getDuanQiJiLuInfo (BkChanYeLianTreeNode node, LocalDate startdate, LocalDate enddate)
	{
		String nodecode = node.getMyOwnCode();
		int nodetype = node.getType();
		
		Collection<News> cqjllist = new ArrayList<>();
		
		String timerangesql = null ;
		if(startdate == null && enddate != null) {
			timerangesql = "WHERE 日期  < '" + enddate + "' \r\n" ;
		} else if(startdate != null && enddate == null) {
			timerangesql = "WHERE 日期 > '" + startdate + "' \r\n" ;
		} else if(startdate != null && enddate != null) {
			timerangesql = " WHERE 日期  BETWEEN '" + startdate + "' AND '" + enddate + "' \r\n";
		}
		
		String sqlquerystat = 	" SELECT * FROM 关注个股板块表"
								+ timerangesql
								+ "  AND  关注类型 = false " //短期记录
								+ " AND 代码= '" + nodecode + "'"
								+ " AND 类型 = " + nodetype
								+ "  ORDER BY 日期 DESC"
								;
		
		CachedRowSetImpl result = null;
		try {
			result = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			while(result.next()) {
	   		 	int meetingID = result.getInt("ID");
	   		 	LocalDate start  = result.getDate("日期").toLocalDate(); 
	   		 	LocalDate end  = result.getDate("截至日期").toLocalDate();
	   		 	if(end == null)
	   		 		end =  LocalDate.parse("3000-01-01");
	            String description = result.getString("说明");
	            String detail = result.getString("具体描述");
	            if(Strings.isNullOrEmpty(detail))
	            	description = "";
	            String keywords = result.getString("关键词");
	            String slackurl = result.getString("URL");
	            String ownercodes = result.getString("代码");
	            Integer ownertype = result.getInt("类型");
	            String gpc = result.getString("股票池类型");
	            
	            DuanQiGuanZhu news = new DuanQiGuanZhu(node, description, start, end, detail, keywords, new HashSet<InsertedNews.Label>(),slackurl);
	            news.setDqgzGuPiaoChi(gpc);

	            InsertedExternalNews newmeeting = new InsertedExternalNews(news, meetingID);

	            cqjllist.add(newmeeting);
   		  }
			
			
			//label 	 
	   		 for (News m : cqjllist) {
	         	int meetingid = ((InsertedExternalNews)m).getID();
	         	
	         	String area = "关注个股板块表";

	         	sqlquerystat = "SELECT label.* FROM label INNER "
	         					+" JOIN meetingLabel "
	         					+" ON label.LABEL_ID = meetingLabel.LABEL_ID " 
	         					+" WHERE meetingLabel.NEWS_ID = " + meetingid
	         					+" AND meetingLabel.fromtable = " + "'" + area + "'"
	         					;
	         	
	         	CachedRowSetImpl set = connectdb.sqlQueryStatExecute(sqlquerystat);
	             while (set.next()) {
	                 int labelID = set.getInt("LABEL_ID");
	                 String name = set.getString("NAME");
	                 Color colour = Color.decode(set.getString("COLOUR"));
//	                 boolean active = set.getBoolean("ACTIVE");
	                 InsertedNews.Label label = new InsertedNews.Label(new ExternalNewsType.Label(name, colour), labelID);
	                 m.getLabels().add(label);
	             }
	             
	             set.close();
	             set = null;
	         }
		} catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	if(result != null)
				try {
					result.close();
					result = null;
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }
		
//		logger.debug("Database: query was successful [SELECT * FROM MEETING]");

        return cqjllist;
	}
	public  Collection<News> getDuanQiJiLuInfo (String nodeid, LocalDate startdate, LocalDate enddate)
	{
		BanKuaiAndStockTree treeofbkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		
		Collection<News> cqjllist = new ArrayList<>();
		
		String timerangesql = null ;
		if(startdate == null && enddate != null) {
			timerangesql = "WHERE 日期  < '" + enddate + "' \r\n" ;
		} else if(startdate != null && enddate == null) {
			timerangesql = "WHERE 日期 > '" + startdate + "' \r\n" ;
		} else if(startdate != null && enddate != null) {
			timerangesql = " WHERE 日期  BETWEEN '" + startdate + "' AND '" + enddate + "' \r\n";
		}
		
		String sqlquerystat;
		if(nodeid.toUpperCase().equals("ALL")) 
			sqlquerystat = 	" SELECT * FROM 关注个股板块表"
									+ timerangesql
									+ "  AND  关注类型 = false " //长期记录
									+ " ORDER BY 日期 DESC"
									;
		else
			sqlquerystat = 	" SELECT * FROM 关注个股板块表"
					+ timerangesql
					+ "  AND  关注类型 = false " //长期记录
					+ "  AND 代码 = '" + nodeid + "'"
					+ " ORDER BY 日期 DESC"
					;
		
		CachedRowSetImpl result = null;
		try {
			result = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			while(result.next()) {
	   		 	int meetingID = result.getInt("ID");
	   		 	LocalDate start  = result.getDate("日期").toLocalDate(); 
	   		 	LocalDate end;
	   		 	try {
	   		 		end  = result.getDate("截至日期").toLocalDate();
	   		 	} catch (java.lang.NullPointerException e) {
	   		 		end = LocalDate.parse("3000-01-01");
	   		 	}
	            String description = result.getString("说明");
	            String detail = result.getString("具体描述");
	            if(Strings.isNullOrEmpty(detail))
	            	description = "";
	            String keywords = result.getString("关键词");
	            String slackurl = result.getString("URL");
	            String ownercodes = result.getString("代码");
	            Integer ownertype = result.getInt("类型");
	            String gpc = result.getString("股票池类型");
	            
	            BkChanYeLianTreeNode node = treeofbkstk.getSpecificNodeByHypyOrCode(ownercodes, ownertype);
	            DuanQiGuanZhu news = new DuanQiGuanZhu(node, description, start, end , detail, keywords, new HashSet<InsertedNews.Label>(),slackurl);
	            news.setDqgzGuPiaoChi(gpc);
	            
	            InsertedExternalNews newmeeting = new InsertedExternalNews(news, meetingID);

	            cqjllist.add(newmeeting);
   		  }
			
			
			//label 	 
	   		 for (News m : cqjllist) {
	         	int meetingid = ((InsertedExternalNews)m).getID();
	         	
	         	String area = "关注个股板块表";

	         	sqlquerystat = "SELECT label.* FROM label INNER "
	         					+" JOIN meetingLabel "
	         					+" ON label.LABEL_ID = meetingLabel.LABEL_ID " 
	         					+" WHERE meetingLabel.NEWS_ID = " + meetingid
	         					+" AND meetingLabel.fromtable = " + "'" + area + "'"
	         					;
	         	
	         	CachedRowSetImpl set = connectdb.sqlQueryStatExecute(sqlquerystat);
	             while (set.next()) {
	                 int labelID = set.getInt("LABEL_ID");
	                 String name = set.getString("NAME");
	                 Color colour = Color.decode(set.getString("COLOUR"));
//	                 boolean active = set.getBoolean("ACTIVE");
	                 InsertedNews.Label label = new InsertedNews.Label(new ExternalNewsType.Label(name, colour), labelID);
	                 m.getLabels().add(label);
	             }
	             
	             set.close();
	             set = null;
	         }
		} catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	if(result != null)
				try {
					result.close();
					result = null;
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }
		
		logger.debug("Database: query was successful [SELECT * FROM MEETING]");

        return cqjllist;
	}
	/*
	 * 
	 */
	public  Collection<News> getChangQiJiLuInfo (BkChanYeLianTreeNode node, LocalDate startdate, LocalDate enddate)
	{
		String nodeid = node.getMyOwnCode();
		Integer nodetype = node.getType();
		
		Collection<News> cqjllist = new ArrayList<>();
		
		String timerangesql = null ;
		if(startdate == null && enddate != null) {
			timerangesql = "WHERE 日期  < '" + enddate + "' \r\n" ;
		} else if(startdate != null && enddate == null) {
			timerangesql = "WHERE 日期 > '" + startdate + "' \r\n" ;
		} else if(startdate != null && enddate != null) {
			timerangesql = " WHERE 日期  BETWEEN '" + startdate + "' AND '" + enddate + "' \r\n";
		}
		
		String sqlquerystat = 	" SELECT * FROM 关注个股板块表"
								+ timerangesql
								+ "  AND  关注类型 = true " //长期记录
								+ " AND 代码= '" + nodeid + "'"
								+ " AND 类型 = " + nodetype
								+ "  ORDER BY 日期 DESC"
								;
		
		CachedRowSetImpl result = null;
		try {
			result = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			while(result.next()) {
	   		 	int meetingID = result.getInt("ID");
	   		 	LocalDate start  = result.getDate("日期").toLocalDate(); 
//	   		 	LocalDate end  = result.getDate("截至日期").toLocalDate();
	            String description = result.getString("说明");
	            String detail = result.getString("具体描述");
	            if(Strings.isNullOrEmpty(detail))
	            	description = "";
	            String keywords = result.getString("关键词");
	            String slackurl = result.getString("URL");
	            String ownercodes = result.getString("代码");
	            Integer ownertype = result.getInt("类型");
	            
	            ExternalNewsType news = new ChangQiGuanZhu(node, description, start,  LocalDate.parse("3000-01-01"), detail, keywords, new HashSet<InsertedNews.Label>(),slackurl);

	            InsertedExternalNews newmeeting = new InsertedExternalNews(news, meetingID);

	            cqjllist.add(newmeeting);
   		  }
			
			
			//label 	 
	   		 for (News m : cqjllist) {
	         	int meetingid = ((InsertedExternalNews)m).getID();
	         	
	         	String area = "关注个股板块表";

	         	sqlquerystat = "SELECT label.* FROM label INNER "
	         					+" JOIN meetingLabel "
	         					+" ON label.LABEL_ID = meetingLabel.LABEL_ID " 
	         					+" WHERE meetingLabel.NEWS_ID = " + meetingid
	         					+" AND meetingLabel.fromtable = " + "'" + area + "'"
	         					;
	         	
	         	CachedRowSetImpl set = connectdb.sqlQueryStatExecute(sqlquerystat);
	             while (set.next()) {
	                 int labelID = set.getInt("LABEL_ID");
	                 String name = set.getString("NAME");
	                 Color colour = Color.decode(set.getString("COLOUR"));
//	                 boolean active = set.getBoolean("ACTIVE");
	                 InsertedNews.Label label = new InsertedNews.Label(new ExternalNewsType.Label(name, colour), labelID);
	                 m.getLabels().add(label);
	             }
	             
	             set.close();
	             set = null;
	         }
		} catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	if(result != null)
				try {
					result.close();
					result = null;
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }
		
		logger.debug("Database: query was successful [SELECT * FROM MEETING]");

        return cqjllist;
		
	}
	public  Collection<News> getChangQiJiLuInfo (String nodeid,  LocalDate startdate, LocalDate enddate)
	{
		BanKuaiAndStockTree treeofbkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
			
		Collection<News> cqjllist = new ArrayList<>();
		
		String timerangesql = null ;
		if(startdate == null && enddate != null) {
			timerangesql = "WHERE 日期  < '" + enddate + "' \r\n" ;
		} else if(startdate != null && enddate == null) {
			timerangesql = "WHERE 日期 > '" + startdate + "' \r\n" ;
		} else if(startdate != null && enddate != null) {
			timerangesql = " WHERE 日期  BETWEEN '" + startdate + "' AND '" + enddate + "' \r\n";
		}
		
		String sqlquerystat;
		if(nodeid.toUpperCase().equals("ALL"))
			 sqlquerystat = 	" SELECT * FROM 关注个股板块表"
					+ " WHERE   关注类型 = true " //长期记录
					+ " ORDER BY 日期 DESC"
					;
		else
			sqlquerystat = 	" SELECT * FROM 关注个股板块表"
									+ " WHERE 关注类型 = true " //长期记录
									+ "  AND 代码= '"  + nodeid + "'" 
									+ " ORDER BY 日期 DESC"
									;
		
		CachedRowSetImpl result = null;
		try {
			result = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			while(result.next()) {
	   		 	int meetingID = result.getInt("ID");
	   		 	LocalDate start  = result.getDate("日期").toLocalDate();
	   		 	LocalDate end;
	   		 	try {
	   		 		end  = result.getDate("截至日期").toLocalDate();
	   		 	} catch (java.lang.NullPointerException e) {
	   		 		end = LocalDate.parse("3000-01-01");
	   		 	}
	            String description = result.getString("说明");
	            String detail = result.getString("具体描述");
	            if(Strings.isNullOrEmpty(detail))
	            	description = "";
	            String keywords = result.getString("关键词");
	            String slackurl = result.getString("URL");
	            String ownercodes = result.getString("代码");
	            Integer ownertype = result.getInt("类型");
	            
	            BkChanYeLianTreeNode node = treeofbkstk.getSpecificNodeByHypyOrCode(ownercodes, ownertype);
	            if(node == null)
	            	node = treeofbkstk.getSpecificNodeByHypyOrCode("999999", BkChanYeLianTreeNode.TDXBK);
	            
	            ExternalNewsType news = new ChangQiGuanZhu(node, description, start,  end, detail, keywords, new HashSet<InsertedNews.Label>(),slackurl);

	            InsertedExternalNews newmeeting = new InsertedExternalNews(news, meetingID);

	            cqjllist.add(newmeeting);
   		  }
			
			
			//label 	 
	   		 for (News m : cqjllist) {
	         	int meetingid = ((InsertedExternalNews)m).getID();
	         	
	         	String area = "关注个股板块表";

	         	sqlquerystat = "SELECT label.* FROM label INNER "
	         					+" JOIN meetingLabel "
	         					+" ON label.LABEL_ID = meetingLabel.LABEL_ID " 
	         					+" WHERE meetingLabel.NEWS_ID = " + meetingid
	         					+" AND meetingLabel.fromtable = " + "'" + area + "'"
	         					;
	         	
	         	CachedRowSetImpl set = connectdb.sqlQueryStatExecute(sqlquerystat);
	             while (set.next()) {
	                 int labelID = set.getInt("LABEL_ID");
	                 String name = set.getString("NAME");
	                 Color colour = Color.decode(set.getString("COLOUR"));
//	                 boolean active = set.getBoolean("ACTIVE");
	                 InsertedNews.Label label = new InsertedNews.Label(new ExternalNewsType.Label(name, colour), labelID);
	                 m.getLabels().add(label);
	             }
	             
	             set.close();
	             set = null;
	         }
		} catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	if(result != null)
				try {
					result.close();
					result = null;
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }
		
		logger.debug("Database: query was successful [SELECT * FROM MEETING]");

        return cqjllist;
	}
	/*
	 * 
	 */
	public InsertedExternalNews createDuanQiJiLuInfo (News news)
	{
		BkChanYeLianTreeNode node = ((ExternalNewsType)news).getNode();
		String nodecode = node.getMyOwnCode();
		Integer nodetype = node.getType();
		
		LocalDate start = news.getStart();
		LocalDate end = ((ExternalNewsType)news).getEnd();
		
    	String description = news.getTitle();
    	String detail = news.getDescription();
    	String keywords = news.getKeyWords();
    	String slackurl = news.getNewsUrl();
    	
    	String gpc = ((DuanQiGuanZhu)news).getDqgzGuPiaoChi();
    	
    	InsertedExternalNews InsertedNews = null;
    	
    			try {
    					String sqlinsertstat = "INSERT INTO 关注个股板块表(代码,类型,关注类型,日期,截至日期,说明,具体描述,url, 关键词,股票池类型) values ("
    							+ "'" + nodecode + "'" + ","
    							+ nodetype  + ","
    							+ false + ","
    							+ "'" + start + "'" + ","
    							+ "'" + end  + "'" + ","
    							+ "'" +  description + "'" + ","
    							+ "'" +  detail +  "|'" + ","
    							+ "'" +  slackurl + "'"  + ","
    	    					+ "'" +  keywords + "'"  + ","
    							+ "'" +  gpc + "'"
    							+ ")"
    							;
    					int meetingID = connectdb.sqlInsertStatExecute(sqlinsertstat) ;
    					int labelid = 0;
    					String area = "关注个股板块表";
    					for (InsertedNews.Label label : news.getLabels()) {
    			                labelid = label.getID();
    			                String sqlstatementlabel = "INSERT INTO meetingLabel (news_ID, LABEL_ID,fromtable) VALUES( " 
    			                							+ meetingID + "," 
    			                							+ labelid + ","
    			                							+ "'"+ area + "'"
    			                							+ ")";
    			   			 	
    			                connectdb.sqlInsertStatExecute(sqlstatementlabel);
    					}
    					
    					InsertedNews = new InsertedExternalNews( (ExternalNewsType)news, meetingID);
    					
    				}catch(java.lang.NullPointerException e){ 
    			    	e.printStackTrace();
    			    } catch(Exception e){
    			    	e.printStackTrace();
    			    }  finally {
    			    	
    			    }
    			
    	return InsertedNews;
	}
	/*
	 * 
	 */
	public InsertedExternalNews createChangQiJiLuInfo (News news)
	{
		BkChanYeLianTreeNode node = ((ExternalNewsType)news).getNode();
		String nodecode = node.getMyOwnCode();
		Integer nodetype = node.getType();
		
		LocalDate start = news.getStart();
		LocalDate end = ((ExternalNewsType)news).getEnd();
		
    	String description = news.getTitle();
    	String detail = news.getDescription();
    	String keywords = news.getKeyWords();
    	String slackurl = news.getNewsUrl();
    	
    	InsertedExternalNews InsertedNews = null;
    	
    			try {
    					String sqlinsertstat = "INSERT INTO 关注个股板块表(代码,类型,关注类型,日期,截至日期,说明,具体描述,url, 关键词) values ("
    							+ "'" + nodecode + "'" + ","
    							+ nodetype  + ","
    							+ true + ","
    							+ "'" + start + "'" + ","
    							+ "'" + end  + "'" + ","
    							+ "'" +  description + "'" + ","
    							+ "'" +  detail +  "|'" + ","
    							+ "'" +  slackurl + "'"  + ","
    	    					+ "'" +  keywords + "'"
    							+ ")"
    							;
    					int meetingID = connectdb.sqlInsertStatExecute(sqlinsertstat) ;
    					int labelid = 0;
    					String area = "关注个股板块表";
    					for (InsertedNews.Label label : news.getLabels()) {
    			                labelid = label.getID();
    			                String sqlstatementlabel = "INSERT INTO meetingLabel (news_ID, LABEL_ID,fromtable) VALUES( " 
    			                							+ meetingID + "," 
    			                							+ labelid + ","
    			                							+ "'"+ area + "'"
    			                							+ ")";
    			   			 	
    			                connectdb.sqlInsertStatExecute(sqlstatementlabel);
    					}
    					
    					InsertedNews = new InsertedExternalNews( (ExternalNewsType)news, meetingID);
    					
    				}catch(java.lang.NullPointerException e){ 
    			    	e.printStackTrace();
    			    } catch(Exception e){
    			    	e.printStackTrace();
    			    }  finally {
    			    	
    			    }
    			
    	return InsertedNews;
		
	}
	/*
	 * 
	 */
	public InsertedNews createNodeNews (News meeting)
	{
		String newsownercode = meeting.getNewsOwnerCodes();
		LocalDate newdate = meeting.getStart();
    	String title = meeting.getTitle();
    	String description = meeting.getDescription().replace("'", " ");
    	String keywords = meeting.getKeyWords();
    	String slackurl = meeting.getNewsUrl();
    	
    	InsertedNews InsertedNews = null;
    	
    			try {
    					String sqlinsertstat = "INSERT INTO 商业新闻(新闻标题,关键词,SLACK链接,录入日期,关联板块,具体描述) values ("
    							+ "'" + title + "'" + ","
    							+ "'" + keywords + "'" + ","
    							+ "'" + slackurl  + "'" + ","
    							+ "'" +  newdate + "'" + ","
    							+ "'" +  newsownercode +  "'" + ","
    							+ "'" +  description + "'"
    							+ ")"
    							;
    					int meetingID = connectdb.sqlInsertStatExecute(sqlinsertstat) ;
    					int labelid = 0;
    					String area = "商业新闻";
    					for (InsertedNews.Label label : meeting.getLabels()) {
    			                labelid = label.getID();
    			                String sqlstatementlabel = "INSERT INTO meetingLabel (news_ID, LABEL_ID,fromtable) VALUES( " 
    			                							+ meetingID + "," 
    			                							+ labelid + ","
    			                							+ "'"+ area + "'"
    			                							+ ")";
    			   			 	
    			                connectdb.sqlInsertStatExecute(sqlstatementlabel);
    					}
    					
    			        InsertedNews = new InsertedNews(meeting, meetingID);
    					
    				}catch(java.lang.NullPointerException e){ 
    			    	e.printStackTrace();
    			    } catch(Exception e){
    			    	e.printStackTrace();
    			    }  finally {
    			    	
    			    }
    			
    	return InsertedNews;
	}
	/*
	 * 
	 */
	public News createZhiShuGuanJianRiQi (ZhiShuBoLang meeting)
	{
		BkChanYeLianTreeNode node = meeting.getNode();
		LocalDate start = meeting.getStart();
		LocalDate end = meeting.getEnd();
    	String title = meeting.getTitle();
    	String description = meeting.getDescription().replace("'", " ");
    	String keywords = meeting.getKeyWords();
    	if(keywords.contains("指数")) keywords = "";
    	String slackurl = meeting.getNewsUrl();
    	
    	InsertedNews InsertedNews = null;
    	
    	try {
    		String sqlinsertstat = null ;
    		if(end != null)
    			sqlinsertstat = "INSERT INTO 指数关键日期表(代码, 类型 ,  日期, 截至日期,说明,  详细说明, URL,关键词 ) VALUES ("
    				+ "'"  + node.getMyOwnCode() + "',"
    				+ ""  + node.getType() + ","
    				+ "'" + start  + "',"
    				+ "'" + end + "',"
    				+ "'"  + title  + "',"
    				+ "'"  + description  + "',"
    				+ "'"  + slackurl  + "',"
    				+ "'"  + keywords  + "'"
    				+ ")"
    				;
    		else
    			sqlinsertstat = "INSERT INTO 指数关键日期表(代码, 类型,,日期,  说明,  详细说明, URL,关键词 ) VALUES ("
    					+ "'"  + node.getMyOwnCode() + "',"
        				+ ""  + node.getType() + ","
        				+ "'" + start  + "',"
        				+ "'"  + title  + "',"
        				+ "'"  + description  + "',"
        				+ "'"  + slackurl  + "',"
        				+ "'"  + keywords  + "'"
        				+ ")"
        				;
    		
    		logger.debug(sqlinsertstat);
			int meetingID = connectdb.sqlInsertStatExecute(sqlinsertstat) ;
			int labelid = 0;
			String area = "指数关键日期";
			for (InsertedNews.Label label : meeting.getLabels()) {
	                labelid = label.getID();
	                String sqlstatementlabel = "INSERT INTO meetingLabel (news_ID, LABEL_ID,fromtable) VALUES( " 
	                						+ meetingID + "," 
	                						+ labelid + ","
	                						+ "'"+ area + "'"
	                						+ ")";
	                
	                connectdb.sqlInsertStatExecute(sqlstatementlabel);
			 }
			
			InsertedNews = new InsertedNews(meeting, meetingID);
			
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	
	    }
		
		return InsertedNews;
	}
	
	public InsertedExternalNews createQiangInfo (News news)
	{
		ExternalNewsType meeting = (ExternalNewsType) news;
		BkChanYeLianTreeNode node = meeting.getNode();
		String newsownercode = node.getMyOwnCode();
		int newstype = node.getType();
		LocalDate start = meeting.getStart();
		LocalDate end = meeting.getStart();
    	String description = meeting.getTitle();
    	String detail = meeting.getDescription().replace("'", " ");
    	String keywords = meeting.getKeyWords();
    	String slackurl = meeting.getNewsUrl();
    	
    	
    	InsertedExternalNews InsertedNews = null;
		try {
			String sqlinsertstat = "INSERT INTO 强弱势板块个股表(代码,类型,日期,截至日期,强弱类型,说明,具体描述,URL,关键字) VALUES ("
    				+ "'"  + newsownercode  + "'" + ","
    				+  newstype   + ","
    				+ "'"  + start  + "'" + ","
    				+ "'"  + end  + "'" + ","
    				+ true + ","
    				+ "'"  + description  + "'" + ","
    				+ "'"  + detail  + "'" + ","
    				+ "'"  + slackurl  + "'" + ","
    				+ "'"  + keywords  + "'" 
    				+ ")"
    				;
    		
    		int meetingID = 0;
    		try{
    			meetingID = connectdb.sqlInsertStatExecute(sqlinsertstat) ;
    		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ex) {
    			String cause = ex.getMessage();
    			logger.debug(cause);
    			return null;
    		}
    		
			int labelid = 0;
			String area = "强弱势板块个股表";
			for (InsertedNews.Label label : meeting.getLabels()) {
	                labelid = label.getID();
	                String sqlstatementlabel = "INSERT INTO meetingLabel (news_ID, LABEL_ID,fromtable) VALUES( " 
	                						+ meetingID + "," 
	                						+ labelid + ","
	                						+ "'"+ area + "'"
	                						+ ")";
	                
	                connectdb.sqlInsertStatExecute(sqlstatementlabel);
			 }
			
			InsertedNews = new InsertedExternalNews(meeting, meetingID);
			
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	
	    }
		
		return InsertedNews;

	}
	public InsertedExternalNews createRuoShiInfo (News news)
	{
		ExternalNewsType meeting = (ExternalNewsType) news;
		
		BkChanYeLianTreeNode node = meeting.getNode();
		String newsownercode = node.getMyOwnCode();
		int newstype = node.getType();
		LocalDate start = meeting.getStart();
		LocalDate end = meeting.getEnd();
		String description = meeting.getTitle();
		String detail = meeting.getDescription().replace("'", " ");
		String keywords = meeting.getKeyWords();
		String slackurl = meeting.getNewsUrl();
		
		
		InsertedExternalNews InsertedNews = null;
		try {
			String sqlinsertstat = "INSERT INTO 强弱势板块个股表(代码,类型,日期,截至日期,强弱类型,说明,具体描述,URL,关键字) VALUES ("
					+ "'"  + newsownercode  + "'" + ","
					+  newstype   + ","
					+ "'"  + start  + "'" + ","
					+ "'"  + end  + "'" + ","
					+ false + ","
					+ "'"  + description  + "'" + ","
					+ "'"  + detail  + "'" + ","
					+ "'"  + slackurl  + "'" + ","
					+ "'"  + keywords  + "'" 
					+ ")"
					;
			
			int meetingID = 0;
			try{
				meetingID = connectdb.sqlInsertStatExecute(sqlinsertstat) ;
			} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ex) {
				String cause = ex.getMessage();
				logger.debug(cause);
				return null;
			}
			
			int labelid = 0;
			String area = "强弱势板块个股";
			for (InsertedNews.Label label : meeting.getLabels()) {
	                labelid = label.getID();
	                String sqlstatementlabel = "INSERT INTO meetingLabel (news_ID, LABEL_ID,fromtable) VALUES( " 
	                						+ meetingID + "," 
	                						+ labelid + ","
	                						+ "'"+ area + "'"
	                						+ ")";
	                
	                connectdb.sqlInsertStatExecute(sqlstatementlabel);
			 }
			
			InsertedNews = new InsertedExternalNews(meeting, meetingID);
			
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	
	    }
		
		return InsertedNews;
	}

	public News deleteGuanZhuJiLuInfo (News news)
	{
		InsertedExternalNews in = (InsertedExternalNews) news;
		
		int newsid = in.getID();

		try {
			String deletestat = "DELETE  FROM 关注个股板块表    WHERE id =" + newsid;
			connectdb.sqlDeleteStatExecute (deletestat);

			String sqlstatement = "DELETE FROM meetingLabel "
									+ " WHERE news_ID = " + newsid 
									+ " AND fromtable = '关注个股板块表' "
									;
			int key = connectdb.sqlDeleteStatExecute(sqlstatement);
			
//			sqlstatement = "DELETE FROM 产业链板块国新闻对应表 "
//					+ " WHERE news_id = " + newsid 
//					;
//			key = connectdb.sqlDeleteStatExecute(sqlstatement);
			
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    
	    }
		
		return news;
	}
	public News updateGuanZhuJiLuInfo (News news)
	{
		InsertedExternalNews event = (InsertedExternalNews) news;
		
		int newsid = event.getID();
    	LocalDate starttime = event.getStart();
    	LocalDate endtime = event.getEnd();
		String desc = event.getTitle();
		String detail = event.getDescription();
		String keywordsurl = event.getKeyWords();
		String newsowners = event.getNode().getMyOwnCode();
		Integer newstype = event.getNode().getType();
		String slackurl = event.getNewsUrl();
		ExternalNewsType gz = event.getNews();
		
		String gpc = "";
		if(gz instanceof DuanQiGuanZhu)
			gpc = ((DuanQiGuanZhu)event.getNews() ).getDqgzGuPiaoChi();
		
   		try {
        		String sqlupatestatement =  "UPDATE 关注个股板块表  SET 日期 = '" + starttime + "', "
        				+ " 截至日期 = '" + endtime + "', "
        				+ " 代码 = '" + newsowners + "', "
        				+ " 类型 = '" + newstype + "', "
        				+ " 说明  = '" + desc + "', "
        				+ " 具体描述  = '" + detail + "', "
        				+ " url  = '" + slackurl + "', "
        				+ " 关键词  = '" + keywordsurl +  "', "
        				+ " 股票池类型='" + gpc + "'"  
        				+ " WHERE id =  " + newsid
        				;
        		
        		connectdb.sqlUpdateStatExecute(sqlupatestatement);
        		
        		sqlupatestatement = "DELETE FROM meetingLabel WHERE news_ID = " + newsid
        							+ " AND fromtable = '关注个股板块表' "
        							;
        		connectdb.sqlUpdateStatExecute(sqlupatestatement);

              for (InsertedNews.Label l : event.getLabels()) {
                  int labelid = l.getID();
                  String area = "关注个股板块表";
                  sqlupatestatement = "INSERT INTO meetingLabel (news_id, LABEL_ID ,fromtable) VALUES ("
    			            		    +  newsid + ","
    									+  labelid  + ","
    									+ " '" + area + "' " 
    									+ ")"
                		  				;
                	connectdb.sqlUpdateStatExecute(sqlupatestatement);
              }
   		} catch(java.lang.NullPointerException e){ 
   	    	e.printStackTrace();
   	    } catch(Exception e){
   	    	e.printStackTrace();
   	    }  finally {
   	    
   	    }
   		
   		return event;
	
	}
	/*
	 * 
	 */
	public InsertedNews deleteNews (News meeting)
	{
		InsertedNews deletedMeeting = (InsertedNews) meeting;
		int newsid = deletedMeeting.getID();

		try {
			String deletestat = "DELETE  FROM 商业新闻 WHERE news_id =" + newsid;
			connectdb.sqlDeleteStatExecute (deletestat);

			String sqlstatement = "DELETE FROM meetingLabel "
									+ " WHERE news_ID = " + newsid 
									+ " AND fromtable = '商业新闻' "
									;
			int key = connectdb.sqlDeleteStatExecute(sqlstatement);
			
//			sqlstatement = "DELETE FROM 产业链板块国新闻对应表 "
//					+ " WHERE news_id = " + newsid 
//					;
//			key = connectdb.sqlDeleteStatExecute(sqlstatement);
			
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    
	    }
		
		return deletedMeeting;
	}
	public InsertedExternalNews deleteQiangRuoShiInfo (News meeting)
	{
		InsertedExternalNews deletedMeeting =  (InsertedExternalNews) meeting;
		
		int newsid = deletedMeeting.getID();
    	
		
		try {
			String deletestat = "DELETE  FROM 强弱势板块个股表  WHERE id =" + newsid;
			logger.debug(deletestat);
			connectdb.sqlDeleteStatExecute (deletestat);

			String sqlstatement = "DELETE FROM meetingLabel "
									+ " WHERE news_ID = " + newsid 
									+ " AND fromtable = '强弱势板块个股' "
									;
			int key = connectdb.sqlDeleteStatExecute(sqlstatement);
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    
	    }
		
		return deletedMeeting;
	}
	/*
	 * 
	 */
	public InsertedExternalNews  updatedQiangRuoShiInfo (News meeting) throws SQLException
	{
		InsertedExternalNews event = (InsertedExternalNews) meeting;
		
		int newsid = event.getID();
		BkChanYeLianTreeNode node = event.getNode();
    	LocalDate starttime = event.getStart();
    	LocalDate endtime = event.getEnd();
		String desc = event.getTitle();
		String detail = event.getDescription();
		String keywordsurl = event.getKeyWords();
		String newsowners = event.getNewsOwnerCodes();
		String slackurl = event.getNewsUrl();
		String keywords = event.getKeyWords();
    	
   		try {
        		String sqlupatestatement =  "UPDATE 强弱势板块个股表  SET "
        				+ "日期 = '" + starttime + "', "
        				+ "截至日期 = '" + endtime + "', "
        				+ " 代码 = '" + node.getMyOwnCode() + "', "
        				+ " 类型 = " + node.getType() + ", "
        				+ " 说明  = '" + desc + "', "
        				+ " 具体描述= '" + detail + "', " 
        				+ " URL = '" + slackurl   + "', "
        				+ " 关键字 = '" + keywords   + "' "
        				+ " WHERE id =  " + newsid
        				;
        		
        		connectdb.sqlUpdateStatExecute(sqlupatestatement);
        		
        		sqlupatestatement = "DELETE FROM meetingLabel WHERE news_ID = " + newsid
        							+ " AND fromtable = '强弱势板块个股' "
        							;
        		connectdb.sqlUpdateStatExecute(sqlupatestatement);

              for (InsertedNews.Label l : event.getLabels()) {
                  int labelid = l.getID();
                  sqlupatestatement = "INSERT INTO meetingLabel (news_id, LABEL_ID ,fromtable) VALUES ("
    			            		    +  newsid + ","
    									+  labelid  + ","
    									+ " '强弱势板块个股' " 
    									+ ")"
                		  				;
                	connectdb.sqlUpdateStatExecute(sqlupatestatement);
              }
        		

   		} catch(java.lang.NullPointerException e){ 
   	    	e.printStackTrace();
   	    } catch(Exception e){
   	    	e.printStackTrace();
   	    }  finally {
   	    
   	    }
   		
   		return event;
	}
	public News deleteZhiShuGuanJianRiQi (News meeting )
	{
		InsertedExternalNews deletedMeeting = (InsertedExternalNews) meeting;
		
		int newsid = deletedMeeting.getID();

		try {
			String deletestat = "DELETE  FROM 指数关键日期表  WHERE id =" + newsid;
			logger.debug(deletestat);
			connectdb.sqlDeleteStatExecute (deletestat);

			String sqlstatement = "DELETE FROM meetingLabel "
									+ " WHERE news_ID = " + newsid 
									+ " AND fromtable = '指数关键日期' "
									;
			int key = connectdb.sqlDeleteStatExecute(sqlstatement);
			
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    
	    }

		return deletedMeeting;
		
	}


	public InsertedExternalNews updateZhiShuGuanJIanRiQi(News meeting) throws SQLException
	{
		InsertedExternalNews updatedMeeting = (InsertedExternalNews) meeting;
		BkChanYeLianTreeNode node = updatedMeeting.getNode() ;
    	LocalDate starttime = updatedMeeting.getStart();
    	LocalDate endtime = updatedMeeting.getEnd();
		String title = updatedMeeting.getTitle();
		String desc = updatedMeeting.getDescription();
		String keywordsurl = updatedMeeting.getKeyWords();
		String newsowners = updatedMeeting.getNewsOwnerCodes();
		String slackurl = updatedMeeting.getNewsUrl();
		int newsid = updatedMeeting.getID();
//		logger.debug("test");
		
		String sqlupatestatement = null;
		if(Strings.isNullOrEmpty(newsowners)) {
			this.deleteZhiShuGuanJianRiQi(meeting);
			return updatedMeeting;
		} else	
		if(endtime != null)
			sqlupatestatement =  "UPDATE 指数关键日期表  SET"
					+ "  日期 = '" + starttime + "', "
					+ "  截至日期 = '" + endtime +"',"
					+ "  代码  = '" + node.getMyOwnCode() + "', "
					+ " 类型 = " + node.getType() + ", "
					+ " 详细说明 =  ' " + desc +  "', "
					+ " URL = '" + slackurl +  "', "
					+ " 关键词 = '" + keywordsurl +  "' , "
					+ " 说明  = '" + title + "' "
					+ " WHERE id =  " + newsid
					;
		else
			sqlupatestatement =   "UPDATE 指数关键日期表  SET"
					+ "  日期 = '" + starttime + "', "
					+ "  代码  = '" + node.getMyOwnCode() + "', "
					+ " 类型 = " + node.getType() + ", "
					+ " 详细说明 =  ' " + desc +  "', "
					+ " URL = '" + slackurl +  "', "
					+ " 关键词 = '" + keywordsurl +  "' , "
					+ " 说明  = '" + title + "' "
					+ " WHERE id =  " + newsid
					;
			
		
		connectdb.sqlUpdateStatExecute(sqlupatestatement);
		
		sqlupatestatement = "DELETE FROM meetingLabel WHERE news_ID = " + newsid 
							+ " AND fromtable = '指数关键日期' "
							;
		connectdb.sqlUpdateStatExecute(sqlupatestatement);

      for (InsertedNews.Label l : meeting.getLabels()) {
          int labelid = l.getID();
          sqlupatestatement = "INSERT INTO meetingLabel (news_id, LABEL_ID,fromtable ) VALUES ("
		            		    +  newsid + ","
								+  labelid  + ","
								+ "'指数关键日期')"
        		  				;
        	connectdb.sqlUpdateStatExecute(sqlupatestatement);
      }
      
      return updatedMeeting;

	}
	public InsertedNews updateNews (News meeting)
	{
		InsertedNews updatedMeeting = (InsertedNews) meeting;
    	LocalDate starttime = meeting.getStart();
		String title = meeting.getTitle();
		String desc = meeting.getDescription();
		String keywordsurl = meeting.getKeyWords();
		String newsowners = meeting.getNewsOwnerCodes();
		String slackurl = meeting.getNewsUrl();
		int newsid = updatedMeeting.getID();
		
		try {

    		String sqlupatestatement =  "UPDATE 商业新闻  SET 录入日期 = '" + starttime + "', "
    				+ " 新闻标题 = '" + title + "', "
    				+ " 具体描述 = '" + desc + "', "
    				+ " 关键词 = '" + keywordsurl + "',"  
    				+ " 关联板块= '" + newsowners + "',"
    				+ " SLACK链接= '" + slackurl + "'"
    				+ " WHERE news_id =  " + newsid
    				;
    		
    		connectdb.sqlUpdateStatExecute(sqlupatestatement);
    		
    		sqlupatestatement = "DELETE FROM meetingLabel WHERE news_ID = " + newsid ;
    		connectdb.sqlUpdateStatExecute(sqlupatestatement);
    		
          for (InsertedNews.Label l : meeting.getLabels()) {
              int labelid = l.getID();
              sqlupatestatement = "INSERT INTO meetingLabel (news_id, LABEL_ID,fromtable ) VALUES ("
			            		    +  newsid + ","
									+  labelid  + ","
									+ "'商业新闻')"
            		  				;
            	connectdb.sqlUpdateStatExecute(sqlupatestatement);
          }
		} catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    
	    }

		return updatedMeeting;
	}
	/*
	 * 
	 */
    public Collection<InsertedNews.Label> getLabels() throws SQLException {

        Collection<InsertedNews.Label> labels = new ArrayList<>();
        String sqlstatement = "SELECT * FROM label "
                ;
 
        CachedRowSetImpl result = null;
		try {
			result = connectdb.sqlQueryStatExecute(sqlstatement);
			
		      while (result.next()) {
	                int id = result.getInt("LABEL_ID");
	                String name = result.getString("NAME");
	                Color colour = Color.decode(result.getString("COLOUR"));
	                boolean active = result.getBoolean("ACTIVE");
	                News.Label nlabel = new News.Label(name, colour);
	                InsertedNews.Label label = new InsertedNews.Label(nlabel, id);
	                labels.add(label);
	          }
            
            
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	try {
	    		result.close();
	    		result = null;
	    		
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }

        logger.debug("Database: query was successful [SELECT * FROM LABEL]");
        return labels;
    }
    /**
     * 
     * @param label
     * @return
     * @throws SQLException
     */
    public InsertedNews.Label createNewsLabel(News.Label label) throws SQLException {

        InsertedNews.Label insertedLabel = null;
        
        String labelname = label.getName();
        String color =  "#" + Integer.toHexString(label.getColor().getRGB()).substring(2);
        
        String sqlstatement = "INSERT INTO label (NAME, COLOUR, ACTIVE ) VALUES ('" + labelname +"','" + color + "', 1) "
                ;
		try {
			int key = connectdb.sqlInsertStatExecute(sqlstatement);
			insertedLabel = new InsertedNews.Label(label, key);
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    
	    }
        
        logger.debug("Database: query was successful [INSERT INTO label] ");
        return insertedLabel;
    }

    public InsertedNews.Label deleteNewsLabel(InsertedNews.Label label) throws SQLException 
    {
    	InsertedNews.Label deletedLabel = null;
    	int labelid = label.getID();
    	String meetingsqlstatement = "DELETE FROM meetingLabel WHERE LABEL_ID = " + labelid 
                ;
    	
    	String labelsqlstatement = "DELETE FROM label WHERE LABEL_ID =  " + labelid 
                ;
    	
    	try {
			connectdb.sqlDeleteStatExecute(labelsqlstatement);
			connectdb.sqlDeleteStatExecute(meetingsqlstatement);
			
			 deletedLabel = label;
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    
	    }

    	logger.debug("Database: query was successful [DELETE label WHERE ID= " + label.getID() + "]");

       return deletedLabel;
    }

    public InsertedNews.Label updateNewsLabel(InsertedNews.Label label) throws SQLException 
    {
    	InsertedNews.Label updatedLabel = null;
    
    	int labelid = label.getID();
    	String labelname = label.getName();
        String color =  "#" + Integer.toHexString(label.getColor().getRGB()).substring(2);
        
        String sqlstatement = "UPDATE label SET NAME = '" + labelname + "', COLOUR = '" + color + "' WHERE LABEL_ID = " + labelid
        						;
        try {
			 connectdb.sqlUpdateStatExecute(sqlstatement);
			 updatedLabel = label;
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    
	    }
 
//        Connection connection = DriverManager.getConnection(this.url);
//        PreparedStatement statement = null;
//        InsertedNews.Label updatedLabel = null;
//
//        try {
//            statement = connection.prepareStatement(
//                "UPDATE label SET NAME = ?, COLOUR = ?, ACTIVE = ? WHERE LABEL_ID = ? ");
//
//            statement.setString(1, label.getName());
//            statement.setString(2, "#" + Integer.toHexString(label.getColor().getRGB()).substring(2));
//            statement.setInt(3, label.isActive() ? 1 : 0);
//            statement.setInt(4, label.getID());
//            statement.execute();
//            updatedLabel = label;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            
//        }
        logger.info("Database: query was successful [UPDATE label WHERE ID= " + label.getID() + "]");
        return updatedLabel;
    }
    
    
    
}
