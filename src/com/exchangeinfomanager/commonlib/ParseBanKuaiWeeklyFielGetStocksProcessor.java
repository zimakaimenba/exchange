package com.exchangeinfomanager.commonlib;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.io.LineProcessor;

public class ParseBanKuaiWeeklyFielGetStocksProcessor implements LineProcessor<List<String>> 
{

private List<String> stocklists = Lists.newArrayList();

@Override
public boolean processLine(String line) throws IOException {
	if(line.trim().length() ==7) {
		if(line.startsWith("1")) { //�Ϻ��ĸ��ɻ���
			if(line.startsWith("16")) { //�Ϻ��ĸ���
				stocklists.add(line.substring(1));
			}
		} 
		else {
			if(!line.startsWith("0399") )
					if(line.startsWith("00") || line.startsWith("03") ) { //���ڵĸ���
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