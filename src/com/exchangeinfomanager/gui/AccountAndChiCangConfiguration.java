package com.exchangeinfomanager.gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.exchangeinfomanager.accountconfiguration.AccountOperation.ZiJingHuaZhuan;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountPuTong;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountRongQuan;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountRongZi;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountXinYongPuTong;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.CashAccountBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;
import com.exchangeinfomanager.asinglestockinfo.ASingleStockInfo;
import com.exchangeinfomanager.database.AccountDbOperation;
import com.exchangeinfomanager.database.ConnectDatabase;
import com.exchangeinfomanager.database.StockDbOperations;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;


public class AccountAndChiCangConfiguration 
{
	AccountAndChiCangConfiguration() 
	{
		 acntdbop = new AccountDbOperation ();
		 stockdbopt = new StockDbOperations ();
	
		 cashaccountsdetailmap = new HashMap<String,CashAccountBasic>();
		 putongaccountsdetailmap = new HashMap<String,AccountPuTong>();
		 rzrqputongaccountsdetailmap = new HashMap<String,AccountXinYongPuTong>();
		 rongziaccountsdetailmap = new HashMap<String,AccountRongZi>();
		 rongquanaccountsdetailmap = new HashMap<String,AccountRongQuan> () ;

		initializeAccounts ();
		initiazlizeStockChiCang ();
		//initiazlizeTodayOpt ();
	}
	
	

	SystemConfigration sysconfig ;

	private AccountDbOperation acntdbop;
	private StockDbOperations stockdbopt;
	private HashMap<String, CashAccountBasic> cashaccountsdetailmap;
	private HashMap<String ,AccountPuTong> putongaccountsdetailmap;
	private HashMap<String ,AccountXinYongPuTong> rzrqputongaccountsdetailmap; 
	private HashMap<String ,AccountRongZi> rongziaccountsdetailmap;
	private HashMap<String ,AccountRongQuan> rongquanaccountsdetailmap;
	private HashMap<String ,ASingleStockInfo> stockChiCangdetailmap; //当前系统持仓
	private HashMap<String ,BuyStockNumberPrice> todayoptmap; //当前系统持仓
	

	public boolean isSystemChiCang(String stockcode) 
	{

		if(stockChiCangdetailmap.containsKey(stockcode) )
			return true;
		else return false;
	}
	public AccountInfoBasic getAccount (String tmpstockacntname)
	{
		
		if(tmpstockacntname.contains("融券"))
			return rongquanaccountsdetailmap.get(tmpstockacntname);
		else if(tmpstockacntname.contains("融资"))
			return   rongziaccountsdetailmap.get (tmpstockacntname);
		else if(tmpstockacntname.contains("信用普通"))
			return  rzrqputongaccountsdetailmap.get(tmpstockacntname);
//		else if(tmpstockacntname.contains("现金"))
//			return cashaccountsdetailmap.get(tmpstockacntname);
		else
			return  putongaccountsdetailmap.get(tmpstockacntname);
	}
	
	public CashAccountBasic getCashAccount (String tmpstockacntname)
	{
		
		if(tmpstockacntname.contains("融券"))
			return rongquanaccountsdetailmap.get(tmpstockacntname);
		else if(tmpstockacntname.contains("融资"))
			return   rongziaccountsdetailmap.get (tmpstockacntname);
		else if(tmpstockacntname.contains("信用普通"))
			return  rzrqputongaccountsdetailmap.get(tmpstockacntname);
		else if(tmpstockacntname.contains("现金"))
			return cashaccountsdetailmap.get(tmpstockacntname);
		else
			return  putongaccountsdetailmap.get(tmpstockacntname);
	}
	
