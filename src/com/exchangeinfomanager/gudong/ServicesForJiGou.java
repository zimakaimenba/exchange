package com.exchangeinfomanager.gudong;

import com.exchangeinfomanager.nodes.Stock;

public interface ServicesForJiGou 
{
	public void addJiGou (String jigouname);
	public Stock getStockGuDong (Stock stock, Boolean onlyimporjigougudong,  Boolean forcetorefrshallgudong);
}
