package com.exchangeinfomanager.nodes.nodexdata;

import java.time.LocalDate;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.nodes.TDXNodes;

public interface NodeXPeriodDataBasic 
{
	public String getNodeperiodtype();
	
	/*
	 * 获取数据
	 */
//	public ArrayList<ChenJiaoZhanBiInGivenPeriod> getNodePeriodRecords ();
	 String getNodeCode ();
	
	 Double getChengJiaoEr (LocalDate requireddate,int difference);
	 Double getChenJiaoErZhanBi (LocalDate requireddate,int difference);
	
	 void addNewXPeriodData (TDXNodeGivenPeriodDataItem kdata);

	 LocalDate getAmoRecordsStartDate ();
	 LocalDate getAmoRecordsEndDate ();
	 LocalDate getKXxianRecordsStartDate ();
	 LocalDate getKXxianRecordsEndDate ();
	
	Boolean hasRecordInThePeriod (LocalDate requireddate, int difference); 
	/*
	 * 在交易记录中找到对应周/日的位置,difference是偏移量，
	 */
//	protected Integer getRequiredRecordsPostion (LocalDate requireddate,int difference)

	/*
	 * 获得指定周的记录
	 */
//	TDXNodeGivenPeriodDataItem getSpecficRecord(LocalDate requireddate, int difference);
	/*
	 * 计算指定周期和上周期的成交额差额，适合stock/bankuai，dapan有自己的计算方法
	 */
	Double getChengJiaoErDifferenceWithLastPeriod (LocalDate requireddate,int difference);
	/*
	 * 对上级板块的成交额占比增速
	 */
	Double  getChenJiaoErZhanBiGrowthRateOfSuperBanKuai (LocalDate requireddate,int difference);
	/*
	 * 对上级板块的成交额占比是多少周内的最大值
	 */
	Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai (LocalDate requireddate,int difference);
	/*
	 * 对上级板块的成交额占比是多少周内的最小值
	 */
	Integer getChenJiaoErZhanBiMinWeekOfSuperBanKuai (LocalDate requireddate,int difference);
	/*
	 * 计算成交额变化贡献率，即成交额的变化占整个上级板块成交额增长量的比率
	 */
	Double getChenJiaoErChangeGrowthRateOfSuperBanKuai (TDXNodes superbk, LocalDate requireddate,int difference);
	/*
	 * 成交额是多少周内的最大
	 * 
	 */
	Integer getChenJiaoErMaxWeekOfSuperBanKuai (LocalDate requireddate,int difference);
	/*
	 * 
	 */
//	TimeSeries getRangeChengJiaoEr (LocalDate requiredstart,LocalDate requiredend);
	/*
	 * 
	 */
//	TimeSeries getChengJiaoEr ();
	/*
	 * 
	 */
	public OHLCSeries getOHLCData ();
	/*
	 * 
	 */
	TimeSeries getRangeChengJiaoErZhanBi (LocalDate requiredstart,LocalDate requiredend);
	/*
	 * 
	 */
//	public OHLCSeries getRangeOHLCData (LocalDate requiredstart,LocalDate requiredend);
	/*
	 * 
	 */
	public OHLCItem getSpecificDateOHLCData (LocalDate requireddate,int difference);
	/*
	 * 
	 */
	public Boolean hasFxjgInPeriod (LocalDate requireddate,int difference);
	/*
	 * 
	 */
	public void addFxjgToPeriod (RegularTimePeriod period,Integer fxjg);
	/*
	 * 
	 */
	public Integer getExchangeDaysNumberForthePeriod (LocalDate requireddate,int difference);
	/*
	 * 一次性计算所有数据
	 */
//	ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate (LocalDate requireddate);
	/*
	 * 
	 */
	public Integer getLianXuFangLiangPeriodNumber (LocalDate requireddate,int difference,int settindpgmaxwk);
	


}