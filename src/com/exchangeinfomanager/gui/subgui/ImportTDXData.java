package com.exchangeinfomanager.gui.subgui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.commonlib.ExecutePythonScripts;
import com.exchangeinfomanager.commonlib.SystemAudioPlayed;
import com.exchangeinfomanager.commonlib.UserSelectingForMultiSameCodeNode;
import com.exchangeinfomanager.commonlib.WrapLayout;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.exchangeinfomanager.database.BanKuaiDZHDbOperation;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.JiGouGuDongDbOperation;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.exchangeinfomanager.zhidingyibankuai.TDXZhiDingYiBanKuaiServices;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
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
import java.util.Set;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.text.Collator;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import java.awt.Color;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSeparator;

import org.python.util.PythonInterpreter;
import org.python.core.*;
import net.miginfocom.swing.MigLayout;

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
			cbxImportSzGeGuVol.setSelected(false);
			ckbxnetease.setSelected(false);
			
			chbxdaorutdxsysbk.setEnabled(false);
			cbximporttdxgeguinfo.setEnabled(false);
			chbxdaorutdxsysbkvol.setEnabled(false);
			cbxImportSzGeGuVol.setEnabled(false);
			ckbxnetease.setEnabled(false);
			
			lblstatus.setText("交易日9:00-16:35期间不可以导入和成交量相关信息！");
		}
		
		tfldfloatholder.setText(this.sysconfig.getFloatHoldersFile() ); tfldtopholder.setText(this.sysconfig.getTop10HoldersFile());
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
		
		if(chkbximportgudong.isSelected()) { //导入TUSHARE的股东数据
			System.out.println("------导入股东信息开始" + LocalTime.now() );
			long start=System.currentTimeMillis(); //获取开始时间
			JiGouGuDongDbOperation gudongdbopt = new JiGouGuDongDbOperation ();
			if(ckbxgddatadwnloded.isSelected() )
				gudongdbopt.refreshGuDongData (true, true, tfldfloatholder.getText(), tfldtopholder.getText());
			else
				gudongdbopt.refreshGuDongData (true, true);
			gudongdbopt = null;
			long end=System.currentTimeMillis(); //获取结束时间
			System.out.println("......导入股东信息结束" + LocalTime.now()  + ".....导入耗费时间： "+(end-start)+"ms \r\n");
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
				
//				File resulttmpfilebkamppreck = bkdbopt.preCheckTDXBanKuaiVolAmoToDb ("sh");
////				System.out.println("导入上海板块当日成交信息.....结束");
//				List<String> lines = Files.readLines(resulttmpfilebkamppreck, sysconfig.charSet());
//				for (String line : lines) {
//		        	tfldresult.append(line+"\n");
//		        }
			} catch (java.lang.NullPointerException e) {}
			
			try {
				long start=System.currentTimeMillis(); //获取开始时间
				File resulttmpfilebkamo = bkdbopt.refreshTDXBanKuaiVolAmoToDbBulkImport("sh", 15);
				long end=System.currentTimeMillis(); //获取结束时间
				System.out.println(".......导入上海指数/板块当日成交信息结束" + LocalTime.now() + ".....导入耗费时间： "+(end-start)+"ms \r\n");
//				List<String> lines = Files.readLines(resulttmpfilebkamo, sysconfig.charSet());
//				for (String line : lines) {
//		        	tfldresult.append(line+"\n");
//		        }
			} catch (java.lang.NullPointerException e) {}
			
			try {
				System.out.println("------导入深圳指数板块当日成交信息开始" + LocalTime.now() );
//				File resulttmpfilezhishupreck = bkdbopt.preCheckTDXBanKuaiVolAmoToDb ("sz");
////				System.out.println("导入上海指数板块当日成交信息.....结束");
//				List<String> lines = Files.readLines(resulttmpfilezhishupreck, sysconfig.charSet());
//				for (String line : lines) {
//		        	tfldresult.append(line+"\n");
//		        }
//				resulttmpfilezhishupreck = null;
			} catch (java.lang.NullPointerException e) {e.printStackTrace();}
			
			try {
				long start=System.currentTimeMillis(); //获取开始时间
				File resulttmpfilezsamo = bkdbopt.refreshTDXBanKuaiVolAmoToDbBulkImport ("sz", 15);
				long end=System.currentTimeMillis(); //获取结束时间
				System.out.println("......导入深圳指数板块当日成交信息结束" + LocalTime.now() + ".....导入耗费时间： "+(end-start)+"ms \r\n");
//				List<String> lines = Files.readLines(resulttmpfilezsamo, sysconfig.charSet());
//				for (String line : lines) {
//		        	tfldresult.append(line+"\n");
//		        }
				resulttmpfilezsamo = null;
			} catch (java.lang.NullPointerException e) {			}
			
			
			chbxdaorutdxsysbkvol.setEnabled(false);
		}
		
		//同步个股成交量
		if(cbxImportSzGeGuVol.isSelected() && cbxImportSzGeGuVol.isEnabled() ) {
			try {
				System.out.println("------导入上证股票当日成交信息开始于" + LocalTime.now() );
				long start=System.currentTimeMillis(); //获取开始时间
				File resulttmpfilebkamo = bkdbopt.refreshTDXGeGuVolAmoToDbBulkImport("sh", 15);
				long end=System.currentTimeMillis(); //获取结束时间
				System.out.println("......导入上海股票当日成交信息结束于" + LocalTime.now() + "。.....导入耗费时间： "+(end-start)+"ms \r\n");
//				List<String> lines = Files.readLines(resulttmpfilebkamo, sysconfig.charSet());
//				for (String line : lines) {
//		        	tfldresult.append(line+"\n");
//		        }
//				lines = null;
			} catch (java.lang.NullPointerException e) {}
			
			ExecutePythonScripts.executePythonScriptForExtraData ("EXTRADATAFROMTUSHARE", null); //先让后台python script下载当日的extra data
			
			try {
				System.out.println("------导入深证股票当日成交信息开始于" +  LocalTime.now());
				long start=System.currentTimeMillis(); //获取开始时间
				File resulttmpfilebkamo = bkdbopt.refreshTDXGeGuVolAmoToDbBulkImport("sz", 15);
				long end=System.currentTimeMillis(); //获取结束时间
				System.out.println("......导入深圳股票当日成交信息结束于" + LocalTime.now() + "......导入耗费时间： "+(end-start)+"ms \r\n");
//				List<String> lines = Files.readLines(resulttmpfilebkamo, sysconfig.charSet());
//				for (String line : lines) {
//		        	tfldresult.append(line+"\n");
//		        }
//				lines = null;
			} catch (java.lang.NullPointerException e) {}
			
			cbxImportSzGeGuVol.setEnabled(false);
		}                                     
		
		//用户同步完个股和板块成交量后，要update一下板块的类型，以便后用
		if(chbxdaorutdxsysbkvol.isSelected()  || chbxdaorutdxsysbk.isSelected()  ) {
			System.out.println("------更新板块类型开始于" + LocalTime.now() );
			long start=System.currentTimeMillis(); //获取开始时间
			bkdbopt.refreshTDXSystemBanKuaiLeiXing ();
			long end=System.currentTimeMillis(); //获取结束时间
			System.out.println("......更新板块类型结束于" + LocalTime.now() + ".....导入耗费时间： "+(end-start)+"ms \r\n");
		}
		
		if(chckbximporttushareextradata.isSelected()  )  {
			System.out.println("------导入TUSHARE EXTRA交易数据开始于" + LocalTime.now() );
			
			int trycount =0;
			while(trycount <=3) {
				String savedfilename = sysconfig.getTuShareExtraDataDownloadedFilePath () + "/" + LocalDate.now().toString().replaceAll("-", "") + "dailyexchangedata.csv";
				File savedfile = new File (savedfilename);
				if(!savedfile.exists()) {
					ExecutePythonScripts.executePythonScriptForExtraData ("EXTRADATAFROMTUSHARE", null); //先让后台python script下载当日的extra data
					try {Thread.sleep(1000 * 90);} catch (InterruptedException e) {e.printStackTrace();}
					trycount ++;
				} else
					break;
			}
			
			long start=System.currentTimeMillis(); //获取开始时间
			try {Thread.sleep(1000 * 10);} catch (InterruptedException e) {e.printStackTrace();}
			bkdbopt.refreshExtraStockDataFromTushare (); //估计当日python EXTRA 数据已经下载完成，这里更新extra数据
			long end=System.currentTimeMillis(); //获取结束时间
			System.out.println("......导入TUSHARE EXTRA交易数据结束于" + LocalTime.now() + ".....导入耗费时间： "+(end-start)+"ms \r\n");
		}
		
		//导入网易的股票的数据,主要是换手率/市值等数据，
		if(ckbxnetease.isSelected() && ckbxnetease.isEnabled()) { 
			try {
				System.out.println("------导入网易股票数据开始" + LocalTime.now() );
				long start=System.currentTimeMillis(); //获取开始时间
				bkdbopt.importNetEaseStockData ("SH");
				bkdbopt.importNetEaseStockData ("SZ");
				long end=System.currentTimeMillis(); //获取结束时间
				System.out.println("......导入网易股票数据结束" + LocalTime.now()  + ".....导入耗费时间： "+(end-start)+"ms \r\n");
			} catch (java.lang.NullPointerException e) {e.printStackTrace();	}
			ckbxnetease.setEnabled(false);
		}
		
		if(ckbxbuquanshuju.isSelected()  ) {
			System.out.println("------补全股票数据开始" + LocalTime.now() );
			long start=System.currentTimeMillis(); //获取开始时间
			checkNodeTimeLineInDbWithInDataFile ( txfldbqsjnodecode.getText()  );
			long end=System.currentTimeMillis(); //获取结束时间
			System.out.println("......补全股票数据结束" + LocalTime.now()  + ".....导入耗费时间： "+(end-start)+"ms \r\n");
		}
		
		if(chckbqtushareextra.isSelected() ) {
			LocalDate startdate = dctushareextastart.getLocalDate();
			LocalDate enddate = dctushareextaend.getLocalDate();
			if(startdate != null) {
				if(enddate == null)
					enddate = LocalDate.now();
				System.out.println("------补全TUSHARE股票Extra数据开始" + LocalTime.now() );
				long start=System.currentTimeMillis(); //获取开始时间
				checkNodeTuShareExtraDataInDbWithInDataFile ( startdate, enddate );
				long end=System.currentTimeMillis(); //获取结束时间
				System.out.println("......补全TUSHARE股票Extra数据结束" + LocalTime.now()  + ".....导入耗费时间： "+(end-start)+"ms \r\n");
			}
		}

		if(chckbximportdzhbk.isSelected() ) {
			System.out.println("------导入大智慧板块信息开始" + LocalTime.now() );
			long start=System.currentTimeMillis(); //获取开始时间
			BanKuaiDZHDbOperation dzhdbopt = new BanKuaiDZHDbOperation ();
			dzhdbopt.refreshDZHGaiNianBanKuaiGeGu ();
			dzhdbopt = null;
			long end=System.currentTimeMillis(); //获取结束时间
			System.out.println("......导入大智慧板块信息结束" + LocalTime.now()  + ".....导入耗费时间： "+(end-start)+"ms \r\n");
		}
		
