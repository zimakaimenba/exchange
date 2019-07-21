package com.exchangeinfomanager.bankuaichanyelian;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.operations.BanKuaiAndStockTree;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
/**
 * 
 */
class TwelveZhongDianGuanZhuXmlHandler 
{
	public  TwelveZhongDianGuanZhuXmlHandler() 
	{
		initializeSysConfig ();
		gzbkdetailmap = new HashMap<String,ArrayList<BkChanYeLianTreeNode>>();

		this.xmlfile =   sysconfig.getTwelveZhongDianGuanZhuBanKuaiSheZhiXmlFile ();
		File zdgzxml = new File(this.xmlfile );
		if(!zdgzxml.exists()) { //�����ڣ��������ļ�
			try {
				zdgzxml.createNewFile();
			} catch (IOException e) {
				//logger.debug(this.xmlfile + "�����ڣ��Ѿ�����");
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
			logger.debug(this.xmlfile + "���ڴ���");
			return;
		}
	}
	
	private static Logger logger = Logger.getLogger(TwelveZhongDianGuanZhuXmlHandler.class);
	private HashMap<String,ArrayList<BkChanYeLianTreeNode>> gzbkdetailmap;
//	private Multimap<String,String> gzbkdetailsimplemap;
	private static Element xmlroot = null;
	private Document document;
	private SystemConfigration sysconfig;
	private String xmlfile;
	//private HashMap<String,ArrayList<GuanZhuBanKuaiInfo>> gzbkmap; //�ص��ע�İ��
	private boolean hasXmlRevised;
	
	private void initializeSysConfig()
	{
		sysconfig = SystemConfigration.getInstance();
	}
	public boolean hasXmlRevised ()
	{
		return hasXmlRevised;
	}
	/*
	 * ��ȡ�ص��עXML,��ÿ����ע�Ĳ�ҵ����װ�ɲ�ҵ��XML TREE�Ľڵ�
	 */
	public HashMap<String, ArrayList<BkChanYeLianTreeNode>> getZdgzBanKuaiFromXmlAndUpatedToCylTree (BanKuaiAndStockTree cyltree)
	{
//		gzbkdetailmap = new HashMap<String,ArrayList<BkChanYeLianTreeNode>>  (); //�ص��ע�İ��
		
		Iterator it = xmlroot.elementIterator();
		 while (it.hasNext()) 
		 {
			   Element daleielement = (Element) it.next();
			   ArrayList<BkChanYeLianTreeNode> tmpgzbksublist = new ArrayList<BkChanYeLianTreeNode> (); //�ص��ע�İ��list
			   String daleiname = daleielement.attributeValue("daleiname");
			   
			   Iterator daleiit = daleielement.elementIterator();
			   while(daleiit.hasNext()) {
				   Element iteele = (Element)daleiit.next();
				   
				   //String suoshutdxbkcode = iteele.attributeValue("tdxbkcode");
				   
				   String chanyelian = iteele.getText() ;
				   String addedtime = iteele.attributeValue("addedTime") ;
				   boolean officallyselt;
				   if(iteele.attributeValue("officallyselected") == null || iteele.attributeValue("officallyselected").toLowerCase().equals("false")) {
					   officallyselt = false;
				   } else 
					   officallyselt = true;
				  
				   //���ص��ע��Ϣ��ӵ����ϡ������NODE��path
				    BkChanYeLianTreeNode expectnode = cyltree.updateZdgzInfoToBkCylTreeNode(chanyelian, addedtime,officallyselt);
				    if(expectnode != null)
				    	tmpgzbksublist.add(expectnode);
//				    else { //û���ҵ���ص�node��˵������ص��ע�����ڲ�ҵ��XML�����Ѿ�ɾ���ˣ���������ҲӦ����XML����ɾ��
//				    	removeChanYeLianFromXml (daleiname,chanyelian);
//				    	hasXmlRevised = true;
//				    }
			   }
			   
			   gzbkdetailmap.put(daleiname, tmpgzbksublist);
		 }
		 
		 return gzbkdetailmap;
	}
	/*
	 * ���ص��ע��ɾ���ڲ�ҵ��XML���Ѿ������ڵ��ص��ע������ֱ��ɾ����ô������Ҫ���ǣ�Ŀǰ����ֱ��ɾ��
	 */
	private void removeChanYeLianFromXml(String daleiname, String chanyelian) 
	{
		List<String> tmpcyltreepathlist =  Splitter.on("->").trimResults().omitEmptyStrings().splitToList(chanyelian);  
		String bkname = tmpcyltreepathlist.get(0);
		
		try {
			String xpath = ".//DaLeiBanKuai[@daleiname=\""+ daleiname + "\"]/Item[@tdxbk='" +  bkname + "\']";
			@SuppressWarnings("unchecked")
			List<Node> tmpnodegg = document.selectNodes(xpath);
			for(Node singlenode:tmpnodegg) {
				String nodecyl = singlenode.getText();
				if(chanyelian.equals(nodecyl))
					xmlroot.remove(singlenode);
			}
			logger.debug("�ص��עXML��ɾ��" + chanyelian);
		} catch (java.lang.NullPointerException ex) {
		}
		
	}
	public  HashMap<String, ArrayList<BkChanYeLianTreeNode>> getZdgzBkDetail()
	{
		return gzbkdetailmap;
	}

	/*
	 * ����ĳ�������ע���Ӱ��
	 */
	public void addNewGuanZhuBanKuai (String daleiname,BkChanYeLianTreeNode parent)
	{
		 ArrayList<BkChanYeLianTreeNode> tmpzdgzbklist = gzbkdetailmap.get(daleiname);
		 tmpzdgzbklist.add(parent);
	}
	/*
	 * ɾ��ĳ������Ĺ�ע���Ӱ��
	 */
	public void removeGuanZhuBanKuai (String daleiname,BkChanYeLianTreeNode gzcyl)
	{
		 ArrayList<BkChanYeLianTreeNode> tmpzdgzbklist = gzbkdetailmap.get(daleiname );
		 tmpzdgzbklist.remove(gzcyl);

	}
	public void addNewDaLei (String daleiname) 
	{
		if( !gzbkdetailmap.keySet().contains(daleiname)) {
			ArrayList<BkChanYeLianTreeNode> tmpdllist = new ArrayList<BkChanYeLianTreeNode> ();
			gzbkdetailmap.put(daleiname, tmpdllist);
			
			xmlroot.addElement(daleiname);
			
		}
	}
	public void deleteDaLei (String daleiname) 
	{
		if( gzbkdetailmap.keySet().contains(daleiname)) {
			
			gzbkdetailmap.remove(daleiname);
			
//			Element delele = xmlroot.element(daleiname);
//			xmlroot.remove(delele);
			
		}
	}
	
	
	public boolean saveAllZdgzbkToXml() 
	{
		Document documenttosave = DocumentFactory.getInstance().createDocument();
        Element rootele = documenttosave.addElement("ZhongDianGuanZhuanBanKuaiDetail");//����ĵ���
        
        for(String dastr:gzbkdetailmap.keySet() ) {
//        	System.out.print(dastr);
        	Element bkele = rootele.addElement("DaLeiBanKuai");
			bkele.addAttribute("daleiname",dastr);
        	
        	ArrayList<BkChanYeLianTreeNode> tmpsubcyllist = gzbkdetailmap.get(dastr);
        	for(BkChanYeLianTreeNode tmpgzbkifno :tmpsubcyllist) {
        		if(tmpgzbkifno.getNodeTreeRelated().shouldBeRemovedWhenSaveXml())
        			continue;
        		
        		String tdxbk = tmpgzbkifno.getNodeTreeRelated().getChanYeLianSuoShuTdxBanKuaiName();
        		String tdxbkcode = tmpgzbkifno.getNodeTreeRelated().getChanYeLianSuoShuTdxBanKuaiName();
        		String subcyl = tmpgzbkifno.getNodeTreeRelated().getNodeCurLocatedChanYeLian();
        		String addedtime = tmpgzbkifno.getNodeTreeRelated().getSelectedToZdgzTime();
        		String offselted = String.valueOf(tmpgzbkifno.getNodeTreeRelated().isOfficallySelected() ).toLowerCase();
        		
        		Element subcylele = bkele.addElement("Item");
        		subcylele.addAttribute("tdxbk", tdxbk);
        		subcylele.addAttribute("tdxbkcode", tdxbkcode);
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
		return true;
		
	}

	/*
	 * ����12������ĵ�ǰ��ҵ�������Ƿ�����ṩ�İ�飬ֻ��ע��ҵ���İ�鲿�֣�ֻҪ���һ�¾�����
	 */
	public Multimap<String,String> subBkSuoSuTwelveDaLei (Set<String> union)
	{
		Multimap<String,String> tmpsuoshudalei =  HashMultimap.create();
		String[] bankuaidaleiname = gzbkdetailmap.keySet().toArray(new String[gzbkdetailmap.keySet().size()]);
		try {
			for(String currentdalei:bankuaidaleiname) {
				   
				 Set<String> daleibankuaiset = this.getZdgzDaLeiBanKuaiSet(currentdalei);
				 SetView<String> intersectionbk = Sets.intersection( daleibankuaiset,union  );
				  
				  if(intersectionbk.size()>0) {
					  for(String intersectionbkname : intersectionbk)
						  tmpsuoshudalei.put(intersectionbkname, currentdalei);
				  }
			}
		} catch (java.lang.NullPointerException ex) {
			ex.printStackTrace();
		}

		return tmpsuoshudalei;
	}
	/*
	 * ���ĳ������Ĳ�ҵ����ͨ���Ű��
	 */
	private Set<String> getZdgzDaLeiBanKuaiSet (String daleiname) 
	{
		ArrayList<BkChanYeLianTreeNode> daleibankuailist = gzbkdetailmap.get(daleiname); //�����еİ��ID
		  
		  Set<String> daleibankuaiset = new HashSet<String>();
		  for(BkChanYeLianTreeNode cylnode : daleibankuailist) {
			  String tdxbk = cylnode.getNodeTreeRelated().getChanYeLianSuoShuTdxBanKuaiName ();
			  daleibankuaiset.add(tdxbk);
		  }
		  
		  return daleibankuaiset;
	}

	/*
	 * 
	 */
	public ArrayList<BkChanYeLianTreeNode> getASubDaiLeiDetail(String daleiname)
	{
		return gzbkdetailmap.get(daleiname);
	}
	/*
	 * 
	 */
	public void openZdgzXmlInWinSystem() 
	{
		String gegucylxmlfilepath = sysconfig.getTwelveZhongDianGuanZhuBanKuaiSheZhiXmlFile ();
		try {
			String cmd = "rundll32 url.dll,FileProtocolHandler " + gegucylxmlfilepath;
			Process p  = Runtime.getRuntime().exec(cmd);
			p.waitFor();
		} catch (Exception e1) 
		{
			e1.printStackTrace();
		}
		
	}
	

	

}
