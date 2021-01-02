package com.exchangeinfomanager.gui.subgui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.SystemAudioPlayed;
import com.exchangeinfomanager.commonlib.UserSelectingForMultiSameCodeNode;
import com.exchangeinfomanager.commonlib.WrapLayout;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.exchangeinfomanager.zhidingyibankuai.TDXZhiDingYiBanKuaiServices;
import com.google.common.base.Strings;
import com.google.common.io.Files;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import java.util.HashMap;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import java.io.IOException;
import java.text.Collator;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import java.awt.Color;


import javax.swing.DefaultComboBoxModel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class ImportTDXData extends JDialog {
	

	/**
	 * Create the dialog.
	 * @param bkdbopt 
	 */
	public ImportTDXData() 
	{
		this.bkdbopt = new BanKuaiDbOperation ();
		this.sysconfig = new SetupSystemConfiguration(); 
//		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
		initializeGui ();

		iniiazlizeZdyGui ();
		formateGui ();
		createEvents ();
	}
	
	

	JCheckBox[] zdybkckbxs;
	Map<String, String> zdybkmap; //从tdx得到的自定义板块设置
	Map<String, String> zdybkmapfromfile; //从用户选择的文件直接得到的自定义板块设置
	private SetupSystemConfiguration sysconfig;
//	private AllCurrentTdxBKAndStoksTree allbksks;
//	private BanKuaiAndChanYeLian2 bkcyl;
	private BanKuaiDbOperation bkdbopt;
	
	private void formateGui() 
	{
		LocalTime tdytime = LocalTime.now(); //如果是在交易时间导入数据的话，当天的数据还没有，所以要判断一下
		Calendar cal = Calendar.getInstance();//可以对每个时间域单独修改
//		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int wkday = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if( (chkbxforenotimportatwork.isSelected() && wkday<=5 && wkday>=1) && (tdytime.compareTo(LocalTime.of(9, 0, 0)) >0 && tdytime.compareTo(LocalTime.of(16, 35, 0)) <0)  ) {
			chbxdaorutdxsysbk.setSelected(false);
			cbximporttdxgeguinfo.setSelected(false);
			chbxdaorutdxsysbkvol.setSelected(false);
			cbxImportShGeGuVol.setSelected(false);
			cbxImportSzGeGuVol.setSelected(false);
			ckbxnetease.setSelected(false);
			
			chbxdaorutdxsysbk.setEnabled(false);
			cbximporttdxgeguinfo.setEnabled(false);
			chbxdaorutdxsysbkvol.setEnabled(false);
			cbxImportShGeGuVol.setEnabled(false);
			cbxImportSzGeGuVol.setEnabled(false);
			ckbxnetease.setEnabled(false);
			
			lblstatus.setText("交易日9:00-16:35期间不可以导入和成交量相关信息！");
		}
		
	}
	
	private void iniiazlizeZdyGui() 
	{
		TDXZhiDingYiBanKuaiServices tdxzdysvs = new TDXZhiDingYiBanKuaiServices ();
		zdybkmap = tdxzdysvs.getZhiDingYiBanKuaiLists();
		
		ArrayList<String> zdybknames = new ArrayList<String> ();
		try {
			zdybknames.addAll(zdybkmap.keySet());
			
			Collator collator = Collator.getInstance(Locale.CHINESE); //用中文排序
	 		Collections.sort(zdybknames,collator);
	 		
			zdybkckbxs = new JCheckBox[zdybknames.size()];
			Iterator<String> tmpit = zdybknames.iterator();
			int i=0;
			while (tmpit.hasNext()) {
				String tmpname = tmpit.next();
				zdybkckbxs[i] = new JCheckBox(tmpname);
				pnlZdy.add(zdybkckbxs[i]);
				
				if(tmpname.equals(this.sysconfig.getNameOfGuanZhuZdyBanKuai() ) )
					zdybkckbxs[i].setSelected(true);
				
				i++;
			}
		} catch (java.lang.NullPointerException e) {
			JLabel zdyfilenull = new JLabel ("没有自定义板块");
			pnlZdy.add(zdyfilenull);
		}
		
	}

	private void partThatCanImportDuringWork ()
	{
		//同步自定义板块
		if(chbxdaorutdxzdybk.isSelected() && chbxdaorutdxzdybk.isEnabled() && this.zdybkckbxs != null) { 
			System.out.println("------导入自定义板块开始于" + LocalTime.now() );
			for(JCheckBox tmpbox:zdybkckbxs) {
				if(! tmpbox.isSelected() )
					zdybkmap.remove(tmpbox.getText() );
			}
			File resulttmpfilezdy = bkdbopt.refreshTDXZDYBanKuai (zdybkmap);
			chbxdaorutdxzdybk.setEnabled(false);
			
			try {
				List<String> lines = Files.readLines(resulttmpfilezdy, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
//				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			System.out.println("......导入自定义板块结束"  + LocalTime.now()+ ".......\r\n");
		}
		if(cbximportzdyfromout.isSelected() && cbximportzdyfromout.isEnabled() ) {
			File resulttmpfilezdy = bkdbopt.refreshTDXZDYBanKuai (zdybkmapfromfile);
			cbximportzdyfromout.setEnabled(false);
			
			try {
				List<String> lines = Files.readLines(resulttmpfilezdy, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
//				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			zdybkmapfromfile = null;
		}
		
		//从通达信中导入股票曾用名和现用名的信息
		if(chbximportcym.isSelected() && chbximportcym.isEnabled())	{
			System.out.println("------导入股票曾用名和现用名的信息开始" + LocalTime.now() );
			long start=System.currentTimeMillis(); //获取开始时间
			File resulttmpfilesys = bkdbopt.refreshEverUsedStorkName (); //股票曾用名的信息
			Integer resulttmpfilesys2 = bkdbopt.refreshCurrentUsedStorkNameOfShangHai(); //上海股票现用名的信息
			Integer resulttmpfilesys3 = bkdbopt.refreshCurrentUsedStorkNameOfShenZhen(); //深圳股票现用名的信息
			long end=System.currentTimeMillis(); //获取结束时间
			System.out.println("......导入股票曾用名和现用名的信息结束"  + LocalTime.now() + "导入耗费时间： "+(end-start)+"ms.......\r\n");
			chbximportcym.setEnabled(false);
		}
		
	}
	/*
	 * 
	 */
	private void partThatHasBeImportAfterWsork () 
	{
		
		//从通达信foxpro中导入股票的基本面信息
		if(cbximporttdxgeguinfo.isSelected() && cbximporttdxgeguinfo.isEnabled()) {
					try {
						System.out.println("------导入股票的基本面信息开始" + LocalTime.now());
						long start=System.currentTimeMillis(); //获取开始时间
						File resultimporttdxgegutinfo = this.bkdbopt.refreshStockJiBenMianInfoFromTdxFoxProFile ();
						long end=System.currentTimeMillis(); //获取结束时间
						System.out.println("......导入股票的基本面信息结束" + LocalTime.now() + "....导入耗费时间： "+(end-start)+"ms \r\n");
						
						List<String> lines = Files.readLines(resultimporttdxgegutinfo, sysconfig.charSet());
						for (String line : lines) {
				        	tfldresult.append(line+"\n");
				        }
					} catch (IOException e) {
//						e.printStackTrace();
					} catch (java.lang.NullPointerException e) {
					} 
					
					cbximporttdxgeguinfo.setEnabled(false);
		 }
				
		 //导入通达信定义的板块信息 ，包括概念，行业，风格，指数 板块
		if(chbxdaorutdxsysbk.isSelected() && chbxdaorutdxsysbk.isEnabled()) {
			System.out.println("------导入通达信板块信息开始" + LocalTime.now());
			long start=System.currentTimeMillis(); //获取开始时间
			File resulttmpfilesys = bkdbopt.refreshTDXSystemBanKuai ();
			long end=System.currentTimeMillis(); //获取结束时间
			System.out.println("......导入通达信板块信息结束" + LocalTime.now() + ".....导入耗费时间： "+(end-start)+"ms \r\n");
			chbxdaorutdxsysbk.setEnabled(false);
			
			try {
				List<String> lines = Files.readLines(resulttmpfilesys, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
		}
		

		//同步通达信板块成交量成交额
		if(chbxdaorutdxsysbkvol.isSelected() &&  chbxdaorutdxsysbkvol.isEnabled() ) {
			try {
				System.out.println("------导入上海指数/板块当日成交信息开始" + LocalTime.now());
				
				File resulttmpfilebkamppreck = bkdbopt.preCheckTDXBanKuaiVolAmoToDb ("sh");
//				System.out.println("导入上海板块当日成交信息.....结束");
				List<String> lines = Files.readLines(resulttmpfilebkamppreck, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {e.printStackTrace();
			} catch (java.lang.NullPointerException e) {}
			
			try {
				long start=System.currentTimeMillis(); //获取开始时间
				File resulttmpfilebkamo = bkdbopt.refreshTDXBanKuaiVolAmoToDb("sh");
				long end=System.currentTimeMillis(); //获取结束时间
				System.out.println(".......导入上海指数/板块当日成交信息结束" + LocalTime.now() + ".....导入耗费时间： "+(end-start)+"ms \r\n");
				List<String> lines = Files.readLines(resulttmpfilebkamo, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {e.printStackTrace();
			} catch (java.lang.NullPointerException e) {}
			
			try {
				System.out.println("------导入深圳指数板块当日成交信息开始" + LocalTime.now() );
				File resulttmpfilezhishupreck = bkdbopt.preCheckTDXBanKuaiVolAmoToDb ("sz");
//				System.out.println("导入上海指数板块当日成交信息.....结束");
				List<String> lines = Files.readLines(resulttmpfilezhishupreck, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
				resulttmpfilezhishupreck = null;
			} catch (IOException e) {e.printStackTrace();
			} catch (java.lang.NullPointerException e) {e.printStackTrace();}
			
			try {
				long start=System.currentTimeMillis(); //获取开始时间
				File resulttmpfilezsamo = bkdbopt.refreshTDXBanKuaiVolAmoToDb ("sz");
				long end=System.currentTimeMillis(); //获取结束时间
				System.out.println("......导入深圳指数板块当日成交信息结束" + LocalTime.now() + ".....导入耗费时间： "+(end-start)+"ms \r\n");
				List<String> lines = Files.readLines(resulttmpfilezsamo, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
				resulttmpfilezsamo = null;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			
			chbxdaorutdxsysbkvol.setEnabled(false);
		}
		
		//同步个股成交量
		if(cbxImportSzGeGuVol.isSelected() && cbxImportSzGeGuVol.isEnabled() ) {
			try {
				System.out.println("------导入深证股票当日成交信息开始于" +  LocalTime.now());
				long start=System.currentTimeMillis(); //获取开始时间
				File resulttmpfilebkamo = bkdbopt.refreshTDXGeGuVolAmoToDb("sz");
				long end=System.currentTimeMillis(); //获取结束时间
				System.out.println("......导入深圳股票当日成交信息结束于" + LocalTime.now() + "......导入耗费时间： "+(end-start)+"ms \r\n");
				List<String> lines = Files.readLines(resulttmpfilebkamo, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
				lines = null;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			cbxImportSzGeGuVol.setEnabled(false);
			
		}                                     
		if(cbxImportShGeGuVol.isSelected() && cbxImportShGeGuVol.isEnabled() ) {
			try {
				System.out.println("------导入上证股票当日成交信息开始于" + LocalTime.now() );
				long start=System.currentTimeMillis(); //获取开始时间
				File resulttmpfilebkamo = bkdbopt.refreshTDXGeGuVolAmoToDb("sh");
				long end=System.currentTimeMillis(); //获取结束时间
				System.out.println("......导入上海股票当日成交信息结束于" + LocalTime.now() + "。.....导入耗费时间： "+(end-start)+"ms \r\n");
				List<String> lines = Files.readLines(resulttmpfilebkamo, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
				lines = null;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			cbxImportShGeGuVol.setEnabled(false);
		}
		
		//用户同步完个股和板块成交量后，要update一下板块的类型，以便后用
		if(chbxdaorutdxsysbkvol.isSelected()  || chbxdaorutdxsysbk.isSelected()  ) {
			System.out.println("------更新板块类型开始于" + LocalTime.now() );
			long start=System.currentTimeMillis(); //获取开始时间
			bkdbopt.refreshTDXSystemBanKuaiLeiXing ();
			long end=System.currentTimeMillis(); //获取结束时间
			System.out.println("......更新板块类型结束于" + LocalTime.now() + ".....导入耗费时间： "+(end-start)+"ms \r\n");
		}
		
		
		//导入网易的股票的数据,主要是换手率/市值等数据，
		if(ckbxnetease.isSelected() && ckbxnetease.isEnabled()) { 
			try {
				System.out.println("------导入网易股票数据开始" + LocalTime.now() );
				long start=System.currentTimeMillis(); //获取开始时间
				File resulttmpfilenetease = bkdbopt.importNetEaseStockData ();
				long end=System.currentTimeMillis(); //获取结束时间
				System.out.println("......导入网易股票数据结束" + LocalTime.now()  + ".....导入耗费时间： "+(end-start)+"ms \r\n");
				List<String> lines = Files.readLines(resulttmpfilenetease, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
				e.printStackTrace();
			}
			ckbxnetease.setEnabled(false);
			
		 
		}
		
		if(ckbxbuquanshuju.isSelected() && !Strings.isNullOrEmpty( txfldbqsjnodecode.getText() ) ) {
			checkNodeTimeLineInDbWithInDataFile ( txfldbqsjnodecode.getText()  );
		}

		
//		if(ckbxtushare.isSelected()) { //导入TUSHARE的数据
//			String pythoninterpreter = sysconfig.getPythonInterpreter();
//			String pythonscripts = sysconfig.getPythonScriptsPath ();
//			String cmd = pythoninterpreter + "/python.exe" + "   " + pythonscripts + "exportdailydata.py\"" ;
//			System.out.println(cmd);
//			try {
//				Process p = Runtime.getRuntime().exec(cmd);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		

	}
	
//	private void queKouTongJi ()
//	{
////		ArrayList<Stock> allstocks = bkdbopt.getAllStocks ();
////		for (Stock stock : allstocks ) {
////		    
////
////		}
//		
//		LocalDate lastestdate = bkdbopt.getLatestTDXNodeQueKouDate ();
//		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.allbksks.getAllBkStocksTree().getModel().getRoot();
//		int bankuaicount = allbksks.getAllBkStocksTree().getModel().getChildCount(treeroot);
//        
////		TDXNodes childnode1 = (TDXNodes) allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("300017", BkChanYeLianTreeNode.TDXGG);
////		bkdbopt.tDXNodeQueKouInfo((TDXNodes) childnode1 , lastestdate.plusDays(1));
//		
//		for(int i=0;i< bankuaicount; i++) {
//			
//			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.allbksks.getAllBkStocksTree().getModel().getChild(treeroot, i);
//			if(childnode.getType() != BkChanYeLianTreeNode.TDXGG) 
//				continue;
//			
//		
//			bkdbopt.tDXNodeQueKouInfo((TDXNodes) childnode , lastestdate.plusDays(1));
//		}
//
//
//	}

//	private void importEastMoneyExportFile()
//	{
//		String parsedpath = sysconfig.getEastMoneyDownloadedFilePath ();
//		JFileChooser chooser = new JFileChooser();
//		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//		chooser.setCurrentDirectory(new File(parsedpath) );
//		
//		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
//		    
//		    String linuxpath;
//		    if(chooser.getSelectedFile().isDirectory())
//		    	linuxpath = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
//		    else
//		    	linuxpath = (chooser.getSelectedFile()).toString().replace('\\', '/');
//		    
//		    File emfile = chooser.getSelectedFile();
//		    bkdbopt.importEastMoneyStockData (emfile);
//
//		}
//		
//		
//	}
	/*
	 * 
	 */
	protected void chooseDirectZdyBanKuaiFile() 
	{
		//先选择文件
		String filename = null;
		
		String parsedpath = sysconfig.getTDXModelMatchExportFile ();
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setCurrentDirectory(new File(parsedpath) );
			
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			if(chooser.getSelectedFile().isDirectory())
			   	filename = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
			 else
			   	filename = (chooser.getSelectedFile()).toString().replace('\\', '/');
		} else
			return;
		
		if(!filename.endsWith("EBK") ) { //不是板块文件
			lblstatus.setText("文件不是板块文件！请重新选择文件！");
			return;
		} else {
			zdybkmapfromfile = new HashMap<String, String> ();
			String filerealname = (new File(filename)).getName().replace(".EBK","");
			zdybkmapfromfile.put(filerealname, filename);
			tfldoutzdyfile.setText(filename);
		}
		
		chooser = null;
		filename = null;
	}
	/*
	 * 
	 */
	private void checkNodeTimeLineInDbWithInDataFile (String searchcode)
	{
		UserSelectingForMultiSameCodeNode userselectnode = new UserSelectingForMultiSameCodeNode(searchcode);
		TDXNodes node = userselectnode.getUserSelectedNode();
		if(node == null) {
			lblstatus.setText("代码为" + searchcode + "不存在！");
			return;
		}
		
		Interval nodetimerangeindb = bkdbopt.getNodeDataBaseStoredDataTimeRange (node);
		Interval nodetimerangeinfile = bkdbopt.getNodeExportFileDataTimeRange (node);
		
		java.time.LocalDate requiredstartday = java.time.LocalDate.of(nodetimerangeinfile.getStart().toLocalDate().getYear(), 
				nodetimerangeinfile.getStart().toLocalDate().getMonthOfYear(),
				nodetimerangeinfile.getStart().toLocalDate().getDayOfMonth());
		java.time.LocalDate requiredendday = java.time.LocalDate.of(nodetimerangeinfile.getEnd().toLocalDate().getYear(), 
				nodetimerangeinfile.getEnd().toLocalDate().getMonthOfYear(),
				nodetimerangeinfile.getEnd().toLocalDate().getDayOfMonth());
		lblbqsjsjwj.setText(requiredstartday.toString() + "--" + requiredendday.toString());
		
		java.time.LocalDate nodestart = java.time.LocalDate.of(nodetimerangeindb.getStart().toLocalDate().getYear(), 
				nodetimerangeindb.getStart().toLocalDate().getMonthOfYear(),
				nodetimerangeindb.getStart().toLocalDate().getDayOfMonth());
		java.time.LocalDate nodeend = java.time.LocalDate.of(nodetimerangeindb.getEnd().toLocalDate().getYear(), 
				nodetimerangeindb.getEnd().toLocalDate().getMonthOfYear(),
				nodetimerangeindb.getEnd().toLocalDate().getDayOfMonth());
		lblbqsjdangqian.setText(nodestart.toString() + "--" + nodeend.toString());
		
		
		List<Interval> timeintersection = CommonUtility.getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval(requiredstartday, requiredendday, nodestart, nodeend);
		lblbqsjshijianduan.setText("");
		for(Interval requirediterval : timeintersection) {
//			File exportfile = bkdbopt.getTDXNodesTDXSystemExportFile (node);
			lblbqsjshijianduan.setText(lblbqsjshijianduan.getText() + requirediterval.toString());
			java.time.LocalDate neededstart = java.time.LocalDate.of(requirediterval.getStart().toLocalDate().getYear(), 
					requirediterval.getStart().toLocalDate().getMonthOfYear(),
					requirediterval.getStart().toLocalDate().getDayOfMonth());
			java.time.LocalDate neededend = java.time.LocalDate.of(requirediterval.getEnd().toLocalDate().getYear(), 
					requirediterval.getEnd().toLocalDate().getMonthOfYear(),
					requirediterval.getEnd().toLocalDate().getDayOfMonth());
			
			if(neededstart.isAfter(nodestart)) //现在只对以前的数据补全，近日的数据通过导入即可，无需补全
				continue;
			
			bkdbopt.setVolAmoRecordsFromTDXExportFileToDatabase (node,neededstart.minus(1,ChronoUnit.DAYS),neededend,null); //开始要减少一天，保证最早数据被导入，代码的需要
		}
	}
	
	


	private void createEvents() 
	{
		txfldbqsjnodecode.addKeyListener(new KeyAdapter() {
			
			public void keyPressed(KeyEvent  evt)
			{
				if(evt.getKeyCode() == KeyEvent.VK_ENTER)	{
					String searchcode = txfldbqsjnodecode.getText();
					UserSelectingForMultiSameCodeNode userselectnode = new UserSelectingForMultiSameCodeNode(searchcode);
					TDXNodes node = userselectnode.getUserSelectedNode();
					if(node == null) {
						lblstatus.setText("代码为" + searchcode + "不存在！");
						return;
					}
					
					Interval nodetimerangeindb = bkdbopt.getNodeDataBaseStoredDataTimeRange (node);
					Interval nodetimerangeinfile = bkdbopt.getNodeExportFileDataTimeRange (node);
					
					java.time.LocalDate requiredstartday = java.time.LocalDate.of(nodetimerangeinfile.getStart().toLocalDate().getYear(), 
							nodetimerangeinfile.getStart().toLocalDate().getMonthOfYear(),
							nodetimerangeinfile.getStart().toLocalDate().getDayOfMonth());
					java.time.LocalDate requiredendday = java.time.LocalDate.of(nodetimerangeinfile.getEnd().toLocalDate().getYear(), 
							nodetimerangeinfile.getEnd().toLocalDate().getMonthOfYear(),
							nodetimerangeinfile.getEnd().toLocalDate().getDayOfMonth());
					lblbqsjsjwj.setText(requiredstartday.toString() + "--" + requiredendday.toString());
					
					java.time.LocalDate nodestart = java.time.LocalDate.of(nodetimerangeindb.getStart().toLocalDate().getYear(), 
							nodetimerangeindb.getStart().toLocalDate().getMonthOfYear(),
							nodetimerangeindb.getStart().toLocalDate().getDayOfMonth());
					java.time.LocalDate nodeend = java.time.LocalDate.of(nodetimerangeindb.getEnd().toLocalDate().getYear(), 
							nodetimerangeindb.getEnd().toLocalDate().getMonthOfYear(),
							nodetimerangeindb.getEnd().toLocalDate().getDayOfMonth());
					lblbqsjdangqian.setText(nodestart.toString() + "--" + nodeend.toString());
					
					List<Interval> timeintersection = CommonUtility.getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval(requiredstartday, requiredendday, nodestart, nodeend);
					lblbqsjshijianduan.setText("");
					for(Interval requirediterval : timeintersection) {
						lblbqsjshijianduan.setText(lblbqsjshijianduan.getText() + requirediterval.toString());
					}
				}
			}
		});

		ckbxbuquanshuju.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(ckbxbuquanshuju.isSelected() ) {
					txfldbqsjnodecode.setEnabled(true);
					
					chbximportcym.setEnabled(false);
					chbxdaorutdxsysbk.setEnabled(false);
					cbximporttdxgeguinfo.setEnabled(false);
					chbxdaorutdxsysbkvol.setEnabled(false);
					cbxImportShGeGuVol.setEnabled(false);
					cbxImportSzGeGuVol.setEnabled(false);
					ckbxnetease.setEnabled(false);
					chbxdaorutdxzdybk.setEnabled(false);
					cbximportzdyfromout.setEnabled(false);
					chbxselectall.setEnabled(false);
					
				} else {
					txfldbqsjnodecode.setEnabled(false);
					
					chbximportcym.setEnabled(true);
					chbxdaorutdxsysbk.setEnabled(true);
					cbximporttdxgeguinfo.setEnabled(true);
					chbxdaorutdxsysbkvol.setEnabled(true);
					cbxImportShGeGuVol.setEnabled(true);
					cbxImportSzGeGuVol.setEnabled(true);
					ckbxnetease.setEnabled(true);
					chbxdaorutdxzdybk.setEnabled(true);
					cbximportzdyfromout.setEnabled(true);
					chbxselectall.setEnabled(true);
				}
			}
		});
		
		chkbxforenotimportatwork.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if( chkbxforenotimportatwork.isSelected() ) {
					LocalTime tdytime = LocalTime.now(); //如果是在交易时间导入数据的话，当天的数据还没有，所以要判断一下
					Calendar cal = Calendar.getInstance();//可以对每个时间域单独修改
					int wkday = cal.get(Calendar.DAY_OF_WEEK) - 1;
					
					if( (wkday<=5 && wkday>=1) && (tdytime.compareTo(LocalTime.of(9, 0, 0)) >0 && tdytime.compareTo(LocalTime.of(16, 35, 0)) <0)  ) {
						chbxdaorutdxsysbk.setSelected(false);
						cbximporttdxgeguinfo.setSelected(false);
						chbxdaorutdxsysbkvol.setSelected(false);
						cbxImportShGeGuVol.setSelected(false);
						cbxImportSzGeGuVol.setSelected(false);
						ckbxnetease.setSelected(false);
						
						chbxdaorutdxsysbk.setEnabled(false);
						cbximporttdxgeguinfo.setEnabled(false);
						chbxdaorutdxsysbkvol.setEnabled(false);
						cbxImportShGeGuVol.setEnabled(false);
						cbxImportSzGeGuVol.setEnabled(false);
						ckbxnetease.setEnabled(false);
						
						lblstatus.setText("交易日9:00-16:35期间不可以导入和成交量相关信息！");
					}
				} else {
					String msg =  "交易时段导入交易数据可能导致数据缺失！\n是否继续？";
					int exchangeresult = JOptionPane.showConfirmDialog(null,msg , "确实导入？", JOptionPane.OK_CANCEL_OPTION);
					if(exchangeresult == JOptionPane.CANCEL_OPTION)
							return;
					
//					chbxdaorutdxsysbk.setSelected(true);
//					cbximporttdxgeguinfo.setSelected(true);
//					chbxdaorutdxsysbkvol.setSelected(true);
//					cbxImportShGeGuVol.setSelected(true);
//					cbxImportSzGeGuVol.setSelected(true);
//					ckbxnetease.setSelected(true);
					
					chbxdaorutdxsysbk.setEnabled(true);
					cbximporttdxgeguinfo.setEnabled(true);
					chbxdaorutdxsysbkvol.setEnabled(true);
					cbxImportShGeGuVol.setEnabled(true);
					cbxImportSzGeGuVol.setEnabled(true);
					ckbxnetease.setEnabled(true);
					
					lblstatus.setText("交易日9:00-16:35期间导入成交量相关信息可能导致数据缺失！");
				}
				
			}
		});
		
		cbximportzdyfromout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(cbximportzdyfromout.isSelected()) {
					chbxdaorutdxzdybk.setSelected(false);
					chbxdaorutdxzdybk.setEnabled(false);
					pnlZdy.setEnabled(false);
					for(JCheckBox zjdbk : zdybkckbxs) {
						zjdbk.setEnabled(false);
					}
					chooseDirectZdyBanKuaiFile ();
				} else {
					chbxdaorutdxzdybk.setEnabled(true);
					pnlZdy.setEnabled(true);
					for(JCheckBox zjdbk : zdybkckbxs) {
						zjdbk.setEnabled(true);
					}
					tfldoutzdyfile.setText("");
				}
			}
		});
		
		pnlZdy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				chbxdaorutdxzdybk.setSelected(true);
			}
		});
		
		btnchecksync.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				checkDataSyncResult ();
			}
		});
		
		chbxselectall.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				if(chbxselectall.isSelected()) { 
					cbximporttdxgeguinfo.setSelected(true);
					chbxdaorutdxsysbkvol.setSelected(true);
					cbxImportSzGeGuVol.setSelected(true);
					cbxImportShGeGuVol.setSelected(true);
					chbxdaorutdxzdybk.setSelected(true);
					
					ckbxbuquanshuju.setEnabled(false);
					ckbxbuquanshuju.setSelected(false);
					
					LocalTime tdytime = LocalTime.now(); 
					Calendar cal = Calendar.getInstance();
					int wkday = cal.get(Calendar.DAY_OF_WEEK) - 1;
					//9点前导入数据不选择导入网易数据
					if( (wkday<=5 && wkday>=1) &&  tdytime.compareTo(LocalTime.of(21, 30, 0)) <0  ) 
						ckbxnetease.setSelected(false);
					else
						ckbxnetease.setSelected(true);
					
					if(wkday == 1 || wkday == 4 ) {
						chbxdaorutdxsysbk.setSelected(true);
					}
						
					
					//曾用名和现用名一周只要更新一次，周五即可
//					Calendar cal = Calendar.getInstance();
//					int wkday = cal.get(Calendar.DAY_OF_WEEK) - 1;
					if(wkday>=5)
						chbximportcym.setSelected(true);
					else
						chbximportcym.setSelected(false);
				}
				if(!chbxselectall.isSelected()) { 
					cbximporttdxgeguinfo.setSelected(false);
					chbximportcym.setSelected(false);
					chbxdaorutdxsysbk.setSelected(false);
					chbxdaorutdxsysbkvol.setSelected(false);
					cbxImportSzGeGuVol.setSelected(false);
					cbxImportShGeGuVol.setSelected(false);
					ckbxnetease.setSelected(false);
					chbxdaorutdxzdybk.setSelected(false);
					
					ckbxbuquanshuju.setEnabled(true);
					ckbxbuquanshuju.setSelected(false);
				}
			}
		});
		
		btnStart.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				partThatCanImportDuringWork ();
				partThatHasBeImportAfterWsork();

				lblstatus.setText("同步结束");
				SystemAudioPlayed.playSound();
				
				JOptionPane.showMessageDialog(null, "数据导入完成！请重启本系统！");
			}
		});
		
	}

	protected void checkDataSyncResult() 
	{
		File synccheckresult = bkdbopt.checkTDXDataImportIsCompleted ();
		if(synccheckresult != null) {
			int exchangeresult = JOptionPane.showConfirmDialog(null, "同步通达信数据完整性检查完成，请在" + synccheckresult.getAbsolutePath() + "下查看！是否打开该目录？","检查完毕", JOptionPane.OK_CANCEL_OPTION);
      		  if(exchangeresult == JOptionPane.CANCEL_OPTION)
      				return;
      		  try {
      			String path = synccheckresult.getParent();
//      			Desktop.getDesktop().open(new File( path ));
      			Runtime.getRuntime().exec("explorer.exe /select," + synccheckresult.getAbsolutePath() );
      		  } catch (IOException e1) {
      				e1.printStackTrace();
      		  }
		} else {
//			JOptionPane.showMessageDialog(null, "通达信数据完整！","Warning", JOptionPane.WARNING_MESSAGE);
		}
			
	}

	private final JPanel contentPanel = new JPanel();
	private JProgressBar pbarbankuai;
	private JProgressBar progressBar_2;
	private JButton okButton;
	private JButton btnStart;
	private JTextArea tfldresult;
	private JPanel pnlZdy;
	private JScrollPane scrollPane_1;
	private JCheckBox chbxdaorutdxsysbk;
	private JCheckBox chbxdaorutdxzdybk;
	private JCheckBox chbxdaorutdxsysbkvol;
	private JCheckBox chbximportcym;
	private JProgressBar progressBar;
	private JCheckBox cbximporttdxgeguinfo;
	private JProgressBar progressBar_1;
	private JCheckBox cbxImportSzGeGuVol;
	private JCheckBox cbxImportShGeGuVol;
	private JCheckBox chbxselectall;
	private JButton btnchecksync;
	private JCheckBox ckbxnetease;
	private JComboBox cbximportoptions;
	private JLabel lblstatus;
	private JTextField tfldoutzdyfile;
	private JCheckBox cbximportzdyfromout;
	private JCheckBox chkbxforenotimportatwork;
	private JTextField txfldbqsjnodecode;
	private JCheckBox ckbxbuquanshuju;
	private JLabel lblbqsjdangqian;
	private JLabel lblbqsjsjwj;
	private JLabel lblbqsjshijianduan;
	
	private void initializeGui() 
	{
		setTitle("\u540C\u6B65\u901A\u8FBE\u4FE1\u6570\u636E");
		setBounds(100, 100, 732, 824);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		
		pbarbankuai = new JProgressBar();
		
		progressBar_2 = new JProgressBar(); 
		
		JScrollPane scrollPane = new JScrollPane();
		
		scrollPane_1 = new JScrollPane();
		
		chbxdaorutdxsysbk = new JCheckBox("*\u5BFC\u5165\u901A\u8FBE\u4FE1\u7CFB\u7EDF\u677F\u5757\u548C\u6307\u6570\u4FE1\u606F(\u5468\u4E00\u4E09\u4E94\u540C\u6B65)");
		
		chbxdaorutdxzdybk = new JCheckBox("\u5BFC\u5165\u901A\u8FBE\u4FE1\u81EA\u5B9A\u4E49\u677F\u5757(\u9009\u62E9\u8981\u5BFC\u5165\u7684\u81EA\u5B9A\u4E49\u677F\u5757)");
		
		chbxdaorutdxsysbkvol = new JCheckBox("*\u5BFC\u5165\u901A\u8FBE\u4FE1\u677F\u5757\u53CA\u6307\u6570\u6210\u4EA4\u989D\u4FE1\u606F");
		
		chbximportcym = new JCheckBox("\u5BFC\u5165\u901A\u8FBE\u4FE1\u80A1\u7968\u73B0\u7528\u540D/\u66FE\u7528\u540D\u4FE1\u606F((\u6BCF\u5468\u540C\u6B65\u4E00\u6B21\u5373\u53EF))");
		
		progressBar = new JProgressBar();
		
		cbximporttdxgeguinfo = new JCheckBox("*\u5BFC\u5165\u901A\u8FBE\u4FE1\u4E2A\u80A1\u57FA\u672C\u9762\u4FE1\u606F");
		
		progressBar_1 = new JProgressBar();
		
		cbxImportSzGeGuVol = new JCheckBox("*\u5BFC\u5165\u6DF1\u5733\u4E2A\u80A1\u6210\u4EA4\u91CF\u6210\u4EA4\u989D\u4FE1\u606F");
		
		cbxImportShGeGuVol = new JCheckBox("*\u5BFC\u5165\u6CAA\u5E02\u4E2A\u80A1\u6210\u4EA4\u91CF\u6210\u4EA4\u989D\u4FE1\u606F");
		
		JProgressBar progressBar_3 = new JProgressBar();
		
		JProgressBar progressBar_4 = new JProgressBar();
		
		ckbxnetease = new JCheckBox("\u5BFC\u5165\u7F51\u6613\u8D22\u7ECF\u6BCF\u65E5\u4EA4\u6613\u4FE1\u606F\uFF08\u6362\u624B\u7387/\u5E02\u503C\u7B49\uFF09");
		
		cbximportoptions = new JComboBox();
		cbximportoptions.setEnabled(false);
		cbximportoptions.setModel(new DefaultComboBoxModel(new String[] {"\u5982\u679C\u7F51\u6613\u6570\u636E\u83B7\u53D6\u5931\u8D25\uFF0C\u5219\u4ECE\u5176\u4ED6\u6570\u636E\u6E90\u83B7\u53D6\u6570\u636E", "\u5982\u679C\u7F51\u6613\u6570\u636E\u83B7\u53D6\u5931\u8D25\uFF0C\u76F4\u63A5\u5BFC\u5165\u4E1C\u65B9\u8D22\u5BCC\u6BCF\u65E5\u81EA\u4E0B\u8F7D\u6570\u636E"}));
		
		cbximportzdyfromout = new JCheckBox("\u76F4\u63A5\u4ECE\u6587\u4EF6\u5BFC\u5165\u81EA\u5B9A\u4E49\u677F\u5757");
		
		
		tfldoutzdyfile = new JTextField();
		tfldoutzdyfile.setEnabled(false);
		tfldoutzdyfile.setEditable(false);
		tfldoutzdyfile.setColumns(10);
		
		chkbxforenotimportatwork = new JCheckBox("\u4EA4\u6613\u65E59:00-16:35\u671F\u95F4\u4E0D\u53EF\u4EE5\u5BFC\u5165\u548C\u6210\u4EA4\u91CF\u76F8\u5173\u4FE1\u606F\uFF01");
		chkbxforenotimportatwork.setEnabled(false);
		chkbxforenotimportatwork.setSelected(true);
		
		JSeparator separator = new JSeparator();
		
		ckbxbuquanshuju = new JCheckBox("\u8865\u5168\u6570\u636E:");
		
		
		txfldbqsjnodecode = new JTextField();
		txfldbqsjnodecode.setEnabled(false);
		txfldbqsjnodecode.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("\u4E2A\u80A1\u6216\u677F\u5757\u4EE3\u7801");
		
		JLabel lblNewLabel_1 = new JLabel("\u5F53\u524D\u6570\u636E\u8BB0\u5F55\u5F00\u59CB/\u622A\u81F3\u65F6\u95F4");
		
		lblbqsjdangqian = new JLabel("New label");
		lblbqsjdangqian.setHorizontalAlignment(SwingConstants.LEFT);
		
		JLabel lblNewLabel_3 = new JLabel("\u6570\u636E\u6587\u4EF6\u5F00\u59CB/\u622A\u81F3\u65F6\u95F4");
		
		lblbqsjsjwj = new JLabel("New label");
		lblbqsjsjwj.setHorizontalAlignment(SwingConstants.LEFT);
		
		lblbqsjshijianduan = new JLabel("New label");
		lblbqsjshijianduan.setHorizontalAlignment(SwingConstants.LEFT);
		
		JLabel lblNewLabel_6 = new JLabel("\u8865\u5168\u6570\u636E\u65F6\u95F4\u6BB5");
		
		pnlZdy = new JPanel();
		
		//scrollPane_1.setPreferredSize(scrollPane_1.getPreferredSize());
		scrollPane_1.setViewportView(pnlZdy);
		//pnlZdy.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); WrapLayout
		pnlZdy.setLayout(new WrapLayout(WrapLayout.CENTER, 5, 5)); 
		tfldresult = new JTextArea();
		tfldresult.setLineWrap(true);
		scrollPane.setViewportView(tfldresult);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addComponent(cbximporttdxgeguinfo)
					.addGap(15)
					.addComponent(progressBar_1, GroupLayout.PREFERRED_SIZE, 502, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addComponent(chbximportcym)
					.addGap(36)
					.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addComponent(chbxdaorutdxsysbk)
					.addGap(16)
					.addComponent(pbarbankuai, GroupLayout.PREFERRED_SIZE, 393, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addComponent(chbxdaorutdxzdybk)
					.addGap(10)
					.addComponent(cbximportzdyfromout)
					.addGap(18)
					.addComponent(tfldoutzdyfile, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
					.addGap(11))
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 692, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addComponent(chbxdaorutdxsysbkvol)
					.addGap(21)
					.addComponent(progressBar_2, GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addComponent(cbxImportSzGeGuVol)
					.addGap(10)
					.addComponent(progressBar_4, GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addComponent(cbxImportShGeGuVol)
					.addGap(14)
					.addComponent(progressBar_3, GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addComponent(ckbxnetease)
					.addGap(8)
					.addComponent(cbximportoptions, 0, 410, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 692, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(90)
					.addComponent(lblNewLabel_3)
					.addGap(23)
					.addComponent(lblbqsjsjwj, GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(90)
					.addComponent(lblNewLabel_6)
					.addGap(65)
					.addComponent(lblbqsjshijianduan, GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addComponent(chkbxforenotimportatwork))
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(7)
							.addComponent(ckbxbuquanshuju)
							.addGap(4)
							.addComponent(lblNewLabel)
							.addGap(23)
							.addComponent(txfldbqsjnodecode, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(90)
							.addComponent(lblNewLabel_1)
							.addGap(21)
							.addComponent(lblbqsjdangqian, GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(8)
							.addComponent(cbximporttdxgeguinfo))
						.addComponent(progressBar_1, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
					.addGap(4)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(chbximportcym)
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(chbxdaorutdxsysbk)
						.addComponent(pbarbankuai, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(4)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(chbxdaorutdxzdybk)
								.addComponent(cbximportzdyfromout)))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(5)
							.addComponent(tfldoutzdyfile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(13)
							.addComponent(chbxdaorutdxsysbkvol))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(4)
							.addComponent(progressBar_2, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(cbxImportSzGeGuVol)
						.addComponent(progressBar_4, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(15)
							.addComponent(cbxImportShGeGuVol))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(progressBar_3, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(ckbxnetease)
						.addComponent(cbximportoptions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(4)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(ckbxbuquanshuju)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGap(4)
									.addComponent(lblNewLabel))))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(5)
							.addComponent(txfldbqsjnodecode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_1)
						.addComponent(lblbqsjdangqian))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_3)
						.addComponent(lblbqsjsjwj))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_6)
						.addComponent(lblbqsjshijianduan))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
					.addGap(4)
					.addComponent(chkbxforenotimportatwork))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("\u5173\u95ED");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				getRootPane().setDefaultButton(okButton);
			}
			
			btnStart = new JButton("\u5F00\u59CB\u5BFC\u5165");
			
			chbxselectall = new JCheckBox("\u5168\u9009");
			chbxselectall.setForeground(Color.RED);
			
			btnchecksync = new JButton("\u4E00\u81F4\u6027\u68C0\u67E5");
			btnchecksync.setEnabled(false);
			
			lblstatus = new JLabel("New label");
			
			
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addComponent(chbxselectall)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(lblstatus, GroupLayout.PREFERRED_SIZE, 245, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnStart)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnchecksync)
						.addGap(18)
						.addComponent(okButton)
						.addGap(39))
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(5)
						.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnStart)
							.addComponent(chbxselectall)
							.addComponent(btnchecksync)
							.addComponent(okButton)
							.addComponent(lblstatus)))
			);
			buttonPane.setLayout(gl_buttonPane);
		}
		this.setLocationRelativeTo(null);
		
		
		
	}
}
