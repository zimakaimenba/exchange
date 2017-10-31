package com.exchangeinfomanager.database;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountPuTong;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountRongQuan;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountRongZi;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountXinYongPuTong;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.CashAccountBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.gui.AccountAndChiCangConfiguration;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sun.rowset.CachedRowSetImpl;

public class AccountDbOperation 
{
	public AccountDbOperation() 
	{
		initializeDb ();
		initialzieSysconf ();
		
	}
	private SystemConfigration sysconfig ;
	private ConnectDataBase connectdb;
	
	
	
	private void initializeDb() 
	{
		connectdb = ConnectDataBase.getInstance();
	}
	private void initialzieSysconf ()
	{
		sysconfig = SystemConfigration.getInstance();
	}
	/*
	 * ��Ʊ�ֲ�namelist
	 */
	public ArrayList<String> getStockChiCangList ()
	{
		ArrayList<String> stockchicang = new ArrayList<String> ();
		
		String sqlquerystat ="select czjl.��Ʊ���� , agu.`��Ʊ����`  from ������¼���� czjl, a�� agu\r\n" + 
							" where  �ֱֲ�־ = '1' and czjl.`��Ʊ����` = agu.`��Ʊ����`\r\n" + 
							"  group by ��Ʊ����"
							;
		
		CachedRowSetImpl rs = null; 
	    try {  
	    	 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	 while(rs.next()) {  
	            String stockcode  = rs.getString("��Ʊ����");
	            stockchicang.add(stockcode);
	        }
	        
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	if(rs != null)
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }

		
		return stockchicang;
	}
	/*
	 * �����ݿ��гֱֲ�ʶtrue�Ĺ�Ʊ�����¼��ȥ����¼�õ���ǰĳ��Ʊ�ĳֲ������ÿһ���˻���Ӧһ����Ʊ
	 */
	public Multimap<String, StockChiCangInfo> getStockChiCang() 
	{
		HashMap<String,String> sqlstatmap = new HashMap<String,String> (); 
		
		Multimap<String,StockChiCangInfo> acntchicangmap =  ArrayListMultimap.create();
		String sqlquerystat = null;
//		sqlquerystat = " SELECT t1.�����˺�,t1.��Ʊ����, t2.��Ʊ����,  "
//				+ " SUM(SWITCH(t1.����������־ = true, t1.��������, t1.����������־ = false, -t1.��������)) as ��������, "
//				+ " SUM(SWITCH(t1.����������־ = true, t1.�������, t1.����������־ = false, -t1.�������)) as �������  "
//				+ " FROM ������¼���� t1, A�� t2 "
//				+ " WHERE t1.��Ʊ���� = t2.��Ʊ����  AND �ֱֲ�־ = true "
//				+ " GROUP BY t1.��Ʊ����, t2.��Ʊ����, t1.�����˺� "
//				;
//		sqlstatmap.put("access", sqlquerystat);
		
		sqlquerystat = " SELECT t1.�����˺�,t1.��Ʊ����, t2.��Ʊ����,  "
				+ "	       SUM(CASE WHEN t1.����������־ = true then t1.�������� ELSE -t1.�������� END) AS ��������,"
				+ "	       SUM(CASE WHEN t1.����������־ = true then t1.������� ELSE -t1.������� END) AS ������� "
				+ " FROM ������¼���� t1, A�� t2 "
				+ " WHERE t1.��Ʊ���� = t2.��Ʊ����  AND �ֱֲ�־ = true "
				+ " GROUP BY t1.��Ʊ����, t2.��Ʊ����, t1.�����˺� "
				; 
		sqlstatmap.put("mysql", sqlquerystat);

		CachedRowSetImpl rs = null; 
	    try {  
	    	System.out.println(sqlquerystat);
	    	 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	        while(rs.next()) {  
	        	String acnt = rs.getString("�����˺�");
	            String stockcode = rs.getString("��Ʊ����");
	            String stockname = rs.getString("��Ʊ����");
	            Integer gushu = rs.getInt("��������");
	            Double chenben = rs.getDouble("�������");
	            
	            StockChiCangInfo tmpchicang = new StockChiCangInfo (stockcode,stockname,gushu,chenben); //(String chicangcode,String chicangname, int chicanggushu, double chicangchenben )
	            acntchicangmap.put(acnt, tmpchicang);
	        }
	        
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	if(rs != null)
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }

	    return acntchicangmap;
	}
	


	


	public int setZijingLiuShui(Date actiondate,String accountname, String actionqstype, boolean ischuru, double zhuanruxianjing,String actionshuoming) 
	{
		HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
		String sqlinsertstat = null;
		sqlinsertstat = "INSERT INTO �ʽ���ˮ(��������,�����˻�,��������,ת��ת��,�ʽ���,����˵��) values ("
				+ "\"" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(actiondate)  + "\"" + ","
				+ "'" + accountname.trim() + "'" + ","
				+ "'" + actionqstype.trim() + "'" + ","
				+ ischuru + ","
				+ zhuanruxianjing + ","
				+ "'" + actionshuoming.trim() + "'"  
				+ ")"
				;
		sqlstatmap.put("mysql", sqlinsertstat);
		
		int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlstatmap);
		
