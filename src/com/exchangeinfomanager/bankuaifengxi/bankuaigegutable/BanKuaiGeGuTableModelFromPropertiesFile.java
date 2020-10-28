package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegubasictable.BanKuaiGeGuBasicTableModel;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.google.common.base.Splitter;

public class BanKuaiGeGuTableModelFromPropertiesFile extends BanKuaiGeGuBasicTableModel 
{
	private Properties prop;


	BanKuaiGeGuTableModelFromPropertiesFile (Properties prop) 
	{
		super ();
		this.prop = prop;
		
		createTableTitleStrings ();
//		String[] jtableTitleStrings = { "代码", "名称","板块成交额贡献","大盘CJEZB增长率","CJEZbDpMaxWk","N日内最小MinWk","周平均成交额MAXWK","高级排序排名"};
//		
//		super.setTableHeader(jtableTitleStrings);
//		
//		this.setDisplayChenJiaoEr(null, null);
//		this.setDisplayCjeZhanBiDPMaxMinWk(null,null);
//		this.setDisplayHuanShouLv(null);
//		this.setDisplayCjeMaxWk(null);
//		this.setDisplayLiuTongShiZhi(null, null);
//		this.setHighLightHuiBuDownQueKou(false);
	}

	private void createTableTitleStrings() 
	{
		String[] jtableTitleStrings = new String[8];
		
		String column_name0  = prop.getProperty ("0column_name");
		String column_name1  = prop.getProperty ("1column_name");
		String column_name2  = prop.getProperty ("2column_name");
		String column_name3  = prop.getProperty ("3column_name");
		String column_name4  = prop.getProperty ("4column_name");
		String column_name5  = prop.getProperty ("5column_name");
		String column_name6  = prop.getProperty ("6column_name");
		String column_name7  = prop.getProperty ("7column_name");
		
		jtableTitleStrings[0] = column_name0 ;
		jtableTitleStrings[1] =	column_name1;
		jtableTitleStrings[2] = column_name2;
		jtableTitleStrings[3] = column_name3;
		jtableTitleStrings[4] =	column_name4;
		jtableTitleStrings[5] =	column_name5;
		jtableTitleStrings[6] =	column_name6;
		jtableTitleStrings[7] =	column_name7;
		super.setTableHeader(jtableTitleStrings);
	
	}

	private static Logger logger = Logger.getLogger(BanKuaiGeGuTableModelFromPropertiesFile.class);
//	private BanKuaiGeGuMatchCondition condition;
//	private String systeminstalledpath;
	
	
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(entryList.isEmpty())
	    		return null;

	    	Object value = "??";
			switch (columnIndex) {
            case 0: //{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
            	String column_kw0  = prop.getProperty ("0column_info_keyword");
            	value = getColomnValue (column_kw0, rowIndex);
                break;
                
            case 1: 
            	String column_kw1  = prop.getProperty ("1column_info_keyword");
            	value = getColomnValue (column_kw1, rowIndex);
            	break;
            	
            case 2: // "板块成交额贡献",
            	String column_kw2  = prop.getProperty ("2column_info_keyword");
            	value = getColomnValue (column_kw2, rowIndex);
            	break;
            	
            case 3://{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
            	String column_kw3  = prop.getProperty ("3column_info_keyword");
            	value = getColomnValue (column_kw3, rowIndex);
            	break;            	
                
            case 4:
            	String column_kw4  = prop.getProperty ("4column_info_keyword");
            	value = getColomnValue (column_kw4, rowIndex);
                break;
                
            case 5://{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
            	String column_kw5  = prop.getProperty ("5column_info_keyword");
            	value = getColomnValue (column_kw5, rowIndex);
                break;
            case 6:
            	String column_kw6  = prop.getProperty ("6column_info_keyword");
            	value = getColomnValue (column_kw6, rowIndex);
                break;
            case 7://{ "代码", "名称","权重","流通市值排名",
            	String column_kw7  = prop.getProperty ("7column_info_keyword");
            	value = getColomnValue (column_kw7, rowIndex);            	
            	break;
	    	}
	    	
	    	return value;
	  }
	  
