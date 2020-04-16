package com.exchangeinfomanager.gui;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JViewport;

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

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.ScrollUtil;
import com.exchangeinfomanager.commonlib.SystemAudioPlayed;
import com.exchangeinfomanager.commonlib.TableCellListener;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.exchangeinfomanager.commonlib.checkboxtree.CheckBoxTree;
import com.exchangeinfomanager.commonlib.jstockcombobox.JStockComboBox;
import com.exchangeinfomanager.AccountAndChiCang.AccountAndChiCangConfiguration;
import com.exchangeinfomanager.License.License;
import com.exchangeinfomanager.News.CreateNewsDialog;
import com.exchangeinfomanager.News.CreateNewsWithFurtherOperationDialog;
import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsLabelServices;
import com.exchangeinfomanager.News.NewsServices;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Search.SearchDialog;
import com.exchangeinfomanager.Services.ServicesForNewsLabel;
import com.exchangeinfomanager.Services.ServicesForNode;
import com.exchangeinfomanager.Services.ServicesOfPaoMaDeng;

import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisPlayNodeSuoShuBanKuaiListPanel;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisPlayNodeSuoShuBanKuaiListServices;

import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisplayNodeInfoPanel;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisplayNodeJiBenMianService;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisplayNodesRelatedNewsServices;
import com.exchangeinfomanager.TagLabel.TagsPanel;
import com.exchangeinfomanager.TagManagment.JDialogForTagSearchMatrixPanelForAddNewsToNode;
import com.exchangeinfomanager.TagManagment.JDialogForTagSearchMatrixPanelForAddSysNewsToNode;
import com.exchangeinfomanager.TagManagment.NodeLabelMatrixManagement;
import com.exchangeinfomanager.TagServices.TagCache;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.accountconfiguration.AccountOperation.AccountSeeting;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiGuanLi;
import com.exchangeinfomanager.bankuaichanyelian.JDialogOfBanKuaiChanYeLian;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsPnl2.TDXNodsInforPnl;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;
import com.exchangeinfomanager.bankuaifengxi.GeGuTDXFengXi;
import com.exchangeinfomanager.bankuaifengxi.PaoMaDengServices;
import com.exchangeinfomanager.bankuaifengxi.ai.WeeklyExportFileFengXi;
import com.exchangeinfomanager.bankuaifengxi.ai.WeeklyFenXiWizard;
import com.exchangeinfomanager.commonlib.jstockcombobox.JStockComboBoxNodeRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextArea;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.JScrollPane;
import javax.swing.JPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.ButtonGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import java.util.HashMap;
import java.util.HashSet;

import java.util.Properties;
import java.util.Set;
import java.util.Vector;
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
import com.google.common.base.Strings;

import com.exchangeinfomanager.database.*;
import com.exchangeinfomanager.gui.subgui.BuyCheckListTreeDialog;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.gui.subgui.GengGaiZhangHu;
import com.exchangeinfomanager.gui.subgui.ImportTDXData;
import com.exchangeinfomanager.gui.subgui.PaoMaDeng2;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.border.BevelBorder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;

import javax.swing.border.SoftBevelBorder;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.beans.PropertyChangeEvent;
import javax.swing.JPopupMenu;
import javax.swing.border.EtchedBorder;
import java.awt.FlowLayout;



import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import com.exchangeinfomanager.commonlib.jstockcombobox.JStockComboBoxModel;
import javax.swing.JComboBox;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;


public class StockInfoManager 
{
	/**
	 * Create the application.
	 */
	public StockInfoManager() 
	{
		License license = new License ();
		if( !license.isLicenseValide() ) {
			JOptionPane.showMessageDialog(null,"License非法！再见！");
			System.exit(0);
		}
		license = null;
			
	    
		sysconfig = SystemConfigration.getInstance();
		connectdb = ConnectDataBase.getInstance();
		boolean localconnect = connectdb.isLocalDatabaseconnected();
		if(localconnect == false) {
			JOptionPane.showMessageDialog(null,"数据库连接失败！再见！");
			System.exit(0);
		}
		
		accountschicangconfig = AccountAndChiCangConfiguration.getInstance();
		acntdbopt = new AccountDbOperation();
		bkdbopt = new BanKuaiDbOperation ();
		
		initializeGui();
		displayAccountAndChiCang ();
		displayDbInfo();
		createEvents();
    	initializePaoMaDeng ();
 	}
	
	private static Logger logger = Logger.getLogger(StockInfoManager.class);
	private ConnectDataBase connectdb = null;
	private SystemConfigration sysconfig = null;
	private AccountAndChiCangConfiguration accountschicangconfig;
	private BkChanYeLianTreeNode nodeshouldbedisplayed; 
//	private BuyCheckListTreeDialog buychklstdialog;
	private BanKuaiDbOperation bkdbopt;
	private AccountDbOperation acntdbopt;
	private BanKuaiGuanLi bkgldialog = null;
	private BanKuaiFengXi bkfx ;
	private GeGuTDXFengXi ggfx ;
	private SearchDialog searchdialog;
	private WeeklyExportFileFengXi effx;
	private TagCache bkstkkwcache;
	private JDialogOfBanKuaiChanYeLian cyldialog;
	/*
	 * 
	 */
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
		SvsForNodeOfStock svsstock = new SvsForNodeOfStock ();
		for(String tmpsgstockcodename:tmpchicangname) {
			BkChanYeLianTreeNode tmpstock = svsstock.getNode(tmpsgstockcodename.substring(0, 6) );
			( (JStockComboBoxModel)cBxstockcode.getModel() ).addElement(tmpstock);
		}
		try {
			kspanel.setStockcode(cBxstockcode.getSelectedItem().toString().substring(0, 6));
		} catch (java.lang.NullPointerException e) {
		}
		
