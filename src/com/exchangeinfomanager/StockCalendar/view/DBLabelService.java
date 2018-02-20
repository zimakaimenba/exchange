package com.exchangeinfomanager.StockCalendar.view;




import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Logger;

import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;

@SuppressWarnings("all")
public class DBLabelService implements LabelService {


    private static Logger LOGGER = Logger.getLogger(DBLabelService.class.getSimpleName());

    private StockCalendarAndNewDbOperation database;

	private Cache cache;


    public DBLabelService ()
    {
    	super();
    	this.database = new StockCalendarAndNewDbOperation ();
    }
    
    public void setCache (Cache cache)
    {
    	this.cache = cache;
    }

    @Override
    public Collection<InsertedMeeting.Label> getLabels() throws SQLException  {
        return this.database.getLabels();
    }

    @Override
    public void createLabel(Meeting.Label label) throws SQLException  {
        InsertedMeeting.Label lbl = this.database.createMeetingLabel(label);

        cache.addMeetingLabel(lbl);
    }

    @Override
    public void deleteLabel(InsertedMeeting.Label label) throws SQLException  {
        InsertedMeeting.Label lbl = this.database.deleteMeetingLabel(label);
        cache.removeMeetingLabel(lbl);
    }

    @Override
    public void updateLabel(InsertedMeeting.Label label) throws SQLException  {
        InsertedMeeting.Label lbl = this.database.updateMeetingLabel(label);
        cache.updateMeetingLabel(lbl);
    }
}
