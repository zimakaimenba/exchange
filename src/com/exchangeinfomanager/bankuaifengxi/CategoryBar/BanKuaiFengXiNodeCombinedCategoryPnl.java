package com.exchangeinfomanager.bankuaifengxi.CategoryBar;


import java.awt.Color;
import java.awt.Component;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.TitledBorder;

import org.jfree.chart.renderer.category.CategoryItemRenderer;

import com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelHightLightColumnListener;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjePnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjeZhanbiPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditionListener;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditions;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;

public class BanKuaiFengXiNodeCombinedCategoryPnl extends JPanel 
		implements BarChartPanelDataChangedListener, BarChartPanelHightLightColumnListener ,PropertyChangeListener,BanKuaiAndGeGuMatchingConditionListener
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
		this.setName(cjeorcjl);
		
		initializeSystem ();
	}
	public BanKuaiFengXiNodeCombinedCategoryPnl(String cjeorcjl)
	{
		this.horizonlayout = false;
		this.pnltype = cjeorcjl;
		this.setName(cjeorcjl);
		
		initializeSystem ();
	}
	private void initializeSystem ()
	{
		createGui ();
		
		chartpanelhighlightlisteners = new HashSet<BarChartPanelHightLightColumnListener> ();
		chartpanelhighlightlisteners.add(cjecjlpnlup);
		chartpanelhighlightlisteners.add(cjecjlzbpnldown);
			
		cjecjlpnlup.addPropertyChangeListener(this);
		cjecjlzbpnldown.addPropertyChangeListener(this);

		createEvents();
	}
	
	
	public static final String SELECTED_PROPERTY = "selected";
	protected boolean selectchanged;
	private boolean horizonlayout;
	private String pnltype = "CJE"; //cje or cjl pnl?
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this); //	https://stackoverflow.com/questions/4690892/passing-a-value-between-components/4691447#4691447
	private String period;
	private Properties prop;
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	        pcs.addPropertyChangeListener(listener);
	}
	/*
	 * 
	 */
	public LocalDate getCurSelectedDate ()
	{
		return cjecjlzbpnldown.getCurSelectedDate ();
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
	public void addMenuItem (JMenuItem menuitem,Integer position)
	{
		if(position == null) 
			cjecjlzbpnldown.getPopupMenu().add(menuitem);
		else
			cjecjlzbpnldown.getPopupMenu().add(menuitem,position);
	}

	@Override
	public void updatedDate(TDXNodes node, LocalDate startdate,LocalDate enddate,String period) 
	{
		this.setBorder(new TitledBorder(null, node.getMyOwnCode()+node.getMyOwnName(), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.curdisplayednode = node;
		this.period = period;
		
		cjecjlpnlup.updatedDate(node, startdate,enddate, period);
		cjecjlzbpnldown.updatedDate(node, startdate,enddate, period);
	}
	@Override
	public void highLightSpecificBarColumn(LocalDate selecteddate) 
	{
		chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(selecteddate));
	}
	@Override
	public void highLightSpecificBarColumn(Integer columnindex) 
	{
		
	}
	/*
	 * 
	 */
	public void resetData ()
	{
		cjecjlzbpnldown.resetDate();
		cjecjlpnlup.resetDate();
	}
	/*
	 * 
	 */
	public void setAllowDrawAnnoation (Boolean allowdraw)
	{
		cjecjlzbpnldown.setAllowDrawAnnoation(allowdraw);
		cjecjlpnlup.setAllowDrawAnnoation(allowdraw);
	}
	/*
	 * 
	 */
	public void setDrawQueKouLine (Boolean draw)
	{
		cjecjlpnlup.setDrawQueKouLine(draw);
		cjecjlzbpnldown.setDrawQueKouLine(draw);
	}
	/*
	 * 
	 */
	public void setDrawZhangDieTingLine (Boolean draw)
	{
		cjecjlpnlup.setDrawZhangDieTingLine(draw) ;
		cjecjlzbpnldown.setDrawZhangDieTingLine(draw) ;
	}
	public void setDisplayZhanBiInLine (Boolean draw)
	{
//		cjecjlpnlup.setDisplayZhanBiInLine(draw) ;
		if(this.pnltype.equalsIgnoreCase("CJE"))
			((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjecjlzbpnldown).setDisplayZhanBiInLine(draw) ;
		else
			((BanKuaiFengXiCategoryBarChartCjlZhanbiPnl)cjecjlzbpnldown).setDisplayZhanBiInLine(draw) ;
	}
	public void setDrawAverageDailyCjeOfWeekLine  (Boolean draw)
	{
		if(this.pnltype.toUpperCase().equals("CJE"))
			((BanKuaiFengXiCategoryBarChartCjePnl)cjecjlpnlup).setDrawAverageDailyCjeOfWeekLine(draw) ;
		else
			((BanKuaiFengXiCategoryBarChartCjlPnl)cjecjlpnlup).setDrawAverageDailyCjlOfWeekLine(draw) ;
	}
	/*
	 * 
	 */
	public void setDisplayBarOfSpecificBanKuaiCjeCjlInsteadOfSelfCjeCjl (TDXNodes supernode) 
	{
		if(this.pnltype.toUpperCase().equals("CJE"))
			((BanKuaiFengXiCategoryBarChartCjePnl)cjecjlpnlup).setDisplayBarOfSpecificBanKuaiCjeCjlInsteadOfSelfCjeCjl( supernode);
		else
			((BanKuaiFengXiCategoryBarChartCjlPnl)cjecjlpnlup).setDisplayBarOfSpecificBanKuaiCjeCjlInsteadOfSelfCjeCjl( supernode);
	}
	/*
	 * 
	 */
	public void setProperties (Properties prop1)
	{
		this.prop = prop1;
		CategoryItemRenderer rendererup = ((BanKuaiFengXiCategoryBarChartPnl)cjecjlpnlup).plot.getRenderer();
		((BanKuaiFengXiCategoryBarRenderer)rendererup).setProperties(prop1);
		
		CategoryItemRenderer rendererdown = ((BanKuaiFengXiCategoryBarChartPnl)cjecjlzbpnldown).plot.getRenderer();
		((BanKuaiFengXiCategoryBarRenderer)rendererdown).setProperties(prop1);
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
		LocalDate startdate = cjecjlpnlup.getCurrentDisplayOhlcStartDate ();
		LocalDate enddate = cjecjlpnlup.getCurrentDisplayOhlcEndDate ();
		
		Object source = evt.getSource();
		String evtpropertyname = evt.getPropertyName();
//		String indictor = (String) evt.getNewValue();
		switch (evtpropertyname) {
		case BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY:
			if(evt.getNewValue() != null ) {
				String selectedinfo = evt.getNewValue().toString();
				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(LocalDate.parse(selectedinfo) ) );
	    		//特别标记点击的日期，这样界面上看比较清晰
	    		if(cjecjlzbpnldown.isAllowDrawAnnoation()) {
					cjecjlzbpnldown.setAnnotations( LocalDate.parse(selectedinfo)  );
					cjecjlpnlup.setAnnotations( LocalDate.parse(selectedinfo)  );
				}
			}
			
			PropertyChangeEvent evtnew = new PropertyChangeEvent(source, SELECTED_PROPERTY, "", evt.getNewValue() );
			pcs.firePropertyChange(evtnew);
			break;
		case BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY:
			pcs.firePropertyChange(evt);
			break;
		case BanKuaiFengXiCategoryBarChartPnl.DISPLAYZHANGDIETING :
			cjecjlpnlup.resetLineDate ();
			cjecjlzbpnldown.resetLineDate();
			cjecjlpnlup.resetDate();
			cjecjlzbpnldown.resetDate();
			
			((BanKuaiFengXiCategoryBarChartCjePnl)cjecjlpnlup).displayBarDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
			((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjecjlzbpnldown).displayBarDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
			
			((BanKuaiFengXiCategoryBarChartCjePnl)cjecjlpnlup).displayZhangDieTingLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
			((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjecjlzbpnldown).displayZhangDieTingLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),period);
			break;
		case BanKuaiFengXiCategoryBarChartPnl.DISPLAYQUEKOUDATA:
			cjecjlpnlup.resetLineDate ();
			cjecjlzbpnldown.resetLineDate();
			cjecjlpnlup.resetDate();
			cjecjlzbpnldown.resetDate();
			
			((BanKuaiFengXiCategoryBarChartCjePnl)cjecjlpnlup).displayBarDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
			((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjecjlzbpnldown).displayBarDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
			
			((BanKuaiFengXiCategoryBarChartCjePnl)cjecjlpnlup).displayQueKouLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
			((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjecjlzbpnldown).displayQueKouLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),period);
			break;
		case BanKuaiFengXiCategoryBarChartPnl.ONLYSHOWCJEZBBARDATA:
//			cjecjlpnlup.resetLineDate ();
			cjecjlzbpnldown.resetLineDate();
//			cjecjlpnlup.resetDate();
			cjecjlzbpnldown.resetDate();
			
//			((BanKuaiFengXiCategoryBarChartCjePnl)cjecjlpnlup).displayBarDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
			((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjecjlzbpnldown).displayBarDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
			break;
		case BanKuaiFengXiCategoryBarChartPnl.CJEZBTOLINE:
			String indictor = (String) evt.getNewValue();
			if(indictor.equals("notcjecjlzbtoline")) {
//				cjecjlpnlup.setDisplayZhanBiInLine (false);
//				((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjecjlzbpnldown).setDisplayZhanBiInLine (false);
			} else {
//				cjecjlpnlup.setDisplayZhanBiInLine (true);
//				((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjecjlzbpnldown).setDisplayZhanBiInLine (true);
				
//				cjecjlpnlup.resetLineDate ();
				cjecjlzbpnldown.resetLineDate();
				
				if(this.pnltype.equals("CJE")) {
//					((BanKuaiFengXiCategoryBarChartCjePnl)cjecjlpnlup).dipalyCjeCjlZBLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
					((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjecjlzbpnldown).dipalyCjeZBLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),period);
				}
			}
			break;
		case BanKuaiFengXiCategoryBarChartPnl.AVERAGEDAILYCJE:
			cjecjlpnlup.resetLineDate ();
//			cjecjlzbpnldown.resetLineDate();
			cjecjlpnlup.resetDate();
//			cjecjlzbpnldown.resetDate();
			
			((BanKuaiFengXiCategoryBarChartCjePnl)cjecjlpnlup).displayBarDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
			((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjecjlzbpnldown).displayBarDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
			
			((BanKuaiFengXiCategoryBarChartCjePnl)cjecjlpnlup).displayAverageDailyCjeOfWeekLineDataToGuiUsingRightAxix(this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
//			((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjecjlzbpnldown).dipalyCjeCjlZBLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),period);
			break;
		case BanKuaiFengXiCategoryBarChartPnl.COMPAREAVERAGEDAILYCJEWITHDAPAN :
			cjecjlpnlup.resetLineDate ();
			cjecjlpnlup.resetDate();
			
			 ((BanKuaiFengXiCategoryBarRenderer)((BanKuaiFengXiCategoryBarChartPnl)cjecjlpnlup).plot.getRenderer()).setBarDisplayedColor( "CjeAverageColumnColor" );
			
			TDXNodes shouldDisplayBarOfSuperBanKuaiCjeInsteadOfSelfCje = ((BanKuaiFengXiCategoryBarChartCjePnl)cjecjlpnlup).getSettingSpecificSuperBanKuai();
			if(shouldDisplayBarOfSuperBanKuaiCjeInsteadOfSelfCje == null) {
				DaPan treeroot = (DaPan) this.curdisplayednode.getRoot();
				((BanKuaiFengXiCategoryBarChartCjePnl)cjecjlpnlup).setDisplayBarOfSpecificBanKuaiCjeCjlInsteadOfSelfCjeCjl (treeroot);
				shouldDisplayBarOfSuperBanKuaiCjeInsteadOfSelfCje = treeroot;
			}
			NodeXPeriodData nodexdataOfSuperBk = shouldDisplayBarOfSuperBanKuaiCjeInsteadOfSelfCje.getNodeXPeroidData(period);
			Double leftrangeaxix = ((BanKuaiFengXiCategoryBarChartCjePnl)cjecjlpnlup).displayAverageBarDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
			Double avecjeaxix = ((BanKuaiFengXiCategoryBarChartCjePnl)cjecjlpnlup).displayAverageDailyCjeOfWeekLineDataToGuiUsingRightAxix(nodexdataOfSuperBk ,startdate,enddate,period);
			break;
		case BanKuaiFengXiCategoryBarChartPnl.AVERAGEDAILYCJL:
			cjecjlpnlup.resetLineDate ();
//			cjecjlzbpnldown.resetLineDate();
			cjecjlpnlup.resetDate();
//			cjecjlzbpnldown.resetDate();
			
			((BanKuaiFengXiCategoryBarChartCjlPnl)cjecjlpnlup).displayBarDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
//			((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)cjecjlzbpnldown).displayBarDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
			
			((BanKuaiFengXiCategoryBarChartCjlPnl)cjecjlpnlup).displayAverageDailyCjlOfWeekLineDataToGuiUsingRightAxix(this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
//			((BanKuaiFengXiCategoryBarChartCjlZhanbiPnl)cjecjlzbpnldown).diplayCjlCjlZBLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),period);
			break;
		case BanKuaiFengXiCategoryBarChartPnl.COMPAREAVERAGEDAILYCJLWITHDAPAN:
			cjecjlpnlup.resetLineDate ();
			cjecjlpnlup.resetDate();
			
			((BanKuaiFengXiCategoryBarRenderer)((BanKuaiFengXiCategoryBarChartPnl)cjecjlpnlup).plot.getRenderer()).setBarDisplayedColor( "CjlAverageColumnColor" );
//			 ((BanKuaiFengXiCategoryBarChartCjlPnl)cjecjlpnlup).setBarDisplayedColor(new Color(204,155,153) );
			
			TDXNodes shouldDisplayBarOfSuperBanKuaiCjlInsteadOfSelfCjl = ((BanKuaiFengXiCategoryBarChartCjlPnl)cjecjlpnlup).getSettingSpecificSuperBanKuai();
			if(shouldDisplayBarOfSuperBanKuaiCjlInsteadOfSelfCjl == null) {
				DaPan treeroot = (DaPan) this.curdisplayednode.getRoot();
				((BanKuaiFengXiCategoryBarChartCjlPnl)cjecjlpnlup).setDisplayBarOfSpecificBanKuaiCjeCjlInsteadOfSelfCjeCjl (treeroot);
				shouldDisplayBarOfSuperBanKuaiCjlInsteadOfSelfCjl = treeroot;
			}
			NodeXPeriodData nodexdataOfSuperBk2 = shouldDisplayBarOfSuperBanKuaiCjlInsteadOfSelfCjl.getNodeXPeroidData(period);
			Double leftrangeaxix2 = ((BanKuaiFengXiCategoryBarChartCjlPnl)cjecjlpnlup).displayAverageBarDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
			Double avecjeaxix2 = ((BanKuaiFengXiCategoryBarChartCjlPnl)cjecjlpnlup).displayAverageDailyCjlOfWeekLineDataToGuiUsingRightAxix(nodexdataOfSuperBk2 ,startdate,enddate,period);
			break;
		case BanKuaiFengXiCategoryBarChartPnl.CJLZBTOLINE:
			String indictor2 = (String) evt.getNewValue();
			if(indictor2.equals("notcjlcjlzbtoline")) {
//				cjecjlpnlup.setDisplayZhanBiInLine (false);
				((BanKuaiFengXiCategoryBarChartCjlZhanbiPnl)cjecjlzbpnldown).setDisplayZhanBiInLine (false);
			} else {
//				cjecjlpnlup.setDisplayZhanBiInLine (true);
				((BanKuaiFengXiCategoryBarChartCjlZhanbiPnl)cjecjlzbpnldown).setDisplayZhanBiInLine (true);
				
//				cjecjlpnlup.resetLineDate ();
				cjecjlzbpnldown.resetLineDate();
				
				if(this.pnltype.equals("CJL")) {
//					((BanKuaiFengXiCategoryBarChartCjePnl)cjecjlpnlup).dipalyCjeCjlZBLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
					((BanKuaiFengXiCategoryBarChartCjlZhanbiPnl)cjecjlzbpnldown).dipalyCjlZBLineDataToGui (this.curdisplayednode.getNodeXPeroidData(period),period);
				}
			}
			break;
		case BanKuaiFengXiCategoryBarChartPnl.ONLYSHOWCJLZBBARDATA:
			cjecjlpnlup.resetLineDate ();
			cjecjlzbpnldown.resetLineDate();
			cjecjlpnlup.resetDate();
			cjecjlzbpnldown.resetDate();
			
			((BanKuaiFengXiCategoryBarChartCjlPnl)cjecjlpnlup).displayBarDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
			((BanKuaiFengXiCategoryBarChartCjlZhanbiPnl)cjecjlzbpnldown).displayBarDataToGui (this.curdisplayednode.getNodeXPeroidData(period),startdate,enddate,period);
			break;
		case BanKuaiFengXiCategoryBarChartPnl.DISPLAYNODENEWRANGEDATA:
			cjecjlpnlup.resetLineDate ();
			cjecjlzbpnldown.resetLineDate();
			cjecjlpnlup.resetDate();
			cjecjlzbpnldown.resetDate();
			
			String indictor3 = (String) evt.getNewValue();
			cjecjlpnlup.reDisplayNodeDataOnDirection (indictor3);
			cjecjlzbpnldown.reDisplayNodeDataOnDirection(indictor3 );
			
			pcs.firePropertyChange(evt);
			break;
		}
	}
	
	private TDXNodes curdisplayednode;
	private BanKuaiFengXiCategoryBarChartPnl cjecjlpnlup;
	private BanKuaiFengXiCategoryBarChartPnl cjecjlzbpnldown;
	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
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
			cjecjlpnlup = new BanKuaiFengXiCategoryBarChartCjePnl ();
			this.add(cjecjlpnlup);
			
			cjecjlzbpnldown = new BanKuaiFengXiCategoryBarChartCjeZhanbiPnl ();
			this.add(cjecjlzbpnldown);
		} else if(this.pnltype.toUpperCase().equals("CJL")) {
			cjecjlpnlup = new BanKuaiFengXiCategoryBarChartCjlPnl ();
			this.add(cjecjlpnlup);
			
			cjecjlzbpnldown = new BanKuaiFengXiCategoryBarChartCjlZhanbiPnl ();
			this.add(cjecjlzbpnldown);
		} 
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
	@Override
	public void setAnnotations(LocalDate columnkey) 
	{
		if(cjecjlzbpnldown.isAllowDrawAnnoation()) {
			cjecjlzbpnldown.setAnnotations( columnkey  );
			cjecjlpnlup.setAnnotations( columnkey  );
		}
		
	}
	public void setChangeNodeDisplayDateRange(boolean b) {
		cjecjlzbpnldown.setChangeNodeDisplayDateRange( b  );
		cjecjlpnlup.setChangeNodeDisplayDateRange( b  );
	}
	@Override
	public void BanKuaiAndGeGuMatchingConditionValuesChanges(BanKuaiAndGeGuMatchingConditions expc) 
	{
		cjecjlzbpnldown.BanKuaiAndGeGuMatchingConditionValuesChanges(expc) ;
		cjecjlpnlup.BanKuaiAndGeGuMatchingConditionValuesChanges(expc) ;
		
	}


}
