package com.exchangeinfomanager.bankuaifengxi.ai;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.CashAccountBasic;
import com.exchangeinfomanager.asinglestockinfo.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjeZhanbiPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartMultiCjeZhanbiPnl;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.github.cjwizard.WizardPage;
import com.google.common.base.Strings;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JEditorPane;
import net.miginfocom.swing.MigLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.swing.JTextArea;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/*
 * 用于大盘每周分析
 */
public class DaPanWeeklyFengXi extends WizardPage 
{
	/**
	 * Create the dialog.
	 */
	public DaPanWeeklyFengXi(String title, String description, LocalDate selectdate) 
	{
		super(title,description);
		
		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
		this.bkcyl = BanKuaiAndChanYeLian2.getInstance();
		this.selectdate = selectdate;
		
		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
		zhishulist = new ArrayList<BkChanYeLianTreeNode>();
		
		createGui ();
		datachooser.setLocalDate(selectdate);
		
		createEvents ();
		
		showZhiShuAnalysisXmlResults ();
		showZhiShuCjeZhanBiBarChart ();
	}
	private SystemConfigration sysconfig;
	private BanKuaiDbOperation bkdbopt;
	private AllCurrentTdxBKAndStoksTree allbksks;
	private BanKuaiAndChanYeLian2 bkcyl;
	private DaPanWeeklyFengXiXmlHandler dpfxmlhandler; 
	private LocalDate selectdate;
	private List<BkChanYeLianTreeNode> zhishulist;
	private boolean infochanged = false;
	private ZhongDianGuanZhu zdgzinfo;

