package com.exchangeinfomanager.TagServices;

import java.awt.Color;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


import com.exchangeinfomanager.Tag.InsertedTag;
import com.exchangeinfomanager.Tag.NodeInsertedTag;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.google.common.base.Joiner;
import com.mysql.jdbc.MysqlDataTruncation;
import com.sun.rowset.CachedRowSetImpl;

public class TagsDbOperation 
{
	private ConnectDataBase connectdb;
	private AllCurrentTdxBKAndStoksTree bkstk;

	public TagsDbOperation() 
	{
		connectdb = ConnectDataBase.getInstance();
		this.bkstk = AllCurrentTdxBKAndStoksTree.getInstance();
	}
	
	private static Logger logger = Logger.getLogger(TagsDbOperation.class);
	
	private InsertedTag checkTagExistInSystem (String tagname)
	{
		try {
					String sqlq = "SELECT COUNT(*) AS COUNT , id, 板块国名称, DefaultCOLOUR, 板块国说明  FROM 产业链板块国列表 WHERE 板块国名称  =  '" + tagname + "'";
			 		CachedRowSetImpl checkresult = connectdb.sqlQueryStatExecute(sqlq);
			 		while(checkresult.next()) {
						int count = checkresult.getInt ("COUNT");
						if(count > 0) {
							Color color = Color.WHITE;
							try {
								color = Color.decode( checkresult.getString("DefaultCOLOUR")) ;
							} catch (java.lang.Exception e)  {}
							String desc;
							try {
								desc = checkresult.getString("板块国名称").trim();
							} catch (java.lang.Exception e)  {
								desc = "";
							}
							Tag newtag = new Tag (checkresult.getString("板块国名称"), color, desc ) ;
							InsertedTag anewtag = new InsertedTag (newtag, checkresult.getInt("id"));
							
							anewtag.setDescription( checkresult.getString("板块国说明") );
							
							return anewtag;
						}
			 		}
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException exc) {exc.printStackTrace();return null;
		} catch (MysqlDataTruncation e) {e.printStackTrace();return null;
		} catch (SQLException e) {e.printStackTrace();return null;
		}
		
		return null;
	}
	
	public InsertedTag createSystemTags (Tag newtag)
	{
		String tagname = newtag.getName();
		//先检查系统里面有没有，有直接返回
		InsertedTag checktag = this.checkTagExistInSystem (tagname);
		if(checktag != null)
			return checktag;
		
		String color =  "#"+Integer.toHexString(  newtag.getColor() .getRGB()).substring(2);
		String desc = newtag.getDescription();
		
		String sqlinsertquery = "INSERT INTO 产业链板块国列表 (板块国名称, DefaultCOLOUR,板块国说明) VALUES("
								+ "'" + tagname + "', "
								+ "'" + color.toString() + "', "
								+ "'" + desc + "'"
								+ ")"
								;
		int autoIncKeyFromApi = 0;
		try {
			autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertquery);
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException exc) {exc.printStackTrace();return null;
		} catch (MysqlDataTruncation e) {e.printStackTrace();return null;
		} catch (SQLException e) {e.printStackTrace();return null;}
		
