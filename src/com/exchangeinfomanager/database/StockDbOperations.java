package com.exchangeinfomanager.database;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.asinglestockinfo.ASingleStockInfo;
import com.exchangeinfomanager.checkboxtree.CheckBoxTreeNode;
import com.exchangeinfomanager.gui.AccountAndChiCangConfiguration;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.CharMatcher;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.sun.rowset.CachedRowSetImpl;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

public class StockDbOperations 
{

	
	public StockDbOperations() 
	{
		this.connectdb = ConnectDataBase.getInstance();
		this.sysconfig = SystemConfigration.getInstance();
	}
	
	private ConnectDataBase connectdb;
	private SystemConfigration sysconfig ;
	
	
	static Element xmlroot;
	private Document ggcylxmldocument ;
	private SAXReader ggcylsaxreader;

	//private Object[][] zdgzmrmcykRecords = null;
	public ASingleStockInfo getZdgzMrmcZdgzYingKuiFromDB (ASingleStockInfo stockbasicinfo)
	{
		HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
		String sqlquerystat = null;
		sqlquerystat = getZdgzMrmcYingKuiSQLForMysql (stockbasicinfo);
		sqlstatmap.put("mysql", sqlquerystat);
		
		sqlquerystat = getZdgzMrmcYingKuiSQLForAccess (stockbasicinfo);
		sqlstatmap.put("access", sqlquerystat);
     
		 CachedRowSetImpl rs = null;
		 rs = connectdb.sqlQueryStatExecute(sqlstatmap);
		 stockbasicinfo.setZdgzmrmcykRecords( setZdgzMrmcZdgzYingKuiRecords (rs) );
		 
		 try {
				rs.close();
				rs = null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		 } 
		
		 return stockbasicinfo;
	}
	
	private String getZdgzMrmcYingKuiSQLForAccess(ASingleStockInfo stockbasicinfo) 
	{
		String stockcode = stockbasicinfo.getStockcode();
		String sqlquerystat1= "SELECT ggyk.日期, "
							+ "IIF(ggyk.盈亏金额>0,'盈利','亏损') AS 盈亏情况,"
							+ " ggyk.原因描述,"
							+ " ggyk.ID,"
							+ " ggyk.操作账号,"
							+  "'个股盈亏'" 
							+ " FROM  A股个股盈亏 ggyk "
							+ " WHERE ggyk.股票代码 =" + "'" + stockcode + "'" 
							
							+ " UNION ALL " 
							;
		//System.out.println(sqlquerystat1);
       String sqlquerystat2="SELECT czjl.日期, " +		""
							+ "IIF( czjl.买卖金额=0.0,'送转股',IIF(czjl.挂单 = true,IIF(czjl.买入卖出标志, '挂单买入', '挂单卖出'),IIF(czjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
							+ " czjl.原因描述,"
							+ " czjl.ID,"
							+ " czjl.买卖账号,"
							+ "'操作记录买卖'" 
							+ " FROM 操作记录买卖 czjl "
							+ "	WHERE czjl.股票代码 =" + "'" + stockcode + "'"
							
							+ " UNION ALL " 
							;
       //System.out.println(sqlquerystat2);
      String sqlquerystat3="SELECT rqczjl.日期,"
							+ "IIF( rqczjl.买卖金额=0.0,'送转股',IIF(rqczjl.挂单 = true,IIF(rqczjl.买入卖出标志, '挂单买入', '挂单卖出'),IIF(rqczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
							+ "rqczjl.原因描述,"
							+ "rqczjl.ID,"
							+ "rqczjl.买卖账号,"
							+ "'操作记录融券买卖'"
							+ " FROM 操作记录融券买卖 rqczjl"
							+ "	WHERE rqczjl.股票代码 =" + "'" + stockcode + "'"
							
							+ " UNION  ALL "
							;
      //System.out.println(sqlquerystat3);
	 String sqlquerystat4="SELECT rzczjl.日期,"
							+ "IIF( rzczjl.买卖金额=0.0,'送转股',IIF(rzczjl.挂单 = true,IIF(rzczjl.买入卖出标志, '挂单买入', '挂单卖出'),IIF(rzczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
							+ "rzczjl.原因描述,"
							+ "rzczjl.ID,"
							+ "rzczjl.买卖账号,"
							+ "'操作记录融资买卖'"
							+ " FROM 操作记录融资买卖 rzczjl"
							+ " WHERE rzczjl.股票代码 =" + "'" + stockcode + "'"
							
							+ " UNION ALL "
							;
	 //System.out.println(sqlquerystat4);
     String sqlquerystat5="SELECT zdgz.日期,"
							+ "zdgz.加入移出标志,"
							+ "zdgz.原因描述,"
							+ "zdgz.ID,"
							+ "null,"
							+ "'操作记录重点关注'"
							+ " FROM 操作记录重点关注  zdgz"
							+ " WHERE zdgz.股票代码 =" + "'" + stockcode + "'"
							+ " ORDER BY 1 DESC,5,3 " //desc
							;
     //System.out.println(sqlquerystat5);
	
     String sqlquerystat = sqlquerystat1 + sqlquerystat2 + sqlquerystat3 + sqlquerystat4 + sqlquerystat5;
     //System.out.println(sqlquerystat);
     
     return sqlquerystat;
		
	}

