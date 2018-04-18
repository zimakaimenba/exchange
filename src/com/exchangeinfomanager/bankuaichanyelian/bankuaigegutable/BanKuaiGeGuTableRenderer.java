package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai;

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
//		logger.debug("row" + row +"column" + col);
	    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

	    
	    if (comp instanceof JLabel && (col == 3 || col == 4 || col == 6)) { 

        	String valuepect = null;
        	try {
        		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(value.toString()).doubleValue();
        		 
        		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
	    	    	 percentFormat.setMinimumFractionDigits(1);
            	 valuepect = percentFormat.format (formatevalue );
        	} catch (java.lang.NumberFormatException e)   	{
        		e.printStackTrace();
        	} catch (ParseException e) {
				e.printStackTrace();
			}
        	((JLabel)comp).setText(valuepect);
        }
	    
	    BanKuaiGeGuTableModel tablemodel =  (BanKuaiGeGuTableModel)table.getModel() ;
	    
	    Color foreground, background = Color.white;
	    
	    //突出显示parse file
	    HashSet<String> parsefiel =  tablemodel.getStockInParseFile();
	    int modelRow = table.convertRowIndexToModel(row);
	    StockOfBanKuai stock = ( (BanKuaiGeGuTableModel)table.getModel() ).getStock(modelRow);
	    if(col == 0  ) {
	    	
	    }
	    if( col == 1 ) {
	    	if(!table.isRowSelected(row) && parsefiel != null && tablemodel.showParsedFile()  ) {
		    	String stockcode = stock.getMyOwnCode();
		    	
		    	if(parsefiel != null && parsefiel.contains(stockcode)  )
		    		background = Color.ORANGE  ;
		    	else
		    		background = Color.white;
		    } 
	    	else
	    		background = Color.white;
	    } 
	    //突出权重，目前没有特别设置
	    if(col == 2) {
	    	background = Color.white;
	    }
	    //突出显示成交额达到标准的股票
	    if( col == 3) {
	    	BanKuai bk = tablemodel.getCurDispalyBandKuai ();
		    Double cje = tablemodel.getDisplayChenJiaoEr ();
		    LocalDate requireddate = tablemodel.getShowCurDate();
		    String period = tablemodel.getCurDisplayPeriod();
		    NodeXPeriodDataBasic nodexdata = bk.getStockXPeriodDataForABanKuai(stock.getMyOwnCode(), period);
		    Double curcje = nodexdata.getChengJiaoEr(requireddate, 0);
		    if(cje != null && cje >0 && curcje > cje ) 
		    	background = Color.yellow ;
		    else
		    	background = Color.white;
	    } 
	    
	    //突出显示bkMAXWK>=4的个股
	    if(col == 5 || col == 4  ) {
	    	int bkmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, 5).toString() );
	    	
	    	
	    	int fazhi = tablemodel.getDisplayCjeBKMaxWk();
	    	 if(bkmaxwk >= fazhi )
	    		 background = Color.magenta ;
	    	 else 
	    		 background = Color.white ;
	    }
	    //突出显示dpMAXWK>=4的个股
	    if( col == 7 || col == 6 ) {
	    	int dpmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, 7).toString() );
	    	
	    	int fazhi = tablemodel.getDisplayCjeDPMaxWk();
	    	 if( dpmaxwk >=fazhi )
	    		 background = Color.red ;
	    	 else 
	    		 background = Color.white ;
	    }
	  //突出显示CjeMAXWK>=的个股
	    if( col ==  8 ) {
	    	int dpmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, 8).toString() );
	    	
	    	int fazhi = tablemodel.getDisplayCjeMaxWk();
	    	 if( dpmaxwk >=fazhi )
	    		 background = Color.CYAN ;
	    	 else 
	    		 background = Color.white ;
	    }
	    if( col == 9) {
	    	double hsl = Double.parseDouble( tablemodel.getValueAt(modelRow, 9).toString() );
	    	
	    	double shouldhsl = tablemodel.getDisplayHuanShouLv();
	    	if(hsl >= shouldhsl)
	    		 background = Color.BLUE.brighter() ;
	    	 else 
	    		 background = Color.white ;
	    }
	    
	    if (!table.isRowSelected(row)) 
	    	comp.setBackground(background);
	    
	    return comp;
	}
	
}
