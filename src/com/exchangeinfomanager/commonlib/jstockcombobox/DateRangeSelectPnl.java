package com.exchangeinfomanager.commonlib.jstockcombobox;

import javax.swing.JPanel;

import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

import javax.swing.JLabel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import net.miginfocom.swing.MigLayout;
import java.awt.FlowLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class DateRangeSelectPnl extends JPanel {

	private SystemConfigration sysconfig;
	private JLocalDateChooser datachoosestart;
	private JLocalDateChooser datachooseend;
	
	
	public LocalDate getDatachoosestart() {
		return datachoosestart.getLocalDate();
	}

	public LocalDate getDatachooseend() {
		return datachooseend.getLocalDate();
	}

	/**
	 * Create the panel.
	 */
	public DateRangeSelectPnl() {
		
		sysconfig = SystemConfigration.getInstance();
		
		JLabel lblNewLabel = new JLabel("\u65F6\u95F4\u8DE8\u5EA6");
		
		datachoosestart = new JLocalDateChooser();
		
		JLabel lblNewLabel_1 = new JLabel("~~~~");
		
		datachooseend = new JLocalDateChooser();
		
		LocalDate searchstart = LocalDate.now().with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		datachoosestart.setLocalDate(searchstart);
		
		datachooseend.setLocalDate(LocalDate.now());
		setLayout(new MigLayout("", "[48px][89px][24px][100px]", "[21px]"));
		add(lblNewLabel, "cell 0 0,alignx left,aligny center");
		add(datachoosestart, "cell 1 0,growx,aligny top");
		add(lblNewLabel_1, "cell 2 0,alignx left,aligny center");
		add(datachooseend, "cell 3 0,growx,aligny top");

	}

}
