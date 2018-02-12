package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

//import com.exchangeinfomanager.bankuaichanyelian.TwelveZhongDianGuanZhuXmlHandler;
import com.exchangeinfomanager.bankuaifengxi.ChenJiaoZhanBiInGivenPeriod;
import com.exchangeinfomanager.commonlib.CommonUtility;

public class BanKuai extends BkChanYeLianTreeNode
{

	public static String  HASGGWITHSELFCJL = "HASGGWITHSELFCJL" , HASGGNOSELFCJL = "HASGGNOSELFCJL"
						, NOGGWITHSELFCJL = "NOGGWITHSELFCJL" , NOGGNOSELFCJL = "NOGGNOSELFCJL"; // 通达信里面定义的板块有几种：1.有个股自身有成交量数据 2. 有个股自身无成交量数据 3.无个股自身有成交量数据
	
	public BanKuai(String bkcode,String name ) 
	{
		super(bkcode,name);
		nodetype = BanKuaiAndStockBasic.TDXBK;
	}
	
	private static Logger logger = Logger.getLogger(BanKuai.class);
	private DaPan dapan;
	private HashMap<String, Stock> allbkge;
	
	private String bankuaileixing; // 通达信里面定义的板块有几种：1.有个股自身有成交量数据 2. 有个股自身无成交量数据 3.无个股自身有成交量数据 
	private boolean notexporttogehpi = false;

