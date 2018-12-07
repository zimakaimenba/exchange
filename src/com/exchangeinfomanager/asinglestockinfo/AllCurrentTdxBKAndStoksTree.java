package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

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
//		    logger.debug(stock.getMyOwnCode() + "类型" + stock.getType());
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
		BanKuai shdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("999999",BanKuaiAndStockBasic.TDXBK);
		BanKuai szdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("399001",BanKuaiAndStockBasic.TDXBK);
		treeallstockrootdapan.setShangHai(shdpbankuai);
		treeallstockrootdapan.setShenZhen(szdpbankuai);
	}
	
	/*
	 * 获得板块，并同步上证/深圳指数的成交量，以保住板块和上证/深圳的成交量记录日期跨度是完全的。
	 */
	public BanKuai getBanKuai (BanKuai bankuai,LocalDate requiredrecordsday,String period)
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getRecordsEndDate();
		
		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		//判断时间的相关关系，以便决定是否需要到数据库中查询新纪录
		if(bkstartday == null || bkendday == null) { //还没有数据，直接找
			bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,requirestart,requireend,period);
		} else	{
			HashMap<String,LocalDate> startend = null;
			if(period.equals(StockGivenPeriodDataItem.WEEK))
				startend = nodeWeekTimeStampRelation (bkstartday,bkendday,requirestart,requireend);
			else if(period.equals(StockGivenPeriodDataItem.DAY)) //暂时没开发
				;
			else if(period.equals(StockGivenPeriodDataItem.MONTH)) //暂时没开发
				;
			
			if(!startend.isEmpty()) {
				LocalDate searchstart,searchend,position;
				searchstart = startend.get("searchstart"); 
				searchend = startend.get("searchend");
				bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,searchstart,searchend,period);
			}
		}
	
		String bkcode = bankuai.getMyOwnCode();
		if(!bkcode.equals("399001") && !bkcode.equals("999999") ) { //2个大盘指数
			getDaPan (requiredrecordsday,period);
		}
		
		return bankuai;
	}
	public BanKuai getBanKuai (String bkcode,LocalDate requiredrecordsday,String period) 
	{
		BanKuai bankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode(bkcode,BanKuaiAndStockBasic.TDXBK);
		if(bankuai == null)
			return null;
		
		bankuai = this.getBanKuai (bankuai,requiredrecordsday,period);
		return bankuai;
	}
	/*
	 * 
	 */
	public BanKuai getBanKuaiKXian (String bkcode,LocalDate requiredrecordsday,String period)
	{
		BanKuai stock = (BanKuai)this.treecyl.getSpecificNodeByHypyOrCode(bkcode,BanKuaiAndStockBasic.TDXGG);
		stock = (BanKuai) this.getNodeKXian(stock, requiredrecordsday, period);
		return stock;
	}
	/*
	 * 
	 */
	public BanKuai getBanKuaiKXian (BanKuai bankuai,LocalDate requiredrecordsday,String period)
	{
		bankuai = (BanKuai) this.getNodeKXian(bankuai, requiredrecordsday, period);
		return bankuai;
	}
	/*
	 * 只要是在整个时间周期内都曾经是该板块的个股，板块都会存入 
	 */
	public BanKuai getAllGeGuOfBanKuai (BanKuai bankuai,String period) 
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getRecordsEndDate();
		
		if(bkstartday == null || bkendday == null) {
			bkendday = LocalDate.now();
			bkstartday = bkendday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		}
		
		//同步板块的个股
		bankuai = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (bankuai,bkstartday,bkendday,treecyl);
		ArrayList<StockOfBanKuai> allbkgg = bankuai.getAllCurrentBanKuaiGeGu();
		for(StockOfBanKuai stock : allbkgg)   {
    		  stock = this.getGeGuOfBanKuai(bankuai, stock,period );
		}
		return bankuai;
	}
	/*
	 * 同步每个板块的所属个股的属于板块的相关数据，同时同步该股自己的数据
	 */
	private StockOfBanKuai getGeGuOfBanKuai(BanKuai bankuai, StockOfBanKuai stock,String period)
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getRecordsEndDate();
		
		if(bkstartday == null && bkendday == null) //板块本身没有数据，无法找出对应的个股的数据，所以原路返回
			return stock;
		
		LocalDate stockstartday = stock.getNodeXPeroidData(period).getRecordsStartDate();
		LocalDate stockendday = stock.getNodeXPeroidData(period).getRecordsEndDate();
		
		if(stockstartday == null || stockendday == null ) { //还没有数据，直接找
			stock = bkdbopt.getGeGuZhanBiOfBanKuai (bankuai,stock, bkstartday, bkendday,period);
			Stock stockroot = stock.getStock(); //本身stock胡数据
			this.getStock(stockroot, bkstartday, bkendday, period);
//			bkdbopt.getStockZhanBi (stock.getStock(), bkstartday, bkendday,period);
		} else {
			HashMap<String,LocalDate> startend = null;
			if(period.equals(StockGivenPeriodDataItem.WEEK))
				startend = nodeWeekTimeStampRelation (stockstartday,stockendday,bkstartday,bkendday);
			else if(period.equals(StockGivenPeriodDataItem.DAY)) //暂时没开发
				;
			else if(period.equals(StockGivenPeriodDataItem.MONTH)) //暂时没开发
				;
			
			if(!startend.isEmpty()) {
				LocalDate searchstart,searchend,position;
				searchstart = startend.get("searchstart"); 
				searchend = startend.get("searchend");
				stock = bkdbopt.getGeGuZhanBiOfBanKuai (bankuai,stock,searchstart,searchend,period);
				this.getStock (stock.getStock(), searchstart, searchend,period);
			}
			
			startend = null;
		}
		
		return stock;

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
		BanKuai bankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode(bkcode,BanKuaiAndStockBasic.TDXBK);
		if(bankuai == null)
			return null;
		
		StockOfBanKuai stock = bankuai.getBanKuaiGeGu(stockcode);
		if(stock == null)
			return null;
		
		stock = this.getGeGuOfBanKuai( bankuai,  stock,period);

		return stock;
	}

	/*
	 * 同步大盘成交额
	 */
	public void getDaPan (LocalDate requiredrecordsday,String period)
	{
		BanKuai shdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("999999",BanKuaiAndStockBasic.TDXBK);
		BanKuai szdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("399001",BanKuaiAndStockBasic.TDXBK);
		
		NodeXPeriodDataBasic shdpnodexdata = shdpbankuai.getNodeXPeroidData(period);
		LocalDate bkstartday = shdpnodexdata.getRecordsStartDate();
		LocalDate bkendday = shdpnodexdata.getRecordsEndDate();
		
		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
		//大盘的start日期要比一般板块和个股早一些，这样做系统反应速度明显提高，具体原因不指定为什么。
		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange()+12,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		
		//判断时间的相关关系，以便决定是否需要到数据库中查询新纪录
		if(bkstartday == null || bkendday == null) { //还没有数据，直接找
			shdpbankuai = bkdbopt.getBanKuaiZhanBi (shdpbankuai,requirestart,requireend,period);
			szdpbankuai = bkdbopt.getBanKuaiZhanBi (szdpbankuai,requirestart,requireend,period);
		} else	{
			HashMap<String,LocalDate> startend = null;
			if(period.equals(StockGivenPeriodDataItem.WEEK))
				startend = nodeWeekTimeStampRelation (bkstartday,bkendday,requirestart,requireend);
			else if(period.equals(StockGivenPeriodDataItem.DAY)) //暂时没开发
				;
			else if(period.equals(StockGivenPeriodDataItem.MONTH)) //暂时没开发
				;
			
			LocalDate searchstart,searchend,position;
			if(!startend.isEmpty()) {
				searchstart = startend.get("searchstart"); 
				searchend = startend.get("searchend");
				shdpbankuai = bkdbopt.getBanKuaiZhanBi (shdpbankuai,searchstart,searchend,period);
				szdpbankuai = bkdbopt.getBanKuaiZhanBi (szdpbankuai,searchstart,searchend,period);
			}
			
			searchend = null;
		}
	}
	/*
	 * 确定时间点直接的关系,周线
	 */
	private  HashMap<String, LocalDate> nodeWeekTimeStampRelation (LocalDate curstart,LocalDate curend, LocalDate requiredstart, LocalDate requiredend) 
	{
		HashMap<String,LocalDate> startend = new HashMap<String,LocalDate> (); 
		if(  CommonUtility.isInSameWeek(curstart,requiredstart) &&  CommonUtility.isInSameWeek(requiredend,curend)    ) {//数据完整
			return startend;
		}	
		else if( (requiredstart.isAfter(curstart) || requiredstart.isEqual(curstart) ) && (requiredend.isBefore(curend) || requiredend.isEqual(curend) ) ) {  //数据完整
			return startend;
		}
		else if( !CommonUtility.isInSameWeek(curstart,requiredstart)  && requiredstart.isBefore(curstart) //部分完整1,前缺失
				&& (requiredend.isBefore(curend) || CommonUtility.isInSameWeek(requiredend,curend) )    ) {
			LocalDate searchstart,searchend;
			searchstart = requiredstart; 
			searchend = curstart.with(DayOfWeek.SATURDAY).minus(1,ChronoUnit.WEEKS);
			startend.put("searchstart", searchstart);
			startend.put("searchend",  searchend);
			return startend;
		}
		else if ( (CommonUtility.isInSameWeek(curstart,requiredstart) || requiredstart.isAfter(curstart) )  //部分完整2，后缺失
				&& requiredend.isAfter(curend) &&  !CommonUtility.isInSameWeek(requiredend,curend)    ) {
			LocalDate searchstart,searchend;
			searchstart = curend.with(DayOfWeek.MONDAY).plus(1,ChronoUnit.WEEKS); 
			searchend = requiredend;
			startend.put("searchstart", searchstart);
			startend.put("searchend", searchend);
			return startend;
		}
		else if( requiredstart.isBefore(curstart) && requiredend.isAfter(curend)  ) {//部分完整3， 前后双缺失，这种情况目前的设置似乎不可能发生，暂时不写
//			logger.info("部分完整3， 前后双缺失!");
			startend.put("searchstart", requiredstart);
			startend.put("searchend", requiredend);
			return startend;
		}

		return null;
	}
	//日线
	private  HashMap<String, LocalDate> nodeDayTimeStampRelation (LocalDate curstart,LocalDate curend, LocalDate requiredstart, LocalDate requiredend) 
	{
		HashMap<String,LocalDate> startend = new HashMap<String,LocalDate> (); 
		if(  curstart.isEqual(requiredstart) && requiredend.isEqual(curend)    ) {//数据完整
			return startend;
		}	
		else if( (requiredstart.isAfter(curstart) || requiredstart.isEqual(curstart) ) && (requiredend.isBefore(curend) || requiredend.isEqual(curend) ) ) {  //数据完整
			return startend;
		}
		else if( !curstart.isEqual(requiredstart)  && requiredstart.isBefore(curstart) //部分完整1,前缺失
				&& (requiredend.isBefore(curend) || requiredend.isEqual(curend) )    ) {
			LocalDate searchstart,searchend;
			searchstart = requiredstart; 
			searchend = curstart.with(DayOfWeek.SATURDAY).minus(1,ChronoUnit.WEEKS);
			startend.put("searchstart", searchstart);
			startend.put("searchend",  searchend);
			return startend;
		}
		else if ( (curstart.isEqual(requiredstart) || requiredstart.isAfter(curstart) )  //部分完整2，后缺失
				&& requiredend.isAfter(curend) &&  !requiredend.isEqual(curend)    ) {
			LocalDate searchstart,searchend;
			searchstart = curend.with(DayOfWeek.MONDAY).plus(1,ChronoUnit.WEEKS); 
			searchend = requiredend;
			startend.put("searchstart", searchstart);
			startend.put("searchend", searchend);
			return startend;
		}
		else if( requiredstart.isBefore(curstart) && requiredend.isAfter(curend)  ) {//部分完整3， 前后双缺失，这种情况目前的设置似乎不可能发生，暂时不写
			logger.debug("部分完整3， 前后双缺失，这种情况目前的设置似乎不可能发生");
			return startend;
		}

		return null;
	}
	/*
	 * 独立个股的指定周期的占比
	 */
	public Stock getStock (String stockcode,LocalDate requiredrecordsday,String period)
	{
		Stock stock = (Stock)this.treecyl.getSpecificNodeByHypyOrCode(stockcode,BanKuaiAndStockBasic.TDXGG);
		if(stock == null)
			return null;
		
		this.getStock(stock, requiredrecordsday, period);
		
		return stock;
	}
	public Stock getStock(Stock stock, LocalDate requirestart, LocalDate requireend,String period)
	{
		try{
			NodeXPeriodDataBasic stockxdata = stock.getNodeXPeroidData(period);
			LocalDate nodestartday = stockxdata.getRecordsStartDate();
			LocalDate nodeendday = stockxdata.getRecordsEndDate();
					
			if(nodestartday == null || nodeendday == null) { //还没有数据，直接找
				stock = bkdbopt.getStockZhanBi ((Stock)stock,requirestart,requireend,period); 
			} else {
				HashMap<String,LocalDate> startend = null ;
//				if(period.equals(ChenJiaoZhanBiInGivenPeriod.WEEK))
					startend = nodeWeekTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(ChenJiaoZhanBiInGivenPeriod.DAY)) 
//					startend = nodeDayTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(ChenJiaoZhanBiInGivenPeriod.MONTH)) //暂时没开发
					;
						 
				LocalDate searchstart,searchend,position;
				if(!startend.isEmpty()) {
							searchstart = startend.get("searchstart"); 
							searchend = startend.get("searchend");
							
							stock = bkdbopt.getStockZhanBi ((Stock)stock,searchstart,searchend,period);
				}
				
				startend = null;
			}
		} catch (java.lang.NullPointerException e) {
					e.printStackTrace();
		}
		
		return stock;
	}
	/*
	 * 
	 */
	public Stock getStock(Stock stock, LocalDate requiredrecordsday, String period)
	{
		//占比还是要看周线/月线，日线数据没有太大意义，下面专门找出个股周线占比数据
		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		stock = this.getStock(stock,requirestart,requireend,period);
		
		return stock;
	}
	/*
	 * 该函数为每一个独立个股设置制定周期的K线数据
	 */
	private BkChanYeLianTreeNode getNodeKXian (BkChanYeLianTreeNode bkorstock,LocalDate requiredrecordsday,String period) 
	{
		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		try{
			
			NodeXPeriodDataBasic stockxdata = bkorstock.getNodeXPeroidData(period);
			LocalDate nodestartday = stockxdata.getRecordsStartDate();
			LocalDate nodeendday = stockxdata.getRecordsEndDate();
			
			if(nodestartday == null || nodeendday == null) { //还没有数据，直接找
				bkorstock = bkdbopt.getNodeKXianZouShi (bkorstock,requirestart,requireend,period);
			} else {
				HashMap<String,LocalDate> startend = null ;
				if(period.equals(StockGivenPeriodDataItem.WEEK))
					startend = nodeWeekTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
				else if(period.equals(StockGivenPeriodDataItem.DAY)) 
					startend = nodeDayTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
				else if(period.equals(StockGivenPeriodDataItem.MONTH)) //暂时没开发
					;
				 
				LocalDate searchstart,searchend,position;
				if(!startend.isEmpty()) {
					searchstart = startend.get("searchstart"); 
					searchend = startend.get("searchend");

					bkorstock = bkdbopt.getNodeKXianZouShi (bkorstock,searchstart,searchend,period);
				}
				
				startend = null;
			}
		} catch (java.lang.NullPointerException e) {
			e.printStackTrace();
		}
		
		return bkorstock;
		
	}
	/*
	 * 
	 */
	public Stock getStockKXian (String stockcode,LocalDate requiredrecordsday,String period)
	{
		Stock stock = (Stock)this.treecyl.getSpecificNodeByHypyOrCode(stockcode,BanKuaiAndStockBasic.TDXGG);
		stock = (Stock) this.getNodeKXian(stock, requiredrecordsday, period);
		return stock;
	}
	/*
	 * 
	 */
	public Stock getStockKXian (Stock stock,LocalDate requiredrecordsday,String period)
	{
		stock = (Stock) this.getNodeKXian(stock, requiredrecordsday, period);
		return stock;
		
//		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		
//		try{
//			
//			NodeXPeriodDataBasic stockxdata = stock.getNodeXPeroidData(period);
//			LocalDate nodestartday = stockxdata.getRecordsStartDate();
//			LocalDate nodeendday = stockxdata.getRecordsEndDate();
//			
//			if(nodestartday == null || nodeendday == null) { //还没有数据，直接找
//				stock = (Stock)bkdbopt.getNodeKXianZouShi (stock,requirestart,requireend,period);
//			} else {
//				HashMap<String,LocalDate> startend = null ;
//				if(period.equals(StockGivenPeriodDataItem.WEEK))
//					startend = nodeWeekTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(StockGivenPeriodDataItem.DAY)) 
//					startend = nodeDayTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(StockGivenPeriodDataItem.MONTH)) //暂时没开发
//					;
//				 
//				LocalDate searchstart,searchend,position;
//				if(!startend.isEmpty()) {
//					searchstart = startend.get("searchstart"); 
//					searchend = startend.get("searchend");
//
//					stock = (Stock)bkdbopt.getNodeKXianZouShi (stock,searchstart,searchend,period);
//				}
//				
//				startend = null;
//			}
//		} catch (java.lang.NullPointerException e) {
//			e.printStackTrace();
//		}
//		
//		return stock;
	}


}
