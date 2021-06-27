package com.exchangeinfomanager.bankuaifengxi.BankuaiSocialTreeTable;

import java.util.Properties;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

public class BanKuaiSocialTreeTableModel  extends AbstractTreeTableModel
{
	private Properties prop;
	protected String[] jtableTitleStrings ;
	
	public BanKuaiSocialTreeTableModel (Properties prop)
	{
		this.prop = prop;
	}

	@Override
	public int getColumnCount() {
		String columnmaxnumberstr = prop.getProperty ( "columnmaxnumber");
		int columnmaxnumber = Integer.parseInt(columnmaxnumberstr);
        return columnmaxnumber;
	}
	
	public void setTableHeader (String[] jtableTitleStrings1)
	{
		this.jtableTitleStrings = jtableTitleStrings1;
	}
	public String[] getTableHeader ()
	{
		return this.jtableTitleStrings;
	}
	@Override
	public Object getValueAt(Object node, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getChild(Object parentnode, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		// TODO Auto-generated method stub
		return 0;
	}

}