		return new InsertedTag (newtag, autoIncKeyFromApi);
	}

	public  Collection<Tag> getAllTagsFromDataBase() throws SQLException
	{
		Collection<Tag> labels = new HashSet<>();
		
		String sqlquerystat = "SELECT * FROM 产业链板块国列表";
		CachedRowSetImpl result = null;
 		try {
 			result = connectdb.sqlQueryStatExecute(sqlquerystat);
 			while (result.next()) {
		        	String bkzname = result.getString("板块国名称");
 		        	String tagcolor = result.getString("DefaultCOLOUR") ;
 		        	Color defaultColor = Color.WHITE;
 		        	try {
 		        		defaultColor = Color.decode( tagcolor);
 		        	} catch (java.lang.Exception e) {
 		        	}
 		        	String desc;
					try {
						desc = result.getString("板块国说明").trim();
					} catch (java.lang.Exception e)  {
						desc = "";
					}
 		        	int id = result.getInt ("id");
 		        	Tag newtag = new Tag(bkzname,defaultColor,desc);
 		        	InsertedTag label = new InsertedTag(newtag,id );
 	                labels.add(label);
 	        }
 		} catch(java.lang.NullPointerException e) {e.printStackTrace();
 	    } catch(Exception e){e.printStackTrace();
 	    } finally {
 	    	try {	result.close(); 		result = null;} catch (SQLException e) {e.printStackTrace();}
 	    }
		return labels;
    }
	/*
	 * 
	 */
	
	public Collection<Tag> getNodesTagsFromDataBase(Collection<BkChanYeLianTreeNode> nodesets) throws SQLException 
	{
        Collection<Tag> labels = new HashSet<>();
        
        
        for(BkChanYeLianTreeNode code : nodesets) 
        		labels.addAll( this.getNodeTagsFromDataBase (code.getMyOwnCode(),code.getType()) );
        
        
        return labels;
	}
	public Collection<NodeInsertedTag> getNodeTagsFromDataBase(String nodecode, int nodetype) throws SQLException
	{
		Collection<NodeInsertedTag> labels = new HashSet<>();
		
		String	sqlquerystat = "SELECT * FROM 产业链板块国列表 \r\n" + 
		 				" JOIN 产业链板块国个股板块对应表 \r\n" + 
		 				"  ON 产业链板块国列表.id = 产业链板块国个股板块对应表.`板块国ID`\r\n" + 
		 				"   WHERE 产业链板块国个股板块对应表.`个股板块` = '" + nodecode + "'"
		 				+ " AND 产业链板块国个股板块对应表.`个股板块类型` = " + nodetype
		 				;
		
		CachedRowSetImpl result = null;
		try {
			result = connectdb.sqlQueryStatExecute(sqlquerystat);
			
		   while (result.next()) {
		     		String bkzname = result.getString("板块国名称");
			     	Color defaultColor = Color.WHITE;
					try {
						defaultColor = Color.decode( result.getString("DefaultCOLOUR")  ) ;
					} catch (java.lang.Exception e)  {}
					String desc;
					try {
						desc = result.getString("板块国说明").trim();
					} catch (java.lang.Exception e)  {
						desc = "";
					}
			     	int id = result.getInt("id");
			     	Tag newtag = new Tag(bkzname,defaultColor,desc);
			     	InsertedTag intag = new InsertedTag (newtag, id);
			     	
			     	int matchid = result.getInt("matchid");
			     	String gegubk = result.getString("个股板块");
			     	Color color = Color.WHITE;
			     	try {
			     		color=  Color.decode( result.getString("颜色") );
			     	} catch (java.lang.Exception e) {
			     		
			     	}
			     	
			         NodeInsertedTag label = new NodeInsertedTag(intag, matchid, gegubk, color);
			         labels.add(label);
		   }
		}catch(java.lang.NullPointerException e){ 
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}  finally {
			try {
				result.close();
				result = null;
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return labels;

	}
	/*
	 * 
	 */
	public Tag attachedTagToNodes(Collection<BkChanYeLianTreeNode> nodesets, Tag label)
	{
		for(BkChanYeLianTreeNode tmpnode : nodesets)
			this.attachedTagToNode(tmpnode, label);
		
		return label;
	}
	public  Collection<Tag> attachedTagsToNodes(Set<BkChanYeLianTreeNode> nodesets, Collection<Tag> label) 
	{
		for(BkChanYeLianTreeNode tmpnode : nodesets) {
			for ( Iterator<Tag> lit = label.iterator(); lit.hasNext(); ) {
				Tag l = lit.next();
				this.attachedTagToNode(tmpnode, l);
			}
		}
		
		return label;
	}
	public Tag attachedTagToNode(BkChanYeLianTreeNode node, Tag label) 
	{

		int lname = ((InsertedTag)label).getID();
		String color =   "#"+Integer.toHexString( label.getColor() .getRGB()).substring(2);
		String nodecode = node.getMyOwnCode();
		int nodetype = node.getType();
		
		String sqlquery = "INSERT INTO 产业链板块国个股板块对应表(板块国ID,个股板块,个股板块类型,颜色) VALUES("
				+ "" + lname + ","
				+ "'" + nodecode + "',"
				+ nodetype + ","
				+ "'" + color.toString() + "'" 
				+ ")"
				;
		int autoIncKeyFromApi = 0;
		try {
			autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlquery);
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
			logger.info ("插入失败，可能是记录已经存在，请检查！");

		} catch (MysqlDataTruncation e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		return new NodeInsertedTag( (InsertedTag)label, autoIncKeyFromApi, nodecode, Color.decode(color) );
	}

	
	public void deleteSystemTags(Collection<Tag> label)
	{
		for ( Iterator<Tag> lit = label.iterator(); lit.hasNext(); ) {
			Tag l = lit.next();
		    this.deleteSystemTag (l);
		}
	}
	
	public void deleteSystemTag(Tag l) 
	{
		String lname = l.getName();
		int tagid = ((InsertedTag)l).getID();
		try {
 			String sqlqueryinsys = "DELETE FROM 产业链板块国列表  WHERE  "
	 				+ " 板块国名称 = '" + lname + "'"
	 				;
	 		int autoIncKeyFromApi = 0;
 			autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqlqueryinsys);
 		} catch (MysqlDataTruncation e) {
 			e.printStackTrace();
 		} catch (SQLException e) {
 			e.printStackTrace();
 		}
 		//绝对不可以在这里替其他service删除，放这里是威力提醒
//		try {
//			String sqlqueryindy = "DELETE FROM 产业链板块国个股板块对应表 WHERE  "
//					+ " 板块国id = " + tagid + ""
//					;
//			int autoIncKeyFromApi = 0;
//			autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqlqueryindy);
//		} catch (MysqlDataTruncation e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			String sqlqueryindy = "DELETE FROM 产业链板块国新闻对应表  WHERE  "
//					+ " 板块国id = " + tagid + ""
//					;
//			int autoIncKeyFromApi = 0;
//			autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqlqueryindy);
//		} catch (MysqlDataTruncation e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}

	
	public void unattachedTagsFromNodes(Collection<BkChanYeLianTreeNode> nodesets, Collection<Tag> label) 
	{
		for ( Iterator<BkChanYeLianTreeNode> it = nodesets.iterator(); it.hasNext(); ) {
			BkChanYeLianTreeNode f = it.next();
	        
			for ( Iterator<Tag> lit = label.iterator(); lit.hasNext(); ) {
		         Tag l = lit.next();
		        
		         this.unattachedTagFromNode (f, l);
		    }
			
	    }
		
	}
	/*
	 * 当有系统关键字被删除时候，所有表都要强制删除
	 */
	public void forcedUnattachedTagFromAllNodes (Tag l)
	{
		int id = ((InsertedTag)l).getID();
		
		String sqlquery = "DELETE FROM 产业链板块国个股板块对应表 WHERE  "
				+ " 板块国ID = '" + id + "'"
				;
		
		int autoIncKeyFromApi = 0;
		try {
			autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqlquery);
		} catch (MysqlDataTruncation e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public void unattachedTagFromNode(BkChanYeLianTreeNode node, Tag l)
	{
		
		String nodecode = node.getMyOwnCode();
		int nodetype = node.getType ();
		String sqlquery;
		if(l instanceof InsertedTag) {
			int id= ((InsertedTag)l).getID();
			sqlquery = "DELETE FROM 产业链板块国个股板块对应表 WHERE  "
					+ " 板块国ID = '" + id + "'"
					+ " AND 个股板块 = '" + nodecode + "'" 
					+ " AND 个股板块类型 = " + nodetype 
					;
		} else {
			sqlquery = "DELETE FROM 产业链板块国个股板块对应表   "
					+ " WHERE  ( 板块国ID  IN ("
					+ " SELECT 产业链板块国列表.id FROM 产业链板块国列表"
					+ " WHERE  产业链板块国列表.板块国名称 = '" + l.getName()  + "' ) )"
					+ " AND 个股板块 = '" + nodecode + "'" 
					+ " AND 个股板块类型 = " + nodetype 
					;
		}
		
		int autoIncKeyFromApi = 0;
		try {
			autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqlquery);
		} catch (MysqlDataTruncation e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateSystemTags(Tag l) 
	{
		int id = ((InsertedTag)l).getID();
		String lname = l.getName();
		Color lcolor = l.getColor();
		String color =   "#"+Integer.toHexString( lcolor.getRGB()).substring(2);
		String desc = l.getDescription();
		desc.replaceAll(",", ".");
		desc.replaceAll("，", ". ");
		
		String sqlupdated;
		if( l  instanceof NodeInsertedTag) { //NodeInsertedTag就不改系统的颜色)
			 sqlupdated = "UPDATE 产业链板块国列表 SET "
					+ "  板块国名称 = '" + lname + "', " 
					+ " 板块国说明 = '" + desc + "' "
					+ "  WHERE id = " + id
					;
		} else {
			sqlupdated = "UPDATE 产业链板块国列表 SET "
					+ " 板块国名称 = '" + lname + "', " 
					+ " DefaultCOLOUR= '" + color + "', "
					+ " 板块国说明 = '" + desc + "'"
					+ " WHERE id = " + id
					;
		}
		
		try {
			int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute( sqlupdated);
		} catch (MysqlDataTruncation e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	public void updateTagForNodesInDataBase(Tag label)
	{
		int matchid = ((NodeInsertedTag)label).getMatchID();
		String color =   "#"+Integer.toHexString(  ((NodeInsertedTag)label).getNodeMachColor().getRGB()).substring(2);
		
//		Tag systg = ((NodeInsertedTag)label).getTag();
//		this.updateSystemTags (systg);
		
		try {
			String sql = "UPDATE 产业链板块国个股板块对应表  SET \r\n" +
					"  颜色 = '" +  color + "'\r\n" + 
					 " WHERE matchid = " + matchid
					;
			int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute( sql);
		} catch (MysqlDataTruncation e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} 				 
	}
	public InsertedTag combinLabelsToNewOneForSystem (Collection<Tag> curlabls, Tag newlabel)
	{
		//1.在系统tag表，删除原来的，添加一个新的。
		this.deleteSystemTags (curlabls);
		
		//检查系统表里面有没有已经存在
		InsertedTag already = null;
		try {
		 		String sqlq = "SELECT COUNT(*) AS COUNT , id, 板块国名称 FROM 产业链板块国列表 where 板块国名称 = ' " + newlabel.getName() + "'";
		 		CachedRowSetImpl checkresult = connectdb.sqlQueryStatExecute(sqlq);
		 		while(checkresult.next()) {
				int count = checkresult.getInt ("COUNT");
				if(count > 0) {
					String desc;
					try {
						desc = checkresult.getString("板块国名称").trim();
					} catch (java.lang.Exception e)  {
						desc = "";
					}
					String name = checkresult.getString("板块国名称");
					Color color = Color.decode( checkresult.getString("DefaultCOLOUR"));
					Tag newtag = new Tag (name , color, desc );
					return  new InsertedTag ( newtag, checkresult.getInt("id"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		InsertedTag lbltag = this.createSystemTags (newlabel);
		return lbltag;
	}
	public void combinLabelsToNewOneForNodes (Collection<Tag> curlabls, Tag newlabel)
	{
		Map<String,Integer> nodecodeset = new HashMap<> (); //先找出所有相关的板块个股
		for ( Iterator<Tag> lit = curlabls.iterator(); lit.hasNext(); ) {
			InsertedTag l = (InsertedTag) lit.next();
			
			//先找出符合的
			CachedRowSetImpl result = null;
			try {
				Integer id = l.getID();
				String sql = "SELECT * FROM 产业链板块国个股板块对应表 \r\n" + 
						 " WHERE ( 板块国ID = " +  id + ")\r\n" + 
						 " GROUP BY 产业链板块国个股板块对应表.`个股板块`"
						;
				
				result = connectdb.sqlQueryStatExecute(sql);
				while (result.next()) {
				     String nodecode = result.getString("个股板块");	
				     Integer nodetype = result.getInt ("个股板块类型");
				     nodecodeset.put (nodecode, nodetype);
				}
				
				this.forcedUnattachedTagFromAllNodes (l);
				 
			}catch(java.lang.NullPointerException e){ 
					e.printStackTrace();
			} catch(Exception e){
					e.printStackTrace();
			}  finally {
					try {
						result.close();
						result = null;
						
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}
			//加上新的
			int newid = ((InsertedTag)newlabel).getID();
			for(Map.Entry<String,Integer> entry : nodecodeset.entrySet()) {
				String code = entry.getKey();
				Integer nodetype = entry.getValue();
				
				String sqlquery = "INSERT INTO 产业链板块国个股板块对应表(板块国ID,个股板块,个股板块类型,颜色) VALUES("
						+ "" + newid + ","
						+ "'" + code + "',"
						+ nodetype + ","
						+ "'" + "#"+Integer.toHexString( newlabel.getColor() .getRGB()).substring(2) + "'" 
						+ ")"
						;
				int autoIncKeyFromApi = 0;
				try {
					autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlquery);
				} catch (MysqlDataTruncation e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}

	public void forcedUnattachedTagFromAllNodes(Collection<Tag> label)
	{
		for ( Iterator<Tag> lit = label.iterator(); lit.hasNext(); ) {
	         Tag l = lit.next();
	        
	         this.forcedUnattachedTagFromAllNodes (l);
	    }
	}

	public Collection<BkChanYeLianTreeNode>  getNodesSetWithAllSpecificTags(Collection<Tag> tagnames) 
	{
//		InsertedTag checkresult = this.checkTagExistInSystem (tagname);
//		if(checkresult == null)
//			return null;
		
		Collection<BkChanYeLianTreeNode> nodecodeset = new ArrayList <> ();
		CachedRowSetImpl result = null;
		try {
			String join = Joiner.on("','").skipNulls().join(tagnames);
			
			
			String sql = "SELECT * FROM 产业链板块国个股板块对应表 \r\n" + 
					 " WHERE ( 板块国ID = " + ")\r\n" + 
					 " GROUP BY 产业链板块国个股板块对应表.`个股板块`"
					;
			
			result = connectdb.sqlQueryStatExecute(sql);
			while (result.next()) {
			     String nodecode = result.getString("个股板块");	
			     Integer nodetype = result.getInt ("个股板块类型");
			     BkChanYeLianTreeNode node = bkstk.getAllBkStocksTree().getSpecificNodeByHypyOrCode(nodecode, nodetype);
			     nodecodeset.add (node);
			}
			 
		}catch(java.lang.NullPointerException e){ 
				e.printStackTrace();
		} catch(Exception e){
				e.printStackTrace();
		}  finally {
				try {
					result.close();
					result = null;
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return nodecodeset;
	}
	public Collection<BkChanYeLianTreeNode> getNodesSetWithAllSpecificTags(String tagnames) 
	{
//		String join = Joiner.on("','").skipNulls().join(tagnames);
//		Joiner.on(" ").skipNulls().join(tagnames);
		Collection<BkChanYeLianTreeNode> nodecodeset = new ArrayList <> ();
		CachedRowSetImpl result = null;
		try {
			String newtagnames = tagnames.trim().replaceAll(" +", " ").replaceAll("\\s", "' , '");
			int count = StringUtils.countMatches(newtagnames, ",") + 1;
			String sql = " SELECT dy.`个股板块`, COUNT(*) AS count , dy.`个股板块类型`, lb.`板块国名称`  AS tagname, dy.`板块国ID` AS tagid FROM 产业链板块国个股板块对应表 AS dy\r\n" + 
					"INNER  JOIN 产业链板块国列表 AS lb\r\n" + 
					"ON lb.id = dy.`板块国ID`\r\n" + 
					" WHERE lb.`板块国名称` IN ('" + newtagnames + "')\r\n" + 
					" GROUP BY dy.`个股板块` \r\n" + 
					" HAVING count=" + count
					;
			result = connectdb.sqlQueryStatExecute(sql);
			while (result.next()) {
			     String nodecode = result.getString("个股板块");	
			     Integer nodetype = result.getInt ("个股板块类型");
			     BkChanYeLianTreeNode node = bkstk.getAllBkStocksTree().getSpecificNodeByHypyOrCode(nodecode, nodetype);
			     nodecodeset.add (node);
			}
			 
		} catch(java.lang.NullPointerException e) {e.printStackTrace();
		} catch(Exception e){e.printStackTrace();
		} finally {	try {result.close();result = null;	} catch (SQLException e) {e.printStackTrace();} 
		}
		
		return nodecodeset;
	}
	
	public Collection<BkChanYeLianTreeNode> getNodesSetWithOneOfSpecificTags (String tagnames)
	{
		Collection<BkChanYeLianTreeNode> nodecodeset = new ArrayList <> ();
		CachedRowSetImpl result = null;
		try {
//			String newtagnames = tagnames.trim().replaceAll(" +", " ").replaceAll("\\s","' OR 产业链板块国列表.`板块国名称`  = '").trim();
//			newtagnames = "产业链板块国列表.`板块国名称`  = '" + newtagnames + "'";
//			
//			String sql = "SELECT * FROM 产业链板块国个股板块对应表 \r\n" + 
//						"LEFT JOIN 产业链板块国列表\r\n" + 
//						"ON 产业链板块国列表.id = 产业链板块国个股板块对应表.`板块国ID`\r\n" + 
//						"WHERE " + newtagnames + "\r\n" + 
//						"GROUP BY 产业链板块国个股板块对应表.`个股板块` "
//						;
			String newtagnames = tagnames.trim().replaceAll(" +", " ").replaceAll("\\s", "' , '");
			String sql = "SELECT * FROM 产业链板块国个股板块对应表 \r\n" + 
						"LEFT JOIN 产业链板块国列表\r\n" + 
						"ON 产业链板块国列表.id = 产业链板块国个股板块对应表.`板块国ID`\r\n" + 
						"WHERE  产业链板块国列表.`板块国名称`  IN ('" + newtagnames + "')\r\n" + 
						"GROUP BY 产业链板块国个股板块对应表.`个股板块`"
						;
			result = connectdb.sqlQueryStatExecute(sql);
			while (result.next()) {
			     String nodecode = result.getString("个股板块");	
			     Integer nodetype = result.getInt ("个股板块类型");
			     BkChanYeLianTreeNode node = bkstk.getAllBkStocksTree().getSpecificNodeByHypyOrCode(nodecode, nodetype);
			     nodecodeset.add (node);
			}
			
		}catch(java.lang.NullPointerException e){ 
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}  finally {
			try {
				result.close();
				result = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return nodecodeset;
	}
	
}