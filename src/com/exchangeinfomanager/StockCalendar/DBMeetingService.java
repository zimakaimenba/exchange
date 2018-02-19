package com.exchangeinfomanager.StockCalendar;

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
    public Collection<InsertedMeeting> getMeetings() throws SQLException {
        return this.database.getMeetings();
    }

    @Override
    public void createMeeting(Meeting meeting) throws SQLException {
        InsertedMeeting m  = this.database.createMeeting(meeting);
        
          cache.addMeeting(m);
    }

    @Override
    public void deleteMeeting(InsertedMeeting meeting) throws SQLException {
        InsertedMeeting m = this.database.deleteMeeting(meeting);
        
        cache.removeMeeting(m);
    }

    @Override
    public void updateMeeting(InsertedMeeting meeting) throws SQLException {
        InsertedMeeting m = this.database.updateMeeting(meeting);
        
        	cache.updateMeeting(m);
    }



}
