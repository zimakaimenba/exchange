package com.exchangeinfomanager.Tag;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.exchangeinfomanager.Services.TagService;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;


public class ModifyTagDialog extends TagDialog<Tag>
{
    public ModifyTagDialog(TagService labelService) {
        super(labelService);
        JLabel deleteButton = JLabelFactory.createPinkButton("Delete");
        JLabel updateButton = JLabelFactory.createOrangeButton("Update");

//        deleteButton.addMouseListener(new DeleteController());
        updateButton.addMouseListener(new UpdateController());

        JPanel layoutPanel = JPanelFactory.createFixedSizePanel();
//        layoutPanel.add(deleteButton);
        layoutPanel.add(Box.createHorizontalStrut(5));
        layoutPanel.add(updateButton);
        this.centerPanel.add(layoutPanel);

    }

    private class UpdateController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
                labelService.updateTag(getLabel());
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            setVisible(false);
        }
    }
//    private class DeleteController extends MouseAdapter {
//        @Override
//        public void mouseClicked(MouseEvent e) {
//            super.mouseClicked(e);
//            try {
//                labelService.deleteLabel(getLabel());
//            } catch (SQLException e1) {
//                e1.printStackTrace();
//            } 
//            setVisible(false);
//        }
//    }
}
