package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@SuppressWarnings("all")
public class Cache {

    private Set<CacheListener> listeners;
    private Set<InsertedMeeting> meetings;
    private Set<InsertedMeeting.Label> meetingLabels;
    private MeetingService meetingService;
    private LabelService labelService;
	private String nodecode; //"ALL":所有新闻；"000000":每个固定月都显示的新闻。"999999":显示在jstockcalendar的新闻
	private boolean datachanged = false;
	private LocalDate cashestartdate;
	private LocalDate casheenddate;

    public Cache(String nodecode,MeetingService meetingService, LabelService labelService,LocalDate startdate, LocalDate enddate) 
    {
    	this.nodecode = nodecode;
        this.meetingService = meetingService;
        this.meetingService.setCache(this);
        this.labelService = labelService;
        this.labelService.setCache(this);
        this.listeners = new HashSet<>();
        this.meetings = new HashSet<>();
        this.meetingLabels = new HashSet<>();
        this.cashestartdate = startdate;
        this.casheenddate = enddate;
        
        this.refreshMeetings(nodecode,cashestartdate,casheenddate);
        this.refreshLabels();
    }
    /*
     * 
     */
    public void refreshNews ()
    {
//    	if(true) {
//    		
//    		this.refreshMeetings(nodecode,this.cashestartdate,this.casheenddate);
//        	this.listeners.forEach(l -> l.onMeetingChange(this));
//    	}
    }
    
    public String getNodeCode ()
    {
    	return this.nodecode;
    }
    /*
     * 
     */
    public void addCacheListener(CacheListener listener) {
        this.listeners.add(listener);
    }
    /*
     * 
     */
    public Collection<InsertedMeeting> produceMeetings() 
    {
    	return this.meetings;
    }
    /*
     * 
     */
    public Collection<InsertedMeeting> produceMeetings(LocalDate firstdayofmonth) 
    {
    	//cash只选择初始一年的新闻，如果超过这个范围需要重新从数据库中选择数据
    	try{
    		if(firstdayofmonth.isAfter(this.cashestartdate) && firstdayofmonth.isBefore(this.casheenddate))
        		return this.meetings;
    	} catch (java.lang.NullPointerException e) { //每月head新闻是一次都找出来的，不设置限制
    		return this.meetings;
    	}
    	
    	//可能用户选择的日期已经超过了cash内存的新闻的最早日期，要从新选择新闻
    	if(firstdayofmonth.isBefore(this.cashestartdate)) {
    		this.cashestartdate = firstdayofmonth.minusMonths(6).with(DayOfWeek.MONDAY);
    	} else if(firstdayofmonth.isAfter(this.casheenddate)) {
    		this.casheenddate = firstdayofmonth.plusMonths(6).with(DayOfWeek.SUNDAY);
    	}
    	this.refreshMeetings (this.nodecode,this.cashestartdate ,this.casheenddate ) ;
        return this.meetings;
    }
    /*
     * 
     */
    public Collection<InsertedMeeting.Label> produceLabels() {
        return this.meetingLabels;
    }

    public void updateMeeting(InsertedMeeting meeting) {
    	datachanged = true;
        this.refreshMeetings(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onMeetingChange(this));
    }

    public void removeMeeting(InsertedMeeting meeting) {
    	datachanged = true;
        this.refreshMeetings(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onMeetingChange(this));
    }

    public void addMeeting(InsertedMeeting meeting) {
    	datachanged = true;
        this.refreshMeetings(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onMeetingChange(this));
    }

    public void updateMeetingLabel(InsertedMeeting.Label label) {
    	datachanged = true;
        this.refreshLabels();
        this.refreshMeetings(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onLabelChange(this));
    }

    public void removeMeetingLabel(InsertedMeeting.Label label) {
    	datachanged = true;
        this.refreshLabels();
        this.refreshMeetings(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onLabelChange(this));
    }

    public void addMeetingLabel(InsertedMeeting.Label label) 
    {
    	datachanged = true;
        this.refreshLabels();
        this.refreshMeetings(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onLabelChange(this));
    }

    private void refreshMeetings(String nodecode,LocalDate startdate,LocalDate enddate) 
    {
        this.meetings.clear();

        try {
            this.meetings.addAll(meetingService.getMeetings(nodecode,startdate,enddate));
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }

    private void refreshLabels() {
        this.meetingLabels.clear();

        try {
            this.meetingLabels.addAll(labelService.getLabels());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
