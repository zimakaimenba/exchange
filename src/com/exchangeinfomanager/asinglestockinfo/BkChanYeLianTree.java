package com.exchangeinfomanager.asinglestockinfo;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import javax.swing.tree.TreeNode;

import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLianGUI2;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

public class BkChanYeLianTree extends JTree 
{
	public BkChanYeLianTree (BkChanYeLianTreeNode bkcylrootnode,String treeid)
	{
		super(bkcylrootnode);
		this.treeid = treeid;
		
		InvisibleTreeModel ml = new InvisibleTreeModel(bkcylrootnode);
		ml.activateFilter(true);
		this.setModel(ml);
		
		this.createEvents(this);
		this.setDragEnabled(false);
		this.setDragEnabled(true);
		this.setDropMode(javax.swing.DropMode.ON_OR_INSERT);
		this.setEditable(true);
		this.setCellRenderer(new BkChanYeLianTreeCellRenderer());
		this.setRootVisible(false);
		this.setTransferHandler(new TreeTransferHandler());
	}
	
	private static Logger logger = Logger.getLogger(BkChanYeLianTree.class);
	private String treeid; //系统要用到两棵cyltree，用ID来区分
	
	private boolean ignoreExpansion = false;
	private String currentselectedtdxbk;
	private LocalDate currentdisplayedwk; //tree当前所显示的分析州

	/*
	 * 
	 */
	public void setCurrentDisplayedWk (LocalDate diswk)
	{
		this.currentdisplayedwk = diswk;
	}
	/*
	 * 
	 */
	public LocalDate getCurrentDisplayedWk ()
	{
		return this.currentdisplayedwk ;
	}
	/*
	 * 
	 */
	public String getTreeId ()
	{
		return this.treeid;
	}
	/*
	 * 
	 */
	private void createEvents(final JTree tree) 
	{
		this.addMouseListener(new MouseAdapter() {
    		
            public void mouseClicked(MouseEvent evt) 
            {
            	treeMousePressed( evt);
            }
		});
		
        this.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                treeTreeExpanded(evt);
            }
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
                treeTreeCollapsed(evt);
            }
        });
	}
	/*
	 * 
	 */
	private void treeTreeCollapsed(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_treeTreeCollapsed
        if (!ignoreExpansion)
        ((BkChanYeLianTreeNode) evt.getPath().getLastPathComponent()).getNodeTreerelated().setExpansion(false);
    }
    private void treeTreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_treeTreeExpanded
        if (!ignoreExpansion )
        	((BkChanYeLianTreeNode) evt.getPath().getLastPathComponent()).getNodeTreerelated().setExpansion(true);
    }

    /*
     * 
     */
	private void treeMousePressed(MouseEvent evt) 
	{
		TreePath closestPath = this.getClosestPathForLocation(evt.getX(), evt.getY());

        if(closestPath != null) {
            Rectangle pathBounds = this.getPathBounds(closestPath);
            int maxY = (int) pathBounds.getMaxY();
            int minX = (int) pathBounds.getMinX();
            int maxX = (int) pathBounds.getMaxX();
            if (evt.getY() > maxY) this.clearSelection();
            else if (evt.getX() < minX || evt.getX() > maxX) this.clearSelection();
        }
        expandTreePathAllNode (closestPath);
	}
	/*
     * 展开节点,在其他地方也有调用
     */
    public void expandTreePathAllNode (TreePath closestPath)
    {
    		 this.currentselectedtdxbk = closestPath.getPathComponent(1).toString();
    		 String tdxbkcode = ((BkChanYeLianTreeNode)closestPath.getPathComponent(1)).getMyOwnCode();
        	 BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) closestPath.getLastPathComponent();
        	 this.setSelectionPath(closestPath);
        	 this.scrollPathToVisible(closestPath);
         	 expandTreeNode (this,parent);
    }
    /*
     * 展开所选取的节点
     */
    private static void expandTreeNode(JTree aTree, BkChanYeLianTreeNode aNode) {  
        if (aNode.isLeaf()) {  
          return;  
        }  
        aTree.expandPath(new TreePath( ( (BkChanYeLianTreeNode) aNode).getPath()));  
        int n = aNode.getChildCount();  
        for (int i = 0; i <n; i++) {  
          expandTreeNode(aTree, (BkChanYeLianTreeNode) aNode.getChildAt(i));  
        }  
     } 

	/*
	 * 时间参数是 处理这个时间那一周的板块个股和file比
	 */
