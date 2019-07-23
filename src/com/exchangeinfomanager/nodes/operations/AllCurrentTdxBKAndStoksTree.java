package com.exchangeinfomanager.nodes.operations;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodesXPeriodData;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
/*
 * 
 */
public class AllCurrentTdxBKAndStoksTree 
{
	private AllCurrentTdxBKAndStoksTree ()
	{
		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
		
		initializeAllStocksTree ();
		setupDaPan ();
	}
	
	 // 单例实现  
	 public static AllCurrentTdxBKAndStoksTree getInstance ()
	 {  
	        return Singtonle.instance;  
	 }
	 
	 private static class Singtonle 
	 {  
	        private static AllCurrentTdxBKAndStoksTree instance =  new AllCurrentTdxBKAndStoksTree ();  
	 }
	
	
	private static Logger logger = Logger.getLogger(AllCurrentTdxBKAndStoksTree.class);
	
	private BanKuaiDbOperation bkdbopt;
	private BanKuaiAndStockTree treecyl;
	private SystemConfigration sysconfig;

	
	
	private void initializeAllStocksTree() 
	{
		
		DaPan alltopNode = new DaPan("000000","两交易所");
		
		ArrayList<BanKuai> allbkandzs = bkdbopt.getTDXBanKuaiList ("all");
		for (BanKuai bankuai : allbkandzs ) {
		    alltopNode.add(bankuai);
//		    logger.debug(bankuai.getMyOwnCode()+ "类型" + bankuai.getType());
		}
		
		ArrayList<Stock> allstocks = bkdbopt.getAllStocks ();
		for (Stock stock : allstocks ) {
		    alltopNode.add(stock);

		}

		treecyl = new BanKuaiAndStockTree(alltopNode,"ALLBKSTOCKS");
		allstocks = null;
	}
	
