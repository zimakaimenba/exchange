package com.exchangeinfomanager.TagServices;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Tag.InsertedTag;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.Tag.TagService;

public class TagCache 
{
    private Set<TagCacheListener> listeners;
    private Set<Tag> tags;
    private TagService labelService;
//	private Set<String> nodecode; 

    public TagCache (TagService labelService) 
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
            this.tags.addAll(labelService.getTags());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.lang.NullPointerException ex) {
        	ex.printStackTrace();
        }
    }
    /*
     * 
     */
    public void addCacheListener(TagCacheListener listener) {
        this.listeners.add(listener);
    }
    /*
     * 
     */
    public Collection<Tag> produceTags() {
    	
        return this.tags;
    }
    public Collection<Tag> produceSelectedTags() {
    	Collection<Tag> tagslist = this.produceTags ();
    	Collection<Tag> selectedtag = new HashSet<> ();
    	
    	for(Tag tmptag : tagslist) 
	    	if(  tmptag.isSelected() )
	    		selectedtag.add(tmptag);
	    	
	    return selectedtag;
    }
    public void unSelectAllTags ()
    {
    	Collection<Tag> tagslist = this.produceTags ();
    	for(Tag tmptag : tagslist) 
	    	if(  tmptag.isSelected() )
	    		tmptag.setSelected(false);
    	
    	this.listeners.forEach(l -> l.onTagChange(this));
    }
    
    
    public void addTags(Collection<Tag> label) 
	  {
	      this.refreshTags();
	      this.listeners.forEach(l -> l.onTagChange(this));
	  }
	  public void addTag(Tag label) 
	  {
	      this.refreshTags();
	      this.listeners.forEach(l -> l.onTagChange(this));
	  }
	  public void addLabels(Collection<Tag> label)
	  {
		  this.refreshTags();
	      this.listeners.forEach(l -> l.onTagChange(this));
	  }
	    
    
	  public InsertedTag hasBeenInCache (String tagname)
	  {
			Collection<Tag> curlbs = this.produceTags();
			for ( Iterator<Tag> it = curlbs.iterator(); it.hasNext(); ) {
		        Tag f = it.next();
		        if (f.getName().equals(tagname ))
		            return (InsertedTag) f;
		    }

			return null;
	  }
	  public void removeTags(Collection<Tag> label) 
	  {
	        this.refreshTags();
	        this.listeners.forEach(l -> l.onTagChange(this));
	  }
	  public void removeTag (Tag label) 
	  {
	        this.refreshTags();
	        this.listeners.forEach(l -> l.onTagChange(this));
	  }

    public void updateTag(Tag label) 
    {
    	this.refreshTags();
    	this.listeners.forEach(l -> l.onTagChange(this));
    }
    public void updateTags (Collection<Tag> label) 
    {
    	this.refreshTags();
    	this.listeners.forEach(l -> l.onTagChange(this));
    }
    public void remindListenersToRefresh ()
    {
    	this.refreshTags();
    	this.listeners.forEach(l -> l.onTagChange(this));
    }

}
