package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.HanYuPinYing;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;

public abstract class BanKuaiGeGuBasicTableModel extends DefaultTableModel
{
	public BanKuaiGeGuBasicTableModel ()
	{
		super ();
	}

	private static Logger logger = Logger.getLogger(BanKuaiGeGuTableModel.class);
	
	protected String[] jtableTitleStrings ;
	protected BanKuai curbk;
	protected ArrayList<TDXNodes> entryList;
	protected LocalDate showwknum;
	protected String period;
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
		entryList = new ArrayList<TDXNodes>( bankuai.getSpecificPeriodBanKuaiGeGu(wknum,0,period) );
		
		 Iterator itr = entryList.iterator(); 
	     while (itr.hasNext())      { 
	    	 StockOfBanKuai sob = (StockOfBanKuai) itr.next();
//	    	 if(sob.getMyOwnCode().equals("603259"))
//	    		 logger.debug("test ?");
    	 
	    	 if( !sob.isInBanKuaiAtSpecificDate(showwknum)  ) { //ȷ�ϵ�ǰ���ڰ����
	    		 itr.remove();
	    	 } else { //��ͣ�Ƶĸ��ɵ��޳������������
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
	/*
	 * 
	 */
	public LocalDate getShowCurDate ()
	{
		return this.showwknum;
	}
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
	public void sortTableByChenJiaoEr ()
	{
		try{
			Collections.sort(entryList, new NodeChenJiaoErComparator(showwknum,0,period) );
			this.fireTableDataChanged();
		} catch (java.lang.NullPointerException e) {
			e.printStackTrace();
			logger.debug("��λ�գ����������");
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
		  String stockcode = stock.getMyOwnCode();
	      curbk.setGeGuSuoShuBanKuaiWeight(stockcode,newweight);
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
	    }//���ñ������ 
		

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
	    public List<TDXNodes> getAllStocks ()
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
	    	this.fireTableDataChanged();
	    }
	    
	    public int getStockRowIndex (String neededfindstring) //���Բ���codeҲ���Բ���name
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
	    			String namehypy = hypy.getBanKuaiNameOfPinYin(stockname );
			   		if(namehypy.toLowerCase().equals(neededfindstring.trim().toLowerCase())) {
			   			index = i;
			   			break;
			   		}
	    		}
	   		
	    		return index;
	    }

	    public void removeAllRows ()
	    {
	    	if(entryList != null) {
	    		this.entryList.clear();
		    	this.fireTableDataChanged();
	    	}
	    }
	    
				

}
/*
 * ����ͨ��ֵ�Ը�������
 */
class NodeLiuTongShiZhiComparator implements Comparator<TDXNodes> {
	private String period;
	private LocalDate compareDate;
	private int difference;
	public NodeLiuTongShiZhiComparator (LocalDate compareDate, int difference, String period )
	{
		this.period = period;
		this.compareDate = compareDate;
		this.difference = difference;
	}
    public int compare(TDXNodes node1, TDXNodes node2) {
    	Stock stock1 = ( (StockOfBanKuai)node1).getStock();
    	Stock stock2 = ( (StockOfBanKuai)node2).getStock();
    	
        Double cje1 = ((StockNodesXPeriodData)stock1.getNodeXPeroidData( period)).getSpecificTimeLiuTongShiZhi(compareDate, difference) ;
        Double cje2 = ((StockNodesXPeriodData)stock2.getNodeXPeroidData( period)).getSpecificTimeLiuTongShiZhi(compareDate, difference);
        
        return cje2.compareTo(cje1);
    }
}


