package com.exchangeinfomanager.database;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
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
		initialzieSysconf ();
	}
	
	private  ConnectDataBase connectdb;
	private  SystemConfigration sysconfig;
		
	

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
	public Collection<InsertedMeeting> getRequiredRelatedInfoForNewsAndOthers(String bankuaiid,LocalDate startdate, LocalDate enddate,Integer[] requiredtype)
	{
		Collection<InsertedMeeting> events = new ArrayList<InsertedMeeting>();
		
		for(int i=0;i<requiredtype.length;i++) {
			int type = requiredtype[i];
			if(type == Meeting.NODESNEWS) {
				Collection<InsertedMeeting> newslist = this.getNodeRelatedNews(bankuaiid,startdate,enddate);
				events.addAll(newslist);
			} else if(type == Meeting.CHANGQIJILU) {
				Collection<InsertedMeeting> cqlist = this.getChangQiJiLuInfo (startdate,enddate);
				events.addAll(cqlist);
			} else if(type == Meeting.JINQIGUANZHU) {
				Collection<InsertedMeeting> jqgzlist = this.getJiQiGuanZhuInfo (startdate,enddate);
				events.addAll(jqgzlist);
			} else if( type == Meeting.RUOSHI) {
				Collection<InsertedMeeting> qiangruoshilist = this.getRuoShiBanKuaiAndStock (bankuaiid,startdate,enddate);
				events.addAll(qiangruoshilist);
			} else if(type == Meeting.QIANSHI ) {
				Collection<InsertedMeeting> qiangruoshilist = this.getQiangShiBanKuaiAndStock(bankuaiid,startdate,enddate);
				events.addAll(qiangruoshilist);
			} else if(type == Meeting.WKZONGJIE) {
				Collection<InsertedMeeting> wkslist = this.getWeeklySummary(startdate, enddate);
				events.addAll(wkslist);
			} else if(type == Meeting.ZHISHUDATE) {
				Collection<InsertedMeeting> zhishudatelist = this.getZhiShuKeyDates (bankuaiid,startdate,enddate);
				events.addAll(zhishudatelist);
			} 
		}
		
		return events;
	}
	/*
	 * 
	 */
	private Collection<InsertedMeeting> getZhiShuKeyDates (String bankuaiid,LocalDate startdate, LocalDate enddate)
	{
		Collection<InsertedMeeting> meetings = new ArrayList<InsertedMeeting>();
//		System.out.print("getZhiShuKeyDates");
		
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
	        					+ "WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'"
	        					+ " AND ( 关联板块 like '%" + bankuaiid.trim() +  "%' ) \r\n"
	        					+ " ORDER BY  日期  DESC"
	        					;

			 rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			 while(rspd.next())  {
				 int meetingID = rspd.getInt("id");
				 String nodecode = rspd.getString("关联板块");
			     java.sql.Date recordstartdate = rspd.getDate("日期"); 
		         LocalDate start = recordstartdate.toLocalDate();
		         java.sql.Date recordenddate = rspd.getDate("截至日期");
		         LocalDate end ;
		         try{
		        	 end = recordenddate.toLocalDate();
		         } catch (java.lang.NullPointerException ex) {
		        	 end =  null; 
		         }
		         
		         String shuoming = rspd.getString("说明");
		         
		         InsertedMeeting newmeeting = new InsertedMeeting(
			                new Meeting(nodecode + shuoming, start,  end, shuoming, "指数关键日期", new HashSet<InsertedMeeting.Label>(),null,nodecode,Meeting.ZHISHUDATE), meetingID);
		         meetings.add(newmeeting);
			 }
			 
			//label 	 
	  		 for (InsertedMeeting m : meetings) {
	        	int meetingid = m.getID();
	        	int meetingtype = m.getMeetingType();
	        	
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
	                InsertedMeeting.Label label = new InsertedMeeting.Label(new Meeting.Label(name, colour, active), labelID);
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
	/*
	 * 
	 */
	private Collection<InsertedMeeting> getNodeRelatedNews(String bankuaiid,LocalDate startdate, LocalDate enddate) 
	{
		Collection<InsertedMeeting> meetings = new ArrayList<InsertedMeeting>();
		
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
	            int type = Meeting.NODESNEWS;
	            
	            InsertedMeeting newmeeting = new InsertedMeeting(
		                new Meeting(title, start,  description, keywords, new HashSet<InsertedMeeting.Label>(),slackurl,ownercodes,type), meetingID);

	            meetings.add(newmeeting);
   		 	}
   		 
   		//label 	 
   		 for (InsertedMeeting m : meetings) {
         	int meetingid = m.getID();
         	int meetingtype = m.getMeetingType();
         	
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
                 InsertedMeeting.Label label = new InsertedMeeting.Label(new Meeting.Label(name, colour, active), labelID);
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
	private Collection<InsertedMeeting> getWeeklySummary(LocalDate startdate, LocalDate enddate)
	{
		Collection<InsertedMeeting> meetings = new ArrayList<InsertedMeeting>();
		
		//如果是ALL，还要从操作记录重点关注中读取每周总结报告
		String 	sqlquerystat = "SELECT * FROM 操作记录重点关注 " +
			  " WHERE 股票代码= '999999'" + 
			  " AND  日期  BETWEEN '" + startdate + "' AND '" + enddate + "'" +
			  " AND (加入移出标志 = '加入关注' OR 加入移出标志 = '移除重点' OR 加入移出标志 = '分析结果' OR 加入移出标志 = '重点关注' )" 
			  ;

    	logger.debug(sqlquerystat);
    	
    	CachedRowSetImpl result = null;
    	try{
    		result = connectdb.sqlQueryStatExecute(sqlquerystat);
    		
    		while(result.next())  {
    	        	int meetingID = result.getInt("id");
    		        java.sql.Date recorddate = result.getDate("日期"); 
    	            LocalDate start = recorddate.toLocalDate();
    	            String description = result.getString("原因描述");
    	            if(Strings.isNullOrEmpty(description))
    	            	description = "描述";

    	            InsertedMeeting newmeeting = new InsertedMeeting(
    		                new Meeting("一周总结", start,  description, "一周总结", new HashSet<InsertedMeeting.Label>(),null,"000000",Meeting.WKZONGJIE), meetingID);
    	            meetings.add(newmeeting);
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
	private Collection<InsertedMeeting> getQiangShiBanKuaiAndStock(String nodeid, LocalDate startdate, LocalDate enddate)
	{
		Collection<InsertedMeeting> meetings = new ArrayList<InsertedMeeting>();
		
		String sqlquerystat = null;
		if(nodeid.equals("ALL"))
        sqlquerystat = "SELECT 强弱势板块个股表.* , a股.`股票名称` AS 名称\r\n"
        		+ " FROM 强弱势板块个股表 , a股 \r\n"
        		+ " WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " AND  强弱类型 LIKE '%强势%' " 
        		+ " AND 强弱势板块个股表.`代码` = a股.`股票代码`\r\n"
        		+ ""
        		+ " UNION \r\n"
        		+ " SELECT 强弱势板块个股表.* , 通达信板块列表.`板块名称` AS 名称 \r\n"
        		+ " FROM 强弱势板块个股表 , 通达信板块列表 \r\n"
        		+ " WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " AND  强弱类型 LIKE '%强势%' " 
        		+ " and 强弱势板块个股表.`代码` = 通达信板块列表.`板块ID` \r\n"
        		;
		else 
			sqlquerystat = "SELECT 强弱势板块个股表.* , a股.`股票名称` AS 名称\r\n"
        		+ " FROM 强弱势板块个股表 , a股 \r\n"
        		+ " WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " AND 强弱势板块个股表.`代码` = a股.`股票代码`\r\n"
        		+ " AND 强弱势板块个股表.`代码` = '" + nodeid + "'"
        		+ " AND  强弱类型 LIKE '%强势%' " 
        		+ ""
        		+ " UNION \r\n"
        		+ " SELECT 强弱势板块个股表.* , 通达信板块列表.`板块名称` AS 名称 \r\n"
        		+ " FROM 强弱势板块个股表 , 通达信板块列表 \r\n"
        		+ "WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " and 强弱势板块个股表.`代码` = 通达信板块列表.`板块ID` \r\n"
        		+ " AND 强弱势板块个股表.`代码` = '" + nodeid + "'"
        		+ " AND  强弱类型 LIKE '%强势%' " 
        		;
			;
        logger.debug(sqlquerystat);
        CachedRowSetImpl rspd = null;
        try{
        	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
        	
        	while(rspd.next())  {
   			 int meetingID = rspd.getInt("id");
   			 String nodecode = rspd.getString("代码");
   			 String nodename = rspd.getString("名称");
   		     java.sql.Date recorddate = rspd.getDate("日期"); 
   	         LocalDate start = recorddate.toLocalDate();
   	         String shuoming = rspd.getString("说明");
   	         
   	         String leixing = rspd.getString("强弱类型").trim();
   	         String owner = null; int type = 0;
   	         if(leixing.contains("强势板块")) {
   	        	 type = Meeting.QIANSHI;
   	        	 owner = nodecode + "bk";
   	         } else if(leixing.contains("强势个股")) {
   	        	 type = Meeting.QIANSHI;
   	        	 owner = nodecode + "gg";
   	         } 
   	         
   	         InsertedMeeting newmeeting = new InsertedMeeting(
   		                new Meeting( owner.subSequence(0, 6) + "/" + nodename, start,  shuoming, leixing, 
   		                		new HashSet<InsertedMeeting.Label>(),null,owner,type), meetingID);
   	         meetings.add(newmeeting);
   		 }
        	
        	//label 	 
      		 for (InsertedMeeting m : meetings) {
            	int meetingid = m.getID();
            	int meetingtype = m.getMeetingType();
            	
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
                    InsertedMeeting.Label label = new InsertedMeeting.Label(new Meeting.Label(name, colour, active), labelID);
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
	private Collection<InsertedMeeting> getRuoShiBanKuaiAndStock(String nodeid, LocalDate startdate, LocalDate enddate)
	{
	    //找出强弱板块个股
		Collection<InsertedMeeting> meetings = new ArrayList<InsertedMeeting>();
		
		String sqlquerystat = null;
		if(nodeid.equals("ALL"))
        sqlquerystat = "SELECT 强弱势板块个股表.* , a股.`股票名称` AS 名称\r\n"
        		+ " FROM 强弱势板块个股表 , a股 \r\n"
        		+ " WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " AND 强弱势板块个股表.`代码` = a股.`股票代码`\r\n"
        		+ " AND  强弱类型 LIKE '%弱势%' "
        		+ ""
        		+ " UNION \r\n"
        		+ " SELECT 强弱势板块个股表.* , 通达信板块列表.`板块名称` AS 名称 \r\n"
        		+ " FROM 强弱势板块个股表 , 通达信板块列表 \r\n"
        		+ "WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " and 强弱势板块个股表.`代码` = 通达信板块列表.`板块ID` \r\n"
        		+ " AND  强弱类型 LIKE '%弱势%' "
        		;
		else 
			sqlquerystat = "SELECT 强弱势板块个股表.* , a股.`股票名称` AS 名称\r\n"
        		+ " FROM 强弱势板块个股表 , a股 \r\n"
        		+ " WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " AND 强弱势板块个股表.`代码` = a股.`股票代码`\r\n"
        		+ " AND 强弱势板块个股表.`代码` = '" + nodeid + "'"
        		+ " AND  强弱类型 LIKE '%弱势%' "
        		+ ""
        		+ " UNION \r\n"
        		+ " SELECT 强弱势板块个股表.* , 通达信板块列表.`板块名称` AS 名称 \r\n"
        		+ " FROM 强弱势板块个股表 , 通达信板块列表 \r\n"
        		+ "WHERE 日期 BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
        		+ " and 强弱势板块个股表.`代码` = 通达信板块列表.`板块ID` \r\n"
        		+ " AND 强弱势板块个股表.`代码` = '" + nodeid + "'"
        		+ " AND  强弱类型 LIKE '%弱势%' "
        		;
			;
        logger.debug(sqlquerystat);
        CachedRowSetImpl rspd = null;
        try{
        	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
        	
        	while(rspd.next())  {
   			 int meetingID = rspd.getInt("id");
   			 String nodecode = rspd.getString("代码");
   			 String nodename = rspd.getString("名称");
   		     java.sql.Date recorddate = rspd.getDate("日期"); 
   	         LocalDate start = recorddate.toLocalDate();
   	         String shuoming = rspd.getString("说明");
   	         
   	         String leixing = rspd.getString("强弱类型").trim();
   	         String owner = null; int type = 0;
   	        if(leixing.contains("弱势板块")) {
   	        	 type = Meeting.RUOSHI;
   	        	 owner = nodecode + "gg";
   	         } else if(leixing.contains("弱势个股")) {
   	        	 type = Meeting.RUOSHI;
   	        	 owner = nodecode + "gg";
   	         }
   	         
   	         InsertedMeeting newmeeting = new InsertedMeeting(
   		                new Meeting( owner.subSequence(0, 6) + "/" + nodename, start,  shuoming, leixing, 
   		                		new HashSet<InsertedMeeting.Label>(),null,owner,type), meetingID);
   	         meetings.add(newmeeting);
   		 }
        	
        	//label 	 
      		 for (InsertedMeeting m : meetings) {
            	int meetingid = m.getID();
            	int meetingtype = m.getMeetingType();
            	
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
                    InsertedMeeting.Label label = new InsertedMeeting.Label(new Meeting.Label(name, colour, active), labelID);
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
	/*
	 * 
	 */
	private Collection<InsertedMeeting> getJiQiGuanZhuInfo (LocalDate startdate, LocalDate enddate)
	{
		Collection<InsertedMeeting> jqgzlist = new ArrayList<InsertedMeeting>();
		
		String timerangesql = null ;
		if(startdate == null && enddate != null) {
			timerangesql = "WHERE 录入日期  < '" + enddate + "' \r\n" ;
		} else if(startdate != null && enddate == null) {
			timerangesql = "WHERE 录入日期 > '" + startdate + "' \r\n" ;
		} else if(startdate != null && enddate != null) {
			timerangesql = " WHERE 录入日期  BETWEEN '" + startdate + "' AND '" + enddate + "' \r\n";
		}
		
		String sqlquerystat = 	" SELECT * FROM 商业新闻 "
								+ timerangesql + "\r\n"
								+ "AND  关联板块 LIKE '%gzgzgz%' " //
								+ " ORDER BY 录入日期 DESC"
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
	            int type = Meeting.JINQIGUANZHU;
	            
	            InsertedMeeting newmeeting = new InsertedMeeting(
		                new Meeting(title, start,  description, keywords, new HashSet<InsertedMeeting.Label>(),slackurl,ownercodes,type), meetingID);

	            jqgzlist.add(newmeeting);
   		  }
			//label 	 
	   		 for (InsertedMeeting m : jqgzlist) {
	         	int meetingid = m.getID();
	         	int meetingtype = m.getMeetingType();
	         	
	         	String area = "商业新闻";

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
	                 InsertedMeeting.Label label = new InsertedMeeting.Label(new Meeting.Label(name, colour, active), labelID);
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

       return jqgzlist;
		
	}
	/*
	 * 
	 */
	private Collection<InsertedMeeting> getChangQiJiLuInfo (LocalDate startdate, LocalDate enddate)
	{
		Collection<InsertedMeeting> cqjllist = new ArrayList<InsertedMeeting>();
		
		String timerangesql = null ;
		if(startdate == null && enddate != null) {
			timerangesql = "WHERE 录入日期  < '" + enddate + "' \r\n" ;
		} else if(startdate != null && enddate == null) {
			timerangesql = "WHERE 录入日期 > '" + startdate + "' \r\n" ;
		} else if(startdate != null && enddate != null) {
			timerangesql = " WHERE 录入日期  BETWEEN '" + startdate + "' AND '" + enddate + "' \r\n";
		}
		
		String sqlquerystat = 	" SELECT * FROM 商业新闻"
								+ timerangesql
								+ "  AND  关联板块 LIKE '%wwwwww%' " //长期记录
								 
								+ " ORDER BY 录入日期 DESC"
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
	            int type = Meeting.CHANGQIJILU;
	            
	            InsertedMeeting newmeeting = new InsertedMeeting(
		                new Meeting(title, start,  description, keywords, new HashSet<InsertedMeeting.Label>(),slackurl,ownercodes,type), meetingID);

	            cqjllist.add(newmeeting);
   		  }
			
			
			//label 	 
	   		 for (InsertedMeeting m : cqjllist) {
	         	int meetingid = m.getID();
	         	int meetingtype = m.getMeetingType();
	         	
	         	String area = "商业新闻";

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
	                 InsertedMeeting.Label label = new InsertedMeeting.Label(new Meeting.Label(name, colour, active), labelID);
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
	private InsertedMeeting createNodeNews (Meeting meeting)
	{
		String newsownercode = meeting.getNewsOwnerCodes();
		LocalDate newdate = meeting.getStart();
    	String title = meeting.getTitle();
    	String description = meeting.getDescription().replace("'", " ");
    	String keywords = meeting.getKeyWords();
    	String slackurl = meeting.getSlackUrl();
    	
    	InsertedMeeting insertedMeeting = null;
    	
    	//其他都放到商业新闻表
    			try {
    					String sqlinsertstat = "INSERT INTO 商业新闻(新闻标题,关键词,SLACK链接,录入日期,关联板块,具体描述) values ("
    							+ "'" + title + "'" + ","
    							+ "'" + keywords + "'" + ","
    							+ "'" + slackurl  + "'" + ","
    							+ "'" +  newdate + "'" + ","
    							+ "'" +  newsownercode +  "|'" + ","
    							+ "'" +  description + "'"
    							+ ")"
    							;
    					logger.debug(sqlinsertstat);
    					int meetingID = connectdb.sqlInsertStatExecute(sqlinsertstat) ;
    					int labelid = 0;
    					String area = "商业新闻";
    					for (InsertedMeeting.Label label : meeting.getLabels()) {
    			                labelid = label.getID();
    			                String sqlstatementlabel = "INSERT INTO meetingLabel (news_ID, LABEL_ID,fromtable) VALUES( " 
    			                							+ meetingID + "," 
    			                							+ labelid + ","
    			                							+ "'"+ area + "'"
    			                							+ ")";
    			   			 	
    			                connectdb.sqlInsertStatExecute(sqlstatementlabel);
    					}
    					
    			        insertedMeeting = new InsertedMeeting(meeting, meetingID);
    					
    				}catch(java.lang.NullPointerException e){ 
    			    	e.printStackTrace();
    			    } catch(Exception e){
    			    	e.printStackTrace();
    			    }  finally {
    			    	
    			    }
    			
    	return insertedMeeting;
	}
	/*
	 * 
	 */
	private InsertedMeeting createZhiShuGuanJianRiQi (Meeting meeting)
	{
		String newsownercode = meeting.getNewsOwnerCodes();
		LocalDate start = meeting.getStart();
		LocalDate end = meeting.getEnd();
    	String title = meeting.getTitle();
    	String description = meeting.getDescription().replace("'", " ");
    	String keywords = meeting.getKeyWords();
    	String slackurl = meeting.getSlackUrl();
    	
    	InsertedMeeting insertedMeeting = null;
    	
    	try {
    		String sqlinsertstat = null ;
    		if(end != null)
    			sqlinsertstat = "INSERT INTO 指数关键日期表(关联板块,日期,截至日期,说明) VALUES ("
    				+ "'"  + newsownercode + "',"
    				+ "'" + start  + "',"
    				+ "'" + end + "',"
    				+ "'"  + description  + "'"
    				+ ")"
    				;
    		else
    			sqlinsertstat = "INSERT INTO 指数关键日期表(关联板块,日期,说明) VALUES ("
        				+ "'"  + newsownercode + "',"
        				+ "'" + start  + "',"
        				+ "'"  + description  + "'"
        				+ ")"
        				;
    		
    		logger.debug(sqlinsertstat);
			int meetingID = connectdb.sqlInsertStatExecute(sqlinsertstat) ;
			int labelid = 0;
			String area = "指数关键日期";
			for (InsertedMeeting.Label label : meeting.getLabels()) {
	                labelid = label.getID();
	                String sqlstatementlabel = "INSERT INTO meetingLabel (news_ID, LABEL_ID,fromtable) VALUES( " 
	                						+ meetingID + "," 
	                						+ labelid + ","
	                						+ "'"+ area + "'"
	                						+ ")";
	                
	                connectdb.sqlInsertStatExecute(sqlstatementlabel);
			 }
			
			insertedMeeting = new InsertedMeeting(meeting, meetingID);
			
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	
	    }
		
		return insertedMeeting;
		
	}
	private InsertedMeeting createQianRuoShiInfo (Meeting meeting)
	{
		String newsownercode = meeting.getNewsOwnerCodes();
		LocalDate newdate = meeting.getStart();
    	String title = meeting.getTitle();
    	String description = meeting.getDescription().replace("'", " ");
    	String keywords = meeting.getKeyWords();
    	String slackurl = meeting.getSlackUrl();
    	
    	InsertedMeeting insertedMeeting = null;
    	
    	String leixing = null; 
		if(meeting.getMeetingType() == Meeting.QIANSHI) {
			if(newsownercode.toLowerCase().contains("bk")) { //指数和个股代码可能相同，前端通过加后缀来区别
				leixing = "强势板块";
			} else
				leixing = "强势个股";
		}
		if(meeting.getMeetingType() == Meeting.RUOSHI) {
			if(newsownercode.toLowerCase().contains("bk")) { //指数和个股代码可能相同，前端通过加后缀来区别
				leixing = "弱势板块";
			} else
				leixing = "弱势个股";
		}
		
		try {
			String sqlinsertstat = "INSERT INTO 强弱势板块个股表(代码,日期,强弱类型,说明) VALUES ("
    				+ "'"  + newsownercode.subSequence(0, 6)  + "'" + ","
    				+ "'"  + newdate  + "'" + ","
    				+ "'" + leixing + "'" + ","
    				+ "'"  + description  + "'"
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
			for (InsertedMeeting.Label label : meeting.getLabels()) {
	                labelid = label.getID();
	                String sqlstatementlabel = "INSERT INTO meetingLabel (news_ID, LABEL_ID,fromtable) VALUES( " 
	                						+ meetingID + "," 
	                						+ labelid + ","
	                						+ "'"+ area + "'"
	                						+ ")";
	                
	                connectdb.sqlInsertStatExecute(sqlstatementlabel);
			 }
			
			insertedMeeting = new InsertedMeeting(meeting, meetingID);
			
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	
	    }
		
		return insertedMeeting;

	}
    /*
	 * 增加板块新闻
	 */
	public InsertedMeeting createRequiredRelatedInfoForNewsAndOthers (Meeting meeting)
	{
    	if(meeting.getMeetingType() == Meeting.ZHISHUDATE ) { //放入指数关键日期表
    		return createZhiShuGuanJianRiQi (meeting);
    	}
    	
    	if( meeting.getMeetingType() == Meeting.QIANSHI ||  meeting.getMeetingType() == Meeting.RUOSHI) { //放入强弱势板块个股表
    		 return this.createQianRuoShiInfo(meeting);		
    	}
    	
    	return createNodeNews (meeting);
	}
	/*
	 * 
	 */
	private InsertedMeeting deleteNodeNews (InsertedMeeting meeting)
	{
		InsertedMeeting deletedMeeting = null;
		int newsid = meeting.getID();

		try {
			String deletestat = "DELETE  FROM 商业新闻 WHERE news_id =" + newsid;
			logger.debug(deletestat);
			connectdb.sqlDeleteStatExecute (deletestat);

			String sqlstatement = "DELETE FROM meetingLabel "
									+ " WHERE news_ID = " + newsid 
									+ " AND fromtable = '商业新闻' "
									;
			int key = connectdb.sqlDeleteStatExecute(sqlstatement);
			
			deletedMeeting = meeting;
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    
	    }
		
		return deletedMeeting;
	}
	private InsertedMeeting deleteQianRuoShiInfo (InsertedMeeting meeting)
	{
		InsertedMeeting deletedMeeting = null;
		
		int newsid = meeting.getID();
    	
		
		try {
			String deletestat = "DELETE  FROM 强弱势板块个股表  WHERE id =" + newsid;
			logger.debug(deletestat);
			connectdb.sqlDeleteStatExecute (deletestat);

			String sqlstatement = "DELETE FROM meetingLabel "
									+ " WHERE news_ID = " + newsid 
									+ " AND fromtable = '强弱势板块个股' "
									;
			int key = connectdb.sqlDeleteStatExecute(sqlstatement);
			
			deletedMeeting = meeting;
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    
	    }
		
		return deletedMeeting;
	}
	private InsertedMeeting deleteZhiShuGuanJianRiQi (InsertedMeeting meeting )
	{
		InsertedMeeting deletedMeeting = null;
		
		int newsid = meeting.getID();

		try {
			String deletestat = "DELETE  FROM 指数关键日期表  WHERE id =" + newsid;
			logger.debug(deletestat);
			connectdb.sqlDeleteStatExecute (deletestat);

			String sqlstatement = "DELETE FROM meetingLabel "
									+ " WHERE news_ID = " + newsid 
									+ " AND fromtable = '指数关键日期' "
									;
			int key = connectdb.sqlDeleteStatExecute(sqlstatement);
			
			deletedMeeting = meeting;
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
	public InsertedMeeting deleteRequiredRelatedInfoForNewsAndOthers(InsertedMeeting meeting)
	{
		if(meeting.getMeetingType() == Meeting.NODESNEWS || meeting.getMeetingType() == Meeting.CHANGQIJILU 
				|| meeting.getMeetingType() == Meeting.JINQIGUANZHU ) {
			return this.deleteNodeNews(meeting);
		}
		
		if(meeting.getMeetingType() == Meeting.QIANSHI || meeting.getMeetingType() == Meeting.RUOSHI) {
			return this.deleteQianRuoShiInfo(meeting);
		}
		
		if(meeting.getMeetingType() == Meeting.ZHISHUDATE) {
			return this.deleteZhiShuGuanJianRiQi(meeting);
		}
		
		return null;
	}
	/*
	 * 
	 */
	private InsertedMeeting updateQianRuoShiBanKuai(InsertedMeeting event) throws SQLException
	{
		InsertedMeeting updatedMeeting = null;
    	LocalDate starttime = event.getStart();
		String title = event.getTitle();
		String desc = event.getDescription();
		String keywordsurl = event.getKeyWords();
		String newsowners = event.getNewsOwnerCodes();
		String slackurl = event.getSlackUrl();
		int newsid = event.getID();
//		logger.debug("test");
    	
   		try {
        		String sqlupatestatement =  "UPDATE 强弱势板块个股表  SET 日期 = '" + starttime + "', "
        				+ " 代码 = '" + newsowners.subSequence(0, 6) + "', "
        				+ " 说明  = '" + desc + "', "
        				+ " 强弱类型 = '" + keywordsurl + "'"  
        				+ " WHERE id =  " + newsid
        				;
        		
        		connectdb.sqlUpdateStatExecute(sqlupatestatement);
        		
        		sqlupatestatement = "DELETE FROM meetingLabel WHERE news_ID = " + newsid
        							+ " AND fromtable = '强弱势板块个股' "
        							;
        		connectdb.sqlUpdateStatExecute(sqlupatestatement);

              for (InsertedMeeting.Label l : event.getLabels()) {
                  int labelid = l.getID();
                  sqlupatestatement = "INSERT INTO meetingLabel (news_id, LABEL_ID ,fromtable) VALUES ("
    			            		    +  newsid + ","
    									+  labelid  + ","
    									+ " '强弱势板块个股' " 
    									+ ")"
                		  				;
                	connectdb.sqlUpdateStatExecute(sqlupatestatement);
              }
        		
              updatedMeeting = event;

   		} catch(java.lang.NullPointerException e){ 
   	    	e.printStackTrace();
   	    } catch(Exception e){
   	    	e.printStackTrace();
   	    }  finally {
   	    
   	    }
   		
   		return updatedMeeting;
	}
	private InsertedMeeting updateZhiShuGuanJIanRiQi(InsertedMeeting meeting) throws SQLException
	{
		InsertedMeeting updatedMeeting = null;
    	LocalDate starttime = meeting.getStart();
    	LocalDate endtime = meeting.getEnd();
		String title = meeting.getTitle();
		String desc = meeting.getDescription();
		String keywordsurl = meeting.getKeyWords();
		String newsowners = meeting.getNewsOwnerCodes();
		String slackurl = meeting.getSlackUrl();
		int newsid = meeting.getID();
//		logger.debug("test");
		
		String sqlupatestatement = null;
		if(endtime != null)
			sqlupatestatement =  "UPDATE 指数关键日期表  SET"
					+ " 日期 = '" + starttime + "', "
					+ " 截至日期 = '" + endtime +"',"
					+ " 关联板块  = '" + newsowners + "', "
					+ " 说明  = '" + desc + "' "
					+ " WHERE id =  " + newsid
					;
		else
			sqlupatestatement =  "UPDATE 指数关键日期表  SET"
					+ " 日期 = '" + starttime + "', "
					+ " 关联板块  = '" + newsowners + "', "
					+ " 说明  = '" + desc + "' "
					+ " WHERE id =  " + newsid
					;
			
		
		connectdb.sqlUpdateStatExecute(sqlupatestatement);
		
		sqlupatestatement = "DELETE FROM meetingLabel WHERE news_ID = " + newsid 
							+ " AND fromtable = '指数关键日期' "
							;
		connectdb.sqlUpdateStatExecute(sqlupatestatement);

      for (InsertedMeeting.Label l : meeting.getLabels()) {
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
	private InsertedMeeting updateNodeNews (InsertedMeeting meeting)
	{
		InsertedMeeting updatedMeeting = null;
    	LocalDate starttime = meeting.getStart();
		String title = meeting.getTitle();
		String desc = meeting.getDescription();
		String keywordsurl = meeting.getKeyWords();
		String newsowners = meeting.getNewsOwnerCodes();
		String slackurl = meeting.getSlackUrl();
		int newsid = meeting.getID();
		
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
    		
          for (InsertedMeeting.Label l : meeting.getLabels()) {
              int labelid = l.getID();
              sqlupatestatement = "INSERT INTO meetingLabel (news_id, LABEL_ID,fromtable ) VALUES ("
			            		    +  newsid + ","
									+  labelid  + ","
									+ "'商业新闻')"
            		  				;
            	connectdb.sqlUpdateStatExecute(sqlupatestatement);
          }
    		
          updatedMeeting = meeting;

		} catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    
	    }

		return updatedMeeting;
	}
	/**
	 * 
	 * @param meeting
	 * @return
	 * @throws SQLException
	 */
	public InsertedMeeting updateRequiredRelatedInfoForNewsAndOthers(InsertedMeeting meeting) throws SQLException 
	{
//    	InsertedMeeting updatedMeeting = null;
    	if(meeting.getMeetingType() == Meeting.QIANSHI || meeting.getMeetingType() == Meeting.RUOSHI) {
    		return updateQianRuoShiBanKuai (meeting);
    	}
    	
    	if(meeting.getMeetingType() == Meeting.ZHISHUDATE) {
    		return updateZhiShuGuanJIanRiQi (meeting);
    	}
    	
    	if(meeting.getMeetingType() == Meeting.NODESNEWS || meeting.getMeetingType() == Meeting.CHANGQIJILU
    			|| meeting.getMeetingType() == Meeting.JINQIGUANZHU ) {
    		return this.updateNodeNews(meeting);
    	}
    	
        return null;
    }
	/*
	 * 
	 */
    public Collection<InsertedMeeting.Label> getLabels() throws SQLException {

        Collection<InsertedMeeting.Label> labels = new ArrayList<>();
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
	                InsertedMeeting.Label label = new InsertedMeeting.Label(new Meeting.Label(name, colour, active), id);
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
    public InsertedMeeting.Label createMeetingLabel(Meeting.Label label) throws SQLException {

        InsertedMeeting.Label insertedLabel = null;
        
        String labelname = label.getName();
        String color =  "#" + Integer.toHexString(label.getColor().getRGB()).substring(2);
        
        String sqlstatement = "INSERT INTO label (NAME, COLOUR, ACTIVE ) VALUES ('" + labelname +"','" + color + "', 1) "
                ;
		try {
			int key = connectdb.sqlInsertStatExecute(sqlstatement);
			insertedLabel = new InsertedMeeting.Label(label, key);
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    
	    }
        
        logger.debug("Database: query was successful [INSERT INTO label] ");
        return insertedLabel;
    }

    public InsertedMeeting.Label deleteMeetingLabel(InsertedMeeting.Label label) throws SQLException 
    {
    	InsertedMeeting.Label deletedLabel = null;
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

    public InsertedMeeting.Label updateMeetingLabel(InsertedMeeting.Label label) throws SQLException 
    {
    	InsertedMeeting.Label updatedLabel = null;
    
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
//        InsertedMeeting.Label updatedLabel = null;
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
