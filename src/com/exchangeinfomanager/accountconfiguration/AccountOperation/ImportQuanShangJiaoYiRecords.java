package com.exchangeinfomanager.accountconfiguration.AccountOperation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountPuTong;
import com.exchangeinfomanager.database.AccountDbOperation;
import com.exchangeinfomanager.database.StockDbOperations;
import com.exchangeinfomanager.gui.AccountAndChiCangConfiguration;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.io.Files;

import javax.swing.JTextField;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JCheckBox;

public class ImportQuanShangJiaoYiRecords extends JDialog {


	

	/**
	 * Create the dialog.
	 */
	public ImportQuanShangJiaoYiRecords(AccountInfoBasic actiozaccount ,AccountAndChiCangConfiguration acntstckconfig2,AccountDbOperation acntdbopt2,StockDbOperations stockdbopt2)
	{
		initializeGui ();
		createEvents ();
		initializeSysConfig();
		
		this.actionacnt = actiozaccount;
		this.acntstckconfig = acntstckconfig2;
		this.acntdbopt = acntdbopt2;
		this.stockdbopt = stockdbopt2; 
		txtarealogs.append("导入账户：" + actionacnt.getAccountName() + "\n");
		
		
	}
	private AccountInfoBasic actionacnt;
	private AccountAndChiCangConfiguration acntstckconfig;
	private AccountDbOperation acntdbopt;
	private StockDbOperations stockdbopt;
	private SystemConfigration sysconfig;
	
	private void initializeSysConfig() 
	{
		sysconfig = SystemConfigration.getInstance();
	}

