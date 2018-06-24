package com.exchangeinfomanager.bankuaifengxi.ai;

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
import java.awt.GridLayout;

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
	private JLabel label_1;
	private JLabel label_2;
	private JLabel label_3;
	private JLabel label_4;
	private JLabel label_5;
	private JLabel label_6;
	private JLabel label_7;
	private JLabel label_8;
	private JLabel label_9;
	private JLabel label_10;
	private JLabel label_11;
	private JLabel label_12;
	private JLabel label_13;
	private JLabel label_14;
	private JLabel label_15;
	private JLabel label_16;
	private JLabel label_17;
	private JLabel label_18;
	private JLabel label_19;
	private JLabel label_20;
	private JLabel label_21;
	private JLabel label_22;
	private JLabel label_23;
	private JLabel label_24;
	private JLabel label_25;
	private JLabel label_26;
	private JLabel label_27;
	private JLabel label_28;
	private JLabel label_29;
	private JLabel label_30;
	private JLabel label_31;
	private JLabel label_32;
	private JLabel label_33;
	private JLabel label_34;
	private JLabel label_35;
	private JLabel label_36;
	private JLabel label_37;
	private JLabel label_38;
	private JLabel label_39;
	private JLabel label_40;
	private JLabel label_41;
	private JLabel label_42;
	private JLabel label_43;
	private JLabel label_44;
	private JLabel label_45;
	private JLabel label_46;
	private JLabel label_47;
	private JLabel label_48;
	private JLabel label_49;
	private JLabel label_50;
	private JLabel label_51;
	private JLabel label_52;
	private JLabel label_53;
	private JLabel label_54;
	private JLabel label_55;
	private JLabel label_56;
	private JLabel label_57;
	private JLabel label_58;
	private JLabel label_59;
	private JLabel label_60;
	private JLabel label_61;
	private JLabel label_62;
	private JLabel label_63;
	private JLabel label_64;
	private JLabel label_65;
	private JLabel label_66;
	private JLabel label_67;
	private JLabel label_68;
	private JLabel label_69;
	private JLabel label_70;
	private JLabel label_71;
	private JLabel label_72;
	private JLabel label_73;
	private JLabel label_74;
	private JLabel label_75;
	private JLabel label_76;
	private JLabel label_77;
	private JLabel label_78;
	private JLabel label_79;
	private JLabel label_80;
	private JLabel label_81;
	private JLabel label_82;
	private JLabel label_83;
	private JLabel label_84;
	private JLabel label_85;
	private JLabel label_86;
	private JLabel label_87;
	private JLabel label_88;
	private JLabel label_89;
	private JLabel label_90;
	private JLabel label_91;
	private JLabel label_92;
	private JLabel label_93;
	private JLabel label_94;
	private JLabel label_95;
	private JLabel label_96;
	private JLabel label_97;
	private JLabel label_98;
	private JLabel label_99;
	private JLabel label_100;
	private JLabel label_101;
	private JLabel label_102;
	private JLabel label_103;
	private JLabel label_104;
	private JLabel label_105;
	private JLabel label_106;
	private JLabel label_107;
	private JLabel label_108;
	private JLabel label_109;
	private JLabel label_110;
	private JLabel label_111;
	private JLabel label_112;
	private JLabel label_113;
	private JLabel label_114;
	private JLabel label_115;
	private JLabel label_116;
	private JLabel label_117;
	private JLabel label_118;
	private JLabel label_119;
	private JLabel label_120;
	private JLabel label_121;
	private JLabel label_122;
	private JLabel label_123;
	private JLabel label_124;
	private JLabel label_125;
	private JLabel label_126;
	private JLabel label_127;
	private JLabel label_128;
	private JLabel label_129;
	private JLabel label_130;
	private JLabel label_131;
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
		setLayout(new MigLayout("", "[55px][85px][61px][27px][73px][][][6px][73px][85px][85px][1px][4px]", "[26px][25px][26px][26px][25px][26px][26px][26px][25px][26px][26px][25px][31px]"));
		JLabel label = new JLabel("\u8BF4\u660E");
		add(label, "cell 0 0,grow");
		
		label_1 = new JLabel("");
		add(label_1, "flowy,cell 1 0,grow");
		
		label_2 = new JLabel("");
		add(label_2, "cell 2 0,grow");
		
		label_3 = new JLabel("");
		add(label_3, "cell 4 0,grow");
		
		label_4 = new JLabel("");
		add(label_4, "cell 7 0,grow");
		
		label_5 = new JLabel("");
		add(label_5, "cell 8 0,grow");
		
		label_6 = new JLabel("");
		add(label_6, "cell 9 0,grow");
		
		label_7 = new JLabel("");
		add(label_7, "cell 10 0,grow");
		
		label_8 = new JLabel("");
		add(label_8, "cell 11 0,grow");
		
		label_9 = new JLabel("");
		add(label_9, "cell 0 0,grow");
		
		label_10 = new JLabel("");
		add(label_10, "cell 12 0,grow");
		
		scrollPane = new JScrollPane();
		
		tfdShuoming = new JTextArea();
		tfdShuoming.setLineWrap(true);
		scrollPane.setViewportView(tfdShuoming);
		add(scrollPane, "cell 0 1,grow");
		
		label_11 = new JLabel("");
		add(label_11, "cell 1 1,grow");
		
		label_12 = new JLabel("");
		add(label_12, "cell 2 1,grow");
		
		label_13 = new JLabel("");
		add(label_13, "cell 3 1,grow");
		
		label_14 = new JLabel("");
		add(label_14, "cell 4 1,grow");
		
		label_15 = new JLabel("");
		add(label_15, "cell 7 1,grow");
		
		label_16 = new JLabel("");
		add(label_16, "cell 8 1,grow");
		
		label_17 = new JLabel("");
		add(label_17, "cell 9 1,grow");
		
		label_18 = new JLabel("");
		add(label_18, "cell 10 1,grow");
		
		label_19 = new JLabel("");
		add(label_19, "cell 11 1,grow");
		
		label_20 = new JLabel("");
		add(label_20, "cell 0 0,grow");
		
		label_21 = new JLabel("");
		add(label_21, "cell 12 1,grow");
		
		checkBox = new JCheckBox("\u6EE1\u8DB3\u653E\u91CF\u5747\u7EBF\u6A21\u578B");
		add(checkBox, "cell 0 2,grow");
		
		label_22 = new JLabel("");
		add(label_22, "cell 1 2,grow");
		
		label_23 = new JLabel("");
		add(label_23, "cell 2 2,grow");
		
		label_24 = new JLabel("");
		add(label_24, "cell 3 2,grow");
		
		label_25 = new JLabel("");
		add(label_25, "cell 4 2,grow");
		
		label_26 = new JLabel("");
		add(label_26, "cell 7 2,grow");
		
		label_27 = new JLabel("");
		add(label_27, "cell 8 2,grow");
		
		label_28 = new JLabel("");
		add(label_28, "cell 9 2,grow");
		
		label_29 = new JLabel("");
		add(label_29, "cell 10 2,grow");
		
		label_30 = new JLabel("");
		add(label_30, "cell 11 2,grow");
		
		label_31 = new JLabel("");
		add(label_31, "cell 0 0,grow");
		
		label_32 = new JLabel("");
		add(label_32, "cell 12 2,grow");
		
		
		checkBox_1 = new JCheckBox("\u5927\u6DA8\u540E\u53F3\u4FA7\u4E0B\u5C71\u8D70\u52BF\uFF0C\u53CD\u5F39\u8D8A\u6765\u8D8A\u5F31");
		add(checkBox_1, "cell 0 3,grow");
		
		label_33 = new JLabel("");
		add(label_33, "cell 1 3,grow");
		
		label_34 = new JLabel("");
		add(label_34, "cell 2 3,grow");
		
		label_35 = new JLabel("");
		add(label_35, "cell 3 3,grow");
		
		label_36 = new JLabel("");
		add(label_36, "cell 4 3,grow");
		
		label_37 = new JLabel("");
		add(label_37, "cell 7 3,grow");
		
		label_38 = new JLabel("");
		add(label_38, "cell 8 3,grow");
		
		label_39 = new JLabel("");
		add(label_39, "cell 9 3,grow");
		
		label_40 = new JLabel("");
		add(label_40, "cell 10 3,grow");
		
		label_41 = new JLabel("");
		add(label_41, "cell 11 3,grow");
		
		label_42 = new JLabel("");
		add(label_42, "cell 0 0,grow");
		
		label_43 = new JLabel("");
		add(label_43, "cell 12 3,grow");
		
		
		chckbxmacd = new JCheckBox("\u4E0B\u8DCC\u7F29\u91CF\uFF0C\u5206\u65F6MACD\u80CC\u79BB");
		add(chckbxmacd, "cell 0 4,grow");
		
		label_44 = new JLabel("");
		add(label_44, "flowy,cell 1 4,grow");
		
		label_45 = new JLabel("");
		add(label_45, "cell 2 4,grow");
		
		label_46 = new JLabel("");
		add(label_46, "cell 3 4,grow");
		
		label_47 = new JLabel("");
		add(label_47, "cell 4 4,grow");
		
		label_48 = new JLabel("");
		add(label_48, "cell 7 4,grow");
		
		label_49 = new JLabel("");
		add(label_49, "cell 8 4,grow");
		
		label_50 = new JLabel("");
		add(label_50, "cell 9 4,grow");
		
		label_51 = new JLabel("");
		add(label_51, "cell 10 4,grow");
		
		label_52 = new JLabel("");
		add(label_52, "cell 11 4,grow");
		
		label_53 = new JLabel("");
		add(label_53, "cell 0 0,grow");
		
		label_54 = new JLabel("");
		add(label_54, "cell 12 4,grow");
		
		
		checkBox_2 = new JCheckBox("\u8D85\u8DCC\u53CD\u5F39");
		add(checkBox_2, "cell 0 5,grow");
		
		checkBox_5 = new JCheckBox("\u524D\u70ED\u70B9\u4F59\u70ED");
		checkBox_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(checkBox_5.isSelected()) 
					updatedRecords (checkBox_5);
			}
		});
		add(checkBox_5, "cell 1 5,grow");
		
		label_55 = new JLabel("");
		add(label_55, "cell 2 5,grow");
		
		label_56 = new JLabel("");
		add(label_56, "cell 3 5,grow");
		
		label_57 = new JLabel("");
		add(label_57, "cell 4 5,grow");
		
		label_58 = new JLabel("");
		add(label_58, "cell 7 5,grow");
		
		label_59 = new JLabel("");
		add(label_59, "cell 8 5,grow");
		
		label_60 = new JLabel("");
		add(label_60, "cell 9 5,grow");
		
		label_61 = new JLabel("");
		add(label_61, "cell 11 5,grow");
		
		label_62 = new JLabel("");
		add(label_62, "cell 0 0,grow");
		
		checkBox_10 = new JCheckBox("\u9898\u6750\u8F6C\u4E1A\u7EE9");
		checkBox_10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_10.isSelected()) 
					updatedRecords (checkBox_10);
			}
		});
		
		checkBox_4 = new JCheckBox("\u9ED1\u5929\u9E45\u6536\u76CA");
		checkBox_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_4.isSelected()) 
					updatedRecords (checkBox_4);
			}
		});
		
		label_63 = new JLabel("");
		add(label_63, "cell 12 5,grow");
		add(checkBox_4, "cell 0 6,grow");
		
		checkBox_7 = new JCheckBox("\u6B21\u65B0\u80A1");
		checkBox_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_7.isSelected()) 
					updatedRecords (checkBox_7);
			}
		});
		add(checkBox_7, "flowy,cell 1 6,grow");
		
		label_64 = new JLabel("");
		add(label_64, "cell 1 6,grow");
		
		label_65 = new JLabel("");
		add(label_65, "cell 3 6,grow");
		
		label_66 = new JLabel("");
		add(label_66, "cell 4 6,grow");
		
		label_67 = new JLabel("");
		add(label_67, "cell 7 6,grow");
		
		label_68 = new JLabel("");
		add(label_68, "cell 9 6,grow");
		
		label_69 = new JLabel("");
		add(label_69, "cell 10 6,grow");
		
		label_70 = new JLabel("");
		add(label_70, "cell 11 6,grow");
		
		label_71 = new JLabel("");
		add(label_71, "cell 0 0,grow");
		
		label_72 = new JLabel("");
		add(label_72, "cell 12 6,grow");
		
		checkBox_9 = new JCheckBox("\u5238\u5546\u53C2\u5238");
		checkBox_9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_9.isSelected()) 
					updatedRecords (checkBox_9);
			}
		});
		add(checkBox_9, "cell 0 7,grow");
		add(checkBox_10, "cell 1 7,grow");
		
		label_73 = new JLabel("");
		add(label_73, "cell 2 7,grow");
		
		label_74 = new JLabel("");
		add(label_74, "cell 3 7,grow");
		
		label_75 = new JLabel("");
		add(label_75, "cell 4 7,grow");
		
		label_76 = new JLabel("");
		add(label_76, "cell 7 7,grow");
		
		label_77 = new JLabel("");
		add(label_77, "cell 8 7,grow");
		
		label_78 = new JLabel("");
		add(label_78, "cell 10 7,grow");
		
		label_79 = new JLabel("");
		add(label_79, "cell 0 0,grow");
		
		label_80 = new JLabel("");
		add(label_80, "cell 12 7,grow");
		
		checkBox_3 = new JCheckBox("\u7A33\u5B9A\u767D\u9A6C");
		checkBox_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_3.isSelected()) 
					updatedRecords (checkBox_3);
			}
		});
		
		
		chckbxma = new JCheckBox("\u91CD\u7EC4\u6536\u8D2D");
		add(chckbxma, "cell 0 8,grow");
		add(checkBox_3, "cell 1 8,grow");
		
		label_81 = new JLabel("");
		add(label_81, "cell 2 8,grow");
		
		label_82 = new JLabel("");
		add(label_82, "cell 3 8,grow");
		
		label_83 = new JLabel("");
		add(label_83, "cell 4 8,grow");
		
		label_84 = new JLabel("");
		add(label_84, "cell 7 8,grow");
		
		label_85 = new JLabel("");
		add(label_85, "cell 8 8,grow");
		
		label_86 = new JLabel("");
		add(label_86, "cell 9 8,grow");
		
		label_87 = new JLabel("");
		add(label_87, "cell 10 8,grow");
		
		label_88 = new JLabel("");
		add(label_88, "cell 11 8,grow");
		
		label_89 = new JLabel("");
		add(label_89, "cell 0 0,grow");
		
		label_90 = new JLabel("");
		add(label_90, "cell 12 8,grow");
		
		checkBox_12 = new JCheckBox("\u7279\u5B9A\u65F6\u95F4\u91CD\u5927\u4E8B\u4EF6");
		checkBox_12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_12.isSelected()) 
					updatedRecords (checkBox_12);
			}
		});
		add(checkBox_12, "cell 0 9,grow");
		
		checkBox_13 = new JCheckBox("\u56FD\u65B0\u653F\u7B56");
		checkBox_13.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_13.isSelected()) 
					updatedRecords (checkBox_13);
			}
		});
		add(checkBox_13, "flowy,cell 1 9,grow");
		
		label_91 = new JLabel("");
		add(label_91, "cell 1 9,grow");
		
		label_92 = new JLabel("");
		add(label_92, "cell 2 9,grow");
		
		label_93 = new JLabel("");
		add(label_93, "cell 3 9,grow");
		
		label_94 = new JLabel("");
		add(label_94, "cell 7 9,grow");
		
		label_95 = new JLabel("");
		add(label_95, "cell 8 9,grow");
		
		label_96 = new JLabel("");
		add(label_96, "cell 9 9,grow");
		
		label_97 = new JLabel("");
		add(label_97, "cell 10 9,grow");
		
		label_98 = new JLabel("");
		add(label_98, "cell 11 9,grow");
		
		label_99 = new JLabel("");
		add(label_99, "cell 0 0,grow");
		
		label_100 = new JLabel("");
		add(label_100, "cell 12 9,grow");
		
		checkBox_8 = new JCheckBox("\u6E2F\u7F8E\u8054\u52A8");
		checkBox_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_8.isSelected()) 
					updatedRecords (checkBox_8);
			}
		});
		add(checkBox_8, "flowx,cell 0 10,grow");
		
		checkBox_11 = new JCheckBox("\u6210\u957F\u548C\u6982\u5FF5");
		checkBox_11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_11.isSelected()) 
					updatedRecords (checkBox_11);
			}
		});
		add(checkBox_11, "flowy,cell 1 10,grow");
		
		label_101 = new JLabel("");
		add(label_101, "cell 1 10,grow");
		
		label_102 = new JLabel("");
		add(label_102, "cell 2 10,grow");
		
		label_103 = new JLabel("");
		add(label_103, "cell 3 10,grow");
		
		label_104 = new JLabel("");
		add(label_104, "cell 4 10,grow");
		
		label_105 = new JLabel("");
		add(label_105, "cell 7 10,grow");
		
		label_106 = new JLabel("");
		add(label_106, "cell 8 10,grow");
		
		label_107 = new JLabel("");
		add(label_107, "cell 9 10,grow");
		
		label_108 = new JLabel("");
		add(label_108, "cell 10 10,grow");
		
		label_109 = new JLabel("");
		add(label_109, "cell 11 10,grow");
		
		label_110 = new JLabel("");
		add(label_110, "cell 0 0,grow");
		
		label_111 = new JLabel("");
		add(label_111, "cell 12 10,grow");
		
		jbxMingRiJIhua = new JCheckBox("\u660E\u65E5\u8BA1\u5212");
		add(jbxMingRiJIhua, "cell 0 11,grow");
		
		label_112 = new JLabel("");
		add(label_112, "cell 1 11,grow");
		
		label_113 = new JLabel("");
		add(label_113, "cell 2 11,grow");
		
		label_114 = new JLabel("");
		add(label_114, "cell 3 11,grow");
		
		label_115 = new JLabel("");
		add(label_115, "cell 4 11,grow");
		
		label_116 = new JLabel("");
		add(label_116, "cell 7 11,grow");
		
		label_117 = new JLabel("");
		add(label_117, "cell 8 11,grow");
		
		label_118 = new JLabel("");
		add(label_118, "cell 9 11,grow");
		
		label_119 = new JLabel("");
		add(label_119, "cell 10 11,grow");
		
		label_120 = new JLabel("");
		add(label_120, "cell 11 11,grow");
		
		label_121 = new JLabel("");
		add(label_121, "cell 0 0,grow");
		
		label_122 = new JLabel("");
		add(label_122, "cell 12 11,grow");
		
		cbxJihuaLeixing = new JComboBox();
		cbxJihuaLeixing.setEnabled(false);
		cbxJihuaLeixing.setModel(new DefaultComboBoxModel(new String[] {"\u666E\u901A\u4E70\u5165", "\u666E\u901A\u5356\u51FA", "\u4FE1\u7528\u666E\u901A\u4E70\u5165", "\u4FE1\u7528\u666E\u901A\u5356\u51FA", "\u878D\u8D44\u4E70\u5165", "\u5356\u51FA\u507F\u8FD8\u878D\u8D44", "\u878D\u5238\u5356\u51FA", "\u4E70\u5165\u6362\u5238"}));
		add(cbxJihuaLeixing, "cell 0 12,grow");
		
		JLabel lblNewLabel = new JLabel("\u4EF7\u683C");
		add(lblNewLabel, "flowy,cell 1 12,grow");
		
		label_123 = new JLabel("");
		add(label_123, "cell 1 12,grow");
		
		label_124 = new JLabel("");
		add(label_124, "cell 2 12,grow");
		
		label_125 = new JLabel("");
		add(label_125, "cell 4 12,grow");
		
		
		tfdJihuaJiage = new JTextField();
		tfdJihuaJiage.setEnabled(false);
		tfdJihuaJiage.setColumns(10);
		add(tfdJihuaJiage, "cell 5 12 3 4,grow");
		
		label_126 = new JLabel("");
		add(label_126, "cell 8 12,grow");
		
		label_127 = new JLabel("");
		add(label_127, "cell 9 12,grow");
		
		label_128 = new JLabel("");
		add(label_128, "cell 10 12,grow");
		
		label_129 = new JLabel("");
		add(label_129, "cell 11 12,grow");
		
		label_130 = new JLabel("");
		add(label_130, "cell 0 0,grow");
		
		label_131 = new JLabel("");
		add(label_131, "cell 12 12,grow");
		
		checkBox_6 = new JCheckBox("\u6DA8\u4EF7\u548C\u4E1A\u7EE9");
		checkBox_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_6.isSelected()) 
					updatedRecords (checkBox_6);
			}
		});
		add(checkBox_6, "cell 1 4,grow");
		
		dateChooser = new JDateChooser();
		dateChooser.setDate(new Date ());
		add(dateChooser, "cell 1 0,grow");
	}
}
