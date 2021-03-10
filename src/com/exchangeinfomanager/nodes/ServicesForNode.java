package com.exchangeinfomanager.nodes;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ServicesForNode 
{
	public BkChanYeLianTreeNode getNodeJiBenMian (BkChanYeLianTreeNode node);
	public Collection<BkChanYeLianTreeNode> getSubSetOfTheNodesWithSpecificGntxString (String requiredgntxstring);
	public Collection<BkChanYeLianTreeNode> getSubSetOfTheNodesWithSpecificFmxxString (String requiredfmxxstring);
	
	public BkChanYeLianTreeNode getNodeData (BkChanYeLianTreeNode bankuai,LocalDate requiredstartday,LocalDate requiredendday,String period ,Boolean calwholeweek );
	public BkChanYeLianTreeNode getNodeData (String bkcode,LocalDate requiredstartday,LocalDate requiredendday,String period,Boolean calwholeweek);
	public BkChanYeLianTreeNode getNodeKXian (String bkcode,LocalDate requiredstartday,LocalDate requiredendday,String period,Boolean calwholeweek);
	public BkChanYeLianTreeNode getNodeKXian (BkChanYeLianTreeNode bk,LocalDate requiredstartday,LocalDate requiredendday,String period,Boolean calwholeweek);
	
	public BkChanYeLianTreeNode getNodeZhangDieTingInfo (BkChanYeLianTreeNode bk,LocalDate requiredstartday,LocalDate requiredendday,String period);
	public BkChanYeLianTreeNode getNodeQueKouInfo (String bkcode,LocalDate requiredrecordsday,LocalDate requiredendday,String period);
	public BkChanYeLianTreeNode getNodeQueKouInfo (BkChanYeLianTreeNode bk,LocalDate requiredstartday,LocalDate requiredendday,String period);
	public void syncNodeData (BkChanYeLianTreeNode bk);
	
	public BkChanYeLianTreeNode getNodeCjeCjlZhanbiExtremeUpDownLevel (BkChanYeLianTreeNode node);
	public void setNodeCjeExtremeUpDownZhanbiLevel (BkChanYeLianTreeNode node, Double min, Double max);
	public void setNodeCjlExtremeUpDownZhanbiLevel (BkChanYeLianTreeNode node, Double min, Double max);
	
	List<BkChanYeLianTreeNode> getNodeChanYeLianInfo (String nodecode  );
	List<BkChanYeLianTreeNode> getNodeSlidingInChanYeLianInfo (String nodecode );
	List<BkChanYeLianTreeNode> getNodeChildrenInChanYeLianInfo (String nodecode );
	Collection<BkChanYeLianTreeNode> getNodesWithKeyWords(String keywords);
	
	public BkChanYeLianTreeNode getNodeGzMrMcYkInfo (BkChanYeLianTreeNode node,LocalDate selecteddatestart,LocalDate selecteddateend,String period);
}
