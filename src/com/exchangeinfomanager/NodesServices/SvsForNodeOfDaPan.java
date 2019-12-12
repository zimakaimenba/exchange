package com.exchangeinfomanager.NodesServices;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.exchangeinfomanager.Services.ServicesForNode;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

public class SvsForNodeOfDaPan implements ServicesForNode
{

	private BanKuaiAndStockTree allbkstk;
	private BanKuaiDbOperation bkdbopt;
	private SvsForNodeOfBanKuai servicsofbk;

	public SvsForNodeOfDaPan ()
	{
		this.bkdbopt = new BanKuaiDbOperation ();
		allbkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		
		servicsofbk = new SvsForNodeOfBanKuai  ();
	}

	@Override
	public Collection<BkChanYeLianTreeNode> getNodes(Collection<String> nodenames) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNode(String nodenames) {
		return allbkstk.getSpecificNodeByHypyOrCode(nodenames, BkChanYeLianTreeNode.DAPAN);
	}

	@Override
	public Collection<BkChanYeLianTreeNode> getAllNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeJiBenMian(BkChanYeLianTreeNode node) {
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
			LocalDate requiredendday, String period, Boolean calwholeweek) 
	{
		BanKuai shdpbankuai = (BanKuai) allbkstk.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		BanKuai szdpbankuai = (BanKuai) allbkstk.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.TDXBK);
		BanKuai cybdpbankuai = (BanKuai) allbkstk.getSpecificNodeByHypyOrCode("399006",BkChanYeLianTreeNode.TDXBK);
		
		shdpbankuai = (BanKuai) this.servicsofbk.getNodeData(shdpbankuai,requiredstartday,requiredendday,period,calwholeweek);
		szdpbankuai = (BanKuai) this.servicsofbk.getNodeData (szdpbankuai,requiredstartday,requiredendday,period,calwholeweek);
		cybdpbankuai = (BanKuai) this.servicsofbk.getNodeData (cybdpbankuai,requiredstartday,requiredendday,period,calwholeweek);
		
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeData(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period)
	{
		BanKuai shdpbankuai = (BanKuai) allbkstk.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		BanKuai szdpbankuai = (BanKuai) allbkstk.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.TDXBK);
		BanKuai cybdpbankuai = (BanKuai) allbkstk.getSpecificNodeByHypyOrCode("399006",BkChanYeLianTreeNode.TDXBK);
		
		shdpbankuai = (BanKuai) this.servicsofbk.getNodeKXian(shdpbankuai, requiredstartday, requiredendday,period);
		szdpbankuai = (BanKuai) this.servicsofbk.getNodeKXian(szdpbankuai, requiredstartday, requiredendday,period);
		cybdpbankuai = (BanKuai) this.servicsofbk.getNodeKXian(cybdpbankuai, requiredstartday, requiredendday, period);
		
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(BkChanYeLianTreeNode bk, LocalDate requiredstartday,
			LocalDate requiredendday, String period) {
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
	public void syncNodeData(BkChanYeLianTreeNode bk) 
	{
		BanKuai shdpbankuai = (BanKuai) allbkstk.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		LocalDate bkstartday = shdpbankuai.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsStartDate();
		LocalDate bkendday = shdpbankuai.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsEndDate();
		
		this.getNodeKXian ("",bkstartday,bkendday,NodeGivenPeriodDataItem.DAY);
		
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
