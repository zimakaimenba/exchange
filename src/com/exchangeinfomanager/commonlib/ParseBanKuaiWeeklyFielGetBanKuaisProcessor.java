package com.exchangeinfomanager.commonlib;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.io.LineProcessor;

public class ParseBanKuaiWeeklyFielGetBanKuaisProcessor implements LineProcessor<List<String>> 
{

private List<String> stocklists = Lists.newArrayList();

@Override
public boolean processLine(String line) throws IOException {
	if(line.trim().length() ==7) {
		if(line.startsWith("1")) { //上海的个股或板块
			if(!line.startsWith("16")) { //上海的板块
				stocklists.add(line.substring(1));
			}
		} else  {
			if(!line.startsWith("00") && !line.startsWith("03") ) { //深圳的板块
				stocklists.add(line.substring(1));
			}
		}
	}
 return true;
}
@Override
public List<String> getResult() {
 return stocklists;
}
}
