package com.exchangeinfomanager.gui.subgui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.WrapLayout;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.Collator;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.ScrollPaneConstants;
import java.awt.GridBagLayout;
import java.awt.Color;

import org.apache.commons.io.FileUtils;
import org.jfree.data.time.ohlc.OHLCItem;

import javax.swing.DefaultComboBoxModel;

public class ImportTDXData extends JDialog {
	

	/**
	 * Create the dialog.
	 * @param bkdbopt 
	 */
	public ImportTDXData() 
	{
		this.bkdbopt = new BanKuaiDbOperation ();
		this.sysconfig = SystemConfigration.getInstance(); 
		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
		initializeGui ();

		iniiazlizeZdyGui ();
		formateGui ();
		createEvents ();
	}
	
	

	JCheckBox[] zdybkckbxs;
	Map<String, String> zdybkmap; //��tdx�õ����Զ���������
	Map<String, String> zdybkmapfromfile; //���û�ѡ����ļ�ֱ�ӵõ����Զ���������
	private SystemConfigration sysconfig;
	private AllCurrentTdxBKAndStoksTree allbksks;
	private BanKuaiAndChanYeLian2 bkcyl;
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
			cbxImportShGeGuVol.setSelected(false);
			cbxImportSzGeGuVol.setSelected(false);
			ckbxnetease.setSelected(false);
			
			chbxdaorutdxsysbk.setEnabled(false);
			cbximporttdxgeguinfo.setEnabled(false);
			chbxdaorutdxsysbkvol.setEnabled(false);
			cbxImportShGeGuVol.setEnabled(false);
			cbxImportSzGeGuVol.setEnabled(false);
			ckbxnetease.setEnabled(false);
			
