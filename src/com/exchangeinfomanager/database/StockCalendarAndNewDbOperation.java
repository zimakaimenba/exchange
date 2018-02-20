package com.exchangeinfomanager.database;

import com.exchangeinfomanager.StockCalendar.view.InsertedMeeting;
import com.exchangeinfomanager.StockCalendar.view.Meeting;
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
			sqlquerystat = "SELECT * FROM 商业新闻   WHERE 录入日期 >= DATE(NOW()) - INTERVAL 40 DAY ORDER BY  录入日期 DESC"
									;
		else
			sqlquerystat = "SELECT * FROM 商业新闻   WHERE 关联板块 like '%" + bankuaiid.trim() +  "%' ORDER BY  录入日期 DESC";
		
		CachedRowSetImpl result = null;
		try {
			result = connectdb.sqlQueryStatExecute(sqlquerystat);
   		 	while(result.next()) {
	   		 	int meetingID = result.getInt("news_id");
		        java.sql.Date startdate = result.getDate("录入日期"); 
	            LocalDate start = startdate.toLocalDate();
	            String title = result.getString("新闻标题");
	            String description = result.getString("具体描述");
	            if(Strings.isNullOrEmpty(description))
	            	description = "描述";
	            String location = result.getString("关键词");
	            String slackurl = result.getString("SLACK链接");
	            String ownercodes = result.getString("关联板块");
	            
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

//    public Collection<InsertedMeeting> getMeetings()  {
//
//        Collection<InsertedMeeting> meetings = new HashSet<>();
////        Connection connection = DriverManager.getConnection(this.url);
////        PreparedStatement statement = null;
//
//        String sqlstatement = "SELECT MEETING.* FROM MEETING  WHERE ISDELETED = 0 "
//                ;
//        
//        
//        CachedRowSetImpl result = null;
//		try {
//			result = connectdb.sqlQueryStatExecute(sqlstatement);
//			 
//			while(result.next()) {
//		        int meetingID = result.getInt("MEETING_ID");
//		        java.sql.Date startdate = result.getDate("START_TIME"); 
//                LocalDate start = startdate.toLocalDate();
//                String title = result.getString("TITLE");
//                String description = result.getString("DESCRIPTION");
//                String location = result.getString("LOCATION");
//                String slackurl = result.getString("SLACK链接");
//                meetings.add(new InsertedMeeting(
//                    new Meeting(title, start,  description, location, new HashSet<InsertedMeeting.Label>(),slackurl), meetingID));
//			}
//			
//            for (InsertedMeeting m : meetings) {
//            	int meetingid = m.getID();
//            	sqlstatement = "SELECT label.* FROM label INNER JOIN meetingLabel ON label.LABEL_ID = meetingLabel.LABEL_ID " +
//                        "WHERE meetingLabel.NEWS_ID = " + meetingid; 
//            			;
//            	
//            	CachedRowSetImpl set = connectdb.sqlQueryStatExecute(sqlstatement);
//            	
////                statement = connection.prepareStatement(
////                    "SELECT label.* FROM label INNER JOIN meetingLabel ON label.LABEL_ID = meetingLabel.LABEL_ID " +
////                        "WHERE meetingLabel.MEETING_ID = ? AND label.USER_ID = ?");
////                statement.setInt(1, m.getID());
////                statement.setInt(2, userID);
////
////                ResultSet set = statement.executeQuery();
//                while (set.next()) {
//                    int labelID = set.getInt("LABEL_ID");
//                    String name = set.getString("NAME");
//                    Color colour = Color.decode(set.getString("COLOUR"));
//                    boolean active = set.getBoolean("ACTIVE");
//                    InsertedMeeting.Label label = new InsertedMeeting.Label(new Meeting.Label(name, colour, active), labelID);
//                    m.getLabels().add(label);
//                }
//            }
//            
//            
//		}catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    } catch(Exception e){
//	    	e.printStackTrace();
//	    }  finally {
//	    	try {
//	    		result.close();
//	    		result = null;
//	    		
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//	    }

        
//        try {
//            statement = connection.prepareStatement(
//                "SELECT MEETING.* FROM MEETING INNER JOIN userMeeting ON MEETING.MEETING_ID = userMeeting" + "" + ""
//                    + ".MEETING_ID INNER JOIN USER ON userMeeting.USER_ID = USER.USER_ID WHERE (ISDELETED = 0 AND" +
//                    " " + "" + "" + "" + "USER.USER_ID = ? )");
//
//            statement.setInt(1, userID);
//            ResultSet result = statement.executeQuery();
//
//            while (result.next()) {
//                int meetingID = result.getInt("MEETING_ID");
//                Instant start = result.getTimestamp("START_TIME").toInstant();
//                Instant end = result.getTimestamp("FINISH_TIME").toInstant();
//                String title = result.getString("TITLE");
//                String description = result.getString("DESCRIPTION");
//                String location = result.getString("LOCATION");
//                meetings.add(new InsertedMeeting(
//                    new Meeting(title, start, end, description, location, new HashSet<InsertedMeeting.Label>()), meetingID));
//            }
//
//            for (InsertedMeeting m : meetings) {
//                statement = connection.prepareStatement(
//                    "SELECT label.* FROM label INNER JOIN meetingLabel ON label.LABEL_ID = meetingLabel.LABEL_ID " +
//                        "WHERE meetingLabel.MEETING_ID = ? AND label.USER_ID = ?");
//                statement.setInt(1, m.getID());
//                statement.setInt(2, userID);
//
//                ResultSet set = statement.executeQuery();
//                while (set.next()) {
//                    int labelID = set.getInt("LABEL_ID");
//                    String name = set.getString("NAME");
//                    Color colour = Color.decode(set.getString("COLOUR"));
//                    boolean active = set.getInt("ACTIVE") == 1 ? true : false;
//                    InsertedMeeting.Label label = new InsertedMeeting.Label(new Meeting.Label(name, colour, active), labelID);
//                    m.getLabels().add(label);
//                }
//            }
//
//
//        } finally {
//            this.closeResources(statement, connection);
//        }
//
//        LOGGER.log(Level.INFO, "Database: query was successful [SELECT * FROM MEETING]");
//
//        return meetings;
//    }


    /**
     * Helper method which closes resources. Should be called in finally clause.
     *
     * @param stmt to be closed
     * @param con  connection to be closed
     * @throws SQLException
     */
//    private void closeResources(Statement stmt, Connection con) throws SQLException {
//
//        if (stmt != null)
//            stmt.close();
//        if (con != null)
//            con.close();
//    }


//    public InsertedMeeting createMeeting(Meeting meeting) throws SQLException 
//    {
//    	LocalDate startdate = meeting.getStart();
//    	String meetingtitle = meeting.getTitle();
//    	String description = meeting.getDescription();
//    	String keywordsurl = meeting.getDescription();
//    	
//    	String sqlstatementmeeting = "INSERT INTO meeting (START_TIME, TITLE, DESCRIPTION, LOCATION, ISDELETED) VALUES " 
//                + "('" + startdate + "'"
//                + "'" + meetingtitle + "'"
//                + "'" + description + "'"
//                + "'" + keywordsurl + "'"
//                + ", 0)"
//                ;
//
//    	InsertedMeeting insertedMeeting = null;
//		try {
//			int meetingID = connectdb.sqlInsertStatExecute(sqlstatementmeeting);
//
//			 int labelid = 0;
//			 for (InsertedMeeting.Label label : meeting.getLabels()) {
//	                labelid = label.getID();
//	                String sqlstatementlabel = "INSERT INTO meetingLabel (MEETING_ID, LABEL_ID) VALUES( " + meetingID + "," + labelid + ")";
//	   			 	
//	                connectdb.sqlInsertStatExecute(sqlstatementlabel);
//			 }
//			 
//	         insertedMeeting = new InsertedMeeting(meeting, meetingID);
//		}catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    } catch(Exception e){
//	    	e.printStackTrace();
//	    }  finally {
//	    	
//	    }

//        Connection connection = DriverManager.getConnection(this.url);
//
//        PreparedStatement statement = null;
//        InsertedMeeting insertedMeeting = null;
//
//        try {
//            statement = connection.prepareStatement(
//                "INSERT INTO meeting (START_TIME, FINISH_TIME, TITLE, DESCRIPTION, LOCATION, ISDELETED) VALUES " + ""
//                    + "(?, ?, ?, ?, ?, 0)");
//
//            statement.setTimestamp(1, Timestamp.from(meeting.getStart()));
//            statement.setTimestamp(2, Timestamp.from(meeting.getEnd()));
//            statement.setString(3, meeting.getTitle());
//            statement.setString(4, meeting.getDescription());
//            statement.setString(5, meeting.getLocation());
//            statement.executeUpdate();
//            int meetingID = statement.getGeneratedKeys().getInt(1);

//            statement = connection.prepareStatement("INSERT INTO userMeeting ( MEETING_ID,  USER_ID ) VALUES(?, ?)");
//            statement.setInt(1, meetingID);
//            statement.setInt(2, userID);
//            statement.execute();

//            statement = connection.prepareStatement("INSERT INTO meetingLabel (MEETING_ID, LABEL_ID) VALUES(?, ?)");
//            for (InsertedMeeting.Label label : meeting.getLabels()) {
//                statement.setInt(1, meetingID);
//                statement.setInt(2, label.getID());
//                statement.execute();
//            }
//
//            insertedMeeting = new InsertedMeeting(meeting, meetingID);
//
//        } finally {
//            this.closeResources(statement, connection);
//        }

//        LOGGER.log(Level.INFO, "Database: query was successful [INSERT INTO MEETING] " + meeting.getStart());
//        return insertedMeeting;
//    }
    
    /*
	 * 增加板块新闻
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
//		if( newsownercode.length() == 6) { //只有一个code,说明是新的NEWS 
			
			try {
				String sqlinsertstat = "INSERT INTO 商业新闻(新闻标题,关键词,SLACK链接,录入日期,关联板块) values ("
						+ "'" + title + "'" + ","
						+ "'" + keywords + "'" + ","
						+ "'" + slackurl  + "'" + ","
						+ "'" +  newdate + "'" + ","
						+ "'" +  newsownercode +  "'"
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
//		} 
//		else {
//			//如果板块id是不是已经在新闻的板块list里面了，已经加过了。
//			
//			String  updatestat = "UPDATE 商业新闻 "
//								  + " SET 关联板块 = "
//								  + " CASE WHEN 关联板块 like '%" + bankuaiid + "%' THEN"
//								  + " CONCAT(关联板块,  ' ') " 
//								  + " ELSE " 
//								  + " CONCAT(关联板块,  '"+ bankuaiid + "|' )"
//								  +	" END" 
//								  + " WHERE ID=" +  autoIncKeyFromApi
//								  ;
//			logger.debug(updatestat);
//			connectdb.sqlUpdateStatExecute (updatestat);
//			
//			insertedMeeting = new InsertedMeeting(meeting, autoIncKeyFromApi);
//		}
		
		return insertedMeeting;
	}

	public InsertedMeeting deleteBanKuaiNews(InsertedMeeting meeting)
	{
		InsertedMeeting deletedMeeting = null;
        
    	int newsid = meeting.getID();
    	String myowncode = "ALL";
		if("ALL".equals(myowncode.toUpperCase())) { //删除该新闻
			try {
				String deletestat = "DELETE  FROM 商业新闻 WHERE news_id =" + newsid;
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
//		else { //删除在所有板块里的新闻
//			String updatestat = "UPDATE 商业新闻  SET 关联板块 = REPLACE (关联板块, '" + myowncode.trim() + "|', ' ' ) WHERE news_id=" + newsid;
//			logger.debug(updatestat);
//			connectdb.sqlDeleteStatExecute(updatestat);
//			
//			deletedMeeting = meeting;
//		}
		
		return deletedMeeting;
	}

//    public InsertedMeeting deleteMeeting(InsertedMeeting meeting) throws SQLException 
//    {
//    	 InsertedMeeting deletedMeeting = null;
//        
//    	int meetingid = meeting.getID();
//        
//		try {
//			String sqlstatement = "DELETE FROM meetingLabel WHERE MEETING_ID = " + meetingid
//	                ;
//			int key = connectdb.sqlDeleteStatExecute(sqlstatement);
//			
//			sqlstatement = "UPDATE MEETING SET ISDELETED = 1 WHERE MEETING_ID = " + meetingid
//	                ;
//			key = connectdb.sqlUpdateStatExecute(sqlstatement);
//			
//			deletedMeeting = meeting;
//		}catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    } catch(Exception e){
//	    	e.printStackTrace();
//	    }  finally {
//	    
//	    }
    	
//        Connection connection = DriverManager.getConnection(this.url);
//
//        PreparedStatement statement = null;
//        InsertedMeeting deletedMeeting = null;
//
//        try {
//            statement = connection.prepareStatement("DELETE FROM userMeeting WHERE MEETING_ID = ?");
//            statement.setInt(1, meeting.getID());
//            statement.execute();
//
//            statement = connection.prepareStatement("DELETE FROM meetingLabel WHERE MEETING_ID = ?");
//            statement.setInt(1, meeting.getID());
//            statement.execute();
//
//            statement = connection.prepareStatement("UPDATE MEETING SET ISDELETED = 1 WHERE MEETING_ID = ? ");
//            statement.setInt(1, meeting.getID());
//            statement.execute();
//
//            deletedMeeting = meeting;
//
//        } finally {
//            this.closeResources(statement, connection);
//        }

//        LOGGER.log(Level.INFO, "Database: query was successful [DELETE MEETING WHERE ID=ID]");
//
//        return deletedMeeting;
//
//    }

    public InsertedMeeting updateMeeting(InsertedMeeting meeting) throws SQLException {
    	
    	InsertedMeeting updatedMeeting = null;
    	
    	try {
    		LocalDate starttime = meeting.getStart();
    		String title = meeting.getTitle();
    		String desc = meeting.getDescription();
    		String keywordsurl = meeting.getLocation();
    		String newsowners = meeting.getNewsownercodes();
    		String slackurl = meeting.getSlackUrl();
    		int newsid = meeting.getID();
    		String sqlupatestatement =  "UPDATE 商业新闻  SET 录入日期 = '" + starttime + "', "
    				+ " 新闻标题 = '" + title + "', "
    				+ " 具体描述 = '" + desc + "', "
    				+ " 关键词 = '" + keywordsurl + "',"  
    				+ " 关联板块= '" + newsowners + "',"
    				+ " SLACK链接= '" + slackurl + "'"
    				+ " WHERE news_id =  " + newsid
    				;
    		
    		connectdb.sqlUpdateStatExecute(sqlupatestatement);
    		
    		updatedMeeting = meeting;

		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    
	    }
    	

//        Connection connection = DriverManager.getConnection(this.url);
//
//        PreparedStatement statement = null;
//        InsertedMeeting updatedMeeting = null;
//
//        try {
//            statement = connection.prepareStatement(
//                "UPDATE MEETING SET START_TIME = ?, FINISH_TIME = ?, TITLE = ?, DESCRIPTION = ?, LOCATION = ? " +
//                    "WHERE MEETING_ID = ? ");
//            statement.setTimestamp(1, Timestamp.from(meeting.getStart()));
//            
//            statement.setString(3, meeting.getTitle());
//            statement.setString(4, meeting.getDescription());
//            statement.setString(5, meeting.getLocation());
//            statement.setInt(6, meeting.getID());
//            statement.execute();
//
//            statement = connection.prepareStatement("DELETE FROM meetingLabel WHERE MEETING_ID = ?");
//            statement.setInt(1, meeting.getID());
//            statement.execute();
//
//            statement = connection.prepareStatement("INSERT INTO meetingLabel (MEETING_ID, LABEL_ID ) VALUES (?, ?)");
//            for (InsertedMeeting.Label l : meeting.getLabels()) {
//                statement.setInt(1, meeting.getID());
//                statement.setInt(2, l.getID());
//                statement.execute();
//            }
//
//            updatedMeeting = meeting;
//
//        } finally {
//            this.closeResources(statement, connection);
//
//            LOGGER.log(Level.INFO, "Database: query was successful [UPDATE MEETING WHERE ID=ID]");
//        }
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
        
        
//        Connection connection = DriverManager.getConnection(this.url);
//        PreparedStatement statement = null;
//
//        try {
//            statement = connection.prepareStatement("SELECT * FROM label WHERE USER_ID = ?");
//            statement.setInt(1, userID);
//            ResultSet result = statement.executeQuery();
//
//            while (result.next()) {
//                int id = result.getInt("LABEL_ID");
//                String name = result.getString("NAME");
//                Color colour = Color.decode(result.getString("COLOUR"));
//                boolean active = result.getInt("ACTIVE") == 1 ? true : false;
//                InsertedMeeting.Label label = new InsertedMeeting.Label(new Meeting.Label(name, colour, active), id);
//                labels.add(label);
//            }
//
//        } finally {
//            this.closeResources(statement, connection);
//        }

        logger.info("Database: query was successful [SELECT * FROM LABEL]");

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
        
        
        
//        Connection connection = DriverManager.getConnection(this.url);
//        PreparedStatement statement = null;
//
//        try {
//            statement = connection.prepareStatement(
//                "INSERT INTO label (NAME, COLOUR, ACTIVE) VALUES (?, ?, 1)");
//            statement.setString(1, label.getName());
//            statement.setString(2, "#" + Integer.toHexString(label.getColor().getRGB()).substring(2));
//            statement.executeUpdate();
//            insertedLabel = new InsertedMeeting.Label(label, statement.getGeneratedKeys().getInt(1));
//
//        } finally {
//            this.closeResources(statement, connection);
//        }

        logger.info("Database: query was successful [INSERT INTO label] ");
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
    	
    	
    	
//        Connection connection = DriverManager.getConnection(this.url);
//        PreparedStatement statement = null;
//        InsertedMeeting.Label deletedLabel = null;
//
//        try {
//
//            statement = connection.prepareStatement("DELETE FROM meetingLabel WHERE LABEL_ID = ?");
//            statement.setInt(1, label.getID());
//            statement.execute();
//
//            statement = connection.prepareStatement("DELETE FROM label WHERE LABEL_ID = ? ");
//            statement.setInt(1, label.getID());
//            statement.execute();
//
//            deletedLabel = label;
//
//        } finally {
//            this.closeResources(statement, connection);
//        }

    	logger.info("Database: query was successful [DELETE label WHERE ID= " + label.getID() + "]");

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
