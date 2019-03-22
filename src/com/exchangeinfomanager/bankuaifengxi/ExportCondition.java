package com.exchangeinfomanager.bankuaifengxi;

public class ExportCondition 
{

	

	public ExportCondition ()
	{
	}

	private Boolean shouldnotexportSTstocks;
	
	private Boolean havedayangxianundercertainchenjiaoer;
	private Double  cjelevelundercertainchenjiaoeforyangxian;
	private Double  dayangxianundercertainchenjiaoer;
	
	private Boolean havelianxufundercertainchenjiaoer;
	private Double  cjelevelundercertainchenjiaoeforlianxu;
	private Integer fanglianglevelundercertainchenjiaoer;
	
	//下面这些变量，有2个地方使用，界面突出显示和导出，标准不一，直接返回原始数据，由客户自己处理
	private Double seetingltszmax;
	private Double seetingltszmin;
	private Double settingcjemax;
	private Double settingcjemin;
	private Integer settindpgmaxwk;
	private Integer settinbkgmaxwk;
	private Integer settingcjemaxwk;
	private Double settinghsl;
	private String settingbk;
	
	private String tooltips = "";

	private boolean huibudownquekou;
	

	//
	public void setHaveDaYangXianUnderCertainChenJiaoEr (Boolean shouldhaveyangxian, Double cjelevelundercertainchenjiaoeforyangxian, Double dayangxianundercertainchenjiaoer) 
	{
		this.havedayangxianundercertainchenjiaoer = shouldhaveyangxian;
		if(this.havedayangxianundercertainchenjiaoer) {
			this.cjelevelundercertainchenjiaoeforyangxian =  cjelevelundercertainchenjiaoeforyangxian;
			this.dayangxianundercertainchenjiaoer  = dayangxianundercertainchenjiaoer;
			
			this.tooltips = this.tooltips + "成交量小于" + this.cjelevelundercertainchenjiaoeforyangxian +  "亿,必须有" + this.dayangxianundercertainchenjiaoer + "%大阳线。";
		} else {
//			this.tooltips = this.tooltips + "成交量小于" + shouldHaveDaYangXianUnderCertainChenJiaoEr +  "必须有5%大阳线。";
		}
		
			
	}
	public Boolean shouldHaveDaYangXianUnderCertainChenJiaoEr ()
	{
		if(havedayangxianundercertainchenjiaoer == null) 
			return false;
		else
			return this.havedayangxianundercertainchenjiaoer;
	}
	public Double getCjeLevelUnderCertaincChenJiaoErOfBigYangXian ()
	{
		if(cjelevelundercertainchenjiaoeforyangxian == null)
			return -1.0;
		else
			return this.cjelevelundercertainchenjiaoeforyangxian * 100000000;
	}
	public Double getDaYangXianUnderCertainChenJiaoEr ()
	{
		if(this.dayangxianundercertainchenjiaoer == null)
			return -1.0;
		else
			return this.dayangxianundercertainchenjiaoer;
	}
	//
	public void setLianXuFangLiangUnderCertainChenJiaoEr (Boolean shouldhavelianxu, Double cjelevelundercertainchenjiaoeforlianxu, Integer fllevelundercertainchenjiaoer) 
	{
		this.havelianxufundercertainchenjiaoer = shouldhavelianxu;
		if(this.havelianxufundercertainchenjiaoer) {
			this.cjelevelundercertainchenjiaoeforlianxu =  cjelevelundercertainchenjiaoeforlianxu;
			this.fanglianglevelundercertainchenjiaoer  = fllevelundercertainchenjiaoer;
			
			this.tooltips = this.tooltips + "或成交量小于" + this.cjelevelundercertainchenjiaoeforlianxu +  "亿，必须有" + this.fanglianglevelundercertainchenjiaoer + "周放量。";
		} else {
//			this.tooltips = this.tooltips + "成交量小于" + shouldHaveDaYangXianUnderCertainChenJiaoEr +  "必须有5%大阳线。";
		}
		
			
	}
	public Boolean shouldHaveLianXuFangLangUnderCertainChenJiaoEr ()
	{
		if(havelianxufundercertainchenjiaoer == null)
			return false;
		else
			return this.havelianxufundercertainchenjiaoer;
	}
	public Double getCjeLevelUnderCertaincChenJiaoErOfLianXuFangLiang ()
	{
		if(cjelevelundercertainchenjiaoeforlianxu == null)
			return -1.0;
		else
			return this.cjelevelundercertainchenjiaoeforlianxu * 100000000;
	}
	public Integer getFangLiangLevelUnderCertainChenJiaoEr ()
	{
		if(fanglianglevelundercertainchenjiaoer == null)
			return -1;
		else
			return this.fanglianglevelundercertainchenjiaoer;
	}
	//
	public void setExportSTStocks(boolean shouldNotExportSTStocks) 
	{
		this.shouldnotexportSTstocks = shouldNotExportSTStocks;
		if(this.shouldnotexportSTstocks)
			this.tooltips = this.tooltips + "不导出ST个股。";
	}
	public Boolean shouldNotExportSTStocks()
	{
		return this.shouldnotexportSTstocks;
	}
	//
	public void setSettingBanKuai(String exportbk) 
	{
		if(exportbk != null ) {
			this.settingbk = exportbk;
			this.tooltips = this.tooltips + "限定在板块" + settingbk + "内。";
		} 
		
	}
	public String getSettingbk()
	{
		if(settingbk == null)
			return null;
		else
			return this.settingbk;
	}
	//
	public String getTooltips ()
	{
		return this.tooltips + "</br>";
	}
	//
	public Integer getSettinDpmaxwk() 
	{
		return settindpgmaxwk;
	}
	
