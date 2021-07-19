package com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.HanYuPinYing;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.Nodes.StockOfBanKuai;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditions;
import com.google.common.base.Splitter;

public  abstract class BandKuaiAndGeGuTableBasicModel extends DefaultTableModel 
{
	protected Properties prop;

	public BandKuaiAndGeGuTableBasicModel (Properties prop) {
		super ();
		this.prop = prop;
		
		createTableTitleStrings ();
	}
	
	protected String[] jtableTitleStrings ;
	protected TDXNodes curbk;
	protected TDXNodes curnode;
	protected List<BkChanYeLianTreeNode> entryList;
	protected LocalDate showwknum;
	protected String curperiod;
	protected LocalDate[] timerangezhangfu = new LocalDate[2]; //区间日期，用于判断区间涨幅
	protected BanKuaiAndGeGuMatchingConditions highlightcond;
	
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this); //	https://stackoverflow.com/questions/4690892/passing-a-value-between-components/4691447#4691447
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	        pcs.addPropertyChangeListener(listener);
	}
	
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
		
		String columnmaxnumberstr = prop.getProperty ( "columnmaxnumber");
		int columnmaxnumber =0;
		try { columnmaxnumber = Integer.parseInt(columnmaxnumberstr);
		} catch (java.lang.NumberFormatException e) {	e.printStackTrace();	}
		String[] jtableTitleStrings = new String[columnmaxnumber];
		for(int i=0;i<columnmaxnumber;i++) {
			String column_name  = prop.getProperty (String.valueOf(i) + "column_name");
			if(column_name != null)	jtableTitleStrings[i] = column_name ;
			else	jtableTitleStrings[i] = "NULL";
		}

		this.setTableHeader(jtableTitleStrings);
	}
	/*
	 * 
	 */
	public void setTimeRangeZhangFuDates(LocalDate start, LocalDate end)	{
		this.timerangezhangfu[0] = start;
		this.timerangezhangfu[1] = end; 
	}
	/*
	 * 
	 */
	public LocalDate getCurDisplayedDate () {
		return this.showwknum;
	}
	/*
	 * 
	 */
	public String getCurDisplayPeriod ()	{
		return this.curperiod;
	}
	@Override
	public int getRowCount() 	{
		 if(entryList == null)	return 0;
		 else	return entryList.size();
	}

	    @Override
	    public int getColumnCount() 
	    {
	    	String columnmaxnumberstr = prop.getProperty ( "columnmaxnumber");
			int columnmaxnumber = Integer.parseInt(columnmaxnumberstr);
	        return columnmaxnumber;
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
	    	if(this.entryList == null)	 return ;
			else  entryList.clear();
	    	
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
	    			
	    			if(stockname == null) continue;
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
		  Object value = null;NodeXPeriodData nodexdatawk = null;
		  try {
			  nodexdatawk =  ((TDXNodes)node).getNodeXPeroidData(period);
		  } catch( java.lang.NullPointerException e) {
			  e.printStackTrace();
		  }
		  value = nodexdatawk.getNodeDataByKeyWord(keyword,showdate,maformula[0]);
		  if(value != null)		  return value;
			  
		  switch (keyword) {
			  case "CurrentBanKuai":
				  value = this.curbk.getMyOwnCode();
				  break;
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
		  			if (node.getType() == BkChanYeLianTreeNode.TDXBK || node.getType() == BkChanYeLianTreeNode.DZHBK) {
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
			     	  Double cjechangegrowthrate = nodexdatawk.getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage(this.curbk,showdate);// fxrecord.getGgbkcjegrowthzhanbi();
			     	  value = cjechangegrowthrate;
			     	 break;
		       case "BanKuaiChengJiaoLiangGongXian":
			     	  Double cjlchangegrowthrate = nodexdatawk.getChenJiaoLiangChangeGrowthRateOfSuperBanKuaiOnDailyAverage(this.curbk,showdate);// fxrecord.getGgbkcjegrowthzhanbi();
			     	  value = cjlchangegrowthrate;
			     	  break;	
		       case "HighLevelPaiXuRank" :
			    	   for(int i=0;i<this.entryList.size();i++) {
			    		   if(this.entryList.get(i).getMyOwnCode().equals(node.getMyOwnCode())) {
			    			   Integer paiming = i+1;
						       value = paiming;
						       break;
			    		   }
			    	   }
			    	   break;
		       case "NCjeZbDpMinWk":
			     	  Integer nCjeZbDpMinWk = nodexdatawk.getChenJiaoErZhanBiMinestWeekForDaPanInSpecificPeriod(showdate,15);
			       	  value = nCjeZbDpMinWk;
			       	 break;
		       case "QuanZhongInBanKuai" :
			     	  Integer stockweight =  ((BanKuai)curbk).getGeGuSuoShuBanKuaiWeight( node.getMyOwnCode() );
				        	try {
				        		value = (Integer)stockweight;
				        	} catch (java.lang.NullPointerException e) {value = null;}
				      break;
			  case "TimeRangeZhangFu":
				  	NodeXPeriodData nodexdataday = ((TDXNodes)node).getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
			    	Double nodetimerangezhangfu = nodexdataday.getSpecificTimeRangeOHLCHightestZhangFu (this.timerangezhangfu[0], this.timerangezhangfu[1] );
			    	value =  nodetimerangezhangfu;
				    break;
			  case "CoreZhiShu":
				  	if (node.getType() == BkChanYeLianTreeNode.TDXBK || node.getType() == BkChanYeLianTreeNode.DZHBK) 
		  				value = ((BanKuai)node).getNodeJiBenMian().isCoreZhiShu();
		  			else value = Boolean.FALSE;
		  }

		  return value;
	}
	protected Object getColomnValue(String column_keyword, int rowIndex) 
	{
		if(column_keyword == null)		return null;
		column_keyword = column_keyword.trim();

		BkChanYeLianTreeNode sendnode = null;
		if(this.curnode != null) { //显示个股所有板块数据的表
			sendnode = this.curnode;
			this.curbk = (TDXNodes) entryList.get(rowIndex);
		} else {
			BkChanYeLianTreeNode node = entryList.get(rowIndex);
			try {
				if( node.getType() == BkChanYeLianTreeNode.BKGEGU ) 
					sendnode = ((StockOfBanKuai)node).getStock();
				else if ( node.getType() == BkChanYeLianTreeNode.TDXBK || node.getType() == BkChanYeLianTreeNode.DZHBK ) 
					sendnode = node;
			} catch(java.lang.NullPointerException e) {
				e.printStackTrace();
			}
		}
		  		
		  Object value = null;
		  if(column_keyword.equalsIgnoreCase("CLOSEVSMA")) {
			  if(highlightcond == null) return null;
			  
			  String maformula = highlightcond.getSettingMaFormula();
			  value = getNodeValueByKeyWord(sendnode, column_keyword, this.showwknum, NodeGivenPeriodDataItem.DAY, maformula);
		  }
		  else 
			  value = getNodeValueByKeyWord(sendnode, column_keyword, this.showwknum, this.curperiod,"");
		  
		  return value;
	  }
	
	protected Object reformateDoubleValue (int columnIndex, Object value, String columnIndexForDecimal)
    {
		if(value == null)	return null;
		
    	Class<?> columncl = this.getColumnClass (columnIndex);
    	if (  !columncl.equals(Double.class) ) 
    		return value;
    	
    	String decimal = prop.getProperty (columnIndexForDecimal);
    	if(decimal == null)
    		return value;
    			
    	int decimalnumber = Integer.parseInt(decimal);
    	double count = Math.pow(10, decimalnumber);
    	try {		Double output = Math.round(  (Double)value * count) / count;
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
	  /*
	   * 
	   */
	  private Object getCellValueByKeywords (int rowIndex, int columnIndex,String kw)
	  {
		  if(kw == null)			  return null;
		  
		  Object value = null;
		  
		  if(kw.toUpperCase().contains("OTHERWISE")) {
			  List<String> kwlist = Splitter.on("OTHERWISE").omitEmptyStrings().splitToList(kw);
			  for(String modelkw : kwlist ) {
				  Object valueresult = getColomnValue (modelkw, rowIndex);
				  if(valueresult != null) {
					  value = valueresult;
					  break;
				  }
			  }
		  } else 
			  value = getColomnValue (kw, rowIndex);
		  
		  return value;
	  }
	  /*
	   * 
	   */
	  public String getColumnKeyWord (int columnIndex)
	  {
		  String column_kw  = prop.getProperty ( String.valueOf(columnIndex) + "column_info_keyword");
		  return column_kw;
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
	  /*
	   * 
	   */
	  public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(entryList.isEmpty() && this.curnode == null)
	    		return null;

	    	Object value = null;
			switch (columnIndex) {
	        case 0: //{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
	        	String column_kw0  = prop.getProperty ("0column_info_keyword");
//	        	value = getColomnValue (column_kw0, rowIndex);
	        	value = getCellValueByKeywords(rowIndex, columnIndex, column_kw0);
	        	value = reformateDoubleValue (columnIndex, value, "0column_info_value_decimal" );
	            break;
	            
	        case 1: 
	        	String column_kw1  = prop.getProperty ("1column_info_keyword");
//	        	value = getColomnValue (column_kw1, rowIndex);
	        	value = getCellValueByKeywords(rowIndex, columnIndex, column_kw1);
	        	value = reformateDoubleValue (columnIndex, value, "1column_info_value_decimal" );
	        	break;
	        	
	        case 2: // "板块成交额贡献",
	        	String column_kw2  = prop.getProperty ("2column_info_keyword");
//	        	value = getColomnValue (column_kw2, rowIndex);
	        	value = getCellValueByKeywords(rowIndex, columnIndex, column_kw2);
	        	value = reformateDoubleValue (columnIndex, value, "2column_info_value_decimal" );
	        	break;
	        	
	        case 3://{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
	        	String column_kw3  = prop.getProperty ("3column_info_keyword");
//	        	value = getColomnValue (column_kw3, rowIndex);
	        	value = getCellValueByKeywords(rowIndex, columnIndex, column_kw3);
	        	value = reformateDoubleValue (columnIndex, value, "3column_info_value_decimal" );
	        	break;            	
	            
	        case 4:
	        	String column_kw4  = prop.getProperty ("4column_info_keyword");
//	        	value = getColomnValue (column_kw4, rowIndex);
	        	value = getCellValueByKeywords(rowIndex, columnIndex, column_kw4);
	        	value = reformateDoubleValue (columnIndex, value, "4column_info_value_decimal" );
	            break;
	            
	        case 5://{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
	        	String column_kw5  = prop.getProperty ("5column_info_keyword");
//	        	value = getColomnValue (column_kw5, rowIndex);
	        	value = getCellValueByKeywords(rowIndex, columnIndex, column_kw5);
	        	value = reformateDoubleValue (columnIndex, value, "5column_info_value_decimal" );
	            break;
	        case 6:
	        	String column_kw6  = prop.getProperty ("6column_info_keyword");
//	        	value = getColomnValue (column_kw6, rowIndex);
	        	value = getCellValueByKeywords(rowIndex, columnIndex, column_kw6);
	        	value = reformateDoubleValue (columnIndex, value, "6column_info_value_decimal" );
	            break;
	        case 7:
	        	String column_kw7  = prop.getProperty ("7column_info_keyword");
//	        	value = getColomnValue (column_kw7, rowIndex);
	        	value = getCellValueByKeywords(rowIndex, columnIndex, column_kw7);
	        	value = reformateDoubleValue (columnIndex, value, "7column_info_value_decimal" );
	            break;
	        case 8:
	        	String column_kw8  = prop.getProperty ("8column_info_keyword");
//	        	value = getColomnValue (column_kw8, rowIndex);
	        	value = getCellValueByKeywords(rowIndex, columnIndex, column_kw8);
	        	value = reformateDoubleValue (columnIndex, value, "8column_info_value_decimal" );
	            break;
	        case 9:
	        	String column_kw9  = prop.getProperty ("9column_info_keyword");
//	        	value = getColomnValue (column_kw9, rowIndex);
	        	value = getCellValueByKeywords(rowIndex, columnIndex, column_kw9);
	        	value = reformateDoubleValue (columnIndex, value, "9column_info_value_decimal" );
	            break;
	        case 10:
	        	String column_kw10  = prop.getProperty ("10column_info_keyword");
//	        	value = getColomnValue (column_kw10, rowIndex);
	        	value = getCellValueByKeywords(rowIndex, columnIndex, column_kw10);
	        	value = reformateDoubleValue (columnIndex, value, "10column_info_value_decimal" );
	            break;
	        case 11:
	        	String column_kw11  = prop.getProperty ("11column_info_keyword");
//	        	value = getColomnValue (column_kw10, rowIndex);
	        	value = getCellValueByKeywords(rowIndex, columnIndex, column_kw11);
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
		        case 11:
		        	String column_kwt11  = prop.getProperty ("11column_info_valuetype");
		        	clazz = getColomnValueType (column_kwt11, columnIndex);
	               break;
		      }
		      
		      return clazz;
		 }

		public void setHighLightBanKuaiAndGeGuMatchingCondition(BanKuaiAndGeGuMatchingConditions expc)
		{
			this.highlightcond = expc;
		}
		public BanKuaiAndGeGuMatchingConditions getHighLightBanKuaiAndGeGuMatchingCondition()
		{
			return this.highlightcond;
		}
}
