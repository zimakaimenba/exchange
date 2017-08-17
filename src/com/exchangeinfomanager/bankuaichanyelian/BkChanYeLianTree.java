package com.exchangeinfomanager.bankuaichanyelian;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.tree.TreeNode;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import net.ginkgo.dom4jcopy.TreeTransferHandler;


public class BkChanYeLianTree extends JTree 
{
	public BkChanYeLianTree (BkChanYeLianTreeNode bkcylrootnode)
	{
		super(bkcylrootnode);
		this.setDragEnabled(false);
		this.setDragEnabled(true);
		this.setDropMode(javax.swing.DropMode.ON_OR_INSERT);
		this.setEditable(true);
		
		this.setCellRenderer(new BkChanYeLianTreeCellRenderer());
		
		this.setRootVisible(false);
		this.setTransferHandler(new TreeTransferHandler());
		
	      //CellEditor cellEditor = new CellEditor(this); 
//      treechanyelian.setCellEditor(new CustomTreeCellEditor(treechanyelian, (DefaultTreeCellRenderer) treechanyelian.getCellRenderer(),
//          cellEditor));
      //treechanyelian.setInvokesStopCellEditing(true);
//		treeModel.nodeStructureChanged(topNode);
//        setModifiedTitle(false);

		 bkdbopt = new BanKuaiDbOperation(); 
		 hypy = new HanYuPinYing ();
		this.createEvents(this);
//      
	}
	
	private String currentselectedtdxbk;
	private boolean ignoreExpansion = false;
	private BanKuaiDbOperation bkdbopt;
	private HanYuPinYing hypy;
	
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
	
