package com.exchangeinfomanager.nodes.operations;

import java.time.DayOfWeek;

import java.time.LocalDate;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import java.util.List;

import org.apache.log4j.Logger;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;

import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;


import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
/*
 * 
 */
public class AllCurrentTdxBKAndStoksTree 
{
	
	private AllCurrentTdxBKAndStoksTree ()
	{
//		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
		

		initializeAllStocksTree ();
		setupDaPan ();
		
//		initializeChanYeLianTree ();
	}
	
	// ����ʵ��  
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
	private BanKuaiAndStockTree allbkggtree;

	private void initializeAllStocksTree() 
	{
		
		DaPan alltopNode = new DaPan("000000","��������");
		
		ArrayList<BanKuai> allbkandzs = bkdbopt.getTDXBanKuaiList ("all");
		for (BanKuai bankuai : allbkandzs ) 
		    alltopNode.add(bankuai);
		
		ArrayList<Stock> allstocks = bkdbopt.getAllStocks ();
		for (Stock stock : allstocks ) 
		    alltopNode.add(stock);

		allbkggtree = new BanKuaiAndStockTree(alltopNode,"ALLBKSTOCKS");
		allstocks = null;
	}
//	private void initializeChanYeLianTree() 
//	{
//		this.cyltreedb = new CylTreeDbOperation ();
//		this.cyltree = this.cyltreedb.getBkChanYeLianTree();
//		
//	}
	
