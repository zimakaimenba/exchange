package com.exchangeinfomanager.TagServices;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.exchangeinfomanager.Services.TagService;
import com.exchangeinfomanager.Tag.InsertedTag;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class CacheForInsertedTag extends TagCache
{
	public CacheForInsertedTag (TagService labelService)
	{
		super(labelService);
	}
	
	  public Set<BkChanYeLianTreeNode> getTagOwners (String tagname) 
	  {
		  if( ! this.hasBeenInCache(tagname) )
			  return null;
		  
		  Set<BkChanYeLianTreeNode> nodeset = new HashSet<> ();
		  Collection<Tag> curlbs = this.produceTags();
		  for ( Iterator<Tag> it = curlbs.iterator(); it.hasNext(); ) {
		        Tag f = it.next();
		        if (f.getName().equals(tagname )) {
		        	return ((InsertedTag)f).getOwners();
		        }
		  }
		  nodeset = null;
		  return null;
	  }
}
