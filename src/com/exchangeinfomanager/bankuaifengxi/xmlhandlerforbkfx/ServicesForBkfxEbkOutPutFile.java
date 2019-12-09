package com.exchangeinfomanager.bankuaifengxi.xmlhandlerforbkfx;

import java.io.File;
import java.time.LocalDate;

import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public interface ServicesForBkfxEbkOutPutFile 
{
//	void setBkfeOutPutFile (String ebkfile);
//	File getBkfxXmlFiel ();
	
	void patchOutPutFileToTrees (BanKuaiAndStockTree treeroot);
	void resetBkfxFileDate ();

//	void setBkfeOutPutFile(String ebkfile, Boolean usingfiledate);

	LocalDate setBkfeOutPutFile(String ebkfile);
	
//	void parseWeeklyBanKuaiFengXiFileToXmlAndPatchToCylTree (BkChanYeLianTreeNode treeroot);

}
