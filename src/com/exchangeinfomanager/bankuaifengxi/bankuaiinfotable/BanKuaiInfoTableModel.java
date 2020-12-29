package com.exchangeinfomanager.bankuaifengxi.bankuaiinfotable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BandKuaiAndGeGuTableBasicModel;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.NodeChenJiaoErComparator;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.HanYuPinYing;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;


public class BanKuaiInfoTableModel extends BandKuaiAndGeGuTableBasicModel 
{

	public BanKuaiInfoTableModel(Properties prop) 
	{
		super (prop);
	}
	
//	String[] jtableTitleStrings = { "板块代码", "名称","CJE占比增长率","CJE占比","CJEZBMAXWK","大盘成交额增长贡献率(成交额上周变化升降)","周日平均成交额MAXWK(近期关注板块)","周日平均成交额连续"};
//	List<TDXNodes> entryList;
//	LocalDate showzhbiwknum;
//	private String curperiod;
//	private int difference;
//	private BanKuaiGeGuMatchCondition condition;
	
//	private static Logger logger = Logger.getLogger(BanKuaiInfoTableModel.class);
	
//	protected void createTableTitleStrings ()
//	{
//		String[] jtableTitleStrings = new String[11];
//		
//		String column_name0  = prop.getProperty ("0column_name");
//		String column_name1  = prop.getProperty ("1column_name");
//		String column_name2  = prop.getProperty ("2column_name");
//		String column_name3  = prop.getProperty ("3column_name");
//		String column_name4  = prop.getProperty ("4column_name");
//		String column_name5  = prop.getProperty ("5column_name");
//		String column_name6  = prop.getProperty ("6column_name");
//		
//		String column_name7  = prop.getProperty ("7column_name");
//		String column_name8  = prop.getProperty ("8column_name");
//		String column_name9  = prop.getProperty ("9column_name");
//		String column_name10  = prop.getProperty ("10column_name");
//		
//		if(column_name0 != null)
//			jtableTitleStrings[0] = column_name0 ;
//		else
//			jtableTitleStrings[0] = "NULL";
//		if(column_name1 != null)
//			jtableTitleStrings[1] = column_name1 ;
//		else
//			jtableTitleStrings[1] = "NULL";
//		if(column_name2 != null)
//			jtableTitleStrings[2] = column_name2 ;
//		else
//			jtableTitleStrings[2] = "NULL";
//		if(column_name3 != null)
//			jtableTitleStrings[3] = column_name3 ;
//		else
//			jtableTitleStrings[3] = "NULL";
//		if(column_name4 != null)
//			jtableTitleStrings[4] = column_name4 ;
//		else
//			jtableTitleStrings[4] = "NULL";
//		if(column_name5 != null)
//			jtableTitleStrings[5] = column_name5 ;
//		else
//			jtableTitleStrings[5] = "NULL";
//		if(column_name6 != null)
//			jtableTitleStrings[6] = column_name6 ;
//		else
//			jtableTitleStrings[7] = "NULL";
//		if(column_name7 != null)
//			jtableTitleStrings[7] = column_name7 ;
//		else
//			jtableTitleStrings[7] = "NULL";
//		if(column_name8 != null)
//			jtableTitleStrings[8] = column_name8 ;
//		else
//			jtableTitleStrings[8] = "NULL";
//		if(column_name9 != null)
//			jtableTitleStrings[9] = column_name9 ;
//		else
//			jtableTitleStrings[9] = "NULL";
//		if(column_name10 != null)
//			jtableTitleStrings[10] = column_name10 ;
//		else
//			jtableTitleStrings[10] = "NULL";
//
//		super.setTableHeader(jtableTitleStrings);
//	}
	
	/*
	 * 
	 */
	public void refresh  (LocalDate curselectdate,int difference2, String period)
	{
		this.showwknum = curselectdate;
		this.difference = difference2;
		this.curperiod = period;

		try{
			if(entryList != null) //按成交额排序
				Collections.sort(entryList, new NodeChenJiaoErComparator(showwknum,difference,curperiod) );
		} catch (java.lang.IllegalArgumentException e) {
			e.printStackTrace();
		}
	 	
		this.fireTableDataChanged();
	}

	public void addBanKuai ( BanKuai bankuai)
	{
		if(entryList == null)
			entryList = new ArrayList<BkChanYeLianTreeNode> ();
		
		entryList.add(bankuai);
	}
	public void addBanKuai ( List<BkChanYeLianTreeNode> bankuaiwithcje)
	{
		entryList = bankuaiwithcje;
	}
	

