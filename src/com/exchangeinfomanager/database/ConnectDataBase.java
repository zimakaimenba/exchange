package com.exchangeinfomanager.database;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.systemconfigration.*;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.sun.rowset.CachedRowSetImpl;

import java.sql.*;
import java.util.HashMap;
import java.util.List;

import javax.sql.rowset.CachedRowSet;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.bouncycastle.util.Strings;



public class ConnectDataBase 
{		
		 private ConnectDataBase ()
		 {  
			 refreshDatabaseConnection();  
		 }  
		 // ����ʵ��  
		 public static ConnectDataBase getInstance ()
		 {  
		        return Singtonle.instance;  
		 }
		 
		 private static class Singtonle 
		 {  
		        private static ConnectDataBase instance =  new ConnectDataBase ();  
		 }
		 
		 private static Logger logger = Logger.getLogger(ConnectDataBase.class);
		 DataBaseConnection localcon;
		 DataBaseConnection remotcon;
		 SystemConfigration sysconfig = null;
//		 String[] rmtservertable = {"��Ʊͨ���Ž�����ָ����Ӧ��","��Ʊͨ���Ÿ������Ӧ��","��Ʊͨ������ҵ����Ӧ��","��Ʊͨ���ŷ�����Ӧ��",
//				 					"ͨ���Ž�����ָ���б�","ͨ���Ž�����ָ��ÿ�ս�����Ϣ","ͨ���Ű���б�","ͨ���Ű��ÿ�ս�����Ϣ","��ҵ���Ӱ���б�"};	
////			Connection con;
//			Connection rmtcon;
//			boolean rmtdatabaseconnected = false;
//			//boolean databaseconnected = false;
//			private String databasetype;
//			private String databasename;
			

		public void refreshDatabaseConnection ()
		{
			sysconfig = SystemConfigration.getInstance();
			CurDataBase localcurdb = sysconfig.getCurrentDatabaseSource ();
			CurDataBase rmtcurdb = sysconfig.getRemoteCurrentDatabaseSource();
			
			localcon = new DataBaseConnection (localcurdb);
			String localconstr = localcurdb.getDataBaseConStr ().trim().toLowerCase();
			String rmtconstr = rmtcurdb.getDataBaseConStr().trim().toLowerCase();
			if(localconstr.equals(rmtconstr))
				remotcon = localcon;
			else 
				remotcon = new DataBaseConnection (rmtcurdb); 
			
			boolean localconnect = localcon.isDatabaseconnected();
			boolean rmtconnect = remotcon.isDatabaseconnected();
			if(localconnect == true && rmtconnect == true)
				sysconfig.setSoftWareMode (sysconfig.MODELSERVERCLIENT);
			else if(localconnect == false && rmtconnect == true) 
				sysconfig.setSoftWareMode (sysconfig.MODELSERVER);
			else if(localconnect == true && rmtconnect == false) 
				sysconfig.setSoftWareMode (sysconfig.MODELCLIENT);
			
		}
		
		
		public boolean isLocalDatabaseconnected() 
		{
			if(localcon.isDatabaseconnected() == true )
				return true;
			else 
				return false;
		}
		public boolean isRemoteDatabaseconnected() 
		{
			if(remotcon.isDatabaseconnected() == true)
				return true;
			else 
				return false;
		}

		public void closeConnectedDb()
		{
				try	{
					if(localcon != null)
						localcon.closeConnectedDb();
					
					if(remotcon != null)
						remotcon.closeConnectedDb();
				}catch(Exception ex) {
					ex.printStackTrace();
				}
        } 
		
		private boolean checkExecuteOnServer (String sqlstatement)
		{
			List<String> rmtservertable = Splitter.on('|').trimResults().omitEmptyStrings().splitToList(sysconfig.getTablesDataSelectedFromServer() );
			for(String str:rmtservertable) {
				if(sqlstatement.contains(str) )
					return true;
			}
			
			return false;
		}
		
