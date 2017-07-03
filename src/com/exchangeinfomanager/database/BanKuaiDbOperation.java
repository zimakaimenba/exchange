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

import com.exchangeinfomanager.asinglestockinfo.ASingleStockInfo;
import com.exchangeinfomanager.bankuai.gui.BanKuai;
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

import net.ginkgo.dom4jcopy.GinkgoNode;

public class BanKuaiDbOperation 
{
	public BanKuaiDbOperation() 
	{
		initializeDb ();
		initialzieSysconf ();
//		bankuaichanyelianxml = sysconfig.getBanKuaiChanYeLianXml ();
//		geguchanyelianxml = sysconfig.getGeGuChanYeLianXmlFile ();
		twelvezdgzxmlname = sysconfig.getTwelveZhongDianGuanZhuBanKuaiSheZhiXmlFile();

	}
	
	private  ConnectDataBase2 connectdb;
	private  SystemConfigration sysconfig;
//	private String bankuaichanyelianxml;
//	private String geguchanyelianxml;
	private String twelvezdgzxmlname;
//	private Document cylxmldoc;
//	private Element cylxmlroot;
//	private Document gegucylxmldoc;
//	private Element gegucylxmlroot;
//	private Document twelvezdgzxmldoc;
//	private Element twelvezdgzxmlroot;
	
	

	private void initializeDb() 
	{
		connectdb = ConnectDataBase2.getInstance();
	}
	private void initialzieSysconf ()
	{
		sysconfig = SystemConfigration.getInstance();
	}
	
	
	/*
	 * 控制 同步通达信相关的信息，并反应到产业链XML里。  导入通达信定义的板块信息 ，包括概念，行业，风格，指数 板块
	 */
	public File refreshTDXSystemBanKuai ()
	{
		File tmpreportfolder = Files.createTempDir(); 
		File tmprecordfile = new File(tmpreportfolder + "同步通达信系统板块报告.txt");
//		
//		 //准备产业链XML文件
//		File sysbkconfigfile = new File(bankuaichanyelianxml );
//		if(!sysbkconfigfile.exists()) { //不存在，创建该文件	
//			try {
//				sysbkconfigfile.createNewFile();
//				System.out.println(bankuaichanyelianxml + "不存在，已经创建");
//			} catch (IOException e) {
//				System.out.println(bankuaichanyelianxml + "不存在，创建失败！");
//			}
//		}
//		FileInputStream sysbkxmlfileinput = null;
//		try {
//			sysbkxmlfileinput = new FileInputStream(sysbkconfigfile);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		try {
//			SAXReader reader = new SAXReader();
//			cylxmldoc = reader.read(sysbkxmlfileinput);
//			cylxmlroot = cylxmldoc.getRootElement();
//		} catch (DocumentException e) {
//			System.out.println(bankuaichanyelianxml+ "存在错误");
//			e.printStackTrace();
//			cylxmldoc = DocumentFactory.getInstance().createDocument();
//			cylxmlroot = cylxmldoc.addElement("JTree");//添加文档根
//		}
//		
//		 //准备个股产业链XML文件
//		File gegucylconfigfile = new File(geguchanyelianxml );
//		if(!gegucylconfigfile.exists()) { //不存在，创建该文件	
//			try {
//				gegucylconfigfile.createNewFile();
//				System.out.println(geguchanyelianxml + "不存在，已经创建");
//			} catch (IOException e) {
//				System.out.println(geguchanyelianxml + "不存在，创建失败！");
//			}
//		}
//		FileInputStream ggcylxmlfileinput = null;
//		try {
//			ggcylxmlfileinput = new FileInputStream(gegucylconfigfile);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		try {
//			SAXReader reader = new SAXReader();
//			gegucylxmldoc = reader.read(ggcylxmlfileinput);
//			gegucylxmlroot = gegucylxmldoc.getRootElement();
//		} catch (DocumentException e) {
//			System.out.println(geguchanyelianxml+ "存在错误");
//			e.printStackTrace();
//			gegucylxmldoc = DocumentFactory.getInstance().createDocument();
//			gegucylxmlroot = gegucylxmldoc.addElement("geguchanyelian");//添加文档根
//		}
//
//		//准备12大重点关注板块XML
//		File twelvezdgzfile = new File(twelvezdgzxmlname );
//		if(!twelvezdgzfile.exists()) { //不存在，创建该文件	
//			try {
//				twelvezdgzfile.createNewFile();
//				System.out.println(twelvezdgzxmlname + "不存在，已经创建");
//			} catch (IOException e) {
//				System.out.println(twelvezdgzxmlname + "不存在，创建失败！");
//			}
//		}
//		FileInputStream twelvezdgzfileinput = null;
//		try {
//			twelvezdgzfileinput = new FileInputStream(twelvezdgzfile);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		try {
//			SAXReader reader = new SAXReader();
//			twelvezdgzxmldoc = reader.read(twelvezdgzfileinput);
//			twelvezdgzxmlroot = twelvezdgzxmldoc.getRootElement();
//		} catch (DocumentException e) {
//			System.out.println(twelvezdgzxmlname+ "存在错误");
//			e.printStackTrace();
//			twelvezdgzxmldoc = DocumentFactory.getInstance().createDocument();
//			twelvezdgzxmlroot = twelvezdgzxmldoc.addElement("ZhongDianGuanZhuanBanKuaiDetail");//添加文档根
//		}
//		
		
		//更新通达信系统所有板块信息，在更新中，把新的存入数据库，加入XML， 旧的从XML和数据库删除
		this.refreshTDXAllBanKuaiToSystem(tmprecordfile);
		
//		OutputFormat format = OutputFormat.createPrettyPrint();
//		format.setEncoding("GBK");    // 指定XML编码        
//		//保存产业链XML
//		try {
//			XMLWriter writer = new XMLWriter(new FileOutputStream(bankuaichanyelianxml),format); // 输出全部原始数据，并用它生成新的我们需要的XML文件  
//				writer.write(cylxmldoc); //输出到文件  
//				//writer.flush();
//				writer.close();
//		} catch (IOException e) {
//				e.printStackTrace();
//		}
//		try {
//			XMLWriter writer = new XMLWriter(new FileOutputStream(geguchanyelianxml),format); // 输出全部原始数据，并用它生成新的我们需要的XML文件  
//				writer.write(gegucylxmldoc); //输出到文件  
//				//writer.flush();
//				writer.close();
//		} catch (IOException e) {
//				e.printStackTrace();
//		}
//		try {
//			XMLWriter writer = new XMLWriter(new FileOutputStream(twelvezdgzxmlname),format); // 输出全部原始数据，并用它生成新的我们需要的XML文件  
//				writer.write(twelvezdgzxmldoc); //输出到文件  
//				//writer.flush();
//				writer.close();
//		} catch (IOException e) {
//				e.printStackTrace();
//		}

		this.refreshTDXFengGeBanKuaiToGeGu(tmprecordfile);
		this.refreshTDXGaiNianBanKuaiToGeGu(tmprecordfile);
		this.refreshTDXHangYeBanKuaiToGeGu(tmprecordfile);
		this.refreshTDXZhiShuBanKuaiToGeGu(tmprecordfile); //通达信指数和股票对应关系
		this.refreshTDXZhiShuLists (tmprecordfile);

		return tmprecordfile;
	}
	
	
	
