package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Color;   
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
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

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class BanKuaiGeGuTable extends JTable 
{ 
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(BanKuaiGeGuTable.class);
	private StockInfoManager stockmanager;
	private BanKuaiGeGuTableRenderer renderer;
	private JMenuItem menuItemAddNews;
	private JMenuItem menuItemAddGz;
	private JMenuItem menuItemReDian;
	private JMenuItem menuItemMakeLongTou;
	
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
//		int columnIndexToSort = 3; //��������ռ������
//		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
//		sorter.setSortKeys(sortKeys);
//		sorter.sort();
		
		this.bkdbopt = new BanKuaiDbOperation ();
		this.stockmanager = stockmanager1;
		
		createMenu ();
		createEvents ();
	}
	
	private void createMenu() 
	{
		JPopupMenu popupMenuGeguNews = new JPopupMenu();
		menuItemAddNews = new JMenuItem("��Ӹ�������");
		menuItemAddGz = new JMenuItem("��ʼ��ע");
		menuItemReDian = new JMenuItem("���Ϊ����ȵ�");
		menuItemMakeLongTou = new JMenuItem("���ù�Ʊ���Ȩ��");
		popupMenuGeguNews.add(menuItemAddNews);
		popupMenuGeguNews.add(menuItemMakeLongTou);
		popupMenuGeguNews.add(menuItemAddGz);
		popupMenuGeguNews.add(menuItemReDian);
				
		this.setComponentPopupMenu(popupMenuGeguNews);
	}

	public TableCellRenderer getCellRenderer(int row, int column) 
	{
		return renderer;
	}
	public void sortByParsedFile ()
	{
		TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)this.getRowSorter();
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int columnIndexToSort = 6; //��������ռ������
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
	}
	public void sortByZhanBiGrowthRate ()
	{
		TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)this.getRowSorter();
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int columnIndexToSort = 4; //��������ռ������
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
	}
	
	private BanKuaiDbOperation bkdbopt;
//	private boolean youxianxianshiparsefile;
	
	private void createEvents() 
	{

		menuItemAddNews.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {

				addGeGuNews ();
			}
			
		});
		
//		menuItemMakeLongTou.setComponentPopupMenu(popupMenuGeguNews);
		menuItemMakeLongTou.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setGeGuWeightInBanKuai ();
			}
			
		});
		
		menuItemAddGz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addGuanZhu ();
			}
			
		});
		
		menuItemReDian.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addReDian();
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
	
	
	private void tableMouseClickActions (MouseEvent arg0)
	{

        		int  view_row = this.rowAtPoint(arg0.getPoint()); //�����ͼ�е�������
				 int  view_col = this.columnAtPoint(arg0.getPoint()); //�����ͼ�е�������
				 int  model_row = this.convertRowIndexToModel(view_row);//����ͼ�е�������ת��Ϊ����ģ���е�������
				 int  model_col = this.convertColumnIndexToModel(view_col);//����ͼ�е�������ת��Ϊ����ģ���е�������
				 
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
//					 int  view_row = tablebkgegu.rowAtPoint(arg0.getPoint()); //�����ͼ�е�������
//					 int  view_col = tablebkgegu.columnAtPoint(arg0.getPoint()); //�����ͼ�е�������
//					 int  model_row = tablebkgegu.convertRowIndexToModel(view_row);//����ͼ�е�������ת��Ϊ����ģ���е�������
//					 int  model_col = tablebkgegu.convertColumnIndexToModel(view_col);//����ͼ�е�������ת��Ϊ����ģ���е�������
					 
					 
					 int row = this.getSelectedRow();
					 //int column = tblSearchResult.getSelectedColumn();
					 //String stockcode = tblSearchResult.getModel().getValueAt(row, 0).toString().trim();
					 String stockcode = this.getModel().getValueAt(model_row, 0).toString().trim();
//					 logger.debug(stockcode);
					 this.stockmanager.getcBxstockcode().setSelectedItem(stockcode);
					 this.stockmanager.preUpdateSearchResultToGui(stockcode);
					 this.stockmanager.toFront();
				 }

	}

