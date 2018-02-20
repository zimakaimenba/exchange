package com.exchangeinfomanager.StockCalendar.view;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;


@SuppressWarnings("all")
public interface MeetingService  {

    Collection<InsertedMeeting> getMeetings(String nodeid) throws SQLException;

    void createMeeting(Meeting meeting) throws SQLException;

    void deleteMeeting(InsertedMeeting meeting) throws SQLException;

    void updateMeeting(InsertedMeeting meeting) throws SQLException;
    
    void setCache (Cache cache);

}
