package com.exchangeinfomanager.accountconfiguration.AccountOperation.ImportOpts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.io.LineProcessor;

/*
 * 专门为招商证券
 */
public class MoneyFlowRecordsProcessorZhaoShang implements LineProcessor<List<String>>
{

	private List<String> recoreslists = Lists.newArrayList();
    private String zhanghuid = null;
	private SystemConfigration sysconfig;
	boolean newformate = false;
   
    public MoneyFlowRecordsProcessorZhaoShang ()
	{
		 sysconfig = SystemConfigration.getInstance();
		 //initializeZhuanRuChuMap ();
		 initializeZhuanRuChuList ();
	}
    @Override
    public boolean processLine(String line) throws IOException 
    {
    	List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);
    	System.out.println(tmplinelist);
    	if(tmplinelist.isEmpty())
    		return true;
    	if( tmplinelist.get(0).trim().contains("客户号") && zhanghuid == null) {
    		zhanghuid =  tmplinelist.get(0);//.replace("客户号:", "").trim();
    		recoreslists.add( zhanghuid ); //直接返回用户账号ID
    	}
    	
    	
		if(line.contains("银行代码"))
    		newformate  = true;
    	
        if( Pattern.matches("\\d{8}",tmplinelist.get(0)) 		&& newformate == true 	&& isActionMoney(tmplinelist.get(2)) != null) {
       		parseLineUsingNewFormate (line,tmplinelist);
    	} else if( Pattern.matches("\\d{8}",tmplinelist.get(0)) && newformate == false 	&& isActionMoney(tmplinelist.get(1 )) != null ) {
    		parseLineUsingOldFormate (line,tmplinelist);
    	}
    	
        return true;
    }
    private void parseLineUsingNewFormate(String line, List<String> tmplinelist)
    {
    	String actiondate = tmplinelist.get(0);
		String actionquanshangtype = tmplinelist.get(tmplinelist.size()-7);
		String actionmoney =tmplinelist.get(tmplinelist.size()-2);
		Boolean isbuysell = isActionMoney(actionquanshangtype) ;
//		String systemtype = null;
//		if( (systemtype = isActionSystemTypeForBuy(actionquanshangtype)) !=null ) {
//			//systemtype = isActionSystemTypeForBuy(actionquanshangtype);
//			isbuysell = true;
//		} else if( (systemtype = isActionSystemTypeForSell(actionquanshangtype)) !=null ) {
//			//systemtype = isActionSystemTypeForBuy(actionquanshangtype);
//			isbuysell = false;
//		} 

		String formateline = fmorateLineResult(actiondate,actionquanshangtype,isbuysell,actionmoney,line);
		recoreslists.add(formateline);
	}
	private void parseLineUsingOldFormate(String line, List<String> tmplinelist) 
    {
		String actiondate = tmplinelist.get(0);
		String actionquanshangtype = tmplinelist.get(tmplinelist.size()-7);
		String actionmoney = tmplinelist.get(tmplinelist.size()-2);
		Boolean isbuysell = isActionMoney(actionquanshangtype) ;
		//String systemtype = null;
		
		
//		if( (systemtype = isActionSystemTypeForBuy(actionquanshangtype)) !=null ) {
//			//systemtype = isActionSystemTypeForBuy(actionquanshangtype);
//			isbuysell = true;
//		} else if( (systemtype = isActionSystemTypeForSell(actionquanshangtype)) !=null ) {
//			//systemtype = isActionSystemTypeForBuy(actionquanshangtype);
//			isbuysell = false;
//		} 

		String formateline = fmorateLineResult(actiondate,actionquanshangtype,isbuysell,actionmoney,line);
		recoreslists.add(formateline);
	}
	private String fmorateLineResult(String actiondate, String actiontype, Boolean isbuysell,String actionmoney,String line ) 
	{
		//[20070613, 银行转存, true,银行, 10000.00, 20070613            银行转存                      0.00    0.000      0.00      0.00    10000.00       43583.85]
			return actiondate + "|"
					+ actiontype + "|"
					+ isbuysell.toString() + "|"
					+ actionmoney + "|"
					+ line
					;
	}
	private Boolean isActionMoney(String actiontype) 
	{
		Boolean zhuanruchu = null;
		
		for(String maistr:zhuanrugjc ) {
			if(actiontype.contains(maistr) ) {
				zhuanruchu = true;
				return zhuanruchu;
			} 
		}
		for(String sellstr:zhuanchugjc) {
			if(actiontype.contains(sellstr) ) {
				zhuanruchu = false;
				return zhuanruchu;
			}
		}

		return zhuanruchu;
	}
	
