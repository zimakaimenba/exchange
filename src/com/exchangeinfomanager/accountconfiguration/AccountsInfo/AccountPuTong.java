package com.exchangeinfomanager.accountconfiguration.AccountsInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AccountPuTong extends AccountInfoBasic 
{
	
	
	public AccountPuTong(String accountname) 
	{
		super(accountname);
		super.setAccounttype(super.TYPEPUTONG);
		this.hasxinyongacnt = false;
	}
	
	private boolean hasxinyongacnt; //����������˻��������˻�����Ϊtrue;
	
	/**
	 * @���ظ��˻��Ƿ��������˻�
	 */
	public boolean hasXinYongAccount() {
		return hasxinyongacnt;
	}

	/**
	 * @param glzh the glzh to set
	 */
	public void setHasXinYongAccount(boolean hasxinyongacnt2) {
		this.hasxinyongacnt = hasxinyongacnt2;
	}

	public void zhuanruguquan (StockChiCangInfo stockchicanginfo, int guquanshu, double guquandanjia,boolean guadan)
	{
//		String stockcode = stockchicanginfo.getChicangcode();
//		int tmpindex = (super.getChicanggupiao()).indexOf(stockcode);
//		if(tmpindex != -1) {	//�µĹ�Ʊ��ԭ�����ֲ�
//			((StockChiCangInfo)(super.getChicanggupiaochenben()).get(tmpindex)).setChicanggushu(guquanshu);
//			((StockChiCangInfo)(super.getChicanggupiaochenben()).get(tmpindex)).setChicangchenben(guquandanjia * guquanshu);
//		} else {
//			String stockname = stockchicanginfo.getChicangname();
//			StockChiCangInfo tmpstockchicanginfo = new StockChiCangInfo (stockcode, stockname,guquanshu, guquandanjia * guquanshu);
//			 (super.getChicanggupiao()).add(stockcode);
//			 (super.getChicanggupiaochenben()).put(tmpstockchicanginfo);
//		}
		
		//adbo.setActionSellBuyToDb (this,new Date(),stockcode,guquanshu,guquandanjia,"",true);
		
	}
	
	public void zhuanchuguquan (StockChiCangInfo stockchicanginfo, int guquanshu, double guquandanjia,boolean guadan)
	{
//		String stockcode = stockchicanginfo.getChicangcode();
//		int tmpindex = (super.getChicanggupiao()).indexOf(stockcode);
//		if(tmpindex != -1) {
//			int currentgushu = ((StockChiCangInfo)(super.getChicanggupiaochenben()).get(tmpindex)).getChicanggushu();
//			if( 0 == (currentgushu - guquanshu)) { //ת����ֲ�Ϊ0
//				(super.getChicanggupiaochenben()).remove(tmpindex);
//				(super.getChicanggupiao()).remove(tmpindex);
//			} else {
//				((StockChiCangInfo)(super.getChicanggupiaochenben()).get(tmpindex)).setChicanggushu(0 - guquanshu);
//				((StockChiCangInfo)(super.getChicanggupiaochenben()).get(tmpindex)).setChicangchenben(0 - guquandanjia * guquanshu);
//			}
//		} else {
//			System.out.println("���˻�δ���иù�Ʊ");
//		}
		
//		adbo.setActionSellBuyToDb (this,new Date(),stockcode,guquanshu,guquandanjia,"",false);	
//		
//		int tmpindex2 = (super.getChicanggupiao()).indexOf(stockcode);
//		if(tmpindex2 == -1) { //�ֲ�Ϊ0��
//			adbo.setChicangKongcang (this,stockcode);
//		}
	}
	
}
