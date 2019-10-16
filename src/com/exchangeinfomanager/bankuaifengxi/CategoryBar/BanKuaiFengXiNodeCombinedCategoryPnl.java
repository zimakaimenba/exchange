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
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

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
	 * @param cjeorcjl 
	 */
	public BanKuaiFengXiNodeCombinedCategoryPnl(String horizonorvertical, String cjeorcjl)
	{
		if(horizonorvertical != null && horizonorvertical.toLowerCase().equals("horizon"))
			this.horizonlayout = true;
		else 
			this.horizonlayout = false;
		
		this.pnltype = cjeorcjl;
		
		initializeSystem ();
	}
	public BanKuaiFengXiNodeCombinedCategoryPnl(String cjeorcjl)
	{
		this.horizonlayout = false;
		this.pnltype = cjeorcjl;
		
		initializeSystem ();
	}
	private void initializeSystem ()
	{
		createGui ();
		
//		if(this.pnltype.toUpperCase().equals("CJE")) {
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
	private String pnltype = "CJE"; //cje or cjl pnl?
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this); //	https://stackoverflow.com/questions/4690892/passing-a-value-between-components/4691447#4691447
	private String period;
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	        pcs.addPropertyChangeListener(listener);
	}
	/*
	 * 
	 */
	public LocalDate getCurSelectedDate ()
	{
		return cjezblargepnl.getCurSelectedDate ();
	}
	/*
	 * 
	 */
	public TDXNodes getCurDisplayedNode ()
	{
		return this.curdisplayednode;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener#updatedDate(com.exchangeinfomanager.nodes.TDXNodes, java.time.LocalDate, java.time.LocalDate, java.lang.String)
	 */
	public void addMenuItem (JMenuItem menuitem,String position)
	{
		if(position == null) {
			cjezblargepnl.getPopupMenu().add(menuitem);
//			cjelargepnl.getPopupMenu().add(menuitem);
			
		}
	}

	@Override
	public void updatedDate(TDXNodes node, LocalDate startdate,LocalDate enddate,String period) 
	{
		this.setBorder(new TitledBorder(null, node.getMyOwnCode()+node.getMyOwnName(), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.curdisplayednode = node;
		this.period = period;
		
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
	public void setAllowDrawAnnoation (Boolean allowdraw)
	{
		cjezblargepnl.setAllowDrawAnnoation(allowdraw);
		cjelargepnl.setAllowDrawAnnoation(allowdraw);
	}
	/*
	 * 
	 */
	public void setDrawQueKouLine (Boolean draw)
	{
		cjelargepnl.setDrawQueKouLine(draw);
		cjezblargepnl.setDrawQueKouLine(draw);
	}
	/*
	 * 
	 */
	public void setDrawZhangDieTingLine (Boolean draw)
	{
		cjelargepnl.setDrawZhangDieTingLine(draw) ;
		cjezblargepnl.setDrawZhangDieTingLine(draw) ;
	}
	public void setDisplayZhanBiInLine (Boolean draw)
	{
		cjelargepnl.setDisplayZhanBiInLine(draw) ;
		cjezblargepnl.setDisplayZhanBiInLine(draw) ;
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
		String evtpropertyname = evt.getPropertyName();
		if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
			
			String selectedinfo = evt.getNewValue().toString();
			chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(LocalDate.parse(selectedinfo) ) );
    		//特别标记点击的日期，这样界面上看比较清晰
    		if(cjezblargepnl.isAllowDrawAnnoation()) {
				cjezblargepnl.setAnnotations( LocalDate.parse(selectedinfo)  );
				cjelargepnl.setAnnotations( LocalDate.parse(selectedinfo)  );
			}
			PropertyChangeEvent evtnew = new PropertyChangeEvent(this, SELECTED_PROPERTY, "", selectedinfo );
			pcs.firePropertyChange(evtnew);
			
		} else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY)) {
	        pcs.firePropertyChange(evt);
		
		} else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.DISPLAYZHANGDIETING ) ) {
			cjelargepnl.resetLineDate ();
			cjezblargepnl.resetLineDate();
			((BanKuaiFengXiCategoryBarChartCjePnl)cjelargepnl).displayZhangDieTingLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),period);
			((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjezblargepnl).displayZhangDieTingLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),period);

		} else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.DISPLAYQUEKOUDATA ) ) {
			cjelargepnl.resetLineDate ();
			cjezblargepnl.resetLineDate();
			((BanKuaiFengXiCategoryBarChartCjePnl)cjelargepnl).displayQueKouLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),period);
			((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjezblargepnl).displayQueKouLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),period);
		} else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.ONLYSHOWBARDATA ) ) {
			cjelargepnl.resetLineDate ();
			cjezblargepnl.resetLineDate();
			
			((BanKuaiFengXiCategoryBarRenderer)cjelargepnl.plot.getRenderer()).unhideBarMode();
			((BanKuaiFengXiCategoryBarRenderer)cjezblargepnl.plot.getRenderer()).unhideBarMode();
			
		} else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.CJECJLZBTOLINE ) ) {
			cjelargepnl.resetLineDate ();
			cjezblargepnl.resetLineDate();
			
			if(this.pnltype.equals("CJE")) {
				((BanKuaiFengXiCategoryBarChartCjePnl)cjelargepnl).dipalyCjeCjlZBLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),period);
				((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjezblargepnl).dipalyCjeCjlZBLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),period);
			}
		} else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.AVERAGEDAILYCJE ) ) {
			cjelargepnl.resetLineDate ();
			cjezblargepnl.resetLineDate();
			
			((BanKuaiFengXiCategoryBarChartCjePnl)cjelargepnl).displayAverageDailyCjeOfWeekLineDataToGui(this.curdisplayednode.getNodeXPeroidData(period),period);
			((BanKuaiFengXiCategoryBarRenderer)cjezblargepnl.plot.getRenderer()).unhideBarMode();
//			((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjezblargepnl).displayAverageDailyCjeOfWeekLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),period);
		}
		
		
		
	}
	
	private TDXNodes curdisplayednode;
	private BanKuaiFengXiCategoryBarChartPnl cjelargepnl;
	private BanKuaiFengXiCategoryBarChartPnl cjezblargepnl;
	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
//	private JPopupMenu quekouImage;
//	private JMenuItem mntmqkopenup;
//	private JMenuItem mntmqkopendown;
//	private JMenuItem mntmqkhuibuup;
//	private JMenuItem mntmqkhuibudown;
	/*
	 * 
	 */
	private void createGui() 
	{
		if(this.horizonlayout)
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		else
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		if(this.pnltype.toUpperCase().equals("CJE")) {
			cjelargepnl = new BanKuaiFengXiCategoryBarChartCjePnl ();
			this.add(cjelargepnl);
			
			cjezblargepnl = new BanKuaiFengXiCategoryBarChartCjeZhanbiPnl ();
			this.add(cjezblargepnl);
		} else if(this.pnltype.toUpperCase().equals("CJL")) {
			cjelargepnl = new BanKuaiFengXiCategoryBarChartCjlPnl ();
			this.add(cjelargepnl);
			
			cjezblargepnl = new BanKuaiFengXiCategoryBarChartCjlZhanbiPnl ();
			this.add(cjezblargepnl);
		} 
		
		
		
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
