package com.exchangeinfomanager.nodes.operations;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
/*
 * 
 */
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
		BanKuai shdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		BanKuai szdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.TDXBK);
		treeallstockrootdapan.setDaPanContents(shdpbankuai,szdpbankuai);
		
		
	}
	/*
	 * ��ð�飬��ͬ����֤/����ָ���ĳɽ������Ա�ס������֤/���ڵĳɽ�����¼���ڿ������ȫ�ġ�
	 */
	public BanKuai getBanKuai (BanKuai bankuai,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,requiredstartday,requiredendday,period);
		
//		String bkcode = bankuai.getMyOwnCode();
//		if(!bkcode.equals("399001") && !bkcode.equals("999999") ) { //2������ָ��
//			getDaPan (requiredstartday,requiredendday,period);
//		}
		
		return bankuai;
	}
	public BanKuai getBanKuai (String bkcode,LocalDate requiredstartday,LocalDate requiredendday,String period) 
	{
		BanKuai bankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		if(bankuai == null)
			return null;
		
		bankuai = this.getBanKuai (bankuai,requiredstartday,requiredendday,period);
		return bankuai;
	}
		/*
	 *����K�����ƣ� ֱ�Ӵ����ݿ��ж�ȡ���ݣ��͸��ɲ�һ��
	 */
	public BanKuai getBanKuaiKXian (String bkcode,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		BanKuai bk = (BanKuai)this.treecyl.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		bk = (BanKuai) this.getBanKuaiKXian(bk, requiredstartday,requiredendday, period);
		return bk;
	}
	public BanKuai getBanKuaiKXian (BanKuai bk,LocalDate requiredstartday,LocalDate requiredenddayString,String period)
	{
		bk = bkdbopt.getBanKuaiKXianZouShi (bk,requiredstartday,requiredenddayString,period);
		
		return bk;
	}
	public BanKuai getBanKuaiQueKouInfo (String bkcode,LocalDate requiredrecordsday,LocalDate requiredendday,String period)
	{
		BanKuai bankuai = (BanKuai)this.treecyl.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		bankuai = (BanKuai) this.getBanKuaiQueKouInfo(bankuai, requiredrecordsday, requiredendday, period);
		return bankuai;
	}
	public BanKuai getBanKuaiQueKouInfo (BanKuai bk,LocalDate requiredrecordsday,LocalDate requiredendday,String period)
	{
		bk = bkdbopt.getStocksInBanKuaiQueKouTimeRangTongJIResult (bk,requiredrecordsday,requiredendday,period);
		
		return bk;
	}
	public void syncBanKuaiData (BanKuai bk)
	{
		LocalDate bkstartday = bk.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getRecordsStartDate();
		LocalDate bkendday = bk.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getRecordsEndDate();
		
		this.getBanKuaiKXian(bk, bkstartday, bkendday, TDXNodeGivenPeriodDataItem.DAY);
		this.getBanKuaiQueKouInfo(bk, bkstartday, bkendday, TDXNodeGivenPeriodDataItem.WEEK);
		
	}
	/*
	 * ֻҪ��������ʱ�������ڶ������Ǹð��ĸ��ɣ���鶼����� 
	 */
	public BanKuai getAllGeGuOfBanKuai (BanKuai bankuai,String period) 
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getRecordsEndDate();
		
		if(bkstartday == null || bkendday == null) 
			return bankuai;
		
		
		//ͬ�����ĸ���
		bankuai = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (bankuai,bkstartday,bkendday,treecyl);
		ArrayList<StockOfBanKuai> allbkgg = bankuai.getAllCurrentBanKuaiGeGu();
		for(StockOfBanKuai stockofbk : allbkgg)   {
    		  stockofbk = this.getGeGuOfBanKuai(bankuai, stockofbk,period );
//    		  Stock stock = (Stock)this.treecyl.getSpecificNodeByHypyOrCode(stockofbk.getMyOwnCode(), BkChanYeLianTreeNode.TDXGG);
    		  Stock stock = this.getStock(stockofbk.getMyOwnCode(), bkstartday, bkendday, period);
		}
		
		return bankuai;
	}
	private StockOfBanKuai getGeGuOfBanKuai(BanKuai bankuai, StockOfBanKuai stockofbk,String period)
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getRecordsEndDate();
		
		if(bkstartday == null && bkendday == null) //��鱾��û�����ݣ��޷��ҳ���Ӧ�ĸ��ɵ����ݣ�����ԭ·����
			return stockofbk;
		
		stockofbk = bkdbopt.getGeGuZhanBiOfBanKuai (bankuai,stockofbk, bkstartday, bkendday,period);

		return stockofbk;
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
		BanKuai bankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		if(bankuai == null)
			return null;
		
		StockOfBanKuai stock = bankuai.getBanKuaiGeGu(stockcode);
		if(stock == null)
			return null;
		
		stock = this.getGeGuOfBanKuai( bankuai,  stock,period);

		return stock;
	}

