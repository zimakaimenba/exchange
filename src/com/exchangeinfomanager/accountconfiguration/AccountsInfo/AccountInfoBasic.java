package com.exchangeinfomanager.accountconfiguration.AccountsInfo;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.exchangeinfomanager.database.AccountDbOperation;
import com.google.common.collect.Maps;


public class AccountInfoBasic extends CashAccountBasic
{

	public AccountInfoBasic(String accountname) 
	{
		super(accountname);
		
		this.totoalbuyguadanjiner = 0.0;
		this.totoalsellguadanjiner = 0.0;

		chicanggupiaodetailsmap = Maps.newHashMap();
	}

	private Double totoalbuyguadanjiner; //��������ҵ����
	private Double totoalsellguadanjiner; //���������ҵ����
	private HashMap<String,StockChiCangInfo> chicanggupiaodetailsmap;
	
	
	/**
	 * @��ǰ�����ֽ�,����Ļ�Ҫ����ǰ�ж����ֽ���ã��ҵ�ռ�����ʽ�
	 */
	public double getCurrentAvailableXianJin ()
	{
		return super.getCurrentCash() - totoalbuyguadanjiner;
	}
	/**
	 * @�ܵ���ҵ����
	 */
	public Double getTotoalBuyGuaDanJinEr() {
		return totoalbuyguadanjiner;
	}
	public void setTotoalBuyGuaDanJinEr(Double totoalguadanjiner2) {
		this.totoalbuyguadanjiner = totoalguadanjiner2;
	}
	/**
	 * @�ܵ����ҵ����
	 */
	public Double getTotoalSellGuaDanJinEr() {
		return totoalsellguadanjiner;
	}
	public void setTotoalSellGuaDanJinEr(Double totoalsellguadanjiner2) {
		this.totoalsellguadanjiner = totoalsellguadanjiner;
	}

	/*
	 * �ֲ����
	 */
	public StockChiCangInfo getStockChiCangInfoIndexOf (String stockcode)
	{
		return ((StockChiCangInfo)this.chicanggupiaodetailsmap.get(stockcode));
	}
	public boolean isChiCang (String stockcode)
	{
		if( (StockChiCangInfo)this.chicanggupiaodetailsmap.get(stockcode) != null )
			return true;
		else return false;
	}
	/*
	 * @�ֲֹ�Ʊ���
	 *  
	 */
	public double getChiCangGuPiaoChenBen (String stockcode) {
		return this.chicanggupiaodetailsmap.get(stockcode).getChicangchenben();
	}
	public ArrayList<String> getAllChiCangGuPiaoList() 
	{
		return new ArrayList<String>(this.chicanggupiaodetailsmap.keySet());
	}
	public int getChiCangGuPiaoGuShu (String gupiaoname)
	{
		if(chicanggupiaodetailsmap.get(gupiaoname) != null) {
			StockChiCangInfo tmpscc = chicanggupiaodetailsmap.get(gupiaoname);
			return tmpscc.getChicanggushu();
		} else 
			return 0;
		
	}
	public void addNewChiCangStock(StockChiCangInfo tmpstockchicanginfo) 
	{
		String tmpstockcode = tmpstockchicanginfo.getChicangcode();
		try {
			chicanggupiaodetailsmap.put(tmpstockcode,tmpstockchicanginfo);
		} catch (java.lang.NullPointerException e) {
			chicanggupiaodetailsmap = new  HashMap<String,StockChiCangInfo>();
			chicanggupiaodetailsmap.put(tmpstockcode,tmpstockchicanginfo);
		}
	}
	public HashMap<String,StockChiCangInfo> getAllChiCangGuPiaoMap() 
	{
			return chicanggupiaodetailsmap;
	}
	public void setAllChiCangGuPiaoMapgetAllChiCangGuPiaoMap(HashMap<String, StockChiCangInfo> chicanggupiaochenben) {
		this.chicanggupiaodetailsmap = chicanggupiaochenben;
	}


	/*
	 *  * ������㣬ֱ�ӷ���-1�����򷵻������ʣ������ֽ�
	 */
	public int actionBuyPreCheck (Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		 System.out.println("��Ҫ���ʽ�Ϸ��Լ��");
		if( (this.getCurrentAvailableXianJin() - guquanshu * guquandanjia) >=0 ) { //�ж��Ƿ����㹻���ֽ�
			return 1;
		} else {
			System.out.print("�˻��ֽ��㣬�޷�����");
			return -1;
		} 
	}
	
