package com.exchangeinfomanager.bankuaifengxi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.exchangeinfomanager.systemconfigration.SystemConfigration;


public class BanKuaiFengXiRemindXmlHandler 
{
	private SystemConfigration sysconfig;

	public BanKuaiFengXiRemindXmlHandler ()
	{
		this.sysconfig = SystemConfigration.getInstance();
		
		getSettingInfoFromXML();
	}
	
	String bankuairemind = "";
	String bankuaicolumnremind = "";
	String stockremind = "";
	String stockcolumnremind = "";
	
	public String getBankuairemind() {
		return bankuairemind;
	}

	public String getBankuaicolumnremind() {
		return bankuaicolumnremind;
	}

	public String getStockremind() {
		return stockremind;
	}

	public String getStockcolumnremind() {
		return stockcolumnremind;
	}
	
	private void getSettingInfoFromXML() 
	{
		Document document;
    	Element xmlroot ;
    	SAXReader reader ;
    	
    	FileInputStream xmlfileinput = null;
		try {
			xmlfileinput = new FileInputStream(this.sysconfig.getBanKuaiFengXiChecklistXmlFile() );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		reader = new SAXReader();
	
		try {
			document = reader.read(xmlfileinput);
			xmlroot = document.getRootElement();
			
			Element BanKuaiRemind = xmlroot.element("BanKuaiRemind");
			Iterator it = BanKuaiRemind.elementIterator();
			while (it.hasNext()) {
				 Element elementdbs = (Element) it.next();
				 String id = elementdbs.attributeValue("id");
				 String bkr = elementdbs.getText();
				 bankuairemind = bankuairemind + id + ":" + bkr + "\r\n";
			}
			
			Element BanKuaiColumnRemind = xmlroot.element("BanKuaiColumnRemind");
			it = BanKuaiColumnRemind.elementIterator();
			while (it.hasNext()) {
				 Element elementdbs = (Element) it.next();
				 String id = elementdbs.attributeValue("id");
				 String bkrc = elementdbs.getText();
				 bankuaicolumnremind = bankuaicolumnremind + id + ":"+ bkrc + "\r\n";
			}
		
			Element StockRemind = xmlroot.element("StockRemind");
			it = StockRemind.elementIterator();
			while (it.hasNext()) {
				 Element elementdbs = (Element) it.next();
				 String id = elementdbs.attributeValue("id");
				 String bkrc = elementdbs.getText();
				 stockremind = stockremind + id + ":"+ bkrc + "\r\n";
			}
			
			Element StockColumnRemind = xmlroot.element("StockColumnRemind");
			it = StockColumnRemind.elementIterator();
			while (it.hasNext()) {
				 Element elementdbs = (Element) it.next();
				 String id = elementdbs.attributeValue("id");
				 String bkrc = elementdbs.getText();
				 stockcolumnremind = stockcolumnremind + id + ":"+ bkrc + "\r\n";
			}
			
			try {
				xmlfileinput.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (DocumentException e) {
		}
	}


}
