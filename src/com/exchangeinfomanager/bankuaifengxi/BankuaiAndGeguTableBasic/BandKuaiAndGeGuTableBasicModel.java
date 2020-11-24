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
	
	protected abstract void createTableTitleStrings();
	
	private static Logger logger = Logger.getLogger(BandKuaiAndGeGuTableBasicModel.class);
	
	protected String[] jtableTitleStrings ;
	protected BanKuai curbk;
	protected List<TDXNodes> entryList;
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
	    
	    
	    public List<TDXNodes> getAllNodes ()
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

	    public void removeAllRows ()
	    {
	    	if(entryList != null) {
	    		this.entryList.clear();
		    	this.fireTableDataChanged();
	    	}
	    }

	
	protected Object getColomnValue(String column_keyword, int rowIndex) 
	{
		  Object value = "??";
		  BkChanYeLianTreeNode node = entryList.get(rowIndex);
		  BkChanYeLianTreeNode datanode; BkChanYeLianTreeNode datasupernode;
		  DaPan dapan;
		  
		  NodeXPeriodData nodexdata = null;
		  if( node.getType() == BkChanYeLianTreeNode.BKGEGU ) {
			  Stock stock = ((StockOfBanKuai)node).getStock();
			  nodexdata = stock.getNodeXPeroidData(curperiod);
		  } else if (node.getType() == BkChanYeLianTreeNode.TDXBK) {
			  nodexdata = ((BanKuai) node).getNodeXPeroidData (curperiod);
			  dapan = (DaPan)node.getRoot();
		  }
		  
//		  StockOfBanKuai curdisplaystockofbankuai = (StockOfBanKuai) entryList.get(rowIndex);
////		  String bkcode = curbk.getMyOwnCode();
//		  Stock stock = curdisplaystockofbankuai.getStock();
////		    DaPan dapan = (DaPan)stock.getRoot();
//		  NodeXPeriodData stockxdata = stock.getNodeXPeroidData(period);
//		  
//		  BanKuai bankuai = null;
//	    	DaPan dapan = null;
//	    	try {
//	    		bankuai = (BanKuai) entryList.get( rowIndex );
//	    		dapan = (DaPan)bankuai.getRoot();
//	    	} catch (java.lang.IndexOutOfBoundsException e) {
//	    		e.printStackTrace();
//	    		return null;
//	    	}
//	    	NodeXPeriodData bkxdata = bankuai.getNodeXPeroidData(this.curperiod);
		    
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
		       case "chengjiaoerzhanbi":
		    	   Double zhanbi = nodexdata.getChenJiaoErZhanBi(showwknum, 0);
           		   value = zhanbi;
		    	   break;
		       case "CjeZbDpMaxWk":
		     	  Integer cjedpmaxwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek();
			          try{
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
		       case "NCjeZbDpMinWk":
		     	  Integer cjldpgrowthrate = nodexdata.getChenJiaoErZhanBiMinestWeekOfSuperBanKuaiInSpecificPeriod(showwknum,0,15);//.getGgdpzhanbigrowthrate();
		       	  value = cjldpgrowthrate;
		       	  break;
		       case "averagecjemaxwk" :
		     	  Integer cjldpmaxwk = nodexdata.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek();
		       	  if(cjldpmaxwk != null && cjldpmaxwk > 0) 
		       		value = cjldpmaxwk;
		          else value = 0;
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

		
}
