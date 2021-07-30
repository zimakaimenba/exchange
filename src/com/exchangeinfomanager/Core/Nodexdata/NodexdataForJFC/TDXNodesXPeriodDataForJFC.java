package com.exchangeinfomanager.Core.Nodexdata.NodexdataForJFC;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.DaPan;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.TDXNodesXPeriodExternalData;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItemForJFC;
import com.exchangeinfomanager.Core.Trees.CreateExchangeTree;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.FormatDoubleToShort;
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
		this.nodeamoma20 = new TimeSeries(nodeperiodtype);
		this.nodeamoma30 = new TimeSeries(nodeperiodtype);
		this.nodeamoma60 = new TimeSeries(nodeperiodtype);
		this.nodeamoma120 = new TimeSeries(nodeperiodtype);
		this.nodeamoma250 = new TimeSeries(nodeperiodtype);
		
		this.periodhighestzhangdiefu = new TimeSeries(nodeperiodtype);
		this.periodlowestzhangdiefu = new TimeSeries(nodeperiodtype);
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
	
	private  TimeSeries periodhighestzhangdiefu; //�?高涨�?
	private  TimeSeries periodlowestzhangdiefu; //�?高涨�?
	/*
	 * 
	 */
	public void removeNodeDataFromSpecificDate (LocalDate requireddate)
	 {
		super.removeNodeDataFromSpecificDate(requireddate);

		RegularTimePeriod period = super.getJFreeChartFormateTimePeriod(requireddate);
		if(period == null) return;

		try {
			Integer curindex = this.nodeamo.getIndex(period);
				if(curindex >= 0 ) {
					int itemcount = this.nodeamo.getItemCount();
					nodeamo.delete(curindex, itemcount-1);
					nodevol.delete(curindex, itemcount-1);
				}
		} catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 
		 try {
			 	Integer curindex = this.periodhighestzhangdiefu.getIndex(period);
				if(curindex >= 0 ) {
					int itemcount = this.periodhighestzhangdiefu.getItemCount();
					periodhighestzhangdiefu.delete(curindex, itemcount-1);
				}
		} catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}

		 try {	int  curindex =  this.periodlowestzhangdiefu.getIndex(period);
				if(curindex >= 0 ) {
					int itemcount = this.periodlowestzhangdiefu.getItemCount();
					periodlowestzhangdiefu.delete(curindex, itemcount-1);
				}
		} catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 
		 //OHLC
		 try {	int curindex =  this.nodeohlcma5.getIndex(period);
				if(curindex >= 0 ) {
					int itemcount = this.nodeohlcma5.getItemCount();
					nodeohlcma5.delete(curindex, itemcount-1);
				}
		} catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 try {	int curindex =  this.nodeohlcma10.getIndex(period);
				if(curindex >= 0 ) {
					int itemcount = this.nodeohlcma10.getItemCount();
					nodeohlcma10.delete(curindex, itemcount-1);
				}
		} catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 try {	int curindex =  this.nodeohlcma20.getIndex(period);
				if(curindex >= 0 ) {
					int itemcount = this.nodeohlcma20.getItemCount();
					nodeohlcma20.delete(curindex, itemcount-1);
				}
		} catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 try {	int curindex =  this.nodeohlcma30.getIndex(period);
				if(curindex >= 0 ) {
					int itemcount = this.nodeohlcma30.getItemCount();
					nodeohlcma30.delete(curindex, itemcount-1);
				}
		} catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 try {	int curindex =  this.nodeohlcma60.getIndex(period);
			if(curindex >= 0 ) {
				int itemcount = this.nodeohlcma60.getItemCount();
				nodeohlcma60.delete(curindex, itemcount-1);
			}
		 } catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 try {	int curindex =  this.nodeohlcma120.getIndex(period);
				if(curindex >= 0 ) {
					int itemcount = this.nodeohlcma120.getItemCount();
					nodeohlcma120.delete(curindex, itemcount-1);
				}
		} catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 try {	int curindex =  this.nodeohlcma250.getIndex(period);
			if(curindex >= 0 ) {
				int itemcount = this.nodeohlcma250.getItemCount();
				nodeohlcma250.delete(curindex, itemcount-1);
			}
		 } catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 // AMO
		 try {	int curindex =  this.nodeamoma5.getIndex(period);
				if(curindex >= 0 ) {
					int itemcount = this.nodeamoma5.getItemCount();
					nodeamoma5.delete(curindex, itemcount-1);
				}
		} catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 try {	int curindex =  this.nodeamoma10.getIndex(period);
				if(curindex >= 0 ) {
					int itemcount = this.nodeamoma10.getItemCount();
					nodeamoma10.delete(curindex, itemcount-1);
				}
		} catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 try {	Integer curindex =  this.nodeamoma20.getIndex(period);
				if(curindex != null && curindex >= 0 ) {
					int itemcount = this.nodeamoma20.getItemCount();
					nodeamoma20.delete(curindex, itemcount-1);
				}
		} catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 try {	Integer curindex =  this.nodeamoma30.getIndex(period);
			if(curindex != null && curindex >= 0 ) {
				int itemcount = this.nodeamoma30.getItemCount();
				nodeamoma30.delete(curindex, itemcount-1);
			}
		 } catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 try {	Integer curindex =  this.nodeamoma60.getIndex(period);
			if(curindex != null && curindex >= 0 ) {
				int itemcount = this.nodeamoma60.getItemCount();
				nodeamoma60.delete(curindex, itemcount-1);
			}
		 } catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 try {	Integer curindex =  this.nodeamoma120.getIndex(period);
			if(curindex != null && curindex >= 0 ) {
				int itemcount = this.nodeamoma20.getItemCount();
				nodeamoma120.delete(curindex, itemcount-1);
			}
		 } catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 try {	Integer curindex =  this.nodeamoma250.getIndex(period);
			if( curindex != null && curindex >= 0 ) {
				int itemcount = this.nodeamoma250.getItemCount();
				nodeamoma250.delete(curindex, itemcount-1);
			}
		 } catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
		 
		 try {	int curindex = this.nodeohlc.indexOf(period);
				if(curindex >= 0 ) {
					int itemcount = nodeohlc.getItemCount();
					int recordsshouldberemovecount = itemcount  - curindex ;
					ComparableObjectItem[] ohlcdataarray = new ComparableObjectItem[recordsshouldberemovecount]; 
					for(int i= 0;i<recordsshouldberemovecount;i++) {
						ohlcdataarray[i] = nodeohlc.getDataItem(curindex + i);
					}
					for(int i= 0;i<recordsshouldberemovecount;i++) {
						RegularTimePeriod rmperiod = ((OHLCItem)ohlcdataarray[i]).getPeriod();
						nodeohlc.remove(rmperiod);
					}
				}
		} catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
	 }
	/*
	 * 
	 */
	public void addNewXPeriodData (NodeGivenPeriodDataItem kdata)
	{
		try {
			nodeohlc.setNotify(false);
			nodeohlc.add( (NodeGivenPeriodDataItemForJFC)kdata);
		} catch (org.jfree.data.general.SeriesException e) {
			logger.info(super.getNodeCode() + super.getNodeperiodtype() 
				+ kdata.getJFreeChartPeriod(super.getNodeperiodtype()).toString() 
				+ kdata.getJFreeChartPeriod(super.getNodeperiodtype()).getStart().toString() 
				+ "nodeohlc 数据已经存在，重复添加！"  );
		} catch (java.lang.IllegalArgumentException e) {
//			e.printStackTrace();
		}
		try {
			nodeamo.setNotify(false);
			nodevol.setNotify(false);
			nodeamo.add(kdata.getJFreeChartPeriod( super.getNodeperiodtype()  ),kdata.getMyOwnChengJiaoEr(),false);
			nodevol.add(kdata.getJFreeChartPeriod( super.getNodeperiodtype() ), kdata.getMyOwnChengJiaoLiang(),false);
		} catch (org.jfree.data.general.SeriesException e) {
//			logger.info(super.getNodeCode() + super.getNodeperiodtype() 
//			+ kdata.getJFreeChartPeriod(super.getNodeperiodtype()).toString() 
//			+ kdata.getJFreeChartPeriod(super.getNodeperiodtype()).getStart().toString() 
//			+ "nodeamo 数据已经存在，重复添加！"  );
		}
		try {	
			periodhighestzhangdiefu.setNotify(false);
			if( kdata.getPeriodHighestZhangDieFu() != null && kdata.getPeriodHighestZhangDieFu() != 0)
				periodhighestzhangdiefu.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()),kdata.getPeriodHighestZhangDieFu(),false);
		} catch (org.jfree.data.general.SeriesException e) {
//			logger.info(super.getNodeCode() + super.getNodeperiodtype() 
//			+ kdata.getJFreeChartPeriod(super.getNodeperiodtype()).toString() 
//			+ kdata.getJFreeChartPeriod(super.getNodeperiodtype()).getStart().toString() 
//			+ "periodhighestzhangdiefu 数据已经存在，重复添加！"  );
		}
		try {	
			periodlowestzhangdiefu.setNotify(false);
			if( kdata.getPeriodLowestZhangDieFu() != null && kdata.getPeriodLowestZhangDieFu() != 0)
				periodlowestzhangdiefu.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()), kdata.getPeriodLowestZhangDieFu(),false);
		} catch (org.jfree.data.general.SeriesException e) {
//			logger.info(super.getNodeCode() + super.getNodeperiodtype() +  "  "
//			+ kdata.getJFreeChartPeriod(super.getNodeperiodtype()).toString() 
//			+ kdata.getJFreeChartPeriod(super.getNodeperiodtype()).getStart().toString() 
//			+ "periodlowestzhangdiefu 数据已经存在，重复添加！"  );
		}
		
		super.addNewXPeriodData(kdata);
		
