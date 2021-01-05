package com.exchangeinfomanager.bankuaifengxi;

import javax.swing.JPanel;

import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.DefaultTableModel;

import com.exchangeinfomanager.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;


import com.exchangeinfomanager.commonlib.ExportToCsvFile;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.toedter.calendar.JCalendar;

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
	private AllCurrentTdxBKAndStoksTree allbksks;

	/**
	 * Create the panel.
	 */
	public NodeInfoToCsv() 
	{
		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();

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
					 
//					LocalDate tmpstartdate = Instant.ofEpochMilli(timerange.getStartMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
//					LocalDate tmpenddate = Instant.ofEpochMilli(timerange.getEndMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
					
					if(timerange.overlap(newtimeinterval) != null) {
						timeintervalchange = true;
						finalinterval = unionInterval (timerange,newtimeinterval);
//						LocalDate startdate = Instant.ofEpochMilli(finalinterval.getStartMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
//						LocalDate enddate = Instant.ofEpochMilli(finalinterval.getEndMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
						
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
				
				((NodeInfoTableModel)nodelisttable.getModel()).refresh(nodetimeframeMultimap);
		
	}
	/*
	 * 	
	 */
	public void clearCsvDataSet ()
	{
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
//		     			String path = csvfile.getParent();
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
		String[] csvheaderheader = {"代码", "名称", "日期"};
		String[] csvdataheader = ObjectArrays.concat(NodeXPeriodData.NODEXDATACSVDATAHEADLINE, StockNodesXPeriodData.NODEXDATACSVDATAHEADLINE, String.class);  
		String[] csvheader = ObjectArrays.concat(csvheaderheader, csvdataheader, String.class);
		
		ExportToCsvFile etcsv = new ExportToCsvFile ();
		
//		Multiset<BkChanYeLianTreeNode> keyset = nodetimeframeMultimap.keys();
		Set<BkChanYeLianTreeNode> keysets = nodetimeframeMultimap.keySet();
		for(BkChanYeLianTreeNode node : keysets) {
			
//			node = this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(node.getMyOwnCode(), node.getType() );
			DaPan dapan = (DaPan)  node.getRoot();
			NodeXPeriodData nodexdata = ((TDXNodes)node).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);

			Collection<Interval> nodetimeframes = nodetimeframeMultimap.get(node);
			for(Interval timerange : nodetimeframes) {
				LocalDate tmpstartdate = Instant.ofEpochMilli(timerange.getStartMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate tmpenddate = Instant.ofEpochMilli(timerange.getEndMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
				
				LocalDate tmpdate = tmpstartdate;
				do  {
					if(dapan.isDaPanXiuShi(tmpdate, 0, NodeGivenPeriodDataItem.WEEK)) {
						tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
						continue;
					}
					
					 String[] nodecsvline = nodexdata.getNodeXDataCsvData(dapan, tmpdate, 0);						
					 String[] csvheadline = {	"'" + node.getMyOwnCode() + "'", node.getMyOwnName(), tmpdate.with(DayOfWeek.FRIDAY).toString()	};
					
					 if(nodecsvline != null  ) {
						 String [] csvline = ObjectArrays.concat(csvheadline, nodecsvline, String.class);
						 nodecontentArrayList.add(csvline);
						 csvline = null;
					 }
					
					tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
				} while (tmpdate.isBefore( tmpenddate) || tmpdate.isEqual(tmpenddate));
			}
		}

		//
		String parsedpath = (new SetupSystemConfiguration()).getNodeExportCsvFilePath ();
		String csvfilepath = parsedpath + "信息导出" 
							+ LocalDate.now().toString().replace("-", "")
							+ ".csv"
							;
		File csvfile = etcsv.exportToCsvFile(csvfilepath,csvheader, nodecontentArrayList);
		
		etcsv = null;
		csvheader = null;
		return csvfile;
	}
			
//	private void unifiedAddingCsvData(String nodecode, String nodename, LocalDate tmpdate, 
//			Double cje, Double cjezb, Integer cjemaxwk, Integer cjezbmaxwk, Integer cjezbminwk, Double hsl, Double liutongshizhi, 
//			Integer openupquekou , Integer huibudowquekou, Integer opendownquekou, Integer huibuupquekou ) 
//	{
//		
//		//代码必须加“‘”，否则导出后000前缀会消失，很麻烦
//		String[] csvline = {	"'" + nodecode + "'", nodename,tmpdate.with(DayOfWeek.FRIDAY).toString(),
//								cje.toString(),cjezb.toString(),
//								cjemaxwk.toString(),cjezbmaxwk.toString(),cjezbminwk.toString(),
//								formateOutputValueString (hsl) ,
//								formateOutputValueString (liutongshizhi),
//								formateOutputValueString (openupquekou),
//								formateOutputValueString (huibudowquekou),
//								formateOutputValueString (opendownquekou),
//								formateOutputValueString (huibuupquekou)
//								
//								};
//		
//		nodecontentArrayList.add(csvline);
//	}
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
	private Multimap<BkChanYeLianTreeNode,Interval> nodeslist;
//	private Table<String,String,Interval> csvnodelist;
//	ArrayList<Cell<String, String, Interval>> datalist;
	List<BkChanYeLianTreeNode> nodes ; 
	
	String[] jtableTitleStrings = { "代码", "名称","启始时间","终止时间"};
	private Object[][] data;
	
	NodeInfoTableModel (){
	}

	public void refresh  (Multimap<BkChanYeLianTreeNode,Interval> nodelst)
	{
		this.nodeslist = nodelst;
		Map<BkChanYeLianTreeNode, Collection<Interval>> dataMap = nodeslist.asMap();
		nodes = new ArrayList<BkChanYeLianTreeNode> (dataMap.keySet() );
		
		data = null;
		data = new Object[this.getRowCount()][4];
		int rowcount = 0;
		for(Map.Entry<BkChanYeLianTreeNode, Collection<Interval>> ent : dataMap.entrySet() ) {
			 BkChanYeLianTreeNode tmpnode = ent.getKey();
			 Collection<Interval> datainterval = ent.getValue();
			 for(Interval tmpitvl : datainterval) {
				 LocalDate exportstart = java.time.LocalDate.of( tmpitvl.getStart().getYear(),tmpitvl.getStart().getMonthOfYear(),tmpitvl.getStart().getDayOfMonth() );
				 LocalDate exportend = java.time.LocalDate.of( tmpitvl.getEnd().getYear(),tmpitvl.getEnd().getMonthOfYear(),tmpitvl.getEnd().getDayOfMonth() );
				 JLocalDateChooser stdc = new JLocalDateChooser ();
				 stdc.setLocalDate(exportstart);
				 JLocalDateChooser sted = new JLocalDateChooser ();
				 sted.setLocalDate(exportend);
				 Object[] rowdata = {tmpnode.getMyOwnCode(), tmpnode.getMyOwnName(),stdc.getLocalDate(), sted.getLocalDate()};
				 data[rowcount] = rowdata;
				 rowcount ++;
			 }
		}
		
		this.fireTableDataChanged();
	}
	 public int getRowCount() 
	 {
		 if(this.nodeslist == null)
			 return 0;
		 else if(this.nodeslist.isEmpty()  )
			 return 0;
		 else {
			 	int size =0;
			 	Map<BkChanYeLianTreeNode, Collection<Interval>> dataMap = nodeslist.asMap();
				for(Map.Entry<BkChanYeLianTreeNode, Collection<Interval>> ent : dataMap.entrySet()){
					 Collection<Interval> datainterval = ent.getValue();
					 size = size + datainterval.size();
				}
			 return size;
		 }
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(nodeslist.isEmpty())
	    		return null;
	    	
	    	Object value = "??";
	    	
	    	switch (columnIndex) {
            case 0:
                value = data[rowIndex][0];
                break;
            case 1:
            	value = data[rowIndex][1]; 
                break;
            case 2:
            	value = data[rowIndex][2];
                break;
            case 3:
            	value = data[rowIndex][3]; 
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
			          clazz = 	LocalDate.class;
			          break;
		        case 3:
			          clazz = LocalDate.class;
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
	    
	    public void removeAllRows ()
	    {
	    	if(nodeslist != null) {
	    		this.nodeslist.clear();
		    	this.fireTableDataChanged();
	    	}
	    	
	    }
}