	/*
	 * 
	 */
	public File refreshTDXZDYBanKuai (HashMap<String, String> neededimportedzdybkmap)
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步通达信自定义板块报告.txt");
		
		 //准备XML文件
//		File sysconfigfile = new File(bankuaichanyelianxml );
//		if(!sysconfigfile.exists()) { //不存在，创建该文件	
//			try {
//				sysconfigfile.createNewFile();
//				System.out.println(bankuaichanyelianxml + "不存在，已经创建");
//			} catch (IOException e) {
//				System.out.println(bankuaichanyelianxml + "不存在，创建失败！");
//			}
//		}
		
		this.refreshTDXZiDingYiBanKuai (neededimportedzdybkmap,tmprecordfile);
		this.refreshTDXZiDingYiBanKuaiToGeGu(neededimportedzdybkmap, tmprecordfile);
		
		return tmprecordfile;
	}
	
	

	/*
	 * 通达信的行业与个股对应文件
	 * 相关文件：
		incon.dat                        证监会行业，通达信新行业，申万行业等描述信息
		T0002/hq_cache/block.dat         一般板块
		T0002/hq_cache/block_gn.dat      概念板块
		T0002/hq_cache/block_fg.dat       风格板块
		T0002/hq_cache/block_zs.dat       指数板块:
		T0002/hq_cache/tdxhy.cfg             每个股票对应通达信行业和申万行业
		T0002/hq_cache/tdxzs.cfg             板块指数，部分板块的最后一个字段映射到incon.dat的TDXNHY和SWHY
		T0002/blocknew/blocknew.cfg        自定义板块概要描述文件
		
		行业包括三个类别：证监会行业；申万行业；通达信新行业
		行业在文件“incon.dat”中定义。文件格式：
		1) 文件包含多个行业分类：
			a) 证监会行业：开头#ZJHHY，结束######
			b) 申万行业：开头#SWHY，结束######
			c) 通达信新行业：开头#TDXNHY，结束######
		2) 每个分类中，每一行包含一个细分行业的代码和名称，以“|”分隔
			a) 证监会行业：一级分类为A~M，二级分类A99，三级分类为A9999
			b) 申万行业：一级分类为990000，二级分类为999900，三级分类为999999
			c) 通达信新行业：一级分类为T99，二级分类为T9999，三级分类为T999999
			
			基本目录：T0002blocknew
			配置文件：blocknew.cfg 记录自定义的板块名称和文件头
			配置文件存储格式：
			1) 每个板块120字节
			2) 板块名称50字节
			3) 板块文件名头70字节
			// 自定义板块概述文件格式，T0002blocknewblocknew.cfg
			struct TTDXUserBlockRecord
			{
			char szName[50];
			char szAbbr[70]; // 也是文件名称前缀 T0002blocknewxxxx.blk
			};
			
			板块列表文件: *.blk
			1) 每行一条记录：每个记录7个数字：
			a) 市场代码1位：0 – 深市；1 – 沪市
			b) 股票代码6位
			c) 行结束符：rn
			T0002blocknewZXG.blk
			市场 股票代码
			1999999
			0399001
			0399005
			0399006
			1000016
			1000300
			0399330
	 */
	/*
	 * 更新通达信的自定义板块
	 */
	private int refreshTDXZiDingYiBanKuai (HashMap<String, String> neededimportedzdybkmap, File tmprecordfile)
	{
		Set<String> neededimportzdybknames = neededimportedzdybkmap.keySet(); 
		Set<String> curzdybknames = this.initializeSystemAllZdyBanKuaiList().keySet();
		
		 //把新的自定义板块加入数据库
	      SetView<String> differencebankuainew = Sets.difference(neededimportzdybknames, curzdybknames ); 
		    for (String str : differencebankuainew) {
				String sqlinsertstat = "INSERT INTO  通达信自定义板块列表(板块名称,创建时间) values ("
						+ "'" + str.trim() + "'" + ","
						+ "\"" +  sysconfig.formatDate(new Date())   + "\""  
						+ ")"
						;
				//System.out.println(sqlinsertstat);
				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
				//tfldresult.append("加入：" + str.trim() + " " + gupiaoheader + "\n");
			}
		    
	        //把旧的自定义板块删除出数据库
	        SetView<String> differencebankuaiold = Sets.difference(curzdybknames,neededimportzdybknames  );
	        for (String str : differencebankuaiold) {
	        	String sqldeletetstat = "DELETE  FROM 通达信自定义板块列表 "
	        							+ " WHERE 板块名称=" + "'" + str.trim() + "'" 
	        							;
	        	//System.out.println(sqldeletetstat);
	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
	    		//tfldresult.append("删除：" + str.trim() + " " + gupiaoheader + "\n");
	    		
	    		sqldeletetstat = "DELETE  FROM 股票通达信自定义板块对应表 "
						+ " WHERE 自定义板块=" + "'" + str.trim() + "'" 
						;
				//System.out.println(sqldeletetstat);
				autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
	        }
	        
	        return 1;
	}
	/*
	 * 更新自定义板块和个股的对应关系
	 */
	private int refreshTDXZiDingYiBanKuaiToGeGu (HashMap<String, String> neededimportedzdybkmap, File tmprecordfile) 
	{
		Set<String> neededimportzdybknames = neededimportedzdybkmap.keySet(); 
		//Set<String> curzdybknames = this.getZdyBkSet ();
		
		for(String newzdybk:neededimportzdybknames) {
			String filename = neededimportedzdybkmap.get(newzdybk); //str是自定义板块的名称
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
			    	String sqlquerystat = "SELECT *  FROM 股票通达信自定义板块对应表  WHERE  自定义板块 = " 
			    							+ "'"  + newzdybk.trim() + "'";
			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			        rs.last();  
			        int rows = rs.getRow();  
			        rs.first();  
			        for(int j=0;j<rows;j++) {  
			        	String stockcode = rs.getString("股票代码");
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
			
			 //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
		    SetView<String> differencebankuainew = Sets.difference(stocknamesnew, tmpstockcodesetold ); 
   		    for (String newstock : differencebankuainew) {
   				
   				String sqlinsertstat = "INSERT INTO  股票通达信自定义板块对应表(股票代码,自定义板块) values ("
   						+ "'" + newstock.trim() + "'" + ","
   						+ "'" + newzdybk.trim() + "'" 
   						+ ")"
   						;
   				//System.out.println(sqlinsertstat);
   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
   				//tfldresult.append("加入：" + str.trim() + " " + gupiaoheader + "\n");
   			}
   		    
   	        //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要从数据库中删除
   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,stocknamesnew  );
   	        for (String oldstock : differencebankuaiold) {
   	        	String sqldeletetstat = "DELETE  FROM 股票通达信自定义板块对应表 "
   	        							+ " WHERE 股票代码=" +  "'" + oldstock.trim() + "'"
   	        							+ " AND 风格板块=" + "'" + newzdybk.trim() + "'"
   	        							;
   	        	//System.out.println(sqldeletetstat);
   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
   	    		//tfldresult.append("加入：" + str.trim() + " " + gupiaoheader + "\n");
   	        }
		}
		
		return 0;
	}
	
	/*
	 * 自定义板块有些并不需要导入，用户选择导入哪些自定义板块
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
           	   //板块名称
           	   String zdybankuainame = (new String(itemBuf2,0,48)).trim();
           	   System.out.println(zdybankuainame);
           	   
           	   //板块对应file
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
	 * 通达信的行业与个股对应文件
	 * 相关文件：
		incon.dat                        证监会行业，通达信新行业，申万行业等描述信息
		T0002/hq_cache/block.dat         一般板块
		T0002/hq_cache/block_gn.dat      概念板块
		T0002/hq_cache/block_fg.dat       风格板块
		T0002/hq_cache/block_zs.dat       指数板块:
		T0002/hq_cache/tdxhy.cfg             每个股票对应通达信行业和申万行业
		T0002/hq_cache/tdxzs.cfg             板块指数，部分板块的最后一个字段映射到incon.dat的TDXNHY和SWHY
		T0002/blocknew/blocknew.cfg        自定义板块概要描述文件
		
		行业包括三个类别：证监会行业；申万行业；通达信新行业
		行业在文件“incon.dat”中定义。文件格式：
		1) 文件包含多个行业分类：
			a) 证监会行业：开头#ZJHHY，结束######
			b) 申万行业：开头#SWHY，结束######
			c) 通达信新行业：开头#TDXNHY，结束######
		2) 每个分类中，每一行包含一个细分行业的代码和名称，以“|”分隔
			a) 证监会行业：一级分类为A~M，二级分类A99，三级分类为A9999
			b) 申万行业：一级分类为990000，二级分类为999900，三级分类为999999
			c) 通达信新行业：一级分类为T99，二级分类为T9999，三级分类为T999999
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
		 
		 //把板块和对应的板块代码存入map
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
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //T01|能源
				System.out.println(tmpbkinfo);
				hydmmap.put(tmpbkinfo.get(0).trim(), tmpbkinfo.get(1).trim());
			}
			
            Files.append("开始导入通达信股票行业对应信息:" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
					stockbkname = "未知";
				}
				
				 
				//先在数据库中找出该股票，比较行业，如果不一样旧更新，如果相同就不更新
				CachedRowSetImpl rs = null;
				try {
   			    	String sqlquerystat = "SELECT *  FROM 股票通达信行业板块对应表  WHERE  股票代码 = " 
   			    							+ "'"  + stockcode + "'";
   			    	System.out.println(sqlquerystat);
   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   			    	
   			        rs.last();  
   			        int rows = rs.getRow();  
   			        rs.first();  
   			        if(rows == 0) { //该股票还不存在于数据库中，要添加
   			        	String sqlinsertstat = "INSERT INTO  股票通达信行业板块对应表(股票代码,行业板块,对应TDXSWID) values ("
		   						+ "'" + stockcode.trim() + "'" + ","
		   						+ "'" + stockbkname.trim() + "'"  + ","
		   						+ "'" + stockbkcode.trim() + "'"
		   						+ ")"
		   						;
		   				System.out.println(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   	            Files.append("加入：" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   				
		   				allupdatednum++;
   			        } else { //存在
   			        	String stockbkanameindb = rs.getString("对应TDXSWID");
   			        	if( !stockbkanameindb.equals(stockbkcode) ) { //不一样就更新
   			        		
	   			     		String sqlupdatestat = "UPDATE  股票通达信行业板块对应表  SET " 
	   			 				+ " 行业板块 =" + "'"  + stockbkname.trim() + "'" + ","
	   			 				+ " 对应TDXSWID=" + stockbkcode
	   			 				+ " WHERE 股票代码 = " + "'" + stockcode.trim() + "'" 
	   			 				+ " AND 对应TDXSWID=" + stockbkanameindb 
	   			 				;
		   			 		System.out.println(sqlupdatestat);
		   			 		@SuppressWarnings("unused")
		   			 		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   			 		allupdatednum ++;
			   	            Files.append("更新：" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
	 * 通达信的风格与个股代码对应文件
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
               
           // 股票板块信息文件头格式，T0002/hq_cache/block.dat block_fg.dat block_gn.dat block_zs.dat
		   // 文件头：384字节
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
               
               //板块个数：2字节
               dis.read(itemBuf, 0, 2); 
               String bknumber =new String(itemBuf,0,2);  
               System.out.println(bknumber);
			 
               //各板块数据存储结构（紧跟板块数目依次存放）
//               第一个版块的起始位置为0x182h。
//               每个板块占据的存储空间为2813个字节，每个板块最多包括400只股票。(2813 -9 - 2 - 2) / 7 =  400
//               板块名称：9字节
//               该板块包含的个股个数：2字节
//               板块类别：2字节
//               该板块下个股代码列表（连续存放，直到代码为空）
//                   个股代码：7字节
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
               Files.append("开始导入通达信股票风格对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   //板块名称
            	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
            	   System.out.println(gupiaoheader);
            	   
            	   //板块个股
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                  
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   System.out.println(tmpstockcodestr);
                   System.out.print(tmpstockcodestr.size());
                   Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                   System.out.println(tmpstockcodesetnew);
                   
                   //从数据库中读出当前该板块的个股
                   Set<String> tmpstockcodesetold = new HashSet();
                   CachedRowSetImpl rs = null; 
	   			   try {
	   			    	String sqlquerystat = "SELECT *  FROM 股票通达信风格板块对应表   WHERE  风格板块 = " 
	   			    							+ "'"  + gupiaoheader + "'";
	   			    	System.out.println(sqlquerystat);
	   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			        rs.last();  
	   			        int rows = rs.getRow();  
	   			        rs.first();  
	   			        for(int j=0;j<rows;j++) {  
	   			        	String stockcode = rs.getString("股票代码");
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
	   			    
	   			   
	   		        //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   				
		   				String sqlinsertstat = "INSERT INTO  股票通达信风格板块对应表(股票代码,风格板块) values ("
		   						+ "'" + str.trim() + "'" + ","
		   						+ "'" + gupiaoheader + "'" 
		   						+ ")"
		   						;
		   				System.out.println(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				
		   				Files.append("加入："  + str.trim() + " " + gupiaoheader + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   		    
		   	        //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要从数据库中删除
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
		   	        	String sqldeletetstat = "DELETE  FROM 股票通达信风格板块对应表 "
		   	        							+ " WHERE 股票代码=" +  "'" + str + "'"
		   	        							+ " AND 风格板块=" + "'" + gupiaoheader + "'"
		   	        							;
		   	        	System.out.println(sqldeletetstat);
		   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
		   	    		Files.append("删除："  + str.trim() + " " + gupiaoheader + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
	 * 通达信的行业与个股对应文件
	 * 相关文件：
		incon.dat                        证监会行业，通达信新行业，申万行业等描述信息
		T0002/hq_cache/block.dat         一般板块
		T0002/hq_cache/block_gn.dat      概念板块
		T0002/hq_cache/block_fg.dat       风格板块
		T0002/hq_cache/block_zs.dat       指数板块:
		T0002/hq_cache/tdxhy.cfg             每个股票对应通达信行业和申万行业
		T0002/hq_cache/tdxzs.cfg             板块指数，部分板块的最后一个字段映射到incon.dat的TDXNHY和SWHY
		T0002/blocknew/blocknew.cfg        自定义板块概要描述文件
		
		行业包括三个类别：证监会行业；申万行业；通达信新行业
		行业在文件“incon.dat”中定义。文件格式：
		1) 文件包含多个行业分类：
			a) 证监会行业：开头#ZJHHY，结束######
			b) 申万行业：开头#SWHY，结束######
			c) 通达信新行业：开头#TDXNHY，结束######
		2) 每个分类中，每一行包含一个细分行业的代码和名称，以“|”分隔
			a) 证监会行业：一级分类为A~M，二级分类A99，三级分类为A9999
			b) 申万行业：一级分类为990000，二级分类为999900，三级分类为999999
			c) 通达信新行业：一级分类为T99，二级分类为T9999，三级分类为T999999
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
              
          // 股票板块信息文件头格式，T0002/hq_cache/block.dat block_fg.dat block_gn.dat block_zs.dat
		   // 文件头：384字节
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
              
              //板块个数：2字节
              dis.read(itemBuf, 0, 2); 
              String bknumber =new String(itemBuf,0,2);  
              System.out.println(bknumber);
			 
              //各板块数据存储结构（紧跟板块数目依次存放）
//              第一个版块的起始位置为0x182h。
//              每个板块占据的存储空间为2813个字节，每个板块最多包括400只股票。(2813 -9 - 2 - 2) / 7 =  400
//              板块名称：9字节
//              该板块包含的个股个数：2字节
//              板块类别：2字节
//              该板块下个股代码列表（连续存放，直到代码为空）
//                  个股代码：7字节
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
              Files.append("开始导入通达信股票指数板块对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
              while (dis.read(itemBuf2, 0, sigbk) != -1) {
           	   //板块名称
           	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
           	   System.out.println(gupiaoheader);
           	   
           	   //板块个股
                  String gupiaolist =new String(itemBuf2,12,sigbk-12);
                 
                  List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                  System.out.println(tmpstockcodestr);
                  System.out.print(tmpstockcodestr.size());
                  Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                  System.out.println(tmpstockcodesetnew);
                  
                  //从数据库中读出当前该板块的个股
                  Set<String> tmpstockcodesetold = new HashSet();
                  CachedRowSetImpl rs = null;
	   			    try {
	   			    	String sqlquerystat = "SELECT *  FROM 股票通达信交易所指数对应表   WHERE  指数板块 = " 
	   			    							+ "'"  + gupiaoheader + "'";
	   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			        rs.last();  
	   			        int rows = rs.getRow();  
	   			        rs.first();  
	   			        for(int j=0;j<rows;j++) {  
	   			        	String stockcode = rs.getString("股票代码");
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
	   			    
	   			   
	   		        //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   				
		   				String sqlinsertstat = "INSERT INTO  股票通达信交易所指数对应表(股票代码,指数板块) values ("
		   						+ "'" + str.trim() + "'" + ","
		   						+ "'" + gupiaoheader + "'" 
		   						+ ")"
		   						;
		   				System.out.println(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				addednumber ++;
		                Files.append("加入：" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   		    
		   	        //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要从数据库中删除
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
		   	        	String sqldeletetstat = "DELETE  FROM 股票通达信交易所指数对应表 "
		   	        							+ " WHERE 股票代码=" + "'" + str + "'" 
		   	        							+ " AND 指数板块=" + "'" + gupiaoheader + "'" 
		   	        							;
		   	        	System.out.println(sqldeletetstat);
		   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
		                Files.append("删除：" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
	 * 从通达信更新所有的交易所指数，目前只更新在个股指数表中有的交易所指数。方法是直接从上表中读出指数，加入新的，删除旧的。
	 */
	private void refreshTDXZhiShuLists(File tmprecordfile) 
	{
		 	Set<String> tmpzhishusetnew = new HashSet();
			CachedRowSetImpl rsnew = null;
		    try {
		    	String sqlquerystat = "SELECT DISTINCT 指数板块 FROM 股票通达信交易所指数对应表" 
		    							;
		    	rsnew = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	
		        rsnew.last();  
		        int rows = rsnew.getRow();  
		        rsnew.first();  
		        for(int j=0;j<rows;j++) {  
		        	String stockcode = rsnew.getString("指数板块");
		        	tmpzhishusetnew.add(stockcode);
		        	
		        	rsnew.next();
		        }
		        
		    }catch(java.lang.NullPointerException e){ 
		    	e.printStackTrace();
		    } catch (SQLException e) {
		    	e.printStackTrace();
		    }catch(Exception e){
		    	e.printStackTrace();
		    } finally {
	    	if(rsnew != null)
				try {
					rsnew.close();
					rsnew = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    }
		    
		    Set<String> tmpzhishusetold = new HashSet();
			CachedRowSetImpl rsold = null;
		    try {
		    	String sqlquerystat = "SELECT DISTINCT 指数名称 FROM 通达信交易所指数列表" 
		    							;
		    	rsold = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	
		    	rsold.last();  
		        int rows = rsold.getRow();  
		        rsold.first();  
		        for(int j=0;j<rows;j++) {  
		        	String stockcode = rsold.getString("指数名称");
		        	tmpzhishusetold.add(stockcode);
		        	
		        	rsold.next();
		        }
		        
		    }catch(java.lang.NullPointerException e){ 
		    	e.printStackTrace();
		    } catch (SQLException e) {
		    	e.printStackTrace();
		    }catch(Exception e){
		    	e.printStackTrace();
		    } finally {
	    	if(rsold != null)
				try {
					rsold.close();
					rsold = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    }
		    try {
			    //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
			        SetView<String> differencezhishunew = Sets.difference(tmpzhishusetnew, tmpzhishusetold ); 
	   		    for (String str : differencezhishunew) {
	   				
	   				String sqlinsertstat = "INSERT INTO  通达信交易所指数列表(指数名称) values ("
	   						+ "'" + str.trim() + "'"  
	   						+ ")"
	   						;
	   				System.out.println(sqlinsertstat);
	   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
	                Files.append("加入：" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	   			}
	   		    
	   	        //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要从数据库中删除
	   	        SetView<String> differencezhishuold = Sets.difference(tmpzhishusetold,tmpzhishusetnew  );
	   	        for (String str : differencezhishuold) {
	   	        	String sqldeletetstat = "DELETE  FROM 通达信交易所指数列表"
	   	        							+ " WHERE 指数名称=" + "'" + str + "'" 
	   	        							;
	   	        	System.out.println(sqldeletetstat);
	   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
					Files.append("删除：" + str.trim() + " " +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
	   	        }
			} catch (IOException e) {
				e.printStackTrace();
			}        
		    
		
		
	}
	/*
	 * 通达信的概念板块与个股对应
	 */
	private int refreshTDXGaiNianBanKuaiToGeGu (File tmprecordfile)
	{
		File file = new File(sysconfig.getTDXGaiNianBanKuaiToGeGuFile() );
		 if(!file.exists() ) {
			 try {
				Files.append( "没有发现" +  sysconfig.getTDXGaiNianBanKuaiToGeGuFile() +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
               
           // 股票板块信息文件头格式，T0002/hq_cache/block.dat block_fg.dat block_gn.dat block_zs.dat
		   // 文件头：384字节
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
               
               //板块个数：2字节
               dis.read(itemBuf, 0, 2); 
               String bknumber =new String(itemBuf,0,2);  
               System.out.println(bknumber);
			 
               //各板块数据存储结构（紧跟板块数目依次存放）
//               第一个版块的起始位置为0x182h。
//               每个板块占据的存储空间为2813个字节，每个板块最多包括400只股票。(2813 -9 - 2 - 2) / 7 =  400
//               板块名称：9字节
//               该板块包含的个股个数：2字节
//               板块类别：2字节
//               该板块下个股代码列表（连续存放，直到代码为空）
//                   个股代码：7字节
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
               Files.append("开始导入通达信股票概念对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
               while (dis.read(itemBuf2, 0, sigbk) != -1) {
            	   //板块名称
            	   String gupiaoheader = (new String(itemBuf2,0,9)).trim();
            	   System.out.println(gupiaoheader);
            	   
            	   //板块个股
                   String gupiaolist =new String(itemBuf2,12,sigbk-12);
                  
                   List<String> tmpstockcodestr = Splitter.fixedLength(7).omitEmptyStrings().trimResults(CharMatcher.INVISIBLE).splitToList(gupiaolist); 
                   System.out.println(tmpstockcodestr);
                   System.out.print(tmpstockcodestr.size());
                   Set<String> tmpstockcodesetnew = new HashSet(tmpstockcodestr);
                   System.out.println(tmpstockcodesetnew);
                   
                   //从数据库中读出当前该板块的个股
                   Set<String> tmpstockcodesetold = new HashSet();
                   CachedRowSetImpl rs = null;
	   			    try {
	   			    	String sqlquerystat = "SELECT *  FROM 股票通达信概念板块对应表  WHERE  概念板块 = " 
	   			    							+ "'"  + gupiaoheader + "'";
	   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	   			    	
	   			        rs.last();  
	   			        int rows = rs.getRow();  
	   			        rs.first();  
	   			        for(int j=0;j<rows;j++) {  
	   			        	String stockcode = rs.getString("股票代码");
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
	   			    
	   			   
	   		        //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   				
		   				String sqlinsertstat = "INSERT INTO  股票通达信概念板块对应表(股票代码,概念板块) values ("
		   						+ "'" + str.trim() + "'" + ","
		   						+ "'" + gupiaoheader + "'" 
		   						+ ")"
		   						;
		   				System.out.println(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				addednumber ++;
		                Files.append("加入：" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   		    
		   	        //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要从数据库中删除
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
		   	        	String sqldeletetstat = "DELETE  FROM 股票通达信概念板块对应表 "
		   	        							+ " WHERE 股票代码=" + "'" + str + "'" 
		   	        							+ " AND 概念板块=" + "'" + gupiaoheader + "'" 
		   	        							;
		   	        	System.out.println(sqldeletetstat);
		   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
		                Files.append("删除：" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
	 * 把数据库中的所有板块和通达信的所有板块文件比较，保存出现的新板块，删除通达信中已经剔除的板块
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
				
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //内蒙板块|880232|3|1|0|32
				System.out.println(tmpbkinfo);
				if( !Pattern.matches("\\d{4}00",tmpbkinfo.get(5)) )  //如果是申万板块，不导入
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
			Files.append("开始导入通达信板块信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		} catch (IOException e3) {
			e3.printStackTrace();
		}

        Set<String> tmpsysbkset =  tmpsysbkmap.keySet();
        Set<String> curdaleidetaillistset = this.getTDXBanKuaiList().keySet();
        
        //把tmpsysbkset里面有的，curdaleidetaillistset没有的选出，这是新的，要加入数据库
        SetView<String> differencebankuainew = Sets.difference(tmpsysbkset, curdaleidetaillistset );
        try {
	        if(differencebankuainew.size() == 0)
	        	Files.append("没有新的板块。" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			else
				Files.append("导入新板块："+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		} catch (IOException ex) {
				ex.printStackTrace();
		}
			
        for (String newbkcode : differencebankuainew) {
        	String newbk = tmpsysbkmap.get(newbkcode).get(0);
        	String newbktdxswcode = tmpsysbkmap.get(newbkcode).get(5);
			String sqlinsertstat = "INSERT INTO  通达信板块列表(板块名称,创建时间,板块ID,对应TDXSWID) values ("
					+ "'" + newbk.trim() + "'" + ","
					+ "\"" +  sysconfig.formatDate(new Date())   + "\"" + ","
					+ "'" + newbkcode + "'" + ","
					+ "'" + newbktdxswcode + "'"
					+ ")"
					;
			System.out.println(sqlinsertstat);
			int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);

//			//在XML 中加入新的板块
//			String xpathnew = ".//Node[@Subject=\"" + newbk + "\"]";
//			Node tmpnode = cylxmldoc.selectSingleNode(xpathnew);
//			if(tmpnode == null) {
//				Element newele = cylxmlroot.addElement("Node"); //在XML 中加入新的板块
//				newele.addAttribute("Subject", newbk);
//				newele.addAttribute("Status", "4");
//				System.out.println("XML中加入" + newbk);
//			}

			try {
				Files.append(newbk.trim() + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        
        
        //把 curdaleidetaillistset 里面有的，tmpsysbkset没有的选出，这是旧的，要从数据库中删除
        SetView<String> differencebankuaiold = Sets.difference(curdaleidetaillistset, tmpsysbkset );
			try {
				if(differencebankuaiold.size() != 0)
					Files.append("删除旧板块：" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        for (String oldbkcode : differencebankuaiold) {
        	String oldbk = this.getTDXBanKuaiList().get(oldbkcode).getBankuainame();
        	String sqldeletetstat = "DELETE  FROM 通达信板块列表 "
        							+ " WHERE 板块名称=" + "'"  + oldbk + "'"
        							;
        	System.out.println(sqldeletetstat);
    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
    		
    		try {
				Files.append( oldbk.trim() + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		    		
    		//还要删除该板块在 股票概念对应表/股票行业对应表/股票风格对应表/产业链子版块列表  中的股票和板块的对应信息
    		sqldeletetstat = "DELETE  FROM  股票通达信概念板块对应表"
					+ " WHERE 概念板块=" + "'"  + oldbk + "'"
					;
			System.out.println(sqldeletetstat);
			autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
			
			sqldeletetstat = "DELETE  FROM  股票通达信行业板块对应表"
					+ " WHERE 行业板块=" + "'"  + oldbk + "'"
					;
			System.out.println(sqldeletetstat);
			autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
			
			sqldeletetstat = "DELETE  FROM 股票通达信风格板块对应表"
					+ " WHERE 风格板块=" + "'"  + oldbk + "'"
					;
			System.out.println(sqldeletetstat);
			autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
			
			sqldeletetstat = "DELETE  FROM 产业链子板块列表"
					+ " WHERE 所属通达信板块=" + "'"  + oldbk + "'"
					;
			System.out.println(sqldeletetstat);
			autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
			
//			//从产业链XML中删除该板块
//			try {
//				String xpath = ".//Node[@Subject=\"" + oldbk + "\"]";
//				//String xpath = ".//" + oldbk  ;
//				Node tmpnode = cylxmldoc.selectSingleNode(xpath);
//				cylxmlroot.remove(tmpnode);
//				//xmldoc.remove(tmpnode);
//				System.out.println("XML中删除" + oldbk);
//			} catch (java.lang.NullPointerException ex) {
//				
//			}
//			
//			//从个股产业链XML中删除该板块
//			try {
//				String xpath = ".//gegu/item[@bankuai=\"" + oldbk + "\"]";
//				//String xpath = ".//" + oldbk  ;
//				Node tmpnodegg = gegucylxmldoc.selectSingleNode(xpath);
//				gegucylxmlroot.remove(tmpnodegg);
//				//xmldoc.remove(tmpnode);
//				System.out.println("XML中删除" + oldbk);
//			} catch (java.lang.NullPointerException ex) {
//			}
//			
//			//从重点关注XML中删除该板块
//			try {
//				String xpath = ".//DaLeiBanKuai/Item[@tdxbk=\"" + oldbk + "\"]";
//				Node tmpnodegg = gegucylxmldoc.selectSingleNode(xpath);
//				gegucylxmlroot.remove(tmpnodegg);
//				System.out.println("XML中删除" + oldbk);
//			} catch (java.lang.NullPointerException ex) {
//			}
        }
   
		return true;
	}

	/*
	 * 找出数据库中通达信自定义板块
	 */
	private HashMap<String, BanKuai> initializeSystemAllZdyBanKuaiList()  
	{
		//HashMap<String,BanKuai> sysbankuailiebiaoinfo;
//		private  HashMap<String,BanKuai> zdybankuailiebiaoinfo;

		HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();

		String sqlquerystat = "SELECT 板块ID,板块名称,创建时间  FROM 通达信自定义板块列表 	ORDER BY 板块ID,板块名称 ,创建时间 "
							   ;   
		System.out.println(sqlquerystat);
		CachedRowSetImpl  rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	        rs.last();  
	        int rows = rs.getRow();  
//	        data = new Object[rows][];    
//	        int columnCount = 3;//列数  
	        rs.first();  
	        //int k = 0;  
	        //while(rs.next())
	        for(int j=0;j<rows;j++) {  
	            //Object[] row = new Object[columnCount]; 
	        	BanKuai tmpbk = new BanKuai (rs.getString("板块ID"));
	        	tmpbk.setBankuainame(rs.getString("板块名称") );
	        	tmpsysbankuailiebiaoinfo.put(rs.getString("板块ID"), tmpbk);
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
	 * 找出通达信定义的所有板块.不包括交易所的指数板块
	 */
	public HashMap<String, BanKuai> getTDXBanKuaiList()   
	{

		HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();

		String sqlquerystat = "SELECT 板块ID,板块名称,创建时间  FROM 通达信板块列表 	ORDER BY 板块名称 ,板块ID,创建时间 "
							   ;   
		System.out.println(sqlquerystat);
		CachedRowSetImpl rs = null;
	    try {  
	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	
	        rs.last();  
	        int rows = rs.getRow();  
//	        data = new Object[rows][];    
//	        int columnCount = 3;//列数  
	        rs.first();  
	        //int k = 0;  
	        //while(rs.next())
	        for(int j=0;j<rows;j++) {  
	        	BanKuai tmpbk = new BanKuai (rs.getString("板块ID"));
	        	tmpbk.setBankuainame(rs.getString("板块名称") );
	        	tmpsysbankuailiebiaoinfo.put(rs.getString("板块ID"), tmpbk);
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
	 * 找出某通达信板块的所有子板块，为板块产业链
	 */
	public ArrayList<String> getSubBanKuai(String tdxbk) 
	{
		String sqlquerystat = "SELECT * FROM 产业链子板块列表 	"
								+" WHERE 所属通达信板块 =" + "'" + tdxbk + "'" 
								+ "ORDER BY 子板块名称"
				   			   ;   
		System.out.println(sqlquerystat);
		ArrayList<String> tmpsubbklist = new ArrayList<String>();
		CachedRowSetImpl rs = null;
		try {  
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			
			rs.last();  
			int rows = rs.getRow();  
			rs.first();  
			for(int j=0;j<rows;j++) {  
				String tmpbk = rs.getString("子板块名称");
				tmpsubbklist.add(tmpbk);
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
	 * 找出某通达信风格板块的所有个股
	 */
	public HashMap<String,String> getTDXFengGeBanKuaiGeGu(String currentbk) 
	{
		String sqlquerystat = "SELECT fg.股票代码, agu.股票名称" 
				+ " FROM 股票通达信风格板块对应表  AS fg,  A股  AS agu"
				+ " WHERE  fg.风格板块= " + "'" + currentbk + "'" 
				+ " AND fg.股票代码  = agu.股票代码"
				//+ " GROUP BY  czjl.买卖账号, czjl.股票代码, agu.股票名称"
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
				String tmpstockcode = rsfg.getString("股票代码");
				String tmpstockname = rsfg.getString("股票名称");
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
	 * 找出某通达信概念板块的所有个股
	 */
	public HashMap<String,String> getTDXGaiNianBanKuaiGeGu(String currentbk) 
	{
		String sqlquerystat = "SELECT gn.股票代码, agu.股票名称" 
				+ " FROM 股票通达信概念板块对应表  AS gn, A股 AS agu"
				+ " WHERE  gn.概念板块= " + "'" + currentbk + "'" 
				+ " AND gn.股票代码  = agu.股票代码"
				//+ " GROUP BY  czjl.买卖账号, czjl.股票代码, agu.股票名称"
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
				String tmpstockcode = rsfg.getString("股票代码");
				String tmpstockname = rsfg.getString("股票名称");
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
	 * 找出某通达信板块的所有行业/概念/风格个股
	 */
	public HashMap<String,String> getTDXBanKuaiGeGuOfHyGnFg(String currentbk)
	{
		String sqlquerystat = "SELECT hy.股票代码, agu.股票名称" 
				+ " FROM 股票通达信行业板块对应表  AS hy, A股 AS agu"
				+ " WHERE  hy.行业板块= " + "'" + currentbk + "'" 
				+ " AND hy.股票代码  = agu.股票代码"
				
				+ " UNION "
				
				+ "SELECT gn.股票代码, agu.股票名称" 
				+ " FROM 股票通达信概念板块对应表  AS gn, A股 AS agu"
				+ " WHERE  gn.概念板块= " + "'" + currentbk + "'" 
				+ " AND gn.股票代码  = agu.股票代码"
				
				+ " UNION "
				
				+ "SELECT fg.股票代码, agu.股票名称" 
				+ " FROM 股票通达信风格板块对应表  AS fg,  A股  AS agu"
				+ " WHERE  fg.风格板块= " + "'" + currentbk + "'" 
				+ " AND fg.股票代码  = agu.股票代码"
				
				+ " UNION "

				+ "SELECT zs.股票代码, agu.股票名称" 
				+ " FROM 股票通达信交易所指数对应表  AS zs,  A股  AS agu"
				+ " WHERE  zs.指数板块= " + "'" + currentbk + "'" 
				+ " AND zs.股票代码  = agu.股票代码"
				
				+ " ORDER BY 股票代码,股票名称 "
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
				String tmpstockcode = rsfg.getString("股票代码");
				String tmpstockname = rsfg.getString("股票名称");
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
				rsfg.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rsfg = null;
		}
		
		return tmpgnset;
	}
	/*
	 * 找出某通达信行业板块的所有个股
	 */
	public HashMap<String,String> getTDXHangYeBanKuaiGeGu(String currentbk) 
	{
		String sqlquerystat = "SELECT hy.股票代码, agu.股票名称" 
				+ " FROM 股票通达信行业板块对应表  AS hy, A股 AS agu"
				+ " WHERE  hy.行业板块= " + "'" + currentbk + "'" 
				+ " AND hy.股票代码  = agu.股票代码"
				//+ " GROUP BY  czjl.买卖账号, czjl.股票代码, agu.股票名称"
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
				String tmpstockcode = rsfg.getString("股票代码");
				String tmpstockname = rsfg.getString("股票名称");
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
	public int addNewSubBanKuai(String newsubbk,String tdxbk) 
	{
		
		String sqlinsertstat = "INSERT INTO  产业链子板块列表(子板块名称,所属通达信板块) values ("
								+ "'" + newsubbk.trim() + "'" + ","
								+ "'" + tdxbk.trim() + "'"
								+ ")"
								;
		System.out.println(sqlinsertstat);
		int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		
		return autoIncKeyFromApi;
		
	}
	
	
	public void setNewZdyBanKuai(String bkname, String bkcode) 
	{
		String dbbkname =  "zdy"+bkname.trim();
		String sqlinsertstat = "INSERT INTO  自定义板块列表(板块名称,创建时间,板块ID) values ("
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
				
				String sqlquerystat = "SELECT 板块ID,板块名称,创建时间  FROM 通达信自定义板块列表 	ORDER BY 板块名称, 板块ID, 创建时间";   
				System.out.println(sqlquerystat);
				CachedRowSetImpl  rs = null;  
			    try {  
			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
			    	
			        rs.last();  
			        int rows = rs.getRow();  
//			        data = new Object[rows][];    
//			        int columnCount = 3;//列数  
			        rs.first();  
			        //int k = 0;  
			        //while(rs.next())
			        for(int j=0;j<rows;j++) {  
			            //Object[] row = new Object[columnCount]; 
			        	BanKuai tmpbk = new BanKuai (rs.getString("板块名称") );
			        	String tmpid = rs.getString("板块ID");
			        	tmpbk.setBankuaiCode( rs.getString("板块ID") );
			        	//tmpbk.setBankuaicreatedtime(rs.getDate("创建时间"));
		          	  
			        	//tmpbankuailiebiaoset.add(rs.getString("板块名称"));
			        	tmpsysbankuailiebiaoinfo.put(rs.getString("板块名称"),tmpbk); 
			           
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
	 * 检查数据库中的通达信板块哪些有记录文件，哪些没有。 
	 */
	
	public File preCheckTDXBanKuaiVolAmoToDb ()
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步通达信板块成交量预检查.tmp");
		
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
			Files.append("系统共有"+ allbkcode.size() + "个板块！"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		
			for(String tmpbkcode:allbkcode) {
				//String bkfilename = "SH" + filenamerule.replaceAll("YYXXXXXX", tmpbkcode);
				String bkfilename = (filenamerule.replaceAll("YY","SH")).replaceAll("XXXXXX", tmpbkcode);
				
				File tmpbkfile = new File(exportath + "/" + bkfilename);
					
					if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {
						Files.append(tmpbkcode + "未找到记录文件！"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
						notfound ++;
					}
					 else {
						 Files.append(tmpbkcode + "有记录文件！"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
						 found ++;
					 }
			}
			
			Files.append(found + "个板块有记录文件！"+ notfound + "个板块没有记录文件！" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return tmprecordfile;
	}
	/*
	 * 同步通达信板块的每日成交量信息
	 */
	public File refreshTDXBanKuaiVolAmoToDb ()
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步通达信板块成交量报告.tmp");
		
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
					String sqlquerystat = "SELECT  MAX(交易日期) 	MOST_RECENT_TIME"
							+ " FROM 通达信板块每日交易信息  WHERE  代码 = " 
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
				
				setVolAmoRecordsFromFileToDatabase(tmpbkcode,tmpbkfile,lastestdbrecordsdate,"通达信板块每日交易信息",dateRule,tmprecordfile);
		}
		
		return tmprecordfile;
	}
	/*
	 * 同步通达信交易所指数的每日成交量信息
	 */
	public File preCheckTDXZhiShuVolAmoToDb ()
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步通达信交易所指数成交量预检查.tmp");
		
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
			Files.append("数据库共有"+ allbkcode.size() + "个通达信交易所指数！"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		
			for(String tmpbkcode:allbkcode) {
				//String bkfilename = allsysbk.get(tmpbkcode).getBanKuaiJiaoYiSuo().trim() + filenamerule.replaceAll("YYXXXXXX", tmpbkcode);
				String bkfilename = (filenamerule.replaceAll("YY", allsysbk.get(tmpbkcode).getBanKuaiJiaoYiSuo().trim())).replaceAll("XXXXXX", tmpbkcode);
				File tmpbkfile = new File(exportath + "/" + bkfilename);
					
					if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {
						Files.append(tmpbkcode + "未找到记录文件！"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
						notfound ++;
					}
					 else {
						 //Files.append(tmpbkcode + "有记录文件！"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
						 found ++;
					 }
			}
			
			Files.append(found + "个通达信交易所指数有记录文件！"+ notfound + "个通达信交易所指数没有记录文件！" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return tmprecordfile;
	}
	/*
	 * 同步通达信指数每日成交量信息
	 */
	public File refreshTDXZhiShuVolAmoToDb ()
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步通达信指数成交量报告.tmp");
		
		List<String> volamooutput = getTDXVolFilesRule ();
		String exportath = volamooutput.get(0);
		String filenamerule = volamooutput.get(1);
		String dateRule = volamooutput.get(2);
//		List<String> volamooutput = sysconfig.getTDXVOLFilesPath();
//		String exportath = sysconfig.toUNIXpath(Splitter.on('=').trimResults().omitEmptyStrings().splitToList(volamooutput.get(0) ).get(1) );
//		String filenamerule = Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(1)).get(1);
//		String dateRule = getTDXDateExportDateRule(Splitter.on('=').trimResults().omitEmptyStrings().splitToList( volamooutput.get(2)).get(1));
				
		HashMap<String, BanKuai> allsysbk = this.getTDXAllZhiShuList ();
		ArrayList<String> allbkcode = new ArrayList<String>(allsysbk.keySet() );
		for(String tmpbkcode:allbkcode) {
			//String bkfilename = allsysbk.get(tmpbkcode).getBanKuaiJiaoYiSuo().trim() + filenamerule.replaceAll("YYXXXXXX", tmpbkcode);
			String bkfilename = (filenamerule.replaceAll("YY", allsysbk.get(tmpbkcode).getBanKuaiJiaoYiSuo().trim())).replaceAll("XXXXXX", tmpbkcode);
			File tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {  
			    continue; 
			} 

				CachedRowSetImpl  rs = null;
				Date lastestdbrecordsdate = null;
				try { 				
					String sqlquerystat = "SELECT MAX(交易日期) 	MOST_RECENT_TIME"
							+ " FROM 通达信交易所指数每日交易信息  WHERE  代码 = " 
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
				
				setVolAmoRecordsFromFileToDatabase(tmpbkcode,tmpbkfile,lastestdbrecordsdate,"通达信交易所指数每日交易信息",dateRule,tmprecordfile);
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
		if(lastestdbrecordsdate == null) //null说明数据库里面还没有相关的数据，把时间设置为1900年把文件中所有数据都导入
			try {
				Calendar rightNow = Calendar.getInstance();
		        rightNow.setTime(new Date() );
		        rightNow.add(Calendar.YEAR,-100);//日期减1年
		        Date rightdate = rightNow.getTime();
		        SimpleDateFormat formatter = new SimpleDateFormat(datarule); 
				lastestdbrecordsdate =  formatter.parse( formatter.format(rightdate) );
				Files.append(tmpbkcode + "板块成交量记录将导入所有记录！"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
                        //if(Pattern.matches("\\d{2}/\\d{2}/\\{4}",tmplinelist.get(0)) && !tmplinelist.get(5).equals("0")) { //有可能是半天数据，有0，不完整，不能录入
                        if( tmplinelist.size() ==7 && !tmplinelist.get(5).equals("0")) { //有可能是半天数据，有0，不完整，不能录入
                        	Date curlinedate = null;
                    		try {
                    			String beforparsedate = tmplinelist.get(0);
                    			curlinedate =  new SimpleDateFormat( datarule ).parse(beforparsedate) ;
                    			
                    			if(curlinedate.after(lastestdbrecordsdate)) {
                        			String sqlinsertstat = "INSERT INTO " + inserttablename +"(代码,交易日期,开盘价,最高价,最低价,收盘价,成交量,成交额) values ("
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
                        			Files.append(tmpbkcode + "指数成交量记录导入起始时间为:" + line + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        			Files.append(tmpbkcode + "共导入:" + lineimportcount + "个记录" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        		}
                    		} catch (ParseException e) {
//                    			e.printStackTrace();
                    			System.out.println("不是有日期的数据行");
                    		}
                        }
                    } //else {  
//                        System.out.println(line);  
//                    }  
                    nextend--;  
                }  
                nextend--;  
                rf.seek(nextend);  
                if (nextend == 0) {// 当文件指针退至文件开始处，输出第一行   文件第一行是文件抬头，不需要处理
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
	 * 交易所定义的所有指数
	 */
	public HashMap<String, BanKuai> getTDXAllZhiShuList() 
	{
		HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();

		String sqlquerystat = "SELECT *  FROM 通达信交易所指数列表 WHERE 板块ID IS NOT NULL"
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
	        	BanKuai tmpbk = new BanKuai (rs.getString("板块ID"));
	        	tmpbk.setBankuainame(rs.getString("指数名称") );
	        	tmpbk.setBanKuaiJiaoYiSuo(rs.getString("指数所属交易所"));
	        	tmpsysbankuailiebiaoinfo.put(rs.getString("板块ID"), tmpbk);
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
        lastesttdxtablesmdftime =  sysconfig.formateStringToDate("1900-01-01 00:00:00"); //"yyyy-MM-dd HH:mm:ss"
		
		HashMap<String,String> sqlstatmap = new HashMap<String,String> (); 
		String[] rmtservertable = { "通达信交易所指数列表","通达信板块列表" };
		for(String tablename:rmtservertable) {
			String sqlquerystat = null;
			sqlquerystat = " SELECT UPDATE_TIME   " 
						   +" FROM   information_schema.tables "
						   +" WHERE  TABLE_SCHEMA = 'stockinfomanagementtest' "
						   +" AND TABLE_NAME = '" + tablename + "' "
						   ;
			System.out.println(sqlquerystat);
			sqlstatmap.put("mysql", sqlquerystat);
			
			CachedRowSetImpl rs = null; 
		    try {  
		    	 rs = connectdb.sqlQueryStatExecute(sqlstatmap);
		    	 
		    	 java.sql.Timestamp tmplastmdftime = null;
		    	 while(rs.next())
		    		  tmplastmdftime = rs.getTimestamp("UPDATE_TIME");
		    	 System.out.println(tmplastmdftime);
		    	 if(tmplastmdftime == null)
		    		 lastesttdxtablesmdftime = sysconfig.formateStringToDate( sysconfig.formatDate(new Date() ) );
		    	 else if(tmplastmdftime.after(lastesttdxtablesmdftime))
		    		 	lastesttdxtablesmdftime = tmplastmdftime; 
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
//		rmtservertable.add("通达信交易所指数列表");
//		rmtservertable.add("通达信板块列表");
		List<String> rmtservertable = Lists.newArrayList("通达信板块列表", "通达信交易所指数列表");
		
		
			String sqlquerystat = null;
			sqlquerystat = " show OPEN TABLES where In_use > 0" ;
			//sqlquerystat = "SHOW OPEN TABLES WHERE `Table` LIKE '%[a股]%' AND `Database` LIKE '[stockinfomanagementtest]' AND In_use > 0";		   
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
	

}

/*
 * google guava files 类，可以直接处理读出的line
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

//  SELECT 'a股',UPDATE_TIME FROM information_schema.tables where TABLE_SCHEMA='stockinfomanagementtest' 