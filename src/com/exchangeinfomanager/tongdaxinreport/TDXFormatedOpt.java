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
		if(formatedstockcode.trim().startsWith("00") || formatedstockcode.trim().startsWith("30") )  //ͨ������� ����ǰҪ��0
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
		File tongdaxinfile = new File(tmpfilefoler + "ָ���ؼ�����.txt");
		
		ConnectDataBase connectdb = ConnectDataBase.getInstance();
		
		String sqlquerystat = "SELECT * FROM ָ���ؼ����ڱ�";
		ResultSet rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		try {
			while (rs.next()) {
				String zhishucode = rs.getString("����").trim();
				LocalDate date = rs.getDate("����").toLocalDate();
				String zhishushuoming = rs.getString("˵��");
				
				String colorcode;
				if(zhishucode.equals("999999")) 
					colorcode = "COLOR80FFFF";
				else if(zhishucode.equals("000016")) 
					colorcode = "COLORFF8000";
				else if(zhishucode.equals("399006")) 
					colorcode = "COLORFF80FF";
				else
					colorcode = "COLOR80FFFF";
				//DRAWSL(DATE = 1190506  ,CLOSE,10000,1000,2) LINETHICK1 COLOR80FFFF ; {2000�ڼ�˰}
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
//		File tongdaxinfile = new File(tmpfilefoler + "ͨ�����ص��ע����.txt");
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
//			int nullsteps =0 ; //ͨ���Ŵ����У������ݵİ����ǰ�������ݵİ���Զ���ע��
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
//					Files.append( formatedWholeContents(nullsteps,"��Ʊ��"+siglebkname,result) + System.getProperty("line.separator") ,tongdaxinfile,charset);
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
	 *�����ص��ע��ͨ���Ŵ��� 
	 */
	public  static String getStockZdgzInfo()
	{
		ConnectDataBase connectdb = ConnectDataBase.getInstance();
		
		
		Map<String,String> stockresult = new HashMap<String,String> ();
		String sqlquerystat = "SELECT * FROM ��Ʊͨ�����Զ������Ӧ��  WHERE ����ʱ�� > DATE_SUB( CURDATE( ) , INTERVAL( DAYOFWEEK( CURDATE( ) ) + 300 ) DAY ) ORDER BY ����ʱ��  DESC";
		ResultSet rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		try {
			while(rs.next()) {
				String formatedstockcode = rs.getString("��Ʊ����");
//				if(formatedstockcode.trim().equals("000000")) // 000000����Ҫ����
//					continue;
				
				java.sql.Date jrdate = rs.getDate("����ʱ��");
//				LocalDate ljrdate = jrdate.toLocalDate(); 
				
				java.sql.Date ycdate = rs.getDate("�Ƴ�ʱ��");
				
//				LocalDate lycdate = ycdate.toLocalDate();
				
				String stockzdgz;
				try {
					stockzdgz = stockresult.get(formatedstockcode);
					stockzdgz = stockzdgz.trim() + "[" + jrdate.toLocalDate().toString() + "�����ע" + "]";
				} catch ( java.lang.NullPointerException e) {
					stockzdgz = "[" + jrdate.toLocalDate().toString() + "�����ע" + "]";
				}
				
				
				try {
					stockzdgz = stockzdgz + "[" + ycdate.toString() + "�Ƴ���ע" + "]";
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
			 filezdgz.getParentFile().mkdirs(); //Ŀ¼�����ڣ��ȴ���Ŀ¼
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
		//DRAWTEXT_FIX( NOT(CODELIKE('880')), PSTN_ULX_C2,PSTN_ULY+0*PSTN_STEP_ULY,0, ' ��������:������ҵ(17-04-05) ����(17-04-05) ZDY��ĿPPP(17-04-05)   ')    {};
		String part1 = "DRAWTEXT_FIX( NOT(CODELIKE('880')), PSTN_ULX_C2,PSTN_ULY+" ;
		String part2 = "*PSTN_STEP_ULY,0, ' ";
		String part3 = " ')    {}; "; 
		
		if(result.isEmpty()) //�б�Ϊ�գ��Զ�����ע�ʹ���
			return "{" + part1  + i + part2 + daleibankuai +" : " + result + part3;
		else 
			return part1  + i + part2 + daleibankuai +" : " + result + part3;
		
	}
	/*
	 * ��ҵ�� to ͨ���ű���
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
			cylreport.getParentFile().mkdirs(); //Ŀ¼�����ڣ��ȴ���Ŀ¼
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
				
				//�˴�Ӧ��д���ļ�����д��list
				try {
					Files.append(TDXFormatedOpt.formateToTDXWaiBuShuJuLine(stockcode,geguchanyelian),cylreport, charset);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		} catch (DocumentException e) {
			logger.debug(cylxml+ "���ڴ���");
			cylreport.delete();
			return null;
		}
		
		return cylreport.getParent();
	}
	/*
	 * �����ɷ�����¼��Ϣ���Ǵ��ڲ�����¼�ص��ע ��ġ� ���ڹ�ע��ʷ��ͨ���Զ�����ҳ����ġ�
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
			 filezdgz.getParentFile().mkdirs(); //Ŀ¼�����ڣ��ȴ���Ŀ¼
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
		 
		String sqlquerystat = "SELECT * FROM ������¼�ص��ע WHERE ���� > DATE_SUB( CURDATE( ) , INTERVAL( DAYOFWEEK( CURDATE( ) ) + 300 ) DAY )  ";
				
//			logger.debug(sqlquerystat);
		ResultSet rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		
		ArrayListMultimap<String ,String> treemap = ArrayListMultimap.create();
		try {
			while(rs.next()) {
				String formatedstockcode = rs.getString("��Ʊ����");
				if(formatedstockcode.trim().equals("000000")) // 000000����Ҫ����
					continue;
				
				java.sql.Date dateindb = rs.getDate("����");
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
		
		//����treemap
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
 * ���и��ɵĻ�����Ϣ
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
			filegnts.getParentFile().mkdirs(); //Ŀ¼�����ڣ��ȴ���Ŀ¼
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
		
		//���ɻ�����
		String sqlquerystat = "SELECT * FROM A�� ";
//		logger.debug(sqlquerystat);
		ResultSet rsgg = connectdb.sqlQueryStatExecute(sqlquerystat);
		if(rsgg == null)	{   
			logger.debug("��ȡ���ݿ�ʧ��");
			return null;
		}
		
		try {
			while(rsgg.next()) {
				String formatedstockcode = rsgg.getString("��Ʊ����");

				String result = "";
				if(!Strings.isNullOrEmpty(rsgg.getString("����������"))) 
					try {
						result = result + Strings.nullToEmpty( rsgg.getDate("����ʱ��").toString()) + rsgg.getString("����������");
					} catch (java.lang.NullPointerException e) {
						result = result + Strings.nullToEmpty("") + rsgg.getString("����������");
					}
				
				if(!Strings.isNullOrEmpty(rsgg.getString("ȯ����������") ) ) 
					try {
						result = result + Strings.nullToEmpty(rsgg.getDate("ȯ������ʱ��").toString()) + " " + rsgg.getString("ȯ����������");
					} catch (java.lang.NullPointerException e) {
						result = result + Strings.nullToEmpty("") + " " + rsgg.getString("ȯ����������");
					}
				if(!Strings.isNullOrEmpty(result)  ) {
					String lineformatedgainiantx = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedstockcode, result );
					try {
						Files.append(lineformatedgainiantx,filegnts, charset);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(!Strings.isNullOrEmpty(rsgg.getString("������Ϣ")) && !"null".equals(rsgg.getString("������Ϣ")) ) { //null����ʷ����
					CharSequence lineformatedfumianxx;
					try {
						 lineformatedfumianxx = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedstockcode,Strings.nullToEmpty(rsgg.getDate("������Ϣʱ��").toString()) + " " + rsgg.getString("������Ϣ"));
					} catch (java.lang.NullPointerException e) {
						 lineformatedfumianxx = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedstockcode,Strings.nullToEmpty("") + " " + rsgg.getString("������Ϣ"));
					}
					try {
						Files.append(lineformatedfumianxx,filefmxx, charset);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				String formatedzhengxiangguan = null;
				if( !Strings.isNullOrEmpty(rsgg.getString("����ؼ��ͻ�") ) || !Strings.isNullOrEmpty(rsgg.getString("�ͻ�")) ) {
					formatedzhengxiangguan = "����ؼ��ͻ�(#" + Strings.nullToEmpty(rsgg.getString("����ؼ��ͻ�"))
											 + " " + Strings.nullToEmpty(rsgg.getString("�ͻ�") ) + "#) " ;
				}
				
				String formatedfuxiangguan = null;
				if( !Strings.isNullOrEmpty(rsgg.getString("����ؼ���������") ) || !Strings.isNullOrEmpty(rsgg.getString("��������") ) ) {
					formatedfuxiangguan = "����ؼ���������(#" + Strings.nullToEmpty(rsgg.getString("����ؼ���������") )
											 + " " + Strings.nullToEmpty(rsgg.getString("��������") ) + "#) " ;
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
		
		
		//��������
		sqlquerystat = "SELECT * FROM ͨ���Ű���б� ";
//		logger.debug(sqlquerystat);
		ResultSet rsbk = connectdb.sqlQueryStatExecute(sqlquerystat);
		if(rsbk == null)	{   
			logger.debug("��ȡ���ݿ�ʧ��");
			return null;
		}
		
		try {
			while(rsbk.next()) {
				String formatedbkcode = rsbk.getString("���ID");

				String result = "";
				if(!Strings.isNullOrEmpty(rsbk.getString("����������"))) 
					try {
						result = result + Strings.nullToEmpty( rsbk.getDate("����ʱ��").toString()) + rsbk.getString("����������");
					} catch (java.lang.NullPointerException e) {
						result = result + Strings.nullToEmpty("") + rsbk.getString("����������");
					}
				
				if(!Strings.isNullOrEmpty(rsbk.getString("ȯ����������") ) ) 
					try {
						result = result + Strings.nullToEmpty(rsbk.getDate("ȯ������ʱ��").toString()) + " " + rsbk.getString("ȯ����������");
					} catch (java.lang.NullPointerException e) {
						result = result + Strings.nullToEmpty("") + " " + rsbk.getString("ȯ����������");
					}
				if(!Strings.isNullOrEmpty(result)  ) {
					String lineformatedgainiantx = formateToTDXWaiBuShuJuLine(formatedbkcode, result, "sh" );
					try {
						Files.append(lineformatedgainiantx,filegnts, charset);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(!Strings.isNullOrEmpty(rsbk.getString("������Ϣ")) && !"null".equals(rsbk.getString("������Ϣ")) ) { //null����ʷ����
					CharSequence lineformatedfumianxx;
					try {
						 lineformatedfumianxx = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedbkcode, Strings.nullToEmpty(rsbk.getDate("������Ϣʱ��").toString()) + " " + rsbk.getString("������Ϣ"), "sh");
					} catch (java.lang.NullPointerException e) {
						 lineformatedfumianxx = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedbkcode, Strings.nullToEmpty("") + " " + rsbk.getString("������Ϣ"), "sh");
					}
					try {
						Files.append(lineformatedfumianxx,filefmxx, charset);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				String formatedzhengxiangguan = null;
				if( !Strings.isNullOrEmpty(rsbk.getString("����ؼ��ͻ�") ) || !Strings.isNullOrEmpty(rsbk.getString("�ͻ�")) ) {
					formatedzhengxiangguan = "����ؼ��ͻ�(#" + Strings.nullToEmpty(rsbk.getString("����ؼ��ͻ�"))
											 + " " + Strings.nullToEmpty(rsbk.getString("�ͻ�") ) + "#) " ;
				}
				
				String formatedfuxiangguan = null;
				if( !Strings.isNullOrEmpty(rsbk.getString("����ؼ���������") ) || !Strings.isNullOrEmpty(rsbk.getString("��������") ) ) {
					formatedfuxiangguan = "����ؼ���������(#" + Strings.nullToEmpty(rsbk.getString("����ؼ���������") )
											 + " " + Strings.nullToEmpty(rsbk.getString("��������") ) + "#) " ;
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
