package com.exchangeinfomanager.StockCalendar.view;

import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;

@SuppressWarnings("all")
public class ModifyMeetingDialog extends MeetingDialog<InsertedMeeting> {

    public ModifyMeetingDialog(MeetingService meetingService, Cache cache) {
        super(meetingService, cache);
        super.setTitle("修改");
        super.startTimeChooser.setEnabled(false);

        // let's create delete and update buttons
        JLabel deleteButton = JLabelFactory.createPinkButton("删除");
        JLabel removeButton = JLabelFactory.createOrangeButton("解除关联");
        JLabel updateButton = JLabelFactory.createOrangeButton("更新");

        // then add controllers (listeners)
        deleteButton.addMouseListener(new DeleteController());
        removeButton.addMouseListener(new RemoveController());
        updateButton.addMouseListener(new UpdateController());

        // and add them to the panel
        JPanel layoutPanel = JPanelFactory.createFixedSizePanel(TITLE_SIZE);
        layoutPanel.add(deleteButton);
        layoutPanel.add(Box.createHorizontalStrut(5));
        layoutPanel.add(removeButton);
        layoutPanel.add(Box.createHorizontalStrut(20));
        layoutPanel.add(updateButton);
        this.centerPanel.add(layoutPanel);
        this.centerPanel.add(Box.createVerticalStrut(PADDING));
    }

    private class DeleteController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
                meetingService.deleteMeeting(getMeeting());
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            setVisible(false);
        }
    }

    private class UpdateController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
                meetingService.updateMeeting(getMeeting());
            } catch (SQLException e1) {
                e1.printStackTrace();
            } 
            setVisible(false);
        }
    }
    private class RemoveController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
            	InsertedMeeting upmeeting = getMeeting();
            	String curowner = upmeeting.getCurrentownercode() ;
            	upmeeting.removeMeetingSpecficOwner (curowner);
            	
                meetingService.updateMeeting(upmeeting);
            } catch (SQLException e1) {
                e1.printStackTrace();
            } 
            setVisible(false);
        }
    }
}
