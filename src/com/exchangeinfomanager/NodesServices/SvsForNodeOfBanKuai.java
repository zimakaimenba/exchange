package com.exchangeinfomanager.NodesServices;

import java.awt.Color;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.Trees.TreeOfChanYeLian;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.CylTreeDbOperation;
import com.exchangeinfomanager.database.JiGouGuDongDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

public class SvsForNodeOfBanKuai implements ServicesForNode, ServicesForNodeBanKuai
{
	private BanKuaiDbOperation bkdbopt;
	private CylTreeDbOperation dboptforcyltree;
	private JiGouGuDongDbOperation gddbopt;

	public SvsForNodeOfBanKuai ()
	{
		this.bkdbopt = new BanKuaiDbOperation ();
		gddbopt = new JiGouGuDongDbOperation ();
	}

	@Override
	public BkChanYeLianTreeNode getNodeJiBenMian(BkChanYeLianTreeNode node) 
	{
		node = bkdbopt.getBanKuaiBasicInfo ((BanKuai) node);
		return node;
	}
	private LocalDate getNodeCaculateEndDateAndRelatedActions(BkChanYeLianTreeNode bankuai, String period, LocalDate requiredendday, Boolean calwholeweek)
	{
		LocalDate result = null;
		
		DayOfWeek dayofweek = requiredendday.getDayOfWeek();
		if(dayofweek.equals(DayOfWeek.SUNDAY) ) 
			requiredendday = requiredendday.minus(2,ChronoUnit.DAYS);
		else if(dayofweek.equals(DayOfWeek.SATURDAY) ) 
			requiredendday = requiredendday.minus(1,ChronoUnit.DAYS);
		
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
				nodeperioddata.removeNodeDataFromSpecificDate(amorecordsenddate, 0);
				
				nodexdataday.removeNodeDataFromSpecificDate(amorecordsenddate.with(DayOfWeek.MONDAY), 0); //那一周的数据都不要了
				nodeperioddata.setNotCalWholeWeekMode (null);
				nodexdataday.setNotCalWholeWeekMode (null);
			}
			
			result  = requiredendday.with(DayOfWeek.FRIDAY);
			return result; 
		}
		
		if(!calwholeweek && !origmode) {  //NCW TO NCW
			LocalDate amorecordsenddate = nodeperioddata.getAmoRecordsEndDate();
			if(amorecordsenddate != null && amorecordsenddate.isBefore(requiredendday))
				nodeperioddata.removeNodeDataFromSpecificDate(amorecordsenddate, 0);
			else if(amorecordsenddate != null && amorecordsenddate.isAfter(requiredendday))
				nodeperioddata.removeNodeDataFromSpecificDate(requiredendday, 0);
			
			LocalDate ohlcrecordsenddate = nodexdataday.getOHLCRecordsEndDate();
			if(ohlcrecordsenddate != null && ohlcrecordsenddate.isBefore(requiredendday))
				nodexdataday.removeNodeDataFromSpecificDate(ohlcrecordsenddate, 0);
			else if(ohlcrecordsenddate != null && ohlcrecordsenddate.isAfter(requiredendday))
				nodexdataday.removeNodeDataFromSpecificDate(requiredendday, 0);
			
			nodeperioddata.setNotCalWholeWeekMode (requiredendday);
			nodexdataday.setNotCalWholeWeekMode (requiredendday);
			result  = requiredendday;
			return result;
		}
		
		if(!calwholeweek && origmode) {  //CW TO NCW
			LocalDate amorecordsenddate = nodeperioddata.getAmoRecordsEndDate();
			if(amorecordsenddate != null && amorecordsenddate.isAfter(requiredendday))
				nodeperioddata.removeNodeDataFromSpecificDate(requiredendday, 0);
			
			LocalDate ohlcrecordsenddate = nodexdataday.getOHLCRecordsEndDate();
			if(ohlcrecordsenddate != null && ohlcrecordsenddate.isAfter(requiredendday))
				nodexdataday.removeNodeDataFromSpecificDate(requiredendday, 0);
			
			nodeperioddata.setNotCalWholeWeekMode (requiredendday);
			nodexdataday.setNotCalWholeWeekMode (requiredendday);
			result  = requiredendday;
			return result;
		}
		
		return null;
	}
	@Override
	public BkChanYeLianTreeNode getNodeData(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period, Boolean calwholeweek) 
	{
		requiredstartday = requiredstartday.with(DayOfWeek.MONDAY);
		requiredendday = this.getNodeCaculateEndDateAndRelatedActions(bankuai, period, requiredendday, calwholeweek);
		
		NodeXPeriodData nodedayperioddata = ((BanKuai)bankuai).getNodeXPeroidData(period);
		if(nodedayperioddata.getAmoRecordsStartDate() == null) {
			bankuai = bkdbopt.getBanKuaiZhanBi ((BanKuai)bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getAmoRecordsStartDate(),nodedayperioddata.getAmoRecordsEndDate());
		if(timeintervallist == null) 
			return bankuai;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				LocalDate tmprequiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth());
				LocalDate tmprequiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				
				bankuai = bkdbopt.getBanKuaiZhanBi ((BanKuai) bankuai,tmprequiredstartday,tmprequiredendday,period);
		}
		return bankuai;
	}
	
