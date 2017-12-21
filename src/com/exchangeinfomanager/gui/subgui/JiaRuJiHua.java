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

import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian;
import com.toedter.calendar.JDateChooser;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;
import javax.swing.JTable;

public class JiaRuJiHua extends JPanel 
{
	public JiaRuJiHua(String stockcode,String guanzhutype) 
	{
		this.stockcode = stockcode;
		this.guanzhutype = guanzhutype;
		initializeGui ();
		createEvents ();
	}
	
//	public JiaRuJiHua(String stockcode,String guanzhutype,BanKuaiAndChanYeLian bkcyl2) 
//	{
//		this.stockcode = stockcode;
//		this.guanzhutype = guanzhutype;
//		this.bkcyl = bkcyl2;
//		initializeGui ();
//		initializeTable ();
//		createEvents ();
//	}
	private Integer dbrecordsid;
	private String stockcode;
	private JTextField tfdJihuaJiage;
	private JComboBox<?> cbxJihuaLeixing;
	private JCheckBox jbxMingRiJIhua;
	private String guanzhutype;
//	private LocalDate jrdate;
	private JScrollPane scrollPane;
	private JTextArea tfdShuoming;
	private JDateChooser dateChooser;
	private JCheckBox checkBox;
	private JCheckBox checkBox_1;
	private JCheckBox chckbxmacd;
	private JCheckBox checkBox_2;
	private JCheckBox chckbxma;
//	private BanKuaiAndChanYeLian bkcyl;
	
	public void setDbRecordsId (Integer id)
	{
		this.dbrecordsid = id;
	}
	public Integer getDbRecordsId ()
	{
		return this.dbrecordsid;
	}
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
		
		checkBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(checkBox.isSelected()) 
					updatedRecords (checkBox);
			}
		});
		checkBox_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_2.isSelected()) 
					updatedRecords (checkBox_2);
			}
		});
		chckbxmacd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxmacd.isSelected()) 
					updatedRecords (chckbxmacd);
			}
		});
		checkBox_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_1.isSelected()) 
					updatedRecords (checkBox_1);
			}
		});
		chckbxma.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxma.isSelected()) 
					updatedRecords (chckbxma);
			}
		});
		
	}
	
	private void updatedRecords (JCheckBox box)
	{
		String boxtext = box.getText();
		String cutext = tfdShuoming.getText();
		
		tfdShuoming.setText(cutext + boxtext);
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
		
		checkBox = new JCheckBox("\u653E\u91CF\u6EE1\u8DB3\u6A21\u578B");
		
		
		checkBox_2 = new JCheckBox("\u7A7A\u5934\u6392\u5217");
		
		
		chckbxma = new JCheckBox("\u90E8\u5206\u591A\u5934\u51FA\u73B0\uFF0CMA20\u62D0\u5934");
		
		
		checkBox_1 = new JCheckBox("\u5927\u6DA8\u540E\u53F3\u4FA7\u4E0B\u5C71\u8D70\u52BF\uFF0C\u53CD\u5F39\u8D8A\u6765\u8D8A\u5F31");
		
		
		chckbxmacd = new JCheckBox("\u4E0B\u8DCC\u7F29\u91CF\uFF0C\u5206\u65F6MACD\u80CC\u79BB");
		
		
		JSeparator separator = new JSeparator();
		
		JSeparator separator_1 = new JSeparator();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(7)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 248, GroupLayout.PREFERRED_SIZE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(label)
									.addGap(86)
									.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE))))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(checkBox_1))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(chckbxmacd))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(checkBox_2))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(cbxJihuaLeixing, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(tfdJihuaJiage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(jbxMingRiJIhua, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(checkBox)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(chckbxma)))))
					.addContainerGap(7, Short.MAX_VALUE))
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
						.addComponent(chckbxma))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(checkBox_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxmacd)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(checkBox_2)
					.addGap(20)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 3, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(jbxMingRiJIhua)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(cbxJihuaLeixing, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel)
						.addComponent(tfdJihuaJiage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(24, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
}
