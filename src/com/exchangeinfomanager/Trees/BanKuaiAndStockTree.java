package com.exchangeinfomanager.Trees;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import javax.swing.tree.TreeNode;


import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.CylTreeNestedSetNode;

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
		ml.activateFilter(false);
		this.setModel(ml);
		
		this.setDragEnabled(true);
		this.setDropMode(javax.swing.DropMode.ON_OR_INSERT);
		this.setEditable(false);
		this.setCellRenderer(new BkChanYeLianTreeCellRenderer());
		this.setRootVisible(true);
		ToolTipManager.sharedInstance().registerComponent(this);
//		transhdlr = new TreeTransferHandler();
//		this.setTransferHandler(transhdlr);
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
//		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
//        ds = new TreeDragSource(this, DnDConstants.ACTION_COPY_OR_MOVE);
//        dt = new TreeDropTarget(this);
		this.createEvents ();
	}
	
	public BanKuaiAndStockTree (InvisibleTreeModel bkcylmodel,String treeid)
	{
		super ();
		
		this.treeid = treeid;
		
		
		bkcylmodel.activateFilter(false);
		this.setModel(bkcylmodel);
		
		this.setDragEnabled(true);
		this.setDropMode(javax.swing.DropMode.ON_OR_INSERT);
		this.setEditable(false);
		this.setCellRenderer(new BkChanYeLianTreeCellRenderer());
		this.setRootVisible(true);
//		transhdlr = new TreeTransferHandler();
//		this.setTransferHandler(transhdlr );
		
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
//		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	
        this.createEvents ();
	}

	
	private static Logger logger = Logger.getLogger(BanKuaiAndStockTree.class);
	private String treeid; //系统要用到两棵cyltree，用ID来区分
	
	private String currentselectedtdxbk;
	private LocalDate currentdisplayedwk; //tree当前所显示的分析州
	protected boolean treechangedshouldsave = false;
