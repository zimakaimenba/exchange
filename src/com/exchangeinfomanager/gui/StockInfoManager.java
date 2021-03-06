package com.exchangeinfomanager.gui;

import java.awt.EventQueue;
import static org.junit.Assert.assertEquals;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.exchangeinfomanager.checkboxtree.CheckBoxTree;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.Search.SearchDialog;
//import com.exchangeinfomanager.checkboxtree.CheckBoxTreeXmlHandler;
import com.exchangeinfomanager.accountconfiguration.AccountOperation.AccountSeeting;
import com.exchangeinfomanager.accountconfiguration.AccountOperation.ImportQuanShangJiaoYiRecords;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;
import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiGuanLi;
import com.exchangeinfomanager.bankuaichanyelian.BkChanYeLianTree;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.DisplayBkGgInfoEditorPane;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNews;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXiBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXiPieChartPnl;
import com.exchangeinfomanager.bankuaifengxi.ChenJiaoZhanBiInGivenPeriod;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextArea;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import com.toedter.calendar.JDateChooser;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JMenuItem;


import com.exchangeinfomanager.tongdaxinreport.*;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.sun.rowset.CachedRowSetImpl;
import com.exchangeinfomanager.database.*;
import com.exchangeinfomanager.gui.AccountAndChiCangConfiguration;
import com.exchangeinfomanager.gui.TableCellListener;
import com.exchangeinfomanager.gui.subgui.BanKuaiListEditorPane;
import com.exchangeinfomanager.gui.subgui.BuyCheckListTreeDialog;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.gui.subgui.GengGaiZhangHu;
import com.exchangeinfomanager.gui.subgui.ImportTDXData;
import com.exchangeinfomanager.gui.subgui.JStockComboBox;
import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;
import com.exchangeinfomanager.gui.subgui.PaoMaDeng2;
import com.exchangeinfomanager.gui.subgui.SelectMultiNode;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.border.BevelBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;

import javax.swing.border.SoftBevelBorder;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.tree.TreePath;

import org.jsoup.Jsoup;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.beans.PropertyChangeEvent;
import javax.swing.JPopupMenu;
import javax.swing.border.EtchedBorder;
import java.awt.FlowLayout;



import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;
import javax.swing.JSplitPane;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;


public class StockInfoManager 
{
	/**
	 * Create the application.
	 */
	public StockInfoManager() 
	{
		sysconfig = SystemConfigration.getInstance();
		connectdb = ConnectDataBase.getInstance();
		boolean localconnect = connectdb.isLocalDatabaseconnected();
		boolean rmtconnect = connectdb.isRemoteDatabaseconnected();
		if(localconnect == true && rmtconnect == true) {
			;
		}
		else if(localconnect == false && rmtconnect == true) {
				JOptionPane.showMessageDialog(null,"仅通达信同步数据可用！");
		} else if(localconnect == true && rmtconnect == false) {
				JOptionPane.showMessageDialog(null,"仅基本数据可用！");
		} else{
				JOptionPane.showMessageDialog(null,"基本数据和通达信同步数据两个数据库连接都失败！再见！");
				System.exit(0);
		}
		
		accountschicangconfig = new AccountAndChiCangConfiguration ();

		acntdbopt = new AccountDbOperation();
		bkdbopt = new BanKuaiDbOperation ();
		bkcyl = new BanKuaiAndChanYeLian(this);
//		chklstdialog = new BuyCheckListTreeDialog (this,"","");
//		panelcklt = chklstdialog.getCheckListPanel();
		initializeGui();
		displayAccountAndChiCang ();
		displayDbInfo();
		createEvents();
    	initializePaoMaDeng ();
 	}
	
	private ConnectDataBase connectdb = null;
	private SystemConfigration sysconfig = null;
	private AccountAndChiCangConfiguration accountschicangconfig;
	private BkChanYeLianTreeNode nodeshouldbedisplayed; 
	private BuyCheckListTreeDialog buychklstdialog;
	private BanKuaiDbOperation bkdbopt;
	private AccountDbOperation acntdbopt;
	private BanKuaiGuanLi bkgldialog = null;
	private BanKuaiFengXi bkfx ;
	private SearchDialog searchdialog;
	private BanKuaiAndChanYeLian bkcyl;
//	private BuyCheckListTreeDialog chklstdialog;
//	private JPanel panelcklt;

	
	
	
	protected void refeshSystem() 
	{
		sysconfig.reconfigSystemSettings () ;  //新的采用直接重启系统的方法，简单
	}

	
	/*
	 * 把持仓显示在相应的位置
	 */
	private void displayAccountAndChiCang()
	{
		//在checkbox添加现有持仓，方便启动后就可以查询现有持仓
		ArrayList<String> tmpchicangname = accountschicangconfig.getStockChiCangName();
		cBxstockcode.removeAllItems();
		for(String tmpsgstockcodename:tmpchicangname) {

			cBxstockcode.addItem(tmpsgstockcodename );
		}
		try {
			kspanel.setStockcode(cBxstockcode.getSelectedItem().toString().substring(0, 6));
		} catch (java.lang.NullPointerException e) {
			
		}
	}
	private void initializePaoMaDeng() 
	{
		// TODO Auto-generated method stub
		String title = "明日计划:";
//		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.DAY_OF_MONTH, -1);
//		Date ystday = cal.getTime();
		//asinglestockinfomation = new ASingleStockOperations("");
		String paomad = bkdbopt.getMingRiJiHua();
		
		if(!paomad.isEmpty())
			pnl_paomd.refreshMessage(title+paomad);
		else pnl_paomd.refreshMessage(null);
	}

	/*
	 * @数据库可以有2个数据库，一个存放基本数据，一个存放通达信同步数据，这样减轻基本数据库的负荷	
	 */
		private void displayDbInfo() 
		{
			boolean localconnect = false;
			boolean rmtconnect = false;
			if(connectdb.isRemoteDatabaseconnected()){
				btnDBStatus.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/database_23.147208121827px_1201712_easyicon.net.png")));
				lblStatusBarOperationIndicatior.setText(lblStatusBarOperationIndicatior.getText()+ connectdb.getRemoteDatabaseName("full")+"数据库已连接");
				btnDBStatus.setToolTipText(btnDBStatus.getToolTipText() + connectdb.getLocalDatabaseName("full")+"数据库已连接");
				rmtconnect = true;
			}
			
			if(connectdb.isLocalDatabaseconnected()){
				btnDBStatus.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/database_23.147208121827px_1201712_easyicon.net.png")));
				lblStatusBarOperationIndicatior.setText(lblStatusBarOperationIndicatior.getText() + connectdb.getLocalDatabaseName("full")+"数据库已连接");
				btnDBStatus.setToolTipText(btnDBStatus.getToolTipText() + connectdb.getLocalDatabaseName("full")+"数据库已连接");
				localconnect = true;
			}

//			 if(localconnect == false && rmtconnect == true) {
//				JOptionPane.showMessageDialog(null,"仅通达信同步数据可用！");
//			} else if(localconnect == true && rmtconnect == false) {
//				JOptionPane.showMessageDialog(null,"仅基本数据可用！");
//			} else{
//				btnDBStatus.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/database_23.147208121827px_1201711_easyicon.net.png")));
//				lblStatusBarOperationIndicatior.setText(connectdb.getLocalDatabaseName("full")+"数据库连接失败");
//				btnDBStatus.setToolTipText(connectdb.getLocalDatabaseName("full")+"数据库连接失败");
//				JOptionPane.showMessageDialog(null,"基本数据和通达信同步数据两个数据库连接都失败！再见！");
//				System.exit(0);
//			}
			
			
		}
		
		public void preUpdateSearchResultToGui(String stockcode) 
		{
				btngengxinxx.setEnabled(false);
				clearGuiDispalyedInfo ();
				
//				 ArrayList<BkChanYeLianTreeNode> nodeslist = bkdbopt.getNodesBasicInfo (stockcode);
//				 if(nodeslist.size() == 0) {
//					 JOptionPane.showMessageDialog(null,"股票/板块代码不存在，请再次输入正确股票代码！");
//					 return;
//				 }
				 
//				 BkChanYeLianTreeNode nodeshouldbedisplayed = null;
//				 if(nodeslist.size()>1) {
//					 //显示JLIST，让用户选择
//					 SelectMultiNode userselection = new SelectMultiNode(nodeslist);
//					 int exchangeresult = JOptionPane.showConfirmDialog(null, userselection, "请选择", JOptionPane.OK_CANCEL_OPTION);
//					 if(exchangeresult == JOptionPane.CANCEL_OPTION)
//							return;
//					 
//					 int userselected = userselection.getUserSelection();
//					 nodeshouldbedisplayed = nodeslist.get(userselected);
//				 } else
//					 nodeshouldbedisplayed = nodeslist.get(0);
				
				 displayStockJibenmianInfotoGui (); //显示板块或者股票的基本信息
				 if(!sysconfig.getPrivateModeSetting()) { //隐私模式不显示持仓信息
					 nodeshouldbedisplayed = bkdbopt.getZdgzMrmcZdgzYingKuiFromDB(nodeshouldbedisplayed);
					 displaySellBuyZdgzInfoToGui ();
				 }
//					initializeNetWorkOperation (stockcode); //生成对应的网站网址
				 
				 if(nodeshouldbedisplayed.getType() == 6) { //是个股
					if(accountschicangconfig.isSystemChiCang(stockcode)) {
						nodeshouldbedisplayed = accountschicangconfig.setStockChiCangAccount((Stock)nodeshouldbedisplayed);
					} 
					
					nodeshouldbedisplayed = bkdbopt.getCheckListsXMLInfo ((Stock)nodeshouldbedisplayed);
//					nodeshouldbedisplayed = bkdbopt.getZdgzMrmcZdgzYingKuiFromDB((Stock)nodeshouldbedisplayed);
//					nodeshouldbedisplayed = bkdbopt.getTDXBanKuaiForAStock ((Stock)nodeshouldbedisplayed); //通达信板块信息
					
					
					nodeshouldbedisplayed = bkcyl.getStockChanYeLianInfo ((Stock)nodeshouldbedisplayed);
					
					if(!sysconfig.getPrivateModeSetting()) { //隐私模式不显示持仓信息
						displayAccountTableToGui ();
//						displaySellBuyZdgzInfoToGui ();
					}
					displayStockNews ();
					displayStockSuoShuBanKuai ();
					setKuaiSuGui (stockcode);
					if(buychklstdialog!= null ) {
						displayChecklistsItemsToGui ();
					}
				 } 
				enableGuiEditable();
		}
	
	private void initlizedBuyCheckListTreeDialog ()
	{
		if(buychklstdialog == null ) {
			try {
				buychklstdialog = new BuyCheckListTreeDialog (this,nodeshouldbedisplayed.getMyOwnCode(),((Stock)nodeshouldbedisplayed).getChecklistXml());
			} catch (java.lang.NullPointerException ex) {
				buychklstdialog = new BuyCheckListTreeDialog (this,null,null);
			}
		} else {
			
		}
				
		if(!buychklstdialog.isVisible() ) 
			 buychklstdialog.setVisible(true);
		buychklstdialog.toFront();
	}

	private void createEvents()
	{
		
		
		menuItembkfx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				startBanKuaiFengXi ();
////				if(bkfx == null ) {
//				BanKuaiFengXi bkfx = new BanKuaiFengXi (bkcyl.getBkChanYeLianTree(),"","",dateChsBanKuaiZhanbi.getDate());
//				bkfx.setModal(false);
//				bkfx.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//				bkfx.setVisible(true);
////			} 
//			
////			if(!bkfx.isVisible() ) {
////				bkfx.setVisible(true);
////			 } 
//			bkfx.toFront();
			}
		});
		
		btndetailfx.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				startBanKuaiFengXi ();


				
			}
		});
		
//		cBxstockcode.getEditor().getEditorComponent().addMouseListener(new MouseAdapter() {
//			@Override
//			public void mousePressed(MouseEvent arg0) {
//				//System.out.println("this is the test");
//				
//			}
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				
//				cBxstockcode.getEditor().setItem("");
//			}
//			
//		});
		
//		cBxstockcode.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
//			public void keyPressed(KeyEvent  e)
//			{
//				if(e.getKeyCode() == KeyEvent.VK_ENTER)
//				{
//				}
//			}
//			
//		});
		
		
		cBxstockcode.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e) 
			{
				if(e.getStateChange() == ItemEvent.SELECTED) {
					
					nodeshouldbedisplayed = cBxstockcode.getUserInputNode();
					preUpdateSearchResultToGui(nodeshouldbedisplayed.getMyOwnCode());
//					if(userinputnode.getType() == 6 ) {
//						tfldgeguname.setText(userinputnode.getMyOwnName());
//						displayStockSuoShuBanKuai((Stock)userinputnode);
//					}
					
//					String stockcode;
//					try	{
//						stockcode = formatStockCode((String)cBxstockcode.getSelectedItem());
//						
//						if(!checkCodeInputFormat(stockcode)) {
//							JOptionPane.showMessageDialog(null,"股票/板块代码有误！");
//							return;
//						}
//						
//						preUpdateSearchResultToGui(stockcode);
//						updateStockCombox();
//					} catch(java.lang.NullPointerException ex)	{
//						ex.printStackTrace();
//						JOptionPane.showMessageDialog(frame, "请输入股票代码！","Warning", JOptionPane.WARNING_MESSAGE);
//						return;
//					} catch(java.lang.StringIndexOutOfBoundsException ex2) {
//						ex2.printStackTrace();
//						JOptionPane.showMessageDialog(null,"股票代码有误！");
//						return;
//					}
				}
				
				if(e.getStateChange() == ItemEvent.DESELECTED) {
					if(btngengxinxx.isEnabled() == true) {
						int action = JOptionPane.showConfirmDialog(null, "更改后的信息未存！是否需要保存？","Warning", JOptionPane.YES_NO_OPTION);
						if(0 == action) {
							setGuiInfoToDatabase();
						}
					}
				}
			}
			
		});
		
		
		//为个股板块信息的hyperlink注册时间  http://www.javalobby.org/java/forums/t19716.html
