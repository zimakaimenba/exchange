package com.exchangeinfomanager.gui.subgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.gui.StockInfoManager;

import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Splitter;



import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;



public class BuyCheckListTreeDialog extends JDialog 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BuyCheckListTreeDialog(StockInfoManager sotckinfomanager,String stockcode,String checklistslist) 
	{
		setTitle("\u4E70\u5356CheckLists");
	
		initializeSysConfig();
//		initializeDb();
		
		initialzeGui();
		
		this.stockcode = stockcode;
		this.sotckinfomanager = sotckinfomanager;
		this.checklistslist = checklistslist;
		

		initlizedTreeGuiTiCai(checklistslist);
		initlizedTreeGuiGuDong (checklistslist);
		initlizedTreeGuiZhenChe (checklistslist);
		initlizedTreeGuiCaiWu (checklistslist);
		initlizedTreeGuiJiShu (checklistslist);
		
		
		if(stockcode != null) //如果是NULL，说明还没有checklist值，只需要显示空dialog
			refreshTreeGui ( stockcode, checklistslist);//复用。用于把checklist显示在GUI

		createEvents();
	}
	


	//private AccountConfiguration accountsconfig;
	private ConnectDataBase connectdb = null;
	private SystemConfigration sysconfig = null;
	//private NetworkOperation networkoperation;
	private String stockcode;
	private StockInfoManager sotckinfomanager;
	private CheckBoxTree checklistTreeTiCai;
	private CheckBoxTree checklistTreeGuDong;
	private CheckBoxTree checklistTreeCaiWu;
	private CheckBoxTree checklistTreeJiShu; 
	private CheckBoxTree checklistTreeZhenChe;
	private LocalDate lastestUpdateDate;
	




	
	private void setStockCodeToGui(String stockcode2) 
	{
		lblStockCode.setText(stockcode2);
		
	}

	public void refreshTreeGui (String stockcode,String checklistslist)
	{
		this.stockcode = stockcode;
		this.checklistslist = checklistslist; 
		
		setStockCodeToGui(stockcode);
		setLastestUpdatedDateToGui(checklistslist);
		initialzeTreeItemTiCai(checklistslist);
		initialzeTreeItemGuDong (checklistslist);
		initialzeTreeItemZhenChe (checklistslist);
		initialzeTreeItemCaiWu (checklistslist);
		initialzeTreeItemJiShu (checklistslist);
		
		tabbedPane.setForegroundAt(0, Color.black);
		tabbedPane.setForegroundAt(1, Color.black);
		tabbedPane.setForegroundAt(2, Color.black);
		tabbedPane.setForegroundAt(3, Color.black);
		tabbedPane.setForegroundAt(4, Color.black);
	}
	
	/*
	 * Display checklist Items to Gui
	 */
