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
	private JCheckBox checkBox_5;
	private JCheckBox checkBox_6;
	private JCheckBox checkBox_4;
	private JCheckBox checkBox_7;
	private JCheckBox checkBox_8;
	private JCheckBox checkBox_9;
	private JCheckBox checkBox_10;
	private JCheckBox checkBox_11;
	private JCheckBox checkBox_3;
	private JCheckBox checkBox_12;
	private JCheckBox checkBox_13;
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
		
		checkBox = new JCheckBox("\u6EE1\u8DB3\u653E\u91CF\u5747\u7EBF\u6A21\u578B");
		
		
		checkBox_2 = new JCheckBox("\u8D85\u8DCC\u53CD\u5F39");
		
		
		chckbxma = new JCheckBox("\u91CD\u7EC4\u6536\u8D2D");
		
		
		checkBox_1 = new JCheckBox("\u5927\u6DA8\u540E\u53F3\u4FA7\u4E0B\u5C71\u8D70\u52BF\uFF0C\u53CD\u5F39\u8D8A\u6765\u8D8A\u5F31");
		
		
		chckbxmacd = new JCheckBox("\u4E0B\u8DCC\u7F29\u91CF\uFF0C\u5206\u65F6MACD\u80CC\u79BB");
		
		
		JSeparator separator = new JSeparator();
		
		JSeparator separator_1 = new JSeparator();
		
		checkBox_3 = new JCheckBox("\u7A33\u5B9A\u767D\u9A6C");
		checkBox_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_3.isSelected()) 
					updatedRecords (checkBox_3);
			}
		});
		
		checkBox_4 = new JCheckBox("\u9ED1\u5929\u9E45\u6536\u76CA");
		checkBox_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_4.isSelected()) 
					updatedRecords (checkBox_4);
			}
		});
		
		checkBox_5 = new JCheckBox("\u524D\u70ED\u70B9\u4F59\u70ED");
		checkBox_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(checkBox_5.isSelected()) 
					updatedRecords (checkBox_5);
			}
		});
		
		checkBox_6 = new JCheckBox("\u6DA8\u4EF7\u548C\u4E1A\u7EE9");
		checkBox_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_6.isSelected()) 
					updatedRecords (checkBox_6);
			}
		});
		
		checkBox_7 = new JCheckBox("\u6B21\u65B0\u80A1");
		checkBox_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_7.isSelected()) 
					updatedRecords (checkBox_7);
			}
		});
		
		checkBox_8 = new JCheckBox("\u6E2F\u7F8E\u8054\u52A8");
		checkBox_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_8.isSelected()) 
					updatedRecords (checkBox_8);
			}
		});
		
		checkBox_9 = new JCheckBox("\u5238\u5546\u53C2\u5238");
		checkBox_9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_9.isSelected()) 
					updatedRecords (checkBox_9);
			}
		});
		
		checkBox_10 = new JCheckBox("\u9898\u6750\u8F6C\u4E1A\u7EE9");
		checkBox_10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_10.isSelected()) 
					updatedRecords (checkBox_10);
			}
		});
		
		checkBox_11 = new JCheckBox("\u6210\u957F\u548C\u6982\u5FF5");
		checkBox_11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_11.isSelected()) 
					updatedRecords (checkBox_11);
			}
		});
		
		checkBox_12 = new JCheckBox("\u7279\u5B9A\u65F6\u95F4\u91CD\u5927\u4E8B\u4EF6");
		checkBox_12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_12.isSelected()) 
					updatedRecords (checkBox_12);
			}
		});
		
		checkBox_13 = new JCheckBox("\u56FD\u65B0\u653F\u7B56");
		checkBox_13.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_13.isSelected()) 
					updatedRecords (checkBox_13);
			}
		});
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
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(checkBox_2)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(checkBox_5)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(checkBox_6))
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(checkBox_9)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(checkBox_10)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(checkBox_11)))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(checkBox_4)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(checkBox_7)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(checkBox_8))))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(checkBox))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(jbxMingRiJIhua, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(checkBox_12)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(checkBox_13))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(cbxJihuaLeixing, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfdJihuaJiage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(chckbxma)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(checkBox_3)))
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
					.addComponent(checkBox)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(checkBox_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxmacd)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(checkBox_2)
						.addComponent(checkBox_5)
						.addComponent(checkBox_6))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(checkBox_4)
						.addComponent(checkBox_7)
						.addComponent(checkBox_8))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(checkBox_9)
							.addComponent(checkBox_10)
							.addComponent(checkBox_11)))
					.addPreferredGap(ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(chckbxma)
						.addComponent(checkBox_3))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(checkBox_12)
						.addComponent(checkBox_13))
					.addGap(18)
					.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 3, GroupLayout.PREFERRED_SIZE)
					.addGap(14)
					.addComponent(jbxMingRiJIhua)
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(cbxJihuaLeixing, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel)
						.addComponent(tfdJihuaJiage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(20))
		);
		setLayout(groupLayout);
	}
}