	/*
	 * 
	 */
	private void showZhiShuCjeZhanBiBarChart() 
	{
		if(zhishulist == null)
			return;
		
		LocalDate startdate = selectdate.minus(10,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
		LocalDate enddate = selectdate.with(DayOfWeek.SATURDAY);
		panel.updatedMultiDate(zhishulist, startdate, enddate, StockGivenPeriodDataItem.WEEK);
		
	}
	/*
	 * 
	 */
	private void showZhiShuAnalysisXmlResults() 
	{
		this.dpfxmlhandler = new  DaPanWeeklyFengXiXmlHandler("000000",selectdate);
		zdgzinfo = this.dpfxmlhandler.getZhongDianGuanZhu();

		ArrayList<ZdgzItem> govpolicy = zdgzinfo.getGovpolicy();
		((PolicyTableModel)tablepolicy.getModel()).refresh(govpolicy);
		
		ArrayList<ZdgzItem> dapan = zdgzinfo.getDapanZhiShuLists();
		for(ZdgzItem zhishu : dapan) {
			String zhishucode = zhishu.getValue();
			BanKuai zhishubankuai = allbksks.getBanKuai(zhishucode, selectdate, StockGivenPeriodDataItem.WEEK);
			zhishulist.add(zhishubankuai);
		}
		((DaPanTableModel)tablebk.getModel()).refresh(zhishulist,this.selectdate);
		
		txaComments.setText(zdgzinfo.getDaPanAnalysisComments() );
	}
	/*
	 * 
	 */
	public void saveFenXiResult ()
	{
		if(infochanged) {
			zdgzinfo.setDaPanAnalysisComments(txaComments.getText());
			
			dpfxmlhandler.saveFenXiXml (zdgzinfo);
		}
	}
	/*
	 * 
	 */
	private void createEvents() 
	{
		btnxmlMartrix.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconfig.getDaPanFengXiWeeklyXmlMatrixFile ();
					Process p  = Runtime.getRuntime().exec(cmd);
					p.waitFor();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
				    e1.printStackTrace();
				}
			}
		});
		
		tablepolicy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tablepolicy.getSelectedRow();
				int column = tablepolicy.getSelectedColumn();
				if(row != -1) {
					if(column == 1) {
						((PolicyTableModel)tablepolicy.getModel()).isPolicyZdgzItemSelected(row);
						infochanged = true;
					}
					
				}
			}
		});
		
		btnremv.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int row = tablebk.getSelectedRow();
				if(row != -1) {
					((DaPanTableModel)tablebk.getModel()).deleteZhiShu(row);
					infochanged = true;
				} else 
					JOptionPane.showMessageDialog(null,"请选择需要删除的指数！");
			}
		});
		
		btnadd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String zhishucode = tfldbkcode.getText();
				if( Pattern.matches("\\d{6}$",zhishucode)   ) {
					BanKuai bknode = (BanKuai)allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(zhishucode, BanKuaiAndStockBasic.TDXBK);
					if(bknode !=null)
						((DaPanTableModel)tablebk.getModel()).addNewZhiShu(bknode);
					else {
						JOptionPane.showMessageDialog(null,"未能找到该指数，可能代码有误，重新输入！");
						return ;
					}
				} else {
					JOptionPane.showMessageDialog(null,"指数代码有误，重新输入！");
					return ;
				}
			}
		});
		
		txaComments.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				infochanged = true;
			}
		});
		
		btnremovepolicy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				int row = tablepolicy.getSelectedRow();
				if(row != -1) {
					((PolicyTableModel)tablepolicy.getModel()).deleteZdgzItem(row);
					infochanged = true;
				}
			}
		});
		
		btnaddchecklists.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				ZdgzItem zdgzitem = new ZdgzItem ("GP" + String.valueOf(zdgzinfo.getGovpolicy().size()+1));
				int exchangeresult = JOptionPane.showConfirmDialog(null, zdgzitem, "增加CheckList", JOptionPane.OK_CANCEL_OPTION);
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;
				
				((PolicyTableModel)tablepolicy.getModel()).addNewItem(zdgzitem);
				infochanged = true;
			}
		});
		

	}
	/*
	 * 
	 */
	private void addNewNode(String stockcode) 
	{
		
	}

	private final JPanel contentPanel = new JPanel();
	private JTextField tfldbkcode;
	private JButton btnadd;
	private JLocalDateChooser datachooser;
	private JTable tablebk;
	private JTable tablepolicy;
	private JButton btnNews;
	private JTextArea txaComments;
	private BanKuaiFengXiCategoryBarChartMultiCjeZhanbiPnl panel;
	private JButton btnaddchecklists;
	private JButton btnremovepolicy;
	private JButton btnremv;
	private JButton btnxmlMartrix;
	
	private void createGui ()
	{
		setBounds(100, 100, 1292, 721);
		setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPanel, BorderLayout.CENTER);
		
		panel = new BanKuaiFengXiCategoryBarChartMultiCjeZhanbiPnl();
		
		JScrollPane scrollPane = new JScrollPane();
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JEditorPane editorPane = new JEditorPane();
		scrollPane_1.setViewportView(editorPane);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		
		JScrollPane scrollPane_3 = new JScrollPane();
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 499, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGap(5)
									.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(scrollPane_3, GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 201, GroupLayout.PREFERRED_SIZE)))
					.addGap(20))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 317, GroupLayout.PREFERRED_SIZE)
					.addGap(16)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(scrollPane_3, GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE))
						.addComponent(scrollPane_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		txaComments = new JTextArea();
		
		scrollPane_3.setViewportView(txaComments);
		
		
		PolicyTableModel tablepolicymodel = new PolicyTableModel ();
		tablepolicy = new JTable(tablepolicymodel) {
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) 
			{
				 
		        Component comp = super.prepareRenderer(renderer, row, col);
		        PolicyTableModel tablemodel = (PolicyTableModel)this.getModel(); 
		        if(tablemodel.getRowCount() == 0) {
		        	return null;
		        }
		        
		        int modelRow = convertRowIndexToModel(row);
		        ZdgzItem zdgzitem = tablemodel.getPolicyZdgzItem(modelRow);
		        
		        Color foreground = Color.black, background = Color.white;
		        if(zdgzitem.getSelectedcolor() != null) {
		        	Font defaultFont = this.getFont();
		        	
		        	Font font=new Font(defaultFont.getName(),Font.ITALIC,defaultFont.getSize());
		        	comp.setFont(font);
		        	
			        //为不同情况突出显示不同的颜色
			         background = Color.GREEN;
			        
		        } else {
		        	background = Color.WHITE;
		        }

		        if (!this.isRowSelected(row)) 
			    	comp.setBackground(background);
		        
		        return comp;
			}
			
			public String getToolTipText(MouseEvent e) 
			{
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
//                	e1.printStackTrace();
                }
                return tip;
            } 
		};
		
		scrollPane_2.setViewportView(tablepolicy);
		
		DaPanTableModel dpmodel = new DaPanTableModel ();
		tablebk = new JTable(dpmodel) {
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				 
		        Component comp = super.prepareRenderer(renderer, row, col);
		        DaPanTableModel tablemodel = (DaPanTableModel)this.getModel(); 
		        if(tablemodel.getRowCount() == 0) {
		        	return null;
		        }
		        
		        //更改显示
		        if (comp instanceof JLabel && (col == 3)) {
	            	String value =  ((JLabel)comp).getText();
	            	if(value == null || value.length() == 0)
	            		return null;
	            	
	            	String valuepect = null;
	            	try {
	            		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(value).doubleValue();
	            		 
	            		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
	 	    	    	 percentFormat.setMinimumFractionDigits(1);
		            	 valuepect = percentFormat.format (formatevalue );
	            	} catch (java.lang.NumberFormatException e)   	{
	            		e.printStackTrace();
	            	} catch (ParseException e) {
						e.printStackTrace();
					}
	            	((JLabel)comp).setText(valuepect);
		        }
		        return comp;
			}	
		};
		scrollPane.setViewportView(tablebk);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			add(buttonPane, BorderLayout.SOUTH);
			
			tfldbkcode = new JTextField();
			tfldbkcode.setColumns(10);
