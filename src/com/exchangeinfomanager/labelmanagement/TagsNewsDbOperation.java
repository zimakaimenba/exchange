package com.exchangeinfomanager.labelmanagement;

import java.awt.Color;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.NodeInsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.mysql.jdbc.MysqlDataTruncation;
import com.sun.rowset.CachedRowSetImpl;

public class TagsNewsDbOperation 
{
	private ConnectDataBase connectdb;
	
	private TagsDbOperation tagdboptforsys;

	public TagsNewsDbOperation() 
	{
		connectdb = ConnectDataBase.getInstance();
		
		tagdboptforsys = new TagsDbOperation();
	}

	public Collection<Tag> getNodesNewsTagsFromDataBase(Set<BkChanYeLianTreeNode> nodeset) throws SQLException 
	{
        Collection<Tag> labels = new HashSet<>();
        
        for (Iterator<BkChanYeLianTreeNode> it = nodeset.iterator(); it.hasNext(); ) {
        	BkChanYeLianTreeNode f = it.next();
        	labels.addAll( this.getNodeNewsTagsFromDataBase (f.getMyOwnCode()) );
        }
        
        return labels;
	}
	
//	private InsertedTag createSystemTags (Tag newtag)
//	{
//		String tagname = newtag.getName();
//		String color;
//		try {
//			color =  "#"+Integer.toHexString(  newtag.getColor() .getRGB()).substring(2);
//		} catch ( java.lang.NullPointerException e) {
//			color = "#ffffff";
//		}
//		String sqlinsertquery = "INSERT INTO ��ҵ�������б� (��������, DefaultCOLOUR) VALUES("
//								+ "'" + tagname + "',"
//								+ "'" + color + "'" 
//								+ ")"
//								;
//		int autoIncKeyFromApi = 0;
//		try {
//			autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertquery);
//		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException exc) {
//			exc.printStackTrace();
//			return null;
//		} catch (MysqlDataTruncation e) {
//			e.printStackTrace();
//			return null;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return null;
//		}
//		
//		return new InsertedTag (newtag, autoIncKeyFromApi);
//	}
 
	
	private Collection<? extends Tag> getNodeNewsTagsFromDataBase (String nodecode) 
	{
		Collection<InsertedTag> labels = new HashSet<>();
		
		String sqlquerystat = "SELECT * FROM ��ҵ����   "
					+ " WHERE ������� like '%" + "000000" +  "%'  \r\n"
					+ " ORDER BY  ¼������ DESC"
					;

		CachedRowSetImpl result = null;
		try {
			result = connectdb.sqlQueryStatExecute(sqlquerystat);
   		 	while(result.next()) {
	   		 	int newsID = result.getInt("news_id");
	            String keywords = result.getString("�ؼ���");
	            
	            List<String> tmpkwlist = Splitter.on(" ").omitEmptyStrings().splitToList(keywords);
	            for(String tmpkwname : tmpkwlist) {
	            	if(   !tmpkwname.equals("�ؼ���")) {
	            		Tag tag = new Tag (tmpkwname, Color.decode( "#ffffff"));
	                	InsertedTag itag = new InsertedTag(tag,  newsID);
	        	        labels.add(itag);
	            	}
	            }
   		 	}
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	if(result != null)
				try {
					result.close();
					result = null;
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }
		
		
        return labels;
	}

	public void removeTagsFromNodesNewsInDataBase(Set<BkChanYeLianTreeNode> nodeset, Collection<Tag> label)
	{
		// TODO Auto-generated method stub
		
	}

	public void removeTagFromNodesNewsInDataBase(Set<BkChanYeLianTreeNode> nodeset, Tag f) 
	{
		
		
	}

	public void storeNewsKeyWordsToDataBase(InsertedMeeting m)
	{
		Collection<InsertedTag> labels = new HashSet<>();
		
		int newsid = m.getID();
		
		String kwds = m.getKeyWords();
		List<String> kwlist = Splitter.on(" ").omitEmptyStrings().splitToList(kwds);
		for(String tmpkw : kwlist) {

			Integer bkID = null;
   		 	try {
   		 		String sqlq = "SELECT COUNT(*) AS COUNT , id, �������� FROM ��ҵ�������б� where �������� = '" +tmpkw+ "'";
   		 		CachedRowSetImpl checkresult = connectdb.sqlQueryStatExecute(sqlq);
				while(checkresult.next()) {
					int count = checkresult.getInt ("COUNT");
					if(count > 0)
						bkID = checkresult.getInt("id");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(bkID != null) { //�Ѿ�����
				storeNewsKeyWordToDataBase ( new InsertedTag (new Tag(tmpkw,null),bkID) , m ) ;
				
			} else {
				InsertedTag inserttag = tagdboptforsys.createSystemTags( new Tag(tmpkw,Color.GRAY) );
				storeNewsKeyWordToDataBase (inserttag,m);
			}
		}
	}
	public void storeNewsKeyWordToDataBase (InsertedTag l, InsertedMeeting m)
	{
		int kwid = l.getID();
		int mid = m.getID();
		
		String sql = "INSERT INTO ��ҵ���������Ŷ�Ӧ��(����id, news_id) VALUES ("
				+ kwid + ","
				+ mid + ")"
				;
		int matchid = 0;
		try {
			matchid = connectdb.sqlInsertStatExecute(sql);
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}

	public void deleteKeyWordsMapsOfDeletedNews(InsertedMeeting m)
	{
		int mid = m.getID();
		String sql = "DELETE FROM ��ҵ���������Ŷ�Ӧ��"
					+ " WHERE news_id=" + mid;
					;
					try {
						connectdb.sqlDeleteStatExecute(sql);
					} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
		
		
	}

	public void combinLabelsToNewOneForNews(Collection<Tag> curlabls, InsertedTag newtag)
	{
		Set<String> nodecodeset = new HashSet<> (); //���ҳ�������صİ�����
		for ( Iterator<Tag> lit = curlabls.iterator(); lit.hasNext(); ) {
			InsertedTag l = (InsertedTag) lit.next();
			
			CachedRowSetImpl result = null;
			try {
				Integer id = l.getID();
				String sql = "SELECT * FROM ��ҵ���������Ŷ�Ӧ�� \r\n" + 
						 " WHERE ( ����ID = " +  id + ")\r\n" + 
						 " GROUP BY ��ҵ���������Ŷ�Ӧ��.`news_id`"
						;
				
				result = connectdb.sqlQueryStatExecute(sql);
				while (result.next()) {
				     String nodecode = result.getString("news_id");	
				     nodecodeset. add (nodecode);
				}
				
				String deletesql = "DELETE FROM  ��ҵ���������Ŷ�Ӧ�� "  + 
									" WHERE ( ����ID = " +  id + ")"
									;
				connectdb.sqlDeleteStatExecute(deletesql);
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
		
			int newid = ((InsertedTag)newtag).getID();
			for(String code : nodecodeset ) {

				String sqlquery = "INSERT INTO ��ҵ���������Ŷ�Ӧ��(����ID,news_id,��ɫ) VALUES("
						+ "" + newid + ","
						+ "'" + code + "',"
						+ "'" + "#"+Integer.toHexString( newtag.getColor() .getRGB()).substring(2) + "'" 
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

	public void forcedUnattachedTagFromAllNews(Collection<Tag> label)
	{
		for ( Iterator<Tag> lit = label.iterator(); lit.hasNext(); ) {
	         Tag l = lit.next();
	        
	         this.forcedUnattachedTagFromAllNodes (l);
	    }
		
	}

	private void forcedUnattachedTagFromAllNodes(Tag l) 
	{
		try {
			String sqlqueryindy = "DELETE FROM ��ҵ���������Ŷ�Ӧ��  WHERE  "
								+ " ����id = " + ((InsertedTag)l).getID() + ""
								;
			int autoIncKeyFromApi = 0;
			autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqlqueryindy);
		} catch (MysqlDataTruncation e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
//	public void putAllKeyWordsToNewsStructure ()
//	{
//		String sqlquerystat = "SELECT * FROM ��ҵ����   "
//				+ " ORDER BY  ¼������ DESC"
//				;
//		CachedRowSetImpl result = null;
//		Map<Integer, String> newtagset = new HashMap<> ();
//		try {
//			result = connectdb.sqlQueryStatExecute(sqlquerystat);
//   		 	while(result.next()) {
//	   		 	int newsID = result.getInt("news_id");
//	            String keywords = result.getString("�ؼ���").trim();
//	            if(!keywords.equals("�ؼ���"))
//	            newtagset.put(newsID, keywords);
//   		 	}
//		}catch(java.lang.NullPointerException e ){ 
//	    	e.printStackTrace();
//	    } catch (SQLException e) {
//	    	e.printStackTrace();
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    }  finally {
//	    	if(result != null)
//				try {
//					result.close();
//					result = null;
//					
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//	    }
//		kwset = new HashSet< > ();
//		for(Map.Entry<Integer, String> entry : newtagset.entrySet() ) {
//			int newsid = entry.getKey();
//			
//			String keywords = entry.getValue();
//			List<String> tmpkwlist = Splitter.on(" ").omitEmptyStrings().splitToList(keywords);
//            for(String tmpkwname : tmpkwlist) {
//            	if(  !kwset.contains(tmpkwname) && !tmpkwname.equals("�ؼ���")) {
//            		String sqlq = "SELECT * FROM ��ҵ�������б� where �������� = '" +tmpkwname+ "'";
//            		CachedRowSetImpl result2 = connectdb.sqlQueryStatExecute(sqlq);
//            		int bkID = 0;
//           		 	try {
//						while(result2.next()) {
//						 	try {
//								bkID = result2.getInt("id");
//							} catch (SQLException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch(Exception e) {
//								e.printStackTrace();
//							}
//						}
//					} catch (SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//           		 	if(bkID != 0)  { //�Ѿ�����
//	           		 	String sql = "INSERT INTO ��ҵ���������Ŷ�Ӧ��(����id, news_id) VALUES ("
//								+ bkID + ","
//								+ newsid + ")"
//								;
//						try {
//							connectdb.sqlInsertStatExecute(sql);
//						} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							continue;
//						} catch (SQLException e) {
//							e.printStackTrace();
//						}
//           		 	} else { //������
//           		 		String sql = "INSERT INTO ��ҵ�������б�(��������) VALUES('" 	+  tmpkwname + "')";
//           		 		try {
//							int kwnewid = connectdb.sqlInsertStatExecute(sql);
//							
//							String sql2 = "INSERT INTO ��ҵ���������Ŷ�Ӧ��(����id, news_id) VALUES ("
//									+ kwnewid + ","
//									+ newsid + ")"
//									;
//							connectdb.sqlInsertStatExecute(sql2);
//							
//						} catch (SQLException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//           		 	}
//            	}
//            }
//		}
//		
//	}

}
