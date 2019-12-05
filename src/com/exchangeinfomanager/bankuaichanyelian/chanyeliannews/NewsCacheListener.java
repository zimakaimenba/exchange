package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;

import java.util.Collection;

import com.exchangeinfomanager.News.NewsCache;

@SuppressWarnings("all")
public interface CacheListener {

  void onMeetingChange(Collection<NewsCache> caches);

  void onLabelChange(Cache cache);
  
  void onMeetingAdded (Meeting m);

}