//				getRootPane().setDefaultButton(okButton);
			
			
			datachooser = new JLocalDateChooser ();
			datachooser.setEnabled(false);
			
			btnadd = new JButton("\u52A0\u5165\u6307\u6570");
			
			
			btnremv = new JButton("\u5220\u9664\u6307\u6570");
			
			btnaddchecklists = new JButton("\u6DFB\u52A0\u653F\u7B56Checklists");

			btnremovepolicy = new JButton("\u5220\u9664");
			
			
			btnNews = new JButton("\u5927\u76D8\u76F8\u5173\u65B0\u95FB");
			
			btnxmlMartrix = new JButton("\u6253\u5F00Xml Martrix");
			
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(21)
						.addComponent(btnaddchecklists)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnremovepolicy)
						.addGap(300)
						.addComponent(tfldbkcode, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnadd)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnremv, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
						.addGap(99)
						.addComponent(btnxmlMartrix)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnNews)
						.addGap(18)
						.addComponent(datachooser, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
						.addGap(27))
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_buttonPane.createParallelGroup(Alignment.TRAILING)
							.addComponent(datachooser, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnaddchecklists, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnremovepolicy, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
								.addComponent(tfldbkcode, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnadd, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnremv, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnxmlMartrix, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnNews, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)))
						.addGap(11))
			);
			buttonPane.setLayout(gl_buttonPane);
		}
	}
}


class DaPanTableModel extends AbstractTableModel 
{
	private List<BkChanYeLianTreeNode> dapan;
	private String[] jtableTitleStrings = { "指数代码", "指数名称", "成交额","成交额占比"};
	private LocalDate showdate;
	
	DaPanTableModel ()
	{
	}
	
	public void refresh  (List<BkChanYeLianTreeNode> zhishulist, LocalDate showdate2)
	{
		this.dapan = zhishulist;
		this.showdate = showdate2;
		this.fireTableDataChanged();
	}
	public void addNewZhiShu (BanKuai zhishu)
	{
		dapan.add(zhishu);
		this.fireTableDataChanged();
	}
	public void deleteZhiShu (int row)
	{
		this.dapan.remove(row);
		this.fireTableDataChanged();
	}

	 public int getRowCount() 
	 {
		 if(this.dapan == null)
	        return 0;
		 else
			 return this.dapan.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	Object value = "??";
	    	BanKuai dapanitem = (BanKuai)this.dapan.get(rowIndex);

	    	switch (columnIndex) {
            case 0:
            	String zhishucode = dapanitem.getMyOwnCode();
                value = zhishucode;
                break;
            case 1:
            	String zhishuname = dapanitem.getMyOwnName();
                value = zhishuname;
                break;
            case 2:
            	NodeXPeriodDataBasic zhishunodexdate = dapanitem.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
                value = zhishunodexdate.getChengJiaoEr(this.showdate, 0);
                break;
            case 3:
            	zhishunodexdate = dapanitem.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
                value = zhishunodexdate.getChenJiaoErZhanBi(showdate, 0);
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
		      }
		      
		      return clazz;
		}
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 

	    public boolean isCellEditable(int row,int column)
	    {
			return false;
		}
}

class PolicyTableModel extends AbstractTableModel 
{
	String[] jtableTitleStrings = { "ID","选中", "政策面"};
	private ArrayList<ZdgzItem> govpolicy;
	
	PolicyTableModel ()
	{
	}
	public void refresh  (ArrayList<ZdgzItem> govpolicy )
	{
		this.govpolicy = govpolicy;
		this.fireTableDataChanged();
	}
	public void addNewItem(ZdgzItem zdgzitem) 
	{
		this.govpolicy.add(zdgzitem);
		this.fireTableDataChanged();
	}
	public ZdgzItem getPolicyZdgzItem (int rowIndex) 
	{
		return this.govpolicy.get(rowIndex);
	}
	public void deleteZdgzItem (int row)
	{
		govpolicy.remove(row);
		this.fireTableDataChanged();
	}
	public void isPolicyZdgzItemSelected (int rowIndex)
	{
		ZdgzItem zdgzitem = govpolicy.get(rowIndex);
		
    	try {
    		boolean selected = zdgzitem.isSelected();
    		 zdgzitem.setItemSelected(!selected);
    	} catch (Exception e) {
    		zdgzitem.setItemSelected(true);
    	}
    	
    	this.fireTableDataChanged();
	}
	public int getRowCount() 
	{
		 if(govpolicy != null)
	        return govpolicy.size();
		 else
			 return 0;
	}

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	Object value = "??";
	    	ZdgzItem zdgzitem = govpolicy.get(rowIndex);

	    	switch (columnIndex) {
	    	case 0:
	    		value = zdgzitem.getId().toUpperCase();
	    		break;
            case 1:
            	try {
    	    		value = zdgzitem.isSelected();
    	    	} catch (Exception e) {
    	    		value = new Boolean (false);
    	    	}
                break;
            case 2:
            	String contents = zdgzitem.getContents();
                value = contents;
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
		    	  clazz = Boolean.class;
		    	  break;
		        case 2:
		          clazz = String.class;
		          break;
		      }
		      
		      return clazz;
		}
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) 
	    {
			if(1 == column) {
				return true;
			} else 
				return false;
		}
}