	public  BanKuaiAndStockTree getAllBkStocksTree()
	{
		return treecyl;
	}
	/*
	 * 
	 */
	public void setupDaPan ()
	{
		DaPan treeallstockrootdapan = (DaPan)treecyl.getModel().getRoot();
		BanKuai shdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		BanKuai szdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.TDXBK);
		treeallstockrootdapan.setDaPanContents(shdpbankuai,szdpbankuai);
	}
	/*
	 * 获得板块，并同步上证/深圳指数的成交量，以保住板块和上证/深圳的成交量记录日期跨度是完全的。
	 */
	public BanKuai getBanKuai (BanKuai bankuai,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		NodeXPeriodDataBasic nodedayperioddata = bankuai.getNodeXPeroidData(period);
		if(nodedayperioddata.getAmoRecordsStartDate() == null) {
			bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getAmoRecordsStartDate(),nodedayperioddata.getAmoRecordsEndDate());
		if(timeintervallist == null) {
//			System.out.println("tet beging");
			return bankuai;
			
		}
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,requiredstartday,requiredendday,period);
		}
		return bankuai;
	}
	/*
	 * 
	 */
	public BanKuai getBanKuai (String bkcode,LocalDate requiredstartday,LocalDate requiredendday,String period) 
	{
		BanKuai bankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		if(bankuai == null)
			return null;
		
		bankuai = this.getBanKuai (bankuai,requiredstartday,requiredendday,period);
		return bankuai;
	}
		/*
	 *板块的K线走势， 直接从数据库中读取数据，和个股不一样
	 */
	public BanKuai getBanKuaiKXian (String bkcode,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		BanKuai bk = (BanKuai)this.treecyl.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		bk = (BanKuai) this.getBanKuaiKXian(bk, requiredstartday,requiredendday, period);
		return bk;
	}
	public BanKuai getBanKuaiKXian (BanKuai bk,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		NodeXPeriodDataBasic nodedayperioddata = bk.getNodeXPeroidData(period);
		if(nodedayperioddata.getKXxianRecordsStartDate() == null) {
			bk = bkdbopt.getBanKuaiKXianZouShi (bk,requiredstartday,requiredendday,period);
			return bk;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getKXxianRecordsStartDate(),nodedayperioddata.getKXxianRecordsEndDate());
		if(timeintervallist == null)
			return bk;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				bk = bkdbopt.getBanKuaiKXianZouShi (bk,requiredstartday,requiredendday,period);
		}
		return bk;
	}
	public BanKuai getBanKuaiQueKouInfo (String bkcode,LocalDate requiredrecordsday,LocalDate requiredendday,String period)
	{
		BanKuai bankuai = (BanKuai)this.treecyl.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		bankuai = (BanKuai) this.getBanKuaiQueKouInfo(bankuai, requiredrecordsday, requiredendday, period);
		return bankuai;
	}
	public BanKuai getBanKuaiQueKouInfo (BanKuai bk,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
//		bk = this.getAllGeGuOfBanKuai (bk,period); //获取所有曾经是该板块的个股
		NodeXPeriodDataBasic bkwkdate = bk.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK);
		ArrayList<StockOfBanKuai> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
		
		LocalDate tmpdate = requiredstartday;
		do {
			 Integer openup = 0;
			 Integer opendown = 0;
			 Integer huibuup = 0;
			 Integer huibudown = 0;
			 
			for(StockOfBanKuai stockofbk : allbkgg)   {
				if( !stockofbk.isInBanKuaiAtSpecificDate(tmpdate)  )  //确认当前还在板块内
					break;
				
				Stock stock = stockofbk.getStock();
				NodeXPeriodDataBasic stockxdate = stock.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK);
				Integer stockopenup = ( (TDXNodesXPeriodData)stockxdate ).getQueKouTongJiOpenUp (tmpdate,0);
				if(stockopenup != null)
					openup = openup + stockopenup;
				Integer stockopendown = ( (TDXNodesXPeriodData)stockxdate ).getQueKouTongJiOpenDown(tmpdate, 0);
				if(stockopendown != null)
					opendown = opendown + stockopendown;
				Integer stockhbdown =  ( (TDXNodesXPeriodData)stockxdate ).getQueKouTongJiHuiBuDown(tmpdate, 0);
				if(stockhbdown != null)
					huibudown = huibudown + stockhbdown;
				Integer stockhbup = ( (TDXNodesXPeriodData)stockxdate ).getQueKouTongJiHuiBuUp(tmpdate, 0);
				if(stockhbup != null)
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
			( (TDXNodesXPeriodData)bkwkdate ).addQueKouTongJiJieGuo(tmpdate, openup, huibuup, opendown, huibudown, false);
			
			if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(TDXNodeGivenPeriodDataItem.DAY))
					tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH))
					tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requiredendday) || tmpdate.isEqual(requiredendday));

		return bk;
	}
	public void syncBanKuaiData (BanKuai bk)
	{
		LocalDate bkstartday = bk.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getAmoRecordsStartDate();
		LocalDate bkendday = bk.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getAmoRecordsEndDate();
		
		this.getBanKuaiKXian(bk, bkstartday, bkendday, TDXNodeGivenPeriodDataItem.DAY);
//		this.getBanKuaiQueKouInfo(bk, bkstartday, bkendday, TDXNodeGivenPeriodDataItem.WEEK);
		
	}
	/*
	 * 只要是在整个时间周期内都曾经是该板块的个股，板块都会存入 
	 */
	public BanKuai getAllGeGuOfBanKuai (BanKuai bankuai,String period) 
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getAmoRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getAmoRecordsEndDate();
		
		if(bkstartday == null || bkendday == null)  {
			bkstartday = CommonUtility.getSettingRangeDate(LocalDate.now(), "middle");
			bkendday = LocalDate.now();
//			return bankuai;
		}
		
		//同步板块的个股
		bankuai = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (bankuai,bkstartday,bkendday,treecyl);