//	private BanKuai getBanKuai (BanKuai bankuai,LocalDate requiredrecordsday,String period)
//	{
//		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getRecordsStartDate();
//		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getRecordsEndDate();
//		
//		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange() + 3,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		
//		//�ж�ʱ�����ع�ϵ���Ա�����Ƿ���Ҫ�����ݿ��в�ѯ�¼�¼
//		if(bkstartday == null || bkendday == null) { //��û�����ݣ�ֱ����
//			bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,requirestart,requireend,period);
////			bankuai = bkdbopt.getStockOfBanKuaiQueKouTimeRangTongJIResult (bankuai,requirestart,requireend,period);
//		} else	{
//			HashMap<String,LocalDate> startend = null;
//			if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
//				startend = nodeWeekTimeStampRelation (bkstartday,bkendday,requirestart,requireend);
//			else if(period.equals(TDXNodeGivenPeriodDataItem.DAY)) //��ʱû����
//				;
//			else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH)) //��ʱû����
//				;
//			
//			if(!startend.isEmpty()) {
//				LocalDate searchstart,searchend,position;
//				searchstart = startend.get("searchstart"); 
//				searchend = startend.get("searchend");
//				bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,searchstart,searchend,period);
////				bankuai = bkdbopt.getStockOfBanKuaiQueKouTimeRangTongJIResult (bankuai,searchstart,searchend,period);
//			}
//		}
//	
//		String bkcode = bankuai.getMyOwnCode();
//		if(!bkcode.equals("399001") && !bkcode.equals("999999") ) { //2������ָ��
//			getDaPan (requiredrecordsday,period);
//		}
//		
//		
//		
//		return bankuai;
//	}
//	private BanKuai getBanKuai (String bkcode,LocalDate requiredrecordsday,String period) 
//	{
//		BanKuai bankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
//		if(bankuai == null)
//			return null;
//		
//		bankuai = this.getBanKuai (bankuai,requiredrecordsday,period);
//		return bankuai;
//	}

	
	
	/*
	 * ͬ��ÿ�������������ɵ����ڰ���������ݣ�ͬʱͬ���ù��Լ�������
	 */
