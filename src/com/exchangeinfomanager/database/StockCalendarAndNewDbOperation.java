package com.exchangeinfomanager.database;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;
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
public final class StockCalendarAndNewDbOperation {

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
	
	public Collection<InsertedMeeting> getBanKuaiRelatedNews(String bankuaiid) 
	{
		Collection<InsertedMeeting> meetings = new HashSet<>();
		
		String sqlquerystat;
		if("ALL".equals(bankuaiid.toUpperCase()) ) 
			sqlquerystat = "SELECT * FROM ��ҵ����   WHERE ¼������ >= DATE(NOW()) - INTERVAL 180 DAY ORDER BY  ¼������ DESC"
									;
		else
			sqlquerystat = "SELECT * FROM ��ҵ����   WHERE ������� like '%" + bankuaiid.trim() +  "%' ORDER BY  ¼������ DESC";
		
		CachedRowSetImpl result = null;
		try {
			result = connectdb.sqlQueryStatExecute(sqlquerystat);
   		 	while(result.next()) {
	   		 	int meetingID = result.getInt("news_id");
		        java.sql.Date startdate = result.getDate("¼������"); 
	            LocalDate start = startdate.toLocalDate();
	            String title = result.getString("���ű���");
	            String description = result.getString("��������");
	            if(Strings.isNullOrEmpty(description))
	            	description = "����";
	            String location = result.getString("�ؼ���");
	            String slackurl = result.getString("SLACK����");
	            String ownercodes = result.getString("�������");
	            
	            InsertedMeeting newmeeting = new InsertedMeeting(
		                new Meeting(title, start,  description, location, new HashSet<InsertedMeeting.Label>(),slackurl,ownercodes), meetingID);
	            newmeeting.setCurrentownercode(bankuaiid);
	            meetings.add(newmeeting);
   		 	}
   		 	
   		 for (InsertedMeeting m : meetings) {
         	int meetingid = m.getID();
         	sqlquerystat = "SELECT label.* FROM label INNER JOIN meetingLabel ON label.LABEL_ID = meetingLabel.LABEL_ID " +
                     "WHERE meetingLabel.NEWS_ID = " + meetingid; 
         			;
         	
         	CachedRowSetImpl set = connectdb.sqlQueryStatExecute(sqlquerystat);
         	
//             statement = connection.prepareStatement(
//                 "SELECT label.* FROM label INNER JOIN meetingLabel ON label.LABEL_ID = meetingLabel.LABEL_ID " +
//                     "WHERE meetingLabel.MEETING_ID = ? AND label.USER_ID = ?");
//             statement.setInt(1, m.getID());
//             statement.setInt(2, userID);
//
//             ResultSet set = statement.executeQuery();
             while (set.next()) {
                 int labelID = set.getInt("LABEL_ID");
                 String name = set.getString("NAME");
                 Color colour = Color.decode(set.getString("COLOUR"));
                 boolean active = set.getBoolean("ACTIVE");
                 InsertedMeeting.Label label = new InsertedMeeting.Label(new Meeting.Label(name, colour, active), labelID);
                 m.getLabels().add(label);
             }
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
		String newsownercode = meeting.getNewsownercodes();
		LocalDate newdate = meeting.getStart();
    	String title = meeting.getTitle();
    	String description = meeting.getDescription();
    	String keywords = meeting.getLocation();
    	String slackurl = meeting.getSlackUrl();
    	
    	InsertedMeeting insertedMeeting = null;
//		if( newsownercode.length() == 6) { //ֻ��һ��code,˵�����µ�NEWS 
			
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
				 for (InsertedMeeting.Label label : meeting.getLabels()) {
		                labelid = label.getID();
		                String sqlstatementlabel = "INSERT INTO meetingLabel (news_ID, LABEL_ID) VALUES( " + meetingID + "," + labelid + ")";
		   			 	
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

	public InsertedMeeting deleteBanKuaiNews(InsertedMeeting meeting)
	{
		InsertedMeeting deletedMeeting = null;
        
    	int newsid = meeting.getID();
    	String myowncode = "ALL";
		if("ALL".equals(myowncode.toUpperCase())) { //ɾ��������
			try {
				String deletestat = "DELETE  FROM ��ҵ���� WHERE news_id =" + newsid;
				logger.debug(deletestat);
				connectdb.sqlDeleteStatExecute (deletestat);

				String sqlstatement = "DELETE FROM meetingLabel WHERE news_ID = " + newsid
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

	public InsertedMeeting updateMeeting(InsertedMeeting meeting) throws SQLException 
	{
    	
    	InsertedMeeting updatedMeeting = null;
    	
    	try {
    		LocalDate starttime = meeting.getStart();
    		String title = meeting.getTitle();
    		String desc = meeting.getDescription();
    		String keywordsurl = meeting.getLocation();
    		String newsowners = meeting.getNewsownercodes();
    		String slackurl = meeting.getSlackUrl();
    		int newsid = meeting.getID();
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

          for (InsertedMeeting.Label l : meeting.getLabels()) {
              int labelid = l.getID();
              sqlupatestatement = "INSERT INTO meetingLabel (news_id, LABEL_ID ) VALUES ("
			            		    +  newsid + ","
									+  labelid  
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
    
    /*
	 * ԭ���ȵ������ͷ���ɶ��Ǳ������ص��ע�����棬���湦�����ӣ���2����Ҫ���浽��ҵ�������棬ǰ�˽�����ʱ���䣬��̨�����������
	 */
	public InsertedMeeting setReDianBanKuaiLongTouGeGuToShangYeXinWen (JiaRuJiHua jiarujihua)
	{
		String newsownercode = jiarujihua.getStockCode();		
		String zdgzsign = jiarujihua.getGuanZhuType().trim();
		String description = jiarujihua.getJiHuaShuoMing();
		LocalDate newdate = jiarujihua.getJiaRuDate();
		
		
    	String title = zdgzsign +  newsownercode; //�ȵ���880623
    	String keywords = zdgzsign + " " + newsownercode + " "; //�ȵ���
    	
    	InsertedMeeting insertedMeeting = null;
 
    	//��ȡ������name
    	CachedRowSetImpl rs_gn = null;
		try  { 
			String sqlquerystat = null	;
			if(zdgzsign.contains("���" )) {
				sqlquerystat = "Select ������� AS '����' FROM  ͨ���Ű���б� WHERE ���ID = '" + newsownercode + "'"
								;
			} else if(zdgzsign.contains("����") ) {
				sqlquerystat = "Select ��Ʊ���� AS '����' FROM  A��  WHERE ��Ʊ���� = '" + newsownercode + "'"
								;
			}
			
			rs_gn = connectdb.sqlQueryStatExecute(sqlquerystat);
			
	        while(rs_gn.next()) {
	        	String bkname = rs_gn.getString(1);
	        	title = title +  bkname ; //�ȵ���880623������
	        	keywords = keywords + " " + bkname + " ";
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
		//��ȡ��Ӧ��label
		int bkgglableid = 0;
		InsertedMeeting.Label label = null;
		try  { 
			HashMap<String,String> labelmap = new HashMap<String,String> (); 
			String sqlquerystat = "Select * FROM  label" 
								;
			rs_gn = connectdb.sqlQueryStatExecute(sqlquerystat);
			
	        while(rs_gn.next()) {
	        	String labelname = rs_gn.getString("NAME");
	        	if(labelname.equals(zdgzsign)) {
	        		int labelid = rs_gn.getInt("LABEL_ID");
	        		bkgglableid = labelid;
	        		Color colour = Color.decode(rs_gn.getString("COLOUR"));
	                boolean active = rs_gn.getBoolean("ACTIVE");
	                label = new InsertedMeeting.Label(new Meeting.Label(labelname, colour, active), labelid);
	                
	        		break;
	        	}
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
		
		
			
		try {
			String sqlinsertstat = "INSERT INTO ��ҵ����(���ű���,�ؼ���,¼������,�������,��������) values ("
						+ "'" + title + "'" + ","
						+ "'" + keywords + "'" + ","
						+ "'" +  newdate + "'" + ","
						+ "'" +  newsownercode +  "|'" + ","
						+ "'" +  description + "'"
						+ ")"
						;
				logger.debug(sqlinsertstat);
				int meetingID = connectdb.sqlInsertStatExecute(sqlinsertstat) ;
				if(bkgglableid != 0) {
		                String sqlstatementlabel = "INSERT INTO meetingLabel (news_ID, LABEL_ID) VALUES( " + meetingID + "," + bkgglableid + ")";
		   			 	
		                connectdb.sqlInsertStatExecute(sqlstatementlabel);
				 }
				 
				 Meeting meeting = new Meeting(title,newdate,
						 description, keywords, new HashSet<>(),"SlackURL",newsownercode);
		         insertedMeeting = new InsertedMeeting(meeting, meetingID);
		         insertedMeeting.getLabels().add(label);
				
			}catch(java.lang.NullPointerException e){ 
		    	e.printStackTrace();
		    } catch(Exception e){
		    	e.printStackTrace();
		    }  finally {
		    	
		    }
		return insertedMeeting;
	}
}
