package com.exchangeinfomanager.bankuaichanyelian;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfDZHBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.StockCalendar.ColorScheme;
import com.exchangeinfomanager.StockCalendar.GBC;
import com.exchangeinfomanager.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.Trees.TreeOfChanYeLian;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BanKuaiandGeGuTableBasic;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BandKuaiAndGeGuTableBasicModel;
import com.exchangeinfomanager.bankuaifengxi.bankuaiinfotable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.commonlib.SystemAudioPlayed;
import com.exchangeinfomanager.commonlib.JTreeTable.AbstractTreeTableModel;
import com.exchangeinfomanager.commonlib.JTreeTable.JTreeTable;
import com.exchangeinfomanager.commonlib.JTreeTable.TreeTableModelAdapter;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.CylTreeDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.CylTreeNestedSetNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.HanYuPinYing;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.google.common.base.Strings;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

//import net.ginkgo.copy.Ginkgo2;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.Collator;
import java.time.LocalDate;
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
import javax.swing.JComponent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.border.CompoundBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JTree;
import javax.swing.JViewport;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class BanKuaiGuanLi extends JDialog 
{
private Set<JTreeTable> tableallbktableset;

//	private CylTreeDbOperation cyltreedb;

	/**
	 * Create the dialog.
	 * @param stockInfoManager 
	 * @param zhongDianGuanZhuBanKuaiSheZhi 
	 * @param bkdbopt2 
	 * @param zdgzbkxmlhandler 
	 * @param cylxmlhandler 
	 */
	public BanKuaiGuanLi() 
	{
		this.bkdbopt = new BanKuaiDbOperation ();
		initializeBaiKuaiOfNoGeGuWithSelfCJLTree ();
		initializeGui2 ();
		createEvents ();
		
		 this.tableallbktableset = new HashSet<> ();
		 tableallbktableset.add(tableSysBk);
		 tableallbktableset.add(tableDzhBk);
	}
	
	private BanKuaiDbOperation bkdbopt;
//	private HashMap<String,BanKuai> zhishulist;
	private BanKuaiAndStockTree treebkonlynoggwithselfcjl;
	private TreeOfChanYeLian tdxbksocialtree;
	
	private Border outsidepos = new MatteBorder(1, 0, 1, 0, Color.RED);
	private Border insidepos = new EmptyBorder(0, 1, 0, 1);
	private Border highlightpos = new CompoundBorder(outsidepos, insidepos);
	
	private Border outsideneg = new MatteBorder(1, 0, 1, 0, Color.GREEN);
	private Border insideneg = new EmptyBorder(0, 1, 0, 1);
	private Border highlightneg = new CompoundBorder(outsideneg, insideneg);

	private void initializeBaiKuaiOfNoGeGuWithSelfCJLTree() 
	{
		BanKuaiAndStockTree treeofbkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treeofbkstk.getModel().getRoot();
        int bankuaicount = treeofbkstk.getModel().getChildCount(treeroot);
		
        DaPan alltopNode = new DaPan("000000","两交易所");
	
		for(int i=0;i < bankuaicount; i++) {
			try {
				BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)treeofbkstk.getModel().getChild(treeroot, i);
				if(childnode.getType() != BkChanYeLianTreeNode.TDXBK) 
					continue;
				
				String bkleixing = ((BanKuai)childnode).getBanKuaiLeiXing(); 
				if(  bkleixing != null  && (bkleixing.equals(BanKuai.NOGGWITHSELFCJL) || bkleixing.equals(BanKuai.HASGGWITHSELFCJL))  ) {//有些指数是没有个股和成交量的，不列入比较范围
					BanKuai newbk = new BanKuai (childnode.getMyOwnCode(),childnode.getMyOwnName());
					newbk.setBanKuaiLeiXing(bkleixing);
					alltopNode.add(newbk);
				}
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		} 

		treebkonlynoggwithselfcjl = new BanKuaiAndStockTree(alltopNode,"ONLYBANKUAINOGGNOSELFCJL");
	}

	private void createEvents() 
	{
		tfldsearchsysbk.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e){
            	if(!Strings.isNullOrEmpty(tfldsearchsysbk.getText() ) )
					findInputedNodeInTable (tfldsearchsysbk.getText().trim());
            }});
		btnsearchsysbk.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				if(!Strings.isNullOrEmpty(tfldsearchsysbk.getText() ) )
					findInputedNodeInTable (tfldsearchsysbk.getText().trim());
			}
			
		});

		btndelnode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				deleteANode ();
			}
		});
		
		btnAddBktotree.addMouseMotionListener(new mousemotionadapter()  );
		btnAddBktotree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
