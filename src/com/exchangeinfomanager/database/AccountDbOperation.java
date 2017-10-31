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
	 * 股票持仓namelist
	 */
	public ArrayList<String> getStockChiCangList ()
	{
		ArrayList<String> stockchicang = new ArrayList<String> ();
		
		String sqlquerystat ="select czjl.股票代码 , agu.`股票名称`  from 操作记录买卖 czjl, a股 agu\r\n" + 
							" where  持仓标志 = '1' and czjl.`股票代码` = agu.`股票代码`\r\n" + 
							"  group by 股票代码"
							;
		
		CachedRowSetImpl rs = null; 
	    try {  
	    	 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	 while(rs.next()) {  
	            String stockcode  = rs.getString("股票代码");
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
	 * 把数据库中持仓标识true的股票的买记录减去卖记录得到当前某股票的持仓情况，每一个账户对应一个股票
	 */
	public Multimap<String, StockChiCangInfo> getStockChiCang() 
	{
		HashMap<String,String> sqlstatmap = new HashMap<String,String> (); 
		
		Multimap<String,StockChiCangInfo> acntchicangmap =  ArrayListMultimap.create();
		String sqlquerystat = null;
//		sqlquerystat = " SELECT t1.买卖账号,t1.股票代码, t2.股票名称,  "
//				+ " SUM(SWITCH(t1.买入卖出标志 = true, t1.买卖股数, t1.买入卖出标志 = false, -t1.买卖股数)) as 买卖股数, "
//				+ " SUM(SWITCH(t1.买入卖出标志 = true, t1.买卖金额, t1.买入卖出标志 = false, -t1.买卖金额)) as 买卖金额  "
//				+ " FROM 操作记录买卖 t1, A股 t2 "
//				+ " WHERE t1.股票代码 = t2.股票代码  AND 持仓标志 = true "
//				+ " GROUP BY t1.股票代码, t2.股票名称, t1.买卖账号 "
//				;
//		sqlstatmap.put("access", sqlquerystat);
		
		sqlquerystat = " SELECT t1.买卖账号,t1.股票代码, t2.股票名称,  "
				+ "	       SUM(CASE WHEN t1.买入卖出标志 = true then t1.买卖股数 ELSE -t1.买卖股数 END) AS 买卖股数,"
				+ "	       SUM(CASE WHEN t1.买入卖出标志 = true then t1.买卖金额 ELSE -t1.买卖金额 END) AS 买卖金额 "
				+ " FROM 操作记录买卖 t1, A股 t2 "
				+ " WHERE t1.股票代码 = t2.股票代码  AND 持仓标志 = true "
				+ " GROUP BY t1.股票代码, t2.股票名称, t1.买卖账号 "
				; 
		sqlstatmap.put("mysql", sqlquerystat);

		CachedRowSetImpl rs = null; 
	    try {  
	    	System.out.println(sqlquerystat);
	    	 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	        while(rs.next()) {  
	        	String acnt = rs.getString("买卖账号");
	            String stockcode = rs.getString("股票代码");
	            String stockname = rs.getString("股票名称");
	            Integer gushu = rs.getInt("买卖股数");
	            Double chenben = rs.getDouble("买卖金额");
	            
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
		sqlinsertstat = "INSERT INTO 资金流水(操作日期,操作账户,操作类型,转入转出,资金金额,操作说明) values ("
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
				+ "可用现金=" + currentxianjing
				+ " WHERE 账户名称=" + "'" + accountname.trim() + "'" 
				;
		//System.out.println(sqlupdatestat);
		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		
	}


	public void updateRongziZhaiwuToDb(String accountname, double rongzizhaiwu) 
	{
		String sqlupdatestat = "UPDATE 账户融资账户信息  SET " 
				+ "融资债务余额=" + rongzizhaiwu
				+ " WHERE 账户名称=" + "'" + accountname.trim() + "'" 
				;
		//System.out.println(sqlupdatestat);
		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
	}

	public void updateTotalUsedXianjianToDb(String accountname, double totoalusedxianjing) 
	{
		String updatetablename =  getZhangHuTableName (accountname);
			
		String sqlupdatestat = "UPDATE " + updatetablename +  " SET " 
				+ "已用现金=" + totoalusedxianjing
				+ " WHERE 账户名称=" + "'" + accountname.trim() + "'" 
				;
		//System.out.println(sqlupdatestat);
		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		
	}

	public void updateHistoryprofitToDb(String accountname, Double historyprofit) 
	{
		String updatetablename =  getZhangHuTableName (accountname);

		String sqlupdatestat = "UPDATE " + updatetablename +  " SET " 
				+ "历史盈亏=" + historyprofit
				+ " WHERE 账户名称=" + "'" + accountname.trim() + "'" 
				;
		//System.out.println(sqlupdatestat);
		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		
	}


	public int setChicangKongcang(AccountInfoBasic accountPuTong, String stockcode) 
	{
		String stockaccount =  accountPuTong.getAccountName();
		String sqliupdatestat = "UPDATE 操作记录买卖 SET "
				+"持仓标志=false"
				+ " WHERE 持仓标志=true"
				+ " AND 股票代码=" + stockcode
				+ " AND 买卖账号=" + "'" + stockaccount + "'"
				;
		//System.out.println(sqliupdatestat);
		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqliupdatestat);
		return autoIncKeyFromApi;
	}

	 
	public HashMap<String,AccountPuTong> getPuTongSubAccounts()
	{
		HashMap<String,AccountPuTong> ptacntsmap = new HashMap<String,AccountPuTong> ();
		
		String sqlquerystat= "SELECT * FROM 账户普通账户信息";	
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);

		try {
			rs.last();  
	        int rows = rs.getRow();  
	        //data = new Object[rows+1][];    
	        int columnCount = 8;//列数  
	        rs.first();  
	        
//	        double totoalkeyongxianjing=0, totoalusedxianjing=0, totallishiprofit=0;
	        //while(rs.next())
	        for(int j=0;j<rows;j++) {  
	        	Object[] row = new Object[columnCount]; 
	            row[0] = rs.getString("账户代码"); 
	            row[1] = rs.getString("账户名称");
	            row[2] = new Double(rs.getDouble("可用现金"));
	            row[3] = new Double(rs.getDouble("已用现金"));
	            row[4] = new Double(rs.getDouble("历史盈亏"));
	            row[5] = new Boolean(rs.getBoolean("激活")); 
	            row[6] = rs.getBoolean("有信用账户");
	            row[7] = rs.getString("所属券商");

	            AccountPuTong  putongaccount = new AccountPuTong ((String)row[1]);
	            putongaccount.setAccountid((String)row[0]);
	            //putongaccount.setAccountname((String)row[1]);
	            putongaccount.setCash((double)row[2]);
	            putongaccount.setTotoalZiJingChenBen((double)row[3]);
	            putongaccount.setHistoryProfit((double)row[4]);
	            putongaccount.setJihuo((boolean)row[5]);
	            //putongaccount.setRongzirongquanputong((boolean)rs.getBoolean("普通"));
	            //putongaccount.setCashAccountBasic(cashacnt);
	            putongaccount.setHasXinYongAccount( (Boolean)row[6]);
	            putongaccount.setSuoShuQuanShngYingHang (row[7].toString());

	            //计算该账号当日的挂单金额，因为可用现金要减去挂单金额才合理
	            CachedRowSetImpl rs_jiner = null;
	            try {
	            	HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
	            	String sqlquerystat2 = null;
	            	sqlquerystat2 = "SELECT 买入卖出标志 , SUM(买卖金额) AS 金额  FROM 操作记录买卖   "
							+ " WHERE 买卖账号 =" +"'" + (String)row[1] +"'" 
							+ " AND 挂单=true "
							+ " AND 日期>=Date () "
							+ " GROUP BY 买入卖出标志 , 买卖金额"   
							;
	            	sqlstatmap.put("access", sqlquerystat2);
	            	
	            	sqlquerystat2 = "SELECT 买入卖出标志 , SUM(买卖金额) AS 金额  FROM 操作记录买卖   "
							+ " WHERE 买卖账号 =" +"'" + (String)row[1] +"'" 
							+ " AND 挂单=true "
							+ " AND 日期>=curdate() "
							+ " GROUP BY 买入卖出标志 , 买卖金额"   
							;
	            	sqlstatmap.put("mysql", sqlquerystat2);

		    		rs_jiner = connectdb.sqlQueryStatExecute(sqlstatmap);
		    		while(rs_jiner.next()) {
		    			boolean mairumaichusign = rs_jiner.getBoolean("买入卖出标志");
		    			
		    			if(mairumaichusign) {
							try {
								putongaccount.setTotoalBuyGuaDanJinEr(rs_jiner.getDouble("金额"));
							} catch (SQLException e2) {
								double curguadanjiner = 0.0;
				    			putongaccount.setTotoalBuyGuaDanJinEr(curguadanjiner);
				    	    	e2.printStackTrace();
							}
						} else {
							try {
								putongaccount.setTotoalSellGuaDanJinEr(rs_jiner.getDouble("金额"));
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
		
		String sqlquerystat= "SELECT * FROM 账户信用普通账户信息";	
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		
		try {
			rs.last();  
	        int rows = rs.getRow();  
	        //data = new Object[rows+1][];    
	        int columnCount = 7;//列数  
	        rs.first();  
	        
	        double totoalkeyongxianjing=0, totoalusedxianjing=0, totallishiprofit=0;
	        //while(rs.next())
	        for(int j=0;j<rows;j++) {  
	        	Object[] row = new Object[columnCount]; 
	            row[0] = rs.getString("账户代码"); 
	            row[1] = rs.getString("账户名称");
	            row[2] = new Double(rs.getDouble("可用现金"));
	            row[3] = new Double(rs.getDouble("已用现金"));
	            row[4] = new Double(rs.getDouble("历史盈亏"));
	            row[5] = new Boolean(rs.getBoolean("激活")); 
	            row[6] = rs.getString("所属券商");
	            

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
//	            if(rs.getBoolean("激活状态")) {
//	            	SubAccountSetting tmpaccountinfo = new SubAccountSetting (rs.getString("账户代码"),rs.getString("账户名称"));
//	            	tmpaccountinfo.setAccountcurrentcurrency(rs.getDouble("可用现金"));
//	            	tmpaccountinfo.setAccountusedcurrency(rs.getDouble("已用现金"));
//	            	tmpaccountinfo.setAccountprofit(rs.getDouble("历史盈亏"));
//	            	stockaccoutsinfo.put(rs.getString("账户名称"),tmpaccountinfo);
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
		
		String sqlquerystat= "SELECT * FROM 账户融资账户信息";	
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);

		
		try {
			rs.last();  
	        int rows = rs.getRow();  
	        //data = new Object[rows+1][];    
	        int columnCount = 8;//列数  
	        rs.first();  
	        
	        //double totoalkeyongxianjing=0, totoalusedxianjing=0, lishiprofit=0;
	        for(int j=0;j<rows;j++) {  
	        	Object[] row = new Object[columnCount]; 
	            row[0] = rs.getString("账户代码"); 
	            row[1] = rs.getString("账户名称");
	            row[2] = rs.getDouble("可用现金");
	            row[3] = rs.getDouble("已用现金");
	            row[4] = rs.getDouble("融资债务余额");
	            row[5] = rs.getDouble("历史盈亏");
	            row[6] = rs.getBoolean("激活"); 
	            row[7] = rs.getString("所属券商");
	            
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
		
		String sqlquerystat= "SELECT * FROM 账户融券账户信息";	
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		
		try {
			rs.last();  
	        int rows = rs.getRow();  
	        //data = new Object[rows+1][];    
	        int columnCount = 7;//列数  
	        rs.first();  
	        
	        //double totoalkeyongxianjing=0, totoalusedxianjing=0, lishiprofit=0;
	        for(int j=0;j<rows;j++) {  
	        	Object[] row = new Object[columnCount]; 
	            row[0] = rs.getString("账户代码"); 
	            row[1] = rs.getString("账户名称");
	            row[2] = new Double(rs.getDouble("可用现金"));
	            row[3] = new Integer(rs.getInt("融券余额"));
	            row[4] = new Double(rs.getDouble("历史盈亏"));
	            row[5] = new Boolean(rs.getBoolean("激活")); 
	            row[6] = rs.getString("所属券商");

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
		
		String sqlquerystat= "SELECT * FROM 账户现金账户信息";	
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		

		
		try {
			rs.last();  
	        int rows = rs.getRow();  
	        //data = new Object[rows+1][];    
	        int columnCount = 4;//列数  
	        rs.first();  
	        
	        //double totoalkeyongxianjing=0, totoalusedxianjing=0, lishiprofit=0;
	        for(int j=0;j<rows;j++) {  
	        	Object[] row = new Object[columnCount]; 
	            row[0] = rs.getString("账户代码"); 
	            row[1] = rs.getString("账户名称");
	            row[2] = new Double(rs.getDouble("可用现金"));
	            row[3] = new Boolean(rs.getBoolean("激活")); 
	            //row[4] = rs.getString("关联账户");

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

	//把交易相关的处理都放这里
	public int setGeguProft(BuyStockNumberPrice stocknumberpricepanel) 
	{
		String subaccount = stocknumberpricepanel.getJiaoyiZhanghu();
		String stockcode = stocknumberpricepanel.getStockcode();
		double geguprofit = stocknumberpricepanel.getProfit();
		
		//盈亏时间如果和交易时间相同会导致显示时候排序混乱，所以把盈利时间加2分钟，保住按时间排序
		java.util.Calendar Cal=java.util.Calendar.getInstance();
		Cal.setTime(stocknumberpricepanel.getActionDay());
		Cal.add(java.util.Calendar.SECOND,10);
		
		HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
		String sqlinsertstat  = null;
		sqlinsertstat = "INSERT INTO A股个股盈亏(股票代码,日期,原因描述,盈亏金额,操作账号) VALUES ("
				+ "'" +  stockcode.trim() + "'" + "," 
				+ "\"" + CommonUtility.formatDateYYYY_MM_DD_HHMMSS( Cal.getTime() )   + "\""   + "," 
				+ "'" + "(" + geguprofit + ")" + "'" + ","
				+ geguprofit + ","
				+ "'" + subaccount + "'" 
				+ ")"
				;
		sqlstatmap.put("mysql", sqlinsertstat);
		int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlstatmap);

		//更新买卖数据库中profit的ID，
		String actiontable = this.getActionTableName (subaccount); 
		int maimaidatabaseid = stocknumberpricepanel.getDatabaseid();
		String sqliupdatestat = "UPDATE " + actiontable + " SET "
						+ " 盈利标识="	+ autoIncKeyFromApi 
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
		
		//还原这笔交易的信息
		String sqlquerystat = "SELECT *  FROM " + actiontable 
				+ " WHERE ID=" + databaseid 
				;
		//System.out.println(sqlquerystat);
		
		BuyStockNumberPrice stocknumberpricepanel = null;
		String shuoming = null;
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		try {
			rs.first();
			Date actionday = rs.getDate("日期");
			String stockcode = rs.getString("股票代码").trim();
			int gushu = rs.getInt("买卖股数");
			double chengben = rs.getDouble("买卖金额");
			boolean maimaisign = rs.getBoolean("买入卖出标志");
			String actionacnt = rs.getString("买卖账号").trim();

			stocknumberpricepanel = new BuyStockNumberPrice(stockcode,null,maimaisign);
			stocknumberpricepanel.setActionDay(actionday);
			stocknumberpricepanel.setJiaoYiGuShu(gushu);
			stocknumberpricepanel.setJiaoYiJiaGe(chengben/gushu);
			stocknumberpricepanel.setJiaoyiZhanghu(actionacnt);
			
			//更新说明里面共多少股部分
			shuoming = rs.getString("原因描述");
			
			String replacement = null;
			int curgushu = tmpaccount.getChiCangGuPiaoGuShu(stockcode);
			if(maimaisign)
				replacement = "共" +(curgushu+gushu)+ "股";
			else replacement = "共" +(curgushu-gushu)+ "股";
			
			List<String> tmpshuolist = Splitter.on("|").omitEmptyStrings ().splitToList(shuoming); //[(100 * ￥10.00=-1000.0), 共200股]
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

		
		//更新这笔交易
		String sqliupdatestat = "UPDATE " + actiontable + " SET "
				+ " 持仓标志=true "
				+ "  ,挂单=false"
				+ " ,原因描述=" + "\"" + shuoming + "\""
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
		if(!guadan) { //账户处理
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
		
		String sqlinsertstat = "INSERT INTO " + actiontable + "(股票代码,日期,买入卖出标志,原因描述,买卖账号,持仓标志,买卖股数,买卖金额,挂单) VALUES ("
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

		String sqlinsertstat = "INSERT INTO " + actiontable + "(股票代码,日期,买入卖出标志,原因描述,买卖账号,持仓标志,买卖股数,买卖金额,挂单) VALUES ("
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
		if(actionstockaccount.contains("融券"))
			actiontable = "操作记录融券买卖";
		else if(actionstockaccount.contains("融资"))
			actiontable = "操作记录融资买卖";
		else
			actiontable = "操作记录买卖";
		return actiontable.trim();
	}
	
	private String getZhangHuTableName(String accountname) 
	{
		String updatetablename;
		if(accountname.contains("信用"))
			updatetablename = "账户信用普通账户信息";
		else if(accountname.contains("融资"))
			updatetablename = "账户融资账户信息";
		else if(accountname.contains("融券"))
			updatetablename = "账户融券账户信息";
		else if(accountname.contains("现金"))
			updatetablename = "账户现金账户信息";
		else
			updatetablename = "账户普通账户信息";
		
		return  updatetablename;
	}
	
	public int updateNewRzrqAccountsToDb (String tmpactid, String tmpactname, boolean tmpselected,boolean tmprzrq,String tmprzrqid, String tmpsuoshuquanshang)
	{
		//update普通账户的关联账户项
				String sqlupdatestat = "UPDATE 账户普通账户信息  SET " 
						+ " 有信用账户=true"     
						+ " WHERE 账户名称=" + "'" + tmpactname.trim() + "'" 
						;
				System.out.println(sqlupdatestat);
				int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
				
				this.setNewRzrqAccountsToDb (tmpactid,  tmpactname,  tmpselected, tmprzrq, tmprzrqid,tmpsuoshuquanshang);
				
				return 1;
				
	}
	public int setANewAccountToDb(String tmpactid, String tmpactname, boolean tmpselected,boolean tmprzrq,String tmprzrqid, String tmpactquanshang) 
	{
		String sqlinsertstat = "INSERT INTO 账户普通账户信息(账户代码,账户名称,普通,可用现金,已用现金,历史盈亏,最新盈亏记录日期,激活,有信用账户,所属券商) values ("
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
			
			String cashaccountname = tmpactname.trim() + "- 现金账户";
			sqlinsertstat = "INSERT INTO 账户现金账户信息(账户代码,账户名称,可用现金,激活, 账户创建时间) values ("
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
		
		String subaccountname = tmpactname.trim() + "- 信用普通子账户";
		String sqlinsertstat = "INSERT INTO 账户信用普通账户信息(账户代码,账户名称,信用普通,可用现金,已用现金,历史盈亏,最新盈亏记录日期,激活,所属券商) values ("
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
		
		subaccountname = tmpactname + "- 融资子账户";
		sqlinsertstat = "INSERT INTO 账户融资账户信息(账户代码,账户名称,信用融资,可用现金,已用现金,历史盈亏,融资债务余额,最新盈亏记录日期,激活,所属券商) values ("
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
		
		subaccountname = tmpactname + "- 融券子账户";
		sqlinsertstat = "INSERT INTO 账户融券账户信息(账户代码,账户名称,可用现金,已用现金,历史盈亏,融券余额,最新盈亏记录日期,信用融券,激活,所属券商) values ("
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
	 * 保存交易账户信息
	 */
	public void setAccountInfoToDb(CashAccountBasic oneacnt) 
	{
		String acntname = oneacnt.getAccountName();
		String acnttable = this.getZhangHuTableName(acntname);
		
		double currentxianjing = oneacnt.getCurrentCash();
		double currentusedxianjing = oneacnt.getTotalZiJingChenBen();
		double currentprofit = oneacnt.getHistoryProfit();
		
		String sqlupdatestat = "UPDATE " + acnttable + " SET " 
				+ " 可用现金=" + "'" + currentxianjing    + "'" + ","
				+ " 已用现金=" + "'" + currentusedxianjing    + "'" + ","
				+ " 历史盈亏=" + "'" + currentprofit    + "'"
				+ " WHERE 账户名称=" + "'" + acntname.trim() + "'" 
				;
		System.out.println(sqlupdatestat);
		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
	}
	/*
	 * 设置现金账户的现金额
	 */
	public int setCashAccountXianjing(CashAccountBasic cashacnt) 
	{
		String actionaccountname = cashacnt.getAccountName();
		double currentcash = cashacnt.getCurrentCash();
		double curusedcash = cashacnt.getTotalZiJingChenBen();
		
		String sqliupdatestat = "UPDATE 账户现金账户信息 SET "
				+" 可用现金=" + currentcash + ","
				+" 已用现金=" + curusedcash 
				+ " WHERE 账户名称=" + "'" + actionaccountname + "'"
				;
		System.out.println(sqliupdatestat);
		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqliupdatestat);
		
		return autoIncKeyFromApi;
		
	}


	public BuyStockNumberPrice changeAcntToNewAcnt(String tmpcuraccount, int databaseid, String tmpnewaccount) 
	{
		//可能是存在不同表里的买卖操作，要把旧表新表都找出来
		String actionoldtable = this.getActionTableName (tmpcuraccount);
		String actionnewtable = this.getActionTableName (tmpnewaccount);
		
		//还原这笔交易的信息
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
			String stockcode = rs.getString("股票代码").trim();
			 gushu = rs.getInt("买卖股数");
			 chengben = rs.getDouble("买卖金额");
			 maimaisign = rs.getBoolean("买入卖出标志");
			 shuoming = rs.getString("原因描述");
			 //actionacnt = rs.getString("买卖账号").trim();
			 guadan = rs.getBoolean("挂单");
			 jyriqi = rs.getDate("日期");
		
			stocknumberpricepanel = new BuyStockNumberPrice(stockcode,null,maimaisign);
			stocknumberpricepanel.setJiaoYiGuShu(gushu);
			stocknumberpricepanel.setJiaoYiJiaGe(chengben/gushu);
			stocknumberpricepanel.setJiaoYiShuoMing (shuoming);
			stocknumberpricepanel.setGuadan(guadan);
			stocknumberpricepanel.setActionDay(jyriqi);
			stocknumberpricepanel.setJiaoyiZhanghu(tmpnewaccount); //旧表只要有数据库ID旧可以删除，新的买入/出需要新的账户ID
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
		
		if(actionoldtable.equals(actionnewtable) ) { //同表只要update
			String sqliupdatestat = "UPDATE " + actionoldtable + " SET "
					+ " 买卖账号=" + "'" + tmpnewaccount + "' "
					+ " WHERE ID=" + databaseid
					;
			//System.out.println(sqliupdatestat);
			@SuppressWarnings("unused")
			int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqliupdatestat);
			
		} else { //异表需要删除 + insert
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
