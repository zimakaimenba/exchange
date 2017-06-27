package com.exchangeinfomanager.accountconfiguration.AccountsInfo;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.exchangeinfomanager.database.AccountDbOperation;
import com.google.common.collect.Maps;


public class AccountInfoBasic extends CashAccountBasic
{

	public AccountInfoBasic(String accountname) 
	{
		super(accountname);
		
		this.totoalbuyguadanjiner = 0.0;
		this.totoalsellguadanjiner = 0.0;

		chicanggupiaodetailsmap = Maps.newHashMap();
	}

	private Double totoalbuyguadanjiner; //当日买入挂单金额
	private Double totoalsellguadanjiner; //当日卖出挂单金额
	private HashMap<String,StockChiCangInfo> chicanggupiaodetailsmap;
	
	
	/**
	 * @当前可用现金,买入的话要看当前有多少现金可用，挂单占据了资金。
	 */
	public double getCurrentAvailableXianJin ()
	{
		return super.getCurrentCash() - totoalbuyguadanjiner;
	}
	/**
	 * @总的买挂单金额
	 */
	public Double getTotoalBuyGuaDanJinEr() {
		return totoalbuyguadanjiner;
	}
	public void setTotoalBuyGuaDanJinEr(Double totoalguadanjiner2) {
		this.totoalbuyguadanjiner = totoalguadanjiner2;
	}
	/**
	 * @总的卖挂单金额
	 */
	public Double getTotoalSellGuaDanJinEr() {
		return totoalsellguadanjiner;
	}
	public void setTotoalSellGuaDanJinEr(Double totoalsellguadanjiner2) {
		this.totoalsellguadanjiner = totoalsellguadanjiner;
	}

	/*
	 * 持仓相关
	 */
	public StockChiCangInfo getStockChiCangInfoIndexOf (String stockcode)
	{
		return ((StockChiCangInfo)this.chicanggupiaodetailsmap.get(stockcode));
	}
	public boolean isChiCang (String stockcode)
	{
		if( (StockChiCangInfo)this.chicanggupiaodetailsmap.get(stockcode) != null )
			return true;
		else return false;
	}
	/*
	 * @持仓股票相关
	 *  
	 */
	public double getChiCangGuPiaoChenBen (String stockcode) {
		return this.chicanggupiaodetailsmap.get(stockcode).getChicangchenben();
	}
	public ArrayList<String> getAllChiCangGuPiaoList() 
	{
		return new ArrayList<String>(this.chicanggupiaodetailsmap.keySet());
	}
	public int getChiCangGuPiaoGuShu (String gupiaoname)
	{
		if(chicanggupiaodetailsmap.get(gupiaoname) != null) {
			StockChiCangInfo tmpscc = chicanggupiaodetailsmap.get(gupiaoname);
			return tmpscc.getChicanggushu();
		} else 
			return 0;
		
	}
	public void addNewChiCangStock(StockChiCangInfo tmpstockchicanginfo) 
	{
		String tmpstockcode = tmpstockchicanginfo.getChicangcode();
		try {
			chicanggupiaodetailsmap.put(tmpstockcode,tmpstockchicanginfo);
		} catch (java.lang.NullPointerException e) {
			chicanggupiaodetailsmap = new  HashMap<String,StockChiCangInfo>();
			chicanggupiaodetailsmap.put(tmpstockcode,tmpstockchicanginfo);
		}
	}
	public HashMap<String,StockChiCangInfo> getAllChiCangGuPiaoMap() 
	{
			return chicanggupiaodetailsmap;
	}
	public void setAllChiCangGuPiaoMapgetAllChiCangGuPiaoMap(HashMap<String, StockChiCangInfo> chicanggupiaochenben) {
		this.chicanggupiaodetailsmap = chicanggupiaochenben;
	}


