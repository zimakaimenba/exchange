package com.exchangeinfomanager.bankuaifengxi.ai;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

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
	
	protected SAXReader reader;
	protected ZhongDianGuanZhu zdgzinfo;
	protected boolean hasxmlindatabase = false;
	protected LocalDate currentdate;
	protected Document xmldoc;
	protected Element xmlroot;
	protected SystemConfigration sysconfig;
	protected BanKuaiDbOperation bkopt;
	protected String fxweeklyxmlmatrixfile;
	protected String curnodecode;
	
	/*
	 * 
	 */
	protected String getStockBanKuaiZdgz(String nodecode, LocalDate date) 
	{
		String zdgzinfo = bkopt.getBanKuaiOrStockZdgzInfo (nodecode,date);
		
		if(zdgzinfo.isEmpty()) {
			hasxmlindatabase = false;
			return null;
		}
		
		try {
//			xmldoc = reader.read(zdgzinfo);
			xmldoc = DocumentHelper.parseText(zdgzinfo.trim());
			xmlroot = xmldoc.getRootElement();
			hasxmlindatabase = true;
		} catch (DocumentException e) {
//			e.printStackTrace(); //说明不是XML，是原来遗留的,但对999999应该没有，以前没有过
			hasxmlindatabase = false;
			return zdgzinfo;
		}
		
		return null;
	}
	/*
	 * 
	 */
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
	abstract  void createZdgzFromXml(Element xmlroot,String extracomments);
	/*
	 * 
	 */
	public void saveFenXiXml(ZhongDianGuanZhu zdgzinfo2) 
	{
		Document document = DocumentFactory.getInstance().createDocument();
        Element rootele = document.addElement("Checklists");//添加文档根
        
        setupXmlContentsFromGui (rootele); 
        
        String xmlcontents = document.asXML();
        bkopt.updateBanKuaiOrStockZdgzInfo (curnodecode,currentdate,xmlcontents);
	}
	/*
	 * 
	 */
	abstract void setupXmlContentsFromGui(Element rootele) ;
	/*
	 * 
	 */
	protected ArrayList<ZdgzItem> getXmlElementInfo (Element element)
	{
		ArrayList<ZdgzItem> eleitem = new ArrayList<ZdgzItem> ();
		
		Iterator itele = element.elementIterator();
	   	while (itele.hasNext() ) 
	   	{
	   		Element subelement = (Element) itele.next();
	   		
	   		String id = subelement.attributeValue("id").trim();
	   		ZdgzItem zdgzitem = new ZdgzItem(id);

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
	 * 
	 */
	protected void createFenXiXmlItems (Element eleadded, ArrayList<ZdgzItem> zdgzitems)
	{
		for(ZdgzItem zdgzitem : zdgzitems) {
        	Element eleitem = eleadded.addElement("item");
        	
        	String id = zdgzitem.getId();
        	eleitem.addAttribute("id", id);
        	
        	Boolean selected = zdgzitem.isSelected();
        	eleitem.addAttribute("selected",String.valueOf(selected));
        	
        	String value = zdgzitem.getValue();
        	if(!Strings.isNullOrEmpty(value))
        		eleitem.addAttribute("value",value);
        			
        	String contents = zdgzitem.getContents();
        	if(!Strings.isNullOrEmpty(contents))
        		eleitem.setText(contents);
        	
        	String selectedcolor = zdgzitem.getSelectedcolor();
        	if(!Strings.isNullOrEmpty(selectedcolor))
        	      eleitem.addAttribute("selectedcolor",selectedcolor);
        }
	}


}
