package com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public abstract class BanKuaiandGeGuTableBasic extends JTable 
{
	private String systeminstalledpath;
	protected Properties prop;
	protected TableFilterHeader filterHeader;
	String[] jtableTitleStringsTooltips = new String[11];

	public BanKuaiandGeGuTableBasic (String propertiesfile)
	{
		super ();
		
		setPropertiesInfo (propertiesfile);
		
		filterHeader = new TableFilterHeader(this, AutoChoices.ENABLED); //https://coderazzi.net/tablefilter/index.html#    //https://stackoverflow.com/questions/16277700/i-want-to-obtain-auto-filtering-in-jtable-as-in-ms-excel
	}
	
	public void resetTableHeaderFilter ()
	{
		filterHeader.resetFilter();
	}
	
	 protected JTableHeader createDefaultTableHeader() 
	    {
	        return new JTableHeader(columnModel) {
	            public String getToolTipText(MouseEvent e) {
	                String tip = null;
	                java.awt.Point p = e.getPoint();
	                int index = columnModel.getColumnIndexAtX(p.x);
	                int realIndex = 
	                        columnModel.getColumn(index).getModelIndex();
	                return jtableTitleStringsTooltips[realIndex];
	            }
	        };
	    }
	 
	private void setPropertiesInfo (String propertiesfile)
	{

		File directory = new File("");//设定为当前文件夹
		try{
//		    logger.debug(directory.getCanonicalPath());//获取标准的路径
//		    logger.debug(directory.getAbsolutePath());//获取绝对路径
//		    this.systeminstalledpath = toUNIXpath(directory.getCanonicalPath()+ "\\");
		    Properties properties = System.getProperties();
		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //用户运行程序的当前目录
		} catch(Exception e) {
			System.exit(0);
		}
		
//		FileInputStream inputStream = null;
		String propxmlFileName = null ;
		try {
//			Properties properties = System.getProperties();
//		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //用户运行程序的当前目录
//		    
//			Properties prop = new Properties();
//			String propFileName =  this.systeminstalledpath  + "/config/config.properties";
//			inputStream = new FileInputStream(propFileName);
//			if (inputStream != null) {
//				prop.load(inputStream);
//			} else {
//				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
//			}
			
			prop = new Properties();
			String propFileName = this.systeminstalledpath  + propertiesfile;
			FileInputStream inputStream = new FileInputStream(propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			} 
			inputStream.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}

	private  String toUNIXpath(String filePath) 
   	{
   		    return filePath.replace('\\', '/');
   	}
	
	protected void setColumnPreferredWidth ()
	{
		String column_name0  = prop.getProperty ("0column_name");
		String column_width0  = prop.getProperty ("0column_preferredWidth");
		if(column_name0 == null || column_name0.toUpperCase().equals("NULL") )
			this.getColumnModel().getColumn(0).setPreferredWidth(0 );
		else if(column_width0 != null) {
			this.getColumnModel().getColumn(0).setPreferredWidth(Integer.valueOf(column_width0) );
		}
		String column_name1  = prop.getProperty ("1column_name");
		String column_width1  = prop.getProperty ("1column_preferredWidth");
		if(column_name1 == null || column_name1.toUpperCase().equals("NULL") )
			this.getColumnModel().getColumn(1).setPreferredWidth(0 );
		else
		if(column_width1 != null) {
			this.getColumnModel().getColumn(1).setPreferredWidth(Integer.valueOf(column_width1) );
		}
		String column_name2  = prop.getProperty ("2column_name");
		String column_width2  = prop.getProperty ("2column_preferredWidth");
		if(column_name2 == null || column_name2.toUpperCase().equals("NULL") )
			this.getColumnModel().getColumn(2).setPreferredWidth(0 );
		else
		if(column_width2 != null) {
			this.getColumnModel().getColumn(2).setPreferredWidth(Integer.valueOf(column_width2) );
		}
		String column_name3  = prop.getProperty ("3column_name");
		String column_width3  = prop.getProperty ("3column_preferredWidth");
		if(column_name3 == null || column_name3.toUpperCase().equals("NULL") )
			this.getColumnModel().getColumn(3).setPreferredWidth(0 );
		else
		if(column_width3 != null) {
			this.getColumnModel().getColumn(3).setPreferredWidth(Integer.valueOf(column_width3) );
		}
		String column_name4  = prop.getProperty ("4column_name");
		String column_width4  = prop.getProperty ("4column_preferredWidth");
		if(column_name4 == null || column_name4.toUpperCase().equals("NULL") )
			this.getColumnModel().getColumn(4).setPreferredWidth(0 );
		else
		if(column_width4 != null) {
			this.getColumnModel().getColumn(4).setPreferredWidth(Integer.valueOf(column_width4) );
		}
		String column_name5  = prop.getProperty ("5column_name");
		String column_width5  = prop.getProperty ("5column_preferredWidth");
		if(column_name5 == null || column_name5.toUpperCase().equals("NULL") ) {
			this.getColumnModel().getColumn(5).setMinWidth(0);
			this.getColumnModel().getColumn(5).setMaxWidth(0);
			this.getColumnModel().getColumn(5).setWidth(0);
		}
		else
		if(column_width5 != null) {
			this.getColumnModel().getColumn(5).setPreferredWidth(Integer.valueOf(column_width5) );
		}
		
		String column_name6  = prop.getProperty ("6column_name");
		String column_width6  = prop.getProperty ("6column_preferredWidth");
		if(column_name6 == null || column_name6.toUpperCase().equals("NULL") ) {
			this.getColumnModel().getColumn(6).setMinWidth(0);
			this.getColumnModel().getColumn(6).setMaxWidth(0);
			this.getColumnModel().getColumn(6).setWidth(0);
		}
		else
		if(column_width6 != null) {
			this.getColumnModel().getColumn(6).setPreferredWidth(Integer.valueOf(column_width6) );
		}
		
		String column_name7  = prop.getProperty ("7column_name");
		String column_width7  = prop.getProperty ("7column_preferredWidth");
		if(column_name7 == null || column_name7.toUpperCase().equals("NULL") ) {
			this.getColumnModel().getColumn(7).setMinWidth(0);
			this.getColumnModel().getColumn(7).setMaxWidth(0);
			this.getColumnModel().getColumn(7).setWidth(0);
		}
		else
		if(column_width7 != null) {
			this.getColumnModel().getColumn(7).setPreferredWidth(Integer.valueOf(column_width7) );
		}
		
		String column_name8  = prop.getProperty ("8column_name");
		String column_width8  = prop.getProperty ("8column_preferredWidth");
		if(column_name8 == null || column_name8.toUpperCase().equals("NULL") ) {
//			this.getColumnModel().getColumn(8).setPreferredWidth(0 );
			this.getColumnModel().getColumn(8).setMinWidth(0);
			this.getColumnModel().getColumn(8).setMaxWidth(0);
			this.getColumnModel().getColumn(8).setWidth(0);
		}
		else
		if(column_width8 != null) {
			this.getColumnModel().getColumn(8).setPreferredWidth(Integer.valueOf(column_width8) );
		}
		
		String column_name9  = prop.getProperty ("9column_name");
		String column_width9  = prop.getProperty ("9column_preferredWidth");
		if(column_name9 == null || column_name9.toUpperCase().equals("NULL") ) {
//			this.getColumnModel().getColumn(9).setPreferredWidth(0 );
			
			this.getColumnModel().getColumn(9).setMinWidth(0);
			this.getColumnModel().getColumn(9).setMaxWidth(0);
			this.getColumnModel().getColumn(9).setWidth(0);
		}
		else
		if(column_width9 != null) {
			this.getColumnModel().getColumn(9).setPreferredWidth(Integer.valueOf(column_width9) );
		}
		String column_name10  = prop.getProperty ("10column_name");
		String column_width10  = prop.getProperty ("10column_preferredWidth");
		if(column_name10 == null || column_name10.toUpperCase().equals("NULL") ) {
//			this.getColumnModel().getColumn(10).setPreferredWidth(0 );
			
			this.getColumnModel().getColumn(10).setMinWidth(0);
			this.getColumnModel().getColumn(10).setMaxWidth(0);
			this.getColumnModel().getColumn(10).setWidth(0);
			
		}
		else
		if(column_width10 != null) {
			this.getColumnModel().getColumn(10).setPreferredWidth(Integer.valueOf(column_width10) );
		}
		
		
	}
	
	protected void createTableHeaderTooltips ()
	{
		String column_name0  = prop.getProperty ("0column_name");
		String column_background_highlight_info0  = prop.getProperty ("0column_background_highlight_info");
		if(column_background_highlight_info0 != null)
			column_background_highlight_info0 = "后突出:" + column_background_highlight_info0;
		else column_background_highlight_info0 = "";
		String column_foreground_highlight_info0  = prop.getProperty ("0column_foreground_highlight_info");
		if(column_foreground_highlight_info0 != null)
			column_foreground_highlight_info0 = "前突出:" + column_foreground_highlight_info0;
		else column_foreground_highlight_info0 = "";
			
		String column_name1  = prop.getProperty ("1column_name");
		String column_background_highlight_info1  = prop.getProperty ("1column_background_highlight_info");
		if(column_background_highlight_info1 != null)
			column_background_highlight_info1 = "后突出:" + column_background_highlight_info1;
		else column_background_highlight_info1 = "";
		String column_foreground_highlight_info1  = prop.getProperty ("1column_foreground_highlight_info");
		if(column_foreground_highlight_info1 != null)
			column_foreground_highlight_info1 = "前突出:" + column_foreground_highlight_info1;
		else column_foreground_highlight_info1 = "";
		
		String column_name2  = prop.getProperty ("2column_name");
		String column_background_highlight_info2  = prop.getProperty ("2column_background_highlight_info");
		if(column_background_highlight_info2 != null)
			column_background_highlight_info2 = "后突出:" + column_background_highlight_info2;
		else column_background_highlight_info2 = "";
		String column_foreground_highlight_info2  = prop.getProperty ("2column_foreground_highlight_info");
		if(column_foreground_highlight_info2 != null)
			column_foreground_highlight_info2 = "前突出:" + column_foreground_highlight_info2;
		else column_foreground_highlight_info2 = "";
		
		String column_name3  = prop.getProperty ("3column_name");
		String column_background_highlight_info3  = prop.getProperty ("3column_background_highlight_info");
		if(column_background_highlight_info3 != null)
			column_background_highlight_info3 = "后突出:" + column_background_highlight_info3;
		else column_background_highlight_info3 = "";
		String column_foreground_highlight_info3  = prop.getProperty ("3column_foreground_highlight_info");
		if(column_foreground_highlight_info3 != null)
			column_foreground_highlight_info3 = "前突出:" + column_foreground_highlight_info3;
		else column_foreground_highlight_info3 = "";
		
		String column_name4  = prop.getProperty ("4column_name");
		String column_background_highlight_info4  = prop.getProperty ("4column_background_highlight_info");
		if(column_background_highlight_info4 != null)
			column_background_highlight_info4 = "后突出:" + column_background_highlight_info4;
		else column_background_highlight_info4 = "";
		String column_foreground_highlight_info4  = prop.getProperty ("4column_foreground_highlight_info");
		if(column_foreground_highlight_info4 != null)
			column_foreground_highlight_info4 = "前突出:" + column_foreground_highlight_info4;
		else column_foreground_highlight_info4 = "";
		
		String column_name5  = prop.getProperty ("5column_name");
		String column_background_highlight_info5  = prop.getProperty ("5column_background_highlight_info");
		if(column_background_highlight_info5 != null)
			column_background_highlight_info5 = "后突出:" + column_background_highlight_info5;
		else column_background_highlight_info5 = "";
		String column_foreground_highlight_info5  = prop.getProperty ("5column_foreground_highlight_info");
		if(column_foreground_highlight_info5 != null)
			column_foreground_highlight_info5 = "前突出:" + column_foreground_highlight_info5;
		else column_foreground_highlight_info5 = "";
		
		String column_name6  = prop.getProperty ("6column_name");
		String column_background_highlight_info6  = prop.getProperty ("6column_background_highlight_info");
		if(column_background_highlight_info6 != null)
			column_background_highlight_info6 = "后突出:" + column_background_highlight_info6;
		else column_background_highlight_info6 = "";
		String column_foreground_highlight_info6  = prop.getProperty ("6column_foreground_highlight_info");
		if(column_foreground_highlight_info6 != null)
			column_foreground_highlight_info6 = "前突出:" + column_foreground_highlight_info6;
		else column_foreground_highlight_info6 = "";
		
		String column_name7  = prop.getProperty ("7column_name");
		String column_background_highlight_info7  = prop.getProperty ("7column_background_highlight_info");
		if(column_background_highlight_info7 != null)
			column_background_highlight_info7 = "后突出:" + column_background_highlight_info7;
		else column_background_highlight_info7 = "";
		String column_foreground_highlight_info7  = prop.getProperty ("7column_foreground_highlight_info");
		if(column_foreground_highlight_info7 != null)
			column_foreground_highlight_info7 = "前突出:" + column_foreground_highlight_info7;
		else column_foreground_highlight_info7 = "";
		
		String column_name8  = prop.getProperty ("8column_name");
		String column_background_highlight_info8  = prop.getProperty ("8column_background_highlight_info");
		if(column_background_highlight_info8 != null)
			column_background_highlight_info8 = "后突出:" + column_background_highlight_info8;
		else column_background_highlight_info8 = "";
		String column_foreground_highlight_info8  = prop.getProperty ("8column_foreground_highlight_info");
		if(column_foreground_highlight_info8 != null)
			column_foreground_highlight_info8 = "前突出:" + column_foreground_highlight_info8;
		else column_foreground_highlight_info8 = "";
		
		String column_name9  = prop.getProperty ("9column_name");
		String column_background_highlight_info9  = prop.getProperty ("9column_background_highlight_info");
		if(column_background_highlight_info9 != null)
			column_background_highlight_info9 = "后突出:" + column_background_highlight_info9;
		else column_background_highlight_info9 = "";
		String column_foreground_highlight_info9  = prop.getProperty ("9column_foreground_highlight_info");
		if(column_foreground_highlight_info9 != null)
			column_foreground_highlight_info9 = "前突出:" + column_foreground_highlight_info9;
		else column_foreground_highlight_info9 = "";
		
		String column_name10  = prop.getProperty ("10column_name");
		String column_background_highlight_info10  = prop.getProperty ("10column_background_highlight_info");
		if(column_background_highlight_info10 != null)
			column_background_highlight_info10 = "后突出:" + column_background_highlight_info10;
		else column_background_highlight_info10 = "";
		String column_foreground_highlight_info10  = prop.getProperty ("10column_foreground_highlight_info");
		if(column_foreground_highlight_info10 != null)
			column_foreground_highlight_info10 = "前突出:" + column_foreground_highlight_info10;
		else column_foreground_highlight_info10 = "";
		
		jtableTitleStringsTooltips[0] = column_name0 + "(" + column_background_highlight_info0 + "." + column_foreground_highlight_info0 +  ")";
		jtableTitleStringsTooltips[1] =	column_name1+ "(" + column_background_highlight_info1 + "." + column_foreground_highlight_info1 + ")";
		jtableTitleStringsTooltips[2] = column_name2+ "(" + column_background_highlight_info2 + "." + column_foreground_highlight_info2 + ")";
		jtableTitleStringsTooltips[3] = column_name3+ "(" + column_background_highlight_info3 + "." + column_foreground_highlight_info3 + ")";
		jtableTitleStringsTooltips[4] =	column_name4+ "(" + column_background_highlight_info4 + "." + column_foreground_highlight_info4 + ")";
		jtableTitleStringsTooltips[5] =	column_name5+ "(" + column_background_highlight_info5 + "." + column_foreground_highlight_info5 + ")";
		jtableTitleStringsTooltips[6] =	column_name6+ "(" + column_background_highlight_info6 + "." + column_foreground_highlight_info6 + ")";
		jtableTitleStringsTooltips[7] =	column_name7+ "(" + column_background_highlight_info7 + "." + column_foreground_highlight_info7+ ")";
		jtableTitleStringsTooltips[8] =	column_name8+ "(" + column_background_highlight_info8 + "." + column_foreground_highlight_info8+ ")";
		jtableTitleStringsTooltips[9] =	column_name9+ "(" + column_background_highlight_info9 + "." + column_foreground_highlight_info9+ ")";
		jtableTitleStringsTooltips[10] =	column_name10+ "(" + column_background_highlight_info10 + "." + column_foreground_highlight_info10+ ")";
	}
	

}
