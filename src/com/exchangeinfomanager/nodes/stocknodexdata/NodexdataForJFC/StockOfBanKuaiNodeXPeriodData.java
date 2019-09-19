package com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.TDXNodeGivenPeriodDataItem;


private class StockOfBanKuaiNodeXPeriodData implements NodeXPeriodDataBasic
{
	
	private TimeSeries stockamo;
	private String bkcode;

	public StockOfBanKuaiNodeXPeriodData (String nodecode, String bkcode,String nodeperiodtype1) 
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
			NodeXPeriodDataBasic bkxdata = superbk.getNodeXPeroidData(nodept);
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
		String nodeperiod = getNodeperiodtype();
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
	public String getNodeperiodtype() {
		// TODO Auto-generated method stub
		return this.nodeperiodtype;
	}

	@Override
	public String getNodeCode() {
		// TODO Auto-generated method stub
		return null;
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
	public Double getChenJiaoErZhanBi(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addNewXPeriodData (TDXNodeGivenPeriodDataItem kdata)
	{
		
		
		try {
			stockamo.setNotify(false);
			stockamo.add(kdata.getPeriod(),kdata.getMyOwnChengJiaoEr(),false);
		} catch (org.jfree.data.general.SeriesException e) {
//			logger.debug(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//			e.printStackTrace();
		}
		

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

	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#hasRecordInThePeriod(java.time.LocalDate, int)
	 */
	public Boolean hasRecordInThePeriod (LocalDate requireddate, int difference) 
	{
		 TimeSeriesDataItem stockamovaule = stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference) );
		 if(stockamovaule != null)
			 return true;
		 else
			 return false;
	}

	@Override
	public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate, int difference) {
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
	public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OHLCSeries getOHLCData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimeSeries getRangeChengJiaoErZhanBi(LocalDate requiredstart, LocalDate requiredend) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public OHLCSeries getRangeOHLCData(LocalDate requiredstart, LocalDate requiredend) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public OHLCItem getSpecificDateOHLCData(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
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
	public Integer getExchangeDaysNumberForthePeriod(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getCjeLianXuFangLiangPeriodNumber(LocalDate requireddate, int difference, int settindpgmaxwk) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getCjlLianXuFangLiangPeriodNumber(LocalDate requireddate, int difference, int settindpgmaxwk) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public LocalDate getKXxianRecordsStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDate getKXxianRecordsEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getChenJiaoLiangDifferenceWithLastPeriod(LocalDate requireddate, int difference) {
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
	public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuai(TDXNodes superbk, LocalDate requireddate,
			int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChenJiaoLiangMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getChengJiaoLiang(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getChenJiaoLiangZhanBi(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNodeXDataInHtml(TDXNodes superbk,LocalDate requireddate, int difference) {
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
	public Boolean checkCloseComparingToMAFormula(String maformula, LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	

}