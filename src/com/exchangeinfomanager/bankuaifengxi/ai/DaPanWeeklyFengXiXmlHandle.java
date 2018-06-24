package com.exchangeinfomanager.bankuaifengxi.ai;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.GuPiaoChi;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.SubBanKuai;
import com.exchangeinfomanager.asinglestockinfo.SubGuPiaoChi;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

class DaPanWeeklyFengXiXmlHandler 
{

	private SystemConfigration sysconfig;
	private BanKuaiDbOperation bkopt;
	private Document xmldoc;
	private Element xmlroot;
	private String dpfxweeklyxmlmatrixfile;
	private SAXReader reader;
	private ZhongDianGuanZhu zdgzinfo;

	public DaPanWeeklyFengXiXmlHandler(LocalDate date) 
	{
		this.sysconfig = SystemConfigration.getInstance();
		this.bkopt = new BanKuaiDbOperation ();
		this.zdgzinfo = new ZhongDianGuanZhu ();
		
		reader = new SAXReader();
		
		String text = this.getStockBanKuaiZdgz(date);
		if( text != null) {
			this.dpfxweeklyxmlmatrixfile = sysconfig.getDaPanFengXiWeeklyXmlMatrixFile ();
			
			FileInputStream xmlfileinput = null;
			try {
				xmlfileinput = new FileInputStream(dpfxweeklyxmlmatrixfile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			try {
				xmldoc = reader.read(xmlfileinput);
				xmlroot = xmldoc.getRootElement();
				
				createZdgzFromXml (xmlroot,text);
			} catch (DocumentException e) {
				e.printStackTrace();
				return;
			}
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
	private String getStockBanKuaiZdgz(LocalDate date) 
	{
		String zdgzinfo = bkopt.getZdgzInfo ("999999",date);
		
		if(zdgzinfo.isEmpty())
			return null;
		
		try {
//			xmldoc = reader.read(zdgzinfo);
			xmldoc = DocumentHelper.parseText(zdgzinfo);
			xmlroot = xmldoc.getRootElement();
			
			createZdgzFromXml (xmlroot,null);
		} catch (DocumentException e) {
			e.printStackTrace(); //说明不是XML，是原来遗留的,但对999999应该没有，以前没有过
			return zdgzinfo;
		}
		
		return null;
	}
	/*
	 * 
	 */
	private void createZdgzFromXml(Element xmlroot,String extracomments) 
	{
		if(zdgzinfo == null)
			zdgzinfo = new ZhongDianGuanZhu ();
		
		Element eleply = xmlroot.element("GovPolicy");
		Iterator it = eleply.elementIterator();
		ArrayList<ZdgzItem> govpolicy = new ArrayList<ZdgzItem> ();
	   	while (it.hasNext() ) 
	   	{
	   		Element element = (Element) it.next();
	   		
	   		String id = element.attributeValue("id").trim();
	   		ZdgzItem zdgzitem = new ZdgzItem(id);
	   		
	   		try {
		   		String selectedcolor =  element.attributeValue("selectedcolor").trim();
		   		zdgzitem.setSelectedcolor(selectedcolor);
	   		} catch (java.lang.NullPointerException e) {
	   			
	   		}
	   		
	   		try {
		   		String vaule = element.attributeValue("value").trim();
		   		zdgzitem.setValue(vaule);
		   	} catch (java.lang.NullPointerException e) {
	   			
	   		}
	   		
	   		try {
		   		String text = element.getText();
		   		zdgzitem.setContents(text);
		   	} catch (java.lang.NullPointerException e) {
	   			
	   		}
	   		
	   		govpolicy.add(zdgzitem);
	   	}
	   	
	   	zdgzinfo.setGovpolicy(govpolicy);
	   	
	   	Element eledapan = xmlroot.element("DaPan");
		Iterator itdapan = eledapan.elementIterator();
		ArrayList<String> dapan = new ArrayList<String> ();
	   	while (itdapan.hasNext() ) 
	   	{
	   		Element element = (Element) itdapan.next();
	   		
	   		String id = element.attributeValue("id").trim();

	   		try {
		   		String vaule = element.attributeValue("value").trim();
		   		dapan.add(vaule);
			} catch (java.lang.NullPointerException e) {
	   			
	   		}
	   	}
	   	
	   	zdgzinfo.setDapan(dapan);
	   	
		Element elecomments = xmlroot.element("AnalysisComments");
		Iterator itcomments = elecomments.elementIterator();
	   	while (itcomments.hasNext() ) 
	   	{
	   		Element element = (Element) itcomments.next();
	   		
	   		String id = element.attributeValue("id").trim();

		   	try {	
		   		String text = element.getText();
		   		if(extracomments != null)
		   			text = text + extracomments;
		   		zdgzinfo.setAnalysisComments (text);
			} catch (java.lang.NullPointerException e) {
	   			
	   		}
	   	}
	}

}
