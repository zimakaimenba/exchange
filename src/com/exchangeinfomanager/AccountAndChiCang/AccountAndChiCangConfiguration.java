package com.exchangeinfomanager.AccountAndChiCang;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.bouncycastle.util.Arrays.Iterator;

import com.exchangeinfomanager.accountconfiguration.AccountOperation.ZiJingHuaZhuan;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountPuTong;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountRongQuan;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountRongZi;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountXinYongPuTong;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.CashAccountBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;
import com.exchangeinfomanager.asinglestockinfo.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.AccountDbOperation;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;


public class AccountAndChiCangConfiguration 
{
	private AccountAndChiCangConfiguration() 
	{
		 acntdbop = new AccountDbOperation ();
//		 bkdbopt = new BanKuaiDbOperation ();
//		 stockdbopt = new StockDbOperations ();
	
		 cashaccountsdetailmap = new HashMap<String,CashAccountBasic>();
		 putongaccountsdetailmap = new HashMap<String,AccountPuTong>();
		 rzrqputongaccountsdetailmap = new HashMap<String,AccountXinYongPuTong>();
		 rongziaccountsdetailmap = new HashMap<String,AccountRongZi>();
		 rongquanaccountsdetailmap = new HashMap<String,AccountRongQuan> () ;
		 
//		 tdxstockdetailmap = new HashMap<String, Stock> ();
		 tdxbankuaidetailmap = new HashMap<String, BanKuai> ();  

		initializeAccounts ();
		initiazlizeStockChiCang ();
//		initiazlizeTodayOpt ();
	}
	
	 public static AccountAndChiCangConfiguration getInstance ()
	 {  
	        return Singtonle.instance;  
	 }
	 
	 private static class Singtonle 
	 {  
	        private static AccountAndChiCangConfiguration instance =  new AccountAndChiCangConfiguration ();  
	 }
	
//	SystemConfigration sysconfig ;

	private AccountDbOperation acntdbop;
//	private StockDbOperations stockdbopt;
//	private BanKuaiDbOperation bkdbopt;
	private HashMap<String, CashAccountBasic> cashaccountsdetailmap;
	private HashMap<String ,AccountPuTong> putongaccountsdetailmap;
	private HashMap<String ,AccountXinYongPuTong> rzrqputongaccountsdetailmap; 
	private HashMap<String ,AccountRongZi> rongziaccountsdetailmap;
	private HashMap<String ,AccountRongQuan> rongquanaccountsdetailmap;
	
	private Multimap<String, StockChiCangInfo>  acntChiCangdetailmap; //��ǰϵͳ�ֲ� <�˻������ֲ���Ϣ>
//	private HashMap<String, Stock>  tdxstockdetailmap; // <��Ʊcode���ֲ��˻�>
	private HashMap<String, BanKuai>  tdxbankuaidetailmap; // <���code���ֲ��˻�>
	private ArrayList<String> curChiCangStockCodeNamelist; //��ǰ���гֲֹ�Ʊ��namelist


	
	
