package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsPnl2;

import java.util.Collection;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;

public interface InformationTableModelBasic 
{
	void refresh (Collection<InsertedMeeting> meetings);
	InsertedMeeting getRequiredInformationOfTheRow(int modelRow);
	InsertedMeeting getLastestInformation();
	Integer getMainInforTypeForCreating ();
}
