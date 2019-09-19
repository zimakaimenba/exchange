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
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.google.common.base.Strings;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.nodes.nodejibenmian.NodeJiBenMian;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForTA4J.StockXPeriodData;
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
		
		super.nodewkdata = new StockXPeriodData (myowncode1,NodeGivenPeriodDataItem.WEEK) ;
		super.nodedaydata = new StockXPeriodData (myowncode1,NodeGivenPeriodDataItem.DAY) ;
//		super.nodemonthdata = new StockNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
		super.nodetreerelated = new NodesTreeRelated (this);
		
	}
	
	private Boolean hasreviewedtoday; //锟节帮拷锟斤拷锟斤拷锟斤拷时锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟街撅拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟窖撅拷review锟斤拷锟矫革拷锟缴ｏ拷锟酵匡拷锟皆斤拷省时锟斤拷
//	private String checklistXml;
	private static Logger logger = Logger.getLogger(Stock.class);
	
//	private Multimap<String> chiCangAccountNameList; //锟斤拷锟叫该癸拷票锟斤拷锟斤拷锟斤拷锟剿伙拷锟斤拷锟斤拷锟斤拷
	private HashMap<String,AccountInfoBasic> chiCangAccounts; //锟斤拷锟叫该癸拷票锟斤拷锟斤拷锟斤拷锟剿伙拷<锟剿伙拷锟斤拷锟剿伙拷锟斤拷息>
//	private HashMap<String,StockOfBanKuai> suoShuTdxBanKuaiData; //锟斤拷锟斤拷通锟斤拷锟斤拷系统锟斤拷锟� <锟斤拷锟絚ode,锟斤拷锟斤拷锟斤拷锟�>
//	private  HashMap<String,Integer> sysBanKuaiWeight; //锟斤拷锟斤拷通锟斤拷锟斤拷系统锟斤拷锟饺拷锟�
//	private HashSet<String> suoShuZdyBanKuai; //锟斤拷锟斤拷锟皆讹拷锟斤拷锟斤拷
	private HashMap<String, String> suoShuCurSysBanKuai; //锟斤拷锟缴碉拷前锟斤拷锟斤拷锟斤拷通锟斤拷锟脚帮拷锟�
	
	
	public void setHasReviewedToday ()
	{
		this.hasreviewedtoday = true;
	}
	public Boolean wetherHasReiewedToday ()
	{
		if(this.hasreviewedtoday == null)
			return false;
		else
			return this.hasreviewedtoday;
	}
	
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
	public boolean isInTdxBanKuai (String tdxbk)
	{
		if(this.suoShuCurSysBanKuai.containsKey(tdxbk))
			return true;
		else 
			return false;
	}
	/**
	 * @return the suoShuBanKuai
	 */
	public HashMap<String,String> getGeGuCurSuoShuTDXSysBanKuaiList() 
	{
		if( this.suoShuCurSysBanKuai == null)
			return new HashMap<String,String> ();
		else
			return suoShuCurSysBanKuai;
	}

	/**
	 * @param tmpsysbk the suoShuBanKuai to set
	 */
	public void setGeGuCurSuoShuTDXSysBanKuaiList(HashMap<String,String> tmpsysbk) {
		this.suoShuCurSysBanKuai = tmpsysbk;
	}
 
	
	/**
	 * @return the suoShuBanKuai
	 */
//	public HashSet<String> getSuoShuTDXZdyBanKuai() {
//		return suoShuZdyBanKuai;
//	}

	/**
	 * @param tmpbk the suoShuBanKuai to set
	 */
//	public void setSuoShuTDXZdyBanKuai(HashSet<String> tmpbk) {
//		this.suoShuZdyBanKuai = tmpbk;
//	}
	
	ArrayList<String> nodeallchanyelian ;
	
	public void setGeGuAllChanYeLianInfo(ArrayList<String> ggcyl) 
	{
		this.nodeallchanyelian = ggcyl;
	}
	public ArrayList<String> getGeGuAllChanYeLianInfo() 
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
//		NodeXPeriodDataBasic stockxdata = this.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.DAY);
//		OHLCSeries ohlcdata = stockxdata.getOHLCData();
//		if(ohlcdata.getItemCount() <= 20)
//			return true;
//		else
//			return false;
		NodeJiBenMian tmpnodejbm = super.getNodeJiBenMian();
		LocalDate shangshiriqi = tmpnodejbm.getShangShiRiQi();
		if(com.exchangeinfomanager.commonlib.DayCounter.bestDaysBetweenIngoreWeekEnd(shangshiriqi, checkdate) <= 15)
			return true;
		else
			return false;
	}
}

