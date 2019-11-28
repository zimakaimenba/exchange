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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;

import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.InvisibleTreeModel;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.CylTreeDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.CylTreeNestedSetNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.operations.BkfxWeeklyFileResultXmlHandler;
import com.exchangeinfomanager.nodes.services.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.treerelated.BanKuaiTreeRelated;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.exchangeinfomanager.nodes.treerelated.StockOfBanKuaiTreeRelated;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

import com.google.common.base.Charsets;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import javax.swing.JOptionPane;

class BanKuaiAndChanYeLian2 
{
	private static final long serialVersionUID = 1L;

	private CylTreeDbOperation treedb;

	private BanKuaiAndChanYeLian2 () 
	{
		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
		this.allbkstocks = AllCurrentTdxBKAndStoksTree.getInstance();

		treedb = new CylTreeDbOperation  ();
		treechanyelian = treedb.getBanKuaiAndStockTree();
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
//	protected ChanYeLianXMLHandler cylxmhandler;
    protected BanKuaiAndStockTree treechanyelian;
    private BanKuaiDbOperation bkdbopt;
    



	public void patchWeeklyBanKuaiFengXiXmlFileToCylTree(File xmlfile, LocalDate localDate)
	{
		bkfxrfxmlhandler.getXmlRootFileForBkfxWeeklyFile (xmlfile);
		
		InvisibleTreeModel treeModel = (InvisibleTreeModel)this.treechanyelian.getModel();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treeModel.getRoot();
		
		patchParsedResultXmlToTrees(treeroot,localDate);
		
		this.treechanyelian.setCurrentDisplayedWk (localDate);
		
		treeModel.reload(treeroot);
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
            	BanKuaiAndStockTree bkstkstree = this.allbkstocks.getAllBkStocksTree();
            	BanKuai nodeinallbktree = (BanKuai)bkstkstree.getSpecificNodeByHypyOrCode(tmpbkcode, BkChanYeLianTreeNode.TDXBK);
            	
            	if(!nodeinallbktree.isImportdailytradingdata())
            		continue;
            	
            	if( nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) 
            			|| nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) 
            			|| nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL)  ) //��Щָ����û�и��ɲ�����ȽϷ�Χ
					continue;
          	
            	
            	Integer setnum = this.bkfxrfxmlhandler.getBanKuaiFxSetNumberOfSpecificDate (tmpbkcode, localDate);
            	Boolean bkselfinparsedfile = this.bkfxrfxmlhandler.getBanKuaiFxSelfMatchModelOfSepecificDate (tmpbkcode, localDate);
  
				if(setnum != null && setnum > 0) {
//					BanKuaiTreeRelated treerelated = (BanKuaiTreeRelated)treeChild.getNodeTreeRelated ();
//					if(bkselfinparsedfile)
//						treerelated.setSelfIsMatchModel (localDate);
//	    			treerelated.setStocksNumInParsedFile (localDate,  setnum);
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
		
		HashSet<String> stockinfile = new HashSet<String> (readparsefileLines);
		HashSet<String> bkinfile = new HashSet<String> (readparsefilegetbkLines);
		this.bkfxrfxmlhandler.getXmlRootFileForBkfxWeeklyFile (null); //Ϊ�洢XML׼��XML�ļ�
		
		//���ڲ�ҵ�����ϱ�Ǹ��ɵ�����
		InvisibleTreeModel treeModel = (InvisibleTreeModel)this.treechanyelian.getModel();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treeModel.getRoot();
		patchParsedFileToTrees(treeroot,selectiondate,stockinfile,bkinfile);
		
		this.bkfxrfxmlhandler.saveXmlFileForBkfxWeeklyFile(weeklyfilename);

		this.treechanyelian.setCurrentDisplayedWk (selectiondate);
		
		treeModel.reload(treeroot);
		
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
	private void patchParsedFileToTrees (BkChanYeLianTreeNode treeroot, LocalDate localDate, HashSet<String> stockinfile, HashSet<String> bkinfile)
	{
		BkChanYeLianTreeNode treeChild;
		
		for (Enumeration<BkChanYeLianTreeNode> child = treeroot.children(); child.hasMoreElements();) {
			
            treeChild = (BkChanYeLianTreeNode) child.nextElement();
            
            int nodetype = treeChild.getType();
            if( nodetype == BkChanYeLianTreeNode.TDXBK) {
            	String tmpbkcode = treeChild.getMyOwnCode() ;
            	BanKuaiAndStockTree bkstkstree = this.allbkstocks.getAllBkStocksTree();
            	BanKuai nodeinallbktree = (BanKuai)bkstkstree.getSpecificNodeByHypyOrCode(tmpbkcode, BkChanYeLianTreeNode.TDXBK);
            	
            	if( !nodeinallbktree.isShowinbkfxgui() ) //��Щ��鲻�ᱻ��ʾ�������У�Ҳ�Ͳ��������ɷ���
            		continue;
            	
            	if( nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) 
            			|| nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) 
            			|| nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL)  ) 
					continue;
            	
            	nodeinallbktree = this.allbkstocks.getAllGeGuOfBanKuai(nodeinallbktree, NodeGivenPeriodDataItem.WEEK);
            	Set<StockOfBanKuai> curbkallbkset = nodeinallbktree.getSpecificPeriodBanKuaiGeGu(localDate,0,NodeGivenPeriodDataItem.WEEK);
            	HashSet<String> stkofbkset = new HashSet<String>  ();
            	for(StockOfBanKuai stkofbk : curbkallbkset) {
            		stkofbkset.add(stkofbk.getMyOwnCode());
            	}
            	
            	//������û�У����͸��ɶ��������ã��������԰���һ��XML����ϢĨȥ
            	SetView<String>  intersectionbankuai = Sets.intersection(stockinfile, stkofbkset);
	    		BanKuaiTreeRelated treerelated = (BanKuaiTreeRelated)treeChild.getNodeTreeRelated ();
    			treerelated.setStocksNumInParsedFile (localDate,intersectionbankuai.size());
            	if(bkinfile.contains(tmpbkcode))
            		treerelated.setSelfIsMatchModel(localDate);
            	//���ø����Ƿ��ڷ����ļ���
				for(StockOfBanKuai stkofbk : curbkallbkset ) {
					StockOfBanKuaiTreeRelated stofbktree = (StockOfBanKuaiTreeRelated)stkofbk.getNodeTreeRelated();
					if( intersectionbankuai.contains(stkofbk.getMyOwnCode() ) ) 
						stofbktree.setStocksNumInParsedFile (localDate,true);
				}

				//��Ϣ����XML
				this.bkfxrfxmlhandler.addBanKuaiFxSetToXml (nodeinallbktree.getMyOwnCode(),bkinfile.contains(tmpbkcode),intersectionbankuai,localDate);
				
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

	public Set<BkChanYeLianTreeNode> getGuoPiaoChi ()
	{
		Set<BkChanYeLianTreeNode> gpcindb =  new HashSet<BkChanYeLianTreeNode> ();
		InvisibleTreeModel ml = (InvisibleTreeModel)this.treechanyelian.getModel();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)(ml.getRoot());
		int bankuaicount = this.treechanyelian.getModel().getChildCount(treeroot);
		for(int i=0;i< bankuaicount; i++) {
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.treechanyelian.getModel().getChild(treeroot, i);
			gpcindb.add(childnode);
		}
		
		return gpcindb;
	}
	/*
	 * 
	 */
	private void updatedChanYeLianTree(AllCurrentTdxBKAndStoksTree allbkstocks)
	{
		// �ȱ�֤12����Ʊ��XML�����ݿ�һ��
		Set<BkChanYeLianTreeNode> gpcindb = bkdbopt.getGuPiaoChi ();
		Set<String> gpcnameindb = new HashSet<String> ();
		for(BkChanYeLianTreeNode tmpnode : gpcindb)
			gpcnameindb.add(tmpnode.getMyOwnCode());
		
		InvisibleTreeModel ml = (InvisibleTreeModel)this.treechanyelian.getModel();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)(ml.getRoot());
		int bankuaicount = this.treechanyelian.getModel().getChildCount(treeroot);
		Set<String> gpcintree = new HashSet<String> ();
		for(int i=0;i< bankuaicount; i++) {
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.treechanyelian.getModel().getChild(treeroot, i);
			gpcintree.add(childnode.getMyOwnCode());
		}
		
		SetView<String> differencegpc = Sets.difference(gpcnameindb, gpcintree );
		for (String gpccode : differencegpc) {
			for(BkChanYeLianTreeNode tmpnode : gpcindb) {
				if(tmpnode.getMyOwnCode().equals(gpccode)) {
					treeroot.add(tmpnode);
					break;
				}
			}
			
			
		}
		differencegpc = null;
		gpcintree = null;
		gpcnameindb = null;
		
		//��֤���һ�£�����Ӧ��ɾ���İ�鶼ɾ��������һ�£�ȱ�ٵİ�鶼�ŵ���������
		Set<String> cyltreebkset = this.treechanyelian.getSpecificTypeNodesSubCodesSet ("000000",BkChanYeLianTreeNode.DAPAN,BkChanYeLianTreeNode.TDXBK);
	    Set<String> allbks = allbkstocks.getAllBkStocksTree().getSpecificTypeNodesSubCodesSet ("000000",BkChanYeLianTreeNode.DAPAN,BkChanYeLianTreeNode.TDXBK);

