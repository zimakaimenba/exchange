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
		
	}
	
	private static final long serialVersionUID = 1L;
	private Boolean showparsedfile = true;
	private Boolean showcjeup;
	private Double showcje;
	private LocalDate requireddate;
	

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

	    HashSet<String> parsefiel =  ( (BanKuaiGeGuTableModel)table.getModel() ).getStockInParseFile();
	    Stock stock = ( (BanKuaiGeGuTableModel)table.getModel() ).getStock(row);
	    if(!table.isRowSelected(row) && parsefiel!= null && this.showparsedfile  ) {
	    	int modelRow = table.convertRowIndexToModel(row);
	    	String stockcode = (String)table.getValueAt(modelRow, 0);
	    	
	    	if(parsefiel != null && parsefiel.contains(stockcode) )
	    		comp.setForeground(Color.BLUE);
	    	else
	    		comp.setForeground(Color.BLACK);
	    }
	    
	    if(this.showcje >0 && stock.getSpecficChenJiaoErRecord(requireddate).getMyOwnChengJiaoEr() > this.showcje) 
	    	comp.setForeground(Color.BLUE);
	    else
	    		comp.setForeground(Color.BLACK);
	    
	    return comp;
	}
	
	public void setDisplayChenJiaoEr (Double cje)
	{
		this.showcje = cje;
	}
	public void setShowParsedFile (Boolean onoff)
	{
		this.showparsedfile = onoff;
	}
	public void setCurrentRecordDate (LocalDate curdate) 
	{
		this.requireddate = curdate;
	}
}
