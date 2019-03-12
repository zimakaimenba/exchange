package com.exchangeinfomanager.gui.subgui;

import javax.swing.JPanel;
import javax.swing.JLabel;

import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JList;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

import javax.swing.JScrollPane;

public class SelectMultiNode extends JPanel {
	private JList list;

	/**
	 * Create the panel.
	 */
	public SelectMultiNode(ArrayList<BkChanYeLianTreeNode> multinode) 
	{
		
		JLabel lblNewLabel = new JLabel("\u8BF7\u9009\u62E9:");
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(25)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel))
					.addContainerGap(42, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(19)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 156, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(100, Short.MAX_VALUE))
		);
		
		DefaultListModel  listModel = new DefaultListModel();
		list = new JList(listModel);
		
		for(BkChanYeLianTreeNode tmpnode : multinode) {
			String nodecode = tmpnode.getMyOwnCode();
			String nodename = tmpnode.getMyOwnName();

			listModel.addElement(nodecode+nodename);
		}
		scrollPane.setViewportView(list);
		setLayout(groupLayout);

	}
	
	public int getUserSelection ()
	{
		int index = list.getSelectedIndex();
		return index;
	}
}
