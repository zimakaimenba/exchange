package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.bankuaichanyelian.BkChanYeLianTree;
import com.exchangeinfomanager.bankuaichanyelian.HanYuPinYing;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ImageIcon;
import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import com.toedter.components.JLocaleChooser;
import com.toedter.calendar.JDateChooser;
import javax.swing.JCheckBox;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.beans.PropertyChangeEvent;
import javax.swing.JTable;
import java.awt.event.MouseAdapter;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.GridLayout;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.BoxLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BanKuaiFengXi extends JDialog {

	

	/**
	 * Create the dialog.
	 */
	public BanKuaiFengXi(BkChanYeLianTree bkcyltree2,String fenxibk,String fenxistock,Date fxdate)
	{
		initializeGui ();
		this.bkcyltree = bkcyltree2; 
//		if(parsedfilename2 != null)
//			this.tfldparsedfile.setText(parsedfilename2);
		this.bkdbopt = new BanKuaiDbOperation ();
		dateChooser.setDate(fxdate);
		tfldparsedfile.setText(fenxibk + "," + fenxistock);
		
		createEvents ();
		allbkandzs = bkdbopt.getTDXAllZhiShuAndBanKuai ();
		initializeBanKuaiZhanBiByGrowthRate ();
		
	}
	
	private BkChanYeLianTree bkcyltree;
	private BanKuaiDbOperation bkdbopt;
	private HashMap<String, BanKuai> allbkandzs;
	
	/*
	 * 所有板块占比增长率的排名
	 */
	private void initializeBanKuaiZhanBiByGrowthRate ()
	{
		Date endday = CommonUtility.getLastDayOfWeek(dateChooser.getDate() );
    	Date startday = CommonUtility.getFirstDayOfWeek( CommonUtility.getDateOfSpecificMonthAgo(dateChooser.getDate() ,6) );
    	
    	
    	HashMap<String, BanKuai> bkhascjl = new HashMap<String, BanKuai> ();
    	for (Entry<String, BanKuai> entry : allbkandzs.entrySet()) {
    		String bkcode = entry.getKey();
    		BanKuai bankuai = entry.getValue();
    		bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,startday,endday);
    		if(bankuai.getChenJiaoErZhanBiInGivenPeriod() != null )
    			bkhascjl.put(bkcode, bankuai);

    	}
    	((BanKuaiFengXiZhanBiPaiMingTableModel)tableBkZhanBi.getModel()).refresh(bkhascjl,CommonUtility.getWeekNumber(dateChooser.getDate() ));

	}
	

	private void createEvents() 
	{ 
		tflddwbk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				int rowindex = ((BanKuaiFengXiZhanBiPaiMingTableModel)tableBkZhanBi.getModel()).getBanKuaiRowIndex(tflddwbk.getText().trim());
				if(rowindex != -1) {
					tableBkZhanBi.setRowSelectionInterval(rowindex, rowindex);
					tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(rowindex, 0, true)));
				}
			}
		});
		tflddingweigegu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		
		
		tableGuGuZhanBiInBk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int row = tableGuGuZhanBiInBk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				Stock selectstock = ((GeGuFengXiZhanBiPaiMingTableModel)tableGuGuZhanBiInBk.getModel()).getStock (row);
				panelgeguwkzhanbi.setBanKuaiWithDaPanNeededDisplay(selectstock);
				
				 String stockcode = selectstock.getMyOwnCode(); 
				 try {
					 String stockname = selectstock.getMyOwnName(); 
					 pnllastestggzhanbi.hightlightSpecificSector (stockcode+stockname);
					 panelLastWkGeGuZhanBi.hightlightSpecificSector (stockcode+stockname);
				 } catch ( java.lang.NullPointerException e) {
					 pnllastestggzhanbi.hightlightSpecificSector (stockcode);
					 panelLastWkGeGuZhanBi.hightlightSpecificSector (stockcode);
				 }
				
			}
		});
		
		btnexportbk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		
		
		tableBkZhanBi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tableBkZhanBi.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				//板块自身占比
				BanKuai selectedbk = ((BanKuaiFengXiZhanBiPaiMingTableModel)tableBkZhanBi.getModel()).getBanKuai(row);
				panelbkwkzhanbi.setBanKuaiWithDaPanNeededDisplay(selectedbk);
				
				//更新板块所属个股
				String tdxbk = selectedbk.getMyOwnName();
				String bkcode = selectedbk.getMyOwnCode();
				
				HashMap<String, Stock> tmpallbkge = null;
	  	        if( selectedbk.checkSavedStockListIsTheSame(dateChooser.getDate() ) )
	  	        	tmpallbkge = selectedbk.getBanKuaiGeGu ();
	  	        else {
		  	        Date startdate = CommonUtility.getFirstDayOfWeek(dateChooser.getDate() );
		  	        Date lastdate = CommonUtility.getLastDayOfWeek(dateChooser.getDate() );
			  	  	tmpallbkge = bkdbopt.getTDXBanKuaiGeGuOfHyGnFgAndChenJiaoLIang (tdxbk,bkcode,startdate,lastdate);
			  	  selectedbk.setBanKuaiGeGu(tmpallbkge);
	  	        }
				
				for (Entry<String, Stock> entry : tmpallbkge.entrySet()) {
		    		String stockcode = entry.getKey();
		    		Stock stock = entry.getValue();
		    		
		    		Date endday = CommonUtility.getLastDayOfWeek(dateChooser.getDate() );
		        	Date startday = CommonUtility.getFirstDayOfWeek( CommonUtility.getDateOfSpecificMonthAgo(dateChooser.getDate() ,6) );
		    		
		        	stock = bkdbopt.getGeGuZhanBiOfBanKuai (bkcode,stock,startday,endday);
		    	}
				
				((GeGuFengXiZhanBiPaiMingTableModel)tableGuGuZhanBiInBk.getModel()).refresh(bkcode, tmpallbkge, CommonUtility.getWeekNumber(dateChooser.getDate() ) );
				
				//显示2周的板块个股pie chart
				pnllastestggzhanbi.setBanKuaiNeededDisplay(selectedbk,Integer.parseInt(tfldweight.getText() ),CommonUtility.getWeekNumber(dateChooser.getDate() ) );
				panelLastWkGeGuZhanBi.setBanKuaiNeededDisplay(selectedbk,Integer.parseInt(tfldweight.getText() ),CommonUtility.getWeekNumber(dateChooser.getDate() ) -1 );
				
				// 个股半年占比要清空
				panelgeguwkzhanbi.resetDate();
				
			}
		});
		
		dateChooser.addPropertyChangeListener(new PropertyChangeListener() {
		    @Override
		    public void propertyChange(PropertyChangeEvent e) {
		    	if("date".equals(e.getPropertyName())) {
		    		
		    		panelbkwkzhanbi.resetDate();
		    		panelgeguwkzhanbi.resetDate();
		    		pnllastestggzhanbi.resetDate();
		    		panelLastWkGeGuZhanBi.resetDate();
		    		((GeGuFengXiZhanBiPaiMingTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
		    		initializeBanKuaiZhanBiByGrowthRate ();
		    		
		    		
		    	}
//		        System.out.println(e.getPropertyName()+ ": " + e.getNewValue());
		    }
		});
		
	}

	  protected void diaplayGeGuOfBanKuai(String bankcode, String bankname, Date bkfxdate) 
	  {
		  TreePath bkpath = bkcyltree.locateNodeByNameOrHypyOrBkCode (bankcode);
		  bkcyltree.setSelectionPath(bkpath);
		  BkChanYeLianTreeNode knode = (BkChanYeLianTreeNode)bkcyltree.getLastSelectedPathComponent();
		  
		
	  }




	



	private final JPanel contentPanel = new JPanel();
	private JTextField tfldparsedfile;
	private JButton btnchosefile;
	private JButton okButton;
	private JButton cancelButton;
	private JDateChooser dateChooser;
	private JScrollPane sclpleft;
	private JTable tableBkZhanBi;
	private JTable tableGuGuZhanBiInBk;
	private BanKuaiFengXiBarChartPnl panelbkwkzhanbi;
	private JButton btnexportbk;
	private JTextField tfldweight;
	private BanKuaiFengXiBarChartPnl panelgeguwkzhanbi;
	private BanKuaiFengXiPieChartPnl pnllastestggzhanbi;
	private JPanel panel_7;
	private BanKuaiFengXiPieChartPnl panelLastWkGeGuZhanBi;
	private JTextField tflddwbk;
	private JTextField tflddingweigegu;
	
	private void initializeGui() {
		setTitle("\u677F\u5757\u5206\u6790");
		setBounds(100, 100, 1870, 1002);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		
		JPanel panel_1 = new JPanel();
		
		panelbkwkzhanbi = new BanKuaiFengXiBarChartPnl();
		panelbkwkzhanbi.setBorder(new TitledBorder(null, "\u677F\u5757\u534A\u5E74\u5185\u5468\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(null);
		
		pnllastestggzhanbi = new BanKuaiFengXiPieChartPnl();
		pnllastestggzhanbi.setBorder(new TitledBorder(null, "\u677F\u5757\u5F53\u524D\u5468\u4E2A\u80A1\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, "Test", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panel_7 = new JPanel();
		panel_7.setBorder(null);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 1822, Short.MAX_VALUE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 254, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(panelbkwkzhanbi, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 777, GroupLayout.PREFERRED_SIZE)
									.addGap(28))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(pnllastestggzhanbi, GroupLayout.PREFERRED_SIZE, 508, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(panel_7, GroupLayout.PREFERRED_SIZE, 507, GroupLayout.PREFERRED_SIZE)
									.addGap(4)
									.addComponent(panel_5, GroupLayout.PREFERRED_SIZE, 545, GroupLayout.PREFERRED_SIZE)))
							.addContainerGap())))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(panelbkwkzhanbi, GroupLayout.PREFERRED_SIZE, 410, GroupLayout.PREFERRED_SIZE)
								.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 410, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_5, GroupLayout.PREFERRED_SIZE, 413, GroupLayout.PREFERRED_SIZE)
								.addComponent(pnllastestggzhanbi, GroupLayout.PREFERRED_SIZE, 409, GroupLayout.PREFERRED_SIZE)
								.addComponent(panel_7, GroupLayout.PREFERRED_SIZE, 417, GroupLayout.PREFERRED_SIZE))
							.addContainerGap())
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
		);
		panel_7.setLayout(new BoxLayout(panel_7, BoxLayout.X_AXIS));
		
		panelLastWkGeGuZhanBi = new BanKuaiFengXiPieChartPnl();
		panelLastWkGeGuZhanBi.setBorder(new TitledBorder(null, "\u677F\u5757\u4E0A\u4E00\u5468\u4E2A\u80A1\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_7.add(panelLastWkGeGuZhanBi);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		panelgeguwkzhanbi = new BanKuaiFengXiBarChartPnl();
		panelgeguwkzhanbi.setBorder(new TitledBorder(null, "\u4E2A\u80A1\u534A\u5E74\u5185\u5468\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.add(panelgeguwkzhanbi, BorderLayout.CENTER);
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablelast2 = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablelast2gr = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablelast = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablelastgr = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablecurwk = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablecurwkgr = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		sclpleft = new JScrollPane();
		
		JScrollPane scrollPane_6 = new JScrollPane();
		
		btnexportbk = new JButton("\u5BFC\u51FA\u677F\u5757");
		
		JButton button = new JButton("\u5BFC\u51FA\u4E2A\u80A1");
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("\u5254\u9664\u80A1\u7968\u6743\u91CD<=");
		
		tfldweight = new JTextField();
		tfldweight.setText("0");
		tfldweight.setColumns(10);
		
		tflddwbk = new JTextField();
		
		tflddwbk.setColumns(10);
		
		tflddingweigegu = new JTextField();
		
		tflddingweigegu.setColumns(10);
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(chckbxNewCheckBox)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfldweight, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE))
						.addComponent(scrollPane_6, GroupLayout.PREFERRED_SIZE, 234, GroupLayout.PREFERRED_SIZE)
						.addComponent(sclpleft, GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(tflddwbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
							.addComponent(btnexportbk))
						.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
							.addComponent(tflddingweigegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
							.addComponent(button)))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(6)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(tflddwbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnexportbk))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(sclpleft, GroupLayout.PREFERRED_SIZE, 374, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(chckbxNewCheckBox)
						.addComponent(tfldweight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_6, GroupLayout.PREFERRED_SIZE, 334, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(button)
						.addComponent(tflddingweigegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(21, Short.MAX_VALUE))
		);
		
		GeGuFengXiZhanBiPaiMingTableModel ggzb = new GeGuFengXiZhanBiPaiMingTableModel ();
		tableGuGuZhanBiInBk = new JTable(ggzb){
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
		
		scrollPane_6.setViewportView(tableGuGuZhanBiInBk);
		
		BanKuaiFengXiZhanBiPaiMingTableModel bkzb = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		tableBkZhanBi = new JTable(bkzb){
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
		
		
		sclpleft.setViewportView(tableBkZhanBi);
		panel_1.setLayout(gl_panel_1);
		
		JLabel lblNewLabel = new JLabel("\u89E3\u6790\u677F\u5757\u6587\u4EF6");
		
		tfldparsedfile = new JTextField();
		tfldparsedfile.setColumns(10);
		
		btnchosefile = new JButton("");
		btnchosefile.setIcon(new ImageIcon(BanKuaiFengXi.class.getResource("/images/open24.png")));
		
		dateChooser = new JDateChooser();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(7)
					.addComponent(lblNewLabel)
					.addGap(4)
					.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 621, GroupLayout.PREFERRED_SIZE)
					.addGap(4)
					.addComponent(btnchosefile)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE)
					.addGap(835))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(16)
							.addComponent(lblNewLabel))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(7)
							.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(7)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(dateChooser, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnchosefile, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
					.addContainerGap(8, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
			}
			
			JLabel lblNewLabel_1 = new JLabel("New label");
			buttonPane.setLayout(new MigLayout("", "[105px][54px][951px][71px][84px]", "[34px]"));
			buttonPane.add(okButton, "cell 3 0,growx,aligny center");
			buttonPane.add(cancelButton, "cell 4 0,growx,aligny center");
			buttonPane.add(lblNewLabel_1, "cell 2 0,grow");
		}
		

	}
}