//	    DefaultTreeModel model = (DefaultTreeModel) treechanyelian.getModel();
	    SetView<String> differencebkold = Sets.difference(cyltreebkset, allbks ); //Ӧ��ɾ����
	    for(String oldnodecode : differencebkold) {
	    	BkChanYeLianTreeNode oldnode = this.treechanyelian.getSpecificNodeByHypyOrCode (oldnodecode,BkChanYeLianTreeNode.TDXBK);
	    	TreePath nodepath = new TreePath(oldnode.getPath() );
	    	this.treechanyelian.deleteNodes(nodepath);
	    }
//	    HashSet<BkChanYeLianTreeNode> cyltreebkset1 = this.treechanyelian.getSpecificTypeNodesSet (BkChanYeLianTreeNode.TDXBK);
	    
	    SetView<String> differencebknew = Sets.difference(allbks, cyltreebkset ); //Ӧ����ӵ�
	    BkChanYeLianTreeNode qitanode = treechanyelian.getSpecificNodeByHypyOrCode("GPC999", BkChanYeLianTreeNode.GPC);
	    for(String newnode : differencebknew) {
	    	BanKuai tmpnode = (BanKuai)allbkstocks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(newnode, BkChanYeLianTreeNode.TDXBK);
	
	    	if(tmpnode.isShowincyltree() ) {
	    		BanKuai newbk = new BanKuai (newnode,tmpnode.getMyOwnName() );

	    		newbk.setExporttogehpi(tmpnode.isExporttogehpi());
	    		newbk.setImportdailytradingdata(tmpnode.isImportdailytradingdata());
	    		newbk.setShowinbkfxgui(tmpnode.isShowinbkfxgui());
	    		newbk.setShowincyltree(tmpnode.isShowincyltree());
	    		
	    		qitanode.add(newbk);
	    	}
	    	
	    }