//	private BkChanYeLianTreeNode getNodeDataInNotCalWholeWeekMode(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
//			LocalDate requiredendday, String period, Boolean calwholeweek) 
//	{
//		requiredstartday = requiredstartday.with(DayOfWeek.MONDAY);
//		
//		NodeXPeriodData nodedayperioddata = ((BanKuai)bankuai).getNodeXPeroidData(period);
//		nodedayperioddata.setNotCalWholeWeekMode (requiredendday);
//		NodeXPeriodData nodexdataday = ((BanKuai)bankuai).getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
//		nodexdataday.setNotCalWholeWeekMode (requiredendday);
//		
//		if(nodedayperioddata.getAmoRecordsStartDate() == null) {
//			bankuai = bkdbopt.getBanKuaiZhanBi ((BanKuai)bankuai,requiredstartday,requiredendday,period);
//			return bankuai;
//		}
//		
//		LocalDate amorecordsenddate = nodedayperioddata.getAmoRecordsEndDate();
//		if(amorecordsenddate.isBefore(requiredendday))
//			nodedayperioddata.removeNodeDataFromSpecificDate(amorecordsenddate, 0);
//		else if(amorecordsenddate.isAfter(requiredendday))
//			nodedayperioddata.removeNodeDataFromSpecificDate(requiredendday, 0);
//		else
//			nodedayperioddata.removeNodeDataFromSpecificDate(requiredendday, 0);
//		
//		LocalDate ohlcrecordsenddate = nodexdataday.getOHLCRecordsEndDate();
//		if(ohlcrecordsenddate != null && ohlcrecordsenddate.isBefore(requiredendday))
//			nodexdataday.removeNodeDataFromSpecificDate(ohlcrecordsenddate, 0);
//		else if(ohlcrecordsenddate != null && ohlcrecordsenddate.isAfter(requiredendday))
//			nodexdataday.removeNodeDataFromSpecificDate(requiredendday, 0);
//		else if(ohlcrecordsenddate != null)
//			nodexdataday.removeNodeDataFromSpecificDate(requiredendday, 0);
//		
//		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
//				(requiredstartday,requiredendday,nodedayperioddata.getAmoRecordsStartDate(),nodedayperioddata.getAmoRecordsEndDate());
//		if(timeintervallist == null) 
//			return bankuai;
//		
//		for(Interval tmpinterval : timeintervallist) {
//				DateTime newstartdt = tmpinterval.getStart();
//				DateTime newenddt = tmpinterval.getEnd();
//				
//				LocalDate tmprequiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth());
//				LocalDate tmprequiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
//				
//				bankuai = bkdbopt.getBanKuaiZhanBi ((BanKuai) bankuai,tmprequiredstartday,tmprequiredendday,period);
//		}
//		return bankuai;
//	}

	@Override
	public BkChanYeLianTreeNode getNodeData(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period, Boolean calwholeweek) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period,Boolean calwholeweek) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period,Boolean calwholeweek) 
	{
		if(!calwholeweek) {} //目前不需要
		
		requiredstartday = requiredstartday.with(DayOfWeek.MONDAY);
		
		NodeXPeriodData nodedayperioddata = ((BanKuai) bankuai).getNodeXPeroidData(period);
		if(nodedayperioddata.isInNotCalWholeWeekMode() == null) 
			requiredendday   = requiredendday.with(DayOfWeek.FRIDAY);
		else { //在getnodedata里面已经把日线OHLC删除了，最好在这里再保证一下
		}

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
//	public BkChanYeLianTreeNode getNodeKXianInNotCalWholeWeekMode(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
//			LocalDate requiredendday, String period,Boolean calwholeweek) 
//	{
//		requiredstartday = requiredstartday.with(DayOfWeek.MONDAY);
//		
//		NodeXPeriodData nodedayperioddata = ((BanKuai)bankuai).getNodeXPeroidData(period);
//		nodedayperioddata.setNotCalWholeWeekMode (requiredendday);
//		((BanKuai)bankuai).getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).setNotCalWholeWeekMode (requiredendday);
//		
//		if(nodedayperioddata.getOHLCRecordsStartDate() == null) {
//			bankuai = bkdbopt.getBanKuaiKXianZouShi (((BanKuai) bankuai),requiredstartday,requiredendday,period);
//			return bankuai;
//		}
//		
//		LocalDate ohlcrecordsenddate = nodedayperioddata.getOHLCRecordsEndDate();
//		if(ohlcrecordsenddate.isBefore(requiredendday))
//			nodedayperioddata.removeNodeDataFromSpecificDate(ohlcrecordsenddate, 0);
//		else if(ohlcrecordsenddate.isAfter(requiredendday))
//			nodedayperioddata.removeNodeDataFromSpecificDate(requiredendday, 0);
//		else
//			nodedayperioddata.removeNodeDataFromSpecificDate(requiredendday, 0);
//		
//		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
//				(requiredstartday,requiredendday,nodedayperioddata.getOHLCRecordsStartDate(),nodedayperioddata.getOHLCRecordsEndDate() );
//		if(timeintervallist == null)
//			return bankuai;
//		
//		for(Interval tmpinterval : timeintervallist) {
//				DateTime newstartdt = tmpinterval.getStart();
//				DateTime newenddt = tmpinterval.getEnd();
//				
//				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
//				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
//				bankuai = bkdbopt.getBanKuaiKXianZouShi (((BanKuai) bankuai),requiredstartday,requiredendday,period);
//		}
//		
//		return bankuai;
//	}
	@Override
	public BkChanYeLianTreeNode getNodeZhangDieTingInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) 
	{
		if(bankuai.getMyOwnCode().equals("999999") ) {
			this.getShangZhengZhangDieTingInfo (bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		}
		else
		if(bankuai.getMyOwnCode().equals("399001") ) {
			this.getShenZhenZhangDieTingInfo (bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		}
		else
		if(bankuai.getMyOwnCode().equals("399006") ) {
			this.getChuangYeBanZhangDieTingInfo (bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		}
		
		BanKuai bk = (BanKuai) bankuai;
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
		if(allbkgg == null)
			return bk;
		
		LocalDate tmpdate = requiredstartday;
		do {
			 Integer zhangtingnum = 0;
			 Integer dietingnum = 0;
			 
			for(BkChanYeLianTreeNode stockofbk : allbkgg)   {
				if( !((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(tmpdate)  )  //确锟较碉拷前锟斤拷锟节帮拷锟斤拷锟?
					continue;

				Stock stock = ((StockOfBanKuai)stockofbk).getStock();
				NodeXPeriodData stockxdate = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
				Integer stockzt = stockxdate .getZhangTingTongJi (tmpdate,0);
				if(stockzt != null)
					zhangtingnum = zhangtingnum + stockzt;
				
				Integer stockdt =  stockxdate .getDieTingTongJi(tmpdate, 0);
				if(stockdt != null)
					dietingnum = dietingnum + stockdt;
			}
			
			if(zhangtingnum == 0)
				zhangtingnum = null;
			if(dietingnum == 0)
				dietingnum = null;
			
			bkwkdate.addZhangDieTingTongJiJieGuo(tmpdate, zhangtingnum, dietingnum, false);
			
			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
					tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
					tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requiredendday) || tmpdate.isEqual(requiredendday));

		return bk;
	
	}

	private BkChanYeLianTreeNode getChuangYeBanZhangDieTingInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) 
	{
		BanKuai bk = (BanKuai) bankuai;
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		
		bk = this.bkdbopt.getChuangYeBanZhangDieTingInfo (bk,  requiredstartday, requiredendday,  period);
		return bankuai;
	}
	private BkChanYeLianTreeNode getShenZhenZhangDieTingInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) {
		BanKuai bk = (BanKuai) bankuai;
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		
		bk = this.bkdbopt.getShenZhenZhangDieTingInfo (bk,  requiredstartday, requiredendday,  period);
		return bankuai;
	}
	private BkChanYeLianTreeNode getShangZhengZhangDieTingInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) 
	{
		BanKuai bk = (BanKuai) bankuai;
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		
		bk = this.bkdbopt.getShangZhengZhangDieTingInfo (bk,  requiredstartday, requiredendday,  period);
		
		return bankuai;
	}
	private BkChanYeLianTreeNode getKeChuangBanZhangDieTingInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) 
	{
		
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
			LocalDate requiredendday, String period) 
	{
		BanKuai bk = (BanKuai) bankuai;
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
		if(allbkgg == null)
			return bankuai;
		
		LocalDate tmpdate = requiredstartday;
		do {
			 Integer openup = 0;
			 Integer opendown = 0;
			 Integer huibuup = 0;
			 Integer huibudown = 0;
			 
			for(BkChanYeLianTreeNode stockofbk : allbkgg)   {
				if( ! ((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(tmpdate)  )  //确锟较碉拷前锟斤拷锟节帮拷锟斤拷锟?
					continue;
				
				Stock stock = ((StockOfBanKuai)stockofbk).getStock();
				NodeXPeriodData stockxdate = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
				Integer stockopenup = stockxdate.getQueKouTongJiOpenUp (tmpdate,0);
				if(stockopenup != null && stockopenup != 0)
					openup = openup + stockopenup;
				Integer stockopendown = stockxdate.getQueKouTongJiOpenDown(tmpdate, 0);
				if(stockopendown != null && stockopendown !=0 )
					opendown = opendown + stockopendown;
				Integer stockhbdown =  stockxdate.getQueKouTongJiHuiBuDown(tmpdate, 0);
				if(stockhbdown != null && stockhbdown !=0)
					huibudown = huibudown + stockhbdown;
				Integer stockhbup =stockxdate.getQueKouTongJiHuiBuUp(tmpdate, 0);
				if(stockhbup != null && stockhbup  != 0)
					huibuup =  huibuup + stockhbup;
			}
			
			if(openup == 0)
				openup = null;
			if(huibuup == 0)
				huibuup = null;
			if(opendown == 0)
				opendown = null;
			if(huibudown == 0)
				huibudown = null;
			bkwkdate.addQueKouTongJiJieGuo(tmpdate, openup, huibuup, opendown, huibudown, false);
			
			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
					tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
					tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requiredendday) || tmpdate.isEqual(requiredendday));

		return bk;
	
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
		LocalDate bkamoendday;
		if(notcalwholewkmodedate == null)
			bkamoendday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getAmoRecordsEndDate();
		else bkamoendday = notcalwholewkmodedate;
		
		if(bkohlcstartday == null && bkamostartday == null) //都为空，
			return ;
		
		if(bkohlcstartday == null) {
			bkohlcstartday = bkamostartday;
			bkohlcendday = bkamoendday;
			bankuai = this.getNodeKXian(bankuai, bkohlcstartday,bkohlcendday, NodeGivenPeriodDataItem.DAY,true);
//			this.getNodeQueKouInfo(bk, bkohlcstartday, bkohlcendday, NodeGivenPeriodDataItem.WEEK);
//			this.getNodeZhangDieTingInfo(bk, bkohlcstartday, bkohlcendday, NodeGivenPeriodDataItem.WEEK);
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
				
				bankuai = this.getNodeKXian(bankuai, requiredstartday,requiredendday, NodeGivenPeriodDataItem.DAY,true);
		}
	}
	
	private List<Interval> getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
	(LocalDate requiredstartday,LocalDate requiredendday,LocalDate nodestart,LocalDate nodeend)
	{
		if(nodestart == null)	return null;
		
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
		}
		if(requiredinterval.abuts(nodeinterval)) {
			result.add(requiredinterval);
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
			
			bankuai = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (bankuai,bkstartday,bkendday);
			bankuai.setBanKuaiGeGuTimeRange (bkstartday,bkendday);
			return bankuai;
		} 
		
		Interval alreadyinggtimerange = bankuai.getBanKuaiGeGuTimeRange();
		LocalDate alreadystartday; LocalDate alreadyendday ;
		if(alreadyinggtimerange == null) {
			alreadystartday = bkstartday;
			alreadyendday = bkendday;
			
			bankuai = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (bankuai,alreadystartday,alreadyendday);
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
				
				bankuai = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (bankuai,requiredstartday,requiredendday);
		}

		return bankuai;
	}
	public StockOfBanKuai getGeGuOfBanKuaiData(BanKuai bankuai, StockOfBanKuai stockofbk,String period)
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getOHLCRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getOHLCRecordsEndDate();
		
		if(bkstartday == null && bkendday == null) 
			return stockofbk;
		
		stockofbk = bkdbopt.getGeGuZhanBiOfBanKuai (bankuai,stockofbk, bkstartday, bkendday,period);

		return stockofbk;
	}
	/*
	 * 
	 */
	public StockOfBanKuai getGeGuOfBanKuaiData(BanKuai bankuai, String stockcode,String period)
	{
		StockOfBanKuai stock = bankuai.getStockOfBanKuai(stockcode);
		if(stock == null)
			return null;
		
		stock = this.getGeGuOfBanKuaiData( bankuai,  stock,period);
		return (StockOfBanKuai) stock;
	}
	public StockOfBanKuai getGeGuOfBanKuaiData(String bkcode, String stockcode,String period) 
	{
		BanKuai bankuai = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		if(bankuai == null)
			return null;
		
		StockOfBanKuai stock = bankuai.getStockOfBanKuai(stockcode);
		if(stock == null)
			return null;
		
		stock = this.getGeGuOfBanKuaiData( bankuai,  stock,period);

		return stock;
	}
	/*
	 * 
	 */
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
			calwholeweek = false;
		}

		if(bk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) {
			int bkggbefore = 0;
			try {	bkggbefore = bk.getAllGeGuOfBanKuaiInHistory().size();
			} catch (java.lang.NullPointerException e) {}
			bk = this.getAllGeGuOfBanKuai (bk);
			int bkggafter = 0;
			try {	bkggafter = bk.getAllGeGuOfBanKuaiInHistory().size();
			} catch (java.lang.NullPointerException e) {}
			
			if( bkggafter != bkggbefore ) { //说明板块个股有了变化，需要重新计算，否则无需再此计算，浪费时间,其实这里应该是计算板块的数据是否有变化，单比较难，就暂时用这个方法来避免重复计算
				bk = bkdbopt.getBanKuaiGeGuZhanBi (bk,requiredstartday,requiredendday,period);
				bkdbopt.getTDXBanKuaiSetForBanKuaiGeGu (bk);
				bkdbopt.getBanKuaiGeGuGzMrMcYkInfo (bk);
				gddbopt.checkBanKuaiGeGuHasHuangQinGuoQieAndMingXin(bk, requiredendday); //查询板块个股是否有皇亲国戚
			}

			List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
			if(allbkgg != null) {
				SvsForNodeOfStock svstock = new SvsForNodeOfStock ();
				for(BkChanYeLianTreeNode stockofbk : allbkgg)   
			    	if( ((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(requiredendday)  ) {
			    		Stock stock = ((StockOfBanKuai)stockofbk).getStock();
			    		if(!calwholeweek) {
			    			stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).setNotCalWholeWeekMode (notcalwholewkmodedate);
			    			stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).setNotCalWholeWeekMode (notcalwholewkmodedate);
			    		}
//			    		Stock stock = (Stock) svstock.getNodeData( ((StockOfBanKuai)stockofbk).getStock(), requiredstartday, requiredendday, period,calwholeweek); //数据在前面已经获取了
		    			svstock.syncNodeData(stock);
			    	 }
			}

			if(calwholeweek ) { 
				this.getNodeQueKouInfo(bk, requiredstartday, requiredendday, period);
				this.getNodeZhangDieTingInfo(bk, requiredstartday, requiredendday, period);
			}
		} else
		if(bk.getMyOwnCode().equals("999999")) 
			this.getNodeZhangDieTingInfo(bk, requiredstartday, requiredendday, period);
		
		return;
	}
	@Override
	public List<BkChanYeLianTreeNode> getNodeChanYeLianInfo(String nodecode) 
	{
		if(dboptforcyltree == null)
			dboptforcyltree = new CylTreeDbOperation (CreateExchangeTree.CreateTreeOfChanYeLian());
		
		List<BkChanYeLianTreeNode> cylinfo = this.dboptforcyltree.getChanYeLianInfo (nodecode,BkChanYeLianTreeNode.TDXBK);
		return cylinfo;
	}

	@Override
	public List<BkChanYeLianTreeNode> getNodeSlidingInChanYeLianInfo(String nodecode) 
	{
		if(dboptforcyltree == null)
			dboptforcyltree = new CylTreeDbOperation (CreateExchangeTree.CreateTreeOfChanYeLian());
		
		List<BkChanYeLianTreeNode> cylsliding = this.dboptforcyltree.getSlidingInChanYeLianInfo (nodecode,BkChanYeLianTreeNode.TDXBK);
		return cylsliding;
	}

	@Override
	public List<BkChanYeLianTreeNode> getNodeChildrenInChanYeLianInfo(String nodecode) 
	{
		if(dboptforcyltree == null)
		dboptforcyltree = new CylTreeDbOperation (CreateExchangeTree.CreateTreeOfChanYeLian());
		
		List<BkChanYeLianTreeNode> cylchildren  = this.dboptforcyltree.getChildrenInChanYeLianInfo (nodecode,BkChanYeLianTreeNode.TDXBK);
		return cylchildren;
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
		((BanKuai)node).getNodeJiBenMian().setNodeCjeZhanbiLevel (min,max);
		
	}

	@Override
	public void setNodeCjlExtremeUpDownZhanbiLevel(BkChanYeLianTreeNode node, Double min, Double max) {
		node = this.bkdbopt.setNodeCjlExtremeZhanbiUpDownLevel( (TDXNodes)node, min, max);
		((BanKuai)node).getNodeJiBenMian().setNodeCjlZhanbiLevel (min,max);
	}
	
	public BkChanYeLianTreeNode updateBanKuaiBasicOperationsSettings(BkChanYeLianTreeNode node, boolean importdailydata, boolean exporttogephi, 
			boolean showinbkfx,boolean showincyltree, boolean exporttowkfile, boolean importbkgg,Color bkcolor,boolean corezhishu)
	{
		bkdbopt.updateBanKuaiOperationsSettings (node,importdailydata,exporttogephi,showinbkfx,showincyltree,exporttowkfile,importbkgg,bkcolor,corezhishu);
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
	public Collection<BkChanYeLianTreeNode> getNodesWithKeyWords(String keywords)
	{
		Collection<BkChanYeLianTreeNode> result = this.bkdbopt.searchKeyWordsInBanKuaiJiBenMian (keywords);
		return result;
	}

	@Override
	public BkChanYeLianTreeNode getNodeGzMrMcYkInfo(BkChanYeLianTreeNode node, LocalDate selecteddatestart,
			LocalDate selecteddateend, String period) {
		node =  bkdbopt.getNodeGzMrMcYkInfo((TDXNodes)node, selecteddatestart,selecteddateend, period);
		return node;
	}

	@Override
	public void forcedeleteBanKuaiImportedDailyExchangeData(BanKuai bk) {
		bkdbopt.forcedeleteBanKuaiImportedDailyExchangeData (bk);
		
	}

	@Override
	public void forcedeleteBanKuaiImportedGeGuData(BanKuai bk) {
		bkdbopt.forcedeleteBanKuaiImportedGeGuData (bk);
	}
	
}
