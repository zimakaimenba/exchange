package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.HanYuPinYing;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuai.BanKuaiNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi.ExportCondition;

public class BanKuaiInfoTableModel extends DefaultTableModel 
{

	public BanKuaiInfoTableModel() {
		super ();
	}
	
	String[] jtableTitleStrings = { "板块代码", "板块名称","占比增长率","占比MaxWk","成交额增长贡献率","CjeMaxWk","条件统计"};
//	HashMap<String,BanKuai> bkmap;
	List<BanKuai> entryList;
	LocalDate showzhbiwknum;
	private String curperiod;
	private ArrayList<ExportCondition> initialzedcon;
	private static Logger logger = Logger.getLogger(BanKuaiInfoTableModel.class);
	
	public void refresh  (LocalDate curselectdate,String period, ArrayList<ExportCondition> initializeconditon1)
	{
		this.showzhbiwknum = curselectdate;
		this.curperiod = period;
		this.initialzedcon = initializeconditon1;

		this.fireTableDataChanged();
	}
	public void addBanKuai (BanKuai bankuai)
	{
		if(entryList == null)
			entryList = new ArrayList<BanKuai> ();
		else
			entryList.add(bankuai);
	}
	
	 public int getRowCount() 
	 {
		 if(entryList == null)
			 return 0;
		 else
			 return entryList.size();
	 }
	 public void deleteAllRows ()
	 {
		 if(this.entryList != null && this.entryList.size() >0)
			 this.entryList.clear();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(entryList.isEmpty() )
	    		return null;
	    	
	    	BanKuai bankuai = null;
	    	try {
	    		bankuai = entryList.get( rowIndex );
	    	} catch (java.lang.IndexOutOfBoundsException e) {
	    		e.printStackTrace();
	    		return null;
	    	}
	    	NodeXPeriodDataBasic bkxdata = (BanKuaiNodeXPeriodData)bankuai.getNodeXPeroidData(this.curperiod);
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
            	String bkcode = bankuai.getMyOwnCode();
                value = bkcode;
                break;
            case 1:
            	String thisbkname = bankuai.getMyOwnName();
            	value = thisbkname;
            	break;
            case 2: //"板块代码", "板块名称","占比增长率","MaxWeek","成交额增长贡献率"
            	Double zhanbigrowthrate = bkxdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(showzhbiwknum,0);// fxjg.getGgBkCjeZhanbiGrowthRate();
            	value = zhanbigrowthrate;
            	
            	zhanbigrowthrate = null;
            	bkxdata = null;
            	bankuai = null;
            			
            	break;
            case 3:
            	Integer maxweek = bkxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(showzhbiwknum,0);// fxjg.getGgBkCjeZhanbiMaxweek();
            	value = maxweek;
            	
            	maxweek = null;
            	bkxdata = null;
            	bankuai = null;
            	
            	break;
            case 4:
            	Double cjegrowthrate = bkxdata.getChenJiaoErChangeGrowthRateOfSuperBanKuai(showzhbiwknum,0);// fxjg.getGgBkCjeGrowthRateToSuperBanKuaiCjeGrowth();
            	value = cjegrowthrate;
            	
            	cjegrowthrate = null;
            	bkxdata = null;
            	bankuai = null;
            	
            	break;
            case 5:
            	Integer cjemaxwk = bkxdata.getChenJiaoErMaxWeekOfSuperBanKuai(showzhbiwknum,0);// fxjg.getGgBkCjeGrowthRateToSuperBanKuaiCjeGrowth();
            	value = cjemaxwk;
            	
            	cjemaxwk = null;
            	bkxdata = null;
            	bankuai = null;
            	
            	break;
            case 6: 
            	value = "";
            	break;
	    	}

