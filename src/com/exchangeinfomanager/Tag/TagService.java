package com.exchangeinfomanager.Tag;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.TagServices.TagCache;

public interface TagService 
{
	Collection<Tag> getTags() throws SQLException ;

    Tag createTag(Tag label) throws SQLException ;
    Collection<Tag> createTags(Collection<Tag> label) throws SQLException ;
    
    void deleteTags(Collection<Tag> label) throws SQLException ;
    void deleteTag(Tag label) throws SQLException;

    Tag updateTag(Tag label) throws SQLException ;
    Collection<Tag> updateTags(Collection<Tag> label) throws SQLException ;
    
    void setCache (TagCache cache);

	void setDbOptNodesSet(Set<BkChanYeLianTreeNode> nodesets);

	Tag combinTags(Collection<Tag> oldlabels, Tag newlabel) throws SQLException;
	
	Tag combinTags(Tag newlabel) throws SQLException;

	
   
}
