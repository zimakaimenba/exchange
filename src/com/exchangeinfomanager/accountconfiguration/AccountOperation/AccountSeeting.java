package com.exchangeinfomanager.accountconfiguration.AccountOperation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountPuTong;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountRongQuan;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountRongZi;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountXinYongPuTong;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.CashAccountBasic;
import com.exchangeinfomanager.database.AccountDbOperation;
import com.exchangeinfomanager.database.ConnectDatabase;
import com.exchangeinfomanager.database.StockDbOperations;
import com.exchangeinfomanager.gui.AccountAndChiCangConfiguration;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.TableCellListener;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JLabel;

public class AccountSeeting extends JDialog 
{
	/**
	 * Create the dialog.
	 */
	public AccountSeeting(AccountAndChiCangConfiguration acntstckconfig, JFrame frame)
	{
//		super(frame,true);
//		this.setAlwaysOnTop(true);
		initializeSysConfig ();
		initializeAccountConfig(acntstckconfig);
		initializeGui();
    	createEvents();
	}

	//	private ConnectDatabase connectdb;
	private SystemConfigration sysconfig ;
	private AccountAndChiCangConfiguration acntstckconfig;
	private AccountDbOperation acntdbopt;
	private StockDbOperations stockdbopt;
	

	private void initializeAccountConfig(AccountAndChiCangConfiguration acntstckconfig) 
	{
		this.acntstckconfig = acntstckconfig;
		acntdbopt = new AccountDbOperation ();
		stockdbopt = new StockDbOperations ();
	}
	
