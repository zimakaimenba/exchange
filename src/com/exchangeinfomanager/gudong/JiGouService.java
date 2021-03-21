package com.exchangeinfomanager.gudong;

import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.database.JiGouGuDongDbOperation;
import com.exchangeinfomanager.nodes.Stock;


public class JiGouService implements ServicesForJiGou
{
	JiGouGuDongDbOperation dbopt;
	
	public JiGouService ()
	{
		dbopt = new JiGouGuDongDbOperation ();
	}
	
	@Override
	public void addJiGou(String jigouname, String jigouquancheng, String jigoushuoming, Boolean hqgq, Boolean mxjj) 
	{
		dbopt.storeJiGouToDb (jigouname, jigouquancheng, jigoushuoming, hqgq, mxjj);
		return;
	}

	@Override
	public Collection<Stock> getJiGouStock(String jigouname, LocalDate requiredstart, LocalDate requiredend) {
		// TODO Auto-generated method stub
		return null;
	}

}
