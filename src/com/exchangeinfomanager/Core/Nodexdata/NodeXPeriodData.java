package com.exchangeinfomanager.Core.Nodexdata;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.google.common.collect.Multimap;

public interface NodeXPeriodData 
{
	public void addNewXPeriodData (NodeGivenPeriodDataItem kdata);
	
	public void resetAllData ();
		
	public Boolean isLocalDateReachFristDayInHistory (LocalDate requireddate );
	public void setShangShiRiQi (LocalDate requireddate);
	
	public String getNodeCode ();
	public String getNodeperiodtype() ;
	
	public Boolean hasFxjgInPeriod (LocalDate requireddate );
	public void addFxjgToPeriod (RegularTimePeriod period,Integer fxjg);
	
	public void resetQueKouTongJiJieGuo ();
	public void addQueKouTongJiJieGuo (LocalDate qkdate,
			Integer nodeqktjopenup1,Integer nodeqktjhuibuup1,Integer nodeqktjopendown1,Integer nodeqktjhuibudown1,
			Boolean addbasedoncurdata);
	public List<QueKou> getPeriodQueKou ();
	public void setPeriodQueKou (List<QueKou> qkl);
	
	public void addZhangDieTingTongJiJieGuo (LocalDate tjdate,Integer nodeztnum,Integer nodedtnum,Boolean addbasedoncurdata);
	public Integer getZhangTingTongJi (LocalDate requireddate );
	
	public Integer getDieTingTongJi (LocalDate requireddate );
	
	public Integer getQueKouTongJiOpenUp (LocalDate requireddate );
	public Integer getQueKouTongJiOpenDown (LocalDate requireddate );
	public Integer getQueKouTongJiHuiBuUp (LocalDate requireddate );
	public Integer getQueKouTongJiHuiBuDown (LocalDate requireddate );
	public  LocalDate  getQueKouRecordsStartDate ();
	public  LocalDate  getQueKouRecordsEndDate ();
	
	public Integer getExchangeDaysNumberForthePeriod (LocalDate requireddate );
	
	public Double getChenJiaoErZhanBi (LocalDate requireddate );
	public Double getChenJiaoErZhanBiGrowthRateForDaPan (LocalDate requireddate );
	public void addDaPanChenJiaoErZhanBiGrowingRate (LocalDate requireddate, Double cjezbgr);
	public Integer getChenJiaoErZhanBiMaxWeekForDaPan (LocalDate requireddate );
	public Integer getChenJiaoErZhanBiMinWeekForDaPan (LocalDate requireddate );
	public Integer getChenJiaoErZhanBiMinestWeekForDaPanInSpecificPeriod (LocalDate requireddate , int checkrange);
	public Integer getChenJiaoErZhanBiDpMaxWkLianXuFangLiangPeriodNumber(LocalDate requireddate ,int settindpgmaxwk);
	
	public Double getChenJiaoLiangZhanBi(LocalDate requireddate );
	public Double getChenJiaoLiangZhanBiGrowthRateForDaPan(LocalDate requireddate );
	public void addDaPanChenJiaoLiangZhanBiGrowingRate (LocalDate requireddate, Double cjezbgr);
	public Integer getChenJiaoLiangZhanBiMaxWeekForDaPan(LocalDate requireddate );
	public Integer getChenJiaoLiangZhanBiMinWeekForDaPan(LocalDate requireddate );
	
//	public  org.ta4j.core.TimeSeries getOHLCData ();
	public Integer getIndexOfSpecificDateOHLCData (LocalDate requireddate );
	public LocalDate getLocalDateOfSpecificIndexOfOHLCData (Integer index);
	public Double getSpecificOHLCZhangDieFu (LocalDate requireddate );
	public Double getSpecificTimeRangeOHLCHightestZhangFu (LocalDate requiredstart,LocalDate requiredend);
	
	public OHLCSeries getOHLCData ();
	public LocalDate getOHLCRecordsStartDate ();
	public LocalDate getOHLCRecordsEndDate ();
	
	public TimeSeries getAMOData ();
	public TimeSeries getVOLData ();
	public LocalDate getAmoRecordsStartDate ();
	public LocalDate getAmoRecordsEndDate ();

	public Double getChengJiaoEr (LocalDate requireddate );
	public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate );
	public Integer getChenJiaoErMaxWeek (LocalDate requireddate );
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai (TDXNodes superbk, LocalDate requireddate );
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage (TDXNodes superbk, LocalDate requireddate );
	public Double getAverageDailyChengJiaoErOfWeek (LocalDate requireddate );
	public Double getChengJiaoErDailyAverageDifferenceWithLastPeriod (LocalDate requireddate );
	public Integer getAverageDailyChenJiaoErMaxWeek (LocalDate requireddate );
	public Integer getAverageDailyChenJiaoErLianXuFangLiangPeriodNumber (LocalDate requireddate );
	public Double getAverageDailyChenJiaoErGrowingRate(LocalDate requireddate );
	
	public Double getChengJiaoLiang	(LocalDate requireddate );
	public Double getChenJiaoLiangDifferenceWithLastPeriod (LocalDate requireddate );
	public Integer getChenJiaoLiangMaxWeek (LocalDate requireddate );
	public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuai (TDXNodes superbk, LocalDate requireddate );
	public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuaiOnDailyAverage (TDXNodes superbk, LocalDate requireddate );
	public Double getAverageDailyChengJiaoLiangOfWeek(LocalDate requireddate );
	public Double getChengJiaoLiangDailyAverageDifferenceWithLastPeriod (LocalDate requireddate );
	public Integer getAverageDailyChenJiaoLiangMaxWeek (LocalDate requireddate );
	public Double getAverageDailyChenJiaoLiangGrowingRate (LocalDate requireddate );
