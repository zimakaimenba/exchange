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

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.Nodes.StockOfBanKuai;
import com.exchangeinfomanager.Core.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.Core.Trees.CreateExchangeTree;
import com.exchangeinfomanager.commonlib.ExecutePythonScripts;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.google.common.base.Splitter;
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
	public void refreshGuDongData(Boolean onlyimportwithjigougudong, Boolean forcetorefrshallgudong,String... downloededgudongdatafilenamepattern) 
	{
		Collection<BkChanYeLianTreeNode> allstocks = AllCurrentTdxBKAndStoksTree.getInstance().getAllBkStocksTree().getAllRequiredNodes(BkChanYeLianTreeNode.TDXGG);
		for(BkChanYeLianTreeNode stock : allstocks) {
			refreshStockGuDongData ((Stock)stock, onlyimportwithjigougudong, forcetorefrshallgudong, downloededgudongdatafilenamepattern[0],downloededgudongdatafilenamepattern[1]);
		}
	}
	/*
	 * 
	 */
	public Stock refreshStockGuDongData (Stock stock, Boolean onlyimportwithjigougudong, Boolean forcetorefrshallgudong,String... downloededgudongdatafilenamepattern)
	{
		if( forcetorefrshallgudong)
			this.deleteStockGuDong (stock);
			
		
		Set<String> jigou = null;
		if(onlyimportwithjigougudong)
			jigou = getJiGouList ();
		
		String floatcsvfilename = null; String top10csvfilename = null;
		if(downloededgudongdatafilenamepattern == null || downloededgudongdatafilenamepattern.length == 0 ) {
			Boolean execresult = false; 
			List<String> result = ExecutePythonScripts.executePythonScriptForExtraData("FORCETOREFRESHSHAREHOLDER", stock);
			for(String resultline : result) {
				if(resultline.toLowerCase().contains("got all data"))
					execresult = true;
				else if(resultline.toLowerCase().contains("floatcsvfilename")) {
//					List<String> tmpbkinfo = Splitter.on("").omitEmptyStrings().splitToList(line); //���ɰ��|880232|3|1|0|32
					floatcsvfilename = resultline.toLowerCase().replaceAll("floatcsvfilename", "");
					floatcsvfilename = floatcsvfilename.substring(1, floatcsvfilename.length()-1);
				} else if(resultline.toLowerCase().contains("top10csvfilename")) {
//					resultline.toLowerCase().replaceAll("top10csvfilename", "");
					top10csvfilename = resultline.toLowerCase().replaceAll("top10csvfilename", "");
					top10csvfilename = top10csvfilename.substring(1, top10csvfilename.length()-1);
				}
			}
			if(!execresult) {
				System.out.println("Refresh GuDong info failed. Error Info: " + result);
				return stock;
			}
		} else {
			String csvfilepath = sysconfig.getGuDongInfoCsvFile();
			floatcsvfilename = csvfilepath +  "/" + downloededgudongdatafilenamepattern[0];
			top10csvfilename  = csvfilepath + "/" + downloededgudongdatafilenamepattern[1];
			
			floatcsvfilename = floatcsvfilename.replace("XXXXXX", stock.getMyOwnCode());
			top10csvfilename = top10csvfilename.replace("XXXXXX", stock.getMyOwnCode());
		}
		
		File floatcsvfile = new File(floatcsvfilename);
		File top10csvfile = new File(top10csvfilename);
//		String stockcode = stock.getMyOwnCode();
//		String floatcsvfilename = stockcode + "_floatholders.csv";
//		String top10csvfilename = stockcode + "_top10holders.csv";
//		File floatcsvfile = new File(csvfilepath + "/" + floatcsvfilename);
//		File top10csvfile = new File(csvfilepath + "/" + top10csvfilename);
		if (  (!floatcsvfile.exists()  || !floatcsvfile.canRead() ) && (!top10csvfile.exists()  || !top10csvfile.canRead())   ) {  
				logger.info(stock.getMyOwnCode() + "�ɶ��ļ���ȫ���߶�ȡ����û�е����κ����ݡ�");
				return stock;   
		}

		readGuDongCsvFile (stock,  floatcsvfilename, "liutong", jigou);
//		readGuDongCsvFile (stock,   top10csvfilename,"gudong");
		
		return stock;
	}
	/*
	 * 
	 */
	private void deleteStockGuDong (BkChanYeLianTreeNode stock) 
	{
		String sqldeletestat = "DELETE  FROM ��Ʊ�ɶ���Ӧ��"
				+ " WHERE ���� = " + "'" + stock.getMyOwnCode() + "'"
				;
		
		try {
			int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletestat);
		} catch (SQLException e) {e.printStackTrace();}
	}
	/*
	 * 
	 */
	private void readGuDongCsvFile (BkChanYeLianTreeNode stock, String gdfile ,String ggtype,Set<String> jigou)
	{
		System.out.print("Get Gudong info from " + gdfile + "\n");
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
			storeGuDongXinToDatabase (stock, gdlinevalueforoneseason, ggtype);
			records = null;
		} catch (IOException e) {e.printStackTrace();
		} finally {	try { floatcsvreader.close();	} catch (IOException e) {e.printStackTrace(); }		}
		
		return;
	}
	/*
	 * 
	 */
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
	/*
	 * 
	 */
	public Stock getStockGuDong(Stock stock, String liutongorgudong, LocalDate requiredstart, LocalDate requiredend)
	{
//		Object[][] data = null;
		CachedRowSetImpl  rs = null;
		LocalDate maxrecordsdate = null; LocalDate minrecordsdate = null; String sqlquerystat;
		if(liutongorgudong.equalsIgnoreCase("LIUTONG"))				
			sqlquerystat = "SELECT * , GROUP_CONCAT(�����ɶ�.`��������`) jigoulist, GROUP_CONCAT(�����ɶ�.`˵��`) jigoushuom\r\n" + 
					"FROM \r\n" + 
					"(SELECT  * ,IF (�������� REGEXP  \r\n" + 
					" (SELECT \r\n" + 
					" GROUP_CONCAT(DISTINCT �������� SEPARATOR '|')\r\n" + 
					"FROM �����ɶ�\r\n" + 
					"WHERE ���׹��� = TRUE  )\r\n" + 
					", TRUE, FALSE ) AS HQGQ ,\r\n" + 
					"\r\n" + 
					"IF (�������� REGEXP  \r\n" + 
					" (SELECT \r\n" + 
					" GROUP_CONCAT(DISTINCT �������� SEPARATOR '|')\r\n" + 
					"FROM �����ɶ�\r\n" + 
					"WHERE ���� = TRUE )\r\n" + 
					", TRUE, FALSE ) AS MX\r\n" + 
					"FROM ��Ʊ�ɶ���Ӧ��  \r\n" + 
					"WHERE  ���� = '"  + stock.getMyOwnCode().trim() + "' \r\n" + 
					"AND ��ͨ�ɶ� = TRUE \r\n"  
					+ " AND �ɶ����� BETWEEN '" + requiredstart + "' AND '" + requiredend + "'" 
					+ ") gudong\r\n" + 
					"\r\n" + 
					"LEFT JOIN  �����ɶ�\r\n" + 
					"ON gudong.�������� REGEXP �����ɶ�.`��������`  \r\n" + 
					"\r\n" + 
					"GROUP BY gudong.��������, gudong.�ɶ����� \r\n"
					+ "ORDER BY gudong.�ɶ�����  DESC"
					;
		else 
			sqlquerystat = "SELECT  * FROM ��Ʊ�ɶ���Ӧ��   "
					+  "IF (�������� REGEXP  \r\n"  
					+ " (SELECT \r\n"  
					+ "   GROUP_CONCAT(DISTINCT �������� SEPARATOR '|')\r\n"  
					+ "FROM �����ɶ�\r\n"  
					+ " WHERE ���׹��� = TRUE OR ���� = TRUE ), TRUE, FALSE ) AS HQGQMX\r\n"
					+ "WHERE  ���� = " 
						+ "'"  + stock.getMyOwnCode().trim() + "'" 
						+ " AND ʮ��ɶ� = TRUE"
						+ " AND �ɶ����� BETWEEN '" + requiredstart + "' AND '" + requiredend + "'"
						+ "  ORDER BY �ɶ�����  DESC"
						;
			List<Object[]> data = new ArrayList<>();
			try { 
		    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		        int columnCount = 6;//����
		        while(rs.next()) {  
		        	Object[] row = new Object[columnCount];
		        	
		    		 String gudong = rs.getString("��������"); //mOST_RECENT_TIME
		    		 Double chigushu = rs.getDouble("��ͨ����"); //mOST_RECENT_TIME
		    		 LocalDate riqi = rs.getDate("�ɶ�����").toLocalDate();
		    		 Boolean hqgq =  rs.getBoolean("HQGQ");
		    		 Boolean mx =  rs.getBoolean("MX");
		    		 String jigoushuoming = rs.getString("jigoushuom");
		    		 
		    		 row[0] = gudong;
		    		 row[1] = riqi;
		    		 row[2] = chigushu;
		    		 row[3] = hqgq;
		    		 row[4] = mx;
		    		 row[5] = jigoushuoming;
		    		 data.add( row);
		    	}
		        
		        if(data.size() >= 0 )
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
	public Integer storeJiGouToDb(String jigouname, String jigouquancheng, String jigoushuoming, Boolean hqgq, Boolean mxjj)
	{
		String	sqlinsertstat = "INSERT INTO �����ɶ�(��������, ����ȫ��, ˵��, ���׹��� , ����) VALUES ("
					+ "'" + jigouname + "',"
					+ "'" + jigouquancheng + "',"
					+ "'" + jigoushuoming + "',"
					+ hqgq + ","
					+ mxjj 
					+ ")"
						;
		String sqlupdatedtstat = "UPDATE �����ɶ�  set"
						+ " ˵��= '" + jigoushuoming + "',"
						+ " ����ȫ��= '" + jigouquancheng + "',"
						+ " ���׹���  = "	+ hqgq + ","
						 + " ����= " + mxjj 
						 + " WHERE ��������= '" + jigouname + "'"
						 ;
		int autoIncKeyFromApi = 0;
		try {
			autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
			try {
			connectdb.sqlUpdateStatExecute(sqlupdatedtstat);
			} catch (SQLException e1) {e1.printStackTrace();}
		} catch (SQLException e) { e.printStackTrace();return null;}
		
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
	/*
	 * 
	 */
	public void checkStockHasHuangQinGuoQieAndMingXin (Stock stock)
	{
		String sqlstat = "SELECT * FROM \r\n" + 
				"(\r\n" + 
				"SELECT *, \r\n" + 
				"IF (�������� REGEXP  \r\n" + 
				" (SELECT \r\n" + 
				" GROUP_CONCAT(DISTINCT �������� SEPARATOR '|')\r\n" + 
				"FROM �����ɶ�\r\n" + 
				"WHERE ���׹��� = TRUE  )\r\n" + 
				", TRUE, FALSE ) AS HQGQ ,\r\n" + 
				"\r\n" + 
				"IF (�������� REGEXP  \r\n" + 
				" (SELECT \r\n" + 
				" GROUP_CONCAT(DISTINCT �������� SEPARATOR '|')\r\n" + 
				"FROM �����ɶ�\r\n" + 
				"WHERE ���� = TRUE )\r\n" + 
				", TRUE, FALSE ) AS MX\r\n"  
				+ "FROM ��Ʊ�ɶ���Ӧ��\r\n" + 
				"WHERE �������� REGEXP  \r\n" + 
				" (SELECT \r\n" + 
				"   GROUP_CONCAT(DISTINCT �������� SEPARATOR '|')\r\n" + 
				"FROM �����ɶ�\r\n" + 
				"WHERE ���׹��� = TRUE OR ���� = TRUE )\r\n" + 
				"AND ���� =  '" + stock.getMyOwnCode() + "' \r\n" + 
				"GROUP BY ��Ʊ�ɶ���Ӧ��.`����`, ��Ʊ�ɶ���Ӧ��.`�ɶ�����` \r\n" + 
				"ORDER  BY ��Ʊ�ɶ���Ӧ��.`�ɶ�����` DESC \r\n" + 
				") gd\r\n" + 
				"LEFT  JOIN \r\n" + 
				"(\r\n" + 
				"SELECT ��Ʊ�ɶ���Ӧ��.`����` dm,  MAX( ��Ʊ�ɶ���Ӧ��.`�ɶ�����`)  maxgdrq \r\n" + 
				"FROM ��Ʊ�ɶ���Ӧ��\r\n" + 
				"WHERE ���� =  '" + stock.getMyOwnCode() + "' \r\n" + 
				"GROUP BY ��Ʊ�ɶ���Ӧ��.`����`)  gdmaxtime\r\n" + 
				"ON gd.`����` = gdmaxtime.dm"
				;

		CachedRowSetImpl  rs = connectdb.sqlQueryStatExecute(sqlstat);
		try {
			while(rs.next() ) {
				Boolean hqgq = rs.getBoolean("HQGQ");
				Boolean mx = rs.getBoolean("MX");
				LocalDate gddate = rs.getDate("�ɶ�����").toLocalDate();
				if(hqgq)
					stock.getNodeJiBenMian().addGuDongHqgqInterval (gddate);
				if(mx)
					stock.getNodeJiBenMian().addGuDongMinXingInterval (gddate);
				
				LocalDate maxgdrq = rs.getDate("maxgdrq").toLocalDate(); //�����б�����
				stock.getNodeJiBenMian().setLastestCaiBaoDate(maxgdrq);
			}
		} catch(java.lang.NullPointerException e) {
	    } catch(Exception e){e.printStackTrace();
	    } finally {
	    	if(rs != null)
			try { rs.close();rs = null;
			} catch (SQLException e) {e.printStackTrace();}
	    }
		
		return;
	
	}
	/*
	 * 
	 */
	public void checkBanKuaiGeGuHasHuangQinGuoQieAndMingXin (BanKuai bk, LocalDate checkdate)
	{
		String bkggstr = "";
		List<BkChanYeLianTreeNode> bkgg = bk.getAllGeGuOfBanKuaiInHistory();
		if(bkgg == null || bkgg.isEmpty())
			return;
		
		for(BkChanYeLianTreeNode stockofbk : bkgg) 
			if( ((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(checkdate)  ) 
				bkggstr = bkggstr + "'" + stockofbk.getMyOwnCode() +  "', ";
		bkggstr = bkggstr.substring(0, bkggstr.length() -2);
		
		String sqlstat = "SELECT * FROM \r\n" + 
				"(\r\n" + 
				"SELECT *, \r\n" + 
				"IF (�������� REGEXP  \r\n" + 
				" (SELECT \r\n" + 
				" GROUP_CONCAT(DISTINCT �������� SEPARATOR '|')\r\n" + 
				"FROM �����ɶ�\r\n" + 
				"WHERE ���׹��� = TRUE  )\r\n" + 
				", TRUE, FALSE ) AS HQGQ ,\r\n" + 
				"\r\n" + 
				"IF (�������� REGEXP  \r\n" + 
				" (SELECT \r\n" + 
				" GROUP_CONCAT(DISTINCT �������� SEPARATOR '|')\r\n" + 
				"FROM �����ɶ�\r\n" + 
				"WHERE ���� = TRUE )\r\n" + 
				", TRUE, FALSE ) AS MX\r\n"  
				+ "FROM ��Ʊ�ɶ���Ӧ��\r\n" + 
				"WHERE �������� REGEXP  \r\n" + 
				" (SELECT \r\n" + 
				"   GROUP_CONCAT(DISTINCT �������� SEPARATOR '|')\r\n" + 
				"FROM �����ɶ�\r\n" + 
				"WHERE ���׹��� = TRUE OR ���� = TRUE )\r\n" + 
				"AND ���� IN  (" + bkggstr + ") \r\n" + 
				"GROUP BY ��Ʊ�ɶ���Ӧ��.`����`, ��Ʊ�ɶ���Ӧ��.`�ɶ�����` \r\n" + 
				"ORDER  BY ��Ʊ�ɶ���Ӧ��.`�ɶ�����` DESC \r\n" + 
				") gd\r\n" + 
				"LEFT  JOIN \r\n" + 
				"(\r\n" + 
				"SELECT ��Ʊ�ɶ���Ӧ��.`����` dm,  MAX( ��Ʊ�ɶ���Ӧ��.`�ɶ�����`)  maxgdrq \r\n" + 
				"FROM ��Ʊ�ɶ���Ӧ��\r\n" + 
				"WHERE ���� IN  (" + bkggstr + ") \r\n" + 
				"GROUP BY ��Ʊ�ɶ���Ӧ��.`����`)  gdmaxtime\r\n" + 
				"ON gd.`����` = gdmaxtime.dm"
				;

		CachedRowSetImpl  rs = connectdb.sqlQueryStatExecute(sqlstat);
		try {
			while(rs.next() ) {
				String stockcode = rs.getString("����");
				Stock node = (Stock)CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(stockcode, BkChanYeLianTreeNode.TDXGG);
				if(node == null) 
					continue;
				Boolean hqgq = rs.getBoolean("HQGQ");
				Boolean mx = rs.getBoolean("MX");
				LocalDate gddate = rs.getDate("�ɶ�����").toLocalDate();
				if(hqgq)
					node.getNodeJiBenMian().addGuDongHqgqInterval (gddate);
				if(mx)
					node.getNodeJiBenMian().addGuDongMinXingInterval (gddate);
				
				LocalDate maxgdrq = rs.getDate("maxgdrq").toLocalDate(); //�����б�����
				node.getNodeJiBenMian().setLastestCaiBaoDate(maxgdrq);
			}
		} catch(java.lang.NullPointerException e) {
	    } catch(Exception e){e.printStackTrace();
	    } finally {
	    	if(rs != null)
			try { rs.close();rs = null;
			} catch (SQLException e) {e.printStackTrace();}
	    }
		
		return;
	}

}
