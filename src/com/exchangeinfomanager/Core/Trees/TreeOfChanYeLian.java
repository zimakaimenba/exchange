package com.exchangeinfomanager.Core.Trees;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.CylTreeNestedSetNode;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLianGUI;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.database.CylTreeDbOperation;
import com.sun.rowset.CachedRowSetImpl;

public class TreeOfChanYeLian  extends BanKuaiAndStockTree implements CylTreeUpdatedListener
{
//	private CylTreeNestedSetNode treeroot;
	private CylTreeDbOperation dboptforcyl; 
	private HashSet<CylTreeUpdatedListener> treechangedlistenerset;

	public TreeOfChanYeLian (BkChanYeLianTreeNode bkcylrootnode,String treeid)
	{
		super(bkcylrootnode, treeid);
		
		dboptforcyl = new CylTreeDbOperation (this);
		createTreeOfChanYeLian ();
		
		createOwnEvents ();
		
		treechangedlistenerset = new HashSet<> ();
	}
	public void addCylTreeUpdatedListener (CylTreeUpdatedListener listener)
	{
		treechangedlistenerset.add(listener);
	}
	private void createOwnEvents ()
	{
		((InvisibleTreeModel)this.getModel()).addTreeModelListener(new CylTreeModelListener () );
	}
	/*
	 * 
	 */
//	public BkChanYeLianTreeNode getSpecificTreeOfChanYeLianNode (CylTreeNestedSetNode node) //有时候板块和个股代码相同,所以要加上type
//	{
//		String nodecode = node.getMyOwnCode();
//		int requirenodetype = node.getType();
//		int nestedid = node.getNestedId();
//		
//		TreePath bkpath = null ;
//    	BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.getModel().getRoot();
//	    @SuppressWarnings("unchecked")
//		Enumeration<TreeNode> e = treeroot.depthFirstEnumeration();
//	    while (e.hasMoreElements() ) {
//	    	CylTreeNestedSetNode tmpnode = (CylTreeNestedSetNode) e.nextElement();
//	    	Boolean found = tmpnode.checktHanYuPingYin(nodecode);
//	        if (found && tmpnode.getType() == requirenodetype && tmpnode.getNestedId() == nestedid ) {
//	             bkpath = new TreePath(node.getPath());
//	             break;
//	        }
//	    }
//	    
//		if(bkpath != null)	return (BkChanYeLianTreeNode) bkpath.getLastPathComponent();
//		else	return null;
//	}
	class CylTreeModelListener implements TreeModelListener 
	{//https://stackoverflow.com/questions/12607688/jtree-node-title-edited-listener
		public void treeNodesChanged(TreeModelEvent e) {
		}
		
		public void treeNodesInserted(TreeModelEvent e)
		{
			CylTreeNestedSetNode node;
		    node = (CylTreeNestedSetNode) (e.getTreePath().getLastPathComponent());
		    TreeNode[] nodepath = node.getPath();

		    /*
		     * If the event lists children, then the changed
		     * node is the child of the node we have already
		     * gotten.  Otherwise, the changed node and the
		     * specified node are the same.
		     */
		    try {
		        int index = e.getChildIndices()[0];
		        CylTreeNestedSetNode tmpnode = (CylTreeNestedSetNode) (node.getChildAt(index));
		        TreeNode[] path = tmpnode.getPath();
	            dboptforcyl.createTagsOnCurrentTreePathForChanYeLianNode( (CylTreeNestedSetNode) tmpnode);
	            
		        
		    } catch (NullPointerException exc) {}
		}
		public void treeNodesRemoved(TreeModelEvent e) 
		{	//System.out.println("CYL Tree changed");
			treechangedlistenerset.forEach(l -> l.ChanYeLianTreeUpdated());
		}
		public void treeStructureChanged(TreeModelEvent e) 
		{
//			System.out.println("CYL Tree changed");
			treechangedlistenerset.forEach(l -> l.ChanYeLianTreeUpdated());
		}
	}
	
