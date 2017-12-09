package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BanKuaiPopUpMenu;
import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiInfoTable extends JTable 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BanKuaiDbOperation bkdbopt;
	private SystemConfigration sysconfig;
	private StockInfoManager stockmanager;

	public BanKuaiInfoTable(StockInfoManager stockmanager1) 
	{
		super ();
		BanKuaiInfoTableModel bkmodel = new BanKuaiInfoTableModel ();
		this.setModel(bkmodel);
		this.createEvents ();
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.getModel());
		this.setRowSorter(sorter);
		this.sortByZhanBiGrowthRate ();
		this.bkdbopt = new BanKuaiDbOperation ();
		this.sysconfig = SystemConfigration.getInstance();
		this.stockmanager = stockmanager1;
	}
	/*
	 * 
	 */
	private void sortByZhanBiGrowthRate ()
	{
		TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)this.getRowSorter();
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int columnIndexToSort = 2; //优先排序占比增长
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
	}
	
	private void createEvents ()
	{
		this.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) 
        	{
        		tableMouseClickActions (arg0);
        	}
        });
		
		JPopupMenu popupMenuGeguNews = new BanKuaiPopUpMenu(this);
		
//		JPopupMenu popupMenuGeguNews = new JPopupMenu();
//		JMenuItem menuItemAddNews = new JMenuItem("添加板块新闻");
//		JMenuItem menuItemAddToGz = new JMenuItem("加入关注");
//		JMenuItem menuItemMakeLongTou = new JMenuItem("设置为当周热点");
//		
//		popupMenuGeguNews.add(menuItemAddNews);
//		popupMenuGeguNews.add(menuItemMakeLongTou);
//		popupMenuGeguNews.add(menuItemAddToGz);
		
		this.setComponentPopupMenu(popupMenuGeguNews);
		
//		menuItemAddNews.addActionListener(new ActionListener() {
//			@Override
//
//			public void actionPerformed(ActionEvent evt) {
//
//				addBanKuaiNews ();
//			}
//			
//		});
//		
//		menuItemMakeLongTou.addActionListener(new ActionListener() {
//			@Override
//
//			public void actionPerformed(ActionEvent evt) {
//
//				addBanKuaiReDian ();
//			}
//			
//		});
//		
//		menuItemAddToGz.addActionListener(new ActionListener() {
//			@Override
//
//			public void actionPerformed(ActionEvent evt) {
//
//				addZdgz ();
//			}
//			
//		});



	}
	
	private void tableMouseClickActions (MouseEvent arg0)
	{

        		int  view_row = this.rowAtPoint(arg0.getPoint()); //获得视图中的行索引
				 int  view_col = this.columnAtPoint(arg0.getPoint()); //获得视图中的列索引
				 int  model_row = this.convertRowIndexToModel(view_row);//将视图中的行索引转化为数据模型中的行索引
				 int  model_col = this.convertColumnIndexToModel(view_col);//将视图中的列索引转化为数据模型中的列索引
				 
        		if (arg0.getClickCount() == 1) {
//        			int row = this.getSelectedRow();
//					 //int column = tblSearchResult.getSelectedColumn();
//					 //String stockcode = tblSearchResult.getModel().getValueAt(row, 0).toString().trim();
//					 String stockcode = ((BanKuaiGeGuTableModel)this.getModel()).getValueAt(model_row, 0).toString().trim();
//					 try {
//						 String stockname = ((BanKuaiGeGuTableModel)this.getModel()).getValueAt(model_row, 1).toString().trim();
//						 pnlGeGuZhanBi.hightlightSpecificSector (stockcode+stockname);
//					 } catch ( java.lang.NullPointerException e) {
//						 pnlGeGuZhanBi.hightlightSpecificSector (stockcode);
//					 }

						 
        		}
        		 if (arg0.getClickCount() == 2) {
//					 int  view_row = tablebkgegu.rowAtPoint(arg0.getPoint()); //获得视图中的行索引
//					 int  view_col = tablebkgegu.columnAtPoint(arg0.getPoint()); //获得视图中的列索引
//					 int  model_row = tablebkgegu.convertRowIndexToModel(view_row);//将视图中的行索引转化为数据模型中的行索引
//					 int  model_col = tablebkgegu.convertColumnIndexToModel(view_col);//将视图中的列索引转化为数据模型中的列索引
					 
					 
					 int row = this.getSelectedRow();
					 //int column = tblSearchResult.getSelectedColumn();
					 //String stockcode = tblSearchResult.getModel().getValueAt(row, 0).toString().trim();
					 String stockcode = this.getModel().getValueAt(model_row, 0).toString().trim();
//					 System.out.println(stockcode);
					 this.stockmanager.getcBxstockcode().setSelectedItem(stockcode);
					 this.stockmanager.preUpdateSearchResultToGui(stockcode);
					 this.stockmanager.toFront();
				 }

	}