	private void initializeSysConfig() 
	{
		sysconfig = SystemConfigration.getInstance();
	}
	private void createEvents() 
	{
		/*
		 * 导入普通账户的交易记录
		 */
		btnimportputongrecords.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tableputong.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个账户","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				AccountPuTong actiozhuanrunaccount = (AccountPuTong) ((AccountsPutongTableModel)tableputong.getModel()).getAccountsAt(row);
				ImportQuanShangJiaoYiRecords tmpimport = new ImportQuanShangJiaoYiRecords(actiozhuanrunaccount,acntstckconfig,acntdbopt,stockdbopt);
				tmpimport.setModal(true);
				tmpimport.setVisible(true);
				
				
			}
		});
		/*
		 * 修改账户信息
		 */
		btnXiuGaiZhanghu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tableputong.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个账户","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				AccountPuTong actiozhuanrunaccount = (AccountPuTong) ((AccountsPutongTableModel)tableputong.getModel()).getAccountsAt(row);
				
				if(actiozhuanrunaccount.hasXinYongAccount()) 
				{
					JOptionPane.showMessageDialog(null,"该账户已有信用账户！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				NewAccountCreating tmpNewActSet = new NewAccountCreating ();
				tmpNewActSet.setAccountid(actiozhuanrunaccount.getAccountid());
				tmpNewActSet.setAccountName(actiozhuanrunaccount.getAccountName() );
				tmpNewActSet.setSuoShuQuanShang(actiozhuanrunaccount.getSuoShuQuanShngYingHang());
				//tmpNewActSet.setXinYongAccountId(actiozhuanrunaccount.getGuanlianzhanghu());
				
				int settingresult = JOptionPane.showConfirmDialog(null, tmpNewActSet, "生成信用账户", JOptionPane.OK_CANCEL_OPTION);
				if(settingresult == JOptionPane.CANCEL_OPTION)
					return;
				
				String tmpactid = tmpNewActSet.getAccountid ();
				String tmpactname = tmpNewActSet.getAccountName ();
				String tmpsuoshuquanshang = actiozhuanrunaccount.getSuoShuQuanShngYingHang ();
				boolean tmpjihuo = tmpNewActSet.getJihuo();
//				if(tmpactid.equals("") || tmpactname.equals("")) {
//					JOptionPane.showMessageDialog(null, "账户名和账户ID不能为空","错误！", JOptionPane.WARNING_MESSAGE);
//					return;
//				}
				
				boolean tmprzrq = tmpNewActSet.isRongziRongquan();
				String tmprzrqid = tmpNewActSet.getXinYongAccountId();
				if (tmprzrqid != null ) {
					if(acntdbopt.updateNewRzrqAccountsToDb(tmpactid,tmpactname,tmpjihuo,tmprzrq,tmprzrqid,tmpsuoshuquanshang) >0) {
						
						acntstckconfig.refreshAccounts ();
						refreshAllAccountsTables();
						
						allTablesDispalyRelatedAcnt(tmpactname);
//						//把炒作账户保持高亮
//						int rowindex = ((AccountsPutongTableModel)tableputong.getModel() ).getAccountRowNumber(tmpactname); kkk
//						System.out.println(tmpactname + "at" + rowindex + "be selected");
//						tableputong.setRowSelectionInterval(rowindex, rowindex);
						
					} else {
						JOptionPane.showMessageDialog(null, "账户名重复！","Warning", JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		});
		
		btnrzrqguquanhz.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
//				GuQuanHuaZhuan gqhz = new GuQuanHuaZhuan();
//				gqhz.setVisible(true);
			}
		});
		
		/*
		 * 融资
		 */
		btnrongzihuankuan.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) 
			{
				int row = tableRongzi.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个账户","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				AccountRongZi actiozhuanrunaccount = (AccountRongZi) ((AccountsRongZiTableModel)tableRongzi.getModel()).getAccountsAt(row);
				String actionaccountname = actiozhuanrunaccount.getAccountName();
				String jiekuanjingestring = JOptionPane.showInputDialog(null,"输入融资还款金额","融资还款交易信息",JOptionPane.QUESTION_MESSAGE);
				try {
					double jiekuanjinge = Double.parseDouble(jiekuanjingestring);
					//actiozhuanrunaccount.rongzihuankuan (jiekuanjinge);
				} catch (java.lang.NullPointerException ex) {
					return;
				}
				
				
				refreshAllAccountsTables ();
				
				//把炒作账户保持高亮
				int rowindex = ((AccountsRongZiTableModel)tableRongzi.getModel()).getAccountRowNumber(actionaccountname);
				//System.out.println(actionaccountname + "at" + rowindex + "be selected");
				tableRongzi.setRowSelectionInterval(rowindex, rowindex);

			}
		});
		
		btnrongzi.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) 
			{
				int row = tableRongzi.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个账户J","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				AccountRongZi actiozhuanrunaccount = (AccountRongZi) ((AccountsRongZiTableModel)tableRongzi.getModel()).getAccountsAt(row);
				String actionaccountname = actiozhuanrunaccount.getAccountName();
				String jiekuanjingestring = JOptionPane.showInputDialog(null,"输入融资借款金额","融资借款交易信息",JOptionPane.QUESTION_MESSAGE);
				try {
					double jiekuanjinge = Double.parseDouble(jiekuanjingestring);
					//actiozhuanrunaccount.rongzijiekuan (jiekuanjinge);
				} catch (java.lang.NullPointerException ex) {
					return;
				}

				refreshAllAccountsTables ();
				
				//把炒作账户保持高亮
				int rowindex = ((AccountsRongZiTableModel)tableRongzi.getModel()).getAccountRowNumber(actionaccountname);
				System.out.println(actionaccountname + "at" + rowindex + "be selected");
				tableRongzi.setRowSelectionInterval(rowindex, rowindex);
			}
		});
		
		btnrzrqputongzhuanruzj.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e) 
			{
//				int row = tableRzrqPutong.getSelectedRow();
//				if(row <0) {
//					JOptionPane.showMessageDialog(null,"请选择一个账户","Warning",JOptionPane.WARNING_MESSAGE);
//					return;
//				}
//				
//				AccountXinYongPuTong actiozhuanrunaccount = (AccountXinYongPuTong) ((AccountsXinYongPutongTableModel)tableRzrqPutong.getModel()).getAccountsAt(row);
//				String actionaccountname = actiozhuanrunaccount.getAccountName();
//				
//				ZiJingHuaZhuan zijinginput = new ZiJingHuaZhuan (actionaccountname);
//				int exchangeresult = JOptionPane.showConfirmDialog(null, zijinginput, "账户" + "\"" + actiozhuanrunaccount.getAccountName() + "\"" + "资金转入", JOptionPane.OK_CANCEL_OPTION);
//				if(exchangeresult == JOptionPane.CANCEL_OPTION)
//						return;
//				double tmpzijing = zijinginput.getZijing();
//				String tmpactionaccountname = zijinginput.getZhuanRuOrChuAccount();
//				
//				if(tmpactionaccountname.contains("融资")) { //到信用融资账户表中查
//					//暂时不开放
//				} else if (tmpactionaccountname.contains("融券")) {
//					//暂时不开放
////				} else if (tmpactionaccountname.contains("现金")) {
////					actiozhuanrunaccount.zhuanruxianjing(tmpzijing);
////					CashAccountBasic cashacnt = accountsconfig.cashAccountOfIndex (tmpactionaccountname);
////					cashacnt.zijingzhuanchu(tmpzijing);
//				} else { //普通账户
//					AccountPuTong rzrqrongzi = (AccountPuTong) acntstckconfig.getAccount(tmpactionaccountname);
//					actiozhuanrunaccount.ZiJingZhuanRu(tmpzijing);
//					rzrqrongzi.ZiJingZhuanChu(tmpzijing);
//				}
//				
//				refreshAllAccountsTables ();
//				
//				//把炒作账户保持高亮
//				int rowindex = ((AccountsXinYongPutongTableModel)tableRzrqPutong.getModel()).getAccountRowNumber(actionaccountname);
//				System.out.println(actionaccountname + "at" + rowindex + "be selected");
//				tableRzrqPutong.setRowSelectionInterval(rowindex, rowindex);
			}
		});
		/*
		 * 创建新账户
		 */
		btnNewAccount.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				NewAccountCreating tmpNewActSet = new NewAccountCreating ();
				int settingresult = JOptionPane.showConfirmDialog(null, tmpNewActSet, "添加新账户", JOptionPane.OK_CANCEL_OPTION);
				if(settingresult == JOptionPane.CANCEL_OPTION)
					return;
				
				String tmpactid = tmpNewActSet.getAccountid();
				String tmpactname = tmpNewActSet.getAccountName();
				String tmpactquanshang = tmpNewActSet.getSuoShuQuanShang ();
				if(tmpactid.equals("") || tmpactname.equals("") || tmpactquanshang.equals("")) {
					JOptionPane.showMessageDialog(null, "账户名和账户ID不能为空","错误！", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				boolean tmpjihuo = tmpNewActSet.getJihuo();
				boolean tmprzrq = tmpNewActSet.isRongziRongquan();
				String tmprzrqid = tmpNewActSet.getXinYongAccountId();
				if(acntdbopt.setANewAccountToDb(tmpactid,tmpactname,tmpjihuo,tmprzrq,tmprzrqid,tmpactquanshang) >0) {
					refreshAllAccountsTables ();
					
					//把炒作账户保持高亮
					int rowindex = ((AccountsPutongTableModel)tableputong.getModel() ).getAccountRowNumber(tmpactname);
					System.out.println(tmpactname + "at" + rowindex + "be selected");
					tableputong.setRowSelectionInterval(rowindex, rowindex);
					
				} else 
					JOptionPane.showMessageDialog(null, "账户名重复！","Warning", JOptionPane.WARNING_MESSAGE);
				
			}
		});
		/*
		 * 普通账户转入现金
		 */
		btnZhuanruzijin.addMouseListener(new MouseAdapter() 
		{	 @Override
			public void mousePressed(MouseEvent arg0) 
			{
				int row = tableputong.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个账户","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}

				 AccountPuTong zhuanrunacnt = (AccountPuTong) ((AccountsPutongTableModel)tableputong.getModel()).getAccountsAt(row);
				 ZiJingHuaZhuan zijinginput = new ZiJingHuaZhuan (zhuanrunacnt);
				 zijinginput.setActionDate(new Date() );

				 int exchangeresult = JOptionPane.showConfirmDialog(null, zijinginput, "账户" + "\"" + zhuanrunacnt.getAccountName() + "\"" + "资金划转操作", JOptionPane.OK_CANCEL_OPTION);
				 if(exchangeresult == JOptionPane.CANCEL_OPTION)
						return;
				 
				acntstckconfig.accountZiJingYuanZiOpt (zhuanrunacnt,zijinginput);

				refreshAllAccountsTables ();
				
				//把操作账户保持高亮
				String actionaccountname = zhuanrunacnt.getAccountName();
				int rowindex = ((AccountsPutongTableModel)tableputong.getModel() ).getAccountRowNumber(actionaccountname);
				tableputong.setRowSelectionInterval(rowindex, rowindex);
				
				
			}
		});
		
		
		
		
		
		
		
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				dispose();
			}
		});
		
		
		tableputong.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
					 int row = tableputong.getSelectedRow();
					 int column = tableputong.getSelectedColumn();
					 
					 AccountPuTong tmpaccount = (AccountPuTong)((AccountsPutongTableModel)tableputong.getModel()).getAccountsAt(row);
					 String acntname = tmpaccount.getAccountName();
					 allTablesDispalyRelatedAcnt (acntname);
					 
