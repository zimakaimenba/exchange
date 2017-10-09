package com.exchangeinfomanager.asinglestockinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.google.common.base.Strings;

public class Stock extends BkChanYeLianTreeNode {

	public Stock(String myowncode1,String name)
	{
		super(myowncode1,name);
		nodetype = BanKuaiAndStockBasic.BKGEGU;
	}
	
	private String suoshujiaoyisuo;
	private boolean aNewDtockCodeIndicate = false;
//	private Date gainiantishidate;
//	private String gainiantishi;
//	private Date quanshangpingjidate;
//	private String quanshangpingji;
//	private Date fumianxiaoxidate;
//	private String fumianxiaoxi;
//	private String zhengxiangguan;
//	private String fuxiangguan;
//	private String jingZhengDuiShou;
//	private String keHuCustom;
	private String checklistXml;
	
	private Object[][] zdgzmrmcykRecords ;

//	private Multimap<String> chiCangAccountNameList; //���иù�Ʊ�������˻�������
	private HashMap<String,AccountInfoBasic> chiCangAccounts; //���иù�Ʊ�������˻�<�˻����˻���Ϣ>
	private HashMap<String,String> suoShuSysBanKuai; //����ͨ����ϵͳ��� <���code,�������>
	private  HashMap<String,Integer> sysBanKuaiWeight; //����ͨ����ϵͳ���Ȩ��
		
	private HashSet<String> suoShuZdyBanKuai; //�����Զ�����


		
	public void addNewChiCangAccount (AccountInfoBasic acnt)
	{
		if(chiCangAccounts == null)
			chiCangAccounts = new HashMap<String,AccountInfoBasic>();
		try {
			String acntname = acnt.getAccountName();
			chiCangAccounts.put(acntname, acnt);
		} catch(java.lang.NullPointerException e) {
			e.printStackTrace();
			
		}
	}
	
	public void removeChiCangAccount (AccountInfoBasic acnt)
	{
		String acntname = acnt.getAccountName();
		chiCangAccounts.remove(acntname);
	}
	
	/**
	 * @return the chiCangAccountNameList
	 */
	public HashMap<String, AccountInfoBasic> getChiCangAccounts () {
		return chiCangAccounts;
	}
	public ArrayList<String> getChiCangAccountsNameList() 
	{
		
		return new  ArrayList<String>(chiCangAccounts.keySet());
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

	public boolean isInTdxBanKuai (String tdxbk)
	{
		if(this.suoShuSysBanKuai.size() == 0) //sizeΪ0�����ɲ�������ȫû�а�飬Ϊ0˵�������ݿ����������⣬�Ƕ�����true
			return true;
		if(this.suoShuSysBanKuai.containsKey(tdxbk))
			return true;
		else 
			return false;
	}
	/**
	 * @return the suoShuBanKuai
	 */
	public HashMap<String,String> getGeGuSuoShuTDXSysBanKuaiList() {
		return suoShuSysBanKuai;
	}

	/**
	 * @param tmpsysbk the suoShuBanKuai to set
	 */
	public void setGeGuSuoShuTDXSysBanKuaiList(HashMap<String,String> tmpsysbk) {
		this.suoShuSysBanKuai = tmpsysbk;
	}
	/**
	 * @return the sysBanKuaiWeight
	 */
	public HashMap<String, Integer> getGeGuSuoShuBanKuaiWeight() {
		return sysBanKuaiWeight;
	}
	/**
	 * @param sysBanKuaiWeight the sysBanKuaiWeight to set
	 */
	public void setGeGuSuoShuBanKuaiWeight(HashMap<String, Integer> sysBanKuaiWeight) {
		this.sysBanKuaiWeight = sysBanKuaiWeight;
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
	
	ArrayList<String> nodeallchanyelian ;
	
	public void setGeGuAllChanYeLianInfo(ArrayList<String> ggcyl) 
	{
		this.nodeallchanyelian = ggcyl;
	}
	public ArrayList<String> getGeGuAllChanYeLianInfo() 
	{
		return this.nodeallchanyelian;
	}
	
	/**
	 * @return the sysBanKuaichenjiaoe
	 */
//	private HashMap<String, Double> sysBanKuaichenjiaoe;
//	public HashMap<String, Double> getSysBanKuaiChenJiaoE() {
//		return sysBanKuaichenjiaoe;
//	}
//	/**
//	 * @param sysBanKuaichenjiaoe the sysBanKuaichenjiaoe to set
//	 */
//	public void setSysBanKuaiChenJiaoE(HashMap<String, Double> sysBanKuaichenjiaoe) {
//		this.sysBanKuaichenjiaoe = sysBanKuaichenjiaoe;
//	}
	
	public  void setParseFileStockSet (HashSet<String> parsefilestockset2)
	 {
	    	if(super.parsefilestockset == null) {
	    		this.parsefilestockset = new HashSet<String> ();
	    		this.parsefilestockset = parsefilestockset2;
	    	}
	    	else
	    		this.parsefilestockset = parsefilestockset2;
	 }
	

}
