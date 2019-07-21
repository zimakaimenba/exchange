package com.exchangeinfomanager.commonlib.jstockcombobox;

import java.awt.Color;
import java.awt.Component;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class JStockComboBoxRenderer extends  DefaultListCellRenderer 
{

    private static final long serialVersionUID = -1L;
    private Set<String> guanzhugegulist;
    private Set<String> chicanggegulist;
    

    public JStockComboBoxRenderer( ) 
    {
    	super ();
    }

    public void setGuanZhuGeGuList(Set<String> guanzhugegulist)
    {
        this.guanzhugegulist = guanzhugegulist;
    }
    public void setChiCangGeGuList(Set<String> ccgegulist)
    {
    	this.chicanggegulist = ccgegulist;
    }

    
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
    	
      Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      
      String stockcode;
      if(value.toString().length() >6)
    	  stockcode = value.toString().substring(0,6).trim();
		else
			stockcode = value.toString();
      
      if (this.guanzhugegulist != null && (this.guanzhugegulist.contains(stockcode) || this.guanzhugegulist.contains(value.toString()) )) {
        comp.setBackground(Color.gray.brighter());
//        c = super.getListCellRendererComponent(list, value, index, isSelected,
//            cellHasFocus);
      } else
    	  comp.setBackground(Color.WHITE);
      
      if(this.chicanggegulist !=null && (this.chicanggegulist.contains(stockcode) || this.chicanggegulist.contains(value.toString()) )) {
    	  comp.setForeground(Color.red);
      } else
    	  comp.setForeground(Color.BLACK);
      
      return comp;
    }
}