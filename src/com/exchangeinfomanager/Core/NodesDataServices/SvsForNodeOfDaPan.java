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
		
		shdpbankuai = (BanKuai) shdpbankuai.getServicesForNode(true).getNodeKXian(shdpbankuai, requiredstartday, requiredendday,period,true);
		szdpbankuai = (BanKuai) shdpbankuai.getServicesForNode(true).getNodeKXian(szdpbankuai, requiredstartday, requiredendday,period,true);
		cybdpbankuai = (BanKuai) shdpbankuai.getServicesForNode(true).getNodeKXian(cybdpbankuai, requiredstartday, requiredendday, period,true);
	
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
