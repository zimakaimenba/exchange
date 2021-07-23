package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.A.MainGui.StockInfoManager;
import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.Nodes.StockOfBanKuai;
import com.exchangeinfomanager.Core.NodesDataServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.TagManagment.JDialogForTagSearchMatrixPanelForAddSysNewsToNode;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsPnl2.TDXNodsInforPnl;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BanKuaiandGeGuTableBasic;
import com.exchangeinfomanager.bankuaifengxi.ai.WeeklyFenXiWizard;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.gui.subgui.DateRangeSelectPnl;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public abstract class BanKuaiGeGuBasicTable extends BanKuaiandGeGuTableBasic 
{
	public BanKuaiGeGuBasicTable (StockInfoManager stockmanager1,String propertiesfilepath)
	{
		super (propertiesfilepath);

		this.bkdbopt = new BanKuaiDbOperation ();
		this.newsdbopt = new StockCalendarAndNewDbOperation ();
		this.stockmanager = stockmanager1;
				
//		super.setColumnPreferredWidth ();
//		super.createTableHeaderTooltips ();
//		super.setColumnPredefinedFilter();
		
		createMenuForIndividualGeGu ();
		createMenuForTable ();
		createEvents ();
		
//		filterHeader = new TableFilterHeader(this, AutoChoices.ENABLED); //https://coderazzi.net/tablefilter/index.html#    //https://stackoverflow.com/questions/16277700/i-want-to-obtain-auto-filtering-in-jtable-as-in-ms-excel
	}
	
	protected StockCalendarAndNewDbOperation newsdbopt;
	protected BanKuaiDbOperation bkdbopt;
	protected StockInfoManager stockmanager;
	private static final long serialVersionUID = 1L;

	protected JMenuItem menuItemAddNews;
	protected JMenuItem menuItemAddGz;
	protected JMenuItem menuItemGeguInfo;
	protected JMenuItem menuItemLongTou;
	protected JPopupMenu popupMenuGeguNews;
//	protected Properties prop;

	
//	protected TableFilterHeader filterHeader;
//	private JMenuItem menuItemQiangShi;
//
//	private JMenuItem menuItemRuoShi;
	
	
	/*
	 * 
	 */
	public JPopupMenu getPopupMenu ()
	{
		return this.popupMenuGeguNews;
	}
	/*
	 * 
	 */
	protected void sortByZhanBiGrowthRate ()
	{
		TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)this.getRowSorter();
		
		String columnIndexToSortProp = super.prop.getProperty("columnIndexToSort"); //优先排序占比增长
		int columnIndexToSort = 4;
		if(columnIndexToSortProp != null)
			columnIndexToSort = Integer.parseInt(columnIndexToSortProp);
		
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
	}


	protected void createMenuForTable ()
	{
		String columnmaxnumberstr = prop.getProperty ( "columnmaxnumber");
		int columnmaxnumber =0;
		try {
			columnmaxnumber = Integer.parseInt(columnmaxnumberstr);
		} catch (java.lang.NumberFormatException e) {	e.printStackTrace();	}
		
		JPopupMenu popupMenuGegu = new JPopupMenu () ;
		for(int i=0;i<columnmaxnumber;i++) {
			String column_name = prop.getProperty (String.valueOf(i) + "column_name");
			if(column_name == null || column_name.toUpperCase().equals("NULL")   )
					continue;
			String column_kw = prop.getProperty (String.valueOf(i) + "column_info_keyword");
			String column_info_topaixumenu =  prop.getProperty (String.valueOf(i) + "column_info_topaixumenu");
			if(column_info_topaixumenu!= null && column_info_topaixumenu.toUpperCase().equals("FALSE") )
				continue;
			
			JMenuItem menuItemGegu = new JMenuItem("按" + column_name + "排序");
			menuItemGegu.setName(column_kw);
			menuItemGegu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					String kw = menuItemGegu.getName();
					
					if(kw.equals("TimeRangeZhangFu")) {
						BanKuai bk = (BanKuai) ((BanKuaiGeGuBasicTableModel)getModel()).getCurDispalyBandKuai ();
						if(bk == null)  return;
						
						DateRangeSelectPnl datachoose = new DateRangeSelectPnl ( LocalDate.now().minusDays(1), LocalDate.now() ); 
		        		JOptionPane.showMessageDialog(null, datachoose,"选择时间段", JOptionPane.OK_CANCEL_OPTION);
		        		
		        		LocalDate searchstart = datachoose.getDatachoosestart();
		        		LocalDate searchend = datachoose.getDatachooseend();
		        		
						SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
		    			//一次性同步板块数据以及所属个股数据
		    			try {	svsbk.syncBanKuaiAndItsStocksForSpecificTime(bk, searchstart, LocalDate.now(),NodeGivenPeriodDataItem.WEEK,true);
		    			} catch (SQLException ex) {ex.printStackTrace();}
		    			svsbk = null;
		        		((BanKuaiGeGuBasicTableModel)getModel()).sortTableByKeywords (kw,true);
					} else 
						((BanKuaiGeGuBasicTableModel)getModel()).sortTableByKeywords (kw,true);
				}
			});
			
			popupMenuGegu.add(menuItemGegu);
		}
		this.getTableHeader().setComponentPopupMenu (popupMenuGegu);
	}
	
	private void createMenuForIndividualGeGu() 
	{
		popupMenuGeguNews = new JPopupMenu();
		menuItemGeguInfo = new JMenuItem("个股信息");
		menuItemAddNews = new JMenuItem("添加个股新闻");
		menuItemAddGz = new JMenuItem("个股分析");
		menuItemLongTou = new JMenuItem("设为/取消板块龙头");
		
		
		popupMenuGeguNews.add(menuItemGeguInfo);
//		popupMenuGeguNews.add(menuItemAddNews);
		popupMenuGeguNews.add(menuItemAddGz);
		popupMenuGeguNews.add(menuItemLongTou);
		
		
		this.setComponentPopupMenu(popupMenuGeguNews);
	}
	
	private void createEvents() 
	{
		menuItemGeguInfo.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {

				showGeGuInfoWin ();
			}
			
		});
		
		menuItemAddNews.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {

				addGeGuNews ();
			}
			
		});
		
		menuItemLongTou.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {

				setBanKuaiLongTou ();
			}
			
		});
		
		menuItemAddGz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addGuanZhu ();
			}
			
		});
		
		this.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) 
        	{
        		tableMouseClickActions (arg0);
        	}
        });

		
	}
	
	protected void setBanKuaiLongTou() 
	{
		int row = this.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个股票","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		int  model_row = this.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
		StockOfBanKuai stockofbankuai = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel) this.getModel()).getNode(model_row);
	
		stockofbankuai.setBkLongTou(!stockofbankuai.isBkLongTou());

		BanKuai bk = (BanKuai)((BanKuaiGeGuBasicTableModel)this.getModel()).getCurDispalyBandKuai();
		bkdbopt.setBanKuaiLongTou (bk,stockofbankuai.getMyOwnCode(),stockofbankuai.isBkLongTou());
	}

	protected void showGeGuInfoWin() 
	{
		 int  view_row = this.getSelectedRow();
		 int  model_row = this.convertRowIndexToModel(view_row);//将视图中的行索引转化为数据模型中的行索引
		 
		 StockOfBanKuai stockofbk = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)this.getModel()).getNode(model_row);
		 this.stockmanager.getcBxstockcode().updateUserSelectedNode(stockofbk.getStock() );
		 this.stockmanager.toFront();
	}

	private void tableMouseClickActions (MouseEvent arg0)
	{

        		 int  view_row = this.rowAtPoint(arg0.getPoint()); //获得视图中的行索引
				 int  view_col = this.columnAtPoint(arg0.getPoint()); //获得视图中的列索引
				 int  model_row = this.convertRowIndexToModel(view_row);//将视图中的行索引转化为数据模型中的行索引
				 int  model_col = this.convertColumnIndexToModel(view_col);//将视图中的列索引转化为数据模型中的列索引
				 
        		if (arg0.getClickCount() == 1) {
        		}
        		 if (arg0.getClickCount() == 2) {
//					 int  view_row = tablebkgegu.rowAtPoint(arg0.getPoint()); //获得视图中的行索引
//					 int  view_col = tablebkgegu.columnAtPoint(arg0.getPoint()); //获得视图中的列索引
//					 int  model_row = tablebkgegu.convertRowIndexToModel(view_row);//将视图中的行索引转化为数据模型中的行索引
//					 int  model_col = tablebkgegu.convertColumnIndexToModel(view_col);//将视图中的列索引转化为数据模型中的列索引

        			 //int column = tblSearchResult.getSelectedColumn();
					 //String stockcode = tblSearchResult.getModel().getValueAt(row, 0).toString().trim();
//					 String stockcode = this.getModel().getValueAt(model_row, 0).toString().trim();
////					 logger.debug(stockcode);
//					 this.stockmanager.getcBxstockcode().setSelectedItem(stockcode);
//					 this.stockmanager.preUpdateSearchResultToGui(stockcode);
//					 this.stockmanager.toFront();
       					 
        			 showGeGuInfoWin ();
        				
				 }

	}
	/*
	 * 
	 */
	protected void addGuanZhu() 
	{
		int row = this.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个股票","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		int  model_row = this.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
		StockOfBanKuai stockofbankuai = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel) this.getModel()).getNode(model_row);

		LocalDate fxdate = ((BanKuaiGeGuBasicTableModel)this.getModel()).getCurDisplayedDate();
		
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		WeeklyFenXiWizard ggfx = new WeeklyFenXiWizard ( stockofbankuai.getStock(),fxdate);
    	ggfx.setSize(new Dimension(1400, 800));
    	ggfx.setModalityType(Dialog.ModalityType.APPLICATION_MODAL); // prevent user from doing something else
    	ggfx.setLocationRelativeTo(null);
    	
    	Toolkit.getDefaultToolkit().beep();
    	
    	if(!ggfx.isVisible() ) 
    		ggfx.setVisible(true);
    	ggfx.toFront();
    	
    	ggfx = null;
    	
    	Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(hourglassCursor2);
	
	}
	
	protected void addGeGuNews() 
	{
		int row = this.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个股票","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		int  model_row = this.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
		StockOfBanKuai stockofbk = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel) this.getModel()).getNode (model_row);
		Stock stock = stockofbk.getStock();
		
		JDialogForTagSearchMatrixPanelForAddSysNewsToNode newlog 
			= new JDialogForTagSearchMatrixPanelForAddSysNewsToNode (  stock);
	    newlog.setModal(true);
	    newlog.setVisible(true);

	}

	
	public void removeRows () 
	{
		BanKuaiGeGuBasicTableModel model = (BanKuaiGeGuBasicTableModel)this.getModel(); 
		int rows = model.getRowCount(); 
		for(int i = rows - 1; i >=0; i--)
		{
		   model.removeRow(i); 
		}
	}
	
}
