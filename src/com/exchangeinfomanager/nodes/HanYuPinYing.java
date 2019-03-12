package com.exchangeinfomanager.nodes;

import org.apache.log4j.Logger;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class HanYuPinYing {

	public HanYuPinYing() {
		// TODO Auto-generated constructor stub
	}
	private static Logger logger = Logger.getLogger(HanYuPinYing.class);
	
	public String getBanKuaiNameOfPinYin (String chinese)
	 {
		 //logger.debug(chinese);
		 //这部分获得全部拼音
		 HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
	        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
	        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	        format.setVCharType(HanyuPinyinVCharType.WITH_V);
	 
	        char[] input;
	        try {
	        	input = chinese.trim().toCharArray();
	        } catch (java.lang.NullPointerException e ) {
	        	input = "".toCharArray();
	        }
	        String output = "";
	 
	        try {
	            for (int i = 0; i < input.length; i++) {
	                if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
	                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
	                    output += temp[0];
	                } else
	                    output += java.lang.Character.toString(input[i]);
	            }
	        } catch (BadHanyuPinyinOutputFormatCombination e) {
	            e.printStackTrace();
	        }
	       //logger.debug(output);
	       
	       //这部分获得首字母拼音
	        StringBuffer pybf = new StringBuffer();  
           char[] arr = chinese.toCharArray();  
           HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();  
           defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
           defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
           for (int i = 0; i < arr.length; i++) {  
                   if (arr[i] > 128) {  
                           try {  
                                   String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);  
                                   if (temp != null) {  
                                           pybf.append(temp[0].charAt(0));  
                                   }  
                           } catch (BadHanyuPinyinOutputFormatCombination e) {  
                                   e.printStackTrace();  
                           }  
                   } else {  
                           pybf.append(arr[i]);  
                   }  
           }  
           //logger.debug( pybf.toString().replaceAll("\\W", "").trim() ); 
           
           return pybf.toString().replaceAll("\\W", "").trim(); //返回首字母拼音
	 } 
	

}
