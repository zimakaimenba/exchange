package com.exchangeinfomanager.nodes;

import java.awt.Color;
import java.sql.SQLException;
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

import com.exchangeinfomanager.NodesServices.ServicesForNode;
import com.exchangeinfomanager.NodesServices.ServicesForNodeBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfDZHBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
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

	private BanKuaiOperationSetting bkoptsetting; 
	
	public BanKuai(String bkcode,String name ) 
	{
		super(bkcode,name);
		super.nodetype = BkChanYeLianTreeNode.TDXBK;
		
		super.nodewkdata = new BanKuaiXPeriodDataForJFC (bkcode,NodeGivenPeriodDataItem.WEEK) ;
		super.nodedaydata = new BanKuaiXPeriodDataForJFC (bkcode,NodeGivenPeriodDataItem.DAY) ;

		super.nodetreerelated = new BanKuaiTreeRelated (this);
		
		bkoptsetting = new BanKuaiOperationSetting ();
	}
	
	public BanKuai(String bkcode,String name,String vendor) 
	{
		super(bkcode,name);
		
		this.bkvendor = vendor;
		if(vendor.toUpperCase().equalsIgnoreCase("DZH"))
			super.nodetype = BkChanYeLianTreeNode.DZHBK;
		else
			super.nodetype = BkChanYeLianTreeNode.TDXBK;
		
		super.nodewkdata = new BanKuaiXPeriodDataForJFC (bkcode,NodeGivenPeriodDataItem.WEEK) ;
		super.nodedaydata = new BanKuaiXPeriodDataForJFC (bkcode,NodeGivenPeriodDataItem.DAY) ;

		super.nodetreerelated = new BanKuaiTreeRelated (this);
		
		bkoptsetting = new BanKuaiOperationSetting ();
	}
	
//	private static Logger logger = Logger.getLogger(BanKuai.class);

	private String bankuaileixing; // 通达信里面定义的板块有几种：1.有个股自身有成交量数据 2. 有个股自身无成交量数据 3.无个股自身有成交量数据
	
	private List<BkChanYeLianTreeNode> stockofbklist; //存放所有的个股
	
	private Set<String> socialfriendspos;
	private Set<String> socialfriendsneg;
	
	private Interval bkgegusearchtimerange;
	
	private String bkvendor; // dazhihui, tondaxin;
	
	public BanKuaiOperationSetting getBanKuaiOperationSetting ()	{
		return bkoptsetting;
	}
	public void setBanKuaiVendor (String vendor)
	{
		this.bkvendor = vendor;
	}
	public String getBanKuaiVendor ()
	{
		if(this.bkvendor == null) {
			if(this.getMyOwnCode().startsWith("99")) return "DZH";
			else return "TDX";
		} else	return this.bkvendor;
	}
	public void addSocialFriendsPostive (String friend)
	{
		if(socialfriendspos == null)	socialfriendspos = new HashSet<String> ();
		socialfriendspos.add(friend);
	}
	public Set<String> getSocialFriendsSetPostive ()
	{
		if(this.socialfriendspos == null)
			this.socialfriendspos = new HashSet<String> ();
		
		return this.socialfriendspos;
	}
	public void addSocialFriendsNegtive (String friend)
	{
		if(socialfriendsneg == null)	socialfriendsneg = new HashSet<String> ();
		socialfriendsneg.add(friend);
	}
	public Set<String> getSocialFriendsSetNegtive ()	{
		if(this.socialfriendsneg == null)	this.socialfriendsneg = new HashSet<String> ();
		return this.socialfriendsneg;
	}
