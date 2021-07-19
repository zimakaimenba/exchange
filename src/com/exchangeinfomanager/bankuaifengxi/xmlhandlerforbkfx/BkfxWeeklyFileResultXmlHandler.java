package com.exchangeinfomanager.bankuaifengxi.xmlhandlerforbkfx;

import java.io.File;

import java.io.FileOutputStream;

import java.io.IOException;
import java.time.LocalDate;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.tree.TreeNode;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.Core.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.Core.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Core.Trees.InvisibleTreeModel;
import com.exchangeinfomanager.Core.exportimportrelated.BanKuaiTreeRelated;
import com.exchangeinfomanager.Core.exportimportrelated.NodesTreeRelated;
import com.exchangeinfomanager.commonlib.ParseBanKuaiWeeklyFielGetBanKuaisProcessor;
import com.exchangeinfomanager.commonlib.ParseBanKuaiWeeklyFielGetStocksProcessor;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.google.common.base.Charsets;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;


public class BkfxWeeklyFileResultXmlHandler 
{
	private static Logger logger = Logger.getLogger(BkfxWeeklyFileResultXmlHandler.class);
	private SetupSystemConfiguration sysconfig;
	private String bkfwkfilexml;
	private BanKuaiDbOperation bkopt;
	private Document bkfxxmldoc;
	private Element bkfxxmlroot;
	private AllCurrentTdxBKAndStoksTree allbksks;

