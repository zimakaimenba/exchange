package com.exchangeinfomanager.StockCalendar;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;

import net.miginfocom.swing.MigLayout;

public class ZhiShuMilestoneDatePanel extends JPanel {
	private JTextField tfldzhishudaima;
	private JTextField tfldshuoming;
	private JLocalDateChooser jdatechooser;

	/**
	 * Create the panel.
	 */
	public ZhiShuMilestoneDatePanel()
	{
		
		JLabel label = new JLabel("\u6307\u6570\u4EE3\u7801");
		
		tfldzhishudaima = new JTextField();
		tfldzhishudaima.setColumns(10);
		
		jdatechooser = new JLocalDateChooser ();
		
		JLabel label_1 = new JLabel("\u8BF4\u660E");
		
		tfldshuoming = new JTextField();
		tfldshuoming.setColumns(10);
		setLayout(new MigLayout("", "[24px][4px][20px][10px][105px][10px][123px]", "[21px][21px]"));
		add(label, "cell 0 0 3 1,alignx left,aligny center");
		add(tfldzhishudaima, "cell 4 0,growx,aligny top");
		add(jdatechooser, "cell 6 0,alignx left,aligny center");
		add(label_1, "cell 0 1,alignx left,aligny center");
		add(tfldshuoming, "cell 2 1 5 1,growx,aligny top");

	}
	
	public String getZhiShuCode ()
	{
		return this.tfldzhishudaima.getText();
	}
	public JLocalDateChooser getZhiShuMilestoneDate ()
	{
		return this.jdatechooser;
	}
	public String getZhiShuMilestoneShuoming ()
	{
		return this.tfldshuoming.getText();
	}

}
