package com.exchangeinfomanager.gui.subgui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockDbOperations;
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
import java.text.Collator;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.ScrollPaneConstants;
import java.awt.GridBagLayout;

public class ImportTDXData extends JDialog {
	

	/**
	 * Create the dialog.
	 * @param bkdbopt 
	 */
	public ImportTDXData() 
	{
		this.bkdbopt = new BanKuaiDbOperation ();
		this.stockdbopt = new StockDbOperations ();
		sysconfig = SystemConfigration.getInstance(); 
		initializeGui ();
		importPreCheckTDX ();
		iniiazlizeZdyGui ();
		createEvents ();
	}
	
	JCheckBox[] zdybkckbxs;
	private StockDbOperations stockdbopt;
	HashMap<String, String> zdybkmap;

	private SystemConfigration sysconfig;
	BanKuaiDbOperation bkdbopt;
	
	private void importPreCheckTDX()
	{
		String tdxpath = sysconfig.getTDXInstalledLocation();
		
		File file = new File(sysconfig.getTDXStockEverUsedNameFile() );
		if(!file.exists() ) {
			 System.out.println("通达信目录不正确:" + tdxpath ); 
			 JOptionPane.showMessageDialog(null,"通达信目录不正确，请重新设置!当前目录:" + tdxpath);
			 dispose();
		 }
		
		tfldresult.append("当前通达信目录:" + tdxpath + "\n");
//		FileReader tongdaxinfile = null;
//		try {
//			tongdaxinfile = new FileReader(sysconfig.getTDXSysAllBanKuaiFile() );
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		
	}
	
