package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;

public class StockOfBanKuai extends Stock
{
	public StockOfBanKuai (BanKuai bankuai1,Stock stock1) 
	{
		super (stock1.getMyOwnCode(),stock1.getMyOwnName());
		this.bankuai = bankuai1;
		this.stock = stock1;
		
		super.nodewkdata = new StockOfBanKuaiNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.WEEK) ;
//		super.nodedaydata = new StockOfBanKuaiNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.DAY) ;
		super.nodemonthdata = new StockOfBanKuaiNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.MONTH) ;
	}
	
	private static Logger logger = Logger.getLogger(StockOfBanKuai.class);
	private BanKuai bankuai;
	private Stock stock;
	private Integer quanzhong;
//	private StockNodeXPeriodData stockwkdataofbankuai; 
//	private StockNodeXPeriodData stockmonthdataofbankuai;
	
	public void setStockQuanZhong (Integer quanzhong)
	{
		this.quanzhong = quanzhong; 
	}
	public BanKuai getBanKuai ()
	{
		return this.bankuai;
	}
	public Integer getQuanZhong ()
	{
		return this.quanzhong;
	}
	public NodeXPeriodData getStockXPeriodDataForBanKuai (String period)
	{
		if(period.equals(ChenJiaoZhanBiInGivenPeriod.MONTH))
			return (StockOfBanKuaiNodeXPeriodData) this.nodemonthdata;
		else if(period.equals(ChenJiaoZhanBiInGivenPeriod.WEEK))
			return this.nodewkdata;
		else 
			return null;
	}
	
	public class StockOfBanKuaiNodeXPeriodData extends NodeXPeriodData
	{
		public StockOfBanKuaiNodeXPeriodData (String nodeperiodtype1) 
		{
			super(nodeperiodtype1);
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
			if(lastcjlrecord == null) //休市前还是空，说明要么停牌，要么是新股票
				return null;
			
			Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
			Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
			 
			return curcje - lastcje;
		}
		/*
		 * 获得指定周的记录,适合stock/bankuai，dapan有自己的计算方法
		 */
		public ChenJiaoZhanBiInGivenPeriod getSpecficRecord (LocalDate requireddate,int difference)
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
				if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.WEEK)) { //如果是周线数据，只要数据周相同，即可返回
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
				logger.debug(getMyOwnCode() + getMyOwnName() + "可能是一个新个股");
				return 100.0;
			}
			
			Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
			Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
			double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
			logger.debug(getMyOwnCode() + getMyOwnName() + "占比增速" + requireddate.toString() + zhanbigrowthrate);
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
			for(int i = index;i>=0;i--) {
				lastcjlrecord = this.getSpecficRecord (requireddate,index);
				Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
				if(curzhanbiratio > lastzhanbiratio)
					maxweek ++;
				else
					break;
			}
			
			return maxweek;
		}

		@Override
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErChangeGrowthRateOfSuperBanKuai(java.time.LocalDate)
		 * 和其他该函数不同的地方就是这里去板块的成交量而不是大盘的成交量
		 */
		public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(LocalDate requireddate) 
		{
			if(periodlist == null)
				return null;
			
			//判断上级板块(大盘或者板块)是否缩量,所以了没有比较的意义，直接返回-100；
			String nodept = super.getNodeperiodtype();
			Double bkcjediff = bankuai.getNodeXPeroidData(nodept).getChengJiaoErDifferenceWithLastPeriod(requireddate);
			if( bkcjediff == null || bkcjediff<0  ) //上级板块缩量，
				return -100.0;
			
			ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
			if( curcjlrecord == null) 
				return null;
			
			int index = -1;
			while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //上周可能大盘修饰
				index --;
			}
			
			ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
			if(lastcjlrecord == null) { //休市前还是空，说明要是新板块。板块没有停牌的
				Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //新板块所有成交量都应该计算入
				return curggcje/bkcjediff;
			}
			
			Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
			Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
			Double cjechange = curcje - lastcje; //个股成交量的变化
			
			return cjechange/bkcjediff;
		}
		
		/*
		 * 判断本周是停牌后复牌
		 */