	public BkfxWeeklyFileResultXmlHandler() 
	{
		this.sysconfig = new SetupSystemConfiguration();
		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
	}
	/*
	 * 
	 */
	public Element setXmlRootFileForBkfxWeeklyFile (File xmlfile)
	{
		if(xmlfile != null && xmlfile.exists()) { //不存在，创建该文件
			try {
				bkfxxmldoc = null;
				bkfxxmlroot = null;
				
				SAXReader reader = new SAXReader();
				bkfxxmldoc = reader.read(xmlfile);
				bkfxxmlroot = bkfxxmldoc.getRootElement();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		} else {
			bkfxxmldoc = null;
			bkfxxmlroot = null;
			bkfxxmldoc = DocumentFactory.getInstance().createDocument();
	        bkfxxmlroot = bkfxxmldoc.addElement("板块分析结果");//添加文档根
		}
        
        return bkfxxmlroot;
	}
	/*
	 * 
	 */
	public void addBanKuaiFxSetToXml ( String bkcode, boolean selfinset, Set<String> bkstks, LocalDate date)
	{
		if(bkstks.size() == 0)
			return;
		
		String xpath = ".//BanKuai[@bkcode=\"" + bkcode + "\"]";  
		Node tmpnode = bkfxxmldoc.selectSingleNode(xpath);
		
		if(tmpnode == null) { //要加一个板块
			Element bankuai = bkfxxmlroot.addElement("BanKuai");
			bankuai.addAttribute("bkcode", bkcode  );
			bankuai.addAttribute("StocskNum", String.valueOf(bkstks.size())  );
			if(selfinset)
				bankuai.addAttribute("selfIsMatchModel", "TRUE"  );
			else
				bankuai.addAttribute("selfIsMatchModel", "FALSE"  );

			for(String stkcode : bkstks) {
				Element stock = bankuai.addElement("gegu");
				stock.addAttribute("gegucode", stkcode);
			}
		} else {
			for(String stkcode : bkstks) {
				Element stock = ((Element)tmpnode).addElement("gegu");
				stock.addAttribute("gegucode", stkcode);
			}
		}
		
	}
	/*
	 * 
	 */
	public void  saveXmlFileForBkfxWeeklyFile (File weeklyfilename)
	{
		String xmlfilename = weeklyfilename.getName().replace(".EBK", ".XML");
//		File filefmxx = new File( xmlfilename );
		
		String exportfilename = sysconfig.getTDXModelMatchExportFile () +  xmlfilename;
		File exportxmlfile = new File(exportfilename);
		if(!exportxmlfile.getParentFile().exists()) {  
            //如果目标文件所在的目录不存在，则创建父目录  
            logger.debug("目标文件所在目录不存在，准备创建它！");  
            if(!exportxmlfile.getParentFile().mkdirs()) {  
                System.out.println("创建目标文件所在目录失败！");  
                return ;  
            }  
        }  
		
		try {
				if (exportxmlfile.exists()) {
					exportxmlfile.delete();
					exportxmlfile.createNewFile();
				} else
					exportxmlfile.createNewFile();
		} catch (Exception e) {
				e.printStackTrace();
				return ;
		}
		
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");    // 指定XML编码        
		//保存产业链XML
		try {
			XMLWriter writer = new XMLWriter(new FileOutputStream(exportxmlfile),format); // 输出全部原始数据，并用它生成新的我们需要的XML文件  
				writer.write(bkfxxmldoc); //输出到文件  
				//writer.flush();
				writer.close();
		} catch (IOException e) {
				e.printStackTrace();
		}
		
		exportxmlfile = null;
//		filefmxx = null;
	}
	/*
	 * 
	 */
	public Integer getBanKuaiFxSetNumberOfSpecificDate (String bkcode, LocalDate date)
	{
		String xpath = ".//BanKuai[@bkcode=\"" + bkcode + "\"]";  
		Node tmpnode = bkfxxmldoc.selectSingleNode(xpath);
		
		if(tmpnode == null) 
			return null;
		
		String stocksnum = ((Element)tmpnode).attributeValue("StocskNum");
		return Integer.parseInt(stocksnum);
		
	}
	/*
	 * 
	 */
	public Boolean getBanKuaiFxSelfMatchModelOfSepecificDate (String bkcode, LocalDate date)
	{
		String xpath = ".//BanKuai[@bkcode=\"" + bkcode + "\"]";  
		Node tmpnode = bkfxxmldoc.selectSingleNode(xpath);
		
		if(tmpnode == null) 
			return null;
		
		String selfmatchmodel = ((Element)tmpnode).attributeValue("selfIsMatchModel");
		return Boolean.parseBoolean(selfmatchmodel);
	}
	/*
	 * 
	 */
	public Set<String> getBanKuaiFxSetOfSpecificDate (String bkcode, LocalDate date)
	{
		String xpath = ".//BanKuai[@bkcode=\"" + bkcode + "\"]";  
		Node tmpnode = bkfxxmldoc.selectSingleNode(xpath);
		
		if(tmpnode == null) 
			return null;
		
		String stocksnum = ((Element)tmpnode).attributeValue("StocskNum");
		
		Set<String> result = new HashSet<String> ();
		Iterator eleIt = ((Element)tmpnode).elementIterator();
		while (eleIt.hasNext()) 
		{
			   Element e = (Element) eleIt.next();

			   result.add(e.attributeValue("gegucode"));
		 }
		
		return result;
		
	}
	
	/*
	 * 
	 */
	public void patchParsedResultXmlToTrees(BkChanYeLianTreeNode treeroot,  LocalDate localDate)
	{
		BkChanYeLianTreeNode treeChild;
		
		for (Enumeration<TreeNode> child = treeroot.children(); child.hasMoreElements();) {
			
            treeChild = (BkChanYeLianTreeNode) child.nextElement();

            int nodetype = treeChild.getType();
            if( nodetype == BkChanYeLianTreeNode.TDXBK) {
            	String tmpbkcode = treeChild.getMyOwnCode() ;
            	BanKuaiAndStockTree bkstkstree = this.allbksks.getAllBkStocksTree();
            	BanKuai nodeinallbktree = (BanKuai)bkstkstree.getSpecificNodeByHypyOrCode(tmpbkcode, BkChanYeLianTreeNode.TDXBK);
            	
            	if(!nodeinallbktree.isImportdailytradingdata())
            		continue;
            	
            	if( nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) 
            			|| nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) 
            			|| nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL)  ) //有些指数是没有个股不列入比较范围
					continue;
          	
            	
            	Integer setnum = this.getBanKuaiFxSetNumberOfSpecificDate (tmpbkcode, localDate);
            	Boolean bkselfinparsedfile = this.getBanKuaiFxSelfMatchModelOfSepecificDate (tmpbkcode, localDate);
  
				if(setnum != null && setnum > 0) {
					NodesTreeRelated treerelated = treeChild.getNodeTreeRelated ();
					if(bkselfinparsedfile)
						treerelated.setSelfIsMatchModel (localDate);
	    			treerelated.setStocksNumInParsedFile (localDate,  setnum);
	    			
	    			//对个股进行记录
	    			Set<String> stocks = this.getBanKuaiFxSetOfSpecificDate (tmpbkcode,localDate);
	    			for(String tmpstkcode : stocks) {
	    				Stock stock = (Stock)bkstkstree.getSpecificNodeByHypyOrCode(tmpstkcode, BkChanYeLianTreeNode.TDXGG);
	    				NodesTreeRelated stocktreerelated = stock.getNodeTreeRelated ();
	    				stocktreerelated.setSelfIsMatchModel (localDate);
	    			}
				}
	        } 
            
