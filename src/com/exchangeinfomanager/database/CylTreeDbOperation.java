package com.exchangeinfomanager.database;
//https://www.we-rc.com/blog/2015/07/19/nested-set-model-practical-examples-part-i#foot-1
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.joda.time.LocalDate;

import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLianGUI;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.CylTreeNestedSetNode;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.nodes.operations.BanKuaiAndStockTree;
import com.exchangeinfomanager.nodes.operations.InvisibleTreeModel;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mysql.jdbc.MysqlDataTruncation;
import com.sun.rowset.CachedRowSetImpl;

public class CylTreeDbOperation 
{
	private ConnectDataBase connectdb;
	private SystemConfigration sysconfig;
	private AllCurrentTdxBKAndStoksTree allbksks;
	private BanKuaiAndStockTree treecyl;
	private CylTreeNestedSetNode alltopNode; 

	public CylTreeDbOperation ()
	{
		connectdb = ConnectDataBase.getInstance();
		sysconfig = SystemConfigration.getInstance();
		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
		
		setupBanKuaiAndStockTree ();
	}
	
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
				    	} 	else 
				    		tmpnode = new CylTreeNestedSetNode (nodeid,nodename,nodetype);

				    	tmpnode.setNestedId(nestedtree_id);
				    	tmpnode.setNestedLeft(left);
				    	tmpnode.setNestedRight(right);
				    	tmpnode.setNestedParent(parent_id);
				    	if(isolatedtime != null)
				    		tmpnode.setNodeIsolatedDate(isolatedtime.toLocalDate());
				    	
				    	buildNodeToTree (tmpnode);
				    }
				    
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

	private void buildNodeToTree(CylTreeNestedSetNode tmpnode)
	{
		if(tmpnode.getNestedId() == 1) {
			this.treecyl = new BanKuaiAndStockTree(alltopNode,"CYLTREE");
			return;
		}
		
		int parent_id = tmpnode.getNestedParent();
		
		CylTreeNestedSetNode treeroot = (CylTreeNestedSetNode)treecyl.getModel().getRoot();
		InvisibleTreeModel treemodel = (InvisibleTreeModel)treecyl.getModel();
	    
		Enumeration<TreeNode> e = treeroot.breadthFirstEnumeration();
	    while (e.hasMoreElements() ) {
	    	CylTreeNestedSetNode node = (CylTreeNestedSetNode) e.nextElement();
	    	int nodeid = node.getNestedid();
	        if (parent_id == nodeid) {
	             treemodel.insertNodeInto(tmpnode, node, node.getChildCount());
	             break;
	         }
	    }

	    return ;
	}
	/*
	 * 股票池对应的子股票池/板块国
	 */
	public List<BkChanYeLianTreeNode> getSubGuPiaoChi() 
	{
        CachedRowSetImpl rspd = null; 
        
        List<BkChanYeLianTreeNode> subpgcset = new ArrayList<BkChanYeLianTreeNode> ();
		try {
//			   String sqlquerystat = "SELECT 产业链板块国列表.`板块国代码`,产业链板块国列表.`板块国名称`" + 
//									" FROM 产业链板块国列表" +
//									" INNER JOIN 产业链板块州板块国对应表" +
//									" ON 产业链板块国列表.`板块国代码` = 产业链板块州板块国对应表.`板块国代码`" +
//									" WHERE 产业链板块州板块国对应表.`板块州代码`  = '" + gpccode + "' " +
//									" ORDER BY 产业链板块国列表.`板块国代码`" 
//									;
				String sqlquerystat = "SELECT 产业链板块国列表.`板块国代码`,产业链板块国列表.`板块国名称`" + 
					" FROM 产业链板块国列表" 
					;

		    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	
		    	
		        while(rspd.next())  {
		        	String bkzcode = rspd.getString("板块国代码");
		        	String bkzname = rspd.getString("板块国名称");
		        	CylTreeNestedSetNode subgpc = new CylTreeNestedSetNode (bkzcode,bkzname,BkChanYeLianTreeNode.SUBGPC); 
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
	public BkChanYeLianTreeNode addNewSubGuoPiaoChi(String subgpccode, String subgpcname) 
	{
		String sqlinsertstat = "INSERT INTO 产业链板块国列表(板块国代码,板块国名称) VALUES ("
								+ "'" + subgpccode.trim() + "'" + ","
								+ "'" + subgpcname.trim() + "'" 
								+ ")"
								;
		int autoIncKeyFromApi;
		try {
			autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		} catch (MysqlDataTruncation e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BkChanYeLianTreeNode subnode = new CylTreeNestedSetNode (subgpccode,subgpcname,BkChanYeLianTreeNode.SUBGPC);
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
	private void addNodeToNestedDatabase (CylTreeNestedSetNode topNode,CylTreeNestedSetNode parentsleaf)
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
					    
					    parentsleaf.setNestedId(lstid);
					    String sqlquerystat = "INSERT INTO tree_content (tree_id, nodeid,nodetype) VALUES (" 
					    						+ lstid + ","
					    						+ "'" + parentsleaf.getMyOwnCode() + "',"
					    						+ parentsleaf.getType() 
					    						+ ")"
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
	public void deleteNodeFromTree(BkChanYeLianTreeNode delnode, boolean deleteforever) 
	{
		if(! deleteforever) {
			isolatedNodes (delnode);
		} else
			deleteNodes (delnode);
	}
	private void isolatedNodes (BkChanYeLianTreeNode delnode)
	{
		//在数据库中标记
		isolatedNodeInDb (delnode);
		
		treecyl.isolatedNodes (   new TreePath (delnode.getPath() ) );  //在树上标记
	}
	private  void isolatedNodeInDb(BkChanYeLianTreeNode aNode)
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
	private void deleteNodes (BkChanYeLianTreeNode delnode)
	{
		//在数据中删除
		deleteNodeInDb (delnode);
		
		treecyl.deleteNodes (   new TreePath (delnode.getPath() ) );  //在树上删除
	}
	private  void deleteNodeInDb(BkChanYeLianTreeNode aNode)
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
		List<String[]> result = this.getChanYeLianInfo(node.getMyOwnCode(), node.getType());
		node.getNodeJiBenMian().setChanYeLianInfo(result);
		
		return node;
	}
	public  List<String[]> getChanYeLianInfo (String nodecode, int nodetype)
	{
		String query = "SELECT * FROM  tree_content "
				+ " WHERE nodeid ='" + nodecode + "'"
				+ " AND nodetype = " + nodetype
				;
		
		List<String[]> cylinfo = new ArrayList<> ();
		CachedRowSetImpl rs = null;
		try{
				rs = this.connectdb.sqlQueryStatExecute(query);
				while(rs.next()) {
					int tree_id = rs.getInt("tree_id");
					
					query = "{CALL r_return_path(?)}";
					java.sql.CallableStatement stmdel = connectdb.getCurrentDataBaseConnect().prepareCall(query); 
					stmdel.setInt(1, tree_id);
					 
					ResultSet rsstm = stmdel.executeQuery();
					int rowcount = 0;
					if (rsstm.last()) {
					  rowcount = rsstm.getRow();
					  rsstm.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
					}
					String[] treepath = new String[rowcount];
					int tmpc =0;
					while(rsstm.next()){
						int treeid = rsstm.getInt("tree_id");
						if(treeid != 1) {
							treepath[tmpc] =  rsstm.getString("name") + "[" + rsstm.getString("nodeid")  +"]";
							tmpc ++;
						} else
							tmpc ++;
					}
					
					cylinfo.add(treepath);
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
					+ " WHERE 产业链板块国列表.`板块国代码` = " + "'" + node.getMyOwnCode() + "'"
					+ " AND 产业链板块国列表.`板块国名称` = " + "'" + node.getMyOwnName() + "'" 
					;

			try {
				@SuppressWarnings("unused")
				int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletestat);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
				
	}	

}
