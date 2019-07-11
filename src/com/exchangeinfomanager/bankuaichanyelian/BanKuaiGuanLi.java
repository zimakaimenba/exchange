package com.exchangeinfomanager.bankuaichanyelian;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
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

import com.exchangeinfomanager.StockCalendar.ColorScheme;
import com.exchangeinfomanager.StockCalendar.GBC;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuExternalInfoTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JPanelFactory;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.commonlib.SystemAudioPlayed;
import com.exchangeinfomanager.commonlib.JTreeTable.AbstractTreeTableModel;
import com.exchangeinfomanager.commonlib.JTreeTable.JTreeTable;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.HanYuPinYing;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.nodexdata.BanKuaiNodeXPeriodData;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.nodes.operations.BanKuaiAndStockTree;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

//import net.ginkgo.copy.Ginkgo2;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
		
		initializeBaiKuaiOfNoGeGuWithSelfCJLTree ();
		initializeGui2 ();
		
		createEvents ();

//		initializeTDXBanKuaiLists ();
	}

	private StockInfoManager stockInfoManager;	
	private BanKuaiDbOperation bkdbopt;
	private HashMap<String,BanKuai> zhishulist;
//	private HashMap<String,BanKuai> sysbankuailist ; 
	private SystemConfigration sysconfig;
	private BanKuaiAndChanYeLian2 bkcyl;
	private BanKuaiAndStockTree treebkonlynoggwithselfcjl;
	
	private Border outside = new MatteBorder(1, 0, 1, 0, Color.RED);
	private Border inside = new EmptyBorder(0, 1, 0, 1);
	private Border highlight = new CompoundBorder(outside, inside);

//	private void initializeTDXBanKuaiLists() 
//	{
//		DefaultTreeModel treeModel = (DefaultTreeModel)this.allbkstks.getAllBkStocksTree().getModel();
//		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treeModel.getRoot();
//		
//		BanKuaiDetailTableModel treetablemodel = new BanKuaiDetailTableModel (this.allbkstks.getAllBkStocksTree(),"ALL");
//	}

	private void initializeBaiKuaiOfNoGeGuWithSelfCJLTree() 
	{
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.allbkstks.getAllBkStocksTree().getModel().getRoot();
        int bankuaicount = this.allbkstks.getAllBkStocksTree().getModel().getChildCount(treeroot);
		
        DaPan alltopNode = new DaPan("000000","两交易所");
	
		for(int i=0;i < bankuaicount; i++) {
			try {
				BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.allbkstks.getAllBkStocksTree().getModel().getChild(treeroot, i);
				if(childnode.getType() != BkChanYeLianTreeNode.TDXBK) 
					continue;
				
				if(  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL)  ) {//有些指数是没有个股和成交量的，不列入比较范围
					BanKuai newbk = new BanKuai (childnode.getMyOwnCode(),childnode.getMyOwnName());
					newbk.setBanKuaiLeiXing(BanKuai.NOGGWITHSELFCJL);
					alltopNode.add(newbk);
				}
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
//				System.out.println("test");
			}
		} 

		treebkonlynoggwithselfcjl = new BanKuaiAndStockTree(alltopNode,"ONLYBANKUAINOGGNOSELFCJL");
	}

	private void createEvents() 
	{
		menuItemrelatedbk.addActionListener(new ActionListener() 
		{
			@Override

			public void actionPerformed(ActionEvent evt) {
				
				int row = tableSysBk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				BkChanYeLianTreeNode selectnode = (BkChanYeLianTreeNode) ( tableSysBk.getModel().getValueAt(row, 0) );
				
				if(selectnode.getType() == BkChanYeLianTreeNode.TDXBK  ) {
					String bkleixing = ((BanKuai) selectnode).getBanKuaiLeiXing(); 
					if( bkleixing.equals( BanKuai.HASGGWITHSELFCJL) || bkleixing.equals( BanKuai.HASGGNOSELFCJL ) ) 
						bankuaiSelectedSocialOperations( (BanKuai )selectnode);
				}
			}
			
		});
		
		
		
		menuItemSocialFriendofNogegubk.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {
				
				int row = tablenoggbk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				BkChanYeLianTreeNode friend = (BkChanYeLianTreeNode) ( tablenoggbk.getModel().getValueAt(row, 0) );
				
				
				row = tableSysBk.getSelectedRow();
				int column = tableSysBk.getSelectedColumn();
				BkChanYeLianTreeNode mainnode = (BkChanYeLianTreeNode) ( tableSysBk.getModel().getValueAt(row, 0) );
				
				if(!mainnode.getMyOwnCode().equals(friend.getMyOwnCode()  ) ) {
					updateNodeSocialFriend ( (BanKuai)mainnode, (BanKuai)friend);
					tablenoggbk.repaint();
				}
				
				
			}
			
		});
		
		menuItemSocialFriend.addActionListener(new ActionListener() {
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
				updateNodeSocialFriend (mainnode, friend);
				
				tableBkfriends.repaint();
			}
			
		});
		
		tableSysBk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tableSysBk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				((BanKuaiSocialFriendsTableModel)tableBkfriends.getModel()).deleteAllRows();
				((BkChanYeLianTreeNodeListTableModel)tablebkgegu.getModel()).deleteAllRows();
				
				BkChanYeLianTreeNode selectnode = (BkChanYeLianTreeNode) ( tableSysBk.getModel().getValueAt(row, 0) );
				
				tfldsearchsysbk.setText(selectnode.getMyOwnCode());
				
				LocalDate requiredstart = CommonUtility.getSettingRangeDate( LocalDate.now(), "Large");
				selectnode = allbkstks.getBanKuai( (BanKuai)selectnode,  requiredstart, LocalDate.now(), TDXNodeGivenPeriodDataItem.WEEK);
				selectnode = bkdbopt.getBanKuaiBasicInfo( (BanKuai)selectnode);

				panelsetting.setSettingNode(selectnode);
				
				tablenoggbk.repaint();
				tableBkfriends.repaint();
			}
		});
		

		
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
			}
		});
	}

	protected void updateNodeSocialFriend(BanKuai mainnode, BanKuai friend)
	{
		bkdbopt.updateNodeSocialFriendShips (mainnode,friend);
		
//		Set<String> friendset = mainnode.getSocialFriendsSet();
//		if( friendset.contains(friend.getMyOwnCode() ) ) { //已经是朋友了要取消
//			bkdbopt.updateNodeSocialFriendShips (mainnode,friend);
//		} else { //不是朋友，要加入
//			kk
//		}
		
	}


	protected void bankuaiSelectedSocialOperations(BanKuai selectnode) 
	{
		// TODO Auto-generated method stub
		if(selectnode.getType() != BkChanYeLianTreeNode.TDXBK) 
			return;
		
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);

		LocalDate requiredstart = CommonUtility.getSettingRangeDate( LocalDate.now(), "Large");
