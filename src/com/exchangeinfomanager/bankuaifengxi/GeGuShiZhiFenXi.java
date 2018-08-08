package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.asinglestockinfo.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.bankuaifengxi.CategoryBarOfName.BanKuaiFengXiCategoryBarChartHuanShouLvPnl;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.google.common.base.Splitter;
import com.toedter.calendar.JDateChooser;

import javax.swing.JComboBox;
import net.miginfocom.swing.MigLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;

public class GeGuShiZhiFenXi extends JDialog 
{
	/**
	 * Create the dialog.
	 */
	public GeGuShiZhiFenXi() 
	{
		initializeGui ();
		datachooser.setLocalDate(LocalDate.now());
		
		createEvents ();
		this.bkdbopt = new BanKuaiDbOperation ();
		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
		
		
		initializeShiZhiLevels ();
		setupStocksByShiZhiLevels ();
	}
	
	private static Logger logger = Logger.getLogger(GeGuShiZhiFenXi.class);
	private List<String> shizhilevellist;
	private HashMap<Integer, Set<String>> stocksshizhisets;
	private BanKuaiDbOperation bkdbopt;
	private AllCurrentTdxBKAndStoksTree allbksks;
	/*
	 * 
	 */
	public String getStockShiZhiDuiBiFenXiResult (String stockcode)
	{
		String outputresult = displayStockFenXiResult (stockcode);
		return outputresult;
	}
	/*
	 * 
	 */
	private void initializeShiZhiLevels()
	{
		String shizhilevels = tfldshizhilevel.getText();
		List<String> tmpbszlevelist = Splitter.on(",").omitEmptyStrings().splitToList(shizhilevels); 
		
		shizhilevellist = null;
		shizhilevellist = new ArrayList(tmpbszlevelist);
		Collections.sort(shizhilevellist, new Comparator<String>() {
		    @Override
		    public int compare(String sz1, String sz2) {
		        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
		    	Integer sz1int = Integer.parseInt(sz1);
		    	Integer sz2int = Integer.parseInt(sz2);
		    	
		        return sz2int > sz1int ? -1 : (sz2int < sz1int) ? 1 : 0;
		    }
		});
	}
	/*
	 * 
	 */
	private void setupStocksByShiZhiLevels() 
	{
		int n = shizhilevellist.size() + 1;
		stocksshizhisets = null;
		stocksshizhisets = new HashMap<Integer,Set<String>> (n);
		
//		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
//		setCursor(hourglassCursor);
//		
//		for(int i= -1; i<shizhilevellist.size(); i++) {
//			Double shizhidown = 0.0;
//			Double shizhiup = 0.0;
//			if(i == -1) 
//				 shizhidown = 0.0;
//			else 
//				shizhidown = Double.parseDouble(shizhilevellist.get(i) );
//				
//			try {
//					shizhiup = Double.parseDouble(shizhilevellist.get(i+1) );
//			} catch (Exception e) {
//					shizhiup = 100000.0;
//			}
//			
//			
//			Set<String> stocksetofrangeshizhi = this.bkdbopt.getStockOfRangeShiZhi (shizhidown * 100000000,shizhiup * 100000000,datachooser.getLocalDate(),0,"liutong");
//			stocksshizhisets.add(stocksetofrangeshizhi);
//		}
//		
//		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
//		hourglassCursor2 = null;
//		setCursor(hourglassCursor2);
	}
	/*
	 * 
	 */
	protected String displayStockFenXiResult(String stockcode) 
	{
		Stock stocksearched = (Stock)this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(stockcode, BanKuaiAndStockBasic.TDXGG);
		boolean hasRecordinPeriod = ((StockNodeXPeriodData)stocksearched.getNodeXPeroidData( StockGivenPeriodDataItem.WEEK)).hasRecordInThePeriod(datachooser.getLocalDate(), 0);
		if(!hasRecordinPeriod) 
			stocksearched = this.allbksks.getStock(stocksearched, datachooser.getLocalDate(), StockGivenPeriodDataItem.WEEK);
		
		Double stockshizhi = ((StockNodeXPeriodData)stocksearched.getNodeXPeroidData( StockGivenPeriodDataItem.WEEK))
				.getSpecificTimeLiuTongShiZhi(datachooser.getLocalDate(), 0) ;
		
		if(stockshizhi == null)
			return null;

		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		int shzhilevelid = 0; String resultoutput = "" + datachooser.getLocalDate().toString();
		for(int i = 0;i<shizhilevellist.size();i++) {
			Double shizhidown = Double.parseDouble(shizhilevellist.get(i) );
			Double shizhiup;
			try {
				shizhiup = Double.parseDouble(shizhilevellist.get(i+1) );
			} catch (Exception e) {
				shzhilevelid = i+1;
				resultoutput = resultoutput + "市值范围:>" + shizhidown + "亿。\n";
				break;
			}
			
			if(stockshizhi / 100000000 < shizhidown) {
				shzhilevelid = i;
				resultoutput = resultoutput + "市值范围:0-" + shizhidown + "亿。\n";
				break;
			} else	if(between(stockshizhi / 100000000,shizhidown,shizhiup) ) {
				shzhilevelid = i+1;
				resultoutput = resultoutput + "市值范围:" + shizhidown + "-" + shizhiup + "亿。\n";
				break;
			} else if(stockshizhi / 100000000 > shizhiup) {
				continue;
			}
		}
		
		Set<String> stocksetofrangeshizhi = null;
		try {
			stocksetofrangeshizhi = stocksshizhisets.get(shzhilevelid);
		} catch (Exception e ) {
			stocksetofrangeshizhi = getStockSetFromDbOftheRange (shzhilevelid);
		}
		if(stocksetofrangeshizhi == null || stocksetofrangeshizhi.isEmpty()) { //没有初始化过市值范围的个股集合，这是为了提高效率
			stocksetofrangeshizhi = getStockSetFromDbOftheRange (shzhilevelid);
		}
		
		List<Stock> stocklistofrangeshizhi = new ArrayList<>(stocksetofrangeshizhi.size() );
		
		double allzhanbi = 0.0; double stocksearchedzhanbi = 0.0;
		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
		for(String tmpstockcode : stocksetofrangeshizhi) {
			Stock node = (Stock)this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(tmpstockcode, BanKuaiAndStockBasic.TDXGG);
			hasRecordinPeriod = ((StockNodeXPeriodData)node.getNodeXPeroidData( StockGivenPeriodDataItem.WEEK)).hasRecordInThePeriod(datachooser.getLocalDate(), 0);
			if(!hasRecordinPeriod) 
				node = this.allbksks.getStock(node, datachooser.getLocalDate(), StockGivenPeriodDataItem.WEEK);
			
			stocklistofrangeshizhi.add(node);
			
			Double nodezhanbi = ((StockNodeXPeriodData)node.getNodeXPeroidData( StockGivenPeriodDataItem.WEEK)).getChenJiaoErZhanBi(datachooser.getLocalDate(), 0);
			allzhanbi = allzhanbi + nodezhanbi; 
			
			if(node.getMyOwnCode().equalsIgnoreCase(stocksearched.getMyOwnCode()))
				stocksearchedzhanbi = nodezhanbi;
		}
		
		Collections.sort(stocklistofrangeshizhi, new NodeShiZhiComparator (datachooser.getLocalDate(),0,StockGivenPeriodDataItem.WEEK,"liutong") );
		int shizhipaiming = stocklistofrangeshizhi.indexOf(stocksearched );
		Collections.sort(stocklistofrangeshizhi, new NodeZhanBiComparator (datachooser.getLocalDate(),0,StockGivenPeriodDataItem.WEEK) );
		int zhanbipaiming = stocklistofrangeshizhi.indexOf(stocksearched );
		
		NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
	    percentFormat.setMinimumFractionDigits(4);
		resultoutput = resultoutput + "共有" + stocksetofrangeshizhi.size() + "个股票。\n";
		resultoutput = resultoutput + "平均占比" + percentFormat.format (allzhanbi/stocksetofrangeshizhi.size() ) + "\n";
		resultoutput = resultoutput + stocksearched.getMyOwnCode() + stocksearched.getMyOwnName() + ":市值" + stockshizhi/100000000 + "亿" +  "个股占比:" + percentFormat.format (stocksearchedzhanbi)  
							+ "流通市值排名：" + shizhipaiming + "占比排名：" + zhanbipaiming + "\n";
		
	
		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
		hourglassCursor2 = null;
		setCursor(hourglassCursor2);
		
		return resultoutput;
	}
	/*
	 * 
	 */
	private void outputResutlToGui (String outputresult)
	{
		txaeoutput.setText(outputresult + txaeoutput.getText() +  "\n");
		txaeoutput.setCaretPosition(0);
	}
	/*
	 * 
	 */
	private Set<String> getStockSetFromDbOftheRange(int shzhilevelid)
	{
		Double shizhidown = null, shizhiup = null;
		if(shzhilevelid == 0) {
			shizhidown = 0.0;
			shizhiup = Double.parseDouble(shizhilevellist.get(0) ); 
		} else if(shzhilevelid == shizhilevellist.size()) {
			shizhidown = Double.parseDouble( shizhilevellist.get(  shizhilevellist.size() -1 ) );
			shizhiup = 100000.0;;
		} else {
			shizhidown = Double.parseDouble(shizhilevellist.get(shzhilevelid - 1) );
			shizhiup = Double.parseDouble(shizhilevellist.get(shzhilevelid ) );
		}
		
		Set<String> tmpstocksetofrangeshizhi = this.bkdbopt.getStockOfRangeShiZhi (shizhidown * 100000000,shizhiup * 100000000,datachooser.getLocalDate(),0,"liutong");
		stocksshizhisets.put(shzhilevelid, tmpstocksetofrangeshizhi);
		
		return tmpstocksetofrangeshizhi;
	}
	/*
	 * 
	 */
	private boolean between(double i, double minValueInclusive, double maxValueInclusive) {
	    if (i >= minValueInclusive && i <= maxValueInclusive)
	        return true;
	    else
	        return false;
	}
	/*
	 * 
	 */
	private void createEvents() 
	{
		datachooser.addPropertyChangeListener(new PropertyChangeListener() {
		    @Override
		    public void propertyChange(PropertyChangeEvent e) {
		    	if("date".equals(e.getPropertyName() ) ) {
		    		setupStocksByShiZhiLevels ();
		    	}
		    }
		});
		
		btnaddstock.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				String stockcode = tfldstockcode.getText().trim();
				String outputresult = displayStockFenXiResult (stockcode);
				outputResutlToGui(outputresult);
			}
		});
		
		btndel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				String delLevl = JOptionPane.showInputDialog(null,"输入删除的level:","市值level", JOptionPane.QUESTION_MESSAGE);
				if(delLevl != null) 
					shizhilevellist.remove(delLevl.trim());
				else
					 return;
				
				Collections.sort(shizhilevellist, new Comparator<String>() {
				    @Override
				    public int compare(String sz1, String sz2) {
				        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
				    	Integer sz1int = Integer.parseInt(sz1);
				    	Integer sz2int = Integer.parseInt(sz2);
				    	
				        return sz2int > sz1int ? -1 : (sz2int < sz1int) ? 1 : 0;
				    }
				});
				
				tfldshizhilevel.setText("");
				for(String everylevel : shizhilevellist)
					tfldshizhilevel.setText(tfldshizhilevel.getText() + everylevel + ","); 
				
				initializeShiZhiLevels ();
			}
		});
		
		btnadd.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				String newLevl = JOptionPane.showInputDialog(null,"请输入新的level:","市值level", JOptionPane.QUESTION_MESSAGE);
				if(newLevl != null)
					shizhilevellist.add(newLevl);
				else
					return;
				
				Collections.sort(shizhilevellist, new Comparator<String>() {
				    @Override
				    public int compare(String sz1, String sz2) {
				        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
				    	Integer sz1int = Integer.parseInt(sz1);
				    	Integer sz2int = Integer.parseInt(sz2);
				    	
				        return sz2int > sz1int ? -1 : (sz2int < sz1int) ? 1 : 0;
				    }
				});
				
				tfldshizhilevel.setText("");
				for(String everylevel : shizhilevellist)
					tfldshizhilevel.setText(tfldshizhilevel.getText() + everylevel + ","); 
				
				initializeShiZhiLevels ();
			}
		});
		
	}

	

	private final JPanel contentPanel = new JPanel();
	private JComboBox cbxshizhitype;
	private JButton okButton;
	private JLabel label;
	private JTextField tfldshizhilevel;
	private JTextField tfldparsedfile;
	private JTextField tfldstockcode;
	private JTable table;
	private BanKuaiFengXiCategoryBarChartHuanShouLvPnl panel_1;
	private BanKuaiFengXiCategoryBarChartHuanShouLvPnl panel_2;
	private JButton btnadd;
	private JButton btndel;
	private JCheckBox cbxparsedfile;
	private JButton btnchoosefile;
	private JTextArea txaeoutput;
	private JButton btnreset;
	private JButton btnlater;
	private JButton btnprevious;
	private JLocalDateChooser datachooser;
	private JPanel panel;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JButton btnaddstock;
	
	private void initializeGui() 
	{
		setBounds(100, 100, 1228, 813);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		panel = new JPanel();
		
		scrollPane = new JScrollPane();
		
		scrollPane_1 = new JScrollPane();
		
		panel_1 = new BanKuaiFengXiCategoryBarChartHuanShouLvPnl();
		
		panel_2 = new BanKuaiFengXiCategoryBarChartHuanShouLvPnl();
		
		txaeoutput = new JTextArea();
		txaeoutput.setLineWrap(true);
		scrollPane_1.setViewportView(txaeoutput);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		datachooser = new JLocalDateChooser();
		
		btnprevious = new JButton("<");
		
		btnlater = new JButton(">");
		
		btnreset = new JButton("\u91CD\u7F6E");
		
		tfldstockcode = new JTextField();
		tfldstockcode.setColumns(10);
		
		btnaddstock = new JButton("\u6DFB\u52A0\u4E2A\u80A1");
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(panel, GroupLayout.PREFERRED_SIZE, 282, GroupLayout.PREFERRED_SIZE)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 282, GroupLayout.PREFERRED_SIZE))
							.addGap(4)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 899, Short.MAX_VALUE)
								.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 899, Short.MAX_VALUE)))
						.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 282, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(13)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 398, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE)
							.addGap(9)
							.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)))
					.addGap(4)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 234, GroupLayout.PREFERRED_SIZE))
		);
		contentPanel.setLayout(gl_contentPanel);
		
		panel.setLayout(new MigLayout("", "[111px][39px][12px][40px][57px]", "[23px][23px]"));
		panel.add(datachooser, "cell 0 0,growx,aligny center");
		panel.add(btnprevious, "cell 1 0,alignx left,aligny top");
		panel.add(btnlater, "cell 3 0,alignx left,aligny top");
		panel.add(btnreset, "cell 4 0,alignx left,aligny top");
		panel.add(tfldstockcode, "cell 0 1,growx,aligny center");
		panel.add(btnaddstock, "cell 1 1 3 1,alignx right,aligny top");
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				cbxshizhitype = new JComboBox();
				cbxshizhitype.setModel(new DefaultComboBoxModel(new String[] {"\u6D41\u901A\u5E02\u503C"}));
			}
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				getRootPane().setDefaultButton(okButton);
			}
			
			label = new JLabel("\u5E02\u503C\u5206\u7EA7");
			
			tfldshizhilevel = new JTextField();
			tfldshizhilevel.setEditable(false);
			tfldshizhilevel.setText("20,50,100,300,500,800");
			tfldshizhilevel.setColumns(10);
			
			btnadd = new JButton("\u6DFB\u52A0\u5206\u7EA7");
			
			
			btndel = new JButton("\u5220\u9664\u5206\u7EA7");
			
			cbxparsedfile = new JCheckBox("\u6BCF\u5468\u6A21\u578B\u5206\u6790\u6587\u4EF6");
			
			tfldparsedfile = new JTextField();
			tfldparsedfile.setColumns(10);
			
			btnchoosefile = new JButton("\u9009\u62E9\u6587\u4EF6");
			buttonPane.setLayout(new MigLayout("", "[74px][48px][242px][81px][81px][121px][184px][81px][45px]", "[23px]"));
			buttonPane.add(cbxshizhitype, "cell 0 0,alignx left,aligny center");
			buttonPane.add(okButton, "cell 8 0,alignx left,aligny top");
			buttonPane.add(label, "cell 1 0,alignx left,aligny center");
			buttonPane.add(tfldshizhilevel, "cell 2 0,growx,aligny center");
			buttonPane.add(btnadd, "cell 3 0,alignx left,aligny top");
			buttonPane.add(btndel, "cell 4 0,alignx left,aligny top");
			buttonPane.add(cbxparsedfile, "cell 5 0,alignx left,aligny top");
			buttonPane.add(tfldparsedfile, "cell 6 0,growx,aligny center");
			buttonPane.add(btnchoosefile, "cell 7 0,alignx left,aligny top");
		}
		
	}
	
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			GeGuShiZhiFenXi dialog = new GeGuShiZhiFenXi();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}


