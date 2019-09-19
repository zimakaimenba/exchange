package com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForTA4J;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseTimeSeries;
//import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.FormatDoubleToShort;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.TDXNodesXPeriodExternalData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItemForTA4J;
import com.udojava.evalex.Expression;


public abstract class TDXNodesXPeriodDataForTA4J extends TDXNodesXPeriodExternalData
{
	private Logger logger = Logger.getLogger(TDXNodesXPeriodDataForTA4J.class);
	
	public TDXNodesXPeriodDataForTA4J (String nodecode,String nodeperiodtype1)
	{
		super (nodecode,nodeperiodtype1);
		
		ohlcvaseries = new BaseTimeSeries.SeriesBuilder().withName(nodecode + nodeperiodtype1).build();
	}
	
	private org.ta4j.core.TimeSeries ohlcvaseries;
	
//	protected TimeSeries nodeamozhanbi; 
//	protected TimeSeries nodevolzhanbi; 
//	protected TimeSeries nodefxjg; //
//	protected TimeSeries nodeexchangedaysnumber ; //
//	 // ȱ�ڵ�ͳ�ƽ������ֱ�Ӵ��ȱ�ڵ�ԭ�����Ѿ���ohlc�ˣ����Լ����ȱ�ڣ������ר�Ŵ��ȱ��list�е��ظ���������ȱ��ͳ������
//	protected TimeSeries nodeqktjopenup;
//	protected TimeSeries nodeqktjhuibuup;
//	protected TimeSeries nodeqktjopendown;
//	protected TimeSeries nodeqktjhuibudown;
//	protected List<QueKou> qklist;
//	
//	protected TimeSeries nodezhangtingnum;
//	protected TimeSeries nodedietingnum;
//	
//	private LocalDate firstdayinhistory; //����֪����ʷ��¼���������
	
	
	public void addNewXPeriodData (NodeGivenPeriodDataItem kdata)
	{
		ohlcvaseries.addBar( (NodeGivenPeriodDataItemForTA4J)kdata,false);
		
		super.addNewXPeriodData(kdata);
		
//		RegularTimePeriod period = kdata.getPeriod();
//		LocalDate recordday = kdata.getp.getRecordsDayofEndofWeek();
//		double close = kdata.getCloseValue();
//		double open = kdata.getOpenValue();
//		double high = kdata.getHighValue();
//		double low = kdata.getLowValue();
//		Double cje = kdata.getMyOwnChengJiaoEr();
//		Double cjl = kdata.getMyownchengjiaoliang ();
		
//		ZonedDateTime zdt = null;
//		if(this.nodeperiodtype.equals(NodeGivenPeriodDataItem.WEEK))
//			zdt = recordday.with(DayOfWeek.FRIDAY).atStartOfDay(ZoneOffset.UTC);
//		else if(this.nodeperiodtype.equals(NodeGivenPeriodDataItem.DAY))
//			zdt = recordday.atStartOfDay(ZoneOffset.UTC);
//		ohlcvaseries.addBar(zdt,open,high,low,close,cjl,cje);
		
		
		
//		if(kdata.getNodeToDpChenJiaoErZhanbi() != null) {
//			nodeamozhanbi.setNotify(false);
//			nodeamozhanbi.add(kdata.getJFreeChartPeriod(this.nodeperiodtype),kdata.getNodeToDpChenJiaoErZhanbi(),false);
//			
//			nodevolzhanbi.setNotify(false);
//			nodevolzhanbi.add(kdata.getJFreeChartPeriod(this.nodeperiodtype),kdata.getNodeToDpChenJiaoLiangZhanbi(),false);
//		}
//		
//		try{
//			nodeexchangedaysnumber.setNotify(false);
//			if(kdata.getExchangeDaysNumber() != null && kdata.getExchangeDaysNumber() != 5) //
//				nodeexchangedaysnumber.add(kdata.getJFreeChartPeriod(this.nodeperiodtype),kdata.getExchangeDaysNumber(),false);
//			
//		} catch (org.jfree.data.general.SeriesException e) {
//			logger.debug(kdata.getMyOwnCode() + kdata.getJFreeChartPeriod(this.nodeperiodtype) + "�����Ѿ����ڣ�" + kdata.getJFreeChartPeriod(this.nodeperiodtype).getStart() + "," + kdata.getJFreeChartPeriod(this.nodeperiodtype).getEnd() + ")");
////			e.printStackTrace();
//		}
//		
//		try {
//			if(nodezhangtingnum == null)
//				nodezhangtingnum = new TimeSeries(this.nodeperiodtype);
//			if(nodedietingnum == null)
//				nodedietingnum = new TimeSeries(this.nodeperiodtype);
//			
//			if(kdata.getZhangTingNumber() != null && kdata.getZhangTingNumber() !=0)
//				nodezhangtingnum.add(kdata.getJFreeChartPeriod(this.nodeperiodtype), kdata.getZhangTingNumber());
//			
//			if(kdata.getDieTingNumber() != null && kdata.getDieTingNumber() !=0)
//				nodedietingnum.add(kdata.getJFreeChartPeriod(this.nodeperiodtype), kdata.getDieTingNumber());
//			
//		} catch (org.jfree.data.general.SeriesException e) {
//			logger.debug(kdata.getMyOwnCode() + kdata.getJFreeChartPeriod(this.nodeperiodtype) + "�����Ѿ����ڣ�" + kdata.getJFreeChartPeriod(this.nodeperiodtype).getStart() + "," + kdata.getJFreeChartPeriod(this.nodeperiodtype).getEnd() + ")");
////			e.printStackTrace();
//		}
	}
	/*
	 * 
	 */
	public LocalDate getOhlcRecordsStartDate ()
	{
		LocalDate begin = this.ohlcvaseries.getBar(0).getBeginTime().toLocalDate();
		return begin;
	}
	public LocalDate getOhlcRecordsEndDate ()
	{
		int length = this.ohlcvaseries.getBarCount();
		LocalDate end = this.ohlcvaseries.getBar(length -1).getBeginTime().toLocalDate();
		return end;
	}
	/*
	 * �ж������Ƿ������ݿ���ʷ��¼�ĵ�һ��
	 */
//	public Boolean isLocalDateReachFristDayInHistory (LocalDate requireddate,int difference)
//	{
//		Boolean result = externaldata.isLocalDateReachFristDayInHistory(requireddate, difference);
//		return result;
//		if(this.firstdayinhistory == null)
//			return null;
//		
//		if(this.nodeperiodtype.equals(NodeGivenPeriodDataItem.DAY) ) {
//			if(requireddate.equals(this.firstdayinhistory))
//				return true;
//			else
//				return false;
//		} else if(this.nodeperiodtype.equals(NodeGivenPeriodDataItem.WEEK) ) {
//			Boolean checkresult = CommonUtility.isInSameWeek(requireddate, this.firstdayinhistory);
//			return checkresult;
//		}
//		
//		return null;
//	}
//	public void setShangShiRiQi (LocalDate requireddate)
//	{
//		this.firstdayinhistory = requireddate;
//	}
	/*
	 * 
	 */
//	public String getNodeCode ()
//	{
//		return this.nodecode;
//	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#getNodeperiodtype()
	 */
//	public String getNodeperiodtype() {
//		return nodeperiodtype;
//	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#hasFxjgInPeriod(java.time.LocalDate, int)
	 */
//	public Boolean hasFxjgInPeriod (LocalDate requireddate,int difference)
//	{
//		if(nodefxjg == null)
//			return false;
//		
//		TimeSeriesDataItem fxjgitem = nodefxjg.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//		if(fxjgitem == null)
//			return false;
//		else
//			 return true;
//	}
	/*
	 * 
	 */
//	public void addFxjgToPeriod (RegularTimePeriod period,Integer fxjg)
//	{
//		if(nodefxjg == null)
//			nodefxjg = new TimeSeries(this.nodeperiodtype);
//		
//		try {
//			nodefxjg.add(period,fxjg);
////			 addOrUpdate()
//		} catch (org.jfree.data.general.SeriesException e) {
////			e.printStackTrace();
//		}
//	}
	/*
	 * 
	 */
//	public void resetQueKouTongJiJieGuo ()
//	{
//		if(nodeqktjopenup != null)
//			nodeqktjopenup.clear();
//		if(nodeqktjhuibuup != null)
//			nodeqktjhuibuup.clear();
//		if(nodeqktjopendown != null)
//			nodeqktjopendown.clear();
//		if(nodeqktjhuibudown != null)
//			nodeqktjhuibudown.clear();
//	}
	/*
	 * 
	 */
//	public void addZhangDieTingTongJiJieGuo (LocalDate tjdate,Integer nodeztnum,Integer nodedtnum,Boolean addbasedoncurdata)
//	{
//		if( this.nodezhangtingnum == null)
//			this.nodezhangtingnum = new TimeSeries(this.nodeperiodtype);
//		
//		if( this.nodedietingnum == null)
//			this.nodedietingnum = new TimeSeries(this.nodeperiodtype);
//		
//		RegularTimePeriod period = getJFreeChartFormateTimePeriod(tjdate,0);
//		try {
//			if(nodeztnum != null  )
//			if(addbasedoncurdata) {
//				TimeSeriesDataItem dataitem = this.nodezhangtingnum.getDataItem(period);
//				if(dataitem == null) 
//					nodezhangtingnum.add(period,nodeztnum);
//				else {
//					int count = dataitem.getValue().intValue();
//					nodezhangtingnum.addOrUpdate(period, count + nodeztnum);
//				}
//			} else
//				nodezhangtingnum.addOrUpdate(period,nodeztnum);
//
//		} catch (org.jfree.data.general.SeriesException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			if(nodedtnum != null  )
//			if(addbasedoncurdata) {
//				TimeSeriesDataItem dataitem = this.nodedietingnum.getDataItem(period);
//				if(dataitem == null) 
//					nodedietingnum.add(period,nodedtnum);
//				else {
//					int count = dataitem.getValue().intValue();
//					nodedietingnum.addOrUpdate(period, count + nodedtnum);
//				}
//			} else
//				nodedietingnum.addOrUpdate(period,nodedtnum);
//
//		} catch (org.jfree.data.general.SeriesException e) {
//			e.printStackTrace();
//		}
//	}
	/*
	 * ����addbasedoncurdata��true�����е�ֵ�ϸ��£�false��������ֵ
	 */
//	public void addQueKouTongJiJieGuo (LocalDate qkdate,
//			Integer nodeqktjopenup1,Integer nodeqktjhuibuup1,Integer nodeqktjopendown1,Integer nodeqktjhuibudown1,
//			Boolean addbasedoncurdata)
//	{
//		
//		if( nodeqktjopenup == null)
//			nodeqktjopenup = new TimeSeries(this.nodeperiodtype);
//		
//		if( nodeqktjhuibuup == null)
//			nodeqktjhuibuup = new TimeSeries(this.nodeperiodtype);
//		
//		if( nodeqktjopendown == null)
//			nodeqktjopendown = new TimeSeries(this.nodeperiodtype);
//		
//		if( nodeqktjhuibudown == null)
//			nodeqktjhuibudown = new TimeSeries(this.nodeperiodtype);
//		
//		RegularTimePeriod period = getJFreeChartFormateTimePeriod(qkdate,0);
//		try {
//			if(nodeqktjopenup1 != null  ) //nodeqktjopenup1���Ե���0���������ͳ��ȱ�ڵĿ�ʼ/����ʱ��
//			if(addbasedoncurdata) {
//				TimeSeriesDataItem dataitem = nodeqktjopenup.getDataItem(period);
//				if(dataitem == null) 
//					nodeqktjopenup.add(period,nodeqktjopenup1);
//				else {
//					int count = dataitem.getValue().intValue();
//					nodeqktjopenup.addOrUpdate(period, count + nodeqktjopenup1);
//				}
//			} else
//				nodeqktjopenup.addOrUpdate(period,nodeqktjopenup1);
//
//		} catch (org.jfree.data.general.SeriesException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			if(nodeqktjhuibuup1 != null && nodeqktjhuibuup1 !=0 )
//			if(addbasedoncurdata) {
//				TimeSeriesDataItem dataitem = nodeqktjhuibuup.getDataItem(period);
//				if(dataitem == null)
//					nodeqktjhuibuup.add(period,nodeqktjhuibuup1);
//				else {
//					int count = dataitem.getValue().intValue();
//					nodeqktjhuibuup.addOrUpdate(period, count + nodeqktjhuibuup1);
//				}
//					
//			} else
//				nodeqktjhuibuup.addOrUpdate(period,nodeqktjhuibuup1);
//		} catch (org.jfree.data.general.SeriesException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			if(nodeqktjopendown1 != null && nodeqktjopendown1 !=0 )
//			if(addbasedoncurdata) {
//				TimeSeriesDataItem dataitem = nodeqktjopendown.getDataItem(period);
//				if(dataitem == null)
//					nodeqktjopendown.add(period,nodeqktjopendown1);
//				else {
//					int count = dataitem.getValue().intValue();
//					nodeqktjopendown.addOrUpdate(period, count + nodeqktjopendown1);
//				}
//					
//			} else
//				nodeqktjopendown.addOrUpdate(period,nodeqktjopendown1);
//
//		} catch (org.jfree.data.general.SeriesException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			if(nodeqktjhuibudown1 != null && nodeqktjhuibudown1 !=0 )
//			if(addbasedoncurdata) {
//				TimeSeriesDataItem dataitem = nodeqktjhuibudown.getDataItem(period);
//				if(dataitem == null)
//					nodeqktjhuibudown.add(period,nodeqktjhuibudown1);
//				else {
//					int count = dataitem.getValue().intValue();
//					nodeqktjhuibudown.addOrUpdate(period, count + 1);
//				}
//					
//			} else
//				nodeqktjhuibudown.addOrUpdate(period,nodeqktjhuibudown1);
//
//		} catch (org.jfree.data.general.SeriesException e) {
//			e.printStackTrace();
//		}
//	}
//	public List<QueKou> getPeriodQueKou ()
//	{
//		return this.qklist;
//	}
//	public void setPeriodQueKou (List<QueKou> qkl)
//	{
//		this.qklist = qkl;
//	}
	/*
	 * 
	 */
//	public Integer getZhangTingTongJi (LocalDate requireddate, Integer difference)
//	{
//		if(this.nodezhangtingnum == null)
//			return null;
//		
//		TimeSeriesDataItem zttjitem = nodezhangtingnum.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//		if(zttjitem == null)
//			return null;
//		else
//			 return (Integer)zttjitem.getValue().intValue();			
//					
//	}
//	public Integer getDieTingTongJi (LocalDate requireddate, Integer difference)
//	{
//		if(this.nodedietingnum == null)
//			return null;
//		
//		TimeSeriesDataItem dttjitem = nodedietingnum.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//		if(dttjitem == null)
//			return null;
//		else
//			 return (Integer)dttjitem.getValue().intValue();			
//					
//	}
	/*
	 * 
	 */
//	public Integer getQueKouTongJiOpenUp (LocalDate requireddate, Integer difference)
//	{
//		if(this.nodeqktjopenup == null)
//			return null;
//		
//		TimeSeriesDataItem qktjitem = nodeqktjopenup.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//		if(qktjitem == null)
//			return null;
//		else
//			 return (Integer)qktjitem.getValue().intValue(); 
//		
//	}
//	public Integer getQueKouTongJiOpenDown (LocalDate requireddate, Integer difference)
//	{
//		if(this.nodeqktjopendown == null)
//			return null;
//		
//		TimeSeriesDataItem qktjitem = nodeqktjopendown.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//		if(qktjitem == null)
//			return null;
//		else
//			 return (Integer)qktjitem.getValue().intValue(); 
//		
//	}
//	public Integer getQueKouTongJiHuiBuUp (LocalDate requireddate, Integer difference)
//	{
//		if(this.nodeqktjhuibuup == null)
//			return null;
//		
//		TimeSeriesDataItem qktjitem = nodeqktjhuibuup.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//		if(qktjitem == null)
//			return null;
//		else
//			 return (Integer)qktjitem.getValue().intValue(); 
//		
//	}
//	public Integer getQueKouTongJiHuiBuDown (LocalDate requireddate, Integer difference)
//	{
//		if(this.nodeqktjhuibudown == null)
//			return null;
//		
//		TimeSeriesDataItem qktjitem = nodeqktjhuibudown.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//		if(qktjitem == null)
//			return null;
//		else
//			 return qktjitem.getValue().intValue(); 
//	}
	/*
	 * 
	 */
	public  org.ta4j.core.TimeSeries getOHLCData ()
	{
		return this.ohlcvaseries;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#getOHLCData(java.time.LocalDate, int)
	 */
	public Bar getSpecificDateOHLCData (LocalDate requireddate,int difference)
	{
		int barcount = this.ohlcvaseries.getBarCount();
		if(barcount == -1)
			return null;
		
		if(super.getNodeperiodtype().equals(NodeGivenPeriodDataItem.WEEK )) {
			requireddate = requireddate.with(DayOfWeek.FRIDAY);
		}
		
		for(int i= 0;i<= barcount-1;i++) {
			Bar dataitemp = this.ohlcvaseries.getBar(i);
			ZonedDateTime time = dataitemp.getEndTime();
			LocalDate ldtime = time.toLocalDate();
			
			
			if(ldtime.equals( requireddate) )
				return dataitemp;
		}
		
		return null;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic#getSpecificDateOHLCData(java.time.LocalDate, int)
	 */
	public Integer getIndexOfSpecificDateOHLCData (LocalDate requireddate,int difference)
	{
		int barcount = this.ohlcvaseries.getBarCount();
		if(barcount == -1)
			return null;
		
		if(super.getNodeperiodtype().equals(NodeGivenPeriodDataItem.WEEK )) {
			requireddate = requireddate.with(DayOfWeek.FRIDAY);
		}
		
		for(int i= 0;i<= barcount-1;i++) {
			Bar dataitemp = this.ohlcvaseries.getBar(i);
			ZonedDateTime time = dataitemp.getEndTime();
			LocalDate ldtime = time.toLocalDate();
			
			
			if(ldtime.equals( requireddate) )
				return i;
		}
		
		return null;
	}
	/*
	 * ĳ����ǵ���
	 */
	public Double getSpecificOHLCZhangDieFu (LocalDate requireddate,int difference)
	{
		Integer indexofcur = this.getIndexOfSpecificDateOHLCData(requireddate, 0);
		Bar curohlc =  this.getOHLCData().getBar(indexofcur.intValue());
		double curclose = curohlc.getClosePrice().doubleValue();
		
		Bar lastholc =  this.getOHLCData().getBar(indexofcur.intValue() -1 );
		double lastclose = lastholc.getClosePrice().doubleValue();
		double zhangfu = (curclose - lastclose) / lastclose;
		
		return zhangfu;
		
	}
	/*
	 * 
	 */
	public Double getChengJiaoEr (LocalDate requireddate,int difference)
	{
		
		Bar curohlcvadata = this.getSpecificDateOHLCData (requireddate,difference);
		if(curohlcvadata != null) {
			double amo = curohlcvadata.getAmount().doubleValue();
			return amo;
		}
		
		return null;
	}
	/*
	 * 
	 */
//	public  LocalDate  getQueKouRecordsStartDate ()
//	{ 
//		if(nodeqktjopenup == null)
//			return null;
//		
//		if(nodeqktjopenup.getItemCount() == 0)
//			return null;
//
//		RegularTimePeriod firstperiod = nodeqktjopenup.getTimePeriod( 0);
//		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
//		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
//	
//		if(this.nodeperiodtype == NodeGivenPeriodDataItem.WEEK) {
//			TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
//			LocalDate mondayday = startdate.with(fieldUS, 2);
//			return mondayday;
//		} else if(this.nodeperiodtype == NodeGivenPeriodDataItem.DAY) {
//			return startdate;
//		}
//		
//		return null;
//	}
	/*
	 * 
	 */
//	public  LocalDate  getQueKouRecordsEndDate ()
//	{ 
//		if(nodeqktjopenup == null || nodeqktjopenup.getItemCount() == 0)
//			return null;
//
//		int itemcount = nodeqktjopenup.getItemCount();
//		RegularTimePeriod firstperiod = nodeqktjopenup.getTimePeriod( itemcount - 1 );
//		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
//		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
//	
//		if(this.nodeperiodtype == NodeGivenPeriodDataItem.WEEK) {
//			LocalDate saturday = enddate.with(DayOfWeek.SATURDAY);
//			return saturday;
//		} else if(this.nodeperiodtype == NodeGivenPeriodDataItem.DAY) {
//			return enddate;
//		}
//		
//		return null;
//	}
	/*
	 * 
	 */
	public LocalDate getOHLCRecordsStartDate ()
	{
		int itemcount = this.ohlcvaseries.getBarCount();
		if( itemcount == 0)
			return null;
		
		
		Bar firstperiod = this.ohlcvaseries.getBar(0);
		LocalDate startdate = firstperiod.getBeginTime().toLocalDate();
	
		if(super.getNodeperiodtype() == NodeGivenPeriodDataItem.WEEK) {
			TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
			LocalDate mondayday = startdate.with(fieldUS, 2);
			return mondayday;
		} else if(super.getNodeperiodtype () == NodeGivenPeriodDataItem.DAY) {
			return startdate;
		}
		
		return null;
	}
	public LocalDate getOHLCRecordsEndDate ()
	{
		int itemcount = this.ohlcvaseries.getBarCount();
		if( itemcount == 0)
			return null;
		
		
		Bar endperiod = this.ohlcvaseries.getBar(itemcount -1);
		LocalDate enddate = endperiod.getBeginTime().toLocalDate();
		
		if(super.getNodeperiodtype() == NodeGivenPeriodDataItem.WEEK) {
			LocalDate saturday = enddate.with(DayOfWeek.SATURDAY);
			return saturday;
		} else if(super.getNodeperiodtype () == NodeGivenPeriodDataItem.DAY) {
			return enddate;
		}
		
		return null;
	}
	/*
	 *	  ����ָ�����ں������ڵĳɽ�����ʺ�stock/bankuai��dapan���Լ��ļ��㷽��
	 */
	public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate,int difference)
	{
		if(ohlcvaseries == null)
			return null;
		
		Bar curcjlrecord = this.getSpecificDateOHLCData(requireddate, difference);
		if( curcjlrecord == null) 
			return null;
		
		Integer curindex = this.getIndexOfSpecificDateOHLCData(requireddate, 0);
		
		
		try{
			Bar lastcjlrecord = this.ohlcvaseries.getBar(curindex - 1);
			if(lastcjlrecord == null) //����ǰ���ǿգ�˵��Ҫ���°�顣���û��ͣ�Ƶ�
				return null;
			
			Double curcje = curcjlrecord.getAmount().doubleValue();
			Double lastcje = lastcjlrecord.getAmount().doubleValue();
			
			return curcje - lastcje;
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			return 100.0;
		}

	}
	/*
	 * 
	 */
//	public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate,int difference) 
//	{
//		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//		if( curcjlrecord == null) 
//			return null;
//		
//		int index = this.nodeamozhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference));
//		
//		try {
//			TimeSeriesDataItem lastcjlrecord = nodeamozhanbi.getDataItem( index -1  );
//			if(lastcjlrecord == null) { //����ǰ���ǿգ�˵��Ҫ���°�顣���û��ͣ�Ƶ�
//				logger.debug(getNodeCode() + "������һ���¸��ɻ���");
//				return 100.0;
//			}
//			
//			Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
//			Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
//			Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
//			logger.debug(getNodeCode() + "ռ������" + requireddate.toString() + zhanbigrowthrate);
//			
//			return zhanbigrowthrate;
//			
//		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
//			return 100.0;
//		}
//
//	}
	/*
	 * 
	 */
	public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference) 
	{
		Bar curcjlrecord = this.getSpecificDateOHLCData(requireddate, 0);
		if( curcjlrecord == null) 
			return null;
		
		Double curcje = curcjlrecord.getAmount().doubleValue();
		int itemcount = this.ohlcvaseries.getBarCount();
		int maxweek = 0;
		
		int index = this.getIndexOfSpecificDateOHLCData(requireddate, 0);
		
		for(int i = index-1;i >=0; i--) {
			
			 Bar lastcjlrecord = this.ohlcvaseries.getBar(i);
			if(lastcjlrecord == null ) //���ܵ��˼�¼��ͷ���ˣ������Ǹ�����ʱ�䲻���İ��
				return maxweek;
			
			Double lastcje = lastcjlrecord.getAmount().doubleValue();
			if(curcje > lastcje)
				maxweek ++;
			else
				break;
		}

		return maxweek;
	}
	/*
	 * ���ϼ����ĳɽ���ռ���Ƕ������ڵ����ֵ
	 */
//	public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference) 
//	{
//		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//		if( curcjlrecord == null) 
//			return null;
//					
//		Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
//		
//		int index = nodeamozhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
//		
//		int maxweek = 0;
//		for(int i = index - 1; i >= 0; i--) {
//			
//			
//			TimeSeriesDataItem lastcjlrecord = nodeamozhanbi.getDataItem( i );
//			if(lastcjlrecord == null ) //���ܵ��˼�¼��ͷ���ˣ������Ǹ�����ʱ�䲻���İ��
//				return maxweek;
//
//			Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
//			if(curzhanbiratio > lastzhanbiratio)
//				maxweek ++;
//			else
//				break;
//		}
//		
//		return maxweek;
//	}
	/*
	 * 
	 */
//	public Integer getChenJiaoErZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate,int difference)
//	{
//		if(this.ohlcvaseries == null)
//			return null;
//		
//		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//		if( curcjlrecord == null) 
//			return null;
//
//		Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
//		
//		int index = nodeamozhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
//		int minweek = 0;
//		for(int i = index -1; i >= 0; i --) {
//			TimeSeriesDataItem lastcjlrecord = nodeamozhanbi.getDataItem( i );
//			if(lastcjlrecord == null ) //���ܵ��˼�¼��ͷ���ˣ������Ǹ�����ʱ�䲻���İ��
//				return minweek;
//
//			Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
//			if(curzhanbiratio < lastzhanbiratio)
//				minweek ++;
//			else
//				break;
//		}
//		
//		return minweek;
//	}
	/*
	 * ����ɽ���仯�����ʣ������ɽ���ı仯ռ�����ϼ����ɽ����������ı��ʣ�
	 */
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai (TDXNodes superbk, LocalDate requireddate,int difference) 
	{
		Bar curcjlrecord = this.getSpecificDateOHLCData(requireddate, 0);
		if( curcjlrecord == null) 
			return null;
		
		//�ж��ϼ����(���̻��߰��)�Ƿ�����,������û�бȽϵ����壬ֱ�ӷ���-100��
		String nodept = getNodeperiodtype();
		NodeXPeriodData bkxdata = superbk.getNodeXPeroidData(nodept);
		Double bkcjediff = bkxdata.getChengJiaoErDifferenceWithLastPeriod(requireddate,difference);
		if( bkcjediff == null || bkcjediff < 0   ) {//���������
			return -100.0;
		}
		
		Bar lastcjlrecord = null;
		int index = this.getIndexOfSpecificDateOHLCData(requireddate, 0);
		try{
			lastcjlrecord = this.ohlcvaseries.getBar( index - 1);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			logger.debug("index = 0���������¹ɵ�һ�ܣ����������ݼ�¼�����¼�ܣ��޷��ж�");
			e.printStackTrace();
		}
		if(lastcjlrecord == null) { //˵����ͣ�ƺ����ˣ������¹�
			try {
			Double curggcje = curcjlrecord.getAmount().doubleValue(); //�°�����гɽ�����Ӧ�ü�����
			return curggcje/bkcjediff;
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		
		Double curcje = curcjlrecord.getAmount().doubleValue();
		Double lastcje = lastcjlrecord.getAmount().doubleValue();
		Double cjechange = curcje - lastcje; //���ɳɽ����ı仯
		
		return cjechange/bkcjediff;
	}
	/*
	  * ����ָ�����м���������
	  */
//	 public Integer getExchangeDaysNumberForthePeriod (LocalDate requireddate,int difference)
//	 {
//		 if(nodeexchangedaysnumber == null)
//			 return null;
//		 
//		 TimeSeriesDataItem curdaynumberrecord = this.nodeexchangedaysnumber.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//		 if( curdaynumberrecord == null) 
//				return 5;
//		 else
//			 return curdaynumberrecord.getValue().intValue();
//		 
//	 }
	 /*
	  * (non-Javadoc)
	  * @see com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic#getLianXuFangLiangPeriodNumber(java.time.LocalDate, int, int)
	  * ����������ָ��������������
	  */
	 public Integer getCjeLianXuFangLiangPeriodNumber (LocalDate requireddate,int difference,int settindpgmaxwk)
	 {
		 	
		 	int recordnum = this.ohlcvaseries.getBarCount();
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
	public Double[] getNodeOhlcSMA (LocalDate  requireddate,int difference)
	{
		requireddate = this.adjustDate(requireddate, difference); //��ȷ���������ڽ�������
		int beginin = ohlcvaseries.getBeginIndex();
		int endin = ohlcvaseries.getEndIndex();
		Integer location = null;
		for(int i = beginin;i<endin;i++) {
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
		
		Double sma[] = {ma5,ma10,ma20,ma30,ma60,ma120,ma250};
		return sma;
	}
	 /*
	  * (non-Javadoc)
	  * @see com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic#checkCloseComparingToMAFormula(java.lang.String, java.time.LocalDate, int)
	  */
	 public Boolean checkCloseComparingToMAFormula (String maformula, LocalDate requireddate, int difference)
	 {
		 LocalDate tmpdate = requireddate.with(DayOfWeek.FRIDAY);
		    
		 Bar ohlcdata;
		 do { //�������ݱȽϸ��� ���û�ѡ������ӿ��ܲ��ǽ����գ�������ͣ���գ���ֱ�������壬�������û�����ݣ���ǰ1�죬ֱ��������Ϊֹ���ܽ���
		    	ohlcdata = this.getSpecificDateOHLCData(tmpdate, 0);
		    	if(ohlcdata != null)  //û��ͣ��
		    		break;
		    	
		    	tmpdate = tmpdate.minus(1,ChronoUnit.DAYS);
		 } while (CommonUtility.isInSameWeek(requireddate, tmpdate) );
		    
		 if (ohlcdata != null) {
		    	Double close = (Double)ohlcdata.getClosePrice().doubleValue();
			    Double[] maresult = this.getNodeOhlcSMA(tmpdate, 0);
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
		    	BigDecimal result1 = new Expression(maformula).with("x",String.valueOf(close)).eval(); //https://github.com/uklimaschewski/EvalEx
		    	String sesultstr = result1.toString();
			    if(sesultstr.equals("0"))
			    	return false;
			    else 
			    	return true;
		    } catch (com.udojava.evalex.Expression.ExpressionException e) {
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
	public Integer getCjlLianXuFangLiangPeriodNumber(LocalDate requireddate, int difference, int settindpgmaxwk) 
	{
			int recordnum = this.ohlcvaseries.getBarCount();
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
	/*
	 * 
	 */
	public Double getChenJiaoLiangDifferenceWithLastPeriod(LocalDate requireddate, int difference) 
	{
		if(ohlcvaseries == null)
			return null;
		
		Bar curcjlrecord = this.getSpecificDateOHLCData(requireddate, 0);
		if( curcjlrecord == null) 
			return null;
		
		int index = this.getIndexOfSpecificDateOHLCData(requireddate,difference);
		try{
			Bar lastcjlrecord = ohlcvaseries.getBar( index -1 );
			if(lastcjlrecord == null) //����ǰ���ǿգ�˵��Ҫ���°�顣���û��ͣ�Ƶ�
				return null;
			
			Double curcje = curcjlrecord.getVolume().doubleValue();
			Double lastcje = lastcjlrecord.getVolume().doubleValue();
			
			return curcje - lastcje;
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			return 100.0;
		}
	}
	/*
	 * 
	 */
//	public Double getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate, int difference) 
//	{
//		TimeSeriesDataItem curcjlrecord = this.nodevolzhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//		if( curcjlrecord == null) 
//			return null;
//		
//		int index = this.getIndexOfSpecificDateOHLCData(requireddate, difference);
//		
//		try {
//			TimeSeriesDataItem lastcjlrecord = nodevolzhanbi.getDataItem( index -1  );
//			if(lastcjlrecord == null) { //����ǰ���ǿգ�˵��Ҫ���°�顣���û��ͣ�Ƶ�
//				logger.debug(getNodeCode() + "������һ���¸��ɻ���");
//				return 100.0;
//			}
//			
//			Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
//			Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
//			Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
//			logger.debug(getNodeCode() + "ռ������" + requireddate.toString() + zhanbigrowthrate);
//			
//			return zhanbigrowthrate;
//			
//		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
//			return 100.0;
//		}
//
//	}
	/*
	 * 
	 */
//	public Integer getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) 
//	{
//		TimeSeriesDataItem curcjlrecord = this.nodevolzhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//		if( curcjlrecord == null) 
//			return null;
//					
//		Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
//		
//		int index = nodevolzhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
//		
//		int maxweek = 0;
//		for(int i = index - 1; i >= 0; i--) {
//			
//			
//			TimeSeriesDataItem lastcjlrecord = nodevolzhanbi.getDataItem( i );
//			if(lastcjlrecord == null ) //���ܵ��˼�¼��ͷ���ˣ������Ǹ�����ʱ�䲻���İ��
//				return maxweek;
//
//			Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
//			if(curzhanbiratio > lastzhanbiratio)
//				maxweek ++;
//			else
//				break;
//		}
//		
//		return maxweek;
//	}
	/*
	 * 
	 */
//	public Integer getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate, int difference) 
//	{
//		if(this.ohlcvaseries == null)
//			return null;
//		
//		TimeSeriesDataItem curcjlrecord = this.nodevolzhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//		if( curcjlrecord == null) 
//			return null;
//
//		Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
//		
//		int index = nodevolzhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
//		int minweek = 0;
//		for(int i = index -1; i >= 0; i --) {
//			TimeSeriesDataItem lastcjlrecord = nodevolzhanbi.getDataItem( i );
//			if(lastcjlrecord == null ) //���ܵ��˼�¼��ͷ���ˣ������Ǹ�����ʱ�䲻���İ��
//				return minweek;
//
//			Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
//			if(curzhanbiratio < lastzhanbiratio)
//				minweek ++;
//			else
//				break;
//		}
//		
//		return minweek;
//	}
	/*
	 * 
	 */
	public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuai(TDXNodes superbk, LocalDate requireddate,	int difference) 
	{
		Bar curcjlrecord = this.getSpecificDateOHLCData(requireddate,difference) ;
		if( curcjlrecord == null) 
			return null;
		
		//�ж��ϼ����(���̻��߰��)�Ƿ�����,������û�бȽϵ����壬ֱ�ӷ���-100��
		String nodept = getNodeperiodtype();
		NodeXPeriodData bkxdata = superbk.getNodeXPeroidData(nodept);
		Double bkcjediff = bkxdata.getChenJiaoLiangDifferenceWithLastPeriod(requireddate,difference);
								   
		if( bkcjediff == null || bkcjediff < 0   ) {//���������
			return -100.0;
		}
		
		Bar lastcjlrecord = null;
		int index = this.getIndexOfSpecificDateOHLCData(requireddate,difference) ;
		try{
			lastcjlrecord = this.ohlcvaseries.getBar( index - 1);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			logger.debug("index = 0���޷��ж�");
			return 100.0;
//			e.printStackTrace();
		}
		if(lastcjlrecord == null) { //����ǰ���ǿգ�˵����ͣ�ƺ�����
			try {
			Double curggcje = curcjlrecord.getVolume().doubleValue(); //�°�����гɽ�����Ӧ�ü�����
			return curggcje/bkcjediff;
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		
		Double curcje = curcjlrecord.getVolume().doubleValue();
		Double lastcje = lastcjlrecord.getVolume().doubleValue();
		Double cjechange = curcje - lastcje; //���ɳɽ����ı仯
		
		return cjechange/bkcjediff;
	}
	/*
	 * 
	 */
	public Integer getChenJiaoLiangMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) 
	{
		Bar curcjlrecord = this.getSpecificDateOHLCData(requireddate,difference) ;
		if( curcjlrecord == null) 
			return null;
		
		Double curcje = curcjlrecord.getVolume().doubleValue();
		
		int itemcount = this.ohlcvaseries.getBarCount();
		int maxweek = 0;
		
		int index = this.getIndexOfSpecificDateOHLCData(requireddate,difference) ;
		
		for(int i = index-1;i >=0; i--) {
			
			Bar lastcjlrecord = this.ohlcvaseries.getBar(i);
			if(lastcjlrecord == null ) //���ܵ��˼�¼��ͷ���ˣ������Ǹ�����ʱ�䲻���İ��
				return maxweek;
			
			Double lastcje = lastcjlrecord.getVolume().doubleValue();
			if(curcje > lastcje)
				maxweek ++;
			else
				break;
		}

		return maxweek;
	}
	/*
	 * 
	 */
	public Double getChengJiaoLiang(LocalDate requireddate, int difference) 
	{
		Bar curcjlrecord = null;
//		if(difference >=0 )
			curcjlrecord = this.getSpecificDateOHLCData(requireddate, difference);
		
		if( curcjlrecord == null) 
			return null;
		else
			return curcjlrecord.getVolume().doubleValue();
	}
	/*
	 * 
	 */
//	public Double getChenJiaoLiangZhanBi(LocalDate requireddate, int difference) 
//	{
//		if(this.ohlcvaseries == null)
//			return null;
//		
//		TimeSeriesDataItem curcjlrecord = null;
//		curcjlrecord = nodevolzhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//		
//		if( curcjlrecord == null) 
//			return null;
//		else
//			return curcjlrecord.getValue().doubleValue();
//	}
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
			 
			 Double curcje = this.getChengJiaoEr(requireddate, 0);
			 String cjedanwei = null;
			 try{
					cjedanwei = FormatDoubleToShort.getNumberChineseDanWei(curcje);
					curcje = FormatDoubleToShort.formateDoubleToShort (curcje);
			 } catch (java.lang.NullPointerException e) {
//					e.printStackTrace();
					logger.debug(super.getNodeCode() + "��" + requireddate.toString() + "û�����ݣ�����ͣ�ơ�");
					return "";
			 }
			 org.jsoup.nodes.Element licje = dl.appendElement("li");
			 org.jsoup.nodes.Element fontcje = licje.appendElement("font");
			 fontcje.appendText("�ɽ���" + decimalformate.format(curcje) + cjedanwei);
			 fontcje.attr("color", "#AF7AC5");
			 
			 Integer cjemaxwk = null;
		     try{
		    		cjemaxwk = this.getChenJiaoErMaxWeekOfSuperBanKuai(requireddate,0);//��ʾ�ɽ����Ƕ��������,�ɽ����������Сû�����壬��Ϊ������������ܳɽ������ǻ��С
		     } catch (java.lang.NullPointerException e) {
		    		
		     }
			 if(cjemaxwk>0) {
				 org.jsoup.nodes.Element licjemaxwk = dl.appendElement("li");
				 org.jsoup.nodes.Element fontcjemaxwk = licjemaxwk.appendElement("font");
				 fontcjemaxwk.appendText("�ɽ���MaxWk=" + cjemaxwk);
				 fontcjemaxwk.attr("color", "#AF7AC5 ");
			 } 
			 
			 try{
				 Double cjechangerate = this.getChenJiaoErChangeGrowthRateOfSuperBanKuai(superbk,requireddate,0);//�ɽ�����̱仯������
				 if(cjechangerate != -100.0) {
					 htmlstring = "�ɽ�����仯������" + percentFormat.format (cjechangerate) ;
					 org.jsoup.nodes.Element licjechangerate = dl.appendElement("li");
					 org.jsoup.nodes.Element fontcjechangerate = licjechangerate.appendElement("font");
					 fontcjechangerate.appendText(htmlstring);
					 fontcjechangerate.attr("color", "#AF7AC5 ");
				 }
			 } catch (java.lang.IllegalArgumentException e) {
//				 li4.appendText("�ɽ�����̱仯������NULL" );
			 }

			try {
				Double curcjezhanbidata = super.getChenJiaoErZhanBi(requireddate, 0);  //ռ��
				
				org.jsoup.nodes.Element licjezb = dl.appendElement("li");
				org.jsoup.nodes.Element fontcjezb = licjezb.appendElement("font");
				fontcjezb.appendText( "�ɽ���ռ��" + decimalformate2.format(curcjezhanbidata)  );
				fontcjezb.attr("color", "#17202A");
			} catch (java.lang.IllegalArgumentException e ) {
//				htmltext = "ռ��ռ��NULL" ;
			}
			
			Integer cjemaxweek = this.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(requireddate,0);//nodefx.getGgbkzhanbimaxweek();
			Integer cjeminweek = this.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(requireddate,0);
			if(cjemaxweek > 0) {
				try {
					 org.jsoup.nodes.Element licjemaxwk = dl.appendElement("li");
					 org.jsoup.nodes.Element fontcjemaxwk = licjemaxwk.appendElement("font");
					 fontcjemaxwk.appendText( "�ɽ���ռ��MaxWk=" + cjemaxweek.toString()  );
					 fontcjemaxwk.attr("color", "#17202A");
				} catch (java.lang.IllegalArgumentException e ) {
//					htmltext = "ռ��MaxWk=NULL"  ;
				}
			} else {
				try {
					 org.jsoup.nodes.Element licjeminwk = dl.appendElement("li");
					 org.jsoup.nodes.Element fontcjeminwk = licjeminwk.appendElement("font");
					 fontcjeminwk.appendText( "�ɽ���ռ��MinWk=" + cjeminweek.toString()  );
					 fontcjeminwk.attr("color", "#17202A");  
				} catch (java.lang.IllegalArgumentException e ) {
//					htmltext = "ռ��MaxWk=NULL"  ;
				}
			}
			
			Double cjezbgrowthrate = this.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(requireddate, 0);
			org.jsoup.nodes.Element licjezbgr = dl.appendElement("li");
			org.jsoup.nodes.Element fontcjezbgr = licjezbgr.appendElement("font");
			fontcjezbgr.appendText( "�ɽ���ռ��������=" + decimalformate2.format(cjezbgrowthrate)  );
			fontcjezbgr.attr("color", "#17202A");
			
			 //
			 Double curcjl = this.getChengJiaoLiang(requireddate, 0);
	    	 String cjldanwei = FormatDoubleToShort.getNumberChineseDanWei(curcjl);
	    	 curcjl = FormatDoubleToShort.formateDoubleToShort (curcjl);
			 org.jsoup.nodes.Element licjl = dl.appendElement("li");
			 org.jsoup.nodes.Element fontcjl = licjl.appendElement("font");
			 fontcjl.appendText("�ɽ���" + decimalformate.format(curcjl) + cjldanwei);
			 fontcjl.attr("color", "#641E16");
			 //
			 Integer cjlmaxwk = null;
		     try{
		    		cjlmaxwk = this.getChenJiaoLiangMaxWeekOfSuperBanKuai(requireddate,0);//��ʾcjl�Ƕ��������
		     } catch (java.lang.NullPointerException e) {
		     }
			 if(cjlmaxwk>0) {
				 org.jsoup.nodes.Element licjlmaxwk = dl.appendElement("li");
				 org.jsoup.nodes.Element fontcjlmaxwk = licjlmaxwk.appendElement("font");
				 fontcjlmaxwk.appendText("�ɽ���MaxWk=" + cjlmaxwk);
				 fontcjlmaxwk.attr("color", "#641E16");
			 } 
			 //
			 try{
				 Double cjlchangerate = this.getChenJiaoLiangChangeGrowthRateOfSuperBanKuai(superbk,requireddate,0);//cjl���̱仯������
				 if( cjlchangerate != -100.0) {
					 org.jsoup.nodes.Element licjlchangerate = dl.appendElement("li");
					 org.jsoup.nodes.Element fontcjlchangerate = licjlchangerate.appendElement("font");
					 fontcjlchangerate.appendText( "�ɽ������仯������" + percentFormat.format (cjlchangerate)  );
					 fontcjlchangerate.attr("color", "#641E16");
				 }
			 } catch (java.lang.IllegalArgumentException e) {
//				 li4.appendText("�ɽ�����̱仯������NULL" );
			 }

			try {
				Double cjlzhanbidata = this.getChenJiaoLiangZhanBi(requireddate, 0);
				
				org.jsoup.nodes.Element licjlzb = dl.appendElement("li");
				org.jsoup.nodes.Element fontzjlzb = licjlzb.appendElement("font");
				fontzjlzb.appendText( "�ɽ���ռ��" + decimalformate2.format(cjlzhanbidata)  );
				fontzjlzb.attr("color", "#512E5F");
			} catch (java.lang.IllegalArgumentException e ) {
//				htmltext = "ռ��ռ��NULL" ;
			}
			
			Integer cjlzbmaxwk = this.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(requireddate, 0);
			Integer cjlzbminwk = this.getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(requireddate, 0);
			if(cjlzbmaxwk > 0) {
				try {
					 org.jsoup.nodes.Element licjlmaxwk = dl.appendElement("li");
					 org.jsoup.nodes.Element fontcjlmaxwk = licjlmaxwk.appendElement("font");
					 fontcjlmaxwk.appendText( "�ɽ���ռ��MaxWk=" + cjlzbmaxwk.toString()  );
					 fontcjlmaxwk.attr("color", "#512E5F");
				} catch (java.lang.IllegalArgumentException e ) {
//					htmltext = "ռ��MaxWk=NULL"  ;
				}
			} else {
				try {
					 org.jsoup.nodes.Element licjlminwk = dl.appendElement("li");
					 org.jsoup.nodes.Element fontcjlminwk = licjlminwk.appendElement("font");
					 fontcjlminwk.appendText( "�ɽ���ռ��MinWk=" + cjlzbminwk.toString()  );
					 fontcjlminwk.attr("color", "#512E5F");
				} catch (java.lang.IllegalArgumentException e ) {
//					htmltext = "ռ��MaxWk=NULL"  ;
				}
			}
			
			Double cjlzbgrowthrate = this.getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(requireddate, 0);
			org.jsoup.nodes.Element licjlzbgr = dl.appendElement("li");
			org.jsoup.nodes.Element fontcjlzbgr = licjlzbgr.appendElement("font");
			fontcjlzbgr.appendText( "�ɽ���ռ��������=" + decimalformate2.format(cjlzbgrowthrate)  );
			fontcjlzbgr.attr("color", "#512E5F");

 
				 Integer opneupquekou = this.getQueKouTongJiOpenUp(requireddate, 0);
				 if( opneupquekou != null) {
					 org.jsoup.nodes.Element li6 = dl.appendElement("li");
					 org.jsoup.nodes.Element font6 = li6.appendElement("font");
					 font6.appendText("ȱ��OpenUp =" + opneupquekou);
					 font6.attr("color", "#F39C12");
				 }
				 Integer opendownquekou = this.getQueKouTongJiOpenDown(requireddate, 0);
				 if( opendownquekou != null) {
					 org.jsoup.nodes.Element li7 = dl.appendElement("li");
					 org.jsoup.nodes.Element font7 = li7.appendElement("font");
					 font7.appendText("ȱ��OpenDown =" + opendownquekou);
					 font7.attr("color", "#F39C12");
				 }
				 Integer huibuupquekou = this.getQueKouTongJiHuiBuUp(requireddate, 0);
				 if( huibuupquekou != null) {
					 org.jsoup.nodes.Element li8 = dl.appendElement("li");
					 org.jsoup.nodes.Element font8 = li8.appendElement("font");
					 font8.appendText("ȱ��HuiBuUp =" + huibuupquekou );
					 font8.attr("color", "#F39C12");
				 }
				 Integer huibudowquekou = this.getQueKouTongJiHuiBuDown(requireddate, 0);
				 if( huibudowquekou != null) {
					 org.jsoup.nodes.Element li9 = dl.appendElement("li");
					 org.jsoup.nodes.Element font9= li9.appendElement("font");
					 font9.appendText("ȱ��HuiBuDown =" + huibudowquekou );
					 font9.attr("color", "#F39C12");
				 }
				 
				 Integer zhangtingnum = this.getZhangTingTongJi(requireddate, 0);
				 if(zhangtingnum != null) {
					 org.jsoup.nodes.Element li10 = dl.appendElement("li");
					 org.jsoup.nodes.Element font10 = li10.appendElement("font");
					 font10.appendText("��ͣ =" + zhangtingnum );
					 font10.attr("color", "#1B2631");
				 }
				 
				 Integer dietingnum = this.getDieTingTongJi(requireddate, 0);
				 if(dietingnum != null) {
					 org.jsoup.nodes.Element li11 = dl.appendElement("li");
					 org.jsoup.nodes.Element font11 = li11.appendElement("font");
					 font11.appendText("��ͣ =" + dietingnum );
					 font11.attr("color", "#1B2631");
				 }
			 
			
		}
		
		return doc.toString();
	}
	/*
	 * 
	 */
//	protected RegularTimePeriod getJFreeChartFormateTimePeriod (LocalDate requireddate,int difference) 
//	{
//		String nodeperiod = this.getNodeperiodtype();
//		LocalDate expectedate = null;
//		RegularTimePeriod period = null;
//		if(nodeperiod.equals(NodeGivenPeriodDataItem.WEEK)) { 
//			expectedate = requireddate.plus(difference,ChronoUnit.WEEKS);
//			java.sql.Date lastdayofweek = java.sql.Date.valueOf(expectedate);
//			period = new org.jfree.data.time.Week (lastdayofweek);
////			period = new org.jfree.data.time.Week (Date.from(expectedate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
//		} else if(nodeperiod.equals(NodeGivenPeriodDataItem.DAY)) {
//			expectedate = requireddate.plus(difference,ChronoUnit.DAYS);
//			java.sql.Date lastdayofweek = java.sql.Date.valueOf(expectedate);
//			period = new org.jfree.data.time.Day (lastdayofweek);
////			period = new org.jfree.data.time.Day(Date.from(expectedate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
//		}  else if(nodeperiod.equals(NodeGivenPeriodDataItem.MONTH)) {
//			expectedate = requireddate.plus(difference,ChronoUnit.MONTHS);
//		}
//		
//		return period;
//	}
	 /*
	  * �û����������������/�յ����ڣ����תΪ���ܵ�����
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
	 
}
