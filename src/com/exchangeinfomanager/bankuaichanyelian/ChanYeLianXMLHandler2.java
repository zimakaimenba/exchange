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

import com.exchangeinfomanager.asinglestockinfo.ASingleStockInfo;
import com.exchangeinfomanager.bankuai.gui.BanKuai;
import com.exchangeinfomanager.checkboxtree.CheckBoxTreeNode;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockDbOperations;
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

public class ChanYeLianXMLHandler2 
{
	public ChanYeLianXMLHandler2() 
	{
		initialzieSysconf ();
		bankuaichanyelianxml = sysconfig.getBanKuaiChanYeLianXml ();
		geguchanyelianxml = sysconfig.getGeGuChanYeLianXmlFile ();
		this.bkopt = new BanKuaiDbOperation ();
//		this.hypy = new HanYuPinYing();
		this.stockopt = new StockDbOperations ();
		
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
	private StockDbOperations stockopt;
	
	private Document cylxmldoc;
	Element cylxmlroot = null;
	private Document ggcylxmldocument;
	private Element ggcylxmlroot;
	private boolean hasXmlRevised = false;
	private HanYuPinYing hypy;

	
	private void initialzieSysconf ()
	{
		sysconfig = SystemConfigration.getInstance();
	}
	public  BkChanYeLianTreeNode getBkChanYeLianXMLTree()
	{
		if(this.isXmlShouldUpdated() )
			this.updateBkCylXmlFileAfterDbChanged2 ();
		
		BkChanYeLianTreeNode topNode = new BkChanYeLianTreeNode("TongDaXinBanKuaiAndZhiShu","000000");
		if(cylxmlroot != null) {
			generateChanYeLianTreeFromXML(topNode,cylxmlroot);
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
		Calendar  caldbmdf =  Calendar.getInstance();
		caldbmdf.setTime(bklastmodifiedtimeindb);
		System.out.println(caldbmdf.getTime() );
		
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
	
	/*
	 * 把数据库中的板块信息保存到XML中, 用新的BkChanYeLianTreeNode存储
	 */
	private void updateBkCylXmlFileAfterDbChanged2 ()
	{
		
		HashMap<String, BkChanYeLianTreeNode> tdxbk = bkopt.getTDXBanKuaiList2 ();
		HashMap<String, BkChanYeLianTreeNode> tdxzhishu = bkopt.getTDXAllZhiShuList2 ();
		Set<String> tdxbkset = tdxbk.keySet();
		Set<String> tdxzhishuset = tdxzhishu.keySet();
		SetView<String> uniontdxbkindb = Sets.union(tdxbkset, tdxzhishuset );
		 
		Set<String> bkcylxmlset = this.getTDXBanKuaiSetInCylXml ();
		
		SetView<String> differencebankuainew = Sets.difference(uniontdxbkindb, bkcylxmlset );
        for (String newbkcode : differencebankuainew) {
        	System.out.println("XML将要中加入" + newbkcode);
			//在XML 中加入新的板块 //<BanKuai bkname="高速公路" Status="4">
//			String xpathnew = ".//BanKuai[@bkname=\"" + newbk + "\"]";
//			Node tmpnode = cylxmldoc.selectSingleNode(xpathnew);
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
	
	
	/*
	 * 把数据库中的板块信息保存到XML中
	 */
//	private void updateBkCylXmlFileAfterDbChanged ()
//	{
//		
//		HashMap<String, BanKuai> tdxbk = bkopt.getTDXBanKuaiList ();
//		HashMap<String, BanKuai> tdxzhishu = bkopt.getTDXAllZhiShuList ();
//		Set<String> tdxbkset = tdxbk.keySet();
//		Set<String> tdxzhishuset = tdxzhishu.keySet();
//		SetView<String> uniontdxbkindb = Sets.union(tdxbkset, tdxzhishuset );
//		 
//		Set<String> bkcylxmlset = this.getTDXBanKuaiSetInCylXml ();
//		
//		SetView<String> differencebankuainew = Sets.difference(uniontdxbkindb, bkcylxmlset );
//        for (String newbk : differencebankuainew) {
//			//在XML 中加入新的板块 //<BanKuai bkname="高速公路" Status="4">
////			String xpathnew = ".//BanKuai[@bkname=\"" + newbk + "\"]";
////			Node tmpnode = cylxmldoc.selectSingleNode(xpathnew);
//				Element newele = cylxmlroot.addElement("BanKuai"); //在XML 中加入新的板块
//				String bkname = null;
//				try {
//					bkname = tdxbk.get(newbk).getBankuainame();
//				} catch (java.lang.NullPointerException e ) { //
//					//e.printStackTrace();
//					bkname = tdxzhishu.get(newbk).getBankuainame();
//				} 
//				newele.addAttribute("bkname", bkname);
//				newele.addAttribute("suoshubkcode", newbk);
//				newele.addAttribute("Type", "4");
//				System.out.println("XML中加入" + newbk + bkname);
//        }
//        
//        SetView<String> differencebankuaiold = Sets.difference(bkcylxmlset, uniontdxbkindb );
//	    for (String oldbk : differencebankuaiold) {
//			//从产业链XML中删除该板块
//			try {
//				String xpath = ".//BanKuai[@bkname=\"" + oldbk + "\"]";
//				Node tmpnode = cylxmldoc.selectSingleNode(xpath);
//				cylxmlroot.remove(tmpnode);
//				System.out.println("产业链XML中删除" + oldbk);
//			} catch (java.lang.NullPointerException ex) {
//				
//			}
//			
//			//从个股产业链XML中删除该板块 //  <bankuai bkname="智能穿戴">智能穿戴</bankuai>
//			try {
//				String xpath = ".//gegu/bankuai[@bkname=\"" + oldbk + "\"]";
//				Node tmpnodegg = ggcylxmldocument.selectSingleNode(xpath);
//				ggcylxmlroot.remove(tmpnodegg);
//				//xmldoc.remove(tmpnode);
//				System.out.println("个股产业链XML中删除" + oldbk);
//			} catch (java.lang.NullPointerException ex) {
//			}
//	    }
//	    
//	    OutputFormat format = OutputFormat.createPrettyPrint();
//		format.setEncoding("GBK");    // 指定XML编码        
//		//保存产业链XML
//		try {
//			XMLWriter writer = new XMLWriter(new FileOutputStream(bankuaichanyelianxml),format); // 输出全部原始数据，并用它生成新的我们需要的XML文件  
//				writer.write(cylxmldoc); //输出到文件  
//				//writer.flush();
//				writer.close();
//		} catch (IOException e) {
//				e.printStackTrace();
//		}
//		//保存个股产业链XML		
//		try {
//			XMLWriter writer = new XMLWriter(new FileOutputStream(geguchanyelianxml),format); // 输出全部原始数据，并用它生成新的我们需要的XML文件  
//			writer.write(ggcylxmldocument); //输出到文件  
//			//writer.flush();
//			writer.close();
//			
//		} catch (IOException e) {
//				e.printStackTrace();
//		}
//		
//		hasXmlRevised = true;
//	}
	
	
	
//	 private void importFromXML(BkChanYeLianTreeNode topNode, Element xmlelement) 
//	 {
//	    	Iterator it = xmlelement.elementIterator();
//	    	 while (it.hasNext()) 
//			 {
//				   Element element = (Element) it.next();
//				   
//				   BkChanYeLianTreeNode parentsleaf = null;
//
//				   //已知属性名称情况下
//				   //System.out.println("id: " + element.attributeValue("Subject")); //通达信板块名称或者个股名称或子版块名称
//				   //System.out.println("id: " + element.attributeValue("Status")); //标记是板块还是个股
//			   
//				   String bkname = null;
//				   if(element.attributeValue("Type").trim().equals("4") ) //是通达信板块
//					   bkname = element.attributeValue("bkname");
//				   else if(element.attributeValue("Type").trim().equals("5") ) //是自定义子板块
//					   bkname = element.attributeValue("subbkname");
//				   else if(element.attributeValue("Type").trim().equals("6") ) //是自定义子板块
//					   bkname = element.attributeValue("geguname");
//				   parentsleaf = new BkChanYeLianTreeNode( bkname);
//				   
//				   
//				   String bkcode = element.attributeValue("suoshubkcode"); //所有节点都保存所属板块的板块code，便于识别是在哪个板块下的节点
//				   parentsleaf.setTDXBanKuaiZhiShuCode (bkcode);
//				   parentsleaf.setNodeType( Integer.parseInt(element.attributeValue("Type") ) );
//				   if(parentsleaf.getNodeType() != BkChanYeLianTreeNode.BKGEGU)
//					   parentsleaf.setHanYuPingYin(hypy.getBanKuaiNameOfPinYin(bkname ) );
//				   else //个股的话只存代码的汉语拼音，把名字去掉，因为名字可能会变
//					   parentsleaf.setHanYuPingYin(hypy.getBanKuaiNameOfPinYin(bkname.substring(0,6 ) ) );
//				   topNode.add(parentsleaf);
//
//				   importFromXML(parentsleaf,element);
//			}
//	}
	
	 private void generateChanYeLianTreeFromXML(BkChanYeLianTreeNode topNode, Element xmlelement) 
	 {
	    	 Iterator it = xmlelement.elementIterator();
	    	 while (it.hasNext() ) 
			 {
				   Element element = (Element) it.next();

				   //已知属性名称情况下
				   //System.out.println("id: " + element.attributeValue("Subject")); //通达信板块名称或者个股名称或子版块名称
				   //System.out.println("id: " + element.attributeValue("Status")); //标记是板块还是个股
			   
				   String bkname = null, bkowncode = null; String suoshubkcode = null;
				   if(element.attributeValue("Type").trim().equals("4") ) { //是通达信板块  
					   bkname = element.attributeValue("bkname");
					   bkowncode = element.attributeValue("bkcode");
					   suoshubkcode = bkowncode;
				   } else if(element.attributeValue("Type").trim().equals("5") ) {//是自定义子板块
					   bkname = element.attributeValue("subbkname");
					   bkowncode = element.attributeValue("subbkcode");
					    suoshubkcode = element.attributeValue("suoshubkcode"); //所有节点都保存所属板块的板块code，便于识别是在哪个板块下的节点
					   
				   } else if(element.attributeValue("Type").trim().equals("6") ) {//是自定义子板块
					   bkname = element.attributeValue("geguname");
					   bkowncode = element.attributeValue("gegucode");
					   suoshubkcode = element.attributeValue("suoshubkcode"); //所有节点都保存所属板块的板块code，便于识别是在哪个板块下的节点
					   
				   }
				   
				   BkChanYeLianTreeNode parentsleaf  = new BkChanYeLianTreeNode( bkname,bkowncode);
				   parentsleaf.setTongDaXingBanKuaiCode (suoshubkcode);
				   parentsleaf.setNodeType( Integer.parseInt(element.attributeValue("Type") ) );

				   topNode.add(parentsleaf);

				   generateChanYeLianTreeFromXML(parentsleaf,element);
			}
	}

	 
//	 private String getBanKuaiNameOfPinYin (String chinese)
//	 {
//		 //System.out.println(chinese);
//		 //这部分获得全部拼音
//		 HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
//	        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//	        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//	        format.setVCharType(HanyuPinyinVCharType.WITH_V);
//	 
//	        char[] input = chinese.trim().toCharArray();
//	        String output = "";
//	 
//	        try {
//	            for (int i = 0; i < input.length; i++) {
//	                if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
//	                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
//	                    output += temp[0];
//	                } else
//	                    output += java.lang.Character.toString(input[i]);
//	            }
//	        } catch (BadHanyuPinyinOutputFormatCombination e) {
//	            e.printStackTrace();
//	        }
//	       //System.out.println(output);
//	       
//	       //这部分获得首字母拼音
//	        StringBuffer pybf = new StringBuffer();  
//            char[] arr = chinese.toCharArray();  
//            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();  
//            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
//            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
//            for (int i = 0; i < arr.length; i++) {  
//                    if (arr[i] > 128) {  
//                            try {  
//                                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);  
//                                    if (temp != null) {  
//                                            pybf.append(temp[0].charAt(0));  
//                                    }  
//                            } catch (BadHanyuPinyinOutputFormatCombination e) {  
//                                    e.printStackTrace();  
//                            }  
//                    } else {  
//                            pybf.append(arr[i]);  
//                    }  
//            }  
//            //System.out.println( pybf.toString().replaceAll("\\W", "").trim() ); 
//            
//            return pybf.toString().replaceAll("\\W", "").trim(); //返回首字母拼音
//	 }
	 
	 public boolean saveTreeToChanYeLianXML (BkChanYeLianTreeNode ginkgorootnode) //GEN-FIRST:event_saveButtonActionPerformed 
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
	            
	            String tmpbkcode = treeChild.getNodeOwnCode() ;
	            int nodetype = treeChild.getNodeType();
	            //因为板块的名字经常会变，所以存到XML中需要从数据库中查出最新的名字
	            String tmpname = null;
	            if(nodetype == BkChanYeLianTreeNode.BKGEGU ) {
	            	ASingleStockInfo tmpsinglestock = new ASingleStockInfo (tmpbkcode); 
	            	tmpsinglestock = stockopt.getSingleStockBasicInfo(tmpsinglestock);
	            	tmpname = tmpsinglestock.getStockname();
	            } else if(nodetype == BkChanYeLianTreeNode.TDXBK ) {
	            	tmpname = bkopt.getTdxBanKuaiNameByCode (tmpbkcode);
	            }

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
		        	String suoshubkcode = treeChild.getTongDaXingBanKuaiCode ();
		            cylchildele.addAttribute("suoshubkcode", suoshubkcode  ); 
		            cylchildele.addAttribute("Type", status);
		            
		        } else if(Integer.parseInt(status) == BkChanYeLianTreeNode.BKGEGU) {
	            	cylchildele = cylparentele.addElement("gegu");
		        	cylchildele.addAttribute("geguname", tmpname  );
		        	cylchildele.addAttribute("gegucode", tmpbkcode  );
		        	String suoshubkcode = treeChild.getTongDaXingBanKuaiCode ();
		            cylchildele.addAttribute("suoshubkcode", suoshubkcode  ); 
		            cylchildele.addAttribute("Type", status);
	            	
		        	  //如果是个股，要保存到个股产业链XML里  	
		            	TreeNode[] path = treeChild.getPath();
		            	String chanyelianstring = "";
		            	String curbankuai = path[1].toString(); //板块名字
		            
		            	
		            	for(int i=1;i<path.length-1;i++) //把产业链整合成一个字符串
		            		chanyelianstring = chanyelianstring + path[i].toString() + "-";
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
			    			 tmpggcylele.addAttribute("stockname", tmpbkcode  );
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
		
		public ASingleStockInfo getStockChanYeLianInfo (ASingleStockInfo stockbasicinfo2) 
		{
			String stockcode = stockbasicinfo2.getStockcode().trim();
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
				 stockbasicinfo2.setChanYeLianInfo (ggcyl);
			 }
			stockbasicinfo2.setChanYeLianInfo (ggcyl);
			return stockbasicinfo2;
		}

}
