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
import java.util.Map;
import java.util.Set;

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

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai;
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
		this.sysconfig = SystemConfigration.getInstance();
		
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
		
		initializeAllStocksTree ();
	}
	
	private static Logger logger = Logger.getLogger(ChanYeLianXMLHandler.class);
	private String bankuaichanyelianxml;
	private String geguchanyelianxml;
	private SystemConfigration sysconfig;
	private BanKuaiDbOperation bkopt;
	
	private Document cylxmldoc;
	Element cylxmlroot = null;
	private Document ggcylxmldocument;
	private Element ggcylxmlroot;
	private boolean hasXmlRevised = false;

	private SetView<String> oldBanKuaiFromDb = null;
//	private BkChanYeLianTree treeallstocks;
	private BkChanYeLianTree treecyl;
	
	private void initializeAllStocksTree() 
	{
		DaPan alltopNode = new DaPan("000000","两交易所");
		
		ArrayList<Stock> allstocks = bkopt.getAllStocks ();
		for (Stock stock : allstocks ) {
		    alltopNode.add(stock);
//		    logger.debug(stock.getMyOwnCode() + "类型" + stock.getType());
		}
		
		ArrayList<BanKuai> allbkandzs = bkopt.getTDXBanKuaiList ("all");
		for (BanKuai bankuai : allbkandzs ) {
		    alltopNode.add(bankuai);
//		    logger.debug(bankuai.getMyOwnCode()+ "类型" + bankuai.getType());
		}

		treecyl = new BkChanYeLianTree(alltopNode);
		
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treecyl.getModel().getRoot();
	    @SuppressWarnings("unchecked")
		Enumeration<BkChanYeLianTreeNode> e = treeroot.depthFirstEnumeration();
	    while (e.hasMoreElements() ) {
	    	BkChanYeLianTreeNode node = e.nextElement();
    		logger.debug(node.getMyOwnCode() + "类型" + node.getType());
	    }
		
		//产业链tree part
		if(this.isXmlShouldUpdated() ) {
			hasXmlRevised = true;

			if(cylxmlroot != null) {
				generateChanYeLianTreeFromXML(alltopNode,cylxmlroot,treecyl); //生成产业链树
			} else 
				logger.debug(bankuaichanyelianxml+ "存在错误");
		}
		
		allstocks = null;
		e = null;
	}
	/*
	 * 
	 */
	private void generateChanYeLianTreeFromXML(BkChanYeLianTreeNode topNode, Element xmlelement, BkChanYeLianTree treeallstocks ) 
	{
	    	 Iterator it = xmlelement.elementIterator();
	    	 
	    	 while (it.hasNext() ) 
	    	 {
	    		   boolean shouldremovedwhensavexml = false;
				   Element element = (Element) it.next();
			   
				   BkChanYeLianTreeNode parentsleaf = null;
				   
				   String bkname = null, bkowncode = null; String suoshubkcode = null;
				   String nodetypestr = element.attributeValue("Type").trim();
				   Integer nodetype = Integer.parseInt(nodetypestr ); 
				   
				   if(BanKuaiAndStockBasic.TDXBK == nodetype ) { //是通达信板块  
					   bkowncode = element.attributeValue("bkcode");
					   suoshubkcode = bkowncode;
					 
					   try{
						   parentsleaf = (BanKuai) treeallstocks.getSpecificNodeByHypyOrCode(bkowncode,nodetype);
						  
					   } catch (java.lang.NullPointerException e) { //可能出现数据库中已经删除的板块，XML里面还有的板块，是要删除的板块
						   bkname = element.attributeValue("bkname");
						   shouldremovedwhensavexml = true;
						   
						   parentsleaf = new BanKuai ( bkowncode,bkname);
						   ((BanKuai)parentsleaf).setBanKuaiLeiXing(BanKuai.NOGGNOSELFCJL); //后面显示的时候还需要这属性，对于删除的板块设置为nogegu no cje
					   }
					   
					   
				   } else if(BanKuaiAndStockBasic.SUBBK == nodetype ) {//是自定义子板块
					   bkname = element.attributeValue("subbkname");
					   bkowncode = element.attributeValue("subbkcode");
					   suoshubkcode = element.attributeValue("suoshubkcode"); //所有节点都保存所属板块的板块code，便于识别是在哪个板块下的节点
					    
					    parentsleaf = new SubBanKuai(bkowncode,bkname);
					    topNode.add(parentsleaf);
					   
				   } else if(BanKuaiAndStockBasic.TDXBK == nodetype) {//是个股
					   bkname = element.attributeValue("geguname");
					   bkowncode = element.attributeValue("gegucode");
					   
					   Stock tmpparentsleaf = (Stock)treeallstocks.getSpecificNodeByHypyOrCode (bkowncode,nodetype );
					   logger.debug(bkowncode);
					   tmpparentsleaf = bkopt.getTDXBanKuaiForAStock (tmpparentsleaf); //通达信板块信息
					   suoshubkcode = element.attributeValue("suoshubkcode"); //所有节点都保存所属板块的板块code，便于识别是在哪个板块下的节点
					   if( !tmpparentsleaf.isInTdxBanKuai(suoshubkcode))//如果该股已经不属于该板块，需要删除的，则标记好
						   shouldremovedwhensavexml = true;

					   StockOfBanKuai stofbk = new StockOfBanKuai ((BanKuai)topNode,(Stock)tmpparentsleaf);
					   parentsleaf = stofbk; //树形里面还是存板块所属的个股
					   topNode.add(parentsleaf);
				   }

				   if(shouldremovedwhensavexml) //如果代码已经属于需要删除的，则标记好
					   parentsleaf.getNodetreerelated().setShouldBeRemovedWhenSaveXml ();
				   
				   try {
					   String addedTime =  element.attributeValue("addedTime"); //板块或个股加入到产业链树的时间
					   if(addedTime != null)
						   parentsleaf.getNodetreerelated().setAddedToCylTreeTime(addedTime);
					   
				   } catch (java.lang.NullPointerException ex) {
					   ex.printStackTrace();
				   }

				   

				   generateChanYeLianTreeFromXML(parentsleaf,element,treeallstocks);
			}
	}
	/*
	 * 
	 */
	private void addNewTdxBanKuaiFromDbToChanYeLianTree(BkChanYeLianTreeNode topNode, SetView<String> newBanKuaiFromDb, BkChanYeLianTree treeallstocks) 
	{
		for(String newbkcode:newBanKuaiFromDb) {
			        	logger.debug("XML将要中加入" + newbkcode);
			        	try {
			        		BanKuai parentsleaf = (BanKuai)treeallstocks.getSpecificNodeByHypyOrCode(newbkcode,BanKuaiAndStockBasic.TDXBK);
			        		
							topNode.add(parentsleaf);
							
						} catch (java.lang.NullPointerException ex) {
							ex.printStackTrace();
						}
	    }
	}

	public  BkChanYeLianTree getBkChanYeLianXMLTree()
	{
		return treecyl;
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
//		logger.debug(calfilemdf.getTime() );
		
		Date bklastmodifiedtimeindb = bkopt.getTDXRelatedTableLastModfiedTime ();
		if(bklastmodifiedtimeindb == null)
			return false;
		
		Calendar  caldbmdf =  Calendar.getInstance();
		caldbmdf.setTime(bklastmodifiedtimeindb);
		//logger.debug(caldbmdf.getTime() );
		
//		return true;//测试用
		if(caldbmdf.after(calfilemdf)) {
			logger.debug("数据库已经更新，开始更新XML");
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
	            
	            if(treeChild.getNodetreerelated().shouldBeRemovedWhenSaveXml()) //应该删除的节点，该节点以下的所有节点都不保存
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
		        	String suoshubkcode = treeChild.getNodetreerelated().getChanYeLianSuoShuTdxBanKuaiName();
		            cylchildele.addAttribute("suoshubkcode", suoshubkcode  ); 
		            cylchildele.addAttribute("Type", status);
		            
		        } else if(Integer.parseInt(status) == BkChanYeLianTreeNode.BKGEGU) {
	            	cylchildele = cylparentele.addElement("gegu");
		        	cylchildele.addAttribute("geguname", tmpname  );
		        	cylchildele.addAttribute("gegucode", tmpbkcode  );
		        	String suoshubkcode = treeChild.getNodetreerelated().getChanYeLianSuoShuTdxBanKuaiName();
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
		 * 把数据库中的板块信息保存到XML中, 用新的BkChanYeLianTreeNode存储
		 */
		private void updateBkCylXmlFileAfterDbChanged2 ()
		{
			
//			HashMap<String, BkChanYeLianTreeNode> tdxbk = bkopt.getTDXBanKuaiList2 ();
			HashMap<String, BanKuai> tdxbk = bkopt.getTDXBanKuaiList ("all");
			
			Set<String> uniontdxbkindb = tdxbk.keySet();
			
//			SetView<String> uniontdxbkindb = Sets.union(tdxbkset, tdxzhishuset );
			 
			Set<String> bkcylxmlset = this.getTDXBanKuaiSetInCylXml ();
			
			//XML没有，数据库有，要添加如XML
			SetView<String> differencebankuainew = Sets.difference(uniontdxbkindb, bkcylxmlset );
	        for (String newbkcode : differencebankuainew) {
	        	logger.debug("XML将要中加入" + newbkcode);
				//在XML 中加入新的板块 //<BanKuai bkname="高速公路" Status="4">
//				String xpathnew = ".//BanKuai[@bkname=\"" + newbk + "\"]";
//				Node tmpnode = cylxmldoc.selectSingleNode(xpathnew);
					Element newele = cylxmlroot.addElement("BanKuai"); //在XML 中加入新的板块
					String bkname = null;
					try {
						bkname = tdxbk.get(newbkcode).getUserObject().toString();
					} catch (java.lang.NullPointerException ex) {
//						bkname = tdxzhishu.get(newbkcode).getUserObject().toString();
					}
					newele.addAttribute("bkname", bkname);
					newele.addAttribute("bkcode", newbkcode);
					newele.addAttribute("Type", String.valueOf(BkChanYeLianTreeNode.TDXBK) );
					logger.debug("XML中加入" + newbkcode + bkname);
	        }
	        
	        //XML里面有，数据库中没有了，应该删除，要从几个XML都删除
	        SetView<String> differencebankuaiold = Sets.difference(bkcylxmlset, uniontdxbkindb );
		    for (String oldbkcode : differencebankuaiold) {
				//从产业链XML中删除该板块
				try {
					String xpath = ".//BanKuai[@bkcode=\"" + oldbkcode + "\"]";
					Node tmpnode = cylxmldoc.selectSingleNode(xpath);
					cylxmlroot.remove(tmpnode);
					logger.debug("产业链XML中删除" + oldbkcode);
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
					logger.debug("个股产业链XML中删除" + oldbkcode);
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
//						bknameindb = tdxzhishu.get(intsbkcode).getUserObject().toString();
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