//		 ActionMap actionMap = new ActionMap(); 
//	     actionMap.put("openBanKuaiAndChanYeLianDialog", new AbstractAction (){
//			public void actionPerformed(ActionEvent e) {
//				 	HyperlinkEvent hle = (HyperlinkEvent)e.getSource();
//				 	String link = null;
//			        try{
//			        	Element elem = hle.getSourceElement(); 
//			            Document doc = elem.getDocument(); 
//			            int start = elem.getStartOffset(); 
//			            int end = elem.getEndOffset(); 
//			            link = doc.getText(start, end-start);
//			            System.out.println(link);
////			            link = link.equals("contains people") ? "" : link.substring("contains ".length()); 
//			            StringTokenizer stok = new StringTokenizer(link, ", "); 
//			            while(stok.hasMoreTokens()){ 
//			                String token = stok.nextToken(); 
//			            }
//			        } catch(BadLocationException ex){ 
//			            ex.printStackTrace(); 
//			        }
//			        
//			        
//			        btndetailfx.setEnabled(true);
//			        displayBanKuaiOfStockZhanBiByWeek (link);
//			        displayStockBanKuaiZhanBiByStock (link);
//			}
//	     }); 
//	     editorPaneBanKuai.addHyperlinkListener(new ActionBasedBanKuaiAndChanYeLianHyperlinkListener(actionMap)); 

			/*
			 * 导入交易记录
			 */
		menuItemimportrecords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				AccountSeeting accountsetting= new AccountSeeting(accountschicangconfig,frame); 
				accountsetting.setVisible(true);
				
			}
//			
//			boolean isBuyOrSell (String descriptioninrecords) 
//			{
//				boolean buysell ;
//				if(descriptioninrecords.contains("买入") || descriptioninrecords.contains("红股入帐") || descriptioninrecords.contains("申购中签") )
//					buysell = true;
//				else buysell = false;
//				
//				return buysell;
//			}
			
		});
		
		menuItemSysSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				refeshSystem ();
			}
		});
				
		menuItemChanYeLian.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				startBanKuaiGuanLiDlg ();
				
			}
		});
		
		menuItemRfshBk.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				//BanKuaiDbOperation bkdbopt = new BanKuaiDbOperation ();
				ImportTDXData importtdx= new ImportTDXData();
				importtdx.setModal(true);
				importtdx.setVisible(true);
			}
		});
		
		
		menuItemChexiao.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{

//				int rowIndex = tblzhongdiangz.getSelectedRow();
//				
//				String actiontype = (String)((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 1);
//				String actiondate = (String)((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 0);
//				if(actiontype.contains("挂单") && !actiondate.contains(sysconfig.formatDate(new Date()).substring(0, 10) )  ) { 
//					JOptionPane.showMessageDialog(null,"只有今日挂单可以撤销，历史挂单不可以撤销！");
//					return ;
//				} else if( !actiontype.contains("挂单") ) {
//					JOptionPane.showMessageDialog(null,"不是挂单记录！");
//					return ;
//				}
				
//				Object dabataseidstr =  ((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 3);
//				Integer dabataseid = Integer.parseInt(dabataseidstr.toString() );
//				String actionstockaccount = (String) ((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 4);
//				
//				AccountInfoBasic tmpaccount = accountschicangconfig.getAccount(actionstockaccount); //从几个账户list里面找到当前操作的账户
//				BuyStockNumberPrice stocknumberpricepanel = acntdbopt.setGuadanExchangeFinishedActions(tmpaccount,dabataseid.intValue());
//				
//				if(stocknumberpricepanel != null ) {
//					((DefaultTableModel)tblzhongdiangz.getModel()).setValueAt(actiontype.replace("挂单", ""), rowIndex, 1);
//					
//					if(stocknumberpricepanel.isBuySell() )
//						accountschicangconfig.setBuyStockChiCangRelatedActions (stocknumberpricepanel);
//					else { 
//						accountschicangconfig.setSellStockChiCangRelatedActions (stocknumberpricepanel);
//						postSellAction (stocknumberpricepanel);
//					}
//				}

//				refreshChiCangAccountPanel (stocknumberpricepanel);

			}
		});
		
		btnSearch.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) 
			{
				String stockcode = "";
				try{
					stockcode = formatStockCode((String)cBxstockcode.getSelectedItem());
				} catch (java.lang.NullPointerException ex) {
					ex.printStackTrace();
				} 
				ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (stockcode);
				int exchangeresult = JOptionPane.showConfirmDialog(null, cylnews, "增加个股新闻", JOptionPane.OK_CANCEL_OPTION);
				System.out.print(exchangeresult);
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;
				
			}
		});
		
		
		
		mntmNewMenuItem_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				initializeSearchDialog();
				
				if(!searchdialog.isVisible() ) {
					 searchdialog.setVisible(true);
				 } 
				searchdialog.toFront();
				
			}
		});
		
		btnXueQiu.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				
			}
		});
		
		btnEnableChklsts.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				initlizedBuyCheckListTreeDialog ();
			}
		});
		
		KeyListener kl = new KeyAdapter()
		{
			public void keyPressed(KeyEvent kevt) {
				 if(kevt.getKeyChar()=='c') {
				 
				  if(kevt.isControlDown()) {
					  System.out.println("ctrl c");
				  }
				 
				}
			}
			
		};
		
		tfdCustom.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) 
			{
				btngengxinxx.setEnabled(true);
			}
		});
		
		tfdJingZhengDuiShou.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) 
			{
				btngengxinxx.setEnabled(true);
			}
		});
		
		menuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				AccountSeeting accountsetting= new AccountSeeting(accountschicangconfig,frame); 
				accountsetting.setVisible(true);

				String stockcode = "";
				try{
					stockcode = cBxstockcode.getSelectedItem().toString();
				} catch (java.lang.NullPointerException e) {
					
				}
				kspanel = new BuyStockNumberPrice(stockcode,accountschicangconfig,true);
				tabbedPane.remove(0);
				tabbedPane.addTab("\u4E70\u5165\u5FEB\u901F\u8BB0\u5F55", null, kspanel, null);
			}
		});
			
		
				

		
		 Action tableaction = new AbstractAction()  {
			private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e)
		        {
		            TableCellListener tcl = (TableCellListener)e.getSource();
//		            System.out.println("Row   : " + tcl.getRow());
//		            System.out.println("Column: " + tcl.getColumn());
		            System.out.println("Old   : " + tcl.getOldValue());
		            System.out.println("New   : " + tcl.getNewValue());
		            
		            Object[] tabledata= new Object [6] ;
		            tabledata[0] = tblzhongdiangz.getModel().getValueAt(tcl.getRow(),0);
		            tabledata[1] = tblzhongdiangz.getModel().getValueAt(tcl.getRow(),1);
		            tabledata[2] = tblzhongdiangz.getModel().getValueAt(tcl.getRow(),2);
		            tabledata[3] = tblzhongdiangz.getModel().getValueAt(tcl.getRow(),3);
		            tabledata[4] = tblzhongdiangz.getModel().getValueAt(tcl.getRow(),4);
		            tabledata[5] = tblzhongdiangz.getModel().getValueAt(tcl.getRow(),5);
		            
		            setTableNewInfoToDB (tabledata);
		            btngengxinxx.setEnabled(true);
		        }
		    };

		TableCellListener tcl = new TableCellListener(tblzhongdiangz, tableaction);
		    
		tblzhongdiangz.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if ("tableCellEditor".equals(e.getPropertyName()))
				{
					if (tblzhongdiangz.isEditing())
						System.out.println("edit");
					else
						System.out.println("unedit");
				}
			}
		});
		
		tblzhongdiangz.getModel().addTableModelListener(new TableModelListener()  
        {  
            @Override  
            public void tableChanged(TableModelEvent e)  
            {  
                 //System.out.println("action");     
                //int col = e.getColumn();  
                //int row = e.getFirstRow();  
//                System.out.print(row);
//                System.out.print(col);
//                System.out.println("/////");
  
            }  
        });
		
		txtFldStockName.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event) {
				if(cBxstockcode.getSelectedItem().toString().trim().length() == 0)
					return;
				
				if (SwingUtilities.isRightMouseButton(event)) {
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Clipboard clipboard = toolkit.getSystemClipboard();
					try	{
						String result = (String) clipboard.getData(DataFlavor.stringFlavor);
						txtFldStockName.setText(txtFldStockName.getText() + result);
						btngengxinxx.setEnabled(true);
					}catch(Exception ex)	{
						System.out.println(ex);
					}
				}
			}
		});
		
		
		
		/*
		 * 移出重点关注
		 */
		btnyichuzdgz.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				JiaRuJiHua jiarujihua = new JiaRuJiHua ( formatStockCode((String)cBxstockcode.getSelectedItem()),"移出关注"); 
				int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "计划细节", JOptionPane.OK_CANCEL_OPTION);
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;
				
				int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
				
				if(autoIncKeyFromApi >0) {
					DefaultTableModel tableModel = (DefaultTableModel) tblzhongdiangz.getModel();
					//String stockcode = jiarujihua.getStockCode();		
					String addedday = CommonUtility.formatDateYYYY_MM_DD_HHMMSS( LocalDateTime.now());
					String zdgzsign = "移出关注";
					String shuoming = "";
					if(jiarujihua.isMingRiJiHua()) {
						zdgzsign = "明日计划";
						shuoming = jiarujihua.getJiHuaLeiXing() + "(价格" + jiarujihua.getJiHuaJiaGe() + ")(" +  jiarujihua.getJiHuaShuoMing();
					} else
						shuoming =  jiarujihua.getJiHuaShuoMing();
					
					Object[] tableData = new Object[] { addedday, zdgzsign,  shuoming,autoIncKeyFromApi," ","操作记录重点关注"};
					tableModel.insertRow(0, tableData);
					tblzhongdiangz.setEditingColumn(3);
				}
			}
		});
		
		/*
		 * 加入重点关注
		 */
		btnjiaruzdgz.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				if(nodeshouldbedisplayed.getType() == 6) {
					JiaRuJiHua jiarujihua = new JiaRuJiHua ( formatStockCode((String)cBxstockcode.getSelectedItem()),"加入关注" ); 
					int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "加入关注", JOptionPane.OK_CANCEL_OPTION);
					if(exchangeresult == JOptionPane.CANCEL_OPTION)
						return;
					
					 int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
					 jiarujihua.setDbRecordsId(autoIncKeyFromApi);
					 
					 updateTableAfterZdgz (jiarujihua);
					
				} else if(nodeshouldbedisplayed.getType() == 4) {
//					bkcyl.startGui ();
					bkcyl.findBanKuaiInTree(nodeshouldbedisplayed.getMyOwnCode() );
					startBanKuaiGuanLiDlg ();
					
				}
				
				
//				JiaRuJiHua jiarujihua = new JiaRuJiHua ( formatStockCode((String)cBxstockcode.getSelectedItem()),"加入关注" ); 
//				int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "加入关注", JOptionPane.OK_CANCEL_OPTION);
//				if(exchangeresult == JOptionPane.CANCEL_OPTION)
//					return;
//				
//				int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
				
//				if(autoIncKeyFromApi >0 && !sysconfig.getPrivateModeSetting() ) {
//					DefaultTableModel tableModel = (DefaultTableModel) tblzhongdiangz.getModel();
//					//String stockcode = jiarujihua.getStockCode();		
//					String addedday = CommonUtility.formatDateYYYY_MM_DD_HHMMSS( LocalDateTime.now() );
//					String zdgzsign = "加入关注";
//					String shuoming = "";
//					if(jiarujihua.isMingRiJiHua()) {
//						zdgzsign = "明日计划";
//						shuoming = jiarujihua.getJiHuaLeiXing() + "(价格" + jiarujihua.getJiHuaJiaGe() + ")(" +  jiarujihua.getJiHuaShuoMing();
//					} else
//						shuoming =  jiarujihua.getJiHuaShuoMing();
//					
//					Object[] tableData = new Object[] { addedday, zdgzsign,  shuoming,autoIncKeyFromApi," ","操作记录重点关注"};
//					tableModel.insertRow(0, tableData);
//					tblzhongdiangz.setEditingColumn(3);
//				}

