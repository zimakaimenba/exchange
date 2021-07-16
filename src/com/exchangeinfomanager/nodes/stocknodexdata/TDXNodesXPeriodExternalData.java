package com.exchangeinfomanager.nodes.stocknodexdata;

import java.sql.SQLException;
import java.text.DateFormat;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.log4j.Logger;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Week;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ta4j.core.Bar;

import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.FormatDoubleToShort;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;
import net.sf.javaml.tools.InstanceTools;

/*
 * node的OHLC数据有JFC/TA4J两种存储方式，该类是这2中的所有公共方法 
 */
public abstract class TDXNodesXPeriodExternalData implements NodeXPeriodData 
{
	private Logger logger = Logger.getLogger(TDXNodesXPeriodExternalData.class);
	
	public TDXNodesXPeriodExternalData (String nodecode,String nodeperiodtype1)
	{
		this.nodecode = nodecode;
		this.nodeperiodtype = nodeperiodtype1;
		
		nodeamozhanbi = new TimeSeries(nodeperiodtype);
		nodevolzhanbi = new TimeSeries(nodeperiodtype);
		
		nodedpcjezbgr = new TimeSeries(nodeperiodtype);
		nodedpcjlzbgr = new TimeSeries(nodeperiodtype);
		
		nodeexchangedaysnumber = new TimeSeries(nodeperiodtype);
	}
	
	private String nodecode;
	private String nodeperiodtype;

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
	
	protected TimeSeries nodezhangtingnum;
	protected TimeSeries nodedietingnum;
	private LocalDate firstdayinhistory;
	
	private TimeSeries nodedpcjezbgr;
	private TimeSeries nodedpcjlzbgr;
	
//	private LocalDate lastdayofbxfx; //为不完整周分析准备的。如果为空说明是完整周
	
