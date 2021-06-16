package com.exchangeinfomanager.bankuaifengxi.bankuaigegumergetable;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BanKuaiandGeGuTableBasic;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuBasicTableModel;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;

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
		int bkcolcount = mergemodels[0].getColumnCount();
		int ggcolcount = mergemodels[1].getColumnCount();
		if(col < bkcolcount) {
//			int modelRow = mergetables[0].convertRowIndexToModel(row);
			comp = mergetables[0].getCellRenderer(row, col).getTableCellRendererComponent(mergetables[0], value, isSelected, hasFocus, row, col);
		}
		else {
			 Integer rightrow = tablemodel.getMergeModelRightToLeftRowMatch (row);
			 comp = mergetables[1].getCellRenderer(rightrow, col - bkcolcount).getTableCellRendererComponent(mergetables[1], value, isSelected, hasFocus, rightrow, col - bkcolcount );
		}

		return comp;
	}
}
