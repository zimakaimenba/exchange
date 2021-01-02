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
	Map<String, String> zdybkmap; //��tdx�õ����Զ���������
	Map<String, String> zdybkmapfromfile; //���û�ѡ����ļ�ֱ�ӵõ����Զ���������
	private SetupSystemConfiguration sysconfig;
//	private AllCurrentTdxBKAndStoksTree allbksks;
//	private BanKuaiAndChanYeLian2 bkcyl;
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
				
				File resulttmpfilebkamppreck = bkdbopt.preCheckTDXBanKuaiVolAmoToDb ("sh");
//				System.out.println("�����Ϻ���鵱�ճɽ���Ϣ.....����");
				List<String> lines = Files.readLines(resulttmpfilebkamppreck, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {e.printStackTrace();
			} catch (java.lang.NullPointerException e) {}
			
			try {
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				File resulttmpfilebkamo = bkdbopt.refreshTDXBanKuaiVolAmoToDb("sh");
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println(".......�����Ϻ�ָ��/��鵱�ճɽ���Ϣ����" + LocalTime.now() + ".....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
				List<String> lines = Files.readLines(resulttmpfilebkamo, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {e.printStackTrace();
			} catch (java.lang.NullPointerException e) {}
			
			try {
				System.out.println("------��������ָ����鵱�ճɽ���Ϣ��ʼ" + LocalTime.now() );
				File resulttmpfilezhishupreck = bkdbopt.preCheckTDXBanKuaiVolAmoToDb ("sz");
//				System.out.println("�����Ϻ�ָ����鵱�ճɽ���Ϣ.....����");
				List<String> lines = Files.readLines(resulttmpfilezhishupreck, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
				resulttmpfilezhishupreck = null;
			} catch (IOException e) {e.printStackTrace();
			} catch (java.lang.NullPointerException e) {e.printStackTrace();}
			
			try {
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				File resulttmpfilezsamo = bkdbopt.refreshTDXBanKuaiVolAmoToDb ("sz");
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println("......��������ָ����鵱�ճɽ���Ϣ����" + LocalTime.now() + ".....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
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
		
		//ͬ�����ɳɽ���
		if(cbxImportSzGeGuVol.isSelected() && cbxImportSzGeGuVol.isEnabled() ) {
			try {
				System.out.println("------������֤��Ʊ���ճɽ���Ϣ��ʼ��" +  LocalTime.now());
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				File resulttmpfilebkamo = bkdbopt.refreshTDXGeGuVolAmoToDb("sz");
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println("......�������ڹ�Ʊ���ճɽ���Ϣ������" + LocalTime.now() + "......����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
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
				System.out.println("------������֤��Ʊ���ճɽ���Ϣ��ʼ��" + LocalTime.now() );
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				File resulttmpfilebkamo = bkdbopt.refreshTDXGeGuVolAmoToDb("sh");
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println("......�����Ϻ���Ʊ���ճɽ���Ϣ������" + LocalTime.now() + "��.....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
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
		
		//�û�ͬ������ɺͰ��ɽ�����Ҫupdateһ�°������ͣ��Ա����
		if(chbxdaorutdxsysbkvol.isSelected()  || chbxdaorutdxsysbk.isSelected()  ) {
			System.out.println("------���°�����Ϳ�ʼ��" + LocalTime.now() );
			long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
			bkdbopt.refreshTDXSystemBanKuaiLeiXing ();
			long end=System.currentTimeMillis(); //��ȡ����ʱ��
			System.out.println("......���°�����ͽ�����" + LocalTime.now() + ".....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
		}
		
		
		//�������׵Ĺ�Ʊ������,��Ҫ�ǻ�����/��ֵ�����ݣ�
		if(ckbxnetease.isSelected() && ckbxnetease.isEnabled()) { 
			try {
				System.out.println("------�������׹�Ʊ���ݿ�ʼ" + LocalTime.now() );
				long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
				File resulttmpfilenetease = bkdbopt.importNetEaseStockData ();
				long end=System.currentTimeMillis(); //��ȡ����ʱ��
				System.out.println("......�������׹�Ʊ���ݽ���" + LocalTime.now()  + ".....����ķ�ʱ�䣺 "+(end-start)+"ms \r\n");
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
			
			if(neededstart.isAfter(nodestart)) //����ֻ����ǰ�����ݲ�ȫ�����յ�����ͨ�����뼴�ɣ����貹ȫ
				continue;
			
			bkdbopt.setVolAmoRecordsFromTDXExportFileToDatabase (node,neededstart.minus(1,ChronoUnit.DAYS),neededend,null); //��ʼҪ����һ�죬��֤�������ݱ����룬�������Ҫ
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
					chbxdaorutdxsysbkvol.setSelected(true);
					cbxImportSzGeGuVol.setSelected(true);
					cbxImportShGeGuVol.setSelected(true);
					chbxdaorutdxzdybk.setSelected(true);
					
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
					
					if(wkday == 1 || wkday == 4 ) {
						chbxdaorutdxsysbk.setSelected(true);
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

				lblstatus.setText("ͬ������");
				SystemAudioPlayed.playSound();
				
				JOptionPane.showMessageDialog(null, "���ݵ�����ɣ���������ϵͳ��");
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
