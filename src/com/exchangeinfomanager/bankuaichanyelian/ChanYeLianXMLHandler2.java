 package com.exchangeinfomanager.bankuaichanyelian;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import java.util.Set;

import javax.swing.tree.TreeNode;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.SubBanKuai;
import com.exchangeinfomanager.checkboxtree.CheckBoxTreeNode;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

class ChanYeLianXMLHandler 
{
	public ChanYeLianXMLHandler() 
	{
		initialzieSysconf ();
		bankuaichanyelianxml = sysconfig.getBanKuaiChanYeLianXml ();
		geguchanyelianxml = sysconfig.getGeGuChanYeLianXmlFile ();
		this.bkopt = new BanKuaiDbOperation ();
		
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
		
		try {
			xmlfileinput = new FileInputStream(geguchanyelianxml);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			SAXReader ggcylsaxreader = new SAXReader();
			ggcylxmldocument = ggcylsaxreader.read(xmlfileinput );
			ggcylxmlroot = ggcylxmldocument.getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
			return;
		 } catch (Exception e) {  
	            e.printStackTrace();  
	     }  
	}
	
	private String bankuaichanyelianxml;
	private String geguchanyelianxml;
	private SystemConfigration sysconfig;
	private BanKuaiDbOperation bkopt;
	
	private Document cylxmldoc;
	Element cylxmlroot = null;
	private Document ggcylxmldocument;
	private Element ggcylxmlroot;
	private boolean hasXmlRevised = false;
	
//	HashMap<String, BanKuai> tdxbk = null ;
//	HashMap<String, BanKuai> tdxzhishu = null ;
	private SetView<String> oldBanKuaiFromDb = null;

	
	private void initialzieSysconf ()
	{
		sysconfig = SystemConfigration.getInstance();
	}
	public  BkChanYeLianTreeNode getBkChanYeLianXMLTree()
	{
		 SetView<String> newBanKuaiFromDb = null;
//		 tdxbk = bkopt.getTDXBanKuaiList ();
//		 tdxzhishu = bkopt.getTDXAllZhiShuList ();
		 HashMap<String, BanKuai> allbkandzs = bkopt.getTDXAllZhiShuAndBanKuai ();
		
		if(this.isXmlShouldUpdated() ) {
			hasXmlRevised = true;
			
//			Set<String> tdxbkset = tdxbk.keySet();
//			Set<String> tdxzhishuset = tdxzhishu.keySet();
//			SetView<String> uniontdxbkindb = Sets.union(tdxbkset, tdxzhishuset );
			
			Set<String> uniontdxbkindb = allbkandzs.keySet();

			Set<String> bkcylxmlset = this.getTDXBanKuaiSetInCylXml ();

			if(uniontdxbkindb.size() != 0) { //个股不可能完全没有板块信息，如果为0，说明和数据库连接断了，要考虑
				//XML没有，数据库有，要添加如XML
				newBanKuaiFromDb = Sets.difference(uniontdxbkindb, bkcylxmlset );
				//XML 
				oldBanKuaiFromDb = Sets.difference(bkcylxmlset, uniontdxbkindb );
			} else { //个股不可能完全没有板块信息，如果为0，说明和数据库连接断了，要考虑
				
			}
		}
		
		BkChanYeLianTreeNode topNode = new BkChanYeLianTreeNode("TongDaXinBanKuaiAndZhiShu","000000");
		if(cylxmlroot != null) {
			generateChanYeLianTreeFromXML(topNode,cylxmlroot,allbkandzs); //生成产业链树
			
			if(newBanKuaiFromDb!= null && newBanKuaiFromDb.size()>0) {//把新的加进来
//				addNewTdxBanKuaiFromDbToChanYeLianTree (topNode,newBanKuaiFromDb,tdxbk,tdxzhishu);
				addNewTdxBanKuaiFromDbToChanYeLianTree (topNode,newBanKuaiFromDb,allbkandzs);
			}				
		} else {
			System.out.println(bankuaichanyelianxml+ "存在错误");
		}
		return topNode;
	}
	
