package com.exchangeinfomanager.bankuaifengxi.xmlhandlerforbkfx;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.TreeNode;

import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.InvisibleTreeModel;
import com.exchangeinfomanager.commonlib.ParseBanKuaiWeeklyFielGetBanKuaisProcessor;
import com.exchangeinfomanager.commonlib.ParseBanKuaiWeeklyFielGetStocksProcessor;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

import com.exchangeinfomanager.nodes.treerelated.BanKuaiTreeRelated;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class ServicesForBkfxEbkOutPutFileDirectRead implements ServicesForBkfxEbkOutPutFile 
{
	private File fileebk;
	private LocalDate edbfiledate;
	private Boolean resetoldrecord = true;

	public ServicesForBkfxEbkOutPutFileDirectRead ()
	{}

	@Override
	public LocalDate setBkfeOutPutFile(String ebkfile) 
	{
		if(ebkfile.endsWith("EBK")) {
			fileebk = new File( ebkfile );
			try { if (!fileebk.exists())	return null;
			} catch (Exception e) {	e.printStackTrace();	return null;}
		}
		
		edbfiledate = this.getBkfxFileDate();
		return edbfiledate;
	}
	public LocalDate getBkfxFileDate ()
	{
		LocalDate localDate = null;
		try{
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			
			String filenamedate = null;
			if(this.fileebk != null) {
				filenamedate = fileebk.getName().replaceAll("\\D+","");
				if(filenamedate.length() >8)	filenamedate = filenamedate.substring(0, 8);
			}

			localDate = LocalDate.parse(filenamedate, formatter);
			return localDate;
		} catch (java.time.format.DateTimeParseException e) {return null;}
	}
	public void resetBkfxFileDate (LocalDate localDate)
	{
		if(localDate != null)	this.edbfiledate = localDate.with(DayOfWeek.FRIDAY);
		else	this.edbfiledate = LocalDate.now().with(DayOfWeek.FRIDAY);
	}
	public LocalDate getCurBkfxSettingDate ()
	{
		return this.edbfiledate;
	}
	public void resetOldRecord (Boolean reornot)
	{
		this.resetoldrecord = reornot;
	}
	@Override
	public void patchOutPutFileToTrees(BanKuaiAndStockTree tree) 
	{
		List<String> readparsefileLines = null;
		try { //读出个股
			readparsefileLines = Files.readLines(fileebk,Charsets.UTF_8,new ParseBanKuaiWeeklyFielGetStocksProcessor ());
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> readparsefilegetbkLines = null;
		try {//读出板块
			readparsefilegetbkLines = Files.readLines(fileebk,Charsets.UTF_8,new ParseBanKuaiWeeklyFielGetBanKuaisProcessor ());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Set<String> stockinfile = new HashSet<String> (readparsefileLines);
		Set<String> bkinfile = new HashSet<String> (readparsefilegetbkLines);
		
		InvisibleTreeModel treeModel = (InvisibleTreeModel)tree.getModel();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treeModel.getRoot();
		patchParsedFileToTrees(treeroot,this.edbfiledate,stockinfile,bkinfile,this.resetoldrecord);
	}
	/*
	 * 
	 */
	private void patchParsedFileToTrees (BkChanYeLianTreeNode treeroot, LocalDate localDate, Set<String> stockinfile, Set<String> bkinfile, boolean removeoldrecord)
	{
		BkChanYeLianTreeNode treeChild;
		if(localDate == null) localDate  = LocalDate.now();
		
		for (Enumeration<TreeNode> child = treeroot.children(); child.hasMoreElements();) {
			
            treeChild = (BkChanYeLianTreeNode) child.nextElement();
            
            String nodecode = treeChild.getMyOwnCode();
            int nodetype = treeChild.getType();
            if( nodetype == BkChanYeLianTreeNode.TDXBK) {
            	BanKuaiTreeRelated treerelated = (BanKuaiTreeRelated)treeChild.getNodeTreeRelated ();
            		String colorcode = String.format("#%02x%02x%02x", Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getBlue() );
                	if(!bkinfile.contains(nodecode) && removeoldrecord)		treerelated.setSelfIsMatchModel(localDate, colorcode, false);
                	else   if(bkinfile.contains(nodecode) ) treerelated.setSelfIsMatchModel(localDate, colorcode,  true);
            } else 
            if( nodetype == BkChanYeLianTreeNode.TDXGG) {
            	NodesTreeRelated stofbktree = treeChild.getNodeTreeRelated();
            		String colorcode = String.format("#%02x%02x%02x", Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getBlue() );
                	if(!stockinfile.contains(nodecode) && removeoldrecord)		
                		stofbktree.setSelfIsMatchModel(localDate, colorcode, false);
                	else if(stockinfile.contains(nodecode) )  
                		stofbktree.setSelfIsMatchModel(localDate, colorcode,  true);
            }
  	          
	        patchParsedFileToTrees(treeChild,localDate,stockinfile,bkinfile,removeoldrecord);
        }
	}

}
