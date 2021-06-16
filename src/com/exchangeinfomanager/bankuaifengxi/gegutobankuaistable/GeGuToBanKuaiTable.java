package com.exchangeinfomanager.bankuaifengxi.gegutobankuaistable;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BanKuaiandGeGuTableBasic;
import com.exchangeinfomanager.bankuaifengxi.bankuaiinfotable.BanKuaiInfoTableRenderer;

public class GeGuToBanKuaiTable  extends BanKuaiandGeGuTableBasic
{
	private GeGuToBanKuaiTableRenderer renderer;

	public GeGuToBanKuaiTable(String propfile)
	{
		super(propfile);
		
		GeGuToBanKuaiTableModel bkmodel = new GeGuToBanKuaiTableModel (super.prop);
		this.setModel(bkmodel);
		
		this.renderer =  new GeGuToBanKuaiTableRenderer (prop);
		
		super.setColumnPreferredWidth ();
		super.createTableHeaderTooltips ();
	
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.getModel());
		this.setRowSorter(sorter);
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JTable#getCellRenderer(int, int)
	 */
	public TableCellRenderer getCellRenderer(int row, int column) 
	{
		return renderer;
	}
}
