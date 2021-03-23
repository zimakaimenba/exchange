package com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiGuanLi;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchConditionListener;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuBasicTableModel;
import com.exchangeinfomanager.bankuaifengxi.bankuaiinfotable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;

import net.coderazzi.filters.IFilterObserver;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.IFilterEditor;
import net.coderazzi.filters.gui.IFilterHeaderObserver;
import net.coderazzi.filters.gui.TableFilterHeader;

public abstract class BanKuaiandGeGuTableBasic extends JTable  implements  BanKuaiGeGuMatchConditionListener  , IFilterHeaderObserver
{
	private String systeminstalledpath;
	protected Properties prop;
	protected TableFilterHeader filterHeader;
	String[] jtableTitleStringsTooltips = new String[11];
//	JMenuItem mntmResetFilter;
	private JMenuItem menuItemfiltersetting;

	public BanKuaiandGeGuTableBasic (String propertiesfile)
	{
		super ();
		
		setPropertiesInfo (propertiesfile);
		
		filterHeader = new TableFilterHeader(this, AutoChoices.ENABLED); //https://coderazzi.net/tablefilter/index.html#    //https://stackoverflow.com/questions/16277700/i-want-to-obtain-auto-filtering-in-jtable-as-in-ms-excel
		filterHeader.addHeaderObserver(this);

		JPopupMenu popupMenu = new JPopupMenu();
		menuItemfiltersetting = new JMenuItem("设置Filter");
		popupMenu.add(menuItemfiltersetting);
		filterHeader.setComponentPopupMenu(popupMenu);
		
		createEvents ();
	}
	
	@Override
	public void BanKuaiGeGuMatchConditionValuesChanges(BanKuaiGeGuMatchCondition expc)
	{
		((BandKuaiAndGeGuTableBasicModel)this.getModel()).setDisplayMatchCondition(expc);
    	this.repaint();
		
	}
	/*
	 * 
	 */
	private void filterSetting ()
	{
		BanKuaiAndGeGuTablesFilterSetting filterdlg = new BanKuaiAndGeGuTablesFilterSetting (this,prop,this.filterHeader);
		filterdlg.setModal(true);
		filterdlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		filterdlg.toFront();
		filterdlg.setVisible(true);
		
	}
	
	private void createEvents() 
	{
//		filterHeader.getTable().getRowSorter().addRowSorterListener(new RowSorterListener() {
//            @Override
//            public void sorterChanged(RowSorterEvent e) {
//                System.out.println(e.getPreviousRowCount());
//            }
//         });
		
		menuItemfiltersetting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	filterSetting ();
            }
        });
		
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
		    Properties properties = System.getProperties();
		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //用户运行程序的当前目录
		} catch(Exception e) {
			System.exit(0);
		}
		
		try {
			prop = new Properties();
//			String propFileName = this.systeminstalledpath  + propertiesfile;
			String propFileName = propertiesfile;
			FileInputStream inputStream = new FileInputStream(propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			} 
			inputStream.close();
			
		} catch (Exception e) {			e.printStackTrace();		} finally {}
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
				this.getColumnModel().getColumn(i).setPreferredWidth(columnwidth );
//				this.getColumnModel().getColumn(i).setMinWidth(columnwidth);
//				this.getColumnModel().getColumn(i).setMaxWidth(columnwidth);
//				this.getColumnModel().getColumn(i).setWidth(columnwidth);
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
	
	protected void setColumnPredefinedFilter ()
	{
		for(int i=0;i<=10;i++) {
			String column_name = prop.getProperty (String.valueOf(i) + "column_name");
			if(column_name == null || column_name.toUpperCase().equals("NULL")   )
					continue;
			String column_predefinedfileter  = prop.getProperty (String.valueOf(i) + "column_info_predefinedfilter");
			if(column_predefinedfileter == null)
				continue;
			
			filterHeader.getFilterEditor(i).setContent(column_predefinedfileter);
		}
		
	}
	
	@Override
	public void tableFilterUpdated (TableFilterHeader header, IFilterEditor editor, TableColumn tableColumn)
	{
		header.setToolTipText("");
		String tooltipstr = header.getToolTipText(); 
		
		for(int i=0;i<=10;i++) {
			String column_name = prop.getProperty (String.valueOf(i) + "column_name");
			if(column_name == null || column_name.toUpperCase().equals("NULL")   )
					continue;
			
			Object content = filterHeader.getFilterEditor(i).getContent();
			if(content instanceof String)
				tooltipstr = tooltipstr + column_name + ":" +  content + ";" ;
		}
		header.setToolTipText(tooltipstr);
		if(!header.getToolTipText().isEmpty())
			header.setBackground(Color.yellow);
		else
			header.setBackground(Color.WHITE);
	}
	@Override
	public void tableFilterEditorCreated(TableFilterHeader arg0, IFilterEditor arg1, TableColumn arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tableFilterEditorExcluded(TableFilterHeader arg0, IFilterEditor arg1, TableColumn arg2) {
		// TODO Auto-generated method stub
		
	}
	

}