		((JStockComboBoxNodeRenderer)cBxstockcode.getRenderer()).setChiCangGeGuList(new HashSet<String> (tmpchicangname));
	}
	private void initializePaoMaDeng() 
	{
		// TODO Auto-generated method stub
		String title = "明日计划:";
		ServicesOfPaoMaDeng svspmd = new PaoMaDengServices ();
		String paomad = svspmd.getPaoMaDengInfo();
		
		if(!paomad.isEmpty())
			pnl_paomd.refreshMessage(title+paomad);
		else pnl_paomd.refreshMessage(null);
		
		svspmd = null;
	}

		private void displayDbInfo() 
		{
			if(connectdb.isLocalDatabaseconnected()){
				btnDBStatus.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/database_23.147208121827px_1201712_easyicon.net.png")));
				lblStatusBarOperationIndicatior.setText(lblStatusBarOperationIndicatior.getText() + connectdb.getLocalDatabaseName("full")+"数据库已连接");
				btnDBStatus.setToolTipText(btnDBStatus.getToolTipText() + connectdb.getLocalDatabaseName("full")+"数据库已连接");
			}
		}
		
		
		public void preUpdateSearchResultToGui(BkChanYeLianTreeNode node)
		{
			nodeshouldbedisplayed = node;
			String stockcode = nodeshouldbedisplayed.getMyOwnCode();
			preUpdateSearchResultToGui (stockcode);
		}
		/*
		 * 
		 */
		private void preUpdateSearchResultToGui()
		{
			String stockcode = nodeshouldbedisplayed.getMyOwnCode();
			preUpdateSearchResultToGui (stockcode);
		}
		/*
		 * 
		 */
		public void preUpdateSearchResultToGui(String stockcode) 
		{
				btngengxinxx.setEnabled(false);
				clearGuiDispalyedInfo ();
				
				 displayStockJibenmianInfotoGui (); //显示基本信息
				 
				 if(!sysconfig.getPrivateModeSetting()) { //隐私模式不显示持仓信息
					 nodeshouldbedisplayed = bkdbopt.getZdgzMrmcZdgzYingKuiFromDB(nodeshouldbedisplayed);
					 displaySellBuyZdgzInfoToGui ();
				 }
				 
				 if(nodeshouldbedisplayed.getType() == 6) { //是个股
					if(accountschicangconfig.isSystemChiCang(stockcode)) 
						nodeshouldbedisplayed = accountschicangconfig.setStockChiCangAccount((Stock)nodeshouldbedisplayed);
					if(!sysconfig.getPrivateModeSetting()) { //隐私模式不显示持仓信息
						displayAccountTableToGui ();
					}

					displayStockSuoShuBanKuai ();
					setKuaiSuGui (stockcode);
				 } 
				 
				 
				 displayBanKuaiAndStockNews (); //显示板块和个股新闻
				 displayNodeTags (); // 显示关键信息
				 enableGuiEditable();
		}
		
	private void displayNodeTags() 
	{
//		int count = pnltags.getComponentCount();
//		for(int i=0;i<count;i++) {
//			Component comp = pnltags.getComponent(i);
//			String name = comp.getName();
//			Class<? extends Component> classes = comp.getClass();
//			comp.getLocation();
//		}
		Set<BkChanYeLianTreeNode> bkstk = new HashSet<> ();
		bkstk.add(this.nodeshouldbedisplayed);
		TagsServiceForNodes lbnodedbservice = new TagsServiceForNodes (bkstk);
		bkstkkwcache = new TagCache (lbnodedbservice);
		lbnodedbservice.setCache(bkstkkwcache);
		pnltags.initializeTagsPanel (lbnodedbservice,bkstkkwcache);
	}
	/*
	 * 
	 */
	private void tableMouseClickActions (MouseEvent arg0)
	{
		if( tblzhongdiangz.getRowCount() <=0 )
			return;
		
		int row = tblzhongdiangz.getSelectedRow();
		if(row <0) {
//			JOptionPane.showMessageDialog(null,"没有记录被选中！","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		int modelRow = tblzhongdiangz.convertRowIndexToModel(row);
				 
        if (arg0.getClickCount() == 1) {

        }
        if (arg0.getClickCount() == 2) {
        	String date = (String)tblzhongdiangz.getValueAt(modelRow, 0);	
        	LocalDate selectdate = null;
        	try {
        		selectdate = LocalDate.parse(date);
        	} catch( java.time.format.DateTimeParseException e) {
        		selectdate = LocalDate.parse(date.substring(0, 10).trim());
        	}
        	
        	String stockcode = cBxstockcode.getSelectedItem().toString().substring(0, 6);
        	showWeeklyFenXiWizardDialog (selectdate);
		}
	}
	/*
	 * 
	 */
	private void showWeeklyFenXiWizardDialog(LocalDate selectdate) 
	{
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		this.frame.setCursor(hourglassCursor);
		
		WeeklyFenXiWizard ggfx = new WeeklyFenXiWizard ( nodeshouldbedisplayed,selectdate);
    	ggfx.setSize(new Dimension(1550, 900));
    	ggfx.setModalityType(Dialog.ModalityType.APPLICATION_MODAL); // prevent user from doing something else
    	ggfx.setLocationRelativeTo(null);
//    	Toolkit.getDefaultToolkit().beep();
    	SystemAudioPlayed.playSound();
    	if(!ggfx.isVisible() ) 
    		ggfx.setVisible(true);
    	
    	ggfx.toFront();
    	
    	
    	ggfx = null;
    	
    	hourglassCursor = null;
    	Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
		this.frame.setCursor(hourglassCursor2);
	}
	/*
	 * 
	 */
	private void createEvents()
	{
		mntmFreemind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				try {
					String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconfig.getFreeMindInstallationPath() ;
					logger.debug(cmd);
					Process p  = Runtime.getRuntime().exec(cmd);
					p.waitFor();
				} catch (Exception e1){
					e1.printStackTrace();
				}
			}
		});
		
		tblzhongdiangz.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) 
        	{
        		tableMouseClickActions (arg0);
        	}
        });
		
		btnyituishi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int action = JOptionPane.showConfirmDialog(null, "是否将该股设置为已经退市状态？设置后将无法更改，是否继续？","警告", JOptionPane.YES_NO_OPTION);
				if(0 == action) {
					bkdbopt.setStockAsYiJingTuiShi (((BkChanYeLianTreeNode) cBxstockcode.getSelectedItem()).getMyOwnCode() );
				}
			}
		});
		menuItembkfx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				startBanKuaiFengXi ();
			}
		});
		menuItemggfx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				startGeGuFengXi ();
			}
		});
		
		
		btndetailfx.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				startBanKuaiFengXi ();
			}
		});
		
		cBxstockcode.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) 
			{
				if(e.getStateChange() == ItemEvent.SELECTED) {

					nodeshouldbedisplayed = cBxstockcode.getUserInputNode();
					if(nodeshouldbedisplayed != null)
						preUpdateSearchResultToGui(nodeshouldbedisplayed.getMyOwnCode());
					else 
						clearGuiDispalyedInfo ();
					
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
//			            logger.debug(link);
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
		menuItemimportrecords.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				AccountSeeting accountsetting= new AccountSeeting(accountschicangconfig,frame); 
				accountsetting.setVisible(true);
				
			}
			
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
				
		menuItembkconfg.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				startBanKuaiGuanLiDlg ();
				
			}
		});
		menuItemChanYeLian.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				startChanyeLianDlg ();
				
			}
		});

		
		menuItemRfshBk.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				if( this.importPreCheckTDX()) {
					ImportTDXData importtdx= new ImportTDXData();
					importtdx.setModal(true);
					importtdx.setVisible(true);
				}
			}
			
			private Boolean importPreCheckTDX()
			{
				String tdxpath = sysconfig.getTDXInstalledLocation();
				
				File file = new File(sysconfig.getTDXStockEverUsedNameFile() );
				if(!file.exists() ) {
					 System.out.println("通达信目录不正确:" + tdxpath ); 
					 JOptionPane.showMessageDialog(null,"通达信目录不正确，请重新设置!当前设置为:" + tdxpath);
					 return false;
				 } else 
					 return true;
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
//				NewsServices svsns = new NewsServices ();
//				NewsCache newcache = new NewsCache (nodeshouldbedisplayed.getMyOwnCode(),svsns,null,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
//				svsns.setCache(newcache);
//
//				JDialogForTagSearchMatrixPanelForAddNewsToNode pnlsearchtags = new JDialogForTagSearchMatrixPanelForAddNewsToNode(newcache);
//				
//                News News = new News("新闻标题",LocalDate.now() ,"描述", "", new HashSet<>(),"URL",((BkChanYeLianTreeNode) cBxstockcode.getSelectedItem()).getMyOwnCode()  );
//                
//                CreateNewsDialog cd = new CreateNewsDialog (svsns);
//                cd.setNews(News);
//                cd.setVisible(true);
                
//                JDialogForTagSearchMatrixPanelForAddSysNewsToNode newlog 
//                	= new JDialogForTagSearchMatrixPanelForAddSysNewsToNode (  (BkChanYeLianTreeNode) cBxstockcode.getSelectedItem() );
//                newlog.setModal(true);
//                newlog.setVisible(true);
                
                News News = new News("新闻标题",LocalDate.now() ,"描述", "", new HashSet<InsertedNews.Label>(),"URL",
                		((BkChanYeLianTreeNode) cBxstockcode.getSelectedItem()).getMyOwnCode()  );
                
                NewsServices svsns = new NewsServices ();
                ServicesForNewsLabel svslabel = new NewsLabelServices ();
                NewsCache newcache = new NewsCache (nodeshouldbedisplayed.getMyOwnCode(),svsns,svslabel,LocalDate.now().minusMonths(1),LocalDate.now().plusMonths(6));
				svsns.setCache(newcache);
                
                CreateNewsWithFurtherOperationDialog newdlg = 
                		new CreateNewsWithFurtherOperationDialog (svsns);
                
                newdlg.setNews(News);
                newdlg.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - newdlg.getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - newdlg.getHeight()/2);
                newdlg.setVisible(true);
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
//				initlizedBuyCheckListTreeDialog ();
			}
		});
		
		KeyListener kl = new KeyAdapter()
		{
			public void keyPressed(KeyEvent kevt) {
				 if(kevt.getKeyChar()=='c') {
				 
				  if(kevt.isControlDown()) {
					  logger.debug("ctrl c");
				  }
				 
				}
			}
			
		};
		
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
//		            logger.debug("Row   : " + tcl.getRow());
//		            logger.debug("Column: " + tcl.getColumn());
		            logger.debug("Old   : " + tcl.getOldValue());
		            logger.debug("New   : " + tcl.getNewValue());
		            
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
						logger.debug("edit");
					else
						logger.debug("unedit");
				}
			}
		});
		
		tblzhongdiangz.getModel().addTableModelListener(new TableModelListener()  
        {  
            @Override  
            public void tableChanged(TableModelEvent e)  
            {  
                 //logger.debug("action");     
                //int col = e.getColumn();  
                //int row = e.getFirstRow();  
//                System.out.print(row);
//                System.out.print(col);
//                logger.debug("/////");
  
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
						logger.debug(ex);
					}
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
				
				showWeeklyFenXiWizardDialog (LocalDate.now());
				preUpdateSearchResultToGui();
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
				String stockcode = ((BkChanYeLianTreeNode) cBxstockcode.getSelectedItem()).getMyOwnCode();
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
		 */
		mntmChengjiao.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent arg0)
			{
				//logger.debug("popup");
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
				String stockcode = ((BkChanYeLianTreeNode) cBxstockcode.getSelectedItem()).getMyOwnCode();
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
				if(kspanel.shouldOpenGeGuFenXi())
					showWeeklyFenXiWizardDialog (LocalDate.now());
				
				if(kspanel.getJiaoyiGushu()>0 && kspanel.getJiaoyiJiage()>0) {
					saveKuaiSuJiLuJiaoYi ();
					return ;
				}
				BkChanYeLianTreeNode buynode = 	(BkChanYeLianTreeNode) cBxstockcode.getSelectedItem();
				BuyStockNumberPrice stocknumberpricepanel = new BuyStockNumberPrice ( buynode.getMyOwnCode(),accountschicangconfig,true);
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
		/*
		 * 
		 */
		btnSell.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				BkChanYeLianTreeNode sellnode = (BkChanYeLianTreeNode) cBxstockcode.getSelectedItem();
				BuyStockNumberPrice stocknumberpricepanel = new BuyStockNumberPrice (  sellnode.getMyOwnCode() ,accountschicangconfig,false);
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
				bkdbopt.refreshTDXDrawLineInfo (null);
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
				NodeLabelMatrixManagement lblmag = new NodeLabelMatrixManagement (nodeshouldbedisplayed );
				lblmag.setModal(true);
				lblmag.setVisible(true);
				
				bkstkkwcache.refreshTags();
				
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
		/*
		 * 
		 */
		mntmopenlcldbfile.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
//				ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
//		        Map<String, Object> vars = new HashMap<String, Object>();
//		        vars.put("x", 3);
//		        vars.put("y", 2);
//		        vars.put("z", 1);
//		        try {
//					System.out.println("result = "+engine.eval("(x > y && y > z) && z>x", new SimpleBindings(vars)));
//				} catch (ScriptException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
		        
				String dbfile = null;
				switch (connectdb.getLocalDatabaseType().toLowerCase() ) {
				case "mysql":    dbfile = "D:\\tools\\HeidiSQL\\heidisql.exe";
					break;
				case "access":  dbfile = connectdb.getLocalDatabaseName("full");
					break;
				}
				try {
					String cmd = "rundll32 url.dll,FileProtocolHandler " + dbfile;
					logger.debug(cmd);
					Process p  = Runtime.getRuntime().exec(cmd);
					p.waitFor();
				} catch (Exception e1){
					e1.printStackTrace();
				}
			}
		});
		
		
		txtareafumianxx.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent event) 
			{
				if(cBxstockcode.getSelectedItem() == null)
					return;
				
				if (SwingUtilities.isRightMouseButton(event))
			    {
					Toolkit toolkit = Toolkit.getDefaultToolkit();
				     Clipboard clipboard = toolkit.getSystemClipboard();
				     
					if(dateChsefumian.getLocalDate() == null || dateChsefumian.getLocalDate().equals(LocalDate.now())) { //如果是对当天的信息进行修改
						 
					     try {
					      String result = (String) clipboard.getData(DataFlavor.stringFlavor);
					      txtareafumianxx.insert(result, txtareafumianxx.getCaretPosition());
					      txtareafumianxx.insert("  ", txtareafumianxx.getCaretPosition());
					     } catch(Exception e) {
					      logger.debug(e);
					     }
					} else { //如果是对过去的信息进行修改，要把日期加上
					     try {
					      String result = (String) clipboard.getData(DataFlavor.stringFlavor);
					      
					      String curtext = "[" + dateChsefumian.getLocalDate().toString() + txtareafumianxx.getText() + "]";
					      txtareafumianxx.setText(result + "  " +curtext); 
					     } catch(Exception e) {
					      logger.debug(e);
					     }
					}
					btngengxinxx.setEnabled(true);
				    dateChsefumian.setDate(new Date());
			    }
			}
		});
		
		txtfldquanshangpj.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent event) 
			{
				if(cBxstockcode.getSelectedItem()  == null)
					return;
				
				if (SwingUtilities.isRightMouseButton(event))
				{
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Clipboard clipboard = toolkit.getSystemClipboard();
					try	{
						String result = (String) clipboard.getData(DataFlavor.stringFlavor);
						logger.debug(result);
						txtfldquanshangpj.setText(txtfldquanshangpj.getText() + result);
						
						btngengxinxx.setEnabled(true);
						dateChsquanshang.setDate(new Date());
					}catch(Exception e)	{
						logger.debug(e);
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
				
				if(cBxstockcode.getSelectedItem() == null)
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
						logger.debug(e);
					}
					
				}
			}
		});
		
