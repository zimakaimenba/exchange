package com.exchangeinfomanager.nodes;

import org.apache.log4j.Logger;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class HanYuPinYing {

	private static Logger logger = Logger.getLogger(HanYuPinYing.class);
	
	public String getQuanBuOfPinYin (String chinese)
	{
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
	                	try{
	                		String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
	                		if(temp != null)
	                			output += temp[0];
	                	} catch (java.lang.NullPointerException e ) {
	                		e.printStackTrace();
	                	}
	                    
	                } else
	                    output += java.lang.Character.toString(input[i]);
	            }
	        } catch (BadHanyuPinyinOutputFormatCombination e) {
	            e.printStackTrace();
	        }
	        
	        return output;
	}
	
	public String getSouZiMuOfPinYin (String chinese)
	{
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
	
	public Boolean compareTwoStrings (String checkedstr, String comparedstr)
	{
		if(this.getQuanBuOfPinYin (checkedstr).equals(comparedstr) || this.getSouZiMuOfPinYin(checkedstr).equals(comparedstr) )
			return true;
		else
			return false;
	}
	

}
