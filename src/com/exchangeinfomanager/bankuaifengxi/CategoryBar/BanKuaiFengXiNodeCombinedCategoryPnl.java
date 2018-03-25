package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelHightLightColumnListener;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjePnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjeZhanbiPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.TimeSeries.BanKuaiFengXiBarCjeLargePeriodChartPnl;
import com.exchangeinfomanager.bankuaifengxi.TimeSeries.BanKuaiFengXiBarCjeZhanBiLargePeriodChartPnl;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;

public class BanKuaiFengXiNodeCombinedCategoryPnl extends JPanel implements BarChartPanelHightLightColumnListener
{
	/**
	 * Create the panel.
	 */
	public BanKuaiFengXiNodeCombinedCategoryPnl() 
	{
		createGui ();
				
		chartpanelhighlightlisteners = new HashSet<BarChartPanelHightLightColumnListener> ();
		chartpanelhighlightlisteners.add(cjelargepnl);
		chartpanelhighlightlisteners.add(cjezblargepnl);
		
		createEvents();
	}
	/*
	 * 
	 */
	private void createGui() 
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		cjelargepnl = new BanKuaiFengXiCategoryBarChartCjePnl ();
		this.add(cjelargepnl);
		
		cjezblargepnl = new BanKuaiFengXiCategoryBarChartCjeZhanbiPnl ();
		this.add(cjezblargepnl);
	
	}

	private BanKuaiFengXiCategoryBarChartPnl cjelargepnl;
	private BanKuaiFengXiCategoryBarChartPnl cjezblargepnl;
	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
	/**
	 * Create the panel.
	 */
	public void updateData(BkChanYeLianTreeNode node, LocalDate displayedenddate1,String period) 
	{
		cjelargepnl.updatedDate(node, displayedenddate1, 0, period);
		cjezblargepnl.updatedDate(node, displayedenddate1, 0, period);
	}
	public void updateData(BkChanYeLianTreeNode node, LocalDate startdate,LocalDate enddate,String period) 
	{
		cjelargepnl.updatedDate(node, startdate,enddate, period);
		cjezblargepnl.updatedDate(node, startdate,enddate, period);
	}
	@Override
	public void highLightSpecificBarColumn(Comparable<String> selecteddate) 
	{
		chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(selecteddate));
	}
	@Override
	public void highLightSpecificBarColumn(Integer columnindex) 
	{
		// TODO Auto-generated method stub
	}
	@Override
	public void setHightLightColumnListeners(Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners1) 
	{
		cjelargepnl.setHightLightColumnListeners(chartpanelhighlightlisteners1);
		cjezblargepnl.setHightLightColumnListeners(chartpanelhighlightlisteners1);
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
	 			
//     				String selectdate = cjelargepnl.getCurSelectedBarDate().toString();
//     				LocalDate selectdate1 = CommonUtility.formateStringToDate(selectdate);
     				
     				//同步几个panel
     				Comparable<String> datekey = cjelargepnl.getCurSelectedBarDate ();
     				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
    	        }
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {

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
	 			
//     				String selectdate = cjelargepnl.getCurSelectedBarDate().toString();
//     				LocalDate selectdate1 = CommonUtility.formateStringToDate(selectdate);
     				
     				//同步几个panel
     				Comparable<String> datekey = cjezblargepnl.getCurSelectedBarDate ();
     				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
    	        }
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	
    /*
     * 
     */
//    private JScrollBar getScrollBar(CategoryAxis axis)
//    {
//        double r1 = axis.getLowerMargin();
//        double r2 = axis.getUpperMargin();
//        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 100, 100, 0, 400);
//        scrollBar.addAdjustmentListener( new AdjustmentListener() {
//            public void adjustmentValueChanged(AdjustmentEvent e) {
//                double x = e.getValue() *60 *60 * 1000;
////                axis.setRange(r1+x, r2+x);
//            }
//        });
//        return scrollBar;
//    }


}
