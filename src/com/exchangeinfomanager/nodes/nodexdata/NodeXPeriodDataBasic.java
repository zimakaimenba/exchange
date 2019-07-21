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
	 * ��ȡ����
	 */
	 String getNodeCode ();
	 
	 void addNewXPeriodData (TDXNodeGivenPeriodDataItem kdata);

	 LocalDate getAmoRecordsStartDate ();
	 LocalDate getAmoRecordsEndDate ();
	 LocalDate getKXxianRecordsStartDate ();
	 LocalDate getKXxianRecordsEndDate ();
	 LocalDate getQueKouRecordsStartDate ();
	 LocalDate getQueKouRecordsEndDate ();
	
	 Boolean hasRecordInThePeriod (LocalDate requireddate, int difference); 
	
	 Double getChengJiaoEr (LocalDate requireddate,int difference);
	 Double getChenJiaoErZhanBi (LocalDate requireddate,int difference);
	 
	 Double getChengJiaoLiang (LocalDate requireddate,int difference);
	 Double getChenJiaoLiangZhanBi (LocalDate requireddate,int difference);
	
	 	/*
	 * �ڽ��׼�¼���ҵ���Ӧ��/�յ�λ��,difference��ƫ������
	 */
//	protected Integer getRequiredRecordsPostion (LocalDate requireddate,int difference)

	/*
	 * ���ָ���ܵļ�¼
	 */
//	TDXNodeGivenPeriodDataItem getSpecficRecord(LocalDate requireddate, int difference);
	/*
	 * ����ָ�����ں������ڵĳɽ�����ʺ�stock/bankuai��dapan���Լ��ļ��㷽��
	 */
	Double getChengJiaoErDifferenceWithLastPeriod (LocalDate requireddate,int difference);
	/*
	 * ���ϼ����ĳɽ���ռ������
	 */
	Double  getChenJiaoErZhanBiGrowthRateOfSuperBanKuai (LocalDate requireddate,int difference);
	/*
	 * ���ϼ����ĳɽ���ռ���Ƕ������ڵ����ֵ
	 */
	Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai (LocalDate requireddate,int difference);
	/*
	 * ���ϼ����ĳɽ���ռ���Ƕ������ڵ���Сֵ
	 */
	Integer getChenJiaoErZhanBiMinWeekOfSuperBanKuai (LocalDate requireddate,int difference);
	/*
	 * ����ɽ���仯�����ʣ����ɽ���ı仯ռ�����ϼ����ɽ����������ı���
	 */
	Double getChenJiaoErChangeGrowthRateOfSuperBanKuai (TDXNodes superbk, LocalDate requireddate,int difference);
	/*
	 * �ɽ����Ƕ������ڵ����
	 */
	Integer getChenJiaoErMaxWeekOfSuperBanKuai (LocalDate requireddate,int difference);
	/*
	 * ����ָ�����ں������ڵĳɽ������ʺ�stock/bankuai��dapan���Լ��ļ��㷽��
	 */
	Double getChenJiaoLiangDifferenceWithLastPeriod (LocalDate requireddate,int difference);
	/*
	 * ���ϼ����ĳɽ���ռ������
	 */
	Double  getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai (LocalDate requireddate,int difference);
	/*
	 * ���ϼ����ĳɽ���ռ���Ƕ������ڵ����ֵ
	 */
	Integer getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai (LocalDate requireddate,int difference);
	/*
	 * ���ϼ����ĳɽ���ռ���Ƕ������ڵ���Сֵ
	 */
	Integer getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai (LocalDate requireddate,int difference);
	/*
	 * ����ɽ���仯�����ʣ����ɽ���ı仯ռ�����ϼ����ɽ����������ı���
	 */
	Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuai (TDXNodes superbk, LocalDate requireddate,int difference);
	/*
	 * �ɽ����Ƕ������ڵ����
	 */
	Integer getChenJiaoLiangMaxWeekOfSuperBanKuai (LocalDate requireddate,int difference);
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
	 * һ���Լ�����������
	 */
//	ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate (LocalDate requireddate);
	/*
	 * 
	 */
	public Integer getCjeLianXuFangLiangPeriodNumber (LocalDate requireddate,int difference,int settindpgmaxwk);
	public Integer getCjlLianXuFangLiangPeriodNumber (LocalDate requireddate,int difference,int settindpgmaxwk);
	/*
	 * 
	 */
	public String getNodeXDataInHtml (TDXNodes superbk,LocalDate requireddate,int difference);
	

}