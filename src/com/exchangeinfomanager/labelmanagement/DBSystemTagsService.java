package com.exchangeinfomanager.labelmanagement;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelService;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting.Label;
import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;

public class DBSystemTagsService implements TagService  
{
	private TagsDbOperation dboptforsys;
	private TagsDbOperation dboptfornode;
	private TagsNewsDbOperation dboptfornews;
	private LabelCache cache;
	private Set<String> nodeset;

	public DBSystemTagsService (Set<String> nodecode)
	{
		dboptforsys = new TagsDbOperation ();
		dboptfornode = new TagsDbOperation ();
		dboptfornews = new TagsNewsDbOperation ();
		
		this.nodeset = nodecode;
	}

	@Override
	public Collection<Tag> getLabels() throws SQLException {
		
		Collection<Tag> result = this.dboptforsys.getAllTagsFromDataBase ();
		return result;
	}
	
	@Override
	public void setCache(LabelCache cache) {
		this.cache = cache;
	}

	@Override
	public void createLabel(Tag label) throws SQLException 
	{
		if( cache.hasBeenInCache (label.getName())   ) 
			return;
			
		Tag m = this.dboptforsys.createSystemTags(label);
		if(m != null && cache != null)
			 cache.addLabel(m);
	}

	@Override
	public void deleteLabels(Collection<Tag> label) throws SQLException 
	{
		this.dboptforsys.deleteSystemTags (label);
		this.dboptfornode.forcedUnattachedTagFromAllNodes(label);
		this.dboptfornews.forcedUnattachedTagFromAllNews(label);
		cache.removeTags(label);
	}
	@Override
	public void deleteLabel(Tag label) throws SQLException 
	{
		this.dboptforsys.deleteSystemTag (label);
		cache.removeTags(label);
	}

	@Override
	public void updateLabel(Tag label) throws SQLException
	{
		this.dboptforsys.updateSystemTags (label);
		cache.updateMeetingLabel (label);
		
	}

	@Override
	public void createLabels(Collection<Tag> label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDbOptNodesSet(Set<BkChanYeLianTreeNode> nodesets) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void combinLabels(Tag newlabel) throws SQLException {
		// TODO Auto-generated method stub
		
	}


}


