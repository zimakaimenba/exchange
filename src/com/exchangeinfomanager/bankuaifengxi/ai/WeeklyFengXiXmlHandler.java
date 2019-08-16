package com.exchangeinfomanager.bankuaifengxi.ai;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Strings;

public abstract class WeeklyFengXiXmlHandler 
{

	public WeeklyFengXiXmlHandler(String nodecode, LocalDate date) 
	{
		this.currentdate = date;
		this.curnodecode = nodecode;
		this.sysconfig = SystemConfigration.getInstance();
		this.bkopt = new BanKuaiDbOperation ();
		this.zdgzinfo = new ZhongDianGuanZhu ();
		
		reader = new SAXReader();
		
	}

	public static final String XMLINDB_PROPERTY = "XMLINDB_CHANGED";
	public static final String XMLMATRIX_PROPERTY = "XMLMATRIX_CHANGED";
	public static final String XMLINDB_ADDED = "XMLINDB_ADDED";
	public static final String XMLMATRIX_ADDED = "XMLMATRIX_ADDED";
	public static final String XMLINDB_RMVED = "XMLINDB_RMVED";
	public static final String XMLMATRIX_RMVED = "XMLMATRIX_RMVED";
	
	protected SAXReader reader;
	protected ZhongDianGuanZhu zdgzinfo;
//	protected boolean hasxmlindatabase = false;
	protected LocalDate currentdate;
	protected Document xmlindbdoc;
	protected Element xmlindbroot;
	protected SystemConfigration sysconfig;
	protected BanKuaiDbOperation bkopt;
	protected String fxweeklyxmlmatrixfile;
	protected String curnodecode;
	protected Document matrixfilexmldoc;
	protected Element matrixfilexmlroot;
	
	private boolean xmlindbchanged;
	private boolean xmlmatrixfilechanged;
	
