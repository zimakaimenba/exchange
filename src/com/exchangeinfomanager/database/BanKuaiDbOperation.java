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

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNews;
//import com.exchangeinfomanager.bankuaifengxi.ChenJiaoLangZhanBiInGivenPeriod;
import com.exchangeinfomanager.bankuaifengxi.ChenJiaoZhanBiInGivenPeriod;
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

	private void initializeDb() 
	{
		connectdb = ConnectDataBase.getInstance();
	}
	private void initialzieSysconf ()
	{
		sysconfig = SystemConfigration.getInstance();
	}
	
	
//	public Stock getStockTDXBanKuaiInfo(Stock stockbasicinfo) 
//	{
//		String stockcode = stockbasicinfo.getMyOwnCode();
//		HashMap<String,String> tmpsysbk = new HashMap<String,String> ();
//		
//		String sqlquerystat =null;
//		sqlquerystat=  "SELECT gpgn.������ ������, tdxbk.`�������` ������� FROM ��Ʊͨ���Ÿ������Ӧ�� gpgn, ͨ���Ű���б� tdxbk"
//				+ "  WHERE ��Ʊ����=" + "'" +  stockcode.trim() + "'" + "AND gpgn.������ = tdxbk.`���ID` AND ISNULL(�Ƴ�ʱ��)"
//				+ "UNION " 
//				+ " SELECT gphy.��ҵ��� ������, tdxbk.`�������` ������� FROM ��Ʊͨ������ҵ����Ӧ�� gphy, ͨ���Ű���б� tdxbk "
//				+ " WHERE ��Ʊ����=" + "'" +  stockcode.trim() + "'" + "AND gphy.`��ҵ���` = tdxbk.`���ID` AND ISNULL(�Ƴ�ʱ��)"
//				+ "UNION " 
//				+ " SELECT gpfg.`�����`������, tdxbk.`�������` �������  FROM ��Ʊͨ���ŷ�����Ӧ�� gpfg, ͨ���Ű���б� tdxbk"
//				+ "  WHERE ��Ʊ����= "+ "'" +  stockcode.trim() + "'" + "AND gpfg.`�����` = tdxbk.`���ID` AND ISNULL(�Ƴ�ʱ��)"
//				;
//		
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rs_gn = connectdb.sqlQueryStatExecute(sqlquerystat);
//		try  {     
//	        while(rs_gn.next()) {
//	        	String bkcode = rs_gn.getString("������");
//	        	String bkname = rs_gn.getString("�������");
//	        	tmpsysbk.put(bkcode,bkname);
//	        } 
//	        
//	    }catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    	
//	    }catch(Exception e) {
//	    	e.printStackTrace();
//	    } finally {
//	    	if(rs_gn != null) {
//	    		try {
//					rs_gn.close();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//	    		rs_gn = null;
//	    	}
//	    }
//		
//		stockbasicinfo.setGeGuSuoShuTDXSysBanKuaiList(tmpsysbk);
//		
////		sqlquerystat=  "SELECT �Զ����� FROM ��Ʊͨ�����Զ������Ӧ��  WHERE ��Ʊ����= "
////						+ "'" +  stockcode.trim() + "'"
////						;
////		//System.out.println(sqlquerystat);
////		CachedRowSetImpl rs_zdy = connectdb.sqlQueryStatExecute(sqlquerystat);
////		HashSet<String> tmpzdybk = new HashSet<String> ();
////		try  {     
////			rs_zdy.last();  
////	        int rows = rs_zdy.getRow();  
////	        rs_zdy.first();  
////	        int k = 0;  
////	        //while(rs.next())
////	        for(int j=0;j<rows;j++) { 
////	        	tmpzdybk.add( rs_zdy.getString("�Զ�����") );
////	        	rs_zdy.next();
////	        } 
////	        rs_zdy.close();
////	    }catch(java.lang.NullPointerException e){ 
////	    	e.printStackTrace();
////	    }catch(Exception e) {
////	    	e.printStackTrace();
////	    }
////		
////		stockbasicinfo.setSuoShuTDXZdyBanKuai(tmpzdybk);
//		
//		return stockbasicinfo;
//	}

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
			 String sqlquerystat= "select �������, ����ʱ��,����������,������Ϣʱ��,������Ϣ,ȯ������ʱ��,ȯ����������,����ؼ��ͻ�,����ؼ���������,�ͻ�,�������� from ͨ���Ű���б�  where ���ID = '" + bkcode +"' \r\n" + 
			 		"union\r\n" + 
			 		"select �������,����ʱ��,����������,������Ϣʱ��,������Ϣ,ȯ������ʱ��,ȯ����������,����ؼ��ͻ�,����ؼ���������,�ͻ�,�������� from ͨ���Ž�����ָ���б�  where ���ID = '" + bkcode + "' " 
					 			;
			 System.out.println(sqlquerystat);
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
					" select �������, 'ͨ���Ű���б�' as tablename , 'bankuai' as type from ͨ���Ű���б� where ���ID = '" + nodecode + "'" + 
					" union\r\n" + 
					" select �������, 'ͨ���Ž�����ָ���б�' as tablename, 'zhishu' as type from ͨ���Ž�����ָ���б� where ���ID = '" + nodecode + "'"
					;
				System.out.println(sqlquerystat);
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
		
		
		try {
			java.util.Date gainiantishidate = rs.getDate("����ʱ��");
//			LocalDate ldgainiantishidate = gainiantishidate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); 
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(gainiantishidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			tmpnode.setGainiantishidate(ldgainiantishidate); 
			
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setGainiantishidate(null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		try {
			String gainiantishi = rs.getString("����������");
			tmpnode.setGainiantishi(gainiantishi);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setGainiantishi(" ");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try 
		{
			Date quanshangpingjidate = rs.getDate("ȯ������ʱ��");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(quanshangpingjidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			tmpnode.setQuanshangpingjidate(ldgainiantishidate);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setQuanshangpingjidate(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try	{
			String quanshangpingji = rs.getString("ȯ����������").trim();
			tmpnode.setQuanshangpingji(quanshangpingji);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setQuanshangpingji("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try	{
			Date fumianxiaoxidate = rs.getDate("������Ϣʱ��");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(fumianxiaoxidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			tmpnode.setFumianxiaoxidate(ldgainiantishidate);
		} catch(java.lang.NullPointerException ex1) { 
			tmpnode.setFumianxiaoxidate(null);
		} catch(Exception ex2) {
			ex2.printStackTrace();				
		}

		try	{
			String fumianxiaoxi = rs.getString("������Ϣ").trim();
			tmpnode.setFumianxiaoxi(fumianxiaoxi);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setFumianxiaoxi("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			String zhengxiangguan = rs.getString("����ؼ��ͻ�");
			tmpnode.setZhengxiangguan(zhengxiangguan);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setZhengxiangguan("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try {
			String fuxiangguan = rs.getString("����ؼ���������");
			tmpnode.setFuxiangguan(fuxiangguan);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setFuxiangguan("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try {
			String keHuCustom = rs.getString("�ͻ�");
			tmpnode.setKeHuCustom(keHuCustom);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setKeHuCustom("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try {
			String jingZhengDuiShou = rs.getString("��������");
			tmpnode.setJingZhengDuiShou(jingZhengDuiShou);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setJingZhengDuiShou("");			
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
		this.refreshTDXAllBanKuaiToSystem(tmprecordfile); //880���
		this.refreshTDXZhiShuShangHaiLists (tmprecordfile); //�Ϻ�ָ��
		this.refreshTDXZhiShuShenZhenLists (tmprecordfile); //����ָ��
		 
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
               System.out.println(fileHead);
               
               //��������2�ֽ�
               dis.read(itemBuf, 0, 2); 
               String bknumber =new String(itemBuf,0,2);  
               System.out.println(bknumber);
			 
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
            	   System.out.println(gupiaoheader);
            	   
            	   //������
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                  
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   System.out.println(tmpstockcodestr);
                   System.out.print(tmpstockcodestr.size());
                   Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                   System.out.println(tmpstockcodesetnew);
                   
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
	   			    	System.out.println(sqlquerystat);
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
//		   	        	System.out.println(sqldeletetstat);
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
				       	System.out.println(sqlupdatestat);
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
		   				System.out.println(sqlinsertstat);
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
	 * �����ݿ��е����а���ͨ���ŵ����а���ļ��Ƚϣ�������ֵ��°�飬ɾ��ͨ�������Ѿ��޳��İ��
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
				System.out.println(tmpbkinfo);
				if( !Pattern.matches("\\d{4}00",tmpbkinfo.get(5)) )  //����������飬������
					tmpsysbkmap.put(tmpbkinfo.get(1), tmpbkinfo );
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

        Set<String> tmpsysbkset =  tmpsysbkmap.keySet(); //�µİ���б�
        HashMap<String, BanKuai> curbkindbmap = this.getTDXBanKuaiList();
        Set<String> curdaleidetaillistset = curbkindbmap.keySet();
        
        //��tmpsysbkset�����еģ�curdaleidetaillistsetû�е�ѡ���������µģ�Ҫ�������ݿ�
        SetView<String> differencebankuainew = Sets.difference(tmpsysbkset, curdaleidetaillistset );
        try {
	        if(differencebankuainew.size() == 0)
	        	Files.append("û���µİ�顣" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			else
				Files.append("�����°�飺"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		} catch (IOException ex) {
				ex.printStackTrace();
		}
			
        for (String newbkcode : differencebankuainew) {
        	String newbk = tmpsysbkmap.get(newbkcode).get(0);
        	String newbktdxswcode = tmpsysbkmap.get(newbkcode).get(5);
			String sqlinsertstat = "INSERT INTO  ͨ���Ű���б�(�������,����ʱ��,���ID,��ӦTDXSWID) values ("
					+ "'" + newbk.trim() + "'" + ","
					+ "\"" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() )   + "\"" + ","
					+ "'" + newbkcode + "'" + ","
					+ "'" + newbktdxswcode + "'"
					+ ")"
					;
			System.out.println(sqlinsertstat);
			int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);

			try {
				Files.append(newbk.trim() + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        
        
        //�� curdaleidetaillistset �����еģ�tmpsysbksetû�е�ѡ�������Ǿɵģ�Ҫ�����ݿ���ɾ��
        SetView<String> differencebankuaiold = Sets.difference(curdaleidetaillistset, tmpsysbkset );
			try {
				if(differencebankuaiold.size() != 0)
					Files.append("ɾ���ɰ�飺" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        for (String oldbkcode : differencebankuaiold) {
        	String oldbk = this.getTDXBanKuaiList().get(oldbkcode).getMyOwnName();
        	String sqldeletetstat = "DELETE  FROM ͨ���Ű���б� "
        							+ " WHERE �������=" + "'"  + oldbk + "'"
        							;
        	System.out.println(sqldeletetstat);
    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
    		
    		try {
				Files.append( oldbk.trim() + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		    		
    		//��Ҫɾ���ð���� ��Ʊ�����Ӧ��/��Ʊ��ҵ��Ӧ��/��Ʊ����Ӧ��/��ҵ���Ӱ���б�  �еĹ�Ʊ�Ͱ��Ķ�Ӧ��Ϣ
    		sqldeletetstat = "DELETE  FROM  ��Ʊͨ���Ÿ������Ӧ��"
					+ " WHERE ������=" + "'"  + oldbk + "'"
					;
			System.out.println(sqldeletetstat);
			autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
			
			sqldeletetstat = "DELETE  FROM  ��Ʊͨ������ҵ����Ӧ��"
					+ " WHERE ��ҵ���=" + "'"  + oldbk + "'"
					;
			System.out.println(sqldeletetstat);
			autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
			
			sqldeletetstat = "DELETE  FROM ��Ʊͨ���ŷ�����Ӧ��"
					+ " WHERE �����=" + "'"  + oldbk + "'"
					;
			System.out.println(sqldeletetstat);
			autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
			
			sqldeletetstat = "DELETE  FROM ��ҵ���Ӱ���б�"
					+ " WHERE ����ͨ���Ű��=" + "'"  + oldbk + "'"
					;
			System.out.println(sqldeletetstat);
			autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
        }
        
        //����û�б仯�İ�飬Ҫ����������Ƿ��б仯
        SetView<String> intersectionbankuai = Sets.intersection(curdaleidetaillistset, tmpsysbkset );
        for(String interbkcode : intersectionbankuai) {
        	String bknameintdxfile =  tmpsysbkmap.get(interbkcode).get(0); //ͨ��������İ������
        	String tdxswidintdxfile = tmpsysbkmap.get(interbkcode).get(5); 
        	String bknameincurdb = curbkindbmap.get(interbkcode).getMyOwnName();
        	
        	String updatesqlstat = "UPDATE ͨ���Ű���б� SET "
        						+ " ������� = '" + bknameintdxfile  + "'" + ","
        						+ " ��ӦTDXSWID= '" + tdxswidintdxfile + "'"
        						+ " where ������� != '" + bknameintdxfile + "' and ���ID = '" + interbkcode + "'"
        						;
        	System.out.println(updatesqlstat);
        	connectdb.sqlUpdateStatExecute(updatesqlstat);
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
			 System.out.println("File not exist");
			 return -1;
		 }
		 File fileHyTGg = new File(sysconfig.getTDXHangYeBanKuaiToGeGuFile() );  //"T0002/hq_cache/" + "tdxhy.cfg";
		 if(!fileHyTGg.exists() ) {
			 System.out.println("File not exist");
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
				System.out.println(line);
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //T01|��Դ  T010101|ú̿����
				System.out.println(tmpbkinfo);
				hydmmap.put(tmpbkinfo.get(0).trim(), tmpbkinfo.get(1).trim());
			}
			
            Files.append("��ʼ����ͨ���Ź�Ʊ��ҵ��Ӧ��Ϣ:" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			fr = new FileReader(sysconfig.getTDXHangYeBanKuaiToGeGuFile()); //ͨ���Ÿ��ɺͰ��Ķ�Ӧ��ϵ
			bufr = new BufferedReader (fr);
			while((line = bufr.readLine()) != null) {
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //0|000001|T1001|440101
				String stockcode = tmpbkinfo.get(1).trim();
				String stockbkcode = tmpbkinfo.get(2).trim();
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
   			    							+ " AND ��ӦTDXSWID=" +  "'" + stockbkcode + "'"
   			    							+ " AND isnull(�Ƴ�ʱ��)"
   			    							;
   			    	System.out.println(sqlquerystat);
   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   			    	
   			        rs.last();  
   			        int rows = rs.getRow();  
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
		   				System.out.println(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   	            Files.append("���룺" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   				
		   				allupdatednum++;
   			        } else { //����
   			        	String stockbkanameindb = rs.getString("��ӦTDXSWID");
   			        	if( !stockbkanameindb.equals(stockbkcode) ) { //��һ��˵������б仯���ϵİ������Ƴ�ʱ�䣬ͬʱ����һ������Ϣ����Ӧ�µİ��
   			        		//�Ȱ��ϼ�¼ �Ƴ�ʱ�����
//	   			     		String sqlupdatestat = "UPDATE  ��Ʊͨ������ҵ����Ӧ��  SET " 
//	   			 				+ " �Ƴ�ʱ�� =" + "'" +  sysconfig.formatDate(new Date() ) + "'" + ","
//	   			 				+ " WHERE ��Ʊ���� = " + "'" + stockcode.trim() + "'" 
//	   			 				+ " AND ��ӦTDXSWID=" + stockbkanameindb 
//	   			 				+ " AND isnull(�Ƴ�ʱ��)"
//	   			 				;
   			        		String sqlupdatestat = "UPDATE ��Ʊͨ������ҵ����Ӧ�� JOIN ͨ���Ű���б�"
   	       							+ "  ON  ��Ʊͨ������ҵ����Ӧ��.`��Ʊ����` = " + "'" + stockcode.trim() + "'"
   	       							+ "  AND isnull(�Ƴ�ʱ��)"
   	       							+ "  and  ��Ʊͨ������ҵ����Ӧ��.`������` = ͨ���Ű���б�.`���ID` "
   	       							+ "  and ͨ���Ű���б�.`��ӦTDXSWID` = " + "'" + stockbkanameindb + "'"
   	       							+ "  SET �Ƴ�ʱ�� = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
   	       							;
		   			 		System.out.println(sqlupdatestat);
		   			 		@SuppressWarnings("unused")
		   			 		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   			 		allupdatednum ++;
		   			 		
		   			 		//������һ����¼
//			   			 	String sqlinsertstat = "INSERT INTO  ��Ʊͨ������ҵ����Ӧ��(��Ʊ����,��ҵ���,��ӦTDXSWID,��ƱȨ��) values ("
//			   						+ "'" + stockcode.trim() + "'" + ","
//			   						+ "'" + stockbkname.trim() + "'"  + ","
//			   						+ "'" + stockbkcode.trim() + "'" + ","
//			   						+ 10
//			   						+ ")"
//			   						;
		   			 		String sqlinsertstat =  "insert into ��Ʊͨ������ҵ����Ӧ��(��Ʊ����,������,��ӦTDXSWID,��ƱȨ��)"
								+ " SELECT '" + stockcode.trim() + "', ͨ���Ű���б�.`���ID`,'" + stockbkcode + "',10 "
								+ " FROM ͨ���Ű���б�  where ͨ���Ű���б�.`��ӦTDXSWID` = '" + stockbkcode + "'"
								;
			   				System.out.println(sqlinsertstat);
			   				autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//		   	            Files.append("���룺" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//			   	        Files.append("���£�" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
   			        	}
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				bufr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 
		 return allupdatednum;
	}
	/*
	 * ͨ���ŵķ������ɴ����Ӧ�ļ�
	 */
	private void refreshTDXFengGeBanKuaiToGeGu (File tmprecordfile)
	{
		File file = new File(sysconfig.getTDXFengGeBanKuaiToGeGuFile() );
		 
		 if(!file.exists() ) {
			 System.out.println("File not exist");
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
               System.out.println(fileHead);
               
               //��������2�ֽ�
               dis.read(itemBuf, 0, 2); 
               String bknumber =new String(itemBuf,0,2);  
               System.out.println(bknumber);
			 
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
            	   System.out.println(gupiaoheader);
            	   
            	   //������
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   System.out.println(tmpstockcodestr);
                   System.out.print(tmpstockcodestr.size());
                   Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                   System.out.println(tmpstockcodesetnew);
                   
                   //���� ������ȯ�� �Ǹ���飬��û��ID����ͨ���Ű���б�����û�У�Ҫ���ж��������Σ��Ա��ò�ͬ��SQL
                   boolean hasbkcode = true;
                   CachedRowSetImpl rspd = null; 
	   			   try {
	   			    	String sqlquerystat = "SELECT count(*) as result FROM ͨ���Ű���б� WHERE �������='"  + gupiaoheader + "'";

	   			    	System.out.println(sqlquerystat);
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

	   			    	System.out.println(sqlquerystat);
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
		   	        	System.out.println(sqlupdatestat);
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
		   				System.out.println(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				
//		   				Files.append("���룺"  + str.trim() + " " + gupiaoheader + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   	        
//		   	        if(differencebankuainew.size() ==0 && differencebankuaiold.size()>0 ) { // �µ�Ϊ0���ɵĴ����㣬ɾ����ʱ������ʵ�ֻ������ݿⲻ����������ʱ�䣬�Ը���XML��Ӱ�죬����ǿ�Ƹ���һ����Ϣ
//		   	        	String sqlupdatestat = "UPDATE ͨ���Ű���б� SET �������¸���ʱ�� = "
//		   	        			+  formateDateForDiffDatabase("mysql", sysconfig.formatDate( LocalDateTime.now() ) ) + ","
//		   	        			;
//		   	        	System.out.println(sqlupdatestat);
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
			 System.out.println("File not exist");
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
              System.out.println(fileHead);
              
              //��������2�ֽ�
              dis.read(itemBuf, 0, 2); 
              String bknumber =new String(itemBuf,0,2);  
              System.out.println(bknumber);
			 
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
           	   System.out.println(gupiaoheader);
           	   
           	   //������
                  String gupiaolist =new String(itemBuf2,12,sigbk-12);
                 
                  List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                  System.out.println(tmpstockcodestr);
                  System.out.print(tmpstockcodestr.size());
                  Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                  System.out.println(tmpstockcodesetnew);
                  
                  //�����ݿ��ж�����ǰ�ð��ĸ���
                  Set<String> tmpstockcodesetold = new HashSet();
                  CachedRowSetImpl rs = null;
	   			    try {
//	   			    	String sqlquerystat = "SELECT *  FROM ��Ʊͨ���Ž�����ָ����Ӧ��   WHERE  ָ����� = " 
//	   			    							+ "'"  + gupiaoheader + "'"
//	   			    							+ " AND ISNULL(�Ƴ�ʱ��)"
//	   			    							;
	   			    	String sqlquerystat = "SELECT zsbk.��Ʊ����  FROM ��Ʊͨ���Ž�����ָ����Ӧ��  zsbk, ͨ���Ž�����ָ���б� zslb"
	    						+ " WHERE  zsbk.������ = zslb.`���ID`"
	    						+ " AND zslb.`�������` = '"  + gupiaoheader + "'"
	    						+ " AND ISNULL(zsbk.�Ƴ�ʱ��)"
	    						;
	   			    	System.out.println(sqlquerystat);
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
//		   	        	String sqldeletetstat = "DELETE  FROM ��Ʊͨ���Ž�����ָ����Ӧ�� "
//		   	        							+ " WHERE ��Ʊ����=" + "'" + str + "'" 
//		   	        							+ " AND ָ�����=" + "'" + gupiaoheader + "'" 
//		   	        							;
//		   	        	System.out.println(sqldeletetstat);
//		   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//		                Files.append("ɾ����" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//		   	        	String sqlupdatestat = "UPDATE ��Ʊͨ���Ž�����ָ����Ӧ��   SET"
//		   	        			+ " �Ƴ�ʱ�� =" + "'" +  sysconfig.formatDate(new Date() ) + "'" 
//	   			 				+ " WHERE ��Ʊ���� = " + "'" + str.trim() + "'" 
//	   			 				+ " AND ָ�����=" + "'" + gupiaoheader + "'" 
//	   			 				+ " AND isnull(�Ƴ�ʱ��)"
//	   			 				;
		   	        	String sqlupdatestat = "UPDATE ��Ʊͨ���Ž�����ָ����Ӧ��  JOIN ͨ���Ž�����ָ���б�"
       							+ "  ON  ��Ʊͨ���Ž�����ָ����Ӧ��.`��Ʊ����` = " + "'" + str.trim() + "'"
       							+ "  AND isnull(�Ƴ�ʱ��)"
       							+ "  and  ��Ʊͨ���Ž�����ָ����Ӧ��.`������` = ͨ���Ž�����ָ���б�.`���ID` "
       							+ "  and ͨ���Ž�����ָ���б�.`�������` = " + "'" + gupiaoheader + "'"
       							+ "  SET �Ƴ�ʱ�� = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
       							;	
				       	System.out.println(sqlupdatestat);
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
								+ "	  SELECT '" + str.trim() + "', ͨ���Ž�����ָ���б�.`���ID`, 10 "
								+ " FROM ͨ���Ž�����ָ���б�  where ͨ���Ž�����ָ���б�.`�������` = '" + gupiaoheader + "'"   
								;
		   				System.out.println(sqlinsertstat);
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
		File file = new File(sysconfig.getTDXShangHaiZhiShuNameFile() );
		
		if(!file.exists() ) {
			 System.out.println("File not exist");
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
//               System.out.println(fileHead);
               
               int sigbk = 18*16+12+14;
               byte[] itemBuf2 = new byte[sigbk];
               int i=0;
               Files.append("��ʼ����ͨ���Ź�Ʊָ������Ӧ��Ϣ:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   String zhishuline =new String(itemBuf2,0,sigbk);
//            	   System.out.println(zhishuline);
            	   String zhishucode = zhishuline.trim().substring(0, 6);
//            	   System.out.println(zhishucode);
            	   
            	   if(  !(zhishucode.startsWith("99") || zhishucode.startsWith("00")) )
            		   continue;
            	   
            	   List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(zhishuline.substring(6, zhishuline.length()));
            	   String zhishuname = null;
            	   try {
	            	    zhishuname = tmplinelist.get(0).trim().substring(0, 6).trim();
            	   } catch (java.lang.StringIndexOutOfBoundsException ex) {
            		   List<String> tmplinepartnamelist = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(1).trim());
            		   zhishuname = tmplinelist.get(0).trim() + tmplinepartnamelist.get(0).trim();
            	   }
//            	   System.out.println(zhishuname);
            	   
            	   String sqlinsertstat = "INSERT INTO  ͨ���Ž�����ָ���б�(�������,���ID,ָ������������) values ("
	   						+ " '" + zhishuname.trim() + "'" + ","
	   						+ " '" + zhishucode.trim() + "'" + ","
	   						+ " '" + "sh" + "'"
	   						+ ")"
	   						+ " ON DUPLICATE KEY UPDATE "
	   						+ " �������=" + " '" + zhishuname.trim() + "'" + ","
	   						+ " ָ������������=" + " '" + "sh" + "'" 
	   						;
            	   System.out.println(sqlinsertstat);
	   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//	                Files.append("���룺" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
	 * ��ͨ���Ÿ������еĽ�����ָ��������part
	 */
	private int refreshTDXZhiShuShenZhenLists(File tmprecordfile)
	{
		File file = new File(sysconfig.getTDXShenZhenShuNameFile() );
		
		if(!file.exists() ) {
			 System.out.println("File not exist");
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
               System.out.println(fileHead);
               
               int sigbk = 18*16+12+14;
               byte[] itemBuf2 = new byte[sigbk];
               int i=0;
               Files.append("��ʼ����ͨ���Ź�Ʊָ������Ӧ��Ϣ:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   String zhishuline =new String(itemBuf2,0,sigbk);
//            	   System.out.println(zhishuline);
            	   String zhishucode = zhishuline.trim().substring(0, 6);
//            	   System.out.println(zhishucode);
            	   
            	   if(  !zhishucode.startsWith("3990") )
            		   continue;
            	   
            	   List<String> tmplinelist = Splitter.onPattern("\\s+").omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(zhishuline.substring(6, zhishuline.length()));
            	   String zhishuname = null;
            	   try {
	            	    zhishuname = tmplinelist.get(0).trim().substring(0, 6).trim();
            	   } catch (java.lang.StringIndexOutOfBoundsException ex) {
            		   List<String> tmplinepartnamelist = Splitter.fixedLength(8).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(tmplinelist.get(1).trim());
            		   zhishuname = tmplinelist.get(0).trim() + tmplinepartnamelist.get(0).trim();
            	   }
//            	   System.out.println(zhishuname);
            	   
            	   String sqlinsertstat = "INSERT INTO  ͨ���Ž�����ָ���б�(�������,���ID,ָ������������) values ("
	   						+ " '" + zhishuname.trim() + "'" + ","
	   						+ " '" + zhishucode.trim() + "'" + ","
	   						+ " '" + "sz" + "'"
	   						+ ")"
	   						+ " ON DUPLICATE KEY UPDATE "
	   						+ " �������=" + " '" + zhishuname.trim() + "'" + ","
	   						+ " ָ������������=" + " '" + "sz" + "'" 
	   						;
            	   System.out.println(sqlinsertstat);
	   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//	                Files.append("���룺" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
	 * �ҳ����ݿ���ͨ�����Զ�����
	 */
	private HashMap<String, BanKuai> initializeSystemAllZdyBanKuaiList()  
	{
		//HashMap<String,BanKuai> sysbankuailiebiaoinfo;
//		private  HashMap<String,BanKuai> zdybankuailiebiaoinfo;
		HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();

		String sqlquerystat = "SELECT ���ID,�������,����ʱ��  FROM ͨ�����Զ������б� 	ORDER BY ���ID,������� ,����ʱ�� "
							   ;   
		System.out.println(sqlquerystat);
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
	 * �ҳ�ͨ���Ŷ�������а��.��������������ָ����飬���µ�BkChanYeLianTreeNode�洢
	 */
//	public HashMap<String, BkChanYeLianTreeNode> getTDXBanKuaiList()   
//	{
//		HashMap<String,BkChanYeLianTreeNode> tmpsysbankuailiebiaoinfo = new HashMap<String,BkChanYeLianTreeNode> ();
//
//		String sqlquerystat = "SELECT ���ID,�������,����ʱ��  FROM ͨ���Ű���б� 	ORDER BY ������� ,���ID,����ʱ�� "
//							   ;   
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rs = null;
//	    try {  
//	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//	    	
//	        rs.last();  
//	        int rows = rs.getRow();  
////	        data = new Object[rows][];    
////	        int columnCount = 3;//����  
//	        rs.first();  
//	        //int k = 0;  
//	        //while(rs.next())
//	        for(int j=0;j<rows;j++) { 
//	        	
//	        	BkChanYeLianTreeNode tmpbk = new BanKuai (rs.getString("���ID"),rs.getString("�������"));
//	        	tmpsysbankuailiebiaoinfo.put(rs.getString("���ID"), tmpbk);
//	            rs.next();
//	        }
//	        
//	    }catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    } catch (SQLException e) {
//	    	e.printStackTrace();
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    } finally {
//	    	if(rs != null)
//				try {
//					rs.close();
//					rs = null;
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//	    } 
//	    
//	    return tmpsysbankuailiebiaoinfo;
//	}
	/*
	 * ���й�Ʊ
	 */
	public HashMap<String, Stock> getAllStocks() 
	{
		HashMap<String,Stock> tmpsysbankuailiebiaoinfo = new HashMap<String,Stock> ();

		String sqlquerystat = "SELECT ��Ʊ����,��Ʊ���� FROM A��"									
							   ;   
		System.out.println(sqlquerystat);
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
	        	tmpsysbankuailiebiaoinfo.put(rs.getString("��Ʊ����"), tmpbk);
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
	 * ���е�ָ��������������ָ���Ͱ��
	 */
	public HashMap<String, BanKuai> getTDXAllZhiShuAndBanKuai() 
	{
		HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();

		String sqlquerystat = "SELECT ���ID,�������,����ʱ��  FROM ͨ���Ž�����ָ���б�" +
								" UNION" +
								" SELECT ���ID,�������,����ʱ��  FROM ͨ���Ű���б�  "  ;	
							   ;   
		System.out.println(sqlquerystat);
		CachedRowSetImpl rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	        rs.last();  
	        int rows = rs.getRow();  
	        rs.first();  
	        //int k = 0;  
//	        while(rs.next()) {
	        for(int j=0;j<rows;j++) {  
	        	System.out.println(rs.getString("���ID") );
	        	System.out.println(rs.getString("�������"));
	        	BanKuai tmpbk = new BanKuai (rs.getString("���ID"),rs.getString("�������") );
//	        	boolean notexporttogephi = rs.getBoolean("��������gephi");
//	        	if(notexporttogephi)
//	        		tmpbk.setNotExportToGephi();
//	        	tmpbk.setSuoShuJiaoYiSuo(rs.getString("ָ������������"));
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
	 * ���������������ָ��
	 */
	public HashMap<String, BanKuai> getTDXAllZhiShuList() 
	{
		HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();

		String sqlquerystat = "SELECT *  FROM ͨ���Ž�����ָ���б� "
							   ;   
		System.out.println(sqlquerystat);
		CachedRowSetImpl rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	        rs.last();  
	        int rows = rs.getRow();  
	        rs.first();  
	        //int k = 0;  
//	        while(rs.next()) {
	        for(int j=0;j<rows;j++) {  
//	        	System.out.println(rs.getString("���ID"));
	        	BanKuai tmpbk = new BanKuai (rs.getString("���ID"),rs.getString("�������"));
	        	tmpbk.setSuoShuJiaoYiSuo(rs.getString("ָ������������"));
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
	public HashMap<String, BanKuai> getTDXBanKuaiList()   
	{

		HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();

		String sqlquerystat = "SELECT ���ID,�������,����ʱ��  FROM ͨ���Ű���б� 	ORDER BY ������� ,���ID,����ʱ�� "  ;   
		System.out.println(sqlquerystat);
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
		System.out.println(sqlquerystat);
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
	 * �ҳ�ĳͨ���ŷ��������и���
	 */
//	private HashMap<String,String> getTDXFengGeBanKuaiGeGu(String currentbk) 
//	{
//		String sqlquerystat = "SELECT fg.��Ʊ����, agu.��Ʊ����" 
//				+ " FROM ��Ʊͨ���ŷ�����Ӧ��  AS fg,  A��  AS agu"
//				+ " WHERE  fg.�����= " + "'" + currentbk + "'" 
//				+ " AND fg.��Ʊ����  = agu.��Ʊ����" + "'"
//				+ " AND ISNULL(�Ƴ�ʱ��)"
//				//+ " GROUP BY  czjl.�����˺�, czjl.��Ʊ����, agu.��Ʊ����"
//				;   
//		System.out.println(sqlquerystat);
//		HashMap<String,String> tmpfgset = new HashMap<String,String>();
//		CachedRowSetImpl rsfg = null;
//		try {  
//			rsfg = connectdb.sqlQueryStatExecute(sqlquerystat);
//			
//			rsfg.last();  
//			int rows = rsfg.getRow();  
//			rsfg.first();  
//			for(int j=0;j<rows;j++) {  
//				String tmpstockcode = rsfg.getString("��Ʊ����");
//				String tmpstockname = rsfg.getString("��Ʊ����");
//				tmpfgset.put(tmpstockcode,tmpstockname);
//				rsfg.next();
//			}
//			
//		}catch(java.lang.NullPointerException e){ 
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}catch(Exception e){
//			e.printStackTrace();
//		} finally {
//			try {
//				rsfg.close ();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			rsfg = null;
//		}
//		
//		return tmpfgset;
//	}
	/*
	 * �ҳ�ĳͨ���Ÿ���������и���
	 */
//	private HashMap<String,String> getTDXGaiNianBanKuaiGeGu(String currentbk) 
//	{
//		String sqlquerystat = "SELECT gn.��Ʊ����, agu.��Ʊ����" 
//				+ " FROM ��Ʊͨ���Ÿ������Ӧ��  AS gn, A�� AS agu"
//				+ " WHERE  gn.������= " + "'" + currentbk + "'" 
//				+ " AND gn.��Ʊ����  = agu.��Ʊ����" + "'"
//				+ " AND ISNULL(�Ƴ�ʱ��)"
//				//+ " GROUP BY  czjl.�����˺�, czjl.��Ʊ����, agu.��Ʊ����"
//				;   
//		System.out.println(sqlquerystat);
//		HashMap<String,String> tmpgnset = new HashMap<String,String>();
//		CachedRowSetImpl rsfg = null;
//		try {  
//			 rsfg = connectdb.sqlQueryStatExecute(sqlquerystat);
//			
//			rsfg.last();  
//			int rows = rsfg.getRow();  
//			rsfg.first();  
//			for(int j=0;j<rows;j++) {  
//				String tmpstockcode = rsfg.getString("��Ʊ����");
//				String tmpstockname = rsfg.getString("��Ʊ����");
//				tmpgnset.put(tmpstockcode,tmpstockname);
//				rsfg.next();
//			}
//			rsfg.close();
//		}catch(java.lang.NullPointerException e){ 
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}catch(Exception e){
//			e.printStackTrace();
//		} finally {
//			try {
//				rsfg.close ();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			rsfg = null;
//		}
//		
//		return tmpgnset;
//	}

	
	/*
	 * �ҳ�ĳ��ʱ��� ��ҵ/����/���/ָ�� ͨ���Ű������и��ɼ�����Ȩ��
	 */
//	public HashMap<String, Stock> getTDXBanKuaiGeGuOfHyGnFg(String currentbk,String currentbkcode, Date selecteddatestart , Date selecteddateend)
//	{
//				String actiontable = this.getBanKuaiType(currentbkcode);
//				if(actiontable == null)
//					return null;
//			
//				String sqlquerystat = null;
//				 if(actiontable.trim().equals("��Ʊͨ���Ż�������Ϣ��Ӧ��") ) { //�ҵ�����
//					 sqlquerystat = "SELECT tdxjbm.��Ʊ����GPDM AS ��Ʊ����, agu.��Ʊ����, tdxjbm.��ƱȨ��" 
//								+ " FROM ��Ʊͨ���Ż�������Ϣ��Ӧ��  AS tdxjbm, A�� AS agu, ͨ���Ű���б� AS tdxbk"
//								+ " WHERE  tdxbk.���ID= " + "'" + currentbkcode + "'" 
//								+ " AND tdxbk.��ӦTDXSWID = tdxjbm.����DY"
//								+ " AND tdxjbm.��Ʊ����GPDM  = agu.��Ʊ����" 
//								;
//				 } else { //��������ҵ���
//					 sqlquerystat = "SELECT hy.��Ʊ����, agu.��Ʊ����, hy.��ƱȨ�� , hy.`��ҵ���` as ���ID" 
//								+ " FROM ��Ʊͨ������ҵ����Ӧ��  AS hy, A�� AS agu"
//								+ " WHERE  hy.��ҵ���= " + "'" + currentbkcode + "'" 
//								+ " AND hy.��Ʊ����  = agu.��Ʊ����" 
//								+ " AND unix_timestamp(Date('" + sysconfig.formatDate(selecteddateend) + "')) >= unix_timestamp(Date(����ʱ��)) "
//								+ " AND (unix_timestamp(Date('" +sysconfig.formatDate(selecteddatestart) + "')) <= unix_timestamp(Date(�Ƴ�ʱ��)) OR ISNULL(�Ƴ�ʱ��) ) "
//								
//								+ " UNION "
//								
//								+ "SELECT gn.��Ʊ����, agu.��Ʊ����, gn.��ƱȨ�� , gn.`������` as ���ID" 
//								+ " FROM ��Ʊͨ���Ÿ������Ӧ��  AS gn, A�� AS agu"
//								+ " WHERE  gn.������= " + "'" + currentbkcode + "'" 
//								+ " AND gn.��Ʊ����  = agu.��Ʊ����" 
//								+ " AND unix_timestamp(Date('" + sysconfig.formatDate(selecteddateend) + "')) >= unix_timestamp(Date(����ʱ��)) "
//								+ " AND (unix_timestamp(Date('" +sysconfig.formatDate(selecteddatestart) + "')) <= unix_timestamp(Date(�Ƴ�ʱ��)) OR ISNULL(�Ƴ�ʱ��) ) "
//								
//								+ " UNION "
//								
//								+ "SELECT fg.��Ʊ����, agu.��Ʊ����, fg.��ƱȨ�� , fg.`�����` as ���ID" 
//								+ " FROM ��Ʊͨ���ŷ�����Ӧ��  AS fg,  A��  AS agu"
//								+ " WHERE  fg.�����= " + "'" + currentbkcode + "'" 
//								+ " AND fg.��Ʊ����  = agu.��Ʊ����" 
//								+ " AND unix_timestamp(Date('" + sysconfig.formatDate(selecteddateend) + "')) >= unix_timestamp(Date(����ʱ��)) "
//								+ " AND (unix_timestamp(Date('" +sysconfig.formatDate(selecteddatestart) + "')) <= unix_timestamp(Date(�Ƴ�ʱ��)) OR ISNULL(�Ƴ�ʱ��) ) "
//								
//								+ " UNION "
//
//								+ "SELECT zs.��Ʊ����, agu.��Ʊ����, zs.��ƱȨ�� , zs.`ָ�����` as ���ID" 
//								+ " FROM ��Ʊͨ���Ž�����ָ����Ӧ��  AS zs,  A��  AS agu"
//								+ " WHERE  zs.ָ�����= " + "'" + currentbkcode + "'" 
//								+ " AND zs.��Ʊ����  = agu.��Ʊ����" 
//								+ " AND unix_timestamp(Date('" + sysconfig.formatDate(selecteddateend) + "')) >= unix_timestamp(Date(����ʱ��)) "
//								+ " AND (unix_timestamp(Date('" +sysconfig.formatDate(selecteddatestart) + "')) <= unix_timestamp(Date(�Ƴ�ʱ��)) OR ISNULL(�Ƴ�ʱ��) ) "
//								
//								+ "GROUP BY  ��Ʊ����  ORDER BY ��Ʊ����,��Ʊ���� "
//								;
//				 }
//				
//		System.out.println(sqlquerystat);
////		HashMap<String,String> tmpgnset = new HashMap<String,String>();
//		HashMap<String,Stock> gegumap = new HashMap<String,Stock> ();
//		CachedRowSetImpl rsfg = null;
//		try {  
//			 rsfg = connectdb.sqlQueryStatExecute(sqlquerystat);
//
//			while(rsfg.next()) {  
//				String tmpstockcode = rsfg.getString(1); //"��Ʊ����"
//				String tmpstockname = rsfg.getString("��Ʊ����"); //"��Ʊ����"
//				int stockweight = rsfg.getInt("��ƱȨ��");
//				
//				System.out.println(tmpstockname+tmpstockcode);
//				Stock tmpstock = new Stock (tmpstockcode,tmpstockname);
//				
//				HashMap<String, Integer> stockbkweight = new  HashMap<String, Integer>() ;
//				stockbkweight.put(currentbkcode, stockweight); //�������Ȩ��
//				tmpstock.setGeGuSuoShuBanKuaiWeight (  stockbkweight );
//				
//				gegumap.put(tmpstockcode, tmpstock);
//				
//			}
//			
//		}catch(java.lang.NullPointerException e){ 
//			e.printStackTrace();
////			System.out.println( "���ݿ�����ΪNULL!");
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}catch(Exception e){
//			e.printStackTrace();
//		} finally {
//			try {
//				if(rsfg != null)
//					rsfg.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			rsfg = null;
//		}
//		
//
//		return gegumap;
//	}
//	/*
//	 * ����ĳ����Ʊ��ĳ��ʱ���������ĳ�����ʱ��ĳɽ���
//	 */
//	private double setStockChenJiaoE(String stockcode, String stockname, String currentbk, String currentbkcode,
//			Date selecteddatestart, Date selecteddateend) 
//	{
//		String formatedstartdate = sysconfig.formatDate(selecteddatestart);
//		String formatedenddate  = sysconfig.formatDate(selecteddateend);
//		String diyusqlquerystat;
//		if(stockcode.startsWith("6")) {
//			diyusqlquerystat =  "SELECT ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����` AS ��Ʊ����, ��Ʊͨ���ŷ�����Ӧ��.����� AS ���, sum(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`�ɽ���`) AS �ɽ���"
//					+ "		from ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ join"
//					+ "		     ��Ʊͨ���ŷ�����Ӧ��"
//					+ "		     on ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����` = ��Ʊͨ���ŷ�����Ӧ��.`��Ʊ����`"
//					+ "			  and ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` >= ��Ʊͨ���ŷ�����Ӧ��.`����ʱ��` and"
//					+ "		        (ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` <= ��Ʊͨ���ŷ�����Ӧ��.`�Ƴ�ʱ��` or ��Ʊͨ���ŷ�����Ӧ��.`�Ƴ�ʱ��` is null)"
//					+ "		where Date(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`) <= Date('" +  formatedenddate + "')"
//					+ "		and ��Ʊͨ���ŷ�����Ӧ��.`�����` = '" + currentbkcode + "' and ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����` = '" + stockcode +"'"
//					+ "		group by ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����`, ��Ʊͨ���ŷ�����Ӧ��.`�����`"
//					
//					+ " UNION "
//					
//					+ "SELECT ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����` AS ��Ʊ����, ��Ʊͨ������ҵ����Ӧ��.��ҵ��� AS ���, sum(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`�ɽ���`) AS �ɽ���"
//					+ "		from ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ join"
//					+ "		     ��Ʊͨ������ҵ����Ӧ��"
//					+ "		     on ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����` = ��Ʊͨ������ҵ����Ӧ��.`��Ʊ����`"
//					+ "			  and ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` >= ��Ʊͨ������ҵ����Ӧ��.`����ʱ��` and"
//					+ "		        (ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` <= ��Ʊͨ������ҵ����Ӧ��.`�Ƴ�ʱ��` or ��Ʊͨ������ҵ����Ӧ��.`�Ƴ�ʱ��` is null)"
//					+ "		where Date(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`) <= Date('" +  formatedenddate + "')"
//					+ "		and ��Ʊͨ������ҵ����Ӧ��.`��ҵ���` = '" + currentbkcode + "' and ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����` = '" + stockcode +"'"
//					+ "		group by ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����`, ��Ʊͨ������ҵ����Ӧ��.`��ҵ���`"
//					
//					+ " UNION "
//					
//					+  "SELECT ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����` AS ��Ʊ����, ��Ʊͨ���Ÿ������Ӧ��.������ AS ���, sum(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`�ɽ���`) AS �ɽ���"
//					+ "		from ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ join"
//					+ "		     ��Ʊͨ���Ÿ������Ӧ��"
//					+ "		     on ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����` = ��Ʊͨ���Ÿ������Ӧ��.`��Ʊ����`"
//					+ "			  and ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` >= ��Ʊͨ���Ÿ������Ӧ��.`����ʱ��` and"
//					+ "		        (ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` <= ��Ʊͨ���Ÿ������Ӧ��.`�Ƴ�ʱ��` or ��Ʊͨ���Ÿ������Ӧ��.`�Ƴ�ʱ��` is null)"
//					+ "		where Date(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`) <= Date('" +  formatedenddate + "')"
//					+ "		and ��Ʊͨ���Ÿ������Ӧ��.`������` = '" + currentbkcode + "' and ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����` = '" + stockcode +"'"
//					+ "		group by ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����`, ��Ʊͨ���Ÿ������Ӧ��.`������`"
//					
//					+ " UNION "
//					
//					+  "SELECT ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����` AS ��Ʊ����, ��Ʊͨ���Ž�����ָ����Ӧ��.ָ����� AS ���, sum(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`�ɽ���`) AS �ɽ���"
//					+ "		from ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ join"
//					+ "		     ��Ʊͨ���Ž�����ָ����Ӧ��"
//					+ "		     on ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����` = ��Ʊͨ���Ž�����ָ����Ӧ��.`��Ʊ����`"
//					+ "			  and ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` >= ��Ʊͨ���Ž�����ָ����Ӧ��.`����ʱ��` and"
//					+ "		        (ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` <= ��Ʊͨ���Ž�����ָ����Ӧ��.`�Ƴ�ʱ��` or ��Ʊͨ���Ž�����ָ����Ӧ��.`�Ƴ�ʱ��` is null)"
//					+ "		where Date(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`) <= Date('" +  formatedenddate + "')"
//					+ "		and ��Ʊͨ���Ž�����ָ����Ӧ��.`ָ�����` = '" + currentbkcode + "' and ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����` = '" + stockcode +"'"
//					
//					+ "		group by  ��Ʊ����,  ���"
//					
//					;
//			
//		} else {
//			diyusqlquerystat = 					 "SELECT ͨ���������Ʊÿ�ս�����Ϣ.`����` AS ��Ʊ����, ��Ʊͨ���ŷ�����Ӧ��.����� AS ���, sum(ͨ���������Ʊÿ�ս�����Ϣ.`�ɽ���`) AS �ɽ���"
//					+ "		from ͨ���������Ʊÿ�ս�����Ϣ join"
//					+ "		     ��Ʊͨ���ŷ�����Ӧ��"
//					+ "		     on ͨ���������Ʊÿ�ս�����Ϣ.`����` = ��Ʊͨ���ŷ�����Ӧ��.`��Ʊ����`"
//					+ "			  and ͨ���������Ʊÿ�ս�����Ϣ.`��������` >= ��Ʊͨ���ŷ�����Ӧ��.`����ʱ��` and"
//					+ "		        (ͨ���������Ʊÿ�ս�����Ϣ.`��������` <= ��Ʊͨ���ŷ�����Ӧ��.`�Ƴ�ʱ��` or ��Ʊͨ���ŷ�����Ӧ��.`�Ƴ�ʱ��` is null)"
//					+ "		where Date(ͨ���������Ʊÿ�ս�����Ϣ.`��������`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(ͨ���������Ʊÿ�ս�����Ϣ.`��������`) <= Date('" +  formatedenddate + "')"
//					+ "		and ��Ʊͨ���ŷ�����Ӧ��.`�����` = '" + currentbkcode + "' and ͨ���������Ʊÿ�ս�����Ϣ.`����` = '" + stockcode +"'"
//					+ "		group by ͨ���������Ʊÿ�ս�����Ϣ.`����`, ��Ʊͨ���ŷ�����Ӧ��.`�����`"
//					
//					+ " UNION "
//					
//					+ "SELECT ͨ���������Ʊÿ�ս�����Ϣ.`����` AS ��Ʊ����, ��Ʊͨ������ҵ����Ӧ��.��ҵ��� AS ���, sum(ͨ���������Ʊÿ�ս�����Ϣ.`�ɽ���`) AS �ɽ���"
//					+ "		from ͨ���������Ʊÿ�ս�����Ϣ join"
//					+ "		     ��Ʊͨ������ҵ����Ӧ��"
//					+ "		     on ͨ���������Ʊÿ�ս�����Ϣ.`����` = ��Ʊͨ������ҵ����Ӧ��.`��Ʊ����`"
//					+ "			  and ͨ���������Ʊÿ�ս�����Ϣ.`��������` >= ��Ʊͨ������ҵ����Ӧ��.`����ʱ��` and"
//					+ "		        (ͨ���������Ʊÿ�ս�����Ϣ.`��������` <= ��Ʊͨ������ҵ����Ӧ��.`�Ƴ�ʱ��` or ��Ʊͨ������ҵ����Ӧ��.`�Ƴ�ʱ��` is null)"
//					+ "		where Date(ͨ���������Ʊÿ�ս�����Ϣ.`��������`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(ͨ���������Ʊÿ�ս�����Ϣ.`��������`) <= Date('" +  formatedenddate + "')"
//					+ "		and ��Ʊͨ������ҵ����Ӧ��.`��ҵ���` = '" + currentbkcode + "' and ͨ���������Ʊÿ�ս�����Ϣ.`����` = '" + stockcode +"'"
//					+ "		group by ͨ���������Ʊÿ�ս�����Ϣ.`����`, ��Ʊͨ������ҵ����Ӧ��.`��ҵ���`"
//					
//					+ " UNION "
//					
//					+  "SELECT ͨ���������Ʊÿ�ս�����Ϣ.`����` AS ��Ʊ����, ��Ʊͨ���Ÿ������Ӧ��.������ AS ���, sum(ͨ���������Ʊÿ�ս�����Ϣ.`�ɽ���`) AS �ɽ���"
//					+ "		from ͨ���������Ʊÿ�ս�����Ϣ join"
//					+ "		     ��Ʊͨ���Ÿ������Ӧ��"
//					+ "		     on ͨ���������Ʊÿ�ս�����Ϣ.`����` = ��Ʊͨ���Ÿ������Ӧ��.`��Ʊ����`"
//					+ "			  and ͨ���������Ʊÿ�ս�����Ϣ.`��������` >= ��Ʊͨ���Ÿ������Ӧ��.`����ʱ��` and"
//					+ "		        (ͨ���������Ʊÿ�ս�����Ϣ.`��������` <= ��Ʊͨ���Ÿ������Ӧ��.`�Ƴ�ʱ��` or ��Ʊͨ���Ÿ������Ӧ��.`�Ƴ�ʱ��` is null)"
//					+ "		where Date(ͨ���������Ʊÿ�ս�����Ϣ.`��������`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(ͨ���������Ʊÿ�ս�����Ϣ.`��������`) <= Date('" +  formatedenddate + "')"
//					+ "		and ��Ʊͨ���Ÿ������Ӧ��.`������` = '" + currentbkcode + "' and ͨ���������Ʊÿ�ս�����Ϣ.`����` = '" + stockcode +"'"
//					+ "		group by ͨ���������Ʊÿ�ս�����Ϣ.`����`, ��Ʊͨ���Ÿ������Ӧ��.`������`"
//					
//					+ " UNION "
//					
//					+  "SELECT ͨ���������Ʊÿ�ս�����Ϣ.`����` AS ��Ʊ����, ��Ʊͨ���Ž�����ָ����Ӧ��.ָ����� AS ���, sum(ͨ���������Ʊÿ�ս�����Ϣ.`�ɽ���`) AS �ɽ���"
//					+ "		from ͨ���������Ʊÿ�ս�����Ϣ join"
//					+ "		     ��Ʊͨ���Ž�����ָ����Ӧ��"
//					+ "		     on ͨ���������Ʊÿ�ս�����Ϣ.`����` = ��Ʊͨ���Ž�����ָ����Ӧ��.`��Ʊ����`"
//					+ "			  and ͨ���������Ʊÿ�ս�����Ϣ.`��������` >= ��Ʊͨ���Ž�����ָ����Ӧ��.`����ʱ��` and"
//					+ "		        (ͨ���������Ʊÿ�ս�����Ϣ.`��������` <= ��Ʊͨ���Ž�����ָ����Ӧ��.`�Ƴ�ʱ��` or ��Ʊͨ���Ž�����ָ����Ӧ��.`�Ƴ�ʱ��` is null)"
//					+ "		where Date(ͨ���������Ʊÿ�ս�����Ϣ.`��������`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(ͨ���������Ʊÿ�ս�����Ϣ.`��������`) <= Date('" +  formatedenddate + "')"
//					+ "		and ��Ʊͨ���Ž�����ָ����Ӧ��.`ָ�����` = '" + currentbkcode + "' and ͨ���������Ʊÿ�ս�����Ϣ.`����` = '" + stockcode +"'"
//					
//					+ "		group by  ��Ʊ����,  ���"
//					
//					;
//		}
//		System.out.println(diyusqlquerystat);
//		CachedRowSetImpl rsdy = null;
//		double dy = 0.0;
//		try {  
//			 rsdy = connectdb.sqlQueryStatExecute(diyusqlquerystat);
//			 while(rsdy.next()) {
//				 dy = rsdy.getDouble("�ɽ���");
//				 System.out.println(dy);
//			 }
//		}catch(java.lang.NullPointerException e){ 
//			System.out.println( "���ݿ�����ΪNULL!");
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}catch(Exception e){
//			e.printStackTrace();
//		} finally {
//			try {
//				if(rsdy != null)
//					rsdy.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			rsdy = null;
//		}
//		
//		return dy;
//	}
	/*
	 * �ҳ�ĳͨ������ҵ�������и���
	 */
//	private HashMap<String,String> getTDXHangYeBanKuaiGeGu(String currentbk) 
//	{
//		String sqlquerystat = "SELECT hy.��Ʊ����, agu.��Ʊ����" 
//				+ " FROM ��Ʊͨ������ҵ����Ӧ��  AS hy, A�� AS agu"
//				+ " WHERE  hy.��ҵ���= " + "'" + currentbk + "'" 
//				+ " AND hy.��Ʊ����  = agu.��Ʊ����" + "'"
//				+ " AND ISNULL(�Ƴ�ʱ��)"
//				//+ " GROUP BY  czjl.�����˺�, czjl.��Ʊ����, agu.��Ʊ����"
//				;   
//		System.out.println(sqlquerystat);
//		HashMap<String,String> tmpgnset = new HashMap<String,String>();
//		CachedRowSetImpl rsfg = null;
//		try {  
//			 rsfg = connectdb.sqlQueryStatExecute(sqlquerystat);
//			
//			rsfg.last();  
//			int rows = rsfg.getRow();  
//			rsfg.first();  
//			for(int j=0;j<rows;j++) {  
//				String tmpstockcode = rsfg.getString("��Ʊ����");
//				String tmpstockname = rsfg.getString("��Ʊ����");
//				tmpgnset.put(tmpstockcode,tmpstockname);
//				rsfg.next();
//			}
//			
//		}catch(java.lang.NullPointerException e){ 
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}catch(Exception e){
//			e.printStackTrace();
//		} finally {
//			 try {
//				rsfg.close ();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			 rsfg = null;
//		}
//		
//		return tmpgnset;
//	}
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
		System.out.println(sqlinsertstat);
		int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		
		return subcylcode;
		
	}
	
	
	public void setNewZdyBanKuai(String bkname, String bkcode) 
	{
		String dbbkname =  "zdy"+bkname.trim();
		String sqlinsertstat = "INSERT INTO  �Զ������б�(�������,����ʱ��,���ID) values ("
				+ "'" + dbbkname + "'" + ","
				+  "#" + CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now()) + "#" + ","
				+ "'" + bkcode.trim() + "'" 
				+ ")"
				;
		System.out.println(sqlinsertstat);
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
				System.out.println(sqlquerystat);
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
	
	public File preCheckTDXBanKuaiVolAmoToDb ()
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "ͬ��ͨ���Ű��ɽ���Ԥ���.tmp");
		
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
		
		HashMap<String, BanKuai> allsysbk = this.getTDXBanKuaiList ();
		ArrayList<String> allbkcode = new ArrayList<String>(allsysbk.keySet() );
		int found =0 ; int notfound = 0;
		try {
			Files.append("ϵͳ����"+ allbkcode.size() + "����飡"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		
			for(String tmpbkcode:allbkcode) {
				//String bkfilename = "SH" + filenamerule.replaceAll("YYXXXXXX", tmpbkcode);
				String bkfilename = (filenamerule.replaceAll("YY","SH")).replaceAll("XXXXXX", tmpbkcode);
				
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
	 * ͬ��ͨ���Ű���ÿ�ճɽ�����Ϣ
	 */
	public File refreshTDXBanKuaiVolAmoToDb ()
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "ͬ��ͨ���Ű��ɽ�������.tmp");
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
		
		HashMap<String, BanKuai> allsysbk = this.getTDXBanKuaiList ();
		ArrayList<String> allbkcode = new ArrayList<String>(allsysbk.keySet() );
		for(String tmpbkcode:allbkcode) {
			//String bkfilename = "SH" + filenamerule.replaceAll("YYXXXXXX", tmpbkcode);
			String bkfilename = (filenamerule.replaceAll("YY","SH")).replaceAll("XXXXXX", tmpbkcode);
			File tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {  
			    continue; 
			} 

				CachedRowSetImpl  rs = null;
				LocalDate ldlastestdbrecordsdate = null;
				try { 				
					String sqlquerystat = "SELECT  MAX(��������) 	MOST_RECENT_TIME"
							+ " FROM ͨ���Ű��ÿ�ս�����Ϣ  WHERE  ���� = " 
   							+ "'"  + tmpbkcode + "'" 
   							;
					System.out.println(sqlquerystat);
   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   			    	while(rs.next()) {
//   			    		System.out.println(rs.getMetaData().getColumnCount());
   			    		java.util.Date lastestdbrecordsdate = rs.getDate("MOST_RECENT_TIME"); //mOST_RECENT_TIME
	   			    	Instant instant = Instant.ofEpochMilli(lastestdbrecordsdate.getTime());
	   			    	ldlastestdbrecordsdate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()) .toLocalDate();
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
				
				setVolAmoRecordsFromFileToDatabase(tmpbkcode,tmpbkfile,ldlastestdbrecordsdate,"ͨ���Ű��ÿ�ս�����Ϣ",dateRule,tmprecordfile);
		}
		
		return tmprecordfile;
	}
	/*
	 * ͬ��ͨ���Ž�����ָ����ÿ�ճɽ�����Ϣ
	 */
	public File preCheckTDXZhiShuVolAmoToDb ()
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "ͬ��ͨ���Ž�����ָ���ɽ���Ԥ���.tmp");
		
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
		
		HashMap<String, BanKuai> allsysbk = this.getTDXAllZhiShuList ();
		ArrayList<String> allbkcode = new ArrayList<String>(allsysbk.keySet() );
		int found =0 ; int notfound = 0;
		try {
			Files.append("���ݿ⹲��"+ allbkcode.size() + "��ͨ���Ž�����ָ����"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		
			for(String tmpbkcode:allbkcode) {
				//String bkfilename = allsysbk.get(tmpbkcode).getBanKuaiJiaoYiSuo().trim() + filenamerule.replaceAll("YYXXXXXX", tmpbkcode);
				String bkfilename = (filenamerule.replaceAll("YY", allsysbk.get(tmpbkcode).getSuoShuJiaoYiSuo().trim())).replaceAll("XXXXXX", tmpbkcode);
				File tmpbkfile = new File(exportath + "/" + bkfilename);
					
					if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {
						Files.append(tmpbkcode + "δ�ҵ���¼�ļ���"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
						notfound ++;
					}
					 else {
						 //Files.append(tmpbkcode + "�м�¼�ļ���"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
						 found ++;
					 }
			}
			
			Files.append(found + "��ͨ���Ž�����ָ���м�¼�ļ���"+ notfound + "��ͨ���Ž�����ָ��û�м�¼�ļ���" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return tmprecordfile;
	}
	/*
	 * ͬ��ͨ����ָ��ÿ�ճɽ�����Ϣ
	 */
	public File refreshTDXZhiShuVolAmoToDb ()
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "ͬ��ͨ����ָ���ɽ�������.tmp");
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
			
		HashMap<String, BanKuai> allsysbk = this.getTDXAllZhiShuList ();
		ArrayList<String> allbkcode = new ArrayList<String>(allsysbk.keySet() );
		for(String tmpbkcode:allbkcode) {
//			if(tmpbkcode.equals("000959"))
//				System.out.println("000959 arrived");
			
			String bkfilename = (filenamerule.replaceAll("YY", allsysbk.get(tmpbkcode).getSuoShuJiaoYiSuo().trim())).replaceAll("XXXXXX", tmpbkcode); 
			File tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {  
			    continue; 
			} 

				CachedRowSetImpl  rs = null;
				LocalDate ldlastestdbrecordsdate = null;
				try { 				
					String sqlquerystat1 = "SELECT MAX(��������) 	MOST_RECENT_TIME"
							+ " FROM ͨ���Ž�����ָ��ÿ�ս�����Ϣ  WHERE  ���� = " 
   							+ "'"  + tmpbkcode + "'" 
   							;
					System.out.println(sqlquerystat1);
   			    	 rs = connectdb.sqlQueryStatExecute(sqlquerystat1);
   			    	//rs.first();
   			    	while(rs.next()) {
   			    		java.util.Date lastestdbrecordsdate = rs.getDate("MOST_RECENT_TIME");
   			    		Instant instant = Instant.ofEpochMilli(lastestdbrecordsdate.getTime());
   			    		ldlastestdbrecordsdate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
   			    		System.out.println(lastestdbrecordsdate);
   			    	}
   			    	
   			    }catch(java.lang.NullPointerException e){ 
//   			    	e.printStackTrace();
   			    	System.out.println(e.getMessage());
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
				
				setVolAmoRecordsFromFileToDatabase(tmpbkcode,tmpbkfile,ldlastestdbrecordsdate,"ͨ���Ž�����ָ��ÿ�ս�����Ϣ",dateRule,tmprecordfile);
		}
		
		return tmprecordfile;
	}

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
                        			System.out.println(sqlinsertstat);
                    				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
                    				lineimportcount ++;
                        		} else if(curlinedate.compareTo(lastestdbrecordsdate) == 0) {
                        			finalneededsavelinefound = true;
                        			Files.append(tmpbkcode + "ָ���ɽ�����¼������ʼʱ��Ϊ:" + line + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        			Files.append(tmpbkcode + "������:" + lineimportcount + "����¼" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        		}
                    		} catch (Exception e) {
//                    			e.printStackTrace();
                    			System.out.println("���������ڵ�������");
                    		}
                        }
                    } //else {  
//                        System.out.println(line);  
//                    }  
                    nextend--;  
                }  
                nextend--;  
                rf.seek(nextend);  
                if (nextend == 0) {// ���ļ�ָ�������ļ���ʼ���������һ��   �ļ���һ�����ļ�̧ͷ������Ҫ����
                    //System.out.println(new String(rf.readLine().getBytes("ISO-8859-1"), charset ));  
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
	 * ���������������ָ�� ���µ�BkChanYeLianTreeNode�洢
	 */
//	public HashMap<String, BkChanYeLianTreeNode> getTDXAllZhiShuList2() 
//	{
//		HashMap<String,BkChanYeLianTreeNode> tmpsysbankuailiebiaoinfo = new HashMap<String,BkChanYeLianTreeNode> ();
//
//		String sqlquerystat = "SELECT *  FROM ͨ���Ž�����ָ���б� WHERE ���ID IS NOT NULL"
//							   ;   
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rs = null;
//	    try {  
//	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//	    	
//	        rs.last();  
//	        int rows = rs.getRow();  
//	        rs.first();  
//	        for(int j=0;j<rows;j++) {  
//	        	BkChanYeLianTreeNode tmpbk = new BanKuai (rs.getString("���ID"),rs.getString("�������"));
//	        	//tmpbk.setBanKuaiJiaoYiSuo(rs.getString("ָ������������"));
//	        	tmpsysbankuailiebiaoinfo.put(rs.getString("���ID"), tmpbk);
//	            rs.next();
//	        }
//	        
//	    }catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    } catch (SQLException e) {
//	    	e.printStackTrace();
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    } finally {
//	    	if(rs != null)
//				try {
//					rs.close();
//					rs = null;
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//	    } 
//	    
//	    return tmpsysbankuailiebiaoinfo;
//	}

	
	public Date getTDXRelatedTableLastModfiedTime() 
	{
		Date lastesttdxtablesmdftime = null;
		
		HashMap<String,String> sqlstatmap = new HashMap<String,String> (); 
		String sqlquerystat = " SELECT MAX(�������¸���ʱ��) FROM ͨ���Ű���б�" 
						   ;
		sqlstatmap.put("mysql", sqlquerystat);
			
			CachedRowSetImpl rs = null; 
		    try {  
		    	 rs = connectdb.sqlQueryStatExecute(sqlstatmap);
		    	 
		    	 java.sql.Timestamp tmplastmdftime = null;
		    	 while(rs.next())
		    		  tmplastmdftime = rs.getTimestamp("MAX(�������¸���ʱ��)");
		    	 System.out.println(tmplastmdftime);
		    	 lastesttdxtablesmdftime = tmplastmdftime;

		    }catch(java.lang.NullPointerException e){
		    	lastesttdxtablesmdftime = null;
		    	e.printStackTrace();
		    } catch (SQLException e) {
		    	System.out.println(sqlquerystat);
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
		    
	   sqlquerystat = " SELECT MAX(�������¸���ʱ��) FROM ͨ���Ž�����ָ���б�"   ;
		CachedRowSetImpl rszs = null; 
	    try {  
	    	 rszs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	 
	    	 java.sql.Timestamp tmplastmdftime = null;
	    	 while(rszs.next())
	    		  tmplastmdftime = rszs.getTimestamp("MAX(�������¸���ʱ��)");
	    	 System.out.println(tmplastmdftime);
	    	 if(tmplastmdftime.after(lastesttdxtablesmdftime))
	    		 lastesttdxtablesmdftime = tmplastmdftime;
	    }catch(java.lang.NullPointerException e){
	    	lastesttdxtablesmdftime = null;
	    	e.printStackTrace();
	    } catch (SQLException e) {
	    	System.out.println(sqlquerystat);
	    	lastesttdxtablesmdftime = null;
	    	e.printStackTrace();
	    }catch(Exception e){
	    	lastesttdxtablesmdftime = null;
	    	e.printStackTrace();
	    }  finally {
	    	if(rszs != null)
				try {
					rszs.close();
					rszs = null;
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
		List<String> rmtservertable = Lists.newArrayList("ͨ���Ű���б�", "ͨ���Ž�����ָ���б�");
		
		
			String sqlquerystat =  " show OPEN TABLES where In_use > 0" ;
			//sqlquerystat = "SHOW OPEN TABLES WHERE `Table` LIKE '%[a��]%' AND `Database` LIKE '[stockinfomanagementtest]' AND In_use > 0";		   
			System.out.println(sqlquerystat);
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
	 * 
	 */
	private String getTdxBanKuaiNameByCode(String tmpbkcode) 
	{
		String sqlquerystatfrombk     = "SELECT �������  FROM ͨ���Ű���б�             WHERE ���ID = " + tmpbkcode;
		String sqlquerystatfromzhishu = "SELECT �������  FROM ͨ���Ž�����ָ���б�   WHERE ���ID = " + tmpbkcode;
		
		System.out.println(sqlquerystatfrombk);
		String bkname = null ;
		CachedRowSetImpl rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystatfrombk);
	    	
	        rs.last();  
	        int rows = rs.getRow(); 
	        if(rows  == 0) {
	        	rs = connectdb.sqlQueryStatExecute(sqlquerystatfromzhishu);
	        	rs.last();
	        	rows = rs.getRow();
	        }
	        rs.first();
	        if(rows>0)
	        	bkname = rs.getString(1);
	        
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
	
		
		return bkname;
	}
	/*
	 * ���Ӱ������
	 */
	public boolean addBanKuaiNews (String bankuaiid,ChanYeLianNews newdetail)
	{
		int autoIncKeyFromApi = newdetail.getNewsId();
		String title = newdetail.getNewsTitle();
		LocalDate newdate = newdetail.getGenerateDate();
		String slackurl = newdetail.getNewsSlackUrl();
		String keywords = newdetail.getKeyWords ();
		
		if( autoIncKeyFromApi == -1) { //˵�����µ�NEWS sysconfig.formatDate( actionday )
			//��update��ҵ���ű�
			String sqlinsertstat = "INSERT INTO ��ҵ����(���ű���,�ؼ���,SLACK����,¼������,�������) values ("
					+ "'" + title + "'" + ","
					+ "'" + keywords + "'" + ","
					+ "'" + slackurl  + "'" + ","
					+ "'" +  newdate + "'" + ","
					+ "'" +  bankuaiid + "|" + "'"
					+ ")"
					;
			System.out.println(sqlinsertstat);
			 autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat) ;
		} else {
			//������id�ǲ����Ѿ������ŵİ��list�����ˣ��Ѿ��ӹ��ˡ�
			String  updatestat = "UPDATE ��ҵ���� "
								  + " SET ������� = "
								  + " CASE WHEN ������� like '%" + bankuaiid + "%' THEN"
								  + " CONCAT(�������,  ' ') " 
								  + " ELSE " 
								  + " CONCAT(�������,  '"+ bankuaiid + "|' )"
								  +	" END" 
								  + " WHERE ID=" +  autoIncKeyFromApi
								  ;
			System.out.println(updatestat);
			connectdb.sqlUpdateStatExecute (updatestat);
		}
		
		
		//�ٰ�����id update���������ָ����
//		boolean inzhishutable = false;
//		String checkstat = "SELECT COUNT(1) FROM ͨ���Ž�����ָ���б� WHERE ���ID = '" + bankuaiid + "'";
//		CachedRowSetImpl rs = null; 
//	    try {  
//	    	 rs = connectdb.sqlQueryStatExecute(checkstat);
//	    	 while(rs.next()) {
//	    		 int exists = rs.getInt("COUNT(1)");
//		    	 if(exists == 1)
//		    		 inzhishutable = true;
//	    	 }
//	    	 
//	    }catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    } catch (SQLException e) {
//	    	e.printStackTrace();
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    }  finally {
//	    	if(rs != null)
//				try {
//					rs.close();
//					rs = null;
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//	    }
//	    
//	    String updatestat;
//	    if(inzhishutable) 
//	    	updatestat = "UPDATE ͨ���Ž�����ָ���б�"  
//	    				+ "  SET ������� ="
//	    				+ " CASE WHEN ������� IS NULL THEN "
//	    				+  "'" + autoIncKeyFromApi + "|'"
//	    				+ "  ELSE "
//	    				+ "  CONCAT(�������," + "'" + autoIncKeyFromApi + "|')" 
//	    				+ " END "
//	    				+ " WHERE ���ID='" +  bankuaiid.trim() + "'"
//	    				;
//	     else
//	    	updatestat = "UPDATE ͨ���Ű���б�"  
//	    				+ "  SET ������� ="
//	    				+ " CASE WHEN ������� IS NULL THEN "
//	    				+  "'" + autoIncKeyFromApi + "|'"
//	    				+ "  ELSE "
//	    				+ "  CONCAT(�������," + "'" + autoIncKeyFromApi + "|')" 
//	    				+ " END "
//	    				+ " WHERE ���ID='" +  bankuaiid.trim() + "'"
//	    				;
//	    
//		System.out.println(updatestat);
//		connectdb.sqlUpdateStatExecute (updatestat);
		
		return true;
	}

	public void deleteBanKuaiNews(String myowncode, ChanYeLianNews aseltnews)
	{
		int newsid = aseltnews.getNewsId();
		
		if("ALL".equals(myowncode.toUpperCase())) { //ɾ��������
			String deletestat = "DELETE  FROM ��ҵ���� WHERE ID=" + newsid;
			
			System.out.println(deletestat);
			connectdb.sqlUpdateStatExecute (deletestat);
			
//			updatestat = "UPDATE ͨ���Ű���б�  SET ������� = REPLACE (�������, '" + String.valueOf(newsid).trim() + "|', ' ' ) WHERE ���ID='" + myowncode + "'";
//			System.out.println(updatestat);
//			connectdb.sqlUpdateStatExecute (updatestat);
//			
//			updatestat = "UPDATE ͨ���Ž�����ָ���б�  SET ������� = REPLACE (�������, '" + String.valueOf(newsid).trim() + "|', ' ' ) WHERE ���ID='" + myowncode + "'";
//			System.out.println(updatestat);
//			connectdb.sqlUpdateStatExecute (updatestat);
		} else { //ɾ�������а���������
			String updatestat = "UPDATE ��ҵ����  SET ������� = REPLACE (�������, '" + myowncode.trim() + "|', ' ' ) WHERE ID=" + newsid;
			System.out.println(updatestat);
			connectdb.sqlDeleteStatExecute(updatestat);
			
//			String  updatestat = "UPDATE ͨ���Ű���б� "
//					  + " SET ������� = "
//					  + " CASE WHEN ������� like '%" + String.valueOf(newsid)+"|"  + "%' THEN"
//					  + " CONCAT(�������,  ' ') " 
//					  + " ELSE " 
//					  + " CONCAT(�������,  '"+ bankuaiid + "|' )"
//					  +	" END" 
//					  + " WHERE ID=" +  autoIncKeyFromApi
//					  ;
//			System.out.println(updatestat);
//			connectdb.sqlUpdateStatExecute (updatestat);
			
		}
		
		
		
	}
	
	public ArrayList<ChanYeLianNews> getBanKuaiRelatedNews(String bankuaiid) 
	{
		ArrayList<ChanYeLianNews> newslist = new ArrayList<ChanYeLianNews>();
		if("ALL".equals(bankuaiid.toUpperCase()) ) {
			String sqlquerystat = "SELECT * FROM ��ҵ����   WHERE ¼������ >= DATE(NOW()) - INTERVAL 40 DAY ORDER BY  ¼������ DESC"
									;
			CachedRowSetImpl rs = null; 
		    try {  
		    	System.out.println(sqlquerystat);
		    	 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	 
		    	 while(rs.next()) {
		    		 ChanYeLianNews cylnew = new ChanYeLianNews ();
		    		 int newsid = rs.getInt("id");
		    		 java.util.Date selectednew = rs.getDate("¼������");
		    		 String newtitle = rs.getString("���ű���");
		    		 String keywords = rs.getString("�ؼ���");
		    		 String slack = rs.getString("SLACK����");
		    		 String relatedbk = rs.getString("�������");

		    		 cylnew.setNewsId(newsid);
		    		 cylnew.setGenerateDate (Instant.ofEpochMilli(selectednew.getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
		    		 cylnew.setNewsTitle(newtitle);
		    		 cylnew.setNewsSlackUrl(slack);
		    		 cylnew.setKeyWords (keywords);
		    		 cylnew.setNewsRelatedBanKuai(relatedbk);
		    		 
		    		 newslist.add(cylnew);
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
			
			return newslist;
		}
		
		//�������/��������
		String sqlquerystat = "SELECT * FROM ��ҵ����   WHERE ������� like '%" + bankuaiid.trim()+"|"  + "%' ORDER BY  ¼������ DESC";
		CachedRowSetImpl rs = null;
		try {
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   		 	while(rs.next()) {
	   		 	ChanYeLianNews cylnew = new ChanYeLianNews ();
	   			 int newsid = rs.getInt("id");
	   			 java.util.Date selectednew = rs.getDate("¼������");
		    	 String newtitle = rs.getString("���ű���");
		    	 String keywords = rs.getString("�ؼ���");
		    	 String slack = rs.getString("SLACK����");
		    		 
		    	 cylnew.setNewsId(newsid);
		    	 cylnew.setGenerateDate ( Instant.ofEpochMilli(selectednew.getTime()).atZone(ZoneId.systemDefault()).toLocalDate() );
		    	 cylnew.setNewsTitle(newtitle);
		    	 cylnew.setNewsSlackUrl(slack);
		    	 cylnew.setKeyWords (keywords);
		    		 
		    	 newslist.add(cylnew);
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
		return newslist;
		
//		String sqlquerystat = "SELECT ������� FROM ͨ���Ű���б�   WHERE ���ID =  " + bankuaiid
//							   + " UNION "
//							   + "SELECT �������  FROM ͨ���Ž�����ָ���б�   WHERE ���ID =  " + bankuaiid
//							   ;
//		
//		CachedRowSetImpl rs = null; 
//	    try {
//	    	List<String> tmpbknewslist = null;
//	    	System.out.println(sqlquerystat);
//	    	 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//	    	 while(rs.next()) {
//	    		 String tmpnewids = rs.getString("�������");
//	    		 tmpbknewslist = Splitter.on("|").omitEmptyStrings().splitToList(tmpnewids); //
//	    	 }
//	    	 
//	    	 for(String newid:tmpbknewslist) {
//	    		 ChanYeLianNews cylnew = new ChanYeLianNews ();
//	    		 String sqlquerystat2 = "SELECT * FROM ��ҵ����   WHERE ID = '" + newid.trim().substring(2, newid.trim().length()) + "'"; //�������Ŷ�Ӧ��ID��ID+ID�ţ���Ϊ�����滻ʱ���ֹ����ID��|�ظ������
//	    		 rs = connectdb.sqlQueryStatExecute(sqlquerystat2);
//	    		 while(rs.next()) {
//	    			 int newsid = rs.getInt("id");
//	    			 Date selectednew = rs.getDate("¼������");
//		    		 String newtitle = rs.getString("���ű���");
//		    		 String keywords = rs.getString("�ؼ���");
//		    		 String slack = rs.getString("SLACK����");
//		    		 
//		    		 cylnew.setNewsId(newsid);
//		    		 cylnew.setGenerateDate (selectednew);
//		    		 cylnew.setNewsTitle(newtitle);
//		    		 cylnew.setNewsSlackUrl(slack);
//		    		 cylnew.setKeyWords (keywords);
//		    		 
//		    		 newslist.add(cylnew);
//	    		 }
//	    		 
//	    	 }
//	    }catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    } catch (SQLException e) {
//	    	e.printStackTrace();
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    }  finally {
//	    	if(rs != null)
//				try {
//					rs.close();
//					rs = null;
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//	    }
//		
//		return newslist;
	}
	/*
	 * �жϰ����ʲô���͵İ�飬����/���/ָ��/��ҵ/����
	 */
	private HashMap<String,String> getActionRelatedTables (String bkcode,String stockcode)
	{
		HashMap<String,String> actiontables = new HashMap<String,String> ();
		if(!Strings.isNullOrEmpty(bkcode)) {
			String stockvsbktable =null;
				if(stockvsbktable == null) {	
					CachedRowSetImpl rs = null;
					int dy = -1;
					try {  
						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM ��Ʊͨ���Ÿ������Ӧ��  WHERE ������='" + bkcode + "'";
						System.out.println(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							 dy = rs.getInt("RESULT");
	//						 System.out.println(dy);
						 }
					}catch(java.lang.NullPointerException e){ 
						System.out.println( "���ݿ�����ΪNULL!");
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
						System.out.println(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							  dy = rs.getInt("RESULT");
//							 System.out.println(dy1);
						 }
					}catch(java.lang.NullPointerException e){ 
						System.out.println( "���ݿ�����ΪNULL!");
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
						System.out.println(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							 dy = rs.getInt("RESULT");
//							 System.out.println(dy);
						 }
					}catch(java.lang.NullPointerException e){ 
						System.out.println( "���ݿ�����ΪNULL!");
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
						System.out.println(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							 dy = rs.getInt("RESULT");
//							 System.out.println(dy);
						 }
					}catch(java.lang.NullPointerException e) {
						e.printStackTrace();
						System.out.println( "���ݿ�����ΪNULL!");
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
				try {
					CachedRowSetImpl rsdy = null;
					int dy = -1;
					try {
						String diyusqlquerystat = "SELECT ��ӦTDXSWID  FROM ͨ���Ű���б� WHERE ���id = '" + bkcode + "'" ;
						System.out.println(diyusqlquerystat);
						 rsdy = connectdb.sqlQueryStatExecute(diyusqlquerystat);
						 while(rsdy.next()) {
							 dy = rsdy.getInt("��ӦTDXSWID");
//							 System.out.println(dy);
						 }
					}catch(java.lang.NullPointerException e){ 
						System.out.println( "���ݿ�����ΪNULL!");
					} catch (SQLException e) {
//						e.printStackTrace();
						System.out.println("java.sql.SQLException: ���� 1 �е�ֵ (оƬ) ִ�� getInt ʧ�ܣ����ǵ����顣");
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
				if(stockvsbktable != null)
					actiontables.put("��Ʊ����Ӧ��", stockvsbktable);
				
				String bkcjltable = null;
				String bknametable = null;
				if(bkcjltable == null) {
					boolean inzhishutable = false;
					int result = -1;
					CachedRowSetImpl rspd = null;
					try {
						String sqlcheckstat = "SELECT EXISTS(SELECT * FROM ͨ���Ű��ÿ�ս�����Ϣ where ���� = '" + bkcode + "') AS result";
						System.out.println(sqlcheckstat);
						rspd = connectdb.sqlQueryStatExecute(sqlcheckstat);
			   		 	while(rspd.next())
			   		 		result = rspd.getInt("result");
					}catch(java.lang.NullPointerException e){ 
				    	e.printStackTrace();
				    } catch(Exception e){
				    	e.printStackTrace();
				    }  finally {
				    	try {
							rspd.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    	rspd = null;
			   		 	if(result > 0) {
			   		 		bkcjltable ="ͨ���Ű��ÿ�ս�����Ϣ";
			   		 		bknametable = "ͨ���Ű���б�";
			   		 	}
				    }
				}
				if(bkcjltable == null) {
					boolean inzhishutable = false;
					int result = -1;
					CachedRowSetImpl rspd = null;
					try {
						String sqlcheckstat = "SELECT EXISTS(SELECT * FROM ͨ���Ž�����ָ��ÿ�ս�����Ϣ where ���� = '" + bkcode + "') AS result";
						System.out.println(sqlcheckstat);
						rspd = connectdb.sqlQueryStatExecute(sqlcheckstat);
			   		 	while(rspd.next())
			   		 		result = rspd.getInt("result");
					}catch(java.lang.NullPointerException e){ 
				    	e.printStackTrace();
				    } catch(Exception e){
				    	e.printStackTrace();
				    }  finally {
				    	try {
							rspd.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    	rspd = null;
			   		 	if(result > 0)
			   		 	bkcjltable ="ͨ���Ž�����ָ��ÿ�ս�����Ϣ";
			   		 	bknametable = "ͨ���Ž�����ָ���б�";
				    }
				}
				
				if(bkcjltable != null) {
					actiontables.put("���ÿ�ս�������", bkcjltable);
				    actiontables.put("���ָ�����Ʊ�", bknametable);
				}
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
	public BanKuai getTDXBanKuaiGeGuOfHyGnFg(BanKuai currentbk,LocalDate selecteddatestart , LocalDate selecteddateend)
	{
		String currentbkcode = currentbk.getMyOwnCode();
		
		HashMap<String, String> actiontables = this.getActionRelatedTables(currentbkcode,null);
//		System.out.println(actiontables);
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
//			 System.out.println(sqlquerystat1);
			 System.out.println("�����" + currentbk.getMyOwnCode() + currentbk.getMyOwnName() + "Ѱ�Ҵ�" + formatedstartdate.toString() + "��" + formatedenddate.toString() + "ʱ����ڵĸ��ɣ�");
			 rs1 = connectdb.sqlQueryStatExecute(sqlquerystat1);
//			  ResultSetMetaData rsmd = rs1.getMetaData();
//			 String name = rsmd.getColumnName(3);
//			 String name2 = rsmd.getColumnName(2);
//			 String name1 = rsmd.getColumnName(1);
			 
			 while(rs1.next()) {  
				 
				String tmpstockcode = rs1.getString("��Ʊ����");
				
				String tmpstockname;
				try {
					tmpstockname = rs1.getString("��Ʊ����"); //"��Ʊ����"
				} catch (java.lang.NullPointerException e ) {
					tmpstockname = "";
				}
				
				Stock tmpstock = new Stock (tmpstockcode,tmpstockname);
				
				Integer weight = rs1.getInt("��ƱȨ��");
				HashMap<String,Integer>  tmpweight = new HashMap<String,Integer> () ;
				tmpweight.put(currentbkcode, weight);
				tmpstock.setGeGuSuoShuBanKuaiWeight(tmpweight);

				currentbk.addNewBanKuaiGeGu(tmpstock);
			}
				
			}catch(java.lang.NullPointerException e){ 
				e.printStackTrace();
//				System.out.println( "���ݿ�����ΪNULL!");
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
	 * �ҳ�ĳ��ʱ��� ��ҵ/����/���/ָ�� ͨ����ĳ���������и��ɼ�Ȩ�غͳɽ���������һ��������һ���ڵ����
	 */
	public HashMap<String, Stock> getTDXBanKuaiGeGuOfHyGnFgAndChenJiaoLIang(String currentbk,String currentbkcode, LocalDate selecteddatestart , LocalDate selecteddateend)
	{
//		if(currentbkcode.equals("000959"))
//			System.out.println("000959 arrived");
		
			HashMap<String, String> actiontables = this.getActionRelatedTables(currentbkcode,null);
//			System.out.println(actiontables);
			String bktypetable = actiontables.get("��Ʊ����Ӧ��");
			String bkorzsvoltable = actiontables.get("���ÿ�ս�������");
			String bknametable = actiontables.get("���ָ�����Ʊ�");
			if(bktypetable == null) {
				return null;
			}
			
			String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart);
			String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend);

			HashMap<String,Stock> gegumap = new HashMap<String,Stock> ();
			
			CachedRowSetImpl rs1 = null;
			try { 
				String sqlquerystat1 = "";
				if(bktypetable.trim().equals("��Ʊͨ���Ż�������Ϣ��Ӧ��") ) { //�ҵ�����
					String dycode = actiontables.get("�������");
					 sqlquerystat1 = "SELECT A.`������`, A.`�������`,A.category_amount, B.`��Ʊ����`, \"\" AS ��Ʊ����, B.stock_amount,   10 AS ��ƱȨ��,\r\n" + 
					 		"       B.stock_amount / A.category_amount ratio, '" + formatedenddate +"' as lastdayofweek  \r\n" + 
					 		"from \r\n" + 
					 		"( SELECT ��Ʊͨ���Ż�������Ϣ��Ӧ��.`��Ʊ����GPDM` AS ��Ʊ����, ��Ʊͨ���Ż�������Ϣ��Ӧ��.`����DY`, sum(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`�ɽ���`)  stock_amount\r\n" + 
					 		"from ��Ʊͨ���Ż�������Ϣ��Ӧ�� , ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ\r\n" + 
					 		"where ��Ʊͨ���Ż�������Ϣ��Ӧ��.`��Ʊ����GPDM`  = ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����`\r\n" + 
					 		"and ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` between ' " + formatedstartdate + "' and '" + formatedenddate + "'\r\n" + 
					 		"and ��Ʊͨ���Ż�������Ϣ��Ӧ��.`����DY` = '"+ dycode + "'\r\n" + 
					 		"group by ��Ʊͨ���Ż�������Ϣ��Ӧ��.`��Ʊ����GPDM`, ��Ʊͨ���Ż�������Ϣ��Ӧ��.`����DY`\r\n" + 
					 		"order by ��Ʊͨ���Ż�������Ϣ��Ӧ��.`��Ʊ����GPDM`, ��Ʊͨ���Ż�������Ϣ��Ӧ��.`����DY` \r\n" + 
					 		") B ,\r\n" + 
					 		"( select ͨ���Ű��ÿ�ս�����Ϣ.`����` AS ������  , ͨ���Ű���б�.`�������`, ͨ���Ű���б�.`��ӦTDXSWID` AS `����DY`,  sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) category_amount\r\n" + 
					 		"          from ͨ���Ű��ÿ�ս�����Ϣ, ͨ���Ű���б�\r\n" + 
					 		"         where ͨ���Ű��ÿ�ս�����Ϣ.`����` =  ͨ���Ű���б�.`���ID`\r\n" + 
					 		"			  and ͨ���Ű��ÿ�ս�����Ϣ.`��������` between ' " + formatedstartdate + "' and '" + formatedenddate + "'\r\n" + 
					 		"           and ͨ���Ű��ÿ�ս�����Ϣ.`����` =  '" + currentbkcode + "'\r\n" + 
					 		"         group by ͨ���Ű��ÿ�ս�����Ϣ.`����`\r\n" + 
					 		"         order by ͨ���Ű��ÿ�ս�����Ϣ.`����`\r\n" + 
					 		") A \r\n" + 
					 		"WHERE B.`����DY` = A.`����DY`"
					 		; 
					 		
				 } else { //��������ҵָ�����
					 //from WQW
					 sqlquerystat1 =" select A.`������`, B.`�������`,B.category_amount, A.`��Ʊ����`,  A.`��Ʊ����`,A.stock_amount,   A.`��ƱȨ��`,\r\n" + 
						 		"       \r\n" + 
						 		"       A.stock_amount / B.category_amount ratio, '" + formatedenddate + "' as lastdayofweek" + 
						 		"  from (select  X.`��Ʊ����`,a��.`��Ʊ����`, X.`������` ,X.stock_amount,X.`��ƱȨ��`\r\n" + 
						 		"FROM \r\n" + 
						 		"       (select " +  bktypetable +  ".`��Ʊ����` , " +  bktypetable +  ".`������` , sum(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`�ɽ���`) stock_amount," +  bktypetable +  ".`��ƱȨ��`\r\n" + 
						 		"          from " +  bktypetable +  ", ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ\r\n" + 
						 		"         where " +  bktypetable +  ".`��Ʊ����`   = ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`����`\r\n" + 
						 		"           and Date(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������`) >= Date(" +  bktypetable +  ".`����ʱ��`)\r\n" + 
						 		"           and ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` <  ifnull(" +  bktypetable +  ".`�Ƴ�ʱ��`, '2099-12-31')\r\n" + 
						 		"           and (ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` between '" +  formatedstartdate +  "' and '" +  formatedenddate +  "')\r\n" + 
						 		"           and " +  bktypetable +  ".`������` =  '" +  currentbkcode +  "'\r\n" + 
						 		"         \r\n" + 
						 		"         group by " +  bktypetable +  ".`��Ʊ����`, " +  bktypetable +  ".`������`\r\n" + 
						 		"         order by " +  bktypetable +  ".`��Ʊ����`, " +  bktypetable +  ".`������` ) X,\r\n" + 
						 		"         a��\r\n" + 
						 		"where  X.`��Ʊ����` = a��.`��Ʊ����`\r\n" + 
						 		"order by X.`��Ʊ����`,a��.`��Ʊ����`) A,\r\n" + 
						 		"         \r\n" + 
						 		"       (select " + bkorzsvoltable + ".`����` , " + bknametable + ".`�������`,   sum(" + bkorzsvoltable + ".`�ɽ���`) category_amount\r\n" + 
						 		"          from " + bkorzsvoltable + ", " + bknametable + "\r\n" + 
						 		"         where " + bkorzsvoltable + ".`����` =  " + bknametable + ".`���ID`\r\n" + 
						 		"			  and " + bkorzsvoltable + ".`��������` >= '" +  formatedstartdate +  "'\r\n" + 
						 		"           and " + bkorzsvoltable + ".`��������` <= '" +  formatedenddate +  "'\r\n" + 
						 		"           and " + bkorzsvoltable + ".`����` =  '" +  currentbkcode +  "'\r\n" + 
						 		"         group by " + bkorzsvoltable + ".`����`\r\n" + 
						 		"         order by " + bkorzsvoltable + ".`����`) B\r\n" + 
						 		" where A.`������`   = B.`����`\r\n" + 
						 		"\r\n" + 
						 		"  order by A.`������`,  A.`��Ʊ����`;"
								;
					 
				 }
				 System.out.println(sqlquerystat1);
				 rs1 = connectdb.sqlQueryStatExecute(sqlquerystat1);

				 while(rs1.next()) {  
					String tmpstockcode = rs1.getString("��Ʊ����");
					String tmpstockname;
					try {
						tmpstockname = rs1.getString("��Ʊ����"); //"��Ʊ����"
					} catch (java.lang.NullPointerException e ) {
						tmpstockname = "";
					}
					
					Stock tmpstock = new Stock (tmpstockcode,tmpstockname);
					
					Integer weight = rs1.getInt("��ƱȨ��");
					HashMap<String,Integer>  tmpweight = new HashMap<String,Integer> () ;
					tmpweight.put(currentbkcode, weight);
					tmpstock.setGeGuSuoShuBanKuaiWeight(tmpweight  );
					
//					ChenJiaoZhanBiInGivenPeriod cjlrecords = new ChenJiaoZhanBiInGivenPeriod ();
//					double stcokcje = rs1.getDouble("stock_amount");
//					double bkcje = rs1.getDouble("category_amount");
//					String lastdayofweek1 = rs1.getString("lastdayofweek");
//					cjlrecords.setDayofEndofWeek(CommonUtility.formateStringToDate(lastdayofweek1) );
//					cjlrecords.setMyOwnChengJiaoEr(stcokcje);
//					cjlrecords.setUpLevelChengJiaoEr(bkcje);
//					ArrayList<ChenJiaoZhanBiInGivenPeriod > tmpcjelist = new ArrayList<ChenJiaoZhanBiInGivenPeriod > ();
//					tmpcjelist.add(cjlrecords);
//					tmpstock.setChenJiaoErZhanBiInGivenPeriod (tmpcjelist);
					

					gegumap.put(tmpstockcode, tmpstock);
				}
					
				}catch(java.lang.NullPointerException e){ 
					e.printStackTrace();
//					System.out.println( "���ݿ�����ΪNULL!");
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
				
			 CachedRowSetImpl rs2 = null;
			 try {
					String sqlquerystat2 = "";
					if(bktypetable.trim().equals("��Ʊͨ���Ż�������Ϣ��Ӧ��") ) { 
						String dycode = actiontables.get("�������");
						sqlquerystat2 = "SELECT A.`������`, A.`�������`,A.category_amount, B.`��Ʊ����`, \"\" AS ��Ʊ����, B.stock_amount,   10 AS ��ƱȨ��,\r\n" + 
						 		"       B.stock_amount / A.category_amount ratio, '" + formatedenddate +"' as lastdayofweek  \r\n" + 
						 		"from \r\n" + 
						 		"( SELECT ��Ʊͨ���Ż�������Ϣ��Ӧ��.`��Ʊ����GPDM` AS ��Ʊ����, ��Ʊͨ���Ż�������Ϣ��Ӧ��.`����DY`, sum(ͨ���������Ʊÿ�ս�����Ϣ.`�ɽ���`)  stock_amount\r\n" + 
						 		"from ��Ʊͨ���Ż�������Ϣ��Ӧ�� , ͨ���������Ʊÿ�ս�����Ϣ\r\n" + 
						 		"where ��Ʊͨ���Ż�������Ϣ��Ӧ��.`��Ʊ����GPDM`  = ͨ���������Ʊÿ�ս�����Ϣ.`����`\r\n" + 
						 		"and ͨ���������Ʊÿ�ս�����Ϣ.`��������` between ' " + formatedstartdate + "' and '" + formatedenddate + "'\r\n" + 
						 		"and ��Ʊͨ���Ż�������Ϣ��Ӧ��.`����DY` = '"+ dycode + "'\r\n" + 
						 		"group by ��Ʊͨ���Ż�������Ϣ��Ӧ��.`��Ʊ����GPDM`, ��Ʊͨ���Ż�������Ϣ��Ӧ��.`����DY`\r\n" + 
						 		"order by ��Ʊͨ���Ż�������Ϣ��Ӧ��.`��Ʊ����GPDM`, ��Ʊͨ���Ż�������Ϣ��Ӧ��.`����DY` \r\n" + 
						 		") B ,\r\n" + 
						 		"( select ͨ���Ű��ÿ�ս�����Ϣ.`����` AS ������  , ͨ���Ű���б�.`�������`, ͨ���Ű���б�.`��ӦTDXSWID` AS `����DY`,  sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) category_amount\r\n" + 
						 		"          from ͨ���Ű��ÿ�ս�����Ϣ, ͨ���Ű���б�\r\n" + 
						 		"         where ͨ���Ű��ÿ�ս�����Ϣ.`����` =  ͨ���Ű���б�.`���ID`\r\n" + 
						 		"			  and ͨ���Ű��ÿ�ս�����Ϣ.`��������` between ' " + formatedstartdate + "' and '" + formatedenddate + "'\r\n" + 
						 		"           and ͨ���Ű��ÿ�ս�����Ϣ.`����` =  '" + currentbkcode + "'\r\n" + 
						 		"         group by ͨ���Ű��ÿ�ս�����Ϣ.`����`\r\n" + 
						 		"         order by ͨ���Ű��ÿ�ս�����Ϣ.`����`\r\n" + 
						 		") A \r\n" + 
						 		"WHERE B.`����DY` = A.`����DY`"
						 		;  
//									;
					 } else { //��������ҵָ�����
						 //from WQW
						 sqlquerystat2 =" select A.`������`, B.`�������`,B.category_amount, A.`��Ʊ����`,  A.`��Ʊ����`,A.stock_amount,   A.`��ƱȨ��`,\r\n" + 
							 		"       \r\n" + 
							 		"       A.stock_amount / B.category_amount ratio, '" + formatedenddate + "' as lastdayofweek" + 
							 		"  from (select  X.`��Ʊ����`,a��.`��Ʊ����`, X.`������` ,X.stock_amount,X.`��ƱȨ��`\r\n" + 
							 		"FROM \r\n" + 
							 		"       (select " +  bktypetable +  ".`��Ʊ����` , " +  bktypetable +  ".`������` , sum(ͨ���������Ʊÿ�ս�����Ϣ.`�ɽ���`) stock_amount," +  bktypetable +  ".`��ƱȨ��`\r\n" + 
							 		"          from " +  bktypetable +  ", ͨ���������Ʊÿ�ս�����Ϣ\r\n" + 
							 		"         where " +  bktypetable +  ".`��Ʊ����`   = ͨ���������Ʊÿ�ս�����Ϣ.`����`\r\n" + 
							 		"           and Date(ͨ���������Ʊÿ�ս�����Ϣ.`��������`) >= Date(" +  bktypetable +  ".`����ʱ��`)\r\n" + 
							 		"           and ͨ���������Ʊÿ�ս�����Ϣ.`��������` <  ifnull(" +  bktypetable +  ".`�Ƴ�ʱ��`, '2099-12-31')\r\n" + 
							 		"           and (ͨ���������Ʊÿ�ս�����Ϣ.`��������` between '" +  formatedstartdate +  "' and '" +  formatedenddate +  "')\r\n" + 
							 		"           and " +  bktypetable +  ".`������` =  '" +  currentbkcode +  "'\r\n" + 
							 		"         \r\n" + 
							 		"         group by " +  bktypetable +  ".`��Ʊ����`, " +  bktypetable +  ".`������`\r\n" + 
							 		"         order by " +  bktypetable +  ".`��Ʊ����`, " +  bktypetable +  ".`������` ) X,\r\n" + 
							 		"         a��\r\n" + 
							 		"where  X.`��Ʊ����` = a��.`��Ʊ����`\r\n" + 
							 		"order by X.`��Ʊ����`,a��.`��Ʊ����`) A,\r\n" + 
							 		"         \r\n" + 
							 		"       (select " + bkorzsvoltable + ".`����` , " + bknametable + ".`�������`,   sum(" + bkorzsvoltable + ".`�ɽ���`) category_amount\r\n" + 
							 		"          from " + bkorzsvoltable + ", " + bknametable + "\r\n" + 
							 		"         where " + bkorzsvoltable + ".`����` =  " + bknametable + ".`���ID`\r\n" + 
							 		"			  and " + bkorzsvoltable + ".`��������` >= '" +  formatedstartdate +  "'\r\n" + 
							 		"           and " + bkorzsvoltable + ".`��������` <= '" +  formatedenddate +  "'\r\n" + 
							 		"           and " + bkorzsvoltable + ".`����` =  '" +  currentbkcode +  "'\r\n" + 
							 		"         group by " + bkorzsvoltable + ".`����`\r\n" + 
							 		"         order by " + bkorzsvoltable + ".`����`) B\r\n" + 
							 		" where A.`������`   = B.`����`\r\n" + 
							 		"\r\n" + 
							 		"  order by A.`������`,  A.`��Ʊ����`;"
									;
					 }
					 System.out.println(sqlquerystat2);
					 rs2 = connectdb.sqlQueryStatExecute(sqlquerystat2);
					 while(rs2.next()) {  
						String tmpstockcode = rs2.getString("��Ʊ����");
						String tmpstockname;
						try {
							tmpstockname = rs2.getString("��Ʊ����"); //"��Ʊ����"
						} catch (java.lang.NullPointerException e ) {
							tmpstockname = "";
						}
						Stock tmpstock = new Stock (tmpstockcode,tmpstockname);
						
						Integer weight = rs2.getInt("��ƱȨ��");
						HashMap<String,Integer>  tmpweight = new HashMap<String,Integer> () ;
						tmpweight.put(currentbkcode, weight);
						tmpstock.setGeGuSuoShuBanKuaiWeight(tmpweight  );
						
//						ChenJiaoZhanBiInGivenPeriod cjlrecords = new ChenJiaoZhanBiInGivenPeriod ();
//						double stcokcje = rs2.getDouble("stock_amount");
//						double bkcje = rs2.getDouble("category_amount");
//						String lastdayofweek1 = rs2.getString("lastdayofweek");
//						cjlrecords.setDayofEndofWeek(CommonUtility.formateStringToDate(lastdayofweek1) );
//						cjlrecords.setMyOwnChengJiaoEr(stcokcje);
//						cjlrecords.setUpLevelChengJiaoEr(bkcje);
//						ArrayList<ChenJiaoZhanBiInGivenPeriod > tmpcjelist = new ArrayList<ChenJiaoZhanBiInGivenPeriod > ();
//						tmpcjelist.add(cjlrecords);
//						tmpstock.setChenJiaoErZhanBiInGivenPeriod (tmpcjelist);
						
	
						gegumap.put(tmpstockcode, tmpstock);
					}
						
					}catch(java.lang.NullPointerException e){ 
						e.printStackTrace();
	//					System.out.println( "���ݿ�����ΪNULL!");
					} catch (SQLException e) {
						e.printStackTrace();
					}catch(Exception e){
						e.printStackTrace();
					} finally {
						try {
							if(rs2 != null)
								rs2.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						rs2 = null;
					}

			 return gegumap;
	}
	/*
	 * ����趨ʱ�����ںʹ�����ȵ�ÿ��ռ��
	 */
	public  BanKuai getBanKuaiZhanBi (BanKuai bankuai,LocalDate selecteddatestart,LocalDate selecteddateend,LocalDate addposition)
	{
		String bkcode = bankuai.getMyOwnCode();
		HashMap<String, String> actiontables = this.getActionRelatedTables(bkcode,null);
		String bkcjltable = actiontables.get("���ÿ�ս�������");
		if(bkcjltable == null) //˵���ð��û�гɽ�����¼������Ҫ��¼ռ��
			return bankuai;
		
		String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart);
		String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend);
			
		String sqlquerystat = null;
		if(!bkcjltable.equals("ͨ���Ž�����ָ��ÿ�ս�����Ϣ")) { 
			sqlquerystat = "SELECT Year(BKJYE.��������) AS CALYEAR , WEEK(BKJYE.��������) AS CALWEEK, BKJYE.`����` AS BKCODE , BKJYE.��������, DATE(BKJYE.�������� + INTERVAL (6 - DAYOFWEEK(BKJYE.��������)) DAY) EndOfWeekDate,\r\n"
					+ "	       SUM(BKJYE.�ɽ���)/2 AS ����ܽ��׶�, SUM(ZSJYE.�ɽ���) AS �����ܽ��׶�, SUM(BKJYE.`�ɽ���`)/(2*SUM(ZSJYE.`�ɽ���`)) AS ռ�� \r\n"
					+ "	FROM  ͨ���Ű��ÿ�ս�����Ϣ BKJYE \r\n"
					+ "	INNER JOIN ͨ���Ž�����ָ��ÿ�ս�����Ϣ  ZSJYE \r\n"
					+ "	ON ZSJYE.���� in ('999999','399001' ) \r\n"
					+ "	       AND BKJYE.`��������` = ZSJYE.`��������` \r\n"
					+ "	       AND BKJYE.`��������` BETWEEN '" + formatedstartdate + "' AND'" + formatedenddate + "' \r\n"
					+ "	       AND BKJYE.`����` = '" + bkcode + "' \r\n"
					+ "			GROUP BY Year(BKJYE.`��������`),WEEK(BKJYE.`��������`),BKJYE.`����` \r\n"
					;
			
		} else { //�����ָ�����,�������ͬ���ڵĲ�ѯ
			sqlquerystat = "SELECT Year(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������) AS CALYEAR , WEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������) AS CALWEEK, ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`����` AS BKCODE , \r\n"
					+ "ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������, DATE(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�������� + INTERVAL (6 - DAYOFWEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������)) DAY) EndOfWeekDate, \r\n"
					+ "	sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�ɽ���) AS ����ܽ��׶�,	sum(sum_dapan) AS �����ܽ��׶�, sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�ɽ���) / sum(sum_dapan) AS ռ�� \r\n"
					+ " FROM ͨ���Ž�����ָ��ÿ�ս�����Ϣ , ( \r\n"
					+ "    SELECT ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�������� dapanjyrq,  SUM(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�ɽ���) sum_dapan \r\n"
					+ "    FROM ͨ���Ž�����ָ��ÿ�ս�����Ϣ   \r\n"
					+ "    where  ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`����` IN  ('999999','399001' )  \r\n"
					+ "    group by dapanjyrq \r\n"
					+ ") sq \r\n"
					+ " where  ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�������� = dapanjyrq AND ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`����` = '"  + bkcode + "' \r\n" 
					+ "	       AND ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`��������` between'" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n"
					+ " GROUP BY Year(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������)  , WEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������), ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`����` \r\n"
					; 
		}
		
//		System.out.println(sqlquerystat);
		System.out.println("Ϊ���" + bankuai.getMyOwnCode() + bankuai.getMyOwnName() + "Ѱ�Ҵ�" + selecteddatestart.toString() + "��" + selecteddateend.toString() + "ռ�����ݣ�");
		
		String sqlquerystatfx = "SELECT COUNT(*) AS RESULT FROM ������¼�ص��ע \r\n" + 
				"WHERE ��Ʊ����='" + bkcode + "'" + "\r\n" + 
				"AND ������¼�ص��ע.`����` between '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n"
				;
		
		CachedRowSetImpl rs = null;
		CachedRowSetImpl rsfx = null;
		Boolean hasfengxiresult = false;
		ArrayList<ChenJiaoZhanBiInGivenPeriod > tmpcjelist = new ArrayList<ChenJiaoZhanBiInGivenPeriod > ();
		try {
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			rsfx = connectdb.sqlQueryStatExecute(sqlquerystatfx);
			while(rsfx.next()) {
				Integer recnum = rsfx.getInt("RESULT");
				if(recnum >0)
					hasfengxiresult = true;
			}
			
			while(rs.next()) {
				ChenJiaoZhanBiInGivenPeriod cjlrecords = new ChenJiaoZhanBiInGivenPeriod ();
				double bankuaicje = rs.getDouble("����ܽ��׶�");
				double dapancje = rs.getDouble("�����ܽ��׶�");
				java.sql.Date lastdayofweek = rs.getDate("EndOfWeekDate");
//				int year = rs.getInt("CALYEAR");
//				int week = rs.getInt("CALWEEK");
				cjlrecords.setRecordsDayofEndofWeek(lastdayofweek);
				cjlrecords.setMyOwnChengJiaoEr(bankuaicje);
				cjlrecords.setUpLevelChengJiaoEr(dapancje);
//				cjlrecords.setYear(year);
//				cjlrecords.setWeek(week);
				
				tmpcjelist.add(cjlrecords);
				
				//�ҳ������������Ӧʱ��εķ������
				if(hasfengxiresult) {
					sqlquerystat = "SELECT COUNT(*) AS RESULT FROM ������¼�ص��ע \r\n" + 
							"WHERE ��Ʊ����='" + bkcode + "'" + "\r\n" + 
							"AND WEEK(������¼�ص��ע.`����`) = WEEK('" + lastdayofweek + "')" 
							;
					System.out.println(sqlquerystat);
					rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
					while(rsfx.next()) {
						Integer recnum = rsfx.getInt("RESULT");
						if(recnum >0)
							cjlrecords.setFengXiJIeGuo(true);
					}
				}
				
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
				rsfx.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		
		bankuai.addChenJiaoErZhanBiInGivenPeriod (tmpcjelist,addposition);
		return bankuai;
	}

	/*
	 * ��Ʊ�԰��İ���ռ��
	 */
	public Stock getGeGuZhanBiOfBanKuai(String bkcode, Stock stock,LocalDate selecteddatestart,LocalDate selecteddateend,LocalDate addposition)
	{
		String stockcode = stock.getMyOwnCode();
		
		HashMap<String, String> actiontables = this.getActionRelatedTables(bkcode,stockcode);

		String stockvsbktable = actiontables.get("��Ʊ����Ӧ��");
		String bkcjetable = actiontables.get("���ÿ�ս�������");
		String gegucjetable = actiontables.get("��Ʊÿ�ս�������");
		String bknametable = actiontables.get("���ָ�����Ʊ�");
		
		String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart);
		String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend);
		
		String sqlquerystat = "";
		if(stockvsbktable.trim().equals("��Ʊͨ���Ż�������Ϣ��Ӧ��") ) { //�ҵ�����
			String dycode = actiontables.get("�������");
			sqlquerystat = "";
			return stock;
		 } else { //��������ҵָ�����
		 sqlquerystat = "SELECT Y.year, Y.week,Y.EndOfWeekDate, Y.��Ʊ����, Y.������, Y.stock_amount, Y.`��ƱȨ��` ,Y.`��Ʊ����`,\r\n" + 
				"Z.���� , Z.�������,  z.category_amount, Y.stock_amount/ z.category_amount ratio \r\n" + 
				"FROM \r\n" + 
				"(select X.year, X.week, X.EndOfWeekDate, X.��Ʊ����, X.������, X.stock_amount, X.`��ƱȨ��` ,a��.`��Ʊ����`\r\n" + 
				"from (select year(" +  gegucjetable    + ".`��������`) as year,week(" +  gegucjetable    + ".`��������`) as week, \r\n" + 
				"DATE(" +  gegucjetable    + ".�������� + INTERVAL (6 - DAYOFWEEK(" +  gegucjetable    + ".��������)) DAY) EndOfWeekDate, \r\n" + 
				"" +  stockvsbktable + ".`��Ʊ����` , " +  stockvsbktable + ".`������` , \r\n" + 
				"sum(" +  gegucjetable    + ".`�ɽ���`) stock_amount," +  stockvsbktable + ".`��ƱȨ��`\r\n" + 
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
				"" +  bkcjetable + ".`����` , " + bknametable + ".`�������`,   sum(" +  bkcjetable + ".`�ɽ���`) category_amount\r\n" + 
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
//		System.out.println(sqlquerystat);
		System.out.println("Ϊ����" + stock.getMyOwnCode() + stock.getMyOwnName() + "Ѱ�Ҵ�" + selecteddatestart.toString() + "��" + selecteddateend.toString() + "��" + bkcode + "��ռ�����ݣ�");
		
		String sqlquerystatfx = "SELECT COUNT(*) AS RESULT FROM ������¼�ص��ע \r\n" + 
				"WHERE ��Ʊ����='" + stockcode + "'" + "\r\n" + 
				"AND ������¼�ص��ע.`����` between '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n"
				;

//		HashMap<String,Stock> gegumap = new HashMap<String,Stock> ();
		CachedRowSetImpl rsfg = null;
		CachedRowSetImpl rsfx = null;
		Boolean hasfengxiresult = false;
		try {  
			 rsfg = connectdb.sqlQueryStatExecute(sqlquerystat);
			 
			 rsfx = connectdb.sqlQueryStatExecute(sqlquerystatfx);
				while(rsfx.next()) {
					Integer recnum = rsfx.getInt("RESULT");
					if(recnum >0)
						hasfengxiresult = true;
				}

			 ArrayList<ChenJiaoZhanBiInGivenPeriod > tmpcjelist = new ArrayList<ChenJiaoZhanBiInGivenPeriod > ();
			 while(rsfg.next()) {
//				String tmpstockcode = rsfg.getString("��Ʊ����");
//				String tmpstockname = rsfg.getString("��Ʊ����"); //"��Ʊ����"
//				Stock tmpstock = new Stock (tmpstockcode,tmpstockname);
				
//				Integer weight = rsfg.getInt("��ƱȨ��");
//				HashMap<String,Integer>  tmpweight = new HashMap<String,Integer> () ;
//				tmpweight.put(bkcode, weight);
//				tmpstock.setGeGuSuoShuBanKuaiWeight(tmpweight  );
				
				ChenJiaoZhanBiInGivenPeriod cjlrecords = new ChenJiaoZhanBiInGivenPeriod ();
//				int year = rsfg.getInt("year");
//				int week = rsfg.getInt("week");
				double stcokcje = rsfg.getDouble("stock_amount");
				double bkcje = rsfg.getDouble("category_amount");
//				Date lastdayofweek1 = rsfg.getString("EndOfWeekDate");
				java.sql.Date  lastdayofweek = rsfg.getDate("EndOfWeekDate");
				cjlrecords.setRecordsDayofEndofWeek(lastdayofweek);
//				cjlrecords.setDayofEndofWeek(CommonUtility.formateStringToDate(lastdayofweek1) );
				cjlrecords.setMyOwnChengJiaoEr(stcokcje);
				cjlrecords.setUpLevelChengJiaoEr(bkcje);
//				cjlrecords.setYear(year);
//				cjlrecords.setWeek(week);
				
				tmpcjelist.add(cjlrecords);
//				tmpstock.setChenJiaoErZhanBiInGivenPeriod (tmpcjelist);
//
//				gegumap.put(tmpstockcode, tmpstock);
				//�ҳ������������Ӧʱ��εķ������
				if(hasfengxiresult) {
					sqlquerystat = "SELECT COUNT(*) AS RESULT FROM ������¼�ص��ע \r\n" + 
							"WHERE ��Ʊ����='" + stockcode + "'" + "\r\n" + 
							"AND WEEK(������¼�ص��ע.`����`) = WEEK('" + lastdayofweek + "')" 
							;
					System.out.println(sqlquerystat);
					rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
					while(rsfx.next()) {
						Integer recnum = rsfx.getInt("RESULT");
						if(recnum >0)
							cjlrecords.setFengXiJIeGuo(true);
					}
				}
			}
			 
			 stock.addChenJiaoErZhanBiInGivenPeriod (tmpcjelist,addposition); 
			
		}catch(java.lang.NullPointerException e){ 
			e.printStackTrace();
//			System.out.println( "���ݿ�����ΪNULL!");
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			try {
				if(rsfg != null)
					rsfg.close();
				rsfx.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rsfg = null;
			rsfx = null;
		}
		

		return stock;

	}
	/*
	 * ��ȡĳ���ڵ���ָ���ܵ����й�ע�����Ƚ��
	 */
	public ArrayList<JiaRuJiHua> getZdgzFxjgForANodeOfGivenPeriod (String nodecode, LocalDate givenwk)
	{
		String sqlquerystat = "SELECT *  FROM ������¼�ص��ע \r\n" + 
				"WHERE ��Ʊ����='" + nodecode + "'" + "\r\n" + 
				"AND WEEK(������¼�ص��ע.`����`) = WEEK('" + givenwk + "')" 
				;
		System.out.println(sqlquerystat);
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
	 * ĳ����Ʊ��ĳ������ĳ��ʱ����ڵİ���ռ�ȣ���bar chart��
	 */
//	public CachedRowSetImpl getGeGuZhanBi(String tdxbk, String stockcode, Date selecteddatestart,Date selecteddateend) 
//	{
//		HashMap<String, String> actiontables = this.getActionRelatedTables(tdxbk,stockcode);
//		String stockvsbktable = actiontables.get("��Ʊ����Ӧ��");
//		String bkcjltable = actiontables.get("���ÿ�ս�������");
//		String gegucjltable = actiontables.get("��Ʊÿ�ս�������");
//		
//		String formatedstartdate = sysconfig.formatDate(selecteddatestart);
//		String formatedenddate  = sysconfig.formatDate(selecteddateend);
//		
//		String sqlquerystat = "SELECT Year(BKJYE.��������) AS CALYEAR , WEEK(BKJYE.��������) AS CALWEEK, BKJYE.`����` AS BKCODE , DATE(BKJYE.�������� + INTERVAL (7 - DAYOFWEEK(BKJYE.��������)) DAY) EndOfWeekDate,\r\n" + 
//				"       SUM(BKJYE.�ɽ���) AS ��Ʊ�ܽ��׶�, SUM(ZSJYE.�ɽ���) AS ����ܽ��׶�, SUM(BKJYE.`�ɽ���`)/SUM(ZSJYE.`�ɽ���`) ռ��\r\n" + 
//				" FROM  " + gegucjltable  + " BKJYE\r\n" + 
//				" INNER JOIN " +  bkcjltable   + " ZSJYE \r\n" + 
//				"ON ZSJYE.���� = '" + tdxbk + "'  \r\n" + 
//				"       AND BKJYE.`��������` = ZSJYE.`��������`\r\n" + 
//				"       AND  BKJYE.`����` = '" + stockcode  + "'\r\n" + 
//				"        AND Year(BKJYE.`��������`) = YEAR(NOW())\r\n" + 
//				"        AND WEEK(BKJYE.`��������`) >=WEEK('" + formatedstartdate + "') \r\n" + 
//				"		  AND WEEK(BKJYE.`��������`) <=WEEK('" + formatedenddate + "')\r\n" + 
//				" GROUP BY Year(BKJYE.`��������`),WEEK(BKJYE.`��������`),BKJYE.`����`;"
//				;
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rs = null;
//		try {
//			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//   		 	
//		}catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    } catch(Exception e){
//	    	e.printStackTrace();
//	    }  finally {
//	    	
//	    }
//		return rs;
//	}

	/*
	 * ĳ����Ʊ��ĳ������ڵ�ռ��������,
	 */
//	public void getStockZhanBiGrowthRateInBanKuai (String stockcode, String bkcode,Date selecteddatestart,Date selecteddateend)
//	{
//		HashMap<String, String> actiontables = this.getActionRelatedTables(bkcode,stockcode);
//		String stockvsbktable = actiontables.get("��Ʊ����Ӧ��");
//		String bkcjltable = actiontables.get("���ÿ�ս�������");
//		String gegucjltable = actiontables.get("��Ʊÿ�ս�������");
//		
//		String formatedstartdate = sysconfig.formatDate(selecteddatestart);
//		String formatedenddate  = sysconfig.formatDate(selecteddateend);
//		
//		String sqlquerystat = "		select A.`������`, A.`��Ʊ����`,\r\n" + 
//				"	       A.stock_amount,  B.category_amount,\r\n" + 
//				"	       A.stock_amount / B.category_amount ratio\r\n" + 
//				"	  from (select ��Ʊͨ���ŷ�����Ӧ��.`��Ʊ����`, ��Ʊͨ���ŷ�����Ӧ��.`������`, sum(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`�ɽ���`) stock_amount\r\n" + 
//				"	          from ��Ʊͨ���ŷ�����Ӧ��, ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ\r\n" + 
//				"	         where ��Ʊͨ���ŷ�����Ӧ��.`��Ʊ����`   = ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��Ʊ����`\r\n" + 
//				"	           and ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` >= ��Ʊͨ���ŷ�����Ӧ��.`����ʱ��`\r\n" + 
//				"	           and ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` <  ifnull(��Ʊͨ���ŷ�����Ӧ��.`�Ƴ�ʱ��`, '2099-12-31')\r\n" + 
//				"	           and (ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` between '2017-09-04' and '2017-09-08')\r\n" + 
//				"	         group by ��Ʊͨ���ŷ�����Ӧ��.`��Ʊ����`, ��Ʊͨ���ŷ�����Ӧ��.`������`\r\n" + 
//				"	         order by ��Ʊͨ���ŷ�����Ӧ��.`��Ʊ����`, ��Ʊͨ���ŷ�����Ӧ��.`������`) A,\r\n" + 
//				"	         \r\n" + 
//				"	       (select ͨ���Ű��ÿ�ս�����Ϣ.`������` , sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) category_amount\r\n" + 
//				"	          from ͨ���Ű��ÿ�ս�����Ϣ\r\n" + 
//				"	         where ͨ���Ű��ÿ�ս�����Ϣ.`��������` >= '2017-09-04'\r\n" + 
//				"	           and ͨ���Ű��ÿ�ս�����Ϣ.`��������` <= '2017-09-08'\r\n" + 
//				"	         group by ͨ���Ű��ÿ�ս�����Ϣ.`������`\r\n" + 
//				"	         order by ͨ���Ű��ÿ�ս�����Ϣ.`������`) B\r\n" + 
//				"	 where A.`������`   = B.`������`\r\n" + 
//				"	  order by A.`������`,  A.`��Ʊ����`"
//				;
//		System.out.println(sqlquerystat);
//		HashMap<String,Double> bkfxzhanbimap = new HashMap<String,Double> ();
//		CachedRowSetImpl rs = null;
//		try {
//			
//			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//   		 	while(rs.next()) {
////   		 		String bkname = rs.getString("�������");
//   		 		String bkcode = rs.getString("BKCODE");
//   		 		Double bkzhanbi = rs.getDouble("ռ��");
//   		 		
//   		 		bkfxzhanbimap.put(bkcode, bkzhanbi);
//   		 	}
//		}catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    } catch (SQLException e) {
//	    	e.printStackTrace();
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    }  finally {
//	    	if(rs != null)
//				try {
//					rs.close();
//					rs = null;
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//	    }
//		
//		sqlquerystat = "		select A.`������`, A.`��Ʊ����`,\r\n" + 
//				"	       A.stock_amount,  B.category_amount,\r\n" + 
//				"	       A.stock_amount / B.category_amount ratio\r\n" + 
//				"	  from (select ��Ʊͨ���ŷ�����Ӧ��.`��Ʊ����`, ��Ʊͨ���ŷ�����Ӧ��.`������`, sum(ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`�ɽ���`) stock_amount\r\n" + 
//				"	          from ��Ʊͨ���ŷ�����Ӧ��, ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ\r\n" + 
//				"	         where ��Ʊͨ���ŷ�����Ӧ��.`��Ʊ����`   = ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��Ʊ����`\r\n" + 
//				"	           and ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` >= ��Ʊͨ���ŷ�����Ӧ��.`����ʱ��`\r\n" + 
//				"	           and ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` <  ifnull(��Ʊͨ���ŷ�����Ӧ��.`�Ƴ�ʱ��`, '2099-12-31')\r\n" + 
//				"	           and (ͨ�����Ͻ�����Ʊÿ�ս�����Ϣ.`��������` between '2017-09-04' and '2017-09-08')\r\n" + 
//				"	         group by ��Ʊͨ���ŷ�����Ӧ��.`��Ʊ����`, ��Ʊͨ���ŷ�����Ӧ��.`������`\r\n" + 
//				"	         order by ��Ʊͨ���ŷ�����Ӧ��.`��Ʊ����`, ��Ʊͨ���ŷ�����Ӧ��.`������`) A,\r\n" + 
//				"	         \r\n" + 
//				"	       (select ͨ���Ű��ÿ�ս�����Ϣ.`������` , sum(ͨ���Ű��ÿ�ս�����Ϣ.`�ɽ���`) category_amount\r\n" + 
//				"	          from ͨ���Ű��ÿ�ս�����Ϣ\r\n" + 
//				"	         where ͨ���Ű��ÿ�ս�����Ϣ.`��������` >= '2017-09-04'\r\n" + 
//				"	           and ͨ���Ű��ÿ�ս�����Ϣ.`��������` <= '2017-09-08'\r\n" + 
//				"	         group by ͨ���Ű��ÿ�ս�����Ϣ.`������`\r\n" + 
//				"	         order by ͨ���Ű��ÿ�ս�����Ϣ.`������`) B\r\n" + 
//				"	 where A.`������`   = B.`������`\r\n" + 
//				"	  order by A.`������`,  A.`��Ʊ����`"
//				;
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rs2 = null;
//		try {
//			rs2 = connectdb.sqlQueryStatExecute(sqlquerystat);
//   		 	while(rs.next()) {
////   		 		String bkname = rs.getString("�������");
//   		 		String bkcode = rs.getString("BKCODE");
//   		 		Double bkzhanbi = rs.getDouble("ռ��");
//   		 		
//   		 	try{
//   		 		Double bkzhanbi = bkfxzhanbimap.get(bkcode);
//   		 		Double bkzbgrowthrate = (bkbenzhouzb - bkzhanbi)/bkzhanbi;
//   		 		bkfxzhanbimap.put(bkcode, bkzbgrowthrate); 
//		 		} catch(java.lang.NullPointerException e){
//		 			//û�У�˵�����°�飬��ô����
//		 			bkfxzhanbimap.put(bkcode, 10000.0); //��ʱ����������Ϊ10000
//		 		}
//   		 
//   		 	}
//		}catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    } catch (SQLException e) {
//	    	e.printStackTrace();
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    }  finally {
//	    	if(rs2 != null)
//				try {
//					rs2.close();
//					rs2 = null;
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//	    }
//		
//		
//		
//		
//	}
	
	/*
	 * ĳһ������趨ʱ�����ںʹ�����ȵ�ÿ��ռ��
	 */
//	public  CachedRowSetImpl getBanKuaiZhanBi (String bkcode,Date selecteddatestart,Date selecteddateend)
//	{
//		HashMap<String, String> actiontables = this.getActionRelatedTables(bkcode,null);
//		String bkcjltable = actiontables.get("���ÿ�ս�������");
//		
//		String formatedstartdate = sysconfig.formatDate(selecteddatestart);
//		String formatedenddate  = sysconfig.formatDate(selecteddateend);
//				
//		if(!bkcjltable.equals("ͨ���Ž�����ָ��ÿ�ս�����Ϣ")) { 
//		
//			String sqlquerystat = "SELECT Year(BKJYE.��������) AS CALYEAR , WEEK(BKJYE.��������) AS CALWEEK, BKJYE.`����` AS BKCODE , BKJYE.��������, DATE(BKJYE.�������� + INTERVAL (7 - DAYOFWEEK(BKJYE.��������)) DAY) EndOfWeekDate,"
//					+ "	       SUM(BKJYE.�ɽ���)/2 AS ����ܽ��׶�, SUM(ZSJYE.�ɽ���) AS �����ܽ��׶�, SUM(BKJYE.`�ɽ���`)/(2*SUM(ZSJYE.`�ɽ���`)) AS ռ��"
//					+ "	FROM  ͨ���Ű��ÿ�ս�����Ϣ BKJYE"
//					+ "	INNER JOIN ͨ���Ž�����ָ��ÿ�ս�����Ϣ  ZSJYE"
//					+ "	ON ZSJYE.���� in ('999999','399001' )"
//					+ "	       AND BKJYE.`��������` = ZSJYE.`��������`"
//					+ "	       AND Year(BKJYE.`��������`) = YEAR(NOW())"
//					+ "  AND WEEK(BKJYE.`��������`) >=WEEK('" + formatedstartdate + "') AND WEEK(BKJYE.`��������`) <=WEEK('" + formatedenddate + "')"
//					+ "	       AND BKJYE.`����` = '" + bkcode + "'"
//					+ "			GROUP BY Year(BKJYE.`��������`),WEEK(BKJYE.`��������`),BKJYE.`����`"
//					;
//			System.out.println(sqlquerystat);
//			CachedRowSetImpl rs = null;
//			try {
//				rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//	   		 	
//			}catch(java.lang.NullPointerException e){ 
//		    	e.printStackTrace();
//		    } catch(Exception e){
//		    	e.printStackTrace();
//		    }  finally {
//		    	
//		    }
//			return rs;
//		} else { //�����ָ�����,�������ͬ���ڵĲ�ѯ
//			String sqlquerystat = "SELECT Year(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������) AS CALYEAR , WEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������) AS CALWEEK, ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`����` AS BKCODE ,"
//					+ "	sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�ɽ���) AS ����ܽ��׶�,	sum(sum_dapan) AS �����ܽ��׶�, sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�ɽ���) / sum(sum_dapan) AS ռ��"
//					+ " FROM ͨ���Ž�����ָ��ÿ�ս�����Ϣ , ("
//					+ "    SELECT ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�������� dapanjyrq,  SUM(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�ɽ���) sum_dapan"
//					+ "    FROM ͨ���Ž�����ָ��ÿ�ս�����Ϣ"
//					+ "    where  ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`����` IN  ('999999','399001' )"
//					+ "    group by dapanjyrq"
//					+ ") sq"
//					+ " where  ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�������� = dapanjyrq AND ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`����` = '"  + bkcode + "'" + " AND Year(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������)  = YEAR(now()) "
//					+ " GROUP BY Year(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������)  , WEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������), ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`����`"
//					; 
//			System.out.println(sqlquerystat);
//			CachedRowSetImpl rs = null;
//			try {
//				rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//	   		 	
//			}catch(java.lang.NullPointerException e){ 
//		    	e.printStackTrace();
//		    } catch(Exception e){
//		    	e.printStackTrace();
//		    }  finally {
//		    	
//		    }
//			return rs;
//		}
//	}
	/*
	 * ĳ������е�ǰ���ɵ�ռ��������
	 */
//	public HashMap<String,Double> getAllGeGuOfChenJiaoErWeekZhanBiInBanKuaiOrderByZhanBiGrowthRate(String bkcode, Date selecteddatestart,Date selecteddateend)
//	{
//		String formatedstartdate = sysconfig.formatDate(selecteddatestart);
//		String formatedenddate  = sysconfig.formatDate(selecteddateend);
//		
//		HashMap<String,Double> bkfxzhanbimap = new HashMap<String,Double> ();
//		
//		
//	}
	/*
	 * ����ָ�����ɽ���ռ�������ʣ���ռ������������
	 */
//	public HashMap<String,Double> getAllZhiShuBanKuaiOfChenJiaoErWeekZhanBiOrderByZhanBiGrowthRate(Date selecteddatestart,Date selecteddateend)
//	{
//		String formatedstartdate = sysconfig.formatDate(selecteddatestart);
//		String formatedenddate  = sysconfig.formatDate(selecteddateend);
//		
//		HashMap<String,Double> bkfxzhanbimap = new HashMap<String,Double> ();
//		//���ܵĽ���ռ��
//		String sqlquerystat = "SELECT  CALYEAR ,  CALWEEK, EndOfWeekDate, BKCODE ,  ����ܽ��׶�,  �����ܽ��׶�,  ռ��, BKLB.`�������`\r\n" + 
//				"FROM  (      \r\n" + 
//				"SELECT Year(BKJYE.��������) AS CALYEAR , WEEK(BKJYE.��������) AS CALWEEK, BKJYE.`����` AS BKCODE , DATE(BKJYE.�������� + INTERVAL (7 - DAYOFWEEK(BKJYE.��������)) DAY) EndOfWeekDate,\r\n" + 
//				"       SUM(BKJYE.�ɽ���)/2 AS ����ܽ��׶�, SUM(ZSJYE.�ɽ���) AS �����ܽ��׶�, SUM(BKJYE.`�ɽ���`)/(2*SUM(ZSJYE.`�ɽ���`)) AS ռ��\r\n" + 
//				"       \r\n" + 
//				"FROM  ͨ���Ž�����ָ��ÿ�ս�����Ϣ BKJYE\r\n" + 
//				"INNER JOIN ͨ���Ž�����ָ��ÿ�ս�����Ϣ  ZSJYE  \r\n" + 
//				"ON ZSJYE.���� in ('999999','399001' ) \r\n" + 
//				"       AND BKJYE.`��������` = ZSJYE.`��������`\r\n" + 
//				"       AND Year(BKJYE.`��������`) = YEAR(NOW())\r\n" + 
//				"    AND WEEK(BKJYE.`��������`) >= (WEEK('" + formatedstartdate + "')-1) AND WEEK(BKJYE.`��������`) <= (WEEK('" + formatedenddate + "')-1)" +
//				"		GROUP BY Year(BKJYE.`��������`),WEEK(BKJYE.`��������`),BKJYE.`����`\r\n" + 
//				"				) X\r\n" + 
//				"				,ͨ���Ž�����ָ���б� BKLB\r\n" + 
//				"				WHERE BKCODE = BKLB.`���ID`"
//				;
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rs = null;
//		try {
//			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//   		 	while(rs.next()) {
////   		 		String bkname = rs.getString("�������");
//   		 		String bkcode = rs.getString("BKCODE");
//   		 		Double bkzhanbi = rs.getDouble("ռ��");
//   		 		
//   		 		bkfxzhanbimap.put(bkcode, bkzhanbi);
//   		 	}
//		}catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    } catch (SQLException e) {
//	    	e.printStackTrace();
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    }  finally {
//	    	if(rs != null)
//				try {
//					rs.close();
//					rs = null;
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//	    }
//		
//		sqlquerystat = "SELECT  CALYEAR ,  CALWEEK, EndOfWeekDate, BKCODE ,  ����ܽ��׶�,  �����ܽ��׶�,  ռ��, BKLB.`�������`\r\n" + 
//				"FROM  (      \r\n" + 
//				"SELECT Year(BKJYE.��������) AS CALYEAR , WEEK(BKJYE.��������) AS CALWEEK, BKJYE.`����` AS BKCODE , DATE(BKJYE.�������� + INTERVAL (7 - DAYOFWEEK(BKJYE.��������)) DAY) EndOfWeekDate,\r\n" + 
//				"       SUM(BKJYE.�ɽ���)/2 AS ����ܽ��׶�, SUM(ZSJYE.�ɽ���) AS �����ܽ��׶�, SUM(BKJYE.`�ɽ���`)/(2*SUM(ZSJYE.`�ɽ���`)) AS ռ��\r\n" + 
//				"       \r\n" + 
//				"FROM  ͨ���Ž�����ָ��ÿ�ս�����Ϣ BKJYE\r\n" + 
//				"INNER JOIN ͨ���Ž�����ָ��ÿ�ս�����Ϣ  ZSJYE  \r\n" + 
//				"ON ZSJYE.���� in ('999999','399001' ) \r\n" + 
//				"       AND BKJYE.`��������` = ZSJYE.`��������`\r\n" + 
//				"       AND Year(BKJYE.`��������`) = YEAR(NOW())\r\n" + 
//				"    AND WEEK(BKJYE.`��������`) >=WEEK('" + formatedstartdate + "') AND WEEK(BKJYE.`��������`) <=WEEK('" + formatedenddate + "') " +
//				"		GROUP BY Year(BKJYE.`��������`),WEEK(BKJYE.`��������`),BKJYE.`����`\r\n" + 
//				"				) X\r\n" + 
//				"				,ͨ���Ž�����ָ���б� BKLB\r\n" + 
//				"				WHERE BKCODE = BKLB.`���ID`"
//				;
//		
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rssz = null; 
//		try {  
//		 	 rssz = connectdb.sqlQueryStatExecute(sqlquerystat);
//		 	 while(rssz.next()) {
//		 		String bkname = rssz.getString("�������");
//   		 		String bkcode = rssz.getString("BKCODE");
//   		 		Double bkbenzhouzb  = rssz.getDouble("ռ��");
//   		 		
//   		 		try{
//	   		 		Double bkzhanbi = bkfxzhanbimap.get(bkcode);
//	   		 		Double bkzbgrowthrate = (bkbenzhouzb - bkzhanbi)/bkzhanbi;
//	   		 		bkfxzhanbimap.put(bkcode, bkzbgrowthrate); 
//   		 		} catch(java.lang.NullPointerException e){
//   		 			//û�У�˵�����°�飬��ô����
//   		 			bkfxzhanbimap.put(bkcode, 10000.0); //��ʱ����������Ϊ10000
//   		 		}
//		 		 
//		 	 }
//		} catch(java.lang.NullPointerException e){ 
//		 	e.printStackTrace();
//		} catch (SQLException e) {
//		 	System.out.println(sqlquerystat);
//		 	e.printStackTrace();
//		} catch(Exception e){
//		 	e.printStackTrace();
//		} finally {
//		 	if(rssz != null)
//					try {
//						rssz.close();
//						rssz = null;
//					} catch (SQLException e) {
//						e.printStackTrace();
//					}
//		 }
//		
//		return bkfxzhanbimap;
//	}
//	/*
//	 * ����880���ɽ���ռ�ȣ���ռ������������
//	 */
//	public HashMap<String,Double> getAllTdxBanKuaiOfChenJiaoErWeekZhanBiOrderByZhanBiGrowthRate(Date selecteddatestart,Date selecteddateend)
//	{
//		String formatedstartdate = sysconfig.formatDate(selecteddatestart);
//		String formatedenddate  = sysconfig.formatDate(selecteddateend);
//		
//		HashMap<String,Double> bkfxzhanbimap = new HashMap<String,Double> ();
//		//���ܵĽ���ռ��
//		String sqlquerystat = "SELECT  CALYEAR ,  CALWEEK,  EndOfWeekDate, BKCODE ,  ����ܽ��׶�,  �����ܽ��׶�,  ռ��, BKLB.`�������`"
//				+ "	FROM  ("
//				+ "	SELECT Year(BKJYE.��������) AS CALYEAR , WEEK(BKJYE.��������) AS CALWEEK, BKJYE.`����` AS BKCODE , DATE(BKJYE.�������� + INTERVAL (7 - DAYOFWEEK(BKJYE.��������)) DAY) EndOfWeekDate, "
//				+ " SUM(BKJYE.�ɽ���)/2 AS ����ܽ��׶�, SUM(ZSJYE.�ɽ���) AS �����ܽ��׶�, SUM(BKJYE.`�ɽ���`)/(2*SUM(ZSJYE.`�ɽ���`)) AS ռ��"
//				+ "	FROM  ͨ���Ű��ÿ�ս�����Ϣ BKJYE"
//				+ "	INNER JOIN ͨ���Ž�����ָ��ÿ�ս�����Ϣ  ZSJYE"
//				+ "	ON ZSJYE.���� in ('999999','399001' )"
//				+ "    AND BKJYE.`��������` = ZSJYE.`��������`"
//				+ "    AND Year(BKJYE.`��������`) = YEAR(NOW())"
//				+ "    AND WEEK(BKJYE.`��������`) >= (WEEK('" + formatedstartdate + "')-1) AND WEEK(BKJYE.`��������`) <= (WEEK('" + formatedenddate + "')-1)"
//				+ "	GROUP BY Year(BKJYE.`��������`),WEEK(BKJYE.`��������`),BKJYE.`����`"
//				+ "	) X"
//				+ "	,ͨ���Ű���б� BKLB"
//				+ "	WHERE BKCODE = BKLB.`���ID`"
//				;
//				
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rs = null;
//		try {
//			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//   		 	while(rs.next()) {
//   		 		String bkname = rs.getString("�������");
//   		 		String bkcode = rs.getString("BKCODE");
//   		 		Double bkzhanbi = rs.getDouble("ռ��");
//   		 		
//   		 		bkfxzhanbimap.put(bkcode, bkzhanbi);
//   		 	}
//		}catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    } catch (SQLException e) {
//	    	e.printStackTrace();
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    }  finally {
//	    	if(rs != null)
//				try {
//					rs.close();
//					rs = null;
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//	    }
//		
//		//���ܵĽ���ռ��
//		sqlquerystat = "SELECT  CALYEAR ,  CALWEEK,  EndOfWeekDate, BKCODE ,  ����ܽ��׶�,  �����ܽ��׶�,  ռ��, BKLB.`�������`"
//				+ "	FROM  ("
//				+ "	SELECT Year(BKJYE.��������) AS CALYEAR , WEEK(BKJYE.��������) AS CALWEEK, BKJYE.`����` AS BKCODE , DATE(BKJYE.�������� + INTERVAL (7 - DAYOFWEEK(BKJYE.��������)) DAY) EndOfWeekDate, "
//				+ " SUM(BKJYE.�ɽ���)/2 AS ����ܽ��׶�, SUM(ZSJYE.�ɽ���) AS �����ܽ��׶�, SUM(BKJYE.`�ɽ���`)/(2*SUM(ZSJYE.`�ɽ���`)) AS ռ��"
//				+ "	FROM  ͨ���Ű��ÿ�ս�����Ϣ BKJYE"
//				+ "	INNER JOIN ͨ���Ž�����ָ��ÿ�ս�����Ϣ  ZSJYE"
//				+ "	ON ZSJYE.���� in ('999999','399001' )"
//				+ "    AND BKJYE.`��������` = ZSJYE.`��������`"
//				+ "    AND Year(BKJYE.`��������`) = YEAR(NOW())"
//				+ "    AND WEEK(BKJYE.`��������`) >= (WEEK('" + formatedstartdate + "')) AND WEEK(BKJYE.`��������`) <= (WEEK('" + formatedenddate + "'))"
//				+ "	GROUP BY Year(BKJYE.`��������`),WEEK(BKJYE.`��������`),BKJYE.`����`"
//				+ "	) X"
//				+ "	,ͨ���Ű���б� BKLB"
//				+ "	WHERE BKCODE = BKLB.`���ID`"
//				;
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rssz = null; 
//		try {  
//		 	 rssz = connectdb.sqlQueryStatExecute(sqlquerystat);
//		 	 while(rssz.next()) {
//		 		String bkname = rssz.getString("�������");
//   		 		String bkcode = rssz.getString("BKCODE");
//   		 		Double bkbenzhouzb  = rssz.getDouble("ռ��");
//   		 		
//   		 		try{
//	   		 		Double bklastzhanbi = bkfxzhanbimap.get(bkcode);
//	   		 		Double bkzbgrowthrate = (bkbenzhouzb - bklastzhanbi)/bklastzhanbi;
//	   		 		bkfxzhanbimap.put(bkcode, bkzbgrowthrate); 
//   		 		} catch(java.lang.NullPointerException e){
//   		 			//û�У�˵�����°�飬��ô����
//   		 			bkfxzhanbimap.put(bkcode, 10000.0); //��ʱ����������Ϊ10000
//   		 		}
//		 		 
//		 	 }
//		} catch(java.lang.NullPointerException e){ 
//		 	e.printStackTrace();
//		} catch (SQLException e) {
//		 	System.out.println(sqlquerystat);
//		 	e.printStackTrace();
//		} catch(Exception e){
//		 	e.printStackTrace();
//		} finally {
//		 	if(rssz != null)
//					try {
//						rssz.close();
//						rssz = null;
//					} catch (SQLException e) {
//						e.printStackTrace();
//					}
//		 }
//		
//		return bkfxzhanbimap;
//	}
	/*
	 * ����趨�����ܵ������Ƿ�������ֻ�Ե�ǰ�߽������������Բ��ԣ���ȥ��Ĭ������������
	 * -1 ��������  0����ѡ��û������  >0�����ݵ���һ��
	 */
//	private int precheckBeforeBanKuaiFengXi(Date bkfxdate)
//	{
//		if(CommonUtility.getWeekNumber(bkfxdate) == CommonUtility.getWeekNumber(new Date()) ) {
//			
//			String sqlquerystat = " SELECT MAX(��������) FROM ͨ���Ž�����ָ��ÿ�ս�����Ϣ" ;
//			CachedRowSetImpl rs = null; 
//			try {  
//			 	 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//			 	 
//			 	 java.sql.Timestamp tmplastmdftime = null;
//			 	 while(rs.next())
//			 		  tmplastmdftime = rs.getTimestamp("MAX(��������)");
//			 	 System.out.println(tmplastmdftime);
//			 	 
//			 	 if(CommonUtility.getWeekNumber(bkfxdate) == CommonUtility.getWeekNumber(tmplastmdftime) ){
//			 		 
//			 		Calendar cal = Calendar.getInstance();
//				 	 cal.setTime(tmplastmdftime);
//				 	 int whichdays = cal.get(Calendar.DAY_OF_WEEK);
//				 	 if(whichdays == 6)
//				 		 return -1;
//				 	 else 
//				 		 return whichdays;
//			 	 } else 
//			 		 return 0;
//			 }catch(java.lang.NullPointerException e){ 
//			 	e.printStackTrace();
//			 } catch (SQLException e) {
//			 	System.out.println(sqlquerystat);
//			 	e.printStackTrace();
//			 }catch(Exception e){
//			 	e.printStackTrace();
//			 }  finally {
//			 	if(rs != null)
//						try {
//							rs.close();
//							rs = null;
//						} catch (SQLException e) {
//							e.printStackTrace();
//						}
//			 }
//		} else 
//			return -1;
//		return -1;
//	}
	
	
	
	/*
	 * ���ɽ���ռ�ȣ���ռ������
	 */
//	public DefaultCategoryDataset getBanKuaiFengXiBarInfoOfChenJiaoErZhanBiOrderByZhanBi(LocalDate bkfxdate) 
//	{
//		HashMap<String, BanKuai> curdaleidetaillistmap = this.getTDXBanKuaiList();
//		Set<String> curdaleidetaillistset = curdaleidetaillistmap.keySet();
//		String fengxidate = formateDateForDiffDatabase("mysql",CommonUtility.formatDateYYYY_MM_DD_HHMMSS(bkfxdate).substring(0,10)); 
//		
//		String sqlquerystat = "	SELECT  CALYEAR ,  CALWEEK,  BKCODE ,  ����ܽ��׶�,  �����ܽ��׶�,  ռ��, BKLB.`�������` "
//				+ "FROM  ("
//				+ "	SELECT Year(BKJYE.��������) AS CALYEAR , WEEK(BKJYE.��������) AS CALWEEK, BKJYE.`����` AS BKCODE,"
//				+ "       SUM(BKJYE.�ɽ���)/2 AS ����ܽ��׶�, SUM(ZSJYE.�ɽ���) AS �����ܽ��׶�, SUM(BKJYE.`�ɽ���`)/(2*SUM(ZSJYE.`�ɽ���`)) AS ռ��"
//				+ "	FROM  ͨ���Ű��ÿ�ս�����Ϣ BKJYE"
//				+ "	INNER JOIN ͨ���Ž�����ָ��ÿ�ս�����Ϣ  ZSJYE"
//				+ "	ON ZSJYE.���� in ('999999','399001' )"
//				+ "    AND BKJYE.`��������` = ZSJYE.`��������`"
//				+ "    AND Year(BKJYE.`��������`) = YEAR(" +  fengxidate + " )"
//				+ "    AND BKJYE.`��������` BETWEEN DATE_SUB( " +  fengxidate + " ,  INTERVAL( WEEKDAY( " +  fengxidate + " ) ) DAY ) AND " +  fengxidate  
//				+ " GROUP BY Year(BKJYE.`��������`),WEEK(BKJYE.`��������`),BKJYE.`����`"
//				+ ") X"
//				+ ",ͨ���Ű���б� BKLB"
//				+ "	WHERE BKCODE = BKLB.`���ID`"
//				+ "	ORDER BY ռ�� desc"
//				;
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rs = null;
//		DefaultCategoryDataset barchart_dataset = new DefaultCategoryDataset();
//		try {
//			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//   		 	while(rs.next()) {
//   		 		String bkname = rs.getString("�������");
//   		 		String bkcode = rs.getString("BKCODE");
//   		 		Double bkzhanbi = rs.getDouble("ռ��");
//   		 		
//   		 		barchart_dataset.addValue(bkzhanbi, "���ɽ���ռ��", bkname);
//   		 	}
//		}catch(java.lang.NullPointerException e){ 
//	    	e.printStackTrace();
//	    } catch (SQLException e) {
//	    	e.printStackTrace();
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    }  finally {
//	    	if(rs != null)
//				try {
//					rs.close();
//					rs = null;
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//	    }
//		
//		return barchart_dataset;
//	}
	/*
	 * ͬ�����ɳɽ�����Ϣ������Ϊ������ SH/SZ
	 */
	public File refreshTDXGeGuVolAmoToDb(String jiaoyisuo) 
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "ͬ�����ڸ��ɳɽ�����Ϣ.tmp");
		
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
			System.out.println(sqlquerystat);
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
			//String bkfilename = "SH" + filenamerule.replaceAll("YYXXXXXX", tmpbkcode);
			String bkfilename = null;
			if(jiaoyisuo.toLowerCase().equals("sz") )
				bkfilename = (filenamerule.replaceAll("YY","SZ")).replaceAll("XXXXXX", tmpbkcode);
			else if(jiaoyisuo.toLowerCase().equals("sh") ) 
				bkfilename = (filenamerule.replaceAll("YY","SH")).replaceAll("XXXXXX", tmpbkcode);
			File tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {  
				System.out.println("��ȡ" + bkfilename + "��������");
			    continue; 
			} 

				CachedRowSetImpl  rs = null;
				LocalDate ldlastestdbrecordsdate = null;
				try { 				
					String sqlquerystat = "SELECT  MAX(��������) 	MOST_RECENT_TIME"
							+ " FROM "+ optTable +  " WHERE  ���� = " 
   							+ "'"  + tmpbkcode + "'" 
   							;
					System.out.println(sqlquerystat);
   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   			    	while(rs.next()) {
//   			    		System.out.println(rs.getMetaData().getColumnCount());
   			    		 Date lastestdbrecordsdate = rs.getDate("MOST_RECENT_TIME"); //mOST_RECENT_TIME 
   			    		 Instant instant = Instant.ofEpochMilli(lastestdbrecordsdate.getTime());
   			    		ldlastestdbrecordsdate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
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
				
				setVolAmoRecordsFromFileToDatabase(tmpbkcode,tmpbkfile,ldlastestdbrecordsdate,optTable,dateRule,tmprecordfile);

		}
		
		return tmprecordfile;
	}
	/*
	 * �޸İ����ĳ�����ɵ�Ȩ��
	 */
	public void setStockWeightInBanKuai(String bkcode, String bkname, String stockcode, int weight) 
	{
		HashMap<String, String> actiontables = this.getActionRelatedTables(bkcode, stockcode);
		String bktypetable = actiontables.get("��Ʊ����Ӧ��");
		String bkorzsvoltable = actiontables.get("���ÿ�ս�������");
		String bknametable = actiontables.get("���ָ�����Ʊ�");
		String sqlupdatestat = "update " + bktypetable  + " set ��ƱȨ�� = " +  weight  + 
				" where ������ = '" + bkcode + "' AND ��Ʊ���� = '" + stockcode + "'" 
				;
		System.out.println(sqlupdatestat);
		connectdb.sqlUpdateStatExecute(sqlupdatestat);
		
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
//				System.out.println(bankuaichanyelianxml + "�����ڣ��Ѿ�����");
//			} catch (IOException e) {
//				System.out.println(bankuaichanyelianxml + "�����ڣ�����ʧ�ܣ�");
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
				//System.out.println(sqlinsertstat);
				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
				//tfldresult.append("���룺" + str.trim() + " " + gupiaoheader + "\n");
			}
		    
	        //�Ѿɵ��Զ�����ɾ�������ݿ�
	        SetView<String> differencebankuaiold = Sets.difference(curzdybknames,neededimportzdybknames  );
	        for (String str : differencebankuaiold) {
	        	String sqldeletetstat = "DELETE  FROM ͨ�����Զ������б� "
	        							+ " WHERE �������=" + "'" + str.trim() + "'" 
	        							;
	        	//System.out.println(sqldeletetstat);
	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
	    		//tfldresult.append("ɾ����" + str.trim() + " " + gupiaoheader + "\n");
	    		
	    		sqldeletetstat = "DELETE  FROM ��Ʊͨ�����Զ������Ӧ�� "
						+ " WHERE �Զ�����=" + "'" + str.trim() + "'" 
						;
				//System.out.println(sqldeletetstat);
				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
	        }
	        
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
   				//System.out.println(sqlinsertstat);
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
   	        	//System.out.println(sqldeletetstat);
   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
   	    		//tfldresult.append("���룺" + str.trim() + " " + gupiaoheader + "\n");
   	        }
		}
		
		return 0;
	}
	
	/*
	 * �Զ�������Щ������Ҫ���룬�û�ѡ������Щ�Զ�����
	 */
	public HashMap<String, String> getTDXZiDingYiBanKuaiList ()
	{
		File file = new File(sysconfig.getTDXSysZDYBanKuaiFile () );
		 
		 if(!file.exists() ) {
			 System.out.println("Zdy BanKuai File not exist");
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
           	   System.out.println(zdybankuainame);
           	   
           	   //����Ӧfile
               String bkfilename = sysconfig.toUNIXpath(file.getParent()) + "/" + new String(itemBuf2,49,eachbankuaibytenumber-51).trim() +".blk";
               System.out.println(bkfilename);
               
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
			 stockbasicinfo.setZdgzmrmcykRecords( setZdgzMrmcZdgzYingKuiRecords (rs) );
			 
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
			//System.out.println(sqlquerystat1);
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
	       //System.out.println(sqlquerystat2);
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
	      //System.out.println(sqlquerystat3);
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
		 //System.out.println(sqlquerystat4);
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
	     //System.out.println(sqlquerystat5);
		
	     String sqlquerystat = sqlquerystat1 + sqlquerystat2 + sqlquerystat3 + sqlquerystat4 + sqlquerystat5;
	     //System.out.println(sqlquerystat);
	     
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
			//System.out.println(sqlquerystat1);
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
	       //System.out.println(sqlquerystat2);
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
	      //System.out.println(sqlquerystat3);
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
		 //System.out.println(sqlquerystat4);
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
	     //System.out.println(sqlquerystat5);
		
	     String sqlquerystat = sqlquerystat1 + sqlquerystat2 + sqlquerystat3 + sqlquerystat4 + sqlquerystat5;
	     //System.out.println(sqlquerystat);
	     
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
			String sqlquerystat =null;
			sqlquerystat= "SELECT * FROM ������¼�ص��ע   WHERE ����= date_sub(curdate(),interval 1 day) AND �����Ƴ���־ = '���ռƻ�' " ;
			sqlstatmap.put("mysql", sqlquerystat);
			
			sqlquerystat=  "SELECT * FROM ������¼�ص��ע "
					+ " WHERE ���� <  DATE() AND  ���� > IIF( Weekday( date() ) =  2,date()-3,date()-1)  "
					+ "AND �����Ƴ���־ = '���ռƻ�'"
					;
			sqlstatmap.put("access", sqlquerystat);
			
			CachedRowSetImpl rs = connectdb.sqlQueryStatExecute(sqlstatmap);
			
			String pmdresult = "";
			try  {     
		        rs.last();  
		        int rows = rs.getRow();  
		        rs.first();  
		        int k = 0;  
		        //while(rs.next())
		        for(int j=0;j<rows;j++) { 
		        	pmdresult = pmdresult + rs.getString("��Ʊ����") + "  " + rs.getString("ԭ������") + " ";
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
//				System.out.println(""+e);
//			} 		
//			
//			return newStockSign;
//		}

		public Stock getCheckListsXMLInfo(Stock stockbasicinfo)
		{
			String stockcode = stockbasicinfo.getMyOwnCode();
			String sqlquerystat= "SELECT checklistsitems FROM A��   WHERE ��Ʊ���� =" +"'" + stockcode +"'" ;
			//System.out.println(sqlquerystat);
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
					txtareainputgainiants = "'" + nodeshouldbedisplayed.getGainiantishi().trim() + "'";
			} catch (java.lang.NullPointerException  e) {
			}

			txtfldinputquanshangpj = "'" + nodeshouldbedisplayed.getQuanshangpingji().trim() + "'";
		    
			txtfldinputfumianxx = "'" + nodeshouldbedisplayed.getFumianxiaoxi() + "'";
		    
		    String txtfldinputzhengxiangguan = "'" + nodeshouldbedisplayed.getZhengxiangguan().trim() + "'";
		    String txtfldinputfuxiangguan = "'" + nodeshouldbedisplayed.getFuxiangguan().trim() + "'";
		    
		    String keHuCustom = nodeshouldbedisplayed.getKeHuCustom();
		    String jingZhengDuiShou = nodeshouldbedisplayed.getJingZhengDuiShou();
	    
			 if(nodeshouldbedisplayed.getType() == 6) //�ǹ�Ʊ
			 {
				 HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
				 String sqlupdatestat= "UPDATE A��  SET "
							+ " ��Ʊ����=" + stockname +","
							+ " ����ʱ��=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getGainiantishidate()) ) +","
							+ " ����������=" + txtareainputgainiants +","
							+ " ȯ������ʱ��=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getQuanshangpingjidate() ) ) +","
							+ " ȯ����������=" + txtfldinputquanshangpj +","
							+ " ������Ϣʱ��=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getFumianxiaoxidate()  ) ) +","
							+ " ������Ϣ=" + txtfldinputfumianxx +","
							+ " ����ؼ��ͻ�=" + txtfldinputzhengxiangguan +","
							+ " ����ؼ���������=" + txtfldinputfuxiangguan +","
							+ " �ͻ�=" + "'" + keHuCustom +"'" + ","
							+ " ��������=" + "'" + jingZhengDuiShou + "'"  
							+ " WHERE ��Ʊ����=" + stockcode
							;
				 sqlstatmap.put("mysql", sqlupdatestat);
				 //System.out.println(sqlupdatestat);
				 
				 if( connectdb.sqlUpdateStatExecute(sqlupdatestat) !=0)
					 return true;
				 else return false;
			 }
			 else { //�ǰ��
				 HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
				 String actiontable = null;
				 if(nodeshouldbedisplayed.getMyOwnCode().trim().startsWith("880")) 
					 actiontable = "ͨ���Ű���б�";
				 else
					 actiontable = "ͨ���Ž�����ָ���б�";
				 
				 String sqlinsertstat = "UPDATE " + actiontable + " SET "
						 	+ " �������=" + stockname +","
							+ " ����ʱ��=" + formateDateForDiffDatabase("mysql",CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getGainiantishidate()) ) +","
							+ " ����������=" + txtareainputgainiants +","
							+ " ȯ������ʱ��=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getQuanshangpingjidate() ) ) +","
							+ " ȯ����������=" + txtfldinputquanshangpj +","
							+ " ������Ϣʱ��=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getFumianxiaoxidate()  ) ) +","
							+ " ������Ϣ=" + txtfldinputfumianxx +","
							+ " ����ؼ��ͻ�=" + txtfldinputzhengxiangguan +","
							+ " ����ؼ���������=" + txtfldinputfuxiangguan +","
							+ " �ͻ�=" + "'" + keHuCustom +"'" + ","
							+ " ��������=" + "'" + jingZhengDuiShou + "'"  
							+ " WHERE ���ID=" + stockcode
							;
				 //System.out.println(sqlinsertstat); 
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
		// System.out.println(sqlupdatestat);
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
			//System.out.println(sqlinsertstat);
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
			
			System.out.println(sqlquerystat);
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
			
			stockbasicinfo.setGeGuSuoShuTDXSysBanKuaiList(tmpsysbk);
			
//			sqlquerystat=  "SELECT �Զ����� FROM ��Ʊͨ�����Զ������Ӧ��  WHERE ��Ʊ����= "
//							+ "'" +  stockcode.trim() + "'"
//							;
//			//System.out.println(sqlquerystat);
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
			//System.out.println(sqlquerystat);
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
		 * ��ͨ�����е����Ʊ����������Ϣ
		 */
		public File refreshEverUsedStorkName()
		{
			File tmpreportfolder = Files.createTempDir();
			File tmprecordfile = new File(tmpreportfolder + "ͬ��ͨ��������������.txt");
		
			File file = new File(sysconfig.getTDXStockEverUsedNameFile() );
			if(!file.exists() ) {
				 System.out.println("File not exist");
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
			           	System.out.println(tmplinelist);
			           	
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
//				 System.out.println(sqlupdatestat);
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
			 System.out.println(sqlupdatestat);
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
			System.out.println(sqlquerystat2);
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
			System.out.println(sqlquerystat3);
			String sqlquerystat4=" SELECT rzczjl.��Ʊ����,"
								+ "IF( rzczjl.�������=0.0,'��ת��',IF(rzczjl.�ҵ� = true,IF(rzczjl.����������־, '�ҵ�����', '�ҵ�����'),IF(rzczjl.����������־,'����','����') )  ) AS ����,"
								+ "rzczjl.ԭ������,"
								+ "rzczjl.ID,"
								+ "rzczjl.�����˺�,"
								+ "'������¼��������'"
								+ " FROM ������¼�������� rzczjl"
								+ " WHERE rzczjl.����>=" + "'" + searchdate + "'"
								
								
								;
			System.out.println(sqlquerystat4);
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

	            System.out.println("Read DBF Metadata: " + meta);
	            int recCounter = 0;
	            while ( (rec = reader.read() ) != null) {
	                rec.setStringCharset(stringCharset);
//	                System.out.println("Record is DELETED: " + rec.isDeleted());
//	                System.out.println(rec.getRecordNumber());
//	                System.out.println(rec.toMap());
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
	              System.out.println(sqlinsetstat);
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
	        	System.out.println("����SQL��:" + sqlinsetstat);
				e1.printStackTrace();
			} catch (ParseException e2) {
				System.out.println("����SQL��:" + sqlinsetstat);
				
				e2.printStackTrace();
			} catch (Exception e3) {
				System.out.println("����SQL��:" + sqlinsetstat);
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
//			System.out.println(sqlinsertstat);
//			int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat) ;
//			return autoIncKeyFromApi;
//		}
		

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