//				if(updateZdgzActionToGuiDB("加入关注",jiarujihua)) {
//					btngengxinxx.setEnabled(true);
//					sclpaneJtable.getVerticalScrollBar().setValue(0);
//					//添加或移出后应该在股票信息里面加入当天，以防用户2次点击，这里是简单的做法，之间吧按钮禁止，以后有时间在拓展。
//					btnjiaruzdgz.setEnabled(false);
//					updateSearchDialog ();
//				}
			}

			
		});
		/*
		 * 目前可以对当日的买入记录更改买入账号,卖出记录因为涉及情况比较多，不支持
		 */
		menuItemGenggai.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				int rowIndex = tblzhongdiangz.getSelectedRow();
				
				String actiontype = (String)((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 1);
				String actiondate = (String)((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 0);
				LocalDate ldactiondate = CommonUtility.formateStringToDate(actiondate); 
				boolean sellbuy = false;
				
				//if( !actiontype.contains("买入") &&  !actiontype.contains("卖出")) {
				if( !actiontype.contains("买入") ) {
					JOptionPane.showMessageDialog(null,"不是买入记录，无法更改操作账户，当前只支持买入记录更改账户！");
					return ;
				} else if (  ldactiondate.isEqual(LocalDate.now() ) ) {
					JOptionPane.showMessageDialog(null,"只有当日买入记录才支持更改账户！");
					return ;
				} else if( actiontype.contains("买入") ) {
					sellbuy = true;
				}
				Object dabataseidstr =  ((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 3);
				Integer dabataseid = Integer.parseInt(dabataseidstr.toString() );
				//String actiondate = (String)((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 0);
				String stockcode = formatStockCode( (String)cBxstockcode.getSelectedItem() );
				String currentacnt = (String)((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 4);
				
				GengGaiZhangHu ggzhpnl = new GengGaiZhangHu( stockcode,  currentacnt,  sellbuy,  accountschicangconfig);
				int exchangeresult = JOptionPane.showConfirmDialog(null, ggzhpnl, "更改买入操作账户", JOptionPane.OK_CANCEL_OPTION);
				System.out.print(exchangeresult);
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;
				
				String newacnt = ggzhpnl.getNewAccount ();	
				
				BuyStockNumberPrice stocknumberpricepanel  = accountschicangconfig.changeBuyRecordAccountYuanZiChaoZuo (currentacnt,newacnt,dabataseid); ;
				
				//修改界面 { "日期", "操作", "说明","ID","操作账户","信息表" };
				((DefaultTableModel)tblzhongdiangz.getModel()).setValueAt( newacnt, rowIndex, 4); //更新账户
				((DefaultTableModel)tblzhongdiangz.getModel()).setValueAt( stocknumberpricepanel.getDatabaseid(), rowIndex, 3); //更新数据库ID
				
				refreshChiCangAccountPanel ();
			}
		});
		/*
		 * 挂单成交
		 *	
		 */
		mntmChengjiao.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent arg0)
			{
				//System.out.println("popup");
				int rowIndex = tblzhongdiangz.getSelectedRow();
				
				String actiontype = (String)((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 1);
				
 				String actiondate = (String)((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 0);
 				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
 				LocalDateTime ldactiondate = LocalDateTime.parse(actiondate, formatter);
 				
				if(actiontype.contains("挂单") && !ldactiondate.toLocalDate().isEqual(LocalDate.now() )  ) {    
					JOptionPane.showMessageDialog(null,"只有今日挂单可以转为成交，历史挂单不可以转为成交！");
					return ;
				} else if( !actiontype.contains("挂单") ) {
					JOptionPane.showMessageDialog(null,"不是挂单记录！");
					return ;
				}
				
				Object dabataseidstr =  ((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 3);
				Integer dabataseid = Integer.parseInt(dabataseidstr.toString() );
				String actionstockaccount = (String) ((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 4);
				
				AccountInfoBasic tmpaccount = accountschicangconfig.getAccount(actionstockaccount); //从几个账户list里面找到当前操作的账户
				BuyStockNumberPrice stocknumberpricepanel = acntdbopt.setGuadanExchangeFinishedActions(tmpaccount,dabataseid.intValue());
				if(stocknumberpricepanel != null ) {
					((DefaultTableModel)tblzhongdiangz.getModel()).setValueAt(actiontype.replace("挂单", ""), rowIndex, 1);
					((DefaultTableModel)tblzhongdiangz.getModel()).setValueAt(stocknumberpricepanel.getJiaoYiShuoMing(), rowIndex, 2); // { "日期", "操作", "说明","ID","操作账户","信息表" };
					
					accountschicangconfig.setGuaDanFinished (stocknumberpricepanel); //处理账户改变
					
					//处理持仓
					if(stocknumberpricepanel.isBuySell() ) //买
						accountschicangconfig.setBuyStockChiCangRelatedActions (stocknumberpricepanel);
					else { //卖
						accountschicangconfig.setSellStockChiCangRelatedActions (stocknumberpricepanel);
						postSellAction (stocknumberpricepanel);
					}
				}

				refreshChiCangAccountPanel ();
			}
	
		});
		/*
		 * 送转股票
		 */
		btnSongZhuanGu.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				int rowindex = tableStockAccountsInfo.getSelectedRow();
				if( rowindex <0 ) {
					JOptionPane.showMessageDialog(null,"请选择一个持仓账户");
					return;
				}

				AccountInfoBasic tmpactionacnt = (AccountInfoBasic) ((AccountsInfoTableModel)tableStockAccountsInfo.getModel()).getAccountsAt(rowindex);
				String stockcode = formatStockCode((String)cBxstockcode.getSelectedItem());
				String actionstockaccount = tmpactionacnt.getAccountName();
				int curgushu = tmpactionacnt.getStockChiCangInfoIndexOf(stockcode).getChicanggushu();
				
		    	String thisprofit = JOptionPane.showInputDialog(null,"请输入送配后总股数:","送配信息", JOptionPane.QUESTION_MESSAGE);
				int songpeigushu = Integer.parseInt(thisprofit);
				
				BuyStockNumberPrice stocknumberpricepanel = new BuyStockNumberPrice ( stockcode,null,true);
				stocknumberpricepanel.setJiaoYiGuShu(songpeigushu - curgushu); //相当于用0元买入这么多股票
				stocknumberpricepanel.setJiaoYiJiaGe(0.0);
				stocknumberpricepanel.setJiaoyiZhanghu (actionstockaccount);
				
				accountschicangconfig.buySellYuanZiOpertion (stocknumberpricepanel);
				//refreshChiCangAccountPanel (stocknumberpricepanel);
				refreshChiCangAccountPanel ();

			}
		});

		
		btnMai.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				if(kspanel.getJiaoyiGushu()>0 && kspanel.getJiaoyiJiage()>0) {
					saveKuaiSuJiLuJiaoYi ();
					return ;
				}
					
					
				BuyStockNumberPrice stocknumberpricepanel = new BuyStockNumberPrice ( formatStockCode((String)cBxstockcode.getSelectedItem()),accountschicangconfig,true);
				int exchangeresult = JOptionPane.showConfirmDialog(null, stocknumberpricepanel, "买入交易细节", JOptionPane.OK_CANCEL_OPTION);
				System.out.print(exchangeresult);
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;
				
				try {
						@SuppressWarnings("unused")
						int stocknumber = stocknumberpricepanel.getJiaoyiGushu();
						@SuppressWarnings("unused")
						double stockprice = stocknumberpricepanel.getJiaoyiJiage();
					} catch (java.lang.NumberFormatException e) {
						JOptionPane.showMessageDialog(frame, "股价或股数输入有误，请重新输入！","Warning", JOptionPane.WARNING_MESSAGE);
						return;
				}
				
				int autoIncKeyFromApi = accountschicangconfig.buySellYuanZiOpertion (stocknumberpricepanel);
				
				if(autoIncKeyFromApi > 0) {
					stocknumberpricepanel.setDatabaseid(autoIncKeyFromApi);
					updatActionResultToGui (stocknumberpricepanel);
			    } else {
			    	JOptionPane.showMessageDialog(frame, "账户现金余额可能不足，无法买入！","Warning", JOptionPane.WARNING_MESSAGE);
			    	return;
			    }

				refreshChiCangAccountPanel ();
				
			}

		});
		
		btnSell.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				BuyStockNumberPrice stocknumberpricepanel = new BuyStockNumberPrice ( formatStockCode((String)cBxstockcode.getSelectedItem()),accountschicangconfig,false);
				int exchangeresult = JOptionPane.showConfirmDialog(null, stocknumberpricepanel, "卖出交易细节", JOptionPane.OK_CANCEL_OPTION);
				System.out.print(exchangeresult);
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;
				
						
				try {
						int stocknumber = stocknumberpricepanel.getJiaoyiGushu();
						double stockprice = stocknumberpricepanel.getJiaoyiJiage();
					} catch (java.lang.NumberFormatException e) {
						JOptionPane.showMessageDialog(frame, "股价或股数输入有误，请重新输入！","Warning", JOptionPane.WARNING_MESSAGE);
						return;
				}
				
				String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu();
				AccountInfoBasic tmpactionacnt = accountschicangconfig.getAccount(actionstockaccount);
				int curchicanggushu = tmpactionacnt.getChiCangGuPiaoGuShu(stocknumberpricepanel.getStockcode() );
				if(curchicanggushu - stocknumberpricepanel.getJiaoyiGushu() == 0) {
					int result = JOptionPane.showConfirmDialog(frame, "账户 " + stocknumberpricepanel.getJiaoyiZhanghu() + " 卖出后持仓将为0，必须确认账户是否正确！是否继续？","Warning", JOptionPane.YES_NO_OPTION);
					if(1 == result)
						return ;
				}
				
				int autoIncKeyFromApi = accountschicangconfig.buySellYuanZiOpertion (stocknumberpricepanel);

				if(autoIncKeyFromApi > 0) {
					stocknumberpricepanel.setDatabaseid(autoIncKeyFromApi);
					updatActionResultToGui (stocknumberpricepanel);
			    }else {
			    	JOptionPane.showMessageDialog(frame, "账户可能没有这么多数量股票，无法卖出！","Warning", JOptionPane.WARNING_MESSAGE);
			    	return;
			    }
	
				postSellAction (stocknumberpricepanel);
				
				refreshChiCangAccountPanel ();

			}

		
		});
		
		btnSlack.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mousePressed(MouseEvent arg0) 
			{

			}
		});
		
				
		
		btnCaiwufengxi.addMouseListener(new MouseAdapter() 
		{
			
		
			public void mousePressed(MouseEvent e) 
			{

			}
		});
		
		btnDongcaiyanbao.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mousePressed(MouseEvent e) 
			{

			}

		});
		
		btnRongzirongquan.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mousePressed(MouseEvent e) 
			{

			}
			
		});
		
		btnXueqiu.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mousePressed(MouseEvent arg0) 
			{

			}
			
		});
		
		btnhudongyi.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mousePressed(MouseEvent arg0) 
			{

			}
		});

		
		
//		menuOpenModelRecord.addMouseListener(new MouseAdapter() 
//		{
//			@Override
//			public void mousePressed(MouseEvent e) 
//			{
//				try {
//					String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconfig.getMoxingjilupath() + sysconfig.getMoxingjilufilename();
//					Process p  = Runtime.getRuntime().exec(cmd);
//					p.waitFor();
//				} catch (Exception e1) 
//				{
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//		});
		
		mntmopenlcldbfile.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				String dbfile = null;
				switch (connectdb.getLocalDatabaseType().toLowerCase() ) {
				case "mysql":    dbfile = "D:\\tools\\HeidiSQL\\heidisql.exe";
					break;
				case "access":  dbfile = connectdb.getLocalDatabaseName("full");
					break;
				}
				try {
					String cmd = "rundll32 url.dll,FileProtocolHandler " + dbfile;
					System.out.println(cmd);
					Process p  = Runtime.getRuntime().exec(cmd);
					p.waitFor();
				}catch (Exception e1){
					e1.printStackTrace();
				}
			}
		});
		mntmOpenRmtDb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				String dbfile = null;
				switch (connectdb.getRemoteDatabaseType().toLowerCase() ) {
				case "mysql":    dbfile = "D:\\tools\\HeidiSQL\\heidisql.exe";
					break;
				case "access":  dbfile = connectdb.getLocalDatabaseName("full");
					break;
				}
				try {
					String cmd = "rundll32 url.dll,FileProtocolHandler " + dbfile;
					System.out.println(cmd);
					Process p  = Runtime.getRuntime().exec(cmd);
					p.waitFor();
				}catch (Exception e1){
					e1.printStackTrace();
				}
				
			}
		});
		
