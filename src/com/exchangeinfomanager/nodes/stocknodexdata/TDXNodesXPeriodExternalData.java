package com.exchangeinfomanager.nodes.stocknodexdata;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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

import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.FormatDoubleToShort;
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
	private static final String Collection = null;
	private Logger logger = Logger.getLogger(TDXNodesXPeriodExternalData.class);
	
	public TDXNodesXPeriodExternalData (String nodecode,String nodeperiodtype1)
	{
		this.nodecode = nodecode;
		this.nodeperiodtype = nodeperiodtype1;
		
		nodeamozhanbi = new TimeSeries(nodeperiodtype1);
		nodevolzhanbi = new TimeSeries(nodeperiodtype1);
		
		nodeexchangedaysnumber = new TimeSeries(nodeperiodtype1);
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
	
	private LocalDate lastdayofbxfx; //为不完整周分析准备的。如果为空说明是完整周
	
	public void addNewXPeriodData (NodeGivenPeriodDataItem kdata)
	{
		if(kdata.getNodeToDpChenJiaoErZhanbi() == null) 
			return ;
		
		try {
			nodeamozhanbi.setNotify(false);
			nodeamozhanbi.add(kdata.getJFreeChartPeriod(this.nodeperiodtype),kdata.getNodeToDpChenJiaoErZhanbi(),false);
			
			nodevolzhanbi.setNotify(false);
			nodevolzhanbi.add(kdata.getJFreeChartPeriod(this.nodeperiodtype),kdata.getNodeToDpChenJiaoLiangZhanbi(),false);
		} catch (org.jfree.data.general.SeriesException e) {
		}
		
		try{
			nodeexchangedaysnumber.setNotify(false);
			if(kdata.getExchangeDaysNumber() != null && kdata.getExchangeDaysNumber() != 5) //
				nodeexchangedaysnumber.add(kdata.getJFreeChartPeriod(this.nodeperiodtype),kdata.getExchangeDaysNumber(),false);
		} catch (org.jfree.data.general.SeriesException e) {
		}
		
		try {
			if(nodezhangtingnum == null)
				nodezhangtingnum = new TimeSeries(this.nodeperiodtype);
			if(nodedietingnum == null)
				nodedietingnum = new TimeSeries(this.nodeperiodtype);
			
			if(kdata.getZhangTingNumber() != null && kdata.getZhangTingNumber() !=0 )
				nodezhangtingnum.add(kdata.getJFreeChartPeriod(this.nodeperiodtype), kdata.getZhangTingNumber() );
			
			if(kdata.getDieTingNumber() != null && kdata.getDieTingNumber() !=0 )
				nodedietingnum.add(kdata.getJFreeChartPeriod(this.nodeperiodtype), kdata.getDieTingNumber() );
			
		} catch (org.jfree.data.general.SeriesException e) {
		}
	}
	/*
	 * 
	 */
	public void resetAllData ()
	{
		try {
			nodeamozhanbi.clear(); 
		} catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodevolzhanbi.clear(); 
		} catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodefxjg.clear();
		} catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeexchangedaysnumber.clear();
		}  catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeqktjopenup.clear();
		} catch (java.lang.NullPointerException e) {
			
		}
		try {	
			nodeqktjhuibuup.clear();
		} catch (java.lang.NullPointerException e) {
			
		}
		try {
			nodeqktjopendown.clear();
		} catch (java.lang.NullPointerException e) {
					
		}
		try {
			nodeqktjhuibudown.clear();
		} catch (java.lang.NullPointerException e) {
					
		}
		try {
			qklist.clear();
		} catch (java.lang.NullPointerException e) {
		}
		try {	
			nodezhangtingnum.clear();
		} catch (java.lang.NullPointerException e) {
		}
		try {
			nodedietingnum.clear();
		} catch (java.lang.NullPointerException e) {
		}
		
		firstdayinhistory = null;
	}

	/*
	 * 判断日期是否到了数据库历史纪录的第一条
	 */
	public Boolean isLocalDateReachFristDayInHistory (LocalDate requireddate,int difference)
	{
		if(this.firstdayinhistory == null)
			return null;
		
		RegularTimePeriod requiredperiod = this.getJFreeChartFormateTimePeriodForAMO (requireddate,difference);
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
		
		String nodeperiod = this.getNodeperiodtype();
		RegularTimePeriod period = null;
		if(nodeperiod.equals(NodeGivenPeriodDataItem.WEEK)) { 
			java.sql.Date lastdayofweek = java.sql.Date.valueOf(requireddate);
			period = new org.jfree.data.time.Week (lastdayofweek);
		} else if(nodeperiod.equals(NodeGivenPeriodDataItem.DAY)) {
			java.sql.Date lastdayofweek = java.sql.Date.valueOf(requireddate);
			period = new org.jfree.data.time.Day (lastdayofweek);
		}  else if(nodeperiod.equals(NodeGivenPeriodDataItem.MONTH)) {
//			expectedate = requireddate.plus(difference,ChronoUnit.MONTHS);
		}

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
		
		try {
			nodefxjg.add(period,fxjg);

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
	/*
	 * 
	 */
	public void addZhangDieTingTongJiJieGuo (LocalDate tjdate,Integer nodeztnum,Integer nodedtnum,Boolean addbasedoncurdata)
	{
		if( this.nodezhangtingnum == null)
			this.nodezhangtingnum = new TimeSeries(this.nodeperiodtype);
		
		if( this.nodedietingnum == null)
			this.nodedietingnum = new TimeSeries(this.nodeperiodtype);
		
		RegularTimePeriod period = getJFreeChartFormateTimePeriodForAMO(tjdate,0);
		if(period == null)
			return;
		
		try {
			if(nodeztnum != null  )
			if(addbasedoncurdata) {
				TimeSeriesDataItem dataitem = this.nodezhangtingnum.getDataItem(period);
				if(dataitem == null) 
					nodezhangtingnum.add(period,nodeztnum);
				else {
					int count = dataitem.getValue().intValue();
					nodezhangtingnum.addOrUpdate(period, count + nodeztnum);
				}
			} else
				nodezhangtingnum.addOrUpdate(period,nodeztnum);

		} catch (org.jfree.data.general.SeriesException e) {
			e.printStackTrace();
		}
		
		try {
			if(nodedtnum != null  )
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

		} catch (org.jfree.data.general.SeriesException e) {
			e.printStackTrace();
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
		
		RegularTimePeriod period = getJFreeChartFormateTimePeriodForAMO(qkdate,0);
		if(period == null)
			return;
		
		try {
			if(nodeqktjopenup1 != null  ) //nodeqktjopenup1可以等于0，用来标记统计缺口的开始/结束时间
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
	public Integer getZhangTingTongJi (LocalDate requireddate, Integer difference)
	{
		if(this.nodezhangtingnum == null)
			return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(curperiod == null)
			return null;
		
		TimeSeriesDataItem zttjitem = nodezhangtingnum.getDataItem( curperiod);
		if(zttjitem == null)
			return null;
		else
			 return (Integer)zttjitem.getValue().intValue();			
					
	}
	/*
	 * 
	 */
	public Integer getDieTingTongJi (LocalDate requireddate, Integer difference)
	{
		if(this.nodedietingnum == null)
			return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(curperiod == null)
			return null;
		
		TimeSeriesDataItem dttjitem = nodedietingnum.getDataItem( curperiod);
		if(dttjitem == null)
			return null;
		else
			 return (Integer)dttjitem.getValue().intValue();			
					
	}
	/*
	 * 
	 */
	public Integer getQueKouTongJiOpenUp (LocalDate requireddate, Integer difference)
	{
		if(this.nodeqktjopenup == null)
			return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(curperiod == null )
			return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjopenup.getDataItem(curperiod);
		if(qktjitem == null)
			return null;
		else
			 return (Integer)qktjitem.getValue().intValue(); 
		
	}
	public Integer getQueKouTongJiOpenDown (LocalDate requireddate, Integer difference)
	{
		if(this.nodeqktjopendown == null)
			return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(curperiod == null )
			return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjopendown.getDataItem( curperiod);
		if(qktjitem == null)
			return null;
		else
			 return (Integer)qktjitem.getValue().intValue(); 
		
	}
	public Integer getQueKouTongJiHuiBuUp (LocalDate requireddate, Integer difference)
	{
		if(this.nodeqktjhuibuup == null)
			return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(curperiod == null )
			return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjhuibuup.getDataItem( curperiod);
		if(qktjitem == null)
			return null;
		else
			 return (Integer)qktjitem.getValue().intValue(); 
		
	}
	public Integer getQueKouTongJiHuiBuDown (LocalDate requireddate, Integer difference)
	{
		if(this.nodeqktjhuibudown == null)
			return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(curperiod == null )
			return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjhuibudown.getDataItem( curperiod);
		if(qktjitem == null)
			return null;
		else
			 return qktjitem.getValue().intValue(); 
	}
	/*
	 * 
	 */
	public  LocalDate  getQueKouRecordsStartDate ()
	{ 
		if(nodeqktjopenup == null)
			return null;
		
		if(nodeqktjopenup.getItemCount() == 0)
			return null;

		RegularTimePeriod firstperiod = nodeqktjopenup.getTimePeriod( 0);
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		if(this.nodeperiodtype == NodeGivenPeriodDataItem.WEEK) {
			TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
			LocalDate mondayday = startdate.with(fieldUS, 2);
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
			LocalDate saturday = enddate.with(DayOfWeek.SATURDAY);
			return saturday;
		} else if(this.nodeperiodtype == NodeGivenPeriodDataItem.DAY) {
			return enddate;
		}
		
		return null;
	}
	/*
	 * 
	 */
	public Double getChenJiaoErZhanBi (LocalDate requireddate,int difference)
	{
		if(nodeamozhanbi == null)
			return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(curperiod == null )
			return null;
		
		TimeSeriesDataItem curcjlrecord = null;
		curcjlrecord = nodeamozhanbi.getDataItem( curperiod);
		
		if( curcjlrecord == null) 
			return null;
		else
			return curcjlrecord.getValue().doubleValue();
	}
	/*
	 * 
	 */
	public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate,int difference) 
	{
		RegularTimePeriod expectedperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(expectedperiod == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem(expectedperiod );
		if( curcjlrecord == null) 
			return null;
		
		int index = this.nodeamozhanbi.getIndex(expectedperiod);
		
		try {
			TimeSeriesDataItem lastcjlrecord = nodeamozhanbi.getDataItem( index -1  );
			if(lastcjlrecord == null) { //休市前还是空，说明要是新板块。板块没有停牌的
				logger.debug(getNodeCode() + "可能是一个新个股或板块");
				return 100.0;
			}
			
			Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
			Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
			Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
			
			return zhanbigrowthrate;
			
		}	catch (java.lang.IndexOutOfBoundsException ex) {
			return 100.0;
		}

	}
	/*
	 * 对上级板块的成交额占比是多少周内的最大值
	 */
	public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference) 
	{
		RegularTimePeriod expectedperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(expectedperiod == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem( expectedperiod);
		if( curcjlrecord == null) 
			return null;
					
		Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
		
		int index = nodeamozhanbi.getIndex(expectedperiod);
		
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
		if(this.nodeamozhanbi == null)
			return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(curperiod == null )
			return null;
		
		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem( curperiod);
		if( curcjlrecord == null) 
			return null;

		Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
		
		int index = nodeamozhanbi.getIndex(curperiod );
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
	  * 计算指定周有几个交易日
	  */
	 public Integer getExchangeDaysNumberForthePeriod (LocalDate requireddate,int difference)
	 {
		 if(nodeexchangedaysnumber == null)
			 return null;
		 
		 RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		 if(curperiod == null )
				return null;
		 
		 TimeSeriesDataItem curdaynumberrecord = this.nodeexchangedaysnumber.getDataItem(curperiod);
		 if( curdaynumberrecord == null) 
				return 5;
		 else
			 return curdaynumberrecord.getValue().intValue();
	 }
	 
	 /*
		 * 
		 */
		public Double getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate, int difference) 
		{
			RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
			if(curperiod == null )
				return null;
			
			TimeSeriesDataItem curcjlrecord = this.nodevolzhanbi.getDataItem( curperiod);
			if( curcjlrecord == null) 
				return null;
			
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
				logger.debug(getNodeCode() + "占比增速" + requireddate.toString() + zhanbigrowthrate);
				
				return zhanbigrowthrate;
				
			}	catch (java.lang.IndexOutOfBoundsException ex) {
				return 100.0;
			}

		}
		/*
		 * 
		 */
		public Integer getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) 
		{
			RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
			if(curperiod == null )
				return null;
			
			TimeSeriesDataItem curcjlrecord = this.nodevolzhanbi.getDataItem( curperiod);
			if( curcjlrecord == null) 
				return null;
						
			Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
			
			int index = nodevolzhanbi.getIndex(curperiod );
			
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
		/*
		 * 
		 */
		public Integer getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate, int difference) 
		{
			if(this.nodevolzhanbi == null)
				return null;
			
			RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
			if(curperiod == null )
				return null;
			
			TimeSeriesDataItem curcjlrecord = this.nodevolzhanbi.getDataItem( curperiod);
			if( curcjlrecord == null) 
				return null;

			Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
			
			int index = nodevolzhanbi.getIndex(curperiod );
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
		/*
		 * 
		 */
		public Double getChenJiaoLiangZhanBi(LocalDate requireddate, int difference) 
		{
			if(this.nodevolzhanbi == null)
				return null;
			
			RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
			if(curperiod == null )
				return null;
			
			TimeSeriesDataItem curcjlrecord = null;
			curcjlrecord = nodevolzhanbi.getDataItem( curperiod);
			
			if( curcjlrecord == null) 
				return null;
			else
				return curcjlrecord.getValue().doubleValue();
		}
		 /*
		  * 
		  */
		 public String[] getNodeXDataCsvData (TDXNodes superbk, LocalDate requireddate, int difference)
		 {
					Double curcjezhanbidata = this.getChenJiaoErZhanBi(requireddate, 0);  //占比
					Integer cjemaxweek = this.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(requireddate,0);//nodefx.getGgbkzhanbimaxweek();
					Integer cjeminweek = this.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(requireddate,0);
					Double cjezbgrowthrate = this.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(requireddate, 0);
					
					Double cjlzhanbidata = this.getChenJiaoLiangZhanBi(requireddate, 0);
					Integer cjlzbmaxwk = this.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(requireddate, 0);
					Integer cjlzbminwk = this.getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(requireddate, 0);
					Double cjlzbgrowthrate = this.getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(requireddate, 0);
		 
					Integer opneupquekou = this.getQueKouTongJiOpenUp(requireddate, 0);
					Integer opendownquekou = this.getQueKouTongJiOpenDown(requireddate, 0);
					Integer huibuupquekou = this.getQueKouTongJiHuiBuUp(requireddate, 0);
					Integer huibudowquekou = this.getQueKouTongJiHuiBuDown(requireddate, 0);
					 
					Integer zhangtingnum = this.getZhangTingTongJi(requireddate, 0);
					Integer dietingnum = this.getDieTingTongJi(requireddate, 0);
					
					String strcurcjezhanbidata = null;
					try {
						strcurcjezhanbidata = curcjezhanbidata.toString();
					} catch (java.lang.NullPointerException e) {
						strcurcjezhanbidata = String.valueOf("0");
					}
					String strcjemaxweek = null;
					try {
						strcjemaxweek = cjemaxweek.toString();
					} catch (java.lang.NullPointerException e) {
						strcjemaxweek = String.valueOf("0");
					}
					String strcjeminweek = null;
					try {
						strcjeminweek = cjeminweek.toString();
					} catch (java.lang.NullPointerException e) {
						strcjeminweek = String.valueOf("0");
					}
					String strcjezbgrowthrate = null;
					try {
						strcjezbgrowthrate = cjezbgrowthrate.toString() ;
						
					} catch (java.lang.NullPointerException e) {
						strcjezbgrowthrate = String.valueOf("0");
					}
					String strcjlzhanbidata = null;
					try {
						strcjlzhanbidata = cjlzhanbidata.toString();
					} catch (java.lang.NullPointerException e) {
						strcjlzhanbidata = String.valueOf("0");
					}
					String strcjlzbmaxwk = null;
					try {
						strcjlzbmaxwk = cjlzbmaxwk.toString();
					} catch (java.lang.NullPointerException e) {
						strcjlzbmaxwk = String.valueOf("0");
					}
					String strcjlzbminwk = null;
					try {
						strcjlzbminwk = cjlzbminwk.toString();
					} catch (java.lang.NullPointerException e) {
						strcjlzbminwk = String.valueOf("0");
					}
					String strcjlzbgrowthrate = null;
					try {
						strcjlzbgrowthrate = cjlzbgrowthrate.toString();
					} catch (java.lang.NullPointerException e) {
						strcjlzbgrowthrate = String.valueOf("0");
					}
					String stropneupquekou = null;
					try {
						stropneupquekou = opneupquekou.toString();
					} catch (java.lang.NullPointerException e) {
						stropneupquekou = String.valueOf("0");
					}
					String stropendownquekou = null;
					try {
						stropendownquekou = opendownquekou.toString();
					} catch (java.lang.NullPointerException e) {
						stropendownquekou = String.valueOf("0");
					}
					String strhuibuupquekou = null;
					try {
						strhuibuupquekou = huibuupquekou.toString();
					} catch (java.lang.NullPointerException e) {
						strhuibuupquekou = String.valueOf("0");
					}
					String strhuibudowquekou = null;
					try {
						strhuibudowquekou = huibudowquekou.toString();
							
					} catch (java.lang.NullPointerException e) {
						strhuibudowquekou = String.valueOf("0");
					} 
					String strzhangtingnum = null;
					try {
						strzhangtingnum = zhangtingnum.toString();
					} catch (java.lang.NullPointerException e) {
						strzhangtingnum = String.valueOf("0");
					}
					String strdietingnum = null;
					try {
						strdietingnum = dietingnum.toString();
					} catch (java.lang.NullPointerException e) {
						strdietingnum = String.valueOf("0");
					}

					String[] csvline =  {strcurcjezhanbidata ,
							strcjemaxweek ,
							strcjeminweek ,
							strcjezbgrowthrate ,
					
							strcjlzhanbidata ,
							strcjlzbmaxwk ,
							strcjlzbminwk ,
							strcjlzbgrowthrate ,
		 
							stropneupquekou ,
							stropendownquekou ,
							strhuibuupquekou ,
							strhuibudowquekou ,
					 
							strzhangtingnum ,
							strdietingnum 
					};
						 
					return csvline;
		 }
		
		protected RegularTimePeriod getJFreeChartFormateTimePeriodForAMO (LocalDate requireddate,int difference) 
		{
			if(nodeamozhanbi == null)
				return null;
			
			String nodeperiod = this.getNodeperiodtype();
			RegularTimePeriod period = null;
			if(nodeperiod.equals(NodeGivenPeriodDataItem.WEEK)) { 
				java.sql.Date lastweek = java.sql.Date.valueOf(requireddate);
				period = new org.jfree.data.time.Week (lastweek);
			} else if(nodeperiod.equals(NodeGivenPeriodDataItem.DAY)) {
				java.sql.Date lastdayofweek = java.sql.Date.valueOf(requireddate);
				period = new org.jfree.data.time.Day (lastdayofweek);
			}  else if(nodeperiod.equals(NodeGivenPeriodDataItem.MONTH)) {
//				expectedate = requireddate.plus(difference,ChronoUnit.MONTHS);
			}

			Integer curindex = this.nodeamozhanbi.getIndex(period);
			try {
				if(curindex >= 0 ) {
					TimeSeriesDataItem dataitem = this.nodeamozhanbi.getDataItem(curindex + difference);
					if(dataitem != null) {
						RegularTimePeriod expectedperiod = dataitem.getPeriod();
						return expectedperiod;
					}
				}
			} catch (java.lang.IndexOutOfBoundsException e) {
				e.printStackTrace();
				return null;
			}
			return null;
		}
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
