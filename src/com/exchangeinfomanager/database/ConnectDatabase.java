package com.exchangeinfomanager.database;
import com.exchangeinfomanager.systemconfigration.*;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.sun.rowset.CachedRowSetImpl;

import java.sql.*;
import java.util.List;

import javax.sql.rowset.CachedRowSet;
import javax.swing.JOptionPane;

import org.bouncycastle.util.Strings;



public class ConnectDatabase 
{		
		SystemConfigration sysconfig = null;
		
		
		Connection con;

		boolean databaseconnected = false;
		boolean anewstockcode = false;
		private String databasetype;
		private String databasename;
		
		 private ConnectDatabase ()
		 {  
			 refreshDatabaseConnection();  
		 }  
		 // 单例实现  
		 public static ConnectDatabase getInstance ()
		 {  
		        return Singtonle.instance;  
		 }
		 
		 private static class Singtonle 
		 {  
		        private static ConnectDatabase instance =  new ConnectDatabase ();  
		 }
		   
		public void refreshDatabaseConnection ()
		{
			
			if(this.isDatabaseconnected())
				try {
					con.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			
			sysconfig = SystemConfigration.getInstance();
			CurDataBase curdb = sysconfig.getCurrentDataBase ();
			String dbconnectstr = curdb.getDataBaseConStr();
			String user = curdb.getUser();
			String password =  curdb.getPassWord();

			databasename = setDatabaseName (curdb);
			System.out.println(databasename);
			
			databasetype = curdb.getCurDatabaseType().toLowerCase();
			String urlToDababasecrypt = null;
			switch(databasetype) {
			case "access": 
						try	{
								Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
						} catch(ClassNotFoundException e)	{
							System.out.println("找不到驱动程序类 ，加载驱动失败！");
							e.printStackTrace();
						}

//jdbc:ucanaccess://C:/Users/wei.Jeff_AUAS/OneDrive/stock/2016/exchange info manager test.accdb;jackcessOpener=com.exchangeinfomanager.database.CryptCodecOpener
						urlToDababasecrypt = "jdbc:ucanaccess://" + dbconnectstr + ";jackcessOpener=com.exchangeinfomanager.database.CryptCodecOpener";
						System.out.println(urlToDababasecrypt);
						break;
			case "mysql":
						try	{
								Class.forName("com.mysql.jdbc.Driver");
								 System.out.println("成功加载MySQL驱动！");
							} catch(ClassNotFoundException e)	{
								System.out.println("找不到驱动程序类 ，加载驱动失败！");
								e.printStackTrace();
							}
						urlToDababasecrypt = "jdbc:mysql://" +  dbconnectstr;  						// "jdbc:mysql://localhost:3306/stockinfomanagementtest" ;
						break;
			case "sqlserver": //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
								break;
								
			}
			
			try	{	// 方式二 通过数据源与数据库建立连接
				System.out.println("begin to connect database");
				con = DriverManager.getConnection(urlToDababasecrypt,user,password);
//				con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
				if(con != null)	{
					System.out.println("Sucessed database");
					setDatabaseconnected(true);
				}
			} catch(SQLException se) {   
			    System.out.println("数据库连接失败，数据库可能未启动！");
			    
			    se.printStackTrace();
			    JOptionPane.showMessageDialog(null,"数据库连接失败，数据库可能未启动！，再见！", "警告",JOptionPane.INFORMATION_MESSAGE);  
				System.exit(0);
			}
						
		}
		
		/*
		 * 返回 数据库类型 加数据库连接字
		 */
		private String setDatabaseName(CurDataBase curdb) 
		{

			String curconstr = curdb.getDataBaseConStr();
			String dbtype = curdb.getCurDatabaseType();
			
			return dbtype + " " + curconstr; 
		}
		public boolean isAnewstockcode() 
		{
			return anewstockcode;
		}

		public void setAnewstockcoder(boolean anewstockcodeindicator) 
		{
			this.anewstockcode = anewstockcodeindicator;
		}
		
		
		private void setDatabaseconnected(boolean databaseconnected) 
		{
			this.databaseconnected = databaseconnected;
		}

		public boolean isDatabaseconnected() 
		{
			return databaseconnected;
		}

		public void closeConnectedDb()
		{
			  
				try	{
					if(con != null)
						con.close();
					databaseconnected = false;
				}catch(Exception ex) {
					System.out.println(ex);
				}
        }  
		
		public CachedRowSetImpl sqlQueryStatExecute(String sqlstatement)
		{
			//System.out.println(sqlstatement);
			ResultSet rsquery = null;
			CachedRowSetImpl cachedRS = null;
			PreparedStatement sqlstat = null;
			try	{
				//sqlstat=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
				sqlstat = con.prepareStatement(sqlstatement);
				rsquery = sqlstat.executeQuery();
				
//				ResultSetMetaData metaData = rsquery.getMetaData();
//				int count = metaData.getColumnCount();
//				for (int i = 1; i <= count; i++)
//				{
//				    System.out.println(metaData.getColumnName(i) );
//				}

				cachedRS = new CachedRowSetImpl();
				cachedRS.populate(rsquery);
				

			}  catch(net.ucanaccess.jdbc.UcanaccessSQLException ex) {
				ex.printStackTrace();
			}	catch(Exception e) {
				JOptionPane.showMessageDialog(null,"读取数据库失败");
				e.printStackTrace();
			} finally {
				if(rsquery != null)
					try {
						rsquery.close();
						rsquery = null;
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				if(sqlstat != null)
					try {
						sqlstat.close();
						sqlstat = null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
			return cachedRS;
		}
		
		
		public int sqlUpdateStatExecute(String sqlstatement)
		{
			int autoIncKeyFromApi = -1;
			ResultSet rs = null;
			Statement sqlstat = null;
			
			try	{
				sqlstat=con.createStatement();
				sqlstat.executeUpdate(sqlstatement,Statement.RETURN_GENERATED_KEYS);

				 rs = sqlstat.getGeneratedKeys();
				 if (rs.next()) {
				      autoIncKeyFromApi = rs.getInt(1);
				    }
				 if (autoIncKeyFromApi>0) {  
                    System.out.println("更新数据库成功");  
                }
			} catch(SQLException e)	{	
				autoIncKeyFromApi = 0;
				e.printStackTrace();  
			} finally {
				if(rs!=null)
					try {
						rs.close();
						rs =null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
				if(sqlstat != null)
					try {
						sqlstat.close();
						sqlstat = null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
			
			return autoIncKeyFromApi;
		}

		public int sqlInsertStatExecute(String sqlinsertstat) 
		{
			int result =0;
			result = sqlUpdateStatExecute(sqlinsertstat);
			
			return result;
			
		}
		public int sqlDeleteStatExecute(String sqldeletestat) 
		{
			int result =0;
			result = sqlUpdateStatExecute(sqldeletestat);
			
			return result;
			
		}
		public String getDatabaseType() {
			return databasetype;
		}
		
		public String getDatabaseName (String expectedname)
		{
			switch(expectedname.toLowerCase()) {
			case "full": return this.databasename;
			case "short" :  List<String> splitresult = Splitter.on('/').omitEmptyStrings().trimResults().splitToList(this.databasename);
							return splitresult.get(splitresult.size()-1);
			}
			
			return null;
		}
		
		

//END				
}