//		protected Boolean isFuPaiAfterTingPai (LocalDate requireddate )
//		{
//			Integer index = super.getRequiredRecordsPostion (requireddate);
//			if( index != null) {
//				if(index == 0) { //到了记录的第一条，怎么办
//					return true;
//				}
//				
//				ChenJiaoZhanBiInGivenPeriod curcjlrecord = periodlist.get(index);
//				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = null;
//				try {
//					 lastcjlrecord = periodlist.get(index -1 );
//				} catch (java.lang.ArrayIndexOutOfBoundsException e) { 
//					return true;
//				}
//				
//				LocalDate curweeknum = curcjlrecord.getRecordsDayofEndofWeek();
//				LocalDate lastweeknum = lastcjlrecord.getRecordsDayofEndofWeek();
//				
//				long diffwknum = Math.abs(ChronoUnit.WEEKS.between (curweeknum,lastweeknum));
//				
//				if(diffwknum == 1) //差一周，正常交易
//					return false;
//				
//				int alldapanxiushi = 0;
//				for(int i=1;i<diffwknum;i++) {
//					LocalDate dapanlastweekdate = curweeknum.with(DayOfWeek.MONDAY).minus(i,ChronoUnit.WEEKS);
//					if( ((DaPan)getRoot()).isDaPanXiuShi(dapanlastweekdate,super.getNodeperiodtype() ) )
//						alldapanxiushi ++;
//				}
//				if(alldapanxiushi == diffwknum-1)
//					return false;
//				else 
//					return true;
//			}
//			
//			return null;
//		}
//		/*
//		 * 计算给定周的成交额占比增速
//		 */
//		public Double getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (LocalDate requireddate)
//		{
//			if(periodlist == null)
//				return null;
//			
//			Integer index = super.getRequiredRecordsPostion (requireddate);
//			if( index != null) {
//					if( getChengJiaoErDifferenceOfPeriod(requireddate) == null && super.periodlist.get(index) != null ) {
//							logger.debug(getMyOwnCode() + getMyOwnName() + requireddate.toString() + "可能是一个新个股或板块");
//							return 100.0;
//					}
//					
//
////					LocalDate curweeknum = curcjlrecord.getRecordsDayofEndofWeek();
////					LocalDate lastweeknum = lastcjlrecord.getRecordsDayofEndofWeek();
//					
////					long diffwknum = Math.abs(ChronoUnit.WEEKS.between (curweeknum,lastweeknum));
////					if(diffwknum >1) {
////						if(this.checkIsFuPaiAfterTingPai (requireddate)) { //说明是停牌的复牌
////							return 100.0;
////						} 
////					}
//					
//					if(this.isFuPaiAfterTingPai (requireddate)) { //说明是停牌的复牌
//						logger.debug(getMyOwnCode() + getMyOwnName() + requireddate.toString() + "为停复牌股，占比增速为100.0");
//						return 100.0;
//					}
//					
//					ChenJiaoZhanBiInGivenPeriod curcjlrecord = periodlist.get(index);
//					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(index -1 );				
//					Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
//					Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
//					Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
//					logger.debug(getMyOwnCode() + getMyOwnName() + requireddate.toString() + "占比增速为" + zhanbigrowthrate.toString());
//					return zhanbigrowthrate;
//			}
//			
//			logger.debug(getMyOwnCode() + getMyOwnName() + requireddate.toString() + "占比增速为null，出问题了，请检查");
//			return null;
//		}
//		/*
//		 * 计算给定周的成交额和板块占比是多少周期内的最大占比
//		 */
//		public Integer getChenJiaoLiangZhanBiMaxWeekForAGivenPeriod (LocalDate requireddate)
//		{
//			if(periodlist == null)
//				return null;
//
//			int maxweek = 0;
//			Integer index = super.getRequiredRecordsPostion (requireddate);
//			if( index != null) {
//				if( getChengJiaoErDifferenceOfPeriod(requireddate) == null && super.periodlist.get(index) != null ) {
//					logger.debug(getMyOwnCode() + getMyOwnName() + "可能是一个新个股或板块");
//					return 0;
//				}
//				
//				if(this.isFuPaiAfterTingPai (requireddate)) { //说明是停牌的复牌
//					return 0;
//				} else {
//					ChenJiaoZhanBiInGivenPeriod curcjlrecord = periodlist.get(index);
//
//					Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
//					for(int i = index -1;i>=0;i--) {
//						ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(i );
//						Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
//						LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
//						
//						if(curzhanbiratio > lastzhanbiratio)
//								maxweek ++;
//						else
//							break;
//						
//						if( this.isFuPaiAfterTingPai (lastdate) ) //
//							break;
//					}
//				}
//			}
//			
//			return maxweek;
//		}
//		/*
//		 * 计算给定周期的成交额和大盘的占比是多少周期内的最大占比
//		 */
//		public Integer getChenJiaoLiangDaPanZhanBiMaxWeekForAGivenPeriod (LocalDate requireddate)
//		{
//			if(periodlist == null)
//				return null;
//
//			int maxweek = 0;
//			Integer index = super.getRequiredRecordsPostion (requireddate);
//			if( index != null) {
//				if( getChengJiaoErDifferenceOfPeriod(requireddate) == null && super.periodlist.get(index) != null ) {
////					logger.debug(this.getMyOwnCode() + this.getMyOwnName() + "可能是一个新个股或板块");
//					return 0;
//				}
//				
//				if(this.isFuPaiAfterTingPai (requireddate)) { //说明是停牌的复牌
//					return 0;
//				} else {
//					ChenJiaoZhanBiInGivenPeriod curcjlrecord = periodlist.get(index);
//					NodeXPeriodData myupbankuaidata = ((DaPan)getRoot()).getNodeXPeroidData(super.getNodeperiodtype());
//					ChenJiaoZhanBiInGivenPeriod bkcjlrecord = myupbankuaidata.getSpecficRecord(requireddate,0);
//					
//					Double curzhanbiratio = curcjlrecord.getMyOwnChengJiaoEr() /  bkcjlrecord.getUpLevelChengJiaoEr(); //个股成交量和大盘成交量
//					for(int i = index -1;i>=0;i--) {
//						ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(i);
//						LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
//						ChenJiaoZhanBiInGivenPeriod lastbkcjlrecord = myupbankuaidata.getSpecficRecord(lastdate,0);
//						
//						Double lastratio = lastcjlrecord.getMyOwnChengJiaoEr() / lastbkcjlrecord.getUpLevelChengJiaoEr();
//						if(curzhanbiratio > lastratio)
//							maxweek ++;
//						else
//							break;
//						
//						if(this.isFuPaiAfterTingPai (lastdate) )
//							break;
//					}
//				}
//			}
//			
//			return maxweek;
//			
//		}
//		/*
//		 * 计算成交额变化贡献率，个体增量占板块增量的百分比
//		 */
//		public Double getChenJiaoErChangeGrowthRateForAGivenPeriod (LocalDate requireddate)
//		{
//			if(periodlist == null)
//				return null;
//			
//			//检查板块的成交量是增加，缩量没有比较意义则直接返回
//			NodeXPeriodData myupbankuaidata = myupbankuai.getNodeXPeroidData(super.getNodeperiodtype());
//			Double bkcjediff = myupbankuaidata.getChengJiaoErDifferenceOfPeriod (requireddate); 
//			if(   bkcjediff == null || bkcjediff <0)
//				return -100.0;
//			
//			Integer index = super.getRequiredRecordsPostion (requireddate);
//			if( index != null) {
//					ChenJiaoZhanBiInGivenPeriod curcjlrecord = super.periodlist.get(index); //自己当前周成交量
//					
//					if( getChengJiaoErDifferenceOfPeriod(requireddate) == null && super.periodlist.get(index) != null ) {
//						logger.debug(getMyOwnCode() + getMyOwnName() + "可能是个新板块" );
//						Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //停牌后所有成交量都应该计算入
//						return curggcje/bkcjediff;
//					}
//					
//					if(this.isFuPaiAfterTingPai (requireddate)) { //说明是停牌的复牌  
//						Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();//停牌后所有成交量都应该计算入
//
//						return curggcje/bkcjediff;
//
//					} else { //上周没有停牌
//						Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();
//						ChenJiaoZhanBiInGivenPeriod lastcjlrecord = super.periodlist.get(index-1); //自己当前周成交量
//						Double lastggcje = lastcjlrecord.getMyOwnChengJiaoEr();
//						
//						Double gegucjechange = curggcje - lastggcje; //个股成交量的变化
//						
//						return gegucjechange/bkcjediff;
//					}
//			}
//			
//			return  -100.0;
//		}
		/*
		 * 计算个股和大盘/板块成交量的各个数据
		 */
		public ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate (LocalDate requireddate)
		{
//			if(periodlist == null)
//				return null;
//			
////			NodeFengXiData nodefx = new NodeFengXiData (super.getMyOwnCode(),requireddate);
//			
//			//计算成交额变化贡献率，个体增量占板块增量的百分比
//			//检查板块的成交量是增加，缩量没有比较意义则直接返回
//			NodeXPeriodData myupbankuaidata = myupbankuai.getNodeXPeroidData(super.getNodeperiodtype());
//			Double bkcjediff = myupbankuaidata.getChengJiaoErDifferenceOfPeriod (requireddate); 
//			
//			Integer index = super.getRequiredRecordsPostion (requireddate);
//			if( index != null) {
//				ChenJiaoZhanBiInGivenPeriod curcjlrecord = super.periodlist.get(index); //自己当前周成交量
//				
//				if( getChengJiaoErDifferenceOfPeriod(requireddate) == null && super.periodlist.get(index) != null ) {
////					logger.debug(this.getMyOwnCode() + this.getMyOwnName() + "可能是一个新个股或板块");
//					//计算成交额变化贡献率，个体增量占板块增量的百分比
//					if( bkcjediff == null || bkcjediff <0)
//						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
//					else {
//						Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //停牌后所有成交量都应该计算入
//						curcjlrecord.setGgbkcjegrowthzhanbi( curggcje/bkcjediff );
//					}
//					//计算给定周的成交额和板块的占比是多少周期内的最大占比
//					curcjlrecord.setGgbkzhanbimaxweek(0);
//					// 计算给定周的成交额和板块占比增速
//					curcjlrecord.setGgbkzhanbigrowthrate(100.0);
//					//计算给定轴的成交额和大盘的占比是多少周期内的最大占比
//					curcjlrecord.setGgdpzhanbimaxweek(0);
//					curcjlrecord.setGgdpzhanbigrowthrate (100.0); //和大盘占比的增速
//					//成交额是多少周最大
//					curcjlrecord.setGgbkcjemaxweek(0);
//					
//					return curcjlrecord;
//				} else if(this.isFuPaiAfterTingPai (requireddate)) { //说明是停牌的复牌
//					//计算成交额变化贡献率，个体增量占板块增量的百分比
//					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();//停牌后所有成交量都应该计算入
//					if( bkcjediff <0 || bkcjediff == null)
//						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
//					else
//						curcjlrecord.setGgbkcjegrowthzhanbi(curggcje/bkcjediff);
//					//计算给定周的成交额和板块的占比是多少周期内的最大占比
//					curcjlrecord.setGgbkzhanbimaxweek(0);
//					// 计算给定周的成交额和板块占比增速
//					curcjlrecord.setGgbkzhanbigrowthrate(100.0);
//					//计算给定轴的成交额和大盘的占比是多少周期内的最大占比
//					curcjlrecord.setGgdpzhanbimaxweek(0);
//					curcjlrecord.setGgdpzhanbigrowthrate (100.0); //和大盘占比的增速
//					//成交额是多少周最大
//					curcjlrecord.setGgbkcjemaxweek(0);
//
//					
//					return curcjlrecord;
//				} else {
//					//计算成交额变化贡献率，个体增量占板块增量的百分比
////					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();
////					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = super.wkcjeperiodlist.get(index-1); //自己上周成交量
////					Double lastggcje = lastcjlrecord.getMyOwnChengJiaoEr();
////					Double gegucjechange = curggcje - lastggcje; //个股成交量的变化
//					
//					Double gegucjechange = getChengJiaoErDifferenceOfPeriod(requireddate);
//					if( bkcjediff <0 || bkcjediff == null)
//						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
//					else
//						curcjlrecord.setGgbkcjegrowthzhanbi( gegucjechange/bkcjediff );
//					
//					//计算给定周的成交额和板块的占比是多少周期内的最大占比
//					Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
//					Integer maxweek = 0;
//					for(int i = index -1;i>=0;i--) {
//						ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(i );
//						Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
//						LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
//
//						if(curzhanbiratio > lastzhanbiratio)
//								maxweek ++;
//						else
//							break;
//						
//						if( this.isFuPaiAfterTingPai (lastdate) ) //
//							break;
//					}
//					curcjlrecord.setGgbkzhanbimaxweek(maxweek);
//					
//					//成交额是多少周最大
//					Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
//					maxweek = 0;
//					for(int i = index -1;i>=0;i--) {
//						ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(i );
//						Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
//						LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
//
//						if(curcje > lastcje)
//								maxweek ++;
//						else
//							break;
//							
//						if( this.isFuPaiAfterTingPai (lastdate) ) //
//								break;
//					}
//					curcjlrecord.setGgbkcjemaxweek(maxweek);
//
//					// 计算给定周的成交额和板块占比增速
//					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(index -1 );				
//					Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
//					Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
//
//					curcjlrecord.setGgbkzhanbigrowthrate( zhanbigrowthrate);
//					
//					//计算给定轴的成交额和大盘的占比是多少周期内的最大占比
//					ChenJiaoZhanBiInGivenPeriod bkcjlrecord = myupbankuaidata.getSpecficRecord(requireddate,0);//通过所属板块来得到大盘成交量
//					Double curdpzhanbiratio = curcjlrecord.getMyOwnChengJiaoEr() /  bkcjlrecord.getUpLevelChengJiaoEr(); //个股成交量和大盘成交量当周占比
//					Integer dpmaxweek = 0;
//					for(int i = index -1;i>=0;i--) {
//						lastcjlrecord = periodlist.get(i);
//						LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
//						
//						ChenJiaoZhanBiInGivenPeriod lastbkcjlrecord = myupbankuaidata.getSpecficRecord(lastdate,0);
//						Double lastratio = lastcjlrecord.getMyOwnChengJiaoEr() / lastbkcjlrecord.getUpLevelChengJiaoEr();
//						
//						if(i == (index -1) ) { //在这里顺便计算一下和大盘占比的增速
//							Double dpzhanbigrowthrate = (curdpzhanbiratio - lastratio)/lastratio;
//							curcjlrecord.setGgdpzhanbigrowthrate(dpzhanbigrowthrate);
//						}
//						
//						if(curdpzhanbiratio > lastratio)
//							dpmaxweek ++;
//						else
//							break;
//						
//						if(this.isFuPaiAfterTingPai (lastdate) ) //查到连续周到头为止
//							break;
//					}
//					curcjlrecord.setGgdpzhanbimaxweek(dpmaxweek);
//					
//					//计算给定轴的成交额和大盘的占比
//					curcjlrecord.setGgdpzhanbi(curdpzhanbiratio);
//					
//					return curcjlrecord;
//					
//				}
//			}
//			
			return null;
		}



		
	}
}