//		btnSearchCode.addMouseListener(new MouseAdapter() 
//		{
//			@Override
//			public void mouseClicked(MouseEvent arg0) 
//			{	
//				nodeshouldbedisplayed = cBxstockcode.getUserInputNode();
//				if(nodeshouldbedisplayed != null)
//					preUpdateSearchResultToGui(nodeshouldbedisplayed.getMyOwnCode());
//				else {
//					clearGuiDispalyedInfo ();
//				}
//				
//				String stockcode = null;
//				try	{
//					stockcode = formatStockCode((String)cBxstockcode.getSelectedItem());
//					preUpdateSearchResultToGui(stockcode);
//					
////					if(!checkCodeInputFormat(stockcode)) {
////						logger.debug("股票代码有误");
////						JOptionPane.showMessageDialog(null,"股票代码有误！");
////						return;
////					}else {
////						preUpdateSearchResultToGui(stockcode);
////					}
//				}catch(java.lang.NullPointerException e)	{
//					e.printStackTrace();
//					JOptionPane.showMessageDialog(frame, "请输入股票代码！","Warning", JOptionPane.WARNING_MESSAGE);
//					return;
//				}catch(java.lang.StringIndexOutOfBoundsException ex2) {
//					ex2.printStackTrace();
//					JOptionPane.showMessageDialog(null,"股票代码有误！");
//					return;
//				}
//			}
//		});

		
		btngengxinxx.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				if(!btngengxinxx.isEnabled())
					return;
				setGuiInfoToDatabase();
