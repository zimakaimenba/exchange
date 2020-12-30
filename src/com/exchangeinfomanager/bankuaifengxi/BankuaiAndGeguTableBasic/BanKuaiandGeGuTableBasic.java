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
		for(int i=0;i<=10;i++) {
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
				this.getColumnModel().getColumn(0).setPreferredWidth(columnwidth );
			} 
		}
	}
	
	protected void createTableHeaderTooltips ()
	{
		for(int i=0;i<=10;i++) {
			String column_name  = prop.getProperty (String.valueOf(i) + "column_name");
			String column_background_highlight_info  = prop.getProperty (String.valueOf(i) + "column_background_highlight_info");
			if(column_background_highlight_info != null)
				column_background_highlight_info = "后突出:" + column_background_highlight_info;
			else column_background_highlight_info = "";
			String column_foreground_highlight_info  = prop.getProperty (String.valueOf(i) + "column_foreground_highlight_info");
			if(column_foreground_highlight_info != null)
				column_foreground_highlight_info = "前突出:" + column_foreground_highlight_info;
			else column_foreground_highlight_info = "";
			
			jtableTitleStringsTooltips[i] = column_name + "(" + column_background_highlight_info + "." + column_foreground_highlight_info +  ")";
		}
	}

}
