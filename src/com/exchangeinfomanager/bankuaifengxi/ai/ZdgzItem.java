package com.exchangeinfomanager.bankuaifengxi.ai;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ZdgzItem extends JPanel 
{

	public ZdgzItem(String id) 
	{
		initializeGui ();
		this.tfldid.setText( id);
		
		createEvents ();
	}
	
	private void createEvents() 
	{
		checkBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{	if(checkBox.isSelected())
					selectedcolor = "GREEN";
			}
		});
		
		chbxselected.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(chbxselected.isSelected())
					selected = true;
			}
		});
		
	}

	public void initializeGui() 
	{
		tfldContents = new JTextField();
		tfldContents.setColumns(10);
		
		checkBox = new JCheckBox("\u5F53\u88AB\u9009\u62E9\u65F6\u5019\u663E\u793A\u7EFF\u8272");
		
		
		lblNewLabel = new JLabel("CheckList");
		
		tfldvalue = new JTextField();
		tfldvalue.setColumns(10);
		
		JLabel lblNeirong = new JLabel("Value");
		
		tfldid = new JTextField();
		tfldid.setColumns(10);
		
		JLabel lblId = new JLabel("id");
		
		chbxselected = new JCheckBox("\u4FDD\u6301\u9009\u4E2D");
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(checkBox)
							.addGap(18)
							.addComponent(chbxselected))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblNeirong)
								.addComponent(lblId, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(tfldvalue, Alignment.LEADING, 364, 364, 364)
								.addComponent(tfldContents, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 364, GroupLayout.PREFERRED_SIZE)
								.addComponent(tfldid, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(18, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(7)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(tfldid, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblId))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
						.addComponent(tfldContents, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(tfldvalue, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNeirong))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(checkBox)
						.addComponent(chbxselected))
					.addContainerGap())
		);
		setLayout(groupLayout);

		
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.tfldid.getText();
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.tfldid.setText( id );
	}
	/**
	 * @return the selectedcolor
	 */
	public String getSelectedcolor() {
		return selectedcolor;
	}
	/**
	 * @param selectedcolor the selectedcolor to set
	 */
	public void setSelectedcolor(String selectedcolor) {
		this.selectedcolor = selectedcolor;
	}
	/*
	 * 
	 */
	public Boolean isSelected ()
	{
		return selected;
	}
	public void setItemSelected(Boolean selected2) 
	{
		this.selected = selected2;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return tfldvalue.getText();
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.tfldvalue.setText( value );
	}
	/**
	 * @return the contents
	 */
	public String getContents() {
		return this.tfldContents.getText().trim();
	}
	/**
	 * @param contents the contents to set
	 */
	public void setContents(String contents) {
		this.tfldContents.setText( contents );
	}

	private String selectedcolor;
	private JTextField tfldContents;
	private JLabel lblNewLabel;
	private JTextField tfldvalue;
	private JCheckBox checkBox;
	private JTextField tfldid;
	private JCheckBox chbxselected;
	private Boolean selected;
	

}
