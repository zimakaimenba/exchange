package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsPnl2;

import java.util.Collection;

import com.exchangeinfomanager.News.News;


public interface InformationTableModelBasic 
{
	void refresh (Collection<News> meetings);
	News getRequiredInformationOfTheRow(int modelRow);
	News getLastestInformation();
	Integer getMainInforTypeForCreating ();
}