//				setCkLstTreeInfoToDatabase ();
				
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
				String jbmexportresult = TDXFormatedOpt.stockJiBenMianToReports(); //基本面
				String nodefenxiportresult = TDXFormatedOpt.stockAndBanKuaiFenXiReports (); //个股分析
				String xmlexportresult = TDXFormatedOpt.parseChanYeLianXmlToTDXReport(); //产业链
				String zdgzexportresult = TDXFormatedOpt.getStockZdgzInfo (); //历史关注信息
				String tagsreportresult = TDXFormatedOpt.parseBanKuaiGeGuTagsToTDXReport (); //关键词
				
				if(Strings.isNullOrEmpty(xmlexportresult) 
						&& Strings.isNullOrEmpty(jbmexportresult) 
						&& Strings.isNullOrEmpty(zdgzexportresult)
						&& Strings.isNullOrEmpty(tagsreportresult) ) {
					int exchangeresult = JOptionPane.showConfirmDialog(null, "报表生成失败，请检查原因！","报表完毕", JOptionPane.OK_CANCEL_OPTION);
					return;
				}
				
				String reportsummary = "";
				if(Strings.isNullOrEmpty(xmlexportresult)) 
					reportsummary = reportsummary + "产业链报表生成失败。";
				if(Strings.isNullOrEmpty(jbmexportresult)) 
					reportsummary = reportsummary + "基本面报表生成失败。";
				if(Strings.isNullOrEmpty(zdgzexportresult)) 
					reportsummary = reportsummary + "重点关注报表生成失败。";
				if(Strings.isNullOrEmpty(nodefenxiportresult))
					reportsummary = reportsummary + "分析结果报表生成失败。";
				if(Strings.isNullOrEmpty(tagsreportresult) ) 
					reportsummary = reportsummary + "个股板块关键词报表生成失败";
				
				int exchangeresult = JOptionPane.showConfirmDialog(null, reportsummary + "其他报表生成成功，是否打开报表目录？","报表完毕", JOptionPane.OK_CANCEL_OPTION);
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
						return;
					
				try {
						Desktop.getDesktop().open(new File(jbmexportresult));
				} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
				}
			}
		});
}
	protected void startChanyeLianDlg() 
	{
		if(cyldialog == null ) {
			cyldialog = new JDialogOfBanKuaiChanYeLian();
			cyldialog.setModal(false);

			cyldialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		} 
		
		if(!cyldialog.isVisible() ) {
			cyldialog.toFront();
			cyldialog.setVisible(true);
		 } 
		
	}
	public BanKuaiFengXi getBanKuaiFengXi ()
	{
			return bkfx;
	}
		
	protected void startGeGuFengXi() 
	{
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		this.frame.setCursor(hourglassCursor);
		
			if(ggfx == null ) {
				ggfx = new GeGuTDXFengXi (this);
				ggfx.setModal(false);
				ggfx.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			} 
			
			if(!ggfx.isVisible() ) {

				ggfx.setVisible(true);
			} 
			
			ggfx.toFront();
			
//			SystemAudioPlayed.playSound();
			
			hourglassCursor = null;
			Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
			this.frame.setCursor(hourglassCursor2);
	}
	
	protected void startBanKuaiFengXi() 
	{
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		this.frame.setCursor(hourglassCursor);
		
			if(bkfx == null ) {
				bkfx = new BanKuaiFengXi (this);
				bkfx.setModal(false);
				bkfx.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			} 
			
			if(!bkfx.isVisible() ) {

				bkfx.setVisible(true);
			} 
			
			bkfx.toFront();
			
//			SystemAudioPlayed.playSound();
			
			hourglassCursor = null;
			Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
			this.frame.setCursor(hourglassCursor2);
	}

		/*
		 * 
		 */
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
		/*
		 * 
		 */
		protected void startBanKuaiGuanLiDlg()
		{
			if(bkgldialog == null ) {
				bkgldialog = new BanKuaiGuanLi();
				bkgldialog.setModal(false);

				bkgldialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			} 
			
			if(!bkgldialog.isVisible() ) {
				bkgldialog.toFront();
				bkgldialog.setVisible(true);
			 } 

		}

	protected void refreshChiCangAccountPanel ()
	{
//		String stockcode = formatStockCode((String)cBxstockcode.getSelectedItem()); 

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

	}



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
		DisPlayNodeSuoShuBanKuaiListServices svsstkbk = new DisPlayNodeSuoShuBanKuaiListServices (nodeshouldbedisplayed);
		DisPlayNodeSuoShuBanKuaiListPanel stkbkpnl = new DisPlayNodeSuoShuBanKuaiListPanel (svsstkbk);
		stkbkpnl.setPreferredSize(new Dimension(200,30));
		
		scrlnodebankuai.setViewportView(stkbkpnl);
		
		stkbkpnl.revalidate();
		stkbkpnl.repaint();
		
		scrlnodebankuai.revalidate();
		scrlnodebankuai.repaint();
		
		stkbkpnl.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(DisPlayNodeSuoShuBanKuaiListPanel.URLSELECTED_PROPERTY)) {
            		String selbk = evt.getNewValue().toString();
            		String selbkcode;
            		if(selbk != null)
    					selbkcode = selbk.trim().substring(1, 7);
    				else
    					return;
    				
    				SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
    				BkChanYeLianTreeNode bankuai = svsbk.getNode(selbkcode);
    				svsbk = null;
    				
    				DisplayNodeJiBenMianService bkjbm = new DisplayNodeJiBenMianService (bankuai);
    				DisplayNodeInfoPanel displaybkpnl = new DisplayNodeInfoPanel (bkjbm);
    				Dimension size = new Dimension(scrlpanofInfo.getViewport().getSize().width, displaybkpnl.getContentHeight() + 10 );
    				displaybkpnl.setPreferredSize(size);
    				displaybkpnl.setMinimumSize(size);
    				displaybkpnl.setMaximumSize(size);
    				pnlextrainfo.add (displaybkpnl,0);
    				
    				DisplayNodesRelatedNewsServices bknews = new DisplayNodesRelatedNewsServices (bankuai);
    				DisplayNodeInfoPanel displaybknewspnl = new DisplayNodeInfoPanel (bknews);
    				Dimension size2 = new Dimension(scrlpanofInfo.getViewport().getSize().width, displaybknewspnl.getContentHeight() + 10 );
    				displaybknewspnl.setPreferredSize(size2);
    				displaybknewspnl.setMinimumSize(size2);
    				displaybknewspnl.setMaximumSize(size2);
    				pnlextrainfo.add (displaybknewspnl,0);
    				
    				pnlextrainfo.revalidate();
    				pnlextrainfo.repaint();
    				
    				ScrollUtil.scroll(scrlpanofInfo, ScrollUtil.BOTTOM);
    				
            	}
            }
		});
		
