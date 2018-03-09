package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;

public class StockOfBanKuai extends Stock
{
	public StockOfBanKuai (BanKuai bankuai1,Stock stock1) 
	{
		super (stock1.getMyOwnCode(),stock1.getMyOwnName());
		this.bankuai = bankuai1;
		this.stock = stock1;
		
		super.nodewkdata = new StockOfBanKuaiNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.WEEK) ;
//		super.nodedaydata = new StockOfBanKuaiNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.DAY) ;
		super.nodemonthdata = new StockOfBanKuaiNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.MONTH) ;
	}
	
	private static Logger logger = Logger.getLogger(StockOfBanKuai.class);
	private BanKuai bankuai;
	private Stock stock;
	private Integer quanzhong;
//	private StockNodeXPeriodData stockwkdataofbankuai; 
//	private StockNodeXPeriodData stockmonthdataofbankuai;
	
	public void setStockQuanZhong (Integer quanzhong)
	{
		this.quanzhong = quanzhong; 
	}
	public BanKuai getBanKuai ()
	{
		return this.bankuai;
	}
	public Integer getQuanZhong ()
	{
		return this.quanzhong;
	}
	public NodeXPeriodData getStockXPeriodDataForBanKuai (String period)
	{
		if(period.equals(ChenJiaoZhanBiInGivenPeriod.MONTH))
			return (StockOfBanKuaiNodeXPeriodData) this.nodemonthdata;
		else if(period.equals(ChenJiaoZhanBiInGivenPeriod.WEEK))
			return this.nodewkdata;
		else 
			return null;
	}
	
	public class StockOfBanKuaiNodeXPeriodData extends NodeXPeriodData
	{
		public StockOfBanKuaiNodeXPeriodData (String nodeperiodtype1) 
		{
			super(nodeperiodtype1);
		}
		
		@Override
		public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate)
		{
	
			if(periodlist == null)
				return null;
			
			ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
			if( curcjlrecord == null) 
				return null;
			
			int index = -1;
			while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //���ܿ��ܴ�������
				index --;
			}
			
			ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
			if(lastcjlrecord == null) //����ǰ���ǿգ�˵��Ҫôͣ�ƣ�Ҫô���¹�Ʊ
				return null;
			
			Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
			Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
			 
			return curcje - lastcje;
		}
		/*
		 * ���ָ���ܵļ�¼,�ʺ�stock/bankuai��dapan���Լ��ļ��㷽��
		 */
		public ChenJiaoZhanBiInGivenPeriod getSpecficRecord (LocalDate requireddate,int difference)
		{
			if(periodlist == null)
				return null;

			String nodeperiod = super.getNodeperiodtype();
			LocalDate expectedate = null;
			if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.WEEK)) { //������������ݣ�ֻҪ��������ͬ�����ɷ���
				expectedate = requireddate.plus(difference,ChronoUnit.WEEKS);
			} else if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.DAY)) { //������������ݣ�������ͬ�ſɷ���
				expectedate = requireddate.plus(difference,ChronoUnit.DAYS);
			}  else if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.MONTH)) { //������������ݣ�������ͬ�ſɷ���
				expectedate = requireddate.plus(difference,ChronoUnit.MONTHS);
			}
			
			int index = -1;
			ChenJiaoZhanBiInGivenPeriod foundrecord = null;
			for(ChenJiaoZhanBiInGivenPeriod tmpcjzb : super.periodlist) {
				if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.WEEK)) { //������������ݣ�ֻҪ��������ͬ�����ɷ���
					index ++;
					
					int year = expectedate.getYear();
					int month = expectedate.getMonthValue();
					
					int yearnum = tmpcjzb.getRecordsYear();
					int monthnum = tmpcjzb.getRecordsMonth();
					
					if(month == monthnum && yearnum == year) {
						foundrecord = tmpcjzb;
						break;
					}
				} else	if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.WEEK)) { //������������ݣ�ֻҪ��������ͬ�����ɷ���
					index ++;
					
					int year = expectedate.getYear();
					WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
					int weekNumber = expectedate.get(weekFields.weekOfWeekBasedYear());
					
					int yearnum = tmpcjzb.getRecordsYear();
					int wknum = tmpcjzb.getRecordsWeek();
					
					if(wknum == weekNumber && yearnum == year) {
						foundrecord = tmpcjzb;
						break;
					}
				} else if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.DAY)) { //������������ݣ�������ͬ�ſɷ���
					index ++;
					LocalDate recordsday = tmpcjzb.getRecordsDayofEndofWeek();
					if(recordsday.isEqual(requireddate)) {
						foundrecord = tmpcjzb;
						break;
					}
				}
			}
			
			return foundrecord;
		}
		@Override
		public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate) 
		{
			if(periodlist == null)
				return null;
			
			ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
			if( curcjlrecord == null) 
				return null;
			
			int index = -1;
			while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //���ܿ��ܴ�������
				index --;
			}
			
			ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
			if(lastcjlrecord == null) { //����ǰ���ǿգ�˵��Ҫ���°�顣���û��ͣ�Ƶ�
				logger.debug(getMyOwnCode() + getMyOwnName() + "������һ���¸���");
				return 100.0;
			}
			
			Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
			Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
			double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
			logger.debug(getMyOwnCode() + getMyOwnName() + "ռ������" + requireddate.toString() + zhanbigrowthrate);
			return zhanbigrowthrate;
		}

		@Override
		public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate) 
		{
			if(periodlist == null)
				return null;
			
			ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
			if( curcjlrecord == null) 
				return null;
			
			int index = -1;
			while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //���ܿ��ܴ�������
				index --;
			}
			
			ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
			if(lastcjlrecord == null) { //����ǰ���ǿգ�˵��Ҫ���°�顣���û��ͣ�Ƶ�
				logger.debug(getMyOwnCode() + getMyOwnName() + "������һ���¸��ɻ���");
				return 100;
			}
			
			Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
			int maxweek = 0;
			for(int i = index;i>=0;i--) {
				lastcjlrecord = this.getSpecficRecord (requireddate,index);
				Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
				if(curzhanbiratio > lastzhanbiratio)
					maxweek ++;
				else
					break;
			}
			
			return maxweek;
		}

		@Override
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErChangeGrowthRateOfSuperBanKuai(java.time.LocalDate)
		 * �������ú�����ͬ�ĵط���������ȥ���ĳɽ��������Ǵ��̵ĳɽ���
		 */
		public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(LocalDate requireddate) 
		{
			if(periodlist == null)
				return null;
			
			//�ж��ϼ����(���̻��߰��)�Ƿ�����,������û�бȽϵ����壬ֱ�ӷ���-100��
			String nodept = super.getNodeperiodtype();
			Double bkcjediff = bankuai.getNodeXPeroidData(nodept).getChengJiaoErDifferenceWithLastPeriod(requireddate);
			if( bkcjediff == null || bkcjediff<0  ) //�ϼ����������
				return -100.0;
			
			ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
			if( curcjlrecord == null) 
				return null;
			
			int index = -1;
			while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //���ܿ��ܴ�������
				index --;
			}
			
			ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
			if(lastcjlrecord == null) { //����ǰ���ǿգ�˵��Ҫ���°�顣���û��ͣ�Ƶ�
				Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //�°�����гɽ�����Ӧ�ü�����
				return curggcje/bkcjediff;
			}
			
			Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
			Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
			Double cjechange = curcje - lastcje; //���ɳɽ����ı仯
			
			return cjechange/bkcjediff;
		}
		
		/*
		 * �жϱ�����ͣ�ƺ���
		 */
