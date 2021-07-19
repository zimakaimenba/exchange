package com.exchangeinfomanager.Core.exportimportrelated;

import java.awt.Color;
import java.time.LocalDate;
import java.util.Collection;

public interface  NodesTreeRelated
{
	public static Color[] SUPPORTEDCOLORSLIST = {Color.YELLOW, Color.RED};
	public void setSelfIsMatchModel (LocalDate selfinsetdate, String colorcode,  Boolean inorout);
	public Boolean selfIsMatchModel (LocalDate selfinsetdate,String colorcode);
	public void setStocksNumInParsedFile (LocalDate parsefiledate, Integer stocksnum);
	public Integer getStocksNumInParsedFileForSpecificDate (LocalDate requiredate);
	Collection<String> selfIsMatchModelSet(LocalDate selfinsetdate);
	Boolean selfIsMatchModel(LocalDate selfinsetdate, Color color);
	void setSelfIsMatchModel(LocalDate selfinsetdate, Color color, Boolean inorout);
}
 	