//	private StockOfBanKuai getGeGuOfBanKuai(BanKuai bankuai, StockOfBanKuai stock,String period)
//	{
//		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getRecordsStartDate();
//		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getRecordsEndDate();
//		
//		if(bkstartday == null && bkendday == null) //��鱾��û�����ݣ��޷��ҳ���Ӧ�ĸ��ɵ����ݣ�����ԭ·����
//			return stock;
//		
//		LocalDate stockstartday = stock.getNodeXPeroidData(period).getRecordsStartDate();
//		LocalDate stockendday = stock.getNodeXPeroidData(period).getRecordsEndDate();
//		
//		if(stockstartday == null || stockendday == null ) { //��û�����ݣ�ֱ����
//			stock = bkdbopt.getGeGuZhanBiOfBanKuai (bankuai,stock, bkstartday, bkendday,period);
//			Stock stockroot = stock.getStock(); //����stock������
//			this.getStock(stockroot, bkstartday, bkendday, period);
////			bkdbopt.getStockZhanBi (stock.getStock(), bkstartday, bkendday,period);
//		} else {
//			HashMap<String,LocalDate> startend = null;
//			if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
//				startend = nodeWeekTimeStampRelation (stockstartday,stockendday,bkstartday,bkendday);
//			else if(period.equals(TDXNodeGivenPeriodDataItem.DAY)) //��ʱû����
//				;
//			else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH)) //��ʱû����
//				;
//			
//			if(!startend.isEmpty()) {
//				LocalDate searchstart,searchend,position;
//				searchstart = startend.get("searchstart"); 
//				searchend = startend.get("searchend");
//				stock = bkdbopt.getGeGuZhanBiOfBanKuai (bankuai,stock,searchstart,searchend,period);
//				this.getStock (stock.getStock(), searchstart, searchend,period);
//			}
//			
//			startend = null;
//		}
//		
//		return stock;
//
//	}
	/*
	 * 
	 */
	public void getDaPanKXian (LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		BanKuai shdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		BanKuai szdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.TDXBK);
		BanKuai cybdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("399006",BkChanYeLianTreeNode.TDXBK);
		
		shdpbankuai = (BanKuai) this.getBanKuaiKXian(shdpbankuai, requiredstartday, requiredendday,period);
		szdpbankuai = (BanKuai) this.getBanKuaiKXian(szdpbankuai, requiredstartday, requiredendday,period);
		cybdpbankuai = (BanKuai) this.getBanKuaiKXian(cybdpbankuai, requiredstartday, requiredendday, period);
		
	}
	/*
	 * ͬ�����̳ɽ���
	 */
	public void getDaPan (LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		BanKuai shdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		BanKuai szdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.TDXBK);
		BanKuai cybdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("399006",BkChanYeLianTreeNode.TDXBK);
		
		shdpbankuai = bkdbopt.getBanKuaiZhanBi (shdpbankuai,requiredstartday,requiredendday,period);
		szdpbankuai = bkdbopt.getBanKuaiZhanBi (szdpbankuai,requiredstartday,requiredendday,period);
		cybdpbankuai = bkdbopt.getBanKuaiZhanBi (szdpbankuai,requiredstartday,requiredendday,period);
	}
	public void syncDaPanData() 
	{
		BanKuai shdpbankuai = (BanKuai) treecyl.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		LocalDate bkstartday = shdpbankuai.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getRecordsStartDate();
		LocalDate bkendday = shdpbankuai.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getRecordsEndDate();
		
		this.getDaPanKXian (bkstartday,bkendday,TDXNodeGivenPeriodDataItem.DAY);
		
	}
	/*
	 * ȷ��ʱ���ֱ�ӵĹ�ϵ,����
	 */
//	private  HashMap<String, LocalDate> nodeWeekTimeStampRelation (LocalDate curstart,LocalDate curend, LocalDate requiredstart, LocalDate requiredend) 
//	{
//		HashMap<String,LocalDate> startend = new HashMap<String,LocalDate> (); 
//		if(  CommonUtility.isInSameWeek(curstart,requiredstart) &&  CommonUtility.isInSameWeek(requiredend,curend)    ) {//��������
//			return startend;
//		}	
//		else if( (requiredstart.isAfter(curstart) || requiredstart.isEqual(curstart) ) && (requiredend.isBefore(curend) || requiredend.isEqual(curend) ) ) {  //��������
//			return startend;
//		}
//		else if( !CommonUtility.isInSameWeek(curstart,requiredstart)  && requiredstart.isBefore(curstart) //��������1,ǰȱʧ
//				&& (requiredend.isBefore(curend) || CommonUtility.isInSameWeek(requiredend,curend) )    ) {
//			LocalDate searchstart,searchend;
//			searchstart = requiredstart; 
//			searchend = curstart.with(DayOfWeek.SATURDAY).minus(1,ChronoUnit.WEEKS);
//			startend.put("searchstart", searchstart);
//			startend.put("searchend",  searchend);
//			return startend;
//		}
//		else if ( (CommonUtility.isInSameWeek(curstart,requiredstart) || requiredstart.isAfter(curstart) )  //��������2����ȱʧ
//				&& requiredend.isAfter(curend) &&  !CommonUtility.isInSameWeek(requiredend,curend)    ) {
//			LocalDate searchstart,searchend;
//			searchstart = curend.with(DayOfWeek.MONDAY).plus(1,ChronoUnit.WEEKS); 
//			searchend = requiredend;
//			startend.put("searchstart", searchstart);
//			startend.put("searchend", searchend);
//			return startend;
//		}
//		else if( requiredstart.isBefore(curstart) && requiredend.isAfter(curend)  ) {//��������3�� ǰ��˫ȱʧ���������Ŀǰ�������ƺ������ܷ�������ʱ��д
////			logger.info("��������3�� ǰ��˫ȱʧ!");
//			startend.put("searchstart", requiredstart);
//			startend.put("searchend", requiredend);
//			return startend;
//		}
//
//		return null;
//	}
	//����
