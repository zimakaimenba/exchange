package com.exchangeinfomanager.accountconfiguration.AccountsInfo;

public class StockChiCangInfo 
{
	
	public StockChiCangInfo (String chicangcode,String chicangname, int chicanggushu, double chicangchenben )
	{
			this.chicangcode = chicangcode;
			this.chicangname = chicangname;
			this.chicanggushu = chicanggushu;
			this.chicangchenben = chicangchenben;
	}
	
	
	public int getChicanggushu() {
		return chicanggushu;
	}
	public void setChicanggushu(int chicanggushu) {
		this.chicanggushu = this.chicanggushu + chicanggushu;
	}

	public double getChicangchenben() {
		return chicangchenben;
	}
	public void setChicangchenben(double chicangchenben) { 
		this.chicangchenben = this.chicangchenben + chicangchenben;
	}
	
	public void resetChicangchenben() {
		this.chicangchenben = 0;
	}
	public void resetChicanggushu () {
		this.chicanggushu = 0;
	}
	
	/**
	 * @return the chicangjunjia
	 */
	public double getChicangjunjia() {
		return this.chicangchenben/this.chicanggushu;
	}

	private int chicanggushu;
	private double chicangchenben;
	private double  chicangjunjia;
	private String chicangcode;
	private String chicangname;
	
	public String getChicangcode() {
		return chicangcode;
	}
	public String getChicangname() {
		return chicangname;
	}

	/**
	 * @param chicangname the chicangname to set
	 */
//	public void setChicangcode(String chicangcode) {
//		this.chicangcode = chicangcode;
//	}
	
	
}
