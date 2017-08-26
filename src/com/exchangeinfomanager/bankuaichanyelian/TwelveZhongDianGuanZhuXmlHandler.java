package com.exchangeinfomanager.bankuaichanyelian;

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
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class TwelveZhongDianGuanZhuXmlHandler 
{
	public  TwelveZhongDianGuanZhuXmlHandler() 
	{
		initializeSysConfig ();
		gzbkdetailmap = new HashMap<String,ArrayList<BkChanYeLianTreeNode>>();
		
		this.xmlfile =   sysconfig.getTwelveZhongDianGuanZhuBanKuaiSheZhiXmlFile ();
		File zdgzxml = new File(this.xmlfile );
		if(!zdgzxml.exists()) { //不存在，创建该文件
			try {
				zdgzxml.createNewFile();
			} catch (IOException e) {
				//System.out.println(this.xmlfile + "不存在，已经创建");
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
			System.out.println(this.xmlfile + "存在错误");
			return;
		}
	
//		getZdgzbkXml ();
	}
	HashMap<String,ArrayList<BkChanYeLianTreeNode>> gzbkdetailmap;
	private static Element xmlroot = null;
	private Document document;
	private SystemConfigration sysconfig;

	
	private String xmlfile;
	//private HashMap<String,ArrayList<GuanZhuBanKuaiInfo>> gzbkmap; //重点关注的板块
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
	 * 读取重点关注XML,把每个关注的产业链包装成产业链XML TREE的节点
	 */
	public HashMap<String, ArrayList<BkChanYeLianTreeNode>> getZdgzBanKuaiFromXmlAndUpatedToCylTree (BkChanYeLianTree cyltree)
	{
		gzbkdetailmap = new HashMap<String,ArrayList<BkChanYeLianTreeNode>>  (); //重点关注的板块
		
		Iterator it = xmlroot.elementIterator();
		 while (it.hasNext()) 
		 {
			   Element daleielement = (Element) it.next();
			   ArrayList<BkChanYeLianTreeNode> tmpgzbksublist = new ArrayList<BkChanYeLianTreeNode> (); //重点关注的板块list
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
				   //
				    BkChanYeLianTreeNode expectnode = cyltree.updateZdgzInfoToBkCylTreeNode(chanyelian, addedtime,officallyselt);
				    if(expectnode != null)
				    	tmpgzbksublist.add(expectnode);
				    else { //没有找到相关的node，说明这个重点关注可能在产业链XML里面已经删除了，那在这里也应该在XML里面删除
				    	removeChanYeLianFromXml (daleiname,chanyelian);
				    	hasXmlRevised = true;
				    }
			   }
			   gzbkdetailmap.put(daleiname, tmpgzbksublist);
		 }
		 
		 return gzbkdetailmap;
	}
	/*
	 * 从重点关注中删除在产业链XML中已经不存在的重点关注，当日直接删除好么？这需要考虑，目前采用直接删除
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
			System.out.println("重点关注XML中删除" + chanyelian);
		} catch (java.lang.NullPointerException ex) {
		}
		
	}
	public  HashMap<String, ArrayList<BkChanYeLianTreeNode>> getZdgzBkDetail()
	{
		return gzbkdetailmap;
	}

	/*
	 * 增加某个大类关注的子板块
	 */
	public void addNewGuanZhuBanKuai (String daleiname,BkChanYeLianTreeNode parent)
	{
		 ArrayList<BkChanYeLianTreeNode> tmpzdgzbklist = gzbkdetailmap.get(daleiname);
		 tmpzdgzbklist.add(parent);
	}
	/*
	 * 删除某个大类的关注的子板块
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
        Element rootele = documenttosave.addElement("ZhongDianGuanZhuanBanKuaiDetail");//添加文档根
        
        for(String dastr:gzbkdetailmap.keySet() ) {
        	System.out.print(dastr);
        	Element bkele = rootele.addElement("DaLeiBanKuai");
			bkele.addAttribute("daleiname",dastr);
        	
        	ArrayList<BkChanYeLianTreeNode> tmpsubcyllist = gzbkdetailmap.get(dastr);
        	for(BkChanYeLianTreeNode tmpgzbkifno :tmpsubcyllist) {
        		String tdxbk = tmpgzbkifno.getTdxBk();
        		String tdxbkcode = tmpgzbkifno.getTongDaXingBanKuaiCode();
        		String subcyl = tmpgzbkifno.getChanYeLian();
        		String addedtime = tmpgzbkifno.getSelectedtime();
        		String offselted = String.valueOf(tmpgzbkifno.isOfficallySelected() ).toLowerCase();
        		
        		Element subcylele = bkele.addElement("Item");
        		subcylele.addAttribute("tdxbk", tdxbk);
        		subcylele.addAttribute("tdxbkcode", tdxbkcode);
       			subcylele.setText(subcyl); //后面多一个 -> ，如何删除还要考虑。直接sub会导致下次载入后，又再删一次
        		subcylele.addAttribute("addedTime", addedtime);
        		subcylele.addAttribute("officallyselected",offselted);
        		
        	}
        	
        }
        
				
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");    // 指定XML编码        
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
	 * 计算12个大类的当前设置是否包含提供的板块
	 */
	public Multimap<String,Set<String>> subBkSuoSuTwelveDaLei (SetView stockcurbks)
	{
		Multimap<String,Set<String>> tmpdalei =  ArrayListMultimap.create();
		Object[] bankuaidaleiname = gzbkdetailmap.keySet().toArray();
		try {
			for(Object currentdalei:bankuaidaleiname) {
				
				SetView<String> intersectionbankuai = calSetIntersectionOfZdgz(gzbkdetailmap.get((String)currentdalei),stockcurbks);
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
	private SetView<String> calSetIntersectionOfZdgz (ArrayList<BkChanYeLianTreeNode> arrayList,SetView stockcurbks)
	{
		Set<String> tmpset = new HashSet<String>();
		for(BkChanYeLianTreeNode tmpgzbk:arrayList) {
			String tmpbank = tmpgzbk.getTdxBk().trim();
			tmpset.add(tmpbank);
		}
		SetView<String> intersectionbankuai = Sets.intersection(tmpset, stockcurbks);
		
		return intersectionbankuai;
	}
	public ArrayList<BkChanYeLianTreeNode> getASubDaiLeiDetail(String daleiname)
	{
		
		return gzbkdetailmap.get(daleiname);
	}
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
