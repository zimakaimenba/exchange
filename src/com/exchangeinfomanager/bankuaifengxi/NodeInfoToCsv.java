package com.exchangeinfomanager.bankuaifengxi;

import javax.swing.JPanel;

import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.DefaultTableModel;

import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Week;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.ExportToCsvFile;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForTA4J.StockXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JCheckBox;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class NodeInfoToCsv extends JPanel 
{
	
	private BanKuaiDbOperation bkdbopt;
	private SystemConfigration sysconfig;
	private BanKuaiAndChanYeLian2 bkcyl;
	private AllCurrentTdxBKAndStoksTree allbksks;

	/**
	 * Create the panel.
	 */
	public NodeInfoToCsv() 
	{
		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
		this.bkcyl = BanKuaiAndChanYeLian2.getInstance();
		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
		
		initializeGui ();
		createEvents ();
		nodecontentArrayList = new ArrayList<String[]> ();
		
		nodetimeframeMultimap = ArrayListMultimap.create(); 
	}
	
	private List<String[]> nodecontentArrayList ;
	private Multimap<BkChanYeLianTreeNode,Interval> nodetimeframeMultimap;
	
	/*
	 * union tow Interval
	 */
	private Interval unionInterval( Interval firstInterval, Interval secondInterval ) 
	{
	    // Purpose: Produce a new Interval instance from the outer limits of any pair of Intervals.

	    // Take the earliest of both starting date-times.
	    DateTime start =  firstInterval.getStart().isBefore( secondInterval.getStart() )  ? firstInterval.getStart() : secondInterval.getStart();
	    // Take the latest of both ending date-times.
	    DateTime end =  firstInterval.getEnd().isAfter( secondInterval.getEnd() )  ? firstInterval.getEnd() : secondInterval.getEnd();
	    // Instantiate a new Interval from the pair of DateTime instances.
	    Interval unionInterval = new Interval( start, end );

	    return unionInterval;
	}

	public void addNodeToCsvList(BkChanYeLianTreeNode node, LocalDate requireddatestart,LocalDate requireddateend) 
	{
				Long requireddatestarttime = requireddatestart.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli() ;
				Long requireddateendtime = requireddateend.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli() ;
				Interval newtimeinterval = new Interval (requireddatestarttime,requireddateendtime);
				
				Collection<Interval> nodetimeframes = nodetimeframeMultimap.get(node);
				Boolean timeintervalchange = false;
				Interval finalinterval = null;
				for(Interval timerange : nodetimeframes) {
					 
					LocalDate tmpstartdate = Instant.ofEpochMilli(timerange.getStartMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
					LocalDate tmpenddate = Instant.ofEpochMilli(timerange.getEndMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
					
					if(timerange.overlap(newtimeinterval) != null) {
						timeintervalchange = true;
						finalinterval = unionInterval (timerange,newtimeinterval);
						LocalDate startdate = Instant.ofEpochMilli(finalinterval.getStartMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
						LocalDate enddate = Instant.ofEpochMilli(finalinterval.getEndMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
						
//						nodetimeframeMultimap.remove(node, timerange);
						break;
					}
				} 
				
				if(timeintervalchange) {
					nodetimeframeMultimap.removeAll(node);
					nodetimeframeMultimap.put(node, finalinterval);
				} else {
					nodetimeframeMultimap.put(node, newtimeinterval);
				}
		
	}
		
	public void clearCsvDataSet ()
	{
//		bknodetimerange.clear();
//		stocknodetimerange.clear();
		nodecontentArrayList.clear();
		nodetimeframeMultimap.clear();
	}
	private void createEvents() 
	{
		btncsv.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				appendRequiredBankuai ();
				
				File csvfile = exportToCsvFile ();
				
				if(csvfile == null)
					 return;
				
				clearCsvDataSet ();
				
				Toolkit.getDefaultToolkit().beep();
				int exchangeresult = JOptionPane.showConfirmDialog(null, "导出完成，是否打开" + csvfile.getAbsolutePath() + "查看","导出完成", JOptionPane.OK_CANCEL_OPTION);
		     	if(exchangeresult == JOptionPane.CANCEL_OPTION) 
		     			  return;
		     		  
		     	try {
		     			String path = csvfile.getParent();
	//	     			Desktop.getDesktop().open(new File( path ));
		     			Runtime.getRuntime().exec("explorer.exe /select," + csvfile.getAbsolutePath() );
		     	} catch (IOException e1) {
		     				e1.printStackTrace();
		     	}
				
		     	
			}
		});
		
	}
	/*
	 * 银行和证券是重要的标杆板块，可以用来和其他板块对比看强弱
	 */
	protected void appendRequiredBankuai() 
	{
		if(cbxappendmorebk.isSelected()) {
			BkChanYeLianTreeNode node = allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("880471", BkChanYeLianTreeNode.TDXBK );
			NodeXPeriodData nodexdata = ((TDXNodes)node).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
			LocalDate curstart = nodexdata.getOHLCRecordsStartDate();
			LocalDate curend = nodexdata.getOHLCRecordsEndDate();
			this.addNodeToCsvList (node,curstart,curend);
			
			node = this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("880472", BkChanYeLianTreeNode.TDXBK );
			nodexdata = ((TDXNodes)node).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
			curstart = nodexdata.getOHLCRecordsStartDate();
			curend = nodexdata.getOHLCRecordsEndDate();
			this.addNodeToCsvList (node,curstart,curend);
		}
	}
	/*
	 * 
	 */
	protected File exportToCsvFile() 
	{
		String[] csvheader = {  "Id","Name","Date",
								"成交额",
								"成交额MaxWk",
								
								"成交额占比",
   							    "成交额占比MaxWk",
								"成交额占比MinWk",
								"成交额占比增长率",
								
								"成交量",
								"成交量MaxWk",
								
								"成交量占比",
   							    "成交量占比MaxWk",
								"成交量占比MinWk",
								"成交量占比增长率",
								
								 "换手率",
								 "周平均流通市值",
								 
								 "OpenUpQueKou",
								 "HuiBuDownQueKou",
								 "OpenDownQueKou",
								 "HuiBuUpQueKou",
								 
								 "涨停",
								 "跌停"
						};
		
		
		ExportToCsvFile etcsv = new ExportToCsvFile ();
		
//		Multiset<BkChanYeLianTreeNode> keyset = nodetimeframeMultimap.keys();
		Set<BkChanYeLianTreeNode> keysets = nodetimeframeMultimap.keySet();
		for(BkChanYeLianTreeNode node : keysets) {
			
			node = this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(node.getMyOwnCode(), node.getType() );
			NodeXPeriodData nodexdata = ((TDXNodes)node).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
			
			Collection<Interval> nodetimeframes = nodetimeframeMultimap.get(node);
			for(Interval timerange : nodetimeframes) {
				LocalDate tmpstartdate = Instant.ofEpochMilli(timerange.getStartMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate tmpenddate = Instant.ofEpochMilli(timerange.getEndMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
				
				LocalDate tmpdate = tmpstartdate;
				do  {
					Double cje = nodexdata.getChengJiaoEr(tmpdate, 0);
					if(cje != null) {
						Integer cjemaxwk = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(tmpdate,0);

						Double cjezb = nodexdata.getChenJiaoErZhanBi(tmpdate, 0);
						Integer cjezbmaxwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(tmpdate, 0);
						Integer cjezbminwk = nodexdata.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(tmpdate, 0);
						Double cjezbgrowthrate = nodexdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(tmpdate, 0);
						
						Double curcjl = nodexdata.getChengJiaoLiang(tmpdate, 0);
						Integer cjlmaxwk = nodexdata.getChenJiaoLiangMaxWeekOfSuperBanKuai(tmpdate,0);//显示cjl是多少周最大
						
						Double cjlzhanbidata = nodexdata.getChenJiaoLiangZhanBi(tmpdate, 0);
						Integer cjlzbmaxwk = nodexdata.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(tmpdate, 0);
						Integer cjlzbminwk = nodexdata.getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(tmpdate, 0);
						Double cjlzbgrowthrate = nodexdata.getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(tmpdate, 0);
						
						Double hsl = null;
						Double ltsz = null;
						if(node.getType() == BkChanYeLianTreeNode.TDXBK) {
							hsl = null;
							ltsz = null;
						} else {
							hsl = ((StockXPeriodData)nodexdata).getSpecificTimeHuanShouLv(tmpdate, 0);
							ltsz = ((StockXPeriodData)nodexdata).getSpecificTimeLiuTongShiZhi(tmpdate, 0);;
						}
						
						Integer opneupquekou = nodexdata.getQueKouTongJiOpenUp(tmpdate, 0);
						Integer opendownquekou = nodexdata.getQueKouTongJiOpenDown(tmpdate, 0);
						Integer huibuupquekou =  nodexdata.getQueKouTongJiHuiBuUp(tmpdate, 0);
						Integer huibudowquekou = nodexdata.getQueKouTongJiHuiBuDown(tmpdate, 0);
						
						Integer zhangtingnum = nodexdata.getZhangTingTongJi(tmpdate, 0);
						Integer dietingnum =  nodexdata.getDieTingTongJi(tmpdate, 0);
						
						String[] csvline = {	"'" + node.getMyOwnCode() + "'", node.getMyOwnName(), tmpdate.with(DayOfWeek.FRIDAY).toString(),
								cje.toString(),
								cjemaxwk.toString(),
								
								cjezb.toString(),
								cjezbmaxwk.toString(),
								cjezbminwk.toString(),
								cjezbgrowthrate.toString(),
								
								curcjl.toString(),
								cjlmaxwk.toString(),
								
								cjlzhanbidata.toString(),
								cjlzbmaxwk.toString(),
								cjlzbminwk.toString(),
								cjlzbgrowthrate.toString(),
								
								formateOutputValueString (hsl) ,
								formateOutputValueString (ltsz),
								
								formateOutputValueString (opneupquekou),
								formateOutputValueString (huibudowquekou),
								formateOutputValueString (opendownquekou),
								formateOutputValueString (huibuupquekou),
								
								formateOutputValueString (zhangtingnum),
								formateOutputValueString (dietingnum)
								};
						
						nodecontentArrayList.add(csvline);
//						unifiedAddingCsvData (node.getMyOwnCode(),node.getMyOwnName(),tmpdate,cje,cjezb,cjemaxwk,cjezbmaxwk,cjezbminwk,hsl,ltsz,
//								opneupquekou,huibudowquekou,opendownquekou,huibuupquekou);
					} 
					
					tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
				} while (tmpdate.isBefore( tmpenddate) || tmpdate.isEqual(tmpenddate));
				
				
			}
		}

		//
		String parsedpath = this.sysconfig.getNodeExportCsvFilePath ();
		String csvfilepath = parsedpath + "信息导出" 
							+ LocalDate.now().toString().replace("-", "")
							+ ".csv"
							;
		File csvfile = etcsv.exportToCsvFile(csvfilepath,csvheader, nodecontentArrayList);
		
		etcsv = null;
		return csvfile;
	}
		
	private void unifiedAddingCsvData(String nodecode, String nodename, LocalDate tmpdate, 
			Double cje, Double cjezb, Integer cjemaxwk, Integer cjezbmaxwk, Integer cjezbminwk, Double hsl, Double liutongshizhi, 
			Integer openupquekou , Integer huibudowquekou, Integer opendownquekou, Integer huibuupquekou ) 
	{
		
		//代码必须加“‘”，否则导出后000前缀会消失，很麻烦
		String[] csvline = {	"'" + nodecode + "'", nodename,tmpdate.with(DayOfWeek.FRIDAY).toString(),
								cje.toString(),cjezb.toString(),
								cjemaxwk.toString(),cjezbmaxwk.toString(),cjezbminwk.toString(),
								formateOutputValueString (hsl) ,
								formateOutputValueString (liutongshizhi),
								formateOutputValueString (openupquekou),
								formateOutputValueString (huibudowquekou),
								formateOutputValueString (opendownquekou),
								formateOutputValueString (huibuupquekou)
								
								};
		
		nodecontentArrayList.add(csvline);
	}
	private String formateOutputValueString (Number value)
	{
		String output = "0";
		if( value != null)
			output = String.valueOf(value);
		
		return output;
	}
	
	
	private JTable nodelisttable;
	private JButton btncsv;
	private JCheckBox cbxappendmorebk;
	private void initializeGui() 
	{
		JScrollPane scrollPane = new JScrollPane();
		
		btncsv = new JButton("\u5BFC\u51FA\u5230CSV");
		
		cbxappendmorebk = new JCheckBox("\u9644\u52A0\u57FA\u51C6\u677F\u5757(\u94F6\u884C/\u8BC1\u5238)");
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 351, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btncsv)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(cbxappendmorebk)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 449, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btncsv)
						.addComponent(cbxappendmorebk))
					.addContainerGap(21, Short.MAX_VALUE))
		);
		
		NodeInfoTableModel nimodel = new NodeInfoTableModel ();
		nodelisttable = new JTable(nimodel);
		scrollPane.setViewportView(nodelisttable);
		setLayout(groupLayout);
		
	}
}


