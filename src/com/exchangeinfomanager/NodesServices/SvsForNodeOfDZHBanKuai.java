package com.exchangeinfomanager.NodesServices;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.exchangeinfomanager.Services.ServicesForNode;
import com.exchangeinfomanager.database.BanKuaiDZHDbOperation;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class SvsForNodeOfDZHBanKuai implements ServicesForNode 
{
	private BanKuaiDZHDbOperation bkdbopt;
	
	public SvsForNodeOfDZHBanKuai ()
	{
		this.bkdbopt = new BanKuaiDZHDbOperation ();
	}
	@Override
	public Collection<BkChanYeLianTreeNode> getAllNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<BkChanYeLianTreeNode> getNodes(Collection<String> nodenames) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNode(String nodenames) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeJiBenMian(BkChanYeLianTreeNode node) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<BkChanYeLianTreeNode> getSubSetOfTheNodesWithSpecificGntxString(String requiredgntxstring) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<BkChanYeLianTreeNode> getSubSetOfTheNodesWithSpecificFmxxString(String requiredfmxxstring) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<BkChanYeLianTreeNode> getRequiredSubSetOfTheNodes(Set<String> subtypesset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeData(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period, Boolean calwholeweek) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeData(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period, Boolean calwholeweek) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period, Boolean calwholeweek) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(BkChanYeLianTreeNode bk, LocalDate requiredstartday,
			LocalDate requiredendday, String period, Boolean calwholeweek) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeZhangDieTingInfo(BkChanYeLianTreeNode bk, LocalDate requiredstartday,
			LocalDate requiredendday, String period) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeQueKouInfo(String bkcode, LocalDate requiredrecordsday, LocalDate requiredendday,
			String period) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeQueKouInfo(BkChanYeLianTreeNode bk, LocalDate requiredstartday,
			LocalDate requiredendday, String period) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void syncNodeData(BkChanYeLianTreeNode bk) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<BkChanYeLianTreeNode> getNodeChanYeLianInfo(String nodecode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BkChanYeLianTreeNode> getNodeSlidingInChanYeLianInfo(String nodecode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BkChanYeLianTreeNode> getNodeChildrenInChanYeLianInfo(String nodecode) {
		// TODO Auto-generated method stub
		return null;
	}

}
