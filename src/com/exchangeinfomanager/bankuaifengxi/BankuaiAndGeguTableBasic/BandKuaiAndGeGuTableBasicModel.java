package com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic;

import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.HanYuPinYing;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;

public abstract class BandKuaiAndGeGuTableBasicModel extends DefaultTableModel 
{
	protected Properties prop;

	public BandKuaiAndGeGuTableBasicModel (Properties prop) {
		super ();
		this.prop = prop;
		
		createTableTitleStrings ();
	}
	
	private static Logger logger = Logger.getLogger(BandKuaiAndGeGuTableBasicModel.class);
	
	protected String[] jtableTitleStrings ;
	protected BanKuai curbk;
	protected List<BkChanYeLianTreeNode> entryList;
	protected LocalDate showwknum;
	protected int difference;
	protected String curperiod;
	
	private BanKuaiGeGuMatchCondition matchcond;
	
	public void setTableHeader (String[] jtableTitleStrings1)
	{
		this.jtableTitleStrings = jtableTitleStrings1;
	}
	public String[] getTableHeader ()
	{
		return this.jtableTitleStrings;
	}
	protected void createTableTitleStrings ()
	{
		String[] jtableTitleStrings = new String[11];
		
		String column_name0  = prop.getProperty ("0column_name");
		String column_name1  = prop.getProperty ("1column_name");
		String column_name2  = prop.getProperty ("2column_name");
		String column_name3  = prop.getProperty ("3column_name");
		String column_name4  = prop.getProperty ("4column_name");
		String column_name5  = prop.getProperty ("5column_name");
		String column_name6  = prop.getProperty ("6column_name");
		
		String column_name7  = prop.getProperty ("7column_name");
		String column_name8  = prop.getProperty ("8column_name");
		String column_name9  = prop.getProperty ("9column_name");
		String column_name10  = prop.getProperty ("10column_name");
		
		if(column_name0 != null)
			jtableTitleStrings[0] = column_name0 ;
		else
			jtableTitleStrings[0] = "NULL";
		if(column_name1 != null)
			jtableTitleStrings[1] = column_name1 ;
		else
			jtableTitleStrings[1] = "NULL";
		if(column_name2 != null)
			jtableTitleStrings[2] = column_name2 ;
		else
			jtableTitleStrings[2] = "NULL";
		if(column_name3 != null)
			jtableTitleStrings[3] = column_name3 ;
		else
			jtableTitleStrings[3] = "NULL";
		if(column_name4 != null)
			jtableTitleStrings[4] = column_name4 ;
		else
			jtableTitleStrings[4] = "NULL";
		if(column_name5 != null)
			jtableTitleStrings[5] = column_name5 ;
		else
			jtableTitleStrings[5] = "NULL";
		if(column_name6 != null)
			jtableTitleStrings[6] = column_name6 ;
		else
			jtableTitleStrings[7] = "NULL";
		if(column_name7 != null)
			jtableTitleStrings[7] = column_name7 ;
		else
			jtableTitleStrings[7] = "NULL";
		if(column_name8 != null)
			jtableTitleStrings[8] = column_name8 ;
		else
			jtableTitleStrings[8] = "NULL";
		if(column_name9 != null)
			jtableTitleStrings[9] = column_name9 ;
		else
			jtableTitleStrings[9] = "NULL";
		if(column_name10 != null)
			jtableTitleStrings[10] = column_name10 ;
		else
			jtableTitleStrings[10] = "NULL";

		this.setTableHeader(jtableTitleStrings);
	}
	/*
	 * 
	 */
	public LocalDate getCurDisplayedDate ()
	{
		return this.showwknum;
	}
	/*
	 * 
	 */
	public String getCurDisplayPeriod ()
	{
		return this.curperiod;
	}
	@Override
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
	    @Override
	    public String getColumnName(int column) { 
	    	return jtableTitleStrings[column];
	    }//设置表格列名
	    @Override
	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    
	    
	    public List<BkChanYeLianTreeNode> getAllNodes ()
	    {
	    	return this.entryList;
	    }
	    public String getNodeCode (int row) 
	    {
	    	return (String)this.getValueAt(row,0);
	    }
	    public String getNodeName (int row) 
	    {
	    	return (String)this.getValueAt(row,1);
	    } 
	    public BkChanYeLianTreeNode getNode (String stockcode)
	    {
	    	int index = this.getNodeRowIndex (stockcode);
	    	BkChanYeLianTreeNode sob = this.getNode(index);
	    	return sob;
	    }
	    public BkChanYeLianTreeNode getNode (int row)
	    {
	    	return (BkChanYeLianTreeNode) this.entryList.get(row);
	    }
	    public void deleteAllRows ()
	    {	
	    	if(this.entryList == null)
				 return ;
			 else 
				 entryList.clear();
	    	
	    	this.showwknum = null;
	    	this.curperiod = null;
	    	this.curbk = null;
	    	
	    	this.fireTableDataChanged();
	    }
	    
	    public Integer getNodeRowIndex (String neededfindstring) //可以查找code也可以查找name
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
	    			
