package com.exchangeinfomanager.StockCalendar;

import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import net.miginfocom.swing.MigLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingOfDisplayNewsArea extends JPanel 
{
	private JCheckBox chkbxzhishudate;
	private JCheckBox chkbxjingqiguanzhu;
	private JCheckBox chxbxqiangruo;
	private JCheckBox chkbxmonthnews;
	private JCheckBox chkbxdisplayall;

	/**
	 * Create the panel.
	 */
	public SettingOfDisplayNewsArea() 
	{
		initializeGui ();
		createEvents ();
		

	}
	/*
	 * 
	 */
	private void initializeGui() 
	{
		JCheckBox chckbxNewCheckBox = new JCheckBox("\u663E\u793A\u6BCF\u65E5\u91CD\u5927\u65B0\u95FB");
		chckbxNewCheckBox.setSelected(true);
		
		chkbxdisplayall = new JCheckBox("\u663E\u793A\u6240\u6709\u5176\u4ED6\u65B0\u95FB");
		
		
		chkbxmonthnews = new JCheckBox("\u663E\u793A\u6BCF\u6708\u56FA\u5B9A\u65B0\u95FB");
		chkbxmonthnews.setSelected(true);
		
		chxbxqiangruo = new JCheckBox("\u663E\u793A\u5F3A\u5F31\u52BF\u677F\u5757\u4E2A\u80A1");
		chxbxqiangruo.setSelected(true);
		
		chkbxjingqiguanzhu = new JCheckBox("\u663E\u793A\u8FD1\u671F\u5173\u6CE8");
		chkbxjingqiguanzhu.setSelected(true);
		
		chkbxzhishudate = new JCheckBox("\u663E\u793A\u6307\u6570\u5173\u6CE8\u65E5\u671F");
		chkbxzhishudate.setSelected(true);
		setLayout(new MigLayout("", "[133px]", "[23px][23px][23px][23px][23px][23px]"));
		add(chckbxNewCheckBox, "cell 0 0,alignx left,aligny top");
		add(chkbxdisplayall, "cell 0 1,alignx left,aligny top");
		add(chkbxmonthnews, "cell 0 2,alignx left,aligny top");
		add(chxbxqiangruo, "cell 0 3,alignx left,aligny top");
		add(chkbxjingqiguanzhu, "cell 0 4,alignx left,aligny top");
		add(chkbxzhishudate, "cell 0 5,alignx left,aligny top");
		
	}
	/*
	 * 
	 */
	private void createEvents() 
	{
		chkbxdisplayall.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( chkbxdisplayall.isSelected() ) {
					chkbxmonthnews.setSelected(true);
					chkbxmonthnews.setEnabled(false);
					
					chxbxqiangruo.setSelected(true);
					chxbxqiangruo.setEnabled(false);
					
					chkbxjingqiguanzhu.setSelected(true);
					chkbxjingqiguanzhu.setEnabled(false);
					
					chkbxzhishudate.setSelected(true);
					chkbxzhishudate.setEnabled(false);
				} else {
//					chkbxmonthnews.setSelected(true);
					chkbxmonthnews.setEnabled(true);
					
//					chxbxqiangruo.setSelected(true);
					chxbxqiangruo.setEnabled(true);
					
//					chkbxjingqiguanzhu.setSelected(true);
					chkbxjingqiguanzhu.setEnabled(true);
					
//					chkbxzhishudate.setSelected(true);
					chkbxzhishudate.setEnabled(true);
					
				}
			}
		});
		
	}

	public Boolean shouldDisplayAllExtraNews ()
	{
		if(chkbxdisplayall.isSelected())
			return true;
		else
			return false;
			
	}
	
	public Boolean shouldDisplayMonthNews ()
	{
		if(chkbxmonthnews.isSelected())
			return true;
		else
			return false;
			
	}
	
	public Boolean shouldDisplayQiangRuoNews ()
	{
		if(chxbxqiangruo.isSelected())
			return true;
		else
			return false;
			
	}
	
	public Boolean shouldDisplayGuanZhuNews ()
	{
		if(chkbxjingqiguanzhu.isSelected())
			return true;
		else
			return false;
			
	}
	
	public Boolean shouldDisplayZhishuDate () {
		if(chkbxzhishudate.isSelected())
			return true;
		else
			return false;
			
	}
	
}
