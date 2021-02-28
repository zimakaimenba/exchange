package com.exchangeinfomanager.zhidingyibankuai;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.exchangeinfomanager.commonlib.ParseBanKuaiWeeklyFielGetStocksProcessor;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.ServicesForZhiDingYiBanKuai;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class TDXZhiDingYiBanKuaiServices implements ServicesForZhiDingYiBanKuai
{
	public TDXZhiDingYiBanKuaiServices ()
	{
		this.bkdbopt = new BanKuaiDbOperation ();
	}
	
	private BanKuaiDbOperation bkdbopt;
	private HashMap<String, String> zdybkmap;
	
	@Override
	public Map<String, String> getZhiDingYiBanKuaiLists()
	{
		zdybkmap = this.bkdbopt.getTDXZiDingYiBanKuaiList ();
		return zdybkmap;
	}

	@Override
	public List<String> getSpecificZhiDingYiBanKuaiInfo(String zdybkname) 
	{
		String filename = zdybkmap.get(zdybkname); //str是自定义板块的名称
		File zdybk = new File(filename);
		List<String> readLines = null;
		try {
			readLines = Files.readLines(zdybk,Charsets.UTF_8,new ParseBanKuaiWeeklyFielGetStocksProcessor ());
		} catch (java.io.FileNotFoundException e) {
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return readLines;
	}

}
