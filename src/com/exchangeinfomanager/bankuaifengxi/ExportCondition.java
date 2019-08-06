package com.exchangeinfomanager.bankuaifengxi;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

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
	
	//������Щ��������2���ط�ʹ�ã�����ͻ����ʾ�͵�������׼��һ��ֱ�ӷ���ԭʼ���ݣ��ɿͻ��Լ�����
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

//	private Integer settingma;
	private String settingmaformula;
	
	/*
	 * 
	 */
	public Boolean checkTDXNodeMatchedCurSettingConditons (TDXNodes node, String period)
	{
		if(node.getType() == BkChanYeLianTreeNode.TDXBK) {
			if ( this.shouldOnlyExportStocksNotBanKuai() )
				return false;
			
			if( ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					 ||  ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //��Щָ����û�и��ɺͳɽ����ģ�������ȽϷ�Χ 
					 ||  ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //�������и��ɵİ��
				return false;
		}
			
		return null;
	}
	/*
	 * 
	 */
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
			this.tooltips = this.tooltips + "�ɽ���С��" + this.getCjeLevelUnderCertaincChenJiaoErOfBigYangXian() +  ",������" + this.getDaYangXianUnderCertainChenJiaoEr () + "%�����ߡ�";
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
			this.tooltips = this.tooltips + "��ɽ���С��" + this.getCjeLevelUnderCertaincChenJiaoErOfLianXuFangLiang() +  "��������" 
							+ this.getFangLiangLevelUnderCertainChenJiaoEr()  + "�ܷ�����";
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
//			this.tooltips = this.tooltips + "������ST���ɡ�";
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
			this.tooltips = this.tooltips + "������ST���ɡ�";
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
				this.tooltips = this.tooltips + "�޶��ڰ��" + settingbk + "�ڡ�";
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
			this.tooltips = this.tooltips + "����MAXWK>=" +  settindpgmaxwk + "��";
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
			this.tooltips = this.tooltips + "���MAXWK>=" + settinbkgmaxwk + "��";
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
			this.tooltips = this.tooltips + "�ɽ���MAXWK>=" + settingcjemaxwk + "��";
		}
		else
			this.settingcjemaxwk = null;
	}
	//�ɽ���ķ�Χ
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
			this.tooltips = this.tooltips + "�ɽ���>=" + settingcjemin + "��;";
		}
		else
			this.settingcjemin = null;
		
		if(exportcjelevelmax != null) {
			this.settingcjemax = Double.parseDouble( exportcjelevelmax );
			this.tooltips = this.tooltips + "�ɽ���<=" + settingcjemax + "�ڡ�";
		}
		else 
			this.settingcjemax = null;
		
	}
	/*
	 * 
	 */
	public void setSettingMAFormula (String maformula) //>250 && <60 &&>10
	{
		if(maformula == null ) {
			this.settingmaformula = maformula;
			return;
		}
			
		//Ӧ����������ʽ��飬���ڲ���
//		if( Pattern.matches("\\d{6}$",maformula)  || Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]+$",maformula) )
//			return ;
		
		maformula = maformula.replace(">", "x>");
		maformula = maformula.replace("<", "x<");
		
		if(maformula.contains(">250") || maformula.contains(">=250") || maformula.contains("<250") )//����250��Ӧ����60
			maformula = maformula.replace("250", "\'250\'"  ) ;
	    if(maformula.contains(">120") || maformula.contains(">=120") || maformula.contains("<120") )//����250��Ӧ����60
	    	maformula = maformula.replace("120", "\'120\'" ) ;
	    if(maformula.contains(">60") || maformula.contains(">=60") || maformula.contains("<60") ) //����250��Ӧ����60
	    	maformula = maformula.replace("60", "\'60\'"   ) ;
	    if(maformula.contains(">30") || maformula.contains(">=30") || maformula.contains("<30") ) //����250��Ӧ����60
	    	maformula = maformula.replace("30", "\'30\'"  ) ;
	    if(maformula.contains(">20") || maformula.contains(">=20") || maformula.contains("<20") ) //����250��Ӧ����60
	    	maformula = maformula.replace("20", "\'20\'"  ) ;
	    if(maformula.contains(">10") || maformula.contains(">=10") || maformula.contains("<10") ) //����250��Ӧ����60
	    	maformula = maformula.replace("10", "\'10\'"  ) ;
	    if(maformula.contains(">5") || maformula.contains(">=5") || maformula.contains("<5") ) //����250��Ӧ����60
	    	maformula = maformula.replace("5","\'5\'"   ) ;
		
		this.settingmaformula = maformula;
		
	}
	public String getSettingMAFormula ()
	{
		return this.settingmaformula;
	}
	//������
	public Double getSettingHsl ()
	{
		return this.settinghsl;
	}
	
	public void setSettingHsl(String exporthsl) 
	{
		if(exporthsl != null) {
			this.settinghsl = Double.parseDouble(exporthsl);
			this.tooltips = this.tooltips + "������>=" + exporthsl + "%";
		} else
			this.settinghsl = null;
	}
	//
	public void setLiuTongShiZhi (String exportltszlevelmin, String exportltszlevelmax)
	{
		if(exportltszlevelmin != null) {
			this.seetingltszmin = Double.parseDouble( exportltszlevelmin );
			this.tooltips = this.tooltips + "��ͨ��ֵ>=" + seetingltszmin + "��;";
		}
		else
			this.seetingltszmin = null;
		
		if(exportltszlevelmax != null) {
			this.seetingltszmax = Double.parseDouble( exportltszlevelmax );
			this.tooltips = this.tooltips + "��ͨ��ֵ<=" + seetingltszmax + "�ڡ�";
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
