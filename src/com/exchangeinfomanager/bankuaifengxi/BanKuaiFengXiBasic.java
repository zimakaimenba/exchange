package com.exchangeinfomanager.bankuaifengxi;

import java.util.Set;

import javax.swing.JTextArea;

public abstract class BanKuaiFengXiBasic 
{
	protected Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
	protected Set<BarChartPanelDataChangedListener> barchartpanelbankuaidatachangelisteners;
	protected Set<BarChartPanelDataChangedListener> piechartpanelbankuaidatachangelisteners;
	protected Set<BarChartPanelDataChangedListener> barchartpanelstockofbankuaidatachangelisteners;
	protected Set<BarChartPanelDataChangedListener> barchartpanelstockdatachangelisteners;
	protected Set<BkfxHightLightValueListener> bkfxhighlightvalueslisteners;
	
	protected JTextArea tfldselectedmsg ;
}
