package com.exchangeinfomanager.TagServices;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.Services.TagService;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class TagServicesForNews implements TagService 
{
	protected TagsNewsDbOperation tagsdboptfornews;
	private News news;

	public TagServicesForNews (News news) 
	{
		this.news = news;
		this.tagsdboptfornews = new TagsNewsDbOperation ();
		
	}
	@Override
	public Collection<Tag> getTags() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tag createTag(Tag label) throws SQLException 
	{
		 this.tagsdboptfornews.storeNewsKeyWordsToDataBase (this.news);
		return null;
	}

	@Override
	public Collection<Tag> createTags(Collection<Tag> label) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteTags(Collection<Tag> label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteTag(Tag label) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Tag updateTag(Tag label) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Tag> updateTags(Collection<Tag> label) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCache(TagCache cache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDbOptNodesSet(Set<BkChanYeLianTreeNode> nodesets) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Tag combinTags(Collection<Tag> oldlabels, Tag newlabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tag combinTags(Tag newlabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
