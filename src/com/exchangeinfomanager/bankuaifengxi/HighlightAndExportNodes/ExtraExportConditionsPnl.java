package com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.exchangeinfomanager.gudong.JiGouService;
import com.google.common.base.Strings;

import net.miginfocom.swing.MigLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JSeparator;
import java.awt.Color;

public class ExtraExportConditionsPnl extends JPanel 
{
	private JCheckBox cbxExceptSt;
	private JCheckBox cbxOnlyCurBk;
	private JCheckBox chkbxexportallbk;
	private JCheckBox chkbxonlybkstock;
	private JCheckBox chkbxzhbiup;
	private JLabel lblNewLabel_2;
	private JLabel label_2;
	private JCheckBox chkbxexporbkallowedinsetting;

	private List<JCheckBox> huchixuanzebasic; //互斥意味着其中一个选了，其他都不惜不选
	private List<JCheckBox> huchixuanzeupdate; //有两个是不互斥的，和其他互斥
	private List<JCheckBox> huchixuanzeaboutstock;
	
	private JCheckBox chkbxexportyangxianbk;
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
	/**
	 * Create the panel.
	 */
	
	public ExtraExportConditionsPnl(BanKuaiGeGuMatchCondition cond) 
	{
		this.cond  = cond;

		initializeGui ();
		createEvents ();
		
		huchixuanzebasic = new ArrayList<JCheckBox> ();
		huchixuanzeupdate = new ArrayList<JCheckBox> ();
		huchixuanzeaboutstock = new ArrayList<JCheckBox> ();
		
		huchixuanzebasic.add(cbxOnlyCurBk);
		huchixuanzebasic.add(chkbxexportallbk);
		huchixuanzebasic.add(chkbxexporbkallowedinsetting);
		
		huchixuanzeupdate.add(chkbxzhbiup);
		huchixuanzeupdate.add(chkbxexportyangxianbk);
		
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
		
		if(chkbxzhbiup.isSelected() )
			this.cond.setExportChenJiaoErZhanbiUpBanKuai(true);
		else
			this.cond.setExportChenJiaoErZhanbiUpBanKuai(false);
		
		if(chkbxexportyangxianbk.isSelected() )
			this.cond.setExportYangXianBanKuai(true);
		else
			this.cond.setExportYangXianBanKuai(false);
		
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
		btncjezbgr.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("CHENJIAOERZHANBIGROWINGRATE","成交额占比增长率","%");
			}
		});
		btncjlzbgr.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("CHENJIAOLIANGZHANBIGROWINGRATE","成交量占比增长率","%");
			}
		});
		
		btncjezb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("CHENJIAOERZHANBI","成交额占比","%");
			}
		});
		btncjlzb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("CHENJIAOLIANGZHANBI","成交量占比","%");
			}
		});
		btncje.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("CHENJIAOER","成交额","亿");
			}
		});
		btngujia.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("GUJIA","股价","元");
			}
		});
		btnwkzhangfu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("WKZHANGFU","周涨幅","%");
			}
		});
		btnfreehsl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("FREEHSL","自由流通换手率","%");
			}
		});
		btnhsl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("HSL","换手率","%");
			}
		});
		btncjlmaxwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("DAILYCJLMAXWK","日平均成交量MAXWK","周");
			}
		});
		btncjemaxwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("DAILYCJEMAXWK","日平均成交额MAXWK","周");
			}
		});
		btncjlzbdpminwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("CJLZBDPMINWK","成交量周占比DPMINWK","周");
			}
		});
		btncjlzbdpmaxwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("CJLZBDPMAXWK","成交量周占比DPMAXWK","周");
			}
		});
		btncjezbdpminwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("CJEZBDPMINWK","成交额周占比DPMINWK","周");
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
				exportConditionSetting ("CJEZBDPMAXWK","成交额周占比DPMAXWK","周");
			}
		});
		
		btnzongshizhi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("ZONGSHIZHI","总市值","亿");
			}
		});
		btnliutongshizhi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exportConditionSetting ("LIUTONGSHIZHI","流通市值","亿");
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
				
				if(chkbxonlycurstock.isSelected()) {
					chkbxexportyangxianbk.setSelected(false);
					chkbxexportyangxianbk.setEnabled(false);
				} else {
					chkbxexportyangxianbk.setEnabled(true);
				}
			}
		});


		
		chkbxexportyangxianbk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				huchiOperationsForJCheckBox (huchixuanzebasic,chkbxexportyangxianbk);

			}
		});
		
		chkbxzhbiup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				huchiOperationsForJCheckBox (huchixuanzebasic,chkbxzhbiup);


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
		ExportConditionsFactorPnl expfpnl = new ExportConditionsFactorPnl (factorkw1,factorname,factordanwei);
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
	    	
	    	ArrayList<String> vars = new ArrayList<String>();
            Pattern p = Pattern.compile("\'.*?\'", Pattern.CASE_INSENSITIVE);
            String regxstring = tfldexportformula.getText();
            Matcher m = p.matcher(regxstring);
            while (m.find()) {
                vars.add(m.group());
            }
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
		chkbxexportallbk.setSelected(true);
		
		chkbxzhbiup = new JCheckBox("\u4EC5\u5BFC\u51FA\u6210\u4EA4\u989D/\u6210\u4EA4\u91CF\u5360\u6BD4\u73AF\u6BD4\u4E0A\u5347\u7684\u677F\u5757(\u677F\u5757\u8BBE\u7F6E\u65E0\u6548)");
		
		chkbxzhbiup.setEnabled(false);
		
		chkbxexporbkallowedinsetting = new JCheckBox("\u5BFC\u51FA\u677F\u5757\u8BBE\u7F6E\u5141\u8BB8\u7684\u677F\u5757");
		chkbxexporbkallowedinsetting.setEnabled(false);
		
		chkbxexportyangxianbk = new JCheckBox("\u4EC5\u5BFC\u51FA\u5468\u7EBF\u9633\u7EBF\u7684\u677F\u5757(\u677F\u5757\u8BBE\u7F6E\u65E0\u6548)");
		chkbxexportyangxianbk.setEnabled(false);
		
		chkbxonlyexportbk = new JCheckBox("\u4EC5\u5BFC\u51FA\u677F\u5757\uFF0C\u4E0D\u5BFC\u51FA\u677F\u5757\u4E2A\u80A1");
		
		lblNewLabel_2 = new JLabel("-----------------------");
		
		chkbxonlybkstock = new JCheckBox("\u4EC5\u5BFC\u51FA\u677F\u5757\u7684\u4E2A\u80A1\uFF0C\u4E0D\u5BFC\u51FA\u677F\u5757");
		
		chkbxonlycurstock = new JCheckBox("\u4EC5\u5BFC\u51FA\u5F53\u524D\u4E2A\u80A1");
		cbxExceptSt = new JCheckBox("\u4E0D\u5BFC\u51FAST\u4E2A\u80A1");
		cbxExceptSt.setSelected(true);
		
		label_2 = new JLabel("-----------------------");
		
		chckbxexportyellowbkstk = new JCheckBox ("导出黄标板块和个股");
		