//					 boolean acntstatus = tmpaccount.isJiHuo();
//					 if(acntstatus) {
//						 
//					 }
//					 else return;
			}
		});
		
		tableRzrqPutong.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
					 int row = tableRzrqPutong.getSelectedRow();
					 int column = tableRzrqPutong.getSelectedColumn();
					 
					 AccountInfoBasic tmpaccount = (AccountXinYongPuTong)((AccountsXinYongPutongTableModel)tableRzrqPutong.getModel()).getAccountsAt(row);
					 String acntname = tmpaccount.getAccountName();
					 allTablesDispalyRelatedAcnt (acntname);
//					 boolean acntstatus = tmpaccount.isJiHuo();
//					 if(acntstatus) {
//						 
//					 }
//					 else return;
			}
		});
		tableRongzi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
					 int row = tableRongzi.getSelectedRow();
					 int column = tableRongzi.getSelectedColumn();
					 
					 AccountInfoBasic tmpaccount = (AccountRongZi)((AccountsRongZiTableModel)tableRongzi.getModel()).getAccountsAt(row);
					 String acntname = tmpaccount.getAccountName();
					 allTablesDispalyRelatedAcnt (acntname);
//					 boolean acntstatus = tmpaccount.isJiHuo();
//					 if(acntstatus) {
//						 
//					 }
//					 else return;
			}
		});
		tableRongquan.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
					 int row = tableRongquan.getSelectedRow();
					 int column = tableRongquan.getSelectedColumn();
					 
					 AccountInfoBasic tmpaccount = (AccountRongQuan)((AccountsRongQuanTableModel)tableRongquan.getModel()).getAccountsAt(row);
					 String acntname = tmpaccount.getAccountName();
					 allTablesDispalyRelatedAcnt (acntname);
//					 boolean acntstatus = tmpaccount.isJiHuo();
//					 if(acntstatus) {
//						 
//					 }
//					 else return;
			}
		});
		
		tableCashAccounts.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				 int row = tableRongquan.getSelectedRow();
				 int column = tableRongquan.getSelectedColumn();
				 
				 CashAccountBasic tmpaccount = (CashAccountBasic)((CashAccountsTableModel)tableCashAccounts.getModel()).getAccountsAt(row);
				 
				String acntname = tmpaccount.getAccountName();
				 allTablesDispalyRelatedAcnt (acntname);
			}
		});
		
	}
