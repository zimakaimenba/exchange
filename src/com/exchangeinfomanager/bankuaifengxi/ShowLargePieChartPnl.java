package com.exchangeinfomanager.bankuaifengxi;

import javax.swing.JPanel;
import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ShowLargePieChartPnl extends JPanel 
{

	/**
	 * Create the panel.
	 */
	public ShowLargePieChartPnl(String picpath) 
	{
		
		ImageIcon image = new ImageIcon(picpath);
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 1031, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(20)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 789, Short.MAX_VALUE)
					.addContainerGap())
		);
		JLabel lblShowPic = new JLabel("", image, JLabel.CENTER);
		scrollPane.setViewportView(lblShowPic);
		
		
//		scrollPane.setViewportView(lblShowPic);
		setLayout(groupLayout);

	}
}
