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
    	if( tmplinelist.get(0).trim().contains("�ͻ���") && zhanghuid == null) {
    		zhanghuid =  tmplinelist.get(0);//.replace("�ͻ���:", "").trim();
    		recoreslists.add( zhanghuid ); //ֱ�ӷ����û��˺�ID
    	}
    	
    	if( Pattern.matches("\\d{8}",tmplinelist.get(0)) ) {
    		parseLineUsingOldFormate (line,tmplinelist);
    	}
//    	if(line.contains("���д���"))
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
     * ���ɸ�ʽ�������ݼ�¼
     */
    private void generateFormatedLineResult(String actiondate, String actiontype, String actionbuysell, String actionstockname, String acioncode,
			String actionnumber, String actionprice, String fullline) 
    {
    	String lineresult = actiondate  // ����
				+  "|" + actiontype //������������
				+ "|" + actionbuysell //��������뻹������
				+ "|" + actionstockname.trim() //�ֲֹ�Ʊ����
				+ "|" + acioncode//�ֲֹ�Ʊ���룬������û�д���ģ�������ȯ���У�Ԥ��
				+ "|" + actionnumber
				+ "|" + actionprice
				+ "|" + fullline
				;
		recoreslists.add(lineresult);
		System.out.println(lineresult);
		
	}
	/*
     * ����ÿ������������ĳɱ�
     */
	private double calChenBen(String tmpactiontype, int actionnumber,double actionsingleprice, double actionshouxufei,double actionyinhuishui) 
	{
		double actionprice;
		if(isBuyOrSell(tmpactiontype)) { //����Ļ������׷����ǳɱ�Ҫ����
			 actionprice = actionsingleprice + ( (actionshouxufei+actionyinhuishui) / actionnumber) ; //���Ϸ��ʺ�ĵ���
		} else { //���������׷���Ҫ��
			actionprice = actionsingleprice - ( (actionshouxufei+actionyinhuishui) / actionnumber) ; //���Ϸ��ʺ�ĵ���
		}
		
		return actionprice;
	}

	private void parseLineUsingOldFormate(String line, List<String> tmplinelist) 
    {
		//�ɽ�����        ҵ������                    �������          ʣ����          ����          �ɶ�����          ֤ȯ����        ֤ȯ����        ������־                    �ɽ��۸�        �ɽ�����        ��ע                                                 
		//20170228        ֤ȯ����                    -12015.00         -617712.26        �����        0107955999        300348          �����Ƽ�        ֤ȯ����                    24.020          500.00          ֤ȯ����                                             
		String tmpactiondate = tmplinelist.get(0).trim();
    	String tmpactiontype = tmplinelist.get(1).trim();
    	if( isBuyOrSell(tmpactiontype) != null ) {
    		int actionnumber = 0;
			double actionprice = 0;
			String actionstockname = "";
			Boolean buysell = false;

			int size = tmplinelist.size();
			actionnumber =  Math.abs(  Integer.parseInt(tmplinelist.get(size-2).trim().substring(0, tmplinelist.get(size-2).indexOf(".")) )  ); 
//				double actionsingleprice =  Double.parseDouble(tmplinelist.get(size-).trim()); //���뵥��
//				double actionshouxufei = Double.parseDouble(tmplinelist.get(size -4).trim());//������
//				double actionyinhuishui = Double.parseDouble(tmplinelist.get(size -3).trim());//ӡ��˰
				actionprice =  Math.abs( Double.parseDouble(tmplinelist.get(2).trim()) / actionnumber  );//���Ϸ��ʺ�ĵ���,����Ӧ�ñ��µ��۸�ߣ�����Ӧ�ñ��µ��۸��
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
//		if(descriptioninrecords.contains("����") || descriptioninrecords.contains("�������") ||  descriptioninrecords.contains("�������")
//				|| descriptioninrecords.contains("�깺��ǩ") || descriptioninrecords.contains("������ǩ") )
//			buysell = true;
//		else if(descriptioninrecords.contains("����") )
//			buysell = false;
		
		return buysell;
	}
    
    private List<String> buygjc = Lists.newArrayList(); //����ؼ���
    private List<String> sellgjc = Lists.newArrayList(); //�����ؼ���
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



