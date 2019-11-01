package com.exchangeinfomanager.bankuaifengxi.ai;

import java.util.ArrayList;

public class ZhongDianGuanZhu 
{

	public ZhongDianGuanZhu() 
	{
	}
	private String nodecode;
	
	//以下对应XML的各项
	private ArrayList<ZdgzItem> govpolicy;
	private ArrayList<ZdgzItem> dapanzhishu;
	private String dpanalysisicomments;
	private ArrayList<ZdgzItem> gegufinancial;
	private ArrayList<ZdgzItem> gudong;
	private ArrayList<ZdgzItem> jishu;
	private ArrayList<ZdgzItem> ticha;
	private String gganalysisicomments;
	private ArrayList<ZdgzItem> gggupiaochi;
	
	//返回所有被选择项目后的string
	public String getZdgzAllSelectedInfo ()
	{
		String result = "";
		try{
			for(ZdgzItem zdgzitem : govpolicy) {
				result = result + formatedResultString (zdgzitem);
			}
		} catch (java.lang.NullPointerException e) {
			
		}
		try {
			for(ZdgzItem zdgzitem : dapanzhishu) {
				result = result + formatedResultString (zdgzitem);
			}
		} catch (java.lang.NullPointerException e) {
			
		}
		try {
			for(ZdgzItem zdgzitem : gegufinancial) {
				result = result + formatedResultString (zdgzitem);
			}
		} catch (java.lang.NullPointerException e) {
					
		}
		try{
			for(ZdgzItem zdgzitem : gudong) {
				result = result + formatedResultString (zdgzitem);
			}
		} catch (java.lang.NullPointerException e) {
			
		}
		try{
			for(ZdgzItem zdgzitem : jishu) {
				result = result + formatedResultString (zdgzitem);
			}
		} catch (java.lang.NullPointerException e) {
			
		}
		try {
			for(ZdgzItem zdgzitem : ticha) {
				result = result + formatedResultString (zdgzitem);
			}
		} catch (java.lang.NullPointerException e) {
			
		}
		try {
			for(ZdgzItem zdgzitem : gggupiaochi) {
				result = result + formatedResultString (zdgzitem);
			}
		} catch (java.lang.NullPointerException e) {
			
		}
		try {
			if(!dpanalysisicomments.isEmpty())
				result =result + dpanalysisicomments;
		} catch (java.lang.NullPointerException e) {
			
		}
		try {
			if(!gganalysisicomments.isEmpty())
				result =result + gganalysisicomments;
		} catch (java.lang.NullPointerException e) {
			
		}
		
		if(result.isEmpty())
			return null;
		else
			return result;
	}
	private String formatedResultString (ZdgzItem zdgzitem)
	{
		String result = "";
		if(zdgzitem.isSelected())
			result =result + "[" + zdgzitem.getXmlTagBelonged() + ":" + zdgzitem.getId() + "]";
		
		return result;
	}
	//明日计划
//	private boolean mrjhenabled;
//	private String mrjhaction;
//	private Double mrjhprice;
	
//	/**
//	 * @return the mrjhenabled
//	 */
//	public boolean isMrjhenabled() {
//		return mrjhenabled;
//	}
//	/**
//	 * @param mrjhenabled the mrjhenabled to set
//	 */
//	public void setMrjhenabled(boolean mrjhenabled) {
//		this.mrjhenabled = mrjhenabled;
//	}
//	/**
//	 * @return the mrjhaction
//	 */
//	public String getMrjhaction() {
//		return mrjhaction;
//	}
//	/**
//	 * @param mrjhaction the mrjhaction to set
//	 */
//	public void setMrjhaction(String mrjhaction) {
//		this.mrjhaction = mrjhaction;
//	}
//	/**
//	 * @return the mrjhprice
//	 */
//	public Double getMrjhprice() {
//		return mrjhprice;
//	}
//	/**
//	 * @param mrjhprice the mrjhprice to set
//	 */
//	public void setMrjhprice(Double mrjhprice) {
//		this.mrjhprice = mrjhprice;
//	}
	/**
	 * @return the govpolicy
	 */
	public ArrayList<ZdgzItem> getGovpolicy() {
		return govpolicy;
	}
	/**
	 * @param govpolicy the govpolicy to set
	 */
	public void setGovpolicy(ArrayList<ZdgzItem> govpolicy2)
	{
		this.govpolicy = govpolicy2;
	}
	/**
	 * @return the dapan
	 */
	public ArrayList<ZdgzItem> getDapanZhiShuLists() {
		return dapanzhishu;
	}
	/**
	 * @param dapan the dapan to set
	 */
	public void setDapanZhiShuLists(ArrayList<ZdgzItem> dapan2) 
	{
			this.dapanzhishu = dapan2;
	}
	public void setDaPanAnalysisComments(String comments2) 
	{
		this.dpanalysisicomments = comments2;
	}
	public String getDaPanAnalysisComments() 
	{
		return this.dpanalysisicomments ;
	}
	public void setGeGuFinancial(ArrayList<ZdgzItem> ggfinancial) 
	{
		this.gegufinancial = ggfinancial;
	} 
	public  ArrayList<ZdgzItem> getGeGuFinancial() 
	{
		return this.gegufinancial;
	}
	public void setGeGuGuDong(ArrayList<ZdgzItem> gudong2) 
	{
		this.gudong = gudong2;		
	}
	public ArrayList<ZdgzItem> getGeGuGuDong() 
	{
		return this.gudong ;		
	}
	public void setGeGuJiShu(ArrayList<ZdgzItem> jishu2) 
	{
		this.jishu = jishu2;
	}
	public ArrayList<ZdgzItem> getGeGuJiShu( ) 
	{
		return this.jishu ;
	}
	public void setGeGuTiCha(ArrayList<ZdgzItem> ticha2)
	{
		this.ticha = ticha2;
	}
	public ArrayList<ZdgzItem> getGeGuTiCha()
	{
		return this.ticha;
	}
	public void setGeGuAnalysisComments(String comments2)
	{
		this.gganalysisicomments = comments2;
	}
	public String getGeGuAnalysisComments()
	{
		return this.gganalysisicomments;
	}
	public void setGeGuGuPiaoChi(ArrayList<ZdgzItem> gpc) 
	{
		this.gggupiaochi = gpc;
	}
	public ArrayList<ZdgzItem> getGeGuGuPiaoChi() 
	{
		return this.gggupiaochi;
	}

}
