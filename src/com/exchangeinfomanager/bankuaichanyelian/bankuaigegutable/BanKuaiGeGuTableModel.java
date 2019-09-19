package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.bankuaifengxi.ExportCondition;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.HanYuPinYing;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class BanKuaiGeGuTableModel extends BanKuaiGeGuBasicTableModel 
{
	BanKuaiGeGuTableModel () 
	{
		super ();
		
		String[] jtableTitleStrings = { "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
		
		super.setTableHeader(jtableTitleStrings);
		
		this.setDisplayChenJiaoEr(null, null);
		this.setDisplayCjeBKMaxWk(null);
		this.setDisplayCjeZhanBiDPMaxMinWk(null,null);
		this.setDisplayHuanShouLv(null);
		this.setDisplayCjeMaxWk(null);
		this.setDisplayLiuTongShiZhi(null, null);
		this.setHighLightHuiBuDownQueKou(false);
	}

	private static Logger logger = Logger.getLogger(BanKuaiGeGuTableModel.class);
	
	private Double showcjemin ;
	private Double showcjemax ;
	private Integer cjemaxwk ;
	private Integer cjezbdpmaxwk ;
	private Integer cjezbbkmaxwk ;
	private Double huanshoulv ;
	private Double showltszmin;
	private Double showltszmax;
	private Boolean showhuibudownquekou;
	private Integer cjezbdpminwk;

//	private Integer displayma;
	private String displaymaformula;

	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
//	    	logger.debug(rowIndex + "col" + columnIndex  + "  ");
	    	if(entryList.isEmpty())
	    		return null;

	    	StockOfBanKuai curdisplaystockofbankuai = entryList.get(rowIndex);
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
            	Double cjechangegrowthrate = stockxdata.getChenJiaoErChangeGrowthRateOfSuperBanKuai(this.curbk,showwknum,0);// fxrecord.getGgbkcjegrowthzhanbi();
            	value = cjechangegrowthrate;
            	
//            	cjechangegrowthrate = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
            	break;
            case 4://{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
            	Double cjedpgrowthrate = stockxdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(showwknum,0);//.getGgdpzhanbigrowthrate();
            	value = cjedpgrowthrate;
            	
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
            	Integer cjldpmaxwk = stockxdata.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek();
            	if(cjldpmaxwk > 0) {
            		value = cjldpmaxwk;
            		break;
            	} else	if(cjldpmaxwk == 0) {
            		Integer cjldpminwk = stockxdata.getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(showwknum, 0);
            		value = 0 - cjldpminwk;
            		break;
            	}
            	
            	
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
	    
		//设置突出显示成交额阀值
		public void setDisplayChenJiaoEr (Double cjemin, Double cjemax)
		{
			if(cjemin != null && cjemax == null) {
				this.showcjemin  = cjemin;
				this.showcjemax = 1000000000000.0;
			} else
			if(cjemax != null && cjemin == null) {
				this.showcjemin  = 0.0;
				this.showcjemax  = cjemax;
			} else
			if(cjemax != null && cjemin != null) {
				this.showcjemin  = cjemin;
				this.showcjemax  = cjemax;
			} else
			if(cjemin == null && cjemax == null) {
				this.showcjemin = 1000000000000.0;
				this.showcjemax = 1000000000000.0;
			}
		}
		public Double getDisplayChenJiaoErMin ()
		{
			return this.showcjemin ;
			
		}
		public Double getDisplayChenJiaoErMax ()
		{
			return this.showcjemax ;
		}
		//设置bkMAXWK阀值
		public void setDisplayCjeMaxWk (Integer cjemax)
		{
			if(cjemax != null)
				this.cjemaxwk = cjemax;
			else
				this.cjemaxwk =  10000000;
		}
		public Integer getDisplayCjeMaxWk ()
		{
			return this.cjemaxwk;
		}
		//设置显示每日板块文件
//		public void setShowParsedFile (Boolean onoff)
//		{
//			this.showparsedfile = onoff;
//		}
//		public Boolean showParsedFile ()
//		{
//			return this.showparsedfile ;
//		}
		//设置成交额dpMAXWK阀值
		public void setDisplayCjeZhanBiDPMaxMinWk (Integer bkmax, Integer bkmin)
		{
			if(bkmax != null)
				this.cjezbdpmaxwk = bkmax;
			else
				cjezbdpmaxwk = 10000000;
			
			if(bkmin != null)
				this.cjezbdpminwk = bkmin;
			else
				cjezbdpminwk = 10000000;
		}
		public Integer getDisplayCjeZhanBiDPMaxWk ()
		{
			return this.cjezbdpmaxwk;
		}
		public Integer getDisplayCjeZhanBiDPMinWk ()
		{
			return this.cjezbdpminwk;
		}
		//设置bkMAXWK阀值
		private void setDisplayCjeBKMaxWk (Integer bkmax)
		{
			if(bkmax != null)
				this.cjezbbkmaxwk = bkmax;
			else
				this.cjezbbkmaxwk = 10000000;
		}
		public Integer getDisplayCjeBKMaxWk ()
		{
			return this.cjezbbkmaxwk;
		}

		public void setDisplayHuanShouLv (Double hsl)
		{
			if(hsl != null)
				this.huanshoulv = hsl;
			else
				huanshoulv = 1000000.0;
		}
		public Double getDisplayHuanShouLv ()
		{
			return this.huanshoulv;
		}
		public void setDisplayLiuTongShiZhi(Double ltszmin, Double ltszmax)
		{
			if(ltszmin != null && ltszmax == null) {
				this.showltszmin  = ltszmin;
				this.showltszmax = 10000000000000.0;
			} else
			if(ltszmax != null && ltszmin == null) {
				this.showltszmin  = 0.0;
				this.showltszmax  = ltszmax;
			} else
			if(ltszmax != null && ltszmin != null) {
				this.showltszmin  = ltszmin;
				this.showltszmax  = ltszmax;
			} else
			if(ltszmin == null && ltszmax == null) {
				this.showltszmin = 10000000000000.0;
				this.showltszmax = 10000000000000.0;
			}
			
		}
		public Double getDisplayLiuTongShiZhiMin ()
		{
			if(this.showltszmin != null)
				return this.showltszmin ;
			else
				return 10000000000000.0;
		}
		public Double getDisplayLiuTongShiZhiMax ()
		{
			if(this.showltszmax != null)
				return this.showltszmax ;
			else
				return 10000000000000.0;
		}
		public void setHighLightHuiBuDownQueKou(Boolean showhuibudownquekou1) 
		{
			this.showhuibudownquekou = showhuibudownquekou1;
			
		}
		public Boolean shouldHighlightHuiBuDownQueKou ()
		{
			return this.showhuibudownquekou;
		}
		
		public void setDisplayMAFormula(String displayma)
		{
			this.displaymaformula = displayma;
			
		}
		public String getDisplayMAFormula ()
		{
			if(displaymaformula != null)
				return this.displaymaformula;
			else
				 return null;
		}
		

}
