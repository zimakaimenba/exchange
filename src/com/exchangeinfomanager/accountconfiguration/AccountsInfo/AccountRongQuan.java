package com.exchangeinfomanager.accountconfiguration.AccountsInfo;

import java.util.ArrayList;

public class AccountRongQuan extends AccountInfoBasic 
{
	int rongquanyuer;
//	private static Logger logger = Logger.getLogger(AccountInfoBasic.class);
	public AccountRongQuan(String accountname) 
	{
		super(accountname);
		// TODO Auto-generated constructor stub
		super.setAccounttype(super.TYPERONGQUAN);
		// TODO Auto-generated constructor stub
	}
	
	public void rongQuanMaiChu (String stockcode, int sellnumber, double selldanjia)
	{
		
	}
	
	public void maiQuanHuanQuan (String stockcode, int sellnumber, double selldanjia)
	{
		
	}
	
	public void xianquanhuanquan ()
	{
		
	}
	
	public void setRongquanyuer(int yuer)
	{
		this.rongquanyuer = yuer;
	}
	public int getRongquanyuer ()
	{
		return 0;
	}
	
	public void setGuanlianzhanghu(String guanlianzhanghu) 
	{
//		  int tmpindex = this.accountname.indexOf('-');//   "- 信用普通子账户";
//	      String tmpsubstring = this.accountname.substring(0, tmpindex);
//	      tmpsubstring = tmpsubstring + "- 信用普通子账户";
//	      
//	      super.setGuanlianzhanghu(tmpsubstring);
	}

	
}
