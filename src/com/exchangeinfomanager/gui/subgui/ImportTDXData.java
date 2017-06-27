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
	public ImportTDXData(BanKuaiDbOperation bkdbopt,StockDbOperations stockdbopt) 
	{
		this.bkdbopt = bkdbopt;
		this.stockdbopt = stockdbopt;
		sysconfig = SystemConfigration.getInstance(); 
		initializeGui ();
		iniiazlizeZdyGui ();
		createEvents ();
	}
	JCheckBox[] zdybkckbxs;
	private StockDbOperations stockdbopt;
	HashMap<String, String> zdybkmap;
	private BanKuaiDbOperation bkdbopt;
	private SystemConfigration sysconfig;
	
	private void iniiazlizeZdyGui() 
	{
		zdybkmap = this.bkdbopt.getTDXZiDingYiBanKuaiList ();
		ArrayList<String> zdybknames = new ArrayList<String> ();
		try {
			zdybknames.addAll(zdybkmap.keySet());
		} catch (java.lang.NullPointerException e) {
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
		}
		
	}


	private void createEvents() 
	{
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				//从通达信中导入股票曾用名的信息
				if(chbximportcym.isSelected() )	{
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
			
				 //导入通达信定义的板块信息 ，包括概念，行业，风格，指数 板块
				if(chbxdaorutdxsysbk.isSelected()) {
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
				
				
				//同步自定义板块
				if(chbxdaorutdxzdybk.isSelected()) { 
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
				
				
				//同步通达信成交量成交额
				if(chbxdaorutdxsysbkvol.isSelected()) {
					Calendar cal = Calendar.getInstance();//可以对每个时间域单独修改
					int hour = cal.get(Calendar.HOUR_OF_DAY);
					int wk = cal.get(Calendar.DAY_OF_WEEK) - 1;
					if( (wk<=5 && wk>=1) && (hour<15 && hour>=9) ) {
						JOptionPane.showMessageDialog(null,"为保证成交量成交额数据完整 ，请在交易日15点收盘后至次日9点前从通达信导出数据再导入本系统。");
						return;
					}
					
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
	
	private void initializeGui() 
	{
		setTitle("\u540C\u6B65\u901A\u8FBE\u4FE1\u6570\u636E");
		setBounds(100, 100, 502, 736);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		pbarbankuai = new JProgressBar();
		
		progressBar_2 = new JProgressBar();
		
		JScrollPane scrollPane = new JScrollPane();
		
		scrollPane_1 = new JScrollPane();
		
		chbxdaorutdxsysbk = new JCheckBox("\u5BFC\u5165\u901A\u8FBE\u4FE1\u7CFB\u7EDF\u677F\u5757\u4FE1\u606F(*)");
		
		chbxdaorutdxzdybk = new JCheckBox("\u5BFC\u5165\u901A\u8FBE\u4FE1\u81EA\u5B9A\u4E49\u677F\u5757(\u9009\u62E9\u8981\u5BFC\u5165\u7684\u81EA\u5B9A\u4E49\u677F\u5757)");
		
		chbxdaorutdxsysbkvol = new JCheckBox("\u5BFC\u5165\u901A\u8FBE\u4FE1\u7CFB\u7EDF\u6210\u4EA4\u91CF\u6210\u4EA4\u989D\u4FE1\u606F(*)");
		
		chbximportcym = new JCheckBox("\u5BFC\u5165\u901A\u8FBE\u4FE1\u80A1\u7968\u66FE\u7528\u540D\u4FE1\u606F");
		
		progressBar = new JProgressBar();
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(chbxdaorutdxsysbk)
						.addComponent(chbximportcym)
						.addComponent(chbxdaorutdxzdybk)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(pbarbankuai, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(scrollPane_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE))
							.addGap(236))
						.addComponent(chbxdaorutdxsysbkvol)
						.addComponent(progressBar_2, GroupLayout.PREFERRED_SIZE, 440, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(scrollPane, Alignment.LEADING)
							.addComponent(progressBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(chbxdaorutdxsysbk)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(pbarbankuai, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chbxdaorutdxzdybk)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chbxdaorutdxsysbkvol)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(progressBar_2, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chbximportcym)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
					.addGap(35)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 265, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(65, Short.MAX_VALUE))
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



	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			ImportTDXData dialog = new ImportTDXData(null);
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
