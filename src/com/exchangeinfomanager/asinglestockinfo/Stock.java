package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.asinglestockinfo.BanKuai.BanKuaiNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.TreeRelated;
import com.google.common.base.Strings;

public class Stock extends BkChanYeLianTreeNode {

	public Stock(String myowncode1,String name)
	{
		super(myowncode1,name);
		super.nodetype = BanKuaiAndStockBasic.TDXGG;
		
		if(myowncode1.startsWith("60"))
			super.setSuoShuJiaoYiSuo("sh");
		else
			super.setSuoShuJiaoYiSuo("sz");
		
//		suoShuTdxBanKuaiData = new HashMap<String,StockOfBanKuai> ();
		
		super.nodewkdata = new StockNodeXPeriodData (StockGivenPeriodDataItem.WEEK) ;
		super.nodedaydata = new StockNodeXPeriodData (StockGivenPeriodDataItem.DAY) ;
//		super.nodemonthdata = new StockNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
		super.nodetreerelated = new TreeRelated (this);
		
	}
	
	private String checklistXml;
	private static Logger logger = Logger.getLogger(Stock.class);
	
//	private Multimap<String> chiCangAccountNameList; //持有该股票的所有账户的名字
	private HashMap<String,AccountInfoBasic> chiCangAccounts; //持有该股票的所有账户<账户，账户信息>
//	private HashMap<String,StockOfBanKuai> suoShuTdxBanKuaiData; //所属通达信系统板块 <板块code,板块名字>
//	private  HashMap<String,Integer> sysBanKuaiWeight; //所属通达信系统板块权重
//	private HashSet<String> suoShuZdyBanKuai; //所属自定义板块
	private HashMap<String, String> suoShuCurSysBanKuai; //个股当前所属的通达信板块
	
//	private DaPan dapan;
//	public NodeXPeriodDataBasic getNodeXPeroidData (String period)
//	{
//		if(period.equals(StockGivenPeriodDataItem.WEEK))
//			return nodewkdata;
//		else if(period.equals(StockGivenPeriodDataItem.MONTH))
//			return nodemonthdata;
//		else if(period.equals(StockGivenPeriodDataItem.DAY))
//			return nodedaydata;
//		else 
//			return null;
//	}
	
	public void addNewChiCangAccount (AccountInfoBasic acnt)
	{
		if(chiCangAccounts == null)
			chiCangAccounts = new HashMap<String,AccountInfoBasic>();
		try {
			String acntname = acnt.getAccountName();
			if(!chiCangAccounts.containsKey(acntname))
				chiCangAccounts.put(acntname, acnt);
		} catch(java.lang.NullPointerException e) {
			e.printStackTrace();
			
		}
	}
	
	public void removeChiCangAccount (AccountInfoBasic acnt)
	{
		String acntname = acnt.getAccountName();
		chiCangAccounts.remove(acntname);
	}
	
	/**
	 * @return the chiCangAccountNameList
	 */
	public HashMap<String, AccountInfoBasic> getChiCangAccounts () {
		return chiCangAccounts;
	}
	public ArrayList<String> getChiCangAccountsNameList() 
	{
		
		return new  ArrayList<String>(chiCangAccounts.keySet());
	}

	/**
	 * @return the checklistXml
	 */
	public String getChecklistXml() {
		return checklistXml;
	}
	/**
	 * @param checklistXml the checklistXml to set
	 */
	public void setChecklistXml(String checklistXml) {
		this.checklistXml = checklistXml;
	}

	/*
	 * 
	 */
	public boolean isInTdxBanKuai (String tdxbk)
	{
		if(this.suoShuCurSysBanKuai.containsKey(tdxbk))
			return true;
		else 
			return false;
	}
	/**
	 * @return the suoShuBanKuai
	 */
	public HashMap<String,String> getGeGuCurSuoShuTDXSysBanKuaiList() {
		return suoShuCurSysBanKuai;
	}

	/**
	 * @param tmpsysbk the suoShuBanKuai to set
	 */
	public void setGeGuCurSuoShuTDXSysBanKuaiList(HashMap<String,String> tmpsysbk) {
		this.suoShuCurSysBanKuai = tmpsysbk;
	}
 
	
	/**
	 * @return the suoShuBanKuai
	 */
//	public HashSet<String> getSuoShuTDXZdyBanKuai() {
//		return suoShuZdyBanKuai;
//	}