	private void iniiazlizeZdyGui() 
	{
		zdybkmap = this.bkdbopt.getTDXZiDingYiBanKuaiList ();
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
				i++;
			}
		} catch (java.lang.NullPointerException e) {
			JLabel zdyfilenull = new JLabel ("没有自定义板块");
			pnlZdy.add(zdyfilenull);
		}
		
	}

	private void partThatCanImportDuringWork ()
	{
		//从通达信中导入股票曾用名的信息
		if(chbximportcym.isSelected() && chbximportcym.isEnabled())	{
			File resulttmpfilesys = stockdbopt.refreshEverUsedStorkName ();
			chbximportcym.setEnabled(false);
			
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
		
		//同步自定义板块
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
//<<<<<<< HEAD
//=======
//		//注意代码的顺序，必须先同步板块信息，再同步成交量信息
//		
//>>>>>>> refs/remotes/exchange/master
		 //导入通达信定义的板块信息 ，包括概念，行业，风格，指数 板块
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
		

		//同步通达信板块成交量成交额
		if(chbxdaorutdxsysbkvol.isSelected() &&  chbxdaorutdxsysbkvol.isEnabled() ) {
			try {
				File resulttmpfilebkamppreck = bkdbopt.preCheckTDXBanKuaiVolAmoToDb ();
				List<String> lines = Files.readLines(resulttmpfilebkamppreck, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			try {
				File resulttmpfilebkamo = bkdbopt.refreshTDXBanKuaiVolAmoToDb();
				List<String> lines = Files.readLines(resulttmpfilebkamo, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			try {
				File resulttmpfilezhishupreck = bkdbopt.preCheckTDXZhiShuVolAmoToDb ();
				List<String> lines = Files.readLines(resulttmpfilezhishupreck, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
			try {
				File resulttmpfilezsamo = bkdbopt.refreshTDXZhiShuVolAmoToDb (); 
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
		
		//同步个股成交量
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
		}
		
		//从通达信foxpro中导入股票的基本面信息
		if(cbximporttdxgeguinfo.isSelected() && cbximporttdxgeguinfo.isEnabled()) {
			try {
				File resultimporttdxgegutinfo = this.stockdbopt.refreshStockJiBenMianInfoFromTdxFoxProFile ();
				List<String> lines = Files.readLines(resultimporttdxgegutinfo, sysconfig.charSet());
				for (String line : lines) {
		        	tfldresult.append(line+"\n");
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e) {
			}
			
		}
		

		
	}

	private void createEvents() 
	{
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				partThatCanImportDuringWork ();
				
				if(chbxdaorutdxsysbk.isSelected() || cbximporttdxgeguinfo.isSelected() || chbxdaorutdxsysbkvol.isSelected() ||cbxImportShGeGuVol.isSelected() || cbxImportSzGeGuVol.isSelected()) {
					Calendar cal = Calendar.getInstance();//可以对每个时间域单独修改
					int hour = cal.get(Calendar.HOUR_OF_DAY);
					int wk = cal.get(Calendar.DAY_OF_WEEK) - 1;
					if( (wk<=5 && wk>=1) && (hour<15 && hour>= 9) ) {
						JOptionPane.showMessageDialog(null,"涉及通达信大量数据同步，请在交易日15点收盘后至次日9点前从通达信导出数据后再导入本系统。");
						return;
					}
					
					partThatHasBeImportAfterWsork();
				}
				lblstatus.setText("同步结束");
			}
		});
		
	}

	private final JPanel contentPanel = new JPanel();
	private JProgressBar pbarbankuai;
	private JProgressBar progressBar_2;
	private JButton okButton;
	private JLabel lblstatus;
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
	
	private void initializeGui() 
	{
		setTitle("\u540C\u6B65\u901A\u8FBE\u4FE1\u6570\u636E");
		setBounds(100, 100, 526, 736);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		pbarbankuai = new JProgressBar();
		
		progressBar_2 = new JProgressBar(); 
		
		JScrollPane scrollPane = new JScrollPane();
		
		scrollPane_1 = new JScrollPane();
		
		chbxdaorutdxsysbk = new JCheckBox("\u5BFC\u5165\u901A\u8FBE\u4FE1\u7CFB\u7EDF\u677F\u5757\u548C\u6307\u6570\u4FE1\u606F(*)");
		
		chbxdaorutdxzdybk = new JCheckBox("\u5BFC\u5165\u901A\u8FBE\u4FE1\u81EA\u5B9A\u4E49\u677F\u5757(\u9009\u62E9\u8981\u5BFC\u5165\u7684\u81EA\u5B9A\u4E49\u677F\u5757)");
		
		chbxdaorutdxsysbkvol = new JCheckBox("\u5BFC\u5165\u901A\u8FBE\u4FE1\u7CFB\u7EDF\u6210\u4EA4\u91CF\u6210\u4EA4\u989D\u4FE1\u606F(*)");
		
		chbximportcym = new JCheckBox("\u5BFC\u5165\u901A\u8FBE\u4FE1\u80A1\u7968\u66FE\u7528\u540D\u4FE1\u606F");
		
		progressBar = new JProgressBar();
		
		cbximporttdxgeguinfo = new JCheckBox("\u5BFC\u5165\u901A\u8FBE\u4FE1\u4E2A\u80A1\u57FA\u672C\u9762\u4FE1\u606F(*)");
		
		progressBar_1 = new JProgressBar();
		
		cbxImportSzGeGuVol = new JCheckBox("\u5BFC\u5165\u6DF1\u5733\u4E2A\u80A1\u6210\u4EA4\u91CF\u6210\u4EA4\u989D\u4FE1\u606F(*)");
		
		cbxImportShGeGuVol = new JCheckBox("\u5BFC\u5165\u6CAA\u5E02\u4E2A\u80A1\u6210\u4EA4\u91CF\u6210\u4EA4\u989D\u4FE1\u606F(*)");
		
		JProgressBar progressBar_3 = new JProgressBar();
		
		JProgressBar progressBar_4 = new JProgressBar();
		
		JProgressBar progressBar_5 = new JProgressBar();
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
							.addComponent(chbxdaorutdxsysbk)
							.addGroup(gl_contentPanel.createSequentialGroup()
								.addComponent(cbximporttdxgeguinfo)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(progressBar_1, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE))
							.addComponent(pbarbankuai, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
								.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
									.addComponent(cbxImportSzGeGuVol)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(progressBar_4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
									.addComponent(cbxImportShGeGuVol)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(progressBar_3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
									.addComponent(chbximportcym)
									.addGap(11)
									.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 262, GroupLayout.PREFERRED_SIZE)))
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(chbxdaorutdxzdybk)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(progressBar_5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addComponent(scrollPane_1, Alignment.LEADING)
								.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
									.addComponent(chbxdaorutdxsysbkvol)
									.addGap(12)
									.addComponent(progressBar_2, GroupLayout.PREFERRED_SIZE, 205, GroupLayout.PREFERRED_SIZE)))))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(chbxdaorutdxsysbk)
						.addComponent(pbarbankuai, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(chbxdaorutdxzdybk)
						.addComponent(progressBar_5, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(progressBar_2, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
						.addComponent(chbxdaorutdxsysbkvol))
					.addPreferredGap(ComponentPlacement.UNRELATED, 10, GroupLayout.PREFERRED_SIZE)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(progressBar_4, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
						.addComponent(cbxImportSzGeGuVol))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(progressBar_3, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
						.addComponent(cbxImportShGeGuVol, Alignment.TRAILING))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
						.addComponent(chbximportcym))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(cbximporttdxgeguinfo)
						.addComponent(progressBar_1, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
					.addGap(36)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 252, GroupLayout.PREFERRED_SIZE)
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
			
			lblstatus = new JLabel("* \u5B58\u653E\u5728\u901A\u8FBE\u4FE1\u540C\u6B65\u6570\u636E\u5E93\u4E2D");
			
			btnStart = new JButton("\u5F00\u59CB\u5BFC\u5165");
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(18)
						.addComponent(lblstatus)
						.addGap(136)
						.addComponent(btnStart)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(okButton)
						.addGap(120))
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(5)
						.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblstatus)
							.addComponent(btnStart)
							.addComponent(okButton)))
			);
			buttonPane.setLayout(gl_buttonPane);
		}
		this.setLocationRelativeTo(null);
		
		//根据系统model定制哪些components可以被用户使用
		if(sysconfig.getSoftWareMode() == sysconfig.MODELCLIENT) {
			
		} else if(sysconfig.getSoftWareMode() == sysconfig.MODELSERVER) {
			
		}
		
	}
}
