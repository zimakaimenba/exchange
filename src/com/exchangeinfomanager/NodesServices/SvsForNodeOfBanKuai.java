package com.exchangeinfomanager.NodesServices;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.Services.ServicesForNode;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

public class SvsForNodeOfBanKuai implements ServicesForNode
{

	private BanKuaiAndStockTree allbkstk;
	private BanKuaiDbOperation bkdbopt;

	public SvsForNodeOfBanKuai ()
	{
		this.bkdbopt = new BanKuaiDbOperation ();
		allbkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
	}
	@Override
	public Collection<BkChanYeLianTreeNode> getNodes(Collection<String> nodenames)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNode(String nodenames)
	{
		return allbkstk.getSpecificNodeByHypyOrCode(nodenames, BkChanYeLianTreeNode.TDXBK);
	}
//	@Override
//	public BkChanYeLianTreeNode getNodeByNameOrCode(String nameorcode, int nodetype) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public Collection<BkChanYeLianTreeNode> getAllNodes() 
	{
		Collection<BkChanYeLianTreeNode> allbks = new ArrayList<> ();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)allbkstk.getModel().getRoot();
		int bankuaicount = allbkstk.getModel().getChildCount(treeroot);
		for(int i=0;i< bankuaicount; i++) {
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.allbkstk.getModel().getChild(treeroot, i);
			if(childnode.getType() != BkChanYeLianTreeNode.TDXBK) 
				continue;
			
			allbks.add(childnode);
		}
		
