package com.exchangeinfomanager.bankuaichanyelian;

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
	
	public String getBanKuaiNameOfPinYin (String chinese)
	 {
		 //System.out.println(chinese);
		 //这部分获得全部拼音
		 HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
	        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
	        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	        format.setVCharType(HanyuPinyinVCharType.WITH_V);
	 
	        char[] input = chinese.trim().toCharArray();
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
	       //System.out.println(output);
	       
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
           //System.out.println( pybf.toString().replaceAll("\\W", "").trim() ); 
           
           return pybf.toString().replaceAll("\\W", "").trim(); //返回首字母拼音
	 } 
	

}
