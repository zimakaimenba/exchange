package com.exchangeinfomanager.labelmanagement;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;

public class DBSystemTagsService implements TagService  
{
	private TagsDbOperation dboptforsys;
	private TagsDbOperation dboptfornode;
	private TagsNewsDbOperation dboptfornews;
	private TagCache cache;
	private Set<String> nodeset;

	public DBSystemTagsService ()
	{
		dboptforsys = new TagsDbOperation ();
		dboptfornode = new TagsDbOperation ();
		dboptfornews = new TagsNewsDbOperation ();
	}

	@Override
	public Collection<Tag> getTags() throws SQLException {
		
		Collection<Tag> result = this.dboptforsys.getAllTagsFromDataBase ();
		return result;
	}
	
	@Override
	public void setCache(TagCache cache) {
		this.cache = cache;
	}

	@Override
	public void createTag(Tag label) throws SQLException 
	{
		if( cache.hasBeenInCache (label.getName())   ) 
			return;
			
		Tag m = this.dboptforsys.createSystemTags(label);
		if(m != null && cache != null)
			 cache.addTag(m);
	}

	@Override
	public void deleteTags(Collection<Tag> label) throws SQLException 
	{
		this.dboptforsys.deleteSystemTags (label);
		this.dboptfornode.forcedUnattachedTagFromAllNodes(label);
		this.dboptfornews.forcedUnattachedTagFromAllNews(label);
		cache.removeTags(label);
	}
	@Override
	public void deleteTag(Tag label) throws SQLException 
	{
		this.dboptforsys.deleteSystemTag (label);
		cache.removeTag(label);
	}

	@Override
	public void updateTag(Tag label) throws SQLException
	{
		this.dboptforsys.updateSystemTags (label);
		cache.updateTag (label);
	}
	@Override
	public void updateTags(Collection<Tag> label) throws SQLException 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createTags(Collection<Tag> label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDbOptNodesSet(Set<BkChanYeLianTreeNode> nodesets) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void combinTags(Tag newlabel) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	public   Collection<BkChanYeLianTreeNode> getNodesSetWithAllSpecificTags (String tagname)
	{
		Collection<BkChanYeLianTreeNode> nodesetfortagname = this.dboptforsys.getNodesSetWithAllSpecificTags (tagname);
		 return nodesetfortagname;
	}
	public   Collection<BkChanYeLianTreeNode> getNodesSetWithAllSpecificTags (Collection<Tag> tags)
	{
		Collection<BkChanYeLianTreeNode> nodesetfortagname = this.dboptforsys.getNodesSetWithAllSpecificTags (tags);
		 return nodesetfortagname;
	}
	public   Collection<BkChanYeLianTreeNode> getNodesSetWithOneOfSpecificTags (String tagname)
	{
		Collection<BkChanYeLianTreeNode> nodesetfortagname = this.dboptforsys.getNodesSetWithOneOfSpecificTags (tagname);
		 return nodesetfortagname;
	}
//	public   Collection<BkChanYeLianTreeNode> getNodesSetWithOneOfSpecificTags (Collection<Tag> tags)
//	{
//		Collection<BkChanYeLianTreeNode> nodesetfortagname = this.dboptforsys.getNodesSetWithOneOfSpecificTags (tags);
//		 return nodesetfortagname;
//	}

	public Collection<InsertedMeeting> getNewsSetWithAllSpecificTags(String tagname)
	{
		Collection<InsertedMeeting> newsset = dboptfornews.getNewsSetWithAllSpecificTags (tagname);
		return newsset;
	}




}