	private void createTreeOfChanYeLian ()
	{
		 List<BkChanYeLianTreeNode> nodelist = dboptforcyl.createTreeOfChanYeLian();
		 CylTreeNestedSetNode treeroot = (CylTreeNestedSetNode)this.getModel().getRoot();
		 treeroot.setNestedId (1);

		 InvisibleTreeModel treemodel = (InvisibleTreeModel)this.getModel();
		 for(BkChanYeLianTreeNode tmpnode : nodelist) {
			  String nodename = tmpnode.getMyOwnName();
			  Integer parent_id = ( (CylTreeNestedSetNode)tmpnode).getNestedParent ();
			  Enumeration<TreeNode> e = treeroot.breadthFirstEnumeration();
			  while (e.hasMoreElements() ) {
			    	CylTreeNestedSetNode node = (CylTreeNestedSetNode) e.nextElement();
			    	int nodeid = node.getNestedId();
			        if (parent_id == nodeid) {
			             treemodel.insertNodeInto(tmpnode, node, node.getChildCount());
			             break;
			         }
			    }
		 }
		 
		 this.setTransferHandler(new TreeTransferHandler() );
	}
	/*
	 * 
	 */
	public Collection<BkChanYeLianTreeNode> getGuoPiaoChi ()
	{
		Collection<BkChanYeLianTreeNode> gpcset = new HashSet<> ();
		
		CylTreeNestedSetNode treeroot = (CylTreeNestedSetNode)this.getModel().getRoot();
		 int bankuaicount = this.getModel().getChildCount(treeroot);
		 for(int i=0;i< bankuaicount; i++) {
			 BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.getModel().getChild(treeroot, i);
			 gpcset.add(childnode);
		 }
		 
		 return gpcset;
	}
	/*
	 * 
	 */
	public void addNewNodeToTree (BkChanYeLianTreeNode node, int direction)
	{
		CylTreeNestedSetNode cylnode = new CylTreeNestedSetNode (node.getMyOwnCode(),node.getMyOwnName(),node.getType());
		BkChanYeLianTreeNode parent;
		try { parent = (BkChanYeLianTreeNode) this.getSelectionPath().getLastPathComponent();
		} catch (java.lang.NullPointerException e) { JOptionPane.showMessageDialog(null,"请选择父节点!"); 
			return;
		}
		
		if(parent.getType() == BkChanYeLianTreeNode.GPC  ) {
			if (direction == BanKuaiAndChanYeLianGUI.DOWN || direction == BanKuaiAndChanYeLianGUI.UP ) 
				direction = BanKuaiAndChanYeLianGUI.RIGHT;
		}
		
		boolean enableoperation = this.checkNodeDuplicate (parent,cylnode);
    	if( enableoperation ) { 		JOptionPane.showMessageDialog(null,"同级中已经存在相同名称子版块或与父节点重名，不能重复添加!");
        		return;
        }
    	
    	try {	this.dboptforcyl.addNodeToNestedDatabase ((CylTreeNestedSetNode)parent,(CylTreeNestedSetNode)cylnode);
		} catch (SQLException e) {e.printStackTrace();	return;}
    	
        if (direction == BanKuaiAndChanYeLianGUI.RIGHT)    	parent.add(cylnode);
        
        if (direction != BanKuaiAndChanYeLianGUI.RIGHT) {
        	BkChanYeLianTreeNode currentNode = (BkChanYeLianTreeNode) this.getSelectionPath().getLastPathComponent();
        	parent = (BkChanYeLianTreeNode) currentNode.getParent();

        	enableoperation = this.checkNodeDuplicate (parent,cylnode);
        	if( enableoperation ) {   		JOptionPane.showMessageDialog(null,"同级中已经存在相同名称子版块或与父节点重名，不能重复添加!");
            		return;
            }
            
            int childIndex = parent.getIndex(currentNode);
            if (direction == BanKuaiAndChanYeLianGUI.DOWN) 
            	parent.insert(cylnode, childIndex+1);
            else if (direction == BanKuaiAndChanYeLianGUI.UP) 
            	parent.insert(cylnode, childIndex);
        }
        
       InvisibleTreeModel treeModel = (InvisibleTreeModel)this.getModel();
       BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treeModel.getRoot();
       treeModel.nodesWereInserted(cylnode.getParent(), new int[] {cylnode.getParent().getIndex(cylnode)});
       treeModel.reload(treeroot);
       this.setSelectionPath(new TreePath(((BkChanYeLianTreeNode)cylnode.getParent()).getPath()));
       this.scrollPathToVisible( new TreePath(cylnode.getPath()));
	}
	
	
	class TreeTransferHandler extends TransferHandler {
	    DataFlavor nodesFlavor;
	    DataFlavor[] flavors = new DataFlavor[1];
	    BkChanYeLianTreeNode[] nodesToRemove;
	    BkChanYeLianTreeNode movingancesternode; //移动的根节点
	  