	public  BanKuaiAndStockTree getAllBkStocksTree()
	{
		return allbkggtree;
	}
	/*
	 * 
	 */
	public void setupDaPan ()
	{
		DaPan treeallstockrootdapan = (DaPan)allbkggtree.getModel().getRoot();
		BanKuai shdpbankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		BanKuai szdpbankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.TDXBK);
		treeallstockrootdapan.setDaPanContents(shdpbankuai,szdpbankuai);
	}
	/**
	 * 	  
	 * �Ѱ��Ͱ���¸�������һ����ͬ��?
	 * @param bk
	 * @param requiredstartday
	 * @param requiredendday
	 * @param period
	 */
	public void syncBanKuaiAndItsStocksForSpecificTime (BanKuai bk,LocalDate requiredstartday,LocalDate requiredendday,String period,Boolean calwholeweek  )
	{
		this.getBanKuai(bk, requiredstartday, requiredendday, period,calwholeweek);
		
		this.syncBanKuaiData(bk);
		
		if(bk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) { 
			bk = this.getAllGeGuOfBanKuai (bk,period); 
			List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
			for(BkChanYeLianTreeNode stockofbk : allbkgg)   {
			    	if( ((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(requiredendday)  ) { 
			    		 Stock stock = this.getStock(((StockOfBanKuai)stockofbk).getStock(), requiredstartday, requiredendday, period,calwholeweek);
			    		 
		    			 this.syncStockData(stock);
			    	 }
	        		
			}
			
			if(calwholeweek ) { //
				
				this.getBanKuaiQueKouInfo(bk, requiredstartday, requiredendday, period);
				
				this.getBanKuaiZhangDieTingInfo(bk, requiredstartday, requiredendday, period);
			}
		}
	}
	/*
	 *
	 */
	public BanKuai getBanKuai (BanKuai bankuai,LocalDate requiredstartday,LocalDate requiredendday,String period ,Boolean calwholeweek )
	{
		NodeXPeriodData nodedayperioddata = bankuai.getNodeXPeroidData(period);
		
		if(!calwholeweek) {
			LocalDate curdate = bankuai.getNodeDataAtNotCalWholeWeekModeLastDate();
			if(curdate == null) {
				bankuai.setNodeDataAtNotCalWholeWeekMode(requiredendday);
				nodedayperioddata.resetAllData();
			} else if(!curdate.isEqual(requiredendday)) {
				bankuai.setNodeDataAtNotCalWholeWeekMode(requiredendday);
				nodedayperioddata.resetAllData();
			} else
				return bankuai;
		}
		if(calwholeweek) {
			if(bankuai.isNodeDataAtNotCalWholeWeekMode() ) {
				bankuai.setNodeDataAtNotCalWholeWeekMode(null);
				nodedayperioddata.resetAllData();
			}
		}

		if(nodedayperioddata.getOHLCRecordsStartDate() == null) {
			bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,requiredstartday,requiredendday,period);
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
				
				bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,requiredstartday,requiredendday,period);
		}
		return bankuai;
	}
	/*
	 * 
	 */
	public BanKuai getBanKuai (String bkcode,LocalDate requiredstartday,LocalDate requiredendday,String period) 
	{
		BanKuai bankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		if(bankuai == null)
			return null;
		
		bankuai = this.getBanKuai (bankuai,requiredstartday,requiredendday,period,false);
		return bankuai;
	}
	/*
	 *����K�����ƣ� ֱ�Ӵ����ݿ��ж�ȡ���ݣ��͸��ɲ�һ��
	 */
	public BanKuai getBanKuaiKXian (String bkcode,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		BanKuai bk = (BanKuai)this.allbkggtree.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		bk = (BanKuai) this.getBanKuaiKXian(bk, requiredstartday,requiredendday, period);
		return bk;
	}
	private void teststub (TDXNodes bk,String period) 
	{
		if(bk.getMyOwnCode().equals("399006") || bk.getMyOwnCode().equals("999999") || bk.getMyOwnCode().equals("399001"))
			return;
		
		period = NodeGivenPeriodDataItem.WEEK;
		NodeXPeriodData nodexdata = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		LocalDate start = nodexdata.getOHLCRecordsStartDate();
		LocalDate end = nodexdata.getOHLCRecordsEndDate();
		
		LocalDate tmpdate = start;
		do  {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			//����Ӧ�ø�������������ѡ���������ͣ�������Ϊ�������ߣ��Ͳ�ϸ����
			Double cje = nodexdata.getChengJiaoEr(wkfriday, 0);
			Double zdf = nodexdata.getSpecificOHLCZhangDieFu(wkfriday, 0);
			if(zdf!=null)
				logger.info(wkfriday + "::" + zdf.toString() );
			else
				logger.info(wkfriday + ":: null" );
			
				
			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
		} while (tmpdate.isBefore( end) || tmpdate.isEqual(end));
		
		return ;
	}
	public BanKuai getBanKuaiKXian (BanKuai bk,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		
		NodeXPeriodData nodedayperioddata = bk.getNodeXPeroidData(period);
		if(nodedayperioddata.getOHLCRecordsStartDate() == null) {
			bk = bkdbopt.getBanKuaiKXianZouShi (bk,requiredstartday,requiredendday,period);
			
			return bk;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getOHLCRecordsStartDate(),nodedayperioddata.getOHLCRecordsEndDate() );
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
	/*
	 * ͳ�ư����ǵ�ͣ����,���ݴӰ������������?
	 */
	public BanKuai getBanKuaiZhangDieTingInfo (BanKuai bk,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
		
		LocalDate tmpdate = requiredstartday;
		do {
			 Integer zhangtingnum = 0;
			 Integer dietingnum = 0;
			 
			for(BkChanYeLianTreeNode stockofbk : allbkgg)   {
				if( !((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(tmpdate)  )  //ȷ�ϵ�ǰ���ڰ����?
					continue;
				
//				if(stockofbk.getMyOwnCode().equals("600812") && tmpdate.getYear() == 2019 && tmpdate.getMonthValue() == 8)
//					logger.debug("tet start");
				
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
	/*
	 * 
	 */
	public BanKuai getBanKuaiQueKouInfo (String bkcode,LocalDate requiredrecordsday,LocalDate requiredendday,String period)
	{
		BanKuai bankuai = (BanKuai)this.allbkggtree.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		bankuai = (BanKuai) this.getBanKuaiQueKouInfo(bankuai, requiredrecordsday, requiredendday, period);
		return bankuai;
	}
	public BanKuai getBanKuaiQueKouInfo (BanKuai bk,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
//		bk = this.getAllGeGuOfBanKuai (bk,period); //��ȡ���������Ǹð��ĸ���
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
		
		LocalDate tmpdate = requiredstartday;
		do {
			 Integer openup = 0;
			 Integer opendown = 0;
			 Integer huibuup = 0;
			 Integer huibudown = 0;
			 
			for(BkChanYeLianTreeNode stockofbk : allbkgg)   {
				if( ! ((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(tmpdate)  )  //ȷ�ϵ�ǰ���ڰ����?
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
	public void syncBanKuaiData (BanKuai bk)
	{
//		if(bk.isNodeDataAtNotCalWholeWeekMode() )
//			return;
		
		LocalDate bkstartday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsStartDate();
		LocalDate bkendday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsEndDate();
		
		this.getBanKuaiKXian(bk, bkstartday, bkendday, NodeGivenPeriodDataItem.DAY);
//		this.getBanKuaiQueKouInfo(bk, bkstartday, bkendday, NodeGivenPeriodDataItem.WEEK);
	}
	/*
	 * 
	 */
	public BanKuai getAllGeGuOfBanKuai (BanKuai bankuai,String period) 
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getOHLCRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getOHLCRecordsEndDate();
		
		if(bkstartday == null || bkendday == null)  {
			bkstartday = CommonUtility.getSettingRangeDate(LocalDate.now(), "middle");
			bkendday = LocalDate.now();
//			return bankuai;
		}
		
		//ͬ�����ĸ���
		bankuai = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (bankuai,bkstartday,bkendday,allbkggtree);
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
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getOHLCRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getOHLCRecordsEndDate();
		
		if(bkstartday == null && bkendday == null) //��鱾��û�����ݣ��޷��ҳ���Ӧ�ĸ��ɵ����ݣ�����ԭ·����?
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
		BanKuai bankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		if(bankuai == null)
			return null;
		
		StockOfBanKuai stock = (StockOfBanKuai) bankuai.getBanKuaiGeGu(stockcode);
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
		BanKuai shdpbankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		BanKuai szdpbankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.TDXBK);
		BanKuai cybdpbankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode("399006",BkChanYeLianTreeNode.TDXBK);
		
		shdpbankuai = (BanKuai) this.getBanKuaiKXian(shdpbankuai, requiredstartday, requiredendday,period);
		szdpbankuai = (BanKuai) this.getBanKuaiKXian(szdpbankuai, requiredstartday, requiredendday,period);
		cybdpbankuai = (BanKuai) this.getBanKuaiKXian(cybdpbankuai, requiredstartday, requiredendday, period);
		
	}
	/*
	 * ͬ�����̳ɽ���
	 */
	public void getDaPan (LocalDate requiredstartday,LocalDate requiredendday,String period, Boolean calwholeweek)
	{
		BanKuai shdpbankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		BanKuai szdpbankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.TDXBK);
		BanKuai cybdpbankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode("399006",BkChanYeLianTreeNode.TDXBK);
		
		shdpbankuai = this.getBanKuai (shdpbankuai,requiredstartday,requiredendday,period,calwholeweek);
		szdpbankuai = this.getBanKuai (szdpbankuai,requiredstartday,requiredendday,period,calwholeweek);
		cybdpbankuai = this.getBanKuai (cybdpbankuai,requiredstartday,requiredendday,period,calwholeweek);
	}
	public void syncDaPanData() 
	{
		BanKuai shdpbankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		LocalDate bkstartday = shdpbankuai.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsStartDate();
		LocalDate bkendday = shdpbankuai.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsEndDate();
		
		this.getDaPanKXian (bkstartday,bkendday,NodeGivenPeriodDataItem.DAY);
	}
	/*
	 * 
	 */
	public Stock getStock (Stock stock,LocalDate requiredstartday,LocalDate requiredendday,String period, boolean calwholeweek )
	{
		NodeXPeriodData nodedayperioddata = stock.getNodeXPeroidData(period);
		
		if(!calwholeweek) {
			LocalDate curdate = stock.getNodeDataAtNotCalWholeWeekModeLastDate();
			if(curdate == null) {
				stock.setNodeDataAtNotCalWholeWeekMode(requiredendday);
				nodedayperioddata.resetAllData();
			} else if(!curdate.isEqual(requiredendday)) {
				stock.setNodeDataAtNotCalWholeWeekMode(requiredendday);
				nodedayperioddata.resetAllData();
			} else
				return stock;
		}
		if(calwholeweek) {
			if(stock.isNodeDataAtNotCalWholeWeekMode() ) {
				stock.setNodeDataAtNotCalWholeWeekMode(null);
				nodedayperioddata.resetAllData();
			}
		}
		
		if(nodedayperioddata.getOHLCRecordsStartDate() == null) {
			stock = bkdbopt.getStockZhanBi (stock,requiredstartday,requiredendday,period);
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
				
				stock = bkdbopt.getStockZhanBi (stock,requiredstartday,requiredendday,period);
		}
		return stock;
	}
	public Stock getStock (String stockcode,LocalDate requiredstartday,LocalDate requiredendday,String period) 
	{
		Stock stock = (Stock) allbkggtree.getSpecificNodeByHypyOrCode(stockcode,BkChanYeLianTreeNode.TDXGG);
		if(stock == null)
			return null;
		
		stock = this.getStock (stock,requiredstartday,requiredendday,period,false);
		return stock;
	}
	/*
	 * 
	 */
	public Stock getStockQueKouInfo (String stockcode,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		Stock stock = (Stock)this.allbkggtree.getSpecificNodeByHypyOrCode(stockcode,BkChanYeLianTreeNode.TDXGG);
		stock = this.getStockQueKouInfo(stock, requiredstartday, requiredendday, period);
		return stock;
	}
	/*
	 * 
	 */
	public Stock getStockQueKouInfo (Stock stock,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		NodeXPeriodData stockxwkdate = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		LocalDate qkstartday = stockxwkdate.getQueKouRecordsStartDate();
		LocalDate qkendday = stockxwkdate.getQueKouRecordsEndDate();
		
		NodeXPeriodData nodedayperioddata = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
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
							logger.debug(stock.getMyOwnName() + "UPȱ��" + tmpqkdate);
							stockxwkdate.addQueKouTongJiJieGuo(tmpqkdate, 1, null, null, null, true);
							if(tmpqk.getQueKouHuiBuDate() != null) { //�Ѿ����ز�
								LocalDate tmpqkhbdate = tmpqk.getQueKouHuiBuDate();
								logger.debug(stock.getMyOwnName() + "UPȱ�ڻز�" + tmpqkhbdate);
								stockxwkdate.addQueKouTongJiJieGuo(tmpqkhbdate, null, 1, null, null, true);
							} 
						} else { //downȱ��
							logger.debug(stock.getMyOwnName() + "DWȱ��" + tmpqkdate);
							stockxwkdate.addQueKouTongJiJieGuo(tmpqkdate, null, null, 1, null, true);
							if(tmpqk.getQueKouHuiBuDate() != null) { //�Ѿ����ز�
								LocalDate tmpqkhbdate = tmpqk.getQueKouHuiBuDate();
								logger.debug(stock.getMyOwnName() + "DWȱ�ڻز�" + tmpqkhbdate);
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
			
			//���ȱ��ͳ�Ƶ�ʱ��?
			 if(!qkstartdate.equals(requiredstartday) && requiredstartday.isBefore(qkstartdate)) { //�ر���������openupquekou����ʼ���ڣ����ڻ��ȱ��ͳ�Ƶ���ʼʱ��?
				 stockxwkdate.addQueKouTongJiJieGuo ( requiredstartday, 0, null, null, null,true);
        	 }  
			 if(!qkenddate.equals(requiredendday)  && requiredendday.isAfter(qkenddate) )  //�ر���������openupquekou�Ľ������ڣ����ڻ��ȱ��ͳ�ƵĽ���ʱ��?
        		 stockxwkdate.addQueKouTongJiJieGuo ( requiredendday, 0, null, null, null,true);
		}
//		System.out.print("");
		return stock;
	}
	/*
	 * 
	 */
	public Stock getStockKXian (String stockcode,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		Stock stock = (Stock)this.allbkggtree.getSpecificNodeByHypyOrCode(stockcode,BkChanYeLianTreeNode.TDXGG);
		stock = (Stock) this.getStockKXian(stock, requiredstartday, requiredendday,period);
		return stock;
	}
	/*
	 * ���ɵ�K�ߴ�CSV������?
	 */
	public Stock getStockKXian (Stock stock,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		NodeXPeriodData nodedayperioddata = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
		if(nodedayperioddata.getOHLCRecordsStartDate() == null) {
			stock = (Stock)bkdbopt.getStockDailyKXianZouShiFromCsv (stock,requiredstartday,requiredendday,period);
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
				
				stock = (Stock)bkdbopt.getStockDailyKXianZouShiFromCsv (stock,requiredstartday,requiredendday,period);
		}
		return stock;
	}
	public void syncStockData (Stock stock)
	{
		LocalDate bkstartday = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsStartDate();
		LocalDate bkendday = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsEndDate();
		
		if(bkstartday != null) 
			this.getStockKXian(stock, bkstartday, bkendday, NodeGivenPeriodDataItem.DAY);

		if(bkstartday != null && !stock.isNodeDataAtNotCalWholeWeekMode() )
			this.getStockQueKouInfo(stock, bkstartday, bkendday, NodeGivenPeriodDataItem.DAY);
		
	}
	/*
	 * ȷ���û�Ҫ���ʱ��κ�node��ǰ�����ݵ�ʱ��εĲ�ֵʱ���
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
//			this.getBanKuai((BanKuai)node, requiredstartday, requiredendday, NodeGivenPeriodDataItem.WEEK);
//		else if(node.getType() == BkChanYeLianTreeNode.TDXGG ||  node.getType() == BkChanYeLianTreeNode.BKGEGU)
//			this.getStock((Stock)node, requiredstartday, requiredendday, NodeGivenPeriodDataItem.WEEK);
		
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
