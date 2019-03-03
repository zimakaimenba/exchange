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

	/**
	 * Create the panel.
	 */
	public ExtraExportConditions() 
	{
		initializeGui ();
//		if(enablecjesetting) {
//			cbxShiZhilevelyangxian.setEnabled(true);
//			chckbxshizhilevellianxu.setEnabled(true);
//			tfldshizhilevelyangxian.setEnabled(true);
//			tfldyangxian.setEnabled(true);
//			tfldshizhilevellianxu.setEnabled(true);
//			tfldwkyangxian.setEnabled(true);
//		}
		
		createEvents ();
		
		
	}
	private void createEvents() 
	{
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
			}
		});
		
	}
	/*
	 * 
	 */
	private void initializeGui() 
	{
		cbxExceptSt = new JCheckBox("\u4E0D\u5BFC\u51FAST\u4E2A\u80A1");
		cbxExceptSt.setSelected(true);
		
		cbxShiZhilevelyangxian = new JCheckBox("\u4EA4\u6613\u989D\u5C0F\u4E8E\u8BBE\u5B9A\u503C");
		cbxShiZhilevelyangxian.setSelected(true);
		
		tfldshizhilevelyangxian = new JTextField();
		tfldshizhilevelyangxian.setText("4");
		tfldshizhilevelyangxian.setColumns(10);
		
		JLabel label = new JLabel("\u4EBF,\u5FC5\u987B\u6709");
		
		cbxOnlyCurBk = new JCheckBox("\u5BFC\u51FA\u6761\u4EF6\u4EC5\u9650\u5F53\u524D\u677F\u5757");
		
		
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
		setLayout(new MigLayout("", "[121px][10px][14px][6px][46px][54px][12px][18px][20px][28px][36px]", "[23px][23px][23px][23px]"));
		add(cbxExceptSt, "cell 0 3,alignx left,aligny top");
		add(chckbxshizhilevellianxu, "cell 0 2 3 1,alignx left,aligny top");
		add(tfldshizhilevellianxu, "cell 4 2,growx,aligny top");
		add(cbxShiZhilevelyangxian, "cell 0 1,alignx left,aligny top");
		add(tfldshizhilevelyangxian, "cell 2 1 3 1,alignx left,aligny center");
		add(label, "cell 5 1,alignx left,aligny center");
		add(tfldyangxian, "cell 7 1 3 1,alignx left,aligny center");
		add(label_1, "cell 10 1,alignx center,aligny center");
		add(lblNewLabel, "cell 5 2 3 1,alignx left,aligny center");
		add(tfldwkyangxian, "cell 8 2 2 1,growx,aligny top");
		add(lblNewLabel_1, "cell 10 2,alignx left,aligny top");
		add(cbxOnlyCurBk, "cell 0 0 3 1,alignx left,aligny top");

		
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
}
