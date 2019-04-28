package com.exchangeinfomanager.StockCalendar;



import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CacheListener;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DialogFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.MeetingDialog;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.MeetingService;
import com.toedter.calendar.JDayChooser;



import javax.swing.*;

import org.jsoup.Jsoup;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;


/**
 * Base class of the views. Each calendar view (month, year... etc) should extend this class
 * and provide update (consumeMeetings) method implementation.
 */
@SuppressWarnings("all")
public abstract class View extends JPanel implements CacheListener 
{

    // create and modify meeting dialogs, which user uses to interact with the program
    private MeetingDialog createDialog;
    private MeetingDialog modifyDialog;

    // each view needs data (meetings) to display
    protected Cache cache;

    // date of the view
    private LocalDate date = LocalDate.now();
    
    /**
     * Constructs view with providedd meeting service and a cache
     *
     * @param meetingService used by meeting dialogs
     * @param cache used by the views
     */
    public View(MeetingService meetingService, Cache cache) {
        this.cache = cache;
        this.createDialog = DialogFactory.createMeetingDialog(meetingService, cache);
        this.modifyDialog = DialogFactory.modifyMeetingDialog(meetingService, cache);
    }
    
//    public View(MeetingService meetingService,LabelService labelService, Cache cache) {
//    	this.cache = cache;
//        this.createDialog = DialogFactory.createMeetingDialog(meetingService, cache);
//        this.modifyDialog = DialogFactory.modifyMeetingDialog(meetingService, cache);
//    }

    /**
     * Accessor for the create meeting dialog.
     *
     * @return meeting dialog
     */
    protected MeetingDialog getCreateDialog() {
        return this.createDialog;
    }

    /**
     * Accessor for the modify meeting dialog.
     *
     * @return meeting dialog
     */
    protected MeetingDialog getModifyDialog() {
        return this.modifyDialog;
    }

    /**
     * Accessor for the meeting cache.
     *
     * @return cache
     */
    protected Cache getCache() {
        return this.cache;
    }

    /**
     * Sets date of the view. Everytime date is changed the
     * view is updated with new data.
     *
     * @param date to be set
     */
    public void setDate(LocalDate date) {
        this.date = date;
        this.onMeetingChange(this.cache);
    }


    /**
     * Accessor for date of the view
     *
     * @return date
     */
    public LocalDate getDate() {
        return this.date;
    }

    /**
     * Returns colour of the meeting based on meeting id.
     * If id is divisible by 6 then Orange will be returned
     * If id is divisible by 5 then Violet will be returned,
     * and so on...
     *
     * @param id to be used
     * @return color of the meeting
     */
    protected Color getMeetingColor(int id) {
//        Color color = null;
//        if (Integer.valueOf(id) % 6 == 0)
//            color = ColorScheme.ORANGE_LIGHT;
//        else if (Integer.valueOf(id) % 5 == 0)
//            color = ColorScheme.VIOLET_LIGHT;
//        else if (Integer.valueOf(id) % 4 == 0)
//            color = ColorScheme.PINK_LIGHT;
//        else if (Integer.valueOf(id) % 3 == 0)
//            color = ColorScheme.GREEN_LIGHT;
//        else if (Integer.valueOf(id) % 2 == 0)
//            color = ColorScheme.ORANGE_DARK;
//        else
//            color = ColorScheme.PINK_DARK;
//        return color;
    	return Color.BLACK;
    }

    protected String getLabelToolTipText (InsertedMeeting m)
    {
    	return m.getTitle() + "\n \n" + m.getDescription();
    	//在tooltips显示当前有几个已经添加的条件及内容
//		String htmlstring = "";
//		org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
//		org.jsoup.select.Elements content = doc.select("body");
//		content.append(m.getTitle());
//		content.append("<br>");
//		content.append(m.getDescription());
//		htmlstring = doc.toString();
//		
//		return htmlstring; 
    }

}
