package com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BanKuaiAndGeGuMatchingConditions implements Cloneable {

	private Set<BanKuaiAndGeGuMatchingConditionListener> bkfxhighlightvaluesoftableslisteners;

	public BanKuaiAndGeGuMatchingConditions() 
	{
		this.bkfxhighlightvaluesoftableslisteners = new HashSet<>();
	}
	 public void addCacheListener(BanKuaiAndGeGuMatchingConditionListener listener) 
	 {
	        this.bkfxhighlightvaluesoftableslisteners.add(listener);
	 }

	 private String exportcondformula;
		public void setExportConditionFormula (String formula)
		{
			this.exportcondformula = formula;
		}
		public String getExportConditionFormula ()
		{
			return this.exportcondformula ;
		}
		private List<String> predefinedexportcondformula;
		public void setPredefinedExportConditionFormula (List<String> formula)
		{
			this.predefinedexportcondformula = formula;
		}
		public List<String> getPredefinedExportConditionFormula ()
		{
			return this.predefinedexportcondformula ;
		} 
		private Double pricemin;
		private Double pricemax;
		public Double getSettingSotckPriceMin ()		{
			return this.pricemin;
		}
		public Double getSettingSotckPriceMax ()		{
			return this.pricemax;
		}
		public void setSettingStockPriceLevel (Double min, Double max)
		{
			this.pricemin = min;
			this.pricemax = max;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiAndGeGuMatchingConditionValuesChanges( this ) );
		}
		
		
		public String getConditionsDescriptions ()
		{
			String outputfilehead = "[导出条件:";
			return outputfilehead + this.getExportConditionFormula();
		}
		private Double settingzszmax;
		private Double settingzszmin;
		public Double getSettingZongShiZhiMax()		{
			return settingzszmax;
		}
		public Double getSettingZongShiZhiMin() {
			return settingzszmin;
		}
		public void setSettingZongShiZhix(Double seetingzszmin , Double seetingzszmax) {
			if(seetingzszmax != null) this.settingzszmax = seetingzszmax * 100000000.0;
			else	this.settingzszmax = seetingzszmax;
			
			if(seetingzszmin != null) this.settingzszmin = seetingzszmin * 100000000.0;
			else this.settingzszmin = seetingzszmin;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiAndGeGuMatchingConditionValuesChanges( this ) );
		}
		
		
		private Double settingltszmax; //流通市值
		private Double settingltszmin; //流通市值
		public Double getSettingLiuTongShiZhiMax() 		{
			return settingltszmax;
		}
		public Double getSettingLiuTongShiZhiMin() {
			return settingltszmin;
		}
		public void setSettingLiuTongShiZhi(Double seetingltszmin, Double seetingltszmax) {
			if(seetingltszmax != null)
				this.settingltszmax = seetingltszmax * 10000000.0;
			else
				this.settingltszmax = seetingltszmax;
			
			if(seetingltszmin != null)
				this.settingltszmin = seetingltszmin * 10000000.0;
			else
				this.settingltszmin = seetingltszmin;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiAndGeGuMatchingConditionValuesChanges( this ) );
		}
		private Double settingcjemax; //成交额
		private Double settingcjemin;
		public Double getSettingChenJiaoErMax() {
			return settingcjemax;
		}
		public Double getSettingChenJiaoErMin() {
			return settingcjemin;
		}
		public void setSettingChenJiaoEr(Double settingcjemin, Double settingcjemax) {
			if(settingcjemin  != null)
				this.settingcjemin = settingcjemin * 100000000.0;
			else
				this.settingcjemin = settingcjemin;
			if(settingcjemax != null)
				this.settingcjemax = settingcjemax * 100000000.0;
			else
				this.settingcjemax = settingcjemax;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiAndGeGuMatchingConditionValuesChanges( this ) );
		}
		private Integer settingcjezbdpmaxwkmin; //dpmax
		private Integer settingcjezbdpmaxwkmax; //dpmax
		public Integer getSettingCjeZbDpMaxWkMin() {
			return settingcjezbdpmaxwkmin;
		}
		public Integer getSettingCjeZbDpMaxWkMax() {
			return settingcjezbdpmaxwkmax;
		}
		public void setSettingCjeZbDpMaxWk(Integer settingdpcjemaxwkmin,Integer settingdpcjemaxwkmax) {
			this.settingcjezbdpmaxwkmin = settingdpcjemaxwkmin;
			this.settingcjezbdpmaxwkmax = settingdpcjemaxwkmax;
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiAndGeGuMatchingConditionValuesChanges( this ) );
		}
		private Integer settingcjezbdpminwkmin;
		private Integer settingcjezbdpminwkmax;
		public Integer getSettingCjeZbDpMinWkMin() {
			return settingcjezbdpminwkmin;
		}
		public Integer getSettingCjeZbDpMinWkMax() {
			return settingcjezbdpminwkmax;
		}
		public void setSettingCjeZbDpMinWk(Integer settingcjedpminwkmin,Integer settingcjedpminwkmax) {
			this.settingcjezbdpminwkmin = settingcjedpminwkmin;
			this.settingcjezbdpminwkmax = settingcjedpminwkmax;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiAndGeGuMatchingConditionValuesChanges( this ) );
		}
		private Integer settingcjemaxwkmin; // 成交额maxwk
		private Integer settingcjemaxwkmax; // 成交额maxwk
		public Integer getSettingChenJiaoErMaxWkMin() {
			return settingcjemaxwkmin;
		}
		public Integer getSettingChenJiaoErMaxWkMax() {
			return settingcjemaxwkmax;
		}
		public void setSettingChenJiaoErMaxWk(Integer settingcjemaxwkmin, Integer settingcjemaxwkmax) {
			this.settingcjemaxwkmin = settingcjemaxwkmin;
			this.settingcjemaxwkmax = settingcjemaxwkmax;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiAndGeGuMatchingConditionValuesChanges( this ) );
		}
		private Double settinghslmin; //
		private Double settinghslmax; //
		public Double getSettingHuanShouLvMin() {
			return settinghslmin;
		}
		public Double getSettingHuanShouLvMax() {
			return settinghslmax;
		}
		public void setSettingHuanShouLv(Double settinghslmin, Double settinghslmax) {
			this.settinghslmin = settinghslmin;
			this.settinghslmax = settinghslmax;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiAndGeGuMatchingConditionValuesChanges( this ) );
		}
		private String settingbk;
		public String getSettingBanKuai() {
			return settingbk;
		}
		public void setSettingBanKuai(String settingbk) {
			this.settingbk = settingbk;
		}
		private String tooltips = "";
		public String getTooltips() {
			return tooltips;
		}
		public void setTooltips(String tooltips) {
			this.tooltips = tooltips;
		}
		private boolean huibudownquekou;
		public boolean hasHuiBuDownQueKou() {
			return huibudownquekou;
		}
		public void setHuiBuDownQueKou(boolean huibudownquekou) {
			this.huibudownquekou = huibudownquekou;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiAndGeGuMatchingConditionValuesChanges( this ) );
		}
		private String settingMaFormula;
		public String getSettingMaFormula() {
			return settingMaFormula;
		}
		public void setSettingMaFormula (String maformula) {
			if(maformula != null ) 
				maformula = maformula.toUpperCase();
			this.settingMaFormula =  maformula;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiAndGeGuMatchingConditionValuesChanges( this ) );
			return;
		}
		boolean exportAllBanKuai;
		public boolean shouldExportAllBanKuai () {
			return exportAllBanKuai;
		}
		public void setExportAllBanKuai (boolean exportAllBanKuai) {
			this.exportAllBanKuai = exportAllBanKuai;
		}
		boolean exportBankuaiInConfig;
		public boolean shouldExportBankuaiInConfig () {
			return exportBankuaiInConfig;
		}
		public void setExportBankuaiInConfig (boolean exportBankuaiInConfig) {
			this.exportBankuaiInConfig = exportBankuaiInConfig;
		}
		boolean exportOnlyCurrentBanKuai;
		public boolean shouldExportOnlyCurrentBanKuai() {
			return exportOnlyCurrentBanKuai;
		}
		public void setExportOnlyCurrentBanKuai (boolean exportOnlyCurrentBanKuai) {
			this.exportOnlyCurrentBanKuai = exportOnlyCurrentBanKuai;
		}
		
