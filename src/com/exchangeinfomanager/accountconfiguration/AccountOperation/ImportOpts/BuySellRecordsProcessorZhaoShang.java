package com.exchangeinfomanager.accountconfiguration.AccountOperation.ImportOpts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.LineProcessor;

/*
 * 专门为招商证券
 */
public class BuySellRecordsProcessorZhaoShang implements LineProcessor<List<String>>
{
    private List<String> recoreslists = Lists.newArrayList();
    private String zhanghuid = null;
    private boolean newformate = false;
	private SystemConfigration sysconfig;
   
	public BuySellRecordsProcessorZhaoShang ()
	{
		sysconfig = SystemConfigration.getInstance();
		initializeIsBuySellList ();
	}
	@Override
    public boolean processLine(String line) throws IOException {
    	List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);
    	System.out.println(tmplinelist);
    	if(tmplinelist.isEmpty())
    		return true;
    	if( tmplinelist.get(0).trim().contains("客户号") && zhanghuid == null) {
    		zhanghuid =  tmplinelist.get(0);//.replace("客户号:", "").trim();
    		recoreslists.add( zhanghuid ); //直接返回用户账号ID
    	}
    	
    	if(line.contains("银行代码"))
    		newformate = true;
    	
        if( Pattern.matches("\\d{8}",tmplinelist.get(0)) ) {
        	if(newformate == false)
        		parseLineUsingOldFormate (line,tmplinelist);
        	else parseLineUsingNewFormate (line,tmplinelist);
        }
        
        return true;
    }
/*
 * 15年以后招商的输出文件有了新的格式
 */
    private void parseLineUsingNewFormate(String line, List<String> tmplinelist)
    {
    	String tmpactiondate = tmplinelist.get(0).trim();
    	String tmpactiontype = tmplinelist.get(3).trim();
    	if( isBuyOrSell(tmpactiontype) != null) {
    		int actionnumber = 0;
			double actionprice = 0;
			String actionstockname = "";
			Boolean buysell = false;
			
			//[20150706, 0105396423, 000506, 证券买入吉, 艾, 科, 技, 1100.00, 15.100, 5.00, 0.00, -16615.00, 75627.82]
				int size = tmplinelist.size();
				actionnumber = Integer.parseInt(tmplinelist.get(size-6).trim().substring(0, tmplinelist.get(size-6).indexOf(".")) ); 
				double actionsingleprice =  Double.parseDouble(tmplinelist.get(size-5).trim()); //买入单价
				double actionshouxufei = Double.parseDouble(tmplinelist.get(size -4).trim());//手续费
				double actionyinhuishui = Double.parseDouble(tmplinelist.get(size -3).trim());//印花税
				actionprice = calChenBen (tmpactiontype,actionnumber,actionsingleprice,actionshouxufei,actionyinhuishui); //加上费率后的单价
				
				for(int i=3;i<size-6;i++)
					actionstockname = actionstockname+tmplinelist.get(i);
				actionstockname = actionstockname.trim().substring(4, actionstockname.length());
				
				buysell = isBuyOrSell(tmpactiontype);

				generateFormatedLineResult (tmpactiondate.trim(),tmpactiontype.substring(0, 4).trim(),buysell.toString().trim(),actionstockname.trim(),
											"XXXXXX",String.valueOf(actionnumber).trim(),String.valueOf(actionprice).trim(),line.trim() );
				
    	}	
    }
    /*
     * 生成格式化的数据记录
     */
    private void generateFormatedLineResult(String actiondate, String actiontype, String actionbuysell, String actionstockname, String acioncode,
			String actionnumber, String actionprice, String fullline) 
    {
    	String lineresult = actiondate  // 日期
				+  "|" + actiontype //买入卖出描述
				+ "|" + actionbuysell //标记是买入还是卖出
				+ "|" + actionstockname.trim() //持仓股票名字
				+ "|" + acioncode//持仓股票代码，招商是没有代码的，但其他券商有，预留
				+ "|" + actionnumber
				+ "|" + actionprice
				+ "|" + fullline
				;
		recoreslists.add(lineresult);
		System.out.println(lineresult);
		
	}
	/*
     * 计算每股买入或卖出的成本
     */
	private double calChenBen(String tmpactiontype, int actionnumber,double actionsingleprice, double actionshouxufei,double actionyinhuishui) 
	{
		double actionprice;
		if(isBuyOrSell(tmpactiontype)) { //买入的话，交易费用是成本要加入
			 actionprice = actionsingleprice + ( (actionshouxufei+actionyinhuishui) / actionnumber) ; //加上费率后的单价
		} else { //卖出，交易费用要减
			actionprice = actionsingleprice - ( (actionshouxufei+actionyinhuishui) / actionnumber) ; //加上费率后的单价
		}
		
		return actionprice;
	}

	private void parseLineUsingOldFormate(String line, List<String> tmplinelist) 
    {
		String tmpactiondate = tmplinelist.get(0).trim();
    	String tmpactiontype = tmplinelist.get(2).trim();
    	if( isBuyOrSell(tmpactiontype) != null ) {
    		int actionnumber = 0;
			double actionprice = 0;
			String actionstockname = "";
			Boolean buysell = false;
			
			 //[20101206, 0104434770, 证券买入新, 海, 宜, 5000.00, 23.780, 35.67, 0.00, -118935.67, 999775.64]
				int size = tmplinelist.size();
				actionnumber = Integer.parseInt(tmplinelist.get(size-6).trim().substring(0, tmplinelist.get(size-6).indexOf(".")) ); 
				double actionsingleprice =  Double.parseDouble(tmplinelist.get(size-5).trim()); //买入单价
				double actionshouxufei = Double.parseDouble(tmplinelist.get(size -4).trim());//手续费
				double actionyinhuishui = Double.parseDouble(tmplinelist.get(size -3).trim());//印花税
				actionprice = calChenBen (tmpactiontype,actionnumber,actionsingleprice,actionshouxufei,actionyinhuishui);//加上费率后的单价
				
				for(int i=2;i<size-6;i++)
					actionstockname = actionstockname.trim() + tmplinelist.get(i);
				actionstockname = actionstockname.trim().substring(4, actionstockname.length());
				
				buysell = isBuyOrSell(tmpactiontype);
				
				generateFormatedLineResult (tmpactiondate.trim(),tmpactiontype.substring(0, 4).trim(),buysell.toString().trim(),actionstockname.trim(),
						"XXXXXX",String.valueOf(actionnumber).trim(),String.valueOf(actionprice).trim(),line.trim() );
    	}
    }
    
	@Override
    public List<String> getResult() {
        return recoreslists;
    }
    
    private Boolean isBuyOrSell (String descriptioninrecords) 
	{
		Boolean buysell = null ;
		
		for(String maistr:buygjc ) {
			if(descriptioninrecords.contains(maistr) ) {
				buysell = true;
				return buysell;
			} 
		}
		for(String sellstr:sellgjc) {
			if(descriptioninrecords.contains(sellstr) ) {
				buysell = false;
				return buysell;
			}
		}
//		
//		if(descriptioninrecords.contains("买入") || descriptioninrecords.contains("红股入帐") ||  descriptioninrecords.contains("配股入帐")
//				|| descriptioninrecords.contains("申购中签") || descriptioninrecords.contains("配售中签") )
//			buysell = true;
//		else if(descriptioninrecords.contains("卖出") )
//			buysell = false;
		
		return buysell;
	}
    
    private List<String> buygjc = Lists.newArrayList(); //买入关键词
    private List<String> sellgjc = Lists.newArrayList(); //卖出关键词
    public void initializeIsBuySellList ()
    {
    	File sysconfigfilexml = new File (sysconfig.getQuanShangJiaoYiSheZhi());
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
		
		Element jiaoyiele = xmlroot.element("JiaoYiGuanJianCiZhaoShang");
		Iterator it = jiaoyiele.elementIterator();
   	 	while (it.hasNext()) {
   	 	  Element element = (Element) it.next();
		  if(element.attribute("isbuy").getText().toLowerCase().equals("true"))
			  buygjc.add(element.getText()); 
		  else if(element.attribute("isbuy").getText().toLowerCase().equals("false"))
			  sellgjc.add(element.getText());
   	 	}
    }
}


