package com.exchangeinfomanager.guifactory;

import com.exchangeinfomanager.News.Labels.CreateLabelDialog;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CreateMeetingDialog;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.EventService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelDialog;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.MeetingDialog;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ModifyLabelDialog;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ModifyMeetingDialog;

@SuppressWarnings("all")
public class DialogFactory {

    private DialogFactory() {}

    public static MeetingDialog createMeetingDialog(EventService meeetingService, Cache cache) {
        return new CreateMeetingDialog(meeetingService, cache);
    }

    public static MeetingDialog modifyMeetingDialog(EventService meetingService, Cache cache) {
        return new ModifyMeetingDialog(meetingService, cache);
    }

    public static LabelDialog createLabelDialog(NewsLabelService service){
        return new CreateLabelDialog(service);
    }

    public static LabelDialog modifyLabelDialog(LabelService service){
        return new ModifyLabelDialog(service);
    }
}
