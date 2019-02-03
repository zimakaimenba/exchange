package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Color;   
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;
import com.exchangeinfomanager.bankuaifengxi.BarChartHightLightFxDataValueListener;
import com.exchangeinfomanager.bankuaifengxi.ai.WeeklyFenXiWizard;
import com.exchangeinfomanager.commonlib.ToolTipHeader;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class BanKuaiGeGuTable extends JTable implements BarChartHightLightFxDataValueListener
{ 
	public BanKuaiGeGuTable (StockInfoManager stockmanager1)
	{
		super ();
		
		BanKuaiGeGuTableModel bkgegumapmdl = new BanKuaiGeGuTableModel();
		this.setModel(bkgegumapmdl);
		this.renderer =  new BanKuaiGeGuTableRenderer (); 
//		this.setDefaultRenderer(Object.class, this.renderer );
		
		//http://esus.com/creating-a-jtable-with-headers-with-jtooltips/
		ToolTipHeader header = new ToolTipHeader(this.getColumnModel() );
	    header.setToolTipStrings(bkgegumapmdl.getTableHeader());
	    header.setToolTipText("Default ToolTip TEXT");
	    this.setTableHeader(header);
		
		//sort http://www.codejava.net/java-se/swing/6-techniques-for-sorting-jtable-you-should-know
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.getModel());
		this.setRowSorter(sorter);
//		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
//		int columnIndexToSort = 3; //优先排序占比增长
//		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
//		sorter.setSortKeys(sortKeys);
//		sorter.sort();
		
		this.bkdbopt = new BanKuaiDbOperation ();
		this.newsdbopt = new StockCalendarAndNewDbOperation ();
		this.stockmanager = stockmanager1;
		
		createMenu ();
		createEvents ();
	}
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(BanKuaiGeGuTable.class);
	private StockInfoManager stockmanager;
	private BanKuaiGeGuTableRenderer renderer;
	private JMenuItem menuItemAddNews;
	private JMenuItem menuItemAddGz;
//	private JMenuItem menuItemReDian;
	private JMenuItem menuItemQuanZhong;
	private JMenuItem menuItemGeguInfo;
	private StockCalendarAndNewDbOperation newsdbopt;
	private BanKuaiDbOperation bkdbopt;
	private JPopupMenu popupMenuGeguNews;

