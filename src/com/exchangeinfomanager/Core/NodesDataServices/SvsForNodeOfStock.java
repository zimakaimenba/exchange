package com.exchangeinfomanager.Core.NodesDataServices;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.TDXNodesXPeriodExternalData;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.Core.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Core.Trees.CreateExchangeTree;
import com.exchangeinfomanager.Core.Trees.TreeOfChanYeLian;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisPlayNodeSuoShuBanKuaiListServices;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.TimeIntervalUnion;
import com.exchangeinfomanager.database.BanKuaiDZHDbOperation;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.CylTreeDbOperation;
import com.exchangeinfomanager.database.JiGouGuDongDbOperation;

public class SvsForNodeOfStock implements ServicesForNode, ServicesOfNodeStock
{
//	private BanKuaiAndStockTree allbkstk;
	private BanKuaiDbOperation bkdbopt;
	private CylTreeDbOperation dboptforcyltree;
//	private TreeOfChanYeLian cyltree;
	private JiGouGuDongDbOperation jgdbopt;

	public SvsForNodeOfStock ()
	{
		this.bkdbopt = new BanKuaiDbOperation ();
		this.jgdbopt = new JiGouGuDongDbOperation ();
	}

	@Override
	public BkChanYeLianTreeNode getNodeJiBenMian(BkChanYeLianTreeNode node)
	{
		node = bkdbopt.getStockBasicInfo (node);
		return node;
	}
	public BkChanYeLianTreeNode checkStockHasHuangQinGuoQieAndMingXin(Stock node, LocalDate requiredstart, LocalDate requiredend)
	{
		this.jgdbopt.checkStockHasHuangQinGuoQieAndMingXin (node);
		return node;
	}
	public BkChanYeLianTreeNode getStockGuDong(Stock node, String gudongtype ,LocalDate requiredstart, LocalDate requiredend)
	{
		node = this.jgdbopt.getStockGuDong (node, gudongtype, requiredstart, requiredend);
		this.checkStockHasHuangQinGuoQieAndMingXin (node,  requiredstart, requiredend);
		return node;
	} 
	@Override
	public void syncNodeData(BkChanYeLianTreeNode stk) 
	{
		Stock stock = (Stock)stk;
		LocalDate bkohlcstartday = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).getOHLCRecordsStartDate();
		LocalDate bkohlcendday = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).getOHLCRecordsEndDate();
		
		NodeXPeriodData nodexdatawk = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		LocalDate bkamostartday =	nodexdatawk.getAmoRecordsStartDate();
		LocalDate bkamoendday = nodexdatawk.getAmoRecordsEndDate();
				
		if(bkohlcstartday == null && bkamostartday == null) return ;
		LocalDate requiredstartday; LocalDate requiredendday = null;
		if(bkohlcstartday == null) {
			requiredstartday = bkamostartday;
			requiredendday = bkamoendday;
		} else {
			if(bkohlcstartday.isBefore(bkamostartday)) requiredstartday = bkohlcstartday;
			else	requiredstartday = bkamostartday;
			
			if(bkohlcendday.isAfter(bkamoendday)) requiredendday = bkohlcendday;
			else	requiredendday = bkamoendday;
		}
		
		stk = this.getNodeKXian(stk, requiredstartday,requiredendday, NodeGivenPeriodDataItem.DAY,true);
		extraActionsForStock ((Stock) stk);
		return;
	}
	@Override
	public BkChanYeLianTreeNode getNodeData(BkChanYeLianTreeNode stk, LocalDate requiredstartday,
			LocalDate requiredendday, String period, Boolean calwholeweek)
	{
		Stock stock = (Stock) stk;
		
		requiredstartday = requiredstartday.with(DayOfWeek.MONDAY);
		requiredendday = ServicesForNode.getNodeCaculateEndDateAndRelatedActions(stock, period, requiredendday, calwholeweek);
		
		NodeXPeriodData nodexdatawk = stock.getNodeXPeroidData(period);

		if(nodexdatawk.getAmoRecordsStartDate() == null) {
			stock = bkdbopt.getStockZhanBi (stock,requiredstartday,requiredendday,period);
			return stock;
		}
		
		List<Interval> timeintervallist;
		timeintervallist = TimeIntervalUnion.getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval(requiredstartday,requiredendday,nodexdatawk.getAmoRecordsStartDate(),nodexdatawk.getAmoRecordsEndDate());
		if(timeintervallist == null)
			return stock;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				//这里比较复杂，如果是非完整周，那就计算到设置日期，如果不是非完整周，是过去时间，过去那还是要计算完整一周的数据
				LocalDate isnotcalwholewkdate = nodexdatawk.isInNotCalWholeWeekMode();
				if( isnotcalwholewkdate == null ) //完整周，到周末
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				else if( CommonUtility.isInSameWeek(isnotcalwholewkdate, requiredendday) ) //非完整周模式，在指定计算非完整周，那到指定日期 
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				else if( isnotcalwholewkdate != null && !CommonUtility.isInSameWeek(isnotcalwholewkdate, requiredendday) )	//非完整周模式，在过去日期，还是要计算完整的周数据
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				stock = bkdbopt.getStockZhanBi (stock,requiredstartday,requiredendday,period);
		}
		
