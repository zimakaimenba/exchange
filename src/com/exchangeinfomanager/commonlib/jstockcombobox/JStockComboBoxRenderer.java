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
    

    public JStockComboBoxRenderer( ) {
//      this.defaultRenderer = defaultRenderer;
    	super ();
      
//      guanzhugegulist = new HashSet<String> ();
//      chicanggegulist = new HashSet<String> ();
      
//      guanzhugegulist.add("000001");
//      guanzhugegulist.add("603693");
//      
//      
//      chicanggegulist.add("603693");
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
    	
      Component c = super.getListCellRendererComponent(list, value,
          index, isSelected, cellHasFocus);
      
      String stockcode;
      if(value.toString().length() >6)
    	  stockcode = value.toString().substring(0,6).trim();
		else
			stockcode = value.toString();
      
      if (this.guanzhugegulist != null && (this.guanzhugegulist.contains(stockcode) || this.guanzhugegulist.contains(value.toString()) )) {
        c.setBackground(Color.gray.brighter());
//        c = super.getListCellRendererComponent(list, value, index, isSelected,
//            cellHasFocus);
      } else
    	  c.setBackground(Color.WHITE);
      
      if(this.chicanggegulist !=null && (this.chicanggegulist.contains(stockcode) || this.chicanggegulist.contains(value.toString()) )) {
    	  c.setForeground(Color.red);
      } else
    	  c.setForeground(Color.BLACK);
      
      return c;
    }
}