	/*
	 * 
	 */
	public void setXmlindbchanged (boolean changed)
	{
		this.xmlindbchanged = changed;
	}
	public boolean isXmlindbchanged ()
	{
		return this.xmlindbchanged;
	}
	public void setXmlmatrixfilechanged (boolean changed)
	{
		this.xmlmatrixfilechanged = changed;
	}
	public boolean isXmlmatrixfilechanged ()
	{
		return this.xmlmatrixfilechanged;
	}
	/*
	 * 
	 */
	protected void setupXmlHandler (String xmlmatrixfilepath)
	{
		//先建立matrix的
		FileInputStream xmlmatrixfileinput = null;
		try {
			fxweeklyxmlmatrixfile = xmlmatrixfilepath;
			xmlmatrixfileinput = new FileInputStream(fxweeklyxmlmatrixfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try { //不管用户是从FILE还是从数据库中读出XML的，都重新为martrix xml 生成新的doc/root，应为用户可能已经更改了XML DOC，而martix xml不需要保留用户的更改信息
			matrixfilexmldoc = reader.read(xmlmatrixfileinput);
			matrixfilexmlroot = matrixfilexmldoc.getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
			return;
		}
		
		String zdgzinfo = null;
		if (curnodecode != null)
			zdgzinfo = bkopt.getBanKuaiOrStockZdgzInfo (curnodecode,currentdate);
		
		if(!Strings.isNullOrEmpty(zdgzinfo)  ) {
			try {
				xmlindbdoc = DocumentHelper.parseText(zdgzinfo.trim());
				xmlindbroot = xmlindbdoc.getRootElement();
				
				createZdgzFromXml (xmlindbroot,null);
			} catch (DocumentException e) {
//				e.printStackTrace(); //说明不是XML，是原来遗留的,但对999999应该没有，以前没有过
				xmlindbdoc = matrixfilexmldoc;
				xmlindbroot = matrixfilexmlroot;
				
				createZdgzFromXml (xmlindbroot,zdgzinfo);
			}
		} else { //说明数据库原来没有
			xmlindbdoc = matrixfilexmldoc;
			xmlindbroot = matrixfilexmlroot;
			
			createZdgzFromXml (xmlindbroot,null);
		}
	}
	/*
	 * 
	 */
	public ZhongDianGuanZhu getZhongDianGuanZhu ()
	{
		return this.zdgzinfo;
	}
	/*
	 * 
	 */
	abstract  void createZdgzFromXml (Element xmlroot,String extracomments);
	/*
	 * 
	 */
	public void saveNodeFenXiXml() 
	{
		Document document = DocumentFactory.getInstance().createDocument();
        Element rootele = document.addElement("Checklists");//添加文档根
        
        setupXmlContentsFromGui (rootele,"Node"); 
        
        String xmlcontents = document.asXML();
        bkopt.updateBanKuaiOrStockZdgzInfo (curnodecode,currentdate,xmlcontents);
	}
	/*
	 * 
	 */
	public void saveFenXiXmlMatrixFile() 
	{
		Document document = DocumentFactory.getInstance().createDocument();
        Element rootele = document.addElement("Checklists");//添加文档根
        
        setupXmlContentsFromGui (rootele,"Matrix");
        
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");    // 指定XML编码        
		//保存产业链XML
		try {
			XMLWriter writer = new XMLWriter(new FileOutputStream(fxweeklyxmlmatrixfile),format); // 输出全部原始数据，并用它生成新的我们需要的XML文件  
			writer.write(document); //输出到文件  
			//writer.flush();
			writer.close();
		} catch (IOException e) {
				e.printStackTrace();
		}
		
	}
	/*
	 * 
	 */
	abstract void setupXmlContentsFromGui(Element rootele, String fortype) ;
	/*
	 * 
	 */
	protected ArrayList<ZdgzItem> getXmlElementInfo (Element element)
	{
		ArrayList<ZdgzItem> eleitem = new ArrayList<ZdgzItem> ();
		
		String xmltag = null;
		try {
			xmltag = element.getName();
		} catch (java.lang.NullPointerException e) {
			e.printStackTrace();
			return null;
		}
		
		Iterator itele = element.elementIterator();
	   	while (itele.hasNext() ) 
	   	{
	   		Element subelement = (Element) itele.next();
	   		
	   		String id = subelement.attributeValue("id").trim();
	   		
	   		ZdgzItem zdgzitem = new ZdgzItem(id,xmltag);

	   		try {
	   			Boolean selected = Boolean.parseBoolean(subelement.attributeValue("selected").trim() );
	   			zdgzitem.setItemSelected(selected);
	   		} catch (java.lang.NullPointerException e) {
	   			
	   		}
	   		try {
		   		String vaule = subelement.attributeValue("value").trim();
		   		zdgzitem.setValue(vaule);
			} catch (java.lang.NullPointerException e) {
	   			
	   		}
	   		
	   		try {
		   		String selectedcolor =  subelement.attributeValue("selectedcolor").trim();
		   		zdgzitem.setSelectedcolor(selectedcolor);
	   		} catch (java.lang.NullPointerException e) {
	   			
	   		}
	   		
	   		String contents = subelement.getText();
	   		zdgzitem.setContents(contents);
	   		
	   		eleitem.add(zdgzitem);
	   	}
	   	
	   	return eleitem;
	}
	/*
	 * fortype : 现在系统如果要把信息保存到matrix file，则不保存用户的选择和value
	 */
	protected void createFenXiXmlItems (Element eleadded, ArrayList<ZdgzItem> zdgzitems, String fortype)
	{
		for(ZdgzItem zdgzitem : zdgzitems) {
        	Element eleitem = eleadded.addElement("item");
        	
        	String id = zdgzitem.getId();
        	eleitem.addAttribute("id", id);
        	
        	if(!fortype.toLowerCase().equals("matrix")) {
        		Boolean selected = zdgzitem.isSelected();
            	eleitem.addAttribute("selected",String.valueOf(selected));
        	}
        	
        	String eleaddedtext = eleadded.getName();
        	if( !fortype.toLowerCase().equals("matrix") || eleaddedtext.equals("DaPan") ) { //对于大盘指数来说，指数ID是存在VALUE里面的，所以要保存
        		String value = zdgzitem.getValue();
            	if(!Strings.isNullOrEmpty(value))
            		eleitem.addAttribute("value",value);
        	}
           	        	
        	String contents = zdgzitem.getContents();
        	if(!Strings.isNullOrEmpty(contents))
        		eleitem.setText(contents);
        	
        	String selectedcolor = zdgzitem.getSelectedcolor();
        	if(!Strings.isNullOrEmpty(selectedcolor))
        	      eleitem.addAttribute("selectedcolor",selectedcolor);
        }
	}
	/*
	 * 
	 */
//	public void addNewZdgzItemToMatrixFile(ZdgzItem zdgzitem) 
//	{
//		
//		Element nodeadded;
//		try { //不管用户是从FILE还是从数据库中读出XML的，都重新为martrix xml 生成新的doc/root，应为用户可能已经更改了XML DOC，而martix xml不需要保留用户的更改信息
//			String xmltag = zdgzitem.getXmlTagBelonged();
//			
//			String xpath = ".//" + xmltag ;
//			nodeadded = (Element)matrixfilexmldoc.selectSingleNode(xpath);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
//		
//		
//		ArrayList<ZdgzItem> tmpalist = new ArrayList<ZdgzItem> ();
//		tmpalist.add(zdgzitem);
//		createFenXiXmlItems (nodeadded,tmpalist);
//		
//	}

//	public void addNewZdgzItemToMatrixFileBasic(ZdgzItem zdgzitem) 
//	{
//		FileInputStream xmlfileinput = null;
//		try {
//			xmlfileinput = new FileInputStream(fxweeklyxmlmatrixfile);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		
//		Element nodeadded;
//		try { //不管用户是从FILE还是从数据库中读出XML的，都重新为martrix xml 生成新的doc/root，应为用户可能已经更改了XML DOC，而martix xml不需要保留用户的更改信息
//			matrixfilexmldoc = reader.read(xmlfileinput);
//			matrixfilexmlroot = xmldoc.getRootElement();
//			
//			String xmltag = zdgzitem.getXmlTagBelonged();
//			
//			String xpath = ".//" + xmltag ;
//			nodeadded = (Element)matrixfilexmldoc.selectSingleNode(xpath);
//
//		} catch (DocumentException e) {
//			e.printStackTrace();
//			return;
//		}
//		
//		
//		ArrayList<ZdgzItem> tmpalist = new ArrayList<ZdgzItem> ();
//		tmpalist.add(zdgzitem);
//		createFenXiXmlItems (nodeadded,tmpalist);
//		
//	}


}