	    public TreeTransferHandler() 
	    {
	        try {
	            String mimeType = DataFlavor.javaJVMLocalObjectMimeType +
	                              ";class=\"" +
	                javax.swing.tree.DefaultMutableTreeNode[].class.getName() +
	                              "\"";
	            nodesFlavor = new DataFlavor(mimeType);
	            flavors[0] = nodesFlavor;
	        } catch(ClassNotFoundException e) {
	            System.out.println("ClassNotFound: " + e.getMessage());
	        }
	    }

	    public boolean canImport(TransferHandler.TransferSupport support) 
	    {
	        if(!support.isDrop()) {
	            return false;
	        }
	        support.setShowDropLocation(true);
	        if(!support.isDataFlavorSupported(nodesFlavor)) {
	            return false;
	        }
	        // Do not allow a drop on the drag source selections.
	        JTree.DropLocation dl =  (JTree.DropLocation)support.getDropLocation();
	        BanKuaiAndStockTree tree = (BanKuaiAndStockTree)support.getComponent();
	        int dropRow = tree.getRowForPath(dl.getPath());
	        int[] selRows = tree.getSelectionRows();
	        for(int i = 0; i < selRows.length; i++) {
	            if(selRows[i] == dropRow) {
	                return false;
	            }
	        }
	        // Do not allow MOVE-action drops if a non-leaf node is
	        // selected unless all of its children are also selected.
	        int action = support.getDropAction();
	        if(action == MOVE) {
	            return haveCompleteNode(tree);
	        }
	        // Do not allow a non-leaf node to be copied to a level
	        // which is less than its source level.
	        TreePath dest = dl.getPath();
	        BkChanYeLianTreeNode target =          (BkChanYeLianTreeNode)dest.getLastPathComponent();
	        TreePath path = tree.getPathForRow(selRows[0]);
	        BkChanYeLianTreeNode firstNode =         (BkChanYeLianTreeNode)path.getLastPathComponent();
	        if(firstNode.getChildCount() > 0 &&     target.getLevel() < firstNode.getLevel()) {
	            return false;
	        }
	        
	      //不可以move到GPC一级
	        if( ((BkChanYeLianTreeNode)target).getType() == BkChanYeLianTreeNode.DAPAN )
	        	return false;
	        
	        return true;
	    }
	  
	    private boolean haveCompleteNode(JTree tree) {
//	        int[] selRows = tree.getSelectionRows();
//	        TreePath path = tree.getPathForRow(selRows[0]);
//	        DefaultMutableTreeNode first =
//	            (DefaultMutableTreeNode)path.getLastPathComponent();
//	        int childCount = first.getChildCount();
//	        // first has children and no children are selected.
//	        if(childCount > 0 && selRows.length == 1)
//	            return false;
//	        // first may have children.
//	        for(int i = 1; i < selRows.length; i++) {
//	            path = tree.getPathForRow(selRows[i]);
//	            DefaultMutableTreeNode next =
//	                (DefaultMutableTreeNode)path.getLastPathComponent();
//	            if(first.isNodeChild(next)) {
//	                // Found a child of first.
//	                if(childCount > selRows.length-1) {
//	                    // Not all children of first are selected.
//	                    return false;
//	                }
//	            }
//	        }
	        return true;
	    }
	  
