package com.exchangeinfomanager.nodes.operations;

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
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import javax.swing.tree.TreeNode;

import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLianGUI2;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiPopUpMenu;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiPopUpMenuForTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiPopUpMenuForTree;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

/*
 * 系统维护2棵树，1个是所有板块和个股在一颗树上。第二个树父节点是板块，板块下面是属于该板块的所有个股。
 */
public class BanKuaiAndStockTree extends JTree 
{
	public BanKuaiAndStockTree (BkChanYeLianTreeNode bkcylrootnode,String treeid)
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
		
//		BanKuaiPopUpMenu popupMenuGeguNews = new BanKuaiPopUpMenuForTree(this);
//		this.setComponentPopupMenu(popupMenuGeguNews);
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiAndStockTree.class);
	private String treeid; //系统要用到两棵cyltree，用ID来区分
	
	private boolean ignoreExpansion = false;
	private String currentselectedtdxbk;
	private LocalDate currentdisplayedwk; //tree当前所显示的分析州
	protected boolean treechangedshouldsave = false;

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
	public Boolean shouldSaveTreeToXml ()
	{
		return treechangedshouldsave;
	}
	/*
	 * 
	 */
	private void createEvents(final JTree tree) 
	{
		
			((InvisibleTreeModel)this.getModel()).addTreeModelListener( new  TreeModelListener () {

				@Override
				public void treeNodesChanged(TreeModelEvent arg0) {
					treechangedshouldsave = true;
				}

				@Override
				public void treeNodesInserted(TreeModelEvent arg0) {
					treechangedshouldsave = true;
				}

				@Override
				public void treeNodesRemoved(TreeModelEvent arg0) {
					treechangedshouldsave = true;
				}

				@Override
				public void treeStructureChanged(TreeModelEvent arg0) {
					treechangedshouldsave = true;
				}});
			
		
		
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
        ((BkChanYeLianTreeNode) evt.getPath().getLastPathComponent()).getNodeTreeRelated().setExpansion(false);
    }
	/*
	 * 
	 */
    private void treeTreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_treeTreeExpanded
        if (!ignoreExpansion ) 
        	try {
        		BkChanYeLianTreeNode node = ((BkChanYeLianTreeNode) evt.getPath().getLastPathComponent());
        		NodesTreeRelated treerelatd = node.getNodeTreeRelated();
        		treerelatd.setExpansion(true);
        	} catch(java.lang.NullPointerException  e) {
//        		e.printStackTrace();
        	}
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
    public void sortPaths(BanKuaiAndStockTree bkChanYeLianTree, TreePath[] treePaths){
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
	            	BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) child.getParent();
	                if (parent != null){
	                    int childIndex = parent.getIndex(child);
	                    parent.remove(child);
	                    treeModel.nodesWereRemoved(parent, new int[] {childIndex}, new Object[] {child});
	                    if (parent.getChildCount()==0) parent.getNodeTreeRelated().setExpansion(false);
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
//	public BkChanYeLianTreeNode updateZdgzInfoToBkCylTreeNode(String bkcyl, String addedtime, boolean officallselect) 
//	{
//		//[TongDaXinBanKuaiAndZhiShu, 染料涂料, 浙江, 300192科斯伍德]
//		//[TongDaXinBanKuaiAndZhiShu, 染料涂料, 浙江, 600352浙江龙盛]
//		//bkcyl = "TongDaXinBanKuaiAndZhiShu->" + bkcyl;
//		ArrayList<String> cyltreepathlist = getFormatedBanKuaiChanYeLian (bkcyl);
//		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.getModel().getRoot();
//		
//		BkChanYeLianTreeNode expectNode = updatedZdgzInfoBkCylTreeNodeOneByOne2 (treeroot,cyltreepathlist,addedtime,officallselect);
//		
//		return expectNode;
//	}
	/*
	 * 
	 */
//	private BkChanYeLianTreeNode updatedZdgzInfoBkCylTreeNodeOneByOne2 (BkChanYeLianTreeNode node, List<String> cyltreepathlist, String addedtime, boolean officallsltopt)
//	{
//		BkChanYeLianTreeNode expectedNode = null;
////		if(cyltreepathlist.size() == 0)
////			return;
//		
//		int childCount = node.getChildCount();
// 	    for (int i = 0; i < childCount; i++) {
//	    	if(cyltreepathlist.size() == 0)
//				return expectedNode;
//
//	    	BkChanYeLianTreeNode childNode = (BkChanYeLianTreeNode) node.getChildAt(i);
//	    	String childnodecode = childNode.getMyOwnCode(); 
//	    	if(childNode.getType() == BkChanYeLianTreeNode.TDXBK && !childnodecode.equals(cyltreepathlist.get(0))  )
//	    		continue;
//	    	
//	    	if(childnodecode.equals(cyltreepathlist.get(0)  ) ) {
//    			if(officallsltopt)
//    				childNode.getNodeTreeRelated().increaseZdgzOfficalCount();
//	    		
//    			childNode.getNodeTreeRelated().increaseZdgzCandidateCount();
//	    			
//	    		DefaultTreeModel treemodel = (DefaultTreeModel) this.getModel();
//	    		treemodel.nodeChanged(childNode);
//	    		
//	    		cyltreepathlist.remove(0);
//	    		
//	    		if(cyltreepathlist.size() == 0) {
//	    			childNode.getNodeTreeRelated().setOfficallySelected(officallsltopt);
//	    			childNode.getNodeTreeRelated().setSelectedToZdgzTime(addedtime);
//	    			
//	    			expectedNode = childNode;
//	    			break;
//	    		}
//	    	}
//	    	
//	    	
//	        if (childNode.getChildCount() > 0 && cyltreepathlist.size()>0 ) {
//	        	expectedNode = updatedZdgzInfoBkCylTreeNodeOneByOne2(childNode,cyltreepathlist,addedtime,officallsltopt);
//	        } 
//	    }
// 	    
// 	    return expectedNode;
		
//	}
//	/*
//	 * 
//	 */
//	public void removeZdgzBkCylInfoFromTreeNode(BkChanYeLianTreeNode childNode,boolean stillkeepincandidate) 
//	{
//		 TreeNode[] nodepath = childNode.getPath();
//		 Boolean isofficallselected = childNode.getNodeTreeRelated().isOfficallySelected();
//		 
//		 DefaultTreeModel model = (DefaultTreeModel) this.getModel();
//		 TreeNode[] tempath = model.getPathToRoot(childNode);
//		 for(int j=1;j<tempath.length;j++ ) {
//		    	BkChanYeLianTreeNode parentnode = (BkChanYeLianTreeNode)tempath[j];
//		    	if(isofficallselected) 
//		    		parentnode.getNodeTreeRelated().decreaseZdgzOfficalCount();
//		    	if(!stillkeepincandidate) //只是从offical移除，candidate还有就不用加
//		    		parentnode.getNodeTreeRelated().decreasedgzCandidateCount();
//		    	
//		    	model.nodeChanged(parentnode);
//		 }
//		
//	}
//	/*
//	 * 
//	 */
//	public void addZdgzBkCylInfoToTreeNode(BkChanYeLianTreeNode childNode, boolean hasbeenincandidate)  
//	{
//		 TreeNode[] nodepath = childNode.getPath();
//		 Boolean isofficallselected = childNode.getNodeTreeRelated().isOfficallySelected();
//		 
////		 DefaultTreeModel treemodel = (DefaultTreeModel) this.getModel();
//		 
//		 DefaultTreeModel model = (DefaultTreeModel) this.getModel();
//		 TreeNode[] tempath = model.getPathToRoot(childNode);
//		 for(int j=1;j<tempath.length;j++ ) {
//		    	BkChanYeLianTreeNode parentnode = (BkChanYeLianTreeNode)tempath[j];
//		    	if(isofficallselected) 
//		    		parentnode.getNodeTreeRelated().increaseZdgzOfficalCount();
//		    	if(!hasbeenincandidate) //如果已经在候选里面，cand count就不用加
//		    		parentnode.getNodeTreeRelated().increaseZdgzCandidateCount();	
//		    	
//		    	model.nodeChanged(parentnode);
//		 }
//		
//	}
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
	public Set<String> getSpecificTypeNodesCodesSet (String nodecode, int nodetype, int requirenodetype)
	{
		Set<String> nodesset = new HashSet<String> ();
		
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


