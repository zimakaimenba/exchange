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

import org.apache.log4j.Logger;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.bankuaichanyelian.HanYuPinYing;

public class BanKuaiInfoTableModel extends DefaultTableModel 
{

	public BanKuaiInfoTableModel() {
		super ();
	}
	
	String[] jtableTitleStrings = { "������", "�������","ռ��������","MaxWeek","�ɽ�������������"};
//	HashMap<String,BanKuai> bkmap;
	List<Map.Entry<String, BanKuai>> entryList;
	LocalDate showzhbiwknum;
	private static Logger logger = Logger.getLogger(BanKuaiInfoTableModel.class);
	
	public void refresh  (HashMap<String,BanKuai> curbkzslist, LocalDate curselectdate)
	{
		showzhbiwknum = curselectdate;
		entryList = new ArrayList<Map.Entry<String, BanKuai>>(curbkzslist.entrySet());

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
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(entryList.isEmpty() )
	    		return null;
	    	
	    	Entry<String, BanKuai> thisbk = entryList.get( rowIndex );
	    	
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
//    	    	NumberFormat percentFormat = NumberFormat.getPercentInstance();
//    	    	percentFormat.setMinimumFractionDigits(1);
            	value = zhanbigrowthrate;
            	break;
            case 3:
            	int maxweek = thisbk.getValue().getChenJiaoLiangZhanBiMaxWeekForAGivenPeriod (showzhbiwknum);
            	value = maxweek;
            	break;
            case 4:
            	Double cjegrowthrate = thisbk.getValue().getChenJiaoErChangeGrowthRateForAGivenPeriod (showzhbiwknum);
//    	    	NumberFormat percentFormat2 = NumberFormat.getPercentInstance();
//    	    	percentFormat2.setMinimumFractionDigits(1);
            	value = cjegrowthrate;
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
			          clazz = Double.class;
			          break;
		        case 3:
			          clazz = Integer.class;
			          break;
		        case 4:
			          clazz = Double.class;
			          break;
		      }
		      
		      return clazz;
	 }
	    
	    public String getColumnName(int column) { 
	    	return jtableTitleStrings[column];
	    }//���ñ������ 
		

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
	    	String bkcode = this.getBanKuaiCode(row);
//	    	return bkmap.get(bkcode);
	    	return this.entryList.get(row).getValue();
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
