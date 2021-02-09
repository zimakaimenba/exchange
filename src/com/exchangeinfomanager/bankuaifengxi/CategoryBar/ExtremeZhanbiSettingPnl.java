package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import javax.swing.JPanel;

import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.google.common.base.Strings;

import javax.swing.JLabel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import net.miginfocom.swing.MigLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;

public class ExtremeZhanbiSettingPnl  extends JPanel 
{
	
	private JTextField tfldmin;
	private JLabel lblmin;
	private JLabel lblmax;
	private JTextField tfldmax;
	private JLabel lblcrosshairvalue;
	private Double yaxisvaluewhenmouseclicked;
	
	public ExtremeZhanbiSettingPnl (Double[] curzb, Double yaxisvaluewhenmouseclicked1)
	{
		this.yaxisvaluewhenmouseclicked = yaxisvaluewhenmouseclicked1;
		initialGui ();
		
		if(curzb[0] != null)
			tfldmin.setText(String.valueOf(curzb[0]));
		if(curzb[1] != null)
			tfldmax.setText(String.valueOf(curzb[1]));
	}
	
	public Double getExtremeZhanbiMin() {
		if(Strings.isNullOrEmpty(tfldmin.getText() ) )
			return null;
		else
			return Double.parseDouble( tfldmin.getText() );
	}

	public Double getExtremeZhanbiMax() {
		if(Strings.isNullOrEmpty(tfldmax.getText() ) )
			return null;
		else
			return Double.parseDouble( tfldmax.getText() );
	}


	/**
	 * Create the panel.
	 */
	private void initialGui() 
	{
		lblmin = new JLabel("\u5360\u6BD4\u4E0B\u9650");
		
		lblmax = new JLabel("\u5360\u6BD4\u4E0A\u9650");
		
		tfldmin = new JTextField();
		tfldmin.setColumns(10);
		
		JLabel label = new JLabel("当前坐标值:");

		if(this.yaxisvaluewhenmouseclicked != null) {
			lblcrosshairvalue = new JLabel(String.valueOf(this.yaxisvaluewhenmouseclicked));
			String myString = String.valueOf(this.yaxisvaluewhenmouseclicked);
			StringSelection stringSelection = new StringSelection(myString);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
		}
		else
			lblcrosshairvalue = new JLabel(" ");
		
		tfldmax = new JTextField();
		tfldmax.setColumns(10);
		setLayout(new MigLayout("", "[60px][87px][24px][84px]", "[21px][15px]"));
		add(lblmin, "cell 0 0,alignx right,aligny center");
		add(tfldmin, "cell 1 0,growx,aligny top");
		add(lblmax, "cell 2 0,alignx left,aligny center");
		add(label, "cell 0 1,alignx left,aligny top");
		add(lblcrosshairvalue, "cell 1 1,alignx left,aligny top");
		add(tfldmax, "cell 3 0,growx,aligny top");

	}
}
