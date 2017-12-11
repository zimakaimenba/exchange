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
import java.time.ZoneId;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import com.toedter.calendar.JDateChooser;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

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
	private JComboBox<?> cbxJihuaLeixing;
	private JCheckBox jbxMingRiJIhua;
	private String guanzhutype;
//	private LocalDate jrdate;
	private JScrollPane scrollPane;
	private JTextArea tfdShuoming;
	private JDateChooser dateChooser;
	
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
		dateChooser.setDate( Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()) );
	}
	public LocalDate getJiaRuDate ()
	{
		return dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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
		
		dateChooser = new JDateChooser();
		dateChooser.setDate(new Date ());
		
		scrollPane = new JScrollPane();
		
		tfdShuoming = new JTextArea();
		tfdShuoming.setLineWrap(true);
		scrollPane.setViewportView(tfdShuoming);
		
		jbxMingRiJIhua = new JCheckBox("\u660E\u65E5\u8BA1\u5212");
		
		cbxJihuaLeixing = new JComboBox();
		cbxJihuaLeixing.setEnabled(false);
		cbxJihuaLeixing.setModel(new DefaultComboBoxModel(new String[] {"\u666E\u901A\u4E70\u5165", "\u666E\u901A\u5356\u51FA", "\u4FE1\u7528\u666E\u901A\u4E70\u5165", "\u4FE1\u7528\u666E\u901A\u5356\u51FA", "\u878D\u8D44\u4E70\u5165", "\u5356\u51FA\u507F\u8FD8\u878D\u8D44", "\u878D\u5238\u5356\u51FA", "\u4E70\u5165\u6362\u5238"}));
		
		JLabel lblNewLabel = new JLabel("\u4EF7\u683C");
		
		
		tfdJihuaJiage = new JTextField();
		tfdJihuaJiage.setEnabled(false);
		tfdJihuaJiage.setColumns(10);
		
		JCheckBox checkBox = new JCheckBox("\u653E\u91CF");
		
		JCheckBox checkBox_1 = new JCheckBox("\u7F29\u91CF");
		
		JCheckBox checkBox_2 = new JCheckBox("\u7A7A\u5934");
		
		JCheckBox checkBox_3 = new JCheckBox("\u591A\u5934");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(7)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(label)
							.addGap(86)
							.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE))))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(cbxJihuaLeixing, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tfdJihuaJiage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(15, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(jbxMingRiJIhua, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(115, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(checkBox)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(checkBox_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(checkBox_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(checkBox_3)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(7)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(label)
						.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(4)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(checkBox)
						.addComponent(checkBox_1)
						.addComponent(checkBox_2)
						.addComponent(checkBox_3))
					.addGap(41)
					.addComponent(jbxMingRiJIhua)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(cbxJihuaLeixing, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel)
						.addComponent(tfdJihuaJiage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(111, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
}