//	private void displayChecklistsItemsToGui() 
//	{
//		//ASingleStockSubInfoCheckListsTreeInfo checklistinfo = new ASingleStockSubInfoCheckListsTreeInfo ( (String)cBxstockcode.getSelectedItem() ); 
//		checklisttree.setChecklistsitems(stockchklst.getChecklistsitems());
//		checklisttree.setStockcode(stockchklst.getStockcode());
//		checklisttree.epdTree(true);
//		checklisttree.updateUI();
//	}
	


	private void createEvents() 
	{
		btnOpenXML.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				int tmpindex = tabbedPane.getSelectedIndex();
				
				switch (tmpindex) {
	            case 0:
	            	try {
						String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconfig.getBuyingChecklistsXMLFile ("ticai");
						Process p  = Runtime.getRuntime().exec(cmd);
						p.waitFor();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
					    e1.printStackTrace();
					}
	                break;
	            case 1:
	            	try {
						String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconfig.getBuyingChecklistsXMLFile ("gudong");
						Process p  = Runtime.getRuntime().exec(cmd);
						p.waitFor();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
					    e1.printStackTrace();
					}
	                break;
	            case 2:
	            	try {
						String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconfig.getBuyingChecklistsXMLFile ("caiwu");
						Process p  = Runtime.getRuntime().exec(cmd);
						p.waitFor();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
					    e1.printStackTrace();
					}
	                break;
	            case 3:
	            	try {
						String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconfig.getBuyingChecklistsXMLFile ("jishu");
						Process p  = Runtime.getRuntime().exec(cmd);
						p.waitFor();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
					    e1.printStackTrace();
					}
	                break;
	            case 4:
	            	try {
						String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconfig.getBuyingChecklistsXMLFile ("zhenche");
						Process p  = Runtime.getRuntime().exec(cmd);
						p.waitFor();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
					    e1.printStackTrace();
					}
	                break; 
				}
			}
		});
		
		mntmxmlZhenChe.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) 
			{
				try {
					String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconfig.getBuyingChecklistsXMLFile ("zhenche");
					Process p  = Runtime.getRuntime().exec(cmd);
					p.waitFor();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
				    e1.printStackTrace();
				}
			}
		});
		
		mntmxmlJiShu.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) 
			{
				try {
					String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconfig.getBuyingChecklistsXMLFile ("jishu");
					Process p  = Runtime.getRuntime().exec(cmd);
					p.waitFor();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
				    e1.printStackTrace();
				}
			}
		});
		
		mntmxmlCaiWu.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) 
			{
				try {
					String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconfig.getBuyingChecklistsXMLFile ("caiwu");
					Process p  = Runtime.getRuntime().exec(cmd);
					p.waitFor();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
				    e1.printStackTrace();
				}
			}
		});
		
		mntmxmlGuDong.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) 
			{
				try {
					String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconfig.getBuyingChecklistsXMLFile ("gudong");
					Process p  = Runtime.getRuntime().exec(cmd);
					p.waitFor();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
				    e1.printStackTrace();
				}
			}
		});
		
		mntmxmlTiCai.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				try {
					String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconfig.getBuyingChecklistsXMLFile ("ticai");
					Process p  = Runtime.getRuntime().exec(cmd);
					p.waitFor();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
				    e1.printStackTrace();
				}
			}
		});
		
		// TODO Auto-generated method stub
		btnRefreshXML.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				int tmpindex = tabbedPane.getSelectedIndex();
				
				switch (tmpindex) {
	            case 0:
	            	CheckBoxTreeNode root0 =  (CheckBoxTreeNode)checklistTreeTiCai.getModel().getRoot();
	            	root0.removeAllChildren();
	            	initlizedTreeGuiTiCai (checklistslist);
	            	initialzeTreeItemTiCai (checklistslist);
	                break;
	            case 1:
	            	CheckBoxTreeNode root1 =  (CheckBoxTreeNode)checklistTreeGuDong.getModel().getRoot();
	            	root1.removeAllChildren();
	            	initlizedTreeGuiGuDong (checklistslist);
	            	initialzeTreeItemGuDong (checklistslist);
	                break;
	            case 2:
	            	CheckBoxTreeNode root2 =  (CheckBoxTreeNode)checklistTreeCaiWu.getModel().getRoot();
	            	root2.removeAllChildren();
	            	initlizedTreeGuiCaiWu (checklistslist);
	            	initialzeTreeItemCaiWu (checklistslist);
	                break;
	            case 3:
	            	CheckBoxTreeNode root3 =  (CheckBoxTreeNode)checklistTreeJiShu.getModel().getRoot();
	            	root3.removeAllChildren();
	            	initlizedTreeGuiJiShu (checklistslist);
	            	initialzeTreeItemJiShu (checklistslist);
	                break;
	            case 4:
	            	CheckBoxTreeNode root4 =  (CheckBoxTreeNode)checklistTreeZhenChe.getModel().getRoot();
	            	root4.removeAllChildren();
	            	initlizedTreeGuiZhenChe (checklistslist);
	            	initialzeTreeItemZhenChe (checklistslist);
	                break; 
				}
			}
		});
		
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				dispose();
			}
		});
		
	}


	private void initializeDb() 
	{
		// TODO Auto-generated method stub
		connectdb = ConnectDataBase.getInstance();
	}

	private void initializeSysConfig()
	{
		sysconfig = SystemConfigration.getInstance();
		//formatterhwy= sysconfig.getFormatDate();
	}

	//从checklistslist头部获得修改时间按戳
	private void setLastestUpdatedDateToGui(String checklistslist)
	{
		try {
			lastestUpdateDate = CommonUtility.formateStringToDate(checklistslist.substring(0, 19));
		} catch(java.lang.NullPointerException e) {
			lastestUpdateDate = null;
		} catch(java.lang.StringIndexOutOfBoundsException e) {
			lastestUpdateDate = null;
		} 
		
		try {
			lblLastestDate.setText(lastestUpdateDate.toString());
		} catch(java.lang.NullPointerException e) {
			lblLastestDate.setText("");
		} catch(java.lang.StringIndexOutOfBoundsException e) {
			lblLastestDate.setText("");
		}
		
	}
	//
	public String getChkLstUpdatedDate ()
	{
		if(checklistTreeTiCai.getChecklistsitems() != null) {
			 return lastestUpdateDate.toString();
		} else 
			return null;
	}

	/*
	 * 初始化，如果没有文件，就返回null
	 */
	private void initlizedTreeGuiTiCai(String checklistslist) 
	{
		String xmlfilepath = sysconfig.getBuyingChecklistsXMLFile ("ticai");
//		FileInputStream xmlfileinput = null;
//		try {
//			xmlfileinput = new FileInputStream(xmlfilepath);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		 CheckBoxTreeXmlHandler xmlhd = new CheckBoxTreeXmlHandler(xmlfilepath);
		 if(xmlhd == null)
			 return;
		 checklistTreeTiCai = new CheckBoxTree(xmlhd.getXmltreeroot(),this.stockcode);
		 scrlpnTiCai.setViewportView(checklistTreeTiCai);
		 
		 checklistTreeTiCai.addMouseListener(new MouseAdapter() {
			 	@Override
			 	public void mousePressed(MouseEvent arg0) 
			 	{
			 		if( checklistTreeTiCai.getRowForLocation(arg0.getX(),arg0.getY()) >0 ) {
			 			sotckinfomanager.setBtngengxinxx();
			 			tabbedPane.setForegroundAt(0, Color.red);
			 			try {
			 				if(lastestUpdateDate.isBefore(LocalDate.now()) ) {
			 					lblLastestDate.setText(LocalDate.now().toString());
				 				lastestUpdateDate = LocalDate.now() ;
				 			}
			 			} catch(java.lang.NullPointerException e) {
			 				lastestUpdateDate = LocalDate.now() ;
			 			}
			 		}
			 	}
		 });
	}
	public void initialzeTreeItemTiCai (String checklistslist)
	{
		int  occuranceticai = StringUtils.countMatches(checklistslist, "&tc");
		tabbedPane.setTitleAt(0, StringUtils.substringBefore(tabbedPane.getTitleAt(0), "(") + "(" + String.valueOf(occuranceticai) + ")" );

		checklistTreeTiCai.setChecklistsitems(checklistslist);
		 checklistTreeTiCai.setStockcode(stockcode);
		 checklistTreeTiCai.epdTree(true);
		 checklistTreeTiCai.updateUI();
	}
	public String getChkLstItemsTiCai ()
	{
		checklistTreeTiCai.epdTree(false);
	    if(checklistTreeTiCai.getChecklistsitems() != null)
	    	return checklistTreeTiCai.getChecklistsitems();
	    else return null;
	}
		
	private void initlizedTreeGuiGuDong(String checklistslist) 
	{
		// TODO Auto-generated method stub
		String xmlfilepath = sysconfig.getBuyingChecklistsXMLFile ("gudong");
//		FileInputStream xmlfileinput = null;
//		try {
//			xmlfileinput = new FileInputStream(xmlfilepath);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		CheckBoxTreeXmlHandler xmlhd = new CheckBoxTreeXmlHandler(xmlfilepath);
		if(xmlhd == null)
			 return;
		 checklistTreeGuDong = new CheckBoxTree(xmlhd.getXmltreeroot(),"ROOT");
		 scrlpnGuDong.setViewportView(checklistTreeGuDong);
		
		 checklistTreeGuDong.addMouseListener(new MouseAdapter() {
			 	@Override
			 	public void mousePressed(MouseEvent arg0) 
			 	{
			 		if( checklistTreeGuDong.getRowForLocation(arg0.getX(),arg0.getY()) >0 ) {
			 			sotckinfomanager.setBtngengxinxx();
			 			tabbedPane.setForegroundAt(1, Color.red);
			 			
			 			try {
			 				if(lastestUpdateDate.isBefore(LocalDate.now()) ) {
			 					lblLastestDate.setText(LocalDate.now().toString() );
				 				lastestUpdateDate = LocalDate.now();
				 			}
			 			} catch(java.lang.NullPointerException e) {
			 				lastestUpdateDate = LocalDate.now();
			 			}
			 		}
			 			
			 	}
		 });
	}
	public void initialzeTreeItemGuDong (String checklistslist)
	{
		int  occuranceticai = StringUtils.countMatches(checklistslist, "&gd");
		tabbedPane.setTitleAt(1, StringUtils.substringBefore(tabbedPane.getTitleAt(1), "(") + "(" + String.valueOf(occuranceticai) + ")" );
		
		checklistTreeGuDong.setChecklistsitems(checklistslist);
		checklistTreeGuDong.setStockcode(stockcode);
		checklistTreeGuDong.epdTree(true);
		checklistTreeGuDong.updateUI();
	}
	public String getChkLstItemsGuDong ()
	{
		checklistTreeGuDong.epdTree(false);
	    if(checklistTreeGuDong.getChecklistsitems() != null)
	    	return checklistTreeGuDong.getChecklistsitems();
	    else return null;
	}
	
	private void initlizedTreeGuiCaiWu(String checklistslist) 
	{
		// TODO Auto-generated method stub
		String xmlfilepath = sysconfig.getBuyingChecklistsXMLFile ("caiwu");
//		FileInputStream xmlfileinput = null;
//		try {
//			xmlfileinput = new FileInputStream(xmlfilepath);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		CheckBoxTreeXmlHandler xmlhd = new CheckBoxTreeXmlHandler(xmlfilepath);
		if(xmlhd == null)
			 return;
		 checklistTreeCaiWu = new CheckBoxTree(xmlhd.getXmltreeroot(),"ROOT");
		 scrlpnCaiWuBaoBiao.setViewportView(checklistTreeCaiWu);
		 
	 
		 checklistTreeCaiWu.addMouseListener(new MouseAdapter() {
			 	@Override
			 	public void mousePressed(MouseEvent arg0) 
			 	{
			 		if( checklistTreeCaiWu.getRowForLocation(arg0.getX(),arg0.getY()) >0 ){
			 			sotckinfomanager.setBtngengxinxx();
			 			tabbedPane.setForegroundAt(2, Color.red);
			 	
			 			try {
			 				if(lastestUpdateDate.isBefore(LocalDate.now()) ) {
			 					lblLastestDate.setText(LocalDate.now().toString() );
				 				lastestUpdateDate = LocalDate.now();
				 			}
			 			} catch(java.lang.NullPointerException e) {
			 				lastestUpdateDate = LocalDate.now();
			 			}
			 		}
			 	}
		 });
	}
	public void initialzeTreeItemCaiWu (String checklistslist)
	{
		int  occuranceticai = StringUtils.countMatches(checklistslist, "&cw");
		tabbedPane.setTitleAt(2, StringUtils.substringBefore(tabbedPane.getTitleAt(2), "(") + "(" + String.valueOf(occuranceticai) + ")" );
		
		checklistTreeCaiWu.setChecklistsitems(checklistslist);
		checklistTreeCaiWu.setStockcode(stockcode);
		checklistTreeCaiWu.epdTree(true);
		checklistTreeCaiWu.updateUI();
	}
	public String getChkLstItemsCaiWu ()
	{
		checklistTreeCaiWu.epdTree(false);
	    if(checklistTreeCaiWu.getChecklistsitems() != null)
	    	return checklistTreeCaiWu.getChecklistsitems();
	    else return null;
	}

	
	private void initlizedTreeGuiJiShu(String checklistslist) 
	{
		// TODO Auto-generated method stub
		String xmlfilepath = sysconfig.getBuyingChecklistsXMLFile ("jishu");
//		FileInputStream xmlfileinput = null;
//		try {
//			xmlfileinput = new FileInputStream(xmlfilepath);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		CheckBoxTreeXmlHandler xmlhd = new CheckBoxTreeXmlHandler(xmlfilepath);
		if(xmlhd == null)
			 return;
		 checklistTreeJiShu = new CheckBoxTree(xmlhd.getXmltreeroot(),"ROOT");
		 scrlpnJiShu.setViewportView(checklistTreeJiShu);
		 
		 checklistTreeJiShu.addMouseListener(new MouseAdapter() {
			 	@Override
			 	public void mousePressed(MouseEvent arg0) 
			 	{
			 		if( checklistTreeJiShu.getRowForLocation(arg0.getX(),arg0.getY()) >0 ){
			 			sotckinfomanager.setBtngengxinxx();
			 			tabbedPane.setForegroundAt(3, Color.red);
			 			try {
			 				if(lastestUpdateDate.isBefore(LocalDate.now()) ) {
			 					lblLastestDate.setText(LocalDate.now().toString() );
				 				lastestUpdateDate = LocalDate.now();
				 			}
			 			} catch(java.lang.NullPointerException e) {
			 				lastestUpdateDate = LocalDate.now();
			 			}
			 		}
			 		
			 	}
		 });
	}
	public void initialzeTreeItemJiShu (String checklistslist)
	{
		int  occuranceticai = StringUtils.countMatches(checklistslist, "&js");
		tabbedPane.setTitleAt(3, StringUtils.substringBefore(tabbedPane.getTitleAt(3), "(") + "(" + String.valueOf(occuranceticai) + ")" );
		
		checklistTreeJiShu.setChecklistsitems(checklistslist);
		checklistTreeJiShu.setStockcode(stockcode);
		checklistTreeJiShu.epdTree(true);
		checklistTreeJiShu.updateUI();
	}
	public String getChkLstItemsJiShu ()
	{
		checklistTreeJiShu.epdTree(false);
	    if(checklistTreeJiShu.getChecklistsitems() != null)
	    	return checklistTreeJiShu.getChecklistsitems();
	    else return null;
	}
	
	private void initlizedTreeGuiZhenChe(String checklistslist) 
	{
		// TODO Auto-generated method stub
		String xmlfilepath = sysconfig.getBuyingChecklistsXMLFile ("zhenche");
//		FileInputStream xmlfileinput = null;
//		try {
//			xmlfileinput = new FileInputStream(xmlfilepath);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		CheckBoxTreeXmlHandler xmlhd = new CheckBoxTreeXmlHandler(xmlfilepath);
		if(xmlhd == null)
			 return;
		 checklistTreeZhenChe = new CheckBoxTree(xmlhd.getXmltreeroot(),"ROOT");
		 scrlpnZhenChenDaPan.setViewportView(checklistTreeZhenChe);
		 
		 checklistTreeZhenChe.addMouseListener(new MouseAdapter() {
			 	@Override
			 	public void mousePressed(MouseEvent arg0) 
			 	{
			 		if( checklistTreeZhenChe.getRowForLocation(arg0.getX(),arg0.getY()) >0 ){
			 			sotckinfomanager.setBtngengxinxx();
			 			tabbedPane.setForegroundAt(4, Color.red);
			 			try {
			 				if(lastestUpdateDate.isBefore(LocalDate.now()) ) {
			 					lblLastestDate.setText(LocalDate.now().toString() );
				 				lastestUpdateDate = LocalDate.now();
				 			}
			 			} catch(java.lang.NullPointerException e) {
			 				lastestUpdateDate = LocalDate.now();
			 			}
			 		}
			 		
			 	}
		 });
	}
	public void initialzeTreeItemZhenChe (String checklistslist)
	{
		int  occuranceticai = StringUtils.countMatches(checklistslist, "&dp");
		tabbedPane.setTitleAt(4, StringUtils.substringBefore(tabbedPane.getTitleAt(4), "(") + "(" + String.valueOf(occuranceticai) + ")" );
		
		checklistTreeZhenChe.setChecklistsitems(checklistslist);
		checklistTreeZhenChe.setStockcode(stockcode);
		checklistTreeZhenChe.epdTree(true);
		checklistTreeZhenChe.updateUI();
	}
	public String getChkLstItemsZhenChe ()
	{
		checklistTreeZhenChe.epdTree(false);
	    if(checklistTreeZhenChe.getChecklistsitems() != null)
	    	return checklistTreeZhenChe.getChecklistsitems();
	    else return null;
	}
	
	public JPanel getCheckListPanel ()
	{
		return this.contentPanel;
	}
	private final JPanel contentPanel = new JPanel();
	private JScrollPane scrlpnTiCai;
	private JScrollPane scrlpnGuDong;
	private JScrollPane scrlpnCaiWuBaoBiao;
	private JScrollPane scrlpnJiShu;
	private JScrollPane scrlpnZhenChenDaPan;
	private JButton okButton;
	private JTabbedPane tabbedPane;
	private JLabel lblLastestDate;
	private JMenuItem mntmxmlTiCai;
	private JMenuItem mntmxmlGuDong;
	private JMenuItem mntmxmlCaiWu;
	private JMenuItem mntmxmlJiShu;
	private JMenuItem mntmxmlZhenChe;
	private JButton btnRefreshXML;
	private String checklistslist;
	private JLabel lblStockCode;
	private JButton btnOpenXML;
	private JScrollPane scrollPaneSmart;

	private void initialzeGui() 
	{
		// TODO Auto-generated method stub
		setBounds(100, 100, 574, 632);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		btnRefreshXML = new JButton("");
		btnRefreshXML.setToolTipText("\u5237\u65B0XML\u6587\u4EF6");
		btnRefreshXML.setIcon(new ImageIcon("D:\\IDE\\eclipse workspace\\StockInfoManage\\img\\Refresh_22.295857988166px_1190959_easyicon.net.png"));
		
		lblStockCode = new JLabel("New label");
		
		btnOpenXML = new JButton("");
		btnOpenXML.setToolTipText("\u6253\u5F00XML\u6587\u4EF6");
		
		btnOpenXML.setIcon(new ImageIcon("D:\\IDE\\eclipse workspace\\StockInfoManage\\img\\oepndocument_24px_1206774_easyicon.net.png"));
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 534, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(lblStockCode)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnOpenXML, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnRefreshXML, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(btnOpenXML, GroupLayout.PREFERRED_SIZE, 23, Short.MAX_VALUE)
						.addComponent(btnRefreshXML, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblStockCode, Alignment.TRAILING))
					.addGap(24)
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		scrlpnTiCai = new JScrollPane();
		tabbedPane.addTab("\u9898\u6750\u548C\u6D88\u606F\u9762", null, scrlpnTiCai, null);
		
		scrlpnGuDong = new JScrollPane();
		tabbedPane.addTab("\u80A1\u4E1C\u7814\u7A76", null, scrlpnGuDong, null);
		
		scrlpnCaiWuBaoBiao = new JScrollPane();
		tabbedPane.addTab("\u8D22\u52A1\u62A5\u8868", null, scrlpnCaiWuBaoBiao, null);
		
		scrlpnJiShu = new JScrollPane();
		tabbedPane.addTab("\u6280\u672F\u9762", null, scrlpnJiShu, null);
		
		scrlpnZhenChenDaPan = new JScrollPane();
		tabbedPane.addTab("\u653F\u7B56\u9762\u4E0E\u5927\u76D8", null, scrlpnZhenChenDaPan, null);
		
		scrollPaneSmart = new JScrollPane();
		tabbedPane.addTab("New tab", null, scrollPaneSmart, null);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				
				okButton.setActionCommand("OK");
				getRootPane().setDefaultButton(okButton);
			}
			
			JLabel lblNewLabel = new JLabel("\u6700\u65B0\u66F4\u65B0\u65F6\u95F4:");
			
			lblLastestDate = new JLabel("New label");
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblNewLabel)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(lblLastestDate)
						.addGap(328)
						.addComponent(okButton)
						.addGap(37))
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(5)
						.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblNewLabel)
							.addComponent(lblLastestDate)
							.addComponent(okButton)))
			);
			buttonPane.setLayout(gl_buttonPane);
		}
		
		{
			JMenuBar menuBar = new JMenuBar();
			setJMenuBar(menuBar);
			{
				JMenu mnNewMenu = new JMenu("\u64CD\u4F5C");
				menuBar.add(mnNewMenu);
				
				mntmxmlTiCai = new JMenuItem("\u6253\u5F00\u9898\u6750XML");
				
				mnNewMenu.add(mntmxmlTiCai);
				
				mntmxmlGuDong = new JMenuItem("\u6253\u5F00\u80A1\u4E1CXML");
				
				mnNewMenu.add(mntmxmlGuDong);
				
				mntmxmlCaiWu = new JMenuItem("\u6253\u5F00\u8D22\u52A1XML");
				mnNewMenu.add(mntmxmlCaiWu);
				
				mntmxmlJiShu = new JMenuItem("\u6253\u5F00\u6280\u672FXML");
				mnNewMenu.add(mntmxmlJiShu);
				
				mntmxmlZhenChe = new JMenuItem("\u6253\u5F00\u653F\u7B56XML");
				mnNewMenu.add(mntmxmlZhenChe);
			}
		}
		
		tabbedPane.remove(scrollPaneSmart);
		this.setLocationRelativeTo(null);
		
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			BuyCheckListTreeDialog dialog = new BuyCheckListTreeDialog(null,null,null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
