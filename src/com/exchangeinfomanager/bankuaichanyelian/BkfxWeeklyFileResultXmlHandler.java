package com.exchangeinfomanager.bankuaichanyelian;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

class BkfxWeeklyFileResultXmlHandler 
{
	private static Logger logger = Logger.getLogger(BkfxWeeklyFileResultXmlHandler.class);
	private SystemConfigration sysconfig;
	private String bkfwkfilexml;
	private BanKuaiDbOperation bkopt;
	private Document bkfxxmldoc;
	private Element bkfxxmlroot;

	public BkfxWeeklyFileResultXmlHandler() 
	{
		this.sysconfig = SystemConfigration.getInstance();
//		this.bkopt = new BanKuaiDbOperation ();
	}
	/*
	 * 
	 */
	public Element getXmlRootFileForBkfxWeeklyFile (File xmlfile)
	{
		if(xmlfile != null && xmlfile.exists()) { //不存在，创建该文件
			try {
				bkfxxmldoc = null;
				bkfxxmlroot = null;
				
				SAXReader reader = new SAXReader();
				bkfxxmldoc = reader.read(xmlfile);
				bkfxxmlroot = bkfxxmldoc.getRootElement();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		} else {
			bkfxxmldoc = null;
			bkfxxmlroot = null;
			bkfxxmldoc = DocumentFactory.getInstance().createDocument();
	        bkfxxmlroot = bkfxxmldoc.addElement("板块分析结果");//添加文档根
		}
        
        return bkfxxmlroot;
	}
	/*
	 * 
	 */
	public void addBanKuaiFxSetToXml ( String bkcode, boolean selfinset, Set<String> bkstks, LocalDate date)
	{
		if(bkstks.size() == 0)
			return;
		
		String xpath = ".//BanKuai[@bkcode=\"" + bkcode + "\"]";  
		Node tmpnode = bkfxxmldoc.selectSingleNode(xpath);
		
		if(tmpnode == null) { //要加一个板块
			Element bankuai = bkfxxmlroot.addElement("BanKuai");
			bankuai.addAttribute("bkcode", bkcode  );
			bankuai.addAttribute("StocskNum", String.valueOf(bkstks.size())  );
			if(selfinset)
				bankuai.addAttribute("selfIsMatchModel", "TRUE"  );
			else
				bankuai.addAttribute("selfIsMatchModel", "FALSE"  );

			for(String stkcode : bkstks) {
				Element stock = bankuai.addElement("gegu");
				stock.addAttribute("gegucode", stkcode);
			}
		} else {
			for(String stkcode : bkstks) {
				Element stock = ((Element)tmpnode).addElement("gegu");
				stock.addAttribute("gegucode", stkcode);
			}
		}
		
	}
	/*
	 * 
	 */
	public void  saveXmlFileForBkfxWeeklyFile (File weeklyfilename)
	{
		String xmlfilename = weeklyfilename.getName().replace(".EBK", ".XML");
//		File filefmxx = new File( xmlfilename );
		
		String exportfilename = sysconfig.getTDXModelMatchExportFile () + xmlfilename;
		File exportxmlfile = new File(exportfilename);
		if(!exportxmlfile.getParentFile().exists()) {  
            //如果目标文件所在的目录不存在，则创建父目录  
            logger.debug("目标文件所在目录不存在，准备创建它！");  
            if(!exportxmlfile.getParentFile().mkdirs()) {  
                System.out.println("创建目标文件所在目录失败！");  
                return ;  
            }  
        }  
		
		try {
				if (exportxmlfile.exists()) {
					exportxmlfile.delete();
					exportxmlfile.createNewFile();
				} else
					exportxmlfile.createNewFile();
		} catch (Exception e) {
				e.printStackTrace();
				return ;
		}
		
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");    // 指定XML编码        
		//保存产业链XML
		try {
			XMLWriter writer = new XMLWriter(new FileOutputStream(exportxmlfile),format); // 输出全部原始数据，并用它生成新的我们需要的XML文件  
				writer.write(bkfxxmldoc); //输出到文件  
				//writer.flush();
				writer.close();
		} catch (IOException e) {
				e.printStackTrace();
		}
		
		exportxmlfile = null;
//		filefmxx = null;
	}
	/*
	 * 
	 */
	public Integer getBanKuaiFxSetNumberOfSpecificDate (String bkcode, LocalDate date)
	{
		String xpath = ".//BanKuai[@bkcode=\"" + bkcode + "\"]";  
		Node tmpnode = bkfxxmldoc.selectSingleNode(xpath);
		
		if(tmpnode == null) 
			return null;
		
		String stocksnum = ((Element)tmpnode).attributeValue("StocskNum");
		return Integer.parseInt(stocksnum);
		
	}
	/*
	 * 
	 */
	public Boolean getBanKuaiFxSelfMatchModelOfSepecificDate (String bkcode, LocalDate date)
	{
		String xpath = ".//BanKuai[@bkcode=\"" + bkcode + "\"]";  
		Node tmpnode = bkfxxmldoc.selectSingleNode(xpath);
		
		if(tmpnode == null) 
			return null;
		
		String selfmatchmodel = ((Element)tmpnode).attributeValue("selfIsMatchModel");
		return Boolean.parseBoolean(selfmatchmodel);
	}
	/*
	 * 
	 */
	public Set<String> getBanKuaiFxSetOfSpecificDate (String bkcode, LocalDate date)
	{
		String xpath = ".//BanKuai[@bkcode=\"" + bkcode + "\"]";  
		Node tmpnode = bkfxxmldoc.selectSingleNode(xpath);
		
		if(tmpnode == null) 
			return null;
		
		String stocksnum = ((Element)tmpnode).attributeValue("StocskNum");
		
		Set<String> result = new HashSet<String> ();
		Iterator eleIt = ((Element)tmpnode).elementIterator();
		while (eleIt.hasNext()) 
		{
			   Element e = (Element) eleIt.next();
			   
			   logger.debug(e.getName());
			   logger.debug(e.attributeValue("gegucode"));

			   result.add(e.attributeValue("gegucode"));
		 }
		
		return result;
		
	}
}
