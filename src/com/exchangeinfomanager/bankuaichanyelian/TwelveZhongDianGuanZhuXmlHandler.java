package com.exchangeinfomanager.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.exchangeinfomanager.bankuai.gui.GuanZhuBanKuaiInfo;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class TwelveZhongDianGuanZhuXmlHandler 
{
	public  TwelveZhongDianGuanZhuXmlHandler() 
	{
		initializeSysConfig ();
		gzbkmap = new HashMap<String,ArrayList<GuanZhuBanKuaiInfo>> ();
		
		this.xmlfile =   sysconfig.getTwelveZhongDianGuanZhuBanKuaiSheZhiXmlFile ();
		File zdgzxml = new File(this.xmlfile );
		if(!zdgzxml.exists()) { //�����ڣ��������ļ�
			try {
				zdgzxml.createNewFile();
			} catch (IOException e) {
				//System.out.println(this.xmlfile + "�����ڣ��Ѿ�����");
				e.printStackTrace();
			}
		}
	
		FileInputStream xmlfileinput = null;
		try {
			xmlfileinput = new FileInputStream(this.xmlfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			SAXReader reader = new SAXReader();
			document = reader.read(xmlfileinput);
			xmlroot = document.getRootElement();
		} catch (DocumentException e) {
			System.out.println(this.xmlfile + "���ڴ���");
			return;
		}
	
		getZdgzbkXml ();
	}
	
	private String xmlfile;
	private static Element xmlroot = null;
	private Document document;
	private SystemConfigration sysconfig;
	private HashMap<String,ArrayList<GuanZhuBanKuaiInfo>> gzbkmap; //�ص��ע�İ��
	private boolean hasXmlRevised;
	
	private void initializeSysConfig()
	{
		sysconfig = SystemConfigration.getInstance();
	}
	public boolean hasXmlRevised ()
	{
		return hasXmlRevised;
	}
	private  void getZdgzbkXml()
	{
		 Iterator it = xmlroot.elementIterator();
		 while (it.hasNext()) 
		 {
			   Element daleielement = (Element) it.next();
			   ArrayList<GuanZhuBanKuaiInfo> tmpdllist = new ArrayList<GuanZhuBanKuaiInfo> ();
			   String daleiname = daleielement.attributeValue("daleiname");
			   
			   Iterator daleiit = daleielement.elementIterator();
			   while(daleiit.hasNext()) {
				   Element iteele = (Element)daleiit.next();
				   GuanZhuBanKuaiInfo tmpgzbkinfo = new GuanZhuBanKuaiInfo ();
				   tmpgzbkinfo.setTdxbk(iteele.attributeValue("tdxbk"));
				   tmpgzbkinfo.setBkchanyelian(iteele.getText() );
				   tmpgzbkinfo.setSelectedtime(iteele.attributeValue("addedTime") );
				   if(iteele.attributeValue("officallyselected") == null || iteele.attributeValue("officallyselected").toLowerCase().equals("false")) {
					   tmpgzbkinfo.setOfficallySelected(false);
				   } else 
					   tmpgzbkinfo.setOfficallySelected(true);
				   
				   tmpdllist.add(tmpgzbkinfo);
			   }
			   
			   gzbkmap.put(daleiname, tmpdllist);
		}
	}
	public HashMap<String, ArrayList<GuanZhuBanKuaiInfo>> getZhongDianGuanZhuBanKuai () 
	{
		return gzbkmap;
	}
	/*
	 * ����ĳ�������ע���Ӱ��
	 */
	public void addNewGuanZhuBanKuai (String daleiname,GuanZhuBanKuaiInfo tmpgzbk)
	{
		 ArrayList<GuanZhuBanKuaiInfo> tmpzdgzbklist = gzbkmap.get(daleiname );
		 tmpzdgzbklist.add(tmpgzbk);
	}
	/*
	 * ɾ��ĳ������Ĺ�ע���Ӱ��
	 */
	public void removeGuanZhuBanKuai (String daleiname,GuanZhuBanKuaiInfo gzbk)
	{
		 ArrayList<GuanZhuBanKuaiInfo> tmpzdgzbklist = gzbkmap.get(daleiname );
		 tmpzdgzbklist.remove(gzbk);

	}
	public void addNewDaLei (String daleiname) 
	{
		if( !gzbkmap.keySet().contains(daleiname)) {
			ArrayList<GuanZhuBanKuaiInfo> tmpdllist = new ArrayList<GuanZhuBanKuaiInfo> ();
			gzbkmap.put(daleiname, tmpdllist);
			
			xmlroot.addElement(daleiname);
			
		}
	}
	public void deleteDaLei (String daleiname) 
	{
		if( gzbkmap.keySet().contains(daleiname)) {
			
			gzbkmap.remove(daleiname);
			
			Element delele = xmlroot.element(daleiname);
			xmlroot.remove(delele);
			
		}
	}
	
	
	public boolean saveAllZdgzbkToXml() 
	{
		Document documenttosave = DocumentFactory.getInstance().createDocument();
        Element rootele = documenttosave.addElement("ZhongDianGuanZhuanBanKuaiDetail");//����ĵ���
        
        for(String dastr:gzbkmap.keySet() ) {
        	System.out.print(dastr);
        	Element bkele = rootele.addElement("DaLeiBanKuai");
			bkele.addAttribute("daleiname",dastr);
        	
        	ArrayList<GuanZhuBanKuaiInfo> tmpsubcyllist = gzbkmap.get(dastr);
        	for(GuanZhuBanKuaiInfo tmpgzbkifno :tmpsubcyllist) {
        		String tdxbk = tmpgzbkifno.getTdxbk();
        		String subcyl = tmpgzbkifno.getBkchanyelian();
        		String addedtime = tmpgzbkifno.getSelectedtime();
        		String offselted = String.valueOf(tmpgzbkifno.isOfficallySelected() ).toLowerCase();
        		
        		Element subcylele = bkele.addElement("Item");
        		subcylele.addAttribute("tdxbk", tdxbk);
       			subcylele.setText(subcyl); //�����һ�� -> �����ɾ����Ҫ���ǡ�ֱ��sub�ᵼ���´����������ɾһ��
        		subcylele.addAttribute("addedTime", addedtime);
        		subcylele.addAttribute("officallyselected",offselted);
        		
        	}
        	
        }
        
				
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");    // ָ��XML����        
		XMLWriter writer;
		try {
			writer = new XMLWriter(new FileWriter(this.xmlfile),format);
			writer.write(documenttosave);
			writer.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		hasXmlRevised = true;
		return false;
		
	}

	/*
	 * ����12������ĵ�ǰ�����Ƿ�����ṩ�İ��
	 */
	public Multimap<String,Set<String>> subBkSuoSuTwelveDaLei (SetView stockcurbks)
	{
		Multimap<String,Set<String>> tmpdalei =  ArrayListMultimap.create();
		Object[] bankuaidaleiname = gzbkmap.keySet().toArray();
		try {
			for(Object currentdalei:bankuaidaleiname) {
				
				SetView<String> intersectionbankuai = calSetIntersectionOfZdgz(gzbkmap.get((String)currentdalei),stockcurbks);
				if(  intersectionbankuai.size() >0  )
					tmpdalei.put((String)currentdalei,intersectionbankuai);
			}
		} catch (java.lang.NullPointerException ex) {
			
		}

		return tmpdalei;
	}
	/*
	 * 
	 */
	private SetView<String> calSetIntersectionOfZdgz (ArrayList<GuanZhuBanKuaiInfo> arrayList,SetView stockcurbks)
	{
		Set<String> tmpset = new HashSet<String>();
		for(GuanZhuBanKuaiInfo tmpgzbk:arrayList) {
			String tmpbank = tmpgzbk.getTdxbk().trim();
			tmpset.add(tmpbank);
		}
		SetView<String> intersectionbankuai = Sets.intersection(tmpset, stockcurbks);
		
		return intersectionbankuai;
	}

	

}