	private String getZdgzMrmcYingKuiSQLForMysql(ASingleStockInfo stockbasicinfo) 
	{
		String stockcode = stockbasicinfo.getStockcode();
		String sqlquerystat1= "SELECT ggyk.日期, "
							+ "IF(ggyk.盈亏金额>0,'盈利','亏损') AS 盈亏情况,"
							+ " ggyk.原因描述,"
							+ " ggyk.ID,"
							+ " ggyk.操作账号,"
							+  "'个股盈亏'" 
							+ " FROM  A股个股盈亏 ggyk "
							+ " WHERE ggyk.股票代码 =" + "'" + stockcode + "'" 
							
							+ " UNION ALL " 
							;
		//System.out.println(sqlquerystat1);
       String sqlquerystat2="SELECT czjl.日期, " +		""
							+ "IF( czjl.买卖金额=0,'送转股',IF(czjl.挂单 = true,IF(czjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(czjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
							+ " czjl.原因描述,"
							+ " czjl.ID,"
							+ " czjl.买卖账号,"
							+ "'操作记录买卖'" 
							+ " FROM 操作记录买卖 czjl "
							+ "	WHERE czjl.股票代码 =" + "'" + stockcode + "'"
							
							+ " UNION ALL " 
							;
       //System.out.println(sqlquerystat2);
      String sqlquerystat3="SELECT rqczjl.日期,"
							+ "IF( rqczjl.买卖金额=0,'送转股',IF(rqczjl.挂单 = true,IF(rqczjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(rqczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
							+ "rqczjl.原因描述,"
							+ "rqczjl.ID,"
							+ "rqczjl.买卖账号,"
							+ "'操作记录融券买卖'"
							+ " FROM 操作记录融券买卖 rqczjl"
							+ "	WHERE rqczjl.股票代码 =" + "'" + stockcode + "'"
							
							+ " UNION  ALL "
							;
      //System.out.println(sqlquerystat3);
	 String sqlquerystat4="SELECT rzczjl.日期,"
							+ "IF( rzczjl.买卖金额=0,'送转股',IF(rzczjl.挂单 = true,IF(rzczjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(rzczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
							+ "rzczjl.原因描述,"
							+ "rzczjl.ID,"
							+ "rzczjl.买卖账号,"
							+ "'操作记录融资买卖'"
							+ " FROM 操作记录融资买卖 rzczjl"
							+ " WHERE rzczjl.股票代码 =" + "'" + stockcode + "'"
							
							+ " UNION ALL "
							;
	 //System.out.println(sqlquerystat4);
     String sqlquerystat5="SELECT zdgz.日期,"
							+ "zdgz.加入移出标志,"
							+ "zdgz.原因描述,"
							+ "zdgz.ID,"
							+ "null,"
							+ "'操作记录重点关注'"
							+ " FROM 操作记录重点关注  zdgz"
							+ " WHERE zdgz.股票代码 =" + "'" + stockcode + "'"
							+ " ORDER BY 1 DESC,5,3 " //desc
							;
     //System.out.println(sqlquerystat5);
	
     String sqlquerystat = sqlquerystat1 + sqlquerystat2 + sqlquerystat3 + sqlquerystat4 + sqlquerystat5;
     //System.out.println(sqlquerystat);
     
     return sqlquerystat;
	}

	/*
	 * 获得和该股票相关的所有信息
	 */
	private Object[][] setZdgzMrmcZdgzYingKuiRecords(CachedRowSetImpl rs) 
	{
		Object[][] data = null;  
	    try  {     
	        rs.last();  
	        int rows = rs.getRow();  
	        data = new Object[rows][];    
	        int columnCount = 6;//列数  
	        rs.first();  
	        int k = 0;  
	        //while(rs.next())
	        for(int j=0;j<rows;j++) {   //{ "日期", "操作", "说明","ID","操作账户","信息表" };
	            Object[] row = new Object[columnCount];  
	            row[0] = rs.getString(1);
	            row[1] = rs.getString(2);
	            row[2] = rs.getString(3);
	            row[3] = rs.getString(4);
	            row[4] = rs.getString(5);
	            row[5] = rs.getString(6);
              
	            data[k] = row;  
	            k++; 
	            rs.next();
	        } 
	       
	    } catch(java.lang.NullPointerException e) { 
	    	e.printStackTrace();
	    }catch(Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	if(rs != null)
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }
	    
	    return data;
	}
	
	public String getMingRiJiHua() 
	{
		HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
		String sqlquerystat =null;
		sqlquerystat= "SELECT * FROM 操作记录重点关注   WHERE 日期= date_sub(curdate(),interval 1 day) AND 加入移出标志 = '明日计划' " ;
		sqlstatmap.put("mysql", sqlquerystat);
		
		sqlquerystat=  "SELECT * FROM 操作记录重点关注 "
				+ " WHERE 日期 <  DATE() AND  日期 > IIF( Weekday( date() ) =  2,date()-3,date()-1)  "
				+ "AND 加入移出标志 = '明日计划'"
				;
		sqlstatmap.put("access", sqlquerystat);
		
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlstatmap);
		
		String pmdresult = "";
		try  {     
	        rs.last();  
	        int rows = rs.getRow();  
	        rs.first();  
	        int k = 0;  
	        //while(rs.next())
	        for(int j=0;j<rows;j++) { 
	        	pmdresult = pmdresult + rs.getString("股票代码") + "  " + rs.getString("原因描述") + " ";
	            rs.next();
	        } 
	        
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    }catch(Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	if(rs != null)
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }
		