            patchParsedResultXmlToTrees(treeChild,localDate);
		}
	}
	/*
	 * 周分析XML不存在，为每周导出的符合模型的文件生成XML
	 */
	public void parseWeeklyBanKuaiFengXiFileToXmlAndPatchToCylTree(File parsefile, LocalDate selectiondate)
	{
    	if(!parsefile.exists() )
    		return;
    	
    	List<String> readparsefileLines = null;
		try { //读出个股
			readparsefileLines = Files.readLines(parsefile,Charsets.UTF_8,new ParseBanKuaiWeeklyFielGetStocksProcessor ());
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> readparsefilegetbkLines = null;
		try {//读出板块
			readparsefilegetbkLines = Files.readLines(parsefile,Charsets.UTF_8,new ParseBanKuaiWeeklyFielGetBanKuaisProcessor ());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Set<String> stockinfile = new HashSet<String> (readparsefileLines);
		Set<String> bkinfile = new HashSet<String> (readparsefilegetbkLines);
		this.setXmlRootFileForBkfxWeeklyFile (null); //为存储XML准备XML文件
		
		//现在产业链树上标记个股的数量
		InvisibleTreeModel treeModel = (InvisibleTreeModel)this.allbksks.getAllBkStocksTree().getModel();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treeModel.getRoot();
		patchParsedFileToTrees(treeroot,selectiondate,stockinfile,bkinfile);
		
		this.saveXmlFileForBkfxWeeklyFile(parsefile);

		readparsefileLines = null;
		readparsefilegetbkLines = null;
		stockinfile = null;
		bkinfile = null;
		parsefile = null;
	}
	/*
	 * 读出个股和板块并附加到产业链树上
	 * 参数HashSet<String> stockinfile ：每周导出文件里面所有的个股
	 * HashSet<String> bkinfile ：每周导出文件里面所有的板块
	 */
	private void patchParsedFileToTrees (BkChanYeLianTreeNode treeroot, LocalDate localDate, Set<String> stockinfile, Set<String> bkinfile)
	{
		BkChanYeLianTreeNode treeChild;
		
		for (Enumeration<TreeNode> child = treeroot.children(); child.hasMoreElements();) {
			
            treeChild = (BkChanYeLianTreeNode) child.nextElement();
            
            int nodetype = treeChild.getType();
            if( nodetype == BkChanYeLianTreeNode.TDXBK) {
            	String tmpbkcode = treeChild.getMyOwnCode() ;
            	BanKuaiAndStockTree bkstkstree = this.allbksks.getAllBkStocksTree();
            	BanKuai nodeinallbktree = (BanKuai)bkstkstree.getSpecificNodeByHypyOrCode(tmpbkcode, BkChanYeLianTreeNode.TDXBK);
            	
            	if( nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) 
            			|| nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) 
            			|| nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL)  ) 
					continue;
            	
            	nodeinallbktree = this.allbksks.getAllGeGuOfBanKuai(nodeinallbktree, NodeGivenPeriodDataItem.WEEK);
            	java.util.Collection<BkChanYeLianTreeNode> curbkallbkset = nodeinallbktree.getSpecificPeriodBanKuaiGeGu(localDate,0);
//            	HashSet<String> stkofbkset = new HashSet<String>  ();
//            	for(BkChanYeLianTreeNode stkofbk : curbkallbkset) {
//            		stkofbkset.add(  ((StockOfBanKuai)stkofbk).getMyOwnCode());
//            	}
            	
            	//不管有没有，板块和个股都必须设置，这样可以把上一个XML的信息抹去
            	SetView<String>  intersectionbankuai = Sets.intersection(stockinfile, (HashSet) curbkallbkset);
	    		BanKuaiTreeRelated treerelated = (BanKuaiTreeRelated)treeChild.getNodeTreeRelated ();
    			treerelated.setStocksNumInParsedFile (localDate,intersectionbankuai.size());
            	if(bkinfile.contains(tmpbkcode))
            		treerelated.setSelfIsMatchModel(localDate);
            	//设置个股是否在分析文件中
				for(BkChanYeLianTreeNode stkofbk : curbkallbkset ) {
					
					NodesTreeRelated stofbktree = stkofbk.getNodeTreeRelated();
					if( intersectionbankuai.contains( stkofbk.getMyOwnCode() ) ) 
						stofbktree.setSelfIsMatchModel(localDate);
				}

				//信息存入XML
				this.addBanKuaiFxSetToXml (nodeinallbktree.getMyOwnCode(),bkinfile.contains(tmpbkcode),intersectionbankuai,localDate);
				
				curbkallbkset = null;
				
	        } 
	          
	        patchParsedFileToTrees(treeChild,localDate,stockinfile,bkinfile);
        }
	}
	
}