//	private  HashMap<String, LocalDate> nodeDayTimeStampRelation (LocalDate curstart,LocalDate curend, LocalDate requiredstart, LocalDate requiredend) 
//	{
//		HashMap<String,LocalDate> startend = new HashMap<String,LocalDate> (); 
//		if(  curstart.isEqual(requiredstart) && requiredend.isEqual(curend)    ) {//��������
//			return startend;
//		}	
//		else if( (requiredstart.isAfter(curstart) || requiredstart.isEqual(curstart) ) && (requiredend.isBefore(curend) || requiredend.isEqual(curend) ) ) {  //��������
//			return startend;
//		}
//		else if( !curstart.isEqual(requiredstart)  && requiredstart.isBefore(curstart) //��������1,ǰȱʧ
//				&& (requiredend.isBefore(curend) || requiredend.isEqual(curend) )    ) {
//			LocalDate searchstart,searchend;
//			searchstart = requiredstart; 
//			searchend = curstart.with(DayOfWeek.SATURDAY).minus(1,ChronoUnit.WEEKS);
//			startend.put("searchstart", searchstart);
//			startend.put("searchend",  searchend);
//			return startend;
//		}
//		else if ( (curstart.isEqual(requiredstart) || requiredstart.isAfter(curstart) )  //��������2����ȱʧ
//				&& requiredend.isAfter(curend) &&  !requiredend.isEqual(curend)    ) {
//			LocalDate searchstart,searchend;
//			searchstart = curend.with(DayOfWeek.MONDAY).plus(1,ChronoUnit.WEEKS); 
//			searchend = requiredend;
//			startend.put("searchstart", searchstart);
//			startend.put("searchend", searchend);
//			return startend;
//		}
//		else if( requiredstart.isBefore(curstart) && requiredend.isAfter(curend)  ) {//��������3�� ǰ��˫ȱʧ���������Ŀǰ�������ƺ������ܷ�������ʱ��д
//			logger.debug("��������3�� ǰ��˫ȱʧ���������Ŀǰ�������ƺ������ܷ���");
//			return startend;
//		}
//
//		return null;
//	}
	
