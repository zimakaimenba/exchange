package com.exchangeinfomanager.bankuaifengxi.xmlhandlerforbkfx;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Core.Trees.InvisibleTreeModel;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;

public class ServiceOfBkFxEbkXml implements ServicesForBkfxEbkOutPutFile
{

	private SetupSystemConfiguration sysconfig;
	private File xmlfile;
	private File fileebk;
	
	private BkfxWeeklyFileResultXmlHandler bkfxfh;
	
	public ServiceOfBkFxEbkXml ()
	{
		this.sysconfig = new SetupSystemConfiguration();
	}

	@Override
	public LocalDate setBkfeOutPutFile(String ebkfile) 
	{
		if(ebkfile.endsWith("XML")) 
			this.setBkFxXmlFile(ebkfile);
		
		if(ebkfile.endsWith("EBK")) {
			fileebk = new File( ebkfile );
			String exportxmlfilename = sysconfig.getTDXModelMatchExportFile () +  fileebk.getName();
			String xmlfilename = exportxmlfilename.replace(".EBK", ".XML");
			xmlfile = new File(xmlfilename);
			try {
					if (!xmlfile.exists()) 
						xmlfile = null;
			} catch (Exception e) {
					e.printStackTrace();
					return null;
			}
		}
		return null;
		
	}

	public String setBkFxXmlFile (String xmlfile)
	{
		File filexminconfigpath = new File(xmlfile);
		Path inputfilepath = FileSystems.getDefault().getPath(filexminconfigpath.getParent());
		Path systemrequiredpath = FileSystems.getDefault().getPath(sysconfig.getTDXModelMatchExportFile ());
		try {
			boolean cresult = java.nio.file.Files.isSameFile(systemrequiredpath, inputfilepath);
			if(!cresult ) {
				return "XML文件位置错误！请在指定的目录里重新选择文件！";
			} else {
				this.xmlfile = filexminconfigpath;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xmlfile;
	}
	
	public LocalDate getBkfxFileDate ()
	{
		LocalDate localDate = null;
		try{
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			
			String filenamedate;
			if(this.fileebk != null)
				filenamedate = fileebk.getName().replaceAll("\\D+","");
			else
				filenamedate = this.xmlfile.getName().replaceAll("\\D+","");
			localDate = LocalDate.parse(filenamedate, formatter);
			
			return localDate;
		} catch (java.time.format.DateTimeParseException e) {
				return null;
		}
	}

	public File getBkfxXmlFiel() 
	{
		// TODO Auto-generated method stub
		return this.xmlfile;
	}

	@Override
	public void patchOutPutFileToTrees(BanKuaiAndStockTree tree)
	{
		if(this.xmlfile == null)
			createXmlAndPatchOutPutFileToTree ();
			
		this.bkfxfh.setXmlRootFileForBkfxWeeklyFile(this.xmlfile);
		
		InvisibleTreeModel treeModel = (InvisibleTreeModel)tree.getModel();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treeModel.getRoot();
		this.bkfxfh.patchParsedResultXmlToTrees (treeroot,this.getBkfxFileDate () );
		
		tree.setCurrentDisplayedWk (this.getBkfxFileDate () );
	}
	
	private void createXmlAndPatchOutPutFileToTree ()
	{
		this.bkfxfh.parseWeeklyBanKuaiFengXiFileToXmlAndPatchToCylTree (this.fileebk,this.getBkfxFileDate() );
	}

	@Override
	public void resetBkfxFileDate(LocalDate date) {
		// TODO Auto-generated method stub
		
	}


	

}