//	private void updateTreeParseFileInfo (HashSet<String> stockinfile,LocalDate selectiondate)
//	{
//		DefaultTreeModel model = (DefaultTreeModel) this.getModel();
//		BkChanYeLianTreeNode root = (BkChanYeLianTreeNode) model.getRoot();
//		
//		updateParseFileInfoToTree (root,stockinfile,selectiondate);
//
//		model.nodeStructureChanged(root);
//	}
//	/*
//	 * 从根目录开始遍历树，安装每日板块文件
//	 */
//	private void updateParseFileInfoToTree(BkChanYeLianTreeNode node, HashSet<String> stockinfile, LocalDate selectiondate) 
//	{
//		int childCount = node.getChildCount();
//
//	    for (int i = 0; i < childCount; i++) {
//	    	BkChanYeLianTreeNode childNode = (BkChanYeLianTreeNode) node.getChildAt(i);
//	    	String bkname = childNode.getMyOwnName();
//	    	int childNodetype = childNode.getType();
//	    	childNode.getNodetreerelated().clearCurParseFileStockSet ( );
//
//	    	if( childNodetype == BanKuaiAndStockBasic.TDXBK ) {
//	    		String bkcode = childNode.getMyOwnCode();
//	    		
//	    		HashMap<String, Stock> tmpallbkge = ((BanKuai)childNode).getSpecificPeriodBanKuaiGeGu(selectiondate);
//	    		if(tmpallbkge != null) {
//	    			Set<String> curbkallbkset = null;
//		    		try {
//		    			 curbkallbkset = tmpallbkge.keySet();
//		    		} catch (java.lang.NullPointerException e) {
//		    			 curbkallbkset = new HashSet<String> ();
//		    		}
//					SetView<String>  intersectionbankuai = Sets.intersection(stockinfile, curbkallbkset );
//					
//					if(intersectionbankuai.size() >0 ) { //有交集
//						HashSet<String> stockbkresult = new HashSet<String> (intersectionbankuai);
//						childNode.getNodetreerelated().setParseFileStockSet (stockbkresult); // 把交集保存
//					}
//	    			
//	    		}
//	    	}
//	    	
//	    	if( childNodetype == BanKuaiAndStockBasic.BKGEGU ) {
//	    		String gegustockcode = childNode.getMyOwnCode();
//	    		Set<String> curbkallbkset = new HashSet<String> ();
//	    		curbkallbkset.add(gegustockcode);
//	    		SetView<String>  intersectionbankuai = Sets.intersection(stockinfile, curbkallbkset);
//	    		
//     		    if(intersectionbankuai.size() >0 ) { //有交集
//     		    	HashSet<String> stockbkresult = new HashSet<String> (intersectionbankuai);
//					childNode.getNodetreerelated().setParseFileStockSet (stockbkresult); // 把交集保存
//					
//				    //如果是个股节点，则要把上面所有父节点(子产业链也标记这个个股)
//				     DefaultTreeModel model = (DefaultTreeModel) this.getModel();
//					 TreeNode[] tempath = model.getPathToRoot(childNode);
//					 for(int j=1;j<tempath.length-1;j++ ) {
//					    	BkChanYeLianTreeNode parentnode = (BkChanYeLianTreeNode)tempath[j];
//					    	int nodeparentstype = parentnode.getType();
//					    	if(nodeparentstype == BkChanYeLianTreeNode.SUBBK) { //如果是子产业链，
//					    			 parentnode.getNodetreerelated().setParseFileStockSet(stockbkresult);
//					    	}
//					 }
//				      
//			      }
//	    	}
//	    	
//	        if (childNode.getChildCount() > 0) {
//	        	updateParseFileInfoToTree(childNode,stockinfile,selectiondate);
//	        } 
//	    }
//	}
//	/*
//	 * 从指定node开始，安装每日板块文件到该node下所有节点
//	 */
//	public void updateParseFileInfoToTreeFromSpecificNode(BkChanYeLianTreeNode node, HashSet<String> stockinfile, LocalDate selectiondate) 
//	{
//		node.getNodetreerelated().clearCurParseFileStockSet ( );
//		int nodetype = node.getType();
//		
//		if( nodetype == BanKuaiAndStockBasic.TDXBK ) {
//    		String bkcode = node.getMyOwnCode();
//    		
//    		HashMap<String, Stock> tmpallbkge = ((BanKuai)node).getSpecificPeriodBanKuaiGeGu(selectiondate);
//    		if(tmpallbkge != null) {
//    			Set<String> curbkallbkset = null;
//	    		try {
//	    			 curbkallbkset = tmpallbkge.keySet();
//	    		} catch (java.lang.NullPointerException e) {
//	    			 curbkallbkset = new HashSet<String> ();
//	    		}
//				SetView<String>  intersectionbankuai = Sets.intersection(stockinfile, curbkallbkset );
//				
//				if(intersectionbankuai.size() >0 ) { //有交集
//					HashSet<String> stockbkresult = new HashSet<String> (intersectionbankuai);
//					node.getNodetreerelated().setParseFileStockSet (stockbkresult); // 把交集保存
//				}
//    			
//    		}
//    	}
//    	
//    	if( nodetype == BanKuaiAndStockBasic.BKGEGU ) {
//    		String gegustockcode = node.getMyOwnCode();
//    		Set<String> curbkallbkset = new HashSet<String> ();
//    		curbkallbkset.add(gegustockcode);
//    		SetView<String>  intersectionbankuai = Sets.intersection(stockinfile, curbkallbkset);
//    		
// 		    if(intersectionbankuai.size() >0 ) { //有交集
// 		    	HashSet<String> stockbkresult = new HashSet<String> (intersectionbankuai);
//				node.getNodetreerelated().setParseFileStockSet (stockbkresult); // 把交集保存
//				
//			    //如果是个股节点，则要把上面所有父节点(子产业链也标记这个个股)
//			     DefaultTreeModel model = (DefaultTreeModel) this.getModel();
//				 TreeNode[] tempath = model.getPathToRoot(node);
//				 for(int j=1;j<tempath.length-1;j++ ) {
//				    	BkChanYeLianTreeNode parentnode = (BkChanYeLianTreeNode)tempath[j];
//				    	int nodeparentstype = parentnode.getType();
//				    	if(nodeparentstype == BkChanYeLianTreeNode.SUBBK) { //如果是子产业链，
//				    			 ((SubBanKuai)parentnode).setParseFileStockSet(stockbkresult);
//				    	}
//				 }
//			      
//		      }
//    	}
//    	DefaultTreeModel model = (DefaultTreeModel) this.getModel();
//    	model.nodeStructureChanged (node);
//    	
//		int childCount = node.getChildCount();
//	    for (int i = 0; i < childCount; i++) {
//	    	BkChanYeLianTreeNode childNode = (BkChanYeLianTreeNode) node.getChildAt(i);
//	    	updateParseFileInfoToTreeFromSpecificNode (childNode,stockinfile,selectiondate);
//	    }
//	}

		
	public boolean checkNodeDuplicate(BkChanYeLianTreeNode parent,  BkChanYeLianTreeNode newNode) 
    {
    	String gegucodename = newNode.getUserObject().toString().trim();
    	String parentname = parent.getUserObject().toString().trim();
    	
    	if(gegucodename.equals(parentname))
    		return true;
    	
    	int childnum = parent.getChildCount();
        for(int i=0;i<childnum;i++) {
        	BkChanYeLianTreeNode tmpnode = (BkChanYeLianTreeNode)parent.getChildAt(i);
        	String tmpnodename = tmpnode.getUserObject().toString();
        	logger.debug(tmpnodename);
        	if(tmpnodename.equals(gegucodename)) {
        		return true;
        	}
        }

		return false;
	}

    @SuppressWarnings("unchecked")
    public void sortPaths(BkChanYeLianTree bkChanYeLianTree, TreePath[] treePaths){
        Arrays.sort(treePaths, new java.util.Comparator(){
            public int compare(Object path1, Object path2){
            	
                int row1 = bkChanYeLianTree.getRowForPath((TreePath) path1);
                int row2 = bkChanYeLianTree.getRowForPath((TreePath) path2);
                if (row1 < row2) return -1;
                else if (row1 > row2) return 1;
                else return 0;
        }});
    }
    
