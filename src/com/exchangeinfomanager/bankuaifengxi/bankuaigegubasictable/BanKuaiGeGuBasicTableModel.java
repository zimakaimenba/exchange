package com.exchangeinfomanager.bankuaifengxi.bankuaigegubasictable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.NodeChenJiaoErComparator;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.NodeTimeRangeZhangFuComparator;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.HanYuPinYing;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;

import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;


public abstract class BanKuaiGeGuBasicTableModel extends DefaultTableModel
{
	public BanKuaiGeGuBasicTableModel ()
	{
		super ();
	}

	private static Logger logger = Logger.getLogger(BanKuaiGeGuBasicTableModel.class);
	
	protected String[] jtableTitleStrings ;
	protected BanKuai curbk;
	protected List<BkChanYeLianTreeNode> entryList;
	protected LocalDate showwknum;
	protected String period;

	private Collection<Tag> tags;

	private BanKuai intersectionbankuai;

	private BanKuaiGeGuMatchCondition matchcond;
	/*
	 * 
	 */
	public void setTableHeader (String[] jtableTitleStrings1)
	{
		this.jtableTitleStrings = jtableTitleStrings1;
	}
	public String[] getTableHeader ()
	{
		return this.jtableTitleStrings;
	}
	public void refresh (BanKuai bankuai,LocalDate wknum,String period)
	{
		this.curbk = bankuai;
		this.showwknum = wknum;
		this.period = period;

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
	
	 protected Object getColomnValue(String column_keyword, int rowIndex) 
	  {
		  Object value = "??";
		  
		  StockOfBanKuai curdisplaystockofbankuai = (StockOfBanKuai) entryList.get(rowIndex);
//		  String bkcode = curbk.getMyOwnCode();
		  Stock stock = curdisplaystockofbankuai.getStock();
//		    DaPan dapan = (DaPan)stock.getRoot();
		  NodeXPeriodData stockxdata = stock.getNodeXPeroidData(period);
		    
		  switch (column_keyword) {
         case "nodecode":
       	  String stockcode = curdisplaystockofbankuai.getMyOwnCode();
       	  value = stockcode;
       	  break;
       	  
         case "nodename":
       	  String thisbkname = curdisplaystockofbankuai.getMyOwnName();
         	  value = thisbkname;
       	  break;
       	  
         case "bankuaichengjiaoergongxian":
       	  Double cjechangegrowthrate = stockxdata.getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage(this.curbk,showwknum,0);// fxrecord.getGgbkcjegrowthzhanbi();
         	  if(cjechangegrowthrate != null)
         		value = cjechangegrowthrate;
         	  else	value = -1;
       	  break;
       	  
         case "cjezbgrowrate":
       	  Double cjedpgrowthrate = stockxdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(showwknum,0);//.getGgdpzhanbigrowthrate();
         	  if(cjedpgrowthrate != null)
         		value = cjedpgrowthrate;
         	  else	value = -1;
       	  break;
       	  
         case "CjeZbDpMaxWk":
       	  Integer cjedpmaxwk = stockxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek();
	          try{
	          		if(cjedpmaxwk > 0) {
	              		value = cjedpmaxwk;
	              		break;
	              	} else	if(cjedpmaxwk == 0) {
	              		Integer cjedpminwk = stockxdata.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(showwknum, 0);
	              		value = 0 - cjedpminwk;
	              		break;
	              	}
	          } catch (java.lang.NullPointerException e) {
	          		e.printStackTrace();
	          }
	          break;
         case "NCjeZbDpMinWk":
       	  Integer cjldpgrowthrate = stockxdata.getChenJiaoErZhanBiMinestWeekOfSuperBanKuaiInSpecificPeriod(showwknum,0,15);//.getGgdpzhanbigrowthrate();
         	  value = cjldpgrowthrate;
         	  break;
         case "averagecjemaxwk" :
       	  Integer cjldpmaxwk = stockxdata.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek();
         	  if(cjldpmaxwk != null && cjldpmaxwk > 0) 
         		value = cjldpmaxwk;
          	  else value = 0;
         	  break;
         case "highlevelpanxurank" :
       	  Integer paiming = this.entryList.indexOf(curdisplaystockofbankuai) + 1;
         	  value = paiming;
       	  break;
         case "quanzhonginbankuai" :
       	  Integer stockweight =  curbk.getGeGuSuoShuBanKuaiWeight( curdisplaystockofbankuai.getMyOwnCode() );
	        	try {
	        		value = (Integer)stockweight;
	        	} catch (java.lang.NullPointerException e) {
	        		value = 0;
	        	}
       	  break;
         case "CjeLianXuZhang": //周日平均成交额MAXWK
	        	Integer cjemaxwk = stockxdata.getAverageDailyCjeLianXuFangLiangPeriodNumber(showwknum,0);//.getGgbkcjemaxweek(); 
	        	value = cjemaxwk;
	        	break;
         case "huanshoulv": //周日平均成交额MAXWK
	        	Double hsl = ((StockNodesXPeriodData)stockxdata).getSpecificTimeHuanShouLv(showwknum, 0);
		    	value = hsl;
		    	break;
         	
		  }
		  
		  return value;
	  }
	
	/*
	 * 
	 */
	public LocalDate getShowCurDate ()
	{
		return this.showwknum;
	}
	/*
	 * 
	 */
	public String getCurDisplayPeriod ()
	{
		return this.period;
	}
	/*
	 * 
	 */
	public void sortTableByLiuTongShiZhi ()
	{
		try{
			Collections.sort(entryList, new NodeLiuTongShiZhiComparator(showwknum,0,period) );
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
			Collections.sort(entryList, new NodeChenJiaoErComparator(showwknum,0,period) );
			this.fireTableDataChanged();
		} catch (java.lang.NullPointerException e) {
//			e.printStackTrace();
			logger.debug("该表内容为空，表排序出错");
		}
		
	}
	public void sortTableByTimeRangeZhangFu (LocalDate start, LocalDate end, String specificperiod)
	{
		try{
			Collections.sort(entryList, new NodeTimeRangeZhangFuComparator(start,end,specificperiod) );
			this.fireTableDataChanged();
		} catch (java.lang.NullPointerException e) {
//			e.printStackTrace();
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
	/*
	 * 
	 */
	public int getStockCurWeight(int rowIndex) 
	{
		int weight = (Integer)this.getValueAt(rowIndex, 2);
		return weight;
	}
	/*
	   * 
	   */
	  public void setStockCurWeight(int row, int newweight) 
	  {
		  if(entryList.isEmpty())
	    		return ;
		  
		  StockOfBanKuai stock = (StockOfBanKuai) entryList.get(row);
		  ((StockOfBanKuai)stock).setStockQuanZhong(newweight);
//		  String stockcode = stock.getMyOwnCode();
//	      curbk.setGeGuSuoShuBanKuaiWeight(stockcode,newweight);
		  this.fireTableDataChanged();
	  }
	 public int getRowCount() 
	 {
		 if(entryList == null)
			 return 0;
		 else 
			 return entryList.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public String getColumnName(int column) { 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    public String getStockCode (int row) 
	    {
	    	return (String)this.getValueAt(row,0);
	    }
	    public String getStockName (int row) 
	    {
	    	return (String)this.getValueAt(row,1);
	    } 
	    public String getStockWeight (int row) 
	    {
	    	return (String)this.getValueAt(row,2);
	    } 
	    public StockOfBanKuai getStock (int row)
	    {
	    	return (StockOfBanKuai) this.entryList.get(row);
	    }
	    public List<BkChanYeLianTreeNode> getAllStocks ()
	    {
	    	return this.entryList;
	    }
	    public StockOfBanKuai getStock (String stockcode)
	    {
	    	int index = this.getStockRowIndex (stockcode);
	    	StockOfBanKuai sob = this.getStock(index);
	    	return sob;
	    }
	    public void deleteAllRows ()
	    {	
	    	if(this.entryList == null)
				 return ;
			 else 
				 entryList.clear();
	    	
	    	this.showwknum = null;
	    	this.period = null;
	    	this.curbk = null;
	    	
	    	this.fireTableDataChanged();
	    }
	    
	    public Integer getStockRowIndex (String neededfindstring) //可以查找code也可以查找name
	    {
	    		int index = -1;
	    		HanYuPinYing hypy = new HanYuPinYing ();
	    		
	    		for(int i=0;i<this.getRowCount();i++) {
	    			String stockcode = (String)this.getValueAt(i, 0);
	    			String stockname = (String)this.getValueAt(i,1); 
	    			if(stockcode.trim().equals(neededfindstring) ) {
	    				index = i;
	    				break;
	    			}
	    			
	    			if(stockname == null)
	    				continue;
	    			
	    			Boolean compresult = hypy.compareTwoStrings (stockname, neededfindstring.trim() );
			   		if(compresult) {
			   			index = i;
			   			break;
			   		}
	    		}
	   		
	    		hypy = null;
	    		return index;
	    }

	    public void removeAllRows ()
	    {
	    	if(entryList != null) {
	    		this.entryList.clear();
		    	this.fireTableDataChanged();
	    	}
	    }
	    
	    public void setCurrentHighlightKeyWords (Collection<Tag> keywords)
		{
			this.tags = keywords;
		}
		public Collection<Tag> getCurrentHighlightKeyWords ()
		{
			return this.tags ;
		}
		
		public void setDisplayMatchCondition(BanKuaiGeGuMatchCondition expc) 
		{
			this.matchcond = expc;
			
		}
		public BanKuaiGeGuMatchCondition getDisplayMatchCondition ()
		{
			return this.matchcond;
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


