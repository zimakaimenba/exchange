package com.exchangeinfomanager.labelmanagement;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Splitter;
import com.lc.nlp.keyword.algorithm.TextRank;

import top.dadagum.extractor.utils.FileExtractUtil;

public class TagsUrlOperation 
{
	public TagsUrlOperation ()
	{
		
	}
	
	
	public Collection<Tag> getTagsFromURL(Set<String> URL) throws SQLException 
	{
        Collection<Tag> labels = new HashSet<>();
        
        for(String tmpurl : URL) {
        	if(tmpurl.contains("http"))
        		labels.addAll( this.getTagsFromURL (tmpurl) ); 
        	else
        		labels.addAll( this.getTagsFromReadingFiles (tmpurl) );
        }
        
        return labels;
	}
	private Collection<? extends Tag> getTagsFromReadingFiles(String path) 
	{
		Collection<Tag> labels = new HashSet<>();
		
		try { 
	        String body = FileExtractUtil.extractString(path);
	        
	        TextRank.setKeywordNumber(80);
			TextRank.setWindowSize(80);
			List<String> keywords = TextRank.getKeyword(path, body);
	        for(String tmpkwname : keywords) {
	        	if( !tmpkwname.equals("关键词")) {
	        		Tag tag = new Tag (tmpkwname, Color.WHITE);
	    	        labels.add(tag);
	        	}
	        }
	        
		} catch (IOException | OpenXML4JException | XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}
		return labels;
	}
	private Collection<? extends Tag> getTagsFromURL(String url)
	{
		Collection<Tag> labels = new HashSet<>();
				
		URL slkurl;
		String contentType = null;
		try {
			slkurl = new URL(url.trim() );
			HttpURLConnection connection = (HttpURLConnection)  slkurl.openConnection();
			connection.setRequestMethod("HEAD");
			connection.connect();
			contentType = connection.getContentType();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(contentType.toUpperCase().contains("HTML"))
			labels.addAll ( this.getTagsFromHTML ( url) );
		else {//是文件
			String file = this.downloadFileToLocal (url);
			labels.addAll (this.getTagsFromReadingFiles (file) );
		}
	
        return labels;
	}
	/*
	 * 
	 */
	private   String downloadFileToLocal(String url) 
	{
		File savedfile = null;
		String savedfilename = null;
		URL URLink;
		try {
			URLink = new URL( url); //"http://quotes.money.163.com/service/chddata.html?code=0601857&start=20071105&end=20150618&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP"
			
//			System.out.println(FilenameUtils.getBaseName(URLink.getPath())); // -> file
//	        System.out.println(FilenameUtils.getExtension(URLink.getPath())); // -> xml
			savedfilename = SystemConfigration.getInstance().getYanJiuBaoGaoDownloadedFilePath () + FilenameUtils.getName(URLink.getPath()); // -> file.xml
	        
			savedfile = new File (savedfilename);
			if(savedfile.exists())
				savedfile.delete();
			
			FileUtils.copyURLToFile(URLink, savedfile,10000,10000); //http://commons.apache.org/proper/commons-io/javadocs/api-2.4/org/apache/commons/io/FileUtils.html#copyURLToFile(java.net.URL,%20java.io.File)
		} catch (java.net.SocketTimeoutException e)  {
			e.printStackTrace();
		} catch ( IOException e) {
			e.printStackTrace();
		}finally {
			URLink = null;
		}
		
		return savedfilename;
		
	}
	private  Collection<Tag> getTagsFromHTML(String url) 
	{
		Collection<Tag> labels = new HashSet<>();
		
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			String title = doc.title();
			String body = doc.body().text ();
		
			TextRank.setKeywordNumber(80);
			TextRank.setWindowSize(80);
			List<String> keywords = TextRank.getKeyword(title, body);
	        for(String tmpkwname : keywords) {
	        	if( !tmpkwname.equals("关键词")) {
	        		Tag tag = new Tag (tmpkwname, Color.WHITE);
	    	        labels.add(tag);
	        	}
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return labels;
	}

}
