package com.exchangeinfomanager.asinglestockinfo;

import java.time.LocalDate;
import java.util.Date;

import org.jfree.data.category.DefaultCategoryDataset;

public interface BanKuaiAndStockBasic 
{
	public static int  TDXBK = 4, SUBBK = 5, BKGEGU = 6;
	
	public int getType ();
//	public void setSuoSuTdxBanKuai (String tdxbkcode);
//	public String getSuoSuTdxBanKuai ();
	
	/**
	 * @return the bankuainame
	 */
	public String getMyOwnName() ;
	/**
	 * @param bankuainame the bankuainame to set
	 */
	public void setMyOwnName(String bankuainame) ;
	/**
	 * @return the bankuaicode
	 */
	public String getMyOwnCode() ;
	/**
	 * @return the bankuaicreatedtime
	 */
	public String getCreatedTime() ;
	/**
	 * @param bankuaicreatedtime the bankuaicreatedtime to set
	 */
	public void setCreatedTime(String bankuaicreatedtime) ;
	public boolean checktHanYuPingYin (String nameorhypy);
	
	/**
	 * @return the jingZhengDuiShou
	 */
	public String getJingZhengDuiShou() ;
	/**
	 * @param jingZhengDuiShou the jingZhengDuiShou to set
	 */
	public void setJingZhengDuiShou(String jingZhengDuiShou);
	/**
	 * @return the keHuCustom
	 */
	public String getKeHuCustom() ;
	/**
	 * @param keHuCustom the keHuCustom to set
	 */
	public void setKeHuCustom(String keHuCustom) ;

	public LocalDate getGainiantishidate() ;
	public void setGainiantishidate(LocalDate gainiantishidate);
	public String getGainiantishi();
	public void setGainiantishi(String gainiantishi) ;
	public LocalDate getQuanshangpingjidate() ;
	public void setQuanshangpingjidate(LocalDate quanshangpingjidate);
	public String getQuanshangpingji() ;
	public void setQuanshangpingji(String quanshangpingji) ;
	public LocalDate getFumianxiaoxidate() ;
	public void setFumianxiaoxidate(LocalDate fumianxiaoxidate) ;
	public String getFumianxiaoxi() ;
	public void setFumianxiaoxi(String fumianxiaoxi) ;
	public String getZhengxiangguan() ;
	public void setZhengxiangguan(String zhengxiangguan);
	public String getFuxiangguan() ;
	public void setFuxiangguan(String fuxiangguan) ;
	
}
