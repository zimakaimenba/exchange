package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.bankuaichanyelian.HanYuPinYing;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class BanKuaiGeGuTableModel extends DefaultTableModel 
{
	String[] jtableTitleStrings = { "股票代码", "股票名称","权重","占比增长率"};
//	HashMap<String,Stock> stockmap;
	String curbkcode;
	private ArrayList<Entry<String, Stock>> entryList;
	int showwknum;
	private HashSet<String> stockcodeinparsefile;
	private boolean youxianpaixuparsefile;
	
	
	BanKuaiGeGuTableModel ()
	{
	}

	public void refresh  (String bkcode, HashMap<String,Stock> stockmap1,int wknum, HashSet<String> stockinparsefile2)
	{
		this.curbkcode = bkcode;
		this.showwknum = wknum;
		this.stockcodeinparsefile = stockinparsefile2;
        
        if(youxianpaixuparsefile) {
        	entryList = new ArrayList<Map.Entry<String, Stock>>();
        	if(stockcodeinparsefile.size() >0 ) { //优先把parsefile里的个股显示在前面
    	 		
     	 		for (Map.Entry<String,Stock> entry : stockmap1.entrySet()) {  
     	 			if(stockcodeinparsefile.contains(entry.getKey() ) ) {
     	 				entryList.add(entry);
     	 			} else 
     	 				entryList.add(0,entry);
     	 		} 
     	 		
        	}
        	
        } else {
        	entryList = new ArrayList<Map.Entry<String, Stock>>(stockmap1.entrySet()  );
        	
        	Collections.sort(entryList, new Comparator<Map.Entry<String, Stock>>() {
                @Override
                public int compare(Map.Entry<String, Stock> integerEmployeeEntry,
                                   Map.Entry<String, Stock> integerEmployeeEntry2) {
                    return integerEmployeeEntry.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showwknum)
                            .compareTo(integerEmployeeEntry2.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showwknum));
                }
                }
            );
        }
        
//        if(stockcodeinparsefile.size() >0 ) { //优先把parsefile里的个股显示在前面
// 			Set<String> bkgegucodelist = tmpallbkge.keySet() ;
// 	 		SetView<String> commonbkcode = Sets.intersection(bkgegucodelist, this.stockcodeinparsefile);
// 	 		SetView<String> diffbkcode = Sets.difference(bkgegucodelist, this.stockcodeinparsefile);
// 	 		
// 	 		ArrayList<String> tmpbkgeguname = new ArrayList<String> (commonbkcode);
// 	 		tmpbkgeguname.addAll(diffbkcode);
// 	 		
// 	 		bkgeguname = tmpbkgeguname;
// 		} else
// 			bkgeguname = new ArrayList<String> (tmpallbkge.keySet() );
        
		this.fireTableDataChanged();
	}
	public void setYouXianPaiXuParsedFile (boolean youxian)
	{
		this.youxianpaixuparsefile = youxian;
	}

	public String getTdxBkCode ()
	{
		return this.curbkcode;
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
	    	Entry<String, Stock> thisbk = entryList.get(entryList.size()-1-rowIndex);
	    	Stock stock = thisbk.getValue();
	    	String bkcode = thisbk.getValue().getMyOwnCode();
	    	String thisbkname = thisbk.getValue().getMyOwnName(); 
	    	
	    	HashMap<String, Integer> stockweightmap = stock.getGeGuSuoShuBanKuaiWeight();
	    	Object[] stockweight = stockweightmap.values().toArray();
	    	
	    	Double zhanbigrowthrate = thisbk.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showwknum);
	    	NumberFormat percentFormat = NumberFormat.getPercentInstance();
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = bkcode;
                break;
            case 1: 
            	value = thisbkname;
            	break;
            case 2: 
            	value = (Integer)stockweight[0];
            	break;
            case 3:
            	value = percentFormat.format(zhanbigrowthrate);
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
		  stockweightmap.put(this.curbkcode,newweight);
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
	    public HashSet<String> getStockInParseFile ()
		{
			return this.stockcodeinparsefile;
		}

	    public void removeAllRows ()
	    {
	    	if(entryList != null) {
	    		this.entryList.clear();
		    	this.fireTableDataChanged();
	    	}
	    	
	    }

	    
	    

}

