package com.exchangeinfomanager.bankuaifengxi.Gephi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;

import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.opencsv.CSVWriter;

public class GephiFilesGenerator 
{
	public GephiFilesGenerator (AllCurrentTdxBKAndStoksTree bkcyl2)
	{
		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
		this.bkcyl = bkcyl2;
		
		preSetupOfGephiFiles ();
	}
	private SystemConfigration sysconfig;
	private BanKuaiDbOperation bkdbopt;
	private AllCurrentTdxBKAndStoksTree bkcyl;
	List<String[]> nodecontentArrayList;
	private List<String[]> edgecontentArrayList;
	private LocalDate recorddate;
	/*
	 * 
	 */
	private void preSetupOfGephiFiles() 
	{
		String[] nodefiletitlestrng = {"id","label","nodetype","amount","zhangfu"};
		String[] titlestrng = {"source","target","type","weight"};
		
		nodecontentArrayList = new ArrayList<>();
//		nodecontentArrayList.add(nodefiletitlestrng);
		
		edgecontentArrayList = new ArrayList<>();
//		edgecontentArrayList.add(edgetitlestrng);
	}
	/*
	 * 
	 */
	public File generatorGephiFile (HashSet<String> requiredcylnodeset,LocalDate requiredrecordsday,String period)
	{
		this.recorddate = requiredrecordsday.with(DayOfWeek.FRIDAY);
	
		writeBanKuaiAndGeGuFiles (requiredcylnodeset,requiredrecordsday,period);
		
		File filegephinode = saveNodesFile ();
		File filegephiedge = saveEdgesFile ();
		
		return filegephiedge;
	}
	/*
	 * 
	 */
	public File generatorGephiFile (String requiredcylnodesetfilepath,LocalDate requiredrecordsday,String period)
	{
		File parsefile = new File(requiredcylnodesetfilepath);
    	if(!parsefile.exists() )
    		return null;
    	
    	List<String> readparsefileLines = null;
		try {
			readparsefileLines = Files.readLines(parsefile,Charsets.UTF_8,new ParseBanKuaiFielProcessor ());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			String filedate = requiredcylnodesetfilepath.replaceAll("[^-?0-9]+", " ").trim(); 
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			this.recorddate = LocalDate.parse(filedate,formatter);
		} catch (Exception e) {
			this.recorddate = requiredrecordsday;
		}

		
		HashSet<String> requiredcylnodeset = new HashSet<String> (readparsefileLines);
		
		writeBanKuaiAndGeGuFiles (requiredcylnodeset,requiredrecordsday,period);
		
		File filegephinode = saveNodesFile ();
		File filegephiedge = saveEdgesFile ();
		
		return filegephiedge;
	}
	/*
	 * 
	 */
	public File generatorGephiFile (File parsefile,LocalDate requiredrecordsday,String period)
	{
		List<String> readparsefileLines = null;
		try {
			readparsefileLines = Files.readLines(parsefile,Charsets.UTF_8,new ParseBanKuaiFielProcessor ());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.recorddate = requiredrecordsday;
		
		HashSet<String> requiredcylnodeset = new HashSet<String> (readparsefileLines);
		
		writeBanKuaiAndGeGuFiles (requiredcylnodeset,requiredrecordsday,period);
		
		File filegephinode = saveNodesFile ();
		File filegephiedge = saveEdgesFile ();
		
		return filegephiedge;
	
	}
	/*
	 * 
	 */
	private String[] formateGephiNodeFileRecord (BkChanYeLianTreeNode cylnode)
	{
		String nodecode = cylnode.getMyOwnCode();
		String nodename = cylnode.getMyOwnName();

		if (cylnode.getType() == BkChanYeLianTreeNode.TDXBK) {
			String[] ss = {nodecode, nodename, "板块", "", ""};
			return ss;
		}	else{
			String[] ss = {nodecode,nodename,"个股","",""};
			return ss;
		}
	}	
	/*
	 * 
	 */
	private void writeBanKuaiAndGeGuFiles (HashSet<String> requiredcylnodeset,LocalDate requiredrecordsday,String period)
	{
		HashMap<String, String> tmpggbkset = new HashMap<String, String> ();
		for (String nodecode : requiredcylnodeset) {
//			if(nodecode.trim().length() ==7) //直接从板块分析导出条件过来的数据，里面板块代码是7位的，要截取。
//				nodecode = nodecode.substring(1);
			
			BkChanYeLianTreeNode cylnode ;
			cylnode = this.bkcyl.getBanKuai(nodecode, requiredrecordsday, period);
			if(cylnode != null) {
				if( ((BanKuai)cylnode).isExporttogehpi())
					tmpggbkset.put(cylnode.getMyOwnCode(), cylnode.getMyOwnName());
//				listofcsv.add(formateGephiNodeFileRecord(cylnode));
			} else {
				cylnode = this.bkcyl.getStock(nodecode, requiredrecordsday, period);
				nodecontentArrayList.add(formateGephiNodeFileRecord(cylnode));
				
				//个股属于的板块也要加
				cylnode = bkdbopt.getTDXBanKuaiForAStock ((Stock)cylnode); //通达信板块信息
				HashMap<String, String> tdxbk = ((Stock)cylnode).getGeGuCurSuoShuTDXSysBanKuaiList();
				for(Iterator<Map.Entry<String, String>> it = tdxbk.entrySet().iterator(); it.hasNext(); ) {
				      Map.Entry<String, String> entry = it.next();
				      String bkcode = entry.getKey();
				      String bkname = entry.getValue();
						
				      BanKuai bk = this.bkcyl.getBanKuai(bkcode, requiredrecordsday, period);
				      if(!((BanKuai)bk).isExporttogehpi()) {
				    	  it.remove();
				      } else {
				    	  String[] ss = {nodecode,bkcode,"Directed",""};
					      edgecontentArrayList.add(ss);  
				      }
			    }
				
				tmpggbkset.putAll(tdxbk);
			}
		}
		//把个股的所属板块也加到node文件里面
		for (Map.Entry<String, String> entry : tmpggbkset.entrySet()) {
			String nodecode = entry.getKey();
			String nodename = entry.getValue();
			String[] ss = {nodecode,nodename,"板块","",""};
			nodecontentArrayList.add(ss);
		}
		
		tmpggbkset = null;
	}
	/*
	 * 
	 */
	private File saveEdgesFile() 
	{
		String parsedpath = sysconfig.getGephiFileExportPath ();
		String bkggedgecsvfile = parsedpath + "板块个股GEPHI对应关系edegs" 
							+ this.recorddate.toString().replace("-", "")
							+ ".csv"
							;
		
		File csvfile = new File(bkggedgecsvfile);
		try	{
			if(csvfile.exists()) {
				csvfile.delete();
				csvfile.createNewFile();
			} else
				csvfile.createNewFile();
		} catch(Exception e) {	
			e.printStackTrace();
		}
		
		try (
				Writer writer = java.nio.file.Files.newBufferedWriter(Paths.get(bkggedgecsvfile));
				CSVWriter csvWriter = new CSVWriter(writer,
	                    CSVWriter.DEFAULT_SEPARATOR,
	                    CSVWriter.NO_QUOTE_CHARACTER,
	                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
	                    CSVWriter.DEFAULT_LINE_END);
	        ) {
				String[] edgetitlestrng = {"source","target","type","weight"};
	            csvWriter.writeNext(edgetitlestrng);
	            
	            csvWriter.writeAll(edgecontentArrayList);	
	     } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		CSVWriter writer = null;  
//		String  characters = ",";
//	    try {
//		    if (",".equalsIgnoreCase(characters)){
//		    	//初始化CSVWriter
//		        writer = new CSVWriter(new FileWriter(csvfile),',',CSVWriter.NO_QUOTE_CHARACTER);  
//		    } else{
//		    	//初始化CSVWriter，带分隔符
//		        writer = new CSVWriter(new FileWriter(csvfile),characters.toCharArray()[0],CSVWriter.NO_QUOTE_CHARACTER);  
//		    }
//	    } catch (IOException e) {  
//	        e.printStackTrace();  
//	    }
//
//        try {
//    	    writer.writeAll(edgecontentArrayList);
////    	    writer.writeAll(listofcsv);
//			writer.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
        return  csvfile;
	}
	private File saveNodesFile() 
	{
		String parsedpath = sysconfig.getGephiFileExportPath ();
		String bkggnodecsvfile = parsedpath + "板块分析Nodes列表" 
							+ this.recorddate.toString().replace("-", "")
							+ ".csv"
							;
		
		File nodecsvfile = new File(bkggnodecsvfile);
		try	{
			if(nodecsvfile.exists()) {
				nodecsvfile.delete();
				nodecsvfile.createNewFile();
			} else
				nodecsvfile.createNewFile();
		} catch(Exception e) {	
			e.printStackTrace();
		}
		
		try (
				Writer writer = java.nio.file.Files.newBufferedWriter(Paths.get(bkggnodecsvfile));
				CSVWriter csvWriter = new CSVWriter(writer,
	                    CSVWriter.DEFAULT_SEPARATOR,
	                    CSVWriter.NO_QUOTE_CHARACTER,
	                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
	                    CSVWriter.DEFAULT_LINE_END);
	        ) {
				String[] nodefiletitlestrng = {"id","label","nodetype","amount","zhangfu"};
	            csvWriter.writeNext(nodefiletitlestrng);
	            
	            csvWriter.writeAll(nodecontentArrayList);	
	     } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		
//		CSVWriter writer = null;  
//		String  characters = ",";
//	    try {
//		    if (",".equalsIgnoreCase(characters)){
//		    	//初始化CSVWriter
//		        writer = new CSVWriter(new FileWriter(nodecsvfile),',',CSVWriter.NO_QUOTE_CHARACTER);  
//		    } else{
//		    	//初始化CSVWriter，带分隔符
//		        writer = new CSVWriter(new FileWriter(nodecsvfile),characters.toCharArray()[0],CSVWriter.NO_QUOTE_CHARACTER);  
//		    }
//	    } catch (IOException e) {  
//	        e.printStackTrace();  
//	    }
//
//	    try {
//		    writer.writeAll(nodecontentArrayList);
////		    writer.writeAll(listofcsv); 
//			writer.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	    
	    return nodecsvfile;
		
	}
}



///*
// * google guava files 类，可以直接处理读出的line
// */
class ParseBanKuaiFielProcessor implements LineProcessor<List<String>> 
{
   
    private List<String> stocklists = Lists.newArrayList();
   
    @Override
    public boolean processLine(String line) throws IOException {
    	if(line.trim().length() ==7)
    		stocklists.add(line.substring(1));
        return true;
    }
    @Override
    public List<String> getResult() {
        return stocklists;
    }
}