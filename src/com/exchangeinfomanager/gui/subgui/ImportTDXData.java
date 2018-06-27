package com.exchangeinfomanager.gui.subgui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.exchangeinfomanager.commonlib.WrapLayout;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import java.text.Collator;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.ScrollPaneConstants;
import java.awt.GridBagLayout;
import java.awt.Color;

import org.apache.commons.io.FileUtils;
import javax.swing.DefaultComboBoxModel;

public class ImportTDXData extends JDialog {
	

	/**
	 * Create the dialog.
	 * @param bkdbopt 
	 */
	public ImportTDXData() 
	{
		this.bkdbopt = new BanKuaiDbOperation ();
		sysconfig = SystemConfigration.getInstance(); 
		initializeGui ();

		iniiazlizeZdyGui ();
		createEvents ();
	}
	
	JCheckBox[] zdybkckbxs;
	HashMap<String, String> zdybkmap;

	private SystemConfigration sysconfig;
	BanKuaiDbOperation bkdbopt;
	
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
		if(cbximportdzhguquan.isSelected() && cbximportdzhguquan.isEnabled()) { //������ǻ۵ĸ��ɹ�Ȩ��Ϣ
			File resulttmpfilesys = bkdbopt.refreshDZHStockGuQuan ();
			cbximportdzhguquan.setEnabled(false);
		}
		
