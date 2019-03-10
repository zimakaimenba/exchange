package com.exchangeinfomanager.bankuaifengxi;

public class ExportCondition 
{
//	public ExportCondition (String exportcjelevel, String exportcjemaxwklevel, String exportdpmaxwklevel, String exportbkmaxwklevel,String exporthsl,String exportbk)
	public ExportCondition ()
	{
//		this.setSettingbk(exportbk);
//		this.setSettindpgmaxwk(exportdpmaxwklevel);
//		this.setSettinbkgmaxwk(exportbkmaxwklevel);
//		this.setSettingcje(exportcjelevel);
//		this.setSettingcjemaxwk(exportcjemaxwklevel);
//		this.setSettinghsl(exporthsl);
		
//		this.setTooltips ();
	}
	
	private Boolean shouldnotexportSTstocks;
	
	private Boolean havedayangxianundercertainchenjiaoer;
	private Double  cjelevelundercertainchenjiaoeforyangxian;
	private Double  dayangxianundercertainchenjiaoer;
	
	private Boolean havelianxufundercertainchenjiaoer;
	private Double  cjelevelundercertainchenjiaoeforlianxu;
	private Integer fanglianglevelundercertainchenjiaoer;
	
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

	//
	public void setHaveDaYangXianUnderCertainChenJiaoEr (Boolean shouldhaveyangxian, Double cjelevelundercertainchenjiaoeforyangxian, Double dayangxianundercertainchenjiaoer) 
	{
		this.havedayangxianundercertainchenjiaoer = shouldhaveyangxian;
		if(this.havedayangxianundercertainchenjiaoer) {
			this.cjelevelundercertainchenjiaoeforyangxian =  cjelevelundercertainchenjiaoeforyangxian;
			this.dayangxianundercertainchenjiaoer  = dayangxianundercertainchenjiaoer;
			
			this.tooltips = this.tooltips + "�ɽ���С��" + this.cjelevelundercertainchenjiaoeforyangxian +  "��,������" + this.dayangxianundercertainchenjiaoer + "%�����ߡ�";
		} else {
//			this.tooltips = this.tooltips + "�ɽ���С��" + shouldHaveDaYangXianUnderCertainChenJiaoEr +  "������5%�����ߡ�";
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
			
			this.tooltips = this.tooltips + "��ɽ���С��" + this.cjelevelundercertainchenjiaoeforlianxu +  "�ڣ�������" + this.fanglianglevelundercertainchenjiaoer + "�ܷ�����";
		} else {
//			this.tooltips = this.tooltips + "�ɽ���С��" + shouldHaveDaYangXianUnderCertainChenJiaoEr +  "������5%�����ߡ�";
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
			this.tooltips = this.tooltips + "������ST���ɡ�";
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
			this.tooltips = this.tooltips + "�޶��ڰ��" + settingbk + "�ڡ�";
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
		if(settindpgmaxwk == null )
			return -1;
		else
			return settindpgmaxwk;
	}
	public void setSettinDpmaxwk(String exportdpmaxwklevel) {
		if(exportdpmaxwklevel != null) {
			this.settindpgmaxwk = Integer.parseInt( exportdpmaxwklevel );
			this.tooltips = this.tooltips + "����MAXWK>=" +  settindpgmaxwk + "��";
		}
		else
			this.settindpgmaxwk = -1;
	}
	//
	public Integer getSettinBkmaxwk() 
	{
		if(settinbkgmaxwk == null)
			return -1;
		else
			return settinbkgmaxwk;
	}
	private void setSettinBkmaxwk(String exportbkmaxwklevel) {
		if(exportbkmaxwklevel != null) {
			this.settinbkgmaxwk = Integer.parseInt( exportbkmaxwklevel );
			this.tooltips = this.tooltips + "���MAXWK>=" + settinbkgmaxwk + "��";
		}
		else
			this.settinbkgmaxwk = -1;
	}
	//
	public Integer getSettingCjemaxwk()
	{
		if(settingcjemaxwk == null)
			return -1;
		else
			return settingcjemaxwk;
	}
	public void setSettingCjemaxwk(String exportcjemaxwklevel) {
		if(exportcjemaxwklevel != null) {
			this.settingcjemaxwk = Integer.parseInt( exportcjemaxwklevel );
			this.tooltips = this.tooltips + "�ɽ���MAXWK>=" + settingcjemaxwk + "��";
		}
		else
			this.settingcjemaxwk = -1;
	}
	//�ɽ���ķ�Χ
	public Double getSettingCjemin()
	{
		if(this.settingcjemin == null)
			return -1.0;
		else
			return settingcjemin * 100000000;
	}
	//
	public Double getSettingCjeMax() 
	{
		if(settingcjemax == null)
			return 1000000000000.0;
		else
			return settingcjemax * 100000000;
	}
	//
	public void setChenJiaoEr (String exportcjelevelmin, String exportcjelevelmax)
	{
		if(exportcjelevelmin != null) {
			this.settingcjemin = Double.parseDouble( exportcjelevelmin );
			this.tooltips = this.tooltips + "�ɽ���>=" + settingcjemin + "��;";
		}
		else
			this.settingcjemin = -1.0;
		
		if(exportcjelevelmax != null) {
			this.settingcjemax = Double.parseDouble( exportcjelevelmax );
			this.tooltips = this.tooltips + "�ɽ���<=" + settingcjemax + "�ڡ�";
		}
		else
			this.settingcjemax = 1000000000000000.0;
		
	}
	//������
	public void setSettingHsl(String exporthsl) 
	{
		if(exporthsl != null) {
			this.settinghsl = Double.parseDouble(exporthsl);
			this.tooltips = this.tooltips + "������>=" + exporthsl + "%";
		} else
			this.settinghsl = -1.0;
	}
	//
	public Double getSettingHsl ()
	{
		if(this.settinghsl == null)
			return -1.0;
		else
			return this.settinghsl;
	}
	//
	public void setLiuTongShiZhi (String exportltszlevelmin, String exportltszlevelmax)
	{
		if(exportltszlevelmin != null) {
			this.seetingltszmin = Double.parseDouble( exportltszlevelmin );
			this.tooltips = this.tooltips + "��ͨ��ֵ>=" + seetingltszmin + "��;";
		}
		else
			this.settingcjemin = -1.0;
		
		if(exportltszlevelmax != null) {
			this.seetingltszmax = Double.parseDouble( exportltszlevelmax );
			this.tooltips = this.tooltips + "��ͨ��ֵ<=" + seetingltszmax + "�ڡ�";
		}
		else
			this.settingcjemax = 1000000000000000.0;
	}
	public Double getLiuTongShiZhiMin ()
	{
		if(seetingltszmin != null)
			return this.seetingltszmin;
		else
			return -1.0;
	}
	public Double getLiuTongShiZhiMax ()
	{
		if(seetingltszmin != null)
			return this.seetingltszmax;
		else
			return -10000000000000000.0;
	}
}
