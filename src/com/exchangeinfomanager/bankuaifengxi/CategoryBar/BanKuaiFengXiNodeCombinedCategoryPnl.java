package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaifengxi.BarChartHightLightFxDataValueListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelHightLightColumnListener;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjePnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjeZhanbiPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.TimeSeries.BanKuaiFengXiBarCjeLargePeriodChartPnl;
import com.exchangeinfomanager.bankuaifengxi.TimeSeries.BanKuaiFengXiBarCjeZhanBiLargePeriodChartPnl;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;

public class BanKuaiFengXiNodeCombinedCategoryPnl extends JPanel implements BarChartPanelDataChangedListener, BarChartPanelHightLightColumnListener ,BarChartHightLightFxDataValueListener
{
	/**
	 * Create the panel.
	 */
//	public BanKuaiFengXiNodeCombinedCategoryPnl(BkChanYeLianTreeNode curdisplayednode,String horizonorvertical) 
	public BanKuaiFengXiNodeCombinedCategoryPnl(String horizonorvertical)
	{
//		this.curdisplayednode =  curdisplayednode;
		if(horizonorvertical.toLowerCase().equals("horizon"))
			this.horizonlayout = true;
		else 
			this.horizonlayout = false;
		
		createGui ();
				
		chartpanelhighlightlisteners = new HashSet<BarChartPanelHightLightColumnListener> ();
		chartpanelhighlightlisteners.add(cjelargepnl);
		chartpanelhighlightlisteners.add(cjezblargepnl);
		
		createEvents();
	}
	public static final String SELECTED_PROPERTY = "selected";
	protected boolean selectchanged;
	private String tooltipselected;
	private LocalDate dateselected;
	private boolean horizonlayout;
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this); //	https://stackoverflow.com/questions/4690892/passing-a-value-between-components/4691447#4691447
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	        pcs.addPropertyChangeListener(listener);
	}
	/*
	 * 
	 */
	private void createGui() 
	{
		if(this.horizonlayout)
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		else
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		cjelargepnl = new BanKuaiFengXiCategoryBarChartCjePnl ();
		this.add(cjelargepnl);
		
		cjezblargepnl = new BanKuaiFengXiCategoryBarChartCjeZhanbiPnl ();
		this.add(cjezblargepnl);
	
	}

	private BkChanYeLianTreeNode curdisplayednode;
	private BanKuaiFengXiCategoryBarChartPnl cjelargepnl;
	private BanKuaiFengXiCategoryBarChartPnl cjezblargepnl;
	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
	
	@Override
	public void updatedDate(BkChanYeLianTreeNode node, LocalDate date, int difference, String period) 
	{
		cjelargepnl.updatedDate(node, date, difference, period);
		cjezblargepnl.updatedDate(node, date, difference, period);
	}
	public void updatedDate(BkChanYeLianTreeNode node, LocalDate startdate,LocalDate enddate,String period) 
	{
		cjelargepnl.updatedDate(node, startdate,enddate, period);
		cjezblargepnl.updatedDate(node, startdate,enddate, period);
	}
	@Override
	public void highLightSpecificBarColumn(LocalDate selecteddate) 
	{
		chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(selecteddate));
	}
	@Override
	public void hightLightFxValues(Integer cjezbdpmax, Integer cjezbbkmax, Double cje, Integer cjemaxwk,Double showhsl) 
	{
		cjezblargepnl.hightLightFxValues(null,cjezbdpmax, null, null,null) ;
		cjelargepnl.hightLightFxValues(null,null, null, cjemaxwk,null) ;
	}
	@Override
	public void highLightSpecificBarColumn(Integer columnindex) 
	{
		// TODO Auto-generated method stub
	}

	/*
	 * 
	 */
	private void createEvents ()
	{
		cjelargepnl.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			 public void propertyChange(PropertyChangeEvent evt)  
			 {
				if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
					String selectedinfo = evt.getNewValue().toString();
                    
					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
    				
    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
    				setCurSelectedBarInfo (datekey,tooltip);
				}
			}
		});
		cjezblargepnl.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			 public void propertyChange(PropertyChangeEvent evt)  
			 {
				if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
					String selectedinfo = evt.getNewValue().toString();
                    
					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
    				
    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
    				setCurSelectedBarInfo (datekey,tooltip);
				}
			}
		});
	}
	
	/*
	 * 
	 */
	public void setCurSelectedBarInfo (LocalDate newdate,String selectedtooltip) 
	{
        String oldText = this.dateselected + this.tooltipselected;
        this.dateselected = newdate ;
        this.tooltipselected =  this.curdisplayednode.getMyOwnCode() + this.curdisplayednode.getMyOwnName() + ": " + selectedtooltip;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, SELECTED_PROPERTY, oldText, this.dateselected.toString() + this.tooltipselected );
        pcs.firePropertyChange(evt);
    }
}
