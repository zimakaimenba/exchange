 package com.exchangeinfomanager.bankuaichanyelian;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.database.CylTreeDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.CylTreeNestedSetNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.operations.BanKuaiAndStockTree;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;
import com.mysql.jdbc.CallableStatement;
import com.sun.rowset.CachedRowSetImpl;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

class ChanYeLianXMLHandler 
{
	private ConnectDataBase connectdb;

	public ChanYeLianXMLHandler() 
	{
		this.sysconfig = SystemConfigration.getInstance();
		connectdb = ConnectDataBase.getInstance();
		
		geguchanyelianxml = sysconfig.getGeGuChanYeLianXmlFile ();
		this.bkopt = new BanKuaiDbOperation ();
		
//		CylTreeDbOperation treedb = new CylTreeDbOperation  ();
//		BanKuaiAndStockTree treecyl = treedb.getBanKuaiAndStockTree();
		
		bankuaichanyelianxml = sysconfig.getBanKuaiChanYeLianXml ();
		FileInputStream xmlfileinput = null;
		try {
			xmlfileinput = new FileInputStream(bankuaichanyelianxml);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			SAXReader reader = new SAXReader();
			cylxmldoc = reader.read(xmlfileinput);
			cylxmlroot = cylxmldoc.getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
//		try {
//			xmlfileinput = new FileInputStream(geguchanyelianxml);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			SAXReader ggcylsaxreader = new SAXReader();
//			ggcylxmldocument = ggcylsaxreader.read(xmlfileinput );
//			ggcylxmlroot = ggcylxmldocument.getRootElement();
//		} catch (DocumentException e) {
//			e.printStackTrace();
//			return;
//		 } catch (Exception e) {  
//	            e.printStackTrace();  
//	     }  
		
		initializeChanYeLianTree ();
	}
	
	private static Logger logger = Logger.getLogger(ChanYeLianXMLHandler.class);
	private String bankuaichanyelianxml;
	private String geguchanyelianxml;
	private SystemConfigration sysconfig;
	private BanKuaiDbOperation bkopt;
	
	private Document cylxmldoc;
	Element cylxmlroot ;
	private Document ggcylxmldocument;
	private Element ggcylxmlroot;
	private boolean hasXmlRevised = false;
	private boolean onlysavewhenhavechild;

	private SetView<String> oldBanKuaiFromDb = null;
//	private BkChanYeLianTree treeallstocks;
	private BanKuaiAndStockTree treecyl;
	
	private void initializeChanYeLianTree() 
	{
		CylTreeNestedSetNode alltopNode = new CylTreeNestedSetNode("000000","��������",0);
		alltopNode.setNestedId(1);
		
		//��ҵ��tree part
		if(cylxmlroot != null) {
			generateChanYeLianTreeFromXML(alltopNode,cylxmlroot); //���ɲ�ҵ����
		}
		
		 treecyl = new BanKuaiAndStockTree(alltopNode,"CYLTREE");
		
//		allstocks = null;
//		e = null;
//		System.out.println("test1");
	}
	/*
	 * 
	 */
	private void generateChanYeLianTreeFromXML(CylTreeNestedSetNode topNode, Element xmlelement) 
	{
	    	 Iterator it = xmlelement.elementIterator();
	    	 
	    	 while (it.hasNext() ) 
	    	 {
				   Element element = (Element) it.next();
			   
				   CylTreeNestedSetNode parentsleaf = null;
				   
				   String bkname = null, bkowncode = null; String suoshubkcode = null;
				   String nodetypestr = element.attributeValue("Type").trim();
				   Integer nodetype = Integer.parseInt(nodetypestr ); 
				   
				   if(BkChanYeLianTreeNode.GPC == nodetype ) { //�ǹ�Ʊ��
					   bkowncode = element.attributeValue("gpccode"); 
					   bkname = element.attributeValue("gpcname");
					   
					   parentsleaf = new GuPiaoChi(bkowncode,bkname);
					   parentsleaf.setNodeType(BkChanYeLianTreeNode.GPC);
					   
				   } else
				   if(BkChanYeLianTreeNode.SUBGPC == nodetype ) { //�ǹ�Ʊ��
					   bkowncode = element.attributeValue("subgpccode"); 
					   bkname = element.attributeValue("subgpcname");
					   
//					   String newcode = addSubGPCToDataBase (bkowncode,bkname);
					   parentsleaf = new SubGuPiaoChi(bkowncode,bkname);
					   parentsleaf.setNodeType(BkChanYeLianTreeNode.SUBGPC);
					   
				   } else
				   if(BkChanYeLianTreeNode.TDXBK == nodetype ) { //��ͨ���Ű��  
					   bkowncode = element.attributeValue("bkcode");
					   bkname = element.attributeValue("bkname");
					   suoshubkcode = bkowncode;
					 
					   parentsleaf = new CylTreeNestedSetNode(bkowncode,bkname);
					   parentsleaf.setNodeType(BkChanYeLianTreeNode.TDXBK);
				   } else
				   if(5 == nodetype ) {//���Զ����Ӱ��
					   bkname = element.attributeValue("subbkname");
					   bkowncode = element.attributeValue("subbkcode");
					   suoshubkcode = element.attributeValue("suoshubkcode"); //���нڵ㶼�����������İ��code������ʶ�������ĸ�����µĽڵ�
					    
//					   String newcode = addSubGPCToDataBase (bkowncode,bkname);
					    parentsleaf = new CylTreeNestedSetNode(bkowncode,bkname);
					    parentsleaf.setNodeType(BkChanYeLianTreeNode.SUBGPC);
					    
				   } else 
				   if(BkChanYeLianTreeNode.TDXGG == nodetype || BkChanYeLianTreeNode.BKGEGU == nodetype) {//�Ǹ���
					   bkname = element.attributeValue("geguname");
					   bkowncode = element.attributeValue("gegucode");
					   
					   parentsleaf = new CylTreeNestedSetNode(bkowncode,bkname );
					   parentsleaf.setNodeType(BkChanYeLianTreeNode.TDXGG);
				   }
				   
				  
					   if(parentsleaf != null) {
//						   addNodeToNestedDatabase (topNode,parentsleaf);
						   
						   topNode.add(parentsleaf);
						   generateChanYeLianTreeFromXML(parentsleaf,element);
					   }
					    else
						   return;
				   
				   
			}
	}
	private String addSubGPCToDataBase (String bkcode,String bkname)
	{
		
		
		String subcode = JOptionPane.showInputDialog(null,"����SUBGPC����",bkcode);
		if(subcode == null)
			return "";
		
		String description = JOptionPane.showInputDialog(null,"SUBGPC����˵��");
		if(description == null)
			description = "";
		
		String sqlinsertstat = "INSERT INTO  ��ҵ�������б�(��������,��������,����˵��) values ("
					+ "'" + subcode + "'" + ","
					+ "'" + bkname + "'"  + ","
					+ "'" + description + "'"
					+ ")"
					;
			try{
				
				    connectdb.sqlInsertStatExecute(sqlinsertstat);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
			
				}
		return subcode;

	}
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
	private void addNewTdxBanKuaiFromDbToChanYeLianTree(BkChanYeLianTreeNode topNode, SetView<String> newBanKuaiFromDb, BanKuaiAndStockTree treeallstocks) 
	{
		for(String newbkcode:newBanKuaiFromDb) {
			logger.debug("XML��Ҫ�м���" + newbkcode);
			try {
			        		BanKuai parentsleaf = (BanKuai)treeallstocks.getSpecificNodeByHypyOrCode(newbkcode,BkChanYeLianTreeNode.TDXBK);
			        		
							topNode.add(parentsleaf);
							
			} catch (java.lang.NullPointerException ex) {
							ex.printStackTrace();
			}
	    }
	}

	public  BanKuaiAndStockTree getBkChanYeLianXMLTree()
	{
		return treecyl;
	}
	
	/*
	 * 
	 */
	public boolean isXmlEditable ()
	{
		if(this.checkDataBaseStatus()) //���ݿ����ڸ��£��û���ò�Ҫ����XML��������ɲ�һ��
			return false;
		else 
			return true;
	}
	public boolean hasXmlRevised ()
	{
		return hasXmlRevised;
	}
	/*
	 * ��ȡ���ݿ��ĵ�ǰ״̬��
	 */
	private boolean checkDataBaseStatus ()
	{
//		boolean dbstatus = bkopt.getTDXRelatedTablesStatus ();
//		return dbstatus;
		return false;
	}
	/*
	 * ������ݿ���¹�����XMLӦ��Ҳ����
	 */
	private boolean isXmlShouldUpdated ()
	{
		if(this.checkDataBaseStatus()) //���ݿ��ڸ��¹����У���ʱXML�����Ը���
			return false;
		
		File tmpfl = new File (bankuaichanyelianxml);
		long cyllastmodifiedtime = tmpfl.lastModified();
		Calendar  calfilemdf =  Calendar.getInstance();
		calfilemdf.setTimeInMillis(cyllastmodifiedtime);
//		logger.debug(calfilemdf.getTime() );
		
		Date bklastmodifiedtimeindb = bkopt.getTDXRelatedTableLastModfiedTime ();
		if(bklastmodifiedtimeindb == null)
			return false;
		
		Calendar  caldbmdf =  Calendar.getInstance();
		caldbmdf.setTime(bklastmodifiedtimeindb);
		//logger.debug(caldbmdf.getTime() );
		
//		return true;//������
		if(caldbmdf.after(calfilemdf)) {
			logger.debug("���ݿ��Ѿ����£���ʼ����XML");
			return true;
		}
		else
			return false;
	}
	/*
	 * ��õ�ǰXML������ͨ���Ű��
	 */
	private  Set<String> getTDXBanKuaiSetInCylXml ()
	{
		 Set<String> bkcylxmlset = new  HashSet<String> ();
		 
		 Iterator<?> itcyl = cylxmlroot.elementIterator();
		 while (itcyl.hasNext()) 
		 {
			   Element element = (Element) itcyl.next();
			   String bkcode = element.attributeValue("bkcode");
			   
			   bkcylxmlset.add(bkcode);
		 }
		 
		 return bkcylxmlset;
	}
	/*
	 * 
	 */
	 public boolean saveTreeToChanYeLianXML (BkChanYeLianTreeNode ginkgorootnode)  
     {
			//��ҵ��XML
			Document cyldocument = DocumentFactory.getInstance().createDocument();
	        Element cylrootele = cyldocument.addElement("��������");//����ĵ���
	        //���ɲ�ҵ��XML
	        Document gegucyldocument = DocumentFactory.getInstance().createDocument();
//	        Element gegucylrootele = gegucyldocument.addElement("��������");//����ĵ��� 
	        
	        exportToDoc (ginkgorootnode,cylrootele,gegucyldocument);
	    	
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GBK");    // ָ��XML����        
			//�����ҵ��XML
			try {
				XMLWriter writer = new XMLWriter(new FileOutputStream(bankuaichanyelianxml),format); // ���ȫ��ԭʼ���ݣ������������µ�������Ҫ��XML�ļ�
				String test = cyldocument.getText();
				
					writer.write(cyldocument); //������ļ�  
					//writer.flush();
					writer.close();
			} catch (IOException e) {
					e.printStackTrace();
			}
			//������ɲ�ҵ��XML		
			try {
				XMLWriter writer = new XMLWriter(new FileOutputStream(geguchanyelianxml),format); // ���ȫ��ԭʼ���ݣ������������µ�������Ҫ��XML�ļ�  
				writer.write(gegucyldocument); //������ļ�  
				//writer.flush();
				writer.close();
				
			} catch (IOException e) {
					e.printStackTrace();
			}
			
			this.cylxmldoc = cyldocument;
			this.cylxmlroot = cylrootele;

			hasXmlRevised = true;
			return hasXmlRevised;
	    }
	 	/*
	 	 * 
	 	 */
	 	private void exportToDoc (BkChanYeLianTreeNode parentnode,Element cylparentele, Document ggcyldoc)
		{
			BkChanYeLianTreeNode treeChild;
			
			for (Enumeration<BkChanYeLianTreeNode> child = parentnode.children(); child.hasMoreElements();) {
				
	            treeChild = (BkChanYeLianTreeNode) child.nextElement();
	            
	            if(treeChild.getNodeTreeRelated().shouldBeRemovedWhenSaveXml()) //Ӧ��ɾ���Ľڵ㣬�ýڵ����µ����нڵ㶼������
	            	continue;
	            
	            String tmpbkcode = treeChild.getMyOwnCode() ;
	            int nodetype = treeChild.getType();
	            String tmpname = treeChild.getMyOwnName();
	            String status = String.valueOf(nodetype);

	            Element cylchildele = null;
		        if(Integer.parseInt(status) == BkChanYeLianTreeNode.GPC) {
		        	cylchildele = cylparentele.addElement("GuPiaoChi");
		        	cylchildele.addAttribute("gpccode", tmpbkcode  );
		            cylchildele.addAttribute("gpcname", tmpname  );
		            cylchildele.addAttribute("Type", status);
		            
		          //����GPC="����"�Ľڵ㲻���棬��������û��ʲô�ر�����
		            if(tmpname.equals("����") )  //��������������û������Ϣ�Ͳ����ˣ�������̫��û����
		            	onlysavewhenhavechild = true;
		            else
		            	onlysavewhenhavechild = false;
		        } else
		        if(Integer.parseInt(status) == BkChanYeLianTreeNode.SUBGPC) {
		        	cylchildele = cylparentele.addElement("SubGuPiaoChi");
		        	cylchildele.addAttribute("subgpccode", tmpbkcode  );
		            cylchildele.addAttribute("subgpcname", tmpname  );
		            cylchildele.addAttribute("Type", status);
		        } else
		        if(Integer.parseInt(status) == BkChanYeLianTreeNode.TDXBK) {
		        	if(treeChild.getChildCount() == 0 && onlysavewhenhavechild)
		        		continue;
		        	
		        	cylchildele = cylparentele.addElement("BanKuai");
		        	cylchildele.addAttribute("bkcode", tmpbkcode  );
		            cylchildele.addAttribute("bkname", tmpname  );
		            cylchildele.addAttribute("Type", status);
		        } else 
		        if(Integer.parseInt(status) == 5) {
		        	cylchildele = cylparentele.addElement("SubBanKuai");
		        	cylchildele.addAttribute("subbkcode", tmpbkcode  );
		        	cylchildele.addAttribute("subbkname", tmpname);
		        	String suoshubkcode = treeChild.getNodeTreeRelated().getChanYeLianSuoShuTdxBanKuaiName();
		            cylchildele.addAttribute("suoshubkcode", suoshubkcode  ); 
		            cylchildele.addAttribute("Type", status);
		            
		        } else 
		        if(Integer.parseInt(status) == BkChanYeLianTreeNode.TDXGG || Integer.parseInt(status) == BkChanYeLianTreeNode.BKGEGU) {
	            	cylchildele = cylparentele.addElement("gegu");
	            	cylchildele.addAttribute("gegucode", tmpbkcode  );
		        	cylchildele.addAttribute("geguname", tmpname  );
//		        	String suoshubkcode = treeChild.getNodetreerelated().getChanYeLianSuoShuTdxBanKuaiName();
//		            cylchildele.addAttribute("suoshubkcode", suoshubkcode  ); 
		            cylchildele.addAttribute("Type", status);
	            	
		        	  //����Ǹ��ɣ�Ҫ���浽���ɲ�ҵ��XML��  	
//		            	TreeNode[] path = treeChild.getPath();
//		            	String chanyelianstring = "";
//		            	String curbankuai = ((BkChanYeLianTreeNode)path[1]).getMyOwnName(); //�������
//		            
//		            	
//		            	for(int i=1;i<path.length-1;i++) //�Ѳ�ҵ�����ϳ�һ���ַ���
//		            		chanyelianstring = chanyelianstring + ((BkChanYeLianTreeNode)path[i]).getMyOwnName() + "-";
//		            	chanyelianstring = chanyelianstring.substring(0, chanyelianstring.length()-1);
//		            	
//		            	String xpath = ".//gegu[@stockcode=\"" + tmpbkcode + "\"]";
//		            	 Node tmpnode = ggcyldoc.selectSingleNode(xpath);
//		            	 if(tmpnode != null) { //�Ѿ����ڵ�
//		            		 Element tmpitemele = ((Element)tmpnode).addElement("chanyelian");
//		            		 tmpitemele.addAttribute("bankuai", curbankuai);
//		            		 tmpitemele.addAttribute("suoshubkcode", tmpbkcode);
//		            		 tmpitemele.setText(chanyelianstring);
//		            		 
//		            	 } else {
//				             Element tmpggcylele = ggcyldoc.getRootElement().addElement("gegu" );
//			    			 tmpggcylele.addAttribute("stockcode", tmpbkcode  );
//			    			 tmpggcylele.addAttribute("stockname", tmpname  );
//			    			 Element tmpnewitem = tmpggcylele.addElement("chanyelian");
//			    			 tmpnewitem.addAttribute("bankuai", curbankuai);
//			    			 tmpnewitem.addAttribute("suoshubkcode", tmpbkcode);
//			    			 tmpnewitem.setText(chanyelianstring);
//		            	 }

		         }
		          
		         exportToDoc(treeChild,cylchildele,ggcyldoc);
	        }
		}
		/*
		 * 
		 */
		public void openChanYeLianXmlInWinSystem ()
		{
			String gegucylxmlfilepath = sysconfig.getBanKuaiChanYeLianXml ();
			try {
				String cmd = "rundll32 url.dll,FileProtocolHandler " + gegucylxmlfilepath;
				Process p  = Runtime.getRuntime().exec(cmd);
				p.waitFor();
			} catch (Exception e1) 
			{
				e1.printStackTrace();
			}
		}
		/*
		 * 
		 */
		public Stock getStockChanYeLianInfo (Stock stockbasicinfo2) 
		{
			String stockcode = stockbasicinfo2.getMyOwnCode().trim();
			String xpath = ".//gegu[@stockcode=\"" + stockcode + "\"]";  
			ArrayList<String> ggcyl = new ArrayList<String> ();
			try {
				Node tmpnode = ggcylxmldocument.selectSingleNode(xpath);
			
				Iterator eleIt = ((Element)tmpnode).elementIterator();
				
				while (eleIt.hasNext()) 
				{
					   Element e = (Element) eleIt.next();
					   //logger.debug(e.getName() + ": " + e.getText());
					   ggcyl.add(e.getText());
				 }
			 }catch (java.lang.NullPointerException e) {
				 stockbasicinfo2.setGeGuAllChanYeLianInfo(ggcyl);
			 }
			stockbasicinfo2.setGeGuAllChanYeLianInfo(ggcyl);
			return stockbasicinfo2;
		}
		
		public ArrayList<String> getGeGuChanYeLian(String stockcode)
		{
			ArrayList<String> gegucyllist = new ArrayList<String> ();
			try {
				String xpath = ".//gegu[@stockcode=\"" + stockcode + "\"]";
				@SuppressWarnings("unchecked")
				List<Element> tmpnodegg = ggcylxmldocument.selectNodes(xpath);
				for(Element singlenode:tmpnodegg) {
					 Iterator it = singlenode.elementIterator();
			    	 while (it.hasNext() ) 
			    	 {
			    		Element element = (Element) it.next();
					    String chanyelian = element.getText().trim();
//					    logger.debug(chanyelian);
					    gegucyllist.add(chanyelian); 
			    	 }
					
				}
					
			} catch (java.lang.NullPointerException ex) {
			}
			
			return gegucyllist;
		}
		
		/*
		 * �����ݿ��еİ����Ϣ���浽XML��, ���µ�BkChanYeLianTreeNode�洢
		 */
//		private void updateBkCylXmlFileAfterDbChanged2 ()
//		{
//			
////			HashMap<String, BkChanYeLianTreeNode> tdxbk = bkopt.getTDXBanKuaiList2 ();
//			HashMap<String, BanKuai> tdxbk = bkopt.getTDXBanKuaiList ("all");
//			
//			Set<String> uniontdxbkindb = tdxbk.keySet();
//			
////			SetView<String> uniontdxbkindb = Sets.union(tdxbkset, tdxzhishuset );
//			 
//			Set<String> bkcylxmlset = this.getTDXBanKuaiSetInCylXml ();
//			
//			//XMLû�У����ݿ��У�Ҫ�����XML
//			SetView<String> differencebankuainew = Sets.difference(uniontdxbkindb, bkcylxmlset );
//	        for (String newbkcode : differencebankuainew) {
//	        	logger.debug("XML��Ҫ�м���" + newbkcode);
//				//��XML �м����µİ�� //<BanKuai bkname="���ٹ�·" Status="4">
////				String xpathnew = ".//BanKuai[@bkname=\"" + newbk + "\"]";
////				Node tmpnode = cylxmldoc.selectSingleNode(xpathnew);
//					Element newele = cylxmlroot.addElement("BanKuai"); //��XML �м����µİ��
//					String bkname = null;
//					try {
//						bkname = tdxbk.get(newbkcode).getUserObject().toString();
//					} catch (java.lang.NullPointerException ex) {
////						bkname = tdxzhishu.get(newbkcode).getUserObject().toString();
//					}
//					newele.addAttribute("bkname", bkname);
//					newele.addAttribute("bkcode", newbkcode);
//					newele.addAttribute("Type", String.valueOf(BkChanYeLianTreeNode.TDXBK) );
//					logger.debug("XML�м���" + newbkcode + bkname);
//	        }
//	        
//	        //XML�����У����ݿ���û���ˣ�Ӧ��ɾ����Ҫ�Ӽ���XML��ɾ��
//	        SetView<String> differencebankuaiold = Sets.difference(bkcylxmlset, uniontdxbkindb );
//		    for (String oldbkcode : differencebankuaiold) {
//				//�Ӳ�ҵ��XML��ɾ���ð��
//				try {
//					String xpath = ".//BanKuai[@bkcode=\"" + oldbkcode + "\"]";
//					Node tmpnode = cylxmldoc.selectSingleNode(xpath);
//					cylxmlroot.remove(tmpnode);
//					logger.debug("��ҵ��XML��ɾ��" + oldbkcode);
//				} catch (java.lang.NullPointerException ex) {
//					
//				}
//				
//				//�Ӹ��ɲ�ҵ��XML��ɾ���ð�� //  <bankuai bkname="���ܴ���">���ܴ���</bankuai>
//				try {
//					String xpath = ".//gegu/chanyelian[@bkcode=\"" + oldbkcode + "\"]";
//					@SuppressWarnings("unchecked")
//					List<Node> tmpnodegg = ggcylxmldocument.selectNodes(xpath);
//					for(Node singlenode:tmpnodegg)
//						ggcylxmlroot.remove(singlenode);
//					//xmldoc.remove(tmpnode);
//					logger.debug("���ɲ�ҵ��XML��ɾ��" + oldbkcode);
//				} catch (java.lang.NullPointerException ex) {
//				}
//				
//				
//		    }
//		    
//		    //����û�б仯�İ�飬Ҫ�����������Ƿ��иı�
//		    SetView<String> intersectionbankuai = Sets.intersection(uniontdxbkindb, bkcylxmlset );
//		    for(String intsbkcode : intersectionbankuai) {
//		    	try {
//					String xpath = ".//BanKuai[@bkcode=\"" + intsbkcode + "\"]";
//					Element tmpnode = (Element)cylxmldoc.selectSingleNode(xpath);
//					String curnameinxml = tmpnode.attribute("bkname").getText();
//					
//					String bknameindb = null;
//					try {
//						bknameindb = tdxbk.get(intsbkcode).getUserObject().toString();
//					} catch (java.lang.NullPointerException ex) {
////						bknameindb = tdxzhishu.get(intsbkcode).getUserObject().toString();
//					}
//					
//					if(!bknameindb.equals(curnameinxml ) ) 
//						tmpnode.attribute("bkname").setText(bknameindb);
//					
//				} catch (java.lang.NullPointerException ex) {
//					
//				}
//		    	
//		    }
//		    
//		    
//		    OutputFormat format = OutputFormat.createPrettyPrint();
//			format.setEncoding("GBK");    // ָ��XML����        
//			//�����ҵ��XML
//			try {
//				XMLWriter writer = new XMLWriter(new FileOutputStream(bankuaichanyelianxml),format); // ���ȫ��ԭʼ���ݣ������������µ�������Ҫ��XML�ļ�  
//					writer.write(cylxmldoc); //������ļ�  
//					//writer.flush();
//					writer.close();
//			} catch (IOException e) {
//					e.printStackTrace();
//			}
//			//������ɲ�ҵ��XML		
//			try {
//				XMLWriter writer = new XMLWriter(new FileOutputStream(geguchanyelianxml),format); // ���ȫ��ԭʼ���ݣ������������µ�������Ҫ��XML�ļ�  
//				writer.write(ggcylxmldocument); //������ļ�  
//				//writer.flush();
//				writer.close();
//				
//			} catch (IOException e) {
//					e.printStackTrace();
//			}
//			
//			hasXmlRevised = true;
//		}


}