/*
 * 保住每个表都统一显示相关的账户
 */
	protected void allTablesDispalyRelatedAcnt(String acntname) 
	{
		tableputong.clearSelection();
		tableRzrqPutong.clearSelection();
		tableRongzi.clearSelection();
		tableRongquan.clearSelection();
		tableCashAccounts.clearSelection();
		
		String basicname = "";
		int index = acntname.indexOf('-');
		if(index == -1)
			basicname = acntname;
		else 
			basicname = acntname.substring(0, index);

		
		String cashname = basicname + "- 现金账户";  
		String rzrqxyptname = basicname + "- 信用普通子账户";
		String rzrqrzname = basicname + "- 融资子账户";
		String rzrqrqname = basicname + "- 融券子账户";
		
		
		
		int rowindex;
		try {
			rowindex = ((AccountsPutongTableModel) tableputong.getModel()).getAccountRowNumber(basicname);
			//System.out.println(basicname + "at" + rowindex + "be selected");
			tableputong.setRowSelectionInterval(rowindex, rowindex);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		try {
			rowindex = ((AccountsXinYongPutongTableModel) tableRzrqPutong.getModel()).getAccountRowNumber(rzrqxyptname);
			//System.out.println(rzrqxyptname + "at" + rowindex + "be selected");
			tableRzrqPutong.setRowSelectionInterval(rowindex, rowindex);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		try {
			rowindex = ((AccountsRongZiTableModel) tableRongzi.getModel()).getAccountRowNumber(rzrqrzname);
			//System.out.println(rzrqxyptname + "at" + rowindex + "be selected");
			tableRongzi.setRowSelectionInterval(rowindex, rowindex);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		try {
			rowindex = ((AccountsRongQuanTableModel) tableRongquan.getModel()).getAccountRowNumber(rzrqrqname);
			//System.out.println(rzrqrqname + "at" + rowindex + "be selected");
			tableRongquan.setRowSelectionInterval(rowindex, rowindex);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		try {
			rowindex = ((CashAccountsTableModel) tableCashAccounts.getModel()).getAccountRowNumber(cashname);
			// System.out.println(cashname + "at" + rowindex + "be selected");
			tableCashAccounts.setRowSelectionInterval(rowindex, rowindex);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		
	}

	protected void refreshAllAccountsTables() 
	{
		acntstckconfig.refreshAccounts();
		
		
		((AccountsPutongTableModel)tableputong.getModel()).refresh(acntstckconfig.getPutongaccountsdetailmap ());
		((AccountsPutongTableModel)tableputong.getModel()).fireTableDataChanged();
		
		((AccountsXinYongPutongTableModel)tableRzrqPutong.getModel()).refresh(acntstckconfig.getRzrqputongaccountsdetailmap());
		((AccountsXinYongPutongTableModel)tableRzrqPutong.getModel()).fireTableDataChanged();
		
		
		((AccountsRongZiTableModel)tableRongzi.getModel()).refresh(acntstckconfig.getRongziaccountsdetailmap());
		((AccountsRongZiTableModel)tableRongzi.getModel()).fireTableDataChanged();
		
		((AccountsRongQuanTableModel)tableRongquan.getModel()).refresh(acntstckconfig.getRongquanaccountsdetailmap());
		((AccountsRongQuanTableModel)tableRongquan.getModel()).fireTableDataChanged();
		
		((CashAccountsTableModel)tableCashAccounts.getModel()).refresh(acntstckconfig.getCashAccountListDetailMap());
		((CashAccountsTableModel)tableCashAccounts.getModel()).fireTableDataChanged();
	}

	protected void updateNewXianjingToDb(String tmpaccount, double newxianjin) 
	{

		// TODO Auto-generated method stub
		
	}
	
	private final JPanel contentPanel = new JPanel();
	private JTable tableputong;
	private JButton btnNewAccount;
	private JButton btnZhuanruzijin;
	private JButton okButton;
	private JTable tableAccountLists;
	private JTable tableRongzi;
	private JTable tableRongquan;
	private JTable tableRzrqPutong;
	private JButton btnrzrqputongzhuanruzj;
	private JButton btnrongzi;
	private JButton btnrongzihuankuan;
	private JButton btnrzrqguquanhz;
	private JLabel lblNewLabel_4;
	private JScrollPane scrollPane_5;
	private JTable tableCashAccounts;
	private JButton btnXiuGaiZhanghu;
	private JScrollPane scrollPane_2;
	private JButton btnimportputongrecords;
	private JButton button;
	private JButton button_1;
	private JButton button_2;
	
	public void initializeGui ()
	{
		setModal(true);
		setTitle("\u8D26\u6237\u4FE1\u606F");
		
		setBounds(100, 100, 713, 826);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		btnZhuanruzijin = new JButton("\u8D44\u91D1\u5212\u8F6C");
		
		
		JScrollPane scrollPane = new JScrollPane();
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		scrollPane_2 = new JScrollPane();
		
		JScrollPane scrollPane_3 = new JScrollPane();
		JScrollPane scrollPane_4 = new JScrollPane();
		
		btnrzrqputongzhuanruzj = new JButton("\u8D44\u91D1\u5212\u8F6C");
		
		
		btnrzrqguquanhz = new JButton("\u80A1\u6743\u5212\u8F6C");
				
		btnrongzi = new JButton("\u878D\u8D44\u501F\u5165");
		
		btnrongzihuankuan = new JButton("\u878D\u8D44\u8FD8\u6B3E");
		
		
		JButton btnNewButton = new JButton("\u501F\u5238");
		btnNewButton.setEnabled(false);
		
		JButton btnNewButton_1 = new JButton("\u8FD8\u5238");
		btnNewButton_1.setEnabled(false);
		
		JLabel lblNewLabel_1 = new JLabel("\u666E\u901A\u8D26\u6237");
		
		JLabel lblNewLabel_2 = new JLabel("\u4FE1\u7528\u666E\u901A\u8D26\u6237");
		
		JLabel lblNewLabel_3 = new JLabel("\u4FE1\u7528\u878D\u8D44\u8D26\u6237");
		
		JLabel jlblrongquan = new JLabel("\u4FE1\u7528\u878D\u5238\u8D26\u6237");
		btnNewAccount = new JButton("\u65B0\u5EFA\u8D26\u6237");
		
		lblNewLabel_4 = new JLabel("\u73B0\u91D1\u8D26\u6237");
		
		scrollPane_5 = new JScrollPane();
		
		JButton btnNewButton_2 = new JButton("\u5B58\u5165\u8D44\u91D1");
		btnNewButton_2.setEnabled(false);
		
		btnXiuGaiZhanghu = new JButton("\u751F\u6210\u4FE1\u7528\u8D26\u6237");
		
		btnimportputongrecords = new JButton("\u5BFC\u5165\u8BB0\u5F55");
		
		
		button = new JButton("\u5BFC\u5165\u8BB0\u5F55");
		button.setEnabled(false);
		
		button_1 = new JButton("\u5BFC\u5165\u8BB0\u5F55");
		button_1.setEnabled(false);
		
		button_2 = new JButton("\u5BFC\u5165\u8BB0\u5F55");
		button_2.setEnabled(false);
		
		
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_contentPanel.createSequentialGroup()
								.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
									.addGroup(gl_contentPanel.createSequentialGroup()
										.addComponent(lblNewLabel_3)
										.addPreferredGap(ComponentPlacement.RELATED, 200, Short.MAX_VALUE)
										.addComponent(button_1)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnrongzi)
										.addGap(18)
										.addComponent(btnrongzihuankuan)
										.addGap(138))
									.addGroup(gl_contentPanel.createSequentialGroup()
										.addComponent(jlblrongquan)
										.addPreferredGap(ComponentPlacement.RELATED, 200, Short.MAX_VALUE)
										.addComponent(button_2)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
										.addGap(132))
									.addGroup(gl_contentPanel.createSequentialGroup()
										.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
											.addGroup(gl_contentPanel.createSequentialGroup()
												.addComponent(lblNewLabel_1)
												.addPreferredGap(ComponentPlacement.RELATED, 331, Short.MAX_VALUE)
												.addComponent(btnimportputongrecords)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(btnZhuanruzijin)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(btnXiuGaiZhanghu))
											.addComponent(scrollPane_3, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
											.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
											.addGroup(gl_contentPanel.createSequentialGroup()
												.addComponent(lblNewLabel_2)
												.addPreferredGap(ComponentPlacement.RELATED, 315, Short.MAX_VALUE)
												.addComponent(button)
												.addPreferredGap(ComponentPlacement.UNRELATED)
												.addComponent(btnrzrqputongzhuanruzj)
												.addGap(18)
												.addComponent(btnrzrqguquanhz))
											.addComponent(scrollPane_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
											.addComponent(scrollPane_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE))
										.addGap(19)))
								.addGap(0))
							.addComponent(btnNewAccount)
							.addGroup(gl_contentPanel.createSequentialGroup()
								.addComponent(lblNewLabel_4)
								.addPreferredGap(ComponentPlacement.RELATED, 322, Short.MAX_VALUE)
								.addComponent(btnNewButton_2)
								.addGap(226)))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(scrollPane_5, GroupLayout.PREFERRED_SIZE, 655, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(btnNewAccount)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(btnXiuGaiZhanghu)
						.addComponent(btnZhuanruzijin)
						.addComponent(btnimportputongrecords))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnrzrqguquanhz)
							.addComponent(btnrzrqputongzhuanruzj)
							.addComponent(button))
						.addComponent(lblNewLabel_2))
					.addGap(13)
					.addComponent(scrollPane_3, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_3)
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnrongzi)
							.addComponent(btnrongzihuankuan)
							.addComponent(button_1)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(jlblrongquan)
						.addComponent(btnNewButton)
						.addComponent(btnNewButton_1)
						.addComponent(button_2))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_4)
						.addComponent(btnNewButton_2, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_5, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		
		
		
		
		//融券账户
		HashMap<String, AccountRongQuan> rongquanaccountslist = acntstckconfig.getRongquanaccountsdetailmap();
		//String[] jrongquantableTitleStrings = accountsconfig.getRongquanSubAccountsTitles(); //{ "账户ID", "账户名称", "可用现金","股票成本","融资债务余额","历史盈亏","激活"};
		AccountsRongQuanTableModel rongquanmodel = new AccountsRongQuanTableModel( rongquanaccountslist );
		tableRongquan = new  JTable(rongquanmodel){
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
			
//		    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
//	        Component comp = super.prepareRenderer(renderer, row, col);
//	        Object value = getModel().getValueAt(row, col);
//	        
////	        try{
////	        	if ("持仓账户ALL".equals(tableputongaccountinfo.getModel().getValueAt(row, 1).toString().trim()  )) {
////	                comp.setBackground(Color.red);
//////	                Font font=new Font("serif",1,15); 
//////	            	comp.setFont(font);
////	            } else if ("true".equals(tableputongaccountinfo.getModel().getValueAt(row, 6).toString().trim()  )) {
////	            	comp.setBackground(Color.orange);
////	                comp.setForeground(Color.BLACK);
////	            } else {
////	                comp.setBackground(Color.white);
////	                comp.setForeground(Color.BLACK);
////	            }
////	        } catch (java.lang.NullPointerException e ){
////	        	comp.setBackground(Color.white);
////                comp.setForeground(Color.BLACK);
////	        }
//	        
//	        return comp;
//	    }

		};
		
		scrollPane_2.setViewportView(tableRongquan);


		// 融资账户
		HashMap<String, AccountRongZi> rongziaccountslist = acntstckconfig.getRongziaccountsdetailmap();
		//String[] jrongzitableTitleStrings = accountsconfig.getRongziSubAccountsTitles(); //{ "账户ID", "账户名称", "可用现金","股票成本","融资债务余额","历史盈亏","激活"};
		AccountsRongZiTableModel rongzimodel = new AccountsRongZiTableModel(rongziaccountslist);
		tableRongzi = new  JTable(rongzimodel){
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
		
		scrollPane_1.setViewportView(tableRongzi);
		
		//融资融券的普通账户
		HashMap<String, AccountXinYongPuTong> rzrqputongaccountslist = acntstckconfig.getRzrqputongaccountsdetailmap();
		//String[] jrzrqputongtableTitleStrings = accountsconfig.getRzrqPutongSubAccountsTitles(); // { "账户ID", "账户名称", "可用现金","股票成本","历史盈亏","激活"};
		AccountsXinYongPutongTableModel rzrqputongmodel = new AccountsXinYongPutongTableModel(rzrqputongaccountslist);
		tableRzrqPutong = new JTable(rzrqputongmodel) {

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

		scrollPane_3.setViewportView(tableRzrqPutong);
		
		//普通账户
		HashMap<String, AccountPuTong> putongaccountslist = acntstckconfig.getPutongaccountsdetailmap ();
		AccountsPutongTableModel putongmodel = new AccountsPutongTableModel(putongaccountslist);
		tableputong = new JTable(putongmodel) {

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
		//tableputongaccountinfo.getTableHeader().getColumnModel().getColumn(6).setMaxWidth(0);
//		tableputongaccountinfo.getTableHeader().getColumnModel().getColumn(6).setMinWidth(0);
//		tableputongaccountinfo.getTableHeader().getColumnModel().getColumn(6).setWidth(0);
//		tableputongaccountinfo.getTableHeader().getColumnModel().getColumn(6).setPreferredWidth(0);
//		tableaccountinfo.getTableHeader().getColumnModel().getColumn(7).setMaxWidth(0);
//		tableaccountinfo.getTableHeader().getColumnModel().getColumn(7).setMinWidth(0);
//		tableaccountinfo.getTableHeader().getColumnModel().getColumn(7).setWidth(0);
//		tableaccountinfo.getTableHeader().getColumnModel().getColumn(7).setPreferredWidth(0);
		scrollPane.setViewportView(tableputong);
		
		HashMap<String, CashAccountBasic> cashaccountslist = acntstckconfig.getCashAccountListDetailMap();
		CashAccountsTableModel cashaccountsmodel = new CashAccountsTableModel(cashaccountslist);
		tableCashAccounts = new JTable(cashaccountsmodel){

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
		
		scrollPane_5.setViewportView(tableCashAccounts);
		
		
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				
				okButton.setActionCommand("OK");
				getRootPane().setDefaultButton(okButton);
			}
			
			JLabel lblNewLabel = new JLabel("* \u8D26\u6237\u5FC5\u987B\u5148\u6FC0\u6D3B\u624D\u53EF\u4EE5\u8FDB\u884C\u64CD\u4F5C\uFF01");
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(22)
						.addComponent(lblNewLabel)
						.addGap(405)
						.addComponent(okButton)
						.addGap(30))
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(5)
						.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblNewLabel)
							.addComponent(okButton)))
			);
			buttonPane.setLayout(gl_buttonPane);
		}
		
		//this. setLocationRelativeTo(null); 
	}
	
	@Override
	public void paint(Graphics g) {
	    super.paint(g);
	    Rectangle screen = this.getGraphicsConfiguration().getBounds();
	    this.setLocation(
	        screen.x + (screen.width - this.getWidth()) / 2,
	        screen.y + (screen.height - this.getHeight()) / 2
	    );
	}
}


