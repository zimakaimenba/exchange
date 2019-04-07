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
	public Collection<InsertedMeeting> getZhiShuKeyDates (LocalDate startdate, LocalDate enddate)
	{
		Collection<InsertedMeeting> meetings = new ArrayList<InsertedMeeting>();
		
		 //�ҳ�ָ���ؼ�����  
		CachedRowSetImpl rspd = null;
		try {
			String sqlquerystat;
			
			if(startdate != null)
				sqlquerystat = "SELECT * FROM ָ���ؼ����ڱ� \r\n"
	//	        		+ "WHERE ���� BETWEEN '" + startdate + "' AND '" + enddate + "'";
		        		;
			else
				sqlquerystat = "SELECT * FROM ָ���ؼ����ڱ� \r\n"
		        		+ "WHERE ���� BETWEEN '" + startdate + "' AND '" + enddate + "'";
		        		;

	     logger.debug(sqlquerystat);
		 rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
		 while(rspd.next())  {
			 int meetingID = rspd.getInt("id");
			 String nodecode = rspd.getString("����");
		     java.sql.Date recorddate = rspd.getDate("����"); 
	         LocalDate start = recorddate.toLocalDate();
	         String shuoming = rspd.getString("˵��");
	         
	         InsertedMeeting newmeeting = new InsertedMeeting(
		                new Meeting("ָ���ؼ�����:"+ nodecode, start,  shuoming, "ָ���ؼ�����", new HashSet<InsertedMeeting.Label>(),null,nodecode,Meeting.ZHISHUDATE), meetingID);
	         meetings.add(newmeeting);
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
	public Collection<InsertedMeeting> getBanKuaiRelatedNews(String bankuaiid,LocalDate startdate, LocalDate enddate) 
	{
		Collection<InsertedMeeting> meetings = new ArrayList<InsertedMeeting>();
		
		String timerangesql = null ;
		if(startdate == null && enddate != null) {
			timerangesql = "WHERE ¼������  < '" + enddate + "' \r\n" ;
		} else if(startdate != null && enddate == null) {
			timerangesql = "WHERE ¼������ > '" + startdate + "' \r\n" ;
		} else if(startdate != null && enddate != null) {
			timerangesql = " WHERE ¼������  BETWEEN '" + startdate + "' AND '" + enddate + "' \r\n";
		}
		
		String sqlquerystat = null;
		if("ALL".equals(bankuaiid.toUpperCase()) ) 
			sqlquerystat = "SELECT * FROM ��ҵ����  \r\n"
							+ timerangesql + "\r\n" 
							+ "UNION \r\n"
							+ " SELECT * FROM ��ҵ����  WHERE  ������� LIKE '%wwwwww%' "
							+ " ORDER BY ¼������ DESC"
							;
		else
			sqlquerystat = "SELECT * FROM ��ҵ����   "
					+ timerangesql
					+ " AND ( ������� like '%" + bankuaiid.trim() +  "%' ) \r\n"
					+ " ORDER BY  ¼������ DESC"
					;

		CachedRowSetImpl result = null;
		try {
			result = connectdb.sqlQueryStatExecute(sqlquerystat);
   		 	while(result.next()) {
	   		 	int meetingID = result.getInt("news_id");
		        java.sql.Date recorddate = result.getDate("¼������"); 
	            LocalDate start = recorddate.toLocalDate();
	            String title = result.getString("���ű���");
	            String description = result.getString("��������");
	            if(Strings.isNullOrEmpty(description))
	            	description = "����";
	            String keywords = result.getString("�ؼ���");
	            String slackurl = result.getString("SLACK����");
	            String ownercodes = result.getString("�������");
	            int type;
	            if(ownercodes.contains("wwwwww"))
	            	type = Meeting.CHANGQIJILU;
	            else if(ownercodes.contains("gzgzgz"))
	            	type = Meeting.JINQIGUANZHU;
	            else
	            	type = Meeting.DAPANNEWS;
	            
	            InsertedMeeting newmeeting = new InsertedMeeting(
		                new Meeting(title, start,  description, keywords, new HashSet<InsertedMeeting.Label>(),slackurl,ownercodes,type), meetingID);

	            meetings.add(newmeeting);
   		 	}
   		 
   		 CachedRowSetImpl rspd = null;
   		 if("ALL".equals(bankuaiid.toUpperCase()) ) { //������Ϣ
   			 //�����ALL����Ҫ�Ӳ�����¼�ص��ע�ж�ȡÿ���ܽᱨ��
   			 	sqlquerystat = "SELECT * FROM ������¼�ص��ע " +
					  " WHERE ��Ʊ����= '999999'" + 
					  " AND  ����  BETWEEN '" + startdate + "' AND '" + enddate + "'" +
					  " AND (�����Ƴ���־ = '�����ע' OR �����Ƴ���־ = '�Ƴ��ص�' OR �����Ƴ���־ = '�������' OR �����Ƴ���־ = '�ص��ע' )" 
					  ;

		    	logger.debug(sqlquerystat);
		    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	
		        while(rspd.next())  {
		        	int meetingID = rspd.getInt("id");
			        java.sql.Date recorddate = rspd.getDate("����"); 
		            LocalDate start = recorddate.toLocalDate();
		            String description = rspd.getString("ԭ������");
		            if(Strings.isNullOrEmpty(description))
		            	description = "����";

		            InsertedMeeting newmeeting = new InsertedMeeting(
			                new Meeting("һ���ܽ�", start,  description, "һ���ܽ�", new HashSet<InsertedMeeting.Label>(),null,"000000",Meeting.WKZONGJIE), meetingID);
//		            newmeeting.setNewsOwnerCodes(bankuaiid);
		            meetings.add(newmeeting);
		        }
		        
		        //�ҳ�ǿ��������
		        rspd = null;
		        sqlquerystat = "SELECT ǿ���ư����ɱ�.* , a��.`��Ʊ����` AS ����\r\n"
		        		+ " FROM ǿ���ư����ɱ� , a�� \r\n"
		        		+ " WHERE ���� BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
		        		+ " and ǿ���ư����ɱ�.`����` = a��.`��Ʊ����`\r\n"
		        		+ ""
		        		+ " UNION \r\n"
		        		+ " SELECT ǿ���ư����ɱ�.* , ͨ���Ű���б�.`�������` AS ���� \r\n"
		        		+ " FROM ǿ���ư����ɱ� , ͨ���Ű���б� \r\n"
		        		+ "WHERE ���� BETWEEN '" + startdate + "' AND '" + enddate + "'\r\n"
		        		+ " and ǿ���ư����ɱ�.`����` = ͨ���Ű���б�.`���ID` \r\n"
		        		;
		        logger.debug(sqlquerystat);
				rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
				while(rspd.next())  {
					 int meetingID = rspd.getInt("id");
					 String nodecode = rspd.getString("����");
					 String nodename = rspd.getString("����");
				     java.sql.Date recorddate = rspd.getDate("����"); 
			         LocalDate start = recorddate.toLocalDate();
			         String shuoming = rspd.getString("˵��");
			         
			         String leixing = rspd.getString("ǿ������").trim();
			         String owner = null; int type = 0;
			         if(leixing.contains("ǿ�ư��")) {
			        	 type = Meeting.QIANSHI;
			        	 owner = nodecode + "bk";
			         } else if(leixing.contains("ǿ�Ƹ���")) {
			        	 type = Meeting.QIANSHI;
			        	 owner = nodecode + "gg";
			         } else if(leixing.contains("���ư��")) {
			        	 type = Meeting.RUOSHI;
			        	 owner = nodecode + "gg";
			         } else if(leixing.contains("���Ƹ���")) {
			        	 type = Meeting.RUOSHI;
			        	 owner = nodecode + "gg";
			         }
			         
			         InsertedMeeting newmeeting = new InsertedMeeting(
				                new Meeting( owner.subSequence(0, 6) + "/" + nodename, start,  shuoming, leixing, 
				                		new HashSet<InsertedMeeting.Label>(),null,owner,type), meetingID);
			         meetings.add(newmeeting);
				 }
				 
				 //�ҳ�ָ���ؼ�����  
				meetings.addAll( this.getZhiShuKeyDates (startdate, enddate) );
				
//				 rspd = null;
//			     sqlquerystat = "SELECT * FROM ָ���ؼ����ڱ� \r\n"
//			        		+ "WHERE ���� BETWEEN '" + startdate + "' AND '" + enddate + "'";
//			        		;
//			     logger.debug(sqlquerystat);
//				 rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
//				 while(rspd.next())  {
//					 int meetingID = rspd.getInt("id");
//					 String nodecode = rspd.getString("����");
//				     java.sql.Date recorddate = rspd.getDate("����"); 
//			         LocalDate start = recorddate.toLocalDate();
//			         String shuoming = rspd.getString("˵��");
//			         
//			         InsertedMeeting newmeeting = new InsertedMeeting(
//				                new Meeting("ָ���ؼ�����:"+ nodecode, start,  shuoming, "ָ���ؼ�����", new HashSet<InsertedMeeting.Label>(),null,nodecode,Meeting.ZHISHUDATE), meetingID);
//			         meetings.add(newmeeting);
//				 }
				 
   		 }
   		
   		 
   		//label 	 
   		 for (InsertedMeeting m : meetings) {
         	int meetingid = m.getID();
         	int meetingtype = m.getMeetingType();
         	
         	String area = null ;
         	if(meetingtype == Meeting.DAPANNEWS || meetingtype == Meeting.JINQIGUANZHU || meetingtype == Meeting.CHANGQIJILU)
         		area = "��ҵ����";
         	else if(meetingtype == Meeting.ZHISHUDATE)
         		area = "ָ���ؼ�����";
         	else if(meetingtype == Meeting.QIANSHI || meetingtype == Meeting.RUOSHI)
         		area = "ǿ���ư�����";
         	
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
	 * ���Ӱ������
	 */
	public InsertedMeeting addBanKuaiNews (Meeting meeting)
	{
		String newsownercode = meeting.getNewsOwnerCodes();
		LocalDate newdate = meeting.getStart();
    	String title = meeting.getTitle();
    	String description = meeting.getDescription().replace("'", " ");
    	String keywords = meeting.getKeyWords();
    	String slackurl = meeting.getSlackUrl();
    	
    	InsertedMeeting insertedMeeting = null;
    	
    	if(meeting.getMeetingType() == Meeting.ZHISHUDATE ) { //����ָ���ؼ����ڱ�
    		try {
    			String sqlinsertstat = "INSERT INTO ָ���ؼ����ڱ�(����,����,˵��) VALUES ("
        				+ "'"  + newsownercode.substring(0, 6)  + "',"
        				+ "'"  + newdate  + "',"
        				+ "'"  + description  + "'"
        				+ ")"
        				;
        		
        		logger.debug(sqlinsertstat);
    			int meetingID = connectdb.sqlInsertStatExecute(sqlinsertstat) ;
    			int labelid = 0;
    			String area = "ָ���ؼ�����";
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
    	
    	if( meeting.getMeetingType() == Meeting.QIANSHI ||  meeting.getMeetingType() == Meeting.RUOSHI) { //����ǿ���ư����ɱ�
    		String leixing = null; 
    		if(meeting.getMeetingType() == Meeting.QIANSHI) {
    			if(newsownercode.toLowerCase().contains("bk")) { //ָ���͸��ɴ��������ͬ��ǰ��ͨ���Ӻ�׺������
    				leixing = "ǿ�ư��";
    			} else
    				leixing = "ǿ�Ƹ���";
    		}
    		if(meeting.getMeetingType() == Meeting.RUOSHI) {
    			if(newsownercode.toLowerCase().contains("bk")) { //ָ���͸��ɴ��������ͬ��ǰ��ͨ���Ӻ�׺������
    				leixing = "���ư��";
    			} else
    				leixing = "���Ƹ���";
    		}
    		
    		try {
    			String sqlinsertstat = "INSERT INTO ǿ���ư����ɱ�(����,����,ǿ������,˵��) VALUES ("
        				+ "'"  + newsownercode.subSequence(0, 6)  + "'" + ","
        				+ "'"  + newdate  + "'" + ","
        				+ "'" + leixing + "'" + ","
        				+ "'"  + description  + "'"
        				+ ")"
        				;
        		
        		logger.debug(sqlinsertstat);
    			int meetingID = connectdb.sqlInsertStatExecute(sqlinsertstat) ;
    			int labelid = 0;
    			String area = "ǿ���ư�����";
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
    	
    	//�������ŵ���ҵ���ű�
		try {
				String sqlinsertstat = "INSERT INTO ��ҵ����(���ű���,�ؼ���,SLACK����,¼������,�������,��������) values ("
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
				String area = "��ҵ����";
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
	public InsertedMeeting deleteBanKuaiNews(InsertedMeeting meeting)
	{
		InsertedMeeting deletedMeeting = null;
        
		if(meeting.getMeetingType() == Meeting.DAPANNEWS || meeting.getMeetingType() == Meeting.CHANGQIJILU 
				|| meeting.getMeetingType() == Meeting.JINQIGUANZHU ) {
			int newsid = meeting.getID();
	    	
			
				try {
					String deletestat = "DELETE  FROM ��ҵ���� WHERE news_id =" + newsid;
					logger.debug(deletestat);
					connectdb.sqlDeleteStatExecute (deletestat);

					String sqlstatement = "DELETE FROM meetingLabel "
											+ " WHERE news_ID = " + newsid 
											+ " AND fromtable = '��ҵ����' "
											;
					int key = connectdb.sqlDeleteStatExecute(sqlstatement);
					
					deletedMeeting = meeting;
				}catch(java.lang.NullPointerException e){ 
			    	e.printStackTrace();
			    } catch(Exception e){
			    	e.printStackTrace();
			    }  finally {
			    
			    }
				
			
		}
		
		if(meeting.getMeetingType() == Meeting.QIANSHI || meeting.getMeetingType() == Meeting.RUOSHI) {
			int newsid = meeting.getID();
	    	
			
				try {
					String deletestat = "DELETE  FROM ǿ���ư����ɱ�  WHERE id =" + newsid;
					logger.debug(deletestat);
					connectdb.sqlDeleteStatExecute (deletestat);

					String sqlstatement = "DELETE FROM meetingLabel "
											+ " WHERE news_ID = " + newsid 
											+ " AND fromtable = 'ǿ���ư�����' "
											;
					int key = connectdb.sqlDeleteStatExecute(sqlstatement);
					
					deletedMeeting = meeting;
				}catch(java.lang.NullPointerException e){ 
			    	e.printStackTrace();
			    } catch(Exception e){
			    	e.printStackTrace();
			    }  finally {
			    
			    }
		}
		
		if(meeting.getMeetingType() == Meeting.ZHISHUDATE) {
			int newsid = meeting.getID();
	    	
			
				try {
					String deletestat = "DELETE  FROM ָ���ؼ����ڱ�  WHERE id =" + newsid;
					logger.debug(deletestat);
					connectdb.sqlDeleteStatExecute (deletestat);

					String sqlstatement = "DELETE FROM meetingLabel "
											+ " WHERE news_ID = " + newsid 
											+ " AND fromtable = 'ָ���ؼ�����' "
											;
					int key = connectdb.sqlDeleteStatExecute(sqlstatement);
					
					deletedMeeting = meeting;
				}catch(java.lang.NullPointerException e){ 
			    	e.printStackTrace();
			    } catch(Exception e){
			    	e.printStackTrace();
			    }  finally {
			    
			    }
		}
    		
		return deletedMeeting;
	}
	/*
	 * 
	 */
	public InsertedMeeting updateMeeting(InsertedMeeting meeting) throws SQLException 
	{
    	
    	InsertedMeeting updatedMeeting = null;
    	LocalDate starttime = meeting.getStart();
		String title = meeting.getTitle();
		String desc = meeting.getDescription();
		String keywordsurl = meeting.getKeyWords();
		String newsowners = meeting.getNewsOwnerCodes();
		String slackurl = meeting.getSlackUrl();
		int newsid = meeting.getID();
		logger.debug("test");
    	
    	if(meeting.getMeetingType() == Meeting.QIANSHI || meeting.getMeetingType() == Meeting.RUOSHI) {
    		try {
        		
        		String sqlupatestatement =  "UPDATE ǿ���ư����ɱ�  SET ���� = '" + starttime + "', "
        				+ " ���� = '" + newsowners.subSequence(0, 6) + "', "
        				+ " ˵��  = '" + desc + "', "
        				+ " ǿ������ = '" + keywordsurl + "'"  
        				+ " WHERE id =  " + newsid
        				;
        		
        		connectdb.sqlUpdateStatExecute(sqlupatestatement);
        		
        		sqlupatestatement = "DELETE FROM meetingLabel WHERE news_ID = " + newsid
        							+ " AND fromtable = 'ǿ���ư�����' "
        							;
        		connectdb.sqlUpdateStatExecute(sqlupatestatement);

              for (InsertedMeeting.Label l : meeting.getLabels()) {
                  int labelid = l.getID();
                  sqlupatestatement = "INSERT INTO meetingLabel (news_id, LABEL_ID ,fromtable) VALUES ("
    			            		    +  newsid + ","
    									+  labelid  + ","
    									+ " 'ǿ���ư�����' " 
    									+ ")"
                		  				;
                	connectdb.sqlUpdateStatExecute(sqlupatestatement);
              }
        		
              updatedMeeting = meeting;

    		}catch(java.lang.NullPointerException e){ 
    	    	e.printStackTrace();
    	    } catch(Exception e){
    	    	e.printStackTrace();
    	    }  finally {
    	    
    	    }

    	}
    	
    	if(meeting.getMeetingType() == Meeting.ZHISHUDATE) {
    		
    		String sqlupatestatement =  "UPDATE ָ���ؼ����ڱ�  SET ���� = '" + starttime + "', "
    				+ " ����  = '" + newsowners + "', "
    				+ " ˵��  = '" + desc + "' "
    				+ " WHERE id =  " + newsid
    				;
    		
    		connectdb.sqlUpdateStatExecute(sqlupatestatement);
    		
    		sqlupatestatement = "DELETE FROM meetingLabel WHERE news_ID = " + newsid 
    							+ " AND fromtable = 'ָ���ؼ�����' "
    							;
    		connectdb.sqlUpdateStatExecute(sqlupatestatement);

          for (InsertedMeeting.Label l : meeting.getLabels()) {
              int labelid = l.getID();
              sqlupatestatement = "INSERT INTO meetingLabel (news_id, LABEL_ID,fromtable ) VALUES ("
			            		    +  newsid + ","
									+  labelid  + ","
									+ "'ָ���ؼ�����')"
            		  				;
            	connectdb.sqlUpdateStatExecute(sqlupatestatement);
          }

    		
    	}
    	
    	if(meeting.getMeetingType() == Meeting.DAPANNEWS || meeting.getMeetingType() == Meeting.CHANGQIJILU
    			|| meeting.getMeetingType() == Meeting.JINQIGUANZHU ) {
    		try {

        		String sqlupatestatement =  "UPDATE ��ҵ����  SET ¼������ = '" + starttime + "', "
        				+ " ���ű��� = '" + title + "', "
        				+ " �������� = '" + desc + "', "
        				+ " �ؼ��� = '" + keywordsurl + "',"  
        				+ " �������= '" + newsowners + "',"
        				+ " SLACK����= '" + slackurl + "'"
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
    									+ "'��ҵ����')"
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

    	}
    	
        return updatedMeeting;
    }

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
