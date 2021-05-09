package com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTable;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.exchangeinfomanager.gudong.JiGouService;
import com.google.common.base.Strings;

import net.miginfocom.swing.MigLayout;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JButton;
import javax.swing.JSeparator;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JScrollPane;

public class ExtraExportConditionsPnl extends JPanel 
{
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
	private JCheckBox chkbxonlycurstock;
	private BanKuaiGeGuMatchCondition cond;
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
	/**
	 * Create the panel.
	 */
	
	public ExtraExportConditionsPnl(BanKuaiGeGuMatchCondition cond, LocalDate curselectdate1) 
	{
		this.cond  = cond;
		this.curselectdate = curselectdate1;

		initializeGui ();
		createEvents ();
		
		huchixuanzebasic = new ArrayList<JCheckBox> ();
		huchixuanzeupdate = new ArrayList<JCheckBox> ();
		huchixuanzeaboutstock = new ArrayList<JCheckBox> ();
		
		huchixuanzebasic.add(cbxOnlyCurBk);
		huchixuanzebasic.add(chkbxexportallbk);
		huchixuanzebasic.add(chkbxexporbkallowedinsetting);
		
		huchixuanzeaboutstock.add(chkbxonlybkstock);
		huchixuanzeaboutstock.add(chkbxonlyexportbk);
		huchixuanzeaboutstock.add(chkbxonlycurstock);
		
			
	}
	/*
	 * 
	 */
	public BanKuaiGeGuMatchCondition getSettingCondition ()
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
		
//		if(chkbxzhbiup.isSelected() )
//			this.cond.setExportChenJiaoErZhanbiUpBanKuai(true);
//		else
//			this.cond.setExportChenJiaoErZhanbiUpBanKuai(false);
//		
//		if(chkbxexportyangxianbk.isSelected() )
//			this.cond.setExportYangXianBanKuai(true);
//		else
//			this.cond.setExportYangXianBanKuai(false);
		
		if( chkbxonlyexportbk.isSelected() )
			this.cond.setExportOnlyBankuaiNotGeGu(true);
		else
			this.cond.setExportOnlyBankuaiNotGeGu(false);
		
		if( chkbxonlybkstock.isSelected() )
			this.cond.setExportOnlyGeGuNotBanKuai(true);
		else
			this.cond.setExportOnlyGeGuNotBanKuai(false);
		
		if( chkbxonlycurstock.isSelected() )
			this.cond.setExportOnlyCurrentGeGu(true);
		else
			this.cond.setExportOnlyCurrentGeGu(false);

		

		
		if(cbxExceptSt.isSelected() )
			this.cond.setExportST(false);
		else
			this.cond.setExportST(true);
		
		if(chckbxexportyellowbkstk.isSelected())
			this.cond.setExportOnlyYellowSignBkStk (true);
		else
			this.cond.setExportOnlyYellowSignBkStk (false);
		
		
		
