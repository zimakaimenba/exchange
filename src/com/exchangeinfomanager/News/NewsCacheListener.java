package com.exchangeinfomanager.News;

public interface NewsCacheListener 
{
	void onNewsChange(NewsCache cache);

	void onLabelChange(NewsCache cache);
	  
	void onNewsAdded (News m);
}