//		((TDXNodesXPeriodExternalData)nodedayperioddata).getKLearnResult ();
		return stock;
	}

	@Override
	public BkChanYeLianTreeNode getNodeGzMrMcYkInfo (BkChanYeLianTreeNode node,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{
		node =  bkdbopt.getNodeGzMrMcYkInfo((TDXNodes)node, selecteddatestart,selecteddateend, period);
		return node;
	}
	@Override
	public BkChanYeLianTreeNode getNodeData(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period, Boolean calwholeweek) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(String stockcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period,Boolean calwholeweek) {
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(BkChanYeLianTreeNode stock, LocalDate requiredstartday,
			LocalDate requiredendday, String period,Boolean calwholeweek)
	{
		Stock stk = (Stock) stock;
		NodeXPeriodData nodedayperioddata = stk.getNodeXPeroidData(period);
		requiredstartday = requiredstartday.with(DayOfWeek.MONDAY);
		if(nodedayperioddata.isInNotCalWholeWeekMode() == null ) 
			requiredendday = requiredendday.with(DayOfWeek.FRIDAY);

		if(nodedayperioddata.getOHLCRecordsStartDate() == null) {
			try {
				stock = (Stock)bkdbopt.getStockDailyKXianZouShiFromCsv (stk,requiredstartday,requiredendday,period);
				nodedayperioddata.calNodeOhlcMA();
				nodedayperioddata.calNodeAMOMA ();
			} catch (java.lang.NullPointerException e) {e.printStackTrace();}
			return stock;
		}
		
		List<Interval> timeintervallist = TimeIntervalUnion.getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getOHLCRecordsStartDate(),nodedayperioddata.getOHLCRecordsEndDate() );
		if(timeintervallist == null)
			return stock;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				//这里比较复杂，如果是非完整周，那就计算到设置日期，如果不是非完整周，是过去时间，过去那还是要计算完整一周的数据
				LocalDate isnotcalwholewkdate = nodedayperioddata.isInNotCalWholeWeekMode();
				if( isnotcalwholewkdate == null ) //完整周，到周末
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				else if( CommonUtility.isInSameWeek(isnotcalwholewkdate, requiredendday) ) //非完整周模式，在指定计算非完整周，那到指定日期 
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				else if( isnotcalwholewkdate != null && !CommonUtility.isInSameWeek(isnotcalwholewkdate, requiredendday) )	//非完整周模式，在过去日期，还是要计算完整的周数据
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				stock = (Stock)bkdbopt.getStockDailyKXianZouShiFromCsv (stk,requiredstartday,requiredendday,period);
		}
		nodedayperioddata.calNodeOhlcMA();
		nodedayperioddata.calNodeAMOMA ();
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
			List<Interval> timeintervallist = TimeIntervalUnion.getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
					(requiredstartday,requiredendday,qkstartday,qkendday );
			if(timeintervallist == null)
				return stock;
			
			for(Interval tmpinterval : timeintervallist) {
					DateTime newstartdt = tmpinterval.getStart();
					DateTime newenddt = tmpinterval.getEnd();
					
					requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
					//这里比较复杂，如果是非完整周，那就计算到设置日期，如果不是非完整周，是过去时间，过去那还是要计算完整一周的数据
					LocalDate isnotcalwholewkdate = nodedayperioddata.isInNotCalWholeWeekMode();
					if( isnotcalwholewkdate == null ) //完整周，到周末
						requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
					else if( CommonUtility.isInSameWeek(isnotcalwholewkdate, requiredendday) ) //非完整周模式，在指定计算非完整周，那到指定日期 
						requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
					else if( isnotcalwholewkdate != null && !CommonUtility.isInSameWeek(isnotcalwholewkdate, requiredendday) )	//非完整周模式，在过去日期，还是要计算完整的周数据
						requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
					
					stock = bkdbopt.getStockDailyQueKouAnalysisResult (((Stock)stock),requiredstartday,requiredendday,period);
			}
		}
		
		if(period.equals(NodeGivenPeriodDataItem.DAY)) { //Calculate the weekly QueKou info for Stock

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
						if(tmpqk.getQueKouLeiXing()) { 
							
							stockxwkdate.addQueKouTongJiJieGuo(tmpqkdate, 1, null, null, null, true);
							if(tmpqk.getQueKouHuiBuDate() != null) { 
								LocalDate tmpqkhbdate = tmpqk.getQueKouHuiBuDate();
							
								stockxwkdate.addQueKouTongJiJieGuo(tmpqkhbdate, null, 1, null, null, true);
							} 
						} else { 
							
							stockxwkdate.addQueKouTongJiJieGuo(tmpqkdate, null, null, 1, null, true);
							if(tmpqk.getQueKouHuiBuDate() != null) { 
								LocalDate tmpqkhbdate = tmpqk.getQueKouHuiBuDate();
							
								stockxwkdate.addQueKouTongJiJieGuo(tmpqkhbdate, null, null, null, 1, true);
							}
						}
						
						if(index == qklist.size() -1) 			readytojumpoutloop = true;
					} else {
						tmpdate = tmpqkdate;
						indexstart = index;
						break;
					}
				}
				
				if(readytojumpoutloop)
					break;
				
			} while (tmpdate.isBefore( qkenddate) || tmpdate.isEqual(qkenddate));
			
			 if(!qkstartdate.equals(requiredstartday) && requiredstartday.isBefore(qkstartdate)) { 
				 stockxwkdate.addQueKouTongJiJieGuo ( requiredstartday, 0, null, null, null,true);
        	 }  
			 if(!qkenddate.equals(requiredendday)  && requiredendday.isAfter(qkenddate) )  
        		 stockxwkdate.addQueKouTongJiJieGuo ( requiredendday, 0, null, null, null,true);
		}

		return stock;
	}


	
	private void extraActionsForStock (Stock stock)
	{
		NodeXPeriodData nodexdatawk = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		LocalDate bkamostartday =	nodexdatawk.getAmoRecordsStartDate();
		LocalDate bkamoendday =	nodexdatawk.getAmoRecordsEndDate();
		
		if(stock.getNodeJiBenMian().getZdgzmrmcykRecords() == null || stock.getNodeJiBenMian().getZdgzmrmcykRecords().isEmpty() )
			this.getNodeGzMrMcYkInfo(stock,  bkamostartday, bkamoendday, NodeGivenPeriodDataItem.DAY);
		
		if (nodexdatawk.getPeriodQueKou () == null || nodexdatawk.getPeriodQueKou ().isEmpty() )
			this.getNodeQueKouInfo(stock, bkamostartday, bkamoendday, NodeGivenPeriodDataItem.DAY);
		if(stock.getNodeJiBenMian().getGuDongInfo () == null || stock.getNodeJiBenMian().getGuDongInfo ().isEmpty() )
			this.getStockGuDong( stock, "LIUTONG", bkamostartday, bkamoendday);
		
		if(  stock.getGeGuCurSuoShuTDXSysBanKuaiList() == null ||  stock.getGeGuCurSuoShuTDXSysBanKuaiList().isEmpty() )
			this.getNodeSuoShuBanKuaiList(stock); //先取得板块信息，可以用来在TABLE过滤
	}
	
