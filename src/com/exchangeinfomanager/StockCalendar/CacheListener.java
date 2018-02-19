package com.exchangeinfomanager.StockCalendar;

@SuppressWarnings("all")
public interface CacheListener {

  void onMeetingChange(Cache cache);

  void onLabelChange(Cache cache);

}