btnCJEZbDpMaxWk = new JButton("\u6210\u4EA4\u989D\u5468\u5360\u6BD4DPMAXWK");
btnCJEZbDpMaxWk.setForeground(Color.RED);
		
		
		btnzongshizhi = new JButton("\u603B\u5E02\u503C");
		
		
		btnma = new JButton("CLOSE VS. \u5747\u7EBF");
		btnma.setForeground(Color.RED);
		
		
		tfldexportformula = new JTextArea();
		tfldexportformula.setLineWrap(true);
		tfldexportformula.setColumns(10);
		
		lblNewLabel = new JLabel(" \u5176\u4ED6\u5BFC\u51FA\u53C2\u6570\u8BBE\u7F6E");
		
btncjezbdpminwk = new JButton("\u6210\u4EA4\u989D\u5360\u6BD4DPMINWK");
btncjezbdpminwk.setEnabled(false);
		
		btncjlzbdpmaxwk = new JButton("\u6210\u4EA4\u91CF\u5468\u5360\u6BD4DPMAXWK");
		
		btncjlzbdpminwk = new JButton("\u6210\u4EA4\u91CF\u5360\u6BD4DPMINWK");
		btncjlzbdpminwk.setEnabled(false);
		
		btnliutongshizhi = new JButton("\u6D41\u901A\u5E02\u503C");
		btnliutongshizhi.setEnabled(false);
		
		JSeparator separator = new JSeparator();
		
		JLabel lblNewLabel_1 = new JLabel("------------------------");
		
		btncjemaxwk = new JButton("\u65E5\u5E73\u5747\u6210\u4EA4\u989DMAXWK");
		
		btncjlmaxwk = new JButton("\u65E5\u5E73\u5747\u6210\u4EA4\u91CFMAXWK");
		btncjlmaxwk.setEnabled(false);
		
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
							.addGap(7)
							.addComponent(chkbxzhbiup))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(7)
							.addComponent(chkbxexportyangxianbk))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(7)
							.addComponent(chkbxonlyexportbk))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(7)
							.addComponent(lblNewLabel_2))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(7)
							.addComponent(chkbxonlybkstock))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(7)
							.addComponent(chkbxonlycurstock))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(7)
							.addComponent(cbxExceptSt))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(17)
							.addComponent(label_2))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblNewLabel_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(separator, GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel)
								.addComponent(chckbxexportyellowbkstk)))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btngujia)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnwkzhangfu)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnhsl)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnfreehsl)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnma))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnzongshizhi)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnliutongshizhi))))
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
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(tfldexportformula)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnJisuanToFormula)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(txaJisuan, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
									.addGap(10))
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
									.addComponent(btncjlzbgr)))))
					.addContainerGap(13, Short.MAX_VALUE))
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
					.addGap(4)
					.addComponent(chkbxzhbiup)
					.addGap(4)
					.addComponent(chkbxexportyangxianbk)
					.addGap(4)
					.addComponent(chkbxonlyexportbk)
					.addGap(4)
					.addComponent(lblNewLabel_2)
					.addGap(4)
					.addComponent(chkbxonlybkstock)
					.addGap(4)
					.addComponent(chkbxonlycurstock)
					.addGap(4)
					.addComponent(cbxExceptSt)
					.addGap(4)
					.addComponent(label_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxexportyellowbkstk)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNewLabel)))
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
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(11)
									.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(btngujia)
										.addComponent(btnwkzhangfu)
										.addComponent(btnhsl)
										.addComponent(btnfreehsl)
										.addComponent(btnma))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(btnzongshizhi)
										.addComponent(btnliutongshizhi))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(tfldexportformula, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(73)
									.addComponent(txaJisuan, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)))
							.addGap(35))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnJisuanToFormula)
							.addGap(80))))
		);
		setLayout(groupLayout);
		
	
	}
}