//	public void addNewMenuItem (JMenuItem newmenuitem)
//	{
//		this.popupMenuGeguNews.add(newmenuitem);
//	}
	
	private void createMenu() 
	{
		popupMenuGeguNews = new JPopupMenu();
		menuItemGeguInfo = new JMenuItem("个股信息");
		menuItemAddNews = new JMenuItem("添加个股新闻");
		menuItemAddGz = new JMenuItem("个股分析");
//		menuItemReDian = new JMenuItem("标记龙头个股");
		menuItemQuanZhong = new JMenuItem("设置股票板块权重");
		popupMenuGeguNews.add(menuItemAddNews);
		popupMenuGeguNews.add(menuItemQuanZhong);
		popupMenuGeguNews.add(menuItemAddGz);
		popupMenuGeguNews.add(menuItemGeguInfo);
//		popupMenuGeguNews.add(menuItemReDian);
				
		this.setComponentPopupMenu(popupMenuGeguNews);
	}
	
	public JPopupMenu getPopupMenu ()
	{
		return this.popupMenuGeguNews;
	}
	
	public TableCellRenderer getCellRenderer(int row, int column) 
	{
		return renderer;
	}
	public void sortByParsedFile ()
	{
		TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)this.getRowSorter();
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int columnIndexToSort = 6; //优先排序占比增长
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
	}
	public void sortByZhanBiGrowthRate ()
	{
		TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)this.getRowSorter();
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int columnIndexToSort = 4; //优先排序占比增长
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
		
		sortKeys = null;
	}
	@Override
	public void hightLightFxValues(Integer cjezbdpmax, Integer cjezbbkmax, Double cjemin, Double cjemax, Integer cjemaxwk,Double showhsl) 
	{
//		if(cjezbbkmax != null)
			((BanKuaiGeGuTableModel)this.getModel()).setDisplayCjeBKMaxWk( cjezbbkmax);
//		if(cjemaxwk != null )
			((BanKuaiGeGuTableModel)this.getModel()).setDisplayCjeMaxWk (cjemaxwk);
//		if(cjezbdpmax != null)
			((BanKuaiGeGuTableModel)this.getModel()).setDisplayCjeDPMaxWk (cjezbdpmax);
//		if(cjemin != null || cjemax != null)
			((BanKuaiGeGuTableModel)this.getModel()).setDisplayChenJiaoEr (cjemin,cjemax);
//		if(showhsl != null)
			((BanKuaiGeGuTableModel)this.getModel()).setDisplayHuanShouLv(showhsl);
		
		this.repaint();
	}
	@Override
	public void hightLightFxValues(Integer cjezbtoupleveldpmax, Double cjemin, Double cjemax, Integer cjemaxwk, Double shoowhsl) {
		// TODO Auto-generated method stub
		
	}
	
    public String getToolTipText(MouseEvent e) {
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);

        try {
        	if(colIndex == 2) { //权重column的tip要具体
				org.jsoup.nodes.Document doc = Jsoup.parse("");
				org.jsoup.select.Elements content = doc.select("body");
				content.append( "5:主业且不亏损<br>" );
				content.append( "4:主业且亏损<br>" );
				content.append( "3:营收占比很大<br>" );
				content.append( "2:营收占比很小<br>" );
				content.append( "1:营收占比几乎没有概念阶段<br>" );
				content.append( "0:毫无关系<br>" );
				
				tip = doc.toString();
        	} else
        		tip = getValueAt(rowIndex, colIndex).toString();
        } catch (RuntimeException e1) {
            //catch null pointer exception if mouse is over an empty line
        }

        return tip;
        
        
        
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
		
//		menuItemMakeLongTou.setComponentPopupMenu(popupMenuGeguNews);
		menuItemQuanZhong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setGeGuWeightInBanKuai ();
			}
			
		});
		
		menuItemAddGz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addGuanZhu ();
			}
			
		});
		
//		menuItemReDian.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent evt) {
//				addReDian();
//			}
//			
//		});
		
		
		this.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) 
        	{
        		tableMouseClickActions (arg0);
        	}
        });

		
	}
	
	
	protected void showGeGuInfoWin() 
	{
		 int  view_row = this.getSelectedRow();
		 int  model_row = this.convertRowIndexToModel(view_row);//将视图中的行索引转化为数据模型中的行索引
		 
		 String stockcode = this.getModel().getValueAt(model_row, 0).toString().trim();
		 this.stockmanager.getcBxstockcode().setSelectedItem(stockcode);
		 this.stockmanager.preUpdateSearchResultToGui(stockcode);
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
					 String stockcode = this.getModel().getValueAt(model_row, 0).toString().trim();
//					 logger.debug(stockcode);
					 this.stockmanager.getcBxstockcode().setSelectedItem(stockcode);
					 this.stockmanager.preUpdateSearchResultToGui(stockcode);
					 this.stockmanager.toFront();
				 }

	}
