package com.exchangeinfomanager.labelmanagement;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CacheListener;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.EventService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelService;

public class LabelCache 
{
    private Set<CacheListener> listeners;
    private Set<Tag> tags;
    private TagService labelService;
	private Set<String> nodecode; 
	private boolean datachanged = false;
	private LocalDate cashestartdate;
	private LocalDate casheenddate;
	private Integer[] eventtype;

    public LabelCache(Set<String> nodecode, TagService labelService) 
    {
    	this.nodecode = nodecode;
        this.labelService = labelService;
       	this.labelService.setCache(this);
        this.listeners = new HashSet<>();
        this.tags = new HashSet<>();
        
        this.refreshTags(nodecode);
    }

    private void refreshTags(Set<String> nodecode) {
        this.tags.clear();

        try {
            this.tags.addAll(labelService.getLabels(nodecode));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.lang.NullPointerException ex) {
        	ex.printStackTrace();
        }
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
    public Collection<Tag> produceLabels() {
        return this.tags;
    }

    public void updateMeeting(InsertedMeeting meeting) {
    	datachanged = true;
        this.refreshMeetings(nodecode,this.cashestartdate,this.casheenddate,this.eventtype);
        this.listeners.forEach(l -> l.onMeetingChange(this));
    }

    public void removeMeeting(InsertedMeeting meeting) {
    	datachanged = true;
        this.refreshMeetings(nodecode,this.cashestartdate,this.casheenddate,this.eventtype);
        this.listeners.forEach(l -> l.onMeetingChange(this));
    }

    public void addMeeting(InsertedMeeting meeting) {
    	datachanged = true;
        this.refreshMeetings(nodecode,this.cashestartdate,this.casheenddate,this.eventtype);
        this.listeners.forEach(l -> l.onMeetingChange(this));
    }

    public void updateMeetingLabel(InsertedMeeting.Label label) {
    	datachanged = true;
        this.refreshLabels();
        this.refreshMeetings(nodecode,this.cashestartdate,this.casheenddate,this.eventtype);
        this.listeners.forEach(l -> l.onLabelChange(this));
    }

    public void removeMeetingLabel(InsertedMeeting.Label label) {
    	datachanged = true;
        this.refreshLabels();
        this.refreshMeetings(nodecode,this.cashestartdate,this.casheenddate,this.eventtype);
        this.listeners.forEach(l -> l.onLabelChange(this));
    }

    public void addMeetingLabel(InsertedMeeting.Label label) 
    {
    	datachanged = true;
        this.refreshLabels();
        this.refreshMeetings(nodecode,this.cashestartdate,this.casheenddate,this.eventtype);
        this.listeners.forEach(l -> l.onLabelChange(this));
    }

}
