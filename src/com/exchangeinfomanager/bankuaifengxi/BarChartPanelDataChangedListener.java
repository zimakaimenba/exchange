package com.exchangeinfomanager.bankuaifengxi;

import java.time.LocalDate;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;


/*
 * 
 */
public interface BarChartPanelDataChangedListener 
{
//	public void updatedDate (TDXNodes node, LocalDate date, int difference,String period);
	public void updatedDate (TDXNodes node, LocalDate startdate, LocalDate enddate,String period);
	public void setAnnotations (LocalDate columnkey);
}