//	private String isActionSystemTypeForBuy (String actiontype)
//	{
//		String result = null;
//		Collection<String> xianjing = zhuanrugjcmap.get("xianjing");
//		if(xianjing.contains(actiontype))
//			return "xianjing";
//		Collection<String> hongli = zhuanrugjcmap.get("hongli");
//		if(hongli.contains(actiontype))
//			return "hongli";
//		
//		return result;
//	}
//	private String isActionSystemTypeForSell (String actiontype)
//	{
//		String result = null;
//		Collection<String> xianjing = zhuanchugjcmap.get("xianjing");
//		if(xianjing.contains(actiontype))
//			return "xianjing";
//		Collection<String> hongli = zhuanchugjcmap.get("hongli");
//		if(hongli.contains(actiontype))
//			return "hongli";
//		
//		return result;
//	}
	 //存放本系统定义的关键词 "银行 红利.."和券商数据的交易类型的对应关系，
	//之所以要这样的对应关系是因为后期存放资金记录的时候要判断是银行来的资金还是股票/券商的红利，银行资金有2个账户的变坏，红利只有一个账户的变化
//	Multimap<String,String> zhuanrugjcmap = ArrayListMultimap.create();
//	Multimap<String,String> zhuanchugjcmap = ArrayListMultimap.create();
//    public void initializeZhuanRuChuMap ()
//    {
//    	File sysconfigfilexml = new File (sysconfig.getSysSettingFile());
//    	FileInputStream xmlfileinput = null;
//		try {
//			xmlfileinput = new FileInputStream(sysconfigfilexml);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		
//		Element xmlroot = null;
//		try {
//			SAXReader reader = new SAXReader();
//			Document xmldoc = reader.read(xmlfileinput);
//			xmlroot = xmldoc.getRootElement();
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//		
//		Element jiaoyiele = xmlroot.element("ZiJingGuanJianCiZhaoShang");
//		Iterator it = jiaoyiele.elementIterator();
//   	 	while (it.hasNext()) {
//   	 	  Element element = (Element) it.next();
//		  if(element.attribute("iszhuanru").getText().toLowerCase().equals("true")) {
//			  String shuxing = element.attribute("shuxing").getText().toLowerCase();
//			  String quanshangjiaoyishuxing = element.getText();
//			  zhuanrugjcmap.put(shuxing, quanshangjiaoyishuxing);
//		  }
//		  else if(element.attribute("iszhuanru").getText().toLowerCase().equals("false")) {
//			  String shuxing = element.attribute("shuxing").getText().toLowerCase();
//			  String quanshangjiaoyishuxing = element.getText();
//			  zhuanchugjcmap.put(shuxing, quanshangjiaoyishuxing);
//		  }
//   	 	}
//    }

		
	 	private List<String> zhuanrugjc = Lists.newArrayList(); //买入关键词
	    private List<String> zhuanchugjc = Lists.newArrayList(); //卖出关键词
	    public void initializeZhuanRuChuList ()
	    {
	    	File sysconfigfilexml = new File (sysconfig.getQuanShangJiaoYiSheZhi() );
	    	FileInputStream xmlfileinput = null;
			try {
				xmlfileinput = new FileInputStream(sysconfigfilexml);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			Element xmlroot = null;
			try {
				SAXReader reader = new SAXReader();
				Document xmldoc = reader.read(xmlfileinput);
				xmlroot = xmldoc.getRootElement();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			
			Element jiaoyiele = xmlroot.element("ZiJingGuanJianCiZhaoShang");
			Iterator it = jiaoyiele.elementIterator();
	   	 	while (it.hasNext()) {
	   	 	  Element element = (Element) it.next();
			  if(element.attribute("iszhuanru").getText().toLowerCase().equals("true"))
				  zhuanrugjc.add(element.getText()); 
			  else if(element.attribute("iszhuanru").getText().toLowerCase().equals("false"))
				  zhuanchugjc.add(element.getText());
	   	 	}
	    }
	@Override
    public List<String> getResult() {
        return recoreslists;
    }
}
