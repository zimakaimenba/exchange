package com.exchangeinfomanager.commonlib.jstockcombobox;

import java.awt.Color;
import java.awt.Component;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class JStockComboBoxNodeRenderer extends BasicComboBoxRenderer 
{
    private Set<String> guanzhugegulist;
    private Set<String> guanzhubankuailist;
	private Set<String> chicanggegulist;
	private Set<String> previouschicanggegulist;
	
	@Override
    public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
            final boolean cellHasFocus) 
	{
		 Component comp =  super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		 
		 if (value != null && (value instanceof com.exchangeinfomanager.nodes.BkChanYeLianTreeNode  ))   {
	      	  BkChanYeLianTreeNode node = (BkChanYeLianTreeNode)value;
	          String stockcode = node.getMyOwnCode() + node.getMyOwnName();
//	          ((JLabel)comp).setText(stockcode);
	            
	           if (this.guanzhugegulist != null && (this.guanzhugegulist.contains(stockcode) || this.guanzhugegulist.contains(value.toString()) )) {
	              comp.setBackground(Color.gray.brighter());
	           } else
	        	   comp.setBackground(Color.WHITE);
	            
	           if(this.chicanggegulist !=null && (this.chicanggegulist.contains(stockcode) || this.chicanggegulist.contains(value.toString()) )) {
	          	  comp.setForeground(Color.red);
	           } else
	           if(this.previouschicanggegulist !=null && (this.previouschicanggegulist.contains(stockcode) || this.previouschicanggegulist.contains(value.toString()) )) {
	 	          comp.setForeground(Color.CYAN.darker());
	 	       } else
	 	       if(this.guanzhubankuailist !=null && (this.guanzhubankuailist.contains(stockcode) || this.guanzhubankuailist.contains(value.toString()) )) {
		          	  comp.setForeground(Color.red);
		       } else	   
	        	   comp.setForeground(Color.BLACK);
	            
	           final BkChanYeLianTreeNode item = (BkChanYeLianTreeNode) value;
	           this.setText(item.getMyOwnCode() + item.getMyOwnName() );  
	     }

        if (index == -1) {
            final BkChanYeLianTreeNode item = (BkChanYeLianTreeNode) value;
            this.setText(item.getMyOwnCode() + item.getMyOwnName() );
        }

        return this;
        
    }
	public void setGuanZhuBanKuaiList(Set<String> guanzhubankuailist1)
    {
        this.guanzhubankuailist = guanzhubankuailist1;
    }
    public void setGuanZhuGeGuList(Set<String> guanzhugegulist1)
    {
        this.guanzhugegulist = guanzhugegulist1;
    }
    public void setChiCangGeGuList(Set<String> ccgegulist1)
    {
    	this.chicanggegulist = ccgegulist1;
    }
    public void setPreviousChicangGeGuList (Set<String> pccgegulist1)
    {
    	this.previouschicanggegulist = pccgegulist1;
    }

}