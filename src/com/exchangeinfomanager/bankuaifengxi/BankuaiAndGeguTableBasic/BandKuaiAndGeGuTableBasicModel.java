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

public  abstract class BandKuaiAndGeGuTableBasicModel extends DefaultTableModel 
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
		if(prop == null)
			return;
		
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

	protected Object getNodeValueByKeyWord(BkChanYeLianTreeNode node, String keyword, LocalDate showdate, String period,String... maformula) 
	{
		  Object value = null;
		  
		  NodeXPeriodData nodexdatawk =  ((TDXNodes)node).getNodeXPeroidData(period);
		  value = nodexdatawk.getNodeDataByKeyWord(keyword,showdate,maformula[0]);
		  if(value != null)
			  return value;
			  
		  switch (keyword) {
			  case "Tags":
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
			  
		  		case "SuoShuBanKuai" :
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
		       case "NodeCode":
			     	  String stockcode = node.getMyOwnCode();
			     	  value = stockcode;
			     	  break;
		       case "NodeName":
			     	  String thisbkname = node.getMyOwnName();
			       	  value = thisbkname;
			       	   break;
		       case "BanKuaiChengJiaoErGongXian":
			     	  Double cjechangegrowthrate = nodexdatawk.getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage(this.curbk,showdate,0);// fxrecord.getGgbkcjegrowthzhanbi();
			     	  value = cjechangegrowthrate;
			     	 break;
		       case "BanKuaiChengJiaoLiangGongXian":
			     	  Double cjlchangegrowthrate = nodexdatawk.getChenJiaoLiangChangeGrowthRateOfSuperBanKuaiOnDailyAverage(this.curbk,showdate,0);// fxrecord.getGgbkcjegrowthzhanbi();
			     	  value = cjlchangegrowthrate;
			     	  break;	
		       case "highlevelpanxurank" :
			     	  Integer paiming = this.entryList.indexOf(node) + 1;
			       	  value = paiming;
			       	 break;  	  
		       case "NCjeZbDpMinWk":
			     	  Integer nCjeZbDpMinWk = nodexdatawk.getChenJiaoErZhanBiMinestWeekOfSuperBanKuaiInSpecificPeriod(showdate,0,15);
			       	  value = nCjeZbDpMinWk;
			       	 break;
		       case "quanzhonginbankuai" :
			     	  Integer stockweight =  ((BanKuai)curbk).getGeGuSuoShuBanKuaiWeight( node.getMyOwnCode() );
				        	try {
				        		value = (Integer)stockweight;
				        	} catch (java.lang.NullPointerException e) {value = null;}
				      break;
			  case "CjeLianXuZhang": //周日平均成交额MAXWK
				        	Integer cjemaxwk = nodexdatawk.getAverageDailyCjeLianXuFangLiangPeriodNumber(showdate,0); 
				        	value = cjemaxwk;
				        	break;     	  
		  }

		  return value;
	}
	protected Object getColomnValue(String column_keyword, int rowIndex) 
	{
		if(column_keyword == null)
			return null;
		 column_keyword = column_keyword.trim();
		
		  Object value = null;
		  
		  BkChanYeLianTreeNode node = entryList.get(rowIndex);
		  BkChanYeLianTreeNode sendnode = null;
		  if( node.getType() == BkChanYeLianTreeNode.BKGEGU ) 
			  sendnode = ((StockOfBanKuai)node).getStock();
		   else if ( node.getType() == BkChanYeLianTreeNode.TDXBK || node.getType() == BkChanYeLianTreeNode.DZHBK ) 
			  sendnode = node;
		
		  if(column_keyword.equals("CLOSEVSMA")) {
			  String maformula = matchcond.getSettingMaFormula();
			  value = getNodeValueByKeyWord(sendnode, column_keyword, this.showwknum, NodeGivenPeriodDataItem.DAY, maformula);
		  }
		  else 
			  value = getNodeValueByKeyWord(sendnode, column_keyword, this.showwknum, this.curperiod,"");
		  
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
