package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.Services.ServicesOfNodeJiBenMianInfo;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.CacheForInsertedTag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class DisplayNodesRelatedTagsServices implements ServicesOfNodeJiBenMianInfo 
{
	private BkChanYeLianTreeNode node;

	public DisplayNodesRelatedTagsServices( BkChanYeLianTreeNode node)
	{
		this.node = node;
	}

	@Override
	public String getRequiredHtmlInfo() 
	{
		TagsServiceForNodes lbnodedbservice = new TagsServiceForNodes (this.node);
		CacheForInsertedTag bkstkkwcache = new CacheForInsertedTag (lbnodedbservice);
		lbnodedbservice.setCache(bkstkkwcache);
		
//		TagsServiceForNodes svsnodetags = new TagsServiceForNodes (this.node);
		Collection<Tag> tags = null;
		tags = bkstkkwcache.produceTags();
		
		String htmlstring = "";
		org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		org.jsoup.select.Elements content = doc.select("body");
		 
		if(!tags.isEmpty() ) {
			String tagstr = "";
			for (Iterator<Tag> it = tags.iterator(); it.hasNext(); ) {
				Tag nodeonetag = it.next();
	   			tagstr = tagstr + "'" + nodeonetag.getName() + "'"; 
			}
			
			content.append( "<h4>" + node.getMyOwnCode() + " " + node.getMyOwnName() + "µÄ¹Ø¼ü´Ê:" + "</h4>");
			content.append( "<p>" + tagstr + "</p> ");
		}

		
	   	htmlstring = doc.toString();

	   	return htmlstring;
	}

}