//	public Integer getAverageDailyCjlLianXuFangLiangPeriodNumber(LocalDate requireddate );
//	public Integer getCjlDpMaxLianXuFangLiangPeriodNumber(LocalDate requireddate ,int settindpgmaxwk);
	
	public void calNodeAMOMA ();
	public Double[] getNodeAMOMA (LocalDate  requireddate );
	
	public void calNodeOhlcMA ();
	public Double[] getNodeOhlcMA (LocalDate  requireddate );
	public Double[] getNodeOhlcSMA (LocalDate  requireddate );
	public Boolean checkCloseComparingToMAFormula (LocalDate requireddate, String maformula );
//	public Boolean checkMAsRelationShip(String maformula, LocalDate requireddate );
	
	public Multimap<LocalDate, LocalDate> isMacdButtomDivergenceInSpecificMonthRange (LocalDate  requireddate , int monthrange);
	public Multimap<LocalDate, LocalDate> isMacdTopDivergenceInSpecificMonthRange (LocalDate  requireddate , int monthrange);
	
	public String getNodeXDataInHtml(TDXNodes superbk,LocalDate requireddate );
	
	public String[] getNodeXDataCsvData (TDXNodes superbk, LocalDate requireddate );
	public Object getNodeDataByKeyWord( String keyword, LocalDate requireddate, String... maformula);

	public void removeNodeDataFromSpecificDate (LocalDate requireddate );
	
	public static String[] NODEXDATACSVKeyWords = {
			"ChengJiaoErZhanBi",	 "CjeZbDpMaxWk", "CjeZbDpMinWk", "CjeZbGrowRate",
			
			"ChengJiaoLiangZhanBi",	"CjlZbDpMaxWk",	"CjlZbDpMinWk",	"CjlZbGrowRate",
			
			"OpenUpQueKou",	"HuiBuDownQueKou",	"OpenDownQueKou",	"HuiBuUpQueKou",
			
			"ZhangTing", "DieTing",
			
			"ChenJiaoEr",	"AverageChenJiaoEr",	"ChengJiaoErDailyAverageGrowingRate",	"ChengJiaoErDailyAverageDifferenceWithLastPeriod",	"AverageChenJiaoErMaxWeek", 
			
			"ChenJiaoLiang",	"AverageChenJiaoLiang",	"ChengJiaoLiangDailyAverageGrowingRate",	"ChengJiaoLiangDailyAverageDifferenceWithLastPeriod", "AverageChenJiaoLiangMaxWeek" 
	};
	
	public static String[] NODEXDATACSVDATAHEADLINE = { 		 
			"成交额占比", "成交额占比MaxWk",	"成交额占比MinWk",	"成交额占比增长率",
			
			"成交量占比",	"成交量占比MaxWk",	"成交量占比MinWk",	"成交量占比增长率",

			"OpenUpQueKou",	"HuiBuDownQueKou",	"OpenDownQueKou",	"HuiBuUpQueKou",
			 
			 "涨停","跌停",
			 
			 "成交额",	"周平均成交额",	"周日平均成交额增长率",	"周日平均成交额增长",	"周平均成交额MaxWK",
			 
			 "成交量",	"周平均成交量",	"周日平均成交量增长率",	"周日平均成交量增长",	"周平均成交量MaxWK",
			 
			 "涨跌幅","开盘价","最高价","最低价","收盘价",
		};
	
	public static  Object [] OuputFormatControl = {
			new DecimalFormat("%#0.0000"),1,1,new DecimalFormat("%#0.000"),
			new DecimalFormat("%#0.0000"),1,1,new DecimalFormat("%#0.000"),
			1,1,1,1,
			1,1,
			1,1,new DecimalFormat("%#0.000"),1,1,
			1,1,new DecimalFormat("%#0.000"),1,1,
			1,1,1,1,1
	};

	public static  String [] OuputColorControl = {
			"#17202A","#17202A","#17202A","#17202A",
			"#512E5F","#512E5F","#512E5F","#512E5F",
			"#F39C12","#F39C12","#F39C12","#F39C12",
			"#1B2631","#1B2631",
			"#AF7AC5","#AF7AC5","#AF7AC5","#AF7AC5","#AF7AC5",
			"#641E16","#641E16","#641E16","#641E16","#641E16",
			"#1B2631","#1B2631","#1B2631","#1B2631","#1B2631",
	};
	public static  Integer [] OuputToHtmlToolTipControl = {
			1,1,1,1,
			1,1,1,1,
			0,0,0,0,
			0,0,
			1,1,1,1,1,
			1,1,1,1,1,
			1,0,0,1
	};

	public void setNotCalWholeWeekMode(LocalDate date);
	public LocalDate isInNotCalWholeWeekMode();
}
