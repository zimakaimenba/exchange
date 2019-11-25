package com.exchangeinfomanager.labelmanagement;

import java.awt.Color;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;

import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.NodeInsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.mysql.jdbc.MysqlDataTruncation;
import com.sun.rowset.CachedRowSetImpl;

public class TagsDbOperation 
{
	private ConnectDataBase connectdb;

	public TagsDbOperation() 
	{
		connectdb = ConnectDataBase.getInstance();
	}
	private InsertedTag checkTagExistInSystem (String tagname)
	{
		try {
					String sqlq = "SELECT COUNT(*) AS COUNT , id, ��������, DefaultCOLOUR, ����˵��  FROM ��ҵ�������б� WHERE ��������  =  '" + tagname + "'";
			 		CachedRowSetImpl checkresult = connectdb.sqlQueryStatExecute(sqlq);
			 		while(checkresult.next()) {
						int count = checkresult.getInt ("COUNT");
						if(count > 0) {
							Color color = Color.GRAY;
							try {
								color = Color.decode( checkresult.getString("DefaultCOLOUR")) ;
							} catch (java.lang.Exception e)  {
								
							}
							InsertedTag anewtag = new InsertedTag (new Tag (checkresult.getString("��������"), color ) , 
									 checkresult.getInt("id"));
							
							anewtag.setDescription( checkresult.getString("����˵��") );
							
							return anewtag;
						}
			 		}
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException exc) {
						exc.printStackTrace();
						return null;
		} catch (MysqlDataTruncation e) {
					e.printStackTrace();
						return null;
		} catch (SQLException e) {
						e.printStackTrace();
						return null;
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
		String sqlinsertquery = "INSERT INTO ��ҵ�������б� (��������, DefaultCOLOUR) VALUES("
								+ "'" + tagname + "',"
								+ "'" + color.toString() + "'" 
								+ ")"
								;
		int autoIncKeyFromApi = 0;
		try {
			autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertquery);
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException exc) {
			exc.printStackTrace();
			return null;
		} catch (MysqlDataTruncation e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
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
 		        	
 		        	int id = result.getInt ("id");
 		        	InsertedTag label = new InsertedTag(new Tag(bkzname,defaultColor),id );
 	                labels.add(label);
 	        }
 		} catch(java.lang.NullPointerException e) { 
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
	
	public Collection<Tag> getNodesTagsFromDataBase(Set<BkChanYeLianTreeNode> nodesets) throws SQLException 
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
					} catch (java.lang.Exception e)  {
						
					}

			     	int id = result.getInt("id");
			     	InsertedTag intag = new InsertedTag (new Tag(bkzname,defaultColor), id);
			     	
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
	public Tag attachedTagToNodes(Set<BkChanYeLianTreeNode> nodesets, Tag label)
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
			e.printStackTrace();
		} catch (MysqlDataTruncation e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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

	
	public void unattachedTagsFromNodes(Set<BkChanYeLianTreeNode> nodesets, Collection<Tag> label) 
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
		int id = ((InsertedTag)l).getID();
		String nodecode = node.getMyOwnCode();
		int nodetype = node.getType ();
		
		String sqlquery = "DELETE FROM ��ҵ���������ɰ���Ӧ�� WHERE  "
				+ " ����ID = '" + id + "'"
				+ " AND ���ɰ�� = '" + nodecode + "'" 
				+ " AND ���ɰ������ = " + nodetype 
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

	public void updateSystemTags(Tag l) 
	{
		int id = ((InsertedTag)l).getID();
		String lname = l.getName();
		Color lcolor = l.getColor();
		String color =   "#"+Integer.toHexString( lcolor.getRGB()).substring(2);
		
		try {
			String sqlupdated = "UPDATE ��ҵ�������б� SET "
								+ " �������� = '" + lname + "', " 
								+ " DefaultCOLOUR= '" + color + "' "
								+ " WHERE id = " + id
								;
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
					return  new InsertedTag (new Tag (checkresult.getString("��������"), Color.decode( checkresult.getString("DefaultCOLOUR")) ) , 
							 checkresult.getInt("id"));
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
	}

	public void forcedUnattachedTagFromAllNodes(Collection<Tag> label)
	{
		for ( Iterator<Tag> lit = label.iterator(); lit.hasNext(); ) {
	         Tag l = lit.next();
	        
	         this.forcedUnattachedTagFromAllNodes (l);
	    }
	}

	public Map<String,Integer>  getNodesSetOfSpecificTag(String tagname) 
	{
		InsertedTag checkresult = this.checkTagExistInSystem (tagname);
		if(checkresult == null)
			return null;
		
		Map<String, Integer> nodecodeset = new HashMap <> ();
		CachedRowSetImpl result = null;
		try {
			Integer id = checkresult.getID();
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