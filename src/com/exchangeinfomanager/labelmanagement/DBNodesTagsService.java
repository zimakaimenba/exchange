package com.exchangeinfomanager.labelmanagement;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class DBNodesTagsService implements TagService  
{
	private TagsDbOperation dboptfornode;
	private TagsNewsDbOperation dbfornews;
	private TagsDbOperation dbforsys;
	
	private LabelCache cache;
	private Set<BkChanYeLianTreeNode> nodesets;
	

	public DBNodesTagsService (Set<BkChanYeLianTreeNode> nodeset)
	{
		dboptfornode = new TagsDbOperation (); 
		 dbforsys = new TagsDbOperation ();
		 dbfornews = new TagsNewsDbOperation ();
		 this.nodesets = nodeset;
	}
	@Override
	public void setCache(LabelCache cache) 
	{
		this.cache = cache;
		
	}
	@Override
	public void setDbOptNodesSet (Set<BkChanYeLianTreeNode> nodesets)
	{
		this.nodesets = nodesets;
	}
	@Override
	public Collection<Tag> getLabels() throws SQLException 
	{
		Collection<Tag> result = this.dboptfornode.getNodesTagsFromDataBase ( nodesets);
		return result;
	}
	@Override
	public void createLabel(Tag label) throws SQLException 
	{
		//可以在这里检查是否已经有同样的label
		Collection<Tag> curlabl = cache.produceLabels();
		for (Iterator<Tag> it = curlabl.iterator(); it.hasNext(); ) {
	        Tag f = it.next();
	        if (f.getName().equals( label.getName()  ))
	            return;
	    }
		
		Tag tag = this.dbforsys.createSystemTags(label);
		this.dboptfornode.attachedTagToNodes ( nodesets, tag);
		if(tag != null && cache != null)
   		 	cache.addLabel(tag);
	}
	public void createLabels(Collection<Tag> label) throws SQLException 
	{
		//可以在这里检查是否已经有同样的label
		Collection<Tag> curlabl = cache.produceLabels();
		for (Iterator<Tag> lit = label.iterator(); lit.hasNext(); ) {
	        Tag f = lit.next();
	        
	        Boolean incache = false;
	        for (Iterator<Tag> it = curlabl.iterator(); it.hasNext(); ) {
		        Tag l = it.next();
		        if (f.getName().equals( l.getName()  )) {
		        	incache = true;
		        	break;
		        }
		    }
	        
	        if(!incache)
	        	this.createLabel (f);
	    }
		
		if(label != null && cache != null)
   		 	cache.addLabels(label);
	}
	@Override
	public void deleteLabel(Tag label) throws SQLException 
	{
//		this.db.unattachedTagFromNode ( nodesets, label);
	}
	@Override
	public void deleteLabels(Collection<Tag> label) throws SQLException 
	{
		this.dboptfornode.unattachedTagsFromNodes ( nodesets, label);
	 	cache.removeTags(label);
	}

	@Override
	public void updateLabel(Tag label) throws SQLException 
	{
		Collection<Tag> curlabl = cache.produceLabels();
		for (Iterator<Tag> it = curlabl.iterator(); it.hasNext(); ) {
	        Tag f = it.next();
	        if (f.getName().equals( label.getName()  )) {
	    		this.dboptfornode.updateTagForNodesInDataBase (f);
	    		cache.updateMeetingLabel (f);
	        }
	    }
	}
	@Override
	public void combinLabels (Tag newlabel) throws SQLException
	{
		Collection<Tag> curlabls = cache.produceSelectedLabels();
		
		InsertedTag newtag = this.dbforsys.combinLabelsToNewOneForSystem (curlabls, newlabel);
		
		this.dboptfornode.combinLabelsToNewOneForNodes(curlabls,newtag);
		
		this.dbfornews.combinLabelsToNewOneForNews (curlabls, newtag);
		
		cache.addLabel(newlabel);
	}
	
	
}


 