	/*
	 * @导入交易记录
	 */
	protected void performImportRecordsActions(ArrayList<String> filenamelist) 
	{
		String acntname = actionacnt.getAccountName();
		int listindex = 0;
		
		for(String str:filenamelist) {
			listRight.setSelectedIndex(listindex);
			listRight.ensureIndexIsVisible(listindex);
			//System.out.println(tfldrecordspath.getText() + str);
			
			 File recordsfile = new File(tfldrecordspath.getText() + str);
			 //这里后面要插入判断是哪个券商，以便调用不同的类的代码
		     List<String> readLines = null;
		     try {
					readLines = Files.readLines(recordsfile,sysconfig.charSet(),new ZhaoShangBuySellRecordsProcessor ());
			 } catch (IOException ex) {
					ex.printStackTrace();
			 }
		     

			 //招商的文件里面含有客户账户，可以和系统的进行比对，保住导入的文件是正确的，其他券商没有
		     if(readLines.get(0).contains("客户号")) {
		    	 String acntid = actionacnt.getAccountid();
		    	 String  tmpzhanghuid =  readLines.get(0).replace("客户号:", "").trim();
		    	 if(!tmpzhanghuid.equals(acntid)) {
		    		 JOptionPane.showMessageDialog(null, "似乎不是该账户的记录文件，请复查！","Warning", JOptionPane.WARNING_MESSAGE);
		    		 return ;
		    	 }
		    	 readLines.remove(0);
		     } else {
		    	 JOptionPane.showMessageDialog(null, "记录文件不包含客户号，请自行确保数据准确性！","Warning", JOptionPane.WARNING_MESSAGE);
		     }
		     
		     for(String everyline:readLines) {
		    	 	List<String> tmplinelist = null;
		    	 	tmplinelist = Splitter.on("|").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(everyline);
		    	 	System.out.println(tmplinelist);
		    	 	
		    	 	Date lineactiondate = null;
					try {
						lineactiondate = new SimpleDateFormat( "yyyyMMdd" ).parse(tmplinelist.get(0));
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
					
					
					String actionstockcode;
					boolean stockcodeisfromeverusedname = false;
					if(tmplinelist.get(4).equals("XXXXXX"))
						try{
							actionstockcode = stockdbopt.getStockCodeByName (tmplinelist.get(3)).trim();
							stockcodeisfromeverusedname = false;
						} catch (java.lang.NullPointerException e) {
							actionstockcode = stockdbopt.getStockCodeByEverUsedName (tmplinelist.get(3));
							stockcodeisfromeverusedname = true;
						}
					else 
						actionstockcode = tmplinelist.get(4); 
					boolean buysell = Boolean.parseBoolean(tmplinelist.get(2));
					BuyStockNumberPrice stocknumberpricepanel = new BuyStockNumberPrice (actionstockcode,null,buysell);
					stocknumberpricepanel.setStockcode(actionstockcode);
					stocknumberpricepanel.setActionDay(lineactiondate);
					stocknumberpricepanel.setJiaoYiGuShu(Integer.parseInt( tmplinelist.get(5)));
					stocknumberpricepanel.setJiaoYiJiaGe(Double.parseDouble(tmplinelist.get(6)));
					stocknumberpricepanel.setJiaoYiShuoMing(tmplinelist.get(7));
					stocknumberpricepanel.setJiaoyiZhanghu(acntname);
					
					//下部分有2个循环，外面大循环是保证用户输入正确的数据，直至导入数据库返回正确值。
					//小循环是让用户确认，以保证输入正确
					int autoIncKeyFromApi = -65535;
					do {
						if( stocknumberpricepanel.getStockcode().isEmpty() || stockcodeisfromeverusedname == true ) { //如果是用曾用名，还是需要用户确认比较保险
							int exchangeresult ;
							do {
								 exchangeresult = JOptionPane.showConfirmDialog(null, stocknumberpricepanel, tmplinelist.get(1).trim()+ "交易细节", JOptionPane.OK_CANCEL_OPTION);
								
							} while (  ( stocknumberpricepanel.getStockcode().isEmpty() ||  !Pattern.matches("\\d{6}",stocknumberpricepanel.getStockcode()) ) 
									 && exchangeresult == JOptionPane.OK_OPTION) ;
							if(exchangeresult == JOptionPane.CANCEL_OPTION) {
								autoIncKeyFromApi = 1; //跳出大循环
								continue;
							}
						}
							
						autoIncKeyFromApi = acntstckconfig.buySellYuanZiOpertion (stocknumberpricepanel); 
						if(autoIncKeyFromApi > 0) {
							if(!buysell) {
								acntstckconfig.afterSellCheckAndSetProfitYuanZiOperation (actionacnt,stocknumberpricepanel); 	//判断该账户里面是否还有该股票，没有就说明已经卖完了
							}
						} else {
							System.out.println(stockcodeisfromeverusedname);
							if(autoIncKeyFromApi == -1 )
									JOptionPane.showMessageDialog(null, "错误!" +"\n" 
																		+"如果买入可能账户现金余额不足！"
																,"警告！", JOptionPane.WARNING_MESSAGE);
							else if(autoIncKeyFromApi == -2 )
									JOptionPane.showMessageDialog(null, "错误!" +"\n" 
											+"如果是卖出可能该账户不持有该股票，注意是否是股票代码填写错误导致;" +"\n" 
									,"警告！", JOptionPane.WARNING_MESSAGE);
							if(JOptionPane.showConfirmDialog(null, "是否再次导入该条数据？","警告", JOptionPane.YES_NO_OPTION) == 1) {
								return;
							}
						}
						
					} while (autoIncKeyFromApi<0);
		     }
		     txtarealogs.append(recordsfile + "股票买卖交易导入成功，请复查！" + "\n");
		     
		     listindex ++;
		}
		JOptionPane.showMessageDialog(null,  "所有股票买卖交易记录文件导入成功，请复查！","Warning", JOptionPane.WARNING_MESSAGE);
		
	}
	/*
	 * @导入资金流水信息
	 */
	private void performImportMoneyActions(ArrayList<String> filenamelist) 
	{
		String acntname = actionacnt.getAccountName();
		int listindex = 0;
		
		for(String str:filenamelist) {
			listRight.setSelectedIndex(listindex);
			listRight.ensureIndexIsVisible(listindex);
			//System.out.println(tfldrecordspath.getText() + str);
			
			File recordsfile = new File(tfldrecordspath.getText() + str);
		     List<String> readLines = null;
		     try {
					readLines = Files.readLines(recordsfile,sysconfig.charSet(),new ZhaoShangMoneyFlowRecordsProcessor ());
			 } catch (IOException ex) {
					ex.printStackTrace();
			 }

			 //招商的文件里面含有客户账户，可以和系统的进行比对，保住导入的文件是正确的，其他券商没有
		     if(readLines.get(0).contains("客户号")) {
		    	 String acntid = actionacnt.getAccountid();
		    	 String  tmpzhanghuid =  readLines.get(0).replace("客户号:", "").trim();
		    	 if(!tmpzhanghuid.equals(acntid)) {
		    		 JOptionPane.showMessageDialog(null, "似乎不是该账户的记录文件，请复查！","Warning", JOptionPane.WARNING_MESSAGE);
		    		 return ;
		    	 }
		    	 readLines.remove(0);
		     }
		     
		     for(String everyline:readLines) {
		    	 	List<String> tmplinelist = null;
		    	 	tmplinelist = Splitter.on("|").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(everyline);
		    	 	System.out.println(tmplinelist); //[20070613, 银行转存, true, 10000.00, 20070613            银行转存                      0.00    0.000      0.00      0.00    10000.00       43583.85]
		    	 	
		    	 	Date lineactiondate = null;
					try {
						lineactiondate = new SimpleDateFormat( "yyyyMMdd" ).parse(tmplinelist.get(0));
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
					String quanshangactiontype = tmplinelist.get(1);
					//String actiontype = tmplinelist.get(2);
					boolean iszhuanruchu = Boolean.parseBoolean(tmplinelist.get(2));
					double zhuanruchumoney = Math.abs( Double.parseDouble(tmplinelist.get(3)) );
					
					ZiJingHuaZhuan zjhz = new ZiJingHuaZhuan(actionacnt);
					zjhz.setQuanShangZhuanRuOrChuLeiXing(quanshangactiontype);
					zjhz.setActionDate(lineactiondate);
					zjhz.setZiJing(zhuanruchumoney);
					zjhz.setShuoMing(everyline);
					
					acntstckconfig.accountZiJingYuanZiOpt (actionacnt, zjhz);
					
		     }
		     txtarealogs.append(recordsfile + "资金记录导入成功，请复查！" + "\n");
		     listindex ++;
		}
		JOptionPane.showMessageDialog(null,  "所有账户资金流水文件导入成功，请复查！","Warning", JOptionPane.WARNING_MESSAGE);
	}
	
	private void setRecordsPathAndFiles()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			((DefaultListModel)listLeft.getModel()).removeAllElements();
			((DefaultListModel)listRight.getModel()).removeAllElements();
			
			String linuxpath;
		    if(chooser.getSelectedFile().toString().length() == 3)
		    	 linuxpath = (chooser.getSelectedFile().toString()).replace('\\', '/');
		    else 
		    	linuxpath = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
		    System.out.println(linuxpath);
		    tfldrecordspath.setText(linuxpath);
		    
		    File recordspath = chooser.getSelectedFile();
		    File[] filesList = recordspath.listFiles();
	        for(File f : filesList){
//	            if(f.isDirectory())
//	                System.out.println(f.getName());
	            if(f.isFile() && f.getName().endsWith("txt")){
	                ((DefaultListModel)listLeft.getModel()).addElement(f.getName());
	            }
	        } 
		    
		}
	}
	
