package com.exchangeinfomanager.nodes.treeservices;

import java.util.ArrayList;
import java.util.List;

import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.Stock;

public interface BanKuaiAndStockServices 
{

	List<BanKuai> getAllTDXBanKuai();
	List<Stock> getAllTDXStocks();

}
