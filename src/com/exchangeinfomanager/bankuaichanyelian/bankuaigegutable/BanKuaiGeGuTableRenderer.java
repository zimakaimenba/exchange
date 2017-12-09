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

import com.exchangeinfomanager.asinglestockinfo.Stock;

public class BanKuaiGeGuTableRenderer extends DefaultTableCellRenderer 
{

	public BanKuaiGeGuTableRenderer() 
	{
		super ();
	}
	
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col) 
	{

	    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

	    
	    if (comp instanceof JLabel && (col == 3 ||  col == 5)) {
//        	String value =  ((JLabel)comp).getText();
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
	    HashSet<String> parsefiel =  tablemodel.getStockInParseFile();
	    
	    int modelRow = table.convertRowIndexToModel(row);
	    Stock stock = ( (BanKuaiGeGuTableModel)table.getModel() ).getStock(modelRow);

	    if(col == 0 || col == 1 || col == 2) {
	    	if(!table.isRowSelected(row) && parsefiel != null && tablemodel.showParsedFile()  ) {
		    	String stockcode = stock.getMyOwnCode();
		    	
		    	if(parsefiel != null && parsefiel.contains(stockcode)  )
		    		comp.setForeground(Color.RED);
		    	else
		    		comp.setForeground(Color.BLACK);
		    } else
	    		comp.setForeground(Color.BLACK);
	    } 
	    
	    if(col == 3 || col == 4 || col == 5) {
		    Double cje = tablemodel.getDisplayChenJiaoEr ();
		    LocalDate requireddate = tablemodel.getShowCurDate();
		    Double curcje = stock.getSpecficChenJiaoErRecord(requireddate).getMyOwnChengJiaoEr();
		    if(cje != null && cje >0 && curcje > cje ) 
		    	comp.setForeground(Color.BLUE);
		    else
		    	comp.setForeground(Color.BLACK);
	    } 
	    	
	    
	    return comp;
	}
	
}
