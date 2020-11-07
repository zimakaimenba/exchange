package com.exchangeinfomanager.bankuaifengxi;

import java.awt.FlowLayout;

import javax.swing.JPanel;

public class HighLightPane extends JPanel 
{
	private BanKuaiGeGuMatchCondition condition;

	public HighLightPane () 
	{
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
	}
	
	public void setHighLightCondition (	BanKuaiGeGuMatchCondition condition)
	{
		this.condition = condition;
	}
}
