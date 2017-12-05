package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

	public BanKuaiInfoTable() {
		super ();
		BanKuaiInfoTableModel bkmodel = new BanKuaiInfoTableModel ();
		this.setModel(bkmodel);
		this.createEvents ();
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.getModel());
		this.setRowSorter(sorter);
		this.sortByZhanBiGrowthRate ();
		this.bkdbopt = new BanKuaiDbOperation ();
		this.sysconfig = SystemConfigration.getInstance();
	}
	/*
	 * 
	 */
	private void sortByZhanBiGrowthRate ()
	{
		TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)this.getRowSorter();
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int columnIndexToSort = 2; //��������ռ������
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
	}
	
	private void createEvents ()
	{
		JPopupMenu popupMenuGeguNews = new JPopupMenu();
		JMenuItem menuItemAddNews = new JMenuItem("��Ӱ������");
		JMenuItem menuItemAddToGz = new JMenuItem("�����ע");
		JMenuItem menuItemMakeLongTou = new JMenuItem("����Ϊ�����ȵ�");
		
		popupMenuGeguNews.add(menuItemAddNews);
		popupMenuGeguNews.add(menuItemMakeLongTou);
		popupMenuGeguNews.add(menuItemAddToGz);
		
		this.setComponentPopupMenu(popupMenuGeguNews);
		
		menuItemAddNews.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {

				addBanKuaiNews ();
			}
			
		});
		
		menuItemMakeLongTou.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {

				addBanKuaiReDian ();
			}
			
		});
		
		menuItemAddToGz.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {

				addZdgz ();
			}
			
		});



	}

	protected void addZdgz() 
	{
		int row = this.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"��ѡ��һ�����","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		int modelRow = this.convertRowIndexToModel(row); 
		String bkcode = ((BanKuaiInfoTableModel) this.getModel()).getBanKuaiCode(modelRow);
		String bkname = ((BanKuaiInfoTableModel) this.getModel()).getBanKuaiName(modelRow);
		
		JiaRuJiHua jiarujihua = new JiaRuJiHua ( bkcode,"�����ע" ); 
		int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "�����ע", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
			return;
		
		int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
		
	}
	protected void addBanKuaiReDian()
	{
		int row = this.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"��ѡ��һ�����","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		int modelRow = this.convertRowIndexToModel(row); 
		String bkcode = ((BanKuaiInfoTableModel) this.getModel()).getBanKuaiCode(modelRow);
		String bkname = ((BanKuaiInfoTableModel) this.getModel()).getBanKuaiName(modelRow);

		JiaRuJiHua jiarujihua = new JiaRuJiHua ( bkcode,"�����ȵ�" ); 
		int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "�����ȵ�", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
			return;
		
		int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
	}
	protected void addBanKuaiNews() 
	{
		int row = this.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"��ѡ��һ�����","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		int modelRow = this.convertRowIndexToModel(row); 
		BanKuai bankuai = ((BanKuaiInfoTableModel) this.getModel()).getBanKuai(modelRow);
		String bkcode = bankuai.getMyOwnCode();
		ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (bkcode);
		int exchangeresult = JOptionPane.showConfirmDialog(null, cylnews, "���Ӱ������", JOptionPane.OK_CANCEL_OPTION);

		if(exchangeresult == JOptionPane.CANCEL_OPTION)
			return;
		
		bkdbopt.addBanKuaiNews(bkcode, cylnews.getInputedNews());
	}
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
	 */
	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
			 
	        Component comp = super.prepareRenderer(renderer, row, col);
	        BanKuaiInfoTableModel tablemodel = (BanKuaiInfoTableModel)this.getModel(); 
	        int modelRow = convertRowIndexToModel(row);
	        BanKuai bankuai = tablemodel.getBanKuai(modelRow);
	        
	        //������ʾ
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
	        	comp.setForeground(Color.BLUE);
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
