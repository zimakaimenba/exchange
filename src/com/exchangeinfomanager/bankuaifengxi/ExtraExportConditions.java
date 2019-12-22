package com.exchangeinfomanager.bankuaifengxi;

import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.exchangeinfomanager.commonlib.ReminderPopToolTip;

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
	private JTextField tfldwkyingxiandayu;
	private JLabel label_3;

	private List<JCheckBox> huchixuanzebasic; //互斥意味着其中一个选了，其他都不惜不选
	private List<JCheckBox> huchixuanzeupdate; //有两个是不互斥的，和其他互斥
	private List<JCheckBox> huchixuanzeaboutstock;
	
	private JCheckBox chkbxexportyangxianbk;
	private JCheckBox chkbxonlyexportbk;
	private JCheckBox chkbxonlycurstock;
	private BanKuaiGeGuMatchCondition cond;
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
		
		if( chckbxexportyangxiangegu.isSelected() )
			this.cond.setExportYangXianGeGu( Double.parseDouble(tfldwkyingxiandayu.getText() ) / 100 );
		else
			this.cond.setExportYangXianGeGu( null );
		
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
					
					tfldwkyingxiandayu.setEnabled(false);
					
				} else {
//					chkbxexportyangxianbk.setEnabled(true);
					
					cbxExceptSt.setEnabled(true);
					cbxExceptSt.setSelected(true);
					
					chckbxexportyangxiangegu.setEnabled(true);
					tfldwkyingxiandayu.setEnabled(true);
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
		
		cbxOnlyCurBk = new JCheckBox("\u5BFC\u51FA\u6761\u4EF6\u4EC5\u9650\u5F53\u524D\u677F\u5757(\u677F\u5757\u8BBE\u7F6E\u65E0\u6548)");
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
		setLayout(new MigLayout("", "[205px,grow][48px,grow][17px][8px][54px][20px][10px][8px][26px][36px]", "[23px][][23px][23px][][][][23px][][][][][23px][23px][][][][23px]"));
		add(chkbxexportallbk, "cell 0 0,alignx left,aligny top");
		
		chkbxexporbkallowedinsetting = new JCheckBox("\u5BFC\u51FA\u677F\u5757\u8BBE\u7F6E\u5141\u8BB8\u7684\u677F\u5757");
		chkbxexporbkallowedinsetting.setEnabled(false);
		add(chkbxexporbkallowedinsetting, "cell 0 1");
		add(cbxOnlyCurBk, "cell 0 2,alignx left,aligny top");
		add(chkbxzhbiup, "cell 0 3 5 1,alignx left,aligny top");
		
		chkbxexportyangxianbk = new JCheckBox("\u4EC5\u5BFC\u51FA\u5468\u7EBF\u9633\u7EBF\u7684\u677F\u5757(\u677F\u5757\u8BBE\u7F6E\u65E0\u6548)");
		chkbxexportyangxianbk.setEnabled(false);
		add(chkbxexportyangxianbk, "cell 0 4");
		
		chkbxonlyexportbk = new JCheckBox("\u4EC5\u5BFC\u51FA\u677F\u5757\uFF0C\u4E0D\u5BFC\u51FA\u677F\u5757\u4E2A\u80A1");
		add(chkbxonlyexportbk, "cell 0 5");
		
		lblNewLabel_2 = new JLabel("-----------------------");
		add(lblNewLabel_2, "cell 0 8");
		
		chkbxonlybkstock = new JCheckBox("\u4EC5\u5BFC\u51FA\u677F\u5757\u7684\u4E2A\u80A1\uFF0C\u4E0D\u5BFC\u51FA\u677F\u5757");
		add(chkbxonlybkstock, "cell 0 9,alignx left,aligny top");
		
		chkbxonlycurstock = new JCheckBox("\u4EC5\u5BFC\u51FA\u5F53\u524D\u4E2A\u80A1");
		add(chkbxonlycurstock, "cell 0 10");
		
		chckbxexportyangxiangegu = new JCheckBox("\u4EC5\u5BFC\u51FA\u4E2A\u80A1\u5468\u7EBF\u9633\u7EBF\u6216\u5468\u7EBF\u9634\u7EBF>=");
		add(chckbxexportyangxiangegu, "cell 0 11");
		
		tfldwkyingxiandayu = new JTextField();
		tfldwkyingxiandayu.setText("-1.9");
		add(tfldwkyingxiandayu, "cell 1 11,growx");
		tfldwkyingxiandayu.setColumns(10);
		
		label_3 = new JLabel("%");
		add(label_3, "cell 4 11");
		add(cbxShiZhilevelyangxian, "cell 0 12,alignx left,aligny top");
		add(tfldshizhilevelyangxian, "cell 1 12 2 1,growx,aligny center");
		add(label, "cell 4 12,alignx left,aligny center");
		add(tfldyangxian, "cell 6 12 3 1,growx,aligny center");
		add(label_1, "cell 9 12,alignx center,aligny center");
		add(chckbxshizhilevellianxu, "cell 0 13,alignx left,aligny top");
		add(tfldshizhilevellianxu, "cell 1 13 2 1,growx,aligny top");
		add(lblNewLabel, "cell 4 13 3 1,alignx left,aligny center");
		add(tfldwkyangxian, "cell 8 13,growx,aligny top");
		add(lblNewLabel_1, "cell 9 13,alignx left,aligny top");
		cbxExceptSt = new JCheckBox("\u4E0D\u5BFC\u51FAST\u4E2A\u80A1");
		cbxExceptSt.setSelected(true);
		add(cbxExceptSt, "cell 0 14,alignx left,aligny top");
		
		label_2 = new JLabel("-----------------------");
		add(label_2, "flowy,cell 0 15");
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
	public boolean shouldOnlyExportBanKuaiWithYangXian()
	{
		if(chkbxexportyangxianbk.isSelected() )
			return true;
		else
			return false;
	}
	
	/*
	 * 
	 */
	public boolean shouldOnlyExportStocksNotBanKuai ()
	{
		if( chkbxonlybkstock.isSelected() )
			return true;
		else
			return false;
	}
	/*
	 * 
	 */
	public boolean shouldOnlyExportCurrentStockNotAllStocks ()
	{
		if(chkbxonlycurstock.isSelected())
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
	public Boolean shouldOnlyExportBanKuaisNotStock ()
	{
		if(this.chkbxonlyexportbk.isSelected())
			return true;
		else
			return false;
	}
	
	
//	public static void main(String[] args) {
//		try {
//			ExtraExportConditions dialog = new ExtraExportConditions();
////			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			
//			 JDialog  testlog = new  JDialog  ();
//			 testlog.setTitle("About");
//			 testlog.setSize(620, 400);
//			 testlog.setVisible(true);
//
//			 testlog.getContentPane().add(dialog, BorderLayout.CENTER);
//			
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}


