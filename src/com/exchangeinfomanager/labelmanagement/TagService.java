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
	Collection<Tag> getLabels() throws SQLException ;

    void createLabel(Tag label) throws SQLException ;
    void createLabels(Collection<Tag> label) throws SQLException ;
    
    void deleteLabels(Collection<Tag> label) throws SQLException ;
    void deleteLabel(Tag label) throws SQLException;

    void updateLabel(Tag label) throws SQLException ;
    
    void setCache (LabelCache cache);

	void setDbOptNodesSet(Set<BkChanYeLianTreeNode> nodesets);

	void combinLabels(Tag newlabel) throws SQLException;
   
}
