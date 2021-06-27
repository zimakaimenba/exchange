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
	public CylTreeDbOperation ()
	{
		connectdb = ConnectDataBase.getInstance();
	}
//	public List<BkChanYeLianTreeNode> createTreeOfNodeTree ()
//	{
//		String treetablename = getTreeTableNamyByTreeId ();
//		String sqlquerystat = "    SELECT n.tree_id, n.lft, n.rgt, n.parent_id, \r\n" + 
//				"      CONCAT( REPEAT(' . . ', COUNT(CAST(p.tree_id AS CHAR)) - 1),  (SELECT  nodeid FROM " + treetablename  + "WHERE tree_id = n.tree_id )  ) AS treenodeid ,\r\n" + 
//				"      (SELECT  nodeid FROM " + treetablename  + " WHERE tree_id = n.tree_id ) AS nodeid,\r\n" + 
//				"      (SELECT  nodetype FROM " + treetablename  + " WHERE tree_id = n.tree_id ) AS nodetype,\r\n" + 
//				"      \r\n" + 
//				"      \r\n" + 
//				"      ( SELECT Name FROM\r\n" + 
//				"			( 	SELECT " + treetablename  + ".tree_id, a股.`股票名称` AS Name\r\n" + 
//				"			FROM 	" + treetablename  + " JOIN a股 ON " + treetablename  + ".nodeid = a股.`股票代码` AND " + treetablename  + ".nodetype = 6\r\n" + 
//				"			UNION\r\n" + 
//				"			SELECT " + treetablename  + ".tree_id, 通达信板块列表.`板块名称`\r\n" + 
//				"			FROM 	" + treetablename  + " JOIN 通达信板块列表 ON " + treetablename  + ".nodeid = 通达信板块列表.`板块ID` AND " + treetablename  + ".nodetype = 4\r\n" + 
//				"			UNION\r\n" + 
//				"			SELECT " + treetablename  + ".tree_id,产业链板块州列表.`板块州名称`\r\n" + 
//				"			FROM 	" + treetablename  + " JOIN 产业链板块州列表   ON " + treetablename  + ".nodeid = 产业链板块州列表.`板块州代码` AND " + treetablename  + ".nodetype = 8\r\n" + 
//				"			UNION\r\n" + 
//				"			SELECT " + treetablename  + ".tree_id, 产业链板块国列表.`板块国名称`\r\n" + 
//				"			FROM 	" + treetablename  + " JOIN 产业链板块国列表 ON " + treetablename  + ".nodeid = 产业链板块国列表.id AND " + treetablename  + ".nodetype = 9\r\n" + 
//				"			\r\n" + 
//				"			) A\r\n" + 
//				"			WHERE tree_id = n.tree_id\r\n" + 
//				"      \r\n" + 
//				"		 ) AS name,\r\n" + 
//				"      \r\n" + 
//				"      \r\n" + 
//				"      n.addedtime, n.isolatedtime\r\n" + 
//				"      \r\n" + 
//				"    FROM " + treetablename  + " AS n, " + treetablename  + " AS p\r\n" + 
//				"    WHERE (n.lft BETWEEN p.lft AND p.rgt)\r\n" + 
//				"    GROUP BY tree_id\r\n" + 
//				"    ORDER BY n.lft";
//		
//		List<BkChanYeLianTreeNode> nodelist = new ArrayList<>();
//		CachedRowSetImpl rs = null;
//	    try {  
//	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//	    	
//	    	while(rs.next()) {
//		    	int nestedtree_id = rs.getInt("tree_id");
//		    	String nodeid = rs.getString("nodeid");
//		    	String nodename = rs.getString("name");
//		    	Integer nodetype = rs.getInt("nodetype");
//		    	int left = rs.getInt("lft");
//		    	int right = rs.getInt("rgt");
//		    	int parent_id = rs.getInt("parent_id");
//		    	Date isolatedtime = rs.getDate("isolatedtime");
//		    	
//		    	if(nestedtree_id == 1)
//		    		continue;
//		    	
//		    	CylTreeNestedSetNode tmpnode;
//		    	tmpnode = new CylTreeNestedSetNode (nodeid,nodename,nodetype);
//		    	tmpnode.setNestedId(nestedtree_id);
//		    	tmpnode.setNestedLeft(left);
//		    	tmpnode.setNestedRight(right);
//		    	tmpnode.setNestedParent(parent_id);
//		    	if(isolatedtime != null)
//		    		tmpnode.setNodeIsolatedDate(isolatedtime.toLocalDate());
//		    	
//		    	nodelist.add(tmpnode);
//		    }
//	    } catch(java.lang.NullPointerException e){ e.printStackTrace();
//	    } catch (SQLException e) {e.printStackTrace();
//	    } catch(Exception e){e.printStackTrace();
//	    } finally {	if(rs != null)	try {rs.close();rs = null;} catch (SQLException e) {e.printStackTrace();}
//	    } 
//	    
//	    return nodelist;
//	}
	/*
	 * 
	 */
	private String getTreeTableNamyByTreeId()
	{
		String treeid = this.treecyl.getTreeId().toUpperCase();
		switch(treeid) {
		case "TDXBANKUAISOCIALTREE":
			return "通达信板块socialtree_map";
		case "CYLTREE":
			return "tree_map";
		}
		return null;
	}
	/*
	 * 
	 */
	public List<BkChanYeLianTreeNode> createTreeOfChanYeLian ()
	{
		String treeid = this.treecyl.getTreeId().toUpperCase();
		String query = null;
		switch(treeid) {
		case "TDXBANKUAISOCIALTREE":
			query = "{call r_BanKuaiSocialTree_return_tree(?)}";
			break;
		case "CYLTREE":
			query = "{call r_ChanYeLianTree_return_tree(?)}";
			break;
		}
		
		List<BkChanYeLianTreeNode> nodelist = new ArrayList<>();
		 CachedRowSetImpl rsagu = null;
		 try{
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
				} catch (Exception e) {	e.printStackTrace();
				} finally {
					try { if(rsagu != null)	rsagu.close();	rsagu = null;} catch (SQLException e) {e.printStackTrace();		}
			}
		 
		 return nodelist;
	}
	/*
	 * 
	 */
	public void addNodeToNestedDatabase (CylTreeNestedSetNode topNode,CylTreeNestedSetNode childrennode) throws SQLException
	{
		String treetablename = getTreeTableNamyByTreeId ();
		
		String treeid = this.treecyl.getTreeId().toUpperCase();
		String query = null;
		switch(treeid) {
		case "TDXBANKUAISOCIALTREE":
			query = "{CALL r_BanKuaiSocialTree_tree_traversal(?,?,?)}";
			break;
		case "CYLTREE":
			query = "{CALL r_ChanYeLianTree_tree_traversal(?,?,?)}";
			break;
		}
		
		int treenodeid = topNode.getNestedId ();
		CachedRowSetImpl rsagu = null;
		try{
					    java.sql.CallableStatement stm = connectdb.getCurrentDataBaseConnect().prepareCall(query); 
					    stm.setString(1, "insert");
					    stm.setString(2, null);
					    stm.setInt(3, treenodeid);
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
					    String sqlquerystat = "UPDATE " + treetablename + " SET " 
					    						+ " nodeid = '" +  childrennode.getMyOwnCode() +  "', "
					    						+ " nodetype = " +  childrennode.getType() 
					    						+ " where tree_id=" + lstid
					    						;
					    connectdb.sqlInsertStatExecute(sqlquerystat);
			} //catch (SQLException e) {					e.printStackTrace();} 
			finally {
				try {		if(rsagu != null)			rsagu.close();
				} catch (SQLException e) {e.printStackTrace();	}
				rsagu = null;
			}
				
		createTagsOnCurrentTreePathForChanYeLianNode (childrennode);
	}