//	  private Object getColomnValue(String column_keyword, int rowIndex) 
//	  {
//		  Object value = "??";
//		  
//		  StockOfBanKuai curdisplaystockofbankuai = (StockOfBanKuai) entryList.get(rowIndex);
//		  String bkcode = curbk.getMyOwnCode();
//		  Stock stock = curdisplaystockofbankuai.getStock();
////		    DaPan dapan = (DaPan)stock.getRoot();
//		  NodeXPeriodData stockxdata = stock.getNodeXPeroidData(period);
//		    
//		  switch (column_keyword) {
//          case "nodecode":
//        	  String stockcode = curdisplaystockofbankuai.getMyOwnCode();
//        	  value = stockcode;
//        	  break;
//        	  
//          case "nodename":
//        	  String thisbkname = curdisplaystockofbankuai.getMyOwnName();
//          	  value = thisbkname;
//        	  break;
//        	  
//          case "bankuaichengjiaoergongxian":
//        	  Double cjechangegrowthrate = stockxdata.getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage(this.curbk,showwknum,0);// fxrecord.getGgbkcjegrowthzhanbi();
//          	  if(cjechangegrowthrate != null)
//          		value = cjechangegrowthrate;
//          	  else	value = -1;
//        	  break;
//        	  
//          case "cjezbgrowrate":
//        	  Double cjedpgrowthrate = stockxdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(showwknum,0);//.getGgdpzhanbigrowthrate();
//          	  if(cjedpgrowthrate != null)
//          		value = cjedpgrowthrate;
//          	  else	value = -1;
//        	  break;
//        	  
//          case "CjeZbDpMaxWk":
//        	  Integer cjedpmaxwk = stockxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek();
//	          try{
//	          		if(cjedpmaxwk > 0) {
//	              		value = cjedpmaxwk;
//	              		break;
//	              	} else	if(cjedpmaxwk == 0) {
//	              		Integer cjedpminwk = stockxdata.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(showwknum, 0);
//	              		value = 0 - cjedpminwk;
//	              		break;
//	              	}
//	          } catch (java.lang.NullPointerException e) {
//	          		e.printStackTrace();
//	          }
//	          break;
//          case "NCjeZbDpMinWk":
//        	  Integer cjldpgrowthrate = stockxdata.getChenJiaoErZhanBiMinestWeekOfSuperBanKuaiInSpecificPeriod(showwknum,0,15);//.getGgdpzhanbigrowthrate();
//          	  value = cjldpgrowthrate;
//          	  break;
//          case "averagecjemaxwk" :
//        	  Integer cjldpmaxwk = stockxdata.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek();
//          	  if(cjldpmaxwk != null && cjldpmaxwk > 0) 
//          		value = cjldpmaxwk;
//           	  else value = 0;
//          	  break;
//          case "highlevelpanxurank" :
//        	  Integer paiming = this.entryList.indexOf(curdisplaystockofbankuai) + 1;
//          	  value = paiming;
//        	  break;
//          	
//		  }
//		  
//		  return value;
//	  }

	/*
	   * (non-Javadoc)
	   * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	   * //for sorting http://www.codejava.net/java-se/swing/6-techniques-for-sorting-jtable-you-should-know
	   */
      public Class<?> getColumnClass(int columnIndex) { 
		      Class clazz = String.class;
		      switch (columnIndex) {
		      	case 0: //{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
		    	  clazz = String.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = Double.class;
			          break;
		        case 3:
			          clazz = Double.class;
			          break;
		        case 4:
		        	  clazz = Integer.class;
			          break;
		        case 5:
			          clazz = Integer.class;
			          break;
		        case 6:
			          clazz = Integer.class;
			          break;
		        case 7:
			          clazz = Integer.class;
			          break;
		      }
		      
		      return clazz;
	 }
      
//    public void setDisplayMatchCondition (BanKuaiGeGuMatchCondition cond)
//    {
//    	  this.condition = cond;
//    }
//    public BanKuaiGeGuMatchCondition getDisplayMatchCondition ()
//    {
//    	return this.condition;
//    }
	    
   

}