	/**
	 * @param tmpbk the suoShuBanKuai to set
	 */
//	public void setSuoShuTDXZdyBanKuai(HashSet<String> tmpbk) {
//		this.suoShuZdyBanKuai = tmpbk;
//	}
	
	ArrayList<String> nodeallchanyelian ;
	
	public void setGeGuAllChanYeLianInfo(ArrayList<String> ggcyl) 
	{
		this.nodeallchanyelian = ggcyl;
	}
	public ArrayList<String> getGeGuAllChanYeLianInfo() 
	{
		return this.nodeallchanyelian;
	}
	/*
	 * 
	 */
	protected Boolean isTingPai (LocalDate requireddate,int difference, String period )
	{
		NodeXPeriodDataBasic nodexdate = this.getNodeXPeroidData(period);
		if( !nodexdate.hasRecordInThePeriod(requireddate, difference) ) {
			boolean dapanxiushi = ((DaPan)getRoot()).isDaPanXiuShi(requireddate,difference,period );
			if(dapanxiushi)
				return false;
			else
				return true;
		} else
			return false;
	}
	
	
	/*
	 * 判断本周期是停牌后复牌
	 */
//	protected Boolean isFuPaiAfterTingPai (LocalDate requireddate,int difference,String period )
//	{
//		NodeXPeriodDataBasic nodexdate = this.getNodeXPeroidData(period);
//		 StockGivenPeriodDataItem nodexcurdatedata = nodexdate.getSpecficRecord(requireddate, difference);
//		if( nodexcurdatedata == null) 
//			return null;
//		
//		int index = -1;
//		while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,period) ) { //上周可能大盘修饰
//			index --;
//		}
//		
//		 StockGivenPeriodDataItem lastcjlrecord = nodexdate.getSpecficRecord (requireddate,index);
//		if(lastcjlrecord == null) {//休市前还是空，说明要么是新板块要么停牌的
//			return true;
//		} else 
//			return false;
//	}
	 
	
	/*
	 * stock 的这个内部类只针对自身对大盘的数据，自身对板块的数据在StockOfBanKuai
	 */
	public class StockNodeXPeriodData extends NodeXPeriodData
	{
		public StockNodeXPeriodData (String nodeperiodtype1) 
		{
			super(nodeperiodtype1);
			stockhuanshoulv = new TimeSeries(nodeperiodtype1);
			stockliutongshizhi = new TimeSeries(nodeperiodtype1);
			stockzongshizhi = new TimeSeries(nodeperiodtype1);
		}
		
