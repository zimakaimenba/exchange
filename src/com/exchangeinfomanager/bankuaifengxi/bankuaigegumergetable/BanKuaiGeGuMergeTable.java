package com.exchangeinfomanager.bankuaifengxi.bankuaigegumergetable;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BanKuaiandGeGuTableBasic;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BandKuaiAndGeGuTableBasicModel;

public class BanKuaiGeGuMergeTable extends JTable
{
	private BanKuaiandGeGuTableBasic tblbk;
	private BanKuaiandGeGuTableBasic tblgegu;
	private BanKuaiGeGuMergeTableRenderer renderer;

	public BanKuaiGeGuMergeTable (BanKuaiandGeGuTableBasic tblbk, String bkmergekeywds, BanKuaiandGeGuTableBasic tblgg, String ggmergekeywds)
	{
		this.tblbk = tblbk;
		this.tblgegu = tblgg;
		
		BanKuaiGeGuMergeTableModel mergemodel = new BanKuaiGeGuMergeTableModel ( (BandKuaiAndGeGuTableBasicModel)this.tblbk.getModel(), bkmergekeywds ,
															(BandKuaiAndGeGuTableBasicModel)this.tblgegu.getModel(), ggmergekeywds
															);
		this.setModel(mergemodel);
		this.renderer = new BanKuaiGeGuMergeTableRenderer ();
		
		setColumnPreferredWidth ();
	}
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JTable#getCellRenderer(int, int)
	 */
	public TableCellRenderer getCellRenderer(int row, int column) 
	{
		return renderer;
	}
	/*
	 * 
	 */
	public BanKuaiandGeGuTableBasic[] getMergeTables ()
	{
		BanKuaiandGeGuTableBasic[] mergetables = {this.tblbk, this.tblgegu};
		return mergetables;
	}
	/*
	 * 
	 */
	protected void setColumnPreferredWidth ()
	{
		String columnmaxnumberstr = this.tblbk.getTableProperties().getProperty ( "columnmaxnumber");
		int tblbkcolumnmaxnumber =0;
		try {
			tblbkcolumnmaxnumber = Integer.parseInt(columnmaxnumberstr);
		} catch (java.lang.NumberFormatException e) {	e.printStackTrace();	}
		
		for(int i=0;i<tblbkcolumnmaxnumber;i++) {
			String column_name = this.tblbk.getTableProperties().getProperty (String.valueOf(i) + "column_name");
			String column_width  = this.tblbk.getTableProperties().getProperty (String.valueOf(i) + "column_preferredWidth");
			Integer columnwidth =0;
			if(column_name == null || column_name.toUpperCase().equals("NULL") || (column_width != null && column_width.equals("0") )  ) {
				columnwidth = 0;
				this.getColumnModel().getColumn(i).setMinWidth(columnwidth);
				this.getColumnModel().getColumn(i).setMaxWidth(columnwidth);
				this.getColumnModel().getColumn(i).setWidth(columnwidth);
			} else if(column_width != null && !column_width.equals("0") ) {
				columnwidth = Integer.valueOf(column_width) ;
				this.getColumnModel().getColumn(i).setPreferredWidth(columnwidth );
//				this.getColumnModel().getColumn(i).setMinWidth(columnwidth);
//				this.getColumnModel().getColumn(i).setMaxWidth(columnwidth);
//				this.getColumnModel().getColumn(i).setWidth(columnwidth);
			} 
		}
	
		columnmaxnumberstr = this.tblgegu.getTableProperties().getProperty ( "columnmaxnumber");
		int tblggcolumnmaxnumber =0;
		try {
			tblggcolumnmaxnumber = Integer.parseInt(columnmaxnumberstr);
		} catch (java.lang.NumberFormatException e) {	e.printStackTrace();	}
		
		for(int i=0;i<tblggcolumnmaxnumber;i++) {
			String column_name = this.tblgegu.getTableProperties().getProperty (String.valueOf(i) + "column_name");
			String column_width  = this.tblgegu.getTableProperties().getProperty (String.valueOf(i) + "column_preferredWidth");
			Integer columnwidth =0;
			if(column_name == null || column_name.toUpperCase().equals("NULL") || (column_width != null && column_width.equals("0") )  ) {
				columnwidth = 0;
				this.getColumnModel().getColumn(tblbkcolumnmaxnumber + i).setMinWidth(columnwidth);
				this.getColumnModel().getColumn(tblbkcolumnmaxnumber + i).setMaxWidth(columnwidth);
				this.getColumnModel().getColumn(tblbkcolumnmaxnumber + i).setWidth(columnwidth);
			} else if(column_width != null && !column_width.equals("0") ) {
				columnwidth = Integer.valueOf(column_width) ;
				this.getColumnModel().getColumn(tblbkcolumnmaxnumber + i).setPreferredWidth(columnwidth );
//				this.getColumnModel().getColumn(i).setMinWidth(columnwidth);
//				this.getColumnModel().getColumn(i).setMaxWidth(columnwidth);
//				this.getColumnModel().getColumn(i).setWidth(columnwidth);
			} 
		}
	}
	
//	 protected JTableHeader createDefaultTableHeader() 
//	    {
//	        return new JTableHeader(columnModel) {
//	            public String getToolTipText(MouseEvent e) {
//	                String tip = null;
//	                java.awt.Point p = e.getPoint();
//	                int index = columnModel.getColumnIndexAtX(p.x);
//	                int realIndex = 
//	                        columnModel.getColumn(index).getModelIndex();
//	                return jtableTitleStringsTooltips[realIndex];
//	            }
//	        };
//	    }
}
