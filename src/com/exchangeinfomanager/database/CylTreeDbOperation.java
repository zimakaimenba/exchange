package com.exchangeinfomanager.database;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
//https://www.we-rc.com/blog/2015/07/19/nested-set-model-practical-examples-part-i#foot-1
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.joda.time.LocalDate;

import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLianGUI;
import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.CylTreeNestedSetNode;

import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.nodes.operations.BanKuaiAndStockTree;
import com.exchangeinfomanager.nodes.operations.InvisibleTreeModel;

import com.exchangeinfomanager.systemconfigration.SystemConfigration;

import com.mysql.jdbc.MysqlDataTruncation;
import com.sun.rowset.CachedRowSetImpl;

public class CylTreeDbOperation 
{
	private ConnectDataBase connectdb;
	private SystemConfigration sysconfig;
//	private AllCurrentTdxBKAndStoksTree allbksks;
	private BanKuaiAndStockTree treecyl;
	private CylTreeNestedSetNode alltopNode; 

	public CylTreeDbOperation ()
	{
		connectdb = ConnectDataBase.getInstance();
		sysconfig = SystemConfigration.getInstance();
//		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
		

		setupBanKuaiAndStockTree ();
//		restoreChanYeLianNestedDbFromXml ();
	}
	
//	private void updatedb ()
//	{
//		Map<Integer,String> map = new HashMap<> ();
//		String sql = "SELECT * FROM 产业链板块国列表 \r\n" + 
//				"JOIN tree_map\r\n" + 
//				"ON 产业链板块国列表.`板块国代码` = tree_map.nodeid"
//				;
//		 CachedRowSetImpl rspd = null;
//		 try{
//			 rspd = connectdb.sqlQueryStatExecute(sql);
//		    	
//		    	
//		        while(rspd.next())  {
//		        	Integer treeid = rspd.getInt("tree_id");
//		        	String bkname = rspd.getString("板块国名称");
//		        	String bkcode = rspd.getString("板块国代码");
//		        	
//		        	map.put(treeid,bkname);
//		        }
//			 
//		 } catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					if(rspd != null)
//					rspd.close();
//					rspd = null;
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		 
//
//		 	for (Map.Entry<Integer, String> entry : map.entrySet()) {
// 
//		 			Integer treeid = entry.getKey();
//		 			String bkname = entry.getValue();
//		 			String sql2 = "UPDATE tree_map SET nodeid='" + bkname + "'"
//		 	     			+ " WHERE tree_id=" + treeid
//		 	     			;
//		 	     	try {
//						connectdb.sqlUpdateStatExecute(sql2);
//					} catch (SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//		 		
// 
//		 	}
//	}
	
	public BanKuaiAndStockTree getBkChanYeLianTree ()
	{
		return treecyl;
	}

