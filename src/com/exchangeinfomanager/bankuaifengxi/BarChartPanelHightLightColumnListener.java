package com.exchangeinfomanager.bankuaifengxi;

import java.time.LocalDate;
import java.util.Set;

public interface BarChartPanelHightLightColumnListener 
{
	void highLightSpecificBarColumn (LocalDate selecteddate);
	void highLightSpecificBarColumn (Integer columnindex);
//	void setHightLightColumnListeners ( Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners);
}
