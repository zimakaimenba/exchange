package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.bankuaichanyelian.HanYuPinYing;
import com.exchangeinfomanager.bankuaifengxi.ChenJiaoZhanBiInGivenPeriod;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class BanKuaiGeGuTableModel extends DefaultTableModel 
{
	String[] jtableTitleStrings = { "股票代码", "股票名称","权重","占比增长率","MAX","成交额贡献"};
	BanKuai curbk;
	private ArrayList<Entry<String, Stock>> entryList;
	LocalDate showwknum;
	private int curdisplayrow = -1;
	private Stock curdisplaystock;
	
	
	BanKuaiGeGuTableModel ()
	{
	}

	public void refreshByParsedFile (BanKuai bankuai,LocalDate wknum)
	{
		curbk = bankuai;
		showwknum = wknum;
		 HashSet<String> stockcodeinparsefile = bankuai.getParseFileStockSet();
		 HashMap<String, Stock> stockmap = bankuai.getSpecificPeriodBanKuaiGeGu(wknum);
		 
		 if(stockmap == null) {
			 entryList = new ArrayList<Map.Entry<String, Stock>>();
		 } else if(stockcodeinparsefile != null  && stockcodeinparsefile.size() >0 ) { //优先把parsefile里的个股显示在前面
	        		entryList = new ArrayList<Map.Entry<String, Stock>>();
	     	 		for (Map.Entry<String,Stock> entry : stockmap.entrySet()) {  
	     	 			if(stockcodeinparsefile.contains(entry.getKey() ) ) {
	     	 				entryList.add(entry);
	     	 			} else 
	     	 				entryList.add(0,entry);
	     	 		} 
	        	
	     } else {
	    	 entryList = new ArrayList<Map.Entry<String, Stock>>();
	    	 for (Map.Entry<String,Stock> entry : stockmap.entrySet()) {
	    		 entryList.add(entry);
	    	 }
	     }
	     this.fireTableDataChanged();
		
	}
	public void refreshByZhanBiGrowthRate (BanKuai bankuai,LocalDate wknum)
	{
		curbk = bankuai;
		showwknum = wknum;
		HashMap<String, Stock> stockmap = bankuai.getSpecificPeriodBanKuaiGeGu(wknum);	
		try {
			entryList = new ArrayList<Map.Entry<String, Stock>>(stockmap.entrySet()  );
		} catch ( java.lang.NullPointerException e) {
			e.printStackTrace();
			System.out.println(curbk.getMyOwnCode()+curbk.getMyOwnName());
			return ;
		}
    	
    	Collections.sort(entryList, new Comparator<Map.Entry<String, Stock>>() {
            @Override
            public int compare(Map.Entry<String, Stock> integerEmployeeEntry,
                               Map.Entry<String, Stock> integerEmployeeEntry2) {
                return integerEmployeeEntry.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showwknum)
                        .compareTo(integerEmployeeEntry2.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showwknum));
            }
            }
        );
    	this.fireTableDataChanged();
	}


	public String getTdxBkCode ()
	{
		return this.curbk.getMyOwnCode();
	}
	public int getStockCurWeight(int rowIndex) 
	{
		int weight = (Integer)this.getValueAt(rowIndex, 2);
		return weight;
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
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(entryList.isEmpty())
	    		return null;
	    	
	    	if( rowIndex != this.curdisplayrow) {
	    		this.curdisplayrow = rowIndex;
	    		
		    	Entry<String, Stock> thisbk = entryList.get(entryList.size()-1-rowIndex);
		    	curdisplaystock = thisbk.getValue();
	    	}

	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
            	String bkcode = curdisplaystock.getMyOwnCode();
                value = bkcode;
                break;
            case 1: 
            	String thisbkname = curdisplaystock.getMyOwnName();
            	value = thisbkname;
            	break;
            case 2: //权重
            	HashMap<String, Integer> stockweightmap = curdisplaystock.getGeGuSuoShuBanKuaiWeight();
            	Object[] stockweight;
            	try {
            		stockweight = stockweightmap.values().toArray();
            		value = (Integer)stockweight[0];
            	} catch (java.lang.NullPointerException e) {
            		value = 0;
            	}
            	
            	break;
            case 3: //{ "股票代码", "股票名称","权重","占比增长率","MAX","成交额贡献"};
            	Double zhanbigrowthrate = curdisplaystock.getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showwknum);
    	    	NumberFormat percentFormat = NumberFormat.getPercentInstance();
            	value = percentFormat.format(zhanbigrowthrate);
            	break;
            case 4: //{ "股票代码", "股票名称","权重","占比增长率","MAX","成交额贡献"};
            	int maxweek = curdisplaystock.getChenJiaoLiangZhanBiMaxWeekForAGivenPeriod (showwknum);
            	value = (Integer)maxweek;
            	break;
            case 5: //{ "股票代码", "股票名称","权重","占比增长率","MAX","成交额贡献"};
            	Double cjechangegrowthrate = curdisplaystock.getChenJiaoErChangeGrowthRateForAGivenPeriod (showwknum);
    	    	NumberFormat percentFormat2 = NumberFormat.getPercentInstance();
            	value = percentFormat2.format(cjechangegrowthrate);
            	break;	
	    	}

	    	return value;
	  }
	  /*
	   * 
	   */
	  public void setStockCurWeight(int row, int newweight) 
	  {
		  if(entryList.isEmpty())
	    		return ;
		  
		  Entry<String, Stock> thisstock = entryList.get(entryList.size()-1-row);
		  Stock stock = thisstock.getValue();
	      String bkcode = thisstock.getValue().getMyOwnCode();
	      String thisbkname = thisstock.getValue().getMyOwnName();

		  HashMap<String, Integer> stockweightmap = stock.getGeGuSuoShuBanKuaiWeight();
		  stockweightmap.put(this.curbk.getMyOwnCode(),newweight);
		  stock.setGeGuSuoShuBanKuaiWeight(stockweightmap);
		  
		  this.fireTableDataChanged();
	  }
	  /*
	   * (non-Javadoc)
	   * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	   */
      public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = String.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = Integer.class;
			          break;
		        case 3:
			          clazz = String.class;
			          break;
		        case 4:
			          clazz = Integer.class;
			          break;
		        case 5:
			          clazz = String.class;
			          break;
		      }
		      
		      return clazz;
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
	    public Stock getStock (int row)
	    {
	    	String stockcode = this.getStockCode(row);
	    	return this.entryList.get(entryList.size()-1-row).getValue();
	    }
	    public void deleteAllRows ()
	    {	
	    	if(this.entryList == null)
				 return ;
			 else 
				 entryList.clear();
	    	this.fireTableDataChanged();
	    	
	    }
	    public int getStockRowIndex (String neededfindstring) 
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

		public HashSet<String> getStockInParseFile() 
		{
			return curbk.getParseFileStockSet();
		}

	    
	    

}