class NodeInfoTableModel extends DefaultTableModel 
{
	private ArrayList<AccountInfoBasic> accountslist;

	
	String[] jtableTitleStrings = { "代码", "名称","启始时间","终止时间"};
	
	NodeInfoTableModel ()
	{
	}

	public void refresh  (ArrayList<AccountInfoBasic> arrayList,String stockcode)
	{
		this.accountslist = arrayList;

		
		this.fireTableDataChanged();
	}
	 public int getRowCount() 
	 {
		 if(this.accountslist == null)
			 return 0;
		 else if(this.accountslist.isEmpty()  )
			 return 0;
		 else
			 return this.accountslist.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(accountslist.isEmpty())
	    		return null;
	    	
	    	Object value = "??";
	    	AccountInfoBasic account = accountslist.get(rowIndex);
	    	StockChiCangInfo tmpstkcc = account.getStockChiCangInfoIndexOf ("");
	    	switch (columnIndex) {
            case 0:
                value = account.getAccountName();
                break;
            case 1:
            	NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(); 
            	
                value = currencyFormat.format(0-tmpstkcc.getChicangchenben()); //因为成本为负

                break;
            case 2:
            	//NumberFormat currencyFormat1 = NumberFormat.getCurrencyInstance();
            	value = new JLocalDateChooser ();
                break;
            case 3:
            	NumberFormat currencyFormat2 = NumberFormat.getCurrencyInstance();
                value = new JLocalDateChooser (); 
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
			          clazz = JLocalDateChooser.class;
			          break;
		        case 3:
			          clazz = JLocalDateChooser.class;
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
	    	return true;
		}
	    
	    public void deleteAllRows()
	    {
//	    	if(this.accountslist == null)
//				 return ;
//			 else 
//				 accountslist.clear();
	    	int rowCount = this.getRowCount();
	    	//Remove rows one by one from the end of the table
//	    	for (int i = rowCount - 1; i >= 0; i--) {
	    	    
	    	
	    	this.fireTableDataChanged();
	    }
	    
	    public void removeAllRows ()
	    {
	    	if(accountslist != null) {
	    		this.accountslist.clear();
		    	this.fireTableDataChanged();
	    	}
	    	
	    }
}
