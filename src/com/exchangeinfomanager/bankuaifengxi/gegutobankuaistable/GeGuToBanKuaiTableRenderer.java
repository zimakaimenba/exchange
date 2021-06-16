package com.exchangeinfomanager.bankuaifengxi.gegutobankuaistable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;

import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BanKuaiAndGeguTableBasicRenderer;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.google.common.base.Strings;

public class GeGuToBanKuaiTableRenderer extends BanKuaiAndGeguTableBasicRenderer
{
	private Color foreground;

	public GeGuToBanKuaiTableRenderer (Properties prop)
	{
		super (prop);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col) 
	{
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
	    JComponent jc = (JComponent)comp;
	    
	    TDXNodes node = (TDXNodes) ((GeGuToBanKuaiTableModel)table.getModel()).getNode(row);
	    Object[] textbackgroundforegroundfont = super.getTableCellRendererBackgroundForegroundColor(table, node, value, row, col);
	    String valuepect = (String)textbackgroundforegroundfont[0];
	    if(!Strings.isNullOrEmpty(valuepect))
	    	((JLabel)comp).setText(valuepect);
	    
	    Color background = (Color)textbackgroundforegroundfont[1];
	    comp.setBackground(background);
	    
	    foreground = (Color)textbackgroundforegroundfont[2];
	    comp.setForeground(foreground);
	    
	    Font font = (Font)textbackgroundforegroundfont[3];
	    comp.setFont(font);
	    
	    return comp;
	}
}
