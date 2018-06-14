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

import org.apache.log4j.Logger;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.HanYuPinYing;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class BanKuaiGeGuTableModel extends DefaultTableModel 
{
	BanKuaiGeGuTableModel () {
		super ();
	}
	
	String[] jtableTitleStrings = { "代码", "名称","权重","板块成交额贡献","板块占比增长率","BkMaxWk","大盘占比增长率","DpMaxWk","CjeMaxWk","换手率"};
	BanKuai curbk;
	private ArrayList<StockOfBanKuai> entryList;
	private LocalDate showwknum;
//	private int curdisplayrow = -1;
//	private Stock curdisplaystock;
//	private HashSet<String> stockcodeinparsefile;
	private Double showcje = 1000000000000.0;
	private Integer cjemaxwk = 10000000;
//	private Boolean showparsedfile = false;
	private Integer cjezbdpmaxwk = 10000000;
	private Integer cjezbbkmaxwk = 10000000;
	private String period;
	private Double huanshoulv = 1000000.0;
	private static Logger logger = Logger.getLogger(BanKuaiGeGuTableModel.class);

	public void refresh (BanKuai bankuai,LocalDate wknum,String period)
	{
		this.curbk = bankuai;
		this.showwknum = wknum;
		this.period = period;
		entryList = null;
		entryList = new ArrayList<StockOfBanKuai>( bankuai.getSpecificPeriodBanKuaiGeGu(wknum,0,period) );	
		
    	
    	this.fireTableDataChanged();
	}

	public String[] getTableHeader ()
	{
		return this.jtableTitleStrings;
	}
	public BanKuai getCurDispalyBandKuai ()
	{
		return this.curbk;
	}
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
		  
		  Stock stock = entryList.get(row);
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
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
//	    	logger.debug(rowIndex + "col" + columnIndex  + "  ");
	    	if(entryList.isEmpty())
	    		return null;

	    	StockOfBanKuai curdisplaystockofbankuai = entryList.get(rowIndex);
		    String stockcode = curdisplaystockofbankuai.getMyOwnCode();
		    String bkcode = curbk.getMyOwnCode();
		    NodeXPeriodDataBasic stockxdataforbk = curdisplaystockofbankuai.getNodeXPeroidData(period);

		    Stock stock = curdisplaystockofbankuai.getStock();
		    NodeXPeriodDataBasic stockxdata = stock.getNodeXPeroidData(period);

		    Object value = "??";
	    	switch (columnIndex) {
            case 0:
            	bkcode = curdisplaystockofbankuai.getMyOwnCode();
                value = bkcode;
                
//                bkcode = null;
                break;
            case 1: 
            	String thisbkname = curdisplaystockofbankuai.getMyOwnName();
            	value = thisbkname;
            	
//            	thisbkname = null;
            	
            	break;
            case 2: //权重
            	Integer stockweight =  curbk.getGeGuSuoShuBanKuaiWeight(stockcode);
            	try {
            		value = (Integer)stockweight;
            	} catch (java.lang.NullPointerException e) {
            		value = 0;
            	}
            	
//            	stockweight = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
            	break;
            case 3: // "板块成交额贡献","板块占比增长率","BkMaxWk","大盘占比增长率","DpMaxWk","CjeMaxWk"};
            	Double cjechangegrowthrate = stockxdataforbk.getChenJiaoErChangeGrowthRateOfSuperBanKuai(showwknum,0);// fxrecord.getGgbkcjegrowthzhanbi();
            	value = cjechangegrowthrate;
            	
//            	cjechangegrowthrate = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
            	break;
            case 4://,"板块成交额贡献","板块占比增长率","BkMaxWk","大盘占比增长率","DpMaxWk","CjeMaxWk"};
            	Double zhanbigrowthrate = stockxdataforbk.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(showwknum,0);//.getGgbkzhanbimaxweek();
            	value = zhanbigrowthrate;
            	
//            	zhanbigrowthrate = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
            	break;
            case 5: 
            	Integer maxweek =  stockxdataforbk.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(showwknum,0);//fxrecord.getGgbkzhanbigrowthrate(); 
            	value = (Integer)maxweek;
            	
//            	maxweek = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
            	break;
            case 6://"大盘占比增长率","DpMaxWk","CjeMaxWk"};
            	Double cjedpgrowthrate = stockxdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(showwknum,0);//.getGgdpzhanbigrowthrate();
            	value = cjedpgrowthrate;
            	
//            	cjedpgrowthrate = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
                break;
            case 7:
            	Integer dpmaxwk = stockxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek(); 
            	value = dpmaxwk;
            	
//            	dpmaxwk = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
                break;
           case 8:
            	Integer cjemaxwk = stockxdata.getChenJiaoErMaxWeekOfSuperBanKuai(showwknum,0);//.getGgbkcjemaxweek(); 
            	value = cjemaxwk;
            	
//            	cjemaxwk = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
                break;
           case 9: 
        	   Double hsl = ((StockNodeXPeriodData)stockxdata).getSpecificTimeHuanShouLv(showwknum, 0);
        	   value = hsl;

        	   break;
	    	}
	    	
	    	return value;
	  }
	  
	  /*
	   * (non-Javadoc)
	   * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	   * //for sorting http://www.codejava.net/java-se/swing/6-techniques-for-sorting-jtable-you-should-know
	   */
      public Class<?> getColumnClass(int columnIndex) { //{ "代码", "名称","权重","占比增长率","MAX","成交额贡献"};
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
			          clazz = Double.class;
			          break;
		        case 4:
			          clazz = Double.class;
			          break;
		        case 5:
			          clazz = Integer.class;
			          break;
		        case 6:
		        	  clazz = Double.class;
			          break;
		        case 7:
			          clazz = Integer.class;
			          break;
		        case 8:
			          clazz = Integer.class;
			          break;
		        case 9:
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
	    	return this.entryList.get(row);
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

//		public HashSet<String> getStockInParseFile() 
//		{
//			return curbk.getNodeTreerelated().getParseFileStockSet();
//		}
		
		//设置突出显示成交额阀值
		public void setDisplayChenJiaoEr (Double cje)
		{
			this.showcje = cje;
		}
		public Double getDisplayChenJiaoEr ()
		{
			return this.showcje ;
		}
		//设置bkMAXWK阀值
		public void setDisplayCjeMaxWk (Integer cjemax)
		{
			this.cjemaxwk = cjemax;
		}
		public Integer getDisplayCjeMaxWk ()
		{
			return this.cjemaxwk;
		}
		//设置显示每日板块文件
//		public void setShowParsedFile (Boolean onoff)
//		{
//			this.showparsedfile = onoff;
//		}
//		public Boolean showParsedFile ()
//		{
//			return this.showparsedfile ;
//		}
		//设置成交额dpMAXWK阀值
		public void setDisplayCjeDPMaxWk (Integer bkmax)
		{
			this.cjezbdpmaxwk = bkmax;
		}
		public Integer getDisplayCjeDPMaxWk ()
		{
			return this.cjezbdpmaxwk;
		}
		//设置bkMAXWK阀值
		public void setDisplayCjeBKMaxWk (Integer bkmax)
		{
			this.cjezbbkmaxwk = bkmax;
		}
		public Integer getDisplayCjeBKMaxWk ()
		{
			return this.cjezbbkmaxwk;
		}
		public LocalDate getShowCurDate ()
		{
			return this.showwknum;
		}
		public String getCurDisplayPeriod ()
		{
			return this.period;
		}
		public void setDisplayHuanShouLv (Double hsl)
		{
			this.huanshoulv = hsl;
		}
		public Double getDisplayHuanShouLv ()
		{
			return this.huanshoulv;
		}
		

}

