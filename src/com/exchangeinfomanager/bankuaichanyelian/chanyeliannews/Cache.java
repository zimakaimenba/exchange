package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;

import java.rmi.RemoteException;
import java.sql.SQLException;
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
	private String nodecode;
	private boolean datachanged = false;
	private LocalDate cashestartdate;
	private LocalDate casheenddate;

    public Cache(String nodecode,MeetingService meetingService, LabelService labelService) {
    	this.nodecode = nodecode;
        this.meetingService = meetingService;
        this.meetingService.setCache(this);
        this.labelService = labelService;
        this.labelService.setCache(this);
        this.listeners = new HashSet<>();
        this.meetings = new HashSet<>();
        this.meetingLabels = new HashSet<>();
        this.refreshMeetings(nodecode);
        this.refreshLabels();
    }
    public void refreshNews ()
    {
//    	if(datachanged) {
    	if(true) {
    		this.refreshMeetings(nodecode);
        	this.listeners.forEach(l -> l.onMeetingChange(this));
    	}
    }
    public String getNodeCode ()
    {
    	return this.nodecode;
    }

    public void addCacheListener(CacheListener listener) {
        this.listeners.add(listener);
    }

    public Collection<InsertedMeeting> produceMeetings() {
        return this.meetings;
    }

    public Collection<InsertedMeeting.Label> produceLabels() {
        return this.meetingLabels;
    }

    public void updateMeeting(InsertedMeeting meeting) {
    	datachanged = true;
        this.refreshMeetings(nodecode);
        this.listeners.forEach(l -> l.onMeetingChange(this));
    }

    public void removeMeeting(InsertedMeeting meeting) {
    	datachanged = true;
        this.refreshMeetings(nodecode);
        this.listeners.forEach(l -> l.onMeetingChange(this));
    }

    public void addMeeting(InsertedMeeting meeting) {
    	datachanged = true;
        this.refreshMeetings(nodecode);
        this.listeners.forEach(l -> l.onMeetingChange(this));
    }

    public void updateMeetingLabel(InsertedMeeting.Label label) {
    	datachanged = true;
        this.refreshLabels();
        this.refreshMeetings(nodecode);
        this.listeners.forEach(l -> l.onLabelChange(this));
    }

    public void removeMeetingLabel(InsertedMeeting.Label label) {
    	datachanged = true;
        this.refreshLabels();
        this.refreshMeetings(nodecode);
        this.listeners.forEach(l -> l.onLabelChange(this));
    }

    public void addMeetingLabel(InsertedMeeting.Label label) {
    	datachanged = true;
        this.refreshLabels();
        this.refreshMeetings(nodecode);
        this.listeners.forEach(l -> l.onLabelChange(this));
    }

    private void refreshMeetings(String nodecode) {
        this.meetings.clear();

        try {
            this.meetings.addAll(meetingService.getMeetings(nodecode));
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
