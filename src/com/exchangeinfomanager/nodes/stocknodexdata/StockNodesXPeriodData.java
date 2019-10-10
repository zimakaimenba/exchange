package com.exchangeinfomanager.nodes.stocknodexdata;

import java.time.LocalDate;

import org.jfree.data.time.RegularTimePeriod;

public interface StockNodesXPeriodData 
{
	public Double getSpecificTimeHuanShouLv (LocalDate requireddate,int difference);
	public Double getSpecificTimeZongShiZhi (LocalDate requireddate,int difference);
	public Double getSpecificTimeHighestZhangDieFu (LocalDate requireddate,int difference);
	public Double getSpecificTimeLowestZhangDieFu (LocalDate requireddate,int difference);
	public Double getSpecificTimeLiuTongShiZhi (LocalDate requireddate,int difference);
	
	public Integer hasGzjlInPeriod (LocalDate requireddate,int difference);
	public void addGzjlToPeriod (RegularTimePeriod period,Integer fxjg);
	
	public static String[] NODEXDATACSVDATAHEADLINE = {
							"换手率",
							"周平均流通市值",
							};
}
