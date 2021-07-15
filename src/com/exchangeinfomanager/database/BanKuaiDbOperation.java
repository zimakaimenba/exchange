package com.exchangeinfomanager.database;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.URL;

import java.nio.charset.Charset;

import java.sql.SQLException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;

import org.apache.http.impl.client.HttpClientBuilder;

import org.apache.log4j.Logger;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.python.icu.util.TimeZone;

import com.exchangeinfomanager.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.ParseBanKuaiWeeklyFielGetStocksProcessor;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.nodejibenmian.NodeJiBenMian;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.TDXNodesXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItemForJFC;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;


import com.mysql.jdbc.MysqlDataTruncation;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.sun.rowset.CachedRowSetImpl;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;



public class BanKuaiDbOperation 
{
	public BanKuaiDbOperation() 
	{
		connectdb = ConnectDataBase.getInstance();
		sysconfig = new SetupSystemConfiguration();
	}
	
	private  ConnectDataBase connectdb;
	private  SetupSystemConfiguration sysconfig;
	private static Logger logger = Logger.getLogger(BanKuaiDbOperation.class);
	
	/*
	 * 
	 */
	public List<BanKuai> getTDXBanKuaiList(String jys)   
	{
		List<BanKuai> tmpsysbankuailiebiaoinfo = new ArrayList<> ();
		
		String sqlquerystat;
		if(!jys.toLowerCase().equals("all"))
			 sqlquerystat = "SELECT * FROM\r\n" + 
			 		"(\r\n" + 
			 		"SELECT *   FROM ͨ���Ű���б� WHERE ָ������������ = '" + jys + "') AS node\r\n" + 
			 		"LEFT   JOIN \r\n" + 
			 		"(SELECT * FROM ����Ʊռ����ֵ )\r\n" + 
			 		"AS nodezbextremelevel\r\n" + 
			 		"ON node.���ID = nodezbextremelevel.����\r\n" + 
			 		"\r\n" + 
			 		"LEFT   JOIN \r\n" + 
			 		"(SELECT ����, MIN(��������)  SHminjytime , MAX(��������)  SHmaxjytime fROM   ͨ���Ű��ÿ�ս�����Ϣ\r\n" + 
			 		"GROUP BY ����) shjyt \r\n" + 
			 		"\r\n" + 
			 		"ON shjyt.���� = node.���ID AND node.ָ������������ = 'SH'\r\n" + 
			 		"\r\n" + 
			 		"LEFT   JOIN \r\n" + 
			 		"(SELECT ����, MIN(��������)  SZminjytime , MAX(��������)  SZmaxjytime fROM   ͨ���Ž�����ָ��ÿ�ս�����Ϣ\r\n" + 
			 		"GROUP BY ����) szjyt \r\n" + 
			 		"\r\n" + 
			 		"ON szjyt.���� = node.���ID AND node.ָ������������='SZ'\r\n"  
					;
		else
			 sqlquerystat = 
						"SELECT * FROM\r\n" + 
						"(\r\n" + 
						"SELECT *   FROM ͨ���Ű���б� ) AS node\r\n" + 
						"LEFT   JOIN \r\n" + 
						"(SELECT * FROM ����Ʊռ����ֵ )\r\n" + 
						"AS nodezbextremelevel\r\n" + 
						"ON node.���ID = nodezbextremelevel.����\r\n" + 
						"\r\n" + 
						"LEFT   JOIN \r\n" + 
						"(SELECT ����, MIN(��������)  SHminjytime , MAX(��������)  SHmaxjytime fROM   ͨ���Ű��ÿ�ս�����Ϣ\r\n" + 
						"GROUP BY ����) shjyt \r\n" + 
						"\r\n" + 
						"ON shjyt.���� = node.���ID AND node.ָ������������ = 'SH'\r\n" + 
						"\r\n" + 
						"LEFT   JOIN \r\n" + 
						"(SELECT ����, MIN(��������)  SZminjytime , MAX(��������)  SZmaxjytime fROM   ͨ���Ž�����ָ��ÿ�ս�����Ϣ\r\n" + 
						"GROUP BY ����) szjyt \r\n" + 
						"\r\n" + 
						"ON szjyt.���� = node.���ID AND node.ָ������������='SZ'\r\n"  
						;
		
		CachedRowSetImpl rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	        while(rs.next()) {
	        	BanKuai tmpbk = new BanKuai (rs.getString("���ID"),rs.getString("�������"),"TDX" );
	        	String zhishujys = rs.getString("ָ������������");
	        	tmpbk.getNodeJiBenMian().setSuoShuJiaoYiSuo(zhishujys);
	        	tmpbk.setBanKuaiLeiXing( rs.getString("�����������") );
	        	tmpbk.getBanKuaiOperationSetting().setImportBKGeGu(rs.getBoolean("���������"));
	        	tmpbk.getBanKuaiOperationSetting().setExporttogehpi(rs.getBoolean("����Gephi"));
	        	tmpbk.getBanKuaiOperationSetting().setImportdailytradingdata(rs.getBoolean("���뽻������"));
	        	tmpbk.getBanKuaiOperationSetting().setShowinbkfxgui(rs.getBoolean("������"));
	        	tmpbk.getBanKuaiOperationSetting().setShowincyltree(rs.getBoolean("��ҵ����"));
	        	tmpbk.getBanKuaiOperationSetting().setExportTowWlyFile(rs.getBoolean("�ܷ����ļ�"));
	        	tmpbk.getBanKuaiOperationSetting().setBanKuaiLabelColor(rs.getString("DefaultCOLOUR"));
	        	tmpbk.getNodeJiBenMian().setNodeCjlZhanbiLevel (rs.getDouble("�ɽ���ռ������"), rs.getDouble("�ɽ���ռ������"));
	        	tmpbk.getNodeJiBenMian().setNodeCjeZhanbiLevel (rs.getDouble("�ɽ���ռ������"), rs.getDouble("�ɽ���ռ������"));
	        	tmpbk.getNodeJiBenMian().setIsCoreZhiShu (rs.getBoolean("����ָ��"));
	        	
	        	if(zhishujys.equalsIgnoreCase("SH")) {
	        		try {	LocalDate shmaxdate = rs.getDate("SHmaxjytime").toLocalDate();
	        				tmpbk.getShuJuJiLuInfo().setJyjlmaxdate(shmaxdate);
	        		} catch(java.lang.NullPointerException e) {
	        			tmpbk.getShuJuJiLuInfo().setJyjlmaxdate(null);
	        		}
	        		try {	LocalDate shmindate = rs.getDate("SHminjytime").toLocalDate();
	        				tmpbk.getShuJuJiLuInfo().setJyjlmindate(shmindate);
	        		} catch(java.lang.NullPointerException e) {
	        			tmpbk.getShuJuJiLuInfo().setJyjlmaxdate(null);
	        		}
	        	} else if(zhishujys.equalsIgnoreCase("SZ")) {
	        		try {	LocalDate szmaxdate = rs.getDate("SZmaxjytime").toLocalDate();
	        				tmpbk.getShuJuJiLuInfo().setJyjlmaxdate(szmaxdate);
		    		} catch(java.lang.NullPointerException e) {
		    				tmpbk.getShuJuJiLuInfo().setJyjlmaxdate(null);
		    		}
		    		try {	LocalDate szmindate = rs.getDate("SZminjytime").toLocalDate();
	        				tmpbk.getShuJuJiLuInfo().setJyjlmindate(szmindate);
		    		} catch(java.lang.NullPointerException e) {
		    				tmpbk.getShuJuJiLuInfo().setJyjlmaxdate(null);
		    		}
	        	}
	        	
	        	tmpsysbankuailiebiaoinfo.add(tmpbk);
	        }
	    } catch(java.lang.NullPointerException e){ e.printStackTrace();
	    } catch (SQLException e) {e.printStackTrace();
	    } catch(Exception e){e.printStackTrace();
	    } finally {	if(rs != null)	try {rs.close();rs = null;} catch (SQLException e) {e.printStackTrace();}
	    } 
	    
	    return tmpsysbankuailiebiaoinfo;
	    

	}
	/*
	 * 
	 */
	public BanKuai getBanKuaiBasicInfo(BanKuai bk)
	{
		String bkcode = bk.getMyOwnCode();
		CachedRowSetImpl rsagu = null;
		try {
			 String sqlquerystat = "select �������, ����ʱ��,����������,������Ϣʱ��,������Ϣ,ȯ������ʱ��,ȯ����������,����ؼ��ͻ�,����ؼ���������,�ͻ�,�������� "
			 		+ " FROM ͨ���Ű���б�  B "
//					+ " RIGHT JOIN ͨ���Ű���罻��ϵ�� F ON (F.����� = m.���ID OR F.����� = m.���ID)  "
			 		+ "WHERE B.���ID = '" + bkcode +"' \r\n"  
		 			;
			logger.debug(sqlquerystat);
			rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);
			while(rsagu.next())
				setSingleNodeInfo (bk,rsagu);
			
			
			sqlquerystat = "SELECT * FROM ͨ���Ű���罻��ϵ�� F"
					+ " WHERE (F.����� = '" + bkcode + "' OR F.����� = '" + bkcode + "')"
					;
			rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);
			while(rsagu.next()) {
				String friendcodeleft = rsagu.getString("�����"); 
				String friendcoderight = rsagu.getString("�����");
				Boolean relationship = rsagu.getBoolean("��ϵ");
				if(relationship == null)					relationship = true;
				if(!friendcodeleft.equals(bkcode) && relationship )					    bk.addSocialFriendsPostive(friendcodeleft);
				if(!friendcoderight.equals(bkcode) && relationship )					bk.addSocialFriendsPostive(friendcoderight);
				if(!friendcodeleft.equals(bkcode) && !relationship )					bk.addSocialFriendsNegtive(friendcodeleft);
				if(!friendcoderight.equals(bkcode) && !relationship )					bk.addSocialFriendsNegtive(friendcoderight);	
			}
		} catch (Exception e) { e.printStackTrace();
		} finally {
			try {	rsagu.close();	} catch (SQLException e) {	e.printStackTrace();}
			rsagu = null;
		}
		
		return bk;
	}
	
	/*
	 * 
	 */
	public BkChanYeLianTreeNode getStockBasicInfo(BkChanYeLianTreeNode stock) 
	{
		String stockcode = stock.getMyOwnCode();
		CachedRowSetImpl rsagu = null;
		try {
			 String sqlquerystat= "SELECT * FROM A��   WHERE ��Ʊ���� =" +"'" + stockcode +"'" ;	
			rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);
			while(rsagu.next())
				setSingleNodeInfo (stock,rsagu);

		} catch (Exception e) {	e.printStackTrace();
		} finally {
			try {rsagu.close();} catch (SQLException e) {e.printStackTrace();}
			rsagu = null;
		}
		
		return stock;
	}
	/*
	 * ��ȡ�û������node�Ļ�����Ϣ��node�����ǰ��Ҳ�����Ǹ���
	 */
	public List<BkChanYeLianTreeNode> getNodesBasicInfo(String nodecode) 
	{
		List<BkChanYeLianTreeNode> nodenamelist = new ArrayList<BkChanYeLianTreeNode> ();
		CachedRowSetImpl rs = null;
		
		try {
			String sqlquerystat = "select ��Ʊ����, 'a��' as tablename, 'gegu' as type from a�� where  ��Ʊ���� = '" + nodecode + "'" + 
					" union \r\n" + 
					" select �������, 'ͨ���Ű���б�' as tablename , 'bankuai' as type from ͨ���Ű���б� where ���ID = '" + nodecode + "'" 
//					+" union\r\n" + 
//					" select �������, 'ͨ���Ž�����ָ���б�' as tablename, 'zhishu' as type from ͨ���Ž�����ָ���б� where ���ID = '" + nodecode + "'"
					;
				logger.debug(sqlquerystat);
		    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	
		        while(rs.next()) {  
		        	BkChanYeLianTreeNode tmpnode = null;
		        	if(rs.getString("type").equals("gegu")) {
		        		tmpnode = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXGG);
		        		this.getStockBasicInfo(tmpnode );
		        	} else {
		        		tmpnode = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXBK);
		        		this.getBanKuaiBasicInfo( (BanKuai)tmpnode );
		        	}
		        	nodenamelist.add(tmpnode);
		        }
		       
		    } catch(java.lang.NullPointerException e){e.printStackTrace();
		    } catch (SQLException e) {e.printStackTrace();
		    } catch(Exception e){e.printStackTrace();
		    } finally {
		    	if(rs != null)	try {rs.close();rs = null;} catch (SQLException e) {e.printStackTrace();}
		    }
		
		return nodenamelist;
	}

	private void setSingleNodeInfo(BkChanYeLianTreeNode tmpnode, CachedRowSetImpl rs) 
	{
		if(tmpnode.getType() == 6) {
			try {
				String stockname = rs.getString("��Ʊ����").trim();
				tmpnode.setMyOwnName(stockname);
			} catch(java.lang.NullPointerException ex1) {tmpnode.setMyOwnName( " ");
			} catch (Exception ex) {}
			try {
				Boolean stockname = rs.getBoolean("������");
				//����Ӧ����������״̬��Ŀǰû��ô��Ҫ��������ʱ������
			} catch(java.lang.NullPointerException ex1) {Boolean stockname = false;
			} catch (Exception ex) {}
		} else {
			try {
				String bankuainame = rs.getString("�������").trim();
				tmpnode.setMyOwnName(bankuainame);
			} catch(java.lang.NullPointerException ex1) {tmpnode.setMyOwnName(" ");
			} catch (Exception ex) {}
		}
		
		NodeJiBenMian nodejbm = tmpnode.getNodeJiBenMian();
		try {
			java.util.Date gainiantishidate = rs.getDate("����ʱ��");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(gainiantishidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			nodejbm.setGainiantishidate(ldgainiantishidate); 
		} catch(java.lang.NullPointerException ex1) {nodejbm.setGainiantishidate(null);
		} catch (Exception e) {}
		
		try {
			String gainiantishi = rs.getString("����������");
			nodejbm.setGainiantishi(gainiantishi);
		} catch(java.lang.NullPointerException ex1) {nodejbm.setGainiantishi(" ");			
		} catch (Exception e) {e.printStackTrace();}
		
		try 
		{
			Date quanshangpingjidate = rs.getDate("ȯ������ʱ��");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(quanshangpingjidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			nodejbm.setQuanshangpingjidate(ldgainiantishidate);
		} catch(java.lang.NullPointerException ex1) {nodejbm.setQuanshangpingjidate(null);
		} catch (Exception e) {e.printStackTrace();}
	
		try	{
			String quanshangpingji = rs.getString("ȯ����������").trim();
			nodejbm.setQuanshangpingji(quanshangpingji);
		} catch(java.lang.NullPointerException ex1) {nodejbm.setQuanshangpingji("");			
		} catch (Exception e) {e.printStackTrace();}
		
		try	{
			Date fumianxiaoxidate = rs.getDate("������Ϣʱ��");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(fumianxiaoxidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			nodejbm.setFumianxiaoxidate(ldgainiantishidate);
		} catch(java.lang.NullPointerException ex1) {nodejbm.setFumianxiaoxidate(null);
		} catch(Exception ex2) {ex2.printStackTrace();}

		try	{
			String fumianxiaoxi = rs.getString("������Ϣ").trim();
			nodejbm.setFumianxiaoxi(fumianxiaoxi);
		} catch(java.lang.NullPointerException ex1) {nodejbm.setFumianxiaoxi("");			
		} catch (Exception e) {e.printStackTrace();}

	}
	/*
	 * ���� ͬ��ͨ������ص���Ϣ  ����ͨ���Ŷ���İ����Ϣ �����������ҵ�����ָ�� ���
	 */
	public File refreshTDXSystemBanKuai ()
	{
		File tmpreportfolder = Files.createTempDir(); 
		File tmprecordfile = new File(tmpreportfolder + "ͬ��ͨ����ϵͳ��鱨��.txt");

		//����ͨ����ϵͳ���а����Ϣ���ڸ����У����µĴ������ݿ⣬
		this.refreshTDXZhiShuShangHaiLists (tmprecordfile); //�Ϻ�ָ��
		this.refreshTDXZhiShuShenZhenLists (tmprecordfile); //����ָ��
		this.refreshTDXAllBanKuaiToSystem(tmprecordfile); //��880��飬����һЩ��Ϣ
		 
		//ͬ����ذ�������Ϣ
		this.refreshTDXFengGeBanKuaiToGeGu(tmprecordfile); //���
		this.refreshTDXGaiNianBanKuaiToGeGu(tmprecordfile); //����
		this.refreshTDXHangYeBanKuaiToGeGu(tmprecordfile); //��ҵ
		this.refreshTDXZhiShuBanKuaiToGeGu(tmprecordfile); //ͨ����ָ���͹�Ʊ��Ӧ��ϵ

		return tmprecordfile;
	}
	/*
	 * ���ǻ۹ɱ��ļ�������Ŀǰû���õ�
	 */
//	public File refreshDZHStockGuQuan() 
//	{
////		File file = new File(sysconfig.getDZHStockGuQuanFile() );
//		File file = new File("E:/stock/dzh365/Download/PWR/full_sh.PWR");
//		 if(!file.exists() ) {
//			 return null;
//		 }
//		 
//		 BufferedInputStream dis = null;
//		 FileInputStream in = null;
//		 try {
//			    in = new FileInputStream(file);  
//			    dis = new BufferedInputStream(in);
//			    
//			    
//			    int fileheadbytenumber = 12;
//	            byte[] itemBuf = new byte[fileheadbytenumber];
//	            dis.read(itemBuf, 0, fileheadbytenumber); 
//	            String fileHead =new String(itemBuf,0,fileheadbytenumber);  
//	            logger.debug(fileHead);
//	            
//	            while(true) {
//	            	int sigbk = 16;
//		            byte[] itemBuf2 = new byte[sigbk];
////		            int i=0;
//		            dis.read(itemBuf2, 0, sigbk) ;
//		            String stockcode = new String(itemBuf2, 0, sigbk );
//		            logger.debug(stockcode);
//		            
//		            int sigbk2 = 20;
//		            byte[] itemBuf3 = new byte[sigbk2];
////		            int i=0;
//		            dis.read(itemBuf3, 0, sigbk2) ;
//		            String stockdate = new String(itemBuf3, 0, sigbk2 );
//		            logger.debug(stockdate);
		            
//		            sigbk2 = 4;
//		            itemBuf3 = new byte[sigbk2];
////		            int i=0;
//		            dis.read(itemBuf3, 0, sigbk2) ;
//		            String stocknext = new String(itemBuf3, 0, sigbk2 );
//		            logger.debug(stocknext);
//		            
//		            sigbk2 = 4;
//		            itemBuf3 = new byte[sigbk2];
////		            int i=0;
//		            dis.read(itemBuf3, 0, sigbk2) ;
//		            stocknext = new String(itemBuf3, 0, sigbk2 );
//		            logger.debug(stocknext);
//		            
//		            sigbk2 = 4;
//		            itemBuf3 = new byte[sigbk2];
////		            int i=0;
//		            dis.read(itemBuf3, 0, sigbk2) ;
//		            stocknext = new String(itemBuf3, 0, sigbk2 );
//		            logger.debug(stocknext);
//		            
//		            sigbk2 = 4;
//		            itemBuf3 = new byte[sigbk2];
////		            int i=0;
//		            dis.read(itemBuf3, 0, sigbk2) ;
//		            stocknext = new String(itemBuf3, 0, sigbk2 );
//		            logger.debug(stocknext);

//	            }
//			    
//		 } catch (Exception e) {
//			 e.printStackTrace();
//			 return null;
//		 } finally {
//			 try {
//				in.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//             try {
//				dis.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}     
//		 }
		 
		 
		 
//		return null;
//	}
	/*https://www.55188.com/thread-9111311-1-1.html
	 * ͨ���ŵĸ���������ɶ�Ӧ
	 */
	private int refreshTDXGaiNianBanKuaiToGeGu (File tmprecordfile)
	{
		File file = new File(sysconfig.getTDXGaiNianBanKuaiToGeGuFile() );
		 if(!file.exists() ) {
			 try {	Files.append( "û�з���" +  sysconfig.getTDXGaiNianBanKuaiToGeGuFile() +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			 } catch (IOException e) {e.printStackTrace();}
			 return -1;
		 }
		 
		 int addednumber =0;BufferedInputStream dis = null;FileInputStream in = null;
		 try {
			    in = new FileInputStream(file);  
			    dis = new BufferedInputStream(in);
               
           // ��Ʊ�����Ϣ�ļ�ͷ��ʽ��T0002/hq_cache/block.dat block_fg.dat block_gn.dat block_zs.dat
		   // �ļ�ͷ��384�ֽ�
//               struct TTDXBlockHeader
//               {
//                   char         szVersion[64];        // 0, Registry ver:1.0 (1999-9-28)
//                   int            nIndexOffset;            // 64, 0x00000054
//                   int            nDataOffset;            // 68, 0x00000180
//                   int            nData1;                // 72, 0x00000003
//                   int            nData2;                // 76, 0x00000000
//                   int            nData3;                // 80, 0x00000003
//               };
               int fileheadbytenumber = 384;
               byte[] itemBuf = new byte[fileheadbytenumber];
               dis.read(itemBuf, 0, fileheadbytenumber); 
               String fileHead =new String(itemBuf,0,fileheadbytenumber);  

               //��������2�ֽ�
               dis.read(itemBuf, 0, 2); 
               String bknumber =new String(itemBuf,0,2);  
               
			 
               //��������ݴ洢�ṹ�����������Ŀ���δ�ţ�
//               ��һ��������ʼλ��Ϊ0x182h��
//               ÿ�����ռ�ݵĴ洢�ռ�Ϊ2813���ֽڣ�ÿ�����������400ֻ��Ʊ��(2813 -9 - 2 - 2) / 7 =  400
//               ������ƣ�9�ֽ�
//               �ð������ĸ��ɸ�����2�ֽ�
//               ������2�ֽ�
//               �ð���¸��ɴ����б�������ţ�ֱ������Ϊ�գ�
//                   ���ɴ��룺7�ֽ�
//               struct TTDXBlockRecord
//               {
//                   char         szName[9];
//                   short        nCount;
//                   short        nLevel;
//                   char         szCode[400][7];
//               };
               int sigbk = 2813;
               byte[] itemBuf2 = new byte[sigbk];
               Files.append("��ʼ����ͨ���Ź�Ʊ�����Ӧ��Ϣ:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   //�������
            	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
            	   CachedRowSetImpl rspd = null; 
	   			   try {
	   			    	String sqlquerystat = "SELECT *  FROM ͨ���Ű���б� WHERE �������='"  + gupiaoheader + "'";
	   			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			    	String bkcode = null;
	   			        while(rspd.next())  
	   			        	bkcode = rspd.getString("���ID");
	   			        
	   			        	BkChanYeLianTreeNode bankuai = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
		   			        if( bankuai != null && !((BanKuai)bankuai).getBanKuaiOperationSetting().isImportBKGeGu()   ) 
		            		   continue;
	   			    } catch(java.lang.NullPointerException e){ e.printStackTrace();
	   			    } catch (SQLException e) {e.printStackTrace();
	   			    } catch(Exception e){e.printStackTrace();
	   			    } finally {
	   			    	if(rspd != null) try {rspd.close();rspd = null; } catch (SQLException e) {e.printStackTrace();}
	   			    }
            	   
            	   //������
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   Set<String> tmpstockcodesetnew = new HashSet<>(tmpstockcodestr);

                   //�����ݿ��ж�����ǰ�ð��ĸ���
                   Set<String> tmpstockcodesetold = new HashSet<>();
                   CachedRowSetImpl rs = null;
	   			    try {
	   			    	String sqlquerystat = "SELECT gnbk.��Ʊ����  FROM ��Ʊͨ���Ÿ������Ӧ�� gnbk, ͨ���Ű���б� bklb"
		    						+ " WHERE  gnbk.������ = bklb.`���ID`"
		    						+ " AND bklb.`�������` = '"  + gupiaoheader + "'"
		    						+ " AND ISNULL(gnbk.�Ƴ�ʱ��)"
		    						;
	   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
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
	   			    
	   			 //�� tmpstockcodesetold �����еģ�tmpstockcodesetnewû�е�ѡ�������Ǿɵģ�Ҫ�����ݿ���ɾ��
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
		   	        	String sqlupdatestat = "UPDATE ��Ʊͨ���Ÿ������Ӧ�� JOIN ͨ���Ű���б�"
       							+ "  ON  ��Ʊͨ���Ÿ������Ӧ��.`��Ʊ����` = " + "'" + str.trim() + "'"
       							+ "  AND isnull(�Ƴ�ʱ��)"
       							+ "  and  ��Ʊͨ���Ÿ������Ӧ��.`������` = ͨ���Ű���б�.`���ID` "
       							+ "  and ͨ���Ű���б�.`�������`=" + "'" + gupiaoheader + "'"
       							+ "  SET �Ƴ�ʱ�� = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
       							;
				   		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   	        }
		   	     differencebankuaiold = null;
		   	     
	   		        //��tmpstockcodesetnew�����еģ�tmpstockcodesetoldû�е�ѡ���������µģ�Ҫ�������ݿ�
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   		    	String sqlinsertstat = "insert into ��Ʊͨ���Ÿ������Ӧ��(��Ʊ����,������,��ƱȨ��)"
								+ "	  SELECT '" + str + "', ͨ���Ű���б�.`���ID`, 10 "
								+ " FROM ͨ���Ű���б�  where ͨ���Ű���б�.`�������` = '" + gupiaoheader + "'"   
								;
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				addednumber ++;
		                Files.append("���룺" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   		 differencebankuainew = null;
               }
		 } catch (Exception e) { e.printStackTrace();return -1;
		 } finally {
			 try {in.close();  } catch (IOException e) { e.printStackTrace();}
             try {dis.close(); } catch (IOException e) { e.printStackTrace();}     
		 }
		
		 return addednumber;
	}
	/*
	 * ����880ָ����飬��һЩ��TDX�����ļ����ж�����Ϣ������һ��
	 */
	private boolean refreshTDXAllBanKuaiToSystem (File tmprecordfile) 
	{
		FileReader tongdaxinfile = null;
		try {
			tongdaxinfile = new FileReader(sysconfig.getTDXSysAllBanKuaiFile() );
		} catch (FileNotFoundException e) {e.printStackTrace();}
		BufferedReader bufr = new BufferedReader (tongdaxinfile);
		
//		Map<String,List<String>> tmpsysbkmap = new HashMap<String, List<String>> ();
		String line = null;  
        try {
			while((line = bufr.readLine()) != null) {
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //���ɰ��|880232|3|1|0|32
					String newbkcode = tmpbkinfo.get(1);
					String newbkname = tmpbkinfo.get(0);
		        	String newbktdxswcode = tmpbkinfo.get(5);
		        	String bkcjs = "sh";
					
					String sqlinsertstat = "INSERT INTO  ͨ���Ű���б�(���ID,��ӦTDXSWID,�������,ָ������������) values ("
	   						+ " '" + newbkcode.trim() + "'" + ","
	   						+ " '" + newbktdxswcode.trim() + "'" + ","
	   						+ " '" + newbkname.trim() + "'" + ","
	   						+ " '" + bkcjs.trim() + "'"
	   						+ ")"
	   						+ " ON DUPLICATE KEY UPDATE "
	   						+ " ��ӦTDXSWID =" + " '" + newbktdxswcode.trim() + "'" + "," 
	   						+ " �������=" + " '" + newbkname.trim() + "'" + ","
	   						+ " ָ������������ = " + " '" + bkcjs.trim() + "'" 
	   						;
	   				try {
						int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
					} catch (MysqlDataTruncation e) {e.printStackTrace();
					} catch (SQLException e) {e.printStackTrace();} 
			}
		} catch (IOException e) {e.printStackTrace();
		} finally {
			try {bufr.close();} catch (IOException e) {e.printStackTrace();}
			try {tongdaxinfile.close();} catch (IOException e) {e.printStackTrace();}
		}
        
        try {
			Files.append("��ʼ����ͨ���Ű����Ϣ:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		} catch (IOException e3) {e3.printStackTrace();}
        
		return true;
	}
	/*
	 * ͨ���ŵ���ҵ����ɶ�Ӧ�ļ�
	 * ����ļ���
		incon.dat                        ֤�����ҵ��ͨ��������ҵ��������ҵ��������Ϣ
		T0002/hq_cache/block.dat         һ����
		T0002/hq_cache/block_gn.dat      ������
		T0002/hq_cache/block_fg.dat       �����
		T0002/hq_cache/block_zs.dat       ָ�����:
		T0002/hq_cache/tdxhy.cfg             ÿ����Ʊ��Ӧͨ������ҵ��������ҵ
		T0002/hq_cache/tdxzs.cfg             ���ָ�������ְ������һ���ֶ�ӳ�䵽incon.dat��TDXNHY��SWHY
		T0002/blocknew/blocknew.cfg        �Զ������Ҫ�����ļ�
		
		��ҵ�����������֤�����ҵ��������ҵ��ͨ��������ҵ
		��ҵ���ļ���incon.dat���ж��塣�ļ���ʽ��
		1) �ļ����������ҵ���ࣺ
			a) ֤�����ҵ����ͷ#ZJHHY������######
			b) ������ҵ����ͷ#SWHY������######
			c) ͨ��������ҵ����ͷ#TDXNHY������######
		2) ÿ�������У�ÿһ�а���һ��ϸ����ҵ�Ĵ�������ƣ��ԡ�|���ָ�
			a) ֤�����ҵ��һ������ΪA~M����������A99����������ΪA9999
			b) ������ҵ��һ������Ϊ990000����������Ϊ999900����������Ϊ999999
			c) ͨ��������ҵ��һ������ΪT99����������ΪT9999����������ΪT999999
	 */
	private int refreshTDXHangYeBanKuaiToGeGu (File tmprecordfile)
	{
		File fileHyTDm = new File(sysconfig.getTDXHangYeToDaiMaFile() );  //"incon.dat";
		 if(!fileHyTDm.exists() ) {
			 logger.debug("File not exist");
			 return -1;
		 }
		 File fileHyTGg = new File(sysconfig.getTDXHangYeBanKuaiToGeGuFile() );  //"T0002/hq_cache/" + "tdxhy.cfg";
		 if(!fileHyTGg.exists() ) {
			 logger.debug("File not exist");
			 return -1;
		 }
		 
		 //�Ѱ��Ͷ�Ӧ�İ��������map
		 HashMap<String,String> hydmmap = new HashMap<String,String> (); 
		 int allupdatednum = 0;
		 FileReader fr = null;
		 BufferedReader  bufr = null; 
		 try {
			 fr = new FileReader(sysconfig.getTDXHangYeToDaiMaFile());
			 bufr = new BufferedReader (fr);
			
			String line = null;
			int i=0;
			do {
			} while ( !(line = bufr.readLine() ).equals("#TDXNHY") );
			
			while ( !(line = bufr.readLine() ).equals("######") ) {  //��ҵ���ƺ�T��ͷ�����Ӧ��ϵ
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //T01|��Դ  T010101|ú̿����
				hydmmap.put(tmpbkinfo.get(0).trim(), tmpbkinfo.get(1).trim());
			}
			
            Files.append("��ʼ����ͨ���Ź�Ʊ��ҵ��Ӧ��Ϣ:" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			fr = new FileReader(sysconfig.getTDXHangYeBanKuaiToGeGuFile()); //ͨ���Ÿ��ɺͰ��Ķ�Ӧ��ϵ
			bufr = new BufferedReader (fr);
			while((line = bufr.readLine()) != null) {
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //0|000001|T1001|440101
				String stockcode = tmpbkinfo.get(1).trim();
				String stockbkcode = tmpbkinfo.get(2).trim(); //T��ͷ�Ĵ���,����880��ͷ��
				String stockbkname = null;
				 
				try {
					stockbkname = hydmmap.get(stockbkcode).trim();
				 }catch(java.lang.NullPointerException e){
					stockbkname = "δ֪";
				}
				
				 
				//�������ݿ����ҳ��ù�Ʊ���Ƚ���ҵ�������һ���ɸ��£������ͬ�Ͳ�����
				CachedRowSetImpl rs = null;
				try {
   			    	String sqlquerystat = "SELECT *  FROM ��Ʊͨ������ҵ����Ӧ��  WHERE  ��Ʊ���� = " 
   			    							+ "'"  + stockcode + "'"
//   			    							+ " AND ��ӦTDXSWID=" +  "'" + stockbkcode + "'"
   			    							+ " AND isnull(�Ƴ�ʱ��)"
   			    							;
   			    	logger.debug(sqlquerystat);
   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   			    	
   			        rs.last(); 
   			        int rows = rs.getRow();  //���ɺ���ҵ��һ��һ�ģ�����д���1��������˵��������,�����ǹ�ȥͬ�����������⣬����Ҫ����
   			        rs.first();  
   			        if(rows == 0) { //�ù�Ʊ�������������ݿ��У�Ҫ���
//   			        	String sqlinsertstat = "INSERT INTO  ��Ʊͨ������ҵ����Ӧ��(��Ʊ����,��ҵ���,��ӦTDXSWID,��ƱȨ��) values ("
//		   						+ "'" + stockcode.trim() + "'" + ","
//		   						+ "'" + stockbkname.trim() + "'"  + ","
//		   						+ "'" + stockbkcode.trim() + "'" + ","
//		   						+ 10
//		   						+ ")"
//		   						;
   			        	String sqlinsertstat =  "insert into ��Ʊͨ������ҵ����Ӧ��(��Ʊ����,������,��ӦTDXSWID,��ƱȨ��)"
												+ " SELECT '" + stockcode.trim() + "', ͨ���Ű���б�.`���ID`,'" + stockbkcode + "',10 "
												+ " FROM ͨ���Ű���б�  where ͨ���Ű���б�.`��ӦTDXSWID` = '" + stockbkcode + "'"
												;
		   				logger.debug(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   	            Files.append("���룺" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   				
		   				allupdatednum++;
   			        } else if(rows == 1) { //����
   			        	String stockbkanameindb = rs.getString("��ӦTDXSWID");
   			        	if( !stockbkanameindb.equals(stockbkcode) ) { //��һ��˵������б仯���ϵİ������Ƴ�ʱ�䣬ͬʱ����һ������Ϣ����Ӧ�µİ��
   			        		checkStockHangYeNewInfo (stockcode,stockbkanameindb,stockbkcode);
   			        		//�Ȱ��ϼ�¼ �Ƴ�ʱ�����
//	   			     		String sqlupdatestat = "UPDATE  ��Ʊͨ������ҵ����Ӧ��  SET " 
//	   			 				+ " �Ƴ�ʱ�� =" + "'" +  sysconfig.formatDate(new Date() ) + "'" + ","
//	   			 				+ " WHERE ��Ʊ���� = " + "'" + stockcode.trim() + "'" 
//	   			 				+ " AND ��ӦTDXSWID=" + stockbkanameindb 
//	   			 				+ " AND isnull(�Ƴ�ʱ��)"
//	   			 				;
   			        		allupdatednum ++;
//   			        		String sqlupdatestat = "UPDATE ��Ʊͨ������ҵ����Ӧ�� "
//   	       										+ "  SET �Ƴ�ʱ�� = " + "'" +  LocalDate.now().toString()  + "'"
//   	       										+ "  WHERE  ��Ʊͨ������ҵ����Ӧ��.`��Ʊ����` = " + "'" + stockcode.trim() + "'"
//   	       										+ "  AND isnull(�Ƴ�ʱ��)"
//   	       										+ "  AND ��Ʊͨ������ҵ����Ӧ��.`��ӦTDXSWID` = " + "'" + stockbkanameindb + "'"
//   	       							;
//		   			 		logger.debug(sqlupdatestat);
//		   			 		@SuppressWarnings("unused")
//		   			 		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   			 		
		   			 		
		   			 		//������һ����¼
//			   			 	String sqlinsertstat = "INSERT INTO  ��Ʊͨ������ҵ����Ӧ��(��Ʊ����,��ҵ���,��ӦTDXSWID,��ƱȨ��) values ("
//			   						+ "'" + stockcode.trim() + "'" + ","
//			   						+ "'" + stockbkname.trim() + "'"  + ","
//			   						+ "'" + stockbkcode.trim() + "'" + ","
//			   						+ 10
//			   						+ ")"
//			   						;
//		   			 		String sqlinsertstat =  "insert into ��Ʊͨ������ҵ����Ӧ��(��Ʊ����,������,��ӦTDXSWID,��ƱȨ��)"
//								+ " SELECT '" + stockcode.trim() + "', ͨ���Ű���б�.`���ID`,'" + stockbkcode + "',10 "
//								+ " FROM ͨ���Ű���б�  where ͨ���Ű���б�.`��ӦTDXSWID` = '" + stockbkcode + "'"
//								;
//			   				logger.debug(sqlinsertstat);
//			   				autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//		   	            Files.append("���룺" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//			   	        Files.append("���£�" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
   			        	}
   			        } else if(rows > 1) { //ͨ�����и��ɺ���ҵ��һ��һ�ģ�����д���1��������˵��������,�����ǹ�ȥͬ�����������⣬����Ҫ����
   			        	JOptionPane.showMessageDialog(null,"����"+ stockcode + "��"+ rows + "����ҵ��飡��������һ���ԣ�");
//   			        	do {
//   			        		//����д����е�ͬʱ����и��£��ж��ĸ��Ǵ����У��ĸ�����Ҫ���µİ�飿��ô�㣿 2017.12.14
//   			        		//����ǳ�һ���Ի������û�ѡ�����ڳ�һ��֪ͨ�����������������취,�����û����ֶ������ݿ�����ģ���Ϊ��ʱ���漰һ�����ƣ������޸���Ͳ��ٳ����ˣ�Ͷ�����������
//   			        		String bkcode = rs.getString("������");
//   			        		String stockbkanameindb = rs.getString("��ӦTDXSWID");
//   			        		int action = JOptionPane.showConfirmDialog(null, stockcode + "��" + bkcode + "��Ϣ�Ƿ���Ҫɾ����","����"+ stockcode + "��"+ rows + "����ҵ��飡��������һ���ԣ�", JOptionPane.YES_NO_OPTION);
//   							if(0 == action) {//�û�ͬ����ɾ��
//   								String sqldeletestat = "DELETE  FROM ��Ʊͨ������ҵ����Ӧ��"
//   													+ " WHERE ��Ʊͨ������ҵ����Ӧ��.`��Ʊ����` = " + "'" + stockcode.trim() + "'"
//   													+ " AND ��Ʊͨ������ҵ����Ӧ��.`��ӦTDXSWID` = " + "'" + stockbkanameindb + "'" 
//   													+ " AND ��Ʊͨ������ҵ����Ӧ��.`������` = " + "'" + bkcode + "'"
//   													;
//   								logger.debug(sqldeletestat);
//   			   			 		@SuppressWarnings("unused")
//   			   			 		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletestat);
//   							} else { //��ɾ���Ļ����͸��º�rows =1һ�������
//   		   			        	if( !stockbkanameindb.equals(stockbkcode) )  //��һ��˵������б仯���ϵİ������Ƴ�ʱ�䣬ͬʱ����һ������Ϣ����Ӧ�µİ��
//   		   			        		checkStockHangYeNewInfo (stockcode,stockbkanameindb,stockbkcode);
//   							}
//   			        	} while(rs.next() );
   			        }
   			      
   			    } catch(java.lang.NullPointerException e) { e.printStackTrace();
   			    } catch (SQLException e) {e.printStackTrace();
   			    } catch(Exception e){e.printStackTrace();
   			    } finally {
   			    	if(rs != null)	try {rs.close();rs = null;} catch (SQLException e) {e.printStackTrace();}
   			    }
			}
		} catch (FileNotFoundException e) {e.printStackTrace();return -1;
		} catch (IOException e) {e.printStackTrace();return -1;
		} finally {
			try {fr.close(); fr = null;} catch (IOException e) {e.printStackTrace();}
			try {bufr.close();bufr = null; } catch (IOException e) {e.printStackTrace();}
		}
		 
		 return allupdatednum;
	}
	/*
	 * 
	 */
	public int refreshTDXDrawLineInfo (File tmprecordfile)
	{
		File file = new File(sysconfig.getTDXDrawLineNameFile() );
//		 if(!file.exists() ) {
//			 try {
//				Files.append( "û�з���" +  sysconfig.getTDXGaiNianBanKuaiToGeGuFile() +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			 return -1;
//		 }
		 
		 int addednumber =0;
		 BufferedInputStream dis = null;
		 FileInputStream in = null;
		 try {
			    in = new FileInputStream(file);  
			    dis = new BufferedInputStream(in);
               
           // ��Ʊ�����Ϣ�ļ�ͷ��ʽ��T0002/hq_cache/block.dat block_fg.dat block_gn.dat block_zs.dat
		   // �ļ�ͷ��384�ֽ�
//               struct TTDXBlockHeader
//               {
//                   char         szVersion[64];        // 0, Registry ver:1.0 (1999-9-28)
//                   int            nIndexOffset;            // 64, 0x00000054
//                   int            nDataOffset;            // 68, 0x00000180
//                   int            nData1;                // 72, 0x00000003
//                   int            nData2;                // 76, 0x00000000
//                   int            nData3;                // 80, 0x00000003
//               };
               int fileheadbytenumber = 500;
               byte[] itemBuf = new byte[fileheadbytenumber];
               dis.read(itemBuf, 0, fileheadbytenumber); 
               String fileHead =new String(itemBuf,0,fileheadbytenumber);  
               logger.debug(fileHead);
               
               //��������2�ֽ�
               dis.read(itemBuf, 0, 2); 
               String bknumber =new String(itemBuf,0,2);  
               logger.debug(bknumber);
			 
               //��������ݴ洢�ṹ�����������Ŀ���δ�ţ�
//               ��һ��������ʼλ��Ϊ0x182h��
//               ÿ�����ռ�ݵĴ洢�ռ�Ϊ2813���ֽڣ�ÿ�����������400ֻ��Ʊ��(2813 -9 - 2 - 2) / 7 =  400
//               ������ƣ�9�ֽ�
//               �ð������ĸ��ɸ�����2�ֽ�
//               ������2�ֽ�
//               �ð���¸��ɴ����б�������ţ�ֱ������Ϊ�գ�
//                   ���ɴ��룺7�ֽ�
//               struct TTDXBlockRecord
//               {
//                   char         szName[9];
//                   short        nCount;
//                   short        nLevel;
//                   char         szCode[400][7];
//               };
               int sigbk = 2813;
               byte[] itemBuf2 = new byte[sigbk];
               int i=0;
               Files.append("��ʼ����ͨ���Ź�Ʊ�����Ӧ��Ϣ:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   //�������
            	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
            	   CachedRowSetImpl rspd = null; 
	   			   try {
	   			    	String sqlquerystat = "SELECT *  FROM ͨ���Ű���б� WHERE �������='"  + gupiaoheader + "'";
	   			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			    	String bkcode = null;
	   			        while(rspd.next())  
	   			        	bkcode = rspd.getString("���ID");
	   			        
	   			        	BkChanYeLianTreeNode bankuai = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
		   			        if( bankuai != null && !((BanKuai)bankuai).getBanKuaiOperationSetting().isImportBKGeGu()   ) 
		            		   continue;
	   			    } catch(java.lang.NullPointerException e){ e.printStackTrace();
	   			    } catch (SQLException e) {e.printStackTrace();
	   			    } catch(Exception e){e.printStackTrace();
	   			    } finally {
	   			    	if(rspd != null) try {rspd.close();rspd = null; } catch (SQLException e) {e.printStackTrace();}
	   			    }
            	   
            	   //������
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                   
                   //�����ݿ��ж�����ǰ�ð��ĸ���
                   Set<String> tmpstockcodesetold = new HashSet();
                   CachedRowSetImpl rs = null;
	   			    try {
	   			    	String sqlquerystat = "SELECT gnbk.��Ʊ����  FROM ��Ʊͨ���Ÿ������Ӧ�� gnbk, ͨ���Ű���б� bklb"
		    						+ " WHERE  gnbk.������ = bklb.`���ID`"
		    						+ " AND bklb.`�������` = '"  + gupiaoheader + "'"
		    						+ " AND ISNULL(gnbk.�Ƴ�ʱ��)"
		    						;
	   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	while(rs.next()) {
	   			    		String stockcode = rs.getString("��Ʊ����");
	   			        	tmpstockcodesetold.add(stockcode);
	   			    	}
	   			    } catch(java.lang.NullPointerException e){e.printStackTrace();
	   			    } catch (SQLException e) {e.printStackTrace();
	   			    } catch(Exception e){e.printStackTrace();
	   			    } finally {
				    	if(rs != null)		try {rs.close();rs = null;} catch (SQLException e) {e.printStackTrace();}
				    }
	   			    
	   			 //�� tmpstockcodesetold �����еģ�tmpstockcodesetnewû�е�ѡ�������Ǿɵģ�Ҫ�����ݿ���ɾ��
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
		   	        	String sqlupdatestat = "UPDATE ��Ʊͨ���Ÿ������Ӧ�� JOIN ͨ���Ű���б�"
       							+ "  ON  ��Ʊͨ���Ÿ������Ӧ��.`��Ʊ����` = " + "'" + str.trim() + "'"
       							+ "  AND isnull(�Ƴ�ʱ��)"
       							+ "  and  ��Ʊͨ���Ÿ������Ӧ��.`������` = ͨ���Ű���б�.`���ID` "
       							+ "  and ͨ���Ű���б�.`�������`=" + "'" + gupiaoheader + "'"
       							+ "  SET �Ƴ�ʱ�� = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
       							;
				       	logger.debug(sqlupdatestat);
				   		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   	        }
		   	     differencebankuaiold = null;
	   			   
	   		        //��tmpstockcodesetnew�����еģ�tmpstockcodesetoldû�е�ѡ���������µģ�Ҫ�������ݿ�
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   				
//		   				String sqlinsertstat = "INSERT INTO  ��Ʊͨ���Ÿ������Ӧ��(��Ʊ����,������,��ƱȨ��) values ("
//		   						+ "'" + str.trim() + "'" + ","
//		   						+ "'" + gupiaoheader + "'"  + ","
//		   						+ 10 
//		   						+ ")"
//		   						;
		   		    	String sqlinsertstat = "insert into ��Ʊͨ���Ÿ������Ӧ��(��Ʊ����,������,��ƱȨ��)"
								+ "	  SELECT '" + str + "', ͨ���Ű���б�.`���ID`, 10 "
								+ " FROM ͨ���Ű���б�  where ͨ���Ű���б�.`�������` = '" + gupiaoheader + "'"   
								;
		   				logger.debug(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				addednumber ++;
		                Files.append("���룺" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   		 differencebankuainew = null;
               }
		 } catch (Exception e) {e.printStackTrace();return -1;
		 } finally {
			 try {in.close();} catch (IOException e) {e.printStackTrace();}
             try {dis.close();} catch (IOException e) {e.printStackTrace();}     
		 }
		
		 return addednumber;
	}
	/*
	 * 
	 */
	private void checkStockHangYeNewInfo(String stockcode, String stockoldTDXSWID, String stocknewTDXSWID)
	{
		//�Ȱ��ϼ�¼ �Ƴ�ʱ�����
//    		String sqlupdatestat = "UPDATE  ��Ʊͨ������ҵ����Ӧ��  SET " 
//				+ " �Ƴ�ʱ�� =" + "'" +  sysconfig.formatDate(new Date() ) + "'" + ","
//				+ " WHERE ��Ʊ���� = " + "'" + stockcode.trim() + "'" 
//				+ " AND ��ӦTDXSWID=" + stockbkanameindb 
//				+ " AND isnull(�Ƴ�ʱ��)"
//				;
			
   			String sqlupdatestat = "UPDATE ��Ʊͨ������ҵ����Ӧ�� "
									+ "  SET �Ƴ�ʱ�� = " + "'" +  LocalDate.now().toString()  + "'"
									+ "  WHERE  ��Ʊͨ������ҵ����Ӧ��.`��Ʊ����` = " + "'" + stockcode.trim() + "'"
									+ "  AND isnull(�Ƴ�ʱ��)"
									+ "  AND ��Ʊͨ������ҵ����Ӧ��.`��ӦTDXSWID` = " + "'" + stockoldTDXSWID + "'"
						;
	 		logger.debug(sqlupdatestat);
	 		@SuppressWarnings("unused")
			int autoIncKeyFromApi;
			try {
				autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
			} catch (MysqlDataTruncation e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 		
	 		//������һ����¼
//		 	String sqlinsertstat = "INSERT INTO  ��Ʊͨ������ҵ����Ӧ��(��Ʊ����,��ҵ���,��ӦTDXSWID,��ƱȨ��) values ("
//					+ "'" + stockcode.trim() + "'" + ","
//					+ "'" + stockbkname.trim() + "'"  + ","
//					+ "'" + stockbkcode.trim() + "'" + ","
//					+ 10
//					+ ")"
//					;
	 		String sqlinsertstat =  "insert into ��Ʊͨ������ҵ����Ӧ��(��Ʊ����,������,��ӦTDXSWID,��ƱȨ��)"
			+ " SELECT '" + stockcode.trim() + "', ͨ���Ű���б�.`���ID`,'" + stocknewTDXSWID + "',10 "
			+ " FROM ͨ���Ű���б�  where ͨ���Ű���б�.`��ӦTDXSWID` = '" + stocknewTDXSWID + "'"
			;
			logger.debug(sqlinsertstat);
			try {
				autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
			} catch (MysqlDataTruncation e) {e.printStackTrace();
			} catch (SQLException e) {e.printStackTrace();}
		
	}
	
	/*
	 * ͨ���ŵķ������ɴ����Ӧ�ļ�
	 */
	private void refreshTDXFengGeBanKuaiToGeGu (File tmprecordfile)
	{
		BanKuaiAndStockTree treeofbkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		
		File file = new File(sysconfig.getTDXFengGeBanKuaiToGeGuFile() );
		 if(!file.exists() ) {	 logger.debug("File not exist");	return;	 }
		 
		 FileInputStream in = null;  
		 BufferedInputStream dis = null;
		 try {
			    in = new FileInputStream(file);  
			    dis = new BufferedInputStream(in);
               
           // ��Ʊ�����Ϣ�ļ�ͷ��ʽ��T0002/hq_cache/block.dat block_fg.dat block_gn.dat block_zs.dat
		   // �ļ�ͷ��384�ֽ�
//               struct TTDXBlockHeader
//               {
//                   char         szVersion[64];        // 0, Registry ver:1.0 (1999-9-28)
//                   int            nIndexOffset;            // 64, 0x00000054
//                   int            nDataOffset;            // 68, 0x00000180
//                   int            nData1;                // 72, 0x00000003
//                   int            nData2;                // 76, 0x00000000
//                   int            nData3;                // 80, 0x00000003
//               };
               int fileheadbytenumber = 384;
               byte[] itemBuf = new byte[fileheadbytenumber];
               dis.read(itemBuf, 0, fileheadbytenumber); 
               String fileHead =new String(itemBuf,0,fileheadbytenumber);  
               
               //��������2�ֽ�
               dis.read(itemBuf, 0, 2); 
               String bknumber =new String(itemBuf,0,2);  
			 
               //��������ݴ洢�ṹ�����������Ŀ���δ�ţ�
//               ��һ��������ʼλ��Ϊ0x182h��
//               ÿ�����ռ�ݵĴ洢�ռ�Ϊ2813���ֽڣ�ÿ�����������400ֻ��Ʊ��(2813 -9 - 2 - 2) / 7 =  400
//               ������ƣ�9�ֽ�
//               �ð������ĸ��ɸ�����2�ֽ�
//               ������2�ֽ�
//               �ð���¸��ɴ����б�������ţ�ֱ������Ϊ�գ�
//                   ���ɴ��룺7�ֽ�
//               struct TTDXBlockRecord
//               {
//                   char         szName[9];
//                   short        nCount;
//                   short        nLevel;
//                   char         szCode[400][7];
//               };
               int sigbk = 2813;
               byte[] itemBuf2 = new byte[sigbk];
               int i=0;
               Files.append("��ʼ����ͨ���Ź�Ʊ����Ӧ��Ϣ:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   //�������
            	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
            	   
            	   //���� ������ȯ�� �Ǹ���飬��û��ID����ͨ���Ű���б�����û�У�Ҫ���ж��������Σ��Ա��ò�ͬ��SQL
                   boolean hasbkcode = true;
                   CachedRowSetImpl rspd = null; 
	   			   try {
	   			    	String sqlquerystat = "SELECT *  FROM ͨ���Ű���б� WHERE �������='"  + gupiaoheader + "'";
	   			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			    	String bkcode = null;
	   			        while(rspd.next())  
	   			        	bkcode = rspd.getString("���ID");

	   			        if(bkcode == null)	hasbkcode = false;
	   			        else {
	   			        	BkChanYeLianTreeNode bankuai = treeofbkstk.getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
		   			        if( bankuai != null && !((BanKuai)bankuai).getBanKuaiOperationSetting().isImportBKGeGu()   ) 
		            		   continue;
	   			        }
	   			    } catch(java.lang.NullPointerException e){ e.printStackTrace();
	   			    } catch (SQLException e) {e.printStackTrace();
	   			    } catch(Exception e){e.printStackTrace();
	   			    } finally {
	   			    	if(rspd != null) try {rspd.close();rspd = null; } catch (SQLException e) {e.printStackTrace();}
	   			    }
	   			   
            	   //������
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   Set<String> tmpstockcodesetnew = new HashSet<>(tmpstockcodestr);
                   
                   //�����ݿ��ж�����ǰ�ð��ĸ���
                   String sqlquerystat;
   				   if(hasbkcode)
   			    	sqlquerystat = "SELECT fgbk.��Ʊ����  FROM ��Ʊͨ���ŷ�����Ӧ�� fgbk, ͨ���Ű���б� bklb"
   			    						+ " WHERE  fgbk.������ = bklb.`���ID`"
   			    						+ " AND bklb.`�������` = '"  + gupiaoheader + "'"
   			    						+ " AND ISNULL(fgbk.�Ƴ�ʱ��)"
   			    						;
   				   else 
   					sqlquerystat = "SELECT ��Ʊ����  FROM ��Ʊͨ���ŷ�����Ӧ�� "
   									+ " WHERE ������='" + gupiaoheader + "'"
   									+ " AND ISNULL(�Ƴ�ʱ��)"
   									;  
                   Set<String> tmpstockcodesetold = new HashSet<>();
   				   CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			   try {
	   			    	while(rs.next() ) {
	   			    		String stockcode = rs.getString("��Ʊ����");
	   			        	tmpstockcodesetold.add(stockcode);
	   			    	}
	   			    } catch(java.lang.NullPointerException e){e.printStackTrace();
	   			    } catch (SQLException e) {e.printStackTrace();
	   			    } catch(Exception e){e.printStackTrace();
	   			    } finally {
	   			    	if(rs != null)
							try {rs.close();rs = null;
							} catch (SQLException e) {e.printStackTrace();}
	   			    }
		   		    
		   	        //�� tmpstockcodesetold �����еģ�tmpstockcodesetnewû�е�ѡ�������Ǿɵģ�Ҫ�����ݿ���update�Ƴ���ʱ�����������µ�
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
//		   	        	String sqldeletetstat = "DELETE  FROM ��Ʊͨ���ŷ�����Ӧ�� "
//		   	        							+ " WHERE ��Ʊ����=" +  "'" + str + "'"
//		   	        							+ " AND �����=" + "'" + gupiaoheader + "'"
//		   	        							;
		   	        	String sqlupdatestat ;
		   	        	if(hasbkcode)
		   	        		sqlupdatestat = "UPDATE ��Ʊͨ���ŷ�����Ӧ�� JOIN ͨ���Ű���б�"
		   	        							+ "  ON  ��Ʊͨ���ŷ�����Ӧ��.`��Ʊ����` = " + "'" + str.trim() + "'"
		   	        							+ "  AND isnull(�Ƴ�ʱ��)"
		   	        							+ "  and  ��Ʊͨ���ŷ�����Ӧ��.`������` = ͨ���Ű���б�.`���ID` "
		   	        							+ "  and ͨ���Ű���б�.`�������` = " + "'" + gupiaoheader + "'"
		   	        							+ "  SET �Ƴ�ʱ�� = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
		   	        							;
		   	        	else
		   	        		sqlupdatestat = "UPDATE ��Ʊͨ���ŷ�����Ӧ�� SET �Ƴ�ʱ�� = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
		   	        						+" WHERE ������=" + "'" + gupiaoheader + "'"
		   	        						+" AND ��Ʊ����= " + "'" + str.trim() + "'"
		   	        						+ "  AND isnull(�Ƴ�ʱ��)"
		   	        						;
		   	        	logger.debug(sqlupdatestat);
		   	    		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
//		   	    		Files.append("ɾ����"  + str.trim() + " " + gupiaoheader + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   	        }
		   	        differencebankuaiold = null;
		   	        
	   		        //��tmpstockcodesetnew�����еģ�tmpstockcodesetoldû�е�ѡ���������µģ�Ҫ�������ݿ�
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   				
		   		    	String sqlinsertstat ;
		   	        	if(hasbkcode)
			   				 sqlinsertstat = "insert into ��Ʊͨ���ŷ�����Ӧ��(��Ʊ����,������,��ƱȨ��)"
			   									+ "	  SELECT '" + str + "', ͨ���Ű���б�.`���ID`, 10 "
			   									+ " FROM ͨ���Ű���б�  where ͨ���Ű���б�.`�������` = '" + gupiaoheader + "'"   
			   									;
		   	        	else
		   	        		sqlinsertstat = "insert into ��Ʊͨ���ŷ�����Ӧ��(��Ʊ����,������,��ƱȨ��) VALUES "
		   	        						+ "('" + str + "','" +  gupiaoheader + "',10)"
		   	        						;
		   				logger.debug(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				
//		   				Files.append("���룺"  + str.trim() + " " + gupiaoheader + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   		    differencebankuainew = null;
		   	        
//		   	        if(differencebankuainew.size() ==0 && differencebankuaiold.size()>0 ) { // �µ�Ϊ0���ɵĴ����㣬ɾ����ʱ������ʵ�ֻ������ݿⲻ����������ʱ�䣬�Ը���XML��Ӱ�죬����ǿ�Ƹ���һ����Ϣ
//		   	        	String sqlupdatestat = "UPDATE ͨ���Ű���б� SET �������¸���ʱ�� = "
//		   	        			+  formateDateForDiffDatabase("mysql", sysconfig.formatDate( LocalDateTime.now() ) ) + ","
//		   	        			;
//		   	        	logger.debug(sqlupdatestat);
//		   				connectdb.sqlUpdateStatExecute (sqlupdatestat);
//		   	        }
               }
		 } catch (Exception e) {e.printStackTrace();
		 } finally {
             try { in.close();	} catch (IOException e) {e.printStackTrace();			}
             try {dis.close();} catch (IOException e) {e.printStackTrace();}
		 }
	
	}
	/*
	 * ͨ���ŵ���ҵ����ɶ�Ӧ�ļ�
	 * ����ļ���
		incon.dat                        ֤�����ҵ��ͨ��������ҵ��������ҵ��������Ϣ
		T0002/hq_cache/block.dat         һ����
		T0002/hq_cache/block_gn.dat      ������
		T0002/hq_cache/block_fg.dat       �����
		T0002/hq_cache/block_zs.dat       ָ�����:
		T0002/hq_cache/tdxhy.cfg             ÿ����Ʊ��Ӧͨ������ҵ��������ҵ
		T0002/hq_cache/tdxzs.cfg             ���ָ�������ְ������һ���ֶ�ӳ�䵽incon.dat��TDXNHY��SWHY
		T0002/blocknew/blocknew.cfg        �Զ������Ҫ�����ļ�
		
		��ҵ�����������֤�����ҵ��������ҵ��ͨ��������ҵ
		��ҵ���ļ���incon.dat���ж��塣�ļ���ʽ��
		1) �ļ����������ҵ���ࣺ
			a) ֤�����ҵ����ͷ#ZJHHY������######
			b) ������ҵ����ͷ#SWHY������######
			c) ͨ��������ҵ����ͷ#TDXNHY������######
		2) ÿ�������У�ÿһ�а���һ��ϸ����ҵ�Ĵ�������ƣ��ԡ�|���ָ�
			a) ֤�����ҵ��һ������ΪA~M����������A99����������ΪA9999
			b) ������ҵ��һ������Ϊ990000����������Ϊ999900����������Ϊ999999
			c) ͨ��������ҵ��һ������ΪT99����������ΪT9999����������ΪT999999
	 */
	private int refreshTDXZhiShuBanKuaiToGeGu (File tmprecordfile)
	{
		File file = new File(sysconfig.getTDXZhiShuBanKuaiToGeGuFile() );
		if(!file.exists() ) 
			 return -1;
		 
		 int addednumber =0;FileInputStream in = null;BufferedInputStream dis = null;
		 try {
			    in = new FileInputStream(file);  
			    dis = new BufferedInputStream(in);
              
          // ��Ʊ�����Ϣ�ļ�ͷ��ʽ��T0002/hq_cache/block.dat block_fg.dat block_gn.dat block_zs.dat
		   // �ļ�ͷ��384�ֽ�
//              struct TTDXBlockHeader
//              {
//                  char         szVersion[64];        // 0, Registry ver:1.0 (1999-9-28)
//                  int            nIndexOffset;            // 64, 0x00000054
//                  int            nDataOffset;            // 68, 0x00000180
//                  int            nData1;                // 72, 0x00000003
//                  int            nData2;                // 76, 0x00000000
//                  int            nData3;                // 80, 0x00000003
//              };
              int fileheadbytenumber = 384;
              byte[] itemBuf = new byte[fileheadbytenumber];
              dis.read(itemBuf, 0, fileheadbytenumber); 
              String fileHead =new String(itemBuf,0,fileheadbytenumber);  
             
              //��������2�ֽ�
              dis.read(itemBuf, 0, 2); 
              String bknumber =new String(itemBuf,0,2);  
			 
              //��������ݴ洢�ṹ�����������Ŀ���δ�ţ�
//              ��һ��������ʼλ��Ϊ0x182h��
//              ÿ�����ռ�ݵĴ洢�ռ�Ϊ2813���ֽڣ�ÿ�����������400ֻ��Ʊ��(2813 -9 - 2 - 2) / 7 =  400
//              ������ƣ�9�ֽ�
//              �ð������ĸ��ɸ�����2�ֽ�
//              ������2�ֽ�
//              �ð���¸��ɴ����б�������ţ�ֱ������Ϊ�գ�
//                  ���ɴ��룺7�ֽ�
//              struct TTDXBlockRecord
//              {
//                  char         szName[9];
//                  short        nCount;
//                  short        nLevel;
//                  char         szCode[400][7];
//              };
              int sigbk = 2813;
              byte[] itemBuf2 = new byte[sigbk];
              int i=0;
              Files.append("��ʼ����ͨ���Ź�Ʊָ������Ӧ��Ϣ:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
              while (dis.read(itemBuf2, 0, sigbk) != -1) {
	           	   //�������
	           	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
	           	   CachedRowSetImpl rspd = null; 
				   try {
				    	String sqlquerystat = "SELECT *  FROM ͨ���Ű���б� WHERE �������='"  + gupiaoheader + "'";
				    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
				    	
				    	String bkcode = null;
				        while(rspd.next())  
				        	bkcode = rspd.getString("���ID");
				        
				        BkChanYeLianTreeNode bankuai = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
		   			    if( bankuai != null && !((BanKuai)bankuai).getBanKuaiOperationSetting().isImportBKGeGu()   ) 
		            		   continue;
				    } catch(java.lang.NullPointerException e){ e.printStackTrace();
				    } catch (SQLException e) {e.printStackTrace();
				    } catch(Exception e){e.printStackTrace();
				    } finally {
				    	if(rspd != null) try {rspd.close();rspd = null; } catch (SQLException e) {e.printStackTrace();}
				    }
           	   
           	   //������
                  String gupiaolist =new String(itemBuf2,12,sigbk-12);
                 
                  List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                  Set<String> tmpstockcodesetnew = new HashSet<>(tmpstockcodestr);
                  //�����ݿ��ж�����ǰ�ð��ĸ���
                  Set<String> tmpstockcodesetold = new HashSet<>();
                  String sqlquerystat = "SELECT zsbk.��Ʊ����  FROM ��Ʊͨ���Ž�����ָ����Ӧ��  zsbk, ͨ���Ű���б� zslb"
  						+ " WHERE  zsbk.������ = zslb.`���ID`"
  						+ " AND zslb.`�������` = '"  + gupiaoheader + "'"
  						+ " AND ISNULL(zsbk.�Ƴ�ʱ��)"
  						;
                  CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    try {
	   			    	
	   			    	while(rs.next()) {  
	   			        	String stockcode = rs.getString("��Ʊ����");
	   			        	tmpstockcodesetold.add(stockcode);
	   			        }
	   			    }catch(java.lang.NullPointerException e){e.printStackTrace();
	   			    } catch (SQLException e) {e.printStackTrace();
	   			    }catch(Exception e){e.printStackTrace();
	   			    } finally {
	   			    	if(rs != null)	try {rs.close();rs = null;} catch (SQLException e) {e.printStackTrace();}
	   			    }
	   			    
	   			    //�� tmpstockcodesetold �����еģ�tmpstockcodesetnewû�е�ѡ�������Ǿɵģ�Ҫ�����ݿ��и���ʱ��
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {

		   	        	String sqlupdatestat = "UPDATE ��Ʊͨ���Ž�����ָ����Ӧ��  JOIN ͨ���Ű���б�"
       							+ "  ON  ��Ʊͨ���Ž�����ָ����Ӧ��.`��Ʊ����` = " + "'" + str.trim() + "'"
       							+ "  AND isnull(�Ƴ�ʱ��)"
       							+ "  and  ��Ʊͨ���Ž�����ָ����Ӧ��.`������` = ͨ���Ű���б�.`���ID` "
       							+ "  and ͨ���Ű���б�.`�������` = " + "'" + gupiaoheader + "'"
       							+ "  SET �Ƴ�ʱ�� = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
       							;	
				   		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   	        }
		   	     differencebankuaiold = null;
		   	     
	   		        //��tmpstockcodesetnew�����еģ�tmpstockcodesetoldû�е�ѡ���������µģ�Ҫ�������ݿ�
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   		    	String sqlinsertstat = "insert into ��Ʊͨ���Ž�����ָ����Ӧ��(��Ʊ����,������,��ƱȨ��)"
								+ "	  SELECT '" + str.trim() + "', ͨ���Ű���б�.`���ID`, 10 "
								+ " FROM ͨ���Ű���б�  where ͨ���Ű���б�.`�������` = '" + gupiaoheader + "'"   
								;
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				addednumber ++;
		                Files.append("���룺" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   		 differencebankuainew = null;
              }
		 } catch (Exception e) {e.printStackTrace();return -1;
		 } finally {
			 try {in.close();} catch (IOException e1) {e1.printStackTrace();}
             try {dis.close();} catch (IOException e) {e.printStackTrace();}
		 }
		
		 return addednumber;
	}
	/*
	 * ��ͨ���Ÿ������еĽ�����ָ�����Ϻ�part
	 */
	private int refreshTDXZhiShuShangHaiLists(File tmprecordfile)
	{
		//�ȴ����ݿ��ж�ȡ�������е��Ϻ�ָ��
		Collection<BkChanYeLianTreeNode> requiredbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK,"sh");
		Set<String> curallshbk = new HashSet<> ();
		for(BkChanYeLianTreeNode tmpnode : requiredbk)
			curallshbk.add(tmpnode.getMyOwnCode());

		File file = new File(sysconfig.getTDXShangHaiZhiShuNameFile() );
		if(!file.exists() ) return -1;
		
		 int addednumber =0;
		 Set<String> allinsertbk = new HashSet<> (); //�������µ�ָ����������curallshbk�����ж��Ƿ��оɵ�ָ����Ҫ��ɾ��
		 BufferedInputStream dis = null;  FileInputStream in = null;
		 try {
			    in = new FileInputStream(file);  
			    dis = new BufferedInputStream(in);
               
               int fileheadbytenumber = 50;
               byte[] itemBuf = new byte[fileheadbytenumber];
               dis.read(itemBuf, 0, fileheadbytenumber); 
               String fileHead =new String(itemBuf,0,fileheadbytenumber);  
               
               int sigbk = 18*16+12+14;
               byte[] itemBuf2 = new byte[sigbk];
               int i=0;
               Files.append("��ʼ����ͨ���Ź�Ʊָ������Ӧ��Ϣ:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   String zhishuline =new String(itemBuf2,0,sigbk);
            	   String zhishucode = zhishuline.trim().substring(0, 6);

            	   if( ! sysconfig.isShangHaiZhiShu(zhishucode))   continue;
            	   
            	   List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(zhishuline.substring(6, zhishuline.length()));
            	   String zhishuname = null;
            	   try {	zhishuname = tmplinelist.get(0).trim().substring(0, 6).trim();
            	   } catch (java.lang.StringIndexOutOfBoundsException ex) {
            		   List<String> tmplinepartnamelist = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(1).trim());
            		   zhishuname = tmplinelist.get(0).trim() + tmplinepartnamelist.get(0).trim();
            	   }

            	   String sqlinsertstat = "INSERT INTO  ͨ���Ű���б�(�������,���ID,ָ������������) values ("
	   						+ " '" + zhishuname.trim() + "'" + ","
	   						+ " '" + zhishucode.trim() + "'" + ","
	   						+ " '" + "sh" + "'"
	   						+ ")"
	   						+ " ON DUPLICATE KEY UPDATE "
	   						+ "������� =" + " '" + zhishuname.trim() + "'" + ","
	   						+ "ָ������������= " + " '" + "sh" + "'"
	   						;
	   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//	                Files.append("���룺" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	   				allinsertbk.add(zhishucode);
               }
               
		 } catch (Exception e) { e.printStackTrace();	 return -1;
		 } finally {
			 try { in.close();	} catch (IOException e) {e.printStackTrace();}
             try { dis.close(); } catch (IOException e) {e.printStackTrace();}     
		 }
		 
		 SetView<String> differencebankuainew = Sets.difference(allinsertbk, curallshbk  );//�µİ��Ҫ�ӵ����ϣ�����Ժ��浼���齻��������Ӱ��
		 for(String newbkcode : differencebankuainew) {
			 BanKuai newbk = new BanKuai(newbkcode,"");
			 newbk.getNodeJiBenMian().setSuoShuJiaoYiSuo("SH");
			 BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot();
			 treeroot.add(newbk);
		 }
		 
//		 SetView<String> differencebankuaiold = Sets.difference( curallshbk, allinsertbk );
//		 if(differencebankuaiold.size() >0 ) { //˵����ĳЩ�����Ҫɾ��
//			 for(String oldbkcode : differencebankuaiold) {
//				 String sqldeletetstat = "DELETE  FROM  ͨ���Ű���б� WHERE "
//						 				+ " ���ID =" + " '" + oldbkcode.trim() + "'" 
//						 				;
//	   			try {
//					int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e) {e.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();				}
//	   			
//	   		//��Ҫɾ���ð���� ��Ʊ�����Ӧ��/��Ʊ��ҵ��Ӧ��/��Ʊ����Ӧ��/��ҵ���Ӱ���б�  �еĹ�Ʊ�Ͱ��Ķ�Ӧ��Ϣ
//	    		sqldeletetstat = "DELETE  FROM  ��Ʊͨ���Ÿ������Ӧ��"
//						+ " WHERE ������=" + "'"  + oldbkcode.trim() + "'"
//						;
//				try {
//					int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e3) {e3.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();}
//				
//				sqldeletetstat = "DELETE  FROM  ��Ʊͨ������ҵ����Ӧ��"
//						+ " WHERE ������=" + "'"  + oldbkcode.trim() + "'"
//						;
//				try {
//					int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e2) {e2.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();}
//				
//				sqldeletetstat = "DELETE  FROM ��Ʊͨ���ŷ�����Ӧ��"
//						+ " WHERE ������=" + "'"  + oldbkcode.trim() + "'"
//						;
//				try {
//					int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e1) {e1.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();	}
//
//			 }
//		 }
//		 differencebankuaiold = null;
		 curallshbk = null;
		return 1;
	}
	/*
	 * ��ͨ���Ÿ������еĽ�����ָ��������part
	 */
	private int refreshTDXZhiShuShenZhenLists(File tmprecordfile)
	{
		//�ȴ����ݿ��ж�ȡ�������е��Ϻ�ָ��
		Collection<BkChanYeLianTreeNode> requiredbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK,"sz");
		Set<String> curallszbk = new HashSet<> ();
		for(BkChanYeLianTreeNode tmpnode : requiredbk)
			curallszbk.add(tmpnode.getMyOwnCode());
			
		File file = new File(sysconfig.getTDXShenZhenShuNameFile() );
		if(!file.exists() )  return -1;

		int addednumber =0;
		Set<String> allinsertbk = new HashSet (); //�������µ�ָ����������curallshbk�����ж��Ƿ��оɵ�ָ����Ҫ��ɾ��
		 BufferedInputStream dis = null; FileInputStream in = null;
		 try {
			    in = new FileInputStream(file);  
			    dis = new BufferedInputStream(in);
               
               int fileheadbytenumber = 50;
               byte[] itemBuf = new byte[fileheadbytenumber];
               dis.read(itemBuf, 0, fileheadbytenumber); 
               String fileHead =new String(itemBuf,0,fileheadbytenumber);  
               logger.debug(fileHead);
               
               int sigbk = 18*16+12+14;
               byte[] itemBuf2 = new byte[sigbk];
               int i=0;
               Files.append("��ʼ����ͨ���Ź�Ʊָ������Ӧ��Ϣ:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   String zhishuline =new String(itemBuf2,0,sigbk);
            	   logger.debug(zhishuline);
            	   String zhishucode = zhishuline.trim().substring(0, 6);

            	   if(! sysconfig.isShenZhengZhiShu(zhishucode))		   continue;
            	   
            	   List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(zhishuline.substring(6, zhishuline.length()));
            	   String zhishuname = null;
            	   try {   zhishuname = tmplinelist.get(0).trim().substring(0, 6).trim();
            	   } catch (java.lang.StringIndexOutOfBoundsException ex) {
            		   List<String> tmplinepartnamelist = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(1).trim());
            		   zhishuname = tmplinelist.get(0).trim() + tmplinepartnamelist.get(0).trim();
            	   }

            	   String sqlinsertstat = "INSERT INTO  ͨ���Ű���б�(�������,���ID,ָ������������) values ("
	   						+ " '" + zhishuname.trim() + "'" + ","
	   						+ " '" + zhishucode.trim() + "'" + ","
	   						+ " '" + "sz" + "'"
	   						+ ")"
	   						+ " ON DUPLICATE KEY UPDATE "
	   						+ "������� =" + " '" + zhishuname.trim() + "'" + ","
	   						+ "ָ������������= " + " '" + "sz" + "'" 
	   						;
            	   logger.debug(sqlinsertstat);
	   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
	   				allinsertbk.add(zhishucode);
               }
		 } catch (Exception e) {e.printStackTrace();return -1;
		 } finally {
			 try {in.close();} catch (IOException e) {e.printStackTrace();}
             try {dis.close();} catch (IOException e) {e.printStackTrace();}     
		 }
		 
		 SetView<String> differencebankuainew = Sets.difference(allinsertbk, curallszbk);//�µİ��Ҫ�ӵ����ϣ�����Ժ��浼���齻��������Ӱ��  //
		 for(String newbkcode : differencebankuainew) {
			 BanKuai newbk = new BanKuai(newbkcode,"");
			 newbk.getNodeJiBenMian().setSuoShuJiaoYiSuo("SZ");
			 BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot();
			 treeroot.add(newbk);
		 }
		 
//		 SetView<String> differencebankuaiold = Sets.difference(curallszbk, allinsertbk );
//		 if(differencebankuaiold.size() >0 ) { //˵����ĳЩ�����Ҫɾ��
//			 for(String oldbkcode : differencebankuaiold) {
//				 String sqldeletetstat = "DELETE  FROM  ͨ���Ű���б� WHERE "
//						 				+ " ���ID =" + " '" + oldbkcode.trim() + "'" 
//						 				;
//	   			int autoIncKeyFromApi;
//				try {
//					autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e4) {e4.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();}
//	   			
//	   			//��Ҫɾ���ð���� ��Ʊ�����Ӧ��/��Ʊ��ҵ��Ӧ��/��Ʊ����Ӧ��/��ҵ���Ӱ���б�  �еĹ�Ʊ�Ͱ��Ķ�Ӧ��Ϣ
//	    		sqldeletetstat = "DELETE  FROM  ��Ʊͨ���Ÿ������Ӧ��"
//						+ " WHERE ������=" + "'"  + oldbkcode.trim() + "'"
//						;
//				try {
//					autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e3) {e3.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();}
//				
//				sqldeletetstat = "DELETE  FROM  ��Ʊͨ������ҵ����Ӧ��"
//						+ " WHERE ������=" + "'"  + oldbkcode.trim() + "'"
//						;
//				try {
//					autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e2) {e2.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();}
//				
//				sqldeletetstat = "DELETE  FROM ��Ʊͨ���ŷ�����Ӧ��"
//						+ " WHERE ������=" + "'"  + oldbkcode.trim() + "'"
//						;
//				try {
//					autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				} catch (MysqlDataTruncation e1) {e1.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();	}
//			 }
//		 }
//		 differencebankuaiold = null;
		 curallszbk = null;
		 return -1;
	}
	/*
	 * ���й�Ʊ
	 */
	public ArrayList<Stock> getAllStocks() 
	{
		ArrayList<Stock> tmpsysbankuailiebiaoinfo = new ArrayList<Stock> ();

		String sqlquerystat = "SELECT * FROM \r\n" + 
				"(\r\n" + 
				"SELECT ��Ʊ����,��Ʊ����,������,����������,��Ʊͨ���Ż�������Ϣ��Ӧ��.`��������SSDATE` \r\n" + 
				"FROM A�� \r\n" + 
				"LEFT JOIN ��Ʊͨ���Ż�������Ϣ��Ӧ�� \r\n" + 
				"ON ��Ʊͨ���Ż�������Ϣ��Ӧ��.`��Ʊ����GPDM` = a��.`��Ʊ����`\r\n" + 
				") node\r\n" + 
				"LEFT JOIN ����Ʊռ����ֵ\r\n" + 
				"ON node.��Ʊ���� = ����Ʊռ����ֵ.`����`"									
				;   
		logger.debug(sqlquerystat);
		CachedRowSetImpl rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);

	        while(rs.next()) {
	        	Stock tmpbk = null;
	        	if(!rs.getBoolean("������")) {
	        		String nodecode = rs.getString("��Ʊ����");
	        		String nodename = rs.getString("��Ʊ����");
	        		tmpbk = new Stock (nodecode,nodename);
	        		tmpbk.getNodeJiBenMian().setSuoShuJiaoYiSuo(rs.getString("����������"));
	        		try{
	        			LocalDate shangshiriqi = rs.getDate("��������SSDATE").toLocalDate();
	        			if(!shangshiriqi.equals(LocalDate.parse("1992-01-01"))) //ͨ���Ű�������ʱû�����н��׵Ĺ�Ʊ�������ڶ�����Ϊ1992-0101
	        				tmpbk.getNodeJiBenMian().setShangShiRiQi( shangshiriqi );
	        		} catch (java.lang.NullPointerException e) {}
	        		tmpbk.getNodeJiBenMian().setNodeCjeZhanbiLevel(rs.getDouble("�ɽ���ռ������"), rs.getDouble("�ɽ���ռ������"));
	        		tmpbk.getNodeJiBenMian().setNodeCjlZhanbiLevel(rs.getDouble("�ɽ���ռ������"), rs.getDouble("�ɽ���ռ������"));
	        		if(nodename != null && nodename.toUpperCase().contains("ST"))
	        			tmpbk.getShuJuJiLuInfo().setHasReviewedToday(true);
		        	tmpsysbankuailiebiaoinfo.add(tmpbk);
	        	} else { 		logger.debug(rs.getString("��Ʊ����") + "�Ѿ�����");
	        	}
	        	
	        }
	    }catch(java.lang.NullPointerException e){ e.printStackTrace();
	    } catch (SQLException e) {e.printStackTrace();
	    }catch(Exception e){e.printStackTrace();
	    } finally {
	    	if(rs != null)	try {rs.close();rs = null;} catch (SQLException e) {e.printStackTrace();}
	    } 
	    
	    return tmpsysbankuailiebiaoinfo;
	}
	
	/*
	 * ������ݿ��е�ͨ���Ű����Щ�м�¼�ļ�����Щû�С� 
	 */
	public File preCheckTDXBanKuaiVolAmoToDb (String jys)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "ͬ��ͨ���Ű��Ϻ���ɽ���Ԥ���.tmp");
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
//		List<String> volamooutput = sysconfig.getTDXVOLFilesPath();
//		String exportath = sysconfig.toUNIXpath(Splitter.on('=').trimResults().omitEmptyStrings().splitToList(volamooutput.get(0) ).get(1) );
//		String filenamerule = Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(1)).get(1);
//		boolean header;
//		if(volamooutput.get(4).endsWith("1"))
//			header = true;
//		else header = false;
		
		Collection<BkChanYeLianTreeNode> requiredbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK,jys);
		List<String> allbkcode = new ArrayList<String>( );
		for(BkChanYeLianTreeNode tmpnode : requiredbk)
			allbkcode.add(tmpnode.getMyOwnCode());
		
		int found =0 ; int notfound = 0;
		try {
			Files.append("ϵͳ����"+ allbkcode.size() + "����飡"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		
			for(String tmpbkcode:allbkcode) {
//				String bkcys = allsysbk.get(tmpbkcode).getSuoShuJiaoYiSuo().trim().toUpperCase();
				String bkfilename = (filenamerule.replaceAll("YY",jys.toUpperCase())).replaceAll("XXXXXX", tmpbkcode);
				
				File tmpbkfile = new File(exportath + "/" + bkfilename);
					
					if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {
						Files.append(tmpbkcode + "δ�ҵ���¼�ļ���"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
						notfound ++;
					}
					 else {
						 Files.append(tmpbkcode + "�м�¼�ļ���"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
						 found ++;
					 }
			}
			
			Files.append(found + "������м�¼�ļ���"+ notfound + "�����û�м�¼�ļ���" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		allbkcode = null;
		return tmprecordfile;
	}
	/*
	 * ���û�����ɽ�����Ҫ���°������ͣ�// ͨ�������涨��İ���м��֣�1.�и��������гɽ������� 2. �и��������޳ɽ������� 3.�޸��������гɽ�������
	 * ÿ����һ�ξͿ��ԣ�����̫��ʱ��
	 */
	public void refreshTDXSystemBanKuaiLeiXing () 
	{
		Collection<BkChanYeLianTreeNode> curallbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getAllRequiredNodes(BkChanYeLianTreeNode.TDXBK);
		
		this.getTDXBanKuaiShuJuJiLuMaxMinDateByJiaoYiSuo ("SH");
		this.getTDXBanKuaiShuJuJiLuMaxMinDateByJiaoYiSuo ("SZ");
		
		this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SH");
	    this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SZ");
	
		for ( BkChanYeLianTreeNode tmpnode : curallbk) {
			BanKuai bankuai = (BanKuai)tmpnode;
			String leixing = bankuai.getBanKuaiLeiXing();
		    if(leixing != null && leixing.equals(BanKuai.HASGGWITHSELFCJL) )  //�������İ�����ͣ������ڸ�����
		    	continue;
		    
		    Boolean bkhacjl = false;
		    if(bankuai.getShuJuJiLuInfo().getJyjlmaxdate()  != null)
		    	bkhacjl = true;
		    
			boolean bkhasgegu = false;
			if(bankuai.getShuJuJiLuInfo().getGuPiaoBanKuaiDuiYingBiao() != null)
		    	bkhasgegu  = true;

			String  bktype = null ;
			if(bkhacjl && bkhasgegu )
				bktype = BanKuai.HASGGWITHSELFCJL;
			else if(bkhacjl && !bkhasgegu)
				bktype = BanKuai.NOGGWITHSELFCJL;
			else if(!bkhacjl && bkhasgegu)
				bktype = BanKuai.HASGGNOSELFCJL;
			else
				bktype = BanKuai.NOGGNOSELFCJL;
			
			String bkcode = tmpnode.getMyOwnCode();
			if(bktype != null) {
				String sqlupdatequery = "UPDATE ͨ���Ű���б� SET ����������� = " + "'" + bktype +"'" + " WHERE ���ID = '" + bkcode + "'";
			    try {
					connectdb.sqlUpdateStatExecute(sqlupdatequery);
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();
				}
			}
//			if(bktype.equals(BanKuai.NOGGWITHSELFCJL)  ||  bktype.equals(BanKuai.NOGGNOSELFCJL)) { //û�и��ɵİ��϶���������gephi
//				String sqlupdatequery = "UPDATE ͨ���Ű���б� SET ����Gephi = false WHERE ���ID = '" + bkcode + "'";
//				logger.debug(sqlupdatequery);
//			    try {
//					connectdb.sqlUpdateStatExecute(sqlupdatequery);
//				} catch (MysqlDataTruncation e) {e.printStackTrace();
//				} catch (SQLException e) {e.printStackTrace();
//				}
//			}
		}
		
		curallbk = null;
	}
	/*
	 * 
	 */
	public void getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo (String jiaoyisuo)
	{
		Collection<BkChanYeLianTreeNode> bkjys = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK, jiaoyisuo);
		String bkcodes = "";
		for(BkChanYeLianTreeNode tmpbk : bkjys)
			bkcodes = bkcodes + tmpbk.getMyOwnCode() + ",";
		bkcodes = bkcodes.substring(0, bkcodes.length()-2);
		
		String sqlquerystat1 =  
				" SELECT ������, COUNT(*) AS RESULT , '��Ʊͨ���Ÿ������Ӧ��'  AS DYB FROM ��Ʊͨ���Ÿ������Ӧ��\r\n" + 
				" WHERE  ������ IN (" +  bkcodes + ")\r\n" + 
				" GROUP BY ������  \r\n"  
//				" + UNION \r\n" 
				; 
		String sqlquerystat2 =		" SELECT ������, COUNT(*) AS RESULT ,'��Ʊͨ���ŷ�����Ӧ��' AS DYB FROM ��Ʊͨ���ŷ�����Ӧ��\r\n" + 
				" WHERE  ������ IN (" +  bkcodes + ")\r\n" + 
				" GROUP BY ������  \r\n"  
//				"+  UNION \r\n" 
				; 
				
		String sqlquerystat3 =			" SELECT ������, COUNT(*) AS RESULT , '��Ʊͨ������ҵ����Ӧ��'   AS DYB FROM  ��Ʊͨ������ҵ����Ӧ��\r\n" + 
				" WHERE  ������ IN (" +  bkcodes + ")\r\n" + 
				" GROUP BY ������  \r\n"  
//				" + UNION \r\n" 
				; 
		String sqlquerystat4 =			"  SELECT ������, COUNT(*) AS RESULT , '��Ʊͨ���Ž�����ָ����Ӧ��'  AS DYB FROM ��Ʊͨ���Ž�����ָ����Ӧ��\r\n" + 
				" WHERE  ������ IN (" +  bkcodes + ") \r\n" +  
				" GROUP BY ������"
				;
		String sqlquerystat = sqlquerystat1 + sqlquerystat2 + sqlquerystat3 + sqlquerystat4;
		setTDXBanKuaiGeGuDuiYingBiao (sqlquerystat1);
		setTDXBanKuaiGeGuDuiYingBiao (sqlquerystat2);
		setTDXBanKuaiGeGuDuiYingBiao (sqlquerystat3);
		setTDXBanKuaiGeGuDuiYingBiao (sqlquerystat4);
		
	}
	/*
	 * 
	 */
	private void setTDXBanKuaiGeGuDuiYingBiao (String sqlquerystat1)
	{
		CachedRowSetImpl rsdm = connectdb.sqlQueryStatExecute(sqlquerystat1);
		try {
			while(rsdm.next()) {
	    		 String bkcode = rsdm.getString("������"); //mOST_RECENT_TIME
	    		 BanKuai bk = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
	    		 if(bk == null) 
	    			 continue;
	    		 try { String dyb = rsdm.getString("DYB");
	    		 		bk.getShuJuJiLuInfo().setGuPiaoBanKuaiDuiYingBiao(dyb);
	    		 } catch (java.lang.NullPointerException ex) {}
	    	}
		} catch(java.lang.NullPointerException e) {	e.printStackTrace();
	    } catch (SQLException e) {	e.printStackTrace();
	    } catch(Exception e){	e.printStackTrace();
	    } finally {
	    	if(rsdm != null)
			try {rsdm.close();rsdm = null;
			} catch (SQLException e) {	e.printStackTrace();}
	    }
		
		return;
	}
	/*
	 * 
	 */
	public void getTDXBanKuaiShuJuJiLuMaxMinDateByJiaoYiSuo (String jiaoyisuo)
	{
		String cjltablename;
		if(jiaoyisuo.toLowerCase().equals("sh"))			cjltablename = "ͨ���Ű��ÿ�ս�����Ϣ";
		else			cjltablename = "ͨ���Ž�����ָ��ÿ�ս�����Ϣ";
		
		String sqlquerystat = "SELECT * FROM \r\n" + 
				"(SELECT ���ID  FROM ͨ���Ű���б�\r\n" + 
				"WHERE ָ������������ = '" + jiaoyisuo + "' ) agu\r\n" + 
				"\r\n" + 
				"LEFT JOIN \r\n" + 
				"( SELECT ����, MIN(��������)  minjytime , MAX(��������)  maxjytime FROM " + cjltablename + "\r\n" + 
				"  GROUP BY ����) shjyt\r\n" + 
				" ON agu.���ID =  shjyt.`����`\r\n" + 
				""
				;
		CachedRowSetImpl rsdm = connectdb.sqlQueryStatExecute(sqlquerystat);
		try { 	
	    	while(rsdm.next()) {
		    		 String bkcode = rsdm.getString("����"); //mOST_RECENT_TIME
		    		 BanKuai bk = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
		    		 if(bk == null) 
		    			 continue;
		    		 try { LocalDate mintime = rsdm.getDate("minjytime").toLocalDate();
		    		 		bk.getShuJuJiLuInfo().setJyjlmindate(mintime);
		    		 } catch (java.lang.NullPointerException ex) {}
		    		 try { LocalDate maxtime = rsdm.getDate("maxjytime").toLocalDate();
		    		 		bk.getShuJuJiLuInfo().setJyjlmaxdate(maxtime);
		    		 } catch (java.lang.NullPointerException ex) {}
		    	}
		    } catch(java.lang.NullPointerException e) {	e.printStackTrace();
		    } catch (SQLException e) {	e.printStackTrace();
		    } catch(Exception e){	e.printStackTrace();
		    } finally {
		    	if(rsdm != null)
				try {rsdm.close();rsdm = null;
				} catch (SQLException e) {	e.printStackTrace();}
		    }
		
		return;
	}
	/*
	 * ͬ��ͨ������֤����ÿ�ճɽ�����Ϣ����Ϣ����"ͨ���Ű��ÿ�ս�����Ϣ"��
	 */
	public File refreshTDXBanKuaiVolAmoToDb (String jiaoyisuo)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "ͬ�����ɽ�������.tmp");
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		getTDXBanKuaiShuJuJiLuMaxMinDateByJiaoYiSuo (jiaoyisuo);
		
		String cjltablename;
		if(jiaoyisuo.toLowerCase().equals("sh"))			cjltablename = "ͨ���Ű��ÿ�ս�����Ϣ";
		else			cjltablename = "ͨ���Ž�����ָ��ÿ�ս�����Ϣ";
		
		 Collection<BkChanYeLianTreeNode> requiredbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK,jiaoyisuo);
		 for(BkChanYeLianTreeNode tmpnode :  requiredbk ) {
			BanKuai bk = (BanKuai) tmpnode;
			if(!bk.getBanKuaiOperationSetting().isImportdailytradingdata())				continue;
			
			String tmpbkcode = bk.getMyOwnCode();
			File tmpbkfile = null; String bkfilename = null;
			if(bk.getMyOwnCode().equalsIgnoreCase("000852"))  //ͨ���Ų�֪��Ϊʲô����֤1000��ÿ�ս�����������ļ���һ��
				 bkfilename = "62000852.TXT";
			else 
				bkfilename = (filenamerule.replaceAll("YY",jiaoyisuo.toUpperCase())).replaceAll("XXXXXX", tmpbkcode);
			tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead())   
			    continue; 

			LocalDate ldlastestdbrecordsdate = bk.getShuJuJiLuInfo().getJyjlmaxdate();
//			CachedRowSetImpl  rs = null;
//				try { 				
//					String sqlquerystat = "SELECT  MAX(��������) 	MOST_RECENT_TIME"
//							+ " FROM " + cjltablename + "  WHERE  ���� = " 
//   							+ "'"  + tmpbkcode + "'" 
//   							;
//   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//   			    	while(rs.next()) {
//   			    		java.sql.Date lastestdbrecordsdate = rs.getDate("MOST_RECENT_TIME"); //mOST_RECENT_TIME
//   			    		try {
//   			    			ldlastestdbrecordsdate = lastestdbrecordsdate.toLocalDate();
//   			    		} catch (java.lang.NullPointerException e) {	logger.debug(tmpbkcode + "û�м�¼");	}
//   			    	}
//   			    } catch(java.lang.NullPointerException e) { e.printStackTrace();
//   			    } catch (SQLException e) {e.printStackTrace();
//   			    } catch(Exception e){e.printStackTrace();
//   			    } finally {
//   			    	if(rs != null)
//						try { rs.close();rs = null;
//						} catch (SQLException e) {e.printStackTrace();}
//   			    }
				
				setVolAmoRecordsFromFileToDatabase(tmpnode,tmpbkfile,ldlastestdbrecordsdate,cjltablename,dateRule,tmprecordfile);
		}
		
		return tmprecordfile;
	}
	/*
	 * 
	 */
	private List<String> getTDXVolFilesRule ()
	{
		List<String> volamooutput = sysconfig.getTDXVOLFilesPath(); 
		try {
			String exportath = sysconfig.toUNIXpath(Splitter.on('=').trimResults().omitEmptyStrings().splitToList(volamooutput.get(0) ).get(1) );
			String filenamerule = Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(1)).get(1);
			String dateRule = getTDXDateExportDateRule(Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(2)).get(1));
			
			List<String> tdxVolFileRule = new ArrayList<String> ();
			tdxVolFileRule.add(exportath);
			tdxVolFileRule.add(filenamerule);
			tdxVolFileRule.add(dateRule);
			
			return tdxVolFileRule;
		} catch (java.lang.IndexOutOfBoundsException e) {
			return null;
		}
	}
	private String getTDXDateExportDateRule(String ruleindex) 
	{
		String value = null;
		switch (ruleindex.trim()) {
		case "0": 	value = "MM/dd/yyyy";
			break;
		case "1": 	value = "yyyy/MM/dd";
			break;
		case "2":	value = "dd/MM/yyyy";
			break;
		case "3":	value = "MM-dd-yyyy";
			break;
		case "4":	value = "yyyy-MM-dd";
			break;
		case "5":	value = "dd-MM-yyyy";
			break;
		case "6":	value = "MMddyyyy";
			break;
		case "7":	value = "yyyyMMdd";
			break;
		case "8":	value = "ddMMyyyy";
			break;
		}
		
		return value;
	}
	/*
	 * 
	 */
	public Boolean setVolAmoRecordsFromTDXExportFileToDatabase (TDXNodes node, LocalDate requiredstartdate, LocalDate requiredenddate, File tmplogfile)
	{
		String inserttablename = null;
		if(node.getType() == BkChanYeLianTreeNode.TDXBK ) 
			inserttablename = this.getBanKuaiJiaoYiLiangBiaoName( (BanKuai)node);
		else if(node.getType() == BkChanYeLianTreeNode.TDXGG ) 
			inserttablename = this.getStockJiaoYiLiangBiaoName(  (Stock)node );
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		File exportfile = this.getTDXNodesTDXSystemExportFile(node);
		
		RandomAccessFile rf = null;
        Boolean newdataimported;
		try {  
            rf = new RandomAccessFile(exportfile, "r");  
            long len = rf.length();  
            long start = rf.getFilePointer();  
            long nextend = start + len - 1;  
            String line;  
            rf.seek(nextend);  
            int c = -1;  
            boolean finalneededsavelinefound = false; //�����Ҫ��������ڵ���ʼ��
            int lineimportcount =0;
            while (nextend > start && finalneededsavelinefound == false) {
                c = rf.read();
            	if (c == '\n' || c == '\r') {  
                    line = rf.readLine();  
                    if (line != null) {  
                        @SuppressWarnings("deprecation")
						List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);

                        if( tmplinelist.size() ==7 && !tmplinelist.get(5).equals("0")) { //�п����ǰ������ݣ���0��������������¼��,�Ը�����˵����������Ҳ�У�Ҫ�ر���
                        	LocalDate curlinedate = null;
                    		try {
                    			String beforparsedate = tmplinelist.get(0);
                    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateRule);
                    			curlinedate =  LocalDate.parse(beforparsedate,formatter) ;
                    			if( curlinedate.isAfter(requiredstartdate) && curlinedate.isBefore(requiredenddate) ) {
                        			String sqlinsertstat = "INSERT INTO " + inserttablename +"(����,��������,���̼�,��߼�,��ͼ�,���̼�,�ɽ���,�ɽ���) values ("
                    						+ "'" + node.getMyOwnCode().trim() + "'" + ","
                    						+ "'" +  curlinedate + "'" + ","
                    						+ "'" +  tmplinelist.get(1).trim() + "'" + "," 
                    						+ "'" +  tmplinelist.get(2).trim() + "'" + "," 
                    						+ "'" +  tmplinelist.get(3).trim() + "'" + "," 
                    						+ "'" +  tmplinelist.get(4).trim() + "'" + "," 
                    						+ "'" +  tmplinelist.get(5).trim() + "'" + "," 
                    						+ "'" +  tmplinelist.get(6).trim() + "'"  
                    						+ ")"
                    						;
                        			logger.debug(sqlinsertstat);
                    				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
                    				lineimportcount ++;
                    				newdataimported = true;
                        		} else if(curlinedate.isBefore(requiredstartdate) ) {
                        			finalneededsavelinefound = true;
                        			if(lineimportcount == 0) {
                        				newdataimported = false;
                        				Files.append(node.getMyOwnCode() + "������¼������ʼʱ��Ϊ:" + line + System.getProperty("line.separator") ,tmplogfile,sysconfig.charSet());
                            			Files.append(node.getMyOwnCode() + "������:" + lineimportcount + "����¼" + System.getProperty("line.separator") ,tmplogfile,sysconfig.charSet());
                        			} else {
                        				Files.append(node.getMyOwnCode() + "�ɽ�����¼������ʼʱ��Ϊ:" + line + System.getProperty("line.separator") ,tmplogfile,sysconfig.charSet());
                            			Files.append(node.getMyOwnCode() + "������:" + lineimportcount + "����¼" + System.getProperty("line.separator") ,tmplogfile,sysconfig.charSet());
                        			}
                        		}
                    		} catch (Exception e) {
                    			logger.debug("���������ڵ�������");
                    		}
                        }
                    } 
                    nextend--;  
                }  
                nextend--;     rf.seek(nextend);  
            }  
        } catch (FileNotFoundException e) { newdataimported = null; e.printStackTrace();  
        } catch (IOException e)           { newdataimported = null; e.printStackTrace();  
        } finally {  
            try {  
                if (rf != null)  rf.close();  
                rf = null;
            } catch (IOException e) {   e.printStackTrace();  }  
        } 
		
 		return true;
	}
	/**
	 * 
	 * @param tmpbkcode
	 * @param tmpbkfile
	 * @param lastestdbrecordsdate
	 * @param inserttablename
	 * @param datarule
	 * @param tmprecordfile
	 * @return
	 */
	private Boolean setVolAmoRecordsFromFileToDatabase (BkChanYeLianTreeNode tmpnode, File tmpbkfile, LocalDate lastestdbrecordsdate, String inserttablename, String datarule,File tmprecordfile)
	{
		Boolean newdataimported = null;
		String tmpbkcode = tmpnode.getMyOwnCode();
		if(lastestdbrecordsdate == null) //null˵�����ݿ����滹û����ص����ݣ���ʱ������Ϊ1900����ļ����������ݶ�����
			try {
		        lastestdbrecordsdate = LocalDate.now().minus(100,ChronoUnit.YEARS);
				Files.append(tmpbkcode + "�ɽ�����¼���������м�¼��"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			} catch (Exception e1) {e1.printStackTrace();} 
		
		RandomAccessFile rf = null;
        try {  
            rf = new RandomAccessFile(tmpbkfile, "r");  
            long len = rf.length();  
            long start = rf.getFilePointer();  
            long nextend = start + len - 1;  
            String line;  
            rf.seek(nextend);  
            int c = -1;  
            boolean finalneededsavelinefound = false; //�����Ҫ��������ڵ���ʼ��
            int lineimportcount =0;
            while (nextend > start && finalneededsavelinefound == false) {
                c = rf.read();
            	if (c == '\n' || c == '\r') {  
                    line = rf.readLine();  
                    if (line != null) {  
                        @SuppressWarnings("deprecation")
						List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);

                        if( tmplinelist.size() ==7 && !tmplinelist.get(5).equals("0")) { //�п����ǰ������ݣ���0��������������¼��,�Ը�����˵����������Ҳ�У�Ҫ�ر���
                        	String open = tmplinelist.get(1).trim();
                        	String high = tmplinelist.get(2).trim();
                        	String low  = tmplinelist.get(3).trim();
                        	String close= tmplinelist.get(4).trim();
                        	String vol = tmplinelist.get(5).trim();
                        	String amo = tmplinelist.get(6).trim();
                        	
                        	if(tmpbkcode.equalsIgnoreCase("000852") && tmpnode.getType() == BkChanYeLianTreeNode.TDXBK) {//��֤1000���ļ���vol and amo ����û�о�ȷ����λ
                        		vol = String.valueOf( Double.parseDouble(vol) * 10000 );
                        		amo = String.valueOf( Double.parseDouble(amo) * 1000000 );
                        	}
                        		
                        	LocalDate curlinedate = null;
                    		try {
                    			String beforparsedate = tmplinelist.get(0);
                    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datarule);
                    			curlinedate =  LocalDate.parse(beforparsedate,formatter) ;
                    			if( curlinedate.isAfter(lastestdbrecordsdate)) {
                        			String sqlinsertstat = "INSERT INTO " + inserttablename +"(����,��������,���̼�,��߼�,��ͼ�,���̼�,�ɽ���,�ɽ���) values ("
                    						+ "'" + tmpbkcode.trim() + "'" + ","
                    						+ "'" +  curlinedate + "'" + ","
                    						+ "'" +  open + "'" + "," 
                    						+ "'" +  high + "'" + "," 
                    						+ "'" +  low + "'" + "," 
                    						+ "'" +  close + "'" + "," 
                    						+ "'" +  vol + "'" + "," 
                    						+ "'" +  amo + "'"  
                    						+ ")"
                    						;
                    				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
                    				lineimportcount ++;
                    				newdataimported = true;
                        		} else if(curlinedate.compareTo(lastestdbrecordsdate) == 0) {
                        			finalneededsavelinefound = true;
                        			if(lineimportcount == 0) {
                        				newdataimported = false;
                        				Files.append(tmpbkcode + "������¼������ʼʱ��Ϊ:" + line + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                            			Files.append(tmpbkcode + "������:" + lineimportcount + "����¼" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        			} else {
                        				Files.append(tmpbkcode + "�ɽ�����¼������ʼʱ��Ϊ:" + line + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                            			Files.append(tmpbkcode + "������:" + lineimportcount + "����¼" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        			}
                        		}
                    		} catch (Exception e) { logger.debug("���������ڵ�������");}
                        }
                    } 
                    nextend--;  
                }  
                nextend--;  
                rf.seek(nextend);  
                if (nextend == 0) {// ���ļ�ָ�������ļ���ʼ���������һ��   �ļ���һ�����ļ�̧ͷ������Ҫ����
                    //logger.debug(new String(rf.readLine().getBytes("ISO-8859-1"), charset ));  
                }  
            }  
        } catch (FileNotFoundException e) {  newdataimported = null; e.printStackTrace();  
        } catch (IOException e) { newdataimported = null;  e.printStackTrace();  
        } finally {  
            try {  
                if (rf != null)  rf.close();        rf = null;
            } catch (IOException e) { e.printStackTrace(); }  
        }  
        
        return newdataimported;
	}
/*
 * 
 */
//	public Date getTDXRelatedTableLastModfiedTime() 
//	{
//		Date lastesttdxtablesmdftime = null;
//		String sqlquerystat = " SELECT MAX(����ʱ��) FROM ͨ���Ű���б�" ;
//
//		CachedRowSetImpl rs = null; 
//		try {  
//		    	 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//		    	 
//		    	 java.sql.Timestamp tmplastmdftime = null;
//		    	 while(rs.next())
//		    		  tmplastmdftime = rs.getTimestamp("MAX(����ʱ��)");
//		    	 logger.debug(tmplastmdftime);
//		    	 lastesttdxtablesmdftime = tmplastmdftime;
//
//		    } catch(java.lang.NullPointerException e){ lastesttdxtablesmdftime = null;e.printStackTrace();
//		    } catch (SQLException e) { lastesttdxtablesmdftime = null; e.printStackTrace();
//		    } catch(Exception e) { lastesttdxtablesmdftime = null; e.printStackTrace();
//		    }  finally {
//		       if(rs != null)
//					try {
//						rs.close(); rs = null;
//					} catch (SQLException e) {e.printStackTrace();}
//		    }
//			
//		return lastesttdxtablesmdftime;
//	}
	/*
	 * �жϰ����ʲô���͵İ�飬����/���/ָ��/��ҵ/����
	 */
//	private HashMap<String,String> getActionRelatedTables1 (BanKuai bankuai,String stockcode)
//	{
//		
////		HashMap<String,String> actiontables = new HashMap<String,String> ();
////		if(bankuai != null) {
////			String bkcode = bankuai.getMyOwnCode();
////			
////			String stockvsbktable =null;
////				if(stockvsbktable == null) {	
////					CachedRowSetImpl rs = null;
////					int dy = -1;
////					try {  
////						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM ��Ʊͨ���Ÿ������Ӧ��  WHERE ������='" + bkcode + "'";
////						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
////						 while(rs.next()) {
////							 dy = rs.getInt("RESULT");
////						 }
////					} catch(java.lang.NullPointerException e) { logger.debug( "���ݿ�����ΪNULL!");
////					} catch (SQLException e) { e.printStackTrace();
////					} catch(Exception e) {e.printStackTrace();
////					} finally {
////						try {
////							if(rs != null) rs.close();
////						} catch (SQLException e) { e.printStackTrace();}
////						rs = null;
////						
////						if(dy>0)
////							stockvsbktable =  "��Ʊͨ���Ÿ������Ӧ��";
////					}
////				}
////				if(stockvsbktable == null) {
////					CachedRowSetImpl rs = null;
////					int dy = -1;
////					try {
////						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM ��Ʊͨ���ŷ�����Ӧ�� WHERE ������='" + bkcode + "'";
////						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
////						 while(rs.next()) {
////							  dy = rs.getInt("RESULT");
////						 }
////					} catch(java.lang.NullPointerException e){logger.debug( "���ݿ�����ΪNULL!");
////					} catch (SQLException e) {e.printStackTrace();
////					} catch(Exception e){e.printStackTrace();
////					} finally {
////						try {
////							if(rs != null)rs.close();
////						} catch (SQLException e) {e.printStackTrace();}
////						rs = null;
////						
////						if(dy>0)
////							stockvsbktable =  "��Ʊͨ���ŷ�����Ӧ��";
////					}
////				}
////				
////				if(stockvsbktable == null) {
////					CachedRowSetImpl rs = null;
////					int dy = -1;
////					try {
////						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM ��Ʊͨ������ҵ����Ӧ��  WHERE ������='" + bkcode + "'";
////						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
////						 while(rs.next()) {
////							 dy = rs.getInt("RESULT");
////						 }
////					} catch(java.lang.NullPointerException e){logger.debug( "���ݿ�����ΪNULL!");
////					} catch (SQLException e) {e.printStackTrace();
////					} catch(Exception e){e.printStackTrace();
////					} finally {
////						try {
////							if(rs != null)rs.close();
////						} catch (SQLException e) {e.printStackTrace();}
////						rs = null;
////						
////						if(dy>0)
////							stockvsbktable = "��Ʊͨ������ҵ����Ӧ��";
////					}
////				}
////				
////				if(stockvsbktable == null) {
////					CachedRowSetImpl rs = null;
////					int dy = -1;
////					try {
////						String sqlquerystat = "SELECT COUNT(*) AS RESULT FROM ��Ʊͨ���Ž�����ָ����Ӧ��  WHERE ������='" + bkcode + "'";
////						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
////						 while(rs.next()) {
////							 dy = rs.getInt("RESULT");
////						 }
////					} catch(java.lang.NullPointerException e) {e.printStackTrace();logger.debug( "���ݿ�����ΪNULL!");
////					} catch (SQLException e) {e.printStackTrace();
////					} catch(Exception e){e.printStackTrace();
////					} finally {
////						try {
////							if(rs != null)rs.close();
////						} catch (SQLException e) {e.printStackTrace();
////						}
////						rs = null;
////						
////						if(dy>0)
////							stockvsbktable = "��Ʊͨ���Ž�����ָ����Ӧ��";
////					}
////				}
////				if(stockvsbktable == null) {
////					try {
////						CachedRowSetImpl rsdy = null;
////						int dy = -1;
////						try {
////							String diyusqlquerystat = "SELECT *  FROM ͨ���Ű���б� WHERE ���id = '" + bkcode + "'" ;
////							 rsdy = connectdb.sqlQueryStatExecute(diyusqlquerystat);
////							 while(rsdy.next()) {
////								 dy = rsdy.getInt("��ӦTDXSWID");
////							 }
////						} catch(java.lang.NullPointerException e){logger.debug( "���ݿ�����ΪNULL!");
////						} catch (SQLException e) {logger.debug("java.sql.SQLException: ���� 1 �е�ֵ (оƬ) ִ�� getInt ʧ�ܣ����ǵ����顣");
////						} catch(Exception e){e.printStackTrace();
////						} finally {
////							try {
////								if(rsdy != null) rsdy.close();
////							} catch (SQLException e) {e.printStackTrace();}
////							rsdy = null;
////							if(dy>=1 && dy <=32) { stockvsbktable =  "��Ʊͨ���Ż�������Ϣ��Ӧ��"; actiontables.put("�������", String.valueOf(dy) );}
////						}
////					} catch (java.lang.NullPointerException e1) {}
////				}
////				
////				if(stockvsbktable != null)
////					actiontables.put("��Ʊ����Ӧ��", stockvsbktable);
////		}
//		
//	String bkcjltable = null;
//	String jys = bankuai.getSuoShuJiaoYiSuo();
//	if(jys == null)
//		bkcjltable = "";
//	else if(bankuai.getSuoShuJiaoYiSuo().toLowerCase().equals("sh"))
//		bkcjltable = "ͨ���Ű��ÿ�ս�����Ϣ";
//	else 
//		bkcjltable = "ͨ���Ž�����ָ��ÿ�ս�����Ϣ";
//	actiontables.put("���ÿ�ս�������", bkcjltable);
//	
//		if(!Strings.isNullOrEmpty(stockcode)) {
//				String gegucjltable;
//				// TODO Auto-generated method stub
////				if(stockcode.startsWith("6"))
//				if(sysconfig.isShangHaiStock(stockcode)) gegucjltable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
//				else  gegucjltable = "ͨ���������Ʊÿ�ս�����Ϣ";
//				actiontables.put("��Ʊÿ�ս�������", gegucjltable);
//		}
//		
//		return actiontables;
//	}
	private String getBanKuaiJiaoYiLiangBiaoName (BanKuai bankuai) {
		String bkcjltable = null;
		String jys = bankuai.getNodeJiBenMian().getSuoShuJiaoYiSuo();
		if(jys == null)
			bkcjltable = "";
		else if(bankuai.getNodeJiBenMian().getSuoShuJiaoYiSuo().toLowerCase().equals("sh"))
			bkcjltable = "ͨ���Ű��ÿ�ս�����Ϣ";
		else if(bankuai.getNodeJiBenMian().getSuoShuJiaoYiSuo().toLowerCase().equals("sz"))
			bkcjltable = "ͨ���Ž�����ָ��ÿ�ս�����Ϣ";
		else if(bankuai.getNodeJiBenMian().getSuoShuJiaoYiSuo().toLowerCase().equals("bk"))
			bkcjltable = "���ǻ۰��ÿ�ս�����Ϣ";

		return bkcjltable;
	}
	private String getStockJiaoYiLiangBiaoName (Stock stock) {
		String stockcode = stock.getMyOwnCode();
					String gegucjltable;
					// TODO Auto-generated method stub
//					if(stockcode.startsWith("6"))
					if(sysconfig.isShangHaiStock(stockcode)) gegucjltable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
					else  gegucjltable = "ͨ���������Ʊÿ�ս�����Ϣ";
		return gegucjltable;
	}
	/*
	 * �ҳ�ĳ��ʱ��� ��ҵ/����/���/ָ�� ͨ����ĳ���������и��ɼ�Ȩ��,���ҳɽ���,�����ϵͳ����������BanKuaiAndStockTree,����Ӧ���͸��ɵĹ�ϵ��
	 */
	public BanKuai getTDXBanKuaiGeGuOfHyGnFg(BanKuai currentbk,LocalDate selecteddatestart , LocalDate selecteddateend  )
	{
		BanKuaiAndStockTree treeallstocks = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		String currentbkcode = currentbk.getMyOwnCode();
		
		String bktypetable = currentbk.getShuJuJiLuInfo().getGuPiaoBanKuaiDuiYingBiao();
		if(bktypetable == null) {
			this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SH");
		    this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SZ");
		}
		bktypetable = currentbk.getShuJuJiLuInfo().getGuPiaoBanKuaiDuiYingBiao();
		if(bktypetable == null)
			return currentbk;
			
		String sqlquerystat1 = "";
		if(bktypetable.trim().equals("��Ʊͨ���Ż�������Ϣ��Ӧ��") ) { //�ҵ�����,�ò���
//			String dycode = actiontables.get("�������");
//			sqlquerystat1 = "SELECT a��.`��Ʊ����` , a��.`��Ʊ����` ,10 as '��ƱȨ��' \r\n" + 
//			 		"FROM ��Ʊͨ���Ż�������Ϣ��Ӧ��, a��\r\n" + 
//			 		"WHERE ��Ʊͨ���Ż�������Ϣ��Ӧ��.`����DY` = " + dycode + "\r\n" + 
//			 		"AND ��Ʊͨ���Ż�������Ϣ��Ӧ��.`��Ʊ����GPDM` = a��.`��Ʊ����`"
//			 		;
		 } else { //��������ҵָ�����
			 //from WQW
			 sqlquerystat1 = "select "+ bktypetable + ".`��Ʊ����` , a��.`��Ʊ����`, "+ bktypetable + ".`������` , \r\n"
					 			+ bktypetable + ".`��ƱȨ��` , "+ bktypetable + ".`�����ͷ`, \r\n "
					 			+ bktypetable + ".`����ʱ��`, " + bktypetable + ".`�Ƴ�ʱ��`  \r\n"
					 			+ " from "+ bktypetable + ", a��\r\n" + 
				 		"          where "+ bktypetable + ".`��Ʊ����`  = a��.`��Ʊ����`   \r\n" + 
				 		
				 		"and (  (  Date("+ bktypetable + ".`����ʱ��`) between '" +  selecteddatestart + "'  and '" +  selecteddateend + "')\r\n" + 
				 		"           		 or( ifnull("+ bktypetable + ".`�Ƴ�ʱ��`, '2099-12-31') between '" +  selecteddatestart + "'  and '" +  selecteddateend + "')\r\n" + 
				 		"           		 or( Date("+ bktypetable + ".`����ʱ��`) <= '" +  selecteddatestart + "' and ifnull(" + bktypetable + ".`�Ƴ�ʱ��`, '2099-12-31') >= '" +  selecteddateend + "' )\r\n" + 
				 		"			  )" +
				 		"           and "+ bktypetable + ".`������` =  '" + currentbkcode + "'\r\n" + 
				 		"         \r\n"  
				 		;
		 }
		 CachedRowSetImpl rs1 = connectdb.sqlQueryStatExecute(sqlquerystat1);
		try { 
			 while(rs1.next()) {  
				String tmpstockcode = rs1.getString("��Ʊ����");
				Integer weight = rs1.getInt("��ƱȨ��");
				Boolean longtou;
				try{
					longtou = rs1.getBoolean("�����ͷ");
				} catch (java.sql.SQLException e) {longtou = false;}
				LocalDate joindate = null;
				try{
					joindate = rs1.getDate("����ʱ��").toLocalDate();
				} catch (java.sql.SQLException ex1) {continue; }
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
				} catch (Exception e) {joinleftinterval = new Interval(leftdt,joindt );}
				
				if(currentbk.getStockOfBanKuai(tmpstockcode) != null ) {//�Ѿ�����
					StockOfBanKuai bkofst =  currentbk.getStockOfBanKuai(tmpstockcode);
					bkofst.setStockQuanZhong(weight);
					bkofst.setBkLongTou(longtou);
					bkofst.addInAndOutBanKuaiInterval(joinleftinterval);
				} else {
					Stock tmpstock = (Stock)treeallstocks.getSpecificNodeByHypyOrCode (tmpstockcode,BkChanYeLianTreeNode.TDXGG);
					if(tmpstock != null) {
						
						StockOfBanKuai bkofst = new StockOfBanKuai(currentbk,tmpstock);
						bkofst.setStockQuanZhong(weight);
						bkofst.setBkLongTou(longtou);
						bkofst.addInAndOutBanKuaiInterval(joinleftinterval);
						
						currentbk.addNewBanKuaiGeGu(bkofst);
					}
				}
			}
				
		} catch(java.lang.NullPointerException e){ e.printStackTrace();
//				logger.debug( "���ݿ�����ΪNULL!");
		} catch (SQLException e) {e.printStackTrace();
		} catch(Exception e){e.printStackTrace();
		} finally {
				try {if(rs1 != null)rs1.close();} catch (SQLException e) {e.printStackTrace();}
				rs1 = null;
		}
		
		return currentbk;
	}
	/*
	 * 
	 */
	
	/*
	 * ��������ֵ��Χ�ڵ����й�Ʊ����,�������������/�հ��������ֵ
	 */
	public Set<String> getStockOfRangeShiZhi(Double shizhidown, Double shizhiup, LocalDate localDate, int difference, String zhishitype) 
	{
		LocalDate requireend = localDate.with(DayOfWeek.FRIDAY);
		LocalDate requirestart = localDate.with(DayOfWeek.MONDAY);
	
		String sqlquerystat = "SELECT A.STOCKCODE AS STOCKCODE, A.WORKDAY, A.����ֵ/JILUTIAOSHU AS ������ƽ������ֵ, A.����ͨ��ֵ/JILUTIAOSHU AS ������ƽ����ͨ��ֵ\r\n" +
								"FROM (select ͨ���������Ʊÿ�ս�����Ϣ.`����` AS STOCKCODE, ͨ���������Ʊÿ�ս�����Ϣ.`��������` as workday,   sum( ͨ���������Ʊÿ�ս�����Ϣ.`�ɽ���`) AS ����ܽ��׶� ,\r\n" + 
								"sum( ͨ���������Ʊÿ�ս�����Ϣ.`�ɽ���`) AS ����ܽ�����,  \r\n" +
								"sum( ͨ���������Ʊÿ�ս�����Ϣ.`������`) AS ����ܻ����� , \r\n" +
								"sum( ͨ���������Ʊÿ�ս�����Ϣ.`����ֵ`) AS ����ֵ , \r\n" +
								"sum( ͨ���������Ʊÿ�ս�����Ϣ.`��ͨ��ֵ`) AS ����ͨ��ֵ, \r\n" + 
								"count(1) as JILUTIAOSHU  \r\n" +
								"from ͨ���������Ʊÿ�ս�����Ϣ \r\n" +
								"where ��������  between ' " + requirestart.toString() + "' and  '" + requireend.toString() + "' \r\n" +
								"GROUP BY YEAR( ͨ���������Ʊÿ�ս�����Ϣ.`��������`), WEEK( ͨ���������Ʊÿ�ս�����Ϣ.`��������`), ���� \r\n" +
								") A \r\n" +
								"WHERE A.����ͨ��ֵ/JILUTIAOSHU >= " + shizhidown + " AND A.����ͨ��ֵ/JILUTIAOSHU <" + shizhiup +" \r\n" + 
								" \r\n"  +
								"UNION \r\n" +
								" \r\n" +
								"SELECT A.STOCKCODE AS STOCKCODE, A.WORKDAY, A.����ֵ/JILUTIAOSHU AS ������ƽ������ֵ, A.����ͨ��ֵ/JILUTIAOSHU AS ������ƽ����ͨ��ֵ\r\n" +
								"FROM (select ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����` AS STOCKCODE, ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` as workday,   sum( ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`�ɽ���`) AS ����ܽ��׶� ,\r\n" + 
								" sum( ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`�ɽ���`) AS ����ܽ�����,  \r\n" +
								" sum( ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`������`) AS ����ܻ����� , \r\n" +
								" sum( ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����ֵ`) AS ����ֵ , \r\n" +
								" sum( ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��ͨ��ֵ`) AS ����ͨ��ֵ,\r\n" + 
								"  count(1) as JILUTIAOSHU \r\n" +
								" from ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ \r\n" +
								"where �������� between ' " + requirestart.toString() + "' and  '" + requireend.toString() + "' \r\n" +
								"GROUP BY YEAR( ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`), WEEK( ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`), ���� \r\n" +
								") A \r\n" +
								" \r\n" +
								"WHERE A.����ͨ��ֵ/JILUTIAOSHU >= " + shizhidown + " AND A.����ͨ��ֵ/JILUTIAOSHU <" + shizhiup +" \r\n"   
							  ;
		
		CachedRowSetImpl rsfg = null;
		Set<String> stockcodesetbyshizhirang = new HashSet<String> ();
		try {  
			 rsfg = connectdb.sqlQueryStatExecute(sqlquerystat);
			 while(rsfg.next()) {
				String stockcode  = rsfg.getString("STOCKCODE");
				stockcodesetbyshizhirang.add(stockcode);
			}
		}catch(java.lang.NullPointerException e){ e.printStackTrace();
//			logger.debug( "���ݿ�����ΪNULL!");
		} catch (SQLException e) {e.printStackTrace();
		}catch(Exception e){e.printStackTrace();
		} finally {
			try { if(rsfg != null) rsfg.close();} catch (SQLException e) {e.printStackTrace();}
			rsfg = null;
		}
		
		return stockcodesetbyshizhirang;
	}
	/*
	 * ����趨ʱ�����ںʹ�����ȵİ�����ռ�ȡ���ʵ��ʱ�䣬�����ݵ�ʱ���Ǻ���ȷ�ɵ��ú�������
	 */
	public  BanKuai getBanKuaiZhanBi (BanKuai bankuai,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{
		if(bankuai == null  )
			return null;
		
//		selecteddatestart = selecteddatestart.with(DayOfWeek.MONDAY);
//		if(!bankuai.isNodeDataAtNotCalWholeWeekMode() )
//			selecteddateend = selecteddateend.with(DayOfWeek.FRIDAY);
		
		//��������ʼ�ǿ���Ϊ�ܵ�ռ�ȣ�������/���ߵ�ռ�ȵ�����������
		if(period.equals(NodeGivenPeriodDataItem.DAY)) //�������߲�ѯ����
			; 
		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //�������߲�ѯ����
			;
		
		NodeXPeriodData nodewkperioddata = bankuai.getNodeXPeroidData(period);
//		if(nodewkperioddata.isInNotCalWholeWeekMode() == null)
//			selecteddateend = selecteddateend.with(DayOfWeek.FRIDAY);
		
		String bkcys = bankuai.getNodeJiBenMian().getSuoShuJiaoYiSuo();
		if(bkcys == null)	return bankuai;
		String bkcjltable = null;
		if(bkcys.equalsIgnoreCase("sh"))
			bkcjltable = "ͨ���Ű��ÿ�ս�����Ϣ";
		else if(bkcys.equalsIgnoreCase("sz"))
			bkcjltable = "ͨ���Ž�����ָ��ÿ�ս�����Ϣ";
		else if(bkcys.equalsIgnoreCase("BK"))	
			bkcjltable = "���ǻ۰��ÿ�ս�����Ϣ";
		
		String bkcode = bankuai.getMyOwnCode();
		String formatedstartdate = selecteddatestart.toString();
		String formatedenddate  = selecteddateend.toString();
		
		//�����ɽ����ͳɽ����SQL
		String sqlquerystat = "SELECT YEAR(t.workday) AS CALYEAR, WEEK(t.workday) AS CALWEEK, M.BKCODE AS BKCODE, t.EndOfWeekDate AS EndOfWeekDate," +
				 "M.����ܽ��׶� as ����ܽ��׶�, SUM(T.AMO) AS �����ܽ��׶� ,  M.����ܽ��׶�/SUM(T.AMO) AS CJEռ��,\r\n" +

				"M.����ܽ����� as ����ܽ�����, SUM(T.VOL) AS �����ܽ����� ,  M.����ܽ�����/SUM(T.VOL) AS VOLռ��, \r\n" +
				"  M.JILUTIAOSHU  \r\n"+
				 
				"FROM\r\n" + 
				"(\r\n" + 
				"SELECT  ͨ���Ű��ÿ�ս�����Ϣ.`��������` as workday, DATE(ͨ���Ű��ÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���Ű��ÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate, \r\n" + 
				"		  sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) AS AMO , sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) AS VOL \r\n" + 
				"FROM ͨ���Ű��ÿ�ս�����Ϣ \r\n" + 
				"WHERE ���� = '999999' AND ͨ���Ű��ÿ�ս�����Ϣ.`��������` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" + 
				" GROUP by YEARWEEK(ͨ���Ű��ÿ�ս�����Ϣ.`��������`,2)\r\n" + 
				"\r\n" + 
				"UNION ALL\r\n" + 
				"\r\n" + 
				"select  ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������` as workday,   DATE(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate, \r\n" + 
				"			sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`�ɽ���`) AS AMO, sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`�ɽ���`) AS VOL \r\n" + 
				"from ͨ���Ž�����ָ��ÿ�ս�����Ϣ\r\n" + 
				"where ���� = '399001' AND ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" + 
				" group by YEARWEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������`,2)\r\n" + 
				") T,\r\n" + 
				"\r\n" + 
				"(select " + bkcjltable + ".`����` AS BKCODE, " + bkcjltable + ".`��������` as workday,  "
						+ "sum( " + bkcjltable + ".`�ɽ���`) AS ����ܽ��׶� , \r\n"
						+ "sum( " + bkcjltable + ".`�ɽ���`) AS ����ܽ�����, \r\n"
						+ " count(1) as JILUTIAOSHU \r\n"
						+ "from " + bkcjltable + "\r\n" +
						
				"where ���� = '" + bkcode + "' AND " + bkcjltable + ".`��������` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" + 
				"GROUP BY YEARWEEK( " + bkcjltable + ".`��������`,2)\r\n" + 
				") M\r\n" + 
				" WHERE YEARWEEK(T.WORKDAY,2) = YEARWEEK(M.WORKDAY,2) \r\n" + 
				"  GROUP BY YEARWEEK(t.workday,2)"
				;
							
		logger.debug("Ϊ���" + bankuai.getMyOwnCode() + bankuai.getMyOwnName() + "Ѱ�Ҵ�" + selecteddatestart.toString() + "��" + selecteddateend.toString() + "ռ�����ݣ�");
		
		String sqlquerystatfx = "SELECT ������¼�ص��ע.`����`, COUNT(*) AS RESULT FROM ������¼�ص��ע \r\n" + 
				" WHERE ��Ʊ����='" + bkcode + "'" + "\r\n" + 
				" AND ������¼�ص��ע.`����` between '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" +
				" GROUP BY YEAR(������¼�ص��ע.`����`), WEEK(������¼�ص��ע.`����`) "
				;
		
		CachedRowSetImpl rs = null;
		CachedRowSetImpl rsfx = null;
		Boolean hasfengxiresult = false;
		
		try {
			//����
			rsfx = connectdb.sqlQueryStatExecute(sqlquerystatfx);
			while(rsfx.next()) {
				java.sql.Date recdate = rsfx.getDate("����");
				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(recdate);
				Integer reccount = rsfx.getInt("RESULT");
				nodewkperioddata.addFxjgToPeriod(wknum, reccount);
			}
			
			//����
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			while(rs.next()) {
				java.util.Date lastdayofweek = rs.getDate("EndOfWeekDate");
//				ZonedDateTime zdtime = lastdayofweek.toLocalDate().with(DayOfWeek.FRIDAY).atStartOfDay(ZoneOffset.UTC);
				java.util.TimeZone china =  java.util.TimeZone.getDefault();
//				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(lastdayofweek, china, Locale.CHINA);
				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(lastdayofweek);
				Date startdate = wknum.getStart(); Date endstart = wknum.getEnd();
				
				Double bankuaicje = rs.getDouble("����ܽ��׶�");
				Double dapancje = rs.getDouble("�����ܽ��׶�");
				Double cjezb = rs.getDouble("CJEռ��");

				Double bankuaicjl = rs.getDouble("����ܽ�����");
				Double dapancjl = rs.getDouble("�����ܽ�����");
				Double cjlzb = rs.getDouble("VOLռ��");
				
				int exchangedaysnumber = rs.getInt("JILUTIAOSHU");
				
				NodeGivenPeriodDataItem bkperiodrecord = new NodeGivenPeriodDataItemForJFC( bkcode, NodeGivenPeriodDataItem.WEEK,
						wknum, 0.0, 0.0,  0.0,  0.0,bankuaicjl, bankuaicje );
				
//				NodeGivenPeriodDataItem bkperiodrecord = new NodeGivenPeriodDataItemForTA4J( bkcode, NodeGivenPeriodDataItem.WEEK,
//						zdtime, PrecisionNum.valueOf(0.0), PrecisionNum.valueOf(0.0),  PrecisionNum.valueOf(0.0),  PrecisionNum.valueOf(0.0), 
//						PrecisionNum.valueOf(bankuaicjl), PrecisionNum.valueOf(bankuaicje) );
				
				bkperiodrecord.setNodeToDpChenJiaoErZhanbi(cjezb);
				bkperiodrecord.setNodeToDpChenJiaoLiangZhanbi(cjlzb);
				bkperiodrecord.setUplevelChengJiaoEr(dapancje);
				bkperiodrecord.setUplevelChengJiaoLiang(dapancjl);
				bkperiodrecord.setExchangeDaysNumber(exchangedaysnumber);
				
				nodewkperioddata.addNewXPeriodData(bkperiodrecord);
				
				
				wknum = null;
				bkperiodrecord = null;
				bankuaicje = null;
				dapancje = null;
				lastdayofweek = null;
				bankuaicjl = null;
				dapancjl = null;
				
				
			}
		}catch(java.lang.NullPointerException e){e.printStackTrace();
	    } catch(Exception e){e.printStackTrace();
	    }  finally {
	    	try {
				rs.close();	rsfx.close();
				rs = null;rsfx = null;bkcjltable = null;
			} catch (SQLException e) {e.printStackTrace();}
	    }

		return bankuai;
	}
	/*
	 * ��Ʊ�԰��İ���ռ��
	 */
	public StockOfBanKuai getGeGuZhanBiOfBanKuai(BanKuai bankuai, StockOfBanKuai stockofbk,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{//��������ʼ�ǿ���Ϊ�ܵ�ռ�ȣ�������/���ߵ�ռ�ȵ�����������
		if(period.equals(NodeGivenPeriodDataItem.DAY)) //�������߲�ѯ����
			; 
		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //�������߲�ѯ����
			;
		
//		StockOfBanKuai stockofbk = bankuai.getBanKuaiGeGu(stock.getMyOwnCode());
		NodeXPeriodData nodedayperioddata = stockofbk.getNodeXPeroidData(period);
		
		String stockcode = stockofbk.getMyOwnCode();
		String stockvsbktable = bankuai.getShuJuJiLuInfo().getGuPiaoBanKuaiDuiYingBiao();
		if(stockvsbktable == null) {
			this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SH");
		    this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SZ");
		}
		String bkcjetable = this.getBanKuaiJiaoYiLiangBiaoName(bankuai);
		String gegucjetable = this.getStockJiaoYiLiangBiaoName(stockofbk.getStock() );
		String bknametable = "ͨ���Ű���б�";
		
		String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart);
		String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend);
		
		String sqlquerystat = "";
		if(stockvsbktable.trim().equals("��Ʊͨ���Ż�������Ϣ��Ӧ��") ) { //�ҵ�����
//			String dycode = actiontables.get("�������");
//			sqlquerystat = "";
//			return stockofbk;
		 } else { //��������ҵָ�����
			 //�����ɽ���� �ɽ�����SQL
		 sqlquerystat = "SELECT Y.year, Y.week,Y.EndOfWeekDate, Y.��Ʊ����, Y.������, Y.stock_amount, Y.stock_vol, Y.`��ƱȨ��` ,Y.`��Ʊ����`,\r\n" + 
				"Z.���� , Z.�������,  z.category_amount, Y.stock_amount/ z.category_amount ratio, z.category_vol, Y.stock_vol/z.category_vol volratio \r\n" + 
				"FROM \r\n" + 
				"(select X.year, X.week, X.EndOfWeekDate, X.��Ʊ����, X.������, X.stock_amount,X.stock_vol, X.`��ƱȨ��` ,a��.`��Ʊ����`\r\n" + 
				"from (select year(" +  gegucjetable    + ".`��������`) as year,week(" +  gegucjetable    + ".`��������`) as week, \r\n" + 
				"DATE(" +  gegucjetable    + ".�������� + INTERVAL (6 - DAYOFWEEK(" +  gegucjetable    + ".��������)) DAY) EndOfWeekDate, \r\n" + 
				"" +  stockvsbktable + ".`��Ʊ����` , " +  stockvsbktable + ".`������` , \r\n" + 
				"sum(" +  gegucjetable    + ".`�ɽ���`) stock_amount, sum(" +  gegucjetable    + ".`�ɽ���`) stock_vol," +  stockvsbktable + ".`��ƱȨ��`\r\n" + 
				"from " +  stockvsbktable + ", " +  gegucjetable    + "\r\n" + 
				"where " +  stockvsbktable + ".`��Ʊ����`   = " +  gegucjetable    + ".`����`\r\n" + 
				"		and Date(" +  gegucjetable    + ".`��������`) >= Date(" +  stockvsbktable + ".`����ʱ��`)\r\n" + 
				"		and " +  gegucjetable    + ".`��������` <  ifnull(" +  stockvsbktable + ".`�Ƴ�ʱ��`, '2099-12-31')\r\n" + 
				"		and " +  gegucjetable    + ".`��������` BETWEEN  '" +  formatedstartdate + "' AND '" +  formatedenddate + "'\r\n" + 
				"		and " +  stockvsbktable + ".`������` =  '"  + bankuai.getMyOwnCode() +"'\r\n" + 
				"		and " +  stockvsbktable + ".`��Ʊ����` = '"+ stockcode +"'\r\n" + 
				"         \r\n" + 
				"group by year(" +  gegucjetable    + ".`��������`) ,week(" +  gegucjetable    + ".`��������`) , \r\n" + 
				"		  " +  stockvsbktable + ".`��Ʊ����`, " +  stockvsbktable + ".`������`\r\n" + 
				"order by year(" +  gegucjetable    + ".`��������`) ,week(" +  gegucjetable    + ".`��������`) , \r\n" + 
				"			" +  stockvsbktable + ".`��Ʊ����`, " +  stockvsbktable + ".`������`) X,\r\n" + 
				"a��\r\n" + 
				"where   X.`��Ʊ����` = a��.`��Ʊ����` ) Y,\r\n" + 
				"						\r\n" + 
				"(select year(" +  bkcjetable + ".`��������`) as year, week(" +  bkcjetable + ".`��������`) as week,\r\n" + 
				"DATE(" +  bkcjetable + ".�������� + INTERVAL (6 - DAYOFWEEK(" +  bkcjetable + ".��������)) DAY) EndOfWeekDate, \r\n" + 
				"" +  bkcjetable + ".`����` , " + bknametable + ".`�������`,   sum(" +  bkcjetable + ".`�ɽ���`) category_amount, sum(" +  bkcjetable + ".`�ɽ���`) category_vol\r\n" + 
				"from " +  bkcjetable + ", " + bknametable + "\r\n" + 
				"where " +  bkcjetable + ".`����` = " + bknametable  + ".`���ID`\r\n" + 
				"		and " +  bkcjetable + ".`��������` BETWEEN '" +  formatedstartdate + "' AND '" +  formatedenddate + "'\r\n" + 
				"		and " +  bkcjetable + ".`����` =  '"  + bankuai.getMyOwnCode() + "'\r\n" + 
				"group by year(" +  bkcjetable + ".`��������`), week(" +  bkcjetable + ".`��������`)," +  bkcjetable + ".`����`\r\n" + 
				"order by year(" +  bkcjetable + ".`��������`), week(" +  bkcjetable + ".`��������`), " +  bkcjetable + ".`����`) Z \r\n" + 
				"         \r\n" + 
				"where Y.year = Z. year\r\n" + 
				"    	and Y.week = Z.week\r\n" + 
				"order by Y.year, Y.week"
				;
		 }
		
		try{
			logger.debug(sqlquerystat);
			logger.debug("Ϊ����" + stockofbk.getMyOwnCode()  + "Ѱ�Ҵ�" + selecteddatestart.toString() + "��" + selecteddateend.toString() + "��" + bankuai.getMyOwnCode() + "��ռ�����ݣ�");
		} catch (java.lang.NullPointerException e) {
			
		}
		

		CachedRowSetImpl rsfg = null;

		try {  
			 rsfg = connectdb.sqlQueryStatExecute(sqlquerystat);

			 while(rsfg.next()) {
				double stcokcje = rsfg.getDouble("stock_amount");
				double bkcje = rsfg.getDouble("category_amount");
				double stcokcjl = rsfg.getDouble("stock_vol");
				double bkcjl = rsfg.getDouble("category_vol");
				java.sql.Date  lastdayofweek = rsfg.getDate("EndOfWeekDate");
				ZonedDateTime zdtime = lastdayofweek.toLocalDate().with(DayOfWeek.FRIDAY).atStartOfDay(ZoneOffset.UTC);
				org.jfree.data.time.Week recordwk = new org.jfree.data.time.Week (lastdayofweek);
				
				NodeGivenPeriodDataItem sobperiodrecord = new NodeGivenPeriodDataItemForJFC( stockofbk.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
						recordwk, 0.0, 0.0,  0.0, 0.0, 
						stcokcjl, stcokcje );
//				NodeGivenPeriodDataItem sobperiodrecord = new NodeGivenPeriodDataItemForTA4J( stockofbk.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
//						zdtime, DoubleNum.valueOf(0.0), DoubleNum.valueOf(0.0),  DoubleNum.valueOf(0.0),  DoubleNum.valueOf(0.0), 
//						PrecisionNum.valueOf(stcokcjl), PrecisionNum.valueOf(stcokcje) );
				
				sobperiodrecord.setUplevelChengJiaoEr(bkcje);
				sobperiodrecord.setUplevelChengJiaoLiang(bkcjl);
			
				nodedayperioddata.addNewXPeriodData(sobperiodrecord);
			}
		}catch(java.lang.NullPointerException e){ 
			e.printStackTrace();
//			logger.debug( "���ݿ�����ΪNULL!");
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			try {
				if(rsfg != null)
					rsfg.close();
//				rsfx.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rsfg = null;
			nodedayperioddata = null;
//			rsfx = null;
		}
		
		//�����Ǹ��ɶ԰����ƶ�����(Ĭ��������)�����沿����Ϊ���������������ݣ�����������ɶԴ��̵�����
//		StockOfBanKuai stockofbankuai = bkcode.getBanKuaiGeGu(stock.getMyOwnCode());
		
//		this.getStockZhanBi (stock.getStock(), selecteddatestart, selecteddateend,addposition,period);
		
		return stockofbk;
	}
	/*
	 * //�����Ǹ��ɶ԰���ָ������(Ĭ��������)�����沿����Ϊ���������������ݣ�����������ɶԴ��̵�����
	 */
	public Stock getStockZhanBi(Stock stock,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{
		if(stock == null)
			return null;
		
		selecteddatestart = selecteddatestart.with(DayOfWeek.MONDAY);
		
		if(period.equals(NodeGivenPeriodDataItem.DAY)) //�������߲�ѯ����
			; 
		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //�������߲�ѯ����
			;
		
		NodeXPeriodData nodewkperioddata = stock.getNodeXPeroidData(period);
		if( nodewkperioddata.isInNotCalWholeWeekMode() == null)
			selecteddateend = selecteddateend.with(DayOfWeek.FRIDAY);

		String stockcode = stock.getMyOwnCode();
		String bkcjltable;
		if(sysconfig.isShangHaiStock(stockcode))
			bkcjltable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
		else
			bkcjltable = "ͨ���������Ʊÿ�ս�����Ϣ";
		
		String formatedstartdate = selecteddatestart.toString();
		String formatedenddate  = selecteddateend.toString();
		
		//�����ɽ����ͳɽ����SQL
		String sqlquerystat = "SELECT YEAR(t.workday) AS CALYEAR, WEEK(t.workday,1) AS CALWEEK, M.BKCODE AS BKCODE, \r\n"
				+ "t.StartOfWeekDate AS StartOfWeekDate, t.EndOfWeekDate AS EndOfWeekDate, \r\n" +
				 "M.����ܽ��׶� as ����ܽ��׶�, SUM(T.AMO) AS �����ܽ��׶� ,  M.����ܽ��׶�/SUM(T.AMO) AS CJEռ��, \r\n" +
				"M.����ܽ����� as ����ܽ�����, SUM(T.VOL) AS �����ܽ����� ,  M.����ܽ�����/SUM(T.VOL) AS VOLռ��, \r\n" +
				"M.����ܻ����� as ����ܻ�����, M.�����������ͨ������ AS �����������ͨ������, \r\n"
				+ "M.����ֵ/M.SHIZHIJILUTIAOSHU   as ��ƽ������ֵ, "
				+ "M.����ͨ��ֵ/M.SHIZHIJILUTIAOSHU   as ��ƽ����ͨ��ֵ, "
				+ "M.JILUTIAOSHU , M.SHIZHIJILUTIAOSHU  , "
				+ "M.������ǵ���,M.����С�ǵ���   ,M.��ͣ,M.��ͣ        \r\n" +
				 
				"FROM\r\n" + 
				"(\r\n" + 
				"select ͨ���Ű��ÿ�ս�����Ϣ.`��������` as workday, "
				+ " 	DATE(ͨ���Ű��ÿ�ս�����Ϣ.�������� + INTERVAL (2 - DAYOFWEEK(ͨ���Ű��ÿ�ս�����Ϣ.��������)) DAY) as StartOfWeekDate, \r\n"
				+ "		DATE(ͨ���Ű��ÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���Ű��ÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate, \r\n" + 
				"		sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) AS AMO , sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) AS VOL \r\n" + 
				"from ͨ���Ű��ÿ�ս�����Ϣ\r\n" + 
				"where ���� = '999999'\r\n" + 
				" AND ͨ���Ű��ÿ�ս�����Ϣ.`��������` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n"+
				" group by YEARWEEK(ͨ���Ű��ÿ�ս�����Ϣ.`��������`,2)\r\n" + 
				"\r\n" + 
				"UNION ALL\r\n" + 
				"\r\n" + 
				"select  ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������` as workday,   "
				+"		DATE(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�������� + INTERVAL (2 - DAYOFWEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������)) DAY) as StartOfWeekDate, \r\n"
				+" 		DATE(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate, \r\n" + 
				"		sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`�ɽ���`) AS AMO, sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`�ɽ���`) AS VOL \r\n" + 
				"from ͨ���Ž�����ָ��ÿ�ս�����Ϣ\r\n" + 
				"where ���� = '399001' "
				+" AND ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n"+ 
				"group by YEARWEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������`,2)\r\n" + 
				") T,\r\n" + 
				"\r\n" + 
				"(select " + bkcjltable + ".`����` AS BKCODE, " + bkcjltable + ".`��������` as workday,  "
						+ " sum( " + bkcjltable + ".`�ɽ���`) AS ����ܽ��׶� , \r\n"
						+ " sum( " + bkcjltable + ".`�ɽ���`) /100 AS ����ܽ�����,  \r\n"
						+ " sum( " + bkcjltable + ".`������`) AS ����ܻ����� , \r\n"
						+ " sum( " + bkcjltable + ".`������ͨ������`) AS �����������ͨ������ , \r\n"
						+ " sum( " + bkcjltable + ".`����ֵ`) AS ����ֵ , \r\n"
						+ " sum( " + bkcjltable + ".`��ͨ��ֵ`) AS ����ͨ��ֵ, \r\n"
						+ "  COUNT(1) as JILUTIAOSHU ,\r\n"
						+ "  COUNT(" + bkcjltable + ".`����ֵ`) AS SHIZHIJILUTIAOSHU,"
						+ ""
						+ "max(" + bkcjltable + ".`�ǵ���`) as ������ǵ���, \r\n"
						+ "min(" + bkcjltable + ".`�ǵ���`) as ����С�ǵ���, \r\n"
						+ "COUNT( IF(" + bkcjltable + ".`�ǵ���` >= 9.0,1,NULL) ) AS ��ͣ, \r\n"
						+ "COUNT( IF(" + bkcjltable + ".`�ǵ���` <= -9.0,1,NULL) ) AS ��ͣ  \r\n"
						+ " from " + bkcjltable + "\r\n" +  
				" Where ���� = '" + stockcode + "'\r\n" + 
				" AND " + bkcjltable + ".`��������` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" +
				" GROUP BY  YEARWEEK( " + bkcjltable +".`��������`,2)\r\n" + 
				") M\r\n" + 
				" WHERE   YEARWEEK(T.WORKDAY,2) = YEARWEEK(M.WORKDAY,2)\r\n" + 
				" GROUP BY YEARWEEK(t.workday,2)"
				;
				
		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		Boolean hasfengxiresult = false;
		try {
			while(rs.next()) {
				java.sql.Date startdayofweek = rs.getDate("StartOfWeekDate");
				java.sql.Date lastdayofweek = rs.getDate("EndOfWeekDate");
				
				double stockcje = rs.getDouble("����ܽ��׶�") ;
				double dapancje = rs.getDouble("�����ܽ��׶�") ;
				double cjezb = rs.getDouble("CJEռ��") ;
				double stockcjl = rs.getDouble("����ܽ�����") ;
				double dapancjl = rs.getDouble("�����ܽ�����") ;
				double cjlzb = rs.getDouble("VOLռ��") ;
				double huanshoulv = rs.getDouble("����ܻ�����") ;
				double huanshoulvfree = rs.getDouble("�����������ͨ������") ;
				double pingjunzongshizhi = rs.getDouble("��ƽ������ֵ") ;
				double pingjunliutongshizhi = rs.getDouble("��ƽ����ͨ��ֵ") ;
				double periodhighestzhangdiefu = rs.getDouble("������ǵ���") ;
				double periodlowestzhangdiefu = rs.getDouble("����С�ǵ���") ;
				int exchengdaysnumber = rs.getInt("JILUTIAOSHU") ;
				int zhangtingnum = rs.getInt("��ͣ") ;
				int dietingnum = rs.getInt("��ͣ") ;
				
				org.jfree.data.time.Week recordwk = new org.jfree.data.time.Week (lastdayofweek);

				NodeGivenPeriodDataItem stockperiodrecord = new NodeGivenPeriodDataItemForJFC( stock.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
						recordwk, 0.0, 0.0,  0.0,  0.0, 
						stockcjl, stockcje );
				
//				NodeGivenPeriodDataItem stockperiodrecord = new NodeGivenPeriodDataItemForTA4J( stock.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
//						zdtime, PrecisionNum.valueOf(0.0), PrecisionNum.valueOf(0.0),  PrecisionNum.valueOf(0.0),  PrecisionNum.valueOf(0.0), 
//						PrecisionNum.valueOf(stockcjl), PrecisionNum.valueOf(stockcje) );
				
				stockperiodrecord.setNodeToDpChenJiaoErZhanbi(cjezb);
				stockperiodrecord.setNodeToDpChenJiaoLiangZhanbi(cjlzb);
				stockperiodrecord.setUplevelChengJiaoEr(dapancje);
				stockperiodrecord.setUplevelChengJiaoLiang(dapancjl);
				stockperiodrecord.setPeriodHighestZhangDieFu(periodhighestzhangdiefu);
				stockperiodrecord.setPeriodLowestZhangDieFu(periodlowestzhangdiefu);
				stockperiodrecord.setExchangeDaysNumber(exchengdaysnumber);
				stockperiodrecord.setZhangTingNumber(zhangtingnum);
				stockperiodrecord.setDieTingNumber(dietingnum);
				stockperiodrecord.setHuanShouLv(huanshoulv);
				stockperiodrecord.setHuanShouLvFree(huanshoulvfree);
				stockperiodrecord.setLiuTongShiZhi(pingjunliutongshizhi);
				stockperiodrecord.setZongShiZhi(pingjunzongshizhi);
				
				nodewkperioddata.addNewXPeriodData(stockperiodrecord);
				
				stockperiodrecord = null;
			}
		} catch(java.lang.NullPointerException e){ e.printStackTrace();
	    } catch(Exception e){e.printStackTrace();
	    } finally {
	    	try { rs.close(); rs = null;} catch (SQLException e) {e.printStackTrace();}
	    	bkcjltable = null;
	    	nodewkperioddata = null; 
	    }

		return stock;
	}
	public BanKuai getBanKuaiGeGuZhanBi(BanKuai bk,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{
		if(period.equals(NodeGivenPeriodDataItem.DAY)) //�������߲�ѯ����
			; 
		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //�������߲�ѯ����
			;
		
		getBanKuaiGeGuZhanBiByJys( bk, selecteddatestart, selecteddateend, period,"SH");
		getBanKuaiGeGuZhanBiByJys( bk, selecteddatestart, selecteddateend, period,"SZ");
		
		return bk;
	}
	/*
	 * 
	 */
	private BanKuai getBanKuaiGeGuZhanBiByJys(BanKuai bk,LocalDate selecteddatestart,LocalDate selecteddateend,String period,String jys)
	{
		List<BkChanYeLianTreeNode> bkshgegu = bk.getAllGeGuOfBanKuaiInHistoryByJiaoYiSuo(jys);
		if(bkshgegu == null  || bkshgegu.isEmpty())
			return bk;
		
		String bkggstr = "";
		for(BkChanYeLianTreeNode stockofbk : bkshgegu) {
			//ϵͳ��̨����Զ������˸��ɵ����ݣ�����������һ�Σ��˷�ʱ�䣬Ҫ�ж�
//			Stock stock = (Stock) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(stockofbk.getMyOwnCode(), BkChanYeLianTreeNode.TDXGG);
			Stock stock = ((StockOfBanKuai)stockofbk).getStock();
			if(stock == null) continue;
			
			NodeXPeriodData nodewkperioddata = stock.getNodeXPeroidData(period);
			if(nodewkperioddata.getAmoRecordsStartDate() != null) //��������ݣ��Ͳ����ˣ����������Ƿ������������и����Լ���ͬ������֤�������򵥵㡣
				continue;
			
			bkggstr  = bkggstr + "'" + stockofbk.getMyOwnCode() +  "', ";
		}
		if(bkggstr.isBlank() )
			return bk;
		bkggstr = bkggstr.substring(0, bkggstr.length() -2);
		
		String bkcjltable;
		if(jys.equalsIgnoreCase("SH"))
			bkcjltable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
		else
			bkcjltable = "ͨ���������Ʊÿ�ս�����Ϣ";
		
		String formatedstartdate = selecteddatestart.toString();
		String formatedenddate  = selecteddateend.toString();
		//�����ɽ����ͳɽ����SQL
				String sqlquerystat = "SELECT YEAR(t.workday) AS CALYEAR, WEEK(t.workday,1) AS CALWEEK, M.BKCODE AS BKCODE, \r\n"
						+ "t.StartOfWeekDate AS StartOfWeekDate, t.EndOfWeekDate AS EndOfWeekDate, \r\n" +
						 "M.����ܽ��׶� as ����ܽ��׶�, SUM(T.AMO) AS �����ܽ��׶� ,  M.����ܽ��׶�/SUM(T.AMO) AS CJEռ��, \r\n" +
						"M.����ܽ����� as ����ܽ�����, SUM(T.VOL) AS �����ܽ����� ,  M.����ܽ�����/SUM(T.VOL) AS VOLռ��, \r\n" +
						"M.����ܻ����� as ����ܻ�����, M.�����������ͨ������ AS �����������ͨ������, \r\n"
						+ "M.����ֵ/M.SHIZHIJILUTIAOSHU   as ��ƽ������ֵ, "
						+ "M.����ͨ��ֵ/M.SHIZHIJILUTIAOSHU   as ��ƽ����ͨ��ֵ, \r\n"
						+ "M.JILUTIAOSHU , M.SHIZHIJILUTIAOSHU  , \r\n"
						+ "M.������ǵ���,M.����С�ǵ���   ,M.��ͣ,M.��ͣ        \r\n" +
						 
						"FROM\r\n" + 
						"(\r\n" + 
						"select ͨ���Ű��ÿ�ս�����Ϣ.`��������` as workday, "
						+ " 	DATE(ͨ���Ű��ÿ�ս�����Ϣ.�������� + INTERVAL (2 - DAYOFWEEK(ͨ���Ű��ÿ�ս�����Ϣ.��������)) DAY) as StartOfWeekDate, \r\n"
						+ "		DATE(ͨ���Ű��ÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���Ű��ÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate, \r\n" + 
						"		sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) AS AMO , sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) AS VOL \r\n" + 
						"from ͨ���Ű��ÿ�ս�����Ϣ\r\n" + 
						"where ���� = '999999'\r\n" + 
						" AND ͨ���Ű��ÿ�ս�����Ϣ.`��������` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n"+
						" group by YEARWEEK(ͨ���Ű��ÿ�ս�����Ϣ.`��������`,2)\r\n" + 
						"\r\n" + 
						"UNION ALL\r\n" + 
						"\r\n" + 
						"select  ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������` as workday,   "
						+"		DATE(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�������� + INTERVAL (2 - DAYOFWEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������)) DAY) as StartOfWeekDate, \r\n"
						+" 		DATE(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate, \r\n" + 
						"		sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`�ɽ���`) AS AMO, sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`�ɽ���`) AS VOL \r\n" + 
						"from ͨ���Ž�����ָ��ÿ�ս�����Ϣ\r\n" + 
						"where ���� = '399001' "
						+" AND ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n"+ 
						"group by YEARWEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������`,2)\r\n" + 
						") T,\r\n" + 
						"\r\n" + 
						"(select " + bkcjltable + ".`����` AS BKCODE, " + bkcjltable + ".`��������` as workday,  "
								+ " sum( " + bkcjltable + ".`�ɽ���`) AS ����ܽ��׶� , \r\n"
								+ " sum( " + bkcjltable + ".`�ɽ���`) /100 AS ����ܽ�����,  \r\n"
								+ " sum( " + bkcjltable + ".`������`) AS ����ܻ����� , \r\n"
								+ " sum( " + bkcjltable + ".`������ͨ������`) AS �����������ͨ������ , \r\n"
								+ " sum( " + bkcjltable + ".`����ֵ`) AS ����ֵ , \r\n"
								+ " sum( " + bkcjltable + ".`��ͨ��ֵ`) AS ����ͨ��ֵ, \r\n"
								+ "  COUNT(1) as JILUTIAOSHU ,\r\n"
								+ "  COUNT(" + bkcjltable + ".`����ֵ`) AS SHIZHIJILUTIAOSHU,"
								+ ""
								+ "max(" + bkcjltable + ".`�ǵ���`) as ������ǵ���, \r\n"
								+ "min(" + bkcjltable + ".`�ǵ���`) as ����С�ǵ���, \r\n"
								+ "COUNT( IF(" + bkcjltable + ".`�ǵ���` >= 9.0,1,NULL) ) AS ��ͣ, \r\n"
								+ "COUNT( IF(" + bkcjltable + ".`�ǵ���` <= -9.0,1,NULL) ) AS ��ͣ  \r\n"
								+ " from " + bkcjltable + "\r\n" +  
						" Where ����  IN (" + bkggstr + " ) \r\n" + 
						" AND " + bkcjltable + ".`��������` BETWEEN '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" +
						" GROUP BY ����,  YEARWEEK( " + bkcjltable +".`��������`,2)\r\n" + 
						") M\r\n" + 
						" WHERE   YEARWEEK(T.WORKDAY,2) = YEARWEEK(M.WORKDAY,2)\r\n" + 
						" GROUP BY bkcode,  YEARWEEK(t.workday,2)"
						;
		CachedRowSetImpl rs = null; Stock stock = null; String curstockcode = ""; NodeXPeriodData nodewkperioddata = null;
		try {
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			while(rs.next()) {
				String stockcode = rs.getString("bkcode");
				if(!curstockcode.equalsIgnoreCase(stockcode) )  {
					curstockcode = stockcode;
					stock = (Stock) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(stockcode, BkChanYeLianTreeNode.TDXGG);
					if(stock == null) continue;
					else nodewkperioddata = stock.getNodeXPeroidData(period);
				}
				java.sql.Date startdayofweek = rs.getDate("StartOfWeekDate");
				java.sql.Date lastdayofweek = rs.getDate("EndOfWeekDate");
				double stockcje = rs.getDouble("����ܽ��׶�") ;
				double dapancje = rs.getDouble("�����ܽ��׶�") ;
				double cjezb = rs.getDouble("CJEռ��") ;
				double stockcjl = rs.getDouble("����ܽ�����") ;
				double dapancjl = rs.getDouble("�����ܽ�����") ;
				double cjlzb = rs.getDouble("VOLռ��") ;
				double huanshoulv = rs.getDouble("����ܻ�����") ;
				double huanshoulvfree = rs.getDouble("�����������ͨ������");
				double pingjunzongshizhi = rs.getDouble("��ƽ������ֵ") ;
				double pingjunliutongshizhi = rs.getDouble("��ƽ����ͨ��ֵ") ;
				double periodhighestzhangdiefu = rs.getDouble("������ǵ���") ;
				double periodlowestzhangdiefu = rs.getDouble("����С�ǵ���") ;
				int exchengdaysnumber = rs.getInt("JILUTIAOSHU") ;
				int zhangtingnum = rs.getInt("��ͣ") ;
				int dietingnum = rs.getInt("��ͣ") ;
				
				org.jfree.data.time.Week recordwk = new org.jfree.data.time.Week (lastdayofweek);

				NodeGivenPeriodDataItem stockperiodrecord = new NodeGivenPeriodDataItemForJFC( stock.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
						recordwk, 0.0, 0.0,  0.0,  0.0, 
						stockcjl, stockcje );
				
//				NodeGivenPeriodDataItem stockperiodrecord = new NodeGivenPeriodDataItemForTA4J( stock.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
//						zdtime, PrecisionNum.valueOf(0.0), PrecisionNum.valueOf(0.0),  PrecisionNum.valueOf(0.0),  PrecisionNum.valueOf(0.0), 
//						PrecisionNum.valueOf(stockcjl), PrecisionNum.valueOf(stockcje) );
				
				stockperiodrecord.setNodeToDpChenJiaoErZhanbi(cjezb);
				stockperiodrecord.setNodeToDpChenJiaoLiangZhanbi(cjlzb);
				stockperiodrecord.setUplevelChengJiaoEr(dapancje);
				stockperiodrecord.setUplevelChengJiaoLiang(dapancjl);
				stockperiodrecord.setPeriodHighestZhangDieFu(periodhighestzhangdiefu);
				stockperiodrecord.setPeriodLowestZhangDieFu(periodlowestzhangdiefu);
				stockperiodrecord.setExchangeDaysNumber(exchengdaysnumber);
				stockperiodrecord.setZhangTingNumber(zhangtingnum);
				stockperiodrecord.setDieTingNumber(dietingnum);
				stockperiodrecord.setHuanShouLv(huanshoulv);
				stockperiodrecord.setHuanShouLvFree(huanshoulvfree);
				stockperiodrecord.setLiuTongShiZhi(pingjunliutongshizhi);
				stockperiodrecord.setZongShiZhi(pingjunzongshizhi);
				
				nodewkperioddata.addNewXPeriodData(stockperiodrecord);
				
				stockperiodrecord = null;

			}
		} catch(java.lang.NullPointerException e){ e.printStackTrace();
	    } catch(Exception e){e.printStackTrace();
	    } finally {
	    	try { rs.close(); rs = null;	} catch (SQLException e) {e.printStackTrace();}
	    	bkcjltable = null;
	    }

		return bk;
	}
	/**
	 * 
	 */
//	public Stock getStockGuanZhuJiLu (Stock stock,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
//	{
//		if(stock == null)
//			return null;
//		
//		selecteddatestart = selecteddatestart.with(DayOfWeek.MONDAY);
//		if(!stock.isNodeDataAtNotCalWholeWeekMode())
//			selecteddateend = selecteddateend.with(DayOfWeek.FRIDAY);
//		
//		if(period.equals(NodeGivenPeriodDataItem.DAY)) //�������߲�ѯ����
//			; 
//		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //�������߲�ѯ����
//			;
//		
//		NodeXPeriodData nodewkperioddata = stock.getNodeXPeroidData(period);
//
//		String stockcode = stock.getMyOwnCode();
//		String guanzhubk = sysconfig.getCurZdyBanKuaiOfGuanZhuGeGu();
//	    String sqlquerystat6="SELECT zdgz.����ʱ��   AS ����,"
//					+ "'�����ע' AS ACTION ,"
//					+ "null, "
//					+ "null,"
//					+ "null,"
//					+ "'������¼�ص��ע'"
//					+ " FROM ��Ʊͨ�����Զ������Ӧ��  zdgz" 
//					+ " WHERE zdgz.��Ʊ���� ="  + "'" + stockcode + "'"
//					+ " AND �Զ�����="  + "'" + guanzhubk + "'" 
//
//					+ " \n UNION ALL \n"
//					;
//	     String sqlquerystat7="SELECT zdgz.�Ƴ�ʱ�� AS ���� ,"
//					+ "'�Ƴ���ע' AS ACTION,"
//					+ "null, "
//					+ "null,"
//					+ "null,"
//					+ "'������¼�ص��ע'"
//					+ " FROM ��Ʊͨ�����Զ������Ӧ��  zdgz" 
//					+ " WHERE zdgz.��Ʊ���� ="  + "'" + stockcode + "'"
//					+ " AND �Զ�����="  + "'" + guanzhubk + "'" 
//					+ " AND zdgz.�Ƴ�ʱ�� IS NOT NULL \n"
//					+ " ORDER BY 1 DESC,5,3 " //desc
//					;
//	     String sqlquerystatgz = sqlquerystat6 + sqlquerystat7;
//		
//		//��ע����
//		CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystatgz);
//		try {
//			while(rs.next()) {
//				java.sql.Date recdate = rs.getDate("����");
//				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(recdate);
//				
//				String action = rs.getString("ACTION").trim();
//				if(action.equals("�����ע"))
//					((StockNodesXPeriodData)nodewkperioddata).addGzjlToPeriod(wknum, 1);
//				else if(action.equals("�Ƴ���ע"))
//					((StockNodesXPeriodData)nodewkperioddata).addGzjlToPeriod(wknum, 0);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//	    	try {
//				rs.close();
//				rs = null;
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//	    }
//		
//		return stock;
//	}
	/*
	 * 
	 */
	public Stock getStockFengXiJieGuo (Stock stock,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{
		if(stock == null)
			return null;
		
		selecteddatestart = selecteddatestart.with(DayOfWeek.MONDAY);
		
		if(period.equals(NodeGivenPeriodDataItem.DAY)) //�������߲�ѯ����
			; 
		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //�������߲�ѯ����
			;
		
		NodeXPeriodData nodewkperioddata = stock.getNodeXPeroidData(period);
		if( nodewkperioddata.isInNotCalWholeWeekMode() == null )
			selecteddateend = selecteddateend.with(DayOfWeek.FRIDAY);

		String stockcode = stock.getMyOwnCode();
		
		String sqlquerystatfx = "SELECT ������¼�ص��ע.`����`,    COUNT(*) AS RESULT FROM ������¼�ص��ע \r\n" + 
				" WHERE ��Ʊ����='" + stockcode + "'" + "\r\n" + 
				" AND ������¼�ص��ע.`����` between '" + selecteddatestart + "' AND '" + selecteddateend + "' \r\n" +
				" GROUP BY YEAR(������¼�ص��ע.`����`), WEEK(������¼�ص��ע.`����`) "
				;
		
//		 String sqlquerystat5="SELECT zdgz.����   AS ����,"
//					+ "zdgz.�����Ƴ���־ AS ACTION,"
//					+ "zdgz.ԭ������,"
//					+ "zdgz.ID,"
//					+ "null,"
//					+ "'������¼�ص��ע'"
//					+ " FROM ������¼�ص��ע  zdgz"
//					+ " WHERE zdgz.��Ʊ���� =" + "'" + stockcode + "'"
//					;
		
		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sqlquerystatfx);
		try {
			while(rsfx.next()) {
				java.sql.Date recdate = rsfx.getDate("����");
				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(recdate);
				Integer reccount = rsfx.getInt("RESULT");
				nodewkperioddata.addFxjgToPeriod(wknum, reccount);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	    	try {
				rsfx.close();
				rsfx = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
		
		return stock;
	}
	/*
	 * 
	 */
//	public Stock getStockMaiRuMaiChuJiLu (Stock stock,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
//	{
//		if(stock == null)
//			return null;
//		
//		selecteddatestart = selecteddatestart.with(DayOfWeek.MONDAY);
//		if(!stock.isNodeDataAtNotCalWholeWeekMode())
//			selecteddateend = selecteddateend.with(DayOfWeek.FRIDAY);
//		
//		if(period.equals(NodeGivenPeriodDataItem.DAY)) //�������߲�ѯ����
//			; 
//		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //�������߲�ѯ����
//			;
//		
//		NodeXPeriodData nodewkperioddata = stock.getNodeXPeroidData(period);
//
//		String stockcode = stock.getMyOwnCode();
//
//		String sqlquerystat2="SELECT czjl.����, " +		""
//				+ "IF(czjl.����������־,'����','����')  AS ����,"
//				+ " czjl.ԭ������,"
//				+ " czjl.ID,"
//				+ " czjl.�����˺�,"
//				+ "'������¼����'" 
//				+ " FROM ������¼���� czjl "
//				+ "	WHERE czjl.��Ʊ���� =" + "'" + stockcode + "'"
//				+ " AND czjl.������� > 0 "
//				+ " AND czjl.�ҵ� = FALSE"  
//				+ "  AND `����`  between '" + selecteddatestart + "' AND '" + selecteddateend + "' \r\n" 
//				
//				+ " \n UNION ALL \n" 
//				;
//
//		String sqlquerystat3="SELECT rqczjl.����,"
//				+ " IF(rqczjl.����������־,'����','����')  AS ����,"
//				+ " rqczjl.ԭ������,"
//				+ " rqczjl.ID,"
//				+ " rqczjl.�����˺�,"
//				+ " '������¼��ȯ����'"
//				+ " FROM ������¼��ȯ���� rqczjl"
//				+ "	WHERE rqczjl.��Ʊ���� =" + "'" + stockcode + "'"
//				+ " AND rqczjl.������� > 0 "
//				+ " AND rqczjl.�ҵ� = FALSE"
//				+ "  AND `����`  between '" + selecteddatestart + "' AND '" + selecteddateend + "' \r\n" 
//				
//				+ " \n UNION ALL \n"
//				;
//
//		String sqlquerystat4="SELECT rzczjl.����,"
//				+ "IF(rzczjl.����������־,'����','����')  AS ����,"
//				+ "rzczjl.ԭ������,"
//				+ "rzczjl.ID,"
//				+ "rzczjl.�����˺�,"
//				+ "'������¼��������'"
//				+ " FROM ������¼�������� rzczjl"
//				+ " WHERE rzczjl.��Ʊ���� =" + "'" + stockcode + "'"
//				+ " AND rzczjl.������� > 0 "
//				+ " AND rzczjl.�ҵ� = FALSE"
//				+ "  AND `����`  between '" + selecteddatestart + "' AND '" + selecteddateend + "' \r\n" 
//				;
//		String sqlquerystatfx = sqlquerystat2 + sqlquerystat3 + sqlquerystat4;
//		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sqlquerystatfx);
//		try {
//			while(rsfx.next()) {
//				java.sql.Date recdate = rsfx.getDate("����");
//				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(recdate);
//				String maimai = rsfx.getString("����");
//				if(maimai.equals("����")) {
//					((StockNodesXPeriodData)nodewkperioddata).addMaiRuJiLu(wknum, 1);
//				} else
//				if(maimai.equals("����")) {
//					((StockNodesXPeriodData)nodewkperioddata).addMaiChuJiLu(wknum, 0);
//				}
//			}
//		} catch (SQLException e) {e.printStackTrace();
//		} finally {
//	    	try {rsfx.close();rsfx = null;} catch (SQLException e) {e.printStackTrace();}
//	    }
//		
//		return stock;
//	}

	/*
	 * ������������ĳ�����̶����ڸ��ɺʹ��̵�ռ������
	 */
	public NodeGivenPeriodDataItem getStockNoFixPerioidZhanBiByWeek(Stock stock,LocalDate formatedstartdate,LocalDate formatedenddate,String period)
	{
		if(period.equals(NodeGivenPeriodDataItem.DAY)) //�������߲�ѯ����
			; 
		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //�������߲�ѯ����
			;
		
//		StockNodeXPeriodData nodewkperioddata = (StockNodeXPeriodData)stock.getNodeXPeroidData(period);
	
		String stockcode = stock.getMyOwnCode();
		String bkcjltable;
//		if(stockcode.startsWith("6"))
		if( sysconfig.isShangHaiStock(stockcode) )
			bkcjltable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
		else
			bkcjltable = "ͨ���������Ʊÿ�ս�����Ϣ";
	
		
		String sqlquerystat = "SELECT  M.BKCODE AS BKCODE, '" + formatedstartdate + "' AS STARTDAY, '" + formatedenddate + "' AS ENDDAY , " +
				 "M.����ܽ��׶� as ����ܽ��׶�, SUM(T.AMO) AS �����ܽ��׶� ,  M.����ܽ��׶�/SUM(T.AMO) AS ռ��, \r\n" +
				"M.����ܽ����� as ����ܽ�����, SUM(T.VOL) AS �����ܽ����� ,  M.����ܽ�����/SUM(T.VOL) AS VOLռ��, \r\n" +
				"M.����ܻ����� as ����ܻ�����, M.����ֵ/M.JILUTIAOSHU as ��ƽ������ֵ, M.����ͨ��ֵ/M.JILUTIAOSHU as ��ƽ����ͨ��ֵ, M.JILUTIAOSHU , M.������ǵ���,M.����С�ǵ���      \r\n" +
				 
				"FROM\r\n" + 
				"(\r\n" + 
				"select  ͨ���Ű��ÿ�ս�����Ϣ.`��������` as workday, DATE(ͨ���Ű��ÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���Ű��ÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate, \r\n" + 
				"		  sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) AS AMO , sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) AS VOL \r\n" + 
				"from ͨ���Ű��ÿ�ս�����Ϣ\r\n" + 
				"WHERE ���� = '999999'\r\n" + 
				"AND  ͨ���Ű��ÿ�ս�����Ϣ.`��������` BETWEEN'" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" + 
				"\r\n" + 
				"UNION ALL\r\n" + 
				"\r\n" + 
				"select  ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������` as workday,   DATE(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate, \r\n" + 
				"			sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`�ɽ���`) AS AMO, sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`�ɽ���`) AS VOL \r\n" + 
				"from ͨ���Ž�����ָ��ÿ�ս�����Ϣ\r\n" + 
				"where ���� = '399001'\r\n" + 
				"AND ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������` BETWEEN'" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" + 
				") T,\r\n" + 
				"\r\n" + 
				"(select " + bkcjltable + ".`����` AS BKCODE, " 
						+ " sum( " + bkcjltable + ".`�ɽ���`) AS ����ܽ��׶� , \r\n"
						+ " sum( " + bkcjltable + ".`�ɽ���`) AS ����ܽ�����,  \r\n"
						+ " sum( " + bkcjltable + ".`������`) AS ����ܻ����� , \r\n"
						+ " sum( " + bkcjltable + ".`����ֵ`) AS ����ֵ , \r\n"
						+ " sum( " + bkcjltable + ".`��ͨ��ֵ`) AS ����ͨ��ֵ, \r\n"
						+ "  count(1) as JILUTIAOSHU ,\r\n"
						+ "max(" + bkcjltable + ".`�ǵ���`) as ������ǵ���, \r\n"
						+ "min(" + bkcjltable + ".`�ǵ���`) as ����С�ǵ��� \r\n"
						+ " from " + bkcjltable + "\r\n" +  
				"where ���� = '" + stockcode + "'\r\n" + 
				"AND " + bkcjltable + ".`��������` BETWEEN'" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" + 
				") M\r\n"  
				;
		logger.debug(sqlquerystat); 
		//��������
		CachedRowSetImpl rs = null;
		NodeGivenPeriodDataItem stockperiodrecord = null;
		try{
				rs = connectdb.sqlQueryStatExecute(sqlquerystat);
				while(rs.next()) {
					double stockcje = rs.getDouble("����ܽ��׶�");
					double dapancje = rs.getDouble("�����ܽ��׶�");
//					java.sql.Date lastdayofweek = rs.getDate("STARTDAY");
					java.sql.Date lastdayofweek =  java.sql.Date.valueOf(formatedstartdate);
					ZonedDateTime zdtime = lastdayofweek.toLocalDate().with(DayOfWeek.FRIDAY).atStartOfDay(ZoneOffset.UTC);
					org.jfree.data.time.Week recordwk = new org.jfree.data.time.Week (lastdayofweek);
					double stockcjl = rs.getDouble("����ܽ�����");
					double dapancjl = rs.getDouble("�����ܽ�����");
					double huanshoulv = rs.getDouble("����ܻ�����");
					double pingjunzongshizhi = rs.getDouble("��ƽ������ֵ");
					double pingjunliutongshizhi = rs.getDouble("��ƽ����ͨ��ֵ");
					double periodhighestzhangdiefu = rs.getDouble("������ǵ���");
					double periodlowestzhangdiefu = rs.getDouble("����С�ǵ���");
					int exchengdaysnumber = rs.getInt("JILUTIAOSHU");
		
					stockperiodrecord = new NodeGivenPeriodDataItemForJFC( stock.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
							recordwk,0.0, 0.0,  0.0,  0.0, 
							stockcjl, stockcje );
					
//					stockperiodrecord = new NodeGivenPeriodDataItemForTA4J( stock.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
//							zdtime, DoubleNum.valueOf(0.0), DoubleNum.valueOf(0.0),  DoubleNum.valueOf(0.0),  DoubleNum.valueOf(0.0), 
//							PrecisionNum.valueOf(stockcjl), PrecisionNum.valueOf(stockcje) );
					
					stockperiodrecord.setUplevelChengJiaoEr(dapancje);
					stockperiodrecord.setUplevelChengJiaoLiang(dapancjl);
					stockperiodrecord.setPeriodHighestZhangDieFu(periodhighestzhangdiefu);
					stockperiodrecord.setPeriodLowestZhangDieFu(periodlowestzhangdiefu);
					stockperiodrecord.setExchangeDaysNumber(exchengdaysnumber);
					
//					nodewkperioddata.addNewXPeriodData(stockperiodrecord);
					
					
				}
			}catch(java.lang.NullPointerException e){ 
		    	e.printStackTrace();
		    } catch(Exception e){
		    	e.printStackTrace();
		    }  finally {
		    	try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    	
		    	bkcjltable = null;
//		    	nodewkperioddata = null; 
		    }

		return stockperiodrecord;
	}
	/*
	 * ���ڸ��ɣ����ڸ�Ȩ���⣬һ����Ȩ�����ݿ��д�ŵ�ÿ�ս������ݾͲ�׼ȷ������ÿ�ս������ݻ��Ǵ�TDX������TXT�����ȡ�Ƚ�׼ȷ��
	 */
	public Stock getStockDailyKXianZouShiFromCsv (Stock stock, LocalDate requiredstartday, LocalDate requiredendday, String period)
	{
		//ȷ����Ҫ��ȡ��ʱ���Ⱥ����еĿ�ȵĲ���ֵ�������ظ���ȡ�Ѿ��е����ݣ���Լʱ��
		NodeXPeriodData nodedayperioddata = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
		
		TemporalField fieldCH = WeekFields.of(Locale.CHINA).dayOfWeek();
		requiredstartday = requiredstartday.with(fieldCH, 1); //ȷ��K��������ʾ������һ��
//		LocalDate notcalwholewkmodedate = nodedayperioddata.isInNotCalWholeWeekMode();
//		if(notcalwholewkmodedate == null)	requiredendday = requiredendday.with(fieldCH, 7);
//		else requiredendday = notcalwholewkmodedate;
		
		String csvfilepath = sysconfig.getCsvPathOfExportedTDXVOLFiles();
		String stockcode = stock.getMyOwnCode();
		String jiaoyisuo = stock.getNodeJiBenMian().getSuoShuJiaoYiSuo();
//		String optTable = null;
//		if(jiaoyisuo.toLowerCase().equals("sz")  )
//			optTable = "ͨ���������Ʊÿ�ս�����Ϣ";
//		else if(jiaoyisuo.toLowerCase().equals("sh") )
//			optTable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
		
		List<String> volamooutput = getTDXVolFilesRule ();
		if(volamooutput == null)
			return stock;
		String filenamerule = volamooutput.get(1);
		String csvfilename = (filenamerule.replaceAll("YY",jiaoyisuo.toUpperCase())).replaceAll("XXXXXX", stockcode).replace("TXT", "CSV") ;
		File csvfile = new File(csvfilepath + "/" + csvfilename);
		if (!csvfile.exists() || csvfile.isDirectory() || !csvfile.canRead()) {  
				logger.info("��ȡ" + csvfilename + "��������û�л��" + stock.getMyOwnName() +  "��K�����ݡ�");
				return stock;
		} 
		
		CSVReader stockcsvreader = null;
		try {
			stockcsvreader = new CSVReaderBuilder(new FileReader(csvfilepath + "/" + csvfilename))
			        .withSkipLines(2)
			        .withCSVParser(new CSVParserBuilder().withSeparator(',').withEscapeChar('\'').build())
			        .build();
		} catch (FileNotFoundException e) {e.printStackTrace();}

		try {
			LocalDate lastdaydate = requiredstartday.with(fieldCH, 6); //
//			org.ta4j.core.TimeSeries nodenewohlc = new BaseTimeSeries.SeriesBuilder().withName(stock.getMyOwnCode()).build();
			OHLCSeries nodenewohlc = new OHLCSeries (stock.getMyOwnCode());
			int linenumber =0;
			
//			String [] linevalue ;
//			while ( (linevalue = stockcsvreader.readNext())!=null ) {
			List<String[]> records = stockcsvreader.readAll();
			for (String[] linevalue : records) {
				linenumber ++;
				
				String recordsdate = linevalue[0];
				LocalDate curlinedate;
				try {
//					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern(volamooutput.get(2).trim() );
					curlinedate = LocalDate.parse(recordsdate, formatter);
					
					if(linenumber == 1) //�����һ�е����ڣ�����K�ߵļ���ʹ��
						lastdaydate = curlinedate.with(fieldCH, 6); //
				} catch (java.time.format.DateTimeParseException e) {break;}
				
				try {
					if(curlinedate.isBefore(requiredstartday) || curlinedate.isAfter(requiredendday) ) continue;		
				} catch (java.lang.NullPointerException e) { continue;}
				// Ϊ����ohlc��ʱ��������
				java.sql.Date sqldate = null;
				try {
//					DateFormat format = new SimpleDateFormat("ddMMyyyy", Locale.CHINA);
					String formatstr = volamooutput.get(2).trim();
					DateFormat format = new SimpleDateFormat(formatstr, Locale.CHINA);
					 sqldate = new java.sql.Date(format.parse(recordsdate).getTime()  );
				} catch (ParseException e) { e.printStackTrace(); }
				org.jfree.data.time.Day recordday = new org.jfree.data.time.Day (sqldate);
				ZonedDateTime zdtime = sqldate.toLocalDate().atStartOfDay(ZoneOffset.UTC);
					
				String open = linevalue[1];
				String high = linevalue[2];
				String low = linevalue[3];
				String close = linevalue[4];
				String cjl = linevalue[5];
				String cje = linevalue[6];
				
				NodeGivenPeriodDataItem stockperiodrecord = new NodeGivenPeriodDataItemForJFC( stock.getMyOwnCode(), NodeGivenPeriodDataItem.DAY,
						recordday, Double.valueOf( open), Double.valueOf(high), Double.valueOf( low), Double.valueOf( close), 
						Double.valueOf(cjl), Double.valueOf(cje) );
				 
//				NodeGivenPeriodDataItem stockperiodrecord = new NodeGivenPeriodDataItemForTA4J( stock.getMyOwnCode(), NodeGivenPeriodDataItem.WEEK,
//						zdtime, PrecisionNum.valueOf(open), PrecisionNum.valueOf(high),  PrecisionNum.valueOf(low),  PrecisionNum.valueOf(close), 
//						PrecisionNum.valueOf(cjl), PrecisionNum.valueOf(cje) );
				 
				 nodedayperioddata.addNewXPeriodData(stockperiodrecord);
				 
				 //��������OHLC����
				 WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
				 int lastdayweekNumber = lastdaydate.get(weekFields.weekOfWeekBasedYear());
				 int curweekNumber = curlinedate.get(weekFields.weekOfWeekBasedYear());
				 if(lastdayweekNumber == curweekNumber) {
//					 nodenewohlc.addBar( (NodeGivenPeriodDataItemForJFC)stockperiodrecord); //�Ȱѱ��ܵ��������ݴ洢�������ȴ����洦��
					 nodenewohlc.add( (NodeGivenPeriodDataItemForJFC)stockperiodrecord); //�Ȱѱ��ܵ��������ݴ洢�������ȴ����洦��
				 } else {
					 //�����ˣ�������һ������OHLC ����
					 this.getTDXNodesWeeklyKXianZouShiForJFC(stock, lastdaydate.with(DayOfWeek.FRIDAY), nodenewohlc);
					 
					 nodenewohlc = null;
//					 nodenewohlc = new BaseTimeSeries.SeriesBuilder().withName(stock.getMyOwnCode()).build();
					 nodenewohlc = new OHLCSeries (stock.getMyOwnCode());
					 lastdaydate = curlinedate.with(DayOfWeek.FRIDAY);
					 nodenewohlc.add( (NodeGivenPeriodDataItemForJFC)stockperiodrecord);
					 stockperiodrecord = null;
				 }
					 
			}
			 //Ŀǰ�㷨���һ��Ҫ����ѭ������ܼ���
			this.getTDXNodesWeeklyKXianZouShiForJFC(stock, lastdaydate.with(DayOfWeek.FRIDAY), nodenewohlc);
			
			//Former ZhangFu can be get from Netease, but for the lastest, sometimes Neteast data is still not imported,
			//so must cal the zhangfu by CSV data.
			this.calTDXNodesWeekyHighestAndLowestZhangFuForJFC (stock, lastdaydate.with(DayOfWeek.FRIDAY), nodenewohlc);;
			
			nodenewohlc = null;
			records = null;
		} catch (IOException e) {e.printStackTrace();}

		return stock;
	}
//	public Stock getStockDailyAmoVol (Stock stock, LocalDate requiredstartday, LocalDate requiredendday, String period)
//	{
//		//��������ʼ�ǿ���Ϊ�����ڵ�K�����ݣ�
//				if(period.equals(NodeGivenPeriodDataItem.WEEK)) //�������߲�ѯ����
//					; 
//				else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //�������߲�ѯ����
//					;
//
//				String stockcode = stock.getMyOwnCode();
//				String bkcjltable;
////				if(stockcode.startsWith("6"))
//				if( sysconfig.isShangHaiStock(stockcode) )
//					bkcjltable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
//				else
//					bkcjltable = "ͨ���������Ʊÿ�ս�����Ϣ"; 
//				
//				TemporalField fieldCH = WeekFields.of(Locale.CHINA).dayOfWeek();
//				try{
//					requiredstartday = requiredstartday.with(fieldCH, 1); //ȷ��K��������ʾ������һ��
//				} catch (java.lang.NullPointerException e) {
//					return stock;
//				}
//				if(!stock.isNodeDataAtNotCalWholeWeekMode() )
//					requiredendday = requiredendday.with(fieldCH, 7);
//				
//				NodeXPeriodData nodedayperioddata = stock.getNodeXPeroidData(period);
//
//				String sqlquerystat = "SELECT *  FROM " + bkcjltable + " \r\n" + 
//						"WHERE ����='" + stockcode + "'" + "\r\n" + 
//						"AND ��������  between'" + requiredstartday + "' AND '" + requiredendday + "'"
//						;
//				
//				CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
//				OHLCSeries nodenewohlc = new OHLCSeries (stock.getMyOwnCode());
//				try {  
//					 rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
//					
//					 while(rsfx.next()) {
//						 double cje = rsfx.getDouble("�ɽ���");
//						 double cjl = rsfx.getDouble("�ɽ���");
//						 java.sql.Date actiondate = rsfx.getDate("��������");
//						 ZonedDateTime zdtime = actiondate.toLocalDate().atStartOfDay(ZoneOffset.UTC);
//						 org.jfree.data.time.Day recordday = new org.jfree.data.time.Day (actiondate);
//						 
//						 NodeGivenPeriodDataItem bkperiodrecord = new NodeGivenPeriodDataItemForJFC( bk.getMyOwnCode(), NodeGivenPeriodDataItem.DAY,
//								 recordday, PrecisionNum.valueOf(open), PrecisionNum.valueOf(high),  PrecisionNum.valueOf(low),  PrecisionNum.valueOf(close), 
//									PrecisionNum.valueOf(cjl), PrecisionNum.valueOf(cje) );
//						 
////						 NodeGivenPeriodDataItem bkperiodrecord = new NodeGivenPeriodDataItemForTA4J( bk.getMyOwnCode(), NodeGivenPeriodDataItem.DAY,
////									zdtime, PrecisionNum.valueOf(open), PrecisionNum.valueOf(high),  PrecisionNum.valueOf(low),  PrecisionNum.valueOf(close), 
////									PrecisionNum.valueOf(cjl), PrecisionNum.valueOf(cje) );
//						 
//						 
//						 nodedayperioddata.addNewXPeriodData(bkperiodrecord);
//						 
//						 //��������OHLC����
//						 WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
//						 int lastdayweekNumber = lastdaydate.get(weekFields.weekOfWeekBasedYear());
//						 int curweekNumber = actiondate.toLocalDate().get(weekFields.weekOfWeekBasedYear());
//						 if(lastdayweekNumber == curweekNumber) {
//							 try{
////								 nodenewohlc.addBar( (NodeGivenPeriodDataItemForTA4J)bkperiodrecord);
//								 nodenewohlc.add( (NodeGivenPeriodDataItemForJFC)bkperiodrecord);
//							 } catch (java.lang.IllegalArgumentException e) {
//								 e.printStackTrace();
//							 }
//						 } else {
//							 //�����ˣ�������һ������OHLC ����
//							 this.getTDXNodesWeeklyKXianZouShiForJFC(bk, lastdaydate.with(DayOfWeek.FRIDAY), nodenewohlc);
//							 
//							 nodenewohlc = null;
//							 nodenewohlc = new OHLCSeries (bk.getMyOwnCode());
//							 lastdaydate = actiondate.toLocalDate().with(DayOfWeek.FRIDAY);
//							 nodenewohlc.add( (NodeGivenPeriodDataItemForJFC)bkperiodrecord);
//						 }
//						 
//						 bkperiodrecord = null;
//					 }
//					 //Ŀǰ�㷨���һ��Ҫ����ѭ������ܼ���
//					 this.getTDXNodesWeeklyKXianZouShiForJFC(bk, lastdaydate.with(DayOfWeek.FRIDAY), nodenewohlc);
//					 
//						
//				}catch(java.lang.NullPointerException e){ 
//					e.printStackTrace();
////					logger.debug( "���ݿ�����ΪNULL!");
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}catch(Exception e){
//					e.printStackTrace();
//				} finally {
//					try {
//						if(rsfx != null)
//							rsfx.close();
//					} catch (SQLException e) {
//						e.printStackTrace();
//					}
//					rsfx = null;
//					searchtable = null;
//					nodenewohlc = null;
//				}
//
//				return bk;
//	}
	/*
	 * 
	 */
	private void calTDXNodesWeekyHighestAndLowestZhangFuForJFC(Stock stock, LocalDate friday, OHLCSeries nodenewohlc) 
	{
		//This part is for get stock highest and lowest zhangfu in the week. //Temperailly abandon.
		StockXPeriodDataForJFC nodexdata =  (StockXPeriodDataForJFC) stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);

		Integer spcohlcdataindex = nodexdata.getIndexOfSpecificDateOHLCData(friday);
		OHLCItem weeklyohlcdatalast;
		try {
			weeklyohlcdatalast = (OHLCItem) nodexdata.getOHLCData().getDataItem(spcohlcdataindex-1);
		} catch (java.lang.IndexOutOfBoundsException e) {
//			e.printStackTrace();
			return;
		}
		Double lastwkclose = weeklyohlcdatalast.getCloseValue();
		Double curwkclose = ((OHLCItem) nodenewohlc.getDataItem(0)  ).getCloseValue();
		Double weeklyhighzhangfu = (  curwkclose  - lastwkclose) / lastwkclose;
		Double weeklylowestzhangfu = (  curwkclose - lastwkclose) / lastwkclose;
		for(int i=1;i<nodenewohlc.getItemCount();i++) {
				OHLCItem curohlctmp = (OHLCItem) nodenewohlc.getDataItem(i);
				OHLCItem lastohlctmp = (OHLCItem) nodenewohlc.getDataItem(i-1);
				
				Double lastdayclose = lastohlctmp.getCloseValue();
				Double curdayclose = curohlctmp.getCloseValue();
				Double zhangfu = (curdayclose - lastdayclose ) /  lastdayclose; 
				if( zhangfu > weeklyhighzhangfu)
					weeklyhighzhangfu = zhangfu;
				if( zhangfu < weeklylowestzhangfu)
					weeklylowestzhangfu = zhangfu;
		}
		((StockNodesXPeriodData)nodexdata).addPeriodHighestZhangDieFu (friday,weeklyhighzhangfu);
		((StockNodesXPeriodData)nodexdata).addPeriodLowestZhangDieFu (friday,weeklylowestzhangfu);
	}
	/*
	 *
	 */
//	private TDXNodes getTDXNodesWeeklyKXianZouShiForTA4J (TDXNodes tdxnode, LocalDate friday, OHLCSeries nodenewohlc)
//	{
////		int newcount = nodenewohlc.getBarCount();
//		int newcount = nodenewohlc.getItemCount();
//		if(newcount == 0)
//			return tdxnode;
//		
//		TDXNodesXPeriodDataForTA4J nodexdata =  (TDXNodesXPeriodDataForTA4J) tdxnode.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
//		//��ǰ�洦�����߳ɽ��������ݵ�ʱ���Ѿ�����OHLC�����ݣ�����0������Ҫ����������ҵ������������
//		Integer spcohlcdataindex = nodexdata.getIndexOfSpecificDateOHLCData(friday, 0);
//		if(spcohlcdataindex == null)
//			return tdxnode;
//		
//		if(spcohlcdataindex != null) {
//			Bar wkohlcdata = nodexdata.getSpecificDateOHLCData(friday,0);
//			
//			if(! (wkohlcdata.getClosePrice().doubleValue() == 0 &&
//				wkohlcdata.getMaxPrice().doubleValue() == 0 &&
//				wkohlcdata.getMinPrice().doubleValue() == 0 &&
//				wkohlcdata.getOpenPrice().doubleValue() == 0) ) 
//			{
//				return tdxnode; //������0��˵����ȷ�������Ѿ���ǰ���ҳ����ˣ�ֱ���˳���
//			}
//		} 
//
//		Num weeklyopen = null;
//		try{
//			Bar newohlcdata0 = nodenewohlc.getBar(0);
//			weeklyopen = newohlcdata0.getOpenPrice();
//		} catch (java.lang.IndexOutOfBoundsException e) {
//			e.printStackTrace();
//		}
//		
//		
//		Bar newohlcdatalast = nodenewohlc.getBar(nodenewohlc.getBarCount() - 1);
//		Num weeklyclose = newohlcdatalast.getClosePrice();
//		
//		Num weeklyhigh= DoubleNum.valueOf(0.0); Num weeklylow= DoubleNum.valueOf(10000000.0);
//		for(int i=0;i<nodenewohlc.getBarCount();i++) {
//			Bar newohlctmp = nodenewohlc.getBar(i) ;
//			if( newohlctmp.getMaxPrice().doubleValue() > weeklyhigh.doubleValue())
//				weeklyhigh = newohlctmp.getMaxPrice();
//			
//			if( newohlctmp.getMinPrice().doubleValue() < weeklylow.doubleValue())
//				weeklylow = newohlctmp.getMinPrice();
//		}
//		
//		Bar wkohlcdata = nodexdata.getSpecificDateOHLCData(friday,0);
//		try{
//			wkohlcdata.updateBarOHLC(weeklyopen, weeklyhigh, weeklylow, weeklyclose, null, null);
//		} catch (java.lang.NullPointerException e) {
//			e.printStackTrace();
//		}
//		
// 		return tdxnode;
//	}
	/*
	 * ������Ҫ�޸�
	 */
	private TDXNodes getTDXNodesWeeklyKXianZouShiForJFC (TDXNodes tdxnode, LocalDate friday, OHLCSeries nodenewohlc)
	{
		int newcount = nodenewohlc.getItemCount();
		if(newcount == 0)	return tdxnode;
		
		TDXNodesXPeriodDataForJFC nodexdata =  (TDXNodesXPeriodDataForJFC) tdxnode.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		//��ǰ�洦�����߳ɽ��������ݵ�ʱ���Ѿ�����OHLC�����ݣ�����0������Ҫ����������ҵ������������
		Integer spcohlcdataindex = nodexdata.getIndexOfSpecificDateOHLCData(friday);
		if(spcohlcdataindex != null) {
			OHLCItem wkohlcdata = nodexdata.getSpecificDateOHLCData(friday);
			double wkclose = wkohlcdata.getCloseValue(); double wkhigh = wkohlcdata.getHighValue();
			double wklow = wkohlcdata.getLowValue(); double wkopen = wkohlcdata.getOpenValue() ;
			if(! (wkclose == 0 && wkhigh == 0 && wklow == 0 && wkopen	== 0) ) 
				return tdxnode; //������0��˵����ȷ�������Ѿ���ǰ���ҳ����ˣ�ֱ���˳���
		} 
		
		try { //����0���Ͱ�ԭ���������Ƴ���Ȼ��������ݷŽ�ȥ
			if(spcohlcdataindex != null) 
				nodexdata.getOHLCData().remove(spcohlcdataindex.intValue());
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {e.printStackTrace();}
		
		Double weeklyopen = null;
		try{
			OHLCItem newohlcdata0 = (OHLCItem) nodenewohlc.getDataItem(0);
			weeklyopen = newohlcdata0.getOpenValue();
		} catch (java.lang.IndexOutOfBoundsException e) {e.printStackTrace();}

		//Close Of this week
		OHLCItem newohlcdatalast = (OHLCItem) nodenewohlc.getDataItem(nodenewohlc.getItemCount()-1); 
		Double weeklyclose = newohlcdatalast.getCloseValue();

		Double weeklyhigh=0.0; Double weeklylow=10000000.0; 
		for(int i=0;i<nodenewohlc.getItemCount();i++) {
			OHLCItem newohlctmp = (OHLCItem) nodenewohlc.getDataItem(i);
			if( newohlctmp.getHighValue() > weeklyhigh)
				weeklyhigh = newohlctmp.getHighValue();
			
			if( newohlctmp.getLowValue() < weeklylow)
				weeklylow = newohlctmp.getLowValue();
		}
		
		java.sql.Date sqldate = null;
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			sqldate = new java.sql.Date(format.parse(friday.toString()).getTime());
		} catch (ParseException e) {e.printStackTrace();}
		try {
			org.jfree.data.time.Week recordwk = new org.jfree.data.time.Week (sqldate);
			nodexdata.getOHLCData().add(new OHLCItem(recordwk,weeklyopen,weeklyhigh,weeklylow,weeklyclose) );
		} catch(Exception e) {e.printStackTrace();}

		return tdxnode;
	}
	/*
	 * �����ݿ��л�ȡ���ĳʱ��ε��������ƣ��������Ǵ�CSV�ж�ȡ
	 */
	public BanKuai getBanKuaiKXianZouShi(BanKuai bk, LocalDate nodestartday, LocalDate nodeendday, String period) 
	{//��������ʼ�ǿ���Ϊ�����ڵ�K�����ݣ�
		if(period.equals(NodeGivenPeriodDataItem.WEEK)) //�������߲�ѯ����
			; 
		else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //�������߲�ѯ����
			;

		String searchtable = null;
		if(bk.getType() == BkChanYeLianTreeNode.TDXBK) 
			searchtable = this.getBanKuaiJiaoYiLiangBiaoName(bk);
		else if(bk.getType() == BkChanYeLianTreeNode.DZHBK)
			searchtable = "���ǻ۰��ÿ�ս�����Ϣ";
		
		TemporalField fieldCH = WeekFields.of(Locale.CHINA).dayOfWeek();
		NodeXPeriodData nodedayperioddata = bk.getNodeXPeroidData(period);
//		try{	nodestartday = nodestartday.with(fieldCH, 1); //ȷ��K��������ʾ������һ��
//		} catch (java.lang.NullPointerException e) {return bk;}
//		LocalDate notcalwholewkmodedate = nodedayperioddata.isInNotCalWholeWeekMode();
//		if(notcalwholewkmodedate == null)	nodeendday = nodeendday.with(fieldCH, 7);
//		else nodeendday = notcalwholewkmodedate;

		String nodecode = bk.getMyOwnCode();
		String sqlquerystat = "SELECT *  FROM " + searchtable + " \r\n" + 
				"WHERE ����='" + nodecode + "'" + "\r\n" + 
				"AND ��������  between'" + nodestartday + "' AND '" + nodeendday + "'"
				;
		
		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
		LocalDate lastdaydate = nodestartday.with(fieldCH, 6); //
//		org.ta4j.core.TimeSeries nodenewohlc = new BaseTimeSeries.SeriesBuilder().withName(bk.getMyOwnCode()).build();
		OHLCSeries nodenewohlc = new OHLCSeries (bk.getMyOwnCode());
		try {  
			 rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			 while(rsfx.next()) {
				 double open = rsfx.getDouble("���̼�");
				 double high = rsfx.getDouble("��߼�");
				 double low = rsfx.getDouble("��ͼ�");
				 double close = rsfx.getDouble("���̼�");
				 double cje = rsfx.getDouble("�ɽ���");
				 double cjl = rsfx.getDouble("�ɽ���");
				 double cjezbgr = rsfx.getDouble("DPCJEZB������");
				 double cjlzbgr = rsfx.getDouble("DPCJLZB������");
				 java.sql.Date actiondate = rsfx.getDate("��������");
				 ZonedDateTime zdtime = actiondate.toLocalDate().atStartOfDay(ZoneOffset.UTC);
				 org.jfree.data.time.Day recordday = new org.jfree.data.time.Day (actiondate);
				 
				 NodeGivenPeriodDataItem bkperiodrecord = new NodeGivenPeriodDataItemForJFC( bk.getMyOwnCode(), NodeGivenPeriodDataItem.DAY,
						 recordday, open, high,  low,  close, 
							cjl, cje );
				 bkperiodrecord.setDaPanChenJiaoErZhanBiGrowingRate(cjezbgr);
				 bkperiodrecord.setDaPanChenJiaoLiangZhanBiGrowingRate(cjlzbgr);
				 
//				 NodeGivenPeriodDataItem bkperiodrecord = new NodeGivenPeriodDataItemForTA4J( bk.getMyOwnCode(), NodeGivenPeriodDataItem.DAY,
//							zdtime, PrecisionNum.valueOf(open), PrecisionNum.valueOf(high),  PrecisionNum.valueOf(low),  PrecisionNum.valueOf(close), 
//							PrecisionNum.valueOf(cjl), PrecisionNum.valueOf(cje) );
				 
				 
				 nodedayperioddata.addNewXPeriodData(bkperiodrecord);
				 
				 //��������OHLC����
				 WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
				 int lastdayweekNumber = lastdaydate.get(weekFields.weekOfWeekBasedYear());
				 int curweekNumber = actiondate.toLocalDate().get(weekFields.weekOfWeekBasedYear());
				 if(lastdayweekNumber == curweekNumber) {
					 try{
//						 nodenewohlc.addBar( (NodeGivenPeriodDataItemForTA4J)bkperiodrecord);
						 nodenewohlc.add( (NodeGivenPeriodDataItemForJFC)bkperiodrecord);
					 } catch (java.lang.IllegalArgumentException e) {
						 e.printStackTrace();
					 }
				 } else {
					 //�����ˣ�������һ������OHLC ����
					 this.getTDXNodesWeeklyKXianZouShiForJFC(bk, lastdaydate.with(DayOfWeek.FRIDAY), nodenewohlc);
					 
					 nodenewohlc = null;
					 nodenewohlc = new OHLCSeries (bk.getMyOwnCode());
					 lastdaydate = actiondate.toLocalDate().with(DayOfWeek.FRIDAY);
					 nodenewohlc.add( (NodeGivenPeriodDataItemForJFC)bkperiodrecord);
				 }
				 
				 bkperiodrecord = null;
			 }
			 //Ŀǰ�㷨���һ��Ҫ����ѭ������ܼ���
			 this.getTDXNodesWeeklyKXianZouShiForJFC(bk, lastdaydate.with(DayOfWeek.FRIDAY), nodenewohlc);
			 
		} catch(java.lang.NullPointerException e){e.printStackTrace();
		} catch (SQLException e) {e.printStackTrace();
		} catch(Exception e){e.printStackTrace();
		} finally {
			try {if(rsfx != null) rsfx.close();} catch (SQLException e) {e.printStackTrace();}
			rsfx = null;
			searchtable = null;
			nodenewohlc = null;
		}

		return bk;
	}
	public BanKuai getShangZhengZhangDieTingInfo(BanKuai bk, LocalDate requiredstartday, LocalDate requiredendday, String period) 
	{
		String sql = "SELECT A.`EndOfWeekDate`, A.DIETING, B.ZHANGTING FROM\r\n" + 
				"\r\n" + 
				"( SELECT ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`, COUNT(*) AS DIETING , DATE(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate\r\n" + 
				"FROM ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ\r\n" + 
				"WHERE  �ǵ��� BETWEEN -20.0 AND -9.9\r\n" + 
				"AND ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` BETWEEN ' " + requiredstartday + "' AND '" + requiredendday + "'\r\n" + 
				"AND  ���� NOT LIKE  '688%'\r\n" + 
				"GROUP BY YEAR (ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`),  WEEK(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`)\r\n" + 
				") A\r\n" + 
				"\r\n" + 
				"LEFT JOIN \r\n" + 
				"( SELECT ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`, COUNT(*) AS ZHANGTING, DATE(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate\r\n" + 
				"FROM ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ\r\n" + 
				"WHERE  �ǵ��� BETWEEN 9.9 AND 20\r\n" + 
				"AND ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` BETWEEN ' " + requiredstartday + "' AND '" + requiredendday + "'\r\n" + 
				"AND  ���� NOT LIKE  '688%'\r\n" + 
				"GROUP BY YEAR (ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`),  WEEK(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`)\r\n" + 
				") B\r\n" + 
				"ON A.`EndOfWeekDate` = B.`EndOfWeekDate`\r\n" + 
				";\r\n" + 
				""
				;
		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sql);
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
			while(rsfx.next()) {
				LocalDate actiondate = rsfx.getDate("EndOfWeekDate").toLocalDate();
				Integer diefu = rsfx.getInt("DIETING");
				Integer zhangfu = rsfx.getInt("ZHANGTING");
				bkwkdate.addZhangDieTingTongJiJieGuo(actiondate.with(DayOfWeek.FRIDAY), zhangfu, diefu, false);
			}
		} catch(java.lang.NullPointerException e){ e.printStackTrace();
		} catch (SQLException e) {e.printStackTrace();
		} catch(Exception e){e.printStackTrace();
		} finally {
			try {if(rsfx != null)rsfx.close();} catch (SQLException e) {e.printStackTrace();}
			rsfx = null;
			sql = null;
		}
		
		return bk;
	}
	public BanKuai getShenZhenZhangDieTingInfo(BanKuai bk, LocalDate requiredstartday, LocalDate requiredendday, String period) 
	{
		String sql = "SELECT A.`EndOfWeekDate`, A.DIETING, B.ZHANGTING FROM\r\n" + 
				"\r\n" + 
				"( SELECT ͨ���������Ʊÿ�ս�����Ϣ.`��������`, COUNT(*) AS DIETING , DATE(ͨ���������Ʊÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���������Ʊÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate\r\n" + 
				"FROM ͨ���������Ʊÿ�ս�����Ϣ\r\n" + 
				"WHERE  �ǵ��� BETWEEN -20.0 AND -9.9\r\n" + 
				"AND ͨ���������Ʊÿ�ս�����Ϣ.`��������` BETWEEN ' " + requiredstartday + "' AND '" + requiredendday + "'\r\n" + 
				"GROUP BY YEAR (ͨ���������Ʊÿ�ս�����Ϣ.`��������`),  WEEK(ͨ���������Ʊÿ�ս�����Ϣ.`��������`)\r\n" + 
				") A\r\n" + 
				"\r\n" + 
				"LEFT JOIN \r\n" + 
				"( SELECT ͨ���������Ʊÿ�ս�����Ϣ.`��������`, COUNT(*) AS ZHANGTING, DATE(ͨ���������Ʊÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���������Ʊÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate\r\n" + 
				"FROM ͨ���������Ʊÿ�ս�����Ϣ\r\n" + 
				"WHERE  �ǵ��� BETWEEN 9.9 AND 20\r\n" + 
				"AND ͨ���������Ʊÿ�ս�����Ϣ.`��������` BETWEEN ' " + requiredstartday + "' AND '" + requiredendday + "'\r\n" + 
				"GROUP BY YEAR (ͨ���������Ʊÿ�ս�����Ϣ.`��������`),  WEEK(ͨ���������Ʊÿ�ս�����Ϣ.`��������`)\r\n" + 
				") B\r\n" + 
				"ON A.`EndOfWeekDate` = B.`EndOfWeekDate`\r\n"  
				;
		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sql);
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
			while(rsfx.next()) {
				LocalDate actiondate = rsfx.getDate("EndOfWeekDate").toLocalDate();
				Integer diefu = rsfx.getInt("DIETING");
				Integer zhangfu = rsfx.getInt("ZHANGTING");
				bkwkdate.addZhangDieTingTongJiJieGuo(actiondate.with(DayOfWeek.FRIDAY), zhangfu, diefu, false);
			}
		} catch(java.lang.NullPointerException e){ e.printStackTrace();
		} catch (SQLException e) {e.printStackTrace();
		}catch(Exception e){e.printStackTrace();
		} finally {
			try {if(rsfx != null)rsfx.close();} catch (SQLException e) {e.printStackTrace();	}
			rsfx = null;
			sql = null;
		}
		
		return bk;
	}
	/*
	 * ��ҵ��
	 */
	public BanKuai getChuangYeBanZhangDieTingInfo(BanKuai bk, LocalDate requiredstartday, LocalDate requiredendday, String period) 
	{
		 String sql = "SELECT A.`EndOfWeekDate`, A.DIETING, B.ZHANGTING FROM\r\n" + 
					"\r\n" + 
					"( SELECT ͨ���������Ʊÿ�ս�����Ϣ.`��������`, COUNT(*) AS DIETING , DATE(ͨ���������Ʊÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���������Ʊÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate\r\n" + 
					"FROM ͨ���������Ʊÿ�ս�����Ϣ\r\n" + 
					"WHERE  �ǵ��� BETWEEN -20.0 AND -9.9\r\n" + 
					"AND ͨ���������Ʊÿ�ս�����Ϣ.`��������` BETWEEN ' " + requiredstartday + "' AND '" + requiredendday + "'\r\n" +
					"AND   ���� LIKE  '300%'\r\n" +
					"GROUP BY YEAR (ͨ���������Ʊÿ�ս�����Ϣ.`��������`),  WEEK(ͨ���������Ʊÿ�ս�����Ϣ.`��������`)\r\n" + 
					") A\r\n" + 
					"\r\n" + 
					"LEFT JOIN \r\n" + 
					"( SELECT ͨ���������Ʊÿ�ս�����Ϣ.`��������`, COUNT(*) AS ZHANGTING, DATE(ͨ���������Ʊÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���������Ʊÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate\r\n" + 
					"FROM ͨ���������Ʊÿ�ս�����Ϣ\r\n" + 
					"WHERE  �ǵ��� BETWEEN 9.9 AND 20\r\n" + 
					"AND ͨ���������Ʊÿ�ս�����Ϣ.`��������` BETWEEN ' " + requiredstartday + "' AND '" + requiredendday + "'\r\n" +
					"AND   ���� LIKE  '300%'\r\n" +
					"GROUP BY YEAR (ͨ���������Ʊÿ�ս�����Ϣ.`��������`),  WEEK(ͨ���������Ʊÿ�ս�����Ϣ.`��������`)\r\n" + 
					") B\r\n" + 
					"ON A.`EndOfWeekDate` = B.`EndOfWeekDate`\r\n"  
					;
		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sql);
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
			while(rsfx.next()) {
				LocalDate actiondate = rsfx.getDate("EndOfWeekDate").toLocalDate();
				Integer diefu = rsfx.getInt("DIETING");
				Integer zhangfu = rsfx.getInt("ZHANGTING");
				bkwkdate.addZhangDieTingTongJiJieGuo(actiondate.with(DayOfWeek.FRIDAY), zhangfu, diefu, false);
			}
		} catch(java.lang.NullPointerException e){ e.printStackTrace();
		} catch (SQLException e) {e.printStackTrace();
		} catch(Exception e){e.printStackTrace();
		} finally {
			try {if(rsfx != null)rsfx.close();} catch (SQLException e) {e.printStackTrace();}
			rsfx = null;
			sql = null;
		}
		
		return bk;
	}
	/*
	 * 
	 */
	public Set<LocalDate> getNodeDataBaseStoredDataTimeSet (TDXNodes node, LocalDate start, LocalDate end)
	{
		String cjltable;
		if(node.getType() == BkChanYeLianTreeNode.TDXBK) 
			cjltable = this.getBanKuaiJiaoYiLiangBiaoName( (BanKuai)node);
		else 
			cjltable = this.getStockJiaoYiLiangBiaoName( (Stock)node);
		
		if(start == null)
			start = LocalDate.parse("1990-01-01");
		if(end == null)
			end = LocalDate.parse("9990-01-01");
		String sqlquerystat = "SELECT  ��������"
				+ " FROM " + cjltable + "  WHERE  ���� = " 
					+ "'"  + node.getMyOwnCode() + "'" 
					+ " AND  ��������  BETWEEN '" + start + "'  AND '" + end + "'"
					;
		CachedRowSetImpl	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		Set<LocalDate> nodetimeset = new HashSet();
		try { 				

		    	while(rs.next()) {
		    		java.sql.Date recordsdate = rs.getDate("��������"); 
		    		nodetimeset.add(recordsdate.toLocalDate() );
		    	}
		 } catch(java.lang.NullPointerException e) { 	e.printStackTrace();
		 } catch (SQLException e) {e.printStackTrace();
		 } catch(Exception e){	e.printStackTrace();
		 } finally {
		    	if(rs != null)	try {rs.close(); rs = null;} catch (SQLException e) {	e.printStackTrace();	}
		 }
		
		return nodetimeset;
	}
	/*
	 * 
	 */
	public Set<LocalDate> getNodeTuShareExtraDataTimeSet(Stock node,LocalDate start, LocalDate end) 
	{
		String cjltable = this.getStockJiaoYiLiangBiaoName( (Stock)node);
		String sqlquerystat = "SELECT  ��������"
				+ " FROM " + cjltable + "  WHERE  ���� = " 
					+ "'"  + node.getMyOwnCode() + "'" 
					+ " AND ������ͨ������ IS not NULL "
					+ " AND �������� BETWEEN '" +  start + "'  AND '" + end + "'" 
					;
		CachedRowSetImpl	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		Set<LocalDate> nodetimeset = new HashSet();
		try { 				

		    	while(rs.next()) {
		    		java.sql.Date recordsdate = rs.getDate("��������"); 
		    		nodetimeset.add(recordsdate.toLocalDate() );
		    	}
		 } catch(java.lang.NullPointerException e) { 	e.printStackTrace();
		 } catch (SQLException e) {e.printStackTrace();
		 } catch(Exception e){	e.printStackTrace();
		 } finally {
		    	if(rs != null)	try {rs.close(); rs = null;} catch (SQLException e) {	e.printStackTrace();	}
		 }
		
		return nodetimeset;
	}
	/*
	 * 
	 */
	public Interval getNodeDataBaseStoredDataTimeRange (TDXNodes node)
	{
		String cjltable;
		if(BkChanYeLianTreeNode.isBanKuai(node)) 
			cjltable = this.getBanKuaiJiaoYiLiangBiaoName( (BanKuai)node);
		else 
			cjltable = this.getStockJiaoYiLiangBiaoName( (Stock)node);
		
		CachedRowSetImpl  rs = null;
		LocalDate ldlastestdbrecordsdate = null;
		LocalDate loldestdbrecordsdate = null;
		try { 				
			String sqlquerystat = "SELECT  MAX(��������)	MOST_RECENT_TIME , MIN(��������)	MOST_OLDEST_TIME"
					+ " FROM " + cjltable + "  WHERE  ���� = " 
						+ "'"  + node.getMyOwnCode() + "'" 
						;
		    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	while(rs.next()) {
		    		java.sql.Date lastestdbrecordsdate = rs.getDate("MOST_RECENT_TIME"); //mOST_RECENT_TIME
		    		java.sql.Date oldestdbrecordsdate =  rs.getDate("MOST_OLDEST_TIME"); //mOST_RECENT_TIME
		    		try {
		    			ldlastestdbrecordsdate = lastestdbrecordsdate.toLocalDate();
		    			loldestdbrecordsdate = oldestdbrecordsdate.toLocalDate();
		    		} catch (java.lang.NullPointerException e) {	}
		    	}
		    } catch(java.lang.NullPointerException e) { 	e.printStackTrace();
		    } catch (SQLException e) {e.printStackTrace();
		    } catch(Exception e){	e.printStackTrace();
		    } finally {
		    	if(rs != null) try {rs.close(); rs = null;} catch (SQLException e) {	e.printStackTrace();	}
		    }
		
		DateTime requiredstartdt= new DateTime(loldestdbrecordsdate.getYear(), loldestdbrecordsdate.getMonthValue(), loldestdbrecordsdate.getDayOfMonth(), 0, 0, 0, 0);
		DateTime requiredenddt= new DateTime(ldlastestdbrecordsdate.getYear(), ldlastestdbrecordsdate.getMonthValue(), ldlastestdbrecordsdate.getDayOfMonth(), 0, 0, 0, 0);
		Interval requiredinterval = new Interval(requiredstartdt,requiredenddt);
		
		return requiredinterval;
	}
	/*
	 * 
	 */
	public Set<LocalDate> getNodeExportFileDataTimeSet (TDXNodes node)
	{
		Set<LocalDate> nodetimeset = new HashSet();
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		File nodeexportfile = this.getTDXNodesTDXSystemExportFile(node);
		BufferedReader reader;
		String line; int linecount = 0; 
		try {
			reader = new BufferedReader(new FileReader(nodeexportfile.getAbsolutePath() ));
			line = reader.readLine();
			linecount ++;
			while (line != null) {
				List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);
               	LocalDate curlinedate = null;
           		try {
	            			String beforparsedate = tmplinelist.get(0);
	            			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateRule);
	            			curlinedate =  LocalDate.parse(beforparsedate,formatter) ;
	            			nodetimeset.add(curlinedate);
           		} catch (Exception e) {	}
				
				// read next line
				line = reader.readLine();
				linecount ++;
			}
			reader.close();
		} catch (IOException e) {e.printStackTrace(); }
		
		return nodetimeset;
		
	}
	/*
	 * 
	 */
	public Interval getNodeExportFileDataTimeRange (TDXNodes node)
	{
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		File nodeexportfile = this.getTDXNodesTDXSystemExportFile(node);
		LocalDate startdate = null; LocalDate enddate = null;
		//get start date
		BufferedReader reader;
		String line; int linecount = 0; Boolean foundalinewithdate = false;
		try {
			reader = new BufferedReader(new FileReader(nodeexportfile.getAbsolutePath() ));
			line = reader.readLine();
			linecount ++;
			while (line != null) {
				List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);
				
				if(!foundalinewithdate) { //������ʼ���ڣ�һ���ڵڶ���
	                	LocalDate curlinedate = null;
	            		try {
	            			String beforparsedate = tmplinelist.get(0);
	            			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateRule);
	            			curlinedate =  LocalDate.parse(beforparsedate,formatter) ;
	            			startdate = curlinedate;
	            			foundalinewithdate = true;
	    					break;
	            		} catch (Exception e) {	foundalinewithdate = false; }
				}
				// read next line
				line = reader.readLine();
				linecount ++;
			}
			reader.close();
		} catch (IOException e) {e.printStackTrace(); }
		
		//get end date
		foundalinewithdate = false;
		RandomAccessFile rf = null;
        try {  
            rf = new RandomAccessFile(nodeexportfile, "r");  
            long len = rf.length();  
            long start = rf.getFilePointer();  
            long nextend = start + len - 1;  
            rf.seek(nextend);  
            int c = -1;  
            int lineimportcount = 0;
            while (nextend > start ) {
                c = rf.read();
            	if (c == '\n' || c == '\r') {  
                    line = rf.readLine();  
                    if (line != null) {  
                        @SuppressWarnings("deprecation")
						List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);

                        if( !foundalinewithdate) { //�п����ǰ������ݣ���0��������������¼��,�Ը�����˵����������Ҳ�У�Ҫ�ر���
                        	LocalDate curlinedate = null;
                    		try {
                    			String beforparsedate = tmplinelist.get(0);
                    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateRule);
                    			curlinedate =  LocalDate.parse(beforparsedate,formatter) ;
                    			enddate = curlinedate;
                    			foundalinewithdate = true;
                    			break;
                    		} catch (Exception e) {	foundalinewithdate = false;	}
                        }
                    } 
                    nextend--;  
                }  
                nextend--;  
                rf.seek(nextend);  
            }  
        } catch (FileNotFoundException e) {e.printStackTrace();  
        } catch (IOException e) { e.printStackTrace();  
        } finally {  
            try {      if (rf != null)  rf.close();   rf = null;} catch (IOException e) {  e.printStackTrace();  }  
        }
        
        if(startdate != null  && enddate != null) {
        	DateTime requiredstartdt= new DateTime(startdate.getYear(), startdate.getMonthValue(), startdate.getDayOfMonth(), 0, 0, 0, 0);
    		DateTime requiredenddt= new DateTime(enddate.getYear(), enddate.getMonthValue(), enddate.getDayOfMonth(), 0, 0, 0, 0);
    		Interval requiredinterval = new Interval(requiredstartdt,requiredenddt);
    		
    		return requiredinterval;
        } else
        	return null;
	}
	/*
	 * ��ͨ���ŵ��������ļ���λ��
	 */
	public File getTDXNodesTDXSystemExportFile (TDXNodes node ) 
	{
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		String bkfilename;
		if(node.getMyOwnCode().equalsIgnoreCase("000852"))  //ͨ���Ų�֪��Ϊʲô����֤1000��ÿ�ս�����������ļ���һ��
			 bkfilename = "62000852.TXT";
		else 
			bkfilename = (filenamerule.replaceAll("YY",node.getNodeJiBenMian().getSuoShuJiaoYiSuo().toUpperCase())).replaceAll("XXXXXX", node.getMyOwnCode());

		File tmpbkfile = new File(exportath + "/" + bkfilename);
		
		return tmpbkfile;
	}
	/*
	 * 
	 */
	private void getTDXGeGuShuJuJiLuMaxMinDateByJiaoYiSuo (String jiaoyisuo)
	{
		String optTable = null;
		if(jiaoyisuo.equalsIgnoreCase("SZ")  )
			optTable = "ͨ���������Ʊÿ�ս�����Ϣ";
		else if(jiaoyisuo.equalsIgnoreCase("SH") )
			optTable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
		CachedRowSetImpl  rsdm = null;
		try { 	
			String sqlquerystat = "SELECT * FROM \r\n" + 
					"(SELECT ��Ʊ����  FROM A��\r\n" + 
					"WHERE ���������� = '" + jiaoyisuo + "' ) agu\r\n" + 
					"\r\n" + 
					"LEFT JOIN \r\n" + 
					"( SELECT ����, MIN(��������) minjytime , MAX(��������) maxjytime FROM " + optTable + "\r\n" + 
					"GROUP BY ����) shjyt\r\n" + 
					"ON agu.��Ʊ���� =  shjyt.`����`\r\n"
					;
	    	rsdm = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	while(rsdm.next()) {
		    		 String ggcode = rsdm.getString("��Ʊ����"); //mOST_RECENT_TIME
		    		 Stock stock = (Stock) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(ggcode, BkChanYeLianTreeNode.TDXGG);
		    		 if(stock == null) 
		    			 continue;
		    		 try { LocalDate mintime = rsdm.getDate("minjytime").toLocalDate();
		    		 		stock.getShuJuJiLuInfo().setJyjlmindate(mintime);
		    		 } catch (java.lang.NullPointerException ex) {}
		    		 try { LocalDate maxtime = rsdm.getDate("maxjytime").toLocalDate();
		    		 		stock.getShuJuJiLuInfo().setJyjlmaxdate(maxtime);
		    		 } catch (java.lang.NullPointerException ex) {}
		    		 
		    		 
		    	}
		    } catch(java.lang.NullPointerException e) {	e.printStackTrace();
		    } catch (SQLException e) {	e.printStackTrace();
		    } catch(Exception e){	e.printStackTrace();
		    } finally {
		    	if(rsdm != null)  try {rsdm.close();rsdm = null;} catch (SQLException e) {	e.printStackTrace();}
		    }
	}
	/*
	 * 
	 */
	public File refreshTDXBanKuaiVolAmoToDbBulkImport (String jiaoyisuo, int bulkcount)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "ͬ�����ɽ�������.tmp");
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		getTDXBanKuaiShuJuJiLuMaxMinDateByJiaoYiSuo (jiaoyisuo);
		
		String cjltablename;
		if(jiaoyisuo.toLowerCase().equals("sh"))	cjltablename = "ͨ���Ű��ÿ�ս�����Ϣ";
		else	cjltablename = "ͨ���Ž�����ָ��ÿ�ս�����Ϣ";
		int nodecount = 0; List<String> nodeinsertdata = new ArrayList<>(); 
		
		 Collection<BkChanYeLianTreeNode> requiredbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK,jiaoyisuo);
		 for(BkChanYeLianTreeNode tmpnode :  requiredbk ) {
			 if( (nodecount == bulkcount  && !nodeinsertdata.isEmpty() ) || nodeinsertdata.size()> bulkcount * 4  ) {
				 setNodeDataToDataBase (cjltablename, nodeinsertdata,BkChanYeLianTreeNode.TDXBK );
				 nodecount = 0; nodeinsertdata.clear();
			 }
			 
			BanKuai bk = (BanKuai) tmpnode;
			if(!bk.getBanKuaiOperationSetting().isImportdailytradingdata())			continue;
			
			String tmpbkcode = bk.getMyOwnCode();
			File tmpbkfile = null; String bkfilename = null;
			if(bk.getMyOwnCode().equalsIgnoreCase("000852"))  //ͨ���Ų�֪��Ϊʲô����֤1000��ÿ�ս�����������ļ���һ��
				 bkfilename = "62000852.TXT";
			else 
				bkfilename = (filenamerule.replaceAll("YY",jiaoyisuo.toUpperCase())).replaceAll("XXXXXX", tmpbkcode);
			tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead())   
			    continue; 
			
			LocalDate ldlastestdbrecordsdate = bk.getShuJuJiLuInfo().getJyjlmaxdate();
			int datasizebefore = nodeinsertdata.size();
			nodeinsertdata = setVolAmoRecordsFromFileToDatabaseBulkImport(tmpnode,tmpbkfile,ldlastestdbrecordsdate,dateRule,nodeinsertdata,tmprecordfile);
			int datasizeafter = nodeinsertdata.size();
			if(datasizeafter > datasizebefore)
				nodecount ++;
		}
		 
		 setNodeDataToDataBase (cjltablename, nodeinsertdata,BkChanYeLianTreeNode.TDXBK );
		
		return tmprecordfile;
	}
	/*
	 * 
	 */
	private void setNodeDataToDataBase (String optable, List<String> inserteddata, int datatype) 
	{
		if(inserteddata.isEmpty())
			return;
		
		String insertstr = "";
		for(String nodedata : inserteddata) 
			insertstr = insertstr + nodedata;
		String sqlinsertstat = null;
		if(datatype == BkChanYeLianTreeNode.TDXBK)
			sqlinsertstat = "INSERT INTO " + optable +"(����,��������,���̼�,��߼�,��ͼ�,���̼�,�ɽ���,�ɽ���)"
									+ " values \r\n" 
									+ insertstr
									;
		else if(datatype == BkChanYeLianTreeNode.TDXGG)
			sqlinsertstat = "INSERT INTO " + optable +"(����,��������,�ɽ���,�ɽ���)"
									+ " values \r\n" 
									+ insertstr
									;
		sqlinsertstat = sqlinsertstat.trim().substring(0, sqlinsertstat.trim().length() - 1);
		try {
			int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		} catch (SQLException e) {e.printStackTrace();}
		
		return;
	}
	/*
	 * 
	 */
	public void refreshExtraStockDataFromTushare (LocalDate start, LocalDate end)
	{
		LocalDate ldlastestdbrecordsdate = null;
		   if(start == null)		   ldlastestdbrecordsdate =  LocalDate.parse("2019-12-29"); //��ǰ���ݵ����
		   else			   ldlastestdbrecordsdate = start; //���������ݵĺ�һ�쿪ʼ��������
		   
		   if(end == null)   end = LocalDate.now().plusDays(1);
		   
		while(ldlastestdbrecordsdate.isBefore( end.plusDays(1) )) {
			if( ldlastestdbrecordsdate.getDayOfWeek() == DayOfWeek.SUNDAY || ldlastestdbrecordsdate.getDayOfWeek() == DayOfWeek.SATURDAY  ) { //��ʼ������������һֱ����������ĩ���϶�����Ҫ����
				ldlastestdbrecordsdate = ldlastestdbrecordsdate.plusDays(1);
				continue;
			}
			
			String savedfilename = sysconfig.getTuShareExtraDataDownloadedFilePath () + "/" + ldlastestdbrecordsdate.toString().replaceAll("-", "") + "dailyexchangedata.csv";
			File savedfile = new File (savedfilename);
			if(!savedfile.exists()) {
				ldlastestdbrecordsdate = ldlastestdbrecordsdate.plusDays(1);
				continue;
			}
			
			try (
					FileReader filereader = new FileReader(savedfile);
					CSVReader csvReader = new CSVReader(filereader);
		    ){ 
				int titlesign = -1;//���CSV�ļ��ĵ�һ�� 
				Integer turnover_rate_f_index = null; Integer free_share_index = null; Integer stockcode_index = null; Integer pe_index = null;Integer pe_ttm_index = null; Integer pb_index = null;
				Integer turnover_rate_index = null; Integer total_mv_index = null; Integer circ_mv_index = null;
				List<String[]> records = csvReader.readAll();
				for (String[] nextRecord : records) {
		            	if(-1 == titlesign) {
		            		for(int i=0;i<nextRecord.length;i++) {
		            			if(nextRecord[i].equalsIgnoreCase("ts_code")  )
		            				stockcode_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("turnover_rate_f")  )
		            				turnover_rate_f_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("turnover_rate")  )
		            				turnover_rate_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("free_share")  )
		            				free_share_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("pe")  )
		            				pe_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("pe_ttm")  )
		            				pe_ttm_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("pb")  )
		            				pb_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("total_mv")  )
		            				total_mv_index = i;
		            			else if(nextRecord[i].equalsIgnoreCase("circ_mv")  )
		            				circ_mv_index = i;
		            		}
		            		
		            		titlesign ++;
		            	} 
		            	else { //�ڶ���
		            		String stockcode = null; 
		            		if(stockcode_index != null) 
		            			stockcode = nextRecord[stockcode_index].trim().substring(0, 6);
		            		BkChanYeLianTreeNode stock = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(stockcode, BkChanYeLianTreeNode.TDXGG );		
		            		if(stock == null)
		            			continue;

		            		String optTable = null;
		            		if(stock.getNodeJiBenMian().getSuoShuJiaoYiSuo().equalsIgnoreCase("SZ")  )
		            			optTable = "ͨ���������Ʊÿ�ս�����Ϣ";
		            		else if(stock.getNodeJiBenMian().getSuoShuJiaoYiSuo().equalsIgnoreCase("SH") )
		            			optTable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
		            		
		            		Double turnover_rate_f = null; Double turnover_rate = null; Double free_share = null;
		            		Double pe = null;Double pe_ttm = null; Double pb = null; Double total_mv = null; Double circ_mv = null;
		            		if(turnover_rate_f_index != null) {
		            			String turnover_rate_f_str = nextRecord[turnover_rate_f_index];
		            			if(!Strings.isNullOrEmpty(turnover_rate_f_str))
		            				turnover_rate_f = Double.parseDouble(turnover_rate_f_str);
		            		}
		            		if(turnover_rate_index != null) {
		            			String turnover_rate_str = nextRecord[turnover_rate_index];
		            			if(!Strings.isNullOrEmpty(turnover_rate_str))
		            				turnover_rate = Double.parseDouble(turnover_rate_str);
		            		}
		            		if(total_mv_index != null) {
		            			String total_mv_str = nextRecord[total_mv_index];
		            			if(!Strings.isNullOrEmpty(total_mv_str))
		            				total_mv = Double.parseDouble(total_mv_str) * 10000;
		            		}
		            		if(circ_mv_index != null) {
		            			String circ_mv_str = nextRecord[circ_mv_index];
		            			if(!Strings.isNullOrEmpty(circ_mv_str))
		            				circ_mv = Double.parseDouble(circ_mv_str) * 10000;
		            		}
//		            		if(free_share_index != null) {
//		            			String free_share_str = nextRecord[free_share_index];
//		            			if(!Strings.isNullOrEmpty(free_share_str))
//		            				free_share = Double.parseDouble(free_share_str) * 10000;
//		            		}
//		            		if(pe_index != null) {
//		            			String pe_str = nextRecord[pe_index];
//		            			if(!Strings.isNullOrEmpty(pe_str))
//		            				pe = Double.parseDouble(pe_str) ;
//		            		}
//		            		if(pe_ttm_index != null) {
//		            			String pe_ttm_str = nextRecord[pe_ttm_index];
//		            			if(!Strings.isNullOrEmpty(pe_ttm_str))
//		            				pe_ttm = Double.parseDouble(pe_ttm_str);
//		            		}
//		            		if(pb_index != null) {
//		            			String pb_str = nextRecord[pb_index];
//		            			if(!Strings.isNullOrEmpty(pb_str))
//		            				pb = Double.parseDouble(pb_str);
//		            		}
		            		
			                String sqlupdate = "Update " + optTable + " SET " +
			                					"  ������ͨ������=" + turnover_rate_f +
			                					"  , ������=" + turnover_rate +
			                					"  , ����ֵ=" + total_mv +
			                					"  , ��ͨ��ֵ= " + circ_mv +
			                					" WHERE �������� = '" + ldlastestdbrecordsdate + "'" + 
			                					" AND ����= '" + stockcode + "'"
			                					;
						    try {
								int result = connectdb.sqlUpdateStatExecute(sqlupdate);
							} catch (MysqlDataTruncation e) {e.printStackTrace();
							} catch (SQLException e) {e.printStackTrace();}
		            	}
		        }
			} catch (IOException e) {e.printStackTrace();}
			  
			ldlastestdbrecordsdate = ldlastestdbrecordsdate.plusDays(1); 
			savedfile = null;
		}

		return ;
	}
	/*
	 * 
	 */
	public void refreshExtraStockDataFromTushare ()
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "����TUSHARE��Ʊ����������Ϣ.tmp");
		
		LocalDate mintime = null;LocalDate maxtime = null; //use stock 60000 as the benchmark for exta data
		String sqlquerystat = "SELECT MIN(��������) minjytime , MAX(��������) maxjytime \r\n" + 
				"FROM ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ\r\n" + 
				"WHERE \r\n" + 
				"  ������ͨ������ IS NOT NULL  "
				;
		CachedRowSetImpl rsdm = connectdb.sqlQueryStatExecute(sqlquerystat);
		try { 	
	    	while(rsdm.next()) {
		    		 try {  mintime = rsdm.getDate("minjytime").toLocalDate();
		    		 } catch (java.lang.NullPointerException ex) {}
		    		 try {  maxtime = rsdm.getDate("maxjytime").toLocalDate();
		    		 } catch (java.lang.NullPointerException ex) {}
		    	}
		    } catch(java.lang.NullPointerException e) {	e.printStackTrace();
		    } catch (SQLException e) {	e.printStackTrace();
		    } catch(Exception e){	e.printStackTrace();
		    } finally {
		    	if(rsdm != null)
				try {rsdm.close();rsdm = null;
				} catch (SQLException e) {	e.printStackTrace();}
		    }
		
		LocalDate ldlastestdbrecordsdate = null;
		   if(maxtime == null)
			   ldlastestdbrecordsdate =  LocalDate.parse("2019-12-29"); //��ǰ���ݵ����
		   else
			   ldlastestdbrecordsdate = maxtime.plusDays(1); //���������ݵĺ�һ�쿪ʼ��������
		   
		   this.refreshExtraStockDataFromTushare (ldlastestdbrecordsdate, null);
		   return ;
	}
	/*
	 * 
	 */
	public File refreshTDXGeGuVolAmoToDbBulkImport(String jiaoyisuo, int bulkcount)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "ͬ�����ɳɽ�����Ϣ.tmp");
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		Collection<BkChanYeLianTreeNode> allgegucode = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXGG,jiaoyisuo);		

		getTDXGeGuShuJuJiLuMaxMinDateByJiaoYiSuo (jiaoyisuo);
		
		String optTable = null;
		if(jiaoyisuo.equalsIgnoreCase("SZ")  )
			optTable = "ͨ���������Ʊÿ�ս�����Ϣ";
		else if(jiaoyisuo.equalsIgnoreCase("SH") )
			optTable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
		int nodecount = 0; List<String> nodeinsertdata = new ArrayList<>();
		
		for(BkChanYeLianTreeNode tmpnode :allgegucode) {
			if( (nodecount ==  bulkcount && !nodeinsertdata.isEmpty() ) || nodeinsertdata.size()>  bulkcount*4) {
				setNodeDataToDataBase (optTable, nodeinsertdata, BkChanYeLianTreeNode.TDXGG  );
				nodecount = 0; nodeinsertdata.clear();
			}
			
			String tmpbkcode = tmpnode.getMyOwnCode();
			String bkfilename = null;
			if(jiaoyisuo.toLowerCase().equals("sz") )
				bkfilename = (filenamerule.replaceAll("YY","SZ")).replaceAll("XXXXXX", tmpbkcode);
			else if(jiaoyisuo.toLowerCase().equals("sh") ) 
				bkfilename = (filenamerule.replaceAll("YY","SH")).replaceAll("XXXXXX", tmpbkcode);
			File tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {  
				logger.debug("��ȡ" + bkfilename + "��������");
			    continue; 
			} 
			
			LocalDate ldlastestdbrecordsdate = ((Stock)tmpnode).getShuJuJiLuInfo().getJyjlmaxdate();
			int datasizebefore = nodeinsertdata.size();
			nodeinsertdata = setVolAmoRecordsFromFileToDatabaseBulkImport(tmpnode,tmpbkfile,ldlastestdbrecordsdate,dateRule,nodeinsertdata,tmprecordfile);
			int datasizeafter = nodeinsertdata.size();
			if(datasizeafter > datasizebefore)
				nodecount ++;
				
				//�û�ͬ������ɺͰ��ɽ�����Ҫ�Ѹ��ɵ�TXTתΪCSV FILE��������Ϊ��Ȩ���⣬��Ϊ���ݿ��ŵ��ǵ�ǰ�ĳɽ����ݣ����������Ȩ��ǰ�����е�OHLC����ı䣬
				//���°����ݿ��е����ݸ�дһ��ɱ��ܸߣ����Ը���ÿ���OHLC����ֱ�ӴӸ�Ȩ��TXTת����CSV�ж�ȡ�������Ȱ�TXT ��תΪCSV����ŵ�ָ��λ��
			if(datasizeafter > datasizebefore) {
				String lastestinsertdata = nodeinsertdata.get(nodeinsertdata.size()-1);
				if(lastestinsertdata.contains(tmpbkcode)) { //˵�������ݲ���
					String csvfilepath = sysconfig.getCsvPathOfExportedTDXVOLFiles();
					ConvertTxtToCsv cttc = new ConvertTxtToCsv ();
					cttc.convertTxtFileToCsv(exportath + "/" + bkfilename, csvfilepath + bkfilename.replace("TXT", "CSV"));
					cttc = null;
				}
			}
			
		}
		
		setNodeDataToDataBase (optTable, nodeinsertdata , BkChanYeLianTreeNode.TDXGG);
		
		return tmprecordfile;
	}
	/*
	 */
	private List<String> setVolAmoRecordsFromFileToDatabaseBulkImport (BkChanYeLianTreeNode tmpnode, File tmpbkfile, LocalDate lastestdbrecordsdate,  String datarule, List<String> nodeinsertdata, File tmprecordfile)
	{
		String tmpbkcode = tmpnode.getMyOwnCode();
		if(lastestdbrecordsdate == null) //null˵�����ݿ����滹û����ص����ݣ���ʱ������Ϊ1900����ļ����������ݶ�����
			try {
		        lastestdbrecordsdate = LocalDate.now().minus(100,ChronoUnit.YEARS);
				Files.append(tmpbkcode + "�ɽ�����¼���������м�¼��"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			} catch (Exception e1) {e1.printStackTrace();} 
		
		RandomAccessFile rf = null;
        try {  
            rf = new RandomAccessFile(tmpbkfile, "r");  
            long len = rf.length();  
            long start = rf.getFilePointer();  
            long nextend = start + len - 1;  
            String line;  
            rf.seek(nextend);  
            int c = -1;  
            boolean finalneededsavelinefound = false; //�����Ҫ��������ڵ���ʼ��
            int lineimportcount =0;
            while (nextend > start && finalneededsavelinefound == false) {
                c = rf.read();
            	if (c == '\n' || c == '\r') {  
                    line = rf.readLine();  
                    if (line != null) {  
                        @SuppressWarnings("deprecation")
						List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);

                        if( tmplinelist.size() ==7 && !tmplinelist.get(5).equals("0")) { //�п����ǰ������ݣ���0��������������¼��,�Ը�����˵����������Ҳ�У�Ҫ�ر���
                        	String open = tmplinelist.get(1).trim();
                        	String high = tmplinelist.get(2).trim();
                        	String low  = tmplinelist.get(3).trim();
                        	String close= tmplinelist.get(4).trim();
                        	String vol = tmplinelist.get(5).trim();
                        	String amo = tmplinelist.get(6).trim();
                        	
                        	if(tmpbkcode.equalsIgnoreCase("000852") && tmpnode.getType() == BkChanYeLianTreeNode.TDXBK) {//��֤1000���ļ���vol and amo ����û�о�ȷ����λ
                        		vol = String.valueOf( Double.parseDouble(vol) * 10000 );
                        		amo = String.valueOf( Double.parseDouble(amo) * 1000000 );
                        	}
                        		
                        	LocalDate curlinedate = null;
                    		try {
                    			String beforparsedate = tmplinelist.get(0);
                    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datarule);
                    			curlinedate =  LocalDate.parse(beforparsedate,formatter) ;
                    			if( curlinedate.isAfter(lastestdbrecordsdate)) { // ('John', 123, 'Lloyds Office'),
                    				String sqlinsertstat = null;
                    				if(tmpnode.getType() == BkChanYeLianTreeNode.TDXBK)
	                        			sqlinsertstat = "(" 
	                    						+ "'" + tmpbkcode.trim() + "'" + ","
	                    						+ "'" +  curlinedate + "'" + ","
	                    						+ "'" +  open + "'" + "," 
	                    						+ "'" +  high + "'" + "," 
	                    						+ "'" +  low + "'" + "," 
	                    						+ "'" +  close + "'" + "," 
	                    						+ "'" +  vol + "'" + "," 
	                    						+ "'" +  amo + "'"  
	                    						+ "), \r\n"
	                    						;
                    				else if(tmpnode.getType() == BkChanYeLianTreeNode.TDXGG)
                    					sqlinsertstat = "(" 
	                    						+ "'" + tmpbkcode.trim() + "'" + ","
	                    						+ "'" +  curlinedate + "'" + ","
	                    						+ "'" +  vol + "'" + "," 
	                    						+ "'" +  amo + "'"  
	                    						+ "), \r\n"
	                    						;
                        			nodeinsertdata.add(sqlinsertstat);
                    				lineimportcount ++;
                        		} else if(curlinedate.compareTo(lastestdbrecordsdate) == 0) {
                        			finalneededsavelinefound = true;
                        			if(lineimportcount == 0) {
                        				Files.append(tmpbkcode + "������¼������ʼʱ��Ϊ:" + line + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                            			Files.append(tmpbkcode + "������:" + lineimportcount + "����¼" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        			} else {
                        				Files.append(tmpbkcode + "�ɽ�����¼������ʼʱ��Ϊ:" + line + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                            			Files.append(tmpbkcode + "������:" + lineimportcount + "����¼" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        			}
                        		}
                    		} catch (Exception e) { logger.debug("���������ڵ�������");}
                        }
                    } 
                    nextend--;  
                }  
                nextend--;  
                rf.seek(nextend);  
                if (nextend == 0) {// ���ļ�ָ�������ļ���ʼ���������һ��   �ļ���һ�����ļ�̧ͷ������Ҫ����
                    //logger.debug(new String(rf.readLine().getBytes("ISO-8859-1"), charset ));  
                }  
            }  
        } catch (FileNotFoundException e) {  e.printStackTrace();  
        } catch (IOException e) {  e.printStackTrace();  
        } finally {  
            try {   if (rf != null)  rf.close();        rf = null;     } catch (IOException e) { e.printStackTrace(); }  
        }  
        
        return nodeinsertdata;
	}
	/*
	 * ͬ�����ɳɽ�����Ϣ������Ϊ������ SH/SZ
	 */
	public File refreshTDXGeGuVolAmoToDb(String jiaoyisuo) 
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "ͬ�����ɳɽ�����Ϣ.tmp");
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		Collection<BkChanYeLianTreeNode> allgegucode = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXGG,jiaoyisuo);		

		getTDXGeGuShuJuJiLuMaxMinDateByJiaoYiSuo (jiaoyisuo);
		
		String optTable = null;
		if(jiaoyisuo.equalsIgnoreCase("SZ")  )
			optTable = "ͨ���������Ʊÿ�ս�����Ϣ";
		else if(jiaoyisuo.equalsIgnoreCase("SH") )
			optTable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
		
		for(BkChanYeLianTreeNode tmpnode :allgegucode) {
			String tmpbkcode = tmpnode.getMyOwnCode();
			String bkfilename = null;
			if(jiaoyisuo.toLowerCase().equals("sz") )
				bkfilename = (filenamerule.replaceAll("YY","SZ")).replaceAll("XXXXXX", tmpbkcode);
			else if(jiaoyisuo.toLowerCase().equals("sh") ) 
				bkfilename = (filenamerule.replaceAll("YY","SH")).replaceAll("XXXXXX", tmpbkcode);
			File tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {  
				logger.debug("��ȡ" + bkfilename + "��������");
			    continue; 
			} 

			LocalDate ldlastestdbrecordsdate = ((Stock)tmpnode).getShuJuJiLuInfo().getJyjlmaxdate();
			Boolean newdataimported = setVolAmoRecordsFromFileToDatabase(tmpnode,tmpbkfile,ldlastestdbrecordsdate,optTable,dateRule,tmprecordfile);
				
				//�û�ͬ������ɺͰ��ɽ�����Ҫ�Ѹ��ɵ�TXTתΪCSV FILE��������Ϊ��Ȩ���⣬��Ϊ���ݿ��ŵ��ǵ�ǰ�ĳɽ����ݣ����������Ȩ��ǰ�����е�OHLC����ı䣬
				//���°����ݿ��е����ݸ�дһ��ɱ��ܸߣ����Ը���ÿ���OHLC����ֱ�ӴӸ�Ȩ��TXTת����CSV�ж�ȡ�������Ȱ�TXT ��תΪCSV����ŵ�ָ��λ��
				if(newdataimported != null && newdataimported) {
					String csvfilepath = sysconfig.getCsvPathOfExportedTDXVOLFiles();
					ConvertTxtToCsv cttc = new ConvertTxtToCsv ();
					cttc.convertTxtFileToCsv(exportath + "/" + bkfilename, csvfilepath + bkfilename.replace("TXT", "CSV"));
					cttc = null;
				}
		}
		
		return tmprecordfile;
	}
	/*
	 * 
	 */
	public void setBanKuaiLongTou (BanKuai bankuai, String stockcode,Boolean longtouornot)
	{
		String bkcode = bankuai.getMyOwnCode();
		String bktypetable = bankuai.getShuJuJiLuInfo().getGuPiaoBanKuaiDuiYingBiao();
		if(bktypetable == null) {
			this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SH");
		    this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("SZ");
//		    this.getTDXBanKuaiGeGuDuiYingBiaoByJiaoYiSuo ("BK");
		}
		String sqlupdatestat = "UPDATE " + bktypetable  + " SET �����ͷ = " +  longtouornot  + 
				" WHERE ������ = '" + bkcode + 
				"' AND ��Ʊ���� = '" + stockcode + "'" +
				"  AND  ISNULL(�Ƴ�ʱ��)"
				;
		try {
			connectdb.sqlUpdateStatExecute(sqlupdatestat);
		} catch (MysqlDataTruncation e) {e.printStackTrace();
		} catch (SQLException e) {e.printStackTrace();
		}
	}
	/*
	 * �޸İ����ĳ�����ɵ�Ȩ��
	 */
	public void setStockWeightInBanKuai(BanKuai bankuai, String stockcode, int weight) 
	{
		String bkcode = bankuai.getMyOwnCode();
		String bktypetable = ((BanKuai)bankuai).getShuJuJiLuInfo().getGuPiaoBanKuaiDuiYingBiao();
		
		String sqlupdatestat = "UPDATE " + bktypetable  + " SET ��ƱȨ�� = " +  weight  + 
				" WHERE ������ = '" + bkcode + 
				"' AND ��Ʊ���� = '" + stockcode + "'" +
				"  AND  ISNULL(�Ƴ�ʱ��)"
				;
		try {	connectdb.sqlUpdateStatExecute(sqlupdatestat);
		} catch (MysqlDataTruncation e) {			e.printStackTrace();
		} catch (SQLException e) {	System.out.println(sqlupdatestat + "\r\n");	e.printStackTrace();	}
	}
	/*
	 * ͨ���ŵ���ҵ����ɶ�Ӧ�ļ�
	 * ����ļ���
		incon.dat                        ֤�����ҵ��ͨ��������ҵ��������ҵ��������Ϣ
		T0002/hq_cache/block.dat         һ����
		T0002/hq_cache/block_gn.dat      ������
		T0002/hq_cache/block_fg.dat       �����
		T0002/hq_cache/block_zs.dat       ָ�����:
		T0002/hq_cache/tdxhy.cfg             ÿ����Ʊ��Ӧͨ������ҵ��������ҵ
		T0002/hq_cache/tdxzs.cfg             ���ָ�������ְ������һ���ֶ�ӳ�䵽incon.dat��TDXNHY��SWHY
		T0002/blocknew/blocknew.cfg        �Զ������Ҫ�����ļ�
		
		��ҵ�����������֤�����ҵ��������ҵ��ͨ��������ҵ
		��ҵ���ļ���incon.dat���ж��塣�ļ���ʽ��
		1) �ļ����������ҵ���ࣺ
			a) ֤�����ҵ����ͷ#ZJHHY������######
			b) ������ҵ����ͷ#SWHY������######
			c) ͨ��������ҵ����ͷ#TDXNHY������######
		2) ÿ�������У�ÿһ�а���һ��ϸ����ҵ�Ĵ�������ƣ��ԡ�|���ָ�
			a) ֤�����ҵ��һ������ΪA~M����������A99����������ΪA9999
			b) ������ҵ��һ������Ϊ990000����������Ϊ999900����������Ϊ999999
			c) ͨ��������ҵ��һ������ΪT99����������ΪT9999����������ΪT999999
			
			����Ŀ¼��T0002blocknew
			�����ļ���blocknew.cfg ��¼�Զ���İ�����ƺ��ļ�ͷ
			�����ļ��洢��ʽ��
			1) ÿ�����120�ֽ�
			2) �������50�ֽ�
			3) ����ļ���ͷ70�ֽ�
			// �Զ���������ļ���ʽ��T0002blocknewblocknew.cfg
			struct TTDXUserBlockRecord
			{
			char szName[50];
			char szAbbr[70]; // Ҳ���ļ�����ǰ׺ T0002blocknewxxxx.blk
			};
			
			����б��ļ�: *.blk
			1) ÿ��һ����¼��ÿ����¼7�����֣�
			a) �г�����1λ��0 �C ���У�1 �C ����
			b) ��Ʊ����6λ
			c) �н�������rn
			T0002blocknewZXG.blk
			�г� ��Ʊ����
			1999999
			0399001
			0399005
			0399006
			1000016
			1000300
			0399330
	 */
	/*
	 * 
	 */
	public File refreshTDXZDYBanKuai (Map<String, String> neededimportedzdybkmap)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "ͬ��ͨ�����Զ����鱨��.txt");
		this.refreshTDXZiDingYiBanKuaiToGeGu(neededimportedzdybkmap, tmprecordfile);
		
		return tmprecordfile;
	}

	/*
	 * �����Զ�����͸��ɵĶ�Ӧ��ϵ
	 */
	private File refreshTDXZiDingYiBanKuaiToGeGu (Map<String, String> neededimportedzdybkmap, File tmprecordfile) 
	{
		Set<String> neededimportzdybknames = neededimportedzdybkmap.keySet(); 
		//Set<String> curzdybknames = this.getZdyBkSet ();
		
		for(String newzdybk:neededimportzdybknames) {
			String filename = neededimportedzdybkmap.get(newzdybk); //str���Զ����������
			File zdybk = new File(filename);
			List<String> readLines = null;
			try {	readLines = Files.readLines(zdybk,Charsets.UTF_8,new ParseBanKuaiWeeklyFielGetStocksProcessor ());
			} catch (java.io.FileNotFoundException e) {
				logger.info("�Զ������ļ�û���ҵ��������Զ�����ʧ�ܣ�");
				return null;
			} catch (IOException e) {	e.printStackTrace();	return null;}
			Set<String> stocknamesnew = new HashSet<String>(readLines);
			
			//�ҳ����ݿ������еĸð�����
			Set<String> tmpstockcodesetold = new HashSet<String>();
			CachedRowSetImpl rs = null;
			try {
			    	String sqlquerystat = "SELECT *  FROM ��Ʊͨ�����Զ������Ӧ��  WHERE  �Զ����� = " 
			    							+ "'"  + newzdybk.trim() + "'"
			    							+ "AND �Ƴ�ʱ�� IS  NULL"
			    							;
			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	while(rs.next()) {  
			        	String stockcode = rs.getString("��Ʊ����");
			        	tmpstockcodesetold.add(stockcode);
			        }
			       
			    }catch(java.lang.NullPointerException e){    	e.printStackTrace();
			    } catch (SQLException e) { 	e.printStackTrace();
			    }catch(Exception e){  	e.printStackTrace();
			    } finally {
			    	if(rs != null)
						try {	rs.close();	rs = null;
						} catch (SQLException e) {	e.printStackTrace();	}
			    }
			
			 //��tmpstockcodesetnew�����еģ�tmpstockcodesetoldû�е�ѡ���������µģ�Ҫ�������ݿ�
		    SetView<String> differencebankuainew = Sets.difference(stocknamesnew, tmpstockcodesetold ); 
   		    for (String newstock : differencebankuainew) {
   				
   				String sqlinsertstat = "INSERT INTO  ��Ʊͨ�����Զ������Ӧ��(��Ʊ����,�Զ�����) values ("
   						+ "'" + newstock.trim() + "'" + ","
   						+ "'" + newzdybk.trim() + "'" 
   						+ ")"
   						;
   				//logger.debug(sqlinsertstat);
   				try {	int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();	}
   				
   			}
   		    differencebankuainew = null;
   		 
   	        //�� tmpstockcodesetold �����еģ�tmpstockcodesetnewû�е�ѡ�������Ǿɵģ�Ҫ�����ݿ�������Ƴ�ʱ��
   		    
   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,stocknamesnew  );
   	        for (String oldstock : differencebankuaiold) {
   	        	String sqlupdatestat = "UPDATE ��Ʊͨ�����Զ������Ӧ��  "
						+ "  SET �Ƴ�ʱ�� = " + "'" +  LocalDate.now().toString()  + "'"
						+ "  WHERE  ��Ʊͨ�����Զ������Ӧ�� .`��Ʊ����` = " + "'" + oldstock.trim().trim() + "'"
						+ "  AND �Զ����� = " +  "'" + newzdybk.trim() + "'"
						+ "  AND isnull(�Ƴ�ʱ��)"
						;
   	        	//logger.debug(sqldeletetstat);
   	    		try {	int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqlupdatestat);
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();	}
   	        }
   	        
   	     differencebankuaiold = null;
   	     stocknamesnew = null;
		}
	
		neededimportzdybknames = null;
		return tmprecordfile;
	}
	
	/*
	 * ��ȡͨ����ϵͳ������Զ������б����������ݿ��Զ�����
	 */
	public HashMap<String, String> getTDXZiDingYiBanKuaiList ()
	{
		File file = new File(sysconfig.getTDXSysZDYBanKuaiFile () );
		logger.debug(sysconfig.getTDXSysZDYBanKuaiFile ());
		 if(!file.exists() ) {
			 logger.debug("Zdy BanKuai File not exist");
			 return null;
		 }
		 
		 int addednumber =0;
		 HashMap<String,String> zdybkmap = Maps.newHashMap();
		 FileInputStream in = null ;  
		 BufferedInputStream dis = null;
		 try {
			   in = new FileInputStream(file);  
			   dis = new BufferedInputStream(in);
              
              int eachbankuaibytenumber = 120;
              byte[] itemBuf2 = new byte[eachbankuaibytenumber];
              int i=0;
              
              while (dis.read(itemBuf2, 0, eachbankuaibytenumber) != -1) {
           	   //�������
           	   String zdybankuainame = (new String(itemBuf2,0,48)).trim();
           	   logger.debug(zdybankuainame);
           	   
           	   //����Ӧfile
               String bkfilename = sysconfig.toUNIXpath(file.getParent()) + "/" + new String(itemBuf2,49,eachbankuaibytenumber-51).trim() +".blk";
               logger.debug(bkfilename);
               
               zdybkmap.put(zdybankuainame, bkfilename );
               
              }
		 } catch (Exception e) { e.printStackTrace();	 return null;
		 } finally {
			 if(in != null)
				try {	in.close();	} catch (IOException e) {	e.printStackTrace();	}
			 if(dis != null)
				try {	dis.close();} catch (IOException e) {e.printStackTrace();		}
		 }
		
		 this.refreshTDXZiDingYiBanKuai (zdybkmap);
		 return zdybkmap;
	}
	/*
	 * �ҳ����ݿ����Ѿ�����ͨ�����Զ�����
	 */
	private HashMap<String, BanKuai> initializeSystemAllZdyBanKuaiList()  
	{
		//HashMap<String,BanKuai> sysbankuailiebiaoinfo;
//		private  HashMap<String,BanKuai> zdybankuailiebiaoinfo;
		HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();

		String sqlquerystat = "SELECT *  FROM ͨ�����Զ������б�"
							   ;   
		logger.debug(sqlquerystat);
		CachedRowSetImpl  rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	    	
//	        rs.last();  
//	        int rows = rs.getRow();  
//	        data = new Object[rows][];    
//	        int columnCount = 3;//����  
//	        rs.first();  
	        //int k = 0;  
	        while(rs.next()) {  
	        	BanKuai tmpbk = new BanKuai (rs.getString("ID"),rs.getString("�������") );
	        	tmpsysbankuailiebiaoinfo.put(rs.getString("�������"), tmpbk);
	        }
	    }catch(java.lang.NullPointerException e){e.printStackTrace();
	    } catch (SQLException e) {   	e.printStackTrace();
	    }catch(Exception e){  	e.printStackTrace();
	    } finally {
	    	try {	rs.close ();	rs = null;	} catch (SQLException e) {	e.printStackTrace();	}
	    }
	    
	    return  tmpsysbankuailiebiaoinfo;
   
	}
	/*
	 * ����������Ϣ��ͬ�� �������ݿ��е�ͨ���ŵ��Զ�����
	 */
	private int refreshTDXZiDingYiBanKuai (HashMap<String, String> neededimportedzdybkmap)
	{
		Set<String> neededimportzdybknames = neededimportedzdybkmap.keySet(); 
		Set<String> curzdybknames = this.initializeSystemAllZdyBanKuaiList().keySet();
		
		 //���µ��Զ�����������ݿ�
	      SetView<String> differencebankuainew = Sets.difference(neededimportzdybknames, curzdybknames ); 
		    for (String str : differencebankuainew) {
				String sqlinsertstat = "INSERT INTO  ͨ�����Զ������б�(�������) values ("
						+ "'" + str.trim() + "'" 
						  
						+ ")"
						;
				//logger.debug(sqlinsertstat);
				try {	int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();	}

			}
		    differencebankuainew = null;
		    
	        //�Ѿɵ��Զ�����ɾ�������ݿ�
	        SetView<String> differencebankuaiold = Sets.difference(curzdybknames,neededimportzdybknames  );
	        for (String str : differencebankuaiold) {
	        	String sqldeletetstat = "DELETE  FROM ͨ�����Զ������б� "
	        							+ " WHERE �������=" + "'" + str.trim() + "'" 
	        							;
	        	//logger.debug(sqldeletetstat);
	    		int autoIncKeyFromApi;
				try {
					autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
				} catch (MysqlDataTruncation e1) {e1.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();}

	    		
	    		sqldeletetstat = "DELETE  FROM ��Ʊͨ�����Զ������Ӧ�� "
						+ " WHERE �Զ�����=" + "'" + str.trim() + "'" 
						;
				//logger.debug(sqldeletetstat);
				try {
					autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();}
	        }
	        
	        differencebankuaiold = null;
	        neededimportzdybknames = null;
	        curzdybknames = null;

	        return 1;
	}
		/*
		 * ��ȡ������ɵ�����������ע����ɵ���Ϣ
		 */
		public BkChanYeLianTreeNode getNodeGzMrMcYkInfo (TDXNodes node,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
		{
			if(node == null)	return null;
			if(node.getNodeJiBenMian().getZdgzmrmcykRecords() != null &&  !node.getNodeJiBenMian().getZdgzmrmcykRecords().isEmpty())
	        	return node;
//			selecteddatestart = selecteddatestart.with(DayOfWeek.MONDAY);
//			selecteddateend = selecteddateend.with(DayOfWeek.FRIDAY);
			
			if(period.equals(NodeGivenPeriodDataItem.DAY)) //�������߲�ѯ����
				; 
			else if(period.equals(NodeGivenPeriodDataItem.MONTH)) //�������߲�ѯ����
				;
			
			NodeXPeriodData nodewkperioddata = node.getNodeXPeroidData(period);
			String sqlquerystat;
			if(node.getType() == BkChanYeLianTreeNode.TDXGG || node.getType() == BkChanYeLianTreeNode.BKGEGU) 
				sqlquerystat = getZdgzMrmcYingKuiSQLForStock (node);
			else //���͸����ǲ�һ���ģ����û������������Ϣ
				sqlquerystat = getZdgzMrmcYingKuiSQLForBanKuai (node);
			
			 CachedRowSetImpl rs = null;
			 try {
				 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
				 List<Object[]> data = new ArrayList<>();
				 int columnCount = 6;//����
			     while(rs.next()) {   //{ "����", "����", "˵��","ID","�����˻�","��Ϣ��" };
			            Object[] row = new Object[columnCount];  
			            row[0] = (java.sql.Date) rs.getDate("����");
			            row[1] = rs.getString(2);
			            row[2] = rs.getString(3);
			            row[3] = rs.getString(4);
			            row[4] = rs.getString(5);
			            row[5] = rs.getString(6);
			            String action = (String) row[1];
						if(action.equals("����")) {
							org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
							((StockNodesXPeriodData)nodewkperioddata).addMaiRuJiLu(wknum, 1);
						} else
						if(action.equals("����")) {
							org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
							((StockNodesXPeriodData)nodewkperioddata).addMaiChuJiLu(wknum, 0);
						} else
						if(action.equals("�����ע")) {
							org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
							((StockNodesXPeriodData)nodewkperioddata).addGzjlToPeriod(wknum, 1);
						} else 
						if(action.equals("�Ƴ���ע")) {
							org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
							((StockNodesXPeriodData)nodewkperioddata).addGzjlToPeriod(wknum, 0);
						}
						
			            data.add(row);
			        }   
			        if(!data.isEmpty())  	node.getNodeJiBenMian().setZdgzmrmcykRecords( data );
				} catch (SQLException e) {e.printStackTrace();
				} finally {try {rs.close();} catch (SQLException e) {e.printStackTrace();}
					rs = null;
				}
			 
			 return node;
		}
		/*
		 * ��ȡ���������Ϣ
		 */
		private String getZdgzMrmcYingKuiSQLForBanKuai(BkChanYeLianTreeNode node) 
		{
			String stockcode = node.getMyOwnCode();
			
			String serachnodecode ; //�������ºʹ��̽�����ص�ָ���⣬�������Ӧ��ֻ�Һ��Լ���صļ�¼��
			Set<String> zhishucodes = new HashSet<String> ();
			zhishucodes.add("000000");
			zhishucodes.add("999999");
			zhishucodes.add("399001");
			zhishucodes.add("399006");
			zhishucodes.add("399005");
			zhishucodes.add("000852");
			zhishucodes.add("000300");
			zhishucodes.add("000016");
			zhishucodes.add("399673");
			if(zhishucodes.contains(stockcode) ) 
				serachnodecode = "000000";
			else
				serachnodecode = stockcode;
			
			String sqlquerystat = "SELECT zdgz.����,"
								+ "zdgz.�����Ƴ���־,"
								+ "zdgz.ԭ������,"
								+ "zdgz.ID,"
								+ "null,"
								+ "'������¼�ص��ע'"
								+ " FROM ������¼�ص��ע  zdgz"
								+ " WHERE (zdgz.��Ʊ���� =" + "'" + stockcode + "'"
								+ " OR zdgz.��Ʊ���� ='" + serachnodecode + "')"
								+ " ORDER BY 1 DESC,5,3 " //desc
								;
	     	     
	     return sqlquerystat;
		}
		/*
		 * ��ȡ���ɵ������Ϣ
		 */
		private String getZdgzMrmcYingKuiSQLForStock(BkChanYeLianTreeNode node) 
		{
			String stockcode = node.getMyOwnCode();
			String sqlquerystat1= "SELECT ggyk.����, "
								+ "IF(ggyk.ӯ�����>0,'ӯ��','����') AS ӯ�����,"
								+ " ggyk.ԭ������,"
								+ " ggyk.ID,"
								+ " ggyk.�����˺�,"
								+  "'����ӯ��'" 
								+ " FROM  A�ɸ���ӯ�� ggyk "
								+ " WHERE ggyk.��Ʊ���� =" + "'" + stockcode + "'" 
								
								+ " \n UNION ALL \n" 
								;
			//logger.debug(sqlquerystat1);
	       String sqlquerystat2="SELECT czjl.����, " +		""
								+ "IF( czjl.�������=0,'��ת��',IF(czjl.�ҵ� = true,IF(czjl.����������־, '�ҵ�����', '�ҵ�����'),IF(czjl.����������־,'����','����') )  ) AS ����,"
								+ " czjl.ԭ������,"
								+ " czjl.ID,"
								+ " czjl.�����˺�,"
								+ "'������¼����'" 
								+ " FROM ������¼���� czjl "
								+ "	WHERE czjl.��Ʊ���� =" + "'" + stockcode + "'"
								
								+ " \n UNION ALL \n" 
								;
	       //logger.debug(sqlquerystat2);
	      String sqlquerystat3="SELECT rqczjl.����,"
								+ "IF( rqczjl.�������=0,'��ת��',IF(rqczjl.�ҵ� = true,IF(rqczjl.����������־, '�ҵ�����', '�ҵ�����'),IF(rqczjl.����������־,'����','����') )  ) AS ����,"
								+ "rqczjl.ԭ������,"
								+ "rqczjl.ID,"
								+ "rqczjl.�����˺�,"
								+ "'������¼��ȯ����'"
								+ " FROM ������¼��ȯ���� rqczjl"
								+ "	WHERE rqczjl.��Ʊ���� =" + "'" + stockcode + "'"
								
								+ " \n UNION ALL \n"
								;
	      //logger.debug(sqlquerystat3);
		 String sqlquerystat4="SELECT rzczjl.����,"
								+ "IF( rzczjl.�������=0,'��ת��',IF(rzczjl.�ҵ� = true,IF(rzczjl.����������־, '�ҵ�����', '�ҵ�����'),IF(rzczjl.����������־,'����','����') )  ) AS ����,"
								+ "rzczjl.ԭ������,"
								+ "rzczjl.ID,"
								+ "rzczjl.�����˺�,"
								+ "'������¼��������'"
								+ " FROM ������¼�������� rzczjl"
								+ " WHERE rzczjl.��Ʊ���� =" + "'" + stockcode + "'"
								
								+ " \n UNION ALL \n"
								;
		 //logger.debug(sqlquerystat4);
	     String sqlquerystat5="SELECT zdgz.����,"
								+ "zdgz.�����Ƴ���־,"
								+ "zdgz.ԭ������,"
								+ "zdgz.ID,"
								+ "null,"
								+ "'������¼�ص��ע'"
								+ " FROM ������¼�ص��ע  zdgz"
								+ " WHERE zdgz.��Ʊ���� =" + "'" + stockcode + "'"
								

								+ " \n UNION ALL \n"
								;
	     //logger.debug(sqlquerystat5);
	     String guanzhubk = sysconfig.getCurZdyBanKuaiOfGuanZhuGeGu();
	     String sqlquerystat6="SELECT zdgz.����ʱ��,"
					+ "'�����ע',"
					+ "null, "
					+ "null,"
					+ "null,"
					+ "'������¼�ص��ע'"
					+ " FROM ��Ʊͨ�����Զ������Ӧ��  zdgz" 
					+ " WHERE zdgz.��Ʊ���� ="  + "'" + stockcode + "'"
					+ " AND �Զ�����="  + "'" + guanzhubk + "'" 
 					

					+ " \n UNION ALL \n"
					;
	     String sqlquerystat7="SELECT zdgz.�Ƴ�ʱ��,"
					+ "'�Ƴ���ע',"
					+ "null, "
					+ "null,"
					+ "null,"
					+ "'������¼�ص��ע'"
					+ " FROM ��Ʊͨ�����Զ������Ӧ��  zdgz" 
					+ " WHERE zdgz.��Ʊ���� ="  + "'" + stockcode + "'"
					+ " AND �Զ�����="  + "'" + guanzhubk + "'" 
					+ " AND zdgz.�Ƴ�ʱ�� IS NOT NULL \n"
					+ " ORDER BY 1 DESC,5,3 " //desc
					;
		
	     String sqlquerystat = sqlquerystat1 + sqlquerystat2 + sqlquerystat3 + sqlquerystat4 + sqlquerystat5 + sqlquerystat6 +sqlquerystat7;
	     return sqlquerystat;
		}
		/*
		 * 
		 */
		public String getMingRiJiHua() 
		{
			String sqlquerystat = 	" SELECT * FROM ������¼�ص��ע   \r\n" + 
									" WHERE ���� >= date_sub(curdate(),interval 30 day) \r\n" + 
									"		and ���� <= curdate()\r\n" + 
									" AND �����Ƴ���־ = '���ռƻ�' " 
									;
			CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			String pmdresult = "";
			try  {     
		        rs.last();  
		        int rows = rs.getRow();  
		        rs.first();  
		        int k = 0;  
		        //while(rs.next())
		        for(int j=0;j<rows;j++) { 
		        	pmdresult = pmdresult + "--*(" + rs.getDate("����") + ")(" + rs.getString("��Ʊ����") + ")  " + rs.getString("ԭ������") + " ";
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
		public int setMingRiJiHua(BkChanYeLianTreeNode node, String msg)
		{
			String sqlinsertstat = "INSERT INTO  ������¼�ص��ע(��Ʊ����,����,�����Ƴ���־,ԭ������) values ("
						+ " '" + node.getMyOwnCode() + "'" + ","
						+ " '" + LocalDate.now().toString() + "'" + ","
						+ " '���ռƻ�'" + ","
						+ " '" + msg + "'"
						+ ")"
						;
    	   logger.debug(sqlinsertstat);
    	   int autoIncKeyFromApi = 0;
			try {
				autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
			} catch (MysqlDataTruncation e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
				
			return autoIncKeyFromApi;
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
		/*
		 * 
		 */
		public Boolean updateStockNewInfoToDb(BkChanYeLianTreeNode nodeshouldbedisplayed) 
		{
			String dategainiants = null;
			
			String txtareainputgainiants = null;
			String datequanshangpj = null;
			String txtfldinputquanshangpj;
			String datefumianxx = null;
			String txtfldinputfumianxx = null;
			
			String stockname = "'" +  nodeshouldbedisplayed.getMyOwnName().trim() + "'" ;
			String stockcode = "'" +  nodeshouldbedisplayed.getMyOwnCode().trim() + "'" ;
			
			try {
					txtareainputgainiants = "'" + nodeshouldbedisplayed.getNodeJiBenMian().getGainiantishi().trim() + "'";
			} catch (java.lang.NullPointerException  e) {
			}

			txtfldinputquanshangpj = "'" + nodeshouldbedisplayed.getNodeJiBenMian().getQuanshangpingji().trim() + "'";
		    
			txtfldinputfumianxx = "'" + nodeshouldbedisplayed.getNodeJiBenMian().getFumianxiaoxi() + "'";
		    
//		    String txtfldinputzhengxiangguan = "'" + nodeshouldbedisplayed.getNodeJiBenMian().getZhengxiangguan().trim() + "'";
//		    String txtfldinputfuxiangguan = "'" + nodeshouldbedisplayed.getNodeJiBenMian().getFuxiangguan().trim() + "'";
		    
//		    String keHuCustom = nodeshouldbedisplayed.getNodeJiBenMian().getKeHuCustom();
//		    String jingZhengDuiShou = nodeshouldbedisplayed.getNodeJiBenMian().getJingZhengDuiShou();
	    
			 if(nodeshouldbedisplayed.getType() == 6) {//�ǹ�Ʊ{
				 HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
				 String sqlupdatestat= "UPDATE A��  SET "
							+ " ��Ʊ����=" + stockname +","
							+ " ����ʱ��=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getGainiantishidate()) ) +","
							+ " ����������=" + txtareainputgainiants +","
							+ " ȯ������ʱ��=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getQuanshangpingjidate() ) ) +","
							+ " ȯ����������=" + txtfldinputquanshangpj +","
							+ " ������Ϣʱ��=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getFumianxiaoxidate()  ) ) +","
							+ " ������Ϣ=" + txtfldinputfumianxx
//							+","
//							+ " ����ؼ��ͻ�=" + txtfldinputzhengxiangguan +","
//							+ " ����ؼ���������=" + txtfldinputfuxiangguan +","
//							+ " �ͻ�=" + "'" + keHuCustom +"'" + ","
//							+ " ��������=" + "'" + jingZhengDuiShou + "'"  
							+ " WHERE ��Ʊ����=" + stockcode
							;
				 sqlstatmap.put("mysql", sqlupdatestat);
				 //logger.debug(sqlupdatestat);
				 
				 try {
					if( connectdb.sqlUpdateStatExecute(sqlupdatestat) !=0)
						 return true;
					 else return false;
				} catch (MysqlDataTruncation e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 } else { //�ǰ��
				 HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
				 String actiontable =  "ͨ���Ű���б�";
				 
				 String sqlinsertstat = "UPDATE " + actiontable + " SET "
						 	+ " �������=" + stockname +","
							+ " ����ʱ��=" + formateDateForDiffDatabase("mysql",CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getGainiantishidate()) ) +","
							+ " ����������=" + txtareainputgainiants +","
							+ " ȯ������ʱ��=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getQuanshangpingjidate() ) ) +","
							+ " ȯ����������=" + txtfldinputquanshangpj +","
							+ " ������Ϣʱ��=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getFumianxiaoxidate()  ) ) +","
							+ " ������Ϣ=" + txtfldinputfumianxx 
//							+","
//							+ " ����ؼ��ͻ�=" + txtfldinputzhengxiangguan +","
//							+ " ����ؼ���������=" + txtfldinputfuxiangguan +","
//							+ " �ͻ�=" + "'" + keHuCustom +"'" + ","
//							+ " ��������=" + "'" + jingZhengDuiShou + "'"  
							+ " WHERE ���ID=" + stockcode
							;
				 //logger.debug(sqlinsertstat); 
				 sqlstatmap.put("mysql", sqlinsertstat);
				 try {
					if( connectdb.sqlUpdateStatExecute(sqlinsertstat) !=0 ) {
						 return true;
					 } else return false;
				} catch (MysqlDataTruncation e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 
			 return null;
		}
		/*
		 * 
		 */
		public BanKuai getBanKuaiGeGuGzMrMcYkInfo (BanKuai bk)
		{
			List<BkChanYeLianTreeNode> bkshgegu = bk.getAllGeGuOfBanKuaiInHistory();
			if(bkshgegu== null || bkshgegu.isEmpty())
				return bk;
			
			String bkggstr = ""; //int ggcount = 0;
			for(BkChanYeLianTreeNode stockofbk : bkshgegu) {
				StockOfBanKuai stkbk = (StockOfBanKuai)stockofbk;
				if(stkbk.getStock().getNodeJiBenMian().getZdgzmrmcykRecords()!= null &&  !stkbk.getStock().getNodeJiBenMian().getZdgzmrmcykRecords().isEmpty())
		        	continue;
				else {
					bkggstr  = bkggstr + "'" + stockofbk.getMyOwnCode() +  "', ";
//					ggcount ++;
				}
			}
			bkggstr = bkggstr.substring(0, bkggstr.length() -2);
			
			String sqlquerystat = "\r\n" + 
					"SELECT ggyk.��Ʊ����, ggyk.����, IF(ggyk.ӯ�����>0,'ӯ��','����') AS ӯ�����, ggyk.ԭ������, ggyk.ID, ggyk.�����˺�,'����ӯ��' FROM  A�ɸ���ӯ�� ggyk \r\n" + 
					" WHERE ggyk.��Ʊ���� IN ( " + bkggstr + ") \r\n" + 
					" UNION ALL \r\n" + 
					"SELECT czjl.��Ʊ����, czjl.����, IF( czjl.�������=0,'��ת��',IF(czjl.�ҵ� = true,IF(czjl.����������־, '�ҵ�����', '�ҵ�����'),IF(czjl.����������־,'����','����') )  ) AS ����, czjl.ԭ������, czjl.ID, czjl.�����˺�,'������¼����' FROM ������¼���� czjl \r\n" + 
					"	WHERE czjl.��Ʊ���� IN (" + bkggstr + ") \r\n" + 
					" UNION ALL \r\n" + 
					"SELECT rqczjl.��Ʊ����, rqczjl.����,IF( rqczjl.�������=0,'��ת��',IF(rqczjl.�ҵ� = true,IF(rqczjl.����������־, '�ҵ�����', '�ҵ�����'),IF(rqczjl.����������־,'����','����') )  ) AS ����,rqczjl.ԭ������,rqczjl.ID,rqczjl.�����˺�,'������¼��ȯ����' FROM ������¼��ȯ���� rqczjl	\r\n" + 
					"WHERE rqczjl.��Ʊ���� IN (" + bkggstr + ") \r\n" + 
					" UNION ALL \r\n" + 
					"SELECT rzczjl.��Ʊ���� , rzczjl.����,IF( rzczjl.�������=0,'��ת��',IF(rzczjl.�ҵ� = true,IF(rzczjl.����������־, '�ҵ�����', '�ҵ�����'),IF(rzczjl.����������־,'����','����') )  ) AS ����,rzczjl.ԭ������,rzczjl.ID,rzczjl.�����˺�,'������¼��������' FROM ������¼�������� rzczjl\r\n" + 
					" WHERE rzczjl.��Ʊ���� IN (" + bkggstr + ") \r\n" + 
					" UNION ALL \r\n" + 
					"SELECT zdgz.��Ʊ����, zdgz.����,zdgz.�����Ƴ���־,zdgz.ԭ������,zdgz.ID,null,'������¼�ص��ע' FROM ������¼�ص��ע  zdgz \r\n" + 
					"WHERE zdgz.��Ʊ���� IN (" + bkggstr + ") \r\n" + 
					" UNION ALL \r\n" + 
					"SELECT zdgz.��Ʊ����, zdgz.����ʱ��,'�����ע',null, null,null,'������¼�ص��ע' FROM ��Ʊͨ�����Զ������Ӧ��  zdgz \r\n" + 
					"WHERE zdgz.��Ʊ���� IN (" + bkggstr + ") \r\n" + 
					" AND �Զ�����='ģ����֤' \r\n" + 
					" UNION ALL \r\n" + 
					"SELECT zdgz.��Ʊ����, zdgz.�Ƴ�ʱ��,'�Ƴ���ע',null, null,null,'������¼�ص��ע' FROM ��Ʊͨ�����Զ������Ӧ��  zdgz\r\n" + 
					" WHERE zdgz.��Ʊ���� IN (" + bkggstr + ") \r\n" + 
					"  AND �Զ�����='ģ����֤' AND zdgz.�Ƴ�ʱ�� IS NOT NULL \r\n" + 
					" ORDER BY 1 ,2 DESC "
					;
			CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlquerystat); 
			String curstockcode = ""; Stock tmpstock = null;
			try  {  
				while(rs.next()) {
					String stockcode = rs.getString("��Ʊ����");
					Stock stock = (Stock) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(stockcode, BkChanYeLianTreeNode.TDXGG);
					NodeXPeriodData nodewkperioddata = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
					Object[] row = new Object[6];  
		            row[0] = (java.sql.Date) rs.getDate("����");
		            row[1] = rs.getString(3);
		            row[2] = rs.getString(4);
		            row[3] = rs.getString(5);
		            row[4] = rs.getString(6);
		            row[5] = rs.getString(7);
		            String action = (String) row[1];
					if(action.equals("����")) {
						org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
						((StockNodesXPeriodData)nodewkperioddata).addMaiRuJiLu(wknum, 1);
					} else
					if(action.equals("����")) {
						org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
						((StockNodesXPeriodData)nodewkperioddata).addMaiChuJiLu(wknum, 0);
					} else
					if(action.equals("�����ע")) {
						org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
						((StockNodesXPeriodData)nodewkperioddata).addGzjlToPeriod(wknum, 1);
					} else 
					if(action.equals("�Ƴ���ע")) {
						org.jfree.data.time.Week wknum = new org.jfree.data.time.Week( (java.sql.Date)row[0]);
						((StockNodesXPeriodData)nodewkperioddata).addGzjlToPeriod(wknum, 0);
					}
					
					stock.getNodeJiBenMian().addZdgzmrmcykRecord(row);
				}
				
			 } catch(java.lang.NullPointerException e){	e.printStackTrace();
			 } catch(Exception e) {e.printStackTrace();
			 } finally {
			    	if(rs != null) {	try {rs.close();	} catch (SQLException e) {	e.printStackTrace();}
			    	rs = null;    }
			 }
			
			return bk;
		}
		/*
		 * 
		 */
		public BanKuai getTDXBanKuaiSetForBanKuaiGeGu (BanKuai bk) 
		{
			List<BkChanYeLianTreeNode> bkgg = bk.getAllGeGuOfBanKuaiInHistory();
			if(bkgg == null || bkgg.isEmpty())
				return bk;
			
			List<Stock> bkstock = new ArrayList<> ();
			for(BkChanYeLianTreeNode tmpbkgg : bkgg) 
				bkstock.add( ((StockOfBanKuai)tmpbkgg).getStock()    );
			this.getTDXBanKuaiSetForStocks(bkstock);
			
			return bk;
		}
		/*
		 * 
		 */
		public Stock getTDXBanKuaiSetForStock(Stock stock)
		{
			List<Stock> nodeset = new ArrayList<>();
			nodeset.add((Stock)stock);
			this.getTDXBanKuaiSetForStocks (nodeset ); //ͨ���Ű����Ϣ
			
			return stock;
		}
		/*
		 * 
		 */
		public List<Stock> getTDXBanKuaiSetForStocks(List<Stock> stocklist) 
		{
			String bkggstr = "";
			for(Stock stock : stocklist ) {
				if(  stock.getGeGuCurSuoShuTDXSysBanKuaiList() != null && ! stock.getGeGuCurSuoShuTDXSysBanKuaiList().isEmpty() )
					continue;
				else bkggstr  = bkggstr + "'" + stock.getMyOwnCode() +  "', ";
			}
			if(bkggstr.isEmpty())
				return stocklist;
			else
				bkggstr = bkggstr.substring(0, bkggstr.length() -2);
			
			BanKuaiAndStockTree treeofstkbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks() ;
//			Set<BkChanYeLianTreeNode> stockbanksset = new HashSet<>();
			String sqlquerystat = 
						"SELECT gpgn.`��Ʊ����`,    gpgn.������ ������, tdxbk.`�������` ������� ,  gpgn.`��ƱȨ��`\r\n" + 
						"FROM ��Ʊͨ���Ÿ������Ӧ�� gpgn, ͨ���Ű���б� tdxbk  \r\n" + 
						"WHERE ��Ʊ���� IN (" + bkggstr + " )  AND gpgn.������ = tdxbk.`���ID` AND ISNULL(�Ƴ�ʱ��)\r\n" + 
						"GROUP BY ��Ʊ����, ������\r\n" + 
						"UNION  \r\n" + 
						"\r\n" + 
						"SELECT gphy.`��Ʊ����`,  gphy.������ ������, tdxbk.`�������` ������� , gphy.`��ƱȨ��`\r\n" + 
						"FROM ��Ʊͨ������ҵ����Ӧ�� gphy, ͨ���Ű���б� tdxbk  \r\n" + 
						"WHERE ��Ʊ���� IN (" + bkggstr + " )  AND gphy.`������` = tdxbk.`���ID` AND ISNULL(�Ƴ�ʱ��)\r\n" + 
						"GROUP BY ��Ʊ����, ������\r\n" + 
						"UNION \r\n" + 
						" \r\n" + 
						"SELECT gpfg.`��Ʊ����`, gpfg.`������` ������, tdxbk.`�������` �������  ,  gpfg.`��ƱȨ��`\r\n" + 
						"FROM ��Ʊͨ���ŷ�����Ӧ�� gpfg, ͨ���Ű���б� tdxbk \r\n" + 
						"WHERE ��Ʊ���� IN (" + bkggstr + " )  AND gpfg.`������` = tdxbk.`���ID` AND ISNULL(�Ƴ�ʱ��)\r\n" + 
						"GROUP BY ��Ʊ����, ������\r\n" + 
						"UNION \r\n" + 
						" \r\n" + 
						"SELECT gpfg.`��Ʊ����`,  gpfg.`������` ������, tdxbk.`�������` �������  ,  gpfg.`��ƱȨ��`\r\n" + 
						"FROM ��Ʊͨ���Ž�����ָ����Ӧ�� gpfg, ͨ���Ű���б� tdxbk \r\n" + 
						"WHERE ��Ʊ���� IN (" + bkggstr + ")  AND gpfg.`������` = tdxbk.`���ID` AND ISNULL(�Ƴ�ʱ��) \r\n" + 
						"GROUP BY ��Ʊ����, ������"
						;
			CachedRowSetImpl rs_gn = connectdb.sqlQueryStatExecute(sqlquerystat); 
			String curstockcode = ""; Stock tmpstock = null;
			try  {     
			        while(rs_gn.next()) {
			        	String stockcode = rs_gn.getString("��Ʊ����");
			        	if(!stockcode.equalsIgnoreCase(curstockcode)) {
			        		curstockcode = stockcode;
			        		tmpstock = (Stock)treeofstkbk.getSpecificNodeByHypyOrCode (stockcode,BkChanYeLianTreeNode.TDXGG);
			        	}
			        	
			        	String bkcode = rs_gn.getString("������");
			        	Integer quanzhong = rs_gn.getInt("��ƱȨ��");
			        	BanKuai bk = (BanKuai)treeofstkbk.getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
						StockOfBanKuai bkofst = new StockOfBanKuai(bk,tmpstock);
						bkofst.setStockQuanZhong (quanzhong);
						bk.addNewBanKuaiGeGu(bkofst);
//						stockbanksset.add(bk);
						tmpstock.addGeGuTDXSysBanKuai (bk);
			        } 
			    } catch(java.lang.NullPointerException e){	e.printStackTrace();
			    } catch(Exception e) {e.printStackTrace();
			    } finally {
			    	if(rs_gn != null) {	try {rs_gn.close();	} catch (SQLException e) {	e.printStackTrace();}
			    	rs_gn = null;    }
			    }

			return stocklist;
		}

		public String getStockCodeByName(String stockname) 
		{
			String sqlquerystat= "SELECT ��Ʊ���� FROM A��   WHERE ��Ʊ���� =" +"'" + stockname +"'" ;
			//logger.debug(sqlquerystat);
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
//			sqlquerystat= "SELECT ��Ʊ���� FROM A��   WHERE ������  LIKE" +"'*" + stockname +"*%'" ;
//			sqlstatmap.put("access", sqlquerystat);

			CachedRowSetImpl rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);

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
		 *ͬ����Ʊ����������Ϣ 
		 */
		public Integer refreshCurrentUsedStorkNameOfShenZhen()
		{
			File file = new File(sysconfig.getTDXShenZhenShuNameFile() );
			
			if(!file.exists() ) {
				 logger.debug("File not exist");
				 return -1;
			 }
			
			 int addednumber =0;
			 BufferedInputStream dis = null;
			 FileInputStream in = null;
			 try {
				    in = new FileInputStream(file);  
				    dis = new BufferedInputStream(in);
	               
	               int fileheadbytenumber = 50;
	               byte[] itemBuf = new byte[fileheadbytenumber];
	               dis.read(itemBuf, 0, fileheadbytenumber); 
	               String fileHead =new String(itemBuf,0,fileheadbytenumber);  
	               logger.debug(fileHead);
	               
	               int sigbk = 18*16+12+14;
	               byte[] itemBuf2 = new byte[sigbk];
	               int i=0;
//	               Files.append("��ʼ����ͨ���Ź�Ʊָ������Ӧ��Ϣ:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	               while (dis.read(itemBuf2, 0, sigbk) != -1) {
	            	   String zhishuline =new String(itemBuf2,0,sigbk);
//	            	   logger.debug(zhishuline);
	            	   String zhishucode = zhishuline.trim().substring(0, 6);
	            	   logger.debug(zhishucode);
	            	   
//	            	   if(  !zhishucode.startsWith("000") && !zhishucode.startsWith("001") && !zhishucode.startsWith("002") && !zhishucode.startsWith("300"))
	            	   if( !sysconfig.isShenZhengStock(zhishucode) )
	            		   continue;
	            	   
	            	   List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(zhishuline.substring(6, zhishuline.length()));
	            	   String zhishuname = null;
	            	   try {
	            		   logger.debug(tmplinelist.get(0));
		            	    zhishuname = tmplinelist.get(0).trim().substring(0, 6).trim();
	            	   } catch (java.lang.StringIndexOutOfBoundsException ex) {
	            		   List<String> tmplinepartnamelist = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(1).trim());
	            		   zhishuname = tmplinelist.get(0).trim() + tmplinepartnamelist.get(0).trim();
	            		   if(zhishuname.trim().length() ==2 ) {
	            			   try{
	            			   List<String> tmplinepartnamelist2 = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(2).trim());
	            			   zhishuname = zhishuname + tmplinepartnamelist2.get(0).trim();
	            			   } catch (java.lang.IndexOutOfBoundsException e) {
//	            				   e.printStackTrace();
	            				   logger.debug(tmplinelist + "����");
	            				   
	            			   }
	            		   }
	            			   
	            	   }
	            	   if(zhishuname.trim().length() <4)
	            		   logger.debug(zhishuname);
	            	   if(zhishuname.trim().length() ==2 )
	            		   logger.debug(zhishuname);
	            	   
	            	   String sqlinsertstat = "Update a�� set ��Ʊ���� = '" + zhishuname.trim() + "'\r\n" + 
	            			   					", ���������� = 'SZ' "	+ "\r\n" +
	            			   				   " WHERE ��Ʊ���� = '" + zhishucode.trim() + "' "
	   						   ;
	            	   logger.debug(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//		                Files.append("���룺" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	               }
	               
			 } catch (Exception e) {
				 e.printStackTrace();
				 return -1;
			 } finally {
				 try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	             try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}     
			 }
			
			return -1;
		}
		/*
		 * ͬ����Ʊ����������Ϣ
		 */
		public Integer refreshCurrentUsedStorkNameOfShangHai()
		{
			File file = new File(sysconfig.getTDXShangHaiZhiShuNameFile() );
			
			if(!file.exists() ) {
				 logger.debug("File not exist");
				 return -1;
			 }
			
			 int addednumber =0;
			 BufferedInputStream dis = null;
			 FileInputStream in = null;
			 try {
				    in = new FileInputStream(file);  
				    dis = new BufferedInputStream(in);
	               
	               int fileheadbytenumber = 50;
	               byte[] itemBuf = new byte[fileheadbytenumber];
	               dis.read(itemBuf, 0, fileheadbytenumber); 
	               String fileHead =new String(itemBuf,0,fileheadbytenumber);  
//	               logger.debug(fileHead);
	               
	               int sigbk = 18*16+12+14;
	               byte[] itemBuf2 = new byte[sigbk];
	               int i=0;
//	               Files.append("��ʼ����ͨ���Ź�Ʊָ������Ӧ��Ϣ:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	               while (dis.read(itemBuf2, 0, sigbk) != -1) {
	            	   String zhishuline =new String(itemBuf2,0,sigbk);
//	            	   logger.debug(zhishuline);
	            	   String zhishucode = zhishuline.trim().substring(0, 6);
//	            	   logger.debug(zhishucode);
	            	   
//	            	   if(  !(zhishucode.startsWith("60") ) )
	            	   if( ! sysconfig.isShangHaiStock(zhishucode) )
	            		   continue;
	            	   
	            	   List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(zhishuline.substring(6, zhishuline.length()));
	            	   String zhishuname = null;
	            	   try {
		            	    zhishuname = tmplinelist.get(0).trim().substring(0, 6).trim();
	            	   } catch (java.lang.StringIndexOutOfBoundsException ex) {
	            		   List<String> tmplinepartnamelist = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(1).trim());
	            		   zhishuname = tmplinelist.get(0).trim() + tmplinepartnamelist.get(0).trim();
	            	   }
	            	   if(zhishuname.trim().length() <4)
	            		   logger.debug(zhishuname);
	            	   if(zhishuname.trim().length() == 2)
	            		   logger.debug(zhishuname);
	            	   
	            	   String sqlinsertstat = "Update a�� set ��Ʊ���� = '" + zhishuname.trim() + "'\r\n" + 
	            			   				   ", ���������� = 'SH' "	+ "\r\n" +
	            	   						   " WHERE ��Ʊ���� = '" + zhishucode.trim() + "' "
		   						;
//	            	   logger.debug(sqlinsertstat);
		   			   int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//		               Files.append("���룺" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	               }
	               
			 } catch (Exception e) {
				 e.printStackTrace();
				 return -1;
			 } finally {
				 try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	             try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}     
			 }
			
			return 1;
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
				 logger.debug("File not exist");
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
			           	logger.debug(tmplinelist);
			           	
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
				 
				 updateStockEverUsedName (stockcode,cymstr);
				 
//				 String sqlupdatestat= "UPDATE A��  SET "
//							+ " ������=" + "'" + cymstr + "'"
//							+ " WHERE ��Ʊ����=" + stockcode
//							;
//				 logger.debug(sqlupdatestat);
//				 connectdb.sqlUpdateStatExecute(sqlupdatestat);
			 }
			 
			return tmprecordfile;
		}
		
		public void updateStockEverUsedName (String stockcode, String everusedname)
		{
			 String sqlupdatestat= "UPDATE A��  SET "
						+ " ������=" + "'" + everusedname + "'"
						+ " WHERE ��Ʊ����=" + stockcode
						;
			 logger.debug(sqlupdatestat);
			 try {
				connectdb.sqlUpdateStatExecute(sqlupdatestat);
			} catch (MysqlDataTruncation e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		public Object[][] getTodaysOperations() 
		{
			 String searchdate = CommonUtility.formatDateYYYY_MM_DD_HHMMSS( LocalDateTime.now() );
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
			logger.debug(sqlquerystat2);
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
			logger.debug(sqlquerystat3);
			String sqlquerystat4=" SELECT rzczjl.��Ʊ����,"
								+ "IF( rzczjl.�������=0.0,'��ת��',IF(rzczjl.�ҵ� = true,IF(rzczjl.����������־, '�ҵ�����', '�ҵ�����'),IF(rzczjl.����������־,'����','����') )  ) AS ����,"
								+ "rzczjl.ԭ������,"
								+ "rzczjl.ID,"
								+ "rzczjl.�����˺�,"
								+ "'������¼��������'"
								+ " FROM ������¼�������� rzczjl"
								+ " WHERE rzczjl.����>=" + "'" + searchdate + "'"
								
								
								;
			logger.debug(sqlquerystat4);
			String sqlquerystat =  sqlquerystat2 + sqlquerystat3 + sqlquerystat4 ;
	     
			 CachedRowSetImpl rs = null;
			 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			 
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
		/*
		 * 
		 */
		public File refreshStockJiBenMianInfoFromTdxFoxProFile ()
		{
			File tmpreportfolder = Files.createTempDir();
			File tmprecordfile = new File(tmpreportfolder + "ͬ��ͨ���Ÿ��ɻ����汨��.tmp");
			
			Charset stringCharset = Charset.forName("Cp866");
			String dbffile = sysconfig.getTdxFoxProFileSource();
//	        InputStream dbf = getClass().getClassLoader().getResourceAsStream(dbffile);
	        InputStream dbf = null;
			try {
				dbf = new FileInputStream(new File(dbffile) );
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

	        DbfRecord rec;
	        String sqlinsetstat = null;
	        try (DbfReader reader = new DbfReader(dbf)) {
	            DbfMetadata meta = reader.getMetadata();

	            logger.debug("Read DBF Metadata: " + meta);
	            int recCounter = 0;
	            while ( (rec = reader.read() ) != null) {
	                rec.setStringCharset(stringCharset);
//	                logger.debug("Record is DELETED: " + rec.isDeleted());
//	                logger.debug(rec.getRecordNumber());
//	                logger.debug(rec.toMap());
//	        		{SC=0, GPDM=000001, GXRQ=20170811, ZGB=1717041.12, GJG=180199.00, FQRFRG=12292000.00, FRG=54769000.00, BG=0.00, HG=0.00, LTAG=1691799.00, ZGG=0.68, 
//	        		ZPG=null, ZZC=3092142080.0, LDZC=0.0, GDZC=7961000.0, WXZC=4636000.0, CQTZ=379179.0, LDFZ=0.0, CQFZ=0.0, ZBGJJ=56465000.0, JZC=211454000.0, 
//	        		ZYSY=54073000.0, ZYLY=0.0, QTLY=0.0, YYLY=40184000.0, TZSY=739000.0, BTSY=-128180000.0, YYWSZ=-107760000.0, SNSYTZ=0.0, LYZE=16432000.0, 
//	        		SHLY=12554000.0, JLY=12554000.0, WFPLY=73110000.0, TZMGJZ=11.150, DY=18, HY=1, ZBNB=6, SSDATE=19910403, MODIDATE=null, GDRS=null}

	                Map<String, Object> jbminfomap = rec.toMap();
	                String stockcode = jbminfomap.get("GPDM").toString();
	                String datavalueindb = jbminfomap.get("GXRQ").toString() ;
	                if( ! (sysconfig.isShangHaiStock(stockcode) || sysconfig.isShenZhengStock(stockcode) )
	                		|| stockcode.equals("000003") 
	                		|| datavalueindb.equals("0") )
	                	continue;

	                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	                LocalDate lastupdatedate = LocalDate.parse(datavalueindb,formatter );
	                double zgb = Double.parseDouble(jbminfomap.get("ZGB").toString());
	                double gjg  = Double.parseDouble(jbminfomap.get("GJG").toString());
	                double fqrfrg  = Double.parseDouble(jbminfomap.get("FQRFRG").toString());
	                double frg  = Double.parseDouble(jbminfomap.get("FRG").toString());
	                double bg  = Double.parseDouble(jbminfomap.get("BG").toString());
	                double hg  = Double.parseDouble(jbminfomap.get("HG").toString());
	                double ltag  = Double.parseDouble(jbminfomap.get("LTAG").toString());
	                double zgg  = Double.parseDouble(jbminfomap.get("ZGG").toString());
	                
	                String zpg = "' '";
	                if(jbminfomap.get("ZPG") != null)
	                	zpg  = jbminfomap.get("ZPG").toString(); 
	                double zzc  = Double.parseDouble(jbminfomap.get("ZZC").toString());
	                double ldzc  = Double.parseDouble(jbminfomap.get("LDZC").toString());
	                double gdzc  = Double.parseDouble(jbminfomap.get("GDZC").toString());
	                double wxzc  = Double.parseDouble(jbminfomap.get("WXZC").toString());
	                float cqtz  = Float.parseFloat(jbminfomap.get("CQTZ").toString());
	                double ldfz  = Double.parseDouble(jbminfomap.get("LDFZ").toString());
	                double cqfz  = Double.parseDouble(jbminfomap.get("CQFZ").toString());
	                double zbgjj  = Double.parseDouble(jbminfomap.get("ZBGJJ").toString());
	                double jzc  = Double.parseDouble(jbminfomap.get("JZC").toString());
	                
	                double zysy  = Double.parseDouble(jbminfomap.get("ZYSY").toString());
	                double zyly  = Double.parseDouble(jbminfomap.get("ZYLY").toString());
	                double qtly  = Double.parseDouble(jbminfomap.get("QTLY").toString());
	                double yyly  = Double.parseDouble(jbminfomap.get("YYLY").toString());
	                double tzsy  = Double.parseDouble(jbminfomap.get("TZSY").toString());
	                double btsy  = Double.parseDouble(jbminfomap.get("BTSY").toString());
	                double yywsz  = Double.parseDouble(jbminfomap.get("YYWSZ").toString());
	                double snsytz  = Double.parseDouble(jbminfomap.get("SNSYTZ").toString()); 
	                double lyze  = Double.parseDouble(jbminfomap.get("LYZE").toString());
	                
	                double shly  = Double.parseDouble(jbminfomap.get("SHLY").toString());
	                double jly  = Double.parseDouble(jbminfomap.get("JLY").toString());
	                double wfply  = Double.parseDouble(jbminfomap.get("WFPLY").toString());
	                double tzmgjz  = Double.parseDouble(jbminfomap.get("TZMGJZ").toString());
	                int dy = Integer.parseInt(jbminfomap.get("DY").toString() );
	                int hy = Integer.parseInt(jbminfomap.get("HY").toString() );
	                int zbnb = Integer.parseInt(jbminfomap.get("ZBNB").toString() );
	                LocalDate shangshidate = null;
	                try {
	                	 shangshidate = LocalDate.parse(jbminfomap.get("SSDATE").toString(),formatter );
	                } catch (java.time.format.DateTimeParseException e) {
	                	shangshidate = LocalDate.parse("1992-01-01");
	                }
	                
	                 sqlinsetstat = "INSERT INTO ��Ʊͨ���Ż�������Ϣ��Ӧ�� (��Ʊ����GPDM, �ܹɱ�ZGB,��������GXRQ, GJG,FQRFRG,���˹�FRG,B��BG,H��HG,��ͨA��LTAG,ÿ������ZGG,"
	                														+  "ZPG,���ʲ�ZZC,�����ʲ�LDZC,�̶��ʲ�GDZC,�����ʲ�WXZC,�ɶ�����CQTZ,������ծLDFZ,������ȨCQFZ,"
	                														+ "������ZBGJJ,���ʲ�JZC,��Ӫ����ZYSY,Ӫҵ�ɱ�ZYLY,Ӧ���ʿ�QTLY,Ӫҵ����YYLY,Ͷ������TZSY,"
	                														+ "��Ӫ�ֽ���BTSY,���ֽ���YYWSZ,���SNSYTZ,�����ܶ�LYZE,˰������SHLY,������JLY,"
	                														+ "δ��������WFPLY,���ʲ�TZMGJZ,����DY,��ҵHY,ZBNB,��������SSDATE) "
	                		              		+ "VALUES("
	                		              		+ "'" + stockcode + "'" +","
	                		              		+ zgb +","
	                		              		+ "'" + lastupdatedate +"',"
	                		              		+ gjg +","
	                		              		+ fqrfrg +","
	                		              		+ frg +","
			                					+ bg +","
			                					+ hg +","
			                					 + ltag +","
			                					 + zgg + ","
			                					+ zpg +","
			                					+ zzc +","
			                					+ ldzc +","
			                					+ gdzc +","
			                					+ wxzc +","
			                					+ cqtz +","
			                					+ ldfz +","
			                					+ cqfz +","
			                					+ zbgjj +","
			                					+ jzc +","
			                					+ zysy +","
			                					+ zyly +","
			                					+ qtly +","
			                					+ yyly +","
			                					+ tzsy +","
			                					+ btsy +","
			                					+ yywsz +","
			                					+ snsytz +","
			                					+ lyze +","
			                					+ shly +","
			                					+ jly +","
			                					+ wfply +","
			                					+ tzmgjz +","
			                					+ dy +","
			                					+ hy +","
			                					+ zbnb +","
			                					+ "'" + shangshidate +"'" 
	                		              		+ ")"
	                		              		+ " ON DUPLICATE KEY UPDATE "
	                        					+ " �ܹɱ�ZGB=" + zgb +","
	                        					+ " ��������GXRQ= '" + lastupdatedate +"' ,"
	                        					+ " GJG=" + gjg +","
	                        					+ " FQRFRG=" + fqrfrg +","
	                        					+ " ���˹�FRG=" + frg +","
	                        					+ " B��BG=" + bg +","
	                        					+ " H��HG=" + hg +","
	                        					+ " ��ͨA��LTAG=" + ltag +","
	                        					+ " ÿ������ZGG=" + zgg + ","
	                        					+ " ZPG=" + zpg +","
	                        					+ " ���ʲ�ZZC=" + zzc +","
	                        					+ " �����ʲ�LDZC=" + ldzc +","
	                        					+ " �̶��ʲ�GDZC=" + gdzc +","
	                        					+ " �����ʲ�WXZC=" + wxzc +","
	                        					+ " �ɶ�����CQTZ=" + cqtz +","
	                        					+ " ������ծLDFZ=" + ldfz +","
	                        					+ " ������ȨCQFZ=" + cqfz +","
	                        					+ " ������ZBGJJ=" + zbgjj +","
	                        					+ " ���ʲ�JZC=" + jzc +","
	                        					+ " ��Ӫ����ZYSY=" + zysy +","
	                        					+ " Ӫҵ�ɱ�ZYLY=" + zyly +","
	                        					+ " Ӧ���ʿ�QTLY=" + qtly +","
	                        					+ " Ӫҵ����YYLY=" + yyly +","
	                        					+ " Ͷ������TZSY=" + tzsy +","
	                        					+ " ��Ӫ�ֽ���BTSY=" + btsy +","
	                        					+ " ���ֽ���YYWSZ=" + yywsz +","
	                        					+ " ���SNSYTZ=" + snsytz +","
	                        					+ " �����ܶ�LYZE=" + lyze +","
	                        					+ " ˰������SHLY=" + shly +","
	                        					+ " ������JLY=" + jly +","
	                        					+ " δ��������WFPLY=" + wfply +","
	                        					+ " ���ʲ�TZMGJZ=" + tzmgjz +","
	                        					+ " ����DY=" + dy +","
	                        					+ " ��ҵHY=" + hy +","
	                        					+ " ZBNB=" + zbnb +" ,"
	                        					+ " ��������SSDATE= '" + shangshidate + "'"
	                        					
	                        					; 
	              logger.debug(sqlinsetstat);
	              connectdb.sqlUpdateStatExecute(sqlinsetstat);
	                recCounter++;

	                //����ڱ�A����Ҳû�������Ʊ���룬����A����Ҳ���Ӹù�Ʊ
	                sqlinsetstat = "INSERT INTO A�� (��Ʊ����)"
	                		+ "   SELECT * FROM (SELECT '" + stockcode + "') AS tmp"
	                		+ "   WHERE NOT EXISTS ("
	                		+ "   SELECT ��Ʊ���� FROM A�� WHERE ��Ʊ���� = '" + stockcode + "'"
	                		+ ") LIMIT 1"
	                		;
	                int insertresult = connectdb.sqlInsertStatExecute(sqlinsetstat);
	                if(insertresult >0) {
	                	String jys;
//	                	if(stockcode.startsWith("60"))
	                	if( sysconfig.isShangHaiStock(stockcode) )
	                		jys = "SH";
	                	else
	                		jys = "SZ";
	                	String sqlinsertstat = "Update a�� SET ���������� = '" + jys + "'"	+ "\r\n" +
 	   						   " WHERE ��Ʊ���� = '" + stockcode.trim() + "' "
 	   						   ;
	                	int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
	                }
	            }
	        } catch (IOException e1) {
	        	logger.debug("����SQL��:" + sqlinsetstat);
				e1.printStackTrace();
			} catch (ParseException e2) {
				logger.debug("����SQL��:" + sqlinsetstat);
				
				e2.printStackTrace();
			} catch (Exception e3) {
				logger.debug("����SQL��:" + sqlinsetstat);
				e3.printStackTrace();
			}
	        
	        return tmprecordfile;
			
		}
		/*
		 * ���ð�������: �Ƿ������ݣ��Ƿ�����ڰ����������У��Ƿ񵼳���Gephi
		 */
		public void updateBanKuaiOperationsSettings(BkChanYeLianTreeNode node, boolean importdailydata, boolean exporttogephi, 
				boolean showinbkfx,boolean showincyltree, boolean exporttowkfile, boolean importbkgg, Color bkcolor, boolean corezhishu)
		{
			String colorcode = String.format("#%02x%02x%02x", bkcolor.getRed(), bkcolor.getGreen(), bkcolor.getBlue() );
			
			String sqlupdatestat = "UPDATE ͨ���Ű���б� SET " +
								" ���뽻������=" + importdailydata  + "," +
								" ����Gephi=" + exporttogephi +  ","  +
								" ������=" + showinbkfx +  ","  +
								" ��ҵ����=" + showincyltree + ","  +
								" �ܷ����ļ� = " + exporttowkfile + ","  +
								" DefaultCOLOUR = '" + colorcode + "',"  +
								" ����ָ�� = " + corezhishu + ","  +
								" ��������� = " + importbkgg +
								" WHERE ���ID='" + node.getMyOwnCode() + "'"
								;
	   		try {	int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
			} catch (MysqlDataTruncation e) {e.printStackTrace();
			} catch (SQLException e) {e.printStackTrace();}
	   		
		}
		/*
		 * �������׹�Ʊ����ӿڵ����ݣ�
		 */
		public File importNetEaseStockData(String jiaoyisuo) 
		{
			File tmpreportfolder = Files.createTempDir();
			File tmprecordfile = new File(tmpreportfolder + "�������׹�Ʊ��Ϣ.tmp");
				
			try {
				FileUtils.cleanDirectory(new File(sysconfig.getNetEaseDownloadedFilePath () ) );
			} catch (IOException e1) {e1.printStackTrace();}
			
			Collection<BkChanYeLianTreeNode> allgegucode = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXGG,jiaoyisuo);		

			String optTable = null;
			if(jiaoyisuo.equalsIgnoreCase("SZ")  )
				optTable = "ͨ���������Ʊÿ�ս�����Ϣ";
			else if(jiaoyisuo.equalsIgnoreCase("SH") )
				optTable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
			
			String sqlquerystat = "SELECT * FROM \r\n" + 
					"(SELECT ��Ʊ����  FROM A��\r\n" + 
					"WHERE ���������� = '" + jiaoyisuo + "' ) agu\r\n" + 
					"\r\n" + 
					"LEFT JOIN \r\n" + 
					"( SELECT ����, MIN(��������) minjytime , MAX(��������) maxjytime FROM " + optTable + "\r\n" + 
					" WHERE  �ǵ��� IS NOT NULL " +
					" GROUP BY ����) shjyt\r\n" + 
					" ON agu.��Ʊ���� =  shjyt.`����`\r\n"
					;
			CachedRowSetImpl  rsdm = connectdb.sqlQueryStatExecute(sqlquerystat);
			try { 	
		    	while(rsdm.next()) {
			    		 String ggcode = rsdm.getString("��Ʊ����"); //mOST_RECENT_TIME
			    		 Stock stock = (Stock) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(ggcode, BkChanYeLianTreeNode.TDXGG);
			    		 if(stock == null) 
			    			 continue;
			    		 try { LocalDate mintime = rsdm.getDate("minjytime").toLocalDate();
			    		 		stock.getShuJuJiLuInfo().setNeteasejlmindate(mintime);
			    		 } catch (java.lang.NullPointerException ex) {}
			    		 try { LocalDate maxtime = rsdm.getDate("maxjytime").toLocalDate();
			    		 		stock.getShuJuJiLuInfo().setNeteasejlmaxdate(maxtime);
			    		 } catch (java.lang.NullPointerException ex) {}
			    	}
			    } catch(java.lang.NullPointerException e) {	e.printStackTrace();
			    } catch (SQLException e) {	e.printStackTrace();
			    } catch(Exception e){	e.printStackTrace();
			    } finally {
			    	if(rsdm != null)	try {rsdm.close();rsdm = null;	} catch (SQLException e) {	e.printStackTrace();}
			    }

			//������Ҫ��ʱ��ε�����
			for(BkChanYeLianTreeNode tmpnode : allgegucode) {
				Stock stock = (Stock)tmpnode;
				boolean neteasthasdata = false;
				String stockcode = stock.getMyOwnCode();
				String formatedstockcode; 
				if(stock.getNodeJiBenMian().getSuoShuJiaoYiSuo().equalsIgnoreCase("sh"))  
					formatedstockcode = "0" + stockcode;
				else 
					formatedstockcode = "1" + stockcode;
				
				LocalDate ldlastestdbrecordsdate = stock.getShuJuJiLuInfo().getNeteasejlmaxdate();
			   if(ldlastestdbrecordsdate == null)
				   ldlastestdbrecordsdate =  LocalDate.parse("2013-03-04"); //��ǰ���ݵ����
			   else
				   ldlastestdbrecordsdate = ldlastestdbrecordsdate.plusDays(1); //���������ݵĺ�һ�쿪ʼ��������
			   
			   
//				 if( ( ldlastestdbrecordsdate.getDayOfWeek() == DayOfWeek.SUNDAY || ldlastestdbrecordsdate.getDayOfWeek() == DayOfWeek.SATURDAY  )
//						 && ( LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY || LocalDate.now().getDayOfWeek() == DayOfWeek.SATURDAY   )  ) //��ʼ������������һֱ����������ĩ���϶�����Ҫ����
//					 continue;
				    
				    //��ȡ�����ļ�
				    URL URLink; //https://blog.csdn.net/NarutoInspire/article/details/72716724
				    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
				    String startdate = ldlastestdbrecordsdate.format(formatters);
				    
				    LocalTime tdytime = LocalTime.now(); //������ڽ���ʱ�䵼�����ݵĻ�����������ݻ�û�У�����Ҫ�ж�һ��
				    LocalDate endldate = LocalDate.now(); 
					if( ( tdytime.compareTo(LocalTime.of(9, 0, 0)) >0 && tdytime.compareTo(LocalTime.of(18, 0, 0)) <0) ) 
						 endldate = endldate.minusDays(1);
				    
					if(ldlastestdbrecordsdate.compareTo(endldate) >0)  //˵��ֻ�н��������û�е��룬�����컹û�����ݣ���������
						continue;
					
				    String enddate = endldate.format(formatters);
				    String urlstr = "http://quotes.money.163.com/service/chddata.html?code=" + formatedstockcode + "&start=" + startdate + 
				    				"&end=" + enddate + "&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";
				    String savedfilename = sysconfig.getNetEaseDownloadedFilePath () + stockcode + ".csv";
				    
				    File savedfile = null;
					try {
						URLink = new URL( urlstr); //"http://quotes.money.163.com/service/chddata.html?code=0601857&start=20071105&end=20150618&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP"
						
						savedfile = new File (savedfilename);
						if(savedfile.exists())
							savedfile.delete();
						
						FileUtils.copyURLToFile(URLink, savedfile,10000,10000); //http://commons.apache.org/proper/commons-io/javadocs/api-2.4/org/apache/commons/io/FileUtils.html#copyURLToFile(java.net.URL,%20java.io.File)
					} catch (java.net.SocketTimeoutException e)  {logger.info("��ȡ" + stockcode + "�������ݳ�ʱ��");
					} catch ( IOException e) { e.printStackTrace();
					} finally {URLink = null;
					}
					//�������ݵ����ݿ�
					if(!savedfile.exists()) {
						System.out.println(stockcode + "��δ�ܴ����׻��"+ stockcode + "�������ļ������飡");
						try {
							Files.append(stockcode + "δ�ܴ����׵õ�"+ stockcode + "�������ļ������飡" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
						} catch (IOException e) {e.printStackTrace();
						}
						neteasthasdata = false;
					} else {
						try (
								FileReader filereader = new FileReader(savedfile);
								CSVReader csvReader = new CSVReader(filereader);
					    ){ 
							int titlesign = -1;//�������CSV�ļ��ĵ�һ��
							Integer hsl_index = null; Integer zsz_index = null; Integer zdf_index = null; Integer ltsz_index = null; Integer spj_index = null;
							List<String[]> records = csvReader.readAll();
							for (String[] nextRecord : records) {
					            	if(-1 == titlesign) {
					            		for(int m=0;m<nextRecord.length;m++) {
					            			if(nextRecord[m].equalsIgnoreCase("������")) 
					            				hsl_index = m;
					            			else if(nextRecord[m].equalsIgnoreCase("����ֵ")) 
					            				zsz_index = m;
					            			else if(nextRecord[m].equalsIgnoreCase("�ǵ���")) 
					            				zdf_index = m;
					            			else if(nextRecord[m].equalsIgnoreCase("��ͨ��ֵ")) 
					            				ltsz_index = m;
					            			else if(nextRecord[m].equalsIgnoreCase("���̼�")) 
					            				spj_index = m;
					            		}
//						                String huanshoulv = nextRecord[10];
//						                String zongshizhi = nextRecord[13];
//						                
//						                if(!huanshoulv.trim().equals("������") || !zongshizhi.trim().equals("����ֵ")) {
//						                	System.out.println("���������ļ���ʽ�����иı䣬���飡");
//						                	Files.append("���������ļ���ʽ�����иı䣬���飡" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//						                	neteasthasdata = false;
//						                	break;
//						                } else 
						                	titlesign ++;
					            	} else { //�ڶ���
					            		String shoupanjia = nextRecord[spj_index];
					            		if(Double.parseDouble(shoupanjia) == 0) //���̼۲�����Ϊ0��0˵��ͣ�ƣ��ڽ������ݿ���У�ͣ����û�м�¼�ģ����� 
					            			continue;
					            		
					            		LocalDate lactiondate = null;
					            		String actiondate =  nextRecord[0];
					            		if(Pattern.matches("\\d*/\\d*/\\d*",actiondate) ) {
					            			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
					            			lactiondate = LocalDate.parse(actiondate, formatter);
					            		} else if(Pattern.matches("\\d*-\\d*-\\d*",actiondate) ) {
					            			lactiondate = LocalDate.parse(actiondate);
					            		} else {
					            			System.out.println("���ش���, ���������ļ����ڸ�ʽ�ı䣡");
					            			neteasthasdata = false;
						                	break;
					            		}
					            		Double zhangdiefu = null;Double huanshoulv = null;Double zongshizhi = null;Double liutongshizhi = null;
					            		if(zdf_index!= null) {
					            			String zhangdiefu_str = nextRecord[zdf_index]; if(!Strings.isNullOrEmpty(zhangdiefu_str)) zhangdiefu = Double.parseDouble(zhangdiefu_str);
					            		}
					            		if(hsl_index!= null) {
					            			String huanshoulv_str = nextRecord[hsl_index]; if(!Strings.isNullOrEmpty(huanshoulv_str)) huanshoulv = Double.parseDouble(huanshoulv_str);
					            		}
					            		if(zsz_index!= null) {
					            			String zongshizhi_str = nextRecord[zsz_index]; if(!Strings.isNullOrEmpty(zongshizhi_str)) zongshizhi = Double.parseDouble(zongshizhi_str);
					            		}
					            		if(ltsz_index!= null) {
					            			String liutongshizhi_str = nextRecord[ltsz_index]; if(!Strings.isNullOrEmpty(liutongshizhi_str)) liutongshizhi = Double.parseDouble(liutongshizhi_str);
					            		}
						                
//						                String sqlupdateorinsert = "INSERT INTO " + optTable + "(�ǵ���,������,��ͨ��ֵ,����ֵ) VALUES("
//						                							+ zhangdiefu + "," 
//						                							+ huanshoulv + ","
//						                							+ liutongshizhi + ","
//						                							+ zongshizhi
//						                							+ ") ON DUPLICATE KEY UPDATE" 
//						                							+ "�������� = '" + lactiondate + "'," 
//						                							+ "����= '" + stockcode + "'"
//						                							;	
						                String sqlupdate = "Update " + optTable + " SET " +
						                					" �ǵ���=" + zhangdiefu + 
//						                					" ,������=" + huanshoulv + 
//						                					" ,��ͨ��ֵ= " + liutongshizhi + 
//						                					" ,����ֵ= " + zongshizhi +
						                					" WHERE �������� = '" + lactiondate + "'" + 
						                					" AND ����= '" + stockcode + "'"
						                					;
									    try {
											int result = connectdb.sqlUpdateStatExecute(sqlupdate);
										} catch (MysqlDataTruncation e) {e.printStackTrace();
										} catch (SQLException e) {e.printStackTrace();}
									    
									    Files.append(stockcode + "�����������ݳɹ���" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
									    
									    neteasthasdata = true;
					            	}
					            }
					            if(!neteasthasdata) 
					            	Files.append(stockcode + "�����ļ�û�����ݣ�����0����¼��" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());

					            records = null;
					        } catch (IOException e) {e.printStackTrace();} 
					}
			}
			return tmprecordfile;
		}
		/*
		 * ��ѩ�������ݣ�Ŀǰ������
		 */
		private void importXueQiuStockData(String stockcode,LocalDate ldlastestdbrecordsdate)
		{
//			Authenticator.setDefault (new Authenticator() {
//			    protected PasswordAuthentication getPasswordAuthentication() {
//			        return new PasswordAuthentication ("justjustjustjust@gmail.com", "sure2001".toCharArray());
//			    }
//			});
			

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet("https://xueqiu.com/stock/forchartk/stocklist.json?symbol=SH600756&period=1day&type=before&begin=1478620800000&end=1510126200000&_=1510126200000");
			httpGet.addHeader(BasicScheme.authenticate(
			 new UsernamePasswordCredentials("justjustjustjust@gmail.com", "sure2001"),
			 "GBK", false));

			HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpEntity responseEntity = httpResponse.getEntity();
			try {
				InputStreamReader isr = new InputStreamReader(responseEntity.getContent() );
				int numCharsRead;
				char[] charArray = new char[1024];
				StringBuffer sb = new StringBuffer();
				while ((numCharsRead = isr.read(charArray)) > 0) {
					sb.append(charArray, 0, numCharsRead);
				}
				String result = sb.toString();
				if(result.contains("error")) { //������ʱ���ò������ݣ���Ҫ��ѩ��ķ����õ�����
					logger.info(stockcode + "δ�ܴ�ѩ���ȡ����Ϊ" + ldlastestdbrecordsdate + "���ݣ�������������Դ��ȡ���ݣ�");
					importSoHuStockData (stockcode,ldlastestdbrecordsdate);
				} else {
					System.out.println(result);
				}
				
			} catch (UnsupportedOperationException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*
		 * �Ѻ�����ֻ���ǵ����ͻ����ʣ�û����ͨ��ֵ������ֵ����
		 */
		private void importSoHuStockData(String stockcode, LocalDate ldlastestdbrecordsdate) 
		{
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
		    String startdate = ldlastestdbrecordsdate.format(formatters);
		    String enddate = LocalDate.now().format(formatters);
		    //              "http://q.stock.sohu.com/hisHq?code=cn_000001&start=20170930&end=20181231&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp");
		    String urlstr = "http://q.stock.sohu.com/hisHq?code=cn_" + stockcode + "&start=" + startdate + "&end=" + enddate + "&stat=1&order=D&period=d&callback=historySearchHandler&rt=json";
//		    String urlstr = "http://cq.ssajax.cn/interact/getTradedata.ashx?pic=qlpic_000001_2_6";
			HttpGet httpGet = new HttpGet(urlstr);

			HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			HttpEntity responseEntity = httpResponse.getEntity();
			try {
				InputStreamReader isr = new InputStreamReader(responseEntity.getContent() );
				int numCharsRead;
				char[] charArray = new char[1024];
				StringBuffer sb = new StringBuffer();
				while ((numCharsRead = isr.read(charArray)) > 0) {
					sb.append(charArray, 0, numCharsRead);
				}
				String result = sb.toString();
				if(result.contains("error")) { 
					logger.info(stockcode + "δ�ܴ��Ѻ���ȡ����Ϊ" + ldlastestdbrecordsdate + "���ݣ�������������Դ��ȡ���ݣ�");
				} else {
//					Map jsonJavaRootObject = new Gson().fromJson(result, Map.class);
//					Map<String, Object> map = new Gson().fromJson(result, new TypeToken<Map<String, Object>>(){}.getType());
//					map.forEach((x,y)-> System.out.println("key : " + x + " , value : " + y));
				}
				
			} catch (UnsupportedOperationException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		public void setStockAsYiJingTuiShi(String formatStockCode) 
		{
			// TODO Auto-generated method stub
			
		}
		/*
		 *���ڴ����д��󣬵��°��͸��ɵĶ�Ӧ��ϵ�п��ܳ����ظ��� 
		 */
		private void checkImportTDXDataDuplicateError(File logfile)
		{
			CachedRowSetImpl rspd = null;
			String tablename[] = {"��Ʊͨ���ŷ�����Ӧ��","��Ʊͨ���Ÿ������Ӧ��","��Ʊͨ������ҵ����Ӧ��","��Ʊͨ���Ž�����ָ����Ӧ��"};
			Charset charset = Charset.forName("GBK") ;
			int countaddline =0;
			for(int i=0;i<tablename.length;i++) {
				String sqlquerystat = "SELECT ������, ��Ʊ����, �Ƴ�ʱ��, COUNT(*) " +
						" FROM  " + tablename[i] + 
						" where isnull(�Ƴ�ʱ��)" +
						" GROUP BY  ������,��Ʊ����,isnull(�Ƴ�ʱ��)" +
						" HAVING   COUNT(*) > 1"
						;
				rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
				try {
					while(rspd.next())  {
						String bkcode = rspd.getString("������");
						String stockcode = rspd.getString("��Ʊ����");
					     
						 Integer duplinecount = rspd.getInt("COUNT(*)");
						 if(duplinecount == 2) { //ֻ��2�У��Ǿ�ֱ���޲�
							 CachedRowSetImpl rsdup = null;
							 sqlquerystat = "SELECT MAX(����ʱ��), MIN(id)\n" + 
										"FROM  ��Ʊͨ���ŷ�����Ӧ��\n" + 
										"where ������ = '" + bkcode + "' and ��Ʊ���� = '" + stockcode + "' and isnull(�Ƴ�ʱ��) "
										;
							 rsdup = connectdb.sqlQueryStatExecute(sqlquerystat);
							 while(rsdup.next()) {
								 java.util.Date maxdupdate = rsdup.getDate("MAX(����ʱ��)");
								 Integer minid = rsdup.getInt("MIN(id)");
								 String sqlupdatequery = "UPDATE ��Ʊͨ���ŷ�����Ӧ�� SET �Ƴ�ʱ�� = '" + maxdupdate + "' \n" + 
														  "where id = '" + minid + "'"
															;
								 int result = connectdb.sqlUpdateStatExecute(sqlupdatequery);
								 
								 String updateresult = tablename[i] + "  " + bkcode + "  �ĸ���  "  + stockcode  + "  ���������ظ����Ѿ����£�������" + maxdupdate.toString() + "���µ�ID=" + minid.toString() + System.getProperty("line.separator");
								 logger.debug(updateresult);
								 try {
										Files.append(updateresult,logfile, charset);
										countaddline ++;
								 } catch (IOException e) {
										e.printStackTrace();
								 }
								 
							 }
							 rsdup.close();
							 rsdup = null;
						 } else {
							 String stock = "��Ҫ��" + tablename[i] + "  " + bkcode + "  �ĸ���  "  + stockcode  + "  ���������ظ�,δ���£����ֶ����£�"+ System.getProperty("line.separator");
							 logger.debug(stock);
							 try {
									Files.append(stock,logfile, charset);
									countaddline ++;
							 } catch (IOException e) {
									e.printStackTrace();
							 }
						 }
						 
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}catch(Exception e){
				    	e.printStackTrace();
				} finally {
				    	if(rspd != null)
						try {
							rspd.close();
							rspd = null;
							
						} catch (SQLException e) {
							e.printStackTrace();
						}
				}
			}
			
		}
		
		/*
		 * checkÿ�յ���������Ƿ�����,��������reports
		 */
		private File checkTDXDataImportIsCompleted ()
		{
			LocalDate today = LocalDate.now();
			TemporalField fieldISO = WeekFields.of(Locale.CHINA).dayOfWeek();
			LocalDate monday = today.with(fieldISO, 2);
			LocalDate friday = today.with(fieldISO, 6);
			
			String checkfilename = sysconfig.getTdxDataImportCheckResult () + friday.toString() +  "ͨ�������ݵ�������.txt";
			File filefmxx = new File( checkfilename );
			try {
					if (filefmxx.exists()) {
						filefmxx.delete();
						filefmxx.createNewFile();
					} else
						filefmxx.createNewFile();
			} catch (Exception e) {
					e.printStackTrace();
					return null;
			}
			
			Charset charset = Charset.forName("GBK") ;
			
			//���ȼ�鼸������ָ���Ƿ�������999999/399001
			CachedRowSetImpl  rssh = null;
			Integer dapanjyrsm =0 ;
			try {
				String sqlquerystat = "SELECT  COUNT(1) AS JIAOYIRISHUMU FROM ͨ���Ű��ÿ�ս�����Ϣ  \r\n" +
						  "	WHERE  ���� = '999999' \r\n" +
						  "	AND �������� BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"  
							;
				logger.debug(sqlquerystat);
				
			    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
			    while(rssh.next()) {
			    	dapanjyrsm = rssh.getInt("JIAOYIRISHUMU");
			    }
			} catch(java.lang.NullPointerException e) {   	e.printStackTrace();
			} catch (SQLException e) { 	e.printStackTrace();
			} catch(Exception e){   	e.printStackTrace();
			} finally {
		    	if(rssh != null)
				try {	rssh.close();	rssh = null;
				} catch (SQLException e) {	e.printStackTrace();}
			}
			try {
				String checkresult = "��֤999999���ܽ���������" + dapanjyrsm + "��"; 
				Files.append(checkresult +  System.getProperty("line.separator"),filefmxx, charset);
			 } catch (IOException e) {e.printStackTrace();	 }

			try {
				String sqlquerystat = "SELECT  COUNT(1) AS JIAOYIRISHUMU FROM ͨ���Ž�����ָ��ÿ�ս�����Ϣ  \r\n" +
						  "	WHERE  ���� = '399001' \r\n" +
						  "	AND �������� BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"  
							;
				logger.debug(sqlquerystat);
				
			    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
			    while(rssh.next()) {
			    	dapanjyrsm = rssh.getInt("JIAOYIRISHUMU");
			    }
			} catch(java.lang.NullPointerException e) { e.printStackTrace();
			} catch (SQLException e) { e.printStackTrace();
			} catch(Exception e){e.printStackTrace();
			} finally {
		    	if(rssh != null)
				try {rssh.close();rssh = null;
				} catch (SQLException e) {e.printStackTrace();}
			}
			try {
				String checkresult = "��֤399001���ܽ���������" + dapanjyrsm + "��"; 
				Files.append(checkresult +  System.getProperty("line.separator"),filefmxx, charset);
			 } catch (IOException e) {e.printStackTrace();}
			//�����ɰ���¼�����ʹ��̼�¼�����Ƿ�һ��
//			List<BanKuai> allbklist = this.getTDXBanKuaiList("sh"); 
			Collection<BkChanYeLianTreeNode> requiredbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK,"SH");
			for( BkChanYeLianTreeNode tmpnode: requiredbk ) {
				BanKuai bk = (BanKuai) tmpnode;
				if(!bk.getBanKuaiOperationSetting().isImportdailytradingdata()) //���������ж���Щ������赼��ÿ������
					continue;
				
				int bkjiaoyirishumu = 0;
				String tmpbkcode = bk.getMyOwnCode();
				try {
					String sqlquerystat = "SELECT  COUNT(1) AS JIAOYIRISHUMU FROM ͨ���Ű��ÿ�ս�����Ϣ  \r\n" +
							  "	WHERE  ���� = '" + tmpbkcode + "' \r\n" +
							  "	AND �������� BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"  
								;
					logger.debug(sqlquerystat);
					
				    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
				    while(rssh.next()) {
				    	bkjiaoyirishumu = rssh.getInt("JIAOYIRISHUMU");
				    }
				} catch(java.lang.NullPointerException e) { e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();
				}catch(Exception e){e.printStackTrace();
				} finally {
			    	if(rssh != null)
					try {rssh.close();rssh = null;
					} catch (SQLException e) {e.printStackTrace();}
				}
				if(bkjiaoyirishumu != dapanjyrsm) {
					try {
						String checkresult = "ע�⣺���" +  tmpbkcode + "����������Ŀ��" + bkjiaoyirishumu + "��,����̽���������Ŀ���ݲ����ϣ�"; 
						Files.append(checkresult +  System.getProperty("line.separator"),filefmxx, charset);
					 } catch (IOException e) {e.printStackTrace();}
				}
				
			}
			Collection<BkChanYeLianTreeNode> requiredszbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getRequiredSubSetOfTheNodesByJiaoYiSuo(BkChanYeLianTreeNode.TDXBK,"SZ"); 
			for(BkChanYeLianTreeNode tmpnode:  requiredszbk ) {
				BanKuai bk = (BanKuai) tmpnode;

				if(!bk.getBanKuaiOperationSetting().isImportdailytradingdata()) //���������ж���Щ������赼��ÿ������
					continue;
				
				int bkjiaoyirishumu = 0;
				String tmpbkcode = bk.getMyOwnCode();
				try {
					String sqlquerystat = "SELECT  COUNT(1) AS JIAOYIRISHUMU FROM ͨ���Ž�����ָ��ÿ�ս�����Ϣ  \r\n" +
							  "	WHERE  ���� = '" + tmpbkcode + "' \r\n" +
							  "	AND �������� BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"  
								;
					logger.debug(sqlquerystat);
					
				    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
				    Integer stockcode =0 ;
				    while(rssh.next()) {
				    	bkjiaoyirishumu = rssh.getInt("JIAOYIRISHUMU");
				    }
				} catch(java.lang.NullPointerException e) { e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();
				}catch(Exception e){e.printStackTrace();
				} finally {
			    	if(rssh != null)
					try {rssh.close();rssh = null;
					} catch (SQLException e) {e.printStackTrace();}
				}
				if(bkjiaoyirishumu != dapanjyrsm) {
					try {
						String checkresult = "ע�⣺���" +  tmpbkcode + "����������Ŀ��" + bkjiaoyirishumu + "��,����̽���������Ŀ���ݲ����ϣ�"; 
						Files.append(checkresult + System.getProperty("line.separator"),filefmxx, charset);
					 } catch (IOException e) {e.printStackTrace();}
				}
			}
			//����͸��������д����׵���������Ƿ�����   
			HashSet<String> stockset = new HashSet<String>(); 
			try { 	
				//�ҳ�ÿ�ս��׵�����յ�
				String sqlquerystat = "SELECT \r\n" +
						  "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����`\r\n" +
						  "FROM \r\n" +
						  "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ \r\n" +   
						  "LEFT JOIN a�� \r\n" +
						  "ON ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����` = a��.��Ʊ����  \r\n" +
						  "WHERE \r\n" +
						  "(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.�ǵ��� is null or      ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.������ is null or      ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.����ֵ is null or      ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.��ͨ��ֵ is null) \r\n" +
						  "and a��.������ is null \r\n" + 
						  "	AND �������� BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"
						  ;
				logger.debug(sqlquerystat);
				
			    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
			    
			    while(rssh.next()) {
			    	String stockcode = rssh.getString("����");
			    	stockset.add(stockcode);
			    }
			} catch(java.lang.NullPointerException e) { 
		    	e.printStackTrace();
			} catch (SQLException e) {
		    	e.printStackTrace();
			}catch(Exception e){
		    	e.printStackTrace();
			} finally {
		    	if(rssh != null)
				try {
					rssh.close();
					rssh = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		   }
			try { 	
				String sqlquerystat = "SELECT \r\n" +
						  "ͨ���������Ʊÿ�ս�����Ϣ.`����`\r\n" +
						  "FROM \r\n" +
						  "ͨ���������Ʊÿ�ս�����Ϣ \r\n" +   
						  "LEFT JOIN a�� \r\n" +
						  "ON ͨ���������Ʊÿ�ս�����Ϣ.`����` = a��.��Ʊ����  \r\n" +
						  "WHERE \r\n" +
						  "(ͨ���������Ʊÿ�ս�����Ϣ.�ǵ��� is null or      ͨ���������Ʊÿ�ս�����Ϣ.������ is null or      ͨ���������Ʊÿ�ս�����Ϣ.����ֵ is null or      ͨ���������Ʊÿ�ս�����Ϣ.��ͨ��ֵ is null) \r\n" +
						  "and a��.������ is null \r\n" + 
						  "	AND �������� BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"
						  ;
				logger.debug(sqlquerystat);
				
			    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
			    
			    while(rssh.next()) {
			    	String stockcode = rssh.getString("����");
			    	stockset.add(stockcode);
			    }
			} catch(java.lang.NullPointerException e) { 
		    	e.printStackTrace();
			} catch (SQLException e) {
		    	e.printStackTrace();
			}catch(Exception e){
		    	e.printStackTrace();
			} finally {
		    	if(rssh != null)
				try {
					rssh.close();
					rssh = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		   }
			for(String stockcode : stockset) {
					try {
						String checkresult = "ע�⣺����" + stockcode + "���������������ݲ�����NULL����"; 
						Files.append(checkresult + System.getProperty("line.separator"),filefmxx, charset);
					 } catch (IOException e) {
							e.printStackTrace();
					 }
			}
			//����͸��������д�TDX����������Ƿ�����   
			stockset.clear(); 
			try { 	
				//�ҳ�ÿ�ս��׵�����յ�
				String sqlquerystat = "SELECT \r\n" +
						  "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����`\r\n" +
						  "FROM \r\n" +
						  "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ \r\n" +   
						  "LEFT JOIN a�� \r\n" +
						  "ON ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����` = a��.��Ʊ����  \r\n" +
						  "WHERE \r\n" +
						  "(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.���̼� is null or ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.��ͼ� is null or ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.��߼� is null or ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.���̼� is null) \r\n" +
						  "and a��.������ is null \r\n" + 
						  "	AND �������� BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"
						  ;
				logger.debug(sqlquerystat);
				
			    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
			    
			    while(rssh.next()) {
			    	String stockcode = rssh.getString("����");
			    	stockset.add(stockcode);
			    }
			} catch(java.lang.NullPointerException e) { 
		    	e.printStackTrace();
			} catch (SQLException e) {
		    	e.printStackTrace();
			}catch(Exception e){
		    	e.printStackTrace();
			} finally {
		    	if(rssh != null)
				try {
					rssh.close();
					rssh = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		   }
			try { 	
				String sqlquerystat = "SELECT \r\n" +
						  "ͨ���������Ʊÿ�ս�����Ϣ.`����`\r\n" +
						  "FROM \r\n" +
						  "ͨ���������Ʊÿ�ս�����Ϣ \r\n" +   
						  "LEFT JOIN a�� \r\n" +
						  "ON ͨ���������Ʊÿ�ս�����Ϣ.`����` = a��.��Ʊ����  \r\n" +
						  "WHERE \r\n" +
						  "(ͨ���������Ʊÿ�ս�����Ϣ.���̼� is null or ͨ���������Ʊÿ�ս�����Ϣ.��ͼ� is null or ͨ���������Ʊÿ�ս�����Ϣ.��߼� is null or ͨ���������Ʊÿ�ս�����Ϣ.���̼� is null) \r\n" +
						  "and a��.������ is null \r\n" + 
						  "	AND �������� BETWEEN '" + monday + "' AND ' " + friday + "' \r\n"
						  ;
				logger.debug(sqlquerystat);
				
			    rssh = connectdb.sqlQueryStatExecute(sqlquerystat);
			    
			    while(rssh.next()) {
			    	String stockcode = rssh.getString("����");
			    	stockset.add(stockcode);
			    }
			} catch(java.lang.NullPointerException e) { 
		    	e.printStackTrace();
			} catch (SQLException e) {
		    	e.printStackTrace();
			}catch(Exception e){
		    	e.printStackTrace();
			} finally {
		    	if(rssh != null)
				try {
					rssh.close();
					rssh = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		   }
			for(String stockcode : stockset) {
				try {
					String checkresult = "ע�⣺����" + stockcode + "��������ͨ�������ݲ�����NULL����"; 
					Files.append(checkresult + System.getProperty("line.separator"),filefmxx, charset);
				 } catch (IOException e) {
						e.printStackTrace();
				 }
		}
		
			
//		���ڴ����д��󣬵��°��͸��ɵĶ�Ӧ��ϵ�п��ܳ����ظ��� 
		checkImportTDXDataDuplicateError(filefmxx);
		
		return 	filefmxx;
	}

	
		/*
		 * ���ܴ��̹��м���������
		 */
		public int getTradingDaysOfTheWeek(LocalDate curselectdate) 
		{
			LocalDate monday = curselectdate.with(DayOfWeek.MONDAY);
			LocalDate friday = curselectdate.with(DayOfWeek.FRIDAY);
			
			boolean hasbkcode = true;
            CachedRowSetImpl rspd = null; 
            int result = 0;
			try {
				   String sqlquerystat = "select count(*) as result from ͨ���Ű��ÿ�ս�����Ϣ " + 
							"where ���� = '999999' " +
							"and �������� >= '" + monday + "'" + 
							"and �������� <= '" + friday + "'"
							;
	
			    	logger.debug(sqlquerystat);
			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			    	
			        while(rspd.next())  {
			        	result = rspd.getInt("result");
			        }
			       
			} catch(java.lang.NullPointerException e){ 
			    	e.printStackTrace();
			} catch (SQLException e) {
			    	e.printStackTrace();
			} catch(Exception e){
			    	e.printStackTrace();
			} finally {
			    	if(rspd != null)
						try {
							rspd.close();
							rspd = null;
						} catch (SQLException e) {
							e.printStackTrace();
						}
			}
			
			return result;
		}
		
		
		/*
		 * 
		 */
		public String getBanKuaiOrStockZdgzInfo(String nodecode, LocalDate date) 
		{
			LocalDate monday = date.with(DayOfWeek.MONDAY);
			LocalDate saterday = date.with(DayOfWeek.SUNDAY);
			
			CachedRowSetImpl rspd = null; 
            String result = "";
            
			try {
				String sqlquerystat = "SELECT * FROM ������¼�ص��ע " +
						  " WHERE ��Ʊ����= '" + nodecode + "'" + 
						  " AND ����  BETWEEN '" + monday + "' AND '" + saterday + "'" +
						  " AND (�����Ƴ���־ = '�����ע' OR �����Ƴ���־ = '�Ƴ��ص�' OR �����Ƴ���־ = '�������' "
						  + " OR �����Ƴ���־ = '�ص��ע' OR �����Ƴ���־ = '�ȵ��� ' OR  �����Ƴ���־ = '�����ص�' OR  �����Ƴ���־ = '�Ƴ���ע')    "
						  ;
	
			    	logger.debug(sqlquerystat);
			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			    	int surviveid = 0 ; int recordsnum = 0;
			        while(rspd.next())  {
			        	result = result + rspd.getString("ԭ������");
			        	surviveid = rspd.getInt("id");
			        	
			        	recordsnum ++;
			        	
			        	if(recordsnum >1) { //��ȥ�������������Ҫȫ��������ÿ��ֻ��һ����¼
				        	int deletedid = rspd.getInt("id");
				        	result = result + rspd.getDate("����") + rspd.getString("�����Ƴ���־") + rspd.getString("ԭ������");
			        		String sqldelete = "DELETE FROM ������¼�ص��ע WHERE ID = " + deletedid ;
			        		connectdb.sqlDeleteStatExecute(sqldelete);
				        }
			        }
			        
			        if(recordsnum >1) { 
			        	String sqlupdate = "UPDATE  ������¼�ص��ע SET ԭ������= '" + result + "' WHERE ID = " + surviveid ;
		        		connectdb.sqlUpdateStatExecute(sqlupdate);
			        }
			} catch(java.lang.NullPointerException e){ 
			    	e.printStackTrace();
			} catch (SQLException e) {
			    	e.printStackTrace();
			} catch(Exception e){
			    	e.printStackTrace();
			} finally {
			    	if(rspd != null)
						try {
							rspd.close();
							rspd = null;
						} catch (SQLException e) {
							e.printStackTrace();
						}
			}
			
			if(Strings.isNullOrEmpty(result))
				return null;
			else
				return result.trim();
		}
		/*
		 * 
		 */
		public void updateBanKuaiOrStockZdgzInfo(String nodecode, LocalDate currentdate, String xmlcontents) 
		{
			LocalDate monday = currentdate.with(DayOfWeek.MONDAY);
			LocalDate saterday = currentdate.with(DayOfWeek.SUNDAY);
			
			CachedRowSetImpl rspd = null; 
            int updateid = -1;
            
			try {
				String sqlquerystat = "SELECT * FROM ������¼�ص��ע " +
						  " WHERE ��Ʊ����= '" + nodecode + "'" + 
						  " AND ����  BETWEEN '" + monday + "' AND '" + saterday + "'" +
						  " AND (�����Ƴ���־ = '�����ע' OR �����Ƴ���־ = '�Ƴ��ص�' OR �����Ƴ���־ = '�������' "
						  + " OR �����Ƴ���־ = '�ص��ע' OR �����Ƴ���־ = '�ȵ��� ' OR  �����Ƴ���־ = '�����ص�' OR  �����Ƴ���־ = '�Ƴ���ע')    " 
						  ;
	
			    	logger.debug(sqlquerystat);
			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			        while(rspd.next())  {
			        	updateid =  rspd.getInt("id");
			        }
			        
			        if(-1 == updateid ) { //˵���������ݿ���û����ص�XML��¼
			        	String sqlinsert = "INSERT INTO  ������¼�ص��ע(��Ʊ����,����,�����Ƴ���־,ԭ������) values ("
		   						+ "'" + nodecode.trim() + "'" + ","
		   						+ "'" + currentdate + "'"  + ","
		   						+ "'�������'"  + ","
		   						+ "'" + xmlcontents + "'" 
		   						+ ")"
		   						;
			        	connectdb.sqlInsertStatExecute(sqlinsert);
			        } else {
			        	String sqlupdate = "UPDATE  ������¼�ص��ע SET ԭ������= '" + xmlcontents + "',"
			        						+ "�����Ƴ���־ = '�������' "
			        						+ " WHERE ID = " + updateid ;
		        		connectdb.sqlUpdateStatExecute(sqlupdate);
			        }
			        
			} catch(java.lang.NullPointerException e){ 
			    	e.printStackTrace();
			} catch (SQLException e) {
			    	e.printStackTrace();
			} catch(Exception e){
			    	e.printStackTrace();
			} finally {
			    	if(rspd != null)
						try {
							rspd.close();
							rspd = null;
						} catch (SQLException e) {
							e.printStackTrace();
						}
			}
	
			
		}
		/*
		 * 
		 */
		public int setMingRiJiHua(BkChanYeLianTreeNode nodecode,LocalDate actiondate, String mrjhaction,Double mrjhprice,String shuoming) 
		{
			String stockcode = nodecode.getMyOwnCode();		
			String zdgzsign = "���ռƻ�";
			String mrjhshuoming = mrjhaction + "(�۸�" + mrjhprice + ")(" +  shuoming + ")";

			String sqlinsertstat = "INSERT INTO ������¼�ص��ע(��Ʊ����,����,�����Ƴ���־,ԭ������) values ("
					+ "'" +  stockcode.trim() + "'" + "," 
					+ "'" + actiondate + "'" + ","
					+  "'" + zdgzsign + "'" + ","
					+ "'" + mrjhshuoming + "'"  
					+ ")"
					;
			logger.debug(sqlinsertstat);
			int autoIncKeyFromApi = 0;
			try {
				autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
			} catch (MysqlDataTruncation e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return autoIncKeyFromApi;
		}
		/*
		 * TDX�����ļ�
		 */
		public File refreshTDXDrawingFile() 
		{
//			File file = new File(sysconfig.getDZHStockGuQuanFile() );
			File file = new File("E:/tdxline.eld");
			 if(!file.exists() ) {
				 return null;
			 }
			 
			 BufferedInputStream dis = null;
			 FileInputStream in = null;
			 try {
				    in = new FileInputStream(file);  
				    dis = new BufferedInputStream(in);
				    
				    
				    int fileheadbytenumber = 1;
		            byte[] jiaoyisuocode = new byte[fileheadbytenumber];
		            dis.read(jiaoyisuocode, 0, fileheadbytenumber); 
		              
		            int nodecodebytenumber = 6;
		            byte[] nodecode = new byte[nodecodebytenumber];
		            dis.read(nodecode, 0, nodecodebytenumber);
		            String nodecodestr =new String(nodecode,0,nodecodebytenumber);
		            
		            int zhongjiantempbytenumber = 17;
		            byte[] zhongjian = new byte[zhongjiantempbytenumber];
		            dis.read(zhongjian, 0, zhongjiantempbytenumber);
		            
		            dis.read(jiaoyisuocode, 0, fileheadbytenumber);
		            
		            int zhibiaonumber = 6;
		            byte[] zhibiaobyte = new byte[zhibiaonumber];
		            dis.read(zhibiaobyte, 0, zhibiaonumber);
		            String zhibiaoname =new String(zhibiaobyte,0,zhibiaonumber);
		            
		            int zhongjian2number = 68;
		            byte[] zhongjian2byte = new byte[zhongjian2number];
		            dis.read(zhongjian2byte, 0, zhongjian2number);
		            
				    
			 } catch (Exception e) {
				 e.printStackTrace();
				 return null;
			 } finally {
				 try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	             try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}     
			 }
			return file;
		}
		/*
		 * 
		 */
		public Set<BkChanYeLianTreeNode> sysnRecentGuanZhuGeGu(String zdybkname, LocalDate searchstartdate,LocalDate searchenddate) 
		{
			BanKuaiAndStockTree treeallstocks = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();

			Set<BkChanYeLianTreeNode> namelist = new HashSet<> ();
			CachedRowSetImpl rspd = null;
			try {
				String sqlquerystat = "SELECT * FROM ��Ʊͨ�����Զ������Ӧ�� " +
						  " WHERE �Զ�����= '" + zdybkname + "'" + 
						  " AND ����ʱ��  BETWEEN '" + searchstartdate + "' AND '" + searchenddate + "'"  
						  ;
	
			    	logger.debug(sqlquerystat);
			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			        while(rspd.next())  {
			        	String stockcode=  rspd.getString("��Ʊ����");
			        	
			        	Stock tmpstock = (Stock)treeallstocks.getSpecificNodeByHypyOrCode (stockcode,BkChanYeLianTreeNode.TDXGG);
			        	
			        	LocalDate joindate = rspd.getDate("����ʱ��").toLocalDate();
			        	LocalDate removedate;
			        	try { removedate = rspd.getDate("�Ƴ�ʱ��").toLocalDate();
			        	} catch (java.lang.NullPointerException e) { removedate = LocalDate.parse("3000-01-01"); 	}
			        	tmpstock.addNewDuanQiGuanZhuRange (joindate,removedate);
			        	namelist.add(tmpstock);
			        }
			} catch(java.lang.NullPointerException e){	e.printStackTrace();
			} catch (SQLException e) {	e.printStackTrace();
			} catch(Exception e){	e.printStackTrace();
			} finally {
			    	if(rspd != null)
						try {	rspd.close();		rspd = null;} catch (SQLException e) {	e.printStackTrace();}
			}
			
			return namelist;
		}
		
		private List<QueKou> checkQueKouForAGivenPeriod (TDXNodes childnode,LocalDate startdate, LocalDate enddate, List<QueKou> qklist, String period )
		{
			if(  ((Stock)childnode).isVeryVeryNewXinStock(startdate)  )	return null;
			
			if(qklist == null)		qklist = new ArrayList<QueKou> ();
			
			NodeXPeriodData nodexdata = childnode.getNodeXPeroidData(period);
			OHLCSeries nodeohlc = ((TDXNodesXPeriodDataForJFC)nodexdata).getOHLCData();
			List<QueKou> newquekou = new ArrayList<> ();
			for(int j=1;j < nodeohlc.getItemCount();j++) {
				
				OHLCItem kxiandatacurwk = (OHLCItem) nodeohlc.getDataItem(j);
				RegularTimePeriod curperiod = kxiandatacurwk.getPeriod();
				LocalDate curstart = curperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate curend = curperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//				if(curend.equals(LocalDate.parse("2019-12-27"))) {
//					String tempstr = "";
//				}
				
				Double curhigh = kxiandatacurwk.getHighValue();
				Double curlow = kxiandatacurwk.getLowValue();
				Double curclose = kxiandatacurwk.getCloseValue();
				
				if(curstart.isBefore(startdate) || curstart.isAfter(enddate)) { //���ڲ���Ҫ���鷶Χ�ڵ��������ݣ�ֻ������Ƿ񲹵�ǰȱ����
					
					Iterator itr = qklist.iterator(); 
					while (itr.hasNext()) 
					{ 
						QueKou tmpqk = (QueKou)itr.next();
						LocalDate tmpqkdate = tmpqk.getQueKouDate();
						if(tmpqkdate.isAfter(curstart))
							continue;
						
						if (!tmpqk.hasQueKouHuiBu() ) {
							String huibuinfo = tmpqk.checkQueKouHuiBu(curstart,curlow, curhigh);
						}
					}
				} else { //��Ҫ����ҷ�Χ�ڵģ����˲����Ƿ�ȱ�ڣ���Ҫ���Ƿ������ȱ��

					Iterator itr = qklist.iterator(); 
					while (itr.hasNext()) 
					{ 
						QueKou tmpqk = (QueKou)itr.next();
						LocalDate tmpqkdate = tmpqk.getQueKouDate();
						if(tmpqkdate.isAfter(curstart))
							continue;
						
						if (!tmpqk.hasQueKouHuiBu() ) {
							String huibuinfo = tmpqk.checkQueKouHuiBu(curstart,curlow, curhigh);
						}
					} 
					
					//������û�в����µ�ȱ��
					OHLCItem kxiandatalastwk = (OHLCItem) nodeohlc.getDataItem(j-1);
					double lasthigh = kxiandatalastwk.getHighValue();
					double lastlow = kxiandatalastwk.getLowValue();
					
					Boolean tiaokongup = false; Boolean tiaokongdown = false;
					if(curlow > lasthigh) {
						Double newqkup = curlow;
						Double newqkdown = lasthigh;
						QueKou newqk = new QueKou (childnode.getMyOwnCode(),curstart,newqkdown,newqkup,true);
						newqk.setShouPanJia(curclose);
						
						qklist.add(0, newqk);
					}
					else 
					if(curhigh < lastlow) {
						Double newqkup = lastlow;
						Double newqkdown = curhigh;
						QueKou newqk = new QueKou (childnode.getMyOwnCode(),curstart,newqkdown,newqkup,false);
						newqk.setShouPanJia(curclose);
						
						qklist.add(0, newqk);
					}
				}

			}
			
			return qklist;
		}
		/*
		 * 
		 */
		public Boolean updateNodeQueKouInfoToDb (List<QueKou> qklist )
		{
			if(qklist == null)
				return null;
			
			for(QueKou tmpqk : qklist) {
				
        		if(!tmpqk.shouldUpdatedToDb())
        			continue;
        		
					String nodecode = tmpqk.getNodeCode ();
					Double qkup = tmpqk.getQueKouUp();  
		        	Double qkdown =  tmpqk.getQueKouDown();
		        	Boolean type = tmpqk.getQueKouLeiXing();
		        	LocalDate qkdate = tmpqk.getQueKouDate();
		        	LocalDate huidate = tmpqk.getQueKouHuiBuDate();
		        	String sqlhuibupart ;
		        	Double shoupanjia = tmpqk.getShouPanJia();
		        	
		        	try {
		        		String sqlinsertstat;
		        		if(huidate == null) {
		        			 sqlinsertstat = "INSERT INTO ȱ��ͳ�Ʊ�(��Ʊ����,��������,ȱ������,ȱ������,ȱ������,�ز�����,�������̼�) VALUES ("
			   						+ "'" + nodecode.trim() + "'" + ","
			        				+ "'" + qkdate + "'," 
			   						+ qkup + ","
			   						+ qkdown + ","
			   						+ type + ","
			   						+ null +  ","
			   						+ shoupanjia + ")"
			   						+ " ON DUPLICATE KEY UPDATE "
			   						+ " �ز����� =" + null + "," 
			   						+ " ȱ������ =" + qkup + ","
			   						+ " ȱ������ =" + qkdown  + ","
			   						+ " �������̼� = " + shoupanjia + ","
			        				+ " ȱ������ = " + type
			   						;
		        		} else {
		        			 sqlinsertstat = "INSERT INTO ȱ��ͳ�Ʊ�(��Ʊ����,��������,ȱ������,ȱ������,ȱ������,�ز�����,�������̼�) VALUES ("
			   						+ "'" + nodecode.trim() + "'" + ","
			        				+ "'" + qkdate + "'," 
			   						+ qkup + ","
			   						+ qkdown + ","
			   						+ type + ","
			   						+ "'" + huidate  + "'," 
			   						+ shoupanjia + ")"
			   						+ " ON DUPLICATE KEY UPDATE "
			   						+ " �ز����� =" + " '" + huidate + "'," 
			   						+ " ȱ������ =" + qkup + ","
			   						+ " ȱ������ =" + qkdown  + ","
			   						+ " �������̼� = " + shoupanjia + ","
			        				+ " ȱ������ = " + type
			   						;
		        			
		        		}
		        		
								
		   				logger.debug(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   					        		
					} catch(java.lang.NullPointerException e){ 
					    	e.printStackTrace();
					} catch(Exception e){
					    	e.printStackTrace();
					} finally {
					}

				
				
			}
			
			return true;
		}
		public LocalDate getLatestTDXNodeQueKouDate() 
		{
			Set<String> namelist = new HashSet<String> ();
			CachedRowSetImpl rspd = null;
			try {
				String sqlquerystat = "SELECT  GREATEST (a.maxqk, a.maxhb ) AS MAXDATE "
						+ "FROM ( SELECT MAX(��������) AS maxqk, MAX(�ز�����) AS maxhb	FROM ȱ��ͳ�Ʊ�	"
						+ ") a"
						;
			    	logger.debug(sqlquerystat);
			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			    	java.sql.Date maxdate = null;
			        while(rspd.next())  {
			        	 maxdate =  rspd.getDate("MAXDATE");
			        }
			        
			        return maxdate.toLocalDate();
			        
			} catch(java.lang.NullPointerException e){ e.printStackTrace();
			} catch (SQLException e) {e.printStackTrace();
			} catch(Exception e){e.printStackTrace();
			} finally {
			    	if(rspd != null)
						try {rspd.close();rspd = null;
						} catch (SQLException e) {e.printStackTrace();}
			}
			
			return null;
		}
		/*
		 *�Ӹ�����������ֱ�Ӽ����ȱ����Ϣ 
		 */
		public Stock getStockDailyQueKouAnalysisResult (Stock stock,LocalDate requiredstartday,LocalDate requiredendday, String period)
		{
			if( ((Stock)stock).isVeryVeryNewXinStock(requiredstartday) ) //�¹ɵ�ȱ�ڲ�����
				return stock;
			
			NodeXPeriodData stockdailyxdate = stock.getNodeXPeroidData(period);
			List<QueKou> qklist = stockdailyxdate.getPeriodQueKou();
			
			qklist = checkQueKouForAGivenPeriod ( stock, requiredstartday, requiredendday,qklist,NodeGivenPeriodDataItem.DAY );
			try {	Collections.sort(qklist, new NodeLocalDateComparator() );
			} catch ( java.lang.IllegalArgumentException e) {e.printStackTrace();}
			stockdailyxdate.setPeriodQueKou(qklist);
			
			 return stock;
		}
		
		/*
		 * 
		 */
		public Boolean updateNodeSocialFriendShips(BanKuai mainnode, BanKuai friend, Boolean relationship)
		{
			Set<String>	friendsetpostive = mainnode.getSocialFriendsSetPostive();
			Set<String>	friendsetnegtive = mainnode.getSocialFriendsSetNegtive();
			
			String friendcode = friend.getMyOwnCode();
			Boolean alreadyinpostive = false;
			if( friendsetpostive.contains(friendcode ) )
				alreadyinpostive = true;
			Boolean alreadyinnegtive = false;
			if( friendsetnegtive.contains(friendcode ) )
				alreadyinnegtive = true;
				
			if( (alreadyinpostive && relationship) || (alreadyinnegtive && !relationship) ) { //�Ѿ���������Ҫȡ��
				String sqlupdatestat = "DELETE FROM ͨ���Ű���罻��ϵ��  "
						+ " WHERE ( "
						+ "( '�����' = '" + mainnode.getMyOwnCode() + "'"
						+ " AND '�����' = '" + friend.getMyOwnCode() + "') " 
						+ " OR "
						+ "( '�����' = '" + mainnode.getMyOwnCode() + "'"
						+ " AND '�����' = '" + friend.getMyOwnCode() + "') ) "
						;
				try {
					int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
				} catch(java.lang.NullPointerException e){ e.printStackTrace();
				} catch(Exception e){e.printStackTrace();
				} finally {}
				
				if(alreadyinpostive)	friendsetpostive.remove(friendcode );
				if(alreadyinnegtive)	friendsetnegtive.remove(friendcode );
			} else 
			if( (alreadyinpostive && !relationship) || (alreadyinnegtive && relationship) ) { //�Ѿ�������,Ҫȡ��
				String sqlupdatestat = "UPDATE  ͨ���Ű���罻��ϵ��  SET ��ϵ= " + relationship
						+ " WHERE ( "
						+ "( '�����' = '" + mainnode.getMyOwnCode() + "'"
						+ " AND '�����' = '" + friend.getMyOwnCode() + "') " 
						+ " OR "
						+ "( '�����' = '" + mainnode.getMyOwnCode() + "'"
						+ " AND '�����' = '" + friend.getMyOwnCode() + "') ) "
						;
				try {
					int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
				} catch(java.lang.NullPointerException e){e.printStackTrace();
				} catch(Exception e){e.printStackTrace();
				} finally {}
				if(alreadyinpostive) {
					friendsetpostive.remove(friendcode );
					friendsetnegtive.add(friendcode );
				}
				if(alreadyinnegtive){
					friendsetpostive.add(friendcode );
					friendsetnegtive.remove(friendcode );
				}
			}
			else { //�������ѣ�Ҫ����
				String sqlinsertstat =  "INSERT INTO  ͨ���Ű���罻��ϵ��(�����,�����,��ϵ) values ("
   						+ " '" + mainnode.getMyOwnCode() + "'" + ","
   						+ " '" + friend.getMyOwnCode() + "'"  + ","
   						+ relationship
   						+ ")"
   						;
				try {
					int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlinsertstat);

				} catch(java.lang.NullPointerException e){e.printStackTrace();
				} catch(Exception e){e.printStackTrace();
				} finally {}
				
				if(relationship) friendsetpostive.add(friendcode );
				else	friendsetnegtive.add(friendcode );
			}
			
			return null;
		}
		/*
		 * 
		 */
		public BkChanYeLianTreeNode getNodeCjeCjlExtremeZhanbiUpDownLevel(TDXNodes node) 
		{
			String sqlquerystat = "SELECT  * FROM ����Ʊռ����ֵ"
					+ " WHERE ���� = '" + node.getMyOwnCode() + "'"
					;
			CachedRowSetImpl	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
			try {
			        while(rspd.next())  {
			        	Double cjezbmax =  rspd.getDouble("�ɽ���ռ������");
			        	Double cjezbmin =  rspd.getDouble("�ɽ���ռ������");
			        	
			        	Double cjlzbmax =  rspd.getDouble("�ɽ���ռ������");
			        	Double cjlzbmin =  rspd.getDouble("�ɽ���ռ������");
			        	
			        	node.getNodeJiBenMian().setNodeCjlZhanbiLevel(cjlzbmin, cjlzbmax);
			        	node.getNodeJiBenMian().setNodeCjeZhanbiLevel(cjezbmin, cjezbmax);
			        }
			} catch(java.lang.NullPointerException e){ e.printStackTrace();
			} catch (SQLException e) {e.printStackTrace();
			} catch(Exception e){e.printStackTrace();
			} finally {
			    	if(rspd != null)
						try {rspd.close();rspd = null;
						} catch (SQLException e) {e.printStackTrace();}
			}
			
			return node;
		}
		/*
		 * 
		 */
		public BkChanYeLianTreeNode setNodeCjlExtremeZhanbiUpDownLevel(TDXNodes node, Double min, Double max) 
		{
				String sqlinsertstat = "INSERT INTO ����Ʊռ����ֵ(����,�ɽ���ռ������,�ɽ���ռ������) VALUES ("
						+ "'" + node.getMyOwnCode().trim() + "'" + ","
        				+ min + "," 
   						+ max
   						+ ")"
   						+ " ON DUPLICATE KEY UPDATE "
   						+ " �ɽ���ռ������ =" + min + "," 
   						+ " �ɽ���ռ������ =" + max 
   						;
				try {
					int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
				} catch (MysqlDataTruncation e) {e.printStackTrace();
				} catch (SQLException e) {e.printStackTrace();} 
			
			return node;
		}
				/*
		 * 
		 */
		public BkChanYeLianTreeNode setNodeCjeExtremeZhanbiUpDownLevel(TDXNodes node, Double min, Double max) 
		{
			int nodetype = node.getType();
			String sqlinsertstat = "INSERT INTO ����Ʊռ����ֵ(����,node����,�ɽ���ռ������,�ɽ���ռ������) VALUES ("
					+ "'" + node.getMyOwnCode().trim() + "'" + ","
					+ nodetype + ","
    				+ min + "," 
						+ max
						+ ")"
						+ " ON DUPLICATE KEY UPDATE "
						+ " �ɽ���ռ������ =" + min + "," 
						+ " �ɽ���ռ������ =" + max + ","
						+ " node���� = " + nodetype
						;
			try {
				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
			} catch (MysqlDataTruncation e) {e.printStackTrace();
			} catch (SQLException e) {e.printStackTrace();} 
		
			return node;
		}
		/*
		 * 
		 */
		public Collection<BkChanYeLianTreeNode> searchKeyWordsInStockJiBenMian(String searchstring) 
		{
			if(searchstring.trim().isEmpty())
				return null	;
			
			Collection<BkChanYeLianTreeNode> nodeset = new ArrayList <> ();
			
			String searchstringformated = "'%" + searchstring.trim()  + "%'";
			String searchsqlstat = "SELECT * FROM A��  WHERE ���������� LIKE " + searchstringformated + " OR ������Ϣ LIKE " + searchstringformated + " " + "ORDER BY ��Ʊ����" ; 
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
		public Collection<BkChanYeLianTreeNode> searchKeyWordsInBanKuaiJiBenMian(String searchstring) 
		{
			if(searchstring.trim().isEmpty())
				return null	;
			
			Collection<BkChanYeLianTreeNode> nodeset = new ArrayList <> ();
			
			String searchstringformated = "'*" + searchstring  + "*'";
			String searchsqlstat = "SELECT * FROM ͨ���Ű���б�  WHERE ���������� LIKE " + searchstringformated + " OR ������Ϣ LIKE " + searchstringformated + " " + "ORDER BY ���ID" ; 
			CachedRowSetImpl  rs = null;
			try {
				rs = connectdb.sqlQueryStatExecute(searchsqlstat);
				while(rs.next() ) {
					String stockcode = rs.getString("��Ʊ����");
					BkChanYeLianTreeNode node = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(stockcode, BkChanYeLianTreeNode.TDXBK);
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
		public void forcedeleteBanKuaiImportedDailyExchangeData(BanKuai bk) 
		{
			String searchtable = null;
			if(BkChanYeLianTreeNode.isBanKuai(bk)) 
				searchtable = this.getBanKuaiJiaoYiLiangBiaoName(bk);
			try {
				String sqlquerystat = "DELETE FROM " + searchtable + " WHERE ���� = '" + bk.getMyOwnCode() + "'";
				connectdb.sqlDeleteStatExecute(sqlquerystat);
			} catch(java.lang.NullPointerException e) {
		    } catch(Exception e){e.printStackTrace();
		    } finally {  }
		}
		/*
		 * 
		 */
		public void forcedeleteBanKuaiImportedGeGuData(BanKuai bk) 
		{
			try {
				String searchtable = bk.getShuJuJiLuInfo().getGuPiaoBanKuaiDuiYingBiao();
				if(searchtable == null) return;
				String sqlquerystat = "DELETE FROM " + searchtable + " WHERE ������ = '" + bk.getMyOwnCode() + "'";
				connectdb.sqlQueryStatExecute(sqlquerystat);
			} catch(java.lang.NullPointerException e) {
		    } catch(Exception e){e.printStackTrace();
		    } finally {  }
		}
		/*
		 * 
		 */
		public void saveBanKuaiExtraDataToDatabase(BanKuai bk, LocalDate date, String extradatakeywords[] ) 
		{
			NodeXPeriodData nodexdatawk = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
			NodeXPeriodData nodexdataday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
			
			String setString = "";
			for(String keyword : extradatakeywords ) {
				Object value = nodexdataday.getNodeDataByKeyWord(keyword,date,"");
				if(value == null) break;
				if( (Double)value == 100.0) 
					continue;
				
				switch(keyword) {
				case "CjeZbGrowRate":	setString = setString + " DPCJEZB������  = " + ((Double)value).toString() + ",";
				break;
				case "CjlZbGrowRate":	setString = setString + " DPCJLZB������  = " + ((Double)value).toString() + ",";
				break;
				}
			}
			try {
				setString = setString.trim().substring(0, setString.length() - 2);
				setString = " SET " + setString ;
			} catch ( java.lang.StringIndexOutOfBoundsException e) {
				e.printStackTrace(); return;
			}
			
			String bkcys = bk.getNodeJiBenMian().getSuoShuJiaoYiSuo();
			if(bkcys == null)	return ;
			
			String bkcjltable = null;
			if(bkcys.equalsIgnoreCase("sh"))
				bkcjltable = "ͨ���Ű��ÿ�ս�����Ϣ";
			else if(bkcys.equalsIgnoreCase("sz"))
				bkcjltable = "ͨ���Ž�����ָ��ÿ�ս�����Ϣ";
			else if(bkcys.equalsIgnoreCase("BK"))	
				bkcjltable = "���ǻ۰��ÿ�ս�����Ϣ";
			
			String sqlupdatestat = "UPDATE " + bkcjltable  +"\r\n"
					+ setString + "\r\n"  
					+ "  WHERE " + bkcjltable + ".`����` = " + "'" + bk.getMyOwnCode() + "' \r\n"
					+ "  AND " + bkcjltable + ".�������� = '" +  date.toString()  + "'"
					;
			try {
				connectdb.sqlUpdateStatExecute(sqlupdatestat);
			} catch(java.lang.NullPointerException e) {
		    } catch(Exception e){e.printStackTrace(); System.out.print(sqlupdatestat + "\r\n");
		    } finally {  }
			return;
		}

}


class NodeLocalDateComparator implements Comparator<QueKou> 
{
    public int compare(QueKou qk1, QueKou qk2) 
    {
    	LocalDate qk1date = qk1.getQueKouDate();
    	LocalDate qk2date = qk2.getQueKouDate();
       
        try{
        	if(qk1date.isBefore(qk2date))	return -1;
        	else return 1;
        } catch (java.lang.NullPointerException e) { return -1;}
    	  catch (java.lang.Exception e) { return -1;}
    }
}