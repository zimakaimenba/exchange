package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;


import javax.swing.*;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;

@SuppressWarnings("all")
public class CreateMeetingDialog extends MeetingDialog<Meeting> {

//    private Meeting meeting;

    public CreateMeetingDialog(EventService meetingService, Cache cache) {
        super(meetingService, cache);
        super.setTitle("Create");
        
        // lets create button and add controller to it
        JLabel createButton = JLabelFactory.createOrangeButton("Create");
        createButton.addMouseListener(new CreateController());
        
//        JLabel createFromURLButton = JLabelFactory.createOrangeButton("Create From URL");
//        createFromURLButton.addMouseListener(new CreateUrlController());

        // add button to the main panel
        JPanel layoutPanel = JPanelFactory.createFixedSizePanel(new FlowLayout(FlowLayout.RIGHT), 35);
        layoutPanel.add(createButton);
//        layoutPanel.add(createFromURLButton);
        super.centerPanel.add(layoutPanel);
        super.centerPanel.add(Box.createVerticalStrut(PADDING));
    }
    /*
     * (non-Javadoc)
     * @see com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.MeetingDialog#setMeeting(com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting)
     */
    public Boolean setMeeting(Meeting event)
    {
    	super.setMeeting( event);
    	
    	if(event.getMeetingType() == Meeting.QIANSHI || event.getMeetingType() == Meeting.RUOSHI ) {
    		newsownersField.setVisible(true);
    		newsownersField.setEnabled(true);
    		newstitleField.setVisible(false);
    	} else  if(event.getMeetingType() == Meeting.ZHISHUDATE ) {
        	endTimeChooser.setVisible(true);
        }
    	    	
    	return true;
    }
    /*
     * 
     */
    private class CreateController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);

            try {
            	Meeting mt = getMeeting();
            	if(mt.getTitle().length() >150) {
            		JOptionPane.showMessageDialog(null,"新闻标题过长！");
            		setVisible(true);
            	} else { 
            		meetingService.createMeeting(mt);
            		setVisible(false);
            	}
            } catch (com.mysql.jdbc.MysqlDataTruncation e2) {
            	e2.printStackTrace();
        	}catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
    /*
     * 
     */
//    private class CreateUrlController extends MouseAdapter {
//
//        @Override
//        public void mouseClicked(MouseEvent e) 
//        {
//            super.mouseClicked(e);
//
//            GetNewsFromHtml	 showhtmldialog = new GetNewsFromHtml (getMeeting());
//            
//            int exchangeresult = JOptionPane.showConfirmDialog(null, showhtmldialog ,"设置新闻", JOptionPane.OK_CANCEL_OPTION);
//			if(exchangeresult == JOptionPane.CANCEL_OPTION)
//					return;
////        			SystemAudioPlayed.playSound();
//            
//          try {
//        	setMeeting(  showhtmldialog.getMeeting () );
//			meetingService.createMeeting(getMeeting());
//			setVisible(false);
//		  } catch (SQLException e1) {
//				e1.printStackTrace();
//		  }
//	   }
//    }
}