	    protected Transferable createTransferable(JComponent c) {
	    	BanKuaiAndStockTree tree = (BanKuaiAndStockTree)c;
	        TreePath[] paths = tree.getSelectionPaths();
	        TreePath pathresult = paths[0];
	        if(paths != null) {
	            // Make up a node array of copies for transfer and
	            // another for/of the nodes that will be removed in
	            // exportDone after a successful drop.
	            List<BkChanYeLianTreeNode> copies =  new ArrayList<>();
	            List<BkChanYeLianTreeNode> toRemove = new ArrayList<>();
	            BkChanYeLianTreeNode node =  (BkChanYeLianTreeNode)paths[0].getLastPathComponent();
	            movingancesternode = node;
	           
	            findNodeSubNodes (node, copies, toRemove);
            
	            Collections.sort(copies, new FatherNodeIdComparator () );
	            BkChanYeLianTreeNode[] nodes =   copies.toArray(new CylTreeNestedSetNode[copies.size()]);
	            nodesToRemove =    toRemove.toArray(new CylTreeNestedSetNode[toRemove.size()]);
	            
	            for(int i=0; i<nodesToRemove.length; i++) { //把node的产业链上的所有TAG删除
	            	TreeNode[] nodepa = nodesToRemove[i].getPath();
	            	dboptforcyl.deleteTagsOnCurrentTreePathForChanYeLianNode( (CylTreeNestedSetNode) nodesToRemove[i]);
	            }
	            
	            return new NodesTransferable(nodes);
	            
	        }
	        return null;
	    }
	    
	    private void findNodeSubNodes (BkChanYeLianTreeNode parentnode,List<BkChanYeLianTreeNode> copylist, List<BkChanYeLianTreeNode> removelist)
	    {
	    	copylist.add(copy(parentnode) );
			removelist.add (parentnode);
			
	    	if(parentnode.isLeaf())
	    		return;

	    	for(int i = 0 ; i < parentnode.getChildCount() ; i++)
		    {
		    	findNodeSubNodes((BkChanYeLianTreeNode)parentnode.getChildAt(i),copylist, removelist);
		    }
	    }
	    /** Defensive copy used in createTransferable. */
	    private BkChanYeLianTreeNode copy(BkChanYeLianTreeNode node) 
	    {
	    	BkChanYeLianTreeNode tmpnode =  new CylTreeNestedSetNode(node.getMyOwnCode(), node.getMyOwnName(),node.getType()  );
	    	int nestedid = ((CylTreeNestedSetNode)node).getNestedId();
	    	((CylTreeNestedSetNode)tmpnode).setNestedId(nestedid);
	    	
	    	CylTreeNestedSetNode fn = (CylTreeNestedSetNode)node.getParent();
	        int parentid = ((CylTreeNestedSetNode)node.getParent()).getNestedId();
	        ((CylTreeNestedSetNode)tmpnode).setNestedParent(parentid);
	        return tmpnode;
	    }
	    /*
	     * 
	     */
	    protected void exportDone(JComponent source, Transferable data, int action) {
	        if((action & MOVE) == MOVE) {
	            JTree tree = (JTree)source;
	            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
	            // Remove nodes saved in nodesToRemove in createTransferable.
	            for(int i = 0; i < nodesToRemove.length; i++) {
	            	TreeNode[] nodep = nodesToRemove[i].getPath();
	            	
	            	nodesToRemove[i].getParent();
	                model.removeNodeFromParent(nodesToRemove[i]);
	            }
	        }
	        
	        nodesToRemove = null;
	    }
	  
	    public int getSourceActions(JComponent c) {
	        return COPY_OR_MOVE;
	    }
	  