class BanKuaiFengXiZhanBiPaiMingTableModel extends AbstractTableModel 
{
	String[] jtableTitleStrings = { "板块代码", "板块名称","占比增长率"};
//	HashMap<String,BanKuai> bkmap;
	List<Map.Entry<String, BanKuai>> entryList;
	int showzhbiwknum;
	
	BanKuaiFengXiZhanBiPaiMingTableModel ()
	{
		
	}

	public void refresh  (HashMap<String,BanKuai> curbkzslist, int weeknumber)
	{
//		this.bkmap = curbkzslist;
		showzhbiwknum = weeknumber;
		entryList = new ArrayList<Map.Entry<String, BanKuai>>(curbkzslist.entrySet());

        Collections.sort(entryList, new Comparator<Map.Entry<String, BanKuai>>() {
            @Override
            public int compare(Map.Entry<String, BanKuai> bk1,
                               Map.Entry<String, BanKuai> bk2) {
            	
                return bk1.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showzhbiwknum)
                        .compareTo(bk2.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showzhbiwknum));
            }
            }
        );

        this.fireTableDataChanged();
	}
	
	 
	
	 public int getRowCount() 
	 {
//		 if(this.bkmap == null)
//			 return 0;
//		 else 
//			 return this.bkmap.size();
		 if(entryList == null)
			 return 0;
		 else
			 return entryList.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
//	    	if(bkmap.isEmpty())
//	    		return null;
//	    	
//	    	String[] bkcodeArray = bkmap.keySet().toArray(new String[bkmap.keySet().size()]);
//	    	String bkcode = bkcodeArray[rowIndex];
//	    	BanKuai thisbk = bkmap.get(bkcode);
//	    	String thisbkname = thisbk.getMyOwnName(); 
//	    	Double zhanbigrowthrate = thisbk.getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod ();
//	    	NumberFormat percentFormat = NumberFormat.getPercentInstance();
//	    	
//	    	Object value = "??";
//	    	switch (columnIndex) {
//            case 0:
//                value = bkcode;
//                break;
//            case 1: 
//            	value = thisbkname;
//            	break;
//            case 2:
//            	value = percentFormat.format(zhanbigrowthrate);
//            	break;
//	    	}
	    	
	    	if(entryList.isEmpty() )
	    		return null;
	    	
	    	Entry<String, BanKuai> thisbk = entryList.get(entryList.size()-1-rowIndex);
	    	String bkcode = thisbk.getValue().getMyOwnCode();
	    	String thisbkname = thisbk.getValue().getMyOwnName(); 
	    	Double zhanbigrowthrate = thisbk.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showzhbiwknum);
	    	NumberFormat percentFormat = NumberFormat.getPercentInstance();
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = bkcode;
                break;
            case 1: 
            	value = thisbkname;
            	break;
            case 2:
            	value = percentFormat.format(zhanbigrowthrate);
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
	    
	    public String getColumnName(int column) { 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    public String getBanKuaiCode (int row) 
	    {
	    	return (String)this.getValueAt(row,0);
	    }
	    public String getBanKuaiName (int row) 
	    {
	    	return (String)this.getValueAt(row,1);
	    } 
	    public BanKuai getBanKuai (int row)
	    {
	    	String bkcode = this.getBanKuaiCode(entryList.size()-1-row);
//	    	return bkmap.get(bkcode);
	    	return this.entryList.get(entryList.size()-1-row).getValue();
	    }
	    public int getBanKuaiRowIndex (String neededfindstring) 
	    {
	    		int index = -1;
	    		HanYuPinYing hypy = new HanYuPinYing ();
	    		
	    		for(int i=0;i<this.getRowCount();i++) {
	    			String bkcode = (String)this.getValueAt(i, 0);
	    			String bkname = (String)this.getValueAt(i,1); 
	    			if(bkcode.trim().equals(neededfindstring) ) {
	    				index = i;
	    				break;
	    			}

	    			String namehypy = hypy.getBanKuaiNameOfPinYin(bkname );
			   		if(namehypy.toLowerCase().equals(neededfindstring.trim().toLowerCase())) {
			   			index = i;
			   			break;
			   		}
	    		}
	    	
	   		
	   		return index;
	    }
	    
}


