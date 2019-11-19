package com.exchangeinfomanager.nodes.treerelated;

import java.time.LocalDate;

public interface  NodesTreeRelated
{
	public void setSelfIsMatchModel (LocalDate selfinsetdate);
	public Boolean selfIsMatchModel (LocalDate selfinsetdate);
	public void setStocksNumInParsedFile (LocalDate parsefiledate, Integer stocksnum);
	public Integer getStocksNumInParsedFileForSpecificDate (LocalDate requiredate);
	
}
 	