	public AccountInfoBasic getAccountByAccountId (String tmpstockacntid)
	{
		for(String acntname:putongaccountsdetailmap.keySet())
			if(putongaccountsdetailmap.get(acntname).getAccountid().equals(tmpstockacntid) )
				return putongaccountsdetailmap.get(acntname);
		
		for(String acntname:rongquanaccountsdetailmap.keySet())
			if(rongquanaccountsdetailmap.get(acntname).getAccountid().equals(tmpstockacntid) )
				return rongquanaccountsdetailmap.get(acntname);
		
		for(String acntname:rongziaccountsdetailmap.keySet())
			if(rongziaccountsdetailmap.get(acntname).getAccountid().equals(tmpstockacntid) )
				return rongziaccountsdetailmap.get(acntname);
		
		for(String acntname:rzrqputongaccountsdetailmap.keySet())
			if(rzrqputongaccountsdetailmap.get(acntname).getAccountid().equals(tmpstockacntid) )
				return rzrqputongaccountsdetailmap.get(acntname);
		
		return null;
	}
//	public CashAccountBasic getCashAccount (String cashacntname)
//	{
//		return cashaccountsdetailmap.get(cashacntname);
//	}
	public ASingleStockInfo getChicangStock (String stockcode)
	{
		if(stockChiCangdetailmap.containsKey(stockcode) )
			return this.stockChiCangdetailmap.get(stockcode);
		else 
			return null;
	}
	/*
	 * 返回该股票的持仓账户的具体信息
	 */
	public ArrayList<AccountInfoBasic>  getStockChicangAccountsList (String stockcode)
	{
		ArrayList<AccountInfoBasic> tmpacnt = null;
		
		if(stockChiCangdetailmap.containsKey(stockcode) ) 
		{
			tmpacnt = new ArrayList<AccountInfoBasic> ();
			ASingleStockInfo tmpsglstock = this.stockChiCangdetailmap.get(stockcode); //从持仓列表中找到相应的该股票
			ArrayList<String> acntnamelist = tmpsglstock.getChiCangAccountNameList();
			for(String tmpactname:acntnamelist){
				tmpacnt.add(this.getAccount(tmpactname) );
			}
		}
		
		return tmpacnt;
	}
	public boolean addNewChicangStock (ASingleStockInfo tmpnewstockinfo)
	{
		String stockcode = tmpnewstockinfo.getStockcode();
		if(!stockChiCangdetailmap.containsKey(stockcode) ) {
			//this.stockChiCangnamelist.add(stockcode);
			this.stockChiCangdetailmap.put(stockcode, tmpnewstockinfo);
			return true;
		} else 
			return false;
	}
	
	public  HashMap<String, CashAccountBasic>  getCashAccountListDetailMap() 
	{
		return cashaccountsdetailmap;
		
	}
	/**
	 * @param stockChiCangdetailmap the stockChiCangdetailmap to set
	 */
	public void setStockChiCangdetailmap(HashMap<String, ASingleStockInfo> stockChiCangdetailmap) {
		this.stockChiCangdetailmap = stockChiCangdetailmap;
	}




