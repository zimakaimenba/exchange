package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import java.util.Collection;
import java.util.HashSet;

import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;



import com.exchangeinfomanager.Tag.Tag;

import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BanKuaiAndGeguTableBasicRenderer;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BandKuaiAndGeGuTableBasicModel;

import com.exchangeinfomanager.nodes.BanKuai;

import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class BanKuaiGeGuBasicTableRenderer extends BanKuaiAndGeguTableBasicRenderer
{
//	protected BandKuaiAndGeGuTableBasicModel tablemodel;
//	protected Properties prop;
	
	protected BanKuaiGeGuBasicTableRenderer (Properties prop1) {
		super (prop1);
//		this.prop = prop1;
	}
	
	private Border outsidefortag = new MatteBorder(1, 0, 1, 0, Color.RED);
	private Border insidefortag = new EmptyBorder(0, 1, 0, 1);
	private Border highlightfortag = new CompoundBorder(outsidefortag, insidefortag);
	//
	private Border outsideforintersectionbk = new MatteBorder(1, 0, 1, 0, Color.BLUE.brighter());
	private Border insideforintersectionbk = new EmptyBorder(0, 1, 0, 1);
	private Border highlightforintersectionbk = new CompoundBorder(outsideforintersectionbk, insideforintersectionbk);

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col) 
	{
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
	    JComponent jc = (JComponent)comp;
	    
	    tablemodel =  (BandKuaiAndGeGuTableBasicModel)table.getModel() ;
   	    
	    int modelRow = table.convertRowIndexToModel(row);
	    StockOfBanKuai stockofbank = (StockOfBanKuai) ( (BanKuaiGeGuBasicTableModel)table.getModel() ).getNode(modelRow);
	    Stock stock = stockofbank.getStock();
	    
	    //对于临时个股表，可以突出显示出正表中该板块的所有个股
	    BanKuai interbk = ((BanKuaiGeGuBasicTableModel)tablemodel).getInterSetctionBanKuai();
	    if(interbk!= null) {
	    	if( interbk.getBanKuaiGeGu (stock.getMyOwnCode())  != null)
	    		jc.setBorder( highlightforintersectionbk );
	    }
	    //
	    Collection<Tag> keywordsset = ((BanKuaiGeGuBasicTableModel)tablemodel).getCurrentHighlightKeyWords ();
	    if(keywordsset != null) {
	    	Collection<Tag> curtags = stock.getNodeTags ();
		    SetView<Tag> intersection = Sets.intersection((HashSet<Tag>)keywordsset, (HashSet<Tag>)curtags);
		    if( !intersection.isEmpty() && intersection.size() == keywordsset.size() ) {
		    	jc.setBorder( highlightfortag );
		    }
	    }
	    
	    Color foreground = Color.BLACK, background = Color.white;
	    Object[] textbackgroundforegroundfont = super.getTableCellRendererBackgroundForegroundColor(table, stockofbank, value, row, col);
	    String valuepect = (String)textbackgroundforegroundfont[0];
	    if(!Strings.isNullOrEmpty(valuepect))
	    	((JLabel)comp).setText(valuepect);
	    
	    background = (Color)textbackgroundforegroundfont[1];
	    comp.setBackground(background);
	    
	    foreground = (Color)textbackgroundforegroundfont[2];
	    comp.setForeground(foreground);
	    
	    Font font = (Font)textbackgroundforegroundfont[3];
	    comp.setFont(font);
	    
	    return comp;
	}
	

}
