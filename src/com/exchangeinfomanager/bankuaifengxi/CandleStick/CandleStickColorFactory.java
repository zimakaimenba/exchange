package com.exchangeinfomanager.bankuaifengxi.CandleStick;

import java.awt.Color;

public class CandleStickColorFactory 
{
  //不同的涨跌用不同的K线颜色标记，和TDX一样
	private final static Color colorKeChuangBanZhangTing = new Color(128,0,128);
	private final static Color colorKeChuangBanDaZhang = new Color(255,0,255);
	
  	private final static Color colorZhangTing = new Color(139,0,0);
  	private final static Color colorDaDaZhang = new Color(255,0,0);
  	private final static Color colorDaZhang = new Color(255,140,0);
  	private final static Color colorRaising = new Color(255,215,0);
  	
  	private final static Color colorDie = new Color(154,205,50);
  	private final static Color colorDaDie = new Color(0,255,0);
  	private final static Color colorDaDaDie = new Color(34,139,34);
  	private final static Color colorDieTing = new Color(0,100,0);
  	
  	private final static Color colorKeChuangBanDieTing = new Color(0,139,139);
  	private final static Color colorKeChuangDaDie = new Color(0,255,255);  	
  	

    public CandleStickColorFactory() {}

    public static Color getCandelStickColor(Double zhangdiefu) 
    {
    	if (zhangdiefu == null)
    		return Color.WHITE;
    	else
    	if(zhangdiefu >= 0.2)
    		return colorKeChuangBanZhangTing;
    	else
    	if(zhangdiefu >= 0.106 && zhangdiefu < 0.2)
        	return colorKeChuangBanDaZhang;
        else
        if(zhangdiefu >= 0.095 && zhangdiefu < 0.106 )
        	return colorZhangTing;
        else
        if(zhangdiefu >= 0.075 && zhangdiefu < 0.095)
        	return colorDaDaZhang;
        else
        if(zhangdiefu >= 0.05 && zhangdiefu < 0.075)
        	return colorDaZhang;
        else
        if(zhangdiefu >= 0.0 && zhangdiefu < 0.5)
        	return colorRaising;
        else
        if(zhangdiefu >= -0.05 && zhangdiefu < 0.0)
        	return colorDie;
        else
        if(zhangdiefu >= -0.075 && zhangdiefu < -0.05)
        	return colorDaDie;
        else
        if(zhangdiefu >= -0.092 && zhangdiefu < -0.075)
        	return colorDaDaDie;
        else
        if(zhangdiefu >= -0.105 && zhangdiefu < -0.092)
        	return colorDieTing;
        else
        if(zhangdiefu > -0.2 && zhangdiefu < -0.105)
        	return colorKeChuangDaDie;
        else
        if(zhangdiefu <= -0.2 )
        	return colorKeChuangBanDieTing;
                                        	
    	
        return null;
    }
}