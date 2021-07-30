package com.exchangeinfomanager.Core.NodesDataServices;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.Core.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Core.Trees.CreateExchangeTree;
import com.exchangeinfomanager.database.BanKuaiDbOperation;

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
	public BkChanYeLianTreeNode getNodeJiBenMian(BkChanYeLianTreeNode node) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public BkChanYeLianTreeNode getNodeData(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period, Boolean calwholeweek) 
	{
		BanKuai shdpbankuai = 	(BanKuai)allbkstk.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		BanKuai szdpbankuai = 	(BanKuai)allbkstk.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.TDXBK);
		BanKuai cybdpbankuai = 	(BanKuai)allbkstk.getSpecificNodeByHypyOrCode("399006",BkChanYeLianTreeNode.TDXBK);
		
		shdpbankuai = 	(BanKuai) this.servicsofbk.getNodeData(shdpbankuai,requiredstartday,requiredendday,period,calwholeweek);
		szdpbankuai = 	(BanKuai) this.servicsofbk.getNodeData(szdpbankuai,requiredstartday,requiredendday,period,calwholeweek);
		cybdpbankuai = 	(BanKuai) this.servicsofbk.getNodeData(cybdpbankuai,requiredstartday,requiredendday,period,calwholeweek);
		
		return bankuai;
	}

	@Override
	public BkChanYeLianTreeNode getNodeData(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period, Boolean calwholeweek) {
		BkChanYeLianTreeNode result = this.getNodeData( allbkstk.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK),
				requiredstartday, requiredendday, period,calwholeweek);
		return result;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period,Boolean calwholeweek)
	{
		this.getNodeKXian ( (BanKuai) allbkstk.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK),
				requiredstartday, requiredendday, period, calwholeweek);
		
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(BkChanYeLianTreeNode bk, LocalDate requiredstartday,
			LocalDate requiredendday, String period,Boolean calwholeweek) 
	{
		BanKuai shdpbankuai = (BanKuai) allbkstk.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		BanKuai szdpbankuai = (BanKuai) allbkstk.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.TDXBK);
		BanKuai cybdpbankuai = (BanKuai) allbkstk.getSpecificNodeByHypyOrCode("399006",BkChanYeLianTreeNode.TDXBK);
		
		shdpbankuai = (BanKuai) shdpbankuai.getServicesForNode(true).getNodeKXian(shdpbankuai, requiredstartday, requiredendday,NodeGivenPeriodDataItem.DAY,calwholeweek);
		szdpbankuai = (BanKuai) shdpbankuai.getServicesForNode(true).getNodeKXian(szdpbankuai, requiredstartday, requiredendday,NodeGivenPeriodDataItem.DAY,calwholeweek);
		cybdpbankuai = (BanKuai) shdpbankuai.getServicesForNode(true).getNodeKXian(cybdpbankuai, requiredstartday, requiredendday, NodeGivenPeriodDataItem.DAY,calwholeweek);
	
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeZhangDieTingInfo(BkChanYeLianTreeNode bk, LocalDate requiredstartday,
			LocalDate requiredendday, String period) 
	{
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
		shdpbankuai.getServicesForNode(true).syncNodeData(shdpbankuai);
		shdpbankuai.getServicesForNode(false);
		
		BanKuai szdpbankuai = (BanKuai) allbkstk.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.TDXBK);
		szdpbankuai.getServicesForNode(true).syncNodeData(szdpbankuai);
		szdpbankuai.getServicesForNode(false);
		
		BanKuai chuangyeban = (BanKuai) allbkstk.getSpecificNodeByHypyOrCode("399006",BkChanYeLianTreeNode.TDXBK);
		chuangyeban.getServicesForNode(true).syncNodeData(chuangyeban);
		chuangyeban.getServicesForNode(false);
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
	public BkChanYeLianTreeNode getNodeCjeCjlZhanbiExtremeUpDownLevel(BkChanYeLianTreeNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNodeCjeExtremeUpDownZhanbiLevel(BkChanYeLianTreeNode node, Double min, Double max) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNodeCjlExtremeUpDownZhanbiLevel(BkChanYeLianTreeNode node, Double min, Double max) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<BkChanYeLianTreeNode> getNodesWithKeyWords(String keywords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeGzMrMcYkInfo(BkChanYeLianTreeNode node, LocalDate selecteddatestart,
			LocalDate selecteddateend, String period) {
		// TODO Auto-generated method stub
		return null;
	}

}
