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
		ArrayList<StockOfBanKuai> allbkgg = bankuai.getAllCurrentBanKuaiGeGu();
		for(StockOfBanKuai stockofbk : allbkgg)   {
    		  stockofbk = this.getGeGuOfBanKuai(bankuai, stockofbk,period );
//    		  Stock stock = (Stock)this.treecyl.getSpecificNodeByHypyOrCode(stockofbk.getMyOwnCode(), BkChanYeLianTreeNode.TDXGG);
    		  Stock stock = this.getStock(stockofbk.getMyOwnCode(), bkstartday, bkendday, period);
		}
		
		return bankuai;
	}
	private StockOfBanKuai getGeGuOfBanKuai(BanKuai bankuai, StockOfBanKuai stockofbk,String period)
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

//	private BanKuai getBanKuai (BanKuai bankuai,LocalDate requiredrecordsday,String period)
//	{
//		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getRecordsStartDate();
//		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getRecordsEndDate();
//		
//		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange() + 3,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		
//		//判断时间的相关关系，以便决定是否需要到数据库中查询新纪录
//		if(bkstartday == null || bkendday == null) { //还没有数据，直接找
//			bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,requirestart,requireend,period);
////			bankuai = bkdbopt.getStockOfBanKuaiQueKouTimeRangTongJIResult (bankuai,requirestart,requireend,period);
//		} else	{
//			HashMap<String,LocalDate> startend = null;
//			if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
//				startend = nodeWeekTimeStampRelation (bkstartday,bkendday,requirestart,requireend);
//			else if(period.equals(TDXNodeGivenPeriodDataItem.DAY)) //暂时没开发
//				;
//			else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH)) //暂时没开发
//				;
//			
//			if(!startend.isEmpty()) {
//				LocalDate searchstart,searchend,position;
//				searchstart = startend.get("searchstart"); 
//				searchend = startend.get("searchend");
//				bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,searchstart,searchend,period);
////				bankuai = bkdbopt.getStockOfBanKuaiQueKouTimeRangTongJIResult (bankuai,searchstart,searchend,period);
//			}
//		}
//	
//		String bkcode = bankuai.getMyOwnCode();
//		if(!bkcode.equals("399001") && !bkcode.equals("999999") ) { //2个大盘指数
//			getDaPan (requiredrecordsday,period);
//		}
//		
//		
//		
//		return bankuai;
//	}
//	private BanKuai getBanKuai (String bkcode,LocalDate requiredrecordsday,String period) 
//	{
//		BanKuai bankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
//		if(bankuai == null)
//			return null;
//		
//		bankuai = this.getBanKuai (bankuai,requiredrecordsday,period);
//		return bankuai;
//	}

	
	
	/*
	 * 同步每个板块的所属个股的属于板块的相关数据，同时同步该股自己的数据
	 */
//	private StockOfBanKuai getGeGuOfBanKuai(BanKuai bankuai, StockOfBanKuai stock,String period)
//	{
//		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getRecordsStartDate();
//		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getRecordsEndDate();
//		
//		if(bkstartday == null && bkendday == null) //板块本身没有数据，无法找出对应的个股的数据，所以原路返回
//			return stock;
//		
//		LocalDate stockstartday = stock.getNodeXPeroidData(period).getRecordsStartDate();
//		LocalDate stockendday = stock.getNodeXPeroidData(period).getRecordsEndDate();
//		
//		if(stockstartday == null || stockendday == null ) { //还没有数据，直接找
//			stock = bkdbopt.getGeGuZhanBiOfBanKuai (bankuai,stock, bkstartday, bkendday,period);
//			Stock stockroot = stock.getStock(); //本身stock胡数据
//			this.getStock(stockroot, bkstartday, bkendday, period);
////			bkdbopt.getStockZhanBi (stock.getStock(), bkstartday, bkendday,period);
//		} else {
//			HashMap<String,LocalDate> startend = null;
//			if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
//				startend = nodeWeekTimeStampRelation (stockstartday,stockendday,bkstartday,bkendday);
//			else if(period.equals(TDXNodeGivenPeriodDataItem.DAY)) //暂时没开发
//				;
//			else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH)) //暂时没开发
//				;
//			
//			if(!startend.isEmpty()) {
//				LocalDate searchstart,searchend,position;
//				searchstart = startend.get("searchstart"); 
//				searchend = startend.get("searchend");
//				stock = bkdbopt.getGeGuZhanBiOfBanKuai (bankuai,stock,searchstart,searchend,period);
//				this.getStock (stock.getStock(), searchstart, searchend,period);
//			}
//			
//			startend = null;
//		}
//		
//		return stock;
//
//	}
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
	/*
	 * 确定时间点直接的关系,周线
	 */
