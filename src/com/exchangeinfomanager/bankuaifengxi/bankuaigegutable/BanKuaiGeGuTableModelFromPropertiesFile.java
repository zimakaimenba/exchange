package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.google.common.base.Splitter;

public class BanKuaiGeGuTableModelFromPropertiesFile extends BanKuaiGeGuBasicTableModel 
{
//	private Properties prop;


	BanKuaiGeGuTableModelFromPropertiesFile (Properties prop) 
	{
		super (prop);
//		this.prop = prop;
		
//		createTableTitleStrings ();
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

//	protected void createTableTitleStrings() 
//	{
//		String[] jtableTitleStrings = new String[8];
//		
//		String column_name0  = prop.getProperty ("0column_name");
//		String column_name1  = prop.getProperty ("1column_name");
//		String column_name2  = prop.getProperty ("2column_name");
//		String column_name3  = prop.getProperty ("3column_name");
//		String column_name4  = prop.getProperty ("4column_name");
//		String column_name5  = prop.getProperty ("5column_name");
//		String column_name6  = prop.getProperty ("6column_name");
//		String column_name7  = prop.getProperty ("7column_name");
//		
//		jtableTitleStrings[0] = column_name0 ;
//		jtableTitleStrings[1] =	column_name1;
//		jtableTitleStrings[2] = column_name2;
//		jtableTitleStrings[3] = column_name3;
//		jtableTitleStrings[4] =	column_name4;
//		jtableTitleStrings[5] =	column_name5;
//		jtableTitleStrings[6] =	column_name6;
//		jtableTitleStrings[7] =	column_name7;
//		super.setTableHeader(jtableTitleStrings);
//	
//	}

	private static Logger logger = Logger.getLogger(BanKuaiGeGuTableModelFromPropertiesFile.class);
//	private BanKuaiGeGuMatchCondition condition;
//	private String systeminstalledpath;
	
	
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	Object value = super.getValueAt(rowIndex, columnIndex);
	    	
//	    	if(entryList.isEmpty())
//	    		return null;
//
//	    	Object value = "??";
//			switch (columnIndex) {
//            case 0: //{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
//            	String column_kw0  = prop.getProperty ("0column_info_keyword");
//            	value = getColomnValue (column_kw0, rowIndex);
//            	value = reformateDoubleValue (columnIndex, value, "0column_info_value_decimal" );
//                break;
//                
//            case 1: 
//            	String column_kw1  = prop.getProperty ("1column_info_keyword");
//            	value = getColomnValue (column_kw1, rowIndex);
//            	value = reformateDoubleValue (columnIndex, value, "1column_info_value_decimal" );
//            	break;
//            	
//            case 2: // "板块成交额贡献",
//            	String column_kw2  = prop.getProperty ("2column_info_keyword");
//            	value = getColomnValue (column_kw2, rowIndex);
//            	value = reformateDoubleValue (columnIndex, value, "2column_info_value_decimal" );
//            	break;
//            	
//            case 3://{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
//            	String column_kw3  = prop.getProperty ("3column_info_keyword");
//            	value = getColomnValue (column_kw3, rowIndex);
//            	value = reformateDoubleValue (columnIndex, value, "3column_info_value_decimal" );
//            	break;            	
//                
//            case 4:
//            	String column_kw4  = prop.getProperty ("4column_info_keyword");
//            	value = getColomnValue (column_kw4, rowIndex);
//            	value = reformateDoubleValue (columnIndex, value, "4column_info_value_decimal" );
//                break;
//                
//            case 5://{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
//            	String column_kw5  = prop.getProperty ("5column_info_keyword");
//            	value = getColomnValue (column_kw5, rowIndex);
//            	value = reformateDoubleValue (columnIndex, value, "5column_info_value_decimal" );
//                break;
//            case 6:
//            	String column_kw6  = prop.getProperty ("6column_info_keyword");
//            	value = getColomnValue (column_kw6, rowIndex);
//            	value = reformateDoubleValue (columnIndex, value, "6column_info_value_decimal" );
//                break;
//            case 7://{ "代码", "名称","权重","流通市值排名",
//            	String column_kw7  = prop.getProperty ("7column_info_keyword");
//            	value = getColomnValue (column_kw7, rowIndex);    
//            	value = reformateDoubleValue (columnIndex, value, "7column_info_value_decimal" );
//            	break;
//            case 8:
//            	String column_kw8  = prop.getProperty ("8column_info_keyword");
//            	value = getColomnValue (column_kw8, rowIndex);
//            	value = reformateDoubleValue (columnIndex, value, "8column_info_value_decimal" );
//                break;
//            case 9:
//            	String column_kw9  = prop.getProperty ("9column_info_keyword");
//            	value = getColomnValue (column_kw9, rowIndex);
//            	value = reformateDoubleValue (columnIndex, value, "9column_info_value_decimal" );
//                break;
//            case 10:
//            	String column_kw10  = prop.getProperty ("10column_info_keyword");
//            	value = getColomnValue (column_kw10, rowIndex);
//            	value = reformateDoubleValue (columnIndex, value, "10column_info_value_decimal" );
//                break;
//	    	}
	    	
	    	return value;
	  }
	    /**
	     * for the Double, only keep the decimal based on config
	     * @param columnIndex
	     * @param value
	     * @param columnIndexForDecimal
	     * @return
	     */
//	    private Object reformateDoubleValue (int columnIndex, Object value, String columnIndexForDecimal)
//	    {
//	    	Class<?> columncl = this.getColumnClass (columnIndex);
//	    	if (  columncl.equals(Double.class) ) {
//        		String decimal = prop.getProperty (columnIndexForDecimal);
//        		if(decimal != null) {
//        			int decimalnumber = Integer.parseInt(decimal);
////        			BigDecimal roundOff = new BigDecimal( (Double)value).setScale(decimalnumber, BigDecimal.ROUND_HALF_EVEN);
////        			value = roundOff;
//        			
//        			double count = Math.pow(10, decimalnumber);
//        			Double output = Math.round(  (Double)value * count) / count;
//        			value = output;
//        		}
//        	}
//	    	
//	    	return value;
//	    }
	/*
	   * (non-Javadoc)
	   * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	   * //for sorting http://www.codejava.net/java-se/swing/6-techniques-for-sorting-jtable-you-should-know
	   */
      public Class<?> getColumnClass(int columnIndex) { 
    	  Class clazz = super.getColumnClass(columnIndex);
//		      Class clazz = String.class;
//		      switch (columnIndex) {
//		      	case 0: //{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
//		      		String column_kwt0  = prop.getProperty ("0column_info_valuetype");
//		        	clazz = getColomnValueType (column_kwt0, columnIndex);
//	                break;
//		        case 1:
//		        	String column_kwt1  = prop.getProperty ("1column_info_valuetype");
//		        	clazz = getColomnValueType (column_kwt1, columnIndex);
//	                break;	
//		        case 2:
//		        	String column_kwt2  = prop.getProperty ("2column_info_valuetype");
//		        	clazz = getColomnValueType (column_kwt2, columnIndex);
//	                break;
//		        case 3:
//		        	String column_kwt3  = prop.getProperty ("3column_info_valuetype");
//		        	clazz = getColomnValueType (column_kwt3, columnIndex);
//	                break;
//		        case 4:
//		        	String column_kwt4  = prop.getProperty ("4column_info_valuetype");
//		        	clazz = getColomnValueType (column_kwt4, columnIndex);
//	                break;
//		        case 5:
//		        	String column_kwt5  = prop.getProperty ("5column_info_valuetype");
//		        	clazz = getColomnValueType (column_kwt5, columnIndex);
//	                break;
//		        case 6:
//		        	String column_kwt6  = prop.getProperty ("6column_info_valuetype");
//		        	clazz = getColomnValueType (column_kwt6, columnIndex);
//	                break;
//		        case 7:
//		        	String column_kwt7  = prop.getProperty ("7column_info_valuetype");
//		        	clazz = getColomnValueType (column_kwt7, columnIndex);
//	                break;
//		        case 8:
//		        	String column_kwt8  = prop.getProperty ("8column_info_valuetype");
//		        	clazz = getColomnValueType (column_kwt8, columnIndex);
//	               break;
//		        case 9:
//		        	String column_kwt9  = prop.getProperty ("9column_info_valuetype");
//		        	clazz = getColomnValueType (column_kwt9, columnIndex);
//	               break;
//		        case 10:
//		        	String column_kwt10  = prop.getProperty ("10column_info_valuetype");
//		        	clazz = getColomnValueType (column_kwt10, columnIndex);
//	               break;
//		      }
		      
		      return clazz;
	 }
      
}