	public  void printTree(CylTreeNestedSetNode aNode)
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
	private void setupBanKuaiAndStockTree ()
	{
		 List<BkChanYeLianTreeNode> nodelist = new ArrayList<>();
		 CachedRowSetImpl rsagu = null;
		 try{
				   String query = "{call r_return_tree(?)}";
				    java.sql.CallableStatement stm = connectdb.getCurrentDataBaseConnect().prepareCall(query); 
				    stm.setString(1, null);
				    
				    ResultSet rs = stm.executeQuery();

				    while(rs.next()) {
				    	int nestedtree_id = rs.getInt("tree_id");
				    	String nodeid = rs.getString("nodeid");
				    	String nodename = rs.getString("name");
				    	Integer nodetype = rs.getInt("nodetype");
				    	int left = rs.getInt("lft");
				    	int right = rs.getInt("rgt");
				    	int parent_id = rs.getInt("parent_id");
				    	Date isolatedtime = rs.getDate("isolatedtime");
				    	
				    	CylTreeNestedSetNode tmpnode;
				    	if(nestedtree_id == 1) {
				    		tmpnode = new CylTreeNestedSetNode ("000000","两交易所",0);
				    		alltopNode = tmpnode;
				    		this.treecyl = new BanKuaiAndStockTree(alltopNode,"CYLTREE");
				    	} 	else 
				    		tmpnode = new CylTreeNestedSetNode (nodeid,nodename,nodetype);

				    	tmpnode.setNestedId(nestedtree_id);
				    	tmpnode.setNestedLeft(left);
				    	tmpnode.setNestedRight(right);
				    	tmpnode.setNestedParent(parent_id);
				    	if(isolatedtime != null)
				    		tmpnode.setNodeIsolatedDate(isolatedtime.toLocalDate());
				    	
				    	nodelist.add(tmpnode);
				    }
				    stm.close();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if(rsagu != null)
						rsagu.close();
						rsagu = null;
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			}
		 	
//		 try{
//				Collections.sort(nodelist, new FatherNodeIdComparator () );
//			} catch (java.lang.NullPointerException e) {
////				e.printStackTrace();
//		 }
		 
		 CylTreeNestedSetNode treeroot = (CylTreeNestedSetNode)treecyl.getModel().getRoot();
		 InvisibleTreeModel treemodel = (InvisibleTreeModel)treecyl.getModel();
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
		 
		 treecyl.setTransferHandler(new TreeTransferHandler() );
//		 treemodel.addTreeModelListener(new CustomTreeModelListener());
	}
	
	class CustomTreeModelListener implements TreeModelListener {
	    public void treeNodesChanged(TreeModelEvent e) {
	        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)e.getTreePath().getLastPathComponent();
	        int index = e.getChildIndices()[0];
	        DefaultMutableTreeNode modifiedNode = (DefaultMutableTreeNode)(parentNode.getChildAt(index));

	        System.out.println("A node on parent: " + parentNode.getUserObject()
	                + " was modified to: " + modifiedNode.getUserObject());
	    }

	    public void treeNodesInserted(TreeModelEvent e) {
	        String parentNode = e.getTreePath().getLastPathComponent().toString();
	        String childNodes = Arrays.toString(e.getChildren());
	        System.out.println("Node(s): " + childNodes + " inserted to parent: " + parentNode);
	    }

	    public void treeNodesRemoved(TreeModelEvent e) {
	        String parentNode = e.getTreePath().getLastPathComponent().toString();
	        String childNodes = Arrays.toString(e.getChildren());
	        System.out.println("Node(s): " + childNodes + " removed from parent: " + parentNode);
	    }

	    public void treeStructureChanged(TreeModelEvent e) {
	        System.out.println("Tree structure has been updated");
	    }
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
	        
	        moveNodeInNestedDatabase ( (CylTreeNestedSetNode)movingancesternode, (CylTreeNestedSetNode)parent);
	        
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
	
