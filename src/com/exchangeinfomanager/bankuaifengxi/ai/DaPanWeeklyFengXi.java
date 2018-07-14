package com.exchangeinfomanager.bankuaifengxi.ai;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTable;
import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;

import com.exchangeinfomanager.asinglestockinfo.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartMultiCjeZhanbiPnl;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.github.cjwizard.WizardPage;

import java.awt.Color;

public class DaPanWeeklyFengXi extends WeeklyFenXiWizardPage 
{
	/*
	 * 
	 */
	public DaPanWeeklyFengXi(String title, String description, BkChanYeLianTreeNode displaynode2,LocalDate selectdate2,WeeklyFengXiXmlHandler dpfxmlhandler) 
	{
		super(title,description,displaynode2,selectdate2,dpfxmlhandler);
		
		initializeGui ();
		createEvents();
		
		dateChooser.setLocalDate(selectdate2);
		
		showZhiShuFenXiResults ();
	}
	/*
	 * 
	 */
	private void showZhiShuFenXiResults() 
	{
		((DaPanTableModel)tablebk.getModel()).refresh(zdgzinfo, displaydate);
		txaComments.setText(zdgzinfo.getDaPanAnalysisComments() );
		
		ArrayList<BkChanYeLianTreeNode> zhishulist = ((DaPanTableModel)tablebk.getModel()).getZdgzZhiShuNodeList(); 

		LocalDate upstartdate = displaydate.minus(30,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
		LocalDate upenddate = displaydate.minus(16,ChronoUnit.WEEKS).with(DayOfWeek.SATURDAY);
		pnlup.updatedMultiDate(zhishulist, upstartdate, upenddate, StockGivenPeriodDataItem.WEEK);
		
		LocalDate dwnstartdate = displaydate.minus(15,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
		LocalDate dwnenddate = displaydate.with(DayOfWeek.SATURDAY);
		pnldown.updatedMultiDate(zhishulist, dwnstartdate, dwnenddate, StockGivenPeriodDataItem.WEEK);
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
		
		btnremv.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int row = tablebk.getSelectedRow();
				if(row != -1) {
					((DaPanTableModel)tablebk.getModel()).deleteZhiShu(row);
					int action = JOptionPane.showConfirmDialog(null, "是否同时将该指数从XML Matrix文件中删除？","警告", JOptionPane.YES_NO_OPTION);
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
		
		btnadd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String zhishucode = tfldbkcode.getText();
				if( Pattern.matches("\\d{6}$",zhishucode)   ) {
					BanKuai bknode = (BanKuai)allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(zhishucode, BanKuaiAndStockBasic.TDXBK);
					if(bknode !=null) {
						((DaPanTableModel)tablebk.getModel()).addNewZhiShu(bknode);
						
						int action = JOptionPane.showConfirmDialog(null, "是否同时将该指数加入到XML Matrix文件中？","警告", JOptionPane.YES_NO_OPTION);
						if(0 == action) {
							PropertyChangeEvent evt = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_ADDED, "", bknode);
					        pcs.firePropertyChange(evt);
					        
					        PropertyChangeEvent evt2 = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLMATRIX_ADDED, "", bknode);
					        pcs.firePropertyChange(evt2);
					        
						} else {
							PropertyChangeEvent evt = new PropertyChangeEvent(this, WeeklyFengXiXmlHandler.XMLINDB_ADDED, "", bknode);
					        pcs.firePropertyChange(evt);
						}
							
					}
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
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.bankuaifengxi.ai.WeeklyFenXiWizardPage#getWeeklyFenXiComments()
	 * 大盘weeklyfengxi
	 */
	public String getWeeklyFenXiComments() 
	{
		return txaComments.getText();
	}
	
	private final JPanel contentPanel = new JPanel();
	private JTable tablebk;
	private JTextField tfldbkcode;
	private JPanel buttonPane;
	private BanKuaiFengXiCategoryBarChartMultiCjeZhanbiPnl pnldown;
	private BanKuaiFengXiCategoryBarChartMultiCjeZhanbiPnl pnlup;
	private JButton btnxmlMartrix;
	private JButton btnremv;
	private JButton btnadd;
	private JTextArea txaComments;
	private JLocalDateChooser dateChooser;
	
	private void initializeGui() 
	{
		setBounds(100, 100, 1569, 806);
		setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.add(contentPanel, BorderLayout.CENTER);
		
		pnlup = new BanKuaiFengXiCategoryBarChartMultiCjeZhanbiPnl();
		
		pnldown = new BanKuaiFengXiCategoryBarChartMultiCjeZhanbiPnl();
		
		JScrollPane scrollPane = new JScrollPane();
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		DaPanTableModel nodecomparetablemode = new DaPanTableModel (); 
		tablebk = new JTable(nodecomparetablemode) {
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
		scrollPane.setViewportView(tablebk);
		
		txaComments = new JTextArea();
		txaComments.setLineWrap(true);
		scrollPane_1.setViewportView(txaComments);
//		{
			buttonPane = new JPanel();
			
			tfldbkcode = new JTextField();
			tfldbkcode.setColumns(10);
			
			btnadd = new JButton("\u52A0\u5165\u5206\u6790");
			
			btnremv = new JButton("\u5220\u9664");
			
			JCheckBox chbxcjezhanbi = new JCheckBox("\u6210\u4EA4\u989D\u5360\u6BD4\u6BD4\u8F83");
			chbxcjezhanbi.setSelected(true);
			
			btnxmlMartrix = new JButton("\u6253\u5F00XML Martiix\u6587\u4EF6");
			
			dateChooser = new JLocalDateChooser();
			dateChooser.setEnabled(false);
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addComponent(tfldbkcode, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnadd)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnremv)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(btnxmlMartrix)
						.addGap(573)
						.addComponent(chbxcjezhanbi)
						.addGap(39)
						.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
						.addGap(278))
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(5)
						.addGroup(gl_buttonPane.createParallelGroup(Alignment.TRAILING)
							.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(tfldbkcode, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnadd)
								.addComponent(btnremv)
								.addComponent(chbxcjezhanbi)
								.addComponent(btnxmlMartrix))))
			);
			buttonPane.setLayout(gl_buttonPane);
//		}
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(buttonPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 1559, Short.MAX_VALUE)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(pnldown, GroupLayout.DEFAULT_SIZE, 1542, Short.MAX_VALUE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 527, GroupLayout.PREFERRED_SIZE)
							.addGap(8)
							.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 328, GroupLayout.PREFERRED_SIZE))
						.addComponent(pnlup, GroupLayout.DEFAULT_SIZE, 1542, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addComponent(pnlup, GroupLayout.PREFERRED_SIZE, 267, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(pnldown, GroupLayout.PREFERRED_SIZE, 264, GroupLayout.PREFERRED_SIZE)
					.addGap(4)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(buttonPane, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		contentPanel.setLayout(gl_contentPanel);
	}
}


class DaPanTableModel extends AbstractTableModel 
{
//	private List<BkChanYeLianTreeNode> dapan;
	private String[] jtableTitleStrings = { "指数代码", "指数名称", "成交额","成交额占比"};
	private LocalDate showdate;
	private String xmltagbelonged;
	private ZhongDianGuanZhu zdgzinfo;
	private ArrayList<BkChanYeLianTreeNode> dapanzhishu;
	
	DaPanTableModel ()
	{
	}
	
	public void refresh (ZhongDianGuanZhu zdgzinfo2,LocalDate showdate2) 
	{
		this.zdgzinfo = zdgzinfo2;
		
		this.showdate = showdate2;
		
		ArrayList<ZdgzItem> zhishulist = zdgzinfo.getDapanZhiShuLists();
		AllCurrentTdxBKAndStoksTree allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
		this.dapanzhishu = new ArrayList<BkChanYeLianTreeNode>();
		for( ZdgzItem zhishuitem :  zhishulist) {
			String zhishucode  = zhishuitem.getValue();
			BanKuai dapanitem = allbksks.getBanKuai(zhishucode, this.showdate, StockGivenPeriodDataItem.WEEK);
			this.dapanzhishu.add(dapanitem);
		}
		
		this.xmltagbelonged = zhishulist.get(0).getXmlTagBelonged();
		
		this.fireTableDataChanged();
	}
	public ArrayList<BkChanYeLianTreeNode> getZdgzZhiShuNodeList ()
	{
		return this.dapanzhishu;
	}
		
	public void addNewZhiShu (BanKuai zhishu)
	{
		this.dapanzhishu.add(zhishu);
		
		ArrayList<ZdgzItem> zhishulist = zdgzinfo.getDapanZhiShuLists();
		ZdgzItem zhishuitem = new ZdgzItem ("NEW",this.xmltagbelonged);
		zhishuitem.setValue(zhishu.getMyOwnCode());
		zhishuitem.setContents(zhishu.getMyOwnName());
		zhishulist.add(zhishuitem);
		
		this.fireTableDataChanged();
	}
	public void deleteZhiShu (int row)
	{
		ArrayList<ZdgzItem> zhishulist = zdgzinfo.getDapanZhiShuLists();
		zhishulist.remove(row);
		
		this.fireTableDataChanged();
	}

	 public int getRowCount() 
	 {
		 
		 if(this.zdgzinfo == null)
	        return 0;
		 else
			 return this.zdgzinfo.getDapanZhiShuLists().size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	Object value = "??";
	    	
//	    	AllCurrentTdxBKAndStoksTree allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
//			ArrayList<ZdgzItem> zhishulist = zdgzinfo.getDapanZhiShuLists();
//			
//			String zhishucode = zhishulist.get(rowIndex).getValue();
			BanKuai dapanitem = (BanKuai)this.dapanzhishu.get(rowIndex);
				
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