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
		String sqlquerystat1= "SELECT ggyk.����, "
							+ "IIF(ggyk.ӯ�����>0,'ӯ��','����') AS ӯ�����,"
							+ " ggyk.ԭ������,"
							+ " ggyk.ID,"
							+ " ggyk.�����˺�,"
							+  "'����ӯ��'" 
							+ " FROM  A�ɸ���ӯ�� ggyk "
							+ " WHERE ggyk.��Ʊ���� =" + "'" + stockcode + "'" 
							
							+ " UNION ALL " 
							;
		//System.out.println(sqlquerystat1);
       String sqlquerystat2="SELECT czjl.����, " +		""
							+ "IIF( czjl.�������=0.0,'��ת��',IIF(czjl.�ҵ� = true,IIF(czjl.����������־, '�ҵ�����', '�ҵ�����'),IIF(czjl.����������־,'����','����') )  ) AS ����,"
							+ " czjl.ԭ������,"
							+ " czjl.ID,"
							+ " czjl.�����˺�,"
							+ "'������¼����'" 
							+ " FROM ������¼���� czjl "
							+ "	WHERE czjl.��Ʊ���� =" + "'" + stockcode + "'"
							
							+ " UNION ALL " 
							;
       //System.out.println(sqlquerystat2);
      String sqlquerystat3="SELECT rqczjl.����,"
							+ "IIF( rqczjl.�������=0.0,'��ת��',IIF(rqczjl.�ҵ� = true,IIF(rqczjl.����������־, '�ҵ�����', '�ҵ�����'),IIF(rqczjl.����������־,'����','����') )  ) AS ����,"
							+ "rqczjl.ԭ������,"
							+ "rqczjl.ID,"
							+ "rqczjl.�����˺�,"
							+ "'������¼��ȯ����'"
							+ " FROM ������¼��ȯ���� rqczjl"
							+ "	WHERE rqczjl.��Ʊ���� =" + "'" + stockcode + "'"
							
							+ " UNION  ALL "
							;
      //System.out.println(sqlquerystat3);
	 String sqlquerystat4="SELECT rzczjl.����,"
							+ "IIF( rzczjl.�������=0.0,'��ת��',IIF(rzczjl.�ҵ� = true,IIF(rzczjl.����������־, '�ҵ�����', '�ҵ�����'),IIF(rzczjl.����������־,'����','����') )  ) AS ����,"
							+ "rzczjl.ԭ������,"
							+ "rzczjl.ID,"
							+ "rzczjl.�����˺�,"
							+ "'������¼��������'"
							+ " FROM ������¼�������� rzczjl"
							+ " WHERE rzczjl.��Ʊ���� =" + "'" + stockcode + "'"
							
							+ " UNION ALL "
							;
	 //System.out.println(sqlquerystat4);
     String sqlquerystat5="SELECT zdgz.����,"
							+ "zdgz.�����Ƴ���־,"
							+ "zdgz.ԭ������,"
							+ "zdgz.ID,"
							+ "null,"
							+ "'������¼�ص��ע'"
							+ " FROM ������¼�ص��ע  zdgz"
							+ " WHERE zdgz.��Ʊ���� =" + "'" + stockcode + "'"
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
		String sqlquerystat1= "SELECT ggyk.����, "
							+ "IF(ggyk.ӯ�����>0,'ӯ��','����') AS ӯ�����,"
							+ " ggyk.ԭ������,"
							+ " ggyk.ID,"
							+ " ggyk.�����˺�,"
							+  "'����ӯ��'" 
							+ " FROM  A�ɸ���ӯ�� ggyk "
							+ " WHERE ggyk.��Ʊ���� =" + "'" + stockcode + "'" 
							
							+ " UNION ALL " 
							;
		//System.out.println(sqlquerystat1);
       String sqlquerystat2="SELECT czjl.����, " +		""
							+ "IF( czjl.�������=0,'��ת��',IF(czjl.�ҵ� = true,IF(czjl.����������־, '�ҵ�����', '�ҵ�����'),IF(czjl.����������־,'����','����') )  ) AS ����,"
							+ " czjl.ԭ������,"
							+ " czjl.ID,"
							+ " czjl.�����˺�,"
							+ "'������¼����'" 
							+ " FROM ������¼���� czjl "
							+ "	WHERE czjl.��Ʊ���� =" + "'" + stockcode + "'"
							
							+ " UNION ALL " 
							;
       //System.out.println(sqlquerystat2);
      String sqlquerystat3="SELECT rqczjl.����,"
							+ "IF( rqczjl.�������=0,'��ת��',IF(rqczjl.�ҵ� = true,IF(rqczjl.����������־, '�ҵ�����', '�ҵ�����'),IF(rqczjl.����������־,'����','����') )  ) AS ����,"
							+ "rqczjl.ԭ������,"
							+ "rqczjl.ID,"
							+ "rqczjl.�����˺�,"
							+ "'������¼��ȯ����'"
							+ " FROM ������¼��ȯ���� rqczjl"
							+ "	WHERE rqczjl.��Ʊ���� =" + "'" + stockcode + "'"
							
							+ " UNION  ALL "
							;
      //System.out.println(sqlquerystat3);
	 String sqlquerystat4="SELECT rzczjl.����,"
							+ "IF( rzczjl.�������=0,'��ת��',IF(rzczjl.�ҵ� = true,IF(rzczjl.����������־, '�ҵ�����', '�ҵ�����'),IF(rzczjl.����������־,'����','����') )  ) AS ����,"
							+ "rzczjl.ԭ������,"
							+ "rzczjl.ID,"
							+ "rzczjl.�����˺�,"
							+ "'������¼��������'"
							+ " FROM ������¼�������� rzczjl"
							+ " WHERE rzczjl.��Ʊ���� =" + "'" + stockcode + "'"
							
							+ " UNION ALL "
							;
	 //System.out.println(sqlquerystat4);
     String sqlquerystat5="SELECT zdgz.����,"
							+ "zdgz.�����Ƴ���־,"
							+ "zdgz.ԭ������,"
							+ "zdgz.ID,"
							+ "null,"
							+ "'������¼�ص��ע'"
							+ " FROM ������¼�ص��ע  zdgz"
							+ " WHERE zdgz.��Ʊ���� =" + "'" + stockcode + "'"
							+ " ORDER BY 1 DESC,5,3 " //desc
							;
     //System.out.println(sqlquerystat5);
	
     String sqlquerystat = sqlquerystat1 + sqlquerystat2 + sqlquerystat3 + sqlquerystat4 + sqlquerystat5;
     //System.out.println(sqlquerystat);
     
     return sqlquerystat;
	}

	/*
	 * ��ú͸ù�Ʊ��ص�������Ϣ
	 */
	private Object[][] setZdgzMrmcZdgzYingKuiRecords(CachedRowSetImpl rs) 
	{
		Object[][] data = null;  
	    try  {     
	        rs.last();  
	        int rows = rs.getRow();  
	        data = new Object[rows][];    
	        int columnCount = 6;//����  
	        rs.first();  
	        int k = 0;  
	        //while(rs.next())
	        for(int j=0;j<rows;j++) {   //{ "����", "����", "˵��","ID","�����˻�","��Ϣ��" };
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
		sqlquerystat= "SELECT * FROM ������¼�ص��ע   WHERE ����= date_sub(curdate(),interval 1 day) AND �����Ƴ���־ = '���ռƻ�' " ;
		sqlstatmap.put("mysql", sqlquerystat);
		
		sqlquerystat=  "SELECT * FROM ������¼�ص��ע "
				+ " WHERE ���� <  DATE() AND  ���� > IIF( Weekday( date() ) =  2,date()-3,date()-1)  "
				+ "AND �����Ƴ���־ = '���ռƻ�'"
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
	        	pmdresult = pmdresult + rs.getString("��Ʊ����") + "  " + rs.getString("ԭ������") + " ";
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
			 String sqlquerystat= "SELECT * FROM A��   WHERE ��Ʊ���� =" +"'" + stockcode +"'" ;	
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
			String stockname = rs.getString("��Ʊ����").trim();
			stockbasicinfo.setStockname(stockname);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setStockname( " ");
		} catch (Exception ex) {
			System.out.println(ex);
		}
		
		try 
		{
			Date gainiantishidate = rs.getDate("����ʱ��");
			stockbasicinfo.setGainiantishidate(gainiantishidate);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setGainiantishidate(null);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		
		try {
			String gainiantishi = rs.getString("����������");
			stockbasicinfo.setGainiantishi(gainiantishi);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setGainiantishi(" ");			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		
		try 
		{
			Date quanshangpingjidate = rs.getDate("ȯ������ʱ��");
			stockbasicinfo.setQuanshangpingjidate(quanshangpingjidate);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setQuanshangpingjidate(null);
		} catch (Exception e) {
			System.out.println(e);
		}
	
		try	{
			String quanshangpingji = rs.getString("ȯ����������").trim();
			stockbasicinfo.setQuanshangpingji(quanshangpingji);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setQuanshangpingji("");			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		try	{
			Date fumianxiaoxidate = rs.getDate("������Ϣʱ��");
			stockbasicinfo.setFumianxiaoxidate(fumianxiaoxidate);
		} catch(java.lang.NullPointerException ex1) { 
			stockbasicinfo.setFumianxiaoxidate(null);
		} catch(Exception ex2) {
			System.out.println(ex2);				
		}

		try	{
			String fumianxiaoxi = rs.getString("������Ϣ").trim();
			stockbasicinfo.setFumianxiaoxi(fumianxiaoxi);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setFumianxiaoxi("");			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		try {
			String zhengxiangguan = rs.getString("����ؼ��ͻ�");
			stockbasicinfo.setZhengxiangguan(zhengxiangguan);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setZhengxiangguan("");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	
		try {
			String fuxiangguan = rs.getString("����ؼ���������");
			stockbasicinfo.setFuxiangguan(fuxiangguan);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setFuxiangguan("");			
		} catch (Exception e) {
			System.out.println(e);
		}
	
		try {
			String keHuCustom = rs.getString("�ͻ�");
			stockbasicinfo.setKeHuCustom(keHuCustom);
		} catch(java.lang.NullPointerException ex1) {
			stockbasicinfo.setKeHuCustom("");			
		} catch (Exception e) {
			System.out.println(e);
		}
	
		try {
			String jingZhengDuiShou = rs.getString("��������");
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
			JOptionPane.showMessageDialog(null,"ConnectDatabase -> setANewDtockCodeIndicate ����Ԥ֪�Ĵ���");
			System.out.println(""+e);
		} 		
		
		return newStockSign;
	}

	public ASingleStockInfo getCheckListsXMLInfo(ASingleStockInfo stockbasicinfo)
	{
		String stockcode = stockbasicinfo.getStockcode();
		String sqlquerystat= "SELECT checklistsitems FROM A��   WHERE ��Ʊ���� =" +"'" + stockcode +"'" ;
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
			 String sqlupdatestat= "UPDATE A��  SET "
						+ " ��Ʊ����=" + stockname +","
						+ " ����ʱ��=" + formateDateForDiffDatabase("mysql", sysconfig.formatDate(stockbasicinfo.getGainiantishidate()) ) +","
						+ " ����������=" + txtareainputgainiants +","
						+ " ȯ������ʱ��=" + formateDateForDiffDatabase("mysql", sysconfig.formatDate(stockbasicinfo.getQuanshangpingjidate() ) ) +","
						+ " ȯ����������=" + txtfldinputquanshangpj +","
						+ " ������Ϣʱ��=" + formateDateForDiffDatabase("mysql", sysconfig.formatDate(stockbasicinfo.getFumianxiaoxidate()  ) ) +","
						+ " ������Ϣ=" + txtfldinputfumianxx +","
						+ " ����ؼ��ͻ�=" + txtfldinputzhengxiangguan +","
						+ " ����ؼ���������=" + txtfldinputfuxiangguan +","
						+ " �ͻ�=" + "'" + keHuCustom +"'" + ","
						+ " ��������=" + "'" + jingZhengDuiShou + "'"  
						+ " WHERE ��Ʊ����=" + stockcode
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
			 String sqlinsertstat = "INSERT INTO A��(��Ʊ����,��Ʊ����,����ʱ��,����������,ȯ������ʱ��,ȯ����������,������Ϣʱ��,������Ϣ,����ؼ��ͻ�,����ؼ���������) values ("
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
		String sqlupdatestat= "UPDATE A��  SET "
				+ "checklistsitems=" + "'" + checklistsitems + "'"
				+ "WHERE ��Ʊ����=" + stockcode
				
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
			zdgzsign = "���ռƻ�";
			shuoming = jiarujihua.getJiHuaLeiXing() + "(�۸�" + jiarujihua.getJiHuaJiaGe() + ")(" +  jiarujihua.getJiHuaShuoMing();
		} else
			shuoming =  jiarujihua.getJiHuaShuoMing();
		
		HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
		String sqlinsertstat = "INSERT INTO ������¼�ص��ע(��Ʊ����,����,�����Ƴ���־,ԭ������) values ("
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
		sqlquerystat=  "SELECT ������ FROM ��Ʊͨ���Ÿ������Ӧ��  WHERE ��Ʊ����= "
				+ "'" +  stockcode.trim() + "'"
				+ "UNION " 
				+ " SELECT ��ҵ��� FROM ��Ʊͨ������ҵ����Ӧ��  WHERE ��Ʊ����= "
				+ "'" +  stockcode.trim() + "'"
				+ "UNION " 
				+ " SELECT ����� FROM ��Ʊͨ���ŷ�����Ӧ��  WHERE ��Ʊ����= "
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
	        	tmpsysbk.add( rs_gn.getString("������") );
	        	rs_gn.next();
	        } 
	        rs_gn.close();
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    }catch(Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	
	    }
		
		sqlquerystat=  "SELECT �Զ����� FROM ��Ʊͨ�����Զ������Ӧ��  WHERE ��Ʊ����= "
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
	        	tmpzdybk.add( rs_zdy.getString("�Զ�����") );
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
		String sqlquerystat= "SELECT ��Ʊ���� FROM A��   WHERE ��Ʊ���� =" +"'" + stockname +"'" ;
		//System.out.println(sqlquerystat);
		CachedRowSetImpl rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);

		String stockcode = null;
		try {
			//rsagu.first();
			while (rsagu.next()) {
				stockcode = rsagu.getString("��Ʊ����");
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
		sqlquerystat= "SELECT ��Ʊ���� FROM A��   WHERE ������  LIKE" +"'%" + stockname +"%'" ;
		sqlstatmap.put("mysql", sqlquerystat);
		sqlquerystat= "SELECT ��Ʊ���� FROM A��   WHERE ������  LIKE" +"'*" + stockname +"*%'" ;
		sqlstatmap.put("access", sqlquerystat);

		CachedRowSetImpl rsagu = connectdb.sqlQueryStatExecute(sqlstatmap);

		String stockcode = "";
		try {
			//rsagu.first();
			while (rsagu.next()) 
				stockcode = rsagu.getString("��Ʊ����");
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
	 * ��ͨ�����е����Ʊ����������Ϣ
	 */
	public File refreshEverUsedStorkName()
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "ͬ��ͨ��������������.txt");
	
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
			 Files.append("��ʼ����ͨ���Ź�Ʊ��������Ϣ:" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
			 
			 String sqlupdatestat= "UPDATE A��  SET "
						+ " ������=" + "'" + cymstr + "'"
						+ " WHERE ��Ʊ����=" + stockcode
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
		 String sqlquerystat2=" SELECT czjl.��Ʊ����, " 
					+ " IF( czjl.�������=0.0,'��ת��',IF(czjl.�ҵ� = true,IF(czjl.����������־, '�ҵ�����', '�ҵ�����'),IF(czjl.����������־,'����','����') )  ) AS ����,"
					+ " czjl.ԭ������,"
					+ " czjl.ID,"
					+ " czjl.�����˺�,"
					+ "'������¼����'" 
					+ " FROM ������¼���� czjl "
					+ "	WHERE czjl.����>=" + "'" + searchdate + "'"
					
					+ " UNION ALL " 
					;
		System.out.println(sqlquerystat2);
		String sqlquerystat3=" SELECT rqczjl.��Ʊ����, "
							+ "IF( rqczjl.�������=0.0,'��ת��',IF(rqczjl.�ҵ� = true,IF(rqczjl.����������־, '�ҵ�����', '�ҵ�����'),IF(rqczjl.����������־,'����','����') )  ) AS ����,"
							+ "rqczjl.ԭ������,"
							+ "rqczjl.ID,"
							+ "rqczjl.�����˺�,"
							+ "'������¼��ȯ����'"
							+ " FROM ������¼��ȯ���� rqczjl"
							+ "	WHERE rqczjl.����>=" + "'" + searchdate + "'"
							
							+ " UNION  ALL "
							;
		System.out.println(sqlquerystat3);
		String sqlquerystat4=" SELECT rzczjl.��Ʊ����,"
							+ "IF( rzczjl.�������=0.0,'��ת��',IF(rzczjl.�ҵ� = true,IF(rzczjl.����������־, '�ҵ�����', '�ҵ�����'),IF(rzczjl.����������־,'����','����') )  ) AS ����,"
							+ "rzczjl.ԭ������,"
							+ "rzczjl.ID,"
							+ "rzczjl.�����˺�,"
							+ "'������¼��������'"
							+ " FROM ������¼�������� rzczjl"
							+ " WHERE rzczjl.����>=" + "'" + searchdate + "'"
							
							
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
		        int columnCount = 4;//����  
		        rs.first();  
		        int k = 0;  
		        //while(rs.next())
		        for(int j=0;j<rows;j++) {   //{ "����", "����", "˵��","ID","�����˻�","��Ϣ��" };
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
