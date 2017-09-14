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
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
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

import com.exchangeinfomanager.asinglestockinfo.ASingleStockInfo;
import com.exchangeinfomanager.bankuai.gui.BanKuai;
import com.exchangeinfomanager.bankuaichanyelian.BkChanYeLianTreeNode;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNews;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
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
	
	
	/*
	 * ���� ͬ��ͨ������ص���Ϣ  ����ͨ���Ŷ���İ����Ϣ �����������ҵ�����ָ�� ���
	 */
	public File refreshTDXSystemBanKuai ()
	{
		File tmpreportfolder = Files.createTempDir(); 
		File tmprecordfile = new File(tmpreportfolder + "ͬ��ͨ����ϵͳ��鱨��.txt");

		//����ͨ����ϵͳ���а����Ϣ���ڸ����У����µĴ������ݿ⣬����XML�� �ɵĴ�XML�����ݿ�ɾ��
		this.refreshTDXAllBanKuaiToSystem(tmprecordfile);
		this.refreshTDXZhiShuShangHaiLists (tmprecordfile); //��������ͬ������ָ���б�ָ�����һ��û��̫��仯����������ͬ����ͬ��һ�κ��ע�͵�
		this.refreshTDXZhiShuShenZhenLists (tmprecordfile);
		 
		//ͬ����ذ�������Ϣ
		this.refreshTDXFengGeBanKuaiToGeGu(tmprecordfile);
		this.refreshTDXGaiNianBanKuaiToGeGu(tmprecordfile);
		this.refreshTDXHangYeBanKuaiToGeGu(tmprecordfile);
		this.refreshTDXZhiShuBanKuaiToGeGu(tmprecordfile); //ͨ����ָ���͹�Ʊ��Ӧ��ϵ

		return tmprecordfile;
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
						+ "\"" +  sysconfig.formatDate(new Date())   + "\""  
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
			
			while ( !(line = bufr.readLine() ).equals("######") ) {
				System.out.println(line);
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //T01|��Դ
				System.out.println(tmpbkinfo);
				hydmmap.put(tmpbkinfo.get(0).trim(), tmpbkinfo.get(1).trim());
			}
			
            Files.append("��ʼ����ͨ���Ź�Ʊ��ҵ��Ӧ��Ϣ:" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			fr = new FileReader(sysconfig.getTDXHangYeBanKuaiToGeGuFile());
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
   			    							+ "'"  + stockcode + "'";
   			    	System.out.println(sqlquerystat);
   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   			    	
   			        rs.last();  
   			        int rows = rs.getRow();  
   			        rs.first();  
   			        if(rows == 0) { //�ù�Ʊ�������������ݿ��У�Ҫ���
   			        	String sqlinsertstat = "INSERT INTO  ��Ʊͨ������ҵ����Ӧ��(��Ʊ����,��ҵ���,��ӦTDXSWID) values ("
		   						+ "'" + stockcode.trim() + "'" + ","
		   						+ "'" + stockbkname.trim() + "'"  + ","
		   						+ "'" + stockbkcode.trim() + "'"
		   						+ ")"
		   						;
		   				System.out.println(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   	            Files.append("���룺" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   				
		   				allupdatednum++;
   			        } else { //����
   			        	String stockbkanameindb = rs.getString("��ӦTDXSWID");
   			        	if( !stockbkanameindb.equals(stockbkcode) ) { //��һ���͸���
   			        		
	   			     		String sqlupdatestat = "UPDATE  ��Ʊͨ������ҵ����Ӧ��  SET " 
	   			 				+ " ��ҵ��� =" + "'"  + stockbkname.trim() + "'" + ","
	   			 				+ " ��ӦTDXSWID=" + stockbkcode
	   			 				+ " WHERE ��Ʊ���� = " + "'" + stockcode.trim() + "'" 
	   			 				+ " AND ��ӦTDXSWID=" + stockbkanameindb 
	   			 				;
		   			 		System.out.println(sqlupdatestat);
		   			 		@SuppressWarnings("unused")
		   			 		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   			 		allupdatednum ++;
			   	            Files.append("���£�" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
                   
                   //�����ݿ��ж�����ǰ�ð��ĸ���
                   Set<String> tmpstockcodesetold = new HashSet();
                   CachedRowSetImpl rs = null; 
	   			   try {
	   			    	String sqlquerystat = "SELECT *  FROM ��Ʊͨ���ŷ�����Ӧ��   WHERE  ����� = " 
	   			    							+ "'"  + gupiaoheader + "'";
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
	   			    
	   			   
	   		        //��tmpstockcodesetnew�����еģ�tmpstockcodesetoldû�е�ѡ���������µģ�Ҫ�������ݿ�
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   				
		   				String sqlinsertstat = "INSERT INTO  ��Ʊͨ���ŷ�����Ӧ��(��Ʊ����,�����) values ("
		   						+ "'" + str.trim() + "'" + ","
		   						+ "'" + gupiaoheader + "'" 
		   						+ ")"
		   						;
		   				System.out.println(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				
		   				Files.append("���룺"  + str.trim() + " " + gupiaoheader + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   		    
		   	        //�� tmpstockcodesetold �����еģ�tmpstockcodesetnewû�е�ѡ�������Ǿɵģ�Ҫ�����ݿ���ɾ��
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
		   	        	String sqldeletetstat = "DELETE  FROM ��Ʊͨ���ŷ�����Ӧ�� "
		   	        							+ " WHERE ��Ʊ����=" +  "'" + str + "'"
		   	        							+ " AND �����=" + "'" + gupiaoheader + "'"
		   	        							;
		   	        	System.out.println(sqldeletetstat);
		   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
		   	    		Files.append("ɾ����"  + str.trim() + " " + gupiaoheader + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   	        }
		   	        
		   	        if(differencebankuainew.size() ==0 && differencebankuaiold.size()>0 ) { // �µ�Ϊ0���ɵĴ����㣬ɾ����ʱ������ʵ�ֻ������ݿⲻ����������ʱ�䣬�Ը���XML��Ӱ�죬����ǿ�Ƹ���һ����Ϣ
		   	        	String sqlupdatestat = "UPDATE ͨ���Ű���б� SET �������¸���ʱ�� = "
		   	        			+  formateDateForDiffDatabase("mysql", sysconfig.formatDate( new Date () ) ) + ","
		   	        			;
		   	        	System.out.println(sqlupdatestat);
		   				connectdb.sqlUpdateStatExecute (sqlupdatestat);
		   	        }
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
	   			    	String sqlquerystat = "SELECT *  FROM ��Ʊͨ���Ž�����ָ����Ӧ��   WHERE  ָ����� = " 
	   			    							+ "'"  + gupiaoheader + "'";
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
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   				
		   				String sqlinsertstat = "INSERT INTO  ��Ʊͨ���Ž�����ָ����Ӧ��(��Ʊ����,ָ�����) values ("
		   						+ "'" + str.trim() + "'" + ","
		   						+ "'" + gupiaoheader + "'" 
		   						+ ")"
		   						;
		   				System.out.println(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				addednumber ++;
		                Files.append("���룺" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   		    
		   	        //�� tmpstockcodesetold �����еģ�tmpstockcodesetnewû�е�ѡ�������Ǿɵģ�Ҫ�����ݿ���ɾ��
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
		   	        	String sqldeletetstat = "DELETE  FROM ��Ʊͨ���Ž�����ָ����Ӧ�� "
		   	        							+ " WHERE ��Ʊ����=" + "'" + str + "'" 
		   	        							+ " AND ָ�����=" + "'" + gupiaoheader + "'" 
		   	        							;
		   	        	System.out.println(sqldeletetstat);
		   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
		                Files.append("ɾ����" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
	 * ��ͨ���Ÿ������еĽ�����ָ����Ŀǰֻ�����ڸ���ָ�������еĽ�����ָ����������ֱ�Ӵ��ϱ��ж���ָ���������µģ�ɾ���ɵġ�
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
            	   
            	   String sqlinsertstat = "INSERT INTO  ͨ���Ž�����ָ���б�(ָ������,���ID,ָ������������) values ("
	   						+ " '" + zhishuname.trim() + "'" + ","
	   						+ " '" + zhishucode.trim() + "'" + ","
	   						+ " '" + "sh" + "'"
	   						+ ")"
	   						+ " ON DUPLICATE KEY UPDATE "
	   						+ " ָ������=" + " '" + zhishuname.trim() + "'" + ","
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
            	   
            	   String sqlinsertstat = "INSERT INTO  ͨ���Ž�����ָ���б�(ָ������,���ID,ָ������������) values ("
	   						+ " '" + zhishuname.trim() + "'" + ","
	   						+ " '" + zhishucode.trim() + "'" + ","
	   						+ " '" + "sz" + "'"
	   						+ ")"
	   						+ " ON DUPLICATE KEY UPDATE "
	   						+ " ָ������=" + " '" + zhishuname.trim() + "'" + ","
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
//	private void refreshTDXZhiShuLists(File tmprecordfile) 
//	{
//		 	Set<String> tmpzhishusetnew = new HashSet();
//			CachedRowSetImpl rsnew = null;
//		    try {
//		    	String sqlquerystat = "SELECT DISTINCT ָ����� FROM ��Ʊͨ���Ž�����ָ����Ӧ��" 
//		    							;
//		    	rsnew = connectdb.sqlQueryStatExecute(sqlquerystat);
//		    	
//		        rsnew.last();  
//		        int rows = rsnew.getRow();  
//		        rsnew.first();  
//		        for(int j=0;j<rows;j++) {  
//		        	String stockcode = rsnew.getString("ָ�����");
//		        	tmpzhishusetnew.add(stockcode);
//		        	
//		        	rsnew.next();
//		        }
//		        
//		    }catch(java.lang.NullPointerException e){ 
//		    	e.printStackTrace();
//		    } catch (SQLException e) {
//		    	e.printStackTrace();
//		    }catch(Exception e){
//		    	e.printStackTrace();
//		    } finally {
//	    	if(rsnew != null)
//				try {
//					rsnew.close();
//					rsnew = null;
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//		    }
//		    
//		    Set<String> tmpzhishusetold = new HashSet();
//			CachedRowSetImpl rsold = null;
//		    try {
//		    	String sqlquerystat = "SELECT DISTINCT ָ������ FROM ͨ���Ž�����ָ���б�" 
//		    							;
//		    	rsold = connectdb.sqlQueryStatExecute(sqlquerystat);
//		    	
//		    	rsold.last();  
//		        int rows = rsold.getRow();  
//		        rsold.first();  
//		        for(int j=0;j<rows;j++) {  
//		        	String stockcode = rsold.getString("ָ������");
//		        	tmpzhishusetold.add(stockcode);
//		        	
//		        	rsold.next();
//		        }
//		        
//		    }catch(java.lang.NullPointerException e){ 
//		    	e.printStackTrace();
//		    } catch (SQLException e) {
//		    	e.printStackTrace();
//		    }catch(Exception e){
//		    	e.printStackTrace();
//		    } finally {
//	    	if(rsold != null)
//				try {
//					rsold.close();
//					rsold = null;
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//		    }
//		    try {
//			    //��tmpstockcodesetnew�����еģ�tmpstockcodesetoldû�е�ѡ���������µģ�Ҫ�������ݿ�
//			        SetView<String> differencezhishunew = Sets.difference(tmpzhishusetnew, tmpzhishusetold ); 
//	   		    for (String str : differencezhishunew) {
//	   				
//	   				String sqlinsertstat = "INSERT INTO  ͨ���Ž�����ָ���б�(ָ������) values ("
//	   						+ "'" + str.trim() + "'"  
//	   						+ ")"
//	   						;	
//	   				System.out.println(sqlinsertstat);
//	   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//	                Files.append("���룺" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//	   			}
//	   		    
//	   	        //�� tmpstockcodesetold �����еģ�tmpstockcodesetnewû�е�ѡ�������Ǿɵģ�Ҫ�����ݿ���ɾ��
//	   	        SetView<String> differencezhishuold = Sets.difference(tmpzhishusetold,tmpzhishusetnew  );
//	   	        for (String str : differencezhishuold) {
//	   	        	String sqldeletetstat = "DELETE  FROM ͨ���Ž�����ָ���б�"
//	   	        							+ " WHERE ָ������=" + "'" + str + "'" 
//	   	        							;
//	   	        	System.out.println(sqldeletetstat);
//	   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//					Files.append("ɾ����" + str.trim() + " " +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//	   	        }
//			} catch (IOException e) {
//				e.printStackTrace();
//			}        
//		    
//		
//		
//	}
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
	   			    	String sqlquerystat = "SELECT *  FROM ��Ʊͨ���Ÿ������Ӧ��  WHERE  ������ = " 
	   			    							+ "'"  + gupiaoheader + "'";
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
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   				
		   				String sqlinsertstat = "INSERT INTO  ��Ʊͨ���Ÿ������Ӧ��(��Ʊ����,������) values ("
		   						+ "'" + str.trim() + "'" + ","
		   						+ "'" + gupiaoheader + "'" 
		   						+ ")"
		   						;
		   				System.out.println(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				addednumber ++;
		                Files.append("���룺" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   		    
		   	        //�� tmpstockcodesetold �����еģ�tmpstockcodesetnewû�е�ѡ�������Ǿɵģ�Ҫ�����ݿ���ɾ��
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
		   	        	String sqldeletetstat = "DELETE  FROM ��Ʊͨ���Ÿ������Ӧ�� "
		   	        							+ " WHERE ��Ʊ����=" + "'" + str + "'" 
		   	        							+ " AND ������=" + "'" + gupiaoheader + "'" 
		   	        							;
		   	        	System.out.println(sqldeletetstat);
		   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
		                Files.append("ɾ����" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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

        Set<String> tmpsysbkset =  tmpsysbkmap.keySet();
        Set<String> curdaleidetaillistset = this.getTDXBanKuaiList().keySet();
        
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
					+ "\"" +  sysconfig.formatDate(new Date())   + "\"" + ","
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
        	String oldbk = this.getTDXBanKuaiList().get(oldbkcode).getBankuainame();
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
        //��������Ŀǰ��û��д����Ϊ���������Ƹı���������������ȷ������ٿ���
		return true;
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
	        	BanKuai tmpbk = new BanKuai (rs.getString("���ID"));
	        	tmpbk.setBankuainame(rs.getString("�������") );
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
	public HashMap<String, BkChanYeLianTreeNode> getTDXBanKuaiList2()   
	{

		HashMap<String,BkChanYeLianTreeNode> tmpsysbankuailiebiaoinfo = new HashMap<String,BkChanYeLianTreeNode> ();

		String sqlquerystat = "SELECT ���ID,�������,����ʱ��  FROM ͨ���Ű���б� 	ORDER BY ������� ,���ID,����ʱ�� "
							   ;   
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
	        	
	        	BkChanYeLianTreeNode tmpbk = new BkChanYeLianTreeNode (rs.getString("�������"),rs.getString("���ID"));
	        	tmpbk.setTongDaXingBanKuaiCode(rs.getString("���ID") );
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
	        	BanKuai tmpbk = new BanKuai (rs.getString("���ID"));
	        	tmpbk.setBankuainame(rs.getString("�������") );
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
	public HashMap<String,String> getTDXFengGeBanKuaiGeGu(String currentbk) 
	{
		String sqlquerystat = "SELECT fg.��Ʊ����, agu.��Ʊ����" 
				+ " FROM ��Ʊͨ���ŷ�����Ӧ��  AS fg,  A��  AS agu"
				+ " WHERE  fg.�����= " + "'" + currentbk + "'" 
				+ " AND fg.��Ʊ����  = agu.��Ʊ����"
				//+ " GROUP BY  czjl.�����˺�, czjl.��Ʊ����, agu.��Ʊ����"
				;   
		System.out.println(sqlquerystat);
		HashMap<String,String> tmpfgset = new HashMap<String,String>();
		CachedRowSetImpl rsfg = null;
		try {  
			rsfg = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			rsfg.last();  
			int rows = rsfg.getRow();  
			rsfg.first();  
			for(int j=0;j<rows;j++) {  
				String tmpstockcode = rsfg.getString("��Ʊ����");
				String tmpstockname = rsfg.getString("��Ʊ����");
				tmpfgset.put(tmpstockcode,tmpstockname);
				rsfg.next();
			}
			
		}catch(java.lang.NullPointerException e){ 
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			try {
				rsfg.close ();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rsfg = null;
		}
		
		return tmpfgset;
	}
	/*
	 * �ҳ�ĳͨ���Ÿ���������и���
	 */
	public HashMap<String,String> getTDXGaiNianBanKuaiGeGu(String currentbk) 
	{
		String sqlquerystat = "SELECT gn.��Ʊ����, agu.��Ʊ����" 
				+ " FROM ��Ʊͨ���Ÿ������Ӧ��  AS gn, A�� AS agu"
				+ " WHERE  gn.������= " + "'" + currentbk + "'" 
				+ " AND gn.��Ʊ����  = agu.��Ʊ����"
				//+ " GROUP BY  czjl.�����˺�, czjl.��Ʊ����, agu.��Ʊ����"
				;   
		System.out.println(sqlquerystat);
		HashMap<String,String> tmpgnset = new HashMap<String,String>();
		CachedRowSetImpl rsfg = null;
		try {  
			 rsfg = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			rsfg.last();  
			int rows = rsfg.getRow();  
			rsfg.first();  
			for(int j=0;j<rows;j++) {  
				String tmpstockcode = rsfg.getString("��Ʊ����");
				String tmpstockname = rsfg.getString("��Ʊ����");
				tmpgnset.put(tmpstockcode,tmpstockname);
				rsfg.next();
			}
			rsfg.close();
		}catch(java.lang.NullPointerException e){ 
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			try {
				rsfg.close ();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rsfg = null;
		}
		
		return tmpgnset;
	}
	/*
	 * �ҳ�ĳͨ���Ű���������ҵ/����/������
	 */
	public HashMap<String,String> getTDXBanKuaiGeGuOfHyGnFg(String currentbk,String currentbkcode)
	{
		//��������İ���ǵ����飬Ҫ����Ʊͨ���Ż�������Ϣ��Ӧ�����
				String diyusqlquerystat = "SELECT count(*)  FROM ͨ���Ű���б� WHERE ���id = '" + currentbkcode +"' and CAST(��ӦTDXSWID AS signed) >0";
				System.out.println(diyusqlquerystat);
				CachedRowSetImpl rsdy = null;
				int dy = -1;
				try {  
					 rsdy = connectdb.sqlQueryStatExecute(diyusqlquerystat);
					 while(rsdy.next()) {
						 dy = rsdy.getInt(1);
						 System.out.println(dy);
					 }
				}catch(java.lang.NullPointerException e){ 
					System.out.println( "���ݿ�����ΪNULL!");
				} catch (SQLException e) {
					e.printStackTrace();
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
				}
				
				String sqlquerystat = null;
				 if(dy == 1 ) {
					 sqlquerystat = "SELECT tdxjbm.��Ʊ����GPDM AS ��Ʊ����, agu.��Ʊ����" 
								+ " FROM ��Ʊͨ���Ż�������Ϣ��Ӧ��  AS tdxjbm, A�� AS agu, ͨ���Ű���б� AS tdxbk"
								+ " WHERE  tdxbk.���ID= " + "'" + currentbkcode + "'" 
								+ " AND tdxbk.��ӦTDXSWID = tdxjbm.����DY"
								+ " AND tdxjbm.��Ʊ����GPDM  = agu.��Ʊ����"
								;
				 } else {
					 sqlquerystat = "SELECT hy.��Ʊ����, agu.��Ʊ����" 
								+ " FROM ��Ʊͨ������ҵ����Ӧ��  AS hy, A�� AS agu"
								+ " WHERE  hy.��ҵ���= " + "'" + currentbk + "'" 
								+ " AND hy.��Ʊ����  = agu.��Ʊ����"
								
								+ " UNION "
								
								+ "SELECT gn.��Ʊ����, agu.��Ʊ����" 
								+ " FROM ��Ʊͨ���Ÿ������Ӧ��  AS gn, A�� AS agu"
								+ " WHERE  gn.������= " + "'" + currentbk + "'" 
								+ " AND gn.��Ʊ����  = agu.��Ʊ����"
								
								+ " UNION "
								
								+ "SELECT fg.��Ʊ����, agu.��Ʊ����" 
								+ " FROM ��Ʊͨ���ŷ�����Ӧ��  AS fg,  A��  AS agu"
								+ " WHERE  fg.�����= " + "'" + currentbk + "'" 
								+ " AND fg.��Ʊ����  = agu.��Ʊ����"
								
								+ " UNION "

								+ "SELECT zs.��Ʊ����, agu.��Ʊ����" 
								+ " FROM ��Ʊͨ���Ž�����ָ����Ӧ��  AS zs,  A��  AS agu"
								+ " WHERE  zs.ָ�����= " + "'" + currentbk + "'" 
								+ " AND zs.��Ʊ����  = agu.��Ʊ����"
								
								+ " ORDER BY ��Ʊ����,��Ʊ���� "
								;
				 }
				
		System.out.println(sqlquerystat);
		HashMap<String,String> tmpgnset = new HashMap<String,String>();
		CachedRowSetImpl rsfg = null;
		try {  
			 rsfg = connectdb.sqlQueryStatExecute(sqlquerystat);

			 rsfg.last();  
			int rows = rsfg.getRow();  
			rsfg.first();  
			for(int j=0;j<rows;j++) {  
				String tmpstockcode = rsfg.getString(1); //"��Ʊ����"
				String tmpstockname = rsfg.getString("��Ʊ����"); //"��Ʊ����"
				tmpgnset.put(tmpstockcode,tmpstockname);
				rsfg.next();
			}
			rsfg.close();
		}catch(java.lang.NullPointerException e){ 
			//e.printStackTrace();
			System.out.println( "���ݿ�����ΪNULL!");
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			try {
				if(rsfg != null)
					rsfg.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rsfg = null;
		}
		

		return tmpgnset;
	}
	/*
	 * �ҳ�ĳͨ������ҵ�������и���
	 */
	public HashMap<String,String> getTDXHangYeBanKuaiGeGu(String currentbk) 
	{
		String sqlquerystat = "SELECT hy.��Ʊ����, agu.��Ʊ����" 
				+ " FROM ��Ʊͨ������ҵ����Ӧ��  AS hy, A�� AS agu"
				+ " WHERE  hy.��ҵ���= " + "'" + currentbk + "'" 
				+ " AND hy.��Ʊ����  = agu.��Ʊ����"
				//+ " GROUP BY  czjl.�����˺�, czjl.��Ʊ����, agu.��Ʊ����"
				;   
		System.out.println(sqlquerystat);
		HashMap<String,String> tmpgnset = new HashMap<String,String>();
		CachedRowSetImpl rsfg = null;
		try {  
			 rsfg = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			rsfg.last();  
			int rows = rsfg.getRow();  
			rsfg.first();  
			for(int j=0;j<rows;j++) {  
				String tmpstockcode = rsfg.getString("��Ʊ����");
				String tmpstockname = rsfg.getString("��Ʊ����");
				tmpgnset.put(tmpstockcode,tmpstockname);
				rsfg.next();
			}
			rsfg.close();
		}catch(java.lang.NullPointerException e){ 
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			 try {
				rsfg.close ();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			 rsfg = null;
		}
		
		return tmpgnset;
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
		System.out.println(sqlinsertstat);
		int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		
		return subcylcode;
		
	}
	
	
	public void setNewZdyBanKuai(String bkname, String bkcode) 
	{
		String dbbkname =  "zdy"+bkname.trim();
		String sqlinsertstat = "INSERT INTO  �Զ������б�(�������,����ʱ��,���ID) values ("
				+ "'" + dbbkname + "'" + ","
				+  "#" + sysconfig.formatDate(new Date()) + "#" + ","
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
			        	BanKuai tmpbk = new BanKuai (rs.getString("�������") );
			        	String tmpid = rs.getString("���ID");
			        	tmpbk.setBankuaiCode( rs.getString("���ID") );
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
	
//	private String formateDateForDiffDatabase (String dbdate)
//	{
//		if(dbdate != null) {
//			switch(databasetype) {
//			case "access": return "#" + dbdate + "#";
//							
//			case "mysql":  return "\"" + dbdate + "\"";
//							
//			}
//		} else
//			return null;
//		return null;
//	}
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
//		List<String> volamooutput = sysconfig.getTDXVOLFilesPath();
//		String exportath = sysconfig.toUNIXpath(Splitter.on('=').trimResults().omitEmptyStrings().splitToList(volamooutput.get(0) ).get(1) );
//		String filenamerule = Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(1)).get(1);
//		String dateRule = getTDXDateExportDateRule(Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(2)).get(1));
		
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
				Date lastestdbrecordsdate = null;
				try { 				
					String sqlquerystat = "SELECT  MAX(��������) 	MOST_RECENT_TIME"
							+ " FROM ͨ���Ű��ÿ�ս�����Ϣ  WHERE  ���� = " 
   							+ "'"  + tmpbkcode + "'" 
   							;
					System.out.println(sqlquerystat);
   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   			    	while(rs.next()) {
//   			    		System.out.println(rs.getMetaData().getColumnCount());
   			    		 lastestdbrecordsdate = rs.getDate("MOST_RECENT_TIME"); //mOST_RECENT_TIME 
   			    		 System.out.println(lastestdbrecordsdate);
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
				
				setVolAmoRecordsFromFileToDatabase(tmpbkcode,tmpbkfile,lastestdbrecordsdate,"ͨ���Ű��ÿ�ս�����Ϣ",dateRule,tmprecordfile);
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
				String bkfilename = (filenamerule.replaceAll("YY", allsysbk.get(tmpbkcode).getBanKuaiJiaoYiSuo().trim())).replaceAll("XXXXXX", tmpbkcode);
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

			String bkfilename = (filenamerule.replaceAll("YY", allsysbk.get(tmpbkcode).getBanKuaiJiaoYiSuo().trim())).replaceAll("XXXXXX", tmpbkcode); 
			File tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {  
			    continue; 
			} 

				CachedRowSetImpl  rs = null;
				Date lastestdbrecordsdate = null;
				try { 				
					String sqlquerystat = "SELECT MAX(��������) 	MOST_RECENT_TIME"
							+ " FROM ͨ���Ž�����ָ��ÿ�ս�����Ϣ  WHERE  ���� = " 
   							+ "'"  + tmpbkcode + "'" 
   							;
					System.out.println(sqlquerystat);
   			    	 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   			    	//rs.first();
   			    	while(rs.next()) {
   			    		lastestdbrecordsdate = rs.getDate("MOST_RECENT_TIME");
   			    		System.out.println(lastestdbrecordsdate);
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
				
				setVolAmoRecordsFromFileToDatabase(tmpbkcode,tmpbkfile,lastestdbrecordsdate,"ͨ���Ž�����ָ��ÿ�ս�����Ϣ",dateRule,tmprecordfile);
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
	private void setVolAmoRecordsFromFileToDatabase (String tmpbkcode, File tmpbkfile, Date lastestdbrecordsdate, String inserttablename, String datarule,File tmprecordfile)
	{
		 
		Charset charset = sysconfig.charSet();
		if(lastestdbrecordsdate == null) //null˵�����ݿ����滹û����ص����ݣ���ʱ������Ϊ1900����ļ����������ݶ�����
			try {
				Calendar rightNow = Calendar.getInstance();
		        rightNow.setTime(new Date() );
		        rightNow.add(Calendar.YEAR,-100);//���ڼ�1��
		        Date rightdate = rightNow.getTime();
		        SimpleDateFormat formatter = new SimpleDateFormat(datarule); 
				lastestdbrecordsdate =  formatter.parse( formatter.format(rightdate) );
				Files.append(tmpbkcode + "���ɽ�����¼���������м�¼��"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			} catch (ParseException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
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
                        //if(Pattern.matches("\\d{2}/\\d{2}/\\{4}",tmplinelist.get(0)) && !tmplinelist.get(5).equals("0")) { //�п����ǰ������ݣ���0��������������¼��
                        if( tmplinelist.size() ==7 && !tmplinelist.get(5).equals("0")) { //�п����ǰ������ݣ���0��������������¼��
                        	Date curlinedate = null;
                    		try {
                    			String beforparsedate = tmplinelist.get(0);
                    			curlinedate =  new SimpleDateFormat( datarule ).parse(beforparsedate) ;
                    			
                    			if(curlinedate.after(lastestdbrecordsdate)) {
                        			String sqlinsertstat = "INSERT INTO " + inserttablename +"(����,��������,���̼�,��߼�,��ͼ�,���̼�,�ɽ���,�ɽ���) values ("
                    						+ "'" + tmpbkcode + "'" + ","
                    						+ "'" +  sysconfig.formatDate(curlinedate) + "'" + ","
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
                    		} catch (ParseException e) {
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
	public HashMap<String, BkChanYeLianTreeNode> getTDXAllZhiShuList2() 
	{
		HashMap<String,BkChanYeLianTreeNode> tmpsysbankuailiebiaoinfo = new HashMap<String,BkChanYeLianTreeNode> ();

		String sqlquerystat = "SELECT *  FROM ͨ���Ž�����ָ���б� WHERE ���ID IS NOT NULL"
							   ;   
		System.out.println(sqlquerystat);
		CachedRowSetImpl rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	        rs.last();  
	        int rows = rs.getRow();  
	        rs.first();  
	        for(int j=0;j<rows;j++) {  
	        	BkChanYeLianTreeNode tmpbk = new BkChanYeLianTreeNode (rs.getString("ָ������"),rs.getString("���ID"));
	        	tmpbk.setTongDaXingBanKuaiCode(rs.getString("���ID") );
	        	//tmpbk.setBanKuaiJiaoYiSuo(rs.getString("ָ������������"));
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

		String sqlquerystat = "SELECT *  FROM ͨ���Ž�����ָ���б� WHERE ���ID IS NOT NULL"
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
	        	BanKuai tmpbk = new BanKuai (rs.getString("���ID"));
	        	tmpbk.setBankuainame(rs.getString("ָ������") );
	        	tmpbk.setBanKuaiJiaoYiSuo(rs.getString("ָ������������"));
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
	public boolean getTDXRelatedTablesStatus()
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
	public String getTdxBanKuaiNameByCode(String tmpbkcode) 
	{
		String sqlquerystatfrombk     = "SELECT �������  FROM ͨ���Ű���б�             WHERE ���ID = " + tmpbkcode;
		String sqlquerystatfromzhishu = "SELECT ָ������  FROM ͨ���Ž�����ָ���б�   WHERE ���ID = " + tmpbkcode;
		
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
	public boolean addBanKuaiNews (String bankuaiid,ChanYeLianNews newdetail)
	{
		int autoIncKeyFromApi = newdetail.getNewsId();
		String title = newdetail.getNewsTitle();
		Date newdate = newdetail.getGenerateDate();
		String slackurl = newdetail.getNewsSlackUrl();
		String keywords = newdetail.getKeyWords ();
		
		if( autoIncKeyFromApi == -1) { //˵�����µ�NEWS sysconfig.formatDate( actionday )
			//��update��ҵ���ű�
			String sqlinsertstat = "INSERT INTO ��ҵ����(���ű���,�ؼ���,SLACK����,¼������,�������) values ("
					+ "'" + title + "'" + ","
					+ "'" + keywords + "'" + ","
					+ "'" + slackurl  + "'" + ","
					+ formateDateForDiffDatabase("mysql", sysconfig.formatDate( newdate) ) + ","
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
			String sqlquerystat = "SELECT * FROM ��ҵ����   WHERE ¼������ >= DATE(NOW()) - INTERVAL 7 DAY ORDER BY  ¼������ DESC"
									;
			CachedRowSetImpl rs = null; 
		    try {  
		    	System.out.println(sqlquerystat);
		    	 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	 
		    	 while(rs.next()) {
		    		 ChanYeLianNews cylnew = new ChanYeLianNews ();
		    		 int newsid = rs.getInt("id");
		    		 Date selectednew = rs.getDate("¼������");
		    		 String newtitle = rs.getString("���ű���");
		    		 String keywords = rs.getString("�ؼ���");
		    		 String slack = rs.getString("SLACK����");
		    		 String relatedbk = rs.getString("�������");

		    		 cylnew.setNewsId(newsid);
		    		 cylnew.setGenerateDate (selectednew);
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
		
		//��������part
		String sqlquerystat = "SELECT * FROM ��ҵ����   WHERE ������� like '%" + bankuaiid.trim()+"|"  + "%' ORDER BY  ¼������ DESC";
		CachedRowSetImpl rs = null;
		try {
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   		 	while(rs.next()) {
	   		 	ChanYeLianNews cylnew = new ChanYeLianNews ();
	   			 int newsid = rs.getInt("id");
	   			 Date selectednew = rs.getDate("¼������");
		    		 String newtitle = rs.getString("���ű���");
		    		 String keywords = rs.getString("�ؼ���");
		    		 String slack = rs.getString("SLACK����");
		    		 
		    		 cylnew.setNewsId(newsid);
		    		 cylnew.setGenerateDate (selectednew);
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
	
	private String formateDateForDiffDatabase (String databasetype,String dbdate)
	{
		if(dbdate != null) {
			switch(databasetype) {
			case "access": return "#" + dbdate + "#";
							
			case "mysql":  return "\'" + dbdate + "\'";
							
			}
		} else
			return null;
		return null;
	}
	/*
	 * ���һ���ڵ�ռ��
	 */
	public  CachedRowSetImpl getBanKuaiZhanBi (String bkcode)
	{
		//�ж�code�ǰ��code����ָ��code
		String sqlcheckstat = "SELECT EXISTS(SELECT * FROM ͨ���Ž�����ָ��ÿ�ս�����Ϣ where ���� = '" + bkcode + "') AS result";
		System.out.println(sqlcheckstat);
		boolean inzhishutable = false;
		CachedRowSetImpl rspd = null;
		try {
			rspd = connectdb.sqlQueryStatExecute(sqlcheckstat);
   		 	int result = 0; 
   		 	while(rspd.next())
   		 		result = rspd.getInt("result");
   		 	if(result == 1)
   		 		inzhishutable = true;
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
	    }
		
		if(!inzhishutable) { //�����880�İ��
			String sqlquerystat = "SELECT Year(BKJYE.��������) AS CALYEAR , WEEK(BKJYE.��������) AS CALWEEK, BKJYE.`����` AS BKCODE , BKJYE.��������, DATE(BKJYE.�������� + INTERVAL (7 - DAYOFWEEK(BKJYE.��������)) DAY) EndOfWeekDate,"
					+ "	       SUM(BKJYE.�ɽ���)/2 AS ����ܽ��׶�, SUM(ZSJYE.�ɽ���) AS �����ܽ��׶�, SUM(BKJYE.`�ɽ���`)/(2*SUM(ZSJYE.`�ɽ���`)) AS ռ��"
					+ "	FROM  ͨ���Ű��ÿ�ս�����Ϣ BKJYE"
					+ "	INNER JOIN ͨ���Ž�����ָ��ÿ�ս�����Ϣ  ZSJYE"
					+ "	ON ZSJYE.���� in ('999999','399001' )"
					+ "	       AND BKJYE.`��������` = ZSJYE.`��������`"
					+ "	       AND Year(BKJYE.`��������`) = YEAR(NOW())"
					+ "	       AND BKJYE.`����` = '" + bkcode + "'"
					+ "			GROUP BY Year(BKJYE.`��������`),WEEK(BKJYE.`��������`),BKJYE.`����`"
					;
			System.out.println(sqlquerystat);
			CachedRowSetImpl rs = null;
			try {
				rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   		 	
			}catch(java.lang.NullPointerException e){ 
		    	e.printStackTrace();
		    } catch(Exception e){
		    	e.printStackTrace();
		    }  finally {
		    	
		    }
			return rs;
		} else { //�����ָ�����
			String sqlquerystat = "SELECT Year(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������) AS CALYEAR , WEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������) AS CALWEEK, ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`����` AS BKCODE ,"
					+ "	sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�ɽ���) AS ����ܽ��׶�,	sum(sum_dapan) AS �����ܽ��׶�, sum(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�ɽ���) / sum(sum_dapan) AS ռ��"
					+ " FROM ͨ���Ž�����ָ��ÿ�ս�����Ϣ , ("
					+ "    SELECT ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�������� dapanjyrq,  SUM(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�ɽ���) sum_dapan"
					+ "    FROM ͨ���Ž�����ָ��ÿ�ս�����Ϣ"
					+ "    where  ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`����` IN  ('999999','399001' )"
					+ "    group by dapanjyrq"
					+ ") sq"
					+ " where  ͨ���Ž�����ָ��ÿ�ս�����Ϣ.�������� = dapanjyrq AND ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`����` = '"  + bkcode + "'" + " AND Year(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������)  = YEAR(now()) "
					+ " GROUP BY Year(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������)  , WEEK(ͨ���Ž�����ָ��ÿ�ս�����Ϣ.��������), ͨ���Ž�����ָ��ÿ�ս�����Ϣ.`����`"
					; 
			System.out.println(sqlquerystat);
			CachedRowSetImpl rs = null;
			try {
				rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   		 	
			}catch(java.lang.NullPointerException e){ 
		    	e.printStackTrace();
		    } catch(Exception e){
		    	e.printStackTrace();
		    }  finally {
		    	
		    }
			return rs;
		}
	}
	/*
	 * ���ɽ���ռ�ȣ���ռ������������
	 */
	public HashMap<String,Double> getBanKuaiFengXiBarInfoOfChenJiaoErZhanBiOrderByZhanBiGrowthRate(Date bkfxdate)
	{

		HashMap<String, BanKuai> curdaleidetaillistmap = this.getTDXBanKuaiList();
		Set<String> curdaleidetaillistset = curdaleidetaillistmap.keySet();
		String fengxidate = formateDateForDiffDatabase("mysql",sysconfig.formatDate(bkfxdate).substring(0,10));
		
		HashMap<String,Double> bkfxzhanbimap = new HashMap<String,Double> ();
		//���ܵĽ���ռ��
		String sqlquerystat = "SELECT  CALYEAR ,  CALWEEK,  BKCODE ,  ����ܽ��׶�,  �����ܽ��׶�,  ռ��, BKLB.`�������`"
				+ "	FROM  ("
				+ "	SELECT Year(BKJYE.��������) AS CALYEAR , WEEK(BKJYE.��������) AS CALWEEK, BKJYE.`����` AS BKCODE ,"
				+ " SUM(BKJYE.�ɽ���)/2 AS ����ܽ��׶�, SUM(ZSJYE.�ɽ���) AS �����ܽ��׶�, SUM(BKJYE.`�ɽ���`)/(2*SUM(ZSJYE.`�ɽ���`)) AS ռ��"
				+ "	FROM  ͨ���Ű��ÿ�ս�����Ϣ BKJYE"
				+ "	INNER JOIN ͨ���Ž�����ָ��ÿ�ս�����Ϣ  ZSJYE"
				+ "	ON ZSJYE.���� in ('999999','399001' )"
				+ "    AND BKJYE.`��������` = ZSJYE.`��������`"
				+ "    AND Year(BKJYE.`��������`) = YEAR(NOW())"
				+ "    AND BKJYE.`��������` BETWEEN DATE_SUB( " +  fengxidate + " , INTERVAL( DAYOFWEEK( " +  fengxidate + " ) +5  )	DAY )"
				+ "  						AND DATE_SUB( " +  fengxidate + " , INTERVAL( DAYOFWEEK( " +  fengxidate + " )   ) DAY )"
				+ "	GROUP BY Year(BKJYE.`��������`),WEEK(BKJYE.`��������`),BKJYE.`����`"
				+ "	) X"
				+ "	,ͨ���Ű���б� BKLB"
				+ "	WHERE BKCODE = BKLB.`���ID`"
				;
				
		System.out.println(sqlquerystat);
		CachedRowSetImpl rs = null;
		try {
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   		 	while(rs.next()) {
   		 		String bkname = rs.getString("�������");
   		 		String bkcode = rs.getString("BKCODE");
   		 		Double bkzhanbi = rs.getDouble("ռ��");
   		 		
   		 		bkfxzhanbimap.put(bkcode, bkzhanbi);
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
		
		//���ܵĽ���ռ��
		sqlquerystat = "	SELECT  CALYEAR ,  CALWEEK,  BKCODE ,  ����ܽ��׶�,  �����ܽ��׶�,  ռ��, BKLB.`�������` "
				+ "FROM  ("
				+ "	SELECT Year(BKJYE.��������) AS CALYEAR , WEEK(BKJYE.��������) AS CALWEEK, BKJYE.`����` AS BKCODE,"
				+ "       SUM(BKJYE.�ɽ���)/2 AS ����ܽ��׶�, SUM(ZSJYE.�ɽ���) AS �����ܽ��׶�, SUM(BKJYE.`�ɽ���`)/(2*SUM(ZSJYE.`�ɽ���`)) AS ռ��"
				+ "	FROM  ͨ���Ű��ÿ�ս�����Ϣ BKJYE"
				+ "	INNER JOIN ͨ���Ž�����ָ��ÿ�ս�����Ϣ  ZSJYE"
				+ "	ON ZSJYE.���� in ('999999','399001' )"
				+ "    AND BKJYE.`��������` = ZSJYE.`��������`"
				+ "    AND Year(BKJYE.`��������`) = YEAR(" +  fengxidate + " )"
				+ "    AND BKJYE.`��������` BETWEEN DATE_SUB( " +  fengxidate + " ,  INTERVAL( WEEKDAY( " +  fengxidate + " ) ) DAY ) AND " +  fengxidate  
				+ " GROUP BY Year(BKJYE.`��������`),WEEK(BKJYE.`��������`),BKJYE.`����`"
				+ ") X"
				+ ",ͨ���Ű���б� BKLB"
				+ "	WHERE BKCODE = BKLB.`���ID`"
				+ "	ORDER BY ռ�� desc"
				;
		System.out.println(sqlquerystat);
		CachedRowSetImpl rssz = null; 
		try {  
		 	 rssz = connectdb.sqlQueryStatExecute(sqlquerystat);
		 	 while(rssz.next()) {
		 		String bkname = rssz.getString("�������");
   		 		String bkcode = rssz.getString("BKCODE");
   		 		Double bkbenzhouzb  = rssz.getDouble("ռ��");
   		 		
   		 		try{
	   		 		Double bkzhanbi = bkfxzhanbimap.get(bkcode);
	   		 		Double bkzbgrowthrate = (bkbenzhouzb - bkzhanbi)/bkzhanbi;
	   		 		bkfxzhanbimap.put(bkcode, bkzbgrowthrate); 
   		 		} catch(java.lang.NullPointerException e){
   		 			//û�У�˵�����°�飬��ô����
   		 			bkfxzhanbimap.put(bkcode, 10000.0); //��ʱ����������Ϊ10000
   		 		}
		 		 
		 	 }
		} catch(java.lang.NullPointerException e){ 
		 	e.printStackTrace();
		} catch (SQLException e) {
		 	System.out.println(sqlquerystat);
		 	e.printStackTrace();
		} catch(Exception e){
		 	e.printStackTrace();
		} finally {
		 	if(rssz != null)
					try {
						rssz.close();
						rssz = null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
		 }
		
		return bkfxzhanbimap;
	}
	/*
	 * ����趨�����ܵ������Ƿ�������ֻ�Ե�ǰ�߽������������Բ��ԣ���ȥ��Ĭ������������
	 * -1 ��������  0����ѡ��û������  >0�����ݵ���һ��
	 */
	public int precheckBeforeBanKuaiFengXi(Date bkfxdate)
	{
		if(CommonUtility.getWeekNumber(bkfxdate) == CommonUtility.getWeekNumber(new Date()) ) {
			
			String sqlquerystat = " SELECT MAX(��������) FROM ͨ���Ž�����ָ��ÿ�ս�����Ϣ" ;
			CachedRowSetImpl rs = null; 
			try {  
			 	 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			 	 
			 	 java.sql.Timestamp tmplastmdftime = null;
			 	 while(rs.next())
			 		  tmplastmdftime = rs.getTimestamp("MAX(��������)");
			 	 System.out.println(tmplastmdftime);
			 	 
			 	 if(CommonUtility.getWeekNumber(bkfxdate) == CommonUtility.getWeekNumber(tmplastmdftime) ){
			 		 
			 		Calendar cal = Calendar.getInstance();
				 	 cal.setTime(tmplastmdftime);
				 	 int whichdays = cal.get(Calendar.DAY_OF_WEEK);
				 	 if(whichdays == 6)
				 		 return -1;
				 	 else 
				 		 return whichdays;
			 	 } else 
			 		 return 0;
			 }catch(java.lang.NullPointerException e){ 
			 	e.printStackTrace();
			 } catch (SQLException e) {
			 	System.out.println(sqlquerystat);
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
		} else 
			return -1;
		return -1;
	}
	
	
	
	/*
	 * ���ɽ���ռ�ȣ���ռ������
	 */
	public DefaultCategoryDataset getBanKuaiFengXiBarInfoOfChenJiaoErZhanBiOrderByZhanBi(Date bkfxdate) 
	{
		HashMap<String, BanKuai> curdaleidetaillistmap = this.getTDXBanKuaiList();
		Set<String> curdaleidetaillistset = curdaleidetaillistmap.keySet();
		String fengxidate = formateDateForDiffDatabase("mysql",sysconfig.formatDate(bkfxdate).substring(0,10)); 
		
		String sqlquerystat = "	SELECT  CALYEAR ,  CALWEEK,  BKCODE ,  ����ܽ��׶�,  �����ܽ��׶�,  ռ��, BKLB.`�������` "
				+ "FROM  ("
				+ "	SELECT Year(BKJYE.��������) AS CALYEAR , WEEK(BKJYE.��������) AS CALWEEK, BKJYE.`����` AS BKCODE,"
				+ "       SUM(BKJYE.�ɽ���)/2 AS ����ܽ��׶�, SUM(ZSJYE.�ɽ���) AS �����ܽ��׶�, SUM(BKJYE.`�ɽ���`)/(2*SUM(ZSJYE.`�ɽ���`)) AS ռ��"
				+ "	FROM  ͨ���Ű��ÿ�ս�����Ϣ BKJYE"
				+ "	INNER JOIN ͨ���Ž�����ָ��ÿ�ս�����Ϣ  ZSJYE"
				+ "	ON ZSJYE.���� in ('999999','399001' )"
				+ "    AND BKJYE.`��������` = ZSJYE.`��������`"
				+ "    AND Year(BKJYE.`��������`) = YEAR(" +  fengxidate + " )"
				+ "    AND BKJYE.`��������` BETWEEN DATE_SUB( " +  fengxidate + " ,  INTERVAL( WEEKDAY( " +  fengxidate + " ) ) DAY ) AND " +  fengxidate  
				+ " GROUP BY Year(BKJYE.`��������`),WEEK(BKJYE.`��������`),BKJYE.`����`"
				+ ") X"
				+ ",ͨ���Ű���б� BKLB"
				+ "	WHERE BKCODE = BKLB.`���ID`"
				+ "	ORDER BY ռ�� desc"
				;
		System.out.println(sqlquerystat);
		CachedRowSetImpl rs = null;
		DefaultCategoryDataset barchart_dataset = new DefaultCategoryDataset();
		try {
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   		 	while(rs.next()) {
   		 		String bkname = rs.getString("�������");
   		 		String bkcode = rs.getString("BKCODE");
   		 		Double bkzhanbi = rs.getDouble("ռ��");
   		 		
   		 		barchart_dataset.addValue(bkzhanbi, "���ɽ���ռ��", bkname);
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
		
		return barchart_dataset;
	}
	/*
	 * ͬ�����ڸ��ɳɽ�����Ϣ
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
				Date lastestdbrecordsdate = null;
				try { 				
					String sqlquerystat = "SELECT  MAX(��������) 	MOST_RECENT_TIME"
							+ " FROM "+ optTable +  " WHERE  ���� = " 
   							+ "'"  + tmpbkcode + "'" 
   							;
					System.out.println(sqlquerystat);
   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   			    	while(rs.next()) {
//   			    		System.out.println(rs.getMetaData().getColumnCount());
   			    		 lastestdbrecordsdate = rs.getDate("MOST_RECENT_TIME"); //mOST_RECENT_TIME 
   			    		 System.out.println(lastestdbrecordsdate);
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
				
				setVolAmoRecordsFromFileToDatabase(tmpbkcode,tmpbkfile,lastestdbrecordsdate,optTable,dateRule,tmprecordfile);

		}
		
		return tmprecordfile;
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