	    			Boolean compresult = hypy.compareTwoStrings (stockname, neededfindstring.trim() );
			   		if(compresult) {
			   			index = i;
			   			break;
			   		}
	    		}
	   		
	    		hypy = null;
	    		return index;
	    }


	protected Object getColomnValue(String column_keyword, int rowIndex) 
	{
		if(column_keyword == null)
			return null;
		
		  Object value = "??";
		  BkChanYeLianTreeNode node = entryList.get(rowIndex);
//		  BkChanYeLianTreeNode datanode; BkChanYeLianTreeNode datasupernode;
//		  DaPan dapan;
		  
		  NodeXPeriodData nodexdata = null;
		  if( node.getType() == BkChanYeLianTreeNode.BKGEGU ) {
			  Stock stock = ((StockOfBanKuai)node).getStock();
			  nodexdata = stock.getNodeXPeroidData(curperiod);
		  } else if (node.getType() == BkChanYeLianTreeNode.TDXBK) {
			  nodexdata = ((BanKuai) node).getNodeXPeroidData (curperiod);
//			  dapan = (DaPan)node.getRoot();
		  }
		    
		  switch (column_keyword) {
		       case "nodecode":
		     	  String stockcode = node.getMyOwnCode();
		     	  value = stockcode;
		     	  break;
		     	  
		       case "nodename":
		     	  String thisbkname = node.getMyOwnName();
		       	  value = thisbkname;
		     	  break;
		     	  
		       case "bankuaichengjiaoergongxian":
		     	  Double cjechangegrowthrate = nodexdata.getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage(this.curbk,showwknum,0);// fxrecord.getGgbkcjegrowthzhanbi();
		       	  if(cjechangegrowthrate != null)
		       		value = cjechangegrowthrate;
		       	  else	value = -1.0;
		     	  break;
		     	  
		       case "cjezbgrowrate":
		     	  Double cjedpgrowthrate = nodexdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(showwknum,0);//.getGgdpzhanbigrowthrate();
		       	  if(cjedpgrowthrate != null)
		       		value = cjedpgrowthrate;
		       	  else	value = -1;
		     	  break;
		       case "cjlzbgrowrate":
			     	  Double cjldpgrowthrate = nodexdata.getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(showwknum,0);//.getGgdpzhanbigrowthrate();
			       	  if(cjldpgrowthrate != null)
			       		value = cjldpgrowthrate;
			       	  else	value = -1;
			     	  break;
		       case "chengjiaoerzhanbi":
		    	   Double cjezhanbi = nodexdata.getChenJiaoErZhanBi(showwknum, 0);
           		   value = cjezhanbi;
		    	   break;
		       case "chengjiaoliangzhanbi":
		    	   Double cjlzhanbi = nodexdata.getChenJiaoLiangZhanBi(showwknum, 0);
           		   value = cjlzhanbi;
		    	   break;
		       case "CjeZbDpMaxWk":
			          try{
			        	  Integer cjedpmaxwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek();
			          		if(cjedpmaxwk > 0) {
			              		value = cjedpmaxwk;
			              		break;
			              	} else	if(cjedpmaxwk == 0) {
			              		Integer cjedpminwk = nodexdata.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(showwknum, 0);
			              		value = 0 - cjedpminwk;
			              		break;
			              	}
			          } catch (java.lang.NullPointerException e) {
			          		e.printStackTrace();
			          }
			          break;
		       case "CjlZbDpMaxWk":
				      try{
				    	  Integer cjldpmaxwk = nodexdata.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek();
				          		if(cjldpmaxwk > 0) {
				              		value = cjldpmaxwk;
				              		break;
				              	} else	if(cjldpmaxwk == 0) {
				              		Integer cjldpminwk = nodexdata.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(showwknum, 0);
				              		value = 0 - cjldpminwk;
				              		break;
				              	}
				       } catch (java.lang.NullPointerException e) {
				          		e.printStackTrace();
				       }
				       break;
		       case "NCjeZbDpMinWk":
		     	  Integer nCjeZbDpMinWk = nodexdata.getChenJiaoErZhanBiMinestWeekOfSuperBanKuaiInSpecificPeriod(showwknum,0,15);//.getGgdpzhanbigrowthrate();
		       	  value = nCjeZbDpMinWk;
		       	  break;
		       case "averagecjemaxwk" :
		    	   try {
		    		   Integer cjedpmaxwk = nodexdata.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek();
				       	  if(cjedpmaxwk != null && cjedpmaxwk > 0) 
				       		value = cjedpmaxwk;
				          else value = 0;
		    	   } catch (Exception e) {e.printStackTrace();   }
		       	  break;
		       case "averagecjlmaxwk" :
		    	   try {
		    		   Integer cjldpmaxwk = nodexdata.getAverageDailyChenJiaoLiangMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek();
				       	  if(cjldpmaxwk != null && cjldpmaxwk > 0) 
				       		value = cjldpmaxwk;
				          else value = 0;
		    	   } catch (Exception e) {e.printStackTrace();   }
		       	  break;
		       case "highlevelpanxurank" :
		     	  Integer paiming = this.entryList.indexOf(node) + 1;
		       	  value = paiming;
		     	  break;
		       case "quanzhonginbankuai" :
		     	  Integer stockweight =  curbk.getGeGuSuoShuBanKuaiWeight( node.getMyOwnCode() );
			        	try {
			        		value = (Integer)stockweight;
			        	} catch (java.lang.NullPointerException e) {
			        		value = 0;
			        	}
		     	  break;
		       case "CjeLianXuZhang": //周日平均成交额MAXWK
			        	Integer cjemaxwk = nodexdata.getAverageDailyCjeLianXuFangLiangPeriodNumber(showwknum,0);//.getGgbkcjemaxweek(); 
			        	value = cjemaxwk;
			        	break;
		       case "huanshoulv": //周日平均成交额MAXWK
			        	Double hsl = ((StockNodesXPeriodData)nodexdata).getSpecificTimeHuanShouLv(showwknum, 0);
				    	value = hsl;
				    	break;
		       case "chenjiaoer" :
			      	 Double curcje  = nodexdata.getChengJiaoEr(showwknum, 0) / 100000000;
			   	     value = curcje;
			   	     break;
		       case "chenjiaoliang" :
			      	 Double curcjl  = nodexdata.getChengJiaoLiang(showwknum, 0) / 100000000;
			   	     value = curcjl;
			   	     break;
		  }
		  
		  return value;
	  }
	
	protected Object reformateDoubleValue (int columnIndex, Object value, String columnIndexForDecimal)
    {
		if(value == null)
			return null;
		
    	Class<?> columncl = this.getColumnClass (columnIndex);
    	if (  columncl.equals(Double.class) ) {
    		String decimal = prop.getProperty (columnIndexForDecimal);
    		if(decimal != null) {
    			int decimalnumber = Integer.parseInt(decimal);
//    			BigDecimal roundOff = new BigDecimal( (Double)value).setScale(decimalnumber, BigDecimal.ROUND_HALF_EVEN);
//    			value = roundOff;
    			
    			double count = Math.pow(10, decimalnumber);
    			Double output = Math.round(  (Double)value * count) / count;
    			value = output;
    		}
    	}
    	
    	return value;
    }
	
	  protected Class<?> getColomnValueType(String column_keyword, int columnIndex)
	  {
	  	  Class clazz = String.class;
	  	  if(column_keyword != null)
	  		  column_keyword = column_keyword.trim().toUpperCase();
	  	  else
	  		  return clazz;
	  	  
	  	  switch (column_keyword) {
			    	case "STRING":
				          clazz = String.class;
				          break;
			        case "INTEGER":
				          clazz = Integer.class;
				          break;
			        case "DOUBLE":
				          clazz = Double.class;
				          break;
			        case "BOOLEAN":
				          clazz = Boolean.class;
				          break;
	  	  }
	  	  
	  	  return clazz;
     }
	  public void setDisplayMatchCondition(BanKuaiGeGuMatchCondition expc) 
	  {
			this.matchcond = expc;
			
	  }
	  public BanKuaiGeGuMatchCondition getDisplayMatchCondition ()
	  {
			return this.matchcond;
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
	        case 7:
	        	String column_kw7  = prop.getProperty ("7column_info_keyword");
	        	value = getColomnValue (column_kw7, rowIndex);
	        	value = reformateDoubleValue (columnIndex, value, "7column_info_value_decimal" );
	            break;
	        case 8:
	        	String column_kw8  = prop.getProperty ("8column_info_keyword");
	        	value = getColomnValue (column_kw8, rowIndex);
	        	value = reformateDoubleValue (columnIndex, value, "8column_info_value_decimal" );
	            break;
	        case 9:
	        	String column_kw9  = prop.getProperty ("9column_info_keyword");
	        	value = getColomnValue (column_kw9, rowIndex);
	        	value = reformateDoubleValue (columnIndex, value, "9column_info_value_decimal" );
	            break;
	        case 10:
	        	String column_kw10  = prop.getProperty ("10column_info_keyword");
	        	value = getColomnValue (column_kw10, rowIndex);
	        	value = reformateDoubleValue (columnIndex, value, "10column_info_value_decimal" );
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
		        case 7:
		        	String column_kwt7  = prop.getProperty ("7column_info_valuetype");
		        	clazz = getColomnValueType (column_kwt7, columnIndex);
	               break;
		        case 8:
		        	String column_kwt8  = prop.getProperty ("8column_info_valuetype");
		        	clazz = getColomnValueType (column_kwt8, columnIndex);
	               break;
		        case 9:
		        	String column_kwt9  = prop.getProperty ("9column_info_valuetype");
		        	clazz = getColomnValueType (column_kwt9, columnIndex);
	               break;
		        case 10:
		        	String column_kwt10  = prop.getProperty ("10column_info_valuetype");
		        	clazz = getColomnValueType (column_kwt10, columnIndex);
	               break;
		      }
		      
		      return clazz;
		 }

		
}
