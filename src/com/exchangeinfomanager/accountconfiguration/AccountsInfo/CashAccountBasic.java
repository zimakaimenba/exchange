package com.exchangeinfomanager.accountconfiguration.AccountsInfo;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import com.exchangeinfomanager.database.AccountDbOperation;

public class CashAccountBasic 
{
	public CashAccountBasic(String cashaccountname) 
	{
		this.accountname = cashaccountname;
		this.cash = 0.0;
		this.historyprofit = 0.0;
		this.totalxianjingchenben = 0.0;
		isallowedzhuanruxianjing = true;
	}
//	private static Logger logger = Logger.getLogger(AccountInfoBasic.class);
	private Double cash;
	private Double totalxianjingchenben; //账户总的现金成本
	private Double historyprofit; //历史上总的盈亏
	private String suoshuquanshang;
	private String accountname;
	private String accountid;
	private boolean jihuo;
	private int accounttype;
	private boolean isallowedzhuanruxianjing ;
	protected static int TYPECASH=0;
	protected static int TYPEPUTONG=1;
	protected static int TYPEXYPUTONG=2;
	protected static int TYPERONGZI=3;
	protected static int TYPERONGQUAN=4;
	
	/**
	 * @历史盈亏
	 */
	public double getHistoryProfit() {
		return historyprofit;
	}
	public void setHistoryProfit(double historyprofit2) 
	{
		this.historyprofit = historyprofit2;
//		if(this.historyprofit == null)
//			this.historyprofit = historyprofit2;
//		else if( this.historyprofit != historyprofit2) {
//			this.historyprofit = this.historyprofit + historyprofit2;
//		}
	}

	public double getTotalZiJingChenBen() 
	{
		return totalxianjingchenben;
	}
	public void setTotoalZiJingChenBen(double totoalusedxianjing) 
	{
		this.totalxianjingchenben =  totoalusedxianjing;
//			this.totalxianjingchenben = this.totalxianjingchenben + totoalusedxianjing;
	}
	public void setSuoShuQuanShngYingHang (String ssqs)
	{
		this.suoshuquanshang = ssqs;
	}
	
	public String getSuoShuQuanShngYingHang ()
	{
		return this.suoshuquanshang;
	}
	
	public void setNotAllowZhuanRuXianJing ()
	{
		this.isallowedzhuanruxianjing = false;
	}
	public int getAccountType() {
		return accounttype;
	}
	protected void setAccounttype(int accounttype2) {
		this.accounttype = accounttype2;
	}
	public String getAccountName ()
	{
		return this.accountname;
	}

	public void setCash (double cash2)
	{
		this.cash = cash2;
	}
	public double getCurrentCash() 
	{
		return this.cash;
	}
	public void ZiJingZhuanRu (LocalDateTime actiondate, String quanshangleixing, double zijing)
	{
		if(isallowedzhuanruxianjing == true)
			this.cash = this.cash + zijing ;
		else
			System.out.println("账户不允许转入现金！");
	}
	/*
	 * 判断记录的日期是否是当天，如果是当天，则是当日记录，要判断资金的合法性，否则是过去记录导入，无需判断资金的合法性
	 */
	private boolean checkIfSameDay (Date actionDay) 
	{
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(new Date() );
		cal2.setTime(actionDay);
		boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
		                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
		
		return sameDay;
	}
	/*
	 *  * 如果金额不足，直接返回-1，否则返回买入后还剩余多少现金
	 */
	public int actionBuyPreCheck (Date actionDay,double guquandanjia)
	{
		 System.out.println("需要做资金合法性检查");
		if( (this.getCurrentCash() - guquandanjia) >0 ) { //判断是否有足够的现金
			return 1;
		} else {
			System.out.print("账户现金不足，无法买入");
			return -1;
		} 
	}
	public int ZiJingZhuanChu (LocalDateTime actiondate, String quanshangleixing, double zijing)
	{
//		if(checkIfSameDay(actiondate) == true) { //判断记录的日期是否是当天，如果是当天，则是当日记录，要判断资金的合法性，否则是过去记录导入，无需判断资金的合法性
//			if( actionBuyPreCheck ( actiondate, zijing) <0 )
//				return -1;
//		}
		this.cash = this.cash - zijing;
		return 1;
	}

	public void setAccountid(String accountid) 
	{
		this.accountid = accountid;
	}
	public String getAccountid() 
	{
		return this.accountid;
	}

	public void setJihuo(boolean b) 
	{
		this.jihuo = b;
	}
	public boolean isJiHuo() 
	{
		return this.jihuo;
	}

}
