package com.exchangeinfomanager.labelmanagement;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.NodeInsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class DBNodesTagsService implements TagService  
{
	private TagsDbOperation dboptfornode;
	private TagsNewsDbOperation dbfornews;
	private TagsDbOperation dbforsys;
	
	private TagCache cache;
	private Collection<BkChanYeLianTreeNode> nodesets;
	

	public DBNodesTagsService (Collection<BkChanYeLianTreeNode> nodeset)
	{
		dboptfornode = new TagsDbOperation (); 
		dbforsys = new TagsDbOperation ();
		dbfornews = new TagsNewsDbOperation ();
		this.nodesets = nodeset;
	}
	public DBNodesTagsService (BkChanYeLianTreeNode nodeset)
	{
		dboptfornode = new TagsDbOperation (); 
		dbforsys = new TagsDbOperation ();
		dbfornews = new TagsNewsDbOperation ();
		
		this.nodesets = new HashSet<> ();
		this.nodesets.add( nodeset);
	}
	@Override
	public void setCache(TagCache cache) 
	{
		this.cache = cache;
		
	}
	@Override
	public void setDbOptNodesSet (Set<BkChanYeLianTreeNode> nodesets)
	{
		this.nodesets = nodesets;
		this.cache.clearAllTags();
	}
	@Override
	public Collection<Tag> getTags() throws SQLException 
	{
		Collection<Tag> result = this.dboptfornode.getNodesTagsFromDataBase ( nodesets);
		return result;
	}
	@Override
	public void createTag(Tag label) throws SQLException 
	{
		//可以在这里检查是否已经有同样的label
		if(cache != null) {
			Collection<Tag> curlabl = cache.produceTags();
			for (Iterator<Tag> it = curlabl.iterator(); it.hasNext(); ) {
		        Tag f = it.next();
		        if (f.getName().equals( label.getName()  ))
		            return;
		    }
		}
		
		Tag tag = this.dbforsys.createSystemTags(label);
		this.dboptfornode.attachedTagToNodes ( nodesets, tag);
		if(tag != null && cache != null)
   		 	cache.addTag(tag);
	}
	public void createTags(Collection<Tag> label) throws SQLException 
	{
		//可以在这里检查是否已经有同样的label
		Collection<Tag> curlabl = cache.produceTags();
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
	        	this.createTag (f);
	    }
		
		if(label != null && cache != null)
   		 	cache.addTags(label);
	}
	@Override
	public void deleteTag(Tag label) throws SQLException 
	{
//		this.db.unattachedTagFromNode ( nodesets, label);
	}
	@Override
	public void deleteTags(Collection<Tag> label) throws SQLException 
	{
		this.dboptfornode.unattachedTagsFromNodes ( nodesets, label);
	 	cache.removeTags(label);
	}
	@Override
	public void updateTag(Tag label) throws SQLException 
	{
		int labelid = ((InsertedTag)label).getID();
		
		Collection<Tag> curlabl = cache.produceTags();
		for (Iterator<Tag> it = curlabl.iterator(); it.hasNext(); ) { //必须从自己CACHE里面找，因为有可能并不是自己cache里面的tag,颜色不一样
	        Tag f = it.next();
	        int myownkwid = ((InsertedTag)f).getID();
	        if(myownkwid == labelid) { 
	        	
	        	this.dbforsys.updateSystemTags (label);
	        	
	        	if(f instanceof NodeInsertedTag) { //只改自己的颜色
	        		((NodeInsertedTag)f).setNodeMachColor( label.getColor()  );
	        		f.setName(label.getName());
		        	this.dboptfornode.updateTagForNodesInDataBase (f);
	        	}

	        	break;
	        }
		}
		
		cache.updateTag (label);
	}
	@Override
	public void combinTags (Tag newlabel) throws SQLException
	{
		Collection<Tag> curlabls = cache.produceSelectedTags();
		
		InsertedTag newtag = this.dbforsys.combinLabelsToNewOneForSystem (curlabls, newlabel);
		
		this.dboptfornode.combinLabelsToNewOneForNodes(curlabls,newtag);
		
		this.dbfornews.combinLabelsToNewOneForNews (curlabls, newtag);
		
		cache.addTag(newlabel);
	}
	@Override
	public void updateTags(Collection<Tag> label) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	
}


 
