package com.exchangeinfomanager.nodes;

import java.time.DayOfWeek;
import java.time.LocalDate;
//import java.time.Period;
//import java.time.ZoneId;
//import java.time.temporal.ChronoUnit;
//import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collection;
//import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
//import java.util.List;
//import java.util.Locale;
import java.util.Map;
//import java.util.Random;
import java.util.Set;

//import org.apache.log4j.Logger;
//import org.jfree.data.category.DefaultCategoryDataset;
//import org.jfree.data.general.DefaultPieDataset;
//import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.joda.time.DateTime;
//import org.jfree.data.time.Week;
import org.joda.time.Interval;

import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.BanKuaiXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.treerelated.BanKuaiTreeRelated;

public class BanKuai extends TDXNodes
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static String  HASGGWITHSELFCJL = "HASGGWITHSELFCJL" , HASGGNOSELFCJL = "HASGGNOSELFCJL"
						, NOGGWITHSELFCJL = "NOGGWITHSELFCJL" , NOGGNOSELFCJL = "NOGGNOSELFCJL"; 
	
	public BanKuai(String bkcode,String name ) 
	{
		super(bkcode,name);
		super.nodetype = BkChanYeLianTreeNode.TDXBK;
		
		super.nodewkdata = new BanKuaiXPeriodDataForJFC (bkcode,NodeGivenPeriodDataItem.WEEK) ;
		super.nodedaydata = new BanKuaiXPeriodDataForJFC (bkcode,NodeGivenPeriodDataItem.DAY) ;

		super.nodetreerelated = new BanKuaiTreeRelated (this);
	}
	
