package com.exchangeinfomanager.database;

import java.awt.Container;
import java.awt.EventQueue;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.tree.TreeNode;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.RegularTimePeriod;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai;
import com.exchangeinfomanager.bankuaichanyelian.BkChanYeLianTree;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.sun.rowset.CachedRowSetImpl;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;


public class BanKuaiDbOperation 
{
	public BanKuaiDbOperation() 
	{
		initializeDb ();
		initialzieSysconf ();
	}
	
	private  ConnectDataBase connectdb;
	private  SystemConfigration sysconfig;
	private static Logger logger = Logger.getLogger(BanKuaiDbOperation.class);

	private void initializeDb() 
	{
		connectdb = ConnectDataBase.getInstance();
	}
	private void initialzieSysconf ()
	{
		sysconfig = SystemConfigration.getInstance();
	}
	/*
	 * 
	 */
	public Stock getStockBasicInfo(Stock stockbasicinfo) 
	{
		String stockcode = stockbasicinfo.getMyOwnCode();
		CachedRowSetImpl rsagu = null;
		try {
			 String sqlquerystat= "SELECT * FROM A��   WHERE ��Ʊ���� =" +"'" + stockcode +"'" ;	
			rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);
			while(rsagu.next())
				setSingleNodeInfo (stockbasicinfo,rsagu);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rsagu.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rsagu = null;
		}
		