///*
//* 这是老算法
//*/
//private void parseLineUsingNewFormate2(String line, List<String> tmplinelist)
//{
//	String tmpactiondate = tmplinelist.get(0).trim();
//	String tmpactiontype = tmplinelist.get(3).trim();
//	if( isBuyOrSell(tmpactiontype) != null) {
//		
//		int actionnumber = 0;
//		double actionprice = 0;
//		String actionstockname = null;
//		Boolean buysell = false;
//		try { 
//			 String tmpastnumber = tmplinelist.get(4).trim();
//			 actionnumber = Integer.parseInt(tmpastnumber.substring(0, tmpastnumber.indexOf(".")) );
//			 
//			 double actionsingleprice =  Double.parseDouble(tmplinelist.get(5).trim()); //买入单价
//			 double actionshouxufei = Double.parseDouble(tmplinelist.get(6).trim());//手续费
//			 double actionyinhuishui = Double.parseDouble(tmplinelist.get(7).trim());//印花税
//			 actionprice = calChenBen (tmpactiontype,actionnumber,actionsingleprice,actionshouxufei,actionyinhuishui);
//			 actionstockname = tmpactiontype.substring(4, tmpactiontype.length() );
//			 buysell = isBuyOrSell(tmpactiontype);
//			 
//		} catch ( java.lang.StringIndexOutOfBoundsException ex ) { //如果股票名称中间有空格会导致拆分出来的数组不对，要重新拆分
//			tmplinelist = Splitter.onPattern("\\s{3}+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);
//			
//			String stocknumber = tmplinelist.get(3).trim();
//			actionnumber = Integer.parseInt(stocknumber.substring(0, stocknumber.indexOf(".")) );
//			double actionsingleprice =  Double.parseDouble(tmplinelist.get(4).trim()); //买入单价
//			double actionshouxufei = Double.parseDouble(tmplinelist.get(5).trim());//手续费
//			double actionyinhuishui;
//			try {
//				actionyinhuishui = Double.parseDouble(tmplinelist.get(6).trim());//印花税
//			} catch ( java.lang.NumberFormatException ex2) {
//				List<String> tmpactionyinhuishui = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(6).trim());
//				actionyinhuishui = Double.parseDouble(tmpactionyinhuishui.get(0).trim());//印花税
//			}
//			actionprice = calChenBen (tmpactiontype,actionnumber,actionsingleprice,actionshouxufei,actionyinhuishui);
//			buysell = isBuyOrSell(tmplinelist.get(0)); 
//			
//			List<String> tmpactnamelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(2));
//			actionstockname = tmpactnamelist.get(0).substring(4, tmpactnamelist.get(0).length()); 
//			for(int i=3;i<tmpactnamelist.size();i++)
//				actionstockname = actionstockname + tmpactnamelist.get(i);
//		}
//		
//		String lineresult = tmpactiondate.trim()  // 日期
//							+ "|" + tmpactiontype.substring(0, 4).trim() //买入卖出描述
//							+ "|" + buysell.toString().trim() //标记是买入还是卖出
//							+ "|" + actionstockname.trim() //持仓股票名字
//							+ "|" + "XXXXXX" //持仓股票代码，招商是没有代码的，但其他券商有，预留
//							+ "|" + String.valueOf(actionnumber).trim()
//							+ "|" + String.valueOf(actionprice).trim()
//							+ "|" + line.trim();
//							;
// 	recoreslists.add(lineresult);
// 	//System.out.println(lineresult);
//	}
//	
//}
///*
//* 这是老算法
//*/
//private void parseLineUsingOldFormate2(String line, List<String> tmplinelist) 
//{
//	String tmpactiondate = tmplinelist.get(0).trim();
//	String tmpactiontype = tmplinelist.get(2).trim();
//	if( isBuyOrSell(tmpactiontype) != null ) {
//		
//		int actionnumber = 0;
//		double actionprice = 0;
//		String actionstockname = null;
//		Boolean buysell = false;
//		try { 
//			 actionnumber = Integer.parseInt(tmplinelist.get(3).trim().substring(0, tmplinelist.get(3).indexOf(".")) );
//			 
//			double actionsingleprice =  Double.parseDouble(tmplinelist.get(4).trim()); //买入单价
//			double actionshouxufei = Double.parseDouble(tmplinelist.get(5).trim());//手续费
//			double actionyinhuishui = Double.parseDouble(tmplinelist.get(6).trim());//印花税
//			actionprice = actionsingleprice + (actionshouxufei+actionyinhuishui) / actionnumber ; //加上费率后的单价
//
//			actionstockname = tmplinelist.get(2).substring(4, tmplinelist.get(2).length() );
//			 buysell = isBuyOrSell(tmplinelist.get(2));
//			 
//		} catch ( java.lang.StringIndexOutOfBoundsException ex ) { //如果股票名称中间有空格会导致拆分出来的数组不对，要重新拆分
//			tmplinelist = Splitter.onPattern("\\s{3}+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);
//			
//			String stocknumber = tmplinelist.get(1).trim();
//			actionnumber = Integer.parseInt(stocknumber.substring(0, stocknumber.indexOf(".")) );
//			double actionsingleprice =  Double.parseDouble(tmplinelist.get(2).trim()); //买入单价
//			double actionshouxufei = Double.parseDouble(tmplinelist.get(3).trim());//手续费
//			double actionyinhuishui ;
//			try {
//				actionyinhuishui = Double.parseDouble(tmplinelist.get(4).trim());//印花税
//			} catch ( java.lang.NumberFormatException ex2) {
//				List<String> tmpactionyinhuishui = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(4).trim());
//				actionyinhuishui = Double.parseDouble(tmpactionyinhuishui.get(0).trim());//印花税
//			}
//			actionprice = actionsingleprice + (actionshouxufei+actionyinhuishui) / actionnumber ; //加上费率后的单价
//
//			buysell = isBuyOrSell(tmplinelist.get(0)); 
//
//			List<String> tmpactnamelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(0));
//			actionstockname = tmpactnamelist.get(2).substring(4, tmpactnamelist.get(2).length()); 
//			for(int i=3;i<tmpactnamelist.size();i++)
//				actionstockname = actionstockname + tmpactnamelist.get(i);
//			
//		}
//		
//		String lineresult = tmpactiondate.trim()  // 日期
//							+  "|" + tmpactiontype.substring(0, 4).trim() //买入卖出描述
//							+ "|" + buysell.toString().trim() //标记是买入还是卖出
//							+ "|" + actionstockname.trim() //持仓股票名字
//							+ "|" + "XXXXXX" //持仓股票代码，招商是没有代码的，但其他券商有，预留
//							+ "|" + String.valueOf(actionnumber).trim()
//							+ "|" + String.valueOf(actionprice).trim()
//							+ "|" + line.trim();
//							;
// 	recoreslists.add(lineresult);
// 	//System.out.println(lineresult);
//	}
//	
//}
