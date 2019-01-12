package com.exchangeinfomanager.commonlib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.exchangeinfomanager.asinglestockinfo.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.opencsv.CSVWriter;

public class ExportToCsvFile {

	public ExportToCsvFile ()
	{
		this.sysconfig = SystemConfigration.getInstance();
//		this.bkdbopt = new BanKuaiDbOperation ();
		
//		nodecontentArrayList = new ArrayList<>();
	}
	private SystemConfigration sysconfig;
//	private BanKuaiDbOperation bkdbopt;
//	private AllCurrentTdxBKAndStoksTree bkcyl;
	
	
	List<String[]> nodecontentArrayList;
	String[] nodecsvfileheader ;
	private String csvparsedpath;
	/*
	 * 
	 */
	private void setCsvFileHeader (String[] tmpheader)
	{
		this.nodecsvfileheader = tmpheader;
	}
	/*
	 * 
	 */
	private void setCsvFileContent (	List<String[]> tmpnodecontentArrayList)
	{
		this.nodecontentArrayList = tmpnodecontentArrayList;
	}
	/*
	 * 
	 */
	public File exportToCsvFile (String filepathname,String[] tmpheader,List<String[]> tmpnodecontentArrayList)
	{
		this.csvparsedpath = filepathname;
		this.nodecsvfileheader = tmpheader;
		this.nodecontentArrayList = tmpnodecontentArrayList;
		File csvfile = exportToCsvFile ();
		return csvfile;
	}
	/*
	 * 
	 */
	private File exportToCsvFile() 
	{
//		String parsedpath = this.sysconfig.getNodeExportCsvFilePath ();
//		String csvfilepath = parsedpath + "信息导出" 
//							+ LocalDate.now().toString().replace("-", "")
//							+ ".csv"
//							;
		
		File csvfile = new File(csvparsedpath);
		try	{
			if(csvfile.exists()) {
				csvfile.delete();
				csvfile.createNewFile();
			} else
				csvfile.createNewFile();
		} catch(Exception e) {	
			csvfile = null;
			e.printStackTrace();
		}
		
		try (
				Writer writer = java.nio.file.Files.newBufferedWriter(Paths.get(csvparsedpath));
				CSVWriter csvWriter = new CSVWriter(writer,
	                    CSVWriter.DEFAULT_SEPARATOR,
	                    CSVWriter.NO_QUOTE_CHARACTER,
	                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
	                    CSVWriter.DEFAULT_LINE_END);
	        ) {
//			http://webcache.googleusercontent.com/search?q=cache:2leHYW9cErsJ:www.chongchonggou.com/g_864266122.html+&cd=15&hl=zh-CN&ct=clnk
//				OutputStreamWriter output = new OutputStreamWriter(entityStream, StandardCharsets.UTF_8);
//				output.write(0xFEFF); //加上BOM
//				final CSVWriter csvWriter = new CSVWriter(output);
				
	            csvWriter.writeNext(this.nodecsvfileheader);
	            
	            csvWriter.writeAll(this.nodecontentArrayList);	
	     } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.nodecsvfileheader = null;
			this.nodecontentArrayList = null;
		}
		
//		FileOutputStream in = null;  
//		   BufferedOutputStream dis = null;
//		
//			    try {
//					in = new FileOutputStream(csvfile);
//					dis = new BufferedOutputStream(in);
//					OutputStreamWriter output = new OutputStreamWriter(dis, StandardCharsets.UTF_8);
//					output.write("\ufeff");
//					
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}  
			    
		
		
        return  csvfile;
	}
}


//
/////*
//// * google guava files 类，可以直接处理读出的line
//// */
//class ParseBanKuaiFielProcessor implements LineProcessor<List<String>> 
//{
//   
//    private List<String> stocklists = Lists.newArrayList();
//   
//    @Override
//    public boolean processLine(String line) throws IOException {
//    	if(line.trim().length() ==7)
//    		stocklists.add(line.substring(1));
//        return true;
//    }
//    @Override
//    public List<String> getResult() {
//        return stocklists;
//    }
//}
