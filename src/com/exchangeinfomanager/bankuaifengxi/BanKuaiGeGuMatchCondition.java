package com.exchangeinfomanager.bankuaifengxi;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.google.common.base.Splitter;

public class BanKuaiGeGuMatchCondition 
{
	private Set<BanKuaiGeGuMatchConditionListener> bkfxhighlightvaluesoftableslisteners;
	private String systeminstalledpath;
	private Properties prop;


	public BanKuaiGeGuMatchCondition() 
	{
		this.bkfxhighlightvaluesoftableslisteners = new HashSet<>();
	}
	
	 public void addCacheListener(BanKuaiGeGuMatchConditionListener listener) 
	 {
	        this.bkfxhighlightvaluesoftableslisteners.add(listener);
	 }
	 
	 public void setConditionFromPropertiesFiles ()
	 {
			File directory = new File("");//�趨Ϊ��ǰ�ļ���
			try{
//			    logger.debug(directory.getCanonicalPath());//��ȡ��׼��·��
//			    logger.debug(directory.getAbsolutePath());//��ȡ����·��
//			    this.systeminstalledpath = toUNIXpath(directory.getCanonicalPath()+ "\\");
			    Properties properties = System.getProperties();
			    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //�û����г���ĵ�ǰĿ¼
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			try {
				prop = new Properties();
				String propFileName = this.systeminstalledpath  + "/config/bankuaifenxihighlightsetting.properties";
				FileInputStream inputStream = new FileInputStream(propFileName);
				if (inputStream != null) {
					prop.load(inputStream);
				} 
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
			
			String majunxian = prop.getProperty ("Dayujunxian");
			this.setSettingMaFormula (majunxian.trim() );
			
			String cjeZbDpMaxWk = prop.getProperty ("CjeZbDpMaxWk");
			if(cjeZbDpMaxWk != null)
				this.setSettingDpMaxWk(Integer.parseInt(cjeZbDpMaxWk) ); 
			
			String cjeZbDpMinWk = prop.getProperty ("CjeZbDpMinWk");
			if(cjeZbDpMinWk != null)
				this.setSettingDpMinWk(Integer.parseInt(cjeZbDpMinWk));
			
			String liuTongShiZhiMin = prop.getProperty ("LiuTongShiZhiMin");
			String liuTongShiZhiMax = prop.getProperty ("LiuTongShiZhiMax");
			String averageCjeMaxWk = prop.getProperty ("AverageCjeMaxWk");
			String huanShouLv = prop.getProperty ("HuanShouLv");
			String zhangFuMin = prop.getProperty ("ZhangFuMin");
			String zhangFuMax = prop.getProperty ("ZhangFuMax");
			String guJiaMin = prop.getProperty ("GuJiaMin");
			String guJiaMax = prop.getProperty ("GuJiaMax");
			String chenJiaoErMin = prop.getProperty ("ChenJiaoErMin");
			String chenJiaoErMax = prop.getProperty ("ChenJiaoErMax");
			String lastWkDpcjezbGrowingRate = prop.getProperty ("LastWkDpcjezbGrowingRate");
			
			 
		}
		 private  String toUNIXpath(String filePath) 
		 {
		   		    return filePath.replace('\\', '/');
		 }
	//������Щ��������2���ط�ʹ�ã�����ͻ����ʾ�͵�������׼��һ��ֱ�ӷ���ԭʼ���ݣ��ɿͻ��Լ�����
	 
		private Double settingltszmax; //��ͨ��ֵ
		private Double settingltszmin; //��ͨ��ֵ
		
		private Double settingcjemax; //�ɽ���
		private Double settingcjemin; 
		
		private Integer settingdpmaxwk; //dpmax
		private Integer settingdpminwk;
		
		private Integer settingcjemaxwk; // �ɽ���maxwk
		
		private Double settinghsl; //
		
		private String settingbk;
		
		private String tooltips = "";

		private boolean huibudownquekou;
		private boolean zhangting; //
		private boolean dieting; //
		private String settingMaFormula;
		
		boolean exportAllBanKuai;
		boolean exportBankuaiInConfig;
		boolean exportOnlyCurrentBanKuai;
		boolean exportCjeZhanbiUp;
		boolean exportYangXianBanKuai;
		boolean exportOnlyBankuaiNotGeGu;
		boolean exportOnlyGeGuOntBanKuai;
		boolean exportOnlyCurrentGeGu;
		
		Double cjebottomforyangxianlevle;
		Double cjeyangxianlevel;
		Double cjebottomforfanglianglevle;
		Integer cjefanglianglevel;
		Boolean exportST;
		private Double zhangfumax;
		private Double zhangfumin;
		private boolean onlyexportyellowsignbkstk;
		
		private Double pricemin;
		private Double pricemax;
		
		/*
		 * 
		 */
		public void setSettingStockPriceLevel (Double min, Double max)
		{
			this.pricemin = min;
			this.pricemax = max;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiGeGuMatchConditionValuesChanges( this ) );
		}
		public Double getSettingSotckPriceMin ()
		{
			return this.pricemin;
		}
		public Double getSettingSotckPriceMax ()
		{
			return this.pricemax;
		}
		
		public String getConditionsDescriptions ()
		{
			String outputfilehead = "[��������:";
//			if(this.shouldExportAllBanKuai())
//				outputfilehead = outputfilehead + "�������а�顣";
//			if(this.shouldOnlyExportCurrentBankuai())
//				outputfilehead = outputfilehead + "�����޶����Ϊ" + this.shouldOnlyExportCurrentBankuai() + "��";
//			if(this.shouldOnlyExportBanKuaiAllowedInSetting())
//				outputfilehead = outputfilehead + "�����������������İ�顣" ; 
//			if(this.shouldOnlyExportBanKuaiOfZhanBiUp())
//				outputfilehead = outputfilehead +"�����������̳ɽ����ɽ���ռ�Ȼ��������İ�顣";
//			outputfilehead = outputfilehead + "���������趨Ϊ:";
//			outputfilehead = outputfilehead + "�ɽ������" + this.getSettingCjemin() + "�ڣ�";
//			outputfilehead = outputfilehead + "�ɽ���С��" + this.getSettingCjeMax() + "�ڡ�";
//			outputfilehead = outputfilehead + "�ɽ����ɽ���DPMAX����" + this.getSettinDpmaxwk() + "��";
//			outputfilehead = outputfilehead + "�ɽ������" + this.getSettingCjemaxwk() + "�ܡ�";
//			outputfilehead = outputfilehead + "������ST" + this.shouldNotExportSTStocks() + "��";
//			outputfilehead = outputfilehead + "����������K���Ƿ�������" + this.shouldOnlyExportStocksWithWkYangXian () + "��";
//			outputfilehead = outputfilehead + "���������̼�������߹�ʽΪ" + this.getSettingMAFormula() + "��";
//			outputfilehead = outputfilehead + "�����ʱ���>=" + this.getSettingHsl() + "��";
//			outputfilehead = outputfilehead  +"]";
			
			return outputfilehead;
		}
		
		public Double getSettingLiuTongShiZhiMax() 
		{
			return settingltszmax;
		}
		public void setSettingLiuTongShiZhiMax(Double seetingltszmax) {
			this.settingltszmax = seetingltszmax;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiGeGuMatchConditionValuesChanges( this ) );
		}
		public Double getSettingLiuTongShiZhiMin() {
			return settingltszmin;
		}
		public void setSettingLiuTongShiZhiMin(Double seetingltszmin) {
			this.settingltszmin = seetingltszmin;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiGeGuMatchConditionValuesChanges( this ) );
		}
		public Double getSettingChenJiaoErMax() {
			return settingcjemax;
		}
		public void setSettingChenJiaoErMax(Double settingcjemax) {
			this.settingcjemax = settingcjemax;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiGeGuMatchConditionValuesChanges( this ) );
		}
		public Double getSettingChenJiaoErMin() {
			return settingcjemin;
		}
		public void setSettingChenJiaoErMin(Double settingcjemin) {
			this.settingcjemin = settingcjemin;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiGeGuMatchConditionValuesChanges( this ) );
		}
		public Integer getSettingDpMaxWk() {
			return settingdpmaxwk;
		}
		public void setSettingDpMaxWk(Integer settindpgmaxwk) {
			this.settingdpmaxwk = settindpgmaxwk;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiGeGuMatchConditionValuesChanges( this ) );
		}
		public Integer getSettingDpMinWk() {
			return settingdpminwk;
		}
		public void setSettingDpMinWk(Integer settindpgminwk) {
			this.settingdpminwk = settindpgminwk;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiGeGuMatchConditionValuesChanges( this ) );
		}
		public Integer getSettingChenJiaoErMaxWk() {
			return settingcjemaxwk;
		}
		public void setSettingChenJiaoErMaxWk(Integer settingcjemaxwk) {
			this.settingcjemaxwk = settingcjemaxwk;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiGeGuMatchConditionValuesChanges( this ) );
		}
		public Double getSettingHuanShouLv() {
			return settinghsl;
		}
		public void setSettingHuanShouLv(Double settinghsl) {
			this.settinghsl = settinghsl;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiGeGuMatchConditionValuesChanges( this ) );
		}
		public String getSettingBanKuai() {
			return settingbk;
		}
		public void setSettingBanKuai(String settingbk) {
			this.settingbk = settingbk;
		}
		public String getTooltips() {
			return tooltips;
		}
		public void setTooltips(String tooltips) {
			this.tooltips = tooltips;
		}
		public boolean hasHuiBuDownQueKou() {
			return huibudownquekou;
		}
		public void setHuiBuDownQueKou(boolean huibudownquekou) {
			this.huibudownquekou = huibudownquekou;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiGeGuMatchConditionValuesChanges( this ) );
		}
		public boolean hasZhangTing() {
			return zhangting;
		}
		public void setZhangTing(boolean zhangting) {
			this.zhangting = zhangting;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiGeGuMatchConditionValuesChanges( this ) );
		}
		public boolean hasDieTing() {
			return dieting;
		}
		public void setDieTing(boolean dieting) {
			this.dieting = dieting;
		}
		public String getSettingMaFormula() {
			return settingMaFormula;
		}
		public void setSettingMaFormula (String maformula) {
			if(maformula != null ) {
				maformula = maformula.replace(">", "x>");
				maformula = maformula.replace("<", "x<");
				
				if(maformula.contains(">250") || maformula.contains(">=250") || maformula.contains("<250") )
					maformula = maformula.replace("250", "\'250\'"  ) ;
			    if(maformula.contains(">120") || maformula.contains(">=120") || maformula.contains("<120") )
			    	maformula = maformula.replace("120", "\'120\'" ) ;
			    if(maformula.contains(">60") || maformula.contains(">=60") || maformula.contains("<60") ) 
			    	maformula = maformula.replace("60", "\'60\'"   ) ;
			    if(maformula.contains(">30") || maformula.contains(">=30") || maformula.contains("<30") ) 
			    	maformula = maformula.replace("30", "\'30\'"  ) ;
			    if(maformula.contains(">20") || maformula.contains(">=20") || maformula.contains("<20") ) 
			    	maformula = maformula.replace("20", "\'20\'"  ) ;
			    if(maformula.contains(">10") || maformula.contains(">=10") || maformula.contains("<10") ) 
			    	maformula = maformula.replace("10", "\'10\'"  ) ;
			    if(maformula.contains(">5") || maformula.contains(">=5") || maformula.contains("<5") ) 
			    	maformula = maformula.replace("5","\'5\'"   ) ;
			}
			this.settingMaFormula =  maformula;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiGeGuMatchConditionValuesChanges( this ) );
			
			return;
		}
		
		public boolean shouldExportAllBanKuai () {
			return exportAllBanKuai;
		}
		public void setExportAllBanKuai (boolean exportAllBanKuai) {
			this.exportAllBanKuai = exportAllBanKuai;
		}
		
		public boolean shouldExportBankuaiInConfig () {
			return exportBankuaiInConfig;
		}
		public void setExportBankuaiInConfig (boolean exportBankuaiInConfig) {
			this.exportBankuaiInConfig = exportBankuaiInConfig;
		}
		
		public boolean shouldExportOnlyCurrentBanKuai() {
			return exportOnlyCurrentBanKuai;
		}
		public void setExportOnlyCurrentBanKuai (boolean exportOnlyCurrentBanKuai) {
			this.exportOnlyCurrentBanKuai = exportOnlyCurrentBanKuai;
		}
		
		public boolean shouldExportChenJiaoErZhanbiUpBanKuai () {
			return exportCjeZhanbiUp;
		}
		public void setExportChenJiaoErZhanbiUpBanKuai (boolean exportCjeZhanbiUp) {
			this.exportCjeZhanbiUp = exportCjeZhanbiUp;
		}
		
		public boolean shouldExportYangXianBanKuai () {
			return exportYangXianBanKuai;
		}
		public void setExportYangXianBanKuai(boolean exportYangXianBanKuai) {
			this.exportYangXianBanKuai = exportYangXianBanKuai;
		}
		
		public boolean shouldExportOnlyBankuaiNotGeGu() {
			return exportOnlyBankuaiNotGeGu;
		}
		public void setExportOnlyBankuaiNotGeGu(boolean exportOnlyBankuaiNotGeGu) {
			this.exportOnlyBankuaiNotGeGu = exportOnlyBankuaiNotGeGu;
		}
		
		public boolean shouldExportOnlyGeGuNotBanKuai() {
			return exportOnlyGeGuOntBanKuai;
		}
		public void setExportOnlyGeGuNotBanKuai(boolean exportOnlyGeGuOntBanKuai) {
			this.exportOnlyGeGuOntBanKuai = exportOnlyGeGuOntBanKuai;
		}
		
		public boolean shouldExportOnlyCurrentGeGu() {
			return exportOnlyCurrentGeGu;
		}
		public void setExportOnlyCurrentGeGu(boolean exportOnlyCurrentGeGu) {
			this.exportOnlyCurrentGeGu = exportOnlyCurrentGeGu;
		}
		
		Boolean exportGeGuZhangfuQuJian;
		public Boolean shouldExporGeGuWithZhangFuQuJian() {
			return exportGeGuZhangfuQuJian;
		}
		public void setExporGeGuWithZhangFuQuJian(Boolean exportgeguqujian) {
			this.exportGeGuZhangfuQuJian = exportgeguqujian;
		}
		
		private Double exportYinXianGeGumin;private Double exportYinXianGeGumax;
		public Double getExportGeGuZhangfuQuJianMin() {
			return exportYinXianGeGumin;
		}
		public Double getExportGeGuZhangfuQuJianMax() {
			return exportYinXianGeGumax;
		}
		public void setExportGeGuZhangFuQuJian(Double min, Double max) {
			this.exportYinXianGeGumin = min;
			this.exportYinXianGeGumax = max;
		}
		
		//�ɽ������
		public Double getChenJiaoErBottomForYangXianLevle() {
			return cjebottomforyangxianlevle;
		}
		public void setChenJiaoErBottomForYangXianLevle(Double cjebottomforyangxianlevle) {
			this.cjebottomforyangxianlevle = cjebottomforyangxianlevle;
		}
		//�ɽ������ˮƽ����������
		public Double getChenJiaoErBottomYangXianLevel() {
			return cjeyangxianlevel;
		}
		public void setChenJiaoErBottomYangXianLevel(Double cjeyangxianlevel) {
			this.cjeyangxianlevel = cjeyangxianlevel;
		}
		//�ɽ������
		public Double getChenJiaoErBottomForFangLiangLevle() {
			return cjebottomforfanglianglevle;
		}
		public void setChenJiaoErBottomForFangLiangLevle(Double cjebottomforfanglianglevle) {
			this.cjebottomforfanglianglevle = cjebottomforfanglianglevle;
		}
		//�ɽ������ˮƽ�����з���
		public Integer getChenJiaoErFangLiangLevel() {
			return cjefanglianglevel;
		}
		public void setChenJiaoErFangLiangLevel(Integer cjefanglianglevel) {
			this.cjefanglianglevel = cjefanglianglevel;
		}
		
		public Boolean shouldExportST() {
			return exportST;
		}
		public void setExportST(Boolean exportST) {
			this.exportST = exportST;
		}

		public void setSettingZhangFuMax(Double zhangfumax1) 
		{
			if (zhangfumax1 != null)
				this.zhangfumax = zhangfumax1/100;
			else
				this.zhangfumax = zhangfumax1;
		}
		public Double getSettingZhangFuMax() 
		{
			return this.zhangfumax ;
		}

		public void setSettingZhangFuMin(Double zhangfumin1) 
		{
			if(zhangfumin1 != null)
				this.zhangfumin = zhangfumin1/100;
			else
				this.zhangfumin = zhangfumin1;
		}
		public Double getSettingZhangFuMin() 
		{
			return this.zhangfumin;
		}

		public void setExportOnlyYellowSignBkStk(boolean export) 
		{
			this.onlyexportyellowsignbkstk = export;
			
		}
		public Boolean shouldExportOnlyYellowSignBkStk() 
		{
			return this.onlyexportyellowsignbkstk ;
			
		} 
		private Double setttinglastwkcjezbgrowingratemin;
		private Double setttinglastwkcjezbgrowingratemax;
		public void setCjezbGrowingRate(Double min, Double max)
		{
			setttinglastwkcjezbgrowingratemin = min;
			setttinglastwkcjezbgrowingratemax = max;
		}
		public Double getCjezbGrowingRateMin()
		{
			return setttinglastwkcjezbgrowingratemin;
		}
		public Double getCjezbGrowingRateMax()
		{
			return setttinglastwkcjezbgrowingratemax;
		}
		private Integer settinglastwkcjezbmaxkwk;
		public void setLastWkCjezbmaxkwk (Integer maxwk) 
		{
			this.settinglastwkcjezbmaxkwk = maxwk;
		}
		public Integer getLastWkCjezbmaxkwk () 
		{
			return this.settinglastwkcjezbmaxkwk ;
		}
		private Integer settinglastwkcjemaxkwk;
		public void setLastWkCjemaxkwk (Integer maxwk) 
		{
			this.settinglastwkcjemaxkwk = maxwk;
		}
		public Integer getLastWkCjemaxkwk () 
		{
			return this.settinglastwkcjezbmaxkwk ;
		}
//		public Boolean shouldExportAverageWkCjeMaxWk() {
//			return exportAverageWkCjeMaxWk;
//		}
//
//		public void setExportAverageWkCjeMaxWk(Boolean exportAverageWkCjeMaxWk) {
//			this.exportAverageWkCjeMaxWk = exportAverageWkCjeMaxWk;
//		}
//
//		public Integer getAverageWkCjeMaxWkLevel() 
//		{
//			return averageWkCjeMaxWkLevel;
//		}
//
//		public void setAverageWkCjeMaxWkLevel(Integer averageWkCjeMaxWkLevel) 
//		{
//			this.averageWkCjeMaxWkLevel = averageWkCjeMaxWkLevel;
//		}

}
