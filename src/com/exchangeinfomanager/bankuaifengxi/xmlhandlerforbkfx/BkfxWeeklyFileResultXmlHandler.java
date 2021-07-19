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
		if(xmlfile != null && xmlfile.exists()) { //�����ڣ��������ļ�
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
	        bkfxxmlroot = bkfxxmldoc.addElement("���������");//����ĵ���
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
		
		if(tmpnode == null) { //Ҫ��һ�����
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
            //���Ŀ���ļ����ڵ�Ŀ¼�����ڣ��򴴽���Ŀ¼  
            logger.debug("Ŀ���ļ�����Ŀ¼�����ڣ�׼����������");  
            if(!exportxmlfile.getParentFile().mkdirs()) {  
                System.out.println("����Ŀ���ļ�����Ŀ¼ʧ�ܣ�");  
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
		format.setEncoding("GBK");    // ָ��XML����        
		//�����ҵ��XML
		try {
			XMLWriter writer = new XMLWriter(new FileOutputStream(exportxmlfile),format); // ���ȫ��ԭʼ���ݣ������������µ�������Ҫ��XML�ļ�  
				writer.write(bkfxxmldoc); //������ļ�  
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
            			|| nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL)  ) //��Щָ����û�и��ɲ�����ȽϷ�Χ
					continue;
          	
            	
            	Integer setnum = this.getBanKuaiFxSetNumberOfSpecificDate (tmpbkcode, localDate);
            	Boolean bkselfinparsedfile = this.getBanKuaiFxSelfMatchModelOfSepecificDate (tmpbkcode, localDate);
  
				if(setnum != null && setnum > 0) {
					NodesTreeRelated treerelated = treeChild.getNodeTreeRelated ();
					if(bkselfinparsedfile)
						treerelated.setSelfIsMatchModel (localDate);
	    			treerelated.setStocksNumInParsedFile (localDate,  setnum);
	    			
	    			//�Ը��ɽ��м�¼
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
	 * �ܷ���XML�����ڣ�Ϊÿ�ܵ����ķ���ģ�͵��ļ�����XML
	 */
	public void parseWeeklyBanKuaiFengXiFileToXmlAndPatchToCylTree(File parsefile, LocalDate selectiondate)
	{
    	if(!parsefile.exists() )
    		return;
    	
    	List<String> readparsefileLines = null;
		try { //��������
			readparsefileLines = Files.readLines(parsefile,Charsets.UTF_8,new ParseBanKuaiWeeklyFielGetStocksProcessor ());
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> readparsefilegetbkLines = null;
		try {//�������
			readparsefilegetbkLines = Files.readLines(parsefile,Charsets.UTF_8,new ParseBanKuaiWeeklyFielGetBanKuaisProcessor ());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Set<String> stockinfile = new HashSet<String> (readparsefileLines);
		Set<String> bkinfile = new HashSet<String> (readparsefilegetbkLines);
		this.setXmlRootFileForBkfxWeeklyFile (null); //Ϊ�洢XML׼��XML�ļ�
		
		//���ڲ�ҵ�����ϱ�Ǹ��ɵ�����
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
	 * �������ɺͰ�鲢���ӵ���ҵ������
	 * ����HashSet<String> stockinfile ��ÿ�ܵ����ļ��������еĸ���
	 * HashSet<String> bkinfile ��ÿ�ܵ����ļ��������еİ��
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
            	
            	//������û�У����͸��ɶ��������ã��������԰���һ��XML����ϢĨȥ
            	SetView<String>  intersectionbankuai = Sets.intersection(stockinfile, (HashSet) curbkallbkset);
	    		BanKuaiTreeRelated treerelated = (BanKuaiTreeRelated)treeChild.getNodeTreeRelated ();
    			treerelated.setStocksNumInParsedFile (localDate,intersectionbankuai.size());
            	if(bkinfile.contains(tmpbkcode))
            		treerelated.setSelfIsMatchModel(localDate);
            	//���ø����Ƿ��ڷ����ļ���
				for(BkChanYeLianTreeNode stkofbk : curbkallbkset ) {
					
					NodesTreeRelated stofbktree = stkofbk.getNodeTreeRelated();
					if( intersectionbankuai.contains( stkofbk.getMyOwnCode() ) ) 
						stofbktree.setSelfIsMatchModel(localDate);
				}

				//��Ϣ����XML
				this.addBanKuaiFxSetToXml (nodeinallbktree.getMyOwnCode(),bkinfile.contains(tmpbkcode),intersectionbankuai,localDate);
				
				curbkallbkset = null;
				
	        } 
	          
	        patchParsedFileToTrees(treeChild,localDate,stockinfile,bkinfile);
        }
	}
	
}