		return this.cond;
	}
	/*
	 * 
	 */
	private void huchiOperationsForJCheckBox (List<JCheckBox> huchixuanlist, JCheckBox selectedjchb) 
	{
		for(JCheckBox tmpjchb : huchixuanlist) {
			if( !tmpjchb.getText().equals(selectedjchb.getText()) ) {
				if(selectedjchb.isSelected()) {
					tmpjchb.setSelected(false);
					tmpjchb.setEnabled(false);
				} else {
					tmpjchb.setSelected(false);
					tmpjchb.setEnabled(true);
				}
			}
		}
	}

	private void createEvents() 
	{
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
			      myPanel.setPreferredSize(new Dimension(600, 150));
			      myPanel.setLayout(new FlowLayout());
			      JScrollPane sclpaneJtable = new JScrollPane();
				  sclpaneJtable.setViewportView(tblpred);
			      myPanel.add(sclpaneJtable);

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
		
		chkbxonlycurstock.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				huchiOperationsForJCheckBox(huchixuanzeaboutstock,chkbxonlycurstock);
				huchiOperationsForJCheckBox (huchixuanzebasic,chkbxonlycurstock);
				huchiOperationsForJCheckBox (huchixuanzeupdate,chkbxonlycurstock);
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
					
					chkbxonlycurstock.setEnabled(false);
					chkbxonlycurstock.setSelected(false);
				} else {
					chkbxonlyexportbk.setEnabled(true);
					chkbxonlycurstock.setEnabled(true);
					chkbxonlybkstock.setEnabled(true);
				}
				
				huchiOperationsForJCheckBox (huchixuanzebasic,chckbxexportyellowbkstk);
				huchiOperationsForJCheckBox (huchixuanzeupdate,chckbxexportyellowbkstk);
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
	    	String tmp = tfldexportformula.getText();
	    	this.cond.setExportConditionFormula (tfldexportformula.getText());
	    }
	}
	/*
	 * 
	 */
	private void initializeGui() 
	{
		
		cbxOnlyCurBk = new JCheckBox("\u5BFC\u51FA\u6761\u4EF6\u4EC5\u9650\u5F53\u524D\u677F\u5757(\u677F\u5757\u8BBE\u7F6E\u65E0\u6548)");
		cbxOnlyCurBk.setEnabled(false);
		
		chkbxexportallbk = new JCheckBox("\u5BFC\u51FA\u5168\u90E8\u677F\u5757(\u677F\u5757\u5BFC\u51FA\u8BBE\u7F6E\u65E0\u6548)");
		chkbxexportallbk.setEnabled(false);
		chkbxexportallbk.setSelected(true);
		
		chkbxexporbkallowedinsetting = new JCheckBox("\u5BFC\u51FA\u677F\u5757\u8BBE\u7F6E\u5141\u8BB8\u7684\u677F\u5757");
		chkbxexporbkallowedinsetting.setEnabled(false);
		
		chkbxonlyexportbk = new JCheckBox("\u4EC5\u5BFC\u51FA\u677F\u5757\uFF0C\u4E0D\u5BFC\u51FA\u677F\u5757\u4E2A\u80A1");
		chkbxonlyexportbk.setEnabled(false);
		
		lblNewLabel_2 = new JLabel("-----------------------");
		
		chkbxonlybkstock = new JCheckBox("\u4EC5\u5BFC\u51FA\u677F\u5757\u7684\u4E2A\u80A1\uFF0C\u4E0D\u5BFC\u51FA\u677F\u5757");
		chkbxonlybkstock.setSelected(true);
		
		chkbxonlycurstock = new JCheckBox("\u4EC5\u5BFC\u51FA\u5F53\u524D\u4E2A\u80A1");
		chkbxonlycurstock.setEnabled(false);
		cbxExceptSt = new JCheckBox("\u4E0D\u5BFC\u51FAST\u4E2A\u80A1");
		cbxExceptSt.setSelected(true);
		
		label_2 = new JLabel("-----------------------");
		
		chckbxexportyellowbkstk = new JCheckBox ("导出黄标板块和个股");
		
btnCJEZbDpMaxWk = new JButton("\u6210\u4EA4\u989D\u5468\u5360\u6BD4DPMAXWK");
btnCJEZbDpMaxWk.setForeground(Color.RED);
		
		
		btnzongshizhi = new JButton("\u603B\u5E02\u503C");
		
		
		btnma = new JButton("CLOSE VS. \u5747\u7EBF");
		btnma.setForeground(Color.RED);
		
		lblNewLabel = new JLabel(" \u5176\u4ED6\u5BFC\u51FA\u53C2\u6570\u8BBE\u7F6E");
		
btncjezbdpminwk = new JButton("\u6210\u4EA4\u989D\u5360\u6BD4DPMINWK");
btncjezbdpminwk.setEnabled(false);
		
		btncjlzbdpmaxwk = new JButton("\u6210\u4EA4\u91CF\u5468\u5360\u6BD4DPMAXWK");
		
		btncjlzbdpminwk = new JButton("\u6210\u4EA4\u91CF\u5360\u6BD4DPMINWK");
		btncjlzbdpminwk.setEnabled(false);
		
		btnliutongshizhi = new JButton("\u6D41\u901A\u5E02\u503C");
		
		JSeparator separator = new JSeparator();
		
		JLabel lblNewLabel_1 = new JLabel("------------------------");
		
		btncjemaxwk = new JButton("\u65E5\u5E73\u5747\u6210\u4EA4\u989DMAXWK");
		
		btncjlmaxwk = new JButton("\u65E5\u5E73\u5747\u6210\u4EA4\u91CFMAXWK");
		
		btnhsl = new JButton("\u6362\u624B\u7387");
		btnhsl.setEnabled(false);
		
		btnfreehsl = new JButton("\u81EA\u7531\u6D41\u901A\u6362\u624B\u7387");
		
		btnwkzhangfu = new JButton("\u5468\u6DA8\u5E45");
		
		btngujia = new JButton("\u80A1\u4EF7");
		
		btncje = new JButton("\u6210\u4EA4\u989D");
		btncje.setForeground(Color.RED);
		
		txaJisuan = new JTextArea();
		txaJisuan.setLineWrap(true);
		
		btncjezb = new JButton("\u6210\u4EA4\u989D\u5360\u6BD4");
		btncjezb.setEnabled(false);
		btncjezb.setForeground(Color.BLACK);
		
		btncjlzb = new JButton("\u6210\u4EA4\u91CF\u5360\u6BD4");
		btncjlzb.setEnabled(false);
		
		btnJisuanToFormula = new JButton("<---");
		
		btncjl = new JButton("\u6210\u4EA4\u91CF");
		btncjl.setEnabled(false);
		
		btncjezbgr = new JButton("\u6210\u4EA4\u989D\u5360\u6BD4\u589E\u957F\u7387");
		btncjezbgr.setForeground(Color.RED);
		
		
		btncjlzbgr = new JButton("\u6210\u4EA4\u91CF\u5360\u6BD4\u589E\u957F\u7387");
		btncjlzbgr.setEnabled(false);
		
		btnClearFormula = new JButton("\u6E05\u9664");
		
		
		JLabel label = new JLabel("\u4EC5\u652F\u6301\u52A0\u51CF\u4E58\u9664\u8FD0\u7B97");
		
		btnimportsavedformula = new JButton("\u5BFC\u5165\u9884\u5B9A\u4E49\u516C\u5F0F");
		
		JScrollPane scrollPane = new JScrollPane();
		
		btnedit = new JButton("\u7F16\u8F91");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(7)
							.addComponent(chkbxexportallbk))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(7)
							.addComponent(chkbxexporbkallowedinsetting))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(7)
							.addComponent(cbxOnlyCurBk))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(158)
							.addComponent(separator, GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(btncje)
							.addGap(18)
							.addComponent(btncjezb)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCJEZbDpMaxWk)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btncjezbdpminwk)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btncjemaxwk)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btncjezbgr))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btncjl)
									.addGap(18)
									.addComponent(btncjlzb)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btncjlzbdpmaxwk)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btncjlzbdpminwk)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btncjlmaxwk)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btncjlzbgr))
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(btnJisuanToFormula))
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(btnClearFormula)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(btnedit)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(btnimportsavedformula)))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(label)
										.addComponent(txaJisuan, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE))
									.addGap(14))))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnzongshizhi)
							.addGap(18)
							.addComponent(btnliutongshizhi))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel_2)
								.addComponent(chkbxonlyexportbk)))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(chkbxonlybkstock))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(chkbxonlycurstock))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(cbxExceptSt))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(label_2))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel_1)
								.addComponent(chckbxexportyellowbkstk)))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblNewLabel))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(btngujia)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnwkzhangfu)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnhsl)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnfreehsl)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnma)))
					.addGap(13))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(7)
					.addComponent(chkbxexportallbk)
					.addGap(4)
					.addComponent(chkbxexporbkallowedinsetting)
					.addGap(4)
					.addComponent(cbxOnlyCurBk)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chkbxonlyexportbk)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblNewLabel_2)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chkbxonlybkstock)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chkbxonlycurstock)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(cbxExceptSt)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(label_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxexportyellowbkstk)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblNewLabel_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel)
					.addGap(16)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btncje)
						.addComponent(btncjezb)
						.addComponent(btnCJEZbDpMaxWk)
						.addComponent(btncjezbdpminwk)
						.addComponent(btncjemaxwk)
						.addComponent(btncjezbgr))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btncjl)
						.addComponent(btncjlzb)
						.addComponent(btncjlzbdpmaxwk)
						.addComponent(btncjlzbdpminwk)
						.addComponent(btncjlmaxwk)
						.addComponent(btncjlzbgr))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnzongshizhi)
						.addComponent(btnliutongshizhi))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btngujia)
						.addComponent(btnwkzhangfu)
						.addComponent(btnhsl)
						.addComponent(btnfreehsl)
						.addComponent(btnma))
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(43)
							.addComponent(btnJisuanToFormula))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(6)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
								.addComponent(txaJisuan, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE))))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(label)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnimportsavedformula)
							.addComponent(btnClearFormula)
							.addComponent(btnedit)))
					.addGap(77))
		);
		
		
		tfldexportformula = new JTextArea();
		scrollPane.setViewportView(tfldexportformula);
		tfldexportformula.setFont(new Font("Monospaced", Font.BOLD, 16));
		tfldexportformula.setEditable(false);
		tfldexportformula.setLineWrap(true);
		tfldexportformula.setColumns(10);
		setLayout(groupLayout);
		
	
	}
}
