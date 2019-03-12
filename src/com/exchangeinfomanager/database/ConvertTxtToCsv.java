package com.exchangeinfomanager.database;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

//import java.io.*;

public class ConvertTxtToCsv 
{
	private SystemConfigration sysconfig;
	private AllCurrentTdxBKAndStoksTree allbksks;
	private BanKuaiAndChanYeLian2 bkcyl;
	private BanKuaiDbOperation bkdbopt;

	public ConvertTxtToCsv() 
	{
//		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
//		this.bkcyl = BanKuaiAndChanYeLian2.getInstance();
//		this.sysconfig = SystemConfigration.getInstance();
////		this.bkdbopt = new BanKuaiDbOperation ();
		
//		convert ();
		
//		System.out.println("Finished");
//		final Path path = Paths.get("path", "to", "folder");
//	    final Path txt = path.resolve("myFile.txt");
//	    final Path csv = path.resolve("myFile.csv");
//	    try (
//	            final Stream<String> lines = Files.lines(txt);
//	            final PrintWriter pw = new PrintWriter(Files.newBufferedWriter(csv, StandardOpenOption.CREATE_NEW))) {
//	        	lines.map((line) -> line.split("\\|")).map((line)-> Stream.of(line).collect(Collectors.joining(","))).forEach(pw::println); 
//	    }
	}

	public void convertTxtFileToCsv(String txtfilepathname, String csvpathname) 
	{
		Read read = new Read(txtfilepathname);
		Print print = new Print();
//		for (int i = 1; i <= 3; i++) {
			try{
				File csvfile = new File(csvpathname);
				if(csvfile.exists())
					csvfile.delete();
				
				print.setFileName(csvpathname);
				read.setPrint(print);
				read.exec(1);
			} catch(IOException ex){
				ex.printStackTrace();
			}
//		}
		
	}
	
	
}

class Print 
{

	protected String fileName;

	protected FileWriter writer;

	public Print() {}

	public Print(String fileName) throws IOException {
		this.fileName = fileName;
		this.writer = new FileWriter(this.fileName);
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) throws IOException {
		this.fileName = fileName;
		this.writer = new FileWriter(this.fileName);
	}

	public void close() throws IOException {
		this.writer.flush();
		this.writer.close();
	}

	public void addRow(String[] c) throws IOException {
		int l = c.length;
		for(int i = 0; i < l; i++) {
			this.writer.append(c[i]);
			if(i != (l - 1)) {
				this.writer.append(",");
			}
		}
		this.writer.append('\n');
	}
}

class Read 
{

	protected String fileName;

	protected BufferedReader bufferedReader;

	protected Print print;

	public Read(String fileName) {
		this.fileName = fileName;
	}

	public Read(Print print, String fileName) {
		this.print = print;
		this.fileName = fileName;
	}

	public void setPrint(Print print) {
		this.print = print;
	}

	public Print getPrint() {
		return this.print;
	}

	public void exec(Integer type) 
	{
		String sCurrentLine = "";
		try{
			this.bufferedReader = new BufferedReader(
				new FileReader(this.fileName));

			while((sCurrentLine = this.bufferedReader.readLine()) != null) {
				String[] columns = sCurrentLine.split("\t");
				int length = columns.length;
				if(type == 1) {
					this.print.addRow(columns);
				} else if(length >= 2){
					try {
						double col1 = Double.parseDouble(columns[1]);
						if(col1 > 20){
							if(type == 2){
								this.print.addRow(columns);
							} else if(type == 3 && length >= 3 && columns[2] != null){
								this.print.addRow(columns);
							}
						}
					} catch (java.lang.NumberFormatException e) {
						e.printStackTrace();
					}
					
				}
			}
			this.print.close();

		} catch(IOException ex) {
			ex.printStackTrace();
		} finally {
			try{
				if(this.bufferedReader != null) {
					this.bufferedReader.close();
				}
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
