package com.exchangeinfomanager.bankuaifengxi.ai;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.commonlib.MultilineTableCell;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.TableCellListener;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;
import com.google.common.base.Strings;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTable;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import com.toedter.calendar.JDateChooser;

public class GeGuWeeklyFengXi extends WeeklyFenXiWizardPage
{

	private static Logger logger = Logger.getLogger(GeGuWeeklyFengXi.class);
//	private SystemConfigration sysconf;
//	private boolean xmlmartixshouldsave;


	/**
	 * Create the dialog.
	 */
	public GeGuWeeklyFengXi(String title, String description,BkChanYeLianTreeNode displaynode2, LocalDate selectdate2,WeeklyFengXiXmlHandler fxmlhandler) 
	{
		super(title,description,displaynode2,selectdate2,fxmlhandler);
		
		//确保只有个股才用这个类，板块和大盘不可以用。
		if(displaynode.getType() == BanKuaiAndStockBasic.DAPAN /*|| displaynode.getType() == BanKuaiAndStockBasic.TDXBK*/)
			return;
		
		initializGui ();
		createEvents ();
		
		dateChooser.setLocalDate(selectdate2);
		updateMingRiJiHuaActions ();
		
		showStockAnalysisXmlResults (displaynode.getMyOwnCode(),selectdate2);
	}
	
	
	private void showStockAnalysisXmlResults(String stockcode, LocalDate date) 
	{
		zdgz = super.fxmlhandler.getZhongDianGuanZhu();
		
		((GeGuCheckListsTableModel)tableGuPiaoChi.getModel()).refresh(zdgz.getGeGuGuPiaoChi());
		((GeGuCheckListsTableModel)tableTiCai.getModel()).refresh(zdgz.getGeGuTiCha());
		((GeGuCheckListsTableModel)tablejishu.getModel()).refresh(zdgz.getGeGuJiShu());
		((GeGuCheckListsTableModel)tableFinancial.getModel()).refresh(zdgz.getGeGuFinancial());
		((GeGuCheckListsTableModel)tablegudong.getModel()).refresh(zdgz.getGeGuGuDong());
		
		txaAnalysisComments.setText(zdgz.getGeGuAnalysisComments());
	}
	@Override
	public String getWeeklyFenXiComments() 
	{
		return txaAnalysisComments.getText();
	}
	/*
	 * 
	 */
	private void updateMingRiJiHuaActions ()
	{
		LocalDate sun = LocalDate.now().with(DayOfWeek.SUNDAY);
		long weeks = ChronoUnit.WEEKS.between(LocalDate.now().with(DayOfWeek.SUNDAY), dateChooser.getLocalDate());
		if(weeks == 0) { //不是当前周，应该不可以设置明日计划，毕竟过去已经过去。
			jbxMingRiJIhua.setEnabled(true);
		} else
			jbxMingRiJIhua.setEnabled(false);
	}
	/*
	 * 
	 */
//	public void saveMingRiJiHuaFenXiResult ()
//	{
//			if(jbxMingRiJIhua.isSelected() && !Strings.isNullOrEmpty( tfdJihuaJiage.getText() ) ) { //明日计划
////				zdgz.setMrjhenabled(true);
////				zdgz.setMrjhaction( cbxJihuaLeixing.getSelectedItem().toString());
////				zdgz.setMrjhprice(Double.parseDouble(tfdJihuaJiage.getText()));
//				
//		        bkopt.setMingRiJiHua(nodecode,actiondate.plus(1,ChronoUnit.DAYS),
//		        		cbxJihuaLeixing.getSelectedItem().toString(),Double.parseDouble(tfdJihuaJiage.getText()), tfldmrjhshuoming.getText() );
//			}
//			
//		
//	}
	
	/* (non-Javadoc)
     * @see com.github.cjwizard.WizardPage#updateSettings(com.github.cjwizard.WizardSettings)
     */
    @Override
    public void updateSettings(WizardSettings settings) {
//             super.updateSettings(settings);
//       System.out.println("test");
       // This is called when the user clicks next, so we could do
       // some longer-running processing here if we wanted to, and
       // pop up progress bars, etc.  Once this method returns, the
       // wizard will continue.  Beware though, this runs in the
       // event dispatch thread (EDT), and may render the UI
       // unresponsive if you don't issue a new thread for any long
       // running ops.  Future versions will offer a better way of
       // doing this.
    }
    