//		ArrayList<StockOfBanKuai> allbkgg = bankuai.getAllCurrentBanKuaiGeGu();
//		
//		for(StockOfBanKuai stockofbk : allbkgg)   {
//			
//    		  stockofbk = this.getGeGuOfBanKuai(bankuai, stockofbk,period );
////    		  Stock stock = (Stock)this.treecyl.getSpecificNodeByHypyOrCode(stockofbk.getMyOwnCode(), BkChanYeLianTreeNode.TDXGG);
//    		  Stock stock = this.getStock(stockofbk.getMyOwnCode(), bkstartday, bkendday, period);
//		}
		
		return bankuai;
	}
	public StockOfBanKuai getGeGuOfBanKuai(BanKuai bankuai, StockOfBanKuai stockofbk,String period)
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getAmoRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getAmoRecordsEndDate();
		
		if(bkstartday == null && bkendday == null) //板块本身没有数据，无法找出对应的个股的数据，所以原路返回
			return stockofbk;
		
		stockofbk = bkdbopt.getGeGuZhanBiOfBanKuai (bankuai,stockofbk, bkstartday, bkendday,period);

		return stockofbk;
	}
	/*
	 * 返回这个板块的某个个股的成交量,不做板块是否包含该个股的检查
	 */
	public StockOfBanKuai getGeGuOfBanKuai(BanKuai bankuai, String stockcode,String period)
	{
		StockOfBanKuai stock = bankuai.getBanKuaiGeGu(stockcode);
		if(stock == null)
			return null;
		
		stock = this.getGeGuOfBanKuai( bankuai,  stock,period);
		return stock;
	}
	public StockOfBanKuai getGeGuOfBanKuai(String bkcode, String stockcode,String period) 
	{
		BanKuai bankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		if(bankuai == null)
			return null;
		
		StockOfBanKuai stock = bankuai.getBanKuaiGeGu(stockcode);
		if(stock == null)
			return null;
		
		stock = this.getGeGuOfBanKuai( bankuai,  stock,period);

		return stock;
	}
	/*
	 * 
	 */
	public void getDaPanKXian (LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		BanKuai shdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		BanKuai szdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.TDXBK);
		BanKuai cybdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("399006",BkChanYeLianTreeNode.TDXBK);
		
		shdpbankuai = (BanKuai) this.getBanKuaiKXian(shdpbankuai, requiredstartday, requiredendday,period);
		szdpbankuai = (BanKuai) this.getBanKuaiKXian(szdpbankuai, requiredstartday, requiredendday,period);
		cybdpbankuai = (BanKuai) this.getBanKuaiKXian(cybdpbankuai, requiredstartday, requiredendday, period);
		
	}
	/*
	 * 同步大盘成交额
	 */
	public void getDaPan (LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		BanKuai shdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		BanKuai szdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.TDXBK);
		BanKuai cybdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("399006",BkChanYeLianTreeNode.TDXBK);
		
		shdpbankuai = bkdbopt.getBanKuaiZhanBi (shdpbankuai,requiredstartday,requiredendday,period);
		szdpbankuai = bkdbopt.getBanKuaiZhanBi (szdpbankuai,requiredstartday,requiredendday,period);
		cybdpbankuai = bkdbopt.getBanKuaiZhanBi (szdpbankuai,requiredstartday,requiredendday,period);
	}
	public void syncDaPanData() 
	{
		BanKuai shdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		LocalDate bkstartday = shdpbankuai.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getAmoRecordsStartDate();
		LocalDate bkendday = shdpbankuai.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getAmoRecordsEndDate();
		
		this.getDaPanKXian (bkstartday,bkendday,TDXNodeGivenPeriodDataItem.DAY);
		
	}
	/*
	 * 
	 */
	public Stock getStock (Stock stock,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		NodeXPeriodDataBasic nodedayperioddata = stock.getNodeXPeroidData(period);
		if(nodedayperioddata.getAmoRecordsStartDate() == null) {
			stock = bkdbopt.getStockZhanBi (stock,requiredstartday,requiredendday,period);
			return stock;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getAmoRecordsStartDate(),nodedayperioddata.getAmoRecordsEndDate());
		if(timeintervallist == null)
			return stock;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				stock = bkdbopt.getStockZhanBi (stock,requiredstartday,requiredendday,period);
		}
		return stock;
	}
	public Stock getStock (String stockcode,LocalDate requiredstartday,LocalDate requiredendday,String period) 
	{
		Stock stock = (Stock) treecyl.getSpecificNodeByHypyOrCode(stockcode,BkChanYeLianTreeNode.TDXGG);
		if(stock == null)
			return null;
		
		stock = this.getStock (stock,requiredstartday,requiredendday,period);
		return stock;
	}
	/*
	 * 
	 */
	public Stock getStockQueKouInfo (String stockcode,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		Stock stock = (Stock)this.treecyl.getSpecificNodeByHypyOrCode(stockcode,BkChanYeLianTreeNode.TDXGG);
		stock = this.getStockQueKouInfo(stock, requiredstartday, requiredendday, period);
		return stock;
	}
