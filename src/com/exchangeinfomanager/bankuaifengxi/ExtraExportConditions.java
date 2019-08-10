package com.exchangeinfomanager.bankuaifengxi;

import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

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
	private JCheckBox chkbxonlystock;
	private JCheckBox chkbxzhbiup;
	private JLabel lblNewLabel_2;
	private JLabel label_2;
	private JCheckBox chckbxexportyangxiangegu;
	private JCheckBox chkbxexporbkallowedinsetting;
	private JTextField tfldwkyingxiandayu;
	private JLabel label_3;

	private List<JCheckBox> huchixuanze; //互斥意味着其中一个选了，其他都不惜不选
	/**
	 * Create the panel.
	 */
	public ExtraExportConditions() 
	{
		initializeGui ();
		
		huchixuanze = new ArrayList<JCheckBox> ();
		huchixuanze.add(cbxOnlyCurBk);
		huchixuanze.add(chkbxexportallbk);
		huchixuanze.add(chkbxzhbiup);
		huchixuanze.add(chkbxexporbkallowedinsetting);
		
		createEvents ();
		
		
	}
	/*
	 * 
	 */
	private void huchiOperationsForJCheckBox (JCheckBox selectedjchb)
	{
		for(JCheckBox tmpjchb : huchixuanze) {
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
		chkbxzhbiup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				huchiOperationsForJCheckBox (chkbxzhbiup);
			}
		});

		
		chkbxexportallbk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

				huchiOperationsForJCheckBox (chkbxexportallbk);
			}
				
		});
		chkbxexporbkallowedinsetting.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				huchiOperationsForJCheckBox (chkbxexporbkallowedinsetting);
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
				
				huchiOperationsForJCheckBox (cbxOnlyCurBk);
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
		
		JLabel label = new JLabel("\u4EBF,\u5FC5\u987B\u6709");
		
		cbxOnlyCurBk = new JCheckBox("\u5BFC\u51FA\u6761\u4EF6\u4EC5\u9650\u5F53\u524D\u677F\u5757");
		cbxOnlyCurBk.setEnabled(false);
		
		
		tfldyangxian = new JTextField();
		tfldyangxian.setText("5");
		tfldyangxian.setColumns(10);
		
		JLabel label_1 = new JLabel("%\u9633\u7EBF");
		
		chckbxshizhilevellianxu = new JCheckBox("\u6216\u4EA4\u6613\u989D\u5C0F\u4E8E\u8BBE\u5B9A\u503C");
		chckbxshizhilevellianxu.setSelected(true);
		
		tfldshizhilevellianxu = new JTextField();
		tfldshizhilevellianxu.setText("4");
		tfldshizhilevellianxu.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("\u4EBF\uFF0C\u5FC5\u987B\u6709\u8FDE\u7EED");
		
		tfldwkyangxian = new JTextField();
		tfldwkyangxian.setText("2");
		tfldwkyangxian.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("\u5468\u653E\u91CF");
		
		chkbxexportallbk = new JCheckBox("\u5BFC\u51FA\u5168\u90E8\u677F\u5757(\u677F\u5757\u5BFC\u51FA\u8BBE\u7F6E\u65E0\u6548)");
		chkbxexportallbk.setSelected(true);
		
		chkbxzhbiup = new JCheckBox("\u4EC5\u5BFC\u51FA\u6210\u4EA4\u989D/\u6210\u4EA4\u91CF\u5360\u6BD4\u73AF\u6BD4\u4E0A\u5347\u7684\u677F\u5757(\u677F\u5757\u8BBE\u7F6E\u65E0\u6548)");
		
		chkbxzhbiup.setEnabled(false);
		
		chkbxonlystock = new JCheckBox("\u4EC5\u5BFC\u51FA\u677F\u5757\u7684\u4E2A\u80A1\uFF0C\u4E0D\u5BFC\u51FA\u677F\u5757");
		setLayout(new MigLayout("", "[205px,grow][48px,grow][17px][8px][54px][20px][10px][8px][26px][36px]", "[23px][][23px][23px][23px][][][23px][23px][][][][23px]"));
		add(chkbxexportallbk, "cell 0 0,alignx left,aligny top");
		
		chkbxexporbkallowedinsetting = new JCheckBox("\u5BFC\u51FA\u677F\u5757\u8BBE\u7F6E\u5141\u8BB8\u7684\u677F\u5757");
		chkbxexporbkallowedinsetting.setEnabled(false);
		add(chkbxexporbkallowedinsetting, "cell 0 1");
		add(cbxOnlyCurBk, "cell 0 2,alignx left,aligny top");
		add(chkbxzhbiup, "cell 0 3 5 1,alignx left,aligny top");
		add(chkbxonlystock, "cell 0 4,alignx left,aligny top");
		
		lblNewLabel_2 = new JLabel("-----------------------");
		add(lblNewLabel_2, "cell 0 5");
		
		chckbxexportyangxiangegu = new JCheckBox("\u4EC5\u5BFC\u51FA\u4E2A\u80A1\u5468\u7EBF\u9633\u7EBF\u6216\u5468\u7EBF\u9634\u7EBF\u4E0D\u5927\u4E8E");
		add(chckbxexportyangxiangegu, "cell 0 6");
		
		tfldwkyingxiandayu = new JTextField();
		tfldwkyingxiandayu.setText("-1.9");
		add(tfldwkyingxiandayu, "cell 1 6,growx");
		tfldwkyingxiandayu.setColumns(10);
		
		label_3 = new JLabel("%");
		add(label_3, "cell 4 6");
		add(cbxShiZhilevelyangxian, "cell 0 7,alignx left,aligny top");
		add(tfldshizhilevelyangxian, "cell 1 7 2 1,growx,aligny center");
		add(label, "cell 4 7,alignx left,aligny center");
		add(tfldyangxian, "cell 6 7 3 1,growx,aligny center");
		add(label_1, "cell 9 7,alignx center,aligny center");
		add(chckbxshizhilevellianxu, "cell 0 8,alignx left,aligny top");
		add(tfldshizhilevellianxu, "cell 1 8 2 1,growx,aligny top");
		add(lblNewLabel, "cell 4 8 3 1,alignx left,aligny center");
		add(tfldwkyangxian, "cell 8 8,growx,aligny top");
		add(lblNewLabel_1, "cell 9 8,alignx left,aligny top");
		cbxExceptSt = new JCheckBox("\u4E0D\u5BFC\u51FAST\u4E2A\u80A1");
		cbxExceptSt.setSelected(true);
		add(cbxExceptSt, "cell 0 9,alignx left,aligny top");
		
		label_2 = new JLabel("-----------------------");
		add(label_2, "flowy,cell 0 10");
	}
	/*
	 * 
	 */
	public Double shouldOnlyExportStocksWithWkYangXian ()
	{
		if(chckbxexportyangxiangegu.isSelected()) {
			return Double.parseDouble( tfldwkyingxiandayu.getText() ) / 100;
		}
		else 
			return null;
	}
	
	/*
	 * 
	 */
	public boolean shouldOnlyExportStocksNotBanKuai ()
	{
		if( chkbxonlystock.isSelected() )
			return true;
		else
			return false;
	}
	/*
	 * 
	 */
	public boolean shouldOnlyExportCurrentBankuai ()
	{
		if (cbxOnlyCurBk.isSelected())
			return true;
		else
			return false;
	}
	/*
	 * 
	 */
	public boolean shouldExportSTStocks ()
	{
		if(cbxExceptSt.isSelected() )
			return true;
		else
			return false;
	}
	public boolean shouldExportAllBanKuai ()
	{
		if( chkbxexportallbk.isSelected() )
			return true;
		else
			return false;
	}
	public Boolean shouldOnlyExportBanKuaiOfZhanBiUp ()
	{
		if(chkbxzhbiup.isSelected())
			return true;
		else
			return false;
	}
	/*
	 * 
	 */
	public Boolean shouldHaveDaYangXianUnderCertainChenJiaoEr ()
	{
		if(cbxShiZhilevelyangxian.isSelected()) {
			return true;
		} else
			return false;
		
	}
	public Double getCjeLevelUnderCertainChenJiaoErOfDaYangXian ()
	{
		if(cbxShiZhilevelyangxian.isSelected()) {
			return Double.parseDouble(tfldshizhilevelyangxian.getText() );
		} else
			return -1.0;
	}
	public Double getYangXianLevelUnderCertainChenJiaoErofDaYangXian ()
	{
		if(cbxShiZhilevelyangxian.isSelected()) {
			return Double.parseDouble(tfldyangxian.getText());
		} else
			return 0.0;
		
	}
	/*
	 * 
	 */
	public Boolean shouldHaveFangLiangUnderCertainChenJiaoEr ()
	{
		if(chckbxshizhilevellianxu.isSelected())
			return true;
		else
			return false;
				
	}
	public Double getCjeLevelUnderCertainChenJiaoErOfLianXuFangLiang ()
	{
		if(chckbxshizhilevellianxu.isSelected()) {
			return Double.parseDouble(tfldshizhilevellianxu.getText() );
		} else
			return -1.0;
		
	}
	public Integer getLianXuFangLianLevelUnderCertainChenJiaoErOfFangLiang ()
	{
		if(chckbxshizhilevellianxu.isSelected()) 
			return Integer.parseInt(tfldwkyangxian.getText());
		else
			return 0;
		
	}
	public Boolean shouldOnlyExportBanKuaiAllowedInSetting ()
	{
		if(this.chkbxexporbkallowedinsetting.isSelected() )
			return true;
		else
			return false;
	}
}