class AccountsRongQuanTableModel extends AbstractTableModel
{
	private  ArrayList<AccountRongQuan> accountslist;
	String[] jtableTitleStrings = { "账户ID", "账户名称", "可用现金","融券余额","历史盈亏","所属券商","激活"};
	
	AccountsRongQuanTableModel(HashMap<String, AccountRongQuan> rongquanaccountslist)
	{
		 accountslist = new ArrayList<AccountRongQuan> ();
		 refresh (rongquanaccountslist);
		
	}
	
	
	 public void refresh( HashMap<String, AccountRongQuan> rongquanaccountslist) 
	 {
		 this.accountslist.clear();
		 for (java.util.Map.Entry<String, AccountRongQuan> entry : rongquanaccountslist.entrySet()) {
				//cashaccountnamelist.add(entry.getKey() );
				this.accountslist.add(entry.getValue());
			}
	}
	public int getAccountRowNumber (String acntname)
	{
						int index =0;
						for(AccountRongQuan tmpacnt:accountslist )
							if(tmpacnt.getAccountName().equals(acntname))
								return index;
							else index ++;
						return -1;
	}


	public int getRowCount() 
	 {
	        return accountslist.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    }
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {//{ "账户ID", "账户名称", "可用现金","融券余额","历史盈亏","所属券商","激活"};
	    	Object value = "??";
	    	AccountRongQuan account = (AccountRongQuan)accountslist.get(rowIndex);
	    	
	    	switch (columnIndex) {
            case 0:
                value = account.getAccountid();
                break;
            case 1:
                value = account.getAccountName();
                break;
            case 2:
                value = account.getCurrentAvailableXianJin();
                break;
            case 3:
                value = account.getRongquanyuer();
                break;
            case 4:
                value = account.getHistoryProfit();
                break;
            case 5:
            	value = account.getSuoShuQuanShngYingHang ();
                break;
            case 6:
                value = new Boolean(account.isJiHuo());
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
			          clazz = Double.class;
			          break;
		        case 3:
			          clazz = Integer.class;
			          break;
		        case 4:
			          clazz = Double.class;
			          break;
		        case 5:
		        	clazz = String.class;
		        	break;
		        case 6:
		          clazz = Boolean.class;
		          break;
		      }
		      
		      return clazz;
		}  
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) 
	    {
	    	if(5 == column) {
	    		boolean status = ((AccountInfoBasic)accountslist.get(row)).isJiHuo();
				((AccountInfoBasic)accountslist.get(row)).setJihuo(!status);
				return true;
	    	} else
	    		return false;
		}
	    
	    public AccountInfoBasic getAccountsAt(int row) {
	        return accountslist.get(row);
	    }
	
}

