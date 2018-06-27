package com.exchangeinfomanager.bankuaifengxi.ai;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
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

public class GeGuWeeklyFengXi extends WizardPage
{

	private static Logger logger = Logger.getLogger(GeGuWeeklyFengXi.class);
	private final JPanel contentPanel = new JPanel();
	private JTable tableFinancial;
	private JTable tablegudong;
	private JTable tablejishu;
	private JTable tableTiCai;
	private JTable tableGuPiaoChi;
	private JTextArea txaAnalysisComments;
	private JTextArea txaExaComments;
	private GeGuWeeklyFengXiXmlHandler ggxmlhandler;
	private boolean infochanged = false;
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
	private SystemConfigration sysconf;


	/**
	 * Create the dialog.
	 */
	public GeGuWeeklyFengXi(String title, String description,BkChanYeLianTreeNode displaynode, LocalDate date) 
	{
		super (title,description);
		
		//确保只有个股才用这个类，板块和大盘不可以用。
		if(displaynode.getType() == BanKuaiAndStockBasic.DAPAN || displaynode.getType() == BanKuaiAndStockBasic.TDXBK)
			return;
		
		this.bkopt = new BanKuaiDbOperation ();
		this.nodecode = displaynode;
		this.actiondate = date;
		sysconf = SystemConfigration.getInstance();
		
		initializGui ();
		createEvents ();
		
		showStockAnalysisXmlResults (displaynode.getMyOwnCode(),date);
	}
	
	
	private void showStockAnalysisXmlResults(String stockcode, LocalDate date) 
	{
		ggxmlhandler = new GeGuWeeklyFengXiXmlHandler (stockcode,date);
		zdgz = ggxmlhandler.getZhongDianGuanZhu();
		
		((GeGuCheckListsTableModel)tableGuPiaoChi.getModel()).refresh(zdgz.getGeGuGuPiaoChi());
		((GeGuCheckListsTableModel)tableTiCai.getModel()).refresh(zdgz.getGeGuTiCha());
		((GeGuCheckListsTableModel)tablejishu.getModel()).refresh(zdgz.getGeGuJiShu());
		((GeGuCheckListsTableModel)tableFinancial.getModel()).refresh(zdgz.getGeGuFinancial());
		((GeGuCheckListsTableModel)tablegudong.getModel()).refresh(zdgz.getGeGuGuDong());
		
		txaAnalysisComments.setText(zdgz.getGeGuAnalysisComments());
		
	}
	
	/*
	 * 
	 */
	public void saveFenXiResult ()
	{
		if(infochanged ) {
			zdgz.setDaPanAnalysisComments(txaAnalysisComments.getText());
			
			if(jbxMingRiJIhua.isSelected() && !Strings.isNullOrEmpty( tfdJihuaJiage.getText() ) ) { //明日计划
//				zdgz.setMrjhenabled(true);
//				zdgz.setMrjhaction( cbxJihuaLeixing.getSelectedItem().toString());
//				zdgz.setMrjhprice(Double.parseDouble(tfdJihuaJiage.getText()));
				
		        bkopt.setMingRiJiHua(nodecode,actiondate.plus(1,ChronoUnit.DAYS),
		        		cbxJihuaLeixing.getSelectedItem().toString(),Double.parseDouble(tfdJihuaJiage.getText()), tfldmrjhshuoming.getText() );
			}
			
			ggxmlhandler.saveFenXiXml (zdgz);
		}
	}
	
