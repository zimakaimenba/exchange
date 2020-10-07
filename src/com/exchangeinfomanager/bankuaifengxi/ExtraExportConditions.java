package com.exchangeinfomanager.bankuaifengxi;

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
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(7)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(chkbxexportallbk)
								.addComponent(chkbxexporbkallowedinsetting)
								.addComponent(cbxOnlyCurBk)
								.addComponent(chkbxzhbiup)
								.addComponent(chkbxexportyangxianbk)
								.addComponent(chkbxonlyexportbk)
								.addComponent(lblNewLabel_2)
								.addComponent(chkbxonlybkstock)
								.addComponent(chkbxonlycurstock)
								.addComponent(cbxExceptSt)
								.addComponent(label_2)
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(chckbxexportyangxiangegu)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(tfldwkzhangfumin, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(label_3)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(label_5)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(tfldwkzhangfumax, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
										.addGroup(groupLayout.createSequentialGroup()
											.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
												.addGroup(groupLayout.createSequentialGroup()
													.addComponent(cbxShiZhilevelyangxian)
													.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
													.addComponent(tfldshizhilevelyangxian, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE))
												.addGroup(groupLayout.createSequentialGroup()
													.addComponent(chckbxshizhilevellianxu)
													.addPreferredGap(ComponentPlacement.RELATED)
													.addComponent(tfldshizhilevellianxu, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)))
											.addPreferredGap(ComponentPlacement.RELATED)
											.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
												.addComponent(lblNewLabel)
												.addComponent(label))))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
											.addGap(10)
											.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
												.addComponent(tfldwkyangxian, 0, 0, Short.MAX_VALUE)
												.addComponent(tfldyangxian, GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
											.addGap(18)
											.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
												.addComponent(label_1)
												.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)))
										.addComponent(label_4)))))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblNewLabel_3))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(chbxlwyxbenzhouyangxian))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(chckbxexportyellowbkstk)))
					.addContainerGap(136, Short.MAX_VALUE))
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
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(chckbxexportyangxiangegu)
						.addComponent(tfldwkzhangfumin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_3)
						.addComponent(label_5)
						.addComponent(tfldwkzhangfumax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_4))
					.addGap(4)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(cbxShiZhilevelyangxian)
							.addComponent(tfldshizhilevelyangxian, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(label)
							.addComponent(tfldyangxian, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(4)
							.addComponent(label_1)))
					.addGap(4)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_1)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(chckbxshizhilevellianxu)
								.addComponent(tfldshizhilevellianxu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewLabel)
								.addComponent(tfldwkyangxian, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(4)
							.addComponent(cbxExceptSt)
							.addGap(4)
							.addComponent(label_2)
							.addGap(6)
							.addComponent(chbxlwyxbenzhouyangxian)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNewLabel_3)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxexportyellowbkstk)
					.addGap(24))
		);
		setLayout(groupLayout);
		
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


