package com.exchangeinfomanager.nodes.nodexdata;

import java.time.LocalDate;

import com.exchangeinfomanager.nodes.DaPan;

import org.apache.log4j.Logger;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;


public class StockNodeXPeriodData extends TDXNodesXPeriodData
{
	public StockNodeXPeriodData (String nodecode, String nodeperiodtype1) 
	{
		super(nodecode , nodeperiodtype1);
		
		stockhuanshoulv = new TimeSeries(nodeperiodtype1);
		stockliutongshizhi = new TimeSeries(nodeperiodtype1);
		stockzongshizhi = new TimeSeries(nodeperiodtype1);
		periodhighestzhangdiefu = new TimeSeries(nodeperiodtype1);
		periodlowestzhangdiefu = new TimeSeries(nodeperiodtype1);
	}
	
	private Logger logger = Logger.getLogger(StockNodeXPeriodData.class);
	
	private  TimeSeries stockhuanshoulv; //换手率
	private  TimeSeries stockliutongshizhi; //流通市值
	private  TimeSeries stockzongshizhi; //总市值
	private  TimeSeries periodhighestzhangdiefu; //最高涨幅
	private  TimeSeries periodlowestzhangdiefu; //最高涨幅
	private  TimeSeries stockgzjl; //关注记录
	
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#hasFxjgInPeriod(java.time.LocalDate, int)
	 */
	public Integer hasGzjlInPeriod (LocalDate requireddate,int difference)
	{
		if(this.stockgzjl == null)
			return null;
		
		TimeSeriesDataItem gzjlitem = stockgzjl.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(gzjlitem == null)
			return null;
		
		Integer value = (Integer)gzjlitem.getValue();
		return value;
	}
	/*
	 * 
	 */
	public void addGzjlToPeriod (RegularTimePeriod period,Integer fxjg) //1锟斤拷锟斤拷锟斤拷锟斤拷注锟斤拷0锟斤拷锟斤拷锟狡筹拷锟斤拷注
	{
		if(this.stockgzjl == null)
			stockgzjl = new TimeSeries(super.getNodeperiodtype() );
		
		try {
			stockgzjl.add(period,fxjg);
//			 addOrUpdate()
		} catch (org.jfree.data.general.SeriesException e) {
//			e.printStackTrace();
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#addNewXPeriodData(com.exchangeinfomanager.asinglestockinfo.TDXNodeGivenPeriodDataItem)
	 */
	public void addNewXPeriodData (TDXNodeGivenPeriodDataItem kdata)
	{
		super.addNewXPeriodData(kdata);
		
		try {
			stockhuanshoulv.setNotify(false);
			stockhuanshoulv.add(kdata.getPeriod(),kdata.getHuanshoulv(),false);
		} catch (org.jfree.data.general.SeriesException e) {
//			System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//			e.printStackTrace();
		} 
		try {
			stockliutongshizhi.setNotify(false);
			stockliutongshizhi.add(kdata.getPeriod(),kdata.getLiutongshizhi(),false);
		} catch (org.jfree.data.general.SeriesException e) {
//			System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//			e.printStackTrace();
		}
		try {	
			stockzongshizhi.setNotify(false);
			stockzongshizhi.add(kdata.getPeriod(),kdata.getZongshizhi(),false);
			
		} catch (org.jfree.data.general.SeriesException e) {
//			System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//			e.printStackTrace();
		}
		try {	
			periodhighestzhangdiefu.setNotify(false);
			periodhighestzhangdiefu.add(kdata.getPeriod(),kdata.getPeriodhighestzhangdiefu(),false);
			
		} catch (org.jfree.data.general.SeriesException e) {
//			System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//			e.printStackTrace();
		}
		try {	
			periodlowestzhangdiefu.setNotify(false);
			periodlowestzhangdiefu.add(kdata.getPeriod(),kdata.getPeriodlowestzhangdiefu(),false);
			
		} catch (org.jfree.data.general.SeriesException e) {
//			System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//			e.printStackTrace();
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
	 */
	public Double getSpecificTimeHuanShouLv (LocalDate requireddate,int difference)
	{
		try{
			TimeSeriesDataItem curhslrecord = stockhuanshoulv.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			Double curhsl = curhslrecord.getValue().doubleValue();
			
			return curhsl;
		} catch (java.lang.NullPointerException e) {
//			e.printStackTrace();
			return null;
		}
		
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
	 */
	public Double getSpecificTimeZongShiZhi (LocalDate requireddate,int difference)
	{
		TimeSeriesDataItem curzszrecord = stockzongshizhi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		Double curzsz = null ;
		try {
			curzsz = curzszrecord.getValue().doubleValue();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(" ");
		}
		
		return curzsz;
	}
	/*
	 * 得到某个时期的涨跌幅
	 */
	public Double getSpecificTimeHighestZhangDieFu (LocalDate requireddate,int difference)
	{
		TimeSeriesDataItem curhighzdfrecord = periodhighestzhangdiefu.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		Double curhzdf = null ;
		try {
			curhzdf = curhighzdfrecord.getValue().doubleValue();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(" ");
		}
		
		return curhzdf;
	}
	/*
	 * 
	 */
	public Double getSpecificTimeLowestZhangDieFu (LocalDate requireddate,int difference)
	{
		TimeSeriesDataItem curlowzdfrecord = periodlowestzhangdiefu.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		Double curlzdf = null ;
		try {
			curlzdf = curlowzdfrecord.getValue().doubleValue();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(" ");
		}
		
		return curlzdf;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
	 */
	public Double getSpecificTimeLiuTongShiZhi (LocalDate requireddate,int difference)
	{
		TimeSeriesDataItem curltszrecord = stockliutongshizhi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		try{
			Double curltsz = curltszrecord.getValue().doubleValue();
			return curltsz;
		} catch (java.lang.NullPointerException e ) {
			return null;
		}
	}

}