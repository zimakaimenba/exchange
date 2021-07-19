package com.exchangeinfomanager.bankuaifengxi.xmlhandlerforbkfx;

import java.io.File;
import java.time.LocalDate;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Trees.BanKuaiAndStockTree;

public interface ServicesForBkfxEbkOutPutFile 
{
//	void setBkfeOutPutFile (String ebkfile);
//	File getBkfxXmlFiel ();
	
	void patchOutPutFileToTrees (BanKuaiAndStockTree treeroot);
	void resetBkfxFileDate (LocalDate date);

//	void setBkfeOutPutFile(String ebkfile, Boolean usingfiledate);

	LocalDate setBkfeOutPutFile(String ebkfile);
	
//	void parseWeeklyBanKuaiFengXiFileToXmlAndPatchToCylTree (BkChanYeLianTreeNode treeroot);

}
