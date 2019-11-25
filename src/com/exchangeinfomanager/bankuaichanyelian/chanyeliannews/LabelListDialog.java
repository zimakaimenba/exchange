package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.exchangeinfomanager.StockCalendar.ColorScheme;

public class LabelListDialog<T extends Meeting> extends JDialog 
{
    JPanel centerPanel;
    private T event;
    protected Cache cache;

    public LabelListDialog(Cache cache, T event)
    {
    	this.centerPanel = JPanelFactory.createPanel();
        this.centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        this.centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        super.add(this.centerPanel);
        super.setModalityType(ModalityType.APPLICATION_MODAL);
        super.setTitle("Labels");
    	
		this.cache = cache;
		this.event = event;
	}

	public void display() 
    {
        this.centerPanel.removeAll();
        Collection<InsertedMeeting.Label> labels = cache.produceLabels();
        for (InsertedMeeting.Label label : labels) {
            JPanel mPanel = JPanelFactory.createFixedSizePanel(new BorderLayout());
            mPanel.setName(event.getLabels().contains(label) ? "selected" : "");

            JLabel name = JLabelFactory.createLabel("  " + label.getName());
            name.setName(String.valueOf(label.getID()));
            name.setOpaque(true);
            name.setBackground(event.getLabels().contains(label) ? label.getColor() : ColorScheme.BACKGROUND);
            name.setForeground(
            		event.getLabels().contains(label) ? ColorScheme.BACKGROUND : ColorScheme.BLACK_FONT);
            name.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ColorScheme.GREY_LINE));

            mPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    Component l = ((Component) e.getSource());
                    if (l.getName().equals("selected")) {
                        l.setName("");
                        name.setBackground(ColorScheme.BACKGROUND);
                        name.setForeground(ColorScheme.BLACK_FONT);
                    } else {
                        l.setName("selected");
                        name.setBackground(label.getColor());
                        name.setForeground(ColorScheme.BACKGROUND);
                    }
                }
            });

            mPanel.add(name, BorderLayout.CENTER);

            this.centerPanel.add(mPanel);
            this.centerPanel.add(Box.createVerticalStrut(5));
        }

        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                event.getLabels().clear();

                for (Component c : centerPanel.getComponents()) {
                    if (c instanceof JPanel && c.getName().equals("selected")) {
                        int id = Integer.valueOf(((JPanel) c).getComponent(0).getName());
                        Optional<InsertedMeeting.Label> label = cache.produceLabels()
                                                             .stream()
                                                             .filter(l -> l.getID() == id)
                                                             .findFirst();
                        if (label.isPresent())
                        	event.getLabels().add(label.get());
                    }
                }

            }
        });

        super.setMinimumSize(new Dimension(200, (int) this.centerPanel.getPreferredSize().getHeight() + 40));
        super.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        super.setVisible(true);
    }
}