//	public  Boolean isExportTowWlyFile ()	{
//		if(exporttowklyfile == null)	return true;
//		else	return this.exporttowklyfile;
//	}
//	public void setExportTowWlyFile (Boolean exporttofile)
//	{
//		if(exporttofile != null)	this.exporttowklyfile = exporttofile;
//	}
//	
//	public Boolean isShowincyltree () {
//		if(this.showincyltree != null)	return this.showincyltree;
//		else	return true;
//	}
//	public void setShowincyltree (Boolean showincyltree) {
//		if(showincyltree != null)	this.showincyltree = showincyltree;
//	}
//	public boolean isExporttogehpi() {
//		if(exporttogehpi != null)	return exporttogehpi;
//		else	return true;
//	}
//	/*
//	 * 设置是否导出数据到Gephi
//	 */
//	public void setExporttogehpi(Boolean exporttogehpi) {
//		if(exporttogehpi != null)	this.exporttogehpi = exporttogehpi;
//	}
//	/*
//	 * 设置是否要每天导入交易数据，设置为false的则在每天导入数据的时候跳过
//	 */
//	public boolean isImportdailytradingdata() {
//		if(importdailytradingdata != null)
//			return importdailytradingdata;
//		else
//			return true;
//	}
//	/*
//	 * 设置是否导入每日交易数据
//	 */
//	public void setImportdailytradingdata(Boolean importdailytradingdata) {
//		if(importdailytradingdata != null)
//			this.importdailytradingdata = importdailytradingdata;
//	}
//	/*
//	 * 
//	 */
//	public boolean isShowinbkfxgui() {
//		if(showinbkfxgui != null)
//			return showinbkfxgui;
//		else
//			return true;
//	}
//	/*
//	 * 设置是否显示在板块分析窗口
//	 */
//	public void setShowinbkfxgui(Boolean showinbkfxgui) {
//		if(showinbkfxgui != null)
//			this.showinbkfxgui = showinbkfxgui;
//	}
//	public void setImportBKGeGu(Boolean importgegu) 	{
//		if(importgegu != null)			this.importbkgegu = importgegu;
//		
//	}
//	public  Boolean isImportBKGeGu ()	{
//		if(importbkgegu == null)			return true;
//		else			return this.importbkgegu;
//	}
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
		if(bankuaileixing == null)		return BanKuai.HASGGWITHSELFCJL;
		else		return this.bankuaileixing;
	}
	/*
	 * 
	 */
	public void addNewBanKuaiGeGu (StockOfBanKuai stockofbk) 
	{
		if(this.stockofbklist == null) 	this.stockofbklist = new ArrayList<> ();

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
	public List<BkChanYeLianTreeNode> getAllGeGuOfBanKuaiInHistoryByJiaoYiSuo(String jiaoyisuo) 
	{
		if(stockofbklist == null)		return null;
		
		List<BkChanYeLianTreeNode> gegubyjys = new ArrayList<> ();
		for(BkChanYeLianTreeNode tmpgg : this.stockofbklist )
			if( ((StockOfBanKuai)tmpgg).getStock().getNodeJiBenMian().getSuoShuJiaoYiSuo().equalsIgnoreCase(jiaoyisuo)    )
				gegubyjys.add(tmpgg);
		
		return gegubyjys;
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
		if(this.stockofbklist == null)	return result;
		
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
		if(this.stockofbklist == null)		return null;
		
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
		if(this.stockofbklist == null)
			return null;
		
		for(BkChanYeLianTreeNode stockofbk : this.stockofbklist) {
			if(stockofbk.getMyOwnCode().toUpperCase().equals(stockcode.toUpperCase())) {
				return ((StockOfBanKuai)stockofbk);
			}
		}
		
		return null;
	}
	public Stock getBanKuaiGeGu (String stockcode)
	{
		if (this.stockofbklist == null)
			return null;
		
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
	
	public ServicesForNodeBanKuai getBanKuaiService (Boolean getorreset)
	{
		if(!getorreset) {
			svsofnodeofbk = null;
			return null;
		}
		
		if(svsofnodeofbk != null)
			return svsofnodeofbk;
		
		if(this.getType() == BkChanYeLianTreeNode.TDXBK)
			svsofnodeofbk = new SvsForNodeOfBanKuai ();
		else
			svsofnodeofbk = new SvsForNodeOfDZHBanKuai ();
		
		return svsofnodeofbk;
	}
	public ServicesForNode getServicesForNode (Boolean getorreset)
	{
		if(!getorreset) {
			svsofnode = null;
			return null;
		}
		
		if(svsofnode == null)
			return svsofnode = new SvsForNodeOfBanKuai ();
		else
			return this.svsofnode;
	}
	
	private SvsForNodeOfBanKuai svsofnode;
	private ServicesForNodeBanKuai svsofnodeofbk;
	
	public class BanKuaiOperationSetting 
	{
		private Boolean exporttogehpi = true;
		private Boolean importdailytradingdata = true;
		private Boolean showinbkfxgui = true;
		private Boolean showincyltree = true;
		private Boolean exporttowklyfile ;
		private Boolean importbkgegu;
		private String bankuailabelcolor;
		
		public void setBanKuaiLabelColor(Color color) {
			String colorcode = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue() );
			this.bankuailabelcolor = colorcode;
		}
		public void setBanKuaiLabelColor(String colorstr) {
			this.bankuailabelcolor = colorstr;
		}
		public Color getBanKuaiLabelColor () {
			if(this.bankuailabelcolor == null) return null; 
			else return Color.decode(this.bankuailabelcolor);
		}
		public String getBanKuaiLabelColorStr () {
			if(this.bankuailabelcolor == null) return null; 
			else return this.bankuailabelcolor;
		}
		
		public void setImportBKGeGu(Boolean importgegu) 	{
			if(importgegu != null)			this.importbkgegu = importgegu;
			
		}
		public  Boolean isImportBKGeGu ()	{
			if(importbkgegu == null)			return true;
			else			return this.importbkgegu;
		}
		public  Boolean isExportTowWlyFile ()	{
			if(exporttowklyfile == null)	return true;
			else	return this.exporttowklyfile;
		}
		public void setExportTowWlyFile (Boolean exporttofile)
		{
			if(exporttofile != null)	this.exporttowklyfile = exporttofile;
		}
		public Boolean isShowincyltree () {
			if(this.showincyltree != null)	return this.showincyltree;
			else	return true;
		}
		public void setShowincyltree (Boolean showincyltree) {
			if(showincyltree != null)	this.showincyltree = showincyltree;
		}
		public boolean isExporttogehpi() {
			if(exporttogehpi != null)	return exporttogehpi;
			else	return true;
		}
		/*
		 * 设置是否导出数据到Gephi
		 */
		public void setExporttogehpi(Boolean exporttogehpi) {
			if(exporttogehpi != null)	this.exporttogehpi = exporttogehpi;
		}
		/*
		 * 设置是否要每天导入交易数据，设置为false的则在每天导入数据的时候跳过
		 */
		public boolean isImportdailytradingdata() {
			if(importdailytradingdata != null)			return importdailytradingdata;
			else	return true;
		}
		/*
		 * 设置是否导入每日交易数据
		 */
		public void setImportdailytradingdata(Boolean importdailytradingdata) {
			if(importdailytradingdata != null)	this.importdailytradingdata = importdailytradingdata;
		}
		/*
		 * 
		 */
		public boolean isShowinbkfxgui() {
			if(showinbkfxgui != null)	return showinbkfxgui;
			else	return true;
		}
		/*
		 * 设置是否显示在板块分析窗口
		 */
		public void setShowinbkfxgui(Boolean showinbkfxgui) {
			if(showinbkfxgui != null) this.showinbkfxgui = showinbkfxgui;
		}
	}
}