//		if(chkbximportgudong.isSelected()) { //导入TUSHARE的股东数据
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
	/*
	 * 
	 */
//	public void executePythonScriptForExtraData(String scriptkeywords)  
//	{
////		try {
////			Process p = Runtime.getRuntime().exec("C:/Users/Administrator.WIN7U-20140921O/Anaconda3/python E:/stock/stockmanager/thirdparty/python/execsrc/importdailyextradatafromtushare.py");
////		} catch (IOException e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		}
//		String pyscriptname = null;
//		scriptkeywords = scriptkeywords.toUpperCase();
//		switch (scriptkeywords) {
//		case "EXTRADATAFROMTUSHARE":
//			pyscriptname = this.sysconfig.getPythonScriptsPath () + "importdailyextradatafromtushare.py";//"E:/stock/stockmanager/thirdparty/python/execscripts/importdailyextradatafromtushare.py"; //
//			break;
//		case "SHAREHOLDER" :
//			pyscriptname = this.sysconfig.getPythonScriptsPath () + "importshareholder.py";// "E:/stock/stockmanager/thirdparty/python/execscripts/importshareholder.py";
//			break;
//		}
//		String pythoninterpreter = this.sysconfig.getPythonInterpreter();
//		ProcessBuilder processBuilder = new ProcessBuilder(pythoninterpreter,pyscriptname );
//	    processBuilder.redirectErrorStream(true);
//
//	    try {
//			Process process = processBuilder.start();
//		} catch (IOException e1) {e1.printStackTrace();	}
//	}
	/*
	 * 
	 */
	private void checkNodeTuShareExtraDataInDbWithInDataFile(LocalDate startdate, LocalDate enddate) 
	{
		List<LocalDate> differencetimelist = getNodeTuShareExtraDataTimeDifferenceInDBWithSpecificTimeRange(startdate, enddate);
		
		List<Interval> timeintersection = new ArrayList<> ();
		LocalDate intervalstart = differencetimelist.get(0);
		LocalDate intervalmiddle = differencetimelist.get(0);
		for(int i=1;i<differencetimelist.size();i++) {
			LocalDate tmpdate = differencetimelist.get(i);
			
			long daynumber = java.time.temporal.ChronoUnit.DAYS.between(intervalmiddle,tmpdate);
			if(  daynumber == 1.0 ) {
				intervalmiddle = tmpdate;
				continue;
			} else {
				DateTime startdt= new DateTime(intervalstart.getYear(), intervalstart.getMonthValue(), intervalstart.getDayOfMonth(), 0, 0, 0, 0);
				DateTime enddt = new DateTime(intervalmiddle.getYear(), intervalmiddle.getMonthValue(), intervalmiddle.getDayOfMonth(), 0, 0, 0, 0);
				Interval nodeinterval = new Interval(startdt, enddt);
				timeintersection.add(nodeinterval);
				
				intervalstart = tmpdate;
				intervalmiddle = tmpdate;
			}
		}
		//last leg will be configed here 
		DateTime startdt= new DateTime(intervalstart.getYear(), intervalstart.getMonthValue(), intervalstart.getDayOfMonth(), 0, 0, 0, 0);
		DateTime enddt = new DateTime(intervalmiddle.getYear(), intervalmiddle.getMonthValue(), intervalmiddle.getDayOfMonth(), 0, 0, 0, 0);
		Interval nodeinterval = new Interval(startdt, enddt);
		timeintersection.add(nodeinterval);

		for(Interval requirediterval : timeintersection) {
			java.time.LocalDate neededstart = java.time.LocalDate.of(requirediterval.getStart().toLocalDate().getYear(), 
					requirediterval.getStart().toLocalDate().getMonthOfYear(),
					requirediterval.getStart().toLocalDate().getDayOfMonth());
			java.time.LocalDate neededend = java.time.LocalDate.of(requirediterval.getEnd().toLocalDate().getYear(), 
					requirediterval.getEnd().toLocalDate().getMonthOfYear(),
					requirediterval.getEnd().toLocalDate().getDayOfMonth());
		
			bkdbopt.refreshExtraStockDataFromTushare (neededstart,neededend ); //开始要减少一天，保证最早数据被导入，代码的需要
		}
	}
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
	private List<LocalDate> getNodeTimeDifferenceInDBWithDataFile (TDXNodes node)
	{
		Set<LocalDate> nodetimesetindb = bkdbopt.getNodeDataBaseStoredDataTimeSet (node, null, null);
		Set<LocalDate> nodetimesetinfile = bkdbopt.getNodeExportFileDataTimeSet (node);
		
		SetView<LocalDate> differencetime= Sets.difference(nodetimesetinfile,nodetimesetindb  );
		List<LocalDate> differencetimelist = new ArrayList<> (differencetime);
		Collections.sort(differencetimelist);
		
		return differencetimelist;
	}
	private List<LocalDate> getNodeTuShareExtraDataTimeDifferenceInDBWithSpecificTimeRange (LocalDate start, LocalDate end)
	{
		BkChanYeLianTreeNode node = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("999999", BkChanYeLianTreeNode.TDXBK);
		Set<LocalDate> nodetimesetindb = bkdbopt.getNodeDataBaseStoredDataTimeSet ((TDXNodes) node,start, end); //计算有多少个交易日
//		for (Iterator<LocalDate> i = nodetimesetindb.iterator(); i.hasNext();) { 
//			LocalDate element = i.next();
//			if(element.isBefore(start) || element.isAfter(end)) {
//		        i.remove();
//		    }
//		}
		
		BkChanYeLianTreeNode node2 = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("600000", BkChanYeLianTreeNode.TDXGG);
		Set<LocalDate> nodetimesetinfile = bkdbopt.getNodeTuShareExtraDataTimeSet ((Stock) node2, start, end);
		
		SetView<LocalDate> differencetime= Sets.difference(nodetimesetindb, nodetimesetinfile  );
		List<LocalDate> differencetimelist = new ArrayList<> (differencetime);
		Collections.sort(differencetimelist);
		
		return differencetimelist;
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
		
		List<LocalDate> differencetimelist = getNodeTimeDifferenceInDBWithDataFile(node);

		if(differencetimelist.size() > 0)
			lblbqsjshijianduan.setText("将补全时间段从" + differencetimelist.get(0).toString() + "-" + differencetimelist.get(differencetimelist.size() -1 ) + "约" + String.valueOf(differencetimelist.size() ) + "个数据！");
		else {
			lblbqsjshijianduan.setText("数据完整，无需补全。");
			return;
		}
		
		List<Interval> timeintersection = new ArrayList<> ();
		LocalDate intervalstart = differencetimelist.get(0);
		LocalDate intervalmiddle = differencetimelist.get(0);
		for(int i=1;i<differencetimelist.size();i++) {
			LocalDate tmpdate = differencetimelist.get(i);
			
			long daynumber = java.time.temporal.ChronoUnit.DAYS.between(intervalmiddle,tmpdate);
			if(  daynumber == 1.0 ) {
				intervalmiddle = tmpdate;
				continue;
			} else {
				DateTime startdt= new DateTime(intervalstart.getYear(), intervalstart.getMonthValue(), intervalstart.getDayOfMonth(), 0, 0, 0, 0);
				DateTime enddt = new DateTime(intervalmiddle.getYear(), intervalmiddle.getMonthValue(), intervalmiddle.getDayOfMonth(), 0, 0, 0, 0);
				Interval nodeinterval = new Interval(startdt, enddt);
				timeintersection.add(nodeinterval);
				
				intervalstart = tmpdate;
				intervalmiddle = tmpdate;
			}
		}
		//last leg will be configed here 
		DateTime startdt= new DateTime(intervalstart.getYear(), intervalstart.getMonthValue(), intervalstart.getDayOfMonth(), 0, 0, 0, 0);
		DateTime enddt = new DateTime(intervalmiddle.getYear(), intervalmiddle.getMonthValue(), intervalmiddle.getDayOfMonth(), 0, 0, 0, 0);
		Interval nodeinterval = new Interval(startdt, enddt);
		timeintersection.add(nodeinterval);

		for(Interval requirediterval : timeintersection) {
			java.time.LocalDate neededstart = java.time.LocalDate.of(requirediterval.getStart().toLocalDate().getYear(), 
					requirediterval.getStart().toLocalDate().getMonthOfYear(),
					requirediterval.getStart().toLocalDate().getDayOfMonth());
			java.time.LocalDate neededend = java.time.LocalDate.of(requirediterval.getEnd().toLocalDate().getYear(), 
					requirediterval.getEnd().toLocalDate().getMonthOfYear(),
					requirediterval.getEnd().toLocalDate().getDayOfMonth());
		
			bkdbopt.setVolAmoRecordsFromTDXExportFileToDatabase (node,neededstart.minus(1,ChronoUnit.DAYS),neededend.plus(1, ChronoUnit.DAYS),null); //开始要减少一天，保证最早数据被导入，代码的需要
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
					
					List<LocalDate> differencetimelist = getNodeTimeDifferenceInDBWithDataFile(node);
					if(differencetimelist.size() > 0)
						lblbqsjshijianduan.setText("将补全时间段从" + differencetimelist.get(0).toString() + "-" + differencetimelist.get(differencetimelist.size() -1 ) + "约" + String.valueOf(differencetimelist.size() ) + "个数据！");
					else
						lblbqsjshijianduan.setText("数据完整，无需补全。");
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
						cbxImportSzGeGuVol.setSelected(false);
						ckbxnetease.setSelected(false);
						
						chbxdaorutdxsysbk.setEnabled(true);
						cbximporttdxgeguinfo.setEnabled(true);
						chbxdaorutdxsysbkvol.setEnabled(true);
						cbxImportSzGeGuVol.setEnabled(true);
						ckbxnetease.setEnabled(true);
						
						lblstatus.setText("交易日9:00-16:35期间不可以导入和成交量相关信息！");
					}
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
					chbxdaorutdxzdybk.setSelected(true);
					chckbximporttushareextradata.setSelected(true);
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
					
					if(wkday == 1 || wkday == 3 ) {
						chbxdaorutdxsysbk.setSelected(true);
						chckbximportdzhbk.setSelected(true);
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
					ckbxnetease.setSelected(false);
					chbxdaorutdxzdybk.setSelected(false);
					chckbximportdzhbk.setSelected(false);
					ckbxbuquanshuju.setEnabled(true);
					ckbxbuquanshuju.setSelected(false);
					chckbximporttushareextradata.setSelected(false);
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
//		File synccheckresult = bkdbopt.checkTDXDataImportIsCompleted ();
//		if(synccheckresult != null) {
//			int exchangeresult = JOptionPane.showConfirmDialog(null, "同步通达信数据完整性检查完成，请在" + synccheckresult.getAbsolutePath() + "下查看！是否打开该目录？","检查完毕", JOptionPane.OK_CANCEL_OPTION);
//      		  if(exchangeresult == JOptionPane.CANCEL_OPTION)
//      				return;
//      		  try {
//      			String path = synccheckresult.getParent();
////      			Desktop.getDesktop().open(new File( path ));
//      			Runtime.getRuntime().exec("explorer.exe /select," + synccheckresult.getAbsolutePath() );
//      		  } catch (IOException e1) {
//      				e1.printStackTrace();
//      		  }
//		} else {
////			JOptionPane.showMessageDialog(null, "通达信数据完整！","Warning", JOptionPane.WARNING_MESSAGE);
//		}
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
	private JCheckBox chbxselectall;
	private JButton btnchecksync;
	private JCheckBox ckbxnetease;
	private JLabel lblstatus;
	private JTextField tfldoutzdyfile;
	private JCheckBox cbximportzdyfromout;
	private JCheckBox chkbxforenotimportatwork;
	private JTextField txfldbqsjnodecode;
	private JCheckBox ckbxbuquanshuju;
	private JLabel lblbqsjshijianduan;
	private JCheckBox chckbximportdzhbk;
	private JCheckBox chkbximportgudong;
	private JTextField tfldfloatholder;
	private JTextField tfldtopholder;
	private JLocalDateChooser dctushareextastart;
	private JLocalDateChooser dctushareextaend;
	private JCheckBox chckbqtushareextra;
	private JCheckBox chckbximporttushareextradata;
	private JCheckBox ckbxgddatadwnloded;
	private JPanel buttonPane;
	
	private void initializeGui() 
	{
		setTitle("\u540C\u6B65\u901A\u8FBE\u4FE1\u6570\u636E");
		setBounds(100, 100, 731, 900);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
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
		
		cbxImportSzGeGuVol = new JCheckBox("*\u5BFC\u5165\u4E2A\u80A1\u6210\u4EA4\u91CF\u6210\u4EA4\u989D\u4FE1\u606F");
		
		JProgressBar progressBar_4 = new JProgressBar();
		
		ckbxnetease = new JCheckBox("\u5BFC\u5165\u7F51\u6613\u8D22\u7ECF\u6BCF\u65E5\u4EA4\u6613\u4FE1\u606F\uFF08\u6362\u624B\u7387/\u5E02\u503C\u7B49\uFF09");
		
		cbximportzdyfromout = new JCheckBox("\u76F4\u63A5\u4ECE\u6587\u4EF6\u5BFC\u5165\u81EA\u5B9A\u4E49\u677F\u5757");
		
		
		tfldoutzdyfile = new JTextField();
		tfldoutzdyfile.setEnabled(false);
		tfldoutzdyfile.setEditable(false);
		tfldoutzdyfile.setColumns(10);
		
		ckbxbuquanshuju = new JCheckBox("\u8865\u5168\u57FA\u672C\u6570\u636E:");
		
		
		txfldbqsjnodecode = new JTextField();
		txfldbqsjnodecode.setEnabled(false);
		txfldbqsjnodecode.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("\u4E2A\u80A1\u6216\u677F\u5757\u4EE3\u7801");
		
		JLabel lblNewLabel_6 = new JLabel("\u8865\u5168\u6570\u636E\u65F6\u95F4\u6BB5");
		
		pnlZdy = new JPanel();
		
		//scrollPane_1.setPreferredSize(scrollPane_1.getPreferredSize());
		scrollPane_1.setViewportView(pnlZdy);
		//pnlZdy.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); WrapLayout
		pnlZdy.setLayout(new WrapLayout(WrapLayout.CENTER, 5, 5)); 
		tfldresult = new JTextArea();
		tfldresult.setLineWrap(true);
		scrollPane.setViewportView(tfldresult);
		
		lblbqsjshijianduan = new JLabel(" ");
		
		chckbximportdzhbk = new JCheckBox("\u5BFC\u5165\u5927\u667A\u6167\u677F\u5757\u548C\u4E2A\u80A1\u4FE1\u606F");
		
		chkbximportgudong = new JCheckBox("\u5BFC\u5165\u80A1\u4E1C\u6570\u636E(\u6BCF\u5B63\u5EA6\u5BFC\u5165\u4E00\u6B21\u5373\u53EF)");
		
		JLabel lblNewLabel_1 = new JLabel("\u6D41\u901A\u80A1\u4E1C\u6570\u636E\u6587\u4EF6\u540D\u683C\u5F0F");
		
		tfldfloatholder = new JTextField();
		tfldfloatholder.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("\u5341\u5927\u80A1\u4E1C\u6570\u636E\u6587\u4EF6\u540D\u683C\u5F0F");
		
		tfldtopholder = new JTextField();
		tfldtopholder.setColumns(10);
		
		chckbqtushareextra = new JCheckBox("\u8865\u5168TUSHARE\u989D\u5916\u6570\u636E");
		
		JLabel lblNewLabel_3 = new JLabel("\u9009\u62E9\u8865\u5168\u65F6\u95F4\u6BB5");
		
		dctushareextastart = new JLocalDateChooser();
		dctushareextaend = new JLocalDateChooser();
		
		chckbximporttushareextradata = new JCheckBox("\u5BFC\u5165TUSHARE EXTRA\u6BCF\u65E5\u4EA4\u6613\u4FE1\u606F(\u6362\u624B\u7387/\u81EA\u7531\u6D41\u901A\u6362\u624B\u7387/\u5E02\u503C) ");
		
		ckbxgddatadwnloded = new JCheckBox("\u6570\u636E\u6587\u4EF6\u5DF2\u4E0B\u8F7D");
		
		JSeparator separator = new JSeparator();
		
		JSeparator separator_1 = new JSeparator();
		{
			buttonPane = new JPanel();
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
		
		chkbxforenotimportatwork = new JCheckBox("\u5047\u65E5(\u4EA4\u6613\u65E59:00-16:35\u671F\u95F4\u4E0D\u53EF\u4EE5\u5BFC\u5165\u548C\u6210\u4EA4\u91CF\u76F8\u5173\u4FE1\u606F\uFF01)");
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(contentPanel, GroupLayout.PREFERRED_SIZE, 716, GroupLayout.PREFERRED_SIZE)
				.addComponent(buttonPane, GroupLayout.PREFERRED_SIZE, 716, GroupLayout.PREFERRED_SIZE)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(chkbxforenotimportatwork))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(contentPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(chkbxforenotimportatwork)
					.addGap(59)
					.addComponent(buttonPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		
		JSeparator separator_2 = new JSeparator();
		
		JSeparator separator_3 = new JSeparator();
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane, Alignment.LEADING)
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(7)
							.addComponent(cbximporttdxgeguinfo)
							.addGap(14)
							.addComponent(progressBar_1, GroupLayout.PREFERRED_SIZE, 503, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(7)
							.addComponent(chbximportcym)
							.addGap(40)
							.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 321, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(9)
							.addComponent(chbxdaorutdxsysbk)
							.addGap(18)
							.addComponent(pbarbankuai, GroupLayout.PREFERRED_SIZE, 389, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(8)
							.addComponent(chbxdaorutdxzdybk)
							.addGap(13)
							.addComponent(cbximportzdyfromout)
							.addGap(23)
							.addComponent(tfldoutzdyfile, GroupLayout.PREFERRED_SIZE, 197, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(7)
							.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 692, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(8)
							.addComponent(chbxdaorutdxsysbkvol)
							.addGap(26)
							.addComponent(progressBar_2, GroupLayout.PREFERRED_SIZE, 454, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(7)
							.addComponent(cbxImportSzGeGuVol)
							.addGap(14)
							.addComponent(progressBar_4, GroupLayout.PREFERRED_SIZE, 503, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(7)
							.addComponent(chckbximporttushareextradata))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(ckbxnetease))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(chckbximportdzhbk, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(separator_3, GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE)
							.addGap(216)
							.addComponent(separator_2, GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(7)
							.addComponent(separator, GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(37)
							.addComponent(ckbxgddatadwnloded)
							.addGap(4)
							.addComponent(lblNewLabel_1)
							.addGap(14)
							.addComponent(tfldfloatholder, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
							.addGap(8)
							.addComponent(lblNewLabel_2)
							.addGap(8)
							.addComponent(tfldtopholder, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(7)
							.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(7)
							.addComponent(chkbximportgudong))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(12)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(ckbxbuquanshuju)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblNewLabel)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(txfldbqsjnodecode, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblNewLabel_6)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblbqsjshijianduan, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(chckbqtushareextra)
									.addGap(30)
									.addComponent(lblNewLabel_3)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(dctushareextastart, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
									.addGap(21)
									.addComponent(dctushareextaend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))))
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
					.addGap(4)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(chbxdaorutdxsysbk)
						.addComponent(pbarbankuai, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
					.addGap(4)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(chbxdaorutdxzdybk)
						.addComponent(cbximportzdyfromout)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(1)
							.addComponent(tfldoutzdyfile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(4)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
					.addGap(4)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(9)
							.addComponent(chbxdaorutdxsysbkvol))
						.addComponent(progressBar_2, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
					.addGap(4)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(cbxImportSzGeGuVol)
						.addComponent(progressBar_4, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
					.addGap(4)
					.addComponent(chckbximporttushareextradata)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(ckbxnetease)
					.addGap(1)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(separator_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(separator_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(chckbximportdzhbk, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
					.addGap(4)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(4)
					.addComponent(chkbximportgudong)
					.addGap(4)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(ckbxgddatadwnloded)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(4)
							.addComponent(lblNewLabel_1))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(1)
							.addComponent(tfldfloatholder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(4)
							.addComponent(lblNewLabel_2))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(1)
							.addComponent(tfldtopholder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(4)
					.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(33)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(txfldbqsjnodecode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_6)
						.addComponent(lblbqsjshijianduan)
						.addComponent(ckbxbuquanshuju))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblNewLabel_3)
							.addComponent(chckbqtushareextra))
						.addComponent(dctushareextastart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(dctushareextaend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		contentPanel.setLayout(gl_contentPanel);
		getContentPane().setLayout(groupLayout);
		this.setLocationRelativeTo(null);
		
		
		
	}
}