	    public boolean importData(TransferHandler.TransferSupport support) {
	        if(!canImport(support)) {
	            return false;
	        }
	        // Extract transfer data.
	        BkChanYeLianTreeNode[] nodes = null;
	        try {
	            Transferable t = support.getTransferable();
	            nodes = (BkChanYeLianTreeNode[])t.getTransferData(nodesFlavor);
	        } catch(UnsupportedFlavorException ufe) {
	            System.out.println("UnsupportedFlavor: " + ufe.getMessage());
	        } catch(java.io.IOException ioe) {
	            System.out.println("I/O error: " + ioe.getMessage());
	        }
	        
	        // Get drop location info.
	        JTree.DropLocation dl =    (JTree.DropLocation)support.getDropLocation();
	        int childIndex = dl.getChildIndex();
	        TreePath dest = dl.getPath();
	        BkChanYeLianTreeNode parent =   (BkChanYeLianTreeNode)dest.getLastPathComponent();
	        BanKuaiAndStockTree tree = (BanKuaiAndStockTree)support.getComponent();
	        InvisibleTreeModel model = (InvisibleTreeModel)tree.getModel();
	        // Configure for drop mode.
	        int index = childIndex;    // DropMode.INSERT
	        if(childIndex == -1) {     // DropMode.ON
	            index = parent.getChildCount();
	        }
	        
	        
	        // Add data to model.
	        BkChanYeLianTreeNode tmpfather = null;
	        for(int i = 0; i < nodes.length; i++) {
	        	String nodecode = ((BkChanYeLianTreeNode)nodes[i]).getMyOwnCode ();
	        	if(nodecode.equals (movingancesternode.getMyOwnCode () ) )
	        		model.insertNodeInto(nodes[i], parent, index++);
	        	
	        	tmpfather = nodes[i];
	        	break;
	        }
	        for(int i = 0; i < nodes.length; i++) {
	        	String nodecode = ((BkChanYeLianTreeNode)nodes[i]).getMyOwnCode ();
	        	if(!nodecode.equals (movingancesternode.getMyOwnCode () ) )	 {
	        		int nodefatherid = ((CylTreeNestedSetNode)nodes[i]).getNestedParent ();
	        		
	        		Enumeration<TreeNode> e = tmpfather.breadthFirstEnumeration();
	        		while (e.hasMoreElements() ) {
				    	CylTreeNestedSetNode possiblefather = (CylTreeNestedSetNode) e.nextElement();
				    	int possiblefatherid = possiblefather.getNestedId();
				    	
				        if (possiblefatherid == nodefatherid) {
				             model.insertNodeInto(nodes[i], possiblefather, possiblefather.getChildCount() );
				             break;
				         }
				    }
	        	}
	        }
	        
	        dboptforcyl.moveNodeInNestedDatabase ( (CylTreeNestedSetNode)movingancesternode, (CylTreeNestedSetNode)parent);
	        
	        return true;
	    }
	  
	    public String toString() {
	        return getClass().getName();
	    }
	  
	    public class NodesTransferable implements Transferable 
	    {
	    	BkChanYeLianTreeNode[] nodes;
	  
	        public NodesTransferable(BkChanYeLianTreeNode[] nodes) {
	            this.nodes = nodes;
	         }
	  
	        public Object getTransferData(DataFlavor flavor)
	                                 throws UnsupportedFlavorException {
	            if(!isDataFlavorSupported(flavor))
	                throw new UnsupportedFlavorException(flavor);
	            return nodes;
	        }
	  
	        public DataFlavor[] getTransferDataFlavors() {
	            return flavors;
	        }
	  
	        public boolean isDataFlavorSupported(DataFlavor flavor) {
	            return nodesFlavor.equals(flavor);
	        }
	    }
	}

	
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
	    	removeTagesFromNodeWhenDeleteNodes (child);
	            	
