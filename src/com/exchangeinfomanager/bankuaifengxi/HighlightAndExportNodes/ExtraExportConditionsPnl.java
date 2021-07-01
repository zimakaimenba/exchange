package com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JButton;
import javax.swing.JSeparator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import java.awt.Font;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;
import java.awt.FlowLayout;

public class ExtraExportConditionsPnl extends JPanel 
{
	
	/**
	 * Create the panel.
	 */
	
	public ExtraExportConditionsPnl(BanKuaiAndGeGuMatchingConditions cond, LocalDate curselectdate1) 
	{
		this.cond  = cond;
		this.curselectdate = curselectdate1;

		huchixuanzebasic = new ArrayList<JCheckBox> ();
		huchixuanzeupdate = new ArrayList<JCheckBox> ();
		huchixuanzeaboutstock = new ArrayList<JCheckBox> ();
		
		initializeGui ();
		createEvents ();
	}
	/*
	 * 
	 */
	public BanKuaiAndGeGuMatchingConditions getSettingCondition ()
	{
		if(chkbxexportallbk.isSelected())
			this.cond.setExportAllBanKuai(true);
		else
			this.cond.setExportAllBanKuai(false);
		
		if(chkbxexporbkallowedinsetting.isSelected() )
			this.cond.setExportBankuaiInConfig(true);
		else
			this.cond.setExportBankuaiInConfig(false);
		
		if(cbxOnlyCurBk.isSelected() )
			this.cond.setExportOnlyCurrentBanKuai (true);
		else
			this.cond.setExportOnlyCurrentBanKuai (false);
		
		if(chckexportredbkgegu.isSelected() )
			this.cond.setExportGeGuOfRedSignBanKuai(true);
		else
			this.cond.setExportGeGuOfRedSignBanKuai(false);
		
		if(chckexportyellowbkgegu.isSelected() )
			this.cond.setExportGeGuOfYellowSignBanKuai(true);
		else
			this.cond.setExportGeGuOfYellowSignBanKuai(false);
		
		if( chkbxonlyexportbk.isSelected() )
			this.cond.setExportOnlyBankuaiNotGeGu(true);
		else
			this.cond.setExportOnlyBankuaiNotGeGu(false);
		
		if( chkbxonlybkstock.isSelected() )
			this.cond.setExportOnlyGeGuNotBanKuai(true);
		else
			this.cond.setExportOnlyGeGuNotBanKuai(false);
		
		
		if(cbxExceptSt.isSelected() )
			this.cond.setExportST(false);
		else
			this.cond.setExportST(true);
		
		if(chckbxexportyellowbkstk.isSelected())
			this.cond.setExportOnlyYellowSignBkStk (true);
		else
			this.cond.setExportOnlyYellowSignBkStk (false);
		
		if(chckbxexportredbkstk.isSelected())
			this.cond.setExportOnlyRedSignBkStk (true);
		else
			this.cond.setExportOnlyRedSignBkStk (false);
		
		
		this.cond.setExportConditionFormula(tfldexportformula.getText().trim());
		
		return this.cond;
	}
	/*
	 * 
	 */
	private void huchiOperationsForJCheckBox (List<JCheckBox> huchixuanlist, JCheckBox selectedjchb) 
	{
//		for(JCheckBox tmpjchb : huchixuanlist) {
//			if( !tmpjchb.getText().equals(selectedjchb.getText()) ) {
//				if(selectedjchb.isSelected()) {
//					tmpjchb.setSelected(false);
//					tmpjchb.setEnabled(false);
//				} else {
//					tmpjchb.setSelected(false);
//					tmpjchb.setEnabled(true);
//				}
//			}
//		}
	}

