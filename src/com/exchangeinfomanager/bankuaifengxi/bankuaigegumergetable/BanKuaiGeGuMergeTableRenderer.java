package com.exchangeinfomanager.bankuaifengxi.bankuaigegumergetable;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BanKuaiandGeGuTableBasic;

public class BanKuaiGeGuMergeTableRenderer extends DefaultTableCellRenderer 
{
	public BanKuaiGeGuMergeTableRenderer ()
	{
		super ();
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col) 
	{
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		
		BanKuaiGeGuMergeTableModel tablemodel = (BanKuaiGeGuMergeTableModel)table.getModel();
		
		BanKuaiandGeGuTableBasic[] mergetables = ((BanKuaiGeGuMergeTable)table).getMergeTables();
		DefaultTableModel[] mergemodels = ((BanKuaiGeGuMergeTableModel)table.getModel()).getTableMergeModels();
		
		if(mergemodels[0].getRowCount() == 0)
			return comp;
		
		if(mergemodels[1].getRowCount() == 0)
			return comp;
		
		int bkcolcount = mergemodels[0].getColumnCount();
		int ggcolcount = mergemodels[1].getColumnCount();
		
		Integer rightrow = tablemodel.getMergeModelRightToLeftRowMatch (row);
		if(table.isRowSelected(row)) {
			mergetables[0].setRowSelectionInterval(row, row);
			if(rightrow!= null)	mergetables[1].setRowSelectionInterval(rightrow, rightrow);
		} else {
			try {
				mergetables[0].removeRowSelectionInterval(row, row);
			} catch (java.lang.IllegalArgumentException e) {}
			
			if(rightrow!= null)	mergetables[1].removeRowSelectionInterval(rightrow, rightrow);
		}
		if(col < bkcolcount) {
//			int modelRow = mergetables[0].convertRowIndexToModel(row);
			try {
				comp = mergetables[0].getCellRenderer(row, col).getTableCellRendererComponent(mergetables[0], value, isSelected, hasFocus, row, col);
			} catch (java.lang.ArrayIndexOutOfBoundsException e ) {}
		}
		else {	 if(rightrow != null)
					comp = mergetables[1].getCellRenderer(rightrow, col - bkcolcount).getTableCellRendererComponent(mergetables[1], value, isSelected, hasFocus, rightrow, col - bkcolcount );
		}
		
		Font font = this.getFont();
		if(table.isRowSelected(row) ) font = new Font(font.getName(), font.getStyle() | Font.ITALIC,font.getSize());
		comp.setFont(font);

		return comp;
	}
}
