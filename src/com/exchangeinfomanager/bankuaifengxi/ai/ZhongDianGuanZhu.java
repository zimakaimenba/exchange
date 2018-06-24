package com.exchangeinfomanager.bankuaifengxi.ai;

import java.util.ArrayList;

public class ZhongDianGuanZhu 
{

	public ZhongDianGuanZhu() 
	{
	}
	
	private ArrayList<ZdgzItem> govpolicy;
	private ArrayList<String> dapan;
	private String analysisicomments;
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
	public ArrayList<String> getDapan() {
		return dapan;
	}
	/**
	 * @param dapan the dapan to set
	 */
	public void setDapan(ArrayList<String> dapan2) 
	{
			this.dapan = dapan2;
	}
	public void setAnalysisComments(String comments2) 
	{
		this.analysisicomments = comments2;
	}
	public String getAnalysisComments() 
	{
		return this.analysisicomments ;
	} 
}