//		 editorpansuosubk.displayBanKuaiListContentsForStock((Stock)nodeshouldbedisplayed);
	}
	
	private void displayBanKuaiAndStockNews()
	{
		DisplayNodesRelatedNewsServices svsnews = new DisplayNodesRelatedNewsServices (this.nodeshouldbedisplayed);
		svsnews.setTimeRangeForInfoRange(LocalDate.now().minusMonths(12), LocalDate.now());
		DisplayNodeInfoPanel newspnl = new DisplayNodeInfoPanel (svsnews);
		
		pnlextrainfo.add (newspnl);

//		editorPanenodeinfo.displayJSTestHtml();
//		editorPanenodeinfo.displayChanYeLianNewsHtml (nodeshouldbedisplayed);
//		editorPanenodeinfo.displayChanYeLianInfo(nodeshouldbedisplayed);
	}


	
	private void setKuaiSuGui(String stockcode) 
	{
		kspanel.resetInput();
		kspanel.setStockcode(stockcode);
	}

	private void displayAccountTableToGui ()
	{
		String tmpstockcode = nodeshouldbedisplayed.getMyOwnCode();
		
		if(accountschicangconfig.isSystemChiCang(tmpstockcode)) { //如果是系统持仓才要显示持仓信息，否则什么都不用做
			nodeshouldbedisplayed = accountschicangconfig.setStockChiCangAccount((Stock)nodeshouldbedisplayed);
			HashMap<String, AccountInfoBasic> accountsnamemap = ((Stock)nodeshouldbedisplayed).getChiCangAccounts();
			
			ArrayList<AccountInfoBasic> accountsnamelist = new ArrayList<AccountInfoBasic>(accountsnamemap.values());
			((AccountsInfoTableModel)tableStockAccountsInfo.getModel()).refresh(accountsnamelist,tmpstockcode );
		} else
			((AccountsInfoTableModel)tableStockAccountsInfo.getModel()).deleteAllRows();
		
	}


	/*
	 * Display checklist Items to Gui
	 */
