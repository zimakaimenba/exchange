package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;



@SuppressWarnings("all")
public class Meeting {

    private LocalDate start;
    private LocalDate end;
    private String title;
    private String description;
    private String keywords; //keywrods
    private String slackUrl;
    protected String newsownercodes;
    private int meetingtype;
    public static int  CHANGQIJILU = 2, NODESNEWS = 3, WKZONGJIE = 4, QIANSHI = 8, RUOSHI = 5, ZHISHUDATE = 7, JINQIGUANZHU = 6;

    private Collection<InsertedMeeting.Label> labels;

    public Meeting(String title, LocalDate start, String description, String keywords, 
    		Collection<InsertedMeeting.Label> labels,String slackUrl,String ownercodes,int meetingtype)
    {
        this.setStart(start);
        this.setTitle(title);
        this.setDescription(description);
        this.setKeyWords(keywords);
        this.labels = new HashSet<>(labels);
        this.setSlackUrl(slackUrl);
        this.setNewsOwnerCodes(ownercodes);
        this.setMeetingType (meetingtype);
    }
    public Meeting(String title, LocalDate start, LocalDate end, String description, String keywords, 
    		Collection<InsertedMeeting.Label> labels,String slackUrl,String ownercodes,int meetingtype)
    {
        this.setStart(start);
        this.setEnd(end);
        this.setTitle(title);
        this.setDescription(description);
        this.setKeyWords(keywords);
        this.labels = new HashSet<>(labels);
        this.setSlackUrl(slackUrl);
        this.setNewsOwnerCodes(ownercodes);
        this.setMeetingType (meetingtype);
    }

    public void setMeetingType(int meetingtype)
    {
		this.meetingtype = meetingtype;
	}
    public int getMeetingType ()
    {
    	return this.meetingtype;
    }

	public Collection<InsertedMeeting.Label> getLabels() {
        return this.labels;
    }

    public LocalDate getStart() {
        return start;
    }
    public LocalDate getEnd (){
    	return end;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }
    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public void setSlackUrl (String url)
    {
    	this.slackUrl = url;
    }
    public String getSlackUrl ()
    {
    	return this.slackUrl;
    }
    public void setNewsOwnerCodes (String ownercodes)
    {
  		this.newsownercodes = ownercodes;
    }
    public String getTitleWithUrl ()
    {
    	if(slackUrl != null && !slackUrl.isEmpty() )	    		
   			return "<a href=\" " +   slackUrl + "\"> " + title + "</a> ";
    	else
    		return this.title;
    }
    public String getNewsOwnerCodes ()
    {
    	return this.newsownercodes;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //keywords
    public String getKeyWords () {
        return keywords;
    }

    public void setKeyWords(String keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return String.format("title: %s, start: %s, end: %s, description: %s, keywords: %s", getTitle(), getStart(),getEnd(),
             getDescription(), getKeyWords());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Meeting meeting = (Meeting) o;

        if (!start.equals(meeting.start))
            return false;
        if (!title.equals(meeting.title))
            return false;
        try{
        	if (!description.equals(meeting.description))
                return false;
        } catch( java.lang.NullPointerException e) {
        	return false;
        }
        
        if (!keywords.equals(meeting.keywords))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
    	int result = 0;
    	
    		
	    		result = start.hashCode();
	            result = 31 * result + title.hashCode();
	            try{
	            	result = 31 * result + description.hashCode();
	            } catch (java.lang.NullPointerException e) {
//	        		e.printStackTrace();
	        		result = 31 * result + "".hashCode();
	        	}
	            try{
	            	result = 31 * result + keywords.hashCode();
	            } catch (java.lang.NullPointerException e) {
//	        		e.printStackTrace();
	        		result = 31 * result + "".hashCode();
	        	}
            
    	
        
        return result;
    }

    public static class Label  {

        private Color color;
        private String name;
        private boolean active;

        public Label(String name, Color color, boolean active) {
            this.color = color;
            this.name = name;
            this.active = active;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return String.format("name: %s, color: %s", getName(), getColor());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Label label = (Label) o;

            if (active != label.active)
                return false;
            if (!color.equals(label.color))
                return false;
            if (!name.equals(label.name))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
        	
            int result = color.hashCode();
            result = 31 * result + name.hashCode();
            result = 31 * result + (active ? 1 : 0);
            return result;
        }
    }
}