//            	addSubnodeButton.setIcon(addSubnodeIcon);
//    	        addSubnodeButton.setToolTipText("Add subnode");
            }
        });
		btnAddBktotree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBanKuaiButtonActionPerformed(evt);
            }
        });
		
		menuItemrelatedbk.addActionListener(new ActionListener() 
		{
			@Override

			public void actionPerformed(ActionEvent evt) {
				
				int row = tableSysBk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int  model_row = tableSysBk.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
				BkChanYeLianTreeNode selectnode = (BkChanYeLianTreeNode) ( tableSysBk.getModel().getValueAt(model_row, 0) );
				
				if(selectnode.getType() == BkChanYeLianTreeNode.TDXBK  ) {
					String bkleixing = ((BanKuai) selectnode).getBanKuaiLeiXing(); 
					if( bkleixing.equals( BanKuai.HASGGWITHSELFCJL) || bkleixing.equals( BanKuai.HASGGNOSELFCJL ) ) 
						bankuaiSelectedSocialOperations( (BanKuai )selectnode);
				}
			}
			
		});
		
		menuItemSocialFriendNegtiveformiddletable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = tablenoggbk.getSelectedRow();
				if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int  model_row = tablenoggbk.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
				BkChanYeLianTreeNode friend = (BkChanYeLianTreeNode) ( tablenoggbk.getModel().getValueAt(model_row, 0) );
				
				row = tableSysBk.getSelectedRow();
				model_row = tableSysBk.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
				BkChanYeLianTreeNode mainnode = (BkChanYeLianTreeNode) ( tableSysBk.getModel().getValueAt(model_row, 0) );
				
				if(!mainnode.getMyOwnCode().equals(friend.getMyOwnCode()  ) ) {
					updateNodeSocialFriend ( (BanKuai)mainnode, (BanKuai)friend, false);
					tablenoggbk.repaint();
				}
			}
		});
		
		menuItemSocialFriendNegtiveformiddletablefordzh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = tabledazsocial.getSelectedRow();
				if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int  model_row = tabledazsocial.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
				BkChanYeLianTreeNode friend = (BkChanYeLianTreeNode) ( tabledazsocial.getModel().getValueAt(model_row, 0) );
				
				row = tableSysBk.getSelectedRow();
				model_row = tableSysBk.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
				BkChanYeLianTreeNode mainnode = (BkChanYeLianTreeNode) ( tableSysBk.getModel().getValueAt(model_row, 0) );
				
				if(!mainnode.getMyOwnCode().equals(friend.getMyOwnCode()  ) ) {
					updateNodeSocialFriend ( (BanKuai)mainnode, (BanKuai)friend, false);
					tablenoggbk.repaint();
				}
			}
		});
		
		menuItemSocialFriendPostiveformiddletablefordzh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = tabledazsocial.getSelectedRow();
				if(row <0) { JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int  model_row = tabledazsocial.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
				BkChanYeLianTreeNode friend = (BkChanYeLianTreeNode) ( tabledazsocial.getModel().getValueAt(model_row, 0) );
				
				row = tableSysBk.getSelectedRow();
				model_row = tableSysBk.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
				BkChanYeLianTreeNode mainnode = (BkChanYeLianTreeNode) ( tableSysBk.getModel().getValueAt(model_row, 0) );
				
				if(!mainnode.getMyOwnCode().equals(friend.getMyOwnCode()  ) ) {
					updateNodeSocialFriend ( (BanKuai)mainnode, (BanKuai)friend, true);
					tablenoggbk.repaint();
				}
			}
			
		});
		
		menuItemSocialFriendPostiveformiddletable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = tablenoggbk.getSelectedRow();
				if(row <0) { JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int  model_row = tablenoggbk.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
				BkChanYeLianTreeNode friend = (BkChanYeLianTreeNode) ( tablenoggbk.getModel().getValueAt(model_row, 0) );
				
				
				row = tableSysBk.getSelectedRow();
				model_row = tableSysBk.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
				BkChanYeLianTreeNode mainnode = (BkChanYeLianTreeNode) ( tableSysBk.getModel().getValueAt(model_row, 0) );
				
				if(!mainnode.getMyOwnCode().equals(friend.getMyOwnCode()  ) ) {
					updateNodeSocialFriend ( (BanKuai)mainnode, (BanKuai)friend, true);
					tablenoggbk.repaint();
				}
			}
			
		});
		
		menuItemSocialFriendNegtivefortoppesttable.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {
				int row = tableBkfriends.getSelectedRow();
				if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int  model_row = tableBkfriends.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
				BanKuai friend = ((BanKuaiSocialFriendsTableModel) tableBkfriends.getModel()).geSocialtBanKuai(model_row);
				
				BanKuai mainnode = ((BanKuaiSocialFriendsTableModel) tableBkfriends.getModel()).getMainBanKuai();
				
				updateNodeSocialFriend (mainnode, friend, false);
				
				tableBkfriends.repaint();
			}
			
		});
		
		menuItemSocialFriendPostivefortoppesttable.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {
				
				int row = tableBkfriends.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int  model_row = tableBkfriends.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
				BanKuai friend = ((BanKuaiSocialFriendsTableModel) tableBkfriends.getModel()).geSocialtBanKuai(model_row);
				
				BanKuai mainnode = ((BanKuaiSocialFriendsTableModel) tableBkfriends.getModel()).getMainBanKuai();
				
				updateNodeSocialFriend (mainnode, friend, true);
				
				tableBkfriends.repaint();
			}
			
		});
		
		tableSysBk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tableSysBk.getSelectedRow();
				if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				((BanKuaiSocialFriendsTableModel)tableBkfriends.getModel()).deleteAllRows();
				((BkChanYeLianTreeNodeListTableModel)tablebkgegu.getModel()).deleteAllRows();
				
				BkChanYeLianTreeNode selectnode = (BkChanYeLianTreeNode) ( tableSysBk.getModel().getValueAt(row, 0) );
				if(selectnode.getType() != BkChanYeLianTreeNode.TDXBK)
					return; 
				
				tfldsearchsysbk.setText(selectnode.getMyOwnCode());
				
				LocalDate requiredstart = CommonUtility.getSettingRangeDate( LocalDate.now(), "Large");
				SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
				selectnode = svsbk.getNodeData(selectnode, requiredstart,  LocalDate.now(), NodeGivenPeriodDataItem.WEEK,true);
				selectnode = svsbk.getNodeJiBenMian(selectnode);

				panelsetting.setSettingNode(selectnode);
				
				tablenoggbk.repaint();
				tableBkfriends.repaint();
				tabledazsocial.repaint();
				
				svsbk = null;
			}
		});
		
		tableDzhBk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tableDzhBk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				((BanKuaiSocialFriendsTableModel)tableBkfriends.getModel()).deleteAllRows();
				((BkChanYeLianTreeNodeListTableModel)tablebkgegu.getModel()).deleteAllRows();
				
				BkChanYeLianTreeNode selectnode = (BkChanYeLianTreeNode) ( tableDzhBk.getModel().getValueAt(row, 0) );
				if(selectnode.getType() != BkChanYeLianTreeNode.DZHBK)
					return; 
				
				tfldsearchsysbk.setText(selectnode.getMyOwnCode());
				
//				LocalDate requiredstart = CommonUtility.getSettingRangeDate( LocalDate.now(), "Large");
//				SvsForNodeOfDZHBanKuai svsbk = new SvsForNodeOfDZHBanKuai ();
//				selectnode = svsbk.getNodeData(selectnode, requiredstart,  LocalDate.now(), NodeGivenPeriodDataItem.WEEK,true);
//				selectnode = svsbk.getNodeJiBenMian(selectnode);

				panelsetting.setSettingNode(selectnode);
				
				tablenoggbk.repaint();
				tableBkfriends.repaint();
				tabledazsocial.repaint();
