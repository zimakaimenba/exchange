package com.exchangeinfomanager.bankuaifengxi;

/*
 * ���û����ø�����ʾ��׼�󣬸���֪ͨ��������ÿ��jtable����PAENL������ʾ���������ĸ��ɻ���
 * ����table��˵���ĸ����������á�����PANEL��˵��ֻ�ú���������
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
