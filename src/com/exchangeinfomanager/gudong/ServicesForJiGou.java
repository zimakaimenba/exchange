package com.exchangeinfomanager.gudong;

import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.Core.Nodes.Stock;

public interface ServicesForJiGou 
{
	public void addJiGou (String jigouname ,String jigouquancheng, String jigoushuoming, Boolean hqgq, Boolean mxjj);
	public Collection<Stock> getJiGouStock(String jigouname, LocalDate requiredstart, LocalDate requiredend);
}