	public Object getValueAt(int rowIndex, int columnIndex) 
    {
		Object value = super.getValueAt(rowIndex, columnIndex);
//    	if(entryList.isEmpty())
//    		return null;
//
//    	Object value = "??";
//		switch (columnIndex) {
//        case 0: //{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
//        	String column_kw0  = prop.getProperty ("0column_info_keyword");
//        	value = getColomnValue (column_kw0, rowIndex);
//        	value = reformateDoubleValue (columnIndex, value, "0column_info_value_decimal" );
//            break;
//            
//        case 1: 
//        	String column_kw1  = prop.getProperty ("1column_info_keyword");
//        	value = getColomnValue (column_kw1, rowIndex);
//        	value = reformateDoubleValue (columnIndex, value, "1column_info_value_decimal" );
//        	break;
//        	
//        case 2: // "板块成交额贡献",
//        	String column_kw2  = prop.getProperty ("2column_info_keyword");
//        	value = getColomnValue (column_kw2, rowIndex);
//        	value = reformateDoubleValue (columnIndex, value, "2column_info_value_decimal" );
//        	break;
//        	
//        case 3://{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
//        	String column_kw3  = prop.getProperty ("3column_info_keyword");
//        	value = getColomnValue (column_kw3, rowIndex);
//        	value = reformateDoubleValue (columnIndex, value, "3column_info_value_decimal" );
//        	break;            	
//            
//        case 4:
//        	String column_kw4  = prop.getProperty ("4column_info_keyword");
//        	value = getColomnValue (column_kw4, rowIndex);
//        	value = reformateDoubleValue (columnIndex, value, "4column_info_value_decimal" );
//            break;
//            
//        case 5://{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
//        	String column_kw5  = prop.getProperty ("5column_info_keyword");
//        	value = getColomnValue (column_kw5, rowIndex);
//        	value = reformateDoubleValue (columnIndex, value, "5column_info_value_decimal" );
//            break;
//        case 6:
//        	String column_kw6  = prop.getProperty ("6column_info_keyword");
//        	value = getColomnValue (column_kw6, rowIndex);
//        	value = reformateDoubleValue (columnIndex, value, "6column_info_value_decimal" );
//            break;
//        case 7:
//        	String column_kw7  = prop.getProperty ("7column_info_keyword");
//        	value = getColomnValue (column_kw7, rowIndex);
//        	value = reformateDoubleValue (columnIndex, value, "7column_info_value_decimal" );
//            break;
//        case 8:
//        	String column_kw8  = prop.getProperty ("8column_info_keyword");
//        	value = getColomnValue (column_kw8, rowIndex);
//        	value = reformateDoubleValue (columnIndex, value, "8column_info_value_decimal" );
//            break;
//        case 9:
//        	String column_kw9  = prop.getProperty ("9column_info_keyword");
//        	value = getColomnValue (column_kw9, rowIndex);
//        	value = reformateDoubleValue (columnIndex, value, "9column_info_value_decimal" );
//            break;
//        case 10:
//        	String column_kw10  = prop.getProperty ("10column_info_keyword");
//        	value = getColomnValue (column_kw10, rowIndex);
//        	value = reformateDoubleValue (columnIndex, value, "10column_info_value_decimal" );
//            break;
//    	}
    	
    	return value;
    }
	
	 public Class<?> getColumnClass(int columnIndex) { 
		 Class<?> clazz = super.getColumnClass(columnIndex);
//	      Class clazz = String.class;
//	      switch (columnIndex) {
//	      	case 0: //{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
//	      		String column_kwt0  = prop.getProperty ("0column_info_valuetype");
//	        	clazz = getColomnValueType (column_kwt0, columnIndex);
//               break;
//	        case 1:
//	        	String column_kwt1  = prop.getProperty ("1column_info_valuetype");
//	        	clazz = getColomnValueType (column_kwt1, columnIndex);
//               break;	
//	        case 2:
//	        	String column_kwt2  = prop.getProperty ("2column_info_valuetype");
//	        	clazz = getColomnValueType (column_kwt2, columnIndex);
//               break;
//	        case 3:
//	        	String column_kwt3  = prop.getProperty ("3column_info_valuetype");
//	        	clazz = getColomnValueType (column_kwt3, columnIndex);
//               break;
//	        case 4:
//	        	String column_kwt4  = prop.getProperty ("4column_info_valuetype");
//	        	clazz = getColomnValueType (column_kwt4, columnIndex);
//               break;
//	        case 5:
//	        	String column_kwt5  = prop.getProperty ("5column_info_valuetype");
//	        	clazz = getColomnValueType (column_kwt5, columnIndex);
//               break;
//	        case 6:
//	        	String column_kwt6  = prop.getProperty ("6column_info_valuetype");
//	        	clazz = getColomnValueType (column_kwt6, columnIndex);
//               break;
//	        case 7:
//	        	String column_kwt7  = prop.getProperty ("7column_info_valuetype");
//	        	clazz = getColomnValueType (column_kwt7, columnIndex);
//               break;
//	        case 8:
//	        	String column_kwt8  = prop.getProperty ("8column_info_valuetype");
//	        	clazz = getColomnValueType (column_kwt8, columnIndex);
//               break;
//	        case 9:
//	        	String column_kwt9  = prop.getProperty ("9column_info_valuetype");
//	        	clazz = getColomnValueType (column_kwt9, columnIndex);
//               break;
//	        case 10:
//	        	String column_kwt10  = prop.getProperty ("10column_info_valuetype");
//	        	clazz = getColomnValueType (column_kwt10, columnIndex);
//               break;
//	      }
	      
	      return clazz;
	 }
	
}


