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
	private String nodecode; //"ALL":�������ţ�"000000":ÿ���̶��¶���ʾ�����š�"999999":��ʾ��jstockcalendar������
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
    	//cashֻѡ���ʼһ������ţ�������������Χ��Ҫ���´����ݿ���ѡ������
    	try{
    		if(firstdayofmonth.isAfter(this.cashestartdate) && firstdayofmonth.isBefore(this.casheenddate))
        		return this.meetings;
    	} catch (java.lang.NullPointerException e) { //ÿ��head������һ�ζ��ҳ����ģ�����������
    		return this.meetings;
    	}
    	
    	//�����û�ѡ��������Ѿ�������cash�ڴ�����ŵ��������ڣ�Ҫ����ѡ������
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
