package com.exchangeinfomanager.gui.subgui;

import javax.swing.JPanel;
import javax.swing.JCheckBox;

import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

public class JaiRuZdyBanKuai extends JPanel {


	/**
	 * Create the panel.
	 */
	public JaiRuZdyBanKuai(Object[] zdybkname) 
	{
		comboBox = new JComboBox(zdybkname);
		
		initializeGui ();

	}
	
	private JComboBox comboBox;

	private void initializeGui() 
	{
		
		
		JLabel lblNewLabel = new JLabel("\u9009\u62E9\u677F\u5757");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE)
					.addGap(56))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(31)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel))
					.addContainerGap(38, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
		
	}
	
	public String getSelectedZdyBkName ()
	{
		return comboBox.getSelectedItem().toString();
	}
}
