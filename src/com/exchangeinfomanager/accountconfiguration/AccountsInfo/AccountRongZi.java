package com.exchangeinfomanager.accountconfiguration.AccountsInfo;

import java.util.ArrayList;
import java.util.Date;

public class AccountRongZi extends AccountInfoBasic 
{
	public AccountRongZi(String accountname) 
	{
		super(accountname);
		super.setAccounttype("ÈÚ×Ê");
	}
	private double rongzizhaiwu;
	
	public double getRongZiZhaiWu ()
	{
		return this.rongzizhaiwu;
	}
	public void setRongziJieKuan (Date jiekuandate,String quanshangleixing,double jierujinger)
	{
		this.rongzizhaiwu = this.rongzizhaiwu + jierujinger;
		super.ZiJingZhuanRu(jiekuandate, quanshangleixing,jierujinger);
	}
	
	public void setRongZiHuanKuan (Date huankuankuandate,String quanshangleixing,double huankuanjinger)
	{
		this.rongzizhaiwu = this.rongzizhaiwu - huankuanjinger;
		super.ZiJingZhuanChu(huankuankuandate,quanshangleixing,huankuanjinger );
	}
	
	public void rongzimairu (Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		 //super.actionBuy(actionDay,stockcode, guquanshu, guquandanjia,shuoming);
	}

	public void rongzimaichu (Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		//return super.actionSell(actionDay,stockcode, guquanshu, guquandanjia,shuoming);
	}
	
}