	/*
	 * �жϼ�¼�������Ƿ��ǵ��죬����ǵ��죬���ǵ��ռ�¼��Ҫ�ж��ʽ�ĺϷ��ԣ������ǹ�ȥ��¼���룬�����ж��ʽ�ĺϷ���
	 */
	private boolean checkIfSameDay (Date actionDay) 
	{
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(new Date() );
		cal2.setTime(actionDay);
		boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
		                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
		
		return sameDay;
	}
	/*
	 * ������㣬ֱ�ӷ���-1�����򷵻������ʣ������ֽ�
	 */
	public Double actionBuy(Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		//System.out.println(this.getAccountName() + "����ǰ���ֽ�" + this.getCurrentAvailableXianJin() );
		if(checkIfSameDay(actionDay) == true) { //�жϼ�¼�������Ƿ��ǵ��죬����ǵ��죬���ǵ��ռ�¼��Ҫ�ж��ʽ�ĺϷ��ԣ������ǹ�ȥ��¼���룬�����ж��ʽ�ĺϷ���
			if( actionBuyPreCheck ( actionDay, stockcode,  guquanshu,  guquandanjia, shuoming) <0 ) {
				System.out.println("δͨ������Ϸ��Լ��");
				return null;
			}
		}
			
			super.setCash( super.getCurrentCash() - guquanshu * guquandanjia); //�����ֽ�
			super.setTotoalZiJingChenBen(super.getTotalZiJingChenBen() + guquanshu * guquandanjia); //�����ֽ�
			
			double chengben = guquanshu * guquandanjia; 
			if (!this.isChiCang(stockcode) ) { //�����Ʊ��
				StockChiCangInfo tmpnewacnt = new StockChiCangInfo(stockcode, "", guquanshu, chengben);
				this.chicanggupiaodetailsmap.put(stockcode, tmpnewacnt);
			} else {  //ԭ�й�Ʊ
				((StockChiCangInfo)chicanggupiaodetailsmap.get(stockcode)).setChicanggushu(guquanshu);
				((StockChiCangInfo)chicanggupiaodetailsmap.get(stockcode)).setChicangchenben(chengben);
			}
			System.out.println(this.getAccountName() + "��������" + this.chicanggupiaodetailsmap.get(stockcode).getChicanggushu() );
			return this.getCurrentAvailableXianJin();
	}
	/*
	 * �����ֲּ�� 
	 */
	public int actionSellPreCheck (Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		if( !this.isChiCang(stockcode) ) { 
			System.out.println("�����˻�û�иù�Ʊ��");
			return -1;
		} else if(guquanshu > this.getChiCangGuPiaoGuShu(stockcode) && this.getAccountType() != super.TYPERONGQUAN  ) { //��ȯ�˻����⣬��������
			System.out.println("�µ������˻���������ô�������ù�Ʊ��");
			return -1;
		} 
		
		return 1;

	}
	/*
	 * ����ֲ����ֱ꣬�ӷ�����������������й�Ʊ�ͷ���null 
	 */
	