//	private  HashMap<String, LocalDate> nodeWeekTimeStampRelation (LocalDate curstart,LocalDate curend, LocalDate requiredstart, LocalDate requiredend) 
//	{
//		HashMap<String,LocalDate> startend = new HashMap<String,LocalDate> (); 
//		if(  CommonUtility.isInSameWeek(curstart,requiredstart) &&  CommonUtility.isInSameWeek(requiredend,curend)    ) {//数据完整
//			return startend;
//		}	
//		else if( (requiredstart.isAfter(curstart) || requiredstart.isEqual(curstart) ) && (requiredend.isBefore(curend) || requiredend.isEqual(curend) ) ) {  //数据完整
//			return startend;
//		}
//		else if( !CommonUtility.isInSameWeek(curstart,requiredstart)  && requiredstart.isBefore(curstart) //部分完整1,前缺失
//				&& (requiredend.isBefore(curend) || CommonUtility.isInSameWeek(requiredend,curend) )    ) {
//			LocalDate searchstart,searchend;
//			searchstart = requiredstart; 
//			searchend = curstart.with(DayOfWeek.SATURDAY).minus(1,ChronoUnit.WEEKS);
//			startend.put("searchstart", searchstart);
//			startend.put("searchend",  searchend);
//			return startend;
//		}
//		else if ( (CommonUtility.isInSameWeek(curstart,requiredstart) || requiredstart.isAfter(curstart) )  //部分完整2，后缺失
//				&& requiredend.isAfter(curend) &&  !CommonUtility.isInSameWeek(requiredend,curend)    ) {
//			LocalDate searchstart,searchend;
//			searchstart = curend.with(DayOfWeek.MONDAY).plus(1,ChronoUnit.WEEKS); 
//			searchend = requiredend;
//			startend.put("searchstart", searchstart);
//			startend.put("searchend", searchend);
//			return startend;
//		}
//		else if( requiredstart.isBefore(curstart) && requiredend.isAfter(curend)  ) {//部分完整3， 前后双缺失，这种情况目前的设置似乎不可能发生，暂时不写
////			logger.info("部分完整3， 前后双缺失!");
//			startend.put("searchstart", requiredstart);
//			startend.put("searchend", requiredend);
//			return startend;
//		}
//
//		return null;
//	}
	//日线
