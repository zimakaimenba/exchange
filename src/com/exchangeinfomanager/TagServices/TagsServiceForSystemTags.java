package com.exchangeinfomanager.TagServices;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.Services.TagService;
import com.exchangeinfomanager.Tag.InsertedTag;
import com.exchangeinfomanager.Tag.Tag;


public class TagsServiceForSystemTags implements TagService  
{
	private TagsDbOperation dboptforsys;
	private TagsDbOperation dboptfornode;
	private TagsNewsDbOperation dboptfornews;
	private TagCache cache;
	private Set<String> nodeset;
	

	public TagsServiceForSystemTags ()
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
	public  Tag createTag(Tag label) throws SQLException 
	{
		if( cache != null)    {
			 InsertedTag checkresult = cache.hasBeenInCache (label.getName() );
			 if(checkresult != null)
				 return checkresult;
		}
			
		Tag m = this.dboptforsys.createSystemTags(label);
		if(m != null && cache != null)
			 cache.addTag(m);
		
		return m;
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
	public Tag updateTag(Tag label) throws SQLException
	{
		this.dboptforsys.updateSystemTags (label);
		
		if(cache != null)
			cache.updateTag (label);
		
		return label;
	}
	@Override
	public Collection<Tag> updateTags(Collection<Tag> label) throws SQLException 
	{
		return label;
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<Tag> createTags(Collection<Tag> label) throws SQLException 
	{
		return null;
	}

	@Override
	public void setDbOptNodesSet(Set<BkChanYeLianTreeNode> nodesets) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Tag combinTags(Collection<Tag> oldlabels, Tag newlabel) throws SQLException 
	{
		InsertedTag newtag = this.dboptforsys.combinLabelsToNewOneForSystem (oldlabels, newlabel);
		
		this.dboptfornode.combinLabelsToNewOneForNodes(oldlabels,newtag);
		
		this.dboptfornews.combinLabelsToNewOneForNews (oldlabels, newtag);
		
		cache.addTag(newlabel);
		
		return newtag;
	}
	@Override
	public Tag combinTags(Tag newlabel) throws SQLException
	{
		Collection<Tag> curlabls = cache.produceSelectedTags();
		Tag m = this.combinTags(curlabls, newlabel);
		
		return m;
	}
	/*
	 * 必须包含所有这些TAG	
	 */
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
	public Collection<News> getNewsSetWithAllSpecificTags(String tagname)
	{
		Collection<News> newsset = dboptfornews.getNewsSetWithAllSpecificTags (tagname);
		return newsset;
	}
}