		private  TimeSeries stockhuanshoulv; //换手率
		private  TimeSeries stockliutongshizhi; //流通市值
		private  TimeSeries stockzongshizhi; //总市值
		/*
		* (non-Javadoc)
		* @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#addNewXPeriodData(com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem)
		*/
		public Boolean isVeryVeryNewXinStock ()
		{
			if(super.getNodeperiodtype().equals(StockGivenPeriodDataItem.WEEK)) {
				if(super.stockamo.getItemCount() >=4)
					return false;
				else 
					return true;
			} else if(super.getNodeperiodtype().equals(StockGivenPeriodDataItem.DAY)) {
				if(super.stockamo.getItemCount() >= 20)
					return false;
				else 
					return true;
			}  else if(super.getNodeperiodtype().equals(StockGivenPeriodDataItem.MONTH)) {
				if(super.stockamo.getItemCount() > 1)
					return false;
				else 
					return true;
			}
			return null;
		}
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#addNewXPeriodData(com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem)
		 */
		public void addNewXPeriodData (StockGivenPeriodDataItem kdata)
		{
			try {
				stockohlc.setNotify(false);
				stockohlc.add(kdata);
			} catch (org.jfree.data.general.SeriesException e) {
//				System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
			}
			try {
				stockamo.setNotify(false);
				stockamo.add(kdata.getPeriod(),kdata.getMyOwnChengJiaoEr(),false);
//				stockvol.add(kdata.getPeriod(),kdata.getMyownchengjiaoliang(),false);
				if(kdata.getCjeZhanBi() != null) {
					stockamozhanbi.setNotify(false);
					stockamozhanbi.add(kdata.getPeriod(),kdata.getCjeZhanBi(),false);
				}
//				if(kdata.getCjlZhanBi() != null)
//					stockvolzhanbi.add(kdata.getPeriod(),kdata.getCjlZhanBi(),false);
			} catch (org.jfree.data.general.SeriesException e) {
//				System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//				e.printStackTrace();
			}
			
			try {
				stockhuanshoulv.setNotify(false);
				stockhuanshoulv.add(kdata.getPeriod(),kdata.getHuanshoulv(),false);
			} catch (org.jfree.data.general.SeriesException e) {
//				System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//				e.printStackTrace();
			} 
			try {
				stockliutongshizhi.setNotify(false);
				stockliutongshizhi.add(kdata.getPeriod(),kdata.getLiutongshizhi(),false);
			} catch (org.jfree.data.general.SeriesException e) {
//				System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//				e.printStackTrace();
			}
			try {	
				stockzongshizhi.setNotify(false);
				stockzongshizhi.add(kdata.getPeriod(),kdata.getZongshizhi(),false);
				
			} catch (org.jfree.data.general.SeriesException e) {
//				System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//				e.printStackTrace();
			}
		}
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
		 */
		public Double getSpecificTimeHuanShouLv (LocalDate requireddate,int difference)
		{
			try{
				TimeSeriesDataItem curhslrecord = stockhuanshoulv.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
				Double curhsl = curhslrecord.getValue().doubleValue();
				
				return curhsl;
			} catch (java.lang.NullPointerException e) {
				e.printStackTrace();
				return null;
			}
			
		}
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
		 */
		public double getSpecificTimeZongShiZhi (LocalDate requireddate,int difference)
		{
			TimeSeriesDataItem curzszrecord = stockzongshizhi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			Double curzsz = null ;
			try {
				curzsz = curzszrecord.getValue().doubleValue();
			} catch (Exception e) {
				e.printStackTrace();
				logger.info(" ");
			}
			
			return curzsz;
		}
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
		 */
		public Double getSpecificTimeLiuTongShiZhi (LocalDate requireddate,int difference)
		{
			TimeSeriesDataItem curltszrecord = stockliutongshizhi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			try{
				Double curltsz = curltszrecord.getValue().doubleValue();
				return curltsz;
			} catch (java.lang.NullPointerException e ) {
				return null;
			}
		}
		@Override
		public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference) 
		{
			if(stockohlc == null)
				return null;
			
			TimeSeriesDataItem curcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			if( curcjlrecord == null) 
				return null;
			
			Double curcje = curcjlrecord.getValue().doubleValue();
			int maxweek = 0;
			
			DaPan dapan = (DaPan)getRoot();
			for(int index = -1;index >= -100; index--) { //目前记录不可能有10000个周期，所以10000足够
				if( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype())  ) //大盘还可能休市
					continue;
				
				if(isTingPai(requireddate,index,getNodeperiodtype()))
					return maxweek;
				
				TimeSeriesDataItem lastcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod (requireddate,index) );
				if(lastcjlrecord == null ) //可能到了记录的头部了，或者是个诞生时间不长的板块
					return maxweek;
				
				Double lastcje = lastcjlrecord.getValue().doubleValue();
				if(curcje > lastcje)
					maxweek ++;
				else
					break;
			}
		
			return maxweek;
		}
		/*
		 * 对上级板块的成交额占比是多少周内的最大值
		 */
		public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference) 
		{
			if(stockohlc == null)
				return null;
			
			TimeSeriesDataItem curcjlrecord = this.stockamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			if( curcjlrecord == null) 
				return null;

			DaPan dapan = (DaPan)getRoot();
			
			Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
			int maxweek = 0;
			for(int index = -1;index >= -100000; index--) {
				if( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype())  ) { //大盘还可能休市
//					logger.debug("dapan停牌");
					continue;
				}
				
				if(isTingPai(requireddate,index,getNodeperiodtype())) //本周也可能停牌，停牌前的就不计算了
					return maxweek;
				
				TimeSeriesDataItem lastcjlrecord = stockamozhanbi.getDataItem( getJFreeChartFormateTimePeriod (requireddate,index) );
				if(lastcjlrecord == null ) //可能到了记录的头部了，或者是个诞生时间不长的板块
					return maxweek;

				Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
				if(curzhanbiratio > lastzhanbiratio)
					maxweek ++;
				else
					break;
			}

			return maxweek;
		}
	}
}