//	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) 
//	{
//		        Component comp = super.prepareRenderer(renderer, row, col);
//		        BanKuaiGeGuTableModel tablemodel = (BanKuaiGeGuTableModel)this.getModel(); 
//		        HashSet<String> stockinparsefile = tablemodel.getStockInParseFile();
//		        
//		        if(stockinparsefile == null)
//		        	return comp;
//		        
//		        //������ʾ����ʾΪ%
//		        if (comp instanceof JLabel && (col == 3 ||  col == 5)) {
//	            	String value =  ((JLabel)comp).getText();
//	            	String valuepect = null;
//	            	try {
//	            		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(value).doubleValue();
//	            		 
//	            		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
//	 	    	    	 percentFormat.setMinimumFractionDigits(1);
//		            	 valuepect = percentFormat.format (formatevalue );
//	            	} catch (java.lang.NumberFormatException e)   	{
//	            		e.printStackTrace();
//	            	} catch (ParseException e) {
//						e.printStackTrace();
//					}
//	            	((JLabel)comp).setText(valuepect);
//		        }
//		        
//		        if (!isRowSelected(row)) {
//		        	comp.setBackground(getBackground());
//		        	comp.setForeground(getForeground());
//		        	int modelRow = convertRowIndexToModel(row);
//		        	String stockcode = (String)tablemodel.getValueAt(modelRow, 0);
//					if(stockinparsefile.contains(stockcode)) {
//						comp.setForeground(Color.BLUE);
//					}
//		        }
//		        
//		        return comp;
//	}
		


	
//	public String getToolTipText(MouseEvent e) 
//	{
//        String tip = null;
//        java.awt.Point p = e.getPoint();
//        int rowIndex = rowAtPoint(p);
//        int colIndex = columnAtPoint(p);
//
//        try {
//            tip = getValueAt(rowIndex, colIndex).toString();
//        } catch (java.lang.NullPointerException e2) {
//        	tip = "";
//        } catch (RuntimeException e1) {
//        	e1.printStackTrace();
//        }
//        return tip;
//    } 

	protected void addReDian() 
	{
		int row = this.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String stockcode = ((BanKuaiGeGuTableModel) this.getModel()).getStockCode (row);
		
		JiaRuJiHua jiarujihua = new JiaRuJiHua (stockcode,"�����ȵ�" ); 
		int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "����Ϊ�����ȵ�", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
			return;
		
		int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
		
	}
	
	protected void addGuanZhu() 
	{
		int row = this.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String stockcode = ((BanKuaiGeGuTableModel) this.getModel()).getStockCode (row);
		
		JiaRuJiHua jiarujihua = new JiaRuJiHua (stockcode,"�����ע" ); 
		int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "�ƻ�ϸ��", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
			return;
		
		int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
	
	}
	
	protected void addGeGuNews() 
	{
		int row = this.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String stockcode = ((BanKuaiGeGuTableModel) this.getModel()).getStockCode (row);
		ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (stockcode);
		int exchangeresult = JOptionPane.showConfirmDialog(null, cylnews, "���Ӹ�������", JOptionPane.OK_CANCEL_OPTION);
		System.out.print(exchangeresult);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
			return;
		
		bkdbopt.addBanKuaiNews(stockcode, cylnews.getInputedNews());
		
	}
	
	/*
     * ���øð����ɵ�Ȩ��
     */
	private void setGeGuWeightInBanKuai()
    {
		int row = this.getSelectedRow();
		if(row < 0)
			return;
		int modelRow = this.convertRowIndexToModel(row);
		
//		BkChanYeLianTreeNode curselectedbknode = (BkChanYeLianTreeNode) treechanyelian.getLastSelectedPathComponent();
		BanKuai bkcode = ((BanKuaiGeGuTableModel)(this.getModel())).getTdxBkCode();
//		String bkname = ((BanKuaiGeGuTableModel)(this.getModel())).getTdxBkName();
		String stockcode = ((BanKuaiGeGuTableModel)(this.getModel())).getStockCode(modelRow);
		int weight = ((BanKuaiGeGuTableModel)(this.getModel())).getStockCurWeight (modelRow);
		
		String weightresult = JOptionPane.showInputDialog(null,"�������Ʊ�ڸð��Ȩ�أ�ע��Ȩ�ز��ܴ���10��",weight);
		int newweight = Integer.parseInt(weightresult);
		if(newweight>10)
			JOptionPane.showMessageDialog(null,"Ȩ��ֵ���ܳ���10��","Warning",JOptionPane.WARNING_MESSAGE);
		
		if(weight != newweight) {
			bkdbopt.setStockWeightInBanKuai (bkcode,"",stockcode,newweight);
			( (BanKuaiGeGuTableModel)this.getModel() ).setStockCurWeight (modelRow,newweight);
		}
	}
	
	public void hideZhanBiColumn (int hidecolumn) 
	{
		TableColumnModel tcm = this.getColumnModel();
		if(hidecolumn >1) {
			//�ڰ��������治��Ҫ3��column
			this.removeColumn(tcm.getColumn(3));
			this.removeColumn(tcm.getColumn(3));
			this.removeColumn(tcm.getColumn(3));
			this.removeColumn(tcm.getColumn(3));

		} 
//		else if (hidecolumn == 1) {
//			this.removeColumn(tcm.getColumn(6));
//		}
	}
	
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


class ToolTipHeader extends JTableHeader {
    String[] toolTips;
   
    public ToolTipHeader(TableColumnModel model) {
      super(model);
    }
     
    public String getToolTipText(MouseEvent e) {
      int col  = columnAtPoint(e.getPoint());
      int modelCol = getTable().convertColumnIndexToModel(col);
      String retStr;
      try {
        retStr = toolTips[modelCol];
      } catch (NullPointerException ex) {
        retStr = "";
      } catch (ArrayIndexOutOfBoundsException ex) {
        retStr = "";
      }
      if (retStr.length() < 1) {
        retStr = super.getToolTipText(e);
      }
      return retStr;
    }  
     
    public void setToolTipStrings(String[] toolTips) {
      this.toolTips = toolTips;
    }
}

