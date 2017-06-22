package com.exchangeinfomanager.checkboxtree;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class CheckBoxTreeXmlHandler 
{
	private FileInputStream xmlfile = null;
	static Element xmlroot = null;
	Document document = null;
	SAXReader reader = null;
	Iterator it = null;
	CheckBoxTreeNode xmltreeroot;

	public  CheckBoxTreeXmlHandler(String xmlfilepath) 
	{
		FileInputStream xmlfileinput = null;
		try {
			xmlfileinput = new FileInputStream(xmlfilepath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		this.xmlfile = xmlfileinput;
		reader = new SAXReader();
	
		try {
			document = reader.read(xmlfile);
			xmlroot = document.getRootElement();
			xmltreeroot = initCheckBoxTree();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null,xmlfilepath+ "存在错误，请修改");
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * @return the xmltreeroot
	 */
	public CheckBoxTreeNode getXmltreeroot() {
		return xmltreeroot;
	}
	
	//public static CheckBoxTree initCheckBoxTree()
	private static CheckBoxTreeNode initCheckBoxTree()
	{
		 Iterator it = xmlroot.elementIterator();
		 System.out.print(it);
		 CheckBoxTreeNode xmltreeroot = new CheckBoxTreeNode("Root");
		 
		 while (it.hasNext()) 
		 {
			   Element element = (Element) it.next();

			   //未知属性名称情况下
			   Iterator attrIt = element.attributeIterator();
			   
			   CheckBoxTreeNode parentsleaf = null;
			   while (attrIt.hasNext()) 
			   {
			    Attribute a  = (Attribute) attrIt.next();
			    //System.out.println(a.getValue());
			    parentsleaf = new CheckBoxTreeNode(a.getValue());
			    //root.add(new CheckBoxTreeNode(a.getValue()));
			   }

			   //已知属性名称情况下
			   //System.out.println("id: " + element.attributeValue("id"));

			   //未知元素名情况下
			   Iterator eleIt = element.elementIterator();
			   
			   
			   while (eleIt.hasNext()) 
			   {
				   Element e = (Element) eleIt.next();
				   
				   CheckBoxTreeNode tmpchktrn = new CheckBoxTreeNode(e.getText().trim());
				   parentsleaf.add(tmpchktrn);
				   
				   if(e.attribute("id") != null)
					   tmpchktrn.setXmltag( e.attribute("id").getText() );
				   if(e.attribute("selectedcolor") != null )
					   tmpchktrn.setXmlattrsltedcolor( e.attribute("selectedcolor").getText().trim() );   
				   //System.out.println(e.getName() + ": " + e.getText());
				   //root.add(new CheckBoxTreeNode(e.getText()));
			   }
			   //System.out.println();
			   xmltreeroot.add(parentsleaf);

			   //已知元素名情况下
//			   System.out.println("title: " + element.elementText("title"));
//			   System.out.println("author: " + element.elementText("author"));
//			   System.out.println();
		}

		 return xmltreeroot;
		
	}

}