	/* (non-Javadoc)
     * @see com.github.cjwizard.WizardPage#updateSettings(com.github.cjwizard.WizardSettings)
     */
    @Override
    public void updateSettings(WizardSettings settings) {
//             super.updateSettings(settings);
       System.out.println("test");
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
					String cmd = "rundll32 url.dll,FileProtocolHandler " + sysconf.getGeGuFengXiWeeklyXmlMatrixFile ();
					Process p  = Runtime.getRuntime().exec(cmd);
					p.waitFor();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
				    e1.printStackTrace();
				}
			}
		});
		
		txaAnalysisComments.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				infochanged = true;
			}
		});
		
		jbxMingRiJIhua.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) 
			{
				if(true == jbxMingRiJIhua.isSelected()) {
					tfdJihuaJiage.setEnabled(true);
					//tfdShuoming.setEnabled(true);
					cbxJihuaLeixing.setEnabled(true);
					tfldmrjhshuoming.setEnabled(true);
					infochanged = true;
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
			    
			tableGuPiaoChi.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					if ("tableCellEditor".equals(e.getPropertyName()))
					{
						if (tableGuPiaoChi.isEditing())
							logger.debug("edit");
						else
							logger.debug("unedit");
					}
				}
			});
			
			btnRmvChklt.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int row = curworktable.getSelectedRow();
					if(row != -1) {
						((GeGuCheckListsTableModel)curworktable.getModel()).deleteZdgzItem(row);
						infochanged = true;
					}
				}
			});
			
			btnaddcklst.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) 
				{
					Object value = ((GeGuCheckListsTableModel)curworktable.getModel()).getValueAt(0, 0);
					Integer idcount = ((GeGuCheckListsTableModel)curworktable.getModel()).getRowCount() +1;
					ZdgzItem zdgzitem = new ZdgzItem ( value.toString().substring(0, 2) + idcount.toString());
					int exchangeresult = JOptionPane.showConfirmDialog(null, zdgzitem, "增加CheckList", JOptionPane.OK_CANCEL_OPTION);
					if(exchangeresult == JOptionPane.CANCEL_OPTION)
						return;
					
					((GeGuCheckListsTableModel)curworktable.getModel()).addNewItem(zdgzitem);
					infochanged = true;
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
					infochanged = true;
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
					infochanged = true;
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
					infochanged = true;
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
					infochanged = true;
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
					infochanged = true;
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
	 * 
	 */
	protected void changeTableRowSelectedStatus(JTable tableFinancial2, int row, int column) 
	{
		if(row != -1) {
			if(column == 1) {
				((GeGuCheckListsTableModel)tableFinancial2.getModel()).isPolicyZdgzItemSelected(row);
				infochanged = true;
			}
			
		}
	}


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
						.addComponent(scrollPane_4, GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
						.addComponent(scrollPane_5, GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(scrollPane_6, GroupLayout.PREFERRED_SIZE, 259, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(5)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
								.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(scrollPane_4, GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(scrollPane_5, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(panel, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)
								.addComponent(scrollPane_6, GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)))
						.addComponent(scrollPane_3, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		chcbxkaishi = new JCheckBox("\u5F00\u59CB\u5173\u6CE8");
		
		chcbxjieshu = new JCheckBox("\u7ED3\u675F\u5173\u6CE8");
		
		chcbxmx = new JCheckBox("\u6EE1\u8DB3\u653E\u91CF\u5747\u7EBF\u6A21\u578B");
		
		chcbxfantan = new JCheckBox("\u5927\u6DA8\u540E\u53F3\u4FA7\u4E0B\u5C71\u8D70\u52BF\uFF0C\u53CD\u5F39\u8D8A\u6765\u8D8A\u5F31");
		
		chckbxmacd = new JCheckBox("\u4E0B\u8DCC\u7F29\u91CF\uFF0C\u5206\u65F6MACD\u80CC\u79BB");
		panel.setLayout(new MigLayout("", "[73px][2px][142px]", "[23px][23px][23px][23px]"));
		panel.add(chcbxkaishi, "cell 0 0,alignx left,aligny top");
		panel.add(chcbxjieshu, "cell 2 0,alignx left,aligny top");
		panel.add(chcbxmx, "cell 0 1 3 1,alignx left,aligny top");
		panel.add(chcbxfantan, "cell 0 2 3 1,alignx left,aligny top");
		panel.add(chckbxmacd, "cell 0 3 3 1,alignx left,aligny top");
		
		txaExaComments = new JTextArea();
		scrollPane_6.setViewportView(txaExaComments);
		
		txaAnalysisComments = new JTextArea();
		
		txaAnalysisComments.setLineWrap(true);
		scrollPane_5.setViewportView(txaAnalysisComments);
		
		GeGuCheckListsTableModel tablegpcmodel = new GeGuCheckListsTableModel ();
		tablegpcmodel.setColumnName("所属股票池");
		tableGuPiaoChi = new JTable(tablegpcmodel) {
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
		scrollPane_4.setViewportView(tableGuPiaoChi);
		
		GeGuCheckListsTableModel tabletichaimoodel = new GeGuCheckListsTableModel ();
		tabletichaimoodel.setColumnName("题材分析");
		tableTiCai = new JTable(tabletichaimoodel) {
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
		scrollPane_3.setViewportView(tableTiCai);
		
		GeGuCheckListsTableModel tablejishumodel = new GeGuCheckListsTableModel ();
		tablejishumodel.setColumnName("技术与模型分析");
		tablejishu = new JTable(tablejishumodel) {
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
		scrollPane_2.setViewportView(tablejishu);
		
		GeGuCheckListsTableModel tablegudongmodel = new GeGuCheckListsTableModel ();
		tablegudongmodel.setColumnName("股东分析");
		tablegudong = new JTable(tablegudongmodel) {
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
		scrollPane_1.setViewportView(tablegudong);
		
		GeGuCheckListsTableModel tableFinancialmodel = new GeGuCheckListsTableModel ();
		tableFinancialmodel.setColumnName("财务分析");
		tableFinancial = new JTable(tableFinancialmodel) {
			/**
			 * 
			 */
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
						.addGap(204)
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
			);
			buttonPane.setLayout(gl_buttonPane);
		}
		
	}
}


class GeGuCheckListsTableModel extends AbstractTableModel 
{
	String[] jtableTitleStrings = { "ID","选中", "描述","分析结果"};
	private ArrayList<ZdgzItem> zdgzitems;
	
	GeGuCheckListsTableModel ()
	{
	}
	
	public void refresh  (ArrayList<ZdgzItem> zdgzitems2 )
	{
		this.zdgzitems = zdgzitems2;
		this.fireTableDataChanged();
	}
	
	public ZdgzItem getPolicyZdgzItem (int rowIndex) 
	{
		return this.zdgzitems.get(rowIndex);
	}
	
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
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	Object value = "??";
	    	ZdgzItem zdgzitem = zdgzitems.get(rowIndex);
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