	private void createEvents() 
	{
		btnrtol.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				 List<Object> filename = listRight.getSelectedValuesList();
				 for(Object str : filename)
					 ((DefaultListModel)listRight.getModel()).removeElement(str);
				 
				
			}
		});
		
		/*
		 * 导入文件
		 */
		btnimport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(JOptionPane.showConfirmDialog(null, "请确保记录文件排序正确，是否继续？","Warning", JOptionPane.YES_NO_OPTION) == 1) {
					return;
				}
				
				ArrayList<String> filenamelist = new ArrayList<String>  ();
				for(int i=0;i<((DefaultListModel)listRight.getModel()).getSize();i++) {
					String tmpname = ((DefaultListModel)listRight.getModel()).getElementAt(i).toString();
					filenamelist.add(tmpname);
				}
				
				if(cbximportmoney.isSelected())
					performImportMoneyActions (filenamelist);
				if(cbximportjiaoyi.isSelected())
					performImportRecordsActions (filenamelist);
									
			}

			
		});
		/*
		 * 选择文件
		 */
		btnltor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				//String filename=  ((DefaultListModel)listLeft.getModel()).getElementAt( listLeft.getSelectedIndex()).toString();
				int i = 0;
				i++;
				 List<Object> filename = listLeft.getSelectedValuesList();
				 for(Object str : filename)
					 ((DefaultListModel)listRight.getModel()).addElement(str);
			}
		});
		
		/*
		 * 设置路径
		 */
		btnchospath.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				setRecordsPathAndFiles ();
			}
		});
		
	}



	
	private final JPanel contentPanel = new JPanel();
	private JTextField tfldrecordspath;
	private JButton btnchospath;
	private JList listLeft;
	private JButton btnltor;
	private JButton btnrtol;
	private JButton btnimport;
	private JList listRight;
	private JTextArea txtarealogs;
	private JCheckBox cbximportmoney;
	private JCheckBox cbximportjiaoyi;
	private void initializeGui() 
	{
		setTitle("\u5BFC\u5165\u4E70\u5356\u8BB0\u5F55");
		setBounds(100, 100, 474, 579);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JLabel label = new JLabel("\u8BB0\u5F55\u6587\u4EF6\u8DEF\u5F84");
		
		tfldrecordspath = new JTextField();
		tfldrecordspath.setEditable(false);
		tfldrecordspath.setColumns(10);
		
		btnchospath = new JButton("");
		
		btnchospath.setIcon(new ImageIcon(ImportQuanShangJiaoYiRecords.class.getResource("/images/open24.png")));
		
		JScrollPane scrollPane = new JScrollPane();
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		btnltor = new JButton("->");
		
		
		btnrtol = new JButton("<-");
		
		
		JScrollPane scrollPane_2 = new JScrollPane();
		
		btnimport = new JButton("\u5F00\u59CB\u5BFC\u5165");
		
		
		JLabel label_1 = new JLabel("*\u786E\u4FDD\u8BB0\u5F55\u6587\u4EF6\u6309\u65F6\u95F4\u6B63\u786E\u6392\u5E8F\uFF0C\u5426\u5219\u4F1A\u51FA\u95EE\u9898");
		
		cbximportjiaoyi = new JCheckBox("\u5BFC\u5165\u4EA4\u6613\u8BB0\u5F55");
		cbximportjiaoyi.setSelected(true);
		
		cbximportmoney = new JCheckBox("\u5BFC\u5165\u8D44\u91D1\u6D41\u6C34");
		cbximportmoney.setSelected(true);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(label)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tfldrecordspath, GroupLayout.PREFERRED_SIZE, 254, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnchospath)
					.addContainerGap(55, Short.MAX_VALUE))
				.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
					.addComponent(label_1)
					.addGap(38)
					.addComponent(btnimport)
					.addContainerGap(59, Short.MAX_VALUE))
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnltor)
						.addComponent(btnrtol))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
					.addGap(27))
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(cbximportmoney)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(cbximportjiaoyi))
						.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(tfldrecordspath, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnchospath))
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(18)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 214, GroupLayout.PREFERRED_SIZE)
								.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 211, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(66)
							.addComponent(btnltor)
							.addGap(38)
							.addComponent(btnrtol)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_1)
						.addComponent(btnimport))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(cbximportjiaoyi)
						.addComponent(cbximportmoney))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE))
		);
		
		txtarealogs = new JTextArea();
		scrollPane_2.setViewportView(txtarealogs);
		
		DefaultListModel listModelright = new DefaultListModel();
		listRight = new JList(listModelright);
		listRight.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		listRight.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index,
                      boolean isSelected, boolean cellHasFocus) {
                 Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                 if (isSelected)
                	 c.setBackground(Color.RED);
                 return c;
            }

       });
		scrollPane_1.setViewportView(listRight);
		
		DefaultListModel listModelleft = new DefaultListModel();
		listLeft = new JList(listModelleft);
		listLeft.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listLeft);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						dispose ();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		
		 this.setLocationRelativeTo(null); 
		
	}
}
