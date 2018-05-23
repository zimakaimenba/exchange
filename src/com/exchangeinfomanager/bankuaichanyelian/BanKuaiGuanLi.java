package com.exchangeinfomanager.bankuaichanyelian;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

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
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTree;
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
	public BanKuaiGuanLi(StockInfoManager stockInfoManager2, AllCurrentTdxBKAndStoksTree bkstk1,BanKuaiAndChanYeLian2 bkcyl1) 
	{
		sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
		this.stockInfoManager = stockInfoManager2;
		this.allbkstks = bkstk1;
		this.bkcyl = bkcyl1;
		
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
	private void commonrules (BkChanYeLianTreeNode node)
	{
		if(node.getType() == BanKuaiAndStockBasic.TDXBK) {
			if( ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL ) || ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL )  ) {
//				cbxnotgephi.setEnabled(false);
			}
		}
	}

	private void createEvents() 
	{
		cbxnotgephi.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			}
		});
		cbxnotbkfx.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			}
		});
		cbxnotimport.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if(cbxnotimport.isSelected()) {
					cbxnotbkfx.setSelected(true);
					cbxnotgephi.setSelected(true);
					
					cbxnotbkfx.setEnabled(false);
					cbxnotgephi.setEnabled(false);
				} else {
					cbxnotbkfx.setEnabled(true);
					cbxnotgephi.setEnabled(true);
					
					buttonapplybksetting.setEnabled(true);
				}
				
			}
		});
		
		buttonapplybksetting.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int row = tableSysBk.getSelectedRow();
				int column = tableSysBk.getSelectedColumn();
				
				BkChanYeLianTreeNode selectnode = (BkChanYeLianTreeNode) ( tableSysBk.getModel().getValueAt(row, 0) );
				if(selectnode.getType() == BanKuaiAndStockBasic.TDXBK) {
					bkdbopt.updateBanKuaiExportGephiBkfxOperation (selectnode.getMyOwnCode(),!cbxnotimport.isSelected(),!cbxnotgephi.isSelected(),!cbxnotbkfx.isSelected());
					((BanKuai)selectnode).setImportdailytradingdata(!cbxnotimport.isSelected());
					((BanKuai)selectnode).setExporttogehpi(!cbxnotgephi.isSelected());
					((BanKuai)selectnode).setShowinbkfxgui(!cbxnotbkfx.isSelected());
					
					buttonapplybksetting.setEnabled(false);
				}
			}
		});
		
		tableSysBk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tableSysBk.getSelectedRow();
				int column = tableSysBk.getSelectedColumn();
				
				BkChanYeLianTreeNode selectnode = (BkChanYeLianTreeNode) ( tableSysBk.getModel().getValueAt(row, 0) );
				if(selectnode.getType() == BanKuaiAndStockBasic.TDXBK) {
					cbxnotimport.setEnabled(true);
					cbxnotbkfx.setEnabled(true);
					cbxnotgephi.setEnabled(true);
					buttonapplybksetting.setEnabled(true);
					
					cbxnotimport.setSelected(!((BanKuai)selectnode).isImportdailytradingdata());
					cbxnotbkfx.setSelected(!((BanKuai)selectnode).isShowinbkfxgui());
					cbxnotgephi.setSelected(!((BanKuai)selectnode).isExporttogehpi());
					
					if(cbxnotimport.isSelected()) {
						cbxnotbkfx.setEnabled(false);
						cbxnotgephi.setEnabled(false);
					}
						
				} else { //个股，不能设置
					cbxnotimport.setSelected(false);
					cbxnotbkfx.setSelected(false);
					cbxnotgephi.setSelected(false);
					
					cbxnotimport.setEnabled(false);
					cbxnotbkfx.setEnabled(false);
					cbxnotgephi.setEnabled(false);
					buttonapplybksetting.setEnabled(false);
				}
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
	private JCheckBox cbxnotimport;
	private JCheckBox cbxnotbkfx;
	private JCheckBox cbxnotgephi;
	private JButton buttonapplybksetting;
	private BkChanYeLianTree cyltree;
	
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
		
		JScrollPane scrollPane = new JScrollPane();
		
		JLabel label = new JLabel("\u901A\u8FBE\u4FE1\u677F\u5757");
		
		JLabel label_1 = new JLabel("\u901A\u8FBE\u4FE1\u81EA\u5B9A\u4E49\u677F\u5757");
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED, new Color(240, 240, 240), new Color(255, 255, 255), new Color(105, 105, 105), new Color(160, 160, 160)), new LineBorder(new Color(180, 180, 180), 5)), "\u677F\u5757\u5C5E\u6027\u8BBE\u7F6E", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));


		GroupLayout gl_panelSys = new GroupLayout(panelSys);
		gl_panelSys.setHorizontalGroup(
			gl_panelSys.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelSys.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelSys.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelSys.createSequentialGroup()
							.addGroup(gl_panelSys.createParallelGroup(Alignment.LEADING)
								.addComponent(label)
								.addComponent(scrollPanesysbk, GroupLayout.PREFERRED_SIZE, 234, GroupLayout.PREFERRED_SIZE))
							.addGap(17)
							.addGroup(gl_panelSys.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_panelSys.createSequentialGroup()
									.addComponent(label_1)
									.addContainerGap(1168, Short.MAX_VALUE))
								.addComponent(scrollPane, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 241, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_panelSys.createSequentialGroup()
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 237, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(1278, Short.MAX_VALUE))))
		);
		gl_panelSys.setVerticalGroup(
			gl_panelSys.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelSys.createSequentialGroup()
					.addContainerGap(25, Short.MAX_VALUE)
					.addGroup(gl_panelSys.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(label_1))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelSys.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 680, GroupLayout.PREFERRED_SIZE)
						.addComponent(scrollPanesysbk, GroupLayout.PREFERRED_SIZE, 680, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
					.addGap(11))
		);
		
		cyltree = this.bkcyl.getBkChanYeLianTree();
		scrollPane.setViewportView(cyltree);
		
		cbxnotimport = new JCheckBox("\u4E0D\u5BFC\u5165\u6BCF\u65E5\u4EA4\u6613\u6570\u636E");
		
		
		cbxnotbkfx = new JCheckBox("\u4E0D\u505A\u677F\u5757\u5206\u6790");
		
		
		cbxnotgephi = new JCheckBox("\u4E0D\u5BFC\u51FA\u5230Gephi");
		
		
		buttonapplybksetting = new JButton("\u5E94\u7528");
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(cbxnotbkfx)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
							.addComponent(cbxnotimport)
							.addGroup(gl_panel_1.createSequentialGroup()
								.addComponent(cbxnotgephi)
								.addGap(40)
								.addComponent(buttonapplybksetting))))
					.addGap(19))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(cbxnotimport)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(cbxnotbkfx)
					.addGap(1)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(cbxnotgephi)
						.addComponent(buttonapplybksetting))
					.addContainerGap(13, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		
		//初始化jtreetable
		BanKuaiDetailTableModel treetablemodel = new BanKuaiDetailTableModel (this.allbkstks.getAllBkStocksTree());
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
		
//		bkcylpnl = new BanKuaiAndChanYeLian(this.stockInfoManager) ;
//		bkcylpnl.startGui();
		
		//tabbedPane.addTab("\u4EA7\u4E1A\u94FE\u5B50\u7248\u5757\u5B9A\u4E49", null, pnlGingo2, null);
//		tabbedPane.addTab("\u4EA7\u4E1A\u94FE\u5B50\u7248\u5757\u5B9A\u4E49", null, bkcylpnl, null);
//		tabbedPane.setSelectedIndex(0) ;
		
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
	
	BanKuaiDetailTableModel (BkChanYeLianTree cyltree)
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
