package com.exchangeinfomanager.Services;

import java.util.List;
import java.util.Map;

public interface ServicesForZhiDingYiBanKuai 
{
	public Map<String, String> getZhiDingYiBanKuaiLists(); //��tdx�õ����Զ���������
	public List<String> getSpecificZhiDingYiBanKuaiInfo (String zdybkname);
}