/*
 * 
 */
class NodeShiZhiComparator implements Comparator<Stock> 
{
	private String period;
	private LocalDate compareDate;
	private int difference;
	private String shizhitype;
	
	public NodeShiZhiComparator (LocalDate compareDate, int difference, String period,String shizhitype )
	{
		this.period = period;
		this.compareDate = compareDate;
		this.difference = difference;
		this.shizhitype = shizhitype;
	}
	
    public int compare(Stock node1, Stock node2) {
        Double liutongshizhi1 = ((StockNodeXPeriodData)node1.getNodeXPeroidData( period)).getSpecificTimeLiuTongShiZhi(compareDate, difference) ;
        Double liutongshizhi2 = ((StockNodeXPeriodData)node2.getNodeXPeroidData( period)).getSpecificTimeLiuTongShiZhi(compareDate, difference);
        
        return liutongshizhi2.compareTo(liutongshizhi1);
    }
}


class NodeHuanShouLvComparator implements Comparator<Stock> 
{
	private String period;
	private LocalDate compareDate;
	private int difference;
	
	
	public NodeHuanShouLvComparator (LocalDate compareDate, int difference, String period)
	{
		this.period = period;
		this.compareDate = compareDate;
		this.difference = difference;
		
	}
	
    public int compare(Stock node1, Stock node2) {
        Double huanshoulv1 = ((StockNodeXPeriodData)node1.getNodeXPeroidData( period)).getSpecificTimeHuanShouLv(compareDate, difference) ;
        Double huanshoulv2 = ((StockNodeXPeriodData)node2.getNodeXPeroidData( period)).getSpecificTimeHuanShouLv(compareDate, difference);
        
        return huanshoulv2.compareTo(huanshoulv1);
    }
}

class NodeZhanBiComparator implements Comparator<Stock> 
{
	private String period;
	private LocalDate compareDate;
	private int difference;
	
	
	public NodeZhanBiComparator (LocalDate compareDate, int difference, String period)
	{
		this.period = period;
		this.compareDate = compareDate;
		this.difference = difference;
		
	}
	
    public int compare(Stock node1, Stock node2) {
        Double zhanbi1 = ((StockNodeXPeriodData)node1.getNodeXPeroidData( period)).getChenJiaoErZhanBi(compareDate, difference) ;
        Double zhanbi2 = ((StockNodeXPeriodData)node2.getNodeXPeroidData( period)).getChenJiaoErZhanBi(compareDate, difference);
        
        return zhanbi2.compareTo(zhanbi1);
    }
}