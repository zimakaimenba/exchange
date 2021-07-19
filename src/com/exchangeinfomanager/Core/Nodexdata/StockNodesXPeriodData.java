package com.exchangeinfomanager.Core.Nodexdata;

import java.text.DecimalFormat;
import java.time.LocalDate;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

public interface StockNodesXPeriodData 
{
	public Object getNodeDataByKeyWord( String keyword, LocalDate date, String... maformula);
	public Double getSpecificTimeHuanShouLv (LocalDate requireddate);
	public Double getSpecificTimeHuanShouLvFree (LocalDate requireddate);
	public Double getSpecificTimeZongShiZhi (LocalDate requireddate);
	public void addPeriodHighestZhangDieFu (LocalDate requireddate,Double zhangfu);
	public Double getSpecificTimeHighestZhangDieFu (LocalDate requireddate);
	public void addPeriodLowestZhangDieFu (LocalDate requireddate,Double zhangfu);
	public Double getSpecificTimeLowestZhangDieFu (LocalDate requireddate);
	public Double getSpecificTimeLiuTongShiZhi (LocalDate requireddate);

	public void addGzjlToPeriod (RegularTimePeriod period,Integer fxjg);
	public Integer hasGzjlInPeriod (LocalDate requireddate);

	public void addMaiRuJiLu (RegularTimePeriod period,Integer fxjg); 
	public Integer hasMaiRuJiLuInPeriod (LocalDate requireddate);
	
	public void addMaiChuJiLu (RegularTimePeriod period,Integer fxjg) ;
	public Integer hasMaiChuJiLuInPeriod (LocalDate requireddate);
	
	public Double getAverageDailyHuanShouLvOfWeek (LocalDate requireddate);
	public static String[] NODEXDATACSVDATAHEADLINE = {
							"换手率",
							"自由流通换手率",
							"流通市值",
							"总市值"
							};
	public static  Object [] OuputFormatControl = {
			1,1,
			1,1
			};
	public static  String [] OuputColorControl = {
			"#17202A","#17202A","#17202A","#17202A"
	};
	public static  Integer [] ouputToHtmlToolTipControl = {
			1,1,1,1
	};
}
