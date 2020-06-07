package com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.math.stat.StatUtils;
import org.apache.log4j.Logger;
import org.jfree.data.ComparableObjectItem;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Week;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;


import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.FormatDoubleToShort;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.TDXNodesXPeriodExternalData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItemForJFC;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ObjectArrays;
import com.udojava.evalex.Expression;

/*
 *  
 */
 public abstract class TDXNodesXPeriodDataForJFC extends TDXNodesXPeriodExternalData
 {
	public TDXNodesXPeriodDataForJFC (String nodecode,String nodeperiodtype)
	{
		super(nodecode,nodeperiodtype);
		
		nodeohlc = new OHLCSeries(nodeperiodtype);
		nodeamo = new TimeSeries(nodeperiodtype);
		nodevol = new TimeSeries(nodeperiodtype);
		
		this.nodeohlcma5 = new TimeSeries(nodeperiodtype);
		this.nodeohlcma10 = new TimeSeries(nodeperiodtype);
		this.nodeohlcma20 = new TimeSeries(nodeperiodtype);
		this.nodeohlcma30 = new TimeSeries(nodeperiodtype);
		this.nodeohlcma60 = new TimeSeries(nodeperiodtype);
		this.nodeohlcma120 = new TimeSeries(nodeperiodtype);
		this.nodeohlcma250 = new TimeSeries(nodeperiodtype);
		
		this.nodeamoma5 = new TimeSeries(nodeperiodtype);
		this.nodeamoma10 = new TimeSeries(nodeperiodtype);
	}
	
	private Logger logger = Logger.getLogger(TDXNodesXPeriodDataForJFC.class);

	protected OHLCSeries nodeohlc; 
	protected TimeSeries nodeamo; 
	protected TimeSeries nodevol; 

	//均线
	protected TimeSeries nodeohlcma5;
	protected TimeSeries nodeohlcma10;
	protected TimeSeries nodeohlcma20;
	protected TimeSeries nodeohlcma30;
	protected TimeSeries nodeohlcma60;
	protected TimeSeries nodeohlcma120;
	protected TimeSeries nodeohlcma250;
	
	protected TimeSeries nodeamoma5;
	protected TimeSeries nodeamoma10;
	protected TimeSeries nodeamoma20;
	protected TimeSeries nodeamoma30;
	protected TimeSeries nodeamoma60;
	protected TimeSeries nodeamoma120;
	protected TimeSeries nodeamoma250;
	
	public void addNewXPeriodData (NodeGivenPeriodDataItem kdata)
	{
		try {
			nodeohlc.setNotify(false);
			nodeohlc.add( (NodeGivenPeriodDataItemForJFC)kdata);
		} catch (org.jfree.data.general.SeriesException e) {
//			logger.debug(kdata.getMyOwnCode() + kdata.getJFreeChartPeriod( super.getNodeperiodtype() ) + "锟斤拷锟斤拷锟窖撅拷锟斤拷锟节ｏ拷" + kdata.getJFreeChartPeriod( super.getNodeperiodtype() ).getStart() + "," + kdata.getJFreeChartPeriod( super.getNodeperiodtype() ).getEnd() + ")");
		} catch (java.lang.IllegalArgumentException e) {
//			e.printStackTrace();
		}
		
		try {
			nodeamo.setNotify(false);
			nodevol.setNotify(false);
			nodeamo.add(kdata.getJFreeChartPeriod( super.getNodeperiodtype()  ),kdata.getMyOwnChengJiaoEr(),false);
			nodevol.add(kdata.getJFreeChartPeriod( super.getNodeperiodtype() ), kdata.getMyOwnChengJiaoLiang(),false);

		} catch (org.jfree.data.general.SeriesException e) {
//			logger.debug(kdata.getMyOwnCode() + kdata.getJFreeChartPeriod( super.getNodeperiodtype() ) + "锟斤拷锟斤拷锟窖撅拷锟斤拷锟节ｏ拷" + kdata.getJFreeChartPeriod( super.getNodeperiodtype() ).getStart() + "," + kdata.getJFreeChartPeriod( super.getNodeperiodtype() ).getEnd() + ")");
		}
		
		super.addNewXPeriodData(kdata);
	}
	
	public LocalDate getAmoRecordsStartDate ()
	{
		if(super.nodeamozhanbi.getItemCount() == 0)
			return null;
		
		int itemcount = super.nodeamozhanbi.getItemCount();
		TimeSeriesDataItem dataitem = super.nodeamozhanbi.getDataItem(0);
		Date start = dataitem.getPeriod().getStart();
		LocalDate startdate = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate enddate = dataitem.getPeriod().getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		if(super.getNodeperiodtype() == NodeGivenPeriodDataItem.WEEK) {
			TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
			LocalDate mondayday = startdate.with(fieldUS, 2);
			return mondayday;
		} else if(super.getNodeperiodtype() == NodeGivenPeriodDataItem.DAY) {
			return startdate;
		}
		
		return null;
	}
	public LocalDate getAmoRecordsEndDate ()
	{
		if(super.nodeamozhanbi.getItemCount() == 0)
			return null;
		
		int itemcount = super.nodeamozhanbi.getItemCount();
		TimeSeriesDataItem dataitem = super.nodeamozhanbi.getDataItem(itemcount - 1);
		Date start = dataitem.getPeriod().getStart();
		LocalDate startdate = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate enddate = dataitem.getPeriod().getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		if(super.getNodeperiodtype() == NodeGivenPeriodDataItem.WEEK) {
			TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
			LocalDate fridayday = enddate.with(fieldUS, 6);
			return fridayday;
		} else if(super.getNodeperiodtype() == NodeGivenPeriodDataItem.DAY) {
			return enddate;
		}
		
		return null;
	}
	public void resetAllData ()
	{
		super.resetAllData();
		try {
			nodeohlc.clear(); 
		}  catch (java.lang.NullPointerException e) {
		
		}
		try {
			nodeamo.clear(); 
		}   catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodevol.clear(); 
		}   catch (java.lang.NullPointerException e) {
			
		}

		//均线
		try {
			nodeohlcma5.clear();
		}  catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeohlcma10.clear();
		}  catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeohlcma20.clear();
		}  catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeohlcma30.clear();
		}  catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeohlcma60.clear();
		}  catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeohlcma120.clear();
		}  catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeohlcma250.clear();
		}  catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeamoma5.clear();
		}  catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeamoma10.clear();
		}  catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeamoma20.clear();
		}  catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeamoma30.clear();
		}  catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeamoma60.clear();
		}  catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeamoma120.clear();
		}  catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeamoma250.clear();
		}  catch (java.lang.NullPointerException e) {
			
		}
	}
	/*
	 * 
	 */
	public OHLCSeries getOHLCData ()
	{
		return this.nodeohlc;
	}
	public TimeSeries getMA250 ()
	{
		return this.nodeohlcma250;
	}
	public TimeSeries getMA60 ()
	{
		return this.nodeohlcma60;
	}
	/*
	 * 
	 */
	public TimeSeries getAMOData ()
	{
		return this.nodeamo;
	}
	/*
	 * 
	 */
	public org.ta4j.core.TimeSeries getOHLCDataOfTa4jFormat (LocalDate requiredstart, LocalDate requiredend)
	{
		if(this.nodeohlc == null)
			return null;
		
		org.ta4j.core.TimeSeries ohlcvaseries = new BaseTimeSeries.SeriesBuilder().withName(super.getNodeCode() + super.getNodeperiodtype()).build();
		LocalDate tmpdate = requiredstart;
		for(int i=0;i<this.nodeohlc.getItemCount();i++) {
			 OHLCItem dataitem = (OHLCItem)this.nodeohlc.getDataItem(i);
			 RegularTimePeriod period = dataitem.getPeriod();
			 tmpdate = period.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			 if(tmpdate.isBefore(requiredstart) )
				 continue;
			 if(tmpdate.isAfter(requiredend))
				 break;
			 
			 double close = dataitem.getCloseValue();
			 double high = dataitem.getHighValue();
			 double low = dataitem.getLowValue();
			 double open = dataitem.getOpenValue();
			 
			 ZonedDateTime zdtime = tmpdate.atStartOfDay(ZoneOffset.UTC);
			 
			 
			 Bar ohlcbar = new BaseBar (zdtime,PrecisionNum.valueOf(open),PrecisionNum.valueOf(high),PrecisionNum.valueOf(low),PrecisionNum.valueOf(close),null,null);
			 ohlcvaseries.addBar(ohlcbar);
			
		}
		
		return ohlcvaseries;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic#getSpecificDateOHLCData(java.time.LocalDate, int)
	 */
	public Integer getIndexOfSpecificDateOHLCData (LocalDate requireddate,int difference)
	{
		int itemcount = this.nodeohlc.getItemCount();
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForOHLC (requireddate, difference);
		if(curperiod == null)
			return null;
		
		for(int i=0;i<itemcount;i++) {
			RegularTimePeriod dataitemp = this.nodeohlc.getPeriod(i);
			if(dataitemp.equals(curperiod) )
				 return i ;
		}

		return null;
	}
	/*
	 * 
	 */
	protected RegularTimePeriod getJFreeChartFormateTimePeriodForOHLC (LocalDate requireddate,int difference) 
	{
		int itemcount = this.nodeohlc.getItemCount();
		String nodeperiod = this.getNodeperiodtype();
		RegularTimePeriod period = null;
		if(nodeperiod.equals(NodeGivenPeriodDataItem.WEEK)) { 
			LocalDate expectdate = requireddate.plus(difference,ChronoUnit.WEEKS);
			java.sql.Date lastdayofweek = java.sql.Date.valueOf(expectdate);
			period = new org.jfree.data.time.Week (lastdayofweek);
		} else if(nodeperiod.equals(NodeGivenPeriodDataItem.DAY)) {
			LocalDate expectdate = requireddate.plus(difference,ChronoUnit.DAYS);
			expectdate = super.adjustDate(expectdate);
			java.sql.Date lastdayofweek = java.sql.Date.valueOf(expectdate);
			period = new org.jfree.data.time.Day (lastdayofweek);
		}  else if(nodeperiod.equals(NodeGivenPeriodDataItem.MONTH)) {
		}

		int curindex = 0;
		for(int i=0;i<itemcount;i++) {
			RegularTimePeriod dataitemp = this.nodeohlc.getPeriod(i);
			if(dataitemp.equals(period) ) {
				curindex = i;
				
				try {
					OHLCItem result = (OHLCItem)this.nodeohlc.getDataItem(curindex);
					if(result != null) {
						RegularTimePeriod ep = result.getPeriod();
						return result.getPeriod();
					}
				} catch ( java.lang.IndexOutOfBoundsException e) {
					return null;
				}
				
				break;
			}
		}
		
		return null;
	}
	public LocalDate getLocalDateOfSpecificIndexOfOHLCData (Integer index)
	{
		OHLCItem indexrecord = (OHLCItem)this.nodeohlc.getDataItem(index);
		Date end = indexrecord.getPeriod().getEnd();
		return end.toInstant()
			      .atZone(ZoneId.systemDefault())
			      .toLocalDate();
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#getOHLCData(java.time.LocalDate, int)
	 */
	public OHLCItem getSpecificDateOHLCData (LocalDate requireddate,int difference)
	{
		int itemcount = this.nodeohlc.getItemCount();
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForOHLC (requireddate,difference);
		
		for(int i=0;i<itemcount;i++) {
			RegularTimePeriod dataitemp = this.nodeohlc.getPeriod(i);
			if(dataitemp.equals(curperiod) )
				 return (OHLCItem)this.nodeohlc.getDataItem(i);
		}

		return null;
	}
	
	/*
	 * 某天的涨跌幅
	 */
	public Double getSpecificOHLCZhangDieFu (LocalDate requireddate,int difference)
	{
		
		Integer indexofcur = this.getIndexOfSpecificDateOHLCData(requireddate, difference);
		if(indexofcur == null)
			return null;
		
		OHLCItem curohlc;
		try {
			curohlc = (OHLCItem) this.getOHLCData().getDataItem(indexofcur.intValue());
			
			double curclose = curohlc.getCloseValue();
			
			OHLCItem lastholc = null; Double lastclose = 0.0;
			try {
				lastholc = (OHLCItem) this.getOHLCData().getDataItem(indexofcur.intValue() -1 );
				lastclose = lastholc.getCloseValue();
			}	catch (java.lang.IndexOutOfBoundsException ex) { //第一周
				return null;
			}
			
			if(lastclose == 0.0)
				return null;
			
			double zhangfu = (curclose - lastclose) / lastclose;
			
			return zhangfu;
		} catch (java.lang.NullPointerException e) {
			String code = super.getNodeCode();
			e.printStackTrace();
		}
		
		return null;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData#getOHLCRecordsStartDate()
	 */
	public Double getSpecificTimeRangeOHLCHightestZhangFu (LocalDate requiredstart,LocalDate requiredend)
	{
		LocalDate curohlcstart = this.getOHLCRecordsStartDate();
		LocalDate curohlcend = this.getOHLCRecordsEndDate();
		if(requiredstart.isBefore(curohlcstart) || requiredend.isAfter(curohlcend) )
			return null;

		Integer indexofstart = this.getIndexOfSpecificDateOHLCData(requiredstart, 0);
		if(indexofstart == null) { //没有可能是停牌，往后找
			int itemcount = this.nodeohlc.getItemCount();
			for(int i=0;i<itemcount;i++) {
				RegularTimePeriod dataitemp = this.nodeohlc.getPeriod(i);
				if( dataitemp.getStart().after( java.util.Date.from( requiredstart.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() ) )
					&& dataitemp.getStart().before( java.util.Date.from( requiredend.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() ) )
						) {
					indexofstart = i;
					requiredstart = dataitemp.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					break;
				}
			}
		}
		
		Integer indexofend = this.getIndexOfSpecificDateOHLCData(requiredend, 0);
		if(indexofend == null) { //没有可能是停牌，往后找
			int itemcount = this.nodeohlc.getItemCount();
			for(int i=itemcount -1; i>=0; i--) {
				RegularTimePeriod dataitemp = this.nodeohlc.getPeriod(i);
				if( dataitemp.getStart().before( java.util.Date.from( requiredend.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() ) )
					&& dataitemp.getStart().after( java.util.Date.from( requiredstart.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() ) )
						) {
					indexofend = i;
					requiredend = dataitemp.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					break;
				}
			}
		}
		
		if(indexofstart == null || indexofend == null)
			return null;
		
		
		if(indexofstart < indexofend) { //不是同一天
			OHLCItem dataitem = (OHLCItem) this.getOHLCData().getDataItem(indexofstart.intValue());
			double startclose = dataitem.getCloseValue();
			
			Double highest = startclose;
			for(int i = indexofstart + 1; i<=indexofend ; i++) {
				OHLCItem tmpohlc = (OHLCItem) this.getOHLCData().getDataItem(i);
				double tmphigh = tmpohlc.getCloseValue();
				if(tmphigh > highest)
					highest = tmphigh;
			}
			
			double result = (highest - startclose) / startclose + this.getSpecificOHLCZhangDieFu(requiredstart, 0);
			return result;
			
//			if(highest > startclose) { //
//				double result = (highest - startclose) / startclose + this.getSpecificOHLCZhangDieFu(requiredstart, 0);
//				return result;
//			} else //头一天就是最高值
//				return this.getSpecificOHLCZhangDieFu(requiredstart, 0);
			
		} else { //start 和 end是同一天
			return this.getSpecificOHLCZhangDieFu(requiredstart, 0);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData#getOHLCRecordsStartDate()
	 */
	public LocalDate getOHLCRecordsStartDate ()
	{
		if(this.nodeohlc.getItemCount() == 0)
			return null;
		
		int itemcount = nodeohlc.getItemCount();
		RegularTimePeriod firstperiod = nodeohlc.getPeriod(0);
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		if(super.getNodeperiodtype() == NodeGivenPeriodDataItem.WEEK) {
			TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
			LocalDate mondayday = startdate.with(fieldUS, 2);
			return mondayday;
		} else if(super.getNodeperiodtype() == NodeGivenPeriodDataItem.DAY) {
			return startdate;
		}
		
		return null;
	}
	public LocalDate getOHLCRecordsEndDate ()
	{
		if(this.nodeohlc.getItemCount() == 0)
			return null;
		
		int itemcount = nodeohlc.getItemCount();
		RegularTimePeriod firstperiod = nodeohlc.getPeriod( itemcount-1);
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		if(super.getNodeperiodtype() == NodeGivenPeriodDataItem.WEEK) {
			LocalDate saturday = enddate.with(DayOfWeek.SATURDAY);
			return saturday;
		} else if(super.getNodeperiodtype() == NodeGivenPeriodDataItem.DAY) {
			return enddate;
		}
		
		return null;
	}

	/*
	 * 
	 */
	public Double getChengJiaoEr (LocalDate requireddate,int difference)
	{
		TimeSeriesDataItem curcjlrecord = null;
		RegularTimePeriod period = super.getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(period == null)
			return null;
					
		curcjlrecord = nodeamo.getDataItem(period);
		if( curcjlrecord == null) 
			return null;
		else
			return curcjlrecord.getValue().doubleValue();
	}
	/*
	 * 
	 */
	public Double getAverageDailyChengJiaoErOfWeek (LocalDate requireddate,int difference)
	{
		Double cje = this.getChengJiaoEr(requireddate, 0);
		if(cje != null) {
			Integer daynum = super.getExchangeDaysNumberForthePeriod(requireddate, 0);
			if(daynum != null)
				return cje/daynum;
			else
				return cje/5;
		} else
			return null;
	}
	
	/*
	 * /*
			 * 计算指定周期和上周期的成交额差额，适合stock/bankuai，dapan有自己的计算方法
	 */

	public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate,int difference)
	{
		if(nodeohlc == null)
			return null;
		
		RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriodForAMO(requireddate,difference) ;
		if(curperiod == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = nodeamo.getDataItem( curperiod);
		if( curcjlrecord == null) 
			return null;
		
		int index = nodeamo.getIndex(curperiod);
		try{
			TimeSeriesDataItem lastcjlrecord = nodeamo.getDataItem( index -1 );
			if(lastcjlrecord == null) //休市前还是空，说明要是新板块。板块没有停牌的
				return null;
			
			Double curcje = curcjlrecord.getValue().doubleValue();
			Double lastcje = lastcjlrecord.getValue().doubleValue();
			
			return curcje - lastcje;
		}	catch (java.lang.IndexOutOfBoundsException ex) {
			return 100.0;
		}

	}
	/*
	 * 每日均量和上周均量的差额
	 */
	public Double getChengJiaoErDailyAverageDifferenceWithLastPeriod(LocalDate requireddate,int difference)
	{
		if(nodeohlc == null)
			return null;
		
		RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriodForAMO(requireddate,difference) ;
		if(curperiod == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = nodeamo.getDataItem( curperiod );
		if( curcjlrecord == null) 
			return null;
		
		Integer curexchangedaynum = super.getExchangeDaysNumberForthePeriod(requireddate,difference);
		int index = nodeamo.getIndex(curperiod);
		try{
			TimeSeriesDataItem lastcjlrecord = nodeamo.getDataItem( index -1 );
			if(lastcjlrecord == null) 
				return null;
			
			RegularTimePeriod lastp = lastcjlrecord.getPeriod();
			Date end = lastp.getEnd();
			LocalDate lastdate = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			Integer lastexchangedaynum = super.getExchangeDaysNumberForthePeriod(lastdate,difference);
			
			double curcje = curcjlrecord.getValue().doubleValue() ;
			Double curcjeave = curcje/ curexchangedaynum;
			double lastcje = lastcjlrecord.getValue().doubleValue();
			Double lastcjeave = lastcje / lastexchangedaynum;
			
			double result = curcjeave - lastcjeave;
			return result;
		}	catch (java.lang.IndexOutOfBoundsException ex) {
			return 100.0;
		}

	}
	@Override
	public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference) 
	{
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference) ;
		if(curperiod == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = nodeamo.getDataItem(curperiod);
		if( curcjlrecord == null) 
			return null;
		
		Double curcje = curcjlrecord.getValue().doubleValue();
		int maxweek = 0;
		
		int index = nodeamo.getIndex(curperiod );
		
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
	@Override
	public Integer getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference)
	{
		RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriodForAMO(requireddate,difference) ;
		if(curperiod == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = nodeamo.getDataItem( curperiod);
		if( curcjlrecord == null) 
			return null;
		
		int curexchangedaynum = this.getExchangeDaysNumberForthePeriod(requireddate, difference);
		Double curcje = curcjlrecord.getValue().doubleValue() / curexchangedaynum;
		
		int maxweek = 0;
		int index = nodeamo.getIndex(curperiod );
		for(int i = index-1;i >=0; i--) {
			
			TimeSeriesDataItem lastcjlrecord = nodeamo.getDataItem( i );
			if(lastcjlrecord == null ) //可能到了记录的头部了，或者是个诞生时间不长的板块
				return maxweek;
			
			LocalDate lastdate = lastcjlrecord.getPeriod().getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			int lastexchangedaynum = this.getExchangeDaysNumberForthePeriod(lastdate, 0);
			Double lastcje = lastcjlrecord.getValue().doubleValue() / lastexchangedaynum;
			if(curcje > lastcje)
				maxweek ++;
			else
				break;
		}

		return maxweek;
	}
	/*
	 * 计算成交额变化贡献率，即板块成交额的变化占整个上级板块成交额增长量的比率，
	 */
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai (TDXNodes superbk, LocalDate requireddate,int difference) 
	{
		RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriodForAMO(requireddate,difference) ;
		if(curperiod == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = this.nodeamo.getDataItem( curperiod );
		if( curcjlrecord == null) 
			return null;
		
		//判断上级板块(大盘或者板块)是否缩量,所以了没有比较的意义，直接返回-100；
		String nodept = getNodeperiodtype();
		NodeXPeriodData bkxdata = superbk.getNodeXPeroidData(nodept);
		Double bkcjediff = bkxdata.getChengJiaoErDifferenceWithLastPeriod(requireddate,difference);
		if( bkcjediff == null || bkcjediff < 0   ) {//板块缩量，
			return -100.0;
		}
		
		TimeSeriesDataItem lastcjlrecord = null;
		int index = this.nodeamo.getIndex( curperiod );
		try{
			lastcjlrecord = nodeamo.getDataItem( index - 1);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			logger.debug("index = 0，可能是新股第一周，可能是数据记录最早记录周，无法判断");
//			return 100.0; //一般不会查到记录的第一周，多数发生这种情况是新股第一周，所以默认为新股第一周
//			e.printStackTrace();
		}
		if(lastcjlrecord == null) { //说明是停牌后复牌了，或者新股
			try {
			Double curggcje = curcjlrecord.getValue().doubleValue(); //新板块所有成交量都应该计算入
			return curggcje/bkcjediff;
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		
		Double curcje = curcjlrecord.getValue().doubleValue();
		Double lastcje = lastcjlrecord.getValue().doubleValue();
		Double cjechange = curcje - lastcje; //个股成交量的变化，如果缩量了也还是计算
		
		return cjechange/bkcjediff;
	}
	/*
	 * 计算成交额日平均变化贡献率，即基于日平均成交额，板块成交额的变化占整个上级板块成交额增长量的比率，
	 */
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage (TDXNodes superbk, LocalDate requireddate,int difference) 
	{	
		RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriodForAMO(requireddate,difference) ;
		if(curperiod == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = this.nodeamo.getDataItem( curperiod );
		if( curcjlrecord == null) 
			return null;
		
//		Double curavecje = this.getAverageDailyChengJiaoErOfWeek(requireddate, difference);
//		if( curavecje == null) 
//			return null;
		
		//判断上级板块(大盘或者板块)是否缩量,所以了没有比较的意义，直接返回-100；
		String nodept = getNodeperiodtype();
		NodeXPeriodData bkxdata = superbk.getNodeXPeroidData(nodept);
		Double bkcjediff = bkxdata.getChengJiaoErDailyAverageDifferenceWithLastPeriod(requireddate,difference);
		if(bkcjediff == null )
			return null;
		if(  bkcjediff < 0   ) {//板块缩量，
			return -100.0;
		}
		
		TimeSeriesDataItem lastcjlrecord = null;
		int index = this.nodeamo.getIndex( curperiod );
		try{
			lastcjlrecord = nodeamo.getDataItem( index - 1);
		}	catch (java.lang.IndexOutOfBoundsException ex) {
			logger.debug("index = 0，可能是新股第一周，可能是数据记录最早记录周，无法判断");
			Boolean reachfirstday = super.isLocalDateReachFristDayInHistory (requireddate,difference); 
			if(reachfirstday != null && reachfirstday == true)
				return null;
		}
		if(this.isNodeDataFuPaiAfterTingPai(superbk,requireddate,0)) { //说明是停牌后复牌了，或者新股
			try {
				Double curggcje = this.getAverageDailyChengJiaoErOfWeek(requireddate, difference); //新板块所有成交量都应该计算入
				return curggcje/bkcjediff;
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		
		Double nodeavediff = this.getChengJiaoErDailyAverageDifferenceWithLastPeriod(requireddate,difference);
		return nodeavediff/bkcjediff;
	}
	/*
	  * (non-Javadoc)
	  * @see com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic#getLianXuFangLiangPeriodNumber(java.time.LocalDate, int, int)
	  * 计算连续放指定的量的周期数
	  */
	 public Integer getAverageDailyCjeLianXuFangLiangPeriodNumber (LocalDate requireddate,int difference)
	 {
		 RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriodForAMO(requireddate,difference) ;
			if(curperiod == null)
				return null;
			
		 TimeSeriesDataItem curcjlrecord = nodeamo.getDataItem( curperiod);
		 if( curcjlrecord == null) 
				return null;
			
		 int curexchangedaynum = this.getExchangeDaysNumberForthePeriod(requireddate, difference);
		 Double curcje = curcjlrecord.getValue().doubleValue() / curexchangedaynum;
		 int maxweek = 0;
			
		 int index = nodeamo.getIndex(curperiod );
			
		 for(int i = index-1;i >=0; i--) {
				
				TimeSeriesDataItem lastcjlrecord = nodeamo.getDataItem( i );
				if(lastcjlrecord == null ) //可能到了记录的头部了，或者是个诞生时间不长的板块
					return maxweek;
				
				LocalDate lastdate = lastcjlrecord.getPeriod().getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				int lastexchangedaynum = this.getExchangeDaysNumberForthePeriod(lastdate, 0);
				Double lastcje = lastcjlrecord.getValue().doubleValue() / lastexchangedaynum;
				if(curcje > lastcje) {
					maxweek ++;
					curcje = lastcje;
				}
				else
					break;
		}

		return maxweek;
		 
	 }
	 /*
	  *  @Override
	  */
	 public Integer getCjeDpMaxLianXuFangLiangPeriodNumber (LocalDate requireddate,int difference,int settindpgmaxwk)
	 {
		 	
		 	int recordnum = this.nodeohlc.getItemCount();
			int lianxu = 0;
			for(int wkindex = 0;wkindex > (0 - recordnum) ; wkindex--) { 
				Integer recordmaxbkwklast = this.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(requireddate,wkindex);
				if( recordmaxbkwklast != null && recordmaxbkwklast >= settindpgmaxwk) 
					lianxu ++;
				else if( recordmaxbkwklast != null && recordmaxbkwklast < settindpgmaxwk)
					return lianxu;
			}
			
			return -1;
	 }
	 @Override
		public Double getChengJiaoLiang(LocalDate requireddate, int difference) 
		{
		 	RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriodForAMO(requireddate,difference) ;
			if(curperiod == null)
				return null;
			
			TimeSeriesDataItem curcjlrecord = null;
			curcjlrecord = nodevol.getDataItem( curperiod);
			
			if( curcjlrecord == null) 
				return null;
			else
				return curcjlrecord.getValue().doubleValue();
		}
	 @Override
		public Double getChenJiaoLiangDifferenceWithLastPeriod(LocalDate requireddate, int difference) 
		{
			if(nodeohlc == null)
				return null;
			
			RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriodForAMO(requireddate,difference) ;
			if(curperiod == null)
				return null;
			
			TimeSeriesDataItem curcjlrecord = nodevol.getDataItem( curperiod);
			if( curcjlrecord == null) 
				return null;
			
			int index = nodevol.getIndex(curperiod);
//			DaPan dapan = (DaPan)getRoot();
//			while ( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) && index >-1000 ) {  //上周可能大盘修饰
//				index --;
//			}
			try{
				TimeSeriesDataItem lastcjlrecord = nodevol.getDataItem( index -1 );
				if(lastcjlrecord == null) //休市前还是空，说明要是新板块。板块没有停牌的
					return null;
				
				Double curcje = curcjlrecord.getValue().doubleValue();
				Double lastcje = lastcjlrecord.getValue().doubleValue();
				
				return curcje - lastcje;
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				return 100.0;
			}
		}
	 @Override
		public Integer getChenJiaoLiangMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) 
		{
		 	RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriodForAMO(requireddate,difference) ;
			if(curperiod == null)
				return null;
			
			TimeSeriesDataItem curcjlrecord = nodevol.getDataItem( curperiod);
			if( curcjlrecord == null) 
				return null;
			
			Double curcje = curcjlrecord.getValue().doubleValue();
			int itemcount = nodevol.getItemCount();
			int maxweek = 0;
			
			int index = nodevol.getIndex(curperiod );
			
			for(int i = index-1;i >=0; i--) {
				
				TimeSeriesDataItem lastcjlrecord = nodevol.getDataItem( i );
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
		@Override
		public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuai(TDXNodes superbk, LocalDate requireddate,	int difference) 
		{
			RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriodForAMO(requireddate,difference) ;
			if(curperiod == null)
				return null;
			
			TimeSeriesDataItem curcjlrecord = this.nodevol.getDataItem( curperiod );
			if( curcjlrecord == null) 
				return null;
			
			//判断上级板块(大盘或者板块)是否缩量,所以了没有比较的意义，直接返回-100；
			String nodept = getNodeperiodtype();
			NodeXPeriodData bkxdata = superbk.getNodeXPeroidData(nodept);
			Double bkcjediff = bkxdata.getChenJiaoLiangDifferenceWithLastPeriod(requireddate,difference);
									   
			if( bkcjediff == null || bkcjediff < 0   ) {//板块缩量，
				return -100.0;
			}
			
			TimeSeriesDataItem lastcjlrecord = null;
			int index = this.nodevol.getIndex( curperiod );
			try{
				lastcjlrecord = nodevol.getDataItem( index - 1);
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				logger.debug("index = 0，无法判断");
				return 100.0;
//				e.printStackTrace();
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
//		@Override
//		public Integer getCjlLianXuFangLiangPeriodNumber(LocalDate requireddate, int difference) 
//		{
//			int recordnum = this.nodevol.getItemCount();
//			int lianxu = 0;
//			Double curcje = this.getChengJiaoEr(requireddate,difference);
//			for(int wkindex = 1;wkindex > (0 - recordnum) ; wkindex--) { 
//				Double recordcjelast = this.getChengJiaoEr(requireddate, difference - wkindex);
//				if( curcje >= recordcjelast ) 
//					lianxu ++;
//				else
//					break;
//			}
//			
//			return lianxu;
//		}
//		@Override
//		public Integer getCjlDpMaxLianXuFangLiangPeriodNumber(LocalDate requireddate,int difference,int settindpgmaxwk)
//		{
//			int recordnum = this.nodeohlc.getItemCount();
//			int lianxu = 0;
//			for(int wkindex = 0;wkindex > (0 - recordnum) ; wkindex--) { 
//				Integer recordmaxbkwklast = this.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(requireddate,wkindex);
//				if( recordmaxbkwklast != null && recordmaxbkwklast >= settindpgmaxwk) 
//					lianxu ++;
//				else if( recordmaxbkwklast != null && recordmaxbkwklast < settindpgmaxwk)
//					return lianxu;
////				else if(recordmaxbkwklast == null )
////						;
//			}
//			
//			return -1;
//		}
		/*
		 * 通过apache math计算AMO的MA
		 */
		public Double[] getNodeAMOMA (LocalDate  requireddate,int difference)
		{
			int itemcount = this.nodeamo.getItemCount();
			double[] amodata = new double[itemcount];
			for(int i=0;i < this.nodeamo.getItemCount();i++) {
				TimeSeriesDataItem dataitem = this.nodeamo.getDataItem(i);
				double close = dataitem.getValue().doubleValue();
				amodata[i] = close;
			}
			
			requireddate = super.adjustDate(requireddate); //先确保日期是在交易日内
			RegularTimePeriod expectedperiod = this.getJFreeChartFormateTimePeriodForOHLC(requireddate,difference);
			Integer itemindex = this.nodeamo.getIndex(expectedperiod);
			
			Double ma5 = null;
			Integer ma5index = this.nodeamoma5.getIndex(expectedperiod);
			if(ma5index == -1 && itemindex>=5) {
				ma5 = StatUtils.mean(amodata, itemindex-4, 5);
				nodeamoma5.add(expectedperiod,ma5,false);
			} else
			{
				TimeSeriesDataItem ma5item = this.nodeamoma5.getDataItem(expectedperiod);
				if(ma5item != null)
					ma5 = ma5item.getValue().doubleValue();
			}
			
			Double ma10 = null;
			Integer ma10index = this.nodeamoma10.getIndex(expectedperiod);
			if(ma10index == -1 && itemindex>=10) {
				ma10 = StatUtils.mean(amodata, itemindex-9, 10);
				nodeamoma10.add(expectedperiod,ma10,false);
			} else
			{
				TimeSeriesDataItem ma10item = this.nodeamoma10.getDataItem(expectedperiod);
				if(ma10item != null)
					ma10 = ma10item.getValue().doubleValue();
			}
			
			Double amoma[] = {ma5,ma10};
			return amoma;
		}
		/*
		 * 
		 */
		public void calNodeAMOMA ()
		{
			int itemcount = this.nodeamo.getItemCount();
			double[] amodata = new double[itemcount];
			for(int i=0;i < this.nodeamo.getItemCount();i++) {
				TimeSeriesDataItem dataitem = this.nodeamo.getDataItem(i);
				double close = dataitem.getValue().doubleValue();
				amodata[i] = close;
			}
			
			Double ma5 = null;Double ma10 = null;
			for(int i= itemcount-1;i >=0;i--) {
				TimeSeriesDataItem dataitem = this.nodeamo.getDataItem(i);
				RegularTimePeriod period = dataitem.getPeriod();
				
				if(this.nodeamoma5.getIndex(period) <0 ) {
					if(i>=5) {
						ma5 = StatUtils.mean(amodata, i-4, 5);
						if(ma5 != null)
							this.nodeamoma5.add(period , ma5, false);
					}
				}
				
				if(this.nodeamoma10.getIndex(period) <0 ) {
					if(i>=10) {
						ma10 = StatUtils.mean(amodata, i-9, 10);
						if(ma10 != null)
							this.nodeamoma10.add(period , ma10, false);
					}
				}
			}
			
			return;
		}
	/**
	 * 
	 */
	public void calNodeOhlcMA ()
	{
		int itemcount = this.nodeohlc.getItemCount();
		double[] closedata = new double[itemcount];
		for(int i=0;i < this.nodeohlc.getItemCount();i++) {
			OHLCItem dataitem = (OHLCItem)this.nodeohlc.getDataItem(i);
			double close = dataitem.getCloseValue();
			closedata[i] = close;
		}
		
		Double ma5 = null;Double ma10 = null;Double ma20 = null;Double ma60 = null;Double ma250 = null;
		for(int i= itemcount-1;i >=0;i--) {
			OHLCItem dataitem = (OHLCItem)this.nodeohlc.getDataItem(i);
			RegularTimePeriod period = dataitem.getPeriod();
			
			if(i>=5) {
				if( this.nodeohlcma5.getIndex(period) <0  )
				ma5 = StatUtils.mean(closedata, i-4, 5);
				if(ma5 != null)
					nodeohlcma5.add(period,ma5,false);
			}
			if(i>=10) {
				if( this.nodeohlcma10.getIndex(period) <0  )
				ma10 = StatUtils.mean(closedata, i-9, 10);
				if(ma10 != null)
					nodeohlcma10.add(period,ma10,false);
			}
			if(i>=20) {
				if( this.nodeohlcma20.getIndex(period) <0  )
				ma20 = StatUtils.mean(closedata, i-19, 20);
				if(ma20 != null)
					nodeohlcma20.add(period,ma20,false);
			}
			if(i>=60) {
				if( this.nodeohlcma60.getIndex(period) <0  )
				ma60 = StatUtils.mean(closedata, i-59, 60);
				if(ma60 != null)
					nodeohlcma60.add(period,ma60,false);
			}
			if(i>=250) {
				if( this.nodeohlcma250.getIndex(period) <0  )
				ma250 = StatUtils.mean(closedata, i-249, 250);
				if(ma250 != null)
					nodeohlcma250.add(period,ma250,false);
			}
		}
		
		return;
	}
	
	/*
	 * 通过apache math计算MA
	 */
	 public Double[] getNodeOhlcMA (LocalDate  requireddate,int difference)
	 {
		requireddate = super.adjustDate(requireddate); //先确保日期是在交易周内
		RegularTimePeriod expectedperiod = this.getJFreeChartFormateTimePeriodForOHLC(requireddate,difference);
		if(expectedperiod == null)
			return null;
			
		int itemcount = this.nodeohlc.getItemCount();
		double[] closedata = new double[itemcount];
		for(int i=0;i < this.nodeohlc.getItemCount();i++) {
			OHLCItem dataitem = (OHLCItem)this.nodeohlc.getDataItem(i);
			double close = dataitem.getCloseValue();
			closedata[i] = close;
		}
		
		
		Integer itemindex = this.getIndexOfSpecificDateOHLCData(requireddate,difference);
		Double ma5 = null;
		Integer ma5index = this.nodeohlcma5.getIndex(expectedperiod);
		if(ma5index == -1 && itemindex>=5) {
			ma5 = StatUtils.mean(closedata, itemindex-4, 5);
			nodeohlcma5.add(expectedperiod,ma5,false);
		} else
		{
			TimeSeriesDataItem ma5item = this.nodeohlcma5.getDataItem(expectedperiod);
			if(ma5item != null)
			ma5 = ma5item.getValue().doubleValue();
		}
		 
		Double ma10 = null;
		Integer ma10index = this.nodeohlcma10.getIndex(expectedperiod);
		if(ma10index == -1 && itemindex>=10) {
			ma10 = StatUtils.mean(closedata, itemindex-9, 10);
			nodeohlcma10.add(expectedperiod,ma10,false);
		} else
		{
			TimeSeriesDataItem ma10item = this.nodeohlcma10.getDataItem(expectedperiod);
			if(ma10item != null)
				ma10 = ma10item.getValue().doubleValue();
		} 
		
		Double ma20 = null;
		Integer ma20index = this.nodeohlcma20.getIndex(expectedperiod);
		if(ma20index == -1 && itemindex>=20) {
			ma20 = StatUtils.mean(closedata, itemindex-19, 20);
			nodeohlcma20.add(expectedperiod,ma20,false);
		} else
		{
			TimeSeriesDataItem ma20item = this.nodeohlcma20.getDataItem(expectedperiod);
			if(ma20item != null)
				ma20 = ma20item.getValue().doubleValue();
		}
		
		Double ma60 = null;
		Integer ma60index = this.nodeohlcma60.getIndex(expectedperiod);
		if(ma60index == -1 && itemindex>=60) {
			ma60 = StatUtils.mean(closedata, itemindex-59, 60);
			nodeohlcma60.add(expectedperiod,ma60,false);
		} else
		{
			TimeSeriesDataItem ma60item = this.nodeohlcma60.getDataItem(expectedperiod);
			if(ma60item != null)
			ma60 = ma60item.getValue().doubleValue();
		} 
		
		Double ma250 = null;
		Integer ma250index = this.nodeohlcma250.getIndex(expectedperiod);
		if(ma250index == -1 && itemindex>=250) {
			ma250 = StatUtils.mean(closedata, itemindex-249, 250);
			nodeohlcma250.add(expectedperiod,ma250,false);
		} else
		{
			TimeSeriesDataItem ma250item = this.nodeohlcma250.getDataItem(expectedperiod);
			if(ma250item != null)
			ma250 = ma250item.getValue().doubleValue();
		} 
		 
		Double sma[] = {ma5,ma10,ma20,null,ma60,null,ma250};
		return sma;
	 }
	 /*
	  * 用TA4J计算均线
	  */
	 public Double[] getNodeOhlcSMA (LocalDate  requireddate,int difference)
	 {
		 requireddate = super.adjustDate(requireddate); //先确保日期是在交易日内
		 
		 org.ta4j.core.TimeSeries ohlcvaseries = this.getOHLCDataOfTa4jFormat(this.getOHLCRecordsStartDate(), this.getOHLCRecordsEndDate() );
		 
		 	Integer location = null; //获取该日期在数据集中的位置
			for(int i = 0;i<ohlcvaseries.getBarCount();i++) {
				Bar bar = ohlcvaseries.getBar(i);
				ZonedDateTime time = bar.getEndTime();
				LocalDate ldtime = time.toLocalDate();
				if(ldtime.equals(requireddate)) {
					location = i;
					break;
				}
			}
			
			if(location == null) {
				Double sma[] = null;
				return sma;
			}
			
			ClosePriceIndicator closePrice = new ClosePriceIndicator(ohlcvaseries);
				
			SMAIndicator Sma5 = new SMAIndicator(closePrice, 5);
			Double ma5 = Sma5.getValue(location).doubleValue();
			
			SMAIndicator Sma10 = new SMAIndicator(closePrice, 10);
			Double ma10 = Sma10.getValue(location).doubleValue();
			
			SMAIndicator Sma20 = new SMAIndicator(closePrice, 20);
			Double ma20 = Sma20.getValue(location).doubleValue();
			
			SMAIndicator Sma30 = new SMAIndicator(closePrice, 30);
			Double ma30 = Sma30.getValue(location).doubleValue();
			
			SMAIndicator Sma60 = new SMAIndicator(closePrice, 60);
			Double ma60 = Sma60.getValue(location).doubleValue();
			
			SMAIndicator Sma120 = new SMAIndicator(closePrice, 120);
			Double ma120 = Sma120.getValue(location).doubleValue();
			
			SMAIndicator Sma250 = new SMAIndicator(closePrice, 250);
			Double ma250 = Sma250.getValue(location).doubleValue();
			
			ohlcvaseries = null;
			closePrice = null;
			Sma5 = null;
			Sma10 = null;
			Sma20 = null;
			Sma30 = null;
			Sma60 = null;
			Sma120 = null;
			Sma250 = null;
			
			Double sma[] = {ma5,ma10,ma20,ma30,ma60,ma120,ma250};
			return sma;
	 }
	 /*
	  * 
	  */
	 public Multimap<LocalDate, LocalDate> isMacdTopDivergenceInSpecificMonthRange (LocalDate  requireddate,int difference, int monthrange)
	 {
		 requireddate = super.adjustDate(requireddate); //先确保日期是在交易日内
		 
		 org.ta4j.core.TimeSeries ohlcvaseries = this.getOHLCDataOfTa4jFormat(requireddate.minusMonths(monthrange + 3),requireddate);
		 
		 Multimap<LocalDate,LocalDate>  macddivergencemap = ArrayListMultimap.create(); 

		 ClosePriceIndicator closePrice = new ClosePriceIndicator(ohlcvaseries);
		 MACDIndicator macd = new MACDIndicator (closePrice);
		 org.ta4j.core.TimeSeries macdorgohlc = macd.getTimeSeries();
		 int mac = macdorgohlc.getBarCount();
		 int ohlccount = ohlcvaseries.getBarCount();
		  
		 
		 List<LocalDate> macdreversedatelist = new ArrayList <> (); 
		 List<Integer> macdreverseIndexlist = new ArrayList <> ();
		 LocalDate requrestart = requireddate.minusMonths(monthrange);
		 int indexcursor = ohlcvaseries.getEndIndex();
		 
		 ZonedDateTime endtime = ohlcvaseries.getBar(indexcursor).getEndTime();
		 for(int i = indexcursor-2; i>=0; i--) {
			 double macdsecond = macd.getValue(i+1).doubleValue();
			 Bar barsecond = ohlcvaseries.getBar(i +1);
			 LocalDate seconddate = barsecond.getEndTime().toLocalDate();
			 
			 if(macdsecond >=0) //小于0的MACD底背离才有意义
				 continue;
			 
			 if(seconddate.isBefore(requireddate.minusMonths(monthrange)))
				 break;
			 
			 double macdfirst = macd.getValue(i+2).doubleValue();
			 double macdthird = macd.getValue(i).doubleValue();
			 
			 
			 
			 if(macdthird < macdsecond && macdfirst <= macdsecond) {
				 macdreversedatelist.add(seconddate);
				 macdreverseIndexlist.add(Integer.valueOf(i+1));
			 }

			 if(seconddate.isBefore(requrestart))
				 break;
		 }
		 //统计背离
		 for(int i=0;i<macdreverseIndexlist.size();i++) {
			 Integer index = macdreverseIndexlist.get(i);
			 double curmacd = macd.getValue(index).doubleValue();
			 double curclose = ohlcvaseries.getBar(index).getClosePrice().doubleValue();
			 LocalDate curzdt = ohlcvaseries.getBar(index).getEndTime().toLocalDate();
			 
			 double toppesemacdbetweentwopoints = -100;
			 for(int j=i+1; j<macdreverseIndexlist.size();j++) {
				 Integer lstindex = macdreverseIndexlist.get(j);
				 double lstmacd = macd.getValue(lstindex).doubleValue();
				 double lstclose = ohlcvaseries.getBar(lstindex).getClosePrice().doubleValue();
				 LocalDate lstzdt = ohlcvaseries.getBar(lstindex).getEndTime().toLocalDate();
				 
				 if(lstmacd > toppesemacdbetweentwopoints)
					 toppesemacdbetweentwopoints = lstmacd; 
				 
				 if(curclose > lstclose && curmacd < lstmacd &&  lstmacd == toppesemacdbetweentwopoints ) { //两个背离的MACD点之间不应该有更大的值
					 long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lstzdt, curzdt);
					 if( daysBetween >=20)
						 macddivergencemap.put(curzdt, lstzdt);
				 }
				 
			 }

		 }
		 
		 ohlcvaseries = null;
		 closePrice = null;
		 macd = null;
		 macdreversedatelist = null; 
		 macdreverseIndexlist = null;
		 
		 return macddivergencemap;

	 }
	 /*
	  * 
	  */
	 public Multimap<LocalDate, LocalDate> isMacdButtomDivergenceInSpecificMonthRange (LocalDate  requireddate,int difference, int monthrange)
	 {
		 requireddate = this.adjustDate(requireddate); //先确保日期是在交易日内
		 
		 org.ta4j.core.TimeSeries ohlcvaseries = this.getOHLCDataOfTa4jFormat(requireddate.minusMonths(monthrange + 3),requireddate);
		 
		 Multimap<LocalDate,LocalDate>  macddivergencemap = ArrayListMultimap.create(); 

		 ClosePriceIndicator closePrice = new ClosePriceIndicator(ohlcvaseries);
		 MACDIndicator macd = new MACDIndicator (closePrice);
		 org.ta4j.core.TimeSeries macdorgohlc = macd.getTimeSeries();
		 int mac = macdorgohlc.getBarCount();
		 int ohlccount = ohlcvaseries.getBarCount();
		  
		 
		 List<LocalDate> macdreversedatelist = new ArrayList <> (); 
		 List<Integer> macdreverseIndexlist = new ArrayList <> ();
		 LocalDate requrestart = requireddate.minusMonths(monthrange);
		 int indexcursor = ohlcvaseries.getEndIndex();
		 
//		 ZonedDateTime endtime = ohlcvaseries.getBar(indexcursor).getEndTime();
		 for(int i = indexcursor-2; i>=0; i--) {
			 double macdsecond = macd.getValue(i+1).doubleValue();
			 Bar barsecond = ohlcvaseries.getBar(i +1);
			 LocalDate seconddate = barsecond.getEndTime().toLocalDate();
			 
			 if(macdsecond >=0) //小于0的MACD底背离才有意义
				 continue;
			 
			 if(seconddate.isBefore(requireddate.minusMonths(monthrange)))
				 break;
			 
			 double macdfirst = macd.getValue(i+2).doubleValue();
			 double macdthird = macd.getValue(i).doubleValue();
			 
			 
			 
			 if(macdthird >= macdsecond && macdfirst > macdsecond) {
				 macdreversedatelist.add(seconddate);
				 macdreverseIndexlist.add(Integer.valueOf(i+1));
			 }

			 if(seconddate.isBefore(requrestart))
				 break;
		 }
		 //统计背离
		 for(int i=0;i<macdreverseIndexlist.size();i++) {
			 Integer index = macdreverseIndexlist.get(i);
			 double curmacd = macd.getValue(index).doubleValue();
			 double curclose = ohlcvaseries.getBar(index).getClosePrice().doubleValue();
			 LocalDate curzdt = ohlcvaseries.getBar(index).getEndTime().toLocalDate();
			 
			 double lowesemacdbetweentwopoints = 100;
			 for(int j=i+1; j<macdreverseIndexlist.size();j++) {
				 Integer lstindex = macdreverseIndexlist.get(j);
				 double lstmacd = macd.getValue(lstindex).doubleValue();
				 double lstclose = ohlcvaseries.getBar(lstindex).getClosePrice().doubleValue();
				 LocalDate lstzdt = ohlcvaseries.getBar(lstindex).getEndTime().toLocalDate();
				 
				 if(lstmacd < lowesemacdbetweentwopoints)
					 lowesemacdbetweentwopoints = lstmacd; 
				 
				 if(curclose < lstclose && curmacd > lstmacd &&  lstmacd == lowesemacdbetweentwopoints ) { //两个背离的MACD点之间不应该有更小的值
					 long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lstzdt, curzdt);
					 if( daysBetween >=20)
						 macddivergencemap.put(curzdt, lstzdt);
				 }
				 
			 }

		 }
		 
		 ohlcvaseries = null;
		 closePrice = null;
		 macd = null;
		 macdreversedatelist = null; 
		 macdreverseIndexlist = null;
		 
		 return macddivergencemap;


	 }
	 /*
	  * (non-Javadoc)
	  * @see com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic#checkCloseComparingToMAFormula(java.lang.String, java.time.LocalDate, int)
	  */
	 public Boolean checkCloseComparingToMAFormula (String maformula, LocalDate requireddate, int difference)
	 {
		 LocalDate tmpdate = requireddate.with(DayOfWeek.FRIDAY);
		    
		 OHLCItem ohlcdata;
		 do { //日线数据比较复杂 ，用户选择的日子可能不是交易日，可能是停牌日，就直接算周五，如果周五没有数据，往前1天，直到有数据为止或本周结束
		    	ohlcdata = this.getSpecificDateOHLCData(tmpdate, 0);
		    	if(ohlcdata != null)  //没有停牌
		    		break;
		    	
		    	tmpdate = tmpdate.minus(1,ChronoUnit.DAYS);
		 } while (CommonUtility.isInSameWeek(requireddate, tmpdate) );
		    
		 if (ohlcdata != null) {
		    	Double close = (Double)ohlcdata.getCloseValue();
		    	Double[] maresult = this.getNodeOhlcMA(tmpdate, difference);
			    Boolean result = checkCloseComparingToMAsettings (close,maresult,maformula);
			    if( result != null )
			    	return result;
		 }
		 
		 return null;
			
	 }
	 private Boolean checkCloseComparingToMAsettings (Double close,Double[] maresult,String maformula)
	 {
			try{
				if(maformula.contains(">\'250\'") || maformula.contains(">=\'250\'") || maformula.contains("<\'250\'") || maformula.contains("<=\'250\'") ) {
					if (maresult[6] != null)
						maformula = maformula.replace("\'250\'",  maresult[6].toString() ) ;
					else
						maformula = maformula.replace("\'250\'",  String.valueOf( 10000000000.0 ) ) ;
				}
			} catch (java.lang.NullPointerException e) {
				e.printStackTrace();
			}
			
		    if(maformula.contains(">\'120\'") || maformula.contains(">=\'120\'") || maformula.contains("<\'120\'") || maformula.contains("<=\'120\'")) {
		    	if (maresult[5] != null)
		    		maformula = maformula.replace("\'120\'",  maresult[5].toString() ) ;
		    	else
					maformula = maformula.replace("\'120\'",  String.valueOf( 10000000000.0 ) ) ;
		    }
		    
		    if(maformula.contains(">\'60\'") || maformula.contains(">=\'60\'") || maformula.contains("<\'60\'") || maformula.contains("<=\'60\'") ) {
		    	if(maresult[4] != null)
		    		maformula = maformula.replace("\'60\'",  maresult[4].toString() ) ;
		    	else
					maformula = maformula.replace("\'60\'",  String.valueOf( 10000000000.0 ) ) ;
		    }
		    	
		    
		    if(maformula.contains(">\'30\'") || maformula.contains(">=\'30\'") || maformula.contains("<\'30\'") || maformula.contains("<=\'30\'") ) {
		    	if(maresult[3] != null)
		    		maformula = maformula.replace("\'30\'",  maresult[3].toString() ) ;
		    	else
					maformula = maformula.replace("\'30\'",  String.valueOf( 10000000000.0 ) ) ;
		    }
		    
		    if(maformula.contains(">\'20\'") || maformula.contains(">=\'20\'") || maformula.contains("<\'20\'") || maformula.contains("<=\'20\'") ) {
		    	if(maresult[2] != null)
		    		maformula = maformula.replace("\'20\'",  maresult[2].toString() ) ;
		    	else
					maformula = maformula.replace("\'20\'",  String.valueOf( 10000000000.0 ) ) ;
		    }
		    
		    if(maformula.contains(">\'10\'") || maformula.contains(">=\'10\'") || maformula.contains("<\'10\'") || maformula.contains("<=\'10\'")) {
		    	if(maresult[1] != null)
		    		maformula = maformula.replace("\'10\'",  maresult[1].toString() ) ;
		    	else
					maformula = maformula.replace("\'10\'",  String.valueOf( 10000000000.0 ) ) ;
		    }
		    
		    if(maformula.contains(">\'5\'") || maformula.contains(">=\'5\'") || maformula.contains("<\'5\'") || maformula.contains("<=\'5\'") ) {
		    	if(maresult[0] != null)
		    		maformula = maformula.replace("\'5\'",  maresult[0].toString() ) ;
		    	else
					maformula = maformula.replace("\'5\'",  String.valueOf( 10000000000.0 ) ) ;
		    }
		    
		    try{
		    	BigDecimal result1 = new Expression(maformula).with("x",String.valueOf(close) ).eval(); //https://github.com/uklimaschewski/EvalEx
		    	String sesultstr = result1.toString();
			    if(sesultstr.equals("0"))
			    	return false;
			    else 
			    	return true;
		    } catch (com.udojava.evalex.Expression.ExpressionException e) {
		    	e.printStackTrace();
		    	return false;
		    }
		    
		    
		    
//			ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
//		      Map<String, Object> vars = new HashMap<String, Object>();
//		      vars.put("x", close);
////		      vars.put("y", 2);
////		      vars.put("z", 1);
//	      try {
//	    	  Boolean result = (Boolean)engine.eval(maformula, new SimpleBindings(vars));
//			  return result;
//			} catch (ScriptException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				return null;
//			}
	      

		}
	 /*
	  * 
	  */
	 public String[] getNodeXDataCsvData (TDXNodes superbk, LocalDate requireddate, int difference)
	 {
		String[] supcsv = super.getNodeXDataCsvData(superbk, requireddate, 0); 
		
		Double curcje = this.getChengJiaoEr(requireddate, 0);
		Double avecje = this.getAverageDailyChengJiaoErOfWeek (requireddate, 0);
		Integer cjemaxwk =  this.getChenJiaoErMaxWeekOfSuperBanKuai(requireddate,0);//显示成交额是多少周最大,成交额多少周最小没有意义，因为如果不是完整周成交量就是会很小
		Double cjechangerate = this.getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage(superbk,requireddate,0);//成交额大盘变化贡献率
		
		Double curcjl = this.getChengJiaoLiang(requireddate, 0);
		Integer cjlmaxwk = this.getChenJiaoLiangMaxWeekOfSuperBanKuai(requireddate,0);//显示cjl是多少周最大
//		Double cjlchangerate = this.getChenJiaoLiangChangeGrowthRateOfSuperBanKuai(superbk,requireddate,0);//cjl大盘变化贡献率
		
		Double zhangfu = this.getSpecificOHLCZhangDieFu (requireddate,0);
		
		String strcurcje = null;
		String stravecje = null;
		String strcjemaxwk =  null;
		String strcjechangerate = null;
		
		String strcurcjl = null;
		String strcjlmaxwk = null;
//		String strcjlchangerate = null;
		String strzhangfu = null;
		
		try {
			strcurcje = curcje.toString();
		} catch (java.lang.NullPointerException e) {
			strcurcje = String.valueOf("0");
		}
		try {
			stravecje = avecje.toString();
		} catch (java.lang.NullPointerException e) {
			stravecje = String.valueOf("0");
		}
		try {
			strcjemaxwk = cjemaxwk.toString();
		} catch (java.lang.NullPointerException e) {
			strcjemaxwk = String.valueOf("0");
		}
		try {
			strcjechangerate = cjechangerate.toString();
					
		} catch (java.lang.NullPointerException e) {
			strcjechangerate = String.valueOf("0");
		}
		try {
			strcurcjl = curcjl.toString();
		} catch (java.lang.NullPointerException e) {
			strcurcjl = String.valueOf("0");
		}
		try {
			strcjlmaxwk =  cjlmaxwk.toString();
		} catch (java.lang.NullPointerException e) {
			strcjlmaxwk = String.valueOf("0");
		}
//		try {
//			strcjlchangerate =  cjlchangerate.toString();
//		} catch (java.lang.NullPointerException e) {
//			strcjlchangerate = String.valueOf("0");
//		}
		try {
			strzhangfu =  zhangfu.toString();
		} catch (java.lang.NullPointerException e) {
			strzhangfu = String.valueOf("0");
		}
		
		
		
		String[] curcsvline = {  strcurcje ,
		 stravecje ,
		 strcjemaxwk ,
		 strcjechangerate ,
		
		 strcurcjl ,
		 strcjlmaxwk ,
//		 strcjlchangerate,
		 
		 strzhangfu
		}; 

		String [] joined = ObjectArrays.concat(supcsv, curcsvline, String.class);
		
		return joined;
	 }
	 
	 /*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic#getNodeXDataInHtml(java.time.LocalDate, int)
		 */
		public String getNodeXDataInHtml(TDXNodes superbk,LocalDate requireddate, int difference) 
		{
			DecimalFormat decimalformate = new DecimalFormat("#0.000"); //",###";
	    	NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
	    	percentFormat.setMinimumFractionDigits(4);
	    	DecimalFormat decimalformate2 = new DecimalFormat("%#0.00000");
				
			String htmlstring = "";
			org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
			Elements body = doc.getElementsByTag("body");
			for(Element elbody : body) {
				org.jsoup.nodes.Element dl = elbody.appendElement("dl");
				 
				 org.jsoup.nodes.Element lidate = dl.appendElement("li");
				 org.jsoup.nodes.Element fontdate = lidate.appendElement("font");
				 fontdate.appendText(requireddate.toString());
				 fontdate.attr("color", "#17202A");
				 
				 Double zhangfu = this.getSpecificOHLCZhangDieFu (requireddate,0);
				 if(zhangfu != null) {
					 org.jsoup.nodes.Element li12 = dl.appendElement("li");
					 org.jsoup.nodes.Element font12 = li12.appendElement("font");
					 font12.appendText("涨跌幅" + percentFormat.format (zhangfu)  );
					 font12.attr("color", "#1B2631");
				 }
				 //
				 Double curcje = this.getChengJiaoEr(requireddate, 0);
				 String cjedanwei = null;
				 try{
						cjedanwei = FormatDoubleToShort.getNumberChineseDanWei(curcje);
						curcje = FormatDoubleToShort.formateDoubleToShort (curcje);
				 } catch (java.lang.NullPointerException e) {
//						e.printStackTrace();
						logger.debug(super.getNodeCode() + "在" + requireddate.toString() + "没有数据，可能停牌。");
						return "";
				 }
				 org.jsoup.nodes.Element licje = dl.appendElement("li");
				 org.jsoup.nodes.Element fontcje = licje.appendElement("font");
				 fontcje.appendText("成交额" + decimalformate.format(curcje) + cjedanwei);
				 fontcje.attr("color", "#AF7AC5");
				 
				 Double avecje = this.getAverageDailyChengJiaoErOfWeek (requireddate, 0);
				 cjedanwei = null;
				 try{
						cjedanwei = FormatDoubleToShort.getNumberChineseDanWei(avecje);
						avecje = FormatDoubleToShort.formateDoubleToShort (avecje);
				 } catch (java.lang.NullPointerException e) {
//						e.printStackTrace();
						logger.debug(super.getNodeCode() + "在" + requireddate.toString() + "没有数据，可能停牌。");
						return "";
				 }
				 org.jsoup.nodes.Element liavecje = dl.appendElement("li");
				 org.jsoup.nodes.Element fontavecje = liavecje.appendElement("font");
				 fontavecje.appendText("周日平均成交额" + decimalformate.format(avecje) + cjedanwei);
				 fontavecje.attr("color", "#AF7AC5");
				 //
				 Integer cjemaxwk = null;
			     try{
			    		cjemaxwk = this.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(requireddate,0);//显示成交额是多少周最大,成交额多少周最小没有意义，因为如果不是完整周成交量就是会很小
			     } catch (java.lang.NullPointerException e) {
			    		
			     }
				 if(cjemaxwk>0) {
					 org.jsoup.nodes.Element licjemaxwk = dl.appendElement("li");
					 org.jsoup.nodes.Element fontcjemaxwk = licjemaxwk.appendElement("font");
					 fontcjemaxwk.appendText("周日平均成交额MaxWk=" + cjemaxwk);
					 fontcjemaxwk.attr("color", "#AF7AC5 ");
				 } 
				 //
				 Integer avcjelxwk = null;
				 try {
					 avcjelxwk = this.getAverageDailyCjeLianXuFangLiangPeriodNumber(requireddate,0);
				 } catch (java.lang.NullPointerException e) {
			    		
			     }
				 if(avcjelxwk != null && avcjelxwk >0) {
					 org.jsoup.nodes.Element liavcjelxwk = dl.appendElement("li");
					 org.jsoup.nodes.Element fontavcjelxwk = liavcjelxwk.appendElement("font");
					 fontavcjelxwk.appendText("周日平均成交额连续涨=" + avcjelxwk);
					 fontavcjelxwk.attr("color", "#AF7AC5 ");
				 }
				 //
				 try{
					 Double cjechangerate = this.getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage(superbk,requireddate,0);//成交额大盘变化贡献率
					 if( cjechangerate != -100.0) {
						 htmlstring = "CJE板块贡献率(周日均)" + percentFormat.format (cjechangerate) ;
						 org.jsoup.nodes.Element licjechangerate = dl.appendElement("li");
						 org.jsoup.nodes.Element fontcjechangerate = licjechangerate.appendElement("font");
						 fontcjechangerate.appendText(htmlstring);
						 fontcjechangerate.attr("color", "#AF7AC5 ");
					 }
				 } catch (java.lang.IllegalArgumentException e) {
//					 li4.appendText("成交额大盘变化贡献率NULL" );
				 } catch (java.lang.NullPointerException e) {
					 logger.debug(e.getMessage());
//			    	e.printStackTrace();	
			     }

				try {
					Double curcjezhanbidata = super.getChenJiaoErZhanBi(requireddate, 0);  //占比
					
					org.jsoup.nodes.Element licjezb = dl.appendElement("li");
					org.jsoup.nodes.Element fontcjezb = licjezb.appendElement("font");
					fontcjezb.appendText( "成交额占比" + decimalformate2.format(curcjezhanbidata)  );
					fontcjezb.attr("color", "#17202A");
				} catch (java.lang.IllegalArgumentException e ) {
					logger.debug(e.getMessage());
//					htmltext = "占比占比NULL" ;
				}
				
				Integer cjemaxweek = this.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(requireddate,0);//nodefx.getGgbkzhanbimaxweek();
				Integer cjeminweek = this.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(requireddate,0);
				if(cjemaxweek > 0) {
					try {
						 org.jsoup.nodes.Element licjemaxwk = dl.appendElement("li");
						 org.jsoup.nodes.Element fontcjemaxwk = licjemaxwk.appendElement("font");
						 fontcjemaxwk.appendText( "成交额占比MaxWk=" + cjemaxweek.toString()  );
						 fontcjemaxwk.attr("color", "#17202A");
					} catch (java.lang.IllegalArgumentException e ) {
//						htmltext = "占比MaxWk=NULL"  ;
					}
				} else {
					try {
						 org.jsoup.nodes.Element licjeminwk = dl.appendElement("li");
						 org.jsoup.nodes.Element fontcjeminwk = licjeminwk.appendElement("font");
						 fontcjeminwk.appendText( "成交额占比MinWk=" + cjeminweek.toString()  );
						 fontcjeminwk.attr("color", "#17202A");  
					} catch (java.lang.IllegalArgumentException e ) {
//						htmltext = "占比MaxWk=NULL"  ;
					}
				}
				
				Double cjezbgrowthrate = super.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(requireddate, 0);
				org.jsoup.nodes.Element licjezbgr = dl.appendElement("li");
				org.jsoup.nodes.Element fontcjezbgr = licjezbgr.appendElement("font");
				fontcjezbgr.appendText( "成交额占比增长率=" + decimalformate2.format(cjezbgrowthrate)  );
				fontcjezbgr.attr("color", "#17202A");
				
				 //
				 Double curcjl = this.getChengJiaoLiang(requireddate, 0);
		    	 String cjldanwei = FormatDoubleToShort.getNumberChineseDanWei(curcjl);
		    	 curcjl = FormatDoubleToShort.formateDoubleToShort (curcjl);
				 org.jsoup.nodes.Element licjl = dl.appendElement("li");
				 org.jsoup.nodes.Element fontcjl = licjl.appendElement("font");
				 fontcjl.appendText("成交量" + decimalformate.format(curcjl) + cjldanwei);
				 fontcjl.attr("color", "#641E16");
				 //
				 Integer cjlmaxwk = null;
			     try{
			    		cjlmaxwk = this.getChenJiaoLiangMaxWeekOfSuperBanKuai(requireddate,0);//显示cjl是多少周最大
			     } catch (java.lang.NullPointerException e) {
			     }
				 if(cjlmaxwk>0) {
					 org.jsoup.nodes.Element licjlmaxwk = dl.appendElement("li");
					 org.jsoup.nodes.Element fontcjlmaxwk = licjlmaxwk.appendElement("font");
					 fontcjlmaxwk.appendText("成交量MaxWk=" + cjlmaxwk);
					 fontcjlmaxwk.attr("color", "#641E16");
				 } 
				 //
//				 try{
//					 Double cjlchangerate = this.getChenJiaoLiangChangeGrowthRateOfSuperBanKuai(superbk,requireddate,0);//cjl大盘变化贡献率
//					 if( cjlchangerate != -100.0) {
//						 org.jsoup.nodes.Element licjlchangerate = dl.appendElement("li");
//						 org.jsoup.nodes.Element fontcjlchangerate = licjlchangerate.appendElement("font");
//						 fontcjlchangerate.appendText( "成交量板块变化贡献率" + percentFormat.format (cjlchangerate)  );
//						 fontcjlchangerate.attr("color", "#641E16");
//					 }
//				 } catch (java.lang.IllegalArgumentException e) {
////					 li4.appendText("成交额大盘变化贡献率NULL" );
//				 }

				try {
					Double cjlzhanbidata = this.getChenJiaoLiangZhanBi(requireddate, 0);
					
					org.jsoup.nodes.Element licjlzb = dl.appendElement("li");
					org.jsoup.nodes.Element fontzjlzb = licjlzb.appendElement("font");
					fontzjlzb.appendText( "成交量占比" + decimalformate2.format(cjlzhanbidata)  );
					fontzjlzb.attr("color", "#512E5F");
				} catch (java.lang.IllegalArgumentException e ) {
//					htmltext = "占比占比NULL" ;
				}
				
				Integer cjlzbmaxwk = this.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(requireddate, 0);
				Integer cjlzbminwk = this.getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(requireddate, 0);
				if(cjlzbmaxwk > 0) {
					try {
						 org.jsoup.nodes.Element licjlmaxwk = dl.appendElement("li");
						 org.jsoup.nodes.Element fontcjlmaxwk = licjlmaxwk.appendElement("font");
						 fontcjlmaxwk.appendText( "成交量占比MaxWk=" + cjlzbmaxwk.toString()  );
						 fontcjlmaxwk.attr("color", "#512E5F");
					} catch (java.lang.IllegalArgumentException e ) {
//						htmltext = "占比MaxWk=NULL"  ;
					}
				} else {
					try {
						 org.jsoup.nodes.Element licjlminwk = dl.appendElement("li");
						 org.jsoup.nodes.Element fontcjlminwk = licjlminwk.appendElement("font");
						 fontcjlminwk.appendText( "成交量占比MinWk=" + cjlzbminwk.toString()  );
						 fontcjlminwk.attr("color", "#512E5F");
					} catch (java.lang.IllegalArgumentException e ) {
//						htmltext = "占比MaxWk=NULL"  ;
					}
				}
				
				Double cjlzbgrowthrate = this.getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(requireddate, 0);
				org.jsoup.nodes.Element licjlzbgr = dl.appendElement("li");
				org.jsoup.nodes.Element fontcjlzbgr = licjlzbgr.appendElement("font");
				fontcjlzbgr.appendText( "成交量占比增长率=" + decimalformate2.format(cjlzbgrowthrate)  );
				fontcjlzbgr.attr("color", "#512E5F");

	 
					 Integer opneupquekou = this.getQueKouTongJiOpenUp(requireddate, 0);
					 if( opneupquekou != null) {
						 org.jsoup.nodes.Element li6 = dl.appendElement("li");
						 org.jsoup.nodes.Element font6 = li6.appendElement("font");
						 font6.appendText("缺口OpenUp =" + opneupquekou);
						 font6.attr("color", "#F39C12");
					 }
					 Integer opendownquekou = this.getQueKouTongJiOpenDown(requireddate, 0);
					 if( opendownquekou != null) {
						 org.jsoup.nodes.Element li7 = dl.appendElement("li");
						 org.jsoup.nodes.Element font7 = li7.appendElement("font");
						 font7.appendText("缺口OpenDown =" + opendownquekou);
						 font7.attr("color", "#F39C12");
					 }
					 Integer huibuupquekou = this.getQueKouTongJiHuiBuUp(requireddate, 0);
					 if( huibuupquekou != null) {
						 org.jsoup.nodes.Element li8 = dl.appendElement("li");
						 org.jsoup.nodes.Element font8 = li8.appendElement("font");
						 font8.appendText("缺口HuiBuUp =" + huibuupquekou );
						 font8.attr("color", "#F39C12");
					 }
					 Integer huibudowquekou = this.getQueKouTongJiHuiBuDown(requireddate, 0);
					 if( huibudowquekou != null) {
						 org.jsoup.nodes.Element li9 = dl.appendElement("li");
						 org.jsoup.nodes.Element font9= li9.appendElement("font");
						 font9.appendText("缺口HuiBuDown =" + huibudowquekou );
						 font9.attr("color", "#F39C12");
					 }
					 
					 Integer zhangtingnum = this.getZhangTingTongJi(requireddate, 0);
					 if(zhangtingnum != null) {
						 org.jsoup.nodes.Element li10 = dl.appendElement("li");
						 org.jsoup.nodes.Element font10 = li10.appendElement("font");
						 font10.appendText("涨停 =" + zhangtingnum );
						 font10.attr("color", "#1B2631");
					 }
					 
					 Integer dietingnum = this.getDieTingTongJi(requireddate, 0);
					 if(dietingnum != null) {
						 org.jsoup.nodes.Element li11 = dl.appendElement("li");
						 org.jsoup.nodes.Element font11 = li11.appendElement("font");
						 font11.appendText("跌停 =" + dietingnum );
						 font11.attr("color", "#1B2631");
					 }
				
			}
			
			return doc.toString();
		}
		
		protected Boolean isNodeDataFuPaiAfterTingPai (TDXNodes superbk, LocalDate requireddate,int difference)
		{
			RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
			if(curperiod == null)
				return null;
			
			int index = this.nodeamo.getIndex( curperiod );
			
			TimeSeriesDataItem lastcjlrecord = null;
			try{
				lastcjlrecord = nodeamo.getDataItem( index - 1);
			} catch (java.lang.ArrayIndexOutOfBoundsException e ) {
				logger.debug("index = 0，可能是新股第一周");
				return true;
			} catch (java.lang.IndexOutOfBoundsException ex ) {
				logger.debug("index = 0，可能是新股第一周");
				return true;
			}
			Date lastdate = lastcjlrecord.getPeriod().getEnd();
			LocalDate lastld = lastdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			String nodept = getNodeperiodtype();
			NodeXPeriodData bkxdata = superbk.getNodeXPeroidData(nodept);
			int superbkindex = bkxdata.getIndexOfSpecificDateOHLCData(requireddate, 0);
			int bklstindex = 0;
			for(int i= -1;;i--) {
				Integer bklastxdata = bkxdata.getIndexOfSpecificDateOHLCData(requireddate, i);
				if(bklastxdata != null) {
					bklstindex = bklastxdata;
					break;
				}
			}
			LocalDate bklastlocaldate = bkxdata.getLocalDateOfSpecificIndexOfOHLCData(bklstindex);
			
			WeekFields weekFields = WeekFields.of(Locale.getDefault());
			int year2 = lastld.getYear();
			int weeknumber2 = lastld.get(weekFields.weekOfWeekBasedYear());
			
			int year1 = bklastlocaldate.getYear();
			int weeknumber1 = bklastlocaldate.get(weekFields.weekOfWeekBasedYear());
			
			if(year1 != year2)
				return true;
			else if(year1 == year2 && weeknumber1 != weeknumber2  ) //板块一般不会停牌，以板块为基准
				return true;
			else 
				return false;
		}
		
		
 } //END OF 