//		selectnode = this.allbkstks.getBanKuai( selectnode,  requiredstart, LocalDate.now(), TDXNodeGivenPeriodDataItem.WEEK);
//		selectnode = bkdbopt.getBanKuaiBasicInfo(selectnode);
		
		Set<String> bkrelatedbks = new HashSet<String> ();
		selectnode = this.allbkstks.getAllGeGuOfBanKuai (selectnode,TDXNodeGivenPeriodDataItem.WEEK); //获取所有曾经是该板块的个股
		ArrayList<StockOfBanKuai> bkgg = selectnode.getAllGeGuOfBanKuaiInHistory();
		for(StockOfBanKuai sob : bkgg) {
			Stock stock = sob.getStock();
			stock = this.allbkstks.getStock(stock, requiredstart, LocalDate.now(), TDXNodeGivenPeriodDataItem.WEEK);
			stock = bkdbopt.getTDXBanKuaiForAStock ( stock ); //通达信板块信息
			HashMap<String, String> suoshubk = stock.getGeGuCurSuoShuTDXSysBanKuaiList();
			Set<String> bkcodeset = suoshubk.keySet();
			bkrelatedbks.addAll(bkcodeset);
		}
		
		ArrayList<BanKuai> bklist = new ArrayList<BanKuai> ();
		for(String bkcode : bkrelatedbks) {
			if(bkcode.equals(selectnode.getMyOwnCode()))
				continue;
			
			BanKuai tmpbk;
			tmpbk = this.allbkstks.getBanKuai( bkcode,  requiredstart, LocalDate.now(), TDXNodeGivenPeriodDataItem.WEEK);
			tmpbk = bkdbopt.getBanKuaiBasicInfo(tmpbk);
			tmpbk = this.allbkstks.getAllGeGuOfBanKuai (tmpbk,TDXNodeGivenPeriodDataItem.WEEK); //获取所有曾经是该板块的个股
			
			bklist.add(tmpbk);
		}
		
		((BanKuaiSocialFriendsTableModel)tableBkfriends.getModel()).refresh(selectnode, bklist);
		
		hourglassCursor = null;
		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(hourglassCursor2);

		SystemAudioPlayed.playSound();

	}

	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JButton okButton;
	private JTreeTable tableSysBk;
	private final JPanel contentPanel = new JPanel();
	private AllCurrentTdxBKAndStoksTree allbkstks;
	private JPanel panelSys;
	private JTable tableZdy;
	private BanKuaiAndStockTree cyltree;
	private BanKuaiAndChanYeLianGUI2 bkcylpnl;
	private JUpdatedTextField tfldsearchsysbk;
	private BanKuaiShuXingSheZhi panelsetting;
	private JMenuBar menuBar;
	private JTable tableBkfriends;
	private JTable tablebkgegu;
	private JMenuItem menuItemSocialFriend;
	private JTreeTable tablenoggbk;
	private JMenuItem menuItemSocialFriendofNogegubk;
	private JMenuItem menuItemrelatedbk;
	
	private void initializeGui2() 
	{
		setTitle("\u901A\u8FBE\u4FE1\u677F\u5757/\u81EA\u5B9A\u4E49\u677F\u5757\u8BBE\u7F6E");
		setBounds(100, 100, 1576, 988);
		getContentPane().setLayout(new BorderLayout());
		
		JPanel layoutPanel = JPanelFactory.createFixedSizePanel(new BorderLayout ());
		layoutPanel.setBackground(ColorScheme.BACKGROUND);
		layoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 0));
		this.add(layoutPanel);
		
		JPanel westbankuaipnl = JPanelFactory.createPanel(); //右边板块设置pnl
		westbankuaipnl.setPreferredSize(new Dimension(500, 988));
		westbankuaipnl.setLayout(new GridLayout(1, 2));
		westbankuaipnl.setBackground(ColorScheme.GREY_LINE);
	    
		JPanel allbkfriendspnl = JPanelFactory.createPanel(); //板块的social friend pnl
		allbkfriendspnl.setLayout(new GridLayout(3, 1));
		allbkfriendspnl.setPreferredSize(new Dimension(300, 988));
		
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

	                try {
	                    tip = getValueAt(rowIndex, colIndex).toString();
	                } catch (RuntimeException e1) {
	                    //catch null pointer exception if mouse is over an empty line
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
				        
				        BanKuai mainbankuai = tablemodel.getMainBanKuai();
				        Set<String> socialset = mainbankuai.getSocialFriendsSet();
				        String socialbkcode =  (String) this.getValueAt(row, 0);
				        	if(socialset.contains(socialbkcode)) {
				        		jc.setBorder( highlight );
			        	}

				        
						return comp;
				}
				
			};
		scrollPanebkfriends.setViewportView(tableBkfriends);
