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
import org.jsoup.Jsoup;

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

public class BanKuaiFengXiNodeCombinedCategoryPnl extends JPanel 
		implements BarChartPanelDataChangedListener, BarChartPanelHightLightColumnListener ,BarChartHightLightFxDataValueListener,PropertyChangeListener
{
	/**
	 * Create the panel.
	 */
//	public BanKuaiFengXiNodeCombinedCategoryPnl(BkChanYeLianTreeNode curdisplayednode,String horizonorvertical) 
	public BanKuaiFengXiNodeCombinedCategoryPnl(String horizonorvertical)
	{
//		this.curdisplayednode =  curdisplayednode;
		if(horizonorvertical != null && horizonorvertical.toLowerCase().equals("horizon"))
			this.horizonlayout = true;
		else 
			this.horizonlayout = false;
		
		initializeSystem ();
	}
	public BanKuaiFengXiNodeCombinedCategoryPnl()
	{
		this.horizonlayout = false;
		initializeSystem ();
	}
	private void initializeSystem ()
	{
		createGui ();
		
		chartpanelhighlightlisteners = new HashSet<BarChartPanelHightLightColumnListener> ();
		chartpanelhighlightlisteners.add(cjelargepnl);
		chartpanelhighlightlisteners.add(cjezblargepnl);
		
		cjelargepnl.addPropertyChangeListener(this);
		cjezblargepnl.addPropertyChangeListener(this);
		
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
	public BkChanYeLianTreeNode getCurDisplayedNode ()
	{
		return this.curdisplayednode;
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
		this.curdisplayednode = node;
		cjelargepnl.updatedDate(node, date, difference, period);
		cjezblargepnl.updatedDate(node, date, difference, period);
	}
	public void updatedDate(BkChanYeLianTreeNode node, LocalDate startdate,LocalDate enddate,String period) 
	{
		this.curdisplayednode = node;
		cjelargepnl.updatedDate(node, startdate,enddate, period);
		cjezblargepnl.updatedDate(node, startdate,enddate, period);
	}
	@Override
	public void highLightSpecificBarColumn(LocalDate selecteddate) 
	{
		chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(selecteddate));
	}
	@Override
	public void hightLightFxValues(Integer cjezbdpmax, Integer cjezbbkmax, Double cjemin, Double cjemax, Integer cjemaxwk,Double showhsl,Double ltszmin,Double ltszmax) {
		
	}
	@Override
	public void hightLightFxValues(Integer cjezbtoupleveldpmax, Double cjemin, Double cjemax,Integer cjemaxwk, Double shoowhsl) {
		cjezblargepnl.hightLightFxValues(cjezbtoupleveldpmax, cjemin, cjemax,cjemaxwk,shoowhsl) ;
		cjelargepnl.hightLightFxValues(cjezbtoupleveldpmax, cjemin, cjemax,cjemaxwk,shoowhsl) ;
		
	}
	@Override
	public void highLightSpecificBarColumn(Integer columnindex) 
	{
		
	}
	/*
	 * 
	 */
	public void resetDate ()
	{
		cjezblargepnl.resetDate();
		cjelargepnl.resetDate();
	}

	/*
	 * 
	 */
	private void createEvents ()
	{
//		this.addPropertyChangeListener(new PropertyChangeListener() {
//			@Override
//			 public void propertyChange(PropertyChangeEvent evt)  
//			 {
//				if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
//					String selectedinfo = evt.getNewValue().toString();
//                    
//					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
//    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
//    				
//    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
//    				setCurSelectedBarInfo (datekey,tooltip);
//					
//				} else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY)) {
//					
//				}
//			 }
//		
//		});
		
//		cjelargepnl.addPropertyChangeListener(new PropertyChangeListener() {
//			@Override
//			 public void propertyChange(PropertyChangeEvent evt)  
//			 {
//				if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
//					String selectedinfo = evt.getNewValue().toString();
//                    
//					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
//    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
//    				
//    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
//    				setCurSelectedBarInfo (datekey,tooltip);
//				}
//			}
//		});
//		cjezblargepnl.addPropertyChangeListener(new PropertyChangeListener() {
//			@Override
//			 public void propertyChange(PropertyChangeEvent evt)  
//			 {
//				if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
//					String selectedinfo = evt.getNewValue().toString();
//                    
//					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
//    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
//    				
//    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
//    				setCurSelectedBarInfo (datekey,tooltip);
//				}
//			}
//		});
	}
	/*
	 * 
	 */
//	public void setCurSelectedBarInfo (LocalDate newdate,String selectedtooltip) 
//	{
//        String oldText = this.dateselected + this.tooltipselected;
//        this.dateselected = newdate ;
////        try {
////        	this.tooltipselected =  this.curdisplayednode.getMyOwnCode() + this.curdisplayednode.getMyOwnName() + ": " + selectedtooltip;
////        } catch (java.lang.NullPointerException e) {
//////        	e.printStackTrace();
////        	this.tooltipselected =  "个股代码/名称NULL"  + ": " + selectedtooltip;
////        }
////        PropertyChangeEvent evt = new PropertyChangeEvent(this, SELECTED_PROPERTY, oldText, this.dateselected.toString() + this.tooltipselected );
//        PropertyChangeEvent evt = new PropertyChangeEvent(this, SELECTED_PROPERTY, oldText, this.tooltipselected );
//        pcs.firePropertyChange(evt);
//    }
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
			String selectedinfo = evt.getNewValue().toString();
            if(selectedinfo.equals("test"))
            	return;
            
            org.jsoup.nodes.Document doc = Jsoup.parse(selectedinfo);
    		org.jsoup.select.Elements content = doc.select("body");
    		org.jsoup.select.Elements dl = content.select("dl");
    		org.jsoup.select.Elements li = dl.get(0).select("li");
    		String selecteddate = li.get(0).text();
    		LocalDate datekey = LocalDate.parse(selecteddate);
    		chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
    		
    		//把两个panel的HTML信息组合成一个HTML向上级界面分发
    		org.jsoup.select.Elements divnodecodename = content.select("nodecode");
    		org.jsoup.select.Elements nodetype = content.select("nodetype");
    		
    		String cjettp = cjelargepnl.getToolTipSelected ();
    		org.jsoup.nodes.Document cjedoc = Jsoup.parse(cjettp);
    		org.jsoup.select.Elements cjecontent = cjedoc.select("body");
    		org.jsoup.select.Elements cjedl = cjedoc.select("dl");
    		org.jsoup.select.Elements cjeli = cjedoc.select("li");
    		
    		
    		String cjezbttp = cjezblargepnl.getToolTipSelected ();
    		org.jsoup.nodes.Document cjezbdoc = Jsoup.parse(cjezbttp);
    		org.jsoup.select.Elements cjezbcontent = cjezbdoc.select("body");
    		org.jsoup.select.Elements cjezbdl = cjezbdoc.select("dl");
    		org.jsoup.select.Elements cjezbli = cjezbdoc.select("li");
    		
    		
    		String html = "";
    		org.jsoup.nodes.Document htmldoc = Jsoup.parse(html);
    		org.jsoup.select.Elements htmlcontent = htmldoc.select("body");
    		for(org.jsoup.nodes.Element htmlbody : htmlcontent) {
    			org.jsoup.nodes.Element htmldiv = htmlbody.appendElement("div");
    			htmldiv.appendChild( divnodecodename.get(0) );
    			htmldiv.appendChild( nodetype.get(0) );
    			
    			org.jsoup.nodes.Element htmldl = htmldiv.appendElement("dl");
   			 
	   			 org.jsoup.nodes.Element lidate = htmldl.appendElement("li");
	   			 lidate.appendText(selecteddate.toString());
	   			 
	   			 for(int i=1;i<cjeli.size();i++) {
	   				org.jsoup.nodes.Element htmlcjeli = cjeli.get(i);
	   				htmldl.appendChild(htmlcjeli);
	   			 }
	   			for(int i=1;i<cjezbli.size();i++) { 
	   				org.jsoup.nodes.Element htmlcjezbli = cjezbli.get(i); 
	   				htmldl.appendChild(htmlcjezbli);
	   			 }
	   			 
    		}
    		html = htmldoc.toString();
    		PropertyChangeEvent evtnew = new PropertyChangeEvent(this, SELECTED_PROPERTY, "", html );
            pcs.firePropertyChange(evtnew);
    		
//			setCurSelectedBarInfo (datekey,tooltip);
			
		} else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY)) {
			String selectedinfo = evt.getNewValue().toString();
			
//			PropertyChangeEvent evt2 = new PropertyChangeEvent(this, SELECTED_PROPERTY, oldText, this.dateselected.toString() + this.tooltipselected );
	        pcs.firePropertyChange(evt);
		}
		
	}

}
