package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.StockNodeXPeriodData;
import com.exchangeinfomanager.nodes.treerelated.StockOfBanKuaiTreeRelated;


public class BanKuaiGeGuTableRenderer extends DefaultTableCellRenderer 
{

	public BanKuaiGeGuTableRenderer() 
	{
		super ();
	}
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(BanKuaiGeGuTableRenderer.class);

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col) 
	{
//		{ "����", "����","Ȩ��","��ͨ��ֵ����","���ɽ����","BkMaxWk","����ռ��������","DpMaxWk","CjeMaxWk","������"};
		
	    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

	    String valuepect = "";
	    if (comp instanceof JLabel && (col == 4 || col == 6)) { //�ðٷֱ���ʾ

	    	try {
        		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(value.toString()).doubleValue();
        		 
        		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
	    	     percentFormat.setMinimumFractionDigits(1);
            	 valuepect = percentFormat.format (formatevalue );
        	} catch (java.lang.NullPointerException e) {
        		valuepect = "";
	    	}catch (java.lang.NumberFormatException e)   	{
        		e.printStackTrace();
        	} catch (ParseException e) {
				e.printStackTrace();
			}
        	((JLabel)comp).setText(valuepect);
        }
	    
	    BanKuaiGeGuTableModel tablemodel =  (BanKuaiGeGuTableModel)table.getModel() ;
	    int modelRow = table.convertRowIndexToModel(row);
	    StockOfBanKuai stockofbank = ( (BanKuaiGeGuTableModel)table.getModel() ).getStock(modelRow);
	    Stock stock = stockofbank.getStock();
	    
	    if(stock.wetherHasReiewedToday()) {
        	Font defaultFont = this.getFont();
        	Font font = new Font(defaultFont.getName(), Font.BOLD + Font.ITALIC,defaultFont.getSize());
        	comp.setFont(font);
        }
	    
	    Color foreground = Color.BLACK, background = Color.white;

	    if( col == 1 ) { //��������
	    	LocalDate requireddate = tablemodel.getShowCurDate();
	 		StockOfBanKuaiTreeRelated stofbktree = (StockOfBanKuaiTreeRelated)stockofbank.getNodeTreeRelated();
    	
	 		Boolean isin = stofbktree.isInBanKuaiFengXiResultFileForSpecificDate(requireddate);
	    	if(isin != null && isin  ) 
		    		background = Color.ORANGE;  
	    	else
		    		background = Color.white;
	    	
	    	if(stockofbank.isBkLongTou())
	    		foreground = Color.RED;
	    	else 
	    		foreground = Color.BLACK;
	    } else if( col ==3) { //��ͨ��ֵ
	    	Double ltszmin = tablemodel.getDisplayLiuTongShiZhiMin() ;
		    Double ltszmax = tablemodel.getDisplayLiuTongShiZhiMax() ;
		    
		    LocalDate requireddate = tablemodel.getShowCurDate();
		    String period = tablemodel.getCurDisplayPeriod();
		    StockNodeXPeriodData nodexdata = (StockNodeXPeriodData)stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
		    Double curltsz = nodexdata.getSpecificTimeLiuTongShiZhi(requireddate, 0);
		    if( curltsz >= ltszmin && curltsz <= ltszmax ) 
		    	background = Color.MAGENTA ;
		    else
		    	background = Color.white;
	    	
	    }

	    //ͻ���ﵽ�û���׼�Ĺ�Ʊ
	    if( col == 4 && value != null ) { //�ɽ���>=
//	    	BanKuai bk = tablemodel.getCurDispalyBandKuai ();
		    Double cjemin = tablemodel.getDisplayChenJiaoErMin ();
		    Double cjemax = tablemodel.getDisplayChenJiaoErMax ();
		    
		    LocalDate requireddate = tablemodel.getShowCurDate();
		    String period = tablemodel.getCurDisplayPeriod();
		    NodeXPeriodDataBasic nodexdata = stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
		    Double curcje = nodexdata.getChengJiaoEr(requireddate, 0);
		    if( curcje >= cjemin && curcje <= cjemax ) 
		    	background = Color.yellow ;
		    else
		    	background = Color.white;
	    } else   if( col == 5  && value != null  ) { //ͻ����ʾbkMAXWK>=�ĸ���
	    	int bkmaxwk;
	    	try {
	    		 bkmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, 5).toString() );
	    	} catch (java.lang.NullPointerException e) {
	    		 bkmaxwk = -1;
	    	}
	    	
	    	int fazhi = tablemodel.getDisplayCjeBKMaxWk();
	    	 if(bkmaxwk >= fazhi )
	    		 background = Color.magenta ;
	    	 else 
	    		 background = Color.white ;
	    } else  if( col == 7   && value != null  ) { 	    //ͻ����ʾdpMAXWK>=�ĸ���
	    	int dpmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, 7).toString() );
	    	
	    	int fazhi = tablemodel.getDisplayCjeDPMaxWk();
	    	 if( dpmaxwk >=fazhi )
	    		 background = Color.red ;
	    	 else 
	    		 background = Color.white ;
	    }else  if( col == 8  && value != null ) { //ͻ����ʾCjeMAXWK>=�ĸ���
	    	int dpmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, 8).toString() );
	    	
	    	int fazhi = tablemodel.getDisplayCjeMaxWk();
	    	 if( dpmaxwk >=fazhi )
	    		 background = Color.CYAN ;
	    	 else 
	    		 background = Color.white ;
	    } else   if( col == 9 && value != null  ) {	    //ͻ��������
	    	double hsl = Double.parseDouble( tablemodel.getValueAt(modelRow, 9).toString() );
	    	
	    	double shouldhsl = tablemodel.getDisplayHuanShouLv();
	    	if(hsl >= shouldhsl)
	    		 background = Color.BLUE.brighter() ;
	    	 else 
	    		 background = Color.white ;
	    }
 
    	comp.setBackground(background);
    	comp.setForeground(foreground);
    	
	    if(table.isRowSelected(row) && col == 0 ) {
	    	comp.setBackground(Color.blue);
	    }
	    
	    return comp;
	}
	
}
