package com.exchangeinfomanager.StockCalendar;

import java.sql.SQLException;
import java.util.Collection;



@SuppressWarnings("all")
public interface LabelService {

    Collection<InsertedMeeting.Label> getLabels() throws SQLException ;

    void createLabel(Meeting.Label label) throws SQLException ;

    void deleteLabel(InsertedMeeting.Label label) throws SQLException ;

    void updateLabel(InsertedMeeting.Label label) throws SQLException ;
    
    void setCache (Cache cache);
}