//	private void displayChecklistsItemsToGui() 
//	{
//		//ASingleStockSubInfoCheckListsTreeInfo checklistinfo = new ASingleStockSubInfoCheckListsTreeInfo ( (String)cBxstockcode.getSelectedItem() ); 
////		checklisttree.setChecklistsitems(stockchklst.getChecklistsitems());
////		checklisttree.setStockcode(stockchklst.getStockcode());
////		checklisttree.epdTree(true);
////		checklisttree.updateUI();
//		try {
////			if(buychklstdialog.isVisible() ) {
////				 logger.debug("dialog v");
////				 buychklstdialog.refreshTreeGui(stockchklst.getStockcode(), stockchklst.getChecklistsitems());
////			}
//			 buychklstdialog.refreshTreeGui(nodeshouldbedisplayed.getMyOwnCode(), ((Stock)nodeshouldbedisplayed).getChecklistXml());
//			
//		}catch (java.lang.NullPointerException e) {
//			e.printStackTrace();
//		}
//		
//	}
//
//	protected void setCkLstTreeInfoToDatabase() 
//	{
//		if(buychklstdialog == null)
//			return;
//    
//	    String tmpchecklist = buychklstdialog.getChkLstItemsTiCai() 
//	    					+ buychklstdialog.getChkLstItemsGuDong()
//	    					+ buychklstdialog.getChkLstItemsCaiWu()
//	    					+ buychklstdialog.getChkLstItemsJiShu()
//	    					+ buychklstdialog.getChkLstItemsZhenChe()
//	    					;
//	    String tmpupdatedate = buychklstdialog.getChkLstUpdatedDate();
//	    if(tmpchecklist != null) {
//	    	((Stock)nodeshouldbedisplayed).setChecklistXml(tmpupdatedate + tmpchecklist);
//	    	bkdbopt.updateChecklistsitemsToDb((Stock)nodeshouldbedisplayed);
//	    }
//	    	
//	    buychklstdialog.refreshTreeGui(cBxstockcode.getSelectedItem().toString(),tmpupdatedate + tmpchecklist);
//	}
	
	private void initializeNetWorkOperation(String stockname) 
	{
		// TODO Auto-generated method stub
		//networkoperation = new NetworkOperation(stockname); 
		
	}
	
	
	private boolean checkCodeInputFormat(String stockcode) 
	{
		// TODO Auto-generated method stub
//		logger.debug(Pattern.matches("\\d{6}$","2223") );
//		logger.debug(Pattern.matches("^\\d{6}","600138zhong") );
//		logger.debug(Pattern.matches("\\d{6}$","000123") );
//		logger.debug(Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]+$","000123abccc") );
//		logger.debug(Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]+$","000123") );
//		logger.debug(Pattern.matches("^\\d{6}{6,100}$","000123中青旅理论") );
//		logger.debug(Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]$","ccccc000123abccc") );
		
		//或者是6位全数字，或者是前面6位是数字
			if( Pattern.matches("\\d{6}$",stockcode)  || Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]+$",stockcode) )
				return true;
			else return false;
	}
	
	
	private void displayStockJibenmianInfotoGui() 
	{
		ServicesForNode svsnode;
		if(nodeshouldbedisplayed.getType() == BkChanYeLianTreeNode.TDXBK) {
			 svsnode = new SvsForNodeOfBanKuai ();
		} else
			 svsnode = new SvsForNodeOfStock ();
		svsnode.getNodeJiBenMian(nodeshouldbedisplayed);
		
		try	{   
				try {
					txtFldStockName.setText(nodeshouldbedisplayed.getMyOwnName().trim());
				} catch (java.lang.NullPointerException e) {
					txtFldStockName.setText("");
				}

				
			
				try {
					txtareagainiants.setText(nodeshouldbedisplayed.getNodeJiBenMian().getGainiantishi().trim());
				} catch (java.lang.NullPointerException e) {
					txtareagainiants.setText("");
				}
				
				try {
					dateChsgainian.setDate( Date.from(nodeshouldbedisplayed.getNodeJiBenMian().getGainiantishidate().atStartOfDay(ZoneId.systemDefault()).toInstant()));    
				} catch(java.lang.NullPointerException e) {
					dateChsgainian.setDate(null);
				}
				try {
					txtfldquanshangpj.setText(nodeshouldbedisplayed.getNodeJiBenMian().getQuanshangpingji());
				} catch(java.lang.NullPointerException e) {
					txtfldquanshangpj.setText("");
				}
				try {
					dateChsquanshang.setDate(Date.from(nodeshouldbedisplayed.getNodeJiBenMian().getQuanshangpingjidate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
				} catch(java.lang.NullPointerException e) {
					dateChsquanshang.setDate(null);
				}
				try {
					txtareafumianxx.setText(nodeshouldbedisplayed.getNodeJiBenMian().getFumianxiaoxi());
				} catch(java.lang.NullPointerException e) {
					txtareafumianxx.setText("");
				}
				try {
					dateChsefumian.setDate( Date.from(nodeshouldbedisplayed.getNodeJiBenMian().getFumianxiaoxidate().atStartOfDay(ZoneId.systemDefault()).toInstant()) );
				} catch(java.lang.NullPointerException e) {
					dateChsefumian.setDate(null);
				}
//				try {
//					txtfldzhengxg.setText(nodeshouldbedisplayed.getNodeJiBenMian().getZhengxiangguan());
//				} catch(java.lang.NullPointerException e) {
//					txtfldzhengxg.setText("");
//				}
//				try {
//					txtfldfuxg.setText(nodeshouldbedisplayed.getNodeJiBenMian().getFuxiangguan());
//				} catch(java.lang.NullPointerException e) {
//					txtfldfuxg.setText("");
//				}
//				try {
//					tfdJingZhengDuiShou.setText(nodeshouldbedisplayed.getNodeJiBenMian().getJingZhengDuiShou());
//				} catch(java.lang.NullPointerException e) {
//					tfdJingZhengDuiShou.setText("");
//				}
//				try {
//				tfdCustom.setText(nodeshouldbedisplayed.getNodeJiBenMian().getKeHuCustom());
//				} catch(java.lang.NullPointerException e) {
//					tfdCustom.setText("");
//				}
			
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
		
//	    txtfldzhengxg.setText("");
//		txtfldfuxg.setText("");
//		tfdCustom.setText("");
//		tfdJingZhengDuiShou.setText("");
		
//		editorpansuosubk.setText("");
//		editorPanenodeinfo.setText("");
	
		((DefaultTableModel)tblzhongdiangz.getModel()).setRowCount(0);
		//tableStockAccountsInfo.setModel( new DefaultTableModel()  );
		((AccountsInfoTableModel)(tableStockAccountsInfo.getModel())).deleteAllRows();
		
		
		kspanel.resetInput ();
		
		Component view = scrlnodebankuai.getViewport().getView();
		if(view != null)
			scrlnodebankuai.getViewport().remove(view);
		scrlnodebankuai.getViewport().revalidate();
		scrlnodebankuai.getViewport().repaint();
			
		pnltags.removeAll();
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
//			txtfldzhengxg.setEnabled(true);
//			txtfldfuxg.setEnabled(true);
			
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
			btnSongZhuanGu.setEnabled(true);
//			tfdCustom.setEnabled(true);
//			tfdJingZhengDuiShou.setEnabled(true);
			
			btnRemvZdy.setEnabled(true);
			btndetailfx.setEnabled(true);
			
			if(nodeshouldbedisplayed.getType() == 4) {
				btnMai.setEnabled(false);
				btnSell.setEnabled(false);
				btnSongZhuanGu.setEnabled(false);
			}
			
			if(nodeshouldbedisplayed.getType() == BkChanYeLianTreeNode.TDXBK)
				cobxgpc.setEnabled(true);
		
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
			nodeshouldbedisplayed.getNodeJiBenMian().setGainiantishidate(dateChsgainian.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		nodeshouldbedisplayed.getNodeJiBenMian().setGainiantishi(txtareagainiants.getText().trim());
		if(dateChsquanshang.getDate() != null)
			nodeshouldbedisplayed.getNodeJiBenMian().setQuanshangpingjidate(dateChsquanshang.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		nodeshouldbedisplayed.getNodeJiBenMian().setQuanshangpingji(txtfldquanshangpj.getText().trim());
		if(dateChsefumian.getDate() != null)
		nodeshouldbedisplayed.getNodeJiBenMian().setFumianxiaoxidate(dateChsefumian.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		nodeshouldbedisplayed.getNodeJiBenMian().setFumianxiaoxi(txtareafumianxx.getText().trim());
//		nodeshouldbedisplayed.getNodeJiBenMian().setZhengxiangguan( txtfldzhengxg.getText().trim());
//		nodeshouldbedisplayed.getNodeJiBenMian().setFuxiangguan(txtfldfuxg.getText().trim());
//		nodeshouldbedisplayed.getNodeJiBenMian().setKeHuCustom(tfdCustom.getText().trim());
//		nodeshouldbedisplayed.getNodeJiBenMian().setJingZhengDuiShou(tfdJingZhengDuiShou.getText().trim());
	    
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
	
	private void displaySellBuyZdgzInfoToGui() 
	{
				Object[][] sellbuyObjects = (nodeshouldbedisplayed).getNodeJiBenMian().getZdgzMrmcZdgzYingKuiRecords();
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
		
//		//logger.debug("popup");
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
	private JButton btnSearchCode;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JButton btngengxinxx;
	private JTextArea txtareagainiants;
	private JTextArea txtareafumianxx;
	private JLocalDateChooser dateChsefumian;
	private JLocalDateChooser dateChsquanshang;
	private JLocalDateChooser dateChsgainian;
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
	private JScrollPane sclpaneJtable;
//	private DisPlayNodeSuoShuBanKuaiListPanel  editorpansuosubk;
	private JPopupMenu popupMenu;
	private JMenuItem mntmChengjiao;
	private JButton btnSongZhuanGu;
	private JPanel panel_1;
	private JPanel panel_2;
	private JPanel panelinfo;
	private JMenuItem menuItem;
	private JTable tableStockAccountsInfo;
	private JScrollPane scrollPane;
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
//	private BkChanYeLianTree tree_1;
	private JButton btndetailfx;
	private JMenuItem menuItembkfx;
	private JButton btnyituishi;
	private JComboBox cobxgpc;
	private TagsPanel pnltags;
	private JMenuItem menuItembkconfg;
	private JScrollPane scrlpanofInfo;
	private JPanel pnlextrainfo;
	private JScrollPane scrlnodebankuai;
	private JMenu mnThirdparty;
	private JMenuItem mntmFreemind;
	private JMenuItem menuItemggfx;
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeGui() 
	{
		frame = new JFrame();
		
		frame.getContentPane().setForeground(Color.RED);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(StockInfoManager.class.getResource("/images/Adobe_Stock_Photos_256px_1124397_easyicon.net.png")));
		frame.getContentPane().setEnabled(false);
				
		frame.setTitle("StockMining");
		frame.setBounds(100, 100, 866, 921);
//		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		JPanel panelStatusBar = new JPanel();
		
		scrlnodebankuai = new JScrollPane();
		

		
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
		
		panelinfo = new JPanel();
		panelinfo.setBorder(null);
		
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
							.addComponent(pnl_paomd, GroupLayout.PREFERRED_SIZE, 474, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(panelStatusBar, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDBStatus))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(scrollPane, 0, 0, Short.MAX_VALUE)
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
											.addComponent(panel_2, 0, 0, Short.MAX_VALUE)
											.addComponent(sclpaneJtable, 0, 0, Short.MAX_VALUE)
											.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 368, Short.MAX_VALUE)))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(panelinfo, GroupLayout.PREFERRED_SIZE, 458, GroupLayout.PREFERRED_SIZE)))
							.addGap(1067)
							.addComponent(btnRemvZdy, GroupLayout.PREFERRED_SIZE, 0, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(btnRemvZdy)
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(sclpaneJtable, GroupLayout.PREFERRED_SIZE, 332, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 228, GroupLayout.PREFERRED_SIZE))
						.addComponent(panelinfo, GroupLayout.PREFERRED_SIZE, 755, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnDBStatus, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
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
		
		JLabel lblstockinfo = new JLabel("\u6982\u5FF5\u63D0\u793A");
		dateChsgainian = new JLocalDateChooser();
		dateChsgainian.setEnabled(false);
		
		JScrollPane scrollPanegainian = new JScrollPane();
		
		
		txtareagainiants = new JTextArea();
		scrollPanegainian.setViewportView(txtareagainiants);
		
		
		txtareagainiants.setFont(new Font("Monospaced", Font.PLAIN, 16));
		txtareagainiants.setEnabled(false);		
		txtareagainiants.getDocument();
		
		txtareagainiants.setLineWrap(true);
		
		JLabel lblfumianxiaoxi = new JLabel("\u8D1F\u9762\u6D88\u606F");
		dateChsefumian = new JLocalDateChooser();
		dateChsefumian.setEnabled(false);
		
		JScrollPane scrollPanefumian = new JScrollPane();
		
		txtareafumianxx = new JTextArea();
		txtareafumianxx.setLineWrap(true);
		
		txtareafumianxx.setFont(new Font("Monospaced", Font.PLAIN, 16));
		
		txtareafumianxx.setEnabled(false);
		scrollPanefumian.setViewportView(txtareafumianxx);
		
		JLabel lblquanshangpj = new JLabel("\u5238\u5546\u8BC4\u7EA7\u63D0\u793A");
		
		dateChsquanshang = new JLocalDateChooser();
		dateChsquanshang.setEnabled(false);
		
		txtfldquanshangpj = new JTextField();
		
		txtfldquanshangpj.setFont(new Font("宋体", Font.PLAIN, 16));
		
		txtfldquanshangpj.setEnabled(false);
		txtfldquanshangpj.setColumns(10);
		
		
		
		scrlpanofInfo = new JScrollPane();
		
		JLabel label_2 = new JLabel("\u540C\u6B65\u597D\u53CB\u677F\u5757");
		
		Vector v = new Vector();
		v.add(new JCheckBox("概念提示",true));
		v.add(new JCheckBox("负面消息",true));
		v.add(new JCheckBox("券商评级",true));
		v.add(new JCheckBox("正相关",true));
		v.add(new JCheckBox("负相关",true));
		v.add(new JCheckBox("客户",false));
		v.add(new JCheckBox("竞争对手",false));
//		cobxgpc = new JComboCheckBox(v);
		cobxgpc = new JComboBox (v);
		cobxgpc.setEnabled(false);
		cobxgpc.setFont(new Font("宋体", Font.PLAIN, 12));
		
		pnltags = new TagsPanel("",TagsPanel.HIDEHEADERMODE,TagsPanel.FULLCONTROLMODE);
		GroupLayout gl_panelinfo = new GroupLayout(panelinfo);
		gl_panelinfo.setHorizontalGroup(
			gl_panelinfo.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelinfo.createSequentialGroup()
					.addGroup(gl_panelinfo.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPanefumian, Alignment.TRAILING)
						.addComponent(scrlnodebankuai, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_panelinfo.createSequentialGroup()
							.addGap(4)
							.addComponent(lblfumianxiaoxi)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(dateChsefumian, GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, gl_panelinfo.createSequentialGroup()
							.addComponent(lblstockinfo)
							.addGap(14)
							.addComponent(dateChsgainian, GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))
						.addComponent(scrollPanegainian, Alignment.TRAILING)
						.addGroup(Alignment.TRAILING, gl_panelinfo.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblquanshangpj)
							.addGap(32)
							.addComponent(dateChsquanshang, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtfldquanshangpj, GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, gl_panelinfo.createSequentialGroup()
							.addComponent(label_2)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(cobxgpc, 0, 372, Short.MAX_VALUE))
						.addComponent(scrlpanofInfo, Alignment.TRAILING)
						.addGroup(gl_panelinfo.createSequentialGroup()
							.addContainerGap()
							.addComponent(pnltags, GroupLayout.PREFERRED_SIZE, 437, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_panelinfo.setVerticalGroup(
			gl_panelinfo.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelinfo.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_panelinfo.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_2)
						.addComponent(cobxgpc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelinfo.createParallelGroup(Alignment.TRAILING)
						.addComponent(dateChsgainian, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblstockinfo))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPanegainian, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(gl_panelinfo.createParallelGroup(Alignment.TRAILING)
						.addComponent(dateChsefumian, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblfumianxiaoxi))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPanefumian, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelinfo.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblquanshangpj)
						.addComponent(dateChsquanshang, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtfldquanshangpj, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(4)
					.addComponent(pnltags, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrlnodebankuai, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrlpanofInfo, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		pnlextrainfo = new JPanel();
		scrlpanofInfo.setViewportView(pnlextrainfo);
		pnlextrainfo.setLayout(new BoxLayout(pnlextrainfo, BoxLayout.Y_AXIS));
		panelinfo.setLayout(gl_panelinfo);
		
		cBxstockcode = new JStockComboBox();

	
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
		
		btnhudongyi.setToolTipText("\u5173\u952E\u8BCD\u7BA1\u7406");
		btnhudongyi.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/tags (1).png")));
		
		btnSlack = new JButton("");
		
		btnSlack.setToolTipText("Slack");
		btnSlack.setEnabled(false);
		btnSlack.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/social_round_slack_24px_1196575_easyicon.net.png")));
		
		btnEnableChklsts = new JButton("");
		
		btnEnableChklsts.setToolTipText("\u67E5\u627E");
		btnEnableChklsts.setEnabled(false);
		btnEnableChklsts.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/if_magnifyingglass_1055031.png")));
		
		btnXueQiu = new JButton("");
		btnXueQiu.setToolTipText("\u96EA\u7403");
		btnXueQiu.setEnabled(false);
		btnXueQiu.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/Snow_flake_24px_1094255_easyicon.net.png")));
		
		btnSearch = new JButton("");
		btnSearch.setToolTipText("\u4E2A\u80A1\u65B0\u95FB");
		
		btnSearch.setEnabled(false);
		btnSearch.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/if_icon-70_667363 (4).png")));
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
		
		btnjiaruzdgz = new JButton("\u5173\u6CE8/\u5206\u6790/\u8BA1\u5212");
		btnjiaruzdgz.setEnabled(false);
		
		btnSongZhuanGu = new JButton("\u9001\u8F6C\u80A1");
		btnSongZhuanGu.setToolTipText("\u9001\u914D\u80A1\u8BBE\u7F6E");
		btnSongZhuanGu.setEnabled(false);
		btnSongZhuanGu.setIcon(null);
		
		JButton btnNewButton_2 = new JButton("\u73B0\u5238\u8FD8\u5238");
		btnNewButton_2.setEnabled(false);
		
		JButton btnGudongzjc = new JButton("\u80A1\u4E1C\u589E\u51CF\u6301");
		btnGudongzjc.setEnabled(false);
		
		panel_1.setLayout(new MigLayout("", "[45px][45px][87px][57px][69px][81px][81px][93px][105px][]", "[26px]"));
		panel_1.add(btnMai, "cell 0 0,alignx left,aligny center");
		panel_1.add(btnSell, "cell 1 0,alignx left,aligny center");
		panel_1.add(btnjiaruzdgz, "cell 2 0,alignx left,growy");
		panel_1.add(btnSongZhuanGu, "cell 4 0,alignx left,aligny center");
		panel_1.add(btnNewButton_2, "cell 5 0,alignx left,aligny center");
		panel_1.add(btnzengjiadingzeng, "cell 6 0,alignx left,aligny center");
		panel_1.add(btnGudongzjc, "cell 7 0,alignx left,aligny center");
		
		btndetailfx = new JButton("\u677F\u5757\u8BE6\u7EC6\u5206\u6790");
		panel_1.add(btndetailfx, "cell 8 0");
		btndetailfx.setEnabled(false);
		
		btnyituishi = new JButton("");
		btnyituishi.setToolTipText("\u8BBE\u7F6E\u4E3A\u5DF2\u9000\u5E02");
		try {
			btnyituishi.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/cloud-computing.png")));
		} catch (java.lang.NullPointerException ex) {
			
		}
		
		panel_1.add(btnyituishi, "cell 9 0");
		frame.getContentPane().setLayout(groupLayout);
		
		menuBar = new JMenuBar();
		menuBar.setBorderPainted(false);
		frame.setJMenuBar(menuBar);
		
				
		
		JMenu menuOperationList = new JMenu("\u64CD\u4F5C");
		buttonGroup.add(menuOperationList);
		menuBar.add(menuOperationList);
		
		menuItemRfshBk = new JMenuItem("\u540C\u6B65\u901A\u8FBE\u4FE1\u6570\u636E");
		menuItemRfshBk.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/synchronization.png")));
		
		menuOperationList.add(menuItemRfshBk);
		
		menuItembkfx = new JMenuItem("\u677F\u5757\u5206\u6790");
		menuItembkfx.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/analysis.png")));
		
		menuOperationList.add(menuItembkfx);
		
		menuItemggfx = new JMenuItem("个股分析");
		menuItemggfx.setEnabled(false);
		menuItemggfx.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/analysis.png")));
		
		menuOperationList.add(menuItemggfx);
		
		menuItemChanYeLian = new JMenuItem("\u4EA7\u4E1A\u94FE\u8BBE\u7F6E");
		menuItemChanYeLian.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/focus.png")));
		
		menuOperationList.add(menuItemChanYeLian);
		
		menuItemTongdaxinbb = new JMenuItem("\u751F\u6210\u901A\u8FBE\u4FE1\u62A5\u8868");
		menuItemTongdaxinbb.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/if_reports_49615 (1).png")));
		menuOperationList.add(menuItemTongdaxinbb);
		
		menuItembkconfg = new JMenuItem("板块设置");
		menuItembkconfg.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/focus.png")));
		
		menuOperationList.add(menuItembkconfg);
		
		menuItemimportrecords = new JMenuItem("\u5BFC\u5165\u4EA4\u6613\u8BB0\u5F55");
		menuItemimportrecords.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/import.png")));
		
		menuOperationList.add(menuItemimportrecords);
		
		mntmNewMenuItem_1 = new JMenuItem("\u67E5\u8BE2");
		mntmNewMenuItem_1.setEnabled(false);
		mntmNewMenuItem_1.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/search_results_30.918276374443px_1194419_easyicon.net.png")));
		
		menuOperationList.add(mntmNewMenuItem_1);
		
		mnThirdparty = new JMenu("ThirdParty");
		menuBar.add(mnThirdparty);
		
		mntmFreemind = new JMenuItem("FreeMind");
		mntmFreemind.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/open24.png")));
		mnThirdparty.add(mntmFreemind);
		
		mntmopenlcldbfile = new JMenuItem("\u6570\u636E\u5E93");
		mnThirdparty.add(mntmopenlcldbfile);
		mntmopenlcldbfile.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/open24.png")));
		
		JMenu menuConfigration = new JMenu("\u76F8\u5173\u8BBE\u7F6E");
		menuBar.add(menuConfigration);
		
		menuItem = new JMenuItem("\u8D26\u6237\u4FE1\u606F");
		
		menuConfigration.add(menuItem);
		
		menuItemSysSet = new JMenuItem("\u7CFB\u7EDF\u8BBE\u7F6E");
		
		
		menuConfigration.add(menuItemSysSet);
		
		mntmNewMenuItem = new JMenuItem("V19.11.03.12.02");
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
	
	public JStockComboBox getcBxstockcode() 
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
//	public static void showOnScreen( int screen, JFrame frame ) {
//	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//	    GraphicsDevice[] gd = ge.getScreenDevices();
//	    if( screen > -1 && screen < gd.length ) {
//	        frame.setLocation(gd[screen].getDefaultConfiguration().getBounds().x, frame.getY());
//	    } else if( gd.length > 0 ) {
//	        frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x, frame.getY());
//	    } else {
//	        throw new RuntimeException( "No Screens Found" );
//	    }
//	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Properties properties = System.getProperties();
				    String rootPath = properties.getProperty("user.dir").replace('\\', '/');
					System.setProperty("logfile.name",rootPath);
					
					JFrame.setDefaultLookAndFeelDecorated(true);
					StockInfoManager window = new StockInfoManager();
//					showOnScreen(2,window.frame);
					
					window.frame.setVisible(true);
//					Toolkit.getDefaultToolkit().beep();
					SystemAudioPlayed.playSound();
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
            	try {
            		value = currencyFormat.format(0 - tmpstkcc.getChicangchenben()); //因为成本为负
            	} catch (java.lang.NullPointerException e) {
        			e.printStackTrace();
        		}

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
	    	if(accountslist != null) {
	    		this.accountslist.clear();
		    	this.fireTableDataChanged();
	    	}
	    }
	    

}