	public void addNewXPeriodData (NodeGivenPeriodDataItem kdata)
	{
		if(kdata.getNodeToDpChenJiaoErZhanbi() != null ) {
			try {
				nodeamozhanbi.setNotify(false);
				nodevolzhanbi.setNotify(false);

				nodeamozhanbi.add(kdata.getJFreeChartPeriod(this.nodeperiodtype),kdata.getNodeToDpChenJiaoErZhanbi(),false);
				nodevolzhanbi.add(kdata.getJFreeChartPeriod(this.nodeperiodtype),kdata.getNodeToDpChenJiaoLiangZhanbi(),false);
			} catch (org.jfree.data.general.SeriesException e) {
				System.out.println(getNodeCode() + getNodeperiodtype() 
				+ kdata.getJFreeChartPeriod(getNodeperiodtype()).toString() 
				+ kdata.getJFreeChartPeriod(getNodeperiodtype()).getStart().toString() 
				+ "nodeamozhanbi 数据已经存在，重复添加！"  );
			}
			try{
				nodeexchangedaysnumber.setNotify(false);
				if(kdata.getExchangeDaysNumber() != null && kdata.getExchangeDaysNumber() != 5) //
					nodeexchangedaysnumber.add(kdata.getJFreeChartPeriod(this.nodeperiodtype),kdata.getExchangeDaysNumber(),false);
			} catch (org.jfree.data.general.SeriesException e) {
				System.out.println(getNodeCode() + getNodeperiodtype() 
				+ kdata.getJFreeChartPeriod(getNodeperiodtype()).toString() 
				+ kdata.getJFreeChartPeriod(getNodeperiodtype()).getStart().toString()
				+ "nodeexchangedaysnumber 数据已经存在，重复添加！"  );
			}
			try {
				if(nodezhangtingnum == null)
					nodezhangtingnum = new TimeSeries(this.nodeperiodtype);
				if(nodedietingnum == null)
					nodedietingnum = new TimeSeries(this.nodeperiodtype);
				
				nodezhangtingnum.setNotify(false);
				nodedietingnum.setNotify(false);
				if(kdata.getZhangTingNumber() != null && kdata.getZhangTingNumber() !=0 )
					nodezhangtingnum.add(kdata.getJFreeChartPeriod(this.nodeperiodtype), kdata.getZhangTingNumber() );
				if(kdata.getDieTingNumber() != null && kdata.getDieTingNumber() !=0 )
					nodedietingnum.add(kdata.getJFreeChartPeriod(this.nodeperiodtype), kdata.getDieTingNumber() );
				
			} catch (org.jfree.data.general.SeriesException e) {
				System.out.println(getNodeCode() + getNodeperiodtype() 
				+ kdata.getJFreeChartPeriod(getNodeperiodtype()).toString() 
				+ kdata.getJFreeChartPeriod(getNodeperiodtype()).getStart().toString()
				+ " nodezhangtingnum 数据已经存在，重复添加！"  ); 
			}
			
//			nodeamozhanbi.setNotify(true);
//			nodevolzhanbi.setNotify(true);
//			nodeexchangedaysnumber.setNotify(true);
//			nodezhangtingnum.setNotify(true);
//			nodedietingnum.setNotify(true);
		}
		try {	
			nodedpcjezbgr.setNotify(false);
			if( kdata.getDaPanChenJiaoErZhanBiGrowingRate() != null && kdata.getDaPanChenJiaoErZhanBiGrowingRate() != 0)
				nodedpcjezbgr.add(kdata.getJFreeChartPeriod(this.getNodeperiodtype()), kdata.getDaPanChenJiaoErZhanBiGrowingRate(),false);
		} catch (org.jfree.data.general.SeriesException e) {
			System.out.println(getNodeCode() + getNodeperiodtype() 
			+ kdata.getJFreeChartPeriod(getNodeperiodtype()).toString() 
			+ kdata.getJFreeChartPeriod(getNodeperiodtype()).getStart().toString()
			+ "nodedpcjezbgr 数据已经存在，重复添加！"  ); 
		}
		try {	
			nodedpcjlzbgr.setNotify(false);
			if( kdata.getDaPanChenJiaoLiangZhanBiGrowingRate() != null && kdata.getDaPanChenJiaoLiangZhanBiGrowingRate() != 0.0)
				nodedpcjlzbgr.add(kdata.getJFreeChartPeriod(this.getNodeperiodtype()), kdata.getDaPanChenJiaoLiangZhanBiGrowingRate(),false);
		} catch (org.jfree.data.general.SeriesException e) {
			System.out.println(getNodeCode() + getNodeperiodtype() 
			+ kdata.getJFreeChartPeriod(getNodeperiodtype()).toString() 
			+ kdata.getJFreeChartPeriod(getNodeperiodtype()).getStart().toString()
			+ "nodedpcjlzbgr 数据已经存在，重复添加！"  );
		}
		
//		nodedpcjezbgr.setNotify(true);
//		nodedpcjlzbgr.setNotify(true);
	}
	/*
	 * 
	 */
	public void resetAllData ()
	{
		try {	nodeamozhanbi.clear();} catch (java.lang.NullPointerException e) {}
		try {	nodevolzhanbi.clear();} catch (java.lang.NullPointerException e) {}
		try {	nodefxjg.clear();} catch (java.lang.NullPointerException e) {}
		try {	nodeexchangedaysnumber.clear();}  catch (java.lang.NullPointerException e) {}
		try {	nodeqktjopenup.clear();} catch (java.lang.NullPointerException e) {}
		try {	nodeqktjhuibuup.clear();} catch (java.lang.NullPointerException e) {}
		try {	nodeqktjopendown.clear();} catch (java.lang.NullPointerException e) {}
		try {	nodeqktjhuibudown.clear();} catch (java.lang.NullPointerException e) {}
		try {	qklist.clear();} catch (java.lang.NullPointerException e) {}
		try {	nodezhangtingnum.clear();} catch (java.lang.NullPointerException e) {}
		try {	nodedietingnum.clear();} catch (java.lang.NullPointerException e) {}
		try {nodedpcjezbgr.clear();}  catch (java.lang.NullPointerException e) {}
		try {nodedpcjlzbgr.clear();}  catch (java.lang.NullPointerException e) {}
		
		firstdayinhistory = null;
	}
	/*
	 * 判断日期是否到了数据库历史纪录的第一条
	 */
	public Boolean isLocalDateReachFristDayInHistory (LocalDate requireddate)
	{
		if(this.firstdayinhistory == null)
			return null;
		
		RegularTimePeriod requiredperiod = this.getJFreeChartFormateTimePeriod (requireddate);
		if(requiredperiod == null)
			return null;
		
		requireddate = requiredperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if(this.nodeperiodtype.equals(NodeGivenPeriodDataItem.DAY) ) {
			if(requireddate.equals(this.firstdayinhistory))
				return true;
			else
				return false;
		} else if(this.nodeperiodtype.equals(NodeGivenPeriodDataItem.WEEK) ) {
			Boolean checkresult = CommonUtility.isInSameWeek(requireddate, this.firstdayinhistory);
			return checkresult;
		}
		
		return null;
	}
	public void setShangShiRiQi (LocalDate requireddate)
	{
		this.firstdayinhistory = requireddate;
	}
	/*
	 * 
	 */
	public String getNodeCode ()
	{
		return this.nodecode;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#getNodeperiodtype()
	 */
	public String getNodeperiodtype() {
		return nodeperiodtype;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#hasFxjgInPeriod(java.time.LocalDate, int)
	 */
	public Boolean hasFxjgInPeriod (LocalDate requireddate)
	{
		if(nodefxjg == null)
			return false;

		RegularTimePeriod period = this.getJFreeChartFormateTimePeriod(requireddate);

		TimeSeriesDataItem fxjgitem = nodefxjg.getDataItem( period);
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
		
		try {	nodefxjg.add(period,fxjg);
		} catch (org.jfree.data.general.SeriesException e) {	}
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
	/*
	 * 
	 */
	public void addZhangDieTingTongJiJieGuo (LocalDate tjdate,Integer nodeztnum,Integer nodedtnum,Boolean addbasedoncurdata)
	{
		if( this.nodezhangtingnum == null)
			this.nodezhangtingnum = new TimeSeries(this.nodeperiodtype);
		
		if( this.nodedietingnum == null)
			this.nodedietingnum = new TimeSeries(this.nodeperiodtype);
		
		RegularTimePeriod period = getJFreeChartFormateTimePeriod(tjdate);
		if(period == null)	return;
		
		if(nodeztnum != null) {
			try {
				if(addbasedoncurdata) {
					TimeSeriesDataItem dataitem = this.nodezhangtingnum.getDataItem(period);
					if(dataitem == null)	nodezhangtingnum.add(period,nodeztnum);
					else {
						int count = dataitem.getValue().intValue();
						nodezhangtingnum.addOrUpdate(period, count + nodeztnum);
					}
				} else	nodezhangtingnum.addOrUpdate(period,nodeztnum);
			} catch (org.jfree.data.general.SeriesException e) { e.printStackTrace();	}
		}
		if(nodedtnum != null) {
			try {
				if(addbasedoncurdata) {
					TimeSeriesDataItem dataitem = this.nodedietingnum.getDataItem(period);
					if(dataitem == null) 
						nodedietingnum.add(period,nodedtnum);
					else {
						int count = dataitem.getValue().intValue();
						nodedietingnum.addOrUpdate(period, count + nodedtnum);
					}
				} else
					nodedietingnum.addOrUpdate(period,nodedtnum);
			} catch (org.jfree.data.general.SeriesException e) {e.printStackTrace();}
		}
	}
	/*
	 * 参数addbasedoncurdata：true在现有的值上更新，false覆盖现有值
	 */
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
		
		RegularTimePeriod period = getJFreeChartFormateTimePeriod(qkdate);
		if(period == null)
			return;
		
		if(nodeqktjopenup1 != null  && nodeqktjopenup1 != 0) { //nodeqktjopenup1可以等于0，用来标记统计缺口的开始/结束时间
			try {
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
			} catch (org.jfree.data.general.SeriesException e) {e.printStackTrace();}
		}

		if(nodeqktjhuibuup1 != null && nodeqktjhuibuup1 !=0 ) {
			try {
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
			} catch (org.jfree.data.general.SeriesException e) {e.printStackTrace();}
		}
		
		if(nodeqktjopendown1 != null && nodeqktjopendown1 !=0 ) {
			try {
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

			} catch (org.jfree.data.general.SeriesException e) {e.printStackTrace();}

		}
			
		if(nodeqktjhuibudown1 != null && nodeqktjhuibudown1 !=0 ) {
			try {
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
			} catch (org.jfree.data.general.SeriesException e) {e.printStackTrace();}
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
	public Integer getZhangTingTongJi (LocalDate requireddate)
	{
		if(this.nodezhangtingnum == null)	return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod(requireddate);
		if(curperiod == null)	return null;
		
		TimeSeriesDataItem zttjitem = nodezhangtingnum.getDataItem( curperiod);
		if(zttjitem == null)	return null;
		else	return (Integer)zttjitem.getValue().intValue();			
	}
	/*
	 * 
	 */
	public Integer getDieTingTongJi (LocalDate requireddate)
	{
		if(this.nodedietingnum == null)	return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod(requireddate);
		if(curperiod == null)	return null;
		
		TimeSeriesDataItem dttjitem = nodedietingnum.getDataItem( curperiod);
		if(dttjitem == null)	return null;
		else	return (Integer)dttjitem.getValue().intValue();			
	}
	/*
	 * 
	 */
	public Integer getQueKouTongJiOpenUp (LocalDate requireddate)
	{
		if(this.nodeqktjopenup == null)	return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod(requireddate);
		if(curperiod == null )	return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjopenup.getDataItem(curperiod);
		if(qktjitem == null)	return null;
		else
			 return (Integer)qktjitem.getValue().intValue(); 
	}
	/*
	 * 
	 */
	public Integer getQueKouTongJiOpenDown (LocalDate requireddate)
	{
		if(this.nodeqktjopendown == null)	return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod(requireddate);
		if(curperiod == null )	return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjopendown.getDataItem( curperiod);
		if(qktjitem == null)	return null;
		else
			 return (Integer)qktjitem.getValue().intValue(); 
	}
	/*
	 * 
	 */
	public Integer getQueKouTongJiHuiBuUp (LocalDate requireddate)
	{
		if(this.nodeqktjhuibuup == null)	return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod(requireddate);
		if(curperiod == null )	return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjhuibuup.getDataItem( curperiod);
		if(qktjitem == null)	return null;
		else	return (Integer)qktjitem.getValue().intValue(); 
	}
	/*
	 * 
	 */
	public Integer getQueKouTongJiHuiBuDown (LocalDate requireddate)
	{
		if(this.nodeqktjhuibudown == null)	return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod(requireddate);
		if(curperiod == null )	return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjhuibudown.getDataItem( curperiod);
		if(qktjitem == null)	return null;
		else	return qktjitem.getValue().intValue(); 
	}
	/*
	 * 
	 */
	public  LocalDate  getQueKouRecordsStartDate ()
	{ 
		if(nodeqktjopenup == null)	return null;
		if(nodeqktjopenup.getItemCount() == 0)	return null;

		RegularTimePeriod firstperiod = nodeqktjopenup.getTimePeriod( 0);
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		if(this.nodeperiodtype == NodeGivenPeriodDataItem.WEEK) {
			LocalDate mondayday = startdate.with(DayOfWeek.MONDAY);
			return mondayday;
		} else if(this.nodeperiodtype == NodeGivenPeriodDataItem.DAY) {
			return startdate;
		}
		
		return null;
	}
	/*
	 * 
	 */
	public  LocalDate  getQueKouRecordsEndDate ()
	{ 
		if(nodeqktjopenup == null || nodeqktjopenup.getItemCount() == 0)
			return null;

		int itemcount = nodeqktjopenup.getItemCount();
		RegularTimePeriod firstperiod = nodeqktjopenup.getTimePeriod( itemcount - 1 );
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		if(this.nodeperiodtype == NodeGivenPeriodDataItem.WEEK) {
			LocalDate saturday = enddate.with(DayOfWeek.FRIDAY);
			return saturday;
		} else if(this.nodeperiodtype == NodeGivenPeriodDataItem.DAY) {
			return enddate;
		}
		
		return null;
	}
	/*
	 * 
	 */
	public void addDaPanChenJiaoErZhanBiGrowingRate (LocalDate requireddate, Double cjezbgr)
	{
		try {	
			nodedpcjezbgr.setNotify(false);
			
			RegularTimePeriod recorddate = null;
			if(this.nodeperiodtype == NodeGivenPeriodDataItem.WEEK) {
				try {	DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
						java.sql.Date sqldate = new java.sql.Date(format.parse(requireddate.with(DayOfWeek.MONDAY).toString()).getTime());
						recorddate = new org.jfree.data.time.Week (sqldate);
				} catch (ParseException e) {e.printStackTrace();}
			} else if(this.nodeperiodtype == NodeGivenPeriodDataItem.DAY) {
				try {	DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
						java.sql.Date sqldate = new java.sql.Date(format.parse(requireddate.toString()).getTime());
						recorddate = new org.jfree.data.time.Day(sqldate);
				} catch (ParseException e) {e.printStackTrace();}
			}
			
			Double curcjezbgr = this.getChenJiaoErZhanBiGrowthRateForDaPan(requireddate);
			if( (curcjezbgr == null ||  curcjezbgr == 100.0) && cjezbgr != 100.0) {
				try{	nodedpcjezbgr.add(recorddate, cjezbgr, false );
				} catch(Exception e) {e.printStackTrace();	return; }
				return;
			}
			Double curcjezbgrround = CommonUtility.round(curcjezbgr,6);
			Double setcjezbgrround =  CommonUtility.round(cjezbgr,6);
			if(  curcjezbgrround.compareTo(setcjezbgrround ) !=0 ) {
				try{	this.nodedpcjezbgr.delete(recorddate);
						nodedpcjezbgr.add(recorddate, cjezbgr, false );
				} catch(Exception e) {e.printStackTrace();	return;	}
				return;
			}
		} catch (org.jfree.data.general.SeriesException e) {}
	}
	/*
	 * 
	 */
	public Double getDaPanChenJiaoLiangZhanBiGrowingRate (LocalDate requireddate)
	{
		if(nodedpcjlzbgr == null) return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod (requireddate);
		if(curperiod == null)	return null;
		
		TimeSeriesDataItem cjlzbgrdata = nodedpcjlzbgr.getDataItem(curperiod);
		Double value = cjlzbgrdata.getValue().doubleValue();
		return value;
	}
		public void addDaPanChenJiaoLiangZhanBiGrowingRate (LocalDate requireddate, Double cjezbgr)
		{
			try {	
				nodedpcjlzbgr.setNotify(false);
				
				RegularTimePeriod recorddate = null;
				if(this.nodeperiodtype == NodeGivenPeriodDataItem.WEEK) {
					try {	DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
							java.sql.Date sqldate = new java.sql.Date(format.parse(requireddate.with(DayOfWeek.MONDAY).toString()).getTime());
							recorddate = new org.jfree.data.time.Week (sqldate);
					} catch (ParseException e) {e.printStackTrace();}
				} else if(this.nodeperiodtype == NodeGivenPeriodDataItem.DAY) {
					try {	DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
							java.sql.Date sqldate = new java.sql.Date(format.parse(requireddate.toString()).getTime());
							recorddate = new org.jfree.data.time.Day(sqldate);
					} catch (ParseException e) {e.printStackTrace();}
				}
				
				Double curcjlzbgr = this.getChenJiaoLiangZhanBiGrowthRateForDaPan(requireddate);
				if( (curcjlzbgr == null || curcjlzbgr ==100.0) && cjezbgr != 100.0  ) {
					try{	nodedpcjlzbgr.add(recorddate, cjezbgr, false );
					} catch(Exception e) {e.printStackTrace();	return; }
					return;
				}
				Double curcjlzbgrround = CommonUtility.round(curcjlzbgr,6);
				Double settingcjezbgrround = CommonUtility.round(cjezbgr,6);
				if(  curcjlzbgrround.compareTo(settingcjezbgrround) !=0 ) {
					try{	this.nodedpcjlzbgr.delete(recorddate);
							nodedpcjlzbgr.add(recorddate, cjezbgr, false );
					} catch(Exception e) {e.printStackTrace();	return;	}
					return;
				}
			} catch (org.jfree.data.general.SeriesException e) {}
		}
	/*
	 * 
	 */
	public Double getChenJiaoErZhanBi (LocalDate requireddate)
	{
		if(nodeamozhanbi == null)	return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod(requireddate);
		if(curperiod == null )	return null;
		
		TimeSeriesDataItem curcjlrecord = nodeamozhanbi.getDataItem( curperiod);
		
		if( curcjlrecord == null) return null;
		else
			return curcjlrecord.getValue().doubleValue();
	}
	 /*
	  *  @Override CJEDPZBMAXWK 连续大于指定数据
	  */
	 public Integer getChenJiaoErZhanBiDpMaxWkLianXuFangLiangPeriodNumber (LocalDate requireddate, int settingzbdpmaxwk)
	 {
		 RegularTimePeriod curperiod = this.getJFreeChartFormateTimePeriod(requireddate ) ;
		 if(curperiod == null)	return null;
		 
		 Integer recordmaxbkwk = this.getChenJiaoErZhanBiMaxWeekForDaPan(requireddate);
		 int max = 0;
		 if(recordmaxbkwk < settingzbdpmaxwk) return max;
		 else max ++;
		 
		 int curindex = nodeamozhanbi.getIndex(curperiod);
		 if(curindex == 0) return max;
		 for(int i=1;curindex -i >= 0;i++) {
			 TimeSeriesDataItem lstitem = this.nodeamozhanbi.getDataItem(curindex -i);
			 LocalDate lstdate = lstitem.getPeriod().getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			 Integer recordmaxbkwklast = this.getChenJiaoErZhanBiMaxWeekForDaPan(lstdate);
			 if(recordmaxbkwklast >= settingzbdpmaxwk) max ++;
			 else break;
		 }
		 
		 return max;
	 }
	/*
	 * 周线的AMOZBGR是计算出来的，日线的是在周线计算后存进来的，获取方法不一样
	 */
	public Double getChenJiaoErZhanBiGrowthRateForDaPan(LocalDate requireddate) 
	{
		RegularTimePeriod expectedperiod = getJFreeChartFormateTimePeriod(requireddate);
		if(expectedperiod == null)	return null;
		
		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem(expectedperiod );
		if( curcjlrecord == null) { //可能是日线，要去 nodedpcjezbgr 取值
			TimeSeriesDataItem curcjezbgrrecord = this.nodedpcjezbgr.getDataItem(expectedperiod );
			if(curcjezbgrrecord != null) {
				Double cjezbgr = curcjezbgrrecord.getValue().doubleValue();
				return cjezbgr;
			}
		}
		
		try {
			int index = this.nodeamozhanbi.getIndex(expectedperiod);
			TimeSeriesDataItem lastcjlrecord = nodeamozhanbi.getDataItem( index -1  );
			if(lastcjlrecord == null) { //休市前还是空，说明要是新板块。板块没有停牌的
				logger.debug(getNodeCode() + "可能是一个新个股或板块");
				return 100.0;
			}
			
			Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
			Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
			Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
			
			return zhanbigrowthrate;
			
		}	catch (java.lang.IndexOutOfBoundsException ex) {	return 100.0;	}
	}
	/*
	 * 对上级板块的成交额占比是多少周内的最大值
	 */
	public Integer getChenJiaoErZhanBiMaxWeekForDaPan(LocalDate requireddate) 
	{
		RegularTimePeriod expectedperiod = getJFreeChartFormateTimePeriod(requireddate);
		if(expectedperiod == null)	return null;
		
		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem( expectedperiod);
		if( curcjlrecord == null) 	return null;
					
		Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
		int index = nodeamozhanbi.getIndex(expectedperiod);
		int maxweek = 0;
		for(int i = index - 1; i >= 0; i--) {
			TimeSeriesDataItem lastcjlrecord = nodeamozhanbi.getDataItem( i );
			if(lastcjlrecord == null )	return maxweek; //可能到了记录的头部了，或者是个诞生时间不长的板块

			Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
			if(curzhanbiratio > lastzhanbiratio)	maxweek ++;
			else	break;
		}
		
		return maxweek;
	}
	/*
	 * 
	 */
	public Integer getChenJiaoErZhanBiMinWeekForDaPan(LocalDate requireddate)
	{
		if(this.nodeamozhanbi == null)	return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod(requireddate);
		if(curperiod == null )	return null;
		
		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem( curperiod);
		if( curcjlrecord == null)	return null;

		Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
		int index = nodeamozhanbi.getIndex(curperiod );
		int minweek = 0;
		for(int i = index -1; i >= 0; i --) {
			TimeSeriesDataItem lastcjlrecord = nodeamozhanbi.getDataItem( i );
			if(lastcjlrecord == null )	return minweek; //可能到了记录的头部了，或者是个诞生时间不长的板块

			Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
			if(curzhanbiratio < lastzhanbiratio)	minweek ++;
			else	break;
		}
		
		return minweek;
	}
	/*
	 * 在指定时间段内，大盘占比最小值是多少
	 */
	public Integer getChenJiaoErZhanBiMinestWeekForDaPanInSpecificPeriod(LocalDate requireddate, int checkrange)
	{
		if(this.nodeamozhanbi == null)	return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod(requireddate);
		if(curperiod == null )	return null;
		
		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem( curperiod);
		if( curcjlrecord == null)	return null;
		
		int index = nodeamozhanbi.getIndex(curperiod );
		int bottomline;
		if( index -20 >=0 )	bottomline = index -20;
		else	bottomline = 0;
		
		int minweek = 0;
		for(int i = index ; i >= bottomline; i --) {
			TimeSeriesDataItem lastcjlrecord = nodeamozhanbi.getDataItem( i );
			if(lastcjlrecord == null ) //可能到了记录的头部了，或者是个诞生时间不长的板块
				return minweek;
			
			RegularTimePeriod lstperiod = lastcjlrecord.getPeriod();
			LocalDate curend = lstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			Integer lstminwk = this.getChenJiaoErZhanBiMinWeekForDaPan(curend);
			if(lstminwk > minweek)
				minweek = lstminwk ;
		}
		
		return minweek;
	}
	/*
	  * 计算指定周有几个交易日
	  */
	 public Integer getExchangeDaysNumberForthePeriod (LocalDate requireddate)
	 {
		 if(nodeexchangedaysnumber == null)	return null;
		 
		 RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod(requireddate);
		 if(curperiod == null )	return null;
		 
		 TimeSeriesDataItem curdaynumberrecord = this.nodeexchangedaysnumber.getDataItem(curperiod);
		 if( curdaynumberrecord == null)	return 5;
		 else	return curdaynumberrecord.getValue().intValue();
	 }
	 /*
		 * 周线日线不一样，周线是计算的，日线是获取的
		 */
		public Double getChenJiaoLiangZhanBiGrowthRateForDaPan(LocalDate requireddate) 
		{
			RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod(requireddate);
			if(curperiod == null )	return null;		
			
			TimeSeriesDataItem curcjlrecord = this.nodevolzhanbi.getDataItem( curperiod);
			if( curcjlrecord == null) { //可能是日线，要去 nodedpcjezbgr 取值
				TimeSeriesDataItem curcjlzbgrrecord = this.nodedpcjlzbgr.getDataItem(curperiod );
				if(curcjlzbgrrecord != null) {
					Double cjezbgr = curcjlzbgrrecord.getValue().doubleValue();
					return cjezbgr;
				} 
			}
			
			int index = this.nodevolzhanbi.getIndex(curperiod);
			try {
				TimeSeriesDataItem lastcjlrecord = nodevolzhanbi.getDataItem( index -1  );
				if(lastcjlrecord == null) { //休市前还是空，说明要是新板块。板块没有停牌的
					logger.debug(getNodeCode() + "可能是一个新个股或板块");
					return 100.00;
				}
				
				Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
				Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
				Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
				return zhanbigrowthrate;
			}	catch (java.lang.IndexOutOfBoundsException ex) {return 100.0;}
		}
		/*
		 * 
		 */
		public Integer getChenJiaoLiangZhanBiMaxWeekForDaPan(LocalDate requireddate ) 
		{
			RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod(requireddate);
			if(curperiod == null )	return null;
			
			TimeSeriesDataItem curcjlrecord = this.nodevolzhanbi.getDataItem( curperiod);
			if( curcjlrecord == null)	return null;
						
			Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
			int index = nodevolzhanbi.getIndex(curperiod );
			int maxweek = 0;
			for(int i = index - 1; i >= 0; i--) {
				TimeSeriesDataItem lastcjlrecord = nodevolzhanbi.getDataItem( i );
				if(lastcjlrecord == null )	return maxweek; //可能到了记录的头部了，或者是个诞生时间不长的板块

				Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
				if(curzhanbiratio > lastzhanbiratio)	maxweek ++;
				else	break;
			}
			
			return maxweek;
		}
		/*
		 * 
		 */
		public Integer getChenJiaoLiangZhanBiMinWeekForDaPan(LocalDate requireddate ) 
		{
			if(this.nodevolzhanbi == null)	return null;
			
			RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod(requireddate);
			if(curperiod == null )	return null;
			
			TimeSeriesDataItem curcjlrecord = this.nodevolzhanbi.getDataItem( curperiod);
			if( curcjlrecord == null)	return null;

			Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
			int index = nodevolzhanbi.getIndex(curperiod );
			int minweek = 0;
			for(int i = index -1; i >= 0; i --) {
				TimeSeriesDataItem lastcjlrecord = nodevolzhanbi.getDataItem( i );
				if(lastcjlrecord == null ) //可能到了记录的头部了，或者是个诞生时间不长的板块
					return minweek;

				Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
				if(curzhanbiratio < lastzhanbiratio)	minweek ++;
				else	break;
			}
			
			return minweek;
		}
		/*
		 * 
		 */
		public Double getChenJiaoLiangZhanBi(LocalDate requireddate ) 
		{
			if(this.nodevolzhanbi == null)	return null;
			
			RegularTimePeriod curperiod = getJFreeChartFormateTimePeriod(requireddate);
			if(curperiod == null )	return null;
			
			TimeSeriesDataItem curcjlrecord = nodevolzhanbi.getDataItem( curperiod);
			
			if( curcjlrecord == null)	return null;
			else	return curcjlrecord.getValue().doubleValue();
		}
		/*
		 * 
		 */
		public Object getNodeDataByKeyWord( String keyword, LocalDate date, String... maformula) 
		{
			  Object value = null;
			  switch (keyword) {
			  case "ChengJiaoErZhanBi":
		    	   Double cjezhanbi = this.getChenJiaoErZhanBi(date);
        		   value = cjezhanbi;
		    	   break;
			  case "CjeZbDpMaxWk":
	    	   	  Integer cjedpmaxwk = null;
		          try{
		        	   cjedpmaxwk = this.getChenJiaoErZhanBiMaxWeekForDaPan(date);
		        	   if(cjedpmaxwk == 0)
		        		   value = null;
		        	   else
		        	   	   value = cjedpmaxwk;
		                break;
		          } catch (java.lang.NullPointerException e) {  value = null;}
		          break;
			  case "CjeZbDpMinWk":
	    	   	  Integer cjedpminwk = null;
		          try{
	              		cjedpminwk = this.getChenJiaoErZhanBiMinWeekForDaPan(date);
	              		value = 0 - cjedpminwk;
	              		if(cjedpminwk == 0)
	              			value = null;
	              		break;
		          } catch (java.lang.NullPointerException e) {  value = null;}
		          break;
			  case "CjeZbGrowRate":
		     	  Double cjedpgrowthrate = this.getChenJiaoErZhanBiGrowthRateForDaPan(date);
		     	  value = cjedpgrowthrate;
		     	  break; 
		     	  
			  case "ChengJiaoLiangZhanBi":
		    	   Double cjlzhanbi = this.getChenJiaoLiangZhanBi(date);
        		   value = cjlzhanbi;
		    	   break;	
			  case "CjlZbDpMaxWk":
		    	   Integer cjldpmaxwk = null;
				   try{
				    	  cjldpmaxwk = this.getChenJiaoLiangZhanBiMaxWeekForDaPan(date);
				          value = cjldpmaxwk;
			       } catch (java.lang.NullPointerException e) {value = null;}
			       break;
			  case "CjlZbDpMinWk":
	    	   	  Integer cjldpminwk = null;
		          try{
	              		cjldpminwk = this.getChenJiaoLiangZhanBiMinWeekForDaPan(date);
	              		value = 0 - cjldpminwk;
	              		break;
		          } catch (java.lang.NullPointerException e) {  value = null;}
		          break;	  
			  case "CjlZbGrowRate":
		     	  Double cjldpgrowthrate = this.getChenJiaoLiangZhanBiGrowthRateForDaPan(date);//.getGgdpzhanbigrowthrate();
		     	  value = cjldpgrowthrate;
		     	  break;
			  }
		
			return value;
		}
		 /*
		  * 
		  */
//		 public String[] getNodeXDataCsvData (TDXNodes superbk, LocalDate requireddate )
//		 {
//					Double curcjezhanbidata = this.getChenJiaoErZhanBi(requireddate);  //占比
//					Integer cjezbmaxweek = this.getChenJiaoErZhanBiMaxWeekForDaPan(requireddate);//nodefx.getGgbkzhanbimaxweek();
//					Integer cjezbminweek = this.getChenJiaoErZhanBiMinWeekForDaPan(requireddate);
//					Double cjezbgrowthrate = this.getChenJiaoErZhanBiGrowthRateForDaPan(requireddate);
//					
//					Double cjlzhanbidata = this.getChenJiaoLiangZhanBi(requireddate);
//					Integer cjlzbmaxwk = this.getChenJiaoLiangZhanBiMaxWeekForDaPan(requireddate);
//					Integer cjlzbminwk = this.getChenJiaoLiangZhanBiMinWeekForDaPan(requireddate);
//					Double cjlzbgrowthrate = this.getChenJiaoLiangZhanBiGrowthRateForDaPan(requireddate);
//		 
//					Integer opneupquekou = this.getQueKouTongJiOpenUp(requireddate);
//					Integer opendownquekou = this.getQueKouTongJiOpenDown(requireddate);
//					Integer huibuupquekou = this.getQueKouTongJiHuiBuUp(requireddate);
//					Integer huibudowquekou = this.getQueKouTongJiHuiBuDown(requireddate);
//					 
//					Integer zhangtingnum = this.getZhangTingTongJi(requireddate);
//					Integer dietingnum = this.getDieTingTongJi(requireddate);
//					
//					String strcurcjezhanbidata = null;
//					try {	strcurcjezhanbidata = curcjezhanbidata.toString();	} catch (java.lang.NullPointerException e) {	strcurcjezhanbidata = String.valueOf("0"); }
//					String strcjemaxweek = null;
//					try {	strcjemaxweek = cjezbmaxweek.toString();	} catch (java.lang.NullPointerException e) {	strcjemaxweek = String.valueOf("0");	}
//					String strcjeminweek = null;
//					try {	strcjeminweek = cjezbminweek.toString();	} catch (java.lang.NullPointerException e) {	strcjeminweek = String.valueOf("0");	}
//					String strcjezbgrowthrate = null;
//					try {	strcjezbgrowthrate = cjezbgrowthrate.toString() ;} catch (java.lang.NullPointerException e) {	strcjezbgrowthrate = String.valueOf("0");	}
//					String strcjlzhanbidata = null;
//					try {	strcjlzhanbidata = cjlzhanbidata.toString();	} catch (java.lang.NullPointerException e) {	strcjlzhanbidata = String.valueOf("0");	}
//					String strcjlzbmaxwk = null;
//					try {	strcjlzbmaxwk = cjlzbmaxwk.toString();	} catch (java.lang.NullPointerException e) {	strcjlzbmaxwk = String.valueOf("0");	}
//					String strcjlzbminwk = null;
//					try {	strcjlzbminwk = cjlzbminwk.toString();	} catch (java.lang.NullPointerException e) {	strcjlzbminwk = String.valueOf("0");	}
//					String strcjlzbgrowthrate = null;
//					try {	strcjlzbgrowthrate = cjlzbgrowthrate.toString();	} catch (java.lang.NullPointerException e) {	strcjlzbgrowthrate = String.valueOf("0");	}
//					String stropneupquekou = null;
//					try {	stropneupquekou = opneupquekou.toString();	} catch (java.lang.NullPointerException e) {	stropneupquekou = String.valueOf("0");	}
//					String stropendownquekou = null;
//					try {	stropendownquekou = opendownquekou.toString();	} catch (java.lang.NullPointerException e) {	stropendownquekou = String.valueOf("0");	}
//					String strhuibuupquekou = null;
//					try {	strhuibuupquekou = huibuupquekou.toString();	} catch (java.lang.NullPointerException e) {	strhuibuupquekou = String.valueOf("0");	}
//					String strhuibudowquekou = null;
//					try {	strhuibudowquekou = huibudowquekou.toString();	} catch (java.lang.NullPointerException e) {	strhuibudowquekou = String.valueOf("0");	} 
//					String strzhangtingnum = null;
//					try {	strzhangtingnum = zhangtingnum.toString();	} catch (java.lang.NullPointerException e) {	strzhangtingnum = String.valueOf("0");	}
//					String strdietingnum = null;
//					try {	strdietingnum = dietingnum.toString();	} catch (java.lang.NullPointerException e) {	strdietingnum = String.valueOf("0");	}
//
//					String[] csvline =  {strcurcjezhanbidata ,
//							strcjemaxweek ,
//							strcjeminweek ,
//							strcjezbgrowthrate ,
//					
//							strcjlzhanbidata ,
//							strcjlzbmaxwk ,
//							strcjlzbminwk ,
//							strcjlzbgrowthrate ,
//		 
//							stropneupquekou ,
//							stropendownquekou ,
//							strhuibuupquekou ,
//							strhuibudowquekou ,
//					 
//							strzhangtingnum ,
//							strdietingnum 
//					};
//						 
//					return csvline;
//		 }
		 /*
		  * 
		  */
		 public void removeNodeDataFromSpecificDate (LocalDate requireddate )
		 {
			 int itemcount = this.nodeamozhanbi.getItemCount();
			 RegularTimePeriod period = this.getJFreeChartFormateTimePeriod(requireddate);
			 if(period == null) return;
			 
			 Integer curindex = this.nodeamozhanbi.getIndex(period);
			 try {
					if(curindex >= 0 ) {
						nodeamozhanbi.delete(curindex, itemcount-1);
						nodevolzhanbi.delete(curindex, itemcount-1);
					}
			} catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
			 
			 itemcount = this.nodeexchangedaysnumber.getItemCount(); 
			 curindex =  this.nodeexchangedaysnumber.getIndex(period);
			 try {
					if(curindex >= 0 ) {
						nodeexchangedaysnumber.delete(curindex, itemcount-1);
					}
			} catch (java.lang.IndexOutOfBoundsException e) {	e.printStackTrace();	}
			  
		 }
		 
		 /*
			 * 
			 */
			protected RegularTimePeriod getJFreeChartFormateTimePeriod (LocalDate requireddate) 
			{
				String nodeperiod = this.getNodeperiodtype();
				RegularTimePeriod period = null;
				if(nodeperiod.equals(NodeGivenPeriodDataItem.WEEK)) { 
					java.sql.Date lastweek = java.sql.Date.valueOf(requireddate);
					period = new org.jfree.data.time.Week (lastweek);
				} else if(nodeperiod.equals(NodeGivenPeriodDataItem.DAY)) {
					java.sql.Date lastdayofweek = java.sql.Date.valueOf(requireddate);
					period = new org.jfree.data.time.Day (lastdayofweek);
				}  else if(nodeperiod.equals(NodeGivenPeriodDataItem.MONTH)) {
//					expectedate = requireddate.plus(difference,ChronoUnit.MONTHS);
				}
				
				return period;
			}
			
//		protected RegularTimePeriod getJFreeChartFormateTimePeriodForAMOZhanBi (LocalDate requireddate,int difference) 
//		{
//			if(nodeamozhanbi == null)
//				return null;
//			
//			RegularTimePeriod period = getJFreeChartFormateTimePeriod(requireddate);
//			
//			Integer curindex = this.nodeamozhanbi.getIndex(period);
//			try {
//				if(curindex >= 0 ) {
//					TimeSeriesDataItem dataitem = this.nodeamozhanbi.getDataItem(curindex + difference);
//					if(dataitem != null) {
//						RegularTimePeriod expectedperiod = dataitem.getPeriod();
//						return expectedperiod;
//					}
//				}
//			} catch (java.lang.IndexOutOfBoundsException e) {//				e.printStackTrace();
//				return null;
//			}
//			return null;
//		}
		 /*
		  * 用户如果传来的是周六/日的日期，最好转为当周的周五
		  */
		 protected LocalDate adjustDate(LocalDate dateneedtobeadjusted)
		 {
			 LocalDate friday;
			 DayOfWeek dayofweek = dateneedtobeadjusted.getDayOfWeek();
			 if( dayofweek.equals(DayOfWeek.SUNDAY)  ) {
				friday = dateneedtobeadjusted.minus(2,ChronoUnit.DAYS);
				return friday;
			 } else if (  dayofweek.equals(DayOfWeek.SATURDAY)  ) {
				friday = dateneedtobeadjusted.minus(1,ChronoUnit.DAYS);
				return friday;
			 }
						
			return dateneedtobeadjusted;
		 }
		 /*
		  * 
		  */
		 protected TimeSeries amozbclusterfromml;
		 public void calulateKLearnResult ()
		 {
			 Dataset data = new DefaultDataset();
			 amozbclusterfromml = null;
			 amozbclusterfromml = new TimeSeries("amozbclusterfromml");
			 for(int i=0; i< this.nodeamozhanbi.getItemCount(); i++ ) {
				 TimeSeriesDataItem record = this.nodeamozhanbi.getDataItem(i);
				 double recordvalue = record.getValue().doubleValue();
				 RegularTimePeriod curperiod = record.getPeriod();
				 LocalDate curstart = curperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				 LocalDate curend = curperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				 
				 double[] dr = {recordvalue};
				 Instance instance = new DenseInstance(dr,curend.toString());
				 data.add(instance);
		 	}
			 
			 Clusterer km = new KMeans(4);
			 Dataset[] clusters = km.cluster(data);
			 
			 for(int i=0; i<clusters.length; i++) {
				 Dataset temp = clusters[i];
				 for(int j=0;j<temp.size();j++) {
					 DenseInstance instance = (DenseInstance)temp.get(j);
					 LocalDate dil = LocalDate.parse( instance.classValue().toString() );
					 java.sql.Date lastdayofweek =  java. sql. Date. valueOf(dil);
					 org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(lastdayofweek);
					 amozbclusterfromml.add(wknum, i);
					 
					 Iterator<Double> it = instance.iterator();
					 Double doubleresult = it.next();
					 
//					 System.out.print("(" + dil.toString() + ":" + doubleresult + ")\n");
				 }
				 
//				 System.out.print("(**********************************)\n");
			 }
			 
//			 for(int i=0; i< amozbclusterfromml.getItemCount() ;i ++) {
//				 TimeSeriesDataItem dataitem = amozbclusterfromml.getDataItem(i);
//				 int value = dataitem.getValue().intValue();
//				 System.out.print(value);
//			 }
			 return;
		 }
		 /*
		  * 
		  */
		 protected TimeSeries amozbclusterfromapache;
		 public void calulateCurrentAMOZhanBiApacheMathKLearnResult ()
		 {
			 amozbclusterfromapache = null;
			 amozbclusterfromapache = new TimeSeries("amozbclusterfromapache");
			 java.util.Collection<StockDoublePoint> amozhanbiset = new HashSet<> ();
			 for(int i=0; i< this.nodeamozhanbi.getItemCount(); i++ ) {
				 TimeSeriesDataItem record = this.nodeamozhanbi.getDataItem(i);
				 double recordvalue = record.getValue().doubleValue();
				 RegularTimePeriod curperiod = record.getPeriod();
				 LocalDate curstart = curperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				 LocalDate curend = curperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				 
				 double[] dr = {recordvalue};
				 StockDoublePoint sdp = new StockDoublePoint (curend, dr);
				 amozhanbiset.add(sdp);
		 	}
			 
			 if(amozhanbiset.size() < 5)
				 return ;
//			 KMeansPlusPlusClusterer<StockDoublePoint> kmeans = new KMeansPlusPlusClusterer<StockDoublePoint>(5); //标准算法
			 FuzzyKMeansClusterer<StockDoublePoint> kmeans = new FuzzyKMeansClusterer<StockDoublePoint>(5,2); //模糊算法
			 List<CentroidCluster<TDXNodesXPeriodExternalData.StockDoublePoint>> result = kmeans.cluster(amozhanbiset);
			 for(int i =0 ; i<result.size(); i++) {
				 CentroidCluster<StockDoublePoint> temp = result.get(i);
				 List<StockDoublePoint> templist = temp.getPoints();
				 for(int j=0; j<templist.size(); j++) {
					 double[] pr = ((StockDoublePoint)templist.get(j)).getPoint();
					 LocalDate pl = ((StockDoublePoint)templist.get(j)).getPointLocalDate();
					 java.sql.Date lastdayofweek =  java. sql. Date. valueOf(pl);
					 org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(lastdayofweek);
					 amozbclusterfromapache.add(wknum, i);
				 }
			 }

			 return;
		 }
		 public void calulateSpecificDateAMOZhanBiApacheMathKLearnResult (LocalDate date)
		 {
			 amozbclusterfromapache = null;
			 amozbclusterfromapache = new TimeSeries("amozbclusterfromapache");
			 
			 java.sql.Date lastdayofweek =  java. sql. Date. valueOf(date);
			 org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(lastdayofweek);
			 Integer dateindex = this.nodeamozhanbi.getIndex(wknum);
			 if(dateindex == null || dateindex <0)
				 return;
			 
			 java.util.Collection<StockDoublePoint> amozhanbiset = new HashSet<> ();
			 for(int i=0; i<= dateindex; i++ ) {
				 TimeSeriesDataItem record = this.nodeamozhanbi.getDataItem(i);
				 double recordvalue = record.getValue().doubleValue();
				 RegularTimePeriod curperiod = record.getPeriod();
				 LocalDate curstart = curperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				 LocalDate curend = curperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				 
				 double[] dr = {recordvalue};
				 StockDoublePoint sdp = new StockDoublePoint (curend, dr);
				 amozhanbiset.add(sdp);
		 	}
			 
			 if(amozhanbiset.size() < 5)
				 return ;
			 
			 FuzzyKMeansClusterer<StockDoublePoint> kmeans = new FuzzyKMeansClusterer<StockDoublePoint>(5,2); //模糊算法
			 List<CentroidCluster<TDXNodesXPeriodExternalData.StockDoublePoint>> result = kmeans.cluster(amozhanbiset);
//			 for(int i =0 ; i<result.size(); i++) {
//				 CentroidCluster<StockDoublePoint> temp = result.get(i);
//				 List<StockDoublePoint> templist = temp.getPoints();
//				 for(int j=0; j<templist.size(); j++) {
//					 double[] pr = ((StockDoublePoint)templist.get(j)).getPoint();
//					 LocalDate pl = ((StockDoublePoint)templist.get(j)).getPointLocalDate();
//					 java.sql.Date plsqldate =  java. sql. Date. valueOf(pl);
//					 org.jfree.data.time.Week plwknum = new org.jfree.data.time.Week(plsqldate);
//					 amozbclusterfromapache.add(plwknum, i);
//				 }
//			 }
			 
			 int curclusterindex = -1;
			 for(int i =0 ; i<result.size(); i++) {
				 CentroidCluster<StockDoublePoint> temp = result.get(i);
				 List<StockDoublePoint> templist = temp.getPoints();
				 for(int j=0; j<templist.size(); j++) {
					 LocalDate pl = ((StockDoublePoint)templist.get(j)).getPointLocalDate().with(DayOfWeek.FRIDAY);
					 if(pl.equals(date)) {
						 curclusterindex = i;
						 break;
					 }
				 }
				 if(curclusterindex != -1)
					 break;
			 }
			 
			 if(curclusterindex == -1)
				 return;
			 
			 CentroidCluster<StockDoublePoint> curdateincluster = result.get(curclusterindex);
			 List<StockDoublePoint> curdateinlist = curdateincluster.getPoints();
			 if(curdateinlist.size() >= dateindex * 0.45) { //如果目标list数目过多，要做二次klearning
				 FuzzyKMeansClusterer<StockDoublePoint> kmeansagain = new FuzzyKMeansClusterer<StockDoublePoint>(3,2); //模糊算法
				 List<CentroidCluster<TDXNodesXPeriodExternalData.StockDoublePoint>> resultagain = kmeansagain.cluster(curdateinlist);
				 for(int i = 0 ; i<resultagain.size(); i++) {
					 CentroidCluster<StockDoublePoint> temp = resultagain.get(i);
					 List<StockDoublePoint> templist = temp.getPoints();
					 for(int j=0; j<templist.size(); j++) {
						 LocalDate pl = ((StockDoublePoint)templist.get(j)).getPointLocalDate();
						 java.sql.Date plsqldate =  java. sql. Date. valueOf(pl);
						 org.jfree.data.time.Week plwknum = new org.jfree.data.time.Week(plsqldate);
						 amozbclusterfromapache.add(plwknum, i);
					 }
				 }
			 } else { //没有过多，就直接保存
				 for(int i=0; i <curdateinlist.size(); i++) {
					 LocalDate pl = ((StockDoublePoint)curdateinlist.get(i)).getPointLocalDate();
					 java.sql.Date plsqldate =  java. sql. Date. valueOf(pl);
					 org.jfree.data.time.Week plwknum = new org.jfree.data.time.Week(plsqldate);
					 amozbclusterfromapache.add(plwknum, curclusterindex);
				 }
			 }

			 return;
		 }
		 public Integer getSpecificDateAMOZhanBiApacheMathKLearnClusteringID (LocalDate date)
		 {
			 java.sql.Date lastdayofweek =  java. sql. Date. valueOf(date);
			 org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(lastdayofweek);
			 TimeSeriesDataItem dataitem = this.amozbclusterfromapache.getDataItem(wknum);
			 try {
				 Integer clusteringid = dataitem.getValue().intValue();
				 return clusteringid;
			 } catch (java.lang.NullPointerException e) {
				 return null;
			 }
			 
		 }
		 
		 class StockDoublePoint extends DoublePoint
		 {
			private LocalDate pointdate;

			public StockDoublePoint( LocalDate date , double[] point) {
				super(point);
				this.pointdate = date;
			}
			
			public LocalDate getPointLocalDate ()
			{
				return this.pointdate;
			}
			 
		 }
		 
}

//		 if(this.nodeperiodtype == NodeGivenPeriodDataItem.WEEK) {
//				LocalDate mondayday = startdate.with(DayOfWeek.MONDAY);
//				return mondayday;
//			} else if(this.nodeperiodtype == NodeGivenPeriodDataItem.DAY) {
//				return startdate;
//			}