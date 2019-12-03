package com.exchangeinfomanager.News;

import java.awt.Color;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class News 
{
    private LocalDate starttime;
    private String title;
    private String description;
    private String keywords; //keywrods
    private String newsUrl;
    protected String newsownercodes;

    private Collection<InsertedNews.Label> labels;

    public News(String title, LocalDate starttime, String description, String keywords, 
    		Collection<InsertedNews.Label> labels,String newsUrl,String ownercodes)
    {
        this.setStart(starttime);
        this.setTitle(title);
        this.setDescription(description);
        this.setKeyWords(keywords);
        this.labels = new HashSet<>(labels);
        this.setNewsUrl(newsUrl);
        this.setNewsOwnerCodes(ownercodes);
    }
    

	public Collection<InsertedNews.Label> getLabels() {
        return this.labels;
    }

    public LocalDate getStart() {
        return starttime;
    }

    public void setStart(LocalDate start) {
        this.starttime = start;
    }


    public void setNewsUrl (String url)
    {
    	this.newsUrl = url;
    }
    public String getNewsUrl ()
    {
    	return this.newsUrl;
    }
    public void setNewsOwnerCodes (String ownercodes)
    {
  		this.newsownercodes = ownercodes;
    }
    public String getTitleWithUrl ()
    {
    	if(newsUrl != null && !newsUrl.isEmpty() )	    		
   			return "<a href=\" " +   newsUrl + "\"> " + title + "</a> ";
    	else
    		return this.title;
    }
    public String getNewsOwnerCodes ()
    {
    	return this.newsownercodes;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //keywords
    public String getKeyWords () {
        return keywords;
    }

    public void setKeyWords(String keywords) {
        this.keywords = keywords;
    }
    
    /*
     * 
     */
    public void removeNewsSpecficOwner (String removedowner) 
    {
    	if(newsownercodes.contains(removedowner)) {
    		newsownercodes = newsownercodes.replace(removedowner , "");
    		if(newsownercodes.contains("||"))
    			newsownercodes = newsownercodes.replace("||" , "|");
    		if(newsownercodes.equals("|"))
    			newsownercodes = "";
    	}
    }
    public void removeNewsSpecficOwner(Set<String> friendset) 
    {
    	for(String removedowner : friendset) {
    		if(newsownercodes.contains(removedowner)) {
        		newsownercodes = newsownercodes.replace(removedowner , "");
        		if(newsownercodes.contains("||"))
        			newsownercodes = newsownercodes.replace("||" , "|");
        		if(newsownercodes.equals("|"))
        			newsownercodes = "";
        	}
    	}
	}
    /*
     * 
     */
    public Boolean addNewsToSpecificOwner (String newowner)
    {
    	if(!newsownercodes.contains(newowner)) {
    		newsownercodes = newsownercodes + newowner + "|";
    		return true;
    	} else
    		return false;
    }
    public Boolean addNewsToSpecificOwner (Set<String> newowners)
    {
    	for(String newowner : newowners) {
    		if(!newsownercodes.contains(newowner)) {
        		newsownercodes = newsownercodes + newowner + "|";
        	} 
    	}
    	
    	return true;
    }
    @Override
    public String toString() {
        return String.format("title: %s, start: %s, end: %s, description: %s, keywords: %s", getTitle(), getStart(),
             getDescription(), getKeyWords());
    }
    

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        News News = (News) o;

        if (!starttime.equals(News.starttime))
            return false;
        if (!title.equals(News.title))
            return false;
        try{
        	if (!description.equals(News.description))
                return false;
        } catch( java.lang.NullPointerException e) {
        	return false;
        }
        
        if (!keywords.equals(News.keywords))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
    	int result = 0;

    			result = starttime.hashCode();
	            result = 31 * result + title.hashCode();
	            try{
	            	result = 31 * result + description.hashCode();
	            } catch (java.lang.NullPointerException e) {
//	        		e.printStackTrace();
	        		result = 31 * result + "".hashCode();
	        	}
	            try{
	            	result = 31 * result + keywords.hashCode();
	            } catch (java.lang.NullPointerException e) {
//	        		e.printStackTrace();
	        		result = 31 * result + "".hashCode();
	        	}
            
    	
        
        return result;
    }

    public static class Label  {

        private Color color;
        private String name;
        

        public Label(String name, Color color) {
            this.color = color;
            this.name = name;
        
        }

        
        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return String.format("name: %s, color: %s", getName(), getColor());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Label label = (Label) o;

            
            if (!color.equals(label.color))
                return false;
            if (!name.equals(label.name))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
        	
            int result = color.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
    }


}