//	public void moveNode(int direction) 
//	{
//		if (this.getSelectionCount()==0) return;
//        
//		DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
//        TreePath parentPath = this.getSelectionPath().getParentPath();
//        TreePath[] treePaths = this.getSelectionPaths();
//        final BkChanYeLianTreeNode parentNode = (BkChanYeLianTreeNode) parentPath.getLastPathComponent();
//        //String tmpstr = parentPath.toString();
//        if(parentNode.getType() == BkChanYeLianTreeNode.TDXBK || parentPath.toString().equals("[JTree]"))
//        	return;
//        
//        //unselect non-siblings
//        for (TreePath treePath : treePaths)
//            if (!parentPath.equals(treePath.getParentPath()))
//                this.removeSelectionPath(treePath);
//        treePaths = this.getSelectionPaths();
//        
//        sortPaths(this,treePaths);
//        
//        BkChanYeLianTreeNode node=null, newParentNode;
//        int index;
//        if (direction == BanKuaiAndChanYeLian.UP){
//            this.clearSelection();
//            for (TreePath treePath : treePaths){
//                node = (BkChanYeLianTreeNode) treePath.getLastPathComponent();
//                index = parentNode.getIndex(node);
//                if (index == 0) {
//                    this.setSelectionPaths(treePaths);
//                    return;
//                }
//                else {
//                    parentNode.remove(index);
//                    treeModel.nodesWereRemoved(parentNode, new int[] {index}, new Object[] {node});
//                    parentNode.insert(node, index-1);
//                    treeModel.nodesWereInserted(parentNode, new int[] {index - 1});
//                }
//            }
//            this.setSelectionPaths(treePaths);
//            this.scrollPathToVisible(treePaths[0]);
//        }
//        else if (direction == BanKuaiAndChanYeLian.DOWN){
//            this.clearSelection();
//            for (int i=treePaths.length - 1; i>=0; i--){
//                node = (BkChanYeLianTreeNode) treePaths[i].getLastPathComponent();
//                index = parentNode.getIndex(node);
//                if (index == parentNode.getChildCount()-1) {
//                    this.setSelectionPaths(treePaths);
//                    return;
//                }
//                else {
//                    parentNode.remove(index);
//                    treeModel.nodesWereRemoved(parentNode, new int[] {index}, new Object[] {node});
//                    parentNode.insert(node, index+1);
//                    treeModel.nodesWereInserted(parentNode, new int[] {index + 1});
//                }
//            }
//            this.setSelectionPaths(treePaths);
//            this.scrollPathToVisible(treePaths[treePaths.length-1]);
//            
//        }
//        else if (direction==BanKuaiAndChanYeLian.LEFT)
//            
//            
//                this.clearSelection();
//                newParentNode = (BkChanYeLianTreeNode)parentNode.getParent();
//            
//                for (TreePath treePath : treePaths){
//                    node = (BkChanYeLianTreeNode) treePath.getLastPathComponent();
//                    index = parentNode.getIndex(node);
//                    parentNode.remove(index);
//                    treeModel.nodesWereRemoved(parentNode, new int[]{index}, new Object[]{node});
//                    newParentNode.add(node);
//                    treeModel.nodesWereInserted(newParentNode, new int[] {newParentNode.getIndex(node)});
//                    this.addSelectionPath(new TreePath(node.getPath()));
//                }
//                this.scrollPathToVisible(new TreePath(node.getPath()));
//	}
	/*
	 * 
	 */
	public boolean deleteNodes(TreePath selectednode) 
	{
		DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();

		this.setSelectionPath(selectednode);
		TreePath[] treePaths = this.getSelectionPaths();
		sortPaths(this,treePaths);
	    int topRow = this.getRowForPath(treePaths[0]);
	    for (TreePath path : treePaths) {
	            	BkChanYeLianTreeNode child = (BkChanYeLianTreeNode) path.getLastPathComponent();
	            	
//	            	if(child.getType() == BkChanYeLianTreeNode.TDXBK) {
//	            		JOptionPane.showMessageDialog(null,"所选为通达信板块，无法删除！","Warning",JOptionPane.WARNING_MESSAGE);
//	            		return false;
//	            	}
//	            	if(child.getNodetreerelated().getInZdgzOfficalCount()>0 || child.getNodetreerelated().getInZdgzCandidateCount()>0) {
//	            		JOptionPane.showMessageDialog(null,"所选产业链是关注股票池候选或正式选择，请先在关注股票池中移除后再删除！","Warning",JOptionPane.WARNING_MESSAGE);
//	            		return false;
//	            	}
	            		
	            	
	            	BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) child.getParent();
	                if (parent != null){
	                    
	                    int childIndex = parent.getIndex(child);
	                    parent.remove(child);
	                    treeModel.nodesWereRemoved(parent, new int[] {childIndex}, new Object[] {child});
	                    if (parent.getChildCount()==0) parent.getNodeTreerelated().setExpansion(false);
	                }
	      }
	            
	      if (this.getVisibleRowCount()>0) 
	            	this.setSelectionRow(topRow);
	   
	
	   return true;
	}

	/*
	 * 把树节点存储的产业链转化为arraylist
	 */
	private ArrayList<String> getFormatedBanKuaiChanYeLian (String bkcyl)
	{
		List<String> tmpcyltreepathlist =  Splitter.on("->").trimResults().omitEmptyStrings().splitToList(bkcyl);  
		logger.debug(tmpcyltreepathlist);
		
		ArrayList<String> cyltreepathlist = new ArrayList<String>(tmpcyltreepathlist);
		logger.debug(cyltreepathlist);

		return cyltreepathlist;
	}
	/*
	 * 找到XMl的产业链对应的树节点
	 */
	public BkChanYeLianTreeNode updateZdgzInfoToBkCylTreeNode(String bkcyl, String addedtime, boolean officallselect) 
	{
		//[TongDaXinBanKuaiAndZhiShu, 染料涂料, 浙江, 300192科斯伍德]
		//[TongDaXinBanKuaiAndZhiShu, 染料涂料, 浙江, 600352浙江龙盛]
		//bkcyl = "TongDaXinBanKuaiAndZhiShu->" + bkcyl;
		ArrayList<String> cyltreepathlist = getFormatedBanKuaiChanYeLian (bkcyl);
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.getModel().getRoot();
		
		BkChanYeLianTreeNode expectNode = updatedZdgzInfoBkCylTreeNodeOneByOne2 (treeroot,cyltreepathlist,addedtime,officallselect);
		
		return expectNode;
	}
	/*
	 * 
	 */
	private BkChanYeLianTreeNode updatedZdgzInfoBkCylTreeNodeOneByOne2 (BkChanYeLianTreeNode node, List<String> cyltreepathlist, String addedtime, boolean officallsltopt)
	{
		BkChanYeLianTreeNode expectedNode = null;
//		if(cyltreepathlist.size() == 0)
//			return;
		
		int childCount = node.getChildCount();
 	    for (int i = 0; i < childCount; i++) {
	    	if(cyltreepathlist.size() == 0)
				return expectedNode;

	    	BkChanYeLianTreeNode childNode = (BkChanYeLianTreeNode) node.getChildAt(i);
	    	String childnodecode = childNode.getMyOwnCode(); 
	    	if(childNode.getType() == BkChanYeLianTreeNode.TDXBK && !childnodecode.equals(cyltreepathlist.get(0))  )
	    		continue;
	    	
	    	if(childnodecode.equals(cyltreepathlist.get(0)  ) ) {
    			if(officallsltopt)
    				childNode.getNodeTreerelated().increaseZdgzOfficalCount();
	    		
    			childNode.getNodeTreerelated().increaseZdgzCandidateCount();
	    			
	    		DefaultTreeModel treemodel = (DefaultTreeModel) this.getModel();
	    		treemodel.nodeChanged(childNode);
	    		
	    		cyltreepathlist.remove(0);
	    		
	    		if(cyltreepathlist.size() == 0) {
	    			childNode.getNodeTreerelated().setOfficallySelected(officallsltopt);
	    			childNode.getNodeTreerelated().setSelectedToZdgzTime(addedtime);
	    			
	    			expectedNode = childNode;
	    			break;
	    		}
	    	}
	    	
	    	
	        if (childNode.getChildCount() > 0 && cyltreepathlist.size()>0 ) {
	        	expectedNode = updatedZdgzInfoBkCylTreeNodeOneByOne2(childNode,cyltreepathlist,addedtime,officallsltopt);
	        } 
	    }
 	    
 	    return expectedNode;
		
	}
	/*
	 * 
	 */
	public void removeZdgzBkCylInfoFromTreeNode(BkChanYeLianTreeNode childNode,boolean stillkeepincandidate) 
	{
		 TreeNode[] nodepath = childNode.getPath();
		 Boolean isofficallselected = childNode.getNodetreerelated().isOfficallySelected();
		 
		 DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		 TreeNode[] tempath = model.getPathToRoot(childNode);
		 for(int j=1;j<tempath.length;j++ ) {
		    	BkChanYeLianTreeNode parentnode = (BkChanYeLianTreeNode)tempath[j];
		    	if(isofficallselected) 
		    		parentnode.getNodetreerelated().decreaseZdgzOfficalCount();
		    	if(!stillkeepincandidate) //只是从offical移除，candidate还有就不用加
		    		parentnode.getNodetreerelated().decreasedgzCandidateCount();
		    	
		    	model.nodeChanged(parentnode);
		 }
		
	}
	/*
	 * 
	 */
	public void addZdgzBkCylInfoToTreeNode(BkChanYeLianTreeNode childNode, boolean hasbeenincandidate)  
	{
		 TreeNode[] nodepath = childNode.getPath();
		 Boolean isofficallselected = childNode.getNodetreerelated().isOfficallySelected();
		 
//		 DefaultTreeModel treemodel = (DefaultTreeModel) this.getModel();
		 
		 DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		 TreeNode[] tempath = model.getPathToRoot(childNode);
		 for(int j=1;j<tempath.length;j++ ) {
		    	BkChanYeLianTreeNode parentnode = (BkChanYeLianTreeNode)tempath[j];
		    	if(isofficallselected) 
		    		parentnode.getNodetreerelated().increaseZdgzOfficalCount();
		    	if(!hasbeenincandidate) //如果已经在候选里面，cand count就不用加
		    		parentnode.getNodetreerelated().increaseZdgzCandidateCount();	
		    	
		    	model.nodeChanged(parentnode);
		 }
		
	}
	/*
	 * 找到指定节点的位置
	 */