//		menuOpenDailyReport.addMouseListener(new MouseAdapter() 
//		{
//			@Override
//			public void mousePressed(MouseEvent e) 
//			{
//				try {
//					String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconfig.getMeirijilupath() + sysconfig.getMeirijilufilename();
//					Process p  = Runtime.getRuntime().exec(cmd);
//					p.waitFor();
//				} catch (Exception e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//		});
		
		txtfldfuxg.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent event) 
			{
				if(cBxstockcode.getSelectedItem().toString().trim().length() == 0)
					return;
				
			    if (SwingUtilities.isRightMouseButton(event))  {
			    	Toolkit toolkit = Toolkit.getDefaultToolkit();
			    	Clipboard clipboard = toolkit.getSystemClipboard();
			    	try 	{
			    		String result = (String) clipboard.getData(DataFlavor.stringFlavor);
			    		System.out.println(result);
			    		txtfldfuxg.setText(txtfldfuxg.getText() + result);
			    		btngengxinxx.setEnabled(true);
			    		dateChsgainian.setDate(new Date());
			    	} catch(Exception e)  	{
			    		e.printStackTrace();
			    		System.out.println(e);
			    	}
			    }
			 }
		});
		
		
		txtfldzhengxg.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent event) 
			{
				if(cBxstockcode.getSelectedItem().toString().trim().length() == 0)
					return;
				
			    if (SwingUtilities.isRightMouseButton(event))   {
			    	Toolkit toolkit = Toolkit.getDefaultToolkit();
			    	Clipboard clipboard = toolkit.getSystemClipboard();
			    	try	{
			    		String result = (String) clipboard.getData(DataFlavor.stringFlavor);
			    		System.out.println(result);
			    		txtfldzhengxg.setText(txtfldzhengxg.getText() + result);
			    		//txtareafumianxx.insert(result, txtareafumianxx.getCaretPosition());
			    		btngengxinxx.setEnabled(true);
			    		dateChsgainian.setDate(new Date());
			    	} catch(Exception e)	{
			    		System.out.println(e);
			    	}
			    }
			 }
		});
		
		
		txtareafumianxx.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent event) 
			{
				if(cBxstockcode.getSelectedItem().toString().trim().length() == 0)
					return;
				
				if (SwingUtilities.isRightMouseButton(event))
			    {
			     Toolkit toolkit = Toolkit.getDefaultToolkit();
			     Clipboard clipboard = toolkit.getSystemClipboard();
			     try {
			      String result = (String) clipboard.getData(DataFlavor.stringFlavor);
			      //System.out.println(result);
			      txtareafumianxx.insert(result, txtareafumianxx.getCaretPosition());
			      txtareafumianxx.insert("  ", txtareafumianxx.getCaretPosition());
			      btngengxinxx.setEnabled(true);
			      dateChsefumian.setDate(new Date());
			     } catch(Exception e) {
			      System.out.println(e);
			     }
			     
			    }
			}
		});
		
		txtfldquanshangpj.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent event) 
			{
				if(cBxstockcode.getSelectedItem().toString().trim().length() == 0)
					return;
				
				if (SwingUtilities.isRightMouseButton(event))
				{
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Clipboard clipboard = toolkit.getSystemClipboard();
					try	{
						String result = (String) clipboard.getData(DataFlavor.stringFlavor);
						System.out.println(result);
						txtfldquanshangpj.setText(txtfldquanshangpj.getText() + result);
						
						btngengxinxx.setEnabled(true);
						dateChsquanshang.setDate(new Date());
					}catch(Exception e)	{
						System.out.println(e);
					}
					
				}
			}
		});
		
		txtFldStockName.addKeyListener(new KeyAdapter() 
		{
			@Override
			public void keyTyped(KeyEvent e) 
			{
				btngengxinxx.setEnabled(true);
			}
		});
		
		txtareagainiants.addKeyListener(new KeyAdapter() 
		{
			@Override
			public void keyTyped(KeyEvent e) 
			{
				btngengxinxx.setEnabled(true);
				dateChsgainian.setDate(new Date());
			}
		});
		
		txtareagainiants.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event) 
			{
				
				if(cBxstockcode.getSelectedItem().toString().trim().length() == 0)
					return;
				
				if (SwingUtilities.isRightMouseButton(event)) {
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Clipboard clipboard = toolkit.getSystemClipboard();
					
					try	{
						String result = (String) clipboard.getData(DataFlavor.stringFlavor);
						txtareagainiants.insert(result, txtareagainiants.getCaretPosition());
						txtareagainiants.append(" ");
						btngengxinxx.setEnabled(true);
						dateChsgainian.setDate(new Date());
					}catch(Exception e)	{
						System.out.println(e);
					}
					
				}
			}
		});
		
		
		tfdCustom.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent event) 
			{
				if(cBxstockcode.getSelectedItem().toString().trim().length() == 0)
					return;
				
			    if (SwingUtilities.isRightMouseButton(event))  {
			    	Toolkit toolkit = Toolkit.getDefaultToolkit();
			    	Clipboard clipboard = toolkit.getSystemClipboard();
			    	try 	{
			    		String result = (String) clipboard.getData(DataFlavor.stringFlavor);
			    		System.out.println(result);
			    		tfdCustom.setText(tfdCustom.getText() + result);
			    		btngengxinxx.setEnabled(true);
			    		
			    	} catch(Exception e)  	{
			    		e.printStackTrace();
			    		System.out.println(e);
			    	}
			    }
			 }
		});
		
		tfdJingZhengDuiShou.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent event) 
			{
				if(cBxstockcode.getSelectedItem().toString().trim().length() == 0)
					return;
				
			    if (SwingUtilities.isRightMouseButton(event))  {
			    	Toolkit toolkit = Toolkit.getDefaultToolkit();
			    	Clipboard clipboard = toolkit.getSystemClipboard();
			    	try 	{
			    		String result = (String) clipboard.getData(DataFlavor.stringFlavor);
			    		System.out.println(result);
			    		tfdJingZhengDuiShou.setText(tfdJingZhengDuiShou.getText() + result);
			    		btngengxinxx.setEnabled(true);
			    	} catch(Exception e)  	{
			    		e.printStackTrace();
			    		System.out.println(e);
			    	}
			    }
			 }
		});
		
		btnSearchCode.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{	
				String stockcode = null;
				try	{
					stockcode = formatStockCode((String)cBxstockcode.getSelectedItem());
					preUpdateSearchResultToGui(stockcode);
					
//					if(!checkCodeInputFormat(stockcode)) {
//						System.out.println("股票代码有误");
//						JOptionPane.showMessageDialog(null,"股票代码有误！");
//						return;
//					}else {
//						preUpdateSearchResultToGui(stockcode);
//					}
				}catch(java.lang.NullPointerException e)	{
					e.printStackTrace();
					JOptionPane.showMessageDialog(frame, "请输入股票代码！","Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}catch(java.lang.StringIndexOutOfBoundsException ex2) {
					ex2.printStackTrace();
					JOptionPane.showMessageDialog(null,"股票代码有误！");
					return;
				}
			}

				
			
		});

		
		btngengxinxx.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				if(!btngengxinxx.isEnabled())
					return;
				setGuiInfoToDatabase();
				setCkLstTreeInfoToDatabase ();
				
				//setZdgzMrmcInfoToDB();
			}
			
		});
		
		
		frame.addWindowListener(new WindowAdapter() 
		{
			@Override
			public void windowClosing(WindowEvent arg0)
			{
				accountschicangconfig.SaveAccountsInfo ();
				connectdb.closeConnectedDb();
			}
		});
		
		
		
		txtareagainiants.getDocument().addDocumentListener(new DocumentListener()  
		{
			@Override
	        public void changedUpdate(DocumentEvent arg0) 
			{
				btngengxinxx.setEnabled(true);
				dateChsgainian.setDate(new Date());

	        }
			
			@Override
		    public void removeUpdate(DocumentEvent e) 
			{
	        }

			@Override
		    public void insertUpdate(DocumentEvent e) 
			{
			}

		});
		
		txtareafumianxx.addKeyListener(new KeyAdapter() 
		{
			@Override
			public void keyTyped(KeyEvent arg0) 
			{
				btngengxinxx.setEnabled(true);
				dateChsefumian.setDate(new Date());
			}
		});
		
		txtfldquanshangpj.addKeyListener(new KeyAdapter() 
		{
			@Override
			public void keyTyped(KeyEvent e) 
			{
				btngengxinxx.setEnabled(true);
				dateChsquanshang.setDate(new Date());
			}
		});
		
		txtfldzhengxg.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyTyped(KeyEvent e) 
			{
				btngengxinxx.setEnabled(true);
			    
			}
		});
		
		txtfldfuxg.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyTyped(KeyEvent e) 
			{
				btngengxinxx.setEnabled(true);
			    
			}
		});
		
		dateChsgainian.getCalendarButton().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				btngengxinxx.setEnabled(true);
			}
		});
		
		
		dateChsquanshang.getCalendarButton().addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				btngengxinxx.setEnabled(true);
			}
		});
		
		dateChsefumian.getCalendarButton().addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				btngengxinxx.setEnabled(true);
			}
		});
		
		
		menuItemTongdaxinbb.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{			
				TDXFormatedOpt.stockJiBenMianToReports();
				TDXFormatedOpt.stockZdgzReports ();
				String resultfiepath = TDXFormatedOpt.parseChanYeLianXmlToTDXReport();
//				JOptionPane.showMessageDialog(null,"报表生成成功，请在" + resultfiepath + "下查看！");
				
				int exchangeresult = JOptionPane.showConfirmDialog(null, "报表生成成功，请在" + resultfiepath + "下查看！是否打开该目录？","报表完毕", JOptionPane.OK_CANCEL_OPTION);
			
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;
				
				try {
					Desktop.getDesktop().open(new File(resultfiepath));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
}
	
	public void updateTableAfterZdgz(JiaRuJiHua jiarujihua) 
	{
		String stockcode = jiarujihua.getStockCode().trim();
		if(  !stockcode.equals(formatStockCode((String)cBxstockcode.getSelectedItem()))  )
			return;
		
		Integer autoIncKeyFromApi = jiarujihua.getDbRecordsId();
		
		if(autoIncKeyFromApi >0 && !sysconfig.getPrivateModeSetting() ) {
			DefaultTableModel tableModel = (DefaultTableModel) tblzhongdiangz.getModel();
			//String stockcode = jiarujihua.getStockCode();		
			String addedday = CommonUtility.formatDateYYYY_MM_DD_HHMMSS( LocalDateTime.now() );
			String zdgzsign = "加入关注";
			String shuoming = "";
			if(jiarujihua.isMingRiJiHua()) {
				zdgzsign = "明日计划";
				shuoming = jiarujihua.getJiHuaLeiXing() + "(价格" + jiarujihua.getJiHuaJiaGe() + ")(" +  jiarujihua.getJiHuaShuoMing();
			} else
				shuoming =  jiarujihua.getJiHuaShuoMing();
			
			Object[] tableData = new Object[] { addedday, zdgzsign,  shuoming,autoIncKeyFromApi," ","操作记录重点关注"};
			tableModel.insertRow(0, tableData);
			tblzhongdiangz.setEditingColumn(3);
		}
	}


	public BanKuaiFengXi getBanKuaiFengXi ()
	{
			return bkfx;
	}
		
	protected void startBanKuaiFengXi() 
	{
			if(bkfx == null && bkcyl.getBanKuaiFengXi() == null) {
				bkfx = new BanKuaiFengXi (this,bkcyl);
				bkfx.setModal(false);
				bkfx.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				bkfx.setVisible(true);
			} else if(bkfx == null && bkcyl.getBanKuaiFengXi() != null) {
				bkfx = bkcyl.getBanKuaiFengXi(); 
			}
			
			if(!bkfx.isVisible() ) {
				bkfx.setVisible(true);
			} 
			
			bkfx.toFront();
	}

	/*
	 * 
	 */
//	protected  void displayBanKuaiOfStockZhanBiByWeek(String firstshowbkcode) 
//	{
//		String bkcode = firstshowbkcode.trim().substring(1,7);
////		TreePath bankuaitreepath = bkcyl.getBkChanYeLianTree().locateNodeByNameOrHypyOrBkCode(bkcode);
////		BanKuai bankuai = (BanKuai) bankuaitreepath.getPathComponent(1);
////   	 	String tdxbk = bankuai.getMyOwnName(); 
////   	 	String tdxbkcode = bankuai.getMyOwnCode();
//    	
//		LocalDate curselectdate = dateChsBanKuaiZhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		BanKuai bankuai = bkcyl.getBanKuai(bkcode,curselectdate);
//		DaPan dapan =  (DaPan) bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode("000000");
//    	
//    	//板块成交额
//    	panelbkcje.resetDate();
//    	panelbkcje.setNodeJiaoYiErByWeek (bankuai,curselectdate,dapan);
//    	
//    	//板块成交额占比
//    	panelZhanBi.resetDate();
//    	panelZhanBi.setNodeZhanBiByWeek(bankuai,curselectdate,dapan);
//	}
	/*
	 * 股票在板块的半年内的占比
	 */
//	protected  void displayStockBanKuaiZhanBiByStock (String bkname)
//	{
//		String bkcode = bkname.trim().substring(1,7);
//		
//		LocalDate curselectdate = dateChsBanKuaiZhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//    	Stock stockofbankuai = bkcyl.getGeGuOfBanKuai (bkcode,nodeshouldbedisplayed .getMyOwnCode());
//    	stockofbankuai.setMyOwnName(txtFldStockName.getText());
//    	DaPan dapan =  (DaPan) bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode("000000");
//
//    	//个股成交额
//    	panelgegucje.resetDate();
//    	panelgegucje.setNodeJiaoYiErByWeek (stockofbankuai,curselectdate,dapan);
//
//    	//个股板块成交额占比
//    	pnlGeGuWkZhanBi.resetDate();
//    	pnlGeGuWkZhanBi.setNodeZhanBiByWeek(stockofbankuai,curselectdate,dapan);
//    	
//    	//个股大盘成交额占比
//    	panelGeguWkDpZhanBi.resetDate();
//    	tabbedPanegeguzhanbi.setSelectedIndex(0);
//    	panelGeguWkDpZhanBi.setNodeAndDaPanZhanBiByWeek (stockofbankuai,curselectdate,dapan);
//	}
	
	
		protected void saveKuaiSuJiLuJiaoYi() 
		{
			try {
				
				@SuppressWarnings("unused")
				int stocknumber = kspanel.getJiaoyiGushu();
				@SuppressWarnings("unused")
				double stockprice = kspanel.getJiaoyiJiage();
			} catch (java.lang.NumberFormatException ex) {
				JOptionPane.showMessageDialog(frame, "股价或股数输入有误，请重新输入！","Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			if(btngengxinxx.isEnabled() == true) {
				int action = JOptionPane.showConfirmDialog(null, "更改后的信息未存！是否需要保存？","Warning", JOptionPane.YES_NO_OPTION);
				if(0 == action) {
					setGuiInfoToDatabase();
				}
			}
			
			kspanel.setActionDay(new Date());
			int autoIncKeyFromApi = accountschicangconfig.buySellYuanZiOpertion (kspanel); 
			
			if(autoIncKeyFromApi > 0) {
				cBxstockcode.updateUserSelectedNode(kspanel.getStockcode());
				preUpdateSearchResultToGui(kspanel.getStockcode());
//				updateStockCombox();
				
				kspanel.resetInput(); 
			} else {
				JOptionPane.showMessageDialog(frame, "账户现金余额可能不足，无法买入！","Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			refreshChiCangAccountPanel ();
			
		}
		
		protected void startBanKuaiGuanLiDlg()
		{
			if(bkgldialog == null ) {
				bkgldialog = new BanKuaiGuanLi(this,bkcyl);
				bkgldialog.setModal(false);
		//		bkgldialog.startDialog ();
				bkgldialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				bkgldialog.setVisible(true);
			} 
			
			if(!bkgldialog.isVisible() ) {
				bkgldialog.setVisible(true);
			 } 
			bkgldialog.toFront();
		}

	protected void refreshChiCangAccountPanel ()
	{
		String stockcode = formatStockCode((String)cBxstockcode.getSelectedItem()); 
//		((AccountsInfoTableModel)tableStockAccountsInfo.getModel()).refresh(accountschicangconfig.getStockChiCangAccount(stockcode),stockcode );
//		((AccountsInfoTableModel)tableStockAccountsInfo.getModel()).fireTableDataChanged();
//		HashMap<String, AccountInfoBasic> accountsnamelist = ((Stock)nodeshouldbedisplayed).getChiCangAccounts();
		displayAccountTableToGui ();
		
		
	}

	protected void postSellAction(BuyStockNumberPrice stocknumberpricepanel)
	{
		
		String stockcode = stocknumberpricepanel.getStockcode();
		String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		AccountInfoBasic accountInfoBasic = accountschicangconfig.getAccount(actionstockaccount);
		if(!accountInfoBasic.isChiCang(stockcode) ) { //判断该账户里面是否还有该股票，没有就说明已经卖完了
			
			acntdbopt.setChicangKongcang (accountInfoBasic,stockcode);
			
			String thisprofit = JOptionPane.showInputDialog(null,"持股已卖完，请输入盈亏金额:",stocknumberpricepanel.getProfit());
			double geguprofit = Double.parseDouble(thisprofit);
			stocknumberpricepanel.setProfit(geguprofit);
			 
			int autoIncKeyFromApi = accountschicangconfig.afterSellCheckAndSetProfitYuanZiOperation (accountInfoBasic,stocknumberpricepanel);
			
//			java.util.Calendar Cal=java.util.Calendar.getInstance();
//			Cal.setTime();
//			Cal.add(java.util.Calendar.SECOND,10);
			String addedday = CommonUtility.formatDateYYYY_MM_DD_HHMMSS( stocknumberpricepanel.getActionDay()  );
			String buyresult = "(" + geguprofit + ")";
			
			String action =null;
			if(geguprofit >=0) {
					action = "盈利";
			} else
					action = "亏损";
			Object[] tableData = new Object[] { addedday, action,  buyresult, autoIncKeyFromApi ,actionstockaccount,"个股盈亏"};
			DefaultTableModel tableModel = (DefaultTableModel) tblzhongdiangz.getModel();
			tableModel.insertRow(0, tableData);
		}

		
//		((AccountsInfoTableModel)tableStockAccountsInfo.getModel()).refresh(accountschicangconfig.getStockChicangAccountsList(stocknumberpricepanel.getStockcode()),formatStockCode((String)cBxstockcode.getSelectedItem()) );
//		((AccountsInfoTableModel)tableStockAccountsInfo.getModel()).fireTableDataChanged();
		
	}


//	LocalDate startday = dateChsBanKuaiZhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//	LocalDate requirestart = startday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//	dateChsBanKuaiZhanbi.setDate(Date.from(requirestart.atStartOfDay(ZoneId.systemDefault()).toInstant() ) );
	

	protected void initializeSearchDialog() 
	{
		if(searchdialog == null ) {
			try {
				searchdialog = new SearchDialog (this);
			} catch (java.lang.NullPointerException ex) {
				
			}
		} else {
			
		}

	}

	private void updatActionResultToGui(BuyStockNumberPrice stocknumberpricepanel) 
	{
		if(sysconfig.getPrivateModeSetting()) //隐私模式不显示买卖信息
			return;
		
		int stocknumber =  stocknumberpricepanel.getJiaoyiGushu();
		double stockprice =  stocknumberpricepanel.getJiaoyiJiage();
		
		String addedday = CommonUtility.formatDateYYYY_MM_DD_HHMMSS( stocknumberpricepanel.getActionDay() );
		String action =  stocknumberpricepanel.getActionType();
		String shuoming = stocknumberpricepanel.getJiaoYiShuoMing();
		
		String actionstockaccount =  stocknumberpricepanel.getJiaoyiZhanghu();
		int databaseid = stocknumberpricepanel.getDatabaseid();
		
		Object[] tableData = new Object[] { addedday, action,  shuoming, databaseid ,actionstockaccount,""};
		DefaultTableModel tableModel = (DefaultTableModel) tblzhongdiangz.getModel();
		tableModel.insertRow(0, tableData);
		
//		if(stocknumberpricepanel.getProfit() != null) {
//			double profit =  stocknumberpricepanel.getProfit();
//			String profitresult = "(" + profit + ")";
//			int profitdatabaseid = stocknumberpricepanel.getProfitDatabaseid ();
//			
//			if(profit >=0) {
//				action = "盈利";
//			} else
//				action = "亏损";
//			
//			tableData = new Object[] { addedday, action,  profitresult, profitdatabaseid ,actionstockaccount,"个股盈亏"};
//			tableModel = (DefaultTableModel) tblzhongdiangz.getModel();
//			tableModel.insertRow(0, tableData);
//		}
	}	
	
	
	/*
 * 显示板块信息
 */
	private void displayStockSuoShuBanKuai() 
	{
//		 HashMap<String, String> suosusysbankuai = ((Stock)nodeshouldbedisplayed).getGeGuSuoShuTDXSysBanKuaiList(); //所属通达信系统板块
		 HashMap<String, String> suosusysbankuai = ((Stock)nodeshouldbedisplayed).getGeGuSuoShuTDXSysBanKuaiList();
		 Set<String> union =  suosusysbankuai.keySet();
		 Multimap<String,String> suoshudaleibank = bkcyl.checkBanKuaiSuoSuTwelveDaLei (  union ); //获得板块是否属于12个大类板块
		 
		 String htmlstring = nodeinfopane.getText();
		 org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		 System.out.println(doc.toString());
		 org.jsoup.select.Elements content = doc.select("body");
		 
		 content.append( "<html> "
		 		+ "<body>"
		 		+ " <p>所属板块:");
		 
	     for(String suoshubankcode : union ) {
	    	 Collection<String> daleilist = suoshudaleibank.get(suoshubankcode);
	    	 String displayedbkformate = "\"" + suoshubankcode + suosusysbankuai.get(suoshubankcode) + "\"";
	    	 if(daleilist.size()>0)
	    		 content.append("<a style=\"color:red\" href=\"openBanKuaiAndChanYeLianDialog\">  " + displayedbkformate + daleilist.toString()  + "</a> " );
	    		else
	    			 content.append("<a href=\"openBanKuaiAndChanYeLianDialog\"> " + displayedbkformate + "</a> ");
	     } 
	     content.append( "</p>");
	     		
	     
//	     String stockcode = formatStockCode((String)cBxstockcode.getSelectedItem());
	     ArrayList<String> gegucyl = ((Stock)nodeshouldbedisplayed).getGeGuAllChanYeLianInfo();
	     for(String cyl : gegucyl) {
	    	 content.append( " <p>个股产业链:"
	    	 		+ "<a  href=\"openBanKuaiAndChanYeLianDialog\">  " + cyl)
	    	 		;
	     }
	     if(gegucyl.size()>0)
	    	 content.append( "</p>");
	     
	     content.append( "</body>"
					+ "</html>");
	    
	     htmlstring = doc.toString();
	     nodeinfopane.setText(htmlstring);
	     nodeinfopane.setCaretPosition(0);
	     
	}
	
	private void displayStockNews()
	{
		String stockcode = formatStockCode((String)cBxstockcode.getSelectedItem());
		nodeinfopane.displayChanYeLianNewsHtml (nodeshouldbedisplayed);
//		ArrayList<ChanYeLianNews> curnewlist = bkdbopt.getBanKuaiRelatedNews (stockcode);
//   	 	String htmlstring = null;
//	     if(curnewlist.size()>0)
//	    	  htmlstring = "<html> "
//	 		 		+ "<body>"
//	 		 		+ " <p>个股新闻:";
//	     for(ChanYeLianNews cylnew : curnewlist ) {
//	    		String title = cylnew.getNewsTitle();
//	    		String newdate = CommonUtility.formatDateYYYY_MM_DD( cylnew.getGenerateDate()    ).substring(0,11); 
//	    		String slackurl = cylnew.getNewsSlackUrl();
//	    		String keywords = cylnew.getKeyWords ();
//	    		if(slackurl != null && !slackurl.isEmpty() )	    		
//	    			htmlstring  += "<p>" + newdate + "<a href=\" " +   slackurl + "\"> " + title + "</a></p> ";
//	    		else
//	    			htmlstring  += "<p>" + newdate  + title + "</p> ";
//	    		//notesPane.setText("<a href=\"http://www.google.com/finance?q=NYSE:C\">C</a>, <a href=\"http://www.google.com/finance?q=NASDAQ:MSFT\">MSFT</a>");
//	    	}
//	     
//	     if(curnewlist.size()>0)
//	    	 htmlstring += "</body>"
// 						+ "</html>";
//	     
//	     nodeinfopane.setText(htmlstring);
//	     nodeinfopane.setCaretPosition(0);
	}


	
	private void setKuaiSuGui(String stockcode) 
	{
		kspanel.resetInput();
		kspanel.setStockcode(stockcode);
	}

	private void displayAccountTableToGui ()
	{
//		ArrayList<AccountInfoBasic> chicangaccountslist = null;
		String tmpstockcode = nodeshouldbedisplayed.getMyOwnCode();
		
		if(accountschicangconfig.isSystemChiCang(tmpstockcode)) { //如果是系统持仓才要显示持仓信息，否则什么都不用做
			nodeshouldbedisplayed = accountschicangconfig.setStockChiCangAccount((Stock)nodeshouldbedisplayed);
			HashMap<String, AccountInfoBasic> accountsnamemap = ((Stock)nodeshouldbedisplayed).getChiCangAccounts();
//			chicangaccountslist = new ArrayList<AccountInfoBasic> ();
//			for(String tmpstockacntname: accountsnamelist ){
//				
//				AccountInfoBasic tmpacnt  = accountschicangconfig.getAccount(tmpstockacntname);
//				
//				chicangaccountslist.add(tmpacnt);
//			}
			
			ArrayList<AccountInfoBasic> accountsnamelist = new ArrayList<AccountInfoBasic>(accountsnamemap.values());
			((AccountsInfoTableModel)tableStockAccountsInfo.getModel()).refresh(accountsnamelist,tmpstockcode );
		} else
			((AccountsInfoTableModel)tableStockAccountsInfo.getModel()).removeAllRows();
		
	}


	/*
	 * Display checklist Items to Gui
	 */
	private void displayChecklistsItemsToGui() 
	{
		//ASingleStockSubInfoCheckListsTreeInfo checklistinfo = new ASingleStockSubInfoCheckListsTreeInfo ( (String)cBxstockcode.getSelectedItem() ); 
//		checklisttree.setChecklistsitems(stockchklst.getChecklistsitems());
//		checklisttree.setStockcode(stockchklst.getStockcode());
//		checklisttree.epdTree(true);
//		checklisttree.updateUI();
		try {
//			if(buychklstdialog.isVisible() ) {
//				 System.out.println("dialog v");
//				 buychklstdialog.refreshTreeGui(stockchklst.getStockcode(), stockchklst.getChecklistsitems());
//			}
			 buychklstdialog.refreshTreeGui(nodeshouldbedisplayed.getMyOwnCode(), ((Stock)nodeshouldbedisplayed).getChecklistXml());
			
		}catch (java.lang.NullPointerException e) {
			e.printStackTrace();
		}
		
	}

	protected void setCkLstTreeInfoToDatabase() 
	{
		if(buychklstdialog == null)
			return;
    
	    String tmpchecklist = buychklstdialog.getChkLstItemsTiCai() 
	    					+ buychklstdialog.getChkLstItemsGuDong()
	    					+ buychklstdialog.getChkLstItemsCaiWu()
	    					+ buychklstdialog.getChkLstItemsJiShu()
	    					+ buychklstdialog.getChkLstItemsZhenChe()
	    					;
	    String tmpupdatedate = buychklstdialog.getChkLstUpdatedDate();
	    if(tmpchecklist != null) {
	    	((Stock)nodeshouldbedisplayed).setChecklistXml(tmpupdatedate + tmpchecklist);
	    	bkdbopt.updateChecklistsitemsToDb((Stock)nodeshouldbedisplayed);
	    }
	    	
	    buychklstdialog.refreshTreeGui(cBxstockcode.getSelectedItem().toString(),tmpupdatedate + tmpchecklist);
	}
	
	private void initializeNetWorkOperation(String stockname) 
	{
		// TODO Auto-generated method stub
		//networkoperation = new NetworkOperation(stockname); 
		
	}
	
	
	private boolean checkCodeInputFormat(String stockcode) 
	{
		// TODO Auto-generated method stub
//		System.out.println(Pattern.matches("\\d{6}$","2223") );
//		System.out.println(Pattern.matches("^\\d{6}","600138zhong") );
//		System.out.println(Pattern.matches("\\d{6}$","000123") );
//		System.out.println(Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]+$","000123abccc") );
//		System.out.println(Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]+$","000123") );
//		System.out.println(Pattern.matches("^\\d{6}{6,100}$","000123中青旅理论") );
//		System.out.println(Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]$","ccccc000123abccc") );
		
		//或者是6位全数字，或者是前面6位是数字
			if( Pattern.matches("\\d{6}$",stockcode)  || Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]+$",stockcode) )
				return true;
			else return false;
	}
	
	
	private void displayStockJibenmianInfotoGui() 
	{

		try	{   
				try {
					txtFldStockName.setText(nodeshouldbedisplayed.getMyOwnName().trim());
				} catch (java.lang.NullPointerException e) {
					txtFldStockName.setText("");
				}
				
				
//				spinnerChicanggushu.setValue(asinglestockinfomation.getChicanggushu());
//				NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();  
//				tfldchicangchenbentxt.setText(currencyFormat.format(asinglestockinfomation.getChicangchengben()));
				
			
				try {
					txtareagainiants.setText(nodeshouldbedisplayed.getGainiantishi().trim());
				} catch (java.lang.NullPointerException e) {
					txtareagainiants.setText("");
				}
				
				try {
					dateChsgainian.setDate( Date.from(nodeshouldbedisplayed.getGainiantishidate().atStartOfDay(ZoneId.systemDefault()).toInstant()));    
				} catch(java.lang.NullPointerException e) {
					dateChsgainian.setDate(null);
				}
				
				txtfldquanshangpj.setText(nodeshouldbedisplayed.getQuanshangpingji());
				try {
					dateChsquanshang.setDate(Date.from(nodeshouldbedisplayed.getQuanshangpingjidate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
				} catch(java.lang.NullPointerException e) {
					dateChsquanshang.setDate(null);
				}
				
				txtareafumianxx.setText(nodeshouldbedisplayed.getFumianxiaoxi());
				try {
					dateChsefumian.setDate( Date.from(nodeshouldbedisplayed.getFumianxiaoxidate().atStartOfDay(ZoneId.systemDefault()).toInstant()) );
				} catch(java.lang.NullPointerException e) {
					dateChsefumian.setDate(null);
				}
				
				
				txtfldzhengxg.setText(nodeshouldbedisplayed.getZhengxiangguan());
				txtfldfuxg.setText(nodeshouldbedisplayed.getFuxiangguan());
				tfdJingZhengDuiShou.setText(nodeshouldbedisplayed.getJingZhengDuiShou());
				tfdCustom.setText(nodeshouldbedisplayed.getKeHuCustom());
			
			lblStatusBarOperationIndicatior.setText("");
			lblStatusBarOperationIndicatior.setText("相关信息查找成功");
			
		}catch(Exception e)
		{
			//JOptionPane.showMessageDialog(null,"displayInfotoGui 不可预知的错误");
			e.printStackTrace();
		}
		 
		
	}
	

	private void clearGuiDispalyedInfo() 
	{
		txtFldStockName.setText("");
	    
	    dateChsgainian.setDate(null);
	    txtareagainiants.setText("");
	    
	    dateChsquanshang.setDate(null);
	    txtfldquanshangpj.setText("");
	    
	    dateChsefumian.setDate(null);
	    txtareafumianxx.setText("");
		
	    txtfldzhengxg.setText("");
		txtfldfuxg.setText("");
		tfdCustom.setText("");
		tfdJingZhengDuiShou.setText("");
		
		nodeinfopane.setText("");
	
		((DefaultTableModel)tblzhongdiangz.getModel()).setRowCount(0);
		//tableStockAccountsInfo.setModel( new DefaultTableModel()  );
		((AccountsInfoTableModel)(tableStockAccountsInfo.getModel())).deleteAllRows();
				
//		editorPaneBanKuai.setText("");
//		panelZhanBi.resetDate();
//		pnlGeGuWkZhanBi.resetDate();
//		panelbkcje.resetDate();
//		panelgegucje.resetDate();
//		
//		dateChsBanKuaiZhanbi.setDate(new Date() );
//		btnResetDate.setEnabled(false);
		
//		//xml tree 应该也要刷新
	}

	private void enableGuiEditable () 
	{
		
			dateChsgainian.setEnabled(true);
			txtareagainiants.setEnabled(true);
			dateChsquanshang.setEnabled(true);
			txtfldquanshangpj.setEnabled(true);
			dateChsefumian.setEnabled(true);
			txtareafumianxx.setEnabled(true);
			txtfldzhengxg.setEnabled(true);
			txtfldfuxg.setEnabled(true);
			
			btnCaiwufengxi.setEnabled(true);
			btnDongcaiyanbao.setEnabled(true);
			btnRongzirongquan.setEnabled(true);
			btnXueqiu.setEnabled(true);
			btnhudongyi.setEnabled(true);
			btnSlack.setEnabled(true);
			btnEnableChklsts.setEnabled(true);
			btnXueQiu.setEnabled(true);
			btnSearch.setEnabled(true);
			tblzhongdiangz.setEnabled(true);
			btnMai.setEnabled(true);
			btnSell.setEnabled(true);
			
			btnjiaruzdgz.setEnabled(true);
			btnyichuzdgz.setEnabled(true);
			btnSongZhuanGu.setEnabled(true);
			tfdCustom.setEnabled(true);
			tfdJingZhengDuiShou.setEnabled(true);
			
			btnRemvZdy.setEnabled(true);
			btndetailfx.setEnabled(true);
			
			if(nodeshouldbedisplayed.getType() == 4) {
				btnMai.setEnabled(false);
				btnSell.setEnabled(false);
				btnSongZhuanGu.setEnabled(false);
				btnyichuzdgz.setEnabled(false);
			}
		
	}
	
	
	/*
	 * 把GUI上的更改更新到数据库
	 */
	private void setGuiInfoToDatabase()
	{

//		String stockcode = "'" + formatStockCode((String)cBxstockcode.getSelectedItem()) + "'";
//		if(stockcode.equals("''") )	{
//			JOptionPane.showMessageDialog(null,"股票代码不可以为空！");
//			return;
//		}
	    
		nodeshouldbedisplayed.setMyOwnName(txtFldStockName.getText().trim().replaceAll("\\s*", "")); //去除股票名中间所有的空格
		if(dateChsgainian.getDate() != null)
			nodeshouldbedisplayed.setGainiantishidate(dateChsgainian.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		nodeshouldbedisplayed.setGainiantishi(txtareagainiants.getText().trim());
		if(dateChsquanshang.getDate() != null)
			nodeshouldbedisplayed.setQuanshangpingjidate(dateChsquanshang.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		nodeshouldbedisplayed.setQuanshangpingji(txtfldquanshangpj.getText().trim());
		if(dateChsefumian.getDate() != null)
		nodeshouldbedisplayed.setFumianxiaoxidate(dateChsefumian.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		nodeshouldbedisplayed.setFumianxiaoxi(txtareafumianxx.getText().trim());
		nodeshouldbedisplayed.setZhengxiangguan( txtfldzhengxg.getText().trim());
		nodeshouldbedisplayed.setFuxiangguan(txtfldfuxg.getText().trim());
		nodeshouldbedisplayed.setKeHuCustom(tfdCustom.getText().trim());
		nodeshouldbedisplayed.setJingZhengDuiShou(tfdJingZhengDuiShou.getText().trim());
	    
	    if( bkdbopt.updateStockNewInfoToDb(nodeshouldbedisplayed) ) {
	    	lblStatusBarOperationIndicatior.setText("");
			lblStatusBarOperationIndicatior.setText("股票信息更新成功！");
			btngengxinxx.setEnabled(false); 
   	 	} else {
	    	JOptionPane.showMessageDialog(null,"数据库更新失败");
			return;
	    };
		
	}
	
/*
 * 	
 */
//	private void updateStockCombox() 
//	{
//		boolean isaddItem=true;
//		String tmp= formatStockCode((String)cBxstockcode.getSelectedItem());//有可能是原来输入过的，要把代码选择出来。
//		
//
//	   	  //判断用户所输入的项目是否有重复，若有重复则不增加到JComboBox中。
//	   	  try{
//	   			  for(int i=0;i< cBxstockcode.getItemCount();i++) {
//		   	  	  	  if (cBxstockcode.getItemAt(i).toString().substring(0, 6).equals(tmp)){
//		   	  	  	  	 isaddItem=false;
//		   	  	  	  	 break;
//		   	  	  	  }
//		   	  	  }
//	   	  	  
//	   	  	  
//	   	  	  if (isaddItem){
//	  			  tmp = nodeshouldbedisplayed.getMyOwnCode().trim() + nodeshouldbedisplayed.getMyOwnName().trim();
//
//	   	  		  cBxstockcode.insertItemAt(tmp,0);//插入项目tmp到0索引位置(第一列中).
//	   	  	  }
//	   	  }catch(NumberFormatException ne){
//	   		
//	   	  }
//		
//	}
	
	
	private static void addPopup(Component component, final JPopupMenu popup) 
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
	
	private void displaySellBuyZdgzInfoToGui() 
	{
				Object[][] sellbuyObjects = (nodeshouldbedisplayed).getZdgzMrmcZdgzYingKuiRecords();
				for(int i=0;i<sellbuyObjects.length;i++) {
					((DefaultTableModel)tblzhongdiangz.getModel()).addRow(sellbuyObjects[i]);
				}

	}

	

	
	
	private String formatStockCode (String stockcode)
	{
		return stockcode.substring(0,6).trim(); 
	}
	
	private void setTableNewInfoToDB(Object[] tableData) 
	{
		//把修改的信息更新到数据，需要对买卖操作的3个表以及重点关注一共4个表进行可能的更新
		
//		//System.out.println("popup");
//		int rowIndex = tblzhongdiangz.getSelectedRow();
//		
//		Object dabataseidstr =  ((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 3);
//		Integer dabataseid = Integer.parseInt(dabataseidstr.toString() );
//		String actionstockaccount = (String) ((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 4);
//		AccountInfoBasic tmpaccount = accountschicangconfig.getAccount(actionstockaccount); //从几个账户list里面找到当前操作的账户
//		String shuoming = (String) ((DefaultTableModel)tblzhongdiangz.getModel()).getValueAt(rowIndex, 3);
//		
//			
//		 acntdbopt.setRecordNewInfoToDb(dabataseid.intValue(),shuoming);
		

	}
	

	private void updateSearchDialog ()
	{
//		try {
//			if(searchdialog.isVisible())
//				searchdialog.updateSearchGui ();
//		} catch (java.lang.NullPointerException ex) {
//			ex.printStackTrace();
//		}
		
	}

	private JFrame frame;
	private JTextField txtfldquanshangpj;
	private JTextField txtfldzhengxg;
	private JTextField txtfldfuxg;
	private JButton btnSearchCode;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JButton btngengxinxx;
	private JTextArea txtareagainiants;
	private JTextArea txtareafumianxx;
	private JDateChooser dateChsefumian;
	private JDateChooser dateChsquanshang;
	private JDateChooser dateChsgainian;
	private JMenuItem menuItemTongdaxinbb;
	private JMenuBar menuBar;
	private JLabel lblStatusBarOperationIndicatior;
	private JTextField txtFldStockName;
	private JButton btnhudongyi;
	private JButton btnXueqiu;
	private JButton btnRongzirongquan;
	private JButton btnDongcaiyanbao;
	private JButton btnCaiwufengxi;
	private JStockComboBox cBxstockcode;
	private JButton btnSlack;
	private JMenuItem mntmopenlcldbfile;
	private JTable tblzhongdiangz;
	private CheckBoxTree checklisttree = null;
	private JButton btnMai;
	private JButton btnSell;
	private JButton btnjiaruzdgz;
	private JButton btnyichuzdgz;
	private JScrollPane sclpaneJtable;
	private DisplayBkGgInfoEditorPane  nodeinfopane;
	private JPopupMenu popupMenu;
	private JMenuItem mntmChengjiao;
	private JButton btnSongZhuanGu;
	private JPanel panel_1;
	private JPanel panel_2;
	private JPanel panel_3;
	private JMenuItem menuItem;
	private JTable tableStockAccountsInfo;
	private JScrollPane scrollPane;
	private JTextField tfdCustom;
	private JTextField tfdJingZhengDuiShou;
	private JMenuItem mntmNewMenuItem;
	private JTabbedPane tabbedPane;
	private BuyStockNumberPrice kspanel;
	private JButton btnEnableChklsts;
	private JButton btnXueQiu;
	private JButton btnSearch;
	private JMenuItem mntmNewMenuItem_1;
	private PaoMaDeng2 pnl_paomd;
	private JButton btnDBStatus;
	private JMenuItem menuItemChexiao;
	private JMenuItem menuItemGenggai;
	private JButton btnRemvZdy;
	private JMenuItem menuItemRfshBk;
	private JMenuItem menuItemChanYeLian;
	private JMenuItem menuItemSysSet;
	private JMenuItem menuItemimportrecords;
	private JMenuItem mntmOpenRmtDb;
	private BkChanYeLianTree tree_1;
	private JButton btndetailfx;
	private JMenuItem menuItembkfx;
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeGui() 
	{
		frame = new JFrame();

		frame.getContentPane().setForeground(Color.RED);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(StockInfoManager.class.getResource("/images/Adobe_Stock_Photos_256px_1124397_easyicon.net.png")));
		frame.getContentPane().setEnabled(false);
				
		frame.setTitle("\u80A1\u7968\u4FE1\u606F\u7BA1\u7406");
		frame.setBounds(100, 100, 845, 921);
//		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		JPanel panelStatusBar = new JPanel();
		
		sclpaneJtable = new JScrollPane();
		
		tblzhongdiangz = new JTable(){
			    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
			 
		        Component comp = super.prepareRenderer(renderer, row, col);
		        Object value = getModel().getValueAt(row, col);
		        
		        
		        
		        try{
		        	if(value == null)
		        		return null;
		            if (value.toString().trim().contains("买入") && !value.toString().trim().contains("挂单") && 1==col) { 
		                comp.setForeground(Color.red);
		            } else if ( value.toString().trim().contains("卖出") && !value.toString().trim().contains("挂单") && 1==col) {
		                comp.setForeground(Color.green);
		            } else if( value.toString().trim().contains("挂单") && 1==col ) {
		            	comp.setBackground(Color.yellow);
		            } else if ("加入关注".equals(value.toString().trim()) && 1==col ) {
		                comp.setForeground(Color.blue);
		            } else if ( "盈利".equals(value.toString().trim()) && 1==col ) {
		            	comp.setBackground(Color.red);
		            } else if ( "亏损".equals(value.toString().trim()) && 1==col ) {
		            	comp.setBackground(Color.green);
		            } else if ("明日计划".equals(value.toString().trim()) && 1==col ) {
		            	comp.setBackground(Color.ORANGE);
		            }else {
		                comp.setBackground(Color.white);
		                comp.setForeground(Color.BLACK);
		            }
		        } catch (java.lang.NullPointerException e ){
		        	comp.setBackground(Color.white);
	                comp.setForeground(Color.BLACK);
		        }
		        
		        return comp;
		    }
			    
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
		String[] jtableTitleStrings = { "日期", "操作", "说明","ID","操作账户","信息表" };
		tblzhongdiangz.setEnabled(false);
		tblzhongdiangz.setFillsViewportHeight(true);
		tblzhongdiangz.setColumnSelectionAllowed(true);
		tblzhongdiangz.setCellSelectionEnabled(true);
		tblzhongdiangz.setModel(new DefaultTableModel(
			new Object[][] {
			},
			jtableTitleStrings) {
				
				private static final long serialVersionUID = 1L;
				public boolean isCellEditable(int row,int column) {
					if(2 == column) return true;
					else return false;
				}
//				public Class getColumnClass(int column) {  
//			        Class returnValue;  
//			        if ((column >= 0) && (column < getColumnCount())) {  
//			            returnValue = getValueAt(0, column).getClass();  
//			        } else {  
//			            returnValue = Object.class;  
//			        }  
//			        return returnValue;  
//			    }  
		});

		tblzhongdiangz.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(100);
		tblzhongdiangz.getTableHeader().getColumnModel().getColumn(0).setMinWidth(100);
		tblzhongdiangz.getTableHeader().getColumnModel().getColumn(0).setWidth(100);
		tblzhongdiangz.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(100);
		
		tblzhongdiangz.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(60);
		tblzhongdiangz.getTableHeader().getColumnModel().getColumn(1).setMinWidth(60);
		tblzhongdiangz.getTableHeader().getColumnModel().getColumn(1).setWidth(60);
		tblzhongdiangz.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(60);		
		
	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(0);
	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(3).setMinWidth(0);
	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(3).setWidth(0);
	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(3).setPreferredWidth(0);
	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(80);
	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(4).setMinWidth(80);
	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(4).setWidth(80);
	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(4).setPreferredWidth(80);
	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(0);
	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(5).setMinWidth(0);
	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(5).setWidth(0);
	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(5).setPreferredWidth(0);
//	    RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tblzhongdiangz.getModel());  
//	    tblzhongdiangz.setRowSorter(sorter);
//		 DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {
//			              public Component getTableCellRendererComponent(JTable table, Object value,
//			                      boolean isSelected, boolean hasFocus, int row, int column) {
//			            	  
//			                  JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//			                  Object value = this.getModel().getValueAt(row, column);
//			                  if(cell.getText().equals("买入") )
//			                	  cell.setForeground(Color.RED);
//			                  return cell;
//			              }
//			          };
//	    tblzhongdiangz.setDefaultRenderer(Object.class, tcr);
		sclpaneJtable.setViewportView(tblzhongdiangz);
		


		
		popupMenu = new JPopupMenu();
		addPopup(tblzhongdiangz, popupMenu);
		
		mntmChengjiao = new JMenuItem("\u6302\u5355\u6210\u4EA4");
		popupMenu.add(mntmChengjiao);
		
		menuItemChexiao = new JMenuItem("\u64A4\u9500\u6302\u5355");
		menuItemChexiao.setEnabled(false);
		popupMenu.add(menuItemChexiao);
		
		menuItemGenggai = new JMenuItem("\u66F4\u6539\u8D26\u6237");
		popupMenu.add(menuItemGenggai);
		panelStatusBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		
		lblStatusBarOperationIndicatior = new JLabel("hlo");
		
		panel_1 = new JPanel();
		panel_1.setBorder(null);
		
		panel_2 = new JPanel();
		panel_2.setBorder(null);
		
		panel_3 = new JPanel();
		panel_3.setBorder(null);
		
		scrollPane = new JScrollPane();
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		//JPanel pnl_paomd = new JPanel();
		pnl_paomd = new PaoMaDeng2();

		pnl_paomd.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		btnDBStatus = new JButton("");
		
		
		btnRemvZdy = new JButton("");
		btnRemvZdy.setEnabled(false);
		btnRemvZdy.setToolTipText("\u79FB\u9664\u81EA\u5B9A\u4E49\u677F\u5757");
		btnRemvZdy.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/minus_red20.png")));
		
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(panel_2, 0, 0, Short.MAX_VALUE)
								.addComponent(scrollPane, 0, 0, Short.MAX_VALUE)
								.addComponent(sclpaneJtable, 0, 0, Short.MAX_VALUE)
								.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 368, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(1093)
							.addComponent(btnRemvZdy, GroupLayout.PREFERRED_SIZE, 0, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(pnl_paomd, GroupLayout.PREFERRED_SIZE, 474, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(panelStatusBar, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDBStatus))
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 762, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(13, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(btnRemvZdy, Alignment.TRAILING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(6)
									.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(sclpaneJtable, GroupLayout.PREFERRED_SIZE, 332, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 228, GroupLayout.PREFERRED_SIZE))
						.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 745, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnDBStatus, GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(pnl_paomd, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
							.addComponent(panelStatusBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
					.addContainerGap())
		);
		panelStatusBar.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panelStatusBar.add(lblStatusBarOperationIndicatior);
		GroupLayout gl_pnl_paomd = new GroupLayout(pnl_paomd);
		gl_pnl_paomd.setHorizontalGroup(
			gl_pnl_paomd.createParallelGroup(Alignment.LEADING)
				.addGap(0, 416, Short.MAX_VALUE)
		);
		gl_pnl_paomd.setVerticalGroup(
			gl_pnl_paomd.createParallelGroup(Alignment.LEADING)
				.addGap(0, 30, Short.MAX_VALUE)
		);
		pnl_paomd.setLayout(gl_pnl_paomd);
		
		JLabel lblNewLabel_5 = new JLabel("\u8D70\u9A6C\u706F");
		
		
		//kspanel = new BuyStockNumberPrice(null,accountschicangconfig,true);
		
		
//		tableStockAccountsInfo = new JTable();
//		String[] jtableTitleStrings2 = { "账户名称", "信用操作","持仓成本", "持仓股数","持仓均价" };
//		tableStockAccountsInfo.setModel(new DefaultTableModel(
//				new Object[][] {
//				},
//				jtableTitleStrings2) {
//					
//					private static final long serialVersionUID = 1L;
//					public boolean isCellEditable(int row,int column) {
//						if(2 == column) return true;
//						else return false;
//					}
////					public Class getColumnClass(int column) {  
////				        Class returnValue;  
////				        if ((column >= 0) && (column < getColumnCount())) {  
////				            returnValue = getValueAt(0, column).getClass();  
////				        } else {  
////				            returnValue = Object.class;  
////				        }  
////				        return returnValue;  
////				    }  
//			});
//		scrollPane.setViewportView(tableStockAccountsInfo);
		
//		ArrayList<AccountInfoBasic> rongquanaccountslist = accountsconfig.getRongquanaccountslist();
//		AccountsInfoTableModel stockaccount = new AccountsInfoTableModel(rongquanaccountslist,(String)cBxstockcode.getSelectedItem());
//		tableStockAccountsInfo = new  JTable(stockaccount)
//		{
//			private static final long serialVersionUID = 1L;
//			public String getToolTipText(MouseEvent e) 
//			{
//                String tip = null;
//                java.awt.Point p = e.getPoint();
//                int rowIndex = rowAtPoint(p);
//                int colIndex = columnAtPoint(p);
//
//                try {
//                    tip = getValueAt(rowIndex, colIndex).toString();
//                } catch (RuntimeException e1) {
//                    //catch null pointer exception if mouse is over an empty line
//                }
//                return tip;
//            } 
//		};
//		scrollPane.setViewportView(tableStockAccountsInfo);
	
		
		JLabel lblstockinfo = new JLabel("\u6982\u5FF5\u63D0\u793A");
		dateChsgainian = new JDateChooser();
		dateChsgainian.setEnabled(false);
		
		JScrollPane scrollPanegainian = new JScrollPane();
		
		
		txtareagainiants = new JTextArea();
		scrollPanegainian.setViewportView(txtareagainiants);
		
		
		txtareagainiants.setFont(new Font("Monospaced", Font.PLAIN, 16));
		txtareagainiants.setEnabled(false);		
		txtareagainiants.getDocument();
		
		txtareagainiants.setLineWrap(true);
		
		JLabel lblfumianxiaoxi = new JLabel("\u8D1F\u9762\u6D88\u606F");
		dateChsefumian = new JDateChooser();
		dateChsefumian.setEnabled(false);
		
		JScrollPane scrollPanefumian = new JScrollPane();
		
		txtareafumianxx = new JTextArea();
		txtareafumianxx.setLineWrap(true);
		
		txtareafumianxx.setFont(new Font("Monospaced", Font.PLAIN, 16));
		
		txtareafumianxx.setEnabled(false);
		scrollPanefumian.setViewportView(txtareafumianxx);
		
		JLabel lblquanshangpj = new JLabel("\u5238\u5546\u8BC4\u7EA7\u63D0\u793A");
		
		dateChsquanshang = new JDateChooser();
		dateChsquanshang.setEnabled(false);
		
		txtfldquanshangpj = new JTextField();
		
		txtfldquanshangpj.setFont(new Font("宋体", Font.PLAIN, 16));
		
		txtfldquanshangpj.setEnabled(false);
		txtfldquanshangpj.setColumns(10);
		
		JLabel lblzhengxg = new JLabel("\u6B63\u76F8\u5173");
		
		txtfldzhengxg = new JTextField();
		
		txtfldzhengxg.setFont(new Font("宋体", Font.PLAIN, 16));
		
		txtfldzhengxg.setEnabled(false);
		txtfldzhengxg.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("\u8D1F\u76F8\u5173");
		
		txtfldfuxg = new JTextField();
		
		txtfldfuxg.setFont(new Font("宋体", Font.PLAIN, 16));
		txtfldfuxg.setEnabled(false);
		txtfldfuxg.setColumns(10);
		
		JLabel label = new JLabel("\u5BA2\u6237");
		
		tfdCustom = new JTextField();
		
		tfdCustom.setEnabled(false);
		tfdCustom.setColumns(10);
		
		JLabel label_1 = new JLabel("\u7ADE\u4E89\u5BF9\u624B");
		
		tfdJingZhengDuiShou = new JTextField();
		tfdJingZhengDuiShou.setEnabled(false);
		tfdJingZhengDuiShou.setColumns(10);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		nodeinfopane = new DisplayBkGgInfoEditorPane ();
		nodeinfopane.setPreferredSize(new Dimension(200,30));
		//txaBanKuai.setEditorKit(new WrapEditorKit());
		//txaBanKuai.setLineWrap(true);
		nodeinfopane.setEditable(false);
		nodeinfopane.setContentType("text/html");
		scrollPane_1.setViewportView(nodeinfopane);
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addComponent(lblstockinfo)
							.addGap(11)
							.addComponent(dateChsgainian, GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(gl_panel_3.createSequentialGroup()
							.addComponent(lblfumianxiaoxi)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(dateChsefumian, GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(gl_panel_3.createSequentialGroup()
							.addComponent(lblquanshangpj)
							.addGap(15)
							.addComponent(dateChsquanshang, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
							.addGap(22))
						.addGroup(gl_panel_3.createSequentialGroup()
							.addGap(3)
							.addComponent(lblzhengxg, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtfldzhengxg, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(gl_panel_3.createSequentialGroup()
							.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
								.addComponent(label_1)
								.addGroup(gl_panel_3.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
									.addGap(10)
									.addComponent(txtfldfuxg, GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)))
							.addContainerGap())))
				.addGroup(gl_panel_3.createSequentialGroup()
					.addComponent(txtfldquanshangpj, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_panel_3.createSequentialGroup()
					.addContainerGap()
					.addComponent(label)
					.addGap(30)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addComponent(tfdJingZhengDuiShou, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
						.addComponent(tfdCustom, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))
					.addContainerGap())
				.addGroup(gl_panel_3.createSequentialGroup()
					.addComponent(scrollPanefumian, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_panel_3.createSequentialGroup()
					.addComponent(scrollPanegainian, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_panel_3.createSequentialGroup()
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel_3.setVerticalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addComponent(lblstockinfo)
						.addComponent(dateChsgainian, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPanegainian, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addGap(10)
							.addComponent(lblfumianxiaoxi))
						.addGroup(gl_panel_3.createSequentialGroup()
							.addGap(4)
							.addComponent(dateChsefumian, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPanefumian, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addGap(10)
							.addComponent(lblquanshangpj))
						.addGroup(gl_panel_3.createSequentialGroup()
							.addGap(4)
							.addComponent(dateChsquanshang, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtfldquanshangpj, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblzhengxg)
						.addComponent(txtfldzhengxg, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblNewLabel)
						.addComponent(txtfldfuxg, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.TRAILING)
						.addComponent(label)
						.addComponent(tfdCustom, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addGap(18)
							.addComponent(label_1))
						.addGroup(gl_panel_3.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfdJingZhengDuiShou, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE))
		);
		panel_3.setLayout(gl_panel_3);
		
		cBxstockcode = new JStockComboBox();

//		for(String stockcode:accountschicangconfig.getStockChiCang())
		//HashMap<String, ASingleStockInfo> tmpchicanginfo = accountschicangconfig.getStockChiCangdetailmap ();
		
//		cBxstockcode.setForeground(Color.RED);
//		
//		cBxstockcode.setEditable(true);
		
		btnSearchCode = new JButton("\u67E5\u627E");
		
		btngengxinxx = new JButton("\u4FDD\u5B58");
		btngengxinxx.setForeground(Color.RED);
		btngengxinxx.setEnabled(false);
		
		
		txtFldStockName = new JTextField();
		
		txtFldStockName.setHorizontalAlignment(SwingConstants.CENTER);
		
		txtFldStockName.setFont(new Font("宋体", Font.BOLD, 14));
		txtFldStockName.setColumns(10);
		
		btnXueqiu = new JButton("");
		btnXueqiu.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		
		btnXueqiu.setEnabled(false);
		btnXueqiu.setToolTipText("\u96EA\u7403");
		btnXueqiu.setIcon(new ImageIcon("img/Snow_flake_24px_1094255_easyicon.net.png"));
		
		btnCaiwufengxi = new JButton("");
		
		btnCaiwufengxi.setEnabled(false);
		btnCaiwufengxi.setToolTipText("\u8D22\u52A1\u5206\u6790");
		btnCaiwufengxi.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/analysis_23.570881226054px_1202840_easyicon.net.png")));
		
		btnDongcaiyanbao = new JButton("");
		
		btnDongcaiyanbao.setEnabled(false);
		btnDongcaiyanbao.setToolTipText("\u4E1C\u8D22\u7814\u62A5");
		btnDongcaiyanbao.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/gnome_documents_24px_1173881_easyicon.net.png")));
		
		btnRongzirongquan = new JButton("");
		
		btnRongzirongquan.setEnabled(false);
		btnRongzirongquan.setToolTipText("\u878D\u8D44\u878D\u5238");
		btnRongzirongquan.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/htc_android_stock_stockwidget_24px_1135365_easyicon.net.png")));
		btnhudongyi = new JButton("");
		btnhudongyi.setEnabled(false);
		
		btnhudongyi.setToolTipText("\u4E92\u52A8\u6613");
		btnhudongyi.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/Question_23.970037453184px_1183370_easyicon.net.png")));
		
		btnSlack = new JButton("");
		
		btnSlack.setToolTipText("Slack");
		btnSlack.setEnabled(false);
		btnSlack.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/social_round_slack_24px_1196575_easyicon.net.png")));
		
		btnEnableChklsts = new JButton("");
		
		btnEnableChklsts.setToolTipText("CheckLists");
		btnEnableChklsts.setEnabled(false);
		btnEnableChklsts.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/edit_23.851162790698px_1200630_easyicon.net.png")));
		
		btnXueQiu = new JButton("");
		btnXueQiu.setToolTipText("\u96EA\u7403");
		btnXueQiu.setEnabled(false);
		btnXueQiu.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/Snow_flake_24px_1094255_easyicon.net.png")));
		
		btnSearch = new JButton("");
		btnSearch.setToolTipText("\u4E2A\u80A1\u65B0\u95FB");
		
		btnSearch.setEnabled(false);
		btnSearch.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/search_results_30.918276374443px_1194419_easyicon.net.png")));
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING, false)
						.addComponent(txtFldStockName, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
						.addComponent(cBxstockcode, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(btnSearchCode, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btngengxinxx, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnEnableChklsts, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCaiwufengxi, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnRongzirongquan, 0, 0, Short.MAX_VALUE)
							.addGap(104))
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(btnhudongyi, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(btnDongcaiyanbao, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSlack, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnXueQiu, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
							.addGap(100))))
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGap(383)
					.addComponent(btnXueqiu)
					.addContainerGap(61, Short.MAX_VALUE))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(btnhudongyi, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(cBxstockcode, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 31, Short.MAX_VALUE)
							.addComponent(btnSearchCode, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
						.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(btnXueQiu, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnSlack, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnDongcaiyanbao, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnCaiwufengxi, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
							.addComponent(txtFldStockName, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addComponent(btngengxinxx, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(btnSearch)
						.addComponent(btnEnableChklsts, 0, 0, Short.MAX_VALUE)
						.addComponent(btnRongzirongquan))
					.addGap(54))
				.addGroup(gl_panel_2.createSequentialGroup()
					.addComponent(btnXueqiu, GroupLayout.PREFERRED_SIZE, 0, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		panel_2.setLayout(gl_panel_2);
		
		btnMai = new JButton("\u4E70/\u4FDD\u5B58");
		
		btnMai.setEnabled(false);
		btnMai.setForeground(Color.RED);
		
		btnSell = new JButton("\u5356");
		
		btnSell.setEnabled(false);
		btnSell.setForeground(Color.GREEN);
		
		JButton btnzengjiadingzeng = new JButton("\u589E\u52A0\u5B9A\u589E");
		btnzengjiadingzeng.setEnabled(false);
		
		btnjiaruzdgz = new JButton("\u5173\u6CE8/\u8BA1\u5212");
		btnjiaruzdgz.setEnabled(false);
		
		btnyichuzdgz = new JButton("\u79FB\u51FA");
		
		btnyichuzdgz.setEnabled(false);
		
		btnSongZhuanGu = new JButton("\u9001\u8F6C\u80A1");
		btnSongZhuanGu.setToolTipText("\u9001\u914D\u80A1\u8BBE\u7F6E");
		btnSongZhuanGu.setEnabled(false);
		btnSongZhuanGu.setIcon(null);
		
		JButton btnNewButton_2 = new JButton("\u73B0\u5238\u8FD8\u5238");
		btnNewButton_2.setEnabled(false);
		
		JButton btnGudongzjc = new JButton("\u80A1\u4E1C\u589E\u51CF\u6301");
		btnGudongzjc.setEnabled(false);
		
		panel_1.setLayout(new MigLayout("", "[45px][45px][87px][57px][69px][81px][81px][93px][105px]", "[26px]"));
		panel_1.add(btnMai, "cell 0 0,alignx left,aligny center");
		panel_1.add(btnSell, "cell 1 0,alignx left,aligny center");
		panel_1.add(btnjiaruzdgz, "cell 2 0,alignx left,growy");
		panel_1.add(btnyichuzdgz, "cell 3 0,alignx left,growy");
		panel_1.add(btnSongZhuanGu, "cell 4 0,alignx left,aligny center");
		panel_1.add(btnNewButton_2, "cell 5 0,alignx left,aligny center");
		panel_1.add(btnzengjiadingzeng, "cell 6 0,alignx left,aligny center");
		panel_1.add(btnGudongzjc, "cell 7 0,alignx left,aligny center");
		
		btndetailfx = new JButton("\u677F\u5757\u8BE6\u7EC6\u5206\u6790");
		panel_1.add(btndetailfx, "cell 8 0");
		btndetailfx.setEnabled(false);
		frame.getContentPane().setLayout(groupLayout);
		
		menuBar = new JMenuBar();
		menuBar.setBorderPainted(false);
		frame.setJMenuBar(menuBar);
		
				
		
		JMenu menuOperationList = new JMenu("\u64CD\u4F5C");
		buttonGroup.add(menuOperationList);
		menuBar.add(menuOperationList);
		
		menuItemTongdaxinbb = new JMenuItem("\u751F\u6210\u901A\u8FBE\u4FE1\u62A5\u8868");
		menuOperationList.add(menuItemTongdaxinbb);
		
		menuItemRfshBk = new JMenuItem("\u540C\u6B65\u901A\u8FBE\u4FE1\u6570\u636E");
		
		menuOperationList.add(menuItemRfshBk);
		
		menuItemChanYeLian = new JMenuItem("\u91CD\u70B9\u5173\u6CE8\u4E0E\u4EA7\u4E1A\u94FE");
		
		menuOperationList.add(menuItemChanYeLian);
		
		menuItembkfx = new JMenuItem("\u677F\u5757\u6210\u4EA4\u91CF\u5206\u6790");
		
		menuOperationList.add(menuItembkfx);
		
		menuItemimportrecords = new JMenuItem("\u5BFC\u5165\u4EA4\u6613\u8BB0\u5F55");
		
		menuOperationList.add(menuItemimportrecords);
		
		mntmopenlcldbfile = new JMenuItem("\u6253\u5F00\u57FA\u672C\u6570\u636E\u6570\u636E\u5E93");
		menuOperationList.add(mntmopenlcldbfile);
		
		mntmOpenRmtDb = new JMenuItem("\u6253\u5F00\u901A\u8FBE\u4FE1\u540C\u6B65\u6570\u636E\u6570\u636E\u5E93");
		
		menuOperationList.add(mntmOpenRmtDb);
		
		mntmNewMenuItem_1 = new JMenuItem("\u67E5\u8BE2");
		
		menuOperationList.add(mntmNewMenuItem_1);
		
		JMenu menuConfigration = new JMenu("\u76F8\u5173\u8BBE\u7F6E");
		menuBar.add(menuConfigration);
		
		menuItem = new JMenuItem("\u8D26\u6237\u4FE1\u606F");
		
		menuConfigration.add(menuItem);
		
		menuItemSysSet = new JMenuItem("\u7CFB\u7EDF\u8BBE\u7F6E");
		
		
		menuConfigration.add(menuItemSysSet);
		
		mntmNewMenuItem = new JMenuItem("V17.12.15.13.02");
		menuConfigration.add(mntmNewMenuItem);
		
		AccountsInfoTableModel stockaccountmodel = new AccountsInfoTableModel();
		tableStockAccountsInfo = new  JTable(stockaccountmodel)
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
		scrollPane.setViewportView(tableStockAccountsInfo);
		
		kspanel = new BuyStockNumberPrice(null,accountschicangconfig,true);
		tabbedPane.addTab("\u4E70\u5165\u5FEB\u901F\u8BB0\u5F55", null, kspanel, null);
		
		
	}
	
	private void initlizedSearchDialog ()
	{
//		searchdialog = new SearchDialog(this);
//		searchdialog.setModal(false);
	}
	
	public JComboBox<String> getcBxstockcode() 
	{
		return cBxstockcode;
	}
	
	public void setBtngengxinxx()
	{
		btngengxinxx.setEnabled(true);
	}
	
	/*
	 * 如果是双屏就显示在副屏幕上
	 */
	public static void showOnScreen( int screen, JFrame frame ) {
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] gd = ge.getScreenDevices();
	    if( screen > -1 && screen < gd.length ) {
	        frame.setLocation(gd[screen].getDefaultConfiguration().getBounds().x, frame.getY());
	    } else if( gd.length > 0 ) {
	        frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x, frame.getY());
	    } else {
	        throw new RuntimeException( "No Screens Found" );
	    }
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame.setDefaultLookAndFeelDecorated(true);
					StockInfoManager window = new StockInfoManager();
//					showOnScreen(2,window.frame);
					window.frame.setVisible(true);
			    	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public void toFront() 
	{
		this.frame.toFront();
		
	}
}


class AccountsInfoTableModel extends DefaultTableModel 
{
	private ArrayList<AccountInfoBasic> accountslist;
//	private ArrayList<String> acntnamelist;
	private String stockcode;
	String[] jtableTitleStrings = { "账户名称", "持仓成本","持仓股数","持仓均价"};
	
	AccountsInfoTableModel ()
	{
	}

	public void refresh  (ArrayList<AccountInfoBasic> arrayList,String stockcode)
	{
		this.accountslist = arrayList;
//		this.acntnamelist = new ArrayList<String>(accountslist.keySet()); 
		this.stockcode = stockcode.substring(0, 6);
		
		this.fireTableDataChanged();
	}
	 public int getRowCount() 
	 {
		 if(this.accountslist == null)
			 return 0;
		 else if(this.accountslist.isEmpty()  )
			 return 0;
		 else
			 return this.accountslist.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(accountslist.isEmpty())
	    		return null;
	    	
	    	Object value = "??";
	    	AccountInfoBasic account = accountslist.get(rowIndex);
	    	StockChiCangInfo tmpstkcc = account.getStockChiCangInfoIndexOf (stockcode);
	    	switch (columnIndex) {
            case 0:
                value = account.getAccountName();
                break;
            case 1:
            	NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(); 
            	
                value = currencyFormat.format(0-tmpstkcc.getChicangchenben()); //因为成本为负

                break;
            case 2:
            	//NumberFormat currencyFormat1 = NumberFormat.getCurrencyInstance();
                value = tmpstkcc.getChicanggushu();
                break;
            case 3:
            	NumberFormat currencyFormat2 = NumberFormat.getCurrencyInstance();
                value = currencyFormat2.format(tmpstkcc.getChicangchenben()/tmpstkcc.getChicanggushu()); 
                break;

	    	}

        return value;
	  }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = String.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = String.class;
			          break;
		        case 3:
			          clazz = String.class;
			          break;
		      }
		      
		      return clazz;
		}
	    
//	    @Override
//	    public Class<?> getColumnClass(int columnIndex) {
//	        return // Return the class that best represents the column...
//	    }
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    
	    public AccountInfoBasic getAccountsAt(int row) {
	        return accountslist.get(row);
	    }
	    
	    public void deleteAllRows()
	    {
//	    	if(this.accountslist == null)
//				 return ;
//			 else 
//				 accountslist.clear();
	    	int rowCount = this.getRowCount();
	    	//Remove rows one by one from the end of the table
//	    	for (int i = rowCount - 1; i >= 0; i--) {
	    	    
	    	
	    	this.fireTableDataChanged();
	    }
	    
	    public void removeAllRows ()
	    {
	    	if(accountslist != null) {
	    		this.accountslist.clear();
		    	this.fireTableDataChanged();
	    	}
	    	
	    }
}

