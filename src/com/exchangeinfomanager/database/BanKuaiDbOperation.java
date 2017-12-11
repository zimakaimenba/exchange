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
//		sqlquerystat=  "SELECT gpgn.概念板块 板块代码, tdxbk.`板块名称` 板块名称 FROM 股票通达信概念板块对应表 gpgn, 通达信板块列表 tdxbk"
//				+ "  WHERE 股票代码=" + "'" +  stockcode.trim() + "'" + "AND gpgn.概念板块 = tdxbk.`板块ID` AND ISNULL(移除时间)"
//				+ "UNION " 
//				+ " SELECT gphy.行业板块 板块代码, tdxbk.`板块名称` 板块名称 FROM 股票通达信行业板块对应表 gphy, 通达信板块列表 tdxbk "
//				+ " WHERE 股票代码=" + "'" +  stockcode.trim() + "'" + "AND gphy.`行业板块` = tdxbk.`板块ID` AND ISNULL(移除时间)"
//				+ "UNION " 
//				+ " SELECT gpfg.`风格板块`板块代码, tdxbk.`板块名称` 板块名称  FROM 股票通达信风格板块对应表 gpfg, 通达信板块列表 tdxbk"
//				+ "  WHERE 股票代码= "+ "'" +  stockcode.trim() + "'" + "AND gpfg.`风格板块` = tdxbk.`板块ID` AND ISNULL(移除时间)"
//				;
//		
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rs_gn = connectdb.sqlQueryStatExecute(sqlquerystat);
//		try  {     
//	        while(rs_gn.next()) {
//	        	String bkcode = rs_gn.getString("板块代码");
//	        	String bkname = rs_gn.getString("板块名称");
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
////		sqlquerystat=  "SELECT 自定义板块 FROM 股票通达信自定义板块对应表  WHERE 股票代码= "
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
////	        	tmpzdybk.add( rs_zdy.getString("自定义板块") );
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
			 String sqlquerystat= "SELECT * FROM A股   WHERE 股票代码 =" +"'" + stockcode +"'" ;	
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
			 String sqlquerystat= "select 板块名称, 概念时间,概念板块提醒,负面消息时间,负面消息,券商评级时间,券商评级提醒,正相关及客户,负相关及竞争对手,客户,竞争对手 from 通达信板块列表  where 板块ID = '" + bkcode +"' \r\n" + 
			 		"union\r\n" + 
			 		"select 板块名称,概念时间,概念板块提醒,负面消息时间,负面消息,券商评级时间,券商评级提醒,正相关及客户,负相关及竞争对手,客户,竞争对手 from 通达信交易所指数列表  where 板块ID = '" + bkcode + "' " 
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
	 * 获取用户输入的node的基本信息，node可能是板块也可能是个股
	 */
	public ArrayList<BkChanYeLianTreeNode> getNodesBasicInfo(String nodecode) 
	{
		ArrayList<BkChanYeLianTreeNode> nodenamelist = new ArrayList<BkChanYeLianTreeNode> ();
		CachedRowSetImpl rs = null;
		
		try {
			String sqlquerystat = "select 股票名称, 'a股' as tablename, 'gegu' as type from a股 where  股票代码 = '" + nodecode + "'" + 
					" union \r\n" + 
					" select 板块名称, '通达信板块列表' as tablename , 'bankuai' as type from 通达信板块列表 where 板块ID = '" + nodecode + "'" + 
					" union\r\n" + 
					" select 板块名称, '通达信交易所指数列表' as tablename, 'zhishu' as type from 通达信交易所指数列表 where 板块ID = '" + nodecode + "'"
					;
				System.out.println(sqlquerystat);
		    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	
		        while(rs.next()) {  
		        	String stockname = rs.getString("股票名称");
		        	BkChanYeLianTreeNode tmpnode = null;
		        	
		        	if(rs.getString("type").equals("gegu")) {
		        		tmpnode = new Stock(nodecode,stockname);
		        		
		        		sqlquerystat= "SELECT * FROM A股   WHERE 股票代码 =" +"'" + nodecode +"'" ;
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
		        		
		        		sqlquerystat= "SELECT * FROM " +  searchtable  + "  WHERE 板块ID =" +"'" + nodecode +"'" ;
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
				String stockname = rs.getString("股票名称").trim();
				tmpnode.setMyOwnName(stockname);
			} catch(java.lang.NullPointerException ex1) {
				tmpnode.setMyOwnName( " ");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				String bankuainame = rs.getString("板块名称").trim();
				tmpnode.setMyOwnName(bankuainame);
			} catch(java.lang.NullPointerException ex1) {
				tmpnode.setMyOwnName( " ");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		
		try {
			java.util.Date gainiantishidate = rs.getDate("概念时间");
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
			String gainiantishi = rs.getString("概念板块提醒");
			tmpnode.setGainiantishi(gainiantishi);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setGainiantishi(" ");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try 
		{
			Date quanshangpingjidate = rs.getDate("券商评级时间");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(quanshangpingjidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			tmpnode.setQuanshangpingjidate(ldgainiantishidate);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setQuanshangpingjidate(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try	{
			String quanshangpingji = rs.getString("券商评级提醒").trim();
			tmpnode.setQuanshangpingji(quanshangpingji);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setQuanshangpingji("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try	{
			Date fumianxiaoxidate = rs.getDate("负面消息时间");
			LocalDate ldgainiantishidate = Instant.ofEpochMilli(fumianxiaoxidate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			tmpnode.setFumianxiaoxidate(ldgainiantishidate);
		} catch(java.lang.NullPointerException ex1) { 
			tmpnode.setFumianxiaoxidate(null);
		} catch(Exception ex2) {
			ex2.printStackTrace();				
		}

		try	{
			String fumianxiaoxi = rs.getString("负面消息").trim();
			tmpnode.setFumianxiaoxi(fumianxiaoxi);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setFumianxiaoxi("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			String zhengxiangguan = rs.getString("正相关及客户");
			tmpnode.setZhengxiangguan(zhengxiangguan);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setZhengxiangguan("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try {
			String fuxiangguan = rs.getString("负相关及竞争对手");
			tmpnode.setFuxiangguan(fuxiangguan);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setFuxiangguan("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try {
			String keHuCustom = rs.getString("客户");
			tmpnode.setKeHuCustom(keHuCustom);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setKeHuCustom("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try {
			String jingZhengDuiShou = rs.getString("竞争对手");
			tmpnode.setJingZhengDuiShou(jingZhengDuiShou);
		} catch(java.lang.NullPointerException ex1) {
			tmpnode.setJingZhengDuiShou("");			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/*
	 * 控制 同步通达信相关的信息  导入通达信定义的板块信息 ，包括概念，行业，风格，指数 板块
	 */
	public File refreshTDXSystemBanKuai ()
	{
		File tmpreportfolder = Files.createTempDir(); 
		File tmprecordfile = new File(tmpreportfolder + "同步通达信系统板块报告.txt");

		//更新通达信系统所有板块信息，在更新中，把新的存入数据库，
		this.refreshTDXAllBanKuaiToSystem(tmprecordfile); //880板块
		this.refreshTDXZhiShuShangHaiLists (tmprecordfile); //上海指数
		this.refreshTDXZhiShuShenZhenLists (tmprecordfile); //深圳指数
		 
		//同步相关板块个股信息
		this.refreshTDXFengGeBanKuaiToGeGu(tmprecordfile); //风格
		this.refreshTDXGaiNianBanKuaiToGeGu(tmprecordfile); //概念
		this.refreshTDXHangYeBanKuaiToGeGu(tmprecordfile); //行业
		this.refreshTDXZhiShuBanKuaiToGeGu(tmprecordfile); //通达信指数和股票对应关系

		return tmprecordfile;
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
	   			    	String sqlquerystat = "SELECT gnbk.股票代码  FROM 股票通达信概念板块对应表 gnbk, 通达信板块列表 bklb"
		    						+ " WHERE  gnbk.板块代码 = bklb.`板块ID`"
		    						+ " AND bklb.`板块名称` = '"  + gupiaoheader + "'"
		    						+ " AND ISNULL(gnbk.移除时间)"
		    						;
	   			    	
//	   			    	String sqlquerystat = "SELECT *  FROM 股票通达信概念板块对应表  WHERE  概念板块 = " 
//	   			    							+ "'"  + gupiaoheader + "'"
//	   			    							+ "AND ISNULL(移除时间)"
//	   			    							;
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
	   			    
	   			 //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要从数据库中删除
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
//		   	        	String sqldeletetstat = "DELETE  FROM 股票通达信概念板块对应表 "
//		   	        							+ " WHERE 股票代码=" + "'" + str + "'" 
//		   	        							+ " AND 概念板块=" + "'" + gupiaoheader + "'" 
//		   	        							;
//		   	        	System.out.println(sqldeletetstat);
//		   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//		                Files.append("删除：" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//		   	        	String sqlupdatestat = "UPDATE 股票通达信概念板块对应表   SET"
//		   	        			+ " 移除时间 =" + "'" +  sysconfig.formatDate(new Date() ) + "'" 
//	   			 				+ " WHERE 股票代码 = " + "'" + str.trim() + "'" 
//	   			 				+ " AND 概念板块=" + "'" + gupiaoheader + "'" 
//	   			 				+ " AND isnull(移除时间)"
//	   			 				;
		   	        	String sqlupdatestat = "UPDATE 股票通达信概念板块对应表 JOIN 通达信板块列表"
       							+ "  ON  股票通达信概念板块对应表.`股票代码` = " + "'" + str.trim() + "'"
       							+ "  AND isnull(移除时间)"
       							+ "  and  股票通达信概念板块对应表.`板块代码` = 通达信板块列表.`板块ID` "
       							+ "  and 通达信板块列表.`板块名称`=" + "'" + gupiaoheader + "'"
       							+ "  SET 移除时间 = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
       							;
				       	System.out.println(sqlupdatestat);
				   		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   	        }
	   			   
	   		        //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   				
//		   				String sqlinsertstat = "INSERT INTO  股票通达信概念板块对应表(股票代码,概念板块,股票权重) values ("
//		   						+ "'" + str.trim() + "'" + ","
//		   						+ "'" + gupiaoheader + "'"  + ","
//		   						+ 10 
//		   						+ ")"
//		   						;
		   		    	String sqlinsertstat = "insert into 股票通达信概念板块对应表(股票代码,板块代码,股票权重)"
								+ "	  SELECT '" + str + "', 通达信板块列表.`板块ID`, 10 "
								+ " FROM 通达信板块列表  where 通达信板块列表.`板块名称` = '" + gupiaoheader + "'"   
								;
		   				System.out.println(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				addednumber ++;
		                Files.append("加入：" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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

        Set<String> tmpsysbkset =  tmpsysbkmap.keySet(); //新的板块列表
        HashMap<String, BanKuai> curbkindbmap = this.getTDXBanKuaiList();
        Set<String> curdaleidetaillistset = curbkindbmap.keySet();
        
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
        	String oldbk = this.getTDXBanKuaiList().get(oldbkcode).getMyOwnName();
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
        }
        
        //对于没有变化的板块，要检查板块名称是否有变化
        SetView<String> intersectionbankuai = Sets.intersection(curdaleidetaillistset, tmpsysbkset );
        for(String interbkcode : intersectionbankuai) {
        	String bknameintdxfile =  tmpsysbkmap.get(interbkcode).get(0); //通达信里面的板块名字
        	String tdxswidintdxfile = tmpsysbkmap.get(interbkcode).get(5); 
        	String bknameincurdb = curbkindbmap.get(interbkcode).getMyOwnName();
        	
        	String updatesqlstat = "UPDATE 通达信板块列表 SET "
        						+ " 板块名称 = '" + bknameintdxfile  + "'" + ","
        						+ " 对应TDXSWID= '" + tdxswidintdxfile + "'"
        						+ " where 板块名称 != '" + bknameintdxfile + "' and 板块ID = '" + interbkcode + "'"
        						;
        	System.out.println(updatesqlstat);
        	connectdb.sqlUpdateStatExecute(updatesqlstat);
        }
        
		return true;
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
			
			while ( !(line = bufr.readLine() ).equals("######") ) {  //行业名称和T开头代码对应关系
				System.out.println(line);
				List<String> tmpbkinfo = Splitter.on("|").omitEmptyStrings().splitToList(line); //T01|能源  T010101|煤炭开采
				System.out.println(tmpbkinfo);
				hydmmap.put(tmpbkinfo.get(0).trim(), tmpbkinfo.get(1).trim());
			}
			
            Files.append("开始导入通达信股票行业对应信息:" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
			fr = new FileReader(sysconfig.getTDXHangYeBanKuaiToGeGuFile()); //通达信个股和板块的对应关系
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
   			    							+ "'"  + stockcode + "'"
   			    							+ " AND 对应TDXSWID=" +  "'" + stockbkcode + "'"
   			    							+ " AND isnull(移除时间)"
   			    							;
   			    	System.out.println(sqlquerystat);
   			    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   			    	
   			        rs.last();  
   			        int rows = rs.getRow();  
   			        rs.first();  
   			        if(rows == 0) { //该股票还不存在于数据库中，要添加
//   			        	String sqlinsertstat = "INSERT INTO  股票通达信行业板块对应表(股票代码,行业板块,对应TDXSWID,股票权重) values ("
//		   						+ "'" + stockcode.trim() + "'" + ","
//		   						+ "'" + stockbkname.trim() + "'"  + ","
//		   						+ "'" + stockbkcode.trim() + "'" + ","
//		   						+ 10
//		   						+ ")"
//		   						;
   			        	String sqlinsertstat =  "insert into 股票通达信行业板块对应表(股票代码,板块代码,对应TDXSWID,股票权重)"
												+ " SELECT '" + stockcode.trim() + "', 通达信板块列表.`板块ID`,'" + stockbkcode + "',10 "
												+ " FROM 通达信板块列表  where 通达信板块列表.`对应TDXSWID` = '" + stockbkcode + "'"
												;
		   				System.out.println(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   	            Files.append("加入：" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   				
		   				allupdatednum++;
   			        } else { //存在
   			        	String stockbkanameindb = rs.getString("对应TDXSWID");
   			        	if( !stockbkanameindb.equals(stockbkcode) ) { //不一样说明板块有变化，老的板块更新移除时间，同时加入一条新信息，反应新的板块
   			        		//先把老记录 移除时间更新
//	   			     		String sqlupdatestat = "UPDATE  股票通达信行业板块对应表  SET " 
//	   			 				+ " 移除时间 =" + "'" +  sysconfig.formatDate(new Date() ) + "'" + ","
//	   			 				+ " WHERE 股票代码 = " + "'" + stockcode.trim() + "'" 
//	   			 				+ " AND 对应TDXSWID=" + stockbkanameindb 
//	   			 				+ " AND isnull(移除时间)"
//	   			 				;
   			        		String sqlupdatestat = "UPDATE 股票通达信行业板块对应表 JOIN 通达信板块列表"
   	       							+ "  ON  股票通达信行业板块对应表.`股票代码` = " + "'" + stockcode.trim() + "'"
   	       							+ "  AND isnull(移除时间)"
   	       							+ "  and  股票通达信行业板块对应表.`板块代码` = 通达信板块列表.`板块ID` "
   	       							+ "  and 通达信板块列表.`对应TDXSWID` = " + "'" + stockbkanameindb + "'"
   	       							+ "  SET 移除时间 = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
   	       							;
		   			 		System.out.println(sqlupdatestat);
		   			 		@SuppressWarnings("unused")
		   			 		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   			 		allupdatednum ++;
		   			 		
		   			 		//再增加一条记录
//			   			 	String sqlinsertstat = "INSERT INTO  股票通达信行业板块对应表(股票代码,行业板块,对应TDXSWID,股票权重) values ("
//			   						+ "'" + stockcode.trim() + "'" + ","
//			   						+ "'" + stockbkname.trim() + "'"  + ","
//			   						+ "'" + stockbkcode.trim() + "'" + ","
//			   						+ 10
//			   						+ ")"
//			   						;
		   			 		String sqlinsertstat =  "insert into 股票通达信行业板块对应表(股票代码,板块代码,对应TDXSWID,股票权重)"
								+ " SELECT '" + stockcode.trim() + "', 通达信板块列表.`板块ID`,'" + stockbkcode + "',10 "
								+ " FROM 通达信板块列表  where 通达信板块列表.`对应TDXSWID` = '" + stockbkcode + "'"
								;
			   				System.out.println(sqlinsertstat);
			   				autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//		   	            Files.append("加入：" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//			   	        Files.append("更新：" + stockcode.trim() + " " + stockbkname.trim()  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
                   
                   //比如 融资融券， 是个板块，但没有ID，在通达信板块列表里面没有，要先判断这种情形，以便用不同的SQL
                   boolean hasbkcode = true;
                   CachedRowSetImpl rspd = null; 
	   			   try {
	   			    	String sqlquerystat = "SELECT count(*) as result FROM 通达信板块列表 WHERE 板块名称='"  + gupiaoheader + "'";

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

                   
                   //从数据库中读出当前该板块的个股
                   Set<String> tmpstockcodesetold = new HashSet();
                   CachedRowSetImpl rs = null; 
	   			   try {
	   				   String sqlquerystat;
	   				   if(hasbkcode)
	   			    	sqlquerystat = "SELECT fgbk.股票代码  FROM 股票通达信风格板块对应表 fgbk, 通达信板块列表 bklb"
	   			    						+ " WHERE  fgbk.板块代码 = bklb.`板块ID`"
	   			    						+ " AND bklb.`板块名称` = '"  + gupiaoheader + "'"
	   			    						+ " AND ISNULL(fgbk.移除时间)"
	   			    						;
	   				   else 
	   					sqlquerystat = "SELECT 股票代码  FROM 股票通达信风格板块对应表 "
	   									+ " WHERE 板块代码='" + gupiaoheader + "'"
	   									+ " AND ISNULL(移除时间)"
	   									;  

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
		   		    
		   	        //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要在数据库中update移除的时间后才能增加新的
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
//		   	        	String sqldeletetstat = "DELETE  FROM 股票通达信风格板块对应表 "
//		   	        							+ " WHERE 股票代码=" +  "'" + str + "'"
//		   	        							+ " AND 风格板块=" + "'" + gupiaoheader + "'"
//		   	        							;
		   	        	String sqlupdatestat ;
		   	        	if(hasbkcode)
		   	        		sqlupdatestat = "UPDATE 股票通达信风格板块对应表 JOIN 通达信板块列表"
		   	        							+ "  ON  股票通达信风格板块对应表.`股票代码` = " + "'" + str.trim() + "'"
		   	        							+ "  AND isnull(移除时间)"
		   	        							+ "  and  股票通达信风格板块对应表.`板块代码` = 通达信板块列表.`板块ID` "
		   	        							+ "  and 通达信板块列表.`板块名称` = " + "'" + gupiaoheader + "'"
		   	        							+ "  SET 移除时间 = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
		   	        							;
		   	        	else
		   	        		sqlupdatestat = "UPDATE 股票通达信风格板块对应表 SET 移除时间 = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
		   	        						+" WHERE 板块代码=" + "'" + gupiaoheader + "'"
		   	        						+" AND 股票代码= " + "'" + str.trim() + "'"
		   	        						+ "  AND isnull(移除时间)"
		   	        						;
		   	        	System.out.println(sqlupdatestat);
		   	    		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
//		   	    		Files.append("删除："  + str.trim() + " " + gupiaoheader + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   	        }
		   	        
	   		        //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   				
		   		    	String sqlinsertstat ;
		   	        	if(hasbkcode)
			   				 sqlinsertstat = "insert into 股票通达信风格板块对应表(股票代码,板块代码,股票权重)"
			   									+ "	  SELECT '" + str + "', 通达信板块列表.`板块ID`, 10 "
			   									+ " FROM 通达信板块列表  where 通达信板块列表.`板块名称` = '" + gupiaoheader + "'"   
			   									;
		   	        	else
		   	        		sqlinsertstat = "insert into 股票通达信风格板块对应表(股票代码,板块代码,股票权重) VALUES "
		   	        						+ "('" + str + "','" +  gupiaoheader + "',10)"
		   	        						;
		   				System.out.println(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				
//		   				Files.append("加入："  + str.trim() + " " + gupiaoheader + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
		   			}
		   	        
//		   	        if(differencebankuainew.size() ==0 && differencebankuaiold.size()>0 ) { // 新的为0，旧的大于零，删除的时候现在实现机制数据库不更新最后更新时间，对更新XML有影响，所以强制更新一条信息
//		   	        	String sqlupdatestat = "UPDATE 通达信板块列表 SET 数据最新更新时间 = "
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
//	   			    	String sqlquerystat = "SELECT *  FROM 股票通达信交易所指数对应表   WHERE  指数板块 = " 
//	   			    							+ "'"  + gupiaoheader + "'"
//	   			    							+ " AND ISNULL(移除时间)"
//	   			    							;
	   			    	String sqlquerystat = "SELECT zsbk.股票代码  FROM 股票通达信交易所指数对应表  zsbk, 通达信交易所指数列表 zslb"
	    						+ " WHERE  zsbk.板块代码 = zslb.`板块ID`"
	    						+ " AND zslb.`板块名称` = '"  + gupiaoheader + "'"
	    						+ " AND ISNULL(zsbk.移除时间)"
	    						;
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
	   			    
	   			    //把 tmpstockcodesetold 里面有的，tmpstockcodesetnew没有的选出，这是旧的，要从数据库中更新时间
		   	        SetView<String> differencebankuaiold = Sets.difference(tmpstockcodesetold,tmpstockcodesetnew  );
		   	        for (String str : differencebankuaiold) {
//		   	        	String sqldeletetstat = "DELETE  FROM 股票通达信交易所指数对应表 "
//		   	        							+ " WHERE 股票代码=" + "'" + str + "'" 
//		   	        							+ " AND 指数板块=" + "'" + gupiaoheader + "'" 
//		   	        							;
//		   	        	System.out.println(sqldeletetstat);
//		   	    		int autoIncKeyFromApi = connectdb.sqlDeleteStatExecute(sqldeletetstat);
//		                Files.append("删除：" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
//		   	        	String sqlupdatestat = "UPDATE 股票通达信交易所指数对应表   SET"
//		   	        			+ " 移除时间 =" + "'" +  sysconfig.formatDate(new Date() ) + "'" 
//	   			 				+ " WHERE 股票代码 = " + "'" + str.trim() + "'" 
//	   			 				+ " AND 指数板块=" + "'" + gupiaoheader + "'" 
//	   			 				+ " AND isnull(移除时间)"
//	   			 				;
		   	        	String sqlupdatestat = "UPDATE 股票通达信交易所指数对应表  JOIN 通达信交易所指数列表"
       							+ "  ON  股票通达信交易所指数对应表.`股票代码` = " + "'" + str.trim() + "'"
       							+ "  AND isnull(移除时间)"
       							+ "  and  股票通达信交易所指数对应表.`板块代码` = 通达信交易所指数列表.`板块ID` "
       							+ "  and 通达信交易所指数列表.`板块名称` = " + "'" + gupiaoheader + "'"
       							+ "  SET 移除时间 = " + "'" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now() ) + "'"
       							;	
				       	System.out.println(sqlupdatestat);
				   		int autoIncKeyFromApi = connectdb.sqlUpdateStatExecute(sqlupdatestat);
		   	        }
	   			   
	   		        //把tmpstockcodesetnew里面有的，tmpstockcodesetold没有的选出，这是新的，要加入数据库
	   		        SetView<String> differencebankuainew = Sets.difference(tmpstockcodesetnew, tmpstockcodesetold ); 
		   		    for (String str : differencebankuainew) {
		   				
//		   				String sqlinsertstat = "INSERT INTO  股票通达信交易所指数对应表(股票代码,指数板块,股票权重) values ("
//		   						+ "'" + str.trim() + "'" + ","
//		   						+ "'" + gupiaoheader + "'" + ","
//		   						+ 10
//		   						+ ")"
//		   						;
		   		    	String sqlinsertstat = "insert into 股票通达信交易所指数对应表(股票代码,板块代码,股票权重)"
								+ "	  SELECT '" + str.trim() + "', 通达信交易所指数列表.`板块ID`, 10 "
								+ " FROM 通达信交易所指数列表  where 通达信交易所指数列表.`板块名称` = '" + gupiaoheader + "'"   
								;
		   				System.out.println(sqlinsertstat);
		   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
		   				addednumber ++;
		                Files.append("加入：" + str.trim() + " " + gupiaoheader +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
	 * 从通达信更新所有的交易所指数，上海part
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
               Files.append("开始导入通达信股票指数板块对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
            	   
            	   String sqlinsertstat = "INSERT INTO  通达信交易所指数列表(板块名称,板块ID,指数所属交易所) values ("
	   						+ " '" + zhishuname.trim() + "'" + ","
	   						+ " '" + zhishucode.trim() + "'" + ","
	   						+ " '" + "sh" + "'"
	   						+ ")"
	   						+ " ON DUPLICATE KEY UPDATE "
	   						+ " 板块名称=" + " '" + zhishuname.trim() + "'" + ","
	   						+ " 指数所属交易所=" + " '" + "sh" + "'" 
	   						;
            	   System.out.println(sqlinsertstat);
	   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//	                Files.append("加入：" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
	 * 从通达信更新所有的交易所指数，深圳part
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
               Files.append("开始导入通达信股票指数板块对应信息:" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
            	   
            	   String sqlinsertstat = "INSERT INTO  通达信交易所指数列表(板块名称,板块ID,指数所属交易所) values ("
	   						+ " '" + zhishuname.trim() + "'" + ","
	   						+ " '" + zhishucode.trim() + "'" + ","
	   						+ " '" + "sz" + "'"
	   						+ ")"
	   						+ " ON DUPLICATE KEY UPDATE "
	   						+ " 板块名称=" + " '" + zhishuname.trim() + "'" + ","
	   						+ " 指数所属交易所=" + " '" + "sz" + "'" 
	   						;
            	   System.out.println(sqlinsertstat);
	   				int autoIncKeyFromApi = connectdb.sqlInsertStatExecute(sqlinsertstat);
//	                Files.append("加入：" + str.trim() + " "  +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
	        	BanKuai tmpbk = new BanKuai (rs.getString("板块ID"),rs.getString("板块名称") );
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
	 * 找出通达信定义的所有板块.不包括交易所的指数板块，用新的BkChanYeLianTreeNode存储
	 */
//	public HashMap<String, BkChanYeLianTreeNode> getTDXBanKuaiList()   
//	{
//		HashMap<String,BkChanYeLianTreeNode> tmpsysbankuailiebiaoinfo = new HashMap<String,BkChanYeLianTreeNode> ();
//
//		String sqlquerystat = "SELECT 板块ID,板块名称,创建时间  FROM 通达信板块列表 	ORDER BY 板块名称 ,板块ID,创建时间 "
//							   ;   
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rs = null;
//	    try {  
//	    	rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//	    	
//	        rs.last();  
//	        int rows = rs.getRow();  
////	        data = new Object[rows][];    
////	        int columnCount = 3;//列数  
//	        rs.first();  
//	        //int k = 0;  
//	        //while(rs.next())
//	        for(int j=0;j<rows;j++) { 
//	        	
//	        	BkChanYeLianTreeNode tmpbk = new BanKuai (rs.getString("板块ID"),rs.getString("板块名称"));
//	        	tmpsysbankuailiebiaoinfo.put(rs.getString("板块ID"), tmpbk);
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
	 * 所有股票
	 */
	public HashMap<String, Stock> getAllStocks() 
	{
		HashMap<String,Stock> tmpsysbankuailiebiaoinfo = new HashMap<String,Stock> ();

		String sqlquerystat = "SELECT 股票代码,股票名称 FROM A股"									
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
	        	Stock tmpbk = new Stock (rs.getString("股票代码"),rs.getString("股票名称"));
	        	tmpsysbankuailiebiaoinfo.put(rs.getString("股票代码"), tmpbk);
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
	 * 所有的指数，包括交易所指数和板块
	 */
	public HashMap<String, BanKuai> getTDXAllZhiShuAndBanKuai() 
	{
		HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();

		String sqlquerystat = "SELECT 板块ID,板块名称,创建时间  FROM 通达信交易所指数列表" +
								" UNION" +
								" SELECT 板块ID,板块名称,创建时间  FROM 通达信板块列表  "  ;	
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
	        	System.out.println(rs.getString("板块ID") );
	        	System.out.println(rs.getString("板块名称"));
	        	BanKuai tmpbk = new BanKuai (rs.getString("板块ID"),rs.getString("板块名称") );
//	        	boolean notexporttogephi = rs.getBoolean("不导出到gephi");
//	        	if(notexporttogephi)
//	        		tmpbk.setNotExportToGephi();
//	        	tmpbk.setSuoShuJiaoYiSuo(rs.getString("指数所属交易所"));
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
	 * 交易所定义的所有指数
	 */
	public HashMap<String, BanKuai> getTDXAllZhiShuList() 
	{
		HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();

		String sqlquerystat = "SELECT *  FROM 通达信交易所指数列表 "
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
//	        	System.out.println(rs.getString("板块ID"));
	        	BanKuai tmpbk = new BanKuai (rs.getString("板块ID"),rs.getString("板块名称"));
	        	tmpbk.setSuoShuJiaoYiSuo(rs.getString("指数所属交易所"));
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
	 * 找出通达信定义的所有板块.不包括交易所的指数板块
	 */
	public HashMap<String, BanKuai> getTDXBanKuaiList()   
	{

		HashMap<String,BanKuai> tmpsysbankuailiebiaoinfo = new HashMap<String,BanKuai> ();

		String sqlquerystat = "SELECT 板块ID,板块名称,创建时间  FROM 通达信板块列表 	ORDER BY 板块名称 ,板块ID,创建时间 "  ;   
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
	        	BanKuai tmpbk = new BanKuai (rs.getString("板块ID"),rs.getString("板块名称") );
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
	public HashMap<String,String> getSubBanKuai(String tdxbk) 
	{
		String sqlquerystat = "SELECT * FROM 产业链子板块列表 	"
								+" WHERE 所属通达信板块 =" + "'" + tdxbk + "'" 
								+ "ORDER BY 子板块名称"
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
				String tmpbkcode = rs.getString("子板块代码");
				String tmpbkname = rs.getString("子板块名称");
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
	 * 找出某通达信风格板块的所有个股
	 */
//	private HashMap<String,String> getTDXFengGeBanKuaiGeGu(String currentbk) 
//	{
//		String sqlquerystat = "SELECT fg.股票代码, agu.股票名称" 
//				+ " FROM 股票通达信风格板块对应表  AS fg,  A股  AS agu"
//				+ " WHERE  fg.风格板块= " + "'" + currentbk + "'" 
//				+ " AND fg.股票代码  = agu.股票代码" + "'"
//				+ " AND ISNULL(移除时间)"
//				//+ " GROUP BY  czjl.买卖账号, czjl.股票代码, agu.股票名称"
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
//				String tmpstockcode = rsfg.getString("股票代码");
//				String tmpstockname = rsfg.getString("股票名称");
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
	 * 找出某通达信概念板块的所有个股
	 */
//	private HashMap<String,String> getTDXGaiNianBanKuaiGeGu(String currentbk) 
//	{
//		String sqlquerystat = "SELECT gn.股票代码, agu.股票名称" 
//				+ " FROM 股票通达信概念板块对应表  AS gn, A股 AS agu"
//				+ " WHERE  gn.概念板块= " + "'" + currentbk + "'" 
//				+ " AND gn.股票代码  = agu.股票代码" + "'"
//				+ " AND ISNULL(移除时间)"
//				//+ " GROUP BY  czjl.买卖账号, czjl.股票代码, agu.股票名称"
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
//				String tmpstockcode = rsfg.getString("股票代码");
//				String tmpstockname = rsfg.getString("股票名称");
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
	 * 找出某个时间点 行业/概念/风格/指数 通达信板块的所有个股及个股权重
	 */
//	public HashMap<String, Stock> getTDXBanKuaiGeGuOfHyGnFg(String currentbk,String currentbkcode, Date selecteddatestart , Date selecteddateend)
//	{
//				String actiontable = this.getBanKuaiType(currentbkcode);
//				if(actiontable == null)
//					return null;
//			
//				String sqlquerystat = null;
//				 if(actiontable.trim().equals("股票通达信基本面信息对应表") ) { //找地域板块
//					 sqlquerystat = "SELECT tdxjbm.股票代码GPDM AS 股票代码, agu.股票名称, tdxjbm.股票权重" 
//								+ " FROM 股票通达信基本面信息对应表  AS tdxjbm, A股 AS agu, 通达信板块列表 AS tdxbk"
//								+ " WHERE  tdxbk.板块ID= " + "'" + currentbkcode + "'" 
//								+ " AND tdxbk.对应TDXSWID = tdxjbm.地域DY"
//								+ " AND tdxjbm.股票代码GPDM  = agu.股票代码" 
//								;
//				 } else { //概念风格行业板块
//					 sqlquerystat = "SELECT hy.股票代码, agu.股票名称, hy.股票权重 , hy.`行业板块` as 板块ID" 
//								+ " FROM 股票通达信行业板块对应表  AS hy, A股 AS agu"
//								+ " WHERE  hy.行业板块= " + "'" + currentbkcode + "'" 
//								+ " AND hy.股票代码  = agu.股票代码" 
//								+ " AND unix_timestamp(Date('" + sysconfig.formatDate(selecteddateend) + "')) >= unix_timestamp(Date(加入时间)) "
//								+ " AND (unix_timestamp(Date('" +sysconfig.formatDate(selecteddatestart) + "')) <= unix_timestamp(Date(移除时间)) OR ISNULL(移除时间) ) "
//								
//								+ " UNION "
//								
//								+ "SELECT gn.股票代码, agu.股票名称, gn.股票权重 , gn.`概念板块` as 板块ID" 
//								+ " FROM 股票通达信概念板块对应表  AS gn, A股 AS agu"
//								+ " WHERE  gn.概念板块= " + "'" + currentbkcode + "'" 
//								+ " AND gn.股票代码  = agu.股票代码" 
//								+ " AND unix_timestamp(Date('" + sysconfig.formatDate(selecteddateend) + "')) >= unix_timestamp(Date(加入时间)) "
//								+ " AND (unix_timestamp(Date('" +sysconfig.formatDate(selecteddatestart) + "')) <= unix_timestamp(Date(移除时间)) OR ISNULL(移除时间) ) "
//								
//								+ " UNION "
//								
//								+ "SELECT fg.股票代码, agu.股票名称, fg.股票权重 , fg.`风格板块` as 板块ID" 
//								+ " FROM 股票通达信风格板块对应表  AS fg,  A股  AS agu"
//								+ " WHERE  fg.风格板块= " + "'" + currentbkcode + "'" 
//								+ " AND fg.股票代码  = agu.股票代码" 
//								+ " AND unix_timestamp(Date('" + sysconfig.formatDate(selecteddateend) + "')) >= unix_timestamp(Date(加入时间)) "
//								+ " AND (unix_timestamp(Date('" +sysconfig.formatDate(selecteddatestart) + "')) <= unix_timestamp(Date(移除时间)) OR ISNULL(移除时间) ) "
//								
//								+ " UNION "
//
//								+ "SELECT zs.股票代码, agu.股票名称, zs.股票权重 , zs.`指数板块` as 板块ID" 
//								+ " FROM 股票通达信交易所指数对应表  AS zs,  A股  AS agu"
//								+ " WHERE  zs.指数板块= " + "'" + currentbkcode + "'" 
//								+ " AND zs.股票代码  = agu.股票代码" 
//								+ " AND unix_timestamp(Date('" + sysconfig.formatDate(selecteddateend) + "')) >= unix_timestamp(Date(加入时间)) "
//								+ " AND (unix_timestamp(Date('" +sysconfig.formatDate(selecteddatestart) + "')) <= unix_timestamp(Date(移除时间)) OR ISNULL(移除时间) ) "
//								
//								+ "GROUP BY  股票代码  ORDER BY 股票代码,股票名称 "
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
//				String tmpstockcode = rsfg.getString(1); //"股票代码"
//				String tmpstockname = rsfg.getString("股票名称"); //"股票名称"
//				int stockweight = rsfg.getInt("股票权重");
//				
//				System.out.println(tmpstockname+tmpstockcode);
//				Stock tmpstock = new Stock (tmpstockcode,tmpstockname);
//				
//				HashMap<String, Integer> stockbkweight = new  HashMap<String, Integer>() ;
//				stockbkweight.put(currentbkcode, stockweight); //板块代码加权重
//				tmpstock.setGeGuSuoShuBanKuaiWeight (  stockbkweight );
//				
//				gegumap.put(tmpstockcode, tmpstock);
//				
//			}
//			
//		}catch(java.lang.NullPointerException e){ 
//			e.printStackTrace();
////			System.out.println( "数据库连接为NULL!");
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
//	 * 计算某个股票在某个时间段内属于某个板块时候的成交量
//	 */
//	private double setStockChenJiaoE(String stockcode, String stockname, String currentbk, String currentbkcode,
//			Date selecteddatestart, Date selecteddateend) 
//	{
//		String formatedstartdate = sysconfig.formatDate(selecteddatestart);
//		String formatedenddate  = sysconfig.formatDate(selecteddateend);
//		String diyusqlquerystat;
//		if(stockcode.startsWith("6")) {
//			diyusqlquerystat =  "SELECT 通达信上交所股票每日交易信息.`代码` AS 股票代码, 股票通达信风格板块对应表.风格板块 AS 板块, sum(通达信上交所股票每日交易信息.`成交额`) AS 成交额"
//					+ "		from 通达信上交所股票每日交易信息 join"
//					+ "		     股票通达信风格板块对应表"
//					+ "		     on 通达信上交所股票每日交易信息.`代码` = 股票通达信风格板块对应表.`股票代码`"
//					+ "			  and 通达信上交所股票每日交易信息.`交易日期` >= 股票通达信风格板块对应表.`加入时间` and"
//					+ "		        (通达信上交所股票每日交易信息.`交易日期` <= 股票通达信风格板块对应表.`移除时间` or 股票通达信风格板块对应表.`移除时间` is null)"
//					+ "		where Date(通达信上交所股票每日交易信息.`交易日期`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(通达信上交所股票每日交易信息.`交易日期`) <= Date('" +  formatedenddate + "')"
//					+ "		and 股票通达信风格板块对应表.`风格板块` = '" + currentbkcode + "' and 通达信上交所股票每日交易信息.`代码` = '" + stockcode +"'"
//					+ "		group by 通达信上交所股票每日交易信息.`代码`, 股票通达信风格板块对应表.`风格板块`"
//					
//					+ " UNION "
//					
//					+ "SELECT 通达信上交所股票每日交易信息.`代码` AS 股票代码, 股票通达信行业板块对应表.行业板块 AS 板块, sum(通达信上交所股票每日交易信息.`成交额`) AS 成交额"
//					+ "		from 通达信上交所股票每日交易信息 join"
//					+ "		     股票通达信行业板块对应表"
//					+ "		     on 通达信上交所股票每日交易信息.`代码` = 股票通达信行业板块对应表.`股票代码`"
//					+ "			  and 通达信上交所股票每日交易信息.`交易日期` >= 股票通达信行业板块对应表.`加入时间` and"
//					+ "		        (通达信上交所股票每日交易信息.`交易日期` <= 股票通达信行业板块对应表.`移除时间` or 股票通达信行业板块对应表.`移除时间` is null)"
//					+ "		where Date(通达信上交所股票每日交易信息.`交易日期`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(通达信上交所股票每日交易信息.`交易日期`) <= Date('" +  formatedenddate + "')"
//					+ "		and 股票通达信行业板块对应表.`行业板块` = '" + currentbkcode + "' and 通达信上交所股票每日交易信息.`代码` = '" + stockcode +"'"
//					+ "		group by 通达信上交所股票每日交易信息.`代码`, 股票通达信行业板块对应表.`行业板块`"
//					
//					+ " UNION "
//					
//					+  "SELECT 通达信上交所股票每日交易信息.`代码` AS 股票代码, 股票通达信概念板块对应表.概念板块 AS 板块, sum(通达信上交所股票每日交易信息.`成交额`) AS 成交额"
//					+ "		from 通达信上交所股票每日交易信息 join"
//					+ "		     股票通达信概念板块对应表"
//					+ "		     on 通达信上交所股票每日交易信息.`代码` = 股票通达信概念板块对应表.`股票代码`"
//					+ "			  and 通达信上交所股票每日交易信息.`交易日期` >= 股票通达信概念板块对应表.`加入时间` and"
//					+ "		        (通达信上交所股票每日交易信息.`交易日期` <= 股票通达信概念板块对应表.`移除时间` or 股票通达信概念板块对应表.`移除时间` is null)"
//					+ "		where Date(通达信上交所股票每日交易信息.`交易日期`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(通达信上交所股票每日交易信息.`交易日期`) <= Date('" +  formatedenddate + "')"
//					+ "		and 股票通达信概念板块对应表.`概念板块` = '" + currentbkcode + "' and 通达信上交所股票每日交易信息.`代码` = '" + stockcode +"'"
//					+ "		group by 通达信上交所股票每日交易信息.`代码`, 股票通达信概念板块对应表.`概念板块`"
//					
//					+ " UNION "
//					
//					+  "SELECT 通达信上交所股票每日交易信息.`代码` AS 股票代码, 股票通达信交易所指数对应表.指数板块 AS 板块, sum(通达信上交所股票每日交易信息.`成交额`) AS 成交额"
//					+ "		from 通达信上交所股票每日交易信息 join"
//					+ "		     股票通达信交易所指数对应表"
//					+ "		     on 通达信上交所股票每日交易信息.`代码` = 股票通达信交易所指数对应表.`股票代码`"
//					+ "			  and 通达信上交所股票每日交易信息.`交易日期` >= 股票通达信交易所指数对应表.`加入时间` and"
//					+ "		        (通达信上交所股票每日交易信息.`交易日期` <= 股票通达信交易所指数对应表.`移除时间` or 股票通达信交易所指数对应表.`移除时间` is null)"
//					+ "		where Date(通达信上交所股票每日交易信息.`交易日期`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(通达信上交所股票每日交易信息.`交易日期`) <= Date('" +  formatedenddate + "')"
//					+ "		and 股票通达信交易所指数对应表.`指数板块` = '" + currentbkcode + "' and 通达信上交所股票每日交易信息.`代码` = '" + stockcode +"'"
//					
//					+ "		group by  股票代码,  板块"
//					
//					;
//			
//		} else {
//			diyusqlquerystat = 					 "SELECT 通达信深交所股票每日交易信息.`代码` AS 股票代码, 股票通达信风格板块对应表.风格板块 AS 板块, sum(通达信深交所股票每日交易信息.`成交额`) AS 成交额"
//					+ "		from 通达信深交所股票每日交易信息 join"
//					+ "		     股票通达信风格板块对应表"
//					+ "		     on 通达信深交所股票每日交易信息.`代码` = 股票通达信风格板块对应表.`股票代码`"
//					+ "			  and 通达信深交所股票每日交易信息.`交易日期` >= 股票通达信风格板块对应表.`加入时间` and"
//					+ "		        (通达信深交所股票每日交易信息.`交易日期` <= 股票通达信风格板块对应表.`移除时间` or 股票通达信风格板块对应表.`移除时间` is null)"
//					+ "		where Date(通达信深交所股票每日交易信息.`交易日期`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(通达信深交所股票每日交易信息.`交易日期`) <= Date('" +  formatedenddate + "')"
//					+ "		and 股票通达信风格板块对应表.`风格板块` = '" + currentbkcode + "' and 通达信深交所股票每日交易信息.`代码` = '" + stockcode +"'"
//					+ "		group by 通达信深交所股票每日交易信息.`代码`, 股票通达信风格板块对应表.`风格板块`"
//					
//					+ " UNION "
//					
//					+ "SELECT 通达信深交所股票每日交易信息.`代码` AS 股票代码, 股票通达信行业板块对应表.行业板块 AS 板块, sum(通达信深交所股票每日交易信息.`成交额`) AS 成交额"
//					+ "		from 通达信深交所股票每日交易信息 join"
//					+ "		     股票通达信行业板块对应表"
//					+ "		     on 通达信深交所股票每日交易信息.`代码` = 股票通达信行业板块对应表.`股票代码`"
//					+ "			  and 通达信深交所股票每日交易信息.`交易日期` >= 股票通达信行业板块对应表.`加入时间` and"
//					+ "		        (通达信深交所股票每日交易信息.`交易日期` <= 股票通达信行业板块对应表.`移除时间` or 股票通达信行业板块对应表.`移除时间` is null)"
//					+ "		where Date(通达信深交所股票每日交易信息.`交易日期`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(通达信深交所股票每日交易信息.`交易日期`) <= Date('" +  formatedenddate + "')"
//					+ "		and 股票通达信行业板块对应表.`行业板块` = '" + currentbkcode + "' and 通达信深交所股票每日交易信息.`代码` = '" + stockcode +"'"
//					+ "		group by 通达信深交所股票每日交易信息.`代码`, 股票通达信行业板块对应表.`行业板块`"
//					
//					+ " UNION "
//					
//					+  "SELECT 通达信深交所股票每日交易信息.`代码` AS 股票代码, 股票通达信概念板块对应表.概念板块 AS 板块, sum(通达信深交所股票每日交易信息.`成交额`) AS 成交额"
//					+ "		from 通达信深交所股票每日交易信息 join"
//					+ "		     股票通达信概念板块对应表"
//					+ "		     on 通达信深交所股票每日交易信息.`代码` = 股票通达信概念板块对应表.`股票代码`"
//					+ "			  and 通达信深交所股票每日交易信息.`交易日期` >= 股票通达信概念板块对应表.`加入时间` and"
//					+ "		        (通达信深交所股票每日交易信息.`交易日期` <= 股票通达信概念板块对应表.`移除时间` or 股票通达信概念板块对应表.`移除时间` is null)"
//					+ "		where Date(通达信深交所股票每日交易信息.`交易日期`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(通达信深交所股票每日交易信息.`交易日期`) <= Date('" +  formatedenddate + "')"
//					+ "		and 股票通达信概念板块对应表.`概念板块` = '" + currentbkcode + "' and 通达信深交所股票每日交易信息.`代码` = '" + stockcode +"'"
//					+ "		group by 通达信深交所股票每日交易信息.`代码`, 股票通达信概念板块对应表.`概念板块`"
//					
//					+ " UNION "
//					
//					+  "SELECT 通达信深交所股票每日交易信息.`代码` AS 股票代码, 股票通达信交易所指数对应表.指数板块 AS 板块, sum(通达信深交所股票每日交易信息.`成交额`) AS 成交额"
//					+ "		from 通达信深交所股票每日交易信息 join"
//					+ "		     股票通达信交易所指数对应表"
//					+ "		     on 通达信深交所股票每日交易信息.`代码` = 股票通达信交易所指数对应表.`股票代码`"
//					+ "			  and 通达信深交所股票每日交易信息.`交易日期` >= 股票通达信交易所指数对应表.`加入时间` and"
//					+ "		        (通达信深交所股票每日交易信息.`交易日期` <= 股票通达信交易所指数对应表.`移除时间` or 股票通达信交易所指数对应表.`移除时间` is null)"
//					+ "		where Date(通达信深交所股票每日交易信息.`交易日期`) >= Date('" + formatedstartdate + "') "
//					+ "           and Date(通达信深交所股票每日交易信息.`交易日期`) <= Date('" +  formatedenddate + "')"
//					+ "		and 股票通达信交易所指数对应表.`指数板块` = '" + currentbkcode + "' and 通达信深交所股票每日交易信息.`代码` = '" + stockcode +"'"
//					
//					+ "		group by  股票代码,  板块"
//					
//					;
//		}
//		System.out.println(diyusqlquerystat);
//		CachedRowSetImpl rsdy = null;
//		double dy = 0.0;
//		try {  
//			 rsdy = connectdb.sqlQueryStatExecute(diyusqlquerystat);
//			 while(rsdy.next()) {
//				 dy = rsdy.getDouble("成交额");
//				 System.out.println(dy);
//			 }
//		}catch(java.lang.NullPointerException e){ 
//			System.out.println( "数据库连接为NULL!");
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
	 * 找出某通达信行业板块的所有个股
	 */
//	private HashMap<String,String> getTDXHangYeBanKuaiGeGu(String currentbk) 
//	{
//		String sqlquerystat = "SELECT hy.股票代码, agu.股票名称" 
//				+ " FROM 股票通达信行业板块对应表  AS hy, A股 AS agu"
//				+ " WHERE  hy.行业板块= " + "'" + currentbk + "'" 
//				+ " AND hy.股票代码  = agu.股票代码" + "'"
//				+ " AND ISNULL(移除时间)"
//				//+ " GROUP BY  czjl.买卖账号, czjl.股票代码, agu.股票名称"
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
//				String tmpstockcode = rsfg.getString("股票代码");
//				String tmpstockname = rsfg.getString("股票名称");
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
		String sqlinsertstat = "INSERT INTO  产业链子板块列表(子板块名称,所属通达信板块,子板块代码) values ("
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
		String sqlinsertstat = "INSERT INTO  自定义板块列表(板块名称,创建时间,板块ID) values ("
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
			        	BanKuai tmpbk = new BanKuai (rs.getString("板块ID"),rs.getString("板块名称") );
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
					String sqlquerystat = "SELECT  MAX(交易日期) 	MOST_RECENT_TIME"
							+ " FROM 通达信板块每日交易信息  WHERE  代码 = " 
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
				
				setVolAmoRecordsFromFileToDatabase(tmpbkcode,tmpbkfile,ldlastestdbrecordsdate,"通达信板块每日交易信息",dateRule,tmprecordfile);
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
				String bkfilename = (filenamerule.replaceAll("YY", allsysbk.get(tmpbkcode).getSuoShuJiaoYiSuo().trim())).replaceAll("XXXXXX", tmpbkcode);
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
					String sqlquerystat1 = "SELECT MAX(交易日期) 	MOST_RECENT_TIME"
							+ " FROM 通达信交易所指数每日交易信息  WHERE  代码 = " 
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
				
				setVolAmoRecordsFromFileToDatabase(tmpbkcode,tmpbkfile,ldlastestdbrecordsdate,"通达信交易所指数每日交易信息",dateRule,tmprecordfile);
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
		if(lastestdbrecordsdate == null) //null说明数据库里面还没有相关的数据，把时间设置为1900年把文件中所有数据都导入
			try {
		        lastestdbrecordsdate = LocalDate.now().minus(100,ChronoUnit.YEARS);
				Files.append(tmpbkcode + "板块成交量记录将导入所有记录！"+ System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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

                        if( tmplinelist.size() ==7 && !tmplinelist.get(5).equals("0")) { //有可能是半天数据，有0，不完整，不能录入
                        	LocalDate curlinedate = null;
                    		try {
                    			String beforparsedate = tmplinelist.get(0);
                    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datarule);
                    			curlinedate =  LocalDate.parse(beforparsedate,formatter) ;
                    			
                    			if(curlinedate.isAfter(lastestdbrecordsdate)) {
                        			String sqlinsertstat = "INSERT INTO " + inserttablename +"(代码,交易日期,开盘价,最高价,最低价,收盘价,成交量,成交额) values ("
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
                        			Files.append(tmpbkcode + "指数成交量记录导入起始时间为:" + line + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        			Files.append(tmpbkcode + "共导入:" + lineimportcount + "个记录" + System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
                        		}
                    		} catch (Exception e) {
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
	 * 交易所定义的所有指数 用新的BkChanYeLianTreeNode存储
	 */
//	public HashMap<String, BkChanYeLianTreeNode> getTDXAllZhiShuList2() 
//	{
//		HashMap<String,BkChanYeLianTreeNode> tmpsysbankuailiebiaoinfo = new HashMap<String,BkChanYeLianTreeNode> ();
//
//		String sqlquerystat = "SELECT *  FROM 通达信交易所指数列表 WHERE 板块ID IS NOT NULL"
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
//	        	BkChanYeLianTreeNode tmpbk = new BanKuai (rs.getString("板块ID"),rs.getString("板块名称"));
//	        	//tmpbk.setBanKuaiJiaoYiSuo(rs.getString("指数所属交易所"));
//	        	tmpsysbankuailiebiaoinfo.put(rs.getString("板块ID"), tmpbk);
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
		String sqlquerystat = " SELECT MAX(数据最新更新时间) FROM 通达信板块列表" 
						   ;
		sqlstatmap.put("mysql", sqlquerystat);
			
			CachedRowSetImpl rs = null; 
		    try {  
		    	 rs = connectdb.sqlQueryStatExecute(sqlstatmap);
		    	 
		    	 java.sql.Timestamp tmplastmdftime = null;
		    	 while(rs.next())
		    		  tmplastmdftime = rs.getTimestamp("MAX(数据最新更新时间)");
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
		    
	   sqlquerystat = " SELECT MAX(数据最新更新时间) FROM 通达信交易所指数列表"   ;
		CachedRowSetImpl rszs = null; 
	    try {  
	    	 rszs = connectdb.sqlQueryStatExecute(sqlquerystat);
	    	 
	    	 java.sql.Timestamp tmplastmdftime = null;
	    	 while(rszs.next())
	    		  tmplastmdftime = rszs.getTimestamp("MAX(数据最新更新时间)");
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
//		rmtservertable.add("通达信交易所指数列表");
//		rmtservertable.add("通达信板块列表");
		List<String> rmtservertable = Lists.newArrayList("通达信板块列表", "通达信交易所指数列表");
		
		
			String sqlquerystat =  " show OPEN TABLES where In_use > 0" ;
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
	/*
	 * 
	 */
	private String getTdxBanKuaiNameByCode(String tmpbkcode) 
	{
		String sqlquerystatfrombk     = "SELECT 板块名称  FROM 通达信板块列表             WHERE 板块ID = " + tmpbkcode;
		String sqlquerystatfromzhishu = "SELECT 板块名称  FROM 通达信交易所指数列表   WHERE 板块ID = " + tmpbkcode;
		
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
	 * 增加板块新闻
	 */
	public boolean addBanKuaiNews (String bankuaiid,ChanYeLianNews newdetail)
	{
		int autoIncKeyFromApi = newdetail.getNewsId();
		String title = newdetail.getNewsTitle();
		LocalDate newdate = newdetail.getGenerateDate();
		String slackurl = newdetail.getNewsSlackUrl();
		String keywords = newdetail.getKeyWords ();
		
		if( autoIncKeyFromApi == -1) { //说明是新的NEWS sysconfig.formatDate( actionday )
			//先update商业新闻表
			String sqlinsertstat = "INSERT INTO 商业新闻(新闻标题,关键词,SLACK链接,录入日期,关联板块) values ("
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
			//如果板块id是不是已经在新闻的板块list里面了，已经加过了。
			String  updatestat = "UPDATE 商业新闻 "
								  + " SET 关联板块 = "
								  + " CASE WHEN 关联板块 like '%" + bankuaiid + "%' THEN"
								  + " CONCAT(关联板块,  ' ') " 
								  + " ELSE " 
								  + " CONCAT(关联板块,  '"+ bankuaiid + "|' )"
								  +	" END" 
								  + " WHERE ID=" +  autoIncKeyFromApi
								  ;
			System.out.println(updatestat);
			connectdb.sqlUpdateStatExecute (updatestat);
		}
		
		
		//再把新闻id update到板块表或者指数表
//		boolean inzhishutable = false;
//		String checkstat = "SELECT COUNT(1) FROM 通达信交易所指数列表 WHERE 板块ID = '" + bankuaiid + "'";
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
//	    	updatestat = "UPDATE 通达信交易所指数列表"  
//	    				+ "  SET 相关新闻 ="
//	    				+ " CASE WHEN 相关新闻 IS NULL THEN "
//	    				+  "'" + autoIncKeyFromApi + "|'"
//	    				+ "  ELSE "
//	    				+ "  CONCAT(相关新闻," + "'" + autoIncKeyFromApi + "|')" 
//	    				+ " END "
//	    				+ " WHERE 板块ID='" +  bankuaiid.trim() + "'"
//	    				;
//	     else
//	    	updatestat = "UPDATE 通达信板块列表"  
//	    				+ "  SET 相关新闻 ="
//	    				+ " CASE WHEN 相关新闻 IS NULL THEN "
//	    				+  "'" + autoIncKeyFromApi + "|'"
//	    				+ "  ELSE "
//	    				+ "  CONCAT(相关新闻," + "'" + autoIncKeyFromApi + "|')" 
//	    				+ " END "
//	    				+ " WHERE 板块ID='" +  bankuaiid.trim() + "'"
//	    				;
//	    
//		System.out.println(updatestat);
//		connectdb.sqlUpdateStatExecute (updatestat);
		
		return true;
	}

	public void deleteBanKuaiNews(String myowncode, ChanYeLianNews aseltnews)
	{
		int newsid = aseltnews.getNewsId();
		
		if("ALL".equals(myowncode.toUpperCase())) { //删除该新闻
			String deletestat = "DELETE  FROM 商业新闻 WHERE ID=" + newsid;
			
			System.out.println(deletestat);
			connectdb.sqlUpdateStatExecute (deletestat);
			
//			updatestat = "UPDATE 通达信板块列表  SET 相关新闻 = REPLACE (相关新闻, '" + String.valueOf(newsid).trim() + "|', ' ' ) WHERE 板块ID='" + myowncode + "'";
//			System.out.println(updatestat);
//			connectdb.sqlUpdateStatExecute (updatestat);
//			
//			updatestat = "UPDATE 通达信交易所指数列表  SET 相关新闻 = REPLACE (相关新闻, '" + String.valueOf(newsid).trim() + "|', ' ' ) WHERE 板块ID='" + myowncode + "'";
//			System.out.println(updatestat);
//			connectdb.sqlUpdateStatExecute (updatestat);
		} else { //删除在所有板块里的新闻
			String updatestat = "UPDATE 商业新闻  SET 关联板块 = REPLACE (关联板块, '" + myowncode.trim() + "|', ' ' ) WHERE ID=" + newsid;
			System.out.println(updatestat);
			connectdb.sqlDeleteStatExecute(updatestat);
			
//			String  updatestat = "UPDATE 通达信板块列表 "
//					  + " SET 相关新闻 = "
//					  + " CASE WHEN 关联板块 like '%" + String.valueOf(newsid)+"|"  + "%' THEN"
//					  + " CONCAT(关联板块,  ' ') " 
//					  + " ELSE " 
//					  + " CONCAT(关联板块,  '"+ bankuaiid + "|' )"
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
			String sqlquerystat = "SELECT * FROM 商业新闻   WHERE 录入日期 >= DATE(NOW()) - INTERVAL 40 DAY ORDER BY  录入日期 DESC"
									;
			CachedRowSetImpl rs = null; 
		    try {  
		    	System.out.println(sqlquerystat);
		    	 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	 
		    	 while(rs.next()) {
		    		 ChanYeLianNews cylnew = new ChanYeLianNews ();
		    		 int newsid = rs.getInt("id");
		    		 java.util.Date selectednew = rs.getDate("录入日期");
		    		 String newtitle = rs.getString("新闻标题");
		    		 String keywords = rs.getString("关键词");
		    		 String slack = rs.getString("SLACK链接");
		    		 String relatedbk = rs.getString("关联板块");

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
		
		//单个板块/个股新闻
		String sqlquerystat = "SELECT * FROM 商业新闻   WHERE 关联板块 like '%" + bankuaiid.trim()+"|"  + "%' ORDER BY  录入日期 DESC";
		CachedRowSetImpl rs = null;
		try {
			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
   		 	while(rs.next()) {
	   		 	ChanYeLianNews cylnew = new ChanYeLianNews ();
	   			 int newsid = rs.getInt("id");
	   			 java.util.Date selectednew = rs.getDate("录入日期");
		    	 String newtitle = rs.getString("新闻标题");
		    	 String keywords = rs.getString("关键词");
		    	 String slack = rs.getString("SLACK链接");
		    		 
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
		
//		String sqlquerystat = "SELECT 相关新闻 FROM 通达信板块列表   WHERE 板块ID =  " + bankuaiid
//							   + " UNION "
//							   + "SELECT 相关新闻  FROM 通达信交易所指数列表   WHERE 板块ID =  " + bankuaiid
//							   ;
//		
//		CachedRowSetImpl rs = null; 
//	    try {
//	    	List<String> tmpbknewslist = null;
//	    	System.out.println(sqlquerystat);
//	    	 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//	    	 while(rs.next()) {
//	    		 String tmpnewids = rs.getString("相关新闻");
//	    		 tmpbknewslist = Splitter.on("|").omitEmptyStrings().splitToList(tmpnewids); //
//	    	 }
//	    	 
//	    	 for(String newid:tmpbknewslist) {
//	    		 ChanYeLianNews cylnew = new ChanYeLianNews ();
//	    		 String sqlquerystat2 = "SELECT * FROM 商业新闻   WHERE ID = '" + newid.trim().substring(2, newid.trim().length()) + "'"; //个股新闻对应的ID是ID+ID号，因为后面替换时候防止出现ID号|重复的情况
//	    		 rs = connectdb.sqlQueryStatExecute(sqlquerystat2);
//	    		 while(rs.next()) {
//	    			 int newsid = rs.getInt("id");
//	    			 Date selectednew = rs.getDate("录入日期");
//		    		 String newtitle = rs.getString("新闻标题");
//		    		 String keywords = rs.getString("关键词");
//		    		 String slack = rs.getString("SLACK链接");
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
	 * 判断板块是什么类型的板块，概念/风格/指数/行业/地域
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
						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM 股票通达信概念板块对应表  WHERE 板块代码='" + bkcode + "'";
						System.out.println(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							 dy = rs.getInt("RESULT");
	//						 System.out.println(dy);
						 }
					}catch(java.lang.NullPointerException e){ 
						System.out.println( "数据库连接为NULL!");
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
							stockvsbktable =  "股票通达信概念板块对应表";
					}
				}
				if(stockvsbktable == null) {
					CachedRowSetImpl rs = null;
					int dy = -1;
					try {
						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM 股票通达信风格板块对应表 WHERE 板块代码='" + bkcode + "'";
						System.out.println(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							  dy = rs.getInt("RESULT");
//							 System.out.println(dy1);
						 }
					}catch(java.lang.NullPointerException e){ 
						System.out.println( "数据库连接为NULL!");
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
							stockvsbktable =  "股票通达信风格板块对应表";
					}
				}
				
				if(stockvsbktable == null) {
					CachedRowSetImpl rs = null;
					int dy = -1;
					try {
						String sqlquerystat = "SELECT COUNT(*)  RESULT FROM 股票通达信行业板块对应表  WHERE 板块代码='" + bkcode + "'";
						System.out.println(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							 dy = rs.getInt("RESULT");
//							 System.out.println(dy);
						 }
					}catch(java.lang.NullPointerException e){ 
						System.out.println( "数据库连接为NULL!");
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
							stockvsbktable = "股票通达信行业板块对应表";
					}
				}
				
				if(stockvsbktable == null) {
					CachedRowSetImpl rs = null;
					int dy = -1;
					try {
						String sqlquerystat = "SELECT COUNT(*) AS RESULT FROM 股票通达信交易所指数对应表  WHERE 板块代码='" + bkcode + "'";
						System.out.println(sqlquerystat);
						 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
						 while(rs.next()) {
							 dy = rs.getInt("RESULT");
//							 System.out.println(dy);
						 }
					}catch(java.lang.NullPointerException e) {
						e.printStackTrace();
						System.out.println( "数据库连接为NULL!");
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
							stockvsbktable = "股票通达信交易所指数对应表";
					}
				}
				try {
					CachedRowSetImpl rsdy = null;
					int dy = -1;
					try {
						String diyusqlquerystat = "SELECT 对应TDXSWID  FROM 通达信板块列表 WHERE 板块id = '" + bkcode + "'" ;
						System.out.println(diyusqlquerystat);
						 rsdy = connectdb.sqlQueryStatExecute(diyusqlquerystat);
						 while(rsdy.next()) {
							 dy = rsdy.getInt("对应TDXSWID");
//							 System.out.println(dy);
						 }
					}catch(java.lang.NullPointerException e){ 
						System.out.println( "数据库连接为NULL!");
					} catch (SQLException e) {
//						e.printStackTrace();
						System.out.println("java.sql.SQLException: 对列 1 中的值 (芯片) 执行 getInt 失败，不是地域板块。");
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
							stockvsbktable =  "股票通达信基本面信息对应表";
							actiontables.put("地域代码", String.valueOf(dy) );
						}
					}
				} catch (java.lang.NullPointerException e1) {
					
				}
				if(stockvsbktable != null)
					actiontables.put("股票板块对应表", stockvsbktable);
				
				String bkcjltable = null;
				String bknametable = null;
				if(bkcjltable == null) {
					boolean inzhishutable = false;
					int result = -1;
					CachedRowSetImpl rspd = null;
					try {
						String sqlcheckstat = "SELECT EXISTS(SELECT * FROM 通达信板块每日交易信息 where 代码 = '" + bkcode + "') AS result";
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
			   		 		bkcjltable ="通达信板块每日交易信息";
			   		 		bknametable = "通达信板块列表";
			   		 	}
				    }
				}
				if(bkcjltable == null) {
					boolean inzhishutable = false;
					int result = -1;
					CachedRowSetImpl rspd = null;
					try {
						String sqlcheckstat = "SELECT EXISTS(SELECT * FROM 通达信交易所指数每日交易信息 where 代码 = '" + bkcode + "') AS result";
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
			   		 	bkcjltable ="通达信交易所指数每日交易信息";
			   		 	bknametable = "通达信交易所指数列表";
				    }
				}
				
				if(bkcjltable != null) {
					actiontables.put("板块每日交易量表", bkcjltable);
				    actiontables.put("板块指数名称表", bknametable);
				}
		}
		if(!Strings.isNullOrEmpty(stockcode)) {
				String gegucjltable;
				// TODO Auto-generated method stub
				if(stockcode.startsWith("6"))
					gegucjltable = "通达信上交所股票每日交易信息";
				else
					gegucjltable = "通达信深交所股票每日交易信息";
				actiontables.put("股票每日交易量表", gegucjltable);
		}
		
		return actiontables;
	}
	/*
	 * 找出某个时间点 行业/概念/风格/指数 通达信某个板块的所有个股及权重,不找成交额
	 */
	public BanKuai getTDXBanKuaiGeGuOfHyGnFg(BanKuai currentbk,LocalDate selecteddatestart , LocalDate selecteddateend)
	{
		String currentbkcode = currentbk.getMyOwnCode();
		
		HashMap<String, String> actiontables = this.getActionRelatedTables(currentbkcode,null);
//		System.out.println(actiontables);
		String bktypetable = actiontables.get("股票板块对应表");
		String bknametable = actiontables.get("板块指数名称表");
		if(bktypetable == null) {
			return currentbk;
		}
		
		String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart).trim();
		String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend).trim();

		ArrayList<Stock> gegumap = new ArrayList<Stock> ();
		
		CachedRowSetImpl rs1 = null;
		try { 
			String sqlquerystat1 = "";
			if(bktypetable.trim().equals("股票通达信基本面信息对应表") ) { //找地域板块
				String dycode = actiontables.get("地域代码");
				sqlquerystat1 = "SELECT a股.`股票代码` , a股.`股票名称` ,10 as '股票权重' \r\n" + 
				 		"FROM 股票通达信基本面信息对应表, a股\r\n" + 
				 		"WHERE 股票通达信基本面信息对应表.`地域DY` = " + dycode + "\r\n" + 
				 		"AND 股票通达信基本面信息对应表.`股票代码GPDM` = a股.`股票代码`"
				 		;
			 } else { //概念风格行业指数板块
				 //from WQW
//				 sqlquerystat1 = "select "+ bktypetable + ".`股票代码` , a股.`股票名称`, "+ bktypetable + ".`板块代码` , "+ bktypetable + ".`股票权重`\r\n" + 
//				 		"          from "+ bktypetable + ", a股\r\n" + 
//				 		"          where "+ bktypetable + ".`股票代码`  = a股.`股票代码`\r\n" + 
//				 		"           and '" +  formatedstartdate + "' >= Date("+ bktypetable + ".`加入时间`)\r\n" + 
//				 		"           and '" +  formatedenddate + "' <  ifnull("+ bktypetable + ".`移除时间`, '2099-12-31')\r\n" + 
//				 		"           and "+ bktypetable + ".`板块代码` =  '" + currentbkcode + "'\r\n" + 
//				 		"         \r\n" + 
//				 		"         group by "+ bktypetable + ".`股票代码`, "+ bktypetable + ".`板块代码`\r\n" + 
//				 		"         order by "+ bktypetable + ".`股票代码`, "+ bktypetable + ".`板块代码` ";
				 sqlquerystat1 = "select "+ bktypetable + ".`股票代码` , a股.`股票名称`, "+ bktypetable + ".`板块代码` , "+ bktypetable + ".`股票权重`\r\n" + 
					 		"          from "+ bktypetable + ", a股\r\n" + 
					 		"          where "+ bktypetable + ".`股票代码`  = a股.`股票代码`\r\n" + 
					 		
					 		"and (  (  Date("+ bktypetable + ".`加入时间`) between '" +  formatedstartdate + "'  and '" +  formatedenddate + "')\r\n" + 
					 		"           		 or( ifnull("+ bktypetable + ".`移除时间`, '2099-12-31') between '" +  formatedstartdate + "'  and '" +  formatedenddate + "')\r\n" + 
					 		"           		 or( Date("+ bktypetable + ".`加入时间`) <= '" +  formatedstartdate + "' and ifnull(" + bktypetable + ".`移除时间`, '2099-12-31') >= '" +  formatedenddate + "' )\r\n" + 
					 		"			  )" +
//					 		"           and '" +  formatedstartdate + "' >= Date("+ bktypetable + ".`加入时间`)\r\n" + 
//					 		"           and '" +  formatedenddate + "' <  ifnull("+ bktypetable + ".`移除时间`, '2099-12-31')\r\n" + 
					 		
					 		
					 		"           and "+ bktypetable + ".`板块代码` =  '" + currentbkcode + "'\r\n" + 
					 		"         \r\n" + 
					 		"         group by "+ bktypetable + ".`股票代码`, "+ bktypetable + ".`板块代码`\r\n" + 
					 		"         order by "+ bktypetable + ".`股票代码`, "+ bktypetable + ".`板块代码` ";
			 }
//			 System.out.println(sqlquerystat1);
			 System.out.println("给板块" + currentbk.getMyOwnCode() + currentbk.getMyOwnName() + "寻找从" + formatedstartdate.toString() + "到" + formatedenddate.toString() + "时间段内的个股！");
			 rs1 = connectdb.sqlQueryStatExecute(sqlquerystat1);
//			  ResultSetMetaData rsmd = rs1.getMetaData();
//			 String name = rsmd.getColumnName(3);
//			 String name2 = rsmd.getColumnName(2);
//			 String name1 = rsmd.getColumnName(1);
			 
			 while(rs1.next()) {  
				 
				String tmpstockcode = rs1.getString("股票代码");
				
				String tmpstockname;
				try {
					tmpstockname = rs1.getString("股票名称"); //"股票名称"
				} catch (java.lang.NullPointerException e ) {
					tmpstockname = "";
				}
				
				Stock tmpstock = new Stock (tmpstockcode,tmpstockname);
				
				Integer weight = rs1.getInt("股票权重");
				HashMap<String,Integer>  tmpweight = new HashMap<String,Integer> () ;
				tmpweight.put(currentbkcode, weight);
				tmpstock.setGeGuSuoShuBanKuaiWeight(tmpweight);

				currentbk.addNewBanKuaiGeGu(tmpstock);
			}
				
			}catch(java.lang.NullPointerException e){ 
				e.printStackTrace();
//				System.out.println( "数据库连接为NULL!");
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
	 * 找出某个时间点 行业/概念/风格/指数 通达信某个板块的所有个股及权重和成交额，这个函数一般用于找一周内的情况
	 */
	public HashMap<String, Stock> getTDXBanKuaiGeGuOfHyGnFgAndChenJiaoLIang(String currentbk,String currentbkcode, LocalDate selecteddatestart , LocalDate selecteddateend)
	{
//		if(currentbkcode.equals("000959"))
//			System.out.println("000959 arrived");
		
			HashMap<String, String> actiontables = this.getActionRelatedTables(currentbkcode,null);
//			System.out.println(actiontables);
			String bktypetable = actiontables.get("股票板块对应表");
			String bkorzsvoltable = actiontables.get("板块每日交易量表");
			String bknametable = actiontables.get("板块指数名称表");
			if(bktypetable == null) {
				return null;
			}
			
			String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart);
			String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend);

			HashMap<String,Stock> gegumap = new HashMap<String,Stock> ();
			
			CachedRowSetImpl rs1 = null;
			try { 
				String sqlquerystat1 = "";
				if(bktypetable.trim().equals("股票通达信基本面信息对应表") ) { //找地域板块
					String dycode = actiontables.get("地域代码");
					 sqlquerystat1 = "SELECT A.`板块代码`, A.`板块名称`,A.category_amount, B.`股票代码`, \"\" AS 股票名称, B.stock_amount,   10 AS 股票权重,\r\n" + 
					 		"       B.stock_amount / A.category_amount ratio, '" + formatedenddate +"' as lastdayofweek  \r\n" + 
					 		"from \r\n" + 
					 		"( SELECT 股票通达信基本面信息对应表.`股票代码GPDM` AS 股票代码, 股票通达信基本面信息对应表.`地域DY`, sum(通达信上交所股票每日交易信息.`成交额`)  stock_amount\r\n" + 
					 		"from 股票通达信基本面信息对应表 , 通达信上交所股票每日交易信息\r\n" + 
					 		"where 股票通达信基本面信息对应表.`股票代码GPDM`  = 通达信上交所股票每日交易信息.`代码`\r\n" + 
					 		"and 通达信上交所股票每日交易信息.`交易日期` between ' " + formatedstartdate + "' and '" + formatedenddate + "'\r\n" + 
					 		"and 股票通达信基本面信息对应表.`地域DY` = '"+ dycode + "'\r\n" + 
					 		"group by 股票通达信基本面信息对应表.`股票代码GPDM`, 股票通达信基本面信息对应表.`地域DY`\r\n" + 
					 		"order by 股票通达信基本面信息对应表.`股票代码GPDM`, 股票通达信基本面信息对应表.`地域DY` \r\n" + 
					 		") B ,\r\n" + 
					 		"( select 通达信板块每日交易信息.`代码` AS 板块代码  , 通达信板块列表.`板块名称`, 通达信板块列表.`对应TDXSWID` AS `地域DY`,  sum(通达信板块每日交易信息.`成交额`) category_amount\r\n" + 
					 		"          from 通达信板块每日交易信息, 通达信板块列表\r\n" + 
					 		"         where 通达信板块每日交易信息.`代码` =  通达信板块列表.`板块ID`\r\n" + 
					 		"			  and 通达信板块每日交易信息.`交易日期` between ' " + formatedstartdate + "' and '" + formatedenddate + "'\r\n" + 
					 		"           and 通达信板块每日交易信息.`代码` =  '" + currentbkcode + "'\r\n" + 
					 		"         group by 通达信板块每日交易信息.`代码`\r\n" + 
					 		"         order by 通达信板块每日交易信息.`代码`\r\n" + 
					 		") A \r\n" + 
					 		"WHERE B.`地域DY` = A.`地域DY`"
					 		; 
					 		
				 } else { //概念风格行业指数板块
					 //from WQW
					 sqlquerystat1 =" select A.`板块代码`, B.`板块名称`,B.category_amount, A.`股票代码`,  A.`股票名称`,A.stock_amount,   A.`股票权重`,\r\n" + 
						 		"       \r\n" + 
						 		"       A.stock_amount / B.category_amount ratio, '" + formatedenddate + "' as lastdayofweek" + 
						 		"  from (select  X.`股票代码`,a股.`股票名称`, X.`板块代码` ,X.stock_amount,X.`股票权重`\r\n" + 
						 		"FROM \r\n" + 
						 		"       (select " +  bktypetable +  ".`股票代码` , " +  bktypetable +  ".`板块代码` , sum(通达信上交所股票每日交易信息.`成交额`) stock_amount," +  bktypetable +  ".`股票权重`\r\n" + 
						 		"          from " +  bktypetable +  ", 通达信上交所股票每日交易信息\r\n" + 
						 		"         where " +  bktypetable +  ".`股票代码`   = 通达信上交所股票每日交易信息.`代码`\r\n" + 
						 		"           and Date(通达信上交所股票每日交易信息.`交易日期`) >= Date(" +  bktypetable +  ".`加入时间`)\r\n" + 
						 		"           and 通达信上交所股票每日交易信息.`交易日期` <  ifnull(" +  bktypetable +  ".`移除时间`, '2099-12-31')\r\n" + 
						 		"           and (通达信上交所股票每日交易信息.`交易日期` between '" +  formatedstartdate +  "' and '" +  formatedenddate +  "')\r\n" + 
						 		"           and " +  bktypetable +  ".`板块代码` =  '" +  currentbkcode +  "'\r\n" + 
						 		"         \r\n" + 
						 		"         group by " +  bktypetable +  ".`股票代码`, " +  bktypetable +  ".`板块代码`\r\n" + 
						 		"         order by " +  bktypetable +  ".`股票代码`, " +  bktypetable +  ".`板块代码` ) X,\r\n" + 
						 		"         a股\r\n" + 
						 		"where  X.`股票代码` = a股.`股票代码`\r\n" + 
						 		"order by X.`股票代码`,a股.`股票名称`) A,\r\n" + 
						 		"         \r\n" + 
						 		"       (select " + bkorzsvoltable + ".`代码` , " + bknametable + ".`板块名称`,   sum(" + bkorzsvoltable + ".`成交额`) category_amount\r\n" + 
						 		"          from " + bkorzsvoltable + ", " + bknametable + "\r\n" + 
						 		"         where " + bkorzsvoltable + ".`代码` =  " + bknametable + ".`板块ID`\r\n" + 
						 		"			  and " + bkorzsvoltable + ".`交易日期` >= '" +  formatedstartdate +  "'\r\n" + 
						 		"           and " + bkorzsvoltable + ".`交易日期` <= '" +  formatedenddate +  "'\r\n" + 
						 		"           and " + bkorzsvoltable + ".`代码` =  '" +  currentbkcode +  "'\r\n" + 
						 		"         group by " + bkorzsvoltable + ".`代码`\r\n" + 
						 		"         order by " + bkorzsvoltable + ".`代码`) B\r\n" + 
						 		" where A.`板块代码`   = B.`代码`\r\n" + 
						 		"\r\n" + 
						 		"  order by A.`板块代码`,  A.`股票代码`;"
								;
					 
				 }
				 System.out.println(sqlquerystat1);
				 rs1 = connectdb.sqlQueryStatExecute(sqlquerystat1);

				 while(rs1.next()) {  
					String tmpstockcode = rs1.getString("股票代码");
					String tmpstockname;
					try {
						tmpstockname = rs1.getString("股票名称"); //"股票名称"
					} catch (java.lang.NullPointerException e ) {
						tmpstockname = "";
					}
					
					Stock tmpstock = new Stock (tmpstockcode,tmpstockname);
					
					Integer weight = rs1.getInt("股票权重");
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
//					System.out.println( "数据库连接为NULL!");
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
					if(bktypetable.trim().equals("股票通达信基本面信息对应表") ) { 
						String dycode = actiontables.get("地域代码");
						sqlquerystat2 = "SELECT A.`板块代码`, A.`板块名称`,A.category_amount, B.`股票代码`, \"\" AS 股票名称, B.stock_amount,   10 AS 股票权重,\r\n" + 
						 		"       B.stock_amount / A.category_amount ratio, '" + formatedenddate +"' as lastdayofweek  \r\n" + 
						 		"from \r\n" + 
						 		"( SELECT 股票通达信基本面信息对应表.`股票代码GPDM` AS 股票代码, 股票通达信基本面信息对应表.`地域DY`, sum(通达信深交所股票每日交易信息.`成交额`)  stock_amount\r\n" + 
						 		"from 股票通达信基本面信息对应表 , 通达信深交所股票每日交易信息\r\n" + 
						 		"where 股票通达信基本面信息对应表.`股票代码GPDM`  = 通达信深交所股票每日交易信息.`代码`\r\n" + 
						 		"and 通达信深交所股票每日交易信息.`交易日期` between ' " + formatedstartdate + "' and '" + formatedenddate + "'\r\n" + 
						 		"and 股票通达信基本面信息对应表.`地域DY` = '"+ dycode + "'\r\n" + 
						 		"group by 股票通达信基本面信息对应表.`股票代码GPDM`, 股票通达信基本面信息对应表.`地域DY`\r\n" + 
						 		"order by 股票通达信基本面信息对应表.`股票代码GPDM`, 股票通达信基本面信息对应表.`地域DY` \r\n" + 
						 		") B ,\r\n" + 
						 		"( select 通达信板块每日交易信息.`代码` AS 板块代码  , 通达信板块列表.`板块名称`, 通达信板块列表.`对应TDXSWID` AS `地域DY`,  sum(通达信板块每日交易信息.`成交额`) category_amount\r\n" + 
						 		"          from 通达信板块每日交易信息, 通达信板块列表\r\n" + 
						 		"         where 通达信板块每日交易信息.`代码` =  通达信板块列表.`板块ID`\r\n" + 
						 		"			  and 通达信板块每日交易信息.`交易日期` between ' " + formatedstartdate + "' and '" + formatedenddate + "'\r\n" + 
						 		"           and 通达信板块每日交易信息.`代码` =  '" + currentbkcode + "'\r\n" + 
						 		"         group by 通达信板块每日交易信息.`代码`\r\n" + 
						 		"         order by 通达信板块每日交易信息.`代码`\r\n" + 
						 		") A \r\n" + 
						 		"WHERE B.`地域DY` = A.`地域DY`"
						 		;  
//									;
					 } else { //概念风格行业指数板块
						 //from WQW
						 sqlquerystat2 =" select A.`板块代码`, B.`板块名称`,B.category_amount, A.`股票代码`,  A.`股票名称`,A.stock_amount,   A.`股票权重`,\r\n" + 
							 		"       \r\n" + 
							 		"       A.stock_amount / B.category_amount ratio, '" + formatedenddate + "' as lastdayofweek" + 
							 		"  from (select  X.`股票代码`,a股.`股票名称`, X.`板块代码` ,X.stock_amount,X.`股票权重`\r\n" + 
							 		"FROM \r\n" + 
							 		"       (select " +  bktypetable +  ".`股票代码` , " +  bktypetable +  ".`板块代码` , sum(通达信深交所股票每日交易信息.`成交额`) stock_amount," +  bktypetable +  ".`股票权重`\r\n" + 
							 		"          from " +  bktypetable +  ", 通达信深交所股票每日交易信息\r\n" + 
							 		"         where " +  bktypetable +  ".`股票代码`   = 通达信深交所股票每日交易信息.`代码`\r\n" + 
							 		"           and Date(通达信深交所股票每日交易信息.`交易日期`) >= Date(" +  bktypetable +  ".`加入时间`)\r\n" + 
							 		"           and 通达信深交所股票每日交易信息.`交易日期` <  ifnull(" +  bktypetable +  ".`移除时间`, '2099-12-31')\r\n" + 
							 		"           and (通达信深交所股票每日交易信息.`交易日期` between '" +  formatedstartdate +  "' and '" +  formatedenddate +  "')\r\n" + 
							 		"           and " +  bktypetable +  ".`板块代码` =  '" +  currentbkcode +  "'\r\n" + 
							 		"         \r\n" + 
							 		"         group by " +  bktypetable +  ".`股票代码`, " +  bktypetable +  ".`板块代码`\r\n" + 
							 		"         order by " +  bktypetable +  ".`股票代码`, " +  bktypetable +  ".`板块代码` ) X,\r\n" + 
							 		"         a股\r\n" + 
							 		"where  X.`股票代码` = a股.`股票代码`\r\n" + 
							 		"order by X.`股票代码`,a股.`股票名称`) A,\r\n" + 
							 		"         \r\n" + 
							 		"       (select " + bkorzsvoltable + ".`代码` , " + bknametable + ".`板块名称`,   sum(" + bkorzsvoltable + ".`成交额`) category_amount\r\n" + 
							 		"          from " + bkorzsvoltable + ", " + bknametable + "\r\n" + 
							 		"         where " + bkorzsvoltable + ".`代码` =  " + bknametable + ".`板块ID`\r\n" + 
							 		"			  and " + bkorzsvoltable + ".`交易日期` >= '" +  formatedstartdate +  "'\r\n" + 
							 		"           and " + bkorzsvoltable + ".`交易日期` <= '" +  formatedenddate +  "'\r\n" + 
							 		"           and " + bkorzsvoltable + ".`代码` =  '" +  currentbkcode +  "'\r\n" + 
							 		"         group by " + bkorzsvoltable + ".`代码`\r\n" + 
							 		"         order by " + bkorzsvoltable + ".`代码`) B\r\n" + 
							 		" where A.`板块代码`   = B.`代码`\r\n" + 
							 		"\r\n" + 
							 		"  order by A.`板块代码`,  A.`股票代码`;"
									;
					 }
					 System.out.println(sqlquerystat2);
					 rs2 = connectdb.sqlQueryStatExecute(sqlquerystat2);
					 while(rs2.next()) {  
						String tmpstockcode = rs2.getString("股票代码");
						String tmpstockname;
						try {
							tmpstockname = rs2.getString("股票名称"); //"股票名称"
						} catch (java.lang.NullPointerException e ) {
							tmpstockname = "";
						}
						Stock tmpstock = new Stock (tmpstockcode,tmpstockname);
						
						Integer weight = rs2.getInt("股票权重");
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
	//					System.out.println( "数据库连接为NULL!");
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
	 * 板块设定时间跨度内和大盘相比的每周占比
	 */
	public  BanKuai getBanKuaiZhanBi (BanKuai bankuai,LocalDate selecteddatestart,LocalDate selecteddateend,LocalDate addposition)
	{
		String bkcode = bankuai.getMyOwnCode();
		HashMap<String, String> actiontables = this.getActionRelatedTables(bkcode,null);
		String bkcjltable = actiontables.get("板块每日交易量表");
		if(bkcjltable == null) //说明该板块没有成交量记录，不需要记录占比
			return bankuai;
		
		String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart);
		String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend);
			
		String sqlquerystat = null;
		if(!bkcjltable.equals("通达信交易所指数每日交易信息")) { 
			sqlquerystat = "SELECT Year(BKJYE.交易日期) AS CALYEAR , WEEK(BKJYE.交易日期) AS CALWEEK, BKJYE.`代码` AS BKCODE , BKJYE.交易日期, DATE(BKJYE.交易日期 + INTERVAL (6 - DAYOFWEEK(BKJYE.交易日期)) DAY) EndOfWeekDate,\r\n"
					+ "	       SUM(BKJYE.成交额)/2 AS 板块周交易额, SUM(ZSJYE.成交额) AS 大盘周交易额, SUM(BKJYE.`成交额`)/(2*SUM(ZSJYE.`成交额`)) AS 占比 \r\n"
					+ "	FROM  通达信板块每日交易信息 BKJYE \r\n"
					+ "	INNER JOIN 通达信交易所指数每日交易信息  ZSJYE \r\n"
					+ "	ON ZSJYE.代码 in ('999999','399001' ) \r\n"
					+ "	       AND BKJYE.`交易日期` = ZSJYE.`交易日期` \r\n"
					+ "	       AND BKJYE.`交易日期` BETWEEN '" + formatedstartdate + "' AND'" + formatedenddate + "' \r\n"
					+ "	       AND BKJYE.`代码` = '" + bkcode + "' \r\n"
					+ "			GROUP BY Year(BKJYE.`交易日期`),WEEK(BKJYE.`交易日期`),BKJYE.`代码` \r\n"
					;
			
		} else { //板块是指数板块,变成了在同表内的查询
			sqlquerystat = "SELECT Year(通达信交易所指数每日交易信息.交易日期) AS CALYEAR , WEEK(通达信交易所指数每日交易信息.交易日期) AS CALWEEK, 通达信交易所指数每日交易信息.`代码` AS BKCODE , \r\n"
					+ "通达信交易所指数每日交易信息.交易日期, DATE(通达信交易所指数每日交易信息.交易日期 + INTERVAL (6 - DAYOFWEEK(通达信交易所指数每日交易信息.交易日期)) DAY) EndOfWeekDate, \r\n"
					+ "	sum(通达信交易所指数每日交易信息.成交额) AS 板块周交易额,	sum(sum_dapan) AS 大盘周交易额, sum(通达信交易所指数每日交易信息.成交额) / sum(sum_dapan) AS 占比 \r\n"
					+ " FROM 通达信交易所指数每日交易信息 , ( \r\n"
					+ "    SELECT 通达信交易所指数每日交易信息.交易日期 dapanjyrq,  SUM(通达信交易所指数每日交易信息.成交额) sum_dapan \r\n"
					+ "    FROM 通达信交易所指数每日交易信息   \r\n"
					+ "    where  通达信交易所指数每日交易信息.`代码` IN  ('999999','399001' )  \r\n"
					+ "    group by dapanjyrq \r\n"
					+ ") sq \r\n"
					+ " where  通达信交易所指数每日交易信息.交易日期 = dapanjyrq AND 通达信交易所指数每日交易信息.`代码` = '"  + bkcode + "' \r\n" 
					+ "	       AND 通达信交易所指数每日交易信息.`交易日期` between'" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n"
					+ " GROUP BY Year(通达信交易所指数每日交易信息.交易日期)  , WEEK(通达信交易所指数每日交易信息.交易日期), 通达信交易所指数每日交易信息.`代码` \r\n"
					; 
		}
		
//		System.out.println(sqlquerystat);
		System.out.println("为板块" + bankuai.getMyOwnCode() + bankuai.getMyOwnName() + "寻找从" + selecteddatestart.toString() + "到" + selecteddateend.toString() + "占比数据！");
		
		String sqlquerystatfx = "SELECT COUNT(*) AS RESULT FROM 操作记录重点关注 \r\n" + 
				"WHERE 股票代码='" + bkcode + "'" + "\r\n" + 
				"AND 操作记录重点关注.`日期` between '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n"
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
				double bankuaicje = rs.getDouble("板块周交易额");
				double dapancje = rs.getDouble("大盘周交易额");
				java.sql.Date lastdayofweek = rs.getDate("EndOfWeekDate");
//				int year = rs.getInt("CALYEAR");
//				int week = rs.getInt("CALWEEK");
				cjlrecords.setRecordsDayofEndofWeek(lastdayofweek);
				cjlrecords.setMyOwnChengJiaoEr(bankuaicje);
				cjlrecords.setUpLevelChengJiaoEr(dapancje);
//				cjlrecords.setYear(year);
//				cjlrecords.setWeek(week);
				
				tmpcjelist.add(cjlrecords);
				
				//找出分析表里面对应时间段的分析结果
				if(hasfengxiresult) {
					sqlquerystat = "SELECT COUNT(*) AS RESULT FROM 操作记录重点关注 \r\n" + 
							"WHERE 股票代码='" + bkcode + "'" + "\r\n" + 
							"AND WEEK(操作记录重点关注.`日期`) = WEEK('" + lastdayofweek + "')" 
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
	 * 股票对板块的按周占比
	 */
	public Stock getGeGuZhanBiOfBanKuai(String bkcode, Stock stock,LocalDate selecteddatestart,LocalDate selecteddateend,LocalDate addposition)
	{
		String stockcode = stock.getMyOwnCode();
		
		HashMap<String, String> actiontables = this.getActionRelatedTables(bkcode,stockcode);

		String stockvsbktable = actiontables.get("股票板块对应表");
		String bkcjetable = actiontables.get("板块每日交易量表");
		String gegucjetable = actiontables.get("股票每日交易量表");
		String bknametable = actiontables.get("板块指数名称表");
		
		String formatedstartdate = CommonUtility.formatDateYYYY_MM_DD(selecteddatestart);
		String formatedenddate  = CommonUtility.formatDateYYYY_MM_DD(selecteddateend);
		
		String sqlquerystat = "";
		if(stockvsbktable.trim().equals("股票通达信基本面信息对应表") ) { //找地域板块
			String dycode = actiontables.get("地域代码");
			sqlquerystat = "";
			return stock;
		 } else { //概念风格行业指数板块
		 sqlquerystat = "SELECT Y.year, Y.week,Y.EndOfWeekDate, Y.股票代码, Y.板块代码, Y.stock_amount, Y.`股票权重` ,Y.`股票名称`,\r\n" + 
				"Z.代码 , Z.板块名称,  z.category_amount, Y.stock_amount/ z.category_amount ratio \r\n" + 
				"FROM \r\n" + 
				"(select X.year, X.week, X.EndOfWeekDate, X.股票代码, X.板块代码, X.stock_amount, X.`股票权重` ,a股.`股票名称`\r\n" + 
				"from (select year(" +  gegucjetable    + ".`交易日期`) as year,week(" +  gegucjetable    + ".`交易日期`) as week, \r\n" + 
				"DATE(" +  gegucjetable    + ".交易日期 + INTERVAL (6 - DAYOFWEEK(" +  gegucjetable    + ".交易日期)) DAY) EndOfWeekDate, \r\n" + 
				"" +  stockvsbktable + ".`股票代码` , " +  stockvsbktable + ".`板块代码` , \r\n" + 
				"sum(" +  gegucjetable    + ".`成交额`) stock_amount," +  stockvsbktable + ".`股票权重`\r\n" + 
				"from " +  stockvsbktable + ", " +  gegucjetable    + "\r\n" + 
				"where " +  stockvsbktable + ".`股票代码`   = " +  gegucjetable    + ".`代码`\r\n" + 
				"		and Date(" +  gegucjetable    + ".`交易日期`) >= Date(" +  stockvsbktable + ".`加入时间`)\r\n" + 
				"		and " +  gegucjetable    + ".`交易日期` <  ifnull(" +  stockvsbktable + ".`移除时间`, '2099-12-31')\r\n" + 
				"		and " +  gegucjetable    + ".`交易日期` BETWEEN  '" +  formatedstartdate + "' AND '" +  formatedenddate + "'\r\n" + 
				"		and " +  stockvsbktable + ".`板块代码` =  '"  + bkcode +"'\r\n" + 
				"		and " +  stockvsbktable + ".`股票代码` = '"+ stockcode +"'\r\n" + 
				"         \r\n" + 
				"group by year(" +  gegucjetable    + ".`交易日期`) ,week(" +  gegucjetable    + ".`交易日期`) , \r\n" + 
				"		  " +  stockvsbktable + ".`股票代码`, " +  stockvsbktable + ".`板块代码`\r\n" + 
				"order by year(" +  gegucjetable    + ".`交易日期`) ,week(" +  gegucjetable    + ".`交易日期`) , \r\n" + 
				"			" +  stockvsbktable + ".`股票代码`, " +  stockvsbktable + ".`板块代码`) X,\r\n" + 
				"a股\r\n" + 
				"where   X.`股票代码` = a股.`股票代码` ) Y,\r\n" + 
				"						\r\n" + 
				"(select year(" +  bkcjetable + ".`交易日期`) as year, week(" +  bkcjetable + ".`交易日期`) as week,\r\n" + 
				"DATE(" +  bkcjetable + ".交易日期 + INTERVAL (6 - DAYOFWEEK(" +  bkcjetable + ".交易日期)) DAY) EndOfWeekDate, \r\n" + 
				"" +  bkcjetable + ".`代码` , " + bknametable + ".`板块名称`,   sum(" +  bkcjetable + ".`成交额`) category_amount\r\n" + 
				"from " +  bkcjetable + ", " + bknametable + "\r\n" + 
				"where " +  bkcjetable + ".`代码` = " + bknametable  + ".`板块ID`\r\n" + 
				"		and " +  bkcjetable + ".`交易日期` BETWEEN '" +  formatedstartdate + "' AND '" +  formatedenddate + "'\r\n" + 
				"		and " +  bkcjetable + ".`代码` =  '"  + bkcode + "'\r\n" + 
				"group by year(" +  bkcjetable + ".`交易日期`), week(" +  bkcjetable + ".`交易日期`)," +  bkcjetable + ".`代码`\r\n" + 
				"order by year(" +  bkcjetable + ".`交易日期`), week(" +  bkcjetable + ".`交易日期`), " +  bkcjetable + ".`代码`) Z \r\n" + 
				"         \r\n" + 
				"where Y.year = Z. year\r\n" + 
				"    	and Y.week = Z.week\r\n" + 
				"order by Y.year, Y.week"
				;
		 }
//		System.out.println(sqlquerystat);
		System.out.println("为个股" + stock.getMyOwnCode() + stock.getMyOwnName() + "寻找从" + selecteddatestart.toString() + "到" + selecteddateend.toString() + "在" + bkcode + "的占比数据！");
		
		String sqlquerystatfx = "SELECT COUNT(*) AS RESULT FROM 操作记录重点关注 \r\n" + 
				"WHERE 股票代码='" + stockcode + "'" + "\r\n" + 
				"AND 操作记录重点关注.`日期` between '" + formatedstartdate + "' AND '" + formatedenddate + "' \r\n"
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
//				String tmpstockcode = rsfg.getString("股票代码");
//				String tmpstockname = rsfg.getString("股票名称"); //"股票名称"
//				Stock tmpstock = new Stock (tmpstockcode,tmpstockname);
				
//				Integer weight = rsfg.getInt("股票权重");
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
				//找出分析表里面对应时间段的分析结果
				if(hasfengxiresult) {
					sqlquerystat = "SELECT COUNT(*) AS RESULT FROM 操作记录重点关注 \r\n" + 
							"WHERE 股票代码='" + stockcode + "'" + "\r\n" + 
							"AND WEEK(操作记录重点关注.`日期`) = WEEK('" + lastdayofweek + "')" 
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
//			System.out.println( "数据库连接为NULL!");
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
	 * 获取某个节点在指定周的所有关注分析等结果
	 */
	public ArrayList<JiaRuJiHua> getZdgzFxjgForANodeOfGivenPeriod (String nodecode, LocalDate givenwk)
	{
		String sqlquerystat = "SELECT *  FROM 操作记录重点关注 \r\n" + 
				"WHERE 股票代码='" + nodecode + "'" + "\r\n" + 
				"AND WEEK(操作记录重点关注.`日期`) = WEEK('" + givenwk + "')" 
				;
		System.out.println(sqlquerystat);
		CachedRowSetImpl rsfx = connectdb.sqlQueryStatExecute(sqlquerystat);
		
		ArrayList<JiaRuJiHua> jrjh = new ArrayList<JiaRuJiHua> ();
		try {
			while(rsfx.next()) {
				String acttype = rsfx.getString("加入移出标志");
				JiaRuJiHua guanzhu = new JiaRuJiHua(nodecode,acttype); 
				
				java.sql.Date  lastdayofweek = rsfx.getDate("日期");
				LocalDate actiondate = lastdayofweek.toLocalDate(); 
				guanzhu.setJiaRuDate (actiondate );
				String shuoming = rsfx.getString("原因描述");
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
	 * 某个股票在某个板块的某个时间段内的按周占比，给bar chart用
	 */
//	public CachedRowSetImpl getGeGuZhanBi(String tdxbk, String stockcode, Date selecteddatestart,Date selecteddateend) 
//	{
//		HashMap<String, String> actiontables = this.getActionRelatedTables(tdxbk,stockcode);
//		String stockvsbktable = actiontables.get("股票板块对应表");
//		String bkcjltable = actiontables.get("板块每日交易量表");
//		String gegucjltable = actiontables.get("股票每日交易量表");
//		
//		String formatedstartdate = sysconfig.formatDate(selecteddatestart);
//		String formatedenddate  = sysconfig.formatDate(selecteddateend);
//		
//		String sqlquerystat = "SELECT Year(BKJYE.交易日期) AS CALYEAR , WEEK(BKJYE.交易日期) AS CALWEEK, BKJYE.`代码` AS BKCODE , DATE(BKJYE.交易日期 + INTERVAL (7 - DAYOFWEEK(BKJYE.交易日期)) DAY) EndOfWeekDate,\r\n" + 
//				"       SUM(BKJYE.成交额) AS 股票周交易额, SUM(ZSJYE.成交额) AS 板块周交易额, SUM(BKJYE.`成交额`)/SUM(ZSJYE.`成交额`) 占比\r\n" + 
//				" FROM  " + gegucjltable  + " BKJYE\r\n" + 
//				" INNER JOIN " +  bkcjltable   + " ZSJYE \r\n" + 
//				"ON ZSJYE.代码 = '" + tdxbk + "'  \r\n" + 
//				"       AND BKJYE.`交易日期` = ZSJYE.`交易日期`\r\n" + 
//				"       AND  BKJYE.`代码` = '" + stockcode  + "'\r\n" + 
//				"        AND Year(BKJYE.`交易日期`) = YEAR(NOW())\r\n" + 
//				"        AND WEEK(BKJYE.`交易日期`) >=WEEK('" + formatedstartdate + "') \r\n" + 
//				"		  AND WEEK(BKJYE.`交易日期`) <=WEEK('" + formatedenddate + "')\r\n" + 
//				" GROUP BY Year(BKJYE.`交易日期`),WEEK(BKJYE.`交易日期`),BKJYE.`代码`;"
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
	 * 某个股票在某个板块内的占比增长率,
	 */
//	public void getStockZhanBiGrowthRateInBanKuai (String stockcode, String bkcode,Date selecteddatestart,Date selecteddateend)
//	{
//		HashMap<String, String> actiontables = this.getActionRelatedTables(bkcode,stockcode);
//		String stockvsbktable = actiontables.get("股票板块对应表");
//		String bkcjltable = actiontables.get("板块每日交易量表");
//		String gegucjltable = actiontables.get("股票每日交易量表");
//		
//		String formatedstartdate = sysconfig.formatDate(selecteddatestart);
//		String formatedenddate  = sysconfig.formatDate(selecteddateend);
//		
//		String sqlquerystat = "		select A.`板块代码`, A.`股票代码`,\r\n" + 
//				"	       A.stock_amount,  B.category_amount,\r\n" + 
//				"	       A.stock_amount / B.category_amount ratio\r\n" + 
//				"	  from (select 股票通达信风格板块对应表.`股票代码`, 股票通达信风格板块对应表.`板块代码`, sum(通达信上交所股票每日交易信息.`成交额`) stock_amount\r\n" + 
//				"	          from 股票通达信风格板块对应表, 通达信上交所股票每日交易信息\r\n" + 
//				"	         where 股票通达信风格板块对应表.`股票代码`   = 通达信上交所股票每日交易信息.`股票代码`\r\n" + 
//				"	           and 通达信上交所股票每日交易信息.`交易日期` >= 股票通达信风格板块对应表.`加入时间`\r\n" + 
//				"	           and 通达信上交所股票每日交易信息.`交易日期` <  ifnull(股票通达信风格板块对应表.`移除时间`, '2099-12-31')\r\n" + 
//				"	           and (通达信上交所股票每日交易信息.`交易日期` between '2017-09-04' and '2017-09-08')\r\n" + 
//				"	         group by 股票通达信风格板块对应表.`股票代码`, 股票通达信风格板块对应表.`板块代码`\r\n" + 
//				"	         order by 股票通达信风格板块对应表.`股票代码`, 股票通达信风格板块对应表.`板块代码`) A,\r\n" + 
//				"	         \r\n" + 
//				"	       (select 通达信板块每日交易信息.`板块代码` , sum(通达信板块每日交易信息.`成交额`) category_amount\r\n" + 
//				"	          from 通达信板块每日交易信息\r\n" + 
//				"	         where 通达信板块每日交易信息.`交易日期` >= '2017-09-04'\r\n" + 
//				"	           and 通达信板块每日交易信息.`交易日期` <= '2017-09-08'\r\n" + 
//				"	         group by 通达信板块每日交易信息.`板块代码`\r\n" + 
//				"	         order by 通达信板块每日交易信息.`板块代码`) B\r\n" + 
//				"	 where A.`板块代码`   = B.`板块代码`\r\n" + 
//				"	  order by A.`板块代码`,  A.`股票代码`"
//				;
//		System.out.println(sqlquerystat);
//		HashMap<String,Double> bkfxzhanbimap = new HashMap<String,Double> ();
//		CachedRowSetImpl rs = null;
//		try {
//			
//			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//   		 	while(rs.next()) {
////   		 		String bkname = rs.getString("板块名称");
//   		 		String bkcode = rs.getString("BKCODE");
//   		 		Double bkzhanbi = rs.getDouble("占比");
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
//		sqlquerystat = "		select A.`板块代码`, A.`股票代码`,\r\n" + 
//				"	       A.stock_amount,  B.category_amount,\r\n" + 
//				"	       A.stock_amount / B.category_amount ratio\r\n" + 
//				"	  from (select 股票通达信风格板块对应表.`股票代码`, 股票通达信风格板块对应表.`板块代码`, sum(通达信上交所股票每日交易信息.`成交额`) stock_amount\r\n" + 
//				"	          from 股票通达信风格板块对应表, 通达信上交所股票每日交易信息\r\n" + 
//				"	         where 股票通达信风格板块对应表.`股票代码`   = 通达信上交所股票每日交易信息.`股票代码`\r\n" + 
//				"	           and 通达信上交所股票每日交易信息.`交易日期` >= 股票通达信风格板块对应表.`加入时间`\r\n" + 
//				"	           and 通达信上交所股票每日交易信息.`交易日期` <  ifnull(股票通达信风格板块对应表.`移除时间`, '2099-12-31')\r\n" + 
//				"	           and (通达信上交所股票每日交易信息.`交易日期` between '2017-09-04' and '2017-09-08')\r\n" + 
//				"	         group by 股票通达信风格板块对应表.`股票代码`, 股票通达信风格板块对应表.`板块代码`\r\n" + 
//				"	         order by 股票通达信风格板块对应表.`股票代码`, 股票通达信风格板块对应表.`板块代码`) A,\r\n" + 
//				"	         \r\n" + 
//				"	       (select 通达信板块每日交易信息.`板块代码` , sum(通达信板块每日交易信息.`成交额`) category_amount\r\n" + 
//				"	          from 通达信板块每日交易信息\r\n" + 
//				"	         where 通达信板块每日交易信息.`交易日期` >= '2017-09-04'\r\n" + 
//				"	           and 通达信板块每日交易信息.`交易日期` <= '2017-09-08'\r\n" + 
//				"	         group by 通达信板块每日交易信息.`板块代码`\r\n" + 
//				"	         order by 通达信板块每日交易信息.`板块代码`) B\r\n" + 
//				"	 where A.`板块代码`   = B.`板块代码`\r\n" + 
//				"	  order by A.`板块代码`,  A.`股票代码`"
//				;
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rs2 = null;
//		try {
//			rs2 = connectdb.sqlQueryStatExecute(sqlquerystat);
//   		 	while(rs.next()) {
////   		 		String bkname = rs.getString("板块名称");
//   		 		String bkcode = rs.getString("BKCODE");
//   		 		Double bkzhanbi = rs.getDouble("占比");
//   		 		
//   		 	try{
//   		 		Double bkzhanbi = bkfxzhanbimap.get(bkcode);
//   		 		Double bkzbgrowthrate = (bkbenzhouzb - bkzhanbi)/bkzhanbi;
//   		 		bkfxzhanbimap.put(bkcode, bkzbgrowthrate); 
//		 		} catch(java.lang.NullPointerException e){
//		 			//没有，说明是新板块，怎么处理？
//		 			bkfxzhanbimap.put(bkcode, 10000.0); //暂时设置增长率为10000
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
	 * 某一个板块设定时间跨度内和大盘相比的每周占比
	 */
//	public  CachedRowSetImpl getBanKuaiZhanBi (String bkcode,Date selecteddatestart,Date selecteddateend)
//	{
//		HashMap<String, String> actiontables = this.getActionRelatedTables(bkcode,null);
//		String bkcjltable = actiontables.get("板块每日交易量表");
//		
//		String formatedstartdate = sysconfig.formatDate(selecteddatestart);
//		String formatedenddate  = sysconfig.formatDate(selecteddateend);
//				
//		if(!bkcjltable.equals("通达信交易所指数每日交易信息")) { 
//		
//			String sqlquerystat = "SELECT Year(BKJYE.交易日期) AS CALYEAR , WEEK(BKJYE.交易日期) AS CALWEEK, BKJYE.`代码` AS BKCODE , BKJYE.交易日期, DATE(BKJYE.交易日期 + INTERVAL (7 - DAYOFWEEK(BKJYE.交易日期)) DAY) EndOfWeekDate,"
//					+ "	       SUM(BKJYE.成交额)/2 AS 板块周交易额, SUM(ZSJYE.成交额) AS 大盘周交易额, SUM(BKJYE.`成交额`)/(2*SUM(ZSJYE.`成交额`)) AS 占比"
//					+ "	FROM  通达信板块每日交易信息 BKJYE"
//					+ "	INNER JOIN 通达信交易所指数每日交易信息  ZSJYE"
//					+ "	ON ZSJYE.代码 in ('999999','399001' )"
//					+ "	       AND BKJYE.`交易日期` = ZSJYE.`交易日期`"
//					+ "	       AND Year(BKJYE.`交易日期`) = YEAR(NOW())"
//					+ "  AND WEEK(BKJYE.`交易日期`) >=WEEK('" + formatedstartdate + "') AND WEEK(BKJYE.`交易日期`) <=WEEK('" + formatedenddate + "')"
//					+ "	       AND BKJYE.`代码` = '" + bkcode + "'"
//					+ "			GROUP BY Year(BKJYE.`交易日期`),WEEK(BKJYE.`交易日期`),BKJYE.`代码`"
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
//		} else { //板块是指数板块,变成了在同表内的查询
//			String sqlquerystat = "SELECT Year(通达信交易所指数每日交易信息.交易日期) AS CALYEAR , WEEK(通达信交易所指数每日交易信息.交易日期) AS CALWEEK, 通达信交易所指数每日交易信息.`代码` AS BKCODE ,"
//					+ "	sum(通达信交易所指数每日交易信息.成交额) AS 板块周交易额,	sum(sum_dapan) AS 大盘周交易额, sum(通达信交易所指数每日交易信息.成交额) / sum(sum_dapan) AS 占比"
//					+ " FROM 通达信交易所指数每日交易信息 , ("
//					+ "    SELECT 通达信交易所指数每日交易信息.交易日期 dapanjyrq,  SUM(通达信交易所指数每日交易信息.成交额) sum_dapan"
//					+ "    FROM 通达信交易所指数每日交易信息"
//					+ "    where  通达信交易所指数每日交易信息.`代码` IN  ('999999','399001' )"
//					+ "    group by dapanjyrq"
//					+ ") sq"
//					+ " where  通达信交易所指数每日交易信息.交易日期 = dapanjyrq AND 通达信交易所指数每日交易信息.`代码` = '"  + bkcode + "'" + " AND Year(通达信交易所指数每日交易信息.交易日期)  = YEAR(now()) "
//					+ " GROUP BY Year(通达信交易所指数每日交易信息.交易日期)  , WEEK(通达信交易所指数每日交易信息.交易日期), 通达信交易所指数每日交易信息.`代码`"
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
	 * 某板块所有当前个股的占比增长率
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
	 * 所有指数板块成交额占比增长率，以占比增长率排名
	 */
//	public HashMap<String,Double> getAllZhiShuBanKuaiOfChenJiaoErWeekZhanBiOrderByZhanBiGrowthRate(Date selecteddatestart,Date selecteddateend)
//	{
//		String formatedstartdate = sysconfig.formatDate(selecteddatestart);
//		String formatedenddate  = sysconfig.formatDate(selecteddateend);
//		
//		HashMap<String,Double> bkfxzhanbimap = new HashMap<String,Double> ();
//		//上周的交易占比
//		String sqlquerystat = "SELECT  CALYEAR ,  CALWEEK, EndOfWeekDate, BKCODE ,  板块周交易额,  大盘周交易额,  占比, BKLB.`板块名称`\r\n" + 
//				"FROM  (      \r\n" + 
//				"SELECT Year(BKJYE.交易日期) AS CALYEAR , WEEK(BKJYE.交易日期) AS CALWEEK, BKJYE.`代码` AS BKCODE , DATE(BKJYE.交易日期 + INTERVAL (7 - DAYOFWEEK(BKJYE.交易日期)) DAY) EndOfWeekDate,\r\n" + 
//				"       SUM(BKJYE.成交额)/2 AS 板块周交易额, SUM(ZSJYE.成交额) AS 大盘周交易额, SUM(BKJYE.`成交额`)/(2*SUM(ZSJYE.`成交额`)) AS 占比\r\n" + 
//				"       \r\n" + 
//				"FROM  通达信交易所指数每日交易信息 BKJYE\r\n" + 
//				"INNER JOIN 通达信交易所指数每日交易信息  ZSJYE  \r\n" + 
//				"ON ZSJYE.代码 in ('999999','399001' ) \r\n" + 
//				"       AND BKJYE.`交易日期` = ZSJYE.`交易日期`\r\n" + 
//				"       AND Year(BKJYE.`交易日期`) = YEAR(NOW())\r\n" + 
//				"    AND WEEK(BKJYE.`交易日期`) >= (WEEK('" + formatedstartdate + "')-1) AND WEEK(BKJYE.`交易日期`) <= (WEEK('" + formatedenddate + "')-1)" +
//				"		GROUP BY Year(BKJYE.`交易日期`),WEEK(BKJYE.`交易日期`),BKJYE.`代码`\r\n" + 
//				"				) X\r\n" + 
//				"				,通达信交易所指数列表 BKLB\r\n" + 
//				"				WHERE BKCODE = BKLB.`板块ID`"
//				;
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rs = null;
//		try {
//			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//   		 	while(rs.next()) {
////   		 		String bkname = rs.getString("板块名称");
//   		 		String bkcode = rs.getString("BKCODE");
//   		 		Double bkzhanbi = rs.getDouble("占比");
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
//		sqlquerystat = "SELECT  CALYEAR ,  CALWEEK, EndOfWeekDate, BKCODE ,  板块周交易额,  大盘周交易额,  占比, BKLB.`板块名称`\r\n" + 
//				"FROM  (      \r\n" + 
//				"SELECT Year(BKJYE.交易日期) AS CALYEAR , WEEK(BKJYE.交易日期) AS CALWEEK, BKJYE.`代码` AS BKCODE , DATE(BKJYE.交易日期 + INTERVAL (7 - DAYOFWEEK(BKJYE.交易日期)) DAY) EndOfWeekDate,\r\n" + 
//				"       SUM(BKJYE.成交额)/2 AS 板块周交易额, SUM(ZSJYE.成交额) AS 大盘周交易额, SUM(BKJYE.`成交额`)/(2*SUM(ZSJYE.`成交额`)) AS 占比\r\n" + 
//				"       \r\n" + 
//				"FROM  通达信交易所指数每日交易信息 BKJYE\r\n" + 
//				"INNER JOIN 通达信交易所指数每日交易信息  ZSJYE  \r\n" + 
//				"ON ZSJYE.代码 in ('999999','399001' ) \r\n" + 
//				"       AND BKJYE.`交易日期` = ZSJYE.`交易日期`\r\n" + 
//				"       AND Year(BKJYE.`交易日期`) = YEAR(NOW())\r\n" + 
//				"    AND WEEK(BKJYE.`交易日期`) >=WEEK('" + formatedstartdate + "') AND WEEK(BKJYE.`交易日期`) <=WEEK('" + formatedenddate + "') " +
//				"		GROUP BY Year(BKJYE.`交易日期`),WEEK(BKJYE.`交易日期`),BKJYE.`代码`\r\n" + 
//				"				) X\r\n" + 
//				"				,通达信交易所指数列表 BKLB\r\n" + 
//				"				WHERE BKCODE = BKLB.`板块ID`"
//				;
//		
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rssz = null; 
//		try {  
//		 	 rssz = connectdb.sqlQueryStatExecute(sqlquerystat);
//		 	 while(rssz.next()) {
//		 		String bkname = rssz.getString("板块名称");
//   		 		String bkcode = rssz.getString("BKCODE");
//   		 		Double bkbenzhouzb  = rssz.getDouble("占比");
//   		 		
//   		 		try{
//	   		 		Double bkzhanbi = bkfxzhanbimap.get(bkcode);
//	   		 		Double bkzbgrowthrate = (bkbenzhouzb - bkzhanbi)/bkzhanbi;
//	   		 		bkfxzhanbimap.put(bkcode, bkzbgrowthrate); 
//   		 		} catch(java.lang.NullPointerException e){
//   		 			//没有，说明是新板块，怎么处理？
//   		 			bkfxzhanbimap.put(bkcode, 10000.0); //暂时设置增长率为10000
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
//	 * 所有880板块成交额占比，以占比增长率排名
//	 */
//	public HashMap<String,Double> getAllTdxBanKuaiOfChenJiaoErWeekZhanBiOrderByZhanBiGrowthRate(Date selecteddatestart,Date selecteddateend)
//	{
//		String formatedstartdate = sysconfig.formatDate(selecteddatestart);
//		String formatedenddate  = sysconfig.formatDate(selecteddateend);
//		
//		HashMap<String,Double> bkfxzhanbimap = new HashMap<String,Double> ();
//		//上周的交易占比
//		String sqlquerystat = "SELECT  CALYEAR ,  CALWEEK,  EndOfWeekDate, BKCODE ,  板块周交易额,  大盘周交易额,  占比, BKLB.`板块名称`"
//				+ "	FROM  ("
//				+ "	SELECT Year(BKJYE.交易日期) AS CALYEAR , WEEK(BKJYE.交易日期) AS CALWEEK, BKJYE.`代码` AS BKCODE , DATE(BKJYE.交易日期 + INTERVAL (7 - DAYOFWEEK(BKJYE.交易日期)) DAY) EndOfWeekDate, "
//				+ " SUM(BKJYE.成交额)/2 AS 板块周交易额, SUM(ZSJYE.成交额) AS 大盘周交易额, SUM(BKJYE.`成交额`)/(2*SUM(ZSJYE.`成交额`)) AS 占比"
//				+ "	FROM  通达信板块每日交易信息 BKJYE"
//				+ "	INNER JOIN 通达信交易所指数每日交易信息  ZSJYE"
//				+ "	ON ZSJYE.代码 in ('999999','399001' )"
//				+ "    AND BKJYE.`交易日期` = ZSJYE.`交易日期`"
//				+ "    AND Year(BKJYE.`交易日期`) = YEAR(NOW())"
//				+ "    AND WEEK(BKJYE.`交易日期`) >= (WEEK('" + formatedstartdate + "')-1) AND WEEK(BKJYE.`交易日期`) <= (WEEK('" + formatedenddate + "')-1)"
//				+ "	GROUP BY Year(BKJYE.`交易日期`),WEEK(BKJYE.`交易日期`),BKJYE.`代码`"
//				+ "	) X"
//				+ "	,通达信板块列表 BKLB"
//				+ "	WHERE BKCODE = BKLB.`板块ID`"
//				;
//				
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rs = null;
//		try {
//			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//   		 	while(rs.next()) {
//   		 		String bkname = rs.getString("板块名称");
//   		 		String bkcode = rs.getString("BKCODE");
//   		 		Double bkzhanbi = rs.getDouble("占比");
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
//		//这周的交易占比
//		sqlquerystat = "SELECT  CALYEAR ,  CALWEEK,  EndOfWeekDate, BKCODE ,  板块周交易额,  大盘周交易额,  占比, BKLB.`板块名称`"
//				+ "	FROM  ("
//				+ "	SELECT Year(BKJYE.交易日期) AS CALYEAR , WEEK(BKJYE.交易日期) AS CALWEEK, BKJYE.`代码` AS BKCODE , DATE(BKJYE.交易日期 + INTERVAL (7 - DAYOFWEEK(BKJYE.交易日期)) DAY) EndOfWeekDate, "
//				+ " SUM(BKJYE.成交额)/2 AS 板块周交易额, SUM(ZSJYE.成交额) AS 大盘周交易额, SUM(BKJYE.`成交额`)/(2*SUM(ZSJYE.`成交额`)) AS 占比"
//				+ "	FROM  通达信板块每日交易信息 BKJYE"
//				+ "	INNER JOIN 通达信交易所指数每日交易信息  ZSJYE"
//				+ "	ON ZSJYE.代码 in ('999999','399001' )"
//				+ "    AND BKJYE.`交易日期` = ZSJYE.`交易日期`"
//				+ "    AND Year(BKJYE.`交易日期`) = YEAR(NOW())"
//				+ "    AND WEEK(BKJYE.`交易日期`) >= (WEEK('" + formatedstartdate + "')) AND WEEK(BKJYE.`交易日期`) <= (WEEK('" + formatedenddate + "'))"
//				+ "	GROUP BY Year(BKJYE.`交易日期`),WEEK(BKJYE.`交易日期`),BKJYE.`代码`"
//				+ "	) X"
//				+ "	,通达信板块列表 BKLB"
//				+ "	WHERE BKCODE = BKLB.`板块ID`"
//				;
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rssz = null; 
//		try {  
//		 	 rssz = connectdb.sqlQueryStatExecute(sqlquerystat);
//		 	 while(rssz.next()) {
//		 		String bkname = rssz.getString("板块名称");
//   		 		String bkcode = rssz.getString("BKCODE");
//   		 		Double bkbenzhouzb  = rssz.getDouble("占比");
//   		 		
//   		 		try{
//	   		 		Double bklastzhanbi = bkfxzhanbimap.get(bkcode);
//	   		 		Double bkzbgrowthrate = (bkbenzhouzb - bklastzhanbi)/bklastzhanbi;
//	   		 		bkfxzhanbimap.put(bkcode, bkzbgrowthrate); 
//   		 		} catch(java.lang.NullPointerException e){
//   		 			//没有，说明是新板块，怎么处理？
//   		 			bkfxzhanbimap.put(bkcode, 10000.0); //暂时设置增长率为10000
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
	 * 检查设定日期周的数据是否完整，只对当前走进行数据完整性测试，过去周默认数据完整。
	 * -1 数据完整  0：所选周没有数据  >0，数据到那一天
	 */
//	private int precheckBeforeBanKuaiFengXi(Date bkfxdate)
//	{
//		if(CommonUtility.getWeekNumber(bkfxdate) == CommonUtility.getWeekNumber(new Date()) ) {
//			
//			String sqlquerystat = " SELECT MAX(交易日期) FROM 通达信交易所指数每日交易信息" ;
//			CachedRowSetImpl rs = null; 
//			try {  
//			 	 rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//			 	 
//			 	 java.sql.Timestamp tmplastmdftime = null;
//			 	 while(rs.next())
//			 		  tmplastmdftime = rs.getTimestamp("MAX(交易日期)");
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
	 * 板块成交额占比，以占比排序
	 */
//	public DefaultCategoryDataset getBanKuaiFengXiBarInfoOfChenJiaoErZhanBiOrderByZhanBi(LocalDate bkfxdate) 
//	{
//		HashMap<String, BanKuai> curdaleidetaillistmap = this.getTDXBanKuaiList();
//		Set<String> curdaleidetaillistset = curdaleidetaillistmap.keySet();
//		String fengxidate = formateDateForDiffDatabase("mysql",CommonUtility.formatDateYYYY_MM_DD_HHMMSS(bkfxdate).substring(0,10)); 
//		
//		String sqlquerystat = "	SELECT  CALYEAR ,  CALWEEK,  BKCODE ,  板块周交易额,  大盘周交易额,  占比, BKLB.`板块名称` "
//				+ "FROM  ("
//				+ "	SELECT Year(BKJYE.交易日期) AS CALYEAR , WEEK(BKJYE.交易日期) AS CALWEEK, BKJYE.`代码` AS BKCODE,"
//				+ "       SUM(BKJYE.成交额)/2 AS 板块周交易额, SUM(ZSJYE.成交额) AS 大盘周交易额, SUM(BKJYE.`成交额`)/(2*SUM(ZSJYE.`成交额`)) AS 占比"
//				+ "	FROM  通达信板块每日交易信息 BKJYE"
//				+ "	INNER JOIN 通达信交易所指数每日交易信息  ZSJYE"
//				+ "	ON ZSJYE.代码 in ('999999','399001' )"
//				+ "    AND BKJYE.`交易日期` = ZSJYE.`交易日期`"
//				+ "    AND Year(BKJYE.`交易日期`) = YEAR(" +  fengxidate + " )"
//				+ "    AND BKJYE.`交易日期` BETWEEN DATE_SUB( " +  fengxidate + " ,  INTERVAL( WEEKDAY( " +  fengxidate + " ) ) DAY ) AND " +  fengxidate  
//				+ " GROUP BY Year(BKJYE.`交易日期`),WEEK(BKJYE.`交易日期`),BKJYE.`代码`"
//				+ ") X"
//				+ ",通达信板块列表 BKLB"
//				+ "	WHERE BKCODE = BKLB.`板块ID`"
//				+ "	ORDER BY 占比 desc"
//				;
//		System.out.println(sqlquerystat);
//		CachedRowSetImpl rs = null;
//		DefaultCategoryDataset barchart_dataset = new DefaultCategoryDataset();
//		try {
//			rs = connectdb.sqlQueryStatExecute(sqlquerystat);
//   		 	while(rs.next()) {
//   		 		String bkname = rs.getString("板块名称");
//   		 		String bkcode = rs.getString("BKCODE");
//   		 		Double bkzhanbi = rs.getDouble("占比");
//   		 		
//   		 		barchart_dataset.addValue(bkzhanbi, "板块成交额占比", bkname);
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
	 * 同步个股成交量信息，参数为交易所 SH/SZ
	 */
	public File refreshTDXGeGuVolAmoToDb(String jiaoyisuo) 
	{
		File tmpreportfolder = Files.createTempDir();
		File tmprecordfile = new File(tmpreportfolder + "同步深圳个股成交量信息.tmp");
		
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
			
			String sqlquerystat = "SELECT  股票代码  FROM A股  WHERE      ifnull(已退市,1 )  or 已退市 = false "
						;
			System.out.println(sqlquerystat);
		    	rsdm = connectdb.sqlQueryStatExecute(sqlquerystat);
		    	while(rsdm.next()) {
		    		 String ggcode = rsdm.getString("股票代码"); //mOST_RECENT_TIME
		    		 if( (ggcode.startsWith("00") || ggcode.startsWith("30") ) && jiaoyisuo.toLowerCase().equals("sz")) //只存深市股票
		    			 allgegucode.add(ggcode);
		    		 else if( (ggcode.startsWith("6") ) && jiaoyisuo.toLowerCase().equals("sh") ) //只存沪市股票
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
			optTable = "通达信深交所股票每日交易信息";
		else if(jiaoyisuo.toLowerCase().equals("sh") )
			optTable = "通达信上交所股票每日交易信息";
		for(String tmpbkcode:allgegucode) {
			//String bkfilename = "SH" + filenamerule.replaceAll("YYXXXXXX", tmpbkcode);
			String bkfilename = null;
			if(jiaoyisuo.toLowerCase().equals("sz") )
				bkfilename = (filenamerule.replaceAll("YY","SZ")).replaceAll("XXXXXX", tmpbkcode);
			else if(jiaoyisuo.toLowerCase().equals("sh") ) 
				bkfilename = (filenamerule.replaceAll("YY","SH")).replaceAll("XXXXXX", tmpbkcode);
			File tmpbkfile = new File(exportath + "/" + bkfilename);
			if (!tmpbkfile.exists() || tmpbkfile.isDirectory() || !tmpbkfile.canRead()) {  
				System.out.println("读取" + bkfilename + "发生错误！");
			    continue; 
			} 

				CachedRowSetImpl  rs = null;
				LocalDate ldlastestdbrecordsdate = null;
				try { 				
					String sqlquerystat = "SELECT  MAX(交易日期) 	MOST_RECENT_TIME"
							+ " FROM "+ optTable +  " WHERE  代码 = " 
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
	 * 修改板块中某个个股的权重
	 */
	public void setStockWeightInBanKuai(String bkcode, String bkname, String stockcode, int weight) 
	{
		HashMap<String, String> actiontables = this.getActionRelatedTables(bkcode, stockcode);
		String bktypetable = actiontables.get("股票板块对应表");
		String bkorzsvoltable = actiontables.get("板块每日交易量表");
		String bknametable = actiontables.get("板块指数名称表");
		String sqlupdatestat = "update " + bktypetable  + " set 股票权重 = " +  weight  + 
				" where 板块代码 = '" + bkcode + "' AND 股票代码 = '" + stockcode + "'" 
				;
		System.out.println(sqlupdatestat);
		connectdb.sqlUpdateStatExecute(sqlupdatestat);
		
		//因为不知道bkcode在哪个表里面，
//		String sqlupdatestat1 = "update 股票通达信交易所指数对应表  set 股票权重 = " +  weight  + 
//							" where 指数板块 = '" + bkcode + "' AND 股票代码 = '" + stockcode + "' AND ISNULL(移除时间)" 
//							;
//		String sqlupdatestat2 = "update 股票通达信概念板块对应表  set 股票权重 = " +  weight  + 
//							" where 概念板块 = '" + bkcode + "' AND 股票代码 = '" + stockcode + "' AND ISNULL(移除时间)"
//							;
//		String sqlupdatestat3 = "update 股票通达信行业板块对应表  set 股票权重 = " +  weight  + 
//							" where 行业板块 = '" + bkcode + "' AND 股票代码 = '" + stockcode + "' AND ISNULL(移除时间)"
//							;
//		String sqlupdatestat4 = "update 股票通达信风格板块对应表  set 股票权重 = " +  weight  + 
//							" where 风格板块 = '" + bkcode + "' AND 股票代码 = '" + stockcode + "' AND ISNULL(移除时间)"
//							;
//		//很傻的实现
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
						+ "\"" +  CommonUtility.formatDateYYYY_MM_DD_HHMMSS(LocalDateTime.now())   + "\""  
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
			String sqlquerystat1= "SELECT ggyk.日期, "
								+ "IIF(ggyk.盈亏金额>0,'盈利','亏损') AS 盈亏情况,"
								+ " ggyk.原因描述,"
								+ " ggyk.ID,"
								+ " ggyk.操作账号,"
								+  "'个股盈亏'" 
								+ " FROM  A股个股盈亏 ggyk "
								+ " WHERE ggyk.股票代码 =" + "'" + stockcode + "'" 
								
								+ " UNION ALL " 
								;
			//System.out.println(sqlquerystat1);
	       String sqlquerystat2="SELECT czjl.日期, " +		""
								+ "IIF( czjl.买卖金额=0.0,'送转股',IIF(czjl.挂单 = true,IIF(czjl.买入卖出标志, '挂单买入', '挂单卖出'),IIF(czjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
								+ " czjl.原因描述,"
								+ " czjl.ID,"
								+ " czjl.买卖账号,"
								+ "'操作记录买卖'" 
								+ " FROM 操作记录买卖 czjl "
								+ "	WHERE czjl.股票代码 =" + "'" + stockcode + "'"
								
								+ " UNION ALL " 
								;
	       //System.out.println(sqlquerystat2);
	      String sqlquerystat3="SELECT rqczjl.日期,"
								+ "IIF( rqczjl.买卖金额=0.0,'送转股',IIF(rqczjl.挂单 = true,IIF(rqczjl.买入卖出标志, '挂单买入', '挂单卖出'),IIF(rqczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
								+ "rqczjl.原因描述,"
								+ "rqczjl.ID,"
								+ "rqczjl.买卖账号,"
								+ "'操作记录融券买卖'"
								+ " FROM 操作记录融券买卖 rqczjl"
								+ "	WHERE rqczjl.股票代码 =" + "'" + stockcode + "'"
								
								+ " UNION  ALL "
								;
	      //System.out.println(sqlquerystat3);
		 String sqlquerystat4="SELECT rzczjl.日期,"
								+ "IIF( rzczjl.买卖金额=0.0,'送转股',IIF(rzczjl.挂单 = true,IIF(rzczjl.买入卖出标志, '挂单买入', '挂单卖出'),IIF(rzczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
								+ "rzczjl.原因描述,"
								+ "rzczjl.ID,"
								+ "rzczjl.买卖账号,"
								+ "'操作记录融资买卖'"
								+ " FROM 操作记录融资买卖 rzczjl"
								+ " WHERE rzczjl.股票代码 =" + "'" + stockcode + "'"
								
								+ " UNION ALL "
								;
		 //System.out.println(sqlquerystat4);
	     String sqlquerystat5="SELECT zdgz.日期,"
								+ "zdgz.加入移出标志,"
								+ "zdgz.原因描述,"
								+ "zdgz.ID,"
								+ "null,"
								+ "'操作记录重点关注'"
								+ " FROM 操作记录重点关注  zdgz"
								+ " WHERE zdgz.股票代码 =" + "'" + stockcode + "'"
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
			String sqlquerystat1= "SELECT ggyk.日期, "
								+ "IF(ggyk.盈亏金额>0,'盈利','亏损') AS 盈亏情况,"
								+ " ggyk.原因描述,"
								+ " ggyk.ID,"
								+ " ggyk.操作账号,"
								+  "'个股盈亏'" 
								+ " FROM  A股个股盈亏 ggyk "
								+ " WHERE ggyk.股票代码 =" + "'" + stockcode + "'" 
								
								+ " UNION ALL " 
								;
			//System.out.println(sqlquerystat1);
	       String sqlquerystat2="SELECT czjl.日期, " +		""
								+ "IF( czjl.买卖金额=0,'送转股',IF(czjl.挂单 = true,IF(czjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(czjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
								+ " czjl.原因描述,"
								+ " czjl.ID,"
								+ " czjl.买卖账号,"
								+ "'操作记录买卖'" 
								+ " FROM 操作记录买卖 czjl "
								+ "	WHERE czjl.股票代码 =" + "'" + stockcode + "'"
								
								+ " UNION ALL " 
								;
	       //System.out.println(sqlquerystat2);
	      String sqlquerystat3="SELECT rqczjl.日期,"
								+ "IF( rqczjl.买卖金额=0,'送转股',IF(rqczjl.挂单 = true,IF(rqczjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(rqczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
								+ "rqczjl.原因描述,"
								+ "rqczjl.ID,"
								+ "rqczjl.买卖账号,"
								+ "'操作记录融券买卖'"
								+ " FROM 操作记录融券买卖 rqczjl"
								+ "	WHERE rqczjl.股票代码 =" + "'" + stockcode + "'"
								
								+ " UNION  ALL "
								;
	      //System.out.println(sqlquerystat3);
		 String sqlquerystat4="SELECT rzczjl.日期,"
								+ "IF( rzczjl.买卖金额=0,'送转股',IF(rzczjl.挂单 = true,IF(rzczjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(rzczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
								+ "rzczjl.原因描述,"
								+ "rzczjl.ID,"
								+ "rzczjl.买卖账号,"
								+ "'操作记录融资买卖'"
								+ " FROM 操作记录融资买卖 rzczjl"
								+ " WHERE rzczjl.股票代码 =" + "'" + stockcode + "'"
								
								+ " UNION ALL "
								;
		 //System.out.println(sqlquerystat4);
	     String sqlquerystat5="SELECT zdgz.日期,"
								+ "zdgz.加入移出标志,"
								+ "zdgz.原因描述,"
								+ "zdgz.ID,"
								+ "null,"
								+ "'操作记录重点关注'"
								+ " FROM 操作记录重点关注  zdgz"
								+ " WHERE zdgz.股票代码 =" + "'" + stockcode + "'"
								+ " ORDER BY 1 DESC,5,3 " //desc
								;
	     //System.out.println(sqlquerystat5);
		
	     String sqlquerystat = sqlquerystat1 + sqlquerystat2 + sqlquerystat3 + sqlquerystat4 + sqlquerystat5;
	     //System.out.println(sqlquerystat);
	     
	     return sqlquerystat;
		}

		/*
		 * 获得和该股票相关的所有信息
		 */
		private Object[][] setZdgzMrmcZdgzYingKuiRecords(CachedRowSetImpl rs) 
		{
			String[][] data = null;  
		    try  {
		    	int k = 0;
		        int columnCount = 6;//列数
		        
		        rs.last();  
		        int rows = rs.getRow();  
		        data = new String[rows][];
		        rs.first();  
		        //while(rs.next())  //{ "日期", "操作", "说明","ID","操作账户","信息表" };
		        for(int j=0;j<rows;j++) {   //{ "日期", "操作", "说明","ID","操作账户","信息表" };
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
			sqlquerystat= "SELECT * FROM 操作记录重点关注   WHERE 日期= date_sub(curdate(),interval 1 day) AND 加入移出标志 = '明日计划' " ;
			sqlstatmap.put("mysql", sqlquerystat);
			
			sqlquerystat=  "SELECT * FROM 操作记录重点关注 "
					+ " WHERE 日期 <  DATE() AND  日期 > IIF( Weekday( date() ) =  2,date()-3,date()-1)  "
					+ "AND 加入移出标志 = '明日计划'"
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
		        	pmdresult = pmdresult + rs.getString("股票代码") + "  " + rs.getString("原因描述") + " ";
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
//				JOptionPane.showMessageDialog(null,"ConnectDatabase -> setANewDtockCodeIndicate 不可预知的错误");
//				System.out.println(""+e);
//			} 		
//			
//			return newStockSign;
//		}

		public Stock getCheckListsXMLInfo(Stock stockbasicinfo)
		{
			String stockcode = stockbasicinfo.getMyOwnCode();
			String sqlquerystat= "SELECT checklistsitems FROM A股   WHERE 股票代码 =" +"'" + stockcode +"'" ;
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
	    
			 if(nodeshouldbedisplayed.getType() == 6) //是股票
			 {
				 HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
				 String sqlupdatestat= "UPDATE A股  SET "
							+ " 股票名称=" + stockname +","
							+ " 概念时间=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getGainiantishidate()) ) +","
							+ " 概念板块提醒=" + txtareainputgainiants +","
							+ " 券商评级时间=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getQuanshangpingjidate() ) ) +","
							+ " 券商评级提醒=" + txtfldinputquanshangpj +","
							+ " 负面消息时间=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getFumianxiaoxidate()  ) ) +","
							+ " 负面消息=" + txtfldinputfumianxx +","
							+ " 正相关及客户=" + txtfldinputzhengxiangguan +","
							+ " 负相关及竞争对手=" + txtfldinputfuxiangguan +","
							+ " 客户=" + "'" + keHuCustom +"'" + ","
							+ " 竞争对手=" + "'" + jingZhengDuiShou + "'"  
							+ " WHERE 股票代码=" + stockcode
							;
				 sqlstatmap.put("mysql", sqlupdatestat);
				 //System.out.println(sqlupdatestat);
				 
				 if( connectdb.sqlUpdateStatExecute(sqlupdatestat) !=0)
					 return true;
				 else return false;
			 }
			 else { //是板块
				 HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
				 String actiontable = null;
				 if(nodeshouldbedisplayed.getMyOwnCode().trim().startsWith("880")) 
					 actiontable = "通达信板块列表";
				 else
					 actiontable = "通达信交易所指数列表";
				 
				 String sqlinsertstat = "UPDATE " + actiontable + " SET "
						 	+ " 板块名称=" + stockname +","
							+ " 概念时间=" + formateDateForDiffDatabase("mysql",CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getGainiantishidate()) ) +","
							+ " 概念板块提醒=" + txtareainputgainiants +","
							+ " 券商评级时间=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getQuanshangpingjidate() ) ) +","
							+ " 券商评级提醒=" + txtfldinputquanshangpj +","
							+ " 负面消息时间=" + formateDateForDiffDatabase("mysql", CommonUtility.formatDateYYYY_MM_DD(nodeshouldbedisplayed.getFumianxiaoxidate()  ) ) +","
							+ " 负面消息=" + txtfldinputfumianxx +","
							+ " 正相关及客户=" + txtfldinputzhengxiangguan +","
							+ " 负相关及竞争对手=" + txtfldinputfuxiangguan +","
							+ " 客户=" + "'" + keHuCustom +"'" + ","
							+ " 竞争对手=" + "'" + jingZhengDuiShou + "'"  
							+ " WHERE 板块ID=" + stockcode
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
			String sqlupdatestat= "UPDATE A股  SET "
					+ "checklistsitems=" + "'" + checklistsitems + "'"
					+ "WHERE 股票代码=" + stockcode
					
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
				zdgzsign = "明日计划";
				shuoming = jiarujihua.getJiHuaLeiXing() + "(价格" + jiarujihua.getJiHuaJiaGe() + ")(" +  jiarujihua.getJiHuaShuoMing();
			} else
				shuoming =  jiarujihua.getJiHuaShuoMing();
			
			HashMap<String,String> sqlstatmap = new HashMap<String,String> ();
			String sqlinsertstat = "INSERT INTO 操作记录重点关注(股票代码,日期,加入移出标志,原因描述) values ("
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
			
//			SELECT gpgn.概念板块, tdxbk.`板块ID`
//			FROM 股票通达信概念板块对应表 gpgn, 通达信板块列表 tdxbk
//			WHERE 股票代码= '000001' AND gpgn.概念板块 = tdxbk.`板块名称`
			String sqlquerystat =null;
			sqlquerystat=  "SELECT gpgn.板块代码 板块代码, tdxbk.`板块名称` 板块名称 FROM 股票通达信概念板块对应表 gpgn, 通达信板块列表 tdxbk"
					+ "  WHERE 股票代码=" + "'" +  stockcode.trim() + "'" + "AND gpgn.板块代码 = tdxbk.`板块ID` AND ISNULL(移除时间)"
					+ "UNION " 
					+ " SELECT gphy.板块代码 板块代码, tdxbk.`板块名称` 板块名称 FROM 股票通达信行业板块对应表 gphy, 通达信板块列表 tdxbk "
					+ " WHERE 股票代码=" + "'" +  stockcode.trim() + "'" + "AND gphy.`板块代码` = tdxbk.`板块ID` AND ISNULL(移除时间)"
					+ "UNION " 
					+ " SELECT gpfg.`板块代码` 板块代码, tdxbk.`板块名称` 板块名称  FROM 股票通达信风格板块对应表 gpfg, 通达信板块列表 tdxbk"
					+ "  WHERE 股票代码= "+ "'" +  stockcode.trim() + "'" + "AND gpfg.`板块代码` = tdxbk.`板块ID` AND ISNULL(移除时间)"
					;
			
			System.out.println(sqlquerystat);
			CachedRowSetImpl rs_gn = connectdb.sqlQueryStatExecute(sqlquerystat);
			try  {     
		        while(rs_gn.next()) {
		        	String bkcode = rs_gn.getString("板块代码");
		        	String bkname = rs_gn.getString("板块名称");
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
			
//			sqlquerystat=  "SELECT 自定义板块 FROM 股票通达信自定义板块对应表  WHERE 股票代码= "
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
//		        	tmpzdybk.add( rs_zdy.getString("自定义板块") );
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
			String sqlquerystat= "SELECT 股票代码 FROM A股   WHERE 股票名称 =" +"'" + stockname +"'" ;
			//System.out.println(sqlquerystat);
			CachedRowSetImpl rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);

			String stockcode = null;
			try {
				//rsagu.first();
				while (rsagu.next()) {
					stockcode = rsagu.getString("股票代码");
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
			sqlquerystat= "SELECT 股票代码 FROM A股   WHERE 曾用名  LIKE" +"'%" + stockname +"%'" ;
			sqlstatmap.put("mysql", sqlquerystat);
//			sqlquerystat= "SELECT 股票代码 FROM A股   WHERE 曾用名  LIKE" +"'*" + stockname +"*%'" ;
//			sqlstatmap.put("access", sqlquerystat);

			CachedRowSetImpl rsagu = connectdb.sqlQueryStatExecute(sqlquerystat);

			String stockcode = "";
			try {
				//rsagu.first();
				while (rsagu.next()) 
					stockcode = rsagu.getString("股票代码");
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
		 * 从通达信中导入股票曾用名的信息
		 */
		public File refreshEverUsedStorkName()
		{
			File tmpreportfolder = Files.createTempDir();
			File tmprecordfile = new File(tmpreportfolder + "同步通达信曾用名报告.txt");
		
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
				 Files.append("开始导入通达信股票曾用名信息:" +  System.getProperty("line.separator") ,tmprecordfile,sysconfig.charSet());
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
				 
//				 String sqlupdatestat= "UPDATE A股  SET "
//							+ " 曾用名=" + "'" + cymstr + "'"
//							+ " WHERE 股票代码=" + stockcode
//							;
//				 System.out.println(sqlupdatestat);
//				 connectdb.sqlUpdateStatExecute(sqlupdatestat);
			 }
			 
			return tmprecordfile;
		}
		
		public void updateStockEverUsedName (String stockcode, String everusedname)
		{
			 String sqlupdatestat= "UPDATE A股  SET "
						+ " 曾用名=" + "'" + everusedname + "'"
						+ " WHERE 股票代码=" + stockcode
						;
			 System.out.println(sqlupdatestat);
			 connectdb.sqlUpdateStatExecute(sqlupdatestat);
			
		}

		public Object[][] getTodaysOperations() 
		{
			 String searchdate = CommonUtility.formatDateYYYY_MM_DD_HHMMSS( LocalDateTime.now() );
			 searchdate = searchdate.replaceAll("\\d{2}:\\d{2}:\\d{2}", "");
			 String sqlquerystat2=" SELECT czjl.股票代码, " 
						+ " IF( czjl.买卖金额=0.0,'送转股',IF(czjl.挂单 = true,IF(czjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(czjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
						+ " czjl.原因描述,"
						+ " czjl.ID,"
						+ " czjl.买卖账号,"
						+ "'操作记录买卖'" 
						+ " FROM 操作记录买卖 czjl "
						+ "	WHERE czjl.日期>=" + "'" + searchdate + "'"
						
						+ " UNION ALL " 
						;
			System.out.println(sqlquerystat2);
			String sqlquerystat3=" SELECT rqczjl.股票代码, "
								+ "IF( rqczjl.买卖金额=0.0,'送转股',IF(rqczjl.挂单 = true,IF(rqczjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(rqczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
								+ "rqczjl.原因描述,"
								+ "rqczjl.ID,"
								+ "rqczjl.买卖账号,"
								+ "'操作记录融券买卖'"
								+ " FROM 操作记录融券买卖 rqczjl"
								+ "	WHERE rqczjl.日期>=" + "'" + searchdate + "'"
								
								+ " UNION  ALL "
								;
			System.out.println(sqlquerystat3);
			String sqlquerystat4=" SELECT rzczjl.股票代码,"
								+ "IF( rzczjl.买卖金额=0.0,'送转股',IF(rzczjl.挂单 = true,IF(rzczjl.买入卖出标志, '挂单买入', '挂单卖出'),IF(rzczjl.买入卖出标志,'买入','卖出') )  ) AS 买卖,"
								+ "rzczjl.原因描述,"
								+ "rzczjl.ID,"
								+ "rzczjl.买卖账号,"
								+ "'操作记录融资买卖'"
								+ " FROM 操作记录融资买卖 rzczjl"
								+ " WHERE rzczjl.日期>=" + "'" + searchdate + "'"
								
								
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
			        int columnCount = 4;//列数  
			        rs.first();  
			        int k = 0;  
			        //while(rs.next())
			        for(int j=0;j<rows;j++) {   //{ "日期", "操作", "说明","ID","操作账户","信息表" };
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
			File tmprecordfile = new File(tmpreportfolder + "同步通达信个股基本面报告.tmp");
			
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
	                
	                 sqlinsetstat = "INSERT INTO 股票通达信基本面信息对应表 (股票代码GPDM, 总股本ZGB,更新日期GXRQ, GJG,FQRFRG,法人股FRG,B股BG,H股HG,流通A股LTAG,每股收益ZGG,"
	                														+  "ZPG,总资产ZZC,流动资产LDZC,固定资产GDZC,无形资产WXZC,股东人数CQTZ,流动负债LDFZ,少数股权CQFZ,"
	                														+ "公积金ZBGJJ,净资产JZC,主营收益ZYSY,营业成本ZYLY,应收帐款QTLY,营业利润YYLY,投资收益TZSY,"
	                														+ "经营现金流BTSY,总现金流YYWSZ,存货SNSYTZ,利润总额LYZE,税后利润SHLY,净利润JLY,"
	                														+ "未分配利益WFPLY,净资产TZMGJZ,地域DY,行业HY,ZBNB) "
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
	                        					+ " 总股本ZGB=" + zgb +","
	                        					+ " 更新日期GXRQ=" + lastupdatedate +","
	                        					+ " GJG=" + gjg +","
	                        					+ " FQRFRG=" + fqrfrg +","
	                        					+ " 法人股FRG=" + frg +","
	                        					+ " B股BG=" + bg +","
	                        					+ " H股HG=" + hg +","
	                        					+ " 流通A股LTAG=" + ltag +","
	                        					+ " 每股收益ZGG=" + zgg + ","
	                        					+ " ZPG=" + zpg +","
	                        					+ " 总资产ZZC=" + zzc +","
	                        					+ " 流动资产LDZC=" + ldzc +","
	                        					+ " 固定资产GDZC=" + gdzc +","
	                        					+ " 无形资产WXZC=" + wxzc +","
	                        					+ " 股东人数CQTZ=" + cqtz +","
	                        					+ " 流动负债LDFZ=" + ldfz +","
	                        					+ " 少数股权CQFZ=" + cqfz +","
	                        					+ " 公积金ZBGJJ=" + zbgjj +","
	                        					+ " 净资产JZC=" + jzc +","
	                        					+ " 主营收益ZYSY=" + zysy +","
	                        					+ " 营业成本ZYLY=" + zyly +","
	                        					+ " 应收帐款QTLY=" + qtly +","
	                        					+ " 营业利润YYLY=" + yyly +","
	                        					+ " 投资收益TZSY=" + tzsy +","
	                        					+ " 经营现金流BTSY=" + btsy +","
	                        					+ " 总现金流YYWSZ=" + yywsz +","
	                        					+ " 存货SNSYTZ=" + snsytz +","
	                        					+ " 利润总额LYZE=" + lyze +","
	                        					+ " 税后利润SHLY=" + shly +","
	                        					+ " 净利润JLY=" + jly +","
	                        					+ " 未分配利益WFPLY=" + wfply +","
	                        					+ " 净资产TZMGJZ=" + tzmgjz +","
	                        					+ " 地域DY=" + dy +","
	                        					+ " 行业HY=" + hy +","
	                        					+ " ZBNB=" + zbnb
	                        					; 
	              System.out.println(sqlinsetstat);
	              connectdb.sqlUpdateStatExecute(sqlinsetstat);
	                recCounter++;

	                //如果在表：A股中也没有这个股票代码，则在A股中也增加该股票
	                sqlinsetstat = "INSERT INTO A股 (股票代码)"
	                		+ "   SELECT * FROM (SELECT '" + stockcode + "') AS tmp"
	                		+ "   WHERE NOT EXISTS ("
	                		+ "   SELECT 股票代码 FROM A股 WHERE 股票代码 = '" + stockcode + "'"
	                		+ ") LIMIT 1"
	                		;
	                connectdb.sqlInsertStatExecute(sqlinsetstat);
	            }
	        } catch (IOException e1) {
	        	System.out.println("出错SQL是:" + sqlinsetstat);
				e1.printStackTrace();
			} catch (ParseException e2) {
				System.out.println("出错SQL是:" + sqlinsetstat);
				
				e2.printStackTrace();
			} catch (Exception e3) {
				System.out.println("出错SQL是:" + sqlinsetstat);
				e3.printStackTrace();
			}
	        
	        return tmprecordfile;
			
		}
//		/*
//		 * 存储板块为某一周的热点，用来回溯热点，否则过段时间就忘了前期是什么热点
//		 */
//		public int setBanKuaiAsReDian(String bkcode, BanKuaiReDian bkrd) 
//		{
//			LocalDate rediandate = bkrd.getReDianDate();
//			String miaoshu = bkrd.getReDianMiaoShu ();
//			
//			String sqlinsertstat = "INSERT INTO 板块个股热点记录(代码,热点日期,描述) values ("
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