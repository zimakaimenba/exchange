package com.exchangeinfomanager.gudong;

import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.nodes.Stock;

public interface ServicesForJiGou 
{
	public void addJiGou (String jigouname);
	public Collection<Stock> getJiGouStock(String jigouname, LocalDate requiredstart, LocalDate requiredend);
}
