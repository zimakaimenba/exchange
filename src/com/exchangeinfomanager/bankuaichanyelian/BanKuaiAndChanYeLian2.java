package com.exchangeinfomanager.bankuaichanyelian;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;
import com.exchangeinfomanager.asinglestockinfo.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai.StockOfBanKuaiTreeRelated;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.GuPiaoChi;
import com.exchangeinfomanager.asinglestockinfo.InvisibleTreeModel;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuai.BanKuaiTreeRelated;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiPopUpMenu;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.DisplayBkGgInfoEditorPane;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.exchangeinfomanager.tongdaxinreport.TDXFormatedOpt;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.sun.rowset.CachedRowSetImpl;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.HyperlinkEvent;
import com.toedter.calendar.JDateChooser;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.UIManager;

public class BanKuaiAndChanYeLian2 
{
	private static final long serialVersionUID = 1L;

	private BanKuaiAndChanYeLian2 () 
	{
		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
		this.allbkstocks = AllCurrentTdxBKAndStoksTree.getInstance();

		this.cylxmhandler = new ChanYeLianXMLHandler ();
		this.zdgzbkxmlhandler = new TwelveZhongDianGuanZhuXmlHandler ();
		this.bkfxrfxmlhandler = new BkfxWeeklyFileResultXmlHandler ();
	
		treechanyelian = cylxmhandler.getBkChanYeLianXMLTree();
		updatedChanYeLianTree (allbkstocks);
		
		zdgzbkmap = zdgzbkxmlhandler.getZdgzBanKuaiFromXmlAndUpatedToCylTree(treechanyelian);
		
	}
	// ����ʵ��  
	 public static BanKuaiAndChanYeLian2 getInstance ()
	 {  
	        return Singtonle.instance;  
	 }
	 
	 private static class Singtonle 
	 {  
	        private static BanKuaiAndChanYeLian2 instance =  new BanKuaiAndChanYeLian2 ();  
	 }
	
	private static Logger logger = Logger.getLogger(BanKuaiAndChanYeLian2.class);
	
	private BkfxWeeklyFileResultXmlHandler bkfxrfxmlhandler;
	private AllCurrentTdxBKAndStoksTree allbkstocks;
	protected SystemConfigration sysconfig;
	protected HashMap<String, ArrayList<BkChanYeLianTreeNode>> zdgzbkmap;
	protected ChanYeLianXMLHandler cylxmhandler;
	protected TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler;
    protected BkChanYeLianTree treechanyelian;
//    protected BkChanYeLianTree treeallstocks;
    protected BkChanYeLianTree treeallbankuaiandhisstocks;
    protected BanKuaiDbOperation bkdbopt;

	/*
	 * XML����
	 */
	public void patchWeeklyBanKuaiFengXiXmlFileToCylTree(File xmlfile, LocalDate localDate)
	{
		bkfxrfxmlhandler.getXmlRootFileForBkfxWeeklyFile (xmlfile);
		
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.treechanyelian.getModel().getRoot();
		patchParsedResultXmlToTrees(treeroot,localDate);
		
		this.treechanyelian.setCurrentDisplayedWk (localDate);
		DefaultTreeModel treeModel = (DefaultTreeModel) treechanyelian.getModel();
		treeModel.reload();
	}
	/*
	 * 
	 */
	private void patchParsedResultXmlToTrees(BkChanYeLianTreeNode treeroot,  LocalDate localDate)
	{
		BkChanYeLianTreeNode treeChild;
		
		for (Enumeration<BkChanYeLianTreeNode> child = treeroot.children(); child.hasMoreElements();) {
			
            treeChild = (BkChanYeLianTreeNode) child.nextElement();

            int nodetype = treeChild.getType();
            if( nodetype == BkChanYeLianTreeNode.TDXBK) {
            	String tmpbkcode = treeChild.getMyOwnCode() ;
            	BkChanYeLianTree bkstkstree = this.allbkstocks.getAllBkStocksTree();
            	BanKuai nodeinallbktree = (BanKuai)bkstkstree.getSpecificNodeByHypyOrCode(tmpbkcode, BkChanYeLianTreeNode.TDXBK);
            	
            	if( nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) 
            			|| nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) 
            			|| nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL)  ) //��Щָ����û�и��ɲ�����ȽϷ�Χ
					continue;
          	
            	
            	Integer setnum = this.bkfxrfxmlhandler.getBanKuaiFxSetNumberOfSpecificDate(tmpbkcode, localDate);
            	if(setnum == null)
            		continue;
  
