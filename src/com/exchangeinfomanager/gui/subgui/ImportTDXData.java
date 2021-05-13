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
	Map<String, String> zdybkmap; //��tdx�õ����Զ���������
	Map<String, String> zdybkmapfromfile; //���û�ѡ����ļ�ֱ�ӵõ����Զ���������
	private SetupSystemConfiguration sysconfig;
	private BanKuaiDbOperation bkdbopt;
	
	private void formateGui() 
	{
		LocalTime tdytime = LocalTime.now(); //������ڽ���ʱ�䵼�����ݵĻ�����������ݻ�û�У�����Ҫ�ж�һ��
		Calendar cal = Calendar.getInstance();//���Զ�ÿ��ʱ���򵥶��޸�
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
			
			lblstatus.setText("������9:00-16:35�ڼ䲻���Ե���ͳɽ��������Ϣ��");
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
			
			Collator collator = Collator.getInstance(Locale.CHINESE); //����������
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
			JLabel zdyfilenull = new JLabel ("û���Զ�����");
			pnlZdy.add(zdyfilenull);
		}
		
	}

	private void partThatCanImportDuringWork ()
	{
		//ͬ���Զ�����
		if(chbxdaorutdxzdybk.isSelected() && chbxdaorutdxzdybk.isEnabled() && this.zdybkckbxs != null) { 
			System.out.println("------�����Զ����鿪ʼ��" + LocalTime.now() );
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
			
			System.out.println("......�����Զ��������"  + LocalTime.now()+ ".......\r\n");
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
		
		//��ͨ�����е����Ʊ������������������Ϣ
		if(chbximportcym.isSelected() && chbximportcym.isEnabled())	{
			System.out.println("------�����Ʊ������������������Ϣ��ʼ" + LocalTime.now() );
			long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
			File resulttmpfilesys = bkdbopt.refreshEverUsedStorkName (); //��Ʊ����������Ϣ
			Integer resulttmpfilesys2 = bkdbopt.refreshCurrentUsedStorkNameOfShangHai(); //�Ϻ���Ʊ����������Ϣ
			Integer resulttmpfilesys3 = bkdbopt.refreshCurrentUsedStorkNameOfShenZhen(); //���ڹ�Ʊ����������Ϣ
			long end=System.currentTimeMillis(); //��ȡ����ʱ��
			System.out.println("......�����Ʊ������������������Ϣ����"  + LocalTime.now() + "����ķ�ʱ�䣺 "+(end-start)+"ms.......\r\n");
			chbximportcym.setEnabled(false);
		}
		
		if(chkbximportgudong.isSelected()) { //����TUSHARE�Ĺɶ�����
			System.out.println("------����ɶ���Ϣ��ʼ" + LocalTime.now() );
			long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
			JiGouGuDongDbOperation gudongdbopt = new JiGouGuDongDbOperation ();
			if(ckbxgddatadwnloded.isSelected() )
				gudongdbopt.refreshGuDongData (true, true, tfldfloatholder.getText(), tfldtopholder.getText());
			else
				gudongdbopt.refreshGuDongData (true, true);
			gudongdbopt = null;
			long end=System.currentTimeMillis(); //��ȡ����ʱ��
			System.out.println("......����ɶ���Ϣ����" + LocalTime.now()  + ".....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
		}
		
	}
	/*
	 * 
	 */
	private void partThatHasBeImportAfterWsork () 
	{
		
		//��ͨ����foxpro�е����Ʊ�Ļ�������Ϣ
		if(cbximporttdxgeguinfo.isSelected() && cbximporttdxgeguinfo.isEnabled()) {
					try {
						System.out.println("------�����Ʊ�Ļ�������Ϣ��ʼ" + LocalTime.now());
						long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
						File resultimporttdxgegutinfo = this.bkdbopt.refreshStockJiBenMianInfoFromTdxFoxProFile ();
						long end=System.currentTimeMillis(); //��ȡ����ʱ��
						System.out.println("......�����Ʊ�Ļ�������Ϣ����" + LocalTime.now() + "....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
						
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
				
		 //����ͨ���Ŷ���İ����Ϣ �����������ҵ�����ָ�� ���
		if(chbxdaorutdxsysbk.isSelected() && chbxdaorutdxsysbk.isEnabled()) {
			System.out.println("------����ͨ���Ű����Ϣ��ʼ" + LocalTime.now());
			long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
			File resulttmpfilesys = bkdbopt.refreshTDXSystemBanKuai ();
			long end=System.currentTimeMillis(); //��ȡ����ʱ��
			System.out.println("......����ͨ���Ű����Ϣ����" + LocalTime.now() + ".....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
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
		
		//ͬ��ͨ���Ű��ɽ����ɽ���
		if(chbxdaorutdxsysbkvol.isSelected() &&  chbxdaorutdxsysbkvol.isEnabled() ) {
			try {
				System.out.println("------�����Ϻ�ָ��/��鵱�ճɽ���Ϣ��ʼ" + LocalTime.now());
				
//				File resulttmpfilebkamppreck = bkdbopt.preCheckTDXBanKuaiVolAmoToDb ("sh");
////				System.out.println("�����Ϻ���鵱�ճɽ���Ϣ.....����");
//				List<String> lines = Files.readLines(resulttmpfilebkamppreck, sysconfig.charSet());
//				for (String line : lines) {
//		        	tfldresult.append(line+"\n");
//		        }
			} catch (java.lang.NullPointerException e) {}
			
			try {
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				File resulttmpfilebkamo = bkdbopt.refreshTDXBanKuaiVolAmoToDbBulkImport("sh", 15);
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println(".......�����Ϻ�ָ��/��鵱�ճɽ���Ϣ����" + LocalTime.now() + ".....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
//				List<String> lines = Files.readLines(resulttmpfilebkamo, sysconfig.charSet());
//				for (String line : lines) {
//		        	tfldresult.append(line+"\n");
//		        }
			} catch (java.lang.NullPointerException e) {}
			
			try {
				System.out.println("------��������ָ����鵱�ճɽ���Ϣ��ʼ" + LocalTime.now() );
//				File resulttmpfilezhishupreck = bkdbopt.preCheckTDXBanKuaiVolAmoToDb ("sz");
////				System.out.println("�����Ϻ�ָ����鵱�ճɽ���Ϣ.....����");
//				List<String> lines = Files.readLines(resulttmpfilezhishupreck, sysconfig.charSet());
//				for (String line : lines) {
//		        	tfldresult.append(line+"\n");
//		        }
//				resulttmpfilezhishupreck = null;
			} catch (java.lang.NullPointerException e) {e.printStackTrace();}
			
			try {
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				File resulttmpfilezsamo = bkdbopt.refreshTDXBanKuaiVolAmoToDbBulkImport ("sz", 15);
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println("......��������ָ����鵱�ճɽ���Ϣ����" + LocalTime.now() + ".....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
//				List<String> lines = Files.readLines(resulttmpfilezsamo, sysconfig.charSet());
//				for (String line : lines) {
//		        	tfldresult.append(line+"\n");
//		        }
				resulttmpfilezsamo = null;
			} catch (java.lang.NullPointerException e) {			}
			
			
			chbxdaorutdxsysbkvol.setEnabled(false);
		}
		
		//ͬ�����ɳɽ���
		if(cbxImportSzGeGuVol.isSelected() && cbxImportSzGeGuVol.isEnabled() ) {
			try {
				System.out.println("------������֤��Ʊ���ճɽ���Ϣ��ʼ��" + LocalTime.now() );
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				File resulttmpfilebkamo = bkdbopt.refreshTDXGeGuVolAmoToDbBulkImport("sh", 15);
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println("......�����Ϻ���Ʊ���ճɽ���Ϣ������" + LocalTime.now() + "��.....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
//				List<String> lines = Files.readLines(resulttmpfilebkamo, sysconfig.charSet());
//				for (String line : lines) {
//		        	tfldresult.append(line+"\n");
//		        }
//				lines = null;
			} catch (java.lang.NullPointerException e) {}
			
			ExecutePythonScripts.executePythonScriptForExtraData ("EXTRADATAFROMTUSHARE", null); //���ú�̨python script���ص��յ�extra data
			
			try {
				System.out.println("------������֤��Ʊ���ճɽ���Ϣ��ʼ��" +  LocalTime.now());
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				File resulttmpfilebkamo = bkdbopt.refreshTDXGeGuVolAmoToDbBulkImport("sz", 15);
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println("......�������ڹ�Ʊ���ճɽ���Ϣ������" + LocalTime.now() + "......����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
//				List<String> lines = Files.readLines(resulttmpfilebkamo, sysconfig.charSet());
//				for (String line : lines) {
//		        	tfldresult.append(line+"\n");
//		        }
//				lines = null;
			} catch (java.lang.NullPointerException e) {}
			
			cbxImportSzGeGuVol.setEnabled(false);
		}                                     
		
		//�û�ͬ������ɺͰ��ɽ�����Ҫupdateһ�°������ͣ��Ա����
		if(chbxdaorutdxsysbkvol.isSelected()  || chbxdaorutdxsysbk.isSelected()  ) {
			System.out.println("------���°�����Ϳ�ʼ��" + LocalTime.now() );
			long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
			bkdbopt.refreshTDXSystemBanKuaiLeiXing ();
			long end=System.currentTimeMillis(); //��ȡ����ʱ��
			System.out.println("......���°�����ͽ�����" + LocalTime.now() + ".....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
		}
		
		if(chckbximporttushareextradata.isSelected()  )  {
			System.out.println("------����TUSHARE EXTRA�������ݿ�ʼ��" + LocalTime.now() );
			
			int trycount =0;
			while(trycount <=3) {
				String savedfilename = sysconfig.getTuShareExtraDataDownloadedFilePath () + "/" + LocalDate.now().toString().replaceAll("-", "") + "dailyexchangedata.csv";
				File savedfile = new File (savedfilename);
				if(!savedfile.exists()) {
					ExecutePythonScripts.executePythonScriptForExtraData ("EXTRADATAFROMTUSHARE", null); //���ú�̨python script���ص��յ�extra data
					try {Thread.sleep(1000 * 90);} catch (InterruptedException e) {e.printStackTrace();}
					trycount ++;
				} else
					break;
			}
			
			long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
			try {Thread.sleep(1000 * 10);} catch (InterruptedException e) {e.printStackTrace();}
			bkdbopt.refreshExtraStockDataFromTushare (); //���Ƶ���python EXTRA �����Ѿ�������ɣ��������extra����
			long end=System.currentTimeMillis(); //��ȡ����ʱ��
			System.out.println("......����TUSHARE EXTRA�������ݽ�����" + LocalTime.now() + ".....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
		}
		
		//�������׵Ĺ�Ʊ������,��Ҫ�ǻ�����/��ֵ�����ݣ�
		if(ckbxnetease.isSelected() && ckbxnetease.isEnabled()) { 
			try {
				System.out.println("------�������׹�Ʊ���ݿ�ʼ" + LocalTime.now() );
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				bkdbopt.importNetEaseStockData ("SH");
				bkdbopt.importNetEaseStockData ("SZ");
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println("......�������׹�Ʊ���ݽ���" + LocalTime.now()  + ".....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
			} catch (java.lang.NullPointerException e) {e.printStackTrace();	}
			ckbxnetease.setEnabled(false);
		}
		
		if(ckbxbuquanshuju.isSelected()  ) {
			System.out.println("------��ȫ��Ʊ���ݿ�ʼ" + LocalTime.now() );
			long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
			checkNodeTimeLineInDbWithInDataFile ( txfldbqsjnodecode.getText()  );
			long end=System.currentTimeMillis(); //��ȡ����ʱ��
			System.out.println("......��ȫ��Ʊ���ݽ���" + LocalTime.now()  + ".....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
		}
		
		if(chckbqtushareextra.isSelected() ) {
			LocalDate startdate = dctushareextastart.getLocalDate();
			LocalDate enddate = dctushareextaend.getLocalDate();
			if(startdate != null) {
				if(enddate == null)
					enddate = LocalDate.now();
				System.out.println("------��ȫTUSHARE��ƱExtra���ݿ�ʼ" + LocalTime.now() );
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				checkNodeTuShareExtraDataInDbWithInDataFile ( startdate, enddate );
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println("......��ȫTUSHARE��ƱExtra���ݽ���" + LocalTime.now()  + ".....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
			}
		}

		if(chckbximportdzhbk.isSelected() ) {
			System.out.println("------������ǻ۰����Ϣ��ʼ" + LocalTime.now() );
			long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
			BanKuaiDZHDbOperation dzhdbopt = new BanKuaiDZHDbOperation ();
			dzhdbopt.refreshDZHGaiNianBanKuaiGeGu ();
			dzhdbopt = null;
			long end=System.currentTimeMillis(); //��ȡ����ʱ��
			System.out.println("......������ǻ۰����Ϣ����" + LocalTime.now()  + ".....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
		}
		
//		if(chkbximportgudong.isSelected()) { //����TUSHARE�Ĺɶ�����
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
		
			bkdbopt.refreshExtraStockDataFromTushare (neededstart,neededend ); //��ʼҪ����һ�죬��֤�������ݱ����룬�������Ҫ
		}
	}
	/*
	 * 
	 */
	protected void chooseDirectZdyBanKuaiFile() 
	{
		//��ѡ���ļ�
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
		
		if(!filename.endsWith("EBK") ) { //���ǰ���ļ�
			lblstatus.setText("�ļ����ǰ���ļ���������ѡ���ļ���");
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
		Set<LocalDate> nodetimesetindb = bkdbopt.getNodeDataBaseStoredDataTimeSet ((TDXNodes) node,start, end); //�����ж��ٸ�������
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
			lblstatus.setText("����Ϊ" + searchcode + "�����ڣ�");
			return;
		}
		
		List<LocalDate> differencetimelist = getNodeTimeDifferenceInDBWithDataFile(node);

		if(differencetimelist.size() > 0)
			lblbqsjshijianduan.setText("����ȫʱ��δ�" + differencetimelist.get(0).toString() + "-" + differencetimelist.get(differencetimelist.size() -1 ) + "Լ" + String.valueOf(differencetimelist.size() ) + "�����ݣ�");
		else {
			lblbqsjshijianduan.setText("�������������貹ȫ��");
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
		
			bkdbopt.setVolAmoRecordsFromTDXExportFileToDatabase (node,neededstart.minus(1,ChronoUnit.DAYS),neededend.plus(1, ChronoUnit.DAYS),null); //��ʼҪ����һ�죬��֤�������ݱ����룬�������Ҫ
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
						lblstatus.setText("����Ϊ" + searchcode + "�����ڣ�");
						return;
					}
					
					List<LocalDate> differencetimelist = getNodeTimeDifferenceInDBWithDataFile(node);
					if(differencetimelist.size() > 0)
						lblbqsjshijianduan.setText("����ȫʱ��δ�" + differencetimelist.get(0).toString() + "-" + differencetimelist.get(differencetimelist.size() -1 ) + "Լ" + String.valueOf(differencetimelist.size() ) + "�����ݣ�");
					else
						lblbqsjshijianduan.setText("�������������貹ȫ��");
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
					LocalTime tdytime = LocalTime.now(); //������ڽ���ʱ�䵼�����ݵĻ�����������ݻ�û�У�����Ҫ�ж�һ��
					Calendar cal = Calendar.getInstance();//���Զ�ÿ��ʱ���򵥶��޸�
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
						
						lblstatus.setText("������9:00-16:35�ڼ䲻���Ե���ͳɽ��������Ϣ��");
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
					//9��ǰ�������ݲ�ѡ������������
					if( (wkday<=5 && wkday>=1) &&  tdytime.compareTo(LocalTime.of(21, 30, 0)) <0  ) 
						ckbxnetease.setSelected(false);
					else
						ckbxnetease.setSelected(true);
					
					if(wkday == 1 || wkday == 3 ) {
						chbxdaorutdxsysbk.setSelected(true);
						chckbximportdzhbk.setSelected(true);
					}
						
					
					//��������������һ��ֻҪ����һ�Σ����弴��
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

				lblstatus.setText("ͬ������");
				SystemAudioPlayed.playSound();
				
				JOptionPane.showMessageDialog(null, "���ݵ�����ɣ���������ϵͳ��");
			}
		});
		
	}

	protected void checkDataSyncResult() 
	{
//		File synccheckresult = bkdbopt.checkTDXDataImportIsCompleted ();
//		if(synccheckresult != null) {
//			int exchangeresult = JOptionPane.showConfirmDialog(null, "ͬ��ͨ�������������Լ����ɣ�����" + synccheckresult.getAbsolutePath() + "�²鿴���Ƿ�򿪸�Ŀ¼��","������", JOptionPane.OK_CANCEL_OPTION);
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
////			JOptionPane.showMessageDialog(null, "ͨ��������������","Warning", JOptionPane.WARNING_MESSAGE);
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