	/**
	 * @return the putongaccountsnamelist
	 */
	public ArrayList<String> getPutongaccountsnamelist() {
		return new ArrayList<String>(putongaccountsdetailmap.keySet());
	}
	/**
	 * @param putongaccountsnamelist the putongaccountsnamelist to set
	 */
//	public void setPutongaccountsnamelist(ArrayList<String> putongaccountsnamelist) {
//		this.putongaccountsnamelist = putongaccountsnamelist;
//	}
	/**
	 * @return the putongaccountsdetailmap
	 */
	public HashMap<String, AccountPuTong> getPutongaccountsdetailmap() {
		return putongaccountsdetailmap;
	}
	/**
	 * @param putongaccountsdetailmap the putongaccountsdetailmap to set
	 */
	public void setPutongaccountsdetailmap(HashMap<String, AccountPuTong> putongaccountsdetailmap) {
		this.putongaccountsdetailmap = putongaccountsdetailmap;
	}
	/**
	 * @return the rzrqputongaccountsnamelist
	 */
	public ArrayList<String> getRzrqputongaccountsnamelist() {
		return new ArrayList<String>(rzrqputongaccountsdetailmap.keySet());
	}
	/**
	 * @return the rzrqputongaccountsdetailmap
	 */
	public HashMap<String, AccountXinYongPuTong> getRzrqputongaccountsdetailmap() {
		return rzrqputongaccountsdetailmap;
	}
	/**
	 * @param rzrqputongaccountsdetailmap the rzrqputongaccountsdetailmap to set
	 */
	public void setRzrqputongaccountsdetailmap(HashMap<String, AccountXinYongPuTong> rzrqputongaccountsdetailmap) {
		this.rzrqputongaccountsdetailmap = rzrqputongaccountsdetailmap;
	}
	/**
	 * @return the rongziaccountsdetailmap
	 */
	public HashMap<String, AccountRongZi> getRongziaccountsdetailmap() {
		return rongziaccountsdetailmap;
	}
	/**
	 * @param rongziaccountsdetailmap the rongziaccountsdetailmap to set
	 */
	public void setRongziaccountsdetailmap(HashMap<String, AccountRongZi> rongziaccountsdetailmap) {
		this.rongziaccountsdetailmap = rongziaccountsdetailmap;
	}
	/**
	 * @return the rzrqrongziaccountsnamelist
	 */
	public ArrayList<String> getRzrqrongziaccountsnamelist() {
		return new ArrayList<String>(rongziaccountsdetailmap.keySet());
	}
	/**
	 * @param rzrqrongziaccountsnamelist the rzrqrongziaccountsnamelist to set
	 */
//	public void setRzrqrongziaccountsnamelist(ArrayList<String> rzrqrongziaccountsnamelist) {
//		this.rzrqrongziaccountsnamelist = rzrqrongziaccountsnamelist;
//	}
	/**
	 * @return the rongquanaccountsdetailmap
	 */
	public HashMap<String, AccountRongQuan> getRongquanaccountsdetailmap() {
		return rongquanaccountsdetailmap;
	}
	/**
	 * @param rongquanaccountsdetailmap the rongquanaccountsdetailmap to set
	 */
	public void setRongquanaccountsdetailmap(HashMap<String, AccountRongQuan> rongquanaccountsdetailmap) {
		this.rongquanaccountsdetailmap = rongquanaccountsdetailmap;
	}
	/**
	 * @return the rzrqrongqyanaccountsnamelist
	 */
	public ArrayList<String> getRzrqrongqyanaccountsnamelist() {
		return new ArrayList<String>(rongquanaccountsdetailmap.keySet());
	}
	/**
	 * @return the stockChiCangdetailmap
	 */
	public HashMap<String, ASingleStockInfo> getStockChiCangdetailmap() {
		return stockChiCangdetailmap;
	}
/*
 * 初始化今日操作
 */
//	private void initiazlizeTodayOpt() 
//	{
//		todayoptmap = new  HashMap<String ,BuyStockNumberPrice> ();
//		Object[][] ptacntsmap = stockdbopt.getTodaysOperations ();
//	}
/*
 * 初始化持仓股票。方法是
 */
	private void initiazlizeStockChiCang() 
	{
		stockChiCangdetailmap = new HashMap<String ,ASingleStockInfo>();//保存所有系统持仓股票，结构是<持仓股票代码，持仓股票所有信息>
		
		Object[][] ptacntsmap = acntdbop.getStockChiCang ();
		for (int i=0;i<ptacntsmap.length;i++) { 
			//czjl.买卖账号, czjl.股票代码 ,agu.股票名称, SUM(买卖股数) , SUM(买卖金额)
			Object[] data = ptacntsmap[i];
			String tmpstockacntname = data[0].toString();
			String tmpstockcode = data[1].toString();
			String tmpstockname = data[2].toString();
			int tmpstocknumber = Integer.parseInt( data[3].toString() );
			double tmpstockchengben = Double.parseDouble( data[4].toString());
			
	
			try {
				//设置持仓的账户
				AccountInfoBasic tmpacnt = null;
				tmpacnt = this.getAccount(tmpstockacntname);
			
				StockChiCangInfo tmpstockchicanginfo = new StockChiCangInfo(tmpstockcode.trim(), tmpstockname,
						tmpstocknumber, tmpstockchengben);
				
				tmpacnt.addNewChiCangStock(tmpstockchicanginfo);
			} catch (java.lang.NullPointerException e) {
				System.out.println("在账户列表中未能找到持有该股票的账户！" + tmpstockacntname);
			}
			
			
			//设置持仓的股票
			if(stockChiCangdetailmap.containsKey(tmpstockcode)) { //
				 this.getChicangStock(tmpstockcode).addNewChiCangAccountName(tmpstockacntname);//把账户名加入到该股票的持仓账户list里面
			} else {
				ASingleStockInfo tmpsiglestockinfo = new ASingleStockInfo(tmpstockcode.trim());
				tmpsiglestockinfo.setStockname(tmpstockname.trim());
				tmpsiglestockinfo.addNewChiCangAccountName (tmpstockacntname);
				
				//stockChiCangnamelist.add(tmpstockcode);  //持仓股票
				stockChiCangdetailmap.put(tmpstockcode,tmpsiglestockinfo); //
			}
			
		}
	
	}
	 
	
	public void SaveAccountsInfo ()
	{
		for(CashAccountBasic oneacnt : cashaccountsdetailmap.values())
			acntdbop.setCashAccountXianjing (oneacnt);
		
		for(AccountInfoBasic oneacnt : putongaccountsdetailmap.values() )
			acntdbop.setAccountInfoToDb(oneacnt);
		
		for(AccountInfoBasic oneacnt : rzrqputongaccountsdetailmap.values() )
			acntdbop.setAccountInfoToDb(oneacnt);
		
		for(AccountInfoBasic oneacnt : rongziaccountsdetailmap.values() )
			acntdbop.setAccountInfoToDb(oneacnt);
		
		for(AccountInfoBasic oneacnt : rongquanaccountsdetailmap.values() )
			acntdbop.setAccountInfoToDb(oneacnt);
	}