//	    HashSet<BkChanYeLianTreeNode> cyltreebkset2 = this.treechanyelian.getSpecificTypeNodesSet (BkChanYeLianTreeNode.TDXBK);
		//��֤����һ�£�����Ӧ��ɾ���ĸ���ɾ��������һ��
		
	    
		ml.reload(treeroot);
 	}
	/*
	 * �����ҵ������XML
	 */
	public boolean saveChanYeLianTreeToXML ()
	{
		if(this.getBkChanYeLianTree().shouldSaveTreeToXml() ) {
			
			InvisibleTreeModel treeModel = (InvisibleTreeModel)this.getBkChanYeLianTree().getModel();
			BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treeModel.getRoot();
			if( cylxmhandler.saveTreeToChanYeLianXML(treeroot) ) {
				
//				JOptionPane.showMessageDialog(null, "�����ҵ��XMLʧ�ܣ������ԭ��","Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			
//			if( !zdgzbkxmlhandler.saveAllZdgzbkToXml () ) {
//				JOptionPane.showMessageDialog(null, "�����ص��ע��Ʊ��XMLʧ�ܣ������ԭ��","Warning", JOptionPane.WARNING_MESSAGE);
//				return false;
//			}
		}
		
		return true;
	}
	/*
	 * �ж��Ƿ������ص��ע���
	 */
	public Multimap<String,String> checkBanKuaiSuoSuTwelveDaLei (Set<String> bkcodeset)
	{
		Multimap<String,String> tmpsuoshudalei =  HashMultimap.create();
		
		for(String bkcode : bkcodeset) {
			BkChanYeLianTreeNode foundbk = this.treechanyelian.getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
			try {
				TreeNode[] bkpath = foundbk.getPath();
				BkChanYeLianTreeNode gpcbelonged = (BkChanYeLianTreeNode) bkpath[1];
				String gpccode = gpcbelonged.getMyOwnCode();
				if(!gpccode.toUpperCase().equals("GPC999")) {//����
					if(!gpccode.toUpperCase().equals("GPC014")) 
						tmpsuoshudalei.put(bkcode, gpcbelonged.getMyOwnName());
					else { //����Զ����Ƿ�Ҫ�ر��ע
						tmpsuoshudalei.put(bkcode, gpcbelonged.getMyOwnName());
						tmpsuoshudalei.put(bkcode, "GREEN");
					}
				}
			} catch (java.lang.NullPointerException e) {
//				e.printStackTrace();
				
			}
		}
		
		if(tmpsuoshudalei.size() == 0)
			return null;
		else
			return tmpsuoshudalei;
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

	/*
     * 
     */
	public boolean deleteNodes(BkChanYeLianTreeNode delNode) 
	{
		if(delNode.getMyOwnCode().equals("GPC999"))
			return false;
		
		BkChanYeLianTreeNode recylenode = treechanyelian.getSpecificNodeByHypyOrCode("GPC999", BkChanYeLianTreeNode.GPC); //���������в����ص��ע�Ķ��ŵ���������
		
		InvisibleTreeModel treeModel = (InvisibleTreeModel)this.treechanyelian.getModel();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treeModel.getRoot();
		
		if (treechanyelian.getSelectionCount() > 0){
	            TreePath[] treePaths = treechanyelian.getSelectionPaths();
	            treechanyelian.sortPaths(treechanyelian,treePaths);
	            int topRow = treechanyelian.getRowForPath(treePaths[0]);
	            for (TreePath path : treePaths){
	            	BkChanYeLianTreeNode child = (BkChanYeLianTreeNode) path.getLastPathComponent();

	            	BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) child.getParent();
	                if (parent != null){
	                    int childIndex = parent.getIndex(child);
	                    if(child.getType() == BkChanYeLianTreeNode.SUBGPC) { 
	                    	int nodechildcount = child.getChildCount();
	                    	if(nodechildcount >0 ) { //SUBGPCֻ�����Ӱ�飬����а�飬Ҫmove ������
	                    		for(int i=0;i <nodechildcount; i++) {
	                    			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.treechanyelian.getModel().getChild(child, 0);
	                    			recylenode.add(childnode);
	                    		}
	                    	}
	                    	parent.remove(child);	
	                    }  else if(child.getType() == BkChanYeLianTreeNode.TDXBK) { //move ������
		            		recylenode.add(child);
		            	} else
		            		parent.remove(child);
	                    
	                    treeModel.nodesWereRemoved(parent, new int[] {childIndex}, new Object[] {child});
	                    if (parent.getChildCount() == 0) 
	                    	parent.getNodeTreeRelated().setExpansion(false);
	                }
	            }
	            
	            if (treechanyelian.getVisibleRowCount() > 0) 
	            	treechanyelian.setSelectionRow(topRow);
	   }
	
	   treeModel.reload(treeroot);
	   return true;
	}
	/*
	 * 
	 */
	public List<BkChanYeLianTreeNode> getSubGuPiaoChi (String gpccode) 
	{
		 List<BkChanYeLianTreeNode> tmpsubbk = bkdbopt.getSubGuPiaoChi (gpccode);
		 return tmpsubbk;
	}
	/*
	 * 
	 */
	public List<BkChanYeLianTreeNode> getSubBanKuai (String bkcode) 
	{
		 List<BkChanYeLianTreeNode> tmpsubbk = bkdbopt.getSubBanKuai (bkcode);
		 return tmpsubbk;
	}
	public BkChanYeLianTreeNode addNewSubGuoPiaoChi(String parent, String newsubgpc)
	{
		BkChanYeLianTreeNode newnode = bkdbopt.addNewSubGuoPiaoChi(parent, newsubgpc);
		return newnode;
	}
	public BkChanYeLianTreeNode addNewSubBanKuai(String parent, String newsubbk) 
	{
		BkChanYeLianTreeNode newnode = bkdbopt.addNewSubBanKuai(parent, newsubbk);
		return newnode;
	}
	public void openChanYeLianXmlInWinSystem()
	{
		this.cylxmhandler.openChanYeLianXmlInWinSystem ();
		
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

