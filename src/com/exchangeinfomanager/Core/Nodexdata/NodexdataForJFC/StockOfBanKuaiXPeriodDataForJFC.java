package com.exchangeinfomanager.Core.Nodexdata.NodexdataForJFC;

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

import com.exchangeinfomanager.Core.Nodes.DaPan;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.google.common.collect.Multimap;



public class StockOfBanKuaiXPeriodDataForJFC implements NodeXPeriodData
{
	
	private TimeSeries stockamo;
	
	public StockOfBanKuaiXPeriodDataForJFC (String nodecode, String bkcode,String nodeperiodtype1) 
	{
		this.nodecode = nodecode;
		this.nodeperiodtype = nodeperiodtype1;
		
		stockamo = new TimeSeries(nodeperiodtype1);
	}
	
	private String nodeperiodtype;
	private String nodecode;
	
//	@Override
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErChangeGrowthRateOfSuperBanKuai(java.time.LocalDate)
	 * 和其他该函数不同的地方就是这里是板块的成交量而不是大盘的成交�?
	 */
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(TDXNodes superbk,LocalDate requireddate,int difference) 
	{
			TimeSeriesDataItem curcjlrecord = this.stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate) );
			if( curcjlrecord == null) 
				return null;
			
			//判断上级板块(大盘或�?�板�?)是否缩量,�?以了没有比较的意义，直接返回-100�?
			String nodept = getNodeperiodtype();
			NodeXPeriodData bkxdata = superbk.getNodeXPeroidData(nodept);
			Double bkcjediff = bkxdata.getChengJiaoErDifferenceWithLastPeriod(requireddate);
			if( bkcjediff == null || bkcjediff < 0   ) {//板块缩量�?
				return -100.0;
			}
			
			
			int index = this.stockamo.getIndex( getJFreeChartFormateTimePeriod(requireddate) );
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
//			expectedate = requireddate.plus(difference,ChronoUnit.MONTHS);
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
	public Double getChengJiaoEr(LocalDate requireddate) {
		TimeSeriesDataItem curcjlrecord = null;
//		if(difference >=0 )
			curcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate));
		
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
	public Boolean isLocalDateReachFristDayInHistory(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setShangShiRiQi(LocalDate requireddate) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Boolean hasFxjgInPeriod(LocalDate requireddate) {
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
	public Integer getExchangeDaysNumberForthePeriod(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChenJiaoErZhanBi(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChenJiaoLiangZhanBi(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getIndexOfSpecificDateOHLCData(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getSpecificOHLCZhangDieFu(LocalDate requireddate) {
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
	public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChengJiaoLiang(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChenJiaoLiangDifferenceWithLastPeriod(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double[] getNodeOhlcSMA(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getNodeXDataInHtml(TDXNodes superbk, LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getAverageDailyChengJiaoErOfWeek(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getSpecificTimeRangeOHLCHightestZhangFu(LocalDate requiredstart, LocalDate requiredend) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String[] getNodeXDataCsvData(TDXNodes superbk, LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void resetAllData() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Double getChengJiaoErDailyAverageDifferenceWithLastPeriod(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public LocalDate getLocalDateOfSpecificIndexOfOHLCData(Integer index) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public LocalDate getAmoRecordsStartDate() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public LocalDate getAmoRecordsEndDate() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double[] getNodeOhlcMA(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void calNodeOhlcMA() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void calNodeAMOMA() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Double[] getNodeAMOMA(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getAverageDailyChenJiaoErGrowingRate(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChengJiaoLiangDailyAverageDifferenceWithLastPeriod(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
//	@Override
//	public Boolean checkMAsRelationShip(String maformula, LocalDate requireddate) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	@Override
	public Object getNodeDataByKeyWord(String keyword, LocalDate date, String... maformula) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void removeNodeDataFromSpecificDate(LocalDate requireddate) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setNotCalWholeWeekMode(LocalDate date) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public LocalDate isInNotCalWholeWeekMode() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getZhangTingTongJi(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getDieTingTongJi(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getQueKouTongJiOpenUp(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getQueKouTongJiOpenDown(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getQueKouTongJiHuiBuUp(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getQueKouTongJiHuiBuDown(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChenJiaoErZhanBiGrowthRateForDaPan(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getChenJiaoErZhanBiMaxWeekForDaPan(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getChenJiaoErZhanBiMinWeekForDaPan(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getChenJiaoErZhanBiMinestWeekForDaPanInSpecificPeriod(LocalDate requireddate, int checkrange) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChenJiaoLiangZhanBiGrowthRateForDaPan(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getChenJiaoLiangZhanBiMaxWeekForDaPan(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getChenJiaoLiangZhanBiMinWeekForDaPan(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getChenJiaoErMaxWeek(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(TDXNodes superbk, LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage(TDXNodes superbk, LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getAverageDailyChenJiaoErMaxWeek(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getAverageDailyChenJiaoErLianXuFangLiangPeriodNumber(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getChenJiaoLiangMaxWeek(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuai(TDXNodes superbk, LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuaiOnDailyAverage(TDXNodes superbk,
			LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getAverageDailyChengJiaoLiangOfWeek(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getAverageDailyChenJiaoLiangMaxWeek(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Boolean checkCloseComparingToMAFormula(LocalDate requireddate, String maformula) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Multimap<LocalDate, LocalDate> isMacdButtomDivergenceInSpecificMonthRange(LocalDate requireddate,
			int monthrange) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Multimap<LocalDate, LocalDate> isMacdTopDivergenceInSpecificMonthRange(LocalDate requireddate,
			int monthrange) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public OHLCSeries getOHLCData() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public TimeSeries getAMOData() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getChenJiaoErZhanBiDpMaxWkLianXuFangLiangPeriodNumber(LocalDate requireddate, int settindpgmaxwk) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getAverageDailyChenJiaoLiangGrowingRate(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void addDaPanChenJiaoErZhanBiGrowingRate(LocalDate requireddate, Double cjezbgr) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addDaPanChenJiaoLiangZhanBiGrowingRate(LocalDate requireddate, Double cjezbgr) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public TimeSeries getVOLData() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double[] getSpcificDateOHLCData(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getOHLCLianXuYangXianPeriodNumber(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getThroughAnalysisConcolusionResultOfNodeData(TDXNodes superbk, LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	

	
}