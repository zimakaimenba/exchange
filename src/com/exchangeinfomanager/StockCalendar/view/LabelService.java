package com.exchangeinfomanager.StockCalendar.view;

import java.sql.SQLException;
import java.util.Collection;

import com.exchangeinfomanager.StockCalendar.view.InsertedMeeting.Label;



@SuppressWarnings("all")
public interface LabelService {

    Collection<InsertedMeeting.Label> getLabels() throws SQLException ;

    void createLabel(Meeting.Label label) throws SQLException ;

    void deleteLabel(InsertedMeeting.Label label) throws SQLException ;

    void updateLabel(InsertedMeeting.Label label) throws SQLException ;
    
    void setCache (Cache cache);
}
