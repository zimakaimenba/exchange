package com.exchangeinfomanager.nodes.nodexdata;

import java.time.LocalDate;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;


public class DaPanNodeXPeriodData implements NodeXPeriodDataBasic
{
	private String nodeperiodtype;
	private String nodecode;
	
	public DaPanNodeXPeriodData (String nodeperiodtype1,BanKuai shanghai, BanKuai shenzhen)
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
	 * ����ָ�����ں������ڵĳɽ�����
	 */
	public Double getChengJiaoErDifferenceWithLastPeriod (LocalDate requireddate,int difference)
	{
		Double dapanchaer = null;
		
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodDataBasic shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Double shcurrecord = shanghaiperiodrecords.getChengJiaoErDifferenceWithLastPeriod(requireddate,difference);
		
		NodeXPeriodDataBasic shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
		Double szcurrecord = shenzhenperiodrecords.getChengJiaoErDifferenceWithLastPeriod(requireddate,difference);
		
		dapanchaer = szcurrecord + shcurrecord ;
		return dapanchaer ;
	}

	@Override
	public void addNewXPeriodData(TDXNodeGivenPeriodDataItem kdata) {
		// TODO Auto-generated method stub
		
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
	public Boolean hasRecordInThePeriod(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate,int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(TDXNodes superbk, LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getChengJiaoEr(LocalDate requireddate, int difference) 
	{
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodDataBasic shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Double shcurrecord = shanghaiperiodrecords.getChengJiaoEr(requireddate,difference);
		
		NodeXPeriodDataBasic shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
		Double szcurrecord = shenzhenperiodrecords.getChengJiaoEr(requireddate,difference);
		
		Double dapancje = null;
		try {
		 dapancje = shcurrecord + szcurrecord;
		} catch (java.lang.NullPointerException e) {
			System.out.println(requireddate);
		}
		return dapancje;
	}

//	@Override
//	public TimeSeries getRangeChengJiaoEr(LocalDate requiredstart, LocalDate requiredend) {
//		// TODO Auto-generated method stub
//		return null;
//	}

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
		return false;
	}

	@Override
	public void addFxjgToPeriod(RegularTimePeriod period, Integer fxjg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Double getChenJiaoErZhanBi(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getExchangeDaysNumberForthePeriod(LocalDate requireddate, int difference) 
	{
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodDataBasic shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Integer exchangedays = shanghaiperiodrecords.getExchangeDaysNumberForthePeriod(requireddate,difference);
		return exchangedays;
	}

	@Override
	public Integer getChenJiaoErZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OHLCSeries getOHLCData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNodeCode()
	{
		// TODO Auto-generated method stub
		return this.nodecode;
	}

	@Override
	public Integer getLianXuFangLiangPeriodNumber(LocalDate requireddate,int difference,int settindpgmaxwk) {
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

//	@Override
//	public void addNewXPeriodData(com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem kdata) {
//		// TODO Auto-generated method stub
//		
//	}

//	@Override
//	public TimeSeries getChengJiaoEr() {
//		// TODO Auto-generated method stub
//		return null;
//	}


}