//	private BkfxWeeklyFileResultXmlHandler bkfxxml;
	
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
	public void printTreeInformation ()
	{
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.getModel().getRoot();
		printTree(treeroot);
	}
	private  void printTree(BkChanYeLianTreeNode aNode)
	{
	    String name = aNode.toString();
	    int level= aNode.getLevel();
	    String placement = "";
	    while (level > 0)
	    {
	        placement += ">";
	        level--;
	    }
	    if(aNode.isLeaf())
	    {
	        System.out.println(placement + name);
	        return;
	    }

	    System.out.println(placement + "--- " + name + " ---");
	    for(int i = 0 ; i < aNode.getChildCount() ; i++)
	    {
	        printTree((CylTreeNestedSetNode)aNode.getChildAt(i));
	    }
	    System.out.println(placement + "+++ " + name + " +++");
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
	/*
	 * 
	 */
	private void createEvents() 
	{
        this.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
            	treeMousePressed ();
            }
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
//                treeTreeCollapsed(evt);
            }
        });
	}
    /*
     * 
     */
	private void treeMousePressed() 
	{
		try {
			BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) this.getSelectionPath().getLastPathComponent();
			if (parent.isLeaf()) {  
		            return;  
		    }  
			expandTreeNode(parent);
		} catch (java.lang.NullPointerException e) {
			
		}
       
	}
	/*
     * 展开节点,在其他地方也有调用
     */
    public void expandTreePathAllNode (TreePath closestPath)
    {
        	 BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) closestPath.getLastPathComponent();
        	 this.setSelectionPath(closestPath);
        	 this.scrollPathToVisible(closestPath);
         	 expandTreeNode (parent);
    }
    /*
     * 展开所选取的节点
     */
    private void expandTreeNode( BkChanYeLianTreeNode aNode) {  
        if (aNode.isLeaf()) {  
          return;  
        }  
        this.expandPath(new TreePath( ( (BkChanYeLianTreeNode) aNode).getPath()));  
        int n = aNode.getChildCount();  
        for (int i = 0; i <n; i++) {  
          expandTreeNode( (BkChanYeLianTreeNode) aNode.getChildAt(i));  
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
		Enumeration<TreeNode> e = treeroot.depthFirstEnumeration();
	    while (e.hasMoreElements() ) {
	    	BkChanYeLianTreeNode node = (BkChanYeLianTreeNode) e.nextElement();
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
	public Set<String> getSpecificTypeNodesSubCodesSet (String nodecode, int nodetype, int requirenodetype)
	{
		Set<String> nodesset = new HashSet<String> ();
		
		BkChanYeLianTreeNode treeroot = this.getSpecificNodeByHypyOrCode(nodecode, nodetype);
		
		if(treeroot == null)
			return null;
		
	    @SuppressWarnings("unchecked")
		Enumeration<TreeNode> e = treeroot.depthFirstEnumeration();
	    while (e.hasMoreElements() ) {
	    	BkChanYeLianTreeNode node = (BkChanYeLianTreeNode) e.nextElement();
	    	if(node.getType() == requirenodetype) {
	    		nodesset.add(node.getMyOwnCode());
	    	}
	    }
	    
	    return nodesset;
	}
	
	
}



//TreeDragSource.java
//A drag source wrapper for a JTree. This class can be used to make
//a rearrangeable DnD tree with the TransferableTreeNode class as the
//transfer data type.

class TreeDragSource implements DragSourceListener, DragGestureListener 
{

DragSource source;

DragGestureRecognizer recognizer;

TransferableTreeNode transferable;

DefaultMutableTreeNode oldNode;

JTree sourceTree;

public TreeDragSource(JTree tree, int actions) {
  sourceTree = tree;
  source = new DragSource();
  try {
	  recognizer = source.createDefaultDragGestureRecognizer(sourceTree, actions, this);
  } catch (java.awt.dnd.InvalidDnDOperationException e) {
	  e.printStackTrace ();
  }
}

/*
 * Drag Gesture Handler
 */
public void dragGestureRecognized(DragGestureEvent dge) {
  TreePath path = sourceTree.getSelectionPath();
  if ((path == null) || (path.getPathCount() <= 1)) {
    // We can't move the root node or an empty selection
    return;
  }
  oldNode = (DefaultMutableTreeNode) path.getLastPathComponent();
  transferable = new TransferableTreeNode(path);
  try {
	  source.startDrag(dge, DragSource.DefaultMoveNoDrop, transferable, this);
  } catch ( java.awt.dnd.InvalidDnDOperationException e) {
	  e.printStackTrace ();
  }

  // If you support dropping the node anywhere, you should probably
  // start with a valid move cursor:
  //source.startDrag(dge, DragSource.DefaultMoveDrop, transferable,
  // this);
}

/*
 * Drag Event Handlers
 */
public void dragEnter(DragSourceDragEvent dsde) {
}

public void dragExit(DragSourceEvent dse) {
}

public void dragOver(DragSourceDragEvent dsde) {
}

public void dropActionChanged(DragSourceDragEvent dsde) {
  System.out.println("Action: " + dsde.getDropAction());
  System.out.println("Target Action: " + dsde.getTargetActions());
  System.out.println("User Action: " + dsde.getUserAction());
}

public void dragDropEnd(DragSourceDropEvent dsde) {
  /*
   * to support move or copy, we have to check which occurred:
   */
  System.out.println("Drop Action: " + dsde.getDropAction());
  if (dsde.getDropSuccess()
      && (dsde.getDropAction() == DnDConstants.ACTION_MOVE)) {
    ((DefaultTreeModel) sourceTree.getModel()).removeNodeFromParent(oldNode);
  }

  /*
   * to support move only... if (dsde.getDropSuccess()) {
   * ((DefaultTreeModel)sourceTree.getModel()).removeNodeFromParent(oldNode); }
   */
}
}

//TreeDropTarget.java
//A quick DropTarget that's looking for drops from draggable JTrees.
//

class TreeDropTarget implements DropTargetListener 
{

DropTarget target;

JTree targetTree;

public TreeDropTarget(JTree tree) {
  targetTree = tree;
  target = new DropTarget(targetTree, this);
}

/*
 * Drop Event Handlers
 */
private TreeNode getNodeForEvent(DropTargetDragEvent dtde) {
  Point p = dtde.getLocation();
  DropTargetContext dtc = dtde.getDropTargetContext();
  JTree tree = (JTree) dtc.getComponent();
  TreePath path = tree.getClosestPathForLocation(p.x, p.y);
  return (TreeNode) path.getLastPathComponent();
}

public void dragEnter(DropTargetDragEvent dtde) {
  TreeNode node = getNodeForEvent(dtde);

    dtde.acceptDrag(dtde.getDropAction());
  
}

public void dragOver(DropTargetDragEvent dtde) {
  TreeNode node = getNodeForEvent(dtde);

    dtde.acceptDrag(dtde.getDropAction());
  
}

public void dragExit(DropTargetEvent dte) {
}

public void dropActionChanged(DropTargetDragEvent dtde) {
}

public void drop(DropTargetDropEvent dtde) {
  Point pt = dtde.getLocation();
  DropTargetContext dtc = dtde.getDropTargetContext();
  JTree tree = (JTree) dtc.getComponent();
  TreePath parentpath = tree.getClosestPathForLocation(pt.x, pt.y);
  DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentpath.getLastPathComponent();

  try {
    Transferable tr = dtde.getTransferable();
    DataFlavor[] flavors = tr.getTransferDataFlavors();
    for (int i = 0; i < flavors.length; i++) {
      if (tr.isDataFlavorSupported(flavors[i])) {
        dtde.acceptDrop(dtde.getDropAction());
        TreePath p = (TreePath) tr.getTransferData(flavors[i]);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.insertNodeInto(node, parent, 0);
        dtde.dropComplete(true);
        return;
      }
    }
    dtde.rejectDrop();
  } catch (Exception e) {
    e.printStackTrace();
    dtde.rejectDrop();
  }
}
}

//TransferableTreeNode.java
//A Transferable TreePath to be used with Drag & Drop applications.
//

class TransferableTreeNode implements Transferable 
{

public static DataFlavor TREE_PATH_FLAVOR = new DataFlavor(TreePath.class,
    "Tree Path");

DataFlavor flavors[] = { TREE_PATH_FLAVOR };

TreePath path;

public TransferableTreeNode(TreePath tp) {
  path = tp;
}

public synchronized DataFlavor[] getTransferDataFlavors() {
  return flavors;
}

public boolean isDataFlavorSupported(DataFlavor flavor) {
  return (flavor.getRepresentationClass() == TreePath.class);
}

public synchronized Object getTransferData(DataFlavor flavor)
    throws UnsupportedFlavorException, IOException {
  if (isDataFlavorSupported(flavor)) {
    return (Object) path;
  } else {
    throw new UnsupportedFlavorException(flavor);
  }
}
}

         
