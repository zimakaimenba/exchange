package com.exchangeinfomanager.accountconfiguration.AccountsInfo;



import java.util.ArrayList;
import java.util.Date;

public class AccountXinYongPuTong  extends AccountInfoBasic 
{
	public AccountXinYongPuTong(String accountname) 
	{
		super(accountname);
		super.setAccounttype("��ȯ");
	}
	
	
	public void zhuanruguquan (StockChiCangInfo stockchicanginfo, int guquanshu, double guquandanjia,String shuoming)
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
//			 (super.getChicanggupiaochenben()).add(tmpstockchicanginfo);
//		}
		
//		this.actionBuy( new Date(),stockcode, guquanshu, guquandanjia,shuoming);
		
	}
	
	public void zhuanchuguquan (StockChiCangInfo stockchicanginfo, int guquanshu, double guquandanjia,String shuoming)
	{
		String stockcode = stockchicanginfo.getChicangcode();
		int tmpindex = (super.getAllChiCangGuPiaoList()).indexOf(stockcode);
		if(tmpindex != -1) {
			int currentgushu = ((StockChiCangInfo)(super.getAllChiCangGuPiaoMap()).get(tmpindex)).getChicanggushu();
			if( 0 == (currentgushu - guquanshu)) { //ת����ֲ�Ϊ0
				(super.getAllChiCangGuPiaoMap()).remove(tmpindex);
				(super.getAllChiCangGuPiaoList()).remove(tmpindex);
			} else {
				((StockChiCangInfo)(super.getAllChiCangGuPiaoMap()).get(tmpindex)).setChicanggushu(0 - guquanshu);
				((StockChiCangInfo)(super.getAllChiCangGuPiaoMap()).get(tmpindex)).setChicangchenben(0 - guquandanjia * guquanshu);
			}
		} else {
			System.out.println("���˻�δ���иù�Ʊ");
		}
		
		this.actionSell(new Date(),stockcode, guquanshu, guquandanjia,shuoming);
	}

	
}