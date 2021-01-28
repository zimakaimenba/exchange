package com.exchangeinfomanager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.License.EncryptAndDecypt;
import com.exchangeinfomanager.systemconfigration.CurDataBase;
import com.google.common.base.Splitter;
import com.sun.rowset.CachedRowSetImpl;

public class DataBaseConnection 
{
	public DataBaseConnection (CurDataBase curdb)
	{
		if(this.isDatabaseconnected())
			try { con.close();
			} catch (SQLException e1) {e1.printStackTrace();}
		
		con =  setupDataBaseConnection (curdb);
		
		if(con != null) {
			databasetype = curdb.getCurDatabaseType().toLowerCase();
			databasename = setDatabaseName (curdb);
		}
	}
	
	private static Logger logger = Logger.getLogger(DataBaseConnection.class.getName());
	private Connection con;
	private boolean databaseconnected = false;
	private String databasetype;
	private String databasename;
	//private SystemConfigration sysconfig ;
	
	public boolean isDatabaseconnected ()
	{
		return databaseconnected;
	}
	public Connection getDbConnection ()
	{
		return this.con;
	}
	private Connection setupDataBaseConnection(CurDataBase curdb) 
	{
		String curdbtype = curdb.getCurDatabaseType().toUpperCase();
		Connection tmpcon = null;
		switch (curdbtype) {
		case "MYSQL" : tmpcon = setupMySqlDataBaseConnection(curdb);
						break;
		case "SQLITE" : tmpcon = setupSQLiteDataBaseConnection(curdb);
						break;
		}
		
		return tmpcon;
	}

	private Connection setupSQLiteDataBaseConnection (CurDataBase curdb)
	{
		Connection tmpcon = null;
		try {
	            // db parameters
//	            String url = "jdbc:sqlite:e:/stock/dzh365/data/B$/block_v2.db";
	            String url = curdb.getDataBaseConStr() ;
	            // create a connection to the database
	            tmpcon = DriverManager.getConnection(url);
	            
	            System.out.println("Connection to SQLite has been established.");
	     } catch (SQLException e) { System.out.println(e.getMessage()); } 
		 
		 return tmpcon;
	}
	
	private Connection setupMySqlDataBaseConnection (CurDataBase curdb)
	{
		 	Connection tmpcon = null;
		 	String dbconnectstr = curdb.getDataBaseConStr();
			String user = curdb.getUser();
			String password =  curdb.getPassWord();

			try	{
					Class.forName("com.mysql.jdbc.Driver");
					logger.info("成功加载MySQL驱动 for" + dbconnectstr);
			} catch(ClassNotFoundException e)	{	logger.error("找不到驱动程序类 ，加载驱动失败！");	e.printStackTrace(); }
			
			String urlToDababasecrypt = "jdbc:mysql://" +  dbconnectstr;  						// "jdbc:mysql://localhost:3306/stockinfomanagementtest" ;
			try	{	// 方式二 通过数据源与数据库建立连接
				EncryptAndDecypt encyptanddecypt = new EncryptAndDecypt ();
				String decryptedpassword = encyptanddecypt.getDecryptedPassowrd(password);
				encyptanddecypt = null;
				
				tmpcon = DriverManager.getConnection(urlToDababasecrypt,user,decryptedpassword);
				if(tmpcon != null)	{
					logger.info("Sucessed connect database:" + dbconnectstr );
					this.databaseconnected = true;
				}
			} catch(SQLException se) {	logger.error("Failed connect database:" + dbconnectstr );}
						
			return tmpcon;
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
	private String setDatabaseName(CurDataBase curdb) 
	{
		String curconstr = curdb.getDataBaseConStr();
		String dbtype = curdb.getCurDatabaseType();
		
		return dbtype + " " + curconstr; 
	}
	
	public boolean closeConnectedDb()
	{
		if(this.con != null )
			try {con.close();
			} catch (SQLException e1) {e1.printStackTrace();return false;}
		
		databaseconnected = false;
		return true;
	}
	
	public CachedRowSetImpl sqlQueryStatExecute(String sqlstatement)
	{
		//System.out.println(sqlstatement);
		ResultSet rsquery = null;
		CachedRowSetImpl cachedRS = null;
		PreparedStatement sqlstat = null;
		try	{
			sqlstat = con.prepareStatement(sqlstatement);
			rsquery = sqlstat.executeQuery();
			
			cachedRS = new CachedRowSetImpl();
			cachedRS.populate(rsquery);
		}  catch(java.lang.NullPointerException e) {
			logger.info("数据库连接为NULL");
//			System.out.println("数据库连接为NULL");
			e.printStackTrace();
		} catch(com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e1) {
			logger.info("与数据库的连接断开，需要重新连接");
//			System.out.println("与数据库的连接断开，需要重新连接");
			e1.printStackTrace();
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException e1) {
//			System.out.println("与数据库的连接断开，需要重新连接");
			logger.info("与数据库的连接断开，需要重新连接");
			e1.printStackTrace();
		}catch(Exception e) {
//			System.out.println("数据库SQL执行失败");
//			System.out.println("出错SQL是:" + sqlstatement );
			e.printStackTrace();
		}finally {
			if(rsquery != null)
				try { rsquery.close();rsquery = null;
				} catch (SQLException e1) { e1.printStackTrace(); }
			if(sqlstat != null)
				try { sqlstat.close(); sqlstat = null;
				} catch (SQLException e) { e.printStackTrace();}
		}
		return cachedRS;
	}
	
	
	public int sqlUpdateStatExecute(String sqlstatement) throws SQLException
	{
		int autoIncKeyFromApi = -1;
		ResultSet rs = null;
		Statement sqlstat = null;
		
		try	{
			sqlstat=con.createStatement();
			sqlstat.executeUpdate(sqlstatement,Statement.RETURN_GENERATED_KEYS);

			 rs = sqlstat.getGeneratedKeys();
			 
			 if (rs.next()) 
			      autoIncKeyFromApi = rs.getInt(1);
			    
			 if (autoIncKeyFromApi>0) logger.debug("更新数据库成功");
            
		} finally {
			if(rs!=null)
				try {rs.close();rs =null;
				} catch (SQLException e) {e.printStackTrace();}
			if(sqlstat != null)
				try {sqlstat.close();sqlstat = null;
				} catch (SQLException e) {e.printStackTrace();}
		}
		
		return autoIncKeyFromApi;
	}

	public int sqlInsertStatExecute(String sqlinsertstat) throws SQLException
	{
		int result =0;
		result = sqlUpdateStatExecute(sqlinsertstat);
		
		return result;
	}
	public int sqlDeleteStatExecute(String sqldeletestat) throws SQLException
	{
		int result =0;
		result = sqlUpdateStatExecute(sqldeletestat);
		
		return result;
	}
//END
}