	/*
	 * 股票池对应的子股票池/板块国
	 */
	public List<BkChanYeLianTreeNode> getSubGuPiaoChi() 
	{
        CachedRowSetImpl rspd = null; 
        
        List<BkChanYeLianTreeNode> subpgcset = new ArrayList<BkChanYeLianTreeNode> ();
		try {
				String sqlquerystat = "SELECT * FROM 产业链板块国列表" 
										;

		    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	
		    	
		        while(rspd.next())  {
		        	int bkzid = rspd.getInt("id");
		        	String bkzname = rspd.getString("板块国名称");
		        	CylTreeNestedSetNode subgpc = new CylTreeNestedSetNode (String.valueOf(bkzid),bkzname,BkChanYeLianTreeNode.SUBGPC); 
		        	subpgcset.add(subgpc);
		        }
		       
		} catch(java.lang.NullPointerException e){ 
		    	e.printStackTrace();
		} catch (SQLException e) {
		    	e.printStackTrace();
		} catch(Exception e){
		    	e.printStackTrace();
		} finally {
		    	if(rspd != null)
					try {
						rspd.close();
						rspd = null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
		}

		return subpgcset;
	}
	/*
	 * 
	 */
	public BkChanYeLianTreeNode addNewSubGuoPiaoChi( String subgpcname) 
	{
		String sqlinsertstat = "INSERT INTO 产业链板块国列表(板块国名称) VALUES ("
								+ "'" + subgpcname.trim() + "'" 
								+ ")"
								;
		int autoIncKeyFromApi = 0;
		try {
			autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		} catch (MysqlDataTruncation e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BkChanYeLianTreeNode subnode = new CylTreeNestedSetNode (String.valueOf(autoIncKeyFromApi),subgpcname,BkChanYeLianTreeNode.SUBGPC);
		return subnode;
	}
	/*
	 * 
	 */
	public BkChanYeLianTreeNode  addNewGuoPiaoChi(String gpccode, String gpcname) 
	{
		String sqlinsertstat = "INSERT INTO 产业链板块州列表(板块州代码,板块州名称) VALUES ("
				+ "'" + gpccode.trim() + "'" + ","
				+ "'" + gpcname.trim() + "'" 
				+ ")"
				;
		int autoIncKeyFromApi;
		try {
		autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		} catch (MysqlDataTruncation e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		BkChanYeLianTreeNode gpc = new CylTreeNestedSetNode (gpccode,gpcname,BkChanYeLianTreeNode.GPC);
		
		CylTreeNestedSetNode treeroot = (CylTreeNestedSetNode)treecyl.getModel().getRoot();
		this.addNodeToNestedDatabase(treeroot, (CylTreeNestedSetNode)gpc);
		
		InvisibleTreeModel treemodel = (InvisibleTreeModel)treecyl.getModel();
		treemodel.insertNodeInto(gpc, treeroot, treeroot.getChildCount());
		
		return gpc;
	}
	/*
	 * 股票池/板块州
	 */
	public Set<BkChanYeLianTreeNode> getGuPiaoChi() 
	{
		Set<BkChanYeLianTreeNode> gpcindb = new HashSet<BkChanYeLianTreeNode> ();
		boolean hasbkcode = true;
        CachedRowSetImpl rspd = null; 
        
		try {
			   String sqlquerystat = "SELECT * FROM 产业链板块州列表 "
						;

		    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
		        while(rspd.next())  {
		        	String bkzcode = rspd.getString("板块州代码");
		        	String bkzname = rspd.getString("板块州名称");
		        	BkChanYeLianTreeNode gpc = new CylTreeNestedSetNode (bkzcode,bkzname,BkChanYeLianTreeNode.GPC);
		        	gpcindb.add(gpc);
		        }
		       
		} catch(java.lang.NullPointerException e){ 
		    	e.printStackTrace();
		} catch (SQLException e) {
		    	e.printStackTrace();
		} catch(Exception e){
		    	e.printStackTrace();
		} finally {
		    	if(rspd != null)
					try {
						rspd.close();
						rspd = null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
		}

		return gpcindb;
	}
	/*
	 * 
	 */
	private void addNodeToNestedDatabase (CylTreeNestedSetNode topNode,CylTreeNestedSetNode childrennode)
	{
			   int treeid = topNode.getNestedId ();
			   
			   CachedRowSetImpl rsagu = null;
				try{
					   String query = "{CALL r_tree_traversal(?,?,?)}";
					    java.sql.CallableStatement stm = connectdb.getCurrentDataBaseConnect().prepareCall(query); 
					    stm.setString(1, "insert");
					    stm.setString(2, null);
					    stm.setInt(3, treeid);
					    ResultSet rs = stm.executeQuery();
//					    stm.registerOutParameter(1, Types.INTEGER);
//					    stm.execute();
//					    Integer m_count = stm.getInt(1);
					    
					    Integer lstid = null;
					    while(rs.next()) {
					    	lstid = rs.getInt("LAST_INSERT_ID()");
					    }
					    stm.close();
					    
					    childrennode.setNestedId(lstid);
					    String sqlquerystat = "UPDATE tree_map SET " 
					    						+ " nodeid = '" +  childrennode.getMyOwnCode() +  "', "
					    						+ " nodetype = " +  childrennode.getType() 
					    						+ " where tree_id=" + lstid
					    						;
					    connectdb.sqlInsertStatExecute(sqlquerystat);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							if(rsagu != null)
							rsagu.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						rsagu = null;
					}
	}
	private void moveNodeInNestedDatabase (CylTreeNestedSetNode movedNode,CylTreeNestedSetNode parentsnode)
	{
			   int movednodetreeid = movedNode.getNestedId ();
			   int parentnodetreeid = parentsnode.getNestedId();
			   
			   CachedRowSetImpl rsagu = null;
				try{
					   String query = "{CALL r_tree_move(?,?)}";
					    java.sql.CallableStatement stm = connectdb.getCurrentDataBaseConnect().prepareCall(query); 
					    stm.setInt(1, movednodetreeid);
					    stm.setInt(2, parentnodetreeid);
					    stm.executeQuery();
					    stm.close();
					    
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							if(rsagu != null)
							rsagu.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						rsagu = null;
					}
	}
	/*
	 * 
	 */
	public void addNewNodeToTree (BkChanYeLianTreeNode newNode, int direction)
	{
		BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) treecyl.getSelectionPath().getLastPathComponent();
		if(parent.getType() == BkChanYeLianTreeNode.GPC  ) {
			if (direction == BanKuaiAndChanYeLianGUI.DOWN || direction == BanKuaiAndChanYeLianGUI.UP ) 
				direction = BanKuaiAndChanYeLianGUI.RIGHT;
		}
		
		boolean enableoperation = treecyl.checkNodeDuplicate (parent,newNode);
    	if( enableoperation ) {
        		JOptionPane.showMessageDialog(null,"同级中已经存在相同名称子版块或与父节点重名，不能重复添加!");
        		return;
        }
    	
        if (direction == BanKuaiAndChanYeLianGUI.RIGHT) {
        	parent.add(newNode);
        } 
        
        if (direction != BanKuaiAndChanYeLianGUI.RIGHT) {
        	BkChanYeLianTreeNode currentNode = (BkChanYeLianTreeNode) treecyl.getSelectionPath().getLastPathComponent();
        	parent = (BkChanYeLianTreeNode) currentNode.getParent();

        	enableoperation = treecyl.checkNodeDuplicate (parent,newNode);
        	if( enableoperation ) {
            		JOptionPane.showMessageDialog(null,"同级中已经存在相同名称子版块或与父节点重名，不能重复添加!");
            		return;
            }
            
            int childIndex = parent.getIndex(currentNode);
            if (direction == BanKuaiAndChanYeLianGUI.DOWN) 
            	parent.insert(newNode, childIndex+1);
            else if (direction == BanKuaiAndChanYeLianGUI.UP) 
            	parent.insert(newNode, childIndex);
        }
        
        this.addNodeToNestedDatabase ((CylTreeNestedSetNode)parent,(CylTreeNestedSetNode)newNode);
        

       InvisibleTreeModel treeModel = (InvisibleTreeModel)this.treecyl.getModel();
       BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treeModel.getRoot();
       treeModel.nodesWereInserted(newNode.getParent(), new int[] {newNode.getParent().getIndex(newNode)});
       treeModel.reload(treeroot);
       treecyl.setSelectionPath(new TreePath(((BkChanYeLianTreeNode)newNode.getParent()).getPath()));
       treecyl.scrollPathToVisible( new TreePath(newNode.getPath()));
	}
	/*
	 * 
	 */
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
		
		treecyl.isolatedNodes (   new TreePath (delnode.getPath() ) );  //在树上标记
	}
	private  void isolatedNodeInDb(CylTreeNestedSetNode aNode)
	{
    	int nestedtreeid = ((CylTreeNestedSetNode)aNode).getNestedId();
    	CachedRowSetImpl rsagu = null;
		 try{
					   String query = "UPDATE tree_map SET isolatedtime = " + "'" + LocalDate.now().toString() + "'"
							   		+ " WHERE tree_id = " + nestedtreeid 
							   ;
					    int stm = connectdb.sqlUpdateStatExecute(query); 
		} catch (Exception e) {
						e.printStackTrace();
		} finally {
						try {
							if(rsagu != null)
								rsagu.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						rsagu = null;
		} 

	    for(int i = 0 ; i < aNode.getChildCount() ; i++)
	    {
	    	isolatedNodeInDb((CylTreeNestedSetNode)aNode.getChildAt(i));
	    }

	}
	private void deleteNodes (CylTreeNestedSetNode delnode)
	{
		//在数据中删除
		deleteNodeInDb (delnode);
		
		treecyl.deleteNodes (   new TreePath (delnode.getPath() ) );  //在树上删除
	}
	private  void deleteNodeInDb (CylTreeNestedSetNode aNode)
	{
	    	Integer nestedtreeid = ((CylTreeNestedSetNode)aNode).getNestedId();
	    	
	    	 CachedRowSetImpl rsagu = null;
			 try{
				 String query = "{CALL r_return_treeleaves(?)}";
				 java.sql.CallableStatement stm = connectdb.getCurrentDataBaseConnect().prepareCall(query); 
				 stm.setInt(1, nestedtreeid);

				 ResultSet rs = stm.executeQuery();
				 while (rs.next()) {
					 int deltreeid = rs.getInt("tree_id");
					 query = "{CALL r_tree_traversal(?, ?, ?)}"; //CALL r_tree_traversal('delete', 4, NULL);
					 java.sql.CallableStatement stmdel = connectdb.getCurrentDataBaseConnect().prepareCall(query); 
					 stmdel.setString(1, "delete");
					 stmdel.setInt(2, deltreeid);
					 stmdel.setString(3, null);
					 
					 stmdel.executeQuery();
					 
					 if(deltreeid == nestedtreeid ) {
						 return;
					 }
					 
				 }
				 stm.close();
			} catch (Exception e) {
						e.printStackTrace();
			} finally {
				try {
					if(rsagu != null)
						rsagu.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
					rsagu = null;
			}
			 
			deleteNodeInDb (aNode);
	}

	public BkChanYeLianTreeNode getChanYeLianInfo(BkChanYeLianTreeNode node) 
	{
		List<BkChanYeLianTreeNode> result = this.getChanYeLianInfo(node.getMyOwnCode(), node.getType());
		node.getNodeJiBenMian().setChanYeLianInfo(result);
		
		return node;
	}
	public  List<BkChanYeLianTreeNode> getChanYeLianInfo (String nodecode, int nodetype)
	{
		String query = "SELECT * FROM  tree_map "
				+ " WHERE nodeid ='" + nodecode + "'"
				+ " AND nodetype = " + nodetype
				;
		
		List<BkChanYeLianTreeNode> cylinfo = new ArrayList<> ();
		CachedRowSetImpl rs = null;
		try{
				rs = this.connectdb.sqlQueryStatExecute(query);
				while(rs.next()) {
					int tree_id = rs.getInt("tree_id");
					
					query = "{CALL r_return_path(?)}";
					java.sql.CallableStatement stmdel = connectdb.getCurrentDataBaseConnect().prepareCall(query); 
					stmdel.setInt(1, tree_id);
					 
					ResultSet rsstm = stmdel.executeQuery();
					while(rsstm.next()){
						int treeid = rsstm.getInt("tree_id");
						String nodeid = rsstm.getString("nodeid") ;
						int cylnodetype = rsstm.getInt("nodetype") ;
						BkChanYeLianTreeNode node = treecyl.getSpecificNodeByHypyOrCode(nodeid, cylnodetype);
						
						cylinfo.add( node );
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
				try {
					if(rs != null)
						rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return cylinfo;

	}
	public List<BkChanYeLianTreeNode>  getSlidingInChanYeLianInfo (String nodecode, int nodetype)
	{
		String sqlquery = "SELECT * FROM  tree_map "
				+ " WHERE nodeid ='" + nodecode + "'"
				+ " AND nodetype = " + nodetype
				;
		
		List<BkChanYeLianTreeNode> cylinfo = new ArrayList<> ();
		CachedRowSetImpl rs = null;
		CachedRowSetImpl rsnext = null;
		try{
				rs = this.connectdb.sqlQueryStatExecute(sqlquery);
				while(rs.next()) {
					String nodeid = rs.getString("nodeid");
					
					sqlquery = "SELECT * FROM \r\n" + 
							"tree_map ,\r\n" + 
							"\r\n" + 
							"(SELECT tree_map.parent_id, tree_map.nodeid  FROM tree_map \r\n" + 
							"WHERE nodeid = '" + nodecode+ "' AND tree_map.nodetype = " + nodetype + "  \r\n" + 
							") A \r\n" + 
							"where  tree_map.parent_id = A.parent_id \r\n" + 
							"GROUP BY tree_map.tree_id"
							;
					rsnext = this.connectdb.sqlQueryStatExecute(sqlquery);
					while(rsnext.next()){
						String sliblingnodeid = rsnext.getString("nodeid") ;
						int cylnodetype = rsnext.getInt("nodetype") ;
						BkChanYeLianTreeNode node = treecyl.getSpecificNodeByHypyOrCode(sliblingnodeid, cylnodetype);
						
						cylinfo.add(node); 
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
				try {
					if(rs != null)
						rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return cylinfo;
	}
	public List<BkChanYeLianTreeNode> getChildrenInChanYeLianInfo(String nodecode, int nodetype) 
	{
		String query = "SELECT * FROM  tree_map "
				+ " WHERE nodeid ='" + nodecode + "'"
				+ " AND nodetype = " + nodetype
				;
		
		List<BkChanYeLianTreeNode> cylinfo = new ArrayList<> ();
		CachedRowSetImpl rs = null;
		try{
				rs = this.connectdb.sqlQueryStatExecute(query);
				while(rs.next()) {
					int tree_id = rs.getInt("tree_id");
					
					query = "{CALL r_return_subtree(?)}";
					java.sql.CallableStatement stmdel = connectdb.getCurrentDataBaseConnect().prepareCall(query); 
					stmdel.setInt(1, tree_id);
					 
					ResultSet rsstm = stmdel.executeQuery();
					while(rsstm.next()){
						int treeid = rsstm.getInt("tree_id");
						String nodeid = rsstm.getString("nodeid") ;
						int cylnodetype = rsstm.getInt("nodetype") ;
						BkChanYeLianTreeNode node = treecyl.getSpecificNodeByHypyOrCode(nodeid, cylnodetype);
						
						cylinfo.add( node );
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
				try {
					if(rs != null)
						rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return cylinfo;
	}
	


	public Boolean isBanKuaiInBearPart(String bkcode) 
	{
		return null;
	}
	public void deleNodeFromDatabase(BkChanYeLianTreeNode node) 
	{
		if(node.getType() == BkChanYeLianTreeNode.SUBGPC) {
			String sqldeletestat = "DELETE  FROM 产业链板块国列表"
					+ " WHERE 产业链板块国列表.`板块国名称` = " + "'" + node.getMyOwnName() + "'" 
					;

			try {
				@SuppressWarnings("unused")
				int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletestat);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/*
	 * 
	 */
	public void restoreChanYeLianNestedDbFromXml ()
	{
		FileInputStream xmlfileinput = null;
		try {
			xmlfileinput = new FileInputStream(sysconfig.getBanKuaiChanYeLianXml ());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Element cylxmlroot = null;
		try {
			SAXReader reader = new SAXReader();
			Document cylxmldoc = reader.read(xmlfileinput);
			cylxmlroot = cylxmldoc.getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		CylTreeNestedSetNode alltopNode = new CylTreeNestedSetNode("000000","两交易所",0);
		alltopNode.setNestedId(1);
		String sqlquerystat = "INSERT INTO tree_map (tree_id, lft,rgt,parent_id) VALUES (" 
				+ 1 + ","
				+ 1 + ","
				+ 2 + ","
				+ 0
				+ ")"
				;
		try {
			connectdb.sqlInsertStatExecute(sqlquerystat);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//产业链tree part
		generateChanYeLianTreeFromXML(alltopNode,cylxmlroot); //生成产业链树
		
	}
	private void generateChanYeLianTreeFromXML(CylTreeNestedSetNode topNode, Element xmlelement) 
	{
	    	 Iterator it = xmlelement.elementIterator();
	    	 
	    	 while (it.hasNext() ) 
	    	 {
				   Element element = (Element) it.next();
			   
				   CylTreeNestedSetNode parentsleaf = null;
				   
				   String bkname = null, bkowncode = null; 
				   String nodetypestr = element.attributeValue("Type").trim();
				   Integer nodetype = Integer.parseInt(nodetypestr ); 
				   
				   if(BkChanYeLianTreeNode.GPC == nodetype ) { //是股票池
					   bkowncode = element.attributeValue("gpccode"); 
					   bkname = element.attributeValue("gpcname");
					   
					   parentsleaf = new CylTreeNestedSetNode(bkowncode,bkname,BkChanYeLianTreeNode.GPC);
					   
				   } else
				   if(BkChanYeLianTreeNode.SUBGPC == nodetype  ) { //是股票池
					   bkowncode = element.attributeValue("subgpccode"); 
					   bkname = element.attributeValue("subgpcname");
					   
					   parentsleaf = new CylTreeNestedSetNode(bkowncode,bkname,BkChanYeLianTreeNode.SUBGPC);
					   
					   String sqlquerystat = "INSERT INTO 产业链板块国列表 (板块国名称)  VALUES (" 
								+"'" + bkname + "'"
								+ ")"
								;
						try {
							connectdb.sqlInsertStatExecute(sqlquerystat);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					   
				   } else
					if(5 == nodetype ) { //是通达信板块  
							bkowncode = element.attributeValue("subbkcode"); 
						   bkname = element.attributeValue("subbkname");
						   
						   parentsleaf = new CylTreeNestedSetNode(bkowncode,bkname,BkChanYeLianTreeNode.SUBGPC);
						   
						   String sqlquerystat = "INSERT INTO 产业链板块国列表 ( 板块国名称)  VALUES (" 
									+"'" + bkname + "'"
									+ ")"
									;
							try {
								connectdb.sqlInsertStatExecute(sqlquerystat);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
					} else
				   if(BkChanYeLianTreeNode.TDXBK == nodetype ) { //是通达信板块  
					   bkowncode = element.attributeValue("bkcode");
					   bkname = element.attributeValue("bkname");
					 
					   parentsleaf = new CylTreeNestedSetNode(bkowncode,bkname,BkChanYeLianTreeNode.TDXBK);
				   } else
				   if(BkChanYeLianTreeNode.TDXGG == nodetype || BkChanYeLianTreeNode.BKGEGU == nodetype) {//是个股
					   bkname = element.attributeValue("geguname");
					   bkowncode = element.attributeValue("gegucode");
					   
					   parentsleaf = new CylTreeNestedSetNode(bkowncode,bkname,BkChanYeLianTreeNode.TDXGG );
				   }
				   
				   
				   if(parentsleaf != null) {
					   addNodeToNestedDatabase (topNode,parentsleaf);
					   generateChanYeLianTreeFromXML(parentsleaf,element);
				   }  else
						return;
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