		return stockbasicinfo;
	}
	/*
	 * 
	 */
	public BanKuai getBanKuaiBasicInfo(BanKuai bkbasicinfo)
	{
		String bkcode = bkbasicinfo.getMyOwnCode();
		CachedRowSetImpl rsagu = null;
		try {
			 String sqlquerystat= "select �������, ����ʱ��,����������,������Ϣʱ��,������Ϣ,ȯ������ʱ��,ȯ����������,����ؼ��ͻ�,����ؼ���������,�ͻ�,�������� from ͨ���Ű���б�  where ���ID = '" + bkcode +"' \r\n"  
//			 		"union\r\n" + 
//			 		"select �������,����ʱ��,����������,������Ϣʱ��,������Ϣ,ȯ������ʱ��,ȯ����������,����ؼ��ͻ�,����ؼ���������,�ͻ�,�������� from ͨ���Ž�����ָ���б�  where ���ID = '" + bkcode + "' " 
					 			;
			 logger.debug(sqlquerystat);
			rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);
			while(rsagu.next())
				setSingleNodeInfo (bkbasicinfo,rsagu);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rsagu.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rsagu = null;
		}
		
		return bkbasicinfo;

	}
	/*
	 * ��ȡ�û������node�Ļ�����Ϣ��node�����ǰ��Ҳ�����Ǹ���
	 */
	public ArrayList<BkChanYeLianTreeNode> getNodesBasicInfo(String nodecode) 
	{
		ArrayList<BkChanYeLianTreeNode> nodenamelist = new ArrayList<BkChanYeLianTreeNode> ();
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
		        	String stockname = rs.getString("��Ʊ����");
		        	BkChanYeLianTreeNode tmpnode = null;
		        	
		        	if(rs.getString("type").equals("gegu")) {
		        		tmpnode = new Stock(nodecode,stockname);
		        		
		        		sqlquerystat= "SELECT * FROM A��   WHERE ��Ʊ���� =" +"'" + nodecode +"'" ;
		        		CachedRowSetImpl rsagu = null;
		    			rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);
		    			
		    			while(rsagu.next())
		    				setSingleNodeInfo(tmpnode,rsagu);
		    			
		    			rsagu.close();
		    			rsagu = null;
		        	}
		        	else {
		        		tmpnode = new BanKuai(nodecode,stockname);
		        		String searchtable = rs.getString("tablename");
		        		
		        		sqlquerystat= "SELECT * FROM " +  searchtable  + "  WHERE ���ID =" +"'" + nodecode +"'" ;
		        		CachedRowSetImpl rsagu = null;
		    			rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);
		    			
		    			while(rsagu.next())
		    				setSingleNodeInfo(tmpnode,rsagu);
		    			
		    			rsagu.close();
		    			rsagu = null;
		        	}
		        	
		        	nodenamelist.add(tmpnode);
		        }
		       
		    }catch(java.lang.NullPointerException e){ 
		    	e.printStackTrace();
		    } catch (SQLException e) {
		    	e.printStackTrace();
		    }catch(Exception e){
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
		return nodenamelist;
	}


	
	private void setSingleNodeInfo(BkChanYeLianTreeNode tmpnode, CachedRowSetImpl rs) 
	{
		if(tmpnode.getType() == 6) {
			try {
				String stockname = rs.getString("��Ʊ����").trim();
				tmpnode.setMyOwnName(stockname);
			} catch(java.lang.NullPointerException ex1) {
				tmpnode.setMyOwnName( " ");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				String bankuainame = rs.getString("�������").trim();
				tmpnode.setMyOwnName(bankuainame);
			} catch(java.lang.NullPointerException ex1) {
				tmpnode.setMyOwnName( " ");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		BkChanYeLianTreeNode.NodeJiBenMian nodejbm = tmpnode.getNodeJiBenMian();
		
		try {
			java.util.Date gainiantishidate = rs.getDate("����ʱ��");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(gainiantishidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			nodejbm.setGainiantishidate(ldgainiantishidate); 
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setGainiantishidate(null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		try {
			String gainiantishi = rs.getString("����������");
			nodejbm.setGainiantishi(gainiantishi);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setGainiantishi(" ");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try 
		{
			Date quanshangpingjidate = rs.getDate("ȯ������ʱ��");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(quanshangpingjidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			nodejbm.setQuanshangpingjidate(ldgainiantishidate);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setQuanshangpingjidate(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try	{
			String quanshangpingji = rs.getString("ȯ����������").trim();
			nodejbm.setQuanshangpingji(quanshangpingji);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setQuanshangpingji("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try	{
			Date fumianxiaoxidate = rs.getDate("������Ϣʱ��");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(fumianxiaoxidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			nodejbm.setFumianxiaoxidate(ldgainiantishidate);
		} catch(java.lang.NullPointerException ex1) { 
			nodejbm.setFumianxiaoxidate(null);
		} catch(Exception ex2) {
			ex2.printStackTrace();				
		}

		try	{
			String fumianxiaoxi = rs.getString("������Ϣ").trim();
			nodejbm.setFumianxiaoxi(fumianxiaoxi);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setFumianxiaoxi("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			String zhengxiangguan = rs.getString("����ؼ��ͻ�");
			nodejbm.setZhengxiangguan(zhengxiangguan);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setZhengxiangguan("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try {
			String fuxiangguan = rs.getString("����ؼ���������");
			nodejbm.setFuxiangguan(fuxiangguan);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setFuxiangguan("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try {
			String keHuCustom = rs.getString("�ͻ�");
			nodejbm.setKeHuCustom(keHuCustom);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setKeHuCustom("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try {
			String jingZhengDuiShou = rs.getString("��������");
			nodejbm.setJingZhengDuiShou(jingZhengDuiShou);
		} catch(java.lang.NullPointerException ex1) {
			nodejbm.setJingZhengDuiShou("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
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
	 * ͨ���ŵĸ���������ɶ�Ӧ
	 */
	private int refreshTDXGaiNianBanKuaiToGeGu (File tmprecordfile)
	{
		File file = new File(sysconfig.getTDXGaiNianBanKuaiToGeGuFile() );
		 if(!file.exists() ) {
			 try {
				Files.append( "û�з���" +  sysconfig.getTDXGaiNianBanKuaiToGeGuFile() +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			} catch (IOException e) {
				e.printStackTrace();
			}
			 return -1;
		 }
		 
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
               int fileheadbytenumber = 384;
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
            	   logger.debug(gupiaoheader);
            	   
            	   //������
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                  
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   logger.debug(tmpstockcodestr);
//                   System.out.print(tmpstockcodestr.size());
                   Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                   logger.debug(tmpstockcodesetnew);
                   
                   //�����ݿ��ж�����ǰ�ð��ĸ���
                   Set<String> tmpstockcodesetold = new HashSet();
                   CachedRowSetImpl rs = null;
	   			    try {
	   			    	String sqlquerystat = "SELECT gnbk.��Ʊ����  FROM ��Ʊͨ���Ÿ������Ӧ�� gnbk, ͨ���Ű���б� bklb"
		    						+ " WHERE  gnbk.������ = bklb.`���ID`"
		    						+ " AND bklb.`�������` = '"  + gupiaoheader + "'"
		    						+ " AND ISNULL(gnbk.�Ƴ�ʱ��)"
		    						;
	   			    	
//	   			    	String sqlquerystat = "SELECT *  FROM ��Ʊͨ���Ÿ������Ӧ��  WHERE  ������ = " 
//	   			    							+ "'"  + gupiaoheader + "'"
//	   			    							+ "AND ISNULL(�Ƴ�ʱ��)"
//	   			    							;
	   			    	logger.debug(sqlquerystat);
	   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			        rs.last();  
	   			        int rows = rs.getRow();  
	   			        rs.first();  
	   			        for(int j=0;j<rows;j++) {  
	   			        	String stockcode = rs.getString("��Ʊ����");
	   			        	tmpstockcodesetold.add(stockcode);
	   			        	
	   			            rs.next();
	   			        }
	   			        
	   			    }catch(java.lang.NullPointerException e){ 
	   			    	e.printStackTrace();
	   			    } catch (SQLException e) {
	   			    	e.printStackTrace();
	   			    }catch(Exception e){
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
	   			    
	   			 //�� tmpstockcodesetold �����еģ�tmpstockcodesetnewû�е�ѡ�������Ǿɵģ�Ҫ�����ݿ���ɾ��
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
//		   	        	String sqldeletetstat = "DELETE  FROM ��Ʊͨ���Ÿ������Ӧ�� "
//		   	        							+ " WHERE ��Ʊ����=" + "'" + str + "'" 
//		   	        							+ " AND ������=" + "'" + gupiaoheader + "'" 
//		   	        							;
//		   	        	logger.debug(sqldeletetstat);
//		   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//		                Files.append("ɾ����" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//		   	        	String sqlupdatestat = "UPDATE ��Ʊͨ���Ÿ������Ӧ��   SET"
//		   	        			+ " �Ƴ�ʱ�� =" + "'" +  sysconfig.formatDate(new Date() ) + "'" 
//	   			 				+ " WHERE ��Ʊ���� = " + "'" + str.trim() + "'" 
//	   			 				+ " AND ������=" + "'" + gupiaoheader + "'" 
//	   			 				+ " AND isnull(�Ƴ�ʱ��)"
//	   			 				;
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader bufr = new BufferedReader (tongdaxinfile);
		
		HashMap<String,List<String>> tmpsysbkmap = new HashMap<String, List<String>> ();
		String line = null;  
        try {
			while((line = bufr.readLine()) != null) {
				
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //���ɰ��|880232|3|1|0|32
				logger.debug(tmpbkinfo);
//				if(tmpbkinfo.get(1).toString().trim().equals("880601"))
//					logger.debug("880601 arrived");
//				if( !Pattern.matches("\\d{4}00",tmpbkinfo.get(5) ) ) { //����������飬������
//					tmpsysbkmap.put(tmpbkinfo.get(1), tmpbkinfo );
					
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
	        	   logger.debug(sqlinsertstat);
	   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				tongdaxinfile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        
        try {
			Files.append("��ʼ����ͨ���Ű����Ϣ:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		} catch (IOException e3) {
			e3.printStackTrace();
		}
        
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
				logger.debug(line);
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //T01|��Դ  T010101|ú̿����
				logger.debug(tmpbkinfo);
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
   			      
   			    }catch(java.lang.NullPointerException e){ 
   			    	e.printStackTrace();
   			    } catch (SQLException e) {
   			    	e.printStackTrace();
   			    }catch(Exception e){
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
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				fr.close();
				fr = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				bufr.close();
				bufr = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
            	   logger.debug(gupiaoheader);
            	   
            	   //������
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                  
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   logger.debug(tmpstockcodestr);
//                   System.out.print(tmpstockcodestr.size());
                   Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                   logger.debug(tmpstockcodesetnew);
                   
                   //�����ݿ��ж�����ǰ�ð��ĸ���
                   Set<String> tmpstockcodesetold = new HashSet();
                   CachedRowSetImpl rs = null;
	   			    try {
	   			    	String sqlquerystat = "SELECT gnbk.��Ʊ����  FROM ��Ʊͨ���Ÿ������Ӧ�� gnbk, ͨ���Ű���б� bklb"
		    						+ " WHERE  gnbk.������ = bklb.`���ID`"
		    						+ " AND bklb.`�������` = '"  + gupiaoheader + "'"
		    						+ " AND ISNULL(gnbk.�Ƴ�ʱ��)"
		    						;
	   			    	
//	   			    	String sqlquerystat = "SELECT *  FROM ��Ʊͨ���Ÿ������Ӧ��  WHERE  ������ = " 
//	   			    							+ "'"  + gupiaoheader + "'"
//	   			    							+ "AND ISNULL(�Ƴ�ʱ��)"
//	   			    							;
	   			    	logger.debug(sqlquerystat);
	   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			        rs.last();  
	   			        int rows = rs.getRow();  
	   			        rs.first();  
	   			        for(int j=0;j<rows;j++) {  
	   			        	String stockcode = rs.getString("��Ʊ����");
	   			        	tmpstockcodesetold.add(stockcode);
	   			        	
	   			            rs.next();
	   			        }
	   			        
	   			    }catch(java.lang.NullPointerException e){ 
	   			    	e.printStackTrace();
	   			    } catch (SQLException e) {
	   			    	e.printStackTrace();
	   			    }catch(Exception e){
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
	   			    
	   			 //�� tmpstockcodesetold �����еģ�tmpstockcodesetnewû�е�ѡ�������Ǿɵģ�Ҫ�����ݿ���ɾ��
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
//		   	        	String sqldeletetstat = "DELETE  FROM ��Ʊͨ���Ÿ������Ӧ�� "
//		   	        							+ " WHERE ��Ʊ����=" + "'" + str + "'" 
//		   	        							+ " AND ������=" + "'" + gupiaoheader + "'" 
//		   	        							;
//		   	        	logger.debug(sqldeletetstat);
//		   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//		                Files.append("ɾ����" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//		   	        	String sqlupdatestat = "UPDATE ��Ʊͨ���Ÿ������Ӧ��   SET"
//		   	        			+ " �Ƴ�ʱ�� =" + "'" +  sysconfig.formatDate(new Date() ) + "'" 
//	   			 				+ " WHERE ��Ʊ���� = " + "'" + str.trim() + "'" 
//	   			 				+ " AND ������=" + "'" + gupiaoheader + "'" 
//	   			 				+ " AND isnull(�Ƴ�ʱ��)"
//	   			 				;
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
	 		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
	 		
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
			autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		
	}
	/*
	 *���ڴ����д��󣬵��°��͸��ɵĶ�Ӧ��ϵ�п��ܳ����ظ��� 
	 */
	public File checkImportTDXDataSync()
	{
		String checkfilename = sysconfig.getSystemInstalledPath ()+  "ͨ��������һ���Լ����.txt";
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
									Files.append(updateresult,filefmxx, charset);
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
								Files.append(stock,filefmxx, charset);
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
		
		if(countaddline >0)
			return filefmxx;
		else
			return null;
	}
	
	/*
	 * ͨ���ŵķ������ɴ����Ӧ�ļ�
	 */
	private void refreshTDXFengGeBanKuaiToGeGu (File tmprecordfile)
	{
		File file = new File(sysconfig.getTDXFengGeBanKuaiToGeGuFile() );
		 
		 if(!file.exists() ) {
			 logger.debug("File not exist");
			 return;
		 }
		 
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
               Files.append("��ʼ����ͨ���Ź�Ʊ����Ӧ��Ϣ:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   //�������
            	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
            	   logger.debug(gupiaoheader);
            	   
            	   //������
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   logger.debug(tmpstockcodestr);
                   logger.debug(tmpstockcodestr.size());
                   Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                   logger.debug(tmpstockcodesetnew);
                   
                   //���� ������ȯ�� �Ǹ���飬��û��ID����ͨ���Ű���б�����û�У�Ҫ���ж��������Σ��Ա��ò�ͬ��SQL
                   boolean hasbkcode = true;
                   CachedRowSetImpl rspd = null; 
	   			   try {
	   			    	String sqlquerystat = "SELECT count(*) as result FROM ͨ���Ű���б� WHERE �������='"  + gupiaoheader + "'";
	   			    	logger.debug(sqlquerystat);
	   			    	rspd = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			    	int result = 0;
	   			        while(rspd.next())  {
	   			        	result = rspd.getInt("result");
	   			        }
	   			   		if(result == 0)
	   			   			hasbkcode = false;
	   			       
	   			    }catch(java.lang.NullPointerException e){ 
	   			    	e.printStackTrace();
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

                   
                   //�����ݿ��ж�����ǰ�ð��ĸ���
                   Set<String> tmpstockcodesetold = new HashSet();
                   CachedRowSetImpl rs = null; 
	   			   try {
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

	   			    	logger.debug(sqlquerystat);
	   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			        rs.last();  
	   			        int rows = rs.getRow();  
	   			        rs.first();  
	   			        for(int j=0;j<rows;j++) {  //�����ݿ������еĸ��ɴ��뱣��
	   			        	String stockcode = rs.getString("��Ʊ����");
	   			        	tmpstockcodesetold.add(stockcode);
	   			        	
	   			            rs.next();
	   			        }
	   			       
	   			    }catch(java.lang.NullPointerException e){ 
	   			    	e.printStackTrace();
	   			    } catch (SQLException e) {
	   			    	e.printStackTrace();
	   			    }catch(Exception e){
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
		   	        
//		   	        if(differencebankuainew.size() ==0 && differencebankuaiold.size()>0 ) { // �µ�Ϊ0���ɵĴ����㣬ɾ����ʱ������ʵ�ֻ������ݿⲻ����������ʱ�䣬�Ը���XML��Ӱ�죬����ǿ�Ƹ���һ����Ϣ
//		   	        	String sqlupdatestat = "UPDATE ͨ���Ű���б� SET �������¸���ʱ�� = "
//		   	        			+  formateDateForDiffDatabase("mysql", sysconfig.formatDate( LocalDateTime.now() ) ) + ","
//		   	        			;
//		   	        	logger.debug(sqlupdatestat);
//		   				connectdb.sqlUpdateStatExecute (sqlupdatestat);
//		   	        }
               }
		 } catch (Exception e) {
			 e.printStackTrace();
		 } finally {
             try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             try {
				dis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		if(!file.exists() ) {
			 logger.debug("File not exist");
			 return -1;
		 }
		 
		 int addednumber =0;
		 FileInputStream in = null;  
		 BufferedInputStream dis = null;
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
              logger.debug(fileHead);
              
              //��������2�ֽ�
              dis.read(itemBuf, 0, 2); 
              String bknumber =new String(itemBuf,0,2);  
              logger.debug(bknumber);
			 
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
           	   logger.debug(gupiaoheader);
           	   
           	   //������
                  String gupiaolist =new String(itemBuf2,12,sigbk-12);
                 
                  List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                  logger.debug(tmpstockcodestr);
//                  System.out.print(tmpstockcodestr.size());
                  Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                  logger.debug(tmpstockcodesetnew);
                  
                  //�����ݿ��ж�����ǰ�ð��ĸ���
                  Set<String> tmpstockcodesetold = new HashSet();
                  CachedRowSetImpl rs = null;
	   			    try {
//	   			    	String sqlquerystat = "SELECT *  FROM ��Ʊͨ���Ž�����ָ����Ӧ��   WHERE  ָ����� = " 
//	   			    							+ "'"  + gupiaoheader + "'"
//	   			    							+ " AND ISNULL(�Ƴ�ʱ��)"
//	   			    							;
	   			    	String sqlquerystat = "SELECT zsbk.��Ʊ����  FROM ��Ʊͨ���Ž�����ָ����Ӧ��  zsbk, ͨ���Ű���б� zslb"
	    						+ " WHERE  zsbk.������ = zslb.`���ID`"
	    						+ " AND zslb.`�������` = '"  + gupiaoheader + "'"
	    						+ " AND ISNULL(zsbk.�Ƴ�ʱ��)"
	    						;
	   			    	logger.debug(sqlquerystat);
	   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			        rs.last();  
	   			        int rows = rs.getRow();  
	   			        rs.first();  
	   			        for(int j=0;j<rows;j++) {  
	   			        	String stockcode = rs.getString("��Ʊ����");
	   			        	tmpstockcodesetold.add(stockcode);
	   			        	
	   			            rs.next();
	   			        }
	   			        
	   			    }catch(java.lang.NullPointerException e){ 
	   			    	e.printStackTrace();
	   			    } catch (SQLException e) {
	   			    	e.printStackTrace();
	   			    }catch(Exception e){
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
				       	logger.debug(sqlupdatestat);
				   		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   	        }
	   			   
	   		        //��tmpstockcodesetnew�����еģ�tmpstockcodesetoldû�е�ѡ���������µģ�Ҫ�������ݿ�
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   				
//		   				String sqlinsertstat = "INSERT INTO  ��Ʊͨ���Ž�����ָ����Ӧ��(��Ʊ����,ָ�����,��ƱȨ��) values ("
//		   						+ "'" + str.trim() + "'" + ","
//		   						+ "'" + gupiaoheader + "'" + ","
//		   						+ 10
//		   						+ ")"
//		   						;
		   		    	String sqlinsertstat = "insert into ��Ʊͨ���Ž�����ָ����Ӧ��(��Ʊ����,������,��ƱȨ��)"
								+ "	  SELECT '" + str.trim() + "', ͨ���Ű���б�.`���ID`, 10 "
								+ " FROM ͨ���Ű���б�  where ͨ���Ű���б�.`�������` = '" + gupiaoheader + "'"   
								;
		   				logger.debug(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				addednumber ++;
		                Files.append("���룺" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
              }
		 } catch (Exception e) {
			 e.printStackTrace();
			 return -1;
		 } finally {
			 try {
				in.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
             try {
				dis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		
		 return addednumber;
	}
	/*
	 * ��ͨ���Ÿ������еĽ�����ָ�����Ϻ�part
	 */
	private int refreshTDXZhiShuShangHaiLists(File tmprecordfile)
	{
		//�ȴ����ݿ��ж�ȡ�������е��Ϻ�ָ��
		  HashSet<String> curallshbk = this.getTDXBanKuaiSet("sh");
		
		//���ļ��ж�ȡ���µ�ָ��
		HashSet allinsertbk = new HashSet (); //�������µ�ָ����������curallshbk�����ж��Ƿ��оɵ�ָ����Ҫ��ɾ��
		
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
//               logger.debug(fileHead);
               
               int sigbk = 18*16+12+14;
               byte[] itemBuf2 = new byte[sigbk];
               int i=0;
               Files.append("��ʼ����ͨ���Ź�Ʊָ������Ӧ��Ϣ:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   String zhishuline =new String(itemBuf2,0,sigbk);
            	   logger.debug(zhishuline);
            	   String zhishucode = zhishuline.trim().substring(0, 6);
//            	   logger.debug(zhishucode);
            	   
//            	   if(  zhishucode.startsWith("600") || zhishucode.startsWith("601") || zhishucode.startsWith("602") || zhishucode.startsWith("603") 
//            			   || zhishucode.startsWith("10") || zhishucode.startsWith("11") || zhishucode.startsWith("01") || zhishucode.startsWith("02") || zhishucode.startsWith("09") 
//            			   || zhishucode.startsWith("12") || zhishucode.startsWith("13") || zhishucode.startsWith("14") ||  zhishucode.startsWith("18") || zhishucode.startsWith("19") || zhishucode.startsWith("20") 
//            			   || zhishucode.startsWith("50") || zhishucode.startsWith("51") || zhishucode.startsWith("52") 
//            			   || zhishucode.startsWith("75") || zhishucode.startsWith("73") || zhishucode.startsWith("78") || zhishucode.startsWith("79")
//            			   || zhishucode.startsWith("90"))
            	   if( !(zhishucode.startsWith("000") || zhishucode.startsWith("880") || zhishucode.startsWith("999") ))
            		   continue;
            	   
//            	   if(zhishucode.equals("880890"))
//            		   logger.debug("880890 arrived");
            	   
            	   List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(zhishuline.substring(6, zhishuline.length()));
            	   String zhishuname = null;
            	   try {
	            	    zhishuname = tmplinelist.get(0).trim().substring(0, 6).trim();
            	   } catch (java.lang.StringIndexOutOfBoundsException ex) {
            		   List<String> tmplinepartnamelist = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(1).trim());
            		   zhishuname = tmplinelist.get(0).trim() + tmplinepartnamelist.get(0).trim();
            	   }
//            	   logger.debug(zhishuname);
            	   
//            	   String sqlinsertstat = "INSERT INTO  ͨ���Ž�����ָ���б�(�������,���ID,ָ������������) values ("
//	   						+ " '" + zhishuname.trim() + "'" + ","
//	   						+ " '" + zhishucode.trim() + "'" + ","
//	   						+ " '" + "sh" + "'"
//	   						+ ")"
//	   						+ " ON DUPLICATE KEY UPDATE "
//	   						+ " �������=" + " '" + zhishuname.trim() + "'" + ","
//	   						+ " ָ������������=" + " '" + "sh" + "'" 
//	   						;
            	   String sqlinsertstat = "INSERT INTO  ͨ���Ű���б�(�������,���ID,ָ������������) values ("
	   						+ " '" + zhishuname.trim() + "'" + ","
	   						+ " '" + zhishucode.trim() + "'" + ","
	   						+ " '" + "sh" + "'"
	   						+ ")"
	   						+ " ON DUPLICATE KEY UPDATE "
	   						+ "������� =" + " '" + zhishuname.trim() + "'" + ","
	   						+ "ָ������������= " + " '" + "sh" + "'"
	   						;
            	   logger.debug(sqlinsertstat);
	   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//	                Files.append("���룺" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	   				allinsertbk.add(zhishucode);
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
		 
		 SetView<String> differencebankuaiold = Sets.difference( curallshbk, allinsertbk );
		 if(differencebankuaiold.size() >0 ) { //˵����ĳЩ�����Ҫɾ��
			 for(String oldbkcode : differencebankuaiold) {
				 String sqldeletetstat = "DELETE  FROM  ͨ���Ű���б� WHERE "
						 				+ " ���ID =" + " '" + oldbkcode.trim() + "'" 
						 				;
         	   logger.debug(sqldeletetstat);
	   			int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
	   			
//	   			//��Ҫɾ���ð���� ��Ʊ�����Ӧ��/��Ʊ��ҵ��Ӧ��/��Ʊ����Ӧ��/��ҵ���Ӱ���б�  �еĹ�Ʊ�Ͱ��Ķ�Ӧ��Ϣ
//	    		sqldeletetstat = "DELETE  FROM  ��Ʊͨ���Ÿ������Ӧ��"
//						+ " WHERE ������=" + "'"  + oldbkcode.trim() + "'"
//						;
//				logger.debug(sqldeletetstat);
//				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				
//				sqldeletetstat = "DELETE  FROM  ��Ʊͨ������ҵ����Ӧ��"
//						+ " WHERE ������=" + "'"  + oldbkcode.trim() + "'"
//						;
//				logger.debug(sqldeletetstat);
//				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				
//				sqldeletetstat = "DELETE  FROM ��Ʊͨ���ŷ�����Ӧ��"
//						+ " WHERE ������=" + "'"  + oldbkcode.trim() + "'"
//						;
//				logger.debug(sqldeletetstat);
//				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//				
//				sqldeletetstat = "DELETE  FROM ��ҵ���Ӱ���б�"
//						+ " WHERE ����ͨ���Ű��=" + "'"  + oldbkcode.trim() + "'"
//						;
//				logger.debug(sqldeletetstat);
//				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
			 }
		 }
		  
		
		return 1;
	}
	/*
	 * ��ͨ���Ÿ������еĽ�����ָ��������part
	 */
	private int refreshTDXZhiShuShenZhenLists(File tmprecordfile)
	{
		//�ȴ����ݿ��ж�ȡ�������е��Ϻ�ָ��
		HashSet<String> curallszbk = this.getTDXBanKuaiSet("sz");
				
		//���ļ��ж�ȡ���µ�ָ��
		HashSet allinsertbk = new HashSet (); //�������µ�ָ����������curallshbk�����ж��Ƿ��оɵ�ָ����Ҫ��ɾ��
				
				
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
               Files.append("��ʼ����ͨ���Ź�Ʊָ������Ӧ��Ϣ:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   String zhishuline =new String(itemBuf2,0,sigbk);
            	   logger.debug(zhishuline);
            	   String zhishucode = zhishuline.trim().substring(0, 6);
//            	   logger.debug(zhishucode);
            	   
//            	   if(  zhishucode.startsWith("000") || zhishucode.startsWith("001") || zhishucode.startsWith("002") || zhishucode.startsWith("300")  
//            			   ||zhishucode.startsWith("100") ||zhishucode.startsWith("101") ||zhishucode.startsWith("106") ||zhishucode.startsWith("107") ||zhishucode.startsWith("108") ||zhishucode.startsWith("109")
//            			   ||zhishucode.startsWith("111") ||zhishucode.startsWith("112") ||zhishucode.startsWith("114") ||zhishucode.startsWith("115") ||zhishucode.startsWith("116") ||zhishucode.startsWith("117") ||zhishucode.startsWith("118") || zhishucode.startsWith("119")
//            			   ||zhishucode.startsWith("120") ||zhishucode.startsWith("121") ||zhishucode.startsWith("123") ||zhishucode.startsWith("127") ||zhishucode.startsWith("128")
//            			   ||zhishucode.startsWith("131") ||zhishucode.startsWith("140") 
//            			   ||zhishucode.startsWith("150") ||zhishucode.startsWith("160") ||zhishucode.startsWith("161") ||zhishucode.startsWith("162") ||zhishucode.startsWith("163")
//            			   ||zhishucode.startsWith("164") ||zhishucode.startsWith("165") ||zhishucode.startsWith("166") ||zhishucode.startsWith("167") ||zhishucode.startsWith("168") ||zhishucode.startsWith("169")
//            			   ||zhishucode.startsWith("184")
//            			   ||zhishucode.startsWith("200")
//            			   ||zhishucode.startsWith("395")
//            			)
            	   if(! (zhishucode.startsWith("399") || zhishucode.startsWith("159") ) )
            		   continue;
            	   
            	   List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(zhishuline.substring(6, zhishuline.length()));
            	   String zhishuname = null;
            	   try {
	            	    zhishuname = tmplinelist.get(0).trim().substring(0, 6).trim();
            	   } catch (java.lang.StringIndexOutOfBoundsException ex) {
            		   List<String> tmplinepartnamelist = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(1).trim());
            		   zhishuname = tmplinelist.get(0).trim() + tmplinepartnamelist.get(0).trim();
            	   }
//            	   logger.debug(zhishuname);
            	   
//            	   String sqlinsertstat = "INSERT INTO  ͨ���Ž�����ָ���б�(�������,���ID,ָ������������) values ("
//	   						+ " '" + zhishuname.trim() + "'" + ","
//	   						+ " '" + zhishucode.trim() + "'" + ","
//	   						+ " '" + "sz" + "'"
//	   						+ ")"
//	   						+ " ON DUPLICATE KEY UPDATE "
//	   						+ " �������=" + " '" + zhishuname.trim() + "'" + ","
//	   						+ " ָ������������=" + " '" + "sz" + "'" 
//	   						;
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
//	                Files.append("���룺" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	   				allinsertbk.add(zhishucode);
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
		 
		 SetView<String> differencebankuaiold = Sets.difference(curallszbk, allinsertbk );
		 if(differencebankuaiold.size() >0 ) { //˵����ĳЩ�����Ҫɾ��
			 for(String oldbkcode : differencebankuaiold) {
				 String sqldeletetstat = "DELETE  FROM  ͨ���Ű���б� WHERE "
						 				+ " ���ID =" + " '" + oldbkcode.trim() + "'" 
						 				;
         	   logger.debug(sqldeletetstat);
	   			int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
	   			
	   			//��Ҫɾ���ð���� ��Ʊ�����Ӧ��/��Ʊ��ҵ��Ӧ��/��Ʊ����Ӧ��/��ҵ���Ӱ���б�  �еĹ�Ʊ�Ͱ��Ķ�Ӧ��Ϣ
	    		sqldeletetstat = "DELETE  FROM  ��Ʊͨ���Ÿ������Ӧ��"
						+ " WHERE ������=" + "'"  + oldbkcode.trim() + "'"
						;
				logger.debug(sqldeletetstat);
				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
				
				sqldeletetstat = "DELETE  FROM  ��Ʊͨ������ҵ����Ӧ��"
						+ " WHERE ������=" + "'"  + oldbkcode.trim() + "'"
						;
				logger.debug(sqldeletetstat);
				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
				
				sqldeletetstat = "DELETE  FROM ��Ʊͨ���ŷ�����Ӧ��"
						+ " WHERE ������=" + "'"  + oldbkcode.trim() + "'"
						;
				logger.debug(sqldeletetstat);
				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
				
				sqldeletetstat = "DELETE  FROM ��ҵ���Ӱ���б�"
						+ " WHERE ����ͨ���Ű��=" + "'"  + oldbkcode.trim() + "'"
						;
				logger.debug(sqldeletetstat);
				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
			 }
		 }
		
		return -1;
	}

	

	/*
	 * �ҳ����ݿ���ͨ�����Զ�����
	 */
	private HashMap<String, BanKuai> initializeSystemAllZdyBanKuaiList()  
	{
		//HashMap<String,BanKuai> sysbankuailiebiaoinfo;
//		private  HashMap<String,BanKuai> zdybankuailiebiaoinfo;
		HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();

		String sqlquerystat = "SELECT ���ID,�������,����ʱ��  FROM ͨ�����Զ������б� 	ORDER BY ���ID,������� ,����ʱ�� "
							   ;   
		logger.debug(sqlquerystat);
		CachedRowSetImpl  rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	        rs.last();  
	        int rows = rs.getRow();  
//	        data = new Object[rows][];    
//	        int columnCount = 3;//����  
	        rs.first();  
	        //int k = 0;  
	        //while(rs.next())
	        for(int j=0;j<rows;j++) {  
	            //Object[] row = new Object[columnCount]; 
	        	BanKuai tmpbk = new BanKuai (rs.getString("���ID"),rs.getString("�������") );
	        	tmpsysbankuailiebiaoinfo.put(rs.getString("���ID"), tmpbk);
	            rs.next();
	        }
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
	    	e.printStackTrace();
	    } finally {
	    	try {
				rs.close ();
				rs = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
	    
	    return  tmpsysbankuailiebiaoinfo;
   
	}
	/*
	 * ���й�Ʊ
	 */
	public ArrayList<Stock> getAllStocks() 
	{
		ArrayList<Stock> tmpsysbankuailiebiaoinfo = new ArrayList<Stock> ();

		String sqlquerystat = "SELECT ��Ʊ����,��Ʊ���� FROM A��"									
							   ;   
		logger.debug(sqlquerystat);
		CachedRowSetImpl rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	        rs.last();  
	        int rows = rs.getRow();  
	        rs.first();  
	        //int k = 0;  
//	        while(rs.next()) {
	        for(int j=0;j<rows;j++) {  
	        	Stock tmpbk = new Stock (rs.getString("��Ʊ����"),rs.getString("��Ʊ����"));
	        	logger.debug(rs.getString("��Ʊ����"));
	        	tmpsysbankuailiebiaoinfo.add(tmpbk);
	            rs.next();
	        }
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
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
	    
	    return tmpsysbankuailiebiaoinfo;
	}
	/*
	 * �ҳ�ͨ���Ŷ�������а��.��������������ָ�����
	 */
	private HashSet<String> getTDXBanKuaiSet(String jys)
	{
		HashSet<String> tmpsysbankuailiebiaoinfo = new HashSet<String>();
		
		String sqlquerystat;
		if(!jys.toLowerCase().equals("all"))
			 sqlquerystat = "SELECT ���ID,�������,ָ������������,�����������  FROM ͨ���Ű���б� 	WHERE ָ������������ = '" + jys +"' "  ;
		else
			 sqlquerystat = "SELECT ���ID,�������,ָ������������,����������� FROM ͨ���Ű���б� 	"  ;
		logger.debug(sqlquerystat);
		CachedRowSetImpl rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	        rs.last();  
	        int rows = rs.getRow();  
//	        data = new Object[rows][];    
//	        int columnCount = 3;//����  
	        rs.first();  
	        //int k = 0;  
	        //while(rs.next())
	        for(int j=0;j<rows;j++) {  
	        	String bkcode = rs.getString("���ID");
	        	tmpsysbankuailiebiaoinfo.add(bkcode);
	        	
	            rs.next();
	        }
	        
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
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
	    
	    return tmpsysbankuailiebiaoinfo;
	}
	public ArrayList<BanKuai> getTDXBanKuaiList(String jys)   
	{
		ArrayList<BanKuai> tmpsysbankuailiebiaoinfo = new ArrayList<BanKuai> ();
		
		String sqlquerystat;
		if(!jys.toLowerCase().equals("all"))
			 sqlquerystat = "SELECT ���ID,�������,ָ������������,�����������,����Gephi,���뽻������,������   FROM ͨ���Ű���б� 	WHERE ָ������������ = '" + jys +"' "  ;
		else
			 sqlquerystat = "SELECT ���ID,�������,ָ������������,�����������,����Gephi,���뽻������,������   FROM ͨ���Ű���б� 	"  ;
		logger.debug(sqlquerystat);
		CachedRowSetImpl rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	        rs.last();  
	        int rows = rs.getRow();  
//	        data = new Object[rows][];    
//	        int columnCount = 3;//����  
	        rs.first();  
	        //int k = 0;  
	        //while(rs.next())
	        for(int j=0;j<rows;j++) {  
	        	BanKuai tmpbk = new BanKuai (rs.getString("���ID"),rs.getString("�������") );
	        	tmpbk.setSuoShuJiaoYiSuo(rs.getString("ָ������������"));
	        	tmpbk.setBanKuaiLeiXing( rs.getString("�����������") );
	        	
	        	tmpbk.setExporttogehpi(rs.getBoolean("����Gephi"));
	        	tmpbk.setImportdailytradingdata(rs.getBoolean("���뽻������"));
	        	tmpbk.setShowinbkfxgui(rs.getBoolean("������"));
	        	
	        	tmpsysbankuailiebiaoinfo.add(tmpbk);
	        	
	            rs.next();
	        }
	        
	    }catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }catch(Exception e){
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
	    
	    return tmpsysbankuailiebiaoinfo;
	}
	/*
	 * �ҳ�ĳͨ���Ű��������Ӱ�飬Ϊ����ҵ��
	 */
	public HashMap<String,String> getSubBanKuai(String tdxbk) 
	{
		String sqlquerystat = "SELECT * FROM ��ҵ���Ӱ���б� 	"
								+" WHERE ����ͨ���Ű�� =" + "'" + tdxbk + "'" 
								+ "ORDER BY �Ӱ������"
				   			   ;   
		logger.debug(sqlquerystat);
		HashMap<String,String> tmpsubbklist = new HashMap<String,String> ();
		CachedRowSetImpl rs = null;
		try {  
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			rs.last();  
			int rows = rs.getRow();  
			rs.first();  
			for(int j=0;j<rows;j++) {  
				String tmpbkcode = rs.getString("�Ӱ�����");
				String tmpbkname = rs.getString("�Ӱ������");
				tmpsubbklist.put(tmpbkcode,tmpbkname);
				rs.next();
			}
		
		}catch(java.lang.NullPointerException e){ 
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
	    	if(rs != null)
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }  
		return tmpsubbklist;
	}
	
	/*
	 * 
	 */
	public String addNewSubBanKuai(String tdxbk,String newsubbk) 
	{
		HashMap<String, String> cursubbks = this.getSubBanKuai(tdxbk);
		int cursize = cursubbks.size();
		if(cursubbks.containsValue(newsubbk)) 
			return null;
			
		String subcylcode = tdxbk + String.valueOf(cursize+1);
		String sqlinsertstat = "INSERT INTO  ��ҵ���Ӱ���б�(�Ӱ������,����ͨ���Ű��,�Ӱ�����) values ("
								+ "'" + newsubbk.trim() + "'" + ","
								+ "'" + tdxbk.trim() + "'" + ","
								+ "'" + subcylcode.trim() + "'"
								+ ")"
								;
		logger.debug(sqlinsertstat);
		int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		
		return subcylcode;
		
	}
	/*
	 * 
	 */
	public void setNewZdyBanKuai(String bkname, String bkcode) 
	{
		String dbbkname =  "zdy"+bkname.trim();
		String sqlinsertstat = "INSERT INTO  �Զ������б�(�������,����ʱ��,���ID) values ("
				+ "'" + dbbkname + "'" + ","
				+  "#" + CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now()) + "#" + ","
				+ "'" + bkcode.trim() + "'" 
				+ ")"
				;
		logger.debug(sqlinsertstat);
		int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		
//		BanKuai tmpbkai = new BanKuai (dbbkname);
//		tmpbkai.setBankuaiCode(bkcode);
//		//this.zdybankuailiebiaoinfo.put(dbbkname, tmpbkai);
	}
	
	public HashMap<String, BanKuai> getZdyBanKuaiList ()
	{
				//HashSet<String> tmpbankuailiebiaoset = new HashSet<String> ();
				HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();
				//Multimap<String,String> tmpsysbkmap =   ArrayListMultimap.create();
				
				String sqlquerystat = "SELECT ���ID,�������,����ʱ��  FROM ͨ�����Զ������б� 	ORDER BY �������, ���ID, ����ʱ��";   
				logger.debug(sqlquerystat);
				CachedRowSetImpl  rs = null;  
			    try {  
			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			        rs.last();  
			        int rows = rs.getRow();  
//			        data = new Object[rows][];    
//			        int columnCount = 3;//����  
			        rs.first();  
			        //int k = 0;  
			        //while(rs.next())
			        for(int j=0;j<rows;j++) {  
			            //Object[] row = new Object[columnCount]; 
			        	BanKuai tmpbk = new BanKuai (rs.getString("���ID"),rs.getString("�������") );
			        	//tmpbk.setBankuaicreatedtime(rs.getDate("����ʱ��"));
		          	  
			        	//tmpbankuailiebiaoset.add(rs.getString("�������"));
			        	tmpsysbankuailiebiaoinfo.put(rs.getString("�������"),tmpbk); 
			           
			            rs.next();
			        }
			        
			    }catch(java.lang.NullPointerException e){ 
			    	e.printStackTrace();
			    } catch (SQLException e) {
			    	e.printStackTrace();
			    }catch(Exception e){
			    	e.printStackTrace();
			    }finally {
			    	if(rs != null)
						try {
							rs.close();
							rs = null;
						} catch (SQLException e) {
							e.printStackTrace();
						}
			    }  

			    return tmpsysbankuailiebiaoinfo;
	}
	
	/*
	 * ������ݿ��е�ͨ���Ű����Щ�м�¼�ļ�����Щû�С� 
	 */
	public File preCheckTDXBanKuaiVolAmoToDb (String cys)
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
		
		HashSet<String> allsysbk = this.getTDXBanKuaiSet (cys.toLowerCase());
		ArrayList<String> allbkcode = new ArrayList<String>(allsysbk );
		int found =0 ; int notfound = 0;
		try {
			Files.append("ϵͳ����"+ allbkcode.size() + "����飡"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		
			for(String tmpbkcode:allbkcode) {
//				String bkcys = allsysbk.get(tmpbkcode).getSuoShuJiaoYiSuo().trim().toUpperCase();
				String bkfilename = (filenamerule.replaceAll("YY",cys.toUpperCase())).replaceAll("XXXXXX", tmpbkcode);
				
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
		
		return tmprecordfile;
	}
	/*
	 * ���û�����ɽ�����Ҫ���°������ͣ�// ͨ�������涨��İ���м��֣�1.�и��������гɽ������� 2. �и��������޳ɽ������� 3.�޸��������гɽ�������
	 */
	public void refreshTDXSystemBanKuaiLeiXing () 
	{
		 ArrayList<BanKuai> curallbk = this.getTDXBanKuaiList("all");

		for ( BanKuai bankuai : curallbk) {

		    String bkcode = bankuai.getMyOwnCode();
		    String bkcys = bankuai.getSuoShuJiaoYiSuo();
		    
		    String cjltablename;
			if(bkcys.toLowerCase().equals("sh"))
				cjltablename = "ͨ���Ű��ÿ�ս�����Ϣ";
			else
				cjltablename = "ͨ���Ž�����ָ��ÿ�ս�����Ϣ";
			
		    String sqlquerystat = "SELECT COUNT(*) AS RESULT FROM " + cjltablename + " WHERE  ���� = " 
								+ "'"  + bkcode + "'" 
								;
		    logger.debug(sqlquerystat);
		    CachedRowSetImpl  rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		    int dy = 0;
		    try {
				while(rs.next()) {
					 dy = rs.getInt("RESULT");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		    
		    Boolean bkhacjl = false;
		    if(dy>0)
		    	bkhacjl = true;
		    
		    HashMap<String, String> actiontables = this.getActionRelatedTables(bankuai,null);
		    String bktypetable = actiontables.get("��Ʊ����Ӧ��");
		    Boolean bkhasgegu = false;
			if(bktypetable != null)
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
			
			if(bktype != null) {
				String sqlupdatequery = "UPDATE ͨ���Ű���б� SET ����������� = " + "'" + bktype +"'" + " WHERE ���ID = '" + bkcode + "'";
				logger.debug(sqlupdatequery);
			    connectdb.sqlUpdateStatExecute(sqlupdatequery);
			}
			if(bktype.equals(BanKuai.NOGGWITHSELFCJL)  ||  bktype.equals(BanKuai.NOGGNOSELFCJL)) { //û�и��ɵİ��϶���������gephi
				String sqlupdatequery = "UPDATE ͨ���Ű���б� SET ����Gephi = false WHERE ���ID = '" + bkcode + "'";
				logger.debug(sqlupdatequery);
			    connectdb.sqlUpdateStatExecute(sqlupdatequery);
			}
			if(  bktype.equals(BanKuai.NOGGNOSELFCJL)) { //û�и���û�гɽ����İ��϶�����������
				String sqlupdatequery = "UPDATE ͨ���Ű���б� SET ������ = false WHERE ���ID = '" + bkcode + "'";
				logger.debug(sqlupdatequery);
			    connectdb.sqlUpdateStatExecute(sqlupdatequery);
			}
				

		}
		
		curallbk = null;
	}
	/*
	 * ͬ��ͨ������֤����ÿ�ճɽ�����Ϣ����Ϣ����"ͨ���Ű��ÿ�ս�����Ϣ"��
	 */
	public File refreshTDXBanKuaiVolAmoToDb (String cys)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "ͬ��ͨ�����Ϻ����ɽ�������.tmp");
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		String cjltablename;
		if(cys.toLowerCase().equals("sh"))
			cjltablename = "ͨ���Ű��ÿ�ս�����Ϣ";
		else
			cjltablename = "ͨ���Ž�����ָ��ÿ�ս�����Ϣ";
		
		 HashSet<String> allbkcode = this.getTDXBanKuaiSet (cys.toLowerCase());
		 ArrayList<BanKuai> allbklist = this.getTDXBanKuaiList(cys.toLowerCase()); //���������ж���Щ������赼��ÿ������
//		for(String tmpbkcode:allbkcode) {
		for(BanKuai bk :  allbklist ) {
//			String bkcys = allsysbk.get(tmpbkcode).getSuoShuJiaoYiSuo().trim().toUpperCase();
			if(!bk.isImportdailytradingdata())
				continue;
			
			String tmpbkcode = bk.getMyOwnCode();
			String bkfilename = (filenamerule.replaceAll("YY",cys.toUpperCase())).replaceAll("XXXXXX", tmpbkcode);
			File tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {  
			    continue; 
			} 

				CachedRowSetImpl  rs = null;
				LocalDate ldlastestdbrecordsdate = null;
				try { 				
					String sqlquerystat = "SELECT  MAX(��������) 	MOST_RECENT_TIME"
							+ " FROM " + cjltablename + "  WHERE  ���� = " 
   							+ "'"  + tmpbkcode + "'" 
   							;
					logger.debug(sqlquerystat);
   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   			    	while(rs.next()) {
//   			    		logger.debug(rs.getMetaData().getColumnCount());
   			    		java.sql.Date lastestdbrecordsdate = rs.getDate("MOST_RECENT_TIME"); //mOST_RECENT_TIME
   			    		try {
   			    			ldlastestdbrecordsdate = lastestdbrecordsdate.toLocalDate();
   			    		} catch (java.lang.NullPointerException e) {
   			    			logger.debug(tmpbkcode + "û�м�¼");
   			    		}
//	   			    	Instant instant = Instant.ofEpochMilli(lastestdbrecordsdate.getTime());
//	   			    	ldlastestdbrecordsdate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()) .toLocalDate();
//   			    		ldlastestdbrecordsdate = 	(lastestdbrecordsdate.toInstant()).atZone(ZoneId.systemDefault()).toLocalDate();
   			    	}
   			    } catch(java.lang.NullPointerException e) { 
   			    	e.printStackTrace();
   			    } catch (SQLException e) {
   			    	e.printStackTrace();
   			    }catch(Exception e){
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
				
				setVolAmoRecordsFromFileToDatabase(tmpbkcode,tmpbkfile,ldlastestdbrecordsdate,cjltablename,dateRule,tmprecordfile);
		}
		
		allbklist = null;
		allbkcode = null;
		
		return tmprecordfile;
	}
	/*
	 * 
	 */
	private List<String> getTDXVolFilesRule ()
	{
		List<String> volamooutput = sysconfig.getTDXVOLFilesPath();
		String exportath = sysconfig.toUNIXpath(Splitter.on('=').trimResults().omitEmptyStrings().splitToList(volamooutput.get(0) ).get(1) );
		String filenamerule = Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(1)).get(1);
		String dateRule = getTDXDateExportDateRule(Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(2)).get(1));
		
		List<String> tdxVolFileRule = new ArrayList<String> ();
		tdxVolFileRule.add(exportath);
		tdxVolFileRule.add(filenamerule);
		tdxVolFileRule.add(dateRule);
		
		return tdxVolFileRule;
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
	private void setVolAmoRecordsFromFileToDatabase (String tmpbkcode, File tmpbkfile, LocalDate lastestdbrecordsdate, String inserttablename, String datarule,File tmprecordfile)
	{
		 
		Charset charset = sysconfig.charSet();
		if(lastestdbrecordsdate == null) //null˵�����ݿ����滹û����ص����ݣ���ʱ������Ϊ1900����ļ����������ݶ�����
			try {
		        lastestdbrecordsdate = LocalDate.now().minus(100,ChronoUnit.YEARS);
				Files.append(tmpbkcode + "���ɽ�����¼���������м�¼��"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
		
		RandomAccessFile rf = null;
        try {  
            rf = new RandomAccessFile(tmpbkfile, "r");  
            long len = rf.length();  
            long start = rf.getFilePointer();  
            long nextend = start + len - 1;  
            String line;  
            rf.seek(nextend);  
            int c = -1;  
            boolean finalneededsavelinefound = false;
            int lineimportcount =0;
            while (nextend > start && finalneededsavelinefound == false) {
                c = rf.read();
            	if (c == '\n' || c == '\r') {  
                    line = rf.readLine();  
                    if (line != null) {  
                        @SuppressWarnings("deprecation")
						List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(line);

                        if( tmplinelist.size() ==7 && !tmplinelist.get(5).equals("0")) { //�п����ǰ������ݣ���0��������������¼��
                        	LocalDate curlinedate = null;
                    		try {
                    			String beforparsedate = tmplinelist.get(0);
                    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datarule);
                    			curlinedate =  LocalDate.parse(beforparsedate,formatter) ;
                    			
                    			if(curlinedate.isAfter(lastestdbrecordsdate)) {
                        			String sqlinsertstat = "INSERT INTO " + inserttablename +"(����,��������,���̼�,��߼�,��ͼ�,���̼�,�ɽ���,�ɽ���) values ("
                    						+ "'" + tmpbkcode + "'" + ","
                    						+ "'" +  curlinedate + "'" + ","
                    						+ "'" +  tmplinelist.get(1) + "'" + "," 
                    						+ "'" +  tmplinelist.get(2) + "'" + "," 
                    						+ "'" +  tmplinelist.get(3) + "'" + "," 
                    						+ "'" +  tmplinelist.get(4) + "'" + "," 
                    						+ "'" +  tmplinelist.get(5) + "'" + "," 
                    						+ "'" +  tmplinelist.get(6) + "'"  
                    						+ ")"
                    						;
                        			logger.debug(sqlinsertstat);
                    				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
                    				lineimportcount ++;
                        		} else if(curlinedate.compareTo(lastestdbrecordsdate) == 0) {
                        			finalneededsavelinefound = true;
                        			Files.append(tmpbkcode + "ָ���ɽ�����¼������ʼʱ��Ϊ:" + line + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        			Files.append(tmpbkcode + "������:" + lineimportcount + "����¼" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        		}
                    		} catch (Exception e) {
//                    			e.printStackTrace();
                    			logger.debug("���������ڵ�������");
                    		}
                        }
                    } //else {  
//                        logger.debug(line);  
//                  }  
                    nextend--;  
                }  
                nextend--;  
                rf.seek(nextend);  
                if (nextend == 0) {// ���ļ�ָ�������ļ���ʼ���������һ��   �ļ���һ�����ļ�̧ͷ������Ҫ����
                    //logger.debug(new String(rf.readLine().getBytes("ISO-8859-1"), charset ));  
                }  
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                if (rf != null)  
                    rf.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
	}
/*
 * 
 */
	public Date getTDXRelatedTableLastModfiedTime() 
	{
		Date lastesttdxtablesmdftime = null;
		
		HashMap<String,String> sqlstatmap = new HashMap<String,String> (); 
		String sqlquerystat = " SELECT MAX(����ʱ��) FROM ͨ���Ű���б�" 
						   ;
		sqlstatmap.put("mysql", sqlquerystat);
			
			CachedRowSetImpl rs = null; 
		    try {  
		    	 rs = connectdb.sqlQueryStatExecute(sqlstatmap);
		    	 
		    	 java.sql.Timestamp tmplastmdftime = null;
		    	 while(rs.next())
		    		  tmplastmdftime = rs.getTimestamp("MAX(����ʱ��)");
		    	 logger.debug(tmplastmdftime);
		    	 lastesttdxtablesmdftime = tmplastmdftime;

		    }catch(java.lang.NullPointerException e){
		    	lastesttdxtablesmdftime = null;
		    	e.printStackTrace();
		    } catch (SQLException e) {
		    	logger.debug(sqlquerystat);
		    	lastesttdxtablesmdftime = null;
		    	e.printStackTrace();
		    }catch(Exception e){
		    	lastesttdxtablesmdftime = null;
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
			
		return lastesttdxtablesmdftime;
	}
	
	private boolean getTDXRelatedTablesStatus()
	{
		boolean tableunderupdated = false;
		// show OPEN TABLES where In_use > 0;
		//SHOW OPEN TABLES WHERE `Table` LIKE '%[TABLE_NAME]%' AND `Database` LIKE '[DBNAME]' AND In_use > 0;
		HashMap<String,String> sqlstatmap = new HashMap<String,String> (); 
//		ArrayList<String> rmtservertable = new ArrayList<String>();
//		rmtservertable.add("ͨ���Ž�����ָ���б�");
//		rmtservertable.add("ͨ���Ű���б�");
		List<String> rmtservertable = Lists.newArrayList("ͨ���Ű���б�");
		
		
			String sqlquerystat =  " show OPEN TABLES where In_use > 0" ;
			//sqlquerystat = "SHOW OPEN TABLES WHERE `Table` LIKE '%[a��]%' AND `Database` LIKE '[stockinfomanagementtest]' AND In_use > 0";		   
			logger.debug(sqlquerystat);
			sqlstatmap.put("mysql", sqlquerystat);
			
			CachedRowSetImpl rs = null; 
		    try {  
		    	 rs = connectdb.sqlQueryStatExecute(sqlstatmap);
		    	 
		    	 while(rs.next()) {
		    		 String tmptablename = rs.getString("Table");
		    		 String tmpinuse = rs.getString("In_use");
		    		 if(rmtservertable.contains(tmptablename) && tmpinuse.equals("1"))
		    			 tableunderupdated = true; 
		    		 
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
			
		
		
		return tableunderupdated;
	}
	/*
	 * �жϰ����ʲô���͵İ�飬����/���/ָ��/��ҵ/����
	 */
	private HashMap<String,String> getActionRelatedTables (BanKuai bankuai,String stockcode)
	{
		String bkcode = bankuai.getMyOwnCode();
		HashMap<String,String> actiontables = new HashMap<String,String> ();
		if(!Strings.isNullOrEmpty(bkcode)) {
			String stockvsbktable =null;
				if(stockvsbktable == null) {	
					CachedRowSetImpl rs = null;
					int dy = -1;
					try {  
						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM ��Ʊͨ���Ÿ������Ӧ��  WHERE ������='" + bkcode + "'";
//						logger.debug(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							 dy = rs.getInt("RESULT");
	//						 logger.debug(dy);
						 }
					}catch(java.lang.NullPointerException e){ 
						logger.debug( "���ݿ�����ΪNULL!");
					} catch (SQLException e) {
						e.printStackTrace();
					}catch(Exception e){
						e.printStackTrace();
					} finally {
						try {
							if(rs != null)
								rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						rs = null;
						
						if(dy>0)
							stockvsbktable =  "��Ʊͨ���Ÿ������Ӧ��";
					}
				}
				if(stockvsbktable == null) {
					CachedRowSetImpl rs = null;
					int dy = -1;
					try {
						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM ��Ʊͨ���ŷ�����Ӧ�� WHERE ������='" + bkcode + "'";
//						logger.debug(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							  dy = rs.getInt("RESULT");
//							 logger.debug(dy1);
						 }
					}catch(java.lang.NullPointerException e){ 
						logger.debug( "���ݿ�����ΪNULL!");
					} catch (SQLException e) {
						e.printStackTrace();
					}catch(Exception e){
						e.printStackTrace();
					} finally {
						try {
							if(rs != null)
								rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						rs = null;
						
						if(dy>0)
							stockvsbktable =  "��Ʊͨ���ŷ�����Ӧ��";
					}
				}
				
				if(stockvsbktable == null) {
					CachedRowSetImpl rs = null;
					int dy = -1;
					try {
						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM ��Ʊͨ������ҵ����Ӧ��  WHERE ������='" + bkcode + "'";
//						logger.debug(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							 dy = rs.getInt("RESULT");
//							 logger.debug(dy);
						 }
					}catch(java.lang.NullPointerException e){ 
						logger.debug( "���ݿ�����ΪNULL!");
					} catch (SQLException e) {
						e.printStackTrace();
					}catch(Exception e){
						e.printStackTrace();
					} finally {
						try {
							if(rs != null)
								rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						rs = null;
						
						if(dy>0)
							stockvsbktable = "��Ʊͨ������ҵ����Ӧ��";
					}
				}
				
				if(stockvsbktable == null) {
					CachedRowSetImpl rs = null;
					int dy = -1;
					try {
						String sqlquerystat = "SELECT COUNT(*) AS RESULT FROM ��Ʊͨ���Ž�����ָ����Ӧ��  WHERE ������='" + bkcode + "'";
//						logger.debug(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							 dy = rs.getInt("RESULT");
//							 logger.debug(dy);
						 }
					}catch(java.lang.NullPointerException e) {
						e.printStackTrace();
						logger.debug( "���ݿ�����ΪNULL!");
					} catch (SQLException e) {
						e.printStackTrace();
					}catch(Exception e){
						e.printStackTrace();
					} finally {
						try {
							if(rs != null)
								rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						rs = null;
						
						if(dy>0)
							stockvsbktable = "��Ʊͨ���Ž�����ָ����Ӧ��";
					}
				}
				if(stockvsbktable == null) {
					try {
						CachedRowSetImpl rsdy = null;
						int dy = -1;
						try {
							String diyusqlquerystat = "SELECT ��ӦTDXSWID  FROM ͨ���Ű���б� WHERE ���id = '" + bkcode + "'" ;
//							logger.debug(diyusqlquerystat);
							 rsdy = connectdb.sqlQueryStatExecute(diyusqlquerystat);
							 while(rsdy.next()) {
								 dy = rsdy.getInt("��ӦTDXSWID");
//								 logger.debug(dy);
							 }
						}catch(java.lang.NullPointerException e){ 
							logger.debug( "���ݿ�����ΪNULL!");
						} catch (SQLException e) {
//							e.printStackTrace();
							logger.debug("java.sql.SQLException: ���� 1 �е�ֵ (оƬ) ִ�� getInt ʧ�ܣ����ǵ����顣");
						}catch(Exception e){
							e.printStackTrace();
						} finally {
							try {
								if(rsdy != null)
									rsdy.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							rsdy = null;
							if(dy>=1 && dy <=32) {
								stockvsbktable =  "��Ʊͨ���Ż�������Ϣ��Ӧ��";
								actiontables.put("�������", String.valueOf(dy) );
							}
						}
					} catch (java.lang.NullPointerException e1) {
						
					}
				}
				
				if(stockvsbktable != null)
					actiontables.put("��Ʊ����Ӧ��", stockvsbktable);
				
				
				String bkcjltable = null;
				if(bankuai.getSuoShuJiaoYiSuo().toLowerCase().equals("sh"))
					bkcjltable = "ͨ���Ű��ÿ�ս�����Ϣ";
				else 
					bkcjltable = "ͨ���Ž�����ָ��ÿ�ս�����Ϣ";
				actiontables.put("���ÿ�ս�������", bkcjltable);
		}
		
		if(!Strings.isNullOrEmpty(stockcode)) {
				String gegucjltable;
				// TODO Auto-generated method stub
				if(stockcode.startsWith("6"))
					gegucjltable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
				else
					gegucjltable = "ͨ���������Ʊÿ�ս�����Ϣ";
				actiontables.put("��Ʊÿ�ս�������", gegucjltable);
		}
		
		return actiontables;
	}
	/*
	 * �ҳ�ĳ��ʱ��� ��ҵ/����/���/ָ�� ͨ����ĳ���������и��ɼ�Ȩ��,���ҳɽ���
	 */
	public BanKuai getTDXBanKuaiGeGuOfHyGnFg(BanKuai currentbk,LocalDate selecteddatestart , LocalDate selecteddateend, BkChanYeLianTree treeallstocks)
	{
		String currentbkcode = currentbk.getMyOwnCode();
		
		HashMap<String, String> actiontables = this.getActionRelatedTables(currentbk,null);
//		logger.debug(actiontables);
		String bktypetable = actiontables.get("��Ʊ����Ӧ��");
		String bknametable = actiontables.get("���ָ�����Ʊ�");
		if(bktypetable == null) {
			return currentbk;
		}
		
		String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart).trim();
		String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend).trim();

		ArrayList<Stock> gegumap = new ArrayList<Stock> ();
		
		CachedRowSetImpl rs1 = null;
		try { 
			String sqlquerystat1 = "";
			if(bktypetable.trim().equals("��Ʊͨ���Ż�������Ϣ��Ӧ��") ) { //�ҵ�����
				String dycode = actiontables.get("�������");
				sqlquerystat1 = "SELECT a��.`��Ʊ����` , a��.`��Ʊ����` ,10 as '��ƱȨ��' \r\n" + 
				 		"FROM ��Ʊͨ���Ż�������Ϣ��Ӧ��, a��\r\n" + 
				 		"WHERE ��Ʊͨ���Ż�������Ϣ��Ӧ��.`����DY` = " + dycode + "\r\n" + 
				 		"AND ��Ʊͨ���Ż�������Ϣ��Ӧ��.`��Ʊ����GPDM` = a��.`��Ʊ����`"
				 		;
			 } else { //��������ҵָ�����
				 //from WQW
//				 sqlquerystat1 = "select "+ bktypetable + ".`��Ʊ����` , a��.`��Ʊ����`, "+ bktypetable + ".`������` , "+ bktypetable + ".`��ƱȨ��`\r\n" + 
//				 		"          from "+ bktypetable + ", a��\r\n" + 
//				 		"          where "+ bktypetable + ".`��Ʊ����`  = a��.`��Ʊ����`\r\n" + 
//				 		"           and '" +  formatedstartdate + "' >= Date("+ bktypetable + ".`����ʱ��`)\r\n" + 
//				 		"           and '" +  formatedenddate + "' <  ifnull("+ bktypetable + ".`�Ƴ�ʱ��`, '2099-12-31')\r\n" + 
//				 		"           and "+ bktypetable + ".`������` =  '" + currentbkcode + "'\r\n" + 
//				 		"         \r\n" + 
//				 		"         group by "+ bktypetable + ".`��Ʊ����`, "+ bktypetable + ".`������`\r\n" + 
//				 		"         order by "+ bktypetable + ".`��Ʊ����`, "+ bktypetable + ".`������` ";
				 sqlquerystat1 = "select "+ bktypetable + ".`��Ʊ����` , a��.`��Ʊ����`, "+ bktypetable + ".`������` , "+ bktypetable + ".`��ƱȨ��`\r\n" + 
					 		"          from "+ bktypetable + ", a��\r\n" + 
					 		"          where "+ bktypetable + ".`��Ʊ����`  = a��.`��Ʊ����`\r\n" + 
					 		
					 		"and (  (  Date("+ bktypetable + ".`����ʱ��`) between '" +  formatedstartdate + "'  and '" +  formatedenddate + "')\r\n" + 
					 		"           		 or( ifnull("+ bktypetable + ".`�Ƴ�ʱ��`, '2099-12-31') between '" +  formatedstartdate + "'  and '" +  formatedenddate + "')\r\n" + 
					 		"           		 or( Date("+ bktypetable + ".`����ʱ��`) <= '" +  formatedstartdate + "' and ifnull(" + bktypetable + ".`�Ƴ�ʱ��`, '2099-12-31') >= '" +  formatedenddate + "' )\r\n" + 
					 		"			  )" +
//					 		"           and '" +  formatedstartdate + "' >= Date("+ bktypetable + ".`����ʱ��`)\r\n" + 
//					 		"           and '" +  formatedenddate + "' <  ifnull("+ bktypetable + ".`�Ƴ�ʱ��`, '2099-12-31')\r\n" + 
					 		
					 		
					 		"           and "+ bktypetable + ".`������` =  '" + currentbkcode + "'\r\n" + 
					 		"         \r\n" + 
					 		"         group by "+ bktypetable + ".`��Ʊ����`, "+ bktypetable + ".`������`\r\n" + 
					 		"         order by "+ bktypetable + ".`��Ʊ����`, "+ bktypetable + ".`������` ";
			 }
//			 logger.debug(sqlquerystat1);
			 logger.debug("�����" + currentbk.getMyOwnCode() + currentbk.getMyOwnName() + "Ѱ�Ҵ�" + formatedstartdate.toString() + "��" + formatedenddate.toString() + "ʱ����ڵĸ��ɣ�");
			 rs1 = connectdb.sqlQueryStatExecute(sqlquerystat1);
			 
			 while(rs1.next()) {  
				 
				String tmpstockcode = rs1.getString("��Ʊ����");
				
				String tmpstockname;
				try {
					tmpstockname = rs1.getString("��Ʊ����"); //"��Ʊ����"
				} catch (java.lang.NullPointerException e ) {
					tmpstockname = "";
				}
				
				Stock tmpstock = (Stock)treeallstocks.getSpecificNodeByHypyOrCode (tmpstockcode,BanKuaiAndStockBasic.TDXGG);
				StockOfBanKuai bkofst = new StockOfBanKuai(currentbk,tmpstock);
				
				Integer weight = rs1.getInt("��ƱȨ��");
				bkofst.setStockQuanZhong(weight);
				
				currentbk.addNewBanKuaiGeGu(bkofst);
			}
				
			}catch(java.lang.NullPointerException e){ 
				e.printStackTrace();
//				logger.debug( "���ݿ�����ΪNULL!");
			} catch (SQLException e) {
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			} finally {
				try {
					if(rs1 != null)
						rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				rs1 = null;
			}
		
		return currentbk;
	}
	/*
	 * ����趨ʱ�����ںʹ�����ȵİ�����ռ�ȡ�
	 */
	public  BanKuai getBanKuaiZhanBi (BanKuai bankuai,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{//��������ʼ�ǿ���Ϊ�ܵ�ռ�ȣ�������/���ߵ�ռ�ȵ�����������
		if(period.equals(StockGivenPeriodDataItem.DAY)) //�������߲�ѯ����
			; 
		else if(period.equals(StockGivenPeriodDataItem.MONTH)) //�������߲�ѯ����
			;
		
		NodeXPeriodDataBasic nodewkperioddata = bankuai.getNodeXPeroidData(period);
		
		String bkcode = bankuai.getMyOwnCode();
		String bkcjltable;
		String bkcys = bankuai.getSuoShuJiaoYiSuo();
		if(bkcys.toLowerCase().equals("sh"))
			bkcjltable = "ͨ���Ű��ÿ�ս�����Ϣ";
		else
			bkcjltable = "ͨ���Ž�����ָ��ÿ�ս�����Ϣ";
		
		String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart);
		String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend);
		
		//�����ɽ����ͳɽ����SQL
		String sqlquerystat = "SELECT YEAR(t.workday) AS CALYEAR, WEEK(t.workday) AS CALWEEK, M.BKCODE AS BKCODE, t.EndOfWeekDate AS EndOfWeekDate," +
				 "M.����ܽ��׶� as ����ܽ��׶�, SUM(T.AMO) AS �����ܽ��׶� ,  M.����ܽ��׶�/SUM(T.AMO) AS ռ��, \r\n" +

				"M.����ܽ����� as ����ܽ�����, SUM(T.VOL) AS �����ܽ����� ,  M.����ܽ�����/SUM(T.VOL) AS VOLռ�� \r\n" + 
				 
				"FROM\r\n" + 
				"(\r\n" + 
				"select  ͨ���Ű��ÿ�ս�����Ϣ.`��������` as workday, DATE(ͨ���Ű��ÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���Ű��ÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate, \r\n" + 
				"		  sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) AS AMO , sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) AS VOL \r\n" + 
				"from ͨ���Ű��ÿ�ս�����Ϣ\r\n" + 
				"where ���� = '999999'\r\n" + 
				"group by year(ͨ���Ű��ÿ�ս�����Ϣ.`��������`),week(ͨ���Ű��ÿ�ս�����Ϣ.`��������`)\r\n" + 
				"\r\n" + 
				"UNION ALL\r\n" + 
				"\r\n" + 
				"select  ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������` as workday,   DATE(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate, \r\n" + 
				"			sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`�ɽ���`) AS AMO, sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`�ɽ���`) AS VOL \r\n" + 
				"from ͨ���Ž�����ָ��ÿ�ս�����Ϣ\r\n" + 
				"where ���� = '399001'\r\n" + 
				"group by year(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������`),week(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������`)\r\n" + 
				") T,\r\n" + 
				"\r\n" + 
				"(select " + bkcjltable + ".`����` AS BKCODE, " + bkcjltable + ".`��������` as workday,  "
						+ "sum( " + bkcjltable + ".`�ɽ���`) AS ����ܽ��׶� , sum( " + bkcjltable + ".`�ɽ���`) AS ����ܽ����� from " + bkcjltable + "\r\n" +  
				"where ���� = '" + bkcode + "'\r\n" + 
				"GROUP BY YEAR( " + bkcjltable + ".`��������`), WEEK( " + bkcjltable +".`��������`)\r\n" + 
				") M\r\n" + 
				"WHERE YEAR(T.WORKDAY) = YEAR(M.WORKDAY) AND  WEEK(T.WORKDAY) = WEEK(M.WORKDAY)\r\n" + 
				"		AND T.WORKDAY BETWEEN'" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" +
				" GROUP BY year(t.workday),week(t.workday)"
				;
							
//		logger.debug(sqlquerystat);
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
			rsfx = connectdb.sqlQueryStatExecute(sqlquerystatfx);
			while(rsfx.next()) {
				java.sql.Date recdate = rsfx.getDate("����");
				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(recdate);
				Integer reccount = rsfx.getInt("RESULT");
				nodewkperioddata.addFxjgToPeriod(wknum, reccount);
			}
			
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			while(rs.next()) {
				Double bankuaicje = rs.getDouble("����ܽ��׶�");
				Double dapancje = rs.getDouble("�����ܽ��׶�");
				java.sql.Date lastdayofweek = rs.getDate("EndOfWeekDate");
				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(lastdayofweek);
				Double bankuaicjl = rs.getDouble("����ܽ�����");
				Double dapancjl = rs.getDouble("�����ܽ�����");
				
				StockGivenPeriodDataItem bkperiodrecord = new StockGivenPeriodDataItem( bkcode, StockGivenPeriodDataItem.WEEK,
						wknum, 0.0, 0.0,  0.0,  0.0, bankuaicje, bankuaicjl);
				
				bkperiodrecord.setRecordsDayofEndofWeek(lastdayofweek);
				bkperiodrecord.setUpLevelChengJiaoEr(dapancje);
				bkperiodrecord.setUplevelchengjiaoliang(dapancjl);
				
				nodewkperioddata.addNewXPeriodData(bkperiodrecord);
				
				bkperiodrecord = null;
				bankuaicje = null;
				dapancje = null;
				lastdayofweek = null;
				wknum = null;
				bankuaicjl = null;
				dapancjl = null;


				
			}
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	try {
				rs.close();
				rsfx.close();
				rs = null;
				rsfx = null;
				bkcjltable = null;
//				System.gc();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

		return bankuai;
	}

	/*
	 * ��Ʊ�԰��İ���ռ��
	 */
	public StockOfBanKuai getGeGuZhanBiOfBanKuai(BanKuai bkcode, StockOfBanKuai stock,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{//��������ʼ�ǿ���Ϊ�ܵ�ռ�ȣ�������/���ߵ�ռ�ȵ�����������
		if(period.equals(StockGivenPeriodDataItem.DAY)) //�������߲�ѯ����
			; 
		else if(period.equals(StockGivenPeriodDataItem.MONTH)) //�������߲�ѯ����
			;
		
		NodeXPeriodDataBasic nodedayperioddata = bkcode.getStockXPeriodDataForABanKuai(stock.getMyOwnCode(),period);
		
		String stockcode = stock.getMyOwnCode();
		
		HashMap<String, String> actiontables = this.getActionRelatedTables(bkcode,stockcode);
		String stockvsbktable = actiontables.get("��Ʊ����Ӧ��");
		String bkcjetable = actiontables.get("���ÿ�ս�������");
		String gegucjetable = actiontables.get("��Ʊÿ�ս�������");
		String bknametable = "ͨ���Ű���б�";
		
		String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart);
		String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend);
		
		String sqlquerystat = "";
		if(stockvsbktable.trim().equals("��Ʊͨ���Ż�������Ϣ��Ӧ��") ) { //�ҵ�����
			String dycode = actiontables.get("�������");
			sqlquerystat = "";
			return stock;
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
				"		and " +  stockvsbktable + ".`������` =  '"  + bkcode +"'\r\n" + 
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
				"		and " +  bkcjetable + ".`����` =  '"  + bkcode + "'\r\n" + 
				"group by year(" +  bkcjetable + ".`��������`), week(" +  bkcjetable + ".`��������`)," +  bkcjetable + ".`����`\r\n" + 
				"order by year(" +  bkcjetable + ".`��������`), week(" +  bkcjetable + ".`��������`), " +  bkcjetable + ".`����`) Z \r\n" + 
				"         \r\n" + 
				"where Y.year = Z. year\r\n" + 
				"    	and Y.week = Z.week\r\n" + 
				"order by Y.year, Y.week"
				;
		 }
//		logger.debug(sqlquerystat);
		logger.debug("Ϊ����" + stock.getMyOwnCode()  + "Ѱ�Ҵ�" + selecteddatestart.toString() + "��" + selecteddateend.toString() + "��" + bkcode + "��ռ�����ݣ�");
		

		CachedRowSetImpl rsfg = null;

		try {  
			 rsfg = connectdb.sqlQueryStatExecute(sqlquerystat);

			 while(rsfg.next()) {
				double stcokcje = rsfg.getDouble("stock_amount");
				double bkcje = rsfg.getDouble("category_amount");
				double stcokcjl = rsfg.getDouble("stock_vol");
				double bkcjl = rsfg.getDouble("category_vol");
				java.sql.Date  lastdayofweek = rsfg.getDate("EndOfWeekDate");
				org.jfree.data.time.Week recordwk = new org.jfree.data.time.Week (lastdayofweek);
				
				StockGivenPeriodDataItem stokofbkrecord = new StockGivenPeriodDataItem( stockcode, StockGivenPeriodDataItem.WEEK, recordwk, 
						  0.0,  0.0,  0.0,  0.0, stcokcje, stcokcjl);
				
				stokofbkrecord.setRecordsDayofEndofWeek(lastdayofweek);
				stokofbkrecord.setUpLevelChengJiaoEr(bkcje);
				stokofbkrecord.setUplevelchengjiaoliang(bkcjl);
			
				nodedayperioddata.addNewXPeriodData(stokofbkrecord);
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
		
		return stock;

	}
	/*
	 * //�����Ǹ��ɶ԰����ƶ�����(Ĭ��������)�����沿����Ϊ���������������ݣ�����������ɶԴ��̵�����
	 */
	public Stock getStockZhanBi(Stock stock,LocalDate selecteddatestart,LocalDate selecteddateend,String period)
	{	
		if(period.equals(StockGivenPeriodDataItem.DAY)) //�������߲�ѯ����
			; 
		else if(period.equals(StockGivenPeriodDataItem.MONTH)) //�������߲�ѯ����
			;
		
		NodeXPeriodDataBasic nodewkperioddata = stock.getNodeXPeroidData(period);
	
		String stockcode = stock.getMyOwnCode();
		String bkcjltable;
		if(stockcode.startsWith("6"))
			bkcjltable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
		else
			bkcjltable = "ͨ���������Ʊÿ�ս�����Ϣ";
		
		String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart);
		String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend);
		
		//�����ɽ����ͳɽ����SQL
		String sqlquerystat = "SELECT YEAR(t.workday) AS CALYEAR, WEEK(t.workday) AS CALWEEK, M.BKCODE AS BKCODE, t.EndOfWeekDate AS EndOfWeekDate," +
				 "M.����ܽ��׶� as ����ܽ��׶�, SUM(T.AMO) AS �����ܽ��׶� ,  M.����ܽ��׶�/SUM(T.AMO) AS ռ��, \r\n" +
	
				"M.����ܽ����� as ����ܽ�����, SUM(T.VOL) AS �����ܽ����� ,  M.����ܽ�����/SUM(T.VOL) AS VOLռ�� \r\n" + 
				 
				"FROM\r\n" + 
				"(\r\n" + 
				"select  ͨ���Ű��ÿ�ս�����Ϣ.`��������` as workday, DATE(ͨ���Ű��ÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���Ű��ÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate, \r\n" + 
				"		  sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) AS AMO , sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) AS VOL \r\n" + 
				"from ͨ���Ű��ÿ�ս�����Ϣ\r\n" + 
				"where ���� = '999999'\r\n" + 
				"group by year(ͨ���Ű��ÿ�ս�����Ϣ.`��������`),week(ͨ���Ű��ÿ�ս�����Ϣ.`��������`)\r\n" + 
				"\r\n" + 
				"UNION ALL\r\n" + 
				"\r\n" + 
				"select  ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������` as workday,   DATE(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������)) DAY) as EndOfWeekDate, \r\n" + 
				"			sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`�ɽ���`) AS AMO, sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`�ɽ���`) AS VOL \r\n" + 
				"from ͨ���Ž�����ָ��ÿ�ս�����Ϣ\r\n" + 
				"where ���� = '399001'\r\n" + 
				"group by year(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������`),week(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������`)\r\n" + 
				") T,\r\n" + 
				"\r\n" + 
				"(select " + bkcjltable + ".`����` AS BKCODE, " + bkcjltable + ".`��������` as workday,  "
						+ "sum( " + bkcjltable + ".`�ɽ���`) AS ����ܽ��׶� , sum( " + bkcjltable + ".`�ɽ���`) AS ����ܽ����� from " + bkcjltable + "\r\n" +  
				"where ���� = '" + stockcode + "'\r\n" + 
				"GROUP BY YEAR( " + bkcjltable + ".`��������`), WEEK( " + bkcjltable +".`��������`)\r\n" + 
				") M\r\n" + 
				"WHERE YEAR(T.WORKDAY) = YEAR(M.WORKDAY) AND  WEEK(T.WORKDAY) = WEEK(M.WORKDAY)\r\n" + 
				"		AND T.WORKDAY BETWEEN'" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" +
				" GROUP BY year(t.workday),week(t.workday)"
				;
							
	//	logger.debug(sqlquerystat);
		logger.debug("Ϊ���" + stock.getMyOwnCode() + stock.getMyOwnName() + "Ѱ�Ҵ�" + selecteddatestart.toString() + "��" + selecteddateend.toString() + "ռ�����ݣ�");
		
		String sqlquerystatfx = "SELECT ������¼�ص��ע.`����`, COUNT(*) AS RESULT FROM ������¼�ص��ע \r\n" + 
				" WHERE ��Ʊ����='" + stockcode + "'" + "\r\n" + 
				" AND ������¼�ص��ע.`����` between '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n" +
				" GROUP BY YEAR(������¼�ص��ע.`����`), WEEK(������¼�ص��ע.`����`) "
				;
		
		CachedRowSetImpl rs = null;
		CachedRowSetImpl rsfx = null;
		Boolean hasfengxiresult = false;
		try {
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			rsfx = connectdb.sqlQueryStatExecute(sqlquerystatfx);
			while(rsfx.next()) {
				java.sql.Date recdate = rsfx.getDate("����");
				org.jfree.data.time.Week wknum = new org.jfree.data.time.Week(recdate);
				Integer reccount = rsfx.getInt("RESULT");
				nodewkperioddata.addFxjgToPeriod(wknum, reccount);
			}
			 
			while(rs.next()) {
				double bankuaicje = rs.getDouble("����ܽ��׶�");
				double dapancje = rs.getDouble("�����ܽ��׶�");
				java.sql.Date lastdayofweek = rs.getDate("EndOfWeekDate");
				org.jfree.data.time.Week recordwk = new org.jfree.data.time.Week (lastdayofweek);
				double bankuaicjl = rs.getDouble("����ܽ�����");
				double dapancjl = rs.getDouble("�����ܽ�����");

				StockGivenPeriodDataItem stokofbkrecord = new StockGivenPeriodDataItem( stockcode, period, recordwk, 
						  0.0,  0.0,  0.0,  0.0, bankuaicje, bankuaicjl);
				
				stokofbkrecord.setRecordsDayofEndofWeek(lastdayofweek);
				stokofbkrecord.setUpLevelChengJiaoEr(dapancje);
				stokofbkrecord.setUplevelchengjiaoliang(dapancjl);
				
				nodewkperioddata.addNewXPeriodData(stokofbkrecord);
				
				stokofbkrecord = null;
			}
		}catch(java.lang.NullPointerException e){ 
	    	e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }  finally {
	    	try {
				rs.close();
				rsfx.close();
				rs = null;
				rsfx = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	
	    	bkcjltable = null;
	    	nodewkperioddata = null; 
	    }

		return stock;
	}
	
	/*
	 * ��ȡ���ɻ���ĳʱ��ε���������
	 */
	public Stock getNodeKXianZouShi(Stock stock, LocalDate nodestartday, LocalDate nodeendday, String period) 
	{//��������ʼ�ǿ���Ϊ�յ�K�����ݣ�������/���ߵ�ռ�ȵ�����������
		if(period.equals(StockGivenPeriodDataItem.WEEK)) //�������߲�ѯ����
			; 
		else if(period.equals(StockGivenPeriodDataItem.MONTH)) //�������߲�ѯ����
			;
		
		NodeXPeriodDataBasic nodedayperioddata = stock.getNodeXPeroidData(period);
		
		String nodecode = stock.getMyOwnCode();
		String jys = stock.getSuoShuJiaoYiSuo();
		String searchtable;
		if(jys.equals("sh"))
			searchtable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
		else
			searchtable = "ͨ���������Ʊÿ�ս�����Ϣ";
		
		String sqlquerystat = "SELECT *  FROM " + searchtable + " \r\n" + 
				"WHERE ����='" + nodecode + "'" + "\r\n" + 
				"AND ��������  between'" + nodestartday + "' AND '" + nodeendday + "'"
				;
		
		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
		try {  
			 rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			 while(rsfx.next()) {
				 double open = rsfx.getDouble("���̼�");
				 double high = rsfx.getDouble("��߼�");
				 double low = rsfx.getDouble("��ͼ�");
				 double close = rsfx.getDouble("���̼�");
				 double cje = rsfx.getDouble("�ɽ���");
				 double cjl = rsfx.getDouble("�ɽ���");
				 java.sql.Date actiondate = rsfx.getDate("��������");
				 org.jfree.data.time.Day recordwk = new org.jfree.data.time.Day (actiondate);
				 
				 StockGivenPeriodDataItem stokofbkrecord = new StockGivenPeriodDataItem( nodecode, period, recordwk, 
						 open,  high,  low,  close, cje, cjl);
				 
				 stokofbkrecord.setRecordsDayofEndofWeek(actiondate);
				 nodedayperioddata.addNewXPeriodData(stokofbkrecord);
				 
				 stokofbkrecord = null;
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
				if(rsfx != null)
					rsfx.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rsfx = null;
			searchtable = null;
		}

		return stock;
	}
	
	/*
	 * ��ȡĳ���ڵ���ָ���ܵ����й�ע�����Ƚ��
	 */
	public ArrayList<JiaRuJiHua> getZdgzFxjgForANodeOfGivenPeriod (String nodecode, LocalDate givenwk, String period)
	{
		String sqlquerystat = "SELECT *  FROM ������¼�ص��ע \r\n" + 
				"WHERE ��Ʊ����='" + nodecode + "'" + "\r\n" + 
				"AND WEEK(������¼�ص��ע.`����`) = WEEK('" + givenwk + "')" 
				;
		logger.debug(sqlquerystat);
		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
		
		ArrayList<JiaRuJiHua> jrjh = new ArrayList<JiaRuJiHua> ();
		try {
			while(rsfx.next()) {
				String acttype = rsfx.getString("�����Ƴ���־");
				JiaRuJiHua guanzhu = new JiaRuJiHua(nodecode,acttype); 
				
				java.sql.Date  lastdayofweek = rsfx.getDate("����");
				LocalDate actiondate = lastdayofweek.toLocalDate(); 
				guanzhu.setJiaRuDate (actiondate );
				String shuoming = rsfx.getString("ԭ������");
				guanzhu.setiHuaShuoMing(shuoming);
				jrjh.add(guanzhu);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			try {
				if(rsfx != null)
					rsfx.close();
				rsfx.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rsfx = null;
		}
		
		if(jrjh.size() == 0)
			jrjh = null;
		return jrjh;
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
//		List<String> volamooutput = sysconfig.getTDXVOLFilesPath();
//		String exportath = sysconfig.toUNIXpath(Splitter.on('=').trimResults().omitEmptyStrings().splitToList(volamooutput.get(0) ).get(1) );
//		String filenamerule = Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(1)).get(1);
//		String dateRule = getTDXDateExportDateRule(Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(2)).get(1));
		
		ArrayList<String> allgegucode = new ArrayList<String>( );
		CachedRowSetImpl  rsdm = null;
		try { 	
			String sqlquerystat = "SELECT  ��Ʊ����  FROM A��  WHERE      ifnull(������,1 )  or ������ = false "
						;
			logger.debug(sqlquerystat);
		    	rsdm = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	while(rsdm.next()) {
		    		 String ggcode = rsdm.getString("��Ʊ����"); //mOST_RECENT_TIME
		    		 if( (ggcode.startsWith("00") || ggcode.startsWith("30") ) && jiaoyisuo.toLowerCase().equals("sz")) //ֻ�����й�Ʊ
		    			 allgegucode.add(ggcode);
		    		 else if( (ggcode.startsWith("6") ) && jiaoyisuo.toLowerCase().equals("sh") ) //ֻ�滦�й�Ʊ
		    			 allgegucode.add(ggcode);
		    	}
		    } catch(java.lang.NullPointerException e) { 
		    	e.printStackTrace();
		    } catch (SQLException e) {
		    	e.printStackTrace();
		    }catch(Exception e){
		    	e.printStackTrace();
		    } finally {
		    	if(rsdm != null)
				try {
					rsdm.close();
					rsdm = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    }
		
		String optTable = null;
		if(jiaoyisuo.toLowerCase().equals("sz")  )
			optTable = "ͨ���������Ʊÿ�ս�����Ϣ";
		else if(jiaoyisuo.toLowerCase().equals("sh") )
			optTable = "ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ";
		for(String tmpbkcode:allgegucode) {
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

				CachedRowSetImpl  rs = null;
				LocalDate ldlastestdbrecordsdate = null;
				try { 				
					String sqlquerystat = "SELECT  MAX(��������) 	MOST_RECENT_TIME"
							+ " FROM "+ optTable +  " WHERE  ���� = " 
   							+ "'"  + tmpbkcode + "'" 
   							;
					logger.debug(sqlquerystat);
   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   			    	while(rs.next()) {
//   			    		logger.debug(rs.getMetaData().getColumnCount());
   			    		 java.sql.Date lastestdbrecordsdate = rs.getDate("MOST_RECENT_TIME"); //mOST_RECENT_TIME 
   			    		 try {
   			    			 ldlastestdbrecordsdate =lastestdbrecordsdate.toLocalDate();
   			    		 } catch (java.lang.NullPointerException e) {
   			    			 logger.info(tmpbkcode + "�ƺ�û�����ݣ����飡");
   			    		 }
//   			    		 Instant instant = Instant.ofEpochMilli(lastestdbrecordsdate.getTime());
//   			    		ldlastestdbrecordsdate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
//   			    		ldlastestdbrecordsdate = lastestdbrecordsdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
   			    	}
   			    } catch(java.lang.NullPointerException e) { 
   			    	e.printStackTrace();
   			    } catch (SQLException e) {
   			    	e.printStackTrace();
   			    }catch(Exception e){
   			    	e.printStackTrace();
   			    } finally {
   			    	if(rs != null)
						try {
							rs.close();
							rs = null;
						} catch (SQLException e) {
							e.printStackTrace();
						}
   			    	allgegucode = null;
   			    }
				
				setVolAmoRecordsFromFileToDatabase(tmpbkcode,tmpbkfile,ldlastestdbrecordsdate,optTable,dateRule,tmprecordfile);

		}
		
		return tmprecordfile;
	}
	/*
	 * �޸İ����ĳ�����ɵ�Ȩ��
	 */
	public void setStockWeightInBanKuai(BanKuai bankuai, String bkname, String stockcode, int weight) 
	{
		String bkcode = bankuai.getMyOwnCode();
		HashMap<String, String> actiontables = this.getActionRelatedTables(bankuai, stockcode);
		String bktypetable = actiontables.get("��Ʊ����Ӧ��");
		String bkorzsvoltable = actiontables.get("���ÿ�ս�������");
		String bknametable = actiontables.get("���ָ�����Ʊ�");
		String sqlupdatestat = "update " + bktypetable  + " set ��ƱȨ�� = " +  weight  + 
				" where ������ = '" + bkcode + "' AND ��Ʊ���� = '" + stockcode + "'" 
				;
		logger.debug(sqlupdatestat);
		connectdb.sqlUpdateStatExecute(sqlupdatestat);
		
		actiontables = null;
		
		//��Ϊ��֪��bkcode���ĸ������棬
//		String sqlupdatestat1 = "update ��Ʊͨ���Ž�����ָ����Ӧ��  set ��ƱȨ�� = " +  weight  + 
//							" where ָ����� = '" + bkcode + "' AND ��Ʊ���� = '" + stockcode + "' AND ISNULL(�Ƴ�ʱ��)" 
//							;
//		String sqlupdatestat2 = "update ��Ʊͨ���Ÿ������Ӧ��  set ��ƱȨ�� = " +  weight  + 
//							" where ������ = '" + bkcode + "' AND ��Ʊ���� = '" + stockcode + "' AND ISNULL(�Ƴ�ʱ��)"
//							;
//		String sqlupdatestat3 = "update ��Ʊͨ������ҵ����Ӧ��  set ��ƱȨ�� = " +  weight  + 
//							" where ��ҵ��� = '" + bkcode + "' AND ��Ʊ���� = '" + stockcode + "' AND ISNULL(�Ƴ�ʱ��)"
//							;
//		String sqlupdatestat4 = "update ��Ʊͨ���ŷ�����Ӧ��  set ��ƱȨ�� = " +  weight  + 
//							" where ����� = '" + bkcode + "' AND ��Ʊ���� = '" + stockcode + "' AND ISNULL(�Ƴ�ʱ��)"
//							;
//		//��ɵ��ʵ��
//		connectdb.sqlUpdateStatExecute(sqlupdatestat1);
//		connectdb.sqlUpdateStatExecute(sqlupdatestat2);
//		connectdb.sqlUpdateStatExecute(sqlupdatestat3);
//		connectdb.sqlUpdateStatExecute(sqlupdatestat4);
	}
	
	/*
	 * 
	 */
	public File refreshTDXZDYBanKuai (HashMap<String, String> neededimportedzdybkmap)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "ͬ��ͨ�����Զ����鱨��.txt");
		
		 //׼��XML�ļ�
//		File sysconfigfile = new File(bankuaichanyelianxml );
//		if(!sysconfigfile.exists()) { //�����ڣ��������ļ�	
//			try {
//				sysconfigfile.createNewFile();
//				logger.debug(bankuaichanyelianxml + "�����ڣ��Ѿ�����");
//			} catch (IOException e) {
//				logger.debug(bankuaichanyelianxml + "�����ڣ�����ʧ�ܣ�");
//			}
//		}
		
		this.refreshTDXZiDingYiBanKuai (neededimportedzdybkmap,tmprecordfile);
		this.refreshTDXZiDingYiBanKuaiToGeGu(neededimportedzdybkmap, tmprecordfile);
		
		return tmprecordfile;
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
	 * ����ͨ���ŵ��Զ�����
	 */
	private int refreshTDXZiDingYiBanKuai (HashMap<String, String> neededimportedzdybkmap, File tmprecordfile)
	{
		Set<String> neededimportzdybknames = neededimportedzdybkmap.keySet(); 
		Set<String> curzdybknames = this.initializeSystemAllZdyBanKuaiList().keySet();
		
		 //���µ��Զ�����������ݿ�
	      SetView<String> differencebankuainew = Sets.difference(neededimportzdybknames, curzdybknames ); 
		    for (String str : differencebankuainew) {
				String sqlinsertstat = "INSERT INTO  ͨ�����Զ������б�(�������,����ʱ��) values ("
						+ "'" + str.trim() + "'" + ","
						+ "\"" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now())   + "\""  
						+ ")"
						;
				//logger.debug(sqlinsertstat);
				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
				//tfldresult.append("���룺" + str.trim() + " " + gupiaoheader + "\n");
			}
		    
	        //�Ѿɵ��Զ�����ɾ�������ݿ�
	        SetView<String> differencebankuaiold = Sets.difference(curzdybknames,neededimportzdybknames  );
	        for (String str : differencebankuaiold) {
	        	String sqldeletetstat = "DELETE  FROM ͨ�����Զ������б� "
	        							+ " WHERE �������=" + "'" + str.trim() + "'" 
	        							;
	        	//logger.debug(sqldeletetstat);
	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
	    		//tfldresult.append("ɾ����" + str.trim() + " " + gupiaoheader + "\n");
	    		
	    		sqldeletetstat = "DELETE  FROM ��Ʊͨ�����Զ������Ӧ�� "
						+ " WHERE �Զ�����=" + "'" + str.trim() + "'" 
						;
				//logger.debug(sqldeletetstat);
				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
	        }
	        
	        neededimportzdybknames = null;
	        curzdybknames = null;
	        differencebankuaiold = null;
	        
	        return 1;
	}
	/*
	 * �����Զ�����͸��ɵĶ�Ӧ��ϵ
	 */
	private int refreshTDXZiDingYiBanKuaiToGeGu (HashMap<String, String> neededimportedzdybkmap, File tmprecordfile) 
	{
		Set<String> neededimportzdybknames = neededimportedzdybkmap.keySet(); 
		//Set<String> curzdybknames = this.getZdyBkSet ();
		
		for(String newzdybk:neededimportzdybknames) {
			String filename = neededimportedzdybkmap.get(newzdybk); //str���Զ����������
			File zdybk = new File(filename);
			List<String> readLines = null;
			try {
				readLines = Files.readLines(zdybk,Charsets.UTF_8,new TDXBanKuaiFielProcessor ());
			} catch (IOException e) {
				e.printStackTrace();
			}
			Set<String> stocknamesnew = new HashSet<String>(readLines);
			
			Set<String> tmpstockcodesetold = new HashSet<String>();
			CachedRowSetImpl rs = null;
			try {
			    	String sqlquerystat = "SELECT *  FROM ��Ʊͨ�����Զ������Ӧ��  WHERE  �Զ����� = " 
			    							+ "'"  + newzdybk.trim() + "'";
			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			        rs.last();  
			        int rows = rs.getRow();  
			        rs.first();  
			        for(int j=0;j<rows;j++) {  
			        	String stockcode = rs.getString("��Ʊ����");
			        	tmpstockcodesetold.add(stockcode);
			        	
			            rs.next();
			        }
			       
			    }catch(java.lang.NullPointerException e){ 
			    	e.printStackTrace();
			    } catch (SQLException e) {
			    	e.printStackTrace();
			    }catch(Exception e){
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
			
			 //��tmpstockcodesetnew�����еģ�tmpstockcodesetoldû�е�ѡ���������µģ�Ҫ�������ݿ�
		    SetView<String> differencebankuainew = Sets.difference(stocknamesnew, tmpstockcodesetold ); 
   		    for (String newstock : differencebankuainew) {
   				
   				String sqlinsertstat = "INSERT INTO  ��Ʊͨ�����Զ������Ӧ��(��Ʊ����,�Զ�����) values ("
   						+ "'" + newstock.trim() + "'" + ","
   						+ "'" + newzdybk.trim() + "'" 
   						+ ")"
   						;
   				//logger.debug(sqlinsertstat);
   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
   				//tfldresult.append("���룺" + str.trim() + " " + gupiaoheader + "\n");
   			}
   		    
   	        //�� tmpstockcodesetold �����еģ�tmpstockcodesetnewû�е�ѡ�������Ǿɵģ�Ҫ�����ݿ���ɾ��
   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,stocknamesnew  );
   	        for (String oldstock : differencebankuaiold) {
   	        	String sqldeletetstat = "DELETE  FROM ��Ʊͨ�����Զ������Ӧ�� "
   	        							+ " WHERE ��Ʊ����=" +  "'" + oldstock.trim() + "'"
   	        							+ " AND �����=" + "'" + newzdybk.trim() + "'"
   	        							;
   	        	//logger.debug(sqldeletetstat);
   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
   	    		//tfldresult.append("���룺" + str.trim() + " " + gupiaoheader + "\n");
   	        }
		}
		
		neededimportzdybknames = null;
		return 0;
	}
	
	/*
	 * �Զ�������Щ������Ҫ���룬�û�ѡ������Щ�Զ�����
	 */
	public HashMap<String, String> getTDXZiDingYiBanKuaiList ()
	{
		File file = new File(sysconfig.getTDXSysZDYBanKuaiFile () );
		 
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
		
		 return zdybkmap;
	}
		
	//private Object[][] zdgzmrmcykRecords = null;
		public BkChanYeLianTreeNode getZdgzMrmcZdgzYingKuiFromDB (BkChanYeLianTreeNode stockbasicinfo)
		{
			HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
			String sqlquerystat = null;
			sqlquerystat = getZdgzMrmcYingKuiSQLForMysql (stockbasicinfo);
			sqlstatmap.put("mysql", sqlquerystat);
			
//			sqlquerystat = getZdgzMrmcYingKuiSQLForAccess (stockbasicinfo);
//			sqlstatmap.put("access", sqlquerystat);
	     
			 CachedRowSetImpl rs = null;
			 rs = connectdb.sqlQueryStatExecute(sqlstatmap);
			 stockbasicinfo.getNodeJiBenMian().setZdgzmrmcykRecords( setZdgzMrmcZdgzYingKuiRecords (rs) );
			 
			 try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			 } 
			
			 return stockbasicinfo;
		}
		
		private String getZdgzMrmcYingKuiSQLForAccess(Stock stockbasicinfo) 
		{
			String stockcode = stockbasicinfo.getUserObject().toString();
			String sqlquerystat1= "SELECT ggyk.����, "
								+ "IIF(ggyk.ӯ�����>0,'ӯ��','����') AS ӯ�����,"
								+ " ggyk.ԭ������,"
								+ " ggyk.ID,"
								+ " ggyk.�����˺�,"
								+  "'����ӯ��'" 
								+ " FROM  A�ɸ���ӯ�� ggyk "
								+ " WHERE ggyk.��Ʊ���� =" + "'" + stockcode + "'" 
								
								+ " UNION ALL " 
								;
			//logger.debug(sqlquerystat1);
	       String sqlquerystat2="SELECT czjl.����, " +		""
								+ "IIF( czjl.�������=0.0,'��ת��',IIF(czjl.�ҵ� = true,IIF(czjl.����������־, '�ҵ�����', '�ҵ�����'),IIF(czjl.����������־,'����','����') )  ) AS ����,"
								+ " czjl.ԭ������,"
								+ " czjl.ID,"
								+ " czjl.�����˺�,"
								+ "'������¼����'" 
								+ " FROM ������¼���� czjl "
								+ "	WHERE czjl.��Ʊ���� =" + "'" + stockcode + "'"
								
								+ " UNION ALL " 
								;
	       //logger.debug(sqlquerystat2);
	      String sqlquerystat3="SELECT rqczjl.����,"
								+ "IIF( rqczjl.�������=0.0,'��ת��',IIF(rqczjl.�ҵ� = true,IIF(rqczjl.����������־, '�ҵ�����', '�ҵ�����'),IIF(rqczjl.����������־,'����','����') )  ) AS ����,"
								+ "rqczjl.ԭ������,"
								+ "rqczjl.ID,"
								+ "rqczjl.�����˺�,"
								+ "'������¼��ȯ����'"
								+ " FROM ������¼��ȯ���� rqczjl"
								+ "	WHERE rqczjl.��Ʊ���� =" + "'" + stockcode + "'"
								
								+ " UNION  ALL "
								;
	      //logger.debug(sqlquerystat3);
		 String sqlquerystat4="SELECT rzczjl.����,"
								+ "IIF( rzczjl.�������=0.0,'��ת��',IIF(rzczjl.�ҵ� = true,IIF(rzczjl.����������־, '�ҵ�����', '�ҵ�����'),IIF(rzczjl.����������־,'����','����') )  ) AS ����,"
								+ "rzczjl.ԭ������,"
								+ "rzczjl.ID,"
								+ "rzczjl.�����˺�,"
								+ "'������¼��������'"
								+ " FROM ������¼�������� rzczjl"
								+ " WHERE rzczjl.��Ʊ���� =" + "'" + stockcode + "'"
								
								+ " UNION ALL "
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
								+ " ORDER BY 1 DESC,5,3 " //desc
								;
	     //logger.debug(sqlquerystat5);
		
	     String sqlquerystat = sqlquerystat1 + sqlquerystat2 + sqlquerystat3 + sqlquerystat4 + sqlquerystat5;
	     //logger.debug(sqlquerystat);
	     
	     return sqlquerystat;
			
		}

		private String getZdgzMrmcYingKuiSQLForMysql(BkChanYeLianTreeNode stockbasicinfo) 
		{
			String stockcode = stockbasicinfo.getMyOwnCode();
			String sqlquerystat1= "SELECT ggyk.����, "
								+ "IF(ggyk.ӯ�����>0,'ӯ��','����') AS ӯ�����,"
								+ " ggyk.ԭ������,"
								+ " ggyk.ID,"
								+ " ggyk.�����˺�,"
								+  "'����ӯ��'" 
								+ " FROM  A�ɸ���ӯ�� ggyk "
								+ " WHERE ggyk.��Ʊ���� =" + "'" + stockcode + "'" 
								
								+ " UNION ALL " 
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
								
								+ " UNION ALL " 
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
								
								+ " UNION  ALL "
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
								
								+ " UNION ALL "
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
								+ " ORDER BY 1 DESC,5,3 " //desc
								;
	     //logger.debug(sqlquerystat5);
		
	     String sqlquerystat = sqlquerystat1 + sqlquerystat2 + sqlquerystat3 + sqlquerystat4 + sqlquerystat5;
	     //logger.debug(sqlquerystat);
	     
	     return sqlquerystat;
		}

		/*
		 * ��ú͸ù�Ʊ��ص�������Ϣ
		 */
		private Object[][] setZdgzMrmcZdgzYingKuiRecords(CachedRowSetImpl rs) 
		{
			String[][] data = null;  
		    try  {
		    	int k = 0;
		        int columnCount = 6;//����
		        
		        rs.last();  
		        int rows = rs.getRow();  
		        data = new String[rows][];
		        rs.first();  
		        //while(rs.next())  //{ "����", "����", "˵��","ID","�����˻�","��Ϣ��" };
		        for(int j=0;j<rows;j++) {   //{ "����", "����", "˵��","ID","�����˻�","��Ϣ��" };
		            String[] row = new String[columnCount];  
		            row[0] = rs.getTimestamp(1).toString();
		            if(row[0].contains("."))
		            	row[0] = row[0].substring(0, row[0].length()-2);
		            row[1] = rs.getString(2);
		            row[2] = rs.getString(3);
		            row[3] = rs.getString(4);
		            row[4] = rs.getString(5);
		            row[5] = rs.getString(6);
	              
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
		
		public String getMingRiJiHua() 
		{
			HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
			String sqlquerystat = null;
			sqlquerystat= 	" SELECT * FROM ������¼�ص��ע   \r\n" + 
							" WHERE ���� >= date_sub(curdate(),interval 30 day) \r\n" + 
							"		and ���� <= curdate()\r\n" + 
							" AND �����Ƴ���־ = '���ռƻ�' " 
							;
			sqlstatmap.put("mysql", sqlquerystat);
			
//			sqlquerystat=  "SELECT * FROM ������¼�ص��ע "
//					+ " WHERE ���� <  DATE() AND  ���� > IIF( Weekday( date() ) =  2,date()-3,date()-1)  "
//					+ "AND �����Ƴ���־ = '���ռƻ�'"
//					;
//			sqlstatmap.put("access", sqlquerystat);
			
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
		

		
		

//		private boolean getANewDtockCodeIndicate(CachedRowSetImpl rs) 
//		{
//			boolean newStockSign = false;
//			try	{   
//				rs.next();
//				if(0 == rs.getRow()) {
//					newStockSign= true;
//				} else {
//					newStockSign= false;
//				}
//				rs.first();
//			} catch(Exception e) {
//				JOptionPane.showMessageDialog(null,"ConnectDatabase -> setANewDtockCodeIndicate ����Ԥ֪�Ĵ���");
//				logger.debug(""+e);
//			} 		
//			
//			return newStockSign;
//		}

		public Stock getCheckListsXMLInfo(Stock stockbasicinfo)
		{
			String stockcode = stockbasicinfo.getMyOwnCode();
			String sqlquerystat= "SELECT checklistsitems FROM A��   WHERE ��Ʊ���� =" +"'" + stockcode +"'" ;
			//logger.debug(sqlquerystat);
			CachedRowSetImpl rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);

			try {
				rsagu.first();
				while (rsagu.next()) {
					String checklistsitems = rsagu.getString("checklistsitems");
					stockbasicinfo.setChecklistXml(checklistsitems);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				rsagu.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return stockbasicinfo;
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
		public boolean updateStockNewInfoToDb(BkChanYeLianTreeNode nodeshouldbedisplayed) 
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
		    
		    String txtfldinputzhengxiangguan = "'" + nodeshouldbedisplayed.getNodeJiBenMian().getZhengxiangguan().trim() + "'";
		    String txtfldinputfuxiangguan = "'" + nodeshouldbedisplayed.getNodeJiBenMian().getFuxiangguan().trim() + "'";
		    
		    String keHuCustom = nodeshouldbedisplayed.getNodeJiBenMian().getKeHuCustom();
		    String jingZhengDuiShou = nodeshouldbedisplayed.getNodeJiBenMian().getJingZhengDuiShou();
	    
			 if(nodeshouldbedisplayed.getType() == 6) {//�ǹ�Ʊ{
				 HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
				 String sqlupdatestat= "UPDATE A��  SET "
							+ " ��Ʊ����=" + stockname +","
							+ " ����ʱ��=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getGainiantishidate()) ) +","
							+ " ����������=" + txtareainputgainiants +","
							+ " ȯ������ʱ��=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getQuanshangpingjidate() ) ) +","
							+ " ȯ����������=" + txtfldinputquanshangpj +","
							+ " ������Ϣʱ��=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getNodeJiBenMian().getFumianxiaoxidate()  ) ) +","
							+ " ������Ϣ=" + txtfldinputfumianxx +","
							+ " ����ؼ��ͻ�=" + txtfldinputzhengxiangguan +","
							+ " ����ؼ���������=" + txtfldinputfuxiangguan +","
							+ " �ͻ�=" + "'" + keHuCustom +"'" + ","
							+ " ��������=" + "'" + jingZhengDuiShou + "'"  
							+ " WHERE ��Ʊ����=" + stockcode
							;
				 sqlstatmap.put("mysql", sqlupdatestat);
				 //logger.debug(sqlupdatestat);
				 
				 if( connectdb.sqlUpdateStatExecute(sqlupdatestat) !=0)
					 return true;
				 else return false;
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
							+ " ������Ϣ=" + txtfldinputfumianxx +","
							+ " ����ؼ��ͻ�=" + txtfldinputzhengxiangguan +","
							+ " ����ؼ���������=" + txtfldinputfuxiangguan +","
							+ " �ͻ�=" + "'" + keHuCustom +"'" + ","
							+ " ��������=" + "'" + jingZhengDuiShou + "'"  
							+ " WHERE ���ID=" + stockcode
							;
				 //logger.debug(sqlinsertstat); 
				 sqlstatmap.put("mysql", sqlinsertstat);
				 if( connectdb.sqlUpdateStatExecute(sqlinsertstat) !=0 ) {
					 return true;
				 } else return false;
			 }
		}


		public boolean updateChecklistsitemsToDb (Stock stockbasicinfo)
		{
			String stockcode = stockbasicinfo.getMyOwnCode();
			String checklistsitems = stockbasicinfo.getChecklistXml();
			String sqlupdatestat= "UPDATE A��  SET "
					+ "checklistsitems=" + "'" + checklistsitems + "'"
					+ "WHERE ��Ʊ����=" + stockcode
					
					;
		// logger.debug(sqlupdatestat);
		 connectdb.sqlUpdateStatExecute(sqlupdatestat);
		 return true;
		}

		public int setZdgzRelatedActions(JiaRuJiHua jiarujihua) 
		{
			String stockcode = jiarujihua.getStockCode();		
			String zdgzsign = jiarujihua.getGuanZhuType();
			String shuoming = "";
			LocalDate actiondate = jiarujihua.getJiaRuDate();
			
			if(jiarujihua.isMingRiJiHua()) {
				zdgzsign = "���ռƻ�";
				shuoming = jiarujihua.getJiHuaLeiXing() + "(�۸�" + jiarujihua.getJiHuaJiaGe() + ")(" +  jiarujihua.getJiHuaShuoMing();
			} else
				shuoming =  jiarujihua.getJiHuaShuoMing();
			
			HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
			String sqlinsertstat = "INSERT INTO ������¼�ص��ע(��Ʊ����,����,�����Ƴ���־,ԭ������) values ("
					+ "'" +  stockcode.trim() + "'" + "," 
					+ "'" + actiondate + "'" + ","
					+  "'" + zdgzsign + "'" + ","
					+ "'" + shuoming + "'"  
					+ ")"
					;
			//logger.debug(sqlinsertstat);
			sqlstatmap.put("mysql", sqlinsertstat);
			int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlstatmap);
			return autoIncKeyFromApi;
		}
		
		public Stock getTDXBanKuaiForAStock(Stock stockbasicinfo) 
		{
			String stockcode = stockbasicinfo.getMyOwnCode();
			HashMap<String,String> tmpsysbk = new HashMap<String,String> ();
			
//			SELECT gpgn.������, tdxbk.`���ID`
//			FROM ��Ʊͨ���Ÿ������Ӧ�� gpgn, ͨ���Ű���б� tdxbk
//			WHERE ��Ʊ����= '000001' AND gpgn.������ = tdxbk.`�������`
			String sqlquerystat =null;
			sqlquerystat=  "SELECT gpgn.������ ������, tdxbk.`�������` ������� FROM ��Ʊͨ���Ÿ������Ӧ�� gpgn, ͨ���Ű���б� tdxbk"
					+ "  WHERE ��Ʊ����=" + "'" +  stockcode.trim() + "'" + "AND gpgn.������ = tdxbk.`���ID` AND ISNULL(�Ƴ�ʱ��)"
					+ "UNION " 
					+ " SELECT gphy.������ ������, tdxbk.`�������` ������� FROM ��Ʊͨ������ҵ����Ӧ�� gphy, ͨ���Ű���б� tdxbk "
					+ " WHERE ��Ʊ����=" + "'" +  stockcode.trim() + "'" + "AND gphy.`������` = tdxbk.`���ID` AND ISNULL(�Ƴ�ʱ��)"
					+ "UNION " 
					+ " SELECT gpfg.`������` ������, tdxbk.`�������` �������  FROM ��Ʊͨ���ŷ�����Ӧ�� gpfg, ͨ���Ű���б� tdxbk"
					+ "  WHERE ��Ʊ����= "+ "'" +  stockcode.trim() + "'" + "AND gpfg.`������` = tdxbk.`���ID` AND ISNULL(�Ƴ�ʱ��)"
					;
			
			logger.debug(sqlquerystat);
			CachedRowSetImpl rs_gn = connectdb.sqlQueryStatExecute(sqlquerystat);
			try  {     
		        while(rs_gn.next()) {
		        	String bkcode = rs_gn.getString("������");
		        	String bkname = rs_gn.getString("�������");
		        	tmpsysbk.put(bkcode,bkname);
		        } 
		        
		    }catch(java.lang.NullPointerException e){ 
		    	e.printStackTrace();
		    	
		    }catch(Exception e) {
		    	e.printStackTrace();
		    } finally {
		    	if(rs_gn != null) {
		    		try {
						rs_gn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		rs_gn = null;
		    	}
		    }
			
			stockbasicinfo.setGeGuCurSuoShuTDXSysBanKuaiList(tmpsysbk);
			
//			sqlquerystat=  "SELECT �Զ����� FROM ��Ʊͨ�����Զ������Ӧ��  WHERE ��Ʊ����= "
//							+ "'" +  stockcode.trim() + "'"
//							;
//			//logger.debug(sqlquerystat);
//			CachedRowSetImpl rs_zdy = connectdb.sqlQueryStatExecute(sqlquerystat);
//			HashSet<String> tmpzdybk = new HashSet<String> ();
//			try  {     
//				rs_zdy.last();  
//		        int rows = rs_zdy.getRow();  
//		        rs_zdy.first();  
//		        int k = 0;  
//		        //while(rs.next())
//		        for(int j=0;j<rows;j++) { 
//		        	tmpzdybk.add( rs_zdy.getString("�Զ�����") );
//		        	rs_zdy.next();
//		        } 
//		        rs_zdy.close();
//		    }catch(java.lang.NullPointerException e){ 
//		    	e.printStackTrace();
//		    }catch(Exception e) {
//		    	e.printStackTrace();
//		    }
//			
//			stockbasicinfo.setSuoShuTDXZdyBanKuai(tmpzdybk);
			
			return stockbasicinfo;
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
	            	   
	            	   if(  !zhishucode.startsWith("000") && !zhishucode.startsWith("001") && !zhishucode.startsWith("002") && !zhishucode.startsWith("300"))
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
	            			   List<String> tmplinepartnamelist2 = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(2).trim());
	            			   zhishuname = zhishuname + tmplinepartnamelist2.get(0).trim();
	            		   }
	            			   
	            	   }
	            	   if(zhishuname.trim().length() <4)
	            		   logger.debug(zhishuname);
	            	   if(zhishuname.trim().length() ==2 )
	            		   logger.debug(zhishuname);
	            	   
	            	   String sqlinsertstat = "Update a�� set ��Ʊ���� = '" + zhishuname.trim() + "'\r\n" + 
	   						   					"where ��Ʊ���� = '" + zhishucode.trim() + "' "
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
	            	   
	            	   if(  !(zhishucode.startsWith("60") ) )
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
	            	   						   "where ��Ʊ���� = '" + zhishucode.trim() + "' "
		   						;
//	            	   logger.debug(sqlinsertstat);
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
			 connectdb.sqlUpdateStatExecute(sqlupdatestat);
			
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
			
			HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
			sqlstatmap.put("mysql", sqlquerystat);
			
//			sqlquerystat = getZdgzMrmcYingKuiSQLForAccess (stockbasicinfo);
//			sqlstatmap.put("access", sqlquerystat);
	     
			 CachedRowSetImpl rs = null;
			 rs = connectdb.sqlQueryStatExecute(sqlstatmap);
			 
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
	                if( !(stockcode.startsWith("00") || stockcode.startsWith("30") || stockcode.startsWith("60") ) 
	                		|| stockcode.equals("000003") 
	                		|| datavalueindb.equals("0") )
	                	continue;

	                String lastupdatedate = formateDateForDiffDatabase("mysql", datavalueindb );
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
	                
	                 sqlinsetstat = "INSERT INTO ��Ʊͨ���Ż�������Ϣ��Ӧ�� (��Ʊ����GPDM, �ܹɱ�ZGB,��������GXRQ, GJG,FQRFRG,���˹�FRG,B��BG,H��HG,��ͨA��LTAG,ÿ������ZGG,"
	                														+  "ZPG,���ʲ�ZZC,�����ʲ�LDZC,�̶��ʲ�GDZC,�����ʲ�WXZC,�ɶ�����CQTZ,������ծLDFZ,������ȨCQFZ,"
	                														+ "������ZBGJJ,���ʲ�JZC,��Ӫ����ZYSY,Ӫҵ�ɱ�ZYLY,Ӧ���ʿ�QTLY,Ӫҵ����YYLY,Ͷ������TZSY,"
	                														+ "��Ӫ�ֽ���BTSY,���ֽ���YYWSZ,���SNSYTZ,�����ܶ�LYZE,˰������SHLY,������JLY,"
	                														+ "δ��������WFPLY,���ʲ�TZMGJZ,����DY,��ҵHY,ZBNB) "
	                		              		+ "VALUES("
	                		              		+ "'" + stockcode + "'" +","
	                		              		+ zgb +","
	                		              		+ lastupdatedate +","
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
			                					+ zbnb
	                		              		+ ")"
	                		              		+ " ON DUPLICATE KEY UPDATE "
	                        					+ " �ܹɱ�ZGB=" + zgb +","
	                        					+ " ��������GXRQ=" + lastupdatedate +","
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
	                        					+ " ZBNB=" + zbnb
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
	                connectdb.sqlInsertStatExecute(sqlinsetstat);
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
//		/*
//		 * �洢���Ϊĳһ�ܵ��ȵ㣬���������ȵ㣬�������ʱ�������ǰ����ʲô�ȵ�
//		 */
//		public int setBanKuaiAsReDian(String bkcode, BanKuaiReDian bkrd) 
//		{
//			LocalDate rediandate = bkrd.getReDianDate();
//			String miaoshu = bkrd.getReDianMiaoShu ();
//			
//			String sqlinsertstat = "INSERT INTO �������ȵ��¼(����,�ȵ�����,����) values ("
//					+ "'" + bkcode + "'" + ","
//					+ "'" + rediandate + "'" + ","
//					+ "'" + miaoshu  + "'" 
//					+ ")"
//					;
//			logger.debug(sqlinsertstat);
//			int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat) ;
//			return autoIncKeyFromApi;
//		}
		public void updateBanKuaiExportGephiBkfxOperation(String nodecode, boolean importdailydata, boolean exporttogephi, boolean showinbkfx)
		{
			String sqlupdatestat = "UPDATE ͨ���Ű���б� SET " +
								" ���뽻������=" + importdailydata  + "," +
								" ����Gephi=" + exporttogephi +  ","  +
								" ������=" + showinbkfx +
								" WHERE ���ID='" + nodecode + "'"
								;
			logger.debug(sqlupdatestat);
	   		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		}

}

/*
 * google guava files �࣬����ֱ�Ӵ��������line
 */
class TDXBanKuaiFielProcessor implements LineProcessor<List<String>> 
{
   
    private List<String> stocklists = Lists.newArrayList();
   
    @Override
    public boolean processLine(String line) throws IOException {
    	if(line.length() >=7)
    		stocklists.add(line.substring(1));
        return true;
    }
    @Override
    public List<String> getResult() {
        return stocklists;
    }
}

//  SELECT 'a��',UPDATE_TIME FROM information_schema.tables where TABLE_SCHEMA='stockinfomanagementtest' 