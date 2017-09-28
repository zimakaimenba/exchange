package com.exchangeinfomanager.tongdaxinreport;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;

public class TDXFormatedOpt {
	
	private static  Charset charset = Charset.forName("GBK") ;

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
	
	public static String formateToTDXWaiBuShuJuLine(String stockcode, String geguchanyelian)
	{
		return TDXFormatedOpt.formateStockCodeForTDX(stockcode) + "  |  " + geguchanyelian +  " | " + "1.000" + System.getProperty("line.separator");
	}
	/*
	 * 
	 */
	public static boolean parseZdgzBkToTDXCode(HashMap<String, ArrayList<BkChanYeLianTreeNode>> zdgzbkmap)
	{
		Iterator<String> bankuaidaleiname = zdgzbkmap.keySet().iterator();

		File tmpfilefoler = Files.createTempDir();
		File tongdaxinfile = new File(tmpfilefoler + "通达信重点关注代码.txt");
	
		
		boolean runresult = false;
		try	{
			if(tongdaxinfile.exists())
			{
				tongdaxinfile.delete();
				tongdaxinfile.createNewFile();
			}
			else
				tongdaxinfile.createNewFile();
		} catch(Exception e) {	
			e.printStackTrace();
		}
			
			int nullsteps =0 ; //通达信代码中，有内容的板块在前，无内容的板块自动被注释
			while(bankuaidaleiname.hasNext())
			{
				String siglebkname = bankuaidaleiname.next().toString() ;
		   		ArrayList<BkChanYeLianTreeNode> tmpgzbklist = zdgzbkmap.get(siglebkname);
		   		String result = "";
		   		try {
			   		for(BkChanYeLianTreeNode tmpgz:tmpgzbklist) {
			   			if(tmpgz.isOfficallySelected() ) {
			   				String chanyelian =  tmpgz.getNodeCurLocatedChanYeLianByName();
			        		String seltime = tmpgz.getSelectedToZdgzTime();
			        		result =  result + chanyelian + "(" + seltime +")" + "  |  ";
			   			}
		        	}
		   		} catch (Exception e) { 
		   			
		   		}

        		try {
					Files.append( formatedWholeContents(nullsteps,"股票池"+siglebkname,result) + System.getProperty("line.separator") ,tongdaxinfile,charset);
				} catch (IOException e) {
					e.printStackTrace();
				}
        		
        		if(!result.isEmpty()) {
        			nullsteps ++;
        		} 
			}
			runresult = true;

			try {
				String cmd = "rundll32 url.dll,FileProtocolHandler " + tongdaxinfile.getAbsolutePath();
				Process p  = Runtime.getRuntime().exec(cmd);
				p.waitFor();
			} catch (Exception e1) 
			{
				e1.printStackTrace();
			}
		
		return runresult;
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
	 * 
	 */
	public static String parseChanYeLianXmlToTDXReport () 
	{
		SystemConfigration sysconfig = SystemConfigration.getInstance();
		String cylxml = sysconfig.getGeGuChanYeLianXmlFile();
		String cylreportname = sysconfig.getTDXChanYeLianReportFile ();
		Element xmlroot;
    	SAXReader reader;
    	
    	File cylfile =   new File(cylxml );
    	File cylreport = new File(cylreportname);
		if(!cylfile.exists()) { 
			return null;
		} 
    	
    	FileInputStream xmlfileinput = null;
		try {
			xmlfileinput = new FileInputStream(cylxml);
		} catch (FileNotFoundException e) {
			return null;
		}
		
		try {
			if (cylreport.exists()) {
				cylreport.delete();
				cylreport.createNewFile();
			} else
				cylreport.createNewFile();
		} catch (Exception e) {
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
			System.out.println(cylxml+ "存在错误");
			return null;
		}
		
		return cylreport.getParent();
	}
/*
 * 
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
		} catch (Exception e) {
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
			return null;
		}
		
		String sqlquerystat = "SELECT * FROM A股 ";
		System.out.println(sqlquerystat);
		ResultSet rs = connectdb.sqlQueryStatExecute(sqlquerystat);
		if(rs == null)
		{   
			System.out.println("读取数据库失败");
			return null;
		}
		
		try {
			while(rs.next()) {
				String formatedstockcode = rs.getString("股票代码");

				String result = "";
				if(!Strings.isNullOrEmpty(rs.getString("概念板块提醒"))) 
					try {
						result = result + Strings.nullToEmpty( rs.getDate("概念时间").toString()) + rs.getString("概念板块提醒");
					} catch (java.lang.NullPointerException e) {
						result = result + Strings.nullToEmpty("") + rs.getString("概念板块提醒");
					}
				
				if(!Strings.isNullOrEmpty(rs.getString("券商评级提醒") ) ) 
					try {
						result = result + Strings.nullToEmpty(rs.getDate("券商评级时间").toString()) + " " + rs.getString("券商评级提醒");
					} catch (java.lang.NullPointerException e) {
						result = result + Strings.nullToEmpty("") + " " + rs.getString("券商评级提醒");
					}
				if(!Strings.isNullOrEmpty(result)  ) {
					String lineformatedgainiantx = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedstockcode, result );
					try {
						Files.append(lineformatedgainiantx,filegnts, charset);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(!Strings.isNullOrEmpty(rs.getString("负面消息")) && !"null".equals(rs.getString("负面消息")) ) { //null是历史遗留
					CharSequence lineformatedfumianxx;
					try {
						 lineformatedfumianxx = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedstockcode,Strings.nullToEmpty(rs.getDate("负面消息时间").toString()) + " " + rs.getString("负面消息"));
					} catch (java.lang.NullPointerException e) {
						 lineformatedfumianxx = TDXFormatedOpt.formateToTDXWaiBuShuJuLine(formatedstockcode,Strings.nullToEmpty("") + " " + rs.getString("负面消息"));
					}
					try {
						Files.append(lineformatedfumianxx,filefmxx, charset);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				String formatedzhengxiangguan = null;
				if( !Strings.isNullOrEmpty(rs.getString("正相关及客户") ) || !Strings.isNullOrEmpty(rs.getString("客户")) ) {
					formatedzhengxiangguan = "正相关及客户(#" + Strings.nullToEmpty(rs.getString("正相关及客户"))
											 + " " + Strings.nullToEmpty(rs.getString("客户") ) + "#) " ;
				}
				
				String formatedfuxiangguan = null;
				if( !Strings.isNullOrEmpty(rs.getString("负相关及竞争对手") ) || !Strings.isNullOrEmpty(rs.getString("竞争对手") ) ) {
					formatedfuxiangguan = "负相关及竞争对手(#" + Strings.nullToEmpty(rs.getString("负相关及竞争对手") )
											 + " " + Strings.nullToEmpty(rs.getString("竞争对手") ) + "#) " ;
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
		}
		
		return filegnts.getAbsolutePath();
		
	}
}
