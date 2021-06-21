package com.exchangeinfomanager.bankuaifengxi.bankuaigegumergetable;

import javax.swing.table.DefaultTableModel;

import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BandKuaiAndGeGuTableBasicModel;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.StockOfBanKuai;

public class BanKuaiGeGuMergeTableModel extends DefaultTableModel 
{
	private BandKuaiAndGeGuTableBasicModel bkmodel;
	private BandKuaiAndGeGuTableBasicModel gegumodel;
	private String[] jtableTitleStrings;
	private Integer bkmergekeyindex;
	private Integer ggmergekeyindex;

	public BanKuaiGeGuMergeTableModel (BandKuaiAndGeGuTableBasicModel bkmodel, String bkmergekeywds, BandKuaiAndGeGuTableBasicModel ggmodel, String ggmergekeywds)
	{
		super ();
		this.bkmodel = bkmodel;
		this.gegumodel = ggmodel;
		
		for(int i=0;i<this.bkmodel.getColumnCount();i++) {
			try {
				String columnkw = this.bkmodel.getColumnKeyWord(i).trim();
				if(columnkw.equalsIgnoreCase(bkmergekeywds.trim() ) ) {
					this.bkmergekeyindex = i; break;
				}
			} catch (java.lang.NullPointerException e) {}
		}
		
		for(int i=0;i<this.gegumodel.getColumnCount();i++) {
			try {
				String columnkw = this.gegumodel.getColumnKeyWord(i).trim();
				if(columnkw.equalsIgnoreCase(ggmergekeywds.trim() ) ) {
					this.ggmergekeyindex = i; break;
				}
			} catch (java.lang.NullPointerException e) {}
		}
		
		createTableTitleStrings();
	}
	protected void createTableTitleStrings ()
	{
		int bkcolumncount = this.bkmodel.getColumnCount();
		int ggcolumncount = this.gegumodel.getColumnCount ();
		String[] jtableTitleStrings = new String[bkcolumncount + ggcolumncount];
		String[] bkheader = this.bkmodel.getTableHeader();
		String[] ggheader = this.gegumodel.getTableHeader();
		System.arraycopy(bkheader, 0, jtableTitleStrings, 0, bkheader.length);  
		System.arraycopy(ggheader, 0, jtableTitleStrings, bkheader.length, ggheader.length);   
		
		this.setTableHeader(jtableTitleStrings);
	}
	public DefaultTableModel[] getTableMergeModels ()
	{
		DefaultTableModel[] models = {this.bkmodel,this.gegumodel};
		return models;
	}
	public void refresh ()
	{
		this.fireTableDataChanged();
	}
	public void deleteAllRows ()
	{
		this.bkmodel.deleteAllRows();
		this.gegumodel.deleteAllRows();
		
		this.setRowCount(0);
	}
	
	public void setTableHeader (String[] jtableTitleStrings1)
	{
		this.jtableTitleStrings = jtableTitleStrings1;
	}
	public String[] getTableHeader ()
	{
		return this.jtableTitleStrings;
	}
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		if(this.bkmergekeyindex == null || this.ggmergekeyindex == null)
			return null;
		
		int bkcolumncount = this.bkmodel.getColumnCount();
		int ggcolumncount = this.gegumodel.getColumnCount ();
		Object value = null;
		if(columnIndex < bkcolumncount)
			value = bkmodel.getValueAt(rowIndex, columnIndex);
		else {
			Object bkmergeindexvalue = bkmodel.getValueAt(rowIndex, bkmergekeyindex);
			for(int i=0;i<gegumodel.getRowCount();i++) {
				Object ggmergeindexvalue = gegumodel.getValueAt(i, ggmergekeyindex);
				if(comareIndexEqualForMerge(bkmergeindexvalue, ggmergeindexvalue ) ) {
					value = gegumodel.getValueAt(i, columnIndex - bkcolumncount  );
					break;
				}
			}
		}
		
	    return value;
	}
	/*
	 * 
	 */
	public Integer getMergeModelRightToLeftRowMatch (int leftrow)
	{
		Object bkmergeindexvalue = bkmodel.getValueAt(leftrow, bkmergekeyindex);
		for(int i=0;i<gegumodel.getRowCount();i++) {
			Object ggmergeindexvalue = gegumodel.getValueAt(i, ggmergekeyindex);
			if(comareIndexEqualForMerge(bkmergeindexvalue, ggmergeindexvalue ) ) {
				return i;	
			}
		}
		
		return null;
	}
	/*
	 * 
	 */
	private boolean comareIndexEqualForMerge(Object bkmergeindexvalue, Object ggmergeindexvalue)
	{
		if(  ((String)bkmergeindexvalue).equalsIgnoreCase( (String)ggmergeindexvalue ) )
				return true;
		else	return false;
	}
	/*
	 * 
	 */
	public Class<?> getColumnClass(int columnIndex) 
	{ 
		int bkcolumncount = this.bkmodel.getColumnCount();
		int ggcolumncount = this.gegumodel.getColumnCount ();
		Class clazz ;
		if(columnIndex <= bkcolumncount)
			clazz = bkmodel.getColumnClass(columnIndex);
		else
			clazz = gegumodel.getColumnClass(columnIndex - bkcolumncount );
		
		return clazz;
	}
	@Override
	public int getRowCount() 	{
		 if(this.bkmodel == null) return 0;
		 int count = this.bkmodel.getRowCount();
		 return this.bkmodel.getRowCount();
	}
	    @Override
	    public int getColumnCount() 
	    {
	    	 if(this.bkmodel == null) return 0;
	    	 
	    	return this.bkmodel.getColumnCount() + this.gegumodel.getColumnCount();
	    } 
	    @Override
	    public String getColumnName(int column) { 
	    	return jtableTitleStrings[column];
	    }//设置表格列名
	    @Override
	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
		public BkChanYeLianTreeNode getNode(int modelRow)		{
			BkChanYeLianTreeNode node = this.bkmodel.getNode(modelRow);
			return node;
		}
}