//		protected Boolean isFuPaiAfterTingPai (LocalDate requireddate )
//		{
//			Integer index = super.getRequiredRecordsPostion (requireddate);
//			if( index != null) {
//				if(index == 0) { //���˼�¼�ĵ�һ������ô��
//					return true;
//				}
//				
//				ChenJiaoZhanBiInGivenPeriod curcjlrecord = periodlist.get(index);
//				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = null;
//				try {
//					 lastcjlrecord = periodlist.get(index -1 );
//				} catch (java.lang.ArrayIndexOutOfBoundsException e) { 
//					return true;
//				}
//				
//				LocalDate curweeknum = curcjlrecord.getRecordsDayofEndofWeek();
//				LocalDate lastweeknum = lastcjlrecord.getRecordsDayofEndofWeek();
//				
//				long diffwknum = Math.abs(ChronoUnit.WEEKS.between (curweeknum,lastweeknum));
//				
//				if(diffwknum == 1) //��һ�ܣ���������
//					return false;
//				
//				int alldapanxiushi = 0;
//				for(int i=1;i<diffwknum;i++) {
//					LocalDate dapanlastweekdate = curweeknum.with(DayOfWeek.MONDAY).minus(i,ChronoUnit.WEEKS);
//					if( ((DaPan)getRoot()).isDaPanXiuShi(dapanlastweekdate,super.getNodeperiodtype() ) )
//						alldapanxiushi ++;
//				}
//				if(alldapanxiushi == diffwknum-1)
//					return false;
//				else 
//					return true;
//			}
//			
//			return null;
//		}
//		/*
//		 * ��������ܵĳɽ���ռ������
//		 */
//		public Double getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (LocalDate requireddate)
//		{
//			if(periodlist == null)
//				return null;
//			
//			Integer index = super.getRequiredRecordsPostion (requireddate);
//			if( index != null) {
//					if( getChengJiaoErDifferenceOfPeriod(requireddate) == null && super.periodlist.get(index) != null ) {
//							logger.debug(getMyOwnCode() + getMyOwnName() + requireddate.toString() + "������һ���¸��ɻ���");
//							return 100.0;
//					}
//					
//
////					LocalDate curweeknum = curcjlrecord.getRecordsDayofEndofWeek();
////					LocalDate lastweeknum = lastcjlrecord.getRecordsDayofEndofWeek();
//					
////					long diffwknum = Math.abs(ChronoUnit.WEEKS.between (curweeknum,lastweeknum));
////					if(diffwknum >1) {
////						if(this.checkIsFuPaiAfterTingPai (requireddate)) { //˵����ͣ�Ƶĸ���
////							return 100.0;
////						} 
////					}
//					
//					if(this.isFuPaiAfterTingPai (requireddate)) { //˵����ͣ�Ƶĸ���
//						logger.debug(getMyOwnCode() + getMyOwnName() + requireddate.toString() + "Ϊͣ���ƹɣ�ռ������Ϊ100.0");
//						return 100.0;
//					}
//					
//					ChenJiaoZhanBiInGivenPeriod curcjlrecord = periodlist.get(index);
//					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(index -1 );				
//					Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
//					Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
//					Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
//					logger.debug(getMyOwnCode() + getMyOwnName() + requireddate.toString() + "ռ������Ϊ" + zhanbigrowthrate.toString());
//					return zhanbigrowthrate;
//			}
//			
//			logger.debug(getMyOwnCode() + getMyOwnName() + requireddate.toString() + "ռ������Ϊnull���������ˣ�����");
//			return null;
//		}
//		/*
//		 * ��������ܵĳɽ���Ͱ��ռ���Ƕ��������ڵ����ռ��
//		 */
//		public Integer getChenJiaoLiangZhanBiMaxWeekForAGivenPeriod (LocalDate requireddate)
//		{
//			if(periodlist == null)
//				return null;
//
//			int maxweek = 0;
//			Integer index = super.getRequiredRecordsPostion (requireddate);
//			if( index != null) {
//				if( getChengJiaoErDifferenceOfPeriod(requireddate) == null && super.periodlist.get(index) != null ) {
//					logger.debug(getMyOwnCode() + getMyOwnName() + "������һ���¸��ɻ���");
//					return 0;
//				}
//				
//				if(this.isFuPaiAfterTingPai (requireddate)) { //˵����ͣ�Ƶĸ���
//					return 0;
//				} else {
//					ChenJiaoZhanBiInGivenPeriod curcjlrecord = periodlist.get(index);
//
//					Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
//					for(int i = index -1;i>=0;i--) {
//						ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(i );
//						Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
//						LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
//						
//						if(curzhanbiratio > lastzhanbiratio)
//								maxweek ++;
//						else
//							break;
//						
//						if( this.isFuPaiAfterTingPai (lastdate) ) //
//							break;
//					}
//				}
//			}
//			
//			return maxweek;
//		}
//		/*
//		 * ����������ڵĳɽ���ʹ��̵�ռ���Ƕ��������ڵ����ռ��
//		 */
//		public Integer getChenJiaoLiangDaPanZhanBiMaxWeekForAGivenPeriod (LocalDate requireddate)
//		{
//			if(periodlist == null)
//				return null;
//
//			int maxweek = 0;
//			Integer index = super.getRequiredRecordsPostion (requireddate);
//			if( index != null) {
//				if( getChengJiaoErDifferenceOfPeriod(requireddate) == null && super.periodlist.get(index) != null ) {
////					logger.debug(this.getMyOwnCode() + this.getMyOwnName() + "������һ���¸��ɻ���");
//					return 0;
//				}
//				
//				if(this.isFuPaiAfterTingPai (requireddate)) { //˵����ͣ�Ƶĸ���
//					return 0;
//				} else {
//					ChenJiaoZhanBiInGivenPeriod curcjlrecord = periodlist.get(index);
//					NodeXPeriodData myupbankuaidata = ((DaPan)getRoot()).getNodeXPeroidData(super.getNodeperiodtype());
//					ChenJiaoZhanBiInGivenPeriod bkcjlrecord = myupbankuaidata.getSpecficRecord(requireddate,0);
//					
//					Double curzhanbiratio = curcjlrecord.getMyOwnChengJiaoEr() /  bkcjlrecord.getUpLevelChengJiaoEr(); //���ɳɽ����ʹ��̳ɽ���
//					for(int i = index -1;i>=0;i--) {
//						ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(i);
//						LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
//						ChenJiaoZhanBiInGivenPeriod lastbkcjlrecord = myupbankuaidata.getSpecficRecord(lastdate,0);
//						
//						Double lastratio = lastcjlrecord.getMyOwnChengJiaoEr() / lastbkcjlrecord.getUpLevelChengJiaoEr();
//						if(curzhanbiratio > lastratio)
//							maxweek ++;
//						else
//							break;
//						
//						if(this.isFuPaiAfterTingPai (lastdate) )
//							break;
//					}
//				}
//			}
//			
//			return maxweek;
//			
//		}
//		/*
//		 * ����ɽ���仯�����ʣ���������ռ��������İٷֱ�
//		 */
//		public Double getChenJiaoErChangeGrowthRateForAGivenPeriod (LocalDate requireddate)
//		{
//			if(periodlist == null)
//				return null;
//			
//			//�����ĳɽ��������ӣ�����û�бȽ�������ֱ�ӷ���
//			NodeXPeriodData myupbankuaidata = myupbankuai.getNodeXPeroidData(super.getNodeperiodtype());
//			Double bkcjediff = myupbankuaidata.getChengJiaoErDifferenceOfPeriod (requireddate); 
//			if(   bkcjediff == null || bkcjediff <0)
//				return -100.0;
//			
//			Integer index = super.getRequiredRecordsPostion (requireddate);
//			if( index != null) {
//					ChenJiaoZhanBiInGivenPeriod curcjlrecord = super.periodlist.get(index); //�Լ���ǰ�ܳɽ���
//					
//					if( getChengJiaoErDifferenceOfPeriod(requireddate) == null && super.periodlist.get(index) != null ) {
//						logger.debug(getMyOwnCode() + getMyOwnName() + "�����Ǹ��°��" );
//						Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //ͣ�ƺ����гɽ�����Ӧ�ü�����
//						return curggcje/bkcjediff;
//					}
//					
//					if(this.isFuPaiAfterTingPai (requireddate)) { //˵����ͣ�Ƶĸ���  
//						Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();//ͣ�ƺ����гɽ�����Ӧ�ü�����
//
//						return curggcje/bkcjediff;
//
//					} else { //����û��ͣ��
//						Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();
//						ChenJiaoZhanBiInGivenPeriod lastcjlrecord = super.periodlist.get(index-1); //�Լ���ǰ�ܳɽ���
//						Double lastggcje = lastcjlrecord.getMyOwnChengJiaoEr();
//						
//						Double gegucjechange = curggcje - lastggcje; //���ɳɽ����ı仯
//						
//						return gegucjechange/bkcjediff;
//					}
//			}
//			
//			return  -100.0;
//		}
		/*
		 * ������ɺʹ���/���ɽ����ĸ�������
		 */
		public ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate (LocalDate requireddate)
		{
//			if(periodlist == null)
//				return null;
//			
////			NodeFengXiData nodefx = new NodeFengXiData (super.getMyOwnCode(),requireddate);
//			
//			//����ɽ���仯�����ʣ���������ռ��������İٷֱ�
//			//�����ĳɽ��������ӣ�����û�бȽ�������ֱ�ӷ���
//			NodeXPeriodData myupbankuaidata = myupbankuai.getNodeXPeroidData(super.getNodeperiodtype());
//			Double bkcjediff = myupbankuaidata.getChengJiaoErDifferenceOfPeriod (requireddate); 
//			
//			Integer index = super.getRequiredRecordsPostion (requireddate);
//			if( index != null) {
//				ChenJiaoZhanBiInGivenPeriod curcjlrecord = super.periodlist.get(index); //�Լ���ǰ�ܳɽ���
//				
//				if( getChengJiaoErDifferenceOfPeriod(requireddate) == null && super.periodlist.get(index) != null ) {
////					logger.debug(this.getMyOwnCode() + this.getMyOwnName() + "������һ���¸��ɻ���");
//					//����ɽ���仯�����ʣ���������ռ��������İٷֱ�
//					if( bkcjediff == null || bkcjediff <0)
//						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
//					else {
//						Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //ͣ�ƺ����гɽ�����Ӧ�ü�����
//						curcjlrecord.setGgbkcjegrowthzhanbi( curggcje/bkcjediff );
//					}
//					//��������ܵĳɽ���Ͱ���ռ���Ƕ��������ڵ����ռ��
//					curcjlrecord.setGgbkzhanbimaxweek(0);
//					// ��������ܵĳɽ���Ͱ��ռ������
//					curcjlrecord.setGgbkzhanbigrowthrate(100.0);
//					//���������ĳɽ���ʹ��̵�ռ���Ƕ��������ڵ����ռ��
//					curcjlrecord.setGgdpzhanbimaxweek(0);
//					curcjlrecord.setGgdpzhanbigrowthrate (100.0); //�ʹ���ռ�ȵ�����
//					//�ɽ����Ƕ��������
//					curcjlrecord.setGgbkcjemaxweek(0);
//					
//					return curcjlrecord;
//				} else if(this.isFuPaiAfterTingPai (requireddate)) { //˵����ͣ�Ƶĸ���
//					//����ɽ���仯�����ʣ���������ռ��������İٷֱ�
//					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();//ͣ�ƺ����гɽ�����Ӧ�ü�����
//					if( bkcjediff <0 || bkcjediff == null)
//						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
//					else
//						curcjlrecord.setGgbkcjegrowthzhanbi(curggcje/bkcjediff);
//					//��������ܵĳɽ���Ͱ���ռ���Ƕ��������ڵ����ռ��
//					curcjlrecord.setGgbkzhanbimaxweek(0);
//					// ��������ܵĳɽ���Ͱ��ռ������
//					curcjlrecord.setGgbkzhanbigrowthrate(100.0);
//					//���������ĳɽ���ʹ��̵�ռ���Ƕ��������ڵ����ռ��
//					curcjlrecord.setGgdpzhanbimaxweek(0);
//					curcjlrecord.setGgdpzhanbigrowthrate (100.0); //�ʹ���ռ�ȵ�����
//					//�ɽ����Ƕ��������
//					curcjlrecord.setGgbkcjemaxweek(0);
//
//					
//					return curcjlrecord;
//				} else {
//					//����ɽ���仯�����ʣ���������ռ��������İٷֱ�
////					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();
////					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = super.wkcjeperiodlist.get(index-1); //�Լ����ܳɽ���
////					Double lastggcje = lastcjlrecord.getMyOwnChengJiaoEr();
////					Double gegucjechange = curggcje - lastggcje; //���ɳɽ����ı仯
//					
//					Double gegucjechange = getChengJiaoErDifferenceOfPeriod(requireddate);
//					if( bkcjediff <0 || bkcjediff == null)
//						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
//					else
//						curcjlrecord.setGgbkcjegrowthzhanbi( gegucjechange/bkcjediff );
//					
//					//��������ܵĳɽ���Ͱ���ռ���Ƕ��������ڵ����ռ��
//					Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
//					Integer maxweek = 0;
//					for(int i = index -1;i>=0;i--) {
//						ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(i );
//						Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
//						LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
//
//						if(curzhanbiratio > lastzhanbiratio)
//								maxweek ++;
//						else
//							break;
//						
//						if( this.isFuPaiAfterTingPai (lastdate) ) //
//							break;
//					}
//					curcjlrecord.setGgbkzhanbimaxweek(maxweek);
//					
//					//�ɽ����Ƕ��������
//					Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
//					maxweek = 0;
//					for(int i = index -1;i>=0;i--) {
//						ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(i );
//						Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
//						LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
//
//						if(curcje > lastcje)
//								maxweek ++;
//						else
//							break;
//							
//						if( this.isFuPaiAfterTingPai (lastdate) ) //
//								break;
//					}
//					curcjlrecord.setGgbkcjemaxweek(maxweek);
//
//					// ��������ܵĳɽ���Ͱ��ռ������
//					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(index -1 );				
//					Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
//					Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
//
//					curcjlrecord.setGgbkzhanbigrowthrate( zhanbigrowthrate);
//					
//					//���������ĳɽ���ʹ��̵�ռ���Ƕ��������ڵ����ռ��
//					ChenJiaoZhanBiInGivenPeriod bkcjlrecord = myupbankuaidata.getSpecficRecord(requireddate,0);//ͨ������������õ����̳ɽ���
//					Double curdpzhanbiratio = curcjlrecord.getMyOwnChengJiaoEr() /  bkcjlrecord.getUpLevelChengJiaoEr(); //���ɳɽ����ʹ��̳ɽ�������ռ��
//					Integer dpmaxweek = 0;
//					for(int i = index -1;i>=0;i--) {
//						lastcjlrecord = periodlist.get(i);
//						LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
//						
//						ChenJiaoZhanBiInGivenPeriod lastbkcjlrecord = myupbankuaidata.getSpecficRecord(lastdate,0);
//						Double lastratio = lastcjlrecord.getMyOwnChengJiaoEr() / lastbkcjlrecord.getUpLevelChengJiaoEr();
//						
//						if(i == (index -1) ) { //������˳�����һ�ºʹ���ռ�ȵ�����
//							Double dpzhanbigrowthrate = (curdpzhanbiratio - lastratio)/lastratio;
//							curcjlrecord.setGgdpzhanbigrowthrate(dpzhanbigrowthrate);
//						}
//						
//						if(curdpzhanbiratio > lastratio)
//							dpmaxweek ++;
//						else
//							break;
//						
//						if(this.isFuPaiAfterTingPai (lastdate) ) //�鵽�����ܵ�ͷΪֹ
//							break;
//					}
//					curcjlrecord.setGgdpzhanbimaxweek(dpmaxweek);
//					
//					//���������ĳɽ���ʹ��̵�ռ��
//					curcjlrecord.setGgdpzhanbi(curdpzhanbiratio);
//					
//					return curcjlrecord;
//					
//				}
//			}
//			
			return null;
		}



		
	}
}