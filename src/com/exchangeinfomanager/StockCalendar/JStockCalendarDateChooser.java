package com.exchangeinfomanager.StockCalendar;

import com.toedter.calendar.JDayChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.toedter.calendar.IDateEditor;
import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;

public class JStockCalendarDateChooser extends JDateChooser 
{

	
	public JStockCalendarDateChooser (StockCalendar jcal)
	{
        
       this(jcal,null,null,null);
	    
	    
	}
	StockCalendar stockcalendar;
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
//		setName("JDateChooserExtend");
		

//		this.dateEditor = dateEditor;
//		if (this.dateEditor == null) {
//			this.dateEditor = new JTextFieldDateEditor();
//		}
//		this.dateEditor.addPropertyChangeListener("date", this);

		
    	//从这里开始改
    	
//		if (jcal == null) {
//			MeetingService meetingService = new DBMeetingService ( );
//	    	LabelService labelService = new DBLabelService ();
//	       
//	        Cache cache = new Cache(meetingService, labelService);
//
//	        stockcalendar = new StockCalendar(meetingService, cache, labelService);
//
////			jcalendar = new JCalendar(date);
//		} else {
////			jcalendar = jcal;
////			if (date != null) {
////				jcalendar.setDate(date);
////			}
//		}

//		setLayout(new BorderLayout());
//
//		jcalendar.getDayChooser().addPropertyChangeListener("day", this);
//		// always fire"day" property even if the user selects
//		// the already selected day again
//		jcalendar.getDayChooser().setAlwaysFireDayProperty(true);
//
//		setDateFormatString(dateFormatString);
//		setDate(date);
//
//		// Display a calendar button with an icon
//		URL iconURL = getClass().getResource(
//				"/com/toedter/calendar/images/JDateChooserIcon.gif");
//		ImageIcon icon = new ImageIcon(iconURL);
//
//		calendarButton = new JButton(icon) {
//			private static final long serialVersionUID = -1913767779079949668L;
//
//			public boolean isFocusable() {
//				return false;
//			}
//		};
//		calendarButton.setMargin(new Insets(0, 0, 0, 0));
//		calendarButton.addActionListener(this);
//
//		// Alt + 'C' selects the calendar.
//		calendarButton.setMnemonic(KeyEvent.VK_C);
//
//		add(calendarButton, BorderLayout.EAST);
//		add(this.dateEditor.getUiComponent(), BorderLayout.CENTER);
//
//		calendarButton.setMargin(new Insets(0, 0, 0, 0));
//		// calendarButton.addFocusListener(this);

//		popup = new JPopupMenu() {
//			private static final long serialVersionUID = -6078272560337577761L;
//
//			public void setVisible(boolean b) {
//				Boolean isCanceled = (Boolean) getClientProperty("JPopupMenu.firePopupMenuCanceled");
//				if (b
//						|| (!b && dateSelected)
//						|| ((isCanceled != null) && !b && isCanceled
//								.booleanValue())) {
//					super.setVisible(b);
//				}
//			}
//		};

//		popup.setLightWeightPopupEnabled(true);

//		popup.add(jcal);

//		lastSelectedDate = date;

		// Corrects a problem that occurred when the JMonthChooser's combobox is
		// displayed, and a click outside the popup does not close it.

		// The following idea was originally provided by forum user
		// podiatanapraia:
//		changeListener = new ChangeListener() {
//			boolean hasListened = false;
//
//			public void stateChanged(ChangeEvent e) {
//				if (hasListened) {
//					hasListened = false;
//					return;
//				}
//				if (popup.isVisible()
//						&& JDateChooser.this.jcalendar.getMonthChooser()
//								.getComboBox().hasFocus()) {
//					MenuElement[] me = MenuSelectionManager.defaultManager()
//							.getSelectedPath();
//					MenuElement[] newMe = new MenuElement[me.length + 1];
//					newMe[0] = popup;
//					for (int i = 0; i < me.length; i++) {
//						newMe[i + 1] = me[i];
//					}
//					hasListened = true;
//					MenuSelectionManager.defaultManager()
//							.setSelectedPath(newMe);
//				}
//			}
//		};
//		MenuSelectionManager.defaultManager().addChangeListener(changeListener);
		// end of code provided by forum user podiatanapraia

//		isInitialized = true;
	}
	
	/**
	 * Called when the calendar button was pressed.
	 * 
	 * @param e
	 *            the action event
	 */
//	public void actionPerformed(ActionEvent e) {
//		int x = calendarButton.getWidth()
//				- (int) popup.getPreferredSize().getWidth();
//		int y = calendarButton.getY() + calendarButton.getHeight();
//
//		Calendar calendar = Calendar.getInstance();
//		Date date = dateEditor.getDate();
//		if (date != null) {
//			calendar.setTime(date);
//		}
//
//		popup.show(calendarButton, x, y);
//		dateSelected = false;
//	}

	/**
	 * Listens for a "date" property change or a "day" property change event
	 * from the JCalendar. Updates the date editor and closes the popup.
	 * 
	 * @param evt
	 *            the event
	 */
//	public void propertyChange(PropertyChangeEvent evt) {
//		if (evt.getPropertyName().equals("day")) {
//			if (popup.isVisible()) {
//				dateSelected = true;
//				popup.setVisible(false);
//				if (((Integer)evt.getNewValue()).intValue() > 0) {
//					setDate(jcalendar.getCalendar().getTime());
//				} else { 
//					setDate(null);
//				}
//				
//			}
//		} else if (evt.getPropertyName().equals("date")) {
//			if (evt.getSource() == dateEditor) {
//				firePropertyChange("date", evt.getOldValue(), evt.getNewValue());
//				firePropertyChange("stock", evt.getOldValue(), evt.getNewValue());
//			} else {
//				setDate((Date) evt.getNewValue());
//			}
//		}
//	}

	/**
	 * Updates the UI of itself and the popup.
	 */
//	public void updateUI() {
//		super.updateUI();
//		setEnabled(isEnabled());
//
//		if (stockcalendar != null) {
//			SwingUtilities.updateComponentTreeUI(popup);
//		}
//	}



}