	 /**
	 * @return the tmpallbkge
	 */
	public HashMap<String, Stock> getAllBanKuaiGeGu() {
		return allbkge;
	}
	/*
	 * 
	 */
	public void setBanKuaiLeiXing (String leixing)
	{
		if(leixing == null)
			this.bankuaileixing = this.NOGGNOSELFCJL;
		else if(leixing.equals(this.HASGGNOSELFCJL) || leixing.equals(this.HASGGWITHSELFCJL) || leixing.equals(this.NOGGWITHSELFCJL) || leixing.equals(this.NOGGNOSELFCJL) )
			this.bankuaileixing = leixing;
	}
	public String getBanKuaiLeiXing ()
	{
		return this.bankuaileixing;
	}
	/*
	 * 只返回制定日期当周有成交量的个股,有成交量才说明可能是该板块的个股
	 */
	public HashMap<String, Stock> getSpecificPeriodBanKuaiGeGu(LocalDate requireddate) {
		if(this.allbkge == null || this.allbkge.isEmpty())
			return null;
		
		LocalDate bkstart = this.getWkRecordsStartDate();
		LocalDate bkend = this.getWkRecordsEndDate();
		if(requireddate.isBefore(bkstart) || requireddate.isAfter(bkend) ) //当前没有该日期的记录
			return null;

		HashMap<String, Stock> result = new HashMap<String, Stock> ();
		for (Map.Entry<String, Stock> entry : allbkge.entrySet()) {  
			  Stock stock = entry.getValue();
			  ChenJiaoZhanBiInGivenPeriod records = stock.getSpecficChenJiaoErRecord(requireddate);
			  if(records != null)
				  result.put(stock.getMyOwnCode(), stock);
		}
		
		return result;
	}
	/*
	 * 获得某一个个股
	 */
	public Stock getBanKuaiGeGu (String stockcode)
	{
		if(this.allbkge == null) 
			this.allbkge = new HashMap<String, Stock> ();
		
		try {
			Stock stock = this.allbkge.get(stockcode);
			if(stock == null) {
				stock = new Stock (stockcode, "");
				stock.setMyUpBanKuai(this); //把板块的成交量放入，后用
				this.allbkge.put(stockcode, stock);
//				stock.setMyUpBanKuai(this); //把板块的成交量放入，后用
				return stock;
			} else
				return stock;
		} catch ( java.lang.NullPointerException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	/*
	 * 
	 */
	public Boolean isBanKuaiGeGu (Stock stock)
	{
		if(this.allbkge == null) 
			return false;
		
		try {
			String stockcode = stock.getMyOwnCode();
			Stock tmpstock = this.allbkge.get(stockcode);
			if(tmpstock == null) 
				return false;
			 else
				return true;
		} catch ( java.lang.NullPointerException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	/*
	 * 
	 */
	public void addNewBanKuaiGeGu (Stock stock) {
		if(this.allbkge == null) 
			this.allbkge = new HashMap<String, Stock> ();
		
		if(!this.isBanKuaiGeGu (stock) ) {
			String stockcode = stock.getMyOwnCode();
			stock.setMyUpBanKuai(this); //把板块的成交量放入，后用
			this.allbkge.put(stockcode, stock);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode#setParseFileStockSet(java.util.HashSet)
	 */
//	public  void setParseFileStockSet (HashSet<String> parsefilestockset2)
//	 {
//	    	if(super.parsefilestockset == null) {
//	    		this.parsefilestockset = new HashSet<String> ();
//	    		this.parsefilestockset = parsefilestockset2;
//	    	} else
//	    		this.parsefilestockset = parsefilestockset2;
//	 }
	/*
	 * GEPHI part
	 */
//	public void setNotExportToGephi ()
//	 {
//		 this.notexporttogehpi = true;
//	 }
//	 public boolean shouldNotExportToGephi ()
//	 {
//		 return this.notexporttogehpi;
//	 }
	 /*
	  * 
	  */
	 public void setDaPan (DaPan dp)
	 {
		 this.dapan = dp; //大盘成交记录
	 }
	 /*
	  * 
	  */
	 protected Boolean isThisWeekDaPanXiuShi (LocalDate date) 
	 {
		 if(this.dapan.isThisWeekXiuShi (date) )
			 return true;
		 else
			 return false;
	 }
//		/*
//		 * 判断本周是停牌后复牌
//		 */
//		protected Boolean checkIsFuPaiAfterTingPai (LocalDate requireddate )
//		{
//			Integer index = this.getRequiredRecordsPostion (this.wkcjeperiodlist,requireddate);
//			if( index != null) {
//				ChenJiaoZhanBiInGivenPeriod curcjlrecord = wkcjeperiodlist.get(index);
//				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = wkcjeperiodlist.get(index -1 );
//				LocalDate curweeknum = curcjlrecord.getRecordsDayofEndofWeek();
//				LocalDate lastweeknum = lastcjlrecord.getRecordsDayofEndofWeek() ;
//				long diffwknum = ChronoUnit.WEEKS.between (curweeknum,lastweeknum);
//			 
//				if(diffwknum > 2 ) { //说明是停牌的复牌  
//					return true;	
//				} else if(diffwknum == 2) { //可能是大盘休市造成的，
//					LocalDate dapanlastweekdate = curweeknum.with(DayOfWeek.MONDAY).minus(1,ChronoUnit.WEEKS);
//					if( this.isThisWeekDaPanXiuShi(dapanlastweekdate) )
//						return false;
//					else 
//						return true;
//				} else
//					return false;
//			}
//			
//			return null;
//		}
		
		/*
		 * 计算给定周的成交额占比增速
		 */
		public Double getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (LocalDate requireddate)
		{
			if(wkcjeperiodlist == null)
				return null;
			
			Integer index = super.getRequiredRecordsPostion (requireddate);
			if( index != null) {
					if( super.getChengJiaoErDifferenceOfLastWeek(requireddate) == null && super.wkcjeperiodlist.get(index) != null ) {
						
//							logger.debug(this.getMyOwnCode() + this.getMyOwnName() + "可能是一个新个股或板块");
						return 100.0;
					}
				
					ChenJiaoZhanBiInGivenPeriod curcjlrecord = wkcjeperiodlist.get(index);
					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = wkcjeperiodlist.get(index -1 );
						
					Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
					Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
					double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
//					logger.debug(super.getMyOwnCode() + super.getMyOwnName() + "占比增速" + requireddate.toString() + zhanbigrowthrate);
					return zhanbigrowthrate;
			}
			
			logger.debug(this.getMyOwnCode() + this.getMyOwnName() + requireddate.toString() + "占比增速为null");
			return null;
		}
		/*
		 * 计算给定周的成交额占比是多少周期内的最大占比
		 */
		public Integer getChenJiaoLiangZhanBiMaxWeekForAGivenPeriod (LocalDate requireddate)
		{
			if(wkcjeperiodlist == null)
				return null;

			int maxweek = 0;
			Integer index = super.getRequiredRecordsPostion (requireddate);
			if( index != null) {
				if( super.getChengJiaoErDifferenceOfLastWeek(requireddate) == null && super.wkcjeperiodlist.get(index) != null ) {
					if(index != 0)
//						logger.debug(this.getMyOwnCode() + this.getMyOwnName() + "可能是一个新个股或板块");
					return 0;
				}

				ChenJiaoZhanBiInGivenPeriod curcjlrecord = wkcjeperiodlist.get(index);
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord =  null;
					
				Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
				for(int i = index -1;i>=0;i--) {
						lastcjlrecord = wkcjeperiodlist.get(i );
						Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
						if(curzhanbiratio > lastzhanbiratio)
							maxweek ++;
						else
							break;
				}
			}
			
			return maxweek;
		}
	 	/*
		 * 计算成交额变化贡献率
		 */
		public Double getChenJiaoErChangeGrowthRateForAGivenPeriod (LocalDate requireddate)
		{
			if(wkcjeperiodlist == null)
				return null;
			
			//判断上级板块(大盘或者板块)是否缩量,所以了没有比较的意义，直接返回-100；
//			LocalDate tplastdate = curweeknum.minus(1,ChronoUnit.WEEKS);

			Double dpcjediff = this.dapan.getChengJiaoErDifferenceOfLastWeek(requireddate);
			if( dpcjediff<0 || dpcjediff == null ) //大盘缩量，
				return -100.0;
			
			Integer index = super.getRequiredRecordsPostion (requireddate);
			if( index != null) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = wkcjeperiodlist.get(index); //自己当前周成交量

				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = null;
				try {
					 lastcjlrecord = wkcjeperiodlist.get(index -1 );
				} catch ( java.lang.ArrayIndexOutOfBoundsException e) {
					logger.debug(this.getMyOwnCode() + this.getMyOwnName() + "可能是个新板块" );
				
					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //停牌后所有成交量都应该计算入
					return curggcje/dpcjediff;
				}

				//正常板块
				Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
				Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
				Double cjechange = curcje - lastcje; //个股成交量的变化
				
				return cjechange/dpcjediff;
			}

			return -100.0;
		}
		/*
		 * 计算板块和大盘成交量的各个数据
		 */
		public ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate (LocalDate requireddate)
		{
			if(wkcjeperiodlist == null)
				return null;
			
//			NodeFengXiData nodefx = new NodeFengXiData (super.getMyOwnCode(),requireddate);
			Double dpcjediff = this.dapan.getChengJiaoErDifferenceOfLastWeek(requireddate);
			
			Integer index = super.getRequiredRecordsPostion (requireddate);
			if( index != null) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = wkcjeperiodlist.get(index); //自己当前周成交量
				
				if( super.getChengJiaoErDifferenceOfLastWeek(requireddate) == null && super.wkcjeperiodlist.get(index) != null ) {
//					logger.debug(this.getMyOwnCode() + this.getMyOwnName() + "可能是一个新个股或板块");
					//计算成交额变化贡献率
					if( dpcjediff<0 || dpcjediff == null ) {//大盘缩量，
						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
					} else {
						Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //停牌后所有成交量都应该计算入
						curcjlrecord.setGgbkcjegrowthzhanbi( curggcje/dpcjediff);
					}
					//计算给定周的成交额占比是多少周期内的最大占比
					curcjlrecord.setGgbkzhanbimaxweek(0);
					//计算给定周的成交额占比增速
					curcjlrecord.setGgbkzhanbigrowthrate(0.0);
					//成交额是多少周期内的最大
					curcjlrecord.setGgbkcjemaxweek(0);
					
					return curcjlrecord;
				} else {
					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = wkcjeperiodlist.get(index -1 );
					////计算成交额变化贡献率 
					if( dpcjediff<0 ) {//大盘缩量，
						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
					} else {
//						Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
//						Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
//						
//						Double cjechange = curcje - lastcje; //个股成交量的变化
						Double cjechange = super.getChengJiaoErDifferenceOfLastWeek(requireddate);
						curcjlrecord.setGgbkcjegrowthzhanbi(cjechange/dpcjediff);
					}
					
					//计算给定周的成交额占比增速
					Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
					Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
					double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
					curcjlrecord.setGgbkzhanbigrowthrate(zhanbigrowthrate);
					
					//计算给定周的成交额占比是多少周期内的最大占比
					Integer maxweek = 0;
					for(int i = index -1;i>=0;i--) {
							lastcjlrecord = wkcjeperiodlist.get(i );
							lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
							if(curzhanbiratio > lastzhanbiratio)
								maxweek ++;
							else
								break;
					}
					curcjlrecord.setGgbkzhanbimaxweek(maxweek);
					
					//成交额是多少周期内的最大
					maxweek = 0;
					Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
					for(int i = index -1;i>=0;i--) {
						lastcjlrecord = wkcjeperiodlist.get(i );
						Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
						if(curcje > lastcje)
							maxweek ++;
						else
							break;
					}
					curcjlrecord.setGgbkcjemaxweek(maxweek);
					
					return curcjlrecord;
				}
				
			}
			
			return null;
		}
		

}
