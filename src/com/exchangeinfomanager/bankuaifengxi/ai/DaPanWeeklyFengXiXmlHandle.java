package com.exchangeinfomanager.bankuaifengxi.ai;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
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
import com.google.common.base.Strings;

class DaPanWeeklyFengXiXmlHandler extends WeeklyFengXiXmlHandler
{
	public DaPanWeeklyFengXiXmlHandler(String nodecode ,LocalDate date) 
	{
		super (nodecode,date);
		super.setupXmlHandler (sysconfig.getDaPanFengXiWeeklyXmlMatrixFile ());
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.bankuaifengxi.ai.WeeklyFengXiXmlHandler#createZdgzFromXml(org.dom4j.Element, java.lang.String)
	 */
	protected void createZdgzFromXml(Element xmlroot,String extracomments) 
	{
		if(zdgzinfo == null)
			zdgzinfo = new ZhongDianGuanZhu ();
		
		getGovPolicy (xmlroot);
		getDaPanZhiShu (xmlroot);
		getAnalysisComments (xmlroot,extracomments);
	}
	/*
	 * 
	 */
	private void getDaPanZhiShu (Element xmlroot)
	{
		Element eledapan = xmlroot.element("DaPan");
	   	ArrayList<ZdgzItem> dapan = getXmlElementInfo (eledapan);
	   	zdgzinfo.setDapanZhiShuLists(dapan);
	}
	/*
	 * 
	 */
	private void getAnalysisComments(Element xmlroot, String extracomments) 
	{
		Element elecomments = xmlroot.element("AnalysisComments");
		Iterator itcomments = elecomments.elementIterator();
	   	while (itcomments.hasNext() ) 
	   	{
	   		Element element = (Element) itcomments.next();
	   		
	   		String id = element.attributeValue("id").trim().toUpperCase();

		   	try {	
		   		String text = element.getText();
		   		if(extracomments != null)
		   			text = text + extracomments;
		   		zdgzinfo.setDaPanAnalysisComments (text);
			} catch (java.lang.NullPointerException e) {
	   			
	   		}
	   	}
		
	}
	/*
	 * 
	 */
	private void getGovPolicy (Element xmlroot)
	{
		Element eleply = xmlroot.element("GovPolicy");
		ArrayList<ZdgzItem> govpolicy = getXmlElementInfo (eleply);
	   	zdgzinfo.setGovpolicy(govpolicy);
	}


	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.bankuaifengxi.ai.WeeklyFengXiXmlHandler#setupXmlContentsFromGui(org.dom4j.Element)
	 */
	void setupXmlContentsFromGui(Element rootele,String fortype) 
	{
		setGovPolicy (rootele,fortype);
        setDaPanZhiShu(rootele,fortype);
        setAnalysisComments(rootele,fortype);
	}
	/*
	 * 
	 */
	private void setGovPolicy (Element xmlroot, String fortype) 
	{
		Element elegvply = xmlroot.addElement("GovPolicy");
        elegvply.addAttribute("value", "政策面");
        ArrayList<ZdgzItem> govpolicy = zdgzinfo.getGovpolicy();
        createFenXiXmlItems(elegvply, govpolicy,fortype);
	}
	/*
	 * 
	 */
	private void setDaPanZhiShu (Element rootele, String fortype) 
	{
		 	Element eledapan = rootele.addElement("DaPan");
	        eledapan.addAttribute("value", "指数分析");
	        ArrayList<ZdgzItem> zhishulist = zdgzinfo.getDapanZhiShuLists();
	        createFenXiXmlItems(eledapan,zhishulist,fortype);
	}
	private void setAnalysisComments (Element rootele, String fortype) 
	{
		Element elecomments = rootele.addElement("AnalysisComments");
        elecomments.addAttribute("value", "本周总结");
        Element eleitem = elecomments.addElement("item");
        eleitem.addAttribute("id", "ac01");
        if(!fortype.toLowerCase().equals("matrix")) {
        	String comments = zdgzinfo.getDaPanAnalysisComments();
            eleitem.setText(comments);
        }
	}
}
