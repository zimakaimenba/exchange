package com.exchangeinfomanager.commonlib.JComboCheckBox;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import java.awt.event.*;
import java.awt.*;
import java.util.*;
  
public class JComboCheckBox extends JComboBox
{
	private Boolean newitembeenset = false;
   public JComboCheckBox() {
      init();
   }
    
   public JComboCheckBox(JCheckBox[] items) {
      super(items);
      init();
   }
    
   public JComboCheckBox(Vector items) {
      super(items);
      init();
   }
    
   public JComboCheckBox(ComboBoxModel aModel) {
      super(aModel);
      init();
   }
    
   private void init() 
   {
      setRenderer(new JComboCheckBoxRenderer());
      
      addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            itemSelected();
         }
      });
      
      addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) 
			{
				if(e.getStateChange() == ItemEvent.SELECTED) {
					newitembeenset = true;
//					JCheckBox selectitem = (JCheckBox)e.getItem();
//					selectitem.setSelected(false);
					
				}
				
//				if(e.getStateChange() == ItemEvent.DESELECTED) {
//					JCheckBox deselectitem = (JCheckBox)e.getItem();
//					deselectitem.setSelected(false);
//				}
			}
			
		});
   }
  
   private void itemSelected()
   {
	   if(newitembeenset == true) {
		   newitembeenset = false;
		   return;
	   }
	   
      if (getSelectedItem() instanceof JCheckBox) {
         JCheckBox jcb = (JCheckBox)getSelectedItem();
         jcb.setSelected(!jcb.isSelected());
      }
   }
   
   public void deSelectAllItems ()
   {
	   int count = this.getItemCount();
	   for(int i=0;i<count;i++) {
		   JCheckBox jcb  = (JCheckBox)this.getItemAt(i);
		   jcb.setSelected(false);
	   }
   }
   
  
   class JComboCheckBoxRenderer  extends BasicComboBoxRenderer //implements ListCellRenderer //
   {
      private JLabel label;
       
      public JComboCheckBoxRenderer() {
         setOpaque(true);
      }
       
      public Component getListCellRendererComponent(JList list, Object value, int index,
                                                    boolean isSelected, boolean cellHasFocus) 
      {
    	 Component com =  super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); 
         if (value instanceof JCheckBox) {
        	 Component c = (Component)value;
           
            if (isSelected) {
            	Color bg = ((JCheckBox) value).getBackground();
                Color fg = ((JCheckBox) value).getForeground();
                
                c.setBackground(bg);
                c.setForeground(fg);
             } else {
//            	Color bg = list.getBackground();
//            	Color fg = list.getForeground();
//                c.setBackground(bg);
//                c.setForeground(fg);
            	 
            	 Color bg = ((JCheckBox) value).getBackground();
                 Color fg = ((JCheckBox) value).getForeground();
                 
                 c.setBackground(bg);
                 c.setForeground(fg);
             }
              
            return c;
         } else {
            if (label ==null) {
               label = new JLabel(value.toString());
            }
            else {
               label.setText(value.toString());
            }
                
            return label;
         }
      }
   }
   
   
}