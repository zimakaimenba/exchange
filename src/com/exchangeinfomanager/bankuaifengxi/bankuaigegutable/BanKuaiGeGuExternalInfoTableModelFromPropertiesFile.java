package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

import java.util.Properties;
import org.apache.log4j.Logger;

import com.exchangeinfomanager.bankuaifengxi.bankuaigegubasictable.BanKuaiGeGuBasicTableModel;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;

public class BanKuaiGeGuExternalInfoTableModelFromPropertiesFile extends BanKuaiGeGuBasicTableModel
{
	private Properties prop;


	public BanKuaiGeGuExternalInfoTableModelFromPropertiesFile (Properties prop)
	{
		super ();
		this.prop = prop;
		
		createTableTitleStrings();
		
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
	
	private void createTableTitleStrings() 
	{
		String[] jtableTitleStrings = new String[6];
		
		String column_name0  = prop.getProperty ("0column_name");
		String column_name1  = prop.getProperty ("1column_name");
		String column_name2  = prop.getProperty ("2column_name");
		String column_name3  = prop.getProperty ("3column_name");
		String column_name4  = prop.getProperty ("4column_name");
		String column_name5  = prop.getProperty ("5column_name");
		
		jtableTitleStrings[0] = column_name0 ;
		jtableTitleStrings[1] =	column_name1;
		jtableTitleStrings[2] = column_name2;
		jtableTitleStrings[3] = column_name3;
		jtableTitleStrings[4] =	column_name4;
		jtableTitleStrings[5] =	column_name5;
		super.setTableHeader(jtableTitleStrings);
	
	}
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
	    	}
	    	
	    	return value;
	  }
	
		public Class<?> getColumnClass(int columnIndex) { 
		      Class clazz = String.class;
		      switch (columnIndex) {
		      	case 0: //{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
		      		String column_kwt0  = prop.getProperty ("0column_info_valuetype");
		        	clazz = getColomnValueType (column_kwt0, columnIndex);
	                break;
		        case 1:
		        	String column_kwt1  = prop.getProperty ("1column_info_valuetype");
		        	clazz = getColomnValueType (column_kwt1, columnIndex);
	                break;	
		        case 2:
		        	String column_kwt2  = prop.getProperty ("2column_info_valuetype");
		        	clazz = getColomnValueType (column_kwt2, columnIndex);
	                break;
		        case 3:
		        	String column_kwt3  = prop.getProperty ("3column_info_valuetype");
		        	clazz = getColomnValueType (column_kwt3, columnIndex);
	                break;
		        case 4:
		        	String column_kwt4  = prop.getProperty ("4column_info_valuetype");
		        	clazz = getColomnValueType (column_kwt4, columnIndex);
	                break;
		        case 5:
		        	String column_kwt5  = prop.getProperty ("5column_info_valuetype");
		        	clazz = getColomnValueType (column_kwt5, columnIndex);
	                break;
		      }
		      
		      return clazz;
	 }

}
