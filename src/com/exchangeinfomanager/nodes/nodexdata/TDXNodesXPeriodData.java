package com.exchangeinfomanager.nodes.nodexdata;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.joda.time.Interval;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.FormatDoubleToShort;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.udojava.evalex.Expression;




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
	protected OHLCSeries nodeohlc; 
	protected TimeSeries nodeamo; 
	protected TimeSeries nodevol; 
	protected TimeSeries nodeamozhanbi; 
	protected TimeSeries nodevolzhanbi; 
	protected TimeSeries nodefxjg; //
	protected TimeSeries nodeexchangedaysnumber ; //
	 // 缺口的统计结果，不直接存放缺口的原因是已经有ohlc了，可以计算出缺口，如果再专门存放缺口list有点重复，不如存放缺口统计数据
	protected TimeSeries nodeqktjopenup;
	protected TimeSeries nodeqktjhuibuup;
	protected TimeSeries nodeqktjopendown;
	protected TimeSeries nodeqktjhuibudown;
	protected List<QueKou> qklist;
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
	
	private LocalDate firstdayinhistory; //可以知道历史记录起点在哪里
	
	
	public Boolean isLocalDateReachFristDayInHistory (LocalDate requireddate,int difference)
	{
		if(this.firstdayinhistory == null)
			return null;
		
		if(this.nodeperiodtype.equals(TDXNodeGivenPeriodDataItem.DAY) ) {
			if(requireddate.equals(this.firstdayinhistory))
				return true;
			else
				return false;
		} else if(this.nodeperiodtype.equals(TDXNodeGivenPeriodDataItem.WEEK) ) {
			Boolean checkresult = CommonUtility.isInSameWeek(requireddate, this.firstdayinhistory);
			return checkresult;
		}
		
		return null;
	}
	public void setShangShiRiQi (LocalDate requireddate)
	{
		this.firstdayinhistory = requireddate;
	}
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
		} catch (java.lang.IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			nodeamo.setNotify(false);
			nodevol.setNotify(false);
			nodeamo.add(kdata.getPeriod(),kdata.getMyOwnChengJiaoEr(),false);
			nodevol.add(kdata.getPeriod(),kdata.getMyownchengjiaoliang(),false);
			if(kdata.getCjeZhanBi() != null) {
				nodeamozhanbi.setNotify(false);
				nodeamozhanbi.add(kdata.getPeriod(),kdata.getCjeZhanBi(),false);
				nodevolzhanbi.setNotify(false);
				nodevolzhanbi.add(kdata.getPeriod(),kdata.getCjlZhanBi(),false);
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
	public void resetQueKouTongJiJieGuo ()
	{
		if(nodeqktjopenup != null)
			nodeqktjopenup.clear();
		if(nodeqktjhuibuup != null)
			nodeqktjhuibuup.clear();
		if(nodeqktjopendown != null)
			nodeqktjopendown.clear();
		if(nodeqktjhuibudown != null)
			nodeqktjhuibudown.clear();
	}
	public void addQueKouTongJiJieGuo (LocalDate qkdate,
			Integer nodeqktjopenup1,Integer nodeqktjhuibuup1,Integer nodeqktjopendown1,Integer nodeqktjhuibudown1,
			Boolean addbasedoncurdata)
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
			if(nodeqktjopenup1 != null  )
			if(addbasedoncurdata) {
				TimeSeriesDataItem dataitem = nodeqktjopenup.getDataItem(period);
				if(dataitem == null) 
					nodeqktjopenup.add(period,nodeqktjopenup1);
				else {
					int count = dataitem.getValue().intValue();
					nodeqktjopenup.addOrUpdate(period, count + nodeqktjopenup1);
				}
			} else
				nodeqktjopenup.addOrUpdate(period,nodeqktjopenup1);

		} catch (org.jfree.data.general.SeriesException e) {
			e.printStackTrace();
		}
		
		try {
			if(nodeqktjhuibuup1 != null && nodeqktjhuibuup1 !=0 )
			if(addbasedoncurdata) {
				TimeSeriesDataItem dataitem = nodeqktjhuibuup.getDataItem(period);
				if(dataitem == null)
					nodeqktjhuibuup.add(period,nodeqktjhuibuup1);
				else {
					int count = dataitem.getValue().intValue();
					nodeqktjhuibuup.addOrUpdate(period, count + nodeqktjhuibuup1);
				}
					
			} else
				nodeqktjhuibuup.addOrUpdate(period,nodeqktjhuibuup1);
		} catch (org.jfree.data.general.SeriesException e) {
			e.printStackTrace();
		}
		
		try {
			if(nodeqktjopendown1 != null && nodeqktjopendown1 !=0 )
			if(addbasedoncurdata) {
				TimeSeriesDataItem dataitem = nodeqktjopendown.getDataItem(period);
				if(dataitem == null)
					nodeqktjopendown.add(period,nodeqktjopendown1);
				else {
					int count = dataitem.getValue().intValue();
					nodeqktjopendown.addOrUpdate(period, count + nodeqktjopendown1);
				}
					
			} else
				nodeqktjopendown.addOrUpdate(period,nodeqktjopendown1);

		} catch (org.jfree.data.general.SeriesException e) {
			e.printStackTrace();
		}
		
		try {
			if(nodeqktjhuibudown1 != null && nodeqktjhuibudown1 !=0 )
			if(addbasedoncurdata) {
				TimeSeriesDataItem dataitem = nodeqktjhuibudown.getDataItem(period);
				if(dataitem == null)
					nodeqktjhuibudown.add(period,nodeqktjhuibudown1);
				else {
					int count = dataitem.getValue().intValue();
					nodeqktjhuibudown.addOrUpdate(period, count + 1);
				}
					
			} else
				nodeqktjhuibudown.addOrUpdate(period,nodeqktjhuibudown1);

		} catch (org.jfree.data.general.SeriesException e) {
			e.printStackTrace();
		}
	}
	public List<QueKou> getPeriodQueKou ()
	{
		return this.qklist;
	}
	public void setPeriodQueKou (List<QueKou> qkl)
	{
		this.qklist = qkl;
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
			 return (Integer)qktjitem.getValue().intValue(); 
		
	}
	public Integer getQueKouTongJiOpenDown (LocalDate requireddate, Integer difference)
	{
		if(this.nodeqktjopendown == null)
			return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjopendown.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(qktjitem == null)
			return null;
		else
			 return (Integer)qktjitem.getValue().intValue(); 
		
	}
	public Integer getQueKouTongJiHuiBuUp (LocalDate requireddate, Integer difference)
	{
		if(this.nodeqktjhuibuup == null)
			return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjhuibuup.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(qktjitem == null)
			return null;
		else
			 return (Integer)qktjitem.getValue().intValue(); 
		
	}
	public Integer getQueKouTongJiHuiBuDown (LocalDate requireddate, Integer difference)
	{
		if(this.nodeqktjhuibudown == null)
			return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjhuibudown.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(qktjitem == null)
			return null;
		else
			 return qktjitem.getValue().intValue(); 
	}
	
	/*
	 * 
	 */
	public OHLCSeries getOHLCData ()
	{
		return this.nodeohlc;
	}
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
	 * 某天的涨跌幅
	 */
	public Double getSpecificOHLCZhangDieFu (LocalDate requireddate,int difference)
	{
		
		Integer indexofcur = this.getIndexOfSpecificDateOHLCData(requireddate, 0);
		OHLCItem curohlc = (OHLCItem) this.getOHLCData().getDataItem(indexofcur.intValue());
		double curclose = curohlc.getCloseValue();
		
		OHLCItem lastholc = (OHLCItem) this.getOHLCData().getDataItem(indexofcur.intValue() -1 );
		double lastclose = lastholc.getCloseValue();
		double zhangfu = (curclose - lastclose) / lastclose;
		
		return zhangfu;
		
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
	public  LocalDate  getQueKouRecordsStartDate ()
	{ 
		if(nodeqktjopenup == null)
			return null;
		
		if(nodeqktjopenup.getItemCount() == 0)
			return null;

		RegularTimePeriod firstperiod = nodeqktjopenup.getTimePeriod( 0);
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
	public  LocalDate  getQueKouRecordsEndDate ()
	{ 
		if(nodeqktjopenup == null || nodeqktjopenup.getItemCount() == 0)
			return null;

		int itemcount = nodeqktjopenup.getItemCount();
		RegularTimePeriod firstperiod = nodeqktjopenup.getTimePeriod( itemcount - 1 );
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
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
			java.sql.Date lastdayofweek = java.sql.Date.valueOf(expectedate);
			period = new org.jfree.data.time.Week (lastdayofweek);
//			period = new org.jfree.data.time.Week (Date.from(expectedate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		} else if(nodeperiod.equals(TDXNodeGivenPeriodDataItem.DAY)) { //锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷荩锟斤拷锟斤拷锟斤拷锟酵拷趴煞锟斤拷锟�
			expectedate = requireddate.plus(difference,ChronoUnit.DAYS);
			java.sql.Date lastdayofweek = java.sql.Date.valueOf(expectedate);
			period = new org.jfree.data.time.Day (lastdayofweek);
//			period = new org.jfree.data.time.Day(Date.from(expectedate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
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
	 * 计算成交额变化贡献率，即板块成交额的变化占整个上级板块成交额增长量的比率，
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
	 public Integer getCjeLianXuFangLiangPeriodNumber (LocalDate requireddate,int difference,int settindpgmaxwk)
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
	  * 周线计算 1，2，4，6，12，24，55 对应日线5，10，20，30，60，120，250，
	  */
	 public Double[] getNodeAmoMA (LocalDate  requireddate,int difference)
	 {
		 Integer junxiancanshu[] = null ;
		 if(this.nodeperiodtype.equals(TDXNodeGivenPeriodDataItem.WEEK) ) 
			 junxiancanshu = new Integer[]{1,2,4,6,12,24,55};
		 else if(this.nodeperiodtype.equals( TDXNodeGivenPeriodDataItem.DAY ) )
			 junxiancanshu = new Integer[]{5,10,20,30,60,120,250};
		 
		 RegularTimePeriod period = getJFreeChartFormateTimePeriod(requireddate,difference);
		 Double ma5;Double ma10;Double ma20;Double ma30;Double ma60;Double ma120;Double ma250;
		 try {
			 TimeSeriesDataItem qktjitem = this.nodeamoma5.getDataItem(period );
			 if(qktjitem == null) {
				 ma5 = calculateAmoMA (junxiancanshu[0],requireddate,difference);
				 if(ma5 != null)
					 this.nodeamoma5.add(period,ma5);
			 } else
				ma5 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodeamoma5 = new TimeSeries(this.nodeperiodtype);
			 	ma5 = calculateAmoMA (junxiancanshu[0],requireddate,difference);
			 	if(ma5 != null)
			 		this.nodeamoma5.add(period,ma5);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodeamoma10.getDataItem(period );
			 if(qktjitem == null) {
				 ma10 = calculateAmoMA (junxiancanshu[1],requireddate,difference);
				 if(ma10 != null)
					 this.nodeamoma10.add(period,ma10);
			 } else
				ma10 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodeamoma10 = new TimeSeries(this.nodeperiodtype);
			 	ma10 = calculateAmoMA (junxiancanshu[1],requireddate,difference);
			 	if(ma10 != null)
			 		this.nodeamoma10.add(period,ma10);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodeamoma20.getDataItem(period );
			 if(qktjitem == null) {
				 ma20 = calculateAmoMA (junxiancanshu[2],requireddate,difference);
				 if(ma20 != null)
					 this.nodeamoma20.add(period,ma20);
			 } else
				ma20 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodeamoma20 = new TimeSeries(this.nodeperiodtype);
			 	ma20 = calculateAmoMA (junxiancanshu[2],requireddate,difference);
			 	if(ma20 != null)
			 		this.nodeamoma20.add(period,ma20);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodeamoma30.getDataItem(period );
			 if(qktjitem == null) {
				 ma30 = calculateAmoMA (junxiancanshu[3],requireddate,difference);
				 if(ma30 != null)
					 this.nodeamoma30.add(period,ma30);
			 } else
				ma30 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodeamoma30 = new TimeSeries(this.nodeperiodtype);
			 	ma30 = calculateAmoMA (junxiancanshu[3],requireddate,difference);
			 	if(ma30 != null)
			 		this.nodeamoma30.add(period,ma30);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodeamoma60.getDataItem(period );
			 if(qktjitem == null) {
				 ma60 = calculateAmoMA (junxiancanshu[4],requireddate,difference);
				 if(ma60 != null)
					 this.nodeamoma60.add(period,ma60);
			 } else
				ma60 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodeamoma60 = new TimeSeries(this.nodeperiodtype);
			 	ma60 = calculateAmoMA (junxiancanshu[4],requireddate,difference);
			 	if(ma60 != null)
			 		this.nodeamoma60.add(period,ma60);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodeamoma120.getDataItem(period );
			 if(qktjitem == null) {
				 ma120 = calculateAmoMA (junxiancanshu[5],requireddate,difference);
				 if(ma120 != null)
					 this.nodeamoma120.add(period,ma120);
			 } else
				ma120 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodeamoma120 = new TimeSeries(this.nodeperiodtype);
			 	ma120 = calculateAmoMA (junxiancanshu[5],requireddate,difference);
			 	if(ma120 != null)
			 		this.nodeamoma120.add(period,ma120);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodeamoma250.getDataItem(period );
			 if(qktjitem == null) {
				 ma250 = calculateAmoMA (junxiancanshu[6],requireddate,difference);
				 if(ma250 != null)
					 this.nodeamoma250.add(period,ma250);
			 } else
				ma250 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodeamoma250 = new TimeSeries(this.nodeperiodtype);
			 	ma250 = calculateAmoMA (junxiancanshu[6],requireddate,difference);
			 	if(ma250 != null)
			 		this.nodeamoma250.add(period,ma250);
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
		
		Double ohlcsum = 0.0;
		for(int i=0;i<manum;i++) {
			TimeSeriesDataItem data = (TimeSeriesDataItem) this.nodeamo.getDataItem(indexofrc -i);
			ohlcsum = ohlcsum + (Double)data.getValue().doubleValue();
		}
		
		Double result = ohlcsum / manum;
		return result;
	 } 
	 /*
	  * 用户如果传来的是周六/日的日期，最好转为当周的周五
	  */
	 private LocalDate adjustDate(LocalDate dateneedtobeadjusted,int difference )
	 {
		 LocalDate friday;
		 LocalDate expectedate = dateneedtobeadjusted.plus(difference,ChronoUnit.DAYS);
		 DayOfWeek dayofweek = expectedate.getDayOfWeek();
		 if( dayofweek.equals(DayOfWeek.SUNDAY)  ) {
			friday = dateneedtobeadjusted.minus(2,ChronoUnit.DAYS);
			return friday;
		 } else if (  dayofweek.equals(DayOfWeek.SATURDAY)  ) {
			friday = dateneedtobeadjusted.minus(1,ChronoUnit.DAYS);
			return friday;
		 }
					
		return expectedate;
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
			    Double[] maresult = this.getNodeOhlcMA(tmpdate, 0);
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
		    
		    BigDecimal result1 = new Expression(maformula).with("x",String.valueOf(close)).eval(); //https://github.com/uklimaschewski/EvalEx
		    String sesultstr = result1.toString();
		    if(sesultstr.equals("0"))
		    	return false;
		    else 
		    	return true;
		    
		    
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
	  * 周线计算 1，2，4，6，12，24，55 对应日线5，10，20，30，60，120，250，
	  */
	 public Double[] getNodeOhlcMA (LocalDate  requireddate,int difference)
	 {
		 Integer junxiancanshu[] = null ;
		 if(this.nodeperiodtype.equals(TDXNodeGivenPeriodDataItem.WEEK) ) 
			 junxiancanshu = new Integer[]{5,10,20,30,60,120,250};
		 else if(this.nodeperiodtype.equals( TDXNodeGivenPeriodDataItem.DAY ) )
			 junxiancanshu = new Integer[]{5,10,20,30,60,120,250};
		 
		 
		 requireddate = this.adjustDate(requireddate, difference); //先确保日期是在交易日内
		 RegularTimePeriod period = getJFreeChartFormateTimePeriod(requireddate,difference);
		 
		 Double ma5;Double ma10;Double ma20;Double ma30;Double ma60;Double ma120;Double ma250;
		 try {
			 TimeSeriesDataItem qktjitem = this.nodeohlcma5.getDataItem(period );
			 if(qktjitem == null) {
				 ma5 = calculateOhlcMA (junxiancanshu[0],requireddate,difference);
				 if(ma5 != null)
					 this.nodeohlcma5.add(period,ma5);
			 } else
				ma5 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodeohlcma5 = new TimeSeries(this.nodeperiodtype);
			 	ma5 = calculateOhlcMA (junxiancanshu[0],requireddate,difference);
			 	if(ma5 != null)
			 		this.nodeohlcma5.add(period,ma5);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodeohlcma10.getDataItem(period );
			 if(qktjitem == null) {
				 ma10 = calculateOhlcMA (junxiancanshu[1],requireddate,difference);
				 if(ma10 != null)
					 this.nodeohlcma10.add(period,ma10);
			 } else
				ma10 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodeohlcma10 = new TimeSeries(this.nodeperiodtype);
			 	ma10 = calculateOhlcMA (junxiancanshu[1],requireddate,difference);
			 	if(ma10 != null)
			 		this.nodeohlcma10.add(period,ma10);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodeohlcma20.getDataItem(period );
			 if(qktjitem == null) {
				 ma20 = calculateOhlcMA (junxiancanshu[2],requireddate,difference);
				 if(ma20 != null)
					 this.nodeohlcma20.add(period,ma20);
			 } else
				ma20 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodeohlcma20 = new TimeSeries(this.nodeperiodtype);
			 	ma20 = calculateOhlcMA (junxiancanshu[2],requireddate,difference);
			 	if(ma20 != null)
			 		this.nodeohlcma20.add(period,ma20);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodeohlcma30.getDataItem(period );
			 if(qktjitem == null) {
				 ma30 = calculateOhlcMA (junxiancanshu[3],requireddate,difference);
				 if(ma30 != null)
					 this.nodeohlcma30.add(period,ma30);
			 } else
				ma30 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodeohlcma30 = new TimeSeries(this.nodeperiodtype);
			 	ma30 = calculateOhlcMA (junxiancanshu[3],requireddate,difference);
			 	if(ma30 != null)
			 		this.nodeohlcma30.add(period,ma30);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodeohlcma60.getDataItem(period );
			 if(qktjitem == null) {
				 ma60 = calculateOhlcMA (junxiancanshu[4],requireddate,difference);
				 if(ma60 != null)
					 this.nodeohlcma60.add(period,ma60);
			 } else
				ma60 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodeohlcma60 = new TimeSeries(this.nodeperiodtype);
			 	ma60 = calculateOhlcMA (junxiancanshu[4],requireddate,difference);
			 	if(ma60 != null)
			 		this.nodeohlcma60.add(period,ma60);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodeohlcma120.getDataItem(period );
			 if(qktjitem == null) {
				 ma120 = calculateOhlcMA (junxiancanshu[5],requireddate,difference);
				 if(ma120 != null)
					 this.nodeohlcma120.add(period,ma120);
			 } else
				ma120 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodeohlcma120 = new TimeSeries(this.nodeperiodtype);
			 	ma120 = calculateOhlcMA (junxiancanshu[5],requireddate,difference);
			 	if(ma120 != null)
			 		this.nodeohlcma120.add(period,ma120);
		 }
		 
		 try {
			 TimeSeriesDataItem qktjitem = this.nodeohlcma250.getDataItem(period );
			 if(qktjitem == null) {
				 ma250 = calculateOhlcMA (junxiancanshu[6],requireddate,difference);
				 if(ma250 != null)
					 this.nodeohlcma250.add(period,ma250);
			 } else
				ma250 = (Double)qktjitem.getValue(); 
		 } catch (java.lang.NullPointerException e) {
			 	nodeohlcma250 = new TimeSeries(this.nodeperiodtype);
			 	ma250 = calculateOhlcMA (junxiancanshu[6],requireddate,difference);
			 	if(ma250 != null)
			 		this.nodeohlcma250.add(period,ma250);
		 }
		 
		 Double ma[] = {ma5,ma10,ma20,ma30,ma60,ma120,ma250};
		 return ma;
	 }
	 /*
	  * 
	  */
	 private Double calculateOhlcMA (Integer manum,LocalDate  requireddate,int difference)
	 {
		Integer indexofrc = this.getIndexOfSpecificDateOHLCData(requireddate, 0);
		if(indexofrc == null)
			return null;
		
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
	 
		@Override
		public Integer getCjlLianXuFangLiangPeriodNumber(LocalDate requireddate, int difference, int settindpgmaxwk) 
		{
			int recordnum = this.nodeohlc.getItemCount();
			int lianxu = 0;
			for(int wkindex = 0;wkindex > (0 - recordnum) ; wkindex--) { 
				Integer recordmaxbkwklast = this.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(requireddate,wkindex);
				if( recordmaxbkwklast != null && recordmaxbkwklast >= settindpgmaxwk) 
					lianxu ++;
				else if( recordmaxbkwklast != null && recordmaxbkwklast < settindpgmaxwk)
					return lianxu;
//				else if(recordmaxbkwklast == null )
//						;
			}
			
			return -1;
		}

		@Override
		public Double getChenJiaoLiangDifferenceWithLastPeriod(LocalDate requireddate, int difference) 
		{
			if(nodeohlc == null)
				return null;
			
			TimeSeriesDataItem curcjlrecord = nodevol.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference) );
			if( curcjlrecord == null) 
				return null;
			
			int index = nodevol.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference));
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
		public Double getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate, int difference) 
		{
			TimeSeriesDataItem curcjlrecord = this.nodevolzhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			if( curcjlrecord == null) 
				return null;
			
			int index = nodevol.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference));
			
			try {
				TimeSeriesDataItem lastcjlrecord = nodevolzhanbi.getDataItem( index -1  );
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

		@Override
		public Integer getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) 
		{
			TimeSeriesDataItem curcjlrecord = this.nodevolzhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			if( curcjlrecord == null) 
				return null;
						
			Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
			
			int index = nodevolzhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
			
			int maxweek = 0;
			for(int i = index - 1; i >= 0; i--) {
				
				
				TimeSeriesDataItem lastcjlrecord = nodevolzhanbi.getDataItem( i );
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

		@Override
		public Integer getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate, int difference) 
		{
			if(nodeohlc == null)
				return null;
			
			TimeSeriesDataItem curcjlrecord = this.nodevolzhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			if( curcjlrecord == null) 
				return null;

			Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
			
			int index = nodevolzhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
			int minweek = 0;
			for(int i = index -1; i >= 0; i --) {
				TimeSeriesDataItem lastcjlrecord = nodevolzhanbi.getDataItem( i );
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

		@Override
		public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuai(TDXNodes superbk, LocalDate requireddate,	int difference) 
		{
			TimeSeriesDataItem curcjlrecord = this.nodevol.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference) );
			if( curcjlrecord == null) 
				return null;
			
			//判断上级板块(大盘或者板块)是否缩量,所以了没有比较的意义，直接返回-100；
			String nodept = getNodeperiodtype();
			NodeXPeriodDataBasic bkxdata = superbk.getNodeXPeroidData(nodept);
			Double bkcjediff = bkxdata.getChenJiaoLiangDifferenceWithLastPeriod(requireddate,difference);
									   
			if( bkcjediff == null || bkcjediff < 0   ) {//板块缩量，
				return -100.0;
			}
			
			TimeSeriesDataItem lastcjlrecord = null;
			int index = this.nodevol.getIndex( getJFreeChartFormateTimePeriod(requireddate,difference) );
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

		@Override
		public Integer getChenJiaoLiangMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) 
		{
			TimeSeriesDataItem curcjlrecord = nodevol.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			if( curcjlrecord == null) 
				return null;
			
			Double curcje = curcjlrecord.getValue().doubleValue();
			int itemcount = nodevol.getItemCount();
			int maxweek = 0;
			
			int index = nodevol.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
			
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
		public Double getChengJiaoLiang(LocalDate requireddate, int difference) 
		{
			TimeSeriesDataItem curcjlrecord = null;
//			if(difference >=0 )
				curcjlrecord = nodevol.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			
			if( curcjlrecord == null) 
				return null;
			else
				return curcjlrecord.getValue().doubleValue();
		}

		@Override
		public Double getChenJiaoLiangZhanBi(LocalDate requireddate, int difference) 
		{
			if(nodeohlc == null)
				return null;
			
			TimeSeriesDataItem curcjlrecord = null;
//			if(difference >=0 )
				curcjlrecord = nodevolzhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			
			if( curcjlrecord == null) 
				return null;
			else
				return curcjlrecord.getValue().doubleValue();
		}
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic#getNodeXDataInHtml(java.time.LocalDate, int)
		 */
		@Override
		public String getNodeXDataInHtml(TDXNodes superbk,LocalDate requireddate, int difference) 
		{
			Double curcje = this.getChengJiaoEr(requireddate, 0);
			String cjedanwei = null;
			try{
				cjedanwei = FormatDoubleToShort.getNumberChineseDanWei(curcje);
				curcje = FormatDoubleToShort.formateDoubleToShort (curcje);
			} catch (java.lang.NullPointerException e) {
//				e.printStackTrace();
				logger.debug(this.nodecode + "在" + requireddate.toString() + "没有数据，可能停牌。");
				return "";
			}
	    	
	    		
	    	Integer cjemaxwk = null;
	    	try{
	    		cjemaxwk = this.getChenJiaoErMaxWeekOfSuperBanKuai(requireddate,0);//显示成交额是多少周最大
	    	} catch (java.lang.NullPointerException e) {
	    		
	    	}
	    	Integer cjeminwk = this.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(requireddate, 0);
	    	
			Double cjechangerate = this.getChenJiaoErChangeGrowthRateOfSuperBanKuai(superbk,requireddate,0);//成交额大盘变化贡献率
			Double curcjezhanbidata = this.getChenJiaoErZhanBi(requireddate, 0);  //占比
			Integer cjemaxweek = this.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(requireddate,0);//nodefx.getGgbkzhanbimaxweek();
			Integer cjeminweek = this.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(requireddate,0);

			Double curcjl = this.getChengJiaoLiang(requireddate, 0);
	    	String cjldanwei = FormatDoubleToShort.getNumberChineseDanWei(curcjl);
	    	curcjl = FormatDoubleToShort.formateDoubleToShort (curcjl);
	    		
	    	Integer cjlmaxwk = null;
	    	try{
	    		cjlmaxwk = this.getChenJiaoLiangMaxWeekOfSuperBanKuai(requireddate,0);//显示cjl是多少周最大
	    	} catch (java.lang.NullPointerException e) {
	    		
	    	}
	    	Integer cjlminwk = null;
	    	try{
	    		cjlminwk = this.getChenJiaoLiangMaxWeekOfSuperBanKuai(requireddate,0);//显示cjl是多少周最大
	    	} catch (java.lang.NullPointerException e) {
	    		
	    	}
	    	Double cjlzhanbidata = this.getChenJiaoLiangZhanBi(requireddate, 0);
			Double cjlchangerate = this.getChenJiaoLiangChangeGrowthRateOfSuperBanKuai(superbk,requireddate,0);//cjl大盘变化贡献率
			
			Integer cjlzbmaxwk = this.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(requireddate, 0);
			Integer cjlzbminwk = this.getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(requireddate, 0);
			
			DecimalFormat decimalformate = new DecimalFormat("#0.000"); //",###";
	    	NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
	    	percentFormat.setMinimumFractionDigits(4);
				
			String htmlstring = "";
			org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
			Elements body = doc.getElementsByTag("body");
			for(Element elbody : body) {
				org.jsoup.nodes.Element dl = elbody.appendElement("dl");
				 
				 org.jsoup.nodes.Element lidate = dl.appendElement("li");
				 lidate.appendText(requireddate.toString());
				
				 org.jsoup.nodes.Element licje = dl.appendElement("li");
				 licje.appendText("成交额" + decimalformate.format(curcje) + cjedanwei);
				 
				 if(cjemaxwk>0) {
					 org.jsoup.nodes.Element licjemaxwk = dl.appendElement("li");
					 licjemaxwk.appendText("成交额MaxWk=" + cjemaxwk);
				 } else {
					 org.jsoup.nodes.Element licjeminwk = dl.appendElement("li");
					 licjeminwk.appendText("成交额MinWk=" + (0-cjeminwk) );
				 }
				 
				 try{
					 htmlstring = "成交额大盘变化贡献率" + percentFormat.format (cjechangerate) ;
					 
					 org.jsoup.nodes.Element licjechangerate = dl.appendElement("li");
					 licjechangerate.appendText(htmlstring);
				 } catch (java.lang.IllegalArgumentException e) {
//					 li4.appendText("成交额大盘变化贡献率NULL" );
				 }

				try {
					DecimalFormat decimalformate2 = new DecimalFormat("%#0.00000");
					org.jsoup.nodes.Element licjezb = dl.appendElement("li");
					licjezb.appendText( "成交额占比" + decimalformate2.format(curcjezhanbidata)  );
				} catch (java.lang.IllegalArgumentException e ) {
//					htmltext = "占比占比NULL" ;
				}
				
				if(cjemaxweek > 0) {
					try {
						 org.jsoup.nodes.Element licjemaxwk = dl.appendElement("li");
						 licjemaxwk.appendText( "CJE占比MaxWk=" + cjemaxweek.toString()  );
					} catch (java.lang.IllegalArgumentException e ) {
//						htmltext = "占比MaxWk=NULL"  ;
					}
				} else {
					try {
						 org.jsoup.nodes.Element licjeminwk = dl.appendElement("li");
						 licjeminwk.appendText( "CJE占比MinWk=" + cjeminweek.toString()  );
					} catch (java.lang.IllegalArgumentException e ) {
//						htmltext = "占比MaxWk=NULL"  ;
					}
				}
				 
				 org.jsoup.nodes.Element licjl = dl.appendElement("li");
				 licjl.appendText("成交量" + decimalformate.format(curcjl) + cjldanwei);
				 
				 if(cjlmaxwk>0) {
					 org.jsoup.nodes.Element licjlmaxwk = dl.appendElement("li");
					 licjlmaxwk.appendText("成交量MaxWk=" + cjlmaxwk);
				 } else {
					 org.jsoup.nodes.Element licjlminwk = dl.appendElement("li");
					 licjlminwk.appendText("成交量MinWk=" + (0-cjlminwk) );
				 }
				 
				 try{
					 org.jsoup.nodes.Element licjlchangerate = dl.appendElement("li");
					 licjlchangerate.appendText( "成交量大盘变化贡献率" + percentFormat.format (cjlchangerate)  );
				 } catch (java.lang.IllegalArgumentException e) {
//					 li4.appendText("成交额大盘变化贡献率NULL" );
				 }

				try {
					DecimalFormat decimalformate2 = new DecimalFormat("%#0.00000");
					org.jsoup.nodes.Element licjlzb = dl.appendElement("li");
					licjlzb.appendText( "成交量占比" + decimalformate2.format(cjlzhanbidata)  );
				} catch (java.lang.IllegalArgumentException e ) {
//					htmltext = "占比占比NULL" ;
				}
				
				if(cjlzbmaxwk > 0) {
					try {
						 org.jsoup.nodes.Element licjlmaxwk = dl.appendElement("li");
						 licjlmaxwk.appendText( "CJL占比MaxWk=" + cjlzbmaxwk.toString()  );
					} catch (java.lang.IllegalArgumentException e ) {
//						htmltext = "占比MaxWk=NULL"  ;
					}
				} else {
					try {
						 org.jsoup.nodes.Element licjlminwk = dl.appendElement("li");
						 licjlminwk.appendText( "CJL占比MinWk=" + cjlzbminwk.toString()  );
					} catch (java.lang.IllegalArgumentException e ) {
//						htmltext = "占比MaxWk=NULL"  ;
					}
				}

	 
					 Integer opneupquekou = this.getQueKouTongJiOpenUp(requireddate, 0);
					 if( opneupquekou != null) {
						 org.jsoup.nodes.Element li6 = dl.appendElement("li");
						 li6.appendText("缺口OpenUp =" + opneupquekou);
					 }
					 Integer opendownquekou = this.getQueKouTongJiOpenDown(requireddate, 0);
					 if( opendownquekou != null) {
						 org.jsoup.nodes.Element li7 = dl.appendElement("li");
						 li7.appendText("缺口OpenDown =" + opendownquekou);
					 }
					 Integer huibuupquekou = this.getQueKouTongJiHuiBuUp(requireddate, 0);
					 if( huibuupquekou != null) {
						 org.jsoup.nodes.Element li8 = dl.appendElement("li");
						 li8.appendText("缺口HuiBuUp =" + huibuupquekou );
					 }
					 Integer huibudowquekou = this.getQueKouTongJiHuiBuDown(requireddate, 0);
					 if( huibudowquekou != null) {
						 org.jsoup.nodes.Element li9 = dl.appendElement("li");
						 li9.appendText("缺口HuiBuDown =" + huibudowquekou );
					 }
				 
				
			}
			
			return doc.toString();
		}
		
		

	 	 
 } //END OF 