				if(setnum != null && setnum > 0) {
					BanKuaiTreeRelated treerelated = (BanKuaiTreeRelated)treeChild.getNodeTreerelated ();
					Boolean selfisin = treerelated.getSelfIsMatchModel ();
	    			treerelated.setStocksNumInParsedFile (localDate, selfisin,  setnum);
				}
	        } 
            
            patchParsedResultXmlToTrees(treeChild,localDate);
		}
	}
	/*
	 * 
	 */
	public Set<String> getBanKuaiFxSetOfSpecificDate(String myOwnCode, LocalDate curselectdate) 
	{
		Set<String> stkinbkset = this.bkfxrfxmlhandler.getBanKuaiFxSetOfSpecificDate(myOwnCode, curselectdate);
		return stkinbkset;
	}
	/*
	 * XML�����ڣ�Ϊÿ�ܵ����ķ���ģ�͵��ļ�����XML
	 */
	public void parseWeeklyBanKuaiFengXiFileToXmlAndPatchToCylTree(File weeklyfilename, LocalDate selectiondate)
	{
		File parsefile = weeklyfilename;
    	if(!parsefile.exists() )
    		return;
		
    	List<String> readparsefileLines = null;
		try {
			readparsefileLines = Files.readLines(parsefile,Charsets.UTF_8,new ParseBanKuaiWeeklyFielGetStocksProcessor ());
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> readparsefilegetbkLines = null;
		try {
			readparsefilegetbkLines = Files.readLines(parsefile,Charsets.UTF_8,new ParseBanKuaiWeeklyFielGetBanKuaisProcessor ());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		HashSet<String> stockinfile = new HashSet<String> (readparsefileLines);
		HashSet<String> bkinfile = new HashSet<String> (readparsefilegetbkLines);
		this.bkfxrfxmlhandler.getXmlRootFileForBkfxWeeklyFile (null);
		//���ڲ�ҵ�����ϱ�Ǹ��ɵ�����
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.treechanyelian.getModel().getRoot();
		patchParsedFileToTrees(treeroot,selectiondate,stockinfile,bkinfile);
		
		this.bkfxrfxmlhandler.saveXmlFileForBkfxWeeklyFile(weeklyfilename);

		this.treechanyelian.setCurrentDisplayedWk (selectiondate);
		DefaultTreeModel treeModel = (DefaultTreeModel) treechanyelian.getModel();
		treeModel.reload();
		
		readparsefileLines = null;
		readparsefilegetbkLines = null;
		stockinfile = null;
		bkinfile = null;
		parsefile = null;
	}
	/*
	 * 
	 */
	private void patchParsedFileToTrees (BkChanYeLianTreeNode treeroot,LocalDate localDate, HashSet<String> stockinfile, HashSet<String> bkinfile)
	{
		BkChanYeLianTreeNode treeChild;
		
		for (Enumeration<BkChanYeLianTreeNode> child = treeroot.children(); child.hasMoreElements();) {
			
            treeChild = (BkChanYeLianTreeNode) child.nextElement();
            
            int nodetype = treeChild.getType();
            if( nodetype == BkChanYeLianTreeNode.TDXBK) {
            	String tmpbkcode = treeChild.getMyOwnCode() ;
            	BkChanYeLianTree bkstkstree = this.allbkstocks.getAllBkStocksTree();
            	BanKuai nodeinallbktree = (BanKuai)bkstkstree.getSpecificNodeByHypyOrCode(tmpbkcode, BkChanYeLianTreeNode.TDXBK);
            	
            	if( nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) 
            			|| nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) 
            			|| nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL)  ) //��Щָ����û�и��ɲ�����ȽϷ�Χ
					continue;

//            	String tmpname = treeChild.getMyOwnName();
            	boolean selfinset = false;
            	if(bkinfile.contains(tmpbkcode))
            		selfinset = true;
            	
            	nodeinallbktree = this.allbkstocks.getAllGeGuOfBanKuai(nodeinallbktree, StockGivenPeriodDataItem.WEEK);
            	Set<StockOfBanKuai> curbkallbkset = nodeinallbktree.getSpecificPeriodBanKuaiGeGu(localDate,0,StockGivenPeriodDataItem.WEEK);
            	HashSet<String> stkofbkset = new HashSet<String>  ();
            	for(StockOfBanKuai stkofbk : curbkallbkset) {
            		stkofbkset.add(stkofbk.getMyOwnCode());
            	}
            	
            	//������û�У����͸��ɶ��������ã��������԰���һ��XML����ϢĨȥ
            	SetView<String>  intersectionbankuai = Sets.intersection(stockinfile, stkofbkset);
	    		BanKuaiTreeRelated treerelated = null;
				treerelated = (BanKuaiTreeRelated)treeChild.getNodeTreerelated ();
    			treerelated.setStocksNumInParsedFile (localDate,selfinset,intersectionbankuai.size());

				for(StockOfBanKuai stkofbk : curbkallbkset ) {
					StockOfBanKuaiTreeRelated stofbktree = (StockOfBanKuaiTreeRelated)stkofbk.getNodeTreerelated();
					if( intersectionbankuai.contains(stkofbk.getMyOwnCode() ) ) 
						stofbktree.setStocksNumInParsedFile (localDate,true);
					else
						stofbktree.setStocksNumInParsedFile (localDate,false);
				}

				//��Ϣ����XML
				this.bkfxrfxmlhandler.addBanKuaiFxSetToXml (nodeinallbktree.getMyOwnCode(),selfinset,intersectionbankuai,localDate);
				
				curbkallbkset = null;
				stkofbkset= null;
	        } 
	          
	        patchParsedFileToTrees(treeChild,localDate,stockinfile,bkinfile);
        }
	}
	/*
	 * 
	 */
	public void removeGeguFromModelFile (LocalDate date, String bkcode, String ggcode)
	{
		
	}
	/*
	 * 
	 */
	public BkChanYeLianTree getBkChanYeLianTree ()
	{
		return treechanyelian;
	}
	/*
	 * 
	 */
	private void updatedChanYeLianTree(AllCurrentTdxBKAndStoksTree allbkstocks)
	{
		// �ȱ�֤12����Ʊ��һ��
		HashMap<String,String> gpcindb = bkdbopt.getGuPiaoChi ();
		
		InvisibleTreeModel ml = (InvisibleTreeModel)this.treechanyelian.getModel();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)(ml.getRoot());
		int bankuaicount = this.treechanyelian.getModel().getChildCount(treeroot);
		HashSet<String> gpcintree = new HashSet<String> ();
		for(int i=0;i< bankuaicount; i++) {
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.treechanyelian.getModel().getChild(treeroot, i);
			gpcintree.add(childnode.getMyOwnCode());
		}
		
		SetView<String> differencegpc = Sets.difference(gpcindb.keySet(), gpcintree );
		for (String gpccode : differencegpc) {
			GuPiaoChi newpgc = new GuPiaoChi(gpccode,gpcindb.get(gpccode));
			treeroot.add(newpgc);
		}
		differencegpc = null;
		gpcintree = null;
		
		//��֤���һ�£�����Ӧ��ɾ���İ�鶼ɾ��������һ�£�ȱ�ٵİ�鶼�ŵ���������
		HashSet<String> cyltreebkset = this.treechanyelian.getSpecificTypeNodesCodesSet ("000000",BanKuaiAndStockBasic.DAPAN,BanKuaiAndStockBasic.TDXBK);
	    HashSet<String> allbks = allbkstocks.getAllBkStocksTree().getSpecificTypeNodesCodesSet ("000000",BanKuaiAndStockBasic.DAPAN,BanKuaiAndStockBasic.TDXBK);

	    DefaultTreeModel model = (DefaultTreeModel) treechanyelian.getModel();
	    SetView<String> differencebkold = Sets.difference(cyltreebkset, allbks ); //Ӧ��ɾ����
	    for(String oldnodecode : differencebkold) {
	    	BkChanYeLianTreeNode oldnode = this.treechanyelian.getSpecificNodeByHypyOrCode (oldnodecode,BanKuaiAndStockBasic.TDXBK);
	    	TreePath nodepath = new TreePath(oldnode.getPath() );
	    	this.treechanyelian.deleteNodes(nodepath);
	    }