	public String actionSell(Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		if(checkIfSameDay(actionDay) == true) { //�жϼ�¼�������Ƿ��ǵ��죬����ǵ��죬���ǵ��ռ�¼��Ҫ�ж��ʽ�ĺϷ��ԣ������ǹ�ȥ��¼���룬�����ж��ʽ�ĺϷ���
			if( actionSellPreCheck ( actionDay, stockcode,  guquanshu,  guquandanjia, shuoming) <0 ) {
				System.out.println("δͨ�������Ϸ��Լ��");
				return "FAIL";
			}
		}
		

			Double finalprofit = null;

			//�Գֲֹ�Ʊ���и���
			int currentgushu = this.getChiCangGuPiaoGuShu(stockcode);

			if( 0 == (currentgushu - guquanshu)) { //ת����ֲ�Ϊ0
				finalprofit = this.getChiCangGuPiaoChenBen(stockcode) - guquanshu* guquandanjia; //�ֲ�Ϊ0��Ҫ��ż���ӯ��
				finalprofit = 0 - finalprofit; //Ϊ��ֵ˵��ӯ������ֵ˵������
				super.setHistoryProfit(super.getHistoryProfit() + finalprofit); //�����˻������˻�����ֱ����ϵͳ����Ľ���������û��ֶ�������
								
				this.chicanggupiaodetailsmap.remove(stockcode);
				
				System.out.println(super.getAccountName() + "�����������ǣ�" + finalprofit);
			} else {
				((StockChiCangInfo)this.getStockChiCangInfoIndexOf(stockcode)).setChicanggushu(0 - guquanshu); //������Ʊ������Ӧ����Ӧ����
				((StockChiCangInfo)this.getStockChiCangInfoIndexOf(stockcode)).setChicangchenben(0-guquandanjia * guquanshu);
				
				System.out.println(super.getAccountName() + "����" + stockcode + " " + guquanshu + ",������" + ((StockChiCangInfo)chicanggupiaodetailsmap.get(stockcode)).getChicanggushu());
			}
			
			//�Ա��˻�������и���
			super.setCash(super.getCurrentCash() + guquanshu * guquandanjia);  //�����ֽ�
			System.out.println(this.getAccountName() + "���������ֽ�" + this.getCurrentAvailableXianJin() );
			
			//�������˻���ǰ�ɱ��ļ��㷽����û��ã����ڲ�ȡ�����гֲֳɱ���һ��ı��취��
			double tmpchenben =0;
			for(StockChiCangInfo value: chicanggupiaodetailsmap.values()) {
				tmpchenben = tmpchenben + value.getChicangchenben(); 
			}
			this.setTotoalZiJingChenBen(tmpchenben);
		
			if(finalprofit != null)
				return String.valueOf(finalprofit);
			else
				return null;
 	}
	/*
	 * ������㣬�ͷ���null�����򷵻عҵ����ж����ֽ�
	 */
	public Double  actionBuyGuaDan(Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		if(checkIfSameDay(actionDay) == true) { //�жϼ�¼�������Ƿ��ǵ��죬����ǵ��죬���ǵ��ռ�¼��Ҫ�ж��ʽ�ĺϷ��ԣ������ǹ�ȥ��¼���룬�����ж��ʽ�ĺϷ���
			if( actionBuyPreCheck ( actionDay, stockcode,  guquanshu,  guquandanjia, shuoming) <0 ) {
				System.out.println("δͨ������ҵ��Ϸ��Լ��");
				return null;
			}
		}
		
		this.setTotoalBuyGuaDanJinEr ( this.getTotoalBuyGuaDanJinEr() +  guquanshu * guquandanjia); //���ӹҵ��Ľ��
		
		return this.getCurrentAvailableXianJin();
	}
	private void actionBuyGuaDanCannelled (Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		//��¼�Ĺҵ������Ӧ����
		this.setTotoalBuyGuaDanJinEr ( this.getTotoalBuyGuaDanJinEr() -guquanshu * guquandanjia);
	}
	/*
	 * ������㣬�ͷ���-1�����򷵻عҵ����ж����ֽ�
	 */
	public String actionSellGuaDan(Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		if(checkIfSameDay(actionDay) == true) { //�жϼ�¼�������Ƿ��ǵ��죬����ǵ��죬���ǵ��ռ�¼��Ҫ�ж��ʽ�ĺϷ��ԣ������ǹ�ȥ��¼���룬�����ж��ʽ�ĺϷ���
			if( actionSellPreCheck ( actionDay, stockcode,  guquanshu,  guquandanjia, shuoming) <0 ) {
				System.out.println("δͨ�������ҵ��Ϸ��Լ��");
				return "FAIL";
			}
		}
		
		this.setTotoalSellGuaDanJinEr ( this.getTotoalSellGuaDanJinEr() + guquanshu * guquandanjia);
		return String.valueOf( this.getCurrentAvailableXianJin()  );
	}
	private void actionSellGuaDanCannelled (Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		this.setTotoalSellGuaDanJinEr ( this.getTotoalSellGuaDanJinEr() - guquanshu * guquandanjia);
	}
	/*
	 * �ҵ�������2��������һ���ǰѹҵ��������ʽ��������һ��������������������
	 */
	public Double actionBuyGuadanDone (Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		actionBuyGuaDanCannelled(actionDay, stockcode, guquanshu, guquandanjia, shuoming);
		return actionBuy(actionDay, stockcode, guquanshu, guquandanjia, shuoming);
	}
	/*
	 * �ҵ�������2��������һ���ǰѹҵ��������ʽ��������һ��������������������
	 */
	public String actionSellGuadanDone (Date actionDay,String stockcode, int guquanshu, double guquandanjia,String shuoming)
	{
		actionSellGuaDanCannelled(actionDay, stockcode, guquanshu, guquandanjia, shuoming);
		return  actionSell(actionDay, stockcode, guquanshu, guquandanjia, shuoming);
	}
	



}
