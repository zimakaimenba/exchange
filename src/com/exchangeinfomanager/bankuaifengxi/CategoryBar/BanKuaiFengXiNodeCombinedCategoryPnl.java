package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jsoup.Jsoup;


import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaifengxi.BarChartHightLightFxDataValueListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelHightLightColumnListener;
import com.exchangeinfomanager.bankuaifengxi.ExportCondition;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjePnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjeZhanbiPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.TimeSeries.BanKuaiFengXiBarCjeLargePeriodChartPnl;
import com.exchangeinfomanager.bankuaifengxi.TimeSeries.BanKuaiFengXiBarCjeZhanBiLargePeriodChartPnl;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;

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
	public TDXNodes getCurDisplayedNode ()
	{
		return this.curdisplayednode;
	}
	
		
	
	@Override
	public void updatedDate(TDXNodes node, LocalDate date, int difference, String period) 
	{
		this.curdisplayednode = node;
		cjelargepnl.updatedDate(node, date, difference, period);
		cjezblargepnl.updatedDate(node, date, difference, period);
	}
	public void updatedDate(TDXNodes node, LocalDate startdate,LocalDate enddate,String period) 
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
	public void hightLightFxValues(ExportCondition expc) 
	{
		cjezblargepnl.hightLightFxValues(expc) ;
		cjelargepnl.hightLightFxValues(expc) ;
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

	}
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
    		org.jsoup.select.Elements li;
    		try {
    			 li = dl.get(0).select("li");
    		} catch (java.lang.IndexOutOfBoundsException e) {
    			return ;
    		}
    		
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
	
	private TDXNodes curdisplayednode;
	private BanKuaiFengXiCategoryBarChartPnl cjelargepnl;
	private BanKuaiFengXiCategoryBarChartPnl cjezblargepnl;
	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
	private JPopupMenu quekouImage;
	private JMenuItem mntmqkopenup;
	private JMenuItem mntmqkopendown;
	private JMenuItem mntmqkhuibuup;
	private JMenuItem mntmqkhuibudown;
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
		
//		quekouImage = new JPopupMenu ();
//		addPopup(this, quekouImage);
		
//		mntmqkopenup = new JMenuItem("统计新开向上跳空缺口");
//		mntmqkopendown = new JMenuItem("统计新开向下跳空缺口");
//		mntmqkhuibuup = new JMenuItem("统计回补上跳空缺口");
//		mntmqkhuibudown = new JMenuItem("统计回补下跳空缺口");
//		quekouImage.add(mntmqkopenup);
//		quekouImage.add(mntmqkopendown);
//		quekouImage.add(mntmqkhuibuup);
//		quekouImage.add(mntmqkhuibudown);

//		this.setComponentPopupMenu(quekouImage);

	
	}
	private  void addPopup(Component component, final JPopupMenu popup) 
	{
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}


}
