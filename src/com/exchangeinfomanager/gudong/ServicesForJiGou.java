package com.exchangeinfomanager.gudong;

import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.nodes.Stock;

public interface ServicesForJiGou 
{
	public void addJiGou (String jigouname ,Boolean hqgq, Boolean mxjj);
	public Collection<Stock> getJiGouStock(String jigouname, LocalDate requiredstart, LocalDate requiredend);
}