	private void initializeAccounts ()
	{
		initializePutongSubAccounts();
		initializeRzrqPutongSubAccounts ();
		initializeRongziSubAccounts ();
		initializeRongquanSubAccounts ();
		initializeCashSubAccounts ();
	}
	public void refreshAccounts()
	{
		 cashaccountsdetailmap.clear();
		 putongaccountsdetailmap.clear(); 
		 rzrqputongaccountsdetailmap.clear(); 
		 rongziaccountsdetailmap.clear(); 
		 rongquanaccountsdetailmap.clear(); 
		 
		initializeAccounts ();
	}
	
	private void initializeCashSubAccounts() 
	{
		cashaccountsdetailmap = acntdbop.getCashSubAccounts ();
    }
	
	private void initializeRongquanSubAccounts ()
	{
		 rongquanaccountsdetailmap = acntdbop.getRongQuanSubAccounts ();
	}
	
	private void initializeRongziSubAccounts ()
	{
		rongziaccountsdetailmap = acntdbop.getRongZiSubAccounts ();
	}
	
	private void initializeRzrqPutongSubAccounts()
	{
		 rzrqputongaccountsdetailmap = acntdbop.getXinYongPuTongSubAccounts ();
	}
	private void initializePutongSubAccounts()
	{
		putongaccountsdetailmap = acntdbop.getPuTongSubAccounts ();
	}

	/*
	 * 买入对持仓股票list进行处理
	 */
	public void setBuyStockChiCangRelatedActions(BuyStockNumberPrice stocknumberpricepanel) 
	{
		String stockcode = stocknumberpricepanel.getStockcode();
		String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		boolean guadan = stocknumberpricepanel.getGuadan();
		
		
		if(guadan) //挂单还不涉及持仓的变化
			return; 
		
		 //该股票对持仓账户的处理
		 if(this.getChicangStock(stockcode) !=null) { //已经市持仓股票
			  this.getChicangStock(stockcode).addNewChiCangAccountName(actionstockaccount);
		 } else { //还不是持仓股票
			 
			 ASingleStockInfo tmpnewchicang = new ASingleStockInfo(stockcode); 
			 
			 tmpnewchicang.addNewChiCangAccountName(actionstockaccount);
			 
			 this.addNewChicangStock(tmpnewchicang);
		 }
		
	}


	/*
	 * 卖出股票相关的持仓变化操作
	 */
	public void setSellStockChiCangRelatedActions(BuyStockNumberPrice stocknumberpricepanel) 
	{
		String stockcode = stocknumberpricepanel.getStockcode();
		String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		boolean guadan = stocknumberpricepanel.getGuadan();

		
		if(guadan) //挂单还不涉及持仓的变化
			return;
		
		//确认该股票还是某个账户的持仓
		ASingleStockInfo tmpactstock =  stockChiCangdetailmap.get(stockcode); //找到该股票
		if(actionstockaccount.contains("融券")) { //融券账户卖出，则该股票应该加入持仓
			tmpactstock.addNewChiCangAccountName(actionstockaccount);
		} else { // 卖出那肯定是从现有的持仓账户查找是否还有该股票,
			
			ArrayList<String> tmpchicangnamelist = tmpactstock.getChiCangAccountNameList(); //找到该股票所有的持仓账户的名字
			if(!this.getAccount(actionstockaccount).isChiCang(stockcode))
				tmpchicangnamelist.remove(actionstockaccount); //如果股票已经不是该账户的股票，从列表中移除账户
			
			if(tmpchicangnamelist.isEmpty()) { //账户列表空，说明已经不是系统持仓股票
				stockChiCangdetailmap.remove(stockcode);
				 System.out.println(stockcode + "已经不是系统持仓");
			}
			
		}
	}

