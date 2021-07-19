package com.exchangeinfomanager.StockCalendar;

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;

import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.toedter.calendar.IDateEditor;
import com.toedter.calendar.JDateChooser;


public class JStockCalendarDateChooser extends JLocalDateChooser //implements PopupMenuListener
{
	private StockCalendar stockcal;
	private LocalDate lastselecteddate;
	
	public JStockCalendarDateChooser (StockCalendar jcal)
	{
       this(jcal,null,null,null);
       this.stockcal = jcal;
       
       datechangelisteners = new HashSet<>();
       this.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
		    public void propertyChange(PropertyChangeEvent e) {
		    	if("date".equals(e.getPropertyName() ) ) {
		    		JDateChooser wybieraczDat = (JDateChooser) e.getSource();
		    		LocalDate newdate = wybieraczDat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		    		if( (lastselecteddate == null) || ( !newdate.isEqual( lastselecteddate) ) ) {
		    			datechangelisteners.forEach(l -> l.dateChanged(newdate) );
		    			lastselecteddate = newdate;
//		    		}
		    	}
		    }
		});
	}
	
	private ChangeListener changeListener;
	
	/**
	 * Creates a new JDateChooser.
	 * 
	 * @param jcal
	 *            the JCalendar to be used
	 * @param date
	 *            the date or null
	 * @param dateFormatString
	 *            the date format string or null (then MEDIUM Date format is
	 *            used)
	 * @param dateEditor
	 *            the dateEditor to be used used to display the date. if null, a
	 *            JTextFieldDateEditor is used.
	 */
	private JStockCalendarDateChooser(StockCalendar jcal, Date date, String dateFormatString,
			IDateEditor dateEditor) {
		super(jcal,date,dateFormatString,dateEditor);
		
//		datechangelisteners = new HashSet<>();
//		this.addPropertyChangeListener(new PropertyChangeListener() {
//			@Override
//		    public void propertyChange(PropertyChangeEvent e) {
//		    	if("date".equals(e.getPropertyName() ) ) {
//		    		JDateChooser wybieraczDat = (JDateChooser) e.getSource();
//		    		LocalDate newdate = wybieraczDat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
////		    		if( (lastselecteddate == null) || ( !newdate.isEqual( lastselecteddate) ) ) {
//		    			datechangelisteners.forEach(l -> l.dateChanged(newdate) );
//		    			lastselecteddate = newdate;
////		    		}
//		    	}
//		    }
//		});
//		popup.addPopupMenuListener(this);
	}
	//
	private HashSet<OnCalendarDateChangeListener> datechangelisteners;
	public void addJCalendarDateChangeListener (OnCalendarDateChangeListener listener)
	{
		if(datechangelisteners == null) datechangelisteners = new HashSet<>();
		datechangelisteners.add(listener);
	}

//	@Override
//	public void popupMenuCanceled(PopupMenuEvent arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
////		Date changedate = super.lastSelectedDate;
////		stockcal.refreshNews ();
//		
//	}
	
	public StockCalendar getStockCalendar ()
	{
		return this.stockcal;
	}
	
	

}
