package com.exchangeinfomanager.Trees;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.JTree;
import javax.swing.tree.*;

import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.CylTreeNestedSetNode;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

public class TreeTransferHandler extends TransferHandler
{
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
//        int[] selRows = tree.getSelectionRows();
//        TreePath path = tree.getPathForRow(selRows[0]);
//        DefaultMutableTreeNode first =
//            (DefaultMutableTreeNode)path.getLastPathComponent();
//        int childCount = first.getChildCount();
//        // first has children and no children are selected.
//        if(childCount > 0 && selRows.length == 1)
//            return false;
//        // first may have children.
//        for(int i = 1; i < selRows.length; i++) {
//            path = tree.getPathForRow(selRows[i]);
//            DefaultMutableTreeNode next =
//                (DefaultMutableTreeNode)path.getLastPathComponent();
//            if(first.isNodeChild(next)) {
//                // Found a child of first.
//                if(childCount > selRows.length-1) {
//                    // Not all children of first are selected.
//                    return false;
//                }
//            }
//        }
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
//            int pathcount = pathresult.getPathCount();
//            for(int i = 1; i < pathresult.getPathCount(); i++) {
//            	BkChanYeLianTreeNode next =  (BkChanYeLianTreeNode)pathresult[i].getLastPathComponent();
//                // Do not allow higher level nodes to be added to list.
//                if(next.getLevel() < node.getLevel()) {
//                    break;
//                } else if(next.getLevel() > node.getLevel()) {  // child node
//                    copy.add(copy(next));
//                    // node already contains child
//                } else {                                        // sibling
//                    copies.add(copy(next));
//                    toRemove.add(next);
//                }
//            }
            
            Collections.sort(copies, new FatherNodeIdComparator () );
            BkChanYeLianTreeNode[] nodes =   copies.toArray(new CylTreeNestedSetNode[copies.size()]);
            nodesToRemove =    toRemove.toArray(new CylTreeNestedSetNode[toRemove.size()]);
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
                model.removeNodeFromParent(nodesToRemove[i]);
            }
        }
        nodesToRemove = null;
        
//        PropertyChangeEvent evtzd = new PropertyChangeEvent(this, "NODEMOVED", "", movingancesternode.getMyOwnCode() );
//        pcs.firePropertyChange(evtzd);
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