//
//	protected void addReDian() 
//	{
//		int row = this.getSelectedRow();
//		if(row <0) {
//			JOptionPane.showMessageDialog(null,"请选择一个股票","Warning",JOptionPane.WARNING_MESSAGE);
//			return;
//		}
//		
//		int  model_row = this.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
////		 int  model_col = this.convertColumnIndexToModel(view_col);//将视图中的列索引转化为数据模型中的列索引
//		
//		String stockcode = ((BanKuaiGeGuTableModel) this.getModel()).getStockCode (model_row);
//		
//		JiaRuJiHua jiarujihua = new JiaRuJiHua (stockcode,"龙头个股" ); 
//		int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "龙头个股", JOptionPane.OK_CANCEL_OPTION);
//		if(exchangeresult == JOptionPane.CANCEL_OPTION)
//			return;
//		
////		int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
//		InsertedMeeting insetmeeting = newsdbopt.setReDianBanKuaiLongTouGeGuToShangYeXinWen(jiarujihua);
//		
//	}
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
		StockOfBanKuai stockofbankuai = ((BanKuaiGeGuTableModel) this.getModel()).getStock(model_row);

		LocalDate fxdate = ((BanKuaiGeGuTableModel)this.getModel()).getShowCurDate();
		
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
		String stockcode = ((BanKuaiGeGuTableModel) this.getModel()).getStockCode (model_row);
		ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (stockcode);
		cylnews.setVisible(true);
//		ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (stockcode);
//		int exchangeresult = JOptionPane.showConfirmDialog(null, cylnews, "增加个股新闻", JOptionPane.OK_CANCEL_OPTION);
//		System.out.print(exchangeresult);
//		if(exchangeresult == JOptionPane.CANCEL_OPTION)
//			return;
		
//		bkdbopt.newsdbopt(stockcode, cylnews.getInputedNews());
	}
	/*
     * 设置该板块个股的权重
     */
	private void setGeGuWeightInBanKuai()
    {
		int row = this.getSelectedRow();
		if(row < 0)
			return;
		int modelRow = this.convertRowIndexToModel(row);
		
//		BkChanYeLianTreeNode curselectedbknode = (BkChanYeLianTreeNode) treechanyelian.getLastSelectedPathComponent();
		BanKuai bkcode = ((BanKuaiGeGuTableModel)this.getModel()).getCurDispalyBandKuai();
		String stockcode = ((BanKuaiGeGuTableModel)(this.getModel())).getStockCode(modelRow);
		int weight = ((BanKuaiGeGuTableModel)(this.getModel())).getStockCurWeight (modelRow);
		
		String weightresult = JOptionPane.showInputDialog(null,"请输入股票在该板块权重,权重值不能超过5！\n\r"
											+ "5:主业且不亏损,\n\r 4:主业并亏损,\n\r3:营收占比很大,\n\r2:营收占比很小,\n\r1:营收占比几乎没有概念阶段,\n\r0:毫无关系"
				,weight);
		try {
			int newweight = Integer.parseInt(weightresult);
			if(newweight>5)
				JOptionPane.showMessageDialog(null,"权重值不能超过5！\n\r"
												+ "5:主业且不亏损,4:主业并亏损,3:营收占比很大,2:营收占比很小,1:营收占比几乎没有概念阶段,0:毫无关系"	
						,"Warning",JOptionPane.WARNING_MESSAGE);
			
			if(weight != newweight) {
				bkdbopt.setStockWeightInBanKuai (bkcode,"",stockcode,newweight);
				( (BanKuaiGeGuTableModel)this.getModel() ).setStockCurWeight (modelRow,newweight);
			}
		} catch (java.lang.NumberFormatException e) {
			return;
		}
	}
	
//	public void hideZhanBiColumn (int hidecolumn) 
//	{
//		TableColumnModel tcm = this.getColumnModel();
//		if(hidecolumn >1) {
//			//在板块分析界面不需要3个column
//			this.removeColumn(tcm.getColumn(3));
//			this.removeColumn(tcm.getColumn(3));
//			this.removeColumn(tcm.getColumn(3));
//			this.removeColumn(tcm.getColumn(3));
//
//		} 
////		else if (hidecolumn == 1) {
////			this.removeColumn(tcm.getColumn(6));
////		}
//	}
	
	public void removeRows () 
	{
		BanKuaiGeGuTableModel model = (BanKuaiGeGuTableModel)this.getModel(); 
		int rows = model.getRowCount(); 
		for(int i = rows - 1; i >=0; i--)
		{
		   model.removeRow(i); 
		}
	}

	



}