//				svsbk = null;
			}
		});
		
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
			}
		});
	}
	private void deleteANode ()
	{
		BkChanYeLianTreeNode delnode = (BkChanYeLianTreeNode) tdxbksocialtree.getSelectionPath().getLastPathComponent();
		int action = JOptionPane.showConfirmDialog(null, "是否将该节点删除？","询问", JOptionPane.YES_NO_OPTION);
		if(0 == action) {
			tdxbksocialtree.deleteNodeFromTree ( (CylTreeNestedSetNode)delnode,true);
		} 
	}
	private void addBanKuaiButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{
		String selecttitle = tabbedPaneBk.getTitleAt( tabbedPaneBk.getSelectedIndex() );
		if(selecttitle.contains("通达信"))
			addTDXBanKuaiToBanKuaiSocialTree (evt);
		else if(selecttitle.contains("大智慧"))
			addDZHBanKuaiToBanKuaiSocialTree (evt);
	}
	private void addDZHBanKuaiToBanKuaiSocialTree (java.awt.event.ActionEvent evt)
	{
		int row = tableDzhBk.getSelectedRow();
		if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		BkChanYeLianTreeNode selectnode = (BkChanYeLianTreeNode) ( tableDzhBk.getModel().getValueAt(row, 0) );
		if(selectnode.getType() != BkChanYeLianTreeNode.DZHBK)
			return;
		
		if( !((BanKuai)selectnode).getBanKuaiOperationSetting().isShowinbkfxgui() ) {
			JOptionPane.showMessageDialog(null,"板块已被设置为不在板块分析窗口显示，不可添加！","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		BkChanYeLianTreeNode parent;
		try { parent = (BkChanYeLianTreeNode) this.tdxbksocialtree.getSelectionPath().getLastPathComponent();
		} catch (java.lang.NullPointerException e) { JOptionPane.showMessageDialog(null,"请选择父节点!"); 
			return;
		}
		if(parent.getType() == BkChanYeLianTreeNode.DAPAN) {
			if( !((BanKuai)selectnode).getNodeJiBenMian().isCoreZhiShu() ) {	JOptionPane.showMessageDialog(null,"不是核心指数，不能添加为根节点!");
				return;
			}
		}
			 
		int direction = ((SubnodeButton)evt.getSource()).getDirection();
		this.tdxbksocialtree.addNewNodeToTree (selectnode, direction);
	}
	private void addTDXBanKuaiToBanKuaiSocialTree (java.awt.event.ActionEvent evt)
	{
		int row = tableSysBk.getSelectedRow();
		if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		BkChanYeLianTreeNode selectnode = (BkChanYeLianTreeNode) ( tableSysBk.getModel().getValueAt(row, 0) );
		if(selectnode.getType() != BkChanYeLianTreeNode.TDXBK)
			return;
		
		if( !((BanKuai)selectnode).getBanKuaiOperationSetting().isShowinbkfxgui() ) {
			JOptionPane.showMessageDialog(null,"板块已被设置为不在板块分析窗口显示，不可添加！","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		BkChanYeLianTreeNode parent;
		try { parent = (BkChanYeLianTreeNode) this.tdxbksocialtree.getSelectionPath().getLastPathComponent();
		} catch (java.lang.NullPointerException e) { JOptionPane.showMessageDialog(null,"请选择父节点!"); 
			return;
		}
		if(parent.getType() == BkChanYeLianTreeNode.DAPAN) {
			if( !((BanKuai)selectnode).getNodeJiBenMian().isCoreZhiShu() ) {	JOptionPane.showMessageDialog(null,"不是核心指数，不能添加为根节点!");
				return;
			}
		}
			 
		int direction = ((SubnodeButton)evt.getSource()).getDirection();
		this.tdxbksocialtree.addNewNodeToTree (selectnode, direction);
	}

	protected void updateNodeSocialFriend(BanKuai mainnode, BanKuai friend, Boolean relationship)
	{
		bkdbopt.updateNodeSocialFriendShips (mainnode,friend, relationship);
	}


	protected void bankuaiSelectedSocialOperations(BanKuai selectnode) 
	{
		// TODO Auto-generated method stub
		if(selectnode.getType() != BkChanYeLianTreeNode.TDXBK) 	return;
		
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);

		Set<BkChanYeLianTreeNode> bkrelatedbks = new HashSet<> ();
		SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
		selectnode = svsbk.getAllGeGuOfBanKuai (selectnode);
		List<BkChanYeLianTreeNode> bkgg = selectnode.getAllGeGuOfBanKuaiInHistory();
		SvsForNodeOfStock svsstock = new SvsForNodeOfStock ();
		for(BkChanYeLianTreeNode sob : bkgg) {
			Stock stock = ((StockOfBanKuai)sob).getStock();
//			stock = (Stock) svsstock.getNodeData(stock, requiredstart, LocalDate.now(), NodeGivenPeriodDataItem.WEEK,true);
			stock = (Stock) svsstock.getNodeSuoShuBanKuaiList ( stock ); //通达信板块信息
			Set<BkChanYeLianTreeNode> bkcodeset = stock.getGeGuCurSuoShuTDXSysBanKuaiList();
			bkrelatedbks.addAll(bkcodeset);
		}
		
		ArrayList<BanKuai> bklist = new ArrayList<BanKuai> ();
		
		for(BkChanYeLianTreeNode bkcode : bkrelatedbks) {
			if(bkcode.getMyOwnCode().equals(selectnode.getMyOwnCode()))
				continue;
			
			BanKuai tmpbk;
//			tmpbk = (BanKuai) svsbk.getNodeData( bkcode,  requiredstart, LocalDate.now(), NodeGivenPeriodDataItem.WEEK,true);
//			tmpbk = (BanKuai) svsbk.getNodeJiBenMian(tmpbk);
			tmpbk = svsbk.getAllGeGuOfBanKuai ((BanKuai)bkcode); //获取所有曾经是该板块的个股
			
			bklist.add(tmpbk);
		}
		
		((BanKuaiSocialFriendsTableModel)tableBkfriends.getModel()).refresh(selectnode, bklist);
		svsbk = null; svsstock = null;
		hourglassCursor = null;
		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(hourglassCursor2);

		SystemAudioPlayed.playSound();

	}

	private final ButtonGroup buttonGroup = new ButtonGroup();
	
//	private final JPanel contentPanel = new JPanel();
//	private AllCurrentTdxBKAndStoksTree allbkstks;
//	private JPanel panelSys;
//	private JTable tableZdy;
//	private BanKuaiAndStockTree cyltree;
	private JUpdatedTextField tfldsearchsysbk;
	private BanKuaiShuXingSheZhi panelsetting;
	private JMenuBar menuBar;
	private JTreeTable tableSysBk;
	private JTable tableBkfriends;
	private JTable tablebkgegu;
	private JTreeTable tableDzhBk;
	private JTreeTable tabledazsocial;
	private JMenuItem menuItemSocialFriendPostivefortoppesttable;
	private JMenuItem menuItemSocialFriendNegtivefortoppesttable;
	private JTreeTable tablenoggbk;
	private JMenuItem menuItemSocialFriendPostiveformiddletable;
	private JMenuItem menuItemSocialFriendNegtiveformiddletable;
	private JMenuItem menuItemrelatedbk;
	private JTabbedPane tabbedPaneBk;
	private JMenuItem menuItemSocialFriendPostiveformiddletablefordzh;
	private JMenuItem menuItemSocialFriendNegtiveformiddletablefordzh;
	private SubnodeButton btnAddBktotree;
	private JButton btndelnode;
	private JButton btnfindnode;
	private JUpdatedTextField tfldfindnodeintree;
	ImageIcon addBelowIcon, addAboveIcon, addChildIcon, addSubnodeIcon;
	private JButton btnsearchsysbk;
	
	private void initializeGui2() 
	{
		setTitle("\u901A\u8FBE\u4FE1\u677F\u5757/\u81EA\u5B9A\u4E49\u677F\u5757\u8BBE\u7F6E");
		setBounds(100, 100, 750, 988);
		getContentPane().setLayout(new BorderLayout());
		
		JPanel layoutPanel = JPanelFactory.createFixedSizePanel(new BorderLayout ());
		layoutPanel.setBackground(ColorScheme.BACKGROUND);
		layoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 0));
		this.add(layoutPanel);
		
		JPanel pnlwestwestbankuaisocialtree = JPanelFactory.createPanel(); //
		pnlwestwestbankuaisocialtree.setPreferredSize(new Dimension(200, 988));
		pnlwestwestbankuaisocialtree.setLayout(new FlowLayout() );
		layoutPanel.add(pnlwestwestbankuaisocialtree, BorderLayout.WEST);
		
		JPanel westbankuaipnl = JPanelFactory.createPanel(); //右边板块设置pnl
		westbankuaipnl.setPreferredSize(new Dimension(500, 988));
		westbankuaipnl.setLayout(new GridLayout(1, 2));
		westbankuaipnl.setBackground(ColorScheme.GREY_LINE);
		layoutPanel.add(westbankuaipnl, BorderLayout.EAST);
		
		//westbankuaipnl
		JPanel allbkfriendspnl = JPanelFactory.createPanel(); //板块的social friend pnl
		allbkfriendspnl.setLayout(new GridLayout(3, 1));
		allbkfriendspnl.setPreferredSize(new Dimension(300, 900));
		
		JTabbedPane tabbedPaneSocialBk =  new JTabbedPane (JTabbedPane.TOP);
		allbkfriendspnl.add(tabbedPaneSocialBk);
		
		 JScrollPane scrollPanebkfriends = new JScrollPane (); //bankuai's related bankuai/social friends
//			初始化jtreetable
		 BanKuaiSocialFriendsTableModel treetablemodel2 = new BanKuaiSocialFriendsTableModel ();
//			JTable tableSysBk = new JTable();
		 tableBkfriends = new JTable(treetablemodel2) {
			 	
				private static final long serialVersionUID = 1L;
				JMenuItem menuItemSocialFriend;
				
				public String getToolTipText(MouseEvent e) {
	                String tip = null;
	                java.awt.Point p = e.getPoint();
	                int rowIndex = rowAtPoint(p);
	                int colIndex = columnAtPoint(p);

	                try { tip = getValueAt(rowIndex, colIndex).toString();
	                } catch (RuntimeException e1) {//catch null pointer exception if mouse is over an empty line
	                }

	                return tip;
	            }
				
				/*
				 * (non-Javadoc)
				 * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
				 */
				public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
			 
				        Component comp = super.prepareRenderer(renderer, row, col);
				        JComponent jc = (JComponent)comp;
				        BanKuaiSocialFriendsTableModel tablemodel = (BanKuaiSocialFriendsTableModel)this.getModel(); 
				        if(tablemodel.getRowCount() == 0) {
				        	return null;
				        }
				        
				        String socialbkcode =  (String) this.getValueAt(row, 0);
				        BanKuai mainbankuai = tablemodel.getMainBanKuai();
				        Set<String> socialsetpos = mainbankuai.getSocialFriendsSetPostive();
				        if(socialsetpos.contains(socialbkcode)) jc.setBorder( highlightpos );
			        	
				        Set<String> socialsetneg = mainbankuai.getSocialFriendsSetNegtive();
				        if(socialsetneg.contains(socialbkcode)) jc.setBorder( highlightneg );

				        return comp;
				}
				
			};
		scrollPanebkfriends.setViewportView(tableBkfriends);
		allbkfriendspnl.add(scrollPanebkfriends);
		
		JPopupMenu popupMenusocial = new JPopupMenu();
		this.addPopup (tableBkfriends,popupMenusocial);
		menuItemSocialFriendNegtivefortoppesttable = new JMenuItem("设置/取消好友负相关关系");
		menuItemSocialFriendNegtivefortoppesttable.setOpaque(true);
		menuItemSocialFriendNegtivefortoppesttable.setBackground(Color.GREEN);
		menuItemSocialFriendPostivefortoppesttable = new JMenuItem("设置/取消好友正相关关系");
		menuItemSocialFriendPostivefortoppesttable.setOpaque(true);
		menuItemSocialFriendPostivefortoppesttable.setBackground(Color.RED);
		popupMenusocial.add(menuItemSocialFriendPostivefortoppesttable);
		popupMenusocial.add(menuItemSocialFriendNegtivefortoppesttable);
		
		TableRowSorter<TableModel> sorterofbkfriends = new TableRowSorter<TableModel> (tableBkfriends.getModel() );
		tableBkfriends.setRowSorter(sorterofbkfriends);
		
		////////////////////////////////////////////////////////////////////////
		JScrollPane scrollPanenoggbk = new JScrollPane (); //板块列表
		BanKuaiDetailTableModel ggbktreetablemodel = new BanKuaiDetailTableModel ( treebkonlynoggwithselfcjl );
		tablenoggbk = new JTreeTable(ggbktreetablemodel) {

			private static final long serialVersionUID = 1L;
			
			public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {   tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {//catch null pointer exception if mouse is over an empty line
                }

                return tip;
            }
			
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) 
			{
		        Component comp = super.prepareRenderer(renderer, row, col);
		        JComponent jc = (JComponent)comp;
		        
				BkChanYeLianTreeNode node = (BkChanYeLianTreeNode) ( this.getModel().getValueAt(row, 0) );
				if(node.getType() !=  BkChanYeLianTreeNode.TDXBK ) return comp; 
				
					String bktype = ((BanKuai)node).getBanKuaiLeiXing();
			        if(bktype.equals(BanKuai.NOGGWITHSELFCJL)) {
			        	Font defaultFont = this.getFont();
			        	Font font = new Font(defaultFont.getName(),Font.ITALIC,defaultFont.getSize());
			        	comp.setFont(font);
			        }
				
				int rowofsysbk = tableSysBk.getSelectedRow();
				if(rowofsysbk <0) return comp;
				
				String socialbkcode =  node.getMyOwnCode();
				BanKuai sysbk = (BanKuai) ( tableSysBk.getModel().getValueAt(rowofsysbk, 0) );
			        Set<String> socialsetpos = sysbk.getSocialFriendsSetPostive();
			        if(socialsetpos.contains(socialbkcode))	jc.setBorder( highlightpos );
		        	
			        Set<String> socialsetneg = sysbk.getSocialFriendsSetNegtive();
			        if(socialsetneg.contains(socialbkcode))	jc.setBorder( highlightneg );
				
		        return comp;
			}
		};
		TableRowSorter<TableModel> sorterofnoggbk = new TableRowSorter<TableModel>(tablenoggbk.getModel());
		tablenoggbk.setRowSorter(sorterofnoggbk);
		scrollPanenoggbk.setViewportView(tablenoggbk);
		tabbedPaneSocialBk.addTab("通达信板块", null, scrollPanenoggbk, null);
		
		JPopupMenu popupMenunogegubk = new JPopupMenu();
		this.addPopup (tablenoggbk,popupMenunogegubk);
		menuItemSocialFriendNegtiveformiddletable = new JMenuItem("设置/取消好友负相关关系");
		menuItemSocialFriendNegtiveformiddletable.setOpaque(true);
		menuItemSocialFriendNegtiveformiddletable.setBackground(Color.GREEN);
		menuItemSocialFriendPostiveformiddletable = new JMenuItem("设置/取消好友正相关关系");
		menuItemSocialFriendPostiveformiddletable.setOpaque(true);
		menuItemSocialFriendPostiveformiddletable.setBackground(Color.RED);
		popupMenunogegubk.add(menuItemSocialFriendPostiveformiddletable);
		popupMenunogegubk.add(menuItemSocialFriendNegtiveformiddletable);
		
		//////////////////////////////////////////////////
		JScrollPane scrollPanesocialdzhbk   = new JScrollPane (); //板块列表
		BanKuaiDetailTableModel socialdzhtreetablemodel = new BanKuaiDetailTableModel ( CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks()  );
		tabledazsocial = new JTreeTable(socialdzhtreetablemodel) {

			private static final long serialVersionUID = 1L;
			
			public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {   tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {//catch null pointer exception if mouse is over an empty line
                }

                return tip;
            }
			
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) 
			{
				
		        Component comp = super.prepareRenderer(renderer, row, col);
		        JComponent jc = (JComponent)comp;
		        
				BkChanYeLianTreeNode node = (BkChanYeLianTreeNode) ( this.getModel().getValueAt(row, 0) );
				if(node.getType() ==  BkChanYeLianTreeNode.TDXBK ) {
					
					String bktype = ((BanKuai)node).getBanKuaiLeiXing();
			        if(bktype.equals(BanKuai.NOGGWITHSELFCJL)) {
			        	Font defaultFont = this.getFont();
			        	Font font = new Font(defaultFont.getName(),Font.ITALIC,defaultFont.getSize());
			        	comp.setFont(font);
			        }
				}
				
				
				int rowofsysbk = tableSysBk.getSelectedRow();
				if(rowofsysbk <0) {
				} else {
					String socialbkcode =  node.getMyOwnCode();
					BanKuai sysbk = (BanKuai) ( tableSysBk.getModel().getValueAt(rowofsysbk, 0) );
			        Set<String> socialsetpos = sysbk.getSocialFriendsSetPostive();
			        if(socialsetpos.contains(socialbkcode))	jc.setBorder( highlightpos );
		        	
			        Set<String> socialsetneg = sysbk.getSocialFriendsSetNegtive();
			        if(socialsetneg.contains(socialbkcode))	jc.setBorder( highlightneg );
			    }
				
		        return comp;
			}
		};
		scrollPanesocialdzhbk.setViewportView(tabledazsocial);
		JPopupMenu popupMenunodzhsocial = new JPopupMenu();
		this.addPopup (tabledazsocial,popupMenunodzhsocial);
		menuItemSocialFriendNegtiveformiddletablefordzh = new JMenuItem("设置/取消好友负相关关系");
		menuItemSocialFriendNegtiveformiddletablefordzh.setOpaque(true);
		menuItemSocialFriendNegtiveformiddletablefordzh.setBackground(Color.GREEN);
		
		menuItemSocialFriendPostiveformiddletablefordzh = new JMenuItem("设置/取消好友正相关关系");
		menuItemSocialFriendPostiveformiddletablefordzh.setOpaque(true);
		menuItemSocialFriendPostiveformiddletablefordzh.setBackground(Color.RED);
		
		popupMenunodzhsocial.add(menuItemSocialFriendNegtiveformiddletablefordzh);
		popupMenunodzhsocial.add(menuItemSocialFriendPostiveformiddletablefordzh);
		
		tabbedPaneSocialBk.addTab("大智慧板块", null, scrollPanesocialdzhbk, null);
		allbkfriendspnl.add(tabbedPaneSocialBk);

		/////////////////////////////////////////////////
		
		JScrollPane scrollPanebkgegu = new JScrollPane (); //bankuai's 个股
		BkChanYeLianTreeNodeListTableModel bkstmodel = new BkChanYeLianTreeNodeListTableModel ();
		tablebkgegu = new  JTable(bkstmodel)
		{
			private static final long serialVersionUID = 1L;
			
			public String getToolTipText(MouseEvent e) 
			{
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try { tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {e1.printStackTrace();}
                return tip;
            } 
			
		};
		scrollPanebkgegu.setViewportView(tablebkgegu);
		allbkfriendspnl.add(scrollPanebkgegu);
		
		JPanel allbkskpnl = JPanelFactory.createPanel(); //所有板块及板块设置窗口
		allbkskpnl.setLayout(new FlowLayout() );
		allbkskpnl.setPreferredSize(new Dimension(200, 988));

		 JPanel searchpnl = JPanelFactory.createFixedSizePanel(new FlowLayout ());
		 searchpnl.setPreferredSize(new Dimension(200, 38));
			tfldsearchsysbk = new JUpdatedTextField();
			tfldsearchsysbk.setPreferredSize( new Dimension( 100, 33 ) );
//			tfldsearchsysbk.setColumns(10);
			btnsearchsysbk = new JButton("\u67E5\u627E\u677F\u5757");
			searchpnl.add(tfldsearchsysbk);
			searchpnl.add(btnsearchsysbk);
			allbkskpnl.add(searchpnl);
			
			tabbedPaneBk = new JTabbedPane (JTabbedPane.TOP); 
			allbkskpnl.add(tabbedPaneBk);	
		 JScrollPane scrollPanesysbk = new JScrollPane (); //
		 tabbedPaneBk.addTab("通达信板块", null, scrollPanesysbk, null);
//			初始化jtreetable
			BanKuaiDetailTableModel treetablemodel = new BanKuaiDetailTableModel (CreateExchangeTree.CreateTreeOfBanKuaiAndStocks() );
			tableSysBk = new JTreeTable(treetablemodel) {
				
				private static final long serialVersionUID = 1L;

				public String getToolTipText(MouseEvent e) 
				{
	                String tip = null;
	                java.awt.Point p = e.getPoint();
	                int rowIndex = rowAtPoint(p);
	                int colIndex = columnAtPoint(p);

	                try {   tip = getValueAt(rowIndex, colIndex).toString();
	                } catch (RuntimeException e1) {   //catch null pointer exception if mouse is over an empty line
	                }

	                return tip;
	            }
				
				public Component prepareRenderer(TableCellRenderer renderer, int row, int col) 
				{
			        Component comp = super.prepareRenderer(renderer, row, col);
			        JComponent jc = (JComponent)comp;
			        
					BkChanYeLianTreeNode node = (BkChanYeLianTreeNode) ( this.getModel().getValueAt(row, 0) );
					if(node.getType() ==  BkChanYeLianTreeNode.TDXBK ) {
						
						String bktype = ((BanKuai)node).getBanKuaiLeiXing();
				        if(bktype.equals(BanKuai.NOGGWITHSELFCJL)) {
				        	Font defaultFont = this.getFont();
				        	Font font = new Font(defaultFont.getName(),Font.ITALIC,defaultFont.getSize());
				        	comp.setFont(font);
				        }
					}
			        return comp;
				}
			};
			
		TableRowSorter<TableModel> sorterofsysbk = new TableRowSorter<TableModel>(tableSysBk.getModel());
		tableSysBk.setRowSorter(sorterofsysbk);
		scrollPanesysbk.setViewportView(tableSysBk);
		scrollPanesysbk.setPreferredSize(new Dimension(200, 615));
		JPopupMenu popupMenunosysbk = new JPopupMenu();
		this.addPopup (tableSysBk,popupMenunosysbk);
		menuItemrelatedbk = new JMenuItem("计算关联板块");
		popupMenunosysbk.add(menuItemrelatedbk);
		popupMenunosysbk.add(menuItemrelatedbk);
		
		JScrollPane scrollPanedzhsysbk = new JScrollPane (); 
		tabbedPaneBk.addTab("大智慧板块", null, scrollPanedzhsysbk, null);
//		初始化jtreetable
		BanKuaiDetailTableModel dzhtreetablemodel = new BanKuaiDetailTableModel (CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks() );
		tableDzhBk = new JTreeTable(dzhtreetablemodel) {
			
			private static final long serialVersionUID = 1L;

			public String getToolTipText(MouseEvent e) 
			{
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try { tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {}//catch null pointer exception if mouse is over an empty line

                return tip;
            }
			
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) 
			{
		        Component comp = super.prepareRenderer(renderer, row, col);
		        JComponent jc = (JComponent)comp;
		        
				BkChanYeLianTreeNode node = (BkChanYeLianTreeNode) ( this.getModel().getValueAt(row, 0) );
				if(node.getType() ==  BkChanYeLianTreeNode.DZHBK ) {
					String bktype = ((BanKuai)node).getBanKuaiLeiXing();
			        if(bktype!= null && bktype.equals(BanKuai.NOGGWITHSELFCJL)) {
			        	Font defaultFont = this.getFont();
			        	Font font = new Font(defaultFont.getName(),Font.ITALIC,defaultFont.getSize());
			        	comp.setFont(font);
			        }
				}
		        return comp;
			}
		};
		scrollPanedzhsysbk.setViewportView(tableDzhBk);
		scrollPanedzhsysbk.setPreferredSize(new Dimension(200, 615));

		panelsetting = new BanKuaiShuXingSheZhi();
		allbkskpnl.add( panelsetting);
		
		westbankuaipnl.add(allbkskpnl);
		westbankuaipnl.add(allbkfriendspnl);
		
		//pnlwestwestbankuaisocialtree
		JPanel pnlwestwestupbksocial = JPanelFactory.createPanel(); //
		pnlwestwestupbksocial.setLayout(new FlowLayout() );
//		pnlwestwestupbksocial.setPreferredSize(new Dimension(200, 100));
		
		this.tdxbksocialtree = CreateExchangeTree.CreateTreeOfBanKuaiSocialFriends();
		JScrollPane sclpwestwestupbksocial = new JScrollPane(this.tdxbksocialtree,
			      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sclpwestwestupbksocial.setPreferredSize(new Dimension(200, 688));
		sclpwestwestupbksocial.grabFocus();
		sclpwestwestupbksocial.getVerticalScrollBar().setValue(0);
		
		JPanel pnlwestwestdownbksocial = JPanelFactory.createPanel(); //
		pnlwestwestdownbksocial.setLayout(new FlowLayout() );
//		pnlwestwestdownbksocial.setPreferredSize(new Dimension(200, 200));
		
		pnlwestwestbankuaisocialtree.add(pnlwestwestupbksocial);
		pnlwestwestbankuaisocialtree.add(sclpwestwestupbksocial);
		pnlwestwestbankuaisocialtree.add(pnlwestwestdownbksocial);
		//////
		btnfindnode = new JButton("\u5B9A\u4F4D\u8282\u70B9");
		tfldfindnodeintree = new JUpdatedTextField();
		tfldfindnodeintree.setColumns(10);
		pnlwestwestdownbksocial.add(btnfindnode);
		pnlwestwestdownbksocial.add(tfldfindnodeintree);
		/////
		btnAddBktotree = new SubnodeButton();
		btnAddBktotree.setIcon(new ImageIcon(BanKuaiAndChanYeLianGUI.class.getResource("/images/subnode24.png")));
		btndelnode = new JButton("\u5220\u9664\u8282\u70B9");
		pnlwestwestupbksocial.add(btnAddBktotree);
		pnlwestwestupbksocial.add(btndelnode);

		menuBar = new JMenuBar();
		menuBar.setBorderPainted(false);
		this.setJMenuBar(menuBar);
		
		JMenu menuOperationList = new JMenu("\u64CD\u4F5C");
		buttonGroup.add(menuOperationList);
		menuBar.add(menuOperationList);
		
		addBelowIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeBelow24.png"));
	    addAboveIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeAbove24.png"));
	    addSubnodeIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnode24.png"));
	    addChildIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeChild24.png"));
	}
	
	private  void addPopup(Component component, final JPopupMenu popup) 
	{
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	
	 private class mousemotionadapter extends MouseMotionAdapter {

		 public void mouseMoved(java.awt.event.MouseEvent evt) {
           	 SubnodeButton button = (SubnodeButton) evt.getSource();
    	        String key;
    	        if (System.getProperty("os.name").startsWith("Mac OS X")) 
    	        	key = "CMD";
    	        else 
    	        	key = "CTRL";
    	        
    	        int x = evt.getX();
    	        int y = evt.getY();
    	        if (y<19 && x+y<30 && x<19) {
    	            button.setDirection(BanKuaiAndChanYeLianGUI.UP);
    	            button.setIcon(addAboveIcon);
    	            button.setToolTipText("Add above ("+key+"-UP)");
    	        }
    	        else if (y>=19 && x-y < 0 && x<19){
    	            button.setDirection(BanKuaiAndChanYeLianGUI.DOWN);
    	            button.setIcon(addBelowIcon);
    	            button.setToolTipText("Add below ("+key+"-DOWN)");
    	        }
    	        else if (x+y>30 && x-y>0){
    	            button.setDirection(BanKuaiAndChanYeLianGUI.RIGHT);
    	            button.setIcon(addChildIcon);
    	            button.setToolTipText("Add subnode ("+key+"-RIGHT)");
    	        }
    	        else {
    	            button.setDirection(BanKuaiAndChanYeLianGUI.NONE);
    	            button.setIcon(addSubnodeIcon);
    	            button.setToolTipText("Add subnode");
    	        }
           }
	    }
	 
	private Boolean findInputedNodeInTable(String nodecode) 
	{
			Boolean found = false; int rowindex;
			nodecode = nodecode.substring(0,6);
			
			for( JTreeTable tmptable: tableallbktableset) {
				int tblrowcount = tmptable.getTree ().getRowCount();
				for(int row=0;row<tblrowcount;row++) {
					BkChanYeLianTreeNode bk =  (BkChanYeLianTreeNode) ((TreeTableModelAdapter)tmptable.getModel()).getValueAt(row, 0);
					if(bk.getMyOwnCode().equalsIgnoreCase(nodecode) && BkChanYeLianTreeNode.isBanKuai(bk) ) {
//					    JViewport viewport = (JViewport)tmptable.getParent();
//				        // This rectangle is relative to the table where the
//				        // northwest corner of cell (0,0) is always (0,0).
//				    Rectangle rect = tmptable.getCellRect(modelRow, 0, true);
//				        // The location of the viewport relative to the table
//				    Point pt = viewport.getViewPosition();
//				        // Translate the cell location so that it is relative
//				        // to the view, assuming the northwest corner of the
//				        // view is (0,0)
//				    rect.setLocation(rect.x-pt.x, rect.y-pt.y);
		//
//				    tmptable.scrollRectToVisible(rect);
					
					JViewport viewport = (JViewport)tmptable.getParent();
			        // view dimension
			        Dimension dim = viewport.getExtentSize();
			        // cell dimension
			        Dimension dimOne = new Dimension(0,0);

			        // This rectangle is relative to the table where the
			        // northwest corner of cell (0,0) is always (0,0).
			        Rectangle rect = tmptable.getCellRect(row, 0, true);
			        Rectangle rectOne;
			        if (row+1<tmptable.getTree().getRowCount()) {
			            
			            rectOne = tmptable.getCellRect(row+1, 0, true);
			            dimOne.width=rectOne.x-rect.x;
			            dimOne.height=rectOne.y-rect.y;
			        }

			        // '+ veiw dimension - cell dimension' to set first selected row on the top
			        rect.setLocation(rect.x+dim.width-dimOne.width, rect.y+dim.height-dimOne.height);

			        tmptable.scrollRectToVisible(rect);
					try {	tmptable.setRowSelectionInterval(row, row);
					} catch ( java.lang.IllegalArgumentException e) { e.printStackTrace(); System.out.print(row ); return false;}
//						int curselectrow = tmptable.getSelectedRow();
//						try {
//							tmptable.setRowSelectionInterval(modelRow, modelRow);
//							tmptable.scrollRectToVisible(new Rectangle(tmptable.getCellRect(modelRow, 0, true)));
//						} catch (java.lang.IllegalArgumentException ex) {}
					}
				}
			}

			panelsetting.disableAllSettingComponents();
			
			return true;
	}
	
}

/*
 * 系统所有的板块和个股，根据需要显示
 */
class BanKuaiDetailTableModel extends AbstractTreeTableModel 
{
	String[] jtableTitleStrings = { "板块ID","板块名称","板块类型"};
//	private String displaytype;
	
	BanKuaiDetailTableModel (BanKuaiAndStockTree cyltree)
	{
		super (  (BkChanYeLianTreeNode)cyltree.getModel().getRoot() );
//		this.displaytype = displaytype1;
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
	public Object getValueAt(Object node, int column) 
	{
//		int nodetype = ((BkChanYeLianTreeNode)node).getType();
//		if(displaytype.toLowerCase().equals("stock")  ){
//			if(nodetype != BkChanYeLianTreeNode.TDXGG )
//				return null;
//		}
		switch(column) {
         case 0:
            return node;
         case 1:
             return ((BkChanYeLianTreeNode)node).getMyOwnName();
         case 2:
        	 if (  BkChanYeLianTreeNode.isBanKuai((BkChanYeLianTreeNode)node))
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
	
//	public int getRowCount() 
//	{
//		 return 0;
//	}
	

}
/*
 * 板块social friends
 */
class BanKuaiSocialFriendsTableModel extends DefaultTableModel 
{

	public BanKuaiSocialFriendsTableModel() {
		super ();
	}
	
	String[] jtableTitleStrings = { "板块代码", "名称","关联度"};
	List<BanKuai> entryList;
	private BanKuai mainbankuai;
	private static Logger logger = Logger.getLogger(BanKuaiSocialFriendsTableModel.class);
	
	/*
	 * 
	 */
	public void refresh  (BanKuai mainbk, List<BanKuai> bankuaiwithcje)
	{
		this.mainbankuai = mainbk;
		entryList = bankuaiwithcje;
		
		this.fireTableDataChanged();
	}

	public void addSocialFriendsBanKuai (BanKuai bankuai)
	{
		if(entryList == null)
			entryList = new ArrayList<BanKuai> ();
		
		entryList.add(bankuai);
	}
	
	 public int getRowCount() 
	 {
		 if(entryList == null)
			 return 0;
		 else
			 return entryList.size();
	 }
	 public void deleteAllRows ()
	 {
		 if(this.entryList != null && this.entryList.size() >0)
			 this.entryList.clear();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(entryList.isEmpty() )
	    		return null;
	    	
	    	BanKuai socialbankuai = null;
	    	try {
	    		socialbankuai = entryList.get( rowIndex );
	    	} catch (java.lang.IndexOutOfBoundsException e) {
	    		e.printStackTrace();
	    		return null;
	    	}
	    	
	    	
	    	Object value = "??";
	    	
			switch (columnIndex) {
            case 0:
            	String bkcode = socialbankuai.getMyOwnCode();
                value = bkcode;
                break;
            case 1:
            	String thisbkname = socialbankuai.getMyOwnName();
            	value = thisbkname;
            	break;
            case 2: //"板块代码", "名称","CJE占比增长率","CJE占比","CJL占比增长率","CJL占比","大盘成交额增长贡献率","成交额排名"
            	List<BkChanYeLianTreeNode> mainbkgegu = this.mainbankuai.getAllGeGuOfBanKuaiInHistory();
            	List<BkChanYeLianTreeNode> socialbkgegu = socialbankuai.getAllGeGuOfBanKuaiInHistory();
            	Set<String> mainbkgegucodeset = new HashSet<String> (); Set<String> socialbkgegucodeset = new HashSet<String> ();
            	for(BkChanYeLianTreeNode sob: mainbkgegu) {
            		Boolean isinbknow = ((StockOfBanKuai)sob).isInBanKuaiAtSpecificDate(LocalDate.now());
            		if(isinbknow)
            			mainbkgegucodeset.add( sob.getMyOwnCode() );
            	}
            	for(BkChanYeLianTreeNode sob: socialbkgegu) {
            		Boolean isinbknow = ((StockOfBanKuai)sob).isInBanKuaiAtSpecificDate(LocalDate.now());
            		if(isinbknow)
            			socialbkgegucodeset.add( sob.getMyOwnCode() );
            	}
            	
            	SetView<String> resultset = Sets.intersection(mainbkgegucodeset, socialbkgegucodeset);
            	value = String.valueOf( resultset.size() ) + "  (" + String.valueOf( mainbkgegucodeset.size() ) +" / " + socialbkgegucodeset.size() +   ")";  
            			
            	break;
	    	}

	    	return value;
	    
	  }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0://"板块代码", "名称","CJE占比增长率","CJE占比","CJL占比增长率","CJL占比","大盘成交额增长贡献率","成交额排名"
		    	  clazz = String.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = String.class;
			          break;
		      }
		      
		      return clazz;
	 }
	    
	    public String getColumnName(int column) { 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    public  BanKuai getMainBanKuai ()
	    {
	    	return this.mainbankuai;
	    }
//	    public String getSocalBanKuaiCode (int row) 
//	    {
//	    	return (String)this.getValueAt(row,0);
//	    }
//	    public String getSocialBanKuaiName (int row) 
//	    {
//	    	return (String)this.getValueAt(row,1);
//	    } 
	    public BanKuai geSocialtBanKuai (int row)
	    {
	    	return this.entryList.get(row);
	    }

//	    public int getBanKuaiRowIndex (String neededfindstring) 
//	    {
//	    		int index = -1;
//	    		HanYuPinYing hypy = new HanYuPinYing ();
//	    		
//	    		for(int i=0;i<this.getRowCount();i++) {
//	    			String bkcode = (String)this.getValueAt(i, 0);
//	    			String bkname = (String)this.getValueAt(i,1); 
//	    			if(bkcode.trim().equals(neededfindstring) ) {
//	    				index = i;
//	    				break;
//	    			}
//
//	    			String namehypy = hypy.getBanKuaiNameOfPinYin(bkname );
//			   		if(namehypy.toLowerCase().equals(neededfindstring.trim().toLowerCase())) {
//			   			index = i;
//			   			break;
//			   		}
//	    		}
//	    	hypy = null;
//	   		return index;
//	    }

		public String[] getTableHeader() 
		{
			return this.jtableTitleStrings;
		}
}