//	    HashSet<BkChanYeLianTreeNode> cyltreebkset1 = this.treechanyelian.getSpecificTypeNodesSet (BanKuaiAndStockBasic.TDXBK);
	    
	    SetView<String> differencebknew = Sets.difference(allbks, cyltreebkset ); //Ӧ����ӵ�
	    BkChanYeLianTreeNode qitanode = treechanyelian.getSpecificNodeByHypyOrCode("qt", BanKuaiAndStockBasic.GPC);
	    for(String newnode : differencebknew) {
	    	BkChanYeLianTreeNode tmpnode = allbkstocks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(newnode, BanKuaiAndStockBasic.TDXBK);
	    	BanKuai newbk = new BanKuai (newnode,tmpnode.getMyOwnName() );
	    	qitanode.add(newbk);
	    }
//	    HashSet<BkChanYeLianTreeNode> cyltreebkset2 = this.treechanyelian.getSpecificTypeNodesSet (BanKuaiAndStockBasic.TDXBK);
		//��֤����һ�£�����Ӧ��ɾ���ĸ���ɾ��������һ��
		
	    
		ml.reload(treeroot);
 	}
	/*
	 * �����ҵ������XML
	 */
	public boolean saveChanYeLianTreeToXML (BkChanYeLianTreeNode treerootnode)
	{
		cylxmhandler.saveTreeToChanYeLianXML(treerootnode);
		return true;
	}
	/*
	 * �ж��Ƿ������ص��ע���
	 */
	public Multimap<String,String> checkBanKuaiSuoSuTwelveDaLei (Set<String> union)
	{
		return zdgzbkxmlhandler.subBkSuoSuTwelveDaLei (union);
	}
	/*
	 * ��Ʊ������ҵ��
	 */
	public Stock getStockChanYeLianInfo (Stock stockbasicinfo2)
	{
		cylxmhandler.getStockChanYeLianInfo(stockbasicinfo2);
		return stockbasicinfo2;
	}
	/*
	 * ��ȡ���ɲ�ҵ��
	 */
	public ArrayList<String> getGeGuChanYeLian(String stockcode)
	{
		return cylxmhandler.getGeGuChanYeLian(stockcode);
	}

	
	

}