//		scrollPanebkfriends.setPreferredSize(new Dimension(250, 600));
		allbkfriendspnl.add(scrollPanebkfriends);
		JPopupMenu popupMenusocial = new JPopupMenu();
		this.addPopup (tableBkfriends,popupMenusocial);
		menuItemSocialFriend = new JMenuItem("设置/取消好友关系");
		popupMenusocial.add(menuItemSocialFriend);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel> (tableBkfriends.getModel() );
		tableBkfriends.setRowSorter(sorter);

		JScrollPane scrollPanenoggbk = new JScrollPane (); //没有个股的板块列表
		BanKuaiDetailTableModel ggbktreetablemodel = new BanKuaiDetailTableModel ( treebkonlynoggwithselfcjl );
//		JTable tableSysBk = new JTable();
		tablenoggbk = new JTreeTable(ggbktreetablemodel) {

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
					BkChanYeLianTreeNode sysbk = (BkChanYeLianTreeNode) ( tableSysBk.getModel().getValueAt(rowofsysbk, 0) );
			        Set<String> socialset = ((BanKuai)sysbk).getSocialFriendsSet();
			        String socialbkcode =  node.getMyOwnCode();
			        if(socialset.contains(socialbkcode)) {
			        		jc.setBorder( highlight );
		        	}
				}
				
		        return comp;
			}
		};
		scrollPanenoggbk.setViewportView(tablenoggbk);
