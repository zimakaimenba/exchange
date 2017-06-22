package com.exchangeinfomanager.bankuai.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ZdyBanKuaiSheZhi extends JPanel {

	/**
	 * Create the panel.
	 */
	public ZdyBanKuaiSheZhi() 
	{
		initializeGui ();
	}
	
	private JTextField tfdMingCheng;
	private JTextField tfdDaiMa;

	public String getBanKuaiName ()
	{
		return tfdMingCheng.getText();
	}
	public String getBanKuaiCode ()
	{
		return tfdDaiMa.getText();
	}

	private void initializeGui() 
	{
		JLabel lblNewLabel = new JLabel("\u677F\u5757\u540D\u79F0");
		
		JLabel lblNewLabel_1 = new JLabel("\u677F\u5757\u4EE3\u7801");
		
		tfdMingCheng = new JTextField();
		tfdMingCheng.setColumns(10);
		
		tfdDaiMa = new JTextField();
		tfdDaiMa.setColumns(10);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(29)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel_1)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(tfdDaiMa))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(tfdMingCheng, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(51, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(33)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(tfdMingCheng, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(27)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(tfdDaiMa, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(210, Short.MAX_VALUE))
		);
		setLayout(groupLayout);

		
	}
}