		return allbks;
	}

	@Override
	public BkChanYeLianTreeNode getNodeJiBenMian(BkChanYeLianTreeNode node) 
	{
		node = bkdbopt.getBanKuaiBasicInfo ((BanKuai) node);
		return node;
	}

	@Override
	public Collection<BkChanYeLianTreeNode> getRequiredSubSetOfTheNodes(Set<String> subtypesset) 
	{
		Collection<BkChanYeLianTreeNode> bklist = new ArrayList<BkChanYeLianTreeNode> ();
		
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.allbkstk.getModel().getRoot();
		int bankuaicount = allbkstk.getModel().getChildCount(treeroot);
		
		for(int i=0;i< bankuaicount; i++) {
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.allbkstk.getModel().getChild(treeroot, i);
			if(childnode.getType() != BkChanYeLianTreeNode.TDXBK) 
				continue;
			
			String bktype = ((BanKuai)childnode).getBanKuaiLeiXing();
			if(subtypesset.contains(bktype))
				bklist.add(childnode);
		}
		
		return bklist;
	}

	@Override
	public BkChanYeLianTreeNode getNodeData(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period, Boolean calwholeweek) 
	{
		NodeXPeriodData nodedayperioddata = ((BanKuai)bankuai).getNodeXPeroidData(period);
		
		if(!calwholeweek) {
			LocalDate curdate = ((BanKuai)bankuai).getNodeDataAtNotCalWholeWeekModeLastDate();
			if(curdate == null) {
				((BanKuai)bankuai).setNodeDataAtNotCalWholeWeekMode(requiredendday);
				nodedayperioddata.resetAllData();
			} else if(!curdate.isEqual(requiredendday)) {
				((BanKuai)bankuai).setNodeDataAtNotCalWholeWeekMode(requiredendday);
				nodedayperioddata.resetAllData();
			} else
				return bankuai;
		}
		if(calwholeweek) {
			if(((BanKuai)bankuai).isNodeDataAtNotCalWholeWeekMode() ) {
				((BanKuai)bankuai).setNodeDataAtNotCalWholeWeekMode(null);
				nodedayperioddata.resetAllData();
			}
		}

		if(nodedayperioddata.getOHLCRecordsStartDate() == null) {
			bankuai = bkdbopt.getBanKuaiZhanBi ((BanKuai)bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getOHLCRecordsStartDate(),nodedayperioddata.getOHLCRecordsEndDate());
		if(timeintervallist == null) 
			return bankuai;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth());
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				
				bankuai = bkdbopt.getBanKuaiZhanBi ((BanKuai) bankuai,requiredstartday,requiredendday,period);
		}
		return bankuai;
	}

	@Override
	public BkChanYeLianTreeNode getNodeData(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) 
	{
		NodeXPeriodData nodedayperioddata = ((BanKuai) bankuai).getNodeXPeroidData(period);
		if(nodedayperioddata.getOHLCRecordsStartDate() == null) {
			bankuai = bkdbopt.getBanKuaiKXianZouShi (((BanKuai) bankuai),requiredstartday,requiredendday,period);
			
			return bankuai;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getOHLCRecordsStartDate(),nodedayperioddata.getOHLCRecordsEndDate() );
		if(timeintervallist == null)
			return bankuai;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				bankuai = bkdbopt.getBanKuaiKXianZouShi (((BanKuai) bankuai),requiredstartday,requiredendday,period);
		}
		
		return bankuai;
	}

	@Override
	public BkChanYeLianTreeNode getNodeZhangDieTingInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
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
	public BkChanYeLianTreeNode getNodeQueKouInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void syncNodeData(BkChanYeLianTreeNode bankuai)
	{
//		if(bk.isNodeDataAtNotCalWholeWeekMode() )
//		return;
	
		LocalDate bkstartday = ((BanKuai)bankuai).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsStartDate();
		LocalDate bkendday = ((BanKuai)bankuai).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsEndDate();
		
		this.getNodeKXian(bankuai, bkstartday, bkendday, NodeGivenPeriodDataItem.DAY);
	//	this.getBanKuaiQueKouInfo(bk, bkstartday, bkendday, NodeGivenPeriodDataItem.WEEK);
		
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
	
	/*
	 * 
	 */
	public BanKuai getAllGeGuOfBanKuai (BanKuai bankuai) 
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsEndDate();
		
		if(bkstartday == null || bkendday == null)  {
			bkstartday = CommonUtility.getSettingRangeDate(LocalDate.now(), "middle");
			bkendday = LocalDate.now();
//			return bankuai;
		}
		
		bankuai = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (bankuai,bkstartday,bkendday,allbkstk);
		
		return bankuai;
	}
	public StockOfBanKuai getGeGuOfBanKuai(BanKuai bankuai, StockOfBanKuai stockofbk,String period)
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getOHLCRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getOHLCRecordsEndDate();
		
		if(bkstartday == null && bkendday == null) 
			return stockofbk;
		
		stockofbk = bkdbopt.getGeGuZhanBiOfBanKuai (bankuai,stockofbk, bkstartday, bkendday,period);

		return stockofbk;
	}
	/*
	 * �����������ĳ�����ɵĳɽ���?,��������Ƿ�����ø��ɵļ��?
	 */
	public StockOfBanKuai getGeGuOfBanKuai(BanKuai bankuai, String stockcode,String period)
	{
		BkChanYeLianTreeNode stock = bankuai.getBanKuaiGeGu(stockcode);
		if(stock == null)
			return null;
		
		stock = this.getGeGuOfBanKuai( bankuai,  (StockOfBanKuai) stock,period);
		return (StockOfBanKuai) stock;
	}
	public StockOfBanKuai getGeGuOfBanKuai(String bkcode, String stockcode,String period) 
	{
		BanKuai bankuai = (BanKuai) allbkstk.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		if(bankuai == null)
			return null;
		
		StockOfBanKuai stock = (StockOfBanKuai) bankuai.getBanKuaiGeGu(stockcode);
		if(stock == null)
			return null;
		
		stock = this.getGeGuOfBanKuai( bankuai,  stock,period);

		return stock;
	}
	
	public void syncBanKuaiAndItsStocksForSpecificTime (BanKuai bk,LocalDate requiredstartday,LocalDate requiredendday,String period,Boolean calwholeweek  ) throws SQLException
	{
		this.getNodeData(bk, requiredstartday, requiredendday, period,calwholeweek);
		
		this.syncNodeData(bk);
		
		 TagsServiceForNodes tagsevofbk = new TagsServiceForNodes (bk);
		 Collection<Tag> tags = tagsevofbk.getTags();
		 bk.setNodeTags(tags);
		
		if(bk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) { 
			SvsForNodeOfStock svsforstock = new SvsForNodeOfStock (); 
			bk = this.getAllGeGuOfBanKuai (bk); 
//			List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
			Collection<BkChanYeLianTreeNode> allbkgg = bk.getSpecificPeriodBanKuaiGeGu (requiredendday,0);
			for(BkChanYeLianTreeNode stockofbk : allbkgg)   {
//		    	if( ((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(requiredendday)  ) { 
		    		 BkChanYeLianTreeNode stock = svsforstock.getNodeData(((StockOfBanKuai)stockofbk).getStock(), requiredstartday, requiredendday, period,calwholeweek);
		    		 svsforstock.syncNodeData(stock);
	    			 
	    			 TagsServiceForNodes tagsevofnode = new TagsServiceForNodes (stock);
	    			 Collection<Tag> tagsofstock = tagsevofnode.getTags();
	    			 ((Stock)stock).setNodeTags(tagsofstock);
	    			 
	    			 tagsofstock  = null;
	    			 tagsevofnode = null;
	    			 
//		    	 }
			}
			
			if(calwholeweek ) { //
				this.getNodeQueKouInfo(bk, requiredstartday, requiredendday, period);
				this.getNodeZhangDieTingInfo(bk, requiredstartday, requiredendday, period);
			}
		}
	}

}
