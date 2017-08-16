package com.exchangeinfomanager.bankuai.gui;

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

import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian;
import com.exchangeinfomanager.bankuaichanyelian.ChanYeLianXMLHandler2;
import com.exchangeinfomanager.bankuaichanyelian.TwelveZhongDianGuanZhuXmlHandler;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.ChanYeLianXMLHandler;
import com.exchangeinfomanager.database.StockDbOperations;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.google.common.collect.Ordering;

//import net.ginkgo.copy.Ginkgo2;
import net.ginkgo.dom4jcopy.Ginkgo2;
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

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
	public BanKuaiGuanLi(StockInfoManager stockInfoManager2, BanKuaiDbOperation bkdbopt2,StockDbOperations stockdbopt2, TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler2, ChanYeLianXMLHandler2 cylxmlhandler2) 
	{

		this.bkdbopt = bkdbopt2;
		this.stockdbopt = stockdbopt2;
		this.cylxmlhandler = cylxmlhandler2;
		this.zdgzbkxmlhandler = zdgzbkxmlhandler2;
		this.stockInfoManager = stockInfoManager2;
		startDialog ();
	}
	
	private TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler;
	private StockInfoManager stockInfoManager;	
	private BanKuaiDbOperation bkdbopt;
	private StockDbOperations stockdbopt;
	private ChanYeLianXMLHandler2 cylxmlhandler;
