package com.exchangeinfomanager.TagServices;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.exchangeinfomanager.Services.TagService;
import com.exchangeinfomanager.Tag.InsertedTag;
import com.exchangeinfomanager.Tag.NodeInsertedTag;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class TagsServiceForNodes implements TagService  
{
	private TagsDbOperation dboptfornode;
	private TagsNewsDbOperation dbfornews;
//	private TagsDbOperation dbforsys;
	private TagsServiceForSystemTags tagsvssys; 
	
	private TagCache cache;
	private Collection<BkChanYeLianTreeNode> nodesets;
	

	public TagsServiceForNodes (Collection<BkChanYeLianTreeNode> nodeset)
	{
		tagsvssys =  new TagsServiceForSystemTags ();
		CacheForInsertedTag allsyskwcache = new CacheForInsertedTag (tagsvssys);
		tagsvssys.setCache(allsyskwcache);
		
		dboptfornode = new TagsDbOperation (); 
		dbfornews = new TagsNewsDbOperation ();
		
		this.nodesets = nodeset;
	}
	public TagsServiceForNodes (BkChanYeLianTreeNode nodeset)
	{
		tagsvssys =  new TagsServiceForSystemTags ();
		
		dboptfornode = new TagsDbOperation (); 
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
		this.cache.refreshTags();
	}
	@Override
	public Collection<Tag> getTags() throws SQLException 
	{
		Collection<Tag> result = this.dboptfornode.getNodesTagsFromDataBase ( nodesets);
		return result;
	}
	@Override
	public Tag createTag(Tag label) throws SQLException 
	{
		//可以在这里检查是否已经有同样的label
		if(cache != null) {
			Collection<Tag> curlabl = cache.produceTags();
			if(curlabl.contains(label))
				return label;
		}
		
		Tag tag = this.tagsvssys.createTag(label);
		
		this.dboptfornode.attachedTagToNodes ( nodesets, tag);
		
		if(tag != null && cache != null)
   		 	cache.addTag(tag);
		
		return tag;
	}
	public Collection<Tag> createTags(Collection<Tag> label) throws SQLException 
	{
		//可以在这里检查是否已经有同样的label
		Collection<Tag> curlabl = cache.produceTags();
		for (Iterator<Tag> lit = label.iterator(); lit.hasNext(); ) {
	        Tag f = lit.next();

	        Tag m;
			if( !curlabl.contains(f) ) 
	        	m = this.createTag (f);
	    }
		
		if(label != null && cache != null) {
			cache.addTags(label);
		}
		
		return label;
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
	public Tag updateTag(Tag label) throws SQLException 
	{
		int labelid = ((InsertedTag)label).getID();
		
		Collection<Tag> curlabl = cache.produceTags();
		
		for (Iterator<Tag> it = curlabl.iterator(); it.hasNext(); ) { //必须从自己CACHE里面找，因为有可能并不是自己cache里面的tag,颜色不一样
	        Tag f = it.next();
	        int myownkwid = ((InsertedTag)f).getID();
	        if(myownkwid == labelid) { 
	        	
	        	this.tagsvssys.updateTag (label);
	        	
	        	if(f instanceof NodeInsertedTag) { //只改自己的颜色
	        		((NodeInsertedTag)f).setNodeMachColor( label.getColor()  );
	        		f.setName(label.getName());
		        	this.dboptfornode.updateTagForNodesInDataBase (f);
	        	}

	        	break;
	        }
		}
		
		cache.updateTag (label);
		
		return label;
	}
	@Override
	public Tag combinTags (Collection<Tag> oldlabels, Tag newlabel) throws SQLException
	{
//		Collection<Tag> curlabls = cache.produceSelectedTags();
		
		Tag tag = tagsvssys.combinTags(oldlabels, newlabel);
		
		cache.addTag(newlabel);
		
		return tag;
	}
	@Override
	public Collection<Tag> updateTags(Collection<Tag> label) throws SQLException {
		return label;
		// TODO Auto-generated method stub
		
	}
	@Override
	public Tag combinTags(Tag newlabel) throws SQLException 
	{
		Collection<Tag> curlabls = cache.produceSelectedTags();
		
		Tag t = this.combinTags(curlabls, newlabel);
		
		return t;
	}
	
	
}


 