		//��ͨ�����е����Ʊ������������������Ϣ
		if(chbximportcym.isSelected() && chbximportcym.isEnabled())	{
			File resulttmpfilesys = bkdbopt.refreshEverUsedStorkName (); //��Ʊ����������Ϣ
			Integer resulttmpfilesys2 = bkdbopt.refreshCurrentUsedStorkNameOfShangHai(); //�Ϻ���Ʊ����������Ϣ
			Integer resulttmpfilesys3 = bkdbopt.refreshCurrentUsedStorkNameOfShenZhen(); //���ڹ�Ʊ����������Ϣ
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
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
		}
	}
	private void partThatHasBeImportAfterWsork () 
	{
		
		//��ͨ����foxpro�е����Ʊ�Ļ�������Ϣ,��ʱ���ã���Ϊ��û���õ���Щ��Ϣ
		if(cbximporttdxgeguinfo.isSelected() && cbximporttdxgeguinfo.isEnabled()) {
					try {
						File resultimporttdxgegutinfo = this.bkdbopt.refreshStockJiBenMianInfoFromTdxFoxProFile ();
						List<String> lines = Files.readLines(resultimporttdxgegutinfo, sysconfig.charSet());
						for (String line : lines) {
				        	tfldresult.append(line+"\n");
				        }
					} catch (IOException e) {
						e.printStackTrace();
					} catch (java.lang.NullPointerException e) {
					}
					
					cbximporttdxgeguinfo.setEnabled(false);
		 }
				
		 //����ͨ���Ŷ���İ����Ϣ �����������ҵ�����ָ�� ���
		if(chbxdaorutdxsysbk.isSelected() && chbxdaorutdxsysbk.isEnabled()) {
			File resulttmpfilesys = bkdbopt.refreshTDXSystemBanKuai ();
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
				File resulttmpfilebkamppreck = bkdbopt.preCheckTDXBanKuaiVolAmoToDb ("sh");
				List<String> lines = Files.readLines(resulttmpfilebkamppreck, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			try {
				File resulttmpfilebkamo = bkdbopt.refreshTDXBanKuaiVolAmoToDb("sh");
				List<String> lines = Files.readLines(resulttmpfilebkamo, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			try {
				File resulttmpfilezhishupreck = bkdbopt.preCheckTDXBanKuaiVolAmoToDb ("sz");
				List<String> lines = Files.readLines(resulttmpfilezhishupreck, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			try {
				File resulttmpfilezsamo = bkdbopt.refreshTDXBanKuaiVolAmoToDb ("sz"); 
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
				File resulttmpfilebkamo = bkdbopt.refreshTDXGeGuVolAmoToDb("sz");
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
				File resulttmpfilebkamo = bkdbopt.refreshTDXGeGuVolAmoToDb("sh");
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
		if(chbxdaorutdxsysbkvol.isSelected()  
			&& cbxImportSzGeGuVol.isSelected() 
			&& cbxImportShGeGuVol.isSelected()  ) {
			bkdbopt.refreshTDXSystemBanKuaiLeiXing ();
		}
		//
		if(ckbxnetease.isSelected()) { //�������׵Ĺ�Ʊ������,��Ҫ�ǻ�����/��ֵ�����ݣ�
			bkdbopt.importNetEaseStockData ();
		}
		
		//�������ϵ������ݺ��п���ĳЩ���ݻ��ǲ��ܴ����ϵõ���û�취�ˣ�ֻ���ֹ��Ӷ����Ƹ����浼��
		HashSet<String> missingdatastockset = bkdbopt.checkStockDataIsCompleted();
		if(!missingdatastockset.isEmpty()) {
			int exchangeresult = JOptionPane.showConfirmDialog(null, "�����׵�������ݲ���������Ӷ����Ƹ������������ݺ���ļ�ֱ�ӵ��롣","���벻����", JOptionPane.OK_CANCEL_OPTION);
      		if(exchangeresult == JOptionPane.CANCEL_OPTION) 
      			return;
      		else {
      			importEastMoneyExportFile ();
      		}
		}
		missingdatastockset = null;
		
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

	private void importEastMoneyExportFile()
	{
		String parsedpath = sysconfig.getEastMoneyDownloadedFilePath ();
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setCurrentDirectory(new File(parsedpath) );
		
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
		    
		    String linuxpath;
		    if(chooser.getSelectedFile().isDirectory())
		    	linuxpath = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
		    else
		    	linuxpath = (chooser.getSelectedFile()).toString().replace('\\', '/');
		    
		    File emfile = chooser.getSelectedFile();
		    bkdbopt.importEastMoneyStockData (emfile);

		}
		
		
	}

	private void createEvents() 
	{
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
				if(!chbxdaorutdxsysbk.isSelected() && !cbximporttdxgeguinfo.isSelected() 
						&& !chbxdaorutdxsysbkvol.isSelected() && !cbxImportShGeGuVol.isSelected() 
						&& !cbxImportSzGeGuVol.isSelected() && !chbximportcym.isSelected()
						&& !cbximportdzhguquan.isSelected()
						&& !ckbxnetease.isSelected() 
						) {
					JOptionPane.showMessageDialog(null,"��ѡ����Ҫ�������Ŀ��");
					return;
					
				}
				//
				if(chbxdaorutdxsysbk.isSelected() || cbximporttdxgeguinfo.isSelected() || chbxdaorutdxsysbkvol.isSelected() 
						||cbxImportShGeGuVol.isSelected() || cbxImportSzGeGuVol.isSelected() 
						|| ckbxnetease.isSelected()
						) {
					Calendar cal = Calendar.getInstance();//���Զ�ÿ��ʱ���򵥶��޸�
					int hour = cal.get(Calendar.HOUR_OF_DAY);
					int wkday = cal.get(Calendar.DAY_OF_WEEK) - 1;
					if( (wkday<=5 && wkday>=1) && (hour<15 && hour>= 10) && cbximporttime.isSelected() ) {
						JOptionPane.showMessageDialog(null,"�漰ͨ���Ŵ�������ͬ�������ڽ�����15�����̺�������10��ǰ��ͨ���ŵ������ݺ��ٵ��뱾ϵͳ��");
						return;
					}
					
					partThatHasBeImportAfterWsork();
				}
			 partThatCanImportDuringWork ();
				
				lblstatus.setText("ͬ������");
				
				int exchangeresult = JOptionPane.showConfirmDialog(null, "���ݵ�����ɣ��Ƿ�������һ���ԣ�","�������", JOptionPane.OK_CANCEL_OPTION);
	      		if(exchangeresult == JOptionPane.CANCEL_OPTION) 
	      			return;
	      		else
	      			checkDataSyncResult ();
	      		
			}
		});
		
	}

	protected void checkDataSyncResult() 
	{
		File synccheckresult = bkdbopt.checkImportTDXDataSync ();
		if(synccheckresult != null) {
			int exchangeresult = JOptionPane.showConfirmDialog(null, "ͬ��ͨ��������һ���Լ����ɣ�����" + synccheckresult.getAbsolutePath() + "�²鿴���Ƿ�򿪸�Ŀ¼��","������", JOptionPane.OK_CANCEL_OPTION);
      		  if(exchangeresult == JOptionPane.CANCEL_OPTION)
      				return;
      		  try {
      			String path = synccheckresult.getParent();
//      			Desktop.getDesktop().open(new File( path ));
      			Runtime.getRuntime().exec("explorer.exe /select," + synccheckresult.getAbsolutePath() );
      		  } catch (IOException e1) {
      				e1.printStackTrace();
      		  }
		} else
			JOptionPane.showMessageDialog(null, "ͨ��������������","Warning", JOptionPane.WARNING_MESSAGE);
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
	private JCheckBox cbximportdzhguquan;
	private JCheckBox ckbxnetease;
	private JComboBox cbximportoptions;
	private JCheckBox cbximporttime;
	private JLabel lblstatus;
	
	private void initializeGui() 
	{
		setTitle("\u540C\u6B65\u901A\u8FBE\u4FE1\u6570\u636E");
		setBounds(100, 100, 576, 802);
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
		
		JProgressBar progressBar_5 = new JProgressBar();
		
		cbximportdzhguquan = new JCheckBox("\u5BFC\u5165\u5927\u667A\u6167\u4E2A\u80A1\u80A1\u6743\u4FE1\u606F");
		cbximportdzhguquan.setEnabled(false);
		
		ckbxnetease = new JCheckBox("\u5BFC\u5165\u7F51\u6613\u8D22\u7ECF\u6BCF\u65E5\u4EA4\u6613\u4FE1\u606F\uFF08\u6362\u624B\u7387/\u5E02\u503C\u7B49\uFF09");
		
		cbximportoptions = new JComboBox();
		cbximportoptions.setModel(new DefaultComboBoxModel(new String[] {"\u5982\u679C\u7F51\u6613\u6570\u636E\u83B7\u53D6\u5931\u8D25\uFF0C\u5219\u4ECE\u5176\u4ED6\u6570\u636E\u6E90\u83B7\u53D6\u6570\u636E", "\u5982\u679C\u7F51\u6613\u6570\u636E\u83B7\u53D6\u5931\u8D25\uFF0C\u76F4\u63A5\u5BFC\u5165\u4E1C\u65B9\u8D22\u5BCC\u6BCF\u65E5\u81EA\u4E0B\u8F7D\u6570\u636E"}));
		
		lblstatus = new JLabel("New label");
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(cbximportdzhguquan, Alignment.TRAILING)
						.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
							.addComponent(chbxdaorutdxzdybk)
							.addGap(291)
							.addComponent(progressBar_5, GroupLayout.DEFAULT_SIZE, 10, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(ckbxnetease)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(cbximportoptions, 0, 244, Short.MAX_VALUE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(cbximporttdxgeguinfo)
									.addGap(18)
									.addComponent(progressBar_1, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(chbximportcym)
									.addGap(18)
									.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(chbxdaorutdxsysbk)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(pbarbankuai, GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(cbxImportShGeGuVol)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(progressBar_3, GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(cbxImportSzGeGuVol)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(progressBar_4, GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(chbxdaorutdxsysbkvol)
									.addGap(12)
									.addComponent(progressBar_2, GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE))
								.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
								.addComponent(scrollPane))
							.addGap(75))
						.addComponent(lblstatus, GroupLayout.PREFERRED_SIZE, 489, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
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
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(progressBar_5, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(chbxdaorutdxzdybk))
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
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(cbximportdzhguquan)
					.addGap(22)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 214, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblstatus)
					.addGap(7))
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
			
			cbximporttime = new JCheckBox("\u5F3A\u5236\u5728\u6536\u76D8\u540E\u5BFC\u5165\u6570\u636E");
			cbximporttime.setSelected(true);
			
			
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addComponent(chbxselectall)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(cbximporttime)
						.addGap(108)
						.addComponent(btnStart)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnchecksync)
						.addGap(57)
						.addComponent(okButton)
						.addContainerGap())
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(5)
						.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnStart)
							.addComponent(chbxselectall)
							.addComponent(okButton)
							.addComponent(btnchecksync)
							.addComponent(cbximporttime)))
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