//	private HashMap<String,BanKuai> sysbankuailist;
//	private HashMap<String,BanKuai> zdybankuailist;
//	private HashMap<String, BanKuai> zhishulist;

	public void startDialog ()
	{
		initializeGui ();
		createEvents ();
		initializeTDXBanKuaiLists ();
		initializeTDXZhiShuLists ();
		initialzieZdyBanKuaList ();
	}

	private void initializeTDXZhiShuLists() 
	{
		 HashMap<String,BanKuai> zhishulist = bkdbopt.getTDXAllZhiShuList (); 
		
		((BanKuaiDetailTableModel)tablezhishu.getModel()).refresh(zhishulist);
		((BanKuaiDetailTableModel)tablezhishu.getModel()).fireTableDataChanged();
		
	}

	private void initialzieZdyBanKuaList() 
	{
		 HashMap<String,BanKuai> zdybankuailist = bkdbopt.getZdyBanKuaiList (); 
		
		((BanKuaiDetailTableModel)tableZdy.getModel()).refresh(zdybankuailist);
		((BanKuaiDetailTableModel)tableZdy.getModel()).fireTableDataChanged();
		
	}
	private void initializeTDXBanKuaiLists() 
	{
		 HashMap<String,BanKuai> sysbankuailist = bkdbopt.getTDXBanKuaiList (); 
		
		((BanKuaiDetailTableModel)tableSysBk.getModel()).refresh(sysbankuailist);
		((BanKuaiDetailTableModel)tableSysBk.getModel()).fireTableDataChanged();
	}



	private void createEvents() 
	{
		
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				if(pnlGingo2.isXmlEdited()) {
					JOptionPane.showMessageDialog(null,"板块产业链编辑后尚未保存，请先保存");
					return;
				} else					
					dispose ();
			}
		});
		
	}

	private JButton okButton;
	private JTable tableSysBk;
	private final JPanel contentPanel = new JPanel();
	private Ginkgo2 pnlGingo2;
	private BanKuaiAndChanYeLian bkcylpnl;
	private JPanel panelSys;
	private JTable tableZdy;
	private JTable tablezhishu;
	
	private void initializeGui()
	{
		setTitle("\u901A\u8FBE\u4FE1\u677F\u5757/\u81EA\u5B9A\u4E49\u677F\u5757\u8BBE\u7F6E");
		setBounds(100, 100, 993, 980);
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
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JLabel lblNewLabel = new JLabel("\u901A\u8FBE\u4FE1\u4EA4\u6613\u6240\u6307\u6570");


		GroupLayout gl_panelSys = new GroupLayout(panelSys);
		gl_panelSys.setHorizontalGroup(
			gl_panelSys.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelSys.createSequentialGroup()
					.addGroup(gl_panelSys.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelSys.createSequentialGroup()
							.addGap(23)
							.addComponent(label))
						.addGroup(gl_panelSys.createSequentialGroup()
							.addContainerGap()
							.addComponent(scrollPanesysbk, GroupLayout.PREFERRED_SIZE, 208, GroupLayout.PREFERRED_SIZE)))
					.addGap(44)
					.addGroup(gl_panelSys.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 278, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel))
					.addPreferredGap(ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
					.addGroup(gl_panelSys.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 217, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_1))
					.addGap(75))
		);
		gl_panelSys.setVerticalGroup(
			gl_panelSys.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelSys.createSequentialGroup()
					.addContainerGap(46, Short.MAX_VALUE)
					.addGroup(gl_panelSys.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(label_1)
						.addComponent(lblNewLabel))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelSys.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 443, GroupLayout.PREFERRED_SIZE)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 441, GroupLayout.PREFERRED_SIZE)
						.addComponent(scrollPanesysbk, GroupLayout.PREFERRED_SIZE, 447, GroupLayout.PREFERRED_SIZE))
					.addGap(38))
		);
		
		BanKuaiDetailTableModel zhishubankmodel = new BanKuaiDetailTableModel(null);
		tablezhishu = new JTable(zhishubankmodel) {
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
		scrollPane_1.setViewportView(tablezhishu);
		
		BanKuaiDetailTableModel zidingyibankmodel = new BanKuaiDetailTableModel(null);
		tableZdy = new JTable(zidingyibankmodel){

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
		scrollPane.setViewportView(tableZdy);
		
		
		BanKuaiDetailTableModel sysaccountsmodel = new BanKuaiDetailTableModel(null);
		tableSysBk = new JTable(sysaccountsmodel){

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
		
		
		BanKuaiDetailTableModel zdyaccountsmodel = new BanKuaiDetailTableModel(null);
		
		//pnlGingo2 = new Ginkgo2(this.bkdbopt, this.stockdbopt,this.cylxmlhandler);
		bkcylpnl = new BanKuaiAndChanYeLian(this.stockInfoManager) ;
		
		//tabbedPane.addTab("\u4EA7\u4E1A\u94FE\u5B50\u7248\u5757\u5B9A\u4E49", null, pnlGingo2, null);
		tabbedPane.addTab("\u4EA7\u4E1A\u94FE\u5B50\u7248\u5757\u5B9A\u4E49", null, bkcylpnl, null);
		tabbedPane.setSelectedIndex(1) ;
		
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


class BanKuaiDetailTableModel extends AbstractTableModel 
{
	HashMap<String,BanKuai> bankuailist;

	List<BanKuai> valuesList; //存放板块对象列表
	String[] jtableTitleStrings = { "板块名称", "板块代码","创建时间"};
	
	BanKuaiDetailTableModel (HashMap<String,BanKuai> bankuailist)
	{
		this.bankuailist =  bankuailist;
		if(bankuailist != null) {
			 valuesList = new ArrayList<BanKuai>(bankuailist.values());
				
				Ordering<BanKuai> sortedCopy = Ordering.from(byBkName);
				 Collections.sort(valuesList,sortedCopy);
		}
			
	}

	public void refresh(HashMap<String,BanKuai> bankuailist)
	{
		this.bankuailist =  bankuailist;
		valuesList = new ArrayList<BanKuai>(bankuailist.values());
		Collator collator = Collator.getInstance(Locale.CHINESE);
		Ordering<BanKuai> sortedCopy = Ordering.from(byBkName);
		 Collections.sort(valuesList,sortedCopy);

	}

	Comparator<BanKuai> byBkName = new Comparator<BanKuai>() {
		Collator collator = Collator.getInstance(Locale.CHINESE);
		//Collections.sort(rightnamelist,collator);
	    public int compare(final BanKuai p1, final BanKuai p2) {
	        return p1.getBankuainame().compareTo(p2.getBankuainame()  );
	    }
	};

	 public int getRowCount() 
	 {
		 if(this.bankuailist != null)
			 return bankuailist.size();
		 else return 0;
		  
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    
	    } 
//	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(bankuailist == null)
	    		return null;
	    	Object value = "??";
	    	
	    	
	    	switch (columnIndex) {
            case 0:
                value = valuesList.get(rowIndex).getBankuainame();
            	//value = this.bankuailist.get(tmpc[rowIndex]).getBankuainame();
                break;
            case 1:
            	value = valuesList.get(rowIndex).getBankuaicode();
            	//value = this.bankuailist.get(tmpc[rowIndex]).getBankuaicode();
                break;
            case 2:
            	value = valuesList.get(rowIndex).getBankuaicreatedtime();
            	//value = this.bankuailist.get(tmpc[rowIndex]).getBankuaicreatedtime();
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
	    
//	    public AccountInfoBasic getAccountsAt(int row) {
//	        return accountslist.get(row);
//	    }
	    
	    public void deleteAllRows()
	    {
//	    	if(this.accountslist == null)
//				 return ;
//			 else 
//				 accountslist.clear();
	    }
	    
	    public BanKuai getBanKuai (int rowIndex)
	    {
	    	return (BanKuai) bankuailist.get(rowIndex);
	    }
	

	    
}
