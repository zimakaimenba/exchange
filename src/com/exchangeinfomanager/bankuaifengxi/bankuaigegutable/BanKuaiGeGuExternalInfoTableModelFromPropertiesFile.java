package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

import java.util.Properties;
import org.apache.log4j.Logger;

import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;

public class BanKuaiGeGuExternalInfoTableModelFromPropertiesFile extends BanKuaiGeGuBasicTableModel
{
//	private Properties prop;


	public BanKuaiGeGuExternalInfoTableModelFromPropertiesFile (Properties prop)
	{
		super (prop);
	}

	private static Logger logger = Logger.getLogger(BanKuaiGeGuExternalInfoTableModelFromPropertiesFile.class);

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

}
