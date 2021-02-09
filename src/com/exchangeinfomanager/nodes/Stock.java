package com.exchangeinfomanager.nodes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.google.common.base.Strings;
import com.google.common.collect.Range;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Services.ServicesForNode;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.nodes.nodejibenmian.NodeJiBenMian;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.exchangeinfomanager.nodes.treerelated.StockOfBanKuaiTreeRelated;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;


public class Stock extends TDXNodes {

	public Stock(String myowncode1,String name)
	{
		super(myowncode1,name);
		super.nodetype = BkChanYeLianTreeNode.TDXGG;
		
		if(myowncode1.startsWith("60"))
			setSuoShuJiaoYiSuo("sh");
		else
			setSuoShuJiaoYiSuo("sz");
		
//		suoShuTdxBanKuaiData = new HashMap<String,StockOfBanKuai> ();
		
		super.nodewkdata = new StockXPeriodDataForJFC (myowncode1,NodeGivenPeriodDataItem.WEEK) ;
		super.nodedaydata = new StockXPeriodDataForJFC (myowncode1,NodeGivenPeriodDataItem.DAY) ;
//		super.nodemonthdata = new StockNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
		super.nodetreerelated = new StockOfBanKuaiTreeRelated (this);
		
	}
	
	 
	private static Logger logger = Logger.getLogger(Stock.class);
	
	private HashMap<String,AccountInfoBasic> chiCangAccounts; 
	private Set<BkChanYeLianTreeNode> suoShuCurTDXBanKuai; 
	private Set<BkChanYeLianTreeNode> suoShuCurDZHBanKuai;
	
	
	
	public void addNewChiCangAccount (AccountInfoBasic acnt)
	{
		if(chiCangAccounts == null)
			chiCangAccounts = new HashMap<String,AccountInfoBasic>();
		try {
			String acntname = acnt.getAccountName();
			if(!chiCangAccounts.containsKey(acntname))
				chiCangAccounts.put(acntname, acnt);
		} catch(java.lang.NullPointerException e) {
			e.printStackTrace();
			
		}
	}
	
	public void removeChiCangAccount (AccountInfoBasic acnt)
	{
		String acntname = acnt.getAccountName();
		chiCangAccounts.remove(acntname);
	}
	
	/**
	 * @return the chiCangAccountNameList
	 */
	public HashMap<String, AccountInfoBasic> getChiCangAccounts () {
		return chiCangAccounts;
	}
	public ArrayList<String> getChiCangAccountsNameList() 
	{
		
		return new  ArrayList<String>(chiCangAccounts.keySet());
	}
	/*
	 * 
	 */
	public boolean isInDZHBanKuai (String dzhbk)
	{
		for(BkChanYeLianTreeNode node : this.suoShuCurDZHBanKuai) {
			String nodecode = node.getMyOwnCode();
			String nodename = node.getMyOwnName();
			if(nodecode.equals(dzhbk) || nodename.equals(dzhbk))
				return true;
		}
		
		return false;
	}
	/**
	 * @return 
	 * @return the suoShuBanKuai
	 */
	public  Set<BkChanYeLianTreeNode> getGeGuCurSuoShuDZHSysBanKuaiList() 
	{
		if( this.suoShuCurDZHBanKuai == null)
			return new HashSet<>();
		else
			return suoShuCurDZHBanKuai;
	}

	/**
	 * @param stockbanks 
	 * @param tmpsysbk the suoShuBanKuai to set
	 */
	public void setGeGuCurSuoShuDZHSysBanKuaiList( Set<BkChanYeLianTreeNode> stockbanks) {
		this.suoShuCurDZHBanKuai = stockbanks;
	}
	
	/*
	 * 
	 */
	public boolean isInTdxBanKuai (String tdxbk)
	{
		for(BkChanYeLianTreeNode node : this.suoShuCurTDXBanKuai) {
			String nodecode = node.getMyOwnCode();
			String nodename = node.getMyOwnName();
			if(nodecode.equals(tdxbk) || nodename.equals(tdxbk))
				return true;
		}
		
		return false;
	}
	/**
	 * @return 
	 * @return the suoShuBanKuai
	 */
	public  Set<BkChanYeLianTreeNode> getGeGuCurSuoShuTDXSysBanKuaiList() 
	{
		if( this.suoShuCurTDXBanKuai == null)
			return new HashSet<>();
		else
			return suoShuCurTDXBanKuai;
	}

	/**
	 * @param stockbanks 
	 * @param tmpsysbk the suoShuBanKuai to set
	 */
	public void setGeGuCurSuoShuTDXSysBanKuaiList( Set<BkChanYeLianTreeNode> stockbanks) {
		this.suoShuCurTDXBanKuai = stockbanks;
	}
	
	
	
 
	List<String> nodeallchanyelian ;
	
	public void setGeGuAllChanYeLianInfo(List<String> ggcyl) 
	{
		this.nodeallchanyelian = ggcyl;
	}
	public List<String> getGeGuAllChanYeLianInfo() 
	{
		return this.nodeallchanyelian;
	}
	/*
	 * 
	 */
	protected Boolean isTingPai (LocalDate requireddate,int difference, String period )
	{
		NodeXPeriodData nodexdate = super.getNodeXPeroidData(period);
		if( nodexdate.getIndexOfSpecificDateOHLCData(requireddate, difference) != null) {
			boolean dapanxiushi = ((DaPan)getRoot()).isDaPanXiuShi(requireddate,difference,period );
			if(dapanxiushi)
				return false;
			else
				return true;
		} else
			return false;
	}
	
	/*
	* (non-Javadoc)
	* @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#addNewXPeriodData(com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem)
	*/
	public Boolean isVeryVeryNewXinStock (LocalDate checkdate)
	{
		NodeJiBenMian tmpnodejbm = super.getNodeJiBenMian();
		LocalDate shangshiriqi = tmpnodejbm.getShangShiRiQi();
		if(com.exchangeinfomanager.commonlib.DayCounter.bestDaysBetweenIngoreWeekEnd(shangshiriqi, checkdate) <= 15)
			return true;
		else
			return false;
	}
	
	public ServicesForNode getServicesForNode ()
	{
		return new SvsForNodeOfStock ();
	}
}