	private void createEvents()
	{
		btnxmlMatrix.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconfig.getGeGuFengXiWeeklyXmlMatrixFile ();
					Process p  = Runtime.getRuntime().exec(cmd);
					p.waitFor();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
				    e1.printStackTrace();
				}
			}
		});
		
//		txaAnalysisComments.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyTyped(KeyEvent arg0) {
//				infochanged = true;
//			}
//		});
		
		jbxMingRiJIhua.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) 
			{
				if(true == jbxMingRiJIhua.isSelected()) {
					tfdJihuaJiage.setEnabled(true);
					//tfdShuoming.setEnabled(true);
					cbxJihuaLeixing.setEnabled(true);
					tfldmrjhshuoming.setEnabled(true);
					
//					PropertyChangeEvent evt2 = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_PROPERTY, "", "");
//			        pcs.firePropertyChange(evt2);
				} else {
					tfdJihuaJiage.setEnabled(false);
					//tfdShuoming.setEnabled(true);
					cbxJihuaLeixing.setEnabled(false);
					tfldmrjhshuoming.setEnabled(false);
				}
			}
		});
		
		Action tableaction = new AbstractAction()  {
				private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e)
			        {
			            TableCellListener tcl = (TableCellListener)e.getSource();
			            logger.debug("Row   : " + tcl.getRow());
			            logger.debug("Column: " + tcl.getColumn());
			            logger.debug("Old   : " + tcl.getOldValue());
			            logger.debug("New   : " + tcl.getNewValue());
			            
			            if(tcl.getColumn() == 1) {
			            	 Boolean old = (Boolean) tcl.getOldValue();
				             Boolean newvalue = (Boolean) tcl.getNewValue();
				            if(old != newvalue) {
				            	PropertyChangeEvent evt = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_ADDED, "", "new");
						        pcs.firePropertyChange(evt);
				            }
			            } else if(tcl.getColumn() == 3) {
			            	String old = (String) tcl.getOldValue();
				            String newvalue = (String) tcl.getNewValue();
				            if(!old.equals(newvalue)) {
				            	PropertyChangeEvent evt = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_ADDED, "", "new");
						        pcs.firePropertyChange(evt);
				            }
			            } if(tcl.getColumn() == 2) {
			            	String old = (String) tcl.getOldValue();
				            String newvalue = (String) tcl.getNewValue();
				            if(!old.equals(newvalue)) {
				            	PropertyChangeEvent evt = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_ADDED, "", "new");
						        pcs.firePropertyChange(evt);
						        
						        PropertyChangeEvent evt2 = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLMATRIX_PROPERTY, "", "new");
						        pcs.firePropertyChange(evt2);
				            }
			            	
			            }
			            
			            	
			            
//			            Object[] tabledata= new Object [6] ;
//			            tabledata[0] = tblzhongdiangz.getModel().getValueAt(tcl.getRow(),0);
//			            tabledata[1] = tblzhongdiangz.getModel().getValueAt(tcl.getRow(),1);
//			            tabledata[2] = tblzhongdiangz.getModel().getValueAt(tcl.getRow(),2);
//			            tabledata[3] = tblzhongdiangz.getModel().getValueAt(tcl.getRow(),3);
//			            tabledata[4] = tblzhongdiangz.getModel().getValueAt(tcl.getRow(),4);
//			            tabledata[5] = tblzhongdiangz.getModel().getValueAt(tcl.getRow(),5);
//			            
//			            setTableNewInfoToDB (tabledata);
			            
			            
			        }
			    };

			TableCellListener tcl = new TableCellListener(tableGuPiaoChi, tableaction);
			TableCellListener tcl2 = new TableCellListener(tableFinancial, tableaction);
			TableCellListener tcl3 = new TableCellListener(tablegudong, tableaction);
			TableCellListener tcl4 = new TableCellListener(tablejishu, tableaction);
			TableCellListener tcl5 = new TableCellListener(tableTiCai, tableaction);
			