	        BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) child.getParent();
	        if (parent != null){
	            int childIndex = parent.getIndex(child);
	            //删除node在产业链上的TAG
	            dboptforcyl.deleteTagsOnCurrentTreePathForChanYeLianNode ((CylTreeNestedSetNode)child);
	                    
	             parent.remove(child);

	             treeModel.nodesWereRemoved(parent, new int[] {childIndex}, new Object[] {child});
	        }
	      }
	            
	      if (this.getVisibleRowCount()>0) 
	           this.setSelectionRow(topRow);
	   
	   return true;
	}
	private void removeTagesFromNodeWhenDeleteNodes (BkChanYeLianTreeNode deletenode)
	{
		int childcount = deletenode.getChildCount();
    	for(int i=0;i<childcount;i++) {
    		TreeNode childnode = deletenode.getChildAt(i);
    		dboptforcyl.deleteTagsOnCurrentTreePathForChanYeLianNode ((CylTreeNestedSetNode)childnode);
    		
    		removeTagesFromNodeWhenDeleteNodes ((BkChanYeLianTreeNode) childnode);
    	}
	}
	
	public boolean isolatedNodes(TreePath selectednode) 
	{
		this.setSelectionPath(selectednode);
		TreePath[] treePaths = this.getSelectionPaths();
		sortPaths(this,treePaths);
	    int topRow = this.getRowForPath(treePaths[0]);
	    for (TreePath path : treePaths) {
	    	CylTreeNestedSetNode child = (CylTreeNestedSetNode) path.getLastPathComponent();
	    	child.setNodeIsolatedDate(LocalDate.now());
	      }
	            
	      if (this.getVisibleRowCount()>0) 
	            	this.setSelectionRow(topRow);
	   
	   return true;
	}
	
	public BkChanYeLianTreeNode getChanYeLianInfo(BkChanYeLianTreeNode node) 
	{
		List<BkChanYeLianTreeNode> result = this.getChanYeLianInfo(node.getMyOwnCode(), node.getType());
		node.getNodeJiBenMian().setChanYeLianInfo(result);
		
		return node;
	}
	public  List<BkChanYeLianTreeNode> getChanYeLianInfo (String nodecode, int nodetype)
	{
		List<BkChanYeLianTreeNode> cylinfo = dboptforcyl.getChanYeLianInfo(nodecode, nodetype);
		
		return cylinfo;
	}
	public List<BkChanYeLianTreeNode>  getSlidingInChanYeLianInfo (String nodecode, int nodetype)
	{
		return dboptforcyl.getSlidingInChanYeLianInfo(nodecode, nodetype);
	}
	public List<BkChanYeLianTreeNode> getChildrenInChanYeLianInfo(String nodecode, int nodetype) 
	{
		return dboptforcyl.getChildrenInChanYeLianInfo(nodecode, nodetype);
	}
	public void deleteNodeFromTree(CylTreeNestedSetNode delnode, boolean deleteforever) 
	{
		if(! deleteforever) {
			isolatedNodes (delnode);
		} else
			deleteNodes (delnode);
	}
	private void isolatedNodes (CylTreeNestedSetNode delnode)
	{
		//在数据库中标记
		isolatedNodeInDb (delnode);
		
		this.isolatedNodes (   new TreePath (delnode.getPath() ) );  //在树上标记
	}
	private  void isolatedNodeInDb(CylTreeNestedSetNode aNode)
	{
		dboptforcyl.isolatedNodeInDb (aNode);
	}
	private void deleteNodes (CylTreeNestedSetNode delnode)
	{
		//在数据中删除
		deleteNodeInDb (delnode);
		
		this.deleteNodes (   new TreePath (delnode.getPath() ) );  //在树上删除
	}
	private  void deleteNodeInDb (CylTreeNestedSetNode aNode)
	{
		dboptforcyl.deleteNodeInDb (aNode);
	}
	public BkChanYeLianTreeNode addNewGuoPiaoChi(String gpccode, String gpcname)
	{
		return dboptforcyl.addNewGuoPiaoChi( gpccode,  gpcname);
	}
	
	@Override //CylTreeUpdatedListener
	public void ChanYeLianTreeUpdated() {
//		System.out.println("TIME TO UPDATED myslef");
		DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		root.removeAllChildren();
		createTreeOfChanYeLian ();
	}
}
/*
 * 按父亲节点
 */
class FatherNodeIdComparator implements Comparator<BkChanYeLianTreeNode> {
	
	public FatherNodeIdComparator ( )
	{
	}
    public int compare(BkChanYeLianTreeNode node1, BkChanYeLianTreeNode node2) {
    	 Integer node1p = ( (CylTreeNestedSetNode)node1).getNestedParent ();
    	 Integer node2p = ( (CylTreeNestedSetNode)node2).getNestedParent ();
        
        return node1p.compareTo(node2p);
    }
}