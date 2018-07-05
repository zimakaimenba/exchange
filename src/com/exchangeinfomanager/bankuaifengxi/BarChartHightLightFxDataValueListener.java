package com.exchangeinfomanager.bankuaifengxi;

/*
 * 当用户设置高亮显示标准后，负责通知板块分析的每个jtable高亮显示符合条件的个股或板块
 * Integer cjezbdpmax, 
 * Integer cjezbbkmax, 
 * Double cje, Integer 
 * cjemax,Double shoowhsl
 */
public interface BarChartHightLightFxDataValueListener 
{
	void hightLightFxValues(Integer cjezbdpmax, Integer cjezbbkmax, Double cje, Integer cjemax,Double shoowhsl);
}