//	private  HashMap<String, LocalDate> nodeDayTimeStampRelation (LocalDate curstart,LocalDate curend, LocalDate requiredstart, LocalDate requiredend) 
//	{
//		HashMap<String,LocalDate> startend = new HashMap<String,LocalDate> (); 
//		if(  curstart.isEqual(requiredstart) && requiredend.isEqual(curend)    ) {//数据完整
//			return startend;
//		}	
//		else if( (requiredstart.isAfter(curstart) || requiredstart.isEqual(curstart) ) && (requiredend.isBefore(curend) || requiredend.isEqual(curend) ) ) {  //数据完整
//			return startend;
//		}
//		else if( !curstart.isEqual(requiredstart)  && requiredstart.isBefore(curstart) //部分完整1,前缺失
//				&& (requiredend.isBefore(curend) || requiredend.isEqual(curend) )    ) {
//			LocalDate searchstart,searchend;
//			searchstart = requiredstart; 
//			searchend = curstart.with(DayOfWeek.SATURDAY).minus(1,ChronoUnit.WEEKS);
//			startend.put("searchstart", searchstart);
//			startend.put("searchend",  searchend);
//			return startend;
//		}
//		else if ( (curstart.isEqual(requiredstart) || requiredstart.isAfter(curstart) )  //部分完整2，后缺失
//				&& requiredend.isAfter(curend) &&  !requiredend.isEqual(curend)    ) {
//			LocalDate searchstart,searchend;
//			searchstart = curend.with(DayOfWeek.MONDAY).plus(1,ChronoUnit.WEEKS); 
//			searchend = requiredend;
//			startend.put("searchstart", searchstart);
//			startend.put("searchend", searchend);
//			return startend;
//		}
//		else if( requiredstart.isBefore(curstart) && requiredend.isAfter(curend)  ) {//部分完整3， 前后双缺失，这种情况目前的设置似乎不可能发生，暂时不写
//			logger.debug("部分完整3， 前后双缺失，这种情况目前的设置似乎不可能发生");
//			return startend;
//		}
//
//		return null;
//	}
	
//	/*
//	 *板块的K线走势， 直接从数据库中读取数据，和个股不一样
//	 */
//	public BanKuai getBanKuaiKXian (String bkcode,LocalDate requiredrecordsday,String period)
//	{
//		BanKuai stock = (BanKuai)this.treecyl.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXGG);
//		stock = (BanKuai) this.getBanKuaiKXian(stock, requiredrecordsday, period);
//		return stock;
//	}
//	/*
//	 *板块的K线走势，直接从数据库中读取数据 ，和个股不一样
//	 */
//	public BanKuai getBanKuaiKXian (BanKuai bk,LocalDate requiredrecordsday,String period) 
//	{
//		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		
//		try{
//			
//			NodeXPeriodDataBasic stockxdata = bk.getNodeXPeroidData(period);
//			LocalDate nodestartday = stockxdata.getRecordsStartDate();
//			LocalDate nodeendday = stockxdata.getRecordsEndDate();
//			
//			if(nodestartday == null || nodeendday == null) { //还没有数据，直接找
//				bk = bkdbopt.getBanKuaiKXianZouShi (bk,requirestart,requireend,period);
//			} else {
//				HashMap<String,LocalDate> startend = null ;
//				if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
//					startend = nodeWeekTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(TDXNodeGivenPeriodDataItem.DAY)) 
//					startend = nodeDayTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH)) //暂时没开发
//					;
//				 
//				LocalDate searchstart,searchend,position;
//				if(!startend.isEmpty()) {
//					searchstart = startend.get("searchstart"); 
//					searchend = startend.get("searchend");
//
//					bk = bkdbopt.getBanKuaiKXianZouShi (bk,searchstart,searchend,period);
//				}
//				
//				startend = null;
//			}
//		} catch (java.lang.NullPointerException e) {
//			e.printStackTrace();
//		}
//		
//		return bk;
//		
//	}
//	public BanKuai getBanKuaiQueKouInfo (String bkcode,LocalDate requiredrecordsday,String period)
//	{
//		BanKuai bankuai = (BanKuai)this.treecyl.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXGG);
//		bankuai = (BanKuai) this.getBanKuaiQueKouInfo(bankuai, requiredrecordsday, period);
//		return bankuai;
//	}
//	public BanKuai getBanKuaiQueKouInfo (BanKuai bk,LocalDate requiredrecordsday,String period)
//	{
//		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		logger.debug("debug");
//		try{
//			
//			NodeXPeriodDataBasic stockxdata = bk.getNodeXPeroidData(period);
//			LocalDate nodestartday = stockxdata.getRecordsStartDate();
//			LocalDate nodeendday = stockxdata.getRecordsEndDate();
//			
//			if(nodestartday == null || nodeendday == null) { //还没有数据，直接找
//				bk = bkdbopt.getStockOfBanKuaiQueKouTimeRangTongJIResult (bk,requirestart,requireend,period);
//			} else {
//				HashMap<String,LocalDate> startend = null ;
//				if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
//					startend = nodeWeekTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(TDXNodeGivenPeriodDataItem.DAY)) 
//					startend = nodeDayTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH)) //暂时没开发
//					;
//				 
//				LocalDate searchstart,searchend,position;
//				if(!startend.isEmpty()) {
//					searchstart = startend.get("searchstart"); 
//					searchend = startend.get("searchend");
//
//					bk = bkdbopt.getStockOfBanKuaiQueKouTimeRangTongJIResult (bk,searchstart,searchend,period);
//				}
//				
//				startend = null;
//			}
//		} catch (java.lang.NullPointerException e) {
//			e.printStackTrace();
//		}
//		
//		return bk;
//
//		
//	}
	
	/*
	 * 独立个股的指定周期的占比
	 */