//	protected void addZdgz() 
//	{
//		int row = this.getSelectedRow();
//		if(row <0) {
//			JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
//			return;
//		}
//		int modelRow = this.convertRowIndexToModel(row); 
//		String bkcode = ((BanKuaiInfoTableModel) this.getModel()).getBanKuaiCode(modelRow);
//		String bkname = ((BanKuaiInfoTableModel) this.getModel()).getBanKuaiName(modelRow);
//		
//		JiaRuJiHua jiarujihua = new JiaRuJiHua ( bkcode,"加入关注" ); 
//		int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "加入关注", JOptionPane.OK_CANCEL_OPTION);
//		if(exchangeresult == JOptionPane.CANCEL_OPTION)
//			return;
//		
//		int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
//		
//	}
//	protected void addBanKuaiReDian()
//	{
//		int row = this.getSelectedRow();
//		if(row <0) {
//			JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
//			return;
//		}
//		int modelRow = this.convertRowIndexToModel(row); 
//		String bkcode = ((BanKuaiInfoTableModel) this.getModel()).getBanKuaiCode(modelRow);
//		String bkname = ((BanKuaiInfoTableModel) this.getModel()).getBanKuaiName(modelRow);
//
//		JiaRuJiHua jiarujihua = new JiaRuJiHua ( bkcode,"本周热点" ); 
//		int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "本周热点", JOptionPane.OK_CANCEL_OPTION);
//		if(exchangeresult == JOptionPane.CANCEL_OPTION)
//			return;
//		
//		int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
//	}
//	protected void addBanKuaiNews() 
//	{
//		int row = this.getSelectedRow();
//		if(row <0) {
//			JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
//			return;
//		}
//		int modelRow = this.convertRowIndexToModel(row); 
//		BanKuai bankuai = ((BanKuaiInfoTableModel) this.getModel()).getBanKuai(modelRow);
//		String bkcode = bankuai.getMyOwnCode();
//		ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (bkcode);
//		int exchangeresult = JOptionPane.showConfirmDialog(null, cylnews, "增加板块新闻", JOptionPane.OK_CANCEL_OPTION);
//
//		if(exchangeresult == JOptionPane.CANCEL_OPTION)
//			return;
//		
//		bkdbopt.addBanKuaiNews(bkcode, cylnews.getInputedNews());
//	}
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
	 */
	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
			 
	        Component comp = super.prepareRenderer(renderer, row, col);
	        BanKuaiInfoTableModel tablemodel = (BanKuaiInfoTableModel)this.getModel(); 
	        int modelRow = convertRowIndexToModel(row);
	        BanKuai bankuai = tablemodel.getBanKuai(modelRow);
	        
	        //更改显示
	        if (comp instanceof JLabel && (col == 2 ||  col == 4)) {
            	String value =  ((JLabel)comp).getText();
            	String valuepect = null;
            	try {
            		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(value).doubleValue();
            		 
            		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
 	    	    	 percentFormat.setMinimumFractionDigits(1);
	            	 valuepect = percentFormat.format (formatevalue );
            	} catch (java.lang.NumberFormatException e)   	{
            		e.printStackTrace();
            	} catch (ParseException e) {
					e.printStackTrace();
				}
            	((JLabel)comp).setText(valuepect);
	        }
	        
	        if(bankuai.getParseFileStockSet().size()>0)
	        	comp.setForeground(Color.RED);
	        else
	        	comp.setForeground(Color.black);
	        
	        return comp;
	}
		
//		public String getToolTipText(MouseEvent e) 
//		{
//            String tip = null;
//            java.awt.Point p = e.getPoint();
//            int rowIndex = rowAtPoint(p);
//            int colIndex = columnAtPoint(p);
//
//            try {
//                tip = getValueAt(rowIndex, colIndex).toString();
//            } catch (RuntimeException e1) {
//            	e1.printStackTrace();
//            }
//            return tip;
//        } 
	

	

}