	/*
	 *  * 如果金额不足，直接返回-1，否则返回买入后还剩余多少现金
	 */
	public int actionBuyPreCheck (Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		 System.out.println("需要做资金合法性检查");
		if( (this.getCurrentAvailableXianJin() - guquanshu * guquandanjia) >=0 ) { //判断是否有足够的现金
			return 1;
		} else {
			System.out.print("账户现金不足，无法买入");
			return -1;
		} 
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
	 * 如果金额不足，直接返回-1，否则返回买入后还剩余多少现金
	 */
	public Double actionBuy(Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		//System.out.println(this.getAccountName() + "买入前有现金" + this.getCurrentAvailableXianJin() );
		if(checkIfSameDay(actionDay) == true) { //判断记录的日期是否是当天，如果是当天，则是当日记录，要判断资金的合法性，否则是过去记录导入，无需判断资金的合法性
			if( actionBuyPreCheck ( actionDay, stockcode,  guquanshu,  guquandanjia, shuoming) <0 ) {
				System.out.println("未通过买入合法性检查");
				return null;
			}
		}
			
			super.setCash( super.getCurrentCash() - guquanshu * guquandanjia); //可用现金
			super.setTotoalZiJingChenBen(super.getTotalZiJingChenBen() + guquanshu * guquandanjia); //已用现金
			
			double chengben = guquanshu * guquandanjia; 
			if (!this.isChiCang(stockcode) ) { //新入股票，
				StockChiCangInfo tmpnewacnt = new StockChiCangInfo(stockcode, "", guquanshu, chengben);
				this.chicanggupiaodetailsmap.put(stockcode, tmpnewacnt);
			} else {  //原有股票
				((StockChiCangInfo)chicanggupiaodetailsmap.get(stockcode)).setChicanggushu(guquanshu);
				((StockChiCangInfo)chicanggupiaodetailsmap.get(stockcode)).setChicangchenben(chengben);
			}
			System.out.println(this.getAccountName() + "买入后持有" + this.chicanggupiaodetailsmap.get(stockcode).getChicanggushu() );
			return this.getCurrentAvailableXianJin();
	}
	/*
	 * 卖单持仓检查 
	 */
	public int actionSellPreCheck (Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		if( !this.isChiCang(stockcode) ) { 
			System.out.println("错误，账户没有该股票！");
			return -1;
		} else if(guquanshu > this.getChiCangGuPiaoGuShu(stockcode) && this.getAccountType() != super.TYPERONGQUAN  ) { //融券账户例外，可以先卖
			System.out.println("下单错误，账户不持有这么多数量该股票！");
			return -1;
		} 
		
		return 1;

	}
	/*
	 * 如果持仓卖完，直接返回卖出后的利润额，还有股票就返回null 
	 */
	
	public String actionSell(Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		if(checkIfSameDay(actionDay) == true) { //判断记录的日期是否是当天，如果是当天，则是当日记录，要判断资金的合法性，否则是过去记录导入，无需判断资金的合法性
			if( actionSellPreCheck ( actionDay, stockcode,  guquanshu,  guquandanjia, shuoming) <0 ) {
				System.out.println("未通过卖出合法性检查");
				return "FAIL";
			}
		}
		

			Double finalprofit = null;

			//对持仓股票进行更改
			int currentgushu = this.getChiCangGuPiaoGuShu(stockcode);

			if( 0 == (currentgushu - guquanshu)) { //转出后持仓为0
				finalprofit = this.getChiCangGuPiaoChenBen(stockcode) - guquanshu* guquandanjia; //持仓为0，要大概计算盈亏
				finalprofit = 0 - finalprofit; //为负值说明盈利，正值说明亏损
				super.setHistoryProfit(super.getHistoryProfit() + finalprofit); //保存账户利润，账户利润直接用系统计算的结果，不用用户手动输入结果
								
				this.chicanggupiaodetailsmap.remove(stockcode);
				
				System.out.println(super.getAccountName() + "卖出后利润是：" + finalprofit);
			} else {
				((StockChiCangInfo)this.getStockChiCangInfoIndexOf(stockcode)).setChicanggushu(0 - guquanshu); //卖出股票，股数应该相应减少
				((StockChiCangInfo)this.getStockChiCangInfoIndexOf(stockcode)).setChicangchenben(0-guquandanjia * guquanshu);
				
				System.out.println(super.getAccountName() + "卖出" + stockcode + " " + guquanshu + ",共持有" + ((StockChiCangInfo)chicanggupiaodetailsmap.get(stockcode)).getChicanggushu());
			}
			
			//对本账户情况进行更改
			super.setCash(super.getCurrentCash() + guquanshu * guquandanjia);  //可用现金
			System.out.println(this.getAccountName() + "卖出后有现金" + this.getCurrentAvailableXianJin() );
			
			//卖出后账户当前成本的计算方法还没想好，现在采取把所有持仓成本加一遍的笨办法。
			double tmpchenben =0;
			for(StockChiCangInfo value: chicanggupiaodetailsmap.values()) {
				tmpchenben = tmpchenben + value.getChicangchenben(); 
			}
			this.setTotoalZiJingChenBen(tmpchenben);
		
			if(finalprofit != null)
				return String.valueOf(finalprofit);
			else
				return null;
 	}
	/*
	 * 如果金额不足，就返回null，否则返回挂单后还有多少现金
	 */
	public Double  actionBuyGuaDan(Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		if(checkIfSameDay(actionDay) == true) { //判断记录的日期是否是当天，如果是当天，则是当日记录，要判断资金的合法性，否则是过去记录导入，无需判断资金的合法性
			if( actionBuyPreCheck ( actionDay, stockcode,  guquanshu,  guquandanjia, shuoming) <0 ) {
				System.out.println("未通过买入挂单合法性检查");
				return null;
			}
		}
		
		this.setTotoalBuyGuaDanJinEr ( this.getTotoalBuyGuaDanJinEr() +  guquanshu * guquandanjia); //增加挂单的金额
		
		return this.getCurrentAvailableXianJin();
	}
	private void actionBuyGuaDanCannelled (Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		//记录的挂单金额相应减少
		this.setTotoalBuyGuaDanJinEr ( this.getTotoalBuyGuaDanJinEr() -guquanshu * guquandanjia);
	}
	/*
	 * 如果金额不足，就返回-1，否则返回挂单后还有多少现金
	 */
	public String actionSellGuaDan(Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		if(checkIfSameDay(actionDay) == true) { //判断记录的日期是否是当天，如果是当天，则是当日记录，要判断资金的合法性，否则是过去记录导入，无需判断资金的合法性
			if( actionSellPreCheck ( actionDay, stockcode,  guquanshu,  guquandanjia, shuoming) <0 ) {
				System.out.println("未通过卖出挂单合法性检查");
				return "FAIL";
			}
		}
		
		this.setTotoalSellGuaDanJinEr ( this.getTotoalSellGuaDanJinEr() + guquanshu * guquandanjia);
		return String.valueOf( this.getCurrentAvailableXianJin()  );
	}
	private void actionSellGuaDanCannelled (Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		this.setTotoalSellGuaDanJinEr ( this.getTotoalSellGuaDanJinEr() - guquanshu * guquandanjia);
	}
	/*
	 * 挂单结束的2个动作，一个是把挂单锁定的资金解锁，另一个是完成买入或卖出操作
	 */
	public Double actionBuyGuadanDone (Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		actionBuyGuaDanCannelled(actionDay, stockcode, guquanshu, guquandanjia, shuoming);
		return actionBuy(actionDay, stockcode, guquanshu, guquandanjia, shuoming);
	}
	/*
	 * 挂单结束的2个动作，一个是把挂单锁定的资金解锁，另一个是完成买入或卖出操作
	 */
	public String actionSellGuadanDone (Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		actionSellGuaDanCannelled(actionDay, stockcode, guquanshu, guquandanjia, shuoming);
		return  actionSell(actionDay, stockcode, guquanshu, guquandanjia, shuoming);
	}
	



}
