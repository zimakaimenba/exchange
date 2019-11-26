package com.exchangeinfomanager.labelmanagement.Tag;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JLabelFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JPanelFactory;
import com.exchangeinfomanager.labelmanagement.TagService;


public class CombineTagsDialog extends TagDialog<Tag>
{
    public CombineTagsDialog(TagService labelService) {
        super(labelService);
        JLabel createButton = JLabelFactory.createOrangeButton("Combine");

        createButton.addMouseListener(new CombinController());

        JPanel layoutPanel = JPanelFactory.createFixedSizePanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        layoutPanel.add(createButton);
        this.centerPanel.add(layoutPanel);
    }

    private class CombinController extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
                labelService.combinTags(getLabel());
            } catch (SQLException e1) {
                e1.printStackTrace();
            } 
            setVisible(false);
        }
    }
}