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
import com.exchangeinfomanager.asinglestockinfo.ChenJiaoZhanBiInGivenPeriod;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.bankuaichanyelian.HanYuPinYing;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class BanKuaiGeGuTableModel extends DefaultTableModel 
{
	BanKuaiGeGuTableModel () {
		super ();
	}
	
	String[] jtableTitleStrings = { "代码", "名称","板块权重","板块成交额贡献","板块占比增长率","BkMaxWk","大盘占比增长率","DpMaxWk","CjeMaxWk"};
	BanKuai curbk;
	private ArrayList<Entry<String, Stock>> entryList;
	private LocalDate showwknum;
	private int curdisplayrow = -1;
	private Stock curdisplaystock;
	private HashSet<String> stockcodeinparsefile;
	private Double showcje;
	private Integer cjemaxwk = 10000000;
	private Boolean showparsedfile = false;
	private Integer dpmaxwk = 10000000;
	private Integer bkmaxwk = 10000000;
	private String period;
	private static Logger logger = Logger.getLogger(BanKuaiGeGuTableModel.class);

	public void refresh (BanKuai bankuai,LocalDate wknum,String period)
	{
		this.curbk = bankuai;
		this.showwknum = wknum;
		this.period = period;
		HashMap<String, Stock> stockmap = bankuai.getSpecificPeriodBanKuaiGeGu(wknum,period);	
		stockcodeinparsefile = bankuai.getNodetreerelated().getParseFileStockSet();
		try {
			entryList = new ArrayList<Map.Entry<String, Stock>>(stockmap.entrySet()  );
		} catch ( java.lang.NullPointerException e) {
			logger.debug(curbk.getMyOwnCode()+curbk.getMyOwnName() + "没有个股，有问题，请检查！");
		}
    	
    	this.fireTableDataChanged();
	}

	public String[] getTableHeader ()
	{
		return this.jtableTitleStrings;
	}
	public BanKuai getTdxBkCode ()
	{
		return this.curbk;
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
//	    	logger.debug(rowIndex + "col" + columnIndex  + "  ");
	    	if(entryList.isEmpty())
	    		return null;

	    	Entry<String, Stock> thisbk = entryList.get(rowIndex);
		    curdisplaystock = thisbk.getValue();
		    String bkcode = curdisplaystock.getMyOwnCode();
		    StockNodeXPeriodData stockxdataforbk = curdisplaystock.getStockXPeriodDataForABanKuai(bkcode, period);
		    ChenJiaoZhanBiInGivenPeriod fxrecord = stockxdataforbk.getSpecficRecord(showwknum, 0);
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
            	bkcode = curdisplaystock.getMyOwnCode();
                value = bkcode;
                break;
            case 1: 
            	String thisbkname = curdisplaystock.getMyOwnName();
            	value = thisbkname;
            	break;
            case 2: //权重
            	Integer stockweight = curdisplaystock.getGeGuSuoShuBanKuaiWeight(bkcode);
            	try {
            		value = (Integer)stockweight;
            	} catch (java.lang.NullPointerException e) {
            		value = 0;
            	}
            	break;
            case 3: //{ "代码", "名称","板块权重","板块占比增长率","BkMaxWk","板块成交额贡献","大盘占比增长率","DpMaxWk"};
            	Double cjechangegrowthrate = fxrecord.getGgbkcjegrowthzhanbi();
            	value = cjechangegrowthrate;
            	break;
            case 4: //{ "代码", "名称","板块权重","板块占比增长率","BkMaxWk","板块成交额贡献","大盘占比增长率","DpMaxWk"};
            	Double zhanbigrowthrate = fxrecord.getGgbkzhanbigrowthrate(); 
            	value = zhanbigrowthrate;
            	break;
            case 5: //{ "代码", "名称","板块权重","板块占比增长率","BkMaxWk","板块成交额贡献","大盘占比增长率","DpMaxWk"};
            	int maxweek = fxrecord.getGgbkzhanbimaxweek();
            	value = (Integer)maxweek;
            	break;
            case 6:
            	Double cjedpgrowthrate = fxrecord.getGgdpzhanbigrowthrate();
            	value = cjedpgrowthrate;
                break;
            case 7:
            	Integer dpmaxwk = fxrecord.getGgdpzhanbimaxweek(); 
            	value = dpmaxwk;
                break;
           case 8:
            	Integer cjemaxwk = fxrecord.getGgbkcjemaxweek(); 
            	value = cjemaxwk;
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
		  
		  Entry<String, Stock> thisstock = entryList.get(row);
		  Stock stock = thisstock.getValue();
	      String bkcode = thisstock.getValue().getMyOwnCode();
	      String thisbkname = thisstock.getValue().getMyOwnName();

		  stock.setGeGuSuoShuBanKuaiWeight(curbk.getMyOwnCode(),newweight);
		  this.fireTableDataChanged();
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
	    	return this.entryList.get(row).getValue();
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
			return curbk.getNodetreerelated().getParseFileStockSet();
		}
		
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
		public void setShowParsedFile (Boolean onoff)
		{
			this.showparsedfile = onoff;
		}
		public Boolean showParsedFile ()
		{
			return this.showparsedfile ;
		}
		//设置成交额dpMAXWK阀值
		public void setDisplayDPMaxWk (Integer bkmax)
		{
			this.dpmaxwk = bkmax;
		}
		public Integer getDisplayDPMaxWk ()
		{
			return this.dpmaxwk;
		}
		//设置bkMAXWK阀值
		public void setDisplayBKMaxWk (Integer bkmax)
		{
			this.bkmaxwk = bkmax;
		}
		public Integer getDisplayBKMaxWk ()
		{
			return this.bkmaxwk;
		}
		public LocalDate getShowCurDate ()
		{
			return this.showwknum;
		}
		

}

