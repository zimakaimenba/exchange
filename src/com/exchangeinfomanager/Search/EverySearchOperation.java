package com.exchangeinfomanager.Search;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;


import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountXinYongPuTong;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.toedter.calendar.JDateChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.awt.event.ItemListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class EverySearchOperation extends JPanel 
{
	private ConnectDataBase connectdb;
	private SystemConfigration sysconfig ;
	
	private JTable tblSearchResult;
	//private String[] jtableTitleStrings = { "股票代码", "股票名称", "查询字段", "查询结果" };
	private JComboBox<String> cbxSearchAction;
	private JDateChooser dchsSearchdate;
	private JButton btnSearch;
	private JButton btnSaveBankuai;
	private JScrollPane scrollPane;
	//private SimpleDateFormat formatterhwy;
	private StockInfoManager stockinfomanager;
	
	
	/**
	 * Create the panel.
	 */
	public EverySearchOperation(StockInfoManager stockinfomanager) 
	{
		this.stockinfomanager = stockinfomanager;
		this.initlizeGUI ();
		this.initializeDB ();
		this.initializeSysConfig ();
		//this.initializeAccountConfig ();
		this.createEvents();
	}
	
//	private void initializeAccountConfig() 
//	{
//		//accountconfig = AccountConfiguration.getInstance();
//	}

	private void initializeDB() {

		connectdb = ConnectDataBase.getInstance();
				
//				if(connectdb.isDatabaseconnected())
//					lblStatusBarDbstatusindictor.setText("数据库已连接.");
//				else 
//				{
//					lblStatusBarDbstatusindictor.setText("注意：数据库连接失败！");
//					JOptionPane.showMessageDialog(null,"数据库连接失败！");
//				}
	}

	private void initlizeGUI ()
	{
		setLayout(null);
		
		btnSearch = new JButton("\u67E5\u627E");
		btnSearch.setBounds(10, 10, 67, 23);
		add(btnSearch);
		
		cbxSearchAction = new JComboBox();
		cbxSearchAction.setModel(new DefaultComboBoxModel(new String[] {" ", "\u4E70\u5356\u8BB0\u5F55", "\u5173\u6CE8\u8BB0\u5F55", "\u5F53\u524D\u6301\u4ED3"}));
		
		cbxSearchAction.setEditable(true);
		cbxSearchAction.setBounds(87, 10, 170, 23);
		add(cbxSearchAction);
		
		dchsSearchdate = new JDateChooser(new Date());
		dchsSearchdate.setEnabled(false);
		dchsSearchdate.getCalendarButton().setEnabled(false);
		dchsSearchdate.setBounds(267, 10, 95, 23);
		add(dchsSearchdate);
		
		btnSaveBankuai = new JButton("\u5B58\u4E3A\u677F\u5757");
		btnSaveBankuai.setEnabled(false);
		btnSaveBankuai.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				System.out.println("it works");
			}
		});
		btnSaveBankuai.setBounds(372, 10, 93, 23);
		add(btnSaveBankuai);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 52, 455, 324);
		add(scrollPane);
		
		tblSearchResult = new JTable(){
			    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
		        Component comp = super.prepareRenderer(renderer, row, col);
		        
		        	Object value = getModel().getValueAt(row, col);
		        	if ("买入".equals(value.toString().trim())) { 
		                comp.setBackground(Color.red);
		            } else if ("卖出".equals(value.toString().trim())) {
		                comp.setBackground(Color.green);
		            } else if("挂单买入".equals(value.toString()) || "挂单卖出".equals(value.toString())  ) {
		            	comp.setBackground(Color.yellow);
		            } else if ("加入".equals(value.toString().trim())) {
		                comp.setForeground(Color.blue);
//		            } else if (stockinfomanager.getcBxstockcode().getSelectedItem().toString().substring(0, 6).equals(value.toString().trim())) {
//		            	comp.setBackground(Color.orange);
		            } else  {
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
		
//		private String[] jtableTitleStrings = { "股票代码", "股票名称", "查询字段", "查询结果" };
//		tblSearchResult.setModel(new DefaultTableModel(
//				new Object[][] {
//				},
//				jtableTitleStrings) {
//				
//				private static final long serialVersionUID = 1L;
//				public boolean isCellEditable(int row,int column) {
//					return false;
//				}
//		});
//		tblSearchResult.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(6);
//		tblSearchResult.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(6);
//		tblSearchResult.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(11);
//		tblSearchResult.setRowSelectionAllowed(true);
//		tblSearchResult.setColumnSelectionAllowed(false);
//		tblSearchResult.setCellSelectionEnabled(false);
//		tblSearchResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(tblSearchResult);
	}
	
	private void initializeSysConfig()
	{
		sysconfig = SystemConfigration.getInstance();
		//formatterhwy= sysconfig.getFormatDate();
	}
	
	private void createEvents () 
	{
		tblSearchResult.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				 if (arg0.getClickCount() == 2) {
					 int  view_row = tblSearchResult.rowAtPoint(arg0.getPoint()); //获得视图中的行索引
					 int  view_col = tblSearchResult.columnAtPoint(arg0.getPoint()); //获得视图中的列索引
					 int  model_row = tblSearchResult.convertRowIndexToModel(view_row);//将视图中的行索引转化为数据模型中的行索引
					 int  model_col = tblSearchResult.convertColumnIndexToModel(view_col);//将视图中的列索引转化为数据模型中的列索引
					 
					 
					 int row = tblSearchResult.getSelectedRow();
					 //int column = tblSearchResult.getSelectedColumn();
					 //String stockcode = tblSearchResult.getModel().getValueAt(row, 0).toString().trim();
					 String stockcode = tblSearchResult.getModel().getValueAt(model_row, 0).toString().trim();
					 System.out.println(stockcode);
					 stockinfomanager.getcBxstockcode().setSelectedItem(stockcode);
					 stockinfomanager.preUpdateSearchResultToGui(stockcode);
				 }
                      
			}
			
		});
		
		
		dchsSearchdate.getDateEditor().addPropertyChangeListener(new PropertyChangeListener() {
			        @Override
			        public void propertyChange(PropertyChangeEvent e) {
			            if ("date".equals(e.getPropertyName())) {
			                System.out.println(e.getPropertyName()
			                    + ": " + (Date) e.getNewValue());
			                preUpdateSearchresultToGui ();
			            }
			        }
			    });
		
		cbxSearchAction.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) 
			{
				if(e.getStateChange() == ItemEvent.SELECTED) {
					preUpdateSearchresultToGui ();
				}
				if(e.getStateChange() == ItemEvent.DESELECTED) {
				}
			}
		});
	}
	
	protected void preUpdateSearchresultToGui() 
	{
		// TODO Auto-generated method stub
		((DefaultTableModel)tblSearchResult.getModel()).setRowCount(0);
		try	{
			 String currentsearchaction = ((String)cbxSearchAction.getSelectedItem()).trim();
			if( "买卖记录".equals(currentsearchaction) ) {
				dchsSearchdate.setEnabled(true);
				updateMaimaiActionSearchResultToGui (currentsearchaction);
			} else if("挂单记录".equals(currentsearchaction) ) {
				dchsSearchdate.setEnabled(true);
				updateGuadanActionSearchResultToGui (currentsearchaction);
			} else if("关注记录".equals(currentsearchaction) ) {
				dchsSearchdate.setEnabled(false);
				updateGuanzhuActionSearchResultToGui (currentsearchaction);
			} else if("当前持仓".equals(currentsearchaction) ) {
				dchsSearchdate.setEnabled(false);
				updateChicangActionSearchResultToGui (currentsearchaction);
			} else {
				dchsSearchdate.setEnabled(false);
				updateInfoSearchResultToGui (currentsearchaction);
				updateStockCombox();
			}
		} catch(java.lang.NullPointerException ex)	{
			ex.printStackTrace();
		} catch(java.lang.StringIndexOutOfBoundsException ex2) {
			ex2.printStackTrace();
		}
	}

	/*
	 * 当前持仓信息
	 */
	protected boolean updateChicangActionSearchResultToGui(String searchaction) 
	{
		// TODO Auto-generated method stub
		String searchsqlstat = "SELECT czjl.买卖账号, czjl.股票代码, agu.股票名称,  SUM(买卖金额),SUM(买卖股数)" 
								+ "FROM 操作记录买卖 AS czjl, A股 AS agu"
								+ "WHERE  czjl.持仓标志=true AND czjl.股票代码  = agu.股票代码"
								+ " GROUP BY  czjl.买卖账号, czjl.股票代码, agu.股票名称"
								; 
		System.out.println(searchsqlstat);
		
		ResultSet rsquery = connectdb.sqlQueryStatExecute(searchsqlstat);
		Object[][] tabledata = null;  
		
	    try {     
	    	rsquery.last();  
	        int rows = rsquery.getRow();  
 	        tabledata = new Object[rows][];    
	        //ResultSetMetaData md = rsquery.getMetaData();//获取记录集的元数据  
	        int columnCount = 5;//列数
	        //int finialrows = 0;
	        rsquery.first(); 
	        for(int i=0;i<rows;i++) {
	        	Object[] row = new Object[columnCount];
	        	row[0] = rsquery.getObject("股票代码").toString().trim();
	        	row[1] = "";
	        	row[2] = rsquery.getObject("买卖账号").toString().trim();
	        	NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();  
	        	row[3] = currencyFormat.format( rsquery.getDouble(4) ); 
	            row[4] = rsquery.getInt(3);
	            tabledata[i] = row;
	            rsquery.next();
	            
	       }
	       rsquery.close();
	    }catch(java.lang.NullPointerException e){ 
	    
	    }catch(Exception e){
	    	e.printStackTrace();
	    	return false;
	    }  
	    
	    String[] jtableChicangTitleStrings = { "持仓账户", "股票代码", "股票名称","持仓成本", "持仓股数"  };
		tblSearchResult.setModel(new DefaultTableModel(	new Object[][] { },	jtableChicangTitleStrings) 	{
				private static final long serialVersionUID = 1L;
				public boolean isCellEditable(int row,int column) 
				{
					return false;
				}
				
				public Class getColumnClass(int column) 
				{  
			        //Class returnValue;  
			        switch (column) {
//			        case 1:
//                        return String.class;
//			        case 2:
//                        return String.class;
//                    case 3:
//                        return String.class;
                    case 4:
                        return Integer.class;
                    default:
                        return String.class;
			        }  
			    }
		});
		tblSearchResult.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(4);
		tblSearchResult.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(4);
		tblSearchResult.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(10);
		tblSearchResult.setRowSelectionAllowed(true);
		tblSearchResult.setColumnSelectionAllowed(false);
		tblSearchResult.setCellSelectionEnabled(false);
		tblSearchResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		RowSorter sorter = new TableRowSorter(tblSearchResult.getModel());  
		tblSearchResult.setRowSorter(sorter); 
	    try {
	    	for(int i=0;i<tabledata.length;i++) {
				((DefaultTableModel)tblSearchResult.getModel()).addRow(tabledata[i]);
			}
	    } catch(java.lang.NullPointerException e) {
	    	e.printStackTrace();
	    }

	    return true;
		
	}

	
	
	protected boolean updateGuanzhuActionSearchResultToGui(String searchaction) 
	{
		// TODO Auto-generated method stub
//		Date tmpdate = dchsSearchdate.getDate();
//		String searchdate = sysconfig.getFormatDate().format(tmpdate);
//		String searchstringformated = "'*" + searchdate  + "*'";
		
		String searchsqlstat = "SELECT * FROM 操作记录重点关注  WHERE 加入移出标志=" + true	+ " " + "ORDER BY 日期 DESC" ; 
		System.out.println(searchsqlstat);
		
		ResultSet rs = connectdb.sqlQueryStatExecute(searchsqlstat);
		Object[][] tabledata = null;  
		
	    try {     
	        rs.last();  
	        int rows = rs.getRow();  
 	        tabledata = new Object[rows][];    
	        ResultSetMetaData md = rs.getMetaData();//获取记录集的元数据  
	        int columnCount = 4;//列数
	        int finialrows = 0;
	        rs.first();  
	        //while(rs.next())
	        for(int j=0;j<rows;j++)
	        {  
	            Object[] row = new Object[columnCount]; 
	            row[0] = rs.getObject("股票代码").toString();
	        	row[1] = " ";
	        	row[2] = rs.getString("日期");
	        	row[3] = rs.getObject("原因描述").toString();
              
	            tabledata[finialrows] = row;  
	            //data2.add(row);
	            finialrows++; 
	            rs.next();
	        }
	        rs.close();
	    }catch(java.lang.NullPointerException e){ 
	    
	    }catch(Exception e){
	    	e.printStackTrace();
	    	return false;
	    }  
	    
	    String[] jtableChicangTitleStrings = { "股票代码", "股票名称", "关注日期","原因描述", };
		tblSearchResult.setModel(new DefaultTableModel(
				new Object[][] {
				},
				jtableChicangTitleStrings) {
				
				private static final long serialVersionUID = 1L;
				public boolean isCellEditable(int row,int column) {
					return false;
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
		tblSearchResult.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(4);
		tblSearchResult.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(4);
		tblSearchResult.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(10);
		tblSearchResult.setRowSelectionAllowed(true);
		tblSearchResult.setColumnSelectionAllowed(false);
		tblSearchResult.setCellSelectionEnabled(false);
		tblSearchResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		RowSorter sorter = new TableRowSorter(tblSearchResult.getModel());  
		tblSearchResult.setRowSorter(sorter);
	    try {
	    	for(int i=0;i<tabledata.length;i++) {
				((DefaultTableModel)tblSearchResult.getModel()).addRow(tabledata[i]);
			}
	    } catch(java.lang.NullPointerException e) {
	    	e.printStackTrace();
	    }

	    return true;
	}

	protected boolean updateGuadanActionSearchResultToGui(String searchaction)
	{
		// TODO Auto-generated method stub
		Date currentdate = dchsSearchdate.getDate();
		String searchdate = CommonUtility.formatDateYYYY_MM_DD_HHMMSS(currentdate);
		String searchstringformated = "'*" + searchdate  + "*'";
		
		String searchsqlstat = "SELECT * FROM 操作记录挂单  WHERE 日期  LIKE " + searchstringformated + "" + "ORDER BY 股票代码" ; 
		System.out.println(searchsqlstat);
		
		ResultSet rs = connectdb.sqlQueryStatExecute(searchsqlstat);
		Object[][] tabledata = null;  
		
	    try 
	    {     
	        rs.last();  
	        int rows = rs.getRow();  
 	        tabledata = new Object[rows][];    
	        //ResultSetMetaData md = rs.getMetaData();//获取记录集的元数据  
	        int columnCount = 4;//列数
	        int finialrows = 0;
	        rs.first();  
	        //while(rs.next())
	        for(int j=0;j<rows;j++)
	        {  
	            Object[] row = new Object[columnCount]; 
	            row[0] = rs.getObject("股票代码").toString();
	        	row[1] = " ";
	        	if(rs.getBoolean("买入卖出标志"))
	        		row[2] = "挂单买入";
	        	else row[2] = "挂单卖出";
	        	row[3] = rs.getObject("原因描述").toString();
              
	            tabledata[finialrows] = row;  
	            //data2.add(row);
	            finialrows++; 
	            rs.next();
	        }
	        rs.close();
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    	return false;
	    }catch(Exception e){
	    	e.printStackTrace();
	    	return false;
	    }  
	    
	    String[] jtableChicangTitleStrings = { "股票代码", "股票名称", "买入卖出","原因描述", };
		tblSearchResult.setModel(new DefaultTableModel(
				new Object[][] {
				},
				jtableChicangTitleStrings) {
				
				private static final long serialVersionUID = 1L;
				public boolean isCellEditable(int row,int column) {
					return false;
				}
		});
		tblSearchResult.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(4);
		tblSearchResult.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(4);
		tblSearchResult.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(10);
		tblSearchResult.setRowSelectionAllowed(true);
		tblSearchResult.setColumnSelectionAllowed(false);
		tblSearchResult.setCellSelectionEnabled(false);
		tblSearchResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		RowSorter sorter = new TableRowSorter(tblSearchResult.getModel());  
		tblSearchResult.setRowSorter(sorter);
	    for(int i=0;i<tabledata.length;i++) {
			((DefaultTableModel)tblSearchResult.getModel()).addRow(tabledata[i]);
		}

	    return true;
	}

	private boolean updateMaimaiActionSearchResultToGui(String action) 
	{
		// TODO Auto-generated method stub
		Date currentdate = dchsSearchdate.getDate();
		String searchdate =CommonUtility.formatDateYYYY_MM_DD_HHMMSS(currentdate);
		String searchstringformated = "'*" + searchdate  + "*'";
		
		
		String searchsqlstat = "SELECT * FROM 操作记录买卖  WHERE 日期  LIKE " + searchstringformated + "" + "ORDER BY 股票代码" ; 
		System.out.println(searchsqlstat);
		
		ResultSet rs = connectdb.sqlQueryStatExecute(searchsqlstat);
		Object[][] tabledata = null;  
		
	    try 
	    {     
	        rs.last();  
	        int rows = rs.getRow();  
 	        tabledata = new Object[rows][];    
	        ResultSetMetaData md = rs.getMetaData();//获取记录集的元数据  
	        int columnCount = 4;//列数
	        int finialrows = 0;
	        rs.first();  
	        //while(rs.next())
	        for(int j=0;j<rows;j++)
	        {  
	            Object[] row = new Object[columnCount]; 
	            row[0] = rs.getObject("股票代码").toString();
	        	row[1] = " ";
	        	if(rs.getBoolean("买入卖出标志"))
	        		row[2] = "买入";
	        	else row[2] = "卖出";
	        	row[3] = rs.getObject("原因描述").toString();
              
	            tabledata[finialrows] = row;  
	            //data2.add(row);
	            finialrows++; 
	            rs.next();
	        }
	        rs.close();
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    	return false;
	    }catch(Exception e){
	    	e.printStackTrace();
	    	return false;
	    }
	    
	    String[] jtableChicangTitleStrings = { "股票代码", "股票名称", "买入卖出","原因描述", };
		tblSearchResult.setModel(new DefaultTableModel(
				new Object[][] {
				},
				jtableChicangTitleStrings) {
				
				private static final long serialVersionUID = 1L;
				public boolean isCellEditable(int row,int column) {
					return false;
				}
		});
		tblSearchResult.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(4);
		tblSearchResult.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(4);
		tblSearchResult.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(10);
		tblSearchResult.setRowSelectionAllowed(true);
		tblSearchResult.setColumnSelectionAllowed(false);
		tblSearchResult.setCellSelectionEnabled(false);
		tblSearchResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		RowSorter sorter = new TableRowSorter(tblSearchResult.getModel());  
		tblSearchResult.setRowSorter(sorter);
	    for(int i=0;i<tabledata.length;i++) {
			((DefaultTableModel)tblSearchResult.getModel()).addRow(tabledata[i]);
		}

	    return true;
		
	}
	
	private boolean updateInfoSearchResultToGui(String searchstring) 
	{
		// TODO Auto-generated method stub
		if(searchstring.trim().isEmpty())
			return false
					;
		String searchstringformated = "'*" + searchstring  + "*'";
		String searchsqlstat = "SELECT * FROM A股  WHERE 概念板块提醒 LIKE " + searchstringformated + " OR 负面消息 LIKE " + searchstringformated + " " + "ORDER BY 股票代码" ; 
		System.out.println(searchsqlstat);
		ResultSet rs = connectdb.sqlQueryStatExecute(searchsqlstat);
		
		Object[][] data = null;
		int finalrows = 0;
	    try {     
	        rs.last();  
	        int rows = rs.getRow();  
	        data = new Object[2*rows][4];  //因为2个字段可能同时含有查找字符串，所以先把数组行设置大一点   
	        //ResultSetMetaData md = rs.getMetaData();//获取记录集的元数据  
	        int columnCount = 4;//列数  
	        rs.first();  
	         
	        //while(rs.next())
	        for(int j=0;j<rows;j++) {  
	            try {
	            	if( rs.getObject("概念板块提醒").toString().trim().contains(searchstring) ) {
	            		Object[] row = new Object[columnCount];
	    	            
	            		row[0] = rs.getObject("股票代码").toString();
	            		row[1] = rs.getObject("股票名称").toString();
	            		row[2] = "概念板块提醒";
	            		row[3] = rs.getObject("概念板块提醒").toString();
	            		data[finalrows] = row;
	            		finalrows++;
	            	}
	            }catch(java.lang.NullPointerException e){ 
	    	    	//e.printStackTrace();
	    	    } 
	            
	            try{
	            	if( rs.getObject("负面消息").toString().trim().contains(searchstring) ) {
	            		Object[] row = new Object[columnCount];
	    	            
	    	            row[0] = rs.getObject("股票名称").toString();
	    	            row[1] = rs.getObject("股票代码").toString();
	            		row[2] = "负面消息";
	            		row[3] = rs.getObject("负面消息").toString();
	            		data[finalrows] = row;
	            		finalrows++;
	            	}
	            }catch(java.lang.NullPointerException e){ 
	    	    	//e.printStackTrace();
	    	    }

	            rs.next();
	        }
	        rs.close();
	        
	        String[] jtableChicangTitleStrings = { "股票代码", "股票名称", "查询字段","查询结果", };
			tblSearchResult.setModel(new DefaultTableModel(
					new Object[][] {
					},
					jtableChicangTitleStrings) {
					
					private static final long serialVersionUID = 1L;
					public boolean isCellEditable(int row,int column) {
						return false;
					}
//					public Class getColumnClass(int column) {  
//				        Class returnValue;  
//				        if ((column >= 0) && (column < getColumnCount())) {  
//				            returnValue = getValueAt(0, column).getClass();  
//				        } else {  
//				            returnValue = Object.class;  
//				        }  
//				        return returnValue;  
//				    }  
			});
			tblSearchResult.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(4);
			tblSearchResult.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(4);
			tblSearchResult.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(10);
			tblSearchResult.setRowSelectionAllowed(true);
			tblSearchResult.setColumnSelectionAllowed(false);
			tblSearchResult.setCellSelectionEnabled(false);
			tblSearchResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			RowSorter sorter = new TableRowSorter(tblSearchResult.getModel());  
			tblSearchResult.setRowSorter(sorter);
		    for(int i = 0; i < finalrows; i++){
				((DefaultTableModel)tblSearchResult.getModel()).addRow(data[i]);
		       }

		    if(rows>0)
		    	return true;
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
	    
	 return false;
	}
	
	private void updateStockCombox() 
	{
		boolean isaddItem=true;
		String tmp;

		tmp = (String)cbxSearchAction.getSelectedItem().toString().trim();//有可能是原来输入过的，要把代码选择出来。
	   	  //判断用户所输入的项目是否有重复，若有重复则不增加到JComboBox中。
	   	  try{
	   			  for(int i=0;i< cbxSearchAction.getItemCount();i++) {
		   	  	  	  if (cbxSearchAction.getItemAt(i).toString().trim().equals(tmp)){
		   	  	  	  	 isaddItem=false;
		   	  	  	  	 break;
		   	  	  	  }
		   	  	  }
	   	  	  
	   	  	  if (isaddItem){
	   	  		cbxSearchAction.insertItemAt(tmp,0);//插入项目tmp到0索引位置(第一列中).
	   	  	  }
	   	  }catch(NumberFormatException ne){
	   		
	   	  }
	}
	
	/**
	 * @return the tblSearchResult
	 */
	public JTable getTblSearchResult() {
		return tblSearchResult;
	}

	/**
	 * @return the cbxSearchAction
	 */
	public JComboBox getCbxSearchAction() {
		return cbxSearchAction;
	}

	/**
	 * @return the dchsSearchdate
	 */
	public JDateChooser getDchsSearchdate() {
		return dchsSearchdate;
	}
	
//END	
}


class AccountsChiCangTableModel extends AbstractTableModel 
{
	private  ArrayList<StockChiCangInfo> accountslist;
	String[] jtableTitleStrings = { "股票名称", "股票代码", "持仓股数","持仓成本"};
	
	AccountsChiCangTableModel (ArrayList<StockChiCangInfo> accountslist)
	{
		//super(tabledata,jtableTitleStrings);
		this.accountslist = accountslist;
		//this.jtableTitleStrings = jtableTitleStrings; 
	}
	public void refresh  (ArrayList<StockChiCangInfo> accountslist)
	{
		this.accountslist = accountslist;
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
	    	StockChiCangInfo chicanggupiao = (StockChiCangInfo)accountslist.get(rowIndex);
	    	
	    	switch (columnIndex) {
            case 0:
                value = chicanggupiao.getChicangname();
                break;
            case 1:
                value = chicanggupiao.getChicangcode();
                break;
            case 2:
                value = chicanggupiao.getChicanggushu();
                break;
            case 3:
                value = chicanggupiao.getChicangchenben();
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
			          clazz = Integer.class;
			          break;
		        case 4:
			          clazz = Double.class;
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
	    
	    public StockChiCangInfo getAccountsAt(int row) {
	        return accountslist.get(row);
	    }
	    
}