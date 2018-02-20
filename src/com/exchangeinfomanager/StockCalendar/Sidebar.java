package com.exchangeinfomanager.StockCalendar;


import com.exchangeinfomanager.StockCalendar.view.Cache;
import com.exchangeinfomanager.StockCalendar.view.CacheListener;
import com.exchangeinfomanager.StockCalendar.view.DialogFactory;
import com.exchangeinfomanager.StockCalendar.view.InsertedMeeting;
import com.exchangeinfomanager.StockCalendar.view.JLabelFactory;
import com.exchangeinfomanager.StockCalendar.view.JPanelFactory;
import com.exchangeinfomanager.StockCalendar.view.JTextFactory;
import com.exchangeinfomanager.StockCalendar.view.LabelDialog;
import com.exchangeinfomanager.StockCalendar.view.LabelService;
import com.exchangeinfomanager.StockCalendar.view.Meeting;
import com.toedter.calendar.JCalendar;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Optional;

@SuppressWarnings("all")
public class Sidebar extends JPanel implements CacheListener {

    private LabelService labelService;
    private JPanel labels;
    private JLabel colorButton;
    private JLabel createLabel;

    private JTextField nameField;
    private LabelDialog modifyLabelDialog;
    private LabelDialog createLabelDialog;
    private Cache cache;
	private JScrollPane labelsscrollpane;

    public Sidebar(LabelService labelService, Cache cache) {
        this.labelService = labelService;
        this.cache = cache;
        this.createLabelDialog = DialogFactory.createLabelDialog(labelService);
        this.modifyLabelDialog = DialogFactory.modifyLabelDialog(labelService);
        this.initUI();
        this.initSidebar();
        cache.addCacheListener(this);
        this.onLabelChange(cache);
    }

    private void initUI() {
        this.labels = JPanelFactory.createPanel();
        this.createLabel = JLabelFactory.createButton("New label");
        
        this.colorButton = JLabelFactory.createLabel("", 40, 30);
        this.nameField = JTextFactory.createTextField();
    }

    private void initSidebar() {
        super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//        super.setPreferredSize(new Dimension(150, Integer.MAX_VALUE));
        super.setPreferredSize(new Dimension(150, 150));
        super.setBackground(ColorScheme.BACKGROUND);

        JPanel labelPanel = JPanelFactory.createFixedSizePanel();
        labelPanel.add(new JLabel("Labels: "));
        super.add(labelPanel);

        super.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 0));
        this.labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        labelsscrollpane = new JScrollPane ();
        labelsscrollpane.setViewportView(this.labels);
        super.add(labelsscrollpane);
        super.add(Box.createVerticalStrut(5));

        JPanel createLabelPanel = JPanelFactory.createFixedSizePanel(new BorderLayout());
        this.createLabel.addMouseListener(new CreateController());
        createLabelPanel.add(this.createLabel, BorderLayout.CENTER);
        super.add(createLabelPanel);
        
        
    }

    @Override
    public void onMeetingChange(Cache cache) {

    }

    @Override
    public void onLabelChange(Cache cache) {
        this.labels.removeAll();
        for (InsertedMeeting.Label label : cache.produceLabels()) {
            JPanel mPanel = JPanelFactory.createFixedSizePanel(new BorderLayout());
            mPanel.setName(String.valueOf(label.getID()));
            mPanel.setPreferredSize(labelsscrollpane.getPreferredSize());

            JLabel name = JLabelFactory.createLabel("  " + label.getName());
            name.setOpaque(true);
            name.addMouseListener(new NameController());
            name.setBackground(label.isActive()? label.getColor(): ColorScheme.BACKGROUND);
            name.setForeground(label.isActive()? ColorScheme.BACKGROUND: ColorScheme.BLACK_FONT);
            name.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, ColorScheme.GREY_LINE));

            JLabel option = JLabelFactory.createButton("*", 20);
            option.addMouseListener(new ModifyController());
            mPanel.add(name, BorderLayout.CENTER);
            mPanel.add(option, BorderLayout.LINE_END);
            labels.add(mPanel);
            labels.add(Box.createVerticalStrut(5));
        }
//        Dimension d = labelsscrollpane.getPreferredSize();
//        this.labels.setPreferredSize(new Dimension(d.width,cache.produceLabels().size()+1));
        super.validate();
        super.repaint();
    }

    private class NameController extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            int id = Integer.valueOf(((Component) e.getSource()).getParent().getName());
            Optional<InsertedMeeting.Label> label = cache.produceLabels().stream().filter(l -> l.getID() == id).findFirst();
            if (label.isPresent()) {
                InsertedMeeting.Label lbl = label.get();
                if(lbl.isActive())
                    lbl.setActive(false);
                else
                    lbl.setActive(true);
                try {
                    labelService.updateLabel(lbl);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } 
            }
        }
    }

    private class CreateController extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            createLabelDialog.setLabel(new Meeting.Label("New label", ColorScheme.ORANGE_LIGHT, true));
            createLabelDialog.setVisible(true);
        }
    }


    private class ModifyController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            int id = Integer.valueOf(((Component) e.getSource()).getParent().getName());
            Optional<InsertedMeeting.Label> label = cache.produceLabels().stream().filter(l -> l.getID() == id).findFirst();
            if (label.isPresent()) {
                modifyLabelDialog.setLabel(label.get());
                modifyLabelDialog.setVisible(true);
            }
        }
    }
}
