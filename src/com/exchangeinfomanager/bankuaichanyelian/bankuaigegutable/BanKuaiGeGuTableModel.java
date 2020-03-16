package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;

import com.exchangeinfomanager.nodes.DaPan;

import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;


public class BanKuaiGeGuTableModel extends BanKuaiGeGuBasicTableModel 
{
	BanKuaiGeGuTableModel () 
	{
		super ();
		
		String[] jtableTitleStrings = { "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","周平均成交额MAXWK"};
		
		super.setTableHeader(jtableTitleStrings);
		
//		this.setDisplayChenJiaoEr(null, null);
//		this.setDisplayCjeZhanBiDPMaxMinWk(null,null);
//		this.setDisplayHuanShouLv(null);
//		this.setDisplayCjeMaxWk(null);
//		this.setDisplayLiuTongShiZhi(null, null);
//		this.setHighLightHuiBuDownQueKou(false);
	}

	private static Logger logger = Logger.getLogger(BanKuaiGeGuTableModel.class);
	private BanKuaiGeGuMatchCondition condition;

	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
//	    	logger.debug(rowIndex + "col" + columnIndex  + "  ");
	    	if(entryList.isEmpty())
	    		return null;

	    	StockOfBanKuai curdisplaystockofbankuai = (StockOfBanKuai) entryList.get(rowIndex);
		    String stockcode = curdisplaystockofbankuai.getMyOwnCode();
		    String bkcode = curbk.getMyOwnCode();
		    NodeXPeriodData stockxdataforbk = curdisplaystockofbankuai.getNodeXPeroidData(period);

		    Stock stock = curdisplaystockofbankuai.getStock();
		    DaPan dapan = (DaPan)stock.getRoot();
		    NodeXPeriodData stockxdata = stock.getNodeXPeroidData(period);

		    Object value = "??";
	    	switch (columnIndex) {
            case 0: //{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
            	bkcode = curdisplaystockofbankuai.getMyOwnCode();
                value = bkcode;
                
//                bkcode = null;
                break;
            case 1: 
            	String thisbkname = curdisplaystockofbankuai.getMyOwnName();
            	value = thisbkname;
            	
//            	thisbkname = null;
            	
            	break;
            case 2://{ "代码", "名称","权重","流通市值排名",
            	Integer paiming = this.entryList.indexOf(curdisplaystockofbankuai) + 1;
            	value = paiming;
            	
//            	zhanbigrowthrate = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
            	break;
            case 3: // "板块成交额贡献",
            	Double cjechangegrowthrate = stockxdata.getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage(this.curbk,showwknum,0);// fxrecord.getGgbkcjegrowthzhanbi();
            	if(cjechangegrowthrate != null)
            		value = cjechangegrowthrate;
            	else
            		value = -1;
            	
//            	cjechangegrowthrate = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
            	break;
            case 4://{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
            	Double cjedpgrowthrate = stockxdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(showwknum,0);//.getGgdpzhanbigrowthrate();
            	if(cjedpgrowthrate != null)
            		value = cjedpgrowthrate;
            	else
            		value = -1;
//            	cjedpgrowthrate = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
                break;
            case 5:
            	Integer cjedpmaxwk = stockxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek();
            	try{
            		if(cjedpmaxwk > 0) {
                		value = cjedpmaxwk;
                		break;
                	} else	if(cjedpmaxwk == 0) {
                		Integer cjedpminwk = stockxdata.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(showwknum, 0);
                		value = 0 - cjedpminwk;
                		break;
                	}
            	} catch (java.lang.NullPointerException e) {
            		e.printStackTrace();
            		logger.info("");
            	}
//            	dpmaxwk = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
                break;
            case 6://{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
            	Double cjldpgrowthrate = stockxdata.getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(showwknum,0);//.getGgdpzhanbigrowthrate();
            	value = cjldpgrowthrate;
            	
//            	cjedpgrowthrate = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
                break;
            case 7:
            	Integer cjldpmaxwk = stockxdata.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek();
            	if(cjldpmaxwk != null && cjldpmaxwk > 0) {
            		value = cjldpmaxwk;
            	} else	
            		value = 0;
            	
//            	dpmaxwk = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
                break;
	    	}
	    	
	    	return value;
	  }
	  
	  /*
	   * (non-Javadoc)
	   * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	   * //for sorting http://www.codejava.net/java-se/swing/6-techniques-for-sorting-jtable-you-should-know
	   */
      public Class<?> getColumnClass(int columnIndex) { 
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0: //{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
		    	  clazz = String.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = Integer.class;
			          break;
		        case 3:
			          clazz = Double.class;
			          break;
		        case 4:
			          clazz = Double.class;
			          break;
		        case 5:
		        	  clazz = Integer.class;
			          break;
		        case 6:
			          clazz = Double.class;
			          break;
		        case 7:
			          clazz = Integer.class;
			          break;
		      }
		      
		      return clazz;
	 }
      
    public void setDisplayMatchCondition (BanKuaiGeGuMatchCondition cond)
    {
    	  this.condition = cond;
    }
    public BanKuaiGeGuMatchCondition getDisplayMatchCondition ()
    {
    	return this.condition;
    }
	    
		

}