//	private void addTreeNodeToNestedDatabase(CylTreeNestedSetNode topNode, CylTreeNestedSetNode childrennode) 
//	{
//		String treetablename = getTreeTableNamyByTreeId ();
//		int parent_treeid = topNode.getNestedId ();
//		
//		Integer new_lft = null;
//		String sqlquerystat1 = " SELECT rgt INTO new_lft FROM " + treetablename + " WHERE tree_id = " + parent_treeid;
//		CachedRowSetImpl rs = null;
//	    try {  
//	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat1);
//	    	while(rs.next()) {
//		    	new_lft = rs.getInt("new_lft");
//		    }
//	    	rs.close();rs = null;
//	    	String sqlquerystat2 = " UPDATE " + treetablename + " SET rgt = rgt + 2 WHERE rgt >=" + new_lft;
//	    	connectdb.sqlUpdateStatExecute(sqlquerystat2);
//			String sqlquerystat3 = " UPDATE " + treetablename + " SET lft = lft + 2 WHERE lft > " + new_lft;
//			connectdb.sqlUpdateStatExecute(sqlquerystat3);
//			String sqlquerystat4 = " INSERT INTO " + treetablename + " (lft, rgt, parent_id) VALUES (" + new_lft + ", (" + new_lft + 1 + "), pparent_id)";
//			connectdb.sqlInsertStatExecute(sqlquerystat4);
//			String sqlquerystat5 = " SELECT LAST_INSERT_ID()";
//			rs = connectdb.sqlQueryStatExecute(sqlquerystat5);
//			Integer lstid = null;
//		    while(rs.next()) {
//		    	lstid = rs.getInt("LAST_INSERT_ID()");
//		    }
//		    
//		    
//		    childrennode.setNestedId(lstid);
//		    String sqlquerystat = "UPDATE " + treetablename + " SET " 
//		    						+ " nodeid = '" +  childrennode.getMyOwnCode() +  "', "
//		    						+ " nodetype = " +  childrennode.getType() 
//		    						+ " where tree_id=" + lstid
//		    						;
//		    connectdb.sqlInsertStatExecute(sqlquerystat);
//	    	
//	    } catch(java.lang.NullPointerException e){ e.printStackTrace();
//	    } catch (SQLException e) {e.printStackTrace();
//	    } catch(Exception e){e.printStackTrace();
//	    } finally {	if(rs != null)	try {rs.close();rs = null;} catch (SQLException e) {e.printStackTrace();}
//	    } 
//	}
	/*
	 * 
	 */
	public void moveNodeInNestedDatabase (CylTreeNestedSetNode movedNode,CylTreeNestedSetNode parentsnode)
	{
		String treeid = this.treecyl.getTreeId().toUpperCase();
		String query = null;
		switch(treeid) {
		case "TDXBANKUAISOCIALTREE":
			query = "{CALL r_BanKuaiSocialTree_tree_move(?,?)}";
			break;
		case "CYLTREE":
			query = "{CALL r_ChanYeLianTree_tree_move(?,?)}";
			break;
		}

		int movednodetreeid = movedNode.getNestedId ();
		TreeNode[] path = movedNode.getPath();
		int parentnodetreeid = parentsnode.getNestedId();
		
//		this.deleteTagsToChanYeLianNode (movedNode);
		
		CachedRowSetImpl rsagu = null;
		try{
			java.sql.CallableStatement stm = connectdb.getCurrentDataBaseConnect().prepareCall(query); 
			stm.setInt(1, movednodetreeid);
			stm.setInt(2, parentnodetreeid);
			stm.executeQuery();
			stm.close();
		} catch (Exception e) {	e.printStackTrace();
		} finally {
			try {	if(rsagu != null)	rsagu.close();	} catch (SQLException e) {e.printStackTrace();}
			rsagu = null;
		}
		
//		this.setTagsToChanYeLianNode (movedNode);
	}
