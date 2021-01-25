package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

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
import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BandKuaiAndGeGuTableBasicModel;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.HanYuPinYing;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;

import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.google.common.base.Strings;


public abstract class BanKuaiGeGuBasicTableModel extends BandKuaiAndGeGuTableBasicModel
{
	public BanKuaiGeGuBasicTableModel (Properties prop)
	{
		super (prop);
	}

	private static Logger logger = Logger.getLogger(BanKuaiGeGuBasicTableModel.class);

	private Collection<Tag> tags;

	private BanKuai intersectionbankuai;

	private BanKuaiGeGuMatchCondition matchcond;

	public void refresh (BanKuai bankuai,LocalDate wknum,String period)
	{
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
		    	 
		    	 Double cje = stockxdata.getChengJiaoEr(showwknum, 0);
		    	 if(cje == null)
		    		 itr.remove();
	    	 }
	     } 
		
		sortTableByChenJiaoEr ();
    	
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
	
	/*
	 * 
	 */
	public void sortTableByLiuTongShiZhi ()
	{
		try{
			Collections.sort(entryList, new NodeLiuTongShiZhiComparator(showwknum,0,super.curperiod) );
		} catch (java.lang.NullPointerException e) {
//			e.printStackTrace();
		}
		
		this.fireTableDataChanged();
	}
	public void sortTableByZongShiZhi ()
	{
		
	}
	/*
	 * 
	 */
	public void sortTableByChenJiaoEr ()
	{
		try{
			Collections.sort(entryList, new NodeChenJiaoErComparator(showwknum,0,curperiod) );
			this.fireTableDataChanged();
		} catch (java.lang.NullPointerException e) {logger.debug("该表内容为空，表排序出错");}
	}
	public void sortTableByTimeRangeZhangFu (LocalDate start, LocalDate end, String specificperiod)
	{
		try{
			Collections.sort(entryList, new NodeTimeRangeZhangFuComparator(start,end,specificperiod) );
			this.fireTableDataChanged();
		} catch (java.lang.NullPointerException e) {//			e.printStackTrace();
			logger.debug("该表没有数据，表排序出错");
		}
	}
	
	/*
	 * 
	 */
	public BanKuai getCurDispalyBandKuai ()
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
//	    public String getStockCode (int row) 
//	    {
//	    	return (String)this.getValueAt(row,0);
//	    }
//	    public String getStockName (int row) 
//	    {
//	    	return (String)this.getValueAt(row,1);
//	    } 
	    public String getStockWeight (int row) 
	    {
	    	return (String)this.getValueAt(row,2);
	    } 
//	    public StockOfBanKuai getStock (int row)
//	    {
//	    	return (StockOfBanKuai) this.entryList.get(row);
//	    }
//	    public List<BkChanYeLianTreeNode> getAllStocks ()
//	    {
//	    	return this.entryList;
//	    }
//	    public StockOfBanKuai getStock (String stockcode)
//	    {
//	    	int index = this.getStockRowIndex (stockcode);
//	    	StockOfBanKuai sob = this.getStock(index);
//	    	return sob;
//	    }
//	    public Integer getStockRowIndex (String neededfindstring) //可以查找code也可以查找name
//	    {
//	    		int index = -1;
//	    		HanYuPinYing hypy = new HanYuPinYing ();
//	    		
//	    		for(int i=0;i<this.getRowCount();i++) {
//	    			String stockcode = (String)this.getValueAt(i, 0);
//	    			String stockname = (String)this.getValueAt(i,1); 
//	    			if(stockcode.trim().equals(neededfindstring) ) {
//	    				index = i;
//	    				break;
//	    			}
//	    			
//	    			if(stockname == null)
//	    				continue;
//	    			
//	    			Boolean compresult = hypy.compareTwoStrings (stockname, neededfindstring.trim() );
//			   		if(compresult) {
//			   			index = i;
//			   			break;
//			   		}
//	    		}
//	   		
//	    		hypy = null;
//	    		return index;
//	    }
	    
	    public void setCurrentHighlightKeyWords (Collection<Tag> keywords)
		{
			this.tags = keywords;
		}
		public Collection<Tag> getCurrentHighlightKeyWords ()
		{
			return this.tags ;
		}
}
/*
 * 按流通市值对个股排序
 */
class NodeLiuTongShiZhiComparator implements Comparator<BkChanYeLianTreeNode> {
	private String period;
	private LocalDate compareDate;
	private int difference;
	public NodeLiuTongShiZhiComparator (LocalDate compareDate, int difference, String period )
	{
		this.period = period;
		this.compareDate = compareDate;
		this.difference = difference;
	}
    public int compare(BkChanYeLianTreeNode node1, BkChanYeLianTreeNode node2) {
    	Stock stock1 = ( (StockOfBanKuai)node1).getStock();
    	Stock stock2 = ( (StockOfBanKuai)node2).getStock();
    	
        Double cje1 = ((StockNodesXPeriodData)stock1.getNodeXPeroidData( period)).getSpecificTimeLiuTongShiZhi(compareDate, difference) ;
        Double cje2 = ((StockNodesXPeriodData)stock2.getNodeXPeroidData( period)).getSpecificTimeLiuTongShiZhi(compareDate, difference);
        
        return cje2.compareTo(cje1);
    }
}


