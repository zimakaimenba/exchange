package com.exchangeinfomanager.labelmanagement;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class DBNewsTagsService implements TagService
{
	private TagsNewsDbOperation dboptfornews;
	private TagsDbOperation dboptforsys;
	
	private Set<BkChanYeLianTreeNode> nodeset;
	private TagCache cache;
	

	public DBNewsTagsService (Set<BkChanYeLianTreeNode> nodeset)
	{
		this.dboptfornews = new TagsNewsDbOperation ();
		this.nodeset = nodeset;
	}
	

	@Override
	public Collection<Tag> getTags() throws SQLException 
	{
		Collection<Tag> result = this.dboptfornews.getNodesNewsTagsFromDataBase(nodeset);
		return result;
	}

	@Override
	public void createTag(Tag label) throws SQLException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteTags(Collection<Tag> label) throws SQLException 
	{
		//可以在这里检查是否已经有同样的label
		Collection<Tag> curlabl = cache.produceTags();
		for (Iterator<Tag> lit = label.iterator(); lit.hasNext(); )  {
	        Tag f = lit.next();
	        
	        Boolean incache = false;
	        for (Iterator<Tag> it = curlabl.iterator(); it.hasNext(); ) {
		        Tag l = it.next();
		        if (f.getName().equals( l.getName()  )) {
		        	incache = true;
		        	break;
		        }
		    }
	        
	        if(incache)
	        	this.deleteTag (f); 
	        	
	    }
		
		cache.removeTags(label);
	}
	@Override
	public void deleteTag(Tag label) throws SQLException 
	{
		this.dboptfornews.removeTagFromNodesNewsInDataBase(nodeset, label);
	}
	@Override
	public void updateTag(Tag label) throws SQLException 
	{
		cache.updateTag(label);
		
	}

	@Override
	public void setCache(TagCache cache) 
	{
		this.cache = cache;
		
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
