package com.exchangeinfomanager.NodesServices;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;

public class SvsForNodeOfDZHDaPan implements ServicesForNode
{
	private SvsForNodeOfDZHBanKuai servicsofbk;

	public SvsForNodeOfDZHDaPan ()
	{
		servicsofbk = new SvsForNodeOfDZHBanKuai  ();
	}

//	@Override
//	public Collection<BkChanYeLianTreeNode> getNodes(Collection<String> nodenames) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public BkChanYeLianTreeNode getNode(String nodenames) {
//		return allbkstk.getSpecificNodeByHypyOrCode(nodenames, BkChanYeLianTreeNode.DAPAN);
//	}
//
//	@Override
//	public Collection<BkChanYeLianTreeNode> getAllNodes() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public BkChanYeLianTreeNode getNodeJiBenMian(BkChanYeLianTreeNode node) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public Collection<BkChanYeLianTreeNode> getRequiredSubSetOfTheNodes(Set<String> subtypesset) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public BkChanYeLianTreeNode getNodeData(BkChanYeLianTreeNode dapan, LocalDate requiredstartday,
			LocalDate requiredendday, String period, Boolean calwholeweek) 
	{
//		BanKuai shdpbankuai = 	(BanKuai)CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.DZHBK);
//		BanKuai szdpbankuai = 	(BanKuai)CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.DZHBK);
//		BanKuai cybdpbankuai = 	(BanKuai)CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode("399006",BkChanYeLianTreeNode.DZHBK);
		
		BanKuai shdpbankuai = ((DaPan)dapan).getDaPanShangHai ();
		BanKuai szdpbankuai = ((DaPan)dapan).getDaPanShenZhen ();
		shdpbankuai = 	(BanKuai) this.servicsofbk.getNodeData(shdpbankuai,requiredstartday,requiredendday,period,calwholeweek);
		szdpbankuai = 	(BanKuai) this.servicsofbk.getNodeData(szdpbankuai,requiredstartday,requiredendday,period,calwholeweek);
		
		return dapan;
	}

	@Override
	public BkChanYeLianTreeNode getNodeData(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period, Boolean calwholeweek) {
		BkChanYeLianTreeNode result = this.getNodeData( CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.DZHBK),
				requiredstartday, requiredendday, period,calwholeweek);
		return result;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period,Boolean calwholeweek)
	{
		this.getNodeKXian ( (BanKuai) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.DZHBK),
				requiredstartday, requiredendday, period, calwholeweek);
		
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(BkChanYeLianTreeNode dapan, LocalDate requiredstartday,
			LocalDate requiredendday, String period,Boolean calwholeweek) 
	{
//		this.getNodeData(bk, requiredstartday, requiredendday, period, calwholeweek);
		
//		BanKuai shdpbankuai = (BanKuai) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.DZHBK);
//		BanKuai szdpbankuai = (BanKuai) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.DZHBK);
//		BanKuai cybdpbankuai = (BanKuai) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode("399006",BkChanYeLianTreeNode.DZHBK);
		
		BanKuai shdpbankuai = ((DaPan)dapan).getDaPanShangHai ();
		BanKuai szdpbankuai = ((DaPan)dapan).getDaPanShenZhen ();
		
		shdpbankuai = (BanKuai) this.servicsofbk.getNodeKXian(shdpbankuai, requiredstartday, requiredendday,period,true);
		szdpbankuai = (BanKuai) this.servicsofbk.getNodeKXian(szdpbankuai, requiredstartday, requiredendday,period,true);
//		cybdpbankuai = (BanKuai) this.servicsofbk.getNodeKXian(cybdpbankuai, requiredstartday, requiredendday, period,true);
		
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
	public void syncNodeData(BkChanYeLianTreeNode dapan) 
	{
		BanKuai shdpbankuai = ((DaPan)dapan).getDaPanShangHai ();
		BanKuai szdpbankuai = ((DaPan)dapan).getDaPanShenZhen ();
		
//		BanKuai shdpbankuai = (BanKuai) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.DZHBK);
		shdpbankuai.getServicesForNode(true).syncNodeData(shdpbankuai);
		shdpbankuai.getServicesForNode(false);
		
//		BanKuai szdpbankuai = (BanKuai) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.DZHBK);
		szdpbankuai.getServicesForNode(true).syncNodeData(szdpbankuai);
		szdpbankuai.getServicesForNode(false);
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
	private List<Interval> getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
	(LocalDate requiredstartday,LocalDate requiredendday,LocalDate nodestart,LocalDate nodeend)
	{
		if(nodestart == null)
		return null;
		
		List<Interval> result = new ArrayList<Interval> ();
		
		DateTime nodestartdt= new DateTime(nodestart.getYear(), nodestart.getMonthValue(), nodestart.getDayOfMonth(), 0, 0, 0, 0);
		DateTime nodeenddt = new DateTime(nodeend.getYear(), nodeend.getMonthValue(), nodeend.getDayOfMonth(), 0, 0, 0, 0);
		Interval nodeinterval = new Interval(nodestartdt, nodeenddt);
		DateTime requiredstartdt= new DateTime(requiredstartday.getYear(), requiredstartday.getMonthValue(), requiredstartday.getDayOfMonth(), 0, 0, 0, 0);
		DateTime requiredenddt= new DateTime(requiredendday.getYear(), requiredendday.getMonthValue(), requiredendday.getDayOfMonth(), 0, 0, 0, 0);
		Interval requiredinterval = new Interval(requiredstartdt,requiredenddt);
		
		Interval overlapinterval = requiredinterval.overlap(nodeinterval);
		if(overlapinterval != null) {
		DateTime overlapstart = overlapinterval.getStart();
		DateTime overlapend = overlapinterval.getEnd();
		
		Interval resultintervalpart1 = null ;
		if(requiredstartday.isBefore(nodestart)) {
			resultintervalpart1 = new Interval(requiredstartdt,overlapstart);
		} 
		
		Interval resultintervalpart2 = null;
		if (requiredendday.isAfter(nodeend)) {
			resultintervalpart2 = new Interval(overlapend,requiredenddt);
		}
		if(resultintervalpart1 != null)
			result.add(resultintervalpart1);
		if(resultintervalpart2 != null)
			result.add(resultintervalpart2);
		//return result;
		}
		if(requiredinterval.abuts(nodeinterval)) {
		result.add(requiredinterval);
		// return result;
		}
		Interval gapinterval = requiredinterval.gap(nodeinterval);
		if(gapinterval != null) {
		result.add(requiredinterval);
		result.add(gapinterval);
		}
		
		return result;
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

