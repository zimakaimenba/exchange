package com.exchangeinfomanager.tongdaxinreport;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


import com.exchangeinfomanager.bankuaifengxi.ai.GeGuWeeklyFengXiXmlHandler;
import com.exchangeinfomanager.bankuaifengxi.ai.ZdgzItem;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.TreeMultimap;
import com.google.common.io.Files;

public class TDXFormatedOpt {
	
	private static  Charset charset = Charset.forName("GBK") ;
	private static Logger logger = Logger.getLogger(TDXFormatedOpt.class);

	public TDXFormatedOpt() {
		// TODO Auto-generated constructor stub
	}
	
	private static String formateStockCodeForTDX(String formatedstockcode) 
	{
		String result = new String();
		if(formatedstockcode.trim().startsWith("00") || formatedstockcode.trim().startsWith("30") )  //通达信深交所 代码前要加0
			result = "0|"  +  formatedstockcode.trim();
		else
			result = "1|"  +  formatedstockcode.trim();
		
		return result;
	}
	
	private static String formateToTDXWaiBuShuJuLine(String stockcode, String geguchanyelian)
	{
		return TDXFormatedOpt.formateStockCodeForTDX(stockcode) + "  |  " + geguchanyelian +  " | " + "1.000" + System.getProperty("line.separator");
	}
	
