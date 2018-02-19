package com.exchangeinfomanager.StockCalendar.view;

import com.exchangeinfomanager.StockCalendar.Cache;
import com.exchangeinfomanager.StockCalendar.LabelService;
import com.exchangeinfomanager.StockCalendar.MeetingService;

@SuppressWarnings("all")
public class DialogFactory {

    private DialogFactory() {}

    public static MeetingDialog createMeetingDialog(MeetingService meeetingService, Cache cache) {
        return new CreateMeetingDialog(meeetingService, cache);
    }

    public static MeetingDialog modifyMeetingDialog(MeetingService meetingService, Cache cache) {
        return new ModifyMeetingDialog(meetingService, cache);
    }

    public static LabelDialog createLabelDialog(LabelService service){
        return new CreateLabelDialog(service);
    }

    public static LabelDialog modifyLabelDialog(LabelService service){
        return new ModifyLabelDialog(service);
    }
}