//	private void moveTreeNodeInNestedDatabase(CylTreeNestedSetNode movedNode, CylTreeNestedSetNode parentsnode) 
//	{
//		int movednodetreeid = movedNode.getNestedId ();
//		TreeNode[] path = movedNode.getPath();
//		int parentnodetreeid = parentsnode.getNestedId();
//		
//		CachedRowSetImpl rsagu = null;
//		try{
//			String query = "{CALL r_BanKuaiSocialTree_move(?,?)}";
//			java.sql.CallableStatement stm = connectdb.getCurrentDataBaseConnect().prepareCall(query); 
//			stm.setInt(1, movednodetreeid);
//			stm.setInt(2, parentnodetreeid);
//			stm.executeQuery();
//			stm.close();
//		} catch (Exception e) {	e.printStackTrace();
//		} finally {
//			try {	if(rsagu != null)	rsagu.close();
//			} catch (SQLException e) {e.printStackTrace();}
//			rsagu = null;
//		}
//	}
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
		} catch(java.lang.NullPointerException e){   	e.printStackTrace();
		} catch (SQLException e) {    	e.printStackTrace();
		} catch(Exception e){	    	e.printStackTrace();
		} finally {
		    	if(rspd != null)
					try {	rspd.close();	rspd = null;	} catch (SQLException e) {		e.printStackTrace();}
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
		} catch (MysqlDataTruncation e) {	e.printStackTrace();
		} catch (SQLException e) {		e.printStackTrace();	}
		
		BkChanYeLianTreeNode gpc = new CylTreeNestedSetNode (gpccode,gpcname,BkChanYeLianTreeNode.GPC);
		
		CylTreeNestedSetNode treeroot = (CylTreeNestedSetNode)treecyl.getModel().getRoot();
		try {	this.addNodeToNestedDatabase(treeroot, (CylTreeNestedSetNode)gpc);
		} catch (SQLException e) {e.printStackTrace();
		}
		
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
		       
		} catch(java.lang.NullPointerException e){ 		    	e.printStackTrace();
		} catch (SQLException e) {		    	e.printStackTrace();
		} catch(Exception e){		    	e.printStackTrace();
		} finally {
		    	if(rspd != null)
					try {	rspd.close();	rspd = null;	} catch (SQLException e) {	e.printStackTrace();		}
		}

		return gpcindb;
	}
	/*
	 * 
	 */
	public void createTagsOnCurrentTreePathForChanYeLianNode (CylTreeNestedSetNode node)
	{
		BanKuaiAndStockTree treebkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		if(node.getType() == BkChanYeLianTreeNode.SUBGPC ) { //如果插入一个SUBPC，他的所有上级中如果有板块或者个股，必须具有这个subpc 的tag
			TreeNode[] path = node.getPath();
	    	for(int i=1;i<path.length-1;i++) {
	    		CylTreeNestedSetNode tmpnode = (CylTreeNestedSetNode)path[i];
	    		if(tmpnode.getType() == BkChanYeLianTreeNode.SUBGPC || tmpnode.getType() == BkChanYeLianTreeNode.GPC
	    				|| tmpnode.getType() == BkChanYeLianTreeNode.DAPAN ) 
	    			continue;
	    		
	    		BkChanYeLianTreeNode treenode = treebkstk.getSpecificNodeByHypyOrCode(tmpnode.getMyOwnCode(), tmpnode.getType());
	    		if(treenode == null)
					continue;
	    		
	    		TagsServiceForNodes nodetagservice = new TagsServiceForNodes (treenode);
	    		Tag nodetag = new Tag (node.getMyOwnName());
	    		try {
					nodetagservice.createTag(nodetag);
				} catch (SQLException e) {
						e.printStackTrace();
				}
	    		
	    		nodetagservice = null;
	    		nodetag = null;
	     }
	  } 
	  else {
			BkChanYeLianTreeNode treenode = treebkstk.getSpecificNodeByHypyOrCode(node.getMyOwnCode(), node.getType());
			if(treenode == null)	return;
			
			TagsServiceForNodes nodetagservice = new TagsServiceForNodes (treenode);
			TreeNode[] path = node.getPath();
	    	for(int i=1;i<path.length-1;i++) {
	    		CylTreeNestedSetNode tmpnode = (CylTreeNestedSetNode)path[i];
	    		if(tmpnode.getType() != BkChanYeLianTreeNode.SUBGPC) 
	    			continue;
	    		
	    		Tag nodetag = new Tag (tmpnode.getMyOwnName());
	    		try {	nodetagservice.createTag(nodetag);
				} catch (SQLException e) {e.printStackTrace();}
	    		nodetag = null;
	    	}
	    	nodetagservice = null;
	 }
		
	 treebkstk = null;
	}
	/*
	 * 
	 */
	public void deleteTagsOnCurrentTreePathForChanYeLianNode (CylTreeNestedSetNode node)
	{
			BanKuaiAndStockTree treebkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
			if(node.getType() == BkChanYeLianTreeNode.SUBGPC ) { //如果删除是一个SUBPC，他的所有上级中如果有板块或者个股，必须删除这个subpc 的tag
				TreeNode[] path = node.getPath();
		    	for(int i=1;i<path.length-1;i++) {
		    		CylTreeNestedSetNode tmpnode = (CylTreeNestedSetNode)path[i];
		    		if(tmpnode.getType() == BkChanYeLianTreeNode.SUBGPC || tmpnode.getType() == BkChanYeLianTreeNode.GPC
		    				|| tmpnode.getType() == BkChanYeLianTreeNode.DAPAN ) 
		    			continue;
		    		
		    		BkChanYeLianTreeNode treenode = treebkstk.getSpecificNodeByHypyOrCode(tmpnode.getMyOwnCode(), tmpnode.getType());
		    		if(treenode == null)
						continue;
		    		
		    		TagsServiceForNodes nodetagservice = new TagsServiceForNodes (treenode);
		    		Tag nodetag = new Tag (node.getMyOwnName());
		    		try {
						nodetagservice.deleteTag(nodetag);
					} catch (SQLException e) {
							e.printStackTrace();
					}
		    		
		    		nodetagservice = null;
		    		nodetag = null;
		     }
		  } 
		  else {
				BkChanYeLianTreeNode treenode = treebkstk.getSpecificNodeByHypyOrCode(node.getMyOwnCode(), node.getType());
				if(treenode == null)
					return;
				
				TagsServiceForNodes nodetagservice = new TagsServiceForNodes (treenode);
				
				TreeNode[] path = node.getPath();
		    	for(int i=1;i<path.length-1;i++) {
		    		CylTreeNestedSetNode tmpnode = (CylTreeNestedSetNode)path[i];
		    		if(tmpnode.getType() != BkChanYeLianTreeNode.SUBGPC) 
		    			continue;
		    		
		    		Tag nodetag = new Tag (tmpnode.getMyOwnName());
		    		try {
						nodetagservice.deleteTag(nodetag);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		nodetag = null;
		    	}
		    	nodetagservice = null;
		 }
			
		 treebkstk = null;
	 
//		BanKuaiAndStockTree treebkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
//		BkChanYeLianTreeNode treenode = treebkstk.getSpecificNodeByHypyOrCode(node.getMyOwnCode(), node.getType());
//		if(treenode == null)
//			return;
//		
//		TagsServiceForNodes nodetagservice = new TagsServiceForNodes (treenode);
//		
//		TreeNode[] path = node.getPath();
//    	for(int i=1;i<path.length-1;i++) {
//    		CylTreeNestedSetNode tmpnode = (CylTreeNestedSetNode)path[i];
//    		if(tmpnode.getType() == BkChanYeLianTreeNode.SUBGPC) {
//    			Tag nodetag = new Tag (tmpnode.getMyOwnName());
//    			try {
//					nodetagservice.deleteTag(nodetag);
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//    		}
//    	}
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
		String treetablename = getTreeTableNamyByTreeId ();
    	int nestedtreeid = ((CylTreeNestedSetNode)aNode).getNestedId();
    	CachedRowSetImpl rsagu = null;
		 try{
					   String query = "UPDATE "  + treetablename + " SET isolatedtime = " + "'" + LocalDate.now().toString() + "'"
							   		+ " WHERE tree_id = " + nestedtreeid 
							   ;
					    int stm = connectdb.sqlUpdateStatExecute(query); 
		} catch (Exception e) {		e.printStackTrace();
		} finally {
						try {	if(rsagu != null)	rsagu.close();	} catch (SQLException e) {	e.printStackTrace();}
						rsagu = null;
		} 

	    for(int i = 0 ; i < aNode.getChildCount() ; i++)
	    	isolatedNodeInDb((CylTreeNestedSetNode)aNode.getChildAt(i));
	}
	private void deleteNodes (CylTreeNestedSetNode delnode)
	{
		//在数据中删除
		deleteNodeInDb (delnode);
		
		treecyl.deleteNodes (   new TreePath (delnode.getPath() ) );  //在树上删除
	}
	public  void deleteNodeInDb (CylTreeNestedSetNode aNode)
	{
		String treeid = this.treecyl.getTreeId().toUpperCase();
		String query = null;
		switch(treeid) {
		case "TDXBANKUAISOCIALTREE":
			query = "{CALL r_BanKuaiSocialTree_return_treeleaves(?)}";
			break;
		case "CYLTREE":
			query = "{CALL r_ChanYeLianTree_return_treeleaves(?)}";
			break;
		}
		
		String query2 = null;
		switch(treeid) {
		case "TDXBANKUAISOCIALTREE":
			query2 = "{CALL r_BanKuaiSocialTree_tree_traversal(?, ?, ?)}";//"{CALL r_BanKuaiSocialTree_return_treeleaves(?)}";
			break;
		case "CYLTREE":
			query2 = "{CALL r_ChanYeLianTree_tree_traversal(?, ?, ?)}";
			break;
		}
		
		Integer nestedtreeid = ((CylTreeNestedSetNode)aNode).getNestedId();
	    CachedRowSetImpl rsagu = null;
		try{
				 java.sql.CallableStatement stm = connectdb.getCurrentDataBaseConnect().prepareCall(query); 
				 stm.setInt(1, nestedtreeid);

				 ResultSet rs = stm.executeQuery();
				 while (rs.next()) {
					 int deltreeid = rs.getInt("tree_id");
					 java.sql.CallableStatement stmdel = connectdb.getCurrentDataBaseConnect().prepareCall(query2); 
					 stmdel.setString(1, "delete");
					 stmdel.setInt(2, deltreeid);
					 stmdel.setString(3, null);
					 
					 stmdel.executeQuery();
					 
					 if(deltreeid == nestedtreeid ) 
						 return;
				 }
				 stm.close();
			} catch (Exception e) {	e.printStackTrace();
			} finally {
				try {if(rsagu != null)	rsagu.close();	} catch (SQLException e) {	e.printStackTrace();	}
					rsagu = null;
			}
			 
//			deleteNodeInDb (aNode);
	}

//	private void deleteTreeNodeInDb(CylTreeNestedSetNode aNode) 
//	{
//		String treetablename = getTreeTableNamyByTreeId ();
//		Integer nestedtreeid = ((CylTreeNestedSetNode)aNode).getNestedId();
//		String sqlquerystat = 
//				"  SELECT   node.tree_id, node.parent_id,node.lft,node.rgt,\r\n" + 
//				"		     (COUNT(parent.tree_id) - (sub_tree.depth + 1) ) AS depth,\r\n" + 
//				"		     node.nodeid AS nodeid,\r\n" + 
//				"		     node.nodetype AS nodetype,\r\n" + 
//				"		    \r\n" + 
//				"		     node.addedtime,node.isolatedtime\r\n" + 
//				"		     \r\n" + 
//				"    FROM " + treetablename + " AS node,\r\n" + 
//				"         " + treetablename + " AS parent,\r\n" + 
//				"         " + treetablename + " AS sub_parent,\r\n" + 
//				"         \r\n" + 
//				"  ( SELECT node.tree_id, (COUNT(parent.tree_id) - 1) AS depth\r\n" + 
//				"    FROM tree_map AS node,\r\n" + 
//				"		   tree_map AS parent\r\n" + 
//				"	 WHERE node.lft BETWEEN parent.lft AND parent.rgt\r\n" + 
//				"		 AND node.tree_id = " + nestedtreeid + "\r\n" + 
//				"	 GROUP BY node.tree_id\r\n" + 
//				"	 ORDER BY node.lft ) AS sub_tree\r\n" + 
//				"   WHERE node.lft BETWEEN parent.lft AND parent.rgt\r\n" + 
//				"	   AND node.lft BETWEEN sub_parent.lft AND sub_parent.rgt\r\n" + 
//				"	   AND sub_parent.tree_id = sub_tree.tree_id \r\n" + 
//				"	   AND (node.rgt - node.lft) = 1\r\n" + 
//				"   GROUP BY node.tree_id\r\n" + 
//				"   ORDER BY node.lft"
//		;
//	    CachedRowSetImpl rs = null;
//	    CachedRowSetImpl rs2 = null;
//		try{
//				rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//				while (rs.next()) {
//					 int deltreeid = rs.getInt("tree_id");
//					 
//					 String sqlquerystat2 = "SELECT lft, rgt, (rgt - lft), (rgt - lft + 1), parent_id \r\n" + 
//					 		"		  INTO new_lft, new_rgt, has_leafs, width, superior_parent \r\n" + 
//					 		"		  FROM tree_map WHERE tree_id = " + deltreeid
//					 		;
//					 rs2 = connectdb.sqlQueryStatExecute(sqlquerystat);
//					 Integer has_leafs = null; Integer new_lft = null; Integer new_rgt = null; Integer width = null; Integer superior_parent = null;
//					 while(rs2.next()) {
//						 has_leafs = rs.getInt("has_leafs");
//						 new_lft = rs.getInt("new_lft");
//						 new_rgt = rs.getInt("new_rgt");
//						 superior_parent = rs.getInt("superior_parent");
//						 if(has_leafs == 1) {
//							 String sqlquerystat3 = "	DELETE FROM " + treetablename + " WHERE lft BETWEEN "+  new_lft + " AND " + new_rgt +";\r\n"  ;
//							 String sqlquerystat4 = "   UPDATE " + treetablename + " SET rgt = rgt - width WHERE rgt > " + new_rgt + ";\r\n";  
//							 String sqlquerystat5 = "   UPDATE " + treetablename + " SET lft = lft - width WHERE lft > " + new_rgt ;
//							 connectdb.sqlDeleteStatExecute(sqlquerystat3);
//							 connectdb.sqlUpdateStatExecute(sqlquerystat4);
//							 connectdb.sqlUpdateStatExecute(sqlquerystat5);
//						 } else {
//							 String sqlquerystat6 = "	DELETE FROM " + treetablename + " WHERE lft = " + new_lft ; 
//							 String sqlquerystat7 = "   UPDATE " + treetablename + " SET rgt = rgt - 1, lft = lft - 1, parent_id = " + superior_parent + "\r\n"  +
//							 						"	WHERE lft BETWEEN " + new_lft + " AND " + new_rgt ; 
//							 String sqlquerystat8 = "   UPDATE " + treetablename + " SET rgt = rgt - 2 WHERE rgt > " + new_rgt ; 
//							 String sqlquerystat9 = "  UPDATE " + treetablename + " SET lft = lft - 2 WHERE lft > " + new_rgt;
//							 connectdb.sqlDeleteStatExecute(sqlquerystat6);
//							 connectdb.sqlUpdateStatExecute(sqlquerystat7);
//							 connectdb.sqlUpdateStatExecute(sqlquerystat8);
//							 connectdb.sqlUpdateStatExecute(sqlquerystat9);
//						 }
//					 }
//					 
//					 if(deltreeid == nestedtreeid ) 
//						 return;
//				 }
//			} catch (Exception e) {	e.printStackTrace();
//			} finally {
//				try { if(rs != null)	rs.close(); rs = null;	} catch (SQLException e) {	e.printStackTrace();	}
//				try { if(rs2 != null)	rs2.close(); rs2 = null;} catch (SQLException e) {	e.printStackTrace();	}
//			}
//	}
	/*
	 * 
	 */
	public BkChanYeLianTreeNode getChanYeLianInfo(BkChanYeLianTreeNode node) 
	{
		List<BkChanYeLianTreeNode> result = this.getChanYeLianInfo(node.getMyOwnCode(), node.getType());
		node.getNodeJiBenMian().setChanYeLianInfo(result);
		
		return node;
	}
	public  List<BkChanYeLianTreeNode> getChanYeLianInfo (String nodecode, int nodetype)
	{
		String treetablename = getTreeTableNamyByTreeId ();
		
		String query = "SELECT * FROM   " + treetablename 
				+ " WHERE nodeid ='" + nodecode + "'"
				+ " AND nodetype = " + nodetype
				;
		
		String treeid = this.treecyl.getTreeId().toUpperCase();
		String query2 = null;
		switch(treeid) {
		case "TDXBANKUAISOCIALTREE":
			query2 = "{CALL r_BanKuaiSocialTree_return_path(?)}";//"{CALL r_BanKuaiSocialTree_return_treeleaves(?)}";
			break;
		case "CYLTREE":
			query2 = "{CALL r_ChanYeLianTree_return_path(?)}";
			break;
		}
		
		List<BkChanYeLianTreeNode> cylinfo = new ArrayList<> ();
		CachedRowSetImpl rs = null;
		try{
				rs = this.connectdb.sqlQueryStatExecute(query);
				while(rs.next()) {
					int tree_id = rs.getInt("tree_id");
					java.sql.CallableStatement stmdel = connectdb.getCurrentDataBaseConnect().prepareCall(query2);
					stmdel.setInt(1, tree_id);
					 
					ResultSet rsstm = stmdel.executeQuery();
					while(rsstm.next()){
//						int treeid = rsstm.getInt("tree_id");
						String nodeid = rsstm.getString("nodeid") ;
						int cylnodetype = rsstm.getInt("nodetype") ;
						BkChanYeLianTreeNode node = treecyl.getSpecificNodeByHypyOrCode(nodeid, cylnodetype);
						
						cylinfo.add( node );
					}
				}
		} catch (Exception e) {			e.printStackTrace();
		} finally {
			try {if(rs != null)	rs.close();		} catch (SQLException e) {e.printStackTrace();		}
			rs = null;
		}
		
		return cylinfo;
	}
	public List<BkChanYeLianTreeNode>  getSlidingInChanYeLianInfo (String nodecode, int nodetype)
	{
		String treetablename = getTreeTableNamyByTreeId ();
		String sqlquery = "SELECT * FROM  " + treetablename
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
					
					sqlquery = "SELECT * FROM " + treetablename +"\r\n" + 
							"\r\n" + 
							"(SELECT " +  treetablename + ".parent_id,"+  treetablename + ".nodeid  FROM " + treetablename + 
							"WHERE nodeid = '" + nodecode+ "' AND " + treetablename + ".nodetype = " + nodetype + "  \r\n" + 
							") A \r\n" + 
							"where " +  treetablename + ".parent_id = A.parent_id \r\n" + 
							"GROUP BY " + treetablename + ".tree_id"
							;
					rsnext = this.connectdb.sqlQueryStatExecute(sqlquery);
					while(rsnext.next()){
						String sliblingnodeid = rsnext.getString("nodeid") ;
						int cylnodetype = rsnext.getInt("nodetype") ;
						BkChanYeLianTreeNode node = treecyl.getSpecificNodeByHypyOrCode(sliblingnodeid, cylnodetype);
						
						cylinfo.add(node); 
					}
				}
		} catch (Exception e) {	e.printStackTrace();
		} finally {
				try {	if(rs != null)	rs.close();		rs = null;	} catch (SQLException e) {	e.printStackTrace();}
		}
		
		return cylinfo;
	}
	public List<BkChanYeLianTreeNode> getChildrenInChanYeLianInfo(String nodecode, int nodetype) 
	{
		String treetablename = getTreeTableNamyByTreeId ();
		String query = "SELECT * FROM   " + treetablename 
				+ " WHERE nodeid ='" + nodecode + "'"
				+ " AND nodetype = " + nodetype
				;
		
		String treeid = this.treecyl.getTreeId().toUpperCase();
		String query2 = null;
		switch(treeid) {
		case "TDXBANKUAISOCIALTREE":
			query2 = "{CALL r_BanKuaiSocialTree_return_subtree(?)}";//"{CALL r_BanKuaiSocialTree_return_path(?)}";
			break;
		case "CYLTREE":
			query2 = "{CALL r_ChanYeLianTree_return_subtree(?)}";
			break;
		}
		
		List<BkChanYeLianTreeNode> cylinfo = new ArrayList<> ();
		CachedRowSetImpl rs = null;
		try{
				rs = this.connectdb.sqlQueryStatExecute(query);
				while(rs.next()) {
					int tree_id = rs.getInt("tree_id");
					
					java.sql.CallableStatement stmdel = connectdb.getCurrentDataBaseConnect().prepareCall(query2); 
					stmdel.setInt(1, tree_id);
					 
					ResultSet rsstm = stmdel.executeQuery();
					while(rsstm.next()){
//						int treeid = rsstm.getInt("tree_id");
						String nodeid = rsstm.getString("nodeid") ;
						int cylnodetype = rsstm.getInt("nodetype") ;
						BkChanYeLianTreeNode node = treecyl.getSpecificNodeByHypyOrCode(nodeid, cylnodetype);
						
						cylinfo.add( node );
					}
				}
		} catch (Exception e) {e.printStackTrace();
		} finally {
				try {if(rs != null) rs.close();} catch (SQLException e) {	e.printStackTrace();}
				rs = null;	
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