/*
 * google guava  
 */
class ParseBanKuaiWeeklyFielGetStocksProcessor implements LineProcessor<List<String>> 
{
   
    private List<String> stocklists = Lists.newArrayList();
   
    @Override
    public boolean processLine(String line) throws IOException {
    	if(line.trim().length() ==7) {
    		if(line.startsWith("1")) { //�Ϻ��ĸ��ɻ���
    			if(line.startsWith("16")) { //�Ϻ��ĸ���
    				stocklists.add(line.substring(1));
    			}
    		} else  {
    			if(line.startsWith("00") || line.startsWith("03") ) { //���ڵĸ���
    				stocklists.add(line.substring(1));
    			}
    		}
    	}
        return true;
    }
    @Override
    public List<String> getResult() {
        return stocklists;
    }
}
class ParseBanKuaiWeeklyFielGetBanKuaisProcessor implements LineProcessor<List<String>> 
{
   
    private List<String> stocklists = Lists.newArrayList();
   
    @Override
    public boolean processLine(String line) throws IOException {
    	if(line.trim().length() ==7) {
    		if(line.startsWith("1")) { //�Ϻ��ĸ��ɻ���
    			if(!line.startsWith("16")) { //�Ϻ��İ��
    				stocklists.add(line.substring(1));
    			}
    		} else  {
    			if(!line.startsWith("00") && !line.startsWith("03") ) { //���ڵİ��
    				stocklists.add(line.substring(1));
    			}
    		}
    	}
        return true;
    }
    @Override
    public List<String> getResult() {
        return stocklists;
    }
}

