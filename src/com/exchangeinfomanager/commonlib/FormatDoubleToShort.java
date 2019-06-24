package com.exchangeinfomanager.commonlib;

public class FormatDoubleToShort 
{

	public static Double formateDoubleToShort(Double curcje) 
	{
		if(curcje >= 100000000) {
			curcje = curcje / 100000000;
    		
    	}  	else if(curcje >= 10000000 && curcje <100000000) {
    		curcje = curcje / 10000000;
    		
    	}  	else if(curcje >= 1000000 && curcje <10000000) {
    		curcje = curcje / 1000000;
    		
    	} else if(curcje >= 100000 && curcje <1000000) {
    		curcje = curcje / 100000;
    		
    	} else if(curcje >= 10000 && curcje <100000) {
    		curcje = curcje / 10000;
    		
    	}
		return curcje;
	}

	public static String getNumberChineseDanWei (Double number) 
	{
		String danwei = "";
    	if(number >= 100000000) {
//    		number = number / 100000000;
    		danwei = "亿";
    	}  	else if(number >= 10000000 && number <100000000) {
//    		number = number / 10000000;
    		danwei = "千万";
    	}  	else if(number >= 1000000 && number <10000000) {
//    		number = number / 1000000;
    		danwei = "百万";
    	} else if(number >= 100000 && number <1000000) {
//    		number = number / 100000;
    		danwei = "十万";
    	} else if(number >= 10000 && number <100000) {
//    		number = number / 10000;
    		danwei = "万";
    	}
    	
    	return danwei;
		
	}
}
