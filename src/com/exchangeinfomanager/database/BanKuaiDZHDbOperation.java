package com.exchangeinfomanager.database;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;
import com.mysql.jdbc.MysqlDataTruncation;
import com.sun.rowset.CachedRowSetImpl;

public class BanKuaiDZHDbOperation 
{
	private ConnectDZHDataBase dzhconnectdb;
	private ConnectDataBase tdxconnectdb;
	private SetupSystemConfiguration sysconfig;

	public BanKuaiDZHDbOperation() 
	{
		tdxconnectdb = ConnectDataBase.getInstance();
//		dzhconnectdb = ConnectDZHDataBase.getInstance();
		sysconfig = new SetupSystemConfiguration();
	}
	/*
	 * 
	 */
	public void refreshDZHGaiNianBanKuaiGeGu ()
	{
		try {
			List<BanKuai> dzhbklist = this.getDZHBanKuaiList ();
			
			for(BanKuai dzhbk : dzhbklist ) {
				if(!dzhbk.isImportBKGeGu() )
					continue;
				
				String dzhbkcode = dzhbk.getMyOwnCode();
				Set<String> tmpstockcodesetold = getDZHGaiNianBanKuaiGeGuSetInDb (dzhbkcode);
				Set<String> tmpstockcodesetnew  = getDZHGaiNianBanKuaiGeGuSetFromDZHFiles (dzhbkcode);
				
				//�� tmpstockcodesetold �����еģ�tmpstockcodesetnewû�е�ѡ�������Ǿɵģ�Ҫ�����ݿ���ɾ��
				SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
	   	        for (String stockcode : differencebankuaiold) {
	   	        	String sqlupdatestat = 
	   	        	"UPDATE ��Ʊͨ���Ÿ������Ӧ��  \r\n" + 
	   	        	"SET �Ƴ�ʱ�� =  " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'" +  
	   	        	"WHERE ������ = '' \r\n" + 
	   	        	"AND ��Ʊ���� = '' \r\n" + 
	   	        	"AND isnull(�Ƴ�ʱ��)"
	   	        	;
			   		int autoIncKeyFromApi = tdxconnectdb.sqlUpdateStatExecute(sqlupdatestat);
	   	        }
	   	        differencebankuaiold = null;
	   	        
	   	        //��tmpstockcodesetnew�����еģ�tmpstockcodesetoldû�е�ѡ���������µģ�Ҫ�������ݿ�
   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
	   		    for (String stockcode : differencebankuainew) {

	   		    	String sqlinsertstat = "INSERT INTO ���ǻ۹�Ʊ�������Ӧ��(��Ʊ����,������,��ƱȨ��)\r\n" + 
	   		    						   "VALUES ( " 
	   		    						   	+ "'" + stockcode + "',"
	   		    						   	+ "'" + dzhbk +  "',"
	   		    						   	+ "10"
	   		    						   	+ ")"   
	   		    							;
	   				
	   				int autoIncKeyFromApi = tdxconnectdb.sqlInsertStatExecute(sqlinsertstat);
	   			}
	   		    differencebankuainew = null;
			}
		} catch (Exception e) { e.printStackTrace();	} 
		
		return;
	}
	/*
	 * 
	 */
	public Set<String> getDZHGaiNianBanKuaiGeGuSetFromDZHFiles(String dzhbk) 
	{
		String dzhfilepath = sysconfig.getDaZhiHuiBanKuaiFilePath ();
		String bkfilename = dzhbk + ".BLK";
		File dzhbkfile = new File(dzhfilepath + "/" + bkfilename);
		if (!dzhbkfile.exists() || dzhbkfile.isDirectory() || !dzhbkfile.canRead())   
		    return null;
		
		Set<String> dzhbkstock = new HashSet<> ();
		BufferedInputStream dis = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(dzhbkfile);
			dis = new BufferedInputStream(in);
			
			 int fileheadbytenumber = 4;
	         byte[] itemBuf = new byte[fileheadbytenumber];
	         dis.read(itemBuf, 0, fileheadbytenumber);
			 String fileHead = new String(itemBuf,0,fileheadbytenumber);
				
			 int sigbk = 12;
	         byte[] itemBuf2 = new byte[sigbk];
	         while (dis.read(itemBuf2, 0, sigbk) != -1) {
	         	   //�������
	         	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
	         	   String stockcode = gupiaoheader.substring(2);
	         	  dzhbkstock.add(stockcode);
	         	   
	         }
		} catch (FileNotFoundException e) {	e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();}  
		