		public CachedRowSetImpl sqlQueryStatExecute(HashMap<String,String> sqlstatementmap )
		{
			//�����ȡ��һ��SQL�������ж����ĸ����ݿ����������
			String sqlstatement = sqlstatementmap.get("mysql");
			boolean exectrmtcon = checkExecuteOnServer (sqlstatement);

			DataBaseConnection tmpdbcon;
			if(exectrmtcon == true) {
				tmpdbcon = remotcon;
			}
			else 
				tmpdbcon = localcon;

			//����Ӧ�����ݿ�����ִ������
			
			String dbtype = tmpdbcon.getDatabaseType();
			sqlstatement = sqlstatementmap.get(dbtype);
			CachedRowSetImpl cachedRS = null;
			cachedRS = tmpdbcon.sqlQueryStatExecute(sqlstatement);
			return cachedRS;
		}
		public CachedRowSetImpl sqlQueryStatExecute(String sqlstatement )
		{
			boolean exectrmtcon = checkExecuteOnServer (sqlstatement);

			DataBaseConnection tmpdbcon;
			if(exectrmtcon == true) {
				tmpdbcon = remotcon;
			}
			else 
				tmpdbcon = localcon;

			//����Ӧ�����ݿ�����ִ������
			CachedRowSetImpl cachedRS = null;
			cachedRS = tmpdbcon.sqlQueryStatExecute(sqlstatement);
			return cachedRS;
		}

		
		public int sqlUpdateStatExecute(HashMap<String,String> sqlstatementmap)
		{
			//�����ȡ��һ��SQL�������ж����ĸ����ݿ����������
			String sqlstatement = sqlstatementmap.get("mysql");
			boolean exectrmtcon = checkExecuteOnServer (sqlstatement);
			
			DataBaseConnection tmpdbcon;
			if(exectrmtcon == true) {
				tmpdbcon = remotcon;
			}
			else 
				tmpdbcon = localcon;
			
			String dbtype = tmpdbcon.getDatabaseType();
			sqlstatement = sqlstatementmap.get(dbtype);
			int autoIncKeyFromApi = -1;
			autoIncKeyFromApi = tmpdbcon.sqlUpdateStatExecute(sqlstatement);
			
			return autoIncKeyFromApi;
		}
		public int sqlUpdateStatExecute(String sqlstatement)
		{
			boolean exectrmtcon = checkExecuteOnServer (sqlstatement);
			
			DataBaseConnection tmpdbcon;
			if(exectrmtcon == true) {
				tmpdbcon = remotcon;
			}
			else 
				tmpdbcon = localcon;
			
			int autoIncKeyFromApi = -1;
			autoIncKeyFromApi = tmpdbcon.sqlUpdateStatExecute(sqlstatement);
			
			return autoIncKeyFromApi;
		}
		
		public int sqlInsertStatExecute(HashMap<String,String> sqlstatementmap) 
		{
			//�����ȡ��һ��SQL�������ж����ĸ����ݿ����������
			String sqlstatement = sqlstatementmap.get("mysql");
			boolean exectrmtcon = checkExecuteOnServer (sqlstatement);
			
			DataBaseConnection tmpdbcon;
			if(exectrmtcon == true) {
				tmpdbcon = remotcon;
			}
			else 
				tmpdbcon = localcon;
			
			String dbtype = tmpdbcon.getDatabaseType();
			sqlstatement = sqlstatementmap.get(dbtype);
			int result = 0; 
			result = tmpdbcon.sqlUpdateStatExecute(sqlstatement);
			
			return result;
			
		}
		public int sqlInsertStatExecute(String sqlstatement) 
		{
			boolean exectrmtcon = checkExecuteOnServer (sqlstatement);
			
			DataBaseConnection tmpdbcon;
			if(exectrmtcon == true) {
				tmpdbcon = remotcon;
			}
			else 
				tmpdbcon = localcon;
			
			int result = 0; 
			result = tmpdbcon.sqlUpdateStatExecute(sqlstatement);
			
			return result;
		}
		
		public int sqlDeleteStatExecute(HashMap<String,String> sqlstatementmap) 
		{
			//�����ȡ��һ��SQL�������ж����ĸ����ݿ����������
			String sqlstatement = sqlstatementmap.get("mysql");
			boolean exectrmtcon = checkExecuteOnServer (sqlstatement);
			
			DataBaseConnection tmpdbcon;
			if(exectrmtcon == true) {
				tmpdbcon = remotcon;
			}
			else 
				tmpdbcon = localcon;
			
			String dbtype = tmpdbcon.getDatabaseType();
			sqlstatement = sqlstatementmap.get(dbtype);
			int result = 0; 
			result = tmpdbcon.sqlUpdateStatExecute(sqlstatement);
			
			return result;
		}
		public int sqlDeleteStatExecute(String sqlstatement) 
		{
			boolean exectrmtcon = checkExecuteOnServer (sqlstatement);
			
			DataBaseConnection tmpdbcon;
			if(exectrmtcon == true) {
				tmpdbcon = remotcon;
			}
			else 
				tmpdbcon = localcon;
			
			int result = 0; 
			result = tmpdbcon.sqlUpdateStatExecute(sqlstatement);
			
			return result;
		}
		