		return pmdresult;
	}
	
	public ASingleStockInfo getSingleStockBasicInfo(ASingleStockInfo stockbasicinfo) 
	{
		String stockcode = stockbasicinfo.getStockcode();
		CachedRowSetImpl rsagu = null;
		try {
			 String sqlquerystat= "SELECT * FROM A股   WHERE 股票代码 =" +"'" + stockcode +"'" ;	
			rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			if(this.getANewDtockCodeIndicate(rsagu) ) { 
				stockbasicinfo.setaNewDtockCodeIndicate(true);
			} else 
				setSingleStockInfo (stockbasicinfo,rsagu);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rsagu.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rsagu = null;
		}
		
		return stockbasicinfo;
	}
	
	private void setSingleStockInfo(ASingleStockInfo stockbasicinfo, CachedRowSetImpl rs) 
	{
		try {
			String stockname = rs.getString("股票名称").trim();
			stockbasicinfo.setStockname(stockname);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setStockname( " ");
		} catch (Exception ex) {
			System.out.println(ex);
		}
		
		try 
		{
			Date gainiantishidate = rs.getDate("概念时间");
			stockbasicinfo.setGainiantishidate(gainiantishidate);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setGainiantishidate(null);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		
		try {
			String gainiantishi = rs.getString("概念板块提醒");
			stockbasicinfo.setGainiantishi(gainiantishi);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setGainiantishi(" ");			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		
		try 
		{
			Date quanshangpingjidate = rs.getDate("券商评级时间");
			stockbasicinfo.setQuanshangpingjidate(quanshangpingjidate);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setQuanshangpingjidate(null);
		} catch (Exception e) {
			System.out.println(e);
		}
	
		try	{
			String quanshangpingji = rs.getString("券商评级提醒").trim();
			stockbasicinfo.setQuanshangpingji(quanshangpingji);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setQuanshangpingji("");			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		try	{
			Date fumianxiaoxidate = rs.getDate("负面消息时间");
			stockbasicinfo.setFumianxiaoxidate(fumianxiaoxidate);
		} catch(java.lang.NullPointerException ex1) { 
			stockbasicinfo.setFumianxiaoxidate(null);
		} catch(Exception ex2) {
			System.out.println(ex2);				
		}

		try	{
			String fumianxiaoxi = rs.getString("负面消息").trim();
			stockbasicinfo.setFumianxiaoxi(fumianxiaoxi);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setFumianxiaoxi("");			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		try {
			String zhengxiangguan = rs.getString("正相关及客户");
			stockbasicinfo.setZhengxiangguan(zhengxiangguan);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setZhengxiangguan("");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	
		try {
			String fuxiangguan = rs.getString("负相关及竞争对手");
			stockbasicinfo.setFuxiangguan(fuxiangguan);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setFuxiangguan("");			
		} catch (Exception e) {
			System.out.println(e);
		}
	
		try {
			String keHuCustom = rs.getString("客户");
			stockbasicinfo.setKeHuCustom(keHuCustom);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setKeHuCustom("");			
		} catch (Exception e) {
			System.out.println(e);
		}
	
		try {
			String jingZhengDuiShou = rs.getString("竞争对手");
			stockbasicinfo.setJingZhengDuiShou(jingZhengDuiShou);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setJingZhengDuiShou("");			
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private boolean getANewDtockCodeIndicate(CachedRowSetImpl rs) 
	{
		boolean newStockSign = false;
		try	{   
			rs.next();
			if(0 == rs.getRow()) {
				newStockSign= true;
			} else {
				newStockSign= false;
			}
			rs.first();
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null,"ConnectDatabase -> setANewDtockCodeIndicate 不可预知的错误");
			System.out.println(""+e);
		} 		
		
		return newStockSign;
	}

	public ASingleStockInfo getCheckListsXMLInfo(ASingleStockInfo stockbasicinfo)
	{
		String stockcode = stockbasicinfo.getStockcode();
		String sqlquerystat= "SELECT checklistsitems FROM A股   WHERE 股票代码 =" +"'" + stockcode +"'" ;
		//System.out.println(sqlquerystat);
		CachedRowSetImpl rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);

		try {
			rsagu.first();
			while (rsagu.next()) {
				String checklistsitems = rsagu.getString("checklistsitems");
				stockbasicinfo.setChecklistXml(checklistsitems);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			rsagu.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stockbasicinfo;
	}
	
	private String formateDateForDiffDatabase (String databasetype,String dbdate)
	{
		if(dbdate != null) {
			switch(databasetype) {
			case "access": return "#" + dbdate + "#";
							
			case "mysql":  return "\"" + dbdate + "\"";
							
			}
		} else
			return null;
		return null;
	}
	public boolean updateStockNewInfoToDb(ASingleStockInfo stockbasicinfo) 
	{
		String dategainiants = null;
		
		String txtareainputgainiants = null;
		String datequanshangpj = null;
		String txtfldinputquanshangpj;
		String datefumianxx = null;
		String txtfldinputfumianxx = null;
		
		String stockname = "'" +  stockbasicinfo.getStockname().trim() + "'" ;
		String stockcode = "'" +  stockbasicinfo.getStockcode().trim() + "'" ;
		
//		dategainiants = formateDateForDiffDatabase(  sysconfig.formatDate(stockbasicinfo.getGainiantishidate()) );
//		datequanshangpj = formateDateForDiffDatabase( sysconfig.formatDate(stockbasicinfo.getQuanshangpingjidate() ) );
//		datefumianxx = formateDateForDiffDatabase( sysconfig.formatDate(stockbasicinfo.getFumianxiaoxidate()  ) );
	    
		try {
				txtareainputgainiants = "'" + stockbasicinfo.getGainiantishi().trim() + "'";
		} catch (java.lang.NullPointerException  e) {
		}

		txtfldinputquanshangpj = "'" + stockbasicinfo.getQuanshangpingji().trim() + "'";
	    
		txtfldinputfumianxx = "'" + stockbasicinfo.getFumianxiaoxi() + "'";
	    
	    String txtfldinputzhengxiangguan = "'" + stockbasicinfo.getZhengxiangguan().trim() + "'";
	    String txtfldinputfuxiangguan = "'" + stockbasicinfo.getFuxiangguan().trim() + "'";
	    
	    String keHuCustom = stockbasicinfo.getKeHuCustom();
	    String jingZhengDuiShou = stockbasicinfo.getJingZhengDuiShou();
    
		 if(!stockbasicinfo.isaNewDtockCodeIndicate())
		 {
			 HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
			 String sqlupdatestat= "UPDATE A股  SET "
						+ " 股票名称=" + stockname +","
						+ " 概念时间=" + formateDateForDiffDatabase("mysql", sysconfig.formatDate(stockbasicinfo.getGainiantishidate()) ) +","
						+ " 概念板块提醒=" + txtareainputgainiants +","
						+ " 券商评级时间=" + formateDateForDiffDatabase("mysql", sysconfig.formatDate(stockbasicinfo.getQuanshangpingjidate() ) ) +","
						+ " 券商评级提醒=" + txtfldinputquanshangpj +","
						+ " 负面消息时间=" + formateDateForDiffDatabase("mysql", sysconfig.formatDate(stockbasicinfo.getFumianxiaoxidate()  ) ) +","
						+ " 负面消息=" + txtfldinputfumianxx +","
						+ " 正相关及客户=" + txtfldinputzhengxiangguan +","
						+ " 负相关及竞争对手=" + txtfldinputfuxiangguan +","
						+ " 客户=" + "'" + keHuCustom +"'" + ","
						+ " 竞争对手=" + "'" + jingZhengDuiShou + "'"  
						+ " WHERE 股票代码=" + stockcode
						;
			 sqlstatmap.put("mysql", sqlupdatestat);
			 //System.out.println(sqlupdatestat);
			 
			 if( connectdb.sqlUpdateStatExecute(sqlstatmap) !=0)
				 return true;
			 else return false;
		 }
		 else
		 {
			 HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
			 String sqlinsertstat = "INSERT INTO A股(股票名称,股票代码,概念时间,概念板块提醒,券商评级时间,券商评级提醒,负面消息时间,负面消息,正相关及客户,负相关及竞争对手) values ("
						+ stockname + ","
						+ stockcode + ","
						+ formateDateForDiffDatabase("mysql" , sysconfig.formatDate(stockbasicinfo.getGainiantishidate()) ) + ","
						+ txtareainputgainiants + ","
						+ datequanshangpj + ","
						+ txtfldinputquanshangpj + ","
						+ datefumianxx + ","
						+ txtfldinputfumianxx + ","
						+ txtfldinputzhengxiangguan + ","
						+ txtfldinputfuxiangguan 
						+ ")"
						;
			 //System.out.println(sqlinsertstat); 
			 sqlstatmap.put("mysql", sqlinsertstat);
			 if( connectdb.sqlInsertStatExecute(sqlstatmap) >0 ) {
				 stockbasicinfo.setaNewDtockCodeIndicate(false);
				 return true;
			 } else return false;
		 }
		 

		
	}


	public boolean updateChecklistsitemsToDb (ASingleStockInfo stockbasicinfo)
	{
		String stockcode = stockbasicinfo.getStockcode();
		String checklistsitems = stockbasicinfo.getChecklistXml();
		String sqlupdatestat= "UPDATE A股  SET "
				+ "checklistsitems=" + "'" + checklistsitems + "'"
				+ "WHERE 股票代码=" + stockcode
				
				;
	// System.out.println(sqlupdatestat);
	 connectdb.sqlUpdateStatExecute(sqlupdatestat);
	 return true;
	}

	public int setZdgzRelatedActions(JiaRuJiHua jiarujihua) 
	{
		String stockcode = jiarujihua.getStockCode();		
		//String addedday = formateDateForDiffDatabase(sysconfig.formatDate(new Date() ));
		String zdgzsign = jiarujihua.getGuanZhuType();
		String shuoming = "";
		if(jiarujihua.isMingRiJiHua()) {
			zdgzsign = "明日计划";
			shuoming = jiarujihua.getJiHuaLeiXing() + "(价格" + jiarujihua.getJiHuaJiaGe() + ")(" +  jiarujihua.getJiHuaShuoMing();
		} else
			shuoming =  jiarujihua.getJiHuaShuoMing();
		
		HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
		String sqlinsertstat = "INSERT INTO 操作记录重点关注(股票代码,日期,加入移出标志,原因描述) values ("
				+ "'" +  stockcode.trim() + "'" + "," 
				+ formateDateForDiffDatabase("mysql",sysconfig.formatDate(new Date() )) + ","
				+  "'" + zdgzsign + "'" + ","
				+ "'" + shuoming + "'"  
				+ ")"
				;
		//System.out.println(sqlinsertstat);
		sqlstatmap.put("mysql", sqlinsertstat);
		int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlstatmap);
		return autoIncKeyFromApi;
		
		
	}
	
	public ASingleStockInfo getTDXBanKuaiInfo(ASingleStockInfo stockbasicinfo) 
	{
		String stockcode = stockbasicinfo.getStockcode();
		HashMap<String,String> tmpsysbk = new HashMap<String,String> ();
		
//		SELECT gpgn.概念板块, tdxbk.`板块ID`
//		FROM 股票通达信概念板块对应表 gpgn, 通达信板块列表 tdxbk
//		WHERE 股票代码= '000001' AND gpgn.概念板块 = tdxbk.`板块名称`
		String sqlquerystat =null;
		sqlquerystat=  "SELECT gpgn.概念板块 板块, tdxbk.`板块ID` 板块代码 FROM 股票通达信概念板块对应表 gpgn, 通达信板块列表 tdxbk"
				+ "  WHERE 股票代码=" + "'" +  stockcode.trim() + "'" + "AND gpgn.概念板块 = tdxbk.`板块名称`"
				+ "UNION " 
				+ " SELECT gphy.行业板块 板块, tdxbk.`板块ID` 板块代码 FROM 股票通达信行业板块对应表 gphy, 通达信板块列表 tdxbk "
				+ " WHERE 股票代码=" + "'" +  stockcode.trim() + "'" + "AND gphy.`行业板块` = tdxbk.`板块名称`"
				+ "UNION " 
				+ " SELECT gpfg.`风格板块`板块, tdxbk.`板块ID` 板块代码  FROM 股票通达信风格板块对应表 gpfg, 通达信板块列表 tdxbk"
				+ "  WHERE 股票代码= "+ "'" +  stockcode.trim() + "'" + "AND gpfg.`风格板块` = tdxbk.`板块名称`"
				;
		
		System.out.println(sqlquerystat);
		CachedRowSetImpl rs_gn = connectdb.sqlQueryStatExecute(sqlquerystat);
		try  {     
	        while(rs_gn.next()) {
	        	String bkcode = rs_gn.getString("板块代码");
	        	String bkname = rs_gn.getString("板块");
	        	tmpsysbk.put(bkcode,bkname);
	        } 
	        
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    	
	    }catch(Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	if(rs_gn != null) {
	    		try {
					rs_gn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		rs_gn = null;
	    	}
	    }
		
		stockbasicinfo.setSuoShuTDXSysBanKuai(tmpsysbk);
		
//		sqlquerystat=  "SELECT 自定义板块 FROM 股票通达信自定义板块对应表  WHERE 股票代码= "
//						+ "'" +  stockcode.trim() + "'"
//						;
//		//System.out.println(sqlquerystat);
//		CachedRowSetImpl rs_zdy = connectdb.sqlQueryStatExecute(sqlquerystat);
//		HashSet<String> tmpzdybk = new HashSet<String> ();
//		try  {     
//			rs_zdy.last();  
//	        int rows = rs_zdy.getRow();  
//	        rs_zdy.first();  
//	        int k = 0;  
//	        //while(rs.next())
//	        for(int j=0;j<rows;j++) { 
//	        	tmpzdybk.add( rs_zdy.getString("自定义板块") );
//	        	rs_zdy.next();
//	        } 
//	        rs_zdy.close();
//	    }catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    }catch(Exception e) {
//	    	e.printStackTrace();
//	    }
//		
//		stockbasicinfo.setSuoShuTDXZdyBanKuai(tmpzdybk);
		
		return stockbasicinfo;
	}

	public String getStockCodeByName(String stockname) 
	{
		String sqlquerystat= "SELECT 股票代码 FROM A股   WHERE 股票名称 =" +"'" + stockname +"'" ;
		//System.out.println(sqlquerystat);
		CachedRowSetImpl rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);

		String stockcode = null;
		try {
			//rsagu.first();
			while (rsagu.next()) {
				stockcode = rsagu.getString("股票代码");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rsagu.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	
		return stockcode;
	}

	public String getStockCodeByEverUsedName(String stockname) 
	{
		HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
		String sqlquerystat = null;
		sqlquerystat= "SELECT 股票代码 FROM A股   WHERE 曾用名  LIKE" +"'%" + stockname +"%'" ;
		sqlstatmap.put("mysql", sqlquerystat);
		sqlquerystat= "SELECT 股票代码 FROM A股   WHERE 曾用名  LIKE" +"'*" + stockname +"*%'" ;
		sqlstatmap.put("access", sqlquerystat);

		CachedRowSetImpl rsagu = connectdb.sqlQueryStatExecute(sqlstatmap);

		String stockcode = "";
		try {
			//rsagu.first();
			while (rsagu.next()) 
				stockcode = rsagu.getString("股票代码");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rsagu.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	
		return stockcode;
	}


	/*
	 * 从通达信中导入股票曾用名的信息
	 */
	public File refreshEverUsedStorkName()
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步通达信曾用名报告.txt");
	
		File file = new File(sysconfig.getTDXStockEverUsedNameFile() );
		if(!file.exists() ) {
			 System.out.println("File not exist");
			 return null;
		 }
		
		 int addednumber =0;
		 HashMap<String,String> cymmap = Maps.newHashMap();
		 FileInputStream in = null ;  
		 BufferedInputStream dis = null;
		 try {
			 Files.append("开始导入通达信股票曾用名信息:" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			   in = new FileInputStream(file);  
			   dis = new BufferedInputStream(in);
			   
			   	  int eachcymbytenumber = 64;
	              byte[] itemBuf2 = new byte[eachcymbytenumber];
	              int i=0;
	              
	              while (dis.read(itemBuf2, 0, eachcymbytenumber) != -1) {
	            	String cymline = (new String(itemBuf2,0,63)).trim();
		           	List<String> tmplinelist = Splitter.fixedLength(6).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(cymline);
		           	System.out.println(tmplinelist);
		           	
		           	String stockcode = tmplinelist.get(0).trim();
		           	String stockcym = tmplinelist.get(1).replaceAll("\\s*", "");
		           	
		           	if(!cymmap.keySet().contains(tmplinelist.get(0).trim()) )
		           		cymmap.put(stockcode,stockcym);
		           	else {
		           		String cymstr = cymmap.get(stockcode);
		           		cymstr = cymstr +  "|" + stockcym;  
		           		cymmap.put(stockcode,cymstr);
		           	}
	              }
			   
		 } catch (Exception e) {
			 e.printStackTrace();
			 return null;
		 } finally {
			 if(in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			 if(dis != null)
				try {
					dis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 }
		
		 for(String stockcode:cymmap.keySet()) {
			 String cymstr = cymmap.get(stockcode);
			 
			 String sqlupdatestat= "UPDATE A股  SET "
						+ " 曾用名=" + "'" + cymstr + "'"
						+ " WHERE 股票代码=" + stockcode
						;
			 System.out.println(sqlupdatestat);
			 connectdb.sqlUpdateStatExecute(sqlupdatestat);
		 }
		
		 
		 
		return tmprecordfile;
	}

	public Object[][] getTodaysOperations() 
	{
		 String searchdate = sysconfig.formatDate( new Date () );
		 searchdate = searchdate.replaceAll("\\d{2}:\\d{2}:\\d{2}", "");
		 String sqlquerystat2=" SELECT czjl.股票代码, " 
					+ " IF( czjl.买卖金额=0.0,'送转股',IF(czjl.挂单 = true,IF(czjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(czjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
					+ " czjl.原因描述,"
					+ " czjl.ID,"
					+ " czjl.买卖账号,"
					+ "'操作记录买卖'" 
					+ " FROM 操作记录买卖 czjl "
					+ "	WHERE czjl.日期>=" + "'" + searchdate + "'"
					
					+ " UNION ALL " 
					;
		System.out.println(sqlquerystat2);
		String sqlquerystat3=" SELECT rqczjl.股票代码, "
							+ "IF( rqczjl.买卖金额=0.0,'送转股',IF(rqczjl.挂单 = true,IF(rqczjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(rqczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
							+ "rqczjl.原因描述,"
							+ "rqczjl.ID,"
							+ "rqczjl.买卖账号,"
							+ "'操作记录融券买卖'"
							+ " FROM 操作记录融券买卖 rqczjl"
							+ "	WHERE rqczjl.日期>=" + "'" + searchdate + "'"
							
							+ " UNION  ALL "
							;
		System.out.println(sqlquerystat3);
		String sqlquerystat4=" SELECT rzczjl.股票代码,"
							+ "IF( rzczjl.买卖金额=0.0,'送转股',IF(rzczjl.挂单 = true,IF(rzczjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(rzczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
							+ "rzczjl.原因描述,"
							+ "rzczjl.ID,"
							+ "rzczjl.买卖账号,"
							+ "'操作记录融资买卖'"
							+ " FROM 操作记录融资买卖 rzczjl"
							+ " WHERE rzczjl.日期>=" + "'" + searchdate + "'"
							
							
							;
		System.out.println(sqlquerystat4);
		String sqlquerystat =  sqlquerystat2 + sqlquerystat3 + sqlquerystat4 ;
		
		HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
		sqlstatmap.put("mysql", sqlquerystat);
		
//		sqlquerystat = getZdgzMrmcYingKuiSQLForAccess (stockbasicinfo);
//		sqlstatmap.put("access", sqlquerystat);
     
		 CachedRowSetImpl rs = null;
		 rs = connectdb.sqlQueryStatExecute(sqlstatmap);
		 
		 Object[][] data = null;  
		    try  {     
		        rs.last();  
		        int rows = rs.getRow();  
		        data = new Object[rows][];    
		        int columnCount = 4;//列数  
		        rs.first();  
		        int k = 0;  
		        //while(rs.next())
		        for(int j=0;j<rows;j++) {   //{ "日期", "操作", "说明","ID","操作账户","信息表" };
		            Object[] row = new Object[columnCount];  
		            row[0] = rs.getString(1);
		            row[1] = rs.getString(2);
		            row[2] = rs.getString(3);
		            row[3] = rs.getString(5);

		            data[k] = row;  
		            k++; 
		            rs.next();
		        } 
		       
		    } catch(java.lang.NullPointerException e) { 
		    	e.printStackTrace();
		    }catch(Exception e) {
		    	e.printStackTrace();
		    } finally {
		    	if(rs != null)
					try {
						rs.close();
						rs = null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
		    }
		    
		    return data;
	}
	
	public File refreshStockJiBenMianInfoFromTdxFoxProFile ()
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步通达信指数基本面报告.tmp");
		
		Charset stringCharset = Charset.forName("Cp866");
		String dbffile = sysconfig.getTdxFoxProFileSource();
//        InputStream dbf = getClass().getClassLoader().getResourceAsStream(dbffile);
        InputStream dbf = null;
		try {
			dbf = new FileInputStream(new File(dbffile) );
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

        DbfRecord rec;
        String sqlinsetstat = null;
        try (DbfReader reader = new DbfReader(dbf)) {
            DbfMetadata meta = reader.getMetadata();

            System.out.println("Read DBF Metadata: " + meta);
            int recCounter = 0;
            while ( (rec = reader.read() ) != null) {
                rec.setStringCharset(stringCharset);
//                System.out.println("Record is DELETED: " + rec.isDeleted());
//                System.out.println(rec.getRecordNumber());
//                System.out.println(rec.toMap());
//        		{SC=0, GPDM=000001, GXRQ=20170811, ZGB=1717041.12, GJG=180199.00, FQRFRG=12292000.00, FRG=54769000.00, BG=0.00, HG=0.00, LTAG=1691799.00, ZGG=0.68, 
//        		ZPG=null, ZZC=3092142080.0, LDZC=0.0, GDZC=7961000.0, WXZC=4636000.0, CQTZ=379179.0, LDFZ=0.0, CQFZ=0.0, ZBGJJ=56465000.0, JZC=211454000.0, 
//        		ZYSY=54073000.0, ZYLY=0.0, QTLY=0.0, YYLY=40184000.0, TZSY=739000.0, BTSY=-128180000.0, YYWSZ=-107760000.0, SNSYTZ=0.0, LYZE=16432000.0, 
//        		SHLY=12554000.0, JLY=12554000.0, WFPLY=73110000.0, TZMGJZ=11.150, DY=18, HY=1, ZBNB=6, SSDATE=19910403, MODIDATE=null, GDRS=null}

                Map<String, Object> jbminfomap = rec.toMap();
                String stockcode = jbminfomap.get("GPDM").toString();
                String datavalueindb = jbminfomap.get("GXRQ").toString() ;
                if( !(stockcode.startsWith("00") || stockcode.startsWith("30") || stockcode.startsWith("60") ) 
                		|| stockcode.equals("000003") 
                		|| datavalueindb.equals("0") )
                	continue;

                String lastupdatedate = formateDateForDiffDatabase("mysql", sysconfig.formatDate(  sysconfig.formateStringToDate(datavalueindb) ) );
                double zgb = Double.parseDouble(jbminfomap.get("ZGB").toString());
                double gjg  = Double.parseDouble(jbminfomap.get("GJG").toString());
                double fqrfrg  = Double.parseDouble(jbminfomap.get("FQRFRG").toString());
                double frg  = Double.parseDouble(jbminfomap.get("FRG").toString());
                double bg  = Double.parseDouble(jbminfomap.get("BG").toString());
                double hg  = Double.parseDouble(jbminfomap.get("HG").toString());
                double ltag  = Double.parseDouble(jbminfomap.get("LTAG").toString());
                double zgg  = Double.parseDouble(jbminfomap.get("ZGG").toString());
                
                String zpg = "' '";
                if(jbminfomap.get("ZPG") != null)
                	zpg  = jbminfomap.get("ZPG").toString(); 
                double zzc  = Double.parseDouble(jbminfomap.get("ZZC").toString());
                double ldzc  = Double.parseDouble(jbminfomap.get("LDZC").toString());
                double gdzc  = Double.parseDouble(jbminfomap.get("GDZC").toString());
                double wxzc  = Double.parseDouble(jbminfomap.get("WXZC").toString());
                float cqtz  = Float.parseFloat(jbminfomap.get("CQTZ").toString());
                double ldfz  = Double.parseDouble(jbminfomap.get("LDFZ").toString());
                double cqfz  = Double.parseDouble(jbminfomap.get("CQFZ").toString());
                double zbgjj  = Double.parseDouble(jbminfomap.get("ZBGJJ").toString());
                double jzc  = Double.parseDouble(jbminfomap.get("JZC").toString());
                
                double zysy  = Double.parseDouble(jbminfomap.get("ZYSY").toString());
                double zyly  = Double.parseDouble(jbminfomap.get("ZYLY").toString());
                double qtly  = Double.parseDouble(jbminfomap.get("QTLY").toString());
                double yyly  = Double.parseDouble(jbminfomap.get("YYLY").toString());
                double tzsy  = Double.parseDouble(jbminfomap.get("TZSY").toString());
                double btsy  = Double.parseDouble(jbminfomap.get("BTSY").toString());
                double yywsz  = Double.parseDouble(jbminfomap.get("YYWSZ").toString());
                double snsytz  = Double.parseDouble(jbminfomap.get("SNSYTZ").toString()); 
                double lyze  = Double.parseDouble(jbminfomap.get("LYZE").toString());
                
                double shly  = Double.parseDouble(jbminfomap.get("SHLY").toString());
                double jly  = Double.parseDouble(jbminfomap.get("JLY").toString());
                double wfply  = Double.parseDouble(jbminfomap.get("WFPLY").toString());
                double tzmgjz  = Double.parseDouble(jbminfomap.get("TZMGJZ").toString());
                int dy = Integer.parseInt(jbminfomap.get("DY").toString() );
                int hy = Integer.parseInt(jbminfomap.get("HY").toString() );
                int zbnb = Integer.parseInt(jbminfomap.get("ZBNB").toString() );
                
                 sqlinsetstat = "INSERT INTO 股票通达信基本面信息对应表 (股票代码GPDM, 总股本ZGB,更新日期GXRQ, GJG,FQRFRG,法人股FRG,B股BG,H股HG,流通A股LTAG,每股收益ZGG,"
                														+  "ZPG,总资产ZZC,流动资产LDZC,固定资产GDZC,无形资产WXZC,股东人数CQTZ,流动负债LDFZ,少数股权CQFZ,"
                														+ "公积金ZBGJJ,净资产JZC,主营收益ZYSY,营业成本ZYLY,应收帐款QTLY,营业利润YYLY,投资收益TZSY,"
                														+ "经营现金流BTSY,总现金流YYWSZ,存货SNSYTZ,利润总额LYZE,税后利润SHLY,净利润JLY,"
                														+ "未分配利益WFPLY,净资产TZMGJZ,地域DY,行业HY,ZBNB) "
                		              		+ "VALUES("
                		              		+ "'" + stockcode + "'" +","
                		              		+ zgb +","
                		              		+ lastupdatedate +","
                		              		+ gjg +","
                		              		+ fqrfrg +","
                		              		+ frg +","
		                					+ bg +","
		                					+ hg +","
		                					 + ltag +","
		                					 + zgg + ","
		                					+ zpg +","
		                					+ zzc +","
		                					+ ldzc +","
		                					+ gdzc +","
		                					+ wxzc +","
		                					+ cqtz +","
		                					+ ldfz +","
		                					+ cqfz +","
		                					+ zbgjj +","
		                					+ jzc +","
		                					+ zysy +","
		                					+ zyly +","
		                					+ qtly +","
		                					+ yyly +","
		                					+ tzsy +","
		                					+ btsy +","
		                					+ yywsz +","
		                					+ snsytz +","
		                					+ lyze +","
		                					+ shly +","
		                					+ jly +","
		                					+ wfply +","
		                					+ tzmgjz +","
		                					+ dy +","
		                					+ hy +","
		                					+ zbnb
                		              		+ ")"
                		              		+ " ON DUPLICATE KEY UPDATE "
                        					+ " 总股本ZGB=" + zgb +","
                        					+ " 更新日期GXRQ=" + lastupdatedate +","
                        					+ " GJG=" + gjg +","
                        					+ " FQRFRG=" + fqrfrg +","
                        					+ " 法人股FRG=" + frg +","
                        					+ " B股BG=" + bg +","
                        					+ " H股HG=" + hg +","
                        					+ " 流通A股LTAG=" + ltag +","
                        					+ " 每股收益ZGG=" + zgg + ","
                        					+ " ZPG=" + zpg +","
                        					+ " 总资产ZZC=" + zzc +","
                        					+ " 流动资产LDZC=" + ldzc +","
                        					+ " 固定资产GDZC=" + gdzc +","
                        					+ " 无形资产WXZC=" + wxzc +","
                        					+ " 股东人数CQTZ=" + cqtz +","
                        					+ " 流动负债LDFZ=" + ldfz +","
                        					+ " 少数股权CQFZ=" + cqfz +","
                        					+ " 公积金ZBGJJ=" + zbgjj +","
                        					+ " 净资产JZC=" + jzc +","
                        					+ " 主营收益ZYSY=" + zysy +","
                        					+ " 营业成本ZYLY=" + zyly +","
                        					+ " 应收帐款QTLY=" + qtly +","
                        					+ " 营业利润YYLY=" + yyly +","
                        					+ " 投资收益TZSY=" + tzsy +","
                        					+ " 经营现金流BTSY=" + btsy +","
                        					+ " 总现金流YYWSZ=" + yywsz +","
                        					+ " 存货SNSYTZ=" + snsytz +","
                        					+ " 利润总额LYZE=" + lyze +","
                        					+ " 税后利润SHLY=" + shly +","
                        					+ " 净利润JLY=" + jly +","
                        					+ " 未分配利益WFPLY=" + wfply +","
                        					+ " 净资产TZMGJZ=" + tzmgjz +","
                        					+ " 地域DY=" + dy +","
                        					+ " 行业HY=" + hy +","
                        					+ " ZBNB=" + zbnb
                        					; 
//              System.out.println(sqlinsetstat);
              connectdb.sqlUpdateStatExecute(sqlinsetstat);
                recCounter++;

                //如果在表：A股中也没有这个股票代码，则在A股中也增加该股票
                sqlinsetstat = "IF NOT EXISTS(SELECT 1 FROM A股  WHERE 股票代码 = '" + stockcode + "')"
                		+ "    INSERT INTO A股 (股票代码) VALUES ('"+ stockcode + "')"
                		;
                connectdb.sqlInsertStatExecute(sqlinsetstat);
            }
        } catch (IOException e1) {
        	System.out.println("出错SQL是:" + sqlinsetstat);
			e1.printStackTrace();
		} catch (ParseException e2) {
			System.out.println("出错SQL是:" + sqlinsetstat);
			
			e2.printStackTrace();
		} catch (Exception e3) {
			System.out.println("出错SQL是:" + sqlinsetstat);
			e3.printStackTrace();
		}
        
        return tmprecordfile;
		
	}




}