	/*
	 * 对账户变动进行处理
	 */
	public Double setBuyAccountRelatedActions(BuyStockNumberPrice stocknumberpricepanel) 
	{
		
		int stocknumber = stocknumberpricepanel.getJiaoyiGushu();
		double stockprice = stocknumberpricepanel.getJiaoyiJiage();
		Date actionday = stocknumberpricepanel.getActionDay();
		String stockcode = stocknumberpricepanel.getStockcode();
		String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		boolean guadan = stocknumberpricepanel.getGuadan();
		String shuoming = stocknumberpricepanel.getJiaoYiShuoMing();
		
		AccountInfoBasic tmpactionacnt = this.getAccount(actionstockaccount);
		
		Double acntchuliresult = null;
		if(!guadan) { //账户处理,不是挂单
			  acntchuliresult = tmpactionacnt.actionBuy(actionday,stockcode, stocknumber, stockprice,shuoming);
		}
		else { //是挂单
			acntchuliresult = tmpactionacnt.actionBuyGuaDan(actionday,stockcode, stocknumber, stockprice,shuoming);
		}

		return acntchuliresult;
	}
	/*
	 * 处理和卖出相关的账户变化，如果卖完账户持仓数变为0，则返回预估的利润数目，盈利为负数，亏损为正数
	 */
	public String setSellAccountRelatedActions(BuyStockNumberPrice stocknumberpricepanel) 
	{
		int stocknumber = stocknumberpricepanel.getJiaoyiGushu();
		double stockprice = stocknumberpricepanel.getJiaoyiJiage();
		Date actionday = stocknumberpricepanel.getActionDay();
		String stockcode = stocknumberpricepanel.getStockcode();
		String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		boolean guadan = stocknumberpricepanel.getGuadan();
		String shuoming = stocknumberpricepanel.getJiaoYiShuoMing();
		
		AccountInfoBasic tmpactionacnt = this.getAccount(actionstockaccount);
		
		
		String finailprofit = null;
		if(!guadan) { 
			//账户处理
			try {
				finailprofit =  tmpactionacnt.actionSell(actionday,stockcode, stocknumber, stockprice,shuoming);
			} catch (java.lang.NullPointerException e) {
				
			}
		} else {
			finailprofit = tmpactionacnt.actionSellGuaDan(actionday,stockcode, stocknumber, stockprice,shuoming);
		}
		
		return finailprofit;
	}
	
	public String setGuaDanFinished (BuyStockNumberPrice stocknumberpricepanel)
	{
		int gushu = stocknumberpricepanel.getJiaoyiGushu();
		double stockprice = stocknumberpricepanel.getJiaoyiJiage();
		Date actionday = stocknumberpricepanel.getActionDay();
		String stockcode = stocknumberpricepanel.getStockcode();
		String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		//boolean guadan = stocknumberpricepanel.getGuadan();
		//String shuoming = stocknumberpricepanel.getJiaoYiShuoMing();
		boolean maimaisign = stocknumberpricepanel.isBuySell();
		
		AccountInfoBasic tmpaccount = this.getAccount(actionstockaccount);
		
		String finailprofit = null;
		if(maimaisign) {
			tmpaccount.actionBuyGuadanDone(actionday, stockcode, gushu, stockprice, null); //把挂单记录的资金相应减少，复合CANELL的操作
		} else { 
			
			try {
				finailprofit = tmpaccount.actionSellGuadanDone(actionday, stockcode, gushu, stockprice, null); //把挂单记录的资金相应减少，复合CANELL的操作
			} catch (java.lang.NullPointerException e) {
				
			}
			 if(finailprofit != null && !finailprofit.toUpperCase().equals("FAIL"))
				 stocknumberpricepanel.setProfit(Double.parseDouble(finailprofit) );
		}
		
		return finailprofit;
	}
	
