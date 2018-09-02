package com.exchangeinfomanager.bankuaifengxi;

import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

class ExtraExportConditions extends JPanel 
{
	private JTextField tfldshizhilevel;
	private JCheckBox cbxShiZhilevel;
	private JCheckBox cbxExceptSt;
	private JCheckBox cbxOnlyCurBk;

	/**
	 * Create the panel.
	 */
	public ExtraExportConditions() {
		
		cbxExceptSt = new JCheckBox("\u4E0D\u5BFC\u51FAST\u4E2A\u80A1");
		cbxExceptSt.setSelected(true);
		
		cbxShiZhilevel = new JCheckBox("\u4EA4\u6613\u989D\u5C0F\u4E8E\u8BBE\u5B9A\u503C\u5FC5\u987B\u67095%\u9633\u7EBF\u6216\u8FDE\u7EED\u653E\u91CF");
		cbxShiZhilevel.setSelected(true);
		
		tfldshizhilevel = new JTextField();
		tfldshizhilevel.setText("5");
		tfldshizhilevel.setColumns(10);
		
		JLabel label = new JLabel("(\u4EBF)");
		
		cbxOnlyCurBk = new JCheckBox("\u5BFC\u51FA\u6761\u4EF6\u4EC5\u9650\u5F53\u524D\u677F\u5757");
		setLayout(new MigLayout("", "[253px][25px][24px]", "[23px][23px][23px]"));
		add(cbxExceptSt, "cell 0 0,alignx left,aligny top");
		add(cbxShiZhilevel, "cell 0 1,alignx left,aligny top");
		add(tfldshizhilevel, "cell 1 1,growx,aligny center");
		add(label, "cell 2 1,alignx left,aligny center");
		add(cbxOnlyCurBk, "cell 0 2,alignx left,aligny top");

	}
	public boolean shouldOnlyExportCurrentBankuai ()
	{
		if (cbxOnlyCurBk.isSelected())
			return true;
		else
			return false;
	}
	public boolean shouldExportSTStocks ()
	{
		if(cbxExceptSt.isSelected() )
			return true;
		else
			return false;
	}
	
	public double shouldHaveDaYangXianUnderCertainChenJiaoEr ()
	{
		if(cbxShiZhilevel.isSelected()) {
			return Double.parseDouble(tfldshizhilevel.getText() );
		} else
			return -1.0;
		
	}
}
