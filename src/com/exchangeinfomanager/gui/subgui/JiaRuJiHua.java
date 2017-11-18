package com.exchangeinfomanager.gui.subgui;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import java.time.LocalDate;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import net.miginfocom.swing.MigLayout;

public class JiaRuJiHua extends JPanel 
{
	
	public JiaRuJiHua(String stockcode,String guanzhutype) 
	{
		this.stockcode = stockcode;
		this.guanzhutype = guanzhutype;
		initializeGui ();
		createEvents ();
	}
	
	private String stockcode;
	private JTextField tfdJihuaJiage;
	private JTextField tfdShuoming;
	private JComboBox<?> cbxJihuaLeixing;
	private JCheckBox jbxMingRiJIhua;
	private String guanzhutype;
	private LocalDate jrdate;
	
	public String getGuanZhuType ()
	{
		return this.guanzhutype;
	}
	public String getStockCode ()
	{
		return this.stockcode;
	}
	public boolean isMingRiJiHua ()
	{
		return jbxMingRiJIhua.isSelected();
	}
	public String getJiHuaLeiXing ()
	{
		if(jbxMingRiJIhua.isSelected())
			return (String)cbxJihuaLeixing.getSelectedItem();
		else return null;
	}
	@SuppressWarnings("null")
	public String getJiHuaJiaGe ()
	{
		if(jbxMingRiJIhua.isSelected())
			//return  Double.parseDouble(tfdJihuaJiage.getText());
			return  tfdJihuaJiage.getText();
		else return  null;
	}
	public String getJiHuaShuoMing ()
	{
		return  tfdShuoming.getText();
	}
	public void setiHuaShuoMing (String shuoming)
	{
		this.tfdShuoming.setText(shuoming); 
	}
	public void setJiaRuDate (LocalDate date)
	{
		this.jrdate = date;
	}
	public LocalDate getJiaRuDate ()
	{
		return this.jrdate;
	}
	
	
	private void createEvents ()
	{
		jbxMingRiJIhua.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) 
			{
				if(true == jbxMingRiJIhua.isSelected()) {
					tfdJihuaJiage.setEnabled(true);
					//tfdShuoming.setEnabled(true);
					cbxJihuaLeixing.setEnabled(true);
				} else {
					tfdJihuaJiage.setEnabled(false);
					//tfdShuoming.setEnabled(true);
					cbxJihuaLeixing.setEnabled(false);
				}
					
			}
		});
	}

	/**
	 * Create the panel.
	 */

	private void initializeGui() 
	{
		JLabel label = new JLabel("\u8BF4\u660E");
		
		tfdShuoming = new JTextField();
		tfdShuoming.setColumns(10);
		
		jbxMingRiJIhua = new JCheckBox("\u660E\u65E5\u8BA1\u5212");
		
		cbxJihuaLeixing = new JComboBox();
		cbxJihuaLeixing.setEnabled(false);
		cbxJihuaLeixing.setModel(new DefaultComboBoxModel(new String[] {"\u666E\u901A\u4E70\u5165", "\u666E\u901A\u5356\u51FA", "\u4FE1\u7528\u666E\u901A\u4E70\u5165", "\u4FE1\u7528\u666E\u901A\u5356\u51FA", "\u878D\u8D44\u4E70\u5165", "\u5356\u51FA\u507F\u8FD8\u878D\u8D44", "\u878D\u5238\u5356\u51FA", "\u4E70\u5165\u6362\u5238"}));
		
		JLabel lblNewLabel = new JLabel("\u4EF7\u683C");
		
		
		tfdJihuaJiage = new JTextField();
		tfdJihuaJiage.setEnabled(false);
		tfdJihuaJiage.setColumns(10);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(cbxJihuaLeixing, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(17)
							.addComponent(lblNewLabel)
							.addGap(18)
							.addComponent(tfdJihuaJiage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(label)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addComponent(jbxMingRiJIhua, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
							.addComponent(tfdShuoming)))
					.addContainerGap(29, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(label)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tfdShuoming, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(jbxMingRiJIhua)
					.addGap(3)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(cbxJihuaLeixing, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblNewLabel))
						.addComponent(tfdJihuaJiage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(16))
		);
		setLayout(groupLayout);
	}
}
