package com.exchangeinfomanager.bankuaichanyelian;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;



public class BanKuaiAndChanYeLianGUI2 <T extends BanKuaiAndChanYeLian2> extends JPanel 
{
	public BanKuaiAndChanYeLianGUI2 (T bkcyl1)
	{
		initializeGui ();
		this.bkcyl = bkcyl1;
		
	}
	
	private T bkcyl;
	
	private void initializeGui ()
	{
		
	}
}