//	/*
//	 *����K�����ƣ� ֱ�Ӵ����ݿ��ж�ȡ���ݣ��͸��ɲ�һ��
//	 */
//	public BanKuai getBanKuaiKXian (String bkcode,LocalDate requiredrecordsday,String period)
//	{
//		BanKuai stock = (BanKuai)this.treecyl.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXGG);
//		stock = (BanKuai) this.getBanKuaiKXian(stock, requiredrecordsday, period);
//		return stock;
//	}
//	/*
//	 *����K�����ƣ�ֱ�Ӵ����ݿ��ж�ȡ���� ���͸��ɲ�һ��
//	 */
//	public BanKuai getBanKuaiKXian (BanKuai bk,LocalDate requiredrecordsday,String period) 
//	{
//		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		
//		try{
//			
//			NodeXPeriodDataBasic stockxdata = bk.getNodeXPeroidData(period);
//			LocalDate nodestartday = stockxdata.getRecordsStartDate();
//			LocalDate nodeendday = stockxdata.getRecordsEndDate();
//			
//			if(nodestartday == null || nodeendday == null) { //��û�����ݣ�ֱ����
//				bk = bkdbopt.getBanKuaiKXianZouShi (bk,requirestart,requireend,period);
//			} else {
//				HashMap<String,LocalDate> startend = null ;
//				if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
//					startend = nodeWeekTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(TDXNodeGivenPeriodDataItem.DAY)) 
//					startend = nodeDayTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH)) //��ʱû����
//					;
//				 
//				LocalDate searchstart,searchend,position;
//				if(!startend.isEmpty()) {
//					searchstart = startend.get("searchstart"); 
//					searchend = startend.get("searchend");
//
//					bk = bkdbopt.getBanKuaiKXianZouShi (bk,searchstart,searchend,period);
//				}
//				
//				startend = null;
//			}
//		} catch (java.lang.NullPointerException e) {
//			e.printStackTrace();
//		}
//		
//		return bk;
//		
//	}
//	public BanKuai getBanKuaiQueKouInfo (String bkcode,LocalDate requiredrecordsday,String period)
//	{
//		BanKuai bankuai = (BanKuai)this.treecyl.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXGG);
//		bankuai = (BanKuai) this.getBanKuaiQueKouInfo(bankuai, requiredrecordsday, period);
//		return bankuai;
//	}
//	public BanKuai getBanKuaiQueKouInfo (BanKuai bk,LocalDate requiredrecordsday,String period)
//	{
//		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		logger.debug("debug");
//		try{
//			
//			NodeXPeriodDataBasic stockxdata = bk.getNodeXPeroidData(period);
//			LocalDate nodestartday = stockxdata.getRecordsStartDate();
//			LocalDate nodeendday = stockxdata.getRecordsEndDate();
//			
//			if(nodestartday == null || nodeendday == null) { //��û�����ݣ�ֱ����
//				bk = bkdbopt.getStockOfBanKuaiQueKouTimeRangTongJIResult (bk,requirestart,requireend,period);
//			} else {
//				HashMap<String,LocalDate> startend = null ;
//				if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
//					startend = nodeWeekTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(TDXNodeGivenPeriodDataItem.DAY)) 
//					startend = nodeDayTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
//				else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH)) //��ʱû����
//					;
//				 
//				LocalDate searchstart,searchend,position;
//				if(!startend.isEmpty()) {
//					searchstart = startend.get("searchstart"); 
//					searchend = startend.get("searchend");
//
//					bk = bkdbopt.getStockOfBanKuaiQueKouTimeRangTongJIResult (bk,searchstart,searchend,period);
//				}
//				
//				startend = null;
//			}
//		} catch (java.lang.NullPointerException e) {
//			e.printStackTrace();
//		}
//		
//		return bk;
//
//		
//	}
	
	/*
	 * �������ɵ�ָ�����ڵ�ռ��
	 */