//	public Stock getStock (String stockcode,LocalDate requiredrecordsday,String period)
//	{
//		Stock stock = (Stock)this.treecyl.getSpecificNodeByHypyOrCode(stockcode,BkChanYeLianTreeNode.TDXGG);
//		if(stock == null)
//			return null;
//		
//		this.getStock(stock, requiredrecordsday, period);
//		
//		return stock;
//	}
//	public Stock getStock(Stock stock, LocalDate requirestart, LocalDate requireend,String period)
//	{
//		try{
//			NodeXPeriodDataBasic stockxdata = stock.getNodeXPeroidData(period);
//			LocalDate nodestartday = stockxdata.getRecordsStartDate();
//			LocalDate nodeendday = stockxdata.getRecordsEndDate();
//					
////			if(stock.getMyOwnCode().equals("300017"))
////				logger.debug("time to debug");
//			
//			if(nodestartday == null || nodeendday == null) { //还没有数据，直接找
//				stock = bkdbopt.getStockZhanBi ((Stock)stock,requirestart,requireend,period); 
//				stock = bkdbopt.getStockQueKouTimeRangTongJIResult ((Stock)stock,requirestart,requireend,period);
//			} else {
//				HashMap<String,LocalDate> startend = null ;
////				if(period.equals(ChenJiaoZhanBiInGivenPeriod.WEEK))
//					startend = nodeWeekTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
////				else if(period.equals(ChenJiaoZhanBiInGivenPeriod.DAY)) 
////					startend = nodeDayTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
////				else if(period.equals(ChenJiaoZhanBiInGivenPeriod.MONTH)) //暂时没开发
//					;
//						 
//				LocalDate searchstart,searchend,position;
//				if(!startend.isEmpty()) {
//							searchstart = startend.get("searchstart"); 
//							searchend = startend.get("searchend");
//							
//							stock = bkdbopt.getStockZhanBi ((Stock)stock,searchstart,searchend,period);
//							stock = bkdbopt.getStockQueKouTimeRangTongJIResult ((Stock)stock,searchstart,searchend,period);
//				}
//				
//				startend = null;
//			}
//		} catch (java.lang.NullPointerException e) {
//					e.printStackTrace();
//		}
//		
//		return stock;
//	}
//	/*
//	 * 
//	 */
//	public Stock getStock(Stock stock, LocalDate requiredrecordsday, String period)
//	{
//		//占比还是要看周线/月线，日线数据没有太大意义，下面专门找出个股周线占比数据
//		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		
//		stock = this.getStock(stock,requirestart,requireend,period);
//		
//		return stock;
//	}
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
		
		this.getStockKXian(stock, bkstartday, bkendday, TDXNodeGivenPeriodDataItem.DAY);
		this.getStockQueKouInfo(stock, bkstartday, bkendday, TDXNodeGivenPeriodDataItem.WEEK);
		
	}

	

}
