package com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

import com.exchangeinfomanager.nodes.HanYuPinYing;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

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
	protected TDXNodes curbk;
	protected List<BkChanYeLianTreeNode> entryList;
	protected LocalDate showwknum;
	protected int difference;
	protected String curperiod;
	
	protected BanKuaiGeGuMatchCondition matchcond;
	
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
		for(int i=0;i<=10;i++) {
			String column_name  = prop.getProperty (String.valueOf(i) + "column_name");
			if(column_name != null)
				jtableTitleStrings[i] = column_name ;
			else
				jtableTitleStrings[i] = "NULL";
		}

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
		 column_keyword = column_keyword.trim();
		
		  Object value = null;
		  
		  BkChanYeLianTreeNode node = entryList.get(rowIndex);
		  NodeXPeriodData nodexdatawk = null; NodeXPeriodData nodexdataday = null;
		  if( node.getType() == BkChanYeLianTreeNode.BKGEGU ) {
			  Stock stock = ((StockOfBanKuai)node).getStock();
			  nodexdatawk = stock.getNodeXPeroidData(curperiod);
			  nodexdataday = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
		  } else if ( node.getType() == BkChanYeLianTreeNode.TDXBK || node.getType() == BkChanYeLianTreeNode.DZHBK ) {
			  nodexdatawk = ((BanKuai) node).getNodeXPeroidData (curperiod);
			  nodexdataday = ((BanKuai) node).getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
		  }
		    
		  switch (column_keyword) {
		  case "keywords":
			  TagsServiceForNodes tagsservicesfornode = new TagsServiceForNodes (node);
		      Collection<Tag> nodetags = null;
			  try { nodetags = tagsservicesfornode.getTags();
			  } catch (SQLException e) {e.printStackTrace();}
			  tagsservicesfornode = null;
				
		      String tagslist = " ";
		      for (Iterator<Tag> it = nodetags.iterator(); it.hasNext(); ) {
		    		Tag t = it.next();
		    		tagslist = tagslist + t.getName() + "  ";
		      }
		      value = tagslist;
			  break;
			  
		  		case "suoshubankuai" :
		  			if (node.getType() == BkChanYeLianTreeNode.TDXBK) {
		  				value = node.getMyOwnCode() + node.getMyOwnName();
		  			} else 
		  			if( node.getType() == BkChanYeLianTreeNode.BKGEGU ) {
		  				Stock stock = ((StockOfBanKuai)node).getStock();
		  				Set<BkChanYeLianTreeNode> banklist = stock.getGeGuCurSuoShuTDXSysBanKuaiList();
		  				String bankuailist = "";
		  				for(BkChanYeLianTreeNode tmpbknode : banklist) 
		  					bankuailist = bankuailist + tmpbknode.getMyOwnCode() + tmpbknode.getMyOwnName() + ";" ; 
		  				Set<BkChanYeLianTreeNode> dzhbanklist = stock.getGeGuCurSuoShuDZHSysBanKuaiList();
		  				for(BkChanYeLianTreeNode tmpbknode : dzhbanklist) 
		  					bankuailist = bankuailist + tmpbknode.getMyOwnCode() + tmpbknode.getMyOwnName() + ";" ;
		  				
		  				value = bankuailist;
		  			}
		  			break;
		       case "nodecode":
		     	  String stockcode = node.getMyOwnCode();
		     	  value = stockcode;
		     	  break;
		     	  
		       case "nodename":
		     	  String thisbkname = node.getMyOwnName();
		       	  value = thisbkname;
		     	  break;
		     	  
		       case "bankuaichengjiaoergongxian":
		     	  Double cjechangegrowthrate = nodexdatawk.getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage(this.curbk,showwknum,0);// fxrecord.getGgbkcjegrowthzhanbi();
		     	  value = cjechangegrowthrate;
		     	  break;
		       case "bankuaichengjiaolianggongxian":
			     	  Double cjlchangegrowthrate = nodexdatawk.getChenJiaoLiangChangeGrowthRateOfSuperBanKuaiOnDailyAverage(this.curbk,showwknum,0);// fxrecord.getGgbkcjegrowthzhanbi();
			     	  value = cjlchangegrowthrate;
			     	  break;	  
		       case "cjezbgrowrate":
		     	  Double cjedpgrowthrate = nodexdatawk.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(showwknum,0);//.getGgdpzhanbigrowthrate();
		     	  value = cjedpgrowthrate;
		     	  break;
		       case "cjlzbgrowrate":
			     	  Double cjldpgrowthrate = nodexdatawk.getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(showwknum,0);//.getGgdpzhanbigrowthrate();
			     	  value = cjldpgrowthrate;
			     	  break;
		       case "chengjiaoerzhanbi":
		    	   Double cjezhanbi = nodexdatawk.getChenJiaoErZhanBi(showwknum, 0);
           		   value = cjezhanbi;
		    	   break;
		       case "chengjiaoliangzhanbi":
		    	   Double cjlzhanbi = nodexdatawk.getChenJiaoLiangZhanBi(showwknum, 0);
           		   value = cjlzhanbi;
		    	   break;
		       case "CjeZbDpMaxWk":
		    	   	  Integer cjedpmaxwk = null;
			          try{
			        	   cjedpmaxwk = nodexdatawk.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek();
			          		if(cjedpmaxwk > 0) {
			              		value = cjedpmaxwk;
			              		break;
			              	} else	if(cjedpmaxwk == 0) {
			              		Integer cjedpminwk = nodexdatawk.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(showwknum, 0);
			              		value = 0 - cjedpminwk;
			              		break;
			              	}
			          } catch (java.lang.NullPointerException e) {  value = null;}
			          break;
		       case "CjlZbDpMaxWk":
		    	   Integer cjldpmaxwk = null;
				      try{
				    	  		cjldpmaxwk = nodexdatawk.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(showwknum,0);
				          		if(cjldpmaxwk > 0) {
				              		value = cjldpmaxwk;
				              		break;
				              	} else	if(cjldpmaxwk == 0) {
				              		Integer cjldpminwk = nodexdatawk.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(showwknum, 0);
				              		value = 0 - cjldpminwk;
				              		break;
				              	}
				       } catch (java.lang.NullPointerException e) {value = cjldpmaxwk;}
				       break;
		       case "NCjeZbDpMinWk":
		     	  Integer nCjeZbDpMinWk = nodexdatawk.getChenJiaoErZhanBiMinestWeekOfSuperBanKuaiInSpecificPeriod(showwknum,0,15);
		       	  value = nCjeZbDpMinWk;
		       	  break;
		       case "averagecjemaxwk" :
		    	   try {
		    		   Integer averagecjemaxwk = nodexdatawk.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(showwknum,0);
				       	  if(averagecjemaxwk != null && averagecjemaxwk > 0) 
				       		value = averagecjemaxwk;
				          else value = 0;
		    	   } catch (Exception e) { value = null;  }
		       	  break;
		       case "averagecjlmaxwk" :
		    	   Integer averagecjlmaxwk = null;
		    	   try {
		    		   averagecjlmaxwk = nodexdatawk.getAverageDailyChenJiaoLiangMaxWeekOfSuperBanKuai(showwknum,0);
				       	  if(averagecjlmaxwk != null && averagecjlmaxwk > 0) 
				       		value = averagecjlmaxwk;
				          else value = 0;
		    	   } catch (Exception e) {value = averagecjlmaxwk; }
		       	  break;
		       case "highlevelpanxurank" :
		     	  Integer paiming = this.entryList.indexOf(node) + 1;
		       	  value = paiming;
		     	  break;
		       case "quanzhonginbankuai" :
		     	  Integer stockweight =  ((BanKuai)curbk).getGeGuSuoShuBanKuaiWeight( node.getMyOwnCode() );
			        	try {
			        		value = (Integer)stockweight;
			        	} catch (java.lang.NullPointerException e) {value = null;}
		     	  break;
		       case "CjeLianXuZhang": //周日平均成交额MAXWK
			        	Integer cjemaxwk = nodexdatawk.getAverageDailyCjeLianXuFangLiangPeriodNumber(showwknum,0);//.getGgbkcjemaxweek(); 
			        	value = cjemaxwk;
			        	break;
		       case "huanshoulv": //周日平均成交额MAXWK
			        	Double hsl = ((StockNodesXPeriodData)nodexdatawk).getSpecificTimeHuanShouLv(showwknum, 0);
				    	value = hsl;
				    	break;
		       case "huanshoulvfree": //周日平均成交额MAXWK
		        	Double hslf = ((StockNodesXPeriodData)nodexdatawk).getSpecificTimeHuanShouLvFree(showwknum, 0);
			    	value = hslf;
			    	break;
		       case "chenjiaoer" :
			      	 Double curcje  = nodexdatawk.getChengJiaoEr(showwknum, 0) / 100000000;
			   	     value = curcje;
			   	     break;
		       case "chenjiaoliang" :
			      	 Double curcjl  = nodexdatawk.getChengJiaoLiang(showwknum, 0) / 100000000;
			   	     value = curcjl;
			   	     break;
		       case "dayujunxian" :
		    	   	String displayma = matchcond.getSettingMaFormula();
		    	   
		    	   	Boolean checkresult = nodexdataday.checkCloseComparingToMAFormula(displayma,showwknum,0);
				    if( checkresult != null && checkresult) 
				    	value = Boolean.valueOf(checkresult);
				    else if( checkresult != null && !checkresult)
				    	value = Boolean.valueOf(false);
				    
		    	   break;
		       case "zhangdiefu" :
			      	 Double zhangdiefu  = nodexdatawk.getSpecificOHLCZhangDieFu(showwknum, 0)  ;
			      	 if(zhangdiefu != null)
			      		 value = zhangdiefu;
			   	     break;
		  }
		  
		  return value;
	  }
	
	protected Object reformateDoubleValue (int columnIndex, Object value, String columnIndexForDecimal)
    {
		if(value == null)
			return null;
		
    	Class<?> columncl = this.getColumnClass (columnIndex);
    	if (  !columncl.equals(Double.class) ) 
    		return value;
    	
	    if(value.equals("??")) 
			return value;
	    	
    	String decimal = prop.getProperty (columnIndexForDecimal);
    	if(decimal == null)
    		return value;
    			
    	int decimalnumber = Integer.parseInt(decimal);
//    			BigDecimal roundOff = new BigDecimal( (Double)value).setScale(decimalnumber, BigDecimal.ROUND_HALF_EVEN);
//    			value = roundOff;
    			
    	double count = Math.pow(10, decimalnumber);
    	try {
    				Double output = Math.round(  (Double)value * count) / count;
    				value = output;
    	} catch ( java.lang.ClassCastException e) {	e.printStackTrace();return value;}
    			
    	return value;
    }
	
	  protected Class<?> getColomnValueType(String column_value_keyword, int columnIndex)
	  {
	  	  Class<?> clazz = String.class;
	  	  if(column_value_keyword != null)
	  		column_value_keyword = column_value_keyword.trim().toUpperCase();
	  	  else
	  		  return clazz;
		  
	  	  switch (column_value_keyword) {
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
	  
	  public Integer getKeyWrodColumnIndex (String keywords)
	  {
		  Integer columnIndex = null;
		  for(int i=0;i<=10;i++) {
			  String column_kw_i  = prop.getProperty (String.valueOf(i) + "column_info_keyword");
			  if(column_kw_i != null) 
				  if(column_kw_i.equalsIgnoreCase(keywords)) {
					  columnIndex = i;
					  break;
				  }
		  }
		  
		  return columnIndex;
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
