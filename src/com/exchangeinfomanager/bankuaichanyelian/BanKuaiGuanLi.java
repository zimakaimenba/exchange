package com.exchangeinfomanager.bankuaichanyelian;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;

import com.exchangeinfomanager.asinglestockinfo.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockTree;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.JTreeTable.AbstractTreeTableModel;
import com.exchangeinfomanager.commonlib.JTreeTable.JTreeTable;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.collect.Ordering;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

//import net.ginkgo.copy.Ginkgo2;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.border.TitledBorder;
import javax.swing.JTextField;
import com.toedter.calendar.JDateChooser;
import javax.swing.JComboBox;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.border.CompoundBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JTree;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class BanKuaiGuanLi extends JDialog 
{
	/**
	 * Create the dialog.
	 * @param stockInfoManager 
	 * @param zhongDianGuanZhuBanKuaiSheZhi 
	 * @param bkdbopt2 
	 * @param zdgzbkxmlhandler 
	 * @param cylxmlhandler 
	 */
	public BanKuaiGuanLi(StockInfoManager stockInfoManager2 ) 
	{
		sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
		this.stockInfoManager = stockInfoManager2;
		this.allbkstks = AllCurrentTdxBKAndStoksTree.getInstance();
		this.bkcyl = BanKuaiAndChanYeLian2.getInstance();
		
		initializeGui ();
		createEvents ();

//		initializeTDXBanKuaiLists ();
	}

	private StockInfoManager stockInfoManager;	
	private BanKuaiDbOperation bkdbopt;
	private HashMap<String,BanKuai> zhishulist;
//	private HashMap<String,BanKuai> sysbankuailist ; 
	private SystemConfigration sysconfig;
	private BanKuaiAndChanYeLian2 bkcyl;

	private void initializeTDXBanKuaiLists() 
	{
		DefaultTreeModel treeModel = (DefaultTreeModel)this.allbkstks.getAllBkStocksTree().getModel();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treeModel.getRoot();
//		DefaultTableModel tableModel = (DefaultTableModel)this.tableSysBk.getModel();
		
		BanKuaiDetailTableModel treetablemodel = new BanKuaiDetailTableModel (this.allbkstks.getAllBkStocksTree());
	}
	/*
	 * 
	 */
//	private void commonrules (BkChanYeLianTreeNode node)
//	{
//		if(node.getType() == BanKuaiAndStockBasic.TDXBK) {
//			if( ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL ) || ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL )  ) {
////				cbxnotgephi.setEnabled(false);
//			}
//		}
//	}

	private void createEvents() 
	{
		tfldsearchsysbk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
//				String nodeid = tfldsearchsysbk.getText().trim();
//				int rowindex = ((BanKuaiDetailTableModel)(tableSysBk.getModel())).getNodeLineIndex(nodeid);
//				
//				if(rowindex != -1) {
//					tableSysBk.setRowSelectionInterval(rowindex, rowindex);
//					tableSysBk.scrollRectToVisible(new Rectangle(tableSysBk.getCellRect(rowindex, 0, true)));
//				} else 	{
//					JOptionPane.showMessageDialog(null,"股票/板块代码有误或名称拼音有误！","Warning", JOptionPane.WARNING_MESSAGE);
//				}
			}
		});
		
		tableSysBk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tableSysBk.getSelectedRow();
				int column = tableSysBk.getSelectedColumn();
				
				BkChanYeLianTreeNode selectnode = (BkChanYeLianTreeNode) ( tableSysBk.getModel().getValueAt(row, 0) );
				panelsetting.setSettingNode(selectnode);
			}
		});
		

		
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
			}
		});
		
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				bkcylpnl.saveCylXmlAndZdgzXml();
				dispose ();
			}
		});
		
	}

	private JButton okButton;
	private JTreeTable tableSysBk;
	private final JPanel contentPanel = new JPanel();
	private AllCurrentTdxBKAndStoksTree allbkstks;
	private JPanel panelSys;
	private JTable tableZdy;
	private BanKuaiAndStockTree cyltree;
	private BanKuaiAndChanYeLianGUI2 bkcylpnl;
	private JTextField tfldsearchsysbk;
	private BanKuaiShuXingSheZhi panelsetting;
	
	private void initializeGui()
	{
		setTitle("\u901A\u8FBE\u4FE1\u677F\u5757/\u81EA\u5B9A\u4E49\u677F\u5757\u8BBE\u7F6E");
		setBounds(100, 100, 1576, 988);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane)
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 557, Short.MAX_VALUE))
		);
		
		panelSys = new JPanel();
		tabbedPane.addTab("\u901A\u8FBE\u4FE1\u677F\u5757\u4FE1\u606F", null, panelSys, null);
		
		JScrollPane scrollPanesysbk = new JScrollPane();
		
		JLabel label = new JLabel("\u901A\u8FBE\u4FE1\u677F\u5757");
		
		panelsetting = new BanKuaiShuXingSheZhi();
		
		
		tfldsearchsysbk = new JTextField();
		tfldsearchsysbk.setColumns(10);
		
		JButton btnsearchsysbk = new JButton("\u67E5\u627E\u677F\u5757");


		GroupLayout gl_panelSys = new GroupLayout(panelSys);
		gl_panelSys.setHorizontalGroup(
			gl_panelSys.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelSys.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelSys.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPanesysbk, GroupLayout.PREFERRED_SIZE, 234, GroupLayout.PREFERRED_SIZE)
						.addComponent(panelsetting, GroupLayout.PREFERRED_SIZE, 237, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panelSys.createParallelGroup(Alignment.TRAILING, false)
							.addGroup(gl_panelSys.createSequentialGroup()
								.addComponent(tfldsearchsysbk)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btnsearchsysbk))
							.addComponent(label, Alignment.LEADING)))
					.addContainerGap(1278, Short.MAX_VALUE))
		);
		gl_panelSys.setVerticalGroup(
			gl_panelSys.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelSys.createSequentialGroup()
					.addContainerGap(23, Short.MAX_VALUE)
					.addComponent(label)
					.addGap(15)
					.addGroup(gl_panelSys.createParallelGroup(Alignment.BASELINE)
						.addComponent(tfldsearchsysbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnsearchsysbk))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPanesysbk, GroupLayout.PREFERRED_SIZE, 582, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panelsetting, GroupLayout.PREFERRED_SIZE, 168, GroupLayout.PREFERRED_SIZE)
					.addGap(11))
		);
		
		cyltree = this.bkcyl.getBkChanYeLianTree();
		
		