class AccountsRongZiTableModel extends AbstractTableModel 
{
	private  ArrayList<AccountRongZi> accountslist;
	String[] jtableTitleStrings = { "账户ID", "账户名称", "可用现金","股票成本","融资债务余额","历史盈亏","所属券商","激活"};
	
	AccountsRongZiTableModel (HashMap<String, AccountRongZi> rongziaccountslist)
	{
		accountslist = new ArrayList<AccountRongZi> ();
		refresh (rongziaccountslist);
	}
	
	 public void refresh(HashMap<String, AccountRongZi> rongziaccountslist) 
	 {
		 this.accountslist.clear();
		 for (java.util.Map.Entry<String, AccountRongZi> entry : rongziaccountslist.entrySet()) {
				//cashaccountnamelist.add(entry.getKey() );
				this.accountslist.add(entry.getValue());
			}
	}
	public int getAccountRowNumber (String acntname)
	{
					int index =0;
					for(AccountRongZi tmpacnt:accountslist )
						if(tmpacnt.getAccountName().equals(acntname))
							return index;
						else index ++;
					return -1;
	}

	public int getRowCount() 
	 {
	        return accountslist.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    }
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	Object value = "??";
	    	AccountRongZi account = (AccountRongZi)accountslist.get(rowIndex);
	    	
