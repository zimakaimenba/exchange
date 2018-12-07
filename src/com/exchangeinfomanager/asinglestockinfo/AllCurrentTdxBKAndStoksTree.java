package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class AllCurrentTdxBKAndStoksTree 
{
	private AllCurrentTdxBKAndStoksTree ()
	{
		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
		
		initializeAllStocksTree ();
		setupDaPan ();
	}
	
	 // ����ʵ��  
	 public static AllCurrentTdxBKAndStoksTree getInstance ()
	 {  
	        return Singtonle.instance;  
	 }
	 
	 private static class Singtonle 
	 {  
	        private static AllCurrentTdxBKAndStoksTree instance =  new AllCurrentTdxBKAndStoksTree ();  
	 }
	
	
	private static Logger logger = Logger.getLogger(AllCurrentTdxBKAndStoksTree.class);
	
	private BanKuaiDbOperation bkdbopt;
	private BanKuaiAndStockTree treecyl;
	private SystemConfigration sysconfig;
	
	private void initializeAllStocksTree() 
	{
		DaPan alltopNode = new DaPan("000000","��������");
		
		ArrayList<Stock> allstocks = bkdbopt.getAllStocks ();
		for (Stock stock : allstocks ) {
		    alltopNode.add(stock);
//		    logger.debug(stock.getMyOwnCode() + "����" + stock.getType());
		}
		
		ArrayList<BanKuai> allbkandzs = bkdbopt.getTDXBanKuaiList ("all");
		for (BanKuai bankuai : allbkandzs ) {
		    alltopNode.add(bankuai);
//		    logger.debug(bankuai.getMyOwnCode()+ "����" + bankuai.getType());
		}

		treecyl = new BanKuaiAndStockTree(alltopNode,"ALLBKSTOCKS");
		allstocks = null;
	}
	
	public  BanKuaiAndStockTree getAllBkStocksTree()
	{
		return treecyl;
	}
	
	public void setupDaPan ()
	{
		DaPan treeallstockrootdapan = (DaPan)treecyl.getModel().getRoot();
		BanKuai shdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("999999",BanKuaiAndStockBasic.TDXBK);
		BanKuai szdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("399001",BanKuaiAndStockBasic.TDXBK);
		treeallstockrootdapan.setShangHai(shdpbankuai);
		treeallstockrootdapan.setShenZhen(szdpbankuai);
	}
	
	/*
	 * ��ð�飬��ͬ����֤/����ָ���ĳɽ������Ա�ס������֤/���ڵĳɽ�����¼���ڿ������ȫ�ġ�
	 */
	public BanKuai getBanKuai (BanKuai bankuai,LocalDate requiredrecordsday,String period)
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getRecordsEndDate();
		
		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		//�ж�ʱ�����ع�ϵ���Ա�����Ƿ���Ҫ�����ݿ��в�ѯ�¼�¼
		if(bkstartday == null || bkendday == null) { //��û�����ݣ�ֱ����
			bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,requirestart,requireend,period);
		} else	{
			HashMap<String,LocalDate> startend = null;
			if(period.equals(StockGivenPeriodDataItem.WEEK))
				startend = nodeWeekTimeStampRelation (bkstartday,bkendday,requirestart,requireend);
			else if(period.equals(StockGivenPeriodDataItem.DAY)) //��ʱû����
				;
			else if(period.equals(StockGivenPeriodDataItem.MONTH)) //��ʱû����
				;
			
			if(!startend.isEmpty()) {
				LocalDate searchstart,searchend,position;
				searchstart = startend.get("searchstart"); 
				searchend = startend.get("searchend");
				bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,searchstart,searchend,period);
			}
		}
	
		String bkcode = bankuai.getMyOwnCode();
		if(!bkcode.equals("399001") && !bkcode.equals("999999") ) { //2������ָ��
			getDaPan (requiredrecordsday,period);
		}
		
		return bankuai;
	}
	public BanKuai getBanKuai (String bkcode,LocalDate requiredrecordsday,String period) 
	{
		BanKuai bankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode(bkcode,BanKuaiAndStockBasic.TDXBK);
		if(bankuai == null)
			return null;
		
		bankuai = this.getBanKuai (bankuai,requiredrecordsday,period);
		return bankuai;
	}
	/*
	 * 
	 */
	public BanKuai getBanKuaiKXian (String bkcode,LocalDate requiredrecordsday,String period)
	{
		BanKuai stock = (BanKuai)this.treecyl.getSpecificNodeByHypyOrCode(bkcode,BanKuaiAndStockBasic.TDXGG);
		stock = (BanKuai) this.getNodeKXian(stock, requiredrecordsday, period);
		return stock;
	}
	/*
	 * 
	 */
	public BanKuai getBanKuaiKXian (BanKuai bankuai,LocalDate requiredrecordsday,String period)
	{
		bankuai = (BanKuai) this.getNodeKXian(bankuai, requiredrecordsday, period);
		return bankuai;
	}
	/*
	 * ֻҪ��������ʱ�������ڶ������Ǹð��ĸ��ɣ���鶼����� 
	 */
	public BanKuai getAllGeGuOfBanKuai (BanKuai bankuai,String period) 
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getRecordsEndDate();
		
		if(bkstartday == null || bkendday == null) {
			bkendday = LocalDate.now();
			bkstartday = bkendday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		}
		
		//ͬ�����ĸ���
		bankuai = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (bankuai,bkstartday,bkendday,treecyl);
		ArrayList<StockOfBanKuai> allbkgg = bankuai.getAllCurrentBanKuaiGeGu();
		for(StockOfBanKuai stock : allbkgg)   {
    		  stock = this.getGeGuOfBanKuai(bankuai, stock,period );
		}
		return bankuai;
	}
	/*
	 * ͬ��ÿ�������������ɵ����ڰ���������ݣ�ͬʱͬ���ù��Լ�������
	 */
	private StockOfBanKuai getGeGuOfBanKuai(BanKuai bankuai, StockOfBanKuai stock,String period)
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getRecordsEndDate();
		
		if(bkstartday == null && bkendday == null) //��鱾��û�����ݣ��޷��ҳ���Ӧ�ĸ��ɵ����ݣ�����ԭ·����
			return stock;
		
		LocalDate stockstartday = stock.getNodeXPeroidData(period).getRecordsStartDate();
		LocalDate stockendday = stock.getNodeXPeroidData(period).getRecordsEndDate();
		
		if(stockstartday == null || stockendday == null ) { //��û�����ݣ�ֱ����
			stock = bkdbopt.getGeGuZhanBiOfBanKuai (bankuai,stock, bkstartday, bkendday,period);
			Stock stockroot = stock.getStock(); //����stock������
			this.getStock(stockroot, bkstartday, bkendday, period);
//			bkdbopt.getStockZhanBi (stock.getStock(), bkstartday, bkendday,period);
		} else {
			HashMap<String,LocalDate> startend = null;
			if(period.equals(StockGivenPeriodDataItem.WEEK))
				startend = nodeWeekTimeStampRelation (stockstartday,stockendday,bkstartday,bkendday);
			else if(period.equals(StockGivenPeriodDataItem.DAY)) //��ʱû����
				;
			else if(period.equals(StockGivenPeriodDataItem.MONTH)) //��ʱû����
				;
			
			if(!startend.isEmpty()) {
				LocalDate searchstart,searchend,position;
				searchstart = startend.get("searchstart"); 
				searchend = startend.get("searchend");
				stock = bkdbopt.getGeGuZhanBiOfBanKuai (bankuai,stock,searchstart,searchend,period);
				this.getStock (stock.getStock(), searchstart, searchend,period);
			}
			
			startend = null;
		}
		
		return stock;

	}
	/*
	 * �����������ĳ�����ɵĳɽ���,��������Ƿ�����ø��ɵļ��
	 */
	public StockOfBanKuai getGeGuOfBanKuai(BanKuai bankuai, String stockcode,String period)
	{
		StockOfBanKuai stock = bankuai.getBanKuaiGeGu(stockcode);
		if(stock == null)
			return null;
		
		stock = this.getGeGuOfBanKuai( bankuai,  stock,period);
		return stock;
	}
	public StockOfBanKuai getGeGuOfBanKuai(String bkcode, String stockcode,String period) 
	{
		BanKuai bankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode(bkcode,BanKuaiAndStockBasic.TDXBK);
		if(bankuai == null)
			return null;
		
		StockOfBanKuai stock = bankuai.getBanKuaiGeGu(stockcode);
		if(stock == null)
			return null;
		
		stock = this.getGeGuOfBanKuai( bankuai,  stock,period);

		return stock;
	}

	/*
	 * ͬ�����̳ɽ���
	 */
	public void getDaPan (LocalDate requiredrecordsday,String period)
	{
		BanKuai shdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("999999",BanKuaiAndStockBasic.TDXBK);
		BanKuai szdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("399001",BanKuaiAndStockBasic.TDXBK);
		
		NodeXPeriodDataBasic shdpnodexdata = shdpbankuai.getNodeXPeroidData(period);
		LocalDate bkstartday = shdpnodexdata.getRecordsStartDate();
		LocalDate bkendday = shdpnodexdata.getRecordsEndDate();
		
		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
		//���̵�start����Ҫ��һ����͸�����һЩ��������ϵͳ��Ӧ�ٶ�������ߣ�����ԭ��ָ��Ϊʲô��
		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange()+12,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		
		//�ж�ʱ�����ع�ϵ���Ա�����Ƿ���Ҫ�����ݿ��в�ѯ�¼�¼
		if(bkstartday == null || bkendday == null) { //��û�����ݣ�ֱ����
			shdpbankuai = bkdbopt.getBanKuaiZhanBi (shdpbankuai,requirestart,requireend,period);
			szdpbankuai = bkdbopt.getBanKuaiZhanBi (szdpbankuai,requirestart,requireend,period);
		} else	{
			HashMap<String,LocalDate> startend = null;
			if(period.equals(StockGivenPeriodDataItem.WEEK))
				startend = nodeWeekTimeStampRelation (bkstartday,bkendday,requirestart,requireend);
			else if(period.equals(StockGivenPeriodDataItem.DAY)) //��ʱû����
				;
			else if(period.equals(StockGivenPeriodDataItem.MONTH)) //��ʱû����
				;
			
			LocalDate searchstart,searchend,position;
			if(!startend.isEmpty()) {
				searchstart = startend.get("searchstart"); 
				searchend = startend.get("searchend");
				shdpbankuai = bkdbopt.getBanKuaiZhanBi (shdpbankuai,searchstart,searchend,period);
				szdpbankuai = bkdbopt.getBanKuaiZhanBi (szdpbankuai,searchstart,searchend,period);
			}
			
			searchend = null;
		}
	}
	/*
	 * ȷ��ʱ���ֱ�ӵĹ�ϵ,����
	 */
	private  HashMap<String, LocalDate> nodeWeekTimeStampRelation (LocalDate curstart,LocalDate curend, LocalDate requiredstart, LocalDate requiredend) 
	{
		HashMap<String,LocalDate> startend = new HashMap<String,LocalDate> (); 
		if(  CommonUtility.isInSameWeek(curstart,requiredstart) &&  CommonUtility.isInSameWeek(requiredend,curend)    ) {//��������
			return startend;
		}	
		else if( (requiredstart.isAfter(curstart) || requiredstart.isEqual(curstart) ) && (requiredend.isBefore(curend) || requiredend.isEqual(curend) ) ) {  //��������
			return startend;
		}
		else if( !CommonUtility.isInSameWeek(curstart,requiredstart)  && requiredstart.isBefore(curstart) //��������1,ǰȱʧ
				&& (requiredend.isBefore(curend) || CommonUtility.isInSameWeek(requiredend,curend) )    ) {
			LocalDate searchstart,searchend;
			searchstart = requiredstart; 
			searchend = curstart.with(DayOfWeek.SATURDAY).minus(1,ChronoUnit.WEEKS);
			startend.put("searchstart", searchstart);
			startend.put("searchend",  searchend);
			return startend;
		}
		else if ( (CommonUtility.isInSameWeek(curstart,requiredstart) || requiredstart.isAfter(curstart) )  //��������2����ȱʧ
				&& requiredend.isAfter(curend) &&  !CommonUtility.isInSameWeek(requiredend,curend)    ) {
			LocalDate searchstart,searchend;
			searchstart = curend.with(DayOfWeek.MONDAY).plus(1,ChronoUnit.WEEKS); 
			searchend = requiredend;
			startend.put("searchstart", searchstart);
			startend.put("searchend", searchend);
			return startend;
		}
		else if( requiredstart.isBefore(curstart) && requiredend.isAfter(curend)  ) {//��������3�� ǰ��˫ȱʧ���������Ŀǰ�������ƺ������ܷ�������ʱ��д
//			logger.info("��������3�� ǰ��˫ȱʧ!");
			startend.put("searchstart", requiredstart);
			startend.put("searchend", requiredend);
			return startend;
		}

		return null;
	}
	//����
	private  HashMap<String, LocalDate> nodeDayTimeStampRelation (LocalDate curstart,LocalDate curend, LocalDate requiredstart, LocalDate requiredend) 
	{
		HashMap<String,LocalDate> startend = new HashMap<String,LocalDate> (); 
		if(  curstart.isEqual(requiredstart) && requiredend.isEqual(curend)    ) {//��������
			return startend;
		}	
		else if( (requiredstart.isAfter(curstart) || requiredstart.isEqual(curstart) ) && (requiredend.isBefore(curend) || requiredend.isEqual(curend) ) ) {  //��������
			return startend;
		}
		else if( !curstart.isEqual(requiredstart)  && requiredstart.isBefore(curstart) //��������1,ǰȱʧ
				&& (requiredend.isBefore(curend) || requiredend.isEqual(curend) )    ) {
			LocalDate searchstart,searchend;
			searchstart = requiredstart; 
			searchend = curstart.with(DayOfWeek.SATURDAY).minus(1,ChronoUnit.WEEKS);
			startend.put("searchstart", searchstart);
			startend.put("searchend",  searchend);
			return startend;
		}
		else if ( (curstart.isEqual(requiredstart) || requiredstart.isAfter(curstart) )  //��������2����ȱʧ
				&& requiredend.isAfter(curend) &&  !requiredend.isEqual(curend)    ) {
			LocalDate searchstart,searchend;
			searchstart = curend.with(DayOfWeek.MONDAY).plus(1,ChronoUnit.WEEKS); 
			searchend = requiredend;
			startend.put("searchstart", searchstart);
			startend.put("searchend", searchend);
			return startend;
		}
		else if( requiredstart.isBefore(curstart) && requiredend.isAfter(curend)  ) {//��������3�� ǰ��˫ȱʧ���������Ŀǰ�������ƺ������ܷ�������ʱ��д
			logger.debug("��������3�� ǰ��˫ȱʧ���������Ŀǰ�������ƺ������ܷ���");
			return startend;
		}

		return null;
	}
	/*
	 * �������ɵ�ָ�����ڵ�ռ��
	 */
	public Stock getStock (String stockcode,LocalDate requiredrecordsday,String period)
	{
		Stock stock = (Stock)this.treecyl.getSpecificNodeByHypyOrCode(stockcode,BanKuaiAndStockBasic.TDXGG);
		if(stock == null)
			return null;
		
		this.getStock(stock, requiredrecordsday, period);
		
		return stock;
	}
	public Stock getStock(Stock stock, LocalDate requirestart, LocalDate requireend,String period)
	{
		try{
			NodeXPeriodDataBasic stockxdata = stock.getNodeXPeroidData(period);
			LocalDate nodestartday = stockxdata.getRecordsStartDate();
			LocalDate nodeendday = stockxdata.getRecordsEndDate();
					
			if(nodestartday == null || nodeendday == null) { //��û�����ݣ�ֱ����
				stock = bkdbopt.getStockZhanBi ((Stock)stock,requirestart,requireend,period); 
			} else {
				HashMap<String,LocalDate> startend = null ;
//				if(period.equals(ChenJiaoZhanBiInGivenPeriod.WEEK))
					startend = nodeWeekTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(ChenJiaoZhanBiInGivenPeriod.DAY)) 
//					startend = nodeDayTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(ChenJiaoZhanBiInGivenPeriod.MONTH)) //��ʱû����
					;
						 
				LocalDate searchstart,searchend,position;
				if(!startend.isEmpty()) {
							searchstart = startend.get("searchstart"); 
							searchend = startend.get("searchend");
							
							stock = bkdbopt.getStockZhanBi ((Stock)stock,searchstart,searchend,period);
				}
				
				startend = null;
			}
		} catch (java.lang.NullPointerException e) {
					e.printStackTrace();
		}
		
		return stock;
	}
	/*
	 * 
	 */
	public Stock getStock(Stock stock, LocalDate requiredrecordsday, String period)
	{
		//ռ�Ȼ���Ҫ������/���ߣ���������û��̫�����壬����ר���ҳ���������ռ������
		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		stock = this.getStock(stock,requirestart,requireend,period);
		
		return stock;
	}
	/*
	 * �ú���Ϊÿһ���������������ƶ����ڵ�K������
	 */
	private BkChanYeLianTreeNode getNodeKXian (BkChanYeLianTreeNode bkorstock,LocalDate requiredrecordsday,String period) 
	{
		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		try{
			
			NodeXPeriodDataBasic stockxdata = bkorstock.getNodeXPeroidData(period);
			LocalDate nodestartday = stockxdata.getRecordsStartDate();
			LocalDate nodeendday = stockxdata.getRecordsEndDate();
			
			if(nodestartday == null || nodeendday == null) { //��û�����ݣ�ֱ����
				bkorstock = bkdbopt.getNodeKXianZouShi (bkorstock,requirestart,requireend,period);
			} else {
				HashMap<String,LocalDate> startend = null ;
				if(period.equals(StockGivenPeriodDataItem.WEEK))
					startend = nodeWeekTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
				else if(period.equals(StockGivenPeriodDataItem.DAY)) 
					startend = nodeDayTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
				else if(period.equals(StockGivenPeriodDataItem.MONTH)) //��ʱû����
					;
				 
				LocalDate searchstart,searchend,position;
				if(!startend.isEmpty()) {
					searchstart = startend.get("searchstart"); 
					searchend = startend.get("searchend");

					bkorstock = bkdbopt.getNodeKXianZouShi (bkorstock,searchstart,searchend,period);
				}
				
				startend = null;
			}
		} catch (java.lang.NullPointerException e) {
			e.printStackTrace();
		}
		
		return bkorstock;
		
	}
	/*
	 * 
	 */
	public Stock getStockKXian (String stockcode,LocalDate requiredrecordsday,String period)
	{
		Stock stock = (Stock)this.treecyl.getSpecificNodeByHypyOrCode(stockcode,BanKuaiAndStockBasic.TDXGG);
		stock = (Stock) this.getNodeKXian(stock, requiredrecordsday, period);
		return stock;
	}
	/*
	 * 
	 */
	public Stock getStockKXian (Stock stock,LocalDate requiredrecordsday,String period)
	{
		stock = (Stock) this.getNodeKXian(stock, requiredrecordsday, period);
		return stock;
		
//		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		
//		try{
//			
//			NodeXPeriodDataBasic stockxdata = stock.getNodeXPeroidData(period);
//			LocalDate nodestartday = stockxdata.getRecordsStartDate();
//			LocalDate nodeendday = stockxdata.getRecordsEndDate();
//			
//			if(nodestartday == null || nodeendday == null) { //��û�����ݣ�ֱ����
//				stock = (Stock)bkdbopt.getNodeKXianZouShi (stock,requirestart,requireend,period);
//			} else {
//				HashMap<String,LocalDate> startend = null ;
//				if(period.equals(StockGivenPeriodDataItem.WEEK))
//					startend = nodeWeekTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(StockGivenPeriodDataItem.DAY)) 
//					startend = nodeDayTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(StockGivenPeriodDataItem.MONTH)) //��ʱû����
//					;
//				 
//				LocalDate searchstart,searchend,position;
//				if(!startend.isEmpty()) {
//					searchstart = startend.get("searchstart"); 
//					searchend = startend.get("searchend");
//
//					stock = (Stock)bkdbopt.getNodeKXianZouShi (stock,searchstart,searchend,period);
//				}
//				
//				startend = null;
//			}
//		} catch (java.lang.NullPointerException e) {
//			e.printStackTrace();
//		}
//		
//		return stock;
	}


}
