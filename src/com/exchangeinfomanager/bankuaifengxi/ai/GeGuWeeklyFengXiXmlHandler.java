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
import org.dom4j.io.SAXReader;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Strings;

public class GeGuWeeklyFengXiXmlHandler extends WeeklyFengXiXmlHandler
{
	public GeGuWeeklyFengXiXmlHandler (String stockcode, LocalDate date) 
	{
		super (stockcode,date);
		
		String text = super.getStockBanKuaiZdgz(stockcode,date);
		if( !hasxmlindatabase) {
			this.fxweeklyxmlmatrixfile = sysconfig.getGeGuFengXiWeeklyXmlMatrixFile ();
			
			FileInputStream xmlfileinput = null;
			try {
				xmlfileinput = new FileInputStream(fxweeklyxmlmatrixfile);
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
		} else 
			createZdgzFromXml (xmlroot,null);
	}
	
	/*
	 * 
	 */
	void createZdgzFromXml(Element xmlroot,String extracomments) 
	{
		if(zdgzinfo == null)
			zdgzinfo = new ZhongDianGuanZhu ();
		
		getFinancial (xmlroot);
		getGuDong (xmlroot);
		getJiShu(xmlroot);
		getTiCai(xmlroot);
		getGuPiaoChi(xmlroot);
		getAnalysisComments(xmlroot,extracomments);
	}
	private void getFinancial (Element xmlroot) 
	{
		Element eleply = xmlroot.element("Financial");
		ArrayList<ZdgzItem> financial = getXmlElementInfo (eleply);
	   	zdgzinfo.setGeGuFinancial(financial);
	}
	private void getGuDong (Element xmlroot) 
	{
		Element elegd = xmlroot.element("gudong");
	   	ArrayList<ZdgzItem> gudong = getXmlElementInfo (elegd);
	   	zdgzinfo.setGeGuGuDong(gudong);
	}
	private void getJiShu (Element xmlroot) 
	{
		Element elejs = xmlroot.element("jishu");
	   	ArrayList<ZdgzItem> jishu = getXmlElementInfo (elejs);
	   	zdgzinfo.setGeGuJiShu(jishu);
	}
	private void getTiCai (Element xmlroot) 
	{
	   	Element eletc = xmlroot.element("TiCai");
	   	ArrayList<ZdgzItem> ticha = getXmlElementInfo (eletc);
	   	zdgzinfo.setGeGuTiCha(ticha);
		
	}
	private void getGuPiaoChi (Element xmlroot) 
	{
	   	Element elegpc = xmlroot.element("GuPiaoChi");
	   	ArrayList<ZdgzItem> gpc = getXmlElementInfo (elegpc);
	   	zdgzinfo.setGeGuGuPiaoChi(gpc);
		
	}
	private void getAnalysisComments (Element xmlroot, String extracomments) 
	{
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
		   		zdgzinfo.setGeGuAnalysisComments (text);
			} catch (java.lang.NullPointerException e) {
	   			
	   		}
	   	}
	}
	/*
	 * 
	 */
	void setupXmlContentsFromGui(Element rootele) 
	{
		setFinancial (rootele);
        setGuDong (rootele);
        setJiShu(rootele);
        setTiCai(rootele);
        setGuPiaoChi(rootele);
        setAnalysisComments(rootele);
	}

	private void setFinancial (Element xmlroot) 
	{
		Element elefina = xmlroot.addElement("Financial");
		elefina.addAttribute("value", "财务报表");
        ArrayList<ZdgzItem> financial = zdgzinfo.getGeGuFinancial();
        createFenXiXmlItems(elefina, financial);
	}
	private void setGuDong (Element rootele)
	{
		Element elegd = rootele.addElement("gudong");
        elegd.addAttribute("value", "股东研究");
        ArrayList<ZdgzItem> gudong = zdgzinfo.getGeGuGuDong();
        createFenXiXmlItems(elegd,gudong);
	}
	private void setJiShu (Element rootele) 
	{
		Element ele = rootele.addElement("jishu");
        ele.addAttribute("value", "模型与技术面");
        ArrayList<ZdgzItem> jishu = zdgzinfo.getGeGuJiShu();
        createFenXiXmlItems(ele,jishu);
	}
	private void setTiCai (Element rootele) 
	{
		Element ele = rootele.addElement("TiCai");
        ele.addAttribute("value", "题材与消息面");
        ArrayList<ZdgzItem> ticai = zdgzinfo.getGeGuTiCha();
        createFenXiXmlItems(ele,ticai);
	}
	private void setGuPiaoChi (Element rootele) 
	{
		Element ele = rootele.addElement("GuPiaoChi");
        ele.addAttribute("value", "股票池");
        ArrayList<ZdgzItem> gpc = zdgzinfo.getGeGuGuPiaoChi();
        createFenXiXmlItems(ele,gpc);
	}
	private void setAnalysisComments (Element rootele) 
	{
		Element elecomments = rootele.addElement("AnalysisComments");
        elecomments.addAttribute("value", "本周总结");
        Element eleitem = elecomments.addElement("item");
        eleitem.addAttribute("id", "ac01");
        String comments = zdgzinfo.getDaPanAnalysisComments();
        try {
        	eleitem.setText(comments);
        } catch (java.lang.IllegalArgumentException e) {
        	eleitem.setText("");
        }
	}



}
