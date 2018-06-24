package com.exchangeinfomanager.bankuaifengxi.TimeSeries;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelHightLightListener;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXiCandlestick.BanKuaiFengXiCandlestickPnl;
import com.exchangeinfomanager.bankuaifengxi.TimeSeries.BanKuaiFengXiBarCjeLargePeriodChartPnl;
import com.exchangeinfomanager.bankuaifengxi.TimeSeries.BanKuaiFengXiBarCjeZhanBiLargePeriodChartPnl;
import com.exchangeinfomanager.bankuaifengxi.ai.JiaRuJiHua;
import com.exchangeinfomanager.commonlib.CommonUtility;

public class BanKuaiFengXiNodeCombinedTimeSeriesPnl extends JPanel {

	private BanKuaiFengXiBarCjeLargePeriodChartPnl cjelargepnl;
	private BanKuaiFengXiBarCjeZhanBiLargePeriodChartPnl cjezblargepnl;
	private Set<BarChartPanelHightLightListener> chartpanelhighlightlisteners;
	private BanKuaiFengXiCandlestickPnl kxianpnl;

	/**
	 * Create the panel.
	 */
	public BanKuaiFengXiNodeCombinedTimeSeriesPnl(BkChanYeLianTreeNode node, LocalDate displayedenddate1,String period) 
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		 kxianpnl = new BanKuaiFengXiCandlestickPnl ();
		this.add(kxianpnl);
		kxianpnl.updatedDate(node, displayedenddate1, 0, StockGivenPeriodDataItem.DAY);
		
		cjelargepnl = new BanKuaiFengXiBarCjeLargePeriodChartPnl (node,displayedenddate1,period);
		this.add(cjelargepnl);
		
		 cjezblargepnl = new BanKuaiFengXiBarCjeZhanBiLargePeriodChartPnl (node,displayedenddate1,period);
		this.add(cjezblargepnl);
		
		chartpanelhighlightlisteners = new HashSet<BarChartPanelHightLightListener> ();
		chartpanelhighlightlisteners.add(cjelargepnl);
		chartpanelhighlightlisteners.add(cjezblargepnl);
		
		createEvents();
	}
	/*
	 * 
	 */
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
    				Integer columnindex = cjelargepnl.getCurSelectedIndex ();
    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(columnindex));
    	        }
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		cjezblargepnl.getChartPanel().addChartMouseListener(new ChartMouseListener() { 
			@Override
			public void chartMouseClicked(ChartMouseEvent e)  
			{
				java.awt.event.MouseEvent me = e.getTrigger();
    	        if (me.getClickCount() == 2) {
    	        }
    	        
    	        if (me.getClickCount() == 1) {
    	        	//显示选择的tooltip
    				String tooltip = cjezblargepnl.getToolTipSelected ();
    				if(tooltip == null)
    					return;

    				//同步几个panel
    				Integer columnindex = cjezblargepnl.getCurSelectedIndex ();
    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(columnindex));
    	        }
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

}