	    	switch (columnIndex) {
            case 0:
                value = account.getAccountid();
                break;
            case 1:
                value = account.getAccountName();
                break;
            case 2:
                value = account.getCurrentAvailableXianJin();
                break;
            case 3:
                value = account.getTotalZiJingChenBen();
                break;
            case 4:
                value = account.getRongZiZhaiWu ();
                break;
            case 5:
                value = account.getHistoryProfit();
                break;
            case 6:
            	value = account.getSuoShuQuanShngYingHang ();
                break;
            case 7:
                value = new Boolean(account.isJiHuo());
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
			          clazz = Double.class;
			          break;
		        case 3:
			          clazz = Double.class;
			          break;
		        case 4:
			          clazz = Double.class;
			          break;
		        case 5:
			          clazz = Double.class;
			          break;
		        case 6:
		        	clazz = String.class;
		        	break;
		        case 7:
		          clazz = Boolean.class;
		          break;
		      }
		      
		      return clazz;
		}  
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	if(6 == column) {
	    		boolean status = ((AccountInfoBasic)accountslist.get(row)).isJiHuo();
				((AccountInfoBasic)accountslist.get(row)).setJihuo(!status);
				return true;
	    	} else return false;
		}
	    
	    public AccountInfoBasic getAccountsAt(int row) {
	        return accountslist.get(row);
	    }
	    
}

class AccountsPutongTableModel extends AbstractTableModel 
{
	private  ArrayList<AccountPuTong> accountslist;
	String[] jtableTitleStrings = {"账户ID", "账户名称", "可用现金","已用现金(股票成本)","历史盈亏","所属券商","激活","有信用账户"};
	
	AccountsPutongTableModel (HashMap<String, AccountPuTong> putongaccountslist)
	{
		accountslist = new ArrayList<AccountPuTong> ();
		refresh (putongaccountslist);
	}
	public void refresh  (HashMap<String, AccountPuTong> putongaccountslist)
	{
		this.accountslist.clear();
		for (java.util.Map.Entry<String, AccountPuTong> entry : putongaccountslist.entrySet()) {
			//cashaccountnamelist.add(entry.getKey() );
			this.accountslist.add(entry.getValue());
		}
	}
	 public int getRowCount() 
	 {
	        return accountslist.size();
	 }
	public int getAccountRowNumber (String acntname)
	{
				int index =0;
				for(AccountPuTong tmpacnt:accountslist )
					if(tmpacnt.getAccountName().equals(acntname))
						return index;
					else index ++;
				return -1;
	}
    public int getColumnCount() 
    {
	        return jtableTitleStrings.length;
    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	Object value = "??";
	    	//AccountPuTong account = (AccountPuTong)accountslist.get(rowIndex);
	    	AccountPuTong account = (AccountPuTong)accountslist.get(rowIndex);
	    	
	    	switch (columnIndex) {
            case 0:
                value = account.getAccountid();
                break;
            case 1:
                value = account.getAccountName();
                break;
            case 2:
                value = account.getCurrentAvailableXianJin();
                break;
            case 3:
                value = account.getTotalZiJingChenBen();
                break;
            case 4:
                value = account.getHistoryProfit();
                break;
            case 5:
            	value = account.getSuoShuQuanShngYingHang ();
                break;
            case 6:
                value = new Boolean(account.isJiHuo());
                break;
            
            case 7:
                value = new Boolean(account.hasXinYongAccount());
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
			          clazz = Double.class;
			          break;
		        case 3:
			          clazz = Double.class;
			          break;
		        case 4:
			          clazz = Double.class;
			          break;
		        case 5:
		        	clazz = String.class;
		        	break;
		        case 6:
		          clazz = Boolean.class;
		          break;
		
		        case 7:
			          clazz = Boolean.class;
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
			if(5 == column) {
				boolean status = ((AccountInfoBasic)accountslist.get(row)).isJiHuo();
				((AccountInfoBasic)accountslist.get(row)).setJihuo(!status);
				return true;
			} else return false;
		}
	    
	    public AccountInfoBasic getAccountsAt(int row) {
	        return accountslist.get(row);
	    }
}


class AccountsXinYongPutongTableModel extends AbstractTableModel 
{
	private  ArrayList<AccountXinYongPuTong> accountslist;
	String[] jtableTitleStrings = { "账户ID", "账户名称", "可用现金","已用现金(股票成本)","历史盈亏","所属券商","激活"};
	