	private void createEvents() 
	{
		chckexportyellowbkgegu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(chckexportyellowbkgegu.isSelected()) {
					chckexportredbkgegu.setEnabled(false);
					chckexportredbkgegu.setSelected(false);
				} else {
					chckexportredbkgegu.setEnabled(true);
					chckexportredbkgegu.setSelected(false);
				}
			}
		});
		
		chckexportredbkgegu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(chckexportredbkgegu.isSelected()) {
					chckexportyellowbkgegu.setEnabled(false);
					chckexportyellowbkgegu.setSelected(false);
				} else {
					chckexportyellowbkgegu.setEnabled(true);
					chckexportyellowbkgegu.setSelected(false);
				}
			}
		});
		
		btnedit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tfldexportformula.setEditable(true);
			}
		});
		
		btnimportsavedformula.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				List<String> predefined = cond.getPredefinedExportConditionFormula();
				if(predefined == null  || predefined.isEmpty() ) 
					return;
				
				JTable tblpred = new JTable(){
				    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				 
			        Component comp = super.prepareRenderer(renderer, row, col);
			        Object value = getModel().getValueAt(row, col);
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
			String[] jtableTitleStrings = { "预定义公式信息" };
			tblpred.setRowSelectionAllowed(true) ;
			tblpred.setModel(new DefaultTableModel(
				new Object[][] {
				},
				jtableTitleStrings) {
					
					private static final long serialVersionUID = 1L;
			});
			for(String tmpformula : predefined ) {
				String [] value = {tmpformula};
				((DefaultTableModel)tblpred.getModel()).addRow(value);
			}

				JPanel myPanel = new JPanel();
			      myPanel.setPreferredSize(new Dimension(600, 250));
			      myPanel.setLayout(new BorderLayout());
			      JScrollPane sclpaneJtable = new JScrollPane();
				  sclpaneJtable.setViewportView(tblpred);
			      myPanel.add(sclpaneJtable,BorderLayout.CENTER);
			      
			      JTextArea formulainfo = new JTextArea ();
			      formulainfo.setEnabled(false);		
			      formulainfo.setLineWrap(true);
			      formulainfo.setForeground(Color.BLACK);
			      formulainfo.setFont(new Font("Monospaced", Font.BOLD, 16));
			      JScrollPane sclpaneformula = new JScrollPane();
			      sclpaneformula.setPreferredSize(new Dimension(600, 150));
			      sclpaneformula.setViewportView(formulainfo);
			      myPanel.add(sclpaneformula,BorderLayout.SOUTH);
			      
			      tblpred.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent arg0) {
							int row = tblpred.getSelectedRow();
							if(row <0) return;

							String sltformula = (String) ((DefaultTableModel)tblpred.getModel()).getValueAt(row, 0);
							formulainfo.setText(sltformula);
						}
					});
			      
			      int result = JOptionPane.showConfirmDialog(null, myPanel, "预定义公式", JOptionPane.OK_CANCEL_OPTION);
			      if (result == JOptionPane.OK_OPTION) {
			    	  int row = tblpred.getSelectedRow();
					  if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个公式","Warning",JOptionPane.WARNING_MESSAGE);
							return;
					  }
					  String sltformula = (String) ((DefaultTableModel)tblpred.getModel()).getValueAt(row, 0);
					  sltformula = sltformula.replace("XXXXXXXX", curselectdate.with(DayOfWeek.FRIDAY).toString().replaceAll("-", ""));
					  sltformula = sltformula.replace("YYYYYYYY", curselectdate.minusWeeks(1).with(DayOfWeek.FRIDAY).toString().replaceAll("-", ""));
					  sltformula = sltformula.replace("ZZZZZZZZ", curselectdate.minusWeeks(2).with(DayOfWeek.FRIDAY).toString().replaceAll("-", ""));
					  
					  tfldexportformula.setText( sltformula );
					  cond.setExportConditionFormula (tfldexportformula.getText());
			      }
			}
		});
		
		btndailykzhangfu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("DailyZhangDieFuRangeInWeek","日涨幅区间","%");
			}
		});
		
		btnClearFormula.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tfldexportformula.setText( "" );
				cond.setExportConditionFormula ("");
			}
		});
		
		btncjezbgr.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("CjeZbGrowRate","成交额占比增长率","%");
			}
		});
		btncjlzbgr.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("CjlZbGrowRate","成交量占比增长率","%");
			}
		});
		
		btncjezb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("ChengJiaoErZhanBi","成交额占比","%");
			}
		});
		btncjlzb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("ChengJiaoLiangZhanBi","成交量占比","%");
			}
		});
		btncje.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("ChenJiaoEr","成交额","亿");
			}
		});
		btngujia.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("GuJiaCLOSE","股价","元");
			}
		});
		btnwkzhangfu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("ZhangDieFu","周涨幅","%");
			}
		});
		btnfreehsl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("HuanShouLvFree","自由流通换手率","%");
			}
		});
		btnhsl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("HuanShouLv","换手率","%");
			}
		});
		btncjlmaxwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("AverageChenJiaoLiangMaxWeek","日平均成交量MAXWK","周");
			}
		});
		btncjemaxwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("AverageChenJiaoErMaxWeek","日平均成交额MAXWK","周");
			}
		});
		btncjlzbdpminwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("CjlZbDpMinWk","成交量周占比DPMINWK","周");
			}
		});
		btncjlzbdpmaxwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("CjlZbDpMaxWk","成交量周占比DPMAXWK","周");
			}
		});
		btncjezbdpminwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("CjeZbDpMinWk","成交额周占比DPMINWK","周");
			}
		});
		
		btnma.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("CLOSEVSMA","CLOSE VS. MA","");
			}
		});
		
		btnCJEZbDpMaxWk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("CjeZbDpMaxWk","成交额周占比DPMAXWK","周");
			}
		});
		
		btnzongshizhi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("ZongShiZhi","总市值","亿");
			}
		});
		btnliutongshizhi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("LiuTongShiZhi","流通市值","亿");
			}
		});
		
		chkbxonlyexportbk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				huchiOperationsForJCheckBox(huchixuanzeaboutstock,chkbxonlyexportbk);
				
				if(chkbxonlyexportbk.isSelected()) {
					cbxExceptSt.setSelected(false);
					cbxExceptSt.setEnabled(false);
				} else {
					cbxExceptSt.setEnabled(true);
					cbxExceptSt.setSelected(true);
				}
			}
		});
		
		btnJisuanToFormula.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String setting = txaJisuan.getText();
				if(tfldexportformula.getText().isEmpty()) 
		    		tfldexportformula.setText(   "(" + setting + ")" );
		    	else
		    		tfldexportformula.setText( tfldexportformula.getText() +" AND " +  "(" + setting + ")" );
		    	
		    	cond.setExportConditionFormula (tfldexportformula.getText());
		    	
		    	txaJisuan.setText("");
			}
		});

		
		chkbxonlybkstock.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				huchiOperationsForJCheckBox(huchixuanzeaboutstock,chkbxonlybkstock);

			}
		});

		
		chkbxexportallbk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

				huchiOperationsForJCheckBox (huchixuanzebasic,chkbxexportallbk);
				huchiOperationsForJCheckBox (huchixuanzeupdate,chkbxexportallbk);

			}
				
		});
		chkbxexporbkallowedinsetting.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				huchiOperationsForJCheckBox (huchixuanzebasic,chkbxexporbkallowedinsetting);
				huchiOperationsForJCheckBox (huchixuanzeupdate,chkbxexporbkallowedinsetting);
			}
		});
		cbxOnlyCurBk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			
				huchiOperationsForJCheckBox (huchixuanzebasic,cbxOnlyCurBk);
				huchiOperationsForJCheckBox (huchixuanzeupdate,cbxOnlyCurBk);
			}
		});
		
		chckbxexportyellowbkstk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(chckbxexportyellowbkstk.isSelected()) {
					chkbxonlyexportbk.setEnabled(false);
					chkbxonlyexportbk.setSelected(false);
					
					chkbxonlybkstock.setEnabled(false);
					chkbxonlybkstock.setSelected(false);
					
					chckexportredbkgegu.setEnabled(false);
					chckexportredbkgegu.setSelected(false);
					
					chckexportyellowbkgegu.setEnabled(false);
					chckexportyellowbkgegu.setSelected(false);
					
					
				} else {
					chkbxonlyexportbk.setEnabled(true);
					chkbxonlybkstock.setEnabled(true);
					
					chckexportredbkgegu.setEnabled(true);
					chckexportredbkgegu.setSelected(false);
					
					chckexportyellowbkgegu.setEnabled(true);
					chckexportyellowbkgegu.setSelected(false);
				}
				
				huchiOperationsForJCheckBox (huchixuanzebasic,chckbxexportyellowbkstk);
				huchiOperationsForJCheckBox (huchixuanzeupdate,chckbxexportyellowbkstk);
			}
		});
		
		chckbxexportredbkstk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(chckbxexportredbkstk.isSelected()) {
					chkbxonlyexportbk.setEnabled(false);
					chkbxonlyexportbk.setSelected(false);
					
					chkbxonlybkstock.setEnabled(false);
					chkbxonlybkstock.setSelected(false);
					
					chckexportredbkgegu.setEnabled(false);
					chckexportredbkgegu.setSelected(false);
					
					chckexportyellowbkgegu.setEnabled(false);
					chckexportyellowbkgegu.setSelected(false);
					
				} else {
					chkbxonlyexportbk.setEnabled(true);
					chkbxonlybkstock.setEnabled(true);
					
					chckexportredbkgegu.setEnabled(true);
					chckexportredbkgegu.setSelected(false);
					
					chckexportyellowbkgegu.setEnabled(true);
					chckexportyellowbkgegu.setSelected(false);
				}
				
				huchiOperationsForJCheckBox (huchixuanzebasic,chckbxexportredbkstk);
				huchiOperationsForJCheckBox (huchixuanzeupdate,chckbxexportredbkstk);
			}
		});

		
	}
	protected void exportConditionSetting(String factorkw1, String factorname, String factordanwei) 
	{
//		factorkw1 = factorkw1.toUpperCase();
		ExportConditionsFactorPnl expfpnl = new ExportConditionsFactorPnl (factorkw1,factorname,factordanwei,curselectdate);
		int result = JOptionPane.showConfirmDialog(null, expfpnl, "条件设置", JOptionPane.OK_CANCEL_OPTION);
	    if (result == JOptionPane.OK_OPTION) {
	    	String setting =  expfpnl.getExportConditionFactorSetting ();
	    	if(expfpnl.isJiSuanFactor()) {
	    		txaJisuan.setText(txaJisuan.getText() + "  " + setting);
	    		return;
	    	}
	    	//不是计算因子
	    	if(tfldexportformula.getText().isEmpty()) 
	    		tfldexportformula.setText(  "(" + setting + ")" );
	    	else
	    		tfldexportformula.setText( tfldexportformula.getText() +" AND " +  "(" + setting + ")" );
	    	
	    	this.cond.setExportConditionFormula (tfldexportformula.getText());
	    }
	}
	/*
	 * 
	 */
	private JCheckBox cbxExceptSt;
	private JCheckBox cbxOnlyCurBk;
	private JCheckBox chkbxexportallbk;
	private JCheckBox chkbxonlybkstock;
	private JLabel lblNewLabel_2;
	private JLabel label_2;
	private JCheckBox chkbxexporbkallowedinsetting;

	private List<JCheckBox> huchixuanzebasic; //互斥意味着其中一个选了，其他都不惜不选
	private List<JCheckBox> huchixuanzeupdate; //有两个是不互斥的，和其他互斥
	private List<JCheckBox> huchixuanzeaboutstock;
	private JCheckBox chkbxonlyexportbk;
	private BanKuaiAndGeGuMatchingConditions cond;
	private JCheckBox chckbxexportyellowbkstk;
	private JTextArea tfldexportformula;
	private JButton btnCJEZbDpMaxWk;
	private JButton btnzongshizhi;
	private JButton btnma;
	private JLabel lblNewLabel;
	private JButton btncjezbdpminwk;
	private JButton btncjlzbdpmaxwk;
	private JButton btncjlzbdpminwk;
	private JButton btnliutongshizhi;
	private JButton btncje;
	private JButton btngujia;
	private JButton btnwkzhangfu;
	private JButton btnfreehsl;
	private JButton btnhsl;
	private JButton btncjlmaxwk;
	private JButton btncjemaxwk;
	private JTextArea txaJisuan;
	private JButton btnJisuanToFormula;
	private JButton btncjlzb;
	private JButton btncjezb;
	private JButton btncjl;
	private JButton btncjezbgr;
	private JButton btncjlzbgr;
	private LocalDate curselectdate;
	private JButton btnClearFormula;
	private JButton btnimportsavedformula;
	private JButton btnedit;
	private JButton btndailykzhangfu;
	private JCheckBox chckbxexportredbkstk;
	private JCheckBox chckexportredbkgegu;
	private JSeparator separator;
	private JScrollPane scrollPane;
	private JLabel label;
	private JLabel lblNewLabel_1;
	private JCheckBox chckexportyellowbkgegu;
	private JScrollPane scrollPane_1;
	private JLabel lblNewLabel_5;
	private void initializeGui() 
	{
		
		lblNewLabel_2 = new JLabel("-----------------------");
		
		label_2 = new JLabel("-----------------------");
		
		lblNewLabel = new JLabel(" \u5176\u4ED6\u5BFC\u51FA\u53C2\u6570\u8BBE\u7F6E");
		
		separator = new JSeparator();
		
		lblNewLabel_1 = new JLabel("------------------------");
		
JPanel panel = new JPanel();
FlowLayout flowLayout = (FlowLayout) panel.getLayout();
flowLayout.setAlignment(FlowLayout.LEFT);
		
		JLabel label_1 = new JLabel("");
		
		JPanel panel_1 = new JPanel();
		
		JPanel panel_2 = new JPanel();
		FlowLayout fl_panel_2 = (FlowLayout) panel_2.getLayout();
		fl_panel_2.setAlignment(FlowLayout.LEFT);
		
		JPanel panel_3 = new JPanel();
		FlowLayout fl_panel_3 = (FlowLayout) panel_3.getLayout();
		fl_panel_3.setAlignment(FlowLayout.LEFT);
		
		JPanel panel_4 = new JPanel();
		FlowLayout fl_panel_4 = (FlowLayout) panel_4.getLayout();
		fl_panel_4.setAlignment(FlowLayout.LEFT);
		
		JPanel panel_5 = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(panel_5, GroupLayout.PREFERRED_SIZE, 793, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 796, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(separator, GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(803)
							.addComponent(label_1))
						.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 291, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_2)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(panel_2, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
							.addComponent(panel_3, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 344, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 409, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel)
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 799, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addComponent(label_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(84)
							.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(18)
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 122, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_5, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
					.addGap(139)
					.addComponent(label_1)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		btnJisuanToFormula = new JButton("<---");
		
//		scrollPane_1 = new JScrollPane();
//		pnldisplaygongshi.add(scrollPane_1, BorderLayout.EAST);
		
		txaJisuan = new JTextArea();
		txaJisuan.setLineWrap(true);
		
		scrollPane = new JScrollPane();
		
		
		tfldexportformula = new JTextArea();
		scrollPane.setViewportView(tfldexportformula);
		tfldexportformula.setFont(new Font("Monospaced", Font.BOLD, 16));
		tfldexportformula.setEditable(false);
		tfldexportformula.setLineWrap(true);
		tfldexportformula.setColumns(10);
		GroupLayout gl_panel_5 = new GroupLayout(panel_5);
		gl_panel_5.setHorizontalGroup(
			gl_panel_5.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_5.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 448, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnJisuanToFormula)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txaJisuan, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
					.addGap(15))
		);
		gl_panel_5.setVerticalGroup(
			gl_panel_5.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_5.createSequentialGroup()
					.addGroup(gl_panel_5.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_5.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panel_5.createParallelGroup(Alignment.BASELINE)
								.addComponent(txaJisuan, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_panel_5.createSequentialGroup()
							.addGap(59)
							.addComponent(btnJisuanToFormula, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(10, Short.MAX_VALUE))
		);
		panel_5.setLayout(gl_panel_5);
		
		chckbxexportredbkstk = new JCheckBox("\u5BFC\u51FA\u7EA2\u6807\u4E2A\u80A1\u548C\u7EA2\u6807\u677F\u5757");
		panel_4.add(chckbxexportredbkstk);
		
		chckbxexportyellowbkstk = new JCheckBox ("\u5BFC\u51FA\u9EC4\u6807\u677F\u5757\u548C\u7EA2\u6807\u4E2A\u80A1");
		panel_4.add(chckbxexportyellowbkstk);
		
		chkbxonlybkstock = new JCheckBox("\u4EC5\u5BFC\u51FA\u677F\u5757\u7684\u4E2A\u80A1\uFF0C\u4E0D\u5BFC\u51FA\u677F\u5757");
		panel_3.add(chkbxonlybkstock);
		chkbxonlybkstock.setSelected(true);
		
		chckexportredbkgegu = new JCheckBox("\u4EC5\u5BFC\u51FA\u7EA2\u6807\u677F\u5757\u5185\u4E2A\u80A1");
		panel_3.add(chckexportredbkgegu);
		
		chckexportyellowbkgegu = new JCheckBox("\u4EC5\u5BFC\u51FA\u9EC4\u6807\u677F\u5757\u5185\u4E2A\u80A1");
		panel_3.add(chckexportyellowbkgegu);
		cbxExceptSt = new JCheckBox("\u4E0D\u5BFC\u51FAST\u4E2A\u80A1");
		panel_3.add(cbxExceptSt);
		cbxExceptSt.setSelected(true);
		huchixuanzebasic.add(chkbxexportallbk);
		
		chkbxexportallbk = new JCheckBox("\u5BFC\u51FA\u5168\u90E8\u677F\u5757(\u677F\u5757\u5BFC\u51FA\u8BBE\u7F6E\u65E0\u6548)");
		panel_2.add(chkbxexportallbk);
		chkbxexportallbk.setEnabled(false);
		chkbxexportallbk.setSelected(true);
		huchixuanzebasic.add(chkbxexporbkallowedinsetting);
		
		chkbxexporbkallowedinsetting = new JCheckBox("\u5BFC\u51FA\u677F\u5757\u8BBE\u7F6E\u5141\u8BB8\u7684\u677F\u5757");
		panel_2.add(chkbxexporbkallowedinsetting);
		chkbxexporbkallowedinsetting.setEnabled(false);
		
		huchixuanzebasic.add(cbxOnlyCurBk);
		
		cbxOnlyCurBk = new JCheckBox("\u5BFC\u51FA\u6761\u4EF6\u4EC5\u9650\u5F53\u524D\u677F\u5757(\u677F\u5757\u8BBE\u7F6E\u65E0\u6548)");
		panel_2.add(cbxOnlyCurBk);
		cbxOnlyCurBk.setEnabled(false);
		huchixuanzeaboutstock.add(chkbxonlyexportbk);
		
		chkbxonlyexportbk = new JCheckBox("\u4EC5\u5BFC\u51FA\u677F\u5757\uFF0C\u4E0D\u5BFC\u51FA\u677F\u5757\u4E2A\u80A1");
		panel_2.add(chkbxonlyexportbk);
		chkbxonlyexportbk.setEnabled(false);
		
		btnClearFormula = new JButton("\u6E05\u9664");
		panel_1.add(btnClearFormula);
		
		btnedit = new JButton("\u7F16\u8F91");
		panel_1.add(btnedit);
		
		btnimportsavedformula = new JButton("\u5BFC\u5165\u9884\u5B9A\u4E49\u516C\u5F0F");
		panel_1.add(btnimportsavedformula);
		
		lblNewLabel_5 = new JLabel("New label                                                          ");
		panel_1.add(lblNewLabel_5);
		
		
		label = new JLabel("\u4EC5\u652F\u6301\u52A0\u51CF\u4E58\u9664\u8FD0\u7B97");
		panel_1.add(label);
		
		btncje = new JButton("\u6210\u4EA4\u989D");
		panel.add(btncje);
		btncje.setForeground(Color.RED);
		
		btncjezb = new JButton("\u6210\u4EA4\u989D\u5360\u6BD4");
		panel.add(btncjezb);
		btncjezb.setEnabled(false);
		btncjezb.setForeground(Color.BLACK);
		
btnCJEZbDpMaxWk = new JButton("\u6210\u4EA4\u989D\u5468\u5360\u6BD4DPMAXWK");
panel.add(btnCJEZbDpMaxWk);
btnCJEZbDpMaxWk.setForeground(Color.RED);

btncjezbdpminwk = new JButton("\u6210\u4EA4\u989D\u5360\u6BD4DPMINWK");
panel.add(btncjezbdpminwk);
btncjezbdpminwk.setEnabled(false);

btncjemaxwk = new JButton("\u65E5\u5E73\u5747\u6210\u4EA4\u989DMAXWK");
panel.add(btncjemaxwk);

btncjezbgr = new JButton("\u6210\u4EA4\u989D\u5360\u6BD4\u589E\u957F\u7387");
panel.add(btncjezbgr);
btncjezbgr.setForeground(Color.RED);

btncjl = new JButton("\u6210\u4EA4\u91CF");
panel.add(btncjl);
btncjl.setEnabled(false);

btncjlzb = new JButton("\u6210\u4EA4\u91CF\u5360\u6BD4");
panel.add(btncjlzb);
btncjlzb.setEnabled(false);

btncjlzbdpmaxwk = new JButton("\u6210\u4EA4\u91CF\u5468\u5360\u6BD4DPMAXWK");
panel.add(btncjlzbdpmaxwk);

btncjlzbdpminwk = new JButton("\u6210\u4EA4\u91CF\u5360\u6BD4DPMINWK");
panel.add(btncjlzbdpminwk);
btncjlzbdpminwk.setEnabled(false);

btncjlmaxwk = new JButton("\u65E5\u5E73\u5747\u6210\u4EA4\u91CFMAXWK");
panel.add(btncjlmaxwk);


btncjlzbgr = new JButton("\u6210\u4EA4\u91CF\u5360\u6BD4\u589E\u957F\u7387");
panel.add(btncjlzbgr);
btncjlzbgr.setEnabled(false);


btnzongshizhi = new JButton("\u603B\u5E02\u503C");
panel.add(btnzongshizhi);

btnliutongshizhi = new JButton("\u6D41\u901A\u5E02\u503C");
panel.add(btnliutongshizhi);

btngujia = new JButton("\u80A1\u4EF7");
panel.add(btngujia);

btnwkzhangfu = new JButton("\u6DA8\u5E45");
panel.add(btnwkzhangfu);

btndailykzhangfu = new JButton("\u5468\u5185\u65E5K\u6DA8\u5E45");
panel.add(btndailykzhangfu);

btnhsl = new JButton("\u6362\u624B\u7387");
panel.add(btnhsl);
btnhsl.setEnabled(false);

btnfreehsl = new JButton("\u81EA\u7531\u6D41\u901A\u6362\u624B\u7387");
panel.add(btnfreehsl);


btnma = new JButton("CLOSE VS. \u5747\u7EBF");
panel.add(btnma);
btnma.setForeground(Color.RED);
JLabel lblNewLabel_4 = new JLabel("                               ");
panel.add(lblNewLabel_4);
		setLayout(groupLayout);
		
	
	}
}
