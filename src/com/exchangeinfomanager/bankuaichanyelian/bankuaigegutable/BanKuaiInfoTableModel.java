package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.table.DefaultTableModel;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.bankuaichanyelian.HanYuPinYing;

public class BanKuaiInfoTableModel extends DefaultTableModel 
{

	public BanKuaiInfoTableModel() {
		// TODO Auto-generated constructor stub
	}
	
	String[] jtableTitleStrings = { "板块代码", "板块名称","占比增长率","max","成交额增长贡献率"};
//	HashMap<String,BanKuai> bkmap;
	List<Map.Entry<String, BanKuai>> entryList;
	LocalDate showzhbiwknum;
	
	public void refresh  (HashMap<String,BanKuai> curbkzslist, LocalDate curselectdate)
	{
		showzhbiwknum = curselectdate;
		entryList = new ArrayList<Map.Entry<String, BanKuai>>(curbkzslist.entrySet());

        Collections.sort(entryList, new Comparator<Map.Entry<String, BanKuai>>() {
            @Override
            public int compare(Map.Entry<String, BanKuai> bk1,
                               Map.Entry<String, BanKuai> bk2) {
            	
            	Double bk1zhanbi = bk1.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showzhbiwknum);
            	Double bk2zhanbi = bk2.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showzhbiwknum);
            	try {
            		return bk1zhanbi.compareTo(bk2zhanbi);
            	} catch (java.lang.NullPointerException e) {
            		if(bk2zhanbi == null && bk1zhanbi != null)
            			return bk1zhanbi.compareTo(-10000000.0);
            		else if(bk1zhanbi == null && bk2zhanbi != null)
            			return bk2zhanbi.compareTo(-10000000.0);
            		else 
            			return 0;
            	}
            }
            }
        );

        this.fireTableDataChanged();
	}
	
	 
	
	 public int getRowCount() 
	 {
//		 if(this.bkmap == null)
//			 return 0;
//		 else 
//			 return this.bkmap.size();
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
//	    	if(bkmap.isEmpty())
//	    		return null;
//	    	
//	    	String[] bkcodeArray = bkmap.keySet().toArray(new String[bkmap.keySet().size()]);
//	    	String bkcode = bkcodeArray[rowIndex];
//	    	BanKuai thisbk = bkmap.get(bkcode);
//	    	String thisbkname = thisbk.getMyOwnName(); 
//	    	Double zhanbigrowthrate = thisbk.getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod ();
//	    	NumberFormat percentFormat = NumberFormat.getPercentInstance();
//	    	
//	    	Object value = "??";
//	    	switch (columnIndex) {
//            case 0:
//                value = bkcode;
//                break;
//            case 1: 
//            	value = thisbkname;
//            	break;
//            case 2:
//            	value = percentFormat.format(zhanbigrowthrate);
//            	break;
//	    	}
	    	
	    	if(entryList.isEmpty() )
	    		return null;
	    	
	    	Entry<String, BanKuai> thisbk = entryList.get( entryList.size()-1-rowIndex );
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
            	String bkcode = thisbk.getValue().getMyOwnCode();
                value = bkcode;
                break;
            case 1:
            	String thisbkname = thisbk.getValue().getMyOwnName();
            	value = thisbkname;
            	break;
            case 2:
            	Double zhanbigrowthrate = thisbk.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showzhbiwknum);
    	    	NumberFormat percentFormat = NumberFormat.getPercentInstance();
    	    	percentFormat.setMinimumFractionDigits(1);
            	value = percentFormat.format(zhanbigrowthrate);
            	break;
            case 3:
            	int maxweek = thisbk.getValue().getChenJiaoLiangZhanBiMaxWeekForAGivenPeriod (showzhbiwknum);
            	value = maxweek;
            	break;
            case 4:
            	Double cjegrowthrate = thisbk.getValue().getChenJiaoErChangeGrowthRateForAGivenPeriod (showzhbiwknum);
    	    	NumberFormat percentFormat2 = NumberFormat.getPercentInstance();
    	    	percentFormat2.setMinimumFractionDigits(1);
            	value = percentFormat2.format(cjegrowthrate);
	    	}

	    	return value;
	  }

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
			          clazz = String.class;
			          break;
		        case 3:
			          clazz = Integer.class;
			          break;
		        case 4:
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
	    public String getBanKuaiCode (int row) 
	    {
	    	return (String)this.getValueAt(row,0);
	    }
	    public String getBanKuaiName (int row) 
	    {
	    	return (String)this.getValueAt(row,1);
	    } 
	    public BanKuai getBanKuai (int row)
	    {
	    	String bkcode = this.getBanKuaiCode(entryList.size()-1-row);
//	    	return bkmap.get(bkcode);
	    	return this.entryList.get(entryList.size()-1-row).getValue();
	    }

	    public int getBanKuaiRowIndex (String neededfindstring) 
	    {
	    		int index = -1;
	    		HanYuPinYing hypy = new HanYuPinYing ();
	    		
	    		for(int i=0;i<this.getRowCount();i++) {
	    			String bkcode = (String)this.getValueAt(i, 0);
	    			String bkname = (String)this.getValueAt(i,1); 
	    			if(bkcode.trim().equals(neededfindstring) ) {
	    				index = i;
	    				break;
	    			}

	    			String namehypy = hypy.getBanKuaiNameOfPinYin(bkname );
			   		if(namehypy.toLowerCase().equals(neededfindstring.trim().toLowerCase())) {
			   			index = i;
			   			break;
			   		}
	    		}
	    	
	   		
	   		return index;
	    }


}
