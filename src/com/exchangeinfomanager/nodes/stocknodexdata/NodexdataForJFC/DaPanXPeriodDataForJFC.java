package com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC;

import java.time.LocalDate;
import java.util.List;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.google.common.collect.Multimap;


public class DaPanXPeriodDataForJFC implements NodeXPeriodData
{
	private String nodeperiodtype;
	private String nodecode;
	
	public DaPanXPeriodDataForJFC (String nodeperiodtype1,BanKuai shanghai, BanKuai shenzhen)
	{
		this.nodecode = "000000";
		this.nodeperiodtype = nodeperiodtype1;
		this.shanghai = shanghai;
		this.shenzhen = shenzhen;
	}
	
	@Override
	public String getNodeperiodtype() 
	{
		return this.nodeperiodtype;
	}
	
	private BanKuai shanghai;
	private BanKuai shenzhen;

	/*
	 * 
	 */
	public Double getChengJiaoErDifferenceWithLastPeriod (LocalDate requireddate,int difference)
	{
		Double dapanchaer = null;
		
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Double shcurrecord = shanghaiperiodrecords.getChengJiaoErDifferenceWithLastPeriod(requireddate,difference);
		
		NodeXPeriodData shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
		Double szcurrecord = shenzhenperiodrecords.getChengJiaoErDifferenceWithLastPeriod(requireddate,difference);
		
		try{
			dapanchaer = szcurrecord + shcurrecord ;
		} catch (java.lang.NullPointerException e) {
			dapanchaer = 0.0;
			e.printStackTrace();
		}
		return dapanchaer ;
	}

	@Override
	public Double getChengJiaoEr(LocalDate requireddate, int difference) 
	{
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Double shcurrecord = shanghaiperiodrecords.getChengJiaoEr(requireddate,difference);
		
		NodeXPeriodData shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
		Double szcurrecord = shenzhenperiodrecords.getChengJiaoEr(requireddate,difference);
		
		Double dapancje = null;
		try {
		 dapancje = shcurrecord + szcurrecord;
		} catch (java.lang.NullPointerException e) {
//			System.out.println(requireddate);
		}
		return dapancje;
	}


	@Override
	public Integer getExchangeDaysNumberForthePeriod(LocalDate requireddate, int difference) 
	{
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Integer exchangedays = shanghaiperiodrecords.getExchangeDaysNumberForthePeriod(requireddate,difference);
		return exchangedays;
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
	public String getNodeCode() {
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
	public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(TDXNodes superbk, LocalDate requireddate,
			int difference) {
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

//	@Override
//	public Double getSpecificTimeHuanShouLv(LocalDate requireddate, int difference) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Double getSpecificTimeZongShiZhi(LocalDate requireddate, int difference) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Double getSpecificTimeHighestZhangDieFu(LocalDate requireddate, int difference) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Double getSpecificTimeLowestZhangDieFu(LocalDate requireddate, int difference) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Double getSpecificTimeLiuTongShiZhi(LocalDate requireddate, int difference) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	

}