	private void initializeAccounts ()
	{
		putongaccountsdetailmap = acntdbop.getPuTongSubAccounts ();
		rzrqputongaccountsdetailmap = acntdbop.getXinYongPuTongSubAccounts ();
		rongziaccountsdetailmap = acntdbop.getRongZiSubAccounts ();
		rongquanaccountsdetailmap = acntdbop.getRongQuanSubAccounts ();
		cashaccountsdetailmap = acntdbop.getCashSubAccounts ();
	}
	/*
	 * ��ʼ���ֲֹ�Ʊ�� 
	 * acntChiCangdetailmap; //��ǰϵͳ�ֲ� <�˻������ֲ���Ϣ>
	private Multimap<String, AccountInfoBasic>  stockChiCangdetailmap; //��ǰϵͳ�ֲ� <��Ʊ�����ֲ��˻�>
	private ArrayList<String> curChiCangStockCodeNamelist; //��ǰ���гֲֹ�Ʊ��namelist
	 */
	private void initiazlizeStockChiCang() 
	{
			acntChiCangdetailmap = acntdbop.getStockChiCang ();
//			stockChiCangdetailmap =  ArrayListMultimap.create();
			curChiCangStockCodeNamelist = new ArrayList<String> ();
			for (Map.Entry<String,StockChiCangInfo> entry : acntChiCangdetailmap.entries()) {
				String tmpstockacntname = (String) entry.getKey();
				StockChiCangInfo tmpstockchicanginfo = (StockChiCangInfo)entry.getValue();
				
				AccountInfoBasic tmpacnt = this.getAccount(tmpstockacntname);
				tmpacnt.addNewChiCangStock(tmpstockchicanginfo);
				
				String stockcode = tmpstockchicanginfo.getChicangcode();
				String stockname = tmpstockchicanginfo.getChicangname();
//				stockChiCangdetailmap.put(stockcode, tmpacnt);
				if(!curChiCangStockCodeNamelist.contains(stockcode+stockname))
					curChiCangStockCodeNamelist.add(stockcode+stockname);
			} 
		
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
	
	public boolean isSystemChiCang(String stockcode) 
	{
//		if(stockChiCangdetailmap.containsKey(stockcode))
//			return true;
//		else 
//			return false;
		for(String codename : curChiCangStockCodeNamelist)
			if(codename.contains(stockcode.trim()))
				return true;
		
		return false;
	}
	/*
	 * 
	 */
	public ArrayList<String> getStockChiCangName ()
	{
		return curChiCangStockCodeNamelist;
	}
	/*
	 * 
	 */
	public Stock setStockChiCangAccount (Stock stockneeded)
	{
		String stockcode = stockneeded.getMyOwnCode();
//		if(stockChiCangdetailmap.containsKey(stockname)) {
//			Collection<AccountInfoBasic> acntlists = stockChiCangdetailmap.get(stockname);
//			for(AccountInfoBasic tmpacnt : acntlists)
//				stockneeded.addNewChiCangAccount(tmpacnt);
//		}
		
		for (Map.Entry<String,StockChiCangInfo> entry : acntChiCangdetailmap.entries()) {
			String tmpstockacntname = (String) entry.getKey();
						
			AccountInfoBasic tmpacnt = this.getAccount(tmpstockacntname);
			if(tmpacnt.isChiCang (stockcode) )
				stockneeded.addNewChiCangAccount(tmpacnt);
			
		} 
		
		return stockneeded;
	}
	/*
	 * 
	 */
	public ArrayList<AccountInfoBasic> getStockChiCangAccount (String stockneededcode)
	{
//		if(stockChiCangdetailmap.containsKey(stockname)) {
//			Collection<AccountInfoBasic> acntlists = stockChiCangdetailmap.get(stockname);
//			for(AccountInfoBasic tmpacnt : acntlists)
//				stockneeded.addNewChiCangAccount(tmpacnt);
//		}
		
		ArrayList<AccountInfoBasic> stockchicangacntlist = new ArrayList<AccountInfoBasic> (); 
		for (Map.Entry<String,StockChiCangInfo> entry : acntChiCangdetailmap.entries()) { //��δ����д���
			String tmpstockacntname = (String) entry.getKey();
						
			AccountInfoBasic tmpacnt = this.getAccount(tmpstockacntname);
			if(tmpacnt.isChiCang (stockneededcode) && !stockchicangacntlist.contains(tmpacnt) )
				stockchicangacntlist.add(tmpacnt);
			
		} 
		
		
		return stockchicangacntlist;
	}
//	private Multimap<String, Stock>  tdxstockdetailmap; // <��Ʊ�����ֲ��˻�>
//	private Multimap<String, BanKuai>  tdxbankuaidetailmap; // <��Ʊ�����ֲ��˻�>
	/*
	 * 
	 */
//	public void addNewStock (Stock newstock)
//	{
//		String stockcode = newstock.getMyOwnCode();
//		tdxstockdetailmap.put(stockcode, newstock);
//	}
//	public Stock getStock (String stockcode) 
//	{
//		return tdxstockdetailmap.get(stockcode);
//	}


	/*
	 * ���ظù�Ʊ�ĳֲ��˻��ľ�����Ϣ
	 */
//	public ArrayList<AccountInfoBasic>  getStockChicangAccountsList (String stockcode)
//	{
//		ArrayList<AccountInfoBasic> tmpacnt = null;
//		
//		if(stockChiCangdetailmap.containsKey(stockcode) ) 
//		{
//			tmpacnt = new ArrayList<AccountInfoBasic> ();
//			Collection<AccountInfoBasic> tmpsglstock = this.stockChiCangdetailmap.get(stockcode); //�ӳֲ��б����ҵ���Ӧ�ĸù�Ʊ
//			tmpacnt = new ArrayList<AccountInfoBasic> (tmpsglstock);
//		}
//		
//		return tmpacnt;
//	}

	

		public ArrayList<String> getPutongaccountsnamelist() {
			return new ArrayList<String>(putongaccountsdetailmap.keySet());
		}
		public ArrayList<String> getRzrqputongaccountsnamelist() {
			return new ArrayList<String>(rzrqputongaccountsdetailmap.keySet());
		}
		public ArrayList<String> getRzrqrongziaccountsnamelist() {
			return new ArrayList<String>(rongziaccountsdetailmap.keySet());
		}
		public ArrayList<String> getRzrqrongqyanaccountsnamelist() {
			return new ArrayList<String>(rongquanaccountsdetailmap.keySet());
		} 


	
	public AccountInfoBasic getAccount (String tmpstockacntname)
	{
		
		if(tmpstockacntname.contains("��ȯ"))
			return rongquanaccountsdetailmap.get(tmpstockacntname);
		else if(tmpstockacntname.contains("����"))
			return   rongziaccountsdetailmap.get (tmpstockacntname);
		else if(tmpstockacntname.contains("������ͨ"))
			return  rzrqputongaccountsdetailmap.get(tmpstockacntname);
//		else if(tmpstockacntname.contains("�ֽ�"))
//			return cashaccountsdetailmap.get(tmpstockacntname);
		else
			return  putongaccountsdetailmap.get(tmpstockacntname);
	}
	
	public CashAccountBasic getCashAccount (String tmpstockacntname)
	{
		
		if(tmpstockacntname.contains("��ȯ"))
			return rongquanaccountsdetailmap.get(tmpstockacntname);
		else if(tmpstockacntname.contains("����"))
			return   rongziaccountsdetailmap.get (tmpstockacntname);
		else if(tmpstockacntname.contains("������ͨ"))
			return  rzrqputongaccountsdetailmap.get(tmpstockacntname);
		else if(tmpstockacntname.contains("�ֽ�"))
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

//	private boolean addNewChicangStock (Stock tmpnewstockinfo)
//	{
//		String stockcode = tmpnewstockinfo.getMyOwnCode();
//		if(!stockChiCangdetailmap.containsKey(stockcode) ) {
//			this.stockChiCangdetailmap.put(stockcode, tmpnewstockinfo);
//			return true;
//		} else 
//			return false;
//	}
	
	public  HashMap<String, CashAccountBasic>  getCashAccountListDetailMap() 
	{
		return cashaccountsdetailmap;
		
	}
	/**
	 * @param stockChiCangdetailmap the stockChiCangdetailmap to set
	 */
//	public void setStockChiCangdetailmap(HashMap<String, Stock> stockChiCangdetailmap) {
//		this.stockChiCangdetailmap = stockChiCangdetailmap;
//	}

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
//	public void setPutongaccountsdetailmap(HashMap<String, AccountPuTong> putongaccountsdetailmap) {
//		this.putongaccountsdetailmap = putongaccountsdetailmap;
//	}
	/**
	 * @return the rzrqputongaccountsnamelist
	 */

//	/**
//	 * @return the rzrqputongaccountsdetailmap
//	 */
	public HashMap<String, AccountXinYongPuTong> getRzrqputongaccountsdetailmap() {
		return rzrqputongaccountsdetailmap;
	}
//	/**
//	 * @param rzrqputongaccountsdetailmap the rzrqputongaccountsdetailmap to set
//	 */
//	public void setRzrqputongaccountsdetailmap(HashMap<String, AccountXinYongPuTong> rzrqputongaccountsdetailmap) {
//		this.rzrqputongaccountsdetailmap = rzrqputongaccountsdetailmap;
//	}
//	/**
//	 * @return the rongziaccountsdetailmap
//	 */
	public HashMap<String, AccountRongZi> getRongziaccountsdetailmap() {
		return rongziaccountsdetailmap;
	}
//	/**
//	 * @param rongziaccountsdetailmap the rongziaccountsdetailmap to set
//	 */
//	public void setRongziaccountsdetailmap(HashMap<String, AccountRongZi> rongziaccountsdetailmap) {
//		this.rongziaccountsdetailmap = rongziaccountsdetailmap;
//	}
	/**
	 * @return the rzrqrongziaccountsnamelist
	 */

//	/**
//	 * @param rzrqrongziaccountsnamelist the rzrqrongziaccountsnamelist to set
//	 */
////	public void setRzrqrongziaccountsnamelist(ArrayList<String> rzrqrongziaccountsnamelist) {
////		this.rzrqrongziaccountsnamelist = rzrqrongziaccountsnamelist;
////	}
//	/**
//	 * @return the rongquanaccountsdetailmap
//	 */
	public HashMap<String, AccountRongQuan> getRongquanaccountsdetailmap() {
		return rongquanaccountsdetailmap;
	}
//	/**
//	 * @param rongquanaccountsdetailmap the rongquanaccountsdetailmap to set
//	 */
//	public void setRongquanaccountsdetailmap(HashMap<String, AccountRongQuan> rongquanaccountsdetailmap) {
//		this.rongquanaccountsdetailmap = rongquanaccountsdetailmap;
//	}
	/**
	 * @return the rzrqrongqyanaccountsnamelist
	 */
//	/**
//	 * @return the stockChiCangdetailmap
//	 */
//	public HashMap<String, ASingleStockInfo> getStockChiCangdetailmap() {
//		return stockChiCangdetailmap;
//	}
/*
 * ��ʼ�����ղ���
 */
//	private void initiazlizeTodayOpt() 
//	{
//		todayoptmap = new  HashMap<String ,BuyStockNumberPrice> ();
//		Object[][] ptacntsmap = stockdbopt.getTodaysOperations ();
//	}

	
	
	
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




	
//	private void initializeCashSubAccounts() 
//	{
//		cashaccountsdetailmap = acntdbop.getCashSubAccounts ();
//    }
	
//	private void initializeRongquanSubAccounts ()
//	{
//		 rongquanaccountsdetailmap = acntdbop.getRongQuanSubAccounts ();
//	}
	
//	private void initializeRongziSubAccounts ()
//	{
//		rongziaccountsdetailmap = acntdbop.getRongZiSubAccounts ();
//	}
	
//	private void initializeRzrqPutongSubAccounts()
//	{
//		 rzrqputongaccountsdetailmap = acntdbop.getXinYongPuTongSubAccounts ();
//	}
//	private void initializePutongSubAccounts()
//	{
//		putongaccountsdetailmap = acntdbop.getPuTongSubAccounts ();
//	}

	/*
	 * ����Գֲֹ�Ʊlist���д���
	 * private Multimap<String, StockChiCangInfo>  acntChiCangdetailmap; //��ǰϵͳ�ֲ� <�˻������ֲ���Ϣ>
		private ArrayList<String> curChiCangStockCodeNamelist; //��ǰ���гֲֹ�Ʊ��namelist
	 */
	public void setBuyStockChiCangRelatedActions(BuyStockNumberPrice stocknumberpricepanel) 
	{
		boolean guadan = stocknumberpricepanel.getGuadan();
		if(guadan) //�ҵ������漰�ֲֵı仯
			return;
		
		String stockcode = stocknumberpricepanel.getStockcode();
		String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		int gushu = stocknumberpricepanel.getJiaoyiGushu();
		Double chenben = stocknumberpricepanel.getJiaoyiJiage() * gushu;
		
		 //�ù�Ʊ�Գֲ��˻��Ĵ���
		 if(this.isSystemChiCang(stockcode) ) { //�Ѿ��гֲֹ�Ʊ
//			 Collection<StockChiCangInfo> stockchicanginfo = acntChiCangdetailmap.get(actionstockaccount);
//			 for(StockChiCangInfo tmpscc : stockchicanginfo) {
//				 if(tmpscc.getChicangcode().equals(stockcode)) {
//					 tmpscc.setChicanggushu(gushu);
//					 tmpscc.setChicangchenben(chenben);
//				 }
//			 }
			  
			  
		 } else { //�����ǳֲֹ�Ʊ
			 
			 
			 curChiCangStockCodeNamelist.add(stockcode); 
			 
//			 Collection<StockChiCangInfo> stockchicanginfo = acntChiCangdetailmap.get(actionstockaccount);
			 
//			 AccountInfoBasic newstockacnt = this.getAccount(actionstockaccount);
//			 StockChiCangInfo newstockchicang = new StockChiCangInfo(stockcode, "", gushu, chenben);
//			 acntChiCangdetailmap.put(actionstockaccount, newstockchicang);
		 }
		
	}


	/*
	 * ������Ʊ��صĳֱֲ仯����
	 */
	public void setSellStockChiCangRelatedActions(BuyStockNumberPrice stocknumberpricepanel) 
	{
		boolean guadan = stocknumberpricepanel.getGuadan();
		
		if(guadan) //�ҵ������漰�ֲֵı仯
			return;
		
		String stockcode = stocknumberpricepanel.getStockcode();
		String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		int gushu = stocknumberpricepanel.getJiaoyiGushu();
		Double chenben = stocknumberpricepanel.getJiaoyiJiage() * gushu;
		
		 //�ù�Ʊ�Գֲ��˻��Ĵ���
		 if(!this.getStockChiCangAccount(stockcode).isEmpty() ) { //�Ѿ��гֲֹ�Ʊ
//			 Collection<StockChiCangInfo> stockchicanginfo = acntChiCangdetailmap.get(actionstockaccount);
//			 for(StockChiCangInfo tmpscc : stockchicanginfo) {
//				 if(tmpscc.getChicangcode().equals(stockcode)) {
//					 tmpscc.setChicanggushu(0-gushu);
//					 tmpscc.setChicangchenben(0-chenben);
//				 }
//			 }
			  
			  
		 } else { //�Ѿ����ǳֲֹ�Ʊ
			 String shouldremove = null ;
			 for(String codename : curChiCangStockCodeNamelist) 
				 if(codename.contains(stockcode))
					 shouldremove = codename;
					 
			 curChiCangStockCodeNamelist.remove(shouldremove);

			 
//			 Collection<StockChiCangInfo> stockchicanginfo = acntChiCangdetailmap.get(actionstockaccount);
//			 for(StockChiCangInfo tmpst : stockchicanginfo) {
//				 if(tmpst.getChicangcode().equals(stockcode))
//					 acntChiCangdetailmap.remove(actionstockaccount, tmpst);
//			 }
			 
		 }
	}

	/*
	 * ���˻��䶯���д���
	 */
	public Double setBuyAccountRelatedActions(BuyStockNumberPrice stocknumberpricepanel) 
	{
		
		int stocknumber = stocknumberpricepanel.getJiaoyiGushu();
		double stockprice = stocknumberpricepanel.getJiaoyiJiage();
		LocalDateTime actionday = stocknumberpricepanel.getActionDay();
		String stockcode = stocknumberpricepanel.getStockcode();
		String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		boolean guadan = stocknumberpricepanel.getGuadan();
		String shuoming = stocknumberpricepanel.getJiaoYiShuoMing();
		
		AccountInfoBasic tmpactionacnt = this.getAccount(actionstockaccount);
		
		Double acntchuliresult = null;
		if(!guadan) { //�˻�����,���ǹҵ�
			  acntchuliresult = tmpactionacnt.actionBuy(actionday,stockcode, stocknumber, stockprice,shuoming);
		}
		else { //�ǹҵ�
			acntchuliresult = tmpactionacnt.actionBuyGuaDan(actionday,stockcode, stocknumber, stockprice,shuoming);
		}

		return acntchuliresult;
	}
	/*
	 * �����������ص��˻��仯����������˻��ֲ�����Ϊ0���򷵻�Ԥ����������Ŀ��ӯ��Ϊ����������Ϊ����
	 */
	public String setSellAccountRelatedActions(BuyStockNumberPrice stocknumberpricepanel) 
	{
		int stocknumber = stocknumberpricepanel.getJiaoyiGushu();
		double stockprice = stocknumberpricepanel.getJiaoyiJiage();
		LocalDateTime actionday = stocknumberpricepanel.getActionDay();
		String stockcode = stocknumberpricepanel.getStockcode();
		String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		boolean guadan = stocknumberpricepanel.getGuadan();
		String shuoming = stocknumberpricepanel.getJiaoYiShuoMing();
		
		AccountInfoBasic tmpactionacnt = this.getAccount(actionstockaccount);
		
		
		String finailprofit = null;
		if(!guadan) { 
			//�˻�����
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
		LocalDateTime actionday = stocknumberpricepanel.getActionDay();
		String stockcode = stocknumberpricepanel.getStockcode();
		String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		//boolean guadan = stocknumberpricepanel.getGuadan();
		//String shuoming = stocknumberpricepanel.getJiaoYiShuoMing();
		boolean maimaisign = stocknumberpricepanel.isBuySell();
		
		AccountInfoBasic tmpaccount = this.getAccount(actionstockaccount);
		
		String finailprofit = null;
		if(maimaisign) {
			tmpaccount.actionBuyGuadanDone(actionday, stockcode, gushu, stockprice, null); //�ѹҵ���¼���ʽ���Ӧ���٣�����CANELL�Ĳ���
		} else { 
			
			try {
				finailprofit = tmpaccount.actionSellGuadanDone(actionday, stockcode, gushu, stockprice, null); //�ѹҵ���¼���ʽ���Ӧ���٣�����CANELL�Ĳ���
			} catch (java.lang.NullPointerException e) {
				
			}
			 if(finailprofit != null && !finailprofit.toUpperCase().equals("FAIL"))
				 stocknumberpricepanel.setProfit(Double.parseDouble(finailprofit) );
		}
		
		return finailprofit;
	}
	
	/*
	 * �κ�һ���������ױ�ȻҪ�漰�������в���
	 */
	public Integer buySellYuanZiOpertion(BuyStockNumberPrice stocknumberpricepanel) 
	{
		int autoIncKeyFromApi = -65535;
		String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		AccountInfoBasic tmpactionacnt = this.getAccount(actionstockaccount);
		if(stocknumberpricepanel.isBuySell()) { //��
			if(this.setBuyAccountRelatedActions (stocknumberpricepanel) == null) //�����˻��仯
				return -65535; 
			this.setBuyStockChiCangRelatedActions (stocknumberpricepanel); //����ֲֹ�Ʊ�ı仯
			
			 //�޸�˵�������ӵ�ǰ���ж��ٹ�,�����û�ֱ�۸���
			int curallgushu = tmpactionacnt.getChiCangGuPiaoGuShu(stocknumberpricepanel.getStockcode());
			if(curallgushu >=0)
				stocknumberpricepanel.setFormatedShuoMing(curallgushu);
			
			autoIncKeyFromApi =	 acntdbop.setBuyExchangeRelatedActions(stocknumberpricepanel); //�����ݿ�
			
			
		} else { //��
		
			if(!tmpactionacnt.isChiCang(stocknumberpricepanel.getStockcode()) )
				return -2;
			
			String finailprofit = this.setSellAccountRelatedActions (stocknumberpricepanel);//�����˻��仯
			if( finailprofit!= null && finailprofit.toUpperCase().equals("FAIL") )
				return -65535;
			
			if(finailprofit != null) 
				stocknumberpricepanel.setProfit(Double.parseDouble(finailprofit) );
			
			this.setSellStockChiCangRelatedActions (stocknumberpricepanel);//����ֲֹ�Ʊ�ı仯
				
			 //�޸�˵�������ӵ�ǰ���ж��ٹ�
			int curallgushu = tmpactionacnt.getChiCangGuPiaoGuShu(stocknumberpricepanel.getStockcode());
			if(curallgushu >=0)
				stocknumberpricepanel.setFormatedShuoMing(curallgushu);
				
			autoIncKeyFromApi =	acntdbop.setSellExchangeRelatedActions(stocknumberpricepanel);

		}
		
		return autoIncKeyFromApi;
	}

	/*
	 * 
	 */
	private void acntZiJingZhuanRuChu(CashAccountBasic actionacnt, CashAccountBasic mapacnt, ZiJingHuaZhuan zjhz) 
	{
		LocalDateTime actiondate = zjhz.getActionDate();
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
	 * �������������ȫ��������Ҫ��������
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

	/*
	 * Ŀǰ���ԶԵ��յ������¼���������˺�, �������漰���ò�����ԭ�Ӳ���
	 */
	public BuyStockNumberPrice changeBuyRecordAccountYuanZiChaoZuo(String currentacnt, String newacnt, Integer dabataseid) 
	{
		BuyStockNumberPrice stocknumberpricepanel = acntdbop.changeAcntToNewAcnt(currentacnt,dabataseid.intValue(),newacnt);
		
		AccountInfoBasic tmpcuraccount = this.getAccount(currentacnt); //�Ӽ����˻�list�����ҵ���ǰ�������˻�
		AccountInfoBasic tmpnewaccount = this.getAccount(newacnt); //�Ӽ����˻�list�����ҵ��µ��޸ĵ��˻�
		
		if(stocknumberpricepanel.isBuySell()) { //���������
			//���˻�����
			 this.setBuyAccountRelatedActions (stocknumberpricepanel); //�����˻��仯
			 this.setBuyStockChiCangRelatedActions (stocknumberpricepanel); //����ֲֹ�Ʊ�ı仯
			
		} else { //��������
			this.setSellAccountRelatedActions (stocknumberpricepanel);
			 this.setSellStockChiCangRelatedActions (stocknumberpricepanel);
		}
		
		stocknumberpricepanel.setBuySell(!stocknumberpricepanel.isBuySell());
		stocknumberpricepanel.setJiaoyiZhanghu(currentacnt);
		if(stocknumberpricepanel.isBuySell()) { //���������
			//���˻�����
			 this.setBuyAccountRelatedActions (stocknumberpricepanel); //�����˻��仯
			 this.setBuyStockChiCangRelatedActions (stocknumberpricepanel); //����ֲֹ�Ʊ�ı仯
			
		} else { //��������
			this.setSellAccountRelatedActions (stocknumberpricepanel);
			 this.setSellStockChiCangRelatedActions (stocknumberpricepanel);
		}

		
		return stocknumberpricepanel;
	}
}


