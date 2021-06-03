package com.exchangeinfomanager.nodes.nodejibenmian;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.commonlib.Season;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class NodeJiBenMian 
{	
			public NodeJiBenMian ()
			{
			}
			
			private LocalDate shangshiriqi ;
			private String myowncode;
			private LocalDate gainiantishidate;
			private String gainiantishi;
			private LocalDate quanshangpingjidate;
			private String quanshangpingji;
			private LocalDate fumianxiaoxidate;
			private String fumianxiaoxi;
			private String zhengxiangguan;
			private String fuxiangguan;
			private String jingZhengDuiShou;
			private String keHuCustom;
			private String suoshujiaoyisuo;
			private List<Object[]> zdgzmrmcykRecords;
			private List<Object[]> gudonginfo;
			private List<BkChanYeLianTreeNode> cylinfo;
			private List<Interval> hqgqgudong;
			private LocalDate lastestcaibaoriqi;
			private List<Interval> minxing;
			
			private Double nodecjezhanbiuplevel;
			private Double nodecjezhanbidownlevel;
			private Double nodecjlzhanbiuplevel;
			private Double nodecjlzhanbidownlevel;
			
			
			public void setNodeCjeZhanbiLevel (Double min, Double max)
			{
				if(min == null || min == 0.0) min = null;
				if(max == null || max == 0.0) max = null;
				
				this.nodecjezhanbidownlevel = min;
				this.nodecjezhanbiuplevel = max;
			}
			public Double[] getNodeCjeZhanbiLevel ()
			{
				Double[] zblevel =  {nodecjezhanbidownlevel,nodecjezhanbiuplevel};
				return zblevel;
			}
			public void setNodeCjlZhanbiLevel (Double min, Double max)
			{
				if(min == null || min == 0.0) min = null;
				if(max == null || max == 0.0) max = null;
				this.nodecjlzhanbidownlevel = min;
				this.nodecjlzhanbiuplevel = max;
			}
			public Double[] getNodeCjlZhanbiLevel ()
			{
				Double[] zblevel =  {nodecjlzhanbidownlevel,nodecjlzhanbiuplevel};
				return zblevel;
			}
			
			public void setLastestCaiBaoDate (LocalDate time)
			{
				if(lastestcaibaoriqi == null)
					lastestcaibaoriqi = time;
				else if(!this.lastestcaibaoriqi.equals(time))
					lastestcaibaoriqi = time;
			}
			public LocalDate getLastestCaiBaoDate ()
			{
				return lastestcaibaoriqi;
			}
			public void addGuDongHqgqInterval (LocalDate time) {
				if(hqgqgudong == null)
					hqgqgudong = new ArrayList<> ();
				
				LocalDate seasonstart = Season.getSeasonStartDate(time);
				LocalDate seasonend = Season.getSeasonEndDate(time);
				
				DateTime nodestartdt= new DateTime(seasonstart.getYear(), seasonstart.getMonthValue(), seasonstart.getDayOfMonth(), 0, 0, 0, 0);
				DateTime nodeenddt = new DateTime(seasonend.getYear(), seasonend.getMonthValue(), seasonend.getDayOfMonth(), 0, 0, 0, 0);
				Interval nodeinterval = new Interval(nodestartdt, nodeenddt);
				
				if(!hqgqgudong.contains(nodeinterval))
					hqgqgudong.add(nodeinterval);
			}
			public Boolean hasHqgqGuDong (LocalDate time)
			{
				Boolean has = false;
				if(hqgqgudong == null)
					return false;
				
				for(Interval tmpinterval : hqgqgudong) {
					DateTime newstartdt = tmpinterval.getStart();
					DateTime newenddt = tmpinterval.getEnd();
					
					LocalDate requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).minusDays(1);
					LocalDate requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).plusDays(1);
					if(time.isAfter(requiredstartday) && time.isBefore(requiredendday)) {
						has = true;
						break;
					}
				}
				
				return has;
			}
			public void addGuDongMinXingInterval (LocalDate time) {
				if(minxing == null)
					minxing = new ArrayList<> ();
				
				LocalDate seasonstart = Season.getSeasonStartDate(time);
				LocalDate seasonend = Season.getSeasonEndDate(time);
				
				DateTime nodestartdt= new DateTime(seasonstart.getYear(), seasonstart.getMonthValue(), seasonstart.getDayOfMonth(), 0, 0, 0, 0);
				DateTime nodeenddt = new DateTime(seasonend.getYear(), seasonend.getMonthValue(), seasonend.getDayOfMonth(), 0, 0, 0, 0);
				Interval nodeinterval = new Interval(nodestartdt, nodeenddt);
				
				if(!minxing.contains(nodeinterval))
					minxing.add(nodeinterval);
			}
			public Boolean hasMinXingGuDong (LocalDate time)
			{
				Boolean has = false;
				if(minxing == null)
					return false;
				
				for(Interval tmpinterval : minxing) {
					DateTime newstartdt = tmpinterval.getStart();
					DateTime newenddt = tmpinterval.getEnd();
					
					LocalDate requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).minusDays(1);
					LocalDate requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).plusDays(1);
					if(time.isAfter(requiredstartday) && time.isBefore(requiredendday)) {
						has = true;
						break;
					}
				}
				
				return has;
			}
			
			public List<BkChanYeLianTreeNode> getChanYeLianInfo ()
			{
				return this.cylinfo;
			}
			public void setShangShiRiQi (LocalDate ssrq)
			{
				this.shangshiriqi = ssrq;
			}
			public LocalDate getShangShiRiQi ()
			{
				if(this.shangshiriqi == null)
					return LocalDate.parse("1990-01-01");
				else
					return this.shangshiriqi ;
			}
			public String getMyowncode() {
				return myowncode;
			}

			public LocalDate getGainiantishidate() {
				return gainiantishidate;
			}
			public void setGainiantishidate(LocalDate gainiantishidate) {
				this.gainiantishidate = gainiantishidate;
			}
			public String getGainiantishi() {
				return gainiantishi;
			}
			public void setGainiantishi(String gainiantishi) {
				this.gainiantishi = gainiantishi;
			}
			public LocalDate getQuanshangpingjidate() {
				return quanshangpingjidate;
			}
			public void setQuanshangpingjidate(LocalDate quanshangpingjidate) {
				this.quanshangpingjidate = quanshangpingjidate;
			}
			public String getQuanshangpingji() {
				return quanshangpingji;
			}
			public void setQuanshangpingji(String quanshangpingji) {
				this.quanshangpingji = quanshangpingji;
			}
			public LocalDate getFumianxiaoxidate() {
				return fumianxiaoxidate;
			}
			public void setFumianxiaoxidate(LocalDate fumianxiaoxidate) {
				this.fumianxiaoxidate = fumianxiaoxidate;
			}
			public String getFumianxiaoxi() {
				return fumianxiaoxi;
			}
			public void setFumianxiaoxi(String fumianxiaoxi) {
				this.fumianxiaoxi = fumianxiaoxi;
			}
			public String getZhengxiangguan() {
				return zhengxiangguan;
			}
			public void setZhengxiangguan(String zhengxiangguan) {
				this.zhengxiangguan = zhengxiangguan;
			}
			public String getFuxiangguan() {
				return fuxiangguan;
			}
			public void setFuxiangguan(String fuxiangguan) {
				this.fuxiangguan = fuxiangguan;
			}
			public String getJingZhengDuiShou() {
				return jingZhengDuiShou;
			}
			public void setJingZhengDuiShou(String jingZhengDuiShou) {
				this.jingZhengDuiShou = jingZhengDuiShou;
			}
			public String getKeHuCustom() {
				return keHuCustom;
			}
			public void setKeHuCustom(String keHuCustom) {
				this.keHuCustom = keHuCustom;
			}
			public String getSuoShuJiaoYiSuo() {
				return suoshujiaoyisuo;
			}
			public void setSuoShuJiaoYiSuo(String suoshujiaoyisuo) {
				this.suoshujiaoyisuo = suoshujiaoyisuo;
			}
			public List<Object[]> getZdgzmrmcykRecords() {
				return zdgzmrmcykRecords;
			}
			public void setZdgzmrmcykRecords(List<Object[]> zdgzmrmcykRecords) {
				this.zdgzmrmcykRecords = zdgzmrmcykRecords;
			}
			public void addZdgzmrmcykRecord (Object[] record)
			{
				if(this.zdgzmrmcykRecords == null)
					this.zdgzmrmcykRecords = new ArrayList<>();
				
				this.zdgzmrmcykRecords.add(record);
			}
			public List<Object[]> getGuDongInfo() {
				return gudonginfo;
			}
			public void setGuDongInfo(List<Object[]> gdRecords) {
				this.gudonginfo = gdRecords;
			}
			
			public void setChanYeLianInfo(List<BkChanYeLianTreeNode> result)
			{
				this.cylinfo = result;
			}
		
}
