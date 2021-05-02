package com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes;

import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.exchangeinfomanager.commonlib.ReminderPopToolTip;
import com.google.common.base.Strings;

import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;

class ExtraExportConditions extends JPanel 
{
	private JTextField tfldshizhilevelyangxian;
	private JCheckBox cbxShiZhilevelyangxian;
	private JCheckBox cbxExceptSt;
	private JCheckBox cbxOnlyCurBk;
	private JTextField tfldyangxian;
	private JTextField tfldshizhilevellianxu;
	private JTextField tfldwkyangxian;
	private JCheckBox chckbxshizhilevellianxu;
	private JCheckBox chkbxexportallbk;
	private JCheckBox chkbxonlybkstock;
	private JCheckBox chkbxzhbiup;
	private JLabel lblNewLabel_2;
	private JLabel label_2;
	private JCheckBox chckbxexportyangxiangegu;
	private JCheckBox chkbxexporbkallowedinsetting;
	private JTextField tfldwkzhangfumin;
	private JLabel label_3;

	private List<JCheckBox> huchixuanzebasic; //互斥意味着其中一个选了，其他都不惜不选
	private List<JCheckBox> huchixuanzeupdate; //有两个是不互斥的，和其他互斥
	private List<JCheckBox> huchixuanzeaboutstock;
	
	private JCheckBox chkbxexportyangxianbk;
	private JCheckBox chkbxonlyexportbk;
	private JCheckBox chkbxonlycurstock;
	private BanKuaiGeGuMatchCondition cond;
	private JCheckBox chckbxexportyellowbkstk;
	private JLabel label;
	private JLabel label_1;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JTextField tfldwkzhangfumax;
	private JCheckBox chbxlwyxbenzhouyangxian;
	private JCheckBox chckbxdpcjezbgrowingrate;
	private JTextField tflddpcjezbgrowingratemin;
	private JTextField tflddpcjezbgrowingratemax;
	/**
	 * Create the panel.
	 */
	