//		初始化jtreetable
		BanKuaiDetailTableModel treetablemodel = new BanKuaiDetailTableModel (this.allbkstks.getAllBkStocksTree());
//		JTable tableSysBk1 = new JTable();
		tableSysBk = new JTreeTable(treetablemodel){

			private static final long serialVersionUID = 1L;

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
		};
		scrollPanesysbk.setViewportView(tableSysBk);
		panelSys.setLayout(gl_panelSys);
		
		bkcylpnl = new BanKuaiAndChanYeLianGUI2(bkcyl,stockInfoManager,allbkstks) ;
//		bkcylpnl.startGui();
		
		//tabbedPane.addTab("\u4EA7\u4E1A\u94FE\u5B50\u7248\u5757\u5B9A\u4E49", null, pnlGingo2, null);
		tabbedPane.addTab("\u4EA7\u4E1A\u94FE\u5B50\u7248\u5757\u5B9A\u4E49", null, bkcylpnl, null);
		tabbedPane.setSelectedIndex(0) ;
		
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("\u5173\u95ED");
				okButton.setActionCommand("OK");
				getRootPane().setDefaultButton(okButton);
			}
			
			JLabel lblNewLabel_1 = new JLabel("New label");
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.TRAILING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 426, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED, 429, Short.MAX_VALUE)
						.addComponent(okButton)
						.addGap(37))
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.TRAILING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(gl_buttonPane.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(lblNewLabel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(okButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap())
			);
			buttonPane.setLayout(gl_buttonPane);
		}
		
		this.setLocationRelativeTo(null);
		
	}
}


class BanKuaiDetailTableModel extends AbstractTreeTableModel 
{
	String[] jtableTitleStrings = { "板块ID","板块名称","板块类型"};
	
	BanKuaiDetailTableModel (BanKuaiAndStockTree cyltree)
	{
		super (  (BkChanYeLianTreeNode)cyltree.getModel().getRoot() );
		
	}

	@Override
	public int getColumnCount() {
		return jtableTitleStrings.length;
	}


	@Override
	public String getColumnName(int column) {
		return jtableTitleStrings[column];
	}


	@Override
	public Object getValueAt(Object node, int column) {
		 switch(column) {
         case 0:
            return node;
         case 1:
             return ((BkChanYeLianTreeNode)node).getMyOwnName();
         case 2:
        	 if ( ((BkChanYeLianTreeNode)node).getType() == BanKuaiAndStockBasic.TDXBK  )
        		 return ((BanKuai)node).getBanKuaiLeiXing();
        	 else 
        		 return "";
		 }   
		 return null;
	}


	@Override
	public Object getChild(Object parent, int index) {
		return ((BkChanYeLianTreeNode) parent).getChildAt(index);
	}


	@Override
	public int getChildCount(Object parent) {
		if (!((BkChanYeLianTreeNode) parent).isLeaf()) {
	         return ((BkChanYeLianTreeNode) parent).getChildCount();
	      }
	      return 0;
	}
	    

}
