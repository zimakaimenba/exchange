package com.exchangeinfomanager.TagServices;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

import com.exchangeinfomanager.Tag.InsertedTag;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.Tag.TagService;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class TagsServiceForURLAndFile implements TagService
{
	private TagsUrlOperation db;
	private Collection<String> urlset;

	public TagsServiceForURLAndFile (Collection<String> urlset)
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
	public Tag createTag(Tag label) throws SQLException {
		return label;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteTags(Collection<Tag> label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Tag updateTag(Tag label) throws SQLException {
		return label;
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
	public Collection<Tag> createTags(Collection<Tag> label) throws SQLException {
		return label;
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setDbOptNodesSet(Set<BkChanYeLianTreeNode> nodesets) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Tag combinTags(Collection<Tag> oldlabels , Tag newlabel) throws SQLException {
		return newlabel;
		// TODO Auto-generated method stub
		
	}


	@Override
	public Collection<Tag> updateTags(Collection<Tag> label) throws SQLException {
		return label;
		// TODO Auto-generated method stub
		
	}


	@Override
	public Tag combinTags(Tag newlabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


}