		return autoIncKeyFromApi;
	}

	public void updateCurrentXianjianToDb(String accountname, double currentxianjing) 
	{
		String updatetablename = getZhangHuTableName (accountname);
			
		String sqlupdatestat = "UPDATE " + updatetablename +  " SET " 
				+ "�����ֽ�=" + currentxianjing
				+ " WHERE �˻�����=" + "'" + accountname.trim() + "'" 
				;
		//System.out.println(sqlupdatestat);
		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		
	}


	public void updateRongziZhaiwuToDb(String accountname, double rongzizhaiwu) 
	{
		String sqlupdatestat = "UPDATE �˻������˻���Ϣ  SET " 
				+ "����ծ�����=" + rongzizhaiwu
				+ " WHERE �˻�����=" + "'" + accountname.trim() + "'" 
				;
		//System.out.println(sqlupdatestat);
		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
	}

	public void updateTotalUsedXianjianToDb(String accountname, double totoalusedxianjing) 
	{
		String updatetablename =  getZhangHuTableName (accountname);
			
		String sqlupdatestat = "UPDATE " + updatetablename +  " SET " 
				+ "�����ֽ�=" + totoalusedxianjing
				+ " WHERE �˻�����=" + "'" + accountname.trim() + "'" 
				;
		//System.out.println(sqlupdatestat);
		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		
	}

	public void updateHistoryprofitToDb(String accountname, Double historyprofit) 
	{
		String updatetablename =  getZhangHuTableName (accountname);

		String sqlupdatestat = "UPDATE " + updatetablename +  " SET " 
				+ "��ʷӯ��=" + historyprofit
				+ " WHERE �˻�����=" + "'" + accountname.trim() + "'" 
				;
		//System.out.println(sqlupdatestat);
		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		
	}


	public int setChicangKongcang(AccountInfoBasic accountPuTong, String stockcode) 
	{
		String stockaccount =  accountPuTong.getAccountName();
		String sqliupdatestat = "UPDATE ������¼���� SET "
				+"�ֱֲ�־=false"
				+ " WHERE �ֱֲ�־=true"
				+ " AND ��Ʊ����=" + stockcode
				+ " AND �����˺�=" + "'" + stockaccount + "'"
				;
		//System.out.println(sqliupdatestat);
		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqliupdatestat);
		return autoIncKeyFromApi;
	}

	 
	public HashMap<String,AccountPuTong> getPuTongSubAccounts()
	{
		HashMap<String,AccountPuTong> ptacntsmap = new HashMap<String,AccountPuTong> ();
		
		String sqlquerystat= "SELECT * FROM �˻���ͨ�˻���Ϣ";	
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);

		try {
			rs.last();  
	        int rows = rs.getRow();  
	        //data = new Object[rows+1][];    
	        int columnCount = 8;//����  
	        rs.first();  
	        
//	        double totoalkeyongxianjing=0, totoalusedxianjing=0, totallishiprofit=0;
	        //while(rs.next())
	        for(int j=0;j<rows;j++) {  
	        	Object[] row = new Object[columnCount]; 
	            row[0] = rs.getString("�˻�����"); 
	            row[1] = rs.getString("�˻�����");
	            row[2] = new Double(rs.getDouble("�����ֽ�"));
	            row[3] = new Double(rs.getDouble("�����ֽ�"));
	            row[4] = new Double(rs.getDouble("��ʷӯ��"));
	            row[5] = new Boolean(rs.getBoolean("����")); 
	            row[6] = rs.getBoolean("�������˻�");
	            row[7] = rs.getString("����ȯ��");

	            AccountPuTong  putongaccount = new AccountPuTong ((String)row[1]);
	            putongaccount.setAccountid((String)row[0]);
	            //putongaccount.setAccountname((String)row[1]);
	            putongaccount.setCash((double)row[2]);
	            putongaccount.setTotoalZiJingChenBen((double)row[3]);
	            putongaccount.setHistoryProfit((double)row[4]);
	            putongaccount.setJihuo((boolean)row[5]);
	            //putongaccount.setRongzirongquanputong((boolean)rs.getBoolean("��ͨ"));
	            //putongaccount.setCashAccountBasic(cashacnt);
	            putongaccount.setHasXinYongAccount( (Boolean)row[6]);
	            putongaccount.setSuoShuQuanShngYingHang (row[7].toString());

	            //������˺ŵ��յĹҵ�����Ϊ�����ֽ�Ҫ��ȥ�ҵ����ź���
	            CachedRowSetImpl rs_jiner = null;
	            try {
	            	HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
	            	String sqlquerystat2 = null;
	            	sqlquerystat2 = "SELECT ����������־ , SUM(�������) AS ���  FROM ������¼����   "
							+ " WHERE �����˺� =" +"'" + (String)row[1] +"'" 
							+ " AND �ҵ�=true "
							+ " AND ����>=Date () "
							+ " GROUP BY ����������־ , �������"   
							;
	            	sqlstatmap.put("access", sqlquerystat2);
	            	
	            	sqlquerystat2 = "SELECT ����������־ , SUM(�������) AS ���  FROM ������¼����   "
							+ " WHERE �����˺� =" +"'" + (String)row[1] +"'" 
							+ " AND �ҵ�=true "
							+ " AND ����>=curdate() "
							+ " GROUP BY ����������־ , �������"   
							;
	            	sqlstatmap.put("mysql", sqlquerystat2);

		    		rs_jiner = connectdb.sqlQueryStatExecute(sqlstatmap);
		    		while(rs_jiner.next()) {
		    			boolean mairumaichusign = rs_jiner.getBoolean("����������־");
		    			
		    			if(mairumaichusign) {
							try {
								putongaccount.setTotoalBuyGuaDanJinEr(rs_jiner.getDouble("���"));
							} catch (SQLException e2) {
								double curguadanjiner = 0.0;
				    			putongaccount.setTotoalBuyGuaDanJinEr(curguadanjiner);
				    	    	e2.printStackTrace();
							}
						} else {
							try {
								putongaccount.setTotoalSellGuaDanJinEr(rs_jiner.getDouble("���"));
							} catch (SQLException e2) {
								double curguadanjiner = 0.0;
				    			putongaccount.setTotoalSellGuaDanJinEr(curguadanjiner);
				    	    	e2.printStackTrace();
							}
						}
		    		}
	    		
	    		}   catch (SQLException e2) {
	    	    	e2.printStackTrace();
	    	    } finally {
	    	    	if(rs_jiner != null)
	    	    		rs_jiner.close ();
	    	    		rs_jiner = null;
	    	    }
	            
	            ptacntsmap.put((String)row[1], putongaccount);
            
	            rs.next();
	        }
		} catch(java.lang.NullPointerException e) { 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }  finally {
	    	if(rs != null)
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }
		
		return ptacntsmap;
		
	}

	public HashMap<String, AccountXinYongPuTong> getXinYongPuTongSubAccounts() 
	{
		HashMap<String,AccountXinYongPuTong> ptacntsmap = new HashMap<String,AccountXinYongPuTong> ();
		
		String sqlquerystat= "SELECT * FROM �˻�������ͨ�˻���Ϣ";	
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		
		try {
			rs.last();  
	        int rows = rs.getRow();  
	        //data = new Object[rows+1][];    
	        int columnCount = 7;//����  
	        rs.first();  
	        
	        double totoalkeyongxianjing=0, totoalusedxianjing=0, totallishiprofit=0;
	        //while(rs.next())
	        for(int j=0;j<rows;j++) {  
	        	Object[] row = new Object[columnCount]; 
	            row[0] = rs.getString("�˻�����"); 
	            row[1] = rs.getString("�˻�����");
	            row[2] = new Double(rs.getDouble("�����ֽ�"));
	            row[3] = new Double(rs.getDouble("�����ֽ�"));
	            row[4] = new Double(rs.getDouble("��ʷӯ��"));
	            row[5] = new Boolean(rs.getBoolean("����")); 
	            row[6] = rs.getString("����ȯ��");
	            

	            // data[j] = row;  
	            totoalkeyongxianjing = totoalkeyongxianjing + (double)row[2];
	            totoalusedxianjing = totoalusedxianjing + (double)row[3];
	            totallishiprofit = totallishiprofit + (double)row[4] ; 
	        
	            AccountXinYongPuTong  putongaccount = new AccountXinYongPuTong ((String)row[1]);
	            putongaccount.setAccountid((String)row[0]);
	            //putongaccount.setAccountname((String)row[1]);
	            putongaccount.setCash((double)row[2]);
	            putongaccount.setTotoalZiJingChenBen((double)row[3]);
	            putongaccount.setHistoryProfit((double)row[4]);
	            putongaccount.setJihuo((boolean)row[5]);
	            putongaccount.setSuoShuQuanShngYingHang (row[6].toString());
            
	            ptacntsmap.put((String)row[1], putongaccount);
//	            if(rs.getBoolean("����״̬")) {
//	            	SubAccountSetting tmpaccountinfo = new SubAccountSetting (rs.getString("�˻�����"),rs.getString("�˻�����"));
//	            	tmpaccountinfo.setAccountcurrentcurrency(rs.getDouble("�����ֽ�"));
//	            	tmpaccountinfo.setAccountusedcurrency(rs.getDouble("�����ֽ�"));
//	            	tmpaccountinfo.setAccountprofit(rs.getDouble("��ʷӯ��"));
//	            	stockaccoutsinfo.put(rs.getString("�˻�����"),tmpaccountinfo);
//	            	
//	            	if((boolean)row[6]) 
//	            		rzrqaccountsnamelist.add((String)row[1]);
//	            	else putongaccountsnamelist.add((String)row[1]); 
//	            }
	            
	            rs.next();
	        }
		} catch(java.lang.NullPointerException e) { 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }  finally {
	    	if(rs != null)
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }

	    
		//accounttableputong = data;
		return ptacntsmap;
	}

	public HashMap<String, AccountRongZi> getRongZiSubAccounts() 
	{
		HashMap<String,AccountRongZi> ptacntsmap = new HashMap<String,AccountRongZi> ();
		
		String sqlquerystat= "SELECT * FROM �˻������˻���Ϣ";	
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);

		
		try {
			rs.last();  
	        int rows = rs.getRow();  
	        //data = new Object[rows+1][];    
	        int columnCount = 8;//����  
	        rs.first();  
	        
	        //double totoalkeyongxianjing=0, totoalusedxianjing=0, lishiprofit=0;
	        for(int j=0;j<rows;j++) {  
	        	Object[] row = new Object[columnCount]; 
	            row[0] = rs.getString("�˻�����"); 
	            row[1] = rs.getString("�˻�����");
	            row[2] = rs.getDouble("�����ֽ�");
	            row[3] = rs.getDouble("�����ֽ�");
	            row[4] = rs.getDouble("����ծ�����");
	            row[5] = rs.getDouble("��ʷӯ��");
	            row[6] = rs.getBoolean("����"); 
	            row[7] = rs.getString("����ȯ��");
	            
	            //data[j] = row;
	            
	            AccountRongZi  rongziaccount = new AccountRongZi ((String)row[1]);
	            rongziaccount.setAccountid((String)row[0]);
	            //rongziaccount.setAccountname((String)row[1]);
	            rongziaccount.setCash((double)row[2]);
	            rongziaccount.setTotoalZiJingChenBen((double)row[3]);
	            rongziaccount.setRongziJieKuan(null,null,(double)row[4]);
	            rongziaccount.setHistoryProfit((double)row[5]);
	            rongziaccount.setJihuo((boolean)row[6]);
	            rongziaccount.setSuoShuQuanShngYingHang (row[7].toString());

//	            rzrqrongziaccountsnamelist.add((String)row[1]);
//	            rongziaccountslist.add(rongziaccount);
	            
	            ptacntsmap.put((String)row[1], rongziaccount);

	            rs.next();
	        }
		} catch(java.lang.NullPointerException e) { 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }   finally {
	    	if(rs != null)
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }

		return ptacntsmap;
	}

	public HashMap<String, AccountRongQuan> getRongQuanSubAccounts() 
	{
		HashMap<String,AccountRongQuan> ptacntsmap = new HashMap<String,AccountRongQuan> ();
		
		String sqlquerystat= "SELECT * FROM �˻���ȯ�˻���Ϣ";	
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		
		try {
			rs.last();  
	        int rows = rs.getRow();  
	        //data = new Object[rows+1][];    
	        int columnCount = 7;//����  
	        rs.first();  
	        
	        //double totoalkeyongxianjing=0, totoalusedxianjing=0, lishiprofit=0;
	        for(int j=0;j<rows;j++) {  
	        	Object[] row = new Object[columnCount]; 
	            row[0] = rs.getString("�˻�����"); 
	            row[1] = rs.getString("�˻�����");
	            row[2] = new Double(rs.getDouble("�����ֽ�"));
	            row[3] = new Integer(rs.getInt("��ȯ���"));
	            row[4] = new Double(rs.getDouble("��ʷӯ��"));
	            row[5] = new Boolean(rs.getBoolean("����")); 
	            row[6] = rs.getString("����ȯ��");

	            //data[j] = row; 
	            AccountRongQuan  rongquanaccount = new AccountRongQuan ((String)row[1]);
	            rongquanaccount.setAccountid((String)row[0]);
	            rongquanaccount.setCash((double)row[2]);
	            rongquanaccount.setRongquanyuer((int)row[3]);
	            rongquanaccount.setHistoryProfit((double)row[4]);
	            rongquanaccount.setJihuo((boolean)row[5]);
	            rongquanaccount.setSuoShuQuanShngYingHang (row[6].toString());
            
	            ptacntsmap.put((String)row[1], rongquanaccount);

	            rs.next();
	        }
		} catch(java.lang.NullPointerException e) { 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }   finally {
	    	if(rs != null)
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }

	    
		//accounttablerongquan = data;
		return ptacntsmap;
	}

	public HashMap<String, CashAccountBasic> getCashSubAccounts() 
	{
		HashMap<String,CashAccountBasic> ptacntsmap = new HashMap<String,CashAccountBasic> ();
		
		String sqlquerystat= "SELECT * FROM �˻��ֽ��˻���Ϣ";	
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		

		
		try {
			rs.last();  
	        int rows = rs.getRow();  
	        //data = new Object[rows+1][];    
	        int columnCount = 4;//����  
	        rs.first();  
	        
	        //double totoalkeyongxianjing=0, totoalusedxianjing=0, lishiprofit=0;
	        for(int j=0;j<rows;j++) {  
	        	Object[] row = new Object[columnCount]; 
	            row[0] = rs.getString("�˻�����"); 
	            row[1] = rs.getString("�˻�����");
	            row[2] = new Double(rs.getDouble("�����ֽ�"));
	            row[3] = new Boolean(rs.getBoolean("����")); 
	            //row[4] = rs.getString("�����˻�");

	            //data[j] = row; 
	            CashAccountBasic  subcashaccount = new CashAccountBasic ((String)row[1]);
	            subcashaccount.setAccountid ( (String) row[0] );
	            subcashaccount.setCash( (double)row[2] );
	            subcashaccount.setJihuo((boolean)row[3]);
	            
	            ptacntsmap.put((String)row[1], subcashaccount);

	            rs.next();
	        }

		} catch(java.lang.NullPointerException e) { 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }  finally {
	    	if(rs != null)
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }

		return ptacntsmap;
	}

	//�ѽ�����صĴ���������
	public int setGeguProft(BuyStockNumberPrice stocknumberpricepanel) 
	{
		String subaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		String stockcode = stocknumberpricepanel.getStockcode();
		double geguprofit = stocknumberpricepanel.getProfit();
		
		//ӯ��ʱ������ͽ���ʱ����ͬ�ᵼ����ʾʱ��������ң����԰�ӯ��ʱ���2���ӣ���ס��ʱ������
		java.util.Calendar Cal=java.util.Calendar.getInstance();
		Cal.setTime(stocknumberpricepanel.getActionDay());
		Cal.add(java.util.Calendar.SECOND,10);
		
		HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
		String sqlinsertstat  = null;
		sqlinsertstat = "INSERT INTO A�ɸ���ӯ��(��Ʊ����,����,ԭ������,ӯ�����,�����˺�) VALUES ("
				+ "'" +  stockcode.trim() + "'" + "," 
				+ "\"" + CommonUtility.formatDateYYYY_MM_DD_HHMMSS( Cal.getTime() )   + "\""   + "," 
				+ "'" + "(" + geguprofit + ")" + "'" + ","
				+ geguprofit + ","
				+ "'" + subaccount + "'" 
				+ ")"
				;
		sqlstatmap.put("mysql", sqlinsertstat);
		int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlstatmap);

		//�����������ݿ���profit��ID��
		String actiontable = this.getActionTableName (subaccount); 
		int maimaidatabaseid = stocknumberpricepanel.getDatabaseid();
		String sqliupdatestat = "UPDATE " + actiontable + " SET "
						+ " ӯ����ʶ="	+ autoIncKeyFromApi 
						+ " WHERE ID=" + maimaidatabaseid
						;
		//System.out.println(sqliupdatestat);
		connectdb.sqlUpdateStatExecute(sqliupdatestat);
		
		return autoIncKeyFromApi;
		
	}
	
	public BuyStockNumberPrice setGuadanExchangeFinishedActions(AccountInfoBasic tmpaccount, int databaseid) 
	{
		String actionaccountname = tmpaccount.getAccountName();
		String actiontable = this.getActionTableName (actionaccountname);
		
		//��ԭ��ʽ��׵���Ϣ
		String sqlquerystat = "SELECT *  FROM " + actiontable 
				+ " WHERE ID=" + databaseid 
				;
		//System.out.println(sqlquerystat);
		
		BuyStockNumberPrice stocknumberpricepanel = null;
		String shuoming = null;
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		try {
			rs.first();
			Date actionday = rs.getDate("����");
			String stockcode = rs.getString("��Ʊ����").trim();
			int gushu = rs.getInt("��������");
			double chengben = rs.getDouble("�������");
			boolean maimaisign = rs.getBoolean("����������־");
			String actionacnt = rs.getString("�����˺�").trim();

			stocknumberpricepanel = new BuyStockNumberPrice(stockcode,null,maimaisign);
			stocknumberpricepanel.setActionDay(actionday);
			stocknumberpricepanel.setJiaoYiGuShu(gushu);
			stocknumberpricepanel.setJiaoYiJiaGe(chengben/gushu);
			stocknumberpricepanel.setJiaoyiZhanghu(actionacnt);
			
			//����˵�����湲���ٹɲ���
			shuoming = rs.getString("ԭ������");
			
			String replacement = null;
			int curgushu = tmpaccount.getChiCangGuPiaoGuShu(stockcode);
			if(maimaisign)
				replacement = "��" +(curgushu+gushu)+ "��";
			else replacement = "��" +(curgushu-gushu)+ "��";
			
			List<String> tmpshuolist = Splitter.on("|").omitEmptyStrings ().splitToList(shuoming); //[(100 * ��10.00=-1000.0), ��200��]
			if(tmpshuolist.size() == 3) {
				shuoming = tmpshuolist.get(0)+ "|" + replacement + "|" + tmpshuolist.get(2);
			} 
			else 				
				shuoming = tmpshuolist.get(0)+ "|" + replacement + "|" ;

			stocknumberpricepanel.setJiaoYiShuoMing(shuoming);
		} catch (SQLException e) {
				e.printStackTrace();
		} catch(Exception e){
				e.printStackTrace();
		}  finally {
	    	if(rs != null)
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }

		
		//������ʽ���
		String sqliupdatestat = "UPDATE " + actiontable + " SET "
				+ " �ֱֲ�־=true "
				+ "  ,�ҵ�=false"
				+ " ,ԭ������=" + "\"" + shuoming + "\""
				+ " WHERE ID=" + databaseid
				;
		//System.out.println(sqliupdatestat);
		@SuppressWarnings("unused")
		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqliupdatestat);

			
		return stocknumberpricepanel;
	}
	
	
	public int setSellExchangeRelatedActions(BuyStockNumberPrice stocknumberpricepanel) 
	{
		int stocknumber = stocknumberpricepanel.getJiaoyiGushu();
		double stockprice = stocknumberpricepanel.getJiaoyiJiage();
		Date actionday = stocknumberpricepanel.getActionDay();
		String stockcode = stocknumberpricepanel.getStockcode();
		String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		boolean guadan = stocknumberpricepanel.getGuadan();
		String shuoming = stocknumberpricepanel.getJiaoYiShuoMing();
		
		//AccountInfoBasic tmpactionacnt = accountschicangconfig.getAccount(actionstockaccount);
		
		int autoIncKeyFromApi = -1;
		if(!guadan) { 
			 autoIncKeyFromApi = setActionSellBuyToDb ( actionstockaccount,  actionday, stockcode,  stocknumber,  stockprice, shuoming, false);

		} else {
			 autoIncKeyFromApi = setActionSellBuyToDbGuaDan( actionstockaccount,  actionday, stockcode,  stocknumber,  stockprice, shuoming, false);
		}
		return autoIncKeyFromApi;
	}
	
	public int setBuyExchangeRelatedActions(BuyStockNumberPrice stocknumberpricepanel) 
	{
		
		int stocknumber = stocknumberpricepanel.getJiaoyiGushu();
		double stockprice = stocknumberpricepanel.getJiaoyiJiage();
		Date actionday = stocknumberpricepanel.getActionDay();
		String stockcode = stocknumberpricepanel.getStockcode();
		String actionstockaccount = stocknumberpricepanel.getJiaoyiZhanghu().trim();
		boolean guadan = stocknumberpricepanel.getGuadan();
		String shuoming = stocknumberpricepanel.getJiaoYiShuoMing();
		
		//AccountInfoBasic tmpactionacnt = accountschicangconfig.getAccount(actionstockaccount);
		
		int autoIncKeyFromApi = -1;
		if(!guadan) { //�˻�����
			 autoIncKeyFromApi = this.setActionSellBuyToDb ( actionstockaccount,  actionday, stockcode,  stocknumber,  stockprice, shuoming, true);
		}
		else {
			 autoIncKeyFromApi = this.setActionSellBuyToDbGuaDan( actionstockaccount,  actionday, stockcode,  stocknumber,  stockprice, shuoming, true);
		}

		return autoIncKeyFromApi;
		
	}
	
	private int setActionSellBuyToDbGuaDan(String actionaccountname, Date actionDay,String stockcode, int stocknumber, double actionprice, String shuoming,boolean actiontype)  
	{
		int autoIncKeyFromApi =-1;
		double tmpchengben = stocknumber*actionprice;
		//String actionaccountname = accountInfoBasic.getAccountname();
		String actiontable = this.getActionTableName (actionaccountname);
		
		//String buyresult = formateBuyResult(stocknumber,  actionprice, shuoming,actiontype);
		
		String sqlinsertstat = "INSERT INTO " + actiontable + "(��Ʊ����,����,����������־,ԭ������,�����˺�,�ֱֲ�־,��������,�������,�ҵ�) VALUES ("
				+ "'" +  stockcode.trim() + "'"  + ","
				+ "\"" + CommonUtility.formatDateYYYY_MM_DD_HHMMSS( actionDay )   + "\""   + ","
				+ actiontype + ","
				+ "\"" + shuoming + "\"" +"," 
				+ "'" + actionaccountname + "'" + ","  
				+ false + ","
				+ stocknumber + ","
				+ tmpchengben  + ","
				+ true
				+ ")"
				;
		//System.out.println(sqlinsertstat);  
		autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		
		return autoIncKeyFromApi;
	}
	
	private int setActionSellBuyToDb (String actionaccountname, Date actionday,String stockcode, int stocknumber, double actionprice,String shuoming,boolean actiontype) 
	{
		int autoIncKeyFromApi =-1;
		double tmpchengben = stocknumber*actionprice;
		//String actionaccount = accountInfoBasic.getAccountname();
		String actiontable = this.getActionTableName (actionaccountname);

		//String buyresult = this.formateBuyResult(stocknumber,  actionprice, shuoming,actiontype);

		boolean chicang = true;

		String sqlinsertstat = "INSERT INTO " + actiontable + "(��Ʊ����,����,����������־,ԭ������,�����˺�,�ֱֲ�־,��������,�������,�ҵ�) VALUES ("
				+ "'" +  stockcode.trim() + "'"  + ","
				+ "\"" + CommonUtility.formatDateYYYY_MM_DD_HHMMSS( actionday )   + "\""   + ","  
				+ actiontype + ","
				+ "\"" + shuoming + "\"" +"," 
				+ "'" + actionaccountname + "'" + ","  
				+ chicang + ","
				+ stocknumber + ","
				+ tmpchengben  + ","
				+ false
				+ ")"
				;
		//System.out.println(sqlinsertstat);  
		autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		
		return autoIncKeyFromApi;
	}

	private String getActionTableName(String actionstockaccount) 
	{
		String actiontable;
		if(actionstockaccount.contains("��ȯ"))
			actiontable = "������¼��ȯ����";
		else if(actionstockaccount.contains("����"))
			actiontable = "������¼��������";
		else
			actiontable = "������¼����";
		return actiontable.trim();
	}
	
	private String getZhangHuTableName(String accountname) 
	{
		String updatetablename;
		if(accountname.contains("����"))
			updatetablename = "�˻�������ͨ�˻���Ϣ";
		else if(accountname.contains("����"))
			updatetablename = "�˻������˻���Ϣ";
		else if(accountname.contains("��ȯ"))
			updatetablename = "�˻���ȯ�˻���Ϣ";
		else if(accountname.contains("�ֽ�"))
			updatetablename = "�˻��ֽ��˻���Ϣ";
		else
			updatetablename = "�˻���ͨ�˻���Ϣ";
		
		return  updatetablename;
	}
	
	public int updateNewRzrqAccountsToDb (String tmpactid, String tmpactname, boolean tmpselected,boolean tmprzrq,String tmprzrqid, String tmpsuoshuquanshang)
	{
		//update��ͨ�˻��Ĺ����˻���
				String sqlupdatestat = "UPDATE �˻���ͨ�˻���Ϣ  SET " 
						+ " �������˻�=true"     
						+ " WHERE �˻�����=" + "'" + tmpactname.trim() + "'" 
						;
				System.out.println(sqlupdatestat);
				int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
				
				this.setNewRzrqAccountsToDb (tmpactid,  tmpactname,  tmpselected, tmprzrq, tmprzrqid,tmpsuoshuquanshang);
				
				return 1;
				
	}
	public int setANewAccountToDb(String tmpactid, String tmpactname, boolean tmpselected,boolean tmprzrq,String tmprzrqid, String tmpactquanshang) 
	{
		String sqlinsertstat = "INSERT INTO �˻���ͨ�˻���Ϣ(�˻�����,�˻�����,��ͨ,�����ֽ�,�����ֽ�,��ʷӯ��,����ӯ����¼����,����,�������˻�,����ȯ��) values ("
				+ "'" + tmpactid.trim() + "'" + ","
				+ "'" + tmpactname.trim() + "'" + ","
				+ true + ","
				+ 0 + ","
				+ 0 + ","
				+ 0 + ","
				+ "\"" + CommonUtility.formatDateYYYY_MM_DD_HHMMSS( new Date() )   + "\""   + ","
				+ tmpselected  
				+ "," + tmprzrq 
				+ "," + "'" + tmpactquanshang + "'" 
				+ ")"
				;
		System.out.println(sqlinsertstat);
			int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
			if(autoIncKeyFromApi <0) return -1;
			
			String cashaccountname = tmpactname.trim() + "- �ֽ��˻�";
			sqlinsertstat = "INSERT INTO �˻��ֽ��˻���Ϣ(�˻�����,�˻�����,�����ֽ�,����, �˻�����ʱ��) values ("
					+ "'" + tmpactid.trim() + "'" + ","
					+ "'" + cashaccountname.trim() + "'" + ","
					+ 0 + ","
					+ tmpselected + ","
					//+ "'" + tmpactname.trim() + "'" + ","
					+ "\"" + CommonUtility.formatDateYYYY_MM_DD_HHMMSS( new Date() )   + "\""      
					+ ")"
					;
			System.out.println(sqlinsertstat);
			autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
			if(autoIncKeyFromApi <0) return -1;
		
		
		if (tmprzrq) {
			this.setNewRzrqAccountsToDb (tmpactid,  tmpactname,  tmpselected, tmprzrq, tmprzrqid,tmpactquanshang);
		}
		
		return 1;
	}
	
	private int setNewRzrqAccountsToDb(String tmpactid, String tmpactname, boolean tmpselected, boolean tmprzrq,String tmprzrqid,String tmpactquanshang) 
	{
		
		String subaccountname = tmpactname.trim() + "- ������ͨ���˻�";
		String sqlinsertstat = "INSERT INTO �˻�������ͨ�˻���Ϣ(�˻�����,�˻�����,������ͨ,�����ֽ�,�����ֽ�,��ʷӯ��,����ӯ����¼����,����,����ȯ��) values ("
				+ "'" + tmprzrqid.trim() + "'" + ","
				+ "'" + subaccountname + "'" + ","
				+ true + ","
				+ 0 + ","
				+ 0 + ","
				+ 0 + ","
				+ "\"" + CommonUtility.formatDateYYYY_MM_DD_HHMMSS( new Date() )   + "\""   + ","
				+ tmpselected //+ ","
				+ "," + "'" + tmpactquanshang + "'"
				+ ")"
				;
		//System.out.println(sqlinsertstat);
		int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		if(autoIncKeyFromApi <0) return -1;
		
		subaccountname = tmpactname + "- �������˻�";
		sqlinsertstat = "INSERT INTO �˻������˻���Ϣ(�˻�����,�˻�����,��������,�����ֽ�,�����ֽ�,��ʷӯ��,����ծ�����,����ӯ����¼����,����,����ȯ��) values ("
				+ "'" + tmprzrqid.trim() + "'" + ","
				+ "'" + subaccountname + "'" + ","
				+ true + ","
				+ 0 + ","
				+ 0 + ","
				+ 0 + ","
				+ 0 + ","
				+ "\"" + CommonUtility.formatDateYYYY_MM_DD_HHMMSS( new Date() )   + "\""   + ","
				+ true //+ ","
				+ "," + "'" + tmpactquanshang + "'" 
				+ ")"
				;
		//System.out.println(sqlinsertstat);
		autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		if(autoIncKeyFromApi <0) return -1;
		
		subaccountname = tmpactname + "- ��ȯ���˻�";
		sqlinsertstat = "INSERT INTO �˻���ȯ�˻���Ϣ(�˻�����,�˻�����,�����ֽ�,�����ֽ�,��ʷӯ��,��ȯ���,����ӯ����¼����,������ȯ,����,����ȯ��) values ("
				+ "'" + tmprzrqid.trim() + "'" + ","
				+ "'" + subaccountname + "'" + ","
				+ 0 + ","
				+ 0 + ","
				+ 0 + ","
				+ 0 + ","
				+ "\"" + CommonUtility.formatDateYYYY_MM_DD_HHMMSS( new Date() )   + "\""   + ","
				+ true + ","
				+ true 
				+ "," + "'" + tmpactquanshang + "'"
				+ ")"
				;
		//System.out.println(sqlinsertstat);
		autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		if(autoIncKeyFromApi <0) return -1;
		

		
		return 1;
	}

	/*
	 * ���潻���˻���Ϣ
	 */
	public void setAccountInfoToDb(CashAccountBasic oneacnt) 
	{
		String acntname = oneacnt.getAccountName();
		String acnttable = this.getZhangHuTableName(acntname);
		
		double currentxianjing = oneacnt.getCurrentCash();
		double currentusedxianjing = oneacnt.getTotalZiJingChenBen();
		double currentprofit = oneacnt.getHistoryProfit();
		
		String sqlupdatestat = "UPDATE " + acnttable + " SET " 
				+ " �����ֽ�=" + "'" + currentxianjing    + "'" + ","
				+ " �����ֽ�=" + "'" + currentusedxianjing    + "'" + ","
				+ " ��ʷӯ��=" + "'" + currentprofit    + "'"
				+ " WHERE �˻�����=" + "'" + acntname.trim() + "'" 
				;
		System.out.println(sqlupdatestat);
		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
	}
	/*
	 * �����ֽ��˻����ֽ��
	 */
	public int setCashAccountXianjing(CashAccountBasic cashacnt) 
	{
		String actionaccountname = cashacnt.getAccountName();
		double currentcash = cashacnt.getCurrentCash();
		double curusedcash = cashacnt.getTotalZiJingChenBen();
		
		String sqliupdatestat = "UPDATE �˻��ֽ��˻���Ϣ SET "
				+" �����ֽ�=" + currentcash + ","
				+" �����ֽ�=" + curusedcash 
				+ " WHERE �˻�����=" + "'" + actionaccountname + "'"
				;
		System.out.println(sqliupdatestat);
		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqliupdatestat);
		
		return autoIncKeyFromApi;
		
	}


	public BuyStockNumberPrice changeAcntToNewAcnt(String tmpcuraccount, int databaseid, String tmpnewaccount) 
	{
		//�����Ǵ��ڲ�ͬ���������������Ҫ�Ѿɱ��±��ҳ���
		String actionoldtable = this.getActionTableName (tmpcuraccount);
		String actionnewtable = this.getActionTableName (tmpnewaccount);
		
		//��ԭ��ʽ��׵���Ϣ
		String sqlquerystat = "SELECT *  FROM " + actionoldtable 
				+ " WHERE ID=" + databaseid 
				;
		//System.out.println(sqlquerystat);

		BuyStockNumberPrice stocknumberpricepanel = null; 
		int gushu = 0;
		double chengben = 0.0;
		boolean maimaisign = false;
		String shuoming = null;
		//String actionacnt = null;
		boolean guadan = false;
		Date jyriqi = null;
		
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		try {
			rs.first();
			String stockcode = rs.getString("��Ʊ����").trim();
			 gushu = rs.getInt("��������");
			 chengben = rs.getDouble("�������");
			 maimaisign = rs.getBoolean("����������־");
			 shuoming = rs.getString("ԭ������");
			 //actionacnt = rs.getString("�����˺�").trim();
			 guadan = rs.getBoolean("�ҵ�");
			 jyriqi = rs.getDate("����");
		
			stocknumberpricepanel = new BuyStockNumberPrice(stockcode,null,maimaisign);
			stocknumberpricepanel.setJiaoYiGuShu(gushu);
			stocknumberpricepanel.setJiaoYiJiaGe(chengben/gushu);
			stocknumberpricepanel.setJiaoYiShuoMing (shuoming);
			stocknumberpricepanel.setGuadan(guadan);
			stocknumberpricepanel.setActionDay(jyriqi);
			stocknumberpricepanel.setJiaoyiZhanghu(tmpnewaccount); //�ɱ�ֻҪ�����ݿ�ID�ɿ���ɾ�����µ�����/����Ҫ�µ��˻�ID
			stocknumberpricepanel.setDatabaseid(databaseid);
		} catch (SQLException e) {
				e.printStackTrace();
		} catch(Exception e){
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
		
		if(actionoldtable.equals(actionnewtable) ) { //ͬ��ֻҪupdate
			String sqliupdatestat = "UPDATE " + actionoldtable + " SET "
					+ " �����˺�=" + "'" + tmpnewaccount + "' "
					+ " WHERE ID=" + databaseid
					;
			//System.out.println(sqliupdatestat);
			@SuppressWarnings("unused")
			int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqliupdatestat);
			
		} else { //�����Ҫɾ�� + insert
			String sqldelstat = "DELETE  FROM " + actionoldtable
								+ " WHERE ID=" + databaseid
								;
			int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldelstat);
			
			if(maimaisign)
				autoIncKeyFromApi = this.setBuyExchangeRelatedActions( stocknumberpricepanel  );
			else
				autoIncKeyFromApi = this.setSellExchangeRelatedActions( stocknumberpricepanel );
			stocknumberpricepanel.setDatabaseid(autoIncKeyFromApi);
		}
			
		return stocknumberpricepanel;
	}

//	private String formateDateForDiffDatabase (String dbdate)
//	{
//		if(dbdate != null) {
//			switch(databasetype) {
//			case "access": return "#" + dbdate + "#";
//							
//			case "mysql":  return "\"" + dbdate + "\"";
//							
//			}
//		} else
//			return null;
//		return null;
//	}


}
