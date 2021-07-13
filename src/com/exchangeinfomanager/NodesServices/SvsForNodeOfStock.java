package com.exchangeinfomanager.NodesServices;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisPlayNodeSuoShuBanKuaiListServices;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.Trees.TreeOfChanYeLian;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDZHDbOperation;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.CylTreeDbOperation;
import com.exchangeinfomanager.database.JiGouGuDongDbOperation;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.TDXNodesXPeriodExternalData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

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
	public BkChanYeLianTreeNode getNodeData(BkChanYeLianTreeNode stk, LocalDate requiredstartday,
			LocalDate requiredendday, String period, Boolean calwholeweek)
	{
		Stock stock = (Stock) stk;
		
		requiredstartday = requiredstartday.with(DayOfWeek.MONDAY);
		requiredendday = this.getNodeCaculateEndDateAndRelatedActions(stock, period, requiredendday, calwholeweek);
		
		NodeXPeriodData nodedayperioddata = stock.getNodeXPeroidData(period);

		if(nodedayperioddata.getAmoRecordsStartDate() == null) {
			stock = bkdbopt.getStockZhanBi (stock,requiredstartday,requiredendday,period);
//			bkdbopt.getStockFengXiJieGuo(stock, requiredstartday,requiredendday, period);
//			this.getNodeGzMrMcYkInfo(stock, requiredstartday,requiredendday, period);
//			((TDXNodesXPeriodExternalData)nodedayperioddata).getKLearnResult ();
			return stock;
		}
		
		List<Interval> timeintervallist;
		if(calwholeweek)
			timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getAmoRecordsStartDate(),nodedayperioddata.getAmoRecordsEndDate());
		else {
			LocalDate tmpcaldate = requiredendday.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY)	;
			timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
					(requiredstartday,requiredendday,nodedayperioddata.getAmoRecordsStartDate(),requiredendday.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY)	);  //必须要减少一周，这样才能计算出新数据		
		}
			
		if(timeintervallist == null)
			return stock;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				LocalDate tmprequiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth());
				LocalDate tmprequiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				
				stock = bkdbopt.getStockZhanBi (stock,tmprequiredstartday,tmprequiredendday,period);
//				bkdbopt.getStockFengXiJieGuo(stock, tmprequiredstartday,tmprequiredendday, period);
//				this.getNodeGzMrMcYkInfo(stock, tmprequiredstartday,tmprequiredendday, period);
		}
		
