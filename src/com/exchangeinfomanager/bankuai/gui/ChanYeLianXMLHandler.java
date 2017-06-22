package com.exchangeinfomanager.bankuai.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

import net.ginkgo.dom4jcopy.GinkgoNode;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class ChanYeLianXMLHandler {

	

	public ChanYeLianXMLHandler() 
	{
		initialzieSysconf ();
		bankuaichanyelianxml = sysconfig.getBanKuaiChanYeLianXml ();
		geguchanyelianxml = sysconfig.getGeGuChanYeLianXmlFile ();
		File tmpfl = new File (bankuaichanyelianxml);
		cyllastmodifiedtime = tmpfl.lastModified();
		
		this.rfreshBkChanYeLianXMLTree ();
		this.refreshGeGuChanYeLianXml ();
	}
	private String bankuaichanyelianxml;
	private SystemConfigration sysconfig;
	private String geguchanyelianxml;
	GinkgoNode topNode;
	private Document ggcylxmldocument;
	private Element ggcylxmlroot;
	private long cyllastmodifiedtime;
	
	private void initialzieSysconf ()
	{
		sysconfig = SystemConfigration.getInstance();
	}
	public GinkgoNode getBkChanYeLianXMLTree() 
	{
		File tmpfl = new File (bankuaichanyelianxml);
		long newmodifiedtime = tmpfl.lastModified(); //标记XML的修改时间，以后判断如果时间有更新说明XML有更新，就要重新刷新SML
		if(newmodifiedtime != cyllastmodifiedtime) {
			this.rfreshBkChanYeLianXMLTree ();
			this.refreshGeGuChanYeLianXml ();
			cyllastmodifiedtime = newmodifiedtime; 
		}
		
		return topNode;
	}
	
	private  GinkgoNode rfreshBkChanYeLianXMLTree()
	{
		FileInputStream xmlfileinput = null;
		try {
			xmlfileinput = new FileInputStream(bankuaichanyelianxml);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Element xmlroot = null;
		try {
			SAXReader reader = new SAXReader();
			Document xmldoc = reader.read(xmlfileinput);
			xmlroot = xmldoc.getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		if(xmlroot != null) {
			topNode = new GinkgoNode("JTree");
//			System.out.println( "开始导入XML");
			importFromXML(topNode,xmlroot);
		} else {
			System.out.println(bankuaichanyelianxml+ "存在错误");
			topNode = new GinkgoNode("JTree");
		}
		return topNode;
	}
	 private void importFromXML(GinkgoNode parentNode, Element xmlelement) 
	 {
	    	Iterator it = xmlelement.elementIterator();
	    	 while (it.hasNext()) 
			 {
				   Element element = (Element) it.next();
				   
				   GinkgoNode parentsleaf = null;

				   //已知属性名称情况下
				   //System.out.println("id: " + element.attributeValue("Subject")); //通达信板块名称或者个股名称或子版块名称
				   //System.out.println("id: " + element.attributeValue("Status")); //标记是板块还是个股
			   
				   parentsleaf = new GinkgoNode(element.attributeValue("Subject") );
				   parentsleaf.setStatus( Integer.parseInt(element.attributeValue("Status") ) );
				   parentsleaf.setHanYuPingYin(getBanKuaiNameOfPinYin(element.attributeValue("Subject") ) );
				   parentNode.add(parentsleaf);

				   importFromXML(parentsleaf,element);
			}
	}
	
	 private String getBanKuaiNameOfPinYin (String chinese)
	 {
		 //System.out.println(chinese);
		 //这部分获得全部拼音
		 HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
	        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
	        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	        format.setVCharType(HanyuPinyinVCharType.WITH_V);
	 
	        char[] input = chinese.trim().toCharArray();
	        String output = "";
	 
	        try {
	            for (int i = 0; i < input.length; i++) {
	                if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
	                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
	                    output += temp[0];
	                } else
	                    output += java.lang.Character.toString(input[i]);
	            }
	        } catch (BadHanyuPinyinOutputFormatCombination e) {
	            e.printStackTrace();
	        }
	       //System.out.println(output);
	       
	       //这部分获得首字母拼音
	        StringBuffer pybf = new StringBuffer();  
            char[] arr = chinese.toCharArray();  
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();  
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
            for (int i = 0; i < arr.length; i++) {  
                    if (arr[i] > 128) {  
                            try {  
                                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);  
                                    if (temp != null) {  
                                            pybf.append(temp[0].charAt(0));  
                                    }  
                            } catch (BadHanyuPinyinOutputFormatCombination e) {  
                                    e.printStackTrace();  
                            }  
                    } else {  
                            pybf.append(arr[i]);  
                    }  
            }  
            //System.out.println( pybf.toString().replaceAll("\\W", "").trim() ); 
            
            return pybf.toString().replaceAll("\\W", "").trim(); //返回首字母拼音
	 }
	 
	 public boolean saveTreeToChanYeLianXML (GinkgoNode ginkgorootnode) //GEN-FIRST:event_saveButtonActionPerformed 
		//public boolean saveTreeToChanYeLianXML (JTree tree) //GEN-FIRST:event_saveButtonActionPerformed
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
			
//			rfreshBkChanYeLianXMLTree ();
//			refreshGeGuChanYeLianXml ();
			return true;
	    }
		private void exportToDoc (GinkgoNode parentnode,Element cylparentele, Document ggcyldoc)
		{
			GinkgoNode treeChild;
			
			for (Enumeration<GinkgoNode> child = parentnode.children(); child.hasMoreElements();) {
	            treeChild = (GinkgoNode) child.nextElement();
            	String tmpname = treeChild.getUserObject().toString();
	            //System.out.println(tmpname);
	            Element cylchildele = cylparentele.addElement("Node");
	            cylchildele.addAttribute("Subject", tmpname  );
		            
		        String status = String.valueOf(treeChild.getStatus());
		        cylchildele.addAttribute("Status", status);
		            
	            if(Integer.parseInt(status) == GinkgoNode.BKGEGU) {
		            	TreeNode[] path = treeChild.getPath();
		            	String chanyelianstring = "";
		            	String curbankuai = path[1].toString();
		            	for(int i=1;i<path.length-1;i++)
		            		chanyelianstring = chanyelianstring + path[i].toString() + "-";
		            	chanyelianstring = chanyelianstring.substring(0, chanyelianstring.length()-1);
		            	
		            	String stockcode = tmpname.substring(0, 6);
			            String stockname = tmpname.substring(6, tmpname.length());
		            	String xpath = ".//gegu[@stockcode=\"" + stockcode + "\"]";
		            	 Node tmpnode = ggcyldoc.selectSingleNode(xpath);
		            	 if(tmpnode != null) { //已经存在的
		            		 Element tmpitemele = ((Element)tmpnode).addElement("item");
		            		 tmpitemele.addAttribute("bankuai", curbankuai);
		            		 tmpitemele.setText(chanyelianstring);
		            		 
		            	 } else {
				             Element tmpggcylele = ggcyldoc.getRootElement().addElement("gegu" );
			    			 tmpggcylele.addAttribute("stockcode", stockcode  );
			    			 tmpggcylele.addAttribute("stockname", stockname  );
			    			 Element tmpnewitem = tmpggcylele.addElement("item");
			    			 tmpnewitem.addAttribute("bankuai", curbankuai);
			    			 tmpnewitem.setText(chanyelianstring);
		            	 }

		         }
		            
		         exportToDoc(treeChild,cylchildele,ggcyldoc);
	        }
		}
		
		private void refreshGeGuChanYeLianXml() 
		{
			String gegucylxmlfilepath = sysconfig.getGeGuChanYeLianXmlFile ();
			File sysconfigfile = new File(gegucylxmlfilepath );
			if(!sysconfigfile.exists()) { //不存在，创建该文件
				
				try {
					sysconfigfile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}
				
			FileInputStream xmlfileinput = null;
			try {
				xmlfileinput = new FileInputStream(gegucylxmlfilepath);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			try {
				SAXReader ggcylsaxreader = new SAXReader();
				//ggcylsaxreader.setEncoding("UTF-8");
				ggcylxmldocument = ggcylsaxreader.read(xmlfileinput );
				
				ggcylxmlroot = ggcylxmldocument.getRootElement();
//				Element obj = xmlroot.element("gegu600138");
//				//Element obj = (Element)xmlroot.get.element("gegu[@stockcode='600138']");  
//				 List list = obj.elements(); // 得到database元素下的子元素集合  
//				 System.out.println(list);
				 
			} catch (DocumentException e) {
				e.printStackTrace();
				return;
			 } catch (Exception e) {  
		            e.printStackTrace();  
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
