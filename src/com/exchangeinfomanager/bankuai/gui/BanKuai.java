package com.exchangeinfomanager.bankuai.gui;

import java.util.ArrayList;
import java.util.HashMap;

public class BanKuai 
{

	

	public BanKuai(String bankuaicode1) 
	{
		this.bankuaicode = bankuaicode1;
	}
	
	private String bankuainame;
	private String bankuaicode;
	private String bankuaicreatedtime;
	private String bankuaijiaoyisuo; //是上证还是深证的板块指数
	//private HashMap<String,ArrayList<String>> bkchanyelian ;
	
	public void setBanKuaiJiaoYiSuo (String jiaoyisuo)
	{
		this.bankuaijiaoyisuo = jiaoyisuo;
	}
	public String getBanKuaiJiaoYiSuo ()
	{
		return this.bankuaijiaoyisuo;
	}
	
	/**
	 * @return the bankuainame
	 */
	public String getBankuainame() {
		return bankuainame;
	}
	/**
	 * @param bankuainame the bankuainame to set
	 */
	public void setBankuainame(String bankuainame) {
		this.bankuainame = bankuainame;
	}
	/**
	 * @return the bankuaicode
	 */
	public String getBankuaicode() {
		return bankuaicode;
	}
	/**
	 * @param bankuaicode the bankuaicode to set
	 */
	public void setBankuaiCode(String bankuaicode) {
		this.bankuaicode = bankuaicode;
	}
	/**
	 * @return the bankuaicreatedtime
	 */
	public String getBankuaicreatedtime() {
		return bankuaicreatedtime;
	}
	/**
	 * @param bankuaicreatedtime the bankuaicreatedtime to set
	 */
	public void setBankuaicreatedtime(String bankuaicreatedtime) {
		this.bankuaicreatedtime = bankuaicreatedtime;
	}
	
}
