package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.labelmanagement.TagsNewsDbOperation;
import com.google.common.base.Splitter;

@SuppressWarnings("all")
public class DBMeetingService  implements EventService {

    private static Logger LOGGER = Logger.getLogger(DBMeetingService.class.getSimpleName());

    private StockCalendarAndNewDbOperation database;
    private TagsNewsDbOperation tagsdboptfornews;

	private Cache cache;
    
    public DBMeetingService()  {
        super();
        this.database = new StockCalendarAndNewDbOperation ();
        this.tagsdboptfornews = new TagsNewsDbOperation ();
    }

    public void setCache (Cache cache)
    {
    	this.cache = cache;
    }
    @Override
    public Collection<InsertedMeeting> getMeetings(String nodeid,LocalDate startdate, LocalDate enddate, Integer[] eventtype) throws SQLException 
    {
//        return this.database.getMeetings();
        return this.database.getRequiredRelatedInfoForNewsAndOthers (nodeid, startdate,  enddate,eventtype);
    }

    @Override
    public void createMeeting(Meeting meeting) throws SQLException {

    	 InsertedMeeting m  = this.database.createRequiredRelatedInfoForNewsAndOthers (meeting);
    	 this.tagsdboptfornews.storeNewsKeyWordsToDataBase (m);
        
    	 if(m != null && cache != null)
    		 cache.addMeeting(m);
    }
 
    @Override
    public void deleteMeeting(InsertedMeeting meeting) throws SQLException {
        
        InsertedMeeting m = this.database.deleteRequiredRelatedInfoForNewsAndOthers(meeting);
        this.tagsdboptfornews.deleteKeyWordsMapsOfDeletedNews (m);
        
        if(m != null && cache != null)
        	cache.removeMeeting(m);
    }

    @Override
    public void updateMeeting(InsertedMeeting meeting) throws SQLException {
    	//先要把KEYWORDS存下来，以防止用户删除改动，好让tagservice知道
    	String oldkw = meeting.getKeyWords();
    	
        InsertedMeeting m = this.database.updateRequiredRelatedInfoForNewsAndOthers(meeting);
        
        String newkw = m.getKeyWords();
        
        //这里没有完成的是用户改动KW后的操作，比较繁琐，暂时没做
//        List<String> tmpkwlist = Splitter.on(" ").omitEmptyStrings().splitToList(keywords);
        
        if(m != null && cache != null) 
        	cache.updateMeeting(m);
    }
    
    



}
