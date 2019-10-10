package com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.google.common.collect.Multimap;



public class StockOfBanKuaiXPeriodDataForJFC implements NodeXPeriodData
{
	
	private TimeSeries stockamo;
	private String bkcode;

	public StockOfBanKuaiXPeriodDataForJFC (String nodecode, String bkcode,String nodeperiodtype1) 
	{
		this.nodecode = nodecode;
		this.bkcode = bkcode;
		this.nodeperiodtype = nodeperiodtype1;
		
		stockamo = new TimeSeries(nodeperiodtype1);
	}
	
	private String nodeperiodtype;
	private String nodecode;
	
//	@Override
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErChangeGrowthRateOfSuperBanKuai(java.time.LocalDate)
	 * 和其他该函数不同的地方就是这里是板块的成交量而不是大盘的成交量
	 */
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(TDXNodes superbk,LocalDate requireddate,int difference) 
	{
			TimeSeriesDataItem curcjlrecord = this.stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference) );
			if( curcjlrecord == null) 
				return null;
			
			//判断上级板块(大盘或者板块)是否缩量,所以了没有比较的意义，直接返回-100；
			String nodept = getNodeperiodtype();
			NodeXPeriodData bkxdata = superbk.getNodeXPeroidData(nodept);
			Double bkcjediff = bkxdata.getChengJiaoErDifferenceWithLastPeriod(requireddate,difference);
			if( bkcjediff == null || bkcjediff < 0   ) {//板块缩量，
				return -100.0;
			}
			
			
			int index = this.stockamo.getIndex( getJFreeChartFormateTimePeriod(requireddate,difference) );
			TimeSeriesDataItem lastcjlrecord = stockamo.getDataItem( index - 1);
			if(lastcjlrecord == null) { //休市前还是空，说明是停牌后复牌了
				Double curggcje = curcjlrecord.getValue().doubleValue(); //新板块所有成交量都应该计算入
				return curggcje/bkcjediff;
			}
			
			Double curcje = curcjlrecord.getValue().doubleValue();
			Double lastcje = lastcjlrecord.getValue().doubleValue();
			Double cjechange = curcje - lastcje; //个股成交量的变化
			
			return cjechange/bkcjediff;
		
	}
	/*
	 * 
	 */
	protected RegularTimePeriod getJFreeChartFormateTimePeriod (LocalDate requireddate,int difference) 
	{
		String nodeperiod = this.getNodeperiodtype();
		LocalDate expectedate = null;
		RegularTimePeriod period = null;
		if(nodeperiod.equals(NodeGivenPeriodDataItem.WEEK)) { 
			expectedate = requireddate.plus(difference,ChronoUnit.WEEKS);
			java.sql.Date lastdayofweek = java.sql.Date.valueOf(expectedate);
			period = new org.jfree.data.time.Week (lastdayofweek);
//			period = new org.jfree.data.time.Week (Date.from(expectedate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		} else if(nodeperiod.equals(NodeGivenPeriodDataItem.DAY)) {
			expectedate = requireddate.plus(difference,ChronoUnit.DAYS);
			java.sql.Date lastdayofweek = java.sql.Date.valueOf(expectedate);
			period = new org.jfree.data.time.Day (lastdayofweek);
//			period = new org.jfree.data.time.Day(Date.from(expectedate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}  else if(nodeperiod.equals(NodeGivenPeriodDataItem.MONTH)) {
			expectedate = requireddate.plus(difference,ChronoUnit.MONTHS);
		}
		
		return period;
	}
	@Override
	public String getNodeperiodtype() {
		// TODO Auto-generated method stub
		return this.nodeperiodtype;
	}

	@Override
	public String getNodeCode() {
		// TODO Auto-generated method stub
		return this.nodecode;
	}

	@Override
	public Double getChengJiaoEr(LocalDate requireddate, int difference) {
		TimeSeriesDataItem curcjlrecord = null;
//		if(difference >=0 )
			curcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		
		if( curcjlrecord == null) 
			return null;
		else
			return curcjlrecord.getValue().doubleValue();
	}
	@Override
	public void addNewXPeriodData(NodeGivenPeriodDataItem kdata) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Boolean isLocalDateReachFristDayInHistory(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setShangShiRiQi(LocalDate requireddate) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Boolean hasFxjgInPeriod(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void addFxjgToPeriod(RegularTimePeriod period, Integer fxjg) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void resetQueKouTongJiJieGuo() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addQueKouTongJiJieGuo(LocalDate qkdate, Integer nodeqktjopenup1, Integer nodeqktjhuibuup1,
			Integer nodeqktjopendown1, Integer nodeqktjhuibudown1, Boolean addbasedoncurdata) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<QueKou> getPeriodQueKou() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setPeriodQueKou(List<QueKou> qkl) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addZhangDieTingTongJiJieGuo(LocalDate tjdate, Integer nodeztnum, Integer nodedtnum,
			Boolean addbasedoncurdata) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Integer getZhangTingTongJi(LocalDate requireddate, Integer difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getDieTingTongJi(LocalDate requireddate, Integer difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getQueKouTongJiOpenUp(LocalDate requireddate, Integer difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getQueKouTongJiOpenDown(LocalDate requireddate, Integer difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getQueKouTongJiHuiBuUp(LocalDate requireddate, Integer difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getQueKouTongJiHuiBuDown(LocalDate requireddate, Integer difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public LocalDate getQueKouRecordsStartDate() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public LocalDate getQueKouRecordsEndDate() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getExchangeDaysNumberForthePeriod(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChenJiaoErZhanBi(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getChenJiaoErZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChenJiaoLiangZhanBi(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getIndexOfSpecificDateOHLCData(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getSpecificOHLCZhangDieFu(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public LocalDate getOHLCRecordsStartDate() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public LocalDate getOHLCRecordsEndDate() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getCjeLianXuFangLiangPeriodNumber(LocalDate requireddate, int difference, int settindpgmaxwk) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChengJiaoLiang(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChenJiaoLiangDifferenceWithLastPeriod(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getChenJiaoLiangMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuai(TDXNodes superbk, LocalDate requireddate,
			int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getCjlLianXuFangLiangPeriodNumber(LocalDate requireddate, int difference, int settindpgmaxwk) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double[] getNodeOhlcSMA(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Boolean checkCloseComparingToMAFormula(String maformula, LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Multimap<LocalDate, LocalDate> isMacdButtomDivergenceInSpecificMonthRange(LocalDate requireddate,
			int difference, int monthrange) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getNodeXDataInHtml(TDXNodes superbk, LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
//	@Override
//	public Double getSpecificTimeHuanShouLv(LocalDate requireddate, int difference) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public Double getSpecificTimeZongShiZhi(LocalDate requireddate, int difference) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public Double getSpecificTimeHighestZhangDieFu(LocalDate requireddate, int difference) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public Double getSpecificTimeLowestZhangDieFu(LocalDate requireddate, int difference) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public Double getSpecificTimeLiuTongShiZhi(LocalDate requireddate, int difference) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	@Override
	public Multimap<LocalDate, LocalDate> isMacdTopDivergenceInSpecificMonthRange(LocalDate requireddate,
			int difference, int monthrange) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getAverageDailyChengJiaoErOfWeek(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getSpecificTimeRangeOHLCHightestZhangFu(LocalDate requiredstart, LocalDate requiredend) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String[] getNodeXDataCsvData(TDXNodes superbk, LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	
}