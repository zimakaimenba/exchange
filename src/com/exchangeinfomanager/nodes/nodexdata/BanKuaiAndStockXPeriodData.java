package com.exchangeinfomanager.nodes.nodexdata;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.nodes.TDXNodes;




/*
 * 某锟斤拷时锟斤拷锟斤拷锟节硷拷录锟斤拷锟斤拷丶锟斤拷锟�
 */
 public abstract class BanKuaiAndStockXPeriodData implements NodeXPeriodDataBasic
 {
	private String nodecode;

	public BanKuaiAndStockXPeriodData (String nodecode,String nodeperiodtype1)
	{
		this.nodecode = nodecode;
		this.nodeperiodtype = nodeperiodtype1;
		stockohlc = new OHLCSeries(nodeperiodtype1);
		stockamo = new TimeSeries(nodeperiodtype1);
		stockvol = new TimeSeries(nodeperiodtype1);;
		stockamozhanbi = new TimeSeries(nodeperiodtype1);
		stockvolzhanbi = new TimeSeries(nodeperiodtype1);
		stockfxjg = new TimeSeries(nodeperiodtype1);
		stockexchangedaysnumber = new TimeSeries(nodeperiodtype1);
	}
	
	private Logger logger = Logger.getLogger(BanKuaiAndStockXPeriodData.class);

	private String nodeperiodtype;
	protected OHLCSeries stockohlc; //每锟秸成斤拷锟斤拷OHLC
	protected TimeSeries stockamo; //锟缴斤拷锟斤拷
	protected TimeSeries stockvol; //锟缴斤拷锟斤拷
	protected TimeSeries stockamozhanbi; //锟缴斤拷锟斤拷占锟斤拷
	protected TimeSeries stockvolzhanbi; //锟缴斤拷锟斤拷占锟斤拷
	protected TimeSeries stockfxjg; //锟斤拷锟斤拷锟斤拷锟�
	protected TimeSeries stockexchangedaysnumber ; //锟斤拷锟节的斤拷锟斤拷锟秸ｏ拷锟斤拷每锟斤拷锟叫硷拷锟届交锟阶ｏ拷只锟斤拷锟芥不锟斤拷5锟斤拷模锟�

	public String getNodeCode ()
	{
		return this.nodecode;
	}
	
	public void addNewXPeriodData (TDXNodeGivenPeriodDataItem kdata)
	{
		try {
			stockohlc.setNotify(false);
			stockohlc.add(kdata);
		} catch (org.jfree.data.general.SeriesException e) {
			logger.debug(kdata.getMyOwnCode() + kdata.getPeriod() + "锟斤拷锟斤拷锟窖撅拷锟斤拷锟节ｏ拷" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
		}
		
		try {
			stockamo.setNotify(false);
			stockamo.add(kdata.getPeriod(),kdata.getMyOwnChengJiaoEr(),false);
//			stockvol.add(kdata.getPeriod(),kdata.getMyownchengjiaoliang(),false);
			if(kdata.getCjeZhanBi() != null) {
				stockamozhanbi.setNotify(false);
				stockamozhanbi.add(kdata.getPeriod(),kdata.getCjeZhanBi(),false);
			}
//			if(kdata.getCjlZhanBi() != null)
//				stockvolzhanbi.add(kdata.getPeriod(),kdata.getCjlZhanBi(),false);
		} catch (org.jfree.data.general.SeriesException e) {
			logger.debug(kdata.getMyOwnCode() + kdata.getPeriod() + "锟斤拷锟斤拷锟窖撅拷锟斤拷锟节ｏ拷" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//			e.printStackTrace();
		}
		
		try{
			stockexchangedaysnumber.setNotify(false);
			if(kdata.getExchangeDaysNumber() != null && kdata.getExchangeDaysNumber() != 5) //5锟斤拷锟斤拷默锟较的ｏ拷锟斤拷全锟斤拷锟矫达拷
				stockexchangedaysnumber.add(kdata.getPeriod(),kdata.getExchangeDaysNumber(),false);
			
		} catch (org.jfree.data.general.SeriesException e) {
			logger.debug(kdata.getMyOwnCode() + kdata.getPeriod() + "锟斤拷锟斤拷锟窖撅拷锟斤拷锟节ｏ拷" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#hasFxjgInPeriod(java.time.LocalDate, int)
	 */
	public Boolean hasFxjgInPeriod (LocalDate requireddate,int difference)
	{
		TimeSeriesDataItem fxjgitem = stockfxjg.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(fxjgitem == null)
			return false;
		else
			 return true;
	}
	/*
	 * 
	 */
	public void addFxjgToPeriod (RegularTimePeriod period,Integer fxjg)
	{
		try {
			stockfxjg.add(period,fxjg);
//			 addOrUpdate()
		} catch (org.jfree.data.general.SeriesException e) {
//			e.printStackTrace();
		}
	}
	/*
	 * 
	 */
	public OHLCSeries getOHLCData ()
	{
		return this.stockohlc;
	}
	/*
	 * 
	 */
	public OHLCSeries getRangeOHLCData (LocalDate requiredstart,LocalDate requiredend)
	{
		OHLCSeries tmpohlc = new OHLCSeries ("Kxian");
		int itemcount = this.stockohlc.getItemCount();
		for(int i=0;i<itemcount;i++) {
			RegularTimePeriod dataitemp = this.stockohlc.getPeriod(i);
			LocalDate startd = dataitemp.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate endd = dataitemp.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			if( (startd.isAfter(requiredstart) || startd.equals(requiredstart) )  && ( endd.isBefore(requiredend) || endd.equals(requiredend) ))
				tmpohlc.add( (OHLCItem) this.stockohlc.getDataItem(i) );
		}
		
		return tmpohlc;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#getOHLCData(java.time.LocalDate, int)
	 */
	public OHLCItem getSpecificDateOHLCData (LocalDate requireddate,int difference)
	{
		int itemcount = this.stockohlc.getItemCount();
		for(int i=0;i<itemcount;i++) {
			RegularTimePeriod dataitemp = this.stockohlc.getPeriod(i);
			if(dataitemp.equals(this.getJFreeChartFormateTimePeriod(requireddate,0)) )
				return (OHLCItem) this.stockohlc.getDataItem(i);
		}
		
		return null;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#getNodeperiodtype()
	 */
	public String getNodeperiodtype() {
		return nodeperiodtype;
	}
	/*
	 * 
	 */
//	public TimeSeries getChengJiaoEr ()
//	{
//		return stockamo; //锟缴斤拷锟斤拷
//	}
	/*
	 * 
	 */
//	private TimeSeries getRangeChengJiaoEr (LocalDate requiredstart,LocalDate requiredend)
//	{
//		RegularTimePeriod start = this.getJFreeChartFormateTimePeriod(requiredstart,0);
//		RegularTimePeriod end = this.getJFreeChartFormateTimePeriod(requiredend,0);
//		try {
//			return this.stockamo.createCopy( start,  end);
//		} catch (CloneNotSupportedException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	/*
	 * 
	 */
	public TimeSeries getRangeChengJiaoErZhanBi (LocalDate requiredstart,LocalDate requiredend)
	{
		RegularTimePeriod start = this.getJFreeChartFormateTimePeriod(requiredstart,0);
		RegularTimePeriod end = this.getJFreeChartFormateTimePeriod(requiredend,0);
		try {
			return this.stockamozhanbi.createCopy( start,  end);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	} 
	/*
	 * 
	 */
	public Double getChenJiaoErZhanBi (LocalDate requireddate,int difference)
	{
		if(stockohlc == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = null;
//		if(difference >=0 )
			curcjlrecord = stockamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		
		if( curcjlrecord == null) 
			return null;
		else
			return curcjlrecord.getValue().doubleValue();
	}
	/*
	 * 
	 */
	public Double getChengJiaoEr (LocalDate requireddate,int difference)
	{
		
		TimeSeriesDataItem curcjlrecord = null;
//		if(difference >=0 )
			curcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		
		if( curcjlrecord == null) 
			return null;
		else
			return curcjlrecord.getValue().doubleValue();
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#getRecordsStartDate()
	 */
	public LocalDate getRecordsStartDate ()
	{
		if(stockamo.getItemCount() == 0)
			return null;
//		
//		RegularTimePeriod firstperiod = stockohlc.getPeriod(0);
		RegularTimePeriod firstperiod = stockamo.getTimePeriod( 0);
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
		LocalDate mondayday = startdate.with(fieldUS, 2);
		
		return mondayday;
	}
	/*
	 * 
	 */
	public LocalDate getRecordsEndDate ()
	{
		if(stockamo.getItemCount() == 0)
			return null;
		
//		int itemcount = stockohlc.getItemCount();
//		RegularTimePeriod firstperiod = stockohlc.getPeriod(itemcount-1);
		int itemcount = stockamo.getItemCount();
		RegularTimePeriod firstperiod = stockamo.getTimePeriod( itemcount - 1 );
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		LocalDate saturday = enddate.with(DayOfWeek.SATURDAY);
		return saturday;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#hasRecordInThePeriod(java.time.LocalDate, int)
	 */
	public Boolean hasRecordInThePeriod (LocalDate requireddate, int difference) 
	{
		if(stockohlc == null)
			return null;
	
		 TimeSeriesDataItem stockamovaule = stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference) );
		 if(stockamovaule != null)
			 return true;
		 else
			 return false;
	}
	
	/*
	 * /*
			 * 计算指定周期和上周期的成交额差额，适合stock/bankuai，dapan有自己的计算方法
	 */

	public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate,int difference)
	{
		if(stockohlc == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference) );
		if( curcjlrecord == null) 
			return null;
		
		int index = stockamo.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference));
//		DaPan dapan = (DaPan)getRoot();
//		while ( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) && index >-1000 ) {  //上周可能大盘修饰
//			index --;
//		}
		
		TimeSeriesDataItem lastcjlrecord = stockamo.getDataItem( index -1 );
		if(lastcjlrecord == null) //休市前还是空，说明要是新板块。板块没有停牌的
			return null;
		
		Double curcje = curcjlrecord.getValue().doubleValue();
		Double lastcje = lastcjlrecord.getValue().doubleValue();
		
		return curcje - lastcje;
	}
	/*
	 * 锟斤拷锟较硷拷锟斤拷锟侥成斤拷锟斤拷占锟斤拷锟斤拷锟斤拷
	 */
	public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate,int difference) 
	{
		TimeSeriesDataItem curcjlrecord = this.stockamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if( curcjlrecord == null) 
			return null;
		
		int index = stockamo.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference));
		
//		DaPan dapan = (DaPan)getRoot();
//		while ( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) && index >-1000) { //上周可能大盘修饰 
//			index --;
//		}
		
		TimeSeriesDataItem lastcjlrecord = stockamozhanbi.getDataItem( index -1  );
		if(lastcjlrecord == null) { //休市前还是空，说明要是新板块。板块没有停牌的
			logger.debug(getNodeCode() + "可能是一个新个股或板块");
			return 100.0;
		}
		
		Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
		Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
		Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
		logger.debug(getNodeCode() + "占比增速" + requireddate.toString() + zhanbigrowthrate);
		
		return zhanbigrowthrate;
	}
	/*
	 * 
	 */
	protected RegularTimePeriod getJFreeChartFormateTimePeriod (LocalDate requireddate,int difference) 
	{
		String nodeperiod = getNodeperiodtype();
		LocalDate expectedate = null;
		RegularTimePeriod period = null;
		if(nodeperiod.equals(TDXNodeGivenPeriodDataItem.WEEK)) { //锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷荩锟街灰拷锟斤拷锟斤拷锟斤拷锟酵拷锟斤拷锟斤拷煞锟斤拷锟�
			expectedate = requireddate.plus(difference,ChronoUnit.WEEKS);
			period = new org.jfree.data.time.Week (Date.from(expectedate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		} else if(nodeperiod.equals(TDXNodeGivenPeriodDataItem.DAY)) { //锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷荩锟斤拷锟斤拷锟斤拷锟酵拷趴煞锟斤拷锟�
			expectedate = requireddate.plus(difference,ChronoUnit.DAYS);
			period = new org.jfree.data.time.Day(Date.from(expectedate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}  else if(nodeperiod.equals(TDXNodeGivenPeriodDataItem.MONTH)) { //锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷荩锟斤拷锟斤拷锟斤拷锟酵拷趴煞锟斤拷锟�
			expectedate = requireddate.plus(difference,ChronoUnit.MONTHS);
		}
		
		return period;
	}
	@Override
	public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference) 
	{
		TimeSeriesDataItem curcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if( curcjlrecord == null) 
			return null;
		
		Double curcje = curcjlrecord.getValue().doubleValue();
		int itemcount = stockamo.getItemCount();
		int maxweek = 0;
		
		int index = stockamo.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
		
		for(int i = index-1;i >=0; i--) {
			
			TimeSeriesDataItem lastcjlrecord = stockamo.getDataItem( i );
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
		TimeSeriesDataItem curcjlrecord = this.stockamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if( curcjlrecord == null) 
			return null;
					
		Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
		
		int index = stockamozhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
		
		int maxweek = 0;
		for(int i = index - 1; i >= 0; i--) {
			
			
			TimeSeriesDataItem lastcjlrecord = stockamozhanbi.getDataItem( i );
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
	/*
	 * 
	 */
	public Integer getChenJiaoErZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate,int difference)
	{
		if(stockohlc == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = this.stockamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if( curcjlrecord == null) 
			return null;

		Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
		
		int index = stockamozhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
		int minweek = 0;
		for(int i = index -1; i >= 0; i --) {
			TimeSeriesDataItem lastcjlrecord = stockamozhanbi.getDataItem( i );
			if(lastcjlrecord == null ) //可能到了记录的头部了，或者是个诞生时间不长的板块
				return minweek;

			Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
			if(curzhanbiratio < lastzhanbiratio)
				minweek ++;
			else
				break;
		}
		
		return minweek;
	}
	/*
	 * 计算成交额变化贡献率，即板块成交额的变化占整个上级板块成交额增长量的比率，板块和个股都是相对于大盘
	 */
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai (TDXNodes superbk, LocalDate requireddate,int difference) 
	{
		TimeSeriesDataItem curcjlrecord = this.stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference) );
		if( curcjlrecord == null) 
			return null;
		
		//判断上级板块(大盘或者板块)是否缩量,所以了没有比较的意义，直接返回-100；
		String nodept = getNodeperiodtype();
		NodeXPeriodDataBasic bkxdata = superbk.getNodeXPeroidData(nodept);
		Double bkcjediff = bkxdata.getChengJiaoErDifferenceWithLastPeriod(requireddate,difference);
		if( bkcjediff == null || bkcjediff < 0   ) {//板块缩量，
			return -100.0;
		}
		
		TimeSeriesDataItem lastcjlrecord = null;
		int index = this.stockamo.getIndex( getJFreeChartFormateTimePeriod(requireddate,difference) );
		try{
			lastcjlrecord = stockamo.getDataItem( index - 1);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			logger.debug("index = 0，无法判断");
			return 100.0;
//			e.printStackTrace();
		}
		if(lastcjlrecord == null) { //休市前还是空，说明是停牌后复牌了
			try {
			Double curggcje = curcjlrecord.getValue().doubleValue(); //新板块所有成交量都应该计算入
			return curggcje/bkcjediff;
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		
		Double curcje = curcjlrecord.getValue().doubleValue();
		Double lastcje = lastcjlrecord.getValue().doubleValue();
		Double cjechange = curcje - lastcje; //个股成交量的变化
		
		return cjechange/bkcjediff;
	}
	
	 /*
	  * 计算指定周有几个交易日
	  */
	 public Integer getExchangeDaysNumberForthePeriod (LocalDate requireddate,int difference)
	 {
		 if(stockexchangedaysnumber == null)
			 return null;
		 
		 TimeSeriesDataItem curdaynumberrecord = this.stockexchangedaysnumber.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		 if( curdaynumberrecord == null) 
				return 5;
		 else
			 return curdaynumberrecord.getValue().intValue();
		 
	 }
	 
 } //END OF 