	public void setSettinDpmaxwk(String exportdpmaxwklevel) {
		if(exportdpmaxwklevel != null) {
			this.settindpgmaxwk = Integer.parseInt( exportdpmaxwklevel );
			this.tooltips = this.tooltips + "大盘MAXWK>=" +  settindpgmaxwk + "周";
		}
		else
			this.settindpgmaxwk = null;
	}
	//
	public Integer getSettinBkmaxwk() 
	{
		return settinbkgmaxwk;
	}
	private void setSettinBkmaxwk(String exportbkmaxwklevel) {
		if(exportbkmaxwklevel != null) {
			this.settinbkgmaxwk = Integer.parseInt( exportbkmaxwklevel );
			this.tooltips = this.tooltips + "板块MAXWK>=" + settinbkgmaxwk + "周";
		}
		else
			this.settinbkgmaxwk = null;
	}
	//
	public Integer getSettingCjemaxwk()
	{
		return settingcjemaxwk;
	}
	public void setSettingCjemaxwk(String exportcjemaxwklevel) {
		if(exportcjemaxwklevel != null) {
			this.settingcjemaxwk = Integer.parseInt( exportcjemaxwklevel );
			this.tooltips = this.tooltips + "成交额MAXWK>=" + settingcjemaxwk + "周";
		}
		else
			this.settingcjemaxwk = null;
	}
	//成交额的范围
	public Double getSettingCjemin()
	{
		try{
			return settingcjemin * 100000000;
		} catch (java.lang.NullPointerException e) {
			return null;
		}
	}
	//
	public Double getSettingCjeMax() 
	{
		try{
			return settingcjemax * 100000000;
		} catch (java.lang.NullPointerException e) {
			return null;
		}
	}
	//
	public void setChenJiaoEr (String exportcjelevelmin, String exportcjelevelmax)
	{
		if(exportcjelevelmin != null) {
			this.settingcjemin = Double.parseDouble( exportcjelevelmin );
			this.tooltips = this.tooltips + "成交额>=" + settingcjemin + "亿;";
		}
		else
			this.settingcjemin = null;
		
		if(exportcjelevelmax != null) {
			this.settingcjemax = Double.parseDouble( exportcjelevelmax );
			this.tooltips = this.tooltips + "成交额<=" + settingcjemax + "亿。";
		}
		else 
			this.settingcjemax = null;
		
	}
	//换手率
	public Double getSettingHsl ()
	{
		return this.settinghsl;
	}
	public void setSettingHsl(String exporthsl) 
	{
		if(exporthsl != null) {
			this.settinghsl = Double.parseDouble(exporthsl);
			this.tooltips = this.tooltips + "换手率>=" + exporthsl + "%";
		} else
			this.settinghsl = null;
	}
	//
	public void setLiuTongShiZhi (String exportltszlevelmin, String exportltszlevelmax)
	{
		if(exportltszlevelmin != null) {
			this.seetingltszmin = Double.parseDouble( exportltszlevelmin );
			this.tooltips = this.tooltips + "流通市值>=" + seetingltszmin + "亿;";
		}
		else
			this.seetingltszmin = null;
		
		if(exportltszlevelmax != null) {
			this.seetingltszmax = Double.parseDouble( exportltszlevelmax );
			this.tooltips = this.tooltips + "流通市值<=" + seetingltszmax + "亿。";
		}
		else
			this.seetingltszmax = null ;
	}
	public Double getLiuTongShiZhiMin ()
	{
		try {
			return this.seetingltszmin * 100000000;
		} catch (java.lang.NullPointerException e) {
			return null;
		}
	}
	public Double getLiuTongShiZhiMax ()
	{
		try {
			return this.seetingltszmax * 100000000;
		} catch (java.lang.NullPointerException e) {
			return null;
		}
		
	}
	//
	public void setDisplayHuiBuDownQueKou(boolean huibudownquekou1)
	{
		this.huibudownquekou = huibudownquekou1;
	}
	public Boolean shouldHighLightHuiBuDownQueKou  ()
	{
		return this.huibudownquekou ;
	}
}