//			tableGuPiaoChi.addPropertyChangeListener(new PropertyChangeListener() {
//				public void propertyChange(PropertyChangeEvent e) {
//					if ("tableCellEditor".equals(e.getPropertyName()))
//					{
//						if (tableGuPiaoChi.isEditing())
//							logger.debug("edit");
//						else {
//							logger.debug("unedit");
//							int row = tableGuPiaoChi.getSelectedRow();
//							
//						}
//					}
//				}
//			});
			
			btnRmvChklt.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int row = curworktable.getSelectedRow();
					if(row != -1) {
						((GeGuCheckListsTableModel)curworktable.getModel()).deleteZdgzItem(row);
						
						int action = JOptionPane.showConfirmDialog(null, "是否同时将该项从XML Matrix文件删除？","警告", JOptionPane.YES_NO_OPTION);
						if(0 == action) {
							PropertyChangeEvent evt = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_ADDED, "", "new");
					        pcs.firePropertyChange(evt);
					        
					        PropertyChangeEvent evt2 = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLMATRIX_ADDED, "", "new");
					        pcs.firePropertyChange(evt2);
					        
						} else {
							PropertyChangeEvent evt = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_ADDED, "", "new");
					        pcs.firePropertyChange(evt);
						}
						
					}
				}
			});
			
			btnaddcklst.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) 
				{
					String xmltag;
					try {
						xmltag = ((GeGuCheckListsTableModel)curworktable.getModel()).getXmlTagBelonged();
					} catch (java.lang.NullPointerException ex) {
						JOptionPane.showMessageDialog(null,"请先选择需要添加的表！");
						return;
					}
					Object value = ((GeGuCheckListsTableModel)curworktable.getModel()).getValueAt(0, 0);
					Integer idcount = ((GeGuCheckListsTableModel)curworktable.getModel()).getRowCount() +1;
					ZdgzItem zdgzitem = new ZdgzItem ( value.toString().substring(0, 2) + idcount.toString(),xmltag);
					int exchangeresult = JOptionPane.showConfirmDialog(null, zdgzitem, "增加CheckList", JOptionPane.OK_CANCEL_OPTION);
					if(exchangeresult == JOptionPane.CANCEL_OPTION)
						return;
					
					((GeGuCheckListsTableModel)curworktable.getModel()).addNewItem(zdgzitem);
					PropertyChangeEvent evt = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_PROPERTY, "", "NEW");
			        pcs.firePropertyChange(evt);
					
					if(zdgzitem.shouldAddToXmlMartixFile()) {
						PropertyChangeEvent evt2 = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLMATRIX_PROPERTY, "", "NEW");
				        pcs.firePropertyChange(evt2);
					}
				}
			});
			
			tableFinancial.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) 
				{
					int row = tableFinancial.getSelectedRow();
					int column = tableFinancial.getSelectedColumn();
					changeTableRowSelectedStatus (tableFinancial,row,column);
					curworktable = tableFinancial;
					
				}
			});
			
			tablegudong.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) 
				{
					int row = tablegudong.getSelectedRow();
					int column = tablegudong.getSelectedColumn();
					changeTableRowSelectedStatus (tablegudong,row,column);
					curworktable = tablegudong;
					
				}
			});
			
			tablejishu.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) 
				{
					int row = tablejishu.getSelectedRow();
					int column = tablejishu.getSelectedColumn();
					changeTableRowSelectedStatus (tablejishu,row,column);
					curworktable = tablejishu;
					
				}
			});
			
			tableTiCai.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) 
				{
					int row = tableTiCai.getSelectedRow();
					int column = tableTiCai.getSelectedColumn();
					changeTableRowSelectedStatus (tableTiCai,row,column);
					curworktable = tableTiCai;
					
				}
			});
			
			tableGuPiaoChi.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) 
				{
					int row = tableGuPiaoChi.getSelectedRow();
					int column = tableGuPiaoChi.getSelectedColumn();
					changeTableRowSelectedStatus (tableGuPiaoChi,row,column);
					curworktable = tableGuPiaoChi;
					
				}
			});
			
			txaAnalysisComments.getDocument().addDocumentListener(new DocumentListener() {

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
			
			chckbxmacd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(chckbxmacd.isSelected()) 
						updatedRecords (chckbxmacd,actiondate);
				}
			});
			chcbxkaishi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(chcbxkaishi.isSelected()) 
						updatedRecords (chcbxkaishi,actiondate);
				}
			});
			chcbxjieshu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(chcbxjieshu.isSelected()) 
						updatedRecords (chcbxjieshu,null);
				}
			});
			chcbxmx.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(chcbxmx.isSelected()) 
						updatedRecords (chcbxmx,null);
				}
			});
			chcbxfantan.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(chcbxfantan.isSelected()) 
						updatedRecords (chcbxfantan,null);
				}
			});
		
	}
	/*
	 * 
	 */
	private void updatedRecords (JCheckBox box,LocalDate date)
	{
		String boxtext = box.getText();
		String cutext = txaAnalysisComments.getText();
		if(date == null)
			txaAnalysisComments.setText(cutext + boxtext + ", ");
		else
			txaAnalysisComments.setText(cutext + date.toString() +  boxtext + ", ");
	}
	/*
	 * 用户点击
	 */
	protected void changeTableRowSelectedStatus(JTable tableFinancial2, int row, int column) 
	{
//		if(row != -1) {
//			if(column == 1) {
//				((GeGuCheckListsTableModel)tableFinancial2.getModel()).isPolicyZdgzItemSelected(row);
//				infochanged = true;
//			}
//			
//		}
		
		String output = txaExaComments.getText()
						+ ((GeGuCheckListsTableModel)tableFinancial2.getModel()).getValueAt(row, 2) 
						+ ", " 
						+ ((GeGuCheckListsTableModel)tableFinancial2.getModel()).getValueAt(row, 3)
						+ "。\n"
						;
		txaExaComments.setText(output);
	}


	private final JPanel contentPanel = new JPanel();
	private JTable tableFinancial;
	private JTable tablegudong;
	private JTable tablejishu;
	private JTable tableTiCai;
	private JTable tableGuPiaoChi;
	private JTextArea txaAnalysisComments;
	private JTextArea txaExaComments;
