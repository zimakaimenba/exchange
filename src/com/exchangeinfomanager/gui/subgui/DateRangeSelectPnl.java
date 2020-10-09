package com.exchangeinfomanager.gui.subgui;

import javax.swing.JPanel;

import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;


import javax.swing.JLabel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import net.miginfocom.swing.MigLayout;
import java.awt.FlowLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class DateRangeSelectPnl extends JPanel 
{

//	private SystemConfigration sysconfig;
	private JLocalDateChooser datachoosestart;
	private JLocalDateChooser datachooseend;
	private Integer wknum;
	
	public DateRangeSelectPnl (Integer wknum)
	{
		initialGui ();
		
		this.wknum = wknum;
		LocalDate searchstart = LocalDate.now().with(DayOfWeek.MONDAY).minus(wknum,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
		datachoosestart.setLocalDate(searchstart);
		
		datachooseend.setLocalDate(LocalDate.now());
	}
	
	public DateRangeSelectPnl (LocalDate start, LocalDate end)
	{
		initialGui ();
		
		datachoosestart.setLocalDate(start);
		datachooseend.setLocalDate(end);
		
	}
	

	public LocalDate getDatachoosestart() {
		return datachoosestart.getLocalDate();
	}

	public LocalDate getDatachooseend() {
		return datachooseend.getLocalDate();
	}

	/**
	 * Create the panel.
	 */
	private void initialGui() 
	{
		
//		sysconfig = SystemConfigration.getInstance();
		
		JLabel lblNewLabel = new JLabel("\u65F6\u95F4\u8DE8\u5EA6");
		
		datachoosestart = new JLocalDateChooser();
		
		JLabel lblNewLabel_1 = new JLabel("~~~~");
		
		datachooseend = new JLocalDateChooser();
		
		
		setLayout(new MigLayout("", "[48px][89px][24px][100px]", "[21px]"));
		add(lblNewLabel, "cell 0 0,alignx left,aligny center");
		add(datachoosestart, "cell 1 0,growx,aligny top");
		add(lblNewLabel_1, "cell 2 0,alignx left,aligny center");
		add(datachooseend, "cell 3 0,growx,aligny top");

	}

}