//	public Stock getStockQueKouInfo (Stock stock,LocalDate requiredstartday,LocalDate requiredendday,String period)
//	{
//		NodeXPeriodDataBasic nodedayperioddata = stock.getNodeXPeroidData(period);
//		if(nodedayperioddata.getQueKouRecordsStartDate() == null) {
//			stock = bkdbopt.getStockQueKouTimeRangTongJIResult (stock,requiredstartday,requiredendday,period);
//			return stock;
//		}
//		
//		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
//				(requiredstartday,requiredendday,nodedayperioddata.getQueKouRecordsStartDate(),nodedayperioddata.getQueKouRecordsEndDate());
//		if(timeintervallist == null)
//			return stock;
//		
//		for(Interval tmpinterval : timeintervallist) {
//				DateTime newstartdt = tmpinterval.getStart();
//				DateTime newenddt = tmpinterval.getEnd();
//				
//				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
//				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
//				
//				stock = bkdbopt.getStockQueKouTimeRangTongJIResult (stock,requiredstartday,requiredendday,period);
//		}
//		return stock;
//
//	}
	/*
	 * 
	 */
	public Stock getStockQueKouInfo (Stock stock,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
//		if(stock.getMyOwnCode().equals("002415"))
//			logger.debug("test begin");
		NodeXPeriodDataBasic stockxwkdate = stock.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK);
		LocalDate qkstartday = stockxwkdate.getQueKouRecordsStartDate();
		LocalDate qkendday = stockxwkdate.getQueKouRecordsEndDate();
		
		NodeXPeriodDataBasic nodedayperioddata = stock.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.DAY);
		if(qkstartday == null) {
			stock = bkdbopt.getStockDailyQueKouAnalysisResult (stock,requiredstartday,requiredendday,period);
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
					
					stock = bkdbopt.getStockDailyQueKouAnalysisResult (stock,requiredstartday,requiredendday,period);
			}
		}
		
		if(period.equals(TDXNodeGivenPeriodDataItem.DAY)) { //日线计算完了，周线给出统计数据

			List<QueKou> qklist = ( (TDXNodesXPeriodData)nodedayperioddata ).getPeriodQueKou();
			if(qklist == null || qklist.size() == 0)
				return stock;
			
			( (TDXNodesXPeriodData)stockxwkdate ).resetQueKouTongJiJieGuo ();
			
			LocalDate qkstartdate = qklist.get(0).getQueKouDate();
			LocalDate qkenddate = qklist.get(qklist.size() -1 ).getQueKouDate();
			LocalDate tmpdate = qkstartdate;
			int indexstart =0; Boolean readytojumpoutloop = false;
			do {
				
				for(int index=indexstart;index<qklist.size();index++) {
					QueKou tmpqk = qklist.get(index);
					LocalDate tmpqkdate = tmpqk.getQueKouDate();
					
					if(CommonUtility.isInSameWeek(tmpqkdate, tmpdate)) {
						if(tmpqk.getQueKouLeiXing()) { //up缺口
							logger.debug(stock.getMyOwnName() + "UP缺口" + tmpqkdate);
							( (TDXNodesXPeriodData)stockxwkdate ).addQueKouTongJiJieGuo(tmpqkdate, 1, null, null, null, true);
							if(tmpqk.getQueKouHuiBuDate() != null) { //已经被回补
								LocalDate tmpqkhbdate = tmpqk.getQueKouHuiBuDate();
								logger.debug(stock.getMyOwnName() + "UP缺口回补" + tmpqkhbdate);
								( (TDXNodesXPeriodData)stockxwkdate ).addQueKouTongJiJieGuo(tmpqkhbdate, null, 1, null, null, true);
							} 
						} else { //down缺口
							logger.debug(stock.getMyOwnName() + "DW缺口" + tmpqkdate);
							( (TDXNodesXPeriodData)stockxwkdate ).addQueKouTongJiJieGuo(tmpqkdate, null, null, 1, null, true);
							if(tmpqk.getQueKouHuiBuDate() != null) { //已经被回补
								LocalDate tmpqkhbdate = tmpqk.getQueKouHuiBuDate();
								logger.debug(stock.getMyOwnName() + "DW缺口回补" + tmpqkhbdate);
								( (TDXNodesXPeriodData)stockxwkdate ).addQueKouTongJiJieGuo(tmpqkhbdate, null, null, null, 1, true);
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
			
			//标记缺口统计的时间，
			 if(!qkstartdate.equals(requiredstartday) && requiredstartday.isBefore(qkstartdate)) { //特别标记完整的openupquekou的起始日期，用于获得缺口统计的起始时间
				 ( (TDXNodesXPeriodData)stockxwkdate ).addQueKouTongJiJieGuo ( requiredstartday, 0, null, null, null,true);
        	 }  
			 if(!qkenddate.equals(requiredendday)  && requiredendday.isAfter(qkenddate) )  //特别标记完整的openupquekou的结束日期，用于获得缺口统计的结束时间
        		 ( (TDXNodesXPeriodData)stockxwkdate ).addQueKouTongJiJieGuo ( requiredendday, 0, null, null, null,true);
		}
//		System.out.print("");
		return stock;
	}
	/*
	 * 
	 */
	public Stock getStockKXian (String stockcode,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		Stock stock = (Stock)this.treecyl.getSpecificNodeByHypyOrCode(stockcode,BkChanYeLianTreeNode.TDXGG);
		stock = (Stock) this.getStockKXian(stock, requiredstartday, requiredendday,period);
		return stock;
	}
	/*
	 * 个股的K线从CSV里面读取
	 */
	public Stock getStockKXian (Stock stock,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		NodeXPeriodDataBasic nodedayperioddata = stock.getNodeXPeroidData(period);
		if(nodedayperioddata.getKXxianRecordsStartDate() == null) {
			stock = (Stock)bkdbopt.getStockDailyKXianZouShiFromCsv (stock,requiredstartday,requiredendday,period);
			return stock;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getKXxianRecordsStartDate(),nodedayperioddata.getKXxianRecordsEndDate());
		if(timeintervallist == null)
			return stock;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				stock = (Stock)bkdbopt.getStockDailyKXianZouShiFromCsv (stock,requiredstartday,requiredendday,period);
		}
		return stock;
	}
	public void syncStockData (Stock stock)
	{
		LocalDate bkstartday = stock.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getAmoRecordsStartDate();
		LocalDate bkendday = stock.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getAmoRecordsEndDate();
		
		if(bkstartday != null) {
			this.getStockKXian(stock, bkstartday, bkendday, TDXNodeGivenPeriodDataItem.DAY);
			this.getStockQueKouInfo(stock, bkstartday, bkendday, TDXNodeGivenPeriodDataItem.DAY);
		} 
		
		
	}
	/*
	 * 确定用户要求的时间段和node当前的数据的时间段的差值时间段
	 */
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
//				return result;
			}
			if(requiredinterval.abuts(nodeinterval)) {
				result.add(requiredinterval);
//				 return result;
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
	public  Number[] getTDXNodeNoneFixPeriodDpMinMaxWk (Stock stock, LocalDate requiredstartday, LocalDate requiredendday)
	{
//		if(node.getType() == BkChanYeLianTreeNode.TDXBK )
//			this.getBanKuai((BanKuai)node, requiredstartday, requiredendday, TDXNodeGivenPeriodDataItem.WEEK);
//		else if(node.getType() == BkChanYeLianTreeNode.TDXGG ||  node.getType() == BkChanYeLianTreeNode.BKGEGU)
//			this.getStock((Stock)node, requiredstartday, requiredendday, TDXNodeGivenPeriodDataItem.WEEK);
		
		TDXNodeGivenPeriodDataItem dataresult = bkdbopt.getStockNoFixPerioidZhanBiByWeek (stock,requiredstartday,requiredendday,TDXNodeGivenPeriodDataItem.WEEK);
		Double zhanbi = dataresult.getCjeZhanBi();
		
		NodeXPeriodDataBasic nodexdate = stock.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK);
		LocalDate recordstart = nodexdate.getAmoRecordsStartDate();
		LocalDate recordend = nodexdate.getAmoRecordsEndDate();
		LocalDate tmpdate = requiredstartday.minusWeeks(1).with(DayOfWeek.FRIDAY);
		 
		if(tmpdate.isBefore(recordstart)) {
			Number[] zhanbiresult = {zhanbi, null};
			return zhanbiresult;
		}
			 
		
		Integer dpminwk = 0; Integer dpmaxwk = 0;
		while (tmpdate.isAfter(recordstart) ) {
			Double tmpzb = nodexdate.getChenJiaoErZhanBi(tmpdate, 0);
			
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
			Double tmpzb = nodexdate.getChenJiaoErZhanBi(tmpdate, 0);
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
	

}