//	private List<Interval> getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
//	(LocalDate requiredstartday,LocalDate requiredendday,LocalDate nodestart,LocalDate nodeend)
//	{
//		if(nodestart == null)	return null;
//		
//		List<Interval> result = new ArrayList<Interval> ();
//		
//		DateTime nodestartdt= new DateTime(nodestart.getYear(), nodestart.getMonthValue(), nodestart.getDayOfMonth(), 0, 0, 0, 0);
//		DateTime nodeenddt = new DateTime(nodeend.getYear(), nodeend.getMonthValue(), nodeend.getDayOfMonth(), 0, 0, 0, 0);
//		Interval nodeinterval = null;
//		try {
//			nodeinterval = new Interval(nodestartdt, nodeenddt);
//		} catch ( java.lang.IllegalArgumentException e) {
//			e.printStackTrace();
//			return result;
//		}
//		DateTime requiredstartdt= new DateTime(requiredstartday.getYear(), requiredstartday.getMonthValue(), requiredstartday.getDayOfMonth(), 0, 0, 0, 0);
//		DateTime requiredenddt= new DateTime(requiredendday.getYear(), requiredendday.getMonthValue(), requiredendday.getDayOfMonth(), 0, 0, 0, 0);
//		Interval requiredinterval = new Interval(requiredstartdt,requiredenddt);
//		
//		Interval overlapinterval = requiredinterval.overlap(nodeinterval);
//		if(overlapinterval != null) {
//			DateTime overlapstart = overlapinterval.getStart();
//			DateTime overlapend = overlapinterval.getEnd();
//			
//			Interval resultintervalpart1 = null ;
//			if(requiredstartday.isBefore(nodestart)) {
//				resultintervalpart1 = new Interval(requiredstartdt,overlapstart.minusDays(1));
//			} 
//			
//			Interval resultintervalpart2 = null;
//			if (requiredendday.isAfter(nodeend)) {
//				resultintervalpart2 = new Interval(overlapend.plusDays(1),requiredenddt);
//			}
//			if(resultintervalpart1 != null)
//				result.add(resultintervalpart1);
//			if(resultintervalpart2 != null)
//				result.add(resultintervalpart2);
//		}
//		if(requiredinterval.abuts(nodeinterval)) {
//			result.add(requiredinterval);
//		}
//		Interval gapinterval = requiredinterval.gap(nodeinterval);
//		if(gapinterval != null) {
//			result.add(requiredinterval);
//			result.add(gapinterval);
//		}
//		
//		return result;
//	}

	@Override
	public List<BkChanYeLianTreeNode> getNodeChanYeLianInfo(String nodecode) 
	{
		if(dboptforcyltree == null)
			dboptforcyltree = new CylTreeDbOperation (CreateExchangeTree.CreateTreeOfChanYeLian());
		
		List<BkChanYeLianTreeNode> cylinfo = this.dboptforcyltree.getChanYeLianInfo (nodecode,BkChanYeLianTreeNode.TDXGG);
		return cylinfo;
	}

	@Override
	public List<BkChanYeLianTreeNode> getNodeSlidingInChanYeLianInfo(String nodecode) 
	{
		if(dboptforcyltree == null)
			dboptforcyltree = new CylTreeDbOperation (CreateExchangeTree.CreateTreeOfChanYeLian());
		
		List<BkChanYeLianTreeNode> cylsliding = this.dboptforcyltree.getSlidingInChanYeLianInfo (nodecode,BkChanYeLianTreeNode.TDXGG);
		return cylsliding;
	}

	@Override
	public List<BkChanYeLianTreeNode> getNodeChildrenInChanYeLianInfo(String nodecode) 
	{
		if(dboptforcyltree == null)
			dboptforcyltree = new CylTreeDbOperation (CreateExchangeTree.CreateTreeOfChanYeLian());
		
		List<BkChanYeLianTreeNode> cylchildren  = this.dboptforcyltree.getChildrenInChanYeLianInfo (nodecode,BkChanYeLianTreeNode.TDXGG);
		return cylchildren;
	}
	public  Number[] getTDXNodeNoneFixPeriodDpMinMaxWk (Stock stock, LocalDate requiredstartday, LocalDate requiredendday)
	{
		NodeGivenPeriodDataItem dataresult = bkdbopt.getStockNoFixPerioidZhanBiByWeek (stock,requiredstartday,requiredendday,NodeGivenPeriodDataItem.WEEK);
		Double zhanbi = dataresult.getNodeToDpChenJiaoErZhanbi();
		
		NodeXPeriodData nodexdate = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		LocalDate recordstart = nodexdate.getOHLCRecordsStartDate();
		LocalDate recordend = nodexdate.getOHLCRecordsEndDate();
		LocalDate tmpdate = requiredstartday.minusWeeks(1).with(DayOfWeek.FRIDAY);
		 
		if(tmpdate.isBefore(recordstart)) {
			Number[] zhanbiresult = {zhanbi, null};
			return zhanbiresult;
		}
			 
		
		Integer dpminwk = 0; Integer dpmaxwk = 0;
		while (tmpdate.isAfter(recordstart) ) {
			Double tmpzb = nodexdate.getChenJiaoErZhanBi(tmpdate);
			
			try {
				if(tmpzb >= zhanbi) {
					dpminwk ++;
					tmpdate = tmpdate.minusWeeks(1).with(DayOfWeek.FRIDAY);
				} else if(tmpzb < zhanbi) {
					break;
				}
			} catch (java.lang.NullPointerException e) {
				tmpdate = tmpdate.minusWeeks(1).with(DayOfWeek.FRIDAY);
			}
		}
		tmpdate = requiredstartday.minusWeeks(1).with(DayOfWeek.FRIDAY);
		while (tmpdate.isAfter(recordstart) ) {
			Double tmpzb = nodexdate.getChenJiaoErZhanBi(tmpdate);
			try {
				if(tmpzb < zhanbi) {
					dpmaxwk ++;
					tmpdate = tmpdate.minusWeeks(1).with(DayOfWeek.FRIDAY);
				} else if(tmpzb >= zhanbi) {
					break;
				}
			} catch (java.lang.NullPointerException e) {
				tmpdate = tmpdate.minusWeeks(1).with(DayOfWeek.FRIDAY);
			}
		}
		
		Integer zbresult;
		if(dpminwk != 0)
			zbresult = 0 - dpminwk;
		else
			zbresult = dpmaxwk;
		Number[] zhanbiresult = {zhanbi,zbresult};
		return zhanbiresult;
				
	}

	public BkChanYeLianTreeNode getNodeSuoShuBanKuaiList (BkChanYeLianTreeNode node)
	{
		if(  ((Stock)node).getGeGuCurSuoShuTDXSysBanKuaiList() == null ||  ((Stock)node).getGeGuCurSuoShuTDXSysBanKuaiList().isEmpty() )
			bkdbopt.getTDXBanKuaiSetForStock ((Stock)node ); //通达信板块信息
		
		if( ((Stock)node).getGeGuCurSuoShuDZHSysBanKuaiList() == null || ((Stock)node).getGeGuCurSuoShuDZHSysBanKuaiList().isEmpty() ) {
			BanKuaiDZHDbOperation dzhdbopt = new BanKuaiDZHDbOperation ();
			dzhdbopt.getDZHBanKuaiForAStock((Stock)node);
			dzhdbopt = null;
		}

		return node;
	}
	public BkChanYeLianTreeNode getNodeSuoShuBanKuaiList (BkChanYeLianTreeNode node,Boolean forcetorefreshdata)
	{
		if(forcetorefreshdata) {
			bkdbopt.getTDXBanKuaiSetForStock ((Stock)node ); //通达信板块信息
			
			BanKuaiDZHDbOperation dzhdbopt = new BanKuaiDZHDbOperation ();
			dzhdbopt.getDZHBanKuaiForAStock((Stock)node);
			dzhdbopt = null;
		} else {
			if(  ((Stock)node).getGeGuCurSuoShuTDXSysBanKuaiList() == null ||  ((Stock)node).getGeGuCurSuoShuTDXSysBanKuaiList().isEmpty() )
				bkdbopt.getTDXBanKuaiSetForStock ((Stock)node ); //通达信板块信息
			
			if( ((Stock)node).getGeGuCurSuoShuDZHSysBanKuaiList() == null || ((Stock)node).getGeGuCurSuoShuDZHSysBanKuaiList().isEmpty() ) {
				BanKuaiDZHDbOperation dzhdbopt = new BanKuaiDZHDbOperation ();
				dzhdbopt.getDZHBanKuaiForAStock((Stock)node);
				dzhdbopt = null;
			}
		}
		
		return node;
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
		node = this.bkdbopt.getNodeCjeCjlExtremeZhanbiUpDownLevel( (TDXNodes)node);
		return node;
	}

	@Override
	public void setNodeCjeExtremeUpDownZhanbiLevel(BkChanYeLianTreeNode node, Double min, Double max) {
		node = this.bkdbopt.setNodeCjeExtremeZhanbiUpDownLevel( (TDXNodes)node, min, max);
		((Stock)node).getNodeJiBenMian().setNodeCjeZhanbiLevel (min,max);
	}

	@Override
	public void setNodeCjlExtremeUpDownZhanbiLevel(BkChanYeLianTreeNode node, Double min, Double max) {
		node = this.bkdbopt.setNodeCjlExtremeZhanbiUpDownLevel( (TDXNodes)node, min, max);
		((Stock)node).getNodeJiBenMian().setNodeCjlZhanbiLevel (min,max);
		
	}

	@Override
	public BkChanYeLianTreeNode refreshStockGuDong(Stock stock, Boolean onlyimportwithjigougudong,
			Boolean forcetorefrshallgudong) {
		stock = this.jgdbopt.refreshStockGuDongData ( stock,  onlyimportwithjigougudong, forcetorefrshallgudong);
		return stock;
	}
	@Override
	public Collection<BkChanYeLianTreeNode> getNodesWithKeyWords (String keywords)
	{
		Collection<BkChanYeLianTreeNode> result = this.bkdbopt.searchKeyWordsInStockJiBenMian (keywords);
		return result;
	}
}
