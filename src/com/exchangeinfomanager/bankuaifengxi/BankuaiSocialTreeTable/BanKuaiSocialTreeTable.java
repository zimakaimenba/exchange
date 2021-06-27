package com.exchangeinfomanager.bankuaifengxi.BankuaiSocialTreeTable;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.jdesktop.swingx.JXTreeTable;

public class BanKuaiSocialTreeTable extends JXTreeTable 
{
	private Properties prop;
	private String[] jtableTitleStringsTooltips;

	public BanKuaiSocialTreeTable(String propfile) 
	{
		setPropertiesInfo (propfile);
		
		setColumnPreferredWidth ();
		createTableHeaderTooltips ();
	}
	
	private void setPropertiesInfo (String propertiesfile)
	{
//		File directory = new File("");//�趨Ϊ��ǰ�ļ���
//		try{
//		    Properties properties = System.getProperties();
//		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //�û����г���ĵ�ǰĿ¼
//		} catch(Exception e) {
//			System.exit(0);
//		}
		
		try {
			prop = new Properties();
			String propFileName = propertiesfile;
			FileInputStream inputStream = new FileInputStream(propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			} 
			inputStream.close();
			
		} catch (Exception e) {			e.printStackTrace();		} finally {}
	}
	
	protected void setColumnPreferredWidth ()
	{
		String columnmaxnumberstr = prop.getProperty ( "columnmaxnumber");
		int columnmaxnumber =0;
		try {	columnmaxnumber = Integer.parseInt(columnmaxnumberstr);
		} catch (java.lang.NumberFormatException e) {	e.printStackTrace();	}
		
		for(int i=0;i<columnmaxnumber;i++) {
			String column_name = prop.getProperty (String.valueOf(i) + "column_name");
			String column_width  = prop.getProperty (String.valueOf(i) + "column_preferredWidth");
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
		
		return;
	}
	
	protected void createTableHeaderTooltips ()
	{
		String columnmaxnumberstr = prop.getProperty ( "columnmaxnumber");
		int columnmaxnumber =0;
		try {
			columnmaxnumber = Integer.parseInt(columnmaxnumberstr);
		} catch (java.lang.NumberFormatException e) {	e.printStackTrace();	}
		
		jtableTitleStringsTooltips = new String[columnmaxnumber];
		for(int i=0;i<columnmaxnumber;i++) {
			String column_name  = prop.getProperty (String.valueOf(i) + "column_name");
			String column_background_highlight_info  = prop.getProperty (String.valueOf(i) + "column_background_highlight_info");
			if(column_background_highlight_info != null)
				column_background_highlight_info = "��ͻ��:" + column_background_highlight_info;
			else column_background_highlight_info = "";
			String column_foreground_highlight_info  = prop.getProperty (String.valueOf(i) + "column_foreground_highlight_info");
			if(column_foreground_highlight_info != null)
				column_foreground_highlight_info = "ǰͻ��:" + column_foreground_highlight_info;
			else column_foreground_highlight_info = "";
			
			try {
				jtableTitleStringsTooltips[i] = column_name + "(" + column_background_highlight_info + "." + column_foreground_highlight_info +  ")";
			} catch ( java.lang.ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
	}
}