//	public Stock getStock (String stockcode,LocalDate requiredrecordsday,String period)
//	{
//		Stock stock = (Stock)this.treecyl.getSpecificNodeByHypyOrCode(stockcode,BkChanYeLianTreeNode.TDXGG);
//		if(stock == null)
//			return null;
//		
//		this.getStock(stock, requiredrecordsday, period);
//		
//		return stock;
//	}
//	public Stock getStock(Stock stock, LocalDate requirestart, LocalDate requireend,String period)
//	{
//		try{
//			NodeXPeriodDataBasic stockxdata = stock.getNodeXPeroidData(period);
//			LocalDate nodestartday = stockxdata.getRecordsStartDate();
//			LocalDate nodeendday = stockxdata.getRecordsEndDate();
//					
////			if(stock.getMyOwnCode().equals("300017"))
////				logger.debug("time to debug");
//			
//			if(nodestartday == null || nodeendday == null) { //��û�����ݣ�ֱ����
//				stock = bkdbopt.getStockZhanBi ((Stock)stock,requirestart,requireend,period); 
//				stock = bkdbopt.getStockQueKouTimeRangTongJIResult ((Stock)stock,requirestart,requireend,period);
//			} else {
//				HashMap<String,LocalDate> startend = null ;
////				if(period.equals(ChenJiaoZhanBiInGivenPeriod.WEEK))
//					startend = nodeWeekTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
////				else if(period.equals(ChenJiaoZhanBiInGivenPeriod.DAY)) 
////					startend = nodeDayTimeStampRelation (nodestartday,nodeendday,requirestart,requireend);
////				else if(period.equals(ChenJiaoZhanBiInGivenPeriod.MONTH)) //��ʱû����
//					;
//						 
//				LocalDate searchstart,searchend,position;
//				if(!startend.isEmpty()) {
//							searchstart = startend.get("searchstart"); 
//							searchend = startend.get("searchend");
//							
//							stock = bkdbopt.getStockZhanBi ((Stock)stock,searchstart,searchend,period);
//							stock = bkdbopt.getStockQueKouTimeRangTongJIResult ((Stock)stock,searchstart,searchend,period);
//				}
//				
//				startend = null;
//			}
//		} catch (java.lang.NullPointerException e) {
//					e.printStackTrace();
//		}
//		
//		return stock;
//	}
//	/*
//	 * 
//	 */
//	public Stock getStock(Stock stock, LocalDate requiredrecordsday, String period)
//	{
//		//ռ�Ȼ���Ҫ������/���ߣ���������û��̫�����壬����ר���ҳ���������ռ������
//		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		
//		stock = this.getStock(stock,requirestart,requireend,period);
//		
//		return stock;
//	}
	public Stock getStock (Stock stock,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		stock = bkdbopt.getStockZhanBi (stock,requiredstartday,requiredendday,period);
		
		return stock;
	}
	public Stock getStock (String stockcode,LocalDate requiredstartday,LocalDate requiredendday,String period) 
	{
		Stock stock = (Stock) treecyl.getSpecificNodeByHypyOrCode(stockcode,BkChanYeLianTreeNode.TDXGG);
		if(stock == null)
			return null;
		
		stock = this.getStock (stock,requiredstartday,requiredendday,period);
		return stock;
	}
	public Stock getStockQueKouInfo (String stockcode,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		Stock stock = (Stock)this.treecyl.getSpecificNodeByHypyOrCode(stockcode,BkChanYeLianTreeNode.TDXGG);
		stock = this.getStockQueKouInfo(stock, requiredstartday, requiredendday, period);
		return stock;
	}
	public Stock getStockQueKouInfo (Stock stock,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		stock = bkdbopt.getStockQueKouTimeRangTongJIResult (stock,requiredstartday,requiredendday,period);
		
		return stock;
	}
	/*
	 * 
	 */
	public Stock getStockKXian (String stockcode,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		Stock stock = (Stock)this.treecyl.getSpecificNodeByHypyOrCode(stockcode,BkChanYeLianTreeNode.TDXGG);
		stock = (Stock) this.getStockKXian(stock, requiredstartday, requiredendday,period);
		return stock;
	}
	/*
	 * ���ɵ�K�ߴ�CSV�����ȡ
	 */
	public Stock getStockKXian (Stock stock,LocalDate requiredstartday,LocalDate requiredendday,String period)
	{
		stock = (Stock)bkdbopt.getStockKXianZouShiFromCsv (stock,requiredstartday,requiredendday,period);
				
		return stock;
	}
	public void syncStockData (Stock stock)
	{
		LocalDate bkstartday = stock.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getRecordsStartDate();
		LocalDate bkendday = stock.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK).getRecordsEndDate();
		
		this.getStockKXian(stock, bkstartday, bkendday, TDXNodeGivenPeriodDataItem.DAY);
		this.getStockQueKouInfo(stock, bkstartday, bkendday, TDXNodeGivenPeriodDataItem.WEEK);
		
	}

	

}