		public String getLocalDatabaseType() 
		{
			return localcon.getDatabaseType();
		}
		public String getRemoteDatabaseType() 
		{
			return remotcon.getDatabaseType();
		}
		public String getLocalDatabaseName(String string) 
		{
			return localcon.getDatabaseName(string);
		}
		public String getRemoteDatabaseName(String shortorfull) 
		{
			return remotcon.getDatabaseName(shortorfull);
		}
//		public String[] getTDXDataSysRelatedTablesNames ()
//		{
//			return rmtservertable;
//		}

}

class DataBaseConnection 
{
	public DataBaseConnection (CurDataBase curdb)
	{
		if(this.isDatabaseconnected())
			try {
				con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
		}
		
		con = setupDataBaseConnection(curdb);
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
	
	private Connection setupDataBaseConnection (CurDataBase curdb)
	 {
		 	Connection tmpcon = null;
		 	String dbconnectstr = curdb.getDataBaseConStr();
			String user = curdb.getUser();
			String password =  curdb.getPassWord();

			String tmpdatabasetype = curdb.getCurDatabaseType().toLowerCase();
			String urlToDababasecrypt = null;
			switch(tmpdatabasetype) {
			case "access": 
						try	{
								Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
						} catch(ClassNotFoundException e)	{
//							System.out.println("�Ҳ������������� ����������ʧ�ܣ�");
							logger.error("�Ҳ������������� ����������ʧ�ܣ�");
							e.printStackTrace();
						}

//jdbc:ucanaccess://C:/Users/wei.Jeff_AUAS/OneDrive/stock/2016/exchange info manager test.accdb;jackcessOpener=com.exchangeinfomanager.database.CryptCodecOpener
						urlToDababasecrypt = "jdbc:ucanaccess://" + dbconnectstr + ";jackcessOpener=com.exchangeinfomanager.database.CryptCodecOpener";
						logger.debug(urlToDababasecrypt);
//						System.out.println(urlToDababasecrypt);
						break;
			case "mysql":
						try	{
								Class.forName("com.mysql.jdbc.Driver");
								logger.info("�ɹ�����MySQL���� for" + dbconnectstr);
//								 System.out.println("�ɹ�����MySQL���� for" + dbconnectstr);
							} catch(ClassNotFoundException e)	{
								logger.error("�Ҳ������������� ����������ʧ�ܣ�");
//								System.out.println("�Ҳ������������� ����������ʧ�ܣ�");
								e.printStackTrace();
							}
						urlToDababasecrypt = "jdbc:mysql://" +  dbconnectstr;  						// "jdbc:mysql://localhost:3306/stockinfomanagementtest" ;
						break;
			case "sqlserver": //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
								break;
								
			}
			
			try	{	// ��ʽ�� ͨ������Դ�����ݿ⽨������
//				System.out.println("begin to connect database");
				logger.info("begin to connect database");
				tmpcon = DriverManager.getConnection(urlToDababasecrypt,user,password);
				if(tmpcon != null)	{
//					System.out.println( "Sucessed connect database:" + dbconnectstr );
					logger.info("Sucessed connect database:" + dbconnectstr );
					this.databaseconnected = true;
				}
			} catch(SQLException se) {   
			    //se.printStackTrace();
				logger.error("Failed connect database:" + dbconnectstr );
//			    System.out.println( "Failed connect database:" + dbconnectstr );
			}
						
			return tmpcon;
		 
	 }
	
//	private void setDatabaseconnected(boolean databaseconnected) 
//	{
//		this.databaseconnected = databaseconnected;
//	}
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
			try {
				con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
				return false;
		}
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
		}  catch(net.ucanaccess.jdbc.UcanaccessSQLException ex) {
			ex.printStackTrace();
		} catch(java.lang.NullPointerException e) {
			logger.info("���ݿ�����ΪNULL");
//			System.out.println("���ݿ�����ΪNULL");
			e.printStackTrace();
		} catch(com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e1) {
			logger.info("�����ݿ�����ӶϿ�����Ҫ��������");
//			System.out.println("�����ݿ�����ӶϿ�����Ҫ��������");
			e1.printStackTrace();
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException e1) {
//			System.out.println("�����ݿ�����ӶϿ�����Ҫ��������");
			logger.info("�����ݿ�����ӶϿ�����Ҫ��������");
			e1.printStackTrace();
		}catch(Exception e) {
//			System.out.println("���ݿ�SQLִ��ʧ��");
//			System.out.println("����SQL��:" + sqlstatement );
			e.printStackTrace();
		}finally {
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
				 logger.debug("�������ݿ�ɹ�");
//                System.out.println("�������ݿ�ɹ�");  
            }
		} catch(SQLException e)	{	
//			autoIncKeyFromApi = 0;
//			System.out.println("����SQL��:" + sqlstatement );
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

	

//END

}