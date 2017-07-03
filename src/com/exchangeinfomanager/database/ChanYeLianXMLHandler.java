package com.exchangeinfomanager.database;

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
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;

import net.ginkgo.dom4jcopy.GinkgoNode;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class ChanYeLianXMLHandler 
{
	public ChanYeLianXMLHandler(BanKuaiDbOperation bkopt1) 
	{
		initialzieSysconf ();
		bankuaichanyelianxml = sysconfig.getBanKuaiChanYeLianXml ();
		geguchanyelianxml = sysconfig.getGeGuChanYeLianXmlFile ();
		this.bkopt = bkopt1;
		
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

	
	private void initialzieSysconf ()
	{
		sysconfig = SystemConfigration.getInstance();
	}
	public  GinkgoNode getBkChanYeLianXMLTree()
	{
		if(this.isXmlShouldUpdated() )
			this.updateBkCylXmlFileAfterDbChanged ();
		
		GinkgoNode topNode = new GinkgoNode("TongDaXinBanKuaiAndZhiShu");
		if(cylxmlroot != null) {
			importFromXML(topNode,cylxmlroot);
		} else {
			System.out.println(bankuaichanyelianxml+ "���ڴ���");
		}
		return topNode;
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
		System.out.println(calfilemdf.getTime() );
		
		Date bklastmodifiedtimeindb = bkopt.getTDXRelatedTableLastModfiedTime ();
		Calendar  caldbmdf =  Calendar.getInstance();
		caldbmdf.setTime(bklastmodifiedtimeindb);
		System.out.println(caldbmdf.getTime() );
		
//		return true;//������
		if(caldbmdf.after(calfilemdf)) {
			System.out.println("���ݿ��Ѿ����£���ʼ����XML");
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
			   String bkname = element.attributeValue("suoshubkcode");
			   
			   bkcylxmlset.add(bkname);
		 }
		 
		 return bkcylxmlset;
	}
	/*
	 * �����ݿ��еİ����Ϣ���浽XML��
	 */
	private void updateBkCylXmlFileAfterDbChanged ()
	{
		
		HashMap<String, BanKuai> tdxbk = bkopt.getTDXBanKuaiList ();
		HashMap<String, BanKuai> tdxzhishu = bkopt.getTDXAllZhiShuList ();
		Set<String> tdxbkset = tdxbk.keySet();
		Set<String> tdxzhishuset = tdxzhishu.keySet();
		SetView<String> uniontdxbkindb = Sets.union(tdxbkset, tdxzhishuset );
		 
		Set<String> bkcylxmlset = this.getTDXBanKuaiSetInCylXml ();
		
		SetView<String> differencebankuainew = Sets.difference(uniontdxbkindb, bkcylxmlset );
        for (String newbk : differencebankuainew) {
			//��XML �м����µİ�� //<BanKuai bkname="���ٹ�·" Status="4">
//			String xpathnew = ".//BanKuai[@bkname=\"" + newbk + "\"]";
//			Node tmpnode = cylxmldoc.selectSingleNode(xpathnew);
				Element newele = cylxmlroot.addElement("BanKuai"); //��XML �м����µİ��
				String bkname = null;
				try {
					bkname = tdxbk.get(newbk).getBankuainame();
				} catch (java.lang.NullPointerException e ) { //
					//e.printStackTrace();
					bkname = tdxzhishu.get(newbk).getBankuainame();
				} 
				newele.addAttribute("bkname", bkname);
				newele.addAttribute("suoshubkcode", newbk);
				newele.addAttribute("Type", "4");
				System.out.println("XML�м���" + newbk + bkname);
        }
        
        SetView<String> differencebankuaiold = Sets.difference(bkcylxmlset, uniontdxbkindb );
	    for (String oldbk : differencebankuaiold) {
			//�Ӳ�ҵ��XML��ɾ���ð��
			try {
				String xpath = ".//BanKuai[@bkname=\"" + oldbk + "\"]";
				Node tmpnode = cylxmldoc.selectSingleNode(xpath);
				cylxmlroot.remove(tmpnode);
				System.out.println("��ҵ��XML��ɾ��" + oldbk);
			} catch (java.lang.NullPointerException ex) {
				
			}
			
			//�Ӹ��ɲ�ҵ��XML��ɾ���ð�� //  <bankuai bkname="���ܴ���">���ܴ���</bankuai>
			try {
				String xpath = ".//gegu/bankuai[@bkname=\"" + oldbk + "\"]";
				Node tmpnodegg = ggcylxmldocument.selectSingleNode(xpath);
				ggcylxmlroot.remove(tmpnodegg);
				//xmldoc.remove(tmpnode);
				System.out.println("���ɲ�ҵ��XML��ɾ��" + oldbk);
			} catch (java.lang.NullPointerException ex) {
			}
	    }
	    
	    OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");    // ָ��XML����        
		//�����ҵ��XML
		try {
			XMLWriter writer = new XMLWriter(new FileOutputStream(bankuaichanyelianxml),format); // ���ȫ��ԭʼ���ݣ������������µ�������Ҫ��XML�ļ�  
				writer.write(cylxmldoc); //������ļ�  
				//writer.flush();
				writer.close();
		} catch (IOException e) {
				e.printStackTrace();
		}
		//������ɲ�ҵ��XML		
		try {
			XMLWriter writer = new XMLWriter(new FileOutputStream(geguchanyelianxml),format); // ���ȫ��ԭʼ���ݣ������������µ�������Ҫ��XML�ļ�  
			writer.write(ggcylxmldocument); //������ļ�  
			//writer.flush();
			writer.close();
			
		} catch (IOException e) {
				e.printStackTrace();
		}
		
		hasXmlRevised = true;
	}
	
	
	
	 private void importFromXML(GinkgoNode parentNode, Element xmlelement) 
	 {
	    	Iterator it = xmlelement.elementIterator();
	    	 while (it.hasNext()) 
			 {
				   Element element = (Element) it.next();
				   
				   GinkgoNode parentsleaf = null;

				   //��֪�������������
				   //System.out.println("id: " + element.attributeValue("Subject")); //ͨ���Ű�����ƻ��߸������ƻ��Ӱ������
				   //System.out.println("id: " + element.attributeValue("Status")); //����ǰ�黹�Ǹ���
			   
				   String bkname = null;
				   if(element.attributeValue("Type").trim().equals("4") ) //��ͨ���Ű��
					   bkname = element.attributeValue("bkname");
				   else if(element.attributeValue("Type").trim().equals("5") ) //���Զ����Ӱ��
					   bkname = element.attributeValue("subbkname");
				   else if(element.attributeValue("Type").trim().equals("6") ) //���Զ����Ӱ��
					   bkname = element.attributeValue("geguname");
				   parentsleaf = new GinkgoNode( bkname);
				   
				   String bkcode = element.attributeValue("suoshubkcode"); //���нڵ㶼�����������İ��code������ʶ�������ĸ�����µĽڵ�
				   parentsleaf.setTDXBanKuaiZhiShuCode (bkcode);
				   parentsleaf.setStatus( Integer.parseInt(element.attributeValue("Type") ) );
				   parentsleaf.setHanYuPingYin(getBanKuaiNameOfPinYin(bkname ) );
				   parentNode.add(parentsleaf);

				   importFromXML(parentsleaf,element);
			}
	}
	
	 private String getBanKuaiNameOfPinYin (String chinese)
	 {
		 //System.out.println(chinese);
		 //�ⲿ�ֻ��ȫ��ƴ��
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
	       
	       //�ⲿ�ֻ������ĸƴ��
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
            
            return pybf.toString().replaceAll("\\W", "").trim(); //��������ĸƴ��
	 }
	 
	 public boolean saveTreeToChanYeLianXML (GinkgoNode ginkgorootnode) //GEN-FIRST:event_saveButtonActionPerformed 
     {
			//��ҵ��XML
			Document cyldocument = DocumentFactory.getInstance().createDocument();
	        Element cylrootele = cyldocument.addElement(ginkgorootnode.getUserObject().toString());//����ĵ���
	        //���ɲ�ҵ��XML
	        Document gegucyldocument = DocumentFactory.getInstance().createDocument();
	        Element gegucylrootele = gegucyldocument.addElement("geguchanyelian");//����ĵ��� 
	        
	        exportToDoc (ginkgorootnode,cylrootele,gegucyldocument);
	    	
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GBK");    // ָ��XML����        
			//�����ҵ��XML
			try {
				XMLWriter writer = new XMLWriter(new FileOutputStream(bankuaichanyelianxml),format); // ���ȫ��ԭʼ���ݣ������������µ�������Ҫ��XML�ļ�  
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
		private void exportToDoc (GinkgoNode parentnode,Element cylparentele, Document ggcyldoc)
		{
			GinkgoNode treeChild;
			
			for (Enumeration<GinkgoNode> child = parentnode.children(); child.hasMoreElements();) {
	            treeChild = (GinkgoNode) child.nextElement();
            	String tmpname = treeChild.getUserObject().toString();
            	String tmpbkcode = treeChild.getTDXBanKuaiZhiShuCode() ;
//	            //System.out.println(tmpname);
//	            Element cylchildele = cylparentele.addElement("BanKuai");
//	            cylchildele.addAttribute("bkname", tmpname  );
		            
		        String status = String.valueOf(treeChild.getStatus());
		        Element cylchildele = null;
		        if(Integer.parseInt(status) == GinkgoNode.TDXBK) {
		        	cylchildele = cylparentele.addElement("BanKuai");
		            cylchildele.addAttribute("bkname", tmpname  );
		        }
		        if(Integer.parseInt(status) == GinkgoNode.SUBBK) {
		        	cylchildele = cylparentele.addElement("SubBanKuai");
		        	cylchildele.addAttribute("subbkname", tmpname  );
		        }
		      
	            if(Integer.parseInt(status) == GinkgoNode.BKGEGU) {
	            	cylchildele = cylparentele.addElement("gegu");
		        	cylchildele.addAttribute("geguname", tmpname  );
	            	
		        	  //����Ǹ��ɣ�Ҫ���浽���ɲ�ҵ��XML��  	
		            	TreeNode[] path = treeChild.getPath();
		            	String chanyelianstring = "";
		            	String curbankuai = path[1].toString(); //�������
		            
		            	
		            	for(int i=1;i<path.length-1;i++) //�Ѳ�ҵ�����ϳ�һ���ַ���
		            		chanyelianstring = chanyelianstring + path[i].toString() + "-";
		            	chanyelianstring = chanyelianstring.substring(0, chanyelianstring.length()-1);
		            	
		            	String stockcode = tmpname.substring(0, 6);
			            String stockname = tmpname.substring(6, tmpname.length());
		            	String xpath = ".//gegu[@stockcode=\"" + stockcode + "\"]";
		            	 Node tmpnode = ggcyldoc.selectSingleNode(xpath);
		            	 if(tmpnode != null) { //�Ѿ����ڵ�
		            		 Element tmpitemele = ((Element)tmpnode).addElement("chanyelian");
		            		 tmpitemele.addAttribute("bankuai", curbankuai);
		            		 tmpitemele.addAttribute("suoshubkcode", tmpbkcode);
		            		 tmpitemele.setText(chanyelianstring);
		            		 
		            	 } else {
				             Element tmpggcylele = ggcyldoc.getRootElement().addElement("gegu" );
			    			 tmpggcylele.addAttribute("stockcode", stockcode  );
			    			 tmpggcylele.addAttribute("stockname", stockname  );
			    			 Element tmpnewitem = tmpggcylele.addElement("chanyelian");
			    			 tmpnewitem.addAttribute("bankuai", curbankuai);
			    			 tmpnewitem.addAttribute("suoshubkcode", tmpbkcode);
			    			 tmpnewitem.setText(chanyelianstring);
		            	 }

		         }
		         
	            cylchildele.addAttribute("suoshubkcode", tmpbkcode  );
	            cylchildele.addAttribute("Type", status);
	            
		         exportToDoc(treeChild,cylchildele,ggcyldoc);
	        }
		}
		
//		public void gethGeGuChanYeLianXml() 
//		{
////			String gegucylxmlfilepath = sysconfig.getGeGuChanYeLianXmlFile ();
////			File sysconfigfile = new File(gegucylxmlfilepath );
////			if(!sysconfigfile.exists()) { //�����ڣ��������ļ�
////				
////				try {
////					sysconfigfile.createNewFile();
////				} catch (IOException e) {
////					e.printStackTrace();
////				}
////				return;
////			}
////				
////			FileInputStream xmlfileinput = null;
////			try {
////				xmlfileinput = new FileInputStream(gegucylxmlfilepath);
////			} catch (FileNotFoundException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
////		
////			try {
////				SAXReader ggcylsaxreader = new SAXReader();
////				//ggcylsaxreader.setEncoding("UTF-8");
////				ggcylxmldocument = ggcylsaxreader.read(xmlfileinput );
////				
////				ggcylxmlroot = ggcylxmldocument.getRootElement();
//////				Element obj = xmlroot.element("gegu600138");
//////				//Element obj = (Element)xmlroot.get.element("gegu[@stockcode='600138']");  
//////				 List list = obj.elements(); // �õ�databaseԪ���µ���Ԫ�ؼ���  
//////				 System.out.println(list);
////				 
////			} catch (DocumentException e) {
////				e.printStackTrace();
////				return;
////			 } catch (Exception e) {  
////		            e.printStackTrace();  
////		     }  
//			
//		}
		
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