//		public boolean shouldExportChenJiaoErZhanbiUpBanKuai () {
//			return exportCjeZhanbiUp;
//		}
//		public void setExportChenJiaoErZhanbiUpBanKuai (boolean exportCjeZhanbiUp) {
//			this.exportCjeZhanbiUp = exportCjeZhanbiUp;
//		}
		
//		public boolean shouldExportYangXianBanKuai () {
//			return exportYangXianBanKuai;
//		}
//		public void setExportYangXianBanKuai(boolean exportYangXianBanKuai) {
//			this.exportYangXianBanKuai = exportYangXianBanKuai;
//		}
		boolean exportOnlyBankuaiNotGeGu;
		public boolean shouldExportOnlyBankuaiNotGeGu() {
			return exportOnlyBankuaiNotGeGu;
		}
		public void setExportOnlyBankuaiNotGeGu(boolean exportOnlyBankuaiNotGeGu) {
			this.exportOnlyBankuaiNotGeGu = exportOnlyBankuaiNotGeGu;
		}
		boolean exportOnlyGeGuOntBanKuai;
		public boolean shouldExportOnlyGeGuNotBanKuai() {
			return exportOnlyGeGuOntBanKuai;
		}
		public void setExportOnlyGeGuNotBanKuai(boolean exportOnlyGeGuOntBanKuai) {
			this.exportOnlyGeGuOntBanKuai = exportOnlyGeGuOntBanKuai;
		}
		boolean exportOnlyCurrentGeGu;
		public boolean shouldExportOnlyCurrentGeGu() {
			return exportOnlyCurrentGeGu;
		}
		public void setExportOnlyCurrentGeGu(boolean exportOnlyCurrentGeGu) {
			this.exportOnlyCurrentGeGu = exportOnlyCurrentGeGu;
		}
		
		Boolean exportGeGuZhangfuQuJian;
		public Boolean shouldExporGeGuWithZhangFuQuJian() {
			if(exportGeGuZhangfuQuJian == null)
				return false;
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
		
//		Double cjebottomforyangxianlevle;
		//成交额低于
//		public Double getChenJiaoErBottomForYangXianLevle() {
//			return cjebottomforyangxianlevle;
//		}
//		public void setChenJiaoErBottomForYangXianLevle(Double cjebottomforyangxianlevle) {
//			this.cjebottomforyangxianlevle = cjebottomforyangxianlevle;
//		}
		//成交额低于水平必须有阳线
//		Double cjeyangxianlevel;
//		public Double getChenJiaoErBottomYangXianLevel() {
//			return cjeyangxianlevel;
//		}
//		public void setChenJiaoErBottomYangXianLevel(Double cjeyangxianlevel) {
//			this.cjeyangxianlevel = cjeyangxianlevel;
//		}
		//成交额低于
//		Double cjebottomforfanglianglevle;
//		public Double getChenJiaoErBottomForFangLiangLevle() {
//			return cjebottomforfanglianglevle;
//		}
//		public void setChenJiaoErBottomForFangLiangLevle(Double cjebottomforfanglianglevle) {
//			this.cjebottomforfanglianglevle = cjebottomforfanglianglevle;
//		}
		//成交额低于水平必须有放量
//		Integer cjefanglianglevel;
//		public Integer getChenJiaoErFangLiangLevel() {
//			return cjefanglianglevel;
//		}
//		public void setChenJiaoErFangLiangLevel(Integer cjefanglianglevel) {
//			this.cjefanglianglevel = cjefanglianglevel;
//		}
		Boolean exportST;
		public Boolean shouldExportST() {
			return exportST;
		}
		public void setExportST(Boolean exportST) {
			this.exportST = exportST;
		}
		private Double zhangfumax;
		private Double zhangfumin;
		public Double getSettingDailyZhangDieFuMax() {
			return this.zhangfumax ;
		}
		public Double getSettingDailyZhangDieFuMin() {
			return this.zhangfumin;
		}
		public void setSettingDailyZhangDieFu(Double zhangfumin1, Double zhangfumax1) 	{
			if (zhangfumax1 != null)	this.zhangfumax = zhangfumax1/100;
			else this.zhangfumax = zhangfumax1;
			
			if(zhangfumin1 != null) this.zhangfumin = zhangfumin1/100;
			else this.zhangfumin = zhangfumin1;
			
			bkfxhighlightvaluesoftableslisteners.forEach(l -> l.BanKuaiAndGeGuMatchingConditionValuesChanges( this ) );
		}
		private boolean onlyexportyellowsignbkstk;
		public void setExportOnlyYellowSignBkStk(boolean export) 	{
			this.onlyexportyellowsignbkstk = export;
		}
		public Boolean shouldExportOnlyYellowSignBkStk() {
			return this.onlyexportyellowsignbkstk ;
		} 
		private Double setttinglastwkcjezbgrowingratemin;
		private Double setttinglastwkcjezbgrowingratemax;
		public void setCjezbGrowingRate(Double min, Double max)
		{
			setttinglastwkcjezbgrowingratemin = min;
			setttinglastwkcjezbgrowingratemax = max;
		}
		public Double getCjezbGrowingRateMin()	{
			return setttinglastwkcjezbgrowingratemin;
		}
		public Double getCjezbGrowingRateMax()	{
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
//		private Boolean shouldExportDpCjeZbGrowingRate;
//		private Double exportDpCjeZbGrowingRatemin;
//		private Double exportDpCjeZbGrowingRatemax;
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
		
		public Object clone() throws CloneNotSupportedException {
	        return super.clone();
	    }

//		public void setExportDpCjeZbGrowingRate(Boolean b) 
//		{
//			this.shouldExportDpCjeZbGrowingRate = b;
//		}
//		public Boolean shouldExportDpCjeZbGrowingRate ()
//		{
//			return this.shouldExportDpCjeZbGrowingRate;
//		}
//
//		public void setExportDpCjeZbGrowingRateQuJian(Double min, Double max) 
//		{
//			this.exportDpCjeZbGrowingRatemin = min;
//			this.exportDpCjeZbGrowingRatemax = max;
//		}
//		public Double getExportDpCjeZbGrowingRateQuJianMin ()
//		{
//			return this.exportDpCjeZbGrowingRatemin;
//		}
//		public Double getExportDpCjeZbGrowingRateQuJianMax ()
//		{
//			return this.exportDpCjeZbGrowingRatemax;
//		}

}
