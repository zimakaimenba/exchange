package com.exchangeinfomanager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.exchangeinfomanager.systemconfigration.CurDataBase;

public class ConnectDZHDataBase 
{
	public DataBaseConnection dbcon = null;
	private ConnectDZHDataBase ()
	{  
		CurDataBase dzhcurdb = new CurDataBase ("SQLite");
		dzhcurdb.setCurDatabaseType("SQLite");
		dzhcurdb.setDataBaseConStr ("jdbc:sqlite:e:/stock/dzh365/data/B$/block_v2.db");
		
		refreshDatabaseConnection(dzhcurdb);  
	}
	
	 private void refreshDatabaseConnection(CurDataBase dzhcurdb) 
	 {
		 dbcon = new DataBaseConnection (dzhcurdb);
		 
//	        try {
//	            // db parameters
////	            String url = "jdbc:sqlite:e:/stock/dzh365/data/B$/block_v2.db";
//	            String url = dzhcurdb.getDataBaseConStr() ;
//	            // create a connection to the database
//	            localcon = DriverManager.getConnection(url);
//	            
//	            System.out.println("Connection to SQLite has been established.");
//	            
//	        } catch (SQLException e) { System.out.println(e.getMessage());     } 
	 }
	// µ¥ÀýÊµÏÖ  
	 public static ConnectDZHDataBase getInstance ()
	 {  
	        return Singtonle.instance;  
	 }
	 
	 private static class Singtonle 
	 {  
	        private static ConnectDZHDataBase instance =  new ConnectDZHDataBase ();  
	 }
	 
	 public void closeConnectedDb()
	 {
				try	{
					if(dbcon != null)
						dbcon.closeConnectedDb();
				}catch(Exception ex) {	ex.printStackTrace();}
     } 

}
