package com.exchangeinfomanager.bankuaichanyelian;

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

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.collect.Ordering;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

//import net.ginkgo.copy.Ginkgo2;
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
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.border.TitledBorder;
import javax.swing.JTextField;
import com.toedter.calendar.JDateChooser;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;


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
	public BanKuaiGuanLi(StockInfoManager stockInfoManager2, BanKuaiAndChanYeLian bkcyl) 
//	public BanKuaiGuanLi( BanKuaiAndChanYeLian bkcyl)
	{
		sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
//		this.stockdbopt = stockdbopt2;
//		this.cylxmlhandler = cylxmlhandler2;
//		this.zdgzbkxmlhandler = zdgzbkxmlhandler2;
		this.stockInfoManager = stockInfoManager2;
		this.bkcylpnl = bkcyl;
		initializeGui ();
		createEvents ();
//		zhishulist = bkdbopt.getTDXAllZhiShuList ("all");
		sysbankuailist = bkdbopt.getTDXBanKuaiList ("all"); 
//		initializeTDXBanKuaiLists ();
//		initializeTDXZhiShuLists ();
//		initialzieZdyBanKuaList ();
//		initializeGephi ();
		
//		startDialog ();
	}
	


//	private TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler;
	private StockInfoManager stockInfoManager;	
	private BanKuaiDbOperation bkdbopt;
	HashMap<String,BanKuai> zhishulist;
	HashMap<String,BanKuai> sysbankuailist ; 
	private SystemConfigration sysconfig;
//	private StockDbOperations stockdbopt;
//	private ChanYeLianXMLHandler2 cylxmlhandler;

