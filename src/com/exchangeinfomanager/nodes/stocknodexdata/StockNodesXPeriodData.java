package com.exchangeinfomanager.nodes.stocknodexdata;

import java.text.DecimalFormat;
import java.time.LocalDate;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

public interface StockNodesXPeriodData 
{
	public Double getSpecificTimeHuanShouLv (LocalDate requireddate,int difference);
	public Double getSpecificTimeHuanShouLvFree (LocalDate requireddate,int difference);
	public Double getSpecificTimeZongShiZhi (LocalDate requireddate,int difference);
	public void addPeriodHighestZhangDieFu (LocalDate requireddate,Double zhangfu);
	public Double getSpecificTimeHighestZhangDieFu (LocalDate requireddate,int difference);
	public void addPeriodLowestZhangDieFu (LocalDate requireddate,Double zhangfu);
	public Double getSpecificTimeLowestZhangDieFu (LocalDate requireddate,int difference);
	public Double getSpecificTimeLiuTongShiZhi (LocalDate requireddate,int difference);

	public void addGzjlToPeriod (RegularTimePeriod period,Integer fxjg);
	public Integer hasGzjlInPeriod (LocalDate requireddate,int difference);

	public void addMaiRuJiLu (RegularTimePeriod period,Integer fxjg); 
	public Integer hasMaiRuJiLuInPeriod (LocalDate requireddate,int difference);
	
	public void addMaiChuJiLu (RegularTimePeriod period,Integer fxjg) ;
	public Integer hasMaiChuJiLuInPeriod (LocalDate requireddate,int difference);
	
	public Double getAverageDailyHuanShouLvOfWeek (LocalDate requireddate,int difference);
	public static String[] NODEXDATACSVDATAHEADLINE = {
							"换手率",
							"自由流通换手率",
							"周平均流通市值",
							};
	public static  Object [] ouputcontrol = {
			new DecimalFormat("%.3f"),new DecimalFormat("%.3f"),
			new DecimalFormat("#0.000")
			};
	public static  String [] ouputcolor = {
			"#17202A","#17202A","#17202A"
	};
}
