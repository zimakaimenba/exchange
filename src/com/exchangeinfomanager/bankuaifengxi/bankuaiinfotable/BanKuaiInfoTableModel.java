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
	
	protected void createTableTitleStrings ()
	{
		String[] jtableTitleStrings = new String[7];
		
		String column_name0  = prop.getProperty ("0column_name");
		String column_name1  = prop.getProperty ("1column_name");
		String column_name2  = prop.getProperty ("2column_name");
		String column_name3  = prop.getProperty ("3column_name");
		String column_name4  = prop.getProperty ("4column_name");
		String column_name5  = prop.getProperty ("5column_name");
		String column_name6  = prop.getProperty ("6column_name");
		
		
		jtableTitleStrings[0] = column_name0 ;
		jtableTitleStrings[1] =	column_name1;
		jtableTitleStrings[2] = column_name2;
		jtableTitleStrings[3] = column_name3;
		jtableTitleStrings[4] =	column_name4;
		jtableTitleStrings[5] =	column_name5;
		jtableTitleStrings[6] =	column_name6;

		super.setTableHeader(jtableTitleStrings);
	}
	
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
			entryList = new ArrayList<TDXNodes> ();
		
		entryList.add(bankuai);
	}
	public void addBanKuai ( List<TDXNodes> bankuaiwithcje)
	{
		entryList = bankuaiwithcje;
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
        	value = reformateDoubleValue (columnIndex, value, "0column_info_value_decimal" );
            break;
            
        case 1: 
        	String column_kw1  = prop.getProperty ("1column_info_keyword");
        	value = getColomnValue (column_kw1, rowIndex);
        	value = reformateDoubleValue (columnIndex, value, "1column_info_value_decimal" );
        	break;
        	
        case 2: // "板块成交额贡献",
        	String column_kw2  = prop.getProperty ("2column_info_keyword");
        	value = getColomnValue (column_kw2, rowIndex);
        	value = reformateDoubleValue (columnIndex, value, "2column_info_value_decimal" );
        	break;
        	
        case 3://{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
        	String column_kw3  = prop.getProperty ("3column_info_keyword");
        	value = getColomnValue (column_kw3, rowIndex);
        	value = reformateDoubleValue (columnIndex, value, "3column_info_value_decimal" );
        	break;            	
            
        case 4:
        	String column_kw4  = prop.getProperty ("4column_info_keyword");
        	value = getColomnValue (column_kw4, rowIndex);
        	value = reformateDoubleValue (columnIndex, value, "4column_info_value_decimal" );
            break;
            
        case 5://{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
        	String column_kw5  = prop.getProperty ("5column_info_keyword");
        	value = getColomnValue (column_kw5, rowIndex);
        	value = reformateDoubleValue (columnIndex, value, "5column_info_value_decimal" );
            break;
        case 6:
        	String column_kw6  = prop.getProperty ("6column_info_keyword");
        	value = getColomnValue (column_kw6, rowIndex);
        	value = reformateDoubleValue (columnIndex, value, "6column_info_value_decimal" );
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
	        case 6:
	        	String column_kwt6  = prop.getProperty ("6column_info_valuetype");
	        	clazz = getColomnValueType (column_kwt6, columnIndex);
               break;
	        
	      }
	      
	      return clazz;
}
	    
