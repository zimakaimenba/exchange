package com.exchangeinfomanager.labelmanagement;

import java.awt.Color;
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

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.google.common.base.Splitter;
import com.lc.nlp.keyword.algorithm.TextRank;

public class TagsUrlOperation 
{
	private HashSet kwset;
	

	public TagsUrlOperation ()
	{
		
	}
	
	
	public Collection<Tag> getTagsFromURL(Set<String> URL) throws SQLException 
	{
        Collection<Tag> labels = new HashSet<>();
        
        kwset = new HashSet<> ();
        
        for(String tmpurl : URL) {
        		labels.addAll( this.getTagsFromURL (tmpurl) );
        }
        
        kwset = null;
        return labels;
	}

	private Collection<? extends Tag> getTagsFromURL(String url)
	{
		Collection<Tag> labels = new HashSet<>();
		
		TextRank.setKeywordNumber(8);
		TextRank.setWindowSize(6);
		
		URL slkurl;
		String contentType = null;
		try {
			slkurl = new URL(url.trim() );
			HttpURLConnection connection = (HttpURLConnection)  slkurl.openConnection();
			connection.setRequestMethod("HEAD");
			connection.connect();
			contentType = connection.getContentType();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(contentType.toUpperCase().contains("PDF"))
			labels.add ( this.getTagsFromPDFFile ( url) );
		else
		if(contentType.toUpperCase().contains("HTML"))
			labels.addAll ( this.getTagsFromHTML ( url) );
	
        return labels;
	}
	private Tag getTagsFromPDFFile(String url)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private  Collection<Tag> getTagsFromHTML(String url) 
	{
		Collection<Tag> labels = new HashSet<>();
		
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			String title = doc.title();
			String body = doc.body().text ();
			
//			Elements elements = doc.body().select("[href]");
//			for (Element element : elements) {
//			    String contentType1 = new URL(element.attr("href")).openConnection().getContentType();
//			}
			
			List<String> keywords = TextRank.getKeyword(title, body);
	        for(String tmpkwname : keywords) {
	        	if(  !kwset.contains(tmpkwname) && !tmpkwname.equals("¹Ø¼ü´Ê")) {
	        		kwset.add(tmpkwname);
	        		
	        		Tag tag = new Tag (tmpkwname, Color.GRAY);
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