	    	return value;
	    
	  }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = String.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = Double.class;
			          break;
		        case 3:
			          clazz = Integer.class;
			          break;
		        case 4:
			          clazz = Double.class;
			          break;
		        case 5:
			          clazz = Integer.class;
			          break;
		        case 6:
		        	clazz = String.class;
			          break;		
		      }
		      
		      return clazz;
	 }
	    
	    public String getColumnName(int column) { 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    public String getBanKuaiCode (int row) 
	    {
	    	return (String)this.getValueAt(row,0);
	    }
	    public String getBanKuaiName (int row) 
	    {
	    	return (String)this.getValueAt(row,1);
	    } 
	    public BanKuai getBanKuai (int row)
	    {
	    	return this.entryList.get(row);
	    }

	    public int getBanKuaiRowIndex (String neededfindstring) 
	    {
	    		int index = -1;
	    		HanYuPinYing hypy = new HanYuPinYing ();
	    		
	    		for(int i=0;i<this.getRowCount();i++) {
	    			String bkcode = (String)this.getValueAt(i, 0);
	    			String bkname = (String)this.getValueAt(i,1); 
	    			if(bkcode.trim().equals(neededfindstring) ) {
	    				index = i;
	    				break;
	    			}

	    			String namehypy = hypy.getBanKuaiNameOfPinYin(bkname );
			   		if(namehypy.toLowerCase().equals(neededfindstring.trim().toLowerCase())) {
			   			index = i;
			   			break;
			   		}
	    		}
	    	hypy = null;
	   		return index;
	    }



		public String[] getTableHeader() 
		{
			return this.jtableTitleStrings;
		}
		
//		private void calculateMatchConditionStockNum (int rowindex)
//		{
//			Integer matchednum =0;
//			for (ExportCondition expcon : initialzedcon) {
//				BanKuai childnode = entryList.get(rowindex);
//				
//				if(childnode.getType() != BanKuaiAndStockBasic.TDXBK)
//					continue;
//				if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
//				 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //有些指数是没有个股和成交量的，不列入比较范围 
//				 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //仅导出有个股的板块
//					continue;
//				
//				Double settingcje = expcon.getSettingcje() ;
//				Integer settindpgmaxwk = expcon.getSettindpgmaxwk();
//				Integer settinbkgmaxwk = expcon.getSettinbkgmaxwk();
//				Integer seetingcjemaxwk = expcon.getSettingcjemaxwk();
//				
//				NodeXPeriodDataBasic nodexdata = childnode.getNodeXPeroidData(curperiod);
//				if(nodexdata.hasRecordInThePeriod(showzhbiwknum,0) ) { //板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
//					childnode = bkcyl.getAllGeGuOfBanKuai (((BanKuai)childnode),curperiod);
//					ArrayList<StockOfBanKuai> rowbkallgg = ((BanKuai)childnode).getSpecificPeriodBanKuaiGeGu(showzhbiwknum,curperiod);
//					for (StockOfBanKuai stockofbankuai : rowbkallgg) {
//						
//					}
//				}
//				
//				
//				
//			}
//			
//			for(int i=0;i< bankuaicount; i++) {
//		
//				
//				BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
//				
//				
//				NodeXPeriodDataBasic nodexdata = childnode.getNodeXPeroidData(period);
//				if(nodexdata.hasRecordInThePeriod(selectiondate,0) ) { //板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
//					
//					for (StockOfBanKuai stockofbankuai : rowbkallgg) {
//						 if (isCancelled()) 
//							 return null;
//
//						 NodeXPeriodDataBasic stockxdataforbk = stockofbankuai.getNodeXPeroidData( period);
//						 if(!stockxdataforbk.hasRecordInThePeriod(selectiondate, 0))
//							 continue;
//						 
//						 Double recordcje = stockxdataforbk.getChengJiaoEr(selectiondate, 0);
//						 Integer recordmaxbkwk = stockxdataforbk.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate) ;
//						 
//						 Stock ggstock = stockofbankuai.getStock();
//						 NodeXPeriodDataBasic stockxdata = ggstock.getNodeXPeroidData(globeperiod);
//						 Integer recordmaxdpwk = stockxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate);
//						 Integer recordmaxcjewk = stockxdata.getChenJiaoErMaxWeekOfSuperBanKuai(selectiondate);
//						 
//						 if(recordcje >= settingcje &&  recordmaxbkwk >= settinbkgmaxwk
//									 && recordmaxcjewk >= seetingcjemaxwk && recordmaxdpwk >=  settindpgmaxwk)  { //满足条件，导出 ; 
//							 if(!outputnodeslist.contains(childnode)){
//									outputnodeslist.add(childnode);
//							 }
//							 
////							 if(ggstock.getMyOwnCode().startsWith("60") )
////									   outputresultset.add("1" + ggstock.getMyOwnCode().trim());
////							  else
////									   outputresultset.add("0" + ggstock.getMyOwnCode().trim());
//								   
//							  logger.debug(childnode.getMyOwnCode() + "个股：" + ggstock.getMyOwnCode() + "满足导出条件(" + settindpgmaxwk + "," + settinbkgmaxwk + settingcje + ","  + seetingcjemaxwk +  "), "
//								   		+ "数据("  + recordmaxdpwk + "," +  recordmaxbkwk  + "," + recordcje + "," + recordmaxcjewk + ")")
//										   ;   
//						 } 
//					}
//				}
//			}
//		}


}