//	private GeGuWeeklyFengXiXmlHandler ggxmlhandler;
	private ZhongDianGuanZhu zdgz;
	protected JTable curworktable;
	private JButton btnaddcklst;
	private JButton btnRmvChklt;
	private JCheckBox jbxMingRiJIhua;
	private JTextField tfdJihuaJiage;
	private JComboBox cbxJihuaLeixing;
	private BanKuaiDbOperation bkopt;
	private BkChanYeLianTreeNode nodecode;
	private LocalDate actiondate;
	private JTextField tfldmrjhshuoming;
	private JCheckBox chcbxkaishi;
	private JCheckBox chcbxjieshu;
	private JCheckBox chcbxmx;
	private JCheckBox chcbxfantan;
	private JCheckBox chckbxmacd;
	private JButton btnxmlMatrix;
	private JCheckBox checkBox;
	private JLocalDateChooser dateChooser;
	private void initializGui ()
	{
		setBounds(100, 100, 1569, 806);
		setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPanel, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane();
		JScrollPane scrollPane_1 = new JScrollPane();
		JScrollPane scrollPane_2 = new JScrollPane();
		JScrollPane scrollPane_3 = new JScrollPane();
		JScrollPane scrollPane_4 = new JScrollPane();
		JScrollPane scrollPane_5 = new JScrollPane();
		
		JScrollPane scrollPane_6 = new JScrollPane();
		
		JPanel panel = new JPanel();
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane_3, GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
						.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(scrollPane_6, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.TRAILING, gl_contentPanel.createParallelGroup(Alignment.LEADING, false)
							.addComponent(scrollPane_5, Alignment.TRAILING, 0, 0, Short.MAX_VALUE)
							.addComponent(scrollPane_4, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(5)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
								.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(scrollPane_4, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(scrollPane_5, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPane_6, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
								.addComponent(panel, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)))
						.addComponent(scrollPane_3, GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		chcbxkaishi = new JCheckBox("\u5F00\u59CB\u5173\u6CE8");
		
		chcbxjieshu = new JCheckBox("\u7ED3\u675F\u5173\u6CE8");
		
		chcbxmx = new JCheckBox("\u6EE1\u8DB3\u653E\u91CF\u5747\u7EBF\u6A21\u578B");
		
		chcbxfantan = new JCheckBox("\u5927\u6DA8\u540E\u53F3\u4FA7\u4E0B\u5C71\u8D70\u52BF\uFF0C\u53CD\u5F39\u8D8A\u6765\u8D8A\u5F31");
		
		chckbxmacd = new JCheckBox("\u4E0B\u8DCC\u7F29\u91CF\uFF0C\u5206\u65F6MACD\u80CC\u79BB");
		panel.setLayout(new MigLayout("", "[73px][2px][142px]", "[23px][23px][23px][23px][]"));
		panel.add(chcbxkaishi, "cell 0 0,alignx left,aligny top");
		panel.add(chcbxjieshu, "cell 2 0,alignx left,aligny top");
		panel.add(chcbxmx, "flowx,cell 0 1 3 1,alignx left,aligny top");
		panel.add(chcbxfantan, "cell 0 2 3 1,alignx left,aligny top");
		panel.add(chckbxmacd, "cell 0 3 3 1,alignx left,aligny top");
		
		checkBox = new JCheckBox("\u6B21\u65B0\u80A1");
		panel.add(checkBox, "cell 0 4");
		
		txaExaComments = new JTextArea();
		txaExaComments.setLineWrap(true);
		scrollPane_6.setViewportView(txaExaComments);
		
		txaAnalysisComments = new JTextArea();
		
		txaAnalysisComments.setLineWrap(true);
		scrollPane_5.setViewportView(txaAnalysisComments);
		
		tableGuPiaoChi = new GeGuCheckListsTable("所属股票池");
		setTableColumnsWidth(tableGuPiaoChi);
		scrollPane_4.setViewportView(tableGuPiaoChi);
		
		tableTiCai = new GeGuCheckListsTable("题材分析");
		setTableColumnsWidth(tableTiCai);
		scrollPane_3.setViewportView(tableTiCai);
		

		tablejishu = new GeGuCheckListsTable ("技术与模型分析");
		setTableColumnsWidth(tablejishu);
		scrollPane_2.setViewportView(tablejishu);
		
		tablegudong = new GeGuCheckListsTable ("股东分析") ;
		setTableColumnsWidth(tablegudong);
		scrollPane_1.setViewportView(tablegudong);
		
		tableFinancial = new GeGuCheckListsTable("财务分析");
		setTableColumnsWidth(tableFinancial);
		scrollPane.setViewportView(tableFinancial);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			add(buttonPane, BorderLayout.SOUTH);
			
			btnaddcklst = new JButton("\u6DFB\u52A0CheckLists");
			
			
			btnRmvChklt = new JButton("\u5220\u9664");
			
			jbxMingRiJIhua = new JCheckBox("\u660E\u65E5\u8BA1\u5212");
			
			cbxJihuaLeixing = new JComboBox();
			cbxJihuaLeixing.setEnabled(false);
			cbxJihuaLeixing.setModel(new DefaultComboBoxModel(new String[] {"\u666E\u901A\u4E70\u5165", "\u666E\u901A\u5356\u51FA", "\u4FE1\u7528\u666E\u901A\u4E70\u5165", "\u4FE1\u7528\u666E\u901A\u5356\u51FA", "\u878D\u8D44\u4E70\u5165", "\u5356\u51FA\u507F\u8FD8\u878D\u8D44", "\u878D\u5238\u5356\u51FA", "\u4E70\u5165\u6362\u5238"}));
			
			JLabel label = new JLabel("\u4EF7\u683C");
			
			tfdJihuaJiage = new JTextField();
			tfdJihuaJiage.setEnabled(false);
			tfdJihuaJiage.setColumns(10);
			
			JLabel label_1 = new JLabel("\u8BF4\u660E");
			
			tfldmrjhshuoming = new JTextField();
			tfldmrjhshuoming.setEnabled(false);
			tfldmrjhshuoming.setColumns(10);
			
			btnxmlMatrix = new JButton("\u6253\u5F00XML Matrix");
			
			dateChooser = new JLocalDateChooser();
			dateChooser.setEnabled(false);
			
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addComponent(btnaddcklst)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(btnRmvChklt)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(btnxmlMatrix)
						.addGap(59)
						.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
						.addGap(27)
						.addComponent(jbxMingRiJIhua)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(cbxJihuaLeixing, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(label)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(tfdJihuaJiage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldmrjhshuoming, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
						.addGap(405))
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(5)
						.addGroup(gl_buttonPane.createParallelGroup(Alignment.LEADING)
							.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnaddcklst)
								.addComponent(btnRmvChklt)
								.addComponent(jbxMingRiJIhua)
								.addComponent(cbxJihuaLeixing, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(label)
								.addComponent(tfdJihuaJiage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(label_1)
								.addComponent(tfldmrjhshuoming, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnxmlMatrix)))
						.addContainerGap())
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
		
		int columntwo = 250;
		table.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(columntwo);
		table.getTableHeader().getColumnModel().getColumn(2).setMinWidth(columntwo);
		table.getTableHeader().getColumnModel().getColumn(2).setWidth(columntwo);
		table.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(columntwo);
		
		
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(80);
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(4).setMinWidth(80);
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(4).setWidth(80);
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(4).setPreferredWidth(80);
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(0);
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(5).setMinWidth(0);
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(5).setWidth(0);
//	    tblzhongdiangz.getTableHeader().getColumnModel().getColumn(5).setPreferredWidth(0);
	}
}


class GeGuCheckListsTableModel extends AbstractTableModel 
{
	String[] jtableTitleStrings = { "ID","选中", "描述","分析结果"};
	private ArrayList<ZdgzItem> zdgzitems;
//	private Boolean infochanged;
	private String xmltagbelonged;
	
	GeGuCheckListsTableModel ()
	{
	}
	
	public void refresh  (ArrayList<ZdgzItem> zdgzitems2 )
	{
		this.zdgzitems = zdgzitems2;
		this.xmltagbelonged = this.zdgzitems.get(0).getXmlTagBelonged();
		
		this.fireTableDataChanged();
	}
	public String getXmlTagBelonged() {
		return xmltagbelonged;
	}
	public ZdgzItem getPolicyZdgzItem (int rowIndex) 
	{
		return this.zdgzitems.get(rowIndex);
	}
	
//	public boolean isInfoChanged ()
//	{
//		if(infochanged != null)
//			return this.infochanged;
//		else 
//			return false;
//	}
	
	public void setColumnName(String name) {
		jtableTitleStrings[1] = name;
	    fireTableStructureChanged();
	}
	public void addNewItem(ZdgzItem zdgzitem) 
	{
		this.zdgzitems.add(zdgzitem);
		this.fireTableDataChanged();
	}
	public void deleteZdgzItem (int row)
	{
		zdgzitems.remove(row);
		this.fireTableDataChanged();
	}
	public void isPolicyZdgzItemSelected (int rowIndex)
	{
		ZdgzItem zdgzitem = zdgzitems.get(rowIndex);
		
		try {
    		boolean selected = zdgzitem.isSelected();
    		 zdgzitem.setItemSelected(!selected);
    	} catch (Exception e) { //说明以前没有设置，那肯定要设置为true
    		zdgzitem.setItemSelected(true);
    	}
    	
    	this.fireTableDataChanged();
	}
	
	 public int getRowCount() 
	 {
		 if(zdgzitems != null)
	        return zdgzitems.size();
		 else
			 return 0;
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    @Override
	    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	    {
	    	ZdgzItem zdgzitem = zdgzitems.get(rowIndex);
	        if(3 == columnIndex) {
	        	zdgzitem.setValue((String) aValue);
	        } else if(1 == columnIndex) {
	        	zdgzitem.setItemSelected((Boolean) aValue);
	        } else if(2 == columnIndex) {
	        	zdgzitem.setContents((String) aValue);
	        }
	    }
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	Object value = "??";
	    	ZdgzItem zdgzitem;
	    	try {
	    		zdgzitem = zdgzitems.get(rowIndex);
	    	} catch (java.lang.ArrayIndexOutOfBoundsException e) {
//	    		e.printStackTrace();
	    		return null;
	    	}
	    	String itemvalue = zdgzitem.getValue();
	    	String contents = zdgzitem.getContents();
	    	
	    	switch (columnIndex) {
	    	case 0:
	    		value = zdgzitem.getId();
	    		break;
            case 1:
            	try {
    	    		value = zdgzitem.isSelected();
    	    	} catch (Exception e) {
    	    		value = new Boolean (false);
    	    	}
                break;
            case 2:
                value = contents;
                break;
            case 3:
                value = itemvalue;
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
		        case 3:
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
				return true;
		}
	    
}


class GeGuCheckListsTable extends JTable 
{
	public GeGuCheckListsTable (String indicator)
	{
		super ();
		GeGuCheckListsTableModel tablegudongmodel = new GeGuCheckListsTableModel ();
		super.setModel(tablegudongmodel);
		tablegudongmodel.setColumnName(indicator);
	}
	
	MultilineTableCell wordWrapRenderer = new MultilineTableCell (); 
	
	public TableCellRenderer getCellRenderer(int row, int column) {
        if (column == 2 ) {
            return wordWrapRenderer;
        }
        else {
            return super.getCellRenderer(row, column);
        }
    }
	
	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) 
	{
		 
        Component comp = super.prepareRenderer(renderer, row, col);
        GeGuCheckListsTableModel tablemodel = (GeGuCheckListsTableModel)this.getModel(); 
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
        
        try {
            if(zdgzitem.isSelected() && col == 1)
            	background = Color.RED;
        } catch (java.lang.NullPointerException e) {
        	
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
        } catch (java.lang.NullPointerException e1) {
//        	e1.printStackTrace();
        }
        return tip;
    } 

}
