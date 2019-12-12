package com.exchangeinfomanager.StockCalendar;



import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsCacheListener;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.guifactory.DialogFactory;
import com.toedter.calendar.JDayChooser;



import javax.swing.*;

import org.jsoup.Jsoup;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;


/**
 * Base class of the views. Each calendar view (month, year... etc) should extend this class
 * and provide update (consumeMeetings) method implementation.
 */
@SuppressWarnings("all")
public abstract class View extends JPanel implements NewsCacheListener 
{

    // create and modify meeting dialogs, which user uses to interact with the program
//    private MeetingDialog createDialog;
//    private MeetingDialog modifyDialog;

    // each view needs data (meetings) to display
    protected Collection<NewsCache>  caches;

    // date of the view
    private LocalDate date = LocalDate.now();
    
    /**
     * Constructs view with providedd meeting service and a cache
     *
     * @param meetingService used by meeting dialogs
     * @param cache used by the views
     */
    public View(Collection<ServicesForNews> meetingServices) {
    	
    	caches = new HashSet<> ();
    	for (Iterator<ServicesForNews> lit = meetingServices.iterator(); lit.hasNext(); ) {
    		ServicesForNews f = lit.next();
    		this.caches.add( f.getCache() );
    	}
    }
    public View(ServicesForNews meetingServices) 
    {
    	caches = new HashSet<> ();
    	caches.add( meetingServices.getCache() );
    }
    
    /**
     * Accessor for the create meeting dialog.
     *
     * @return meeting dialog
     */
//    protected MeetingDialog getCreateDialog() {
//    	this.createDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
//        return this.createDialog;
//    }
//
//    /**
//     * Accessor for the modify meeting dialog.
//     *
//     * @return meeting dialog
//     */
//    protected MeetingDialog getModifyDialog() {
//    	this.modifyDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
//        return this.modifyDialog;
//    }

    /**
     * Accessor for the meeting cache.
     *
     * @return cache
     */
    protected  Collection<NewsCache> getCaches() {
        return this.caches;
    }

    /**
     * Sets date of the view. Everytime date is changed the
     * view is updated with new data.
     *
     * @param date to be set
     */
    public void setDate(LocalDate date) {
        this.date = date;
        this.onNewsChange(this.caches);
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

    protected String getLabelToolTipText (News m)
    {
    	return m.getTitle() + "\n \n" + m.getDescription();
    }

}
