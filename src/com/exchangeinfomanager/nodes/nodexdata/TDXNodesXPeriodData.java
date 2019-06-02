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
 public abstract class TDXNodesXPeriodData implements NodeXPeriodDataBasic
 {
	private String nodecode;

	public TDXNodesXPeriodData (String nodecode,String nodeperiodtype1)
	{
		this.nodecode = nodecode;
		this.nodeperiodtype = nodeperiodtype1;
		nodeohlc = new OHLCSeries(nodeperiodtype1);
		nodeamo = new TimeSeries(nodeperiodtype1);
		nodevol = new TimeSeries(nodeperiodtype1);;
		nodeamozhanbi = new TimeSeries(nodeperiodtype1);
		nodevolzhanbi = new TimeSeries(nodeperiodtype1);
		
		nodeexchangedaysnumber = new TimeSeries(nodeperiodtype1);
	}
	
	private Logger logger = Logger.getLogger(TDXNodesXPeriodData.class);

	private String nodeperiodtype;
	protected OHLCSeries nodeohlc; //每锟秸成斤拷锟斤拷OHLC
	protected TimeSeries nodeamo; //锟缴斤拷锟斤拷
	protected TimeSeries nodevol; //锟缴斤拷锟斤拷
	protected TimeSeries nodeamozhanbi; //锟缴斤拷锟斤拷占锟斤拷
	protected TimeSeries nodevolzhanbi; //锟缴斤拷锟斤拷占锟斤拷
	protected TimeSeries nodefxjg; //
	protected TimeSeries nodeexchangedaysnumber ; //
	 // 缺口的统计结果，不直接存放缺口的原因是已经有ohlc了，可以计算出缺口，如果再专门存放缺口list有点重复，不如存放缺口统计数据
	protected TimeSeries nodeqktjopenup;
	protected TimeSeries nodeqktjhuibuup;
	protected TimeSeries nodeqktjopendown;
	protected TimeSeries nodeqktjhuibudown;
	//均线
	protected TimeSeries nodema5;
	protected TimeSeries nodema10;
	protected TimeSeries nodema20;
	protected TimeSeries nodema30;
	protected TimeSeries nodema60;
	protected TimeSeries nodema120;
	protected TimeSeries nodema250;
	
	public String getNodeCode ()
	{
		return this.nodecode;
	}
	
	public void addNewXPeriodData (TDXNodeGivenPeriodDataItem kdata)
	{
		try {
			nodeohlc.setNotify(false);
			nodeohlc.add(kdata);
		} catch (org.jfree.data.general.SeriesException e) {
			logger.debug(kdata.getMyOwnCode() + kdata.getPeriod() + "锟斤拷锟斤拷锟窖撅拷锟斤拷锟节ｏ拷" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
		}
		
		try {
			nodeamo.setNotify(false);
			nodeamo.add(kdata.getPeriod(),kdata.getMyOwnChengJiaoEr(),false);
//			nodevol.add(kdata.getPeriod(),kdata.getMyownchengjiaoliang(),false);
			if(kdata.getCjeZhanBi() != null) {
				nodeamozhanbi.setNotify(false);
				nodeamozhanbi.add(kdata.getPeriod(),kdata.getCjeZhanBi(),false);
			}
//			if(kdata.getCjlZhanBi() != null)
//				nodevolzhanbi.add(kdata.getPeriod(),kdata.getCjlZhanBi(),false);
		} catch (org.jfree.data.general.SeriesException e) {
			logger.debug(kdata.getMyOwnCode() + kdata.getPeriod() + "锟斤拷锟斤拷锟窖撅拷锟斤拷锟节ｏ拷" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//			e.printStackTrace();
		}
		
		try{
			nodeexchangedaysnumber.setNotify(false);
			if(kdata.getExchangeDaysNumber() != null && kdata.getExchangeDaysNumber() != 5) //5锟斤拷锟斤拷默锟较的ｏ拷锟斤拷全锟斤拷锟矫达拷
				nodeexchangedaysnumber.add(kdata.getPeriod(),kdata.getExchangeDaysNumber(),false);
			
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
		if(nodefxjg == null)
			return false;
		
		TimeSeriesDataItem fxjgitem = nodefxjg.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
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
		if(nodefxjg == null)
			nodefxjg = new TimeSeries(this.nodeperiodtype);
		
		try {
			nodefxjg.add(period,fxjg);
//			 addOrUpdate()
		} catch (org.jfree.data.general.SeriesException e) {
//			e.printStackTrace();
		}
	}
	/*
	 * 
	 */
	public void addQueKouTongJiJieGuo (LocalDate qkdate,Integer nodeqktjopenup1,Integer nodeqktjhuibuup1,Integer nodeqktjopendown1,Integer nodeqktjhuibudown1)
	{
		
		if( nodeqktjopenup == null)
			nodeqktjopenup = new TimeSeries(this.nodeperiodtype);
		
		if( nodeqktjhuibuup == null)
			nodeqktjhuibuup = new TimeSeries(this.nodeperiodtype);
		
		if( nodeqktjopendown == null)
			nodeqktjopendown = new TimeSeries(this.nodeperiodtype);
		
		if( nodeqktjhuibudown == null)
			nodeqktjhuibudown = new TimeSeries(this.nodeperiodtype);
		
		RegularTimePeriod period = getJFreeChartFormateTimePeriod(qkdate,0);
		try {
			if(nodeqktjopenup1 != null && nodeqktjopenup1 !=0 )
				nodeqktjopenup.add(period,nodeqktjopenup1);
//			 addOrUpdate()
		} catch (org.jfree.data.general.SeriesException e) {
//			e.printStackTrace();
		}
		
		try {
			if(nodeqktjhuibuup1 != null && nodeqktjhuibuup1 !=0 )
				nodeqktjhuibuup.add(period,nodeqktjhuibuup1);
//			 addOrUpdate()
		} catch (org.jfree.data.general.SeriesException e) {
//			e.printStackTrace();
		}
		
		try {
			if(nodeqktjopendown1 != null && nodeqktjopendown1 !=0 )
				nodeqktjopendown.add(period,nodeqktjopendown1);
//			 addOrUpdate()
		} catch (org.jfree.data.general.SeriesException e) {
//			e.printStackTrace();
		}
		
		try {
			if(nodeqktjhuibudown1 != null && nodeqktjhuibudown1 !=0 )
				nodeqktjhuibudown.add(period,nodeqktjhuibudown1);
//			 addOrUpdate()
		} catch (org.jfree.data.general.SeriesException e) {
//			e.printStackTrace();
		}
	}
	/*
	 * 
	 */
	public Integer getQueKouTongJiOpenUp (LocalDate requireddate, Integer difference)
	{
		if(this.nodeqktjopenup == null)
			return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjopenup.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(qktjitem == null)
			return null;
		else
			 return (Integer)qktjitem.getValue(); 
		
	}
	public Integer getQueKouTongJiOpenDown (LocalDate requireddate, Integer difference)
	{
		if(this.nodeqktjopendown == null)
			return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjopendown.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(qktjitem == null)
			return null;
		else
			 return (Integer)qktjitem.getValue(); 
		
	}
	public Integer getQueKouTongJiHuiBuUp (LocalDate requireddate, Integer difference)
	{
		if(this.nodeqktjhuibuup == null)
			return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjhuibuup.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(qktjitem == null)
			return null;
		else
			 return (Integer)qktjitem.getValue(); 
		
	}
	public Integer getQueKouTongJiHuiBuDown (LocalDate requireddate, Integer difference)
	{
		if(this.nodeqktjhuibudown == null)
			return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjhuibudown.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(qktjitem == null)
			return null;
		else
			 return (Integer)qktjitem.getValue(); 
		
	}
	/*
	 * 
	 */
	public OHLCSeries getOHLCData ()
	{
		return this.nodeohlc;
	}
	/*
	 * 
	 */
//	public OHLCSeries getRangeOHLCData (LocalDate requiredstart,LocalDate requiredend)
//	{
//		OHLCSeries tmpohlc = new OHLCSeries ("Kxian");
//		int itemcount = this.nodeohlc.getItemCount();
//		for(int i=0;i<itemcount;i++) {
//			RegularTimePeriod dataitemp = this.nodeohlc.getPeriod(i);
//			LocalDate startd = dataitemp.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//			LocalDate endd = dataitemp.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//			
//			if( (startd.isAfter(requiredstart) || startd.equals(requiredstart) )  && ( endd.isBefore(requiredend) || endd.equals(requiredend) ))
//				tmpohlc.add( (OHLCItem) this.nodeohlc.getDataItem(i) );
//		}
//		
//		return tmpohlc;
//	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#getOHLCData(java.time.LocalDate, int)
	 */
	public OHLCItem getSpecificDateOHLCData (LocalDate requireddate,int difference)
	{
		int itemcount = this.nodeohlc.getItemCount();
		for(int i=0;i<itemcount;i++) {
			RegularTimePeriod dataitemp = this.nodeohlc.getPeriod(i);
			if(dataitemp.equals(this.getJFreeChartFormateTimePeriod(requireddate,0)) )
				return (OHLCItem) this.nodeohlc.getDataItem(i);
		}
		
		return null;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic#getSpecificDateOHLCData(java.time.LocalDate, int)
	 */
	public Integer getIndexOfSpecificDateOHLCData (LocalDate requireddate,int difference)
	{
		int itemcount = this.nodeohlc.getItemCount();
		for(int i=0;i<itemcount;i++) {
			RegularTimePeriod dataitemp = this.nodeohlc.getPeriod(i);
			if(dataitemp.equals(this.getJFreeChartFormateTimePeriod(requireddate,0)) )
				return i;
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
//		return nodeamo; //锟缴斤拷锟斤拷
//	}
	/*
	 * 
	 */
//	private TimeSeries getRangeChengJiaoEr (LocalDate requiredstart,LocalDate requiredend)
//	{
//		RegularTimePeriod start = this.getJFreeChartFormateTimePeriod(requiredstart,0);
//		RegularTimePeriod end = this.getJFreeChartFormateTimePeriod(requiredend,0);
//		try {
//			return this.nodeamo.createCopy( start,  end);
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
			return this.nodeamozhanbi.createCopy( start,  end);
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
		if(nodeohlc == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = null;
//		if(difference >=0 )
			curcjlrecord = nodeamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		
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
			curcjlrecord = nodeamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		
		if( curcjlrecord == null) 
			return null;
		else
			return curcjlrecord.getValue().doubleValue();
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#getRecordsStartDate()
	 */
	public LocalDate getAmoRecordsStartDate ()
	{
		if(nodeamo.getItemCount() == 0)
			return null;
//		
//		RegularTimePeriod firstperiod = nodeohlc.getPeriod(0);
		RegularTimePeriod firstperiod = nodeamo.getTimePeriod( 0);
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		if(this.nodeperiodtype == TDXNodeGivenPeriodDataItem.WEEK) {
			TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
			LocalDate mondayday = startdate.with(fieldUS, 2);
			return mondayday;
		} else if(this.nodeperiodtype == TDXNodeGivenPeriodDataItem.DAY) {
			return startdate;
		}
		
		return null;
	}
	/*
	 * 
	 */
	public LocalDate getAmoRecordsEndDate ()
	{
		if(nodeamo.getItemCount() == 0)
			return null;
		
//		int itemcount = nodeohlc.getItemCount();
//		RegularTimePeriod firstperiod = nodeohlc.getPeriod(itemcount-1);
		int itemcount = nodeamo.getItemCount();
		RegularTimePeriod firstperiod = nodeamo.getTimePeriod( itemcount - 1 );
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		if(this.nodeperiodtype == TDXNodeGivenPeriodDataItem.WEEK) {
			LocalDate saturday = enddate.with(DayOfWeek.SATURDAY);
			return saturday;
		} else if(this.nodeperiodtype == TDXNodeGivenPeriodDataItem.DAY) {
			return enddate;
		}
		
		return null;
		
	}
	public LocalDate getKXxianRecordsStartDate ()
	{
		if(this.nodeohlc.getItemCount() == 0)
			return null;
		
//		int itemcount = nodeohlc.getItemCount();
//		RegularTimePeriod firstperiod = nodeohlc.getPeriod(itemcount-1);
		int itemcount = nodeohlc.getItemCount();
		RegularTimePeriod firstperiod = nodeohlc.getPeriod(0);
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		if(this.nodeperiodtype == TDXNodeGivenPeriodDataItem.WEEK) {
			TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
			LocalDate mondayday = startdate.with(fieldUS, 2);
			return mondayday;
		} else if(this.nodeperiodtype == TDXNodeGivenPeriodDataItem.DAY) {
			return startdate;
		}
		
		return null;
		
	}
	public LocalDate getKXxianRecordsEndDate ()
	{
		if(this.nodeohlc.getItemCount() == 0)
			return null;
		
//		int itemcount = nodeohlc.getItemCount();
//		RegularTimePeriod firstperiod = nodeohlc.getPeriod(itemcount-1);
		int itemcount = nodeohlc.getItemCount();
		RegularTimePeriod firstperiod = nodeohlc.getPeriod(itemcount-1);
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		if(this.nodeperiodtype == TDXNodeGivenPeriodDataItem.WEEK) {
			LocalDate saturday = enddate.with(DayOfWeek.SATURDAY);
			return saturday;
		} else if(this.nodeperiodtype == TDXNodeGivenPeriodDataItem.DAY) {
			return enddate;
		}
		
		return null;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#hasRecordInThePeriod(java.time.LocalDate, int)
	 */
	public Boolean hasRecordInThePeriod (LocalDate requireddate, int difference) 
	{
		if(nodeohlc == null)
			return null;
	
		 TimeSeriesDataItem stockamovaule = nodeamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference) );
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
		if(nodeohlc == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = nodeamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference) );
		if( curcjlrecord == null) 
			return null;
		
		int index = nodeamo.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference));
//		DaPan dapan = (DaPan)getRoot();
//		while ( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) && index >-1000 ) {  //上周可能大盘修饰
//			index --;
//		}
		try{
			TimeSeriesDataItem lastcjlrecord = nodeamo.getDataItem( index -1 );
			if(lastcjlrecord == null) //休市前还是空，说明要是新板块。板块没有停牌的
				return null;
			
			Double curcje = curcjlrecord.getValue().doubleValue();
			Double lastcje = lastcjlrecord.getValue().doubleValue();
			
			return curcje - lastcje;
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			return 100.0;
		}

	}
	/*
	 * 锟斤拷锟较硷拷锟斤拷锟侥成斤拷锟斤拷占锟斤拷锟斤拷锟斤拷
	 */
	public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate,int difference) 
	{
		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if( curcjlrecord == null) 
			return null;
		
		int index = nodeamo.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference));
		
		try {
			TimeSeriesDataItem lastcjlrecord = nodeamozhanbi.getDataItem( index -1  );
			if(lastcjlrecord == null) { //休市前还是空，说明要是新板块。板块没有停牌的
				logger.debug(getNodeCode() + "可能是一个新个股或板块");
				return 100.0;
			}
			
			Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
			Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
			Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
			logger.debug(getNodeCode() + "占比增速" + requireddate.toString() + zhanbigrowthrate);
			
			return zhanbigrowthrate;
			
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			return 100.0;
		}

	}
	/*
	 * 
	 */
	protected RegularTimePeriod getJFreeChartFormateTimePeriod (LocalDate requireddate,int difference) 
	{
		String nodeperiod = this.getNodeperiodtype();
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
		TimeSeriesDataItem curcjlrecord = nodeamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if( curcjlrecord == null) 
			return null;
		
		Double curcje = curcjlrecord.getValue().doubleValue();
		int itemcount = nodeamo.getItemCount();
		int maxweek = 0;
		
		int index = nodeamo.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
		
		for(int i = index-1;i >=0; i--) {
			
			TimeSeriesDataItem lastcjlrecord = nodeamo.getDataItem( i );
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
		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if( curcjlrecord == null) 
			return null;
					
		Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
		
		int index = nodeamozhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
		
		int maxweek = 0;
		for(int i = index - 1; i >= 0; i--) {
			
			
			TimeSeriesDataItem lastcjlrecord = nodeamozhanbi.getDataItem( i );
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
		if(nodeohlc == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if( curcjlrecord == null) 
			return null;

		Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
		
		int index = nodeamozhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
		int minweek = 0;
		for(int i = index -1; i >= 0; i --) {
			TimeSeriesDataItem lastcjlrecord = nodeamozhanbi.getDataItem( i );
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
		TimeSeriesDataItem curcjlrecord = this.nodeamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference) );
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
		int index = this.nodeamo.getIndex( getJFreeChartFormateTimePeriod(requireddate,difference) );
		try{
			lastcjlrecord = nodeamo.getDataItem( index - 1);
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
		 if(nodeexchangedaysnumber == null)
			 return null;
		 
		 TimeSeriesDataItem curdaynumberrecord = this.nodeexchangedaysnumber.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		 if( curdaynumberrecord == null) 
				return 5;
		 else
			 return curdaynumberrecord.getValue().intValue();
		 
	 }
	 /*
	  * (non-Javadoc)
	  * @see com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic#getLianXuFangLiangPeriodNumber(java.time.LocalDate, int, int)
	  * 计算连续放指定的量的周期数
	  */
	 public Integer getLianXuFangLiangPeriodNumber (LocalDate requireddate,int difference,int settindpgmaxwk)
	 {
		 	
		 	int recordnum = this.nodeohlc.getItemCount();
			int lianxu = 0;
			for(int wkindex = 0;wkindex > (0 - recordnum) ; wkindex--) { 
				Integer recordmaxbkwklast = this.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(requireddate,wkindex);
				if( recordmaxbkwklast != null && recordmaxbkwklast >= settindpgmaxwk) 
					lianxu ++;
				else if( recordmaxbkwklast != null && recordmaxbkwklast < settindpgmaxwk)
					return lianxu;
//				else if(recordmaxbkwklast == null )
//						;
			}
			
			return -1;

	 }
	 /*
	  * 
	  */
	 public Double[] getNodeAmoMA (LocalDate  requireddate,int difference)
	 {
		 RegularTimePeriod period = getJFreeChartFormateTimePeriod(requireddate,difference);
		 Double ma5;Double ma10;Double ma20;Double ma30;Double ma60;Double ma120;Double ma250;
		 try {
			 TimeSeriesDataItem qktjitem = this.nodema5.getDataItem(period );
			 if(qktjitem == null) {
				 ma5 = calculateAmoMA (5,requireddate,difference);
				 if(ma5 != null)
					 this.nodema5.add(period,ma5);
			 } else
				ma5 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodema5 = new TimeSeries(this.nodeperiodtype);
			 	ma5 = calculateAmoMA (5,requireddate,difference);
			 	if(ma5 != null)
			 		this.nodema5.add(period,ma5);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodema10.getDataItem(period );
			 if(qktjitem == null) {
				 ma10 = calculateAmoMA (10,requireddate,difference);
				 if(ma10 != null)
					 this.nodema10.add(period,ma10);
			 } else
				ma10 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodema10 = new TimeSeries(this.nodeperiodtype);
			 	ma10 = calculateAmoMA (10,requireddate,difference);
			 	if(ma10 != null)
			 		this.nodema10.add(period,ma10);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodema20.getDataItem(period );
			 if(qktjitem == null) {
				 ma20 = calculateAmoMA (20,requireddate,difference);
				 if(ma20 != null)
					 this.nodema20.add(period,ma20);
			 } else
				ma20 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodema20 = new TimeSeries(this.nodeperiodtype);
			 	ma20 = calculateAmoMA (20,requireddate,difference);
			 	if(ma20 != null)
			 		this.nodema20.add(period,ma20);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodema30.getDataItem(period );
			 if(qktjitem == null) {
				 ma30 = calculateAmoMA (30,requireddate,difference);
				 if(ma30 != null)
					 this.nodema30.add(period,ma30);
			 } else
				ma30 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodema30 = new TimeSeries(this.nodeperiodtype);
			 	ma30 = calculateAmoMA (30,requireddate,difference);
			 	if(ma30 != null)
			 		this.nodema30.add(period,ma30);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodema60.getDataItem(period );
			 if(qktjitem == null) {
				 ma60 = calculateAmoMA (60,requireddate,difference);
				 if(ma60 != null)
					 this.nodema60.add(period,ma60);
			 } else
				ma60 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodema60 = new TimeSeries(this.nodeperiodtype);
			 	ma60 = calculateAmoMA (60,requireddate,difference);
			 	if(ma60 != null)
			 		this.nodema60.add(period,ma60);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodema120.getDataItem(period );
			 if(qktjitem == null) {
				 ma120 = calculateAmoMA (120,requireddate,difference);
				 if(ma120 != null)
					 this.nodema120.add(period,ma120);
			 } else
				ma120 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodema120 = new TimeSeries(this.nodeperiodtype);
			 	ma120 = calculateAmoMA (120,requireddate,difference);
			 	if(ma120 != null)
			 		this.nodema120.add(period,ma120);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodema250.getDataItem(period );
			 if(qktjitem == null) {
				 ma250 = calculateAmoMA (250,requireddate,difference);
				 if(ma250 != null)
					 this.nodema250.add(period,ma250);
			 } else
				ma250 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodema250 = new TimeSeries(this.nodeperiodtype);
			 	ma250 = calculateAmoMA (250,requireddate,difference);
			 	if(ma250 != null)
			 		this.nodema250.add(period,ma250);
		 }
		 
		 Double ma[] = {ma5,ma10,ma20,ma30,ma60,ma120,ma250};
		 return ma;
		 
	 }
	 private Double calculateAmoMA (Integer manum,LocalDate  requireddate,int difference)
	 {
		 RegularTimePeriod period = getJFreeChartFormateTimePeriod(requireddate,difference);
		 
		 LocalDate expectedate = requireddate.plus(difference,ChronoUnit.DAYS);
		 DayOfWeek dayofweek = expectedate.getDayOfWeek();
		 if(dayofweek.equals(DayOfWeek.SUNDAY) ) {
			 requireddate = expectedate.minus(2,ChronoUnit.DAYS);
		 } else if(dayofweek.equals(DayOfWeek.SATURDAY) ) {
			 requireddate = expectedate.minus(1,ChronoUnit.DAYS);
		 }
		 
		 Integer indexofrc = this.nodeamo.getIndex(period);
		 if(indexofrc < manum -1 )
				return null;
		
//		 TimeSeriesDataItem curcjlrecord = null;
//		 curcjlrecord = nodeamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//			
//		 if( curcjlrecord == null) 
//				return null;
//		 else
//				return curcjlrecord.getValue().doubleValue();
		 
		
		Double ohlcsum = 0.0;
		for(int i=0;i<manum;i++) {
			TimeSeriesDataItem data = (TimeSeriesDataItem) this.nodeamo.getDataItem(indexofrc -i);
			ohlcsum = ohlcsum + (Double)data.getValue().doubleValue();
		}
		
		Double result = ohlcsum / manum;
		return result;
	 } 
	 /*
	  * 
	  */
	 public Double[] getNodeOhlcMA (LocalDate  requireddate,int difference)
	 {
		 RegularTimePeriod period = getJFreeChartFormateTimePeriod(requireddate,difference);
		 Double ma5;Double ma10;Double ma20;Double ma30;Double ma60;Double ma120;Double ma250;
		 try {
			 TimeSeriesDataItem qktjitem = this.nodema5.getDataItem(period );
			 if(qktjitem == null) {
				 ma5 = calculateOhlcMA (5,requireddate,difference);
				 if(ma5 != null)
					 this.nodema5.add(period,ma5);
			 } else
				ma5 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodema5 = new TimeSeries(this.nodeperiodtype);
			 	ma5 = calculateOhlcMA (5,requireddate,difference);
			 	if(ma5 != null)
			 		this.nodema5.add(period,ma5);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodema10.getDataItem(period );
			 if(qktjitem == null) {
				 ma10 = calculateOhlcMA (10,requireddate,difference);
				 if(ma10 != null)
					 this.nodema10.add(period,ma10);
			 } else
				ma10 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodema10 = new TimeSeries(this.nodeperiodtype);
			 	ma10 = calculateOhlcMA (10,requireddate,difference);
			 	if(ma10 != null)
			 		this.nodema10.add(period,ma10);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodema20.getDataItem(period );
			 if(qktjitem == null) {
				 ma20 = calculateOhlcMA (20,requireddate,difference);
				 if(ma20 != null)
					 this.nodema20.add(period,ma20);
			 } else
				ma20 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodema20 = new TimeSeries(this.nodeperiodtype);
			 	ma20 = calculateOhlcMA (20,requireddate,difference);
			 	if(ma20 != null)
			 		this.nodema20.add(period,ma20);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodema30.getDataItem(period );
			 if(qktjitem == null) {
				 ma30 = calculateOhlcMA (30,requireddate,difference);
				 if(ma30 != null)
					 this.nodema30.add(period,ma30);
			 } else
				ma30 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodema30 = new TimeSeries(this.nodeperiodtype);
			 	ma30 = calculateOhlcMA (30,requireddate,difference);
			 	if(ma30 != null)
			 		this.nodema30.add(period,ma30);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodema60.getDataItem(period );
			 if(qktjitem == null) {
				 ma60 = calculateOhlcMA (60,requireddate,difference);
				 if(ma60 != null)
					 this.nodema60.add(period,ma60);
			 } else
				ma60 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodema60 = new TimeSeries(this.nodeperiodtype);
			 	ma60 = calculateOhlcMA (60,requireddate,difference);
			 	if(ma60 != null)
			 		this.nodema60.add(period,ma60);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodema120.getDataItem(period );
			 if(qktjitem == null) {
				 ma120 = calculateOhlcMA (120,requireddate,difference);
				 if(ma120 != null)
					 this.nodema120.add(period,ma120);
			 } else
				ma120 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodema120 = new TimeSeries(this.nodeperiodtype);
			 	ma120 = calculateOhlcMA (120,requireddate,difference);
			 	if(ma120 != null)
			 		this.nodema120.add(period,ma120);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodema250.getDataItem(period );
			 if(qktjitem == null) {
				 ma250 = calculateOhlcMA (250,requireddate,difference);
				 if(ma250 != null)
					 this.nodema250.add(period,ma250);
			 } else
				ma250 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodema250 = new TimeSeries(this.nodeperiodtype);
			 	ma250 = calculateOhlcMA (250,requireddate,difference);
			 	if(ma250 != null)
			 		this.nodema250.add(period,ma250);
		 }
		 
		 Double ma[] = {ma5,ma10,ma20,ma30,ma60,ma120,ma250};
		 return ma;
	 }
	 private Double calculateOhlcMA (Integer manum,LocalDate  requireddate,int difference)
	 {
		 LocalDate expectedate = requireddate.plus(difference,ChronoUnit.DAYS);
		 DayOfWeek dayofweek = expectedate.getDayOfWeek();
		 if(dayofweek.equals(DayOfWeek.SUNDAY) ) {
			 requireddate = expectedate.minus(2,ChronoUnit.DAYS);
		 } else if(dayofweek.equals(DayOfWeek.SATURDAY) ) {
			 requireddate = expectedate.minus(1,ChronoUnit.DAYS);
		 } 
		
		 Integer indexofrc = this.getIndexOfSpecificDateOHLCData(requireddate, 0);
		if(indexofrc < manum -1 )
			return null;
		
		Double ohlcsum = 0.0;
		for(int i=0;i<manum;i++) {
			OHLCItem data = (OHLCItem) this.nodeohlc.getDataItem(indexofrc -i);
			ohlcsum = ohlcsum + (Double)data.getCloseValue();
		}
		
		Double result = ohlcsum / manum;
		return result;
	 }
	 	 
 } //END OF 
