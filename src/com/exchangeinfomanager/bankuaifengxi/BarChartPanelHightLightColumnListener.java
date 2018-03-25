package com.exchangeinfomanager.bankuaifengxi;

import java.util.Set;

public interface BarChartPanelHightLightColumnListener 
{
	void highLightSpecificBarColumn (Comparable<String> selecteddate);
	void highLightSpecificBarColumn (Integer columnindex);
	void setHightLightColumnListeners ( Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners);
}
