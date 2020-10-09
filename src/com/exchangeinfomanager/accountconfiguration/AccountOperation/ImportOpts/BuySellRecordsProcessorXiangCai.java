package com.exchangeinfomanager.accountconfiguration.AccountOperation.ImportOpts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.LineProcessor;

public class BuySellRecordsProcessorXiangCai implements LineProcessor<List<String>>
{
    private List<String> recoreslists = Lists.newArrayList();
    private String zhanghuid = null;
    //private boolean newformate = false;
//	private SystemConfigration sysconfig;
   
	public BuySellRecordsProcessorXiangCai ()
	{
//		sysconfig = SystemConfigration.getInstance();
		initializeIsBuySellList ();
	}
	@Override
    public boolean processLine(String line) throws IOException {
    	List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);
    	//System.out.println(tmplinelist);
    	if(tmplinelist.isEmpty())
    		return true;
    	if( tmplinelist.get(0).trim().contains("客户号") && zhanghuid == null) {
    		zhanghuid =  tmplinelist.get(0);//.replace("客户号:", "").trim();
    		recoreslists.add( zhanghuid ); //直接返回用户账号ID
    	}
    	
    	if( Pattern.matches("\\d{8}",tmplinelist.get(0)) ) {
    		parseLineUsingOldFormate (line,tmplinelist);
    	}
//    	if(line.contains("银行代码"))
//    		newformate = true;
//    	
//        if( Pattern.matches("\\d{8}",tmplinelist.get(0)) ) {
//        	if(newformate == false)
//        		parseLineUsingOldFormate (line,tmplinelist);
//        	else parseLineUsingNewFormate (line,tmplinelist);
//        }
        
        return true;
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
		//成交日期        业务名称                    发生金额          剩余金额          币种          股东代码          证券代码        证券名称        买卖标志                    成交价格        成交数量        备注                                                 
		//20170228        证券买入                    -12015.00         -617712.26        人民币        0107955999        300348          长亮科技        证券买入                    24.020          500.00          证券买入                                             
		String tmpactiondate = tmplinelist.get(0).trim();
    	String tmpactiontype = tmplinelist.get(1).trim();
    	if( isBuyOrSell(tmpactiontype) != null ) {
    		int actionnumber = 0;
			double actionprice = 0;
			String actionstockname = "";
			Boolean buysell = false;

			int size = tmplinelist.size();
			actionnumber =  Math.abs(  Integer.parseInt(tmplinelist.get(size-2).trim().substring(0, tmplinelist.get(size-2).indexOf(".")) )  ); 
//				double actionsingleprice =  Double.parseDouble(tmplinelist.get(size-).trim()); //买入单价
//				double actionshouxufei = Double.parseDouble(tmplinelist.get(size -4).trim());//手续费
//				double actionyinhuishui = Double.parseDouble(tmplinelist.get(size -3).trim());//印花税
				actionprice =  Math.abs( Double.parseDouble(tmplinelist.get(2).trim()) / actionnumber  );//加上费率后的单价,买入应该比下单价格高，卖出应该比下单价格低
				String actionstockcode = tmplinelist.get(6);
				actionstockname = tmplinelist.get(7);
				buysell = isBuyOrSell(tmpactiontype);
				
				generateFormatedLineResult (tmpactiondate.trim(),tmpactiontype.trim(),buysell.toString().trim(),actionstockname.trim(),
						actionstockcode,String.valueOf(actionnumber).trim(),String.valueOf(actionprice).trim(),line.trim() );
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
			if(descriptioninrecords.equals(maistr) ) {
				buysell = true;
				return buysell;
			} 
		}
		for(String sellstr:sellgjc) {
			if(descriptioninrecords.equals(sellstr) ) {
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
    	File sysconfigfilexml = new File ((new SetupSystemConfiguration()).getQuanShangJiaoYiSheZhi());
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
		
		Element jiaoyiele = xmlroot.element("JiaoYiGuanJianCiXiangCai");
		Iterator it = jiaoyiele.elementIterator();
   	 	while (it.hasNext()) {
   	 	  Element element = (Element) it.next();
   	 	  String isbuytxt = element.attribute("isbuy").getText().toLowerCase();
		  if(element.attribute("isbuy").getText().toLowerCase().equals("true"))
			  buygjc.add(element.getText()); 
		  else if(element.attribute("isbuy").getText().toLowerCase().equals("false"))
			  sellgjc.add(element.getText());
   	 	}
    }
}



