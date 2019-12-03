package com.exchangeinfomanager.ServicesForNodes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.Services.ServicesForNode;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

public class SvsForNodeOfStock implements ServicesForNode
{
	private BanKuaiAndStockTree allbkstk;
	private BanKuaiDbOperation bkdbopt;

	public SvsForNodeOfStock ()
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
		return allbkstk.getSpecificNodeByHypyOrCode(nodenames, BkChanYeLianTreeNode.TDXGG);
	}

	@Override
	public Collection<BkChanYeLianTreeNode> getAllNodes() 
	{
		Collection<BkChanYeLianTreeNode> allbks = new ArrayList<> ();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)allbkstk.getModel().getRoot();
		int bankuaicount = allbkstk.getModel().getChildCount(treeroot);
		for(int i=0;i< bankuaicount; i++) {
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.allbkstk.getModel().getChild(treeroot, i);
			if(childnode.getType() != BkChanYeLianTreeNode.TDXGG) 
				continue;
			
			allbks.add(childnode);
		}
		
		return allbks;
	}

	@Override
	public BkChanYeLianTreeNode getNodeJiBenMian(BkChanYeLianTreeNode node)
	{
		node = bkdbopt.getStockBasicInfo (node);
		return node;
	}

	@Override
	public Collection<BkChanYeLianTreeNode> getRequiredSubSetOfTheNodes(Set<String> subtypesset) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeData(BkChanYeLianTreeNode stock, LocalDate requiredstartday,
			LocalDate requiredendday, String period, Boolean calwholeweek)
	{
		NodeXPeriodData nodedayperioddata = ((Stock)stock).getNodeXPeroidData(period);
		
		if(!calwholeweek) {
			LocalDate curdate = ((Stock)stock).getNodeDataAtNotCalWholeWeekModeLastDate();
			if(curdate == null) {
				((Stock)stock).setNodeDataAtNotCalWholeWeekMode(requiredendday);
				nodedayperioddata.resetAllData();
			} else if(!curdate.isEqual(requiredendday)) {
				((Stock)stock).setNodeDataAtNotCalWholeWeekMode(requiredendday);
				nodedayperioddata.resetAllData();
			} else
				return stock;
		}
		if(calwholeweek) {
			if(((Stock)stock).isNodeDataAtNotCalWholeWeekMode() ) {
				((Stock)stock).setNodeDataAtNotCalWholeWeekMode(null);
				nodedayperioddata.resetAllData();
			}
		}
		
		if(nodedayperioddata.getOHLCRecordsStartDate() == null) {
			stock = bkdbopt.getStockZhanBi ((Stock) stock,requiredstartday,requiredendday,period);
			return stock;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getOHLCRecordsStartDate(),nodedayperioddata.getOHLCRecordsEndDate()  );
		if(timeintervallist == null)
			return stock;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth());
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				
				stock = bkdbopt.getStockZhanBi (((Stock)stock),requiredstartday,requiredendday,period);
		}
		return stock;
	}

	@Override
	public BkChanYeLianTreeNode getNodeData(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(String stockcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period) 
	{
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(BkChanYeLianTreeNode stock, LocalDate requiredstartday,
			LocalDate requiredendday, String period)
	{
		NodeXPeriodData nodedayperioddata = ((Stock)stock).getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
		if(nodedayperioddata.getOHLCRecordsStartDate() == null) {
			stock = (Stock)bkdbopt.getStockDailyKXianZouShiFromCsv (((Stock)stock),requiredstartday,requiredendday,period);
			return stock;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getOHLCRecordsStartDate(),nodedayperioddata.getOHLCRecordsEndDate() );
		if(timeintervallist == null)
			return stock;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				stock = (Stock)bkdbopt.getStockDailyKXianZouShiFromCsv (((Stock)stock),requiredstartday,requiredendday,period);
		}
		return stock;
	}

	@Override
	public BkChanYeLianTreeNode getNodeZhangDieTingInfo(BkChanYeLianTreeNode bk, LocalDate requiredstartday,
			LocalDate requiredendday, String period) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeQueKouInfo(String bkcode, LocalDate requiredrecordsday, LocalDate requiredendday,
			String period) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeQueKouInfo(BkChanYeLianTreeNode stock, LocalDate requiredstartday,
			LocalDate requiredendday, String period) 
	{
		NodeXPeriodData stockxwkdate = ((Stock)stock).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		LocalDate qkstartday = stockxwkdate.getQueKouRecordsStartDate();
		LocalDate qkendday = stockxwkdate.getQueKouRecordsEndDate();
		
		NodeXPeriodData nodedayperioddata = ((Stock)stock).getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
		if(qkstartday == null) {
			stock = bkdbopt.getStockDailyQueKouAnalysisResult (((Stock)stock),requiredstartday,requiredendday,period);
//			return stock;
		} else {
			List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
					(requiredstartday,requiredendday,qkstartday,qkendday );
			if(timeintervallist == null)
				return stock;
			
			for(Interval tmpinterval : timeintervallist) {
					DateTime newstartdt = tmpinterval.getStart();
					DateTime newenddt = tmpinterval.getEnd();
					
					requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
					
					stock = bkdbopt.getStockDailyQueKouAnalysisResult (((Stock)stock),requiredstartday,requiredendday,period);
			}
		}
		
		if(period.equals(NodeGivenPeriodDataItem.DAY)) { //���߼������ˣ����߸���ͳ������

			List<QueKou> qklist = nodedayperioddata .getPeriodQueKou();
			if(qklist == null || qklist.size() == 0)
				return stock;
			
			stockxwkdate.resetQueKouTongJiJieGuo ();
			
			LocalDate qkstartdate = qklist.get(0).getQueKouDate();
			LocalDate qkenddate = qklist.get(qklist.size() -1 ).getQueKouDate();
			LocalDate tmpdate = qkstartdate;
			int indexstart =0; Boolean readytojumpoutloop = false;
			do {
				
				for(int index = indexstart;index<qklist.size();index++) {
					QueKou tmpqk = qklist.get(index);
					LocalDate tmpqkdate = tmpqk.getQueKouDate();
					
					if(CommonUtility.isInSameWeek(tmpqkdate, tmpdate)) {
						if(tmpqk.getQueKouLeiXing()) { //upȱ��
							
							stockxwkdate.addQueKouTongJiJieGuo(tmpqkdate, 1, null, null, null, true);
							if(tmpqk.getQueKouHuiBuDate() != null) { //�Ѿ����ز�
								LocalDate tmpqkhbdate = tmpqk.getQueKouHuiBuDate();
							
								stockxwkdate.addQueKouTongJiJieGuo(tmpqkhbdate, null, 1, null, null, true);
							} 
						} else { //downȱ��
							
							stockxwkdate.addQueKouTongJiJieGuo(tmpqkdate, null, null, 1, null, true);
							if(tmpqk.getQueKouHuiBuDate() != null) { //�Ѿ����ز�
								LocalDate tmpqkhbdate = tmpqk.getQueKouHuiBuDate();
							
								stockxwkdate.addQueKouTongJiJieGuo(tmpqkhbdate, null, null, null, 1, true);
							}
						}
						
						if(index == qklist.size() -1) {
							readytojumpoutloop = true;
						}
						
					} else {
						tmpdate = tmpqkdate;
						indexstart = index;
						break;
					}
				}
				
				if(readytojumpoutloop)
					break;
				
			} while (tmpdate.isBefore( qkenddate) || tmpdate.isEqual(qkenddate));
			
			 if(!qkstartdate.equals(requiredstartday) && requiredstartday.isBefore(qkstartdate)) { //�ر���������openupquekou����ʼ���ڣ����ڻ��ȱ��ͳ�Ƶ���ʼʱ��?
				 stockxwkdate.addQueKouTongJiJieGuo ( requiredstartday, 0, null, null, null,true);
        	 }  
			 if(!qkenddate.equals(requiredendday)  && requiredendday.isAfter(qkenddate) )  //�ر���������openupquekou�Ľ������ڣ����ڻ��ȱ��ͳ�ƵĽ���ʱ��?
        		 stockxwkdate.addQueKouTongJiJieGuo ( requiredendday, 0, null, null, null,true);
		}

		return stock;
	}

	@Override
	public void syncNodeData(BkChanYeLianTreeNode stock) 
	{
		LocalDate bkstartday = ((Stock)stock).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsStartDate();
		LocalDate bkendday = ((Stock)stock).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsEndDate();
		
		if(bkstartday != null) 
			this.getNodeKXian(((Stock)stock), bkstartday, bkendday, NodeGivenPeriodDataItem.DAY);

		if(bkstartday != null && !((Stock)stock).isNodeDataAtNotCalWholeWeekMode() )
			this.getNodeQueKouInfo(((Stock)stock), bkstartday, bkendday, NodeGivenPeriodDataItem.DAY);
		
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

}