	private static  String formateToTDXWaiBuShuJuLine(String bkcode, String geguchanyelian,String cysid)
	{
		if(cysid.toLowerCase().equals("sh"))
			return "1|"  +  bkcode.trim() + "  |  " + geguchanyelian +  " | " + "1.000" + System.getProperty("line.separator");
		else 
			return "0|"  +  bkcode.trim() + "  |  " + geguchanyelian +  " | " + "1.000" + System.getProperty("line.separator");
	}
	/*
	 * 
	 */
	public static Boolean parserZhiShuGuanJianRiQiToTDXCode ()
	{
		File tmpfilefoler = Files.createTempDir();
		File tongdaxinfile = new File(tmpfilefoler + "指数关键日期.txt");
		
		ConnectDataBase connectdb = ConnectDataBase.getInstance();
		
		String sqlquerystat = "SELECT * FROM 指数关键日期表";
		ResultSet rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		try {
			while (rs.next()) {
				String zhishucode = rs.getString("代码").trim();
				LocalDate date = rs.getDate("日期").toLocalDate();
				String zhishushuoming = rs.getString("说明");
				
				String colorcode;
				if(zhishucode.equals("999999")) 
					colorcode = "COLOR80FFFF";
				else if(zhishucode.equals("000016")) 
					colorcode = "COLORFF8000";
				else if(zhishucode.equals("399006")) 
					colorcode = "COLORFF80FF";
				else
					colorcode = "COLOR80FFFF";
				//DRAWSL(DATE = 1190506  ,CLOSE,10000,1000,2) LINETHICK1 COLOR80FFFF ; {2000亿加税}
				String result = "DRAWSL(DATE =   " 
								 + String.valueOf(Integer.parseInt(date.toString().replace("-", "") ) - 19000000)
								 + "   ,CLOSE,10000,1000,2) LINETHICK1 " + colorcode + "; "
								 + "{" + zhishucode + zhishushuoming + "}"
								 ;
				
				
				try {
					Files.append( result + System.getProperty("line.separator") ,tongdaxinfile,charset);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
		} catch (SQLException e) {
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
		
		try {
			String cmd = "rundll32 url.dll,FileProtocolHandler " + tongdaxinfile.getAbsolutePath();
			Process p  = Runtime.getRuntime().exec(cmd);
			p.waitFor();
		} catch (Exception e1) 	{
			e1.printStackTrace();
		}
		
		return true;
	}
	/*
	 * 
	 */
//	public static boolean parseZdgzBkToTDXCode(HashMap<String, ArrayList<GuanZhuBanKuaiInfo>> zdgzbkmap)
//	{
//		Iterator<String> bankuaidaleiname = zdgzbkmap.keySet().iterator();
//
//		File tmpfilefoler = Files.createTempDir();
//		File tongdaxinfile = new File(tmpfilefoler + "通达信重点关注代码.txt");
//	
//		
//		boolean runresult = false;
//		try	{
//			if(tongdaxinfile.exists())
//			{
//				tongdaxinfile.delete();
//				tongdaxinfile.createNewFile();
//			}
//			else
//				tongdaxinfile.createNewFile();
//		} catch(Exception e) {	
//			e.printStackTrace();
//		}
//			
//			int nullsteps =0 ; //通达信代码中，有内容的板块在前，无内容的板块自动被注释
//			while(bankuaidaleiname.hasNext())
//			{
//				String siglebkname = bankuaidaleiname.next().toString() ;
//		   		ArrayList<GuanZhuBanKuaiInfo> tmpgzbklist = zdgzbkmap.get(siglebkname);
//		   		String result = "";
//		   		try {
//			   		for(GuanZhuBanKuaiInfo tmpgz:tmpgzbklist) {
//			   			if(tmpgz.isOfficallySelected() ) {
//			   				String chanyelian =  tmpgz.getBkchanyelian();
//			        		String seltime = tmpgz.getSelectedtime();
//			        		result =  result + chanyelian + "(" + seltime +")" + "  |  ";
//			   			}
//		        	}
//		   		} catch (Exception e) { 
//		   			
//		   		}
//
//        		try {
//					Files.append( formatedWholeContents(nullsteps,"股票池"+siglebkname,result) + System.getProperty("line.separator") ,tongdaxinfile,charset);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//        		
//        		if(!result.isEmpty()) {
//        			nullsteps ++;
//        		} 
//			}
//			runresult = true;
//
//			try {
//				String cmd = "rundll32 url.dll,FileProtocolHandler " + tongdaxinfile.getAbsolutePath();
//				Process p  = Runtime.getRuntime().exec(cmd);
//				p.waitFor();
//			} catch (Exception e1) 
//			{
//				e1.printStackTrace();
//			}
//		
//		return runresult;
//	}
	/*
	 *生成重点关注的通达信代码 
	 */
	public  static String getStockZdgzInfo()
	{
		ConnectDataBase connectdb = ConnectDataBase.getInstance();
		
		
		Map<String,String> stockresult = new HashMap<String,String> ();
		String sqlquerystat = "SELECT * FROM 股票通达信自定义板块对应表  WHERE 加入时间 > DATE_SUB( CURDATE( ) , INTERVAL( DAYOFWEEK( CURDATE( ) ) + 300 ) DAY ) ORDER BY 加入时间  DESC";
		ResultSet rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		try {
			while(rs.next()) {
				String formatedstockcode = rs.getString("股票代码");
//				if(formatedstockcode.trim().equals("000000")) // 000000不需要导出
//					continue;
				
				java.sql.Date jrdate = rs.getDate("加入时间");
//				LocalDate ljrdate = jrdate.toLocalDate(); 
				
				java.sql.Date ycdate = rs.getDate("移除时间");
				
//				LocalDate lycdate = ycdate.toLocalDate();
				
				String stockzdgz;
				try {
					stockzdgz = stockresult.get(formatedstockcode);
					stockzdgz = stockzdgz.trim() + "[" + jrdate.toLocalDate().toString() + "加入关注" + "]";
				} catch ( java.lang.NullPointerException e) {
					stockzdgz = "[" + jrdate.toLocalDate().toString() + "加入关注" + "]";
				}
				
				
				try {
					stockzdgz = stockzdgz + "[" + ycdate.toString() + "移出关注" + "]";
				} catch ( java.lang.NullPointerException e) {
					
				}
				
				stockresult.put(formatedstockcode, stockzdgz);
			}
				
		} catch (SQLException e) {
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
		
		SystemConfigration sysconfig = SystemConfigration.getInstance();
		
		 File filezdgz = new File( sysconfig.getTdxZdgzReportFile() );
		 try {
				if (filezdgz.exists()) {
					filezdgz.delete();
					filezdgz.createNewFile();
				} else
					filezdgz.createNewFile();
		 } catch (java.io.IOException e) {
			 filezdgz.getParentFile().mkdirs(); //目录不存在，先创建目录
				try {
					filezdgz.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
				filezdgz.delete();
				return null;
			}

		for (Map.Entry<String, String> entry : stockresult.entrySet()) {
			String formatedstockcode = entry.getKey();
			String result = entry.getValue();
			
			if(!Strings.isNullOrEmpty(result)  ) {
				String lineformatedgainiantx = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedstockcode, result );
				try {
					Files.append(lineformatedgainiantx,filezdgz, charset);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		
		stockresult = null;
		
		return filezdgz.getParent();
	}
	/*
	 * 
	 */
	private static String formatedWholeContents(int i, String daleibankuai, String result) 
	{
		//DRAWTEXT_FIX( NOT(CODELIKE('880')), PSTN_ULX_C2,PSTN_ULY+0*PSTN_STEP_ULY,0, ' 国新政策:钢铁行业(17-04-05) 京津冀(17-04-05) ZDY项目PPP(17-04-05)   ')    {};
		String part1 = "DRAWTEXT_FIX( NOT(CODELIKE('880')), PSTN_ULX_C2,PSTN_ULY+" ;
		String part2 = "*PSTN_STEP_ULY,0, ' ";
		String part3 = " ')    {}; "; 
		
		if(result.isEmpty()) //列表为空，自动生成注释代码
			return "{" + part1  + i + part2 + daleibankuai +" : " + result + part3;
		else 
			return part1  + i + part2 + daleibankuai +" : " + result + part3;
		
	}
	/*
	 * 产业链 to 通达信报告
	 */
	public static String parseChanYeLianXmlToTDXReport () 
	{
		SystemConfigration sysconfig = SystemConfigration.getInstance();
		String cylxml = sysconfig.getGeGuChanYeLianXmlFile();
		String cylreportname = sysconfig.getTDXChanYeLianReportFile ();
		Element xmlroot;
    	SAXReader reader;
    	
    	File cylfile =   new File(cylxml );
		if(!cylfile.exists()) { 
			return null;
		}
    	FileInputStream xmlfileinput = null;
		try {
			xmlfileinput = new FileInputStream(cylxml);
		} catch (FileNotFoundException e) {
			return null;
		}
		
    	File cylreport = new File(cylreportname);
		try {
			if (cylreport.exists()) {
				cylreport.delete();
				cylreport.createNewFile();
			} else
				cylreport.createNewFile();
		} catch (java.io.IOException e) {
			cylreport.getParentFile().mkdirs(); //目录不存在，先创建目录
			try {
				cylreport.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			cylreport.delete();
			return null;
		}
		
		reader = new SAXReader();
		try {
			Document document = reader.read(xmlfileinput);
			xmlroot = document.getRootElement();
			
			Iterator itroot = xmlroot.elementIterator();
			while(itroot.hasNext()) {
				Element elestocknode = (Element) itroot.next();
				String stockcode = elestocknode.attributeValue("stockcode");
				Iterator itstocknode = elestocknode.elementIterator();
				String geguchanyelian = "[";
				while(itstocknode.hasNext()) {
					Element elechanyelian = (Element) itstocknode.next();
					geguchanyelian = geguchanyelian + elechanyelian.getText() + "] ";
				}
				
				//此处应该写入文件或者写入list
				try {
					Files.append(TDXFormatedOpt.formateToTDXWaiBuShuJuLine(stockcode,geguchanyelian),cylreport, charset);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		} catch (DocumentException e) {
			logger.debug(cylxml+ "存在错误");
			cylreport.delete();
			return null;
		}
		
		return cylreport.getParent();
	}
	/*
	 * 板块个股分析记录信息，是存在操作记录重点关注 表的。 现在关注历史是通过自定义表找出来的。
	 */
	public static String stockAndBanKuaiFenXiReports ()
	{
		ConnectDataBase connectdb = ConnectDataBase.getInstance();
		SystemConfigration sysconfig = SystemConfigration.getInstance();
		
		 File filezdgz = new File( sysconfig.getTdxFenXiReportFile() );
		 try {
				if (filezdgz.exists()) {
					filezdgz.delete();
					filezdgz.createNewFile();
				} else
					filezdgz.createNewFile();
		 } catch (java.io.IOException e) {
			 filezdgz.getParentFile().mkdirs(); //目录不存在，先创建目录
				try {
					filezdgz.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
				filezdgz.delete();
				return null;
			}
		 
		String sqlquerystat = "SELECT * FROM 操作记录重点关注 WHERE 日期 > DATE_SUB( CURDATE( ) , INTERVAL( DAYOFWEEK( CURDATE( ) ) + 300 ) DAY )  ";
				
//			logger.debug(sqlquerystat);
		ResultSet rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		
		ArrayListMultimap<String ,String> treemap = ArrayListMultimap.create();
		try {
			while(rs.next()) {
				String formatedstockcode = rs.getString("股票代码");
				if(formatedstockcode.trim().equals("000000")) // 000000不需要导出
					continue;
				
				java.sql.Date dateindb = rs.getDate("日期");
				LocalDate recorddate = dateindb.toLocalDate(); 
				
				GeGuWeeklyFengXiXmlHandler xmlhandler = new GeGuWeeklyFengXiXmlHandler (formatedstockcode,recorddate);
				String comments = recorddate.toString();
				comments = comments + xmlhandler.getZhongDianGuanZhu().getZdgzAllSelectedInfo();

				treemap.put(formatedstockcode, comments);
				
				xmlhandler = null;
				comments = null;
			}
				
		} catch (SQLException e) {
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
		
		//遍历treemap
		Set<String> stockcodeset = treemap.keySet();
		for(String stockcode : stockcodeset) {
			List<String> keyvalues = treemap.get(stockcode);
			String result = keyvalues.toString();
			
			if(!Strings.isNullOrEmpty(result)  ) {
				String lineformatedgainiantx = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(stockcode, result );
				try {
					Files.append(lineformatedgainiantx,filezdgz, charset);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		
		
		treemap = null;
		stockcodeset = null;
		
		return filezdgz.getAbsolutePath();
	}
/*
 * 所有个股的基本信息
 */
	public static String stockJiBenMianToReports ()
	{
		ConnectDataBase connectdb = ConnectDataBase.getInstance();
		SystemConfigration sysconfig = SystemConfigration.getInstance();
		
		 File filegnts = new File( sysconfig.getTdxBbFileGaiNianTiShi() );
		 File filefmxx = new File( sysconfig.getTdxBbfileFuMianXiaoXi() );
		 File filezzfxgkzd = new File( sysconfig.getTdxBbFileZzfxgkhzd() );
		 
		try {
			if (filegnts.exists()) {
				filegnts.delete();
				filegnts.createNewFile();
			} else
				filegnts.createNewFile();
		} catch (java.io.IOException e) {
			filegnts.getParentFile().mkdirs(); //目录不存在，先创建目录
			try {
				filegnts.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			filegnts.delete();
			return null;
		}
		
		try {
			if (filefmxx.exists()) {
				filefmxx.delete();
				filefmxx.createNewFile();
			} else
				filefmxx.createNewFile();
		} catch (Exception e) {
			return null;
		}
		try {
			if (filezzfxgkzd.exists()) {
				filezzfxgkzd.delete();
				filezzfxgkzd.createNewFile();
			} else
				filezzfxgkzd.createNewFile();
		} catch (Exception e) {
			filezzfxgkzd.delete();
			return null;
		}
		
		//个股基本面
		String sqlquerystat = "SELECT * FROM A股 ";
//		logger.debug(sqlquerystat);
		ResultSet rsgg = connectdb.sqlQueryStatExecute(sqlquerystat);
		if(rsgg == null)	{   
			logger.debug("读取数据库失败");
			return null;
		}
		
		try {
			while(rsgg.next()) {
				String formatedstockcode = rsgg.getString("股票代码");

				String result = "";
				if(!Strings.isNullOrEmpty(rsgg.getString("概念板块提醒"))) 
					try {
						result = result + Strings.nullToEmpty( rsgg.getDate("概念时间").toString()) + rsgg.getString("概念板块提醒");
					} catch (java.lang.NullPointerException e) {
						result = result + Strings.nullToEmpty("") + rsgg.getString("概念板块提醒");
					}
				
				if(!Strings.isNullOrEmpty(rsgg.getString("券商评级提醒") ) ) 
					try {
						result = result + Strings.nullToEmpty(rsgg.getDate("券商评级时间").toString()) + " " + rsgg.getString("券商评级提醒");
					} catch (java.lang.NullPointerException e) {
						result = result + Strings.nullToEmpty("") + " " + rsgg.getString("券商评级提醒");
					}
				if(!Strings.isNullOrEmpty(result)  ) {
					String lineformatedgainiantx = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedstockcode, result );
					try {
						Files.append(lineformatedgainiantx,filegnts, charset);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(!Strings.isNullOrEmpty(rsgg.getString("负面消息")) && !"null".equals(rsgg.getString("负面消息")) ) { //null是历史遗留
					CharSequence lineformatedfumianxx;
					try {
						 lineformatedfumianxx = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedstockcode,Strings.nullToEmpty(rsgg.getDate("负面消息时间").toString()) + " " + rsgg.getString("负面消息"));
					} catch (java.lang.NullPointerException e) {
						 lineformatedfumianxx = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedstockcode,Strings.nullToEmpty("") + " " + rsgg.getString("负面消息"));
					}
					try {
						Files.append(lineformatedfumianxx,filefmxx, charset);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				String formatedzhengxiangguan = null;
				if( !Strings.isNullOrEmpty(rsgg.getString("正相关及客户") ) || !Strings.isNullOrEmpty(rsgg.getString("客户")) ) {
					formatedzhengxiangguan = "正相关及客户(#" + Strings.nullToEmpty(rsgg.getString("正相关及客户"))
											 + " " + Strings.nullToEmpty(rsgg.getString("客户") ) + "#) " ;
				}
				
				String formatedfuxiangguan = null;
				if( !Strings.isNullOrEmpty(rsgg.getString("负相关及竞争对手") ) || !Strings.isNullOrEmpty(rsgg.getString("竞争对手") ) ) {
					formatedfuxiangguan = "负相关及竞争对手(#" + Strings.nullToEmpty(rsgg.getString("负相关及竞争对手") )
											 + " " + Strings.nullToEmpty(rsgg.getString("竞争对手") ) + "#) " ;
				}
				if(!Strings.isNullOrEmpty(formatedzhengxiangguan) || !Strings.isNullOrEmpty(formatedfuxiangguan)  ) {
					String lineformatedresult = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedstockcode,Strings.nullToEmpty(formatedzhengxiangguan) + Strings.nullToEmpty(formatedfuxiangguan) );
					 try {
						Files.append(lineformatedresult,filezzfxgkzd, charset);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
		    	if(rsgg != null)
					try {
						rsgg.close();
						rsgg = null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
		}
		
		
		//板块基本面
		sqlquerystat = "SELECT * FROM 通达信板块列表 ";
//		logger.debug(sqlquerystat);
		ResultSet rsbk = connectdb.sqlQueryStatExecute(sqlquerystat);
		if(rsbk == null)	{   
			logger.debug("读取数据库失败");
			return null;
		}
		
		try {
			while(rsbk.next()) {
				String formatedbkcode = rsbk.getString("板块ID");

				String result = "";
				if(!Strings.isNullOrEmpty(rsbk.getString("概念板块提醒"))) 
					try {
						result = result + Strings.nullToEmpty( rsbk.getDate("概念时间").toString()) + rsbk.getString("概念板块提醒");
					} catch (java.lang.NullPointerException e) {
						result = result + Strings.nullToEmpty("") + rsbk.getString("概念板块提醒");
					}
				
				if(!Strings.isNullOrEmpty(rsbk.getString("券商评级提醒") ) ) 
					try {
						result = result + Strings.nullToEmpty(rsbk.getDate("券商评级时间").toString()) + " " + rsbk.getString("券商评级提醒");
					} catch (java.lang.NullPointerException e) {
						result = result + Strings.nullToEmpty("") + " " + rsbk.getString("券商评级提醒");
					}
				if(!Strings.isNullOrEmpty(result)  ) {
					String lineformatedgainiantx = formateToTDXWaiBuShuJuLine(formatedbkcode, result, "sh" );
					try {
						Files.append(lineformatedgainiantx,filegnts, charset);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(!Strings.isNullOrEmpty(rsbk.getString("负面消息")) && !"null".equals(rsbk.getString("负面消息")) ) { //null是历史遗留
					CharSequence lineformatedfumianxx;
					try {
						 lineformatedfumianxx = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedbkcode, Strings.nullToEmpty(rsbk.getDate("负面消息时间").toString()) + " " + rsbk.getString("负面消息"), "sh");
					} catch (java.lang.NullPointerException e) {
						 lineformatedfumianxx = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedbkcode, Strings.nullToEmpty("") + " " + rsbk.getString("负面消息"), "sh");
					}
					try {
						Files.append(lineformatedfumianxx,filefmxx, charset);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				String formatedzhengxiangguan = null;
				if( !Strings.isNullOrEmpty(rsbk.getString("正相关及客户") ) || !Strings.isNullOrEmpty(rsbk.getString("客户")) ) {
					formatedzhengxiangguan = "正相关及客户(#" + Strings.nullToEmpty(rsbk.getString("正相关及客户"))
											 + " " + Strings.nullToEmpty(rsbk.getString("客户") ) + "#) " ;
				}
				
				String formatedfuxiangguan = null;
				if( !Strings.isNullOrEmpty(rsbk.getString("负相关及竞争对手") ) || !Strings.isNullOrEmpty(rsbk.getString("竞争对手") ) ) {
					formatedfuxiangguan = "负相关及竞争对手(#" + Strings.nullToEmpty(rsbk.getString("负相关及竞争对手") )
											 + " " + Strings.nullToEmpty(rsbk.getString("竞争对手") ) + "#) " ;
				}
				if(!Strings.isNullOrEmpty(formatedzhengxiangguan) || !Strings.isNullOrEmpty(formatedfuxiangguan)  ) {
					String lineformatedresult = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedbkcode, Strings.nullToEmpty(formatedzhengxiangguan) + Strings.nullToEmpty(formatedfuxiangguan), "sh" );
					 try {
						Files.append(lineformatedresult,filezzfxgkzd, charset);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
		    	if(rsbk != null)
					try {
						rsbk.close();
						rsbk = null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
		}

		
		
//		stockZdgzReports ();
		return filegnts.getParent();
	}
}
