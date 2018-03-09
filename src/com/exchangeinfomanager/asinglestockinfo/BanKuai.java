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

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.commonlib.CommonUtility;

public class BanKuai extends BkChanYeLianTreeNode
{

	public static String  HASGGWITHSELFCJL = "HASGGWITHSELFCJL" , HASGGNOSELFCJL = "HASGGNOSELFCJL"
						, NOGGWITHSELFCJL = "NOGGWITHSELFCJL" , NOGGNOSELFCJL = "NOGGNOSELFCJL"; // 通达信里面定义的板块有几种：1.有个股自身有成交量数据 2. 有个股自身无成交量数据 3.无个股自身有成交量数据
	
	public BanKuai(String bkcode,String name ) 
	{
		super(bkcode,name);
		super.nodetype = BanKuaiAndStockBasic.TDXBK;
		
		super.nodewkdata = new BanKuaiNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.WEEK) ;
		super.nodedaydata = new BanKuaiNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.DAY) ;
		super.nodemonthdata = new BanKuaiNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.MONTH) ;
	}
	
	private static Logger logger = Logger.getLogger(BanKuai.class);
	private HashMap<String, Stock> allbkge;
	private String bankuaileixing; // 通达信里面定义的板块有几种：1.有个股自身有成交量数据 2. 有个股自身无成交量数据 3.无个股自身有成交量数据 
	private boolean notexporttogehpi = false;

	public NodeXPeriodData getNodeXPeroidData (String period)
	{
		if(period.equals(ChenJiaoZhanBiInGivenPeriod.WEEK))
			return nodewkdata;
		else if(period.equals(ChenJiaoZhanBiInGivenPeriod.MONTH))
			return nodemonthdata;
		else if(period.equals(ChenJiaoZhanBiInGivenPeriod.DAY))
			return nodedaydata;
		else 
			return null;
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
	public String getBanKuaiLeiXing ()	{
		return this.bankuaileixing;
	}
	 /**
	 * @return the tmpallbkge
	 */
	public HashMap<String, Stock> getAllCurrentBanKuaiGeGu() {
		return allbkge;
	}
	/*
	 * 只返回指定周期有成交量的个股,有成交量才说明可能是该板块的个股,没有成交量说明要不已经不是该板块的个股，要不就是停牌
	 */
	public HashMap<String, Stock> getSpecificPeriodBanKuaiGeGu(LocalDate requireddate,String period) 
	{
		if(this.allbkge == null || this.allbkge.isEmpty())
			return null;
		
		LocalDate bkstart = getNodeXPeroidData(period).getRecordsStartDate();
		LocalDate bkend = getNodeXPeroidData(period).getRecordsEndDate();
		if(requireddate.isBefore(bkstart) || requireddate.isAfter(bkend) ) //当前没有该日期的记录
			return null;

		HashMap<String, Stock> result = new HashMap<String, Stock> ();
		for (Map.Entry<String, Stock> entry : allbkge.entrySet()) {  
			  Stock stock = entry.getValue();
			  NodeXPeriodData stockxperioddata = stock.getStockXPeriodDataForABanKuai(this.getMyOwnCode(),period);
			  if(stockxperioddata != null) {
				  ChenJiaoZhanBiInGivenPeriod records = stockxperioddata.getSpecficRecord(requireddate, 0);
				  if(records != null)
					  result.put(stock.getMyOwnCode(), stock);
			  }
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
				return null;
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
	private Boolean isBanKuaiGeGu (Stock stock)
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
	 
	public class BanKuaiNodeXPeriodData extends NodeXPeriodData
	{
			public BanKuaiNodeXPeriodData (String nodeperiodtype1)
			{
				super(nodeperiodtype1);
			}

			@Override
			public ChenJiaoZhanBiInGivenPeriod getSpecficRecord(LocalDate requireddate, int difference) 
			{
				if(periodlist == null)
					return null;

				String nodeperiod = super.getNodeperiodtype();
				LocalDate expectedate = null;
				if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.WEEK)) { //如果是周线数据，只要数据周相同，即可返回
					expectedate = requireddate.plus(difference,ChronoUnit.WEEKS);
				} else if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.DAY)) { //如果是日线数据，必须相同才可返回
					expectedate = requireddate.plus(difference,ChronoUnit.DAYS);
				}  else if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.MONTH)) { //如果是日线数据，必须相同才可返回
					expectedate = requireddate.plus(difference,ChronoUnit.MONTHS);
				}
				
				int index = -1;
				ChenJiaoZhanBiInGivenPeriod foundrecord = null;
				for(ChenJiaoZhanBiInGivenPeriod tmpcjzb : super.periodlist) {
					if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.MONTH)) { //如果是周线数据，只要数据周相同，即可返回
						index ++;
						
						int year = expectedate.getYear();
						int month = expectedate.getMonthValue();
						
						int yearnum = tmpcjzb.getRecordsYear();
						int monthnum = tmpcjzb.getRecordsMonth();
						
						if(month == monthnum && yearnum == year) {
							foundrecord = tmpcjzb;
							break;
						}
					} else	if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.WEEK)) { //如果是周线数据，只要数据周相同，即可返回
						index ++;
						
						int year = expectedate.getYear();
						WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
						int weekNumber = expectedate.get(weekFields.weekOfWeekBasedYear());
						
						int yearnum = tmpcjzb.getRecordsYear();
						int wknum = tmpcjzb.getRecordsWeek();
						
						if(wknum == weekNumber && yearnum == year) {
							foundrecord = tmpcjzb;
							break;
						}
					} else if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.DAY)) { //如果是日线数据，必须相同才可返回
						index ++;
						LocalDate recordsday = tmpcjzb.getRecordsDayofEndofWeek();
						if(recordsday.isEqual(requireddate)) {
							foundrecord = tmpcjzb;
							break;
						}
					}
				}
				
				return foundrecord;
			}

			@Override
			public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate)
			{
				if(periodlist == null)
					return null;
				
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
				if( curcjlrecord == null) 
					return null;
				
				int index = -1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //上周可能大盘修饰
					index --;
				}
				
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
				if(lastcjlrecord == null) //休市前还是空，说明要是新板块。板块没有停牌的
					return null;
				
				Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
				Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
				
				curcjlrecord.setGgBkCjeDifferenceWithLastPeriod (curcje - lastcje);
				return curcje - lastcje;
			}

			@Override
			public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate) 
			{
				if(periodlist == null)
					return null;
				
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
				if( curcjlrecord == null) 
					return null;
				
				int index = -1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //上周可能大盘修饰
					index --;
				}
				
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
				if(lastcjlrecord == null) { //休市前还是空，说明要是新板块。板块没有停牌的
					logger.debug(getMyOwnCode() + getMyOwnName() + "可能是一个新个股或板块");
					return 100.0;
				}
				
				Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
				Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
				Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
				logger.debug(getMyOwnCode() + getMyOwnName() + "占比增速" + requireddate.toString() + zhanbigrowthrate);
				
				curcjlrecord.setGgBkCjeZhanbiGrowthRate(zhanbigrowthrate);
				return zhanbigrowthrate;
			}

			@Override
			public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate) 
			{
				if(periodlist == null)
					return null;
				
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
				if( curcjlrecord == null) 
					return null;
				
				int index = -1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //上周可能大盘修饰
					index --;
				}
				
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
				if(lastcjlrecord == null) { //休市前还是空，说明要是新板块。板块没有停牌的
					logger.debug(getMyOwnCode() + getMyOwnName() + "可能是一个新个股或板块");
					return 100;
				}
				
				Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
				int maxweek = 0;
				for(;index >= -100000; index--) {
					lastcjlrecord = this.getSpecficRecord (requireddate,index);
					
					if(lastcjlrecord == null) //要不是到了记录的最头部
						return maxweek;
					
					Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
					if(curzhanbiratio > lastzhanbiratio)
						maxweek ++;
					else
						break;
				}
				
				curcjlrecord.setGgBkCjeZhanbiMaxweek(maxweek);
				return maxweek;
			}

			@Override
			public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(LocalDate requireddate) 
			{
				if(periodlist == null)
					return null;
				
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
				if( curcjlrecord == null) 
					return null;
				
				//判断上级板块(大盘或者板块)是否缩量,所以了没有比较的意义，直接返回-100；
				String nodept = super.getNodeperiodtype();
				Double dpcjediff = ((DaPan)getRoot()).getNodeXPeroidData(nodept).getChengJiaoErDifferenceWithLastPeriod(requireddate);
				if( dpcjediff<0 || dpcjediff == null ) {//大盘缩量，
					curcjlrecord.setGgBkCjeGrowthRateToSuperBanKuaiCjeGrowth (-100.0);
					return -100.0;
				}
				
				int index = -1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //上周可能大盘修饰
					index --;
				}
				
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
				if(lastcjlrecord == null) { //休市前还是空，说明要是新板块。板块没有停牌的
					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //新板块所有成交量都应该计算入
					return curggcje/dpcjediff;
				}
				
				Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
				Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
				Double cjechange = curcje - lastcje; //个股成交量的变化
				
				curcjlrecord.setGgBkCjeGrowthRateToSuperBanKuaiCjeGrowth (cjechange/dpcjediff);
				return cjechange/dpcjediff;
			}

			@Override
			public ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate(LocalDate requireddate) 
			{ 	
				return null;
//				if(periodlist == null)
//					return null;
//				
//				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
//				if( curcjlrecord == null) 
//					return null;
//				
//				this.getChengJiaoErDifferenceWithLastPeriod(requireddate);
//				this.getChenJiaoErChangeGrowthRateOfSuperBanKuai(requireddate);
//				this.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(requireddate);
//				this.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(requireddate);
//				
//				return curcjlrecord;
				// TODO Auto-generated method stub
//				if(periodlist == null)
//					return null;
//				
//				String nodept = super.getNodeperiodtype();
//				
//				ChenJiaoZhanBiInGivenPeriod currecord = this.getSpecficRecord (requireddate,0); //自己当前周成交量
//				if( currecord != null) {
//					
//					DaPan dapan =  (DaPan)getRoot();
//					NodeXPeriodData dpxdata = dapan.getNodeXPeroidData(nodept);
//					Double dpcjediff = dpxdata.getChengJiaoErDifferenceOfPeriod(requireddate);
//			
//					if( getChengJiaoErDifferenceOfPeriod(requireddate) == null  ) {
//						logger.debug(getMyOwnCode() + getMyOwnName() + "可能是一个新个股或板块");
//						//计算成交额变化贡献率
//						if( dpcjediff<0 || dpcjediff == null ) {//大盘缩量，
//							currecord.setGgbkcjegrowthzhanbi(-100.0);
//						} else {
//							Double curggcje = currecord.getMyOwnChengJiaoEr(); //停牌后所有成交量都应该计算入
//							currecord.setGgbkcjegrowthzhanbi( curggcje/dpcjediff);
//						}
//						//计算给定周的成交额占比是多少周期内的最大占比
//						currecord.setGgbkzhanbimaxweek(0);
//						//计算给定周的成交额占比增速
//						currecord.setGgbkzhanbigrowthrate(0.0);
//						//成交额是多少周期内的最大
//						currecord.setGgbkcjemaxweek(0);
//						
//						return currecord;
//					} else {
//						ChenJiaoZhanBiInGivenPeriod lastrecord = this.getSpecficRecord (requireddate,-1);
//						////计算成交额变化贡献率 
//						if( dpcjediff<0 ) {//大盘缩量，
//							currecord.setGgbkcjegrowthzhanbi(-100.0);
//						} else {
////							Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
////							Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
////							
////							Double cjechange = curcje - lastcje; //个股成交量的变化
//							Double cjechange = getChengJiaoErDifferenceOfPeriod(requireddate);
//							currecord.setGgbkcjegrowthzhanbi(cjechange/dpcjediff);
//						}
//						
//						//计算给定周的成交额占比增速
//						Double curzhanbiratio = currecord.getCjeZhanBi();
//						Double lastzhanbiratio = lastrecord.getCjeZhanBi();
//						double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
//						currecord.setGgbkzhanbigrowthrate(zhanbigrowthrate);
//						
//						//计算给定周的成交额占比是多少周期内的最大占比
//						Integer maxweek = 0;
//						for(int i = super.getRequiredRecordsPostion(requireddate) -1;i>=0;i--) {
//								lastrecord = periodlist.get(i );
//								lastzhanbiratio = lastrecord.getCjeZhanBi();
//								if(curzhanbiratio > lastzhanbiratio)
//									maxweek ++;
//								else
//									break;
//						}
//						currecord.setGgbkzhanbimaxweek(maxweek);
//						
//						//成交额是多少周期内的最大
//						maxweek = 0;
//						Double curcje = currecord.getMyOwnChengJiaoEr();
//						for(int i = super.getRequiredRecordsPostion(requireddate) -1;i>=0;i--) {
//							lastrecord = periodlist.get(i );
//							Double lastcje = lastrecord.getMyOwnChengJiaoEr();
//							if(curcje > lastcje)
//								maxweek ++;
//							else
//								break;
//						}
//						currecord.setGgbkcjemaxweek(maxweek);
//						
//						return currecord;
//					}
//					
//				}
//				
//				return null;
			}
			
			
	}
	
	

}