//	public TreePath locateNodeByNameOrHypyOrBkCode(String bkinputed,boolean showatonce) 
//	{
//		TreePath bkpath = null ;
//    	BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.getModel().getRoot();
//	    @SuppressWarnings("unchecked")
//		Enumeration<BkChanYeLianTreeNode> e = treeroot.depthFirstEnumeration();
//	    while (e.hasMoreElements() ) {
//	    	BkChanYeLianTreeNode node = e.nextElement();
//	    	Boolean found = node.checktHanYuPingYin(bkinputed);
//	        if (found) {
//	             bkpath = new TreePath(node.getPath());
//	             if(showatonce) { //定位后同时也跳转到该个股
//		             this.setSelectionPath(bkpath);
//		     	     this.scrollPathToVisible(bkpath);
//		     	     this.expandTreePathAllNode(bkpath);
//	             }
//	     	     return bkpath;
//	        }
//	    }
//		return null;
//	}
	/*
	 * 找到指定的节点
	 */
	public BkChanYeLianTreeNode getSpecificNodeByHypyOrCode (String bkinputed,int requirenodetype) //有时候板块和个股代码相同,所以要加上type
	{
		TreePath bkpath = null ;
    	BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.getModel().getRoot();
    	if(bkinputed.equals("000000"))
    		return treeroot;
    	
	    @SuppressWarnings("unchecked")
		Enumeration<BkChanYeLianTreeNode> e = treeroot.depthFirstEnumeration();
	    while (e.hasMoreElements() ) {
	    	BkChanYeLianTreeNode node = e.nextElement();
	    	Boolean found = node.checktHanYuPingYin(bkinputed);
//    		logger.debug(node.getMyOwnCode());
	        if (found && node.getType() == requirenodetype ) {
//	        	logger.debug(node.getMyOwnCode());
	             bkpath = new TreePath(node.getPath());
	             break;
	        }
	    }
	    
		if(bkpath != null) {
			return (BkChanYeLianTreeNode) bkpath.getLastPathComponent();
			
		} else
			return null;
	}
	/*
	 * 找到nodecode下所有requirenodetype的节点名称
	 */
	public HashSet<String> getSpecificTypeNodesCodesSet (String nodecode, int nodetype, int requirenodetype)
	{
		HashSet<String> nodesset = new HashSet<String> ();
		
//    	BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.getModel().getRoot();
    	
		BkChanYeLianTreeNode treeroot = this.getSpecificNodeByHypyOrCode(nodecode, nodetype);
		
		if(treeroot == null)
			return null;
		
	    @SuppressWarnings("unchecked")
		Enumeration<BkChanYeLianTreeNode> e = treeroot.depthFirstEnumeration();
	    while (e.hasMoreElements() ) {
	    	BkChanYeLianTreeNode node = e.nextElement();
	    	if(node.getType() == requirenodetype) {
	    		nodesset.add(node.getMyOwnCode());
	    	}
	    }
	    
	    return nodesset;
	}
	
	
}


