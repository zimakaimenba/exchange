package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;
import com.exchangeinfomanager.asinglestockinfo.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi.ExportCondition;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi.ExportTask2;
import com.exchangeinfomanager.bankuaifengxi.CategoryBarOfName.BanKuaiFengXiCategoryBarChartHuanShouLvPnl;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.subgui.JStockComboBox;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
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
import javax.swing.SwingWorker.StateValue;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.JCheckBox;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import javax.swing.JProgressBar;

public class GeGuShiZhiFenXi extends JDialog 
{
	/**
	 * Create the dialog.
	 */
	public GeGuShiZhiFenXi() 
	{
		initializeGui ();
		this.sysconfig = SystemConfigration.getInstance();
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
	private ShiZhiFenXiExportTask exporttask;
	private SystemConfigration sysconfig;
	
	/*
	 * 
	 */
	public String getStockShiZhiDuiBiFenXiResult (String stockcode,LocalDate searchdate)
	{
		if(!searchdate.equals(datachooser.getLocalDate()))
			datachooser.setLocalDate(searchdate);
		
		String outputresult = displayStockFenXiResult (stockcode,false);
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
		
		((GeGuShiZhiFenXiResultTableModel)tableStockShiZhiFenXi.getModel()).removeAllRows();
		
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
	protected String displayStockFenXiResult(String stockcode,boolean showInTable) 
	{
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		Stock stocksearched = (Stock)this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(stockcode, BanKuaiAndStockBasic.TDXGG);
		boolean hasRecordinPeriod = ((StockNodeXPeriodData)stocksearched.getNodeXPeroidData( StockGivenPeriodDataItem.WEEK)).hasRecordInThePeriod(datachooser.getLocalDate(), 0);
		if(!hasRecordinPeriod) 
			stocksearched = this.allbksks.getStock(stocksearched, datachooser.getLocalDate(), StockGivenPeriodDataItem.WEEK);
		
		Double stockshizhi = ((StockNodeXPeriodData)stocksearched.getNodeXPeroidData( StockGivenPeriodDataItem.WEEK))
				.getSpecificTimeLiuTongShiZhi(datachooser.getLocalDate(), 0) ;
		
		if(stockshizhi == null)
			return null;

		
		
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
			if(nodezhanbi == null)
				;
			try {
				allzhanbi = allzhanbi + nodezhanbi; 
			} catch (java.lang.NullPointerException e) {
				logger.info(node.getMyOwnCode() + node.getMyOwnName() +"数据出现问题。日期：" + datachooser.getLocalDate() );
			}
			
			if(node.getMyOwnCode().equalsIgnoreCase(stocksearched.getMyOwnCode()))
				stocksearchedzhanbi = nodezhanbi;
		}
		
		if(showInTable) {
			((GeGuShiZhiFenXiResultTableModel)tableStockShiZhiFenXi.getModel()).refresh(stocklistofrangeshizhi, datachooser.getLocalDate(), StockGivenPeriodDataItem.WEEK); 
		}
		
		Collections.sort(stocklistofrangeshizhi, new NodeZhanBiComparator (datachooser.getLocalDate(),0,StockGivenPeriodDataItem.WEEK) );
		int zhanbipaiming = stocklistofrangeshizhi.indexOf(stocksearched );

		Collections.sort(stocklistofrangeshizhi, new NodeShiZhiComparator (datachooser.getLocalDate(),0,StockGivenPeriodDataItem.WEEK,"liutong") );
		int shizhipaiming = stocklistofrangeshizhi.indexOf(stocksearched );
		
		NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
	    percentFormat.setMinimumFractionDigits(4);
	    NumberFormat doubleFormat = NumberFormat.getNumberInstance();
	    doubleFormat.setMinimumFractionDigits(3);
		resultoutput = resultoutput + "共有" + stocksetofrangeshizhi.size() + "个股票。\n";
		resultoutput = resultoutput + "平均占比" + percentFormat.format (allzhanbi/stocksetofrangeshizhi.size() ) + "\n";
		resultoutput = resultoutput + stocksearched.getMyOwnCode() + stocksearched.getMyOwnName() + ":市值" + doubleFormat.format(stockshizhi/100000000) + "亿" +  "个股占比:" + percentFormat.format (stocksearchedzhanbi)  
							+ "流通市值排名：" + (shizhipaiming+1) + "占比排名：" + (zhanbipaiming+1) + "\n";
		
	
		hourglassCursor = null;
		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
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
	public void exportBanKuaiWithGeGuOnCondition2 () 
	{
		String msg =  "导出耗时较长，请先确认条件是否正确。\n是否导出？";
		int exchangeresult = JOptionPane.showConfirmDialog(null,msg , "确实导出？", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
				return;
		
		LocalDate curselectdate = datachooser.getLocalDate();
		String dateshowinfilename = null;
		String globeperiod = StockGivenPeriodDataItem.WEEK;
		if(globeperiod  == null  || globeperiod.equals(StockGivenPeriodDataItem.WEEK))
			dateshowinfilename = "week" + curselectdate.with(DayOfWeek.FRIDAY).toString().replaceAll("-","");
		else if(globeperiod.equals(StockGivenPeriodDataItem.DAY))
			dateshowinfilename = "day" + curselectdate.toString().replaceAll("-","");
		else if(globeperiod.equals(StockGivenPeriodDataItem.MONTH))
			dateshowinfilename = "month" +  curselectdate.withDayOfMonth(curselectdate.lengthOfMonth()).toString().replaceAll("-","");
		String exportfilename = sysconfig.getShiZhiFenXiFilesStoredPath () + "TDX市值分析结果" + dateshowinfilename + ".xml";
		File filefmxx = new File( exportfilename );
		
		if(!filefmxx.getParentFile().exists()) {  
            //如果目标文件所在的目录不存在，则创建父目录  
            logger.debug("目标文件所在目录不存在，准备创建它！");  
            if(!filefmxx.getParentFile().mkdirs()) {  
                System.out.println("创建目标文件所在目录失败！");  
                return ;  
            }  
        }  
		
		try {
				if (filefmxx.exists()) {
					filefmxx.delete();
					filefmxx.createNewFile();
				} else
					filefmxx.createNewFile();
		} catch (Exception e) {
				e.printStackTrace();
				return ;
		}

		exporttask = new ShiZhiFenXiExportTask( curselectdate,filefmxx);
		exporttask.addPropertyChangeListener(new PropertyChangeListener() {
		      @Override
		      public void propertyChange(final PropertyChangeEvent eventexport) {
		        switch (eventexport.getPropertyName()) {
		        case "progress":
		        	progressBar.setIndeterminate(false);
		        	progressBar.setString("正在导出..." + (Integer) eventexport.getNewValue() + "%");
		          break;
		        case "state":
		          switch ((StateValue) eventexport.getNewValue()) {
		          case DONE:
		        	exportCancelAction.putValue(Action.NAME, "导出条件个股");
		            try {
		              final int count = exporttask.get();
		              //保存XML
		              int exchangeresult = JOptionPane.showConfirmDialog(null, "导出完成，是否打开" + filefmxx.getAbsolutePath() + "查看","导出完成", JOptionPane.OK_CANCEL_OPTION);
		      		  if(exchangeresult == JOptionPane.CANCEL_OPTION) {
		      			  progressBar.setString(" ");
		      			  return;
		      		  }
		      		  try {
		      			String path = filefmxx.getAbsolutePath();
		      			Runtime.getRuntime().exec("explorer.exe /select," + path);
		      		  } catch (IOException e1) {
		      				e1.printStackTrace();
		      		  }
		      		progressBar.setString(" ");
		      		System.gc();
		            } catch (final CancellationException e) {
		            	try {
							exporttask.get();
						} catch (InterruptedException | ExecutionException e1) {
							e1.printStackTrace();
						}
		            	progressBar.setIndeterminate(false);
		            	progressBar.setValue(0);
		            	JOptionPane.showMessageDialog(null, "导出条件个股被终止！", "导出条件个股",JOptionPane.WARNING_MESSAGE);
		            } catch (final Exception e) {
//		              JOptionPane.showMessageDialog(Application.this, "The search process failed", "Search Words",
//		                  JOptionPane.ERROR_MESSAGE);
		            }

		            exporttask = null;
		            break;
		          case STARTED:
		          case PENDING:
		        	  exportCancelAction.putValue(Action.NAME, "取消导出");
		        	  progressBar.setVisible(true);
		        	  progressBar.setIndeterminate(true);
		            break;
		          }
		          break;
		        }
		      }
		    });
		
		exporttask.execute();
//		try {
//			exporttask.get();
//		} catch (InterruptedException | ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	/*
	 * 
	 */
	private void createEvents() 
	{
		tfldstockcode.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) 
			{
				if(arg0.getStateChange() == ItemEvent.SELECTED) {
						BkChanYeLianTreeNode stockcode = tfldstockcode.getUserInputNode ();
						String outputresult = displayStockFenXiResult (stockcode.getMyOwnCode(),true);
						outputResutlToGui(outputresult);
				}
				
				if(arg0.getStateChange() == ItemEvent.DESELECTED) {

				
				}
			}
		});
		
		datachooser.addPropertyChangeListener(new PropertyChangeListener() {
		    @Override
		    public void propertyChange(PropertyChangeEvent e) {
		    	if("date".equals(e.getPropertyName() ) ) {
		    		setupStocksByShiZhiLevels ();
		    	}
		    }
		});
		
		btnexportfenxiresult.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				BkChanYeLianTreeNode stockcode = tfldstockcode.getUserInputNode ();
				String outputresult = displayStockFenXiResult (stockcode.getMyOwnCode(),true);
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
	private JStockComboBox tfldstockcode;
	private JTable tableStockShiZhiFenXi;
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
	private JButton btnexportfenxiresult;
	private JProgressBar progressBar;
	
	private Action exportCancelAction;
	private Action bkfxCancelAction;
	
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
		
		GeGuShiZhiFenXiResultTableModel ggszfx = new GeGuShiZhiFenXiResultTableModel ();
		tableStockShiZhiFenXi = new JTable(ggszfx) {
			 /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				 
			        Component comp = super.prepareRenderer(renderer, row, col);
			        int modelRow = this.convertRowIndexToModel(row);
			        Object value = getModel().getValueAt(modelRow, col);
			        
			        try{
			        	if(value == null)
			        		return null;
			        	String currentdisplayedstock = tfldstockcode.getSelectedItem().toString().substring(0, 6);
			            if (value.toString().trim().equals(currentdisplayedstock) && 1==col) { 
			            	comp.setBackground(Color.RED);
			                comp.setForeground(Color.BLACK);
			            } else {
			                comp.setBackground(Color.white);
			                comp.setForeground(Color.BLACK);
			            }
			        } catch (java.lang.NullPointerException e ){
			        	comp.setBackground(Color.white);
		                comp.setForeground(Color.BLACK);
			        }
			        
			        if( col == 3) {
			        	String valuepect = "";
			        	try {
			        		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(value.toString()).doubleValue();
			        		 
			        		 NumberFormat doubleFormat = NumberFormat.getNumberInstance();
				             doubleFormat.setMinimumFractionDigits(3);
			            	 valuepect = doubleFormat.format (formatevalue );
			        	} catch (java.lang.NullPointerException e) {
			        		valuepect = "";
				    	}catch (java.lang.NumberFormatException e)   	{
			        		e.printStackTrace();
			        	} catch (ParseException e) {
							e.printStackTrace();
						}
			        	((JLabel)comp).setText(valuepect);
			        }
			        if( col == 4) {
			        	String valuepect = "";
			        	try {
			        		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(value.toString()).doubleValue();
			        		 
			        		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
				        	 percentFormat.setMinimumFractionDigits(4);
			            	 valuepect = percentFormat.format (formatevalue );
			        	} catch (java.lang.NullPointerException e) {
			        		valuepect = "";
				    	}catch (java.lang.NumberFormatException e)   	{
			        		e.printStackTrace();
			        	} catch (ParseException e) {
							e.printStackTrace();
						}
			        	((JLabel)comp).setText(valuepect);
			        }
//			        if( col == 5) {
//			        	String valuepect = "";
//			        	try {
//			        		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(value.toString()).doubleValue();
//			        		 
//			        		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
//				        	 percentFormat.setMinimumFractionDigits(4);
//			            	 valuepect = percentFormat.format (formatevalue );
//			        	} catch (java.lang.NullPointerException e) {
//			        		valuepect = "";
//				    	}catch (java.lang.NumberFormatException e)   	{
//			        		e.printStackTrace();
//			        	} catch (ParseException e) {
//							e.printStackTrace();
//						}
//			        	((JLabel)comp).setText(valuepect);
//			        }
			        
			        return comp;
			    }
				    
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
		TableRowSorter<GeGuShiZhiFenXiResultTableModel> sorter = new TableRowSorter<GeGuShiZhiFenXiResultTableModel>((GeGuShiZhiFenXiResultTableModel)tableStockShiZhiFenXi.getModel());
		tableStockShiZhiFenXi.setRowSorter(sorter);
		scrollPane.setViewportView(tableStockShiZhiFenXi);
		
		datachooser = new JLocalDateChooser();
		
		btnprevious = new JButton("<");
		
		btnlater = new JButton(">");
		
		btnreset = new JButton("\u91CD\u7F6E");
		
		tfldstockcode = new JStockComboBox(6);
		 
//		tfldstockcode.setColumns(10);
		
//		btnexportfenxiresult = new JButton("\u5BFC\u51FA");
		
		
		exportCancelAction = new AbstractAction("导出") {

		      private static final long serialVersionUID = 4669650683189592364L;

		      @Override
		      public void actionPerformed(final ActionEvent e) {
		    	
		        if (exporttask == null) { 
        			exportBanKuaiWithGeGuOnCondition2();
		        } else {
		        	exporttask.cancel(true);
		        }
		      }
		 };
		 btnexportfenxiresult = new JButton(exportCancelAction);
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(7)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(panel, GroupLayout.PREFERRED_SIZE, 282, GroupLayout.PREFERRED_SIZE)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 282, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 899, Short.MAX_VALUE)
								.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 899, Short.MAX_VALUE)))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 282, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE)
							.addGap(9)
							.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(scrollPane, 0, 0, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 234, GroupLayout.PREFERRED_SIZE)
					.addGap(64))
		);
		contentPanel.setLayout(gl_contentPanel);
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
			tfldshizhilevel.setText("15,28,40,90,200,500,800,3000");
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
		
		progressBar = new JProgressBar();
		progressBar.setFont(new Font("宋体", Font.PLAIN, 9));
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(tfldstockcode, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(datachooser, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(4)
							.addComponent(btnprevious)
							.addGap(17)
							.addComponent(btnlater)
							.addGap(4)
							.addComponent(btnreset))
						.addGroup(gl_panel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnexportfenxiresult)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(progressBar, 0, 0, Short.MAX_VALUE)))
					.addGap(7))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(1)
							.addComponent(datachooser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnprevious)
						.addComponent(btnlater)
						.addComponent(btnreset))
					.addGap(4)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(tfldstockcode, GroupLayout.PREFERRED_SIZE, 34, Short.MAX_VALUE)
						.addComponent(btnexportfenxiresult, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
	
		
	}
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



