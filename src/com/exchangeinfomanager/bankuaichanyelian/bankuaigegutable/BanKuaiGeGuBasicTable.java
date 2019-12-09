package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

import com.exchangeinfomanager.TagManagment.JDialogForTagSearchMatrixPanelForAddSysNewsToNode;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsPnl2.TDXNodsInforPnl;
import com.exchangeinfomanager.bankuaifengxi.BarChartHightLightFxDataValueListener;
import com.exchangeinfomanager.bankuaifengxi.ai.WeeklyFenXiWizard;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;

public abstract class BanKuaiGeGuBasicTable extends JTable implements BarChartHightLightFxDataValueListener
{
	public BanKuaiGeGuBasicTable (StockInfoManager stockmanager1)
	{
		super ();

		this.bkdbopt = new BanKuaiDbOperation ();
		this.newsdbopt = new StockCalendarAndNewDbOperation ();
		this.stockmanager = stockmanager1;
				
		createMenu ();
		createEvents ();
		
//		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		this.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(30);
//		this.getColumnModel().getColumn(0).setPreferredWidth(10);
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiGeGuTable.class);
	
	protected StockCalendarAndNewDbOperation newsdbopt;
	protected BanKuaiDbOperation bkdbopt;
	protected StockInfoManager stockmanager;
	private static final long serialVersionUID = 1L;

	protected JMenuItem menuItemAddNews;
	protected JMenuItem menuItemAddGz;
	protected JMenuItem menuItemGeguInfo;
	protected JMenuItem menuItemLongTou;
	protected JPopupMenu popupMenuGeguNews;

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
	public void sortByParsedFile ()
	{
		TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)this.getRowSorter();
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int columnIndexToSort = 6; //��������ռ������
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
	}
	/*
	 * 
	 */
	public void sortByZhanBiGrowthRate ()
	{
		TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)this.getRowSorter();
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int columnIndexToSort = 4; //��������ռ������
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
		
		sortKeys = null;
	}

	
	private void createMenu() 
	{
		popupMenuGeguNews = new JPopupMenu();
		menuItemGeguInfo = new JMenuItem("������Ϣ");
		menuItemAddNews = new JMenuItem("��Ӹ�������");
		menuItemAddGz = new JMenuItem("���ɷ���");
		menuItemLongTou = new JMenuItem("��Ϊ/ȡ�������ͷ");
		
		
		popupMenuGeguNews.add(menuItemGeguInfo);
		popupMenuGeguNews.add(menuItemAddNews);
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
			JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		int  model_row = this.convertRowIndexToModel(row);//����ͼ�е�������ת��Ϊ����ģ���е�������
		StockOfBanKuai stockofbankuai = ((BanKuaiGeGuTableModel) this.getModel()).getStock(model_row);
	
		stockofbankuai.setBkLongTou(!stockofbankuai.isBkLongTou());

		BanKuai bk = ((BanKuaiGeGuTableModel)this.getModel()).getCurDispalyBandKuai();
		bkdbopt.setBanKuaiLongTou (bk,stockofbankuai.getMyOwnCode(),stockofbankuai.isBkLongTou());
	}

	protected void showGeGuInfoWin() 
	{
		 int  view_row = this.getSelectedRow();
		 int  model_row = this.convertRowIndexToModel(view_row);//����ͼ�е�������ת��Ϊ����ģ���е�������
		 
		 StockOfBanKuai stockofbk = ((BanKuaiGeGuBasicTableModel)this.getModel()).getStock(model_row);
		 this.stockmanager.getcBxstockcode().updateUserSelectedNode(stockofbk.getStock() );
		 this.stockmanager.toFront();
	}

	private void tableMouseClickActions (MouseEvent arg0)
	{

        		 int  view_row = this.rowAtPoint(arg0.getPoint()); //�����ͼ�е�������
				 int  view_col = this.columnAtPoint(arg0.getPoint()); //�����ͼ�е�������
				 int  model_row = this.convertRowIndexToModel(view_row);//����ͼ�е�������ת��Ϊ����ģ���е�������
				 int  model_col = this.convertColumnIndexToModel(view_col);//����ͼ�е�������ת��Ϊ����ģ���е�������
				 
        		if (arg0.getClickCount() == 1) {
        		}
        		 if (arg0.getClickCount() == 2) {
//					 int  view_row = tablebkgegu.rowAtPoint(arg0.getPoint()); //�����ͼ�е�������
//					 int  view_col = tablebkgegu.columnAtPoint(arg0.getPoint()); //�����ͼ�е�������
//					 int  model_row = tablebkgegu.convertRowIndexToModel(view_row);//����ͼ�е�������ת��Ϊ����ģ���е�������
//					 int  model_col = tablebkgegu.convertColumnIndexToModel(view_col);//����ͼ�е�������ת��Ϊ����ģ���е�������

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
			JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		int  model_row = this.convertRowIndexToModel(row);//����ͼ�е�������ת��Ϊ����ģ���е�������
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
			JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		int  model_row = this.convertRowIndexToModel(row);//����ͼ�е�������ת��Ϊ����ģ���е�������
		Stock stock = ((BanKuaiGeGuTableModel) this.getModel()).getStock (model_row).getStock();
		
		JDialogForTagSearchMatrixPanelForAddSysNewsToNode newlog 
			= new JDialogForTagSearchMatrixPanelForAddSysNewsToNode (  stock);
	    newlog.setModal(true);
	    newlog.setVisible(true);

	}
//	/*
//     * ���øð����ɵ�Ȩ��
//     */
//	private void setGeGuWeightInBanKuai()
//    {
//		int row = this.getSelectedRow();
//		if(row < 0)
//			return;
//		int modelRow = this.convertRowIndexToModel(row);
//		
////		BkChanYeLianTreeNode curselectedbknode = (BkChanYeLianTreeNode) treechanyelian.getLastSelectedPathComponent();
//		BanKuai bkcode = ((BanKuaiGeGuTableModel)this.getModel()).getCurDispalyBandKuai();
//		String stockcode = ((BanKuaiGeGuTableModel)(this.getModel())).getStockCode(modelRow);
//		int weight = ((BanKuaiGeGuTableModel)(this.getModel())).getStockCurWeight (modelRow);
//		
//		String weightresult = JOptionPane.showInputDialog(null,"�������Ʊ�ڸð��Ȩ�أ�\n\r"
//											+ "10~1 ��ռ��ҵӪ�ձ���,\n\r 0 : Ӫ��ռ�ȼ���û�и���׶�,\n\r -1 : ���޹�ϵ"
//				,weight);
//		try {
//			int newweight = Integer.parseInt(weightresult);
//			if(newweight> 10 || newweight < -1)
//				JOptionPane.showMessageDialog(null,"Ȩ��ֵ��Χ10 ~ -1��\n\r"
//						,"Warning",JOptionPane.WARNING_MESSAGE);
//			
//			if(weight != newweight) {
//				bkdbopt.setStockWeightInBanKuai (bkcode,"",stockcode,newweight);
//				( (BanKuaiGeGuTableModel)this.getModel() ).setStockCurWeight (modelRow,newweight);
//			}
//		} catch (java.lang.NumberFormatException e) {
//			return;
//		}
//	}
	
//	public void hideZhanBiColumn (int hidecolumn) 
//	{
//		TableColumnModel tcm = this.getColumnModel();
//		if(hidecolumn >1) {
//			//�ڰ��������治��Ҫ3��column
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