	AccountsXinYongPutongTableModel (HashMap<String, AccountXinYongPuTong> rzrqputongaccountslist)
	{
		accountslist = new ArrayList<AccountXinYongPuTong> ();
		refresh (rzrqputongaccountslist);

	}
	public void refresh  (HashMap<String, AccountXinYongPuTong> rzrqputongaccountslist)
	{
		this.accountslist.clear();
		for (java.util.Map.Entry<String, AccountXinYongPuTong> entry : rzrqputongaccountslist.entrySet()) {
			//cashaccountnamelist.add(entry.getKey() );
			this.accountslist.add(entry.getValue());
		}
	}
	public int getAccountRowNumber (String acntname)
	{
			int index =0;
			for(AccountXinYongPuTong tmpacnt:accountslist )
				if(tmpacnt.getAccountName().equals(acntname))
					return index;
				else index ++;
			return -1;
	}
	 public int getRowCount() 
	 {
	        return accountslist.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	Object value = "??";
	    	//AccountPuTong account = (AccountPuTong)accountslist.get(rowIndex);
	    	AccountInfoBasic account = (AccountInfoBasic)accountslist.get(rowIndex);
	    	
	    	switch (columnIndex) {
            case 0:
                value = account.getAccountid();
                break;
            case 1:
                value = account.getAccountName();
                break;
            case 2:
                value = account.getCurrentAvailableXianJin();
                break;
            case 3:
                value = account.getTotalZiJingChenBen();
                break;
            case 4:
                value = account.getHistoryProfit();
                break;
            case 5:
            	value = account.getSuoShuQuanShngYingHang ();
                break;
            case 6:
                value = new Boolean(account.isJiHuo());
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
			          clazz = Double.class;
			          break;
		        case 3:
			          clazz = Double.class;
			          break;
		        case 4:
			          clazz = Double.class;
			          break;
		        case 5:
		        	clazz = String.class;
		        	break;
		        case 6:
		          clazz = Boolean.class;
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
			if(5 == column) {
				boolean status = ((AccountInfoBasic)accountslist.get(row)).isJiHuo();
				((AccountInfoBasic)accountslist.get(row)).setJihuo(!status);
				return true;
			} else return false;
		}
	    
	    public AccountInfoBasic getAccountsAt(int row) {
	        return accountslist.get(row);
	    }
}



class CashAccountsTableModel extends AbstractTableModel 
{
	private  ArrayList<CashAccountBasic> accountslist;
	String[] jtableTitleStrings = { "账户ID", "账户名称", "可用现金","激活"};
	
	CashAccountsTableModel (HashMap<String, CashAccountBasic> cashaccountslist)
	{
		this.accountslist = new ArrayList<CashAccountBasic> ();
		refresh (cashaccountslist);
	
	}
	public void refresh  (HashMap<String, CashAccountBasic> cashaccountslist)
	{
		this.accountslist.clear();
		this.accountslist.addAll(cashaccountslist.values());
//		for (java.util.Map.Entry<String, CashAccountBasic> entry : cashaccountslist.entrySet()) {
//			//cashaccountnamelist.add(entry.getKey() );
//			this.accountslist.add(entry.getValue());
//		}
	}
	public int getAccountRowNumber (String acntname)
	{
			int index =0;
			for(CashAccountBasic tmpacnt:accountslist )
				if(tmpacnt.getAccountName().equals(acntname))
					return index;
				else index ++;
			return -1;
	}
	 public int getRowCount() 
	 {
	        return accountslist.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	Object value = "??";
	    	//AccountPuTong account = (AccountPuTong)accountslist.get(rowIndex);
	    	CashAccountBasic account = (CashAccountBasic)accountslist.get(rowIndex);
	    	
	    	switch (columnIndex) {
            case 0:
                value = account.getAccountid();
                break;
            case 1:
                value = account.getAccountName();
                break;
            case 2:
                value = account.getCurrentCash();
                break;
            case 3:
                value = new Boolean(account.isJiHuo());
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
			      clazz = Double.class;
			      break;
		        case 3:
		          clazz = Boolean.class;
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
			if(3 == column) {
				boolean status = ((CashAccountBasic)accountslist.get(row)).isJiHuo();
				((CashAccountBasic)accountslist.get(row)).setJihuo(!status);
				return true;
			} else return false;
		}
	    
	    public CashAccountBasic getAccountsAt(int row) {
	        return accountslist.get(row);
	    }
}