	/*
	 * 
	 */
	public boolean isXmlEditable ()
	{
		if(this.checkDataBaseStatus()) //数据库正在更新，用户最好不要更改XML，以免造成不一致
			return false;
		else 
			return true;
	}
	public boolean hasXmlRevised ()
	{
		return hasXmlRevised;
	}
	/*
	 * 获取数据库表的当前状态，
	 */
	private boolean checkDataBaseStatus ()
	{
//		boolean dbstatus = bkopt.getTDXRelatedTablesStatus ();
//		return dbstatus;
		return false;
	}
	/*
	 * 如果数据库更新过，则XML应该也更新
	 */
	private boolean isXmlShouldUpdated ()
	{
		if(this.checkDataBaseStatus()) //数据库在更新过程中，暂时XML不可以更新
			return false;
		
		File tmpfl = new File (bankuaichanyelianxml);
		long cyllastmodifiedtime = tmpfl.lastModified();
		Calendar  calfilemdf =  Calendar.getInstance();
		calfilemdf.setTimeInMillis(cyllastmodifiedtime);
		System.out.println(calfilemdf.getTime() );
		
		Date bklastmodifiedtimeindb = bkopt.getTDXRelatedTableLastModfiedTime ();
		if(bklastmodifiedtimeindb == null)
			return false;
		
		Calendar  caldbmdf =  Calendar.getInstance();
		caldbmdf.setTime(bklastmodifiedtimeindb);
		//System.out.println(caldbmdf.getTime() );
		
//		return true;//测试用
		if(caldbmdf.after(calfilemdf)) {
			System.out.println("数据库已经更新，开始更新XML");
			return true;
		}
		else
			return false;
	}
	/*
	 * 获得当前XML里所有通达信板块
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
	
		
	
	private void addNewTdxBanKuaiFromDbToChanYeLianTree(BkChanYeLianTreeNode topNode, SetView<String> newBanKuaiFromDb, HashMap<String, BanKuai> tdxbk) 
	{
		for(String newbkcode:newBanKuaiFromDb) {
		        	System.out.println("XML将要中加入" + newbkcode);
	
		        	String bkname = null;
					try {
						bkname = tdxbk.get(newbkcode).getUserObject().toString();
					} catch (java.lang.NullPointerException ex) {
						ex.printStackTrace();
					}
					BanKuai parentsleaf  = new BanKuai( newbkcode,bkname);

					topNode.add(parentsleaf);
	        }
	}
	 private void generateChanYeLianTreeFromXML(BkChanYeLianTreeNode topNode, Element xmlelement, HashMap<String,BanKuai> allbkandzs ) 
	 {//SetView<String> newBanKuaiFromDb, SetView<String> oldBanKuaiFromDb
	    	 Iterator it = xmlelement.elementIterator();
	    	 
	    	 while (it.hasNext() ) 
	    	 {
	    		   boolean shouldremovedwhensavexml = false;
				   Element element = (Element) it.next();
			   
				   BkChanYeLianTreeNode parentsleaf = null;
				   
				   String bkname = null, bkowncode = null; String suoshubkcode = null;
				   String nodetype = element.attributeValue("Type").trim();
				   if(nodetype.equals("4") ) { //是通达信板块  
					   
					   bkowncode = element.attributeValue("bkcode");
					   suoshubkcode = bkowncode;

//					   System.out.println("通达信代码：" + bkowncode);
					   if(oldBanKuaiFromDb != null && oldBanKuaiFromDb.size()>0 && oldBanKuaiFromDb.contains(bkowncode) ) { //如果代码已经属于需要删除的，则标记好
						   shouldremovedwhensavexml = true;
						   bkname = element.attributeValue("bkname");
					   } else { //板块名字可能会变，所有直接用数据库里的名字，如果allbkandzs为空，说明和数据库断了，也还是用XML本身的名字
						   try{
							   bkname = allbkandzs.get(bkowncode).getMyOwnName();
						   } catch (java.lang.NullPointerException ex) {
//							   ex.printStackTrace(); 
							   bkname = element.attributeValue("bkname");
						   }
					   }
					   
					   parentsleaf = new BanKuai ( bkowncode,bkname);
					   
					   
				   } else if(nodetype.equals("5") ) {//是自定义子板块
					   bkname = element.attributeValue("subbkname");
					   bkowncode = element.attributeValue("subbkcode");
					   suoshubkcode = element.attributeValue("suoshubkcode"); //所有节点都保存所属板块的板块code，便于识别是在哪个板块下的节点
					    
					    parentsleaf = new SubBanKuai(bkowncode,bkname);
//					    parentsleaf.setSuoSuTdxBanKuai(suoshubkcode);
					   
				   } else if(nodetype.equals("6") ) {//是个股
					   bkname = element.attributeValue("geguname");
					   bkowncode = element.attributeValue("gegucode");
					   Stock tmpparentsleaf = new Stock (bkowncode,bkname);
					   tmpparentsleaf = bkopt.getTDXBanKuaiForAStock (tmpparentsleaf); //通达信板块信息

					   suoshubkcode = element.attributeValue("suoshubkcode"); //所有节点都保存所属板块的板块code，便于识别是在哪个板块下的节点
					   if( !tmpparentsleaf.isInTdxBanKuai(suoshubkcode))//如果该股已经不属于该板块，需要删除的，则标记好
						   shouldremovedwhensavexml = true;
					   else  //个股不被删除，那因为股票名字可能会改，所以更新一下股票名字
						   tmpparentsleaf = bkopt.getStockBasicInfo(tmpparentsleaf);

					   parentsleaf = tmpparentsleaf;
				   }

				   if(shouldremovedwhensavexml) //如果代码已经属于需要删除的，则标记好
					   parentsleaf.setShouldBeRemovedWhenSaveXml ();
				   
				   try {
					   String addedTime =  element.attributeValue("addedTime"); //板块或个股加入到产业链树的时间
					   parentsleaf.setAddedToCylTreeTime(addedTime);
					   
				   } catch (java.lang.NullPointerException ex) {
					   
				   }

				   topNode.add(parentsleaf);

				   generateChanYeLianTreeFromXML(parentsleaf,element,allbkandzs);
			}
	}

	 
	 public boolean saveTreeToChanYeLianXML (BkChanYeLianTreeNode ginkgorootnode)  
     {
			//产业链XML
			Document cyldocument = DocumentFactory.getInstance().createDocument();
	        Element cylrootele = cyldocument.addElement(ginkgorootnode.getUserObject().toString());//添加文档根
	        //个股产业链XML
	        Document gegucyldocument = DocumentFactory.getInstance().createDocument();
	        Element gegucylrootele = gegucyldocument.addElement("geguchanyelian");//添加文档根 
	        
	        exportToDoc (ginkgorootnode,cylrootele,gegucyldocument);
	    	
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GBK");    // 指定XML编码        
			//保存产业链XML
			try {
				XMLWriter writer = new XMLWriter(new FileOutputStream(bankuaichanyelianxml),format); // 输出全部原始数据，并用它生成新的我们需要的XML文件  
					writer.write(cyldocument); //输出到文件  
					//writer.flush();
					writer.close();
			} catch (IOException e) {
					e.printStackTrace();
			}
			//保存个股产业链XML		
			try {
				XMLWriter writer = new XMLWriter(new FileOutputStream(geguchanyelianxml),format); // 输出全部原始数据，并用它生成新的我们需要的XML文件  
				writer.write(gegucyldocument); //输出到文件  
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
	 	private void exportToDoc (BkChanYeLianTreeNode parentnode,Element cylparentele, Document ggcyldoc)
		{
			BkChanYeLianTreeNode treeChild;
			
			for (Enumeration<BkChanYeLianTreeNode> child = parentnode.children(); child.hasMoreElements();) {
				
	            treeChild = (BkChanYeLianTreeNode) child.nextElement();
	            
	            if(treeChild.shouldBeRemovedWhenSaveXml()) //应该删除的节点，该节点以下的所有节点都不保存
	            	continue;
	            
	            String tmpbkcode = treeChild.getMyOwnCode() ;
	            int nodetype = treeChild.getType();
	            String tmpname = treeChild.getMyOwnName();
//	            if(nodetype == BkChanYeLianTreeNode.BKGEGU ) {
//	            	ASingleStockInfo tmpsinglestock = new ASingleStockInfo (tmpbkcode); 
//	            	tmpsinglestock = stockopt.getSingleStockBasicInfo(tmpsinglestock);
//	            	tmpname = tmpsinglestock.getStockname();
//	            } else if(nodetype == BkChanYeLianTreeNode.TDXBK ) {
//	            	tmpname = bkopt.getTdxBanKuaiNameByCode (tmpbkcode);
//	            } else {
//	            	tmpname = treeChild.getUserObject().toString();
//	            }

		        String status = String.valueOf(nodetype);
		        Element cylchildele = null;
		        if(Integer.parseInt(status) == BkChanYeLianTreeNode.TDXBK) {
		        	cylchildele = cylparentele.addElement("BanKuai");
		            cylchildele.addAttribute("bkname", tmpname  );
		            cylchildele.addAttribute("bkcode", tmpbkcode  );
		            cylchildele.addAttribute("Type", status);
		            
		        } else if(Integer.parseInt(status) == BkChanYeLianTreeNode.SUBBK) {
		        	cylchildele = cylparentele.addElement("SubBanKuai");
		        	cylchildele.addAttribute("subbkname", tmpname);
		        	cylchildele.addAttribute("subbkcode", tmpbkcode  );
		        	String suoshubkcode = treeChild.getChanYeLianSuoShuTdxBanKuaiName();
		            cylchildele.addAttribute("suoshubkcode", suoshubkcode  ); 
		            cylchildele.addAttribute("Type", status);
		            
		        } else if(Integer.parseInt(status) == BkChanYeLianTreeNode.BKGEGU) {
	            	cylchildele = cylparentele.addElement("gegu");
		        	cylchildele.addAttribute("geguname", tmpname  );
		        	cylchildele.addAttribute("gegucode", tmpbkcode  );
		        	String suoshubkcode = treeChild.getChanYeLianSuoShuTdxBanKuaiName();
		            cylchildele.addAttribute("suoshubkcode", suoshubkcode  ); 
		            cylchildele.addAttribute("Type", status);
	            	
		        	  //如果是个股，要保存到个股产业链XML里  	
		            	TreeNode[] path = treeChild.getPath();
		            	String chanyelianstring = "";
		            	String curbankuai = ((BkChanYeLianTreeNode)path[1]).getMyOwnName(); //板块名字
		            
		            	
		            	for(int i=1;i<path.length-1;i++) //把产业链整合成一个字符串
		            		chanyelianstring = chanyelianstring + ((BkChanYeLianTreeNode)path[i]).getMyOwnName() + "-";
		            	chanyelianstring = chanyelianstring.substring(0, chanyelianstring.length()-1);
		            	
		            	String xpath = ".//gegu[@stockcode=\"" + tmpbkcode + "\"]";
		            	 Node tmpnode = ggcyldoc.selectSingleNode(xpath);
		            	 if(tmpnode != null) { //已经存在的
		            		 Element tmpitemele = ((Element)tmpnode).addElement("chanyelian");
		            		 tmpitemele.addAttribute("bankuai", curbankuai);
		            		 tmpitemele.addAttribute("suoshubkcode", tmpbkcode);
		            		 tmpitemele.setText(chanyelianstring);
		            		 
		            	 } else {
				             Element tmpggcylele = ggcyldoc.getRootElement().addElement("gegu" );
			    			 tmpggcylele.addAttribute("stockcode", tmpbkcode  );
			    			 tmpggcylele.addAttribute("stockname", tmpname  );
			    			 Element tmpnewitem = tmpggcylele.addElement("chanyelian");
			    			 tmpnewitem.addAttribute("bankuai", curbankuai);
			    			 tmpnewitem.addAttribute("suoshubkcode", tmpbkcode);
			    			 tmpnewitem.setText(chanyelianstring);
		            	 }

		         }
		          
		         exportToDoc(treeChild,cylchildele,ggcyldoc);
	        }
		}

	 
		
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
					   //System.out.println(e.getName() + ": " + e.getText());
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
					    System.out.println(chanyelian);
					    gegucyllist.add(chanyelian); 
			    	 }
					
				}
					
			} catch (java.lang.NullPointerException ex) {
			}
			
			return gegucyllist;
		}
		
		/*
		 * 把数据库中的板块信息保存到XML中, 用新的BkChanYeLianTreeNode存储
		 */
		private void updateBkCylXmlFileAfterDbChanged2 ()
		{
			
//			HashMap<String, BkChanYeLianTreeNode> tdxbk = bkopt.getTDXBanKuaiList2 ();
			HashMap<String, BanKuai> tdxbk = bkopt.getTDXBanKuaiList ();
			HashMap<String, BanKuai> tdxzhishu = bkopt.getTDXAllZhiShuList ();
			Set<String> tdxbkset = tdxbk.keySet();
			Set<String> tdxzhishuset = tdxzhishu.keySet();
			SetView<String> uniontdxbkindb = Sets.union(tdxbkset, tdxzhishuset );
			 
			Set<String> bkcylxmlset = this.getTDXBanKuaiSetInCylXml ();
			
			//XML没有，数据库有，要添加如XML
			SetView<String> differencebankuainew = Sets.difference(uniontdxbkindb, bkcylxmlset );
	        for (String newbkcode : differencebankuainew) {
	        	System.out.println("XML将要中加入" + newbkcode);
				//在XML 中加入新的板块 //<BanKuai bkname="高速公路" Status="4">
//				String xpathnew = ".//BanKuai[@bkname=\"" + newbk + "\"]";
//				Node tmpnode = cylxmldoc.selectSingleNode(xpathnew);
					Element newele = cylxmlroot.addElement("BanKuai"); //在XML 中加入新的板块
					String bkname = null;
					try {
						bkname = tdxbk.get(newbkcode).getUserObject().toString();
					} catch (java.lang.NullPointerException ex) {
						bkname = tdxzhishu.get(newbkcode).getUserObject().toString();
					}
					newele.addAttribute("bkname", bkname);
					newele.addAttribute("bkcode", newbkcode);
					newele.addAttribute("Type", String.valueOf(BkChanYeLianTreeNode.TDXBK) );
					System.out.println("XML中加入" + newbkcode + bkname);
	        }
	        
	        //XML里面有，数据库中没有了，应该删除，要从几个XML都删除
	        SetView<String> differencebankuaiold = Sets.difference(bkcylxmlset, uniontdxbkindb );
		    for (String oldbkcode : differencebankuaiold) {
				//从产业链XML中删除该板块
				try {
					String xpath = ".//BanKuai[@bkcode=\"" + oldbkcode + "\"]";
					Node tmpnode = cylxmldoc.selectSingleNode(xpath);
					cylxmlroot.remove(tmpnode);
					System.out.println("产业链XML中删除" + oldbkcode);
				} catch (java.lang.NullPointerException ex) {
					
				}
				
				//从个股产业链XML中删除该板块 //  <bankuai bkname="智能穿戴">智能穿戴</bankuai>
				try {
					String xpath = ".//gegu/chanyelian[@bkcode=\"" + oldbkcode + "\"]";
					@SuppressWarnings("unchecked")
					List<Node> tmpnodegg = ggcylxmldocument.selectNodes(xpath);
					for(Node singlenode:tmpnodegg)
						ggcylxmlroot.remove(singlenode);
					//xmldoc.remove(tmpnode);
					System.out.println("个股产业链XML中删除" + oldbkcode);
				} catch (java.lang.NullPointerException ex) {
				}
				
				
		    }
		    
		    //对于没有变化的板块，要检查板块的名字是否有改变
		    SetView<String> intersectionbankuai = Sets.intersection(uniontdxbkindb, bkcylxmlset );
		    for(String intsbkcode : intersectionbankuai) {
		    	try {
					String xpath = ".//BanKuai[@bkcode=\"" + intsbkcode + "\"]";
					Element tmpnode = (Element)cylxmldoc.selectSingleNode(xpath);
					String curnameinxml = tmpnode.attribute("bkname").getText();
					
					String bknameindb = null;
					try {
						bknameindb = tdxbk.get(intsbkcode).getUserObject().toString();
					} catch (java.lang.NullPointerException ex) {
						bknameindb = tdxzhishu.get(intsbkcode).getUserObject().toString();
					}
					
					if(!bknameindb.equals(curnameinxml ) ) 
						tmpnode.attribute("bkname").setText(bknameindb);
					
				} catch (java.lang.NullPointerException ex) {
					
				}
		    	
		    }
		    
		    
		    OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GBK");    // 指定XML编码        
			//保存产业链XML
			try {
				XMLWriter writer = new XMLWriter(new FileOutputStream(bankuaichanyelianxml),format); // 输出全部原始数据，并用它生成新的我们需要的XML文件  
					writer.write(cylxmldoc); //输出到文件  
					//writer.flush();
					writer.close();
			} catch (IOException e) {
					e.printStackTrace();
			}
			//保存个股产业链XML		
			try {
				XMLWriter writer = new XMLWriter(new FileOutputStream(geguchanyelianxml),format); // 输出全部原始数据，并用它生成新的我们需要的XML文件  
				writer.write(ggcylxmldocument); //输出到文件  
				//writer.flush();
				writer.close();
				
			} catch (IOException e) {
					e.printStackTrace();
			}
			
			hasXmlRevised = true;
		}


}