//	    public Object getValueAt(int rowIndex, int columnIndex) 
//	    {
//	    	if(entryList.isEmpty() )
//	    		return null;
//	    	
//	    	BanKuai bankuai = null;
//	    	DaPan dapan = null;
//	    	try {
//	    		bankuai = (BanKuai) entryList.get( rowIndex );
//	    		dapan = (DaPan)bankuai.getRoot();
//	    	} catch (java.lang.IndexOutOfBoundsException e) {
//	    		e.printStackTrace();
//	    		return null;
//	    	}
//	    	NodeXPeriodData bkxdata = bankuai.getNodeXPeroidData(this.curperiod);
//	    	logger.debug(bankuai.getMyOwnName() + bankuai.getMyOwnCode() );
//	    	
////	    	if(bankuai.getMyOwnCode().equals("159932"))
////	    		logger.debug("可能错误");
//	    	
//	    	Object value = "??";
//	    	switch (columnIndex) {
//            case 0:
//            	String bkcode = bankuai.getMyOwnCode();
//                value = bkcode;
//                break;
//            case 1:
//            	String thisbkname = bankuai.getMyOwnName();
//            	value = thisbkname;
//            	break;
//            case 2: //"板块代码", "名称","CJE占比增长率","CJE占比","CJL占比增长率","CJL占比","大盘成交额增长贡献率","成交额排名"
//            	Double zhanbigrowthrate = bkxdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(showzhbiwknum,0);// fxjg.getGgBkCjeZhanbiGrowthRate();
//            	value = zhanbigrowthrate;
//            	
//            	zhanbigrowthrate = null;
//            	bkxdata = null;
//            	bankuai = null;
//            			
//            	break;
//            case 3: //"板块代码", "名称","CJE占比增长率","CJE占比","CJL占比增长率","CJL占比","大盘成交额增长贡献率","成交额排名"
//            	Double zhanbi = bkxdata.getChenJiaoErZhanBi(showzhbiwknum, 0);
//            	value = zhanbi;
//            	
//            	zhanbi = null;
//            	bkxdata = null;
//            	bankuai = null;
//            	
//            	break;
////            case 4:
////            	Double cjlzhanbigrowthrate = bkxdata.getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(showzhbiwknum,0);
////            	value = cjlzhanbigrowthrate;
////            	
////            	cjlzhanbigrowthrate = null;
////            	bkxdata = null;
////            	bankuai = null;
////            	break;
//            case 4:
//            	Integer cjlzhanbimaxwk = bkxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(showzhbiwknum, 0);
//            	value = cjlzhanbimaxwk;
//            	
////            	cjlzhanbimaxwk = null;
////            	bkxdata = null;
////            	bankuai = null;
//            	
//            	break;
//            case 5:
//            	Double cjegrowthrate = bkxdata.getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage(dapan,showzhbiwknum,0);
//            	value = cjegrowthrate;
//            	
////            	cjegrowthrate = null;
////            	bkxdata = null;
////            	bankuai = null;
//            	
//            	break;
//
//            case 6: 
//            	if(bankuai.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) || bankuai.getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) {
//            		Integer avgdailycjemaxwk = bkxdata.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(showzhbiwknum,0);
//            		value = avgdailycjemaxwk;
//            	} else 
//            		value = -5000;
//            	
//            		break;
//           case 7:
//        	   if(bankuai.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) || bankuai.getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) {
//	           		Integer avgdailycjemaxwk = bkxdata.getAverageDailyCjeLianXuFangLiangPeriodNumber(showzhbiwknum,0);
//	           		value = avgdailycjemaxwk;
//           		} else 
//           			value = -5000;
//        	   
//           		break;
//	       }
//
//	    	return value;
//	    
//	  }

//     public Class<?> getColumnClass(int columnIndex) {
//		      Class clazz = String.class;
//		      switch (columnIndex) {
//		      case 0://"板块代码", "名称","CJE占比增长率","CJE占比","CJL占比增长率","CJL占比","大盘成交额增长贡献率","成交额排名"
//		    	  clazz = String.class;
//		    	  break;
//		        case 1:
//			          clazz = String.class;
//			          break;
//		        case 2:
//			          clazz = Double.class;
//			          break;
//		        case 3:
//			          clazz = Double.class;
//			          break;
//		        case 4:
//			          clazz = Integer.class;
//			          break;
//		        case 5:
//			          clazz = Double.class;
//			          break;
//		        case 6:
//			          clazz = Integer.class;
//			          break;
//		        case 7:
//		        	clazz = Integer.class;
//			          break;		
//		      }
//		      
//		      return clazz;
//	 }
	    
//	    public String getColumnName(int column) { 
//	    	return jtableTitleStrings[column];
//	    }//设置表格列名 
//		
//
//	    public boolean isCellEditable(int row,int column) {
//	    	return false;
//		}
//	    public String getBanKuaiCode (int row) 
//	    {
//	    	return (String)this.getValueAt(row,0);
//	    }
//	    public String getBanKuaiName (int row) 
//	    {
//	    	return (String)this.getValueAt(row,1);
//	    } 
//	    public BanKuai getBanKuai (int row)
//	    {
//	    	return (BanKuai) this.entryList.get(row);
//	    }
//	    public List<TDXNodes> getAllBanKuai ()
//	    {
//	    	return this.entryList;
//	    }

//	    public Integer getBanKuaiRowIndex (String neededfindstring) 
//	    {
//	    	if(entryList == null)
//	    		return null;
//	    	
//	    	for(TDXNodes tmpnode : this.entryList) {
//	    		if(tmpnode.getMyOwnCode().equals(neededfindstring) || tmpnode.getMyOwnName().equals(neededfindstring) )
//	    			return this.entryList.indexOf(tmpnode);
//	    	}
////	    		int index = -1;
////	    		HanYuPinYing hypy = new HanYuPinYing ();
////	    		
////	    		for(int i=0;i<this.getRowCount();i++) {
////	    			String bkcode = (String)this.getValueAt(i, 0);
////	    			String bkname = (String)this.getValueAt(i,1); 
////	    			if(bkcode.trim().equals(neededfindstring) ) {
////	    				index = i;
////	    				break;
////	    			}
////
////	    			String namehypy = hypy.getBanKuaiNameOfPinYin(bkname );
////			   		if(namehypy.toLowerCase().equals(neededfindstring.trim().toLowerCase())) {
////			   			index = i;
////			   			break;
////			   		}
////	    		}
////	    	hypy = null;
//	   		return null;
//	    }

//		public String[] getTableHeader() 
//		{
//			return this.jtableTitleStrings;
//		}
//		
//		public void setDisplayMatchCondition (BanKuaiGeGuMatchCondition cond)
//	    {
//	    	  this.condition = cond;
//	    }
//	    public BanKuaiGeGuMatchCondition getDisplayMatchCondition ()
//	    {
//	    	return this.condition;
//	    }

}


