package com.exchangeinfomanager.bankuaifengxi;

import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;

public class ExportCondition 
{

	public ExportCondition ()
	{
		extracon = new ExtraExportConditions ();
	}
	private  ExtraExportConditions extracon;
	
//	private Boolean shouldnotexportSTstocks;
//	private Boolean havedayangxianundercertainchenjiaoer;
//	private Double  cjelevelundercertainchenjiaoeforyangxian;
//	private Double  dayangxianundercertainchenjiaoer;
//	private Boolean havelianxufundercertainchenjiaoer;
//	private Double  cjelevelundercertainchenjiaoeforlianxu;
//	private Integer fanglianglevelundercertainchenjiaoer;
	
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

	private Integer settindpgminwk;

	private Integer settingma;
	
	public Boolean checkTDXNodeMatchedCurConditons (TDXNodes node, String period)
	{
		if(node.getType() == BkChanYeLianTreeNode.TDXBK) {
			if ( this.shouldOnlyExportStocksNotBanKuai() )
				return false;
			
			if( ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					 ||  ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //有些指数是没有个股和成交量的，不列入比较范围 
					 ||  ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //仅导出有个股的板块
				return false;
		}
			
		return null;
	}
	public ExtraExportConditions getExtraExportConditions ()
	{
		return this.extracon;
	}
	/*
	 * 
	 */
	public Boolean shouldHaveDaYangXianUnderCertainChenJiaoEr ()
	{
		Boolean should = extracon.shouldHaveDaYangXianUnderCertainChenJiaoEr();
		if(should) {
			this.tooltips = this.tooltips + "成交量小于" + this.getCjeLevelUnderCertaincChenJiaoErOfBigYangXian() +  ",必须有" + this.getDaYangXianUnderCertainChenJiaoEr () + "%大阳线。";
			return true;
		}
		else
			return false;
//		if(havedayangxianundercertainchenjiaoer == null) 
//			return false;
//		else
//			return this.havedayangxianundercertainchenjiaoer;
	}
	//
	public Double getCjeLevelUnderCertaincChenJiaoErOfBigYangXian ()
	{
		Double cje = extracon.getCjeLevelUnderCertainChenJiaoErOfDaYangXian() ;
		if(cje == null)
			return -1.0;
		else
			return cje * 100000000;
	}
	//
	public Double getDaYangXianUnderCertainChenJiaoEr ()
	{
		Double dyxlevel = extracon.getYangXianLevelUnderCertainChenJiaoErofDaYangXian();
		
		if(dyxlevel == null)
			return -1.0;
		else
			return dyxlevel;
	}
	/*
	 * 
	 */
	public Boolean shouldHaveLianXuFangLangUnderCertainChenJiaoEr ()
	{
		Boolean should = extracon.shouldHaveFangLiangUnderCertainChenJiaoEr(); 
		if( should ) {
			this.tooltips = this.tooltips + "或成交量小于" + this.getCjeLevelUnderCertaincChenJiaoErOfLianXuFangLiang() +  "，必须有" 
							+ this.getFangLiangLevelUnderCertainChenJiaoEr()  + "周放量。";
			return true;
		} else 
			return false;
		
	}
	//
	public Double getCjeLevelUnderCertaincChenJiaoErOfLianXuFangLiang ()
	{
		Double cje = extracon.getCjeLevelUnderCertainChenJiaoErOfLianXuFangLiang();
		if(cje == null)
			return -1.0;
		else
			return cje * 100000000;
	}
	//
	public Integer getFangLiangLevelUnderCertainChenJiaoEr ()
	{
		Integer flwklevel = extracon.getLianXuFangLianLevelUnderCertainChenJiaoErOfFangLiang();
		if(flwklevel == null)
			return -1;
		else
			return flwklevel;
	}
	//
//	public void setExportSTStocks(boolean shouldNotExportSTStocks) 
//	{
//		this.shouldnotexportSTstocks = shouldNotExportSTStocks;
//		if(this.shouldnotexportSTstocks)
//			this.tooltips = this.tooltips + "不导出ST个股。";
//	}
	public Boolean shouldOnlyExportBanKuaiOfZhanBiUp ()
	{
		boolean should = extracon.shouldOnlyExportBanKuaiOfZhanBiUp();
		return should;
	}
	public Boolean shouldNotExportSTStocks()
	{
		boolean should = extracon.shouldExportSTStocks();
		if(should ) {
			this.tooltips = this.tooltips + "不导出ST个股。";
			return true;
		} else
			return false;
	}
	/*
	 * 
	 */
	public boolean shouldOnlyExportCurrentBankuai ()
	{
		if(extracon.shouldOnlyExportCurrentBankuai() ) 
			return true;
		else
			return false;
	}
	public void setSettingBanKuai(String exportbk) 
	{
		if(extracon.shouldOnlyExportCurrentBankuai() ) {
			if(exportbk != null ) {
				this.settingbk = exportbk;
				this.tooltips = this.tooltips + "限定在板块" + settingbk + "内。";
			}
		}
	}
	/*
	 * 
	 */
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
	public void setSettinDpminwk(String exportdpminwklevel) 
	{
		if(exportdpminwklevel != null) {
			this.settindpgminwk = Integer.parseInt(exportdpminwklevel );
		} else
			this.settindpgminwk = null;
	}
	public Integer getSettingDpminwk ()
	{
		return this.settindpgminwk;
	}
	public void setSettinDpmaxwk(String exportdpmaxwklevel) 
	{
		if(exportdpmaxwklevel != null) {
			this.settindpgmaxwk = Integer.parseInt( exportdpmaxwklevel );
			this.tooltips = this.tooltips + "大盘MAXWK>=" +  settindpgmaxwk + "周";
		}
		else
			this.settindpgmaxwk = null;
	}
	//
	private Integer getSettinBkmaxwk() 
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
	public void setSettingMA(String ma)
	{
		if(ma != null ) {
			this.settingma = Integer.parseInt(ma);
		} else
			this.settingma = null;
	}
	public Integer getSettingMA()
	{
		return this.settingma;
		
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
	/*
	 * 
	 */
	public boolean shouldExportAllBanKuai ()
	{
		return extracon.shouldExportAllBanKuai ();
	}
	/*
	 * 
	 */
	public boolean shouldOnlyExportStocksNotBanKuai ()
	{
		return extracon.shouldOnlyExportStocksNotBanKuai();
	}
	
	
	
}
