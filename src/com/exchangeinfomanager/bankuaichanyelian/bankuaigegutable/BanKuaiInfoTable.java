package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
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

import org.apache.log4j.Logger;

import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.bankuaifengxi.BarChartHightLightFxDataValueListener;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.ToolTipHeader;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.treerelated.BanKuaiTreeRelated;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiInfoTable extends JTable implements BarChartHightLightFxDataValueListener
{
	private BanKuaiPopUpMenu popupMenuGeguNews;
	private static final long serialVersionUID = 1L;
	private BanKuaiDbOperation bkdbopt;
	private SystemConfigration sysconfig;
	private StockInfoManager stockmanager;
//	private AllCurrentTdxBKAndStoksTree allbkgg;
	private BanKuaiAndChanYeLian2 bkcyl;
	private static Logger logger = Logger.getLogger(BanKuaiInfoTable.class);

	public BanKuaiInfoTable(StockInfoManager stockmanager1) 
	{
		super ();
		BanKuaiInfoTableModel bkmodel = new BanKuaiInfoTableModel ();
		this.setModel(bkmodel);
//		this.allbkgg = AllCurrentTdxBKAndStoksTree.getInstance();
		this.bkcyl = BanKuaiAndChanYeLian2.getInstance();
		
		this.createEvents ();
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.getModel());
		this.setRowSorter(sorter);
		this.sortByZhanBiGrowthRate ();
		
		ToolTipHeader header = new ToolTipHeader(this.getColumnModel() );
	    header.setToolTipStrings(bkmodel.getTableHeader());
	    header.setToolTipText("Default ToolTip TEXT");
	    this.setTableHeader(header);
	    
//		this.bkdbopt = new BanKuaiDbOperation ();
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
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JTable#getToolTipText(java.awt.event.MouseEvent)
	 */
    public String getToolTipText(MouseEvent e) {
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);

        try {
            tip = getValueAt(rowIndex, colIndex).toString();
        } catch (RuntimeException e1) {
            //catch null pointer exception if mouse is over an empty line
        }

        return tip;
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
		
		popupMenuGeguNews = new BanKuaiPopUpMenuForTable(this.stockmanager,this);
		this.setComponentPopupMenu(popupMenuGeguNews);
	}
	/*
	 * 
	 */
	public BanKuaiPopUpMenu getPopupMenu ()
	{
		return this.popupMenuGeguNews;
	}
	/*
	 * 
	 */
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
					 
					 
//					 int row = this.getSelectedRow();
					 //int column = tblSearchResult.getSelectedColumn();
					 //String stockcode = tblSearchResult.getModel().getValueAt(row, 0).toString().trim();
					 String stockcode = this.getModel().getValueAt(model_row, 0).toString().trim();
//					 logger.debug(stockcode);
					 this.stockmanager.getcBxstockcode().setSelectedItem(stockcode);
					 this.stockmanager.preUpdateSearchResultToGui(stockcode);
					 this.stockmanager.toFront();
				 }

	}
	@Override
	public void hightLightFxValues(Integer cjezbdpmax, Integer cjezbbkmax, Double cjemin, Double cjemax, Integer cjemaxwk,Double showhsl,Double ltszmin,Double ltszmax) 
	{
//		((BanKuaiInfoTableModel)this.getModel()).setDisplayCjeBKMaxWk( cjezbbkmax);
//		((BanKuaiInfoTableModel)this.getModel()).setDisplayCjeMaxWk (cjemaxwk);
//		((BanKuaiInfoTableModel)this.getModel()).setDisplayCjeDPMaxWk (cjezbdpmax);
//		((BanKuaiInfoTableModel)this.getModel()).setDisplayChenJiaoEr (cjemin,cjemax);
//		((BanKuaiInfoTableModel)this.getModel()).setDisplayHuanShouLv(showhsl);
		
//		this.repaint();
	}
	@Override
	public void hightLightFxValues(Integer cjezbtoupleveldpmaxwk, Double cjemin, Double cjemax, Integer cjemaxwk,
			Double shoowhsl) {
		// TODO Auto-generated method stub
		
	}
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
	 */
	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
			 
	        Component comp = super.prepareRenderer(renderer, row, col);
	        BanKuaiInfoTableModel tablemodel = (BanKuaiInfoTableModel)this.getModel(); 
	        if(tablemodel.getRowCount() == 0) {
	        	return null;
	        }
	        LocalDate curdate = tablemodel.getCurDisplayedDate();
	        
	        int modelRow = convertRowIndexToModel(row);
	        BanKuai bankuai = tablemodel.getBanKuai(modelRow);
	        
	        String bktype = bankuai.getBanKuaiLeiXing();
	        if(bktype.equals(BanKuai.NOGGWITHSELFCJL)) {
	        	Font defaultFont = this.getFont();
	        	Font font = new Font(defaultFont.getName(),Font.ITALIC,defaultFont.getSize());
	        	comp.setFont(font);
	        }
	        
	        //更改显示
	        if (comp instanceof JLabel && (col == 2 ||  col == 4)) {
            	String value =  ((JLabel)comp).getText();
            	if(value == null || value.length() == 0)
            		return null;
            	
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
	        
	        //为不同情况突出显示不同的颜色
	        Color foreground, background = Color.white;

	        if( col == 1 && !bktype.equals(BanKuai.NOGGWITHSELFCJL) ) {
	        	NodesTreeRelated tmptreerelated = this.bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode(bankuai.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK).getNodeTreeRelated();
//	        	TreeRelated tmptreerelated = bankuai.getNodeTreerelated (); 
	        	Integer patchfilestocknum = ((BanKuaiTreeRelated)tmptreerelated).getStocksNumInParsedFileForSpecificDate (curdate);
	        	Boolean selfisin = ((BanKuaiTreeRelated)tmptreerelated).selfIsMatchModel (curdate);
	        	 
	        	if(patchfilestocknum != null && patchfilestocknum > 0 )
		        	background = Color.ORANGE;
		        else
		        	background = Color.white;
	        	
//	        	if(selfisin) {//板块自身满足模型,用粗体
//		        	 Font font = new Font("黑体",Font.BOLD + Font.ITALIC,14);
//		        	 ((JLabel)comp).setFont(font);
//		         } else {
//		        	 Font font=new Font("宋体",Font.PLAIN,14); 
//		        	 ((JLabel)comp).setFont(font);
//		         }
	        }
	        
//	        if(col == 2 || col == 3 || col == 4 ) {
//	        	if(bankuai.getNodeTreerelated().getInZdgzCandidateCount() >0 || bankuai.getNodeTreerelated().getInZdgzOfficalCount() >0)
//	        		background = Color.RED;
//		        else
//		        	background = Color.white;
//	        }
	        
	        if (!this.isRowSelected(row)) 
		    	comp.setBackground(background);
	        
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
