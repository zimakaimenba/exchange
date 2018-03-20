package com.exchangeinfomanager.bankuaifengxi;

import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;

public class BanKuaiFengXiLargePeirod extends JPanel {

	private BanKuaiFengXiBarCjeLargePeriodChartPnl cjelargepnl;
	private BanKuaiFengXiBarCjeZhanBiLargePeriodChartPnl cjezblargepnl;

	/**
	 * Create the panel.
	 */
	public BanKuaiFengXiLargePeirod(BkChanYeLianTreeNode node, LocalDate displayedenddate1,String period) 
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		cjelargepnl = new BanKuaiFengXiBarCjeLargePeriodChartPnl (node,displayedenddate1,period);
		this.add(cjelargepnl);
		 cjezblargepnl = new BanKuaiFengXiBarCjeZhanBiLargePeriodChartPnl (node,displayedenddate1,period);
		this.add(cjezblargepnl);
		
		createEvents();
	}
	
	private void createEvents ()
	{
		cjelargepnl.getChartPanel().addChartMouseListener(new ChartMouseListener() { 
			@Override
			public void chartMouseClicked(ChartMouseEvent e)  
			{
				java.awt.event.MouseEvent me = e.getTrigger();
    	        if (me.getClickCount() == 2) {
    	        }
    	        
    	        if (me.getClickCount() == 1) {
    	        	//显示选择的tooltip
    				String tooltip = cjelargepnl.getToolTipSelected ();
    				if(tooltip == null)
    					return;

    				//同步几个panel
    				Comparable datekey = panelbkwkzhanbi.getCurSelectedBarDate ();
    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
    	        }
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

}
