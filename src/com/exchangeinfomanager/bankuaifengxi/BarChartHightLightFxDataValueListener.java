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
	void hightLightFxValues(Integer cjezbtoupleveldpmax, Integer cjezbtouplevelbkmax,  Double cje, Integer cjemax,Double shoowhsl);
	void hightLightFxValues(Integer cjezbtoupleveldpmax, Double cje, Integer cjemax,Double shoowhsl);
}