	/*
	 * 任何一笔买卖交易必然要涉及到的所有操作
	 */
	public Integer buySellYuanZiOpertion(BuyStockNumberPrice stocknumberpricepanel) 
	{
		int autoIncKeyFromApi = -65535;
		String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		AccountInfoBasic tmpactionacnt = this.getAccount(actionstockaccount);
		if(stocknumberpricepanel.isBuySell()) { //买
			if(this.setBuyAccountRelatedActions (stocknumberpricepanel) == null) //处理账户变化
				return -65535; 
			this.setBuyStockChiCangRelatedActions (stocknumberpricepanel); //处理持仓股票的变化
			
			 //修改说明，增加当前持有多少股,便于用户直观感受
			int curallgushu = tmpactionacnt.getChiCangGuPiaoGuShu(stocknumberpricepanel.getStockcode());
			if(curallgushu >=0)
				stocknumberpricepanel.setFormatedShuoMing(curallgushu);
			
			autoIncKeyFromApi =	 acntdbop.setBuyExchangeRelatedActions(stocknumberpricepanel); //存数据库
		} else { //卖
		
			if(!tmpactionacnt.isChiCang(stocknumberpricepanel.getStockcode()) )
				return -2;
			
			String finailprofit = this.setSellAccountRelatedActions (stocknumberpricepanel);//处理账户变化
			if( finailprofit!= null && finailprofit.toUpperCase().equals("FAIL") )
				return -65535;
			
			if(finailprofit != null) 
				stocknumberpricepanel.setProfit(Double.parseDouble(finailprofit) );
			
			this.setSellStockChiCangRelatedActions (stocknumberpricepanel);//处理持仓股票的变化
				
			 //修改说明，增加当前持有多少股
			int curallgushu = tmpactionacnt.getChiCangGuPiaoGuShu(stocknumberpricepanel.getStockcode());
			if(curallgushu >=0)
				stocknumberpricepanel.setFormatedShuoMing(curallgushu);
				
			autoIncKeyFromApi =	acntdbop.setSellExchangeRelatedActions(stocknumberpricepanel);

		}
		
		return autoIncKeyFromApi;
	}

	
	private void acntZiJingZhuanRuChu(CashAccountBasic actionacnt, CashAccountBasic mapacnt, ZiJingHuaZhuan zjhz) 
	{
		Date actiondate = zjhz.getActionDate();
		double actionmoney = zjhz.getZijing(); 
		String quanshangleixing = zjhz.getQuanShangZhuanRuOrChuLeiXing ();
		
		if(actionacnt != null) {
			actionacnt.ZiJingZhuanRu(actiondate,quanshangleixing,actionmoney);
			acntdbop.setZijingLiuShui(actiondate, actionacnt.getAccountName(), zjhz.getQuanShangZhuanRuOrChuLeiXing(), true, actionmoney, zjhz.getShuoMing());
			acntdbop.setAccountInfoToDb (actionacnt);
		}
		
		if(mapacnt != null) {
			mapacnt.ZiJingZhuanChu(actiondate,quanshangleixing,actionmoney);
			acntdbop.setZijingLiuShui(actiondate, mapacnt.getAccountName(), zjhz.getQuanShangZhuanRuOrChuLeiXing(), false, actionmoney, zjhz.getShuoMing());
			acntdbop.setAccountInfoToDb (mapacnt); 
		}
	}
	public void accountZiJingYuanZiOpt(CashAccountBasic actionacnt, ZiJingHuaZhuan zjhz)
	{
		String mapacntname = zjhz.getZhuanRuOrChuActionAccount();
		CashAccountBasic mapacnt = null;
		if(mapacntname != null) 
			mapacnt = this.getCashAccount(mapacntname);
		
		if(zjhz.isZhuanRuChu() ) {
			acntZiJingZhuanRuChu (actionacnt,mapacnt,zjhz);
		} else {
			acntZiJingZhuanRuChu (mapacnt,actionacnt,zjhz);
		}

	}

	/*
	 * 卖出后如果是完全卖出，则要计算利润
	 */
	public int afterSellCheckAndSetProfitYuanZiOperation(AccountInfoBasic actiozhuanrunaccount,  BuyStockNumberPrice stocknumberpricepanel)
	{
		String actionstockcode = stocknumberpricepanel.getStockcode();
		int autoIncKeyFromApi = -1;
		if(!actiozhuanrunaccount.isChiCang(actionstockcode) ) {
			acntdbop.setChicangKongcang (actiozhuanrunaccount,actionstockcode);
			autoIncKeyFromApi = acntdbop.setGeguProft (stocknumberpricepanel);
		}
		
		return autoIncKeyFromApi;
		
	}
//CLASS END
}


