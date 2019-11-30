package com.exchangeinfomanager.Services;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public interface ServicesForNode 
{
	public Collection<BkChanYeLianTreeNode> getNodes (Collection<String> nodenames);
	public BkChanYeLianTreeNode getNode (String nodenames);
	
	public Collection<BkChanYeLianTreeNode> getAllNodes ();
	public BkChanYeLianTreeNode getNodeJiBenMian (BkChanYeLianTreeNode node);
	public Collection<BkChanYeLianTreeNode> getRequiredSubSetOfTheNodes (Set<String> subtypesset);
//	public BkChanYeLianTreeNode getNodeFriends (BkChanYeLianTreeNode node);
//	public BkChanYeLianTreeNode getNodeByNameOrCode (String nameorcode, int nodetype);
	public BkChanYeLianTreeNode getNodeData (BkChanYeLianTreeNode bankuai,LocalDate requiredstartday,LocalDate requiredendday,String period ,Boolean calwholeweek );
	public BkChanYeLianTreeNode getNodeData (String bkcode,LocalDate requiredstartday,LocalDate requiredendday,String period);
	public BkChanYeLianTreeNode getNodeKXian (String bkcode,LocalDate requiredstartday,LocalDate requiredendday,String period);
	public BkChanYeLianTreeNode getNodeKXian (BkChanYeLianTreeNode bk,LocalDate requiredstartday,LocalDate requiredendday,String period);
	public BkChanYeLianTreeNode getNodeZhangDieTingInfo (BkChanYeLianTreeNode bk,LocalDate requiredstartday,LocalDate requiredendday,String period);
	public BkChanYeLianTreeNode getNodeQueKouInfo (String bkcode,LocalDate requiredrecordsday,LocalDate requiredendday,String period);
	public BkChanYeLianTreeNode getNodeQueKouInfo (BkChanYeLianTreeNode bk,LocalDate requiredstartday,LocalDate requiredendday,String period);
	public void syncNodeData (BkChanYeLianTreeNode bk);
}
