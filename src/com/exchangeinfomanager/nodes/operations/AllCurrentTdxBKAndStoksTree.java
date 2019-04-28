package com.exchangeinfomanager.nodes.operations;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.nodexdata.BanKuaiAndStockXPeriodData;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
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
		
		ArrayList<Stock> allstocks = bkdbopt.getAllStocks ();
		for (Stock stock : allstocks ) {
		    alltopNode.add(stock);

		}
		
		ArrayList<BanKuai> allbkandzs = bkdbopt.getTDXBanKuaiList ("all");
		for (BanKuai bankuai : allbkandzs ) {
		    alltopNode.add(bankuai);
//		    logger.debug(bankuai.getMyOwnCode()+ "类型" + bankuai.getType());
		}

		treecyl = new BanKuaiAndStockTree(alltopNode,"ALLBKSTOCKS");
		allstocks = null;
	}
	
	public  BanKuaiAndStockTree getAllBkStocksTree()
	{
		return treecyl;
	}
	
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
		bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,requiredstartday,requiredendday,period);
		
//		String bkcode = bankuai.getMyOwnCode();
//		if(!bkcode.equals("399001") && !bkcode.equals("999999") ) { //2个大盘指数
//			getDaPan (requiredstartday,requiredendday,period);
//		}
		
		return bankuai;
	}
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
	public BanKuai getBanKuaiKXian (BanKuai bk,LocalDate requiredstartday,LocalDate requiredenddayString,String period)
	{
		bk = bkdbopt.getBanKuaiKXianZouShi (bk,requiredstartday,requiredenddayString,period);
		
		return bk;
	}
	public BanKuai getBanKuaiQueKouInfo (String bkcode,LocalDate requiredrecordsday,LocalDate requiredendday,String period)
	{
		BanKuai bankuai = (BanKuai)this.treecyl.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		bankuai = (BanKuai) this.getBanKuaiQueKouInfo(bankuai, requiredrecordsday, requiredendday, period);
		return bankuai;
	}
	public BanKuai getBanKuaiQueKouInfo (BanKuai bk,LocalDate requiredrecordsday,LocalDate requiredendday,String period)
	{
		bk = bkdbopt.getStocksInBanKuaiQueKouTimeRangTongJIResult (bk,requiredrecordsday,requiredendday,period);
		
		return bk;
	}
	public void syncBanKuaiData (BanKuai bk)
	{
		LocalDate bkstartday = bk.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getRecordsStartDate();
		LocalDate bkendday = bk.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getRecordsEndDate();
		
		this.getBanKuaiKXian(bk, bkstartday, bkendday, TDXNodeGivenPeriodDataItem.DAY);
		this.getBanKuaiQueKouInfo(bk, bkstartday, bkendday, TDXNodeGivenPeriodDataItem.WEEK);
		
	}
	/*
	 * 只要是在整个时间周期内都曾经是该板块的个股，板块都会存入 
	 */
	public BanKuai getAllGeGuOfBanKuai (BanKuai bankuai,String period) 
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getRecordsEndDate();
		
		if(bkstartday == null || bkendday == null) 
			return bankuai;
		
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
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getRecordsEndDate();
		
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
		LocalDate bkstartday = shdpbankuai.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getRecordsStartDate();
		LocalDate bkendday = shdpbankuai.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getRecordsEndDate();
		
		this.getDaPanKXian (bkstartday,bkendday,TDXNodeGivenPeriodDataItem.DAY);
		
	}
	
	public Stock getStock (Stock stock,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		stock = bkdbopt.getStockZhanBi (stock,requiredstartday,requiredendday,period);
		
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
	public Stock getStockQueKouInfo (String stockcode,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		Stock stock = (Stock)this.treecyl.getSpecificNodeByHypyOrCode(stockcode,BkChanYeLianTreeNode.TDXGG);
		stock = this.getStockQueKouInfo(stock, requiredstartday, requiredendday, period);
		return stock;
	}
	public Stock getStockQueKouInfo (Stock stock,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		stock = bkdbopt.getStockQueKouTimeRangTongJIResult (stock,requiredstartday,requiredendday,period);
		
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
		stock = (Stock)bkdbopt.getStockKXianZouShiFromCsv (stock,requiredstartday,requiredendday,period);
				
		return stock;
	}
	public void syncStockData (Stock stock)
	{
		LocalDate bkstartday = stock.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getRecordsStartDate();
		LocalDate bkendday = stock.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getRecordsEndDate();
		
		if(bkstartday != null) {
			this.getStockKXian(stock, bkstartday, bkendday, TDXNodeGivenPeriodDataItem.DAY);
			this.getStockQueKouInfo(stock, bkstartday, bkendday, TDXNodeGivenPeriodDataItem.WEEK);
		} 
	}

	

}
