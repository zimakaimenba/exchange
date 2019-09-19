package com.exchangeinfomanager.nodes.stocknodexdata;

import java.time.LocalDate;
import java.util.List;

import org.jfree.data.time.RegularTimePeriod;

import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

public interface NodeXPeriodData 
{
	public void addNewXPeriodData (NodeGivenPeriodDataItem kdata);
	
	public Boolean isLocalDateReachFristDayInHistory (LocalDate requireddate,int difference);
	public void setShangShiRiQi (LocalDate requireddate);
	
	public String getNodeCode ();
	public String getNodeperiodtype() ;
	
	public Boolean hasFxjgInPeriod (LocalDate requireddate,int difference);
	public void addFxjgToPeriod (RegularTimePeriod period,Integer fxjg);
	
	public void resetQueKouTongJiJieGuo ();
	public void addQueKouTongJiJieGuo (LocalDate qkdate,
			Integer nodeqktjopenup1,Integer nodeqktjhuibuup1,Integer nodeqktjopendown1,Integer nodeqktjhuibudown1,
			Boolean addbasedoncurdata);
	public List<QueKou> getPeriodQueKou ();
	public void setPeriodQueKou (List<QueKou> qkl);
	
	public void addZhangDieTingTongJiJieGuo (LocalDate tjdate,Integer nodeztnum,Integer nodedtnum,Boolean addbasedoncurdata);
	public Integer getZhangTingTongJi (LocalDate requireddate, Integer difference);
	public Integer getDieTingTongJi (LocalDate requireddate, Integer difference);
	public Integer getQueKouTongJiOpenUp (LocalDate requireddate, Integer difference);
	public Integer getQueKouTongJiOpenDown (LocalDate requireddate, Integer difference);
	public Integer getQueKouTongJiHuiBuUp (LocalDate requireddate, Integer difference);
	public Integer getQueKouTongJiHuiBuDown (LocalDate requireddate, Integer difference);
	public  LocalDate  getQueKouRecordsStartDate ();
	public  LocalDate  getQueKouRecordsEndDate ();
	
	public Integer getExchangeDaysNumberForthePeriod (LocalDate requireddate,int difference);
	
	public Double getChenJiaoErZhanBi (LocalDate requireddate,int difference);
	public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate,int difference);
	public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference);
	public Integer getChenJiaoErZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate,int difference);
	
	public Double getChenJiaoLiangZhanBi(LocalDate requireddate, int difference);
	public Double getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate, int difference);
	public Integer getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference);
	public Integer getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate, int difference);
	
//	public  org.ta4j.core.TimeSeries getOHLCData ();
//	public LocalDate getOhlcRecordsStartDate ();
//	public LocalDate getOhlcRecordsEndDate ();
	public Integer getIndexOfSpecificDateOHLCData (LocalDate requireddate,int difference);
	public Double getSpecificOHLCZhangDieFu (LocalDate requireddate,int difference);
	
	public LocalDate getOHLCRecordsStartDate ();
	public LocalDate getOHLCRecordsEndDate ();
	
	public Double getChengJiaoEr (LocalDate requireddate,int difference);
	public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate,int difference);
	public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference);
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai (TDXNodes superbk, LocalDate requireddate,int difference);
	public Integer getCjeLianXuFangLiangPeriodNumber (LocalDate requireddate,int difference,int settindpgmaxwk);
	
	public Double getChengJiaoLiang(LocalDate requireddate, int difference);
	public Double getChenJiaoLiangDifferenceWithLastPeriod(LocalDate requireddate, int difference);
	public Integer getChenJiaoLiangMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference);
	public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuai(TDXNodes superbk, LocalDate requireddate,	int difference);
	public Integer getCjlLianXuFangLiangPeriodNumber(LocalDate requireddate, int difference, int settindpgmaxwk);
	
	public Double[] getNodeOhlcSMA (LocalDate  requireddate,int difference);
	public Boolean checkCloseComparingToMAFormula (String maformula, LocalDate requireddate, int difference);
	
	public String getNodeXDataInHtml(TDXNodes superbk,LocalDate requireddate, int difference);
}
