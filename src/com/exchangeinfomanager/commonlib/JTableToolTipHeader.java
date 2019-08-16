package com.exchangeinfomanager.commonlib;

import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class JTableToolTipHeader extends JTableHeader 
{
    String[] toolTips;
   
    public JTableToolTipHeader(TableColumnModel model) {
      super(model);
    }
     
    public String getToolTipText(MouseEvent e) {
      int col  = columnAtPoint(e.getPoint());
      int modelCol = getTable().convertColumnIndexToModel(col);
      String retStr;
      try {
        retStr = toolTips[modelCol];
      } catch (NullPointerException ex) {
        retStr = "";
      } catch (ArrayIndexOutOfBoundsException ex) {
        retStr = "";
      }
      if (retStr.length() < 1) {
        retStr = super.getToolTipText(e);
      }
      return retStr;
    }  
     
    public void setToolTipStrings(String[] toolTips) {
      this.toolTips = toolTips;
    }
}