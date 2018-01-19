package com.exchangeinfomanager.accountconfiguration.AccountsInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class AccountRongZi extends AccountInfoBasic 
{
	public AccountRongZi(String accountname) 
	{
		super(accountname);
		super.setAccounttype(super.TYPERONGZI);
	}
	private double rongzizhaiwu;
//	private static Logger logger = Logger.getLogger(AccountInfoBasic.class);
	public double getRongZiZhaiWu ()
	{
		return this.rongzizhaiwu;
	}
	public void setRongziJieKuan (LocalDateTime jiekuandate,String quanshangleixing,double jierujinger)
	{
		this.rongzizhaiwu = this.rongzizhaiwu + jierujinger;
		super.ZiJingZhuanRu(jiekuandate, quanshangleixing,jierujinger);
	}
	
	public void setRongZiHuanKuan (LocalDateTime huankuankuandate,String quanshangleixing,double huankuanjinger)
	{
		this.rongzizhaiwu = this.rongzizhaiwu - huankuanjinger;
		super.ZiJingZhuanChu(huankuankuandate,quanshangleixing,huankuanjinger );
	}
	
	public void rongzimairu (LocalDateTime actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		 //super.actionBuy(actionDay,stockcode, guquanshu, guquandanjia,shuoming);
	}

	public void rongzimaichu (LocalDateTime actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		//return super.actionSell(actionDay,stockcode, guquanshu, guquandanjia,shuoming);
	}
	
}
