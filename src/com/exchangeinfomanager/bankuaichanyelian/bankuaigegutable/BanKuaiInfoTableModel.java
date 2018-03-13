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
import com.exchangeinfomanager.asinglestockinfo.BanKuai.BanKuaiNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.ChenJiaoZhanBiInGivenPeriod;
import com.exchangeinfomanager.bankuaichanyelian.HanYuPinYing;

public class BanKuaiInfoTableModel extends DefaultTableModel 
{

	public BanKuaiInfoTableModel() {
		super ();
	}
	
	String[] jtableTitleStrings = { "板块代码", "板块名称","占比增长率","MaxWeek","成交额增长贡献率"};
//	HashMap<String,BanKuai> bkmap;
	List<BanKuai> entryList;
	LocalDate showzhbiwknum;
	private String curperiod;
	private static Logger logger = Logger.getLogger(BanKuaiInfoTableModel.class);
	
	public void refresh  (ArrayList<BanKuai> bkhascjl, LocalDate curselectdate,String period)
	{
		showzhbiwknum = curselectdate;
		this.curperiod = period;
//		entryList = bkhascjl;

		this.fireTableDataChanged();
	}
	public void addBanKuai (BanKuai bankuai)
	{
		if(entryList == null)
			entryList = new ArrayList<BanKuai> ();
		else
			entryList.add(bankuai);
	}
	
	 public int getRowCount() 
	 {
		 if(entryList == null)
			 return 0;
		 else
			 return entryList.size();
	 }
	 public void deleteAllRows ()
	 {
		 if(this.entryList != null && this.entryList.size() >0)
			 this.entryList.clear();
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
    	
	    	BanKuai bankuai = entryList.get( rowIndex );
	    	NodeXPeriodDataBasic bkxdata = (BanKuaiNodeXPeriodData)bankuai.getNodeXPeroidData(this.curperiod);
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
            	String bkcode = bankuai.getMyOwnCode();
                value = bkcode;
                break;
            case 1:
            	String thisbkname = bankuai.getMyOwnName();
            	value = thisbkname;
            	break;
            case 2: //"板块代码", "板块名称","占比增长率","MaxWeek","成交额增长贡献率"
            	Double zhanbigrowthrate = bkxdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(showzhbiwknum);// fxjg.getGgBkCjeZhanbiGrowthRate();
            	value = zhanbigrowthrate;
            	break;
            case 3:
            	int maxweek = bkxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(showzhbiwknum);// fxjg.getGgBkCjeZhanbiMaxweek();
            	value = maxweek;
            	break;
            case 4:
            	Double cjegrowthrate = bkxdata.getChenJiaoErChangeGrowthRateOfSuperBanKuai(showzhbiwknum);// fxjg.getGgBkCjeGrowthRateToSuperBanKuaiCjeGrowth();
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
	    	return this.entryList.get(row);
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



		public String[] getTableHeader() 
		{
			return this.jtableTitleStrings;
		}


}