//		scrollPanesysbk.setPreferredSize(new Dimension(200, 615));
		allbkfriendspnl.add(scrollPanenoggbk);
		JPopupMenu popupMenunogegubk = new JPopupMenu();
		this.addPopup (tablenoggbk,popupMenunogegubk);
		menuItemSocialFriendofNogegubk = new JMenuItem("设置/取消好友关系");
		popupMenunogegubk.add(menuItemSocialFriendofNogegubk);

		
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

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                	e1.printStackTrace();
                }
                return tip;
            } 
			
		};
		scrollPanebkgegu.setViewportView(tablebkgegu);
//		scrollPanebkgegu.setPreferredSize(new Dimension(250, 340));
		allbkfriendspnl.add(scrollPanebkgegu);

		
		JPanel allbkskpnl = JPanelFactory.createPanel(); //所有板块及板块设置窗口
		allbkskpnl.setLayout(new FlowLayout() );
		allbkskpnl.setPreferredSize(new Dimension(200, 988));

		 JPanel searchpnl = JPanelFactory.createFixedSizePanel(new FlowLayout ());
		 searchpnl.setPreferredSize(new Dimension(200, 38));
			tfldsearchsysbk = new JUpdatedTextField();
			tfldsearchsysbk.setColumns(8);
			JButton btnsearchsysbk = new JButton("\u67E5\u627E\u677F\u5757");
			searchpnl.add(tfldsearchsysbk);
			searchpnl.add(btnsearchsysbk);
			allbkskpnl.add(searchpnl);
			
			
		 JScrollPane scrollPanesysbk = new JScrollPane (); //
//			初始化jtreetable
			BanKuaiDetailTableModel treetablemodel = new BanKuaiDetailTableModel (this.allbkstks.getAllBkStocksTree() );
//			JTable tableSysBk = new JTable();
			tableSysBk = new JTreeTable(treetablemodel) {

				private static final long serialVersionUID = 1L;

				public String getToolTipText(MouseEvent e) 
				{
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
		scrollPanesysbk.setViewportView(tableSysBk);
		scrollPanesysbk.setPreferredSize(new Dimension(200, 615));
		allbkskpnl.add(scrollPanesysbk);
		JPopupMenu popupMenunosysbk = new JPopupMenu();
		this.addPopup (tableSysBk,popupMenunosysbk);
		menuItemrelatedbk = new JMenuItem("计算关联板块");
		popupMenunosysbk.add(menuItemrelatedbk);
		
		
		panelsetting = new BanKuaiShuXingSheZhi();
		allbkskpnl.add( panelsetting);
		
		westbankuaipnl.add(allbkskpnl);
		westbankuaipnl.add(allbkfriendspnl);
		
		layoutPanel.add(westbankuaipnl, BorderLayout.WEST);
		
		
		bkcylpnl = new BanKuaiAndChanYeLianGUI2(bkcyl,stockInfoManager,allbkstks) ;
		layoutPanel.add(bkcylpnl, BorderLayout.CENTER);
		
		menuBar = new JMenuBar();
		menuBar.setBorderPainted(false);
		this.setJMenuBar(menuBar);
		
		JMenu menuOperationList = new JMenu("\u64CD\u4F5C");
		buttonGroup.add(menuOperationList);
		menuBar.add(menuOperationList);
		 
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
        	 if ( ((BkChanYeLianTreeNode)node).getType() == BkChanYeLianTreeNode.TDXBK  )
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
            	ArrayList<StockOfBanKuai> mainbkgegu = this.mainbankuai.getAllGeGuOfBanKuaiInHistory();
            	ArrayList<StockOfBanKuai> socialbkgegu = socialbankuai.getAllGeGuOfBanKuaiInHistory();
            	Set<String> mainbkgegucodeset = new HashSet<String> (); Set<String> socialbkgegucodeset = new HashSet<String> ();
            	for(StockOfBanKuai sob: mainbkgegu) {
            		Boolean isinbknow = sob.isInBanKuaiAtSpecificDate(LocalDate.now());
            		if(isinbknow)
            			mainbkgegucodeset.add( sob.getMyOwnCode() );
            	}
            	for(StockOfBanKuai sob: socialbkgegu) {
            		Boolean isinbknow = sob.isInBanKuaiAtSpecificDate(LocalDate.now());
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



