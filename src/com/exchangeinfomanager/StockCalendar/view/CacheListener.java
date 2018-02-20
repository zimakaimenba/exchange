package com.exchangeinfomanager.StockCalendar.view;

@SuppressWarnings("all")
public interface CacheListener {

  void onMeetingChange(Cache cache);

  void onLabelChange(Cache cache);

}