	public ExtraExportConditions(BanKuaiGeGuMatchCondition cond) 
	{
		this.cond  = cond;

		initializeGui ();
		
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
		
		tfldwkzhangfumax = new JTextField();
		tfldwkzhangfumax.setColumns(10);
		
		JLabel label_4 = new JLabel("%");
		
		JLabel label_5 = new JLabel("~");
		
		chbxlwyxbenzhouyangxian = new JCheckBox("\u5BFC\u51FA\u4E0A\u5468\u7B26\u5408\u8BBE\u7F6E\u6761\u4EF6\u4E14\u6DA8\u5E45\u9634\u7EBF\uFF0C\u672C\u5468\u5468\u7EBF\u6DA8\u5E45\u9633\u7EBF\u4E2A\u80A1");
		chbxlwyxbenzhouyangxian.setEnabled(false);
		
		JLabel lblNewLabel_3 = new JLabel("------------------------");
		
		chckbxdpcjezbgrowingrate = new JCheckBox("\u5BFC\u51FA\u5468\u6210\u4EA4\u989D\u5360\u6BD4\u589E\u957F\u7387");
		
		tflddpcjezbgrowingratemin = new JTextField();
		tflddpcjezbgrowingratemin.setText("90");
		tflddpcjezbgrowingratemin.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("%  ~");
		
		tflddpcjezbgrowingratemax = new JTextField();
		tflddpcjezbgrowingratemax.setColumns(10);
		
		JLabel label_6 = new JLabel("%");
		setLayout(new MigLayout("", "[160px][9px][13px][8px][39px][37px][4px][13px][10px][6px][8px][24px][3px][6px][8px][14px][8px][27px][36px]", "[23px][23px][23px][23px][23px][23px][15px][23px][23px][23px][23px][23px][24px][23px][15px][23px][15px][23px]"));
		add(chkbxexportallbk, "cell 0 0 5 1,alignx left,aligny top");
		add(chkbxexporbkallowedinsetting, "cell 0 1,alignx left,aligny top");
		add(cbxOnlyCurBk, "cell 0 2 5 1,alignx left,aligny top");
		add(chkbxzhbiup, "cell 0 3 12 1,alignx left,aligny top");
		add(chkbxexportyangxianbk, "cell 0 4 5 1,alignx left,aligny top");
		add(chkbxonlyexportbk, "cell 0 5 3 1,alignx right,aligny top");
		add(lblNewLabel_2, "cell 0 6,alignx left,aligny top");
		add(chkbxonlybkstock, "cell 0 7 5 1,alignx left,aligny top");
		add(chkbxonlycurstock, "cell 0 8,alignx left,aligny top");
		add(chckbxdpcjezbgrowingrate, "cell 0 9,alignx center,aligny top");
		add(cbxExceptSt, "cell 0 13,alignx left,aligny top");
		add(label_2, "cell 0 14,alignx left,aligny top");
		add(chckbxexportyangxiangegu, "cell 0 10,alignx left,aligny top");
		add(tfldwkzhangfumin, "cell 4 10 2 1,growx,aligny center");
		add(label_3, "cell 7 10,alignx center,aligny center");
		add(label_5, "cell 9 10,alignx left,aligny center");
		add(tfldwkzhangfumax, "cell 11 10,growx,aligny top");
		add(cbxShiZhilevelyangxian, "cell 0 11,alignx left,aligny top");
		add(tfldshizhilevelyangxian, "cell 2 11 3 1,growx,aligny center");
		add(chckbxshizhilevellianxu, "cell 0 12,alignx left,aligny bottom");
		add(tfldshizhilevellianxu, "cell 2 12 3 1,growx,aligny center");
		add(lblNewLabel, "cell 5 12 7 1,alignx left,aligny center");
		add(label, "cell 5 11 3 1,alignx left,aligny center");
		add(tfldwkyangxian, "cell 15 12 3 1,growx,aligny top");
		add(tfldyangxian, "cell 15 11 3 1,growx,aligny center");
		add(label_1, "cell 18 11,alignx left,aligny center");
		add(lblNewLabel_1, "cell 18 12,alignx left,aligny top");
		add(label_4, "cell 13 10,alignx left,aligny center");
		add(lblNewLabel_3, "cell 0 16,alignx right,aligny top");
		add(chbxlwyxbenzhouyangxian, "cell 0 15 14 1,alignx center,aligny top");
		add(chckbxexportyellowbkstk, "cell 0 17,alignx left,aligny top");
		add(tflddpcjezbgrowingratemin, "cell 4 9 2 1,alignx left,aligny center");
		add(lblNewLabel_4, "cell 7 9 3 1,alignx left,aligny center");
		add(tflddpcjezbgrowingratemax, "cell 11 9 5 1,growx,aligny center");
		add(label_6, "cell 17 9,alignx left,aligny center");
		
		createEvents ();
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
		
		if( chckbxexportyangxiangegu.isSelected() ) {
			this.cond.setExporGeGuWithZhangFuQuJian(true);
			
			Double min = null; Double max = null;
			if( !Strings.isNullOrEmpty(tfldwkzhangfumin.getText() ) )
				min = Double.parseDouble(tfldwkzhangfumin.getText() ) / 100;
			else min = -1000.0;
			if( !Strings.isNullOrEmpty(tfldwkzhangfumax.getText() ) )
				max = Double.parseDouble(tfldwkzhangfumax.getText() ) / 100;
			else max= 1000.0;
			this.cond.setExportGeGuZhangFuQuJian(min, max  );
		} else {
			this.cond.setExportGeGuZhangFuQuJian( null, null );
		}
		
		if(chckbxdpcjezbgrowingrate.isSelected() ) {
			this.cond.setExportDpCjeZbGrowingRate (true);
			
			Double min = null; Double max = null;
			if( !Strings.isNullOrEmpty(tflddpcjezbgrowingratemin.getText() ) )
				min = Double.parseDouble(tflddpcjezbgrowingratemin.getText() ) / 100;
			else min = -1000.0;
			if( !Strings.isNullOrEmpty(tflddpcjezbgrowingratemax.getText() ) )
				max = Double.parseDouble(tflddpcjezbgrowingratemax.getText() ) / 100;
			else max= 1000000000000.0;
			this.cond.setExportDpCjeZbGrowingRateQuJian(min, max  );
		} else {
			this.cond.setExportDpCjeZbGrowingRateQuJian( null, null );
		}
		
		if( cbxShiZhilevelyangxian.isSelected() ) {
			this.cond.setChenJiaoErBottomForYangXianLevle( 100000000 * Double.parseDouble(tfldshizhilevelyangxian.getText() ) );
			this.cond.setChenJiaoErBottomYangXianLevel ( Double.parseDouble(tfldyangxian.getText() ) );
		}
		else
			this.cond.setChenJiaoErBottomForYangXianLevle( null );
		
		if(chckbxshizhilevellianxu.isSelected() ) {
			this.cond.setChenJiaoErBottomForFangLiangLevle( 100000000 * Double.parseDouble( tfldshizhilevellianxu.getText() ) );
			this.cond.setChenJiaoErFangLiangLevel ( Integer.parseInt( tfldwkyangxian.getText() ));
		}
		else
			this.cond.setChenJiaoErBottomForFangLiangLevle( null );
		
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
		chkbxonlyexportbk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				huchiOperationsForJCheckBox(huchixuanzeaboutstock,chkbxonlyexportbk);
				
				if(chkbxonlyexportbk.isSelected()) {
					cbxShiZhilevelyangxian.setEnabled(false);
					cbxShiZhilevelyangxian.setSelected(false);
					chckbxshizhilevellianxu.setEnabled(false);
					chckbxshizhilevellianxu.setSelected(false);
					tfldshizhilevelyangxian.setEnabled(false);
					
					tfldyangxian.setEnabled(false);
					tfldshizhilevellianxu.setEnabled(false);
					tfldwkyangxian.setEnabled(false);
					

				} else {
					cbxShiZhilevelyangxian.setEnabled(true);
					cbxShiZhilevelyangxian.setSelected(true);
					chckbxshizhilevellianxu.setEnabled(true);
					chckbxshizhilevellianxu.setSelected(true);
					tfldshizhilevelyangxian.setEnabled(true);
					tfldyangxian.setEnabled(true);
					tfldshizhilevellianxu.setEnabled(true);
					tfldwkyangxian.setEnabled(true);
					
				}

				
				if(chkbxonlyexportbk.isSelected()) {
//					chkbxexportyangxianbk.setSelected(false);
//					chkbxexportyangxianbk.setEnabled(false);
					
					cbxExceptSt.setSelected(false);
					cbxExceptSt.setEnabled(false);
					
					chckbxexportyangxiangegu.setSelected(false);
					chckbxexportyangxiangegu.setEnabled(false);
					
					tfldwkzhangfumin.setEnabled(false);
					
				} else {
//					chkbxexportyangxianbk.setEnabled(true);
					
					cbxExceptSt.setEnabled(true);
					cbxExceptSt.setSelected(true);
					
					chckbxexportyangxiangegu.setEnabled(true);
					tfldwkzhangfumin.setEnabled(true);
				}
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
					cbxShiZhilevelyangxian.setEnabled(false);
					cbxShiZhilevelyangxian.setSelected(false);
					chckbxshizhilevellianxu.setEnabled(false);
					chckbxshizhilevellianxu.setSelected(false);
					tfldshizhilevelyangxian.setEnabled(false);
					
					tfldyangxian.setEnabled(false);
					tfldshizhilevellianxu.setEnabled(false);
					tfldwkyangxian.setEnabled(false);
				} else {
					cbxShiZhilevelyangxian.setEnabled(true);
					cbxShiZhilevelyangxian.setSelected(true);
					chckbxshizhilevellianxu.setEnabled(true);
					chckbxshizhilevellianxu.setSelected(true);
					tfldshizhilevelyangxian.setEnabled(true);
					tfldyangxian.setEnabled(true);
					tfldshizhilevellianxu.setEnabled(true);
					tfldwkyangxian.setEnabled(true);
					
				}

				
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
				if(cbxOnlyCurBk.isSelected()) {
					cbxShiZhilevelyangxian.setEnabled(false);
					cbxShiZhilevelyangxian.setSelected(false);
					chckbxshizhilevellianxu.setEnabled(false);
					chckbxshizhilevellianxu.setSelected(false);
					tfldshizhilevelyangxian.setEnabled(false);
					
					tfldyangxian.setEnabled(false);
					tfldshizhilevellianxu.setEnabled(false);
					tfldwkyangxian.setEnabled(false);
					

				} else {
					cbxShiZhilevelyangxian.setEnabled(true);
					cbxShiZhilevelyangxian.setSelected(true);
					chckbxshizhilevellianxu.setEnabled(true);
					chckbxshizhilevellianxu.setSelected(true);
					tfldshizhilevelyangxian.setEnabled(true);
					tfldyangxian.setEnabled(true);
					tfldshizhilevellianxu.setEnabled(true);
					tfldwkyangxian.setEnabled(true);
					
				}
				
