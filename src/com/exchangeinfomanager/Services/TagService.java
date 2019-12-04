package com.exchangeinfomanager.Services;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

import com.exchangeinfomanager.Tag.InsertedTag;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagCache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

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
