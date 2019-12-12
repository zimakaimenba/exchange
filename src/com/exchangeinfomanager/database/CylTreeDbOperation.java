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

import com.exchangeinfomanager.Tag.InsertedTag;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.Trees.InvisibleTreeModel;
import com.exchangeinfomanager.Trees.TreeOfChanYeLian;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLianGUI;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.CylTreeNestedSetNode;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

import com.mysql.jdbc.MysqlDataTruncation;
import com.sun.rowset.CachedRowSetImpl;

public class CylTreeDbOperation 
{
	private ConnectDataBase connectdb;
//	private SystemConfigration sysconfig;
	private TreeOfChanYeLian treecyl;
//	private CylTreeNestedSetNode alltopNode; 

	public CylTreeDbOperation (TreeOfChanYeLian treecyl)
	{
		connectdb = ConnectDataBase.getInstance();
		
		this.treecyl = treecyl;
	}
//	public CylTreeDbOperation ()
//	{
//		connectdb = ConnectDataBase.getInstance();
//	}
	public List<BkChanYeLianTreeNode> createTreeOfChanYeLian ()
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
				    	
				    	if(nestedtree_id == 1)
				    		continue;
				    	
				    	CylTreeNestedSetNode tmpnode;
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
		 
		 return nodelist;
	}
	/**
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
	public void addNodeToNestedDatabase (CylTreeNestedSetNode topNode,CylTreeNestedSetNode childrennode)
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
				
		createTagsOnCurrentTreePathForChanYeLianNode (childrennode);
	}
	public void moveNodeInNestedDatabase (CylTreeNestedSetNode movedNode,CylTreeNestedSetNode parentsnode)
	{
		int movednodetreeid = movedNode.getNestedId ();
		TreeNode[] path = movedNode.getPath();
		int parentnodetreeid = parentsnode.getNestedId();
		
//		this.deleteTagsToChanYeLianNode (movedNode);
		
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
		
//		this.setTagsToChanYeLianNode (movedNode);
	}
	/*
	 * 
	 */
	public void createTagsOnCurrentTreePathForChanYeLianNode (CylTreeNestedSetNode node)
	{
		BanKuaiAndStockTree treebkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		BkChanYeLianTreeNode treenode = treebkstk.getSpecificNodeByHypyOrCode(node.getMyOwnCode(), node.getType());
		if(treenode == null)
			return;
		
		TagsServiceForNodes nodetagservice = new TagsServiceForNodes (treenode);
		
		TreeNode[] path = node.getPath();
    	for(int i=1;i<path.length-1;i++) {
    		CylTreeNestedSetNode tmpnode = (CylTreeNestedSetNode)path[i];
    		if(tmpnode.getType() == BkChanYeLianTreeNode.SUBGPC) {
    			Tag nodetag = new Tag (tmpnode.getMyOwnName());
    			try {
					nodetagservice.createTag(nodetag);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
	}
	public void deleteTagsOnCurrentTreePathForChanYeLianNode (CylTreeNestedSetNode node)
	{
		BanKuaiAndStockTree treebkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		BkChanYeLianTreeNode treenode = treebkstk.getSpecificNodeByHypyOrCode(node.getMyOwnCode(), node.getType());
		if(treenode == null)
			return;
		
		TagsServiceForNodes nodetagservice = new TagsServiceForNodes (treenode);
		
		TreeNode[] path = node.getPath();
    	for(int i=1;i<path.length-1;i++) {
    		CylTreeNestedSetNode tmpnode = (CylTreeNestedSetNode)path[i];
    		if(tmpnode.getType() == BkChanYeLianTreeNode.SUBGPC) {
    			Tag nodetag = new Tag (tmpnode.getMyOwnName());
    			try {
					nodetagservice.deleteTag(nodetag);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
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
	public  void isolatedNodeInDb(CylTreeNestedSetNode aNode)
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
	public  void deleteNodeInDb (CylTreeNestedSetNode aNode)
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
//	public void restoreChanYeLianNestedDbFromXml ()
//	{
//		FileInputStream xmlfileinput = null;
//		try {
//			xmlfileinput = new FileInputStream(sysconfig.getBanKuaiChanYeLianXml ());
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		
//		Element cylxmlroot = null;
//		try {
//			SAXReader reader = new SAXReader();
//			Document cylxmldoc = reader.read(xmlfileinput);
//			cylxmlroot = cylxmldoc.getRootElement();
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//		
//		CylTreeNestedSetNode alltopNode = new CylTreeNestedSetNode("000000","两交易所",0);
//		alltopNode.setNestedId(1);
//		String sqlquerystat = "INSERT INTO tree_map (tree_id, lft,rgt,parent_id) VALUES (" 
//				+ 1 + ","
//				+ 1 + ","
//				+ 2 + ","
//				+ 0
//				+ ")"
//				;
//		try {
//			connectdb.sqlInsertStatExecute(sqlquerystat);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		//产业链tree part
//		generateChanYeLianTreeFromXML(alltopNode,cylxmlroot); //生成产业链树
//		
//	}
//	private void generateChanYeLianTreeFromXML(CylTreeNestedSetNode topNode, Element xmlelement) 
//	{
//	    	 Iterator it = xmlelement.elementIterator();
//	    	 
//	    	 while (it.hasNext() ) 
//	    	 {
//				   Element element = (Element) it.next();
//			   
//				   CylTreeNestedSetNode parentsleaf = null;
//				   
//				   String bkname = null, bkowncode = null; 
//				   String nodetypestr = element.attributeValue("Type").trim();
//				   Integer nodetype = Integer.parseInt(nodetypestr ); 
//				   
//				   if(BkChanYeLianTreeNode.GPC == nodetype ) { //是股票池
//					   bkowncode = element.attributeValue("gpccode"); 
//					   bkname = element.attributeValue("gpcname");
//					   
//					   parentsleaf = new CylTreeNestedSetNode(bkowncode,bkname,BkChanYeLianTreeNode.GPC);
//					   
//				   } else
//				   if(BkChanYeLianTreeNode.SUBGPC == nodetype  ) { //是股票池
//					   bkowncode = element.attributeValue("subgpccode"); 
//					   bkname = element.attributeValue("subgpcname");
//					   
//					   parentsleaf = new CylTreeNestedSetNode(bkowncode,bkname,BkChanYeLianTreeNode.SUBGPC);
//					   
//					   String sqlquerystat = "INSERT INTO 产业链板块国列表 (板块国名称)  VALUES (" 
//								+"'" + bkname + "'"
//								+ ")"
//								;
//						try {
//							connectdb.sqlInsertStatExecute(sqlquerystat);
//						} catch (SQLException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} 
//					   
//				   } else
//					if(5 == nodetype ) { //是通达信板块  
//							bkowncode = element.attributeValue("subbkcode"); 
//						   bkname = element.attributeValue("subbkname");
//						   
//						   parentsleaf = new CylTreeNestedSetNode(bkowncode,bkname,BkChanYeLianTreeNode.SUBGPC);
//						   
//						   String sqlquerystat = "INSERT INTO 产业链板块国列表 ( 板块国名称)  VALUES (" 
//									+"'" + bkname + "'"
//									+ ")"
//									;
//							try {
//								connectdb.sqlInsertStatExecute(sqlquerystat);
//							} catch (SQLException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} 
//					} else
//				   if(BkChanYeLianTreeNode.TDXBK == nodetype ) { //是通达信板块  
//					   bkowncode = element.attributeValue("bkcode");
//					   bkname = element.attributeValue("bkname");
//					 
//					   parentsleaf = new CylTreeNestedSetNode(bkowncode,bkname,BkChanYeLianTreeNode.TDXBK);
//				   } else
//				   if(BkChanYeLianTreeNode.TDXGG == nodetype || BkChanYeLianTreeNode.BKGEGU == nodetype) {//是个股
//					   bkname = element.attributeValue("geguname");
//					   bkowncode = element.attributeValue("gegucode");
//					   
//					   parentsleaf = new CylTreeNestedSetNode(bkowncode,bkname,BkChanYeLianTreeNode.TDXGG );
//				   }
//				   
//				   
//				   if(parentsleaf != null) {
//					   addNodeToNestedDatabase (topNode,parentsleaf);
//					   generateChanYeLianTreeFromXML(parentsleaf,element);
//				   }  else
//						return;
//			}
//	}
//
//	

}


