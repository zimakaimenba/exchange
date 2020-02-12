package com.exchangeinfomanager.bankuaifengxi.xmlhandlerforbkfx;

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
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class ServicesForBkfxEbkOutPutFileDirectRead implements ServicesForBkfxEbkOutPutFile 
{
	private File fileebk;
	private SystemConfigration sysconfig;
	private LocalDate edbfiledate;

	public ServicesForBkfxEbkOutPutFileDirectRead ()
	{
		this.sysconfig = SystemConfigration.getInstance();
	}

	@Override
	public LocalDate setBkfeOutPutFile(String ebkfile) 
	{
		if(ebkfile.endsWith("EBK")) {
			fileebk = new File( ebkfile );
			try {
					if (!fileebk.exists()) 
						return null;
			} catch (Exception e) {
					e.printStackTrace();
					return null;
			}
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
			if(this.fileebk != null)
				filenamedate = fileebk.getName().replaceAll("\\D+","");
			
			localDate = LocalDate.parse(filenamedate, formatter);
			
			return localDate;
		} catch (java.time.format.DateTimeParseException e) {
				return null;
		}
	}
	public void resetBkfxFileDate (LocalDate localDate)
	{
		this.edbfiledate = localDate.with(DayOfWeek.FRIDAY);
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
		patchParsedFileToTrees(treeroot,this.edbfiledate,stockinfile,bkinfile);
	}
	
	
	
	private void patchParsedFileToTrees (BkChanYeLianTreeNode treeroot, LocalDate localDate, Set<String> stockinfile, Set<String> bkinfile)
	{
		BkChanYeLianTreeNode treeChild;
		
		for (Enumeration<TreeNode> child = treeroot.children(); child.hasMoreElements();) {
			
            treeChild = (BkChanYeLianTreeNode) child.nextElement();
            
            String nodecode = treeChild.getMyOwnCode();
            int nodetype = treeChild.getType();
            if( nodetype == BkChanYeLianTreeNode.TDXBK) {
            	BanKuaiTreeRelated treerelated = (BanKuaiTreeRelated)treeChild.getNodeTreeRelated ();
            	treerelated.setStocksNumInParsedFile (localDate, 0);
            	
            	if(!bkinfile.contains(nodecode))
            		treerelated.setSelfIsMatchModel(localDate, false);
            	else
            		treerelated.setSelfIsMatchModel(localDate, true);
            } else 
            if( nodetype == BkChanYeLianTreeNode.TDXGG) {
            	NodesTreeRelated stofbktree = treeChild.getNodeTreeRelated();

            	if(!stockinfile.contains(nodecode))
            		stofbktree.setSelfIsMatchModel(localDate,false);
            	else
            		stofbktree.setSelfIsMatchModel(localDate,true);
            }
  	          
	        patchParsedFileToTrees(treeChild,localDate,stockinfile,bkinfile);
        }
	}

}
