package com.exchangeinfomanager.labelmanagement;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class DBUrlTagsService implements TagService
{
	private TagsUrlOperation db;
	private Set<String> urlset;

	public DBUrlTagsService (Set<String> urlset)
	{
		this.db = new TagsUrlOperation ();
		this.urlset =  urlset;
	}

	
	public DBUrlTagsService ()
	{
		this.db = new TagsUrlOperation ();
	}
	
	public void setUrlSet (Set<String> urlset)
	{
		this.urlset = urlset;
	}

	@Override
	public Collection<Tag> getLabels() throws SQLException {
		Collection<Tag> l = this.db.getTagsFromURL(urlset);
		return l;
	}

	@Override
	public void createLabel(Tag label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteLabels(Collection<Tag> label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateLabel(Tag label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCache(LabelCache cache) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteLabel(Tag label) throws SQLException {
		// TODO Auto-generated method stub
		
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
