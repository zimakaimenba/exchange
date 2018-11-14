package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.HanYuPinYing;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuai.BanKuaiNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi.ExportCondition;

public class BanKuaiInfoTableModel extends DefaultTableModel 
{

	public BanKuaiInfoTableModel() {
		super ();
	}
	
	String[] jtableTitleStrings = { "板块代码", "板块名称","占比增长率","占比MaxWk","成交额增长贡献率","CjeMaxWk","成交量排名"};
	List<BanKuai> entryList;
	LocalDate showzhbiwknum;
	private String curperiod;
	private int difference;
//	private ArrayList<ExportCondition> initialzedcon;
	
	private static Logger logger = Logger.getLogger(BanKuaiInfoTableModel.class);
	
	public void refresh  (LocalDate curselectdate,int difference2, String period, ArrayList<ExportCondition> initializeconditon1)
	{
		this.showzhbiwknum = curselectdate;
		this.difference = difference2;
		this.curperiod = period;
//		this.initialzedcon = initializeconditon1;
		try{
			if(entryList != null) //按成交额排序
				Collections.sort(entryList, new BanKuaiChenJiaoErComparator(showzhbiwknum,difference,curperiod) );
		} catch (java.lang.IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		this.fireTableDataChanged();
	}
	public LocalDate getCurDisplayedDate ()
	{
		return this.showzhbiwknum;
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
	    	
	    	BanKuai bankuai = null;
	    	try {
	    		bankuai = entryList.get( rowIndex );
	    	} catch (java.lang.IndexOutOfBoundsException e) {
	    		e.printStackTrace();
	    		return null;
	    	}
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
            	Double zhanbigrowthrate = bkxdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(showzhbiwknum,0);// fxjg.getGgBkCjeZhanbiGrowthRate();
            	value = zhanbigrowthrate;
            	
            	zhanbigrowthrate = null;
            	bkxdata = null;
            	bankuai = null;
            			
            	break;
            case 3:
            	Integer maxweek = bkxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(showzhbiwknum,0);// fxjg.getGgBkCjeZhanbiMaxweek();
            	value = maxweek;
            	
            	maxweek = null;
            	bkxdata = null;
            	bankuai = null;
            	
            	break;
            case 4:
            	Double cjegrowthrate = bkxdata.getChenJiaoErChangeGrowthRateOfSuperBanKuai(showzhbiwknum,0);// fxjg.getGgBkCjeGrowthRateToSuperBanKuaiCjeGrowth();
            	value = cjegrowthrate;
            	
            	cjegrowthrate = null;
            	bkxdata = null;
            	bankuai = null;
            	
            	break;
            case 5:
            	Integer cjemaxwk = bkxdata.getChenJiaoErMaxWeekOfSuperBanKuai(showzhbiwknum,0);// fxjg.getGgBkCjeGrowthRateToSuperBanKuaiCjeGrowth();
            	value = cjemaxwk;
            	
            	cjemaxwk = null;
            	bkxdata = null;
            	bankuai = null;
            	
            	break;
            case 6: 
            	if(bankuai.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) || bankuai.getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) {
            		Integer chengjiaoerpaiming = this.entryList.indexOf(bankuai) + 1;
            		value = chengjiaoerpaiming;
            	} else 
            		value = 5000;
            	
            	break;
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
		        case 5:
			          clazz = Integer.class;
			          break;
		        case 6:
		        	clazz = Integer.class;
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
	    	hypy = null;
	   		return index;
	    }

		public String[] getTableHeader() 
		{
			return this.jtableTitleStrings;
		}

}


/*
 * 
 */
class BanKuaiChenJiaoErComparator implements Comparator<BanKuai> {
	private String period;
	private LocalDate compareDate;
	private int difference;
	public BanKuaiChenJiaoErComparator (LocalDate compareDate, int difference, String period )
	{
		this.period = period;
		this.compareDate = compareDate;
		this.difference = difference;
	}
    public int compare(BanKuai node1, BanKuai node2) {
   	
        Double cje1 = (node1.getNodeXPeroidData( period)).getChengJiaoEr(compareDate, difference) ;
        Double cje2 = (node2.getNodeXPeroidData( period)).getChengJiaoEr(compareDate, difference);
        
        try{
        	return cje2.compareTo(cje1);
        } catch (java.lang.NullPointerException e) {
        	return -1;
        }
    }
}