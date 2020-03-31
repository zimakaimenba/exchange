package com.exchangeinfomanager.Services;

import java.util.List;
import java.util.Map;

public interface ServicesForZhiDingYiBanKuai 
{
	public Map<String, String> getZhiDingYiBanKuaiLists(); //从tdx得到的自定义板块设置
	public List<String> getSpecificZhiDingYiBanKuaiInfo (String zdybkname);
}
