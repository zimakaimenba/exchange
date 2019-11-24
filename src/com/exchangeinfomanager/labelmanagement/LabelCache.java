package com.exchangeinfomanager.labelmanagement;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CacheListener;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.EventService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelService;
import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;

public class LabelCache 
{
    private Set<LabelCacheListener> listeners;
    private Set<Tag> tags;
    private TagService labelService;
//	private Set<String> nodecode; 

    public LabelCache(TagService labelService) 
    {
        this.labelService = labelService;
       	this.labelService.setCache(this);
        this.listeners = new HashSet<>();
        this.tags = new HashSet<>();
        
        this.refreshTags();
    }

    public void clearAllTags ()
    {
    	this.tags.clear();
    }
    public void refreshTags() {
        this.tags.clear();

        try {
            this.tags.addAll(labelService.getLabels());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.lang.NullPointerException ex) {
        	ex.printStackTrace();
        }
    }
    /*
     * 
     */
    public void addCacheListener(LabelCacheListener listener) {
        this.listeners.add(listener);
    }
    /*
     * 
     */
    public Collection<Tag> produceLabels() {
        return this.tags;
    }
    public Collection<Tag> produceSelectedLabels() {
    	Collection<Tag> tagslist = this.produceLabels ();
    	Collection<Tag> selectedtag = new HashSet<> ();
    	
    	for(Tag tmptag : tagslist) 
	    	if(  ((InsertedTag)tmptag).isSelected() )
	    		selectedtag.add(tmptag);
	    	
	    return selectedtag;
    }
    
	  public void addLabel(Tag label) 
	  {
	      this.refreshTags();
	      this.listeners.forEach(l -> l.onLabelChange(this));
	  }
	  public void addLabels(Collection<Tag> label)
	  {
		  this.refreshTags();
	      this.listeners.forEach(l -> l.onLabelChange(this));
	  }
	    
    
	  public Boolean hasBeenInCache (String tagname)
	  {
			Collection<Tag> curlbs = this.produceLabels();
			for ( Iterator<Tag> it = curlbs.iterator(); it.hasNext(); ) {
		        Tag f = it.next();
		        if (f.getName().equals(tagname ))
		            return true;
		    }

			return false;
	  }
	  public void removeTags(Collection<Tag> label) 
	  {
	        this.refreshTags();
	        this.listeners.forEach(l -> l.onLabelChange(this));
	  }
	  public void removeTags(Tag label) 
	  {
	        this.refreshTags();
	        this.listeners.forEach(l -> l.onLabelChange(this));
	  }

    public void updateMeetingLabel(Tag label) 
    {
    	this.refreshTags();
    	this.listeners.forEach(l -> l.onLabelChange(this));
    }
    public void updateMeetingLabel(Collection<Tag> label) 
    {
    	this.refreshTags();
    	this.listeners.forEach(l -> l.onLabelChange(this));
    }

}
