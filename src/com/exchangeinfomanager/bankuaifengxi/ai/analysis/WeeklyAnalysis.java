package com.exchangeinfomanager.bankuaifengxi.ai.analysis;

import java.awt.Color;
import java.time.LocalDate;

import javax.swing.JLabel;

import org.jfree.data.time.ohlc.OHLCItem;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.CandleStickColorFactory;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.TDXNodesXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.google.common.base.Strings;
import com.google.common.collect.Range;

public class WeeklyAnalysis 
{

	public static String BanKuaiGeGuMatchConditionAnalysis (TDXNodes node, LocalDate requireddate, BanKuaiGeGuMatchCondition matchcond)
	{
		String analysisresult = "";
		
		NodeXPeriodData nodexdata = node.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		
	    Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (requireddate,0);
 		NodesTreeRelated stofbktree = node.getNodeTreeRelated();
 		Boolean isin = stofbktree.selfIsMatchModel(requireddate);
 		String inouptfile = null;
    	if(isin != null && isin  ) {
    		inouptfile = "����" + requireddate.toString() +  "�����ļ��ڡ�";  
    	} else
    		inouptfile = "������" + requireddate.toString() +  "�����ļ��ڡ�";
    	
    	String ltszresult = null; 
	    	Double ltszmin ;
		    Double ltszmax ;
		    try {
		    	ltszmax = matchcond.getSettingLiuTongShiZhiMax().doubleValue() *  100000000;
		    } catch (Exception e) {
		    	ltszmax = null;
		    }
		    try {
		    	ltszmin = matchcond.getSettingLiuTongShiZhiMin() * 100000000;
		    } catch (Exception e) {
		    	ltszmin = null;
		    }
		    if(ltszmin !=null || ltszmax !=null) {
		    	Double curltsz = ((StockNodesXPeriodData)nodexdata).getSpecificTimeLiuTongShiZhi(requireddate, 0);
		    	if(curltsz == null) //��ʱ����һ���׵����ݻ�û�е��룬����û����ͨ��ֵ���ݣ�������һ�ܵ����ݶ�һ�£��Ͼ��������̫��
			    	curltsz = ((StockNodesXPeriodData)nodexdata).getSpecificTimeLiuTongShiZhi(requireddate, -1);
		    	
		    	if(ltszmin == null)
		    		ltszmin = -1.0;
		    	if(ltszmax ==null)
		    		ltszmax = 1000000000000.0; 
		    	if( Range.closed(ltszmin, ltszmax).contains(curltsz) )
		    		ltszresult = "��ͨ��ֵ=" + curltsz + "���ϱ�׼��";
		    }
		    
		    if(inouptfile != null)
		    	analysisresult = analysisresult + inouptfile;
		    if(ltszresult != null)
		    	analysisresult = analysisresult + ltszresult;
		    
		    
//		    try {
//			    if( curltsz >= ltszmin && curltsz <= ltszmax ) {
//			    	if(curltsz >= 1500000000.0 && curltsz <= 3900000000.0)  //���������Χ�ڵĸ���Ҫ�ر����ӣ��ر���ʾ
//			    		background = new Color(201,0,102) ;
//			    	else  
//			    		background = Color.MAGENTA ;
//			    }
//			    else
//			    	background = Color.white;
//		    } catch (java.lang.NullPointerException e) {
//		    	background = Color.white;
//		    }
//	    	
//	    
//	    if( columnname.contains("����CJEZB������")  ) { //�Ƿ�>= col == 4  �͹ɼ�����
//	    	Double zfmax = matchcond.getSettingZhangFuMax();
//	    	Double zfmin = matchcond.getSettingZhangFuMin();
//	    	
//	    	Double pricemin = matchcond.getSettingSotckPriceMin();
//	    	Double pricemax = matchcond.getSettingSotckPriceMax();
//	    	
//	    	if(zfmax == null && zfmin == null ) {
//	    		background = Color.white;
//	    	} else
//    		if(zfmax == null && zfmin != null )
//		    		zfmax = 1000000.0;
//	    	else 
//	    	if(zfmax != null && zfmin == null )
//	    		zfmin = -1000000.0;
//	    	else
//	    	if(zfmax == null && zfmin == null ) {
//	    		zfmin = 1000000.0;
//	    		zfmax = -1000000.0;
//	    	}
//    		if(pricemin == null && pricemax == null ) {
//	    		foreground = Color.BLACK;
//	    	} else
//    		if(pricemax == null && pricemin != null )
//    			pricemax = 1000000.0;
//	    	else 
//	    	if(pricemax != null && pricemin == null )
//	    		pricemin = -1000000.0;
//	    	else
//	    	if(pricemax == null && pricemin == null ) {
//	    		pricemin = 1000000.0;
//	    		pricemax = -1000000.0;
//	    	}
//
//		   	LocalDate requireddate = tablemodel.getShowCurDate();
//			String period = tablemodel.getCurDisplayPeriod();
//			StockXPeriodDataForJFC nodexdata = (StockXPeriodDataForJFC)stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
//			OHLCItem ohlcdata = ((TDXNodesXPeriodDataForJFC)nodexdata).getSpecificDateOHLCData (requireddate,0);
//		    Double close = ohlcdata.getCloseValue();
//			Double wkzhangfu = nodexdata.getSpecificTimeHighestZhangDieFu(requireddate, 0);
//			
//			if(zfmax != null || zfmin != null ) {
//				if(wkzhangfu == null)
//			    	background = Color.white;
//			    else if( wkzhangfu >= zfmin && wkzhangfu <= zfmax ) 
//			    	background = Color.pink ;
//			    else
//			    	background = Color.white;
//			}
//			if(pricemin != null || pricemax != null ) {
//				if(close == null)
//					foreground = Color.black;
//				else 
//				if(close >= pricemin && close <= pricemax)
//					foreground = Color.blue;
//				else
//					foreground = Color.BLACK;
//			}
//
//	    }  else 
//	    if(columnname.contains("���ɽ����")   ) { //ͻ���ز�ȱ�� col == 3
//	    	background = Color.white ;
//		    Boolean hlqk = matchcond.hasHuiBuDownQueKou();
//		    if(hlqk ) {
//		    	LocalDate requireddate = tablemodel.getShowCurDate();
//			    String period = tablemodel.getCurDisplayPeriod();
//		    	NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
//		    	
//			    Integer hbqkdown = nodexdata.getQueKouTongJiHuiBuDown(requireddate, 0);
//			    Integer openupqk = nodexdata.getQueKouTongJiOpenUp(requireddate, 0);
//			    
//			    if( (hbqkdown != null && hbqkdown >0) ||  (openupqk != null && openupqk>0)  ) //ȱ��
//			    	 background = Color.PINK ;
//		    	else
//		    		background = Color.white ;
//		    }
//	    } else  
//	    if( columnname.contains("CJEDpMaxWk")    && value != null  ) { 	    //ͻ����ʾcjedpMAXWK>=�ĸ��� col == 5
//	    	int cjedpmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, col).toString() );
//	    	
//	    	if(cjedpmaxwk > 0 ) {
//	    		int maxfazhi;
//		    	try {
//		    		maxfazhi = matchcond.getSettingDpMaxWk();
//		    	} catch (java.lang.NullPointerException e) {
//		    		maxfazhi = 100000000;
//		    	}
//		    	
//	    		LocalDate requireddate = tablemodel.getShowCurDate();
//			    String period = tablemodel.getCurDisplayPeriod();
//			    NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
//		    	Integer lianxuflnum = nodexdata.getCjeDpMaxLianXuFangLiangPeriodNumber (requireddate,0, maxfazhi);
//		    	 
//		    	if(cjedpmaxwk >= maxfazhi &&  lianxuflnum >=2 ) //��������,��ɫ��ʾ
//		    		background = new Color(102,0,0) ;
//		    	else if( cjedpmaxwk >= maxfazhi &&  lianxuflnum <2 )
//		    		 background = new Color(255,0,0) ;
//		    	else 
//		    		 background = Color.white ;
//	    	} 
//	    	
//	    	if(cjedpmaxwk < 0 ) {
//	    		int minfazhi;
//		    	try {
//		    		minfazhi = matchcond.getSettingDpMinWk();
//		    	} catch (java.lang.NullPointerException e) {
//		    		minfazhi = 100000000;
//		    	}
//		    	
//	    		minfazhi = 0 - minfazhi; //min���ø�����ʾ
//	    		if(cjedpmaxwk <= minfazhi)
//	    			background = Color.GREEN ;
//	    		else
//	    			background = Color.white ;
//	    	} 
//	    } else  
//	    if( columnname.contains("��ƽ���ɽ���MAXWK")   && value != null  ) { //col == 7
//	    	int dpmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, col).toString() );
//	    	
//	    	Integer cjemaxwk = matchcond.getSettingChenJiaoErMaxWk();
//	    	if(cjemaxwk == null)
//	    		cjemaxwk =  10000000;
//	    	
//	    	if( dpmaxwk >=cjemaxwk )
//	    		 background = Color.CYAN ;
//	    	else 
//	    		 background = Color.white ;
//	    } else 
//	    if( columnname.contains("����CJLZB������")   && value != null) { //ͻ��MA,Ĭ��Ϊ���� col == 6
//	    	String displayma = matchcond.getSettingMaFormula();
//	    	background = Color.white ;
//	    	if (!Strings.isNullOrEmpty(displayma)) {
//	    		NodeXPeriodData nodexdataday = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
//			    
//			    LocalDate requireddate = tablemodel.getShowCurDate();
//			    Boolean checkresult = nodexdataday.checkCloseComparingToMAFormula(displayma,requireddate,0);
//			    if( checkresult != null && checkresult)
//				    background = new Color(0,153,153) ;
//	    	}
//	    		
//	    }
		    
		return analysisresult;

	}
}
