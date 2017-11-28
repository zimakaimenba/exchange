package com.exchangeinfomanager.bankuaifengxi;

import java.time.LocalDate;

public 	class NodeFengXiData 
{
		public NodeFengXiData (String pnodecode, LocalDate requireddate)
		{
			nodecode = pnodecode;
			nodefengxidate = requireddate;
		}
		private String nodecode;
		private LocalDate nodefengxidate; //分析周日期
//		private Double ggbkzhanbi; //个股板块占比
		private Double ggbkzhanbigrowthrate;  //个股板块占比增速
		private Integer ggbkzhanbimaxweek;  //个股板块占比最大周
		private Double ggbkcjegrowthzhanbi;  //个股板块成交量贡献率
		private Double ggdpzhanbi;  //个股大盘占比
		private Double ggdpcjegrowthzhanbi;  //个股大盘成交量贡献率
		private Integer ggdpzhanbimaxweek;
		/**
		 * @return the ggdpcjegrowthzhanbi
		 */
		public Double getGgdpcjegrowthzhanbi() {
			return ggdpcjegrowthzhanbi;
		}
		/**
		 * @param ggdpcjegrowthzhanbi the ggdpcjegrowthzhanbi to set
		 */
		public void setGgdpcjegrowthzhanbi(Double ggdpcjegrowthzhanbi) {
			this.ggdpcjegrowthzhanbi = ggdpcjegrowthzhanbi;
		}
		/**
		 * @return the ggdpzhanbimaxweek
		 */
		public Integer getGgdpzhanbimaxweek() {
			return ggdpzhanbimaxweek;
		}
		/**
		 * @param ggdpzhanbimaxweek the ggdpzhanbimaxweek to set
		 */
		public void setGgdpzhanbimaxweek(Integer ggdpzhanbimaxweek) {
			this.ggdpzhanbimaxweek = ggdpzhanbimaxweek;
		}
		/**
		 * @return the ggdpzhanbi
		 */
		public Double getGgdpzhanbi() {
			return ggdpzhanbi;
		}
		/**
		 * @param ggdpzhanbi the ggdpzhanbi to set
		 */
		public void setGgdpzhanbi(Double ggdpzhanbi) {
			this.ggdpzhanbi = ggdpzhanbi;
		}
		private Double ggdpzhanbigrowthrate; //个股大盘占比增速
		/**
		 * @return the nodecode
		 */
		public String getNodecode() {
			return nodecode;
		}
		/**
		 * @return the nodefengxidate
		 */
		public LocalDate getNodefengxidate() {
			return nodefengxidate;
		}
//		/**
//		 * @return the ggbkzhanbi
//		 */
//		public Double getGgbkzhanbi() {
//			return ggbkzhanbi;
//		}
//		/**
//		 * @param ggbkzhanbi the ggbkzhanbi to set
//		 */
//		public void setGgbkzhanbi(Double ggbkzhanbi) {
//			this.ggbkzhanbi = ggbkzhanbi;
//		}
		/**
		 * @return the ggbkzhanbigrowthrate
		 */
		public Double getGgbkzhanbigrowthrate() {
			return ggbkzhanbigrowthrate;
		}
		/**
		 * @param ggbkzhanbigrowthrate the ggbkzhanbigrowthrate to set
		 */
		public void setGgbkzhanbigrowthrate(Double ggbkzhanbigrowthrate) {
			this.ggbkzhanbigrowthrate = ggbkzhanbigrowthrate;
		}
		/**
		 * @return the ggbkzhanbimaxweek
		 */
		public Integer getGgbkzhanbimaxweek() {
			return ggbkzhanbimaxweek;
		}
		/**
		 * @param ggbkzhanbimaxweek the ggbkzhanbimaxweek to set
		 */
		public void setGgbkzhanbimaxweek(Integer ggbkzhanbimaxweek) {
			this.ggbkzhanbimaxweek = ggbkzhanbimaxweek;
		}
		/**
		 * @return the ggbkcjegrowthzhanbi
		 */
		public Double getGgbkcjegrowthzhanbi() {
			return ggbkcjegrowthzhanbi;
		}
		/**
		 * @param ggbkcjegrowthzhanbi the ggbkcjegrowthzhanbi to set
		 */
		public void setGgbkcjegrowthzhanbi(Double ggbkcjegrowthzhanbi) {
			this.ggbkcjegrowthzhanbi = ggbkcjegrowthzhanbi;
		}
		/**
		 * @return the ggdpzhanbigrowthrate
		 */
		public Double getGgdpzhanbigrowthrate() {
			return ggdpzhanbigrowthrate;
		}
		/**
		 * @param ggdpzhanbigrowthrate the ggdpzhanbigrowthrate to set
		 */
		public void setGgdpzhanbigrowthrate(Double ggdpzhanbigrowthrate) {
			this.ggdpzhanbigrowthrate = ggdpzhanbigrowthrate;
		}
		
}
