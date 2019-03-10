package com.exchangeinfomanager.bankuaifengxi;

/*
 * 当用户设置高亮显示标准后，负责通知板块分析的每个jtable或者PAENL高亮显示符合条件的个股或板块
 * 对于table来说，四个参数都有用。对于PANEL来说，只用后三个参数
 * Integer cjezbdpmax,
 * Integer cjezbdpmax, 
 * Double cje, Integer 
 * cjemax,Double shoowhsl
 */
public interface BarChartHightLightFxDataValueListener 
{
	void hightLightFxValues(Integer cjezbtoupleveldpmaxwk, Integer cjezbtouplevelbkmaxwk,  Double cjemin, 
							Double cjemax, Integer cjemaxwk,Double shoowhsl,Double ltszmin, Double ltszmax); //FOR TABLE
	void hightLightFxValues(Integer cjezbtoupleveldpmaxwk, Double cjemin, Double cjemax, Integer cjemaxwk,Double shoowhsl); //FOR PANEL
}
