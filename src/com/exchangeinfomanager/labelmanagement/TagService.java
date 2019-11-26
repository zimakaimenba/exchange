package com.exchangeinfomanager.labelmanagement;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public interface TagService 
{
	Collection<Tag> getTags() throws SQLException ;

    void createTag(Tag label) throws SQLException ;
    void createTags(Collection<Tag> label) throws SQLException ;
    
    void deleteTags(Collection<Tag> label) throws SQLException ;
    void deleteTag(Tag label) throws SQLException;

    void updateTag(Tag label) throws SQLException ;
    
    void setCache (TagCache cache);

	void setDbOptNodesSet(Set<BkChanYeLianTreeNode> nodesets);

	void combinTags(Tag newlabel) throws SQLException;
   
}
