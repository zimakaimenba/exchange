package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

import java.util.Properties;
import org.apache.log4j.Logger;

import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;

public class BanKuaiGeGuExternalInfoTableModelFromPropertiesFile extends BanKuaiGeGuBasicTableModel
{
//	private Properties prop;


	public BanKuaiGeGuExternalInfoTableModelFromPropertiesFile (Properties prop)
	{
		super (prop);
		this.prop = prop;
		
//		createTableTitleStrings();
		
//		String[] jtableTitleStrings = { "代码", "名称","权重","高级排序排名(突出成交额)","周日成交额连续涨","换手率"};
//		super.setTableHeader(jtableTitleStrings);
//		this.setDisplayChenJiaoEr(null, null);
//		this.setDisplayCjeBKMaxWk(null);
//		this.setDisplayCjeZhanBiDPMaxMinWk(null,null);
//		this.setDisplayHuanShouLv(null);
//		this.setDisplayCjeMaxWk(null);
//		this.setDisplayLiuTongShiZhi(null, null);
//		this.setHighLightHuiBuDownQueKou(false);
	}

	private static Logger logger = Logger.getLogger(BanKuaiGeGuExternalInfoTableModelFromPropertiesFile.class);
//	private BanKuaiGeGuMatchCondition matchcond;
	
//	protected void createTableTitleStrings() 
//	{
//		String[] jtableTitleStrings = new String[6];
//		
//		String column_name0  = prop.getProperty ("0column_name");
//		String column_name1  = prop.getProperty ("1column_name");
//		String column_name2  = prop.getProperty ("2column_name");
//		String column_name3  = prop.getProperty ("3column_name");
//		String column_name4  = prop.getProperty ("4column_name");
//		String column_name5  = prop.getProperty ("5column_name");
//		
//		jtableTitleStrings[0] = column_name0 ;
//		jtableTitleStrings[1] =	column_name1;
//		jtableTitleStrings[2] = column_name2;
//		jtableTitleStrings[3] = column_name3;
//		jtableTitleStrings[4] =	column_name4;
//		jtableTitleStrings[5] =	column_name5;
//		super.setTableHeader(jtableTitleStrings);
//	
//	}
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
//                break;
//                
//            case 1: 
//            	String column_kw1  = prop.getProperty ("1column_info_keyword");
//            	value = getColomnValue (column_kw1, rowIndex);
//            	break;
//            	
//            case 2: // "板块成交额贡献",
//            	String column_kw2  = prop.getProperty ("2column_info_keyword");
//            	value = getColomnValue (column_kw2, rowIndex);
//            	break;
//            	
//            case 3://{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
//            	String column_kw3  = prop.getProperty ("3column_info_keyword");
//            	value = getColomnValue (column_kw3, rowIndex);
//            	break;            	
//                
//            case 4:
//            	String column_kw4  = prop.getProperty ("4column_info_keyword");
//            	value = getColomnValue (column_kw4, rowIndex);
//                break;
//                
//            case 5://{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
//            	String column_kw5  = prop.getProperty ("5column_info_keyword");
//            	value = getColomnValue (column_kw5, rowIndex);
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
