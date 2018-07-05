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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;

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
import java.beans.PropertyChangeEvent;
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
import javax.swing.JTree;

/*
 * 用于大盘每周分析
 */
public class GovAndBanKuaiWeeklyFengXi extends WeeklyFenXiWizardPage 
{
	/**
	 * Create the dialog.
	 */
	public GovAndBanKuaiWeeklyFengXi(String title, String description, BkChanYeLianTreeNode displaynode2,LocalDate selectdate2,WeeklyFengXiXmlHandler dpfxmlhandler) 
	{
		super(title,description,displaynode2,selectdate2,dpfxmlhandler);
		
		createGui ();
		datachooser.setLocalDate(selectdate2);
		
		createEvents ();
		
		showZhiShuAnalysisXmlResults ();
	}

//	private SystemConfigration sysconfig;
//	private BanKuaiDbOperation bkdbopt;
//	private AllCurrentTdxBKAndStoksTree allbksks;
//	private BanKuaiAndChanYeLian2 bkcyl;
//	private DaPanWeeklyFengXiXmlHandler dpfxmlhandler; 
//	private LocalDate selectdate;
//	private List<BkChanYeLianTreeNode> zhishulist;
//	private boolean infochanged = false;
//	private boolean xmlmartixshouldsave = false;
//	private ZhongDianGuanZhu zdgzinfo;
	/*
	 * 
	 */
	private void showZhiShuAnalysisXmlResults() 
	{
		
		zdgzinfo = super.fxmlhandler.getZhongDianGuanZhu();

		ArrayList<ZdgzItem> govpolicy = zdgzinfo.getGovpolicy();
		((PolicyTableModel)tablepolicy.getModel()).refresh(govpolicy);
		
//		ArrayList<ZdgzItem> dapan = zdgzinfo.getDapanZhiShuLists();
//		for(ZdgzItem zhishu : dapan) {
//			String zhishucode = zhishu.getValue();
//			BanKuai zhishubankuai = allbksks.getBanKuai(zhishucode, selectdate, StockGivenPeriodDataItem.WEEK);
//			zhishulist.add(zhishubankuai);
//		}
//		
		txaComments.setText(zdgzinfo.getDaPanAnalysisComments() );
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
		
//		tablepolicy.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent arg0) 
//			{
//				int row = tablepolicy.getSelectedRow();
//				int column = tablepolicy.getSelectedColumn();
//				if(row != -1) {
//					if(column == 1) {
//						((PolicyTableModel)tablepolicy.getModel()).isPolicyZdgzItemSelected(row);
////						infochanged = true;
//					}
//					
//				}
//			}
//		});
		
		txaComments.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				String new_value = arg0.getDocument().toString();
				PropertyChangeEvent evt = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_PROPERTY, "", arg0.getDocument());
		        pcs.firePropertyChange(evt);
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				String new_value = arg0.getDocument().toString();
				PropertyChangeEvent evt = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_PROPERTY, "", arg0.getDocument());
		        pcs.firePropertyChange(evt);
				
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				String new_value = arg0.getDocument().toString();
				PropertyChangeEvent evt = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_PROPERTY, "", arg0.getDocument());
		        pcs.firePropertyChange(evt);
			}
		});
		
		btnremovepolicy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				int row = tablepolicy.getSelectedRow();
				if(row != -1) {
					((PolicyTableModel)tablepolicy.getModel()).deleteZdgzItem(row);
					int action = JOptionPane.showConfirmDialog(null, "是否同时将该选项从XML Matrix文件中删除？","警告", JOptionPane.YES_NO_OPTION);
					if(0 == action) {
						PropertyChangeEvent evt = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLMATRIX_PROPERTY, "", "NEW");
				        pcs.firePropertyChange(evt);
				        
				        PropertyChangeEvent evt2 = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_PROPERTY, "", "NEW");
				        pcs.firePropertyChange(evt2);
				        
					} else {
						PropertyChangeEvent evt = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_PROPERTY, "", "NEW");
				        pcs.firePropertyChange(evt);
					}
				} else 
					JOptionPane.showMessageDialog(null,"请选择需要删除的指数！");
			}
		});
		
		btnaddchecklists.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String xmltag = ((PolicyTableModel)tablepolicy.getModel()).getXmlTagBelonged();
				ZdgzItem zdgzitem = new ZdgzItem ("GP" + String.valueOf(zdgzinfo.getGovpolicy().size()+1), xmltag);
				int exchangeresult = JOptionPane.showConfirmDialog(null, zdgzitem, "增加CheckList", JOptionPane.OK_CANCEL_OPTION);
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;
				
				((PolicyTableModel)tablepolicy.getModel()).addNewItem(zdgzitem);
				
				if(zdgzitem.shouldAddToXmlMartixFile()) {
					PropertyChangeEvent evt1 = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_PROPERTY, "", "NEW");
			        pcs.firePropertyChange(evt1);
			        
					PropertyChangeEvent evt2 = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLMATRIX_PROPERTY, "", "NEW");
			        pcs.firePropertyChange(evt2);
				} else {
					PropertyChangeEvent evt2 = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_PROPERTY, "", "NEW");
			        pcs.firePropertyChange(evt2);
				}
			}
		});
	}
	private final JPanel contentPanel = new JPanel();
	private JLocalDateChooser datachooser;
	private JTable tablepolicy;
	private JTextArea txaComments;
	private JButton btnaddchecklists;
	private JButton btnremovepolicy;
	private JButton btnxmlMartrix;
	
	private void createGui ()
	{
		setBounds(100, 100, 1292, 721);
		setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPanel, BorderLayout.CENTER);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JEditorPane editorPane = new JEditorPane();
		scrollPane_1.setViewportView(editorPane);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		
		JScrollPane scrollPane_3 = new JScrollPane();
		
		JLabel label = new JLabel("\u70ED\u70B9\u677F\u5757");
		
		JLabel label_1 = new JLabel("\u5F31\u52BF\u677F\u5757");
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 499, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane_3, GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 201, GroupLayout.PREFERRED_SIZE)
							.addGap(20))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE)
							.addGap(325)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(label_1)
								.addComponent(label))
							.addContainerGap(648, Short.MAX_VALUE))))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(28)
							.addComponent(label)
							.addGap(18)
							.addComponent(label_1))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 326, GroupLayout.PREFERRED_SIZE)))
					.addGap(7)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
						.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
						.addComponent(scrollPane_3, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		JTree tree = new JTree();
		scrollPane.setViewportView(tree);
		
		txaComments = new JTextArea();
		txaComments.setLineWrap(true);
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
		setTableColumnsWidth (tablepolicy);
		scrollPane_2.setViewportView(tablepolicy);
		
		DaPanTableModel dpmodel = new DaPanTableModel ();
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			add(buttonPane, BorderLayout.SOUTH);
//				getRootPane().setDefaultButton(okButton);
			
			
			datachooser = new JLocalDateChooser ();
			datachooser.setEnabled(false);
			
			btnaddchecklists = new JButton("\u6DFB\u52A0\u653F\u7B56Checklists");

			btnremovepolicy = new JButton("\u5220\u9664");
			
			btnxmlMartrix = new JButton("\u6253\u5F00Xml Martrix");
			
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(21)
						.addComponent(btnaddchecklists)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnremovepolicy)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(btnxmlMartrix)
						.addGap(812)
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
								.addComponent(btnxmlMartrix, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)))
						.addGap(11))
			);
			buttonPane.setLayout(gl_buttonPane);
		}
	}
	
	private void setTableColumnsWidth (JTable table)
	{
		table.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(50);
		table.getTableHeader().getColumnModel().getColumn(0).setMinWidth(50);
		table.getTableHeader().getColumnModel().getColumn(0).setWidth(50);
		table.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(50);
		
		table.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(50);
		table.getTableHeader().getColumnModel().getColumn(1).setMinWidth(50);
		table.getTableHeader().getColumnModel().getColumn(1).setWidth(50);
		table.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(50);		
		
//		table.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(0);
//		table.getTableHeader().getColumnModel().getColumn(3).setMinWidth(0);
//		table.getTableHeader().getColumnModel().getColumn(3).setWidth(0);
//		table.getTableHeader().getColumnModel().getColumn(3).setPreferredWidth(0);
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(80);
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(4).setMinWidth(80);
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(4).setWidth(80);
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(4).setPreferredWidth(80);
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(0);
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(5).setMinWidth(0);
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(5).setWidth(0);
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(5).setPreferredWidth(0);
	}

	@Override
	public String getWeeklyFenXiComments() {
		// TODO Auto-generated method stub
		return null;
	}
}




class PolicyTableModel extends AbstractTableModel 
{
	String[] jtableTitleStrings = { "ID","选中", "政策面"};
	private ArrayList<ZdgzItem> govpolicy;
	private String xmltagbelonged;
	
	PolicyTableModel ()
	{
	}
	public void refresh  (ArrayList<ZdgzItem> govpolicy2 )
	{
		this.govpolicy = govpolicy2;
		this.xmltagbelonged = this.govpolicy.get(0).getXmlTagBelonged();
		
		this.fireTableDataChanged();
	}
	public String getXmlTagBelonged() {
		return xmltagbelonged;
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
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
    	ZdgzItem zdgzitem = govpolicy.get(rowIndex);
    	if(1 == columnIndex) {
        	zdgzitem.setItemSelected((Boolean) aValue);
        }
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