class GeGuFengXiZhanBiPaiMingTableModel extends AbstractTableModel 
{
	String[] jtableTitleStrings = { "股票代码", "股票名称","权重","占比增长率"};
//	HashMap<String,Stock> stockmap;
	String curbkcode;
	private ArrayList<Entry<String, Stock>> entryList;
	int showwknum;
	
	GeGuFengXiZhanBiPaiMingTableModel ()
	{
		
	}

	public void refresh  (String bkcode, HashMap<String,Stock> stockmap1,int wknum)
	{
		this.curbkcode = bkcode;
		this.showwknum = wknum;
		
		entryList = new ArrayList<Map.Entry<String, Stock>>(stockmap1.entrySet());

        Collections.sort(entryList, new Comparator<Map.Entry<String, Stock>>() {
            @Override
            public int compare(Map.Entry<String, Stock> integerEmployeeEntry,
                               Map.Entry<String, Stock> integerEmployeeEntry2) {
                return integerEmployeeEntry.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showwknum)
                        .compareTo(integerEmployeeEntry2.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showwknum));
            }
            }
        );
		this.fireTableDataChanged();
	}

	 public int getRowCount() 
	 {
//		 if(this.stockmap == null)
//			 return 0;
//		 else 
//			 return this.stockmap.size();
		 if(entryList == null)
			 return 0;
		 else 
			 return entryList.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
//	    	if(stockmap.isEmpty())
//	    		return null;
//	    	
//	    	String[] stockcodeArray = stockmap.keySet().toArray(new String[stockmap.keySet().size()]);
//	    	String stockcode = stockcodeArray[rowIndex];
//	    	Stock thisstock = stockmap.get(stockcode);
//	    	String thisstockname = thisstock.getMyOwnName();
//	    	HashMap<String, Integer> ggweight = thisstock.getGeGuSuoShuBanKuaiWeight();
//	    	Integer weight = ggweight.get(curbkcode);
//	    	Double zhanbigrowthrate = thisstock.getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod ();
//	    	
//	    	NumberFormat percentFormat = NumberFormat.getPercentInstance();
//	    	
//	    	Object value = "??";
//	    	switch (columnIndex) {
//            case 0:
//                value = stockcode;
//                break;
//            case 1: 
//            	value = thisstockname;
//            	break;
//            case 2: 
//            	value = weight;
//            	break;	
//            case 3:
//            	value = percentFormat.format(zhanbigrowthrate);
//            	break;
//	    	}
	    	
	    	if(entryList.isEmpty())
	    		return null;
	    	Entry<String, Stock> thisbk = entryList.get(entryList.size()-1-rowIndex);
	    	String bkcode = thisbk.getValue().getMyOwnCode();
	    	String thisbkname = thisbk.getValue().getMyOwnName(); 
	    	Double zhanbigrowthrate = thisbk.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showwknum);
	    	NumberFormat percentFormat = NumberFormat.getPercentInstance();
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = bkcode;
                break;
            case 1: 
            	value = thisbkname;
            	break;
            case 2: 
            	value = 10;
            	break;
            case 3:
            	value = percentFormat.format(zhanbigrowthrate);
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
			          clazz = Integer.class;
			          break;
		        case 3:
			          clazz = String.class;
			          break;
		      }
		      
		      return clazz;
	 }
	    
	    public String getColumnName(int column) { 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    public String getStockCode (int row) 
	    {
	    	return (String)this.getValueAt(row,0);
	    }
	    public String getStockName (int row) 
	    {
	    	return (String)this.getValueAt(row,1);
	    } 
	    public String getStockWeight (int row) 
	    {
	    	return (String)this.getValueAt(row,2);
	    } 
	    public Stock getStock (int row)
	    {
	    	String stockcode = this.getStockCode(row);
	    	return this.entryList.get(entryList.size()-1-row).getValue();
	    }
	    public void deleteAllRows ()
	    {	
	    	if(this.entryList == null)
				 return ;
			 else 
				 entryList.clear();
	    	this.fireTableDataChanged();
	    	
	    }
	    
	    

}