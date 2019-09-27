package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;

@SuppressWarnings("all")
public class DialogFactory {

    private DialogFactory() {}

    public static MeetingDialog createMeetingDialog(EventService meeetingService, Cache cache) {
        return new CreateMeetingDialog(meeetingService, cache);
    }

    public static MeetingDialog modifyMeetingDialog(EventService meetingService, Cache cache) {
        return new ModifyMeetingDialog(meetingService, cache);
    }

    public static LabelDialog createLabelDialog(LabelService service){
        return new CreateLabelDialog(service);
    }

    public static LabelDialog modifyLabelDialog(LabelService service){
        return new ModifyLabelDialog(service);
    }
}