				huchiOperationsForJCheckBox (huchixuanzebasic,cbxOnlyCurBk);
				huchiOperationsForJCheckBox (huchixuanzeupdate,cbxOnlyCurBk);
			}
		});
		
		chckbxexportyellowbkstk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(chckbxexportyellowbkstk.isSelected()) {
					cbxShiZhilevelyangxian.setEnabled(false);
					cbxShiZhilevelyangxian.setSelected(false);
					chckbxshizhilevellianxu.setEnabled(false);
					chckbxshizhilevellianxu.setSelected(false);
					tfldshizhilevelyangxian.setEnabled(false);
					
					tfldyangxian.setEnabled(false);
					tfldshizhilevellianxu.setEnabled(false);
					tfldwkyangxian.setEnabled(false);
					
					chkbxonlyexportbk.setEnabled(false);
					chkbxonlyexportbk.setSelected(false);
					
					chkbxonlybkstock.setEnabled(false);
					chkbxonlybkstock.setSelected(false);
					
					chkbxonlycurstock.setEnabled(false);
					chkbxonlycurstock.setSelected(false);
					
					chckbxexportyangxiangegu.setEnabled(false);
					chckbxexportyangxiangegu.setSelected(false);
					
					chckbxdpcjezbgrowingrate.setEnabled(false);
					chckbxdpcjezbgrowingrate.setSelected(false);
					
					tfldwkzhangfumin.setEnabled(false);

				} else {
					cbxShiZhilevelyangxian.setEnabled(true);
					cbxShiZhilevelyangxian.setSelected(true);
					chckbxshizhilevellianxu.setEnabled(true);
					chckbxshizhilevellianxu.setSelected(true);
					tfldshizhilevelyangxian.setEnabled(true);
					tfldyangxian.setEnabled(true);
					tfldshizhilevellianxu.setEnabled(true);
					tfldwkyangxian.setEnabled(true);
					chkbxonlyexportbk.setEnabled(true);
					chkbxonlycurstock.setEnabled(true);
					chkbxonlybkstock.setEnabled(true);
					chckbxexportyangxiangegu.setEnabled(true);
					tfldwkzhangfumin.setEnabled(true);
					chckbxdpcjezbgrowingrate.setEnabled(true);
				}
				
				huchiOperationsForJCheckBox (huchixuanzebasic,chckbxexportyellowbkstk);
				huchiOperationsForJCheckBox (huchixuanzeupdate,chckbxexportyellowbkstk);
			}
		});

		
	}
	/*
	 * 
	 */
	private void initializeGui() 
	{
		
		cbxShiZhilevelyangxian = new JCheckBox("\u4EA4\u6613\u989D\u5C0F\u4E8E\u8BBE\u5B9A\u503C");
		cbxShiZhilevelyangxian.setSelected(true);
		
		tfldshizhilevelyangxian = new JTextField();
		tfldshizhilevelyangxian.setText("4");
		tfldshizhilevelyangxian.setColumns(10);
		
		label = new JLabel("\u4EBF,\u5FC5\u987B\u6709");
		
		cbxOnlyCurBk = new JCheckBox("\u5BFC\u51FA\u6761\u4EF6\u4EC5\u9650\u5F53\u524D\u677F\u5757(\u677F\u5757\u8BBE\u7F6E\u65E0\u6548)");
		cbxOnlyCurBk.setEnabled(false);
		
		
		tfldyangxian = new JTextField();
		tfldyangxian.setText("5");
		tfldyangxian.setColumns(10);
		
		label_1 = new JLabel("%\u9633\u7EBF");
		
		chckbxshizhilevellianxu = new JCheckBox("\u6216\u4EA4\u6613\u989D\u5C0F\u4E8E\u8BBE\u5B9A\u503C");
		chckbxshizhilevellianxu.setSelected(true);
		
		tfldshizhilevellianxu = new JTextField();
		tfldshizhilevellianxu.setText("4");
		tfldshizhilevellianxu.setColumns(10);
		
		lblNewLabel = new JLabel("\u4EBF\uFF0C\u5FC5\u987B\u6709\u8FDE\u7EED");
		
		tfldwkyangxian = new JTextField();
		tfldwkyangxian.setText("2");
		tfldwkyangxian.setColumns(10);
		
		lblNewLabel_1 = new JLabel("\u5468\u653E\u91CF");
		
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
		
		chckbxexportyangxiangegu = new JCheckBox("\u4EC5\u5BFC\u51FA\u4E2A\u80A1\u5468\u7EBF\u6DA8\u5E45\u533A\u95F4");
		
		tfldwkzhangfumin = new JTextField();
		tfldwkzhangfumin.setText("0");
		tfldwkzhangfumin.setColumns(10);
		
		label_3 = new JLabel("%");
		cbxExceptSt = new JCheckBox("\u4E0D\u5BFC\u51FAST\u4E2A\u80A1");
		cbxExceptSt.setSelected(true);
		
		label_2 = new JLabel("-----------------------");
		
		chckbxexportyellowbkstk = new JCheckBox ("导出黄标板块和个股");
	}
}


