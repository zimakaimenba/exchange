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
					String sqlq = "SELECT COUNT(*) AS COUNT , id, ��������, DefaultCOLOUR, ����˵��  FROM ��ҵ�������б� WHERE ��������  =  '" + tagname + "'";
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
								desc = checkresult.getString("��������").trim();
							} catch (java.lang.Exception e)  {
								desc = "";
							}
							Tag newtag = new Tag (checkresult.getString("��������"), color, desc ) ;
							InsertedTag anewtag = new InsertedTag (newtag, checkresult.getInt("id"));
							
							anewtag.setDescription( checkresult.getString("����˵��") );
							
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
		//�ȼ��ϵͳ������û�У���ֱ�ӷ���
		InsertedTag checktag = this.checkTagExistInSystem (tagname);
		if(checktag != null)
			return checktag;
		
		String color =  "#"+Integer.toHexString(  newtag.getColor() .getRGB()).substring(2);
		String desc = newtag.getDescription();
		
		String sqlinsertquery = "INSERT INTO ��ҵ�������б� (��������, DefaultCOLOUR,����˵��) VALUES("
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
		
		String sqlquerystat = "SELECT * FROM ��ҵ�������б�";
		CachedRowSetImpl result = null;
 		try {
 			result = connectdb.sqlQueryStatExecute(sqlquerystat);
 			while (result.next()) {
		        	String bkzname = result.getString("��������");
 		        	String tagcolor = result.getString("DefaultCOLOUR") ;
 		        	Color defaultColor = Color.WHITE;
 		        	try {
 		        		defaultColor = Color.decode( tagcolor);
 		        	} catch (java.lang.Exception e) {
 		        	}
 		        	String desc;
					try {
						desc = result.getString("����˵��").trim();
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
		
		String	sqlquerystat = "SELECT * FROM ��ҵ�������б� \r\n" + 
		 				" JOIN ��ҵ���������ɰ���Ӧ�� \r\n" + 
		 				"  ON ��ҵ�������б�.id = ��ҵ���������ɰ���Ӧ��.`����ID`\r\n" + 
		 				"   WHERE ��ҵ���������ɰ���Ӧ��.`���ɰ��` = '" + nodecode + "'"
		 				+ " AND ��ҵ���������ɰ���Ӧ��.`���ɰ������` = " + nodetype
		 				;
		
		CachedRowSetImpl result = null;
		try {
			result = connectdb.sqlQueryStatExecute(sqlquerystat);
			
		   while (result.next()) {
		     		String bkzname = result.getString("��������");
			     	Color defaultColor = Color.WHITE;
					try {
						defaultColor = Color.decode( result.getString("DefaultCOLOUR")  ) ;
					} catch (java.lang.Exception e)  {}
					String desc;
					try {
						desc = result.getString("����˵��").trim();
					} catch (java.lang.Exception e)  {
						desc = "";
					}
			     	int id = result.getInt("id");
			     	Tag newtag = new Tag(bkzname,defaultColor,desc);
			     	InsertedTag intag = new InsertedTag (newtag, id);
			     	
			     	int matchid = result.getInt("matchid");
			     	String gegubk = result.getString("���ɰ��");
			     	Color color = Color.WHITE;
			     	try {
			     		color=  Color.decode( result.getString("��ɫ") );
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
		
		String sqlquery = "INSERT INTO ��ҵ���������ɰ���Ӧ��(����ID,���ɰ��,���ɰ������,��ɫ) VALUES("
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
			logger.info ("����ʧ�ܣ������Ǽ�¼�Ѿ����ڣ����飡");

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
 			String sqlqueryinsys = "DELETE FROM ��ҵ�������б�  WHERE  "
	 				+ " �������� = '" + lname + "'"
	 				;
	 		int autoIncKeyFromApi = 0;
 			autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqlqueryinsys);
 		} catch (MysqlDataTruncation e) {
 			e.printStackTrace();
 		} catch (SQLException e) {
 			e.printStackTrace();
 		}
 		//���Բ�����������������serviceɾ��������������������
//		try {
//			String sqlqueryindy = "DELETE FROM ��ҵ���������ɰ���Ӧ�� WHERE  "
//					+ " ����id = " + tagid + ""
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
//			String sqlqueryindy = "DELETE FROM ��ҵ���������Ŷ�Ӧ��  WHERE  "
//					+ " ����id = " + tagid + ""
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
	 * ����ϵͳ�ؼ��ֱ�ɾ��ʱ�����б�Ҫǿ��ɾ��
	 */
	public void forcedUnattachedTagFromAllNodes (Tag l)
	{
		int id = ((InsertedTag)l).getID();
		
		String sqlquery = "DELETE FROM ��ҵ���������ɰ���Ӧ�� WHERE  "
				+ " ����ID = '" + id + "'"
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
			sqlquery = "DELETE FROM ��ҵ���������ɰ���Ӧ�� WHERE  "
					+ " ����ID = '" + id + "'"
					+ " AND ���ɰ�� = '" + nodecode + "'" 
					+ " AND ���ɰ������ = " + nodetype 
					;
		} else {
			sqlquery = "DELETE FROM ��ҵ���������ɰ���Ӧ��   "
					+ " WHERE  ( ����ID  IN ("
					+ " SELECT ��ҵ�������б�.id FROM ��ҵ�������б�"
					+ " WHERE  ��ҵ�������б�.�������� = '" + l.getName()  + "' ) )"
					+ " AND ���ɰ�� = '" + nodecode + "'" 
					+ " AND ���ɰ������ = " + nodetype 
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
		desc.replaceAll("��", ". ");
		
		String sqlupdated;
		if( l  instanceof NodeInsertedTag) { //NodeInsertedTag�Ͳ���ϵͳ����ɫ)
			 sqlupdated = "UPDATE ��ҵ�������б� SET "
					+ "  �������� = '" + lname + "', " 
					+ " ����˵�� = '" + desc + "' "
					+ "  WHERE id = " + id
					;
		} else {
			sqlupdated = "UPDATE ��ҵ�������б� SET "
					+ " �������� = '" + lname + "', " 
					+ " DefaultCOLOUR= '" + color + "', "
					+ " ����˵�� = '" + desc + "'"
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
			String sql = "UPDATE ��ҵ���������ɰ���Ӧ��  SET \r\n" +
					"  ��ɫ = '" +  color + "'\r\n" + 
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
		//1.��ϵͳtag��ɾ��ԭ���ģ����һ���µġ�
		this.deleteSystemTags (curlabls);
		
		//���ϵͳ��������û���Ѿ�����
		InsertedTag already = null;
		try {
		 		String sqlq = "SELECT COUNT(*) AS COUNT , id, �������� FROM ��ҵ�������б� where �������� = ' " + newlabel.getName() + "'";
		 		CachedRowSetImpl checkresult = connectdb.sqlQueryStatExecute(sqlq);
		 		while(checkresult.next()) {
				int count = checkresult.getInt ("COUNT");
				if(count > 0) {
					String desc;
					try {
						desc = checkresult.getString("��������").trim();
					} catch (java.lang.Exception e)  {
						desc = "";
					}
					String name = checkresult.getString("��������");
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
		Map<String,Integer> nodecodeset = new HashMap<> (); //���ҳ�������صİ�����
		for ( Iterator<Tag> lit = curlabls.iterator(); lit.hasNext(); ) {
			InsertedTag l = (InsertedTag) lit.next();
			
			//���ҳ����ϵ�
			CachedRowSetImpl result = null;
			try {
				Integer id = l.getID();
				String sql = "SELECT * FROM ��ҵ���������ɰ���Ӧ�� \r\n" + 
						 " WHERE ( ����ID = " +  id + ")\r\n" + 
						 " GROUP BY ��ҵ���������ɰ���Ӧ��.`���ɰ��`"
						;
				
				result = connectdb.sqlQueryStatExecute(sql);
				while (result.next()) {
				     String nodecode = result.getString("���ɰ��");	
				     Integer nodetype = result.getInt ("���ɰ������");
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
			//�����µ�
			int newid = ((InsertedTag)newlabel).getID();
			for(Map.Entry<String,Integer> entry : nodecodeset.entrySet()) {
				String code = entry.getKey();
				Integer nodetype = entry.getValue();
				
				String sqlquery = "INSERT INTO ��ҵ���������ɰ���Ӧ��(����ID,���ɰ��,���ɰ������,��ɫ) VALUES("
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
			
			
			String sql = "SELECT * FROM ��ҵ���������ɰ���Ӧ�� \r\n" + 
					 " WHERE ( ����ID = " + ")\r\n" + 
					 " GROUP BY ��ҵ���������ɰ���Ӧ��.`���ɰ��`"
					;
			
			result = connectdb.sqlQueryStatExecute(sql);
			while (result.next()) {
			     String nodecode = result.getString("���ɰ��");	
			     Integer nodetype = result.getInt ("���ɰ������");
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
			String sql = " SELECT dy.`���ɰ��`, COUNT(*) AS count , dy.`���ɰ������`, lb.`��������`  AS tagname, dy.`����ID` AS tagid FROM ��ҵ���������ɰ���Ӧ�� AS dy\r\n" + 
					"INNER  JOIN ��ҵ�������б� AS lb\r\n" + 
					"ON lb.id = dy.`����ID`\r\n" + 
					" WHERE lb.`��������` IN ('" + newtagnames + "')\r\n" + 
					" GROUP BY dy.`���ɰ��` \r\n" + 
					" HAVING count=" + count
					;
			result = connectdb.sqlQueryStatExecute(sql);
			while (result.next()) {
			     String nodecode = result.getString("���ɰ��");	
			     Integer nodetype = result.getInt ("���ɰ������");
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
//			String newtagnames = tagnames.trim().replaceAll(" +", " ").replaceAll("\\s","' OR ��ҵ�������б�.`��������`  = '").trim();
//			newtagnames = "��ҵ�������б�.`��������`  = '" + newtagnames + "'";
//			
//			String sql = "SELECT * FROM ��ҵ���������ɰ���Ӧ�� \r\n" + 
//						"LEFT JOIN ��ҵ�������б�\r\n" + 
//						"ON ��ҵ�������б�.id = ��ҵ���������ɰ���Ӧ��.`����ID`\r\n" + 
//						"WHERE " + newtagnames + "\r\n" + 
//						"GROUP BY ��ҵ���������ɰ���Ӧ��.`���ɰ��` "
//						;
			String newtagnames = tagnames.trim().replaceAll(" +", " ").replaceAll("\\s", "' , '");
			String sql = "SELECT * FROM ��ҵ���������ɰ���Ӧ�� \r\n" + 
						"LEFT JOIN ��ҵ�������б�\r\n" + 
						"ON ��ҵ�������б�.id = ��ҵ���������ɰ���Ӧ��.`����ID`\r\n" + 
						"WHERE  ��ҵ�������б�.`��������`  IN ('" + newtagnames + "')\r\n" + 
						"GROUP BY ��ҵ���������ɰ���Ӧ��.`���ɰ��`"
						;
			result = connectdb.sqlQueryStatExecute(sql);
			while (result.next()) {
			     String nodecode = result.getString("���ɰ��");	
			     Integer nodetype = result.getInt ("���ɰ������");
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