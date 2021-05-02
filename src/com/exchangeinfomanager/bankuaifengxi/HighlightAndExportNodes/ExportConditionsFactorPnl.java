package com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;

import javax.swing.JComboBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import javax.swing.JCheckBox;

public class ExportConditionsFactorPnl extends JPanel {
	private JTextField tfldvaluelow;
	private JTextField tfldvalueup;
	private JComboBox<String> cbxselection;
	private JLabel lblyinzi;
	private JLabel lbldanwei;
	private JLabel lbldanwei2;
	private JLabel label_2;
	private JLocalDateChooser datechooser;
	private String factorkw;
	private JLabel lblNewLabel;
	private JLabel label;
	private JLabel label_1;
	private JLabel lblNewLabel_1;
	private JCheckBox cbxjisuan;
	private String factordanwei;

	/**
	 * Create the panel.
	 */
	public ExportConditionsFactorPnl(String factorkw1, String factorname, String factordanwei1) 
	{
		this.factorkw = factorkw1;
		this.factordanwei = factordanwei1.trim();
		
		initlizeGui ();
		lblyinzi.setText(factorname);
		lbldanwei.setText(factordanwei1);
		lbldanwei2.setText(factordanwei1);
		

		createEvents ();
	}
	
	public String getExportConditionFactorSetting ()
	{
		LocalDate dc = this.datechooser.getLocalDate();
		if(dc ==  null)
			dc = LocalDate.now().with(DayOfWeek.FRIDAY);
		else
			dc = dc.with(DayOfWeek.FRIDAY);
		
		String factor = "'" + this.factorkw + dc.toString().replace("-", "") + "'";
		
		if(cbxjisuan.isSelected())
			return factor;
		
		String operator = (String) cbxselection.getSelectedItem();
		String result = "";
		if(operator.equals("区间")) {
			String lowvalue = getRealValueByDanWei( tfldvaluelow.getText().trim() );
			String upvalue=  getRealValueByDanWei (tfldvalueup.getText().trim() );
			result = result + lowvalue + "<=" +  factor + "<=" + upvalue;
		} else {
			String lowvalue = getRealValueByDanWei (tfldvaluelow.getText().trim() );
			
			result = result + " " + factor + " "+ operator + " " + lowvalue   ;
		}
		return result;
	}
	private String getRealValueByDanWei (String value)
	{
		String realvalue = value;
		switch (this.factordanwei) {
		case "%": Double reald = Double.parseDouble(value) /100;
				  realvalue = reald.toString();
			break;
		case "亿": Double realy = Double.parseDouble(value) * 100000000;
		  		 realvalue = realy.toString();
			break;
		}
		
		return realvalue;
	}
	
	public Boolean isJiSuanFactor ()
	{
		if(cbxjisuan.isSelected())
			return true;
		else
			return false;
	}
	
	private void createEvents()
	{
		ActionListener cbActionListener = new ActionListener() {//add actionlistner to listen for change
            @Override
            public void actionPerformed(ActionEvent e) {
            	String operator = (String)cbxselection.getSelectedItem();
            	if(operator.trim().equals("区间")) 
            		tfldvalueup.setEnabled(true);
            	else
            		tfldvalueup.setEnabled(false);
            }
        };

        cbxselection.addActionListener(cbActionListener);
        
        cbxjisuan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(cbxjisuan.isSelected()) {
					cbxselection.setEnabled(false);
					tfldvaluelow.setEnabled(false);
					tfldvalueup.setEnabled(false);
				} else {
					cbxselection.setEnabled(true);
					tfldvaluelow.setEnabled(true);
//					tfldvalueup.setEnabled(false);
				}
			}
		});
		
	}

	private void initlizeGui() {
lblNewLabel = new JLabel("\u56E0\u5B50");
		
		lblyinzi = new JLabel("New label");
		
		label = new JLabel("\u9009\u9879");
		
		cbxselection = new JComboBox<>();
		cbxselection.setModel(new DefaultComboBoxModel(new String[] {"=", ">", ">=", "<", "<=", "\u533A\u95F4"}));
		
		label_1 = new JLabel("\u6570\u503C");
		
		tfldvaluelow = new JTextField();
		tfldvaluelow.setColumns(10);
		
		lbldanwei = new JLabel("New label");
		
		lblNewLabel_1 = new JLabel("~");
		
		tfldvalueup = new JTextField();
		tfldvalueup.setEnabled(false);
		tfldvalueup.setColumns(10);
		
		lbldanwei2 = new JLabel("New label");
		
		label_2 = new JLabel("\u65F6\u95F4\u70B9");
		
		datechooser = new JLocalDateChooser ();
		
		setLayout(new MigLayout("", "[36px][139px][4px][54px][6px][66px][54px]", "[15px][21px][21px][21px]"));
		add(lblNewLabel, "cell 0 0,alignx left,aligny top");
		add(lblyinzi, "cell 1 0,alignx left,aligny top");
		
		cbxjisuan = new JCheckBox("\u7528\u4E8E\u7EC4\u5408\u8BA1\u7B97");
		
		add(cbxjisuan, "cell 5 0");
		add(label, "cell 0 2,alignx left,aligny center");
		add(cbxselection, "cell 1 2 3 1,growx,aligny top");
		add(label_1, "cell 0 3,alignx left,aligny center");
		add(tfldvaluelow, "cell 1 3,growx,aligny top");
		add(lbldanwei, "cell 3 3,alignx left,aligny center");
		add(lblNewLabel_1, "cell 4 3,alignx left,aligny center");
		add(tfldvalueup, "cell 5 3,alignx left,aligny top");
		add(lbldanwei2, "cell 6 3,alignx left,aligny center");
		add(label_2, "cell 0 1,alignx left,aligny top");
		add(datechooser, "cell 1 1 3 1,alignx left,aligny top");
		
		if(this.factorkw.equals("CLOSEVSMA") ) 
			cbxselection.setEnabled(false);
	}
}