			lblstatus.setText("������9:00-16:35�ڼ䲻���Ե���ͳɽ��������Ϣ��");
		}
		
	}
	
	private void iniiazlizeZdyGui() 
	{
		zdybkmap = this.bkdbopt.getTDXZiDingYiBanKuaiList ();
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
				i++;
			}
		} catch (java.lang.NullPointerException e) {
			JLabel zdyfilenull = new JLabel ("û���Զ�����");
			pnlZdy.add(zdyfilenull);
		}
		
	}

	private void partThatCanImportDuringWork ()
	{

//		if(ckbxquekoutongji.isSelected() && ckbxquekoutongji.isEnabled()) { 
//			
//			System.out.println("ȱ��ͳ�ƿ�ʼ.....");
//			long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
//			queKouTongJi ();
//			long end=System.currentTimeMillis(); //��ȡ����ʱ��
//			System.out.println("�����Ʊ�Ļ�������Ϣ������" + "����ķ�ʱ�䣺 "+(end-start)+"ms.......");
//			ckbxquekoutongji.setEnabled(false);
//		}
		
		//��ͨ�����е����Ʊ������������������Ϣ
		if(chbximportcym.isSelected() && chbximportcym.isEnabled())	{
			System.out.println("�����Ʊ������������������Ϣ��ʼ......");
			long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
			File resulttmpfilesys = bkdbopt.refreshEverUsedStorkName (); //��Ʊ����������Ϣ
			Integer resulttmpfilesys2 = bkdbopt.refreshCurrentUsedStorkNameOfShangHai(); //�Ϻ���Ʊ����������Ϣ
			Integer resulttmpfilesys3 = bkdbopt.refreshCurrentUsedStorkNameOfShenZhen(); //���ڹ�Ʊ����������Ϣ
			long end=System.currentTimeMillis(); //��ȡ����ʱ��
			System.out.println("�����Ʊ������������������Ϣ������" + "����ķ�ʱ�䣺 "+(end-start)+"ms.......");
			chbximportcym.setEnabled(false);
			
			
//			try {
//				List<String> lines = Files.readLines(resulttmpfilesys, sysconfig.charSet());
//				for (String line : lines) {
//		        	tfldresult.append(line+"\n");
//		        }
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (java.lang.NullPointerException e) {
//			}
//			try {
//				List<String> lines = Files.readLines(resulttmpfilesys2, sysconfig.charSet());
//				for (String line : lines) {
//		        	tfldresult.append(line+"\n");
//		        }
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (java.lang.NullPointerException e) {
//			}
		}
		
		//ͬ���Զ�����
		if(chbxdaorutdxzdybk.isSelected() && chbxdaorutdxzdybk.isEnabled()) { 
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
		
	}
	/*
	 * 
	 */
	private void partThatHasBeImportAfterWsork () 
	{
		
		//��ͨ����foxpro�е����Ʊ�Ļ�������Ϣ
		if(cbximporttdxgeguinfo.isSelected() && cbximporttdxgeguinfo.isEnabled()) {
					try {
						System.out.println("�����Ʊ�Ļ�������Ϣ.....��ʼ");
						long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
						File resultimporttdxgegutinfo = this.bkdbopt.refreshStockJiBenMianInfoFromTdxFoxProFile ();
						long end=System.currentTimeMillis(); //��ȡ����ʱ��
						System.out.println(".....����ķ�ʱ�䣺 "+(end-start)+"ms");
						System.out.println("�����Ʊ�Ļ�������Ϣ.....����");
						
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
			System.out.println("����ͨ���Ű����Ϣ.....��ʼ");
			long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
			File resulttmpfilesys = bkdbopt.refreshTDXSystemBanKuai ();
			long end=System.currentTimeMillis(); //��ȡ����ʱ��
			System.out.println("����ͨ���Ű����Ϣ.....����" + ".....����ķ�ʱ�䣺 "+(end-start)+"ms");
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
				System.out.println("�����Ϻ�ָ��/��鵱�ճɽ���Ϣ.....��ʼ");
				
				File resulttmpfilebkamppreck = bkdbopt.preCheckTDXBanKuaiVolAmoToDb ("sh");
//				System.out.println("�����Ϻ���鵱�ճɽ���Ϣ.....����");
				List<String> lines = Files.readLines(resulttmpfilebkamppreck, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			try {
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				File resulttmpfilebkamo = bkdbopt.refreshTDXBanKuaiVolAmoToDb("sh");
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println("�����Ϻ�ָ��/��鵱�ճɽ���Ϣ.....����." + ".....����ķ�ʱ�䣺 "+(end-start)+"ms");
				List<String> lines = Files.readLines(resulttmpfilebkamo, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			try {
				System.out.println("��������ָ����鵱�ճɽ���Ϣ.....��ʼ");
				File resulttmpfilezhishupreck = bkdbopt.preCheckTDXBanKuaiVolAmoToDb ("sz");
//				System.out.println("�����Ϻ�ָ����鵱�ճɽ���Ϣ.....����");
				List<String> lines = Files.readLines(resulttmpfilezhishupreck, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			try {
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				File resulttmpfilezsamo = bkdbopt.refreshTDXBanKuaiVolAmoToDb ("sz");
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println("��������ָ����鵱�ճɽ���Ϣ.....����." + ".....����ķ�ʱ�䣺 "+(end-start)+"ms");
				List<String> lines = Files.readLines(resulttmpfilezsamo, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			
			chbxdaorutdxsysbkvol.setEnabled(false);
		}
		
		//ͬ�����ɳɽ���
		if(cbxImportSzGeGuVol.isSelected() && cbxImportSzGeGuVol.isEnabled() ) {
			try {
				System.out.println("�������ڹ�Ʊ���ճɽ���Ϣ.....��ʼ");
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				File resulttmpfilebkamo = bkdbopt.refreshTDXGeGuVolAmoToDb("sz");
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println("�������ڹ�Ʊ���ճɽ���Ϣ.....����......����ķ�ʱ�䣺 "+(end-start)+"ms");
				List<String> lines = Files.readLines(resulttmpfilebkamo, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			cbxImportSzGeGuVol.setEnabled(false);
			
		}                                     
		if(cbxImportShGeGuVol.isSelected() && cbxImportShGeGuVol.isEnabled() ) {
			try {
				System.out.println("�����Ϻ���Ʊ���ճɽ���Ϣ.....��ʼ");
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				File resulttmpfilebkamo = bkdbopt.refreshTDXGeGuVolAmoToDb("sh");
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println("�����Ϻ���Ʊ���ճɽ���Ϣ.....����." + ".....����ķ�ʱ�䣺 "+(end-start)+"ms");
				List<String> lines = Files.readLines(resulttmpfilebkamo, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			cbxImportShGeGuVol.setEnabled(false);
		}
		
		//�û�ͬ������ɺͰ��ɽ�����Ҫupdateһ�°������ͣ��Ա����
		if(chbxdaorutdxsysbkvol.isSelected()  || chbxdaorutdxsysbk.isSelected()  ) {
			System.out.println("���°������.....��ʼ");
			long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
			bkdbopt.refreshTDXSystemBanKuaiLeiXing ();
			long end=System.currentTimeMillis(); //��ȡ����ʱ��
			System.out.println("���°������.....����" + ".....����ķ�ʱ�䣺 "+(end-start)+"ms");
		}
		
		
		//�������׵Ĺ�Ʊ������,��Ҫ�ǻ�����/��ֵ�����ݣ�
		if(ckbxnetease.isSelected() && ckbxnetease.isEnabled()) { 
			try {
				System.out.println("�������׹�Ʊ����.....��ʼ");
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				File resulttmpfilenetease = bkdbopt.importNetEaseStockData ();
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println("�������׹�Ʊ����.....����." + ".....����ķ�ʱ�䣺 "+(end-start)+"ms");
				List<String> lines = Files.readLines(resulttmpfilenetease, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			ckbxnetease.setEnabled(false);
			
//			//�������ϵ������ݺ��п���ĳЩ���ݻ��ǲ��ܴ����ϵõ���û�취�ˣ�ֻ���ֹ��Ӷ����Ƹ����浼��
//			HashSet<String> missingdatastockset = bkdbopt.checkStockDataIsCompleted();
//			if(!missingdatastockset.isEmpty()) {
//				int exchangeresult = JOptionPane.showConfirmDialog(null, "�����׵�������ݲ���������Ӷ����Ƹ������������ݺ���ļ�ֱ�ӵ��롣","���벻����", JOptionPane.OK_CANCEL_OPTION);
//	      		if(exchangeresult == JOptionPane.CANCEL_OPTION) 
//	      			return;
//	      		else {
//	      			importEastMoneyExportFile ();
//	      		}
//			}
//			missingdatastockset = null;
		}
		

		
//		if(ckbxtushare.isSelected()) { //����TUSHARE������
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

	private void createEvents() 
	{
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
						cbxImportShGeGuVol.setSelected(false);
						cbxImportSzGeGuVol.setSelected(false);
						ckbxnetease.setSelected(false);
						
						chbxdaorutdxsysbk.setEnabled(false);
						cbximporttdxgeguinfo.setEnabled(false);
						chbxdaorutdxsysbkvol.setEnabled(false);
						cbxImportShGeGuVol.setEnabled(false);
						cbxImportSzGeGuVol.setEnabled(false);
						ckbxnetease.setEnabled(false);
						
						lblstatus.setText("������9:00-16:35�ڼ䲻���Ե���ͳɽ��������Ϣ��");
					}
				} else {
					String msg =  "����ʱ�ε��뽻�����ݿ��ܵ�������ȱʧ��\n�Ƿ������";
					int exchangeresult = JOptionPane.showConfirmDialog(null,msg , "ȷʵ���룿", JOptionPane.OK_CANCEL_OPTION);
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
					
					lblstatus.setText("������9:00-16:35�ڼ䵼��ɽ��������Ϣ���ܵ�������ȱʧ��");
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
					chbxdaorutdxsysbk.setSelected(true);
					chbxdaorutdxsysbkvol.setSelected(true);
					cbxImportSzGeGuVol.setSelected(true);
					cbxImportShGeGuVol.setSelected(true);
					ckbxnetease.setSelected(true);

					
					//��������������һ��ֻҪ����һ�Σ����弴��
					Calendar cal = Calendar.getInstance();
					int wkday = cal.get(Calendar.DAY_OF_WEEK) - 1;
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
				}
			}
		});
		
		btnStart.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				
//				if(chbxdaorutdxsysbk.isSelected() || cbximporttdxgeguinfo.isSelected() || chbxdaorutdxsysbkvol.isSelected() 
//						||cbxImportShGeGuVol.isSelected() || cbxImportSzGeGuVol.isSelected() 
//						|| ckbxnetease.isSelected()
//						) {
//					LocalTime tdytime = LocalTime.now(); //������ڽ���ʱ�䵼�����ݵĻ�����������ݻ�û�У�����Ҫ�ж�һ��
//					Calendar cal = Calendar.getInstance();//���Զ�ÿ��ʱ���򵥶��޸�
////					int hour = cal.get(Calendar.HOUR_OF_DAY);
//					int wkday = cal.get(Calendar.DAY_OF_WEEK) - 1;
//					if( (wkday<=5 && wkday>=1) && (tdytime.compareTo(LocalTime.of(9, 0, 0)) >0 && tdytime.compareTo(LocalTime.of(16, 35, 0)) <0)  ) {
//						JOptionPane.showMessageDialog(null,"�漰ͨ���ųɽ�������ͬ�������ڽ�����16:35���̺�������9��ǰ��ͨ���ŵ������ݺ��ٵ��뱾ϵͳ��" + "\n" 
//												+ "�ɽ���������ֹ����������������");
//						formateGui ();
//					} else
//						partThatHasBeImportAfterWsork();
//				}
				
				partThatHasBeImportAfterWsork();
				partThatCanImportDuringWork ();
				
				lblstatus.setText("ͬ������");
				
				int exchangeresult = JOptionPane.showConfirmDialog(null, "���ݵ�����ɣ��Ƿ������ݵ��������ԣ�","�������", JOptionPane.OK_CANCEL_OPTION);
	      		if(exchangeresult == JOptionPane.CANCEL_OPTION) 
	      			return;
	      		else {
	      			checkDataSyncResult ();
	      		}
	      			
	      		
			}
		});
		
	}

	protected void checkDataSyncResult() 
	{
		File synccheckresult = bkdbopt.checkTDXDataImportIsCompleted ();
		if(synccheckresult != null) {
			int exchangeresult = JOptionPane.showConfirmDialog(null, "ͬ��ͨ�������������Լ����ɣ�����" + synccheckresult.getAbsolutePath() + "�²鿴���Ƿ�򿪸�Ŀ¼��","������", JOptionPane.OK_CANCEL_OPTION);
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
//			JOptionPane.showMessageDialog(null, "ͨ��������������","Warning", JOptionPane.WARNING_MESSAGE);
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
		
		chbxdaorutdxsysbk = new JCheckBox("*\u5BFC\u5165\u901A\u8FBE\u4FE1\u7CFB\u7EDF\u677F\u5757\u548C\u6307\u6570\u4FE1\u606F");
		
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
		
		lblstatus = new JLabel("New label");
		
		cbximportzdyfromout = new JCheckBox("\u76F4\u63A5\u4ECE\u6587\u4EF6\u5BFC\u5165\u81EA\u5B9A\u4E49\u677F\u5757");
		
		
		tfldoutzdyfile = new JTextField();
		tfldoutzdyfile.setEnabled(false);
		tfldoutzdyfile.setEditable(false);
		tfldoutzdyfile.setColumns(10);
		
		chkbxforenotimportatwork = new JCheckBox("\u4EA4\u6613\u65E59:00-16:35\u671F\u95F4\u4E0D\u53EF\u4EE5\u5BFC\u5165\u548C\u6210\u4EA4\u91CF\u76F8\u5173\u4FE1\u606F\uFF01");
		chkbxforenotimportatwork.setSelected(true);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(chkbxforenotimportatwork)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(cbximporttdxgeguinfo)
									.addGap(18)
									.addComponent(progressBar_1, GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(chbximportcym)
									.addGap(18)
									.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(chbxdaorutdxsysbk)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(pbarbankuai, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE))
								.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
									.addGroup(gl_contentPanel.createSequentialGroup()
										.addComponent(chbxdaorutdxsysbkvol)
										.addGap(12)
										.addComponent(progressBar_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
									.addGroup(gl_contentPanel.createSequentialGroup()
										.addComponent(cbxImportSzGeGuVol)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(progressBar_4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
									.addGroup(gl_contentPanel.createSequentialGroup()
										.addComponent(cbxImportShGeGuVol)
										.addPreferredGap(ComponentPlacement.UNRELATED)
										.addComponent(progressBar_3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
									.addGroup(gl_contentPanel.createSequentialGroup()
										.addComponent(ckbxnetease)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(cbximportoptions, 0, 0, Short.MAX_VALUE))
									.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(chbxdaorutdxzdybk)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(cbximportzdyfromout)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tfldoutzdyfile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE))
							.addGap(130))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(lblstatus, GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
							.addGap(453))))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(cbximporttdxgeguinfo)
						.addComponent(progressBar_1, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(chbximportcym)
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
					.addGap(9)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(chbxdaorutdxsysbk)
						.addComponent(pbarbankuai, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
					.addGap(11)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(chbxdaorutdxzdybk)
						.addComponent(cbximportzdyfromout)
						.addComponent(tfldoutzdyfile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(chbxdaorutdxsysbkvol)
						.addComponent(progressBar_2, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
					.addGap(10)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(cbxImportSzGeGuVol)
						.addComponent(progressBar_4, GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(cbxImportShGeGuVol)
						.addComponent(progressBar_3, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(ckbxnetease)
						.addComponent(cbximportoptions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(50)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 214, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chkbxforenotimportatwork)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblstatus)
					.addContainerGap())
		);
		
		pnlZdy = new JPanel();
		
		//scrollPane_1.setPreferredSize(scrollPane_1.getPreferredSize());
		scrollPane_1.setViewportView(pnlZdy);
		//pnlZdy.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); WrapLayout
		pnlZdy.setLayout(new WrapLayout(WrapLayout.CENTER, 5, 5)); 
		tfldresult = new JTextArea();
		tfldresult.setLineWrap(true);
		scrollPane.setViewportView(tfldresult);
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
			
			
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addComponent(chbxselectall)
						.addGap(255)
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
							.addComponent(okButton)))
			);
			buttonPane.setLayout(gl_buttonPane);
		}
		this.setLocationRelativeTo(null);
		
		//����ϵͳmodel������Щcomponents���Ա��û�ʹ��
		if(sysconfig.getSoftWareMode() == sysconfig.MODELCLIENT) {
			
		} else if(sysconfig.getSoftWareMode() == sysconfig.MODELSERVER) {
			
		}
		
	}
}
