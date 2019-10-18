package com.exchangeinfomanager.StockCalendar;

import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JToolTip;
import javax.swing.ToolTipManager;

import com.exchangeinfomanager.commonlib.JMultiLineToolTip;

public class JUpdatedLabel extends JLabel
{
	public JUpdatedLabel (String label)
	{
		super (label);
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseEntered (MouseEvent evt)
			{
				ToolTipManager.sharedInstance().setDismissDelay(60000); //让tooltips显示时间延长
			}
	        public void mouseClicked(MouseEvent evt) 
	        {
	        	
	        }
		});
		
	}
	
	public JToolTip createToolTip() 
	{
		JMultiLineToolTip jmtt =  new JMultiLineToolTip ();
		String test = this.getText();
		FontMetrics fm = getFontMetrics(getFont());
		
		int basiclength = fm.stringWidth(test);

		jmtt.setFixedWidth(  300 );
		
		return jmtt;
	}
}
