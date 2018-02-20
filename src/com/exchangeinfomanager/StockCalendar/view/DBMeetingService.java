package com.exchangeinfomanager.StockCalendar.view;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Logger;

import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;

@SuppressWarnings("all")
public class DBMeetingService  implements MeetingService {

    private static Logger LOGGER = Logger.getLogger(DBMeetingService.class.getSimpleName());

    private StockCalendarAndNewDbOperation database;

	private Cache cache;
    
    public DBMeetingService()  {
        super();
        this.database = new StockCalendarAndNewDbOperation ();
    }

    public void setCache (Cache cache)
    {
    	this.cache = cache;
    }
    @Override
    public Collection<InsertedMeeting> getMeetings(String nodeid) throws SQLException {
//        return this.database.getMeetings();
        return this.database.getBanKuaiRelatedNews (nodeid);
    }

    @Override
    public void createMeeting(Meeting meeting) throws SQLException {
//        InsertedMeeting m  = this.database.createMeeting(meeting);
    	 InsertedMeeting m  = this.database.addBanKuaiNews (meeting);
        
          cache.addMeeting(m);
    }

    @Override
    public void deleteMeeting(InsertedMeeting meeting) throws SQLException {
//        InsertedMeeting m = this.database.deleteMeeting(meeting);
        
        InsertedMeeting m = this.database.deleteBanKuaiNews(meeting);
        
        cache.removeMeeting(m);
    }

    @Override
    public void updateMeeting(InsertedMeeting meeting) throws SQLException {
        InsertedMeeting m = this.database.updateMeeting(meeting);
        
        cache.updateMeeting(m);
    }



}
