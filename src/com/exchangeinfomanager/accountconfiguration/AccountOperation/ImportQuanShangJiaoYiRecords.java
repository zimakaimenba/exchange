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
		txtarealogs.append("�����˻���" + actionacnt.getAccountName() + "\n");
		
		
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
	 * @���뽻�׼�¼
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
			 //�������Ҫ�����ж����ĸ�ȯ�̣��Ա���ò�ͬ����Ĵ���
		     List<String> readLines = null;
		     try {
					readLines = Files.readLines(recordsfile,sysconfig.charSet(),new ZhaoShangBuySellRecordsProcessor ());
			 } catch (IOException ex) {
					ex.printStackTrace();
			 }
		     

			 //���̵��ļ����溬�пͻ��˻������Ժ�ϵͳ�Ľ��бȶԣ���ס������ļ�����ȷ�ģ�����ȯ��û��
		     if(readLines.get(0).contains("�ͻ���")) {
		    	 String acntid = actionacnt.getAccountid();
		    	 String  tmpzhanghuid =  readLines.get(0).replace("�ͻ���:", "").trim();
		    	 if(!tmpzhanghuid.equals(acntid)) {
		    		 JOptionPane.showMessageDialog(null, "�ƺ����Ǹ��˻��ļ�¼�ļ����븴�飡","Warning", JOptionPane.WARNING_MESSAGE);
		    		 return ;
		    	 }
		    	 readLines.remove(0);
		     } else {
		    	 JOptionPane.showMessageDialog(null, "��¼�ļ��������ͻ��ţ�������ȷ������׼ȷ�ԣ�","Warning", JOptionPane.WARNING_MESSAGE);
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
					
					//�²�����2��ѭ���������ѭ���Ǳ�֤�û�������ȷ�����ݣ�ֱ���������ݿⷵ����ȷֵ��
					//Сѭ�������û�ȷ�ϣ��Ա�֤������ȷ
					int autoIncKeyFromApi = -65535;
					do {
						if( stocknumberpricepanel.getStockcode().isEmpty() || stockcodeisfromeverusedname == true ) { //���������������������Ҫ�û�ȷ�ϱȽϱ���
							int exchangeresult ;
							do {
								 exchangeresult = JOptionPane.showConfirmDialog(null, stocknumberpricepanel, tmplinelist.get(1).trim()+ "����ϸ��", JOptionPane.OK_CANCEL_OPTION);
								
							} while (  ( stocknumberpricepanel.getStockcode().isEmpty() ||  !Pattern.matches("\\d{6}",stocknumberpricepanel.getStockcode()) ) 
									 && exchangeresult == JOptionPane.OK_OPTION) ;
							if(exchangeresult == JOptionPane.CANCEL_OPTION) {
								autoIncKeyFromApi = 1; //������ѭ��
								continue;
							}
						}
							
						autoIncKeyFromApi = acntstckconfig.buySellYuanZiOpertion (stocknumberpricepanel); 
						if(autoIncKeyFromApi > 0) {
							if(!buysell) {
								acntstckconfig.afterSellCheckAndSetProfitYuanZiOperation (actionacnt,stocknumberpricepanel); 	//�жϸ��˻������Ƿ��иù�Ʊ��û�о�˵���Ѿ�������
							}
						} else {
							System.out.println(stockcodeisfromeverusedname);
							if(autoIncKeyFromApi == -1 )
									JOptionPane.showMessageDialog(null, "����!" +"\n" 
																		+"�����������˻��ֽ����㣡"
																,"���棡", JOptionPane.WARNING_MESSAGE);
							else if(autoIncKeyFromApi == -2 )
									JOptionPane.showMessageDialog(null, "����!" +"\n" 
											+"������������ܸ��˻������иù�Ʊ��ע���Ƿ��ǹ�Ʊ������д������;" +"\n" 
									,"���棡", JOptionPane.WARNING_MESSAGE);
							if(JOptionPane.showConfirmDialog(null, "�Ƿ��ٴε���������ݣ�","����", JOptionPane.YES_NO_OPTION) == 1) {
								return;
							}
						}
						
					} while (autoIncKeyFromApi<0);
		     }
		     txtarealogs.append(recordsfile + "��Ʊ�������׵���ɹ����븴�飡" + "\n");
		     
		     listindex ++;
		}
		JOptionPane.showMessageDialog(null,  "���й�Ʊ�������׼�¼�ļ�����ɹ����븴�飡","Warning", JOptionPane.WARNING_MESSAGE);
		
	}
	/*
	 * @�����ʽ���ˮ��Ϣ
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

			 //���̵��ļ����溬�пͻ��˻������Ժ�ϵͳ�Ľ��бȶԣ���ס������ļ�����ȷ�ģ�����ȯ��û��
		     if(readLines.get(0).contains("�ͻ���")) {
		    	 String acntid = actionacnt.getAccountid();
		    	 String  tmpzhanghuid =  readLines.get(0).replace("�ͻ���:", "").trim();
		    	 if(!tmpzhanghuid.equals(acntid)) {
		    		 JOptionPane.showMessageDialog(null, "�ƺ����Ǹ��˻��ļ�¼�ļ����븴�飡","Warning", JOptionPane.WARNING_MESSAGE);
		    		 return ;
		    	 }
		    	 readLines.remove(0);
		     }
		     
		     for(String everyline:readLines) {
		    	 	List<String> tmplinelist = null;
		    	 	tmplinelist = Splitter.on("|").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(everyline);
		    	 	System.out.println(tmplinelist); //[20070613, ����ת��, true, 10000.00, 20070613            ����ת��                      0.00    0.000      0.00      0.00    10000.00       43583.85]
		    	 	
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
		     txtarealogs.append(recordsfile + "�ʽ��¼����ɹ����븴�飡" + "\n");
		     listindex ++;
		}
		JOptionPane.showMessageDialog(null,  "�����˻��ʽ���ˮ�ļ�����ɹ����븴�飡","Warning", JOptionPane.WARNING_MESSAGE);
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
		 * �����ļ�
		 */
		btnimport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(JOptionPane.showConfirmDialog(null, "��ȷ����¼�ļ�������ȷ���Ƿ������","Warning", JOptionPane.YES_NO_OPTION) == 1) {
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
		 * ѡ���ļ�
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
		 * ����·��
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
