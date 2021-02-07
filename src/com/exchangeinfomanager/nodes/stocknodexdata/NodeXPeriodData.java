package com.exchangeinfomanager.nodes.stocknodexdata;

import java.time.LocalDate;
import java.util.List;

import org.jfree.data.time.RegularTimePeriod;

import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.google.common.collect.Multimap;

public interface NodeXPeriodData 
{
	public void addNewXPeriodData (NodeGivenPeriodDataItem kdata);
	
	public void resetAllData ();
		
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
	public Integer getChenJiaoErZhanBiMinestWeekOfSuperBanKuaiInSpecificPeriod(LocalDate requireddate,int difference, int checkrange);
	
	public Double getChenJiaoLiangZhanBi(LocalDate requireddate, int difference);
	public Double getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate, int difference);
	public Integer getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference);
	public Integer getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate, int difference);
	
//	public  org.ta4j.core.TimeSeries getOHLCData ();
	public Integer getIndexOfSpecificDateOHLCData (LocalDate requireddate,int difference);
	public LocalDate getLocalDateOfSpecificIndexOfOHLCData (Integer index);
	public Double getSpecificOHLCZhangDieFu (LocalDate requireddate,int difference);
	public Double getSpecificTimeRangeOHLCHightestZhangFu (LocalDate requiredstart,LocalDate requiredend);
	
	public LocalDate getOHLCRecordsStartDate ();
	public LocalDate getOHLCRecordsEndDate ();
	
	public LocalDate getAmoRecordsStartDate ();
	public LocalDate getAmoRecordsEndDate ();
	
	public Double getChengJiaoEr (LocalDate requireddate,int difference);
	public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate,int difference);
	public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference);
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai (TDXNodes superbk, LocalDate requireddate,int difference);
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage (TDXNodes superbk, LocalDate requireddate,int difference);
	public Integer getCjeDpMaxLianXuFangLiangPeriodNumber(LocalDate requireddate,int difference,int settindpgmaxwk);
	public Double getAverageDailyChengJiaoErOfWeek (LocalDate requireddate,int difference);
	public Double getChengJiaoErDailyAverageDifferenceWithLastPeriod (LocalDate requireddate,int difference);
	public Integer getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference);
	public Integer getAverageDailyCjeLianXuFangLiangPeriodNumber (LocalDate requireddate,int difference);
	public Double getAverageDailyChenJiaoErGrowingRate(LocalDate requireddate,int difference);
	
	public Double getChengJiaoLiang(LocalDate requireddate, int difference);
	public Double getChenJiaoLiangDifferenceWithLastPeriod(LocalDate requireddate, int difference);
	public Integer getChenJiaoLiangMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference);
	public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuai(TDXNodes superbk, LocalDate requireddate,	int difference);
	Double getAverageDailyChengJiaoLiangOfWeek(LocalDate requireddate, int difference);
	public Integer getAverageDailyChenJiaoLiangMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference);
//	public Integer getAverageDailyCjlLianXuFangLiangPeriodNumber(LocalDate requireddate, int difference);
//	public Integer getCjlDpMaxLianXuFangLiangPeriodNumber(LocalDate requireddate,int difference,int settindpgmaxwk);
	
	public void calNodeAMOMA ();
	public Double[] getNodeAMOMA (LocalDate  requireddate,int difference);
	
	public void calNodeOhlcMA ();
	public Double[] getNodeOhlcMA (LocalDate  requireddate,int difference);
	public Double[] getNodeOhlcSMA (LocalDate  requireddate,int difference);
	public Boolean checkCloseComparingToMAFormula (String maformula, LocalDate requireddate, int difference);
	
	public Multimap<LocalDate, LocalDate> isMacdButtomDivergenceInSpecificMonthRange (LocalDate  requireddate,int difference, int monthrange);
	public Multimap<LocalDate, LocalDate> isMacdTopDivergenceInSpecificMonthRange (LocalDate  requireddate,int difference, int monthrange);
	
	public String getNodeXDataInHtml(TDXNodes superbk,LocalDate requireddate, int difference);
	
	public String[] getNodeXDataCsvData (TDXNodes superbk, LocalDate requireddate, int difference);
	
	public static String[] NODEXDATACSVDATAHEADLINE = { 		 
			"成交额占比",
		    "成交额占比MaxWk",
			"成交额占比MinWk",
			"成交额占比增长率",
			
			"成交量占比",
		    "成交量占比MaxWk",
			"成交量占比MinWk",
			"成交量占比增长率",

			"OpenUpQueKou",
			 "HuiBuDownQueKou",
			 "OpenDownQueKou",
			 "HuiBuUpQueKou",
			 
			 "涨停",
			 "跌停",
			 
			 "成交额",
			 "周平均成交额",
			 "周平均成交额MaxWK",
			 "成交额大盘贡献率",
			 
			 "成交量",
			 "周平均成交量",
			 "周平均成交量MaxWK",
//			 "成交量大盘贡献率",
			 
			 "涨跌幅",
			 "开盘价",
			 "最高价",
			 "最低价",
			 "收盘价",
			 
			 "周平均成交额增长率"
		};

	
}