class GeGuShiZhiFenXiResultTableModel extends DefaultTableModel 
{
	private List<Stock> stockslist;
	private LocalDate searchdate;
	private String period;
	
	String[] jtableTitleStrings = { "排名", "代码","名称","市值(亿)","占比","换手率"};
	
	GeGuShiZhiFenXiResultTableModel ()
	{
	}

	public void refresh  (List<Stock> stocklistofrangeshizhi,LocalDate searchdate1,String period1)
	{
		this.stockslist = stocklistofrangeshizhi;
		this.searchdate = searchdate1;
		this.period = period1;

		
		this.fireTableDataChanged();
	}
	 public int getRowCount() 
	 {
		 if(this.stockslist == null)
			 return 0;
		 else if(this.stockslist.isEmpty()  )
			 return 0;
		 else
			 return this.stockslist.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(stockslist.isEmpty())
	    		return null;
	    	
	    	Object value = "??";
	    	Stock stock = stockslist.get(rowIndex);
	    	
	    	switch (columnIndex) {
            case 0:
                value = rowIndex+1;
                break;
            case 1:
            	String stockcode = stock.getMyOwnCode();
            	value = stockcode;
                break;
            case 2:
            	String stockname = stock.getMyOwnName();
            	value = stockname;
                break;
            case 3:
            	Double shizhi = ((StockNodeXPeriodData)stock.getNodeXPeroidData( period)).getSpecificTimeLiuTongShiZhi(searchdate, 0)/100000000;
	    	    value = shizhi  ;
                break;
            case 4:
            	Double zhanbi = ((StockNodeXPeriodData)stock.getNodeXPeroidData( period)).getChenJiaoErZhanBi(searchdate, 0);
        	    value = zhanbi;

        	    break;
            case 5:
            	Double huanshoulv = ((StockNodeXPeriodData)stock.getNodeXPeroidData( period)).getSpecificTimeHuanShouLv(searchdate, 0);
        	    value = huanshoulv;

        	    break;
	    	}

        return value;
	  }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = Integer.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = String.class;
			          break;
		        case 3:
			          clazz = Double.class;
			          break;
		        case 4:
			          clazz = Double.class;
			          break;
		        case 5:
			          clazz = Double.class;
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
	    	return false;
		}
	    
	    public Stock getStocksAt(int row) {
	        return stockslist.get(row);
	    }
	    
	    public void removeAllRows ()
	    {
	    	if(stockslist != null) {
	    		this.stockslist.clear();
		    	this.fireTableDataChanged();
	    	}
	    	
	    }
}


class ShiZhiFenXiExportTask extends SwingWorker<Integer, String>  
{
	private LocalDate requiredexportdate;
	private File fxexportfile;
	
	public ShiZhiFenXiExportTask (LocalDate exportdate,File exportfile)
	{
		this.fxexportfile = exportfile;
		this.requiredexportdate = exportdate;
	}

	@Override
	protected Integer doInBackground() throws Exception 
	{
		
		return null;
	}
}