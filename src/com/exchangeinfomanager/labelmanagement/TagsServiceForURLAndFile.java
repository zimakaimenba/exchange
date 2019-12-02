package com.exchangeinfomanager.labelmanagement;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

import com.exchangeinfomanager.Services.TagService;
import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class TagsServiceForURLAndFile implements TagService
{
	private TagsUrlOperation db;
	private Set<String> urlset;

	public TagsServiceForURLAndFile (Set<String> urlset)
	{
		this.db = new TagsUrlOperation ();
		this.urlset =  urlset;
	}

	
	public TagsServiceForURLAndFile ()
	{
		this.db = new TagsUrlOperation ();
	}
	
	public void setUrlSet (Set<String> urlset)
	{
		this.urlset = urlset;
	}

	@Override
	public Collection<Tag> getTags() throws SQLException {
		Collection<Tag> l = this.db.getTagsFromURL(urlset);
		return l;
	}

	@Override
	public void createTag(Tag label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteTags(Collection<Tag> label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTag(Tag label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCache(TagCache cache) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteTag(Tag label) throws SQLException {
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


	@Override
	public void updateTags(Collection<Tag> label) throws SQLException {
		// TODO Auto-generated method stub
		
	}


}