	private void treeTreeCollapsed(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_treeTreeCollapsed
        if (!ignoreExpansion)
        ((BkChanYeLianTreeNode) evt.getPath().getLastPathComponent()).setExpansion(false);
    }//GEN-LAST:event_treeTreeCollapsed
    private void treeTreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_treeTreeExpanded
        if (!ignoreExpansion )
        ((BkChanYeLianTreeNode) evt.getPath().getLastPathComponent()).setExpansion(true);
    }//GEN-LAST:event_treeTreeExpanded

    /*
     * 
     */
	private void treeMousePressed(MouseEvent evt) 
	{
		TreePath closestPath = this.getClosestPathForLocation(evt.getX(), evt.getY());
	       
        //System.out.println(closestPath);
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
     * 展开节点
     */
    public void expandTreePathAllNode (TreePath closestPath)
    {
    	 String tdxbk = closestPath.getPathComponent(1).toString();
//         if(!tdxbk.equals(currentselectedtdxbk)) { //和当前的板块不一样，
        	 BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) closestPath.getLastPathComponent();
        	 this.setSelectionPath(closestPath);
        	 this.scrollPathToVisible(closestPath);
         	 expandTreeNode (this,parent);
//         }
         
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

	
	public void updateTreeParseFileInfo (HashSet<String> stockinfile)
	{
		DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		BkChanYeLianTreeNode root = (BkChanYeLianTreeNode) model.getRoot();
		updateParseFileInfoToTree (root,stockinfile);
		
		
		model.nodeStructureChanged(root);
		

	}
	private void updateParseFileInfoToTree(BkChanYeLianTreeNode node, HashSet<String> stockinfile) 
	{
		int childCount = node.getChildCount();

	    for (int i = 0; i < childCount; i++) {
	    	BkChanYeLianTreeNode childNode = (BkChanYeLianTreeNode) node.getChildAt(i);
	    	String bkname = childNode.getUserObject().toString();
	    	
	    	int childNodetype = childNode.getNodeType();
	    	childNode.clearCurParseFileStockSet ( );

	    	if( childNodetype == BkChanYeLianTreeNode.TDXBK ) {
	    		HashMap<String,String> tmpallbkge = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (bkname);
	    		Set<String> curbkallbkset = tmpallbkge.keySet();
				SetView<String>  intersectionbankuai = Sets.intersection(stockinfile, curbkallbkset );
				
				if(intersectionbankuai.size() >0 ) { //有交集
					HashSet<String> stockbkresult = new HashSet<String> (intersectionbankuai);
					childNode.setParseFileStockSet (stockbkresult); // 把交集保存
				}
	    	}
	    	
	    	if( childNodetype == BkChanYeLianTreeNode.BKGEGU ) {
	    		String gegustockcode = childNode.getUserObject().toString().substring(0, 6);
	    		Set<String> curbkallbkset = new HashSet<String> ();
	    		curbkallbkset.add(gegustockcode);
	    		SetView<String>  intersectionbankuai = Sets.intersection(stockinfile, curbkallbkset);
	    		
     		    if(intersectionbankuai.size() >0 ) { //有交集
     		    	HashSet<String> stockbkresult = new HashSet<String> (intersectionbankuai);
					childNode.setParseFileStockSet (stockbkresult); // 把交集保存
					
				    //如果是个股节点，则要把上面所有父节点(子产业链也标记这个个股)
				     DefaultTreeModel model = (DefaultTreeModel) this.getModel();
					 TreeNode[] tempath = model.getPathToRoot(childNode);
					 for(int j=0;j<tempath.length-1;j++ ) {
					    	BkChanYeLianTreeNode parentnode = (BkChanYeLianTreeNode)tempath[j];
					    	int nodeparentstype = parentnode.getNodeType();
					    	if(nodeparentstype == BkChanYeLianTreeNode.SUBBK) { //如果是子产业链，
					    			 parentnode.setParseFileStockSet(stockbkresult);
					    	}
					 }
				      
			      }
	    	}
	    	
	        if (childNode.getChildCount() > 0) {
	        	updateParseFileInfoToTree(childNode,stockinfile);
	        } 
	    }
	}

	

	public void addNewNode(int addnodetype, String subbkname, int direction)
	{
		if (this.getSelectionCount() == 1) {

			BkChanYeLianTreeNode newNode = new BkChanYeLianTreeNode(subbkname);
            newNode.setNodeType(addnodetype);
            newNode.setHanYuPingYin(hypy.getBanKuaiNameOfPinYin(subbkname));
            
            if (direction == BanKuaiAndChanYeLian.RIGHT){
            	BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) this.getSelectionPath().getLastPathComponent();
                
                boolean enableoperation = checkNodeDuplicate (parent,newNode);
            	if( enableoperation ) {
                		JOptionPane.showMessageDialog(null,"同级中已经存在相同名称子版块或与父节点重名，不能重复添加!");
                		return;
                }
                
                if( parent.getNodeType() != BkChanYeLianTreeNode.BKGEGU) { //父节点不是个股，可以加
                	parent.add(newNode);
	                parent.setExpansion(true);
                } else { ////父节点是个股，不可以加
                	System.out.println("父节点是个股，不能加板块");
                	direction = BanKuaiAndChanYeLian.DOWN;
                }
                
            } 
            
            if (direction != BanKuaiAndChanYeLian.RIGHT){
            	BkChanYeLianTreeNode currentNode = (BkChanYeLianTreeNode) this.getSelectionPath().getLastPathComponent();
            	BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) currentNode.getParent();
                
                boolean enableoperation = checkNodeDuplicate (parent,newNode);
            	if( enableoperation ) {
                		JOptionPane.showMessageDialog(null,"同级中已经存在相同名称子版块或与父节点重名，不能重复添加!");
                		return;
                }
                
                int childIndex = parent.getIndex(currentNode);
                if (direction == BanKuaiAndChanYeLian.DOWN) parent.insert(newNode, childIndex+1);
                else if (direction == BanKuaiAndChanYeLian.UP) parent.insert(newNode, childIndex);
                else return;
            }

            nodeDnaFromParentToChild ( (BkChanYeLianTreeNode)newNode.getParent(),newNode);
            DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
            treeModel.nodesWereInserted(newNode.getParent(), new int[] {newNode.getParent().getIndex(newNode)});
            this.startEditingAtPath(new TreePath(newNode.getPath()));

        }
	}
	
    private void nodeDnaFromParentToChild(BkChanYeLianTreeNode parent, BkChanYeLianTreeNode child) 
    {
    	String suoshubkcode = parent.getTDXBanKuaiZhiShuCode();
    	child.setTDXBanKuaiZhiShuCode(suoshubkcode);
	}

	private boolean checkNodeDuplicate(BkChanYeLianTreeNode parent,  BkChanYeLianTreeNode newNode) 
    {
    	String gegucodename = newNode.getUserObject().toString().trim();
    	String parentname = parent.getUserObject().toString().trim();
    	
    	if(gegucodename.equals(parentname))
    		return true;
    	
    	int childnum = parent.getChildCount();
        for(int i=0;i<childnum;i++) {
        	BkChanYeLianTreeNode tmpnode = (BkChanYeLianTreeNode)parent.getChildAt(i);
        	String tmpnodename = tmpnode.getUserObject().toString();
        	System.out.println(tmpnodename);
        	if(tmpnodename.equals(gegucodename)) {
        		return true;
        	}
        }

		return false;
	}

    @SuppressWarnings("unchecked")
    private void sortPaths(BkChanYeLianTree bkChanYeLianTree, TreePath[] treePaths){
        Arrays.sort(treePaths, new java.util.Comparator(){
            public int compare(Object path1, Object path2){
            	
                int row1 = bkChanYeLianTree.getRowForPath((TreePath) path1);
                int row2 = bkChanYeLianTree.getRowForPath((TreePath) path2);
                if (row1 < row2) return -1;
                else if (row1 > row2) return 1;
                else return 0;
        }});
    }
	public void moveNode(int direction) 
	{
		if (this.getSelectionCount()==0) return;
        
		DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
        TreePath parentPath = this.getSelectionPath().getParentPath();
        TreePath[] treePaths = this.getSelectionPaths();
        final BkChanYeLianTreeNode parentNode = (BkChanYeLianTreeNode) parentPath.getLastPathComponent();
        //String tmpstr = parentPath.toString();
        if(parentNode.getNodeType() == BkChanYeLianTreeNode.TDXBK || parentPath.toString().equals("[JTree]"))
        	return;
        
        //unselect non-siblings
        for (TreePath treePath : treePaths)
            if (!parentPath.equals(treePath.getParentPath()))
                this.removeSelectionPath(treePath);
        treePaths = this.getSelectionPaths();
        
        sortPaths(this,treePaths);
        
        BkChanYeLianTreeNode node=null, newParentNode;
        int index;
        if (direction == BanKuaiAndChanYeLian.UP){
            this.clearSelection();
            for (TreePath treePath : treePaths){
                node = (BkChanYeLianTreeNode) treePath.getLastPathComponent();
                index = parentNode.getIndex(node);
                if (index == 0) {
                    this.setSelectionPaths(treePaths);
                    return;
                }
                else {
                    parentNode.remove(index);
                    treeModel.nodesWereRemoved(parentNode, new int[] {index}, new Object[] {node});
                    parentNode.insert(node, index-1);
                    treeModel.nodesWereInserted(parentNode, new int[] {index - 1});
                }
            }
            this.setSelectionPaths(treePaths);
            this.scrollPathToVisible(treePaths[0]);
        }
        else if (direction == BanKuaiAndChanYeLian.DOWN){
            this.clearSelection();
            for (int i=treePaths.length - 1; i>=0; i--){
                node = (BkChanYeLianTreeNode) treePaths[i].getLastPathComponent();
                index = parentNode.getIndex(node);
                if (index == parentNode.getChildCount()-1) {
                    this.setSelectionPaths(treePaths);
                    return;
                }
                else {
                    parentNode.remove(index);
                    treeModel.nodesWereRemoved(parentNode, new int[] {index}, new Object[] {node});
                    parentNode.insert(node, index+1);
                    treeModel.nodesWereInserted(parentNode, new int[] {index + 1});
                }
            }
            this.setSelectionPaths(treePaths);
            this.scrollPathToVisible(treePaths[treePaths.length-1]);
            
        }
        else if (direction==BanKuaiAndChanYeLian.LEFT)
            
            
                this.clearSelection();
                newParentNode = (BkChanYeLianTreeNode)parentNode.getParent();
            
                for (TreePath treePath : treePaths){
                    node = (BkChanYeLianTreeNode) treePath.getLastPathComponent();
                    index = parentNode.getIndex(node);
                    parentNode.remove(index);
                    treeModel.nodesWereRemoved(parentNode, new int[]{index}, new Object[]{node});
                    newParentNode.add(node);
                    treeModel.nodesWereInserted(newParentNode, new int[] {newParentNode.getIndex(node)});
                    this.addSelectionPath(new TreePath(node.getPath()));
                }
                this.scrollPathToVisible(new TreePath(node.getPath()));
           

	
	}

	public boolean deleteNodes() 
	{
		DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
		if (this.getSelectionCount() > 0){
	            TreePath[] treePaths = this.getSelectionPaths();
	            sortPaths(this,treePaths);
	            int topRow = this.getRowForPath(treePaths[0]);
	            for (TreePath path : treePaths){
	            	BkChanYeLianTreeNode child = (BkChanYeLianTreeNode) path.getLastPathComponent();
	            	
	            	if(child.getNodeType() == BkChanYeLianTreeNode.TDXBK) {
	            		JOptionPane.showMessageDialog(null,"所选为通达信板块，无法删除！","Warning",JOptionPane.WARNING_MESSAGE);
	            		return false;
	            	}
	            	if(child.getInZdgzOfficalCount()>0 || child.getInZdgzCandidateCount()>0) {
	            		JOptionPane.showMessageDialog(null,"所选产业链是关注股票池候选或正式选择，请先在关注股票池中移除后再删除！","Warning",JOptionPane.WARNING_MESSAGE);
	            		return false;
	            	}
	            		
	            	
	            	BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) child.getParent();
	                if (parent != null){
	                    
	                    int childIndex = parent.getIndex(child);
	                    parent.remove(child);
	                    treeModel.nodesWereRemoved(parent, new int[] {childIndex}, new Object[] {child});
	                    if (parent.getChildCount()==0) parent.setExpansion(false);
	                }
	            }
	            
	            if (this.getVisibleRowCount()>0) 
	            	this.setSelectionRow(topRow);
	   }
	
	   return true;
	}


	/*
	 * 把树节点存储的产业链转化为arraylist
	 */
	private ArrayList<String> getFormatedBanKuaiChanYeLian (String bkcyl)
	{
		List<String> tmpcyltreepathlist =  Splitter.on("->").trimResults().omitEmptyStrings().splitToList(bkcyl);  
		System.out.println(tmpcyltreepathlist);
		
		ArrayList<String> cyltreepathlist = new ArrayList<String>(tmpcyltreepathlist);
		System.out.println(cyltreepathlist);

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
	    	String childnodename = childNode.getUserObject().toString(); 
	    	if(childNode.getNodeType() == BkChanYeLianTreeNode.TDXBK && !childnodename.equals(cyltreepathlist.get(0))  )
	    		continue;
	    	
	    	if(childnodename.equals(cyltreepathlist.get(0)  ) ) {
    			if(officallsltopt)
    				childNode.increaseZdgzOfficalCount();
	    		
    			childNode.increaseZdgzCandidateCount();
	    			
	    		DefaultTreeModel treemodel = (DefaultTreeModel) this.getModel();
	    		treemodel.nodeChanged(childNode);
	    		
	    		cyltreepathlist.remove(0);
	    		
	    		if(cyltreepathlist.size() == 0) {
	    			childNode.setOfficallySelected(officallsltopt);
	    			childNode.setSelectedtime(addedtime);
	    			
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

	public void removeZdgzBkCylInfoFromTreeNode(BkChanYeLianTreeNode childNode,boolean stillkeepincandidate) 
	{
		 TreeNode[] nodepath = childNode.getPath();
		 Boolean isofficallselected = childNode.isOfficallySelected();
		 
		 DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		 TreeNode[] tempath = model.getPathToRoot(childNode);
		 for(int j=1;j<tempath.length;j++ ) {
		    	BkChanYeLianTreeNode parentnode = (BkChanYeLianTreeNode)tempath[j];
		    	if(isofficallselected) 
		    		parentnode.decreaseZdgzOfficalCount();
		    	if(!stillkeepincandidate) //只是从offical移除，candidate还有就不用加
		    		parentnode.decreasedgzCandidateCount();
		    	
		    	model.nodeChanged(parentnode);
		 }
		
	}
	public void addZdgzBkCylInfoToTreeNode(BkChanYeLianTreeNode childNode, boolean hasbeenincandidate)  
	{
		 TreeNode[] nodepath = childNode.getPath();
		 Boolean isofficallselected = childNode.isOfficallySelected();
		 
//		 DefaultTreeModel treemodel = (DefaultTreeModel) this.getModel();
		 
		 DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		 TreeNode[] tempath = model.getPathToRoot(childNode);
		 for(int j=1;j<tempath.length;j++ ) {
		    	BkChanYeLianTreeNode parentnode = (BkChanYeLianTreeNode)tempath[j];
		    	if(isofficallselected) 
		    		parentnode.increaseZdgzOfficalCount();
		    	if(!hasbeenincandidate) //如果已经在候选里面，cand count就不用加
		    		parentnode.increaseZdgzCandidateCount();	
		    	
		    	model.nodeChanged(parentnode);
		 }
		
	}

	public TreePath locateNodeByNameOrHypyOrBkCode(String bkinputed) 
	{
		TreePath bkpath = null ;
    	BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.getModel().getRoot();
	    @SuppressWarnings("unchecked")
		Enumeration<BkChanYeLianTreeNode> e = treeroot.depthFirstEnumeration();
	    while (e.hasMoreElements() ) {
	    	BkChanYeLianTreeNode node = e.nextElement();
	    	String bkHypy ;
	    	try {
	    		 bkHypy = node.getHanYuPingYin().toLowerCase();
	    	} catch (java.lang.NullPointerException ex) {
	    		//System.out.println(node.getUserObject().toString() + "汉语拼音是 " + node.getHanYuPingYin());
	    		bkHypy = "";
	    	}
	    	String bkName = node.getUserObject().toString();
	    	String bkcode;
	    	try {
	    		bkcode = node.getTDXBanKuaiZhiShuCode().toString();
	    	} catch (java.lang.NullPointerException ex) {
	    		System.out.println(node.getUserObject().toString() + "板块指数是 " + node.getTDXBanKuaiZhiShuCode());
	    		bkcode = "";
	    	}
	    	
	    	boolean found = false;
	    	if(bkHypy.equalsIgnoreCase(bkinputed)) 
	    		found = true;
	    	else if(bkName.equalsIgnoreCase(bkinputed))
	    		found = true;
	    	else if(bkcode.equals(bkinputed))
	    		found = true;
	    	
	        if (found) {
	             bkpath = new TreePath(node.getPath());
	             this.setSelectionPath(bkpath);
	     	     this.scrollPathToVisible(bkpath);
	     	     this.expandTreePathAllNode(bkpath);

	     	     return bkpath;
	        }
	    }
		return null;
	}
	
	
}


