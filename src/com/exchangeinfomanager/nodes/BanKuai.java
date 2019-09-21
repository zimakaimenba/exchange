package com.exchangeinfomanager.nodes;

import java.time.DayOfWeek;
import java.time.LocalDate;
//import java.time.Period;
//import java.time.ZoneId;
//import java.time.temporal.ChronoUnit;
//import java.time.temporal.WeekFields;
import java.util.ArrayList;
//import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
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
//import org.jfree.data.time.Week;

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
						, NOGGWITHSELFCJL = "NOGGWITHSELFCJL" , NOGGNOSELFCJL = "NOGGNOSELFCJL"; // 通锟斤拷锟斤拷锟斤拷锟芥定锟斤拷陌锟斤拷锟叫硷拷锟街ｏ拷1.锟叫革拷锟斤拷锟斤拷锟斤拷锟叫成斤拷锟斤拷锟斤拷锟斤拷 2. 锟叫革拷锟斤拷锟斤拷锟斤拷锟睫成斤拷锟斤拷锟斤拷锟斤拷 3.锟睫革拷锟斤拷锟斤拷锟斤拷锟叫成斤拷锟斤拷锟斤拷锟斤拷
	
	public BanKuai(String bkcode,String name ) 
	{
		super(bkcode,name);
		super.nodetype = BkChanYeLianTreeNode.TDXBK;
		
		super.nodewkdata = new BanKuaiXPeriodDataForJFC (bkcode,NodeGivenPeriodDataItem.WEEK) ;
		super.nodedaydata = new BanKuaiXPeriodDataForJFC (bkcode,NodeGivenPeriodDataItem.DAY) ;

//		super.nodewkdata = new BanKuaiNodeXPeriodData (bkcode,TDXNodeGivenPeriodDataItem.WEEK) ;
//		super.nodedaydata = new BanKuaiNodeXPeriodData (bkcode,TDXNodeGivenPeriodDataItem.DAY) ;
////		super.nodemonthdata = new BanKuaiNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
		
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
	private ArrayList<StockOfBanKuai> stockofbklist; //存放所有的个股
	private Set<String> socialfriends;
	
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
			this.exporttowklyfile = exporttofile;
	}
	
	public Boolean isShowincyltree () {
		return this.showincyltree;
	}
	public void setShowincyltree (Boolean showincyltree) {
		this.showincyltree = showincyltree;
	}
	public boolean isExporttogehpi() {
		return exporttogehpi;
	}
	/*
	 * 设置是否导出数据到Gephi
	 */
	public void setExporttogehpi(boolean exporttogehpi) {
		this.exporttogehpi = exporttogehpi;
	}
	/*
	 * 设置是否要每天导入交易数据，设置为false的则在每天导入数据的时候跳过
	 */
	public boolean isImportdailytradingdata() {
		return importdailytradingdata;
	}
	/*
	 * 设置是否导入每日交易数据
	 */
	public void setImportdailytradingdata(boolean importdailytradingdata) {
		this.importdailytradingdata = importdailytradingdata;
	}
	/*
	 * 
	 */
	public boolean isShowinbkfxgui() {
		return showinbkfxgui;
	}
	/*
	 * 设置是否显示在板块分析窗口
	 */
	public void setShowinbkfxgui(boolean showinbkfxgui) {
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
				this.stockofbklist = new ArrayList<StockOfBanKuai> ();
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
//		boolean hasalreadybeenbankuaigegu = false;
		
//		if (this.getChildCount() >= 0) {
//            for (Enumeration e= this.children(); e.hasMoreElements(); ) {
//            	BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)e.nextElement();
//            	if(childnode.getType() == BkChanYeLianTreeNode.BKGEGU && childnode.getMyOwnCode().equals(stock.getMyOwnCode()))
//                	hasalreadybeenbankuaigegu = true;
//                
//            }
//        }
//		if(!hasalreadybeenbankuaigegu)
//			this.add(stock);
		
		if( this.stockofbklist.indexOf(stockofbk) < 0 )
			this.stockofbklist.add(stockofbk);
			
	}
	 /**
	 * @return the tmpallbkge
	 * 
	 */
	public ArrayList<StockOfBanKuai> getAllGeGuOfBanKuaiInHistory() 
	{
//		ArrayList<StockOfBanKuai> stocklist = new ArrayList<StockOfBanKuai> ();
//		int childcount = this.getChildCount();
//		if (this.getChildCount() >= 0) {
//            for (Enumeration e= this.children(); e.hasMoreElements(); ) {
//            	BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)e.nextElement();
//            	if(childnode.getType() == BkChanYeLianTreeNode.BKGEGU) 
//                    stocklist.add((StockOfBanKuai)childnode);
//                }
//        }
//		return stocklist;
		
		return this.stockofbklist;
    }
	/*
	 * 
	 */
	public Set<StockOfBanKuai> getSpecificPeriodBanKuaiGeGu(LocalDate requireddate,int difference,String period) 
	{
		HashSet<StockOfBanKuai> result = new HashSet<StockOfBanKuai> ();

		for(StockOfBanKuai stockofbk : this.stockofbklist) {
		
			if(stockofbk.isInBanKuaiAtSpecificDate(requireddate)) 
					result.add(stockofbk);
			
			
		}
		
		return result;
	}
	/*
	 * 
	 */
	public StockOfBanKuai getBanKuaiGeGu (String stockcode)
	{
//		if (this.getChildCount() >= 0) {
//            for (Enumeration e= this.children(); e.hasMoreElements(); ) {
//            	BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)e.nextElement();
//            	if(childnode.getType() == BkChanYeLianTreeNode.BKGEGU && childnode.getMyOwnCode().equals(stockcode))
//                	return (StockOfBanKuai)childnode;
//            }
//        }
//		
//		return null;
		
		for(StockOfBanKuai stockofbk : this.stockofbklist) {
			if(stockofbk.getMyOwnCode().toUpperCase().equals(stockcode.toUpperCase())) {
				return stockofbk;
			}
		}
		
		return null;
	}
	
	
	/**
	 * @return the sysBanKuaiWeight
	 */
	public Integer getGeGuSuoShuBanKuaiWeight(String stockcode) 
	{
//		int childcount = this.getChildCount();
//		if (this.getChildCount() >= 0) {
//            for (Enumeration e= this.children(); e.hasMoreElements(); ) {
//            	BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)e.nextElement();
//                
//                if( childnode.getMyOwnCode().equals(stockcode)) {
//    				Integer quanzhong = ((StockOfBanKuai)childnode).getQuanZhong();
//    				return quanzhong;
//                }
//            }
//        }
//		return null;
		
		for(StockOfBanKuai stockofbk : this.stockofbklist) {
			if(stockofbk.getMyOwnCode().toUpperCase().equals(stockcode.toUpperCase())) {
				return stockofbk.getQuanZhong();
			}
		}
		
		return null;
	}
	/*
	 * 
	 */
	public void setGeGuSuoShuBanKuaiWeight(String stockcode , Integer quanzhong) 
	{
//		int childcount = this.getChildCount();
//		if (this.getChildCount() >= 0) {
//            for (Enumeration e= this.children(); e.hasMoreElements(); ) {
//            	BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)e.nextElement();
//                
//                if(childnode.getMyOwnCode().equals(stockcode)) {
//                	 ((StockOfBanKuai)childnode).setStockQuanZhong(quanzhong);
//                }
//            }
//        }
		
		for(StockOfBanKuai stockofbk : this.stockofbklist) {
			if(stockofbk.getMyOwnCode().toUpperCase().equals(stockcode.toUpperCase())) {
				stockofbk.setStockQuanZhong(quanzhong);
			}
		}
	}
	/*
	 * 
	 */
//	public NodeXPeriodDataBasic getStockXPeriodDataForABanKuai (String stockcode,String period)
//	{
//		int childcount = this.getChildCount();
//		if (this.getChildCount() >= 0) {
//            for (Enumeration e= this.children(); e.hasMoreElements(); ) {
//            	BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)e.nextElement();
//                
//                if(childnode.getMyOwnCode().equals(stockcode)) {
//                	NodeXPeriodDataBasic perioddata = ((TDXNodes)childnode).getNodeXPeroidData(period);
//            		return perioddata;
//                }
//            }
//        }
//		return null;
//	}
	
	 
	

}