		return dzhbkstock;
	}
	/*
	 * 
	 */
	public Set<String> getDZHGaiNianBanKuaiGeGuSetFromSQLite(String dzhbk) 
	{
		CachedRowSetImpl rsagu = null;
		try {
			 String sqlquerystat = "SELECT * FROM blocks WHERE  blockid = '" + dzhbk.trim() + "'"
		 			;
			rsagu = dzhconnectdb.dbcon.sqlQueryStatExecute(sqlquerystat);
			while(rsagu.next()) {
//				  Blob input = rsagu.getBlob("constituents");
				Object input = rsagu.getBinaryStream(2);
				      Object input2 = rsagu.getBinaryStream("constituents");
//				 BufferedInputStream dis = new BufferedInputStream(input);
//				 int fileheadbytenumber = 384;
//		         byte[] itemBuf = new byte[fileheadbytenumber];
//		         try {
//					dis.read(itemBuf, 0, fileheadbytenumber);
//					String fileHead = new String(itemBuf,0,fileheadbytenumber);
//					
//					int sigbk = 2813;
//		            byte[] itemBuf2 = new byte[sigbk];
//		            while (dis.read(itemBuf2, 0, sigbk) != -1) {
//		         	   //�������
//		         	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
//		            }
//				} catch (IOException e) {e.printStackTrace();} 
				 int i =0;
			
			}
		} catch (Exception e) { e.printStackTrace();
		} finally {
			try {	rsagu.close();	} catch (SQLException e) {	e.printStackTrace();}
			rsagu = null;
		}
		
		
         
        return null; 
         
	}
	/*
	 * 
	 */
	public Set<String> getDZHGaiNianBanKuaiGeGuSetInDb (String dzhbk)
	{
		CachedRowSetImpl rs = null; Set<String> tmpstockcodesetold = new HashSet<> ();
		try {
		    	String sqlquerystat = "SELECT ��Ʊ����  \r\n" + 
		    			"FROM ���ǻ۹�Ʊ�������Ӧ�� gnb\r\n" + 
		    			"WHERE gnb.`������` = '" + dzhbk + "'  AND ISNULL(gnb.`�Ƴ�ʱ��`)			"
						;
		    	rs = tdxconnectdb.sqlQueryStatExecute(sqlquerystat);
		    	
		        while(rs.next()) {  
		        	String stockcode = rs.getString("��Ʊ����");
		        	tmpstockcodesetold.add(stockcode);
		        }
		        
		    } catch(java.lang.NullPointerException e) { e.printStackTrace();
		    } catch (SQLException e) { e.printStackTrace();
		    } catch(Exception e) { e.printStackTrace();
		    } finally {
	    	if(rs != null)
				try { rs.close();rs = null;	} catch (SQLException e) {e.printStackTrace();}
	    } 
		
		return tmpstockcodesetold;
		
	}
	public void refreshDZHGaiNianBanKuaiFromExportFileToDatabase ()
	{
		Set<String> dzhbksetindb = getDZHBanKuaiNameSet ();
		
		File dzhbkfile = sysconfig.getDaZhiHuiBanKuaiExportFileName ();
		Set<String> dzhbksetinfile = new HashSet<> ();
		BufferedReader reader;
		String line;  
		try {
			reader = new BufferedReader(new FileReader(dzhbkfile.getAbsolutePath() ));
			line = reader.readLine();
			while (line != null) {
				List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);

           		try {
	            			String bkcode = tmplinelist.get(0);
	            			String bkname = tmplinelist.get(1);
	            			if(bkcode.startsWith("99")) {
	            				String sqlinsertstat = "INSERT INTO  ���ǻ۰���б�(���ID,�������) values ("
	        	   						+ " '" + bkcode.trim() + "'" + ","
	        	   						+ " '" + bkname.trim() + "'" 
	        	   						+ ")"
	        	   						+ " ON DUPLICATE KEY UPDATE "
	        	   						+ "������� =" + " '" + bkname.trim() + "'" 
	        	   						;
	            				int autoIncKeyFromApi = tdxconnectdb.sqlInsertStatExecute(sqlinsertstat);
        	   				
	            				dzhbksetinfile.add(bkcode);
	            			}
           		} catch (Exception e) {e.printStackTrace();	}
				
				// read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {e.printStackTrace(); }
		
		SetView<String> differencebankuaiold = Sets.difference( dzhbksetindb, dzhbksetinfile );
		if(differencebankuaiold.size() >0 ) { //˵����ĳЩ�����Ҫɾ��
			 for(String oldbkcode : differencebankuaiold) {
				 String sqldeletetstat = "DELETE  FROM  ���ǻ۰���б� WHERE "
						 				+ " ���ID =" + " '" + oldbkcode.trim() + "'" 
						 				;
	   			try {
					int autoIncKeyFromApi = tdxconnectdb.sqlDeleteStatExecute(sqldeletetstat);
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();	}

			 }
		 }
		 differencebankuaiold = null;
		 
		 return;
	}
	/*
	 * 
	 */
	public Set<String> getDZHBanKuaiNameSet () 
	{
		CachedRowSetImpl rsagu = null;
		Set<String> dzhbkset = new HashSet<> ();
		try {
			 String sqlquerystat = "select *  FROM ���ǻ۰���б�   "
		 			;
			rsagu = tdxconnectdb.sqlQueryStatExecute(sqlquerystat);
			while(rsagu.next()) {
				String dzhbk = rsagu.getString("���ID"); 
				dzhbkset.add(dzhbk);
			}
		} catch (Exception e) { e.printStackTrace();
		} finally {
			try {	rsagu.close();
			} catch (SQLException e) {	e.printStackTrace();}
			rsagu = null;
		}
		
		return dzhbkset;
	}
	/*
	 * 
	 */
	public List<BanKuai> getDZHBanKuaiList () 
	{
		CachedRowSetImpl rs = null;
		List<BanKuai> dzhbkset = new ArrayList<> ();
		try {
			 String sqlquerystat = "select *  FROM ���ǻ۰���б�   "
		 			;
			rs = tdxconnectdb.sqlQueryStatExecute(sqlquerystat);
			while(rs.next()) {
				String dzhbkcode = rs.getString("���ID"); 
				String dzhbkname = rs.getString("�������");
				
				BanKuai dzhbk = new BanKuai(dzhbkcode,dzhbkname, "DZH");
//				dzhbk.setSuoShuJiaoYiSuo(rs.getString("ָ������������"));
//				dzhbk.setBanKuaiLeiXing( rs.getString("�����������") );
//				dzhbk.setExporttogehpi(rs.getBoolean("����Gephi"));
				dzhbk.setImportdailytradingdata(rs.getBoolean("���뽻������"));
//				dzhbk.setShowinbkfxgui(rs.getBoolean("������"));
//				dzhbk.setShowincyltree(rs.getBoolean("��ҵ����"));
//				dzhbk.setExportTowWlyFile(rs.getBoolean("�ܷ����ļ�"));
				dzhbk.setImportBKGeGu(rs.getBoolean("���������"));

				dzhbkset.add(dzhbk);
			}
		} catch (Exception e) { e.printStackTrace();
		} finally {
			try {	rs.close();
			} catch (SQLException e) {	e.printStackTrace();}
			rs = null;
		}
		
		return dzhbkset;
	}
	/*
	 * 
	 */
	public Stock getDZHBanKuaiForAStock(Stock stock) 
	{
		if(stock.getGeGuCurSuoShuDZHSysBanKuaiList() != null && !stock.getGeGuCurSuoShuDZHSysBanKuaiList().isEmpty() )
			return  stock;
		
		BanKuaiAndStockTree treeofstkbk = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks() ;
		String stockcode = stock.getMyOwnCode();
		Set<BkChanYeLianTreeNode> stockbanks = new HashSet<>();

		String sqlquerystat =null;
		sqlquerystat=  "SELECT *  \r\n" + 
						"FROM ���ǻ۹�Ʊ�������Ӧ�� gnb\r\n" + 
						"WHERE gnb.`��Ʊ����` = '" + stockcode + "'  AND ISNULL(gnb.`�Ƴ�ʱ��`)			"
						;

		CachedRowSetImpl rs_gn = tdxconnectdb.sqlQueryStatExecute(sqlquerystat);
		try  {     
	        while(rs_gn.next()) {
	        	String bkcode = rs_gn.getString("������");
	        	Integer quanzhong = rs_gn.getInt("��ƱȨ��");
	        	BanKuai bk = (BanKuai)treeofstkbk.getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.DZHBK);
//	        	Stock tmpstock = (Stock)treeofstkbk.getSpecificNodeByHypyOrCode (stock.getMyOwnCode(),BkChanYeLianTreeNode.TDXGG);
				StockOfBanKuai bkofst = new StockOfBanKuai(bk,stock);
				bkofst.setStockQuanZhong (quanzhong);
				bk.addNewBanKuaiGeGu(bkofst);
	        	stockbanks.add(bk);
	        } 
	        
	    } catch(java.lang.NullPointerException e){	e.printStackTrace();
	    } catch(Exception e) {e.printStackTrace();
	    } finally {
	    	if(rs_gn != null) {	try {rs_gn.close();	} catch (SQLException e) {	e.printStackTrace();}
	    		rs_gn = null;   }	
	    }
//	    Collections.sort (stockbanks);
		stock.setGeGuCurSuoShuDZHSysBanKuaiList(stockbanks);
		
		return stock;
	}
	/*
	 * 
	 */
	public void updateBanKuaiOperationsSettings(BkChanYeLianTreeNode node, boolean importdailydata, boolean exporttogephi, 
			boolean showinbkfx,boolean showincyltree, boolean exporttowkfile, boolean importbkgg)
	{
		String sqlupdatestat = "UPDATE ���ǻ۰���б�  SET " +
							" ���뽻������=" + importdailydata  + "," +
//							" ����Gephi=" + exporttogephi +  ","  +
							" ������=" + showinbkfx +  ","  +
//							" ��ҵ����=" + showincyltree + ","  +
//							" �ܷ����ļ� = " + exporttowkfile + ","  +
							" ��������� = " + importbkgg +
							" WHERE ���ID='" + node.getMyOwnCode() + "'"
							;
   		try {
			int autoIncKeyFromApi = tdxconnectdb.sqlUpdateStatExecute(sqlupdatestat);
		} catch (MysqlDataTruncation e) {e.printStackTrace();
		} catch (SQLException e) {e.printStackTrace();}
	}
	/*
	 * 
	 */
	public BanKuai getDZHBanKuaiGeGuOfHyGnFg(BanKuai bankuai, LocalDate bkstartday, LocalDate bkendday)
	{
		String bkcode = bankuai.getMyOwnCode();

		String sqlquerystat =null;
		sqlquerystat=  "SELECT *  \r\n" + 
						"FROM ���ǻ۹�Ʊ�������Ӧ�� gnb\r\n" + 
						"WHERE gnb.`������` = '" + bkcode + "'  AND ISNULL(gnb.`�Ƴ�ʱ��`)			"
						;

		CachedRowSetImpl rs1 = tdxconnectdb.sqlQueryStatExecute(sqlquerystat);
		try  {     
	        while(rs1.next()) {
	        	String tmpstockcode = rs1.getString("��Ʊ����");

	        	Integer weight = rs1.getInt("��ƱȨ��");
				
				Boolean longtou;
				try{
					longtou = rs1.getBoolean("�����ͷ");
				} catch (java.sql.SQLException e) {
//					e.printStackTrace();
					longtou = false;
				}
				
				LocalDate joindate = null;
				try{
					joindate = rs1.getDate("����ʱ��").toLocalDate();
				} catch (java.sql.SQLException ex1) {
					continue; //
//					ex1.printStackTrace();
				}
				LocalDate leftdate;
				try {
					leftdate = rs1.getDate("�Ƴ�ʱ��").toLocalDate().with(DayOfWeek.FRIDAY); //���߶���������Ϊ����ģ��κθ����Ƴ�������������
				} catch (java.lang.NullPointerException e) {
					leftdate = LocalDate.parse("3000-01-01");
				}
				DateTime joindt= new DateTime(joindate.getYear(), joindate.getMonthValue(), joindate.getDayOfMonth(), 0, 0, 0, 0);
				DateTime leftdt = new DateTime(leftdate.getYear(), leftdate.getMonthValue(), leftdate.getDayOfMonth(), 0, 0, 0, 0);
				Interval joinleftinterval = null ;
				try {
					joinleftinterval = new Interval(joindt, leftdt);
				} catch (Exception e) {
//					e.printStackTrace();
					joinleftinterval = new Interval(leftdt,joindt );
				}
	        	
	        	if(bankuai.getStockOfBanKuai(tmpstockcode) != null ) {//�Ѿ�����
	        		StockOfBanKuai bkofst =  bankuai.getStockOfBanKuai(tmpstockcode);
					bkofst.setStockQuanZhong(weight);
					bkofst.setBkLongTou(longtou);
					bkofst.addInAndOutBanKuaiInterval(joinleftinterval);
	        	} else {
	        		BanKuai bk = (BanKuai)CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.DZHBK);
		        	Stock stock = (Stock)CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(tmpstockcode,BkChanYeLianTreeNode.TDXGG);
					StockOfBanKuai bkofst = new StockOfBanKuai(bk,stock);
					bkofst.setStockQuanZhong (weight);
					bkofst.addInAndOutBanKuaiInterval(joinleftinterval);
					bk.addNewBanKuaiGeGu(bkofst);
	        	}
	        } 
	        
	    } catch(java.lang.NullPointerException e){	e.printStackTrace();
	    } catch(Exception e) {e.printStackTrace();
	    } finally {
	    	if(rs1 != null) {	try {rs1.close();	} catch (SQLException e) {	e.printStackTrace();}
	    		rs1 = null;   }	
	    }
		
		return bankuai;
	}
}