//		((TDXNodesXPeriodExternalData)nodedayperioddata).getKLearnResult ();
		return stock;
	}
	private LocalDate getNodeCaculateEndDateAndRelatedActions(BkChanYeLianTreeNode stock, String period, LocalDate requiredendday, Boolean calwholeweek)
	{
		LocalDate result = null;
		
		DayOfWeek dayofweek = requiredendday.getDayOfWeek();
		if(dayofweek.equals(DayOfWeek.SUNDAY) ) 
			requiredendday = requiredendday.minus(2,ChronoUnit.DAYS);
		else if(dayofweek.equals(DayOfWeek.SATURDAY) ) 
			requiredendday = requiredendday.minus(1,ChronoUnit.DAYS);
		
		NodeXPeriodData nodeperioddata = ((Stock)stock).getNodeXPeroidData(period);
		NodeXPeriodData nodexdataday = ((Stock)stock).getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
		
		Boolean origmode = true; //原来数据是否处于NCW / CW状态, true表示是计算whole wk
		LocalDate isInNotCalWholeWeekModeData = nodeperioddata.isInNotCalWholeWeekMode ();
		if(nodeperioddata.isInNotCalWholeWeekMode () != null ) 
			origmode = false;
		
		if(calwholeweek && origmode) { //CW TO CW
			nodeperioddata.setNotCalWholeWeekMode (null);
			nodexdataday.setNotCalWholeWeekMode (null);
			
			result  = requiredendday.with(DayOfWeek.FRIDAY);
			return result;
		}
		if(calwholeweek && !origmode) { //NCW TO CW，那就把最后一周的数据删除
			LocalDate amorecordsenddate = nodeperioddata.getAmoRecordsEndDate();
			nodeperioddata.removeNodeDataFromSpecificDate(amorecordsenddate);
			
			nodexdataday.removeNodeDataFromSpecificDate(amorecordsenddate.with(DayOfWeek.MONDAY)); //那一周的数据都不要了
			nodeperioddata.setNotCalWholeWeekMode (null);
			nodexdataday.setNotCalWholeWeekMode (null);
			
			result  = requiredendday.with(DayOfWeek.FRIDAY);
			return result; 
		}
		
		if(!calwholeweek && !origmode) {  //NCW TO NCW
			if(isInNotCalWholeWeekModeData == requiredendday) { //说明已经在相同日期计算过了
				result  = requiredendday;
				return result;
			}

			LocalDate amorecordsenddate = nodeperioddata.getAmoRecordsEndDate();
			if(amorecordsenddate != null && amorecordsenddate.isBefore(requiredendday))
				nodeperioddata.removeNodeDataFromSpecificDate(amorecordsenddate);
			else if(amorecordsenddate != null && amorecordsenddate.isAfter(requiredendday))
				nodeperioddata.removeNodeDataFromSpecificDate(requiredendday);
			
			LocalDate ohlcrecordsenddate = nodexdataday.getOHLCRecordsEndDate();
			if(ohlcrecordsenddate != null && ohlcrecordsenddate.isBefore(requiredendday))
				nodexdataday.removeNodeDataFromSpecificDate(ohlcrecordsenddate);
			else if(ohlcrecordsenddate != null && ohlcrecordsenddate.isAfter(requiredendday))
				nodexdataday.removeNodeDataFromSpecificDate(requiredendday);
			
			nodeperioddata.setNotCalWholeWeekMode (requiredendday);
			nodexdataday.setNotCalWholeWeekMode (requiredendday);
			result  = requiredendday;
			return result;
		}
		
		if(!calwholeweek && origmode) {  //CW TO NCW
			LocalDate amorecordsenddate = nodeperioddata.getAmoRecordsEndDate();
			if(amorecordsenddate != null && amorecordsenddate.isAfter(requiredendday))
				nodeperioddata.removeNodeDataFromSpecificDate(requiredendday);
			
			LocalDate ohlcrecordsenddate = nodexdataday.getOHLCRecordsEndDate();
			if(ohlcrecordsenddate != null && ohlcrecordsenddate.isAfter(requiredendday))
				nodexdataday.removeNodeDataFromSpecificDate(requiredendday);
			
			nodeperioddata.setNotCalWholeWeekMode (requiredendday);
			nodexdataday.setNotCalWholeWeekMode (requiredendday);
			result  = requiredendday;
			return result;
		}
		
		return null;
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
			String period,Boolean calwholeweek) 
	{
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
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getOHLCRecordsStartDate(),nodedayperioddata.getOHLCRecordsEndDate() );
		if(timeintervallist == null)
			return stock;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());//.with(DayOfWeek.FRIDAY);
				
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

	@Override
	public void syncNodeData(BkChanYeLianTreeNode stk) 
	{
		Stock stock = (Stock)stk;
		LocalDate bkohlcstartday = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).getOHLCRecordsStartDate();
		LocalDate bkohlcendday = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).getOHLCRecordsEndDate();
		
		NodeXPeriodData nodexdatawk = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		LocalDate bkamostartday =	nodexdatawk.getAmoRecordsStartDate();
		LocalDate notcalwholewkmodedate = nodexdatawk.isInNotCalWholeWeekMode() ;
		LocalDate bkamoendday; 	Boolean calwholeweek;
		if(notcalwholewkmodedate == null) {
			bkamoendday = nodexdatawk.getAmoRecordsEndDate();	calwholeweek = true;
		}	else { bkamoendday = notcalwholewkmodedate; 	calwholeweek = false;	}
		
		if(bkohlcstartday == null && bkamostartday == null) return ;
		
		if(bkohlcstartday == null) {
			bkohlcstartday = bkamostartday;
			bkohlcendday = bkamoendday;
			stk = this.getNodeKXian(stk, bkohlcstartday,bkohlcendday, NodeGivenPeriodDataItem.DAY,true);
			extraActionsForStock ((Stock) stk);
			return;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(bkamostartday,bkamoendday, bkohlcstartday,bkohlcendday );
		
		if(timeintervallist == null || timeintervallist.isEmpty())		return ;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				LocalDate requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				LocalDate requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				
				this.getNodeKXian(stock, requiredstartday, requiredendday, NodeGivenPeriodDataItem.DAY, calwholeweek);
		}
		extraActionsForStock ((Stock) stk);
		
		return;
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
