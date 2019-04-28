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
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.StockNodeXPeriodData;
import com.exchangeinfomanager.nodes.nodexdata.StockOfBanKuaiNodeXPeriodData;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class BanKuaiGeGuTableModel extends DefaultTableModel 
{
	BanKuaiGeGuTableModel () {
		super ();
	}
	
	private String[] jtableTitleStrings = { "����", "����","Ȩ��","�߼���������","���̳ɽ����","BkMaxWk","����ռ��������","DpMaxWk","CjeMaxWk","������"};
	private BanKuai curbk;
	private ArrayList<StockOfBanKuai> entryList;
	private LocalDate showwknum;

	private Double showcjemin ;
	private Double showcjemax ;
	private Integer cjemaxwk ;

	private Integer cjezbdpmaxwk ;
	private Integer cjezbbkmaxwk ;
	private String period;
	private Double huanshoulv ;
	private Double showltszmin;
	private Double showltszmax;
	private static Logger logger = Logger.getLogger(BanKuaiGeGuTableModel.class);
//	private ExportCondition expcond;
	private Boolean showhuibudownquekou;
	private Integer cjezbdpminwk;

	public void refresh (BanKuai bankuai,LocalDate wknum,String period)
	{
		this.curbk = bankuai;
		this.showwknum = wknum;
		this.period = period;
		
		this.setDisplayChenJiaoEr(null, null);
		this.setDisplayCjeBKMaxWk(null);
		this.setDisplayCjeZhanBiDPMaxMinWk(null,null);
		this.setDisplayHuanShouLv(null);
		this.setDisplayCjeMaxWk(null);
		this.setDisplayLiuTongShiZhi(null, null);
		this.setHighLightHuiBuDownQueKou(false);
		
		entryList = null;
		entryList = new ArrayList<StockOfBanKuai>( bankuai.getSpecificPeriodBanKuaiGeGu(wknum,0,period) );
		
		 Iterator itr = entryList.iterator(); 
	     while (itr.hasNext())      { //��ͣ�Ƶ��޳������������
	    	 StockOfBanKuai sob = (StockOfBanKuai) itr.next();
	    	 Stock stock = sob.getStock();
			 NodeXPeriodDataBasic stockxdata = stock.getNodeXPeroidData(period);
	    	 
	    	 Double cje = ((StockNodeXPeriodData)stockxdata).getChengJiaoEr(showwknum, 0);
	    	 if(cje == null)
	    		 itr.remove();
	     } 
		
		sortTableByChenJiaoEr ();
    	
    	this.fireTableDataChanged();
	}
	/*
	 * 
	 */
	public void sortTableByLiuTongShiZhi ()
	{
		try{
			Collections.sort(entryList, new NodeLiuTongShiZhiComparator(showwknum,0,period) );
		} catch (java.lang.NullPointerException e) {
//			e.printStackTrace();
		}
		
		this.fireTableDataChanged();
	}
	public void sortTableByZongShiZhi ()
	{
		
	}
	public void sortTableByChenJiaoEr ()
	{
		try{
			Collections.sort(entryList, new NodeChenJiaoErComparator(showwknum,0,period) );
			this.fireTableDataChanged();
		} catch (java.lang.NullPointerException e) {
			e.printStackTrace();
			logger.debug("��λ�գ����������");
		}
		
	}
	/*
	 * 
	 */
	public String[] getTableHeader ()
	{
		return this.jtableTitleStrings;
	}
	/*
	 * 
	 */
	public BanKuai getCurDispalyBandKuai ()
	{
		return this.curbk;
	}
	/*
	 * 
	 */
	public int getStockCurWeight(int rowIndex) 
	{
		int weight = (Integer)this.getValueAt(rowIndex, 2);
		return weight;
	}
	/*
	   * 
	   */
	  public void setStockCurWeight(int row, int newweight) 
	  {
		  if(entryList.isEmpty())
	    		return ;
		  
		  StockOfBanKuai stock = entryList.get(row);
		  String stockcode = stock.getMyOwnCode();
	      curbk.setGeGuSuoShuBanKuaiWeight(stockcode,newweight);
		  this.fireTableDataChanged();
	  }
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
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
//	    	logger.debug(rowIndex + "col" + columnIndex  + "  ");
	    	if(entryList.isEmpty())
	    		return null;

	    	StockOfBanKuai curdisplaystockofbankuai = entryList.get(rowIndex);
		    String stockcode = curdisplaystockofbankuai.getMyOwnCode();
		    String bkcode = curbk.getMyOwnCode();
		    NodeXPeriodDataBasic stockxdataforbk = curdisplaystockofbankuai.getNodeXPeroidData(period);

		    Stock stock = curdisplaystockofbankuai.getStock();
		    DaPan dapan = (DaPan)stock.getRoot();
		    NodeXPeriodDataBasic stockxdata = stock.getNodeXPeroidData(period);

		    Object value = "??";
	    	switch (columnIndex) {
            case 0:
            	bkcode = curdisplaystockofbankuai.getMyOwnCode();
                value = bkcode;
                
//                bkcode = null;
                break;
            case 1: 
            	String thisbkname = curdisplaystockofbankuai.getMyOwnName();
            	value = thisbkname;
            	
//            	thisbkname = null;
            	
            	break;
            case 2: //Ȩ��
            	Integer stockweight =  curbk.getGeGuSuoShuBanKuaiWeight(stockcode);
            	try {
            		value = (Integer)stockweight;
            	} catch (java.lang.NullPointerException e) {
            		value = 0;
            	}
            	
//            	stockweight = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
            	break;
            case 3://{ "����", "����","Ȩ��","��ͨ��ֵ����",
            	Integer paiming = this.entryList.indexOf(curdisplaystockofbankuai) + 1;
            	value = paiming;
            	
//            	zhanbigrowthrate = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
            	break;
            case 4: // "���̳ɽ����",
            	Double cjechangegrowthrate = stockxdata.getChenJiaoErChangeGrowthRateOfSuperBanKuai(dapan,showwknum,0);// fxrecord.getGgbkcjegrowthzhanbi();
            	value = cjechangegrowthrate;
            	
//            	cjechangegrowthrate = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
            	break;
            case 5: //  "BkMaxWk","����ռ��������","DpMaxWk","CjeMaxWk","������"};
            	Integer bkmaxweek =  stockxdataforbk.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(showwknum,0);//fxrecord.getGgbkzhanbigrowthrate(); 
            	value = bkmaxweek;
            	
//            	maxweek = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
            	break;
            case 6://"����ռ��������","DpMaxWk","CjeMaxWk"};
            	Double cjedpgrowthrate = stockxdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(showwknum,0);//.getGgdpzhanbigrowthrate();
            	value = cjedpgrowthrate;
            	
//            	cjedpgrowthrate = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
                break;
            case 7:
            	Integer dpmaxwk = stockxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(showwknum,0);//.getGgdpzhanbimaxweek();
            	if(dpmaxwk > 0) {
            		value = dpmaxwk;
            		break;
            	} else	if(dpmaxwk == 0) {
            		Integer dpminwk = stockxdata.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(showwknum, 0);
            		value = 0 - dpminwk;
            		break;
            	}
            	
            	
//            	dpmaxwk = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
                break;
           case 8:
            	Integer cjemaxwk = stockxdata.getChenJiaoErMaxWeekOfSuperBanKuai(showwknum,0);//.getGgbkcjemaxweek(); 
            	value = cjemaxwk;
            	
//            	cjemaxwk = null;
//            	curdisplaystockofbankuai = null;
//            	stockxdataforbk = null;
//            	stock = null;
//            	stockxdata = null;
            	
                break;
           case 9: 
        	   Double hsl = ((StockNodeXPeriodData)stockxdata).getSpecificTimeHuanShouLv(showwknum, 0);
        	   value = hsl;

        	   break;
	    	}
	    	
	    	return value;
	  }
	  
	  /*
	   * (non-Javadoc)
	   * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	   * //for sorting http://www.codejava.net/java-se/swing/6-techniques-for-sorting-jtable-you-should-know
	   */
	    //{ "����", "����","Ȩ��","��ͨ��ֵ����","���ɽ����","BkMaxWk","����ռ��������","DpMaxWk","CjeMaxWk","������"};
      public Class<?> getColumnClass(int columnIndex) { //{ "����", "����","Ȩ��","ռ��������","MAX","�ɽ����"};
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = String.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = String.class;
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
		        	  clazz = Double.class;
			          break;
		        case 7:
			          clazz = Integer.class;
			          break;
		        case 8:
			          clazz = Integer.class;
			          break;
		        case 9:
			          clazz = Double.class;
			          break;    
		      }
		      
		      return clazz;
	 }
	    
	    public String getColumnName(int column) { 
	    	return jtableTitleStrings[column];
	    }//���ñ������ 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    public String getStockCode (int row) 
	    {
	    	return (String)this.getValueAt(row,0);
	    }
	    public String getStockName (int row) 
	    {
	    	return (String)this.getValueAt(row,1);
	    } 
	    public String getStockWeight (int row) 
	    {
	    	return (String)this.getValueAt(row,2);
	    } 
	    public StockOfBanKuai getStock (int row)
	    {
	    	return this.entryList.get(row);
	    }
	    public void deleteAllRows ()
	    {	
	    	if(this.entryList == null)
				 return ;
			 else 
				 entryList.clear();
	    	this.fireTableDataChanged();
	    }
	    public int getStockRowIndex (String neededfindstring) 
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
	    			String namehypy = hypy.getBanKuaiNameOfPinYin(stockname );
			   		if(namehypy.toLowerCase().equals(neededfindstring.trim().toLowerCase())) {
			   			index = i;
			   			break;
			   		}
	    		}
	   		
	   		return index;
	    }

	    public void removeAllRows ()
	    {
	    	if(entryList != null) {
	    		this.entryList.clear();
		    	this.fireTableDataChanged();
	    	}
	    }
		
		//����ͻ����ʾ�ɽ��ֵ
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
		//����bkMAXWK��ֵ
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
		//������ʾÿ�հ���ļ�
//		public void setShowParsedFile (Boolean onoff)
//		{
//			this.showparsedfile = onoff;
//		}
//		public Boolean showParsedFile ()
//		{
//			return this.showparsedfile ;
//		}
		//���óɽ���dpMAXWK��ֵ
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
		//����bkMAXWK��ֵ
		public void setDisplayCjeBKMaxWk (Integer bkmax)
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
		public LocalDate getShowCurDate ()
		{
			return this.showwknum;
		}
		public String getCurDisplayPeriod ()
		{
			return this.period;
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
		

}
/*
 * ����ͨ��ֵ�Ը�������
 */
class NodeLiuTongShiZhiComparator implements Comparator<StockOfBanKuai> {
	private String period;
	private LocalDate compareDate;
	private int difference;
	public NodeLiuTongShiZhiComparator (LocalDate compareDate, int difference, String period )
	{
		this.period = period;
		this.compareDate = compareDate;
		this.difference = difference;
	}
    public int compare(StockOfBanKuai node1, StockOfBanKuai node2) {
    	Stock stock1 = node1.getStock();
    	Stock stock2 = node2.getStock();
    	
        Double cje1 = ((StockNodeXPeriodData)stock1.getNodeXPeroidData( period)).getSpecificTimeLiuTongShiZhi(compareDate, difference) ;
        Double cje2 = ((StockNodeXPeriodData)stock2.getNodeXPeroidData( period)).getSpecificTimeLiuTongShiZhi(compareDate, difference);
        
        return cje2.compareTo(cje1);
    }
}

