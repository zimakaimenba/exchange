package com.exchangeinfomanager.database;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.sun.rowset.CachedRowSetImpl;

public class StockDbOperations 
{

	
	public StockDbOperations() 
	{
		this.connectdb = ConnectDataBase2.getInstance();
		this.sysconfig = SystemConfigration.getInstance();
	}
	
	private ConnectDataBase2 connectdb;
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
		HashSet<String> tmpsysbk = new HashSet<String> ();
		
		String sqlquerystat =null;
		sqlquerystat=  "SELECT 概念板块 FROM 股票通达信概念板块对应表  WHERE 股票代码= "
				+ "'" +  stockcode.trim() + "'"
				+ "UNION " 
				+ " SELECT 行业板块 FROM 股票通达信行业板块对应表  WHERE 股票代码= "
				+ "'" +  stockcode.trim() + "'"
				+ "UNION " 
				+ " SELECT 风格板块 FROM 股票通达信风格板块对应表  WHERE 股票代码= "
				+ "'" +  stockcode.trim() + "'"
				;
		
		//System.out.println(sqlquerystat);
		CachedRowSetImpl rs_gn = connectdb.sqlQueryStatExecute(sqlquerystat);
		try  {     
			rs_gn.last();  
	        int rows = rs_gn.getRow();  
	        rs_gn.first();  
	        int k = 0;  
	        //while(rs.next())
	        for(int j=0;j<rows;j++) { 
	        	tmpsysbk.add( rs_gn.getString("概念板块") );
	        	rs_gn.next();
	        } 
	        rs_gn.close();
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    }catch(Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	
	    }
		
		sqlquerystat=  "SELECT 自定义板块 FROM 股票通达信自定义板块对应表  WHERE 股票代码= "
						+ "'" +  stockcode.trim() + "'"
						;
		//System.out.println(sqlquerystat);
		CachedRowSetImpl rs_zdy = connectdb.sqlQueryStatExecute(sqlquerystat);
		HashSet<String> tmpzdybk = new HashSet<String> ();
		try  {     
			rs_zdy.last();  
	        int rows = rs_zdy.getRow();  
	        rs_zdy.first();  
	        int k = 0;  
	        //while(rs.next())
	        for(int j=0;j<rows;j++) { 
	        	tmpzdybk.add( rs_zdy.getString("自定义板块") );
	        	rs_zdy.next();
	        } 
	        rs_zdy.close();
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
		
		stockbasicinfo.setSuoShuTDXSysBanKuai(tmpsysbk);
		stockbasicinfo.setSuoShuTDXZdyBanKuai(tmpzdybk);
		
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



}
