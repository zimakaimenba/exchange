package com.exchangeinfomanager.News;

import java.util.Collection;

public interface NewsCacheListener 
{
	void onNewsChange(Collection<NewsCache> caches);
	
	void onNewsChange(NewsCache cache);

	void onLabelChange(Collection<NewsCache> cache);
	
	void onLabelChange(NewsCache cache);
	  
	void onNewsAdded (News m);

}