//		nodeamo.setNotify(true);
//		nodevol.setNotify(true);
//		periodhighestzhangdiefu.setNotify(true);
//		periodlowestzhangdiefu.setNotify(true);
//		nodeohlc.setNotify(true);
	}
	/*
	 * 
	 */
	public LocalDate getAmoRecordsStartDate ()
	{
		if(super.nodeamozhanbi.getItemCount() == 0)	return null;
		
		TimeSeriesDataItem dataitem = super.nodeamozhanbi.getDataItem(0);
		RegularTimePeriod dataitemperiod = dataitem.getPeriod();
		Date start = dataitem.getPeriod().getStart();
		LocalDate startdate = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate enddate = dataitem.getPeriod().getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	
		if(super.getNodeperiodtype().equalsIgnoreCase(NodeGivenPeriodDataItem.WEEK) ) {
			TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
			LocalDate mondayday = startdate.with(fieldUS, 2);
			return mondayday;
		} else if(super.getNodeperiodtype().equalsIgnoreCase(NodeGivenPeriodDataItem.DAY) ) 
			return startdate;

		return null;
	}
	/*
	 * 
	 */
	public LocalDate getAmoRecordsEndDate ()
	{
		if(super.nodeamozhanbi.getItemCount() == 0)	return null;
		
		int itemcount = super.nodeamozhanbi.getItemCount();
		TimeSeriesDataItem dataitem = super.nodeamozhanbi.getDataItem(itemcount - 1);
		RegularTimePeriod dataitemperiod = dataitem.getPeriod();
		Date start = dataitem.getPeriod().getStart();
		LocalDate startdate = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate enddate = dataitem.getPeriod().getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		if(super.getNodeperiodtype().equalsIgnoreCase( NodeGivenPeriodDataItem.WEEK) ) {
			TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
			LocalDate fridayday = startdate.with(fieldUS, 6);
			return fridayday;
		} else if(super.getNodeperiodtype().equalsIgnoreCase( NodeGivenPeriodDataItem.DAY) ) {
			return enddate;
		}
		
		return null;
	}
	/*
	 * 
	 */
	public void resetAllData ()
	{
		super.resetAllData();
		try {nodeohlc.clear();}  catch (java.lang.NullPointerException e) {}
		try {nodeamo.clear(); }   catch (java.lang.NullPointerException e) {}
		try {nodevol.clear();}   catch (java.lang.NullPointerException e) {}

		//均线
		try {nodeohlcma5.clear();}  catch (java.lang.NullPointerException e) {}
		try {nodeohlcma10.clear();}  catch (java.lang.NullPointerException e) {}
		try {nodeohlcma20.clear();}  catch (java.lang.NullPointerException e) {}
		try {nodeohlcma30.clear();}  catch (java.lang.NullPointerException e) {}
		try {nodeohlcma60.clear();}  catch (java.lang.NullPointerException e) {}
		try {nodeohlcma120.clear();}  catch (java.lang.NullPointerException e) {}
		try {nodeohlcma250.clear();}  catch (java.lang.NullPointerException e) {}
		try {nodeamoma5.clear();}  catch (java.lang.NullPointerException e) {}
		try {nodeamoma10.clear();}  catch (java.lang.NullPointerException e) {}
		try {nodeamoma20.clear();}  catch (java.lang.NullPointerException e) {}
		try {nodeamoma30.clear();}  catch (java.lang.NullPointerException e) {}
		try {nodeamoma60.clear();}  catch (java.lang.NullPointerException e) {}
		try {nodeamoma120.clear();}  catch (java.lang.NullPointerException e) {}
		try {nodeamoma250.clear();}  catch (java.lang.NullPointerException e) {}
		
		try {periodhighestzhangdiefu.clear();} catch (java.lang.NullPointerException e) {}
		try {periodlowestzhangdiefu.clear();} catch (java.lang.NullPointerException e) {}
	}
	/*
	 * 
	 */
	public OHLCSeries getOHLCData () {
		return this.nodeohlc;
	}
	public TimeSeries getMA250 ()	{
		return this.nodeohlcma250;
	}
	public TimeSeries getMA60 () {
		return this.nodeohlcma60;
	}
	/*
	 * 
	 */
	public TimeSeries getAMOData ()	{
		return this.nodeamo;
	}
	public TimeSeries getVOLData ()	{
		return this.nodevol;
	}
	/*
	 * 
	 */
	public org.ta4j.core.TimeSeries getOHLCDataOfTa4jFormat (LocalDate requiredstart, LocalDate requiredend)
	{
		if(this.nodeohlc == null)	return null;
		
		org.ta4j.core.TimeSeries ohlcvaseries = new BaseTimeSeries.SeriesBuilder().withName(super.getNodeCode() + super.getNodeperiodtype()).build();
		LocalDate tmpdate = requiredstart;
		for(int i=0;i<this.nodeohlc.getItemCount();i++) {
			 OHLCItem dataitem = (OHLCItem)this.nodeohlc.getDataItem(i);
			 RegularTimePeriod period = dataitem.getPeriod();
			 tmpdate = period.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			 if(tmpdate.isBefore(requiredstart) )	continue;
			 if(tmpdate.isAfter(requiredend))	break;
			 
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
	public Integer getIndexOfSpecificDateOHLCData (LocalDate requireddate )
	{
		int itemcount = this.nodeohlc.getItemCount();
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod (requireddate);
		if(curperiod == null)	return null;
		
		for(int i= itemcount-1;i>=0;i--) {
//		for(int i=0;i<itemcount;i++) {
			RegularTimePeriod dataitemp = this.nodeohlc.getPeriod(i);
			if(dataitemp.equals(curperiod) )
				 return i ;
		}

		return null;
	}
	/*
	 * 
	 */
	public LocalDate getLocalDateOfSpecificIndexOfOHLCData (Integer index)
	{
		OHLCItem indexrecord = (OHLCItem)this.nodeohlc.getDataItem(index);
		Date start = indexrecord.getPeriod().getStart();
		Date end = indexrecord.getPeriod().getEnd();
		return end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#getOHLCData(java.time.LocalDate, int)
	 * 这个函数之所以不能放到interface里面去，是应为她用到了JFC特有的结构OHLCITEM
	 */
	public OHLCItem getSpecificDateOHLCData (LocalDate requireddate )
	{
		int itemcount = this.nodeohlc.getItemCount();
		RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriod (requireddate );
		if(curperiod == null)	return null;
		
//		for(int i=0;i<itemcount;i++) {
		for(int i=itemcount-1;i>=0;i--) {
			RegularTimePeriod dataitemp = this.nodeohlc.getPeriod(i);
			if(dataitemp.equals(curperiod) )
				 return (OHLCItem)this.nodeohlc.getDataItem(i);
		}
		return null;
	}
	
	/*
	 * 某天的涨跌幅
	 */
	public Double getSpecificOHLCZhangDieFu (LocalDate requireddate )
	{
		Integer indexofcur = this.getIndexOfSpecificDateOHLCData(requireddate);
		if(indexofcur == null)	return null;
		
		try {
			OHLCItem curohlc = (OHLCItem) this.getOHLCData().getDataItem(indexofcur.intValue());
			
			double curclose = curohlc.getCloseValue();
			double curopen = curohlc.getOpenValue();
			if(curclose == 0.0) { //0.0可能是没有数据，也可能是前复权导致的，要判断�?�?
				double curhigh = curohlc.getHighValue();
				double curlow = curohlc.getLowValue();
				if(curhigh == 0.0 && curlow == 0.0) //完全没有波动不合理，说明没有数据
					return null;
			}
			
			OHLCItem lastholc = null; Double lastclose = 0.0;
			try {	lastholc = (OHLCItem) this.getOHLCData().getDataItem(indexofcur.intValue() -1 );
				lastclose = lastholc.getCloseValue();
			}	catch (java.lang.IndexOutOfBoundsException ex) { //新股或者新板块
				double zhangfu = (curclose - curopen) / curopen;
				return zhangfu;
			} //第一�?
			
			if(lastclose == 0.0)	return null;
			
			double zhangfu = (curclose - lastclose) / lastclose;
			return zhangfu;
		} catch (java.lang.NullPointerException e) {e.printStackTrace();}
		
		return null;
	}
	public Double[] getSpcificDateOHLCData (LocalDate requireddate )
	{
		Integer indexofcur = this.getIndexOfSpecificDateOHLCData(requireddate);
		if(indexofcur == null)	return null;
		
		try {
			OHLCItem curohlc = (OHLCItem) this.getOHLCData().getDataItem(indexofcur.intValue());

			double open = curohlc.getOpenValue();
			double high = curohlc.getHighValue();
			double low = curohlc.getLowValue();
			double close = curohlc.getCloseValue();
			
			Double[] ohlcdata = {open,high,low,close};
			return ohlcdata;
		} catch (java.lang.NullPointerException e) {e.printStackTrace();}
		
		return null;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData#getOHLCRecordsStartDate()
	 */
	public Double getSpecificTimeRangeOHLCHightestZhangFu (LocalDate requiredstart,LocalDate requiredend)
	{
		if(requiredstart == null || requiredend == null)
			return null; 
		
		 DayOfWeek d_requiredstart = requiredstart.getDayOfWeek();
		 if(d_requiredstart == DayOfWeek.SATURDAY) requiredstart  = requiredstart.plus(2,ChronoUnit.DAYS);
		 else if( d_requiredstart == DayOfWeek.SUNDAY) requiredstart = requiredstart.plus(1,ChronoUnit.DAYS);
		 
		 DayOfWeek d_requiredend = requiredend.getDayOfWeek();
		 if(d_requiredend == DayOfWeek.SATURDAY) requiredend  = requiredend.minus(1,ChronoUnit.DAYS);
		 else if( d_requiredend == DayOfWeek.SUNDAY) requiredend = requiredend.minus(2,ChronoUnit.DAYS);
		    
		LocalDate curohlcstart = this.getOHLCRecordsStartDate();
		LocalDate curohlcend = this.getOHLCRecordsEndDate();
		if(requiredstart.isBefore(curohlcstart) )	return null;
		if(requiredend.isAfter(curohlcend))	requiredend = curohlcend;

		Integer indexofstart = this.getIndexOfSpecificDateOHLCData(requiredstart);
		if(indexofstart == null) { //没有可能是停牌，�?后找
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
		
		Integer indexofend = this.getIndexOfSpecificDateOHLCData(requiredend);
		if(indexofend == null) { //没有可能是停牌，�?后找
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
		
		if(indexofstart == null || indexofend == null)	return null;
		
		if(indexofstart < indexofend) { //不是同一�?
			OHLCItem dataitem = (OHLCItem) this.getOHLCData().getDataItem(indexofstart.intValue());
			double startclose = dataitem.getCloseValue();
			
			Double highest = startclose;
			for(int i = indexofstart + 1; i<=indexofend ; i++) {
				OHLCItem tmpohlc = (OHLCItem) this.getOHLCData().getDataItem(i);
				double tmphigh = tmpohlc.getCloseValue();
				if(tmphigh > highest)
					highest = tmphigh;
			}
			
			double result = (highest - startclose) / startclose + this.getSpecificOHLCZhangDieFu(requiredstart);
			return result;
			
		} else { //start �? end是同�?�?
			return this.getSpecificOHLCZhangDieFu(requiredstart);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData#getOHLCRecordsStartDate()
	 */
	public LocalDate getOHLCRecordsStartDate ()
	{
		if(this.nodeohlc.getItemCount() == 0)	return null;
		
		RegularTimePeriod firstperiod = nodeohlc.getPeriod(0);
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		if(super.getNodeperiodtype().equalsIgnoreCase (NodeGivenPeriodDataItem.WEEK) ) {
			LocalDate mondayday = startdate.with(DayOfWeek.MONDAY);
			return mondayday;
		} else if(super.getNodeperiodtype().equalsIgnoreCase( NodeGivenPeriodDataItem.DAY)  ) {
			return startdate;
		}
		
		return null;
	}
	/*
	 * 
	 */
	public LocalDate getOHLCRecordsEndDate ()
	{
		if(this.nodeohlc.getItemCount() == 0)	return null;
		
		int itemcount = nodeohlc.getItemCount();
		RegularTimePeriod firstperiod = nodeohlc.getPeriod( itemcount-1);
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		if(super.getNodeperiodtype().equalsIgnoreCase(NodeGivenPeriodDataItem.WEEK) ) {
			LocalDate saturday = enddate.with(DayOfWeek.FRIDAY);
			return saturday;
		} else if(super.getNodeperiodtype().equalsIgnoreCase(NodeGivenPeriodDataItem.DAY) ) {
			return enddate;
		}
		
		return null;
	}

	/*
	 * 
	 */
	public Double getChengJiaoEr (LocalDate requireddate )
	{
		TimeSeriesDataItem curcjlrecord = null;
		RegularTimePeriod period = this.getJFreeChartFormateTimePeriod(requireddate );
		if(period == null)	return null;
					
		curcjlrecord = nodeamo.getDataItem(period);
		if( curcjlrecord == null)	return null;
		else
			return curcjlrecord.getValue().doubleValue();
	}
	/*
	 * 
	 */
	public Double getAverageDailyChengJiaoErOfWeek (LocalDate requireddate )
	{
		Double cje = this.getChengJiaoEr(requireddate);
		if(cje != null) {
			Integer daynum = super.getExchangeDaysNumberForthePeriod(requireddate);
			if(daynum != null)	return cje/daynum;
			else	return cje/5;
		} else	return null;
	}
	/*
	 * /*
			 * 计算指定周期和上周期的成交额差额，�?�合stock/bankuai，dapan有自己的计算方法
	 */

	public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate )
	{
		if(nodeohlc == null)	return null;
		
		RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriod(requireddate ) ;
		if(curperiod == null)	return null;
		
		TimeSeriesDataItem curcjlrecord = nodeamo.getDataItem( curperiod);
		if( curcjlrecord == null)	return null;
		
		int index = nodeamo.getIndex(curperiod);
		try{
			TimeSeriesDataItem lastcjlrecord = nodeamo.getDataItem( index -1 );
			if(lastcjlrecord == null) //休市前还是空，说明要是新板块。板块没有停牌的
				return null;
			
			Double curcje = curcjlrecord.getValue().doubleValue();
			Double lastcje = lastcjlrecord.getValue().doubleValue();
			return curcje - lastcje;
		}	catch (java.lang.IndexOutOfBoundsException ex) {return 100.0;}

	}
	/*
	 * 每日均量和上周均量的差额
	 */
	public Double getChengJiaoErDailyAverageDifferenceWithLastPeriod(LocalDate requireddate )
	{
		if(nodeohlc == null)	return null;
		
		RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriod(requireddate ) ;
		if(curperiod == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = nodeamo.getDataItem( curperiod );
		if( curcjlrecord == null) 	return null;
		
		Integer curexchangedaynum = super.getExchangeDaysNumberForthePeriod(requireddate );
		int index = nodeamo.getIndex(curperiod);
		try{
			TimeSeriesDataItem lastcjlrecord = nodeamo.getDataItem( index -1 );
			if(lastcjlrecord == null)	return null;
			
			RegularTimePeriod lastp = lastcjlrecord.getPeriod();
			Date end = lastp.getEnd();
			LocalDate lastdate = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			Integer lastexchangedaynum = super.getExchangeDaysNumberForthePeriod(lastdate );
			
			double curcje = curcjlrecord.getValue().doubleValue() ;
			Double curcjeave = curcje/ curexchangedaynum;
			double lastcje = lastcjlrecord.getValue().doubleValue();
			Double lastcjeave = lastcje / lastexchangedaynum;
			
			double result = curcjeave - lastcjeave;
			return result;
		}	catch (java.lang.IndexOutOfBoundsException ex) {return null;}

	}
	@Override
	public Integer getChenJiaoErMaxWeek (LocalDate requireddate ) 
	{
		RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriod(requireddate ) ;
		if(curperiod == null)	return null;
		
		TimeSeriesDataItem curcjlrecord = nodeamo.getDataItem(curperiod);
		if( curcjlrecord == null)	return null;
		
		Double curcje = curcjlrecord.getValue().doubleValue();
		int maxweek = 0;
		int index = nodeamo.getIndex(curperiod );
		for(int i = index-1;i >=0; i--) {
			TimeSeriesDataItem lastcjlrecord = nodeamo.getDataItem( i );
			if(lastcjlrecord == null ) //可能到了记录的头部了，或者是个诞生时间不长的板块
			return maxweek;
			
			Double lastcje = lastcjlrecord.getValue().doubleValue();
			if(curcje > lastcje)	maxweek ++;
			else	break;
		}

		return maxweek;
	}
	@Override
	public Integer getAverageDailyChenJiaoErMaxWeek (LocalDate requireddate )
	{
		RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriod(requireddate ) ;
		if(curperiod == null)	return null;
		
		TimeSeriesDataItem curcjlrecord = nodeamo.getDataItem( curperiod);
		if( curcjlrecord == null)	return null;
		
		int curexchangedaynum = super.getExchangeDaysNumberForthePeriod(requireddate);
		Double curcje = curcjlrecord.getValue().doubleValue() / curexchangedaynum;
		
		int maxweek = 0;
		int index = nodeamo.getIndex(curperiod );
		for(int i = index-1;i >=0; i--) {
			
			TimeSeriesDataItem lastcjlrecord = nodeamo.getDataItem( i );
			if(lastcjlrecord == null ) //可能到了记录的头部了，或者是个诞生时间不长的板块
				return maxweek;
			
			LocalDate lastdate = lastcjlrecord.getPeriod().getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			int lastexchangedaynum = super.getExchangeDaysNumberForthePeriod(lastdate);
			Double lastcje = lastcjlrecord.getValue().doubleValue() / lastexchangedaynum;
			if(curcje > lastcje)	maxweek ++;
			else	break;
		}

		return maxweek;
	}
	/*
	 * 计算成交额变化贡献率，即板块成交额的变化占整个上级板块成交额增长量的比率�?
	 */
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai (TDXNodes superbk, LocalDate requireddate ) 
	{
		RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriod(requireddate ) ;
		if(curperiod == null)	return null;
		
		TimeSeriesDataItem curcjlrecord = this.nodeamo.getDataItem( curperiod );
		if( curcjlrecord == null)	return null;
		
		//判断上级板块(大盘或�?�板�?)是否缩量,�?以了没有比较的意义，直接返回-100�?
		String nodept = getNodeperiodtype();
		NodeXPeriodData bkxdata = superbk.getNodeXPeroidData(nodept);
		Double bkcjediff = bkxdata.getChengJiaoErDifferenceWithLastPeriod(requireddate );
		if( bkcjediff == null || bkcjediff < 0   ) //板块缩量�?
			return -100.0;
		
		
		TimeSeriesDataItem lastcjlrecord = null;
		int index = this.nodeamo.getIndex( curperiod );
		try{	lastcjlrecord = nodeamo.getDataItem( index - 1);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			logger.debug("index = 0，可能是新股第一周，可能是数据记录最早记录周，无法判�?");
//			return 100.0; //�?般不会查到记录的第一周，多数发生这种情况是新股第�?周，�?以默认为新股第一�?
//			e.printStackTrace();
		}
		
		if(lastcjlrecord == null) { //说明是停牌后复牌了，或�?�新�?
			try {	Double curggcje = curcjlrecord.getValue().doubleValue(); //新板块所有成交量都应该计算入
					return curggcje/bkcjediff;
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {e.printStackTrace();}
		}
		
		Double curcje = curcjlrecord.getValue().doubleValue();
		Double lastcje = lastcjlrecord.getValue().doubleValue();
		Double cjechange = curcje - lastcje; //个股成交量的变化，如果缩量了也还是计�?
		
		return cjechange/bkcjediff;
	}
	/*
	 * 平均成交额和上周的变化率
	 */
	public Double getAverageDailyChenJiaoErGrowingRate (LocalDate requireddate )
	{
		RegularTimePeriod curperiod = this.getJFreeChartFormateTimePeriod(requireddate ) ;
		if(curperiod == null)	return null;
		
		TimeSeriesDataItem curcjlrecord = this.nodeamo.getDataItem( curperiod );
		if( curcjlrecord == null)	return null;
		
		TimeSeriesDataItem lastcjlrecord = null;
		int index = this.nodeamo.getIndex( curperiod );
		try{	lastcjlrecord = nodeamo.getDataItem( index - 1);
		}	catch (Exception ex) {
			Boolean reachfirstday = super.isLocalDateReachFristDayInHistory (requireddate ); 
			if(reachfirstday != null && reachfirstday == true)
				return null;
			else  return 100.0;
		}

			LocalDate lascjlrecorddate = lastcjlrecord.getPeriod().getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			Double avelastwkcje = this.getAverageDailyChengJiaoErOfWeek(lascjlrecorddate);
			if(avelastwkcje == null)	return 1.0; //new stock
			Double nodeavediff = this.getChengJiaoErDailyAverageDifferenceWithLastPeriod(requireddate );
			if(nodeavediff != null)	return nodeavediff / avelastwkcje;
			else	return 1.0;
	}
	/*
	 * 计算成交额日平均变化贡献率，即基于日平均成交额，板块成交额的变化占整个上级板块成交额增长量的比率
	 */
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage (TDXNodes superbk, LocalDate requireddate ) 
	{	
		RegularTimePeriod curperiod = this.getJFreeChartFormateTimePeriod(requireddate ) ;
		if(curperiod == null)	return null;
		
		TimeSeriesDataItem curcjerecord = this.nodeamo.getDataItem( curperiod );
		if( curcjerecord == null)	return null;

		//判断上级板块是否缩量,缩量没有比较的意义，直接返回-100
		Boolean bkreachfirstday = false;
		String nodept = getNodeperiodtype();
		NodeXPeriodData bkxdata = superbk.getNodeXPeroidData(nodept);
		int bkindex = bkxdata.getAMOData().getIndex(curperiod);
		try {	TimeSeriesDataItem bklastcjerecord = bkxdata.getAMOData().getDataItem( bkindex - 1);
		}	catch (java.lang.IndexOutOfBoundsException ex) {
			logger.debug("index = 0，可能是新股第一周，可能是数据记录最早记录周，无法判断");
			bkreachfirstday = bkxdata.isLocalDateReachFristDayInHistory (requireddate );
			if(bkreachfirstday == null) return null;
		}
		
		if( bkreachfirstday ) { //板块到了第一周，个股所有的成交量都计算, 这里一个无法解决的BUG是板块诞生日可能不是从周一开始，也就是说个股可能是5天平均，板块可能是2天平均，比较复杂，情况特殊就不仔细计算了 
			Double ggcurggcje = this.getAverageDailyChengJiaoErOfWeek(requireddate); //新板块所有成交量都应该计算入
			Double bkcurggcje = bkxdata.getAverageDailyChengJiaoErOfWeek(requireddate); //新板块所有成交量都应该计算入
			return ggcurggcje/bkcurggcje;
		}
		
		Double bkcjediff = bkxdata.getChengJiaoErDailyAverageDifferenceWithLastPeriod(requireddate );
		//板块缩量
		if(bkcjediff == null )	return null;
		if(  bkcjediff < 0   )		return -100.0;
		 
		Boolean gegureachfirstday = false ; 
		int index = this.nodeamo.getIndex( curperiod );
		try {	TimeSeriesDataItem lastcjerecord = nodeamo.getDataItem( index - 1);
		}	catch (java.lang.IndexOutOfBoundsException ex) {
			logger.debug("index = 0，可能是新股第一周，可能是数据记录最早记录周，无法判断");
			gegureachfirstday = super.isLocalDateReachFristDayInHistory (requireddate ); 
			if(gegureachfirstday == null )	return null;
		}
		
		if(gegureachfirstday) {//新个股，所有的成交量、成交额都应该计入，计算的是个股在整个板块成交量的占比,同样问题是个股诞生日可能不是从周一开始，
			try {	Double curggcje = this.getAverageDailyChengJiaoErOfWeek(requireddate); //新板块所有成交量都应该计算入
					return curggcje/bkcjediff;
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {e.printStackTrace(); return null;}
		}
		
		if(this.isNodeDataFuPaiAfterTingPai(superbk,requireddate)) { //说明是停牌后复牌了
			try {	Double curggcje = this.getAverageDailyChengJiaoErOfWeek(requireddate); //新板块所有成交量都应该计算入
					return curggcje/bkcjediff;
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {e.printStackTrace();}
		}
		
		Double nodeavediff = this.getChengJiaoErDailyAverageDifferenceWithLastPeriod(requireddate );
		return nodeavediff/bkcjediff;
	}
	/*
	  * (non-Javadoc)
	  * @see com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic#getLianXuFangLiangPeriodNumber(java.time.LocalDate, int, int)
	  * 计算连续放指定的量的周期�?
	  */
	 public Integer getAverageDailyChenJiaoErLianXuFangLiangPeriodNumber (LocalDate requireddate )
	 {
		RegularTimePeriod curperiod = this.getJFreeChartFormateTimePeriod(requireddate ) ;
		if(curperiod == null)	return null;
			
		 TimeSeriesDataItem curcjlrecord = nodeamo.getDataItem( curperiod);
		 if( curcjlrecord == null) return null;
			
		 int curexchangedaynum = this.getExchangeDaysNumberForthePeriod(requireddate);
		 Double curcje = curcjlrecord.getValue().doubleValue() / curexchangedaynum;
		 
		 int maxweek = 0;
		 int index = nodeamo.getIndex(curperiod );
		 for(int i = index-1;i >=0; i--) {
				TimeSeriesDataItem lastcjlrecord = nodeamo.getDataItem( i );
				if(lastcjlrecord == null )	return maxweek; //可能到了记录的头部了，或者是个诞生时间不长的板块

				LocalDate lastdate = lastcjlrecord.getPeriod().getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				int lastexchangedaynum = this.getExchangeDaysNumberForthePeriod(lastdate);
				Double lastcje = lastcjlrecord.getValue().doubleValue() / lastexchangedaynum;
				if(curcje > lastcje) {
					maxweek ++;
					curcje = lastcje;
				}
				else	break;
		}

		return maxweek;
	 }
	
	 @Override
		public Double getChengJiaoLiang(LocalDate requireddate) 
		{
		 	RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriod(requireddate ) ;
			if(curperiod == null)	return null;
			
			TimeSeriesDataItem curcjlrecord = null;
			curcjlrecord = nodevol.getDataItem( curperiod);
			
			if( curcjlrecord == null)	return null;
			else	return curcjlrecord.getValue().doubleValue();
		}
	 @Override
	 /*
		 * 
		 */
		public Double getAverageDailyChengJiaoLiangOfWeek (LocalDate requireddate )
		{
			Double cjl = this.getChengJiaoLiang(requireddate);
			if(cjl != null) {
				Integer daynum = super.getExchangeDaysNumberForthePeriod(requireddate);
				if(daynum != null)	return cjl/daynum;
				else	return cjl/5;
			} else	return null;
		}
	 @Override
	 /*
		 * 平均成交额和上周的变化率
		 */
		public Double getAverageDailyChenJiaoLiangGrowingRate (LocalDate requireddate )
		{
			RegularTimePeriod curperiod = this.getJFreeChartFormateTimePeriod(requireddate ) ;
			if(curperiod == null)	return null;
			
			TimeSeriesDataItem curcjlrecord = this.nodevol.getDataItem( curperiod );
			if( curcjlrecord == null)	return null;
			
			TimeSeriesDataItem lastcjlrecord = null;
			int index = this.nodevol.getIndex( curperiod );
			try{	lastcjlrecord = nodevol.getDataItem( index - 1);
			}	catch (java.lang.IndexOutOfBoundsException ex) {
				Boolean reachfirstday = super.isLocalDateReachFristDayInHistory (requireddate ); 
				if(reachfirstday != null && reachfirstday == true)
					return null;
				else return 100.0;
			}
			
			LocalDate lascjlrecorddate = lastcjlrecord.getPeriod().getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			Double avelastwkcje = this.getAverageDailyChengJiaoErOfWeek(lascjlrecorddate);
			if(avelastwkcje == null)	return 1.0; //new stock
			
			Double nodeavediff = this.getChengJiaoErDailyAverageDifferenceWithLastPeriod(requireddate );
			if(nodeavediff != null)	return nodeavediff / avelastwkcje;
			else	return 1.0;
		}
	 @Override
	 public Double getChengJiaoLiangDailyAverageDifferenceWithLastPeriod (LocalDate requireddate )
	 {
			if(nodeohlc == null)	return null;
			
			RegularTimePeriod curperiod = this.getJFreeChartFormateTimePeriod(requireddate ) ;
			if(curperiod == null)	return null;
			
			TimeSeriesDataItem curcjlrecord = nodevol.getDataItem( curperiod );
			if( curcjlrecord == null)	return null;
			
			Integer curexchangedaynum = super.getExchangeDaysNumberForthePeriod(requireddate );
			int index = nodevol.getIndex(curperiod);
			try{
				TimeSeriesDataItem lastcjlrecord = nodevol.getDataItem( index -1 );
				if(lastcjlrecord == null) 	return null;
				
				RegularTimePeriod lastp = lastcjlrecord.getPeriod();
				Date end = lastp.getEnd();
				LocalDate lastdate = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				Integer lastexchangedaynum = super.getExchangeDaysNumberForthePeriod(lastdate );
				
				double curcje = curcjlrecord.getValue().doubleValue() ;
				Double curcjeave = curcje/ curexchangedaynum;
				double lastcje = lastcjlrecord.getValue().doubleValue();
				Double lastcjeave = lastcje / lastexchangedaynum;
				
				double result = curcjeave - lastcjeave;
				return result;
			}	catch (java.lang.IndexOutOfBoundsException ex) {return 100.0;	}
	 }
	 
	 @Override
		public Double getChenJiaoLiangDifferenceWithLastPeriod(LocalDate requireddate) 
		{
			if(nodeohlc == null)	return null;
			
			RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriod(requireddate ) ;
			if(curperiod == null)	return null;
			
			TimeSeriesDataItem curcjlrecord = nodevol.getDataItem( curperiod);
			if( curcjlrecord == null)	return null;
			
			try{	int index = nodevol.getIndex(curperiod);
					TimeSeriesDataItem lastcjlrecord = nodevol.getDataItem( index -1 );
					if(lastcjlrecord == null)	return null; //休市前还是空，说明要是新板块。板块没有停牌的

					Double curcje = curcjlrecord.getValue().doubleValue();
					Double lastcje = lastcjlrecord.getValue().doubleValue();
					
					return curcje - lastcje;
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {return 100.0;}
		}
	 	@Override
		public Integer getAverageDailyChenJiaoLiangMaxWeek (LocalDate requireddate )
		{
			RegularTimePeriod curperiod = this.getJFreeChartFormateTimePeriod(requireddate ) ;
			if(curperiod == null)	return null;
			
			TimeSeriesDataItem curcjlrecord = nodevol.getDataItem( curperiod);
			if( curcjlrecord == null)	return null;
			
			int curexchangedaynum = this.getExchangeDaysNumberForthePeriod(requireddate);
			Double curcje = curcjlrecord.getValue().doubleValue() / curexchangedaynum;
			
			int maxweek = 0;
			int index = nodevol.getIndex(curperiod );
			for(int i = index-1;i >=0; i--) {
				
				TimeSeriesDataItem lastcjlrecord = nodevol.getDataItem( i );
				if(lastcjlrecord == null )	return maxweek; //可能到了记录的头部了，或者是个诞生时间不长的板块

				LocalDate lastdate = lastcjlrecord.getPeriod().getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				int lastexchangedaynum = this.getExchangeDaysNumberForthePeriod(lastdate);
				Double lastcje = lastcjlrecord.getValue().doubleValue() / lastexchangedaynum;
				if(curcje > lastcje)	maxweek ++;
				else	break;
			}

			return maxweek;
		}
	 @Override
		public Integer getChenJiaoLiangMaxWeek (LocalDate requireddate) 
		{
		 	RegularTimePeriod curperiod = this.getJFreeChartFormateTimePeriod(requireddate ) ;
			if(curperiod == null)	return null;
			
			TimeSeriesDataItem curcjlrecord = nodevol.getDataItem( curperiod);
			if( curcjlrecord == null)	return null;
			
			Double curcje = curcjlrecord.getValue().doubleValue();
			int maxweek = 0;
			int index = nodevol.getIndex(curperiod );
			for(int i = index-1;i >=0; i--) {
				
				TimeSeriesDataItem lastcjlrecord = nodevol.getDataItem( i );
				if(lastcjlrecord == null ) //可能到了记录的头部了，或者是个诞生时间不长的板块
					return maxweek;
				
				Double lastcje = lastcjlrecord.getValue().doubleValue();
				if(curcje > lastcje)	maxweek ++;
				else	break;
			}

			return maxweek;
		}
		@Override
		public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuai (TDXNodes superbk, LocalDate requireddate) 
		{
			RegularTimePeriod curperiod = this.getJFreeChartFormateTimePeriod(requireddate ) ;
			if(curperiod == null)	return null;
			
			TimeSeriesDataItem curcjlrecord = this.nodevol.getDataItem( curperiod );
			if( curcjlrecord == null)	return null;
			
			//判断上级板块(大盘或�?�板�?)是否缩量,�?以了没有比较的意义，直接返回-100�?
			String nodept = getNodeperiodtype();
			NodeXPeriodData bkxdata = superbk.getNodeXPeroidData(nodept);
			Double bkcjediff = bkxdata.getChenJiaoLiangDifferenceWithLastPeriod(requireddate );
			if( bkcjediff == null || bkcjediff < 0   )		return -100.0; //板块缩量�?
			
			TimeSeriesDataItem lastcjlrecord = null;
			int index = this.nodevol.getIndex( curperiod );
			try{	lastcjlrecord = nodevol.getDataItem( index - 1);
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				logger.debug("index = 0，无法判�?");
				return 100.0;
//				e.printStackTrace();
			}
			if(lastcjlrecord == null) { //休市前还是空，说明是停牌后复牌了
				try {	Double curggcje = curcjlrecord.getValue().doubleValue(); //新板块所有成交量都应该计算入
						return curggcje/bkcjediff;
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {e.printStackTrace();}
			}
			
			Double curcje = curcjlrecord.getValue().doubleValue();
			Double lastcje = lastcjlrecord.getValue().doubleValue();
			Double cjechange = curcje - lastcje; //个股成交量的变化
			
			return cjechange/bkcjediff;
		}
		/*
		 * 计算成交额日平均变化贡献率，即基于日平均成交额，板块成交额的变化占整个上级板块成交额增长量的比率�?
		 */
		public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuaiOnDailyAverage (TDXNodes superbk, LocalDate requireddate ) 
		{	
			RegularTimePeriod curperiod = this.getJFreeChartFormateTimePeriod(requireddate ) ;
			if(curperiod == null)	return null;
			
			TimeSeriesDataItem curcjlrecord = this.nodevol.getDataItem( curperiod );
			if( curcjlrecord == null)	return null;

			//判断上级板块是否缩量,缩量没有比较的意义，直接返回-100
			Boolean bkreachfirstday = false;
			String nodept = getNodeperiodtype();
			NodeXPeriodData bkxdata = superbk.getNodeXPeroidData(nodept);
			int bkindex = bkxdata.getVOLData().getIndex(curperiod);
			try {	TimeSeriesDataItem bklastcjlrecord = bkxdata.getVOLData().getDataItem( bkindex - 1);
			}	catch (java.lang.IndexOutOfBoundsException ex) {
				logger.debug("index = 0，可能是新股第一周，可能是数据记录最早记录周，无法判断");
				bkreachfirstday = bkxdata.isLocalDateReachFristDayInHistory (requireddate );
				if(bkreachfirstday == null) return null;
			}
			
			if( bkreachfirstday ) { //板块到了第一周，个股所有的成交量都计算, 这里一个无法解决的BUG是板块诞生日可能不是从周一开始，也就是说个股可能是5天平均，板块可能是2天平均，比较复杂，情况特殊就不仔细计算了 
				Double ggcurggcjl = this.getAverageDailyChengJiaoLiangOfWeek(requireddate); //新板块所有成交量都应该计算入
				Double bkcurggcjl = bkxdata.getAverageDailyChengJiaoLiangOfWeek(requireddate); //新板块所有成交量都应该计算入
				return ggcurggcjl/bkcurggcjl;
			}
			
			Double bkcjldiff = bkxdata.getChengJiaoLiangDailyAverageDifferenceWithLastPeriod(requireddate );
			//板块缩量
			if(bkcjldiff == null )	return null;
			if(  bkcjldiff < 0   )		return -100.0;
			 
			Boolean gegureachfirstday = false ; 
			int index = this.nodevol.getIndex( curperiod );
			try {	TimeSeriesDataItem lastcjerecord = nodevol.getDataItem( index - 1);
			}	catch (java.lang.IndexOutOfBoundsException ex) {
				logger.debug("index = 0，可能是新股第一周，可能是数据记录最早记录周，无法判断");
				gegureachfirstday = super.isLocalDateReachFristDayInHistory (requireddate ); 
				if(gegureachfirstday == null )	return null;
			}
			
			if(gegureachfirstday) {//新个股，所有的成交量、成交额都应该计入，计算的是个股在整个板块成交量的占比,同样问题是个股诞生日可能不是从周一开始，
				try {	Double curggcjl = this.getAverageDailyChengJiaoLiangOfWeek(requireddate); //新板块所有成交量都应该计算入
						return curggcjl/bkcjldiff;
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {e.printStackTrace(); return null;}
			}
			
			if(this.isNodeDataFuPaiAfterTingPai(superbk,requireddate)) { //说明是停牌后复牌了
				try {	Double curggcjl = this.getAverageDailyChengJiaoLiangOfWeek(requireddate); //新板块所有成交量都应该计算入
						return curggcjl/bkcjldiff;
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {e.printStackTrace();}
			}
			
			Double nodeavediff = this.getChengJiaoLiangDailyAverageDifferenceWithLastPeriod(requireddate );
			return nodeavediff/bkcjldiff;
		}
//		@Override
//		public Integer getCjlLianXuFangLiangPeriodNumber(LocalDate requireddate) 
//		{
//			int recordnum = this.nodevol.getItemCount();
//			int lianxu = 0;
//			Double curcje = this.getChengJiaoEr(requireddate );
//			for(int wkindex = 1;wkindex > (0 - recordnum) ; wkindex--) { 
//				Double recordcjelast = this.getChengJiaoEr(requireddate - wkindex);
//				if( curcje >= recordcjelast ) 
//					lianxu ++;
//				else
//					break;
//			}
//			
//			return lianxu;
//		}
//		@Override
//		public Integer getCjlDpMaxLianXuFangLiangPeriodNumber(LocalDate requireddate ,int settindpgmaxwk)
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
		public Double[] getNodeAMOMA (LocalDate  requireddate )
		{
			int itemcount = this.nodeamo.getItemCount();
			double[] amodata = new double[itemcount];
			for(int i=0;i < this.nodeamo.getItemCount();i++) {
				TimeSeriesDataItem dataitem = this.nodeamo.getDataItem(i);
				double close = dataitem.getValue().doubleValue();
				amodata[i] = close;
			}
			
//			requireddate = super.adjustDate(requireddate); //先确保日期是在交易日�?
			RegularTimePeriod expectedperiod = this.getJFreeChartFormateTimePeriod(requireddate );
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
		
		for(int i= itemcount-1;i >=0;i--) {
			OHLCItem dataitem = (OHLCItem)this.nodeohlc.getDataItem(i);
			RegularTimePeriod period = dataitem.getPeriod();
			
			if(i>=5) {
				Double ma5 = null;
				if( this.nodeohlcma5.getIndex(period) <0  )
					ma5 = StatUtils.mean(closedata, i-4, 5);
				if(ma5 != null)		nodeohlcma5.addOrUpdate(period,ma5);
			}
			if(i>=10) {
				Double ma10 = null;
				if( this.nodeohlcma10.getIndex(period) <0  )
					ma10 = StatUtils.mean(closedata, i-9, 10);
				if(ma10 != null)	nodeohlcma10.addOrUpdate(period,ma10);
			}
			if(i>=20) {
				Double ma20 = null;
				if( this.nodeohlcma20.getIndex(period) <0  )
					ma20 = StatUtils.mean(closedata, i-19, 20);
				if(ma20 != null)	nodeohlcma20.addOrUpdate(period,ma20);
			}
			if(i>=60) {
				Double ma60 = null;
				if( this.nodeohlcma60.getIndex(period) <0  )
					ma60 = StatUtils.mean(closedata, i-59, 60);
				if(ma60 != null)	nodeohlcma60.addOrUpdate(period,ma60);
			}
			if(i>=250) {
				Double ma250 = null;
				if( this.nodeohlcma250.getIndex(period) <0  )
					ma250 = StatUtils.mean(closedata, i-249, 250);
				if(ma250 != null)	nodeohlcma250.addOrUpdate(period,ma250);
			}
		}
		checkNodeOhlcMA ();
		return;
	}
	private void checkNodeOhlcMA ()
	{
		double checkvalue = 0.0; int duplicatecount = 0;RegularTimePeriod dupperiod = null;
		checkvalue = 0.0;duplicatecount = 0;
		int itemcount = this.nodeohlcma10.getItemCount();
		for(int i=0;i<itemcount;i++) {
			TimeSeriesDataItem ma10item = this.nodeohlcma10.getDataItem(i);
			double ma10 = ma10item.getValue().doubleValue();
			if(ma10 == checkvalue) {
				duplicatecount ++;
				dupperiod = ma10item.getPeriod();
			}
			else { checkvalue = ma10; duplicatecount =0;};
			
			if(duplicatecount >= 5) {
				logger.info(super.getNodeCode() + "MA10计算有误，出现连续等值数据，请检�?.!" + "日期�?" + dupperiod.getEnd().toString());
				break;
			}
		}
		checkvalue = 0.0;duplicatecount = 0;
		itemcount = this.nodeohlcma20.getItemCount();
		for(int i=0;i<itemcount;i++) {
			TimeSeriesDataItem ma20item = this.nodeohlcma20.getDataItem(i);
			double ma20 = ma20item.getValue().doubleValue();
			if(ma20 == checkvalue) {
				duplicatecount ++;
				dupperiod = ma20item.getPeriod();
			}
			else { checkvalue = ma20; duplicatecount =0;};
			
			if(duplicatecount >= 10) {
				logger.info(super.getNodeCode() + "MA20计算有误，出现连续等值数据，请检�?!" + "日期�?" + dupperiod.getEnd().toString());
				break;
			}
		}
		checkvalue = 0.0;duplicatecount = 0;
		itemcount = this.nodeohlcma60.getItemCount();
		for(int i=0;i<itemcount;i++) {
			TimeSeriesDataItem ma60item = this.nodeohlcma60.getDataItem(i);
			double ma60 = ma60item.getValue().doubleValue();
			if(ma60 == checkvalue) {
				duplicatecount ++;
				dupperiod = ma60item.getPeriod();
			}
			else { checkvalue = ma60; duplicatecount =0;};
			
			if(duplicatecount >= 20) {
				logger.info(super.getNodeCode() + "MA60计算有误，出现连续等值数据，请检�?.!" + "日期�?" + dupperiod.getEnd().toString());
				debugOhlcMADate (nodeohlcma60);
				break;
			}
		}
		checkvalue = 0.0;duplicatecount = 0;
		itemcount = this.nodeohlcma250.getItemCount();
		for(int i=0;i<itemcount;i++) {
			TimeSeriesDataItem ma250item = this.nodeohlcma250.getDataItem(i);
			double ma250 = ma250item.getValue().doubleValue();
			if(ma250 == checkvalue) {
				duplicatecount ++;
				dupperiod = ma250item.getPeriod();
			}
			else { checkvalue = ma250; duplicatecount =0;};
			
			if(duplicatecount >= 20) {
				logger.info(super.getNodeCode() + "MA250计算有误，出现连续等值数据，请检�?!" + "日期�?" + dupperiod.getEnd().toString());
				debugOhlcMADate (nodeohlcma250);
				break;
			}
		}
	}
	private  void debugOhlcMADate (TimeSeries nodeohlcma)
	{
		logger.info("CLOSE MAVALUE \r\n");
		int itemcount = nodeohlcma.getItemCount();
		for(int i=0;i<itemcount;i++) {
			TimeSeriesDataItem maitem = nodeohlcma.getDataItem(i);
			double ma = maitem.getValue().doubleValue();
			
			RegularTimePeriod maperiod = maitem.getPeriod();
			int itemohlccount = this.nodeohlc.getItemCount();
			for(int j=0;i < itemohlccount;j++) {
				OHLCItem dataitem = (OHLCItem)this.nodeohlc.getDataItem(j);
				RegularTimePeriod ohlcperiod = dataitem.getPeriod();
				if(maperiod.getEnd().equals(ohlcperiod.getEnd())) {
					double close = dataitem.getCloseValue();
					System.out.println(maperiod.getEnd().toString() + "CLOSE " + String.valueOf(close) + " MAVALUE " + String.valueOf(ma) + " \r\n");
					break;
				}
			}
		}
	}
	/*
	 * 通过apache math计算MA
	 */
	 public Double[] getNodeOhlcMA (LocalDate  requireddate )
	 {
//		requireddate = super.adjustDate(requireddate); //先确保日期是在交易周�?
		RegularTimePeriod expectedperiod = this.getJFreeChartFormateTimePeriod(requireddate );
		if(expectedperiod == null)	return null;
			
		int itemcount = this.nodeohlc.getItemCount();
		double[] closedata = new double[itemcount];
		for(int i=0;i < this.nodeohlc.getItemCount();i++) {
			OHLCItem dataitem = (OHLCItem)this.nodeohlc.getDataItem(i);
			double close = dataitem.getCloseValue();
			closedata[i] = close;
		}
		
		Integer itemindex = this.getIndexOfSpecificDateOHLCData(requireddate );
		if(itemindex == null)
			return null;
		
		Double ma5 = null;
		Integer ma5index = this.nodeohlcma5.getIndex(expectedperiod);
		if(ma5index != null && ma5index <0 && itemindex>=5) {
			ma5 = StatUtils.mean(closedata, itemindex-4, 5);
			nodeohlcma5.add(expectedperiod,ma5,false);
		} else	{
			TimeSeriesDataItem ma5item = this.nodeohlcma5.getDataItem(expectedperiod);
			if(ma5item != null)
			ma5 = ma5item.getValue().doubleValue();
		}
		 
		Double ma10 = null;
		Integer ma10index = this.nodeohlcma10.getIndex(expectedperiod);
		if(ma10index != null && ma10index <0 && itemindex>=10) {
			ma10 = StatUtils.mean(closedata, itemindex-9, 10);
			nodeohlcma10.add(expectedperiod,ma10,false);
		} else	{
			TimeSeriesDataItem ma10item = this.nodeohlcma10.getDataItem(expectedperiod);
			if(ma10item != null)
				ma10 = ma10item.getValue().doubleValue();
		} 
		
		Double ma20 = null;
		Integer ma20index = this.nodeohlcma20.getIndex(expectedperiod);
		if(ma20index != null && ma20index <0 && itemindex>=20) {
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
		try {
			if(ma60index != null && ma60index <0 && itemindex>=60) {
				ma60 = StatUtils.mean(closedata, itemindex-59, 60);
				nodeohlcma60.add(expectedperiod,ma60,false);
			} else
			{
				TimeSeriesDataItem ma60item = this.nodeohlcma60.getDataItem(expectedperiod);
				if(ma60item != null)
				ma60 = ma60item.getValue().doubleValue();
			} 
		} catch (java.lang.NullPointerException e) {
			e.printStackTrace();
		}
		
		Double ma250 = null;
		Integer ma250index = this.nodeohlcma250.getIndex(expectedperiod);
		if(ma250index != null && ma250index <0 && itemindex>=250) {
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
	 public Double[] getNodeOhlcSMA (LocalDate  requireddate )
	 {
		 requireddate = super.adjustDate(requireddate); //先确保日期是在交易日�?
		 
		 org.ta4j.core.TimeSeries ohlcvaseries = this.getOHLCDataOfTa4jFormat(this.getOHLCRecordsStartDate(), this.getOHLCRecordsEndDate() );
		 
		 	Integer location = null; //获取该日期在数据集中的位�?
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
	 public Multimap<LocalDate, LocalDate> isMacdTopDivergenceInSpecificMonthRange (LocalDate  requireddate , int monthrange)
	 {
		 requireddate = super.adjustDate(requireddate); //先确保日期是在交易日�?
		 
		 org.ta4j.core.TimeSeries ohlcvaseries = this.getOHLCDataOfTa4jFormat(requireddate.minusMonths(monthrange + 3),requireddate);
		 
		 Multimap<LocalDate,LocalDate>  macddivergencemap = ArrayListMultimap.create(); 

		 ClosePriceIndicator closePrice = new ClosePriceIndicator(ohlcvaseries);
		 MACDIndicator macd = new MACDIndicator (closePrice);
		 
		 List<LocalDate> macdreversedatelist = new ArrayList <> (); 
		 List<Integer> macdreverseIndexlist = new ArrayList <> ();
		 LocalDate requrestart = requireddate.minusMonths(monthrange);
		 int indexcursor = ohlcvaseries.getEndIndex();
		 for(int i = indexcursor-2; i>=0; i--) {
			 double macdsecond = macd.getValue(i+1).doubleValue();
			 Bar barsecond = ohlcvaseries.getBar(i +1);
			 LocalDate seconddate = barsecond.getEndTime().toLocalDate();
			 
			 if(macdsecond >=0) //小于0的MACD底背离才有意�?
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
				 
				 if(curclose > lstclose && curmacd < lstmacd &&  lstmacd == toppesemacdbetweentwopoints ) { //两个背离的MACD点之间不应该有更大的�?
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
	 public Multimap<LocalDate, LocalDate> isMacdButtomDivergenceInSpecificMonthRange (LocalDate  requireddate , int monthrange)
	 {
		 requireddate = this.adjustDate(requireddate); //先确保日期是在交易日�?
		 
		 org.ta4j.core.TimeSeries ohlcvaseries = this.getOHLCDataOfTa4jFormat(requireddate.minusMonths(monthrange + 3),requireddate);
		 
		 Multimap<LocalDate,LocalDate>  macddivergencemap = ArrayListMultimap.create(); 

		 ClosePriceIndicator closePrice = new ClosePriceIndicator(ohlcvaseries);
		 MACDIndicator macd = new MACDIndicator (closePrice);
		 List<LocalDate> macdreversedatelist = new ArrayList <> (); 
		 List<Integer> macdreverseIndexlist = new ArrayList <> ();
		 LocalDate requrestart = requireddate.minusMonths(monthrange);
		 int indexcursor = ohlcvaseries.getEndIndex();
		 
//		 ZonedDateTime endtime = ohlcvaseries.getBar(indexcursor).getEndTime();
		 for(int i = indexcursor-2; i>=0; i--) {
			 double macdsecond = macd.getValue(i+1).doubleValue();
			 Bar barsecond = ohlcvaseries.getBar(i +1);
			 LocalDate seconddate = barsecond.getEndTime().toLocalDate();
			 
			 if(macdsecond >=0) //小于0的MACD底背离才有意�?
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
				 
				 if(curclose < lstclose && curmacd > lstmacd &&  lstmacd == lowesemacdbetweentwopoints ) { //两个背离的MACD点之间不应该有更小的�?
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
	 public Boolean checkCloseComparingToMAFormula (LocalDate requireddate, String maformula)
	 {
			OHLCItem ohlcdata =  this.getSpecificDateOHLCData (requireddate);
			LocalDate expectdate = requireddate;
			if(ohlcdata == null) { //日线有可能当日是停牌的，如果停牌，就找到本周有数据的�?新天
				String nodeperiod = this.getNodeperiodtype();
				if( nodeperiod.equalsIgnoreCase(NodeGivenPeriodDataItem.DAY ) ) {
					DayOfWeek dayOfWeek = requireddate.getDayOfWeek();
					int dayOfWeekIntValue = dayOfWeek.getValue();
					for(int i = 0;i < dayOfWeekIntValue;i++) { 
						expectdate = requireddate.plus(0-i,ChronoUnit.DAYS);
						OHLCItem ohlcdataexpectdate =  this.getSpecificDateOHLCData (expectdate);
						if(ohlcdataexpectdate != null) {
//							expectdate = super.adjustDate(expectdate);
							requireddate = expectdate;
				    		break;
						}
				    }
				} else if( nodeperiod.equalsIgnoreCase(NodeGivenPeriodDataItem.WEEK ) ) { //�?周没有数据，说明停牌，目前周线MA不�?�么用，直接回F
					return false;
				}
			}
		 
			OHLCItem ohlcdatafinal =  this.getSpecificDateOHLCData (requireddate);
			if(ohlcdatafinal == null) return true; //�?有没有OHLC data 都认为是满足条件
			
	    	Double close = (Double)ohlcdatafinal.getCloseValue();
	    	Double[] maresult = this.getNodeOhlcMA(expectdate);
		    Boolean result = checkCloseComparingToMAsettings (close,maresult,maformula);
		    if( result != null )	return result;
		    
		    return null;
	 }
	 /*
	  * 
	  */
	 private Boolean checkCloseComparingToMAsettings (Double close,Double[] maresult,String maformula1)
	 {
			 if(maformula1 == null || maformula1.isEmpty())
				 return false;
			String maformula = maformula1.toUpperCase();
			try{
				if(maformula.contains("MA250")  ) {
					if (maresult[6] != null)
						maformula = maformula.replace("MA250",  maresult[6].toString() ) ;
					else
						maformula = maformula.replace("MA250",  String.valueOf( -10000000000.0 ) ) ;
				}
			} catch (java.lang.NullPointerException e) {e.printStackTrace();	}
			
			try {
		    if(maformula.contains("MA120") ) {
		    	if (maresult[5] != null)
		    		maformula = maformula.replace("MA120",  maresult[5].toString() ) ;
		    	else
					maformula = maformula.replace("MA120",  String.valueOf( -10000000000.0 ) ) ;
		    }
			} catch (java.lang.NullPointerException e) {e.printStackTrace();}
			
			try {
		    if(maformula.contains("MA60")  ) {
		    	if(maresult[4] != null)
		    		maformula = maformula.replace("MA60",  maresult[4].toString() ) ;
		    	else
					maformula = maformula.replace("MA60",  String.valueOf( -10000000000.0 ) ) ;
		    }
			} catch (java.lang.NullPointerException e) {e.printStackTrace();}
		    	
		    try {
		    if(maformula.contains("MA30") ) {
		    	if(maresult[3] != null)
		    		maformula = maformula.replace("MA30",  maresult[3].toString() ) ;
		    	else
					maformula = maformula.replace("MA30",  String.valueOf( -10000000000.0 ) ) ;
		    }
		    } catch (java.lang.NullPointerException e) {e.printStackTrace();}
		    try {
		    if(maformula.contains("MA20")  ) {
		    	if(maresult[2] != null)
		    		maformula = maformula.replace("MA20",  maresult[2].toString() ) ;
		    	else
					maformula = maformula.replace("MA20",  String.valueOf( -10000000000.0 ) ) ;
		    }
		    } catch (java.lang.NullPointerException e) {e.printStackTrace();}
		    try {
		    if(maformula.contains("MA10")  ) {
		    	if(maresult[1] != null)
		    		maformula = maformula.replace("MA10",  maresult[1].toString() ) ;
		    	else
					maformula = maformula.replace("MA10",  String.valueOf( -10000000000.0 ) ) ;
		    }
		    } catch (java.lang.NullPointerException e) {e.printStackTrace();}
		    try {
		    if(maformula.contains("MA5") ) {
		    	if(maresult[0] != null)
		    		maformula = maformula.replace("MA5",  maresult[0].toString() ) ;
		    	else
					maformula = maformula.replace("MA5",  String.valueOf( -10000000000.0 ) ) ;
		    }
		    } catch (java.lang.NullPointerException e) {e.printStackTrace();}
		    
		    try{
		    	BigDecimal result = null; 
		    	if(maformula.contains("CLOSE"))
		    		maformula = maformula.replace("CLOSE", "C");
		    	if(close != null)
		    		result = new Expression(maformula).with("C",String.valueOf(close) ).eval(); //https://github.com/uklimaschewski/EvalEx
		    	else
		    		result = new Expression(maformula).eval(); //https://github.com/uklimaschewski/EvalEx
		    	
		    	String sesultstr = result.toString();
			    if(sesultstr.equals("0"))
			    	return false;
			    else 
			    	return true;
		    } catch (com.udojava.evalex.Expression.ExpressionException e) {	e.printStackTrace();	return false;		    }
		}
	 /*
	  * 
	  */
	 public Object getNodeDataByKeyWord( String keyword, LocalDate date,  String... maformula)
	 {
		 Object value = null;
		 value = super.getNodeDataByKeyWord (keyword,date);
		 if(value != null)
			 return value;
		 
		 Double[] ohlc = this.getSpcificDateOHLCData(date);
		 switch(keyword) {
		 case "ChenJiaoEr" :
		      	 Double curcje  = this.getChengJiaoEr(date) ;
		   	     value = curcje;
		   	     break;
		 case "AverageChenJiaoEr" :
			 Double avecje = this.getAverageDailyChengJiaoErOfWeek (date);
	   	     value = avecje;
	   	     break;
		 case "AverageChenJiaoErMaxWeek" :
			 Integer cjemaxwk =  this.getAverageDailyChenJiaoErMaxWeek(date);//显示成交额是多少周最�?,成交额多少周�?小没有意义，因为如果不是完整周成交量就是会很�?
	   	     value = cjemaxwk;
	   	     break;
		 case "ChengJiaoErDifferenceWithLastPeriod":
			 Double difference = this.getChengJiaoErDifferenceWithLastPeriod(date);
			 value = difference;
			 break;
		 case "ChengJiaoErDailyAverageDifferenceWithLastPeriod":
			 Double averagediff = this.getChengJiaoErDailyAverageDifferenceWithLastPeriod (date);
			 value = averagediff;
		 break;
		 case "ChengJiaoErDailyAverageGrowingRate":
			 Double averagecjegr = this.getAverageDailyChenJiaoErGrowingRate (date);
			 value = averagecjegr;
		 break;
		 case "ChenJiaoLiang" :
			 Double curcjl = this.getChengJiaoLiang(date);
	   	     value = curcjl;
	   	     break;
		 case "AverageChenJiaoLiang" :
			 Double avecjl = this.getAverageDailyChengJiaoLiangOfWeek (date);
	   	     value = avecjl;
	   	     break;
		 case "AverageChenJiaoLiangMaxWeek" :
			 Integer cjlmaxwk = this.getAverageDailyChenJiaoLiangMaxWeek(date);//显示cjl是多少周�?�?
	   	     value = cjlmaxwk;
	   	 break;
		 case "ChengJiaoLiangDailyAverageGrowingRate":
			 Double averagecjlgr = getAverageDailyChenJiaoLiangGrowingRate (date);
			 value = averagecjlgr;
		 break;
		 case "ChengJiaoLiangDailyAverageDifferenceWithLastPeriod":
			 Double averagediffcjl = getChengJiaoLiangDailyAverageDifferenceWithLastPeriod (date);
			 value = averagediffcjl;
		 break;
		 case "ZhangDieFu" :
			 Double zhangfu = this.getSpecificOHLCZhangDieFu (date);
      		 value = zhangfu;
	   	     break; 
		 case "CLOSEVSMA":
			 Boolean maresult = this.checkCloseComparingToMAFormula (date, maformula[0]);
			 value = maresult;
			 break;
		 case "GuJiaCLOSE":
			 Integer indexofcur = this.getIndexOfSpecificDateOHLCData(date);
			 if(indexofcur == null)	return null;
			 
			 OHLCItem curohlc = (OHLCItem) this.getOHLCData().getDataItem(indexofcur.intValue());
			 Double curclose = curohlc.getCloseValue();
			 value = curclose;
			 break;
		 case "DailyZhangDieFuRangeInWeek":
			 Double wkhighzhangfu = this.getSpecificTimeHighestZhangDieFu(date);
			 value = wkhighzhangfu;
			 break;
		 case "CjeLianXuZhang": //周日平均成交额MAXWK
	        	Integer cjelx = this.getAverageDailyChenJiaoErLianXuFangLiangPeriodNumber(date); 
	        	value = cjelx;
	        	break; 
		 case "OpenUpQueKou": 
			 	Integer opupqk = this.getQueKouTongJiOpenUp(date);
		 		value = opupqk;			
		 break;
		 case "HuiBuDownQueKou":
			 Integer huibudowquekou = this.getQueKouTongJiHuiBuDown(date);
			 value = huibudowquekou;
		 break;
		 case "OpenDownQueKou":
			 Integer opendownquekou = this.getQueKouTongJiOpenDown(date);
			 value = opendownquekou;
		 break;
		 case "HuiBuUpQueKou":
			 Integer huibuupquekou = this.getQueKouTongJiHuiBuUp(date);
			 value = huibuupquekou;
		 break;
		 case "ZhangTing":
			 Integer zhangtingnum = this.getZhangTingTongJi(date);
			 value = zhangtingnum;
		 break;
		 case "DieTing":
			 Integer dietingnum = this.getDieTingTongJi(date);
			 value = dietingnum;
		 break;
		 case "Open":try {	 value = ohlc[0];  } catch (java.lang.NullPointerException e) {	 value = null;}
		 break;
		 case "High":try {	 value = ohlc[1]; } catch (java.lang.NullPointerException e) {value = null;}
		 break;
		 case "Low":
			 try {	 value = ohlc[2]; } catch (java.lang.NullPointerException e) {value = null;}
		 break;
		 case "Close":
			 try {	 value = ohlc[3]; } catch (java.lang.NullPointerException e) {value = null;}
		 break;
		 }
		 return value;
	 }
	 /*
	  * 
	  */
	 public String[] getNodeXDataCsvData (TDXNodes superbk, LocalDate requireddate)
	 {
		 String [] result = new String [NodeXPeriodData.NODEXDATACSVDATAHEADLINE.length];
		 for(int i=0;i<NodeXPeriodData.NODEXDATACSVKeyWords.length;i++) {
			 String datakeyword = NodeXPeriodData.NODEXDATACSVKeyWords[i];
			 Object value = this.getNodeDataByKeyWord(datakeyword, requireddate, "");
			 if(value != null && value.getClass() == java.lang.Double.class  ) {
				 if(NodeXPeriodData.OuputFormatControl[i].getClass() == java.lang.Integer.class) {
					 result[i] = String.valueOf(value);
				 } else {
					 DecimalFormat decimalformate = (DecimalFormat)NodeXPeriodData.OuputFormatControl[i]; 
					 String formatresult = decimalformate.format((Double)value);
					 result[i] =  formatresult;
				 }
			 } else if(value != null && value.getClass() == java.lang.Integer.class ) {
				 result[i] = String.valueOf(value);
			 } else {
				result[i] = String.valueOf("0");
			 }
		 }
		 
		return result; 
	 }
	 /*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic#getNodeXDataInHtml(java.time.LocalDate, int)
		 */
		public String getNodeXDataInHtml (TDXNodes superbk,LocalDate requireddate) 
		{
			String[] csvdataresult = getNodeXDataCsvData (superbk,requireddate);
			
			String htmlstring = "";
			org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
			Elements body = doc.getElementsByTag("body");
			for(Element elbody : body) {
				org.jsoup.nodes.Element dl = elbody.appendElement("dl");
				 
				 org.jsoup.nodes.Element lidate = dl.appendElement("li");
				 org.jsoup.nodes.Element fontdate = lidate.appendElement("font");
				 fontdate.appendText(requireddate.toString());
				 fontdate.attr("color", "#17202A");
				 
				 for(int i=0;i<NodeXPeriodData.NODEXDATACSVDATAHEADLINE.length;i++) {
					 String value = csvdataresult[i];
					 if(value != null && !value.equals("0") && OuputToHtmlToolTipControl[i] == 1) {
						 org.jsoup.nodes.Element li = dl.appendElement("li");
						 org.jsoup.nodes.Element font = li.appendElement("font");
						 font.appendText(NODEXDATACSVDATAHEADLINE[i] + ":" + value  );
						 font.attr("color", OuputColorControl[i]);
					 } 
				 }
			}
			
			return doc.toString();
		}
		/*
		 * 
		 */
		protected Boolean isNodeDataFuPaiAfterTingPai (TDXNodes superbk, LocalDate requireddate )
		{
			RegularTimePeriod curperiod = this.getJFreeChartFormateTimePeriod(requireddate );
			if(curperiod == null)	return null;
			
			int index = this.nodeamo.getIndex( curperiod );
			TimeSeriesDataItem lastcjlrecord = null;
			try {	lastcjlrecord = nodeamo.getDataItem( index - 1);
			} catch (java.lang.ArrayIndexOutOfBoundsException e ) {
				logger.debug("index = 0，可能是新股第一�?");
				return true;
			} catch (java.lang.IndexOutOfBoundsException ex ) {
				logger.debug("index = 0，可能是新股第一�?");
				return true;
			}
			Date lastdate = lastcjlrecord.getPeriod().getEnd();
			LocalDate lastld = lastdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			//算法就是把node上周数据的日期和大盘上周的日期比较，如果不一样，说明node停牌过，没停牌的上周数据日期肯定�?样�??
			String nodept = getNodeperiodtype();
			DaPan dapan = (DaPan)superbk.getRoot();
			NodeXPeriodData dpxata = dapan.getNodeXPeroidData(nodept);
			TimeSeries dpamo = dpxata.getAMOData();
			Integer dapcurindx = null ;
			try {			 dapcurindx = dpamo.getIndex(curperiod);
			} catch ( java.lang.NullPointerException e) {
				e.printStackTrace();
			}
			if(dapcurindx == 0 ) return false ;//大盘都到第一周了，肯定没停牌
			if(dapcurindx < 0 ) return false ;
			
			LocalDate dplstwkdata = null;
			try {
				for(int i= 1;i<=4;i++) { //大盘连续停牌4周的可能性大概为0
					TimeSeriesDataItem dplstrecords = dpamo.getDataItem(dapcurindx -i);
					if(dplstrecords != null) { //大盘上周的数据找�?
						dplstwkdata = dplstrecords.getPeriod().getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						break;
					} else {} //说明是假�?
				}
			} catch (java.lang.IndexOutOfBoundsException e) {
				e.printStackTrace();
			}

			WeekFields weekFields = WeekFields.of(Locale.getDefault());
			int year2 = lastld.getYear();
			int weeknumber2 = lastld.get(weekFields.weekOfWeekBasedYear());
			
			int year1 = dplstwkdata.getYear();
			int weeknumber1 = dplstwkdata.get(weekFields.weekOfWeekBasedYear());
			
			if(year1 != year2)
				return true;
			else if(year1 == year2 && weeknumber1 != weeknumber2  ) //大盘�?般不会停牌，以板块为基准
				return true;
			else 
				return false;
		}
		
		public TimeSeries getPeriodHighestZhangDieFuData ()
		{
			return this.periodhighestzhangdiefu;
		}
		public TimeSeries getPeriodLowestZhangDieFuData ()
		{
			return this.periodlowestzhangdiefu;
		} 
		/*
		 * 
		 */
		public void addPeriodHighestZhangDieFu (LocalDate requireddate,Double zhangfu)
		{int i=0;
			try {	
				periodhighestzhangdiefu.setNotify(false);
				
				RegularTimePeriod recordwk = super.getJFreeChartFormateTimePeriod(requireddate);
				Double curzhangfu = this.getSpecificTimeHighestZhangDieFu(requireddate);
				if(curzhangfu == null  ){
					try{	periodhighestzhangdiefu.add(recordwk, zhangfu, false );
					} catch(Exception e) { logger.info(super.getNodeCode() + ":periodhighestzhangdiefu" + recordwk + "/" + recordwk.getEnd() + "已经存在数据！\r\n");
						return;
					}
					return;
				}
				
				try {
					if( CommonUtility.round(curzhangfu,6).compareTo( CommonUtility.round(zhangfu,6) ) !=0) {
						try{	this.periodhighestzhangdiefu.delete(recordwk);
								periodhighestzhangdiefu.add(recordwk, zhangfu, false );
						} catch(Exception e) {logger.info(super.getNodeCode() + ":periodhighestzhangdiefu" + recordwk + "/" + recordwk.getEnd() + "已经存在数据！\r\n");
							return;
						}
						return;
					}
				} catch (java.lang.NullPointerException e) { e.printStackTrace(); }
				
			} catch (org.jfree.data.general.SeriesException e) {}
		}
		/*
		 * 
		 */
		public void addPeriodLowestZhangDieFu (LocalDate requireddate, Double diefu)
		{
			try {	
				periodlowestzhangdiefu.setNotify(false);
				
				RegularTimePeriod recordwk = super.getJFreeChartFormateTimePeriod(requireddate);
				
				Double curzhangfu = this.getSpecificTimeLowestZhangDieFu(requireddate);
				if(curzhangfu == null  ) {
					try{	periodlowestzhangdiefu.add(recordwk, diefu, false );
					} catch(Exception e) { logger.info(super.getNodeCode() + ":periodlowestzhangdiefu" + recordwk + "/" + recordwk.getEnd() + "已经存在数据！\r\n");
						return;
					}
					return;
				}
				
				try {
					if( CommonUtility.round(curzhangfu,6).compareTo( CommonUtility.round(diefu,6) ) !=0) {
						try{
							this.periodlowestzhangdiefu.delete(recordwk);
							periodlowestzhangdiefu.add(recordwk, diefu, false );
						} catch(Exception e) { logger.info(super.getNodeCode() + ":periodlowestzhangdiefu" + recordwk + "/" + recordwk.getEnd() + "已经存在数据！\r\n");
							return;
						}
						return;
					}
				} catch (java.lang.NullPointerException e ) {
					e.printStackTrace();
					System.out.print(super.getNodeCode() + "curzhangfu / diefu:" +  curzhangfu / diefu);
				}
				
			} catch (org.jfree.data.general.SeriesException e) {}
		}
		/*
		 * 得到某个时期的涨跌幅  DailyZhangDieFuRangeInWeek
		 */
		public Double getSpecificTimeHighestZhangDieFu (LocalDate requireddate )
		{
			RegularTimePeriod period = getJFreeChartFormateTimePeriod(requireddate );
			if(period == null)	return null;
			
			TimeSeriesDataItem curhighzdfrecord = periodhighestzhangdiefu.getDataItem(period);
			Double curhzdf = null ;
			try {	curhzdf = curhighzdfrecord.getValue().doubleValue();
			} catch (Exception e) {return null;}
			
			return curhzdf;
		}
		/*
		 * 
		 */
		public Double getSpecificTimeLowestZhangDieFu (LocalDate requireddate )
		{
			RegularTimePeriod period = getJFreeChartFormateTimePeriod(requireddate );
			if(period == null)	return null;
			TimeSeriesDataItem curlowzdfrecord = periodlowestzhangdiefu.getDataItem(period);
			Double curlzdf = null ;
			try { curlzdf = curlowzdfrecord.getValue().doubleValue();
			} catch (Exception e) {return null;}
			
			return curlzdf;
		}
 } //END OF 