//
//	public void startDialog ()
//	{
//		initializeGui ();
//		createEvents ();
//		initializeTDXBanKuaiLists ();
//		initializeTDXZhiShuLists ();
//		initialzieZdyBanKuaList ();
//	}

	private void initializeTDXZhiShuLists() 
	{		
		((BanKuaiDetailTableModel)tablezhishu.getModel()).refresh(zhishulist);
	}

	private void initialzieZdyBanKuaList() 
	{		
//		((BanKuaiDetailTableModel)tableZdy.getModel()).refresh(zhishulist);
		
	}
	private void initializeTDXBanKuaiLists() 
	{
		 
		
		((BanKuaiDetailTableModel)tableSysBk.getModel()).refresh(sysbankuailist);
	}

	private void initializeGephi() {
		String parsedpath = sysconfig.getGephiFileExportPath ();
		tfldfilepath.setText(parsedpath);
		
	}
	/*
	 * 
	 */
	private void writeBanKuaiAndGeGuNodeFile (HashMap<String,BanKuai> allbkandzs, HashMap<String,BanKuai> sysbankuailist)
	{
		String parsedpath = sysconfig.getGephiFileExportPath ();
		String bkggnodecsvfile = parsedpath + "板块个股GEPHINodes列表" 
							+ CommonUtility.formatDateYYMMDD(dateChooser.getDate()).replaceAll("-", "")
							+ ".csv"
							;
		
		File csvfile = new File(bkggnodecsvfile);
		if(csvfile.exists())
			csvfile.delete();
		
		List<String[]> contentArrayList = new ArrayList<>();
		String[] titlestrng = {"id","label","bkgg","amount","zhangfu"};
		contentArrayList.add(titlestrng);
		
		List<String[]> listofcsv = new ArrayList<>();
//		HashMap<String, BanKuai> allbkandzs = bkdbopt.getTDXAllZhiShuAndBanKuai ();
		for (Entry<String, BanKuai> entry : allbkandzs.entrySet()) {
			String bkcode = entry.getKey();
			BanKuai bankuai = entry.getValue();
			String bkname = bankuai.getMyOwnName();
			if(bkname == null) bkname = bkcode;
					
			String[] ss = {bkcode,bkname,"板块","",""};
			listofcsv.add(ss);

		}
		
		for (Entry<String, BanKuai> entry : sysbankuailist.entrySet()) {
			String bkcode = entry.getKey();
			BanKuai bankuai = entry.getValue();
			String bkname = bankuai.getMyOwnName();
			if(bkname == null) bkname = bkcode;
					
			String[] ss = {bkcode,bkname,"板块","",""};
			listofcsv.add(ss);

		}
		
		HashMap<String, Stock> stockmap = bkdbopt.getAllStocks ();
		for (Entry<String, Stock> entry : stockmap.entrySet()) {
			String stockcode = entry.getKey();
			Stock bankuai = entry.getValue();
			String stockname = bankuai.getMyOwnName();
			if(stockname == null) stockname = stockcode;
					
			String[] ss = {stockcode,stockname,"个股","",""};
			listofcsv.add(ss);
		}
		
//	    try {
//			CSVWriter writer = new CSVWriter(new FileWriter(bkggnodecsvfile), '\t');
//			writer.writeAll(contentArrayList);
//			writer.writeAll(listofcsv);
//			 writer.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		CSVWriter writer = null;  
		String  characters = ",";
	    try {
		    if (",".equalsIgnoreCase(characters)){
		    	//初始化CSVWriter
		        writer = new CSVWriter(new FileWriter(csvfile),',',CSVWriter.NO_QUOTE_CHARACTER);  
		    } else{
		    	//初始化CSVWriter，带分隔符
		        writer = new CSVWriter(new FileWriter(csvfile),characters.toCharArray()[0],CSVWriter.NO_QUOTE_CHARACTER);  
		    }
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }

	    try {
		    writer.writeAll(contentArrayList);
		    writer.writeAll(listofcsv); 
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void writeBanKuaiAndGeGuEdegeFile (HashMap<String,BanKuai> allbkandzs, HashMap<String,BanKuai> sysbankuailist)
	{
		String parsedpath = sysconfig.getGephiFileExportPath ();
		String bkggnodecsvfile = parsedpath + "板块个股GEPHI对应关系edegs" 
							+ CommonUtility.formatDateYYMMDD(dateChooser.getDate()).replaceAll("-", "")
							+ ".csv"
							;
		
		File csvfile = new File(bkggnodecsvfile);
		if(csvfile.exists())
			csvfile.delete();
		
		List<String[]> contentArrayList = new ArrayList<>();
		String[] titlestrng = {"source","target","type","weight"};
		contentArrayList.add(titlestrng);
		
		List<String[]> listofcsv = new ArrayList<>();
//		HashMap<String, BanKuai> allbkandzs = bkdbopt.getTDXAllZhiShuAndBanKuai ();
		for (Entry<String, BanKuai> entry : allbkandzs.entrySet()) {
			String bkcode = entry.getKey();
			BanKuai bankuai = entry.getValue();
			String bkname = bankuai.getMyOwnName();
			if(bkname == null) bkname = bkcode;
			
			Date startdate = CommonUtility.getFirstDayOfWeek(new Date());
  	        Date lastdate = CommonUtility.getLastDayOfWeek(new Date());
  	       	HashMap<String, Stock> tmpallbkge = bkdbopt.getTDXBanKuaiGeGuOfHyGnFgAndChenJiaoLIang (bkname,bkcode,startdate,lastdate);
			
			if(tmpallbkge.size() == 0)
				continue;
			
			for (Entry<String, Stock> entrygegu : tmpallbkge.entrySet()) {
				String gegucode =entrygegu.getKey();
				Stock gegu = entrygegu.getValue();
				String geguname = gegu.getMyOwnName();
				if(geguname == null) geguname = gegucode;
				
				HashMap<String, Integer> ggweight = gegu.getGeGuSuoShuBanKuaiWeight();
				String weight = ggweight.values().toString();
				
				String[] ss = {gegucode,bkcode,"Directed",weight.substring(1,weight.length()-1)};
				listofcsv.add(ss);
				
			}
		}
		
		for (Entry<String, BanKuai> entry : sysbankuailist.entrySet()) {
			String bkcode = entry.getKey();
			BanKuai bankuai = entry.getValue();
			String bkname = bankuai.getMyOwnName();
			if(bkname == null) bkname = bkcode;
			
			Date startdate = CommonUtility.getFirstDayOfWeek(new Date());
  	        Date lastdate = CommonUtility.getLastDayOfWeek(new Date());
  	       	HashMap<String, Stock> tmpallbkge = bkdbopt.getTDXBanKuaiGeGuOfHyGnFgAndChenJiaoLIang (bkname,bkcode,startdate,lastdate);
			
			if(tmpallbkge.size() == 0)
				continue;
			
			for (Entry<String, Stock> entrygegu : tmpallbkge.entrySet()) {
				String gegucode =entrygegu.getKey();
				Stock gegu = entrygegu.getValue();
				String geguname = gegu.getMyOwnName();
				if(geguname == null) geguname = gegucode;
				
				HashMap<String, Integer> ggweight = gegu.getGeGuSuoShuBanKuaiWeight();
				String weight = ggweight.values().toString();
				
				String[] ss = {gegucode,bkcode,"Directed",weight.substring(1,weight.length()-1)};
				listofcsv.add(ss);
				
			}
		}
		
		CSVWriter writer = null;  
		String  characters = ",";
	    try {
		    if (",".equalsIgnoreCase(characters)){
		    	//初始化CSVWriter
		        writer = new CSVWriter(new FileWriter(csvfile),',',CSVWriter.NO_QUOTE_CHARACTER);  
		    } else{
		    	//初始化CSVWriter，带分隔符
		        writer = new CSVWriter(new FileWriter(csvfile),characters.toCharArray()[0],CSVWriter.NO_QUOTE_CHARACTER);  
		    }
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }

        try {
    	    writer.writeAll(contentArrayList);
    	    writer.writeAll(listofcsv);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	private void createEvents() 
	{
		tableSysBk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tableSysBk.getSelectedRow();
				int column = tableSysBk.getSelectedColumn();
				
				
				if(column !=3)
					return;
				
				Boolean status = (Boolean)((BanKuaiDetailTableModel)tableSysBk.getModel()).getValueAt(row, column);
				
				 ((BanKuaiDetailTableModel)tableSysBk.getModel()).setValueAt(!status,row, column);

				
			}
		});
		
		tablezhishu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tablezhishu.getSelectedRow();
				int column = tablezhishu.getSelectedColumn();
				
				
				if(column !=3)
					return;
				
				Boolean status = (Boolean)((BanKuaiDetailTableModel)tablezhishu.getModel()).getValueAt(row, column);
				
				 ((BanKuaiDetailTableModel)tablezhishu.getModel()).setValueAt(!status,row, column);

				
			}
		});
		
		btnstartexport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				writeBanKuaiAndGeGuNodeFile (zhishulist, sysbankuailist );
				if(cbxexporttype.getSelectedItem().toString().equals("导出所有板块数据")) {
					
					writeBanKuaiAndGeGuEdegeFile (zhishulist,sysbankuailist);
				} else {
					HashMap<String, BanKuai> selectedbk1 = ((BanKuaiDetailTableModel)tableSysBk.getModel()).getSelectedBanKuai();
					HashMap<String, BanKuai> selectedbk2 = ((BanKuaiDetailTableModel)tablezhishu.getModel()).getSelectedBanKuai();
					
					writeBanKuaiAndGeGuEdegeFile (selectedbk2,selectedbk1);
				}
				
			}
		});
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
			}
		});
		
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				if(bkcylpnl.isXmlEdited()) {
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
	private BanKuaiAndChanYeLian bkcylpnl;
	private JPanel panelSys;
	private JTable tableZdy;
	private JTable tablezhishu;
	private JTextField tfldfilepath;
	private JButton btnstartexport;
	private JComboBox<String> cbxexporttype;
	private JDateChooser dateChooser;
	private JComboBox<String> cbxexportdaytype;
	private JButton button;
	
	private void initializeGui()
	{
		setTitle("\u901A\u8FBE\u4FE1\u677F\u5757/\u81EA\u5B9A\u4E49\u677F\u5757\u8BBE\u7F6E");
		setBounds(100, 100, 1576, 988);
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
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Gephi\u5BFC\u51FA\u8BBE\u7F6E", TitledBorder.LEADING, TitledBorder.TOP, null, null));


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
							.addComponent(scrollPanesysbk, GroupLayout.PREFERRED_SIZE, 234, GroupLayout.PREFERRED_SIZE)))
					.addGap(18)
					.addGroup(gl_panelSys.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 278, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel))
					.addGap(32)
					.addGroup(gl_panelSys.createParallelGroup(Alignment.LEADING)
						.addComponent(label_1)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 241, GroupLayout.PREFERRED_SIZE))
					.addGap(698))
				.addGroup(Alignment.LEADING, gl_panelSys.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 532, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(969, Short.MAX_VALUE))
		);
		gl_panelSys.setVerticalGroup(
			gl_panelSys.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelSys.createSequentialGroup()
					.addContainerGap(15, Short.MAX_VALUE)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelSys.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(lblNewLabel)
						.addComponent(label_1))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelSys.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelSys.createParallelGroup(Alignment.BASELINE)
							.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 443, GroupLayout.PREFERRED_SIZE)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 441, GroupLayout.PREFERRED_SIZE))
						.addComponent(scrollPanesysbk, GroupLayout.PREFERRED_SIZE, 447, GroupLayout.PREFERRED_SIZE))
					.addGap(38))
		);
		
		JLabel lblGephi = new JLabel("Gephi\u6587\u4EF6\u5B58\u653E\u8DEF\u5F84");
		
		tfldfilepath = new JTextField();
		tfldfilepath.setColumns(10);
		
		button = new JButton("\u9009\u62E9\u8DEF\u5F84");
		
		dateChooser = new JDateChooser(new Date());
		
		cbxexporttype = new JComboBox();
		cbxexporttype.setModel(new DefaultComboBoxModel(new String[] {"\u5BFC\u51FA\u6240\u6709\u677F\u5757\u6570\u636E", "\u4EC5\u5BFC\u51FA\u6240\u9009\u677F\u5757\u6570\u636E"}));
		
		btnstartexport = new JButton("\u5F00\u59CB\u5BFC\u51FA");
		
		cbxexportdaytype = new JComboBox();
		cbxexportdaytype.setModel(new DefaultComboBoxModel(new String[] {"\u6309\u65E5\u5BFC\u51FA", "\u6309\u5468\u5BFC\u51FA"}));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblGephi)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfldfilepath, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(button))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(cbxexporttype, GroupLayout.PREFERRED_SIZE, 362, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnstartexport))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 198, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(cbxexportdaytype, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(43, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblGephi)
						.addComponent(tfldfilepath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(button))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
						.addComponent(cbxexportdaytype, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
					.addGap(7)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnstartexport, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(cbxexporttype, GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
					.addContainerGap(145, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		
		BanKuaiDetailTableModel zhishubankmodel = new BanKuaiDetailTableModel();
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
		
		BanKuaiDetailTableModel zidingyibankmodel = new BanKuaiDetailTableModel();
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
		
		
		BanKuaiDetailTableModel sysaccountsmodel = new BanKuaiDetailTableModel();
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
		
		
		BanKuaiDetailTableModel zdyaccountsmodel = new BanKuaiDetailTableModel();
		
		//pnlGingo2 = new Ginkgo2(this.bkdbopt, this.stockdbopt,this.cylxmlhandler);
//		bkcylpnl = new BanKuaiAndChanYeLian(this.stockInfoManager) ;
		bkcylpnl.startGui();
		
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
	String[] jtableTitleStrings = { "板块名称", "板块代码","更新时间","选择"};
	Boolean[] status;
	
	BanKuaiDetailTableModel ()
	{
	}

	public void refresh(HashMap<String,BanKuai> bankuailist)
	{
		this.bankuailist =  bankuailist;
		valuesList = new ArrayList<BanKuai>(bankuailist.values());
		Collator collator = Collator.getInstance(Locale.CHINESE);
		Ordering<BanKuai> sortedCopy = Ordering.from(byBkName);
		 Collections.sort(valuesList,sortedCopy);
		 
		 status = new Boolean[this.getRowCount()];
		 for(int i=0;i<this.getRowCount();i++) {
			 status[i] = false;
		 }
		 this.fireTableDataChanged();

	}

	Comparator<BanKuai> byBkName = new Comparator<BanKuai>() {
		Collator collator = Collator.getInstance(Locale.CHINESE);
		//Collections.sort(rightnamelist,collator);
	    public int compare(final BanKuai p1, final BanKuai p2) {
	    	try {
	    		return p1.getMyOwnCode().compareTo(p2.getMyOwnCode()  );
	    	} catch (java.lang.NullPointerException e) {
	    		return 1;
	    	}
	        
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
                value = valuesList.get(rowIndex).getMyOwnCode();
            	//value = this.bankuailist.get(tmpc[rowIndex]).getBankuainame();
                break;
            case 1:
            	value = valuesList.get(rowIndex).getMyOwnName();
            	//value = this.bankuailist.get(tmpc[rowIndex]).getBankuaicode();
                break;
            case 2:
            	value = valuesList.get(rowIndex).getCreatedTime();
            	//value = this.bankuailist.get(tmpc[rowIndex]).getBankuaicreatedtime();
            	break;
            case 3:
            	value = status[rowIndex];
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
		        	clazz = Boolean.class;
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
	    	if(column == 3 ) {
//	    		boolean curstatus = (Boolean)this.getValueAt(row,column);
//	    		this.setValueAt(new Boolean(!curstatus), row, column);
//	    		this.fireTableCellUpdated(row, column);
//	    		return !curstatus;
	    		return true;
	    	}
	    	else
	    		return false;
		}
	    
//	    public AccountInfoBasic getAccountsAt(int row) {
//	        return accountslist.get(row);
//	    }
	    
	    private void deleteAllRows()
	    {
//	    	if(this.accountslist == null)
//				 return ;
//			 else 
//				 accountslist.clear();
	    }
	    
	    private BanKuai getBanKuai (int rowIndex)
	    {
	    	return (BanKuai) bankuailist.get(rowIndex);
	    }
	    private boolean isSelected (int row)
	    {
	    	if((Boolean)this.getValueAt(row,3))
	    		return true;
	    	else
	    		return false;
	    }
	    
	    public HashMap<String,BanKuai> getSelectedBanKuai ()
	    {
	    	HashMap<String,BanKuai> tmpbklist = new HashMap<String,BanKuai> ();
	    	for(int i=0;i<getRowCount();i++) {
	    		if(this.isSelected(i) ) {
	    			String bkcode = (String)this.getValueAt(i, 0);
	    			BanKuai tmpbk = bankuailist.get(bkcode);
	    			tmpbklist.put(bkcode, tmpbk);
	    		}
	    	}
	    	return tmpbklist;
	    }
	    public void setValueAt(Boolean status1,int row, int column)
	    {
	    	status[row] = status1;
	    	this.fireTableCellUpdated(row, column);
	    }
	    
	

	    
}
