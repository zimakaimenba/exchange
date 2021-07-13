package com.exchangeinfomanager.NodesServices;

import java.awt.Color;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDZHDbOperation;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.JiGouGuDongDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

public class SvsForNodeOfDZHBanKuai implements ServicesForNode ,ServicesForNodeBanKuai
{
	private BanKuaiDZHDbOperation dzhbkdbopt;
	private BanKuaiDbOperation tdxbkdbopt;
	
	public SvsForNodeOfDZHBanKuai ()
	{
		this.dzhbkdbopt = new BanKuaiDZHDbOperation ();
		this.tdxbkdbopt = new BanKuaiDbOperation ();
	}
	public void syncBanKuaiAndItsStocksForSpecificTime (BanKuai bk,LocalDate requiredstartday,LocalDate requiredendday,String period,Boolean calwholeweek  ) throws SQLException
	{
//		this.getNodeData(bk, requiredstartday, requiredendday, period,calwholeweek); //没有必要再计算一遍本身的数据，因为调用前肯定已经有基本的数据了
		this.syncNodeData(bk);
		//板块数据同步后，板块个股的时间轴如果比板块短，要和板块的时间轴一致。如果比板块时间长，不需要同步时间轴
		NodeXPeriodData bknodexdata = bk.getNodeXPeroidData(period);
		LocalDate bkdatastartday = bknodexdata.getAmoRecordsStartDate();
		if(bkdatastartday != null  && bkdatastartday.isBefore(requiredstartday) )
			requiredstartday = bkdatastartday;

		LocalDate notcalwholewkmodedate = bknodexdata.isInNotCalWholeWeekMode();
		if(notcalwholewkmodedate == null) { //如果是计算整周， 则数据轴大小没关系，只要是整周都可以
			LocalDate bkdataendday = bknodexdata.getAmoRecordsEndDate();
			if(bkdataendday != null  && bkdataendday.isAfter(requiredendday) )
				requiredendday = bkdataendday;
		}	else { //如果是计算不完全周，那最后一周必须是要求计算的周
			requiredendday = notcalwholewkmodedate; 
//			calwholeweek = false;
		}

		if(bk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) {
//			int bkggbefore = 0;
//			try {	bkggbefore = bk.getAllGeGuOfBanKuaiInHistory().size();
//			} catch (java.lang.NullPointerException e) {}
			bk = this.getAllGeGuOfBanKuai (bk);
//			int bkggafter = 0;
//			try {	bkggafter = bk.getAllGeGuOfBanKuaiInHistory().size();
//			} catch (java.lang.NullPointerException e) {}
//			
//			if( bkggafter != bkggbefore ) { //说明板块个股有了变化，需要重新计算，否则无需再此计算，浪费时间,其实这里应该是计算板块的数据是否有变化，比较难，就暂时用这个方法来避免重复计算
//				bk = bkdbopt.getBanKuaiGeGuZhanBi (bk,requiredstartday,requiredendday,period);
//				bkdbopt.getTDXBanKuaiSetForBanKuaiGeGu (bk);
//				bkdbopt.getBanKuaiGeGuGzMrMcYkInfo (bk);
//				gddbopt.checkBanKuaiGeGuHasHuangQinGuoQieAndMingXin(bk, requiredendday); //查询板块个股是否有皇亲国戚
//			}

			List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
			if(allbkgg != null) {
				SvsForNodeOfStock svstock = new SvsForNodeOfStock ();
				for(BkChanYeLianTreeNode stockofbk : allbkgg)   
			    	if( ((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(requiredendday)  ) {
			    		Stock stock = ((StockOfBanKuai)stockofbk).getStock();
//			    		if(!calwholeweek) {
//			    			stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).setNotCalWholeWeekMode (notcalwholewkmodedate);
//			    			stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).setNotCalWholeWeekMode (notcalwholewkmodedate);
//			    		}
			    		stock = (Stock) svstock.getNodeData( stock, requiredstartday, requiredendday, period,calwholeweek); 
		    			svstock.syncNodeData(stock);
			    	 }
			}
		}
		
		return;
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
			
			bankuai = dzhbkdbopt.getDZHBanKuaiGeGuOfHyGnFg (bankuai,bkstartday,bkendday);
			bankuai.setBanKuaiGeGuTimeRange (bkstartday,bkendday);
			return bankuai;
		} 
		
		Interval alreadyinggtimerange = bankuai.getBanKuaiGeGuTimeRange();
		LocalDate alreadystartday; LocalDate alreadyendday ;
		if(alreadyinggtimerange == null) {
			alreadystartday = bkstartday;
			alreadyendday = bkendday;
			
			bankuai = dzhbkdbopt.getDZHBanKuaiGeGuOfHyGnFg (bankuai,alreadystartday,alreadyendday);
			bankuai.setBanKuaiGeGuTimeRange (bkstartday,bkendday);
			return bankuai;
		} 
		
		DateTime tmpnewstartdt = alreadyinggtimerange.getStart();
		DateTime tmpnewenddt = alreadyinggtimerange.getEnd();
		alreadystartday = LocalDate.of(tmpnewstartdt.getYear(), tmpnewstartdt.getMonthOfYear(), tmpnewstartdt.getDayOfMonth());
		alreadyendday = LocalDate.of(tmpnewenddt.getYear(), tmpnewenddt.getMonthOfYear(), tmpnewenddt.getDayOfMonth());

		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval(bkstartday,bkendday,alreadystartday,alreadyendday);
		if(timeintervallist == null)
			return bankuai;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				LocalDate requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				LocalDate requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				bankuai = dzhbkdbopt.getDZHBanKuaiGeGuOfHyGnFg (bankuai,requiredstartday,requiredendday);
		}

		return bankuai;
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
	public BkChanYeLianTreeNode getNodeData(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period, Boolean calwholeweek) 
	{
		requiredstartday = requiredstartday.with(DayOfWeek.MONDAY);
		requiredendday = this.getNodeCaculateEndDateAndRelatedActions(bankuai, period, requiredendday, calwholeweek);
		
		LocalDate jlmaxdate = ((BanKuai)bankuai).getShuJuJiLuInfo().getJyjlmaxdate();
		LocalDate jlmindate = ((BanKuai)bankuai).getShuJuJiLuInfo().getJyjlmindate();
		if(jlmaxdate == null && jlmindate == null)
			return bankuai; //bankuai without data records should not be displayed
		
		if(requiredendday.isBefore(jlmindate))
			return bankuai;
		if(requiredstartday.isBefore(jlmindate) )
			requiredstartday = jlmindate;
		
		NodeXPeriodData nodedayperioddata = ((BanKuai)bankuai).getNodeXPeroidData(period);
		if(nodedayperioddata.getAmoRecordsStartDate() == null) {
			bankuai = tdxbkdbopt.getBanKuaiZhanBi ((BanKuai)bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		}
		
		List<Interval> timeintervallist;
		if(calwholeweek)
			timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getAmoRecordsStartDate(),nodedayperioddata.getAmoRecordsEndDate());
		else
			timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
			(requiredstartday,requiredendday,nodedayperioddata.getAmoRecordsStartDate(),requiredendday.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY)	);  //必须要减少一周，这样才能计算出新数据
		
		if(timeintervallist == null) 
			return bankuai;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				LocalDate tmprequiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth());
				LocalDate tmprequiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				
				bankuai = tdxbkdbopt.getBanKuaiZhanBi ((BanKuai) bankuai,tmprequiredstartday,tmprequiredendday,period);
		}
		return bankuai;
	}
	private LocalDate getNodeCaculateEndDateAndRelatedActions(BkChanYeLianTreeNode bankuai, String period, LocalDate requiredendday, Boolean calwholeweek)
	{
		LocalDate result = null;
		
		DayOfWeek dayofweek = requiredendday.getDayOfWeek();
		if(dayofweek.equals(DayOfWeek.SUNDAY) )	requiredendday = requiredendday.minus(2,ChronoUnit.DAYS);
		else if(dayofweek.equals(DayOfWeek.SATURDAY) )	requiredendday = requiredendday.minus(1,ChronoUnit.DAYS);
		
		NodeXPeriodData nodeperioddata = ((BanKuai)bankuai).getNodeXPeroidData(period);
		NodeXPeriodData nodexdataday = ((BanKuai)bankuai).getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
		
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
			if(amorecordsenddate != null) {
				nodeperioddata.removeNodeDataFromSpecificDate(amorecordsenddate);
				
				nodexdataday.removeNodeDataFromSpecificDate(amorecordsenddate.with(DayOfWeek.MONDAY)); //那一周的数据都不要了
				nodeperioddata.setNotCalWholeWeekMode (null);
				nodexdataday.setNotCalWholeWeekMode (null);
			}
			
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
	public BkChanYeLianTreeNode getNodeKXian(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period, Boolean calwholeweek) 
	{
		requiredstartday = requiredstartday.with(DayOfWeek.MONDAY);
		
		NodeXPeriodData nodedayperioddata = ((BanKuai) bankuai).getNodeXPeroidData(period);
		if(nodedayperioddata.isInNotCalWholeWeekMode() == null || calwholeweek) 
			requiredendday   = requiredendday.with(DayOfWeek.FRIDAY);

		if(nodedayperioddata.getOHLCRecordsStartDate() == null) {
			bankuai = tdxbkdbopt.getBanKuaiKXianZouShi (((BanKuai) bankuai),requiredstartday,requiredendday,period);
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
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());//.with(DayOfWeek.FRIDAY);
				bankuai = tdxbkdbopt.getBanKuaiKXianZouShi (((BanKuai) bankuai),requiredstartday,requiredendday,period);
		}
		
		return bankuai;
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
	public void syncNodeData(BkChanYeLianTreeNode bankuai) 
	{
		BanKuai bk = (BanKuai)bankuai;
		
		LocalDate bkohlcstartday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).getOHLCRecordsStartDate();
		LocalDate bkohlcendday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).getOHLCRecordsEndDate();
		
		NodeXPeriodData nodexdatawk = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		LocalDate bkamostartday =	nodexdatawk.getAmoRecordsStartDate();
		LocalDate notcalwholewkmodedate = nodexdatawk.isInNotCalWholeWeekMode() ;
		LocalDate bkamoendday;	Boolean calwholeweek;
		if(notcalwholewkmodedate == null) {
			bkamoendday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getAmoRecordsEndDate();
			 calwholeweek = true;
		}
		else { bkamoendday = notcalwholewkmodedate;  calwholeweek = false; }
		
		if(bkohlcstartday == null && bkamostartday == null) //都为空，
			return ;
		
		if(bkohlcstartday == null) {
			bkohlcstartday = bkamostartday;
			bkohlcendday = bkamoendday;
			bankuai = this.getNodeKXian(bankuai, bkohlcstartday,bkohlcendday, NodeGivenPeriodDataItem.DAY,calwholeweek);
			
			return;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(bkamostartday,bkamoendday,bkohlcstartday,bkohlcendday );
		
		if(timeintervallist == null)
			return ;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				LocalDate requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				LocalDate requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());//.with(DayOfWeek.FRIDAY);
				
				bankuai = this.getNodeKXian(bankuai, requiredstartday,requiredendday, NodeGivenPeriodDataItem.DAY,calwholeweek);
		}
		
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
	public StockOfBanKuai getGeGuOfBanKuaiData(BanKuai bankuai, StockOfBanKuai stockofbk, String period) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public StockOfBanKuai getGeGuOfBanKuaiData(BanKuai bankuai, String stockcode, String period) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public StockOfBanKuai getGeGuOfBanKuaiData(String bkcode, String stockcode, String period) {
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
	
	public BkChanYeLianTreeNode updateBanKuaiBasicOperationsSettings(BkChanYeLianTreeNode node, boolean importdailydata, boolean exporttogephi, 
			boolean showinbkfx,boolean showincyltree, boolean exporttowkfile, boolean importbkgg, Color bkcolor,  boolean corezhishu)
	{
		dzhbkdbopt.updateBanKuaiOperationsSettings (node,importdailydata,exporttogephi,showinbkfx,showincyltree,exporttowkfile,importbkgg,bkcolor,corezhishu);
		((BanKuai)node).getBanKuaiOperationSetting().setImportdailytradingdata(importdailydata);
		((BanKuai)node).getBanKuaiOperationSetting().setExporttogehpi(exporttogephi);
		((BanKuai)node).getBanKuaiOperationSetting().setShowinbkfxgui(showinbkfx);
		((BanKuai)node).getBanKuaiOperationSetting().setShowincyltree(showincyltree);
		((BanKuai)node).getBanKuaiOperationSetting().setExportTowWlyFile(exporttowkfile);
		((BanKuai)node).getBanKuaiOperationSetting().setImportBKGeGu(importbkgg);
		((BanKuai)node).getBanKuaiOperationSetting().setBanKuaiLabelColor (bkcolor);
		((BanKuai)node).getNodeJiBenMian().setIsCoreZhiShu(corezhishu);
		
		return node;
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
	public void forcedeleteBanKuaiImportedDailyExchangeData(BanKuai bk) {
		dzhbkdbopt.forcedeleteBanKuaiImportedDailyExchangeData (bk);
		
	}

	@Override
	public void forcedeleteBanKuaiImportedGeGuData(BanKuai bk) {
		dzhbkdbopt.forcedeleteBanKuaiImportedGeGuData (bk);
	}

}