//	private static Logger logger = Logger.getLogger(BanKuai.class);
//	private HashMap<String, Stock> allbkge;
	private String bankuaileixing; // 通达信里面定义的板块有几种：1.有个股自身有成交量数据 2. 有个股自身无成交量数据 3.无个股自身有成交量数据 
	private Boolean exporttogehpi = true;
	private Boolean importdailytradingdata = true;
	private Boolean showinbkfxgui = true;
	private Boolean showincyltree = true;
	private Boolean exporttowklyfile ;
	private List<BkChanYeLianTreeNode> stockofbklist; //存放所有的个股
	private Set<String> socialfriends;
	private Interval bkgegusearchtimerange;
	
	public void addSocialFriends (String friend)
	{
		if(socialfriends == null)
			socialfriends = new HashSet<String> ();
		
		socialfriends.add(friend);
	}
	public Set<String> getSocialFriendsSet ()
	{
		if(this.socialfriends == null)
			this.socialfriends = new HashSet<String> ();
		
		return this.socialfriends;
	}
	public  Boolean isExportTowWlyFile ()
	{
		if(exporttowklyfile == null)
			return true;
		else
			return this.exporttowklyfile;
	}
	public void setExportTowWlyFile (Boolean exporttofile)
	{
		if(exporttofile != null)
			this.exporttowklyfile = exporttofile;
	}
	
	public Boolean isShowincyltree () {
		if(this.showincyltree != null)
			return this.showincyltree;
		else
			return true;
	}
	public void setShowincyltree (Boolean showincyltree) {
		if(showincyltree != null)
			this.showincyltree = showincyltree;
	}
	public boolean isExporttogehpi() {
		if(exporttogehpi != null)
			return exporttogehpi;
		else
			return true;
	}
	/*
	 * 设置是否导出数据到Gephi
	 */
	public void setExporttogehpi(Boolean exporttogehpi) {
		if(exporttogehpi != null)
			this.exporttogehpi = exporttogehpi;
	}
	/*
	 * 设置是否要每天导入交易数据，设置为false的则在每天导入数据的时候跳过
	 */
	public boolean isImportdailytradingdata() {
		if(importdailytradingdata != null)
			return importdailytradingdata;
		else
			return true;
	}
	/*
	 * 设置是否导入每日交易数据
	 */
	public void setImportdailytradingdata(Boolean importdailytradingdata) {
		if(importdailytradingdata != null)
			this.importdailytradingdata = importdailytradingdata;
	}
	/*
	 * 
	 */
	public boolean isShowinbkfxgui() {
		if(showinbkfxgui != null)
			return showinbkfxgui;
		else
			return true;
	}
	/*
	 * 设置是否显示在板块分析窗口
	 */
	public void setShowinbkfxgui(Boolean showinbkfxgui) {
		if(showinbkfxgui != null)
			this.showinbkfxgui = showinbkfxgui;
	}
	/*
	 * 
	 */
	public void setBanKuaiLeiXing (String leixing)
	{
		if(leixing == null)
			this.bankuaileixing = null;
//			this.bankuaileixing = this.NOGGNOSELFCJL;
		else
		if(leixing.equals(this.HASGGNOSELFCJL) || leixing.equals(this.HASGGWITHSELFCJL) || leixing.equals(this.NOGGWITHSELFCJL) || leixing.equals(this.NOGGNOSELFCJL) ) {
			this.bankuaileixing = leixing;
			
			if(this.bankuaileixing.equals(this.HASGGNOSELFCJL) || leixing.equals(this.HASGGWITHSELFCJL) )
				this.stockofbklist = new ArrayList<> ();
		}
		
	}
	public String getBanKuaiLeiXing ()	{
		return this.bankuaileixing;
	}
	
	/*
	 * 
	 */
	public void addNewBanKuaiGeGu (StockOfBanKuai stockofbk) 
	{
		if( this.stockofbklist.indexOf(stockofbk) < 0 )
			this.stockofbklist.add(stockofbk);
	}
	 /**
	 * @return the tmpallbkge
	 * 
	 */
	public List<BkChanYeLianTreeNode> getAllGeGuOfBanKuaiInHistory() 
	{
		return this.stockofbklist;
    }
	public Interval getBanKuaiGeGuTimeRange ()
	{
		return this.bkgegusearchtimerange;
	}
	public void setBanKuaiGeGuTimeRange (LocalDate requiredstartday, LocalDate requiredendday)
	{
		DateTime requiredstartdt= new DateTime(requiredstartday.getYear(), requiredstartday.getMonthValue(), requiredstartday.getDayOfMonth(), 0, 0, 0, 0);
		DateTime requiredenddt= new DateTime(requiredendday.getYear(), requiredendday.getMonthValue(), requiredendday.getDayOfMonth(), 0, 0, 0, 0);
		this.bkgegusearchtimerange = new Interval(requiredstartdt,requiredenddt);
		 
	}
	/*
	 * 返回stockofBanKuai
	 */
	public Set<BkChanYeLianTreeNode> getSpecificPeriodStockOfBanKuai(LocalDate requireddate,int difference) 
	{
		Set<BkChanYeLianTreeNode> result = new HashSet<> ();

		for(BkChanYeLianTreeNode stockofbk : this.stockofbklist) {
		
			if(  ((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(requireddate)) 
					result.add(stockofbk);
		}
		
		return result;
	}
	/*
	 * 返回stock
	 */
	public Collection<BkChanYeLianTreeNode> getSpecificPeriodBanKuaiGeGu(LocalDate requireddate,int difference)
	{
		if(this.stockofbklist == null)
			return null;
		
		Collection<BkChanYeLianTreeNode> nodefortags = new HashSet<> ();
		for(BkChanYeLianTreeNode tmpstkofbks :  this.stockofbklist) {
			if(  ((StockOfBanKuai)tmpstkofbks).isInBanKuaiAtSpecificDate(requireddate)) {
				Stock stock = ((StockOfBanKuai)tmpstkofbks).getStock();
				nodefortags.add(stock);
			}
		}
		
		return nodefortags;
	}
	/*
	 * 
	 */
	public StockOfBanKuai getStockOfBanKuai (String stockcode)
	{
		for(BkChanYeLianTreeNode stockofbk : this.stockofbklist) {
			if(stockofbk.getMyOwnCode().toUpperCase().equals(stockcode.toUpperCase())) {
				return ((StockOfBanKuai)stockofbk);
			}
		}
		
		return null;
	}
	public Stock getBanKuaiGeGu (String stockcode)
	{
		for(BkChanYeLianTreeNode stockofbk : this.stockofbklist) {
			if(stockofbk.getMyOwnCode().toUpperCase().equals(stockcode.toUpperCase())) {
				return ((StockOfBanKuai)stockofbk).getStock();
			}
		}
		
		return null;
	}
	
	/**
	 * @return the sysBanKuaiWeight
	 */
	public Integer getGeGuSuoShuBanKuaiWeight(String stockcode) 
	{
		for(BkChanYeLianTreeNode stockofbk : this.stockofbklist) {
			if( ((StockOfBanKuai)stockofbk).getMyOwnCode().toUpperCase().equals(stockcode.toUpperCase())) {
				return ((StockOfBanKuai)stockofbk).getQuanZhong();
			}
		}
		
		return null;
	}
	/*
	 * 
	 */
	public void setGeGuSuoShuBanKuaiWeight(String stockcode , Integer quanzhong) 
	{

		
		for(BkChanYeLianTreeNode stockofbk : this.stockofbklist) {
			if(stockofbk.getMyOwnCode().toUpperCase().equals(stockcode.toUpperCase())) {
				((StockOfBanKuai)stockofbk).setStockQuanZhong(quanzhong);
			}
		}
	}
	

}
