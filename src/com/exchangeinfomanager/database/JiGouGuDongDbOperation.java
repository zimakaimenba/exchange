package com.exchangeinfomanager.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import com.exchangeinfomanager.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.mysql.jdbc.MysqlDataTruncation;
import com.opencsv.CSVReader;
import com.sun.rowset.CachedRowSetImpl;

public class JiGouGuDongDbOperation 
{
	private ConnectDataBase connectdb;
	private SetupSystemConfiguration sysconfig;

	public JiGouGuDongDbOperation ()
	{
		connectdb = ConnectDataBase.getInstance();
		sysconfig = new SetupSystemConfiguration();
	}
	private static Logger logger = Logger.getLogger(JiGouGuDongDbOperation.class);
	
	/*
	 * 
	 */
	private LocalDate[] getCurrentStockGuDongDateRange (BkChanYeLianTreeNode stock, String liutongorgudong)
	{
		CachedRowSetImpl  rs = null;
		LocalDate maxrecordsdate = null; LocalDate minrecordsdate = null; String sqlquerystat;
		if(liutongorgudong.equalsIgnoreCase("LIUTONG"))				
			sqlquerystat = "SELECT  MAX(�ɶ�����) 	MAX_RECENT_TIME , MIN(�ɶ�����) 	MIN_RECENT_TIME "
					+ " FROM " + "��Ʊ�ɶ���Ӧ��" + "   WHERE  ���� = " 
						+ "'"  + stock.getMyOwnCode().trim() + "'" 
						+ " AND ��ͨ�ɶ� = TRUE"
						;
		else
			sqlquerystat = "SELECT  MAX(�ɶ�����) 	MAX_RECENT_TIME , MIN(�ɶ�����) 	MIN_RECENT_TIME "
					+ " FROM " + "��Ʊ�ɶ���Ӧ��" + "   WHERE  ���� = " 
						+ "'"  + stock.getMyOwnCode().trim() + "'" 
						+ " AND ʮ��ɶ� = TRUE"
						;
			try { 
		    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	
		    	while(rs.next()) {
		    		 maxrecordsdate = rs.getDate("MAX_RECENT_TIME").toLocalDate(); //mOST_RECENT_TIME
		    		 minrecordsdate = rs.getDate("MIN_RECENT_TIME").toLocalDate(); //mOST_RECENT_TIME
		    	}
		    } catch(java.lang.NullPointerException e) {
		    } catch (SQLException e) {e.printStackTrace();
		    } catch(Exception e){e.printStackTrace();
		    } finally {
		    	if(rs != null)
				try { rs.close();rs = null;
				} catch (SQLException e) {e.printStackTrace();}
		    }
		
		LocalDate[] result = {minrecordsdate,maxrecordsdate};
		return result;
	}
	/*
	 * 
	 */
	private Set<String> getJiGouList ()
	{
		Set<String> jigouset = new HashSet<> ();
		CachedRowSetImpl  rs = null;
		String sqlquerystat = "SELECT  * FROM �����ɶ�"  ;
			try { 
		    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	
		    	while(rs.next()) {
		    		 String jigouname = rs.getString("��������"); //mOST_RECENT_TIME
		    		 jigouset.add(jigouname.trim());
		    	}
		    } catch(java.lang.NullPointerException e) {
		    } catch (SQLException e) {e.printStackTrace();
		    } catch(Exception e){e.printStackTrace();
		    } finally {
		    	if(rs != null)
				try { rs.close();rs = null;} catch (SQLException e) {e.printStackTrace();}
		    }
			
			return jigouset;
	}
	/*
	 * 
	 */
	public void refreshGuDongData(Boolean onlyimportwithjigougudong) 
	{
		String csvfilepath = sysconfig.getGuDongInfoCsvFile();
		Set<String> jigou = null;
		if(onlyimportwithjigougudong)
			jigou = getJiGouList (); 
		
		Collection<BkChanYeLianTreeNode> allstocks = AllCurrentTdxBKAndStoksTree.getInstance().getAllBkStocksTree().getAllRequiredNodes(BkChanYeLianTreeNode.TDXGG);
		for(BkChanYeLianTreeNode stock : allstocks) {
			String stockcode = stock.getMyOwnCode();
			String floatcsvfilename = stockcode + "_floatholders.csv";
			String top10csvfilename = stockcode + "_top10holders.csv";
			File floatcsvfile = new File(csvfilepath + "/" + floatcsvfilename);
			File top10csvfile = new File(csvfilepath + "/" + top10csvfilename);
			if (  (!floatcsvfile.exists()  || !floatcsvfile.canRead() ) && (!top10csvfile.exists()  || !top10csvfile.canRead())   ) {  
					logger.info(stockcode + "�ɶ��ļ���ȫ���߶�ȡ����û�е����κ����ݡ�");
					continue;
			}

			readGuDongCsvFile (stock, csvfilepath + "/" + floatcsvfilename, "liutong", jigou);
//			readGuDongCsvFile (stock, csvfilepath + "/" + top10csvfilename,"gudong");
		}
	}
	public Stock refreshStockGuDongData (Stock stock, Boolean onlyimportwithjigougudong, Boolean forcetorefrshallgudong)
	{
		if( forcetorefrshallgudong)
			this.deleteStockGuDong (stock);
			
		String csvfilepath = sysconfig.getGuDongInfoCsvFile();
		
		Set<String> jigou = null;
		if(onlyimportwithjigougudong)
			jigou = getJiGouList ();
		
		String stockcode = stock.getMyOwnCode();
		String floatcsvfilename = stockcode + "_floatholders.csv";
		String top10csvfilename = stockcode + "_top10holders.csv";
		File floatcsvfile = new File(csvfilepath + "/" + floatcsvfilename);
		File top10csvfile = new File(csvfilepath + "/" + top10csvfilename);
		if (  (!floatcsvfile.exists()  || !floatcsvfile.canRead() ) && (!top10csvfile.exists()  || !top10csvfile.canRead())   ) {  
				logger.info(stockcode + "�ɶ��ļ���ȫ���߶�ȡ����û�е����κ����ݡ�");
				return stock;
		}

		readGuDongCsvFile (stock, csvfilepath + "/" + floatcsvfilename, "liutong", jigou);
//		readGuDongCsvFile (stock, csvfilepath + "/" + top10csvfilename,"gudong");
		
		return stock;
	}
	private void deleteStockGuDong (BkChanYeLianTreeNode stock) 
	{
		String sqldeletestat = "DELETE  FROM ��Ʊ�ɶ���Ӧ��"
				+ " WHERE ���� = " + "'" + stock.getMyOwnCode() + "'"
				;
		
		try {
			int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletestat);
		} catch (SQLException e) {e.printStackTrace();}
	}
	private void readGuDongCsvFile (BkChanYeLianTreeNode stock, String gdfile ,String ggtype,Set<String> jigou)
	{
		LocalDate[] currecorddate = getCurrentStockGuDongDateRange (stock, ggtype);
		if(currecorddate[0] == null)
			currecorddate[0] = LocalDate.parse("3990-01-01");
		if(currecorddate[1] == null)
			currecorddate[1] = LocalDate.parse("3990-12-01");
		CSVReader floatcsvreader = null;
		try {
			floatcsvreader=new CSVReader(
				    new InputStreamReader(new FileInputStream(gdfile), "UTF-8"), 
				    ',', '\'', 1);
//			floatcsvreader = new CSVReaderBuilder(new FileReader(csvfilepath + "/" + floatcsvfilename))
//			        .withSkipLines(1)
//			        .withCSVParser(new CSVParserBuilder().withSeparator(',').withEscapeChar('\'').build())
//			        .build();
		} catch (FileNotFoundException e) {e.printStackTrace(); return;} 
		catch (UnsupportedEncodingException e) {e.printStackTrace(); return;}
		
		try {
			int linenumber = 1; Boolean foundjigou = false;
			List<String[]> gdlinevalueforoneseason = new ArrayList<>();
			LocalDate curseasondate = null;
//			while ( (linevalue = floatcsvreader.readNext() )!=null ) {
			List<String[]> records = floatcsvreader.readAll();
			for (String[] linevalue : records) {
				LocalDate curlinedate;
				try {
					String gudongdate = linevalue[3];
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd" );
					curlinedate = LocalDate.parse(gudongdate,formatter);
				} catch (java.time.format.DateTimeParseException e) {linenumber = 1; continue;}
				
				if(curlinedate.isAfter(currecorddate[0].minusDays(1)) && curlinedate.isBefore(currecorddate[1].plusDays(1)) ) {
					linenumber = 1 ; continue;//�Ѿ�����
				}
				
				String gudongname = linevalue[4].replaceAll(" ", "");
				if(jigou != null)//only store jigou holder ,
					for(String tmpjg : jigou) 
						if(gudongname.contains(tmpjg))
							foundjigou = true;
				
				if(linenumber != 1) {
					if(curseasondate.equals(curlinedate)  ) { 
						if(foundjigou)
							gdlinevalueforoneseason.add(linevalue);
					}
					else {
							storeGuDongXinToDatabase (stock, gdlinevalueforoneseason, ggtype);
							gdlinevalueforoneseason.clear();
							if(foundjigou)
								gdlinevalueforoneseason.add(linevalue);
							curseasondate = curlinedate;
							linenumber = 1;
					}
				} else {
					curseasondate = curlinedate;
					if(foundjigou)
						gdlinevalueforoneseason.add(linevalue);
				}
				
				foundjigou = false;
				linenumber ++;
			}
			records = null;
		} catch (IOException e) {e.printStackTrace();}
		
	}
	private void storeGuDongXinToDatabase(BkChanYeLianTreeNode stock, List<String[]> linevaluelist, String liutongorgudong)
	{
		for(String[] linevalue : linevaluelist) {
			LocalDate curgudongdate; LocalDate gonggaodate;
			try {
				String gudongdate = linevalue[3];
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd" );
				curgudongdate = LocalDate.parse(linevalue[3],formatter);
				gonggaodate = LocalDate.parse(linevalue[2],formatter);
			} catch (java.time.format.DateTimeParseException e) {return;}
			String gudongname = linevalue[4].replaceAll(" ", "");
			Double chigushu = null;
			try {
				chigushu = Double.parseDouble(linevalue[5]);
			} catch (Exception ex) {}
			String sqlinsertstat;
			if(liutongorgudong.equalsIgnoreCase("LIUTONG"))
				sqlinsertstat = "INSERT INTO ��Ʊ�ɶ���Ӧ��(����,��������,�ɶ�����,��������,��ͨ����,��ͨ�ɶ�) VALUES ("
						+ "'" + stock.getMyOwnCode().trim() + "'" + ","
						+ "'" + gudongname + "',"
	    				+ "'" + curgudongdate + "'," 
	    				+ "'" + gonggaodate + "',"
	    				+ chigushu + ","
	    				+ "true"
							+ ")"
							+ " ON DUPLICATE KEY UPDATE "
							+ " �������� = '" + gudongname + "'," 
							+ " �ɶ����� = '" + curgudongdate + "',"
							+ " �������� = '" + gonggaodate + "',"
							+ " ��ͨ����= " + chigushu + ","
							+ " ��ͨ�ɶ�= true"
							;
			else
				sqlinsertstat = "INSERT INTO ��Ʊ�ɶ���Ӧ��(����,��������,�ɶ�����,��������,�ɷ���, ʮ��ɶ�) VALUES ("
						+ "'" + stock.getMyOwnCode().trim() + "'" + ","
						+ "'" + gudongname + "',"
	    				+ "'" + curgudongdate + "'," 
	    				+ "'" + gonggaodate + "',"
	    				+ chigushu + ","
	    				+ "true"
							+ ")"
							+ " ON DUPLICATE KEY UPDATE "
							+ " �������� = '" + gudongname + "'," 
							+ " �ɶ����� = '" + curgudongdate + "',"
							+ " �������� = '" + gonggaodate + "',"
							+ " �ɷ���= " + chigushu + ","
							+ " ʮ��ɶ�= true"
							;
			
			try {
				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
			} catch (MysqlDataTruncation e) {e.printStackTrace();
			} catch (SQLException e) {	System.out.print(sqlinsertstat);			e.printStackTrace();}
			
		}
	}
	public Stock getStockGuDong(Stock stock, String liutongorgudong, LocalDate requiredstart, LocalDate requiredend)
	{
		Object[][] data = null;
		CachedRowSetImpl  rs = null;
		LocalDate maxrecordsdate = null; LocalDate minrecordsdate = null; String sqlquerystat;
		if(liutongorgudong.equalsIgnoreCase("LIUTONG"))				
			sqlquerystat = "SELECT  * FROM ��Ʊ�ɶ���Ӧ��  WHERE  ���� = " 
						+ "'"  + stock.getMyOwnCode().trim() + "'" 
						+ " AND ��ͨ�ɶ� = TRUE"
						+ " AND �ɶ����� BETWEEN '" + requiredstart + "' AND '" + requiredend + "'"
						+ "  ORDER BY �ɶ�����  DESC"
						;
		else 
			sqlquerystat = "SELECT  * FROM ��Ʊ�ɶ���Ӧ��   WHERE  ���� = " 
						+ "'"  + stock.getMyOwnCode().trim() + "'" 
						+ " AND ʮ��ɶ� = TRUE"
						+ " AND �ɶ����� BETWEEN '" + requiredstart + "' AND '" + requiredend + "'"
						+ "  ORDER BY �ɶ�����  DESC"
						;
			try { 
		    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	int k = 0;
		        int columnCount = 3;//����
		        
		        rs.last();  
		        int rows = rs.getRow();  
		        data = new Object[rows][];
		        rs.first();  
		        //while(rs.next())  //{ "����", "����", "˵��","ID","�����˻�","��Ϣ��" };
		        for(int j=0;j<rows;j++) {   //{ "����", "����", "˵��","ID","�����˻�","��Ϣ��" };
		        	Object[] row = new Object[columnCount];
		        	
		    		 String gudong = rs.getString("��������"); //mOST_RECENT_TIME
		    		 Double chigushu = rs.getDouble("��ͨ����"); //mOST_RECENT_TIME
		    		 LocalDate riqi = rs.getDate("�ɶ�����").toLocalDate();
		    		 row[0] = gudong;
		    		 row[1] = riqi;
		    		 row[2] = chigushu;
		    		 data[k] = row;
		    		 
		    		 k++; 
			         rs.next();
		    	}
		        
		        if(ArrayUtils.isNotEmpty(data))
		        	stock.getNodeJiBenMian().setGuDongInfo (data);
		    } catch(java.lang.NullPointerException e) {
		    } catch (SQLException e) {e.printStackTrace();
		    } catch(Exception e){e.printStackTrace();
		    } finally {
		    	if(rs != null)
				try { rs.close();rs = null;
				} catch (SQLException e) {e.printStackTrace();}
		    }
			
			return stock;
	}
	/*
	 * 
	 */
	public Integer storeJiGouToDb(String jigouname)
	{
		String	sqlinsertstat = "INSERT INTO �����ɶ�(��������) VALUES ("
					+ "'" + jigouname+ "')"
						;
		int autoIncKeyFromApi = 0;
		try {
			autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		} catch (MysqlDataTruncation e) {e.printStackTrace();
		} catch (SQLException e) {	return null;}
		
		return autoIncKeyFromApi;
	}
	/*
	 * 
	 */
	public Collection<BkChanYeLianTreeNode> searchJiGouStock(String jigouname) 
	{
		Collection<BkChanYeLianTreeNode> nodeset = new ArrayList <> ();
		
		String searchstringformated = "'*" + jigouname  + "*'";
		String searchsqlstat = "SELECT * FROM ��Ʊ��ͨ�ɶ���Ӧ��  WHERE ��������    LIKE " + searchstringformated  + " ORDER BY ��Ʊ����" ; 
		CachedRowSetImpl  rs = null;
		try {
			rs = connectdb.sqlQueryStatExecute(searchsqlstat);
			while(rs.next() ) {
				String stockcode = rs.getString("��Ʊ����");
				BkChanYeLianTreeNode node = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(stockcode, BkChanYeLianTreeNode.TDXGG);
				if(node != null) nodeset.add(node);
			}
		} catch(java.lang.NullPointerException e) {
	    } catch(Exception e){e.printStackTrace();
	    } finally {
	    	if(rs != null)
			try { rs.close();rs = null;
			} catch (SQLException e) {e.printStackTrace();}
	    }
		
	    
	 return nodeset;
	}

}
