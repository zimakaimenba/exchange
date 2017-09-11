package com.exchangeinfomanager.asinglestockinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.google.common.base.Strings;

public class ASingleStockInfo 
{

	public ASingleStockInfo(String stockcode) 
	{
		this.stockcode = stockcode;
	}

	private String stockcode;
	private String stockname;
	private boolean aNewDtockCodeIndicate = false;
	private Date gainiantishidate;
	private String gainiantishi;
	private Date quanshangpingjidate;
	private String quanshangpingji;
	private Date fumianxiaoxidate;
	private String fumianxiaoxi;
	private String zhengxiangguan;
	private String fuxiangguan;
	private String jingZhengDuiShou;
	private String keHuCustom;
	private String checklistXml;
	
	private Object[][] zdgzmrmcykRecords ;

	private ArrayList<String> chiCangAccountNameList; //持有该股票的所有账户的名字
	private HashMap<String,String> suoShuSysBanKuai; //所属通达信系统板块
	private HashSet<String> suoShuZdyBanKuai; //所属自定义板块
	
	public boolean addNewChiCangAccountName (String tmpacntname)
	{
		try {
			if (chiCangAccountNameList.contains(tmpacntname))
				return false;
			else {
				chiCangAccountNameList.add(tmpacntname);
				return true;
			} 
		} catch (Exception e) {
			chiCangAccountNameList = new ArrayList<String> ();
			chiCangAccountNameList.add(tmpacntname);
			return true;
		}
	}
	public boolean removeChiCangAccountName (String tmpacntname)
	{
		if (chiCangAccountNameList.contains(tmpacntname))
			chiCangAccountNameList.remove(tmpacntname);
		else {
			return false;
		} 
		
		return true;
	}
	
	/**
	 * @return the chiCangAccountNameList
	 */
	public ArrayList<String> getChiCangAccountNameList() {
		return chiCangAccountNameList;
	}
	/**
	 * @return the aNewDtockCodeIndicate
	 */
	public boolean isaNewDtockCodeIndicate() {
		return aNewDtockCodeIndicate;
	}
	/**
	 * @param aNewDtockCodeIndicate the aNewDtockCodeIndicate to set
	 */
	public void setaNewDtockCodeIndicate(boolean aNewDtockCodeIndicate) {
		this.aNewDtockCodeIndicate = aNewDtockCodeIndicate;
	}
	/**
	 * @return the stockcode
	 */
	public String getStockcode() {
		return stockcode;
	}
	/**
	 * @return the checklistXml
	 */
	public String getChecklistXml() {
		return checklistXml;
	}
	/**
	 * @param checklistXml the checklistXml to set
	 */
	public void setChecklistXml(String checklistXml) {
		this.checklistXml = checklistXml;
	}
	/**
	 * @return the zdgzmrmcykRecords
	 */
	public Object[][] getZdgzMrmcZdgzYingKuiRecords() {
		return zdgzmrmcykRecords;
	}
	/**
	 * @param zdgzmrmcykRecords the zdgzmrmcykRecords to set
	 */
	public void setZdgzmrmcykRecords(Object[][] zdgzmrmcykRecords) {
		this.zdgzmrmcykRecords = zdgzmrmcykRecords;
	}

	public String getStockname ()
	{
		if(Strings.isNullOrEmpty(this.stockname))
			return "";
		
		return this.stockname;
	}
	public void setStockname (String stockname)
	{
		 this.stockname = stockname;
	}
	/**
	 * @return the jingZhengDuiShou
	 */
	public String getJingZhengDuiShou() {
		return jingZhengDuiShou;
	}
	/**
	 * @param jingZhengDuiShou the jingZhengDuiShou to set
	 */
	public void setJingZhengDuiShou(String jingZhengDuiShou) {
		this.jingZhengDuiShou = jingZhengDuiShou;
	}
	/**
	 * @return the keHuCustom
	 */
	public String getKeHuCustom() {
		return keHuCustom;
	}
	/**
	 * @param keHuCustom the keHuCustom to set
	 */
	public void setKeHuCustom(String keHuCustom) {
		this.keHuCustom = keHuCustom;
	}

	public Date getGainiantishidate() {
		return gainiantishidate;
	}
	public void setGainiantishidate(Date gainiantishidate) {
		this.gainiantishidate = gainiantishidate;
	}
	public String getGainiantishi() {
		return gainiantishi;
	}
	public void setGainiantishi(String gainiantishi) {
		this.gainiantishi = gainiantishi;
	}
	public Date getQuanshangpingjidate() {
		return quanshangpingjidate;
	}
	public void setQuanshangpingjidate(Date quanshangpingjidate) {
		this.quanshangpingjidate = quanshangpingjidate;
	}
	public String getQuanshangpingji() {
		return quanshangpingji;
	}
	public void setQuanshangpingji(String quanshangpingji) {
		this.quanshangpingji = quanshangpingji;
	}
	public Date getFumianxiaoxidate() {
		return fumianxiaoxidate;
	}
	public void setFumianxiaoxidate(Date fumianxiaoxidate) {
		this.fumianxiaoxidate = fumianxiaoxidate;
	}
	public String getFumianxiaoxi() {
		return fumianxiaoxi;
	}
	public void setFumianxiaoxi(String fumianxiaoxi) {
		this.fumianxiaoxi = fumianxiaoxi;
	}
	public String getZhengxiangguan() {
		return zhengxiangguan;
	}
	public void setZhengxiangguan(String zhengxiangguan) {
		this.zhengxiangguan = zhengxiangguan;
	}
	public String getFuxiangguan() {
		return fuxiangguan;
	}
	public void setFuxiangguan(String fuxiangguan) {
		this.fuxiangguan = fuxiangguan;
	}

	/**
	 * @return the suoShuBanKuai
	 */
	public HashMap<String, String> getSuoShuTDXSysBanKuai() {
		return suoShuSysBanKuai;
	}

	/**
	 * @param tmpsysbk the suoShuBanKuai to set
	 */
	public void setSuoShuTDXSysBanKuai(HashMap<String, String> tmpsysbk) {
		this.suoShuSysBanKuai = tmpsysbk;
	}
	/**
	 * @return the suoShuBanKuai
	 */
	public HashSet<String> getSuoShuTDXZdyBanKuai() {
		return suoShuZdyBanKuai;
	}

	/**
	 * @param tmpbk the suoShuBanKuai to set
	 */
	public void setSuoShuTDXZdyBanKuai(HashSet<String> tmpbk) {
		this.suoShuZdyBanKuai = tmpbk;
	}
	
	ArrayList<String> chanyelian ;
	public void setChanYeLianInfo(ArrayList<String> ggcyl) 
	{
		this.chanyelian = ggcyl;
	}
	public ArrayList<String> gettChanYeLianInfo() 
	{
		return this.chanyelian;
	}



}
