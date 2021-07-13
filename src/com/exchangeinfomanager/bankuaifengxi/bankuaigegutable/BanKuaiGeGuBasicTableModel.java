package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BandKuaiAndGeGuTableBasicModel;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.HanYuPinYing;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.google.common.base.Strings;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.SortByKeyWords.NodeChenJiaoErComparator;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.SortByKeyWords.NodeLiuTongShiZhiComparator;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.SortByKeyWords.NodeTimeRangeZhangFuComparator;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.SortByKeyWords.NodeZongShiZhiComparator;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditions;

public abstract class BanKuaiGeGuBasicTableModel extends BandKuaiAndGeGuTableBasicModel
{
	public BanKuaiGeGuBasicTableModel (Properties prop)
	{
		super (prop);
	}

	private static Logger logger = Logger.getLogger(BanKuaiGeGuBasicTableModel.class);
	public static String SORTBYCHENJIAOER = "Sort Node By ChenJiaoEr";

	private Collection<Tag> tags;

	private BanKuai intersectionbankuai;

//	private BanKuaiAndGeGuMatchingConditions matchcond;

	public void refresh (BanKuai bankuai,LocalDate wknum,String period)
	{
		int i=0;
		super.curbk = bankuai;
		super.showwknum = wknum;
		super.curperiod = period;

		entryList = null;
		entryList = new ArrayList<>( bankuai.getSpecificPeriodStockOfBanKuai(wknum,0) );
		
		 Iterator itr = entryList.iterator(); 
	     while (itr.hasNext())      { 
	    	 StockOfBanKuai sob = (StockOfBanKuai) itr.next();
    	 
	    	 if( !sob.isInBanKuaiAtSpecificDate(showwknum)  ) { //确认当前还在板块内，不在要剔除
	    		 itr.remove();
	    	 } else { //把停牌的个股的剔除，以免出问题
		    	 Stock stock = sob.getStock();
				 NodeXPeriodData stockxdata = stock.getNodeXPeroidData(period);
		    	 
		    	 Double cje = stockxdata.getChengJiaoEr(showwknum);
		    	 if(cje == null)
		    		 itr.remove();
	    	 }
	     } 
		
    	this.fireTableDataChanged();
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
			Object value = super.getValueAt(rowIndex, columnIndex);
    	
	    	return value;
	}
	
	public Class<?> getColumnClass(int columnIndex) 
	{ 
			Class clazz = super.getColumnClass(columnIndex);
			return clazz;
	}
	public void sortTableByKeywords (String kw, Boolean triggereventbyrequest)
	{
		switch(kw) {
		case "LiuTongShiZhi": sortTableByLiuTongShiZhi ();
			break;
		case "ZongShiZhi": sortTableByZongShiZhi ();
			break;
		case "ChenJiaoEr":
			sortTableByChenJiaoEr ();
			break;
		case "TimeRangeZhangFu": sortTableByTimeRangeZhangFu ( super.timerangezhangfu[0] ,  super.timerangezhangfu[1],  NodeGivenPeriodDataItem.DAY );
			break;
		case "QuanZhongInBanKuai":
			break;
		case "CjeZbGrowRate":
			break;
		case "ChengJiaoErZhanBi":
			break;
		case "CjeZbDpMaxWk":
			break;
		case "ChenJiaoLiang":
			break;
		case "ZhangDieFu":
			break;
		case "AverageChenJiaoEr":
			break;
		case "HuanShouLv":
			break;
		case "HuanShouLvFree":
			break;
		}
		
		if(triggereventbyrequest) {
			PropertyChangeEvent evt = new PropertyChangeEvent(this, SORTBYCHENJIAOER, this,  kw );
	        pcs.firePropertyChange(evt);
		}
	}
//	private Boolean sortsource = false;
//	public void setSortTriggerSource () {
//		sortsource = true;
//	}
	/*
	 * 
	 */
	public void sortTableByLiuTongShiZhi ()
	{
		try{ 	Collections.sort(entryList, new NodeLiuTongShiZhiComparator(showwknum,0,super.curperiod) );
				this.fireTableDataChanged();
		} catch (java.lang.NullPointerException e) {e.printStackTrace();}
	}
	public void sortTableByZongShiZhi ()  
	{
		try{ 	Collections.sort(entryList, new NodeZongShiZhiComparator(showwknum,0,super.curperiod) );
				this.fireTableDataChanged();
		} catch (java.lang.NullPointerException e) {e.printStackTrace();}
	}
	public void sortTableByChenJiaoEr ()
	{
		try{ 	Collections.sort(entryList, new NodeChenJiaoErComparator(showwknum,curperiod) );
				this.fireTableDataChanged();
		} catch (java.lang.NullPointerException e) {logger.debug("该表内容为空，表排序出错");
		} catch (java.lang.Exception ex) {ex.printStackTrace();}
	}
	public void sortTableByTimeRangeZhangFu (LocalDate start, LocalDate end, String specificperiod)
	{
		if(start == null && end == null)
			return;
		
		try{	Collections.sort(entryList, new NodeTimeRangeZhangFuComparator(start,end,specificperiod) );
				this.fireTableDataChanged();
		} catch (java.lang.NullPointerException e) {		logger.debug("该表没有数据，表排序出错");		}
	}
	
	/*
	 * 
	 */
	public TDXNodes getCurDispalyBandKuai ()
	{
		return this.curbk;
	}
	/*
	 * intersectionbk是用来显示和当前板块有交集的个股
	 */
	public BanKuai getInterSetctionBanKuai ()
	{
		return this.intersectionbankuai;
	}
	public void setInterSectionBanKuai (BanKuai bk)
	{
		this.intersectionbankuai = bk;
	}
	    public String getStockWeight (int row) 
	    {
	    	return (String)this.getValueAt(row,2);
	    } 
	    public void setCurrentHighlightKeyWords (Collection<Tag> keywords)
		{
			this.tags = keywords;
		}
		public Collection<Tag> getCurrentHighlightKeyWords ()
		{
			return this.tags ;
		}
}


