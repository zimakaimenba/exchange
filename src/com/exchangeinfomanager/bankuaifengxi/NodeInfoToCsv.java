package com.exchangeinfomanager.bankuaifengxi;

import javax.swing.JPanel;

import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.DefaultTableModel;

import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Week;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;
import com.exchangeinfomanager.asinglestockinfo.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.ExportToCsvFile;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Splitter;

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
		bknodetimerange = new HashMap<String,String> ();
		stocknodetimerange = new HashMap<String,String> ();
		nodecontentArrayList = new ArrayList<String[]> ();
	}
	private HashMap<String , String> bknodetimerange ;
	private HashMap<String , String> stocknodetimerange ;
	private List<String[]> nodecontentArrayList ;
	
	private HashMap<String , List<String>> stocknodetimemap ;
	private void addStockToCsvStockMap(BkChanYeLianTreeNode node, LocalDate requireddatestart,LocalDate requireddateend) 
	{
		List<String> nodedateinlist = this.stocknodetimemap.get(node.getMyOwnCode());
		if(nodedateinlist == null) {
			List<String> stocktimerangelist = new ArrayList<String> ();
			if(requireddatestart.equals(requireddateend)) {
				String result = requireddatestart.toString() + "|" + requireddateend.toString();
				stocktimerangelist.add(requireddatestart.toString());
			} else {
				String result = requireddatestart.toString() + "|" + requireddateend.toString();
				stocktimerangelist.add(result);
			}
			this.stocknodetimemap.put(node.getMyOwnCode(), stocktimerangelist);
			
		} else {
			Boolean shouldadddate = false;
			for(String timerange : nodedateinlist) {
				if(timerange.contains("|")) {
					List<String> curnodedate = Splitter.on("|").omitEmptyStrings().splitToList(timerange);
					LocalDate curstart = LocalDate.parse(curnodedate.get(0));
					LocalDate curend = LocalDate.parse(curnodedate.get(1));
					if(requireddatestart.equals(requireddateend)) {
						
					} else {
						HashMap<String, LocalDate> tmpdatemap = nodeWeekTimeStampRelation(node, curstart,curend,requireddatestart,requireddateend);
						String result = tmpdatemap.get("searchstart").toString() + "|" + tmpdatemap.get("searchend").toString();
						
					}
				} else {
					
				}
			}
		}
		
	}
	/*
	 * 
	 */
	public void addNodeToCsvList (BkChanYeLianTreeNode node, LocalDate requireddatestart, LocalDate requireddateend)
	{
		if(node.getType() == BanKuaiAndStockBasic.TDXBK) {
			addBanKuaiToCsvList (node,requireddatestart,requireddateend);
		} else if(node.getType() == BanKuaiAndStockBasic.TDXGG) {
			addStockToCsvList (node,requireddatestart,requireddateend);
		}
		
	}
	private void addBanKuaiToCsvList(BkChanYeLianTreeNode node, LocalDate requireddatestart, LocalDate requireddateend) 
	{
		String nodedateinmap = this.bknodetimerange.get(node.getMyOwnCode());
		if(nodedateinmap == null) {
			String result = requireddatestart.toString() + "|" + requireddateend.toString();
			this.bknodetimerange.put(node.getMyOwnCode(), result);
		} else {
			List<String> curnodedate = Splitter.on("|").omitEmptyStrings().splitToList(nodedateinmap);
			LocalDate curstart = LocalDate.parse(curnodedate.get(0));
			LocalDate curend = LocalDate.parse(curnodedate.get(1));
			HashMap<String, LocalDate> tmpdatemap = nodeWeekTimeStampRelation(node, curstart,curend,requireddatestart,requireddateend);
			String result = tmpdatemap.get("searchstart").toString() + "|" + tmpdatemap.get("searchend").toString();
			this.bknodetimerange.put(node.getMyOwnCode(), result);
		}
	}
	private void addStockToCsvList(BkChanYeLianTreeNode node, LocalDate requireddatestart,LocalDate requireddateend) 
	{
		String nodedateinmap = this.stocknodetimerange.get(node.getMyOwnCode());
		if(nodedateinmap == null) {
			String result = requireddatestart.toString() + "|" + requireddateend.toString();
			this.stocknodetimerange.put(node.getMyOwnCode(), result);
		} else {
			List<String> curnodedate = Splitter.on("|").omitEmptyStrings().splitToList(nodedateinmap);
			LocalDate curstart = LocalDate.parse(curnodedate.get(0));
			LocalDate curend = LocalDate.parse(curnodedate.get(1));
			HashMap<String, LocalDate> tmpdatemap = nodeWeekTimeStampRelation(node, curstart,curend,requireddatestart,requireddateend);
			String result = tmpdatemap.get("searchstart").toString() + "|" + tmpdatemap.get("searchend").toString();
			this.stocknodetimerange.put(node.getMyOwnCode(), result);
		}
		
	}
	/*
	 * 确定时间点直接的关系
	 */
	private  HashMap<String, LocalDate> nodeWeekTimeStampRelation (BkChanYeLianTreeNode node,LocalDate curstart,LocalDate curend, LocalDate requiredstart, LocalDate requiredend) 
	{
		HashMap<String,LocalDate> startend = new HashMap<String,LocalDate> (); 
		
		if(  CommonUtility.isInSameWeek(curstart,requiredstart) &&  CommonUtility.isInSameWeek(requiredend,curend)    ) {//数据完整
			startend.put("searchstart", requiredstart);
			startend.put("searchend",  curend);
//			return startend;
		}	
		else if( (requiredstart.isAfter(curstart) || requiredstart.isEqual(curstart) ) && (requiredend.isBefore(curend) || requiredend.isEqual(curend) ) ) {  //数据完整
			startend.put("searchstart", requiredstart);
			startend.put("searchend",  requiredend);
//			return startend;
		}
		else if( !CommonUtility.isInSameWeek(curstart,requiredstart)  && requiredstart.isBefore(curstart) //部分完整1,前缺失
				&& (requiredend.isBefore(curend) || CommonUtility.isInSameWeek(requiredend,curend) )    ) {
			LocalDate searchstart,searchend;
//			searchstart = requiredstart; 
//			searchend = curstart.with(DayOfWeek.SATURDAY).minus(1,ChronoUnit.WEEKS);
			startend.put("searchstart", requiredstart);
			startend.put("searchend",  curend);
//			return startend;
		}
		else if ( (CommonUtility.isInSameWeek(curstart,requiredstart) || requiredstart.isAfter(curstart) )  //部分完整2，后缺失
				&& requiredend.isAfter(curend) &&  !CommonUtility.isInSameWeek(requiredend,curend)    ) {
			LocalDate searchstart,searchend;
//			searchstart = curend.with(DayOfWeek.MONDAY).plus(1,ChronoUnit.WEEKS); 
//			searchend = requiredend;
			startend.put("searchstart", curstart);
			startend.put("searchend", requiredend);
//			return startend;
		}
		else if( requiredstart.isBefore(curstart) && requiredend.isAfter(curend)  ) {//部分完整3， 前后双缺失，这种情况目前的设置似乎不可能发生，暂时不写
//			logger.info("部分完整3， 前后双缺失!");
			startend.put("searchstart", requiredstart);
			startend.put("searchend", requiredend);
//			return startend;
		}
		
		//和node当前记录的启末日期比较
		NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
		LocalDate nodestart = nodexdata.getRecordsStartDate();
		LocalDate nodeend = nodexdata.getRecordsEndDate();
		if(nodestart.isAfter(startend.get("searchstart")))
			startend.put("searchstart", nodestart);
		if(nodeend.isBefore(startend.get("searchend")))
			startend.put("searchend", nodeend);

		return startend;
	}
	
	public void clearCsvDataSet ()
	{
		bknodetimerange.clear();
		stocknodetimerange.clear();
		nodecontentArrayList.clear();
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
				
		     	clearCsvDataSet ();
			}
		});
		
	}
	/*
	 * 
	 */
	protected void appendRequiredBankuai() 
	{
		if(cbxappendmorebk.isSelected()) {
			BkChanYeLianTreeNode node = allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("880471", BanKuaiAndStockBasic.TDXBK );
			NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
			LocalDate curstart = nodexdata.getRecordsStartDate();
			LocalDate curend = nodexdata.getRecordsEndDate();
			this.addBanKuaiToCsvList (node,curstart,curend);
			
			node = this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("880472", BanKuaiAndStockBasic.TDXBK );
			nodexdata = node.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
			curstart = nodexdata.getRecordsStartDate();
			curend = nodexdata.getRecordsEndDate();
			this.addBanKuaiToCsvList (node,curstart,curend);
		}
	}
	/*
	 * 
	 */
	protected File exportToCsvFile() 
	{
		String[] csvheader = {"id","name","date","chenjiaoer","cjezhanbi","chengjiaoerMaxWk","cjezhanbiMaxWk","cjezhanbiMinWk","HuanShouLv"};
		
		
		ExportToCsvFile etcsv = new ExportToCsvFile ();
		//
		for(Map.Entry<String, String> entry : bknodetimerange.entrySet() ) {
			String nodecode = entry.getKey();
			String daterange = entry.getValue();
			
			List<String> curnodedate = Splitter.on("|").omitEmptyStrings().splitToList(daterange);
			LocalDate curstart = LocalDate.parse(curnodedate.get(0));
			LocalDate curend = LocalDate.parse(curnodedate.get(1));
			
			BkChanYeLianTreeNode node = this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(nodecode, BanKuaiAndStockBasic.TDXBK );
			NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
			LocalDate tmpdate = curstart;
			do  {
				Double cje = nodexdata.getChengJiaoEr(tmpdate, 0);
				if(cje != null) {
					Integer cjemaxwk = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(tmpdate,0);
					Double cjezb = nodexdata.getChenJiaoErZhanBi(tmpdate, 0);
					Integer cjezbmaxwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(tmpdate, 0);
					Integer cjezbminwk = nodexdata.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(tmpdate, 0);
					Double hsl = null;
					
					unifiedAddingCsvData (node.getMyOwnCode(),node.getMyOwnName(),tmpdate,cje,cjezb,cjemaxwk,cjezbmaxwk,cjezbminwk,hsl);
				} 
				
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			} while (tmpdate.isBefore( curend) || tmpdate.isEqual(curend));
		}
		//
		for(Map.Entry<String, String> entry : stocknodetimerange.entrySet() ) {
			String nodecode = entry.getKey();
			String daterange = entry.getValue();
			
			List<String> curnodedate = Splitter.on("|").omitEmptyStrings().splitToList(daterange);
			LocalDate curstart = LocalDate.parse(curnodedate.get(0));
			LocalDate curend = LocalDate.parse(curnodedate.get(1));
			
			BkChanYeLianTreeNode node = this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(nodecode, BanKuaiAndStockBasic.TDXGG );
			NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
			LocalDate tmpdate = curstart;
			do  {
				Double cje = nodexdata.getChengJiaoEr(tmpdate, 0);
				if(cje != null) {
					Integer cjemaxwk = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(tmpdate,0);
					Double cjezb = nodexdata.getChenJiaoErZhanBi(tmpdate, 0);
					Integer cjezbmaxwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(tmpdate, 0);
					Integer cjezbminwk = nodexdata.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(tmpdate, 0);
					Double hsl = ((StockNodeXPeriodData)nodexdata).getSpecificTimeHuanShouLv(tmpdate, 0);;
					
					unifiedAddingCsvData (node.getMyOwnCode(),node.getMyOwnName(),tmpdate,cje,cjezb,cjemaxwk,cjezbmaxwk,cjezbminwk,hsl);
					
				} 
				
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			} while (tmpdate.isBefore( curend) || tmpdate.isEqual(curend));

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
		
		
	private void unifiedAddingCsvData(String nodecode, String nodename, LocalDate tmpdate, Double cje, Double cjezb, Integer cjemaxwk, Integer cjezbmaxwk, Integer cjezbminwk, Double hsl) 
	{
		//代码必须加“‘”，否则导出后000前缀会消失，很麻烦
		String[] csvline = {	"'" + nodecode + "'", nodename,tmpdate.with(DayOfWeek.FRIDAY).toString(),
								cje.toString(),cjezb.toString(),
								cjemaxwk.toString(),cjezbmaxwk.toString(),cjezbminwk.toString(),
								String.valueOf(hsl)
								};
		nodecontentArrayList.add(csvline);
	}
	
	
	private JTable nodelisttable;
	private JButton btncsv;
	private JCheckBox cbxappendmorebk;
	private void initializeGui() 
	{
		JScrollPane scrollPane = new JScrollPane();
		
		btncsv = new JButton("\u5BFC\u51FA\u5230CSV");
		
		cbxappendmorebk = new JCheckBox("\u9644\u52A0\u57FA\u51C6\u677F\u5757(\u94F6\u884C/\u8BC1\u5238)");
		cbxappendmorebk.setSelected(true);
		
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
